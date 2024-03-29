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

import java.util.List;
import java.util.Arrays;

import serene.validation.schema.simplified.SPattern;

import serene.validation.handlers.error.ErrorCatcher;

/*
* In the ConcurrentStackHandler each level handles a conflict and every handler 
* on that level gets a reference to an InternalConflictResolver instance. This 
* counts the qualified candidates and when asked to resolve() theconflict if the
* number is greater than 1, it reports ambiguous content.   
*/
public class UnresolvedListTokenConflictResolver extends ListTokenConflictResolver{
	public UnresolvedListTokenConflictResolver(){				
		super();
	}	
    
	public void recycle(){
		reset();
		pool.recycle(this);
	}
	
	public void resolve(ErrorCatcher errorCatcher){
        if(qualified.cardinality() == 1){
            int q = qualified.nextSetBit(0);
            temporaryMessageStorage[q].transferMessages(errorCatcher);
            temporaryMessageStorage[q] = null;
        }else{
            SPattern[] definitions = candidateDefinitions.toArray(new SPattern[candidateDefinitions.size()]);
            errorCatcher.unresolvedListTokenInContextError(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
        }
    }	
		
    public String toString(){
        return "UnresolvedListTokenConflictResolver candidates "+candidateDefinitions+" qualified "+qualified;
    }
}
