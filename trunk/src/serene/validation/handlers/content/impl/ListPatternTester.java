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

import org.xml.sax.SAXException;

import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AValue;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;

import serene.util.SpaceCharsHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;


// Errors are reported only if some token match has been found. This means that
// if the first token(s) result in errors it is impossible to know if these errors 
// need to be reported or not.
// Solutions:
// 1. Store the error messages and refire them later if necessary
// 2. When a match is found set the ListPatternTester in reporting mode, if errors
// have happened already, that is the match didn't occur at the first tested token,
// redo the chars matching(keep the Tester in report mode)
// Go for 2.

class ListPatternTester extends AbstractCVH{	
	ListPatternTesterState state;
	SimpleListPatternTester simpleState;
	ReportingListPatternTester reportingState;
	
	MessageWriter debugWriter;
	
	ListPatternTester(MessageWriter debugWriter){
		super(debugWriter);		
		reportingState = new ReportingListPatternTester(debugWriter);
		simpleState = new SimpleListPatternTester(debugWriter);
	}
	
	
	public void recycle(){
		pool.recycle(this);
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, MatchHandler matchHandler){		
		throw new IllegalStateException();
	}	
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, SpaceCharsHandler spaceHandler, MatchHandler matchHandler){				
		this.pool = pool;		
		reportingState.init(pool, validationItemLocator, spaceHandler, matchHandler);
		simpleState.init(pool, validationItemLocator, spaceHandler, matchHandler);		
	}	
	
	// init sets the list of charsItemMatches and sets the state according to 
	// the number of available matches
	// see that the lists of matches fit properly
	void init(List<CharsActiveTypeItem> totalCharsItemMatches, int totalCount, ValidationContext validationContext, ErrorCatcher errorCatcher){		
			
		if(totalCount == 1)	state = reportingState;
		else state = simpleState;
		
		state.init(totalCharsItemMatches, validationContext, errorCatcher);
	}
	
	public void handleChars(char[] chars, DataActiveType type) throws SAXException{	
		state.handleChars(chars, type);
	}
	public void handleChars(char[] chars, StructuredDataActiveType type) throws SAXException{		
		state.handleChars(chars, type);
	}
	public void handleChars(char[] chars, CharsActiveType type, boolean isComplexContent){
		throw new IllegalStateException();
	}
		
	
	public void handleString(String value, DataActiveType type) throws SAXException{
		state.handleString(value, type);
	}
	public void handleString(String value, StructuredDataActiveType type) throws SAXException{
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