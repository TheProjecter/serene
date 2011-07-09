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

package serene.validation.handlers.content.impl;

import java.util.Arrays;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.error.ValidationErrorHandler;
import serene.validation.handlers.error.ExternalConflictErrorHandler;
import serene.validation.handlers.error.CommonErrorHandler;
import serene.validation.handlers.error.DefaultErrorHandler;
import serene.validation.handlers.error.ContextErrorHandlerManager;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

abstract class ValidatingEEH extends ComparableEEH implements ContextErrorHandlerManager{

	ValidatorErrorHandlerPool errorHandlerPool;
	
	ContextErrorHandler contextErrorHandler;
	
	ValidationErrorHandler validationErrorHandler;
	ExternalConflictErrorHandler externalConflictErrorHandler;
	CommonErrorHandler commonErrorHandler;
	DefaultErrorHandler defaultErrorHandler;
	
	int contextErrorHandlerId;	
	
	ExternalConflictHandler externalConflictHandler;
	int candidateIndex;
	
	int[] stateHistory;
	int stateHistoryIndex;
	int stateHistorySize;
		
	ContextErrorHandler[] externalHandlerHistory;
	int externalHandlerHistoryIndex;
	int externalHandlerHistorySize;
	
	ValidatingEEH(MessageWriter debugWriter){
		super(debugWriter);
		stateHistorySize = 1;
		stateHistoryIndex = 0;
		stateHistory = new int[stateHistorySize];
		stateHistory[stateHistoryIndex] = NONE;
		
		externalHandlerHistoryIndex = -1;
		externalHandlerHistorySize = 0;	
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, ValidatorErrorHandlerPool errorHandlerPool){
		super.init(pool, validationItemLocator);
		this.errorHandlerPool = errorHandlerPool;
	}
	
	void init(ContextErrorHandlerManager parent){		
		if(parent != null)parent.transmitState(this);
		else setValidation();//this is for the first root element
	}
	 //errorCatcherManager
	//--------------------------------------------------------------------------
	public void setNone(){
		contextErrorHandlerId = NONE;		
		storeState();
		contextErrorHandler = null;		
	}	
	public void setValidation(){
		contextErrorHandlerId = VALIDATION;		
		storeState();
		contextErrorHandler = validationErrorHandler;		
	}
	public void setConflict(ExternalConflictHandler externalConflictHandler, int candidateIndex){
		this.externalConflictHandler = externalConflictHandler;
		this.candidateIndex = candidateIndex;
		contextErrorHandlerId = CONFLICT;
		 storeState();
		contextErrorHandler = externalConflictErrorHandler;
	}
	public void setCommon(){
		throw new IllegalStateException();
	}
	public void setDefault(){		
		contextErrorHandlerId = DEFAULT;
		storeState();
		contextErrorHandler = defaultErrorHandler;
	}
	public void setExternal(ContextErrorHandler externalHandler){		
		contextErrorHandlerId = EXTERNAL;
		storeState(externalHandler);
		contextErrorHandler = externalHandler;
	}	
	public ContextErrorHandler getContextErrorHandler(){
		if(contextErrorHandler == null)setContextErrorHandler();
		return contextErrorHandler;
	}
	
	
	public void restorePreviousState(){
		contextErrorHandlerId = stateHistory[--stateHistoryIndex];
		if(contextErrorHandlerId == NONE){
			contextErrorHandler = null;		
		}else if(contextErrorHandlerId == VALIDATION){
			contextErrorHandler = validationErrorHandler;		
		}else if(contextErrorHandlerId == CONFLICT){
			contextErrorHandler = externalConflictErrorHandler;
		}else if(contextErrorHandlerId == COMMON){
			contextErrorHandler = commonErrorHandler;
		}else if(contextErrorHandlerId == DEFAULT){
			contextErrorHandler = defaultErrorHandler;
		}else if(contextErrorHandlerId == EXTERNAL){
			contextErrorHandler = externalHandlerHistory[--externalHandlerHistoryIndex];
		}else{
			throw new IllegalStateException();
		}	
	}
	
