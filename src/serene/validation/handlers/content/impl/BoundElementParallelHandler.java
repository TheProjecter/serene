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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.BoundElementHandler;

import serene.validation.handlers.conflict.ExternalConflictHandler;

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
	
	State boundCommon;
	State boundConflict;
	
	
	BoundElementParallelHandler(MessageWriter debugWriter){
		super(debugWriter);	
		boundCommon = new BoundCommon();
		boundConflict = new BoundConflict();		
	}

	void init(ExternalConflictHandler candidatesConflictHandler, CandidatesEEH parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){
		super.init(candidatesConflictHandler, parent);
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.queuePool = queuePool;
		
		state  = boundCommon;
	}
	
	public void recycle(){		
		candidatesConflictHandler = null;
		uniqueSample = null;
		
		for(ElementEventHandler individualHandler : individualHandlers){
			individualHandler.recycle();
		}
		individualHandlers.clear();
		recycleErrorHandlers();
		
		state = null;
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
			if(!candidatesConflictHandler.isDisqualified(i) &&
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
		void add(ComparableEEH individualHandler){
			if(uniqueSample == null)uniqueSample = individualHandler;
			else if(!individualHandler.functionalEquivalent(uniqueSample)) state = boundConflict;
			if(!candidatesConflictHandler.isDisqualified(individualHandlers.size()))uniqueSample = individualHandler;
			individualHandlers.add(individualHandler);				
		}
		ElementParallelHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance){			
			ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, instance, bindingModel, queue, queuePool);
			for(int i = 0; i < individualHandlers.size(); i++){	
				next.add(individualHandlers.get(i).handleStartElement(qName, namespace, name));			
			}	
			return next;
		}	
		void handleAttribute(String qName, String namespace, String name, String value){		
			if(uniqueSample instanceof ValidatingEEH){				
				ValidatingEEH sample = (ValidatingEEH)uniqueSample;
				sample.setValidation();
				sample.handleAttribute(qName, namespace, name, value);
				sample.restorePreviousState();
				for(int i = 0; i < individualHandlers.size(); i++){					
					if(!candidatesConflictHandler.isDisqualified(i) &&
						individualHandlers.get(i) != uniqueSample){
						ValidatingEEH ih = (ValidatingEEH)individualHandlers.get(i);
						ih.setDefault();
						ih.handleAttribute(qName, namespace, name, value);
						ih.restorePreviousState();
					}				
				}
			}			
		}
		void handleEndElement(Locator locator) throws SAXException{			
			validateContext();
			reportContextErrors(locator);
			elementTasksBinding();		
			validateInContext();
		}
		void validateContext(){			
			if(uniqueSample instanceof ValidatingEEH){				
				ValidatingEEH sample = (ValidatingEEH)uniqueSample;
				sample.setValidation();
				sample.validateContext();
				sample.restorePreviousState();
			}
		}
				
		void reportContextErrors(Locator locator) throws SAXException{
			//uniqueSample.reportContextErrors(locator);			
			if(commonErrorHandler != null)
				commonErrorHandler.handle(validationItemLocator.getQName(), locator);
			// TODO make sure it all makes sense, 
			// you might need to return after common if not null
			if(uniqueSample instanceof ValidatingEEH){				
				ValidatingEEH sample = (ValidatingEEH)uniqueSample;
				sample.setValidation();
				sample.reportContextErrors(locator);
				sample.restorePreviousState();
			}
		}
		
		void validateInContext(){
			if(uniqueSample instanceof ErrorEEH){				
				parent.setCommon();				
				ElementValidationHandler uniqueSampleParent = (ElementValidationHandler)uniqueSample.getParentHandler();
				uniqueSampleParent.setExternal(parent.getContextErrorHandler());				
				uniqueSample.validateInContext();
				uniqueSampleParent.restorePreviousState();
				parent.restorePreviousState();
			}else if(uniqueSample instanceof ValidatingEEH){
				for(int i = 0; i < individualHandlers.size(); i++){
					if(!candidatesConflictHandler.isDisqualified(i)){
						individualHandlers.get(i).validateInContext();
					}
				}
			}
		}
		void handleInnerCharacters(char[] chars){
			//uniqueSample.handleCharacters(chars, validationContext, locator);			
			if(uniqueSample instanceof ValidatingEEH){				
				ValidatingEEH sample = (ValidatingEEH)uniqueSample;
				sample.setValidation();
				sample.handleInnerCharacters(chars);
				sample.restorePreviousState();
				for(int i = 0; i < individualHandlers.size(); i++){					
					if(!candidatesConflictHandler.isDisqualified(i) &&
						individualHandlers.get(i) != uniqueSample){
						ValidatingEEH ih = (ValidatingEEH)individualHandlers.get(i);
						ih.setDefault();
						ih.handleInnerCharacters(chars);
						ih.restorePreviousState();
					}				
				}
			}			
		}
        void handleLastCharacters(char[] chars){
			//uniqueSample.handleCharacters(chars, validationContext, locator);			
			if(uniqueSample instanceof ValidatingEEH){				
				ValidatingEEH sample = (ValidatingEEH)uniqueSample;
				sample.setValidation();
				sample.handleLastCharacters(chars);
				sample.restorePreviousState();
				for(int i = 0; i < individualHandlers.size(); i++){					
					if(!candidatesConflictHandler.isDisqualified(i) &&
						individualHandlers.get(i) != uniqueSample){
						ValidatingEEH ih = (ValidatingEEH)individualHandlers.get(i);
						ih.setDefault();
						ih.handleLastCharacters(chars);
						ih.restorePreviousState();
					}				
				}
			}			
		}
		
		public String toString(){
			return contextToString()+ " BOUND COMMON";
		}
	}
	
	class BoundConflict extends Conflict{		
		ElementParallelHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance){			
			ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, instance, bindingModel, queue, queuePool);
			for(int i = 0; i < individualHandlers.size(); i++){	
				next.add(individualHandlers.get(i).handleStartElement(qName, namespace, name));			
			}	
			return next;
		}
		void handleEndElement(Locator locator) throws SAXException{
			validateContext();
			reportContextErrors(locator);
			elementTasksBinding();		
			validateInContext();
		}
		
		public String toString(){
			return contextToString()+" BOUND CONFLICT";
		}
	}
}