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
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.content.AttributeEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

class ElementValidationHandler extends ValidatingEEH
							implements ErrorCatcher{
								
		
	MatchHandler matchHandler;	
	boolean recognizeOutOfContext;
	SpaceCharsHandler spaceHandler;
	
	AElement element;

	ContextConflictPool contextConflictPool;
			
	StackHandler stackHandler;
		
	ElementValidationHandler parent;
	
	ElementValidationHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
		
	public void recycle(){		
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}
		recycleErrorHandlers();
		//internalConflicts = null; 
		if(contextConflictPool != null)contextConflictPool.clear();
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
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name){
		if(!element.allowsElementContent()) 
			return getUnexpectedElementHandler(namespace, name);
				
		List<AElement> elementMatches = matchHandler.matchElement(namespace, name, element);
		int matchCount = elementMatches.size();
		if(matchCount == 0){
			return getUnexpectedElementHandler(namespace, name);
		}else if(matchCount == 1){			
			ElementValidationHandler next = pool.getElementValidationHandler(elementMatches.get(0), this);				
			return next;
		}else{	
			if(contextConflictPool == null)	contextConflictPool = new ContextConflictPool();			
			ContextConflictDescriptor ccd = contextConflictPool.getContextConflictDescriptor(elementMatches);
			ElementConcurrentHandler next = pool.getElementConcurrentHandler(ccd.getDefinitions(), this);				
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
	
	public void handleAttributes(Attributes attributes, Locator locator){
		for(int i = 0; i < attributes.getLength(); i++){
			String attributeQName = attributes.getQName(i);
			String attributeNamespace = attributes.getURI(i); 
			String attributeName = attributes.getLocalName(i);
			String attributeValue = attributes.getValue(i);
			validationItemLocator.newAttribute(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), attributeNamespace, attributeName, attributeQName);
			handleAttribute(attributeQName, attributeNamespace, attributeName, attributeValue);
			validationItemLocator.closeAttribute();
		}		
	}	
	
	void handleAttribute(String qName, String namespace, String name, String value){		
		AttributeEventHandler aeh = getAttributeHandler(qName, namespace, name);
		aeh.handleAttribute(value);		
	}
	
	
	private AttributeEventHandler getAttributeHandler(String qName, String namespace, String name){
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
			// TODO
			// if(contextConflictPool == null)	contextConflictPool = new ContextConflictPool();			
			// ContextConflictDescriptor ccd = contextConflictPool.getContextConflictDescriptor(attributeMatches);
			AttributeConcurrentHandler next = pool.getAttributeConcurrentHandler(attributeMatches, this);				
			return next;
		}		
	}	

	protected AttributeEventHandler getUnexpectedAttributeHandler(String namespace, String name){
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

	public void handleEndElement(Locator locator) throws SAXException{		
		validateContext();
		reportContextErrors(locator);
		validateInContext();
	}
	
	void validateContext() {
		//end validation
		if(stackHandler != null){
			stackHandler.endValidation();
		}		
	}
	void reportContextErrors(Locator locator) throws SAXException{
		if(validationErrorHandler != null){
			validationErrorHandler.handle(validationItemLocator.getQName(), element, locator);
		}
		if(externalConflictErrorHandler != null){
			externalConflictErrorHandler.handle(validationItemLocator.getQName(), element, locator);
		}
	}
	void validateInContext(){
		parent.addChildElement(element);
	}
	
	public void handleCharacters(char[] chars){		
		if(!element.allowsChars()){			
			chars = spaceHandler.trimSpace(chars);
			if(chars.length >0){
				unexpectedCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), element);
			}
			return;
		}
		chars = spaceHandler.trimSpace(chars);//for leading and trailing white space
		CharacterContentValidationHandler ceh = pool.getCharacterContentValidationHandler(this, this);
		ceh.handleChars(chars, (CharsActiveType)element);				
	}
	
	
	void addChildElement(AElement element){
		stackHandler.shift(element);
	}
	
	void addChildElement(List<AElement> candidateDefinitions){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllElements(candidateDefinitions);
	}
	
	void addChildElement(List<AElement> candidateDefinitions, ExternalConflictHandler conflictHandler){
		if(!stackHandler.handlesConflict()) stackHandler = element.getStackHandler(stackHandler, this);
		stackHandler.shiftAllElements(candidateDefinitions, conflictHandler);
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
	
	public boolean functionalEquivalent(ComparableEEH other){		
		return other.functionalEquivalent(this);
	}
	public boolean functionalEquivalent(ElementValidationHandler other){
		StackHandler otherStackHandler = other.getStackHandler();		
		//return stackHandler.functionalEquivalenceCode() == otherStackHandler.functionalEquivalenceCode();		
		return functionalEquivalenceCode() == other.functionalEquivalenceCode();
	}	
	private int functionalEquivalenceCode(){
		return element.getDefinitionIndex();
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
		return false;
	}
	//--------------------------------------------------------------------------
	
	
	//errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.unknownElement( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.unexpectedElement( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.unexpectedAmbiguousElement( qName, definition, systemId, lineNumber, columnNumber);
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.unknownAttribute( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.unexpectedAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.unexpectedAmbiguousAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}
	
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName, String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
		
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
	}

	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
	}
	
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();		
		contextErrorHandler.ambiguousElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.ambiguousAttributeContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.ambiguousCharsContentError(systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();		
		contextErrorHandler.ambiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	
	
	
	public void undeterminedByContent(String qName, String candidateMessages){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.undeterminedByContent(qName, candidateMessages);
	}

	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.characterContentDatatypeError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}    
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.attributeValueDatatypeError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}    
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.characterContentValueError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.attributeValueValueError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.characterContentExceptedError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.attributeValueExceptedError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.unexpectedCharacterContent(charsSystemId, charsLineNumber, columnNumber, elementDefinition);
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.unexpectedAttributeValue(charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
	}
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.ambiguousCharacterContent(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.ambiguousAttributeValue(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.listTokenDatatypeError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.listTokenValueError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.listTokenExceptedError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.ambiguousListToken(token, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		if(contextErrorHandler == null)setContextErrorHandler();
		contextErrorHandler.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
	}
	//--------------------------------------------------------------------------
	public String toString(){
		String s = "";
		if(stackHandler == null)s= " null";
		else s = stackHandler.toString();
		return "ElementValidationHandler "+element.toString()+" "+s;
	}
	
	protected class ContextConflictPool{
		ContextConflictDescriptor[] conflicts;
		int size;	
		int maxSize;
		ContextConflictPool(){
			maxSize = 1;
			size = 0;
			conflicts = new ContextConflictDescriptor[maxSize];
		}
		
		void clear(){
			Arrays.fill(conflicts, null);
			size = 0;
		}
		
		ContextConflictDescriptor getContextConflictDescriptor(List<AElement> elementMatches){			
			for(int i = 0; i < size; i++){
				if(conflicts[i].equals(elementMatches))return conflicts[i];
			}
						
			ContextConflictDescriptor[] increased = new ContextConflictDescriptor[++maxSize];
			System.arraycopy(conflicts, 0, increased, 0, size);	
			conflicts = increased;
			ContextConflictDescriptor descriptor = new ContextConflictDescriptor(elementMatches); 
			conflicts[size++] = descriptor;
			return descriptor;
		}		
	}
	
	protected class ContextConflictDescriptor{
		private List<AElement> definitions;
				
		ContextConflictDescriptor(List<AElement> definitions){
			this.definitions = new ArrayList<AElement>(definitions);
		}
		
				
		boolean equalsCandidates(List<AElement> otherConflictElements){
			return definitions.equals(otherConflictElements);
		}
		
		boolean equals(ContextConflictDescriptor other){
			return definitions.equals(other.getDefinitions());
		}
	
		List<AElement> getDefinitions(){
			return definitions;
		}
		
		int getDefinitionsCount(){
			return definitions.size();
		}
		
		AElement getDefinition(int index){
			return definitions.get(index);
		}
	}
}

