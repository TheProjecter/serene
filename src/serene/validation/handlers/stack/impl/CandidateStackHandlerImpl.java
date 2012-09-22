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
	
import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.ContextConflictsDescriptor;
import serene.validation.handlers.conflict.StackConflictsHandler;
import serene.validation.handlers.conflict.ValidatorConflictHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.InnerPatternHandler;

import serene.validation.handlers.stack.CandidateStackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.util.RuleHandlerReplacer;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.CharsMatchPath;
import serene.validation.handlers.match.MatchPath;

public class CandidateStackHandlerImpl extends ContextStackHandler 
								implements CandidateStackHandler, ErrorCatcher{
									
	ContextConflictsDescriptor contextConflictsDescriptor;
	StackConflictsHandler stackConflictsHandler;
	
	ConcurrentStackHandler parent;	
			
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
		
	SRule[] currentInnerPath;
	InternalConflictResolver currentResolver;
	
	public CandidateStackHandlerImpl(){
		super();
		stackConflictsHandler = new StackConflictsHandler();
		ruleHandlerReplacer = new RuleHandlerReplacer();
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
		// TODO recycle;
		currentPath = null;
		
		stackConflictsHandler.clear();
	
		pool.recycle(this);
	}
	
	// super
	// void init(InputStackDescriptor inputStackDescriptor, ValidatorStackHandlerPool pool)
	void init(InputStackDescriptor inputStackDescriptor, ValidatorStackHandlerPool pool){
		this.pool = pool;
		this.inputStackDescriptor = inputStackDescriptor;
	}
	
	// first init
	void init(StructureHandler originalTopHandler, 
	            MatchPath currentPath,
				SRule currentRule,
				ConcurrentStackHandler parent,
				ContextConflictsDescriptor contextConflictsDescriptor,
				ErrorCatcher errorCatcher){	
	    /*reportExcessive = false;		
		reportPreviousMisplaced = false;				
		reportCurrentMisplaced = false;		
		reportMissing = false;
		reportIllegal = false;
		reportCompositorContentMissing = false;*/
	
		topHandler = originalTopHandler.getCopy(this, this);
		this.currentPath = currentPath;
		/*pathHandler.init(topHandler);	*/
		setCurrent(currentRule);			
		this.parent = parent;
		this.contextConflictsDescriptor = contextConflictsDescriptor;
		this.errorCatcher = errorCatcher;
	}

	// copy init
	void init(StructureHandler originalTopHandler,  
	            MatchPath currentPath,
				SRule currentRule,
				StackConflictsHandler stackConflictsHandler,
				ConcurrentStackHandler parent,
				ContextConflictsDescriptor contextConflictsDescriptor,
				boolean hasDisqualifyingError,
				ErrorCatcher errorCatcher){
	    /*reportExcessive = false;		
		reportPreviousMisplaced = false;				
		reportCurrentMisplaced = false;		
		reportMissing = false;
		reportIllegal = false;
		reportCompositorContentMissing = false;*/
	
		
		this.parent = parent;
		this.contextConflictsDescriptor = contextConflictsDescriptor;
		this.errorCatcher = errorCatcher;
		this.stackConflictsHandler.init(stackConflictsHandler);
		topHandler = originalTopHandler.getCopy(this, this);
		ruleHandlerReplacer.replaceHandlers(this.stackConflictsHandler, topHandler);
		/*pathHandler.init(topHandler);		*/
		this.currentPath = currentPath;
		setCurrent(currentRule);		
		this.hasDisqualifyingError = hasDisqualifyingError;
	}	
	
	public CandidateStackHandlerImpl getCopy(){
		return pool.getCandidateStackHandler(topHandler,
                                            currentPath,		    
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
	
	private void setCurrent(SRule currentRule){
		/*currentHandler = topHandler;
		if(currentRule != null){
            pathHandler.activatePath(currentHandler, currentRule);
            StructureHandler result = pathHandler.getBottomHandler();
            if(result == null){ // null is returned after a ChoiceHandler was reset due to a child that differs from currentChild 
                setCurrent(currentRule);
                return;
            }
            currentHandler = result;
        }*/    
        
        
        
        if(currentPath == null){
            currentHandler = topHandler;
            return;
        }
                
        int i = currentPath.size()-1;
        if(currentPath.get(i) == currentRule){
            currentHandler = topHandler;
            return;	        
        }
        if(currentRule != null){
            StructureHandler s = topHandler;
            for(i--; i > 0; i--){
                SRule r = currentPath.get(i);
                if(r == currentRule){
                    currentHandler = s;
                    return;	        
                }
                s = s.getChildHandler(r, currentPath);
                if(s == null){// null is returned after a ChoiceHandler was reset due to a child that differs from currentChild
                    setCurrent(currentRule);                	            
                    return;
                }
            }
            throw new IllegalStateException();
        }
	}
	
	public void shift(ElementMatchPath element){		
		throw new IllegalStateException();
	}
	
	public void shift(AttributeMatchPath attribute){		
		throw new IllegalStateException();
	}
	
	public void shift(CharsMatchPath chars){		
		throw new IllegalStateException();
	}
	
	
	
	public void shift(ElementMatchPath element, 
					boolean reportExcessive, 
					boolean reportPreviousMisplaced, 
					boolean reportCurrentMisplaced, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){
		setModifyers(reportExcessive, reportPreviousMisplaced, reportCurrentMisplaced, reportMissing, reportIllegal, reportCompositorContentMissing);
		setCurrentHandler(element);
		currentHandler.handleChildShiftAndOrder(element.getElement(), expectedOrderHandlingCount);
		/*stackConflictsHandler.resolveInactiveConflicts();*/
	}
		
	public void shift(ElementMatchPath element, 
					SRule[] innerPath, 
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
		stackConflictsHandler.record(element.getElement(), resolver, definitionCandidateIndex);		
		setCurrentHandler(element, innerPath, resolver);
		currentHandler.handleChildShift(element.getElement(), expectedOrderHandlingCount, stackConflictsHandler, resolver);		
		/*stackConflictsHandler.resolveInactiveConflicts();*/
	}
	
	public void shift(AttributeMatchPath attribute, 
					boolean reportExcessive, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){		
		setModifyers(reportExcessive, reportMissing, reportIllegal, reportCompositorContentMissing);
		setCurrentHandler(attribute);
		currentHandler.handleChildShiftAndOrder(attribute.getAttribute(), expectedOrderHandlingCount);		
		/*stackConflictsHandler.resolveInactiveConflicts();*/
	}
		
	public void shift(AttributeMatchPath attribute, 
					SRule[] innerPath, 
					InternalConflictResolver resolver,
					int definitionCandidateIndex, 
					boolean reportExcessive, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){
		this.currentInnerPath = innerPath;
		this.currentResolver = resolver;
		
		setModifyers(reportExcessive, reportMissing, reportIllegal, reportCompositorContentMissing);		
		stackConflictsHandler.record(attribute.getAttribute(), resolver, definitionCandidateIndex);
		setCurrentHandler(attribute, innerPath, resolver);
		currentHandler.handleChildShift(attribute.getAttribute(), expectedOrderHandlingCount, stackConflictsHandler, resolver);
		/*stackConflictsHandler.resolveInactiveConflicts();*/
	}
	
	public void shift(CharsMatchPath chars, 
					boolean reportExcessive, 
					boolean reportPreviousMisplaced, 
					boolean reportCurrentMisplaced, 
					boolean reportMissing, 
					boolean reportIllegal, 
					boolean reportCompositorContentMissing){
		setModifyers(reportExcessive, reportPreviousMisplaced, reportCurrentMisplaced, reportMissing, reportIllegal, reportCompositorContentMissing);
		setCurrentHandler(chars);
		currentHandler.handleChildShiftAndOrder(chars.getChars(), expectedOrderHandlingCount);
		/*stackConflictsHandler.resolveInactiveConflicts();*/
	}
	
	public void shift(CharsMatchPath chars, 
					SRule[] innerPath, 
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
		stackConflictsHandler.record(chars.getChars(), resolver, definitionCandidateIndex);
		setCurrentHandler(chars, innerPath, resolver);
		currentHandler.handleChildShift(chars.getChars(), expectedOrderHandlingCount, stackConflictsHandler, resolver);
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
	public void reduce(InnerPatternHandler handler){
		if(!stackConflictsHandler.isConflictRule(handler.getRule())){
			super.reduce(handler);
			return;
		}
		// Check that during normal validation only the active branches are allowed to reduce
		// During endValidation it starts from the bottom reducing any existing branch,
		// no check is performed.		
		if(!endingValidation && !isActive(handler, currentHandler))throw new IllegalArgumentException();
		StructureHandler parent = handler.getParentHandler();
		SPattern pattern = handler.getRule();		
		if(parent.handleChildShift(pattern, handler.getStartInputRecordIndex(), stackConflictsHandler)){
			parent.closeContentStructure(pattern);// must be last so it doesn't remove location data before error messages
			currentHandler = parent;
		}
	}
	
	public void reshift(InnerPatternHandler handler, SPattern child){
		//System.out.println("************** 2");
		if(!stackConflictsHandler.isConflictRule(child)){
			super.reshift(handler, child);
			return;
		}
		reduce(handler);
		 
		setCurrentHandler(child, currentInnerPath, currentResolver);
		currentHandler.handleChildShift(child, expectedOrderHandlingCount, stackConflictsHandler, currentResolver);
	}
	
	public void validatingReshift(InnerPatternHandler handler, SPattern child){
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
		
	public void blockReduce(StructureHandler handler, int count, SPattern pattern, int startInputRecordIndex){
		//TODO make sure the conflict rule is right
		if(!stackConflictsHandler.isConflictRule(pattern)){
			super.blockReduce(handler, count, pattern, startInputRecordIndex);
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
			if(!currentHandler.handleChildShift(count, pattern, startInputRecordIndex, stackConflictsHandler)){
				shifted = false;
			}
		}
		if(shifted)parent.closeContentStructure(pattern);// must be last so it doesn't remove location data before error messages
	}
	public void limitReduce(StructureHandler handler, int MIN, int MAX, SPattern pattern, int startInputRecordIndex){
		//System.out.println("************** 6");
		//TODO make sure the conflict rule is right
		if(!stackConflictsHandler.isConflictRule(pattern)){
			super.limitReduce(handler, MIN, MAX, pattern, startInputRecordIndex);
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
			if(!currentHandler.handleChildShift(MIN, MAX, pattern, startInputRecordIndex, stackConflictsHandler)){
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
			
	void setCurrentHandler(ElementMatchPath elementPath, SRule[] innerPath, InternalConflictResolver resolver){		
		// System.out.println(element);
		// System.out.println(currentHandler);
		// System.out.println(topHandler);
		
		/*Rule parent  = item.getParent();
		
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
		expectedOrderHandlingCount = pathHandler.getExpectedOrderHandlingCount();	*/
		
		int currentRuleIndexInPath = -1;
		while(currentRuleIndexInPath < 0){
            currentRuleIndexInPath = getCurrentRuleIndexInPath(elementPath);
        }
        activatePath(/*currentRuleIndexInPath,*/ elementPath);
        currentHandler.setConflict(0, innerPath, stackConflictsHandler, resolver);
	}

	void setCurrentHandler(AttributeMatchPath attributePath, SRule[] innerPath, InternalConflictResolver resolver){		
		// System.out.println(element);
		// System.out.println(currentHandler);
		// System.out.println(topHandler);
		
		/*Rule parent  = item.getParent();
		
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
		expectedOrderHandlingCount = 0;		*/

        int currentRuleIndexInPath = -1;
		while(currentRuleIndexInPath < 0){
            currentRuleIndexInPath = getCurrentRuleIndexInPath(attributePath);
        }
        activatePath(/*currentRuleIndexInPath,*/ attributePath);	
        currentHandler.setConflict(0, innerPath, stackConflictsHandler, resolver);
	}	
	
	void setCurrentHandler(CharsMatchPath charsPath, SRule[] innerPath, InternalConflictResolver resolver){		
		// System.out.println(element);
		// System.out.println(currentHandler);
		// System.out.println(topHandler);
		
		/*Rule parent  = item.getParent();
		
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
		expectedOrderHandlingCount = pathHandler.getExpectedOrderHandlingCount();*/	
		
		int currentRuleIndexInPath = -1;
		while(currentRuleIndexInPath < 0){
            currentRuleIndexInPath = getCurrentRuleIndexInPath(charsPath);
        }
        activatePath(/*currentRuleIndexInPath,*/ charsPath);
        
        currentHandler.setConflict(0, innerPath, stackConflictsHandler, resolver);
	}
	
		
	// Only called when reducing(reshift, validatingReshift etc.), so there's no 
	// need for in context checking. It is already in context and the 
	// expectedOrderHandlingCount was set. Possible problem: might need new 
	// expectedOrderHandlingCount. In that case some mechanism to know when this 
	// is 0 due to nature of the pattern for which the handler is set is needed.
	void setCurrentHandler(SPattern pattern, SRule[] innerPath, InternalConflictResolver resolver){	
		// System.out.println(currentHandler);
		// System.out.println(topHandler);		
		/*Rule parent  = pattern.getParent();
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
		//expectedOrderHandlingCount = pathHandler.getExpectedOrderHandlingCount();*/
		
		
		StructureHandler s = topHandler;
	    for(int i = currentPath.size()-2; i >= 0; i--){	        
	        SRule r = currentPath.get(i);
	        if(r == pattern){
	            currentHandler = s;
	            for(int j = 0; j < innerPath.length; j++){
                    if(innerPath[j] == pattern){
                        currentHandler.setConflict(j+1, innerPath, stackConflictsHandler, resolver);
                        return;	  
                    }
                }
                currentHandler.setConflict(0, innerPath, stackConflictsHandler, resolver);// it means the pattern is the conflict pattern and the parent index in the innerPath is 0
	            return;	        
	        }
	        s = s.getChildHandler(r, currentPath);
	        if(s == null){// null is returned after a ChoiceHandler was reset due to a child that differs from currentChild
	            setCurrentHandler(pattern, innerPath, resolver);                	            
	            return;
	        }	        
	    }
	    throw new IllegalStateException();
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
	public void unknownElement(int inputRecordIndex){
		throw new IllegalStateException();
		//errorCatcher.unknownElement( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
		throw new IllegalStateException();
		//errorCatcher.unexpectedElement( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] definition, int inputRecordIndex){
		throw new IllegalStateException();
		//errorCatcher.unexpectedAmbiguousElement( qName, definition, systemId, lineNumber, columnNumber);
	}
	
	public void unknownAttribute(int inputRecordIndex){
		throw new IllegalStateException();
		//errorCatcher.unknownAttribute( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
		throw new IllegalStateException();
		//errorCatcher.unexpectedAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
		throw new IllegalStateException();
		//errorCatcher.unexpectedAmbiguousAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}
	
		
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
		if(stackConflictsHandler.isConflictRule(definition)){
			stackConflictsHandler.disqualify(definition);
			hasDisqualifyingError = true;
		}else if(reper != null && stackConflictsHandler.isConflictRule(reper)){
			stackConflictsHandler.disqualify(reper);
			hasDisqualifyingError = true;
		}else if(reportPreviousMisplaced){			
			errorCatcher.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
			parent.reportedPreviousMisplaced();
		}
	}
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
		if(stackConflictsHandler.isConflictRule(sourceDefinition)){
			stackConflictsHandler.disqualify(sourceDefinition);
			hasDisqualifyingError = true;
		}else if(reper != null && stackConflictsHandler.isConflictRule(reper)){
			stackConflictsHandler.disqualify(reper);
			hasDisqualifyingError = true;
		}else if(reportPreviousMisplaced){			
			errorCatcher.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
			parent.reportedCurrentMisplaced();
		}
	}
	
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
		if(stackConflictsHandler.isConflictRule(excessiveDefinition)){
			stackConflictsHandler.disqualify(excessiveDefinition);
			hasDisqualifyingError = true;
		}else if(reportExcessive){
			errorCatcher.excessiveContent(context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
			parent.reportedExcessive();
		}
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
		if(stackConflictsHandler.isConflictRule(excessiveDefinition)){
			//throw new IllegalStateException(); it could be the last one, processed even disqualified
		}else if(reportExcessive){	
			errorCatcher.excessiveContent(context, excessiveDefinition, inputRecordIndex);
			parent.reportedExcessive();
		}		
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
		if(contextConflictsDescriptor.isConflictRule(definition) || contextConflictsDescriptor.isConflictRule(context)){
			stackConflictsHandler.disqualify(context);//disqualify all since you don't know which is the real cause
			hasDisqualifyingError = true;
		}else if(reportMissing){
			errorCatcher.missingContent(context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
			parent.reportedMissing();
		}	
	}
	
	public void illegalContent(SRule context, int startInputRecordIndex){
		if(contextConflictsDescriptor.isConflictRule(context)){
			stackConflictsHandler.disqualify(context);//disqualify all since you don't know which is the real cause
			hasDisqualifyingError = true;
		}else if(reportIllegal){
			errorCatcher.illegalContent(context, startInputRecordIndex);
			parent.reportedIllegal();
		}		
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){	    
		// TODO are you sure it is possible?
		errorCatcher.unresolvedAmbiguousElementContentError(inputRecordIndex, possibleDefinitions);
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.unresolvedUnresolvedElementContentError(inputRecordIndex, possibleDefinitions);
	}
	
	public void unresolvedAttributeContentError(int inputRecordIndex, SAttribute[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.unresolvedAttributeContentError(inputRecordIndex, possibleDefinitions);
	}
	
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousUnresolvedElementContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousAmbiguousElementContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeContentWarning(int inputRecordIndex, SAttribute[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousAttributeContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
	}

	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		// TODO are you sure it is possible?
		errorCatcher.ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
	}

	
	public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
		throw new IllegalStateException();
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
		throw new IllegalStateException();
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
		throw new IllegalStateException();
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
		throw new IllegalStateException();
	}
	
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
        throw new IllegalStateException();
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        throw new IllegalStateException();
    }
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
		if(contextConflictsDescriptor.isConflictRule(definition)){
			stackConflictsHandler.disqualify(context);//disqualify all since you don't know which is the real cause
			hasDisqualifyingError = true;
		}else if(reportCompositorContentMissing){
			errorCatcher.missingCompositorContent(context, startInputRecordIndex, definition, expected, found);
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