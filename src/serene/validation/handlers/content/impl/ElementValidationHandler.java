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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import serene.util.CharsBuffer;
import serene.util.SpaceCharsHandler;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AListPattern;

	
import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.AttributeEventHandler;
import serene.validation.handlers.content.CharactersEventHandler;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.content.AttributeEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

class ElementValidationHandler extends ValidatingEEH
							implements ErrorCatcher{
								
	CharsBuffer charContentBuffer;
    String charContentSystemId;
    String charContentPublicId;
    int charContentLineNumber;
    int charContentColumnNumber;
    boolean hasComplexContent; 
    // set to true when allowed text content(not data!), or allowed element 
    // content (not unexpected!) is encountered   
	
	MatchHandler matchHandler;	
	boolean recognizeOutOfContext;
	SpaceCharsHandler spaceHandler;
	
	AElement element;

	StackHandler stackHandler;
		
	ElementValidationHandler parent;
	
	ElementValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
        charContentLineNumber = -1;
        charContentColumnNumber = -1;
        charContentBuffer = new CharsBuffer(debugWriter);
        hasComplexContent = false;		
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
		pool.recycle(this);
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, SpaceCharsHandler spaceHandler, MatchHandler matchHandler, ValidatorErrorHandlerPool errorHandlerPool){
		super.init(pool, validationItemLocator, errorHandlerPool);
		this.matchHandler = matchHandler;
		this.spaceHandler = spaceHandler;		
	}
    
	void init(AElement element, ElementValidationHandler parent){
		this.element = element;
		element.assembleDefinition();
		this.parent = parent;
		init((ContextErrorHandlerManager)parent);
		stackHandler = element.getStackHandler(this);
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
		
		if(!element.allowsElementContent()) 
			return getUnexpectedElementHandler(namespace, name);
				
		List<AElement> elementMatches = matchHandler.matchElement(namespace, name, element);
		int matchCount = elementMatches.size();
		if(matchCount == 0){
			return getUnexpectedElementHandler(namespace, name);
		}else if(matchCount == 1){		
            hasComplexContent = true;
			ElementValidationHandler next = pool.getElementValidationHandler(elementMatches.get(0), this);				
			return next;
		}else{
            hasComplexContent = true;			
			ElementConcurrentHandler next = pool.getElementConcurrentHandler(new ArrayList(elementMatches), this);				
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
			String attributeValue = attributes.getValue(i);
			validationItemLocator.newAttribute(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), attributeNamespace, attributeName, attributeQName);			
            ComparableAEH aeh = getAttributeHandler(attributeQName, attributeNamespace, attributeName);
            aeh.handleAttribute(attributeValue);
            aeh.recycle();
			validationItemLocator.closeAttribute();
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
			AttributeValidationHandler next = pool.getAttributeValidationHandler(attributeMatches.get(0), this, this);				
			return next;
		}else{
			AttributeConcurrentHandler next = pool.getAttributeConcurrentHandler(new ArrayList<AAttribute>(attributeMatches), this);				
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
                contextErrorHandler[i].handle(ContextErrorHandler.ELEMENT, validationItemLocator.getQName(), element, restrictToFileName, locator);
            }
        }
	}
	void validateInContext(){
		parent.addChildElement(element);
	}
	
	public void handleInnerCharacters(char[] chars) throws SAXException{
        boolean isIgnorable = chars.length == 0 || spaceHandler.isSpace(chars);
        if(!isIgnorable && element.allowsTextContent()){
            hasComplexContent = true;
            CharacterContentValidationHandler ceh = pool.getCharacterContentValidationHandler(this, this);
            ceh.handleChars(chars, (CharsActiveType)element, hasComplexContent);
            ceh.recycle();
        }else if(!isIgnorable && !element.allowsChars()){
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
	}
	public void handleLastCharacters(char[] chars) throws SAXException {	
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
            if(chars.length > 0){
                charContentBuffer.append(chars, 0, chars.length);
            }
            
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
	}
	
	void addChildElement(AElement element){
		stackHandler.shift(element);
	}
	
	void addChildElement(List<AElement> candidateDefinitions, ConflictMessageReporter conflictMessageReporter){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllElements(candidateDefinitions, conflictMessageReporter);
	}
	
	void addChildElement(List<AElement> candidateDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllElements(candidateDefinitions, conflictHandler, conflictMessageReporter);
	}	
	
	void addAttribute(AAttribute attribute){
		stackHandler.shift(attribute);
	}
	
	void addAttribute(List<AAttribute> candidateDefinitions){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllAttributes(candidateDefinitions);
	}
	
	void addAttribute(List<AAttribute> candidateDefinitions, ExternalConflictHandler conflictHandler){		
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllAttributes(candidateDefinitions, conflictHandler);
	}
	
	void addChars(CharsActiveTypeItem charsDefinition){		
		stackHandler.shift(charsDefinition);
	}
	
	void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions){		
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions);
	}
	
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
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unknownElement( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedElement( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedAmbiguousElement( qName, definition, systemId, lineNumber, columnNumber);
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unknownAttribute( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedAmbiguousAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}
	
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName, String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
		
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
	}

	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
	}
	
	public void unresolvedAmbiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();		
		contextErrorHandler[contextErrorHandlerIndex].unresolvedAmbiguousElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void unresolvedUnresolvedElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();		
		contextErrorHandler[contextErrorHandlerIndex].unresolvedUnresolvedElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void unresolvedAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unresolvedAttributeContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousCharsContentError(systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	
	public void ambiguousUnresolvedElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();		
		contextErrorHandler[contextErrorHandlerIndex].ambiguousUnresolvedElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousAmbiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();		
		contextErrorHandler[contextErrorHandlerIndex].ambiguousAmbiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	
	
	
	public void undeterminedByContent(String qName, String candidateMessages){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].undeterminedByContent(qName, candidateMessages);
	}

	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].characterContentDatatypeError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}    
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].attributeValueDatatypeError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}    
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].characterContentValueError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].attributeValueValueError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].characterContentExceptedError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].attributeValueExceptedError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedCharacterContent(charsSystemId, charsLineNumber, columnNumber, elementDefinition);
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unexpectedAttributeValue(charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unresolvedCharacterContent(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].unresolvedAttributeValue(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].listTokenDatatypeError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].listTokenValueError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].listTokenExceptedError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousListToken(token, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
    
    public void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousListTokenInContextError(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }
    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].ambiguousListTokenInContextWarning(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }
	
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		if(contextErrorHandler[contextErrorHandlerIndex] == null)setContextErrorHandler();
		contextErrorHandler[contextErrorHandlerIndex].missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
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

