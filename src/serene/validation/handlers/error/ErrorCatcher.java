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

import org.xml.sax.SAXException;

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

public interface ErrorCatcher extends ErrorType{	
	
	void unknownElement(int inputRecordIndex);
	void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex);
	void unexpectedAmbiguousElement(SimplifiedComponent[] possibleDefinition, int inputRecordIndex);
	
	void unknownAttribute(int inputRecordIndex);
	void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex);
	void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex);
		
	void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int inputRecordIndex, APattern sourceDefinition, APattern reper);		
	void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper);
		
		
	void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex);
	void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex);

	void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex);	
	void illegalContent(Rule context, int startInputRecordIndex);
	
	void unresolvedAmbiguousElementContentError(int inputRecordIndex, AElement[] possibleDefinitions);
	void unresolvedUnresolvedElementContentError(int inputRecordIndex, AElement[] possibleDefinitions);
	void unresolvedAttributeContentError(int inputRecordIndex, AAttribute[] possibleDefinitions);
	
	void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions);
	void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions);
	void ambiguousAttributeContentWarning(int inputRecordIndex, AAttribute[] possibleDefinitions);
	void ambiguousCharacterContentWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions);
	void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions);
		
    void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);    
	void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);
    	
	void characterContentValueError(int inputRecordIndex, AValue charsDefinition);
	void attributeValueValueError(int inputRecordIndex, AValue charsDefinition);
	
	void characterContentExceptedError(int inputRecordIndex, AData charsDefinition);
	void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition);
	
	void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition);
	void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition);
	
	void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions);
	void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions);
	
	void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);
	void listTokenValueError(int inputRecordIndex, AValue charsDefinition);
	void listTokenExceptedError(int inputRecordIndex, AData charsDefinition);

    void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions);
	void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions);
    
	void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found);
	
	void internalConflict(ConflictMessageReporter conflictMessageReporter) throws SAXException;
} 