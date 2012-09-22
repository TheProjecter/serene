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
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.relaxng.datatype.Datatype;

import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.error.ErrorCatcher;

import serene.bind.util.Queue;
import serene.bind.BindingModel;
import serene.bind.AttributeTask;


public class BoundUnresolvedAttributeConflictResolver extends BoundAttributeConflictResolver{
	    
	public BoundUnresolvedAttributeConflictResolver(){
		super();
	}
	
	public void recycle(){
	    reset();
	    pool.recycle(this);
	}
	
    public void resolve(ErrorCatcher errorCatcher){
        if(qualified.cardinality() == 0){				
            SAttribute[] definitions = candidateDefinitions.toArray(new SAttribute[candidateDefinitions.size()]);
            errorCatcher.unresolvedAttributeContentError(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
        }else if(qualified.cardinality() == 1){
            int qual = qualified.nextSetBit(0);
            
            temporaryMessageStorage[qual].transferMessages(errorCatcher);
            temporaryMessageStorage[qual] = null;
            
            SAttribute attribute = candidateDefinitions.get(qual);
            int definitionIndex = attribute.getDefinitionIndex();
            AttributeTask task = bindingModel.getAttributeTask(attribute);
            if(task != null){
                targetQueue.addAttribute(targetEntry, inputRecordIndex, task);
            }
        }else{		
            int j = 0;
            for(int i = 0; i < candidateDefinitions.size(); i++){			
                if(!qualified.get(j++)){
                    candidateDefinitions.remove(i);
                    i--;
                }
            }   
            SAttribute[] definitions = candidateDefinitions.toArray(new SAttribute[candidateDefinitions.size()]);
            errorCatcher.unresolvedAttributeContentError(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
        }
    }
	
	public String toString(){
		return "BoundUnresolvedAttributeConflictResolver ";
	}
}
