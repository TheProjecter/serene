package serene.validation.handlers.error;

import java.util.Arrays;
import java.util.List;
import java.util.BitSet;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
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

import sereneWrite.MessageWriter;

public class ContextMessageHandler  extends AbstractMessageHandler implements ExternalConflictErrorCatcher{	
    
	public ContextMessageHandler(MessageWriter debugWriter){
		super(debugWriter);
	}	
    
	
	public ConflictMessageReporter getConflictMessageReporter(ErrorDispatcher errorDispatcher){
        ConflictMessageReporter result = new ConflictMessageReporter(parent,
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
                                    errorDispatcher,                                    
                                    debugWriter);
                
        return result;
    }
    
    
    
	public void unknownElement(int inputRecordIndex){
        messageTotalCount++;
		if(unknownElementIndex < 0){
		    unknownElementIndex = 0;
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
        messageTotalCount -= (unknownElementIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unknownElementInputRecordIndex, 0, unknownElementIndex+1, this);
        unknownElementIndex = -1;
        unknownElementInputRecordIndex = null;
    }
	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
        messageTotalCount++;
		if(unexpectedElementIndex < 0){
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
        messageTotalCount -= (unexpectedElementIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unexpectedElementInputRecordIndex, 0, unexpectedElementIndex+1, this);
        unexpectedElementIndex = -1;
        unexpectedElementInputRecordIndex = null;
        unexpectedElementDefinition = null;
    }
    
    
	public void unexpectedAmbiguousElement(SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
        messageTotalCount++;
		if(unexpectedAmbiguousElementIndex < 0){
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
        messageTotalCount -= (unexpectedAmbiguousElementIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousElementInputRecordIndex, 0, unexpectedAmbiguousElementIndex+1, this);
        unexpectedAmbiguousElementIndex = -1;
        unexpectedAmbiguousElementInputRecordIndex = null;
        unexpectedAmbiguousElementDefinition = null;
    }
	
	
	
	
	public void unknownAttribute(int inputRecordIndex){
        messageTotalCount++;
		if(unknownAttributeIndex < 0){
		    unknownAttributeIndex = 0;
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
        messageTotalCount -= (unknownAttributeIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unknownAttributeInputRecordIndex, 0, unknownAttributeIndex+1, this);
        unknownAttributeIndex = -1;
        unknownAttributeInputRecordIndex = null;
    }
	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
        messageTotalCount++;
		if(unexpectedAttributeIndex < 0){
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
        messageTotalCount -= (unexpectedAttributeIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unexpectedAttributeInputRecordIndex, 0, unexpectedAttributeIndex+1, this);
        unexpectedAttributeIndex = -1;
        unexpectedAttributeInputRecordIndex = null;
        unexpectedAttributeDefinition = null;
    }
    
    
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
        messageTotalCount++;
		if(unexpectedAmbiguousAttributeIndex < 0){
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
        messageTotalCount -= (unexpectedAmbiguousAttributeIndex+1);
        activeInputDescriptor.unregisterClientForRecord(unexpectedAmbiguousAttributeInputRecordIndex, 0, unexpectedAmbiguousAttributeIndex+1, this);
        unexpectedAmbiguousAttributeIndex = -1;
        unexpectedAmbiguousAttributeInputRecordIndex = null;
        unexpectedAmbiguousAttributeDefinition = null;
    }
    
    
	
	public void misplacedContent(APattern contextDefinition, 
											int startInputRecordIndex,
											APattern definition, 
											int inputRecordIndex,
											APattern sourceDefinition, //not stored, only used for internal conflict handling
											APattern reper){//not stored, only used for internal conflict handling
        if(misplacedIndex < 0){
		    misplacedIndex = 0;
		    
		    //create arrays for everything
		    //record everything
		    
		    misplacedContext = new APattern[initialSize];
            misplacedStartInputRecordIndex = new int[initialSize];
            misplacedDefinition = new APattern[initialSize][];
            misplacedInputRecordIndex = new int[initialSize][][];
                        
            misplacedContext[misplacedIndex] = contextDefinition;
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new APattern[1];
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
					
					APattern[] increasedDef = new APattern[oldLength+1];					
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
                APattern[] increasedCDef = new APattern[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedContext, 0, increasedCDef, 0, misplacedIndex);
                misplacedContext = increasedCDef;               
                
                int[] increasedSIRI  = new int[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedStartInputRecordIndex, 0, increasedSIRI, 0, misplacedIndex);
                misplacedStartInputRecordIndex = increasedSIRI;
                
                APattern[][] increasedDef = new APattern[misplacedIndex+increaseSizeAmount][];
                System.arraycopy(misplacedDefinition, 0, increasedDef, 0, misplacedIndex);
                misplacedDefinition = increasedDef;
                 
                int[][][] increasedIRI = new int[misplacedIndex+increaseSizeAmount][][];
                System.arraycopy(misplacedInputRecordIndex, 0, increasedIRI, 0, misplacedIndex);
                misplacedInputRecordIndex = increasedIRI;
            }
            
            misplacedContext[misplacedIndex] = contextDefinition;
            
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new APattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = new int[1];
            misplacedInputRecordIndex[misplacedIndex][0][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		}
	}
	
    public void misplacedContent(APattern contextDefinition,  
											int startInputRecordIndex,
											APattern definition,
											int[] inputRecordIndex,
											APattern[] sourceDefinition, 
											APattern reper){//not stored, only used for internal conflict handling
		
		if(misplacedIndex < 0){
		    misplacedIndex = 0;
		    
		    //create arrays for everything
		    //record everything
		    
		    misplacedContext = new APattern[initialSize];
            misplacedStartInputRecordIndex = new int[initialSize];
            misplacedDefinition = new APattern[initialSize][];
            misplacedInputRecordIndex = new int[initialSize][][];
                        
            misplacedContext[misplacedIndex] = contextDefinition;
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new APattern[1];
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
					
					APattern[] increasedDef = new APattern[oldLength+1];					
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
                APattern[] increasedCDef = new APattern[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedContext, 0, increasedCDef, 0, misplacedIndex);
                misplacedContext = increasedCDef;               
                
                int[] increasedSIRI  = new int[misplacedIndex+increaseSizeAmount];
                System.arraycopy(misplacedStartInputRecordIndex, 0, increasedSIRI, 0, misplacedIndex);
                misplacedStartInputRecordIndex = increasedSIRI;
                
                APattern[][] increasedDef = new APattern[misplacedIndex+increaseSizeAmount][];
                System.arraycopy(misplacedDefinition, 0, increasedDef, 0, misplacedIndex);
                misplacedDefinition = increasedDef;
                 
                int[][][] increasedIRI = new int[misplacedIndex+increaseSizeAmount][][];
                System.arraycopy(misplacedInputRecordIndex, 0, increasedIRI, 0, misplacedIndex);
                misplacedInputRecordIndex = increasedIRI;
            }
            
            misplacedContext[misplacedIndex] = contextDefinition;
            
            misplacedStartInputRecordIndex[misplacedIndex] = startInputRecordIndex;
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            
            misplacedDefinition[misplacedIndex] = new APattern[1];
            misplacedDefinition[misplacedIndex][0] = definition;
            
            misplacedInputRecordIndex[misplacedIndex] = new int[1][];
            misplacedInputRecordIndex[misplacedIndex][0] = inputRecordIndex;
            activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
		}
	}
    public void clearMisplacedElement(){
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
    }	
			
	
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern definition, int[] inputRecordIndex){
		
		messageTotalCount++;
		if(excessiveIndex < 0){
			excessiveIndex = 0;
			excessiveContext = new APattern[initialSize];		
			excessiveStartInputRecordIndex = new int[initialSize];
			excessiveDefinition = new APattern[initialSize];
			excessiveInputRecordIndex = new int[initialSize][];		
		}else if(++excessiveIndex == excessiveContext.length){
		    int newSize = excessiveIndex+increaseSizeAmount;
		    
		    APattern[] increasedEC = new APattern[newSize];
			System.arraycopy(excessiveContext, 0, increasedEC, 0, excessiveIndex);
			excessiveContext = increasedEC;
			
			int[] increasedSLN = new int[newSize];
			System.arraycopy(excessiveStartInputRecordIndex, 0, increasedSLN, 0, excessiveIndex);
			excessiveStartInputRecordIndex = increasedSLN;
			
			APattern[] increasedED = new APattern[newSize];
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
	public void excessiveContent(Rule context, APattern definition, int inputRecordIndex){
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
        messageTotalCount -= (excessiveIndex+1);
        
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


    public void missingContent(Rule context,  
								int startInputRecordIndex,								 
								APattern definition, 
								int expected, 
								int found, 
								int[] inputRecordIndex){	    
        
		messageTotalCount++;
		if(missingIndex < 0){
			missingIndex = 0;
			missingContext = new APattern[initialSize];			
			missingStartInputRecordIndex = new int[initialSize];
			missingDefinition = new APattern[initialSize];
			missingExpected = new int[initialSize];
			missingFound = new int[initialSize];		
			missingInputRecordIndex = new int[initialSize][];		
		}else if(++missingIndex == missingContext.length){
		    int newSize = missingIndex+increaseSizeAmount;
		    
		    APattern[] increasedEC = new APattern[newSize];
			System.arraycopy(missingContext, 0, increasedEC, 0, missingIndex);
			missingContext = increasedEC;
			
			int[] increasedSIRI = new int[newSize];
			System.arraycopy(missingStartInputRecordIndex, 0, increasedSIRI, 0, missingIndex);
			missingStartInputRecordIndex = increasedSIRI;
			
			APattern[] increasedED = new APattern[newSize];
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
		if(inputRecordIndex != null) activeInputDescriptor.registerClientForRecord(inputRecordIndex, 0, inputRecordIndex.length, this);
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
    }
    
    
    
    public void illegalContent(Rule context, 
	                        int startInputRecordIndex){
        messageTotalCount++;
		if(illegalIndex < 0){
			illegalIndex = 0;
			illegalContext = new APattern[initialSize];
			illegalStartInputRecordIndex = new int[initialSize];					
		}else if(++illegalIndex == illegalContext.length){
		    int size = illegalIndex + increaseSizeAmount;
			APattern[] increasedEC = new APattern[size];
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
        messageTotalCount -= (illegalIndex+1);
        for(int i = 0; i <= illegalIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(illegalStartInputRecordIndex[i], this);
        }
        
        illegalIndex = -1;
        illegalContext = null;
        illegalStartInputRecordIndex = null;
    }
    
    
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, 
									AElement[] possibleDefinitions){
        
		messageTotalCount++;
		if(unresolvedAmbiguousElementIndexEE < 0){
			unresolvedAmbiguousElementIndexEE = 0;	
			unresolvedAmbiguousElementInputRecordIndexEE =new int[initialSize];
			unresolvedAmbiguousElementDefinitionEE = new AElement[initialSize][];
		}else if(++unresolvedAmbiguousElementIndexEE == unresolvedAmbiguousElementInputRecordIndexEE.length){
		    int size = unresolvedAmbiguousElementInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAmbiguousElementInputRecordIndexEE, 0, increasedCN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementInputRecordIndexEE = increasedCN;
		    
		    AElement[][] increasedDef = new AElement[size][];
			System.arraycopy(unresolvedAmbiguousElementDefinitionEE, 0, increasedDef, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementDefinitionEE = increasedDef;
		}
		
		unresolvedAmbiguousElementInputRecordIndexEE[unresolvedAmbiguousElementIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAmbiguousElementDefinitionEE[unresolvedAmbiguousElementIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedAmbiguousElementContentError(){
        
        messageTotalCount -= (unresolvedAmbiguousElementIndexEE+1);
        
        for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAmbiguousElementInputRecordIndexEE[i], this);
        }
        
        unresolvedAmbiguousElementInputRecordIndexEE = null;
        unresolvedAmbiguousElementDefinitionEE = null;
        unresolvedAmbiguousElementIndexEE = -1;
    }
    
    
    public void unresolvedUnresolvedElementContentError(int inputRecordIndex, 
									AElement[] possibleDefinitions){
        
		messageTotalCount++;
		if(unresolvedUnresolvedElementIndexEE < 0){
			unresolvedUnresolvedElementIndexEE = 0;	
			unresolvedUnresolvedElementInputRecordIndexEE =new int[initialSize];
			unresolvedUnresolvedElementDefinitionEE = new AElement[initialSize][];
		}else if(++unresolvedUnresolvedElementIndexEE == unresolvedUnresolvedElementInputRecordIndexEE.length){
		    int size = unresolvedUnresolvedElementInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedUnresolvedElementInputRecordIndexEE, 0, increasedCN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementInputRecordIndexEE = increasedCN;
		    
		    AElement[][] increasedDef = new AElement[size][];
			System.arraycopy(unresolvedUnresolvedElementDefinitionEE, 0, increasedDef, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementDefinitionEE = increasedDef;
		}
		
		unresolvedUnresolvedElementInputRecordIndexEE[unresolvedUnresolvedElementIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedUnresolvedElementDefinitionEE[unresolvedUnresolvedElementIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedUnresolvedElementContentError(){
        
        messageTotalCount -= (unresolvedUnresolvedElementIndexEE+1);
        
        for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedUnresolvedElementInputRecordIndexEE[i], this);
        }
        
        unresolvedUnresolvedElementInputRecordIndexEE = null;
        unresolvedUnresolvedElementDefinitionEE = null;
        unresolvedUnresolvedElementIndexEE = -1;        
    }
    
    
	public void unresolvedAttributeContentError(int inputRecordIndex, 
									AAttribute[] possibleDefinitions){
        
		messageTotalCount++;
		if(unresolvedAttributeIndexEE < 0){
			unresolvedAttributeIndexEE = 0;	
			unresolvedAttributeInputRecordIndexEE =new int[initialSize];
			unresolvedAttributeDefinitionEE = new AAttribute[initialSize][];
		}else if(++unresolvedAttributeIndexEE == unresolvedAttributeInputRecordIndexEE.length){
		    int size = unresolvedAttributeInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAttributeInputRecordIndexEE, 0, increasedCN, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeInputRecordIndexEE = increasedCN;
		    
		    AAttribute[][] increasedDef = new AAttribute[size][];
			System.arraycopy(unresolvedAttributeDefinitionEE, 0, increasedDef, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeDefinitionEE = increasedDef;
		}
		
		unresolvedAttributeInputRecordIndexEE[unresolvedAttributeIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAttributeDefinitionEE[unresolvedAttributeIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedAttributeContentError(){
        
        messageTotalCount -= (unresolvedAttributeIndexEE+1);
         
        for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAttributeInputRecordIndexEE[i], this);
        }
        
        unresolvedAttributeInputRecordIndexEE = null;
        unresolvedAttributeDefinitionEE = null;
        unresolvedAttributeIndexEE = -1;
    }
	
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, 
									AElement[] possibleDefinitions){
        
        messageTotalCount++;
		if(ambiguousUnresolvedElementIndexWW < 0){
			ambiguousUnresolvedElementIndexWW = 0;	
			ambiguousUnresolvedElementInputRecordIndexWW =new int[initialSize];
			ambiguousUnresolvedElementDefinitionWW = new AElement[initialSize][];
		}else if(++ambiguousUnresolvedElementIndexWW == ambiguousUnresolvedElementInputRecordIndexWW.length){
		    int size = ambiguousUnresolvedElementInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousUnresolvedElementInputRecordIndexWW, 0, increasedCN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementInputRecordIndexWW = increasedCN;
		    
		    AElement[][] increasedDef = new AElement[size][];
			System.arraycopy(ambiguousUnresolvedElementDefinitionWW, 0, increasedDef, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementDefinitionWW = increasedDef;
		}
		
		ambiguousUnresolvedElementInputRecordIndexWW[ambiguousUnresolvedElementIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousUnresolvedElementDefinitionWW[ambiguousUnresolvedElementIndexWW] = possibleDefinitions;
	}
	public void clearAmbiguousUnresolvedElementContentWarning(){
        
        messageTotalCount -= (ambiguousUnresolvedElementIndexWW+1);
        
        for(int i = 0; i <= ambiguousUnresolvedElementIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousUnresolvedElementInputRecordIndexWW[i], this);
        }
        
        ambiguousUnresolvedElementInputRecordIndexWW = null;
        ambiguousUnresolvedElementDefinitionWW = null;
        ambiguousUnresolvedElementIndexWW = -1;       
    }
    
    
    public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, 
									AElement[] possibleDefinitions){
        
        messageTotalCount++;
		if(ambiguousAmbiguousElementIndexWW < 0){
			ambiguousAmbiguousElementIndexWW = 0;	
			ambiguousAmbiguousElementInputRecordIndexWW =new int[initialSize];
			ambiguousAmbiguousElementDefinitionWW = new AElement[initialSize][];
		}else if(++ambiguousAmbiguousElementIndexWW == ambiguousAmbiguousElementInputRecordIndexWW.length){
		    int size = ambiguousAmbiguousElementInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAmbiguousElementInputRecordIndexWW, 0, increasedCN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementInputRecordIndexWW = increasedCN;
		    
		    AElement[][] increasedDef = new AElement[size][];
			System.arraycopy(ambiguousAmbiguousElementDefinitionWW, 0, increasedDef, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementDefinitionWW = increasedDef;
		}
		
		ambiguousAmbiguousElementInputRecordIndexWW[ambiguousAmbiguousElementIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAmbiguousElementDefinitionWW[ambiguousAmbiguousElementIndexWW] = possibleDefinitions;        
	}
	public void clearAmbiguousAmbiguousElementContentWarning(){
        
        messageTotalCount -= (ambiguousAmbiguousElementIndexWW+1);
        
        for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAmbiguousElementInputRecordIndexWW[i], this);
        }
        
        ambiguousAmbiguousElementInputRecordIndexWW = null;
        ambiguousAmbiguousElementDefinitionWW = null;
        ambiguousAmbiguousElementIndexWW = -1;
    }
        
	public void ambiguousAttributeContentWarning(int inputRecordIndex, 
									AAttribute[] possibleDefinitions){
        
		messageTotalCount++;
		if(ambiguousAttributeIndexWW < 0){
			ambiguousAttributeIndexWW = 0;	
			ambiguousAttributeInputRecordIndexWW =new int[initialSize];
			ambiguousAttributeDefinitionWW = new AAttribute[initialSize][];
		}else if(++ambiguousAttributeIndexWW == ambiguousAttributeInputRecordIndexWW.length){
		    int size = ambiguousAttributeInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAttributeInputRecordIndexWW, 0, increasedCN, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeInputRecordIndexWW = increasedCN;
		    
		    AAttribute[][] increasedDef = new AAttribute[size][];
			System.arraycopy(ambiguousAttributeDefinitionWW, 0, increasedDef, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeDefinitionWW = increasedDef;
		}
		
		ambiguousAttributeInputRecordIndexWW[ambiguousAttributeIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAttributeDefinitionWW[ambiguousAttributeIndexWW] = possibleDefinitions;
	}
	public void clearAmbiguousAttributeContentWarning(){
        
        messageTotalCount -= (ambiguousAttributeIndexWW+1);
        
        for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAttributeInputRecordIndexWW[i], this);
        }
        
        ambiguousAttributeInputRecordIndexWW = null;
        ambiguousAttributeDefinitionWW = null;
        ambiguousAttributeIndexWW = -1;
    }
	
    
	public void ambiguousCharacterContentWarning(int inputRecordIndex, 
									CharsActiveTypeItem[] possibleDefinitions){
        
		messageTotalCount++;
		if(ambiguousCharsIndexWW < 0){
			ambiguousCharsIndexWW = 0;	
			ambiguousCharsInputRecordIndexWW =new int[initialSize];
			ambiguousCharsDefinitionWW = new CharsActiveTypeItem[initialSize][];
		}else if(++ambiguousCharsIndexWW == ambiguousCharsInputRecordIndexWW.length){
		    int size = ambiguousCharsInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousCharsInputRecordIndexWW, 0, increasedCN, 0, ambiguousCharsIndexWW);
			ambiguousCharsInputRecordIndexWW = increasedCN;
		    
		    CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[size][];
			System.arraycopy(ambiguousCharsDefinitionWW, 0, increasedDef, 0, ambiguousCharsIndexWW);
			ambiguousCharsDefinitionWW = increasedDef;
		}
		
		ambiguousCharsInputRecordIndexWW[ambiguousCharsIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousCharsDefinitionWW[ambiguousCharsIndexWW] = possibleDefinitions;
	}
	public void clearAmbiguousCharacterContentWarning(){
        
        messageTotalCount -= (ambiguousCharsIndexWW+1);
        
        for(int i = 0; i <= ambiguousCharsIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousCharsInputRecordIndexWW[i], this);
        }
        
        ambiguousCharsInputRecordIndexWW = null;
        ambiguousCharsDefinitionWW = null;
        ambiguousCharsIndexWW = -1;
    }
	
    
	public void ambiguousAttributeValueWarning(int inputRecordIndex, 
									CharsActiveTypeItem[] possibleDefinitions){
        
		messageTotalCount++;
		if(ambiguousAVIndexWW < 0){
			ambiguousAVIndexWW = 0;	
			ambiguousAVInputRecordIndexWW =new int[initialSize];
			ambiguousAVDefinitionWW = new CharsActiveTypeItem[initialSize][];
		}else if(++ambiguousAVIndexWW == ambiguousAVInputRecordIndexWW.length){
		    int size = ambiguousAVInputRecordIndexWW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousAVInputRecordIndexWW, 0, increasedCN, 0, ambiguousAVIndexWW);
			ambiguousAVInputRecordIndexWW = increasedCN;
		    
		    CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[size][];
			System.arraycopy(ambiguousAVDefinitionWW, 0, increasedDef, 0, ambiguousAVIndexWW);
			ambiguousAVDefinitionWW = increasedDef;
		}
		
		ambiguousAVInputRecordIndexWW[ambiguousAVIndexWW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousAVDefinitionWW[ambiguousAVIndexWW] = possibleDefinitions;
	}
	public void clearAmbiguousAttributeValueWarning(){
        
        messageTotalCount -= (ambiguousAVIndexWW+1);
        
        for(int i = 0; i <= ambiguousAVIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAVInputRecordIndexWW[i], this);
        }
        
        ambiguousAVInputRecordIndexWW = null;
        ambiguousAVDefinitionWW = null;
        ambiguousAVIndexWW = -1;
    }
	
    
	
    // {15}
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        
		messageTotalCount++;
		if(datatypeCharsIndex < 0){
			datatypeCharsIndex = 0;	
			datatypeCharsInputRecordIndex =new int[initialSize];
			datatypeCharsDefinition = new DatatypedActiveTypeItem[initialSize];
			datatypeCharsErrorMessage = new String[initialSize];
		}else if(++datatypeCharsIndex == datatypeCharsInputRecordIndex.length){
		    int size = datatypeCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(datatypeCharsInputRecordIndex, 0, increasedCN, 0, datatypeCharsIndex);
			datatypeCharsInputRecordIndex = increasedCN;
		    
		    DatatypedActiveTypeItem[] increasedDef = new DatatypedActiveTypeItem[size];
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
        
        messageTotalCount -= (datatypeCharsIndex+1);
        
        for(int i = 0; i <= datatypeCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeCharsInputRecordIndex[i], this);
        }
        
        datatypeCharsInputRecordIndex = null;
        datatypeCharsDefinition = null;
        datatypeCharsErrorMessage = null;
        datatypeCharsIndex = -1;
    }
     
    
    //{16}
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        
		messageTotalCount++;
		if(datatypeAVIndex < 0){
			datatypeAVIndex = 0;	
			datatypeAVInputRecordIndex =new int[initialSize];
			datatypeAVDefinition = new DatatypedActiveTypeItem[initialSize];
			datatypeAVErrorMessage = new String[initialSize];
		}else if(++datatypeAVIndex == datatypeAVInputRecordIndex.length){
		    int size = datatypeAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(datatypeAVInputRecordIndex, 0, increasedCN, 0, datatypeAVIndex);
			datatypeAVInputRecordIndex = increasedCN;
		    
		    DatatypedActiveTypeItem[] increasedDef = new DatatypedActiveTypeItem[size];
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
        
        messageTotalCount -= (datatypeAVIndex+1);
        
        for(int i = 0; i <= datatypeAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeAVInputRecordIndex[i], this);
        }
        
        datatypeAVInputRecordIndex = null;
        datatypeAVDefinition = null;
        datatypeAVErrorMessage = null;
        datatypeAVIndex = -1;
    }
        
        
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
        		
		messageTotalCount++;
		if(valueCharsIndex < 0){
			valueCharsIndex = 0;	
			valueCharsInputRecordIndex =new int[initialSize];
			valueCharsDefinition = new AValue[initialSize];
		}else if(++valueCharsIndex == valueCharsInputRecordIndex.length){
		    int size = valueCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueCharsInputRecordIndex, 0, increasedCN, 0, valueCharsIndex);
			valueCharsInputRecordIndex = increasedCN;
		    
		    AValue[] increasedDef = new AValue[size];
			System.arraycopy(valueCharsDefinition, 0, increasedDef, 0, valueCharsIndex);
			valueCharsDefinition = increasedDef;
		}
		
		valueCharsInputRecordIndex[valueCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueCharsDefinition[valueCharsIndex] = charsDefinition;
	}
    public void clearCharacterContentValueError(){
        
        messageTotalCount -= (valueCharsIndex+1);
        
        for(int i = 0; i <= valueCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueCharsInputRecordIndex[i], this);
        }
        
        valueCharsInputRecordIndex = null;
        valueCharsDefinition = null;
        valueCharsIndex = -1;
    }
    
    
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
        
		messageTotalCount++;
		if(valueAVIndex < 0){
			valueAVIndex = 0;	
			valueAVInputRecordIndex =new int[initialSize];
			valueAVDefinition = new AValue[initialSize];
		}else if(++valueAVIndex == valueAVInputRecordIndex.length){
		    int size = valueAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueAVInputRecordIndex, 0, increasedCN, 0, valueAVIndex);
			valueAVInputRecordIndex = increasedCN;
		    
		    AValue[] increasedDef = new AValue[size];
			System.arraycopy(valueAVDefinition, 0, increasedDef, 0, valueAVIndex);
			valueAVDefinition = increasedDef;
		}
		
		valueAVInputRecordIndex[valueAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueAVDefinition[valueAVIndex] = charsDefinition;
	}
	public void clearAttributeValueValueError(){
        
        messageTotalCount -= (valueAVIndex+1);
        
        for(int i = 0; i <= valueAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueAVInputRecordIndex[i], this);
        }
        
        valueAVInputRecordIndex = null;
        valueAVDefinition = null;
        valueAVIndex = -1;
    }
    
    
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
        
		messageTotalCount++;
		if(exceptCharsIndex < 0){
			exceptCharsIndex = 0;	
			exceptCharsInputRecordIndex =new int[initialSize];
			exceptCharsDefinition = new AData[initialSize];
		}else if(++exceptCharsIndex == exceptCharsInputRecordIndex.length){
		    int size = exceptCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptCharsInputRecordIndex, 0, increasedCN, 0, exceptCharsIndex);
			exceptCharsInputRecordIndex = increasedCN;
		    
		    AData[] increasedDef = new AData[size];
			System.arraycopy(exceptCharsDefinition, 0, increasedDef, 0, exceptCharsIndex);
			exceptCharsDefinition = increasedDef;
		}
		
		exceptCharsInputRecordIndex[exceptCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptCharsDefinition[exceptCharsIndex] = charsDefinition;
	}
    public void clearCharacterContentExceptedError(){
        
        messageTotalCount -= (exceptCharsIndex+1);
        
        for(int i = 0; i <= exceptCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptCharsInputRecordIndex[i], this);
        }
        
        exceptCharsInputRecordIndex = null;
        exceptCharsDefinition = null;
        exceptCharsIndex = -1;
    }
    
    
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
        
