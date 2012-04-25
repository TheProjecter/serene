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
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.structure.RuleHandlerVisitor;

// The reductions are done at childSaturated and at change(in the getHandlerForPattern).
// Choice can never have excessive content. In case too many occurrences of 
// one of the children happen, then the error will be reported as too many
// occurrence of choice by the parent.
public class ChoiceHandler extends UniqueChildPatternHandler{		
	Rule currentChild;
	
	ChoiceHandler original;
	
	ChoiceHandler(){
		super();			
	}	
	
	
	//It seems that the choice is only sensitive to interleave if it's more than one.
	//This means it can be reduced every time the input changes from one name to another
	//It only needs to remember which is the current child particleHandler, or even better it's
	//childId	
	void init(AChoicePattern choice, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		this.rule = choice;	
		this.parent = parent;		
		this.stackHandler = stackHandler;
		this.errorCatcher = errorCatcher;			
		//childParticleHandler = null;
		//childStructureHandler = null;
	}
	
	public void recycle(){
		original = null;
		setEmptyState();
		recycler.recycle(this);
	}	
		
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	//StructureValidationHandler getAncestorOrSelfHandler(Rule rule); super
    public boolean handleDeactivation(){
        if(!parent.handleDeactivation()){
            stackHandler.endSubtreeValidation(this);
        }
        return true;
	}
	public StructureHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();	
		if(currentChild != null && currentChild != child){
			stackHandler.reset(this);
			return null;			
		}		
		if(childStructureHandler == null){
			childStructureHandler = child.getStructureHandler(errorCatcher, this, stackHandler);
			currentChild = child;
			return childStructureHandler;
		}		
		return childStructureHandler;
	}	
	// APattern getRule() super
	public boolean handleChildShiftAndOrder(APattern pattern, int expectedOrderHandlingCount){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}		
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(inputStackDescriptor.getCurrentItemInputRecordIndex(), pattern);
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(APattern pattern, int startInputRecordIndex){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	
	
	// TODO handleParticleShift()	
	public boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}		
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern, resolver)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(pattern, stackConflictsHandler, resolver);
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	//reduce
	public boolean handleChildShift(APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	
	// void handleValidatingReduce() super
	public int functionalEquivalenceCode(){
		if(currentChild != null){
			if(childStructureHandler != null)return super.functionalEquivalenceCode()+childStructureHandler.getRule().hashCode();
			else return super.functionalEquivalenceCode()+childParticleHandler.getRule().hashCode();
		}
		return super.functionalEquivalenceCode();
	}
	public ChoiceHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		ChoiceHandler copy = ((AChoicePattern)rule).getStructureHandler(errorCatcher, parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler,					
					contentIndex,
					startInputRecordIndex,
					isStartSet,
					currentChild);
		copy.setOriginal(this);
		return copy; 		
	}
	public ChoiceHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		ChoiceHandler copy = ((AChoicePattern)rule).getStructureHandler(errorCatcher, (StructureValidationHandler)parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler,					
					contentIndex,
					startInputRecordIndex,
					isStartSet,
					currentChild);
		copy.setOriginal(this);
		return copy;
	}	
	private void setOriginal(ChoiceHandler original){
		this.original = original;
	}
	public ChoiceHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	//End StructureHandler------------------------------------------------------------
	
	

	//Start ValidationHandler---------------------------------------------------------		
	void handleParticleShift(int inputRecordIndex, APattern childPattern){				
		setCurrentChildParticleHandler(childPattern);
		currentChildParticleHandler.handleOccurrence(inputRecordIndex);
	}	
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
	
			
	//Start InnerPattern------------------------------------------------------------------	
	// boolean handleStateSaturationReduce() super	
	// boolean isReduceAllowed() super
	// boolean isReduceRequired() super
	// boolean isReduceAcceptable() super
	// void setCurrentChildParticleHandler(APattern childPattern)
	// void closeContent() super
	// void closeContentParticle(APattern childPattern) super
	//for Reusable implementation
	void setEmptyState(){
		super.setEmptyState();
		currentChild = null;
	}
	//End InnerPattern------------------------------------------------------------------
		
	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler childParticleHandler, 
							StructureHandler childStructureHandler,							
							int contentIndex,
							int startInputRecordIndex,
							boolean isStartSet,
							Rule currentChild){
		if(childParticleHandler != null)this.childParticleHandler = childParticleHandler.getCopy(this, errorCatcher);
		if(childStructureHandler != null)this.childStructureHandler = childStructureHandler.getCopy(this, stackHandler, errorCatcher);
		
		this.contentIndex = contentIndex;
		
		if(this.isStartSet){
            activeInputDescriptor.unregisterClientForRecord(this.startInputRecordIndex, this);
        }
		this.startInputRecordIndex = startInputRecordIndex;
		this.isStartSet = isStartSet;
		if(isStartSet){		    
		    activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		}
		
		this.currentChild = currentChild;	
	}	
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	
	public String toString(){
		//return "ChoiceHandler "+hashCode()+" "+rule.toString()+" contentHandler "+contentHandler.toString();
		return "ChoiceHandler "+rule.toString()+" contentIndex="+contentIndex;
	}
} 