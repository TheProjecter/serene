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
import java.util.BitSet;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.SAttribute;


import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.content.DefaultValueAttributeHandler;
import serene.validation.handlers.content.CharactersEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.match.MatchHandler;
import serene.validation.handlers.match.CharsMatchPath;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.TemporaryMessageStorage;

class DefaultValueAttributeValidationHandler extends AttributeDefinitionHandler 
                                            implements DefaultValueAttributeHandler{
		
	ErrorCatcher errorCatcher;
    
	DefaultValueAttributeValidationHandler(){
		super();
	}
	
    
    public void recycle(){
        reset();
        pool.recycle(this);
    } 
    void init(ValidatorEventHandlerPool pool, ValidatorStackHandlerPool stackHandlerPool, InputStackDescriptor inputStackDescriptor, MatchHandler matchHandler){
		super.init(pool, inputStackDescriptor);
		this.matchHandler = matchHandler;
		this.stackHandlerPool = stackHandlerPool;
	}
    
    public void init(SAttribute attribute, ErrorCatcher errorCatcher){       
		this.errorCatcher = errorCatcher;
		this.attribute = attribute;
		/*attribute.assembleDefinition();*/
	}
    
    public void reset(){
        super.reset();
        errorCatcher = null;		
	}
    
    public ValidatingEEH getParentHandler(){
        return null;
    }
    
	public void handleAttribute(String value) throws SAXException{	    
		validateValue(value);
	}	
    
    void validateValue(String value) throws SAXException{
        stackHandler = stackHandlerPool.getContextStackHandler(attribute, errorCatcher);
		/*if(!attribute.allowsCharsContent()){
			errorCatcher.unexpectedAttributeValue(inputStackDescriptor.getCurrentItemInputRecordIndex(), attribute);
			return;
		}*/
		CharactersValidationHandler cvh = pool.getCharactersValidationHandler(this, this, errorCatcher);
		cvh.handleString(value, attribute, false);	
        cvh.recycle();
        stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
	}

    void validateInContext(){
		throw new IllegalStateException();
	}
    
	// CharactersValidationHandler
	//==========================================================================
    public void addChars(CharsMatchPath charsDefinition){
		stackHandler.shift(charsDefinition);
	}
	
	public void addChars(List<? extends CharsMatchPath> charsCandidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, errorCatcher);
		    oldStackHandler.recycle();
		} 
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions, temporaryMessageStorage);
	}
	
	public void addChars(List<? extends CharsMatchPath> charsCandidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
		if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, errorCatcher);
		    oldStackHandler.recycle();
		} 
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions, disqualified, temporaryMessageStorage);
	}
	//==========================================================================
 
    
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
