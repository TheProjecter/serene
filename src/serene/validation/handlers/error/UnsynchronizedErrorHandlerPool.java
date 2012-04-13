/*
Copyright 2012 Radu Cernuta 

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

public class UnsynchronizedErrorHandlerPool extends ErrorHandlerPool{	
	UnsynchronizedErrorHandlerPool(){
		super();
	}
	
	public static UnsynchronizedErrorHandlerPool getInstance(){
		return new UnsynchronizedErrorHandlerPool();
	}
	
	public ValidatorErrorHandlerPool getValidatorErrorHandlerPool(){
		return new ValidatorErrorHandlerPool(null);
	}		
	public void recycle(ValidatorErrorHandlerPool vehp){
	    throw new IllegalStateException();
	}	
	
	void fill(ValidatorErrorHandlerPool pool,
						ValidationErrorHandler[] validationErrorHToFill,
						ExternalConflictErrorHandler[] conflictErrorHToFill,
						CommonErrorHandler[] commonErrorHToFill,
						DefaultErrorHandler[] defaultErrorHToFill,
						StartErrorHandler[] startErrorHToFill){
	    throw new IllegalStateException();
	}	
		
	void recycle(int validationErrorHRecycledCount,
	                        int validationErrorHEffectivellyUsed,
							ValidationErrorHandler[] validationErrorHRecycled,
							int conflictErrorHRecycledCount,
							int conflictErrorHEffectivellyUsed,
							ExternalConflictErrorHandler[] conflictErrorHRecycled,
							int commonErrorHRecycledCount,
							int commonErrorHEffectivellyUsed,
							CommonErrorHandler[] commonErrorHRecycled,
							int defaultErrorHRecycledCount,
							int defaultErrorHEffectivellyUsed,
							DefaultErrorHandler[] defaultErrorHRecycled,
							int startErrorHRecycledCount,
							int startErrorHEffectivellyUsed,
							StartErrorHandler[] startErrorHRecycled){
	    throw new IllegalStateException();
	}	
} 
