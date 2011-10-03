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

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;


public abstract class BoundElementConflictResolver extends ElementConflictResolver{
	Queue targetQueue;
	int targetEntry;	
	
	Map<AElement, Queue> candidateQueues;
	
	public BoundElementConflictResolver(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	void init(ConflictMessageReporter conflictMessageReporter,
	        Queue targetQueue,
			int targetEntry,
			Map<AElement, Queue> candidateQueues){		
		super.init(conflictMessageReporter);
		this.targetQueue = targetQueue;
		this.targetEntry = targetEntry;
		this.candidateQueues = candidateQueues;
	}
	
	
	void reset(){
	    super.reset();
		targetQueue = null;
		targetEntry = -1;
		candidateDefinitions.clear();
		candidateQueues = null;
	}
	
		
    /*public void resolve(ErrorCatcher errorCatcher){
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
            errorCatcher.ambiguousUnresolvedElementContentWarning(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
            targetQueue.closeReservation(targetEntry);
        }			
    }*/
    
	public String toString(){
		return "BoundElementConflictResolver ";
	}
}
