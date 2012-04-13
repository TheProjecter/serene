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
import java.util.Map;
import java.util.BitSet;

import serene.bind.util.Queue;
import serene.bind.BindingModel;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.util.PathHandler;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.ActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.conflict.ExternalConflictHandler;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.structure.StructureHandler;

import sereneWrite.MessageWriter;

public class ContextStackHandler  implements  StackHandler{
	InputStackDescriptor inputStackDescriptor;
	StructureHandler topHandler;
	StructureHandler currentHandler;
	int expectedOrderHandlingCount;	

	PathHandler pathHandler;
	
	StackHandlerRecycler recycler;
	
	boolean endingValidation;
	
	/**
	* Is set to true when during path activation the reset method has been called
	* and in that case the path activation must be redone with the new currentHandler. 
	*/
	boolean isCurrentHandlerReseted; 
	
	MessageWriter debugWriter;
	
	ContextStackHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		endingValidation = false;
		isCurrentHandlerReseted = false;
		pathHandler = new PathHandler(debugWriter);
	}	
	
	
	public void recycle(){
		endingValidation = false;
		isCurrentHandlerReseted = false;
		topHandler.recycle();
		topHandler = null;
		currentHandler = null;
		recycler.recycle(this);		
	}
	
	void init(InputStackDescriptor inputStackDescriptor, StackHandlerRecycler recycler){
		this.recycler = recycler;
		this.inputStackDescriptor = inputStackDescriptor;
	}
	
	void init(ActiveType type, ErrorCatcher errorCatcher){
		topHandler = type.getStructureHandler(errorCatcher, this);
		currentHandler = topHandler;
		pathHandler.init(topHandler);
	}
		
	
	public void shift(AElement element){		
		setCurrentHandler(element);	
		currentHandler.handleChildShiftAndOrder(element, expectedOrderHandlingCount);		
	}	
	
	public void shift(AAttribute attribute){				
		setCurrentHandler(attribute);	
		currentHandler.handleChildShiftAndOrder(attribute, expectedOrderHandlingCount);		
	}
	
	public void shift(CharsActiveTypeItem chars){				
		setCurrentHandler(chars);		
		currentHandler.handleChildShiftAndOrder(chars, expectedOrderHandlingCount);		
	}
	
	public void shiftAllElements(List<AElement> elementDefinitions, ConflictMessageReporter conflictMessageReporter){
		throw new IllegalStateException();
	}	
	public void shiftAllAttributes(List<AAttribute> attributeDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){
		throw new IllegalStateException();
	}	
	public void shiftAllCharsDefinitions(List<? extends CharsActiveTypeItem> charsDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){
		throw new IllegalStateException();
	}
	
	public void shiftAllTokenDefinitions(List<? extends DatatypedActiveTypeItem> charsDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){
		throw new IllegalStateException();
	}
	
	public void shiftAllTokenDefinitions(List<? extends DatatypedActiveTypeItem> charsDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
		throw new IllegalStateException();
	}
	
	public void shiftAllElements(List<AElement> elementDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter){
		throw new IllegalStateException();
	}	
	public void shiftAllAttributes(List<AAttribute> attributeDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
		throw new IllegalStateException();
	}
	public void shiftAllCharsDefinitions(List<? extends CharsActiveTypeItem> charsDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
	    throw new IllegalStateException();
	}
	public void shiftAllElements(List<AElement> elementDefinitions, ConflictMessageReporter conflictMessageReporter, BindingModel bindingModel, Queue targetQueue, int reservationStartEntry, int reservationEndEntry, Map<AElement, Queue> candidateQueues){
		throw new IllegalStateException();
	}
	
	public void shiftAllAttributes(List<AAttribute> attributeDefinitions, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue targetQueue, int targetEntry, BindingModel bindingModel){
		throw new IllegalStateException();
	}
	
	public void shiftAllElements(List<AElement> elementDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter, BindingModel bindingModel, Queue targetQueue, int reservationStartEntry, int reservationEndEntry, Map<AElement, Queue> candidateQueues){
		throw new IllegalStateException();
	}
	public void shiftAllAttributes(List<AAttribute> attributeDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue targetQueue, int targetEntry, BindingModel bindingModel){
		throw new IllegalStateException();
	}
	
	public void reduce(StructureHandler handler){
		// Check that during normal validation only the active branches are allowed to reduce
		// During endValidation it starts from the bottom reducing any existing branch,
		// no check is performed.
		if(!endingValidation && !isActive(handler, currentHandler))throw new IllegalArgumentException();
		StructureHandler parent = handler.getParentHandler();
		APattern pattern = (APattern)handler.getRule();
		if(parent.handleChildShift(pattern, /*handler.getItemId(), handler.getStartQName(), handler.getStartSystemId(), handler.getStartLineNumber(), handler.getStartColumnNumber())*/ handler.getStartInputRecordIndex())){
			parent.closeContentStructure(pattern);// must be last so it does not remove location data before error messages
			currentHandler = parent;
		}
	}
	
	public void reshift(StructureHandler handler, APattern child){
		reduce(handler);
		 
		setCurrentHandler(child);
		currentHandler.handleChildShiftAndOrder(child, expectedOrderHandlingCount);
	}
	
	public void validatingReshift(StructureHandler handler, APattern child){
		endSubtreeValidation(handler);
		 
		setCurrentHandler(child);
		currentHandler.handleChildShiftAndOrder(child, expectedOrderHandlingCount);
	}
	
	// Changes the old currentHandler during path activation. The new currentHandler
	// must be set again by calling the pathHandler to activate in the new conditions. 
	public void reset(StructureHandler handler){
		//TODO	
		endSubtreeValidation(currentHandler);
		isCurrentHandlerReseted = true;
	}
		
	public void blockReduce(StructureHandler handler, int count, APattern pattern, int startInputRecordIndex){
		// TODO the checks for Exception
		if(!endingValidation && !isActive(handler, currentHandler))throw new IllegalArgumentException();
		StructureHandler parent = handler.getParentHandler();	
		boolean shifted = true;
		for(int i = 0; i < count; i++){
			// TODO
			// Q: Is it possible to get in trouble with some form of recursive call 
			// to setCurrentHandler() through deactivate?
			setCurrentHandler(pattern);
			// it should be active at the first cycle of the loop//or it might not be the parent???
			if(!currentHandler.handleChildShift(count, pattern, startInputRecordIndex)){
				shifted = false;
			}
		}
		if(shifted)parent.closeContentStructure(pattern);// must be last so it does not remove location data before error messages
	}
	public void limitReduce(StructureHandler handler, int MIN, int MAX, APattern pattern, int startInputRecordIndex){
		// TODO the checks for Exception
		if(!endingValidation && !isActive(handler, currentHandler))throw new IllegalArgumentException();
		StructureHandler parent = handler.getParentHandler();	
		boolean shifted = true;
		for(int i = 0; i < MIN; i++){
			// It is only shifting up to min since the case of interleave pattern
			// with multiple cardinality containing other compositors with multiple
			// cardinality is not handled. MInterleaveHandler throws 
			// UnsupportedOperationException if called.			
			setCurrentHandler(pattern);			
			if(!currentHandler.handleChildShift(MIN, MAX, pattern, startInputRecordIndex)){
				shifted = false;
			}
		}
		if(shifted)parent.closeContentStructure(pattern);// must be last so it does not remove location data before error messages
	}
	
	public void endValidation(){		
		endingValidation = true;
		topHandler.handleValidatingReduce();
	}
	
	public void endSubtreeValidation(StructureHandler handler){		
		endingValidation = true;
		handler.handleValidatingReduce();
		endingValidation = false;
	}
	
	/**
	* current is a handler on the currently active path
	*/
	boolean isActive(StructureHandler handler, StructureHandler current){
		if(current == null)return false;
		if(current.equals(handler))return true;
		return isActive(handler, current.getParentHandler());
	}
		
	public StructureHandler getTopHandler(){
		return topHandler;
	}
	
	public StructureHandler getCurrentHandler(){		
		return currentHandler;
	}
	
	public int functionalEquivalenceCode(){		
		return topHandler.functionalEquivalenceCode();
	}
	
    public void setAsCurrentHandler(StructureHandler sh){
        this.currentHandler = sh;
    }
    
	public boolean handlesConflict(){
		return false;
	}
	/**
	* Sets the handler upon which the item must be shifted. At first the 
	* currentHandler is deactivated, this can lead to closing the subtree 
	* corresponding to it, depending on the nature of the currentHandler, 
	* the nature of the new handler and the context(interleaved?).
	*/
	void setCurrentHandler(AElement item){		
		//System.out.println(item);
		//System.out.println(currentHandler);
		//System.out.println(topHandler);
		
		Rule parent  = item.getParent();
		
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
            setCurrentHandler(item);
            return;
        }
        currentHandler = result;
		expectedOrderHandlingCount = pathHandler.getExpectedOrderHandlingCount();
	}


	/**
	* Sets the handler upon which the item must be shifted. At first the 
	* currentHandler is deactivated, this can lead to closing the subtree 
	* corresponding to it, depending on the nature of the currentHandler, 
	* the nature of the new handler and the context(interleaved?).
	*/
	void setCurrentHandler(AAttribute item){		
		//System.out.println(item);
		//System.out.println(currentHandler);
		//System.out.println(topHandler);
		
		Rule parent  = item.getParent();
		
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
            setCurrentHandler(item);
            return;
        }
        currentHandler = result;
		expectedOrderHandlingCount = 0;
	}	
	
	/**
	* Sets the handler upon which the item must be shifted. At first the 
	* currentHandler is deactivated, this can lead to closing the subtree 
	* corresponding to it, depending on the nature of the currentHandler, 
	* the nature of the new handler and the context(interleaved?).
	*/
	void setCurrentHandler(CharsActiveTypeItem item){		
		//System.out.println(item);
		//System.out.println(currentHandler);
		//System.out.println(topHandler);
		
		Rule parent  = item.getParent();
		
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
            setCurrentHandler(item);
            return;
        }
        currentHandler = result;
		expectedOrderHandlingCount = pathHandler.getExpectedOrderHandlingCount();
	}
	
	// Only called when reducing(reshift, validatingReshift etc.), so there's no 
	// need for in context checking. It is already in context and the 
	// expectedOrderHandlingCount was set. Possible problem: might need new 
	// expectedOrderHandlingCount. In that case some mechanism to know when this 
	// is 0 due to nature of the pattern for which the handler is set is needed.
	void setCurrentHandler(APattern pattern){	
		// System.out.println(currentHandler);
		// System.out.println(topHandler);		
		Rule parent  = pattern.getParent();
		pathHandler.activatePath(currentHandler, parent);
		StructureHandler result = pathHandler.getBottomHandler();
        if(result == null){ // null is returned after a ChoiceHandler was reset due to a child that differs from currentChild 
            setCurrentHandler(pattern);
            return;
        }
        currentHandler = result;
	}	
	
	
	public String toString(){
		//if(currentHandler != null) return "ContextStackHandler "+hashCode()+" stack: "+currentHandler.stackToString();
		if(currentHandler != null) return "ContextStackHandler stack: "+currentHandler.stackToString();
		//return "ContextStackHandler "+hashCode()+" stack: null";
		return "ContextStackHandler  stack: null";
	}
}