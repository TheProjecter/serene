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

import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.content.DefaultValueAttributeHandler;
import serene.validation.handlers.content.CharactersEventHandler;

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;


import sereneWrite.MessageWriter;

class DefaultValueAttributeValidationHandler extends AttributeDefinitionHandler implements DefaultValueAttributeHandler{
		
	ErrorCatcher errorCatcher;
    
	DefaultValueAttributeValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
	
    
    public void recycle(){
        reset();
        pool.recycle(this);
    } 
    void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, MatchHandler matchHandler){
		super.init(pool, validationItemLocator);
		this.matchHandler = matchHandler;
	}
    
    public void init(AAttribute attribute, ErrorCatcher errorCatcher){       
		this.errorCatcher = errorCatcher;
		this.attribute = attribute;
		attribute.assembleDefinition();
		stackHandler = attribute.getStackHandler(errorCatcher);
	}
    
    public void reset(){
        errorCatcher = null;
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}
        attribute.releaseDefinition();
	}
    
    public ValidatingEEH getParentHandler(){
        return null;
    }
    
	public void handleAttribute(String value){
		validateValue(value);
	}	
    
    void validateValue(String value){
		if(!attribute.allowsChars()){
			errorCatcher.unexpectedAttributeValue(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), attribute);
			return;
		}				
		CharactersEventHandler ceh = pool.getAttributeValueValidationHandler(this, errorCatcher);
		ceh.handleString(value, (CharsActiveType)attribute, false);		
        ceh.recycle();
	}

    void validateInContext(){
		throw new IllegalStateException();
	}
    
    void addChars(CharsActiveTypeItem charsDefinition){
		stackHandler.shift(charsDefinition);
	}
	
	void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions){
		if(!stackHandler.handlesConflict()) stackHandler = attribute.getStackHandler(stackHandler, errorCatcher);
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions);
	}
 
    
    boolean functionalEquivalent(ComparableAEH other){
        throw new IllegalStateException();
    }    
    boolean functionalEquivalent(AttributeDefinitionHandler other){
        throw new IllegalStateException();
    }    
	boolean functionalEquivalent(UnexpectedAttributeHandler other){
        throw new IllegalStateException();
    }
	boolean functionalEquivalent(UnexpectedAmbiguousAttributeHandler other){
        throw new IllegalStateException();
    }
	boolean functionalEquivalent(UnknownAttributeHandler other){
        throw new IllegalStateException();
    }
	boolean functionalEquivalent(AttributeConcurrentHandler other){
        throw new IllegalStateException();
	}	
	boolean functionalEquivalent(AttributeParallelHandler other){
        throw new IllegalStateException();
    }
    
    public String toString(){
		return "DefaultValueAttributeValidationHandler "+attribute;
	}
}