		messageTotalCount++;
		if(exceptAVIndex < 0){
			exceptAVIndex = 0;	
			exceptAVInputRecordIndex =new int[initialSize];
			exceptAVDefinition = new AData[initialSize];
		}else if(++exceptAVIndex == exceptAVInputRecordIndex.length){
		    int size = exceptAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptAVInputRecordIndex, 0, increasedCN, 0, exceptAVIndex);
			exceptAVInputRecordIndex = increasedCN;
		    
		    AData[] increasedDef = new AData[size];
			System.arraycopy(exceptAVDefinition, 0, increasedDef, 0, exceptAVIndex);
			exceptAVDefinition = increasedDef;
		}
		
		exceptAVInputRecordIndex[exceptAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptAVDefinition[exceptAVIndex] = charsDefinition;
	}
	public void clearAttributeValueExceptedError(){
        
        messageTotalCount -= (exceptAVIndex+1);
        
        for(int i = 0; i <= exceptAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptAVInputRecordIndex[i], this);
        }
        
        exceptAVInputRecordIndex = null;
        exceptAVDefinition = null;
        exceptAVIndex = -1;
    }
    
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
        
		messageTotalCount++;
		if(unexpectedCharsIndex < 0){
			unexpectedCharsIndex = 0;	
			unexpectedCharsInputRecordIndex =new int[initialSize];
			unexpectedCharsDefinition = new AElement[initialSize];
		}else if(++unexpectedCharsIndex == unexpectedCharsInputRecordIndex.length){
		    int size = unexpectedCharsInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unexpectedCharsInputRecordIndex, 0, increasedCN, 0, unexpectedCharsIndex);
			unexpectedCharsInputRecordIndex = increasedCN;
		    
		    AElement[] increasedDef = new AElement[size];
			System.arraycopy(unexpectedCharsDefinition, 0, increasedDef, 0, unexpectedCharsIndex);
			unexpectedCharsDefinition = increasedDef;
		}
		
		unexpectedCharsInputRecordIndex[unexpectedCharsIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unexpectedCharsDefinition[unexpectedCharsIndex] = elementDefinition;
	}
    public void clearUnexpectedCharacterContent(){
        
        messageTotalCount -= (unexpectedCharsIndex+1);
        
        for(int i = 0; i <= unexpectedCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(unexpectedCharsInputRecordIndex[i], this);
        }
        
        unexpectedCharsInputRecordIndex = null;
        unexpectedCharsDefinition = null;
        unexpectedCharsIndex = -1;
    }
    
    
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
        
		messageTotalCount++;
		if(unexpectedAVIndex < 0){
			unexpectedAVIndex = 0;	
			unexpectedAVInputRecordIndex =new int[initialSize];
			unexpectedAVDefinition = new AAttribute[initialSize];
		}else if(++unexpectedAVIndex == unexpectedAVInputRecordIndex.length){
		    int size = unexpectedAVInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unexpectedAVInputRecordIndex, 0, increasedCN, 0, unexpectedAVIndex);
			unexpectedAVInputRecordIndex = increasedCN;
		    
		    AAttribute[] increasedDef = new AAttribute[size];
			System.arraycopy(unexpectedAVDefinition, 0, increasedDef, 0, unexpectedAVIndex);
			unexpectedAVDefinition = increasedDef;
		}
		
		unexpectedAVInputRecordIndex[unexpectedAVIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unexpectedAVDefinition[unexpectedAVIndex] = attributeDefinition;
	}
	public void clearUnexpectedAttributeValue(){
        
        messageTotalCount -= (unexpectedAVIndex+1);
        
        for(int i = 0; i <= unexpectedAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(unexpectedAVInputRecordIndex[i], this);
        }
        
        unexpectedAVInputRecordIndex = null;
        unexpectedAVDefinition = null;
        unexpectedAVIndex = -1;
    }
    
	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        
		messageTotalCount++;
		if(unresolvedCharsIndexEE < 0){
			unresolvedCharsIndexEE = 0;	
			unresolvedCharsInputRecordIndexEE =new int[initialSize];
			unresolvedCharsDefinitionEE = new CharsActiveTypeItem[initialSize][];
		}else if(++unresolvedCharsIndexEE == unresolvedCharsInputRecordIndexEE.length){
		    int size = unresolvedCharsInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedCharsInputRecordIndexEE, 0, increasedCN, 0, unresolvedCharsIndexEE);
			unresolvedCharsInputRecordIndexEE = increasedCN;
		    
		    CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[size][];
			System.arraycopy(unresolvedCharsDefinitionEE, 0, increasedDef, 0, unresolvedCharsIndexEE);
			unresolvedCharsDefinitionEE = increasedDef;
		}
		
		unresolvedCharsInputRecordIndexEE[unresolvedCharsIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedCharsDefinitionEE[unresolvedCharsIndexEE] = possibleDefinitions;
	}
    public void clearUnresolvedCharacterContent(){
        
        messageTotalCount -= (unresolvedCharsIndexEE+1);
        
        for(int i = 0; i <= unresolvedCharsIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedCharsInputRecordIndexEE[i], this);
        }
        
        unresolvedCharsInputRecordIndexEE = null;
        unresolvedCharsDefinitionEE = null;
        unresolvedCharsIndexEE = -1;
    }
    
	// {24}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        
		messageTotalCount++;
		if(unresolvedAVIndexEE < 0){
			unresolvedAVIndexEE = 0;	
			unresolvedAVInputRecordIndexEE =new int[initialSize];
			unresolvedAVDefinitionEE = new CharsActiveTypeItem[initialSize][];
		}else if(++unresolvedAVIndexEE == unresolvedAVInputRecordIndexEE.length){
		    int size = unresolvedAVInputRecordIndexEE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedAVInputRecordIndexEE, 0, increasedCN, 0, unresolvedAVIndexEE);
			unresolvedAVInputRecordIndexEE = increasedCN;
		    
		    CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[size][];
			System.arraycopy(unresolvedAVDefinitionEE, 0, increasedDef, 0, unresolvedAVIndexEE);
			unresolvedAVDefinitionEE = increasedDef;
		}
		
		unresolvedAVInputRecordIndexEE[unresolvedAVIndexEE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedAVDefinitionEE[unresolvedAVIndexEE] = possibleDefinitions;
	}
	public void clearUnresolvedAttributeValue(){
        
        messageTotalCount -= (unresolvedAVIndexEE+1);
        
        for(int i = 0; i <= unresolvedAVIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAVInputRecordIndexEE[i], this);
        }
        
        unresolvedAVInputRecordIndexEE = null;
        unresolvedAVDefinitionEE = null;
        unresolvedAVIndexEE = -1;
    }
    
    
    // {25}
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        	    
		messageTotalCount++;
		if(datatypeTokenIndex < 0){
			datatypeTokenIndex = 0;	
			datatypeTokenInputRecordIndex =new int[initialSize];
			datatypeTokenDefinition = new DatatypedActiveTypeItem[initialSize];
			datatypeTokenErrorMessage = new String[initialSize];
		}else if(++datatypeTokenIndex == datatypeTokenInputRecordIndex.length){
		    int size = datatypeTokenInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(datatypeTokenInputRecordIndex, 0, increasedCN, 0, datatypeTokenIndex);
			datatypeTokenInputRecordIndex = increasedCN;
		    
		    DatatypedActiveTypeItem[] increasedDef = new DatatypedActiveTypeItem[size];
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
        
        messageTotalCount -= (datatypeTokenIndex+1);
        
        for(int i = 0; i <= datatypeTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeTokenInputRecordIndex[i], this);
        }
        
        datatypeTokenInputRecordIndex = null;
        datatypeTokenDefinition = null;
        datatypeTokenErrorMessage = null;
        datatypeTokenIndex = -1;
    }
    
        
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
        
		messageTotalCount++;
		if(valueTokenIndex < 0){
			valueTokenIndex = 0;	
			valueTokenInputRecordIndex =new int[initialSize];
			valueTokenDefinition = new AValue[initialSize];
		}else if(++valueTokenIndex == valueTokenInputRecordIndex.length){
		    int size = valueTokenInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(valueTokenInputRecordIndex, 0, increasedCN, 0, valueTokenIndex);
			valueTokenInputRecordIndex = increasedCN;
		    
		    AValue[] increasedDef = new AValue[size];
			System.arraycopy(valueTokenDefinition, 0, increasedDef, 0, valueTokenIndex);
			valueTokenDefinition = increasedDef;
		}
		
		valueTokenInputRecordIndex[valueTokenIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		valueTokenDefinition[valueTokenIndex] = charsDefinition;
	}
    public void clearListTokenValueError(){
        
        messageTotalCount -= (valueTokenIndex+1);
        
        for(int i = 0; i <= valueTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueTokenInputRecordIndex[i], this);
        }
        
        valueTokenInputRecordIndex = null;
        valueTokenDefinition = null;
        valueTokenIndex = -1;
    }
    
    
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
        
		messageTotalCount++;
		if(exceptTokenIndex < 0){
			exceptTokenIndex = 0;	
			exceptTokenInputRecordIndex =new int[initialSize];
			exceptTokenDefinition = new AData[initialSize];
		}else if(++exceptTokenIndex == exceptTokenInputRecordIndex.length){
		    int size = exceptTokenInputRecordIndex.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(exceptTokenInputRecordIndex, 0, increasedCN, 0, exceptTokenIndex);
			exceptTokenInputRecordIndex = increasedCN;
		    
		    AData[] increasedDef = new AData[size];
			System.arraycopy(exceptTokenDefinition, 0, increasedDef, 0, exceptTokenIndex);
			exceptTokenDefinition = increasedDef;
		}
		
		exceptTokenInputRecordIndex[exceptTokenIndex] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		exceptTokenDefinition[exceptTokenIndex] = charsDefinition;
	}
    public void clearListTokenExceptedError(){
        
        messageTotalCount -= (exceptTokenIndex+1);
        
        for(int i = 0; i <= exceptTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptTokenInputRecordIndex[i], this);
        }
        
        exceptTokenInputRecordIndex = null;
        exceptTokenDefinition = null;
        exceptTokenIndex = -1;
    }
    	    
    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        
		messageTotalCount++;
		if(unresolvedTokenIndexLPICE < 0){
			unresolvedTokenIndexLPICE = 0;	
			unresolvedTokenInputRecordIndexLPICE =new int[initialSize];
			unresolvedTokenDefinitionLPICE = new CharsActiveTypeItem[initialSize][];
		}else if(++unresolvedTokenIndexLPICE == unresolvedTokenInputRecordIndexLPICE.length){
		    int size = unresolvedTokenInputRecordIndexLPICE.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(unresolvedTokenInputRecordIndexLPICE, 0, increasedCN, 0, unresolvedTokenIndexLPICE);
			unresolvedTokenInputRecordIndexLPICE = increasedCN;
		    
		    CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[size][];
			System.arraycopy(unresolvedTokenDefinitionLPICE, 0, increasedDef, 0, unresolvedTokenIndexLPICE);
			unresolvedTokenDefinitionLPICE = increasedDef;
		}
		
		unresolvedTokenInputRecordIndexLPICE[unresolvedTokenIndexLPICE] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		unresolvedTokenDefinitionLPICE[unresolvedTokenIndexLPICE] = possibleDefinitions;
    }
    public void clearUnresolvedListTokenInContextError(){
        
        messageTotalCount -= (unresolvedTokenIndexLPICE+1);
        
        for(int i = 0; i <= unresolvedTokenIndexLPICE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedTokenInputRecordIndexLPICE[i], this);
        }
        
        unresolvedTokenInputRecordIndexLPICE = null;
        unresolvedTokenDefinitionLPICE = null;
        unresolvedTokenIndexLPICE = -1;
    }
    
    
    
    public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        
		messageTotalCount++;
		if(ambiguousTokenIndexLPICW < 0){
			ambiguousTokenIndexLPICW = 0;	
			ambiguousTokenInputRecordIndexLPICW =new int[initialSize];
			ambiguousTokenDefinitionLPICW = new CharsActiveTypeItem[initialSize][];
		}else if(++ambiguousTokenIndexLPICW == ambiguousTokenInputRecordIndexLPICW.length){
		    int size = ambiguousTokenInputRecordIndexLPICW.length + increaseSizeAmount;
		    
		    int[] increasedCN = new int[size];
			System.arraycopy(ambiguousTokenInputRecordIndexLPICW, 0, increasedCN, 0, ambiguousTokenIndexLPICW);
			ambiguousTokenInputRecordIndexLPICW = increasedCN;
		    
		    CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[size][];
			System.arraycopy(ambiguousTokenDefinitionLPICW, 0, increasedDef, 0, ambiguousTokenIndexLPICW);
			ambiguousTokenDefinitionLPICW = increasedDef;
		}
		
		ambiguousTokenInputRecordIndexLPICW[ambiguousTokenIndexLPICW] = inputRecordIndex;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		ambiguousTokenDefinitionLPICW[ambiguousTokenIndexLPICW] = possibleDefinitions;
    }    
    public void clearAmbiguousListTokenInContextWarning(){
        
        messageTotalCount -= (ambiguousTokenIndexLPICW+1);
        
        for(int i = 0; i <= ambiguousTokenIndexLPICW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousTokenInputRecordIndexLPICW[i], this);
        }
        
        ambiguousTokenInputRecordIndexLPICW = null;
        ambiguousTokenDefinitionLPICW = null;
        ambiguousTokenIndexLPICW = -1;
    }
    
    
	public void missingCompositorContent(Rule context, 
								int startInputRecordIndex,								 
								APattern definition, 
								int expected, 
								int found){
        
		if(missingCompositorContentIndex < 0){
			missingCompositorContentIndex = 0;
			missingCompositorContentContext = new APattern[initialSize];
			missingCompositorContentStartInputRecordIndex = new int[initialSize];
			missingCompositorContentDefinition = new APattern[initialSize];
			missingCompositorContentExpected = new int[initialSize];
			missingCompositorContentFound = new int[initialSize];			
		}else if(++missingCompositorContentIndex == missingCompositorContentContext.length){
		    int size = missingCompositorContentIndex+ increaseSizeAmount;
		    
			APattern[] increasedEC = new APattern[size];
			System.arraycopy(missingCompositorContentContext, 0, increasedEC, 0, missingCompositorContentIndex);
			missingCompositorContentContext = increasedEC;
			
			int[] increasedSCN = new int[size];
			System.arraycopy(missingCompositorContentStartInputRecordIndex, 0, increasedSCN, 0, missingCompositorContentIndex);
			missingCompositorContentStartInputRecordIndex = increasedSCN;
			
			APattern[] increasedED = new APattern[size];
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
    }
    
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
        messageTotalCount++;
        this.conflictResolutionId = conflictResolutionId;
        this.candidatesCount = candidatesCount;
        this.commonMessages = commonMessages;
        this.disqualified = disqualified;
        this.candidateMessages = candidateMessages;
    }
    
    public void clearConflict(){
        messageTotalCount--;
        conflictResolutionId = RESOLVED;
        candidatesCount = -1;
        if(commonMessages != null){
            if(isMessageRetrieved || isDiscarded)commonMessages.clear(); // It can be only one because it is about this context.
            commonMessages = null;
        }
        
        disqualified = null;
        
        if(candidateMessages != null){
            if(isMessageRetrieved || isDiscarded){
                for(MessageReporter cm : candidateMessages){                    
                    if(cm != null){
                        cm.clear();
                    }
                }
            }
            candidateMessages = null;
        }
    }
    
	
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
        }else if(errorId == UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR){
            if(unresolvedUnresolvedElementIndexEE < 0) throw new IllegalArgumentException();
            unresolvedUnresolvedElementIndexEE--;
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
    
    
    public void clear(){
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
        
        messageTotalCount = 0;
        
        if(parent != null){
            parent.clear();
            parent = null;
        }
    
        reportingContextType = ContextErrorHandler.NONE;
        reportingContextQName = null;
        reportingContextDefinition = null;
        reportingContextPublicId = null;
        reportingContextSystemId = null;
        reportingContextLineNumber = -1;
        reportingContextColumnNumber = -1;
        conflictResolutionId = RESOLVED;
        
        isMessageRetrieved = false;
        isDiscarded = false;
    }
    
    public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }   
}
