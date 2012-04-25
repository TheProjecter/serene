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

public interface ContextErrorHandlerManager{
	int NONE = -1;
	int VALIDATION = 0;
    int COMMON = 1;
	int CONFLICT = 2;
	int DEFAULT = 3;	
    int HANDLER_COUNT = 4;
    
	
    void setCandidate(boolean isCandidate);
    boolean isCandidate();
    void setCandidateIndex(int candidateIndex);
    int getCandidateIndex();
    void setCandidatesConflictErrorHandler(CandidatesConflictErrorHandler candidatesConflictErrorHandler);
    CandidatesConflictErrorHandler getCandidatesConflictErrorHandler();
    
    void setContextErrorHandlerIndex(int contextErrorHandlerIndex);
    int getContextErrorHandlerIndex();
    
	void restorePreviousHandler();	
	
	ContextErrorHandler getContextErrorHandler();	
}