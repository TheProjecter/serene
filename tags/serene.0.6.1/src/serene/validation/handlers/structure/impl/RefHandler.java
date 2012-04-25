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
import serene.validation.schema.active.components.ARef;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.structure.RuleHandlerVisitor;

public class RefHandler extends UniqueChildPatternHandler{
	RefHandler original;
	RefHandler(){		
		super();		
	}	
	
	void init(ARef ref, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){		
		this.rule = ref;
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
	public StructureHandler getChildHandler(Rule child){
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler == null){
			childStructureHandler = child.getStructureHandler(errorCatcher, this, stackHandler);
		}
		return childStructureHandler;
	}	
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	// void handleValidatingReduce() super
	// int functionalEquivalenceCode() super
	public RefHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		RefHandler copy = ((ARef)rule).getStructureHandler(errorCatcher, parent, stackHandler);
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
	public RefHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		RefHandler copy = ((ARef)rule).getStructureHandler(errorCatcher, (StructureValidationHandler)parent, stackHandler);
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
	private void setOriginal(RefHandler original){
		this.original = original;
	}
	public RefHandler getOriginal(){
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
		//return "RefHandler "+hashCode()+" "+rule.toString()+" contentHandler "+contentHandler.toString();
		return "RefHandler  "+rule.toString()+" contentIndex="+contentIndex;
	}		
} 