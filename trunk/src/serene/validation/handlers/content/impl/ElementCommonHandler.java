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
		recycleErrorHandlers();
		pool.recycle(this);
	}
	
	void add(ComparableEEH individualHandler){
		if(uniqueHandler != null) throw new IllegalStateException();
		uniqueHandler  = individualHandler;		
		if(uniqueHandler instanceof ValidatingEEH){			
			((ValidatingEEH)uniqueHandler).setValidation();
		}else if(uniqueHandler instanceof ErrorEEH){
			ElementValidationHandler uniqueHandlerParent = (ElementValidationHandler)uniqueHandler.getParentHandler();			
			uniqueHandlerParent.setValidation();
		}
	}
	
	public ElementEventHandler getParentHandler(){
		return parent;
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name){		 
		ElementCommonHandler next = pool.getElementCommonHandler(conflictHandler, candidateCount, this);
		next.add(uniqueHandler.handleStartElement(qName, namespace, name));
		return next;
	}
	
	public void handleAttributes(Attributes attributes, Locator locator){		
		uniqueHandler.handleAttributes(attributes, locator);
	}
	
	void handleAttribute(String qName, String namespace, String name, String value){
		uniqueHandler.handleAttribute(qName, namespace, name, value);
	}
	
	// called from the ValidatorHandlerImpl
	public void handleEndElement(Locator locator) throws SAXException{
		uniqueHandler.validateContext();
		uniqueHandler.reportContextErrors(locator);
		uniqueHandler.validateInContext();
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void validateContext(){
		uniqueHandler.validateContext();
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void reportContextErrors(Locator locator) throws SAXException{
		uniqueHandler.reportContextErrors(locator);
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
	
	public boolean functionalEquivalent(ComparableEEH other){
		return other.functionalEquivalent(this);
	}
	public boolean functionalEquivalent(ElementValidationHandler other){
		return false;
	}		
	public boolean functionalEquivalent(UnrecognizedElementHandler other){
		return false;
	}
	public boolean functionalEquivalent(UnexpectedElementHandler other){
		return false;
	}
	public boolean functionalEquivalent(UnexpectedAmbiguousElementHandler other){
		return false;
	}
	public boolean functionalEquivalent(UnknownElementHandler other){
		return false;
	}
	public boolean functionalEquivalent(ElementDefaultHandler other){
		return false;
	}
	public boolean functionalEquivalent(ElementConcurrentHandler other){
		return false;
	}
	public boolean functionalEquivalent(ElementParallelHandler other){
		return false;
	}
	public boolean functionalEquivalent(ElementCommonHandler other){		
		return other.functionalEquivalent(uniqueHandler, conflictHandler);		
	}
	
	private boolean functionalEquivalent(ComparableEEH otherUniqueHandler, ExternalConflictHandler conflictHandler){		
		return uniqueHandler.functionalEquivalent(otherUniqueHandler);
	}
	
	public String toString(){
		return "ElementCommonHandler uniqueHandler "+uniqueHandler.toString();
	}
	
}