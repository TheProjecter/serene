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
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SListPattern;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.content.DataEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;
import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.UnstructuredDataMatchPath;
import serene.validation.handlers.match.StructuredDataMatchPath;

import serene.util.SpaceCharsHandler;

class DataValidationHandler extends AbstractDVH implements DataEventHandler{
    ListPatternValidationHandler parent;            
    DataContentTypeHandler dataContentTypeHandler;
    ErrorCatcher contextErrorCatcher;
    
    ArrayList<DataMatchPath> dataMatchPathes;
    ArrayList<ValueMatchPath> valueMatchPathes;
    ArrayList<UnstructuredDataMatchPath> matchPathes;
    
    ErrorCatcher currentErrorCatcher;
    int currentIndex;
    
	DataValidationHandler(){
		super();
		
		dataMatchPathes = new ArrayList<DataMatchPath>();
        valueMatchPathes = new ArrayList<ValueMatchPath>();
        matchPathes = new ArrayList<UnstructuredDataMatchPath>();
		
		currentIndex = -1;
	}
	
	
	void init(ListPatternValidationHandler parent, DataContentTypeHandler dataContentTypeHandler, ErrorCatcher contextErrorCatcher){
	    this.parent = parent;
	    this.dataContentTypeHandler = dataContentTypeHandler;
	    this.contextErrorCatcher = contextErrorCatcher;
	}
	
	void reset(){
	    super.reset();
        
		dataMatchPathes.clear();
        valueMatchPathes.clear();
	    matchPathes.clear();
	    
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
	
	public void handleChars(char[] chars, SListPattern type) throws SAXException{
	   //System.out.println("DATA VALIDATION HANDLER chars="+new String(chars)+"  type="+type);
	   int dataOffset = -1;  
       int valueOffset = -1;
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
		
		//System.out.println("DATA VALIDATION HANDLER dataMatches="+dataMatches+"  valueMatches="+valueMatches);
		
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
			
		
		handleAddToParent();
	}
		
	public void handleString(String value, SListPattern type) throws SAXException{	    	    
	    int dataOffset = -1;  
        int valueOffset = -1;
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
		
		handleAddToParent();
	}
	
	
	void handleAddToParent(){
	    if(matchPathes.size() == 1){
	        dataContentTypeHandler.addData(matchPathes.get(0));
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
	            dataContentTypeHandler.addData(matchPathes, temporaryMessageStorage);
	        }else if(qualifiedCount == 1){
	            externalConflictHandler.init(matchPathesCount);
                parent.addData(matchPathes.get(externalConflictHandler.getNextQualified(0)));
                if(temporaryMessageStorage != null) {	            
                    for(int i = 0; i < temporaryMessageStorage.length; i++){                    
                        if(temporaryMessageStorage[i] != null){
                            /*temporaryMessageStorage[i].setDiscarded(true);*/
                            temporaryMessageStorage[i].clear();
                        }
                    }
                }
            }else{
	            dataContentTypeHandler.addData(matchPathes, externalConflictHandler.getDisqualified(), temporaryMessageStorage);
	        }
	    }
	}
	
	boolean mustHandleError(char[] chars, StructuredDataMatchPath path){
		return true;
	}
	
	
	void handleError(SPattern item, String datatypeErrorMessage){	    
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){			
			currentErrorCatcher.listTokenDatatypeError(inputStackDescriptor.getCurrentItemInputRecordIndex(), item, datatypeErrorMessage); 
		}else{
			throw new IllegalStateException();
		}		
	}	
	
	void handleError(SValue value){
	    externalConflictHandler.disqualify(currentIndex);
	    setCurrentErrorCatcher();
	    
	    if(inputStackDescriptor.getItemId() == InputStackDescriptor.LIST_TOKEN){			
			currentErrorCatcher.listTokenValueError(inputStackDescriptor.getCurrentItemInputRecordIndex(), value); 
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
		throw new IllegalStateException();
	}
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
		throw new IllegalStateException();
	}
	
	
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
		throw new IllegalStateException();
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
		throw new IllegalStateException();
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
		throw new IllegalStateException();
	}
	
	public void illegalContent(SRule context, int startInputRecordIndex){
		throw new IllegalStateException();
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
		throw new IllegalStateException();
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
		throw new IllegalStateException();
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
		externalConflictHandler.disqualify(currentIndex);
		setCurrentErrorCatcher();
		currentErrorCatcher.listTokenExceptedError(inputRecordIndex, charsDefinition);
	}	
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
		throw new IllegalStateException();
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
    }
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
		throw new IllegalStateException();
	}
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
}
