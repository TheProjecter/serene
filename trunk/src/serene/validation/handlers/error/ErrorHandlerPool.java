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

import sereneWrite.MessageWriter;

public class ErrorHandlerPool{
	
	private static volatile ErrorHandlerPool instance;
	
	int vehPoolFree; 
	int vehPoolPoolSize;
	ValidatorErrorHandlerPool[] vehPools;
	
	int validationErrorHAverageUse;
	int validationErrorHPoolSize;
	int validationErrorHFree;
	ValidationErrorHandler[] validationErrorH;
    
    int startValidationErrorHAverageUse;
	int startValidationErrorHPoolSize;
	int startValidationErrorHFree;
	StartValidationErrorHandler[] startValidationErrorH;
	
	int conflictErrorHAverageUse;
	int conflictErrorHPoolSize;
	int conflictErrorHFree;
	ExternalConflictErrorHandler[] conflictErrorH;
	
	int commonErrorHAverageUse;
	int commonErrorHPoolSize;
	int commonErrorHFree;
	CommonErrorHandler[] commonErrorH;
	
	int defaultErrorHAverageUse;
	int defaultErrorHPoolSize;
	int defaultErrorHFree;
	DefaultErrorHandler[] defaultErrorH;
	
	int attributeConflictErrorHAverageUse;
	int attributeConflictErrorHPoolSize;
	int attributeConflictErrorHFree;
	AttributeConflictErrorHandler[] attributeConflictErrorH;
	
	final int UNUSED = 0;
	
	MessageWriter debugWriter;
	
	private ErrorHandlerPool(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		vehPoolFree = 0;
		vehPoolPoolSize = 10;
		vehPools = new ValidatorErrorHandlerPool[vehPoolPoolSize];
		
		validationErrorHAverageUse = UNUSED;
		validationErrorHPoolSize = 10;
		validationErrorHFree = 0;
		validationErrorH = new ValidationErrorHandler[validationErrorHPoolSize];
        
        startValidationErrorHAverageUse = UNUSED;
		startValidationErrorHPoolSize = 10;
		startValidationErrorHFree = 0;
		startValidationErrorH = new StartValidationErrorHandler[startValidationErrorHPoolSize];
		
		conflictErrorHAverageUse = UNUSED;
		conflictErrorHPoolSize = 10;
		conflictErrorHFree = 0;
		conflictErrorH = new ExternalConflictErrorHandler[conflictErrorHPoolSize];

		commonErrorHAverageUse = UNUSED;
		commonErrorHPoolSize = 10;
		commonErrorHFree = 0;
		commonErrorH = new CommonErrorHandler[commonErrorHPoolSize];
		
		defaultErrorHAverageUse = UNUSED;
		defaultErrorHPoolSize = 10;
		defaultErrorHFree = 0;
		defaultErrorH = new DefaultErrorHandler[defaultErrorHPoolSize];
		
		attributeConflictErrorHAverageUse = UNUSED;
		attributeConflictErrorHPoolSize = 10;
		attributeConflictErrorHFree = 0;
		attributeConflictErrorH = new AttributeConflictErrorHandler[attributeConflictErrorHPoolSize];
	}
	
	public static ErrorHandlerPool getInstance(MessageWriter debugWriter){
		if(instance == null){
			synchronized(ErrorHandlerPool.class){
				if(instance == null){
					instance = new ErrorHandlerPool(debugWriter); 
				}
			}
		}
		return instance;
	}
	
	public synchronized ValidatorErrorHandlerPool getValidatorErrorHandlerPool(){
		if(vehPoolFree == 0){
			ValidatorErrorHandlerPool vehp = new ValidatorErrorHandlerPool(this, debugWriter);
			return vehp;
		}else{
			ValidatorErrorHandlerPool vehp = vehPools[--vehPoolFree];
			return vehp;
		}
	}
		
