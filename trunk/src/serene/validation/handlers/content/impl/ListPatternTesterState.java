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

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AExceptPattern;


import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;

import serene.util.SpaceCharsHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

abstract class ListPatternTesterState extends AbstractCVH  implements ErrorCatcher{
	boolean hasError;	
	/**
	* Set to true every time a token has at least a match in the context of the 
	* ListPattern. 
	*/	
	boolean tokenMatch;
	/**
	* Set to true at the beginig of every token's validation and to false 
	* when an error is detected. Used to check if a token was shifted correctly.
	*/
	boolean tokenValid;
		
	StackHandler stackHandler;
	
	List<CharsActiveTypeItem> totalCharsItemMatches;
	ListPatternTesterState(MessageWriter debugWriter){
		super(debugWriter);
	}

	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, MatchHandler matchHandler){		
		throw new IllegalStateException();
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, SpaceCharsHandler spaceHandler, MatchHandler matchHandler){
		this.pool = pool;		
		this.validationItemLocator = validationItemLocator;
		this.matchHandler = matchHandler;
		this.spaceHandler = spaceHandler;		
	}	
	
	void init(List<CharsActiveTypeItem> totalCharsItemMatches, ValidationContext validationContext, ErrorCatcher errorCatcher){
		this.totalCharsItemMatches = totalCharsItemMatches;
		this.validationContext = validationContext;
		this.errorCatcher = errorCatcher;
	}
	
	public void recycle(){
		throw new IllegalStateException();
	}	
	
	void validateData(char[] chars, DataActiveType type){		
		for(int i = 0; i < dataMatches.size(); i++){
			AData data = dataMatches.get(i);
			try{
				data.datatypeMatches(chars, validationContext);
				AExceptPattern except = data.getExceptPattern();
				if(except == null){
					charsItemMatches.add(data);					
				}else{
					// test the except
					// the pattern will be added there					
					ExceptPatternTester ept = pool.getExceptPatternTester(data, charsItemMatches, totalCount, this);
					except.assembleDefinition();
					ept.handleChars(chars, except);
                    except.releaseDefinition();
					ept.recycle();
				}
			}catch(DatatypeException de){
				//System.out.println(type+" datatype 1 ERROR "+data+" "+new String(chars));
				//System.out.println(de.getMessage());
				if(totalCount == 1){
					reportDatatypeError(data, de.getMessage());
					charsItemMatches.add(data);
				}
			}
		}
	}
	
	void validateData(String value, DataActiveType type){		
		for(int i = 0; i < dataMatches.size(); i++){
			AData data = dataMatches.get(i);
			try{
				data.datatypeMatches(value, validationContext);
				AExceptPattern except = data.getExceptPattern();
				if(except == null){
					charsItemMatches.add(data);					
				}else{
					// test the except
					// the pattern will be added there
					ExceptPatternTester ept = pool.getExceptPatternTester(data, charsItemMatches, totalCount, this);
					except.assembleDefinition();
					ept.handleString(value, except);
                    except.releaseDefinition();
					ept.recycle();
				}
			}catch(DatatypeException de){
				//System.out.println(type+" datatype 2 ERROR "+data);
				//System.out.println(de.getMessage());
				if(totalCount == 1){
					reportDatatypeError(data, de.getMessage());
					charsItemMatches.add(data);
				}
			}
		}		
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