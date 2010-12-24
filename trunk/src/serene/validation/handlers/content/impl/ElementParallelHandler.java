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

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ContextErrorHandlerManager;

import sereneWrite.MessageWriter;

class ElementParallelHandler extends CandidatesEEH{	
	ExternalConflictHandler candidatesConflictHandler;
	
	ArrayList<ComparableEEH> individualHandlers;
		
	ComparableEEH uniqueSample;
	
	State state;
	State common;
	State conflict;
	
	CandidatesEEH parent;
	
	ElementParallelHandler(MessageWriter debugWriter){
		super(debugWriter);
		individualHandlers = new ArrayList<ComparableEEH>(); 	
		common = new Common();
		conflict = new Conflict();
	}
	
	void init(ExternalConflictHandler candidatesConflictHandler, CandidatesEEH parent){
		individualHandlers.clear();
		this.candidatesConflictHandler = candidatesConflictHandler;
		this.parent = parent;
		init((ContextErrorHandlerManager)parent);		
		state  = common;
	}
	
	public void recycle(){
		//TODO need some way to remember what MessageHandler must not be recycled
		//because it was set to the Candidate as Common or Conflict messages
		//Mind you, warnings are actually neither!!!
		
		candidatesConflictHandler = null;
		uniqueSample = null;
		
		for(ElementEventHandler individualHandler : individualHandlers){
			individualHandler.recycle();
		}
		individualHandlers.clear();
		recycleErrorHandlers();
		
		state = null;
		parent = null;		
		pool.recycle(this);
	}		
	
	void add(ComparableEEH individualHandler){		
		state.add(individualHandler);			
	}
	
	public ValidatingEEH getParentHandler(){
		return parent;			
	}
	public ComparableEEH handleStartElement(String qName, String namespace, String name){
		ComparableEEH next = state.handleStartElement(qName, namespace, name, this);
		return next;
	}
	
