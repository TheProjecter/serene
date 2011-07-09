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
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;

import serene.validation.schema.simplified.components.SPattern;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ContextErrorHandler;

import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.Reusable;

import sereneWrite.MessageWriter;

class AttributeDefaultHandler extends ComparableAEH{
		
    ComparableEEH parent;  
	
	AttributeDefaultHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
		
	public void recycle(){
		pool.recycle(this);
	}
	
    void init(ComparableEEH parent){
        // TODO see if you need an intermediary class for EEH to make this 
        // available only to ErrorEEH and EleemntDefaultHandler 
        this.parent = parent;
    }	
    
    public ComparableEEH getParentHandler(){
        return parent;
    }
    
    public void handleAttribute(String value){}
    
	void validateValue(String value){
        throw new IllegalStateException();
    }

	void validateInContext(){
        throw new IllegalStateException();
    }
    
    
    
    boolean functionalEquivalent(ComparableAEH other){
        return other.functionalEquivalent(this);
    }
    
    boolean functionalEquivalent(AttributeDefinitionHandler other){
        return false;
    }    
	boolean functionalEquivalent(UnexpectedAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAmbiguousAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnknownAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(AttributeConcurrentHandler other){
        return false;        
	}	
	boolean functionalEquivalent(AttributeParallelHandler other){
        return false;
    }
    boolean functionalEquivalent(AttributeDefaultHandler other){
        return true;
    }
    
	public String toString(){
		return "AttributeDefaultHandler ";
	}	
}
