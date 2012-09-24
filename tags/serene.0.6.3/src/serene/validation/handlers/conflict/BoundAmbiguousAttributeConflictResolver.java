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

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.bind.util.Queue;
import serene.bind.BindingModel;
import serene.bind.AttributeTask;

public class BoundAmbiguousAttributeConflictResolver extends BoundAttributeConflictResolver{
    BitSet disqualified;
    public BoundAmbiguousAttributeConflictResolver(){
		super();
	}
	
	public void recycle(){
	    reset();
	    pool.recycle(this);
	}
	
	void reset(){
	    super.reset();
	    disqualified = null;
	}
	
	void init(BitSet disqualified,
	        TemporaryMessageStorage[] temporaryMessageStorage,
	        /*String namespaceURI, 
            String localName,
            String qName,*/
            String value, 
			Queue queue, 
			int entry, 
			BindingModel bindingModel){		
		super.init(temporaryMessageStorage,
		            /*namespaceURI, 
                    localName,
                    qName,*/
                    value, 
                    queue, 
                    entry, 
                    bindingModel);			
		this.disqualified = disqualified;		
	}
		
    public void resolve(ErrorCatcher errorCatcher){
        if(qualified.cardinality() == 0){				
            SAttribute[] definitions = candidateDefinitions.toArray(new SAttribute[candidateDefinitions.size()]);
            errorCatcher.unresolvedAttributeContentError(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
        }else if(qualified.cardinality() == 1){
            int qual = qualified.nextSetBit(0);
            
            if(temporaryMessageStorage != null && temporaryMessageStorage[qual] != null){
                temporaryMessageStorage[qual].transferMessages(errorCatcher);
                temporaryMessageStorage[qual] = null;
            }
            
            SAttribute attribute = candidateDefinitions.get(qual);
            int definitionIndex = attribute.getDefinitionIndex();
            AttributeTask task = bindingModel.getAttributeTask(attribute);
            if(task != null){
                targetQueue.addAttribute(targetEntry, inputRecordIndex, task);
            }
        }else{
            for(int i = 0; i < disqualified.length(); i++){
                if(disqualified.get(i) && qualified.get(i))qualified.clear(i);
            }
            
            if(qualified.cardinality()== 0){
                SAttribute[] definitions = candidateDefinitions.toArray(new SAttribute[candidateDefinitions.size()]);
                errorCatcher.unresolvedAttributeContentError(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
            }else if(qualified.cardinality() > 1){
                int j = 0;
                for(int i = 0; i < candidateDefinitions.size(); i++){			
                    if(!qualified.get(j++)){
                        candidateDefinitions.remove(i);
                        i--;
                    }
                }   
                SAttribute[] definitions = candidateDefinitions.toArray(new SAttribute[candidateDefinitions.size()]);
                errorCatcher.ambiguousAttributeContentWarning(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
            }else{
                int q = qualified.nextSetBit(0);
                if(temporaryMessageStorage != null && temporaryMessageStorage[q] != null){
                    temporaryMessageStorage[q].transferMessages(errorCatcher);
                    temporaryMessageStorage[q] = null;
                }
            }
        }
    }
	
	public String toString(){
		return "BoundAmbiguousAttributeConflictResolver ";
	}
}