	public void transmitState(ContextErrorHandlerManager other){				
		if(contextErrorHandlerId == NONE){
			other.setNone();		
		}else if(contextErrorHandlerId == VALIDATION){
			other.setValidation();		
		}else if(contextErrorHandlerId == CONFLICT){
			other.setConflict(externalConflictHandler, candidateIndex);
		}else if(contextErrorHandlerId == COMMON){
			other.setCommon();
		}else if(contextErrorHandlerId == DEFAULT){
			other.setDefault();
		}else if(contextErrorHandlerId == EXTERNAL){
			other.setExternal(externalHandlerHistory[externalHandlerHistoryIndex]);
		}else{
			throw new IllegalStateException();
		}	
	}	
	
	
	//--------------------------------------------------------------------------
	
	protected void recycleErrorHandlers(){
		if(externalHandlerHistorySize > 0 ){					
			Arrays.fill(externalHandlerHistory, null);
			externalHandlerHistoryIndex = -1;
		}
		
		stateHistoryIndex = 0;
		contextErrorHandlerId = NONE;
		
		if(validationErrorHandler != null){
			validationErrorHandler.recycle();
			validationErrorHandler = null;
		}
		if(externalConflictErrorHandler != null){
			externalConflictErrorHandler.recycle();
			externalConflictErrorHandler = null;
		}
		if(commonErrorHandler != null){
			commonErrorHandler.recycle();
			commonErrorHandler = null;
		}
		if(defaultErrorHandler != null){
			defaultErrorHandler.recycle();
			defaultErrorHandler = null;
		}		
		externalConflictHandler = null;
		candidateIndex = 0;
	}
	
	
	protected void setContextErrorHandler(){
		if(contextErrorHandlerId == NONE){
			contextErrorHandler = null;		
		}else if(contextErrorHandlerId == VALIDATION){
			if(validationErrorHandler == null)validationErrorHandler = errorHandlerPool.getValidationErrorHandler();
			contextErrorHandler = validationErrorHandler;		
		}else if(contextErrorHandlerId == CONFLICT){
			if(externalConflictErrorHandler == null)externalConflictErrorHandler = errorHandlerPool.getExternalConflictErrorHandler(externalConflictHandler, candidateIndex);
			contextErrorHandler = externalConflictErrorHandler;
		}else if(contextErrorHandlerId == COMMON){
			if(commonErrorHandler == null)commonErrorHandler = errorHandlerPool.getCommonErrorHandler();
			contextErrorHandler = commonErrorHandler;
		}else if(contextErrorHandlerId == DEFAULT){
			if(defaultErrorHandler == null)defaultErrorHandler = errorHandlerPool.getDefaultErrorHandler();
			contextErrorHandler = defaultErrorHandler;
		}else if(contextErrorHandlerId == EXTERNAL){
			contextErrorHandler = externalHandlerHistory[externalHandlerHistoryIndex];
		}else{
			throw new IllegalStateException();
		}
	}
	
	protected void storeState(){		
		if(++stateHistoryIndex == stateHistorySize){
			int[] increased = new int[++stateHistorySize];
			System.arraycopy(stateHistory, 0, increased, 0, stateHistoryIndex);
			stateHistory = increased;
		}
		stateHistory[stateHistoryIndex] = contextErrorHandlerId;		
	}

	protected void storeState(ContextErrorHandler externalHandler){		
		storeState();
		if(externalHandlerHistorySize == 0){
			externalHandlerHistorySize = 1;
			externalHandlerHistoryIndex = 0;
			externalHandlerHistory = new ContextErrorHandler[externalHandlerHistorySize];						
		}else if(++externalHandlerHistoryIndex == externalHandlerHistorySize){
			ContextErrorHandler[] increased = new ContextErrorHandler[++externalHandlerHistorySize];
			System.arraycopy(externalHandlerHistory, 0, increased, 0, externalHandlerHistoryIndex);
			externalHandlerHistory = increased;
		}	
		externalHandlerHistory[externalHandlerHistoryIndex] = externalHandler;
	}
}