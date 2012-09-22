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
import serene.validation.schema.active.components.AExceptPattern;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.content.StructuredDataEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.ListPatternMatchPath;
import serene.validation.handlers.match.StructuredDataMatchPath;

class StructuredDataValidationHandler extends AbstractSDVH implements StructuredDataEventHandler{
    ExceptPatternValidationHandler parent;        
    StructuredDataContentTypeHandler structuredDataContentTypeHandler;
    ErrorCatcher contextErrorCatcher;
    
    ArrayList<DataMatchPath> dataMatchPathes;
    ArrayList<ValueMatchPath> valueMatchPathes;
	ArrayList<ListPatternMatchPath> listMatchPathes;
    ArrayList<StructuredDataMatchPath> matchPathes;
    
    ErrorCatcher currentErrorCatcher;
    // Index of the processed candidate in the total matchPathes list.
    // Used for the list pattern validation and error reporting.
    int currentIndex;
    
    StructuredDataValidationHandler(){
        super();
        
        dataMatchPathes = new ArrayList<DataMatchPath>();
        valueMatchPathes = new ArrayList<ValueMatchPath>();
	    listMatchPathes = new ArrayList<ListPatternMatchPath>();
        matchPathes = new ArrayList<StructuredDataMatchPath>();
        
        currentIndex = -1;
    }
    
    void init(ExceptPatternValidationHandler parent, StructuredDataContentTypeHandler structuredDataContentTypeHandler, ErrorCatcher contextErrorCatcher){
        this.parent = parent;
        this.structuredDataContentTypeHandler = structuredDataContentTypeHandler;
        this.contextErrorCatcher = contextErrorCatcher;
    }
    
