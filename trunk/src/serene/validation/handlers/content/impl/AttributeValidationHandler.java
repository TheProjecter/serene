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

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.match.MatchHandler;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.CharsMatchPath;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;
   
	
import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.Reusable;

class AttributeValidationHandler extends AttributeDefinitionHandler 
                                  implements ErrorCatcher{
		
    ElementValidationHandler parent;    
	
	ContextErrorHandlerManager contextErrorHandlerManager;
	
	AttributeValidationHandler(){
		super();
	}
		
	void reset(){
	    super.reset();
		parent = null;
		contextErrorHandlerManager = null;
	}
	
	public void recycle(){
		reset();
		pool.recycle(this);
	}
	
	void init(ValidatorEventHandlerPool pool, ValidatorStackHandlerPool stackHandlerPool, InputStackDescriptor inputStackDescriptor, MatchHandler matchHandler){
		super.init(pool, inputStackDescriptor);
		this.matchHandler = matchHandler;
		this.stackHandlerPool = stackHandlerPool;
	}
	
		
	void init(AttributeMatchPath attributeMatchPath, ElementValidationHandler parent, ContextErrorHandlerManager contextErrorHandlerManager){
		this.parent = parent;
		this.contextErrorHandlerManager = contextErrorHandlerManager;
		this.attributeMatchPath = attributeMatchPath;
		this.attribute = attributeMatchPath.getAttribute();
		/*attribute.assembleDefinition();*/
		
	}
	
    public ElementValidationHandler getParentHandler(){
        return parent;
    }
    
	void validateValue(String value) throws SAXException{
	    stackHandler = stackHandlerPool.getContextStackHandler(attribute, this);
		/*if(!attribute.allowsCharsContent()){
			unexpectedAttributeValue(inputStackDescriptor.getCurrentItemInputRecordIndex(), attribute);
			return;
		}*/				
		CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, this);
		cvh.handleString(value, attribute, false);
		cvh.recycle();
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
                
	}

	void validateInContext(){
		parent.addAttribute(attributeMatchPath);
	}

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

	//errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(int inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unknownElement(inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedElement( definition, inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] definition, int inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedAmbiguousElement( definition, inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	
	public void unknownAttribute(int inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unknownAttribute(inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedAttribute( definition, inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedAmbiguousAttribute( possibleDefinition, inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
		
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
    public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
    
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.excessiveContent(context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.excessiveContent(context, excessiveDefinition, inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.missingContent(context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
    
    public void illegalContent(SRule context, int startInputRecordIndex){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.illegalContent(context, startInputRecordIndex);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
    
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedAmbiguousElementContentError(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
	    ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedUnresolvedElementContentError(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

	public void unresolvedAttributeContentError(int inputRecordIndex, SAttribute[] possibleDefinitions){
        /*ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedAttributeContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);*/
        throw new IllegalStateException();
	}
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousUnresolvedElementContentWarning(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousAmbiguousElementContentWarning(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

	public void ambiguousAttributeContentWarning(int inputRecordIndex, SAttribute[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousAttributeContentWarning(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

	public void ambiguousCharacterContentWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
    public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.characterContentDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);        
		contextErrorHandler.attributeValueDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.characterContentValueError(inputRecordIndex, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);        
		contextErrorHandler.attributeValueValueError(inputRecordIndex, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.characterContentExceptedError(inputRecordIndex, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.attributeValueExceptedError(inputRecordIndex, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedCharacterContent(inputRecordIndex, elementDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unexpectedAttributeValue(inputRecordIndex, attributeDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedCharacterContent(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedAttributeValue(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.listTokenDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.listTokenValueError(inputRecordIndex, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.listTokenExceptedError(inputRecordIndex, charsDefinition);
        contextErrorHandler.setCandidate(oldIsCandidate);
	}

    
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.unresolvedListTokenInContextError(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.ambiguousListTokenInContextWarning(inputRecordIndex, possibleDefinitions);
        contextErrorHandler.setCandidate(oldIsCandidate);
    }
    
    
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
        ContextErrorHandler contextErrorHandler = contextErrorHandlerManager.getContextErrorHandler();
        boolean oldIsCandidate = contextErrorHandler.isCandidate();
        contextErrorHandler.setCandidate(false);
		contextErrorHandler.missingCompositorContent(context, startInputRecordIndex, definition, expected, found);
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