	public synchronized void recycle(ValidatorErrorHandlerPool vehp){
		if(vehPoolFree == vehPoolPoolSize){
			ValidatorErrorHandlerPool[] increased = new ValidatorErrorHandlerPool[++vehPoolPoolSize];
			System.arraycopy(vehPools, 0, increased, 0, vehPoolFree);
			vehPools = increased;
		}
		vehPools[vehPoolFree++] = vehp;
	}	
	
	synchronized void fill(ValidatorErrorHandlerPool pool,
						ValidationErrorHandler[] validationErrorH,
                        StartValidationErrorHandler[] startValidationErrorH,
						ExternalConflictErrorHandler[] conflictErrorH,
						CommonErrorHandler[] commonErrorH,
						DefaultErrorHandler[] defaultErrorH,
						AttributeConflictErrorHandler[] attributeConflictErrorH){
		int validationErrorHFillCount;	
		if(validationErrorH == null || validationErrorH.length < validationErrorHAverageUse)
			validationErrorH = new ValidationErrorHandler[validationErrorHAverageUse];
		if(validationErrorHFree > validationErrorHAverageUse){
			validationErrorHFillCount = validationErrorHAverageUse;
			validationErrorHFree = validationErrorHFree - validationErrorHAverageUse;
		}else{
			validationErrorHFillCount = validationErrorHFree;
			validationErrorHFree = 0;
		}
		System.arraycopy(this.validationErrorH, validationErrorHFree, 
							validationErrorH, 0, validationErrorHFillCount);
        
        
        int startValidationErrorHFillCount;	
		if(startValidationErrorH == null || startValidationErrorH.length < startValidationErrorHAverageUse)
			startValidationErrorH = new StartValidationErrorHandler[startValidationErrorHAverageUse];
		if(startValidationErrorHFree > startValidationErrorHAverageUse){
			startValidationErrorHFillCount = startValidationErrorHAverageUse;
			startValidationErrorHFree = startValidationErrorHFree - startValidationErrorHAverageUse;
		}else{
			startValidationErrorHFillCount = startValidationErrorHFree;
			startValidationErrorHFree = 0;
		}
		System.arraycopy(this.startValidationErrorH, startValidationErrorHFree, 
							startValidationErrorH, 0, startValidationErrorHFillCount);
		
        
		int conflictErrorHFillCount;	
		if(conflictErrorH == null || conflictErrorH.length < conflictErrorHAverageUse)
			conflictErrorH = new ExternalConflictErrorHandler[conflictErrorHAverageUse];
		if(conflictErrorHFree > conflictErrorHAverageUse){
			conflictErrorHFillCount = conflictErrorHAverageUse;
			conflictErrorHFree = conflictErrorHFree - conflictErrorHAverageUse;
		}else{
			conflictErrorHFillCount = conflictErrorHFree;
			conflictErrorHFree = 0;
		}
		System.arraycopy(this.conflictErrorH, conflictErrorHFree, 
							conflictErrorH, 0, conflictErrorHFillCount);
				
		int commonErrorHFillCount;	
		if(commonErrorH == null || commonErrorH.length < commonErrorHAverageUse)
			commonErrorH = new CommonErrorHandler[commonErrorHAverageUse];
		if(commonErrorHFree > commonErrorHAverageUse){
			commonErrorHFillCount = commonErrorHAverageUse;
			commonErrorHFree = commonErrorHFree - commonErrorHAverageUse;
		}else{
			commonErrorHFillCount = commonErrorHFree;
			commonErrorHFree = 0;
		}
		System.arraycopy(this.commonErrorH, commonErrorHFree, 
							commonErrorH, 0, commonErrorHFillCount);
		
		int defaultErrorHFillCount;	
		if(defaultErrorH == null || defaultErrorH.length < defaultErrorHAverageUse)
			defaultErrorH = new DefaultErrorHandler[defaultErrorHAverageUse];
		if(defaultErrorHFree > defaultErrorHAverageUse){
			defaultErrorHFillCount = defaultErrorHAverageUse;
			defaultErrorHFree = defaultErrorHFree - defaultErrorHAverageUse;
		}else{
			defaultErrorHFillCount = defaultErrorHFree;
			defaultErrorHFree = 0;
		}
		System.arraycopy(this.defaultErrorH, defaultErrorHFree, 
							defaultErrorH, 0, defaultErrorHFillCount);
		
		int attributeConflictErrorHFillCount;	
		if(attributeConflictErrorH == null || attributeConflictErrorH.length < attributeConflictErrorHAverageUse)
			attributeConflictErrorH = new AttributeConflictErrorHandler[attributeConflictErrorHAverageUse];
		if(attributeConflictErrorHFree > attributeConflictErrorHAverageUse){
			attributeConflictErrorHFillCount = attributeConflictErrorHAverageUse;
			attributeConflictErrorHFree = attributeConflictErrorHFree - attributeConflictErrorHAverageUse;
		}else{
			attributeConflictErrorHFillCount = attributeConflictErrorHFree;
			attributeConflictErrorHFree = 0;
		}
		System.arraycopy(this.attributeConflictErrorH, attributeConflictErrorHFree, 
							attributeConflictErrorH, 0, attributeConflictErrorHFillCount);
		
		pool.setHandlers(validationErrorHFillCount,
						validationErrorH,
                        startValidationErrorHFillCount,
						startValidationErrorH,
						conflictErrorHFillCount,
						conflictErrorH,
						commonErrorHFillCount,
						commonErrorH,
						defaultErrorHFillCount,
						defaultErrorH,
						attributeConflictErrorHFillCount,
						attributeConflictErrorH);
	}
		
