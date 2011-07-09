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

import serene.Reusable;

import sereneWrite.MessageWriter;

public class ValidatorErrorHandlerPool implements Reusable{
	
	ErrorDispatcher errorDispatcher;
	
	// int validationErrorHCreated;
	int validationErrorHPoolSize;
	int validationErrorHFree = 0;
	ValidationErrorHandler[] validationErrorH;
    
	// int conflictErrorHCreated;
	int conflictErrorHPoolSize;
	int conflictErrorHFree = 0;
	ExternalConflictErrorHandler[] conflictErrorH;
	
	// int commonErrorHCreated;
	int commonErrorHPoolSize;
	int commonErrorHFree = 0;
	CommonErrorHandler[] commonErrorH;
	
	// int defaultErrorHCreated;
	int defaultErrorHPoolSize;
	int defaultErrorHFree = 0;
	DefaultErrorHandler[] defaultErrorH;
	
	// int attributeConflictErrorHCreated;
	int attributeConflictErrorHPoolSize;
	int attributeConflictErrorHFree = 0;
	AttributeConflictErrorHandler[] attributeConflictErrorH;
	
	ErrorHandlerPool errorHandlerPool; 
	
	MessageWriter debugWriter;
	
	public ValidatorErrorHandlerPool(ErrorHandlerPool errorHandlerPool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
		this.errorHandlerPool = errorHandlerPool;
	}

	public void recycle(){
		if(validationErrorHFree != 0 ||
			conflictErrorHFree != 0 ||
			commonErrorHFree != 0 ||
			defaultErrorHFree != 0)
		releaseHandlers();
		errorHandlerPool.recycle(this);
	}
	public void fill(ErrorDispatcher errorDispatcher){
		this.errorDispatcher = errorDispatcher;
		
		errorHandlerPool.fill(this,
							validationErrorH,
							conflictErrorH,
							commonErrorH,
							defaultErrorH,
							attributeConflictErrorH);
	}
	
	void setHandlers(int validationErrorHFree,
					ValidationErrorHandler[] validationErrorH,
					int conflictErrorHFree,
					ExternalConflictErrorHandler[] conflictErrorH,
					int commonErrorHFree,
					CommonErrorHandler[] commonErrorH,
					int defaultErrorHFree,
					DefaultErrorHandler[] defaultErrorH,
					int attributeConflictErrorHFree,
					AttributeConflictErrorHandler[] attributeConflictErrorH){
		validationErrorHPoolSize = validationErrorH.length;
		this.validationErrorHFree = validationErrorHFree;
		this.validationErrorH = validationErrorH;
		for(int i = 0; i < validationErrorHFree; i++){	
			validationErrorH[i].init(this, errorDispatcher);
		}
        		
		conflictErrorHPoolSize = conflictErrorH.length;
		this.conflictErrorHFree = conflictErrorHFree;
		this.conflictErrorH = conflictErrorH;
		for(int i = 0; i < conflictErrorHFree; i++){	
			conflictErrorH[i].init(this, errorDispatcher);
		}

		commonErrorHPoolSize = commonErrorH.length;
		this.commonErrorHFree = commonErrorHFree;
		this.commonErrorH = commonErrorH;
		for(int i = 0; i < commonErrorHFree; i++){	
			commonErrorH[i].init(this, errorDispatcher);
		}
		
		conflictErrorHPoolSize = conflictErrorH.length;
		this.conflictErrorHFree = conflictErrorHFree;
		this.conflictErrorH = conflictErrorH;
		for(int i = 0; i < conflictErrorHFree; i++){	
			conflictErrorH[i].init(this, errorDispatcher);
		}
		
		defaultErrorHPoolSize = defaultErrorH.length;
		this.defaultErrorHFree = defaultErrorHFree;
		this.defaultErrorH = defaultErrorH;
		for(int i = 0; i < defaultErrorHFree; i++){	
			defaultErrorH[i].init(this, errorDispatcher);
		}
		
		attributeConflictErrorHPoolSize = attributeConflictErrorH.length;
		this.attributeConflictErrorHFree = attributeConflictErrorHFree;
		this.attributeConflictErrorH = attributeConflictErrorH;
		for(int i = 0; i < attributeConflictErrorHFree; i++){	
			attributeConflictErrorH[i].init(this);
		}
	}
	
	
	public void releaseHandlers(){
		// System.out.println("validation created " + validationErrorHCreated);		
		// System.out.println("transition created " + transitionErrorHCreated);
		// System.out.println("common created " + commonErrorHCreated);
		// System.out.println("conflict created " + conflictErrorHCreated);
		// System.out.println("default created " + defaultErrorHCreated);
		errorHandlerPool.recycle(validationErrorHFree,
								validationErrorH,
								conflictErrorHFree,
								conflictErrorH,
								commonErrorHFree,
								commonErrorH,
								defaultErrorHFree,
								defaultErrorH,
								attributeConflictErrorHFree,
								attributeConflictErrorH);
		validationErrorHFree = 0;
		conflictErrorHFree = 0;
		commonErrorHFree = 0;
		defaultErrorHFree = 0;
		attributeConflictErrorHFree = 0;
	}
	
	
	public ValidationErrorHandler getValidationErrorHandler(){				
		if(validationErrorHFree == 0){
			// validationErrorHCreated++;
			ValidationErrorHandler veh = new ValidationErrorHandler(debugWriter);			 
			veh.init(this, errorDispatcher);
			return veh;			
		}else{
			ValidationErrorHandler veh = validationErrorH[--validationErrorHFree];
			return veh;
		}		
	}
	
	
	public void recycle(ValidationErrorHandler veh){
		if(validationErrorHFree == validationErrorHPoolSize){
			ValidationErrorHandler[] increased = new ValidationErrorHandler[++validationErrorHPoolSize];
			System.arraycopy(validationErrorH, 0, increased, 0, validationErrorHFree);
			validationErrorH = increased;
		}
		validationErrorH[validationErrorHFree++] = veh;
	}
    
	
	public ExternalConflictErrorHandler getExternalConflictErrorHandler(CandidatesConflictErrorHandler candidatesConflictErrorHandler, int candidateIndex, boolean isCandidate){
		if(conflictErrorHFree == 0){
			// conflictErrorHCreated++;
			ExternalConflictErrorHandler eh = new ExternalConflictErrorHandler(debugWriter);
			eh.init(this, errorDispatcher);
			eh.init(candidatesConflictErrorHandler, candidateIndex, isCandidate);
			return eh;			
		}else{
			ExternalConflictErrorHandler eh = conflictErrorH[--conflictErrorHFree];
			eh.init(candidatesConflictErrorHandler, candidateIndex, isCandidate);
			return eh;
		}		
	}
	
