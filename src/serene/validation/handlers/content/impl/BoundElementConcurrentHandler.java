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
import java.util.Map;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import serene.validation.schema.simplified.SElement;

import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.MessageReporter;
import serene.validation.handlers.error.ConflictMessageReporter;

import serene.validation.handlers.content.BoundElementHandler;

import serene.validation.handlers.match.ElementMatchPath;

import serene.bind.BindingModel;
import serene.bind.util.QueuePool;
import serene.bind.util.Queue;

class BoundElementConcurrentHandler extends ElementConcurrentHandler implements BoundElementHandler{
	BindingModel bindingModel;
	Queue queue;
	QueuePool queuePool;
	int queueStartEntry;
	int queueEndEntry;

	boolean mayRecycleCandidateQueues;
	
	BoundElementConcurrentHandler(){
		super();		
		
		queueStartEntry = -1;
	    queueEndEntry = -1;
	}
	
	void init(List<ElementMatchPath> cdp,  BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, QueuePool queuePool){		
		this.parent = parent;
		this.candidateDefinitionPathes.addAll(cdp);
        localCandidatesConflictErrorHandler.init(activeInputDescriptor); 
		init((ContextErrorHandlerManager)parent);
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.queuePool = queuePool;
		
		startElementBinding();
				
		for(int i = 0; i < candidateDefinitionPathes.size(); i++){			    
			BoundElementValidationHandler candidate = pool.getElementValidationHandler(candidateDefinitionPathes.get(i), parent, bindingModel, queuePool.getQueue(), queuePool);
			candidate.setCandidateIndex(i);
            candidate.setCandidate(true);
            candidate.setCandidatesConflictErrorHandler(localCandidatesConflictErrorHandler);
			candidate.setContextErrorHandlerIndex(CONFLICT);
			candidates.add(candidate);	
		}
        localCandidatesConflictErrorHandler.setCandidates(candidateDefinitionPathes);
        candidatesConflictHandler.init(candidateDefinitionPathes.size());	
        
        mayRecycleCandidateQueues = true;
	}
	
	public void recycle(){
	    if(mayRecycleCandidateQueues)recycleCandidateQueues();
	    
		for(ElementValidationHandler candidate : candidates){
			candidate.recycle();
		}
		candidates.clear();	
		candidateDefinitionPathes.clear();		
		candidatesConflictHandler.clear();
        localCandidatesConflictErrorHandler.clear(false);
		resetContextErrorHandlerManager();
		
		parent = null;
		
		bindingModel = null;
		queue = null;
		queuePool = null;
		
		queueStartEntry = -1;
	    queueEndEntry = -1;
	    
		pool.recycle(this);
	}
	
	private void recycleCandidateQueues(){
	    for(int i = 0; i < candidates.size(); i++){				
			Queue qq = ((BoundElementValidationHandler)candidates.get(i)).getQueue();
			qq.recycle();
		}
	}
	
	public void startElementBinding(){	    
	    queueStartEntry = queue.addStartElement(null);	
		// Start record with null task, just to create the entry and get the index.
	}
	
	
	public void characterContentBinding(String cc){
		// delegated to every qualified candidate by the fact that 
		// the characterContentBinding is done there and so is the binding  
		throw new UnsupportedOperationException();
	}
	