    void reset(){
        super.reset(); 
        
	    dataMatchPathes.clear();
        valueMatchPathes.clear();
	    listMatchPathes.clear();
	    matchPathes.clear();
	    
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
	
    public void handleChars(char[] chars, SExceptPattern type) throws SAXException{				
        int dataOffset = -1;    
        int valueOffset = -1; 	    
        int listOffset = -1;        
        matchHandler.handleCharsMatches(type);
        
        if(type.allowsDatas()){
		    dataMatchPathes.addAll(matchHandler.getDataMatchPathes());						
			dataOffset = 0;
			matchPathes.addAll(dataMatchPathes);
		}	
		if(type.allowsValues()){
			valueMatchPathes.addAll(matchHandler.getValueMatchPathes());			
			valueOffset = matchPathes.size();
			matchPathes.addAll(valueMatchPathes);				
		}	
		if(type.allowsListPatterns()){
			listMatchPathes.addAll(matchHandler.getListPatternMatchPathes());
			listOffset = matchPathes.size();
			matchPathes.addAll(listMatchPathes);	
		}
		
		if(dataMatchPathes != null && dataMatchPathes.size() > 0){
		    for(int i = 0; i < dataMatchPathes.size(); i++){
			    currentIndex = i + dataOffset;
			    validateData(chars, type, dataMatchPathes.get(i));
			}
		}
        if(valueMatchPathes != null && valueMatchPathes.size() > 0){	
            for(int i = 0; i < valueMatchPathes.size(); i++){
			    currentIndex = i + valueOffset;
			    validateValue(chars, type, valueMatchPathes.get(i));
			}
		}
		if(listMatchPathes != null && listMatchPathes.size() > 0){	
		    for(int i = 0; i < listMatchPathes.size(); i++){ 
			    currentIndex = i + listOffset;
			    validateListPattern(chars, listMatchPathes.get(i).getListPattern());
			}
		}
		
		handleAddToParent();
	}
	
	public void handleString(String value, SExceptPattern type) throws SAXException{
	    int dataOffset = -1;    
        int valueOffset = -1; 	    
        int listOffset = -1;
        matchHandler.handleCharsMatches(type);
        
        if(type.allowsDatas()){
		    dataMatchPathes.addAll(matchHandler.getDataMatchPathes());						
			dataOffset = 0;
			matchPathes.addAll(dataMatchPathes);
		}	
		if(type.allowsValues()){
			valueMatchPathes.addAll(matchHandler.getValueMatchPathes());			
			valueOffset = matchPathes.size();
			matchPathes.addAll(valueMatchPathes);				
		}	
		if(type.allowsListPatterns()){
			listMatchPathes.addAll(matchHandler.getListPatternMatchPathes());
			listOffset = matchPathes.size();
			matchPathes.addAll(listMatchPathes);	
		}
		
		if(dataMatchPathes != null && dataMatchPathes.size() > 0){
		    for(int i = 0; i < dataMatchPathes.size(); i++){
			    currentIndex = i + dataOffset;
			    validateData(value, type, dataMatchPathes.get(i));
			}
		}
        if(valueMatchPathes != null && valueMatchPathes.size() > 0){	
            for(int i = 0; i < valueMatchPathes.size(); i++){
			    currentIndex = i + valueOffset;
			    validateValue(value, type, valueMatchPathes.get(i));
			}
		}
		if(listMatchPathes != null && listMatchPathes.size() > 0){	
		    for(int i = 0; i < listMatchPathes.size(); i++){ 
			    currentIndex = i + listOffset;
			    validateListPattern(value, listMatchPathes.get(i).getListPattern());
			}
		}
		
		handleAddToParent();
	}	
	
	void handleAddToParent(){
	    if(matchPathes.size() == 1){
	        structuredDataContentTypeHandler.addStructuredData(matchPathes.get(0));
	        if(temporaryMessageStorage != null) {	            
                for(int i = 0; i < temporaryMessageStorage.length; i++){                    
                    if(temporaryMessageStorage[i] != null){
                        /*temporaryMessageStorage[i].setDiscarded(true);*/
                        temporaryMessageStorage[i].clear();
                    }
                }
            }
	    }else{
	        int matchPathesCount = matchPathes.size();		
	        int qualifiedCount = matchPathesCount - externalConflictHandler.getDisqualifiedCount();        
	        if(qualifiedCount == 0){
	            // it's error, no need for details, the report will come from except anyway
	            SPattern[] items = new SPattern[matchPathes.size()];
	            for(int i = 0; i < items.length; i++){
	                items[i] = matchPathes.get(i).getChars();
	            }
	            parent.unresolvedCharacterContent(inputStackDescriptor.getCurrentItemInputRecordIndex(), items);
	            if(temporaryMessageStorage != null) {	            
                    for(int i = 0; i < temporaryMessageStorage.length; i++){                    
                        if(temporaryMessageStorage[i] != null){
                            /*temporaryMessageStorage[i].setDiscarded(true);*/
                            temporaryMessageStorage[i].clear();
                        }
                    }
                }
	        }else{
	            structuredDataContentTypeHandler.addStructuredData(matchPathes, externalConflictHandler.getDisqualified(), temporaryMessageStorage);
	        }
	    }
	}
	
	boolean mustHandleError(char[] chars, StructuredDataMatchPath path){
	    //TODO review this
	    /*if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){
			if(matchPathes.size() == 1){
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
	
	
	void handleError(SPattern item, String datatypeErrorMessage){	    
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){			
			currentErrorCatcher.characterContentDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){			
			currentErrorCatcher.characterContentDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
			currentErrorCatcher.attributeValueDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage);
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){
		    // doesn't really matter since the report will come from except anyway			
			currentErrorCatcher.characterContentDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage); 
		}else{
			throw new IllegalStateException();
		}		
	}	
	
	void handleError(SValue value){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){			
			currentErrorCatcher.characterContentValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){			
			currentErrorCatcher.characterContentValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value); 
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
			currentErrorCatcher.attributeValueValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value);
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){
		    // doesn't really matter since the report will come from except anyway			
			currentErrorCatcher.characterContentValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value); 
		}else{
			throw new IllegalStateException();
		}
	}
	
	void setCurrentErrorCatcher(){
	    if(matchPathes.size() == 1){
	        currentErrorCatcher = contextErrorCatcher;
	    }else{
	        if(temporaryMessageStorage == null) temporaryMessageStorage = new TemporaryMessageStorage[matchPathes.size()];
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
	
		
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.misplacedContent(contextDefinition, inputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
		
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
	
		
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.excessiveContent(context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.excessiveContent(context, excessiveDefinition, inputRecordIndex);
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.missingContent(context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
	}
	
	public void illegalContent(SRule context, int startInputRecordIndex){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.illegalContent(context, startInputRecordIndex);
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
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
	}
		
	public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
		throw new IllegalStateException();
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
		throw new IllegalStateException();
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
		throw new IllegalStateException();
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenValueError(inputRecordIndex, charsDefinition);
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.listTokenExceptedError(inputRecordIndex, charsDefinition);
	}
	
	public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.unresolvedListTokenInContextError(inputRecordIndex, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.ambiguousListTokenInContextWarning(inputRecordIndex, possibleDefinitions);
    }
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
		currentErrorCatcher.missingCompositorContent(context, startInputRecordIndex, definition, expected, found);
	}
	
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
} 
