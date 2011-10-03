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

import serene.validation.handlers.error.CandidatesConflictErrorHandler;
import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.ContextErrorHandler;

import sereneWrite.MessageWriter;

class ElementParallelHandler extends CandidatesEEH{	
	ExternalConflictHandler candidatesConflictHandler;
	
	ArrayList<ComparableEEH> individualHandlers;
		
	State state;
	Common common;
	Conflict conflict;
	
	CandidatesEEH parent;
	
	ElementParallelHandler(MessageWriter debugWriter){
		super(debugWriter);
		individualHandlers = new ArrayList<ComparableEEH>();
        initStates();
	}
	
    void initStates(){
        common = new Common();
		conflict = new Conflict();
        state = common;
    }
    
	void init(ExternalConflictHandler candidatesConflictHandler,  CandidatesConflictErrorHandler candidatesConflictErrorHandler, CandidatesEEH parent){
		individualHandlers.clear();
        this.candidatesConflictHandler = candidatesConflictHandler;
		this.candidatesConflictErrorHandler = candidatesConflictErrorHandler;
		this.parent = parent;
		init((ContextErrorHandlerManager)parent);		
		state  = common;
	}
	
	public void recycle(){
		//TODO need some way to remember what MessageHandler must not be recycled
		//because it was set to the Candidate as Common or Conflict messages
		//Mind you, warnings are actually neither!!!
		
		candidatesConflictHandler = null;
		
		for(ElementEventHandler individualHandler : individualHandlers){
			individualHandler.recycle();
		}
		individualHandlers.clear();
		resetContextErrorHandlerManager();
		
        common.reset();
		state = common;
		parent = null;		
		pool.recycle(this);
	}		
	
	void add(ComparableEEH individualHandler){		
		state.add(individualHandler);			
	}
	
	public ValidatingEEH getParentHandler(){
		return parent;			
	}
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{		 
		ComparableEEH next = state.handleStartElement(qName, namespace, name, this, restrictToFileName);
		return next;
	}
	
	// called from the ValidatorHandlerImpl
	public void handleAttributes(Attributes attributes, Locator locator) throws SAXException{
        state.handleAttributes(attributes, locator);				
	}
	
    // Used by: 
    //  - ElementParallelHandler state Conflict,
    //  - BoundElementParallelHandler state BoundConflict
    // to build up the AttributeParallelHandler.
    ComparableAEH getAttributeHandler(String qName, String namespace, String name){
        return state.getAttributeHandler(qName, namespace, name);
    }
    
	// called from the ValidatorHandlerImpl
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
		state.handleEndElement(restrictToFileName, locator);
	}	
	
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void validateContext() throws SAXException{
		state.validateContext();
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{
		state.reportContextErrors(restrictToFileName, locator);
	}
	// called from a larger conflict( another ElementParallelHandler/ElementCommonHandler)
	void validateInContext(){
		state.validateInContext();
	}		
	public void handleInnerCharacters(char[] chars) throws SAXException{	
		state.handleInnerCharacters(chars);
	}
    public void handleLastCharacters(char[] chars) throws SAXException{	
		state.handleLastCharacters(chars);
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
	
	boolean functionalEquivalent(ElementCommonHandler other){		
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
		abstract ComparableEEH handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance, boolean restrictToFileName) throws SAXException;	
		abstract void handleAttributes(Attributes attributes, Locator locator) throws SAXException;
        abstract ComparableAEH getAttributeHandler(String qName, String namespace, String name);
		abstract void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException;
		abstract void validateContext() throws SAXException;	
		abstract void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException;
		abstract void validateInContext();
		abstract void handleInnerCharacters(char[] chars) throws SAXException;
        abstract void handleLastCharacters(char[] chars) throws SAXException;
	}
	
	class Common extends State{
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
                    ValidatingEEH sample = (ValidatingEEH)uniqueSample;
                    sample.setContextErrorHandlerIndex(COMMON);
                }
            }else{
                if(!individualHandler.functionalEquivalent(uniqueSample)){
                    state = conflict;
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
		ElementCommonHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance, boolean restrictToFileName) throws SAXException{			
			ElementCommonHandler next = pool.getElementCommonHandler(candidatesConflictHandler, individualHandlers.size(), instance);
			next.add(uniqueSample.handleStartElement(qName, namespace, name, restrictToFileName));
			return next;
		}
		void handleAttributes(Attributes attributes, Locator locator) throws SAXException{
            uniqueSample.handleAttributes(attributes, locator);
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
		void handleInnerCharacters(char[] chars) throws SAXException{
			uniqueSample.handleInnerCharacters(chars);
		}
		void handleLastCharacters(char[] chars) throws SAXException{
			uniqueSample.handleLastCharacters(chars);
		}
		
		public String toString(){
			return contextToString()+ " COMMON";
		}	
    }
    
	AttributeParallelHandler getAttributeParallelHandler() {
        return pool.getAttributeParallelHandler(this, candidatesConflictHandler, candidatesConflictErrorHandler);
    }
    int getHashCode(){return hashCode();}
	class Conflict extends State{
		void add(ComparableEEH individualHandler){					
			individualHandlers.add(individualHandler);	
		}
		ElementParallelHandler handleStartElement(String qName, String namespace, String name, ElementParallelHandler instance, boolean restrictToFileName) throws SAXException{			
			ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, candidatesConflictErrorHandler, instance);
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
                String attributeValue = attributes.getValue(i);
                
                validationItemLocator.newAttribute(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), attributeNamespace, attributeName, attributeQName);			           
                AttributeParallelHandler aph = getAttributeHandler(attributeQName, attributeNamespace, attributeName);                
                aph.handleAttribute(attributeValue);
                aph.recycle();
                validationItemLocator.closeAttribute();            
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
		void handleInnerCharacters(char[] chars) throws SAXException{
			for(int i = 0; i < individualHandlers.size(); i++){				
				//if(!candidatesConflictHandler.isDisqualified(i)) 
                individualHandlers.get(i).handleInnerCharacters(chars);
			}
		}
		void handleLastCharacters(char[] chars) throws SAXException{
			for(int i = 0; i < individualHandlers.size(); i++){				
				//if(!candidatesConflictHandler.isDisqualified(i)) 
                individualHandlers.get(i).handleLastCharacters(chars);
			}
		}
		
		public String toString(){
			return contextToString()+" CONFLICT";
		}
	}
}