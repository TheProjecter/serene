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

import serene.validation.handlers.content.util.CharacterContentDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptorPool;

import serene.bind.BindingModel;
import serene.bind.util.QueuePool;
import serene.bind.util.Queue;

class BoundElementParallelHandler extends ElementParallelHandler implements BoundElementHandler{
	// TODO you might not need this???
	// See about the queue in the common states.
	BindingModel bindingModel;
	Queue queue;
	QueuePool queuePool;
	
	BoundCommon boundCommon;
	BoundConflict boundConflict;
	
	BoundElementParallelHandler(){
		super();		
	}

    void initStates(){
        boundCommon = new BoundCommon();
		boundConflict = new BoundConflict();
        state = boundCommon;
    }
    
	void init(ExternalConflictHandler candidatesConflictHandler, CandidatesConflictErrorHandler candidatesConflictErrorHandler, CandidatesEEH parent, BindingModel bindingModel, Queue queue, QueuePool queuePool){
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
	
	/*public void qNameBinding(){
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
		for(int i = 0; i < individualHandlersCount; i++){
			ComparableEEH ih = individualHandlers.get(i);
			if(//!candidatesConflictHandler.isDisqualified(i) &&
				ih instanceof BoundElementHandler){
			((BoundElementHandler)ih).elementTasksBinding();
			}
		}
	}*/
	
	public void startElementBinding(){
	    throw new IllegalStateException();
	}
	
	public void characterContentBinding(String cc){
	    throw new IllegalStateException();
	}
	
	public void endElementBinding(){
	    ((BoundState)state).endElementBinding();
		/*int individualHandlersCount = individualHandlers.size();
		for(int i = 0; i < individualHandlersCount; i++){
			ComparableEEH ih = individualHandlers.get(i);
			if(//!candidatesConflictHandler.isDisqualified(i) &&
				ih instanceof BoundElementHandler){
			((BoundElementHandler)ih).endElementBinding();
			}
		}*/
	}
	
	public Queue getQueue(){
	    return queue;
	}
    public int getQueueStartEntryIndex(){
        throw new IllegalStateException();
    }
    public int getQueueEndEntryIndex(){
        throw new IllegalStateException();
    }
    public void queuecoppy(Queue qq, int sei, int eei){
        throw new IllegalStateException();
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
	
	abstract class BoundState extends State{
	    abstract void endElementBinding();
	}
	
	class BoundCommon extends BoundState{
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
                if(uniqueSample instanceof ValidatingEEH){
                    ((ValidatingEEH)uniqueSample).setContextErrorHandlerIndex(COMMON);
                }
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
		
		BoundElementCommonHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance, boolean restrictToFileName) throws SAXException{			
			/*ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, candidatesConflictErrorHandler, instance, bindingModel, queue, queuePool);
			for(int i = 0; i < individualHandlers.size(); i++){	
				next.add(individualHandlers.get(i).handleStartElement(qName, namespace, name, restrictToFileName));			
			}	
			return next;*/
			BoundElementCommonHandler next = pool.getBoundElementCommonHandler(candidatesConflictHandler, individualHandlers.size(), instance, bindingModel, queue, queuePool);
			next.add(uniqueSample.handleStartElement(qName, namespace, name, restrictToFileName));
			return next;
		}
        void handleAttributes(Attributes attributes, Locator locator) throws SAXException{
            uniqueSample.handleAttributes(attributes, locator);
            /*if(uniqueSample instanceof ValidatingEEH){
                for(int i = 0; i < individualHandlers.size(); i++){
                    if(!candidatesConflictHandler.isDisqualified(i)
                        && !individualHandlers.get(i).equals(uniqueSample)){
                        ValidatingEEH handler = (ValidatingEEH)individualHandlers.get(i);
                        handler.setContextErrorHandlerIndex(DEFAULT);
                        handler.handleAttributes(attributes, locator);
                        handler.restorePreviousHandler();
                    }
                }			
            }*/
		}
        
		// Used by: 
        //  - ElementParallelHandler state Conflict,
        //  - BoundElementParallelHandler state BoundConflict
        // to build up the AttributeParallelHandler.
        ComparableAEH getAttributeHandler(String qName, String namespace, String name){
            return uniqueSample.getAttributeHandler(qName, namespace, name);
        }
        
		void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
			validateContext();
			reportContextErrors(restrictToFileName, locator);
			/*elementTasksBinding();*/
			endElementBinding();			
			validateInContext();
		}
		void validateContext() throws SAXException{			
			uniqueSample.validateContext();
		}
				
		void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{
			ElementEventHandler uniqueSampleParent = uniqueSample.getParentHandler();
            if(uniqueSampleParent instanceof ElementValidationHandler){			
                ElementValidationHandler evhParent = (ElementValidationHandler)uniqueSampleParent;
                evhParent.setContextErrorHandlerIndex(COMMON);
                uniqueSample.reportContextErrors(restrictToFileName, locator);
                evhParent.restorePreviousHandler();
                for(int i = 0; i < individualHandlers.size(); i++){
                    if(individualHandlers.get(i) != uniqueSample){
                        individualHandlers.get(i).discardContextErrors();
                    }
                }
            }
            
            // if uniqueSample instanceof ElementConcurrentHandler
            // copy it's conflict handler to all individual handlers
            if(uniqueSample instanceof ElementConcurrentHandler){
                ElementConcurrentHandler sample = (ElementConcurrentHandler)uniqueSample;
                ExternalConflictHandler sampleConflictHandler = sample.getConflictHandler();
                for(int i = 0; i < individualHandlers.size(); i++){
                    ComparableEEH e = individualHandlers.get(i);
                    if(e != uniqueSample){
                        ElementConcurrentHandler ech = (ElementConcurrentHandler)e;
                        ech.copyDisqualifiedCandidates(sampleConflictHandler);
                    }
                }
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
			}else if(uniqueSample instanceof BoundElementConcurrentHandler){
			    BoundElementConcurrentHandler sample = (BoundElementConcurrentHandler)uniqueSample;
                uniqueSample.validateInContext();			    
			    for(int i = 0; i < individualHandlers.size(); i++){ 
					if(individualHandlers.get(i) != uniqueSample){
						((BoundElementConcurrentHandler)individualHandlers.get(i)).validateInContext(sample);
					}else{
					    
					}
				}
			}else if(uniqueSample instanceof ValidatingEEH){			    
				for(int i = 0; i < individualHandlers.size(); i++){				    
					//if(!candidatesConflictHandler.isDisqualified(i)){
					individualHandlers.get(i).validateInContext();
					//}
				}
			}
		}
		void handleInnerCharacters(CharacterContentDescriptor characterContentDescriptor, CharacterContentDescriptorPool characterContentDescriptorPool) throws SAXException{
			uniqueSample.handleInnerCharacters(characterContentDescriptor, characterContentDescriptorPool);
            /*if(uniqueSample instanceof BoundElementHandler){
                for(int i = 0; i < individualHandlers.size(); i++){
                    if(!candidatesConflictHandler.isDisqualified(i)
                        && !individualHandlers.get(i).equals(uniqueSample)){
                        ((BoundElementHandler)individualHandlers.get(i)).characterContentBinding();
                    }
                }			
            }*/
		}
        void handleLastCharacters(CharacterContentDescriptor characterContentDescriptor) throws SAXException{
			uniqueSample.handleLastCharacters(characterContentDescriptor);
            /*if(uniqueSample instanceof BoundElementHandler){
                for(int i = 0; i < individualHandlers.size(); i++){
                    if(!candidatesConflictHandler.isDisqualified(i)
                        && !individualHandlers.get(i).equals(uniqueSample)){
                        ((BoundElementHandler)individualHandlers.get(i)).characterContentBinding();
                    }
                }			
            }*/			
		}
		
		void endElementBinding(){
		    //if(uniqueSample instanceof BoundElementHandler){// which it should always be		        
		        BoundElementHandler sample = (BoundElementHandler)uniqueSample;
		        		        
		        sample.endElementBinding();
		        		        
		        int endEntryIndex = sample.getQueueEndEntryIndex();
		        if(endEntryIndex == -1) return;// it means sample hasn't done any binding, happens for Concurrent when ambiguous or unresolved and it's ok, it will be handled in conflict handling
		        int startEntryIndex = sample.getQueueStartEntryIndex();
		        Queue qq = sample.getQueue();
		        
		        for(int i = 0; i < individualHandlers.size(); i++){
                    if(//!candidatesConflictHandler.isDisqualified(i) && 
                        individualHandlers.get(i) != uniqueSample){
                        ((BoundElementHandler)individualHandlers.get(i)).queuecoppy(qq, startEntryIndex, endEntryIndex);
                    }
                }
		    //}
		}
		
		public String toString(){
			return contextToString()+ " BOUND COMMON";
		}		
	}
	
