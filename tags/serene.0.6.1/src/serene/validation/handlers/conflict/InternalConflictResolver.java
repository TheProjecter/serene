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

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.validation.handlers.error.ErrorCatcher;

/*
* In the ConcurrentStackHandler each level handles a conflict and every handler 
* on that level gets a reference to an InternalConflictResolver instance. This 
* counts the qualified candidates and when asked to resolve() theconflict if the
* number is greater than 1, it reports ambiguous content.   
*/
public abstract class InternalConflictResolver implements ConflictResolver{
	BitSet qualified;
		
	
	int inputRecordIndex;
	InputStackDescriptor inputStackDescriptor;
	ActiveInputDescriptor activeInputDescriptor;
	
	ActiveModelConflictHandlerPool pool;
	
	public InternalConflictResolver(){
		qualified = new BitSet();
	}
		
	void init(ActiveModelConflictHandlerPool pool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor){
	    this.activeInputDescriptor = activeInputDescriptor;
		this.inputStackDescriptor = inputStackDescriptor;
		this.pool = pool;
	}
	
	void init(){		 
		inputRecordIndex = inputStackDescriptor.getCurrentItemInputRecordIndex();
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
	}

	
	void reset(){
	    qualified.clear();
	    activeInputDescriptor.unregisterClientForRecord(inputRecordIndex, this);
		inputRecordIndex = -1;
	}
	
	
	public void qualify(int candidateIndex){	    
		qualified.set(candidateIndex);
	}	
}