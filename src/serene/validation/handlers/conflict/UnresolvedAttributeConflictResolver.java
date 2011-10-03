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

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.content.util.ValidationItemLocator;
import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

/*
* In the ConcurrentStackHandler each level handles a conflict and every handler 
* on that level gets a reference to an InternalConflictResolver instance. This 
* counts the qualified candidates and when asked to resolve() theconflict if the
* number is greater than 1, it reports ambiguous content.   
*/
public class UnresolvedAttributeConflictResolver extends AttributeConflictResolver{
	
	public UnresolvedAttributeConflictResolver(MessageWriter debugWriter){				
		super(debugWriter);
	}
	
	public void recycle(){
		reset();
		pool.recycle(this);
	}
		
    public void resolve(ErrorCatcher errorCatcher){			
        if(qualified.cardinality()== 0){
            AAttribute[] definitions = candidateDefinitions.toArray(new AAttribute[candidateDefinitions.size()]);
            errorCatcher.unresolvedAttributeContentError(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
        }else if(qualified.cardinality() > 1){
            int j = 0;
            for(int i = 0; i < candidateDefinitions.size(); i++){			
                if(!qualified.get(j++)){
                    candidateDefinitions.remove(i);
                    i--;
                }
            }   
            AAttribute[] definitions = candidateDefinitions.toArray(new AAttribute[candidateDefinitions.size()]);
            errorCatcher.unresolvedAttributeContentError(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
        }else{
            temporaryMessageStorage[qualified.nextSetBit(0)].transferMessages(errorCatcher);
        }
    }
    
    public String toString(){
        return "UnresolvedAttributeConflictResolver candidates "+candidateDefinitions+" qualified "+qualified;
    }
}
