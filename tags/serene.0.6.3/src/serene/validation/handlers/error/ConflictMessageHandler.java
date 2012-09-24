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

import serene.validation.handlers.conflict.ElementConflictResolver;


import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

import serene.util.IntList;

public class ConflictMessageHandler  extends AbstractMessageHandler implements ConflictErrorCatcher{	

 	// {2}
	int[] unknownElementFEC;// functional equivalence code	
	
	// {3}	
	int[] unexpectedElementFEC;
	
	// {4}
	int[] unexpectedAmbiguousElementFEC;
	
	// {5}
	int[] unknownAttributeFEC;	
	
	// {6}
	int[] unexpectedAttributeFEC;
	
	// {7}
	int[] unexpectedAmbiguousAttributeFEC;
	
	
	// {8}
	int[] misplacedFEC;

	// {9}
	int[] excessiveFEC;
	
	// {10}
	int[] missingFEC;
	
	
	// {11}
	int[] illegalFEC;
	
	// {12 A}
	int[] unresolvedAmbiguousElementFECEE;

	// {12 U}
	int[] unresolvedUnresolvedElementFECEE;

	// {13}
	int[] unresolvedAttributeFECEE;

	// {14}
	int[] ambiguousCharsFECEE;
		
    // {w1 U}
	int[] ambiguousUnresolvedElementFECWW;

	// {w1 A}
	int[] ambiguousAmbiguousElementFECWW;


	// {w2}
	int[] ambiguousAttributeFECWW;

	// {w3}
	int[] ambiguousCharsFECWW;
	
	// {w4}
	int[] ambiguousAVFECWW;
	
		
	// {15}
	int[] datatypeCharsFEC;
	
	// {16}
	int[] datatypeAVFEC;
   
	
	// {17}
	int[] valueCharsFEC;
	
	// {18}
	int[] valueAVFEC;
	
	// {19}
	int[] exceptCharsFEC;
	
	// {20}
	int[] exceptAVFEC;
	
	// {21}
	int[] unexpectedCharsFEC;
	
	// {22}
	int[] unexpectedAVFEC;
	
	
	// {23}
	int[] unresolvedCharsFECEE;
	
	// {24}
	int[] unresolvedAVFECEE;
	
	
	// {25}
	int[] datatypeTokenFEC;
    	
	// {26}
	int[] valueTokenFEC;
	
	// {27}
	int[] exceptTokenFEC;
	
	// {28}
    
    // {28_1}
	int[] unresolvedTokenFECLPICE;
    
    // {28_2}
	int[] ambiguousTokenFECLPICW;
    
	
	// {29}
	int[] missingCompositorContentFEC;
    
    // {30}
    int conflictFEC;
    
	public ConflictMessageHandler(){
		super();
	}	
	
