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
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.structure.RuleHandlerVisitor;

public class GrammarMinimalReduceHandler extends UCMinimalReduceHandler{
	GrammarMinimalReduceHandler original;	
	GrammarMinimalReduceHandler(){		
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
    // void deactivate() super
    // boolean handleDeactivation() super
	// StructureHandler getChildHandler(Rule child) super
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	// void handleValidatingReduce() super
	// int functionalEquivalenceCode() super
	public GrammarMinimalReduceHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		GrammarMinimalReduceHandler copy = ((AGrammar)rule).getStructureHandler(errorCatcher, (MinimalReduceHandler)parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler,
					contentIndex,
					startInputRecordIndex,
					isStartSet);
		copy.setOriginal(this);
		return copy; 		
	}
	public GrammarMinimalReduceHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		GrammarMinimalReduceHandler copy = ((AGrammar)rule).getStructureHandler(errorCatcher, (MinimalReduceHandler)parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler, 
					contentIndex,
					startInputRecordIndex,
					isStartSet);
		copy.setOriginal(this);
		return copy;
	}
	private void setOriginal(GrammarMinimalReduceHandler original){
		this.original = original;
	}
	public GrammarMinimalReduceHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	//End StructureHandler------------------------------------------------------------
	

	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler childParticleHandler, 
							StructureHandler childStructureHandler,
							int contentIndex,
							int startInputRecordIndex,
							boolean isStartSet){
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
	}
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	public String toString(){
		//return "GrammarMinimalReduceHandler "+hashCode()+" "+rule.toString()+" contentHandler "+contentHandler.toString();
		return "GrammarMinimalReduceHandler  "+rule.toString()+" contentIndex="+contentIndex;
	}	
} 