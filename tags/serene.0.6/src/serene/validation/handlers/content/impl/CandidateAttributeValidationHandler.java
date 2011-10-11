/*
Copyright 2011 Radu Cernuta 

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

import java.util.Arrays;
import java.util.List;
import java.util.BitSet;

import org.xml.sax.SAXException;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;

import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.Reusable;

import sereneWrite.MessageWriter;

class CandidateAttributeValidationHandler extends AttributeDefinitionHandler 
                                            implements ErrorCatcher{
	
    ElementValidationHandler parent;    
	
	ExternalConflictHandler conflictHandler;
	int candidateIndex;
	TemporaryMessageStorage[] temporaryMessageStorage;
	
	CandidateAttributeValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
		candidateIndex = -1;
	}
		
	void reset(){
	    super.reset();
	    temporaryMessageStorage = null;
	    candidateIndex = -1;
	    conflictHandler = null;
	    parent = null;
	}
	
	public void recycle(){		
        reset();
		pool.recycle(this);
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, MatchHandler matchHandler){
		super.init(pool, validationItemLocator);
		this.matchHandler = matchHandler;
	}
	
		
	void init(AAttribute attribute, ElementValidationHandler parent, ExternalConflictHandler conflictHandler, int candidateIndex, TemporaryMessageStorage[] temporaryMessageStorage){
		this.parent = parent;
		this.attribute = attribute;
        this.conflictHandler = conflictHandler;
        this.candidateIndex = candidateIndex;
        this.temporaryMessageStorage = temporaryMessageStorage;		
		attribute.assembleDefinition();
	}
	
    public ElementValidationHandler getParentHandler(){
        return parent;
    }
    
	void validateValue(String value) throws SAXException{
	    stackHandler = attribute.getStackHandler(this);
		if(!attribute.allowsChars()){
			unexpectedAttributeValue(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), attribute);
			return;
		}				
		CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
		cvh.handleString(value, (CharsActiveType)attribute, false);	
        cvh.recycle();
        stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
	}

	void validateInContext(){
		parent.addAttribute(attribute);
	}

	
	// CharsContentTypeHandler
	//==========================================================================
	public void addChars(CharsActiveTypeItem charsDefinition){	
		stackHandler.shift(charsDefinition);
	}
	
	public void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){
		if(!stackHandler.handlesConflict()) stackHandler = attribute.getStackHandler(stackHandler, this);
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions, temporaryMessageStorage);
	}
	
	public void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
		if(!stackHandler.handlesConflict()) stackHandler = attribute.getStackHandler(stackHandler, this);
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions, disqualified, temporaryMessageStorage);
	}
	//==========================================================================
	
	
	//errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){        
		throw new IllegalStateException();
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){        
		throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
	
		
	public void misplacedContent(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int itemId, String qName, String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		throw new IllegalStateException();
	}
	
    public void misplacedContent(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int[] itemId, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		throw new IllegalStateException();
	}
    
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, int[] itemId, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		throw new IllegalStateException();
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int itemId, String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		throw new IllegalStateException();
	}
    
    public void illegalContent(Rule context, int startItemId, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		throw new IllegalStateException();
	}
    
	public void unresolvedAmbiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void unresolvedUnresolvedElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void unresolvedAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousUnresolvedElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAmbiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousCharacterContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].ambiguousCharacterContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].ambiguousAttributeValueWarning(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	

    public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].attributeValueDatatypeError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].attributeValueValueError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].attributeValueExceptedError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].unresolvedAttributeValue(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].listTokenDatatypeError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].listTokenValueError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].listTokenExceptedError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
    
    public void unresolvedListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].unresolvedListTokenInContextError(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null) temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage(debugWriter);
		temporaryMessageStorage[candidateIndex].ambiguousListTokenInContextWarning(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }
    
    
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		throw new IllegalStateException();
	}

    public void internalConflict(ConflictMessageReporter conflictMessageReporter){	 
		throw new IllegalStateException();
	}	
	//--------------------------------------------------------------------------

	public String toString(){
		return "AttributeValidationHandler "+attribute;
	}	
}