    public ConflictMessageReporter getConflictMessageReporter(ErrorDispatcher errorDispatcher){
        
        ConflictMessageReporter result =  new ConflictMessageReporter(parent,
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
                
        return result;
    }
    
    
    
	public void unknownElement(int functionalEquivalenceCode, int inputRecordIndex){
        messageTotalCount++;		
		if(unknownElementIndex < 0){
		    unknownElementIndex = 0;
		    unknownElementInputRecordIndex = new int[initialSize];
		    unknownElementFEC = new int[initialSize];
		}else if(++unknownElementIndex == unknownElementInputRecordIndex.length){			
		    int[] increasedEIRI = new int[unknownElementInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unknownElementInputRecordIndex, 0, increasedEIRI, 0, unknownElementIndex);
			unknownElementInputRecordIndex = increasedEIRI;		
			
			int[] increasedFEC = new int[unknownElementFEC.length+increaseSizeAmount];
			System.arraycopy(unknownElementFEC, 0, increasedFEC, 0, unknownElementIndex);
			unknownElementFEC = increasedFEC;
		}
		unknownElementInputRecordIndex[unknownElementIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
        unknownElementFEC[unknownElementIndex] = functionalEquivalenceCode;
	}	
    public void clearUnknownElement(){
        
        messageTotalCount -= (unknownElementIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unknownElementInputRecordIndex, 0, unknownElementIndex+1, this);
        unknownElementIndex = -1;
        unknownElementInputRecordIndex = null;
        
        unknownElementFEC = null;
    }	
    public void clearUnknownElement(int messageId){
        int removeIndex = getRemoveIndex(messageId, unknownElementFEC);        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unknownElementInputRecordIndex[removeIndex], this);
        int moved = unknownElementIndex - removeIndex;
        unknownElementIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unknownElementInputRecordIndex, removeIndex+1, unknownElementInputRecordIndex, removeIndex, moved);            
            System.arraycopy(unknownElementFEC, removeIndex+1, unknownElementFEC, removeIndex, moved);               
        }
    }
    void transferUnknownElement(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unknownElementFEC);
        
        if(removeIndex == -1) return;
        
        other.unknownElement(unknownElementFEC[removeIndex],
                            unknownElementInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unknownElementInputRecordIndex[removeIndex], this);
        
        int moved = unknownElementIndex - removeIndex;
        unknownElementIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unknownElementInputRecordIndex, removeIndex+1, unknownElementInputRecordIndex, removeIndex, moved);
            System.arraycopy(unknownElementFEC, removeIndex+1, unknownElementFEC, removeIndex, moved);               
        }
    }
    
	public void unexpectedElement(int functionalEquivalenceCode, SimplifiedComponent definition, int inputRecordIndex){
        messageTotalCount++;
        if(unexpectedElementIndex < 0){
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
		
	public void clearUnexpectedElement(){
        
        messageTotalCount -= (unexpectedElementIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unexpectedElementInputRecordIndex, 0, unexpectedElementIndex+1, this);
        unexpectedElementIndex = -1;
        unexpectedElementInputRecordIndex = null;
        unexpectedElementDefinition = null;
        
        unexpectedElementFEC = null;
    }
    
    public void clearUnexpectedElement(int messageId){
        int removeIndex = getRemoveIndex(messageId, unexpectedElementFEC);        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedElementInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedElementIndex - removeIndex;
        messageTotalCount--;
        unexpectedElementIndex--;
        if(moved > 0){
            System.arraycopy(unexpectedElementDefinition, removeIndex+1, unexpectedElementDefinition, removeIndex, moved);
            System.arraycopy(unexpectedElementInputRecordIndex, removeIndex+1, unexpectedElementInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedElementFEC, removeIndex+1, unexpectedElementFEC, removeIndex, moved);
        }
    }
    void transferUnexpectedElement(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unexpectedElementFEC);
        
        if(removeIndex == -1) return;
                
        other.unexpectedElement(unexpectedElementFEC[removeIndex],
                                unexpectedElementDefinition[removeIndex],
                                unexpectedElementInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedElementInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedElementIndex - removeIndex;
        messageTotalCount--;
        unexpectedElementIndex--;
        if(moved > 0){            
            System.arraycopy(unexpectedElementDefinition, removeIndex+1, unexpectedElementDefinition, removeIndex, moved);
            System.arraycopy(unexpectedElementInputRecordIndex, removeIndex+1, unexpectedElementInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedElementFEC, removeIndex+1, unexpectedElementFEC, removeIndex, moved);
        }
    }
    
	public void unexpectedAmbiguousElement(int functionalEquivalenceCode, SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
        messageTotalCount++;		
        if(unexpectedAmbiguousElementIndex < 0){
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
	
	public void clearUnexpectedAmbiguousElement(){
        
        messageTotalCount -= (unexpectedAmbiguousElementIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousElementInputRecordIndex, 0, unexpectedAmbiguousElementIndex+1, this);
        unexpectedAmbiguousElementIndex = -1;
        unexpectedAmbiguousElementInputRecordIndex = null;
        unexpectedAmbiguousElementDefinition = null;
        
        unexpectedAmbiguousElementFEC = null;
    }
    
    public void clearUnexpectedAmbiguousElement(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, unexpectedAmbiguousElementFEC);        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousElementInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedAmbiguousElementIndex - removeIndex;
        messageTotalCount--;
        unexpectedAmbiguousElementIndex--;
        if(moved > 0){
            System.arraycopy(unexpectedAmbiguousElementDefinition, removeIndex+1, unexpectedAmbiguousElementDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousElementInputRecordIndex, removeIndex+1, unexpectedAmbiguousElementInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousElementFEC, removeIndex+1, unexpectedAmbiguousElementFEC, removeIndex, moved);
        }
    }
    
	void transferUnexpectedAmbiguousElement(int messageId, ConflictMessageHandler other){
	    
        int removeIndex = getRemoveIndex(messageId, unexpectedAmbiguousElementFEC);
        
        if(removeIndex == -1) return;
        
        other.unexpectedAmbiguousElement(unexpectedAmbiguousElementFEC[removeIndex],
                                unexpectedAmbiguousElementDefinition[removeIndex],
                                unexpectedAmbiguousElementInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousElementInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedAmbiguousElementIndex - removeIndex;
        messageTotalCount--;
        unexpectedAmbiguousElementIndex--;
        if(moved > 0){            
            System.arraycopy(unexpectedAmbiguousElementDefinition, removeIndex+1, unexpectedAmbiguousElementDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousElementInputRecordIndex, removeIndex+1, unexpectedAmbiguousElementInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousElementFEC, removeIndex+1, unexpectedAmbiguousElementFEC, removeIndex, moved);
        }
	}

    public void unknownAttribute(int functionalEquivalenceCode, int inputRecordIndex){
        messageTotalCount++;		
		if(unknownAttributeIndex < 0){
		    unknownAttributeIndex = 0;
		    unknownAttributeInputRecordIndex = new int[initialSize];
		    unknownAttributeFEC = new int[initialSize];
		}else if(++unknownAttributeIndex == unknownAttributeInputRecordIndex.length){
		    int[] increasedEIRI = new int[unknownAttributeInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unknownAttributeInputRecordIndex, 0, increasedEIRI, 0, unknownAttributeIndex);
			unknownAttributeInputRecordIndex = increasedEIRI;		
			
			int[] increasedFEC = new int[unknownAttributeFEC.length+increaseSizeAmount];
			System.arraycopy(unknownAttributeFEC, 0, increasedFEC, 0, unknownAttributeIndex);
			unknownAttributeFEC = increasedFEC;
		}
		unknownAttributeInputRecordIndex[unknownAttributeIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
        unknownAttributeFEC[unknownAttributeIndex] = functionalEquivalenceCode;
	}	
    public void clearUnknownAttribute(){
        
        messageTotalCount -= (unknownAttributeIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unknownAttributeInputRecordIndex, 0, unknownAttributeIndex+1, this);
        unknownAttributeIndex = -1;
        unknownAttributeInputRecordIndex = null;
        
        unknownAttributeFEC = null;
    }	
    public void clearUnknownAttribute(int messageId){
        int removeIndex = getRemoveIndex(messageId, unknownAttributeFEC);        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unknownAttributeInputRecordIndex[removeIndex], this);
        int moved = unknownAttributeIndex - removeIndex;
        unknownAttributeIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unknownAttributeInputRecordIndex, removeIndex+1, unknownAttributeInputRecordIndex, removeIndex, moved);            
            System.arraycopy(unknownAttributeFEC, removeIndex+1, unknownAttributeFEC, removeIndex, moved);               
        }
    }
    void transferUnknownAttribute(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unknownAttributeFEC);
        
        if(removeIndex == -1) return;
        
        other.unknownAttribute(unknownAttributeFEC[removeIndex],
                            unknownAttributeInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unknownAttributeInputRecordIndex[removeIndex], this);
        
        int moved = unknownAttributeIndex - removeIndex;
        unknownAttributeIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unknownAttributeInputRecordIndex, removeIndex+1, unknownAttributeInputRecordIndex, removeIndex, moved);
            System.arraycopy(unknownAttributeFEC, removeIndex+1, unknownAttributeFEC, removeIndex, moved);               
        }
    }
    
	public void unexpectedAttribute(int functionalEquivalenceCode, SimplifiedComponent definition, int inputRecordIndex){
        messageTotalCount++;
        if(unexpectedAttributeIndex < 0){
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
		
	public void clearUnexpectedAttribute(){
        messageTotalCount -= (unexpectedAttributeIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unexpectedAttributeInputRecordIndex, 0, unexpectedAttributeIndex+1, this);
        unexpectedAttributeIndex = -1;
        unexpectedAttributeInputRecordIndex = null;
        unexpectedAttributeDefinition = null;
        
        unexpectedAttributeFEC = null;
    }
    
    public void clearUnexpectedAttribute(int messageId){
        int removeIndex = getRemoveIndex(messageId, unexpectedAttributeFEC);        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedAttributeInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedAttributeIndex - removeIndex;
        messageTotalCount--;
        unexpectedAttributeIndex--;
        if(moved > 0){
            System.arraycopy(unexpectedAttributeDefinition, removeIndex+1, unexpectedAttributeDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAttributeInputRecordIndex, removeIndex+1, unexpectedAttributeInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedAttributeFEC, removeIndex+1, unexpectedAttributeFEC, removeIndex, moved);
        }
    }
    void transferUnexpectedAttribute(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unexpectedAttributeFEC);
        
        if(removeIndex == -1) return;
                
        other.unexpectedAttribute(unexpectedAttributeFEC[removeIndex],
                                unexpectedAttributeDefinition[removeIndex],
                                unexpectedAttributeInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedAttributeInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedAttributeIndex - removeIndex;
        messageTotalCount--;
        unexpectedAttributeIndex--;
        if(moved > 0){
            System.arraycopy(unexpectedAttributeDefinition, removeIndex+1, unexpectedAttributeDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAttributeInputRecordIndex, removeIndex+1, unexpectedAttributeInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedAttributeFEC, removeIndex+1, unexpectedAttributeFEC, removeIndex, moved);
        }
    }
    
	public void unexpectedAmbiguousAttribute(int functionalEquivalenceCode, SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
        messageTotalCount++;
        if(unexpectedAmbiguousAttributeIndex < 0){
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
	
	public void clearUnexpectedAmbiguousAttribute(){
        messageTotalCount -= (unexpectedAmbiguousAttributeIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousAttributeInputRecordIndex, 0, unexpectedAmbiguousAttributeIndex+1, this);
        unexpectedAmbiguousAttributeIndex = -1;
        unexpectedAmbiguousAttributeInputRecordIndex = null;
        unexpectedAmbiguousAttributeDefinition = null;
        
        unexpectedAmbiguousAttributeFEC = null;
    }
    
    public void clearUnexpectedAmbiguousAttribute(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, unexpectedAmbiguousAttributeFEC);        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousAttributeInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedAmbiguousAttributeIndex - removeIndex;
        messageTotalCount--;
        unexpectedAmbiguousAttributeIndex--;
        if(moved > 0){
            System.arraycopy(unexpectedAmbiguousAttributeDefinition, removeIndex+1, unexpectedAmbiguousAttributeDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeInputRecordIndex, removeIndex+1, unexpectedAmbiguousAttributeInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeFEC, removeIndex+1, unexpectedAmbiguousAttributeFEC, removeIndex, moved);
        }
    }
    
	void transferUnexpectedAmbiguousAttribute(int messageId, ConflictMessageHandler other){
	    
        int removeIndex = getRemoveIndex(messageId, unexpectedAmbiguousAttributeFEC);
        
        if(removeIndex == -1) return;
        
        other.unexpectedAmbiguousAttribute(unexpectedAmbiguousAttributeFEC[removeIndex],
                                unexpectedAmbiguousAttributeDefinition[removeIndex],
                                unexpectedAmbiguousAttributeInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousAttributeInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedAmbiguousAttributeIndex - removeIndex;
        messageTotalCount--;
        unexpectedAmbiguousAttributeIndex--;
        if(moved > 0){            
            System.arraycopy(unexpectedAmbiguousAttributeDefinition, removeIndex+1, unexpectedAmbiguousAttributeDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeInputRecordIndex, removeIndex+1, unexpectedAmbiguousAttributeInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeFEC, removeIndex+1, unexpectedAmbiguousAttributeFEC, removeIndex, moved);
        }
	}
	
	
    
	public void misplacedContent(int functionalEquivalenceCode, 
                                            SPattern contextDefinition,
											int startInputRecordIndex, 
											SPattern definition,
											int inputRecordIndex,
											SPattern sourceDefinition, 
											SPattern reper){//not stored, only used for internal conflict handling
        
		if(misplacedIndex < 0){
		    misplacedIndex = 0;
		    
		    //create arrays for everything
		    //record everything
		    
		    misplacedContext = new SPattern[initialSize];
            misplacedStartInputRecordIndex = new int[initialSize];
            misplacedDefinition = new SPattern[initialSize][];
            misplacedInputRecordIndex = new int[initialSize][][];
            
            misplacedFEC = new int[initialSize];            


            
            misplacedContext[misplacedIndex] = contextDefinition;
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new SPattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = new int[1];
            misplacedInputRecordIndex[misplacedIndex][0][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
            
            misplacedFEC[misplacedIndex] = functionalEquivalenceCode;
            return;
		}else{		    
            for(int i = 0; i <= misplacedIndex; i++){
				if(misplacedContext[i].equals(contextDefinition)
                    && misplacedStartInputRecordIndex[i] == startInputRecordIndex){
                    for(int j = 0; j < misplacedDefinition[i].length; j++){
						if(misplacedDefinition[i][j].equals(definition)){						    
						    //increase size for inputRecordIndex
						    //record inputRecordIndex
						    int oldLength = misplacedInputRecordIndex[i][j].length;
						    int[] increasedIRI = new int[oldLength+1];
						    System.arraycopy(misplacedInputRecordIndex[i][j], 0, increasedIRI, 0, oldLength);
						    misplacedInputRecordIndex[i][j] = increasedIRI;
						    
						    misplacedInputRecordIndex[i][j][oldLength] = inputRecordIndex;
						    activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
						    return;
						}
					}
					//increase size for definition
					//create array for inputRecordIndex
					//record inputRecordIndex
					
					int oldLength = misplacedDefinition[i].length;	
					
					SPattern[] increasedDef = new SPattern[oldLength+1];					
					System.arraycopy(misplacedDefinition[i], 0, increasedDef, 0, oldLength);
					misplacedDefinition[i] = increasedDef;
					
					int[][] increasedIRI = new int[(oldLength+1)][];
					System.arraycopy(misplacedInputRecordIndex[i], 0, increasedIRI, 0, oldLength);
					misplacedInputRecordIndex[i] = increasedIRI;			
					misplacedInputRecordIndex[i][oldLength] = new int[1];
					
					
					misplacedDefinition[i][oldLength] = definition;                    
                    misplacedInputRecordIndex[i][oldLength][0] = inputRecordIndex;
                    activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
                    return;  					
                }
            }
            //increase size for context and startInputRecordIndex
            //create array for definition
			//create array for inputRecordIndex
			//record inputRecordIndex
			
			messageTotalCount++;
			if(++misplacedIndex == misplacedContext.length){			    
                SPattern[] increasedCDef = new SPattern[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedContext, 0, increasedCDef, 0, misplacedIndex);
                misplacedContext = increasedCDef;               
                
                int[] increasedSIRI  = new int[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedStartInputRecordIndex, 0, increasedSIRI, 0, misplacedIndex);
                misplacedStartInputRecordIndex = increasedSIRI;
                
                SPattern[][] increasedDef = new SPattern[misplacedIndex+increaseSizeAmount][];
                System.arraycopy(misplacedDefinition, 0, increasedDef, 0, misplacedIndex);
                misplacedDefinition = increasedDef;
                 
                int[][][] increasedIRI = new int[misplacedIndex+increaseSizeAmount][][];
                System.arraycopy(misplacedInputRecordIndex, 0, increasedIRI, 0, misplacedIndex);
                misplacedInputRecordIndex = increasedIRI;
                
                
                int[] increasedFEC = new int[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedFEC, 0, increasedFEC, 0, misplacedIndex);
                misplacedFEC = increasedFEC;
            }
            
            misplacedContext[misplacedIndex] = contextDefinition;
            
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new SPattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = new int[1];
            misplacedInputRecordIndex[misplacedIndex][0][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
            
            
			misplacedFEC[misplacedIndex] = functionalEquivalenceCode;
		}
		
	}
    public void misplacedContent(int functionalEquivalenceCode, 
                                            SPattern contextDefinition,
											int startInputRecordIndex, 
											SPattern definition, 
											int[] inputRecordIndex,
											SPattern[] sourceDefinition, 
											SPattern reper){//not stored, only used for internal conflict handling
		
		if(misplacedIndex < 0){
		    misplacedIndex = 0;
		    
		    //create arrays for everything
		    //record everything
		    
		    misplacedContext = new SPattern[initialSize];
            misplacedStartInputRecordIndex = new int[initialSize];
            misplacedDefinition = new SPattern[initialSize][];
            misplacedInputRecordIndex = new int[initialSize][][];
              
            misplacedFEC = new int[initialSize];    
            
            
            
            misplacedContext[misplacedIndex] = contextDefinition;
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new SPattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
            
            misplacedFEC[misplacedIndex] = functionalEquivalenceCode;
            return;
		}else{		    
            for(int i = 0; i <= misplacedIndex; i++){
				if(misplacedContext[i].equals(contextDefinition)
                    && misplacedStartInputRecordIndex[i] == startInputRecordIndex){
                    for(int j = 0; j < misplacedDefinition[i].length; j++){
						if(misplacedDefinition[i][j].equals(definition)){						    
						    //increase size for inputRecordIndex
						    //record inputRecordIndex
						    int oldLength = misplacedInputRecordIndex[i][j].length;
						    int increase = inputRecordIndex.length;
						    int[] increasedIRI = new int[oldLength+increase];						    
						    System.arraycopy(misplacedInputRecordIndex[i][j], 0, increasedIRI, 0, oldLength);
						    System.arraycopy(inputRecordIndex, 0, increasedIRI, oldLength, increase);
						    misplacedInputRecordIndex[i][j] = increasedIRI;
						    activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
						    return;
						}
					}
					//increase size for definition
					//create array for inputRecordIndex
					//record inputRecordIndex
					
					int oldLength = misplacedDefinition[i].length;	
					
					SPattern[] increasedDef = new SPattern[oldLength+1];					
					System.arraycopy(misplacedDefinition[i], 0, increasedDef, 0, oldLength);
					misplacedDefinition[i] = increasedDef;
					
					int[][] increasedIRI = new int[(oldLength+1)][];
					System.arraycopy(misplacedInputRecordIndex[i], 0, increasedIRI, 0, oldLength);
					misplacedInputRecordIndex[i] = increasedIRI;
					
					
					misplacedDefinition[i][oldLength] = definition;                    
                    misplacedInputRecordIndex[i][oldLength] = inputRecordIndex;
                    activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
                    return;  					
                }
            }
            //increase size for context and startInputRecordIndex
            //create array for definition
			//create array for inputRecordIndex
			//record inputRecordIndex
			
			messageTotalCount++;
			if(++misplacedIndex == misplacedContext.length){			    
                SPattern[] increasedCDef = new SPattern[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedContext, 0, increasedCDef, 0, misplacedIndex);
                misplacedContext = increasedCDef;               
                
                int[] increasedSIRI  = new int[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedStartInputRecordIndex, 0, increasedSIRI, 0, misplacedIndex);
                misplacedStartInputRecordIndex = increasedSIRI;
                
                SPattern[][] increasedDef = new SPattern[misplacedIndex+increaseSizeAmount][];
                System.arraycopy(misplacedDefinition, 0, increasedDef, 0, misplacedIndex);
                misplacedDefinition = increasedDef;
                 
                int[][][] increasedIRI = new int[misplacedIndex+increaseSizeAmount][][];
                System.arraycopy(misplacedInputRecordIndex, 0, increasedIRI, 0, misplacedIndex);
                misplacedInputRecordIndex = increasedIRI;
                
                int[] increasedFEC = new int[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedFEC, 0, increasedFEC, 0, misplacedIndex);
                misplacedFEC = increasedFEC;
            }
            
            misplacedContext[misplacedIndex] = contextDefinition;
            
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new SPattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
            
            misplacedFEC[misplacedIndex] = functionalEquivalenceCode;
		}
	}
    public void clearMisplacedContent(){
        
        messageTotalCount -= (misplacedIndex+1);
        
        for(int i = 0; i <= misplacedIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(misplacedStartInputRecordIndex[i], this);
            for(int j = 0; j < misplacedInputRecordIndex[i].length; j++){
                activeInputDescriptor.unregisterClientForRecord(misplacedInputRecordIndex[i][j], 0, misplacedInputRecordIndex[i][j].length, this);
            }
        }
        
        misplacedContext = null;	
        misplacedStartInputRecordIndex = null;
        misplacedDefinition = null; 
        misplacedInputRecordIndex = null;
        misplacedIndex = -1;	
        
        misplacedFEC = null;
    }
    public void clearMisplacedContent(int messageId){        
        int removeIndex = getRemoveIndex(messageId, misplacedFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(misplacedStartInputRecordIndex[removeIndex], this);
        for(int i = 0; i < misplacedInputRecordIndex[removeIndex].length; i++){
            activeInputDescriptor.unregisterClientForRecord(misplacedInputRecordIndex[removeIndex][i], 0, misplacedInputRecordIndex[removeIndex][i].length, this);
        }
        
        int moved = misplacedIndex - removeIndex;
        misplacedIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(misplacedContext, removeIndex+1, misplacedContext, removeIndex, moved);
            System.arraycopy(misplacedStartInputRecordIndex, removeIndex+1, misplacedStartInputRecordIndex, removeIndex, moved);
            System.arraycopy(misplacedDefinition, removeIndex+1, misplacedDefinition, removeIndex, moved);
            System.arraycopy(misplacedInputRecordIndex, removeIndex+1, misplacedInputRecordIndex, removeIndex, moved);
            
            System.arraycopy(misplacedFEC, removeIndex+1, misplacedFEC, removeIndex, moved);
        }
    }	
    
	void transferMisplacedContent(int messageId, ConflictMessageHandler other){
	    int removeIndex = getRemoveIndex(messageId, misplacedFEC);
	    
        if(removeIndex == -1) return;
        
        for(int i = 0; i < misplacedInputRecordIndex[removeIndex].length; i++){
            activeInputDescriptor.unregisterClientForRecord(misplacedInputRecordIndex[removeIndex][i], 0, misplacedInputRecordIndex[removeIndex][i].length, this);
        }
        activeInputDescriptor.unregisterClientForRecord(misplacedStartInputRecordIndex[removeIndex], this);        
	    
	    SPattern[] sourceDefinition = null;
	    SPattern reper = null;
	    for(int j = 0; j < misplacedDefinition[removeIndex].length; j++){
            other.misplacedContent(misplacedFEC[removeIndex],
                                    misplacedContext[removeIndex],
                                    misplacedStartInputRecordIndex[removeIndex],
                                    misplacedDefinition[removeIndex][j],
                                    misplacedInputRecordIndex[removeIndex][j],
                                    sourceDefinition,
                                    reper);
        }
	    
        int moved = misplacedIndex - removeIndex;
        misplacedIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(misplacedContext, removeIndex+1, misplacedContext, removeIndex, moved);
            System.arraycopy(misplacedStartInputRecordIndex, removeIndex+1, misplacedStartInputRecordIndex, removeIndex, moved);
            System.arraycopy(misplacedDefinition, removeIndex+1, misplacedDefinition, removeIndex, moved);
            System.arraycopy(misplacedInputRecordIndex, removeIndex+1, misplacedInputRecordIndex, removeIndex, moved);
            
            System.arraycopy(misplacedFEC, removeIndex+1, misplacedFEC, removeIndex, moved);
        }
	}		
	
	public void excessiveContent(int functionalEquivalenceCode, 
                                    SRule context,
									int startInputRecordIndex,
									SPattern definition, 
									int[] inputRecordIndex){
		
        messageTotalCount++;
		if(excessiveIndex < 0){
			excessiveIndex = 0;
			excessiveContext = new SPattern[initialSize];		
			excessiveStartInputRecordIndex = new int[initialSize];
			excessiveDefinition = new SPattern[initialSize];
			excessiveInputRecordIndex = new int[initialSize][];	
			
			excessiveFEC = new int[initialSize];	
		}else if(++excessiveIndex == excessiveContext.length){
		    int newSize = excessiveIndex+increaseSizeAmount;
		    
		    SPattern[] increasedEC = new SPattern[newSize];
			System.arraycopy(excessiveContext, 0, increasedEC, 0, excessiveIndex);
			excessiveContext = increasedEC;
			
			int[] increasedSLN = new int[newSize];
			System.arraycopy(excessiveStartInputRecordIndex, 0, increasedSLN, 0, excessiveIndex);
			excessiveStartInputRecordIndex = increasedSLN;
			
			SPattern[] increasedED = new SPattern[newSize];
			System.arraycopy(excessiveDefinition, 0, increasedED, 0, excessiveIndex);
			excessiveDefinition = increasedED;
			
			int[][] increasedIRI = new int[newSize][];
			System.arraycopy(excessiveInputRecordIndex, 0, increasedIRI, 0, excessiveIndex);
			excessiveInputRecordIndex = increasedIRI;
			
			
			int[] increasedFEC = new int[newSize];
			System.arraycopy(excessiveFEC, 0, increasedFEC, 0, excessiveIndex);
			excessiveFEC = increasedFEC;
		}
		excessiveContext[excessiveIndex] = context;
		excessiveStartInputRecordIndex[excessiveIndex] = startInputRecordIndex;
		activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		excessiveDefinition[excessiveIndex] = definition;
		excessiveInputRecordIndex[excessiveIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
		
		excessiveFEC[excessiveIndex] = functionalEquivalenceCode;
		
	}   
	public void excessiveContent(int functionalEquivalenceCode, 
                                SRule context, 
								SPattern definition,
								int inputRecordIndex){
        
        boolean recorded = false;
		for(int i = 0; i <= excessiveIndex; i++){
			if(excessiveContext[i].equals(context) &&
				excessiveDefinition[i].equals(definition)){
			    
                recorded =  true;
                
                int length = excessiveInputRecordIndex[i].length;
                int[] increasedII = new int[length+1];
                System.arraycopy(excessiveInputRecordIndex[i], 0, increasedII, 0, length);
                excessiveInputRecordIndex[i] = increasedII;
                excessiveInputRecordIndex[i][length] = inputRecordIndex;
                activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
				break;
			}
		}		
        if(!recorded) throw new IllegalArgumentException();
	}
	public void clearExcessiveContent(){
        
        for(int i = 0; i <= excessiveIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(excessiveStartInputRecordIndex[i], this);
            activeInputDescriptor.unregisterClientForRecord(excessiveInputRecordIndex[i], 0, excessiveInputRecordIndex[i].length, this);
        }
        
        excessiveContext = null;
        excessiveStartInputRecordIndex = null;
        excessiveDefinition = null;
        excessiveInputRecordIndex = null;
        excessiveIndex = -1;
        
        excessiveFEC = null;
    }
    public void clearExcessiveContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, excessiveFEC);
        
        if(removeIndex == -1) return;
                
        
        activeInputDescriptor.unregisterClientForRecord(excessiveStartInputRecordIndex[removeIndex], this);
        activeInputDescriptor.unregisterClientForRecord(excessiveInputRecordIndex[removeIndex], 0, excessiveInputRecordIndex[removeIndex].length, this);
            
        int moved = excessiveIndex - removeIndex;
        excessiveIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(excessiveContext, removeIndex+1, excessiveContext, removeIndex, moved);
            System.arraycopy(excessiveStartInputRecordIndex, removeIndex+1, excessiveStartInputRecordIndex, removeIndex, moved);
            System.arraycopy(excessiveDefinition, removeIndex+1, excessiveDefinition, removeIndex, moved);
            System.arraycopy(excessiveInputRecordIndex, removeIndex+1, excessiveInputRecordIndex, removeIndex, moved);
            
            System.arraycopy(excessiveFEC, removeIndex+1, excessiveFEC, removeIndex, moved);
        }
    }    
    void transferExcessiveContent(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, excessiveFEC);
        
        if(removeIndex == -1) return;
                           
        other.excessiveContent(excessiveFEC[removeIndex],
                                excessiveContext[removeIndex],
                                excessiveStartInputRecordIndex[removeIndex],
                                excessiveDefinition[removeIndex],
                                excessiveInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(excessiveStartInputRecordIndex[removeIndex], this);
        activeInputDescriptor.unregisterClientForRecord(excessiveInputRecordIndex[removeIndex], 0, excessiveInputRecordIndex[removeIndex].length, this);
        
        int moved = excessiveIndex - removeIndex;
        excessiveIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(excessiveContext, removeIndex+1, excessiveContext, removeIndex, moved);
            System.arraycopy(excessiveStartInputRecordIndex, removeIndex+1, excessiveStartInputRecordIndex, removeIndex, moved);
            System.arraycopy(excessiveDefinition, removeIndex+1, excessiveDefinition, removeIndex, moved);
            System.arraycopy(excessiveInputRecordIndex, removeIndex+1, excessiveInputRecordIndex, removeIndex, moved);
            
            System.arraycopy(excessiveFEC, removeIndex+1, excessiveFEC, removeIndex, moved);
        }
    }
        
    
    public void missingContent(int functionalEquivalenceCode, 
                                SRule context, 
								int startInputRecordIndex,						 
								SPattern definition, 
								int expected, 
								int found,
								int[] inputRecordIndex){
        
        messageTotalCount++;
		if(missingIndex < 0){
			missingIndex = 0;
			missingContext = new SPattern[initialSize];			
			missingStartInputRecordIndex = new int[initialSize];
			missingDefinition = new SPattern[initialSize];
			missingExpected = new int[initialSize];
			missingFound = new int[initialSize];		
			missingInputRecordIndex = new int[initialSize][];	
			
			missingFEC = new int[initialSize];	
		}else if(++missingIndex == missingContext.length){
		    int newSize = missingIndex+increaseSizeAmount;
		    
		    SPattern[] increasedEC = new SPattern[newSize];
			System.arraycopy(missingContext, 0, increasedEC, 0, missingIndex);
			missingContext = increasedEC;
			
			int[] increasedSIRI = new int[newSize];
			System.arraycopy(missingStartInputRecordIndex, 0, increasedSIRI, 0, missingIndex);
			missingStartInputRecordIndex = increasedSIRI;
			
			SPattern[] increasedED = new SPattern[newSize];
			System.arraycopy(missingDefinition, 0, increasedED, 0, missingIndex);
			missingDefinition = increasedED;
			
			int[] increasedE = new int[newSize];
			System.arraycopy(missingExpected, 0, increasedE, 0, missingIndex);
			missingExpected = increasedE;
			
			int[] increasedF = new int[newSize];
			System.arraycopy(missingFound, 0, increasedF, 0, missingIndex);
			missingFound = increasedF;
			
			int[][] increasedIRI = new int[newSize][];
			System.arraycopy(missingInputRecordIndex, 0, increasedIRI, 0, missingIndex);
			missingInputRecordIndex = increasedIRI;
			
			int[] increasedFEC = new int[newSize];
			System.arraycopy(missingFEC, 0, increasedFEC, 0, missingIndex);
			missingFEC = increasedFEC;
		}	
		
		missingContext[missingIndex] = context;
		missingStartInputRecordIndex[missingIndex] = startInputRecordIndex;
		activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);            
		missingDefinition[missingIndex] = definition;
		missingExpected[missingIndex] = expected;
		missingFound[missingIndex] = found;		
		missingInputRecordIndex[missingIndex] = inputRecordIndex;
		if(inputRecordIndex != null)activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
		
		missingFEC[missingIndex] = functionalEquivalenceCode;	
    }
	public void clearMissingContent(){
        
        messageTotalCount -= (missingIndex+1);
                
        for(int i = 0; i <= missingIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(missingStartInputRecordIndex[i], this);
            if(missingInputRecordIndex[i] != null) activeInputDescriptor.unregisterClientForRecord(missingInputRecordIndex[i], 0, missingInputRecordIndex[i].length, this);
        }
        
        missingContext = null;
		missingStartInputRecordIndex = null;
		missingDefinition = null;
		missingExpected = null;
		missingFound = null;		
		missingInputRecordIndex = null;
        missingIndex = -1;
        
        missingFEC = null;
    }
    public void clearMissingContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, missingFEC);
        
        if(removeIndex == -1) return;
        activeInputDescriptor.unregisterClientForRecord(missingStartInputRecordIndex[removeIndex], this);
        if(missingInputRecordIndex[removeIndex] != null)activeInputDescriptor.unregisterClientForRecord(missingInputRecordIndex[removeIndex], 0, missingInputRecordIndex[removeIndex].length, this);
            
        int moved = missingIndex - removeIndex;
        missingIndex--;
        messageTotalCount--;
        
        if(moved > 0){
            System.arraycopy(missingContext, removeIndex+1, missingContext, removeIndex, moved);
            System.arraycopy(missingStartInputRecordIndex, removeIndex+1, missingStartInputRecordIndex, removeIndex, moved);
            System.arraycopy(missingDefinition, removeIndex+1, missingDefinition, removeIndex, moved);
            System.arraycopy(missingExpected, removeIndex+1, missingExpected, removeIndex, moved);
            System.arraycopy(missingFound, removeIndex+1, missingFound, removeIndex, moved);
            System.arraycopy(missingInputRecordIndex, removeIndex+1, missingInputRecordIndex, removeIndex, moved);
            
            System.arraycopy(missingFEC, removeIndex+1, missingFEC, removeIndex, moved);
        }
    }
    void transferMissingContent(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, missingFEC);
        //if(messageTotalCount == 2) throw new IllegalStateException();
        if(removeIndex == -1) return;
           
        other.missingContent(missingFEC[removeIndex],
                                missingContext[removeIndex],
                                missingStartInputRecordIndex[removeIndex],
                                missingDefinition[removeIndex],
                                missingExpected[removeIndex],
                                missingFound[removeIndex],
                                missingInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(missingStartInputRecordIndex[removeIndex], this);
        if(missingInputRecordIndex[removeIndex] != null) activeInputDescriptor.unregisterClientForRecord(missingInputRecordIndex[removeIndex], 0, missingInputRecordIndex[removeIndex].length, this);
         
        
        int moved = missingIndex - removeIndex;
        missingIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(missingContext, removeIndex+1, missingContext, removeIndex, moved);
            System.arraycopy(missingStartInputRecordIndex, removeIndex+1, missingStartInputRecordIndex, removeIndex, moved);
            System.arraycopy(missingDefinition, removeIndex+1, missingDefinition, removeIndex, moved);
            System.arraycopy(missingExpected, removeIndex+1, missingExpected, removeIndex, moved);
            System.arraycopy(missingFound, removeIndex+1, missingFound, removeIndex, moved);
            System.arraycopy(missingInputRecordIndex, removeIndex+1, missingInputRecordIndex, removeIndex, moved);
            
            System.arraycopy(missingFEC, removeIndex+1, missingFEC, removeIndex, moved);
        }
    }    
    

    public void illegalContent(int functionalEquivalenceCode, 
                            SRule context, 
                            int startInputRecordIndex){
        
        messageTotalCount++;
		if(illegalIndex < 0){
			illegalIndex = 0;
			illegalContext = new SPattern[initialSize];
			illegalStartInputRecordIndex = new int[initialSize];	
			
			illegalFEC = new int[initialSize];
		}else if(++illegalIndex == illegalContext.length){
		    int size = illegalIndex + increaseSizeAmount;
			SPattern[] increasedEC = new SPattern[size];
			System.arraycopy(illegalContext, 0, increasedEC, 0, illegalIndex);
			illegalContext = increasedEC;
			
			int[] increasedII = new int[size];
			System.arraycopy(illegalStartInputRecordIndex, 0, increasedII, 0, illegalIndex);
			illegalStartInputRecordIndex = increasedII;	
			
			int[] increasedFEC = new int[size];
			System.arraycopy(illegalFEC, 0, increasedFEC, 0, illegalIndex);
			illegalFEC = increasedFEC;	
		}
		illegalContext[illegalIndex] = context;
		illegalStartInputRecordIndex[illegalIndex] = startInputRecordIndex;
		activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		
		illegalFEC[illegalIndex] = functionalEquivalenceCode;		
	}
    public void clearIllegalContent(){
        
        messageTotalCount -= (illegalIndex+1);
        for(int i = 0; i <= illegalIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(illegalStartInputRecordIndex[i], this);
        }
        
        illegalIndex = -1;
        illegalContext = null;
        illegalStartInputRecordIndex = null;
        
        illegalFEC = null;
    }
	public void clearIllegalContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, illegalFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(illegalStartInputRecordIndex[removeIndex], this);
        
        int moved = illegalIndex - removeIndex;
        illegalIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(illegalContext, removeIndex+1, illegalContext, removeIndex, moved);
            System.arraycopy(illegalStartInputRecordIndex, removeIndex+1, illegalStartInputRecordIndex, removeIndex, moved);
            
            System.arraycopy(illegalFEC, removeIndex+1, illegalFEC, removeIndex, moved);
        }
    }
    void transferIllegalContent(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, illegalFEC);
        
        if(removeIndex == -1) return;
                
        other.illegalContent(illegalFEC[removeIndex],
                                illegalContext[removeIndex],
                                illegalStartInputRecordIndex[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(illegalStartInputRecordIndex[removeIndex], this);
                
        int moved = illegalIndex - removeIndex;
        illegalIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(illegalContext, removeIndex+1, illegalContext, removeIndex, moved);
            System.arraycopy(illegalStartInputRecordIndex, removeIndex+1, illegalStartInputRecordIndex, removeIndex, moved);
            
            System.arraycopy(illegalFEC, removeIndex+1, illegalFEC, removeIndex, moved);
        }
    }
    
    
	public void unresolvedAmbiguousElementContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions){
        
        messageTotalCount++;
		if(unresolvedAmbiguousElementIndexEE < 0){
			unresolvedAmbiguousElementIndexEE = 0;	
			unresolvedAmbiguousElementInputRecordIndexEE =new int[initialSize];
			unresolvedAmbiguousElementDefinitionEE = new SElement[initialSize][];
			
			unresolvedAmbiguousElementFECEE = new int[initialSize];
		}else if(++unresolvedAmbiguousElementIndexEE == unresolvedAmbiguousElementInputRecordIndexEE.length){
		    int size = unresolvedAmbiguousElementInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAmbiguousElementInputRecordIndexEE, 0, increasedCN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementInputRecordIndexEE = increasedCN;
		    
		    SElement[][] increasedDef = new SElement[size][];
			System.arraycopy(unresolvedAmbiguousElementDefinitionEE, 0, increasedDef, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementDefinitionEE = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(unresolvedAmbiguousElementFECEE, 0, increasedFEC, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementFECEE = increasedFEC;
		}
		
		unresolvedAmbiguousElementInputRecordIndexEE[unresolvedAmbiguousElementIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAmbiguousElementDefinitionEE[unresolvedAmbiguousElementIndexEE] = possibleDefinitions;
		
		unresolvedAmbiguousElementFECEE[unresolvedAmbiguousElementIndexEE] = functionalEquivalenceCode;
	}
	public void clearUnresolvedAmbiguousElementContentError(){
        
        messageTotalCount -= (unresolvedAmbiguousElementIndexEE+1);
        
        for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAmbiguousElementInputRecordIndexEE[i], this);
        }
        
        unresolvedAmbiguousElementInputRecordIndexEE = null;
        unresolvedAmbiguousElementDefinitionEE = null;
        unresolvedAmbiguousElementIndexEE = -1;
        
        unresolvedAmbiguousElementFECEE = null;
    }
    public void clearUnresolvedAmbiguousElementContentError(int messageId){        
        int removeIndex = getRemoveIndex(messageId, unresolvedAmbiguousElementFECEE);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedAmbiguousElementInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedAmbiguousElementIndexEE - removeIndex;
        unresolvedAmbiguousElementIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedAmbiguousElementInputRecordIndexEE, removeIndex+1, unresolvedAmbiguousElementInputRecordIndexEE, removeIndex, moved);
            System.arraycopy(unresolvedAmbiguousElementDefinitionEE, removeIndex+1, unresolvedAmbiguousElementDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedAmbiguousElementFECEE, removeIndex+1, unresolvedAmbiguousElementFECEE, removeIndex, moved);
        }
    }
    void transferUnresolvedAmbiguousElementContentError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unresolvedAmbiguousElementFECEE);
        
        if(removeIndex == -1) return;
                
        other.unresolvedAmbiguousElementContentError(unresolvedAmbiguousElementFECEE[removeIndex],
                                                    unresolvedAmbiguousElementInputRecordIndexEE[removeIndex],
                                                    unresolvedAmbiguousElementDefinitionEE[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedAmbiguousElementInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedAmbiguousElementIndexEE - removeIndex;
        unresolvedAmbiguousElementIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedAmbiguousElementInputRecordIndexEE, removeIndex+1, unresolvedAmbiguousElementInputRecordIndexEE, removeIndex, moved);
            System.arraycopy(unresolvedAmbiguousElementDefinitionEE, removeIndex+1, unresolvedAmbiguousElementDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedAmbiguousElementFECEE, removeIndex+1, unresolvedAmbiguousElementFECEE, removeIndex, moved);
        }
    }
        
    public void unresolvedUnresolvedElementContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions){
        
        messageTotalCount++;
		if(unresolvedUnresolvedElementIndexEE < 0){
			unresolvedUnresolvedElementIndexEE = 0;	
			unresolvedUnresolvedElementInputRecordIndexEE =new int[initialSize];
			unresolvedUnresolvedElementDefinitionEE = new SElement[initialSize][];
			
			unresolvedUnresolvedElementFECEE = new int[initialSize];
		}else if(++unresolvedUnresolvedElementIndexEE == unresolvedUnresolvedElementInputRecordIndexEE.length){
		    int size = unresolvedUnresolvedElementInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedUnresolvedElementInputRecordIndexEE, 0, increasedCN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementInputRecordIndexEE = increasedCN;
		    
		    SElement[][] increasedDef = new SElement[size][];
			System.arraycopy(unresolvedUnresolvedElementDefinitionEE, 0, increasedDef, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementDefinitionEE = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(unresolvedUnresolvedElementFECEE, 0, increasedFEC, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementFECEE = increasedFEC;
		}
		
		unresolvedUnresolvedElementInputRecordIndexEE[unresolvedUnresolvedElementIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedUnresolvedElementDefinitionEE[unresolvedUnresolvedElementIndexEE] = possibleDefinitions;

        unresolvedUnresolvedElementFECEE[unresolvedUnresolvedElementIndexEE] = functionalEquivalenceCode;		
	}
	public void clearUnresolvedUnresolvedElementContentError(){
        
        messageTotalCount -= (unresolvedUnresolvedElementIndexEE+1);
        
        for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedUnresolvedElementInputRecordIndexEE[i], this);
        }
        
        unresolvedUnresolvedElementInputRecordIndexEE = null;
        unresolvedUnresolvedElementDefinitionEE = null;
        unresolvedUnresolvedElementIndexEE = -1; 
        
        unresolvedUnresolvedElementFECEE = null;
    }
    public void clearUnresolvedUnresolvedElementContentError(int messageId){        
        int removeIndex = getRemoveIndex(messageId, unresolvedUnresolvedElementFECEE);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedUnresolvedElementInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedUnresolvedElementIndexEE - removeIndex;
        unresolvedUnresolvedElementIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedUnresolvedElementInputRecordIndexEE, removeIndex+1, unresolvedUnresolvedElementInputRecordIndexEE, removeIndex, moved);            
            System.arraycopy(unresolvedUnresolvedElementDefinitionEE, removeIndex+1, unresolvedUnresolvedElementDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedUnresolvedElementFECEE, removeIndex+1, unresolvedUnresolvedElementFECEE, removeIndex, moved);
        }
    }
    void transferUnresolvedUnresolvedElementContentError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unresolvedUnresolvedElementFECEE);
        
        if(removeIndex == -1) return;
                
        other.unresolvedUnresolvedElementContentError(unresolvedUnresolvedElementFECEE[removeIndex],
                                                        unresolvedUnresolvedElementInputRecordIndexEE[removeIndex],
                                                        unresolvedUnresolvedElementDefinitionEE[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedUnresolvedElementInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedUnresolvedElementIndexEE - removeIndex;
        unresolvedUnresolvedElementIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedUnresolvedElementInputRecordIndexEE, removeIndex+1, unresolvedUnresolvedElementInputRecordIndexEE, removeIndex, moved);            
            System.arraycopy(unresolvedUnresolvedElementDefinitionEE, removeIndex+1, unresolvedUnresolvedElementDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedUnresolvedElementFECEE, removeIndex+1, unresolvedUnresolvedElementFECEE, removeIndex, moved);
        }
    }
    
    
	public void unresolvedAttributeContentError(int functionalEquivalenceCode, 
                                    int inputRecordIndex, 
									SAttribute[] possibleDefinitions){
        
		messageTotalCount++;
		if(unresolvedAttributeIndexEE < 0){
			unresolvedAttributeIndexEE = 0;	
			unresolvedAttributeInputRecordIndexEE =new int[initialSize];
			unresolvedAttributeDefinitionEE = new SAttribute[initialSize][];
			
			unresolvedAttributeFECEE = new int[initialSize];
		}else if(++unresolvedAttributeIndexEE == unresolvedAttributeInputRecordIndexEE.length){
		    int size = unresolvedAttributeInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAttributeInputRecordIndexEE, 0, increasedCN, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeInputRecordIndexEE = increasedCN;
		    
		    SAttribute[][] increasedDef = new SAttribute[size][];
			System.arraycopy(unresolvedAttributeDefinitionEE, 0, increasedDef, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeDefinitionEE = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(unresolvedAttributeFECEE, 0, increasedFEC, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeFECEE = increasedFEC;
		}
		
		unresolvedAttributeInputRecordIndexEE[unresolvedAttributeIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAttributeDefinitionEE[unresolvedAttributeIndexEE] = possibleDefinitions;
		
		unresolvedAttributeFECEE[unresolvedAttributeIndexEE] = functionalEquivalenceCode;
	}
    public void clearUnresolvedAttributeContentError(){
        
        messageTotalCount -= (unresolvedAttributeIndexEE+1);
         
        for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAttributeInputRecordIndexEE[i], this);
        }
        
        unresolvedAttributeInputRecordIndexEE = null;
        unresolvedAttributeDefinitionEE = null;
        unresolvedAttributeIndexEE = -1;
        
        unresolvedAttributeFECEE = null;
    }
	public void clearUnresolvedAttributeContentError(int messageId){
        int removeIndex = getRemoveIndex(messageId, unresolvedAttributeFECEE);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedAttributeInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedAttributeIndexEE - removeIndex;
        unresolvedAttributeIndexEE--;
        messageTotalCount--;
        if(moved > 0){            
            System.arraycopy(unresolvedAttributeInputRecordIndexEE, removeIndex+1, unresolvedAttributeInputRecordIndexEE, removeIndex, moved);
            System.arraycopy(unresolvedAttributeDefinitionEE, removeIndex+1, unresolvedAttributeDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedAttributeFECEE, removeIndex+1, unresolvedAttributeFECEE, removeIndex, moved);
        }
    }
	void transferUnresolvedAttributeContentError(int messageId, ConflictMessageHandler other){
	    int removeIndex = getRemoveIndex(messageId, unresolvedAttributeFECEE);
	    
        if(removeIndex == -1) return;
        	    
	    other.unresolvedAttributeContentError(unresolvedAttributeFECEE[removeIndex],
	                                            unresolvedAttributeInputRecordIndexEE[removeIndex],
	                                            unresolvedAttributeDefinitionEE[removeIndex]);
	    
	    activeInputDescriptor.unregisterClientForRecord(unresolvedAttributeInputRecordIndexEE[removeIndex], this);
	    
        int moved = unresolvedAttributeIndexEE - removeIndex;
        unresolvedAttributeIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedAttributeInputRecordIndexEE, removeIndex+1, unresolvedAttributeInputRecordIndexEE, removeIndex, moved);
            System.arraycopy(unresolvedAttributeDefinitionEE, removeIndex+1, unresolvedAttributeDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedAttributeFECEE, removeIndex+1, unresolvedAttributeFECEE, removeIndex, moved);
        }
	}
    
    
	public void ambiguousUnresolvedElementContentWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions){
        
        messageTotalCount++;
		if(ambiguousUnresolvedElementIndexWW < 0){
			ambiguousUnresolvedElementIndexWW = 0;	
			ambiguousUnresolvedElementInputRecordIndexWW =new int[initialSize];
			ambiguousUnresolvedElementDefinitionWW = new SElement[initialSize][];
			
			ambiguousUnresolvedElementFECWW = new int[initialSize];
		}else if(++ambiguousUnresolvedElementIndexWW == ambiguousUnresolvedElementInputRecordIndexWW.length){
		    int size = ambiguousUnresolvedElementInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousUnresolvedElementInputRecordIndexWW, 0, increasedCN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementInputRecordIndexWW = increasedCN;
		    
		    SElement[][] increasedDef = new SElement[size][];
			System.arraycopy(ambiguousUnresolvedElementDefinitionWW, 0, increasedDef, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementDefinitionWW = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(ambiguousUnresolvedElementFECWW, 0, increasedFEC, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementFECWW = increasedFEC;
		}
		
		ambiguousUnresolvedElementInputRecordIndexWW[ambiguousUnresolvedElementIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousUnresolvedElementDefinitionWW[ambiguousUnresolvedElementIndexWW] = possibleDefinitions;
		
		ambiguousUnresolvedElementFECWW[ambiguousUnresolvedElementIndexWW] = functionalEquivalenceCode;
	}
	public void clearAmbiguousUnresolvedElementContentWarning(){
        
        messageTotalCount -= (ambiguousUnresolvedElementIndexWW+1);
        
        for(int i = 0; i <= ambiguousUnresolvedElementIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousUnresolvedElementInputRecordIndexWW[i], this);
        }
        
        ambiguousUnresolvedElementInputRecordIndexWW = null;
        ambiguousUnresolvedElementDefinitionWW = null;
        ambiguousUnresolvedElementIndexWW = -1;  
        
        ambiguousUnresolvedElementFECWW = null;
    }
    public void clearAmbiguousUnresolvedElementContentWarning(int messageId){        
        int removeIndex = getRemoveIndex(messageId, ambiguousUnresolvedElementFECWW);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousUnresolvedElementInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousUnresolvedElementIndexWW - removeIndex;
        ambiguousUnresolvedElementIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousUnresolvedElementInputRecordIndexWW, removeIndex+1, ambiguousUnresolvedElementInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousUnresolvedElementDefinitionWW, removeIndex+1, ambiguousUnresolvedElementDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousUnresolvedElementFECWW, removeIndex+1, ambiguousUnresolvedElementFECWW, removeIndex, moved);
        }
    }
    public void transferAmbiguousUnresolvedElementContentWarning(int messageId, ConflictMessageHandler other){        
        int removeIndex = getRemoveIndex(messageId, ambiguousUnresolvedElementFECWW);
        
        if(removeIndex == -1) return;
                
        other.ambiguousUnresolvedElementContentWarning(ambiguousUnresolvedElementFECWW[removeIndex],
                                                    ambiguousUnresolvedElementInputRecordIndexWW[removeIndex],
                                                    ambiguousUnresolvedElementDefinitionWW[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousUnresolvedElementInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousUnresolvedElementIndexWW - removeIndex;
        ambiguousUnresolvedElementIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousUnresolvedElementInputRecordIndexWW, removeIndex+1, ambiguousUnresolvedElementInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousUnresolvedElementDefinitionWW, removeIndex+1, ambiguousUnresolvedElementDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousUnresolvedElementFECWW, removeIndex+1, ambiguousUnresolvedElementFECWW, removeIndex, moved);
        }
    }
    
    
    public void ambiguousAmbiguousElementContentWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SElement[] possibleDefinitions){
        
        messageTotalCount++;
		if(ambiguousAmbiguousElementIndexWW < 0){
			ambiguousAmbiguousElementIndexWW = 0;	
			ambiguousAmbiguousElementInputRecordIndexWW =new int[initialSize];
			ambiguousAmbiguousElementDefinitionWW = new SElement[initialSize][];
			
			ambiguousAmbiguousElementFECWW = new int[initialSize];
		}else if(++ambiguousAmbiguousElementIndexWW == ambiguousAmbiguousElementInputRecordIndexWW.length){
		    int size = ambiguousAmbiguousElementInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAmbiguousElementInputRecordIndexWW, 0, increasedCN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementInputRecordIndexWW = increasedCN;
		    
		    SElement[][] increasedDef = new SElement[size][];
			System.arraycopy(ambiguousAmbiguousElementDefinitionWW, 0, increasedDef, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementDefinitionWW = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(ambiguousAmbiguousElementFECWW, 0, increasedFEC, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementFECWW = increasedFEC;
		}
		
		ambiguousAmbiguousElementInputRecordIndexWW[ambiguousAmbiguousElementIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAmbiguousElementDefinitionWW[ambiguousAmbiguousElementIndexWW] = possibleDefinitions;
		
		ambiguousAmbiguousElementFECWW[ambiguousAmbiguousElementIndexWW] = functionalEquivalenceCode;
	}
	public void clearAmbiguousAmbiguousElementContentWarning(){
        
        messageTotalCount -= (ambiguousAmbiguousElementIndexWW+1);
        
        for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAmbiguousElementInputRecordIndexWW[i], this);
        }
        
        ambiguousAmbiguousElementInputRecordIndexWW = null;
        ambiguousAmbiguousElementDefinitionWW = null;
        ambiguousAmbiguousElementIndexWW = -1;
        
        ambiguousAmbiguousElementFECWW = null;
    }
    public void clearAmbiguousAmbiguousElementContentWarning(int messageId){        
        int removeIndex = getRemoveIndex(messageId, ambiguousAmbiguousElementFECWW);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousAmbiguousElementInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousAmbiguousElementIndexWW - removeIndex;
        ambiguousAmbiguousElementIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAmbiguousElementInputRecordIndexWW, removeIndex+1, ambiguousAmbiguousElementInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousAmbiguousElementDefinitionWW, removeIndex+1, ambiguousAmbiguousElementDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousAmbiguousElementFECWW, removeIndex+1, ambiguousAmbiguousElementFECWW, removeIndex, moved);
        }
    }
    public void transferAmbiguousAmbiguousElementContentWarning(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, ambiguousAmbiguousElementFECWW);
        
        if(removeIndex == -1) return;
                
        other.ambiguousAmbiguousElementContentWarning(ambiguousAmbiguousElementFECWW[removeIndex],
                                                    ambiguousAmbiguousElementInputRecordIndexWW[removeIndex],
                                                    ambiguousAmbiguousElementDefinitionWW[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousAmbiguousElementInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousAmbiguousElementIndexWW - removeIndex;
        ambiguousAmbiguousElementIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAmbiguousElementInputRecordIndexWW, removeIndex+1, ambiguousAmbiguousElementInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousAmbiguousElementDefinitionWW, removeIndex+1, ambiguousAmbiguousElementDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousAmbiguousElementFECWW, removeIndex+1, ambiguousAmbiguousElementFECWW, removeIndex, moved);
        }
    }
    
	public void ambiguousAttributeContentWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SAttribute[] possibleDefinitions){
        
        messageTotalCount++;
		if(ambiguousAttributeIndexWW < 0){
			ambiguousAttributeIndexWW = 0;	
			ambiguousAttributeInputRecordIndexWW =new int[initialSize];
			ambiguousAttributeDefinitionWW = new SAttribute[initialSize][];
			
			ambiguousAttributeFECWW = new int[initialSize];
		}else if(++ambiguousAttributeIndexWW == ambiguousAttributeInputRecordIndexWW.length){
		    int size = ambiguousAttributeInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAttributeInputRecordIndexWW, 0, increasedCN, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeInputRecordIndexWW = increasedCN;
		    
		    SAttribute[][] increasedDef = new SAttribute[size][];
			System.arraycopy(ambiguousAttributeDefinitionWW, 0, increasedDef, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeDefinitionWW = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(ambiguousAttributeFECWW, 0, increasedFEC, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeFECWW = increasedFEC;
		}
		
		ambiguousAttributeInputRecordIndexWW[ambiguousAttributeIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAttributeDefinitionWW[ambiguousAttributeIndexWW] = possibleDefinitions;
		
		ambiguousAttributeFECWW[ambiguousAttributeIndexWW] = functionalEquivalenceCode;		
	}
	public void clearAmbiguousAttributeContentWarning(){
        
        messageTotalCount -= (ambiguousAttributeIndexWW+1);
        
        for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAttributeInputRecordIndexWW[i], this);
        }
        
        ambiguousAttributeInputRecordIndexWW = null;
        ambiguousAttributeDefinitionWW = null;
        ambiguousAttributeIndexWW = -1;
        
        ambiguousAttributeFECWW = null;
    }
	public void clearAmbiguousAttributeContentWarning(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousAttributeFECWW);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousAttributeInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousAttributeIndexWW - removeIndex;
        ambiguousAttributeIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAttributeInputRecordIndexWW, removeIndex+1, ambiguousAttributeInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousAttributeDefinitionWW, removeIndex+1, ambiguousAttributeDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousAttributeFECWW, removeIndex+1, ambiguousAttributeFECWW, removeIndex, moved);
        }
    }
    public void transferAmbiguousAttributeContentWarning(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, ambiguousAttributeFECWW);
        
        if(removeIndex == -1) return;
                
        other.ambiguousAttributeContentWarning(ambiguousAttributeFECWW[removeIndex],
                                            ambiguousAttributeInputRecordIndexWW[removeIndex],
                                            ambiguousAttributeDefinitionWW[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousAttributeInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousAttributeIndexWW - removeIndex;
        ambiguousAttributeIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAttributeInputRecordIndexWW, removeIndex+1, ambiguousAttributeInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousAttributeDefinitionWW, removeIndex+1, ambiguousAttributeDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousAttributeFECWW, removeIndex+1, ambiguousAttributeFECWW, removeIndex, moved);
        }
    }
    
    
	public void ambiguousCharacterContentWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SPattern[] possibleDefinitions){
        
        messageTotalCount++;
		if(ambiguousCharsIndexWW < 0){
			ambiguousCharsIndexWW = 0;	
			ambiguousCharsInputRecordIndexWW =new int[initialSize];
			ambiguousCharsDefinitionWW = new SPattern[initialSize][];
			
			ambiguousCharsFECWW = new int[initialSize];
		}else if(++ambiguousCharsIndexWW == ambiguousCharsInputRecordIndexWW.length){
		    int size = ambiguousCharsInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousCharsInputRecordIndexWW, 0, increasedCN, 0, ambiguousCharsIndexWW);
			ambiguousCharsInputRecordIndexWW = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(ambiguousCharsDefinitionWW, 0, increasedDef, 0, ambiguousCharsIndexWW);
			ambiguousCharsDefinitionWW = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(ambiguousCharsFECWW, 0, increasedFEC, 0, ambiguousCharsIndexWW);
			ambiguousCharsFECWW = increasedFEC;
		}
		
		ambiguousCharsInputRecordIndexWW[ambiguousCharsIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousCharsDefinitionWW[ambiguousCharsIndexWW] = possibleDefinitions;   
		
		ambiguousCharsFECWW[ambiguousCharsIndexWW] = functionalEquivalenceCode;
	}
	public void clearAmbiguousCharacterContentWarning(){
        
        messageTotalCount -= (ambiguousCharsIndexWW+1);
        
        for(int i = 0; i <= ambiguousCharsIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousCharsInputRecordIndexWW[i], this);
        }
        
        ambiguousCharsInputRecordIndexWW = null;
        ambiguousCharsDefinitionWW = null;
        ambiguousCharsIndexWW = -1;
        
        ambiguousCharsFECWW = null;
    }
	public void clearAmbiguousCharacterContentWarning(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousCharsFECWW);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousCharsInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousCharsIndexWW - removeIndex;
        ambiguousCharsIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousCharsInputRecordIndexWW, removeIndex+1, ambiguousCharsInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousCharsDefinitionWW, removeIndex+1, ambiguousCharsDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousCharsFECWW, removeIndex+1, ambiguousCharsFECWW, removeIndex, moved);
        }
    }
    public void transferAmbiguousCharacterContentWarning(int messageId, ConflictMessageHandler other){    
        int removeIndex = getRemoveIndex(messageId, ambiguousCharsFECWW);
        
        if(removeIndex == -1) return;
                
        other.ambiguousCharacterContentWarning(ambiguousCharsFECWW[removeIndex],
                                        ambiguousCharsInputRecordIndexWW[removeIndex],
                                        ambiguousCharsDefinitionWW[removeIndex]);

        activeInputDescriptor.unregisterClientForRecord(ambiguousCharsInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousCharsIndexWW - removeIndex;
        ambiguousCharsIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousCharsInputRecordIndexWW, removeIndex+1, ambiguousCharsInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousCharsDefinitionWW, removeIndex+1, ambiguousCharsDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousCharsFECWW, removeIndex+1, ambiguousCharsFECWW, removeIndex, moved);
        }
    }
    
    
    public void ambiguousAttributeValueWarning(int functionalEquivalenceCode,
                                    int inputRecordIndex, 
									SPattern[] possibleDefinitions){
        
        messageTotalCount++;
		if(ambiguousAVIndexWW < 0){
			ambiguousAVIndexWW = 0;	
			ambiguousAVInputRecordIndexWW =new int[initialSize];
			ambiguousAVDefinitionWW = new SPattern[initialSize][];
			
			ambiguousAVFECWW = new int[initialSize];
		}else if(++ambiguousAVIndexWW == ambiguousAVInputRecordIndexWW.length){
		    int size = ambiguousAVInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAVInputRecordIndexWW, 0, increasedCN, 0, ambiguousAVIndexWW);
			ambiguousAVInputRecordIndexWW = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(ambiguousAVDefinitionWW, 0, increasedDef, 0, ambiguousAVIndexWW);
			ambiguousAVDefinitionWW = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(ambiguousAVFECWW, 0, increasedFEC, 0, ambiguousAVIndexWW);
			ambiguousAVFECWW = increasedFEC;
		}
		
		ambiguousAVInputRecordIndexWW[ambiguousAVIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAVDefinitionWW[ambiguousAVIndexWW] = possibleDefinitions;
		
		ambiguousAVFECWW[ambiguousAVIndexWW] = functionalEquivalenceCode;
	}
	public void clearAmbiguousAttributeValueWarning(){
        
        messageTotalCount -= (ambiguousAVIndexWW+1);
        
        for(int i = 0; i <= ambiguousAVIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAVInputRecordIndexWW[i], this);
        }
        
        ambiguousAVInputRecordIndexWW = null;
        ambiguousAVDefinitionWW = null;
        ambiguousAVIndexWW = -1;
        
        ambiguousAVFECWW = null;
    }
	public void clearAmbiguousAttributeValueWarning(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousAVFECWW);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousAVInputRecordIndexWW[removeIndex], this);
        
        int moved = ambiguousAVIndexWW - removeIndex;
        ambiguousAVIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAVInputRecordIndexWW, removeIndex+1, ambiguousAVInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousAVDefinitionWW, removeIndex+1, ambiguousAVDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousAVFECWW, removeIndex+1, ambiguousAVFECWW, removeIndex, moved);
        }
    }
	public void transferAmbiguousAttributeValueWarning(int messageId, ConflictMessageHandler other){
	    int removeIndex = getRemoveIndex(messageId, ambiguousAVFECWW);
	    
        if(removeIndex == -1) return;
        	    
	    other.ambiguousAttributeValueWarning(ambiguousAVFECWW[removeIndex],
	                                        ambiguousAVInputRecordIndexWW[removeIndex],
	                                        ambiguousAVDefinitionWW[removeIndex]);

        activeInputDescriptor.unregisterClientForRecord(ambiguousAVInputRecordIndexWW[removeIndex], this);	    
	    
        int moved = ambiguousAVIndexWW - removeIndex;
        ambiguousAVIndexWW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAVInputRecordIndexWW, removeIndex+1, ambiguousAVInputRecordIndexWW, removeIndex, moved);
            System.arraycopy(ambiguousAVDefinitionWW, removeIndex+1, ambiguousAVDefinitionWW, removeIndex, moved);
            
            System.arraycopy(ambiguousAVFECWW, removeIndex+1, ambiguousAVFECWW, removeIndex, moved);
        }
	}
		
    
    // {15}
	public void characterContentDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        
        messageTotalCount++;
		if(datatypeCharsIndex < 0){
			datatypeCharsIndex = 0;	
			datatypeCharsInputRecordIndex =new int[initialSize];
			datatypeCharsDefinition = new SPattern[initialSize];
			datatypeCharsErrorMessage = new String[initialSize];
			
			datatypeCharsFEC = new int[initialSize];
		}else if(++datatypeCharsIndex == datatypeCharsInputRecordIndex.length){
		    int size = datatypeCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(datatypeCharsInputRecordIndex, 0, increasedCN, 0, datatypeCharsIndex);
			datatypeCharsInputRecordIndex = increasedCN;
		    
		    SPattern[] increasedDef = new SPattern[size];
			System.arraycopy(datatypeCharsDefinition, 0, increasedDef, 0, datatypeCharsIndex);
			datatypeCharsDefinition = increasedDef;
			
			String[] increasedEM = new String[size];
			System.arraycopy(datatypeCharsErrorMessage, 0, increasedEM, 0, datatypeCharsIndex);
			datatypeCharsErrorMessage = increasedEM;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(datatypeCharsFEC, 0, increasedFEC, 0, datatypeCharsIndex);
			datatypeCharsFEC = increasedFEC;
		}
		
		datatypeCharsInputRecordIndex[datatypeCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		datatypeCharsDefinition[datatypeCharsIndex] = charsDefinition;
		datatypeCharsErrorMessage[datatypeCharsIndex] = datatypeErrorMessage;
		
		datatypeCharsFEC[datatypeCharsIndex] = functionalEquivalenceCode;
	}
	public void clearCharacterContentDatatypeError(){
        
        messageTotalCount -= (datatypeCharsIndex+1);
        
        for(int i = 0; i <= datatypeCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeCharsInputRecordIndex[i], this);
        }
        
        datatypeCharsInputRecordIndex = null;
        datatypeCharsDefinition = null;
        datatypeCharsErrorMessage = null;
        datatypeCharsIndex = -1;
        
        datatypeCharsFEC = null;
    }
    public void clearCharacterContentDatatypeError(int messageId){
        int removeIndex = getRemoveIndex(messageId, datatypeCharsFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(datatypeCharsInputRecordIndex[removeIndex], this);
        
        int moved = datatypeCharsIndex - removeIndex;
        datatypeCharsIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(datatypeCharsInputRecordIndex, removeIndex+1, datatypeCharsInputRecordIndex, removeIndex, moved);
            System.arraycopy(datatypeCharsDefinition, removeIndex+1, datatypeCharsDefinition, removeIndex, moved);
            System.arraycopy(datatypeCharsErrorMessage, removeIndex+1, datatypeCharsErrorMessage, removeIndex, moved);
            
            System.arraycopy(datatypeCharsFEC, removeIndex+1, datatypeCharsFEC, removeIndex, moved);
        }
    }
    void transferCharacterContentDatatypeError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, datatypeCharsFEC);
        
        if(removeIndex == -1) return;
                
        other.characterContentDatatypeError(datatypeCharsFEC[removeIndex],
                                            datatypeCharsInputRecordIndex[removeIndex],
                                            datatypeCharsDefinition[removeIndex],
                                            datatypeCharsErrorMessage[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(datatypeCharsInputRecordIndex[removeIndex], this);
        
        int moved = datatypeCharsIndex - removeIndex;
        datatypeCharsIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(datatypeCharsInputRecordIndex, removeIndex+1, datatypeCharsInputRecordIndex, removeIndex, moved);
            System.arraycopy(datatypeCharsDefinition, removeIndex+1, datatypeCharsDefinition, removeIndex, moved);
            System.arraycopy(datatypeCharsErrorMessage, removeIndex+1, datatypeCharsErrorMessage, removeIndex, moved);
            
            System.arraycopy(datatypeCharsFEC, removeIndex+1, datatypeCharsFEC, removeIndex, moved);
        }
    }    
    
    //{16}
	public void attributeValueDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        
        messageTotalCount++;
		if(datatypeAVIndex < 0){
			datatypeAVIndex = 0;	
			datatypeAVInputRecordIndex =new int[initialSize];
			datatypeAVDefinition = new SPattern[initialSize];
			datatypeAVErrorMessage = new String[initialSize];
			
			datatypeAVFEC = new int[initialSize];
		}else if(++datatypeAVIndex == datatypeAVInputRecordIndex.length){
		    int size = datatypeAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(datatypeAVInputRecordIndex, 0, increasedCN, 0, datatypeAVIndex);
			datatypeAVInputRecordIndex = increasedCN;
		    
		    SPattern[] increasedDef = new SPattern[size];
			System.arraycopy(datatypeAVDefinition, 0, increasedDef, 0, datatypeAVIndex);
			datatypeAVDefinition = increasedDef;
			
			String[] increasedEM = new String[size];
			System.arraycopy(datatypeAVErrorMessage, 0, increasedEM, 0, datatypeAVIndex);
			datatypeAVErrorMessage = increasedEM;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(datatypeAVFEC, 0, increasedFEC, 0, datatypeAVIndex);
			datatypeAVFEC = increasedFEC;
		}
		
		datatypeAVInputRecordIndex[datatypeAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		datatypeAVDefinition[datatypeAVIndex] = charsDefinition;
		datatypeAVErrorMessage[datatypeAVIndex] = datatypeErrorMessage;
		
		datatypeAVFEC[datatypeAVIndex] = functionalEquivalenceCode;
	}
	public void clearAttributeValueDatatypeError(){
        
        messageTotalCount -= (datatypeAVIndex+1);
        
        for(int i = 0; i <= datatypeAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeAVInputRecordIndex[i], this);
        }
        
        datatypeAVInputRecordIndex = null;
        datatypeAVDefinition = null;
        datatypeAVErrorMessage = null;
        datatypeAVIndex = -1;
        
        datatypeAVFEC = null;
    }
    public void clearAttributeValueDatatypeError(int messageId){
        int removeIndex = getRemoveIndex(messageId, datatypeAVFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(datatypeAVInputRecordIndex[removeIndex], this);
        
        int moved = datatypeAVIndex - removeIndex;
        datatypeAVIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(datatypeAVInputRecordIndex, removeIndex+1, datatypeAVInputRecordIndex, removeIndex, moved);
            System.arraycopy(datatypeAVDefinition, removeIndex+1, datatypeAVDefinition, removeIndex, moved);
            System.arraycopy(datatypeAVErrorMessage, removeIndex+1, datatypeAVErrorMessage, removeIndex, moved);
            
            System.arraycopy(datatypeAVFEC, removeIndex+1, datatypeAVFEC, removeIndex, moved);
        }
    }
    void transferAttributeValueDatatypeError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, datatypeAVFEC);
        
        if(removeIndex == -1) return;
                
        other.attributeValueDatatypeError(datatypeAVFEC[removeIndex],
                                            datatypeAVInputRecordIndex[removeIndex],
                                            datatypeAVDefinition[removeIndex],
                                            datatypeAVErrorMessage[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(datatypeAVInputRecordIndex[removeIndex], this);
        
        int moved = datatypeAVIndex - removeIndex;
        datatypeAVIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(datatypeAVInputRecordIndex, removeIndex+1, datatypeAVInputRecordIndex, removeIndex, moved);
            System.arraycopy(datatypeAVDefinition, removeIndex+1, datatypeAVDefinition, removeIndex, moved);
            System.arraycopy(datatypeAVErrorMessage, removeIndex+1, datatypeAVErrorMessage, removeIndex, moved);
            
            System.arraycopy(datatypeAVFEC, removeIndex+1, datatypeAVFEC, removeIndex, moved);
        }
    }
        
        
	public void characterContentValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition){
        
        messageTotalCount++;
		if(valueCharsIndex < 0){
			valueCharsIndex = 0;	
			valueCharsInputRecordIndex =new int[initialSize];
			valueCharsDefinition = new SValue[initialSize];
			
			valueCharsFEC = new int[initialSize];
		}else if(++valueCharsIndex == valueCharsInputRecordIndex.length){
		    int size = valueCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueCharsInputRecordIndex, 0, increasedCN, 0, valueCharsIndex);
			valueCharsInputRecordIndex = increasedCN;
		    
		    SValue[] increasedDef = new SValue[size];
			System.arraycopy(valueCharsDefinition, 0, increasedDef, 0, valueCharsIndex);
			valueCharsDefinition = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(valueCharsFEC, 0, increasedFEC, 0, valueCharsIndex);
			valueCharsFEC = increasedFEC;
		}
		
		valueCharsInputRecordIndex[valueCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueCharsDefinition[valueCharsIndex] = charsDefinition;
		
		valueCharsFEC[valueCharsIndex] = functionalEquivalenceCode;
	}
    public void clearCharacterContentValueError(){
        
        messageTotalCount -= (valueCharsIndex+1);
        
        for(int i = 0; i <= valueCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueCharsInputRecordIndex[i], this);
        }
        
        valueCharsInputRecordIndex = null;
        valueCharsDefinition = null;
        valueCharsIndex = -1;
        
        valueCharsFEC = null;
    }
    public void clearCharacterContentValueError(int messageId){
        int removeIndex = getRemoveIndex(messageId, valueCharsFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(valueCharsInputRecordIndex[removeIndex], this);
        
        int moved = valueCharsIndex - removeIndex;
        valueCharsIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(valueCharsInputRecordIndex, removeIndex+1, valueCharsInputRecordIndex, removeIndex, moved);
            System.arraycopy(valueCharsDefinition, removeIndex+1, valueCharsDefinition, removeIndex, moved);
            
            System.arraycopy(valueCharsFEC, removeIndex+1, valueCharsFEC, removeIndex, moved);
        }
    }
    void transferCharacterContentValueError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, valueCharsFEC);
        
        if(removeIndex == -1) return;
                
        other.characterContentValueError(valueCharsFEC[removeIndex],
                                            valueCharsInputRecordIndex[removeIndex],
                                            valueCharsDefinition[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(valueCharsInputRecordIndex[removeIndex], this);
        
        int moved = valueCharsIndex - removeIndex;
        valueCharsIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(valueCharsInputRecordIndex, removeIndex+1, valueCharsInputRecordIndex, removeIndex, moved);
            System.arraycopy(valueCharsDefinition, removeIndex+1, valueCharsDefinition, removeIndex, moved);
            
            System.arraycopy(valueCharsFEC, removeIndex+1, valueCharsFEC, removeIndex, moved);
        }
    }   
    
	public void attributeValueValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition){
        
        messageTotalCount++;
		if(valueAVIndex < 0){
			valueAVIndex = 0;	
			valueAVInputRecordIndex =new int[initialSize];
			valueAVDefinition = new SValue[initialSize];
			
			valueAVFEC = new int[initialSize];
		}else if(++valueAVIndex == valueAVInputRecordIndex.length){
		    int size = valueAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueAVInputRecordIndex, 0, increasedCN, 0, valueAVIndex);
			valueAVInputRecordIndex = increasedCN;
		    
		    SValue[] increasedDef = new SValue[size];
			System.arraycopy(valueAVDefinition, 0, increasedDef, 0, valueAVIndex);
			valueAVDefinition = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(valueAVFEC, 0, increasedFEC, 0, valueAVIndex);
			valueAVFEC = increasedFEC;
		}
		
		valueAVInputRecordIndex[valueAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueAVDefinition[valueAVIndex] = charsDefinition;
		
		valueAVFEC[valueAVIndex] = functionalEquivalenceCode;
        
	}
	public void clearAttributeValueValueError(){
        
        messageTotalCount -= (valueAVIndex+1);
        
        for(int i = 0; i <= valueAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueAVInputRecordIndex[i], this);
        }
        
        valueAVInputRecordIndex = null;
        valueAVDefinition = null;
        valueAVIndex = -1;
        
        valueAVFEC = null;
    }    
    public void clearAttributeValueValueError(int messageId){
        int removeIndex = getRemoveIndex(messageId, valueAVFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(valueAVInputRecordIndex[removeIndex], this);
        
        int moved = valueAVIndex - removeIndex;
        valueAVIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(valueAVInputRecordIndex, removeIndex+1, valueAVInputRecordIndex, removeIndex, moved);
            System.arraycopy(valueAVDefinition, removeIndex+1, valueAVDefinition, removeIndex, moved);
            
            System.arraycopy(valueAVFEC, removeIndex+1, valueAVFEC, removeIndex, moved);
        }
    }
    void transferAttributeValueValueError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, valueAVFEC);
        
        if(removeIndex == -1) return;
                
        other.attributeValueValueError(valueAVFEC[removeIndex],
                                            valueAVInputRecordIndex[removeIndex],
                                            valueAVDefinition[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(valueAVInputRecordIndex[removeIndex], this);
        
        int moved = valueAVIndex - removeIndex;
        valueAVIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(valueAVInputRecordIndex, removeIndex+1, valueAVInputRecordIndex, removeIndex, moved);
            System.arraycopy(valueAVDefinition, removeIndex+1, valueAVDefinition, removeIndex, moved);
            
            System.arraycopy(valueAVFEC, removeIndex+1, valueAVFEC, removeIndex, moved);
        }
    }    
    
	public void characterContentExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition){
        
        messageTotalCount++;
		if(exceptCharsIndex < 0){
			exceptCharsIndex = 0;	
			exceptCharsInputRecordIndex =new int[initialSize];
			exceptCharsDefinition = new SData[initialSize];
			
			exceptCharsFEC = new int[initialSize];
		}else if(++exceptCharsIndex == exceptCharsInputRecordIndex.length){
		    int size = exceptCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptCharsInputRecordIndex, 0, increasedCN, 0, exceptCharsIndex);
			exceptCharsInputRecordIndex = increasedCN;
		    
		    SData[] increasedDef = new SData[size];
			System.arraycopy(exceptCharsDefinition, 0, increasedDef, 0, exceptCharsIndex);
			exceptCharsDefinition = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(exceptCharsFEC, 0, increasedFEC, 0, exceptCharsIndex);
			exceptCharsFEC = increasedFEC;
		}
		
		exceptCharsInputRecordIndex[exceptCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptCharsDefinition[exceptCharsIndex] = charsDefinition;
		
		exceptCharsFEC[exceptCharsIndex] = functionalEquivalenceCode;
	}
    public void clearCharacterContentExceptedError(){
        
        messageTotalCount -= (exceptCharsIndex+1);
        
        for(int i = 0; i <= exceptCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptCharsInputRecordIndex[i], this);
        }
        
        exceptCharsInputRecordIndex = null;
        exceptCharsDefinition = null;
        exceptCharsIndex = -1;
        
        exceptCharsFEC = null;
    }
    public void clearCharacterContentExceptedError(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, exceptCharsFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(exceptCharsInputRecordIndex[removeIndex], this);
        
        int moved = exceptCharsIndex - removeIndex;
        exceptCharsIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(exceptCharsInputRecordIndex, removeIndex+1, exceptCharsInputRecordIndex, removeIndex, moved);
            System.arraycopy(exceptCharsDefinition, removeIndex+1, exceptCharsDefinition, removeIndex, moved);
            
            System.arraycopy(exceptCharsFEC, removeIndex+1, exceptCharsFEC, removeIndex, moved);
        }
    }
    void transferCharacterContentExceptedError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, exceptCharsFEC);
        
        if(removeIndex == -1) return;
                
        other.characterContentExceptedError(exceptCharsFEC[removeIndex],
                                            exceptCharsInputRecordIndex[removeIndex],
                                            exceptCharsDefinition[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(exceptCharsInputRecordIndex[removeIndex], this);
        
        int moved = exceptCharsIndex - removeIndex;
        exceptCharsIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(exceptCharsInputRecordIndex, removeIndex+1, exceptCharsInputRecordIndex, removeIndex, moved);
            System.arraycopy(exceptCharsDefinition, removeIndex+1, exceptCharsDefinition, removeIndex, moved);
            
            System.arraycopy(exceptCharsFEC, removeIndex+1, exceptCharsFEC, removeIndex, moved);
        }
    }
        
        
	public void attributeValueExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition){
        
        messageTotalCount++;
		if(exceptAVIndex < 0){
			exceptAVIndex = 0;	
			exceptAVInputRecordIndex =new int[initialSize];
			exceptAVDefinition = new SData[initialSize];
			
			exceptAVFEC = new int[initialSize];
		}else if(++exceptAVIndex == exceptAVInputRecordIndex.length){
		    int size = exceptAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptAVInputRecordIndex, 0, increasedCN, 0, exceptAVIndex);
			exceptAVInputRecordIndex = increasedCN;
		    
		    SData[] increasedDef = new SData[size];
			System.arraycopy(exceptAVDefinition, 0, increasedDef, 0, exceptAVIndex);
			exceptAVDefinition = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(exceptAVFEC, 0, increasedFEC, 0, exceptAVIndex);
			exceptAVFEC = increasedFEC;
		}
		
		exceptAVInputRecordIndex[exceptAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptAVDefinition[exceptAVIndex] = charsDefinition;
		
		exceptAVFEC[exceptAVIndex] = functionalEquivalenceCode;
	}
	public void clearAttributeValueExceptedError(){
        
        messageTotalCount -= (exceptAVIndex+1);
        
        for(int i = 0; i <= exceptAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptAVInputRecordIndex[i], this);
        }
        
        exceptAVInputRecordIndex = null;
        exceptAVDefinition = null;
        exceptAVIndex = -1;
        
        exceptAVFEC = null;
    }
    public void clearAttributeValueExceptedError(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, exceptAVFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(exceptAVInputRecordIndex[removeIndex], this);
        
        int moved = exceptAVIndex - removeIndex;
        exceptAVIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(exceptAVInputRecordIndex, removeIndex+1, exceptAVInputRecordIndex, removeIndex, moved);
            System.arraycopy(exceptAVDefinition, removeIndex+1, exceptAVDefinition, removeIndex, moved);
            
            System.arraycopy(exceptAVFEC, removeIndex+1, exceptAVFEC, removeIndex, moved);
        }
    }
    void transferAttributeValueExceptedError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, exceptAVFEC);
        
        if(removeIndex == -1) return;
                
        other.attributeValueExceptedError(exceptAVFEC[removeIndex],
                                            exceptAVInputRecordIndex[removeIndex],
                                            exceptAVDefinition[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(exceptAVInputRecordIndex[removeIndex], this);
        
        int moved = exceptAVIndex - removeIndex;
        exceptAVIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(exceptAVInputRecordIndex, removeIndex+1, exceptAVInputRecordIndex, removeIndex, moved);
            System.arraycopy(exceptAVDefinition, removeIndex+1, exceptAVDefinition, removeIndex, moved);
            
            System.arraycopy(exceptAVFEC, removeIndex+1, exceptAVFEC, removeIndex, moved);
        }
    }    
    
    
	public void unexpectedCharacterContent(int functionalEquivalenceCode, int inputRecordIndex, SElement elementDefinition){
        
        messageTotalCount++;
		if(unexpectedCharsIndex < 0){
			unexpectedCharsIndex = 0;	
			unexpectedCharsInputRecordIndex =new int[initialSize];
			unexpectedCharsDefinition = new SElement[initialSize];
			
			unexpectedCharsFEC = new int[initialSize];
		}else if(++unexpectedCharsIndex == unexpectedCharsInputRecordIndex.length){
		    int size = unexpectedCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unexpectedCharsInputRecordIndex, 0, increasedCN, 0, unexpectedCharsIndex);
			unexpectedCharsInputRecordIndex = increasedCN;
		    
		    SElement[] increasedDef = new SElement[size];
			System.arraycopy(unexpectedCharsDefinition, 0, increasedDef, 0, unexpectedCharsIndex);
			unexpectedCharsDefinition = increasedDef;
			
		    int[] increasedFEC = new int[size];
		    System.arraycopy(unexpectedCharsFEC, 0, increasedFEC, 0, unexpectedCharsIndex);
			unexpectedCharsFEC = increasedFEC;
		}
		
		unexpectedCharsInputRecordIndex[unexpectedCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unexpectedCharsDefinition[unexpectedCharsIndex] = elementDefinition;
		
		unexpectedCharsFEC[unexpectedCharsIndex] = functionalEquivalenceCode;
	}
    public void clearUnexpectedCharacterContent(){
        
        messageTotalCount -= (unexpectedCharsIndex+1);
        
        for(int i = 0; i <= unexpectedCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(unexpectedCharsInputRecordIndex[i], this);
        }
        
        unexpectedCharsInputRecordIndex = null;
        unexpectedCharsDefinition = null;
        unexpectedCharsIndex = -1;
        
        unexpectedCharsFEC = null;
    }
    public void clearUnexpectedCharacterContent(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, unexpectedCharsFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedCharsInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedCharsIndex - removeIndex;
        unexpectedCharsIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unexpectedCharsInputRecordIndex, removeIndex+1, unexpectedCharsInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedCharsDefinition, removeIndex+1, unexpectedCharsDefinition, removeIndex, moved);
            
            System.arraycopy(unexpectedCharsFEC, removeIndex+1, unexpectedCharsFEC, removeIndex, moved);
        }
    }
    void transferUnexpectedCharacterContent(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unexpectedCharsFEC);
        
        if(removeIndex == -1) return;
                
        other.unexpectedCharacterContent(unexpectedCharsFEC[removeIndex],
                                            unexpectedCharsInputRecordIndex[removeIndex],
                                            unexpectedCharsDefinition[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedCharsInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedCharsIndex - removeIndex;
        unexpectedCharsIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unexpectedCharsInputRecordIndex, removeIndex+1, unexpectedCharsInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedCharsDefinition, removeIndex+1, unexpectedCharsDefinition, removeIndex, moved);
            
            System.arraycopy(unexpectedCharsFEC, removeIndex+1, unexpectedCharsFEC, removeIndex, moved);
        }
    }
    
	public void unexpectedAttributeValue(int functionalEquivalenceCode, int inputRecordIndex, SAttribute attributeDefinition){
        
        messageTotalCount++;
		if(unexpectedAVIndex < 0){
			unexpectedAVIndex = 0;	
			unexpectedAVInputRecordIndex =new int[initialSize];
			unexpectedAVDefinition = new SAttribute[initialSize];
			
			unexpectedAVFEC = new int[initialSize];
		}else if(++unexpectedAVIndex == unexpectedAVInputRecordIndex.length){
		    int size = unexpectedAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unexpectedAVInputRecordIndex, 0, increasedCN, 0, unexpectedAVIndex);
			unexpectedAVInputRecordIndex = increasedCN;
		    
		    SAttribute[] increasedDef = new SAttribute[size];
			System.arraycopy(unexpectedAVDefinition, 0, increasedDef, 0, unexpectedAVIndex);
			unexpectedAVDefinition = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(unexpectedAVFEC, 0, increasedFEC, 0, unexpectedAVIndex);
			unexpectedAVFEC = increasedFEC;
		}
		
		unexpectedAVInputRecordIndex[unexpectedAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unexpectedAVDefinition[unexpectedAVIndex] = attributeDefinition;
		
		unexpectedAVFEC[unexpectedAVIndex] = functionalEquivalenceCode;
	}
	public void clearUnexpectedAttributeValue(){
        
        messageTotalCount -= (unexpectedAVIndex+1);
        
        for(int i = 0; i <= unexpectedAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(unexpectedAVInputRecordIndex[i], this);
        }
        
        unexpectedAVInputRecordIndex = null;
        unexpectedAVDefinition = null;
        unexpectedAVIndex = -1;
        
        unexpectedAVFEC = null;
    }
    public void clearUnexpectedAttributeValue(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, unexpectedAVFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedAVInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedAVIndex - removeIndex;
        unexpectedAVIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unexpectedAVInputRecordIndex, removeIndex+1, unexpectedAVInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedAVDefinition, removeIndex+1, unexpectedAVDefinition, removeIndex, moved);
            
            System.arraycopy(unexpectedAVFEC, removeIndex+1, unexpectedAVFEC, removeIndex, moved);
        }
    }
    void transferUnexpectedAttributeValue(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unexpectedAVFEC);
        
        if(removeIndex == -1) return;
                
        other.unexpectedAttributeValue(unexpectedAVFEC[removeIndex],
                                            unexpectedAVInputRecordIndex[removeIndex],
                                            unexpectedAVDefinition[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unexpectedAVInputRecordIndex[removeIndex], this);
        
        int moved = unexpectedAVIndex - removeIndex;
        unexpectedAVIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unexpectedAVInputRecordIndex, removeIndex+1, unexpectedAVInputRecordIndex, removeIndex, moved);
            System.arraycopy(unexpectedAVDefinition, removeIndex+1, unexpectedAVDefinition, removeIndex, moved);
            
            System.arraycopy(unexpectedAVFEC, removeIndex+1, unexpectedAVFEC, removeIndex, moved);
        }
    }    
    
	public void unresolvedCharacterContent(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions){
        
        messageTotalCount++;
		if(unresolvedCharsIndexEE < 0){
			unresolvedCharsIndexEE = 0;	
			unresolvedCharsInputRecordIndexEE =new int[initialSize];
			unresolvedCharsDefinitionEE = new SPattern[initialSize][];
			
			unresolvedCharsFECEE  = new int[initialSize];
		}else if(++unresolvedCharsIndexEE == unresolvedCharsInputRecordIndexEE.length){
		    int size = unresolvedCharsInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedCharsInputRecordIndexEE, 0, increasedCN, 0, unresolvedCharsIndexEE);
			unresolvedCharsInputRecordIndexEE = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(unresolvedCharsDefinitionEE, 0, increasedDef, 0, unresolvedCharsIndexEE);
			unresolvedCharsDefinitionEE = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(unresolvedCharsFECEE, 0, increasedFEC, 0, unresolvedCharsIndexEE);
			unresolvedCharsFECEE = increasedFEC;
		}
		
		unresolvedCharsInputRecordIndexEE[unresolvedCharsIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedCharsDefinitionEE[unresolvedCharsIndexEE] = possibleDefinitions;
		
		unresolvedCharsFECEE[unresolvedCharsIndexEE] = functionalEquivalenceCode;
	}
    public void clearUnresolvedCharacterContent(){
        
        messageTotalCount -= (unresolvedCharsIndexEE+1);
        
        for(int i = 0; i <= unresolvedCharsIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedCharsInputRecordIndexEE[i], this);
        }
        
        unresolvedCharsInputRecordIndexEE = null;
        unresolvedCharsDefinitionEE = null;
        unresolvedCharsIndexEE = -1;
        
        unresolvedCharsFECEE = null;
    }
    public void clearUnresolvedCharacterContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, unresolvedCharsFECEE);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedCharsInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedCharsIndexEE - removeIndex;
        unresolvedCharsIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedCharsInputRecordIndexEE, removeIndex+1, unresolvedCharsInputRecordIndexEE, removeIndex, moved);
            System.arraycopy(unresolvedCharsDefinitionEE, removeIndex+1, unresolvedCharsDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedCharsFECEE, removeIndex+1, unresolvedCharsFECEE, removeIndex, moved);
        }
    }
    void transferUnresolvedCharacterContent(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unresolvedCharsFECEE);
        
        if(removeIndex == -1) return;
                
        other.unresolvedCharacterContent(unresolvedCharsFECEE[removeIndex],
                                        unresolvedCharsInputRecordIndexEE[removeIndex],
                                        unresolvedCharsDefinitionEE[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedCharsInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedCharsIndexEE - removeIndex;
        unresolvedCharsIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedCharsInputRecordIndexEE, removeIndex+1, unresolvedCharsInputRecordIndexEE, removeIndex, moved);
            System.arraycopy(unresolvedCharsDefinitionEE, removeIndex+1, unresolvedCharsDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedCharsFECEE, removeIndex+1, unresolvedCharsFECEE, removeIndex, moved);
        }
    }
        
    
	// {24}
	public void unresolvedAttributeValue(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions){
        
        messageTotalCount++;
		if(unresolvedAVIndexEE < 0){
			unresolvedAVIndexEE = 0;	
			unresolvedAVInputRecordIndexEE =new int[initialSize];
			unresolvedAVDefinitionEE = new SPattern[initialSize][];
			
			unresolvedAVFECEE = new int[initialSize];
		}else if(++unresolvedAVIndexEE == unresolvedAVInputRecordIndexEE.length){
		    int size = unresolvedAVInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAVInputRecordIndexEE, 0, increasedCN, 0, unresolvedAVIndexEE);
			unresolvedAVInputRecordIndexEE = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(unresolvedAVDefinitionEE, 0, increasedDef, 0, unresolvedAVIndexEE);
			unresolvedAVDefinitionEE = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(unresolvedAVFECEE, 0, increasedFEC, 0, unresolvedAVIndexEE);
			unresolvedAVFECEE = increasedFEC;	
		}
		
		unresolvedAVInputRecordIndexEE[unresolvedAVIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAVDefinitionEE[unresolvedAVIndexEE] = possibleDefinitions;
		
		unresolvedAVFECEE[unresolvedAVIndexEE] = functionalEquivalenceCode;
	}
	public void clearUnresolvedAttributeValue(){
        
        messageTotalCount -= (unresolvedAVIndexEE+1);
        
        for(int i = 0; i <= unresolvedAVIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAVInputRecordIndexEE[i], this);
        }
        
        unresolvedAVInputRecordIndexEE = null;
        unresolvedAVDefinitionEE = null;
        unresolvedAVIndexEE = -1;
        
        unresolvedAVFECEE = null;
    }
    public void clearUnresolvedAttributeValue(int messageId){
        int removeIndex = getRemoveIndex(messageId, unresolvedAVFECEE);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedAVInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedAVIndexEE - removeIndex;
        unresolvedAVIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedAVInputRecordIndexEE, removeIndex+1, unresolvedAVInputRecordIndexEE, removeIndex, moved);
            System.arraycopy(unresolvedAVDefinitionEE, removeIndex+1, unresolvedAVDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedAVFECEE, removeIndex+1, unresolvedAVFECEE, removeIndex, moved);
        }
    }
    void transferUnresolvedAttributeValue(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unresolvedAVFECEE);
        
        if(removeIndex == -1) return;
                
        other.unresolvedAttributeValue(unresolvedAVFECEE[removeIndex],
                                        unresolvedAVInputRecordIndexEE[removeIndex],
                                        unresolvedAVDefinitionEE[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedAVInputRecordIndexEE[removeIndex], this);
        
        int moved = unresolvedAVIndexEE - removeIndex;
        unresolvedAVIndexEE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedAVInputRecordIndexEE, removeIndex+1, unresolvedAVInputRecordIndexEE, removeIndex, moved);
            System.arraycopy(unresolvedAVDefinitionEE, removeIndex+1, unresolvedAVDefinitionEE, removeIndex, moved);
            
            System.arraycopy(unresolvedAVFECEE, removeIndex+1, unresolvedAVFECEE, removeIndex, moved);
        }
    }
    
    
    // {25}
	public void listTokenDatatypeError(int functionalEquivalenceCode, int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        
        messageTotalCount++;
		if(datatypeTokenIndex < 0){
			datatypeTokenIndex = 0;	
			datatypeTokenInputRecordIndex =new int[initialSize];
			datatypeTokenDefinition = new SPattern[initialSize];
			datatypeTokenErrorMessage = new String[initialSize];
			
			datatypeTokenFEC = new int[initialSize];
		}else if(++datatypeTokenIndex == datatypeTokenInputRecordIndex.length){
		    int size = datatypeTokenInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(datatypeTokenInputRecordIndex, 0, increasedCN, 0, datatypeTokenIndex);
			datatypeTokenInputRecordIndex = increasedCN;
		    
		    SPattern[] increasedDef = new SPattern[size];
			System.arraycopy(datatypeTokenDefinition, 0, increasedDef, 0, datatypeTokenIndex);
			datatypeTokenDefinition = increasedDef;
			
			String[] increasedEM = new String[size];
			System.arraycopy(datatypeTokenErrorMessage, 0, increasedEM, 0, datatypeTokenIndex);
			datatypeTokenErrorMessage = increasedEM;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(datatypeTokenFEC, 0, increasedFEC, 0, datatypeTokenIndex);
			datatypeTokenFEC = increasedFEC;
		}
		
		datatypeTokenInputRecordIndex[datatypeTokenIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		datatypeTokenDefinition[datatypeTokenIndex] = charsDefinition;
		datatypeTokenErrorMessage[datatypeTokenIndex] = datatypeErrorMessage;
		
		datatypeTokenFEC[datatypeTokenIndex] = functionalEquivalenceCode;
	}
    public void clearListTokenDatatypeError(){
        
        messageTotalCount -= (datatypeTokenIndex+1);
        
        for(int i = 0; i <= datatypeTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeTokenInputRecordIndex[i], this);
        }
        
        datatypeTokenInputRecordIndex = null;
        datatypeTokenDefinition = null;
        datatypeTokenErrorMessage = null;
        datatypeTokenIndex = -1;
        
        datatypeTokenFEC = null;
    }
    public void clearListTokenDatatypeError(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, datatypeTokenFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(datatypeTokenInputRecordIndex[removeIndex], this);
        
        int moved = datatypeTokenIndex - removeIndex;
        datatypeTokenIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(datatypeTokenInputRecordIndex, removeIndex+1, datatypeTokenInputRecordIndex, removeIndex, moved);
            System.arraycopy(datatypeTokenDefinition, removeIndex+1, datatypeTokenDefinition, removeIndex, moved);
            System.arraycopy(datatypeTokenErrorMessage, removeIndex+1, datatypeTokenErrorMessage, removeIndex, moved);
            
            System.arraycopy(datatypeTokenFEC, removeIndex+1, datatypeTokenFEC, removeIndex, moved);
        }
    }
    void transferListTokenDatatypeError(int messageId, ConflictMessageHandler other){ 
        
        int removeIndex = getRemoveIndex(messageId, datatypeTokenFEC);
        
        if(removeIndex == -1) return;
                
        other.listTokenDatatypeError(datatypeTokenFEC[removeIndex],
                                            datatypeTokenInputRecordIndex[removeIndex],
                                            datatypeTokenDefinition[removeIndex],
                                            datatypeTokenErrorMessage[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(datatypeTokenInputRecordIndex[removeIndex], this);
        
        int moved = datatypeTokenIndex - removeIndex;
        datatypeTokenIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(datatypeTokenInputRecordIndex, removeIndex+1, datatypeTokenInputRecordIndex, removeIndex, moved);
            System.arraycopy(datatypeTokenDefinition, removeIndex+1, datatypeTokenDefinition, removeIndex, moved);
            System.arraycopy(datatypeTokenErrorMessage, removeIndex+1, datatypeTokenErrorMessage, removeIndex, moved);
            
            System.arraycopy(datatypeTokenFEC, removeIndex+1, datatypeTokenFEC, removeIndex, moved);
        }
    }   
        
	public void listTokenValueError(int functionalEquivalenceCode, int inputRecordIndex, SValue charsDefinition){
        
        messageTotalCount++;
		if(valueTokenIndex < 0){
			valueTokenIndex = 0;	
			valueTokenInputRecordIndex =new int[initialSize];
			valueTokenDefinition = new SValue[initialSize];
			
			valueTokenFEC = new int[initialSize];
		}else if(++valueTokenIndex == valueTokenInputRecordIndex.length){
		    int size = valueTokenInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueTokenInputRecordIndex, 0, increasedCN, 0, valueTokenIndex);
			valueTokenInputRecordIndex = increasedCN;
		    
		    SValue[] increasedDef = new SValue[size];
			System.arraycopy(valueTokenDefinition, 0, increasedDef, 0, valueTokenIndex);
			valueTokenDefinition = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(valueTokenFEC, 0, increasedFEC, 0, valueTokenIndex);
			valueTokenFEC = increasedFEC;		    
		}
		
		valueTokenInputRecordIndex[valueTokenIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueTokenDefinition[valueTokenIndex] = charsDefinition;
		
		valueTokenFEC[valueTokenIndex] = functionalEquivalenceCode;
	}
    public void clearListTokenValueError(){
        
        messageTotalCount -= (valueTokenIndex+1);
        
        for(int i = 0; i <= valueTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueTokenInputRecordIndex[i], this);
        }
        
        valueTokenInputRecordIndex = null;
        valueTokenDefinition = null;
        valueTokenIndex = -1;
        
        valueTokenFEC = null;
    }
    public void clearListTokenValueError(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, valueTokenFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(valueTokenInputRecordIndex[removeIndex], this);
        
        int moved = valueTokenIndex - removeIndex;
        valueTokenIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(valueTokenInputRecordIndex, removeIndex+1, valueTokenInputRecordIndex, removeIndex, moved);
            System.arraycopy(valueTokenDefinition, removeIndex+1, valueTokenDefinition, removeIndex, moved);
            
            System.arraycopy(valueTokenFEC, removeIndex+1, valueTokenFEC, removeIndex, moved);
        }
    }
    void transferListTokenValueError(int messageId, ConflictMessageHandler other){
        
        int removeIndex = getRemoveIndex(messageId, valueTokenFEC);
        
        if(removeIndex == -1) return;
                
        other.listTokenValueError(valueTokenFEC[removeIndex],
                                            valueTokenInputRecordIndex[removeIndex],
                                            valueTokenDefinition[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(valueTokenInputRecordIndex[removeIndex], this);
        
        int moved = valueTokenIndex - removeIndex;
        valueTokenIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(valueTokenInputRecordIndex, removeIndex+1, valueTokenInputRecordIndex, removeIndex, moved);
            System.arraycopy(valueTokenDefinition, removeIndex+1, valueTokenDefinition, removeIndex, moved);
            
            System.arraycopy(valueTokenFEC, removeIndex+1, valueTokenFEC, removeIndex, moved);
        }
    }
    
    
	public void listTokenExceptedError(int functionalEquivalenceCode, int inputRecordIndex, SData charsDefinition){
        
        messageTotalCount++;
		if(exceptTokenIndex < 0){
			exceptTokenIndex = 0;	
			exceptTokenInputRecordIndex =new int[initialSize];
			exceptTokenDefinition = new SData[initialSize];
			
			exceptTokenFEC = new int[initialSize];
		}else if(++exceptTokenIndex == exceptTokenInputRecordIndex.length){
		    int size = exceptTokenInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptTokenInputRecordIndex, 0, increasedCN, 0, exceptTokenIndex);
			exceptTokenInputRecordIndex = increasedCN;
		    
		    SData[] increasedDef = new SData[size];
			System.arraycopy(exceptTokenDefinition, 0, increasedDef, 0, exceptTokenIndex);
			exceptTokenDefinition = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(exceptTokenFEC, 0, increasedFEC, 0, exceptTokenIndex);
			exceptTokenFEC = increasedFEC;		    
		}
		
		exceptTokenInputRecordIndex[exceptTokenIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptTokenDefinition[exceptTokenIndex] = charsDefinition;
		
		exceptTokenFEC[exceptTokenIndex] = functionalEquivalenceCode;
	}
    public void clearListTokenExceptedError(){
        
        messageTotalCount -= (exceptTokenIndex+1);
        
        for(int i = 0; i <= exceptTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptTokenInputRecordIndex[i], this);
        }
        
        exceptTokenInputRecordIndex = null;
        exceptTokenDefinition = null;
        exceptTokenIndex = -1;
        
        exceptTokenFEC = null;
    }
    public void clearListTokenExceptedError(int messageId){
        
        int removeIndex = getRemoveIndex(messageId, exceptTokenFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(exceptTokenInputRecordIndex[removeIndex], this);
        
        int moved = exceptTokenIndex - removeIndex;
        exceptTokenIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(exceptTokenInputRecordIndex, removeIndex+1, exceptTokenInputRecordIndex, removeIndex, moved);
            System.arraycopy(exceptTokenDefinition, removeIndex+1, exceptTokenDefinition, removeIndex, moved);
            
            System.arraycopy(exceptTokenFEC, removeIndex+1, exceptTokenFEC, removeIndex, moved);
        }
    }
    void transferListTokenExceptedError(int messageId, ConflictMessageHandler other){
        
        int removeIndex = getRemoveIndex(messageId, exceptTokenFEC);
        
        if(removeIndex == -1) return;
                
        other.listTokenExceptedError(exceptTokenFEC[removeIndex],
                                            exceptTokenInputRecordIndex[removeIndex],
                                            exceptTokenDefinition[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(exceptTokenInputRecordIndex[removeIndex], this);
        
        int moved = exceptTokenIndex - removeIndex;
        exceptTokenIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(exceptTokenInputRecordIndex, removeIndex+1, exceptTokenInputRecordIndex, removeIndex, moved);
            System.arraycopy(exceptTokenDefinition, removeIndex+1, exceptTokenDefinition, removeIndex, moved);
            
            System.arraycopy(exceptTokenFEC, removeIndex+1, exceptTokenFEC, removeIndex, moved);
        }
    }
    
    
    public void unresolvedListTokenInContextError(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions){
        
        messageTotalCount++;
		if(unresolvedTokenIndexLPICE < 0){
			unresolvedTokenIndexLPICE = 0;	
			unresolvedTokenInputRecordIndexLPICE =new int[initialSize];
			unresolvedTokenDefinitionLPICE = new SPattern[initialSize][];
			
			unresolvedTokenFECLPICE = new int[initialSize];
		}else if(++unresolvedTokenIndexLPICE == unresolvedTokenInputRecordIndexLPICE.length){
		    int size = unresolvedTokenInputRecordIndexLPICE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedTokenInputRecordIndexLPICE, 0, increasedCN, 0, unresolvedTokenIndexLPICE);
			unresolvedTokenInputRecordIndexLPICE = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(unresolvedTokenDefinitionLPICE, 0, increasedDef, 0, unresolvedTokenIndexLPICE);
			unresolvedTokenDefinitionLPICE = increasedDef;
			
			int[] increasedFECLPICEN = new int[size];
			System.arraycopy(unresolvedTokenFECLPICE, 0, increasedFECLPICEN, 0, unresolvedTokenIndexLPICE);
			unresolvedTokenFECLPICE = increasedFECLPICEN;
		}
		
		unresolvedTokenInputRecordIndexLPICE[unresolvedTokenIndexLPICE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedTokenDefinitionLPICE[unresolvedTokenIndexLPICE] = possibleDefinitions;
		
		unresolvedTokenFECLPICE[unresolvedTokenIndexLPICE] = functionalEquivalenceCode;
    }
    public void clearUnresolvedListTokenInContextError(){
        
        messageTotalCount -= (unresolvedTokenIndexLPICE+1);
        
        for(int i = 0; i <= unresolvedTokenIndexLPICE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedTokenInputRecordIndexLPICE[i], this);
        }
        
        unresolvedTokenInputRecordIndexLPICE = null;
        unresolvedTokenDefinitionLPICE = null;
        unresolvedTokenIndexLPICE = -1;
        
        unresolvedTokenFECLPICE = null;
    }
    public void clearUnresolvedListTokenInContextError(int messageId){
        int removeIndex = getRemoveIndex(messageId, unresolvedTokenFECLPICE);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedTokenInputRecordIndexLPICE[removeIndex], this);
        
        int moved = unresolvedTokenIndexLPICE - removeIndex;
        unresolvedTokenIndexLPICE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedTokenInputRecordIndexLPICE, removeIndex+1, unresolvedTokenInputRecordIndexLPICE, removeIndex, moved);
            System.arraycopy(unresolvedTokenDefinitionLPICE, removeIndex+1, unresolvedTokenDefinitionLPICE, removeIndex, moved);
            
            System.arraycopy(unresolvedTokenFECLPICE, removeIndex+1, unresolvedTokenFECLPICE, removeIndex, moved);
        }
    }
    void transferUnresolvedListTokenInContextError(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, unresolvedTokenFECLPICE);
        
        if(removeIndex == -1) return;
                
        other.unresolvedListTokenInContextError(unresolvedTokenFECLPICE[removeIndex],
                                            unresolvedTokenInputRecordIndexLPICE[removeIndex],
                                            unresolvedTokenDefinitionLPICE[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(unresolvedTokenInputRecordIndexLPICE[removeIndex], this);
        
        int moved = unresolvedTokenIndexLPICE - removeIndex;
        unresolvedTokenIndexLPICE--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(unresolvedTokenInputRecordIndexLPICE, removeIndex+1, unresolvedTokenInputRecordIndexLPICE, removeIndex, moved);
            System.arraycopy(unresolvedTokenDefinitionLPICE, removeIndex+1, unresolvedTokenDefinitionLPICE, removeIndex, moved);
            
            System.arraycopy(unresolvedTokenFECLPICE, removeIndex+1, unresolvedTokenFECLPICE, removeIndex, moved);
        }
    }
    
    
    
    public void ambiguousListTokenInContextWarning(int functionalEquivalenceCode, int inputRecordIndex, SPattern[] possibleDefinitions){
        
        messageTotalCount++;
		if(ambiguousTokenIndexLPICW < 0){
			ambiguousTokenIndexLPICW = 0;	
			ambiguousTokenInputRecordIndexLPICW =new int[initialSize];
			ambiguousTokenDefinitionLPICW = new SPattern[initialSize][];
			
			ambiguousTokenFECLPICW = new int[initialSize];
		}else if(++ambiguousTokenIndexLPICW == ambiguousTokenInputRecordIndexLPICW.length){
		    int size = ambiguousTokenInputRecordIndexLPICW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousTokenInputRecordIndexLPICW, 0, increasedCN, 0, ambiguousTokenIndexLPICW);
			ambiguousTokenInputRecordIndexLPICW = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(ambiguousTokenDefinitionLPICW, 0, increasedDef, 0, ambiguousTokenIndexLPICW);
			ambiguousTokenDefinitionLPICW = increasedDef;
			
			int[] increasedFEC = new int[size];
			System.arraycopy(ambiguousTokenFECLPICW, 0, increasedFEC, 0, ambiguousTokenIndexLPICW);
			ambiguousTokenFECLPICW = increasedFEC;
		}
		
		ambiguousTokenInputRecordIndexLPICW[ambiguousTokenIndexLPICW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousTokenDefinitionLPICW[ambiguousTokenIndexLPICW] = possibleDefinitions;
		
		ambiguousTokenFECLPICW[ambiguousTokenIndexLPICW] = functionalEquivalenceCode;
    }       
    public void clearAmbiguousListTokenInContextWarning(){
        
        messageTotalCount -= (ambiguousTokenIndexLPICW+1);
        
        for(int i = 0; i <= ambiguousTokenIndexLPICW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousTokenInputRecordIndexLPICW[i], this);
        }
        
        ambiguousTokenInputRecordIndexLPICW = null;
        ambiguousTokenDefinitionLPICW = null;
        ambiguousTokenIndexLPICW = -1;
        
        ambiguousTokenFECLPICW = null;
    }
    public void clearAmbiguousListTokenInContextWarning(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousTokenFECLPICW);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(ambiguousTokenInputRecordIndexLPICW[removeIndex], this);
        
        int moved = ambiguousTokenIndexLPICW - removeIndex;
        ambiguousTokenIndexLPICW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousTokenInputRecordIndexLPICW, removeIndex+1, ambiguousTokenInputRecordIndexLPICW, removeIndex, moved);
            System.arraycopy(ambiguousTokenDefinitionLPICW, removeIndex+1, ambiguousTokenDefinitionLPICW, removeIndex, moved);
            
            System.arraycopy(ambiguousTokenFECLPICW, removeIndex+1, ambiguousTokenFECLPICW, removeIndex, moved);
        }
    }
    void transferAmbiguousListTokenInContextWarning(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, ambiguousTokenFECLPICW);
        
        if(removeIndex == -1) return;
                
        other.ambiguousListTokenInContextWarning(ambiguousTokenFECLPICW[removeIndex],
                                                ambiguousTokenInputRecordIndexLPICW[removeIndex],
                                                ambiguousTokenDefinitionLPICW[removeIndex]);
        
        int moved = ambiguousTokenIndexLPICW - removeIndex;
        ambiguousTokenIndexLPICW--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousTokenInputRecordIndexLPICW, removeIndex+1, ambiguousTokenInputRecordIndexLPICW, removeIndex, moved);
            System.arraycopy(ambiguousTokenDefinitionLPICW, removeIndex+1, ambiguousTokenDefinitionLPICW, removeIndex, moved);
            
            System.arraycopy(ambiguousTokenFECLPICW, removeIndex+1, ambiguousTokenFECLPICW, removeIndex, moved);
        }
    }
        
        
    
	public void missingCompositorContent(int functionalEquivalenceCode, 
                                SRule context, 
								int startInputRecordIndex,								 
								SPattern definition, 
								int expected, 
								int found){
        
        if(missingCompositorContentIndex < 0){
			missingCompositorContentIndex = 0;
			missingCompositorContentContext = new SPattern[initialSize];
			missingCompositorContentStartInputRecordIndex = new int[initialSize];
			missingCompositorContentDefinition = new SPattern[initialSize];
			missingCompositorContentExpected = new int[initialSize];
			missingCompositorContentFound = new int[initialSize];			
			
			missingCompositorContentFEC = new int[initialSize];
		}else if(++missingCompositorContentIndex == missingCompositorContentContext.length){
		    int size = missingCompositorContentIndex+ increaseSizeAmount;
		    
			SPattern[] increasedEC = new SPattern[size];
			System.arraycopy(missingCompositorContentContext, 0, increasedEC, 0, missingCompositorContentIndex);
			missingCompositorContentContext = increasedEC;
			
			int[] increasedSCN = new int[size];
			System.arraycopy(missingCompositorContentStartInputRecordIndex, 0, increasedSCN, 0, missingCompositorContentIndex);
			missingCompositorContentStartInputRecordIndex = increasedSCN;
			
			SPattern[] increasedED = new SPattern[size];
			System.arraycopy(missingCompositorContentDefinition, 0, increasedED, 0, missingCompositorContentIndex);
			missingCompositorContentDefinition = increasedED;
			
			int[] increasedE = new int[size];
			System.arraycopy(missingCompositorContentExpected, 0, increasedE, 0, missingCompositorContentIndex);
			missingCompositorContentExpected = increasedE;
			
			int[] increasedF = new int[size];
			System.arraycopy(missingCompositorContentFound, 0, increasedF, 0, missingCompositorContentIndex);
			missingCompositorContentFound = increasedF;

            int[] increasedFEC = new int[size];
			System.arraycopy(missingCompositorContentFEC, 0, increasedFEC, 0, missingCompositorContentIndex);
			missingCompositorContentFEC = increasedFEC;			
		}
		missingCompositorContentContext[missingCompositorContentIndex] = context;
		missingCompositorContentStartInputRecordIndex[missingCompositorContentIndex] = startInputRecordIndex;
		activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		missingCompositorContentDefinition[missingCompositorContentIndex] = definition;
		missingCompositorContentExpected[missingCompositorContentIndex] = expected;
		missingCompositorContentFound[missingCompositorContentIndex] = found;
		
		missingCompositorContentFEC[missingCompositorContentIndex] = functionalEquivalenceCode;
	}    
	public void clearMissingCompositorContent(){
        
        messageTotalCount -= (missingCompositorContentIndex + 1);
        
        for(int i = 0; i <= missingCompositorContentIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(missingCompositorContentStartInputRecordIndex[i], this);
        }
        
        missingCompositorContentContext = null;
        missingCompositorContentStartInputRecordIndex = null;
        missingCompositorContentDefinition = null;
        missingCompositorContentExpected = null;
        missingCompositorContentFound = null;
        
        missingCompositorContentIndex = -1;
        
        missingCompositorContentFEC = null;
    }
    public void clearMissingCompositorContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, missingCompositorContentFEC);
        
        if(removeIndex == -1) return;
        
        activeInputDescriptor.unregisterClientForRecord(missingCompositorContentStartInputRecordIndex[removeIndex], this);
        
        int moved = missingCompositorContentIndex - removeIndex;
        missingCompositorContentIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(missingCompositorContentContext, removeIndex+1, missingCompositorContentContext, removeIndex, moved);
            System.arraycopy(missingCompositorContentStartInputRecordIndex, removeIndex+1, missingCompositorContentStartInputRecordIndex, removeIndex, moved);
            System.arraycopy(missingCompositorContentDefinition, removeIndex+1, missingCompositorContentDefinition, removeIndex, moved);
            System.arraycopy(missingCompositorContentExpected, removeIndex+1, missingCompositorContentExpected, removeIndex, moved);
            System.arraycopy(missingCompositorContentFound, removeIndex+1, missingCompositorContentFound, removeIndex, moved);
            System.arraycopy(missingCompositorContentFEC, removeIndex+1, missingCompositorContentFEC, removeIndex, moved);
        }
    }
    void transferMissingCompositorContent(int messageId, ConflictMessageHandler other){
        int removeIndex = getRemoveIndex(messageId, missingCompositorContentFEC);
        
        if(removeIndex == -1) return;
                
        other.missingCompositorContent(missingCompositorContentFEC[removeIndex],
                                    missingCompositorContentContext[removeIndex],
                                    missingCompositorContentStartInputRecordIndex[removeIndex],
                                    missingCompositorContentDefinition[removeIndex],
                                    missingCompositorContentExpected[removeIndex],
                                    missingCompositorContentFound[removeIndex]);
        
        activeInputDescriptor.unregisterClientForRecord(missingCompositorContentStartInputRecordIndex[removeIndex], this);
        
        int moved = missingCompositorContentIndex - removeIndex;
        missingCompositorContentIndex--;
        messageTotalCount--;
        if(moved > 0){
            System.arraycopy(missingCompositorContentContext, removeIndex+1, missingCompositorContentContext, removeIndex, moved);
            System.arraycopy(missingCompositorContentStartInputRecordIndex, removeIndex+1, missingCompositorContentStartInputRecordIndex, removeIndex, moved);
            System.arraycopy(missingCompositorContentDefinition, removeIndex+1, missingCompositorContentDefinition, removeIndex, moved);
            System.arraycopy(missingCompositorContentExpected, removeIndex+1, missingCompositorContentExpected, removeIndex, moved);
            System.arraycopy(missingCompositorContentFound, removeIndex+1, missingCompositorContentFound, removeIndex, moved);
            System.arraycopy(missingCompositorContentFEC, removeIndex+1, missingCompositorContentFEC, removeIndex, moved);
        }        
    }
    
    int getRemoveIndex(int messageId, int[] messageCodes){
        int index = -1;
        //if(messageCodes == null) return index;// for warnings that have not been recorded TODO why?
		for(int i = 0; i < messageCodes.length; i++){
			if(messageId == messageCodes[i]){
				index = i;
				break;
			}
		}
        return index;
    }
    
    
    public  void conflict(int functionalEquivalenceCode, int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
        messageTotalCount++;
        this.conflictResolutionId = conflictResolutionId;
        this.candidatesCount = candidatesCount;
        this.commonMessages = commonMessages;
        this.disqualified = disqualified;
        this.candidateMessages = candidateMessages;  
        conflictFEC = functionalEquivalenceCode;     
        
        if(commonMessages != null)commonMessages.registerClient(this);
        if(candidateMessages != null){
            for(int i = 0; i < candidateMessages.length; i++){
                if(candidateMessages[i] != null)candidateMessages[i].registerClient(this);
            }
        }
    }
    public void clearConflict(){
        messageTotalCount--;
        conflictResolutionId = RESOLVED;
        candidatesCount = -1;
        if(commonMessages != null){commonMessages.unregisterClient(this); // It can be only one because it is about this context.
            commonMessages = null;
        }
        
        disqualified = null;
        
        if(candidateMessages != null){
            for(MessageReporter cm : candidateMessages){
                if(cm != null)cm.unregisterClient(this);
            }
            candidateMessages = null;
        }
        
        conflictFEC = -1;
    }
	void transferConflict(int messageId, ConflictMessageHandler other){
	    other.conflict(conflictFEC, conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
	    
	    messageTotalCount--;
        conflictResolutionId = RESOLVED;
        candidatesCount = -1;
        commonMessages = null;
        disqualified = null;
        candidateMessages = null;
        conflictFEC = -1;
	}
    
    // TODO consider replacing with booleans
    int getMessageTotalCount(){
        return messageTotalCount;
    }    
    
    void clearLastMessage(int errorId){		
        if(errorId == UNKNOWN_ELEMENT){
            if(unknownElementIndex < 0) throw new IllegalArgumentException();
            unknownElementIndex--;
            messageTotalCount--;            
        }else if(errorId == UNEXPECTED_ELEMENT){        
            if(unexpectedElementIndex < 0) throw new IllegalArgumentException();
            unexpectedElementIndex--;
            messageTotalCount--;
        }else if(errorId == UNEXPECTED_AMBIGUOUS_ELEMENT){
            if(unexpectedAmbiguousElementIndex < 0) throw new IllegalArgumentException();
            unexpectedAmbiguousElementIndex--;
            messageTotalCount--;
        }else if(errorId == UNKNOWN_ATTRIBUTE){
            if(unknownAttributeIndex < 0) throw new IllegalArgumentException();
            unknownAttributeIndex--;
            messageTotalCount--;
        }else if(errorId == UNEXPECTED_ATTRIBUTE){
            if(unexpectedAttributeIndex < 0) throw new IllegalArgumentException();
            unexpectedAttributeIndex--;
            messageTotalCount--;
        }else if(errorId == UNEXPECTED_AMBIGUOUS_ATTRIBUTE){
            if(unexpectedAmbiguousAttributeIndex < 0) throw new IllegalArgumentException();
            unexpectedAmbiguousAttributeIndex--;
            messageTotalCount--;
        }else if(errorId == MISPLACED_ELEMENT){
            if(misplacedIndex < 0) throw new IllegalArgumentException();
            misplacedIndex--;
            messageTotalCount--;
        }else if(errorId == EXCESSIVE_CONTENT){
            if(excessiveIndex < 0) throw new IllegalArgumentException();
            excessiveIndex--;
            messageTotalCount--;
        }else if(errorId == UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR){
            if(unresolvedAmbiguousElementIndexEE < 0) throw new IllegalArgumentException();
            unresolvedAmbiguousElementIndexEE--;
            messageTotalCount--;
        }else if(errorId == UNRESOLVED_ATTRIBUTE_CONTENT_ERROR){
            if(unresolvedAttributeIndexEE < 0) throw new IllegalArgumentException();
            unresolvedAttributeIndexEE--;
            messageTotalCount--;
        }else if(errorId == MISSING_CONTENT){
            if(missingIndex < 0) throw new IllegalArgumentException();
            missingIndex--;	  
            messageTotalCount--;
        }else if(errorId == ILLEGAL_CONTENT){
            if(illegalIndex < 0) throw new IllegalArgumentException();
            illegalIndex--;
            messageTotalCount--;
        }else if(errorId == UNDETERMINED_BY_CONTENT){
            if(undeterminedQName == null) throw new IllegalArgumentException();
            undeterminedQName = null;
            undeterminedCandidateMessages = null;
            messageTotalCount--;
        }else if(errorId == CHARACTER_CONTENT_DATATYPE_ERROR){
            if(datatypeCharsIndex < 0) throw new IllegalArgumentException();
            datatypeCharsIndex--;
            messageTotalCount--;
        }else if(errorId == ATTRIBUTE_VALUE_DATATYPE_ERROR){
            if(datatypeAVIndex < 0) throw new IllegalArgumentException();
            datatypeAVIndex--;
            messageTotalCount--;
        }else if(errorId == CHARACTER_CONTENT_VALUE_ERROR){
            if(valueCharsIndex < 0) throw new IllegalArgumentException();
            valueCharsIndex--;
            messageTotalCount--;
        }else if(errorId == ATTRIBUTE_VALUE_VALUE_ERROR){
            if(valueAVIndex < 0) throw new IllegalArgumentException();
            valueAVIndex--;
            messageTotalCount--;
        }else if(errorId == CHARACTER_CONTENT_EXCEPTED_ERROR){
            if(exceptCharsIndex < 0) throw new IllegalArgumentException();
            exceptCharsIndex--;
            messageTotalCount--;
        }else if(errorId == ATTRIBUTE_VALUE_EXCEPTED_ERROR){
            if(exceptAVIndex < 0) throw new IllegalArgumentException();
            exceptAVIndex--;
            messageTotalCount--;
        }else if(errorId == UNEXPECTED_CHARACTER_CONTENT){
            if(unexpectedCharsIndex < 0) throw new IllegalArgumentException();
            unexpectedCharsIndex--;
            messageTotalCount--;
        }else if(errorId == UNEXPECTED_ATTRIBUTE_VALUE){
            if(unexpectedAVIndex < 0) throw new IllegalArgumentException();
            unexpectedAVIndex--;
            messageTotalCount--;
        }else if(errorId == UNRESOLVED_CHARACTER_CONTENT){
            if(unresolvedCharsIndexEE < 0) throw new IllegalArgumentException();
            unresolvedCharsIndexEE--;
            messageTotalCount--;
        }else if(errorId == UNRESOLVED_ATTRIBUTE_VALUE){
            if(unresolvedAVIndexEE < 0) throw new IllegalArgumentException();
            unresolvedAVIndexEE--;
            messageTotalCount--;
        }else if(errorId == LIST_TOKEN_DATATYPE_ERROR){
            if(datatypeTokenIndex < 0) throw new IllegalArgumentException();
            datatypeTokenIndex--;
            messageTotalCount--;
        }else if(errorId == LIST_TOKEN_VALUE_ERROR){
            if(valueTokenIndex < 0) throw new IllegalArgumentException();
            valueTokenIndex--;
            messageTotalCount--;
        }else if(errorId == LIST_TOKEN_EXCEPTED_ERROR){
            if(exceptTokenIndex < 0) throw new IllegalArgumentException();
            exceptTokenIndex--;
            messageTotalCount--;
        }else if(errorId == UNRESOLVED_LIST_TOKEN_IN_CONTEXT_ERROR){
            if(unresolvedTokenIndexLPICE < 0) throw new IllegalArgumentException();
            unresolvedTokenIndexLPICE--;
            messageTotalCount--;
        }else if(errorId == MISSING_COMPOSITOR_CONTENT){
            if(missingCompositorContentIndex < 0) throw new IllegalArgumentException();
            missingCompositorContentIndex--;
            messageTotalCount--;
        }else if(errorId == CONFLICT){
            if(disqualified == null) throw new IllegalArgumentException();
            clearConflict();
        }else{
            throw new IllegalArgumentException();
        }
    }
    
    
    void clearErrorMessage(int errorId, int messageId){		
        if(errorId == UNKNOWN_ELEMENT){
            if(unknownElementIndex < 0) throw new IllegalArgumentException();
            clearUnknownElement(messageId);            
        }else if(errorId == UNEXPECTED_ELEMENT){        
            if(unexpectedElementIndex < 0) throw new IllegalArgumentException();
            clearUnexpectedElement(messageId);
        }else if(errorId == UNEXPECTED_AMBIGUOUS_ELEMENT){
            if(unexpectedAmbiguousElementIndex < 0) throw new IllegalArgumentException();
            clearUnexpectedAmbiguousElement(messageId);
        }else if(errorId == UNKNOWN_ATTRIBUTE){
            if(unknownAttributeIndex < 0) throw new IllegalArgumentException();
            clearUnknownAttribute(messageId);
        }else if(errorId == UNEXPECTED_ATTRIBUTE){
            if(unexpectedAttributeIndex < 0) throw new IllegalArgumentException();
            clearUnexpectedAttribute(messageId);
        }else if(errorId == UNEXPECTED_AMBIGUOUS_ATTRIBUTE){
            if(unexpectedAmbiguousAttributeIndex < 0) throw new IllegalArgumentException();
            clearUnexpectedAmbiguousAttribute(messageId);
        }else if(errorId == MISPLACED_ELEMENT){
            if(misplacedIndex < 0) throw new IllegalArgumentException();
            clearMisplacedContent(messageId);
        }else if(errorId == EXCESSIVE_CONTENT){
            if(excessiveIndex < 0) throw new IllegalArgumentException();
            clearExcessiveContent(messageId);
        }else if(errorId == UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR){
            if(unresolvedAmbiguousElementIndexEE < 0) throw new IllegalArgumentException();
            clearUnresolvedAmbiguousElementContentError(messageId);
        }else if(errorId == UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR){
            if(unresolvedUnresolvedElementIndexEE < 0) throw new IllegalArgumentException();
            clearUnresolvedUnresolvedElementContentError(messageId);
        }else if(errorId == UNRESOLVED_ATTRIBUTE_CONTENT_ERROR){
            if(unresolvedAttributeIndexEE < 0) throw new IllegalArgumentException();
            clearUnresolvedAttributeContentError(messageId);
        }else if(errorId == MISSING_CONTENT){
            if(missingIndex < 0) throw new IllegalArgumentException();
            clearMissingContent(messageId);
        }else if(errorId == ILLEGAL_CONTENT){
            if(illegalIndex < 0) throw new IllegalArgumentException();
            clearIllegalContent(messageId);
        }else if(errorId == UNDETERMINED_BY_CONTENT){
            if(undeterminedQName == null) throw new IllegalArgumentException();
            undeterminedQName = null;
            undeterminedCandidateMessages = null;
            messageTotalCount--;
        }else if(errorId == CHARACTER_CONTENT_DATATYPE_ERROR){
            if(datatypeCharsIndex < 0) throw new IllegalArgumentException();
            clearCharacterContentDatatypeError(messageId);
        }else if(errorId == ATTRIBUTE_VALUE_DATATYPE_ERROR){
            if(datatypeAVIndex < 0) throw new IllegalArgumentException();
            clearAttributeValueDatatypeError(messageId);
        }else if(errorId == CHARACTER_CONTENT_VALUE_ERROR){
            if(valueCharsIndex < 0) throw new IllegalArgumentException();
            clearCharacterContentValueError(messageId);
        }else if(errorId == ATTRIBUTE_VALUE_VALUE_ERROR){
            if(valueAVIndex < 0) throw new IllegalArgumentException();
            clearAttributeValueValueError(messageId);
        }else if(errorId == CHARACTER_CONTENT_EXCEPTED_ERROR){
            if(exceptCharsIndex < 0) throw new IllegalArgumentException();
            clearCharacterContentExceptedError(messageId);
        }else if(errorId == ATTRIBUTE_VALUE_EXCEPTED_ERROR){
            if(exceptAVIndex < 0) throw new IllegalArgumentException();
            clearAttributeValueExceptedError(messageId);
        }else if(errorId == UNEXPECTED_CHARACTER_CONTENT){
            if(unexpectedCharsIndex < 0) throw new IllegalArgumentException();
            clearUnexpectedCharacterContent(messageId);
        }else if(errorId == UNEXPECTED_ATTRIBUTE_VALUE){
            if(unexpectedAVIndex < 0) throw new IllegalArgumentException();
            clearUnexpectedAttributeValue(messageId);
        }else if(errorId == UNRESOLVED_CHARACTER_CONTENT){
            if(unresolvedCharsIndexEE < 0) throw new IllegalArgumentException();
            clearUnresolvedCharacterContent(messageId);
        }else if(errorId == UNRESOLVED_ATTRIBUTE_VALUE){
            if(unresolvedAVIndexEE < 0) throw new IllegalArgumentException();
            clearUnresolvedAttributeValue(messageId);
        }else if(errorId == LIST_TOKEN_DATATYPE_ERROR){
            if(datatypeTokenIndex < 0) throw new IllegalArgumentException();
            clearListTokenDatatypeError(messageId);
        }else if(errorId == LIST_TOKEN_VALUE_ERROR){
            if(valueTokenIndex < 0) throw new IllegalArgumentException();
            clearListTokenValueError(messageId);
        }else if(errorId == LIST_TOKEN_EXCEPTED_ERROR){
            if(exceptTokenIndex < 0) throw new IllegalArgumentException();
            clearListTokenExceptedError(messageId);
        }else if(errorId == UNRESOLVED_LIST_TOKEN_IN_CONTEXT_ERROR){
            if(unresolvedTokenIndexLPICE < 0) throw new IllegalArgumentException();
            clearUnresolvedListTokenInContextError(messageId);
        }else if(errorId == MISSING_COMPOSITOR_CONTENT){
            if(missingCompositorContentIndex < 0) throw new IllegalArgumentException();
            clearMissingCompositorContent(messageId);
        }else if(errorId == CONFLICT){
            if(disqualified == null) throw new IllegalArgumentException();
            clearConflict();
        }else{
            throw new IllegalArgumentException();
        }
    }
    
    void transferErrorMessage(int errorId, int messageId, ConflictMessageHandler other){
        // TODO add return codes for feedback and control 		
        if(errorId == UNKNOWN_ELEMENT){
            if(unknownElementIndex < 0) return;
            transferUnknownElement(messageId, other);            
        }else if(errorId == UNEXPECTED_ELEMENT){        
            if(unexpectedElementIndex < 0) return;
            transferUnexpectedElement(messageId, other);
        }else if(errorId == UNEXPECTED_AMBIGUOUS_ELEMENT){
            if(unexpectedAmbiguousElementIndex < 0) return;
            transferUnexpectedAmbiguousElement(messageId, other);
        }else if(errorId == UNKNOWN_ATTRIBUTE){
            if(unknownAttributeIndex < 0) return;
            transferUnknownAttribute(messageId, other);
        }else if(errorId == UNEXPECTED_ATTRIBUTE){
            if(unexpectedAttributeIndex < 0) return;
            transferUnexpectedAttribute(messageId, other);
        }else if(errorId == UNEXPECTED_AMBIGUOUS_ATTRIBUTE){
            if(unexpectedAmbiguousAttributeIndex < 0) return;
            transferUnexpectedAmbiguousAttribute(messageId, other);
        }else if(errorId == MISPLACED_ELEMENT){
            if(misplacedIndex < 0) return;
            transferMisplacedContent(messageId, other);
        }else if(errorId == EXCESSIVE_CONTENT){
            if(excessiveIndex < 0) return;
            transferExcessiveContent(messageId, other);
        }else if(errorId == UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR){
            if(unresolvedAmbiguousElementIndexEE < 0) return;
            transferUnresolvedAmbiguousElementContentError(messageId, other);
        }else if(errorId == UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR){
            if(unresolvedUnresolvedElementIndexEE < 0) return;
            transferUnresolvedUnresolvedElementContentError(messageId, other);
        }else if(errorId == UNRESOLVED_ATTRIBUTE_CONTENT_ERROR){
            if(unresolvedAttributeIndexEE < 0) return;
            transferUnresolvedAttributeContentError(messageId, other);
        }else if(errorId == MISSING_CONTENT){
            if(missingIndex < 0) return;
            transferMissingContent(messageId, other);
        }else if(errorId == ILLEGAL_CONTENT){
            if(illegalIndex < 0) return;
            transferIllegalContent(messageId, other);
        }else if(errorId == UNDETERMINED_BY_CONTENT){
            return;
        }else if(errorId == CHARACTER_CONTENT_DATATYPE_ERROR){
            if(datatypeCharsIndex < 0) return;
            transferCharacterContentDatatypeError(messageId, other);
        }else if(errorId == ATTRIBUTE_VALUE_DATATYPE_ERROR){
            if(datatypeAVIndex < 0) return;
            transferAttributeValueDatatypeError(messageId, other);
        }else if(errorId == CHARACTER_CONTENT_VALUE_ERROR){
            if(valueCharsIndex < 0) return;
            transferCharacterContentValueError(messageId, other);
        }else if(errorId == ATTRIBUTE_VALUE_VALUE_ERROR){
            if(valueAVIndex < 0) return;
            transferAttributeValueValueError(messageId, other);
        }else if(errorId == CHARACTER_CONTENT_EXCEPTED_ERROR){
            if(exceptCharsIndex < 0) return;
            transferCharacterContentExceptedError(messageId, other);
        }else if(errorId == ATTRIBUTE_VALUE_EXCEPTED_ERROR){
            if(exceptAVIndex < 0) return;
            transferAttributeValueExceptedError(messageId, other);
        }else if(errorId == UNEXPECTED_CHARACTER_CONTENT){
            if(unexpectedCharsIndex < 0) return;
            transferUnexpectedCharacterContent(messageId, other);
        }else if(errorId == UNEXPECTED_ATTRIBUTE_VALUE){
            if(unexpectedAVIndex < 0) return;
            transferUnexpectedAttributeValue(messageId, other);
        }else if(errorId == UNRESOLVED_CHARACTER_CONTENT){
            if(unresolvedCharsIndexEE < 0) return;
            transferUnresolvedCharacterContent(messageId, other);
        }else if(errorId == UNRESOLVED_ATTRIBUTE_VALUE){
            if(unresolvedAVIndexEE < 0) return;
            transferUnresolvedAttributeValue(messageId, other);
        }else if(errorId == LIST_TOKEN_DATATYPE_ERROR){
            if(datatypeTokenIndex < 0) return;
            transferListTokenDatatypeError(messageId, other);
        }else if(errorId == LIST_TOKEN_VALUE_ERROR){
            if(valueTokenIndex < 0) return;
            transferListTokenValueError(messageId, other);
        }else if(errorId == LIST_TOKEN_EXCEPTED_ERROR){
            if(exceptTokenIndex < 0) return;
            transferListTokenExceptedError(messageId, other);
        }else if(errorId == UNRESOLVED_LIST_TOKEN_IN_CONTEXT_ERROR){
            if(unresolvedTokenIndexLPICE < 0) return;
            transferUnresolvedListTokenInContextError(messageId, other);
        }else if(errorId == MISSING_COMPOSITOR_CONTENT){
            if(missingCompositorContentIndex < 0) return;
            transferMissingCompositorContent(messageId, other);
        }else if(errorId == CONFLICT){
            if(disqualified == null) return;
            transferConflict(messageId, other);
        }else{
            return;
        }
    }
        
    
    void clearWarningMessage(int warningId, int messageId){        
        if(warningId == AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING){
            if(ambiguousUnresolvedElementIndexWW < 0) throw new IllegalArgumentException();
            clearAmbiguousUnresolvedElementContentWarning(messageId);
        }else if(warningId == AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING){
            if(ambiguousAmbiguousElementIndexWW < 0) throw new IllegalArgumentException();
            clearAmbiguousAmbiguousElementContentWarning(messageId);
        }else if(warningId == AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING){
            if(ambiguousAttributeIndexWW < 0) throw new IllegalArgumentException();
            clearAmbiguousAttributeContentWarning(messageId);
        }else if(warningId == AMBIGUOUS_CHARACTER_CONTENT_WARNING){
            if(ambiguousCharsIndexWW < 0) throw new IllegalArgumentException();
            clearAmbiguousCharacterContentWarning(messageId);
        }else if(warningId == AMBIGUOUS_ATTRIBUTE_VALUE_WARNING){
            if(ambiguousAVIndexWW < 0) throw new IllegalArgumentException();
            clearAmbiguousAttributeValueWarning(messageId);
        }else if(warningId == AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING){            
            if(ambiguousTokenIndexLPICW < 0) throw new IllegalArgumentException();
            clearAmbiguousListTokenInContextWarning(messageId);
        }else{
            throw new IllegalArgumentException();
        }
    }
    
    void transferWarningMessage(int warningId, int messageId, ConflictMessageHandler other){        
        if(warningId == AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING){
            //if(ambiguousUnresolvedElementIndexWW < 0) throw new IllegalArgumentException();
            transferAmbiguousUnresolvedElementContentWarning(messageId, other);
        }else if(warningId == AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING){
            if(ambiguousAmbiguousElementIndexWW < 0) throw new IllegalArgumentException();
            transferAmbiguousAmbiguousElementContentWarning(messageId, other);
        }else if(warningId == AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING){
            if(ambiguousAttributeIndexWW < 0) throw new IllegalArgumentException();
            transferAmbiguousAttributeContentWarning(messageId, other);
        }else if(warningId == AMBIGUOUS_CHARACTER_CONTENT_WARNING){
            if(ambiguousCharsIndexWW < 0) throw new IllegalArgumentException();
            transferAmbiguousCharacterContentWarning(messageId, other);
        }else if(warningId == AMBIGUOUS_ATTRIBUTE_VALUE_WARNING){
            if(ambiguousAVIndexWW < 0) throw new IllegalArgumentException();
            transferAmbiguousAttributeValueWarning(messageId, other);
        }else if(warningId == AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING){            
            if(ambiguousTokenIndexLPICW < 0) throw new IllegalArgumentException();
            transferAmbiguousListTokenInContextWarning(messageId, other);
        }else{
            throw new IllegalArgumentException();
        }
    }
    
    public void unregisterClient(MessageReporter mr){
        clientCount--;
        if(clientCount == 0){
            clear();
        }
    }    
    public void clear(ContextErrorHandler ec){
        if(clientCount > 0) return;
        clear();
    }
    public void clear(CandidatesConflictErrorHandler cceh){
        if(clientCount > 0) return;
        clear();
    } 
    public void clear(TemporaryMessageStorage tms){
        if(clientCount > 0) return;
        clear();
    }   
    public void clear(ElementConflictResolver ecr){
        if(clientCount > 0) return;
        clear();
    }   
    private void clear(){
        // TODO check sizes to only clear when full
        // and refactor the creation of new instances in the ErrorHandlers
        if( unknownElementIndex >= 0 ) clearUnknownElement();
		if( unexpectedElementIndex >= 0) clearUnexpectedElement();
		if( unexpectedAmbiguousElementIndex >= 0) clearUnexpectedAmbiguousElement();
		if( unknownAttributeIndex >= 0 ) clearUnknownAttribute();
		if( unexpectedAttributeIndex >= 0) clearUnexpectedAttribute();
		if( unexpectedAmbiguousAttributeIndex >= 0) clearUnexpectedAmbiguousAttribute();   
        if( misplacedIndex >= 0 ) clearMisplacedContent();
        if( excessiveIndex >= 0) clearExcessiveContent();
        if( missingIndex >= 0) clearMissingContent();
        if( illegalIndex >= 0) clearIllegalContent();
        if( unresolvedAmbiguousElementIndexEE >= 0) clearUnresolvedAmbiguousElementContentError();
        if( unresolvedUnresolvedElementIndexEE >= 0) clearUnresolvedUnresolvedElementContentError();
        if( unresolvedAttributeIndexEE >= 0) clearUnresolvedAttributeContentError();
        
        if( ambiguousUnresolvedElementIndexWW >= 0) clearAmbiguousUnresolvedElementContentWarning();
        if( ambiguousAmbiguousElementIndexWW >= 0) clearAmbiguousAmbiguousElementContentWarning();        
        if( ambiguousAttributeIndexWW >= 0) clearAmbiguousAttributeContentWarning();
        if( ambiguousCharsIndexWW >= 0) clearAmbiguousCharacterContentWarning();
        if( ambiguousAVIndexWW >= 0) clearAmbiguousAttributeValueWarning();
        
        if( datatypeCharsIndex >= 0) clearCharacterContentDatatypeError();
        if( datatypeAVIndex >= 0) clearAttributeValueDatatypeError();
        if( valueCharsIndex >= 0) clearCharacterContentValueError();
        if( valueAVIndex >= 0) clearAttributeValueValueError();
        if( exceptCharsIndex >= 0) clearCharacterContentExceptedError();
        if( exceptAVIndex >= 0) clearAttributeValueExceptedError();
        if( unexpectedCharsIndex >= 0) clearUnexpectedCharacterContent();
        if( unexpectedAVIndex >= 0) clearUnexpectedAttributeValue();
        if( unresolvedCharsIndexEE >= 0) clearUnresolvedCharacterContent();
        if( unresolvedAVIndexEE >= 0) clearUnresolvedAttributeValue();
        if( datatypeTokenIndex >= 0) clearListTokenDatatypeError();
        if( valueTokenIndex >= 0) clearListTokenValueError();
        if( exceptTokenIndex >= 0) clearListTokenExceptedError();
        if( unresolvedTokenIndexLPICE >= 0) clearUnresolvedListTokenInContextError();
        if( ambiguousTokenIndexLPICW >= 0) clearAmbiguousListTokenInContextWarning();
        if( missingCompositorContentIndex >= 0) clearMissingCompositorContent();
        clearConflict();
        
        messageTotalCount = 0;
        
        if(parent != null){
            parent.unregisterClient(this);
            parent = null;
        }
    
        reportingContextType = ContextErrorHandler.NONE;
        reportingContextQName = null;
        reportingContextDefinition = null;
        reportingContextPublicId = null;
        reportingContextSystemId = null;
        reportingContextLineNumber = -1;
        reportingContextColumnNumber = -1;
        conflictResolutionId = UNRESOLVED;
        
    }
    
    public boolean containsOtherErrorMessage(IntList exceptedErrorIds, IntList exceptedErrorCodes){
        
	    if(unknownElementIndex >= 0){
	        if(!exceptedErrorIds.contains(UNKNOWN_ELEMENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= unknownElementIndex; i++){
	                if(!exceptedErrorCodes.contains(unknownElementFEC[i]))return true;
	            }
	        }
	    }
	    if( unexpectedElementIndex >= 0){
	        if(!exceptedErrorIds.contains(UNEXPECTED_ELEMENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= unexpectedElementIndex; i++){
	                if(!exceptedErrorCodes.contains(unexpectedElementFEC[i]))return true;
	            }
	        }
	    }
	    if(unexpectedAmbiguousElementIndex  >= 0){
	        if(!exceptedErrorIds.contains(UNEXPECTED_AMBIGUOUS_ELEMENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= unexpectedAmbiguousElementIndex; i++){
	                if(!exceptedErrorCodes.contains(unexpectedAmbiguousElementFEC[i]))return true;
	            }
	        }
	    }
	    if(unknownAttributeIndex  >= 0){
	        if(!exceptedErrorIds.contains(UNKNOWN_ATTRIBUTE)){
	            return true;
	        }else{
	            for(int i = 0; i <= unknownAttributeIndex; i++){
	                if(!exceptedErrorCodes.contains(unknownAttributeFEC[i]))return true;
	            }
	        }
	    }
	    if(unexpectedAttributeIndex  >= 0){
	        if(!exceptedErrorIds.contains(UNEXPECTED_ATTRIBUTE)){
	            return true;
	        }else{
	            for(int i = 0; i <= unexpectedAttributeIndex; i++){
	                if(!exceptedErrorCodes.contains(unexpectedAttributeFEC[i]))return true;
	            }
	        }
	    }
	    if(unexpectedAmbiguousAttributeIndex >= 0){
	        if(!exceptedErrorIds.contains(UNEXPECTED_AMBIGUOUS_ATTRIBUTE)){
	            return true;
	        }else{
	            for(int i = 0; i <= unexpectedAmbiguousAttributeIndex; i++){
	                if(!exceptedErrorCodes.contains(unexpectedAmbiguousAttributeFEC[i]))return true;
	            }
	        }
	    }
	    if(misplacedIndex  >= 0){
	        if(!exceptedErrorIds.contains(MISPLACED_ELEMENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= misplacedIndex; i++){
	                if(!exceptedErrorCodes.contains(misplacedFEC[i]))return true;
	            }
	        }
	    }
	    if(excessiveIndex  >= 0){
	        if(!exceptedErrorIds.contains(EXCESSIVE_CONTENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= excessiveIndex; i++){
	                if(!exceptedErrorCodes.contains(excessiveFEC[i]))return true;
	            }
	        }
	    }
	    if(missingIndex >= 0){
	        if(!exceptedErrorIds.contains(MISSING_CONTENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= missingIndex; i++){
	                if(!exceptedErrorCodes.contains(missingFEC[i])){
	                    return true;
	                }
	            }
	        }
	    }
	    if(illegalIndex >= 0){
	        if(!exceptedErrorIds.contains(ILLEGAL_CONTENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= illegalIndex; i++){
	                if(!exceptedErrorCodes.contains(illegalFEC[i]))return true;
	            }
	        }
	    }
	    if(unresolvedAmbiguousElementIndexEE >= 0){
	        if(!exceptedErrorIds.contains(UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
	                if(!exceptedErrorCodes.contains(unresolvedAmbiguousElementFECEE[i]))return true;
	            }
	        }
	    }
	    if(unresolvedUnresolvedElementIndexEE >= 0){
	        if(!exceptedErrorIds.contains(UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
	                if(!exceptedErrorCodes.contains(unresolvedUnresolvedElementFECEE[i]))return true;
	            }
	        }
	    }
	    if(unresolvedAttributeIndexEE >= 0){
	        if(!exceptedErrorIds.contains(UNRESOLVED_ATTRIBUTE_CONTENT_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
	                if(!exceptedErrorCodes.contains(unresolvedAttributeFECEE[i]))return true;
	            }
	        }
	    }
	    if(datatypeCharsIndex >= 0){
	        if(!exceptedErrorIds.contains(CHARACTER_CONTENT_DATATYPE_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= datatypeCharsIndex; i++){
	                if(!exceptedErrorCodes.contains(datatypeCharsFEC[i]))return true;
	            }
	        }
	    }
	    if(datatypeAVIndex >= 0){
	        if(!exceptedErrorIds.contains(ATTRIBUTE_VALUE_DATATYPE_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= datatypeAVIndex; i++){
	                if(!exceptedErrorCodes.contains(datatypeAVFEC[i]))return true;
	            }
	        }
	    }
	    if(valueCharsIndex >= 0){
	        if(!exceptedErrorIds.contains(CHARACTER_CONTENT_VALUE_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= valueCharsIndex; i++){
	                if(!exceptedErrorCodes.contains(valueCharsFEC[i]))return true;
	            }
	        }
	    }
	    if(valueAVIndex >= 0){
	        if(!exceptedErrorIds.contains(ATTRIBUTE_VALUE_VALUE_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= valueAVIndex; i++){
	                if(!exceptedErrorCodes.contains(valueAVFEC[i]))return true;
	            }
	        }
	    }
	    if(exceptCharsIndex >= 0){
	        if(!exceptedErrorIds.contains(CHARACTER_CONTENT_EXCEPTED_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= exceptCharsIndex; i++){
	                if(!exceptedErrorCodes.contains(exceptCharsFEC[i]))return true;
	            }
	        }
	    }
	    if(exceptAVIndex >= 0){
	        if(!exceptedErrorIds.contains(ATTRIBUTE_VALUE_EXCEPTED_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= exceptAVIndex; i++){
	                if(!exceptedErrorCodes.contains(exceptAVFEC[i]))return true;
	            }
	        }
	    }
	    if(unexpectedCharsIndex >= 0){
	        if(!exceptedErrorIds.contains(UNEXPECTED_CHARACTER_CONTENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= unexpectedCharsIndex; i++){
	                if(!exceptedErrorCodes.contains(unexpectedCharsFEC[i]))return true;
	            }
	        }
	    }
	    if(unexpectedAVIndex >= 0){
	        if(!exceptedErrorIds.contains(UNEXPECTED_ATTRIBUTE_VALUE)){
	            return true;
	        }else{
	            for(int i = 0; i <= unexpectedAVIndex; i++){
	                if(!exceptedErrorCodes.contains(unexpectedAVFEC[i]))return true;
	            }
	        }
	    }
	    if(unresolvedCharsIndexEE >= 0){
	        if(!exceptedErrorIds.contains(UNRESOLVED_CHARACTER_CONTENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= unresolvedCharsIndexEE; i++){
	                if(!exceptedErrorCodes.contains(unresolvedCharsFECEE[i]))return true;
	            }
	        }
	    }
	    if(unresolvedAVIndexEE >= 0){
	        if(!exceptedErrorIds.contains(UNEXPECTED_ATTRIBUTE_VALUE)){
	            return true;
	        }else{
	            for(int i = 0; i <= unresolvedAVIndexEE; i++){
	                if(!exceptedErrorCodes.contains(unresolvedAVFECEE[i]))return true;
	            }
	        }
	    }
	    if(datatypeTokenIndex >= 0){
	        if(!exceptedErrorIds.contains(LIST_TOKEN_DATATYPE_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= datatypeTokenIndex; i++){
	                if(!exceptedErrorCodes.contains(datatypeTokenFEC[i]))return true;
	            }
	        }
	    }
	    if(valueTokenIndex >= 0){
	        if(!exceptedErrorIds.contains(LIST_TOKEN_VALUE_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= valueTokenIndex; i++){
	                if(!exceptedErrorCodes.contains(valueTokenFEC[i]))return true;
	            }
	        }
	    }
	    if(exceptTokenIndex >= 0){
	        if(!exceptedErrorIds.contains(LIST_TOKEN_EXCEPTED_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= exceptTokenIndex; i++){
	                if(!exceptedErrorCodes.contains(exceptTokenFEC[i]))return true;
	            }
	        }
	    }
	    if(unresolvedTokenIndexLPICE >= 0){
	        if(!exceptedErrorIds.contains(UNRESOLVED_LIST_TOKEN_IN_CONTEXT_ERROR)){
	            return true;
	        }else{
	            for(int i = 0; i <= unresolvedTokenIndexLPICE; i++){
	                if(!exceptedErrorCodes.contains(unresolvedTokenFECLPICE[i]))return true;
	            }
	        }
	    }
	    if(missingCompositorContentIndex >= 0){
	        if(!exceptedErrorIds.contains(MISSING_COMPOSITOR_CONTENT)){
	            return true;
	        }else{
	            for(int i = 0; i <= missingCompositorContentIndex; i++){
	                if(!exceptedErrorCodes.contains(missingCompositorContentFEC[i]))return true;
	            }
	        }
	    }
	    if(commonMessages != null && commonMessages.containsErrorMessage())return true;
	    if(disqualified != null && disqualified.cardinality() == candidatesCount){
	        if(exceptedErrorCodes.contains(conflictFEC)) return false;
	        return true;
	    }else if(disqualified != null){	        
	        for(int i = 0; i < candidatesCount; i++){	            
	            if(!disqualified.get(i)
	                && candidateMessages[i] != null
	            && candidateMessages[i].containsErrorMessage()){
	                return true;
	            }
	        }
	    }
	    
	    if(parent != null)return parent.containsErrorMessage();
	    return false;
    }
}
