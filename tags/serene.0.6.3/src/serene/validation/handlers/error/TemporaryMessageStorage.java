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

import java.io.File;

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
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

public class TemporaryMessageStorage  implements ErrorCatcher{	
    
    ActiveInputDescriptor activeInputDescriptor;
        
    int initialSize = 5;
    int increaseSizeAmount = 5;
    
	// {1}
	String undeterminedQName;
	String undeterminedCandidateMessages;
		
	// {2}
	int[] unknownElementInputRecordIndex;
	int unknownElementIndex;
	
	// {3}
	SimplifiedComponent[] unexpectedElementDefinition;
	int[] unexpectedElementInputRecordIndex;
	int unexpectedElementIndex;
	
	// {4}
	SimplifiedComponent[][] unexpectedAmbiguousElementDefinition;
	int[] unexpectedAmbiguousElementInputRecordIndex;
	int unexpectedAmbiguousElementIndex;
	
	// {5}
	int[] unknownAttributeInputRecordIndex;
	int unknownAttributeIndex;
	
	// {6}
	SimplifiedComponent[] unexpectedAttributeDefinition;
	int[] unexpectedAttributeInputRecordIndex;
	int unexpectedAttributeIndex;
	
	// {7}
	SimplifiedComponent[][] unexpectedAmbiguousAttributeDefinition;
	int[] unexpectedAmbiguousAttributeInputRecordIndex;
	int unexpectedAmbiguousAttributeIndex;
	
	
	// {8}
	SPattern[] misplacedContext;
	int[] misplacedStartInputRecordIndex;
	SPattern[][] misplacedDefinition;
	int[][][] misplacedInputRecordIndex;
	int misplacedIndex;

	// {9}
	SRule[] excessiveContext;
	int[] excessiveStartInputRecordIndex;
	SPattern[] excessiveDefinition;
	int[][] excessiveInputRecordIndex;
	int excessiveIndex;
	
	
	// {10}
	SRule[] missingContext;
	int[] missingStartInputRecordIndex;
	SPattern[] missingDefinition;
	int[] missingExpected;
	int[] missingFound;
	int[][] missingInputRecordIndex;
	int missingIndex;
	
	
	// {11}
	SRule[] illegalContext;
	int[] illegalStartInputRecordIndex;
	int illegalIndex;
	
	
	// {12 A}
	int[] unresolvedAmbiguousElementInputRecordIndexEE;
	SElement[][] unresolvedAmbiguousElementDefinitionEE;
	int unresolvedAmbiguousElementIndexEE;
	
	
	// {12 U}
	int[] unresolvedUnresolvedElementInputRecordIndexEE;
	SElement[][] unresolvedUnresolvedElementDefinitionEE;
	int unresolvedUnresolvedElementIndexEE;

	// {13}
	int[] unresolvedAttributeInputRecordIndexEE;
	SAttribute[][] unresolvedAttributeDefinitionEE;
	int unresolvedAttributeIndexEE;
	

	// {14}
	
	
	// {w1 U}
	int[] ambiguousUnresolvedElementInputRecordIndexWW;
	SElement[][] ambiguousUnresolvedElementDefinitionWW;
	int ambiguousUnresolvedElementIndexWW;
	
	
	// {w1 A}
	int[] ambiguousAmbiguousElementInputRecordIndexWW;
	SElement[][] ambiguousAmbiguousElementDefinitionWW;
	int ambiguousAmbiguousElementIndexWW;
	
	
	
	// {w2}
	int[] ambiguousAttributeInputRecordIndexWW;
	SAttribute[][] ambiguousAttributeDefinitionWW;
	int ambiguousAttributeIndexWW;
	

	// {w3}
	int[] ambiguousCharsInputRecordIndexWW;
	SPattern[][] ambiguousCharsDefinitionWW;
	int ambiguousCharsIndexWW;
	
	
	// {w4}
	int[] ambiguousAVInputRecordIndexWW;
	SPattern[][] ambiguousAVDefinitionWW;
	int ambiguousAVIndexWW;
		
	
	// {15}
	int[] datatypeCharsInputRecordIndex;
	SPattern[] datatypeCharsDefinition;
	String datatypeCharsErrorMessage[];
	int datatypeCharsIndex;
	
	
	// {16}
	int[] datatypeAVInputRecordIndex;
	SPattern[] datatypeAVDefinition;
	String datatypeAVErrorMessage[];
	int datatypeAVIndex;
   
	
	// {17}
	int[] valueCharsInputRecordIndex;
	SValue[] valueCharsDefinition;
	int valueCharsIndex;
	
	// {18}
	int[] valueAVInputRecordIndex;
	SValue[] valueAVDefinition;
	int valueAVIndex;
	
	// {19}
	int[] exceptCharsInputRecordIndex;
	SData[] exceptCharsDefinition;
	int exceptCharsIndex;
	
	// {20}
	int[] exceptAVInputRecordIndex;
	SData[] exceptAVDefinition;
	int exceptAVIndex;
	
	// {21}
	int[] unexpectedCharsInputRecordIndex;
	SElement[] unexpectedCharsDefinition;
	int unexpectedCharsIndex;
	
	// {22}
	int[] unexpectedAVInputRecordIndex;
	SAttribute[] unexpectedAVDefinition;
	int unexpectedAVIndex;
	
	
	// {23}
	int[] unresolvedCharsInputRecordIndexEE;
	SPattern[][] unresolvedCharsDefinitionEE;
	int unresolvedCharsIndexEE;
	
	
	// {24}
	int[] unresolvedAVInputRecordIndexEE;
	SPattern[][] unresolvedAVDefinitionEE;
	int unresolvedAVIndexEE;
	
	
	// {25}
	int[] datatypeTokenInputRecordIndex;
	SPattern[] datatypeTokenDefinition;
	String datatypeTokenErrorMessage[];
	int datatypeTokenIndex;
	
    	
	// {26}
	int[] valueTokenInputRecordIndex;
	SValue[] valueTokenDefinition;
	int valueTokenIndex;
	
	
	// {27}
	int[] exceptTokenInputRecordIndex;
	SData[] exceptTokenDefinition;
	int exceptTokenIndex;
	
	
	// {28}
	
    // {28_1}
	int[] unresolvedTokenInputRecordIndexLPICE;
    SPattern unresolvedTokenDefinitionLPICE[][];
	int unresolvedTokenIndexLPICE;
    
    
    // {28_2}
	int[] ambiguousTokenInputRecordIndexLPICW;
    SPattern ambiguousTokenDefinitionLPICW[][];
	int ambiguousTokenIndexLPICW;
    
	
	// {29}
	SRule[] missingCompositorContentContext;
	int[] missingCompositorContentStartInputRecordIndex;
	SPattern[] missingCompositorContentDefinition;
	int[] missingCompositorContentExpected;
	int[] missingCompositorContentFound;
	int missingCompositorContentIndex;
	
    
    // {30}
    boolean conflict;
    int conflictResolutionId;
    MessageReporter commonMessages; // It can be only one because it is about this context.
    int candidatesCount;
    BitSet disqualified;
    MessageReporter[] candidateMessages;
    
    boolean internalConflict;
    ConflictMessageReporter conflictMessageReporter;
    
    boolean isDiscarded;
    boolean isClear;
    