	synchronized void recycle(int validationErrorHAverageUse,
							ValidationErrorHandler[] validationErrorH,
                            int startValidationErrorHAverageUse,
							StartValidationErrorHandler[] startValidationErrorH,
							int conflictErrorHAverageUse,
							ExternalConflictErrorHandler[] conflictErrorH,
							int commonErrorHAverageUse,
							CommonErrorHandler[] commonErrorH,
							int defaultErrorHAverageUse,
							DefaultErrorHandler[] defaultErrorH,
							int attributeConflictErrorHAverageUse,
							AttributeConflictErrorHandler[] attributeConflictErrorH){
		if(validationErrorHFree + validationErrorHAverageUse >= validationErrorHPoolSize){			 
			validationErrorHPoolSize+= validationErrorHAverageUse;
			ValidationErrorHandler[] increased = new ValidationErrorHandler[validationErrorHPoolSize];
			System.arraycopy(this.validationErrorH, 0, increased, 0, validationErrorHFree);
			this.validationErrorH = increased;
		}
		System.arraycopy(validationErrorH, 0, this.validationErrorH, validationErrorHFree, validationErrorHAverageUse);
		validationErrorHFree += validationErrorHAverageUse;
		if(this.validationErrorHAverageUse != 0)this.validationErrorHAverageUse = (this.validationErrorHAverageUse + validationErrorHAverageUse)/2;
		else this.validationErrorHAverageUse = validationErrorHAverageUse;
		// System.out.println("validationErrorH "+this.validationErrorHAverageUse);
        
        
        if(startValidationErrorHFree + startValidationErrorHAverageUse >= startValidationErrorHPoolSize){			 
			startValidationErrorHPoolSize+= startValidationErrorHAverageUse;
			StartValidationErrorHandler[] increased = new StartValidationErrorHandler[startValidationErrorHPoolSize];
			System.arraycopy(this.startValidationErrorH, 0, increased, 0, startValidationErrorHFree);
			this.startValidationErrorH = increased;
		}
		System.arraycopy(startValidationErrorH, 0, this.startValidationErrorH, startValidationErrorHFree, startValidationErrorHAverageUse);
		startValidationErrorHFree += startValidationErrorHAverageUse;
		if(this.startValidationErrorHAverageUse != 0)this.startValidationErrorHAverageUse = (this.startValidationErrorHAverageUse + startValidationErrorHAverageUse)/2;
		else this.startValidationErrorHAverageUse = startValidationErrorHAverageUse;
		// System.out.println("startValidationErrorH "+this.startValidationErrorHAverageUse);
        
				
		if(commonErrorHFree + commonErrorHAverageUse >= commonErrorHPoolSize){			 
			commonErrorHPoolSize+= commonErrorHAverageUse;
			CommonErrorHandler[] increased = new CommonErrorHandler[commonErrorHPoolSize];
			System.arraycopy(this.commonErrorH, 0, increased, 0, commonErrorHFree);
			this.commonErrorH = increased;
		}
		System.arraycopy(commonErrorH, 0, this.commonErrorH, commonErrorHFree, commonErrorHAverageUse);
		commonErrorHFree += commonErrorHAverageUse;
		if(this.commonErrorHAverageUse != 0)this.commonErrorHAverageUse = (this.commonErrorHAverageUse + commonErrorHAverageUse)/2;
		else this.commonErrorHAverageUse = commonErrorHAverageUse;
		// System.out.println("commonErrorH "+this.commonErrorHAverageUse);
		
		if(conflictErrorHFree + conflictErrorHAverageUse >= conflictErrorHPoolSize){			 
			conflictErrorHPoolSize+= conflictErrorHAverageUse;
			ExternalConflictErrorHandler[] increased = new ExternalConflictErrorHandler[conflictErrorHPoolSize];
			System.arraycopy(this.conflictErrorH, 0, increased, 0, conflictErrorHFree);
			this.conflictErrorH = increased;
		}
		System.arraycopy(conflictErrorH, 0, this.conflictErrorH, conflictErrorHFree, conflictErrorHAverageUse);
		conflictErrorHFree += conflictErrorHAverageUse;
		if(this.conflictErrorHAverageUse != 0)this.conflictErrorHAverageUse = (this.conflictErrorHAverageUse + conflictErrorHAverageUse)/2;
		else this.conflictErrorHAverageUse = conflictErrorHAverageUse;
		// System.out.println("conflictErrorH "+this.conflictErrorHAverageUse);
		
		if(defaultErrorHFree + defaultErrorHAverageUse >= defaultErrorHPoolSize){			 
			defaultErrorHPoolSize+= defaultErrorHAverageUse;
			DefaultErrorHandler[] increased = new DefaultErrorHandler[defaultErrorHPoolSize];
			System.arraycopy(this.defaultErrorH, 0, increased, 0, defaultErrorHFree);
			this.defaultErrorH = increased;
		}
		System.arraycopy(defaultErrorH, 0, this.defaultErrorH, defaultErrorHFree, defaultErrorHAverageUse);
		defaultErrorHFree += defaultErrorHAverageUse;
		if(this.defaultErrorHAverageUse != 0)this.defaultErrorHAverageUse = (this.defaultErrorHAverageUse + defaultErrorHAverageUse)/2;
		else this.defaultErrorHAverageUse = defaultErrorHAverageUse;
		// System.out.println("defaultErrorH "+this.defaultErrorHAverageUse);
		
		if(attributeConflictErrorHFree + attributeConflictErrorHAverageUse >= attributeConflictErrorHPoolSize){			 
			attributeConflictErrorHPoolSize+= attributeConflictErrorHAverageUse;
			AttributeConflictErrorHandler[] increased = new AttributeConflictErrorHandler[attributeConflictErrorHPoolSize];
			System.arraycopy(this.attributeConflictErrorH, 0, increased, 0, attributeConflictErrorHFree);
			this.attributeConflictErrorH = increased;
		}
		System.arraycopy(attributeConflictErrorH, 0, this.attributeConflictErrorH, attributeConflictErrorHFree, attributeConflictErrorHAverageUse);
		attributeConflictErrorHFree += attributeConflictErrorHAverageUse;
		if(this.attributeConflictErrorHAverageUse != 0)this.attributeConflictErrorHAverageUse = (this.attributeConflictErrorHAverageUse + attributeConflictErrorHAverageUse)/2;
		else this.attributeConflictErrorHAverageUse = attributeConflictErrorHAverageUse;
		// System.out.println("attributeConflictErrorH "+this.attributeConflictErrorHAverageUse);
	}
} 