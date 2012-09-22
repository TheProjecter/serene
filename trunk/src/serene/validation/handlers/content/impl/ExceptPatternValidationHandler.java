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
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.content.StructuredDataEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.match.StructuredDataMatchPath;

class ExceptPatternValidationHandler implements StructuredDataEventHandler,
                                        StructuredDataContentTypeHandler,
                                        ErrorCatcher{
    ValidatorEventHandlerPool pool;
    InputStackDescriptor inputStackDescriptor;
    
    AbstractDVH parent;
    SData data;
    SExceptPattern exceptPattern;
    
    ErrorCatcher errorCatcher;
    boolean hasError;    
    
    StackHandler stackHandler;
    ValidatorStackHandlerPool stackHandlerPool;
    
    ExceptPatternValidationHandler(){
        hasError = false;
    }
         
    
    void init(ValidatorEventHandlerPool pool, ValidatorStackHandlerPool stackHandlerPool, InputStackDescriptor inputStackDescriptor){		
		this.inputStackDescriptor = inputStackDescriptor;		
		this.pool = pool;
		this.stackHandlerPool = stackHandlerPool;
	}
    
	void init(SData data, SExceptPattern exceptPattern, AbstractDVH parent, ErrorCatcher errorCatcher){
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
    
    public void handleChars(char[] chars, SExceptPattern context) throws SAXException{
        stackHandler = stackHandlerPool.getContextStackHandler(exceptPattern, this);
        
        StructuredDataValidationHandler sdvh = pool.getStructuredDataValidationHandler(this, this, this);
        sdvh.handleChars(chars, context);
        sdvh.recycle();
        
        stackHandler.endValidation();
        stackHandler.recycle();
        stackHandler = null;
        
        handleExcept();
        
    }
	
	public void handleString(String value, SExceptPattern context) throws SAXException{
	    stackHandler = stackHandlerPool.getContextStackHandler(exceptPattern, this);
	    
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
    public void addStructuredData(StructuredDataMatchPath data){
        stackHandler.shift(data);
    }	
	public void addStructuredData(List<StructuredDataMatchPath> candidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){	   
	    if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
		    oldStackHandler.recycle();
		} 	    
		stackHandler.shiftAllCharsDefinitions(candidateDefinitions, temporaryMessageStorage);
	}
	public void addStructuredData(List<StructuredDataMatchPath> candidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
	    if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, this);
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
	
			
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
		hasError = true;
	}
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
		hasError = true;
	}
	
	
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
		hasError = true;
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
		hasError = true;
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
		hasError = true;
	}
	
	public void illegalContent(SRule context, int startInputRecordIndex){
		hasError = true;
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
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
	}
		
	
	public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
		hasError = true;
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
		hasError = true;
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
		hasError = true;
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
		hasError = true;
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
		hasError = true;
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, SAttribute attributeDefinition){
		hasError = true;
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
		hasError = true;
	}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
		hasError = true;
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
		hasError = true;
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
		hasError = true;
	}
	
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
		hasError = true;
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
    }
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
		hasError = true;
	}
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
}
