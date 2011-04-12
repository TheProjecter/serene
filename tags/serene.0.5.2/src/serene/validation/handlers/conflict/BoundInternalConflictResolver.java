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

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;


public class BoundInternalConflictResolver extends InternalConflictResolver{
	Queue targetQueue;
	int targetEntry;	
	
	BoundElementConflict boundElementConflict;
	BoundAttributeConflict boundAttributeConflict;
	
	public BoundInternalConflictResolver(MessageWriter debugWriter){
		super(debugWriter);
		createStates();		
	}
	
	void createStates(){
		boundElementConflict = new BoundElementConflict();		
		boundAttributeConflict = new BoundAttributeConflict();
		charsConflict = new CharsConflict();		
	}
	void init(Queue targetQueue,
			int targetEntry,
			Map<AElement, Queue> candidateQueues){		
		super.init();
		this.targetQueue = targetQueue;
		this.targetEntry = targetEntry;
		boundElementConflict.setCandidateQueues(candidateQueues);
		state = boundElementConflict;
	}
	
	void init(String namespaceURI, 
            String localName,
            String qName,
            String value, 
			Queue queue, 
			int entry, 
			Map<AAttribute, AttributeBinder> attributeBinders){		
		super.init();			
		this.targetQueue = queue;
		this.targetEntry = entry;
		boundAttributeConflict.setBinders(attributeBinders);
		boundAttributeConflict.setAttributeData(namespaceURI,
                    localName,
                    qName,
                    value);
		state = boundAttributeConflict;
	}

	
	public void recycle(){
		targetQueue = null;
		targetEntry = -1;
		super.recycle();
	}
	
	class BoundElementConflict extends ElementConflict{
		Map<AElement, Queue> candidateQueues;
		
		void setCandidateQueues(Map<AElement, Queue> candidateQueues){
			this.candidateQueues = candidateQueues;
		}
		void reset(){
			candidateDefinitions.clear();
			candidateQueues = null;
		}
		void resolve(ErrorCatcher errorCatcher){
			if(qualified.cardinality() == 0){				
				AElement[] definitions = candidateDefinitions.toArray(new AElement[candidateDefinitions.size()]);
				errorCatcher.ambiguousElementContentError(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
				targetQueue.closeReservation(targetEntry);				
			}else if(qualified.cardinality() == 1){
				int qual = qualified.nextSetBit(0);				
				Queue qq = candidateQueues.get(candidateDefinitions.get(qual));
				targetQueue.closeReservation(targetEntry, qq);				
			}else{		
				int j = 0;
				for(int i = 0; i < candidateDefinitions.size(); i++){			
					if(!qualified.get(j++)){
						candidateDefinitions.remove(i);
						i--;
					}
				}
				AElement[] definitions = candidateDefinitions.toArray(new AElement[candidateDefinitions.size()]);
				errorCatcher.ambiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
				targetQueue.closeReservation(targetEntry);
			}			
		}	
	}
	
	class BoundAttributeConflict extends AttributeConflict{
        String namespaceURI;
        String localName;
        String qName;
		String value;
		Map<AAttribute, AttributeBinder> attributeBinders;
		
		void setBinders(Map<AAttribute, AttributeBinder> attributeBinders){
			this.attributeBinders = attributeBinders;
		}
		void setAttributeData(String namespaceURI, String localName, String qName, String value){
            this.namespaceURI = namespaceURI;
            this.localName = localName;
            this.qName = qName;
			this.value = value;
		}
		void reset(){
			candidateDefinitions.clear();
			attributeBinders = null;
			value = null;
		}
		void resolve(ErrorCatcher errorCatcher){
			if(qualified.cardinality() == 0){				
				AAttribute[] definitions = candidateDefinitions.toArray(new AAttribute[candidateDefinitions.size()]);
				errorCatcher.ambiguousAttributeContentError(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
			}else if(qualified.cardinality() == 1){
				int qual = qualified.nextSetBit(0);
				AAttribute attribute = candidateDefinitions.get(qual);
				int definitionIndex = attribute.getDefinitionIndex();
				AttributeBinder binder = attributeBinders.get(attribute);
				if(binder != null){
                    binder.bindAttribute(targetQueue, targetEntry, definitionIndex, namespaceURI, localName, qName, Datatype.ID_TYPE_NULL, value);
                }
			}else{		
				int j = 0;
				for(int i = 0; i < candidateDefinitions.size(); i++){			
					if(!qualified.get(j++)){
						candidateDefinitions.remove(i);
						i--;
					}
				}
				AAttribute[] definitions = candidateDefinitions.toArray(new AAttribute[candidateDefinitions.size()]);
				errorCatcher.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
			}
		}	
	}
	
	public String toString(){
		//return "BoundInternalConflictResolver "+hashCode();
		return "BoundInternalConflictResolver ";
	}
}
