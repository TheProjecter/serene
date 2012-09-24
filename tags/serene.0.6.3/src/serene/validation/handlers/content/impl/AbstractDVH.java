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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.DataType;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SExceptPattern;

import serene.validation.handlers.content.EventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;
import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.StructuredDataMatchPath;

import serene.util.SpaceCharsHandler;

abstract class AbstractDVH extends AbstractCH implements ErrorCatcher{
    ValidationContext validationContext;        	
    SpaceCharsHandler spaceHandler;
	AbstractDVH(){
		super();
	}
	
	void init(MatchHandler matchHandler, ValidationContext validationContext, SpaceCharsHandler spaceHandler, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor, ValidatorEventHandlerPool pool){
	    super.init(matchHandler, activeInputDescriptor, inputStackDescriptor, pool);
        this.validationContext = validationContext;
        this.spaceHandler = spaceHandler;
	}
	
	void validateData(char[] chars, DataType type, DataMatchPath dataPath) throws SAXException{
	    SData dataPattern = dataPath.getData();
        try{
            Datatype datatype = dataPattern.getDatatype(); 
            if(datatype == null){
                if(mustHandleError(chars, dataPath)){
                    handleError(dataPattern, "No datatype definition.");
                } 
                return;
            }
            datatype.checkValid(new String(chars), validationContext);
            SExceptPattern exceptPattern = dataPattern.getExceptPattern(0);
            if(exceptPattern != null){
                // test the except	
                ExceptPatternValidationHandler epvh = pool.getExceptPatternValidationHandler(dataPattern, exceptPattern, this, this);
                /*exceptPattern.assembleDefinition();*/
                epvh.handleChars(chars, exceptPattern);
                /*exceptPattern.releaseDefinition();*/
                epvh.recycle();
            }
        }catch(DatatypeException de){
            if(mustHandleError(chars, dataPath)){
                handleError(dataPattern, de.getMessage());
			}            
        }
	}
	
	void validateData(String value, DataType type, DataMatchPath dataPath) throws SAXException{
	    SData dataPattern = dataPath.getData();
        try{
            Datatype datatype = dataPattern.getDatatype(); 
            if(datatype == null){
                if(mustHandleError(value.toCharArray(), dataPath)){
                    handleError(dataPattern, "No datatype definition.");
                } 
                return;
            }
            datatype.checkValid(value, validationContext);
            SExceptPattern exceptPattern = dataPattern.getExceptPattern(0);
            if(exceptPattern != null){
                // test the except					
                ExceptPatternValidationHandler epvh = pool.getExceptPatternValidationHandler(dataPattern, exceptPattern, this, this);
                /*exceptPattern.assembleDefinition();*/
                epvh.handleString(value, exceptPattern);
                /*exceptPattern.releaseDefinition();*/
                epvh.recycle();
            }
        }catch(DatatypeException de){
            if(mustHandleError(value.toCharArray(), dataPath)){
                handleError(dataPattern, de.getMessage());
            }
        }	
	}
	
	void validateValue(char[] chars, DataType type, ValueMatchPath valuePath){
	    SValue valuePattern = valuePath.getValue();
        try{
            Datatype datatype = valuePattern.getDatatype(); 
            if(datatype == null){
                if(mustHandleError(chars, valuePath)){
                    handleError(valuePattern, "No datatype definition.");
                } 
                return;
            }
            datatype.checkValid(new String(chars), validationContext);
            String charContent = valuePattern.getCharContent();
            Object o1 = datatype.createValue(charContent, validationContext);
            Object o2 = datatype.createValue(new String(chars), validationContext);            
            if(!datatype.sameValue(o1, o2)){
                if(mustHandleError(chars, valuePath)){
                    handleError(valuePattern);
                }
            }
        }catch(DatatypeException de){
            if(mustHandleError(chars, valuePath)){
                handleError(valuePattern, de.getMessage());
            }
        }
	}
	
	void validateValue(String value, DataType type, ValueMatchPath valuePath){
        SValue valuePattern = valuePath.getValue();	    
        try{				
            Datatype datatype = valuePattern.getDatatype(); 
            if(datatype == null){
                if(mustHandleError(value.toCharArray(), valuePath)){
                    handleError(valuePattern, "No datatype definition.");
                } 
                return;
            }
            datatype.checkValid(value, validationContext);
            String charContent = valuePattern.getCharContent();
            Object o1 = datatype.createValue(charContent, validationContext);
            Object o2 = datatype.createValue(value, validationContext);            
            if(!datatype.sameValue(o1, o2)){
                if(mustHandleError(value.toCharArray(), valuePath)){
                    handleError(valuePattern);
                }
            }
        }catch(DatatypeException de){
            if(mustHandleError(value.toCharArray(), valuePath)){
                handleError(valuePattern, de.getMessage());
            }
        }
	}
	
	abstract boolean mustHandleError(char[] chars, StructuredDataMatchPath path);
	
	abstract void handleError(SPattern item, String datatypeErrorMessage);	
	abstract void handleError(SValue value);
	
}
