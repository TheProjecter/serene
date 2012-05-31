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
import serene.validation.schema.active.components.AListPattern;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.content.DataEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;

import serene.util.SpaceCharsHandler;

class DataValidationHandler extends AbstractDVH implements DataEventHandler{
    ListPatternValidationHandler parent;            
    DataContentTypeHandler dataContentTypeHandler;
    ErrorCatcher contextErrorCatcher;
    
    ArrayList<AData> dataMatches;
	ArrayList<AValue> valueMatches;
    ArrayList<DatatypedActiveTypeItem> matches;
    
    ErrorCatcher currentErrorCatcher;
    int currentIndex;
    
	DataValidationHandler(){
		super();
		
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
	
	public void handleChars(char[] chars, AListPattern type) throws SAXException{
	   int dataOffset = -1;  
       int valueOffset = -1;
       matchHandler.handleCharsMatches(type);
        
	   if(type.allowsDatas()){
		    dataMatches.addAll(matchHandler.getDataMatches());						
			dataOffset = 0;
			matches.addAll(dataMatches);
		}	
		if(type.allowsValues()){
			valueMatches.addAll(matchHandler.getValueMatches());			
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
		
	public void handleString(String value, AListPattern type) throws SAXException{	    	    
	    int dataOffset = -1;  
        int valueOffset = -1;
        matchHandler.handleCharsMatches(type);
        
		if(type.allowsDatas()){
			dataMatches.addAll(matchHandler.getDataMatches());
			dataOffset = 0;
			matches.addAll(dataMatches);			
		}	
		if(type.allowsValues()){
			valueMatches.addAll(matchHandler.getValueMatches());
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
	        if(temporaryMessageStorage != null) {	            
                for(int i = 0; i < temporaryMessageStorage.length; i++){                    
                    if(temporaryMessageStorage[i] != null){
                        /*temporaryMessageStorage[i].setDiscarded(true);*/
                        temporaryMessageStorage[i].clear();
                    }
                }
            }
	    }else{
	        int matchesCount = matches.size();	        
	        int qualifiedCount = matchesCount - externalConflictHandler.getDisqualifiedCount();	        
	        if(qualifiedCount == 0){		
	            dataContentTypeHandler.addData(matches, temporaryMessageStorage);
	        }else if(qualifiedCount == 1){
	            externalConflictHandler.init(matchesCount);
                parent.addData(matches.get(externalConflictHandler.getNextQualified(0)));
                if(temporaryMessageStorage != null) {	            
                    for(int i = 0; i < temporaryMessageStorage.length; i++){                    
                        if(temporaryMessageStorage[i] != null){
                            /*temporaryMessageStorage[i].setDiscarded(true);*/
                            temporaryMessageStorage[i].clear();
                        }
                    }
                }
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
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){			
			currentErrorCatcher.listTokenDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage); 
		}else{
			throw new IllegalStateException();
		}		
	}	
	
	void handleError(AValue value){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){			
			currentErrorCatcher.listTokenValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value); 
		}else{
			throw new IllegalStateException();
		}
	}
		
	void setCurrentErrorCatcher(){
	    if(matches.size() == 1){
	        currentErrorCatcher = contextErrorCatcher;
	    }else{
	        if(temporaryMessageStorage == null) temporaryMessageStorage = new TemporaryMessageStorage[matches.size()];
	        if(temporaryMessageStorage[currentIndex] == null){
                temporaryMessageStorage[currentIndex] = new TemporaryMessageStorage();
                temporaryMessageStorage[currentIndex].init(activeInputDescriptor);
            }
	        currentErrorCatcher = temporaryMessageStorage[currentIndex];
	    }	    
	}
	
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
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	}
		
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
		throw new IllegalStateException();
	}
	
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
		throw new IllegalStateException();
	}
	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
		throw new IllegalStateException();
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
		throw new IllegalStateException();
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
		externalConflictHandler.disqualify(currentIndex);
		setCurrentErrorCatcher();
		currentErrorCatcher.listTokenExceptedError(inputRecordIndex, charsDefinition);
	}	
    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
    }
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
		throw new IllegalStateException();
	}
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
}
