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

import java.util.Arrays;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.MultipleChildrenAPattern;

import serene.validation.handlers.structure.StructureHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

// TODO
// Cosider getting rid of the AbstractSaturatedContent subclasses.
// Replace with a on the fly computing of saturation in the isSaturated()
// Seems simpler and more consistent.
abstract class MultipleChildrenPatternHandler extends InnerPatternHandler{
		
	protected ParticleHandler[] childParticleHandlers;
	protected StructureHandler[] childStructureHandlers;
	protected int size;
		
	/**
	* Represents the total number of childSatisfied events expected by this 
	* handler. It equals the total number of required chilren of the pattern 
	* handled. When that has been reached the schema rule embodied by this handler 
	* is satisfied by what was found in the document.
	*/
	protected int satisfactionIndicator;
	/**
	* Represents the actual number of childSatisfied events fired by the 
	* child ParticleHandlers.
	*/
	protected int satisfactionLevel;
		
	/**
	* Represents the total number of childSaturated events expected by the 
	* handler from the child ParticleHandlers. If this number equals the total 
	* number of children this rule is eligible for reduction. Still, reduction is 
	* only performed if the schema configuration allows it.
	*/
	protected int saturationIndicator;
	/**
	* Represents the actual number of childSaturated events fired by the 
	* child ParticleHandlers.
	*/	
	protected int saturationLevel;
	
	
	protected ContentHandler unsatisfiedSaturatedContent;
	protected ContentHandler satisfiedSaturatedContent;
	protected ContentHandler unsatisfiedExcessiveContent;
	protected ContentHandler satisfiedExcessiveContent;
	
