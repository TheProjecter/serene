/*
Copyright 2011 Radu Cernuta 

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


package serene.validation.handlers.conflict;

import java.util.BitSet;
import java.util.List;
import java.util.Arrays;

import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.TemporaryMessageStorage;

/*
* In the ConcurrentStackHandler each level handles a conflict and every handler 
* on that level gets a reference to an InternalConflictResolver instance. This 
* counts the qualified candidates and when asked to resolve() theconflict if the
* number is greater than 1, it reports ambiguous content.   
*/
public class AmbiguousListTokenConflictResolver extends ListTokenConflictResolver{
    BitSet disqualified;
	public AmbiguousListTokenConflictResolver(){				
		super();
	}
	
	void init(BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
		super.init(temporaryMessageStorage);
        this.disqualified = disqualified;
	}
	
	public void recycle(){
		reset();
		disqualified = null;
		pool.recycle(this);		
	}
	
	public void resolve(ErrorCatcher errorCatcher){
        if(qualified.cardinality() == 0){
            CharsActiveTypeItem[] definitions = candidateDefinitions.toArray(new CharsActiveTypeItem[candidateDefinitions.size()]);
            errorCatcher.unresolvedListTokenInContextError(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));            
        }else if(qualified.cardinality() > 1){ 
            int j = 0;
            for(int i = 0; i < candidateDefinitions.size(); i++){			
                if(!qualified.get(j++)){
                    candidateDefinitions.remove(i);
                    i--;
                }
            }
            CharsActiveTypeItem[] definitions = candidateDefinitions.toArray(new CharsActiveTypeItem[candidateDefinitions.size()]);
            errorCatcher.ambiguousListTokenInContextWarning(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
        }else{
            int q = qualified.nextSetBit(0);
            if(temporaryMessageStorage != null && temporaryMessageStorage[q] != null){
                temporaryMessageStorage[q].transferMessages(errorCatcher);
                temporaryMessageStorage[q] = null;
            }
        }
    }	
		
    public String toString(){
        return "AmbiguousListTokenConflictResolver candidates "+candidateDefinitions+" qualified "+qualified;
    }
}
