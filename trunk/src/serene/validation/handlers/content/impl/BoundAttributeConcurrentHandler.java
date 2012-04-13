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
import java.util.HashMap;

import org.xml.sax.SAXException;

import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.content.BoundAttributeHandler;

import serene.bind.BindingModel;
import serene.bind.util.Queue;

class BoundAttributeConcurrentHandler extends AttributeConcurrentHandler implements BoundAttributeHandler{
	
	BindingModel bindingModel;
	Queue queue;
	int entry;
	String value;
	
	BoundAttributeConcurrentHandler(){
		super();		
	}

	void init(List<AAttribute> candidateDefinitions, ElementValidationHandler parent, BindingModel bindingModel, Queue queue, int entry){
		this.parent = parent;
		this.candidateDefinitions = candidateDefinitions; 
		for(int i = 0; i < candidateDefinitions.size(); i++){						
			// To each candidate set an ConflictErrorHandler that knows the ExternalConflictHandler
			// and the candidate index. Errors will not be handled and reported.
			// At the end of attribute handling only the number of qualified 
			// candidates left is assesed and the appropriate addAttribute() 
			// is called.
			BoundCandidateAttributeValidationHandler candidate = pool.getCandidateAttributeValidationHandler(candidateDefinitions.get(i), parent, localCandidatesConflictHandler, i, bindingModel, queue, entry);
			candidates.add(candidate);
		}		
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.entry = entry;
	}
	
	public void recycle(){
		bindingModel = null;
		queue = null;
		entry = -1;
		value = null;
		
        for(CandidateAttributeValidationHandler candidate: candidates){
			candidate.recycle();
		}
		localCandidatesConflictHandler.clear();
		candidates.clear();
		pool.recycle(this);
	}
	
	public void attributeBinding(){
		int candidatesCount = candidates.size();
		int qualified = candidatesCount-localCandidatesConflictHandler.getDisqualifiedCount(); 
		if(qualified == 0)return;		
		if(qualified == 1){
			int qual = localCandidatesConflictHandler.getNextQualified(0);
			BoundCandidateAttributeValidationHandler cc = (BoundCandidateAttributeValidationHandler)candidates.get(qual);
			cc.attributeBinding();
		}
	}
	
	public void handleAttribute(String value) throws SAXException{
		validateValue(value);
		attributeBinding();
		validateInContext();
	}

	void validateInContext(){
		int candidatesCount = candidates.size();		
		int qualifiedCount = candidatesCount - localCandidatesConflictHandler.getDisqualifiedCount();		
		if(qualifiedCount == 0){		
			((BoundElementValidationHandler)parent).addAttribute(candidateDefinitions, temporaryMessageStorage, value, queue, entry, bindingModel);
		}else if(qualifiedCount == 1){			
			AAttribute qAttribute = candidateDefinitions.get(localCandidatesConflictHandler.getNextQualified(0));
			parent.addAttribute(qAttribute);
		}else if(qualifiedCount > 1){			
			((BoundElementValidationHandler)parent).addAttribute(candidateDefinitions, localCandidatesConflictHandler.getDisqualified(), temporaryMessageStorage, value, queue, entry, bindingModel);
		}
	}	
	
	/*private HashMap<AAttribute, AttributeBinder> mapCandidateToBinder(){
		HashMap<AAttribute, AttributeBinder> map = new HashMap<AAttribute, AttributeBinder>();
		for(int i = 0; i < candidates.size(); i++){
			if(!localCandidatesConflictHandler.isDisqualified(i)){				
				map.put(candidateDefinitions.get(i), ((BoundCandidateAttributeValidationHandler)candidates.get(i)).getBinder());
			}
		}
		return map;
	}*/
	
	public String toString(){
		return "BoundAttributeConcurrentHandler candidates "+candidates;
	}	
}