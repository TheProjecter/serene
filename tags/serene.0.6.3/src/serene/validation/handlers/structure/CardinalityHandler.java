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

package serene.validation.handlers.structure;

import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.error.ErrorCatcher;


public interface CardinalityHandler{
	final static int NO_OCCURRENCE = 0;
	final static int OPEN = 1;
	final static int SATISFIED_NEVER_SATURATED = 2;
	final static int SATISFIED_SIMPLE = 3;
	final static int SATURATED = 4;
	final static int EXCESSIVE = 5;
			
	int getIndex();
	
	void handleOccurrence(int inputRecordIndex);		
	void handleOccurrence(StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver);
	void handleOccurrence(StackConflictsHandler stackConflictsHandler);
	void reportExcessive(SRule context, int startInputRecordIndex);
	void reportMissing(SRule context, int startInputRecordIndex);
	
	/*CardinalityHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher);
	
	CardinalityHandler getOriginal();*/
}