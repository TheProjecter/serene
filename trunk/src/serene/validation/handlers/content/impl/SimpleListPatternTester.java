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
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.match.MatchHandler;

import serene.util.SpaceCharsHandler;

import sereneWrite.MessageWriter;

	
class SimpleListPatternTester extends ListPatternTesterState{
		
	SimpleListPatternTester(MessageWriter debugWriter){
		super(debugWriter);
		charsItemMatches = new ArrayList<CharsActiveTypeItem>();
		dataMatches = new ArrayList<AData>();
		valueMatches = new ArrayList<AValue>();
	}
	
	public void handleChars(char[] chars, DataActiveType type){
		stackHandler =  type.getStackHandler(this);
		char[][] tokens = spaceHandler.removeSpace(chars);
		
		hasError = false;
		for(int i = 0; i < tokens.length; i++){
			charsItemMatches.clear();
			dataMatches.clear();
			valueMatches.clear();
			tokenValid = true; 
			if(type.allowsDataContent()){
				dataMatches.addAll(matchHandler.getDataMatches(type));
			}
			if(type.allowsValueContent()){
				valueMatches.addAll(matchHandler.getValueMatches(type));
			}
			totalCount = dataMatches.size()+valueMatches.size();
			if(!dataMatches.isEmpty())validateData(tokens[i], type);
			if(!valueMatches.isEmpty())validateValue(tokens[i], type);			
			
			int matchesCount = charsItemMatches.size();
			if(totalCount == 0){
				throw new IllegalStateException("This is a weird schema, no data in list.");
			}else if(totalCount == 1 && matchesCount == 0){
				throw new IllegalStateException();
			}else if(totalCount == 1 && matchesCount == 1){
				if(tokenValid) tokenMatch = true;
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount == 0){
				if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
				if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
				ambiguousListToken(new String(tokens[i]), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);//use totalCount since everything is shifted
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}else if(totalCount > 1 && matchesCount == 1){
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount > 1){	
				if(!stackHandler.handlesConflict())  stackHandler = type.getStackHandler(stackHandler, this);
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}
						
			if(hasError)return;
		}
		if(tokenMatch == false){// no matches were found for any token
			return;
		}		
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
		if(hasError == true){// a token resulted in error or the pattern was not satisfied
			return;
		}
		totalCharsItemMatches.add((CharsActiveTypeItem)type);
	}
	public void handleChars(char[] chars, StructuredDataActiveType type){
		throw new IllegalStateException();
	}
	public void handleChars(char[] chars, CharsActiveType type){
		throw new IllegalStateException();
	}
		
	
	public void handleString(String value, DataActiveType type){
		stackHandler =  type.getStackHandler(this);
		char[][] tokens = spaceHandler.removeSpace(value.toCharArray());
		
		hasError = false;
		for(int i = 0; i < tokens.length; i++){
			charsItemMatches.clear();
			dataMatches.clear();
			valueMatches.clear();	
			if(type.allowsDataContent()){
				dataMatches.addAll(matchHandler.getDataMatches(type));
			}
			if(type.allowsValueContent()){
				valueMatches.addAll(matchHandler.getValueMatches(type));
			}
			totalCount = dataMatches.size()+valueMatches.size();
			if(!dataMatches.isEmpty())validateData(tokens[i], type);
			if(!valueMatches.isEmpty())validateValue(tokens[i], type);
			
			
			int matchesCount = charsItemMatches.size();
			if(totalCount == 0){
				throw new IllegalStateException("This is a weird schema, no data in list.");
			}else if(totalCount == 1 && matchesCount == 0){
				throw new IllegalStateException();
			}else if(totalCount == 1 && matchesCount == 1){
				if(tokenValid) tokenMatch = true;
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount == 0){
				if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
				if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
				ambiguousListToken(new String(tokens[i]), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);//use totalCount since everything is shifted
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}else if(totalCount > 1 && matchesCount == 1){
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount > 1){	
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}
			
			if(hasError)return;
		}
		if(tokenMatch == false){// no matches were found for any token
			return;
		}		
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
		if(hasError == true){// a token resulted in error or the pattern was not satisfied
			return;
		}
		totalCharsItemMatches.add((CharsActiveTypeItem)type);
	}
	public void handleString(String value, StructuredDataActiveType type){
		throw new IllegalStateException();
	}
	public void handleString(String value, CharsActiveType type){
		throw new IllegalStateException();
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
		tokenValid = false;
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		hasError = true;
		tokenValid = false;
	}
	
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
		tokenValid = false;
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		hasError = true;
		tokenValid = false;
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
		tokenValid = false;
	}
	
	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		hasError = true;
		tokenValid = false;
	}
		
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		tokenValid = false;
	}
	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		hasError = true;
		tokenValid = false;
	}
	
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
		tokenValid = false;
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
		tokenValid = false;
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
		tokenValid = false;
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
		tokenValid = false;
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
		tokenValid = false;
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
		tokenValid = false;
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		hasError = true;
		tokenValid = false;
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		hasError = true;
		tokenValid = false;
	}
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		tokenValid = false;
	}
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		tokenValid = false;
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
		tokenValid = false;
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
		tokenValid = false;
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
		tokenValid = false;
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		tokenValid = false;
	}
	
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		hasError = true;
		tokenValid = false;
	}
}
	