	public MultipleChildrenPatternHandler(MessageWriter debugWriter){
		super(debugWriter);
		size  = 5;		
		
		childParticleHandlers = new ParticleHandler[size];
		childStructureHandlers = new StructureHandler[size];
		
		noContent = new NoContent();
		openContent = new OpenContent();		
		satisfiedContent = new SatisfiedRequiredContent();
		unsatisfiedSaturatedContent = new UnsatisfiedSaturatedContent();
		satisfiedSaturatedContent = new SatisfiedSaturatedContent();
		unsatisfiedExcessiveContent = new UnsatisfiedExcessiveContent();
		satisfiedExcessiveContent = new SatisfiedExcessiveContent();
		contentHandler = noContent;
	}	
	//--------------------------------------------------------------------------------------------
	//Start ChildEventHandler---------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------
	public void requiredChildSatisfied(){		
		++satisfactionLevel;		
		contentHandler.requiredChildSatisfied();
	}
	public void optionalChildSatisfied(){
		contentHandler.optionalChildSatisfied();
	}
	public void childSaturated(){
		++saturationLevel;
		contentHandler.childSaturated();
	}	
	//--------------------------------------------------------------------------------------------
	//End ChildEventHandler-----------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------

	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	// StructureValidationHandler getAncestorOrSelfHandler(Rule rule) super	
    public void deactivate(){
        if(isReduceAllowed() && isReduceAcceptable()) stackHandler.endSubtreeValidation(this);
        else{
            if(!parent.handleDeactivation()) throw new IllegalStateException();
        }
	}
    public boolean handleDeactivation(){
		stackHandler.setAsCurrentHandler(this);
        return true;
	}
	public StructureHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		int childIndex = child.getChildIndex();
		if(childStructureHandlers[childIndex] == null){
			childStructureHandlers[childIndex] = child.getStructureHandler(errorCatcher, this, stackHandler);
		}
		return childStructureHandlers[childIndex];	
	}
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	public void handleValidatingReduce(){		
		reduce:{
			for(int i = 0; i < size; i++){			
				if(childStructureHandlers[i] != null){
					childStructureHandlers[i].handleValidatingReduce();
					if(childParticleHandlers[i] == null) //this handler was already reduced as result of the child's reduction
						break reduce; 
					childParticleHandlers[i].reportMissing(rule, startInputRecordIndex);
				}else if(childParticleHandlers[i] != null){					
					childParticleHandlers[i].reportMissing(rule, startInputRecordIndex);
				}else{
					if(((MultipleChildrenAPattern)rule).isChildRequired(i)){						
						APattern child = ((MultipleChildrenAPattern)rule).getChild(i);				
						int minOccurs = child.getMinOccurs();
						errorCatcher.missingContent(rule, startInputRecordIndex, child, minOccurs, 0, null);						
					}
				}
			}			
			handleReduce();
		}
	}
	public boolean acceptsMDescendentReduce(APattern p){
		// TODO should you be more defensive and check if the p is the actual 
		// pattern from the particle handler?
		// First thought: here is not really the case, there can be only one child.
			
				
		// if there is no corresponding ParticlEHandler, get one
		// if the ParticleHandler does not expect the last occurrence
		//				return true
		// else determine if this handler is satisfied or if it could be 
		// satisfied by the shifting of p // here if the particleHandler's 
		// distanceToSatisfaction < 1 it means that it is satisfied and it 
		// cannot have any influence on the satisfactionLevel
		// if distanceToSatisfaction > 1 it means it is too far and one shift
		// cannot satisfy the rule anyway
		// if distanceToSatisfaction == 1 then the satisfactionLevel needs to 
		// be tested
		// 		yes return parent.acceptsMDescendentReduce(rule)
		//		no return false
		
		// before every return recycle the ParticleHandler if it was aquired 
		// only for this method
		
		ParticleHandler childParticleHandler = childParticleHandlers[p.getChildIndex()];
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
			if(isSatisfied()) return parent.acceptsMDescendentReduce(rule);
			else if(distanceToSatisfaction == 1 && satisfactionLevel == satisfactionIndicator+1)return parent.acceptsMDescendentReduce(rule);
			else return false;
		}
	}
	public int functionalEquivalenceCode(){		
		int fec = rule.hashCode();
		for(int i = 0; i < childParticleHandlers.length; i++){
			if(childParticleHandlers[i] != null)fec += childParticleHandlers[i].functionalEquivalenceCode(); 
			if(childStructureHandlers[i] != null)fec += childStructureHandlers[i].functionalEquivalenceCode();
		}		
		return fec;
	}
	public void closeContentStructure(APattern pattern){
		int index = pattern.getChildIndex();
		childStructureHandlers[index].recycle();		
		childStructureHandlers[index] = null;
	}	
	public abstract MultipleChildrenPatternHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract MultipleChildrenPatternHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
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
	//boolean handleStateSaturationReduce() super	
	//boolean isReduceAllowed() super
	boolean isReduceRequired(){
		return saturationIndicator == size && contentHandler.isSaturated();
	}
	boolean isReduceAcceptable(){
		return parent.acceptsMDescendentReduce(rule);
	}	
	void setCurrentChildParticleHandler(APattern childPattern){		
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();		
		int childIndex = childPattern.getChildIndex();
		if(childParticleHandlers[childIndex] == null){
			childParticleHandlers[childIndex] = childPattern.getParticleHandler(this, errorCatcher);
		}	
		currentChildParticleHandler = childParticleHandlers[childIndex];
	}
	void closeContent(){
		for(int i = 0; i < size; i++){			
			if(childStructureHandlers[i] != null){
				closeContentStructure(i);
			}
			if(childParticleHandlers[i] != null){
				closeContentParticle(i);
			}		
		}
	}	
	void closeContentParticle(APattern pattern){		
		int index = pattern.getChildIndex();
		childParticleHandlers[index].recycle();
		childParticleHandlers[index] = null;
		
	}
	//for Reusable implementation
	void setEmptyState(){
		closeContent();
		if(stackConflictsHandler != null){
			stackConflictsHandler.close(this);
			stackConflictsHandler = null;
		}
		contentHandler = noContent;
		satisfactionLevel = 0;
		saturationLevel = 0;
		
		if(isStartSet){
		    activeInputDescriptor.unregisterClientForRecord(startInputRecordIndex, this);
		    isStartSet = false;
		    startInputRecordIndex = -1;
		}
	}			
	//End InnerPattern------------------------------------------------------------------
	
	boolean handleExcessiveChildReduce(){
		//APattern child = currentChildParticleHandler.getPattern();
		//handleReset();
		//handleChildShift(inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), inputStackDescriptor.getQName(), child);
		
		if(isReduceAllowed() && isReduceAcceptable()){
			handleReshift(currentChildParticleHandler.getRule());
			return true;
		}
		return false;
	} 
	
	void handleReshift(APattern pattern){
		stackHandler.reshift(this, pattern);
	}
    
    void handleValidatingReshift(APattern pattern){
		stackHandler.validatingReshift(this, pattern);
	}
    
	void closeContentStructure(){
		for(int i = 0; i < size; i++){			
			if(childStructureHandlers[i] != null){
				closeContentStructure(i);
			}
		}
	}	
	void closeContentParticle(){
		for(int i = 0; i < size; i++){
			if(childParticleHandlers[i] != null){
				closeContentParticle(i);
			}		
		}
	}	
	void closeContentStructure(int index){
		childStructureHandlers[index].recycle();		
		childStructureHandlers[index] = null;
	}
	
	void closeContentParticle(int index){
		childParticleHandlers[index].recycle();
		childParticleHandlers[index] = null;
	}
		
	
	void reportExcessive(){
		currentChildParticleHandler.reportExcessive(rule, startInputRecordIndex);
	}
	
	public StructureHandler[] getStructureHandlers(){
		return childStructureHandlers;
	}
	
	public ParticleHandler[] getParticleHandlers(){
		return childParticleHandlers;
	}
	
	protected class NoContent extends AbstractNoContent{
		public void childOpen(){	
			setStart();
			parent.childOpen();
			if(satisfactionIndicator == 0){
				contentHandler = satisfiedContent;
			}else{
				contentHandler = openContent;
			}
		}	
		public void optionalChildSatisfied(){
			setStart();
			parent.childOpen();
			if(satisfactionIndicator == 0){
				contentHandler = satisfiedContent;
			}else{
				contentHandler = openContent;
			}
		}	
		public void requiredChildSatisfied(){
			setStart();
			parent.childOpen();			
			if(satisfactionLevel == satisfactionIndicator) {
				contentHandler = satisfiedContent;
			}else{
				contentHandler = openContent;
			}
		}
		public void childSaturated(){
			setStart();
			parent.childOpen();
			if(saturationLevel == saturationIndicator){
				contentHandler = unsatisfiedSaturatedContent;
			}
			else contentHandler = openContent;
		}		
	}
	protected class OpenContent extends AbstractOpenContent{
		public void childOpen(){
		}
		public void optionalChildSatisfied(){
		}
		public void requiredChildSatisfied(){			
			if(satisfactionLevel == satisfactionIndicator) {
				contentHandler = satisfiedContent;
			}			
		}
		/*public void childSatisfiedPlus(){
			return ACCEPT;
		}*/
		public void childSaturated(){	
			if(saturationLevel == saturationIndicator){
				contentHandler = unsatisfiedSaturatedContent;
			}
		}
		public void childExcessive(){
			// To be totally transparent and consistent with total delegation of 
			// reduce decision responsibility to dedicated methods 
			// handleExcessiveChildReduce() is used here too, but it really seems
			// a bit exagerated.
			if(!handleExcessiveChildReduce()){
				reportExcessive();			
				contentHandler = unsatisfiedExcessiveContent;
			}
		}		
		
	}
	protected class SatisfiedRequiredContent extends AbstractSatisfiedContent{
		public boolean isSatisfied(){
			for(int i = 0; i < size; i++){
				if(childStructureHandlers[i] != null && !childStructureHandlers[i].isSatisfied())
					return false;
				
				if(childParticleHandlers[i] != null && !childParticleHandlers[i].isSatisfied()
					&& !(childParticleHandlers[i].getDistanceToSatisfaction() == 1 && childStructureHandlers[i] != null))
						return false;				
			}
			return true;
		}
		public void childOpen(){
		}
		public void optionalChildSatisfied(){
		}
		public void requiredChildSatisfied(){			
			throw new IllegalStateException();
		}
		/*public void childSatisfiedPlus(){
			//if(isClosingAllowed()) makeConsecutiveHandler();
			return ACCEPT;
		}*/
		public void childSaturated(){		
			if(saturationLevel == saturationIndicator){
				contentHandler = satisfiedSaturatedContent;
			}
		}
		public void childExcessive(){
			if(!handleExcessiveChildReduce()){
				reportExcessive();			
				contentHandler = satisfiedExcessiveContent;
			}
		}		
	}	
	protected class UnsatisfiedSaturatedContent extends AbstractSaturatedContent{
		public int getContentIndex(){
			return UNSATISFIED_SATURATED_CONTENT;
		}
		public void childOpen(){
		}
		public void optionalChildSatisfied(){
		}
		public void requiredChildSatisfied(){
			if(satisfactionLevel == satisfactionIndicator) contentHandler = satisfiedSaturatedContent;
		}
		public void childSaturated(){
			throw new IllegalStateException();
		}
		public void childExcessive(){
			// To be totally transparent and consistent with total delegation of 
			// reduce decision responsibility to dedicated methods 
			// handleExcessiveChildReduce() is used here too, but it really seems
			// a bit exagerated.
			if(!handleExcessiveChildReduce()){
				reportExcessive();
				contentHandler = unsatisfiedExcessiveContent;
			}
		}
		
		public boolean isSatisfied(){
			return false;
		}		
		public String toString(){
			return "CONTENT UNSATISFIED SATURATED";
		}	
	}	
	protected class SatisfiedSaturatedContent extends AbstractSaturatedContent{
		public boolean isSatisfied(){
			for(int i = 0; i < size; i++){
				//System.out.println(i);
				//System.out.println(childStructureHandlers[i]);
				if(childStructureHandlers[i] != null && !childStructureHandlers[i].isSatisfied())
					return false;
				//System.out.println(childParticleHandlers[i]);
				if(childParticleHandlers[i] != null && !childParticleHandlers[i].isSatisfied()
					&& !(childParticleHandlers[i].getDistanceToSatisfaction() == 1 && childStructureHandlers[i] != null))
						return false;				
			}
			return true;
		}
		public int getContentIndex(){
			return SATISFIED_SATURATED_CONTENT;
		}
		public void childOpen(){
		}
		public void optionalChildSatisfied(){
		}
		public void requiredChildSatisfied(){
			throw new IllegalStateException();			
		}
		public void childSaturated(){
			throw new IllegalStateException();
		}
		public void childExcessive(){
			if(!handleExcessiveChildReduce()){
				reportExcessive();			
				contentHandler = satisfiedExcessiveContent;
			}
		}		
		public String toString(){
			return "CONTENT SATISFIED SATURATED";
		}	
	}	
	protected class UnsatisfiedExcessiveContent extends AbstractExcessiveContent{
		public int getContentIndex(){
			return UNSATISFIED_EXCESSIVE_CONTENT;
		}
		public void childOpen(){
		}
		public void optionalChildSatisfied(){
		}
		public void requiredChildSatisfied(){
			if(satisfactionLevel == satisfactionIndicator) contentHandler = satisfiedExcessiveContent;
		}
		public void childSaturated(){
			// TODO // TODO 
		}
		
		public void childExcessive(){
			if(!handleExcessiveChildReduce()){
				reportExcessive();
			}
		}
		public boolean isSatisfied(){
			return false;
		}
		public boolean isSaturated(){
			if(saturationLevel == saturationIndicator)
				return true;
			return false;
		}
		
		public String toString(){
			return "CONTENT UNSATISFIED EXCESSIVE";
		}	
	}	
	protected class SatisfiedExcessiveContent extends AbstractExcessiveContent{
		public boolean isSatisfied(){
			for(int i = 0; i < size; i++){
				if(childStructureHandlers[i] != null && !childStructureHandlers[i].isSatisfied())
					return false;
				
				if(childParticleHandlers[i] != null && !childParticleHandlers[i].isSatisfied()
					&& !(childParticleHandlers[i].getDistanceToSatisfaction() == 1 && childStructureHandlers[i] != null))
						return false;				
			}
			return true;
		}
		public int getContentIndex(){
			return SATISFIED_EXCESSIVE_CONTENT;
		}
		public void childOpen(){
		}
		public void optionalChildSatisfied(){
		}
		public void requiredChildSatisfied(){
			throw new IllegalStateException();			
		}
		public void childSaturated(){
			// TODO // TODO 
		}
		public void childExcessive(){
			if(!handleExcessiveChildReduce()){
				reportExcessive();			
				contentHandler = satisfiedExcessiveContent;
			}
		}
		public boolean isSaturated(){
			if(saturationLevel == saturationIndicator)
				return true;
			return false;
		}
		
		public String toString(){
			return "CONTENT SATISFIED EXCESSIVE";
		}	
	}
	public String toString(){
		return "MultipleChildrenPatternHandler contentHandler "+contentHandler.toString();
	}
} 