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


// Errors are reported only if some token match has been found. This means that
// if the first token(s) result in errors it is impossible to know if these errors 
// need to be reported or not.
// Solutions:
// 1. Store the error messages and refire them later if necessary
// 2. When a match is found set the ListPatternTester in reporting mode, if error
// have happened already, that is the match didn't occur at the first tested token,
// redo the chars matching(keep the Tester in report mode)
// Go for 2.
class ReportingListPatternTester extends ListPatternTesterState{
	
	/**
	* In the begining is always false, but it is set to true when a first match 
	* was encountered, the validation is redone starting from the first token. 
	* When true, errors are reported. In order to report errors it is also 
	* necessary that at least one token was matched and shifted correctly, 
	* otherwise they are not, and the CharactersValidationHandler will report 
	* unexpected character content(no matches).
	*/
	boolean reportError;
	
	char[] token;
	
	ReportingListPatternTester(MessageWriter debugWriter){
		super(debugWriter);
		charsItemMatches = new ArrayList<CharsActiveTypeItem>();
		dataMatches = new ArrayList<AData>();
		valueMatches = new ArrayList<AValue>();
	}
	
	public void handleChars(char[] chars, DataActiveType type){	
		char[][] tokens = spaceHandler.removeSpace(chars);
        if(tokens.length == 0) return;
        
        totalCharsItemMatches.add((CharsActiveTypeItem)type);//add anyway
        stackHandler =  type.getStackHandler(this);
		reportError = false;
		tokenMatch = false;
		hasError = false;
		for(int i = 0; i < tokens.length; i++){
			token = tokens[i];
			tokenValid = true; 
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
			if(!dataMatches.isEmpty())validateData(token, type);
			if(!valueMatches.isEmpty())validateValue(token, type);
			
			/*
			old way
			int matchCount = charsItemMatches.size();
			if(matchCount == 0){
				if(reportError){
					unexpectedTokenInList((AListPattern)type, new String(chars));
				}				
			}else if(matchCount == 1){
				tokenMatch = true;
				stackHandler.shift(charsItemMatches.get(0));
			}else{
				tokenMatch = true;
				stackHandler = type.getStackHandler(stackHandler, matchCount, this);
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}
			if(tokenMatch && tokenValid && !reportError){
				if(i == 0)reportError = true;
				else{
					i = -1;
					reportError = true;
				}
			}*/
			
			int matchesCount = charsItemMatches.size();
			if(totalCount == 0){
				throw new IllegalStateException("This is a weird schema, no data in list.");
			}else if(totalCount == 1 && matchesCount == 0){
				throw new IllegalStateException();
			}else if(totalCount == 1 && matchesCount == 1){
				// if errors: already reported, that's why error before
				//just shift
				// TODO 
				// how do you know if tokenMatch???
				if(tokenValid) tokenMatch = true;
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount == 0){
				// ambiguity error
				// shift all for in context validation
				if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
				if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
				ambiguousListToken(new String(tokens[i]), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
				if(!stackHandler.handlesConflict())  stackHandler = type.getStackHandler(stackHandler, this);//use totalCount since everything is shifted
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}else if(totalCount > 1 && matchesCount == 1){
				//just shift
				if(tokenValid) tokenMatch = true;
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount > 1){
				// ambiguity warning, later maybe
				// shift all for in context validation	
				if(tokenValid) tokenMatch = true;
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}	
			
			if(tokenValid && ! reportError){
				reportError = true;
				if(hasError){
					stackHandler.recycle();
					stackHandler =  type.getStackHandler(this);
					i = -1;
				}
			}
		}
		if(tokenMatch == false){// no matches were found for any token
			return;
		}
		
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;		
	}
	
	public void handleChars(char[] chars, StructuredDataActiveType type){
		throw new IllegalStateException(); // TODO see why this throws exception when is called form the ListPatternTester
	}
	public void handleChars(char[] chars, CharsActiveType type, boolean isComplexContent){
		throw new IllegalStateException();
	}
		
	
	public void handleString(String value, DataActiveType type){		
		char[][] tokens = spaceHandler.removeSpace(value.toCharArray());
        if(tokens.length == 0) return;
		
        totalCharsItemMatches.add((CharsActiveTypeItem)type);//add anyway		
		stackHandler =  type.getStackHandler(this);
        
		reportError = false;
		tokenMatch = false;
		hasError = false;
		for(int i = 0; i < tokens.length; i++){
			token = tokens[i];
			tokenValid = true;
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
			if(!dataMatches.isEmpty())validateData(token, type);
			if(!valueMatches.isEmpty())validateValue(token, type);
			
			/*
			old way
			int matchCount = charsItemMatches.size();
			if(matchCount == 0){
				if(reportError){
					unexpectedTokenInList((AListPattern)type, value);
				}				
			}else if(matchCount == 1){
				tokenMatch = true;
				stackHandler.shift(charsItemMatches.get(0));
			}else{
				tokenMatch = true;
				stackHandler = type.getStackHandler(stackHandler, matchCount, this);
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}
			if(tokenMatch && tokenValid && !reportError){
				if(i == 0)reportError = true;
				else{
					i = -1;
					reportError = true;
				}
			}*/
			
			int matchesCount = charsItemMatches.size();
			if(totalCount == 0){
				throw new IllegalStateException("This is a weird schema, no data in list.");
			}else if(totalCount == 1 && matchesCount == 0){
				throw new IllegalStateException();
			}else if(totalCount == 1 && matchesCount == 1){
				// if errors: already reported, that's why error before
				//just shift
				// TODO 
				// how do you know if tokenMatch???
				if(tokenValid) tokenMatch = true;
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount == 0){
				// ambiguity error
				// shift all for in context validation
				if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
				if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
				ambiguousListToken(new String(tokens[i]), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);//use totalCount since everything is shifted
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}else if(totalCount > 1 && matchesCount == 1){
				//just shift
				if(tokenValid) tokenMatch = true;
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount > 1){
				// ambiguity warning, later maybe
				// shift all for in context validation	
				if(tokenValid) tokenMatch = true;
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);
				stackHandler.shiftAllCharsDefinitions(charsItemMatches);
			}
			
			if(tokenValid && ! reportError){
				reportError = true;
				if(hasError){
					stackHandler.recycle();
					stackHandler =  type.getStackHandler(this);
					i = -1;
				}
			}
		}
		if(tokenMatch == false){// no matches were found for any token
			return;
		}
		
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;		
	}
	public void handleString(String value, StructuredDataActiveType type){
		throw new IllegalStateException(); // TODO see why this throws exception when is called form the ListPatternTester
	}
	public void handleString(String value, CharsActiveType type, boolean isComplexContent){
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
		if(reportError)errorCatcher.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
	
		
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
	}
	
	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
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
		if(reportError)errorCatcher.ambiguousCharsContentError(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		if(reportError)errorCatcher.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.undeterminedByContent(qName, candidateMessages);
	}
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.listTokenDatatypeError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.listTokenDatatypeError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
		tokenValid = false;		
		if(reportError)errorCatcher.listTokenValueError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.listTokenValueError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.listTokenExceptedError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.listTokenExceptedError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.ambiguousListToken(new String(token), systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.ambiguousListToken(new String(token), systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.ambiguousListToken(token, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		hasError = true;
		tokenValid = false;
		if(reportError)errorCatcher.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
	}
}
	