	public TemporaryMessageStorage(){				
		// {2}
        unknownElementIndex = -1;
        
        // {3}
        unexpectedElementIndex = -1;
        
        // {4}
        unexpectedAmbiguousElementIndex = -1;
        
        // {5}
        unknownAttributeIndex = -1;
        
        // {6}
        unexpectedAttributeIndex = -1;
        
        // {7}
        unexpectedAmbiguousAttributeIndex = -1;
        
        
        // {8}
        misplacedIndex = -1;
    
        // {9}
        excessiveIndex = -1;
                
        // {10}
        missingIndex = -1;
        
        
        // {11}	
        illegalIndex = -1;
        
        // {12 A}
        unresolvedAmbiguousElementIndexEE = -1;
        
        // {12 U}
        unresolvedUnresolvedElementIndexEE = -1;
    
        // {13}
        unresolvedAttributeIndexEE = -1;
    
        // {14}
        
        // {w1 U}
        ambiguousUnresolvedElementIndexWW = -1;
        
        // {w1 A}
        ambiguousAmbiguousElementIndexWW = -1;
        
        
        
        // {w2}
        ambiguousAttributeIndexWW = -1;
    
        // {w3}
        ambiguousCharsIndexWW = -1;
        
         // {w4}
        ambiguousAVIndexWW = -1;
        
        // {15}
        datatypeCharsIndex = -1;
        
        // {16}
        datatypeAVIndex = -1;
       
        
        // {17}
        valueCharsIndex = -1;
        
        // {18}
        valueAVIndex = -1;
        
        // {19}
        exceptCharsIndex = -1;
        
        // {20}
        exceptAVIndex = -1;
        
        // {21}
        unexpectedCharsIndex = -1;
        
        // {22}
        unexpectedAVIndex = -1;
        
        
        // {23}
        unresolvedCharsIndexEE = -1;
        
        // {24}
        unresolvedAVIndexEE = -1;
        
        
        // {25}
        datatypeTokenIndex = -1;
            
        // {26}
        valueTokenIndex = -1;
        
        // {27}
        exceptTokenIndex = -1;
        
        // {28}

        // {28_1}
        unresolvedTokenIndexLPICE = -1;
        
        
        // {28_2}
        ambiguousTokenIndexLPICW = -1;
        
        
        // {29}
        missingCompositorContentIndex = -1;
        
        // {30}
        
		conflict = false;
		
		internalConflict = false;
		
		isDiscarded = false;
		
		isClear = false;
		// set like this because if it's created it meance it will be used and 
		// it won't have to be set everytime an error is recorded
	}  
    
	public void init(ActiveInputDescriptor activeInputDescriptor){
	    this.activeInputDescriptor = activeInputDescriptor;
	}
	
