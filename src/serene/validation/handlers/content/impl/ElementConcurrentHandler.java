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

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.AttributeEventHandler;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.ExternalConflictErrorHandler;
import serene.validation.handlers.error.CandidatesConflictErrorHandler;
import serene.validation.handlers.error.ContextErrorHandler;

import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

class ElementConcurrentHandler extends CandidatesEEH{
	List<AElement> candidateDefinitions;
	List<ElementValidationHandler> candidates;	
	ExternalConflictHandler candidatesConflictHandler;
    CandidatesConflictErrorHandler localCandidatesConflictErrorHandler;
    
	ElementValidationHandler parent;
	
	ElementConcurrentHandler(MessageWriter debugWriter){
		super(debugWriter);		
		candidates = new ArrayList<ElementValidationHandler>(3);        
		candidatesConflictHandler = new ExternalConflictHandler(debugWriter);
        localCandidatesConflictErrorHandler = new CandidatesConflictErrorHandler(candidatesConflictHandler, debugWriter);
	}	
	
	void init(List<AElement> candidateDefinitions, ElementValidationHandler parent){		
		this.parent = parent;
		this.candidateDefinitions = candidateDefinitions;
        localCandidatesConflictErrorHandler.init();         
		init((ContextErrorHandlerManager)parent);		
		for(int i = 0; i < candidateDefinitions.size(); i++){			
			ElementValidationHandler candidate = pool.getElementValidationHandler(candidateDefinitions.get(i), parent);
            candidate.setCandidateIndex(i);
            candidate.setCandidate(true);
            candidate.setCandidatesConflictErrorHandler(localCandidatesConflictErrorHandler);
			candidate.setContextErrorHandlerIndex(CONFLICT);
			candidates.add(candidate);	
		}	
        localCandidatesConflictErrorHandler.setCandidates(candidateDefinitions);
	}
	    
	public void recycle(){
		for(ElementValidationHandler candidate : candidates){
			candidate.recycle();
		}
		candidates.clear();	
		candidateDefinitions.clear();		
		candidatesConflictHandler.reset();
        localCandidatesConflictErrorHandler.clear();
		resetContextErrorHandlerManager();
		
		parent = null;
		
		pool.recycle(this);
	}
		
