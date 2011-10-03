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

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;


public class BoundAmbiguousAttributeConflictResolver extends BoundAttributeConflictResolver{
    BitSet disqualified;
    public BoundAmbiguousAttributeConflictResolver(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	public void recycle(){
	    reset();
	    pool.recycle(this);
	}
	
	void init(BitSet disqualified,
	        TemporaryMessageStorage[] temporaryMessageStorage,
	        String namespaceURI, 
            String localName,
            String qName,
            String value, 
			Queue queue, 
			int entry, 
			Map<AAttribute, AttributeBinder> attributeBinders){		
		super.init(temporaryMessageStorage,
		            namespaceURI, 
                    localName,
                    qName,
                    value, 
                    queue, 
                    entry, 
                    attributeBinders);			
		this.disqualified = disqualified;		
	}
		
    public void resolve(ErrorCatcher errorCatcher){
        if(qualified.cardinality() == 0){				
            AAttribute[] definitions = candidateDefinitions.toArray(new AAttribute[candidateDefinitions.size()]);
            errorCatcher.unresolvedAttributeContentError(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
        }else if(qualified.cardinality() == 1){
            int qual = qualified.nextSetBit(0);
            
            if(temporaryMessageStorage != null && temporaryMessageStorage[qual] != null)temporaryMessageStorage[qual].transferMessages(errorCatcher);
            
            AAttribute attribute = candidateDefinitions.get(qual);
            int definitionIndex = attribute.getDefinitionIndex();
            AttributeBinder binder = attributeBinders.get(attribute);
            if(binder != null){
                binder.bindAttribute(targetQueue, targetEntry, definitionIndex, namespaceURI, localName, qName, Datatype.ID_TYPE_NULL, value);
            }
        }else{
            for(int i = 0; i < disqualified.length(); i++){
                if(disqualified.get(i) && qualified.get(i))qualified.clear(i);
            }
            
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
                errorCatcher.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
            }else{
                if(temporaryMessageStorage != null && temporaryMessageStorage[qualified.nextSetBit(0)] != null)temporaryMessageStorage[qualified.nextSetBit(0)].transferMessages(errorCatcher);        
            }
        }
    }
	
	public String toString(){
		return "BoundAmbiguousAttributeConflictResolver ";
	}
}
