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

import sereneWrite.MessageWriter;

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
    
    MessageWriter debugWriter;    
    
    ExceptPatternValidationHandler(MessageWriter debugWriter){
        this.debugWriter = debugWriter;
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
			errorCatcher.characterContentExceptedError(inputStackDescriptor.getItemDescription(), inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), data); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
			errorCatcher.attributeValueExceptedError(inputStackDescriptor.getItemDescription(), inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), data);
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){ 
			errorCatcher.listTokenExceptedError(inputStackDescriptor.getItemDescription(), inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), data);
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
	
			
	public void misplacedContent(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int itemId, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		hasError = true;
	}
	
	public void misplacedContent(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int[] itemId, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		hasError = true;
	}
	
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, int[] itemId, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int itemId, String qName, String systemId, int lineNumber, int columnNumber){
		hasError = true;
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
	}
	
	public void illegalContent(Rule context, int startItemId, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		hasError = true;
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
	}
	
	public void ambiguousAttributeValueWarning(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	}
		
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	
	public void characterContentValueError(String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		hasError = true;
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		hasError = true;
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
	}
	
    public void unresolvedListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
    }
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		hasError = true;
	}
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
}
