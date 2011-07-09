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

import serene.util.SpaceCharsHandler;

import sereneWrite.MessageWriter;

class ExceptPatternTester extends AbstractCVH{	
	ExceptPatternTesterState state;
	SimpleExceptPatternTester simpleState;
	ReportingExceptPatternTester reportingState;
	
	MessageWriter debugWriter;
	
	ExceptPatternTester(MessageWriter debugWriter){
		super(debugWriter);		
		simpleState = new SimpleExceptPatternTester(debugWriter);
		reportingState = new ReportingExceptPatternTester(debugWriter);
	}
		
	public void recycle(){
		pool.recycle(this);
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, MatchHandler matchHandler, SpaceCharsHandler spaceHandler){		
		this.pool = pool;
		simpleState.init(pool, validationItemLocator, matchHandler, spaceHandler);
		reportingState.init(pool, validationItemLocator, matchHandler, spaceHandler);
	}	
	
	void init(AData data, List<CharsActiveTypeItem> totalCharsItemMatches, int totalCount, ValidationContext validationContext, ErrorCatcher errorCatcher){
		if(totalCount == 1)state = reportingState;			
		else state = simpleState;
		
		state.init(data, totalCharsItemMatches, validationContext, errorCatcher);
	}
	
	public void handleChars(char[] chars, DataActiveType type){
		throw new IllegalStateException();
	}
	public void handleChars(char[] chars, StructuredDataActiveType type){	
		state.handleChars(chars, type);
	}
	public void handleChars(char[] chars, CharsActiveType type, boolean isComplexContent){
		throw new IllegalStateException();
	}
		
	
	public void handleString(String value, DataActiveType type){
		throw new IllegalStateException();
	}	
	public void handleString(String value, StructuredDataActiveType type){
		state.handleString(value, type);
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
		throw new IllegalStateException();
	}
	
	void reportValueError(AValue value){
		throw new IllegalStateException();
	}
}