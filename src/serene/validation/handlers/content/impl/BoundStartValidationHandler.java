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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;


import serene.bind.util.Queue;
import serene.bind.BindingModel;
import serene.bind.util.QueuePool;
import serene.bind.DocumentTask;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SElement;

import serene.validation.handlers.error.ContextErrorHandler;

import serene.validation.handlers.match.ElementMatchPath;

class BoundStartValidationHandler extends BoundElementValidationHandler{				
	BoundStartValidationHandler(){
		super();		
	}
		
	void init(SElement element, BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, QueuePool queuePool){
		this.element = element;		
		stackHandler = stackHandlerPool.getContextStackHandler(element, this);
		
		
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.queuePool = queuePool;
		
		startElementBinding();
		/*queueStartEntry = queue.newRecord();
		qNameBinding();
		startLocationBinding();*/
	}
	
	public void recycle(){	
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}
		resetContextErrorHandlerManager();
		/*element.releaseDefinition();*/
		pool.recycle(this);
	}
	
	public void startElementBinding(){
	    DocumentTask startTask = bindingModel.getStartDocumentTask();	    
	    int recordIndex = inputStackDescriptor.getCurrentItemInputRecordIndex();
	    // TODO 
	    // Here needsStartElementInputData should be checked, but it should be 
	    // done for both tasks and might be more expensive than just recording. 
	    // Anyway, for now, I know it is needed, so I just record it.
	    	    
	    queue.addStartDocument(recordIndex, startTask);
	}
		
	public void characterContentBinding(String cc){
	    throw new IllegalStateException(); 
	}
	
	public void endElementBinding(){
	    DocumentTask endTask = bindingModel.getEndDocumentTask();
	    // TODO 
	    // Here needsStartElementInputData should be checked, but it should be 
	    // done for both tasks and might be more expensive than just recording. 
	    // Anyway, for now, I know it is needed, so I just record it.	    
	    
	    queue.addEndDocument(endTask);
	}
	
	public int getQueueStartEntryIndex(){
        return queueStartEntry;
    }
    public int getQueueEndEntryIndex(){
        return queueEndEntry;
    }
    
    
	
    protected void setContextErrorHandler(){
		if(contextErrorHandlerIndex == NONE){
			contextErrorHandler = null;		
		}else if(contextErrorHandlerIndex == VALIDATION){
			if(contextErrorHandler[VALIDATION] == null)contextErrorHandler[VALIDATION] = errorHandlerPool.getStartErrorHandler();
		}else{
			throw new IllegalStateException();
		}
	}
    
    
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{
		if(!element.allowsElements()){
			handleUnexpectedElementHandler(namespace, name, restrictToFileName);
            reportContextErrors(restrictToFileName, inputStackDescriptor);
            return pool.getElementDefaultHandler(this);
        }
		List<ElementMatchPath> elementMatchPathes = matchHandler.matchElement(namespace, name, element);
		
		int matchCount = elementMatchPathes.size();
		if(matchCount == 0){
			handleUnexpectedElementHandler(namespace, name, restrictToFileName);
            reportContextErrors(restrictToFileName, inputStackDescriptor);
            return pool.getElementDefaultHandler(this);
		}else if(matchCount == 1){			
			BoundElementValidationHandler next = pool.getElementValidationHandler(elementMatchPathes.get(0), this, bindingModel, queue, queuePool);
			return next;
		}else{	
			BoundElementConcurrentHandler next = pool.getElementConcurrentHandler(elementMatchPathes, this, bindingModel, queue, queuePool);
			return next;
		}		
	}
    
    void handleUnexpectedElementHandler(String namespace, String name, boolean restrictToFileName) throws SAXException{
		List<SimplifiedComponent> elementMatches = matchHandler.matchElement(namespace, name);        
		int matchCount = elementMatches.size();
		if(matchCount == 0){            
			unknownElement(inputStackDescriptor.getCurrentItemInputRecordIndex());
		}else if(matchCount == 1){
            unexpectedElement(elementMatches.get(0), inputStackDescriptor.getCurrentItemInputRecordIndex());
		}else{
            unexpectedAmbiguousElement(elementMatches.toArray(new SimplifiedComponent[elementMatches.size()]), inputStackDescriptor.getCurrentItemInputRecordIndex());
		}        
	}
    
    
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{		
		validateContext();
		reportContextErrors(restrictToFileName, locator);
		/*elementTasksBinding();*/
		endElementBinding();
	}

    void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{
		if(contextErrorHandler[contextErrorHandlerIndex] != null){
			contextErrorHandler[contextErrorHandlerIndex].handle(ContextErrorHandler.ROOT, inputStackDescriptor.getItemDescription(), element, restrictToFileName, locator);
		}
	}
	//--------------------------------------------------------------------------
	public String toString(){
		String s = "";
		if(stackHandler == null)s= " null";
		else s= stackHandler.toString();
		return "BoundStartValidationHandler "+element.toString()+" "+s;
	}
	
}