	// called from the ValidatorHandlerImpl
	public void handleAttributes(Attributes attributes, Locator locator){
		for(int i = 0; i < attributes.getLength(); i++){
			String attributeQName = attributes.getQName(i);
			String attributeNamespace = attributes.getURI(i); 
			String attributeName = attributes.getLocalName(i);
			String attributeValue = attributes.getValue(i);
			validationItemLocator.newAttribute(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), attributeQName);
			state.handleAttribute(attributeQName, attributeNamespace, attributeName, attributeValue);
			validationItemLocator.closeAttribute();
		}		
	}
	
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void handleAttribute(String qName, String namespace, String name, String value){
		state.handleAttribute(qName, namespace, name, value);
	}
	
	
	// called from the ValidatorHandlerImpl
	public void handleEndElement(Locator locator) throws SAXException{
		state.handleEndElement(locator);
	}	
	
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void validateContext(){
		state.validateContext();
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void reportContextErrors(Locator locator) throws SAXException{
		state.reportContextErrors(locator);
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void validateInContext(){
		state.validateInContext();
	}		
	public void handleCharacters(char[] chars){	
		state.handleCharacters(chars);
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
		return other.functionalEquivalent(individualHandlers);		
	}
	
	private boolean functionalEquivalent(List<ComparableEEH> otherIndividualHandlers){
		int individualHandlersCount = individualHandlers.size();
		if(individualHandlersCount != otherIndividualHandlers.size()) return false;
		for(int i = 0; i < individualHandlersCount; i++){
			if(!individualHandlers.get(i).functionalEquivalent(otherIndividualHandlers.get(i))) return false;
		}
		return true;
	}
	
	public boolean functionalEquivalent(ElementCommonHandler other){		
		return false;		
	}
	public String toString(){
		if( state != null)	return state.toString();
		else return contextToString();
	}
	
	String contextToString(){
		String s = "[";
		for(int i = 0; i < individualHandlers.size(); i++){
			if(candidatesConflictHandler.isDisqualified(i)) s+= "disqualified ";
			s+=(individualHandlers.get(i).toString()+", ");
		}
		if(individualHandlers.size() > 0)s = s.substring(0, s.length()-2)+"]";
		else s = s+"]";
		return "ElementParallelHandler individualHandlers"+s+" "+candidatesConflictHandler.toString()+" state";
	}
	abstract class State{
		abstract void add(ComparableEEH individualHandler);
		abstract ComparableEEH handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance);	
		abstract void handleAttribute(String qName, String namespace, String name, String value);
		abstract void handleEndElement(Locator locator) throws SAXException;
		abstract void validateContext();	
		abstract void reportContextErrors(Locator locator) throws SAXException;
		abstract void validateInContext();
		abstract void handleCharacters(char[] chars);
		
	}
	
	class Common extends State{
		void add(ComparableEEH individualHandler){
			if(uniqueSample == null )uniqueSample = individualHandler;
			else if(!individualHandler.functionalEquivalent(uniqueSample)) state = conflict;
			if(!candidatesConflictHandler.isDisqualified(individualHandlers.size()))uniqueSample = individualHandler;		
			individualHandlers.add(individualHandler);	
		}
		ElementCommonHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance){			
			ElementCommonHandler next = pool.getElementCommonHandler(candidatesConflictHandler, individualHandlers.size(), instance);
			next.add(uniqueSample.handleStartElement(qName, namespace, name));
			return next;
		}
		void handleAttribute(String qName, String namespace, String name, String value){
			if(uniqueSample instanceof ValidatingEEH){				
				ValidatingEEH sample = (ValidatingEEH)uniqueSample;
				sample.setValidation();
				sample.handleAttribute(qName, namespace, name, value);
				sample.restorePreviousState();
			} 
		}		
		void handleEndElement(Locator locator) throws SAXException{			
			validateContext();
			reportContextErrors(locator);
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
		void handleCharacters(char[] chars){
			//uniqueSample.handleCharacters(chars, validationContext, locator);
			if(uniqueSample instanceof ValidatingEEH){				
				ValidatingEEH sample = (ValidatingEEH)uniqueSample;
				sample.setValidation();
				sample.handleCharacters(chars);
				sample.restorePreviousState();
			}
		}
		
		public String toString(){
			return contextToString()+ " COMMON";
		}
	}
	
	class Conflict extends State{
		void add(ComparableEEH individualHandler){					
			individualHandlers.add(individualHandler);	
		}
		ElementParallelHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance){			
			ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, instance);
			for(int i = 0; i < individualHandlers.size(); i++){	
				next.add(individualHandlers.get(i).handleStartElement(qName, namespace, name));			
			}	
			return next;
		}
		void handleAttribute(String qName, String namespace, String name, String value){		
			int candidatesCount = individualHandlers.size();
			for(int i = 0; i < candidatesCount; i++){		
				if(!candidatesConflictHandler.isDisqualified(i)) individualHandlers.get(i).handleAttribute(qName, namespace, name, value);			
			}
		}
		void handleEndElement(Locator locator) throws SAXException{
			validateContext();
			reportContextErrors(locator);
			validateInContext();
		}
		
		void validateContext(){
			for(int i = 0; i < individualHandlers.size(); i++){
				if(!candidatesConflictHandler.isDisqualified(i))individualHandlers.get(i).validateContext();
			}
		}				
		void reportContextErrors(Locator locator) throws SAXException{
			if(commonErrorHandler != null)
				commonErrorHandler.handle(validationItemLocator.getQName(), locator);
			// TODO make sure it all makes sense, 
			// you might need to return after common if not null
			// or it might not be necessary here
			for(int i = 0; i < individualHandlers.size(); i++){
				individualHandlers.get(i).reportContextErrors(locator);
			}
		}		
		
		void validateInContext(){			
			for(int i = 0; i < individualHandlers.size(); i++){
				if(!candidatesConflictHandler.isDisqualified(i))individualHandlers.get(i).validateInContext();
			}
		}	
		void handleCharacters(char[] chars){
			for(int i = 0; i < individualHandlers.size(); i++){				
				if(!candidatesConflictHandler.isDisqualified(i)) individualHandlers.get(i).handleCharacters(chars);
			}
		}
				
		public String toString(){
			return contextToString()+" CONFLICT";
		}
	}
}