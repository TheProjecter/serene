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
import sereneWrite.MessageWriter;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.error.ContextErrorHandler;

class StartValidationHandler extends ElementValidationHandler{				
	StartValidationHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
		
	public void recycle(){		
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}
		resetContextErrorHandlerManager();
		element.releaseDefinition();
		pool.recycle(this);
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
		if(!element.allowsElementContent()){
			handleUnexpectedElementHandler(namespace, name, restrictToFileName);
            reportContextErrors(restrictToFileName, inputStackDescriptor);
            return pool.getElementDefaultHandler(this);            
        }            
		List<AElement> elementMatches = matchHandler.matchElement(namespace, name, element);
		int matchCount = elementMatches.size();
		if(matchCount == 0){
			handleUnexpectedElementHandler(namespace, name, restrictToFileName);
            reportContextErrors(restrictToFileName, inputStackDescriptor);
            return pool.getElementDefaultHandler(this);   
		}else if(matchCount == 1){		
            hasComplexContent = true;
			ElementValidationHandler next = pool.getElementValidationHandler(elementMatches.get(0), this);				
			return next;
		}else{
            hasComplexContent = true;
			ElementConcurrentHandler next = pool.getElementConcurrentHandler(new ArrayList<AElement>(elementMatches), this);				
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
	}

    void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{
		if(contextErrorHandler[contextErrorHandlerIndex] != null){
			contextErrorHandler[contextErrorHandlerIndex].handle(ContextErrorHandler.ROOT, inputStackDescriptor.getItemDescription(), element, restrictToFileName, locator);
		}
	}	
	void discardContextErrors() {
		throw new IllegalStateException();
	}
    
	//--------------------------------------------------------------------------
	public String toString(){
		String s = "";
		if(stackHandler == null)s= " null";
		else s= stackHandler.toString();
		return "StartValidationHandler "+element.toString()+" "+s;
	}
	
}

