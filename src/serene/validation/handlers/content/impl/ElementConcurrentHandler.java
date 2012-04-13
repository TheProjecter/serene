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
import serene.validation.handlers.error.MessageReporter;
import serene.validation.handlers.error.ConflictMessageReporter;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptorPool;

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
        localCandidatesConflictErrorHandler.init(activeInputDescriptor);         
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
        candidatesConflictHandler.init(candidateDefinitions.size());
	}
	    
	public void recycle(){
		for(ElementValidationHandler candidate : candidates){
			candidate.recycle();
		}
		candidates.clear();	
		candidateDefinitions.clear();		
		candidatesConflictHandler.clear();
        localCandidatesConflictErrorHandler.clear(false);
		resetContextErrorHandlerManager();
		
		parent = null;
		
		pool.recycle(this);
	}
    
    
	ExternalConflictHandler getConflictHandler(){
	    return candidatesConflictHandler;
	}
	
	void copyDisqualifiedCandidates(ExternalConflictHandler otherHandler){
	    candidatesConflictHandler.copyDisqualified(otherHandler);
	}
	//elementHandler	
	//--------------------------------------------------------------------------
	public ElementEventHandler getParentHandler(){
		return parent;
	}
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{		
		ElementParallelHandler next = pool.getElementParallelHandler(candidatesConflictHandler, localCandidatesConflictErrorHandler, this);					
		for(int i = 0; i < candidates.size(); i++){			
			ComparableEEH candidate = candidates.get(i);
			next.add(candidate.handleStartElement(qName, namespace, name, restrictToFileName));			
		}
        localCandidatesConflictErrorHandler.endValidationStage();
		return next;
	}
	
	public void handleAttributes(Attributes attributes, Locator locator) throws SAXException{
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
            
            localCandidatesConflictErrorHandler.endValidationStage();
                        
			inputStackDescriptor.popAttribute();
			aph.recycle();            
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
    
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{		
		validateContext();
		reportContextErrors(restrictToFileName, locator);
		validateInContext();		
	}	
	
	void validateContext() throws SAXException{        
		int candidatesCount = candidates.size();
		for(int i = 0; i < candidatesCount; i++){
			//if(!candidatesConflictHandler.isDisqualified(i))
            candidates.get(i).validateContext();
		}        
        localCandidatesConflictErrorHandler.endValidationStage();
	}		
	
	void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{
        for(int i = 0; i < candidates.size(); i++){
			//if(!candidatesConflictHandler.isDisqualified(i))
            candidates.get(i).reportContextErrors(restrictToFileName, locator);
		}        
        localCandidatesConflictErrorHandler.endValidationStage(); 
        localCandidatesConflictErrorHandler.endContextValidation();
        
        boolean mustReport = localCandidatesConflictErrorHandler.mustReport();
        if(mustReport){            
            if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
            localCandidatesConflictErrorHandler.handle(ContextErrorHandler.ELEMENT, inputStackDescriptor.getItemDescription(), restrictToFileName, locator, contextErrorHandler[contextErrorHandlerIndex]);
        }else{
            //System.out.println(hashCode()+" MUST NOT REPORT");
            //localCandidatesConflictErrorHandler.clear(true);
        }
	}
	
	void discardContextErrors(){
        for(int i = 0; i < candidates.size(); i++){
			//if(!candidatesConflictHandler.isDisqualified(i))
            candidates.get(i).discardContextErrors();
		}        
        localCandidatesConflictErrorHandler.clear(true);
	}
	
	
	void validateInContext(){	
	    int conflictResolutionIndex = getConflictResolutionId();
	    	    
		if(conflictResolutionIndex == MessageReporter.UNRESOLVED){						
			// Shift all with errors, hope the parent context disqualifies all but 1
			// Why shift, they all have errors already??? 
			// Maybe the parent actually expects one of them and not shifting 
			// results in a fake error.		
			parent.addChildElement(candidateDefinitions, contextErrorHandler[contextErrorHandlerIndex].getConflictMessageReporter());
		}else if(conflictResolutionIndex == MessageReporter.RESOLVED){
			AElement qElement = candidateDefinitions.get(candidatesConflictHandler.getNextQualified(0));
			parent.addChildElement(qElement);
		}else if(conflictResolutionIndex == MessageReporter.AMBIGUOUS){
			// Shift all without errors, hope the parent conflict disqualifies all but one
			ConflictMessageReporter cmr = null;
			if(contextErrorHandler[contextErrorHandlerIndex] != null) cmr = contextErrorHandler[contextErrorHandlerIndex].getConflictMessageReporter();			
			parent.addChildElement(candidateDefinitions, candidatesConflictHandler, cmr);
		}
	}	
	
	void validateInContext(ElementConcurrentHandler reper){	
	    int conflictResolutionIndex = getConflictResolutionId();
	    	    
		if(conflictResolutionIndex == MessageReporter.UNRESOLVED){						
			// Shift all with errors, hope the parent context disqualifies all but 1
			// Why shift, they all have errors already??? 
			// Maybe the parent actually expects one of them and not shifting 
			// results in a fake error.		
			parent.addChildElement(candidateDefinitions, reper.getConflictMessageReporter());
		}else if(conflictResolutionIndex == MessageReporter.RESOLVED){
			AElement qElement = candidateDefinitions.get(candidatesConflictHandler.getNextQualified(0));
			parent.addChildElement(qElement);
		}else if(conflictResolutionIndex == MessageReporter.AMBIGUOUS){
			// Shift all without errors, hope the parent conflict disqualifies all but one
			ConflictMessageReporter cmr = null;
			/*if(contextErrorHandler[contextErrorHandlerIndex] != null) cmr = contextErrorHandler[contextErrorHandlerIndex].getConflictMessageReporter();*/
			cmr = reper.getConflictMessageReporter(); 			
			parent.addChildElement(candidateDefinitions, candidatesConflictHandler, cmr);
		}
	}	
	
	ConflictMessageReporter getConflictMessageReporter(){
	    return contextErrorHandler[contextErrorHandlerIndex].getConflictMessageReporter();
	}
	
	int getConflictResolutionId(){
		int candidatesCount = candidates.size();		
		int qualifiedCount = candidatesCount - candidatesConflictHandler.getDisqualifiedCount();		
		if(qualifiedCount == 0){						
		    return MessageReporter.UNRESOLVED;
		}else if(qualifiedCount == 1){
			return MessageReporter.RESOLVED;
		}else if(qualifiedCount > 1){
			return MessageReporter.AMBIGUOUS;
		}
		throw new IllegalStateException();
	}
	
	public void handleInnerCharacters(CharacterContentDescriptor characterContentDescriptor, CharacterContentDescriptorPool characterContentDescriptorPool) throws SAXException{		
		int candidatesCount = candidates.size();
		for(int i = 0; i < candidatesCount; i++){
			//if(!candidatesConflictHandler.isDisqualified(i)) 
            candidates.get(i).handleInnerCharacters(characterContentDescriptor, characterContentDescriptorPool);
		}
        localCandidatesConflictErrorHandler.endValidationStage();
	}
    public void handleLastCharacters(CharacterContentDescriptor characterContentDescriptor) throws SAXException{		
		int candidatesCount = candidates.size();
		for(int i = 0; i < candidatesCount; i++){
			//if(!candidatesConflictHandler.isDisqualified(i))
            candidates.get(i).handleLastCharacters(characterContentDescriptor);
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
        //return false;		
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