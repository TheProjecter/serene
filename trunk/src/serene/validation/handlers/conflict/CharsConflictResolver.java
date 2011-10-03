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

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.content.util.ValidationItemLocator;
import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

public class CharsConflictResolver extends InternalConflictResolver{
	
    List<CharsActiveTypeItem> candidateDefinitions;
    
	public CharsConflictResolver(MessageWriter debugWriter){				
		super(debugWriter);
		candidateDefinitions = new ArrayList<CharsActiveTypeItem>();
	}
	
	public void recycle(){
		reset();
		pool.recycle(this);
	}
    
    void reset(){
        super.reset();
        candidateDefinitions.clear();
    }
    public void addCandidate(CharsActiveTypeItem candidate){
        candidateDefinitions.add(candidate);
    }
    public void resolve(ErrorCatcher errorCatcher){
        if(qualified.cardinality() == 0){
            CharsActiveTypeItem[] definitions = candidateDefinitions.toArray(new CharsActiveTypeItem[candidateDefinitions.size()]);
            errorCatcher.ambiguousCharsContentError(systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
        }else if(qualified.cardinality() > 1){ 
            int j = 0;
            for(int i = 0; i < candidateDefinitions.size(); i++){			
                if(!qualified.get(j++)){
                    candidateDefinitions.remove(i);
                    i--;
                }
            }
            CharsActiveTypeItem[] definitions = candidateDefinitions.toArray(new CharsActiveTypeItem[candidateDefinitions.size()]);
            errorCatcher.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
        }
    }	
    
    public String toString(){
        return "CharsConflictResolver candidates "+candidateDefinitions+" qualified "+qualified;
    }
}