	public void unknownElement(int inputRecordIndex){
        
		if(unknownElementInputRecordIndex == null){
		    unknownElementInputRecordIndex = new int[initialSize];
		}else if(++unknownElementIndex == unknownElementInputRecordIndex.length){
		    int[] increasedEIRI = new int[unknownElementInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unknownElementInputRecordIndex, 0, increasedEIRI, 0, unknownElementIndex);
			unknownElementInputRecordIndex = increasedEIRI;		    
		}
		unknownElementInputRecordIndex[unknownElementIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
	}
	public void clearUnknownElement(){
        activeInputDescriptor.unregisterClientForRecord(unknownElementInputRecordIndex, 0, unknownElementIndex+1, this);
        unknownElementIndex = -1;
        unknownElementInputRecordIndex = null;
    }
	
	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
        
		if(unexpectedElementInputRecordIndex == null){
		    unexpectedElementIndex = 0;
		    unexpectedElementInputRecordIndex = new int[initialSize];
		    unexpectedElementDefinition = new SimplifiedComponent[initialSize];
		}else if(++unexpectedElementIndex == unexpectedElementInputRecordIndex.length){
		    int[] increasedEIRI = new int[unexpectedElementInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unexpectedElementInputRecordIndex, 0, increasedEIRI, 0, unexpectedElementIndex);
			unexpectedElementInputRecordIndex = increasedEIRI;		
			
			SimplifiedComponent[] increasedDef = new SimplifiedComponent[unexpectedElementDefinition.length+increaseSizeAmount];
			System.arraycopy(unexpectedElementDefinition, 0, increasedDef, 0, unexpectedElementIndex);
			unexpectedElementDefinition = increasedDef;
		}	
		unexpectedElementDefinition[unexpectedElementIndex] = definition;
		unexpectedElementInputRecordIndex[unexpectedElementIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
	}
    public void clearUnexpectedElement(){
        activeInputDescriptor.unregisterClientForRecord(unexpectedElementInputRecordIndex, 0, unexpectedElementIndex+1, this);
        unexpectedElementIndex = -1;
        unexpectedElementInputRecordIndex = null;
        unexpectedElementDefinition = null;
    }
        
    
	public void unexpectedAmbiguousElement(SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
        
		if(unexpectedAmbiguousElementInputRecordIndex == null){
		    unexpectedAmbiguousElementIndex = 0;
		    unexpectedAmbiguousElementInputRecordIndex = new int[initialSize];
		    unexpectedAmbiguousElementDefinition = new SimplifiedComponent[initialSize][];
		}else if(++unexpectedAmbiguousElementIndex == unexpectedAmbiguousElementInputRecordIndex.length){
		    int[] increasedEIRI = new int[unexpectedAmbiguousElementInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unexpectedAmbiguousElementInputRecordIndex, 0, increasedEIRI, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementInputRecordIndex = increasedEIRI;		
			
			SimplifiedComponent[][] increasedDef = new SimplifiedComponent[unexpectedAmbiguousElementDefinition.length+increaseSizeAmount][];
			System.arraycopy(unexpectedAmbiguousElementDefinition, 0, increasedDef, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementDefinition = increasedDef;
		}	
		unexpectedAmbiguousElementDefinition[unexpectedAmbiguousElementIndex] = possibleDefinitions;
		unexpectedAmbiguousElementInputRecordIndex[unexpectedAmbiguousElementIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
	}
	public void clearUnexpectedAmbiguousElement(){
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousElementInputRecordIndex, 0, unexpectedAmbiguousElementIndex+1, this);
        unexpectedAmbiguousElementIndex = -1;
        unexpectedAmbiguousElementInputRecordIndex = null;
        unexpectedAmbiguousElementDefinition = null;
    }
    
	
	public void unknownAttribute(int inputRecordIndex){
        
		if(unknownAttributeInputRecordIndex == null){
		    unknownAttributeInputRecordIndex = new int[initialSize];
		}else if(++unknownAttributeIndex == unknownAttributeInputRecordIndex.length){
		    int[] increasedEIRI = new int[unknownAttributeInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unknownAttributeInputRecordIndex, 0, increasedEIRI, 0, unknownAttributeIndex);
			unknownAttributeInputRecordIndex = increasedEIRI;		    
		}
		unknownAttributeInputRecordIndex[unknownAttributeIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
	}
	public void clearUnknownAttribute(){
        activeInputDescriptor.unregisterClientForRecord(unknownAttributeInputRecordIndex, 0, unknownAttributeIndex+1, this);
        unknownAttributeIndex = -1;
        unknownAttributeInputRecordIndex = null;
    }
    
	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
        
		if(unexpectedAttributeInputRecordIndex == null){
		    unexpectedAttributeIndex = 0;
		    unexpectedAttributeInputRecordIndex = new int[initialSize];
		    unexpectedAttributeDefinition = new SimplifiedComponent[initialSize];
		}else if(++unexpectedAttributeIndex == unexpectedAttributeInputRecordIndex.length){
		    int[] increasedEIRI = new int[unexpectedAttributeInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unexpectedAttributeInputRecordIndex, 0, increasedEIRI, 0, unexpectedAttributeIndex);
			unexpectedAttributeInputRecordIndex = increasedEIRI;		
			
			SimplifiedComponent[] increasedDef = new SimplifiedComponent[unexpectedAttributeDefinition.length+increaseSizeAmount];
			System.arraycopy(unexpectedAttributeDefinition, 0, increasedDef, 0, unexpectedAttributeIndex);
			unexpectedAttributeDefinition = increasedDef;
		}	
		unexpectedAttributeDefinition[unexpectedAttributeIndex] = definition;
		unexpectedAttributeInputRecordIndex[unexpectedAttributeIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
	}
	public void clearUnexpectedAttribute(){
        activeInputDescriptor.unregisterClientForRecord(unexpectedAttributeInputRecordIndex, 0, unexpectedAttributeIndex+1, this);
        unexpectedAttributeIndex = -1;
        unexpectedAttributeInputRecordIndex = null;
        unexpectedAttributeDefinition = null;
    }
    
    
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
        
		if(unexpectedAmbiguousAttributeInputRecordIndex == null){
		    unexpectedAmbiguousAttributeIndex = 0;
		    unexpectedAmbiguousAttributeInputRecordIndex = new int[initialSize];
		    unexpectedAmbiguousAttributeDefinition = new SimplifiedComponent[initialSize][];
		}else if(++unexpectedAmbiguousAttributeIndex == unexpectedAmbiguousAttributeInputRecordIndex.length){
		    int[] increasedEIRI = new int[unexpectedAmbiguousAttributeInputRecordIndex.length+increaseSizeAmount];
			System.arraycopy(unexpectedAmbiguousAttributeInputRecordIndex, 0, increasedEIRI, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeInputRecordIndex = increasedEIRI;		
			
			SimplifiedComponent[][] increasedDef = new SimplifiedComponent[unexpectedAmbiguousAttributeDefinition.length+increaseSizeAmount][];
			System.arraycopy(unexpectedAmbiguousAttributeDefinition, 0, increasedDef, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeDefinition = increasedDef;
		}	
		unexpectedAmbiguousAttributeDefinition[unexpectedAmbiguousAttributeIndex] = possibleDefinitions;
		unexpectedAmbiguousAttributeInputRecordIndex[unexpectedAmbiguousAttributeIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
	}
	public void clearUnexpectedAmbiguousAttribute(){
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousAttributeInputRecordIndex, 0, unexpectedAmbiguousAttributeIndex+1, this);
        unexpectedAmbiguousAttributeIndex = -1;
        unexpectedAmbiguousAttributeInputRecordIndex = null;
        unexpectedAmbiguousAttributeDefinition = null;
    }
	
	
	public void misplacedContent(SPattern contextDefinition, 
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
                        
            misplacedContext[misplacedIndex] = contextDefinition;
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new SPattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = new int[1];
            misplacedInputRecordIndex[misplacedIndex][0][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
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
		}
	}
	public void misplacedContent(SPattern contextDefinition, 
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
                        
            misplacedContext[misplacedIndex] = contextDefinition;
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new SPattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
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
            }
            
            misplacedContext[misplacedIndex] = contextDefinition;
            
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new SPattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
		}
	}
    public void clearMisplacedElement(){
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
    }	
	    
	
	public void excessiveContent(SRule context,
									int startInputRecordIndex,
									SPattern definition, 
									int[] inputRecordIndex){
		
		if(excessiveIndex < 0){
			excessiveIndex = 0;
			excessiveContext = new SPattern[initialSize];		
			excessiveStartInputRecordIndex = new int[initialSize];
			excessiveDefinition = new SPattern[initialSize];
			excessiveInputRecordIndex = new int[initialSize][];		
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
		}
		excessiveContext[excessiveIndex] = context;
		excessiveStartInputRecordIndex[excessiveIndex] = startInputRecordIndex;
		activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		excessiveDefinition[excessiveIndex] = definition;
		excessiveInputRecordIndex[excessiveIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
	}   
	
	public void excessiveContent(SRule context, 
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
    }
    
	
	public void missingContent(SRule context,  
								int startInputRecordIndex,						 
								SPattern definition, 
								int expected, 
								int found, 
								int[] inputRecordIndex){	    
        
		
		if(missingIndex < 0){
			missingIndex = 0;
			missingContext = new SPattern[initialSize];			
			missingStartInputRecordIndex = new int[initialSize];
			missingDefinition = new SPattern[initialSize];
			missingExpected = new int[initialSize];
			missingFound = new int[initialSize];		
			missingInputRecordIndex = new int[initialSize][];		
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
		}	
		
		missingContext[missingIndex] = context;
		missingStartInputRecordIndex[missingIndex] = startInputRecordIndex;
		activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);            
		missingDefinition[missingIndex] = definition;
		missingExpected[missingIndex] = expected;
		missingFound[missingIndex] = found;		
		missingInputRecordIndex[missingIndex] = inputRecordIndex;
		if(inputRecordIndex != null)activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
    }
    public void clearMissingContent(){
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
    }
	


    public void illegalContent(SRule context, 
	                        int startInputRecordIndex){
        
		if(illegalIndex < 0){
			illegalIndex = 0;
			illegalContext = new SPattern[initialSize];
			illegalStartInputRecordIndex = new int[initialSize];					
		}else if(++illegalIndex == illegalContext.length){
		    int size = illegalIndex + increaseSizeAmount;
			SPattern[] increasedEC = new SPattern[size];
			System.arraycopy(illegalContext, 0, increasedEC, 0, illegalIndex);
			illegalContext = increasedEC;
			
			int[] increasedII = new int[size];
			System.arraycopy(illegalStartInputRecordIndex, 0, increasedII, 0, illegalIndex);
			illegalStartInputRecordIndex = increasedII;		
		}
		illegalContext[illegalIndex] = context;
		illegalStartInputRecordIndex[illegalIndex] = startInputRecordIndex;
		activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
	}
	public void clearIllegalContent(){
        
        for(int i = 0; i <= illegalIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(illegalStartInputRecordIndex[i], this);
        }
        
        illegalIndex = -1;
        illegalContext = null;
        illegalStartInputRecordIndex = null;
    }

	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, 
									SElement[] possibleDefinitions){
        		
		if(unresolvedAmbiguousElementIndexEE < 0){
			unresolvedAmbiguousElementIndexEE = 0;	
			unresolvedAmbiguousElementInputRecordIndexEE =new int[initialSize];
			unresolvedAmbiguousElementDefinitionEE = new SElement[initialSize][];
		}else if(++unresolvedAmbiguousElementIndexEE == unresolvedAmbiguousElementInputRecordIndexEE.length){
		    int size = unresolvedAmbiguousElementInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAmbiguousElementInputRecordIndexEE, 0, increasedCN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementInputRecordIndexEE = increasedCN;
		    
		    SElement[][] increasedDef = new SElement[size][];
			System.arraycopy(unresolvedAmbiguousElementDefinitionEE, 0, increasedDef, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementDefinitionEE = increasedDef;
		}
		
		unresolvedAmbiguousElementInputRecordIndexEE[unresolvedAmbiguousElementIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAmbiguousElementDefinitionEE[unresolvedAmbiguousElementIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedAmbiguousElementContentError(){
              
        for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAmbiguousElementInputRecordIndexEE[i], this);
        }
        
        unresolvedAmbiguousElementInputRecordIndexEE = null;
        unresolvedAmbiguousElementDefinitionEE = null;
        unresolvedAmbiguousElementIndexEE = -1;
    }
	    
    public void unresolvedUnresolvedElementContentError(int inputRecordIndex, 
									SElement[] possibleDefinitions){
        
		if(unresolvedUnresolvedElementIndexEE < 0){
			unresolvedUnresolvedElementIndexEE = 0;	
			unresolvedUnresolvedElementInputRecordIndexEE =new int[initialSize];
			unresolvedUnresolvedElementDefinitionEE = new SElement[initialSize][];
		}else if(++unresolvedUnresolvedElementIndexEE == unresolvedUnresolvedElementInputRecordIndexEE.length){
		    int size = unresolvedUnresolvedElementInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedUnresolvedElementInputRecordIndexEE, 0, increasedCN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementInputRecordIndexEE = increasedCN;
		    
		    SElement[][] increasedDef = new SElement[size][];
			System.arraycopy(unresolvedUnresolvedElementDefinitionEE, 0, increasedDef, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementDefinitionEE = increasedDef;
		}
		
		unresolvedUnresolvedElementInputRecordIndexEE[unresolvedUnresolvedElementIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedUnresolvedElementDefinitionEE[unresolvedUnresolvedElementIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedUnresolvedElementContentError(){
        
        for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedUnresolvedElementInputRecordIndexEE[i], this);
        }
        
        unresolvedUnresolvedElementInputRecordIndexEE = null;
        unresolvedUnresolvedElementDefinitionEE = null;
        unresolvedUnresolvedElementIndexEE = -1;    
    }
    
	    
	public void unresolvedAttributeContentError(int inputRecordIndex, 
									SAttribute[] possibleDefinitions){
        
		if(unresolvedAttributeIndexEE < 0){
			unresolvedAttributeIndexEE = 0;	
			unresolvedAttributeInputRecordIndexEE =new int[initialSize];
			unresolvedAttributeDefinitionEE = new SAttribute[initialSize][];
		}else if(++unresolvedAttributeIndexEE == unresolvedAttributeInputRecordIndexEE.length){
		    int size = unresolvedAttributeInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAttributeInputRecordIndexEE, 0, increasedCN, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeInputRecordIndexEE = increasedCN;
		    
		    SAttribute[][] increasedDef = new SAttribute[size][];
			System.arraycopy(unresolvedAttributeDefinitionEE, 0, increasedDef, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeDefinitionEE = increasedDef;
		}
		
		unresolvedAttributeInputRecordIndexEE[unresolvedAttributeIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAttributeDefinitionEE[unresolvedAttributeIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedAttributeContentError(){
        
        for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAttributeInputRecordIndexEE[i], this);
        }
        
        unresolvedAttributeInputRecordIndexEE = null;
        unresolvedAttributeDefinitionEE = null;
        unresolvedAttributeIndexEE = -1;
    }
	

	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, 
									SElement[] possibleDefinitions){
        
        if(ambiguousUnresolvedElementIndexWW < 0){
			ambiguousUnresolvedElementIndexWW = 0;	
			ambiguousUnresolvedElementInputRecordIndexWW =new int[initialSize];
			ambiguousUnresolvedElementDefinitionWW = new SElement[initialSize][];
		}else if(++ambiguousUnresolvedElementIndexWW == ambiguousUnresolvedElementInputRecordIndexWW.length){
		    int size = ambiguousUnresolvedElementInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousUnresolvedElementInputRecordIndexWW, 0, increasedCN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementInputRecordIndexWW = increasedCN;
		    
		    SElement[][] increasedDef = new SElement[size][];
			System.arraycopy(ambiguousUnresolvedElementDefinitionWW, 0, increasedDef, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementDefinitionWW = increasedDef;
		}
		
		ambiguousUnresolvedElementInputRecordIndexWW[ambiguousUnresolvedElementIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousUnresolvedElementDefinitionWW[ambiguousUnresolvedElementIndexWW] = possibleDefinitions;        
	}
	public void clearAmbiguousUnresolvedElementContentWarning(){
        
        for(int i = 0; i <= ambiguousUnresolvedElementIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousUnresolvedElementInputRecordIndexWW[i], this);
        }
        
        ambiguousUnresolvedElementInputRecordIndexWW = null;
        ambiguousUnresolvedElementDefinitionWW = null;
        ambiguousUnresolvedElementIndexWW = -1;
    }
	    
    public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, 
									SElement[] possibleDefinitions){
        
        if(ambiguousAmbiguousElementIndexWW < 0){
			ambiguousAmbiguousElementIndexWW = 0;	
			ambiguousAmbiguousElementInputRecordIndexWW =new int[initialSize];
			ambiguousAmbiguousElementDefinitionWW = new SElement[initialSize][];
		}else if(++ambiguousAmbiguousElementIndexWW == ambiguousAmbiguousElementInputRecordIndexWW.length){
		    int size = ambiguousAmbiguousElementInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAmbiguousElementInputRecordIndexWW, 0, increasedCN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementInputRecordIndexWW = increasedCN;
		    
		    SElement[][] increasedDef = new SElement[size][];
			System.arraycopy(ambiguousAmbiguousElementDefinitionWW, 0, increasedDef, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementDefinitionWW = increasedDef;
		}
		
		ambiguousAmbiguousElementInputRecordIndexWW[ambiguousAmbiguousElementIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAmbiguousElementDefinitionWW[ambiguousAmbiguousElementIndexWW] = possibleDefinitions;        
	}
	public void clearAmbiguousAmbiguousElementContentWarning(){
        
        for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAmbiguousElementInputRecordIndexWW[i], this);
        }
        
        ambiguousAmbiguousElementInputRecordIndexWW = null;
        ambiguousAmbiguousElementDefinitionWW = null;
        ambiguousAmbiguousElementIndexWW = -1;
    }
    
	        
	public void ambiguousAttributeContentWarning(int inputRecordIndex, 
									SAttribute[] possibleDefinitions){
        		
		if(ambiguousAttributeIndexWW < 0){
			ambiguousAttributeIndexWW = 0;	
			ambiguousAttributeInputRecordIndexWW =new int[initialSize];
			ambiguousAttributeDefinitionWW = new SAttribute[initialSize][];
		}else if(++ambiguousAttributeIndexWW == ambiguousAttributeInputRecordIndexWW.length){
		    int size = ambiguousAttributeInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAttributeInputRecordIndexWW, 0, increasedCN, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeInputRecordIndexWW = increasedCN;
		    
		    SAttribute[][] increasedDef = new SAttribute[size][];
			System.arraycopy(ambiguousAttributeDefinitionWW, 0, increasedDef, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeDefinitionWW = increasedDef;
		}
		
		ambiguousAttributeInputRecordIndexWW[ambiguousAttributeIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAttributeDefinitionWW[ambiguousAttributeIndexWW] = possibleDefinitions;
	}	
	public void clearAmbiguousAttributeContentWarning(){
        
        for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAttributeInputRecordIndexWW[i], this);
        }
        
        ambiguousAttributeInputRecordIndexWW = null;
        ambiguousAttributeDefinitionWW = null;
        ambiguousAttributeIndexWW = -1;
    }
    
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, 
									SPattern[] possibleDefinitions){
        
		if(ambiguousCharsIndexWW < 0){
			ambiguousCharsIndexWW = 0;	
			ambiguousCharsInputRecordIndexWW =new int[initialSize];
			ambiguousCharsDefinitionWW = new SPattern[initialSize][];
		}else if(++ambiguousCharsIndexWW == ambiguousCharsInputRecordIndexWW.length){
		    int size = ambiguousCharsInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousCharsInputRecordIndexWW, 0, increasedCN, 0, ambiguousCharsIndexWW);
			ambiguousCharsInputRecordIndexWW = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(ambiguousCharsDefinitionWW, 0, increasedDef, 0, ambiguousCharsIndexWW);
			ambiguousCharsDefinitionWW = increasedDef;
		}
		
		ambiguousCharsInputRecordIndexWW[ambiguousCharsIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousCharsDefinitionWW[ambiguousCharsIndexWW] = possibleDefinitions;
	}
	public void clearAmbiguousCharacterContentWarning(){
        
        for(int i = 0; i <= ambiguousCharsIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousCharsInputRecordIndexWW[i], this);
        }
        
        ambiguousCharsInputRecordIndexWW = null;
        ambiguousCharsDefinitionWW = null;
        ambiguousCharsIndexWW = -1;
    }
    
    
	public void ambiguousAttributeValueWarning(int inputRecordIndex, 
									SPattern[] possibleDefinitions){
        
		if(ambiguousAVIndexWW < 0){
			ambiguousAVIndexWW = 0;	
			ambiguousAVInputRecordIndexWW =new int[initialSize];
			ambiguousAVDefinitionWW = new SPattern[initialSize][];
		}else if(++ambiguousAVIndexWW == ambiguousAVInputRecordIndexWW.length){
		    int size = ambiguousAVInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAVInputRecordIndexWW, 0, increasedCN, 0, ambiguousAVIndexWW);
			ambiguousAVInputRecordIndexWW = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(ambiguousAVDefinitionWW, 0, increasedDef, 0, ambiguousAVIndexWW);
			ambiguousAVDefinitionWW = increasedDef;
		}
		
		ambiguousAVInputRecordIndexWW[ambiguousAVIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAVDefinitionWW[ambiguousAVIndexWW] = possibleDefinitions;
	}
	public void clearAmbiguousAttributeValueWarning(){
        
        for(int i = 0; i <= ambiguousAVIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAVInputRecordIndexWW[i], this);
        }
        
        ambiguousAVInputRecordIndexWW = null;
        ambiguousAVDefinitionWW = null;
        ambiguousAVIndexWW = -1;
    }
	 
	    
    // {15}
	public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
	            
		if(datatypeCharsIndex < 0){
			datatypeCharsIndex = 0;	
			datatypeCharsInputRecordIndex =new int[initialSize];
			datatypeCharsDefinition = new SPattern[initialSize];
			datatypeCharsErrorMessage = new String[initialSize];
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
		}
		
		datatypeCharsInputRecordIndex[datatypeCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		datatypeCharsDefinition[datatypeCharsIndex] = charsDefinition;
		datatypeCharsErrorMessage[datatypeCharsIndex] = datatypeErrorMessage;
	}
	public void clearCharacterContentDatatypeError(){
        
        for(int i = 0; i <= datatypeCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeCharsInputRecordIndex[i], this);
        }
        
        datatypeCharsInputRecordIndex = null;
        datatypeCharsDefinition = null;
        datatypeCharsErrorMessage = null;
        datatypeCharsIndex = -1;
    }
    
	    
    //{16}
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        
		if(datatypeAVIndex < 0){
			datatypeAVIndex = 0;	
			datatypeAVInputRecordIndex =new int[initialSize];
			datatypeAVDefinition = new SPattern[initialSize];
			datatypeAVErrorMessage = new String[initialSize];
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
		}
		
		datatypeAVInputRecordIndex[datatypeAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		datatypeAVDefinition[datatypeAVIndex] = charsDefinition;
		datatypeAVErrorMessage[datatypeAVIndex] = datatypeErrorMessage;
	}
	public void clearAttributeValueDatatypeError(){
        
        for(int i = 0; i <= datatypeAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeAVInputRecordIndex[i], this);
        }
        
        datatypeAVInputRecordIndex = null;
        datatypeAVDefinition = null;
        datatypeAVErrorMessage = null;
        datatypeAVIndex = -1;
    }
    
	    
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
        
		if(valueCharsIndex < 0){
			valueCharsIndex = 0;	
			valueCharsInputRecordIndex =new int[initialSize];
			valueCharsDefinition = new SValue[initialSize];
		}else if(++valueCharsIndex == valueCharsInputRecordIndex.length){
		    int size = valueCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueCharsInputRecordIndex, 0, increasedCN, 0, valueCharsIndex);
			valueCharsInputRecordIndex = increasedCN;
		    
		    SValue[] increasedDef = new SValue[size];
			System.arraycopy(valueCharsDefinition, 0, increasedDef, 0, valueCharsIndex);
			valueCharsDefinition = increasedDef;
		}
		
		valueCharsInputRecordIndex[valueCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueCharsDefinition[valueCharsIndex] = charsDefinition;
	}
	public void clearCharacterContentValueError(){
        
        for(int i = 0; i <= valueCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueCharsInputRecordIndex[i], this);
        }
        
        valueCharsInputRecordIndex = null;
        valueCharsDefinition = null;
        valueCharsIndex = -1;
    }
        
    
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
        
