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

import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.APattern;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.MarkupEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ErrorCatcher;

class CharactersValidationHandler extends AbstractSDVH implements CharactersEventHandler{
    MarkupEventHandler parent;        
    CharsContentTypeHandler charsContentTypeHandler;
    ErrorCatcher contextErrorCatcher;
    
    ArrayList<AData> dataMatches;
    ArrayList<AValue> valueMatches;
	ArrayList<AListPattern> listMatches;
    ArrayList<CharsActiveTypeItem> matches;
    
    ErrorCatcher currentErrorCatcher;
    int currentIndex;
    
    CharactersValidationHandler(){
        super();
        
        dataMatches = new ArrayList<AData>();
        valueMatches = new ArrayList<AValue>();
	    listMatches = new ArrayList<AListPattern>();
        matches = new ArrayList<CharsActiveTypeItem>();
        
        currentIndex = -1;
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
    
	void init(MarkupEventHandler parent, CharsContentTypeHandler charsContentTypeHandler, ErrorCatcher contextErrorCatcher){
	    this.parent = parent;
	    this.charsContentTypeHandler = charsContentTypeHandler;
	    this.contextErrorCatcher = contextErrorCatcher;
	}
	
	public void recycle(){
	    reset();
	    pool.recycle(this);
	}
	
	public MarkupEventHandler getParentHandler(){
	    return parent;
	}
	
    public void handleChars(char[] chars, AElement type, boolean isComplexContent) throws SAXException{
        if(isComplexContent){// when true, it means element siblings are certainly present, all data would result in errors anyway
            if(type.allowsText()){		
                matchHandler.handleTextMatches(type);
                matches.addAll(matchHandler.getTextMatches());
                handleAddToParent();
                return;
            }
        }
                
	    int dataOffset = -1;    
        int valueOffset = -1; 	    
        int listOffset = -1;    
        int textOffset = -1;
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
		if(type.allowsListPatterns()){
			listMatches.addAll(matchHandler.getListPatternMatches());
			listOffset = matches.size();
			matches.addAll(listMatches);	
		}
		if(type.allowsText()){			
		    textOffset = matches.size();
            matches.addAll(matchHandler.getTextMatches());            
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
	
	public void handleString(String value, AAttribute type, boolean isComplexContent) throws SAXException{
	    if(isComplexContent){
            if(type.allowsText()){			
                matchHandler.handleTextMatches(type);
                matches.addAll(matchHandler.getTextMatches());
                handleAddToParent();
                return;
            }	            
        }
        	    
	    int dataOffset = -1;    
        int valueOffset = -1; 	    
        int listOffset = -1;    
        int textOffset = -1;
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
		if(type.allowsListPatterns()){
			listMatches.addAll(matchHandler.getListPatternMatches());
			listOffset = matches.size();
			matches.addAll(listMatches);	
		}
		if(type.allowsText()){			
		    textOffset = matches.size();
            matches.addAll(matchHandler.getTextMatches());            
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
	        charsContentTypeHandler.addChars(matches.get(0));
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
	            charsContentTypeHandler.addChars(matches, temporaryMessageStorage);
	        }else{
	            charsContentTypeHandler.addChars(matches, externalConflictHandler.getDisqualified(), temporaryMessageStorage);
	        }
	    }
	}
	
	boolean mustHandleError(char[] chars, APattern pattern){
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){
			if(matches.size() == 1){
			    if(pattern.isRequiredBranch())return true;
			    return !(chars.length == 0 || spaceHandler.isSpace(chars));
            }
            return true;
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
            return true;
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){
		    // The characterContentDescriptor was empty, so it was not set in 
		    // the stack, the itemId is that of the element
		    if(chars.length != 0) throw new IllegalStateException();		    
		    if(pattern.isRequiredBranch())return true;
		    return false;
		}else{
		    throw new IllegalStateException();
		}
	}
	
	void handleError(DatatypedActiveTypeItem item, String datatypeErrorMessage){	    
	    externalConflictHandler.disqualify(currentIndex);	    
	    
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){			
			currentErrorCatcher.characterContentDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){		   
			currentErrorCatcher.attributeValueDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage);
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){			
			currentErrorCatcher.characterContentDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage); 
		}else{
			throw new IllegalStateException();
		}		
	}	
	
	void handleError(AValue value){	    
	    externalConflictHandler.disqualify(currentIndex);
	    
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){			
			currentErrorCatcher.characterContentValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){		    
			currentErrorCatcher.attributeValueValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value);
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){			
			currentErrorCatcher.characterContentValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value); 
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
	    externalConflictHandler.disqualify(currentIndex);	    
	    setCurrentErrorCatcher();
		currentErrorCatcher.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
		
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
	
		
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.excessiveContent(context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.excessiveContent(context, excessiveDefinition, inputRecordIndex);
	}
	
	public void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.missingContent(context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
	}
	
	public void illegalContent(Rule context, int startInputRecordIndex){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.illegalContent(context, startInputRecordIndex);
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
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
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
		externalConflictHandler.disqualify(currentIndex);
		setCurrentErrorCatcher();
		currentErrorCatcher.characterContentExceptedError(inputRecordIndex, charsDefinition);
	}	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
		externalConflictHandler.disqualify(currentIndex);
		setCurrentErrorCatcher();
		currentErrorCatcher.attributeValueExceptedError(inputRecordIndex, charsDefinition);
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
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenValueError(inputRecordIndex, charsDefinition);
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenExceptedError(inputRecordIndex, charsDefinition);
	}
	
	public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.unresolvedListTokenInContextError(inputRecordIndex, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousListTokenInContextWarning(inputRecordIndex, possibleDefinitions);
    }
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.missingCompositorContent(context, startInputRecordIndex, definition, expected, found);
	}
	
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
}
