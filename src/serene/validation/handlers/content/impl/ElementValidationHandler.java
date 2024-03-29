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
import java.util.Arrays;
import java.util.Map;
import java.util.BitSet;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import serene.util.CharsBuffer;
import serene.util.SpaceCharsHandler;

import serene.validation.handlers.match.MatchHandler;
import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.CharsMatchPath;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;


import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.AttributeEventHandler;
import serene.validation.handlers.content.CharactersEventHandler;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.content.AttributeEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptorPool;
 
class ElementValidationHandler extends ValidatingEEH
							implements ErrorCatcher, 
							ElementContentTypeHandler,
							AttributeContentTypeHandler,
							CharsContentTypeHandler{
								
	/*CharsBuffer charContentBuffer;
    String charContentSystemId;
    String charContentPublicId;
    int charContentLineNumber;
    int charContentColumnNumber;*/
    boolean hasComplexContent; 
    // set to true when allowed text content(not data!), or allowed element 
    // content (not unexpected!) is encountered
    CharacterContentDescriptor localCharacterContentDescriptor;
	
	MatchHandler matchHandler;	
	boolean recognizeOutOfContext;
	SpaceCharsHandler spaceHandler;
	
	ElementMatchPath elementMatchPath;
	SElement element;
	
	StackHandler stackHandler;
	ValidatorStackHandlerPool stackHandlerPool;
		
	ElementValidationHandler parent;
	
	ElementValidationHandler(){
		super();
        /*charContentLineNumber = -1;
        charContentColumnNumber = -1;
        charContentBuffer = new CharsBuffer(debugWriter);*/
        hasComplexContent = false;		
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
		/*element.releaseDefinition();*/
		elementMatchPath = null;
		pool.recycle(this);
	}
	
	void init(ValidatorEventHandlerPool pool, ValidatorStackHandlerPool stackHandlerPool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor, SpaceCharsHandler spaceHandler, MatchHandler matchHandler, ValidatorErrorHandlerPool errorHandlerPool){
		super.init(pool, activeInputDescriptor, inputStackDescriptor, errorHandlerPool);
		this.matchHandler = matchHandler;
		this.spaceHandler = spaceHandler;		
		this.stackHandlerPool = stackHandlerPool;
	}
    
	void init(ElementMatchPath elementMatchPath, ElementValidationHandler parent){
		this.elementMatchPath = elementMatchPath;
		this.element = elementMatchPath.getElement();
		/*element.assembleDefinition();*/
		this.parent = parent;
		init((ContextErrorHandlerManager)parent);
		stackHandler = stackHandlerPool.getContextStackHandler(element, this);
	}	
	
	/*AElement getElement(){
		return element;
	}*/
		
	
	//elementEventHandler
	//--------------------------------------------------------------------------
	public ElementEventHandler getParentHandler(){
		return parent;
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{
		if(!element.allowsElements()){
			return getUnexpectedElementHandler(namespace, name);
	    }
				
		List<ElementMatchPath> elementMatchPathes = matchHandler.matchElement(namespace, name, element);
		int matchCount = elementMatchPathes.size();
		if(matchCount == 0){
			return getUnexpectedElementHandler(namespace, name);
		}else if(matchCount == 1){		
            hasComplexContent = true;
			ElementValidationHandler next = pool.getElementValidationHandler(elementMatchPathes.get(0), this);				
			return next;
		}else{
            hasComplexContent = true;			
			ElementConcurrentHandler next = pool.getElementConcurrentHandler(elementMatchPathes, this);				
			return next;
		}		
	}	

	protected ComparableEEH getUnexpectedElementHandler(String namespace, String name){
		List<SimplifiedComponent> elementMatches = matchHandler.matchElement(namespace, name);
		int matchCount = elementMatches.size();
		if(matchCount == 0){
			UnknownElementHandler next = pool.getUnknownElementHandler(this);
			return next;
		}else if(matchCount == 1){
			UnexpectedElementHandler next = pool.getUnexpectedElementHandler(elementMatches.get(0), this);				
			return next;
		}else{					
			UnexpectedAmbiguousElementHandler next = pool.getUnexpectedAmbiguousElementHandler(elementMatches, this);				
			return next;
		}		
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
            ComparableAEH aeh = getAttributeHandler(attributeQName, attributeNamespace, attributeName);
            aeh.handleAttribute(attributeValue);
            aeh.recycle();
			inputStackDescriptor.popAttribute();
		}		
	}	
		
	ComparableAEH getAttributeHandler(String qName, String namespace, String name){
		if(!element.allowsAttributes()) 
			return getUnexpectedAttributeHandler(namespace, name);
		List<AttributeMatchPath> attributeMatchPathes = matchHandler.matchAttribute(namespace, name, element);
		int matchCount = attributeMatchPathes.size();
		if(matchCount == 0){
			return getUnexpectedAttributeHandler(namespace, name);
		}else if(matchCount == 1){			
			AttributeValidationHandler next = pool.getAttributeValidationHandler(attributeMatchPathes.get(0), this, this);				
			return next;
		}else{
			AttributeConcurrentHandler next = pool.getAttributeConcurrentHandler(attributeMatchPathes, this);				
			return next;
		}		
	}	

	ComparableAEH getUnexpectedAttributeHandler(String namespace, String name){
		List<SimplifiedComponent> attributeMatches = matchHandler.matchAttribute(namespace, name);
		int matchCount = attributeMatches.size();
		if(matchCount == 0){
			UnknownAttributeHandler next = pool.getUnknownAttributeHandler(this);
			return next;
		}else if(matchCount == 1){
			UnexpectedAttributeHandler next = pool.getUnexpectedAttributeHandler(attributeMatches.get(0), this);				
			return next;
		}else{					
			UnexpectedAmbiguousAttributeHandler next = pool.getUnexpectedAmbiguousAttributeHandler(attributeMatches, this);				
			return next;
		}		
	}	

	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
		validateContext();		
		reportContextErrors(restrictToFileName, locator);
		validateInContext();
	}
	
	void validateContext() throws SAXException{        
		//end validation
		if(stackHandler != null){
			stackHandler.endValidation();
		}		
	}
	void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{
        for(int i = 0; i < HANDLER_COUNT; i++){
            if(contextErrorHandler[i] != null){            
                contextErrorHandler[i].handle(ContextErrorHandler.ELEMENT, inputStackDescriptor.getItemDescription(), element, restrictToFileName, locator);
            }
        }
	}
	
	void discardContextErrors(){
        for(int i = 0; i < HANDLER_COUNT; i++){
            if(contextErrorHandler[i] != null){            
                contextErrorHandler[i].discard();
            }
        }
	}
	
	void validateInContext(){
		parent.addChildElement(elementMatchPath);
	}
	
	public void handleInnerCharacters(CharacterContentDescriptor characterContentDescriptor, CharacterContentDescriptorPool characterContentDescriptorPool) throws SAXException{
       
        boolean isIgnorable = characterContentDescriptor.isEmpty() || characterContentDescriptor.isSpaceOnly();
        if(!isIgnorable && element.allowsText()){
            hasComplexContent = true;
            CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
            inputStackDescriptor.push(characterContentDescriptor.getStartIndex());
            cvh.handleChars(characterContentDescriptor.getCharArrayContent(), element, hasComplexContent);
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
	public void handleLastCharacters(CharacterContentDescriptor characterContentDescriptor) throws SAXException {     
        boolean isIgnorable = characterContentDescriptor.isEmpty() || characterContentDescriptor.isSpaceOnly();
        if(hasComplexContent){
            // No previous text buffered, either has been processed, or there was none and the complex content was established based on elemetn content.
            if(element.allowsText()){
                if(!isIgnorable){
                    CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                    inputStackDescriptor.push(characterContentDescriptor.getStartIndex());
                    cvh.handleChars(characterContentDescriptor.getCharArrayContent(), element, hasComplexContent);
                    inputStackDescriptor.pop();
                    cvh.recycle();
                }
            }else if(element.allowsCharsContent()){
                // Since 
                //      - hasComplexContent is set to "true" only for ALLOWED elements 
                //      - restrictions don't allow data and elements in the same context
                // this only occurs when there is a choice and there might be excessive 
                // content, so only shift characters if not ignorable. 
                if(localCharacterContentDescriptor != null){
                    boolean localIsIgnorable = localCharacterContentDescriptor.isEmpty() || localCharacterContentDescriptor.isSpaceOnly();
                    if(!isIgnorable || !localIsIgnorable){                    
                        localCharacterContentDescriptor.add(characterContentDescriptor.getAllIndexes());
                        
                        CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                        inputStackDescriptor.push(localCharacterContentDescriptor.getStartIndex());
                        cvh.handleChars(localCharacterContentDescriptor.getCharArrayContent(), element, hasComplexContent);
                        inputStackDescriptor.pop();
                        cvh.recycle();
                    }
                }else if(!isIgnorable){
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
                
                CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                inputStackDescriptor.push(localCharacterContentDescriptor.getStartIndex());
                cvh.handleChars(localCharacterContentDescriptor.getCharArrayContent(), element, hasComplexContent);
                inputStackDescriptor.pop();
                cvh.recycle();                
            }else{
                CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
                if(!characterContentDescriptor.isEmpty()) inputStackDescriptor.push(characterContentDescriptor.getStartIndex());
                cvh.handleChars(characterContentDescriptor.getCharArrayContent(), element, hasComplexContent);
                if(!characterContentDescriptor.isEmpty()) inputStackDescriptor.pop();
                cvh.recycle();
            }            
        }        
        
        if(localCharacterContentDescriptor != null){
             localCharacterContentDescriptor.recycle();
             localCharacterContentDescriptor = null;
        }
	}
	
//ElementContentTypeHandler
//==============================================================================
	public void addChildElement(ElementMatchPath elementMatchPath){	    
		stackHandler.shift(elementMatchPath);
	}
	
	public void addChildElement(List<ElementMatchPath> candidateDefinitionPathes, ConflictMessageReporter conflictMessageReporter){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllElements(candidateDefinitionPathes, conflictMessageReporter);
	}
	
	public void addChildElement(List<ElementMatchPath> candidateDefinitionPathes, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllElements(candidateDefinitionPathes, conflictHandler, conflictMessageReporter);
	}	
//==============================================================================


//AttributeContentTypeHandler
//==============================================================================
	public void addAttribute(AttributeMatchPath attributeMatchPath){
		stackHandler.shift(attributeMatchPath);
	}
	
	public void addAttribute(List<AttributeMatchPath> candidateDefinitionPathes, TemporaryMessageStorage[] temporaryMessageStorage){		
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllAttributes(candidateDefinitionPathes, temporaryMessageStorage);
	}
	
	public void addAttribute(List<AttributeMatchPath> candidateDefinitionPathes, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){		
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllAttributes(candidateDefinitionPathes, disqualified, temporaryMessageStorage);
	}
//==============================================================================

	
//CharsContentTypeHandler
//==============================================================================
	public void addChars(CharsMatchPath charsDefinition){		
		stackHandler.shift(charsDefinition);
	}
	
	public void addChars(List<? extends CharsMatchPath> charsCandidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}		
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions, temporaryMessageStorage);
	}
	
	public void addChars(List<? extends CharsMatchPath> charsCandidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}		
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions, disqualified, temporaryMessageStorage);
	}
