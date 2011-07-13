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

import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.CandidatesConflictErrorHandler;

import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

class AttributeConcurrentHandler extends ValidatingAEH{
    ElementValidationHandler parent;	
    
    
	List<AAttribute> candidateDefinitions;
	List<CandidateAttributeValidationHandler> candidates;
	ExternalConflictHandler localCandidatesConflictHandler;
    CandidatesConflictErrorHandler localCandidatesConflictErrorHandler; 
	ValidatorErrorHandlerPool errorHandlerPool;
	
	AttributeConcurrentHandler(MessageWriter debugWriter){
		super(debugWriter);
		candidates = new ArrayList<CandidateAttributeValidationHandler>(3);		
		localCandidatesConflictHandler = new ExternalConflictHandler(debugWriter);
	}
		
	public void recycle(){
		for(CandidateAttributeValidationHandler candidate: candidates){
			candidate.recycle();
		}
		localCandidatesConflictHandler.reset();
		candidates.clear();
		pool.recycle(this);
	}
		
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, ValidatorErrorHandlerPool errorHandlerPool){
		super.init(pool, validationItemLocator);
		this.errorHandlerPool = errorHandlerPool;
	}
	
	void init(List<AAttribute> candidateDefinitions, ElementValidationHandler parent){
		this.parent = parent;
		this.candidateDefinitions = candidateDefinitions; 
		for(int i = 0; i < candidateDefinitions.size(); i++){						
			// To each candidate set a ConflictErrorHandler that knows the ExternalConflictHandler
			// and the candidate index. Errors will not be handled and reported.
			// At the end of attribute handling only the number of qualified 
			// candidates left is assesed and the appropriate addAttribute() 
			// is called.
			CandidateAttributeValidationHandler candidate = pool.getCandidateAttributeValidationHandler(candidateDefinitions.get(i), parent, localCandidatesConflictErrorHandler, i);
			candidates.add(candidate);
		}		
	}
	
    public ElementValidationHandler getParentHandler(){
        return parent;
    }
    
	void validateValue(String value){
		for(int i = 0; i < candidates.size(); i++){
			candidates.get(i).validateValue(value);
		}
	}

	void validateInContext(){
		int candidatesCount = candidates.size();		
		int qualifiedCount = candidatesCount - localCandidatesConflictHandler.getDisqualifiedCount();		
		if(qualifiedCount == 0){						
			// Shift all with errors, hope the parent context disqualifies all but 1
			// Why shift, they all have errors already??? 
			// Maybe the parent actually expects one of them and not shifting 
			// results in a fake error.			
			parent.addAttribute(candidateDefinitions);
		}else if(qualifiedCount == 1){			
			AAttribute qAttribute = candidateDefinitions.get(localCandidatesConflictHandler.getNextQualified(0));
			parent.addAttribute(qAttribute);
		}else if(qualifiedCount > 1){
			// TODO Maybe a warning
			// Shift all without errors, hope the parent conflict disqualifies all but one
			parent.addAttribute(candidateDefinitions, localCandidatesConflictHandler);
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