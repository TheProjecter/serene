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
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

/**
* Subclasses handle all the patterns that don't have a direct correspondent in the 
* document. These are patterns that reduce by themselves based on how the input coming
* from the document fulfills the embodied rules.
*/
abstract class InnerPatternHandler extends StructureValidationHandler{
	
	APattern rule; 
	ParticleHandler currentChildParticleHandler;
	
	StackConflictsHandler stackConflictsHandler;
	InnerPatternHandler(){
		super();		
	}
		
	
	public void setStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
		this.stackConflictsHandler = stackConflictsHandler;
	}
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	public StructureHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		else return parent.getAncestorOrSelfHandler(rule);
	}	
	// void deactivate() subclasses
	//StructureHandler getChildHandler(Rule child); subclasses	
	public APattern getRule(){
		return rule;
	}
	
	public boolean handleChildShiftAndOrder(APattern pattern, int expectedOrderHandlingCount){		
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
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex){
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex){
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	
	// TODO handleParticleShift()
	public boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
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
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	
	
	//void handleValidatingReduce(); subclasses			
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
		
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern child, APattern sourceDefinition, InternalConflictResolver resolver){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
	}
	//int functionalEquivalenceCode(); subclass	
	
	public void setConflict(int index, Rule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(path[index] != rule) throw new IllegalStateException();
		if(this.stackConflictsHandler == null) this.stackConflictsHandler = stackConflictsHandler;
		boolean newRecord = stackConflictsHandler.record(rule, this, resolver);
		if(newRecord && ++index != path.length)parent.setConflict(index, path, stackConflictsHandler, resolver);
	}
	
	public abstract InnerPatternHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract InnerPatternHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
	
	//Start ValidationHandler---------------------------------------------------------		
	void handleParticleShift(int inputRecordIndex, APattern childPattern){
		setCurrentChildParticleHandler(childPattern);
		currentChildParticleHandler.handleOccurrence(inputRecordIndex);
	}
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		setCurrentChildParticleHandler(childPattern);
		currentChildParticleHandler.handleOccurrence(stackConflictsHandler, resolver);
	}
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler){
		setCurrentChildParticleHandler(childPattern);
		currentChildParticleHandler.handleOccurrence(stackConflictsHandler);
	}    
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
	
	
	
	//Start InnerPattern------------------------------------------------------------------	
	/**
	* It asseses the state of the handler and triggers reduce due to the saturation 
	* state of a handler(isReduceRequired() and isReduceAcceptable() are used).
	*/
	boolean handleStateSaturationReduce(){
		if(isReduceRequired() && isReduceAcceptable()){			
			handleReduce();// TODO
			return true;
		}
		return false;
	}
		
	boolean isReduceAllowed(){
		return isSatisfied();
	}
	abstract boolean isReduceRequired();
	abstract boolean isReduceAcceptable();
		
	abstract void setCurrentChildParticleHandler(APattern childPattern);
		
	abstract void closeContent();	
	abstract void closeContentParticle(APattern childPattern);

	//for Reusable implementation
	abstract void setEmptyState();		
	//End InnerPattern------------------------------------------------------------------
	
}