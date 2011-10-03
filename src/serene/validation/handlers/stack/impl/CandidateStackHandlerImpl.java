/*
Copyright 2010 Radu Cernuta 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package serene.validation.handlers.stack.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.xml.sax.SAXException;

import serene.validation.schema.active.Rule;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AGrammar;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AListPattern;
	
import serene.validation.schema.active.ActiveComponentVisitor;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.ContextConflictsDescriptor;
import serene.validation.handlers.conflict.StackConflictsHandler;
import serene.validation.handlers.conflict.ActiveModelConflictHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.handlers.stack.CandidateStackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.util.RuleHandlerReplacer;

import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

public class CandidateStackHandlerImpl extends ContextStackHandler 
								implements CandidateStackHandler, ErrorCatcher{
									
	ContextConflictsDescriptor contextConflictsDescriptor;
	StackConflictsHandler stackConflictsHandler;
	
	ConcurrentStackHandler parent;	
	
	ActiveModelStackHandlerPool pool;
		
	ErrorCatcher errorCatcher;
	
	RuleHandlerReplacer ruleHandlerReplacer;
	
	boolean hasDisqualifyingError;
 	
	/**
	* Determines whether the excessive content errors must be reported or not. Useful
	* when shifting elements whose excessive content errors don't have disqualifying 
	* effects; they are shifted by the parent on all competing candidates, generating 
	* the same errors everywhere, but they need to be reported only once. 
	*/
	private boolean reportExcessive;
	
	/**
	* Determines whether the missing content errors must be reported or not. Useful 
	* when missing content errors don't have disqualifying effects. The messages 
	* are the same everywhere, but they need to be reported only once. 
	*/
	private boolean reportMissing;
	
	/**
	* Determines whether the illegal content errors must be reported or not. Useful 
	* when illegal content errors don't have disqualifying effects. The messages 
	* are the same everywhere, but they need to be reported only once. 
	*/
	private boolean reportIllegal;
	
	/**
	* Determines whether the previous misplaced errors must be reported or not. 
	* Used when previous misplaced errors don't have disqualifying effects. 
	*/
	private boolean reportPreviousMisplaced;
	
	/**
	* Determines whether the current misplaced errors must be reported or not. 
	* Used when previous misplaced errors don't have disqualifying effects. 
	*/
	private boolean reportCurrentMisplaced;	
	/**
	* Determines whether the missing compositor content errors must be reported 
	* or not. Useful when missing content errors don't have disqualifying effects. 
	* The messages are the same everywhere, but they need to be reported only once. 
	*/
	private boolean reportCompositorContentMissing;
		
	Rule[] currentInnerPath;
	InternalConflictResolver currentResolver;
	
	public CandidateStackHandlerImpl(MessageWriter debugWriter){
		super(debugWriter);
		stackConflictsHandler = new StackConflictsHandler(debugWriter);
		ruleHandlerReplacer = new RuleHandlerReplacer(debugWriter);
	}	
	
	
	public void recycle(){
		hasDisqualifyingError = false;
		endingValidation = false;
		parent = null;
		errorCatcher = null;
				
 		if(topHandler != null){
			topHandler.recycle();		
			topHandler = null;
		}
		currentHandler = null;
		
		stackConflictsHandler.clear();
		
		recycler.recycle(this);
	}
	
	// super
	// void init(ValidationItemLocator validationItemLocator, StackHandlerRecycler recycler)
	void init(ValidationItemLocator validationItemLocator, ActiveModelStackHandlerPool pool){
		this.recycler = pool;
		this.pool = pool;
		this.validationItemLocator = validationItemLocator;
	}
	
	// first init
	void init(StructureHandler originalTopHandler, 
				Rule currentRule,
				ConcurrentStackHandler parent,
				ContextConflictsDescriptor contextConflictsDescriptor,
				ErrorCatcher errorCatcher){	
		topHandler = originalTopHandler.getCopy(this, this);
		pathHandler.init(topHandler);		
		setCurrent(currentRule);		
		this.parent = parent;
		this.contextConflictsDescriptor = contextConflictsDescriptor;
		this.errorCatcher = errorCatcher;
	}

	// copy init
	void init(StructureHandler originalTopHandler, 
				Rule currentRule,
				StackConflictsHandler stackConflictsHandler,
				ConcurrentStackHandler parent,
				ContextConflictsDescriptor contextConflictsDescriptor,
				boolean hasDisqualifyingError,
				ErrorCatcher errorCatcher){		
		this.parent = parent;
		this.contextConflictsDescriptor = contextConflictsDescriptor;
		this.errorCatcher = errorCatcher;
		this.stackConflictsHandler.init(stackConflictsHandler);
		topHandler = originalTopHandler.getCopy(this, this);
		ruleHandlerReplacer.replaceHandlers(this.stackConflictsHandler, topHandler);
		pathHandler.init(topHandler);		
		setCurrent(currentRule);		
		this.hasDisqualifyingError = hasDisqualifyingError;
	}	
	
	public CandidateStackHandlerImpl getCopy(){
		return pool.getCandidateStackHandler(topHandler, 
											currentHandler.getRule(), 
											stackConflictsHandler,
											parent,
											contextConflictsDescriptor,
											hasDisqualifyingError,
											errorCatcher);
	}
	
	public boolean hasActiveConflicts(){
		return !stackConflictsHandler.isInactive();
	}
	
	private void setCurrent(Rule currentRule){
		currentHandler = topHandler;
		if(currentRule != null){
            pathHandler.activatePath(currentHandler, currentRule);
            StructureHandler result = pathHandler.getBottomHandler();
            if(result == null){ // null is returned after a ChoiceHandler was reset due to a child that differs from currentChild 
                setCurrent(currentRule);
                return;
            }
            currentHandler = result;
        }
	}
	
	public void shift(AElement element){		
		throw new IllegalStateException();
	}
	
	public void shift(AAttribute attribute){		
		throw new IllegalStateException();
	}
	
	public void shift(CharsActiveTypeItem chars){		
		throw new IllegalStateException();
	}
	
	
	
	public void shift(AElement element, 
					boolean reportExcessive, 
					boolean reportPreviousMisplaced, 
					boolean reportCurrentMisplaced, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){
		setModifyers(reportExcessive, reportPreviousMisplaced, reportCurrentMisplaced, reportMissing, reportIllegal, reportCompositorContentMissing);
		setCurrentHandler(element);
		currentHandler.handleChildShift(element, expectedOrderHandlingCount);
		/*stackConflictsHandler.resolveInactiveConflicts();*/
	}
		
	public void shift(AElement element, 
					Rule[] innerPath, 
					InternalConflictResolver resolver,
					int definitionCandidateIndex, 
					boolean reportExcessive, 
					boolean reportPreviousMisplaced, 
					boolean reportCurrentMisplaced, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){
		this.currentInnerPath = innerPath;
		this.currentResolver = resolver; 
		setModifyers(reportExcessive, reportPreviousMisplaced, reportCurrentMisplaced, reportMissing, reportIllegal, reportCompositorContentMissing);		
		stackConflictsHandler.record(element, resolver, definitionCandidateIndex);		
		setCurrentHandler(element, innerPath, resolver);
		currentHandler.handleChildShift(element, expectedOrderHandlingCount, stackConflictsHandler, resolver);
		/*stackConflictsHandler.resolveInactiveConflicts();*/		
	}
	
	public void shift(AAttribute attribute, 
					boolean reportExcessive, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){		
		setModifyers(reportExcessive, reportMissing, reportIllegal, reportCompositorContentMissing);
		setCurrentHandler(attribute);
		currentHandler.handleChildShift(attribute, expectedOrderHandlingCount);		
		/*stackConflictsHandler.resolveInactiveConflicts();*/
	}
		
	public void shift(AAttribute attribute, 
					Rule[] innerPath, 
					InternalConflictResolver resolver,
					int definitionCandidateIndex, 
					boolean reportExcessive, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){
		this.currentInnerPath = innerPath;
		this.currentResolver = resolver;
		
		setModifyers(reportExcessive, reportMissing, reportIllegal, reportCompositorContentMissing);		
		stackConflictsHandler.record(attribute, resolver, definitionCandidateIndex);
		setCurrentHandler(attribute, innerPath, resolver);
		currentHandler.handleChildShift(attribute, expectedOrderHandlingCount, stackConflictsHandler, resolver);
		/*stackConflictsHandler.resolveInactiveConflicts();*/		
	}
	
	public void shift(CharsActiveTypeItem chars, 
					boolean reportExcessive, 
					boolean reportPreviousMisplaced, 
					boolean reportCurrentMisplaced, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){
		setModifyers(reportExcessive, reportPreviousMisplaced, reportCurrentMisplaced, reportMissing, reportIllegal, reportCompositorContentMissing);
		setCurrentHandler(chars);
		currentHandler.handleChildShift(chars, expectedOrderHandlingCount);
		/*stackConflictsHandler.resolveInactiveConflicts();*/
	}
	
	public void shift(CharsActiveTypeItem chars, 
					Rule[] innerPath, 
					InternalConflictResolver resolver,
					int definitionCandidateIndex, 
					boolean reportExcessive, 
					boolean reportPreviousMisplaced, 
					boolean reportCurrentMisplaced, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){
		this.currentInnerPath = innerPath;
		this.currentResolver = resolver;
		setModifyers(reportExcessive, reportPreviousMisplaced, reportCurrentMisplaced, reportMissing, reportIllegal, reportCompositorContentMissing);
		stackConflictsHandler.record(chars, resolver, definitionCandidateIndex);
		setCurrentHandler(chars, innerPath, resolver);
		currentHandler.handleChildShift(chars, expectedOrderHandlingCount, stackConflictsHandler, resolver);
		/*stackConflictsHandler.resolveInactiveConflicts();*/		
	}
	
		
	public void transferResolversTo(CandidateStackHandler other){
		other.transferResolversFrom(stackConflictsHandler);
	}
	
	public void transferResolversFrom(StackConflictsHandler sch){
		stackConflictsHandler.transferResolversFrom(sch);
	}
	
	
	private void setModifyers(boolean reportExcessive, boolean reportPreviousMisplaced, boolean reportCurrentMisplaced, boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing){
		this.reportExcessive = reportExcessive;		
		this.reportPreviousMisplaced = reportPreviousMisplaced;				
		this.reportCurrentMisplaced = reportCurrentMisplaced;		
		this.reportMissing = reportMissing;
		this.reportIllegal = reportIllegal;
		this.reportCompositorContentMissing = reportCompositorContentMissing;		
	}
	
	private void setModifyers(boolean reportExcessive, boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing){
		this.reportExcessive = reportExcessive;		
		this.reportMissing = reportMissing;		
		this.reportIllegal = reportIllegal;
		this.reportCompositorContentMissing = reportCompositorContentMissing;
		
		reportPreviousMisplaced = false;				
		reportCurrentMisplaced = false;
	}
	
	private void setModifyers(boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing){				
		this.reportMissing = reportMissing;		
		this.reportIllegal = reportIllegal;
		this.reportCompositorContentMissing = reportCompositorContentMissing;
		
		reportExcessive = false;
		reportPreviousMisplaced = false;				
		reportCurrentMisplaced = false;
	}
	
	//override	
	public void reduce(StructureHandler handler){
		if(!stackConflictsHandler.isConflictRule(handler.getRule())){
			super.reduce(handler);
			return;
		}
		// Check that during normal validation only the active branches are allowed to reduce
		// During endValidation it starts from the bottom reducing any existing branch,
		// no check is performed.		
		if(!endingValidation && !isActive(handler, currentHandler))throw new IllegalArgumentException();
		StructureHandler parent = handler.getParentHandler();
		APattern pattern = (APattern)handler.getRule();		
		if(parent.handleChildShift(pattern, handler.getStartQName(), handler.getStartSystemId(), handler.getStartLineNumber(), handler.getStartColumnNumber(), stackConflictsHandler)){
			parent.closeContentStructure(pattern);// must be last so it doesn't remove location data before error messages
			currentHandler = parent;
		}
	}
	
	public void reshift(StructureHandler handler, APattern child){
		//System.out.println("************** 2");
		if(!stackConflictsHandler.isConflictRule(child)){
			super.reshift(handler, child);
			return;
		}
		reduce(handler);
		 
		setCurrentHandler(child, currentInnerPath, currentResolver);
		currentHandler.handleChildShift(child, expectedOrderHandlingCount, stackConflictsHandler, currentResolver);
	}
	
	public void validatingReshift(StructureHandler handler, APattern child){
		//System.out.println("************** 3");
		if(!stackConflictsHandler.isConflictRule(child)){
			super.validatingReshift(handler, child);
			return;
		}
		endSubtreeValidation(handler);
		 
		setCurrentHandler(child, currentInnerPath, currentResolver);
		currentHandler.handleChildShift(child, expectedOrderHandlingCount, stackConflictsHandler, currentResolver);		
	}
	
	// Changes the old currentHandler during path activation. The new currentHandler
	// must be set again by calling the pathHandler to activate in the new conditions. 
	public void reset(StructureHandler handler){
		if(!stackConflictsHandler.isConflictRule(handler.getRule())){
			super.reset(handler);
			return;
		}
		endSubtreeValidation(currentHandler);
		isCurrentHandlerReseted = true;
	}
		
	public void blockReduce(StructureHandler handler, int count, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber){
		//TODO make sure the conflict rule is right
		if(!stackConflictsHandler.isConflictRule(pattern)){
			super.blockReduce(handler, count, pattern, startQName, startSystemId, lineNumber, columnNumber);
			return;
		}
		//System.out.println("************** 5");
		
		// TODO the checks for Exception
		if(!endingValidation && !isActive(handler, currentHandler))throw new IllegalArgumentException();
		StructureHandler parent = handler.getParentHandler();	
		boolean shifted = true;
		for(int i = 0; i < count; i++){
			// TODO
			// Q: Is it possible to get in trouble with some form of recursive call 
			// to setCurrentHandler() through deactivate?
			setCurrentHandler(pattern, currentInnerPath, currentResolver);
			// it should be active at the first cycle of the loop//or it might not be the parent???
			if(!currentHandler.handleChildShift(count, pattern, startQName, startSystemId, lineNumber, columnNumber, stackConflictsHandler)){
				shifted = false;
			}
		}
		if(shifted)parent.closeContentStructure(pattern);// must be last so it doesn't remove location data before error messages
	}
	public void limitReduce(StructureHandler handler, int MIN, int MAX, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber){
		//System.out.println("************** 6");
		//TODO make sure the conflict rule is right
		if(!stackConflictsHandler.isConflictRule(pattern)){
			super.limitReduce(handler, MIN, MAX, pattern, startQName, startSystemId, lineNumber, columnNumber);
			return;
		}
		// TODO the checks for Exception
		if(!endingValidation && !isActive(handler, currentHandler))throw new IllegalArgumentException();
		StructureHandler parent = handler.getParentHandler();
		boolean shifted = true;
		for(int i = 0; i < MIN; i++){
			// It is only shifting up to min since the case of interleave pattern
			// with multiple cardinality containing other compositors with multiple
			// cardinality is not handled. MInterleaveHandler throws 
			// UnsupportedOperationException if called.			
			setCurrentHandler(pattern, currentInnerPath, currentResolver);			
			if(!currentHandler.handleChildShift(MIN, MAX, pattern, startQName, startSystemId, lineNumber, columnNumber, stackConflictsHandler)){
				shifted = false;
			}
		}
		if(shifted)parent.closeContentStructure(pattern);// must be last so it doesn't remove location data before error messages
	}
	// override
	
	
	public void endValidation(boolean reportMissing, 
							boolean reportIllegal, 
							boolean reportCompositorContentMissing){        
		endingValidation = true;	
		setModifyers(reportMissing, reportIllegal, reportCompositorContentMissing);
		topHandler.handleValidatingReduce();//generates eventual missing content errors, possibly disqualifying		
		/*stackConflictsHandler.handleConflicts();*/
	}	
	
	public void endSubtreeValidation(StructureHandler handler){		
		endingValidation = true;		
		handler.handleValidatingReduce();//generates eventual missing content errors, possibly disqualifying			
		endingValidation = false;
	}	
			
	void setCurrentHandler(AElement item, Rule[] innerPath, InternalConflictResolver resolver){		
		// System.out.println(element);
		// System.out.println(currentHandler);
		// System.out.println(topHandler);
		
		Rule parent  = item.getParent();
		
		if(currentHandler != topHandler){
			//Deactivate currentHandler until its rule is ancestor of the item.
			//No need to check for topHandler again since all types can have 
			//only one child.			
			Rule currentRule = currentHandler.getRule();			
			while(!item.isInContext(currentRule)){
				currentHandler.deactivate();				
				if(isCurrentHandlerReseted){
					isCurrentHandlerReseted = false;
				}
				currentRule = currentHandler.getRule();
			}			
		}
		pathHandler.activatePath(currentHandler, parent);
		StructureHandler result = pathHandler.getBottomHandler();
        if(result == null){ // null is returned after a ChoiceHandler was reset due to a child that differs from currentChild 
            setCurrentHandler(item, innerPath, resolver);
            return;
        }
        currentHandler = result;
		currentHandler.setConflict(0, innerPath, stackConflictsHandler, resolver);
		expectedOrderHandlingCount = pathHandler.getExpectedOrderHandlingCount();				
	}

	void setCurrentHandler(AAttribute item, Rule[] innerPath, InternalConflictResolver resolver){		
		// System.out.println(element);
		// System.out.println(currentHandler);
		// System.out.println(topHandler);
		
		Rule parent  = item.getParent();
		
		if(currentHandler != topHandler){
			//Deactivate currentHandler until its rule is ancestor of the item.
			//No need to check for topHandler again since all types can have 
			//only one child.			
			Rule currentRule = currentHandler.getRule();			
			while(!item.isInContext(currentRule)){
				currentHandler.deactivate();				
				if(isCurrentHandlerReseted){
					isCurrentHandlerReseted = false;
				}
				currentRule = currentHandler.getRule();
			}			
		}
		pathHandler.activatePath(currentHandler, parent);
		StructureHandler result = pathHandler.getBottomHandler();
        if(result == null){ // null is returned after a ChoiceHandler was reset due to a child that differs from currentChild 
            setCurrentHandler(item, innerPath, resolver);
            return;
        }
        currentHandler = result;
		currentHandler.setConflict(0, innerPath, stackConflictsHandler, resolver);
		expectedOrderHandlingCount = 0;				
	}	
	
	void setCurrentHandler(CharsActiveTypeItem item, Rule[] innerPath, InternalConflictResolver resolver){		
		// System.out.println(element);
		// System.out.println(currentHandler);
		// System.out.println(topHandler);
		
		Rule parent  = item.getParent();
		
		if(currentHandler != topHandler){
			//Deactivate currentHandler until its rule is ancestor of the item.
			//No need to check for topHandler again since all types can have 
			//only one child.			
			Rule currentRule = currentHandler.getRule();			
			while(!item.isInContext(currentRule)){
				currentHandler.deactivate();				
				if(isCurrentHandlerReseted){
					isCurrentHandlerReseted = false;
				}
				currentRule = currentHandler.getRule();
			}			
		}
		pathHandler.activatePath(currentHandler, parent);
		StructureHandler result = pathHandler.getBottomHandler();
        if(result == null){ // null is returned after a ChoiceHandler was reset due to a child that differs from currentChild 
            setCurrentHandler(item, innerPath, resolver);
            return;
        }
        currentHandler = result;
		currentHandler.setConflict(0, innerPath, stackConflictsHandler, resolver);
		expectedOrderHandlingCount = pathHandler.getExpectedOrderHandlingCount();				
	}
	
		
	// Only called when reducing(reshift, validatingReshift etc.), so there's no 
	// need for in context checking. It is already in context and the 
	// expectedOrderHandlingCount was set. Possible problem: might need new 
	// expectedOrderHandlingCount. In that case some mechanism to know when this 
	// is 0 due to nature of the pattern for which the handler is set is needed.
	void setCurrentHandler(APattern pattern, Rule[] innerPath, InternalConflictResolver resolver){	
		// System.out.println(currentHandler);
		// System.out.println(topHandler);		
		Rule parent  = pattern.getParent();
		pathHandler.activatePath(currentHandler, parent);
		StructureHandler result = pathHandler.getBottomHandler();
        if(result == null){ // null is returned after a ChoiceHandler was reset due to a child that differs from currentChild 
            setCurrentHandler(pattern, innerPath, resolver);
            return;
        }
        currentHandler = result;		
		for(int i = 0; i < innerPath.length; i++){
			if(innerPath[i] == parent){
				currentHandler.setConflict(i, innerPath, stackConflictsHandler, resolver);
				break;
			}
		}		
		//expectedOrderHandlingCount = pathHandler.getExpectedOrderHandlingCount();
	}	
	
	public boolean hasDisqualifyingError(){
		return hasDisqualifyingError;
	}
	
	public void handleConflicts(){
		stackConflictsHandler.handleConflicts();
	}
	//errorCatcher
	//--------------------------------------------------------------------------
	// TODO TODO TODO
	// Thoroughly test all the possible situations and determine exactly which
	// types of errors are really impossible. It might be less than you thought!!
	// Watch out: the character content and attribute value are added with exceptions
	// messages without any thought!!
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
		//errorCatcher.unknownElement( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
		//errorCatcher.unexpectedElement( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
		//errorCatcher.unexpectedAmbiguousElement( qName, definition, systemId, lineNumber, columnNumber);
	}
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
		//errorCatcher.unknownAttribute( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
		//errorCatcher.unexpectedAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
		//errorCatcher.unexpectedAmbiguousAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}
	
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){	
		if(stackConflictsHandler.isConflictRule(definition)){
			stackConflictsHandler.disqualify(definition);
			hasDisqualifyingError = true;
		}else if(reper != null && stackConflictsHandler.isConflictRule(reper)){
			stackConflictsHandler.disqualify(reper);
			hasDisqualifyingError = true;
		}else if(reportPreviousMisplaced){			
			errorCatcher.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
			parent.reportedPreviousMisplaced();
		}
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName, String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		if(stackConflictsHandler.isConflictRule(sourceDefinition)){
			stackConflictsHandler.disqualify(sourceDefinition);
			hasDisqualifyingError = true;
		}else if(reper != null && stackConflictsHandler.isConflictRule(reper)){
			stackConflictsHandler.disqualify(reper);
			hasDisqualifyingError = true;
		}else if(reportPreviousMisplaced){			
			errorCatcher.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
			parent.reportedCurrentMisplaced();
		}
	}
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		if(stackConflictsHandler.isConflictRule(excessiveDefinition)){
			stackConflictsHandler.disqualify(excessiveDefinition);
			hasDisqualifyingError = true;
		}else if(reportExcessive){
			errorCatcher.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
			parent.reportedExcessive();
		}
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		if(stackConflictsHandler.isConflictRule(excessiveDefinition)){
			//throw new IllegalStateException(); it could be the last one, processed even disqualified
		}else if(reportExcessive){	
			errorCatcher.excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
			parent.reportedExcessive();
		}		
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		if(contextConflictsDescriptor.isConflictRule(missingDefinition) || contextConflictsDescriptor.isConflictRule(context)){
			stackConflictsHandler.disqualify(context);//disqualify all since you don't know which is the real cause
			hasDisqualifyingError = true;
		}else if(reportMissing){
			errorCatcher.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
			parent.reportedMissing();
		}	
	}
	
	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		if(contextConflictsDescriptor.isConflictRule(context)){
			stackConflictsHandler.disqualify(context);//disqualify all since you don't know which is the real cause
			hasDisqualifyingError = true;
		}else if(reportIllegal){
			errorCatcher.illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
			parent.reportedIllegal();
		}		
	}
	
	public void unresolvedAmbiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.unresolvedAmbiguousElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void unresolvedUnresolvedElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.unresolvedUnresolvedElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void unresolvedAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.unresolvedAttributeContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	
	public void ambiguousUnresolvedElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousUnresolvedElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousAmbiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousAmbiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousCharacterContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousCharacterContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousAttributeValueWarning(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousAttributeValueWarning(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	
	public void undeterminedByContent(String qName, String candidateMessages){
		//errorCatcher.undeterminedByContent(qName, candidateMessages);
		throw new IllegalStateException();
	}
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}
	
    public void unresolvedListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
    }
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		if(contextConflictsDescriptor.isConflictRule(definition)){
			stackConflictsHandler.disqualify(context);//disqualify all since you don't know which is the real cause
			hasDisqualifyingError = true;
		}else if(reportCompositorContentMissing){
			errorCatcher.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
			parent.reportedCompositorContentMissing();
		}
	}
	
	public void internalConflict(ConflictMessageReporter conflictMessageReporter) throws SAXException{
	    errorCatcher.internalConflict(conflictMessageReporter);
	}
	//--------------------------------------------------------------------------	
	
	public String toString(){
		//if(currentHandler != null) return "CandidateContextStackHandlerImpl "+hashCode()+" stack: "+currentHandler.stackToString();
		if(currentHandler != null) return "CandidateContextStackHandlerImpl stack: "+currentHandler.stackToString();
		//return "CandidateContextStackHandlerImpl "+hashCode()+" stack: null";
		return "CandidateContextStackHandlerImpl stack: null";
	}
}