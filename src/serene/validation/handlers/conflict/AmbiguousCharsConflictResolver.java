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
import java.util.ArrayList;

import serene.validation.schema.simplified.SPattern;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.TemporaryMessageStorage;

public class AmbiguousCharsConflictResolver extends CharsConflictResolver{
    BitSet disqualified;	
	public AmbiguousCharsConflictResolver(){				
		super();
	}
	
	public void recycle(){
		reset();
		disqualified = null;
		pool.recycle(this);
	}
    
	void init(BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
	    super.init(temporaryMessageStorage);
	    this.disqualified = disqualified;
	}	
	
    public void resolve(ErrorCatcher errorCatcher){
        if(qualified.cardinality()== 0){
            reportUnresolvedError(errorCatcher);
        }else if(qualified.cardinality() > 1){
            for(int i = 0; i < disqualified.length(); i++){
                if(disqualified.get(i) && qualified.get(i))qualified.clear(i);
            }
            
            if(qualified.cardinality()== 0){
                reportUnresolvedError(errorCatcher);
            }else if(qualified.cardinality() > 1){
                int j = 0;
                for(int i = 0; i < candidateDefinitions.size(); i++){			
                    if(!qualified.get(j++)){
                        candidateDefinitions.remove(i);
                        i--;
                    }
                }   
                reportAmbiguousWarning(errorCatcher);
            }else{
                int q = qualified.nextSetBit(0);
                if(temporaryMessageStorage != null && temporaryMessageStorage[q] != null){
                    temporaryMessageStorage[q].transferMessages(errorCatcher);
                    temporaryMessageStorage[q] = null;
                }
            }
        }else{  
            int q = qualified.nextSetBit(0);
            if(temporaryMessageStorage != null && temporaryMessageStorage[q] != null){                
                temporaryMessageStorage[q].transferMessages(errorCatcher);
                temporaryMessageStorage[q] = null;
            }
        }
    }	
    
    
    
    public String toString(){
        return "AmbiguousCharsConflictResolver candidates "+candidateDefinitions+" qualified "+qualified;
    }
}
