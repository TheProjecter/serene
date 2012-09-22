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

package serene.validation.handlers.content.impl;

import java.util.List;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;
//import serene.validation.handlers.error.CandidatesConflictErrorHandler;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.match.AttributeMatchPath;

class AttributeConcurrentHandler extends ValidatingAEH{
    ElementValidationHandler parent;	
    
    
	List<AttributeMatchPath> candidateDefinitionPathes;
	boolean isShifted;
	List<CandidateAttributeValidationHandler> candidates;
	ExternalConflictHandler localCandidatesConflictHandler;     
	ValidatorErrorHandlerPool errorHandlerPool;
	TemporaryMessageStorage[] temporaryMessageStorage;
	
	
	AttributeConcurrentHandler(){
		super();
		candidateDefinitionPathes = new ArrayList<AttributeMatchPath>();
		candidates = new ArrayList<CandidateAttributeValidationHandler>(3);		
		localCandidatesConflictHandler = new ExternalConflictHandler();
	}
		
	public void recycle(){
		for(CandidateAttributeValidationHandler candidate: candidates){
			candidate.recycle();
		}
		localCandidatesConflictHandler.clear();
		candidates.clear();
		temporaryMessageStorage = null;
		
		if(!isShifted){
		    for(AttributeMatchPath amp : candidateDefinitionPathes){
		        amp.recycle();
		    }
		}
		isShifted = false;
		candidateDefinitionPathes.clear();
		
		pool.recycle(this);
	}
		
	void init(ValidatorEventHandlerPool pool, InputStackDescriptor inputStackDescriptor, ValidatorErrorHandlerPool errorHandlerPool){
		super.init(pool, inputStackDescriptor);
		this.errorHandlerPool = errorHandlerPool;
	}
	
	void init(List<AttributeMatchPath> cdp, ElementValidationHandler parent){
		this.parent = parent;
		this.candidateDefinitionPathes.addAll(cdp); 
		localCandidatesConflictHandler.init(candidateDefinitionPathes.size());
		temporaryMessageStorage = new TemporaryMessageStorage[candidateDefinitionPathes.size()];
		
		for(int i = 0; i < candidateDefinitionPathes.size(); i++){						
			// To each candidate set a ConflictErrorHandler that knows the ExternalConflictHandler
			// and the candidate index. Errors will not be handled and reported.
			// At the end of attribute handling only the number of qualified 
			// candidates left is assesed and the appropriate addAttribute() 
			// is called.
			CandidateAttributeValidationHandler candidate = pool.getCandidateAttributeValidationHandler(candidateDefinitionPathes.get(i), parent, localCandidatesConflictHandler, i, temporaryMessageStorage);
			candidates.add(candidate);
		}		
	}
	
    public ElementValidationHandler getParentHandler(){
        return parent;
    }
    
	void validateValue(String value) throws SAXException{
		for(int i = 0; i < candidates.size(); i++){
			candidates.get(i).validateValue(value);
		}
	}

	void validateInContext(){
	    isShifted = true;
		int candidatesCount = candidates.size();		
		int qualifiedCount = candidatesCount - localCandidatesConflictHandler.getDisqualifiedCount();		
		if(qualifiedCount == 0){		
			parent.addAttribute(candidateDefinitionPathes, temporaryMessageStorage);
		}else if(qualifiedCount == 1){
			AttributeMatchPath qAttributeMatchPath = candidateDefinitionPathes.get(localCandidatesConflictHandler.getNextQualified(0));
			for(AttributeMatchPath mp : candidateDefinitionPathes){
			    if(mp != qAttributeMatchPath)mp.recycle();
			}
			parent.addAttribute(qAttributeMatchPath);
			if(temporaryMessageStorage != null){
                for(int i = 0;  i < temporaryMessageStorage.length; i++){
                    if(temporaryMessageStorage[i] != null){
                        /*temporaryMessageStorage[i].setDiscarded(true);*/
                        temporaryMessageStorage[i].clear();
                    }
                }
            }
		}else if(qualifiedCount > 1){
			parent.addAttribute(candidateDefinitionPathes, localCandidatesConflictHandler.getDisqualified(), temporaryMessageStorage);
		}
	}

    boolean functionalEquivalent(ComparableAEH other){
        return other.functionalEquivalent(this);
    }
    
    boolean functionalEquivalent(AttributeDefinitionHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAmbiguousAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnknownAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(AttributeConcurrentHandler other){
        return other.functionalEquivalent(candidates);
	}    
	private boolean functionalEquivalent(List<CandidateAttributeValidationHandler> otherCandidates){
		int candidatesCount = candidates.size();
		if(candidatesCount != otherCandidates.size()) return false;
		for(int i = 0; i < candidatesCount; i++){
			if(!candidates.get(i).functionalEquivalent(otherCandidates.get(i))) return false;
		}
		return true;
	}
	boolean functionalEquivalent(AttributeParallelHandler other){
        return false;
    }
    boolean functionalEquivalent(AttributeDefaultHandler other){
        return false;
    }
    
	public String toString(){
		return "AttributeConcurrentHandler candidates "+candidates;
	}	
}