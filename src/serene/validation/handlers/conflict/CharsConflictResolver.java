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

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.content.util.InputStackDescriptor;

public abstract class CharsConflictResolver extends InternalConflictResolver{	
    List<SPattern> candidateDefinitions;
    TemporaryMessageStorage[] temporaryMessageStorage;  
	public CharsConflictResolver(){				
		super();
		candidateDefinitions = new ArrayList<SPattern>();
	}
	
	void init(TemporaryMessageStorage[] temporaryMessageStorage){
	    super.init();
	    this.temporaryMessageStorage = temporaryMessageStorage;	    	    
	}	
    void reset(){
        super.reset();
        candidateDefinitions.clear(); 
        if(temporaryMessageStorage != null) {
            for(int i = 0; i < temporaryMessageStorage.length; i++){
                if(temporaryMessageStorage[i] != null){
                    /*temporaryMessageStorage[i].setDiscarded(true);*/
                    temporaryMessageStorage[i].clear();
                }
            }
            temporaryMessageStorage = null;
        }
        
    }
    public void addCandidate(SPattern candidate){
        candidateDefinitions.add(candidate);
    }
    
    void reportUnresolvedError(ErrorCatcher errorCatcher){  
        SPattern[] definitions = candidateDefinitions.toArray(new SPattern[candidateDefinitions.size()]);
        //errorCatcher.ambiguousCharsContentErrorsystemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
        if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){
            //only possible within the context of an except pattern			
			errorCatcher.unresolvedCharacterContent(inputRecordIndex, Arrays.copyOf(definitions, definitions.length)); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){
			errorCatcher.unresolvedCharacterContent(inputRecordIndex, Arrays.copyOf(definitions, definitions.length)); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
			errorCatcher.unresolvedAttributeValue(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
		}else{		    
			throw new IllegalStateException();
		}
    }
    
    void reportAmbiguousWarning(ErrorCatcher errorCatcher){
        SPattern[] definitions = candidateDefinitions.toArray(new SPattern[candidateDefinitions.size()]);        
        if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){
            //only possible within the context of an except pattern		
			errorCatcher.ambiguousCharacterContentWarning(inputRecordIndex, Arrays.copyOf(definitions, definitions.length)); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){
			errorCatcher.ambiguousCharacterContentWarning(inputRecordIndex, Arrays.copyOf(definitions, definitions.length)); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
		    errorCatcher.ambiguousAttributeValueWarning(inputRecordIndex, Arrays.copyOf(definitions, definitions.length));
		}else{		    
			throw new IllegalStateException();
		}
    }
    
    public String toString(){
        return "CharsConflictResolver candidates "+candidateDefinitions+" qualified "+qualified;
    }
}
