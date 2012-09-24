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
import java.util.BitSet;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;


import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

public class CandidatesConflictMessageHandler  extends ConflictMessageHandler{	
  
	public CandidatesConflictMessageHandler(){
		super();
	}	
	
    public ConflictMessageReporter getConflictMessageReporter(ErrorDispatcher errorDispatcher){
        return new ConflictMessageReporter(parent,                                    
                                    reportingContextType,
                                    reportingContextQName,
                                    reportingContextDefinition,
                                    reportingContextPublicId,
                                    reportingContextSystemId,
                                    reportingContextLineNumber,
                                    reportingContextColumnNumber,
                                    conflictResolutionId,
                                    restrictToFileName,
                                    commonMessages,
                                    candidatesCount,
                                    disqualified,
                                    candidateMessages,
                                    errorDispatcher);
    }
    
    
    
	public void unknownElement(int functionalEquivalenceCode, int inputRecordIndex){
        for(int i = 0; i <= unknownElementIndex; i++){
	        if(unknownElementFEC[i] == functionalEquivalenceCode) return;
	    }
	        
		if(unknownElementInputRecordIndex == null){
		    unknownElementInputRecordIndex = new int[initialSize];
		}else if(++unknownElementIndex == unknownElementInputRecordIndex.length){
		    int[] increasedEIRI = new int[unknownElementInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unknownElementInputRecordIndex, 0, increasedEIRI, 0, unknownElementIndex);
			unknownElementInputRecordIndex = increasedEIRI;		    
		}
		unknownElementInputRecordIndex[unknownElementIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
        unknownElementFEC[unknownElementIndex] = functionalEquivalenceCode;
	}
    
	public void unexpectedElement(int functionalEquivalenceCode, SimplifiedComponent definition, int inputRecordIndex){
	    for(int i = 0; i <= unexpectedElementIndex; i++){
	        if(unexpectedElementFEC[i] == functionalEquivalenceCode) return;
	    }
	        
        messageTotalCount++;
        
        if(unexpectedElementInputRecordIndex == null){
		    unexpectedElementIndex = 0;
		    unexpectedElementInputRecordIndex = new int[initialSize];
		    unexpectedElementDefinition = new SimplifiedComponent[initialSize];
		    unexpectedElementFEC = new int[initialSize];	
		}else if(++unexpectedElementIndex == unexpectedElementInputRecordIndex.length){
		    int[] increasedEIRI = new int[unexpectedElementInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unexpectedElementInputRecordIndex, 0, increasedEIRI, 0, unexpectedElementIndex);
			unexpectedElementInputRecordIndex = increasedEIRI;		
			
			SimplifiedComponent[] increasedDef = new SimplifiedComponent[unexpectedElementDefinition.length+increaseSizeAmount];
			System.arraycopy(unexpectedElementDefinition, 0, increasedDef, 0, unexpectedElementIndex);
			unexpectedElementDefinition = increasedDef;
			
			int[] increasedFEC = new int[unexpectedElementFEC.length+increaseSizeAmount];
			System.arraycopy(unexpectedElementFEC, 0, increasedFEC, 0, unexpectedElementIndex);
			unexpectedElementFEC = increasedFEC;
		}	
		unexpectedElementDefinition[unexpectedElementIndex] = definition;
		unexpectedElementInputRecordIndex[unexpectedElementIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
		unexpectedElementFEC[unexpectedElementIndex] = functionalEquivalenceCode;
	}
		
    
	public void unexpectedAmbiguousElement(int functionalEquivalenceCode, SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
	    for(int i = 0; i <= unexpectedAmbiguousElementIndex; i++){
	        if(unexpectedAmbiguousElementFEC[i] == functionalEquivalenceCode) return;
	    }
	    
        messageTotalCount++;
        
        if(unexpectedAmbiguousElementInputRecordIndex == null){
		    unexpectedAmbiguousElementIndex = 0;
		    unexpectedAmbiguousElementInputRecordIndex = new int[initialSize];
		    unexpectedAmbiguousElementDefinition = new SimplifiedComponent[initialSize][];
		    unexpectedAmbiguousElementFEC = new int[initialSize];	
		}else if(++unexpectedAmbiguousElementIndex == unexpectedAmbiguousElementInputRecordIndex.length){
		    int[] increasedEIRI = new int[unexpectedAmbiguousElementInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unexpectedAmbiguousElementInputRecordIndex, 0, increasedEIRI, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementInputRecordIndex = increasedEIRI;		
			
			SimplifiedComponent[][] increasedDef = new SimplifiedComponent[unexpectedAmbiguousElementDefinition.length+increaseSizeAmount][];
			System.arraycopy(unexpectedAmbiguousElementDefinition, 0, increasedDef, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementDefinition = increasedDef;
			
			int[] increasedFEC = new int[unexpectedAmbiguousElementFEC.length+increaseSizeAmount];
			System.arraycopy(unexpectedAmbiguousElementFEC, 0, increasedFEC, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementFEC = increasedFEC;
		}	
		unexpectedAmbiguousElementDefinition[unexpectedAmbiguousElementIndex] = possibleDefinitions;
		unexpectedAmbiguousElementInputRecordIndex[unexpectedAmbiguousElementIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
		unexpectedAmbiguousElementFEC[unexpectedAmbiguousElementIndex] = functionalEquivalenceCode;
	}
		

    public void unknownAttribute(int functionalEquivalenceCode, int inputRecordIndex){
        for(int i = 0; i <= unknownAttributeIndex; i++){
	        if(unknownAttributeFEC[i] == functionalEquivalenceCode) return;
	    }
	    
		if(unknownAttributeInputRecordIndex == null){
		    unknownAttributeInputRecordIndex = new int[initialSize];
		}else if(++unknownAttributeIndex == unknownAttributeInputRecordIndex.length){
		    int[] increasedEIRI = new int[unknownAttributeInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unknownAttributeInputRecordIndex, 0, increasedEIRI, 0, unknownAttributeIndex);
			unknownAttributeInputRecordIndex = increasedEIRI;		    
		}
		unknownAttributeInputRecordIndex[unknownAttributeIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
        unknownAttributeFEC[unknownAttributeIndex] = functionalEquivalenceCode;
	}
    
	public void unexpectedAttribute(int functionalEquivalenceCode, SimplifiedComponent definition, int inputRecordIndex){
	    for(int i = 0; i <= unexpectedAttributeIndex; i++){
	        if(unexpectedAttributeFEC[i] == functionalEquivalenceCode) return;
	    }
	    
        messageTotalCount++;
        
        if(unexpectedAttributeInputRecordIndex == null){
		    unexpectedAttributeIndex = 0;
		    unexpectedAttributeInputRecordIndex = new int[initialSize];
		    unexpectedAttributeDefinition = new SimplifiedComponent[initialSize];
		    unexpectedAttributeFEC = new int[initialSize];	
		}else if(++unexpectedAttributeIndex == unexpectedAttributeInputRecordIndex.length){
		    int[] increasedEIRI = new int[unexpectedAttributeInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unexpectedAttributeInputRecordIndex, 0, increasedEIRI, 0, unexpectedAttributeIndex);
			unexpectedAttributeInputRecordIndex = increasedEIRI;		
			
			SimplifiedComponent[] increasedDef = new SimplifiedComponent[unexpectedAttributeDefinition.length+increaseSizeAmount];
			System.arraycopy(unexpectedAttributeDefinition, 0, increasedDef, 0, unexpectedAttributeIndex);
			unexpectedAttributeDefinition = increasedDef;
			
			int[] increasedFEC = new int[unexpectedAttributeFEC.length+increaseSizeAmount];
			System.arraycopy(unexpectedAttributeFEC, 0, increasedFEC, 0, unexpectedAttributeIndex);
			unexpectedAttributeFEC = increasedFEC;
		}	
		unexpectedAttributeDefinition[unexpectedAttributeIndex] = definition;
		unexpectedAttributeInputRecordIndex[unexpectedAttributeIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
		unexpectedAttributeFEC[unexpectedAttributeIndex] = functionalEquivalenceCode;
	}
		
    
	public void unexpectedAmbiguousAttribute(int functionalEquivalenceCode, SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
	    for(int i = 0; i <= unexpectedAmbiguousAttributeIndex; i++){
	        if(unexpectedAmbiguousAttributeFEC[i] == functionalEquivalenceCode) return;
	    }
	    
        messageTotalCount++;
        
        if(unexpectedAmbiguousAttributeInputRecordIndex == null){
		    unexpectedAmbiguousAttributeIndex = 0;
		    unexpectedAmbiguousAttributeInputRecordIndex = new int[initialSize];
		    unexpectedAmbiguousAttributeDefinition = new SimplifiedComponent[initialSize][];
		    unexpectedAmbiguousAttributeFEC = new int[initialSize];	
		}else if(++unexpectedAmbiguousAttributeIndex == unexpectedAmbiguousAttributeInputRecordIndex.length){
		    int[] increasedEIRI = new int[unexpectedAmbiguousAttributeInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unexpectedAmbiguousAttributeInputRecordIndex, 0, increasedEIRI, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeInputRecordIndex = increasedEIRI;		
			
			SimplifiedComponent[][] increasedDef = new SimplifiedComponent[unexpectedAmbiguousAttributeDefinition.length+increaseSizeAmount][];
			System.arraycopy(unexpectedAmbiguousAttributeDefinition, 0, increasedDef, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeDefinition = increasedDef;
			
			int[] increasedFEC = new int[unexpectedAmbiguousAttributeFEC.length+increaseSizeAmount];
			System.arraycopy(unexpectedAmbiguousAttributeFEC, 0, increasedFEC, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeFEC = increasedFEC;
		}	
		unexpectedAmbiguousAttributeDefinition[unexpectedAmbiguousAttributeIndex] = possibleDefinitions;
		unexpectedAmbiguousAttributeInputRecordIndex[unexpectedAmbiguousAttributeIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
		unexpectedAmbiguousAttributeFEC[unexpectedAmbiguousAttributeIndex] = functionalEquivalenceCode;
	}
	
    
	public void misplacedContent(int functionalEquivalenceCode, 
                                            SPattern contextDefinition, 
											int startInputRecordIndex, 
											SPattern definition,
											int inputRecordIndex,
											SPattern sourceDefinition, 
											SPattern reper){//not stored, only used for internal conflict handling
	    for(int i = 0; i <= misplacedIndex; i++){
	        if(misplacedFEC[i] == functionalEquivalenceCode) return;
	    }
        
		super.misplacedContent(functionalEquivalenceCode, 
                                            contextDefinition, 
											startInputRecordIndex, 
											definition,
											inputRecordIndex,
											sourceDefinition, 
											reper);
	}
    public void misplacedContent(int functionalEquivalenceCode, 
                                            SPattern contextDefinition,
											int startInputRecordIndex,
											SPattern definition, 
											int[] inputRecordIndex,
											SPattern[] sourceDefinition, 
											SPattern reper){//not stored, only used for internal conflict handling
	    for(int i = 0; i <= misplacedIndex; i++){
	        if(misplacedFEC[i] == functionalEquivalenceCode) return;
	    }
		
		super.misplacedContent(functionalEquivalenceCode, 
                                            contextDefinition,
											startInputRecordIndex,
											definition, 
											inputRecordIndex,
											sourceDefinition, 
											reper);
	}
			
	
	public void excessiveContent(int functionalEquivalenceCode, 
                                    SRule context,
									int startInputRecordIndex,
									SPattern definition, 
									int[] inputRecordIndex){
	    for(int i = 0; i <= excessiveIndex; i++){
	        if(excessiveFEC[i] == functionalEquivalenceCode){
	            if(excessiveDefinition[i] == definition )return;
	        } 
	    }
	    super.excessiveContent(functionalEquivalenceCode, 
                                    context,
									startInputRecordIndex,
									definition, 
									inputRecordIndex);
		
	}   
	public void excessiveContent(int functionalEquivalenceCode, 
                                SRule context, 
								SPattern definition,
								int inputRecordIndex){
        // TODO review the functionalEquivalenceCode handling
        super.excessiveContent(functionalEquivalenceCode, 
                                context, 
								definition,
								inputRecordIndex);
        
	}

	public void missingContent(int functionalEquivalenceCode, 
                                SRule context,
								int startInputRecordIndex,								 
								SPattern definition, 
								int expected, 
								int found,
								int[] inputRecordIndex){
	    for(int i = 0; i <= missingIndex; i++){
	        if(missingFEC[i] == functionalEquivalenceCode){
	            if(missingDefinition[i] == definition )return;
	        } 
	    }
	      
	    super.missingContent(functionalEquivalenceCode, 
                                context,
								startInputRecordIndex,								 
								definition, 
								expected, 
								found,
								inputRecordIndex);
		
    }
    
    
	public void unresolvedAmbiguousElementContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions){
	    for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
	        if(unresolvedAmbiguousElementFECEE[i] == functionalEquivalenceCode) return;
	    }
	            
	    super.unresolvedAmbiguousElementContentError(functionalEquivalenceCode, 
                                                inputRecordIndex, 
                                                possibleDefinitions);
        
		
	}
	
    
    public void unresolvedUnresolvedElementContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions){
	    for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
	        if(unresolvedUnresolvedElementFECEE[i] == functionalEquivalenceCode) return;
	    }
	            
	    super.unresolvedUnresolvedElementContentError(functionalEquivalenceCode, 
                                                    inputRecordIndex, 
                                                    possibleDefinitions);
		
	}
    
    
	public void unresolvedAttributeContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SAttribute[] possibleDefinitions){
	    for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
	        if(unresolvedAttributeFECEE[i] == functionalEquivalenceCode) return;
	    }
	            
	    super.unresolvedAttributeContentError(functionalEquivalenceCode, 
                                            inputRecordIndex, 
                                            possibleDefinitions);
		
	}
	
	public void ambiguousUnresolvedElementContentWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions){
	    for(int i = 0; i <= ambiguousUnresolvedElementIndexWW; i++){
	        if(ambiguousUnresolvedElementFECWW[i] == functionalEquivalenceCode) return;
	    }
	    
	    super.ambiguousUnresolvedElementContentWarning(functionalEquivalenceCode,
                                                inputRecordIndex, 
                                                possibleDefinitions);
	    
	}
    
    
    public void ambiguousAmbiguousElementContentWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions){
	    for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
	        if(ambiguousAmbiguousElementFECWW[i] == functionalEquivalenceCode) return;
	    }
	            
	    super.ambiguousAmbiguousElementContentWarning(functionalEquivalenceCode,
                                    inputRecordIndex, 
									possibleDefinitions);
		
	}
    
    
	public void ambiguousAttributeContentWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SAttribute[] possibleDefinitions){
	    for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
	        if(ambiguousAttributeFECWW[i] == functionalEquivalenceCode) return;
	    }
	    
	    super.ambiguousAttributeContentWarning(functionalEquivalenceCode,
                                            inputRecordIndex, 
                                            possibleDefinitions);
	    
	}
    
	public void ambiguousCharacterContentWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SPattern[] possibleDefinitions){
	    for(int i = 0; i <= ambiguousCharsIndexWW; i++){
	        if(ambiguousCharsFECWW[i] == functionalEquivalenceCode) return;
	    }
	      
	    super.ambiguousCharacterContentWarning(functionalEquivalenceCode,
                                            inputRecordIndex, 
                                            possibleDefinitions);
	          
	}
    
    
    public void ambiguousAttributeValueWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SPattern[] possibleDefinitions){
	    for(int i = 0; i <= ambiguousAVIndexWW; i++){
	        if(ambiguousAVFECWW[i] == functionalEquivalenceCode) return;
	    }
	            
	    ambiguousAttributeValueWarning(functionalEquivalenceCode,
                                        inputRecordIndex, 
									    possibleDefinitions);
		 
	}	
	
	
	    
    
	public void illegalContent(int functionalEquivalenceCode, 
                            SRule context, 
                            int startInputRecordIndex){
	    for(int i = 0; i <= illegalIndex; i++){
	        if(illegalFEC[i] == functionalEquivalenceCode) {
	            if(illegalContext[i] == context )return;
	        } 
	    }
	    
	    super.illegalContent(functionalEquivalenceCode, 
                            context, 
                            startInputRecordIndex);
	    
	}
    
    	
    // {15}
	public void characterContentDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        for(int i = 0; i <= datatypeCharsIndex; i++){
	        if(datatypeCharsFEC[i] == functionalEquivalenceCode) return;
	    }
	            
	    super.characterContentDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
	    
	}
        
    
    //{16}
	public void attributeValueDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
	    for(int i = 0; i <= datatypeAVIndex; i++){
	        if(datatypeAVFEC[i] == functionalEquivalenceCode) return;
	    }
	            
	    super.attributeValueDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
	    
	}
        
        
	public void characterContentValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition){
	    for(int i = 0; i <= valueCharsIndex; i++){
	        if(valueCharsFEC[i] == functionalEquivalenceCode) return;
	    }
	    
	    super.characterContentValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
	    
	}
        
    
	public void attributeValueValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition){
	    for(int i = 0; i <= valueAVIndex; i++){
	        if(valueAVFEC[i] == functionalEquivalenceCode) return;
	    }
	    
	    super.attributeValueValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
	    
	}
        
    
	public void characterContentExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition){
	    for(int i = 0; i <= exceptCharsIndex; i++){
	        if(exceptCharsFEC[i] == functionalEquivalenceCode) return;
	    }
	    
	    super.characterContentExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
	    
	}
    
        
	public void attributeValueExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition){
	    for(int i = 0; i <= exceptAVIndex; i++){
	        if(exceptAVFEC[i] == functionalEquivalenceCode) return;
	    }
	      
	    super.attributeValueExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
	    
	}
        
    
	public void unexpectedCharacterContent(int functionalEquivalenceCode, int inputRecordIndex, SElement elementDefinition){
	    for(int i = 0; i <= unexpectedCharsIndex; i++){
	        if(unexpectedCharsFEC[i] == functionalEquivalenceCode) return;
	    }
	    
	    super.unexpectedCharacterContent(functionalEquivalenceCode, inputRecordIndex, elementDefinition);
	    
	}
    
    
	public void unexpectedAttributeValue(int functionalEquivalenceCode, int inputRecordIndex, SAttribute attributeDefinition){
	    for(int i = 0; i <= unexpectedAVIndex; i++){
	        if(unexpectedAVFEC[i] == functionalEquivalenceCode) return;
	    }
	            
	    super.unexpectedAttributeValue(functionalEquivalenceCode, inputRecordIndex, attributeDefinition);
	    
	}
        
    
	public void unresolvedCharacterContent(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions){
	    for(int i = 0; i <= unresolvedCharsIndexEE; i++){
	        if(unresolvedCharsFECEE[i] == functionalEquivalenceCode) return;
	    }
	            
	    super.unresolvedCharacterContent(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
	    
	}
	
    
	// {24}
	public void unresolvedAttributeValue(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions){
	    for(int i = 0; i <= unresolvedAVIndexEE; i++){
	        if(unresolvedAVFECEE[i] == functionalEquivalenceCode) return;
	    }
	      
	    super.unresolvedAttributeValue(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
	    
	}
        
    
    // {25}
	public void listTokenDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
	    for(int i = 0; i <= datatypeTokenIndex; i++){
	        if(datatypeTokenFEC[i] == functionalEquivalenceCode) return;
	    }
	     
	    super.listTokenDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
	    
	}
        
        
	public void listTokenValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition){
	    for(int i = 0; i <= valueTokenIndex; i++){
	        if(valueTokenFEC[i] == functionalEquivalenceCode) return;
	    }
	    
	    super.listTokenValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
	    
	}
        
    
	public void listTokenExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition){
	    for(int i = 0; i <= exceptTokenIndex; i++){
	        if(exceptTokenFEC[i] == functionalEquivalenceCode) return;
	    }
	    
	    super.listTokenExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
	    
	}
	
    
    public void unresolvedListTokenInContextError(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions){
        for(int i = 0; i <= unresolvedTokenIndexLPICE; i++){
	        if(unresolvedTokenFECLPICE[i] == functionalEquivalenceCode) return;
	    }
	      
	    super.unresolvedListTokenInContextError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
	    
    }
    
    
    public void ambiguousListTokenInContextWarning(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions){
        for(int i = 0; i <= ambiguousTokenIndexLPICW; i++){
	        if(ambiguousTokenFECLPICW[i] == functionalEquivalenceCode) return;
	    }
	     
	    super.ambiguousListTokenInContextWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
	    
    }   
    
    
	public void missingCompositorContent(int functionalEquivalenceCode, 
                                SRule context, 
								int startInputRecordIndex,								 
								SPattern definition, 
								int expected, 
								int found){
	    for(int i = 0; i <= missingCompositorContentIndex; i++){
	        if(missingCompositorContentFEC[i] == functionalEquivalenceCode){
	            if(missingCompositorContentDefinition[i] == definition )return;
	        }   
	    }
	            
	    super.missingCompositorContent(functionalEquivalenceCode, 
                                context, 
								startInputRecordIndex,								 
								definition, 
								expected, 
								found);
			
	}	
}
