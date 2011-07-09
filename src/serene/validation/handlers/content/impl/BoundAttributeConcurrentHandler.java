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

import serene.validation.schema.active.components.AAttribute;
import serene.validation.handlers.content.BoundAttributeHandler;

import serene.bind.BindingModel;
import serene.bind.AttributeBinder;
import serene.bind.Queue;

import sereneWrite.MessageWriter;

class BoundAttributeConcurrentHandler extends AttributeConcurrentHandler implements BoundAttributeHandler{
	
	BindingModel bindingModel;
	Queue queue;
	int entry;
	String value;
	
	BoundAttributeConcurrentHandler(MessageWriter debugWriter){
		super(debugWriter);		
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
			BoundAttributeValidationHandler candidate = pool.getAttributeValidationHandler(candidateDefinitions.get(i), parent, errorHandlerPool.getAttributeConflictErrorHandler(candidatesConflictHandler, i), bindingModel, queue, entry);
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
		super.recycle();
	}
	
	public void attributeBinding(String value){
		int candidatesCount = candidates.size();
		int qualified = candidatesCount-candidatesConflictHandler.getDisqualifiedCount(); 
		if(qualified == 0)return;		
		if(qualified == 1){
			int qual = candidatesConflictHandler.getNextQualified(0);
			BoundAttributeValidationHandler cc = (BoundAttributeValidationHandler)candidates.get(qual);
			cc.attributeBinding(value);
		}else{
			this.value = value; 
		}
	}
	
	public void handleAttribute(String value){
		validateValue(value);
		attributeBinding(value);
		validateInContext();
	}

	void validateInContext(){
		int candidatesCount = candidates.size();		
		int qualifiedCount = candidatesCount - candidatesConflictHandler.getDisqualifiedCount();		
		if(qualifiedCount == 0){						
			// Shift all with errors, hope the parent context disqualifies all but 1
			// Why shift, they all have errors already??? 
			// Maybe the parent actually expects one of them and not shifting 
			// results in a fake error.			
			((BoundElementValidationHandler)parent).addAttribute(candidateDefinitions, value, queue, entry, mapCandidateToBinder());
		}else if(qualifiedCount == 1){			
			AAttribute qAttribute = candidateDefinitions.get(candidatesConflictHandler.getNextQualified(0));
			parent.addAttribute(qAttribute);
		}else if(qualifiedCount > 1){
			// TODO Maybe a warning
			// Shift all without errors, hope the parent conflict disqualifies all but one			
			((BoundElementValidationHandler)parent).addAttribute(candidateDefinitions, candidatesConflictHandler, value, queue, entry, mapCandidateToBinder());
		}
	}	
	
	private HashMap<AAttribute, AttributeBinder> mapCandidateToBinder(){
		HashMap<AAttribute, AttributeBinder> map = new HashMap<AAttribute, AttributeBinder>();
		for(int i = 0; i < candidates.size(); i++){
			if(!candidatesConflictHandler.isDisqualified(i)){				
				map.put(candidateDefinitions.get(i), ((BoundAttributeValidationHandler)candidates.get(i)).getBinder());
			}
		}
		return map;
	}
	
	public String toString(){
		return "BoundAttributeConcurrentHandler candidates "+candidates;
	}	
}