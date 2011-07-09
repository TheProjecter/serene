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
import serene.validation.schema.active.components.UniqueChildAPattern;
import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

// same as choice, also for simplification
// the difference is that it only has one possible child

// The reductions are done at childSaturated and at change.
// Choice can never have excessive content. In case too many occurrences of 
// one of the children happen, then the error will be reported as too many
// occurrence of choice by the parent.

abstract class UniqueChildPatternHandler extends InnerPatternHandler{	
	ParticleHandler childParticleHandler;
	StructureHandler childStructureHandler;	
	
	UniqueChildPatternHandler(MessageWriter debugWriter){		
		super(debugWriter);
		noContent = new NoContent();
		openContent = new OpenContent();
		satisfiedContent = new SatisfiedContent();
		saturatedContent = new SaturatedContent();
		contentHandler = noContent;
	}	
	
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	//StructureValidationHandler getAncestorOrSelfHandler(Rule rule); super
    public void deactivate(){
		stackHandler.endSubtreeValidation(this);
	}
    public boolean handleDeactivation(){
		return parent.handleDeactivation();
	}
	public StructureHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler == null) childStructureHandler = child.getStructureHandler(errorCatcher, this, stackHandler);
		return childStructureHandler;
	}	
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	public void handleValidatingReduce(){
		// - it must have some child or else it would not exist
		// - reduce first any childStructureHandler present 
		//		-> updates the childParticleHandler
		//				-> may trigger saturated state reduce >> childParticleHandler will be null 
		// - afterwards if the childParticleHandler is present(there was no saturated state reduce)
		//		-> if reduce is allowed (state is satisfied) reduce this
		//		-> else report missing content and than reduce
		if(childStructureHandler != null){
			childStructureHandler.handleValidatingReduce();
		}
		if(childParticleHandler != null){
			if(!isReduceAcceptable()){
				childParticleHandler.reportMissing(rule, starttSystemId, starttLineNumber, starttColumnNumber);
			}
			handleReduce();
		}
	}
	public boolean acceptsMDescendentReduce(APattern p){
		// TODO should you be more defensive and check if the p is the actual 
		// pattern from the particle handler?
		// First thought: here is not really the case, there can be only one child.
				
		// if there is no corresponding ParticlHandler, get one
		// if the ParticleHandler does not expect the last occurrence
		//				return true
		// else determine if this handler is satisfied or if it could be 
		// satisfied by the shifting of p // here it's enough to see that the 
		// particleHandler's distanceToSatisfaction is smaller then or equal to 1
		// 		yes return parent.acceptsMDescendentReduce(rule)
		//		no return false
		
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
			int distanceToSatisfaction = childParticleHandler.getDistanceToSatisfaction();
			if(transitoryParticle){
				childParticleHandler.recycle();
				childParticleHandler = null;
			}
			/*if(isSatisfied()) return parent.acceptsMDescendentReduce(rule);
			else*/ if(distanceToSatisfaction <= 1 )return parent.acceptsMDescendentReduce(rule);
			else return false;
		}		
	}
	public int functionalEquivalenceCode(){
		int fec = rule.hashCode();
		if(childParticleHandler != null)fec += childParticleHandler.functionalEquivalenceCode();		
		if(childStructureHandler != null)fec += childStructureHandler.functionalEquivalenceCode();
		return fec;
	}
	public void closeContentStructure(APattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
	}
	public abstract UniqueChildPatternHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract UniqueChildPatternHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
			
	
	//Start ValidationHandler---------------------------------------------------------		
	// boolean handleChildShift(String systemId, int lineNumber, int columnNumber, String qName, APattern childPattern) super
	// boolean handleContentOrder(int expectedOrderHandlingCount, boolean conflict, APattern childDefinition, AElement sourceDefinition) super	
	// void setStart() super
	//End ValidationHandler-----------------------------------------------------------
	
	
	
	
	//Start InnerPattern------------------------------------------------------------------	
	// boolean handleStateSaturationReduce() super	
	// boolean isReduceAllowed()super
	boolean isReduceRequired(){
		return contentHandler.isSaturated();
	}
	boolean isReduceAcceptable(){
		return true;
	}
	void setCurrentChildParticleHandler(APattern childPattern){	
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();				
		if(childParticleHandler == null){
			childParticleHandler = childPattern.getParticleHandler(this, errorCatcher);
		}
		currentChildParticleHandler = childParticleHandler;
	}		
	void closeContent(){
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
	}		
	void closeContentParticle(APattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
	}
	//for Reusable implementation
	void setEmptyState(){
		closeContent();
		if(stackConflictsHandler != null){
			stackConflictsHandler.close(this);
			stackConflictsHandler = null;
		}
		contentHandler = noContent;		
		starttQName = null;
		starttSystemId = null;
	}
	//End InnerPattern------------------------------------------------------------------
	
	void closeContentStructure(){		
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
	}
	void closeContentParticle(){		
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
	}
	
	public StructureHandler getStructureHandler(){
		return childStructureHandler;
	}
	
	public ParticleHandler getParticleHandler(){
		return childParticleHandler;
	}
	class NoContent extends AbstractNoContent{
		public void childOpen(){	
			setStart();
			parent.childOpen();
			contentHandler = openContent;		
		}
		public void requiredChildSatisfied(){
			setStart();
			parent.childOpen();
			contentHandler = satisfiedContent;
		}
		public void optionalChildSatisfied(){
			setStart();
			parent.childOpen();
			contentHandler = satisfiedContent;
		}
		public void childSaturated(){
			setStart();
			parent.childOpen();
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
		/*public void childSatisfiedPlus(){			
			throw new IllegalStateException();
		}*/
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
			throw new IllegalStateException();
		}
		public void requiredChildSatisfied(){			
			throw new IllegalStateException();
		}
		public void optionalChildSatisfied(){			
			throw new IllegalStateException();
		}
		/*public void childSatisfiedPlus(){
			//do nothing favor shift
			//could become a problem if min = 3 would be possible
			return ACCEPT;
		}*/
		public void childSaturated(){
			contentHandler = saturatedContent;
		}
		public void childExcessive(){			
			throw new IllegalStateException();			
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
			throw new IllegalStateException();
		}
		public void requiredChildSatisfied(){
			throw new IllegalStateException();
		}
		public void optionalChildSatisfied(){
			throw new IllegalStateException();
		}		
		public void childSaturated(){
			throw new IllegalStateException();
		}
		public void childExcessive(){
			throw new IllegalStateException();
			// The philosophy is to always reduce UniqueChild when saturated
			// so this should never happen
		}			
	}
	
	public String toString(){
		return "UniqueChildStructureValidationHandler contentHandler "+contentHandler.toString();
	}	
} 