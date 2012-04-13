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
import serene.validation.schema.active.components.AInnerPattern;
import serene.validation.schema.active.components.AChoicePattern;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

// The reductions are done at childSaturated and at change(in the getHandlerForPattern).
// Choice can never have excessive content. In case too many occurrences of 
// one of the children happen, then the error will be reported as too many
// occurrence of choice by the parent.
public class ChoiceMinimalReduceHandler extends UCMinimalReduceHandler{
	// TODO see about the shifts	
	Rule currentChild;
	
	ChoiceMinimalReduceHandler original;
	ChoiceMinimalReduceHandler(){
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
	public MinimalReduceHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler == null){
			MinimalReduceHandler handler = child.getStructureHandler(errorCatcher, this, stackHandler);
			childStructureHandler = handler;			
			currentChild = child;
			return handler;
		}else if(currentChild != child){
			stackHandler.reset(this);
			return null;			
		}			
		return (MinimalReduceHandler)childStructureHandler;
	}	
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	// void handleValidatingReduce() super
	// int functionalEquivalenceCode() super
	public ChoiceMinimalReduceHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		ChoiceMinimalReduceHandler copy = ((AChoicePattern)rule).getStructureHandler(errorCatcher, (MinimalReduceHandler)parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler, 
					contentHandler.getContentIndex(),
					startInputRecordIndex,
					isStartSet,
					currentChild);
		copy.setOriginal(this);
		return copy;		
		
	}
	public ChoiceMinimalReduceHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		ChoiceMinimalReduceHandler copy = ((AChoicePattern)rule).getStructureHandler(errorCatcher, (MinimalReduceHandler)parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler,
					contentHandler.getContentIndex(),
					startInputRecordIndex,
					isStartSet,
					currentChild);
		copy.setOriginal(this);
		return copy;
	}	
	private void setOriginal(ChoiceMinimalReduceHandler original){
		this.original = original;
	}
	public ChoiceMinimalReduceHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	//End StructureHandler------------------------------------------------------------
	
	
	//Start InnerPattern------------------------------------------------------------------	
	// boolean handleStateSaturationReduce() super	
	// boolean isReduceAllowed() super
	// boolean isReduceRequired() super
	// boolean isReduceAcceptable() super
	// void setCurrentChildParticleHandler(APattern childPattern) super
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
							int contentHandlerContentIndex,
							int startInputRecordIndex,
							boolean isStartSet,
							Rule currentChild){
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
		}else{
			throw new IllegalArgumentException();
		}	
		
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
		//return "ChoiceMinimalReduceHandler "+hashCode()+" "+rule.toString()+" contentHandler "+contentHandler.toString();
		return "ChoiceMinimalReduceHandler  "+rule.toString()+" contentHandler "+contentHandler.toString();
	}
} 