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

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;

public abstract class ElementConflictResolver extends InternalConflictResolver{
	
	List<AElement> candidateDefinitions;
	ConflictMessageReporter conflictMessageReporter;
	boolean isResolved;	
	public ElementConflictResolver(){				
		super();
		candidateDefinitions = new ArrayList<AElement>();
		isResolved = false;
	}
			
	void init(ConflictMessageReporter conflictMessageReporter){
	    super.init();
	    this.conflictMessageReporter = conflictMessageReporter;
	}
	
    void reset(){
        super.reset();
        candidateDefinitions.clear();
        if(!isResolved && conflictMessageReporter != null) {
            conflictMessageReporter.setDiscarded(true);
            conflictMessageReporter.clear(this);
            conflictMessageReporter = null;
            isResolved = false;
        }        
    }
    
    public void addCandidate(AElement candidate){
        candidateDefinitions.add(candidate);
    }
      
    public String toString(){
        return "ElementConflictResolver candidates "+candidateDefinitions+" qualified "+qualified;
    }		
}
