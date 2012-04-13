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

import java.util.List;
import java.util.BitSet;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.components.StructuredDataActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.Rule;

import serene.validation.handlers.content.StructuredDataEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

class ExceptPatternValidationHandler implements StructuredDataEventHandler,
                                        StructuredDataContentTypeHandler,
                                        ErrorCatcher{
    ValidatorEventHandlerPool pool;
    InputStackDescriptor inputStackDescriptor;
    
    AbstractDVH parent;
    AData data;
    AExceptPattern exceptPattern;
    
    ErrorCatcher errorCatcher;
    boolean hasError;    
    StackHandler stackHandler;
    
    ExceptPatternValidationHandler(){
        hasError = false;
    }
         
    
    void init(ValidatorEventHandlerPool pool, InputStackDescriptor inputStackDescriptor){		
		this.inputStackDescriptor = inputStackDescriptor;		
		this.pool = pool;
	}
    
	void init(AData data, AExceptPattern exceptPattern, AbstractDVH parent, ErrorCatcher errorCatcher){
	    this.data = data;
        this.exceptPattern = exceptPattern;
        this.parent = parent;
        this.errorCatcher = errorCatcher;
    }
    
    void reset(){
        parent = null;
        data = null;
        exceptPattern = null;
        hasError = false;
        errorCatcher = null;        
    }
    
    public void recycle(){ 
        reset();
        pool.recycle(this);
    }
    
    public AbstractDVH getParentHandler(){
        return parent;
    }
    
    public void handleChars(char[] chars, StructuredDataActiveType context) throws SAXException{
        stackHandler = exceptPattern.getStackHandler(this);
        
        StructuredDataValidationHandler sdvh = pool.getStructuredDataValidationHandler(this, this, this);
        sdvh.handleChars(chars, context);
        sdvh.recycle();
        
        stackHandler.endValidation();
        stackHandler.recycle();
        stackHandler = null;
        
        handleExcept();
        
    }
	
	public void handleString(String value, StructuredDataActiveType context) throws SAXException{
	    stackHandler = exceptPattern.getStackHandler(this);
	    
	    StructuredDataValidationHandler sdvh = pool.getStructuredDataValidationHandler(this, this, this);
        sdvh.handleString(value, context);
        sdvh.recycle();
        
        stackHandler.endValidation();
        stackHandler.recycle();
        stackHandler = null;
        
        handleExcept();        
	}
	
	void handleExcept(){
	    if(hasError)return;		
		if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){			
			errorCatcher.characterContentExceptedError(inputStackDescriptor.getCurrentItemInputRecordIndex(), data); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){			
			errorCatcher.characterContentExceptedError(inputStackDescriptor.getCurrentItemInputRecordIndex(), data); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
			errorCatcher.attributeValueExceptedError(inputStackDescriptor.getCurrentItemInputRecordIndex(), data);
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){ 
			errorCatcher.listTokenExceptedError(inputStackDescriptor.getCurrentItemInputRecordIndex(), data);
		}else{
			throw new IllegalStateException();
		}
	}
//StructuredDataContentTypeHandler
//==============================================================================
    public void addStructuredData(StructuredDataActiveTypeItem data){
        stackHandler.shift(data);
    }	
	public void addStructuredData(List<StructuredDataActiveTypeItem> candidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){	   
	    if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = exceptPattern.getStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		} 	    
		stackHandler.shiftAllCharsDefinitions(candidateDefinitions, temporaryMessageStorage);
	}
	public void addStructuredData(List<StructuredDataActiveTypeItem> candidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
	    if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = exceptPattern.getStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		}	    
		stackHandler.shiftAllCharsDefinitions(candidateDefinitions, disqualified, temporaryMessageStorage);
	}
//==============================================================================


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
		hasError = true;
	}
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper){
		hasError = true;
	}
	
	
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex){
		hasError = true;
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex){
		hasError = true;
	}
	
	public void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex){
		hasError = true;
	}
	
	public void illegalContent(Rule context, int startInputRecordIndex){
		hasError = true;
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
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	}
		
	
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
		hasError = true;
	}
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
		hasError = true;
	}
	
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
		hasError = true;
	}	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
		hasError = true;
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
		hasError = true;
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
		hasError = true;
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
		hasError = true;
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
		hasError = true;
	}
	
    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
    }
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
		hasError = true;
	}
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
}
