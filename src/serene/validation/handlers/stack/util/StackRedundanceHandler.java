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

package serene.validation.handlers.stack.util;

import java.util.Arrays;

import serene.validation.handlers.stack.CandidateStackHandler;

import sereneWrite.MessageWriter;
public class StackRedundanceHandler{
	MessageWriter debugWriter;

	CandidateStackHandler[] candidatesTable;
	int[] codesCacheTable;
 
	 /**
     * The number of codes contained in the codesTable.
     */
    int size;
	
	
	public StackRedundanceHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
        size = 10;
        codesCacheTable = new int[size];
		candidatesTable = new CandidateStackHandler[size];
		for(int i = 0; i < size; i++){
			codesCacheTable[i] = -1;
		}
	}

	public void clear(){
		for(int i = 0; i < size; i++){
			codesCacheTable[i] = -1;
			candidatesTable[i] = null;
		}
	}
	
	/** 
	* Stores the value of the specified CandidateStackHandler's 
	* functionalEquivalenceCode if not already stored. Returns true when the 
	* value has been stored already and false if it is a new value.
	*/
	public boolean isRedundant(CandidateStackHandler candidate){			
        if (candidate == null)
            throw new IllegalArgumentException();
        int code = candidate.functionalEquivalenceCode();
		int index = 0;
		for (index = 0; index < size; index++) {
			if (code == codesCacheTable[index])	{
				boolean candidateHasErrors = candidate.hasDisqualifyingError();
				boolean standardHasErrors = candidatesTable[index].hasDisqualifyingError();
				if(!(candidateHasErrors ^ standardHasErrors)){// THIS MEANS it is possible to have double keys in the codes cache table, so you can't stop searching at the first match.
					candidate.transferResolversTo(candidatesTable[index]);										
					return true;
				}
			}else if(codesCacheTable[index] == -1){		
				break;
			}
		}	
		
		if(index == size){
			size += 10;
			int[] increasedCache = new int[size];
			System.arraycopy(codesCacheTable, 0, increasedCache, 0, index);
			codesCacheTable = increasedCache;
			for(int i = index; i < size; i++){
				codesCacheTable[i] = -1;
			}
			
			CandidateStackHandler[] increasedCandidates = new CandidateStackHandler[size];
			System.arraycopy(candidatesTable, 0, increasedCandidates, 0, index);
			candidatesTable = increasedCandidates;
		}	
		codesCacheTable[index] = code;
		candidatesTable[index] = candidate;
		return false;		
    }	
}