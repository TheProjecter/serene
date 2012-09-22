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

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.content.AttributeEventHandler;
import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.BoundElementHandler;

import serene.validation.handlers.content.util.CharacterContentDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptorPool;

import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ConflictMessageReporter; 

import serene.validation.handlers.stack.StackHandler; 

import serene.bind.BindingModel;
import serene.bind.ElementTask;
import serene.bind.util.QueuePool;
import serene.bind.util.Queue;

import serene.util.CharsBuffer;

class BoundElementValidationHandler extends ElementValidationHandler implements BoundElementHandler{
	BindingModel bindingModel;
	Queue queue;
	QueuePool queuePool;
	int queueStartEntry;
	int queueEndEntry;
	
	CharsBuffer bindCharsBuffer;
	
	BoundElementValidationHandler(){
		super();		
		
		bindCharsBuffer = new CharsBuffer();
		
		queueStartEntry = -1;
	    queueEndEntry = -1;
	}
	
	void init(AElement element, BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, QueuePool queuePool){
		super.init(element, parent);
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.queuePool = queuePool;
		
		startElementBinding();
		/*queueStartEntry = queue.newRecord();
		qNameBinding();
		startLocationBinding();*/
	}
	
	public void recycle(){	
        /*charContentBuffer.clear();
        charContentSystemId = null;
        charContentPublicId = null;
        charContentLineNumber = -1;
        charContentColumnNumber = -1;*/
        hasComplexContent = false;
        
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}
		resetContextErrorHandlerManager();
		element.releaseDefinition();
				
		bindingModel = null;
		//queue.recycle();
		queue = null;
		queuePool = null;
		
		queueStartEntry = -1;
	    queueEndEntry = -1;
		
