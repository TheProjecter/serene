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

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

public interface ErrorCatcher extends ErrorType{	
	
	void unknownElement(int inputRecordIndex);
	void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex);
	void unexpectedAmbiguousElement(SimplifiedComponent[] possibleDefinition, int inputRecordIndex);
	
	void unknownAttribute(int inputRecordIndex);
	void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex);
	void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex);
		
	void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper);		
	void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper);
		
		
	void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex);
	void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex);

	void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex);	
	void illegalContent(SRule context, int startInputRecordIndex);
	
	void unresolvedAmbiguousElementContentError(int inputRecordIndex, SElement[] possibleDefinitions);
	void unresolvedUnresolvedElementContentError(int inputRecordIndex, SElement[] possibleDefinitions);
	void unresolvedAttributeContentError(int inputRecordIndex, SAttribute[] possibleDefinitions);
	
	void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions);
	void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions);
	void ambiguousAttributeContentWarning(int inputRecordIndex, SAttribute[] possibleDefinitions);
	void ambiguousCharacterContentWarning(int inputRecordIndex, SPattern[] possibleDefinitions);
	void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions);
		
    void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);    
	void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);
    	
	void characterContentValueError(int inputRecordIndex, SValue charsDefinition);
	void attributeValueValueError(int inputRecordIndex, SValue charsDefinition);
	
	void characterContentExceptedError(int inputRecordIndex, SData charsDefinition);
	void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition);
	
	void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition);
	void unexpectedAttributeValue(int inputRecordIndex, SAttribute attributeDefinition);
	
	void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions);
	void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions);
	
	void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);
	void listTokenValueError(int inputRecordIndex, SValue charsDefinition);
	void listTokenExceptedError(int inputRecordIndex, SData charsDefinition);

    void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions);
	void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions);
    
	void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found);
	
	void internalConflict(ConflictMessageReporter conflictMessageReporter) throws SAXException;
} 