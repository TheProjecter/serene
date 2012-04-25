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
import serene.validation.handlers.error.MessageReporter;

import serene.bind.util.Queue;


public class BoundAmbiguousElementConflictResolver extends BoundElementConflictResolver{
	
	public BoundAmbiguousElementConflictResolver(){
		super();
	}
	
	public void recycle(){
	    reset();
	    pool.recycle(this);
	}
		
    public void resolve(ErrorCatcher errorCatcher) throws SAXException{
        isResolved = true;
        if(qualified.cardinality() == 0){
            // report internal conflict error for all winners + common or subtree errors reporting for the winners
            if(conflictMessageReporter != null){
                conflictMessageReporter.setConflictInternalResolution(MessageReporter.UNRESOLVED);
                errorCatcher.internalConflict(conflictMessageReporter);
            }
            
            AElement[] definitions = candidateDefinitions.toArray(new AElement[candidateDefinitions.size()]);
            errorCatcher.unresolvedAmbiguousElementContentError(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
            
            closeReservation();			
        }else if(qualified.cardinality() == 1){
            // simple error reporting if external conflict detected any common or subtree errors
            int qual = qualified.nextSetBit(0);
            closeReservation(qual);
            if(conflictMessageReporter != null){
                conflictMessageReporter.setConflictInternalResolution(MessageReporter.RESOLVED, qual, candidateDefinitions.get(qual));
                errorCatcher.internalConflict(conflictMessageReporter);
            }			
        }else{
            // internal conflict warning + common or subtree errors reporting for the winners            
            if(conflictMessageReporter != null){
                conflictMessageReporter.setConflictInternalResolution(MessageReporter.AMBIGUOUS, qualified);
                errorCatcher.internalConflict(conflictMessageReporter);
            }
            int j = 0;
            for(int i = 0; i < candidateDefinitions.size(); i++){			
                if(!qualified.get(j++)){
                    candidateDefinitions.remove(i);
                    i--;
                }
            }   
            AElement[] definitions = candidateDefinitions.toArray(new AElement[candidateDefinitions.size()]);
            errorCatcher.ambiguousAmbiguousElementContentWarning(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
            
            closeReservation();
        }			
    }
    
    
    
	public String toString(){
		return "BounAmbiguousElementConflictResolver ";
	}
}
