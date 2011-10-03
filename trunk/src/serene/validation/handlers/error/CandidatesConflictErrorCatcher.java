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

import serene.validation.handlers.conflict.ExternalConflictHandler;

interface CandidatesConflictErrorCatcher extends ErrorType{	
    void unknownElement(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber);	
	void unexpectedElement(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber);
    
    
	void unexpectedAmbiguousElement(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber);
	
	void unknownAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber);
	
	void unexpectedAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber);
	
	
	void unexpectedAmbiguousAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber);
    
	void misplacedElement(int candidateIndex, int functionalEquivalenceCode, 
                                            APattern contextDefinition, 
											String startSystemId, 
											int startLineNumber, 
											int startColumnNumber, 
											APattern definition, 
											String qName, 
											String systemId, 
											int lineNumber, 
											int columnNumber,
											APattern sourceDefinition, 
											APattern reper);
    void misplacedElement(int candidateIndex, int functionalEquivalenceCode, 
                                            APattern contextDefinition, 
											String startSystemId, 
											int startLineNumber, 
											int startColumnNumber, 
											APattern definition, 
											String[] qName, 
											String[] systemId, 
											int[] lineNumber, 
											int[] columnNumber,
											APattern[] sourceDefinition, 
											APattern reper);
			
	
	void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                    Rule context,
									String startSystemId,
									int startLineNumber,
									int startColumnNumber,
									APattern definition, 
									String[] qName, 
									String[] systemId, 
									int[] lineNumber, 
									int[] columnNumber);
    
	void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                Rule context, 
								APattern definition, 
								String qName, 
								String systemId, 
								int lineNumber,		
								int columnNumber);    
    
	void unresolvedAmbiguousElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions);
	
	void unresolvedUnresolvedElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions);
    
	void ambiguousAttributeContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions);
	
	void ambiguousCharsContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String systemId, 
									int lineNumber, 
									int columnNumber, 
									CharsActiveTypeItem[] possibleDefinitions);

    void ambiguousUnresolvedElementContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions);
    void ambiguousAmbiguousElementContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions);
	void ambiguousAttributeContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions);
	void ambiguousCharsContentWarning(int candidateIndex, int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);	
	
	void missingContent(int candidateIndex, int functionalEquivalenceCode, 
                                Rule context, 
								String startSystemId, 
								int startLineNumber, 
								int startColumnNumber,								 
								APattern definition, 
								int expected, 
								int found,
								String[] qName, 
								String[] systemId, 
								int[] lineNumber, 
								int[] columnNumber);
    
	void illegalContent(int candidateIndex, int functionalEquivalenceCode, 
                            Rule context, 
							String startQName, 
							String startSystemId, 
							int startLineNumber, 
							int startColumnNumber);
        
	void undeterminedByContent(int candidateIndex, int functionalEquivalenceCode, String qName, String candidateMessages);
	
	void characterContentDatatypeError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);
        
	void attributeValueDatatypeError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);
        
        
	void characterContentValueError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition);
    
	void attributeValueValueError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition);
        
	void characterContentExceptedError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition);
    
	void attributeValueExceptedError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition);
    
	void unexpectedCharacterContent(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition);
    
	void unexpectedAttributeValue(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition);
    
	void unresolvedCharacterContent(int candidateIndex, int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
        
	void unresolvedAttributeValue(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
        
	void listTokenDatatypeError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage);
        
	void listTokenValueError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition);
    
	void listTokenExceptedError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition);
        
	void ambiguousListToken(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
        
    void ambiguousListTokenInContextError(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
    void ambiguousListTokenInContextWarning(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions);
    
	void missingCompositorContent(int candidateIndex, int functionalEquivalenceCode, 
                                Rule context, 
								String startSystemId, 
								int startLineNumber, 
								int startColumnNumber,								 
								APattern definition, 
								int expected, 
								int found);
}
