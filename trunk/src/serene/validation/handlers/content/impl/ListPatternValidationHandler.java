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

import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.Rule;

import serene.validation.handlers.content.DataEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.stack.StackHandler;

import serene.util.SpaceCharsHandler;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import sereneWrite.MessageWriter;

class ListPatternValidationHandler implements DataEventHandler,
                                        DataContentTypeHandler,
                                        ErrorCatcher{
    ValidatorEventHandlerPool pool;
    ValidationItemLocator validationItemLocator;
    SpaceCharsHandler spaceHandler;
    
    AbstractSDVH parent;
    char[][] tokens;
    int currentTokenIndex;
    AListPattern listPattern;
        
    ErrorCatcher errorCatcher;    
    StackHandler stackHandler;
    
    MessageWriter debugWriter;    
    
    ListPatternValidationHandler(MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        currentTokenIndex = -1;
    }
        
    
    void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, SpaceCharsHandler spaceHandler){		
		this.validationItemLocator = validationItemLocator;		
		this.pool = pool;
		this.spaceHandler = spaceHandler; 
	}
    
	void init(AListPattern listPattern, AbstractSDVH parent, ErrorCatcher errorCatcher){
        this.listPattern = listPattern;
        this.parent = parent;
        this.errorCatcher = errorCatcher;
    }
    
    void reset(){
        parent = null;
        listPattern = null;
        
        errorCatcher = null;

        currentTokenIndex = -1;        
    }
    
    public void recycle(){
        reset();        
        pool.recycle(this);
    } 
    
    public AbstractSDVH getParentHandler(){
        return parent;    
    }
    
    public void handleChars(char[] chars, DataActiveType context) throws SAXException{
        stackHandler = listPattern.getStackHandler(this);
        
        DataValidationHandler dvh = pool.getDataValidationHandler(this, this, this);        
        
        tokens = spaceHandler.removeSpace(chars);
        
		for(currentTokenIndex = 0; currentTokenIndex < tokens.length; currentTokenIndex++){
		    validationItemLocator.newListToken(new String(tokens[currentTokenIndex]), validationItemLocator.getSystemId(), validationItemLocator.getPublicId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber());
			dvh.handleChars(tokens[currentTokenIndex], context); 
			dvh.reset();
			validationItemLocator.closeListToken();
		}
		dvh.recycle();
		
		//****temporary
		currentTokenIndex--;
		//****temporary
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
    }
	
	public void handleString(String value, DataActiveType context) throws SAXException{
	    stackHandler = listPattern.getStackHandler(this);
	    
	    DataValidationHandler dvh = pool.getDataValidationHandler(this, this, this);
	    
	    tokens = spaceHandler.removeSpace(value.toCharArray());
        
		for(currentTokenIndex = 0; currentTokenIndex < tokens.length; currentTokenIndex++){
		    validationItemLocator.newListToken(new String(tokens[currentTokenIndex]), validationItemLocator.getSystemId(), validationItemLocator.getPublicId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber());
			dvh.handleChars(tokens[currentTokenIndex], context);   
			dvh.reset();
			validationItemLocator.closeListToken();
		}
		dvh.recycle();
		
		//****temporary
		currentTokenIndex--;
		//****temporary
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
	}
	
	
//StructuredDataContentTypeHandler
//==============================================================================
    public void addData(DatatypedActiveTypeItem data){
        if(stackHandler == null) stackHandler = listPattern.getStackHandler(this);
        stackHandler.shift(data);
    }
	public void addData(List<DatatypedActiveTypeItem> candidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){	    
	    if(!stackHandler.handlesConflict()) stackHandler = listPattern.getStackHandler(stackHandler, this);
		stackHandler.shiftAllTokenDefinitions(candidateDefinitions, tokens[currentTokenIndex], temporaryMessageStorage);
	}
	
	public void addData(List<DatatypedActiveTypeItem> candidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){	    
	    if(!stackHandler.handlesConflict()) stackHandler = listPattern.getStackHandler(stackHandler, this);
		stackHandler.shiftAllTokenDefinitions(candidateDefinitions, tokens[currentTokenIndex], disqualified, temporaryMessageStorage);
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
	
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		errorCatcher.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		errorCatcher.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
	
		
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, int[] itemId, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		errorCatcher.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int itemId, String qName, String systemId, int lineNumber, int columnNumber){
		errorCatcher.excessiveContent(context, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		errorCatcher.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
	}
	
	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		errorCatcher.illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
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
		errorCatcher.ambiguousCharacterContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		errorCatcher.ambiguousAttributeValueWarning(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		errorCatcher.undeterminedByContent(qName, candidateMessages);
	}
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		errorCatcher.listTokenDatatypeError(new String(tokens[currentTokenIndex]), charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		errorCatcher.listTokenDatatypeError(new String(tokens[currentTokenIndex]), charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		errorCatcher.listTokenValueError(new String(tokens[currentTokenIndex]), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		errorCatcher.listTokenValueError(new String(tokens[currentTokenIndex]), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		errorCatcher.listTokenExceptedError(new String(tokens[currentTokenIndex]), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		errorCatcher.listTokenExceptedError(new String(tokens[currentTokenIndex]), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		//errorCatcher.ambiguousListToken(new String(token), systemId, lineNumber, columnNumber, possibleDefinitions);
		throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		//errorCatcher.ambiguousListToken(new String(token), systemId, lineNumber, columnNumber, possibleDefinitions);
		throw new IllegalStateException();
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorCatcher.unresolvedListTokenInContextError(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		errorCatcher.ambiguousListTokenInContextWarning(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		errorCatcher.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
	}
	
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
}
