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
	ReportingListPatternTester(MessageWriter debugWriter){
		super(debugWriter);
		charsItemMatches = new ArrayList<CharsActiveTypeItem>();
		dataMatches = new ArrayList<AData>();
		valueMatches = new ArrayList<AValue>();
	}
	
	public void handleChars(char[] chars, DataActiveType type){	
		char[][] tokens = spaceHandler.removeSpace(chars);
        if(tokens.length == 0) return;
        stackHandler =  type.getStackHandler(this);
		hasError = false;
		for(int i = 0; i < tokens.length; i++){
			token = tokens[i];
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
			
			int matchesCount = charsItemMatches.size();
			if(totalCount == 0){
				throw new IllegalStateException("This is a weird schema, no data in list.");
			}else if(totalCount == 1 && matchesCount == 0){
				throw new IllegalStateException();
			}else if(totalCount == 1 && matchesCount == 1){
				// if errors: already reported, that's why error before
				// just shift
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount == 0){
				// ambiguity error
				// shift all for in context validation
				if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
				if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
				ambiguousListToken(new String(tokens[i]), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
				if(!stackHandler.handlesConflict())  stackHandler = type.getStackHandler(stackHandler, this);//use totalCount since everything is shifted
				stackHandler.shiftAllTokenDefinitions(charsItemMatches, token);
			}else if(totalCount > 1 && matchesCount == 1){
				//just shift
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount > 1){
				// ambiguity warning, later maybe
				// shift all for in context validation
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);
				stackHandler.shiftAllTokenDefinitions(charsItemMatches, token);
			}	
		}
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;		
        totalCharsItemMatches.add((CharsActiveTypeItem)type);
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
        
		stackHandler =  type.getStackHandler(this);
        
		hasError = false;
		for(int i = 0; i < tokens.length; i++){
			token = tokens[i];
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
						
			int matchesCount = charsItemMatches.size();
			if(totalCount == 0){
				throw new IllegalStateException("This is a weird schema, no data in list.");
			}else if(totalCount == 1 && matchesCount == 0){
				throw new IllegalStateException();
			}else if(totalCount == 1 && matchesCount == 1){
				// if errors: already reported, that's why error before
				// just shift
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount == 0){
				// ambiguity error
				// shift all for in context validation
				if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
				if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
				ambiguousListToken(new String(tokens[i]), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);//use totalCount since everything is shifted
				stackHandler.shiftAllTokenDefinitions(charsItemMatches, token);
			}else if(totalCount > 1 && matchesCount == 1){
				//just shift
				stackHandler.shift(charsItemMatches.get(0));
			}else if(totalCount > 1 && matchesCount > 1){
				// ambiguity warning, later maybe
				// shift all for in context validation
				if(!stackHandler.handlesConflict()) stackHandler = type.getStackHandler(stackHandler, this);
				stackHandler.shiftAllTokenDefinitions(charsItemMatches, token);
			}
		}		
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
        
        totalCharsItemMatches.add((CharsActiveTypeItem)type);		
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
		errorCatcher.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		hasError = true;
		errorCatcher.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
	}
	
		
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
		errorCatcher.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		hasError = true;
		errorCatcher.excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		hasError = true;
		errorCatcher.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
	}
	
	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		hasError = true;
		errorCatcher.illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
	}
		
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		errorCatcher.ambiguousCharsContentError(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		errorCatcher.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		hasError = true;
		errorCatcher.undeterminedByContent(qName, candidateMessages);
	}
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
		errorCatcher.listTokenDatatypeError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		hasError = true;
		errorCatcher.listTokenDatatypeError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;		
		errorCatcher.listTokenValueError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		hasError = true;
		errorCatcher.listTokenValueError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
		errorCatcher.listTokenExceptedError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		hasError = true;
		errorCatcher.listTokenExceptedError(new String(token), charsSystemId, charsLineNumber, columnNumber, charsDefinition);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		errorCatcher.ambiguousListToken(new String(token), systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
		errorCatcher.ambiguousListToken(new String(token), systemId, lineNumber, columnNumber, possibleDefinitions);
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
		errorCatcher.ambiguousListToken(token, systemId, lineNumber, columnNumber, possibleDefinitions);
	}
	public void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		hasError = true;
        errorCatcher.ambiguousListTokenInContextError(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		errorCatcher.ambiguousListTokenInContextWarning(token, systemId, lineNumber, columnNumber, possibleDefinitions);
    }
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		hasError = true;
		errorCatcher.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
	}
}
	
