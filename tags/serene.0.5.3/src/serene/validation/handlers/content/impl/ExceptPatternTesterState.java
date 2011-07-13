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

import java.util.List;

import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;

import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.content.util.ValidationItemLocator;
import serene.validation.handlers.match.MatchHandler;
import serene.validation.handlers.stack.StackHandler;

import sereneWrite.MessageWriter;

abstract class ExceptPatternTesterState extends AbstractCVH  implements ErrorCatcher{
    AData data;
	
	boolean hasError;			
	List<CharsActiveTypeItem> totalCharsItemMatches;
	
	StackHandler stackHandler;
	
	ExceptPatternTesterState(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	
	void init(AData data, List<CharsActiveTypeItem> totalCharsItemMatches, ValidationContext validationContext, ErrorCatcher errorCatcher){
		this.data = data;
		this.totalCharsItemMatches = totalCharsItemMatches;
		this.validationContext = validationContext;
		this.errorCatcher = errorCatcher;
	}

	public void handleChars(char[] chars, DataActiveType type){
		throw new IllegalStateException();
	}
	public void handleChars(char[] chars, CharsActiveType type, boolean isComplexContent){
		throw new IllegalStateException();
	}
		
	
	public void handleString(String value, DataActiveType type){
		throw new IllegalStateException();
	}	
	public void handleString(String value, CharsActiveType type, boolean isComplexContent){
		throw new IllegalStateException();
	}
	
	void handleAddToParent(DataActiveType type){
		throw new IllegalStateException();
	}

	void handleAddToParent(StructuredDataActiveType type){
		throw new IllegalStateException();
	}	
	
	void handleAddToParent(CharsActiveType type){
		throw new IllegalStateException();
	}
	
    void reportDatatypeError(DatatypedActiveTypeItem item, String message){
		if(validationItemLocator.isAttributeContext()){
			attributeValueDatatypeError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), item, message);
		}else if(validationItemLocator.isElementContext()){
			characterContentDatatypeError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), item, message);
		}else{
			throw new IllegalStateException();
		}
	}
	
	void reportValueError(AValue value){
		if(validationItemLocator.isAttributeContext()){
			attributeValueValueError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), value);
		}else if(validationItemLocator.isElementContext()){
			characterContentValueError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), value);
		}else{
			throw new IllegalStateException();
		}
	}
}