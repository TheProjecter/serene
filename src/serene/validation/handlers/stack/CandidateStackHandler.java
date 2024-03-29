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

package serene.validation.handlers.stack;


import serene.validation.schema.simplified.SRule;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.stack.StackHandler;


import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.CharsMatchPath;

public interface CandidateStackHandler extends StackHandler{
	final int UNINDEXED = -1;
	
	//void init(int candidateIndex, ConcurrentStackHandler parent);	
	/*InternalConflictResolver getLevelConflictResolver();*/
	/*int getCandidateIndex();*/	
	void shift(ElementMatchPath element, boolean reportExcessive, boolean reportPreviousMisplaced, boolean reportCurrentMisplaced, boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing);
	void shift(AttributeMatchPath attribute, boolean reportExcessive, boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing);
	void shift(CharsMatchPath chars, boolean reportExcessive, boolean reportPreviousMisplaced, boolean reportCurrentMisplaced, boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing);
	
	void shift(ElementMatchPath element, SRule[] innerPath, InternalConflictResolver resolver, int definitionCandidateIndex, boolean reportExcessive, boolean reportPreviousMisplaced, boolean reportCurrentMisplaced, boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing);
	void shift(AttributeMatchPath attribute, SRule[] innerPath, InternalConflictResolver resolver, int definitionCandidateIndex, boolean reportExcessive, boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing);
	void shift(CharsMatchPath chars, SRule[] innerPath, InternalConflictResolver resolver, int definitionCandidateIndex, boolean reportExcessive, boolean reportPreviousMisplaced, boolean reportCurrentMisplaced, boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing);
	
	CandidateStackHandler getCopy();
		
	boolean hasActiveConflicts();
	
	/**
	* The resolvers from this handler's StackConflictsResolver are transfered to 
	* the other handler's StackConflictsResolver. This is done when this handler
	* is functionally equivalent to the other and it will be removed since further
	* processing here would be redundant.
	*/
	void transferResolversTo(CandidateStackHandler other);
	
	void transferResolversFrom(StackConflictsHandler sch);
	
	void endValidation(boolean reportMissing, boolean reportIllegal, boolean reportCompositorContentMissing);
	
	boolean hasDisqualifyingError();
	
	void handleConflicts();
}