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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.error.ContextErrorHandlerManager;

import serene.validation.handlers.content.BoundElementHandler;

import serene.bind.BindingModel;
import serene.bind.ValidatorQueuePool;
import serene.bind.Queue;


import sereneWrite.MessageWriter;

class BoundElementConcurrentHandler extends ElementConcurrentHandler implements BoundElementHandler{
	BindingModel bindingModel;
	Queue queue;
	ValidatorQueuePool queuePool;
	int queueStartEntry;
	
	BoundElementConcurrentHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
	
	void init(List<AElement> candidateDefinitions,  BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		this.parent = parent;
		this.candidateDefinitions = candidateDefinitions;
        localCandidatesConflictErrorHandler.init(); 
		init((ContextErrorHandlerManager)parent);
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.queuePool = queuePool;
		queueStartEntry = queue.newRecord();
		
		for(int i = 0; i < candidateDefinitions.size(); i++){			
			BoundElementValidationHandler candidate = pool.getElementValidationHandler(candidateDefinitions.get(i), parent, bindingModel, queuePool.getQueue(), queuePool);			
			candidate.setCandidateIndex(i);
            candidate.setCandidate(true);
            candidate.setCandidatesConflictErrorHandler(localCandidatesConflictErrorHandler);
			candidate.setContextErrorHandlerIndex(CONFLICT);
			candidates.add(candidate);	
		}
        localCandidatesConflictErrorHandler.setCandidates(candidateDefinitions);		
	}
	
	public void recycle(){
		for(ElementValidationHandler candidate : candidates){
			candidate.recycle();
		}
		candidates.clear();	
		candidateDefinitions.clear();		
		candidatesConflictHandler.reset();
        localCandidatesConflictErrorHandler.clear();
		resetContextErrorHandlerManager();
		
		parent = null;
		
		bindingModel = null;
		queue = null;
		queuePool = null;
		
		pool.recycle(this);
	}
	
	public void qNameBinding(){
		throw new UnsupportedOperationException();
	}	
	public void startLocationBinding(){
		throw new UnsupportedOperationException();
	}
	public void endLocationBinding(){
		throw new UnsupportedOperationException();
	}	
	public void characterContentBinding(char[] chars){
		// delegated to every qualified candidate by the fact that 
		// the characterContentBinding is done there and so is the binding  
		throw new UnsupportedOperationException();
	}
	public void elementTasksBinding(){
		//delegate binding to every qualifed candidate
		//interpret the conflict resolution:
		//		resolved - reserve enough places and add the winner queue
		// 		ambiguu > 0 - reserve enough places in the queue, 
		//						validate in context will delegate the queue 
		//						addition to the InternalConflictResolver		
		int candidatesCount = candidates.size();
		int qualified = candidatesCount-candidatesConflictHandler.getDisqualifiedCount();
		if(qualified == 0){
			for(int i = 0; i < candidatesCount; i++){
				((BoundElementValidationHandler)candidates.get(i)).elementTasksBinding();
			}			
			BoundElementValidationHandler cc = (BoundElementValidationHandler)candidates.get(0);
			Queue q = cc.getQueue();
			int size = q.getSize();
			queue.reserve(queueStartEntry, size);
		}else{
			for(int i = 0; i < candidatesCount; i++){
				if(!candidatesConflictHandler.isDisqualified(i)) ((BoundElementValidationHandler)candidates.get(i)).elementTasksBinding();
			}
			int qual = candidatesConflictHandler.getNextQualified(0);
			BoundElementValidationHandler cc = (BoundElementValidationHandler)candidates.get(qual);
			Queue q = cc.getQueue();
			int size = q.getSize();
			queue.reserve(queueStartEntry, size);
			if(qualified == 1){
				queue.closeReservation(queueStartEntry, q);
			}
		}		
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name){			
        
		BoundElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, localCandidatesConflictErrorHandler, this, bindingModel,queue, queuePool);					
		for(int i = 0; i < candidates.size(); i++){			
			ComparableEEH candidate = candidates.get(i);
			next.add(candidate.handleStartElement(qName, namespace, name));			
		}
        localCandidatesConflictErrorHandler.endValidationStage();
		return next;
	}
	
    BoundAttributeParallelHandler getAttributeHandler(String qName, String namespace, String name){
        BoundAttributeParallelHandler baph = pool.getAttributeParallelHandler(this, candidatesConflictHandler, localCandidatesConflictErrorHandler, bindingModel, queue, queueStartEntry);        
        for(ElementValidationHandler candidate : candidates){            
            ComparableAEH aeh = candidate.getAttributeHandler(qName, namespace, name);
            baph.add(aeh);
        }
        return baph;
    }
    
	public void handleEndElement(Locator locator) throws SAXException{		
		validateContext();	
		reportContextErrors(locator);	
		elementTasksBinding();			
		validateInContext();		
	}	
	
	void validateInContext(){
		int candidatesCount = candidates.size();		
		int qualifiedCount = candidatesCount - candidatesConflictHandler.getDisqualifiedCount();		
		if(qualifiedCount == 0){
			// Shift all with errors, hope the parent context disqualifies all but 1
			// Why shift, they all have errors already, noone in his right mind should bind them??? 
			// Maybe the parent actually expects one of them and not shifting 
			// results in a fake error.						
			//parent.addChildElement(candidateDefinitions);
			((BoundElementValidationHandler)parent).addChildElement(candidateDefinitions, queue, queueStartEntry, mapCandidateToQueue());
		}else if(qualifiedCount == 1){			
			AElement qElement = candidateDefinitions.get(candidatesConflictHandler.getNextQualified(0));
			parent.addChildElement(qElement);
		}else if(qualifiedCount > 1){
			// TODO Maybe a warning
			// Shift all without errors, hope the parent conflict disqualifies all but one
			// need to know:
			// 	candidate - queue mapping
			//	reservationStartEntry = queueStartEntry+1
			((BoundElementValidationHandler)parent).addChildElement(candidateDefinitions, candidatesConflictHandler, queue, queueStartEntry, mapCandidateToQueue());
		}
	}		
	
	private HashMap<AElement, Queue> mapCandidateToQueue(){
		HashMap<AElement, Queue> map = new HashMap<AElement, Queue>();
		for(int i = 0; i < candidates.size(); i++){
			//if(!candidatesConflictHandler.isDisqualified(i)){				
				map.put(candidateDefinitions.get(i), ((BoundElementValidationHandler)candidates.get(i)).getQueue());
			//}
		}
		return map;
	}
	
	public String toString(){
		String s = "[";
		for(int i = 0; i < candidates.size(); i++){
			if(candidatesConflictHandler.isDisqualified(i)) s+= "disqualified ";
			s+=(candidates.get(i).toString()+", ");
		}
		if(candidates.size() > 0)s = s.substring(0, s.length()-2)+"]";
		else s = s+"]";
		return "BoundElementConcurrentHandler candidates "+s+" "+candidatesConflictHandler.toString();
		
	}
}