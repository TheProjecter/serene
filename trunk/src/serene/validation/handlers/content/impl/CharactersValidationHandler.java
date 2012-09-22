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
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;

import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.MarkupEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.ListPatternMatchPath;
import serene.validation.handlers.match.CharsMatchPath;
import serene.validation.handlers.match.StructuredDataMatchPath;

class CharactersValidationHandler extends AbstractSDVH implements CharactersEventHandler{
    MarkupEventHandler parent;        
    CharsContentTypeHandler charsContentTypeHandler;
    ErrorCatcher contextErrorCatcher;
    
    ArrayList<DataMatchPath> dataMatchPathes;
    ArrayList<ValueMatchPath> valueMatchPathes;
	ArrayList<ListPatternMatchPath> listMatchPathes;
    ArrayList<CharsMatchPath> matchPathes;
    
    ErrorCatcher currentErrorCatcher;
    int currentIndex;
    
    CharactersValidationHandler(){
        super();
        
        dataMatchPathes = new ArrayList<DataMatchPath>();
        valueMatchPathes = new ArrayList<ValueMatchPath>();
	    listMatchPathes = new ArrayList<ListPatternMatchPath>();
        matchPathes = new ArrayList<CharsMatchPath>();
        
        currentIndex = -1;
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
	
    public void handleChars(char[] chars, SElement type, boolean isComplexContent) throws SAXException{
        //System.out.println("CHARACTERS VALIDATION HANDLER chars="+new String(chars)+"|  type="+type);
        if(isComplexContent){// when true, it means element siblings are certainly present, all data would result in errors anyway
            if(type.allowsText()){		
                matchHandler.handleTextMatches(type);
                matchPathes.addAll(matchHandler.getTextMatchPathes());
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
		if(type.allowsText()){			
		    textOffset = matchPathes.size();
            matchPathes.addAll(matchHandler.getTextMatchPathes());            
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
	
	public void handleString(String value, SAttribute type, boolean isComplexContent) throws SAXException{
	    if(isComplexContent){
            if(type.allowsText()){			
                matchHandler.handleTextMatches(type);
                matchPathes.addAll(matchHandler.getTextMatchPathes());
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
		if(type.allowsText()){			
		    textOffset = matchPathes.size();
            matchPathes.addAll(matchHandler.getTextMatchPathes());            
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
	        charsContentTypeHandler.addChars(matchPathes.get(0));
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
	            charsContentTypeHandler.addChars(matchPathes, temporaryMessageStorage);
	        }else{
	            charsContentTypeHandler.addChars(matchPathes, externalConflictHandler.getDisqualified(), temporaryMessageStorage);
	        }
	    }
	}
	
	boolean mustHandleError(char[] chars, StructuredDataMatchPath path){
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.CHARACTER_CONTENT){
			if(matchPathes.size() == 1){
			    if(path.isRequiredBranch())return true;
			    return !(chars.length == 0 || spaceHandler.isSpace(chars));
            }
            return true;
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ATTRIBUTE){
            return true;
		}else if(inputStackDescriptor.getItemId() == InputStackDescriptor.ELEMENT){
		    // The characterContentDescriptor was empty, so it was not set in 
		    // the stack, the itemId is that of the element
		    if(chars.length != 0) throw new IllegalStateException();		    
		    return path.isRequiredBranch();
		}else{
		    throw new IllegalStateException();
		}
	}
	
	void handleError(SPattern item, String datatypeErrorMessage){	    
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
	
	void handleError(SValue value){	    
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
		currentErrorCatcher.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
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
		externalConflictHandler.disqualify(currentIndex);
		setCurrentErrorCatcher();
		currentErrorCatcher.characterContentExceptedError(inputRecordIndex, charsDefinition);
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
		externalConflictHandler.disqualify(currentIndex);
		setCurrentErrorCatcher();
		currentErrorCatcher.attributeValueExceptedError(inputRecordIndex, charsDefinition);
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
