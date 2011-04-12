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
import java.util.ArrayList;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AExceptPattern;


import serene.validation.handlers.content.MarkupEventHandler;
import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;

import sereneWrite.MessageWriter;

abstract class AbstractCVH implements CharactersEventHandler{
	
	ValidationItemLocator validationItemLocator;	
	ValidatorEventHandlerPool pool;	
	
	MatchHandler matchHandler;
		
	List<CharsActiveTypeItem> charsItemMatches;
	List<AText> textMatches;
	List<AData> dataMatches;
	List<AValue> valueMatches;
	List<AListPattern> listMatches;
	
	int totalCount;
	
	ValidationContext validationContext;
	
	ErrorCatcher errorCatcher;
	
	MessageWriter debugWriter;
	
	AbstractCVH(MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
	}
	
	void init(ValidatorEventHandlerPool pool, ValidationItemLocator validationItemLocator, MatchHandler matchHandler){		
		this.validationItemLocator = validationItemLocator;		
		this.pool = pool;
		this.matchHandler = matchHandler;
	}	
	
	public MarkupEventHandler getParentHandler(){
		return null;
	}
	
	public void handleChars(char[] chars, DataActiveType type){
		charsItemMatches.clear();		
		textMatches.clear();
		dataMatches.clear();
		valueMatches.clear();
		listMatches.clear();
		if(type.allowsDataContent()){
			dataMatches.addAll(matchHandler.getDataMatches(type));
		}
		if(type.allowsValueContent()){
			valueMatches.addAll(matchHandler.getValueMatches(type));
		}	
		totalCount = dataMatches.size()+valueMatches.size();
		if(!dataMatches.isEmpty())validateData(chars, type);
		if(!valueMatches.isEmpty())validateValue(chars, type);
		
		handleAddToParent(type);
	}
	
