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
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.APattern;

import serene.validation.handlers.content.EventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.TemporaryMessageStorage;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;

import serene.util.SpaceCharsHandler;

import sereneWrite.MessageWriter;


abstract class AbstractDVH extends AbstractCH implements ErrorCatcher{
    ValidationContext validationContext;        	
    SpaceCharsHandler spaceHandler;
	AbstractDVH(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	void init(MatchHandler matchHandler, ValidationContext validationContext, SpaceCharsHandler spaceHandler, ValidationItemLocator validationItemLocator, ValidatorEventHandlerPool pool){
	    super.init(matchHandler, validationItemLocator, pool);
        this.validationContext = validationContext;
        this.spaceHandler = spaceHandler;
	}
	
	void validateData(char[] chars, DataActiveType type, AData dataPattern) throws SAXException{		
        try{
            dataPattern.datatypeMatches(chars, validationContext);
            AExceptPattern exceptPattern = dataPattern.getExceptPattern();
            if(exceptPattern != null){
                // test the except	
                ExceptPatternValidationHandler epvh = pool.getExceptPatternValidationHandler(dataPattern, exceptPattern, this, this);
                exceptPattern.assembleDefinition();
                epvh.handleChars(chars, exceptPattern);
                exceptPattern.releaseDefinition();
                epvh.recycle();
            }
        }catch(DatatypeException de){
            if(mustHandleError(chars, dataPattern)){
                handleError(dataPattern, de.getMessage());
			}            
        }
	}
	
	void validateData(String value, DataActiveType type, AData dataPattern) throws SAXException{
        try{
            dataPattern.datatypeMatches(value, validationContext);
            AExceptPattern exceptPattern = dataPattern.getExceptPattern();
            if(exceptPattern != null){
                // test the except					
                ExceptPatternValidationHandler epvh = pool.getExceptPatternValidationHandler(dataPattern, exceptPattern, this, this);
                exceptPattern.assembleDefinition();
                epvh.handleString(value, exceptPattern);
                exceptPattern.releaseDefinition();
                epvh.recycle();
            }
        }catch(DatatypeException de){
            if(mustHandleError(value.toCharArray(), dataPattern)){
                handleError(dataPattern, de.getMessage());
            }
        }	
	}
	
	void validateValue(char[] chars, DataActiveType type, AValue valuePattern){
        try{
            valuePattern.datatypeMatches(chars, validationContext);
            if(!valuePattern.valueMatches(chars, validationContext)){				    
                if(mustHandleError(chars, valuePattern)){
                    handleError(valuePattern);									
                }
            }
        }catch(DatatypeException de){
            if(mustHandleError(chars, valuePattern)){
                handleError(valuePattern, de.getMessage());
            }
        }
	}
	
	void validateValue(String value, DataActiveType type, AValue valuePattern){
        try{				
            valuePattern.datatypeMatches(value, validationContext);
            if(!valuePattern.valueMatches(value, validationContext)){
                if(mustHandleError(value.toCharArray(), valuePattern)){
                    handleError(valuePattern);
                }
            }
        }catch(DatatypeException de){
            if(mustHandleError(value.toCharArray(), valuePattern)){
                handleError(valuePattern, de.getMessage());
            }
        }
	}
	
	abstract boolean mustHandleError(char[] chars, APattern pattern);
	
	abstract void handleError(DatatypedActiveTypeItem item, String datatypeErrorMessage);	
	abstract void handleError(AValue value);
	
}