//==============================================================================	
	
	private StackHandler getStackHandler(){
		return stackHandler;
	}	
	
	boolean functionalEquivalent(ComparableEEH other){		
		return other.functionalEquivalent(this);
	}
    
	boolean functionalEquivalent(ElementValidationHandler other){
		//StackHandler otherStackHandler = other.getStackHandler();		
		//return stackHandler.functionalEquivalenceCode() == otherStackHandler.functionalEquivalenceCode();		
		return functionalEquivalenceCode() == other.functionalEquivalenceCode();
	}	
	private int functionalEquivalenceCode(){
		return element.getDefinitionIndex();
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
		return false;
	}
	//--------------------------------------------------------------------------
	
	
	//errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(int inputRecordIndex){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unknownElement(inputRecordIndex);
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedElement(definition, inputRecordIndex);
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] definition, int inputRecordIndex){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedAmbiguousElement( definition, inputRecordIndex);
	}
	
	
	public void unknownAttribute(int inputRecordIndex){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unknownAttribute( inputRecordIndex);
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){	    
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedAttribute( definition, inputRecordIndex);
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
	    if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedAmbiguousAttribute( possibleDefinition, inputRecordIndex);
	}
	
		
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
		
	
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].excessiveContent(context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].excessiveContent(context, excessiveDefinition, inputRecordIndex);
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].missingContent(context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
	}

	public void illegalContent(SRule context, int startInputRecordIndex){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].illegalContent(context, startInputRecordIndex);
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();		
		contextErrorHandler[contextErrorHandlerIndex].unresolvedAmbiguousElementContentError(inputRecordIndex, possibleDefinitions);
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();		
		contextErrorHandler[contextErrorHandlerIndex].unresolvedUnresolvedElementContentError(inputRecordIndex, possibleDefinitions);
	}

	public void unresolvedAttributeContentError(int inputRecordIndex, SAttribute[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unresolvedAttributeContentError(inputRecordIndex, possibleDefinitions);
	}

	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();		
		contextErrorHandler[contextErrorHandlerIndex].ambiguousUnresolvedElementContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();		
		contextErrorHandler[contextErrorHandlerIndex].ambiguousAmbiguousElementContentWarning(inputRecordIndex, possibleDefinitions);
	}

	public void ambiguousAttributeContentWarning(int inputRecordIndex, SAttribute[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousAttributeContentWarning(inputRecordIndex, possibleDefinitions);
	}

	public void ambiguousCharacterContentWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
	}

	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].characterContentDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}    
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].attributeValueDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}    
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].characterContentValueError(inputRecordIndex, charsDefinition);
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].attributeValueValueError(inputRecordIndex, charsDefinition);
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].characterContentExceptedError(inputRecordIndex, charsDefinition);
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].attributeValueExceptedError(inputRecordIndex, charsDefinition);
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedCharacterContent(inputRecordIndex, elementDefinition);		
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, SAttribute attributeDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedAttributeValue(inputRecordIndex, attributeDefinition);
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unresolvedCharacterContent(inputRecordIndex, possibleDefinitions);
	}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unresolvedAttributeValue(inputRecordIndex, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].listTokenDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].listTokenValueError(inputRecordIndex, charsDefinition);
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].listTokenExceptedError(inputRecordIndex, charsDefinition);
	}
	    
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unresolvedListTokenInContextError(inputRecordIndex, possibleDefinitions);
    }
    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousListTokenInContextWarning(inputRecordIndex, possibleDefinitions);
    }
	
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].missingCompositorContent(context, startInputRecordIndex, definition, expected, found);
	}
	
	public void internalConflict(ConflictMessageReporter conflictMessageReporter) throws SAXException{
	    if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].internalConflict(conflictMessageReporter);
	}
	//--------------------------------------------------------------------------
	public String toString(){
		String s = "";
		if(stackHandler == null)s= " null";
		else s = stackHandler.toString();
		return "ElementValidationHandler "+element.toString()+" "+s;
	}	
}

