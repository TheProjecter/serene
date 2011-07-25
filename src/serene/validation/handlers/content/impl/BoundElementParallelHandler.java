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

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.BoundElementHandler;

import serene.validation.handlers.conflict.ExternalConflictHandler;
import serene.validation.handlers.error.CandidatesConflictErrorHandler;

import serene.bind.BindingModel;
import serene.bind.ValidatorQueuePool;
import serene.bind.Queue;

import sereneWrite.MessageWriter;

class BoundElementParallelHandler extends ElementParallelHandler implements BoundElementHandler{
	//TODO you might not need this???
	// See about the queue in the common states.
	BindingModel bindingModel;
	Queue queue;
	ValidatorQueuePool queuePool;
	
	BoundCommon boundCommon;
	BoundConflict boundConflict;
	
	
	BoundElementParallelHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}

    void initStates(){
        boundCommon = new BoundCommon();
		boundConflict = new BoundConflict();
        state = boundCommon;
    }
    
	void init(ExternalConflictHandler candidatesConflictHandler, CandidatesConflictErrorHandler candidatesConflictErrorHandler, CandidatesEEH parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){
		super.init(candidatesConflictHandler, candidatesConflictErrorHandler, parent);
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.queuePool = queuePool;
		
		state  = boundCommon;        
	}
	
	public void recycle(){		
		candidatesConflictHandler = null;
				
		for(ElementEventHandler individualHandler : individualHandlers){
			individualHandler.recycle();
		}
		individualHandlers.clear();
		resetContextErrorHandlerManager();
		
        boundCommon.reset();
		state = boundCommon;
		parent = null;		
		
		bindingModel = null;
		queue = null;
		queuePool = null;
	
		pool.recycle(this);
	}
	
	void add(ComparableEEH individualHandler){
		state.add(individualHandler);			
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
		throw new UnsupportedOperationException();
	}
	public void elementTasksBinding(){
		int individualHandlersCount = individualHandlers.size();
		int qualified = individualHandlersCount-candidatesConflictHandler.getDisqualifiedCount();
		if(qualified == 0)return;		
		for(int i = 0; i < individualHandlersCount; i++){
			ComparableEEH ih = individualHandlers.get(i);
			if(//!candidatesConflictHandler.isDisqualified(i) &&
				ih instanceof BoundElementHandler){
			((BoundElementHandler)ih).elementTasksBinding();
			}
		}
	}
	
	String contextToString(){
		String s = "[";
		for(int i = 0; i < individualHandlers.size(); i++){
			if(candidatesConflictHandler.isDisqualified(i)) s+= "disqualified ";
			s+=(individualHandlers.get(i).toString()+", ");
		}
		if(individualHandlers.size() > 0)s = s.substring(0, s.length()-2)+"]";
		else s = s+"]";
		return "BoundElementParallelHandler individualHandlers"+s+" "+candidatesConflictHandler.toString()+" state";
	}
	
	class BoundCommon extends State{
        ComparableEEH uniqueSample;
        boolean isQualifiedSample;
        
        void reset(){
            uniqueSample = null;
            isQualifiedSample = false;
        }
        
		void add(ComparableEEH individualHandler){
			if(uniqueSample == null ){
                isQualifiedSample = !candidatesConflictHandler.isDisqualified(0);
                uniqueSample = individualHandler;
                if(uniqueSample instanceof ValidatingEEH)((ValidatingEEH)uniqueSample).setContextErrorHandlerIndex(COMMON);
            }else{
                if(!individualHandler.functionalEquivalent(uniqueSample)){
                    state = boundConflict;
                    if(uniqueSample instanceof ValidatingEEH)((ValidatingEEH)uniqueSample).restorePreviousHandler();
                }else if(!isQualifiedSample && !candidatesConflictHandler.isDisqualified(individualHandlers.size())){
                    if(uniqueSample instanceof ValidatingEEH){
                        ((ValidatingEEH)uniqueSample).restorePreviousHandler();
                        ((ValidatingEEH)individualHandler).setContextErrorHandlerIndex(COMMON);
                    }
                    uniqueSample = individualHandler;
                    isQualifiedSample = true;
                }
            }
			individualHandlers.add(individualHandler);				
		}
		ElementParallelHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance, boolean restrictToFileName) throws SAXException{			
			ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, candidatesConflictErrorHandler, instance, bindingModel, queue, queuePool);
			for(int i = 0; i < individualHandlers.size(); i++){	
				next.add(individualHandlers.get(i).handleStartElement(qName, namespace, name, restrictToFileName));			
			}	
			return next;
		}
        void handleAttributes(Attributes attributes, Locator locator){
            uniqueSample.handleAttributes(attributes, locator);
            if(uniqueSample instanceof ValidatingEEH){
                for(int i = 0; i < individualHandlers.size(); i++){
                    if(!candidatesConflictHandler.isDisqualified(i)
                        && !individualHandlers.get(i).equals(uniqueSample)){
                        ValidatingEEH handler = (ValidatingEEH)individualHandlers.get(i);
                        handler.setContextErrorHandlerIndex(DEFAULT);
                        handler.handleAttributes(attributes, locator);
                        handler.restorePreviousHandler();
                    }
                }			
            }
		}
        
        ComparableAEH getAttributeHandler(String qName, String namespace, String name){
            return uniqueSample.getAttributeHandler(qName, namespace, name);
        }
        
		void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
			validateContext();
			reportContextErrors(restrictToFileName, locator);
			elementTasksBinding();		
			validateInContext();
		}
		void validateContext(){			
			uniqueSample.validateContext();
		}
				
		void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{
			ElementEventHandler uniqueSampleParent = uniqueSample.getParentHandler();
            if(uniqueSampleParent instanceof ElementValidationHandler){			
                ElementValidationHandler evhParent = (ElementValidationHandler)uniqueSampleParent;
                evhParent.setContextErrorHandlerIndex(COMMON);
                uniqueSample.reportContextErrors(restrictToFileName, locator);
                evhParent.restorePreviousHandler();
            }
		}
		
		void validateInContext(){
            if(uniqueSample instanceof ErrorEEH){				
				ElementEventHandler uniqueSampleParent = uniqueSample.getParentHandler();
                if(uniqueSampleParent instanceof ElementValidationHandler){
                    ElementValidationHandler evhParent = (ElementValidationHandler)uniqueSampleParent;
                    evhParent.setContextErrorHandlerIndex(COMMON);				
                    uniqueSample.validateInContext();
                    evhParent.restorePreviousHandler();
                }
			}else if(uniqueSample instanceof ValidatingEEH){
				for(int i = 0; i < individualHandlers.size(); i++){
					//if(!candidatesConflictHandler.isDisqualified(i)){
					individualHandlers.get(i).validateInContext();
					//}
				}
			}
		}
		void handleInnerCharacters(char[] chars){
			uniqueSample.handleInnerCharacters(chars);
            if(uniqueSample instanceof BoundElementHandler){
                for(int i = 0; i < individualHandlers.size(); i++){
                    if(!candidatesConflictHandler.isDisqualified(i)
                        && !individualHandlers.get(i).equals(uniqueSample)){
                        ((BoundElementHandler)individualHandlers.get(i)).characterContentBinding(chars);
                    }
                }			
            }
		}
        void handleLastCharacters(char[] chars){
			uniqueSample.handleLastCharacters(chars);
            if(uniqueSample instanceof BoundElementHandler){
                for(int i = 0; i < individualHandlers.size(); i++){
                    if(!candidatesConflictHandler.isDisqualified(i)
                        && !individualHandlers.get(i).equals(uniqueSample)){
                        ((BoundElementHandler)individualHandlers.get(i)).characterContentBinding(chars);
                    }
                }			
            }			
		}
		
		public String toString(){
			return contextToString()+ " BOUND COMMON";
		}
	}
	
	class BoundConflict extends Conflict{		
		ElementParallelHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance, boolean restrictToFileName) throws SAXException{			
			ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, candidatesConflictErrorHandler, instance, bindingModel, queue, queuePool);
			for(int i = 0; i < individualHandlers.size(); i++){	
				next.add(individualHandlers.get(i).handleStartElement(qName, namespace, name, restrictToFileName));			
			}	
			return next;
		}
		void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
			validateContext();
			reportContextErrors(restrictToFileName, locator);
			elementTasksBinding();		
			validateInContext();
		}
		
		public String toString(){
			return contextToString()+" BOUND CONFLICT";
		}
	}
}