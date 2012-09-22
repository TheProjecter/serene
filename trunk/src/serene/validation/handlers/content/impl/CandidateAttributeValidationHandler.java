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

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.content.CharactersEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.Reusable;

class CandidateAttributeValidationHandler extends AttributeDefinitionHandler 
                                            implements ErrorCatcher{
	
    ElementValidationHandler parent;    
	
	ExternalConflictHandler conflictHandler;
	int candidateIndex;
	TemporaryMessageStorage[] temporaryMessageStorage;
	
	ActiveInputDescriptor activeInputDescriptor;
	
	CandidateAttributeValidationHandler(){
		super();
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
	
	void init(ValidatorEventHandlerPool pool, ValidatorStackHandlerPool stackHandlerPool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor, MatchHandler matchHandler){
		super.init(pool, inputStackDescriptor);
		this.matchHandler = matchHandler;
		this.activeInputDescriptor = activeInputDescriptor;
		this.stackHandlerPool = stackHandlerPool;
	}
	
		
	void init(AttributeMatchPath attributeMatchPath, ElementValidationHandler parent, ExternalConflictHandler conflictHandler, int candidateIndex, TemporaryMessageStorage[] temporaryMessageStorage){
		this.parent = parent;
		this.attributeMatchPath = attributeMatchPath;
		this.attribute = attributeMatchPath.getAttribute();
        this.conflictHandler = conflictHandler;
        this.candidateIndex = candidateIndex;
        this.temporaryMessageStorage = temporaryMessageStorage;		
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

	
	// CharsContentTypeHandler
	//==========================================================================
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
	//==========================================================================
	
	
	//errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(int inputRecordIndex){        
		throw new IllegalStateException();
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){        
		throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] definition, int inputRecordIndex){
		throw new IllegalStateException();
	}
	
	
	public void unknownAttribute(int inputRecordIndex){
		throw new IllegalStateException();
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
		throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
		throw new IllegalStateException();
	}
	
		
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
		throw new IllegalStateException();
	}
	
    public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
		throw new IllegalStateException();
	}
    
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
		throw new IllegalStateException();
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
		throw new IllegalStateException();
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
		throw new IllegalStateException();
	}
    
    public void illegalContent(SRule context, int startInputRecordIndex){
		throw new IllegalStateException();
	}
    
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void unresolvedAttributeContentError(int inputRecordIndex, SAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousAttributeContentWarning(int inputRecordIndex, SAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousCharacterContentWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
	}
	

    public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].attributeValueDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].attributeValueValueError(inputRecordIndex, charsDefinition);
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].attributeValueExceptedError(inputRecordIndex, charsDefinition);
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].unresolvedAttributeValue(inputRecordIndex, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].listTokenDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].listTokenValueError(inputRecordIndex, charsDefinition);
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].listTokenExceptedError(inputRecordIndex, charsDefinition);
	}
	
    
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].unresolvedListTokenInContextError(inputRecordIndex, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].ambiguousListTokenInContextWarning(inputRecordIndex, possibleDefinitions);
    }
    
    
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
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
