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
import serene.validation.handlers.error.CandidatesConflictErrorHandler;
import serene.validation.handlers.error.CommonErrorHandler;
import serene.validation.handlers.error.DefaultErrorHandler;
import serene.validation.handlers.error.ContextErrorHandlerManager;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

abstract class ValidatingEEH extends ComparableEEH implements ContextErrorHandlerManager{

	ValidatorErrorHandlerPool errorHandlerPool;
	
	ContextErrorHandler[] contextErrorHandler;
	
	int contextErrorHandlerIndex;	

    boolean isCandidate; //true when this is a direct candidate, false when it is a descendant of one, or simply not involved in a conflict
	int candidateIndex;
	CandidatesConflictErrorHandler candidatesConflictErrorHandler;
    
	int[] stateHistory;
	int stateHistoryIndex;
	int stateHistorySize;
		
	
	ValidatingEEH(MessageWriter debugWriter){
		super(debugWriter);
        contextErrorHandler = new ContextErrorHandler[HANDLER_COUNT];
        
		stateHistorySize = 1;
		stateHistoryIndex = 0;
		stateHistory = new int[stateHistorySize];		
        contextErrorHandlerIndex = VALIDATION;
        storeState();
        
        isCandidate = false;
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, ValidatorErrorHandlerPool errorHandlerPool){
		super.init(pool, validationItemLocator);
		this.errorHandlerPool = errorHandlerPool;
	}
	
	void init(ContextErrorHandlerManager parent){
        if(parent != null){
            candidateIndex = parent.getCandidateIndex();
            candidatesConflictErrorHandler = parent.getCandidatesConflictErrorHandler();
            contextErrorHandlerIndex = parent.getContextErrorHandlerIndex();
            storeState();
        }
	}
    
	 //errorCatcherManager
	//--------------------------------------------------------------------------
    public void setCandidate(boolean isCandidate){        
		this.isCandidate = isCandidate;
    }
    public boolean isCandidate(){        
		return isCandidate;
    }   
    public void setCandidateIndex(int candidateIndex){        
		this.candidateIndex = candidateIndex;
    }
    public int getCandidateIndex(){        
		return candidateIndex;
    }
    public void setCandidatesConflictErrorHandler(CandidatesConflictErrorHandler candidatesConflictErrorHandler){
        this.candidatesConflictErrorHandler = candidatesConflictErrorHandler;
    }
    public CandidatesConflictErrorHandler getCandidatesConflictErrorHandler(){
        return candidatesConflictErrorHandler;
    }
    
    public void setContextErrorHandlerIndex(int contextErrorHandlerIndex){
        if(contextErrorHandlerIndex <= NONE 
            || contextErrorHandlerIndex >= HANDLER_COUNT)throw new IllegalArgumentException();
        this.contextErrorHandlerIndex = contextErrorHandlerIndex;
        storeState();
    }
    public int getContextErrorHandlerIndex(){
        return contextErrorHandlerIndex;
    }
    
    public ContextErrorHandler getContextErrorHandler(){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		return contextErrorHandler[contextErrorHandlerIndex];
	}
   	
	public void restorePreviousHandler(){
        if(--stateHistoryIndex < 0)throw new IllegalStateException();        
		contextErrorHandlerIndex = stateHistory[stateHistoryIndex];
	}
				
	//--------------------------------------------------------------------------
	
	protected void resetContextErrorHandlerManager(){		
		stateHistoryIndex = 0;
		contextErrorHandlerIndex = VALIDATION;		
	
        for(int i = 0; i < HANDLER_COUNT; i++){
            if(contextErrorHandler[i] != null){
                contextErrorHandler[i].recycle();
                contextErrorHandler[i] = null;
            }
        }		
		candidateIndex = -1;
        candidatesConflictErrorHandler  = null;
        isCandidate = false;
	}
	
	
	protected void setContextErrorHandler(){
		if(contextErrorHandlerIndex == NONE){
			contextErrorHandler = null;		
		}else if(contextErrorHandlerIndex == VALIDATION){
			if(contextErrorHandler[VALIDATION] == null)contextErrorHandler[VALIDATION] = errorHandlerPool.getValidationErrorHandler();
		}else if(contextErrorHandlerIndex == CONFLICT){
			if(contextErrorHandler[CONFLICT] == null)contextErrorHandler[CONFLICT] = errorHandlerPool.getExternalConflictErrorHandler(candidatesConflictErrorHandler, candidateIndex, isCandidate);
		}else if(contextErrorHandlerIndex == COMMON){
			if(contextErrorHandler[COMMON] == null)contextErrorHandler[COMMON] = errorHandlerPool.getCommonErrorHandler(candidatesConflictErrorHandler, isCandidate);
		}else if(contextErrorHandlerIndex == DEFAULT){
			if(contextErrorHandler[DEFAULT] == null)contextErrorHandler[DEFAULT] = errorHandlerPool.getDefaultErrorHandler();
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
		stateHistory[stateHistoryIndex] = contextErrorHandlerIndex;		
	}
}