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

import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.error.ConflictMessageReporter;
   
	
import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.Reusable;

import sereneWrite.MessageWriter;

class AttributeValidationHandler extends AttributeDefinitionHandler implements ErrorCatcher{
		
    ElementValidationHandler parent;    
	
	ContextErrorHandlerManager contextErrorHandlerManager;
	
	AttributeValidationHandler(MessageWriter debugWriter){
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
	
		
	void init(AAttribute attribute, ElementValidationHandler parent, ContextErrorHandlerManager contextErrorHandlerManager){
		this.parent = parent;
		this.contextErrorHandlerManager = contextErrorHandlerManager;
		this.attribute = attribute;
		attribute.assembleDefinition();
		stackHandler = attribute.getStackHandler(this);
	}
	
    public ElementValidationHandler getParentHandler(){
        return parent;
    }
    
	void validateValue(String value) throws SAXException{
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
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unknownElement( qName, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedElement( qName, definition, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedAmbiguousElement( qName, definition, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unknownAttribute( qName, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedAttribute( qName, definition, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedAmbiguousAttribute( qName, definition, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName, String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
    public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
    
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
    
    public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
    
	public void unresolvedAmbiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedAmbiguousElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void unresolvedUnresolvedElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
	    ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedUnresolvedElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

	public void unresolvedAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
        /*ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedAttributeContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);*/
        throw new IllegalStateException();
	}

	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousCharsContentError(systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

	public void ambiguousUnresolvedElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousUnresolvedElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void ambiguousAmbiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousAmbiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.undeterminedByContent(qName, candidateMessages);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}


    public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.characterContentDatatypeError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.attributeValueDatatypeError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.characterContentValueError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.attributeValueValueError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.characterContentExceptedError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.attributeValueExceptedError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedCharacterContent(charsSystemId, charsLineNumber, columnNumber, elementDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedAttributeValue(charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedCharacterContent(systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedAttributeValue(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.listTokenDatatypeError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.listTokenValueError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.listTokenExceptedError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousListToken(token, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
    
    public void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousListTokenInContextError(token, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousListTokenInContextWarning(token, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
    }
    
    
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
	}	
	//--------------------------------------------------------------------------

    
	public String toString(){
		return "AttributeValidationHandler "+attribute;
	}	
}