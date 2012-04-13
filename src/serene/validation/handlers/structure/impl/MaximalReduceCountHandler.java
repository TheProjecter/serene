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
import serene.validation.schema.active.components.AInnerPattern;
import serene.validation.schema.active.components.MultipleChildrenAPattern;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.CardinalityHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

// TODO
// Cosider getting rid of the AbstractSaturatedContent subclasses.
// Replace with a on the fly computing of saturation in the isSaturated()
// Seems simpler and more consistent.
abstract class MaximalReduceCountHandler extends LimitReduceCountHandler implements MaximalReduceHandler{
		
	public MaximalReduceCountHandler(){
		super();
		// TODO
		// init potentialReduceCount???
		//create contentHandler subclasses
		//set the default contentHandler value
	}	
	public MaximalReduceHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		int childIndex = child.getChildIndex();
		if(childStructureHandlers[childIndex] == null){
			MaximalReduceHandler handler = child.getStructureHandler(errorCatcher, this, stackHandler);
			childStructureHandlers[childIndex] = handler;
			return handler;
		}
		return (MaximalReduceHandler)childStructureHandlers[childIndex];
	}
	

	void handleReshift(APattern pattern){
		//doubleHandler.handleReshiftMax(pattern);
		System.out.println("TODO "+toString());		
	}	
	
	
	//Start InnerPattern------------------------------------------------------------------	
	/**
	* It asseses the state of the handler and triggers reduce due to the saturation 
	* state of a handler(isReduceRequired() and isReduceAcceptable() are used).
	*/
	// boolean handleStateSaturationReduce() super;
		
	//boolean isReduceAllowed(){
		//return contentHandler.isSatisfied();
	//}
	boolean isCurrentChildReduceRequired(){
		return currentChildParticleHandler.getIndex() == CardinalityHandler.SATISFIED_NEVER_SATURATED 
			|| currentChildParticleHandler.getIndex() == CardinalityHandler.SATISFIED_SIMPLE
			|| currentChildParticleHandler.getIndex() == CardinalityHandler.SATURATED;
		// equivalent
		// return isCurrentChildReduceAllowed(); 
	}
	//boolean isReduceAcceptable();
		
	//void setCurrentChildParticleHandler(APattern childPattern);
		
	//void closeContent();	
	//void closeContentParticle(APattern childPattern);

	//for Reusable implementation
	//void setEmptyState();		
	//End InnerPattern------------------------------------------------------------------
	
	public String toString(){
		return "MaximalReduceHandler contentHandler "+contentHandler.toString();
	}
} 