	public void handleChars(char[] chars, StructuredDataActiveType type){
		charsItemMatches.clear();
		textMatches.clear();
		dataMatches.clear();
		valueMatches.clear();
		listMatches.clear();		
		if(type.allowsDataContent()){
			dataMatches.addAll(matchHandler.getDataMatches(type));
		}
		if(type.allowsValueContent()){
			valueMatches.addAll(matchHandler.getValueMatches(type));
		}
		if(type.allowsListPatternContent()){
			listMatches.addAll(matchHandler.getListPatternMatches(type));
		}
		totalCount = dataMatches.size()+valueMatches.size()+listMatches.size();
		if(!dataMatches.isEmpty())validateData(chars, type);
		if(!valueMatches.isEmpty())validateValue(chars, type);
		if(!listMatches.isEmpty())validateListPattern(chars, type);
		
		handleAddToParent(type);		
	}
	public void handleChars(char[] chars, CharsActiveType type){		
		charsItemMatches.clear();
		textMatches.clear();
		dataMatches.clear();
		valueMatches.clear();
		listMatches.clear();		
		if(type.allowsDataContent()){
			dataMatches.addAll(matchHandler.getDataMatches(type));
		}
		if(type.allowsValueContent()){
			valueMatches.addAll(matchHandler.getValueMatches(type));
		}		
		if(type.allowsTextContent()){			
			textMatches.addAll(matchHandler.getTextMatches(type));
		}
		if(type.allowsListPatternContent()){
			listMatches.addAll(matchHandler.getListPatternMatches(type));
		}
		totalCount = dataMatches.size()+valueMatches.size()+listMatches.size()+textMatches.size();		
		if(!dataMatches.isEmpty())validateData(chars, type);
		if(!valueMatches.isEmpty())validateValue(chars, type);
		if(!listMatches.isEmpty())validateListPattern(chars, type);
		charsItemMatches.addAll(textMatches);			
		handleAddToParent(type);
	}
		
	
	public void handleString(String value, DataActiveType type){
		charsItemMatches.clear();
		textMatches.clear();
		dataMatches.clear();
		valueMatches.clear();
		listMatches.clear();		
		if(type.allowsDataContent()){
			dataMatches.addAll(matchHandler.getDataMatches(type));
		}
		if(type.allowsValueContent()){
			valueMatches.addAll(matchHandler.getValueMatches(type));
		}
		totalCount = dataMatches.size()+valueMatches.size();
		if(!dataMatches.isEmpty())validateData(value, type);
		if(!valueMatches.isEmpty())validateValue(value, type);
		
		handleAddToParent(type);
	}
	public void handleString(String value, StructuredDataActiveType type){
		charsItemMatches.clear();
		textMatches.clear();
		dataMatches.clear();
		valueMatches.clear();
		listMatches.clear();		
		if(type.allowsDataContent()){
			dataMatches.addAll(matchHandler.getDataMatches(type));
		}
		if(type.allowsValueContent()){
			valueMatches.addAll(matchHandler.getValueMatches(type));
		}
		if(type.allowsListPatternContent()){
			listMatches.addAll(matchHandler.getListPatternMatches(type));
		}		
		totalCount = dataMatches.size()+valueMatches.size()+listMatches.size();
		if(!dataMatches.isEmpty())validateData(value, type);
		if(!valueMatches.isEmpty())validateValue(value, type);
		if(!listMatches.isEmpty())validateListPattern(value, type);
		
		handleAddToParent(type);
	}
	public void handleString(String value, CharsActiveType type){
		charsItemMatches.clear();
		textMatches.clear();
		dataMatches.clear();
		valueMatches.clear();
		listMatches.clear();		
		if(type.allowsDataContent()){						
			dataMatches.addAll(matchHandler.getDataMatches(type));			
		}
		if(type.allowsValueContent()){
			valueMatches.addAll(matchHandler.getValueMatches(type));
		}
		if(type.allowsTextContent()){			
			textMatches.addAll(matchHandler.getTextMatches(type));
		}
		if(type.allowsListPatternContent()){
			listMatches.addAll(matchHandler.getListPatternMatches(type));
		}
		totalCount = dataMatches.size()+valueMatches.size()+listMatches.size()+textMatches.size();
		if(!dataMatches.isEmpty())validateData(value, type);
		if(!valueMatches.isEmpty())validateValue(value, type);
		if(!listMatches.isEmpty())validateListPattern(value, type);
		charsItemMatches.addAll(textMatches);
		
		handleAddToParent(type);
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
					ExceptPatternTester ept = pool.getExceptPatternTester(data, charsItemMatches, totalCount, errorCatcher);
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
					ExceptPatternTester ept = pool.getExceptPatternTester(data, charsItemMatches, totalCount, errorCatcher);
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
	
	void validateValue(char[] chars, DataActiveType type){
		for(int i = 0; i < valueMatches.size(); i++){
			AValue valuePattern = valueMatches.get(i);
			try{
				valuePattern.datatypeMatches(chars, validationContext);
				if(valuePattern.valueMatches(chars, validationContext)){									
					charsItemMatches.add(valuePattern);
				}else{					
					if(totalCount == 1){
						reportValueError(valuePattern);
						charsItemMatches.add(valuePattern);
					}					
					//System.out.println(type+" "+new String(chars)+" value // ERROR "+valuePattern);
				}
			}catch(DatatypeException de){
				//System.out.println(type+" datatype 3 ERROR "+valuePattern);
				//System.out.println(de.getMessage());
				if(totalCount == 1){
					reportDatatypeError(valuePattern, de.getMessage());
					charsItemMatches.add(valuePattern);
				}
			}
		}
	}
	
	void validateValue(String value, DataActiveType type){		
		for(int i = 0; i < valueMatches.size(); i++){
			AValue valuePattern = valueMatches.get(i);
			try{				
				valuePattern.datatypeMatches(value, validationContext);
				if(valuePattern.valueMatches(value, validationContext)){					
					charsItemMatches.add(valuePattern);
				}else{
					if(totalCount == 1){
						reportValueError(valuePattern);
						charsItemMatches.add(valuePattern);
					}
					//System.out.println(type+" value ERROR "+valuePattern);
				}
			}catch(DatatypeException de){								
				//System.out.println(type+" datatype 4 ERROR "+valuePattern);
				//System.out.println(de.getMessage());
				if(totalCount == 1){
					reportDatatypeError(valuePattern, de.getMessage());
					charsItemMatches.add(valuePattern);
				}
			}
		}
	}
	
	void validateListPattern(char[] chars, StructuredDataActiveType type){		
		ListPatternTester ept = pool.getListPatternTester(charsItemMatches, totalCount, errorCatcher);  
		for(int i = 0; i < listMatches.size(); i++){
			AListPattern list = listMatches.get(i);
			ept.handleChars(chars, list);
		}
		ept.recycle();
	}
	
	void validateListPattern(String value, StructuredDataActiveType type){		
		ListPatternTester ept = pool.getListPatternTester(charsItemMatches, totalCount, errorCatcher);  
		for(int i = 0; i < listMatches.size(); i++){
			AListPattern list = listMatches.get(i);
			ept.handleString(value, list);
		}
		ept.recycle(); 		
	}
	
	abstract void handleAddToParent(DataActiveType type);
	abstract void handleAddToParent(StructuredDataActiveType type);
	abstract void handleAddToParent(CharsActiveType type);
	
	abstract void reportDatatypeError(DatatypedActiveTypeItem item, String message);	
	abstract void reportValueError(AValue value);
}
