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

package serene.validation.handlers.conflict;

import java.util.BitSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.TemporaryMessageStorage;

/*
* In the ConcurrentStackHandler each level handles a conflict and every handler 
* on that level gets a reference to an InternalConflictResolver instance. This 
* counts the qualified candidates and when asked to resolve() theconflict if the
* number is greater than 1, it reports ambiguous content.   
*/
public abstract class AttributeConflictResolver extends InternalConflictResolver{
	List<SAttribute> candidateDefinitions;
	TemporaryMessageStorage[] temporaryMessageStorage;
	public AttributeConflictResolver(){				
		super();
		candidateDefinitions = new ArrayList<SAttribute>();
	}	
	
	void init(TemporaryMessageStorage[] temporaryMessageStorage){
	    super.init();
	    this.temporaryMessageStorage = temporaryMessageStorage;
	}	
	
    void reset(){
        super.reset();
        candidateDefinitions.clear();
        if(temporaryMessageStorage != null) {
            for(int i = 0; i < temporaryMessageStorage.length; i++){
                if(temporaryMessageStorage[i] != null){
                    /*temporaryMessageStorage[i].setDiscarded(true);*/
                    temporaryMessageStorage[i].clear();
                }
            }
        }
        temporaryMessageStorage = null;
    }
    
    public void addCandidate(SAttribute candidate){
        candidateDefinitions.add(candidate);
    }
        
    public String toString(){
        return "AttributeConflictResolver candidates "+candidateDefinitions+" qualified "+qualified;
    }
}