	public void endElementBinding(){
		
		
		for(int i = 0; i < candidates.size(); i++){
            ((BoundElementValidationHandler)candidates.get(i)).endElementBinding();
        }
        if(getConflictResolutionId() == MessageReporter.RESOLVED){
            int qual = candidatesConflictHandler.getNextQualified(0);
            BoundElementValidationHandler cc = (BoundElementValidationHandler)candidates.get(qual);
			Queue q = cc.getQueue();
			int size = q.getSize();
			queue.registerReservation(queueStartEntry, size);
			queueEndEntry = queueStartEntry + size -1;
			queue.useReservation(queueStartEntry, q, 0, size-1);
        }
	}
	
	
	public Queue getQueue(){
	    return queue;
	}
    public int getQueueStartEntryIndex(){
        return queueStartEntry;
    }
    public int getQueueEndEntryIndex(){
        return queueEndEntry;
    }
    public void queuecoppy(Queue qq, int sei, int eei){
        queue.registerReservation(queueStartEntry, eei-sei+1);
        queueEndEntry = queueStartEntry + eei - sei;
        queue.useReservation(queueStartEntry, qq, sei, eei);
    }
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{
		BoundElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, localCandidatesConflictErrorHandler, this, bindingModel,queue, queuePool);					
		for(int i = 0; i < candidates.size(); i++){			
			ComparableEEH candidate = candidates.get(i);
			next.add(candidate.handleStartElement(qName, namespace, name, restrictToFileName));			
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
    
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{	    		
		validateContext();	
		reportContextErrors(restrictToFileName, locator);	
		/*elementTasksBinding();*/
		endElementBinding();
		validateInContext();		
	}	
		
		
	void validateInContext(){
	    	    
	    int conflictResolutionIndex = getConflictResolutionId();
		if(conflictResolutionIndex == MessageReporter.UNRESOLVED){
			// Shift all with errors, hope the parent context disqualifies all but 1
			// Why shift, they all have errors already??? 
			// Maybe the parent actually expects one of them and not shifting 
			// results in a fake error.	
			prepareQueueForConflictHandling();
			((BoundElementValidationHandler)parent).addChildElement(candidateDefinitionPathes, contextErrorHandler[contextErrorHandlerIndex].getConflictMessageReporter(), bindingModel, queue, queueStartEntry, queueEndEntry, mapCandidateDefinitionToQueue());
			mayRecycleCandidateQueues = false;
		}else if(conflictResolutionIndex == MessageReporter.RESOLVED){
			ElementMatchPath qElementMatchPath = candidateDefinitionPathes.get(candidatesConflictHandler.getNextQualified(0));
			for(ElementMatchPath mp : candidateDefinitionPathes){
			    if(mp != qElementMatchPath)mp.recycle();
			}
			parent.addChildElement(qElementMatchPath);
		}else if(conflictResolutionIndex == MessageReporter.AMBIGUOUS){
			// TODO Maybe a warning
			// Shift all without errors, hope the parent conflict disqualifies all but one
			prepareQueueForConflictHandling();
			ConflictMessageReporter cmr = null;
			if(contextErrorHandler[contextErrorHandlerIndex] != null) cmr = contextErrorHandler[contextErrorHandlerIndex].getConflictMessageReporter();
			((BoundElementValidationHandler)parent).addChildElement(candidateDefinitionPathes, candidatesConflictHandler, cmr, bindingModel, queue, queueStartEntry, queueEndEntry, mapCandidateDefinitionToQueue());
			mayRecycleCandidateQueues = false;
		}
	}
	
	private void prepareQueueForConflictHandling(){
	    BoundElementValidationHandler cc = (BoundElementValidationHandler)candidates.get(0);// they must all have the same length and that's what matters here
	    Queue q = cc.getQueue();
        int size = q.getSize();
        queue.registerReservation(queueStartEntry, size);
        queueEndEntry = queueStartEntry + size -1;
	}
	
	void validateInContext(BoundElementConcurrentHandler reper){
	    int conflictResolutionIndex = getConflictResolutionId();
	    int reperQueueEndEntry = reper.getQueueEndEntryIndex();
		if(conflictResolutionIndex == MessageReporter.UNRESOLVED){
			// Shift all with errors, hope the parent context disqualifies all but 1
			// Why shift, they all have errors already??? 
			// Maybe the parent actually expects one of them and not shifting 
			// results in a fake error.			
			prepareQueueForConflictHandling(reperQueueEndEntry);
			((BoundElementValidationHandler)parent).addChildElement(candidateDefinitionPathes, reper.getConflictMessageReporter(), bindingModel, queue, queueStartEntry, queueEndEntry, mapCandidateDefinitionToQueue());
			mayRecycleCandidateQueues = false;
		}else if(conflictResolutionIndex == MessageReporter.RESOLVED){
			ElementMatchPath qElementMatchPath = candidateDefinitionPathes.get(candidatesConflictHandler.getNextQualified(0));
			for(ElementMatchPath mp : candidateDefinitionPathes){
			    if(mp != qElementMatchPath)mp.recycle();
			}
			parent.addChildElement(qElementMatchPath);
		}else if(conflictResolutionIndex == MessageReporter.AMBIGUOUS){
			// TODO Maybe a warning
			// Shift all without errors, hope the parent conflict disqualifies all but one
			prepareQueueForConflictHandling(reperQueueEndEntry);
			ConflictMessageReporter cmr = reper.getConflictMessageReporter();
			/*if(contextErrorHandler[contextErrorHandlerIndex] != null) cmr = contextErrorHandler[contextErrorHandlerIndex].getConflictMessageReporter();*/
			((BoundElementValidationHandler)parent).addChildElement(candidateDefinitionPathes, candidatesConflictHandler, cmr, bindingModel, queue, queueStartEntry, queueEndEntry, mapCandidateDefinitionToQueue());
			mayRecycleCandidateQueues = false;
		}
	}
	
	private void prepareQueueForConflictHandling(int reperQueueEndEntry){
        int size = reperQueueEndEntry - queueStartEntry + 1;
        queue.registerReservation(queueStartEntry, size);
        queueEndEntry = reperQueueEndEntry;
	}
	
	private HashMap<SElement, Queue> mapCandidateDefinitionToQueue(){
		HashMap<SElement, Queue> map = new HashMap<SElement, Queue>();
		for(int i = 0; i < candidates.size(); i++){
			//if(!candidatesConflictHandler.isDisqualified(i)){				
				map.put(candidateDefinitionPathes.get(i).getElement(), ((BoundElementValidationHandler)candidates.get(i)).getQueue());
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