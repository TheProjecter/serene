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
import java.util.Map;
import java.util.Arrays;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.content.AttributeEventHandler;
import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.BoundElementHandler;

import serene.bind.BindingModel;
import serene.bind.ValidatorQueuePool;
import serene.bind.Queue;
import serene.bind.CharacterContentBinder;
import serene.bind.ElementBinder;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;

class BoundElementValidationHandler extends ElementValidationHandler implements BoundElementHandler{
	BindingModel bindingModel;
	Queue queue;
	ValidatorQueuePool queuePool;
	int queueStartEntry;
	
	BoundElementValidationHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
	
	void init(AElement element, BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){
		super.init(element, parent);
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.queuePool = queuePool;
		queueStartEntry = queue.newRecord();
		qNameBinding();
		startLocationBinding();
	}
	
	public void recycle(){	
        charContentBuffer.clear();
        charContentSystemId = null;
        charContentPublicId = null;
        charContentLineNumber = -1;
        charContentColumnNumber = -1;
        hasComplexContent = false;
        
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}
		resetContextErrorHandlerManager();
		//internalConflicts = null; 
		if(contextConflictPool != null)contextConflictPool.clear();
		element.releaseDefinition();
				
		bindingModel = null;
		queue = null;
		queuePool = null;
		
		pool.recycle(this);
	}
	
	Queue getQueue(){
		return queue;
	}
	
	public void qNameBinding(){
		int definitionIndex = element.getDefinitionIndex();
		ElementBinder binder = bindingModel.getElementBinder(definitionIndex);
		if(binder != null)binder.bindName(queue, queueStartEntry, validationItemLocator.getNamespaceURI(), validationItemLocator.getLocalName(),validationItemLocator.getQName());
	}
	
	public void startLocationBinding(){
		int definitionIndex = element.getDefinitionIndex();
		ElementBinder binder = bindingModel.getElementBinder(definitionIndex);
		if(binder != null)binder.bindLocation(queue, queueStartEntry, validationItemLocator.getSystemId()+":"+validationItemLocator.getLineNumber()+":"+validationItemLocator.getColumnNumber());
	}

	public void endLocationBinding(){
		throw new UnsupportedOperationException();
	}
	
	public void characterContentBinding(char[] chars){
		CharacterContentBinder binder = bindingModel.getCharacterContentBinder();		
		if(binder != null)binder.bind(queue, queueStartEntry, new String(chars));		
	}
	
	public void elementTasksBinding(){
		int queueEndEntry = queue.newRecord();
		queue.addIndexCorrespondence(queueEndEntry, queueStartEntry);
		
		int definitionIndex = element.getDefinitionIndex();
		ElementBinder binder = bindingModel.getElementBinder(definitionIndex);
		if(binder != null)binder.bind(queue, queueEndEntry);
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{
		
		if(!element.allowsElementContent()) 
			return getUnexpectedElementHandler(namespace, name);
				
		List<AElement> elementMatches = matchHandler.matchElement(namespace, name, element);
		int matchCount = elementMatches.size();
		if(matchCount == 0){
			return getUnexpectedElementHandler(namespace, name);
		}else if(matchCount == 1){			
			BoundElementValidationHandler next = pool.getElementValidationHandler(elementMatches.get(0), this, bindingModel, queue, queuePool);				
			return next;
		}else{	
			if(contextConflictPool == null)	contextConflictPool = new ContextConflictPool();			
			ContextConflictDescriptor ccd = contextConflictPool.getContextConflictDescriptor(elementMatches);
			BoundElementConcurrentHandler next = pool.getElementConcurrentHandler(ccd.getDefinitions(), this, bindingModel, queue, queuePool);				
			return next;
		}		
	}

	
    ComparableAEH getAttributeHandler(String qName, String namespace, String name){
		if(!element.allowsAttributes()) 
			return getUnexpectedAttributeHandler(namespace, name);
		List<AAttribute> attributeMatches = matchHandler.matchAttribute(namespace, name, element);
		int matchCount = attributeMatches.size();
		if(matchCount == 0){
			return getUnexpectedAttributeHandler(namespace, name);
		}else if(matchCount == 1){			
			BoundAttributeValidationHandler next = pool.getAttributeValidationHandler(attributeMatches.get(0), this, this, bindingModel, queue, queueStartEntry);
			return next;
		}else{	
			// TODO
			// if(contextConflictPool == null)	contextConflictPool = new ContextConflictPool();			
			// ContextConflictDescriptor ccd = contextConflictPool.getContextConflictDescriptor(attributeMatches);
			BoundAttributeConcurrentHandler next = pool.getAttributeConcurrentHandler(attributeMatches, this, bindingModel, queue, queueStartEntry);
			return next;
		}		
	}	

	public void handleInnerCharacters(char[] chars){		
		boolean isIgnorable = chars.length == 0 || spaceHandler.isSpace(chars);
        if(!isIgnorable && element.allowsTextContent()){
            hasComplexContent = true;
            CharacterContentValidationHandler ceh = pool.getCharacterContentValidationHandler(this, this);
            ceh.handleChars(chars, (CharsActiveType)element, hasComplexContent);
            ceh.recycle();
        }else if(!isIgnorable && ! element.allowsChars()){
            unexpectedCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), element);
        }else{
            // element.allowsDataContent()
            //  || element.allowsValueContent()
            //  || element.allowsListPatternContent()
            
            // append the content, it could be that the element following is an error
            if(chars.length > 0){            
                charContentBuffer.append(chars, 0, chars.length);
                if(charContentLineNumber == -1 ){
                    charContentSystemId = validationItemLocator.getSystemId();
                    charContentPublicId = validationItemLocator.getPublicId();
                    charContentLineNumber = validationItemLocator.getLineNumber();
                    charContentColumnNumber = validationItemLocator.getColumnNumber();
                }
            }
        }    
		// Character content binding is not done by the InternalConflictResolver 
		// because there are no differences between different internal pattern
		// configurations, the text is added anyway.
		// Still it would be better to have the "normalized" version resulted 
		// from the processing done for the validation.
		// TODO see about what to do if chars validation results in errors
		characterContentBinding(chars);	
				
	}

    public void handleLastCharacters(char[] chars){
        boolean isIgnorable = chars.length == 0 || spaceHandler.isSpace(chars);
        char[] bufferedContent = charContentBuffer.getCharsArray();
        boolean isBufferIgnorable = bufferedContent.length == 0 || spaceHandler.isSpace(bufferedContent);
		if(hasComplexContent){
            if(!isIgnorable && element.allowsTextContent()){
                CharacterContentValidationHandler ceh = pool.getCharacterContentValidationHandler(this, this);
                ceh.handleChars(chars, (CharsActiveType)element, hasComplexContent);
                ceh.recycle();
            }else if(!isIgnorable || !isBufferIgnorable){
                unexpectedCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), element);
            }
        }else{
            if(!element.allowsChars()){
                if(!isIgnorable || !isBufferIgnorable){
                    unexpectedCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), element);
                }
                return;
            }
            
            // append the content, it could be that the element following is an error            
            if(chars.length > 0) charContentBuffer.append(chars, 0, chars.length);
            
            // see that the right location is used in the messages
            if(charContentLineNumber != -1){
                if(chars.length > 0)validationItemLocator.closeCharsContent();
                validationItemLocator.newCharsContent(charContentSystemId, charContentPublicId, charContentLineNumber, charContentColumnNumber);
            }
            
            CharacterContentValidationHandler ceh = pool.getCharacterContentValidationHandler(this, this);
            ceh.handleChars(charContentBuffer.getCharsArray(), (CharsActiveType)element, hasComplexContent);
            ceh.recycle();
            
            if(chars.length == 0)validationItemLocator.closeCharsContent();
        }
		// Character content binding is not done by the InternalConflictResolver 
		// because there are no differences between different internal pattern
		// configurations, the text is added anyway.
		// Still it would be better to have the "normalized" version resulted 
		// from the processing done for the validation.
		// TODO see about what to do if chars validation results in errors
		characterContentBinding(chars);
	}	
	
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{		
		validateContext();
		reportContextErrors(restrictToFileName, locator);
		elementTasksBinding();
		validateInContext();
	}
	
	// 	candidate - queue mapping
	//	reservationStartEntry = queueStartEntry+1	
	
	// Only used when conflict exists and is unsolved and it is possible that
	// in context validation would lead to the resolution of the conflict and 
	// the binding can happen.
	void addChildElement(List<AElement> candidateDefinitions, Queue targetQueue, int targetEntry,  Map<AElement, Queue> candidateQueues){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllElements(candidateDefinitions, targetQueue, targetEntry, candidateQueues);
	}
	void addAttribute(List<AAttribute> candidateDefinitions, String value, Queue queue, int entry, Map<AAttribute, AttributeBinder> attributeBinders){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllAttributes(candidateDefinitions, value, queue, entry, attributeBinders);
	}
	void addChildElement(List<AElement> candidateDefinitions, ExternalConflictHandler conflictHandler, Queue targetQueue, int targetEntry,  Map<AElement, Queue> candidateQueues){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllElements(candidateDefinitions, conflictHandler, targetQueue, targetEntry, candidateQueues);
	}
	void addAttribute(List<AAttribute> candidateDefinitions, ExternalConflictHandler conflictHandler, String value, Queue queue, int entry, Map<AAttribute, AttributeBinder> attributeBinders){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllAttributes(candidateDefinitions, conflictHandler, value, queue, entry, attributeBinders);
	}
	
	public String toString(){
		String s = "";
		if(stackHandler == null)s= " null";
		else s= stackHandler.toString();
		String e = "";
		if(element == null) e = "null";
		else e = element.toString();
		return "BoundElementValidationHandler "+e+" "+s;
	}
}