/*
Copyright 2010 Radu Cernuta 

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

import java.util.Arrays;
import java.util.List;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AListPattern;

import serene.validation.schema.simplified.components.SPattern;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.Reusable;

import sereneWrite.MessageWriter;

class AttributeValidationHandler extends ValidatingAEH{
	AAttribute attribute;
	
	MatchHandler matchHandler;
	
	StackHandler stackHandler;
	
	ErrorCatcher errorCatcher;
	
	AttributeValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
		
	public void recycle(){
		if(errorCatcher != parent)((Reusable)errorCatcher).recycle();
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}
        attribute.releaseDefinition();
		pool.recycle(this);
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, MatchHandler matchHandler){
		super.init(pool, validationItemLocator);
		this.matchHandler = matchHandler;
	}
	
		
	void init(AAttribute attribute, ElementValidationHandler parent, ErrorCatcher errorCatcher){
		this.parent = parent;
		this.errorCatcher = errorCatcher;
		this.attribute = attribute;
		attribute.assembleDefinition();
		stackHandler = attribute.getStackHandler(errorCatcher);
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
		parent.addAttribute(attribute);
	}

	void addChars(CharsActiveTypeItem charsDefinition){
		stackHandler.shift(charsDefinition);
	}
	
	void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions){
		if(!stackHandler.handlesConflict()) stackHandler = attribute.getStackHandler(stackHandler, errorCatcher);
		stackHandler.shiftAllCharsDefinitions(charsCandidateDefinitions);
	}
    
	public String toString(){
		return "AttributeValidationHandler "+attribute;
	}	
}