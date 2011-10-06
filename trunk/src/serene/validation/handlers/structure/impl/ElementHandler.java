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

package serene.validation.handlers.structure.impl;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import sereneWrite.MessageWriter;

public class ElementHandler extends StructureValidationHandler{	
	AElement rule;
	
	ParticleHandler childParticleHandler;
	StructureHandler childStructureHandler;	
	
	ElementHandler original;
	ElementHandler(MessageWriter debugWriter){
		super(debugWriter);
		noContent = new NoContent();
		openContent = new OpenContent();
		satisfiedContent = new SatisfiedContent();
		saturatedContent = new SaturatedContent();
		excessiveContent = new ExcessiveContent();
		contentHandler = noContent;
	}	
	
	void init(AElement element, ErrorCatcher errorCatcher, StackHandler stackHandler){
		this.rule = element;
		this.stackHandler = stackHandler;
		this.errorCatcher = errorCatcher;	
        setStart();
	}
		
	public void recycle(){
		original = null;
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
		contentHandler = noContent;		
		starttQName = null;
		starttSystemId = null;
		
		recycler.recycle(this);;
	}
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	public StructureValidationHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		return null;
	}	
	public void deactivate(){
		throw new IllegalStateException();
	}
    public boolean handleDeactivation(){
		return false;	
	}
	public StructureHandler getChildHandler(Rule child){
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler == null) childStructureHandler = child.getStructureHandler(errorCatcher, this, stackHandler);
		return childStructureHandler;
	}		
	public AElement getRule(){
		return rule;
	}
	
		
	public boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount){
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), validationItemLocator.getItemIdentifier(), validationItemLocator.getItemId(), pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(APattern pattern, int itemId,  String startQName, String startSystemId, int lineNumber, int columnNumber){
		handleParticleShift(startSystemId, lineNumber, columnNumber, startQName, itemId, pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int count, APattern pattern, int itemId, String startQName, String startSystemId, int lineNumber, int columnNumber){
		handleParticleShift(startSystemId, lineNumber, columnNumber, startQName, itemId, pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int itemId, String startQName, String startSystemId, int lineNumber, int columnNumber){
		handleParticleShift(startSystemId, lineNumber, columnNumber, startQName, itemId, pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	
	// TODO handleParticleShift()		
	public boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern, resolver)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(pattern, stackConflictsHandler, resolver);		
		//handleStateSaturationReduce();
		return true;
	}
	
	// reduce
	public boolean handleChildShift(APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber, StackConflictsHandler stackConflictsHandler){
		handleParticleShift(pattern, stackConflictsHandler);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int count, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber, StackConflictsHandler stackConflictsHandler){
		handleParticleShift(pattern, stackConflictsHandler);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber, StackConflictsHandler stackConflictsHandler){
		handleParticleShift(pattern, stackConflictsHandler);		
		//handleStateSaturationReduce();
		return true;
	}
	
	
	
	
	
	// TODO 
	// Review this, the functionality should be moved in a method that doesn't
	// have "reduce" in the name since this handler doesn't really get reduced.
	public void handleValidatingReduce(){		
		// if(starttSystemId == null)setStart();
		// it means either there was no content of any kind to trigger the setStart()
		// or for some reason there is no location data, in any case it can't be wrong
        // LATER: 
        // It can also mean that there was some unexpected content that was 
        // handled at the level of the ValidationEventHandler. 
        // This means that it would be handy for ElementHandler to have some way 
        // of setting the start when they are created.
		 
		// - reduce first any childStructureHandler present 
		//		-> updates the childParticleHandler		 
		// - afterwards 
		// - if the childParticleHandler is present		
		//		-> if contentHandler is not satisfied report missing content by childParticleHandler
		// - else if child was required report missing content here 
		if(childStructureHandler != null){
			childStructureHandler.handleValidatingReduce();
		}
		if(childParticleHandler != null){
			if(contentHandler.isSatisfied()){
				childParticleHandler.reportMissing(rule, starttSystemId, starttLineNumber, starttColumnNumber);
			}
		}else{
			if(rule.isChildRequired()){
				//System.out.println("element handler "+rule);				
				APattern child = rule.getChild();				
				int minOccurs = child.getMinOccurs();
				errorCatcher.missingContent(rule, starttSystemId, starttLineNumber, starttColumnNumber, child, minOccurs, 0, null, null, null, null);
			}
		}
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return true;
		else throw new IllegalStateException();
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition, InternalConflictResolver resolver){
		if(expectedOrderHandlingCount > 0) return true;
		else throw new IllegalStateException();
	}
	public boolean acceptsMDescendentReduce(APattern p){
		// TODO review !!!
		// TODO should you be more defensive and check if the p is the actual 
		// pattern from the particle handler?
		// First thought: here is not really the case, there can be only one child.
		
		// if there is no corresponding ParticleHandler, get one
		// if the ParticleHandler doesn't expect the last occurrence
		//				return true
		// else return false // this cannot be reduced until the content is finished
		
		// before every return recycle the ParticleHandler if it was aquired 
		// only for this method
		
		boolean transitoryParticle = false;
		if(childParticleHandler == null) {
			childParticleHandler = p.getParticleHandler(this, errorCatcher);
			transitoryParticle = true;
		}
		if(!childParticleHandler.expectsLastOccurrence()){
			if(transitoryParticle){
				childParticleHandler.recycle();
				childParticleHandler = null;
			}
			return true;
		}else{			
			if(transitoryParticle){
				childParticleHandler.recycle();
				childParticleHandler = null;
			}
			return false;
		}
	}	
	public int functionalEquivalenceCode(){
		int fec = rule.hashCode();
		if(childParticleHandler != null)fec += childParticleHandler.functionalEquivalenceCode();		
		if(childStructureHandler != null)fec += childStructureHandler.functionalEquivalenceCode();
		return fec;
	}	
	
	public void setConflict(int index, Rule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		throw new IllegalStateException();
	}
	
	public ElementHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		ElementHandler copy = rule.getStructureHandler(errorCatcher, stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandler, 
						childStructureHandler, 
						contentHandler.getContentIndex(),
						starttSystemId,
						starttLineNumber,
						starttColumnNumber,
						starttQName);
		copy.setOriginal(this);
		return copy;
	}
	public ElementHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		// ElementHandler copy = rule.getStructureHandler(errorCatcher, stackHandler);
		// copy.setState(stackHandler, errorCatcher, childParticleHandler, childStructureHandler, contentHandler.getContentIndex());
		// return copy;
		throw new IllegalStateException();
	}
	private void setOriginal(ElementHandler original){
		this.original = original;
	}
	public ElementHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	public void closeContentStructure(APattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
	}
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
	
	
	//Start ValidationHandler---------------------------------------------------------		
	void handleParticleShift(String systemId, int lineNumber, int columnNumber, String qName, int itemId, APattern childPattern){
		setChildParticleHandler(childPattern);
		childParticleHandler.handleOccurrence(itemId, qName, systemId, lineNumber, columnNumber);
	}	
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		setChildParticleHandler(childPattern);
		childParticleHandler.handleOccurrence(stackConflictsHandler, resolver);
	}
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler){
		setChildParticleHandler(childPattern);
		childParticleHandler.handleOccurrence(stackConflictsHandler);
	}    
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------

	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler childParticleHandler, 
							StructureHandler childStructureHandler,
							int contentHandlerContentIndex,
							String startSystemId,
							int startLineNumber,
							int startColumnNumber,
							String startQName){
		if(childParticleHandler != null)this.childParticleHandler = childParticleHandler.getCopy(this, errorCatcher);
		if(childStructureHandler != null)this.childStructureHandler = childStructureHandler.getCopy(this, stackHandler, errorCatcher);
		if(contentHandlerContentIndex == NO_CONTENT){
			contentHandler = noContent;
		}else if(contentHandlerContentIndex == OPEN_CONTENT){
			contentHandler = openContent;
		}else if(contentHandlerContentIndex == SATISFIED_CONTENT){
			contentHandler = satisfiedContent;
		}else if(contentHandlerContentIndex == SATURATED_CONTENT){
			contentHandler = saturatedContent;
		}else if(contentHandlerContentIndex == EXCESSIVE_CONTENT){
			contentHandler = excessiveContent;
		}else{
			throw new IllegalArgumentException();
		}		
		this.starttSystemId = startSystemId;
		this.starttLineNumber = startLineNumber;
		this.starttColumnNumber = startColumnNumber;
		this.starttQName = startQName;
	}
	
	
	//--------------------------------------------------------------------------------------------
	//Start UniqueChildHandler ----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------	
	public void setChildParticleHandler(APattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();		
		if(childParticleHandler == null){
			childParticleHandler = childPattern.getParticleHandler(this, errorCatcher);
			//setStart();
		}		
	}
	//--------------------------------------------------------------------------------------------
	//End UniqueChildHandler ------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------
	
	public StructureHandler getStructureHandler(){
		return childStructureHandler;
	}
	
	public ParticleHandler getParticleHandler(){
		return childParticleHandler;
	}
	
	class NoContent extends AbstractNoContent{
		public void childOpen(){		
			setStart();
			contentHandler = openContent;
		}
		public void requiredChildSatisfied(){
			setStart();
			contentHandler = satisfiedContent;
		}
		public void optionalChildSatisfied(){
			setStart();
			contentHandler = satisfiedContent;
		}
		public void childSaturated(){
			setStart();
			contentHandler = saturatedContent;
		}				
	}
	
	class OpenContent extends AbstractOpenContent{
		public void childOpen(){
			throw new IllegalStateException();
		}
		public void requiredChildSatisfied(){						
			contentHandler = satisfiedContent;
		}
		public void optionalChildSatisfied(){
			contentHandler = satisfiedContent;
		}
		public void childSaturated(){
			contentHandler = saturatedContent;
		}
		public void childExcessive(){					
			throw new IllegalStateException();
		}		
	}
	class SatisfiedContent extends AbstractSatisfiedContent{
		public boolean isSatisfied(){
			if(childStructureHandler != null && !childStructureHandler.isSatisfied()){
				return false;
			}			
			return true;
		}
		public void childOpen(){
		}
		public void requiredChildSatisfied(){
			// TODO review
			throw new IllegalStateException();
		}
		public void optionalChildSatisfied(){
			// TODO review
			throw new IllegalStateException();
		}
		/*public void childSatisfiedPlus(){
			return ACCEPT;
		}*/
		public void childSaturated(){
			contentHandler = saturatedContent;
		}
		public void childExcessive(){
			throw new IllegalStateException();
			// childParticleHandler.reportExcessive(element, startSystemId, startLineNumber, startColumnNumber);			
			// contentHandler = excessiveContent;
		}	
	}
	class SaturatedContent extends AbstractSaturatedContent{
		public boolean isSatisfied(){
			if(childStructureHandler != null && !childStructureHandler.isSatisfied()){
				return false;
			}			
			return true;
		}
		public void childOpen(){
			//throw new IllegalStateException();
			// It is possible to have this in cases when the child of element
			// is handled by a UniqueChildPatternHandler (choice, ref , etc)
			// and after that was reduced excessive content is started. It would
			// be the initial childOpen event fired from the NoContent state of 
			// the new childStructureHandler.
		}
		public void requiredChildSatisfied(){
			throw new IllegalStateException();
		}
		public void optionalChildSatisfied(){
			throw new IllegalStateException();
		}
		/*public void childSatisfiedPlus(){						
			throw new IllegalStateException();
		}*/		
		public void childSaturated(){
			throw new IllegalStateException();
		}
		public void childExcessive(){
			childParticleHandler.reportExcessive(rule, starttSystemId, starttLineNumber, starttColumnNumber);
			contentHandler = excessiveContent;
		}
	}
	class ExcessiveContent extends AbstractExcessiveContent{
		public boolean isSatisfied(){
			if(childStructureHandler != null && !childStructureHandler.isSatisfied()){
				return false;
			}			
			return true;
		}
		public int getContentIndex(){
				return EXCESSIVE_CONTENT;
		}
		public void childOpen(){
			// throw new IllegalStateException();
			// It is possible to have this in cases when the child of element
			// is handled by a UniqueChildPatternHandler (choice, ref , etc)
			// and after that was reduced excessive content is started. It would
			// be the initial childOpen event fired from the NoContent state of 
			// the new childStructureHandler
		}
		public void requiredChildSatisfied(){
			throw new IllegalStateException();
		}
		public void optionalChildSatisfied(){
			throw new IllegalStateException();
		}
		/*public void childSatisfiedPlus(){						
			throw new IllegalStateException();
		}*/		
		public void childSaturated(){
			throw new IllegalStateException();
		}
		public void childExcessive(){
			childParticleHandler.reportExcessive(rule, starttSystemId, starttLineNumber, starttColumnNumber);
		}
		// TODO what exactly is the state here
	}
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	
	public String toString(){		
		//return "ElementHandler "+hashCode()+" "+rule.toString()+" contentHandler "+contentHandler.toString();
		return "ElementHandler  "+rule.toString()+" contentHandler "+contentHandler.toString();
	}
} 