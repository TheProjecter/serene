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

package serene.validation.handlers.error;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.Reusable;

import sereneWrite.MessageWriter;

public class ValidatorErrorHandlerPool implements Reusable{
	
	ErrorDispatcher errorDispatcher;
	ActiveInputDescriptor activeInputDescriptor;
	
	
	// int validationErrorHCreated;
	int validationErrorHMaxSize;
	int validationErrorHFree;
	int validationErrorHMinFree;
	ValidationErrorHandler[] validationErrorH;
    
	// int conflictErrorHCreated;
	int conflictErrorHMaxSize;
	int conflictErrorHFree;
	int conflictErrorHMinFree;
	ExternalConflictErrorHandler[] conflictErrorH;
	
	// int commonErrorHCreated;
	int commonErrorHMaxSize;
	int commonErrorHFree;
	int commonErrorHMinFree;
	CommonErrorHandler[] commonErrorH;
	
	// int defaultErrorHCreated;
	int defaultErrorHMaxSize;
	int defaultErrorHFree;
	int defaultErrorHMinFree;
	DefaultErrorHandler[] defaultErrorH;
    
    // int startErrorHCreated;
	int startErrorHMaxSize;
	int startErrorHFree;
	int startErrorHMinFree;
	StartErrorHandler[] startErrorH;	
	
	boolean full;
	
	ErrorHandlerPool errorHandlerPool; 
	
	MessageWriter debugWriter;
	
	public ValidatorErrorHandlerPool(ErrorHandlerPool errorHandlerPool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
		this.errorHandlerPool = errorHandlerPool;
		
        validationErrorHMaxSize = 40;
        conflictErrorHMaxSize = 20;
        commonErrorHMaxSize = 20;
        defaultErrorHMaxSize = 20;
        startErrorHMaxSize = 10;
        
		full = false;
	}

	public void recycle(){
		if(full)releaseHandlers();
		errorHandlerPool.recycle(this);
	}
	
	public void init(ErrorDispatcher errorDispatcher, ActiveInputDescriptor activeInputDescriptor){
		this.errorDispatcher = errorDispatcher;
		this.activeInputDescriptor = activeInputDescriptor;
	}
	
	public void fill(){		
		errorHandlerPool.fill(this,
							validationErrorH,
							conflictErrorH,
							commonErrorH,
							defaultErrorH,
							startErrorH);
		
		full = true; 
	}
	
	void initFilled(int validationErrorHFillCount,
					int conflictErrorHFillCount,
					int commonErrorHFillCount,
					int defaultErrorHFillCount,
					int startErrorHFillCount){
		validationErrorHFree = validationErrorHFillCount;
		validationErrorHMinFree = validationErrorHFree;
		for(int i = 0; i < validationErrorHFree; i++){	
			validationErrorH[i].init(this, errorDispatcher, activeInputDescriptor);
		}
        		
		conflictErrorHFree = conflictErrorHFillCount;
		conflictErrorHMinFree = conflictErrorHFree;
		for(int i = 0; i < conflictErrorHFree; i++){	
			conflictErrorH[i].init(this, errorDispatcher, activeInputDescriptor);
		}

		
		commonErrorHFree = commonErrorHFillCount;
		commonErrorHMinFree = commonErrorHFree;
		for(int i = 0; i < commonErrorHFree; i++){	
			commonErrorH[i].init(this, errorDispatcher, activeInputDescriptor);
		}
		
				
		defaultErrorHFree = defaultErrorHFillCount;
		defaultErrorHMinFree = defaultErrorHFree;
		for(int i = 0; i < defaultErrorHFree; i++){	
			defaultErrorH[i].init(this, errorDispatcher, activeInputDescriptor);
		}
        
		
		startErrorHFree = startErrorHFillCount;
		startErrorHMinFree = startErrorHFree;
		for(int i = 0; i < startErrorHFree; i++){	
			startErrorH[i].init(this, errorDispatcher, activeInputDescriptor);
		}		
	}
	
	
	public void releaseHandlers(){
		// System.out.println("validation created " + validationErrorHCreated);		
		// System.out.println("transition created " + transitionErrorHCreated);
		// System.out.println("common created " + commonErrorHCreated);
		// System.out.println("conflict created " + conflictErrorHCreated);
		// System.out.println("default created " + defaultErrorHCreated);
		errorHandlerPool.recycle(validationErrorHFree,
		                        validationErrorHFree - validationErrorHMinFree,
								validationErrorH,
								conflictErrorHFree,
								conflictErrorHFree - conflictErrorHMinFree,
								conflictErrorH,
								commonErrorHFree,
								commonErrorHFree - commonErrorHMinFree,
								commonErrorH,
								defaultErrorHFree,
								defaultErrorHFree - defaultErrorHMinFree,
								defaultErrorH,
								startErrorHFree,
								startErrorHFree - startErrorHMinFree,
								startErrorH);
		full = false;
	}
	
	
	public ValidationErrorHandler getValidationErrorHandler(){				
		if(validationErrorHFree == 0){
			// validationErrorHCreated++;
			ValidationErrorHandler veh = new ValidationErrorHandler(debugWriter);			 
			veh.init(this, errorDispatcher, activeInputDescriptor);
			return veh;			
		}else{
			ValidationErrorHandler veh = validationErrorH[--validationErrorHFree];
			if(validationErrorHFree < validationErrorHMinFree) validationErrorHMinFree = validationErrorHFree;
			return veh;
		}		
	}
	
	
	public void recycle(ValidationErrorHandler veh){
		if(validationErrorHFree == validationErrorH.length){
		    if(validationErrorHFree == validationErrorHMaxSize) return;
			ValidationErrorHandler[] increased = new ValidationErrorHandler[10+validationErrorH.length];
			System.arraycopy(validationErrorH, 0, increased, 0, validationErrorHFree);
			validationErrorH = increased;
		}
		validationErrorH[validationErrorHFree++] = veh;
	}
    
	
	public ExternalConflictErrorHandler getExternalConflictErrorHandler(CandidatesConflictErrorHandler candidatesConflictErrorHandler, int candidateIndex, boolean isCandidate){
		if(conflictErrorHFree == 0){
			// conflictErrorHCreated++;
			ExternalConflictErrorHandler eh = new ExternalConflictErrorHandler(debugWriter);
			eh.init(this, errorDispatcher, activeInputDescriptor);
			eh.init(candidatesConflictErrorHandler, candidateIndex, isCandidate);
			return eh;			
		}else{
			ExternalConflictErrorHandler eh = conflictErrorH[--conflictErrorHFree];
			eh.init(candidatesConflictErrorHandler, candidateIndex, isCandidate);
			if(conflictErrorHFree < conflictErrorHMinFree) conflictErrorHMinFree = conflictErrorHFree;
			return eh;
		}		
	}
	
