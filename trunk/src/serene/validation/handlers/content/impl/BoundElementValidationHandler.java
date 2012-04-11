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
import java.util.Map;
import java.util.Arrays;
import java.util.BitSet;


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

import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ConflictMessageReporter; 

import serene.validation.handlers.stack.StackHandler; 

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
		if(binder != null)binder.bindName(queue, queueStartEntry, inputStackDescriptor.getNamespaceURI(), inputStackDescriptor.getLocalName(),inputStackDescriptor.getItemDescription());
	}
	
	public void startLocationBinding(){
		int definitionIndex = element.getDefinitionIndex();
		ElementBinder binder = bindingModel.getElementBinder(definitionIndex);
		if(binder != null)binder.bindLocation(queue, queueStartEntry, inputStackDescriptor.getSystemId()+":"+inputStackDescriptor.getLineNumber()+":"+inputStackDescriptor.getColumnNumber());
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
			BoundElementConcurrentHandler next = pool.getElementConcurrentHandler(new ArrayList<AElement>(elementMatches), this, bindingModel, queue, queuePool);
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
			BoundAttributeConcurrentHandler next = pool.getAttributeConcurrentHandler(new ArrayList<AAttribute>(attributeMatches), this, bindingModel, queue, queueStartEntry);
			return next;
		}		
	}	

	public void handleInnerCharacters(char[] chars) throws SAXException{		
		boolean isIgnorable = chars.length == 0 || spaceHandler.isSpace(chars);
        if(!isIgnorable && element.allowsTextContent()){
            hasComplexContent = true;
            CharactersValidationHandler ceh = pool.getCharactersValidationHandler(this, this, this);
            ceh.handleChars(chars, (CharsActiveType)element, hasComplexContent);
            ceh.recycle();
        }else if(!isIgnorable && ! element.allowsChars()){
            unexpectedCharacterContent(inputStackDescriptor.getCurrentItemInputRecordIndex(), element);
        }else{
            // element.allowsDataContent()
            //  || element.allowsValueContent()
            //  || element.allowsListPatternContent()
            
            // append the content, it could be that the element following is an error
            if(chars.length > 0){            
                charContentBuffer.append(chars, 0, chars.length);
                if(charContentLineNumber == -1 ){
                    charContentSystemId = inputStackDescriptor.getSystemId();
                    charContentPublicId = inputStackDescriptor.getPublicId();
                    charContentLineNumber = inputStackDescriptor.getLineNumber();
                    charContentColumnNumber = inputStackDescriptor.getColumnNumber();
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

    public void handleLastCharacters(char[] chars) throws SAXException{
        boolean isIgnorable = chars.length == 0 || spaceHandler.isSpace(chars);
        char[] bufferedContent = charContentBuffer.getCharsArray();
        boolean isBufferIgnorable = bufferedContent.length == 0 || spaceHandler.isSpace(bufferedContent);
		if(hasComplexContent){
            if(!isIgnorable && element.allowsTextContent()){
                CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                cvh.handleChars(chars, (CharsActiveType)element, hasComplexContent);
                cvh.recycle();
            }else if(!isIgnorable || !isBufferIgnorable){
                //unexpectedCharacterContent(inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), element);
                // append the content, it could be that the element following is an error            
                if(chars.length > 0){
                    charContentBuffer.append(chars, 0, chars.length);
                }
                
                // see that the right location is used in the messages
                if(charContentLineNumber != -1){
                    inputStackDescriptor.popCharsContent();
                    inputStackDescriptor.pushCharsContent(charContentSystemId, charContentPublicId, charContentLineNumber, charContentColumnNumber);
                }
                
                CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                cvh.handleChars(charContentBuffer.getCharsArray(), (CharsActiveType)element, hasComplexContent);
                cvh.recycle();            
            }
        }else{
            if(!element.allowsChars()){
                if(!isIgnorable || !isBufferIgnorable){
                    unexpectedCharacterContent(inputStackDescriptor.getCurrentItemInputRecordIndex(), element);
                }
                return;
            }
            
            // append the content, it could be that the element following is an error            
            if(chars.length > 0) charContentBuffer.append(chars, 0, chars.length);
            
            // see that the right location is used in the messages
            if(charContentLineNumber != -1){
                inputStackDescriptor.popCharsContent();
                inputStackDescriptor.pushCharsContent(charContentSystemId, charContentPublicId, charContentLineNumber, charContentColumnNumber);
            }
            
            CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
            cvh.handleChars(charContentBuffer.getCharsArray(), (CharsActiveType)element, hasComplexContent);
            cvh.recycle();
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
	void addChildElement(List<AElement> candidateDefinitions, ConflictMessageReporter conflictMessageReporter, Queue targetQueue, int targetEntry,  Map<AElement, Queue> candidateQueues){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = element.getStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllElements(candidateDefinitions, conflictMessageReporter, targetQueue, targetEntry, candidateQueues);
	}
	void addAttribute(List<AAttribute> candidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue queue, int entry, Map<AAttribute, AttributeBinder> attributeBinders){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = element.getStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllAttributes(candidateDefinitions, temporaryMessageStorage, value, queue, entry, attributeBinders);
	}
	void addChildElement(List<AElement> candidateDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter, Queue targetQueue, int targetEntry,  Map<AElement, Queue> candidateQueues){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = element.getStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllElements(candidateDefinitions, conflictHandler, conflictMessageReporter, targetQueue, targetEntry, candidateQueues);
	}
	void addAttribute(List<AAttribute> candidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue queue, int entry, Map<AAttribute, AttributeBinder> attributeBinders){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = element.getStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllAttributes(candidateDefinitions, disqualified, temporaryMessageStorage, value, queue, entry, attributeBinders);
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