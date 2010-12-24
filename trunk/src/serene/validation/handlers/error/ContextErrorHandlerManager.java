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

public interface ContextErrorHandlerManager{
	public static final int NONE = 0;
	public static final int VALIDATION = 1;
	public static final int CONFLICT = 2;
	public static final int DEFAULT = 3;
	public static final int COMMON = 4;
	public static final int EXTERNAL = 4;
	
	void setNone();
	void setValidation();
	void setConflict(ExternalConflictHandler conflictHandler, int candidateIndex);
	void setCommon();	
	void setDefault();
	void setExternal(ContextErrorHandler contextErrorHandler);
	
	void restorePreviousState();	
	void transmitState(ContextErrorHandlerManager other);
	
	ContextErrorHandler getContextErrorHandler();	
}