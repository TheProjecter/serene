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
	
	void init(ValidatorEventHandlerPool pool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor, MatchHandler matchHandler){
		super.init(pool, inputStackDescriptor);
		this.matchHandler = matchHandler;
		this.activeInputDescriptor = activeInputDescriptor;
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
			unexpectedAttributeValue(inputStackDescriptor.getCurrentItemInputRecordIndex(), attribute);
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
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = attribute.getStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions, temporaryMessageStorage);
	}
	
	public void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = attribute.getStackHandler(oldStackHandler, this);
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
	
		
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int inputRecordIndex, APattern sourceDefinition, APattern reper){
		throw new IllegalStateException();
	}
	
    public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper){
		throw new IllegalStateException();
	}
    
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex){
		throw new IllegalStateException();
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex){
		throw new IllegalStateException();
	}
	
	public void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex){
		throw new IllegalStateException();
	}
    
    public void illegalContent(Rule context, int startInputRecordIndex){
		throw new IllegalStateException();
	}
    
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void unresolvedAttributeContentError(int inputRecordIndex, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousAttributeContentWarning(int inputRecordIndex, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousCharacterContentWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
	}
	

    public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].attributeValueDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].attributeValueValueError(inputRecordIndex, charsDefinition);
	}
	
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].attributeValueExceptedError(inputRecordIndex, charsDefinition);
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].unresolvedAttributeValue(inputRecordIndex, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].listTokenDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].listTokenValueError(inputRecordIndex, charsDefinition);
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].listTokenExceptedError(inputRecordIndex, charsDefinition);
	}
	
    
    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].unresolvedListTokenInContextError(inputRecordIndex, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
		if(temporaryMessageStorage[candidateIndex] == null){
		    temporaryMessageStorage[candidateIndex] = new TemporaryMessageStorage();
		    temporaryMessageStorage[candidateIndex].init(activeInputDescriptor);
		}
		temporaryMessageStorage[candidateIndex].ambiguousListTokenInContextWarning(inputRecordIndex, possibleDefinitions);
    }
    
    
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
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
