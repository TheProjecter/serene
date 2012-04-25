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

public abstract class ErrorHandlerPool{	
	ErrorHandlerPool(){}
	
	
	public abstract ValidatorErrorHandlerPool getValidatorErrorHandlerPool();		
	public abstract void recycle(ValidatorErrorHandlerPool vehp);
	
	abstract void fill(ValidatorErrorHandlerPool pool,
						ValidationErrorHandler[] validationErrorHToFill,
						ExternalConflictErrorHandler[] conflictErrorHToFill,
						CommonErrorHandler[] commonErrorHToFill,
						DefaultErrorHandler[] defaultErrorHToFill,
						StartErrorHandler[] startErrorHToFill);
		
	abstract void recycle(int validationErrorHRecycledCount,
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
							StartErrorHandler[] startErrorHRecycled);
} 