	class BoundConflict extends BoundState{	
	    void add(ComparableEEH individualHandler){					
			individualHandlers.add(individualHandler);	
		}
		ElementParallelHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance, boolean restrictToFileName) throws SAXException{			
			ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, candidatesConflictErrorHandler, instance, bindingModel, queue, queuePool);
			for(int i = 0; i < individualHandlers.size(); i++){	
				next.add(individualHandlers.get(i).handleStartElement(qName, namespace, name, restrictToFileName));			
			}	
			return next;
		}
		void handleAttributes(Attributes attributes, Locator locator) throws SAXException{
            for(int i = 0; i < attributes.getLength(); i++){
                String attributeQName = attributes.getQName(i);
                String attributeNamespace = attributes.getURI(i); 
                String attributeName = attributes.getLocalName(i);
                String attributeType = attributes.getType(i);
                String attributeValue = attributes.getValue(i);                
                inputStackDescriptor.pushAttribute(attributeQName,
			                                    attributeNamespace, 
			                                    attributeName,
			                                    attributeType,
			                                    attributeValue,
			                                    locator.getSystemId(), 
			                                    locator.getPublicId(), 
			                                    locator.getLineNumber(), 
			                                    locator.getColumnNumber());			           
                AttributeParallelHandler aph = getAttributeHandler(attributeQName, attributeNamespace, attributeName);                
                aph.handleAttribute(attributeValue);
                aph.recycle();
                inputStackDescriptor.popAttribute();            
            }	
		}
        
        // Used by: 
        //  - ElementParallelHandler state Conflict,
        //  - BoundElementParallelHandler state BoundConflict
        // to build up the AttributeParallelHandler.
        AttributeParallelHandler getAttributeHandler(String qName, String namespace, String name){            
            AttributeParallelHandler aph = getAttributeParallelHandler();            
            for(ComparableEEH individualHandler : individualHandlers){
                aph.add(individualHandler.getAttributeHandler(qName, namespace, name));
            }
            return aph;
        }
        
		void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
			validateContext();
			reportContextErrors(restrictToFileName, locator);
			/*elementTasksBinding();*/
			endElementBinding();
			validateInContext();
		}
		
		
		void validateContext() throws SAXException{
			for(int i = 0; i < individualHandlers.size(); i++){
				//if(!candidatesConflictHandler.isDisqualified(i))
                individualHandlers.get(i).validateContext();				
			}
		}				
		void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{			
			for(int i = 0; i < individualHandlers.size(); i++){
				individualHandlers.get(i).reportContextErrors(restrictToFileName, locator);
			}
		}		
		
		void validateInContext(){
			for(int i = 0; i < individualHandlers.size(); i++){
				//if(!candidatesConflictHandler.isDisqualified(i))
                individualHandlers.get(i).validateInContext();
			}
		}	
		void endElementBinding(){
            int individualHandlersCount = individualHandlers.size();
            for(int i = 0; i < individualHandlersCount; i++){
                ComparableEEH ih = individualHandlers.get(i);
                if(//!candidatesConflictHandler.isDisqualified(i) &&
                    ih instanceof BoundElementHandler){
                ((BoundElementHandler)ih).endElementBinding();
                }
            }
        }
        
        void handleInnerCharacters(CharacterContentDescriptor characterContentDescriptor, CharacterContentDescriptorPool characterContentDescriptorPool) throws SAXException{
			for(int i = 0; i < individualHandlers.size(); i++){				
				//if(!candidatesConflictHandler.isDisqualified(i)) 
                individualHandlers.get(i).handleInnerCharacters(characterContentDescriptor, characterContentDescriptorPool);
			}
		}
		void handleLastCharacters(CharacterContentDescriptor characterContentDescriptor) throws SAXException{
			for(int i = 0; i < individualHandlers.size(); i++){				
				//if(!candidatesConflictHandler.isDisqualified(i)) 
                individualHandlers.get(i).handleLastCharacters(characterContentDescriptor);
			}
		}
        
		public String toString(){
			return contextToString()+" BOUND CONFLICT";
		}
	}
}