		pool.recycle(this);
	}
	
	public void startElementBinding(){
	    ElementTask startTask = bindingModel.getStartElementTask(element.getCorrespondingSimplifiedComponent());	    
	    int recordIndex = inputStackDescriptor.getCurrentItemInputRecordIndex();
	    // TODO 
	    // Here needsStartElementInputData should be checked, but it should be 
	    // done for both tasks and might be more expensive than just recording. 
	    // Anyway, for now, I know it is needed, so I just record it.
	    
	    
	    queueStartEntry = queue.addStartElement(recordIndex, startTask);
	}
	
	/*public void qNameBinding(){
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
	}*/
	
	public void characterContentBinding(String cc){
	    // TODO needsCharacterContent from tasks should be checked.
	    queue.addCharacterContent(queueStartEntry, cc);
	}
	
	public void endElementBinding(){
	    ElementTask endTask = bindingModel.getEndElementTask(element.getCorrespondingSimplifiedComponent());
	    queueEndEntry = queue.addEndElement(queueStartEntry, endTask);
	}
	
	/*public void elementTasksBinding(){
		int queueEndEntry = queue.newRecord();
		queue.addIndexCorrespondence(queueEndEntry, queueStartEntry);
		
		int definitionIndex = element.getDefinitionIndex();
		ElementBinder binder = bindingModel.getElementBinder(definitionIndex);
		if(binder != null)binder.bindTasks(queue, queueEndEntry);
	}*/
	
	public Queue getQueue(){
	    return queue;
	}
    public int getQueueStartEntryIndex(){
        return queueStartEntry;
    }
    public int getQueueEndEntryIndex(){
        return queueEndEntry;
    }
    public void queuecoppy(Queue qq, int sei, int eei){
        queue.registerReservation(queueStartEntry, eei-sei+1);
        queueEndEntry = queueStartEntry + eei - sei;
        queue.useReservation(queueStartEntry, qq, sei, eei);
    }
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{
		if(!element.allowsElements()) {
			return getUnexpectedElementHandler(namespace, name);
		}
				
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

	protected ComparableEEH getUnexpectedElementHandler(String namespace, String name){
		List<SimplifiedComponent> elementMatches = matchHandler.matchElement(namespace, name);
		int matchCount = elementMatches.size();
		if(matchCount == 0){
			BoundUnknownElementHandler next = pool.getBoundUnknownElementHandler(this, queue);
			return next;
		}else if(matchCount == 1){
			BoundUnexpectedElementHandler next = pool.getBoundUnexpectedElementHandler(elementMatches.get(0), this, queue);				
			return next;
		}else{					
			BoundUnexpectedAmbiguousElementHandler next = pool.getBoundUnexpectedAmbiguousElementHandler(elementMatches, this, queue);				
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

	public void handleInnerCharacters(CharacterContentDescriptor characterContentDescriptor, CharacterContentDescriptorPool characterContentDescriptorPool) throws SAXException{		
				
		boolean isIgnorable = characterContentDescriptor.isEmpty() || characterContentDescriptor.isSpaceOnly();
        if(!isIgnorable && element.allowsText()){            
            char[] cc = characterContentDescriptor.getCharArrayContent();
            setBindText(cc);
            
            hasComplexContent = true;
            inputStackDescriptor.push(characterContentDescriptor.getStartIndex());
            CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
            cvh.handleChars(cc, element, hasComplexContent);
            inputStackDescriptor.pop();
            cvh.recycle();
        }else if(!isIgnorable && !element.allowsCharsContent()){
            unexpectedCharacterContent(characterContentDescriptor.getStartIndex(), element);            
        }else if(!characterContentDescriptor.isEmpty() && element.allowsCharsContent()){
            // Content is space or more.
            // Element allowsUnstructuredDataContent()
            //          || allowsValueContent()
            //          || allowsListPatternContent()          
            
            // append the content, it could be that the element following is an error
            
            if(localCharacterContentDescriptor == null){
                localCharacterContentDescriptor = characterContentDescriptorPool.getCharacterContentDescriptor();
            }
            localCharacterContentDescriptor.add(characterContentDescriptor.getAllIndexes());
        }		
	}

	private void setBindText(char[] cc){
	    int start = spaceHandler.getHeadSpaceEnd(cc);
	    if(start > 0) start -= 1;
	    int length = spaceHandler.getTailSpaceStart(cc) - start;
	    
	    bindCharsBuffer.append(cc, start, length);
	}
	
	
    public void handleLastCharacters(CharacterContentDescriptor characterContentDescriptor) throws SAXException{
                
        boolean isIgnorable = characterContentDescriptor.isEmpty() || characterContentDescriptor.isSpaceOnly();
        if(hasComplexContent){    
            // No previous text buffered, either has been processed, or there was none and the complex content was established based on elemetn content.
            if(element.allowsText()){
                if(!isIgnorable){
                    char[] cc = characterContentDescriptor.getCharArrayContent();
                    setBindText(cc);  
                    characterContentBinding(bindCharsBuffer.getString());  
                    bindCharsBuffer.clear();
                    
                    inputStackDescriptor.push(characterContentDescriptor.getStartIndex());
                    CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                    cvh.handleChars(cc, element, hasComplexContent);
                    inputStackDescriptor.pop();
                    cvh.recycle();
                }
            }else if(element.allowsCharsContent()){
                // Since 
                //      - hasComplexContent is set to "true" only for ALLOWED elements 
                //      - restrictions don't allow data and elements in the same context
                // this only occurs when there is a choice and there might be excessive 
                // content, so only shift characters if not ignorable.
                // TODO Consider bindign after desambiguation.
                if(localCharacterContentDescriptor != null){
                    boolean localIsIgnorable = localCharacterContentDescriptor.isEmpty() || localCharacterContentDescriptor.isSpaceOnly();
                    if(!isIgnorable || !localIsIgnorable){                    
                        localCharacterContentDescriptor.add(characterContentDescriptor.getAllIndexes());
                        char[] cc = localCharacterContentDescriptor.getCharArrayContent();
                        characterContentBinding(new String(cc));    
                
                        CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                        inputStackDescriptor.push(localCharacterContentDescriptor.getStartIndex());
                        cvh.handleChars(localCharacterContentDescriptor.getCharArrayContent(), element, hasComplexContent);
                        inputStackDescriptor.pop();
                        cvh.recycle();
                    }
                }else if(!isIgnorable){
                    char[] cc = characterContentDescriptor.getCharArrayContent();
                    characterContentBinding(new String(cc)); 
                
                    CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                    if(!characterContentDescriptor.isEmpty()) inputStackDescriptor.push(characterContentDescriptor.getStartIndex());
                    cvh.handleChars(characterContentDescriptor.getCharArrayContent(), element, hasComplexContent);
                    if(!characterContentDescriptor.isEmpty()) inputStackDescriptor.pop();
                    cvh.recycle();
                }
            }else{
                if(!isIgnorable) unexpectedCharacterContent(characterContentDescriptor.getStartIndex(), element);
            }
        }else if(!isIgnorable && !element.allowsCharsContent()){
            unexpectedCharacterContent(characterContentDescriptor.getStartIndex(), element);
        }else if(element.allowsCharsContent()){
            if(localCharacterContentDescriptor != null){
                localCharacterContentDescriptor.add(characterContentDescriptor.getAllIndexes());
                char[] cc = localCharacterContentDescriptor.getCharArrayContent();
                characterContentBinding(new String(cc));                
                
                inputStackDescriptor.push(localCharacterContentDescriptor.getStartIndex());
                CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                cvh.handleChars(cc, element, hasComplexContent);
                inputStackDescriptor.pop();
                cvh.recycle();
            }else{
                char[] cc = characterContentDescriptor.getCharArrayContent();
                characterContentBinding(new String(cc));  
                
                if(!characterContentDescriptor.isEmpty())inputStackDescriptor.push(characterContentDescriptor.getStartIndex());
                CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);                
                cvh.handleChars(cc, element, hasComplexContent);
                if(!characterContentDescriptor.isEmpty())inputStackDescriptor.pop();
                cvh.recycle();
            }            
        } 
        
        if(localCharacterContentDescriptor != null){
             localCharacterContentDescriptor.recycle();
             localCharacterContentDescriptor = null;
        }
		// Character content binding is not done by the InternalConflictResolver 
		// because there are no differences between different internal pattern
		// configurations, the text is added anyway.
		// Still it would be better to have the "normalized" version resulted 
		// from the processing done for the validation.
		// TODO see about what to do if chars validation results in errors
		//characterContentBinding();
	}	
	
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{		
		validateContext();
		reportContextErrors(restrictToFileName, locator);
		endElementBinding();
		validateInContext();
	}
	
	// 	candidate - queue mapping
	//	reservationStartEntry = queueStartEntry+1	
	
	// Only used when conflict exists and is unsolved and it is possible that
	// in context validation would lead to the resolution of the conflict and 
	// the binding can happen.
	void addChildElement(List<AElement> candidateDefinitions, ConflictMessageReporter conflictMessageReporter, BindingModel bindingModel, Queue targetQueue, int reservationStartEntry, int reservationEndEntry, Map<AElement, Queue> candidateQueues){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllElements(candidateDefinitions, conflictMessageReporter, bindingModel, targetQueue, reservationStartEntry, reservationEndEntry, candidateQueues);
	}
	void addAttribute(List<AAttribute> candidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue queue, int entry, BindingModel bindingModel){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllAttributes(candidateDefinitions, temporaryMessageStorage, value, queue, entry, bindingModel);
	}
	void addChildElement(List<AElement> candidateDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter, BindingModel bindingModel, Queue targetQueue, int reservationStartEntry, int reservationEndEntry,  Map<AElement, Queue> candidateQueues){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllElements(candidateDefinitions, conflictHandler, conflictMessageReporter, bindingModel, targetQueue, reservationStartEntry, reservationEndEntry, candidateQueues);
	}
	void addAttribute(List<AAttribute> candidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue queue, int entry, BindingModel bindingModel){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllAttributes(candidateDefinitions, disqualified, temporaryMessageStorage, value, queue, entry, bindingModel);
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