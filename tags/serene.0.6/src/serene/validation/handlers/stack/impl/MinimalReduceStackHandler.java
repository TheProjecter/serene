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

import serene.util.IntList;

import serene.validation.schema.active.components.ACompositor;

import serene.validation.handlers.content.util.ValidationItemLocator;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.StructureHandler;

import sereneWrite.MessageWriter;

public class MinimalReduceStackHandler extends ContextStackHandler{
	ActiveModelStackHandlerPool pool;
	
	MinimalReduceStackHandler(MessageWriter debugWriter){
		super(debugWriter);
	}		
	
	public void recycle(){
		endingValidation = false;
		isCurrentHandlerReseted = false;
		topHandler.recycle();
		topHandler = null;
		currentHandler = null;
		recycler.recycle(this);		
	}
		
	void init(ValidationItemLocator validationItemLocator, ActiveModelStackHandlerPool pool){
		this.recycler = pool;
		this.pool = pool;
		this.validationItemLocator = validationItemLocator;
	}
	
	void init(IntList reduceCountList, ACompositor topPattern, ErrorCatcher errorCatcher){
		topHandler = topPattern.getStructureHandler(reduceCountList, errorCatcher, this);
		currentHandler = topHandler;
		pathHandler.init(topHandler);		
	}
	
	void init(IntList reduceCountList, IntList startedCountList, ACompositor topPattern, ErrorCatcher errorCatcher){
		if(topPattern != null){
			topHandler = topPattern.getStructureHandler(reduceCountList, startedCountList, errorCatcher, this);
			currentHandler = topHandler;
			pathHandler.init(topHandler);
		}
	}
	
	public MinimalReduceStackHandler getCopy(IntList reduceCountList, ErrorCatcher errorCatcher){
		MinimalReduceStackHandler copy = pool.getMinimalReduceStackHandler(reduceCountList, null, errorCatcher);
		StructureHandler topCopy = topHandler.getCopy(reduceCountList, copy, errorCatcher);
		copy.setState(topCopy);
		return copy;
	}
	
	public MinimalReduceStackHandler getCopy(IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher){
		MinimalReduceStackHandler copy = pool.getMinimalReduceStackHandler(reduceCountList, startedCountList, null, errorCatcher);
		StructureHandler topCopy = topHandler.getCopy(reduceCountList, startedCountList, copy, errorCatcher);
		copy.setState(topCopy);
		return copy;
	}
	
	private void setState(StructureHandler topHandler){
		this.topHandler = topHandler;
		currentHandler = topHandler;
		pathHandler.init(topHandler);
	}
	
	public String toString(){
		//if(currentHandler != null) return "MinimalReduceStackHandler "+hashCode()+" stack: "+currentHandler.stackToString();
		if(currentHandler != null) return "MinimalReduceStackHandler  stack: "+currentHandler.stackToString();
		//return "MinimalReduceStackHandler "+hashCode()+" stack: null";
		return "MinimalReduceStackHandler  stack: null";
	}
}