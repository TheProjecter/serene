/*
Copyright 2011 Radu Cernuta 

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

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.BitSet;

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
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.conflict.ExternalConflictHandler;

interface ConflictErrorCatcher extends ErrorType{	
    void unknownElement(int functionalEquivalenceCode, int inputRecordIndex);	
	void unexpectedElement(int functionalEquivalenceCode, SimplifiedComponent definition, int inputRecordIndex);    
	void unexpectedAmbiguousElement(int functionalEquivalenceCode, SimplifiedComponent[] possibleDefinitions, int inputRecordIndex);
	
	
	void unknownAttribute(int functionalEquivalenceCode, int inputRecordIndex);	
	void unexpectedAttribute(int functionalEquivalenceCode, SimplifiedComponent definition, int inputRecordIndex);	
	void unexpectedAmbiguousAttribute(int functionalEquivalenceCode, SimplifiedComponent[] possibleDefinitions, int inputRecordIndex);
	
    
	void misplacedContent(int functionalEquivalenceCode, 
                                            SPattern contextDefinition, 
											int startInputRecordIndex,
											SPattern definition,
											int inputRecordIndex,
											SPattern sourceDefinition, 
											SPattern reper);
    void misplacedContent(int functionalEquivalenceCode, 
                                            SPattern contextDefinition,
											int startInputRecordIndex,
											SPattern definition,
											int[] inputRecordIndex,
											SPattern[] sourceDefinition, 
											SPattern reper);
			
	
	void excessiveContent(int functionalEquivalenceCode, 
                                    SRule context,
									int startInputRecordIndex,
									SPattern definition, 
									int[] inputRecordIndex);
    
	void excessiveContent(int functionalEquivalenceCode, 
                                SRule context, 
								SPattern definition, 
								int inputRecordIndex);    
    
	void unresolvedAmbiguousElementContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions);
	
	void unresolvedUnresolvedElementContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions);
    
	void unresolvedAttributeContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SAttribute[] possibleDefinitions);
	


    void ambiguousUnresolvedElementContentWarning(int functionalEquivalenceCode, int inputRecordIndex, SElement[] possibleDefinitions);
    void ambiguousAmbiguousElementContentWarning(int functionalEquivalenceCode, int inputRecordIndex, SElement[] possibleDefinitions);
	void ambiguousAttributeContentWarning(int functionalEquivalenceCode, int inputRecordIndex, SAttribute[] possibleDefinitions);
	void ambiguousCharacterContentWarning(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
	void ambiguousAttributeValueWarning(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);

	
	void missingContent(int functionalEquivalenceCode, 
                                SRule context, 
								int startInputRecordIndex,						 
								SPattern definition, 
								int expected, 
								int found,
								int[] inputRecordIndex);
    
	void illegalContent(int functionalEquivalenceCode, 
                            SRule context, 
                            int startInputRecordIndex);
        
	void characterContentDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);
        
	void attributeValueDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);
        
        
	void characterContentValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition);
    
	void attributeValueValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition);
        
	void characterContentExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition);
    
	void attributeValueExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition);
    
	void unexpectedCharacterContent(int functionalEquivalenceCode, int inputRecordIndex, SElement elementDefinition);
    
	void unexpectedAttributeValue(int functionalEquivalenceCode, int inputRecordIndex, AAttribute attributeDefinition);
    
	void unresolvedCharacterContent(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
        
	void unresolvedAttributeValue(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
        
	void listTokenDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);
        
	void listTokenValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition);
    
	void listTokenExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition);
        
    void unresolvedListTokenInContextError(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
    void ambiguousListTokenInContextWarning(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);    
    
	void missingCompositorContent(int functionalEquivalenceCode, 
                                SRule context, 
								int startInputRecordIndex,								 
								SPattern definition, 
								int expected, 
								int found);
    
    void conflict(int functionalEquivalenceCode, int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages);
}
