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

import serene.validation.schema.active.components.AGrammar;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.structure.RuleHandlerVisitor;

public class GrammarMaximalReduceHandler extends UCMaximalReduceHandler{
	GrammarMaximalReduceHandler original;	
	GrammarMaximalReduceHandler(){		
		super();					
	}	
	
	void init(AGrammar grammar, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		this.rule = grammar;
		this.parent = parent;
		this.stackHandler = stackHandler;
		this.errorCatcher = errorCatcher;
	}
	
	public void recycle(){
		original = null;
		setEmptyState();
		recycler.recycle(this);
	}
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	//StructureValidationHandler getAncestorOrSelfHandler(Rule rule); super
	// StructureHandler getChildHandler(Rule child) super
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	// void handleValidatingReduce() super
	// int functionalEquivalenceCode() super
	public GrammarMaximalReduceHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		GrammarMaximalReduceHandler copy = ((AGrammar)rule).getStructureHandler(errorCatcher, (MaximalReduceHandler)parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler,
					contentHandler.getContentIndex(),
					startInputRecordIndex,
					isStartSet);
		copy.setOriginal(this);
		return copy; 		
	}
	public GrammarMaximalReduceHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		GrammarMaximalReduceHandler copy = ((AGrammar)rule).getStructureHandler(errorCatcher, (MaximalReduceHandler)parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler, 
					contentHandler.getContentIndex(),
					startInputRecordIndex,
					isStartSet);
		copy.setOriginal(this);
		return copy;
	}
	private void setOriginal(GrammarMaximalReduceHandler original){
		this.original = original;
	}
	public GrammarMaximalReduceHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	//End StructureHandler------------------------------------------------------------
	

	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler childParticleHandler, 
							StructureHandler childStructureHandler,
							int contentHandlerContentIndex,
							int startInputRecordIndex,
							boolean isStartSet){
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
	}
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	public String toString(){
		//return "GrammarMaximalReduceHandler "+hashCode()+" "+rule.toString()+" contentHandler "+contentHandler.toString();
		return "GrammarMaximalReduceHandler  "+rule.toString()+" contentHandler "+contentHandler.toString();
	}	
} 