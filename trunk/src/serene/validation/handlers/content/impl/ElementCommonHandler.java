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
import java.util.ArrayList;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import serene.util.CharsBuffer;

import serene.validation.handlers.content.ElementEventHandler;

import serene.validation.handlers.conflict.ExternalConflictHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ContextErrorHandlerManager;

import sereneWrite.MessageWriter;

class ElementCommonHandler extends UndeterminedEEH{	
	ComparableEEH uniqueHandler; 
	
	ExternalConflictHandler conflictHandler;
	int candidateCount;
	
	ValidatingEEH parent;
	ElementCommonHandler(MessageWriter debugWriter){
		super(debugWriter);	
	}
	
	void init(ExternalConflictHandler conflictHandler, int candidateCount, ValidatingEEH parent){
		this.conflictHandler = conflictHandler;
		this.candidateCount = candidateCount;
		this.parent = parent;
		init((ContextErrorHandlerManager)parent);
	}
	
	public void recycle(){		
		uniqueHandler.recycle();
		uniqueHandler = null;
		resetContextErrorHandlerManager();
		pool.recycle(this);
	}
	
	void add(ComparableEEH individualHandler){
		if(uniqueHandler != null) throw new IllegalStateException();
		uniqueHandler  = individualHandler;		
		if(uniqueHandler instanceof ValidatingEEH){			
			((ValidatingEEH)uniqueHandler).setContextErrorHandlerIndex(VALIDATION);
		}else if(uniqueHandler instanceof ErrorEEH){
			ElementValidationHandler uniqueHandlerParent = (ElementValidationHandler)uniqueHandler.getParentHandler();			
			uniqueHandlerParent.setContextErrorHandlerIndex(VALIDATION);
		}
	}
	
	public ElementEventHandler getParentHandler(){
		return parent;
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{		 
		ElementCommonHandler next = pool.getElementCommonHandler(conflictHandler, candidateCount, this);
		next.add(uniqueHandler.handleStartElement(qName, namespace, name, restrictToFileName));
		return next;
	}
	
	public void handleAttributes(Attributes attributes, Locator locator){		
		uniqueHandler.handleAttributes(attributes, locator);
	}
	
    // Used by: 
    //  - ElementParallelHandler state Conflict,
    //  - BoundElementParallelHandler state BoundConflict
    // to build up the AttributeParallelHandler.
    ComparableAEH getAttributeHandler(String qName, String namespace, String name){
        return uniqueHandler.getAttributeHandler(qName, namespace, name);
    }
    
	// called from the ValidatorHandlerImpl
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
		uniqueHandler.validateContext();
		uniqueHandler.reportContextErrors(restrictToFileName, locator);
		uniqueHandler.validateInContext();
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void validateContext(){
		uniqueHandler.validateContext();
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{
		uniqueHandler.reportContextErrors(restrictToFileName, locator);
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void validateInContext(){		
		uniqueHandler.validateInContext();		
	}
	
	public void handleInnerCharacters(char[] chars){		
		uniqueHandler.handleInnerCharacters(chars);
	}
	public void handleLastCharacters(char[] chars){		
		uniqueHandler.handleLastCharacters(chars);
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
		return other.functionalEquivalent(uniqueHandler, conflictHandler);		
	}
	
	private boolean functionalEquivalent(ComparableEEH otherUniqueHandler, ExternalConflictHandler conflictHandler){		
		return uniqueHandler.functionalEquivalent(otherUniqueHandler);
	}
	
	public String toString(){
		return "ElementCommonHandler uniqueHandler "+uniqueHandler.toString();
	}
	
}