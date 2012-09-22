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

import serene.util.IntList;

interface CandidatesConflictErrorCatcher extends ErrorType{	
    void unknownElement(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex);	
	void unexpectedElement(int candidateIndex, int functionalEquivalenceCode, SimplifiedComponent definition, int inputRecordIndex);
	void unexpectedAmbiguousElement(int candidateIndex, int functionalEquivalenceCode, SimplifiedComponent[] possibleDefinitions, int inputRecordIndex);
	
	
	void unknownAttribute(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex);
	void unexpectedAttribute(int candidateIndex, int functionalEquivalenceCode, SimplifiedComponent definition, int inputRecordIndex);
	void unexpectedAmbiguousAttribute(int candidateIndex, int functionalEquivalenceCode, SimplifiedComponent[] possibleDefinitions, int inputRecordIndex);
    
	void misplacedContent(int candidateIndex, int functionalEquivalenceCode, 
                                            SPattern contextDefinition, 
											int startInputRecordIndex, 
											SPattern definition, 
											int inputRecordIndex,
											SPattern sourceDefinition, 
											SPattern reper);
    void misplacedContent(int candidateIndex, int functionalEquivalenceCode, 
                                            SPattern contextDefinition,
											int startInputRecordIndex,
											SPattern definition,
											int[] inputRecordIndex,
											SPattern[] sourceDefinition, 
											SPattern reper);
			
	
	void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                    SRule context,
									int startInputRecordIndex,
									SPattern definition, 
									int[] inputRecordIndex);
    
	void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                SRule context, 
								SPattern definition,
								int inputRecordIndex);    
    
	void unresolvedAmbiguousElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions);
	
	void unresolvedUnresolvedElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions);
    
	void unresolvedAttributeContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SAttribute[] possibleDefinitions);
		

    void ambiguousUnresolvedElementContentWarning(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SElement[] possibleDefinitions);
    void ambiguousAmbiguousElementContentWarning(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SElement[] possibleDefinitions);
	void ambiguousAttributeContentWarning(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SAttribute[] possibleDefinitions);
	void ambiguousCharacterContentWarning(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
	void ambiguousAttributeValueWarning(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);	
	
	void missingContent(int candidateIndex, int functionalEquivalenceCode,
                                SRule context, 
								int startInputRecordIndex,						 
								SPattern definition, 
								int expected, 
								int found,
								int[] inputRecordIndex);
    
	void illegalContent(int candidateIndex, int functionalEquivalenceCode, 
                            SRule context, 
                            int startInputRecordIndex);
        
		
	void characterContentDatatypeError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);
        
	void attributeValueDatatypeError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);
        
        
	void characterContentValueError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition);
    
	void attributeValueValueError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition);
        
	void characterContentExceptedError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition);
    
	void attributeValueExceptedError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition);
    
	void unexpectedCharacterContent(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SElement elementDefinition);
    
	void unexpectedAttributeValue(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, AAttribute attributeDefinition);
    
	void unresolvedCharacterContent(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
        
	void unresolvedAttributeValue(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
        
	void listTokenDatatypeError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage);
        
	void listTokenValueError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition);
    
	void listTokenExceptedError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition);
            
    void unresolvedListTokenInContextError(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
    void ambiguousListTokenInContextWarning(int candidateIndex, int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions);
    
	void missingCompositorContent(int candidateIndex, int functionalEquivalenceCode, 
                                SRule context, 
								int startInputRecordIndex,								 
								SPattern definition, 
								int expected, 
								int found);
}
