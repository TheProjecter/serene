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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.APattern;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.content.DataEventHandler;

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;

import serene.util.SpaceCharsHandler;

import sereneWrite.MessageWriter;


class DataValidationHandler extends AbstractDVH implements DataEventHandler{
    ListPatternValidationHandler parent;            
    DataContentTypeHandler dataContentTypeHandler;
    ErrorCatcher contextErrorCatcher;
    
    ArrayList<AData> dataMatches;
	ArrayList<AValue> valueMatches;
    ArrayList<DatatypedActiveTypeItem> matches;
    
    ErrorCatcher currentErrorCatcher;
    int currentIndex;
    
	DataValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
		
		dataMatches = new ArrayList<AData>();
		valueMatches = new ArrayList<AValue>();
		matches = new ArrayList<DatatypedActiveTypeItem>();
		
		currentIndex = -1;
	}
	
	
	void init(ListPatternValidationHandler parent, DataContentTypeHandler dataContentTypeHandler, ErrorCatcher contextErrorCatcher){
	    this.parent = parent;
	    this.dataContentTypeHandler = dataContentTypeHandler;
	    this.contextErrorCatcher = contextErrorCatcher;
	}
	
	void reset(){
	    super.reset();
        
	    dataMatches.clear();
		valueMatches.clear();
	    matches.clear();
	    
	    if(currentErrorCatcher != null)currentErrorCatcher = null;
	    currentIndex = -1;
	}
		
	public void recycle(){
	    reset();
	    pool.recycle(this);
	}
	
	public ListPatternValidationHandler getParentHandler(){
	    return parent;
	}
	
	public void handleChars(char[] chars, DataActiveType type) throws SAXException{
	   int dataOffset = -1;  
       int valueOffset = -1;
        
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
		
		handleAddToParent();
	}
		
	public void handleString(String value, DataActiveType type) throws SAXException{	    	    
	    int dataOffset = -1;  
        int valueOffset = -1;
        
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
		
		handleAddToParent();
	}
	
	
	void handleAddToParent(){
	    if(matches.size() == 1){
	        dataContentTypeHandler.addData(matches.get(0));
	    }else{
	        int matchesCount = matches.size();	        
	        int qualifiedCount = matchesCount - externalConflictHandler.getDisqualifiedCount();	        
	        if(qualifiedCount == 0){		
	            dataContentTypeHandler.addData(matches, temporaryMessageStorage);
	        }else if(qualifiedCount == 1){
	            externalConflictHandler.init(matchesCount);
                parent.addData(matches.get(externalConflictHandler.getNextQualified(0)));
            }else{
	            dataContentTypeHandler.addData(matches, externalConflictHandler.getDisqualified(), temporaryMessageStorage);
	        }
	    }
	}
	
	boolean mustHandleError(char[] chars, APattern pattern){
		return true;
	}
	
	
	void handleError(DatatypedActiveTypeItem item, String datatypeErrorMessage){	    
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(validationItemLocator.getItemId() == ValidationItemLocator.LIST_TOKEN){			
			currentErrorCatcher.listTokenDatatypeError(validationItemLocator.getItemIdentifier(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), item, datatypeErrorMessage); 
		}else{
			throw new IllegalStateException();
		}		
	}	
	
	void handleError(AValue value){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(validationItemLocator.getItemId() == ValidationItemLocator.LIST_TOKEN){			
			currentErrorCatcher.listTokenValueError(validationItemLocator.getItemIdentifier(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), value); 
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
		throw new IllegalStateException();
	}
	
	public void misplacedContent(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int[] itemId, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		throw new IllegalStateException();
	}
	
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, int[] itemId, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		throw new IllegalStateException();
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int itemId, String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		throw new IllegalStateException();
	}
	
	public void illegalContent(Rule context, int startItemId, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		throw new IllegalStateException();
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
		throw new IllegalStateException();
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		externalConflictHandler.disqualify(currentIndex);
		setCurrentErrorCatcher();
		currentErrorCatcher.listTokenExceptedError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}	
    public void unresolvedListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
    }
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		throw new IllegalStateException();
	}
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
}
