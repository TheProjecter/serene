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
import java.util.ArrayList;

import org.xml.sax.SAXException;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.components.StructuredDataActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.APattern;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.content.StructuredDataEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

class StructuredDataValidationHandler extends AbstractSDVH implements StructuredDataEventHandler{
    ExceptPatternValidationHandler parent;        
    StructuredDataContentTypeHandler structuredDataContentTypeHandler;
    ErrorCatcher contextErrorCatcher;
    
    ArrayList<AData> dataMatches;
    ArrayList<AValue> valueMatches;
	ArrayList<AListPattern> listMatches;
    ArrayList<StructuredDataActiveTypeItem> matches;
    
    ErrorCatcher currentErrorCatcher;
    // Index of the processed candidate in the total matches list.
    // Used for the list pattern validation and error reporting.
    int currentIndex;
    
    StructuredDataValidationHandler(MessageWriter debugWriter){
        super(debugWriter);
        
        dataMatches = new ArrayList<AData>();
        valueMatches = new ArrayList<AValue>();
	    listMatches = new ArrayList<AListPattern>();
        matches = new ArrayList<StructuredDataActiveTypeItem>();   
        
        currentIndex = -1;
    }
    
    void init(ExceptPatternValidationHandler parent, StructuredDataContentTypeHandler structuredDataContentTypeHandler, ErrorCatcher contextErrorCatcher){
        this.parent = parent;
        this.structuredDataContentTypeHandler = structuredDataContentTypeHandler;
        this.contextErrorCatcher = contextErrorCatcher;
    }
    
    void reset(){
        super.reset(); 
        
        dataMatches.clear();
        valueMatches.clear();
	    listMatches.clear();
	    matches.clear();
	    
	    if(currentErrorCatcher != null)currentErrorCatcher = null;
	    currentIndex = -1;
	}
    
	public void recycle(){
	    reset();
	    pool.recycle(this);
	}
	
	public ExceptPatternValidationHandler getParentHandler(){
	    return parent;
	}
	
    public void handleChars(char[] chars, StructuredDataActiveType type) throws SAXException{				
        int dataOffset = -1;    
        int valueOffset = -1; 	    
        int listOffset = -1;
        
        if(type.allowsDataContent()){
		    dataMatches.addAll(matchHandler.getDataMatches(type));						
			dataOffset = 0;
			matches.addAll(dataMatches);
		}	
		if(type.allowsValueContent()){
			valueMatches.addAll(matchHandler.getValueMatches(type));			
			valueOffset = matches.size();
			matches.addAll(valueMatches);				
		}	
		if(type.allowsListPatternContent()){
			listMatches.addAll(matchHandler.getListPatternMatches(type));
			listOffset = matches.size();
			matches.addAll(listMatches);	
		}
		
		
		if(dataMatches != null && dataMatches.size() > 0){
		    for(int i = 0; i < dataMatches.size(); i++){
			    currentIndex = i + dataOffset;
			    validateData(chars, type, dataMatches.get(i));
			}
		}
        if(valueMatches != null && valueMatches.size() > 0){	
            for(int i = 0; i < valueMatches.size(); i++){
			    currentIndex = i + valueOffset;
			    validateValue(chars, type, valueMatches.get(i));
			}
		}
		if(listMatches != null && listMatches.size() > 0){	
		    for(int i = 0; i < listMatches.size(); i++){ 
			    currentIndex = i + listOffset;
			    validateListPattern(chars, listMatches.get(i));
			}
		}
		
		handleAddToParent();
	}
	
