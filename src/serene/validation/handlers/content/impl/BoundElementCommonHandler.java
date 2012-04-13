/*
Copyright 2012 Radu Cernuta 

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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import serene.util.CharsBuffer;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.BoundElementHandler;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptorPool;

import serene.validation.handlers.error.ContextErrorHandlerManager;

import serene.bind.BindingModel;
import serene.bind.util.QueuePool;
import serene.bind.util.Queue;

class BoundElementCommonHandler extends ElementCommonHandler implements BoundElementHandler{	
	BindingModel bindingModel;
	Queue queue;
	QueuePool queuePool;
	
	BoundElementCommonHandler(){
		super();	
	}
		
	void init(ExternalConflictHandler conflictHandler, int candidateCount, ValidatingEEH parent, BindingModel bindingModel, Queue queue, QueuePool queuePool){
		super.init(conflictHandler, candidateCount, parent);
		
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.queuePool = queuePool;
	}
	
	public void recycle(){		
		uniqueHandler.recycle();
		uniqueHandler = null;
		resetContextErrorHandlerManager();
		pool.recycle(this);
	}
	
	
	public ElementEventHandler getParentHandler(){
		return parent;
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{		 
		BoundElementCommonHandler next = pool.getBoundElementCommonHandler(conflictHandler, candidateCount, this, bindingModel, queue, queuePool);
		next.add(uniqueHandler.handleStartElement(qName, namespace, name, restrictToFileName));
		return next;
	}
	
   
	// called from the ValidatorHandlerImpl
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
		uniqueHandler.validateContext();
		uniqueHandler.reportContextErrors(restrictToFileName, locator);
		endElementBinding();
		uniqueHandler.validateInContext();		
	}
	
	// called from a larger conflict( another BoundElementParallelHandler/BoundElementCommonHandler)
	public void startElementBinding(){
	    //((BoundElementCommonHandler)uniqueHandler).startElementBinding();
	    throw new IllegalStateException();
	}
	
	// called from a larger conflict( another BoundElementParallelHandler/BoundElementCommonHandler)
	public void characterContentBinding(String cc){
	    //((BoundElementCommonHandler)uniqueHandler).characterContentBinding(cc);
	    throw new IllegalStateException();
	}
	
	// called from a larger conflict( another BoundElementParallelHandler/BoundElementCommonHandler)
	public void endElementBinding(){		
		((BoundElementHandler)uniqueHandler).endElementBinding();		
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
	
	boolean functionalEquivalent(ComparableEEH other){
		return other.functionalEquivalent(this);
	}
	boolean functionalEquivalent(ElementValidationHandler other){
		return false;
	}		
	boolean functionalEquivalent(UnexpectedElementHandler other){
		return false;
	}
	boolean functionalEquivalent(UnexpectedAmbiguousElementHandler other){
		return false;
	}
	boolean functionalEquivalent(UnknownElementHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementDefaultHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementConcurrentHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementParallelHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementCommonHandler other){		
		return false;		
	}	
	boolean functionalEquivalent(BoundElementCommonHandler other){		
		return other.functionalEquivalent(uniqueHandler, conflictHandler);		
	}
	
	private boolean functionalEquivalent(ComparableEEH otherUniqueHandler, ExternalConflictHandler conflictHandler){		
		return uniqueHandler.functionalEquivalent(otherUniqueHandler);
	}
	
	public String toString(){
		return "BoundElementCommonHandler uniqueHandler "+uniqueHandler.toString();
	}
	
}
