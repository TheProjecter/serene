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
import java.util.ArrayList;

import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
	
import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.match.MatchHandler;

import sereneWrite.MessageWriter;

class ReportingExceptPatternTester extends ExceptPatternTesterState implements ErrorCatcher{
	
	ReportingExceptPatternTester(MessageWriter debugWriter){
		super(debugWriter);
		charsItemMatches = new ArrayList<CharsActiveTypeItem>();
		dataMatches = new ArrayList<AData>();
		valueMatches = new ArrayList<AValue>();
		listMatches = new ArrayList<AListPattern>();		
	}	
		
	public void recycle(){
		throw new IllegalStateException();
	}

	
	public void handleChars(char[] chars, StructuredDataActiveType type){
		totalCharsItemMatches.add(data);//add anyway
		charsItemMatches.clear();
		dataMatches.clear();
		valueMatches.clear();
		listMatches.clear();
		hasError = false;
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
				
		if(charsItemMatches.isEmpty()){ //no in the context of matches the except
			return;
		}
        
		stackHandler =  type.getStackHandler(this);
		int matchesCount = charsItemMatches.size();
		if(totalCount == 0){
			throw new IllegalStateException();
		}else if(totalCount == 1 && matchesCount == 0){
			throw new IllegalStateException();
		}else if(totalCount == 1 && matchesCount == 1){
			stackHandler.shift(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount == 0){
			if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
			if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
			if(!listMatches.isEmpty())charsItemMatches.addAll(listMatches);
			errorCatcher.ambiguousCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
			if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);//use totalCount since everything is shifted
			stackHandler.shiftAllCharsDefinitions(charsItemMatches);
		}else if(totalCount > 1 && matchesCount == 1){
			stackHandler.shift(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount > 1){	
			if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);
			stackHandler.shiftAllCharsDefinitions(charsItemMatches);
		}
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
		
		if(hasError)return;		
		if(validationItemLocator.isElementContext()){			
			errorCatcher.characterContentExceptedError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), (AData)type.getParent()); 
		}else if(validationItemLocator.isAttributeContext()){
			errorCatcher.attributeValueExceptedError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), (AData)type.getParent());
		}else{
			throw new IllegalStateException();
		}
	}
		
		
	public void handleString(String value, StructuredDataActiveType type){
		totalCharsItemMatches.add(data);//add anyway
		charsItemMatches.clear();
		dataMatches.clear();
		valueMatches.clear();
		listMatches.clear();
		hasError = false;		
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
		
		if(charsItemMatches.isEmpty()){ //no in the context of matches the except
			return;
		}
        
		stackHandler =  type.getStackHandler(this);
		int matchesCount = charsItemMatches.size();
		if(totalCount == 0){
			throw new IllegalStateException();
		}else if(totalCount == 1 && matchesCount == 0){
			throw new IllegalStateException();
		}else if(totalCount == 1 && matchesCount == 1){
			stackHandler.shift(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount == 0){
			if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
			if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
			if(!listMatches.isEmpty())charsItemMatches.addAll(listMatches);
			errorCatcher.ambiguousCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
			if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);//use totalCount since everything is shifted
			stackHandler.shiftAllCharsDefinitions(charsItemMatches);
		}else if(totalCount > 1 && matchesCount == 1){
			stackHandler.shift(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount > 1){	
			if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);
			stackHandler.shiftAllCharsDefinitions(charsItemMatches);
		}
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
		
		if(hasError)return;		
		if(validationItemLocator.isAttributeContext()){
			errorCatcher.attributeValueExceptedError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), (AData)type.getParent()); 
		}else if(validationItemLocator.isElementContext()){
			errorCatcher.characterContentExceptedError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), (AData)type.getParent());
		}else{
			throw new IllegalStateException();
		}
	}
	
	
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
	
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		hasError = true;
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		hasError = true;
	}
		
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		hasError = true;
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
	}
	
	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		hasError = true;	
	}
	
	
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		hasError = true;
	}
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		hasError = true;
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		hasError = true;
	}
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
	}
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		hasError = true;
	}
}