	public void handleString(String value, StructuredDataActiveType type) throws SAXException{
	    int dataOffset = -1;    
        int valueOffset = -1; 	    
        int listOffset = -1;
                
        if(type.allowsDataContent()){
		    dataMatches.addAll(matchHandler.getDataMatches(type));						
			dataOffset = 0;
			matches.addAll(dataMatches);
		}	
		if(type.allowsValueContent()){
			valueMatches.addAll(matchHandler.getValueMatches(type));			
			valueOffset = matches.size();
			matches.addAll(valueMatches);				
		}	
		if(type.allowsListPatternContent()){
			listMatches.addAll(matchHandler.getListPatternMatches(type));
			listOffset = matches.size();
			matches.addAll(listMatches);	
		}
		
		if(dataMatches != null && dataMatches.size() > 0){
		    for(int i = 0; i < dataMatches.size(); i++){
			    currentIndex = i + dataOffset;
			    validateData(value, type, dataMatches.get(i));
			}
		}
        if(valueMatches != null && valueMatches.size() > 0){	
            for(int i = 0; i < valueMatches.size(); i++){
			    currentIndex = i + valueOffset;
			    validateValue(value, type, valueMatches.get(i));
			}
		}
		if(listMatches != null && listMatches.size() > 0){	
		    for(int i = 0; i < listMatches.size(); i++){
			    currentIndex = i + listOffset;
			    validateListPattern(value, listMatches.get(i));
			}
		}
		
		handleAddToParent();
	}	
	
	void handleAddToParent(){
	    if(matches.size() == 1){
	        structuredDataContentTypeHandler.addStructuredData(matches.get(0));
	    }else{
	        int matchesCount = matches.size();		
	        int qualifiedCount = matchesCount - externalConflictHandler.getDisqualifiedCount();        
	        if(qualifiedCount == 0){
	            // it's error, no need for details, the report will come from except anyway 
	            parent.unresolvedCharacterContent(inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), matches.toArray(new CharsActiveTypeItem[matches.size()]));
	        }else{
	            structuredDataContentTypeHandler.addStructuredData(matches, externalConflictHandler.getDisqualified(), temporaryMessageStorage);
	        }
	    }
	}
	
	boolean mustHandleError(char[] chars, APattern pattern){
	    //TODO review this
	    /*if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){
			if(matches.size() == 1){
			    if(pattern.isRequiredBranch())return true;
			    return !(chars.length == 0 || spaceHandler.isSpace(chars));
            }
            return true;			
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
            return true;
		}else{
			throw new IllegalStateException();
		}*/
		return true;
	}
	
	
	void handleError(DatatypedActiveTypeItem item, String datatypeErrorMessage){	    
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){			
			currentErrorCatcher.characterContentDatatypeError(inputStackDescriptor.getItemIdentifier(), inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), item, datatypeErrorMessage); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
			currentErrorCatcher.attributeValueDatatypeError(inputStackDescriptor.getItemIdentifier(), inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), item, datatypeErrorMessage);
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){
		    // doesn't really matter since the report will come from except anyway			
			currentErrorCatcher.characterContentDatatypeError(inputStackDescriptor.getItemIdentifier(), inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), item, datatypeErrorMessage); 
		}else{
			throw new IllegalStateException();
		}		
	}	
	
	void handleError(AValue value){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){			
			currentErrorCatcher.characterContentValueError(inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), value); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
			currentErrorCatcher.attributeValueValueError(inputStackDescriptor.getItemIdentifier(), inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), value);
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){
		    // doesn't really matter since the report will come from except anyway			
			currentErrorCatcher.characterContentValueError(inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), value); 
		}else{
			throw new IllegalStateException();
		}
	}
	
	void setCurrentErrorCatcher(){
	    if(matches.size() == 1){
	        currentErrorCatcher = contextErrorCatcher;
	    }else{
	        if(temporaryMessageStorage == null) temporaryMessageStorage = new TemporaryMessageStorage[matches.size()];
	        if(temporaryMessageStorage[currentIndex] == null) temporaryMessageStorage[currentIndex] = new TemporaryMessageStorage(debugWriter);
	        currentErrorCatcher = temporaryMessageStorage[currentIndex];
	    }
	}
	
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
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.misplacedContent(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, itemId, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
		
	public void misplacedContent(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int[] itemId, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.misplacedContent(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, itemId, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
	
		
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, int[] itemId, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int itemId, String qName, String systemId, int lineNumber, int columnNumber){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.excessiveContent(context, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
	}
	
	public void illegalContent(Rule context, int startItemId, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.illegalContent(context, startItemId, startQName, startSystemId, startLineNumber, startColumnNumber);
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
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousCharacterContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousAttributeValueWarning(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
		
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	
	public void characterContentValueError(String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenDatatypeError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenValueError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenExceptedError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void unresolvedListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.unresolvedListTokenInContextError(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousListTokenInContextWarning(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
	}
	
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
} 