		if(valueAVIndex < 0){
			valueAVIndex = 0;	
			valueAVInputRecordIndex =new int[initialSize];
			valueAVDefinition = new SValue[initialSize];
		}else if(++valueAVIndex == valueAVInputRecordIndex.length){
		    int size = valueAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueAVInputRecordIndex, 0, increasedCN, 0, valueAVIndex);
			valueAVInputRecordIndex = increasedCN;
		    
		    SValue[] increasedDef = new SValue[size];
			System.arraycopy(valueAVDefinition, 0, increasedDef, 0, valueAVIndex);
			valueAVDefinition = increasedDef;
		}
		
		valueAVInputRecordIndex[valueAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueAVDefinition[valueAVIndex] = charsDefinition;
	}
	public void clearAttributeValueValueError(){
        
        for(int i = 0; i <= valueAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueAVInputRecordIndex[i], this);
        }
        
        valueAVInputRecordIndex = null;
        valueAVDefinition = null;
        valueAVIndex = -1;
    }

    
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
        
		if(exceptCharsIndex < 0){
			exceptCharsIndex = 0;	
			exceptCharsInputRecordIndex =new int[initialSize];
			exceptCharsDefinition = new SData[initialSize];
		}else if(++exceptCharsIndex == exceptCharsInputRecordIndex.length){
		    int size = exceptCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptCharsInputRecordIndex, 0, increasedCN, 0, exceptCharsIndex);
			exceptCharsInputRecordIndex = increasedCN;
		    
		    SData[] increasedDef = new SData[size];
			System.arraycopy(exceptCharsDefinition, 0, increasedDef, 0, exceptCharsIndex);
			exceptCharsDefinition = increasedDef;
		}
		
		exceptCharsInputRecordIndex[exceptCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptCharsDefinition[exceptCharsIndex] = charsDefinition;
	}
    public void clearCharacterContentExceptedError(){
        
        for(int i = 0; i <= exceptCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptCharsInputRecordIndex[i], this);
        }
        
        exceptCharsInputRecordIndex = null;
        exceptCharsDefinition = null;
        exceptCharsIndex = -1;
    }
    
    
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
        
		if(exceptAVIndex < 0){
			exceptAVIndex = 0;	
			exceptAVInputRecordIndex =new int[initialSize];
			exceptAVDefinition = new SData[initialSize];
		}else if(++exceptAVIndex == exceptAVInputRecordIndex.length){
		    int size = exceptAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptAVInputRecordIndex, 0, increasedCN, 0, exceptAVIndex);
			exceptAVInputRecordIndex = increasedCN;
		    
		    SData[] increasedDef = new SData[size];
			System.arraycopy(exceptAVDefinition, 0, increasedDef, 0, exceptAVIndex);
			exceptAVDefinition = increasedDef;
		}
		
		exceptAVInputRecordIndex[exceptAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptAVDefinition[exceptAVIndex] = charsDefinition;
	}
	public void clearAttributeValueExceptedError(){
        
        for(int i = 0; i <= exceptAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptAVInputRecordIndex[i], this);
        }
        
        exceptAVInputRecordIndex = null;
        exceptAVDefinition = null;
        exceptAVIndex = -1;
    }
    
    
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
        
		if(unexpectedCharsIndex < 0){
			unexpectedCharsIndex = 0;	
			unexpectedCharsInputRecordIndex =new int[initialSize];
			unexpectedCharsDefinition = new SElement[initialSize];
		}else if(++unexpectedCharsIndex == unexpectedCharsInputRecordIndex.length){
		    int size = unexpectedCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unexpectedCharsInputRecordIndex, 0, increasedCN, 0, unexpectedCharsIndex);
			unexpectedCharsInputRecordIndex = increasedCN;
		    
		    SElement[] increasedDef = new SElement[size];
			System.arraycopy(unexpectedCharsDefinition, 0, increasedDef, 0, unexpectedCharsIndex);
			unexpectedCharsDefinition = increasedDef;
		}
		
		unexpectedCharsInputRecordIndex[unexpectedCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unexpectedCharsDefinition[unexpectedCharsIndex] = elementDefinition;
	}	
	public void clearUnexpectedCharacterContent(){
        
        for(int i = 0; i <= unexpectedCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(unexpectedCharsInputRecordIndex[i], this);
        }
        
        unexpectedCharsInputRecordIndex = null;
        unexpectedCharsDefinition = null;
        unexpectedCharsIndex = -1;
    }
    

	public void unexpectedAttributeValue(int inputRecordIndex, SAttribute attributeDefinition){
        
		if(unexpectedAVIndex < 0){
			unexpectedAVIndex = 0;	
			unexpectedAVInputRecordIndex =new int[initialSize];
			unexpectedAVDefinition = new SAttribute[initialSize];
		}else if(++unexpectedAVIndex == unexpectedAVInputRecordIndex.length){
		    int size = unexpectedAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unexpectedAVInputRecordIndex, 0, increasedCN, 0, unexpectedAVIndex);
			unexpectedAVInputRecordIndex = increasedCN;
		    
		    SAttribute[] increasedDef = new SAttribute[size];
			System.arraycopy(unexpectedAVDefinition, 0, increasedDef, 0, unexpectedAVIndex);
			unexpectedAVDefinition = increasedDef;
		}
		
		unexpectedAVInputRecordIndex[unexpectedAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unexpectedAVDefinition[unexpectedAVIndex] = attributeDefinition;
	}
	public void clearUnexpectedAttributeValue(){
        
        for(int i = 0; i <= unexpectedAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(unexpectedAVInputRecordIndex[i], this);
        }
        
        unexpectedAVInputRecordIndex = null;
        unexpectedAVDefinition = null;
        unexpectedAVIndex = -1;
    }
    

	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
        
		if(unresolvedCharsIndexEE < 0){
			unresolvedCharsIndexEE = 0;	
			unresolvedCharsInputRecordIndexEE =new int[initialSize];
			unresolvedCharsDefinitionEE = new SPattern[initialSize][];
		}else if(++unresolvedCharsIndexEE == unresolvedCharsInputRecordIndexEE.length){
		    int size = unresolvedCharsInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedCharsInputRecordIndexEE, 0, increasedCN, 0, unresolvedCharsIndexEE);
			unresolvedCharsInputRecordIndexEE = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(unresolvedCharsDefinitionEE, 0, increasedDef, 0, unresolvedCharsIndexEE);
			unresolvedCharsDefinitionEE = increasedDef;
		}
		
		unresolvedCharsInputRecordIndexEE[unresolvedCharsIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedCharsDefinitionEE[unresolvedCharsIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedCharacterContent(){
        
        for(int i = 0; i <= unresolvedCharsIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedCharsInputRecordIndexEE[i], this);
        }
        
        unresolvedCharsInputRecordIndexEE = null;
        unresolvedCharsDefinitionEE = null;
        unresolvedCharsIndexEE = -1;
    }
    
    
	// {24}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
        
		if(unresolvedAVIndexEE < 0){
			unresolvedAVIndexEE = 0;	
			unresolvedAVInputRecordIndexEE =new int[initialSize];
			unresolvedAVDefinitionEE = new SPattern[initialSize][];
		}else if(++unresolvedAVIndexEE == unresolvedAVInputRecordIndexEE.length){
		    int size = unresolvedAVInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAVInputRecordIndexEE, 0, increasedCN, 0, unresolvedAVIndexEE);
			unresolvedAVInputRecordIndexEE = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(unresolvedAVDefinitionEE, 0, increasedDef, 0, unresolvedAVIndexEE);
			unresolvedAVDefinitionEE = increasedDef;
		}
		
		unresolvedAVInputRecordIndexEE[unresolvedAVIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAVDefinitionEE[unresolvedAVIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedAttributeValue(){
        
        for(int i = 0; i <= unresolvedAVIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAVInputRecordIndexEE[i], this);
        }
        
        unresolvedAVInputRecordIndexEE = null;
        unresolvedAVDefinitionEE = null;
        unresolvedAVIndexEE = -1;
    }

    
    // {25}
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        
		if(datatypeTokenIndex < 0){
			datatypeTokenIndex = 0;	
			datatypeTokenInputRecordIndex =new int[initialSize];
			datatypeTokenDefinition = new SPattern[initialSize];
			datatypeTokenErrorMessage = new String[initialSize];
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
		}
		
		datatypeTokenInputRecordIndex[datatypeTokenIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		datatypeTokenDefinition[datatypeTokenIndex] = charsDefinition;
		datatypeTokenErrorMessage[datatypeTokenIndex] = datatypeErrorMessage;
	}
	public void clearListTokenDatatypeError(){
        
        for(int i = 0; i <= datatypeTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeTokenInputRecordIndex[i], this);
        }
        
        datatypeTokenInputRecordIndex = null;
        datatypeTokenDefinition = null;
        datatypeTokenErrorMessage = null;
        datatypeTokenIndex = -1;
    }

        
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
        
		if(valueTokenIndex < 0){
			valueTokenIndex = 0;	
			valueTokenInputRecordIndex =new int[initialSize];
			valueTokenDefinition = new SValue[initialSize];
		}else if(++valueTokenIndex == valueTokenInputRecordIndex.length){
		    int size = valueTokenInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueTokenInputRecordIndex, 0, increasedCN, 0, valueTokenIndex);
			valueTokenInputRecordIndex = increasedCN;
		    
		    SValue[] increasedDef = new SValue[size];
			System.arraycopy(valueTokenDefinition, 0, increasedDef, 0, valueTokenIndex);
			valueTokenDefinition = increasedDef;
		}
		
		valueTokenInputRecordIndex[valueTokenIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueTokenDefinition[valueTokenIndex] = charsDefinition;
	}
	public void clearListTokenValueError(){
        
        for(int i = 0; i <= valueTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueTokenInputRecordIndex[i], this);
        }
        
        valueTokenInputRecordIndex = null;
        valueTokenDefinition = null;
        valueTokenIndex = -1;
    }

    
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
        
		if(exceptTokenIndex < 0){
			exceptTokenIndex = 0;	
			exceptTokenInputRecordIndex =new int[initialSize];
			exceptTokenDefinition = new SData[initialSize];
		}else if(++exceptTokenIndex == exceptTokenInputRecordIndex.length){
		    int size = exceptTokenInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptTokenInputRecordIndex, 0, increasedCN, 0, exceptTokenIndex);
			exceptTokenInputRecordIndex = increasedCN;
		    
		    SData[] increasedDef = new SData[size];
			System.arraycopy(exceptTokenDefinition, 0, increasedDef, 0, exceptTokenIndex);
			exceptTokenDefinition = increasedDef;
		}
		
		exceptTokenInputRecordIndex[exceptTokenIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptTokenDefinition[exceptTokenIndex] = charsDefinition;
	}
	public void clearListTokenExceptedError(){
        
        for(int i = 0; i <= exceptTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptTokenInputRecordIndex[i], this);
        }
        
        exceptTokenInputRecordIndex = null;
        exceptTokenDefinition = null;
        exceptTokenIndex = -1;
    }
    
	
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
        
		if(unresolvedTokenIndexLPICE < 0){
			unresolvedTokenIndexLPICE = 0;	
			unresolvedTokenInputRecordIndexLPICE =new int[initialSize];
			unresolvedTokenDefinitionLPICE = new SPattern[initialSize][];
		}else if(++unresolvedTokenIndexLPICE == unresolvedTokenInputRecordIndexLPICE.length){
		    int size = unresolvedTokenInputRecordIndexLPICE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedTokenInputRecordIndexLPICE, 0, increasedCN, 0, unresolvedTokenIndexLPICE);
			unresolvedTokenInputRecordIndexLPICE = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(unresolvedTokenDefinitionLPICE, 0, increasedDef, 0, unresolvedTokenIndexLPICE);
			unresolvedTokenDefinitionLPICE = increasedDef;
		}
		
		unresolvedTokenInputRecordIndexLPICE[unresolvedTokenIndexLPICE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedTokenDefinitionLPICE[unresolvedTokenIndexLPICE] = possibleDefinitions;
    }
    public void clearUnresolvedListTokenInContextError(){
        
        for(int i = 0; i <= unresolvedTokenIndexLPICE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedTokenInputRecordIndexLPICE[i], this);
        }
        
        unresolvedTokenInputRecordIndexLPICE = null;
        unresolvedTokenDefinitionLPICE = null;
        unresolvedTokenIndexLPICE = -1;
    }
    
    
    public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        
		if(ambiguousTokenIndexLPICW < 0){
			ambiguousTokenIndexLPICW = 0;	
			ambiguousTokenInputRecordIndexLPICW =new int[initialSize];
			ambiguousTokenDefinitionLPICW = new SPattern[initialSize][];
		}else if(++ambiguousTokenIndexLPICW == ambiguousTokenInputRecordIndexLPICW.length){
		    int size = ambiguousTokenInputRecordIndexLPICW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousTokenInputRecordIndexLPICW, 0, increasedCN, 0, ambiguousTokenIndexLPICW);
			ambiguousTokenInputRecordIndexLPICW = increasedCN;
		    
		    SPattern[][] increasedDef = new SPattern[size][];
			System.arraycopy(ambiguousTokenDefinitionLPICW, 0, increasedDef, 0, ambiguousTokenIndexLPICW);
			ambiguousTokenDefinitionLPICW = increasedDef;
		}
		
		ambiguousTokenInputRecordIndexLPICW[ambiguousTokenIndexLPICW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousTokenDefinitionLPICW[ambiguousTokenIndexLPICW] = possibleDefinitions;
    }
    public void clearAmbiguousListTokenInContextWarning(){
        
        for(int i = 0; i <= ambiguousTokenIndexLPICW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousTokenInputRecordIndexLPICW[i], this);
        }
        
        ambiguousTokenInputRecordIndexLPICW = null;
        ambiguousTokenDefinitionLPICW = null;
        ambiguousTokenIndexLPICW = -1;
    }    
    
    
	public void missingCompositorContent(SRule context, 
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
		}
		missingCompositorContentContext[missingCompositorContentIndex] = context;
		missingCompositorContentStartInputRecordIndex[missingCompositorContentIndex] = startInputRecordIndex;
		activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		missingCompositorContentDefinition[missingCompositorContentIndex] = definition;
		missingCompositorContentExpected[missingCompositorContentIndex] = expected;
		missingCompositorContentFound[missingCompositorContentIndex] = found;
			
	}	
	public void clearMissingCompositorContent(){
        
        for(int i = 0; i <= missingCompositorContentIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(missingCompositorContentStartInputRecordIndex[i], this);
        }
        
        missingCompositorContentContext = null;
        missingCompositorContentStartInputRecordIndex = null;
        missingCompositorContentDefinition = null;
        missingCompositorContentExpected = null;
        missingCompositorContentFound = null;
        
        missingCompositorContentIndex = -1;
    }
        
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
        conflict = true;
        this.conflictResolutionId = conflictResolutionId;
        this.candidatesCount = candidatesCount;
        this.commonMessages = commonMessages;
        this.disqualified = disqualified;
        this.candidateMessages = candidateMessages;
        
    }
    public void clearConflict(){
        candidatesCount = -1;
        if(commonMessages != null){
            if(isDiscarded)commonMessages.clear(this); // It can be only one because it is about this context.
            commonMessages = null;
        }
        
        disqualified = null;
        
        if(candidateMessages != null){
            if(isDiscarded){
                for(MessageReporter cm : candidateMessages){                    
                    if(cm != null){
                        cm.clear(this);
                    }
                }
            }
            candidateMessages = null;
        }
    }
    
    
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    internalConflict = true;
	    this.conflictMessageReporter = conflictMessageReporter;
	}
	void clearInternalConflict(){	    
	    if(conflictMessageReporter != null) {
	        conflictMessageReporter.clear(this);
	        conflictMessageReporter = null;
	    }
	    internalConflict = false;
	}
	
    public void transferErrorMessages(ErrorCatcher errorCatcher){
		// {2}
        //String message = "";
		if(unknownElementIndex >= 0){
			for(int i = 0; i <= unknownElementIndex; i++){
				errorCatcher.unknownElement(unknownElementInputRecordIndex[i]);
				activeInputDescriptor.unregisterClientForRecord(unknownElementInputRecordIndex[i], this);
			}
		}	
		// {3}
		if(unexpectedElementIndex >= 0){
			for(int i = 0; i <= unexpectedElementIndex; i++){
				errorCatcher.unexpectedElement(unexpectedElementDefinition[i],
                                        unexpectedElementInputRecordIndex[i]);
                activeInputDescriptor.unregisterClientForRecord(unexpectedElementInputRecordIndex[i], this);
			}
		}
		// {4}
		if(unexpectedAmbiguousElementIndex  >= 0){
			for(int i = 0; i <= unexpectedAmbiguousElementIndex; i++){
				errorCatcher.unexpectedAmbiguousElement(unexpectedAmbiguousElementDefinition[i],
                                            unexpectedAmbiguousElementInputRecordIndex[i]);
                activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousElementInputRecordIndex[i], this);
			}
		}
		// {5}
		if(unknownAttributeIndex  >= 0){
			for(int i = 0; i <= unknownAttributeIndex; i++){
				errorCatcher.unknownAttribute(unknownAttributeInputRecordIndex[i]);
				activeInputDescriptor.unregisterClientForRecord(unknownAttributeInputRecordIndex[i], this);
			}
		}	
		// {6}
		if(unexpectedAttributeIndex  >= 0){
			for(int i = 0; i <= unexpectedAttributeIndex; i++){
				errorCatcher.unexpectedAttribute(unexpectedAttributeDefinition[i],
                                        unknownAttributeInputRecordIndex[i]);
                activeInputDescriptor.unregisterClientForRecord(unknownAttributeInputRecordIndex[i], this);
			}
		}
		// {7}
		if(unexpectedAmbiguousAttributeIndex >= 0){
			for(int i = 0; i <= unexpectedAmbiguousAttributeIndex; i++){
				errorCatcher.unexpectedAmbiguousAttribute(unexpectedAmbiguousAttributeDefinition[i],
                                                    unknownAttributeInputRecordIndex[i]);
                activeInputDescriptor.unregisterClientForRecord(unknownAttributeInputRecordIndex[i], this);
			}
		}
		
		// {8}
		if(misplacedIndex  >= 0){
		    SPattern[] sourceDefinition = null;
		    SPattern reper = null;
			for(int i = 0; i <= misplacedIndex; i++){
			    for(int j = 0; j < misplacedDefinition[i].length; j++){
                    errorCatcher.misplacedContent(misplacedContext[i],
                                            misplacedStartInputRecordIndex[i],
                                            misplacedDefinition[i][j],
                                            misplacedInputRecordIndex[i][j],
                                            sourceDefinition,
                                            reper);                    
                    activeInputDescriptor.unregisterClientForRecord(misplacedInputRecordIndex[i][j], 0, misplacedInputRecordIndex[i][j].length, this);
                }
                activeInputDescriptor.unregisterClientForRecord(misplacedStartInputRecordIndex[i], this);
			}			
		}
		
		// {9}
		if(excessiveIndex  >= 0){
			for(int i = 0; i <= excessiveIndex; i++){
				errorCatcher.excessiveContent(excessiveContext[i],
                                        excessiveStartInputRecordIndex[i],
                                        excessiveDefinition[i],
                                        excessiveInputRecordIndex[i]);
                activeInputDescriptor.unregisterClientForRecord(excessiveStartInputRecordIndex[i], this);
                activeInputDescriptor.unregisterClientForRecord(excessiveInputRecordIndex[i], 0, excessiveInputRecordIndex[i].length, this);
			}
		}
		// {10}
		if(missingIndex >= 0){
			for(int i = 0; i <= missingIndex; i++){
				errorCatcher.missingContent(missingContext[i],
                                        missingStartInputRecordIndex[i],
                                        missingDefinition[i],
                                        missingExpected[i],
                                        missingFound[i],
                                        missingInputRecordIndex[i]);
                activeInputDescriptor.unregisterClientForRecord(missingStartInputRecordIndex[i], this);
                if(missingInputRecordIndex[i] != null) activeInputDescriptor.unregisterClientForRecord(missingInputRecordIndex[i], 0, missingInputRecordIndex[i].length, this);
			}			
		}		
		// {11}
		if(illegalIndex >= 0){
			for(int i = 0; i <= illegalIndex; i++){
				errorCatcher.illegalContent(illegalContext[i],
				                        illegalStartInputRecordIndex[i]);
				activeInputDescriptor.unregisterClientForRecord(illegalStartInputRecordIndex[i], this);
			}			
		}
		// {12 A}
		if(unresolvedAmbiguousElementIndexEE >= 0){
			for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
				errorCatcher.unresolvedAmbiguousElementContentError(unresolvedAmbiguousElementInputRecordIndexEE[i],
                                                    unresolvedAmbiguousElementDefinitionEE[i]);
                
                activeInputDescriptor.unregisterClientForRecord(unresolvedAmbiguousElementInputRecordIndexEE[i], this);
			}
		}
		// {12 U}
		if(unresolvedUnresolvedElementIndexEE >= 0){
			for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
				errorCatcher.unresolvedUnresolvedElementContentError(unresolvedUnresolvedElementInputRecordIndexEE[i],
                                                                unresolvedUnresolvedElementDefinitionEE[i]);
                
                activeInputDescriptor.unregisterClientForRecord(unresolvedUnresolvedElementInputRecordIndexEE[i], this);
			}
		}
		// {13}
		if(unresolvedAttributeIndexEE >= 0){
			for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
				errorCatcher.unresolvedAttributeContentError(unresolvedAttributeInputRecordIndexEE[i],
                                                        unresolvedAttributeDefinitionEE[i]);
                
                activeInputDescriptor.unregisterClientForRecord(unresolvedAttributeInputRecordIndexEE[i], this);
			}
		}
		// {14}
		
		// {15}
		if(datatypeCharsIndex >= 0){
			for(int i = 0; i <= datatypeCharsIndex; i++){
				errorCatcher.characterContentDatatypeError(datatypeCharsInputRecordIndex[i],
                                                        datatypeCharsDefinition[i],
                                                        datatypeCharsErrorMessage[i]);
                
                activeInputDescriptor.unregisterClientForRecord(datatypeCharsInputRecordIndex[i], this);
			}
		}
		// {16}
		if(datatypeAVIndex >= 0){
		    for(int i = 0; i <= datatypeAVIndex; i++){
		        errorCatcher.attributeValueDatatypeError(datatypeAVInputRecordIndex[i],
                                                    datatypeAVDefinition[i],
                                                    datatypeAVErrorMessage[i]);
                
                activeInputDescriptor.unregisterClientForRecord(datatypeAVInputRecordIndex[i], this);
            }
		}
		// {17}
		if(valueCharsIndex >= 0){
			for(int i = 0; i <= valueCharsIndex; i++){
			    errorCatcher.characterContentValueError(valueCharsInputRecordIndex[i],
                                                    valueCharsDefinition[i]);	
                activeInputDescriptor.unregisterClientForRecord(valueCharsInputRecordIndex[i], this);
			}
		}
		// {18}
		if(valueAVIndex >= 0){
			for(int i = 0; i <= valueAVIndex; i++){
				errorCatcher.attributeValueValueError(valueAVInputRecordIndex[i],
                                                    valueAVDefinition[i]);
                activeInputDescriptor.unregisterClientForRecord(valueAVInputRecordIndex[i], this);
			}
		}
		// {19}
		if(exceptCharsIndex >= 0){
			for(int i = 0; i <= exceptCharsIndex; i++){
				errorCatcher.characterContentExceptedError(exceptCharsInputRecordIndex[i],
                                                        exceptCharsDefinition[i]);
                activeInputDescriptor.unregisterClientForRecord(exceptCharsInputRecordIndex[i], this);
			}
		}
		// {20}
		if(exceptAVIndex >= 0){
			for(int i = 0; i <= exceptAVIndex; i++){
				errorCatcher.attributeValueExceptedError(exceptAVInputRecordIndex[i],
                                                        exceptAVDefinition[i]);
                activeInputDescriptor.unregisterClientForRecord(exceptAVInputRecordIndex[i], this);
			}
		}
		// {21}
		if(unexpectedCharsIndex >= 0){
			for(int i = 0; i <= unexpectedCharsIndex; i++){
				errorCatcher.unexpectedCharacterContent(unexpectedCharsInputRecordIndex[i],
                                                    unexpectedCharsDefinition[i]);
                activeInputDescriptor.unregisterClientForRecord(unexpectedCharsInputRecordIndex[i], this);
			}
		}
		// {22}
		if(unexpectedAVIndex >= 0){
			for(int i = 0; i <= unexpectedAVIndex; i++){
				errorCatcher.unexpectedAttributeValue(unexpectedAVInputRecordIndex[i],
                                                    unexpectedAVDefinition[i]);
                
                activeInputDescriptor.unregisterClientForRecord(unexpectedAVInputRecordIndex[i], this);
			}
		}
		// {23}
		if(unresolvedCharsIndexEE >= 0){
			for(int i = 0; i <= unresolvedCharsIndexEE; i++){
				errorCatcher.unresolvedCharacterContent(unresolvedCharsInputRecordIndexEE[i],
                                                    unresolvedCharsDefinitionEE[i]);
                
                activeInputDescriptor.unregisterClientForRecord(unresolvedCharsInputRecordIndexEE[i], this);
			}
		}
		// {24}
		if(unresolvedAVIndexEE >= 0){			
			for(int i = 0; i <= unresolvedAVIndexEE; i++){				
				errorCatcher.unresolvedAttributeValue(unresolvedAVInputRecordIndexEE[i],
				                                    unresolvedAVDefinitionEE[i]);
                
                activeInputDescriptor.unregisterClientForRecord(unresolvedAVInputRecordIndexEE[i], this);
			}
		}
		// {25}
		if(datatypeTokenIndex >= 0){
			for(int i = 0; i <= datatypeTokenIndex; i++){
				errorCatcher.listTokenDatatypeError(datatypeTokenInputRecordIndex[i],
                                                    datatypeTokenDefinition[i],
                                                    datatypeTokenErrorMessage[i]);
                activeInputDescriptor.unregisterClientForRecord(datatypeTokenInputRecordIndex[i], this);
			}
		}
		// {26}
		if(valueTokenIndex >= 0){
			for(int i = 0; i <= valueTokenIndex; i++){
				errorCatcher.listTokenValueError(valueTokenInputRecordIndex[i],
                                                valueTokenDefinition[i]);
                
                activeInputDescriptor.unregisterClientForRecord(valueTokenInputRecordIndex[i], this);
			}
		}
		// {27}
		if(exceptTokenIndex >= 0){
			for(int i = 0; i <= exceptTokenIndex; i++){
				errorCatcher.listTokenExceptedError(exceptTokenInputRecordIndex[i],
                                                exceptTokenDefinition[i]);
                activeInputDescriptor.unregisterClientForRecord(exceptTokenInputRecordIndex[i], this);
			}
		}
		// {28}
		
        
        // {28_1}
        if(unresolvedTokenIndexLPICE >= 0){
			for(int i = 0; i <= unresolvedTokenIndexLPICE; i++){
				errorCatcher.unresolvedListTokenInContextError(unresolvedTokenInputRecordIndexLPICE[i],
                                                        unresolvedTokenDefinitionLPICE[i]);
                
                activeInputDescriptor.unregisterClientForRecord(unresolvedTokenInputRecordIndexLPICE[i], this);
			}
		}
		
		// {29}
		if(missingCompositorContentIndex >= 0){
			for(int i = 0; i <= missingCompositorContentIndex; i++){
				errorCatcher.missingCompositorContent(missingCompositorContentContext[i],
                                                missingCompositorContentStartInputRecordIndex[i],
                                                missingCompositorContentDefinition[i],
                                                missingCompositorContentExpected[i],
                                                missingCompositorContentFound[i]);
                activeInputDescriptor.unregisterClientForRecord(missingCompositorContentStartInputRecordIndex[i], this);
			}			
		}
		
		//if(conflict)errorCatcher.conflict(conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
		
        if(internalConflict)internalConflict(conflictMessageReporter);
	} 
	
    public void transferWarningMessages(ErrorCatcher errorCatcher){
		String message = "";
		// {w1 U}
		if(ambiguousUnresolvedElementIndexWW >= 0){
			for(int i = 0; i <= ambiguousUnresolvedElementIndexWW; i++){
				errorCatcher.ambiguousUnresolvedElementContentWarning(ambiguousUnresolvedElementInputRecordIndexWW[i],
                                                                        ambiguousUnresolvedElementDefinitionWW[i]);

                activeInputDescriptor.unregisterClientForRecord(ambiguousUnresolvedElementInputRecordIndexWW[i], this);				
			}
		}
		// {w1 A}
		if(ambiguousAmbiguousElementIndexWW >= 0){
			for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
				errorCatcher.ambiguousAmbiguousElementContentWarning(ambiguousAmbiguousElementInputRecordIndexWW[i],
                                                                ambiguousAmbiguousElementDefinitionWW[i]);
                
                activeInputDescriptor.unregisterClientForRecord(ambiguousAmbiguousElementInputRecordIndexWW[i], this);				
			}
		}
		
		// {w2}
		if(ambiguousAttributeIndexWW >= 0){
			for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
				errorCatcher.ambiguousAttributeContentWarning(ambiguousAttributeInputRecordIndexWW[i],
                                                            ambiguousAttributeDefinitionWW[i]);
                
                activeInputDescriptor.unregisterClientForRecord(ambiguousAttributeInputRecordIndexWW[i], this);
			}
		}
		// {w3}
		if(ambiguousCharsIndexWW >= 0){
			for(int i = 0; i <= ambiguousCharsIndexWW; i++){
				errorCatcher.ambiguousCharacterContentWarning(ambiguousCharsInputRecordIndexWW[i],
                                                    ambiguousCharsDefinitionWW[i]);
                
                activeInputDescriptor.unregisterClientForRecord(ambiguousCharsInputRecordIndexWW[i], this);
			}
		}
        // {w4}
		if(ambiguousAVIndexWW >= 0){
			for(int i = 0; i <= ambiguousAVIndexWW; i++){
				errorCatcher.ambiguousAttributeValueWarning(ambiguousAVInputRecordIndexWW[i],
                                                    ambiguousAVDefinitionWW[i]);
                
                activeInputDescriptor.unregisterClientForRecord(ambiguousAVInputRecordIndexWW[i], this);
                
			}
		}
		
		
        // {28_2}
        if(ambiguousTokenIndexLPICW >= 0){
			for(int i = 0; i <= ambiguousTokenIndexLPICW; i++){
				errorCatcher.ambiguousListTokenInContextWarning(ambiguousTokenInputRecordIndexLPICW[i],
                                                            ambiguousTokenDefinitionLPICW[i]);
                
                activeInputDescriptor.unregisterClientForRecord(ambiguousTokenInputRecordIndexLPICW[i], this);
			}
		}
	}	
    
	public void transferMessages(ErrorCatcher errorCatcher){
	    isClear = true;
	    transferErrorMessages(errorCatcher);
	    transferWarningMessages(errorCatcher);
	}
	
    public String toString(){
        return "TemporaryMessageStorage ";
    }
 
    protected void finalize(){
        if(!isClear)clear();
	    if(unknownElementIndex >= 0){
	        activeInputDescriptor.unregisterClientForRecord(unknownElementInputRecordIndex, 0, unknownElementIndex+1, this);
	    }
	}
	
	public void clear(){
	    if(isClear) return;
	    isClear = true;
        // TODO check sizes to only clear when full
        // and refactor the creation of new instances in the ErrorHandlers
        if( unknownElementIndex >= 0 ) clearUnknownElement();
		if( unexpectedElementIndex >= 0) clearUnexpectedElement();
		if( unexpectedAmbiguousElementIndex >= 0) clearUnexpectedAmbiguousElement();
		if( unknownAttributeIndex >= 0 ) clearUnknownAttribute();
		if( unexpectedAttributeIndex >= 0) clearUnexpectedAttribute();
		if( unexpectedAmbiguousAttributeIndex >= 0) clearUnexpectedAmbiguousAttribute();   
        if( misplacedIndex >= 0 ) clearMisplacedElement();
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
        
        clearInternalConflict();
        
        isDiscarded = false;
    }
}