    /*protected void setContextErrorHandler(){
		if(contextErrorHandlerId == NONE){
			contextErrorHandler = null;		
		}else if(contextErrorHandlerId == VALIDATION){
			//if(validationErrorHandler == null)validationErrorHandler = errorHandlerPool.getValidationErrorHandler();
			//contextErrorHandler = validationErrorHandler;
            throw new IllegalStateException();
		}else if(contextErrorHandlerId == CONFLICT){
			if(externalConflictErrorHandler == null)externalConflictErrorHandler = errorHandlerPool.getExternalConflictErrorHandler(localCandidatesConflictErrorHandler, candidateIndex, isCandidate);			
            contextErrorHandler = externalConflictErrorHandler;
		}else if(contextErrorHandlerId == COMMON){
			throw new IllegalStateException();
		}else if(contextErrorHandlerId == DEFAULT){
			//if(defaultErrorHandler == null)defaultErrorHandler = errorHandlerPool.getDefaultErrorHandler();
			//contextErrorHandler = defaultErrorHandler;
            throw new IllegalStateException();
		}else if(contextErrorHandlerId == EXTERNAL){
			contextErrorHandler = externalHandlerHistory[externalHandlerHistoryIndex];
		}else{
			throw new IllegalStateException();
		}
	}*/
    
    
	//elementHandler	
	//--------------------------------------------------------------------------
	public ElementEventHandler getParentHandler(){
		return parent;
	}
	public ComparableEEH handleStartElement(String qName, String namespace, String name){		
		ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, localCandidatesConflictErrorHandler, this);					
		for(int i = 0; i < candidates.size(); i++){			
			ComparableEEH candidate = candidates.get(i);
			next.add(candidate.handleStartElement(qName, namespace, name));			
		}
        localCandidatesConflictErrorHandler.endValidationStage();
		return next;
	}
	
	public void handleAttributes(Attributes attributes, Locator locator){
		for(int i = 0; i < attributes.getLength(); i++){
			String attributeQName = attributes.getQName(i);
			String attributeNamespace = attributes.getURI(i); 
			String attributeName = attributes.getLocalName(i);
			String attributeValue = attributes.getValue(i);
			validationItemLocator.newAttribute(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), attributeNamespace, attributeName, attributeQName);			           
            AttributeParallelHandler aph = getAttributeHandler(attributeQName, attributeNamespace, attributeName);
            aph.handleAttribute(attributeValue);
            localCandidatesConflictErrorHandler.endValidationStage();
            aph.recycle();
			validationItemLocator.closeAttribute();            
		}		
	}	
	    
    AttributeParallelHandler getAttributeHandler(String qName, String namespace, String name){
        AttributeParallelHandler aph = pool.getAttributeParallelHandler(this, candidatesConflictHandler, localCandidatesConflictErrorHandler);
        for(ElementValidationHandler candidate : candidates){
            ComparableAEH aeh = candidate.getAttributeHandler(qName, namespace, name);
            aph.add(aeh);
        }
        return aph;
    }
    
	public void handleEndElement(Locator locator) throws SAXException{		
		validateContext();
		reportContextErrors(locator);
		validateInContext();		
	}	
	
	void validateContext(){        
		int candidatesCount = candidates.size();
		for(int i = 0; i < candidatesCount; i++){
			//if(!candidatesConflictHandler.isDisqualified(i))
            candidates.get(i).validateContext();
		}        
        localCandidatesConflictErrorHandler.endValidationStage();
	}		
	void reportContextErrors(Locator locator) throws SAXException{
        for(int i = 0; i < candidates.size(); i++){
			//if(!candidatesConflictHandler.isDisqualified(i))
            candidates.get(i).reportContextErrors(locator);
		}        
        localCandidatesConflictErrorHandler.endValidationStage(); 
        localCandidatesConflictErrorHandler.endContextValidation();
        
        boolean mustReport = localCandidatesConflictErrorHandler.mustReport();        
        if(mustReport){
            if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
            localCandidatesConflictErrorHandler.handle(ContextErrorHandler.ELEMENT, validationItemLocator.getQName(), locator, contextErrorHandler[contextErrorHandlerIndex]);
        }
	}
	void validateInContext(){
		int candidatesCount = candidates.size();		
		int qualifiedCount = candidatesCount - candidatesConflictHandler.getDisqualifiedCount();		
		if(qualifiedCount == 0){						
			// Shift all with errors, hope the parent context disqualifies all but 1
			// Why shift, they all have errors already??? 
			// Maybe the parent actually expects one of them and not shifting 
			// results in a fake error.			
			parent.addChildElement(candidateDefinitions);
		}else if(qualifiedCount == 1){
			AElement qElement = candidateDefinitions.get(candidatesConflictHandler.getNextQualified(0));
			parent.addChildElement(qElement);
		}else if(qualifiedCount > 1){
			// TODO Maybe a warning
			// Shift all without errors, hope the parent conflict disqualifies all but one			
			parent.addChildElement(candidateDefinitions, candidatesConflictHandler);
		}
	}	
	public void handleInnerCharacters(char[] chars){		
		int candidatesCount = candidates.size();
		for(int i = 0; i < candidatesCount; i++){
			//if(!candidatesConflictHandler.isDisqualified(i)) 
            candidates.get(i).handleInnerCharacters(chars);
		}
        localCandidatesConflictErrorHandler.endValidationStage();
	}
    public void handleLastCharacters(char[] chars){		
		int candidatesCount = candidates.size();
		for(int i = 0; i < candidatesCount; i++){
			//if(!candidatesConflictHandler.isDisqualified(i)) 
            candidates.get(i).handleLastCharacters(chars);
		}
        localCandidatesConflictErrorHandler.endValidationStage();
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
		return other.functionalEquivalent(candidates);
	}
	private boolean functionalEquivalent(List<ElementValidationHandler> otherCandidates){
		int candidatesCount = candidates.size();
		if(candidatesCount != otherCandidates.size()) return false;
		for(int i = 0; i < candidatesCount; i++){
			if(!candidates.get(i).functionalEquivalent(otherCandidates.get(i))) return false;
		}
		return true;
	}
	boolean functionalEquivalent(ElementParallelHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementCommonHandler other){
		return false;
	}
	//--------------------------------------------------------------------------

	
	
	
	public String toString(){
		String s = "[";
		for(int i = 0; i < candidates.size(); i++){
			if(candidatesConflictHandler.isDisqualified(i)) s+= "disqualified ";
			s+=(candidates.get(i).toString()+", ");
		}
		if(candidates.size() > 0)s = s.substring(0, s.length()-2)+"]";
		else s = s+"]";
		return "ElementConcurrentHandler candidates "+s+" "+candidatesConflictHandler.toString();
		
	}
}