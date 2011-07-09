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

package serene.validation.handlers.error;

import java.util.List;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AListPattern;

import serene.validation.schema.simplified.SimplifiedComponent;

public interface ErrorCatcher{	
	
	void unknownElement(String qName, String systemId, int lineNumber, int columnNumber);
	void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber);
	void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] possibleDefinition, String systemId, int lineNumber, int columnNumber);
		
	void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber);
	void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber);
	void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] possibleDefinition, String systemId, int lineNumber, int columnNumber);
		
	void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper);	
	void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper);
		
	
	void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber);
	void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber);

	void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber);	
	void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber);
	
	void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions);
	void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions);
	void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
	
	void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions);
	void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions);
	void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
	
	void undeterminedByContent(String qName, String candidateMessages);	
	
	void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);    
	void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);
    	
	void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition);
	void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition);
	
	void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition);
	void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition);
	
	void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition);
	void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition);
	
	void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
	void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
	
	void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);
	void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition);
	void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition);
	void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
	
    void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
	void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
    
	void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found);
} 