	public void recycle(ExternalConflictErrorHandler eh){
		if(conflictErrorHFree == conflictErrorH.length){
		    if(conflictErrorHFree == conflictErrorHMaxSize) return;
			ExternalConflictErrorHandler[] increased = new ExternalConflictErrorHandler[10+conflictErrorH.length];
			System.arraycopy(conflictErrorH, 0, increased, 0, conflictErrorHFree);
			conflictErrorH = increased;
		}
		conflictErrorH[conflictErrorHFree++] = eh;
	}
	
	
	public CommonErrorHandler getCommonErrorHandler(CandidatesConflictErrorHandler candidatesConflictErrorHandler, boolean isCandidate){
		if(commonErrorHFree == 0){
			// commonErrorHCreated++;
			CommonErrorHandler eh = new CommonErrorHandler(debugWriter);
			eh.init(this, errorDispatcher, activeInputDescriptor);
			eh.init(candidatesConflictErrorHandler, isCandidate);
			return eh;			
		}else{
			CommonErrorHandler eh = commonErrorH[--commonErrorHFree];
			eh.init(candidatesConflictErrorHandler, isCandidate);
			if(commonErrorHFree < commonErrorHMinFree) commonErrorHMinFree = commonErrorHFree;
			return eh;
		}		
	}
	
	public void recycle(CommonErrorHandler eh){
		if(commonErrorHFree == commonErrorH.length){
		    if(commonErrorHFree == commonErrorHMaxSize) return;
			CommonErrorHandler[] increased = new CommonErrorHandler[10+commonErrorH.length];
			System.arraycopy(commonErrorH, 0, increased, 0, commonErrorHFree);
			commonErrorH = increased;
		}
		commonErrorH[commonErrorHFree++] = eh;
	}
	
	public DefaultErrorHandler getDefaultErrorHandler(){				
		if(defaultErrorHFree == 0){
			// defaultErrorHCreated++;
			DefaultErrorHandler eh = new DefaultErrorHandler(debugWriter);
			eh.init(this, errorDispatcher, activeInputDescriptor);
			//eh.init();
			return eh;			
		}else{
			DefaultErrorHandler eh = defaultErrorH[--defaultErrorHFree];
			//eh.init();
			if(defaultErrorHFree < defaultErrorHMinFree) defaultErrorHMinFree = defaultErrorHFree;
			return eh;
		}		
	}
	
	public void recycle(DefaultErrorHandler eh){
		if(defaultErrorHFree == defaultErrorH.length){
		    if(defaultErrorHFree == defaultErrorHMaxSize) return;
			DefaultErrorHandler[] increased = new DefaultErrorHandler[10+defaultErrorH.length];
			System.arraycopy(defaultErrorH, 0, increased, 0, defaultErrorHFree);
			defaultErrorH = increased;
		}
		defaultErrorH[defaultErrorHFree++] = eh;
	}
    
    public StartErrorHandler getStartErrorHandler(){				
		if(startErrorHFree == 0){
			// startErrorHCreated++;
			StartErrorHandler eh = new StartErrorHandler(debugWriter);
			eh.init(this, errorDispatcher, activeInputDescriptor);
			//eh.init();
			return eh;			
		}else{
			StartErrorHandler eh = startErrorH[--startErrorHFree];
			//eh.init();
			if(startErrorHFree < startErrorHMinFree) startErrorHMinFree = startErrorHFree;
			return eh;
		}		
	}
	
	public void recycle(StartErrorHandler eh){
		if(startErrorHFree == startErrorH.length){
		    if(startErrorHFree == startErrorHMaxSize) return;
			StartErrorHandler[] increased = new StartErrorHandler[10+startErrorH.length];
			System.arraycopy(startErrorH, 0, increased, 0, startErrorHFree);
			startErrorH = increased;
		}
		startErrorH[startErrorHFree++] = eh;
	}	
} 