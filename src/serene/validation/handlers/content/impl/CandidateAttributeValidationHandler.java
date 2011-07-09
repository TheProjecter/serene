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

import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.error.CandidatesConflictErrorHandler;

import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.Reusable;

import sereneWrite.MessageWriter;

class CandidateAttributeValidationHandler extends AttributeDefinitionHandler implements ErrorCatcher{
	
    ElementValidationHandler parent;    
	
	ContextErrorHandler contextErrorHandler;
	
	CandidateAttributeValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
		
	public void recycle(){
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}        
        attribute.releaseDefinition();
		pool.recycle(this);
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, MatchHandler matchHandler){
		super.init(pool, validationItemLocator);
		this.matchHandler = matchHandler;
	}
	
		
	void init(AAttribute attribute, ElementValidationHandler parent, CandidatesConflictErrorHandler candidatesConflictErrorHandler, int candidateIndex){
		this.parent = parent;
		this.contextErrorHandler = contextErrorHandler;
		this.attribute = attribute;
		attribute.assembleDefinition();
		stackHandler = attribute.getStackHandler(this);
	}
	
    public ElementValidationHandler getParentHandler(){
        return parent;
    }
    
	void validateValue(String value){
		if(!attribute.allowsChars()){
			unexpectedAttributeValue(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), attribute);
			return;
		}				
		CharactersEventHandler ceh = pool.getAttributeValueValidationHandler(this, this);
		ceh.handleString(value, (CharsActiveType)attribute, false);		
        ceh.recycle();
	}

	void validateInContext(){
		parent.addAttribute(attribute);
	}

	void addChars(CharsActiveTypeItem charsDefinition){
		stackHandler.shift(charsDefinition);
	}
	
	void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions){
		if(!stackHandler.handlesConflict()) stackHandler = attribute.getStackHandler(stackHandler, this);
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions);
	}
	
	//errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){        
		contextErrorHandler.unknownElement( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){        
		contextErrorHandler.unexpectedElement( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		contextErrorHandler.unexpectedAmbiguousElement( qName, definition, systemId, lineNumber, columnNumber);
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		contextErrorHandler.unknownAttribute( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		contextErrorHandler.unexpectedAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		contextErrorHandler.unexpectedAmbiguousAttribute( qName, definition, systemId, lineNumber, columnNumber);
	}
	
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName, String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		contextErrorHandler.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
	
    public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		contextErrorHandler.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
    
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		contextErrorHandler.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		contextErrorHandler.excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		contextErrorHandler.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
	}
    
    public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		contextErrorHandler.illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
	}
    
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		contextErrorHandler.ambiguousElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		contextErrorHandler.ambiguousAttributeContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		contextErrorHandler.ambiguousCharsContentError(systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		contextErrorHandler.ambiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		contextErrorHandler.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		contextErrorHandler.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		contextErrorHandler.undeterminedByContent(qName, candidateMessages);
	}


    public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		contextErrorHandler.characterContentDatatypeError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		contextErrorHandler.attributeValueDatatypeError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		contextErrorHandler.characterContentValueError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		contextErrorHandler.attributeValueValueError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		contextErrorHandler.characterContentExceptedError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		contextErrorHandler.attributeValueExceptedError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		contextErrorHandler.unexpectedCharacterContent(charsSystemId, charsLineNumber, columnNumber, elementDefinition);
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		contextErrorHandler.unexpectedAttributeValue(charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
	}
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		contextErrorHandler.ambiguousCharacterContent(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		contextErrorHandler.ambiguousAttributeValue(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		contextErrorHandler.listTokenDatatypeError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		contextErrorHandler.listTokenValueError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		contextErrorHandler.listTokenExceptedError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		contextErrorHandler.ambiguousListToken(token, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
    
    public void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		contextErrorHandler.ambiguousListTokenInContextError(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		contextErrorHandler.ambiguousListTokenInContextWarning(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }
    
    
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		contextErrorHandler.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
	}	
	//--------------------------------------------------------------------------

	public String toString(){
		return "AttributeValidationHandler "+attribute;
	}	
}
