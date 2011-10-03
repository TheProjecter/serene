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

import org.xml.sax.SAXException;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.MessageReporter;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;


public class BoundUnresolvedElementConflictResolver extends BoundElementConflictResolver{
	
	public BoundUnresolvedElementConflictResolver(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	public void recycle(){
	    reset();
	    pool.recycle(this);
	}
		
    public void resolve(ErrorCatcher errorCatcher) throws SAXException{
        if(qualified.cardinality() == 0){	
            // report all external conflict errors + internal conflict error for all losers
            conflictMessageReporter.setConflictInternalResolution(MessageReporter.UNRESOLVED);
            errorCatcher.internalConflict(conflictMessageReporter);
            
            AElement[] definitions = candidateDefinitions.toArray(new AElement[candidateDefinitions.size()]);
            errorCatcher.unresolvedUnresolvedElementContentError(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
            
            targetQueue.closeReservation(targetEntry);				
        }else if(qualified.cardinality() == 1){
            // report the external conflict errors of the winner
            int qual = qualified.nextSetBit(0);				
            Queue qq = candidateQueues.get(candidateDefinitions.get(qual));
            targetQueue.closeReservation(targetEntry, qq);	
            
            conflictMessageReporter.setConflictInternalResolution(MessageReporter.RESOLVED, qual, candidateDefinitions.get(qual));
            errorCatcher.internalConflict(conflictMessageReporter);			
        }else{
            // report the external conflict errors of the winners ?+ ambiguity warning?
            conflictMessageReporter.setConflictInternalResolution(MessageReporter.AMBIGUOUS, qualified);
            errorCatcher.internalConflict(conflictMessageReporter);
            
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
    }
	public String toString(){
		return "BounUnresolvedElementConflictResolver ";
	}
}
