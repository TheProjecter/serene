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

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.Reusable;

import sereneWrite.MessageWriter;

class AttributeValidationHandler extends ValidatingAEH /*implements ErrorCatcher*/{
	AAttribute attribute;
	
	MatchHandler matchHandler;
	
	StackHandler stackHandler;
	
	ErrorCatcher errorCatcher;
	
	AttributeValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
		
	public void recycle(){
		if(errorCatcher != parent)((Reusable)errorCatcher).recycle();
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
	
		
	void init(AAttribute attribute, ElementValidationHandler parent, ErrorCatcher errorCatcher){
		this.parent = parent;
		this.errorCatcher = errorCatcher;
		this.attribute = attribute;
		attribute.assembleDefinition();
		stackHandler = attribute.getStackHandler(errorCatcher);
	}
	
	void validateValue(String value){	
		if(!attribute.allowsChars()){			
			if(value.length() >0){
				errorCatcher.unexpectedAttributeValue(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), attribute);
			}
			return;
		}				
		CharactersEventHandler ceh = pool.getAttributeValueValidationHandler(this, errorCatcher);
		ceh.handleString(value, (CharsActiveType)attribute);		
	}

	void validateInContext(){
		parent.addAttribute(attribute);
	}

	void addChars(CharsActiveTypeItem charsDefinition){
		stackHandler.shift(charsDefinition);
	}
	
	void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions){
		if(!stackHandler.handlesConflict()) stackHandler = attribute.getStackHandler(stackHandler, errorCatcher);
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions);
	}
	/*
	//errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
		errorCatcher.unknownElement( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedElement(String qName, AElement definition, String systemId, int lineNumber, int columnNumber){
		errorCatcher.unexpectedElement( qName, definition, systemId, lineNumber, columnNumber);	
	}	
	public void unexpectedAmbiguousElement(String qName, AElement[] definition, String systemId, int lineNumber, int columnNumber){
		errorCatcher.unexpectedAmbiguousElement( qName, definition, systemId, lineNumber, columnNumber);	
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		errorCatcher.unknownAttribute( qName, systemId, lineNumber, columnNumber);
	}	
	public void unexpectedAttribute(String qName, AAttribute definition, String systemId, int lineNumber, int columnNumber){
		errorCatcher.unexpectedAttribute( qName, definition, systemId, lineNumber, columnNumber);	
	}	
	public void unexpectedAmbiguousAttribute(String qName, AAttribute[] definition, String systemId, int lineNumber, int columnNumber){
		errorCatcher.unexpectedAmbiguousAttribute( qName, definition, systemId, lineNumber, columnNumber);	
	}
	
		
	public void misplacedElement(boolean conflict, APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition){
		errorCatcher.misplacedElement(conflict, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition);
	}
	
	public void misplacedElement(boolean conflict, APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName, String systemId, int lineNumber, int columnNumber, APattern sourceDefinition){
		errorCatcher.misplacedElement(conflict, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition);
	}
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		errorCatcher.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		errorCatcher.excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		errorCatcher.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
	}

	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		errorCatcher.ambiguousElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		errorCatcher.ambiguousAttributeContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		errorCatcher.ambiguousCharsContentError(systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		errorCatcher.ambiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		errorCatcher.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}

	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		errorCatcher.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		errorCatcher.undeterminedByContent(qName, candidateMessages);
	}	
	//--------------------------------------------------------------------------
	*/

	public String toString(){
		return "AttributeValidationHandler "+attribute;
	}	
}