	public void recycle(ExternalConflictErrorHandler eh){
		if(conflictErrorHFree == conflictErrorHPoolSize){
			ExternalConflictErrorHandler[] increased = new ExternalConflictErrorHandler[++conflictErrorHPoolSize];
			System.arraycopy(conflictErrorH, 0, increased, 0, conflictErrorHFree);
			conflictErrorH = increased;
		}
		conflictErrorH[conflictErrorHFree++] = eh;
	}
	
	
	public CommonErrorHandler getCommonErrorHandler(CandidatesConflictErrorHandler candidatesConflictErrorHandler, boolean isCandidate){
		if(commonErrorHFree == 0){
			// commonErrorHCreated++;
			CommonErrorHandler eh = new CommonErrorHandler(debugWriter);
			eh.init(this, errorDispatcher);
			eh.init(candidatesConflictErrorHandler, isCandidate);
			return eh;			
		}else{
			CommonErrorHandler eh = commonErrorH[--commonErrorHFree];
			eh.init(candidatesConflictErrorHandler, isCandidate);
			return eh;
		}		
	}
	
	public void recycle(CommonErrorHandler eh){
		if(commonErrorHFree == commonErrorHPoolSize){
			CommonErrorHandler[] increased = new CommonErrorHandler[++commonErrorHPoolSize];
			System.arraycopy(commonErrorH, 0, increased, 0, commonErrorHFree);
			commonErrorH = increased;
		}
		commonErrorH[commonErrorHFree++] = eh;
	}
	
	public DefaultErrorHandler getDefaultErrorHandler(){				
		if(defaultErrorHFree == 0){
			// defaultErrorHCreated++;
			DefaultErrorHandler eh = new DefaultErrorHandler(debugWriter);
			eh.init(this, errorDispatcher);
			//eh.init();
			return eh;			
		}else{
			DefaultErrorHandler eh = defaultErrorH[--defaultErrorHFree];
			//eh.init();
			return eh;
		}		
	}
	
	public void recycle(DefaultErrorHandler eh){
		if(defaultErrorHFree == defaultErrorHPoolSize){
			DefaultErrorHandler[] increased = new DefaultErrorHandler[++defaultErrorHPoolSize];
			System.arraycopy(defaultErrorH, 0, increased, 0, defaultErrorHFree);
			defaultErrorH = increased;
		}
		defaultErrorH[defaultErrorHFree++] = eh;
	}

	public AttributeConflictErrorHandler getAttributeConflictErrorHandler(ExternalConflictHandler attributeConflictHandler, int candidateIndex){
		if(attributeConflictErrorHFree == 0){
			// attributeConflictErrorHCreated++;
			AttributeConflictErrorHandler eh = new AttributeConflictErrorHandler(debugWriter);
			eh.init(this);
			eh.init(attributeConflictHandler, candidateIndex);
			return eh;			
		}else{
			AttributeConflictErrorHandler eh = attributeConflictErrorH[--attributeConflictErrorHFree];
			eh.init(attributeConflictHandler, candidateIndex);
			return eh;
		}		
	}
	
	public void recycle(AttributeConflictErrorHandler eh){
		if(attributeConflictErrorHFree == attributeConflictErrorHPoolSize){
			AttributeConflictErrorHandler[] increased = new AttributeConflictErrorHandler[++attributeConflictErrorHPoolSize];
			System.arraycopy(attributeConflictErrorH, 0, increased, 0, attributeConflictErrorHFree);
			attributeConflictErrorH = increased;
		}
		attributeConflictErrorH[attributeConflictErrorHFree++] = eh;
	}
} 