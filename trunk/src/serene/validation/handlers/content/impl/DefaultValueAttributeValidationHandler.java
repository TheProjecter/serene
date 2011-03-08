package serene.validation.handlers.content.impl;

import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.content.DefaultValueAttributeHandler;

import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

class DefaultValueAttributeValidationHandler extends AttributeValidationHandler implements DefaultValueAttributeHandler{
		
	DefaultValueAttributeValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
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
    
	public void handleAttribute(String value){
		validateValue(value);
	}	
}
