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
	
	// {12}
	int[] ambiguousElementFECEE;

	// {13}
	int[] ambiguousAttributeFECEE;

	// {14}
	int[] ambiguousCharsFECEE;
		
    // {w1}
	int[] ambiguousElementFECWW;

	// {w2}
	int[] ambiguousAttributeFECWW;

	// {w3}
	int[] ambiguousCharsFECWW;
	
	// {15}
	int[] datatypeFECCC;
	
	// {16}
	int[] datatypeFECAV;
   
	
	// {17}
	int[] valueFECCC;
	
	// {18}
	int[] valueFECAV;
	
	// {19}
	int[] exceptFECCC;
	
	// {20}
	int[] exceptFECAV;
	
	// {21}
	int[] unexpectedFECCC;
	
	// {22}
	int[] unexpectedFECAV;
	
	
	// {23}
	int[] ambiguousFECCC;
	
	// {24}
	int[] ambiguousFECAV;
	
	
	// {25}
	int[] datatypeFECLP;
    	
	// {26}
	int[] valueFECLP;
	
	// {27}
	int[] exceptFECLP;
	
	// {28}
	int[] ambiguousFECLP;
    
    // {28_1}
	int[] ambiguousFECLPICE;
    
    // {28_2}
	int[] ambiguousFECLPICW;
    
	
	// {29}
	int[] missingCompositorContentFEC;
    
    // {30}
    
    
	public ConflictMessageHandler(MessageWriter debugWriter){
		super(debugWriter);
        conflictResolutionId = RESOLVED;        
	}	
	
        
	public void unknownElement(int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
		if(unknownElementSize == 0){
			unknownElementSize = 1;
			unknownElementIndex = 0;	
			unknownElementQName = new String[unknownElementSize];			
			unknownElementSystemId = new String[unknownElementSize];			
			unknownElementLineNumber = new int[unknownElementSize];
			unknownElementColumnNumber = new int[unknownElementSize];
            unknownElementFEC = new int[unknownElementSize];
		}else if(++unknownElementIndex == unknownElementSize){			
			String[] increasedQN = new String[++unknownElementSize];
			System.arraycopy(unknownElementQName, 0, increasedQN, 0, unknownElementIndex);
			unknownElementQName = increasedQN;
			
			String[] increasedSI = new String[unknownElementSize];
			System.arraycopy(unknownElementSystemId, 0, increasedSI, 0, unknownElementIndex);
			unknownElementSystemId = increasedSI;
						
			int[] increasedLN = new int[unknownElementSize];
			System.arraycopy(unknownElementLineNumber, 0, increasedLN, 0, unknownElementIndex);
			unknownElementLineNumber = increasedLN;
			
			int[] increasedCN = new int[unknownElementSize];
			System.arraycopy(unknownElementColumnNumber, 0, increasedCN, 0, unknownElementIndex);
			unknownElementColumnNumber = increasedCN;
            
            int[] increasedFEC = new int[unknownElementSize];
			System.arraycopy(unknownElementFEC, 0, increasedFEC, 0, unknownElementIndex);
			unknownElementFEC = increasedFEC;
		}
		unknownElementQName[unknownElementIndex] = qName;
		unknownElementSystemId[unknownElementIndex] = systemId;
		unknownElementLineNumber[unknownElementIndex] = lineNumber;
		unknownElementColumnNumber[unknownElementIndex] = columnNumber;
        unknownElementFEC[unknownElementIndex] = functionalEquivalenceCode;
	}
	
    public void clearUnknownElement(){
        errorTotalCount -= unknownElementSize; 
        unknownElementSize = 0;
        unknownElementIndex = -1;	
        unknownElementQName = null;			
        unknownElementSystemId = null;			
        unknownElementLineNumber = null;
        unknownElementColumnNumber = null;
        
        unknownElementFEC = null;
    }	
    public void clearUnknownElement(int messageId){
        int removeIndex = getRemoveIndex(messageId, unknownElementFEC);
        int moved = unknownElementIndex - removeIndex;
        unknownElementIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(unknownElementQName, removeIndex+1, unknownElementQName, removeIndex, moved);
            System.arraycopy(unknownElementQName, removeIndex+1, unknownElementQName, removeIndex, moved);
            System.arraycopy(unknownElementLineNumber, removeIndex+1, unknownElementLineNumber, removeIndex, moved);
            System.arraycopy(unknownElementColumnNumber, removeIndex+1, unknownElementColumnNumber, removeIndex, moved);
            System.arraycopy(unknownElementFEC, removeIndex+1, unknownElementFEC, removeIndex, moved);               
        }
    }
    
	public void unexpectedElement(int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
		if(unexpectedElementSize == 0){
			unexpectedElementSize = 1;
			unexpectedElementIndex = 0;	
			unexpectedElementQName = new String[unexpectedElementSize];
			unexpectedElementDefinition = new SimplifiedComponent[unexpectedElementSize];
			unexpectedElementSystemId = new String[unexpectedElementSize];			
			unexpectedElementLineNumber = new int[unexpectedElementSize];
			unexpectedElementColumnNumber = new int[unexpectedElementSize];
            unexpectedElementFEC = new int[unexpectedElementSize];			
		}else if(++unexpectedElementIndex == unexpectedElementSize){			
			String[] increasedQN = new String[++unexpectedElementSize];
			System.arraycopy(unexpectedElementQName, 0, increasedQN, 0, unexpectedElementIndex);
			unexpectedElementQName = increasedQN;
			
			SimplifiedComponent[] increasedDef = new SimplifiedComponent[unexpectedElementSize];
			System.arraycopy(unexpectedElementDefinition, 0, increasedDef, 0, unexpectedElementIndex);
			unexpectedElementDefinition = increasedDef;
			
			String[] increasedSI = new String[unexpectedElementSize];
			System.arraycopy(unexpectedElementSystemId, 0, increasedSI, 0, unexpectedElementIndex);
			unexpectedElementSystemId = increasedSI;
						
			int[] increasedLN = new int[unexpectedElementSize];
			System.arraycopy(unexpectedElementLineNumber, 0, increasedLN, 0, unexpectedElementIndex);
			unexpectedElementLineNumber = increasedLN;
			
			int[] increasedCN = new int[unexpectedElementSize];
			System.arraycopy(unexpectedElementColumnNumber, 0, increasedCN, 0, unexpectedElementIndex);
			unexpectedElementColumnNumber = increasedCN;
            
            int[] increasedFEC = new int[unexpectedElementSize];
			System.arraycopy(unexpectedElementFEC, 0, increasedFEC, 0, unexpectedElementIndex);
			unexpectedElementFEC = increasedFEC;
		}
		unexpectedElementQName[unexpectedElementIndex] = qName;
		unexpectedElementDefinition[unexpectedElementIndex] = definition;
		unexpectedElementSystemId[unexpectedElementIndex] = systemId;
		unexpectedElementLineNumber[unexpectedElementIndex] = lineNumber;
		unexpectedElementColumnNumber[unexpectedElementIndex] = columnNumber;
        unexpectedElementFEC[unexpectedElementIndex] = functionalEquivalenceCode;
	}
		
	public void clearUnexpectedElement(){
        errorTotalCount -= unexpectedElementSize;
        unexpectedElementSize = 0;
        unexpectedElementIndex = -1;	
        unexpectedElementQName = null;
        unexpectedElementDefinition = null;
        unexpectedElementSystemId = null;			
        unexpectedElementLineNumber = null;
        unexpectedElementColumnNumber = null;
        
        unexpectedElementFEC = null;
    }
    
    public void clearUnexpectedElement(int messageId){
        int removeIndex = getRemoveIndex(messageId, unexpectedElementFEC);
        int moved = unexpectedElementIndex - removeIndex;
        errorTotalCount--;
        unexpectedElementIndex--;
        if(moved > 0){
            System.arraycopy(unexpectedElementQName, removeIndex+1, unexpectedElementQName, removeIndex, moved);
            System.arraycopy(unexpectedElementDefinition, removeIndex+1, unexpectedElementDefinition, removeIndex, moved);
            System.arraycopy(unexpectedElementSystemId, removeIndex+1, unexpectedElementSystemId, removeIndex, moved);
            System.arraycopy(unexpectedElementLineNumber, removeIndex+1, unexpectedElementLineNumber, removeIndex, moved);
            System.arraycopy(unexpectedElementColumnNumber, removeIndex+1, unexpectedElementColumnNumber, removeIndex, moved);
            System.arraycopy(unexpectedElementFEC, removeIndex+1, unexpectedElementFEC, removeIndex, moved);
        }
    }
    
    
	public void unexpectedAmbiguousElement(int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
		if(unexpectedAmbiguousElementSize == 0){
			unexpectedAmbiguousElementSize = 1;
			unexpectedAmbiguousElementIndex = 0;	
			unexpectedAmbiguousElementQName = new String[unexpectedAmbiguousElementSize];
			unexpectedAmbiguousElementDefinition = new SimplifiedComponent[unexpectedAmbiguousElementSize][];
			unexpectedAmbiguousElementSystemId = new String[unexpectedAmbiguousElementSize];			
			unexpectedAmbiguousElementLineNumber = new int[unexpectedAmbiguousElementSize];
			unexpectedAmbiguousElementColumnNumber = new int[unexpectedAmbiguousElementSize];
            unexpectedAmbiguousElementFEC = new int[unexpectedAmbiguousElementSize];			
		}else if(++unexpectedAmbiguousElementIndex == unexpectedAmbiguousElementSize){			
			String[] increasedQN = new String[++unexpectedAmbiguousElementSize];
			System.arraycopy(unexpectedAmbiguousElementQName, 0, increasedQN, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementQName = increasedQN;
			
			SimplifiedComponent[][] increasedDef = new SimplifiedComponent[unexpectedAmbiguousElementSize][];
			System.arraycopy(unexpectedAmbiguousElementDefinition, 0, increasedDef, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementDefinition = increasedDef;
			
			String[] increasedSI = new String[unexpectedAmbiguousElementSize];
			System.arraycopy(unexpectedAmbiguousElementSystemId, 0, increasedSI, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementSystemId = increasedSI;
						
			int[] increasedLN = new int[unexpectedAmbiguousElementSize];
			System.arraycopy(unexpectedAmbiguousElementLineNumber, 0, increasedLN, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementLineNumber = increasedLN;
			
			int[] increasedCN = new int[unexpectedAmbiguousElementSize];
			System.arraycopy(unexpectedAmbiguousElementColumnNumber, 0, increasedCN, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementColumnNumber = increasedCN;
            
            int[] increasedFEC = new int[unexpectedAmbiguousElementSize];
			System.arraycopy(unexpectedAmbiguousElementFEC, 0, increasedFEC, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementFEC = increasedFEC;
		}
		unexpectedAmbiguousElementQName[unexpectedAmbiguousElementIndex] = qName;
		unexpectedAmbiguousElementDefinition[unexpectedAmbiguousElementIndex] = possibleDefinitions;
		unexpectedAmbiguousElementSystemId[unexpectedAmbiguousElementIndex] = systemId;
		unexpectedAmbiguousElementLineNumber[unexpectedAmbiguousElementIndex] = lineNumber;
		unexpectedAmbiguousElementColumnNumber[unexpectedAmbiguousElementIndex] = columnNumber;
        unexpectedAmbiguousElementFEC[unexpectedAmbiguousElementIndex] = functionalEquivalenceCode;
	}
	
	public void clearUnexpectedAmbiguousElement(){
        errorTotalCount -= unexpectedAmbiguousElementSize;
        unexpectedAmbiguousElementSize = 0;
        unexpectedAmbiguousElementIndex = -1;	
        unexpectedAmbiguousElementQName = null;
        unexpectedAmbiguousElementDefinition = null;
        unexpectedAmbiguousElementSystemId = null;			
        unexpectedAmbiguousElementLineNumber = null;
        unexpectedAmbiguousElementColumnNumber = null;
        
        unexpectedAmbiguousElementFEC = null;
    }
    public void clearUnexpectedAmbiguousElement(int messageId){
        int removeIndex = getRemoveIndex(messageId, unexpectedAmbiguousElementFEC);
        int moved = unexpectedAmbiguousElementIndex - removeIndex;
        unexpectedAmbiguousElementIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(unexpectedAmbiguousElementQName, removeIndex+1, unexpectedAmbiguousElementQName, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousElementDefinition, removeIndex+1, unexpectedAmbiguousElementDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousElementSystemId, removeIndex+1, unexpectedAmbiguousElementSystemId, removeIndex, moved);	
            System.arraycopy(unexpectedAmbiguousElementLineNumber, removeIndex+1, unexpectedAmbiguousElementLineNumber, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousElementColumnNumber, removeIndex+1, unexpectedAmbiguousElementColumnNumber, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousElementFEC, removeIndex+1, unexpectedAmbiguousElementFEC, removeIndex, moved);
        }
    }
	

    public void unknownAttribute(int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
		if(unknownAttributeSize == 0){
			unknownAttributeSize = 1;
			unknownAttributeIndex = 0;	
			unknownAttributeQName = new String[unknownAttributeSize];			
			unknownAttributeSystemId = new String[unknownAttributeSize];			
			unknownAttributeLineNumber = new int[unknownAttributeSize];
			unknownAttributeColumnNumber = new int[unknownAttributeSize];			
		}else if(++unknownAttributeIndex == unknownAttributeSize){			
			String[] increasedQN = new String[++unknownAttributeSize];
			System.arraycopy(unknownAttributeQName, 0, increasedQN, 0, unknownAttributeIndex);
			unknownAttributeQName = increasedQN;
			
			String[] increasedSI = new String[unknownAttributeSize];
			System.arraycopy(unknownAttributeSystemId, 0, increasedSI, 0, unknownAttributeIndex);
			unknownAttributeSystemId = increasedSI;
						
			int[] increasedLN = new int[unknownAttributeSize];
			System.arraycopy(unknownAttributeLineNumber, 0, increasedLN, 0, unknownAttributeIndex);
			unknownAttributeLineNumber = increasedLN;
			
			int[] increasedCN = new int[unknownAttributeSize];
			System.arraycopy(unknownAttributeColumnNumber, 0, increasedCN, 0, unknownAttributeIndex);
			unknownAttributeColumnNumber = increasedCN;
            
            int[] increasedFEC = new int[unknownAttributeSize];
			System.arraycopy(unknownAttributeFEC, 0, increasedFEC, 0, unknownAttributeIndex);
			unknownAttributeFEC = increasedFEC;
		}
		unknownAttributeQName[unknownAttributeIndex] = qName;
		unknownAttributeSystemId[unknownAttributeIndex] = systemId;
		unknownAttributeLineNumber[unknownAttributeIndex] = lineNumber;
		unknownAttributeColumnNumber[unknownAttributeIndex] = columnNumber;
        unknownAttributeFEC[unknownAttributeIndex] = functionalEquivalenceCode;
	}
	public void clearUnknownAttribute(){
        errorTotalCount -= unknownAttributeSize;
        unknownAttributeSize = 0;
        unknownAttributeIndex = -1;	
        unknownAttributeQName = null;			
        unknownAttributeSystemId = null;			
        unknownAttributeLineNumber = null;
        unknownAttributeColumnNumber = null;
        
        unknownAttributeFEC = null;
    }
    public void clearUnknownAttribute(int messageId){
        int removeIndex = getRemoveIndex(messageId, unknownAttributeFEC);
        int moved = unknownAttributeIndex - removeIndex;
        unknownAttributeIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(unknownAttributeQName, removeIndex+1, unknownAttributeQName, removeIndex, moved);
            System.arraycopy(unknownAttributeSystemId, removeIndex+1, unknownAttributeSystemId, removeIndex, moved);
            System.arraycopy(unknownAttributeLineNumber, removeIndex+1, unknownAttributeLineNumber, removeIndex, moved);
            System.arraycopy(unknownAttributeColumnNumber, removeIndex+1, unknownAttributeColumnNumber, removeIndex, moved);
            System.arraycopy(unknownAttributeFEC, removeIndex+1, unknownAttributeFEC, removeIndex, moved);
        }
    }
	
    
	public void unexpectedAttribute(int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        
        errorTotalCount++;
		if(unexpectedAttributeSize == 0){
			unexpectedAttributeSize = 1;
			unexpectedAttributeIndex = 0;	
			unexpectedAttributeQName = new String[unexpectedAttributeSize];
			unexpectedAttributeDefinition = new SimplifiedComponent[unexpectedAttributeSize];
			unexpectedAttributeSystemId = new String[unexpectedAttributeSize];			
			unexpectedAttributeLineNumber = new int[unexpectedAttributeSize];
			unexpectedAttributeColumnNumber = new int[unexpectedAttributeSize];
            unexpectedAttributeFEC = new int[unexpectedAttributeSize];			
		}else if(++unexpectedAttributeIndex == unexpectedAttributeSize){			
			String[] increasedQN = new String[++unexpectedAttributeSize];
			System.arraycopy(unexpectedAttributeQName, 0, increasedQN, 0, unexpectedAttributeIndex);
			unexpectedAttributeQName = increasedQN;
			
			SimplifiedComponent[] increasedDef = new SimplifiedComponent[unexpectedAttributeSize];
			System.arraycopy(unexpectedAttributeDefinition, 0, increasedDef, 0, unexpectedAttributeIndex);
			unexpectedAttributeDefinition = increasedDef;
			
			String[] increasedSI = new String[unexpectedAttributeSize];
			System.arraycopy(unexpectedAttributeSystemId, 0, increasedSI, 0, unexpectedAttributeIndex);
			unexpectedAttributeSystemId = increasedSI;
						
			int[] increasedLN = new int[unexpectedAttributeSize];
			System.arraycopy(unexpectedAttributeLineNumber, 0, increasedLN, 0, unexpectedAttributeIndex);
			unexpectedAttributeLineNumber = increasedLN;
			
			int[] increasedCN = new int[unexpectedAttributeSize];
			System.arraycopy(unexpectedAttributeColumnNumber, 0, increasedCN, 0, unexpectedAttributeIndex);
			unexpectedAttributeColumnNumber = increasedCN;
            
            int[] increasedFEC = new int[unexpectedAttributeSize];
			System.arraycopy(unexpectedAttributeFEC, 0, increasedFEC, 0, unexpectedAttributeIndex);
			unexpectedAttributeFEC = increasedFEC;
		}
		unexpectedAttributeQName[unexpectedAttributeIndex] = qName;
		unexpectedAttributeDefinition[unexpectedAttributeIndex] = definition;
		unexpectedAttributeSystemId[unexpectedAttributeIndex] = systemId;
		unexpectedAttributeLineNumber[unexpectedAttributeIndex] = lineNumber;
		unexpectedAttributeColumnNumber[unexpectedAttributeIndex] = columnNumber;
        unexpectedAttributeFEC[unexpectedAttributeIndex] = functionalEquivalenceCode;
	}
	public void clearUnexpectedAttribute(){
        errorTotalCount -= unexpectedAttributeSize;
        unexpectedAttributeSize = 0;
        unexpectedAttributeIndex = -1;	
        unexpectedAttributeQName = null;
        unexpectedAttributeDefinition = null;
        unexpectedAttributeSystemId = null;			
        unexpectedAttributeLineNumber = null;
        unexpectedAttributeColumnNumber = null;
        
        unexpectedAttributeFEC = null;
    }
    public void clearUnexpectedAttribute(int messageId){
        int removeIndex = getRemoveIndex(messageId, unexpectedAttributeFEC);
        int moved = unexpectedAttributeIndex - removeIndex;
        unexpectedAttributeIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(unexpectedAttributeQName, removeIndex+1, unexpectedAttributeQName, removeIndex, moved);
            System.arraycopy(unexpectedAttributeDefinition, removeIndex+1, unexpectedAttributeDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAttributeSystemId, removeIndex+1, unexpectedAttributeSystemId, removeIndex, moved);
            System.arraycopy(unexpectedAttributeLineNumber, removeIndex+1, unexpectedAttributeLineNumber, removeIndex, moved);
            System.arraycopy(unexpectedAttributeColumnNumber, removeIndex+1, unexpectedAttributeColumnNumber, removeIndex, moved);
            System.arraycopy(unexpectedAttributeFEC, removeIndex+1, unexpectedAttributeFEC, removeIndex, moved);
        }
    }
	    
	
	public void unexpectedAmbiguousAttribute(int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
		if(unexpectedAmbiguousAttributeSize == 0){
			unexpectedAmbiguousAttributeSize = 1;
			unexpectedAmbiguousAttributeIndex = 0;	
			unexpectedAmbiguousAttributeQName = new String[unexpectedAmbiguousAttributeSize];
			unexpectedAmbiguousAttributeDefinition = new SimplifiedComponent[unexpectedAmbiguousAttributeSize][];
			unexpectedAmbiguousAttributeSystemId = new String[unexpectedAmbiguousAttributeSize];			
			unexpectedAmbiguousAttributeLineNumber = new int[unexpectedAmbiguousAttributeSize];
			unexpectedAmbiguousAttributeColumnNumber = new int[unexpectedAmbiguousAttributeSize];
            unexpectedAmbiguousAttributeFEC = new int[unexpectedAmbiguousAttributeSize];			
		}else if(++unexpectedAmbiguousAttributeIndex == unexpectedAmbiguousAttributeSize){			
			String[] increasedQN = new String[++unexpectedAmbiguousAttributeSize];
			System.arraycopy(unexpectedAmbiguousAttributeQName, 0, increasedQN, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeQName = increasedQN;
			
			SimplifiedComponent[][] increasedDef = new SimplifiedComponent[unexpectedAmbiguousAttributeSize][];
			System.arraycopy(unexpectedAmbiguousAttributeDefinition, 0, increasedDef, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeDefinition = increasedDef;
			
			String[] increasedSI = new String[unexpectedAmbiguousAttributeSize];
			System.arraycopy(unexpectedAmbiguousAttributeSystemId, 0, increasedSI, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeSystemId = increasedSI;
						
			int[] increasedLN = new int[unexpectedAmbiguousAttributeSize];
			System.arraycopy(unexpectedAmbiguousAttributeLineNumber, 0, increasedLN, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeLineNumber = increasedLN;
			
			int[] increasedCN = new int[unexpectedAmbiguousAttributeSize];
			System.arraycopy(unexpectedAmbiguousAttributeColumnNumber, 0, increasedCN, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeColumnNumber = increasedCN;
            
            int[] increasedFEC = new int[unexpectedAmbiguousAttributeSize];
			System.arraycopy(unexpectedAmbiguousAttributeFEC, 0, increasedFEC, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeFEC = increasedFEC;
		}
		unexpectedAmbiguousAttributeQName[unexpectedAmbiguousAttributeIndex] = qName;
		unexpectedAmbiguousAttributeDefinition[unexpectedAmbiguousAttributeIndex] = possibleDefinitions;
		unexpectedAmbiguousAttributeSystemId[unexpectedAmbiguousAttributeIndex] = systemId;
		unexpectedAmbiguousAttributeLineNumber[unexpectedAmbiguousAttributeIndex] = lineNumber;
		unexpectedAmbiguousAttributeColumnNumber[unexpectedAmbiguousAttributeIndex] = columnNumber;
        unexpectedAmbiguousAttributeFEC[unexpectedAmbiguousAttributeIndex] = functionalEquivalenceCode;
	}
	public void clearUnexpectedAmbiguousAttribute(){
        errorTotalCount -= unexpectedAmbiguousAttributeSize;
        unexpectedAmbiguousAttributeSize = 0;
        unexpectedAmbiguousAttributeIndex = -1;	
        unexpectedAmbiguousAttributeQName = null;
        unexpectedAmbiguousAttributeDefinition = null;
        unexpectedAmbiguousAttributeSystemId = null;			
        unexpectedAmbiguousAttributeLineNumber = null;
        unexpectedAmbiguousAttributeColumnNumber = null;
        
        unexpectedAmbiguousAttributeFEC = null;
    }
    public void clearUnexpectedAmbiguousAttribute(int messageId){
        int removeIndex = getRemoveIndex(messageId, unexpectedAmbiguousAttributeFEC);
        int moved = unexpectedAmbiguousAttributeIndex - removeIndex;
        unexpectedAmbiguousAttributeIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(unexpectedAmbiguousAttributeQName, removeIndex+1, unexpectedAmbiguousAttributeQName, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeDefinition, removeIndex+1, unexpectedAmbiguousAttributeDefinition, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeSystemId, removeIndex+1, unexpectedAmbiguousAttributeSystemId, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeLineNumber, removeIndex+1, unexpectedAmbiguousAttributeLineNumber, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeColumnNumber, removeIndex+1, unexpectedAmbiguousAttributeColumnNumber, removeIndex, moved);
            System.arraycopy(unexpectedAmbiguousAttributeFEC, removeIndex+1, unexpectedAmbiguousAttributeFEC, removeIndex, moved);
        }
    }	
    
	public void misplacedElement(int functionalEquivalenceCode, 
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
											APattern reper){//not stored, only used for internal conflict handling
        
		all: {	           
			if(misplacedSize == 0){
                errorTotalCount++;
				misplacedSize = 1;
				misplacedIndex = 0;	
				misplacedContext = new APattern[misplacedSize];	
				misplacedStartSystemId = new String[misplacedSize];			
				misplacedStartLineNumber = new int[misplacedSize];
				misplacedStartColumnNumber = new int[misplacedSize];
				misplacedDefinition = new APattern[misplacedSize][1];
				misplacedQName = new String[misplacedSize][1][1];
				misplacedSystemId = new String[misplacedSize][1][1];			
				misplacedLineNumber = new int[misplacedSize][1][1];
				misplacedColumnNumber = new int[misplacedSize][1][1];
				
                misplacedFEC = new int[misplacedSize];
                
				misplacedContext[misplacedIndex] = contextDefinition;
				misplacedStartSystemId[misplacedIndex] = startSystemId;
				misplacedStartLineNumber[misplacedIndex] = startLineNumber;
				misplacedStartColumnNumber[misplacedIndex] = startColumnNumber;
				misplacedDefinition[misplacedIndex][0] = definition; 
				misplacedQName[misplacedIndex][0][0] = qName; 		
				misplacedSystemId[misplacedIndex][0][0] = systemId;
				misplacedLineNumber[misplacedIndex][0][0] = lineNumber;
				misplacedColumnNumber[misplacedIndex][0][0] = columnNumber;
				
                misplacedFEC[misplacedIndex] = functionalEquivalenceCode;
                
				break all;
			}
            
			for(int i = 0; i < misplacedSize; i++){
				if(misplacedContext[i].equals(contextDefinition)
                    && misplacedStartSystemId[i].equals(startSystemId)			
                    && misplacedStartLineNumber[i] == startLineNumber
                    && misplacedStartColumnNumber[i] == startColumnNumber){
					for(int j = 0; j < misplacedDefinition[i].length; j++){
						if(misplacedDefinition[i][j].equals(definition)){
							int length = misplacedQName[i][j].length;
							String[] increasedQN = new String[(length+1)];
							System.arraycopy(misplacedQName[i][j], 0, increasedQN, 0, length);
							misplacedQName[i][j] = increasedQN;
							misplacedQName[i][j][length] = qName;
							
							length = misplacedSystemId[i][j].length;
							String[] increasedSI = new String[(length+1)];
							System.arraycopy(misplacedSystemId[i][j], 0, increasedSI, 0, length);
							misplacedSystemId[i][j] = increasedSI;
							misplacedSystemId[i][j][length] = systemId;
							
							length = misplacedLineNumber[i][j].length;
							int[] increasedLN = new int[(length+1)];
							System.arraycopy(misplacedLineNumber[i][j], 0, increasedLN, 0, length);
							misplacedLineNumber[i][j] = increasedLN;
							misplacedLineNumber[i][j][length] = lineNumber;
							
							length = misplacedColumnNumber[i][j].length;
							int[] increasedCN = new int[(length+1)];
							System.arraycopy(misplacedColumnNumber[i][j], 0, increasedCN, 0, length);
							misplacedColumnNumber[i][j] = increasedCN;
							misplacedColumnNumber[i][j][length] = columnNumber;
							
							break all;
						}
					}                   
					int length = misplacedDefinition[i].length;					
					APattern[] increasedDef = new APattern[(length+1)];					
					System.arraycopy(misplacedDefinition[i], 0, increasedDef, 0, length);
					misplacedDefinition[i] = increasedDef;
					misplacedDefinition[i][length] = definition; 
										
					
					String[][] increasedQN = new String[(length+1)][];
					System.arraycopy(misplacedQName[i], 0, increasedQN, 0, length);
					misplacedQName[i] = increasedQN;			
					misplacedQName[i][length] = new String[1];
					misplacedQName[i][length][0] = qName;
										
					String[][] increasedSI = new String[(length+1)][];
					System.arraycopy(misplacedSystemId[i], 0, increasedSI, 0, length);
					misplacedSystemId[i] = increasedSI;
					misplacedSystemId[i][length] = new String[1];
					misplacedSystemId[i][length][0] = systemId;
					
					int[][] increasedLN = new int[(length+1)][];
					System.arraycopy(misplacedLineNumber[i], 0, increasedLN, 0, length);
					misplacedLineNumber[i] = increasedLN;
					misplacedLineNumber[i][length] = new int[1];
					misplacedLineNumber[i][length][0] = lineNumber;
										
					int[][] increasedCN = new int[(length+1)][];
					System.arraycopy(misplacedColumnNumber[i], 0, increasedCN, 0, length);
					misplacedColumnNumber[i] = increasedCN;
					misplacedColumnNumber[i][length] = new int[1];
					misplacedColumnNumber[i][length][0] = columnNumber;
					
					break all;
				}
			}
            
            errorTotalCount++;
			APattern[] increasedCDef = new APattern[++misplacedSize];
                                                                  // ISSUE 194 added ++
			System.arraycopy(misplacedContext, 0, increasedCDef, 0, ++misplacedIndex);
			misplacedContext = increasedCDef;
            // ISSUE 194 removed ++
			misplacedContext[misplacedIndex] = contextDefinition;
			
			String[] increasedSSI = new String[misplacedSize];
			System.arraycopy(misplacedStartSystemId, 0, increasedSSI, 0, misplacedIndex);
			misplacedStartSystemId = increasedSSI;
			misplacedStartSystemId[misplacedIndex] = startSystemId;
			
			int[] increasedSLN = new int[misplacedSize];
			System.arraycopy(misplacedStartLineNumber, 0, increasedSLN, 0, misplacedIndex);
			misplacedStartLineNumber = increasedSLN;
			misplacedStartLineNumber[misplacedIndex] = startLineNumber;
			
			int[] increasedSCN = new int[misplacedSize];
			System.arraycopy(misplacedStartColumnNumber, 0, increasedSCN, 0, misplacedIndex);
			misplacedStartColumnNumber = increasedSCN;
			misplacedStartColumnNumber[misplacedIndex] = startColumnNumber;
			
			APattern[][] increasedDef = new APattern[misplacedSize][];
			System.arraycopy(misplacedDefinition, 0, increasedDef, 0, misplacedIndex);
			misplacedDefinition = increasedDef;
			misplacedDefinition[misplacedIndex] = new APattern[1];
			misplacedDefinition[misplacedIndex][0] = definition; 
			
			String[][][] increasedQN = new String[misplacedSize][][];
			System.arraycopy(misplacedQName, 0, increasedQN, 0, misplacedIndex);
			misplacedQName = increasedQN;
			misplacedQName[misplacedIndex] = new String[1][1];			
			misplacedQName[misplacedIndex][0][0] = qName; 			
			
			String[][][] increasedSI = new String[misplacedSize][][];
			System.arraycopy(misplacedSystemId, 0, increasedSI, 0, misplacedIndex);
			misplacedSystemId = increasedSI;
			misplacedSystemId[misplacedIndex] = new String[1][1];
			misplacedSystemId[misplacedIndex][0][0] = systemId;
						
			int[][][] increasedLN = new int[misplacedSize][][];
			System.arraycopy(misplacedLineNumber, 0, increasedLN, 0, misplacedIndex);
			misplacedLineNumber = increasedLN;
			misplacedLineNumber[misplacedIndex] = new int[1][1];
			misplacedLineNumber[misplacedIndex][0][0] = lineNumber;
			
			int[][][] increasedCN = new int[misplacedSize][][];
			System.arraycopy(misplacedColumnNumber, 0, increasedCN, 0, misplacedIndex);
			misplacedColumnNumber = increasedCN;
			misplacedColumnNumber[misplacedIndex] = new int[1][1];
			misplacedColumnNumber[misplacedIndex][0][0] = columnNumber;
            
            int[] increasedFEC = new int[misplacedSize];
			System.arraycopy(misplacedFEC, 0, increasedFEC, 0, misplacedIndex);
			misplacedFEC = increasedFEC;
			misplacedFEC[misplacedIndex] = functionalEquivalenceCode;
		}
		
	}
    public void misplacedElement(int functionalEquivalenceCode, 
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
											APattern reper){//not stored, only used for internal conflict handling
		/*
		for(int i = 0; i < qName.length; i++){	
			misplacedElement(contextDefinition, 
									startSystemId, 
									startLineNumber, 
									startColumnNumber,															
									definition, 
									qName[i],
									systemId[i], 
									lineNumber[i], 
									columnNumber[i],
									sourceDefinition[i],
									reper);
		}	*/
        
        all: {	           
			if(misplacedSize == 0){
                errorTotalCount++;
				misplacedSize = 1;
				misplacedIndex = 0;	
				misplacedContext = new APattern[misplacedSize];	
				misplacedStartSystemId = new String[misplacedSize];			
				misplacedStartLineNumber = new int[misplacedSize];
				misplacedStartColumnNumber = new int[misplacedSize];
				misplacedDefinition = new APattern[misplacedSize][1];
				misplacedQName = new String[misplacedSize][1][];
				misplacedSystemId = new String[misplacedSize][1][];			
				misplacedLineNumber = new int[misplacedSize][1][];
				misplacedColumnNumber = new int[misplacedSize][1][];
				
                misplacedFEC = new int[misplacedSize];
                
				misplacedContext[misplacedIndex] = contextDefinition;
				misplacedStartSystemId[misplacedIndex] = startSystemId;
				misplacedStartLineNumber[misplacedIndex] = startLineNumber;
				misplacedStartColumnNumber[misplacedIndex] = startColumnNumber;
				misplacedDefinition[misplacedIndex][0] = definition; 
				misplacedQName[misplacedIndex][0] = qName; 		
				misplacedSystemId[misplacedIndex][0] = systemId;
				misplacedLineNumber[misplacedIndex][0] = lineNumber;
				misplacedColumnNumber[misplacedIndex][0] = columnNumber;
				
                misplacedFEC[misplacedIndex] = functionalEquivalenceCode;
                
				break all;
			}
            
			for(int i = 0; i < misplacedSize; i++){
				if(misplacedContext[i].equals(contextDefinition)
                    && misplacedStartSystemId[i].equals(startSystemId)			
                    && misplacedStartLineNumber[i] == startLineNumber
                    && misplacedStartColumnNumber[i] == startColumnNumber){
					for(int j = 0; j < misplacedDefinition[i].length; j++){                        
						if(misplacedDefinition[i][j].equals(definition)){
                            int addedLength = qName.length; 
                            
							int length = misplacedQName[i][j].length;
							String[] increasedQN = new String[length+addedLength];
							System.arraycopy(misplacedQName[i][j], 0, increasedQN, 0, length);
							misplacedQName[i][j] = increasedQN;
                            System.arraycopy(qName, 0, misplacedQName[i][j], length, addedLength);
							
							length = misplacedSystemId[i][j].length;
							String[] increasedSI = new String[length+addedLength];
							System.arraycopy(misplacedSystemId[i][j], 0, increasedSI, 0, length);
							misplacedSystemId[i][j] = increasedSI;
                            System.arraycopy(systemId, 0, misplacedSystemId[i][j], length, addedLength);
							
							length = misplacedLineNumber[i][j].length;
							int[] increasedLN = new int[length+addedLength];
							System.arraycopy(misplacedLineNumber[i][j], 0, increasedLN, 0, length);
							misplacedLineNumber[i][j] = increasedLN;
                            System.arraycopy(lineNumber, 0, misplacedLineNumber[i][j], length, addedLength);
														
							length = misplacedColumnNumber[i][j].length;
							int[] increasedCN = new int[length+addedLength];
							System.arraycopy(misplacedColumnNumber[i][j], 0, increasedCN, 0, length);
							misplacedColumnNumber[i][j] = increasedCN;
                            System.arraycopy(columnNumber, 0, misplacedColumnNumber[i][j], length, addedLength);
							
							break all;
						}
					}                   
					int length = misplacedDefinition[i].length;					
					APattern[] increasedDef = new APattern[(length+1)];					
					System.arraycopy(misplacedDefinition[i], 0, increasedDef, 0, length);
					misplacedDefinition[i] = increasedDef;
					misplacedDefinition[i][length] = definition; 
										
					
					String[][] increasedQN = new String[(length+1)][];
					System.arraycopy(misplacedQName[i], 0, increasedQN, 0, length);
					misplacedQName[i] = increasedQN;
					misplacedQName[i][length] = qName;
										
					String[][] increasedSI = new String[(length+1)][];
					System.arraycopy(misplacedSystemId[i], 0, increasedSI, 0, length);
					misplacedSystemId[i] = increasedSI;
					misplacedSystemId[i][length] = systemId;
					
					int[][] increasedLN = new int[(length+1)][];
					System.arraycopy(misplacedLineNumber[i], 0, increasedLN, 0, length);
					misplacedLineNumber[i] = increasedLN;
					misplacedLineNumber[i][length] = lineNumber;
										
					int[][] increasedCN = new int[(length+1)][];
					System.arraycopy(misplacedColumnNumber[i], 0, increasedCN, 0, length);
					misplacedColumnNumber[i] = increasedCN;
					misplacedColumnNumber[i][length] = columnNumber;
					
					break all;
				}
			}
            
            errorTotalCount++;
			APattern[] increasedCDef = new APattern[++misplacedSize];
                                                                  // ISSUE 194 added ++
			System.arraycopy(misplacedContext, 0, increasedCDef, 0, ++misplacedIndex);
			misplacedContext = increasedCDef;
            // ISSUE 194 removed ++
			misplacedContext[misplacedIndex] = contextDefinition;
			
			String[] increasedSSI = new String[misplacedSize];
			System.arraycopy(misplacedStartSystemId, 0, increasedSSI, 0, misplacedIndex);
			misplacedStartSystemId = increasedSSI;
			misplacedStartSystemId[misplacedIndex] = startSystemId;
			
			int[] increasedSLN = new int[misplacedSize];
			System.arraycopy(misplacedStartLineNumber, 0, increasedSLN, 0, misplacedIndex);
			misplacedStartLineNumber = increasedSLN;
			misplacedStartLineNumber[misplacedIndex] = startLineNumber;
			
			int[] increasedSCN = new int[misplacedSize];
			System.arraycopy(misplacedStartColumnNumber, 0, increasedSCN, 0, misplacedIndex);
			misplacedStartColumnNumber = increasedSCN;
			misplacedStartColumnNumber[misplacedIndex] = startColumnNumber;
			
			APattern[][] increasedDef = new APattern[misplacedSize][];
			System.arraycopy(misplacedDefinition, 0, increasedDef, 0, misplacedIndex);
			misplacedDefinition = increasedDef;
			misplacedDefinition[misplacedIndex] = new APattern[1];
			misplacedDefinition[misplacedIndex][0] = definition; 
			
			String[][][] increasedQN = new String[misplacedSize][][];
			System.arraycopy(misplacedQName, 0, increasedQN, 0, misplacedIndex);
			misplacedQName = increasedQN;
			misplacedQName[misplacedIndex] = new String[1][];			
			misplacedQName[misplacedIndex][0] = qName; 			
			
			String[][][] increasedSI = new String[misplacedSize][][];
			System.arraycopy(misplacedSystemId, 0, increasedSI, 0, misplacedIndex);
			misplacedSystemId = increasedSI;
			misplacedSystemId[misplacedIndex] = new String[1][];
			misplacedSystemId[misplacedIndex][0] = systemId;
						
			int[][][] increasedLN = new int[misplacedSize][][];
			System.arraycopy(misplacedLineNumber, 0, increasedLN, 0, misplacedIndex);
			misplacedLineNumber = increasedLN;
			misplacedLineNumber[misplacedIndex] = new int[1][];
			misplacedLineNumber[misplacedIndex][0] = lineNumber;
			
			int[][][] increasedCN = new int[misplacedSize][][];
			System.arraycopy(misplacedColumnNumber, 0, increasedCN, 0, misplacedIndex);
			misplacedColumnNumber = increasedCN;
			misplacedColumnNumber[misplacedIndex] = new int[1][];
			misplacedColumnNumber[misplacedIndex][0] = columnNumber;
            
            int[] increasedFEC = new int[misplacedSize];
			System.arraycopy(misplacedFEC, 0, increasedFEC, 0, misplacedIndex);
			misplacedFEC = increasedFEC;
			misplacedFEC[misplacedIndex] = functionalEquivalenceCode;
		}
	}
    public void clearMisplacedElement(){
        errorTotalCount -= misplacedSize;
        misplacedSize = 0;
        misplacedIndex = -1;	
        misplacedContext = null;	
        misplacedStartSystemId = null;			
        misplacedStartLineNumber = null;
        misplacedStartColumnNumber = null;
        misplacedDefinition = null;
        misplacedQName = null;
        misplacedSystemId = null;			
        misplacedLineNumber = null;
        misplacedColumnNumber = null;
        
        misplacedFEC = null;
    }
    public void clearMisplacedElement(int messageId){        
        int removeIndex = getRemoveIndex(messageId, misplacedFEC);
        int moved = misplacedIndex - removeIndex;
        misplacedIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(misplacedContext, removeIndex+1, misplacedContext, removeIndex, moved);
            System.arraycopy(misplacedStartSystemId, removeIndex+1, misplacedStartSystemId, removeIndex, moved);
            System.arraycopy(misplacedStartLineNumber, removeIndex+1, misplacedStartLineNumber, removeIndex, moved);
            System.arraycopy(misplacedStartColumnNumber, removeIndex+1, misplacedStartColumnNumber, removeIndex, moved);
            System.arraycopy(misplacedDefinition, removeIndex+1, misplacedDefinition, removeIndex, moved);
            System.arraycopy(misplacedQName, removeIndex+1, misplacedQName, removeIndex, moved);
            System.arraycopy(misplacedSystemId, removeIndex+1, misplacedSystemId, removeIndex, moved);
            System.arraycopy(misplacedLineNumber, removeIndex+1, misplacedLineNumber, removeIndex, moved);
            System.arraycopy(misplacedColumnNumber, removeIndex+1, misplacedColumnNumber, removeIndex, moved);
            System.arraycopy(misplacedFEC, removeIndex+1, misplacedFEC, removeIndex, moved);
        }
    }	
			
	
	public void excessiveContent(int functionalEquivalenceCode, 
                                    Rule context,
									String startSystemId,
									int startLineNumber,
									int startColumnNumber,
									APattern definition, 
									String[] qName, 
									String[] systemId, 
									int[] lineNumber, 
									int[] columnNumber){
		if(excessiveSize == 0){            
			excessiveSize = 1;
			excessiveIndex = 0;
			excessiveContext = new APattern[excessiveSize];
			excessiveStartSystemId = new String[excessiveSize];			
			excessiveStartLineNumber = new int[excessiveSize];
			excessiveStartColumnNumber = new int[excessiveSize];
			excessiveDefinition = new APattern[excessiveSize];
			excessiveQName = new String[excessiveSize][];			
			excessiveSystemId = new String[excessiveSize][];			
			excessiveLineNumber = new int[excessiveSize][];
			excessiveColumnNumber = new int[excessiveSize][];
            excessiveFEC = new int[excessiveSize];			
		}else if(++excessiveIndex == excessiveSize){
			APattern[] increasedEC = new APattern[++excessiveSize];
			System.arraycopy(excessiveContext, 0, increasedEC, 0, excessiveIndex);
			excessiveContext = increasedEC;
			
			String[] increasedSSI = new String[excessiveSize];
			System.arraycopy(excessiveStartSystemId, 0, increasedSSI, 0, excessiveIndex);
			excessiveStartSystemId = increasedSSI;
						
			int[] increasedSLN = new int[excessiveSize];
			System.arraycopy(excessiveStartLineNumber, 0, increasedSLN, 0, excessiveIndex);
			excessiveStartLineNumber = increasedSLN;
			
			int[] increasedSCN = new int[excessiveSize];
			System.arraycopy(excessiveStartColumnNumber, 0, increasedSCN, 0, excessiveIndex);
			excessiveStartColumnNumber = increasedSCN;
			
			APattern[] increasedED = new APattern[excessiveSize];
			System.arraycopy(excessiveDefinition, 0, increasedED, 0, excessiveIndex);
			excessiveDefinition = increasedED;
			
			String[][] increasedQN = new String[excessiveSize][];
			System.arraycopy(excessiveQName, 0, increasedQN, 0, excessiveIndex);
			excessiveQName = increasedQN;
			
			String[][] increasedSI = new String[excessiveSize][];
			System.arraycopy(excessiveSystemId, 0, increasedSI, 0, excessiveIndex);
			excessiveSystemId = increasedSI;
						
			int[][] increasedLN = new int[excessiveSize][];
			System.arraycopy(excessiveLineNumber, 0, increasedLN, 0, excessiveIndex);
			excessiveLineNumber = increasedLN;
			
			int[][] increasedCN = new int[excessiveSize][];
			System.arraycopy(excessiveColumnNumber, 0, increasedCN, 0, excessiveIndex);
			excessiveColumnNumber = increasedCN;
            
            int[] increasedFEC = new int[excessiveSize];
			System.arraycopy(excessiveFEC, 0, increasedFEC, 0, excessiveIndex);
			excessiveFEC = increasedFEC;
		}
        errorTotalCount++;
		excessiveContext[excessiveIndex] = context;
		excessiveStartSystemId[excessiveIndex] = startSystemId;
		excessiveStartLineNumber[excessiveIndex] = startLineNumber;
		excessiveStartColumnNumber[excessiveIndex] = startColumnNumber;
		excessiveDefinition[excessiveIndex] = definition;
		excessiveQName[excessiveIndex] = qName;
		excessiveSystemId[excessiveIndex] = systemId;
		excessiveLineNumber[excessiveIndex] = lineNumber;
		excessiveColumnNumber[excessiveIndex] = columnNumber;
        excessiveFEC[excessiveIndex] = functionalEquivalenceCode;		
		
	}   
	public void excessiveContent(int functionalEquivalenceCode, 
                                Rule context, 
								APattern definition, 
								String qName, 
								String systemId, 
								int lineNumber,		
								int columnNumber){
        // TODO review the functionalEquivalenceCode handling
        boolean recorded = false;
		for(int i = 0; i < excessiveSize; i++){
			if(excessiveContext[i].equals(context) &&
				excessiveDefinition[i].equals(definition)){
			    
                recorded =  true;
                
				int length = excessiveQName[i].length;
				String[] increasedQN = new String[(length+1)];
				System.arraycopy(excessiveQName[i], 0, increasedQN, 0, length);
				excessiveQName[i] = increasedQN;
				excessiveQName[i][length] = qName;
				
				length = excessiveSystemId[i].length;
				String[] increasedSI = new String[(length+1)];
				System.arraycopy(excessiveSystemId[i], 0, increasedSI, 0, length);
				excessiveSystemId[i] = increasedSI;
				excessiveSystemId[i][length] = systemId;
				
				length = excessiveLineNumber[i].length;
				int[] increasedLN = new int[(length+1)];
				System.arraycopy(excessiveLineNumber[i], 0, increasedLN, 0, length);
				excessiveLineNumber[i] = increasedLN;
				excessiveLineNumber[i][length] = lineNumber;
				
				length = excessiveColumnNumber[i].length;
				int[] increasedCN = new int[(length+1)];
				System.arraycopy(excessiveColumnNumber[i], 0, increasedCN, 0, length);
				excessiveColumnNumber[i] = increasedCN;
				excessiveColumnNumber[i][length] = columnNumber;				
               
				break;
			}
		}		
        if(!recorded) throw new IllegalArgumentException();
	}
	public void clearExcessiveContent(){
        errorTotalCount -= excessiveSize;
        excessiveSize = 0;
        excessiveIndex = -1;
        excessiveContext = null;
        excessiveStartSystemId = null;			
        excessiveStartLineNumber = null;
        excessiveStartColumnNumber = null;
        excessiveDefinition = null;
        excessiveQName = null;			
        excessiveSystemId = null;			
        excessiveLineNumber = null;
        excessiveColumnNumber = null;
        
        excessiveFEC = null;
    }
    public void clearExcessiveContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, excessiveFEC);
        int moved = excessiveIndex - removeIndex;
        excessiveIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(excessiveContext, removeIndex+1, excessiveContext, removeIndex, moved);
            System.arraycopy(excessiveStartSystemId, removeIndex+1, excessiveStartSystemId, removeIndex, moved);
            System.arraycopy(excessiveStartLineNumber, removeIndex+1, excessiveStartLineNumber, removeIndex, moved);
            System.arraycopy(excessiveStartColumnNumber, removeIndex+1, excessiveStartColumnNumber, removeIndex, moved);
            System.arraycopy(excessiveDefinition, removeIndex+1, excessiveDefinition, removeIndex, moved);
            System.arraycopy(excessiveQName, removeIndex+1, excessiveQName, removeIndex, moved);
            System.arraycopy(excessiveSystemId, removeIndex+1, excessiveSystemId, removeIndex, moved);
            System.arraycopy(excessiveLineNumber, removeIndex+1, excessiveLineNumber, removeIndex, moved);
            System.arraycopy(excessiveColumnNumber, removeIndex+1, excessiveColumnNumber, removeIndex, moved);
            System.arraycopy(excessiveFEC, removeIndex+1, excessiveFEC, removeIndex, moved);
        }
    }    
    
    
	public void ambiguousElementContentError(int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousElementSizeEE == 0){
			ambiguousElementSizeEE = 1;
			ambiguousElementIndexEE = 0;	
			ambiguousElementQNameEE = new String[ambiguousElementSizeEE];			
			ambiguousElementSystemIdEE = new String[ambiguousElementSizeEE];			
			ambiguousElementLineNumberEE = new int[ambiguousElementSizeEE];
			ambiguousElementColumnNumberEE = new int[ambiguousElementSizeEE];
			ambiguousElementDefinitionEE = new AElement[ambiguousElementSizeEE][];
            ambiguousElementFECEE = new int[ambiguousElementSizeEE];
		}else if(++ambiguousElementIndexEE == ambiguousElementSizeEE){			
			String[] increasedQN = new String[++ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementQNameEE, 0, increasedQN, 0, ambiguousElementIndexEE);
			ambiguousElementQNameEE = increasedQN;
			
			AElement[][] increasedDef = new AElement[ambiguousElementSizeEE][];
			System.arraycopy(ambiguousElementDefinitionEE, 0, increasedDef, 0, ambiguousElementIndexEE);
			ambiguousElementDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementSystemIdEE, 0, increasedSI, 0, ambiguousElementIndexEE);
			ambiguousElementSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementLineNumberEE, 0, increasedLN, 0, ambiguousElementIndexEE);
			ambiguousElementLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementColumnNumberEE, 0, increasedCN, 0, ambiguousElementIndexEE);
			ambiguousElementColumnNumberEE = increasedCN;
            
            int[] increasedFEC = new int[ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementFECEE, 0, increasedFEC, 0, ambiguousElementIndexEE);
			ambiguousElementFECEE = increasedFEC;
		}
		ambiguousElementQNameEE[ambiguousElementIndexEE] = qName;		
		ambiguousElementSystemIdEE[ambiguousElementIndexEE] = systemId;
		ambiguousElementLineNumberEE[ambiguousElementIndexEE] = lineNumber;
		ambiguousElementColumnNumberEE[ambiguousElementIndexEE] = columnNumber;
		ambiguousElementDefinitionEE[ambiguousElementIndexEE] = possibleDefinitions;
        ambiguousElementFECEE[ambiguousElementIndexEE] = functionalEquivalenceCode;
		
	}
	public void clearAmbiguousElementContentError(){
        errorTotalCount -= ambiguousElementSizeEE;
        ambiguousElementSizeEE = 0;
        ambiguousElementIndexEE = -1;	
        ambiguousElementQNameEE = null;			
        ambiguousElementSystemIdEE = null;			
        ambiguousElementLineNumberEE = null;
        ambiguousElementColumnNumberEE = null;
        ambiguousElementDefinitionEE = null;
        
        ambiguousElementFECEE = null;
    }
    public void clearAmbiguousElementContentError(int messageId){        
        int removeIndex = getRemoveIndex(messageId, ambiguousElementFECEE);
        int moved = ambiguousElementIndexEE - removeIndex;
        ambiguousElementIndexEE--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousElementQNameEE, removeIndex+1, ambiguousElementQNameEE, removeIndex, moved);
            System.arraycopy(ambiguousElementSystemIdEE, removeIndex+1, ambiguousElementSystemIdEE, removeIndex, moved);
            System.arraycopy(ambiguousElementLineNumberEE, removeIndex+1, ambiguousElementLineNumberEE, removeIndex, moved);
            System.arraycopy(ambiguousElementColumnNumberEE, removeIndex+1, ambiguousElementColumnNumberEE, removeIndex, moved);
            System.arraycopy(ambiguousElementDefinitionEE, removeIndex+1, ambiguousElementDefinitionEE, removeIndex, moved);
            System.arraycopy(ambiguousElementFECEE, removeIndex+1, ambiguousElementFECEE, removeIndex, moved);
        }
    }
    
    
    
	public void ambiguousAttributeContentError(int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousAttributeSizeEE == 0){
			ambiguousAttributeSizeEE = 1;
			ambiguousAttributeIndexEE = 0;	
			ambiguousAttributeQNameEE = new String[ambiguousAttributeSizeEE];			
			ambiguousAttributeSystemIdEE = new String[ambiguousAttributeSizeEE];			
			ambiguousAttributeLineNumberEE = new int[ambiguousAttributeSizeEE];
			ambiguousAttributeColumnNumberEE = new int[ambiguousAttributeSizeEE];
			ambiguousAttributeDefinitionEE = new AAttribute[ambiguousAttributeSizeEE][];
            ambiguousAttributeFECEE = new int[ambiguousAttributeSizeEE];
		}else if(++ambiguousAttributeIndexEE == ambiguousAttributeSizeEE){			
			String[] increasedQN = new String[++ambiguousAttributeSizeEE];
			System.arraycopy(ambiguousAttributeQNameEE, 0, increasedQN, 0, ambiguousAttributeIndexEE);
			ambiguousAttributeQNameEE = increasedQN;
			
			AAttribute[][] increasedDef = new AAttribute[ambiguousAttributeSizeEE][];
			System.arraycopy(ambiguousAttributeDefinitionEE, 0, increasedDef, 0, ambiguousAttributeIndexEE);
			ambiguousAttributeDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[ambiguousAttributeSizeEE];
			System.arraycopy(ambiguousAttributeSystemIdEE, 0, increasedSI, 0, ambiguousAttributeIndexEE);
			ambiguousAttributeSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[ambiguousAttributeSizeEE];
			System.arraycopy(ambiguousAttributeLineNumberEE, 0, increasedLN, 0, ambiguousAttributeIndexEE);
			ambiguousAttributeLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[ambiguousAttributeSizeEE];
			System.arraycopy(ambiguousAttributeColumnNumberEE, 0, increasedCN, 0, ambiguousAttributeIndexEE);
			ambiguousAttributeColumnNumberEE = increasedCN;
            
            int[] increasedFEC = new int[ambiguousAttributeSizeEE];
			System.arraycopy(ambiguousAttributeFECEE, 0, increasedFEC, 0, ambiguousAttributeIndexEE);
			ambiguousAttributeFECEE = increasedFEC;
		}
		ambiguousAttributeQNameEE[ambiguousAttributeIndexEE] = qName;		
		ambiguousAttributeSystemIdEE[ambiguousAttributeIndexEE] = systemId;
		ambiguousAttributeLineNumberEE[ambiguousAttributeIndexEE] = lineNumber;
		ambiguousAttributeColumnNumberEE[ambiguousAttributeIndexEE] = columnNumber;
		ambiguousAttributeDefinitionEE[ambiguousAttributeIndexEE] = possibleDefinitions;	
		ambiguousAttributeFECEE[ambiguousAttributeIndexEE] = functionalEquivalenceCode;
		
	}
    public void clearAmbiguousAttributeContentError(){
        errorTotalCount -= ambiguousAttributeSizeEE;
        ambiguousAttributeSizeEE = 0;
        ambiguousAttributeIndexEE = -1;	
        ambiguousAttributeQNameEE = null;			
        ambiguousAttributeSystemIdEE = null;			
        ambiguousAttributeLineNumberEE = null;
        ambiguousAttributeColumnNumberEE = null;
        ambiguousAttributeDefinitionEE = null;
        
        ambiguousAttributeFECEE = null;
    }
	public void clearAmbiguousAttributeContentError(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousAttributeFECEE);
        int moved = ambiguousAttributeIndexEE - removeIndex;
        ambiguousAttributeIndexEE--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAttributeQNameEE, removeIndex+1, ambiguousAttributeQNameEE, removeIndex, moved);
            System.arraycopy(ambiguousAttributeSystemIdEE, removeIndex+1, ambiguousAttributeSystemIdEE, removeIndex, moved);
            System.arraycopy(ambiguousAttributeLineNumberEE, removeIndex+1, ambiguousAttributeLineNumberEE, removeIndex, moved);
            System.arraycopy(ambiguousAttributeColumnNumberEE, removeIndex+1, ambiguousAttributeColumnNumberEE, removeIndex, moved);
            System.arraycopy(ambiguousAttributeDefinitionEE, removeIndex+1, ambiguousAttributeDefinitionEE, removeIndex, moved);    
            System.arraycopy(ambiguousAttributeFECEE, removeIndex+1, ambiguousAttributeFECEE, removeIndex, moved);
        }
    }
	
    
    
	public void ambiguousCharsContentError(int functionalEquivalenceCode, 
                                    String systemId, 
									int lineNumber, 
									int columnNumber, 
									CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousCharsSizeEE == 0){
			ambiguousCharsSizeEE = 1;
			ambiguousCharsIndexEE = 0;
			ambiguousCharsSystemIdEE = new String[ambiguousCharsSizeEE];			
			ambiguousCharsLineNumberEE = new int[ambiguousCharsSizeEE];
			ambiguousCharsColumnNumberEE = new int[ambiguousCharsSizeEE];
			ambiguousCharsDefinitionEE = new CharsActiveTypeItem[ambiguousCharsSizeEE][];
            
            ambiguousCharsFECEE = new int[ambiguousCharsSizeEE];
		}else if(++ambiguousCharsIndexEE == ambiguousCharsSizeEE){	
                                                                        // ISSUE 283 
			CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[++ambiguousCharsSizeEE][];
			System.arraycopy(ambiguousCharsDefinitionEE, 0, increasedDef, 0, ambiguousCharsIndexEE);
			ambiguousCharsDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[ambiguousCharsSizeEE];
			System.arraycopy(ambiguousCharsSystemIdEE, 0, increasedSI, 0, ambiguousCharsIndexEE);
			ambiguousCharsSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[ambiguousCharsSizeEE];
			System.arraycopy(ambiguousCharsLineNumberEE, 0, increasedLN, 0, ambiguousCharsIndexEE);
			ambiguousCharsLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[ambiguousCharsSizeEE];
			System.arraycopy(ambiguousCharsColumnNumberEE, 0, increasedCN, 0, ambiguousCharsIndexEE);
			ambiguousCharsColumnNumberEE = increasedCN;
            
            int[] increasedFEC = new int[ambiguousCharsSizeEE];
			System.arraycopy(ambiguousCharsFECEE, 0, increasedFEC, 0, ambiguousCharsIndexEE);
			ambiguousCharsFECEE = increasedFEC;
		}		
        // ISSUE 283        
        ambiguousCharsDefinitionEE[ambiguousCharsIndexEE] = possibleDefinitions;
		ambiguousCharsSystemIdEE[ambiguousCharsIndexEE] = systemId;
		ambiguousCharsLineNumberEE[ambiguousCharsIndexEE] = lineNumber;
		ambiguousCharsColumnNumberEE[ambiguousCharsIndexEE] = columnNumber;
		ambiguousCharsDefinitionEE[ambiguousCharsIndexEE] = possibleDefinitions;	
		
        ambiguousCharsFECEE[ambiguousCharsIndexEE] = columnNumber;
		
	}
    public void clearAmbiguousCharsContentError(){
        errorTotalCount -= ambiguousCharsSizeEE;
        ambiguousCharsSizeEE = 0;
        ambiguousCharsIndexEE = -1;
        ambiguousCharsSystemIdEE = null;			
        ambiguousCharsLineNumberEE = null;
        ambiguousCharsColumnNumberEE = null;
        ambiguousCharsDefinitionEE = null;
        
        ambiguousCharsFECEE = null;
    }
    public void clearAmbiguousCharsContentError(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousCharsFECEE);
        int moved = ambiguousCharsIndexEE - removeIndex;
        ambiguousCharsIndexEE--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousCharsSystemIdEE, removeIndex+1, ambiguousCharsSystemIdEE, removeIndex, moved);
            System.arraycopy(ambiguousCharsLineNumberEE, removeIndex+1, ambiguousCharsLineNumberEE, removeIndex, moved);
            System.arraycopy(ambiguousCharsColumnNumberEE, removeIndex+1, ambiguousCharsColumnNumberEE, removeIndex, moved);
            System.arraycopy(ambiguousCharsDefinitionEE, removeIndex+1, ambiguousCharsDefinitionEE, removeIndex, moved);
            System.arraycopy(ambiguousCharsFECEE, removeIndex+1, ambiguousCharsFECEE, removeIndex, moved);
        }
    }	
	
	
	public void ambiguousElementContentWarning(int functionalEquivalenceCode,
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousElementSizeWW == 0){
			ambiguousElementSizeWW = 1;
			ambiguousElementIndexWW = 0;	
			ambiguousElementQNameWW = new String[ambiguousElementSizeWW];			
			ambiguousElementSystemIdWW = new String[ambiguousElementSizeWW];			
			ambiguousElementLineNumberWW = new int[ambiguousElementSizeWW];
			ambiguousElementColumnNumberWW = new int[ambiguousElementSizeWW];
			ambiguousElementDefinitionWW = new AElement[ambiguousElementSizeWW][];
            
            ambiguousElementFECWW = new int[ambiguousElementSizeWW];
		}else if(++ambiguousElementIndexWW == ambiguousElementSizeWW){			
			String[] increasedQN = new String[++ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementQNameWW, 0, increasedQN, 0, ambiguousElementIndexWW);
			ambiguousElementQNameWW = increasedQN;
			
			AElement[][] increasedDef = new AElement[ambiguousElementSizeWW][];
			System.arraycopy(ambiguousElementDefinitionWW, 0, increasedDef, 0, ambiguousElementIndexWW);
			ambiguousElementDefinitionWW = increasedDef;
			
			String[] increasedSI = new String[ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementSystemIdWW, 0, increasedSI, 0, ambiguousElementIndexWW);
			ambiguousElementSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementLineNumberWW, 0, increasedLN, 0, ambiguousElementIndexWW);
			ambiguousElementLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementColumnNumberWW, 0, increasedCN, 0, ambiguousElementIndexWW);
			ambiguousElementColumnNumberWW = increasedCN;
            
            int[] increasedFEC = new int[ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementFECWW, 0, increasedFEC, 0, ambiguousElementIndexWW);
			ambiguousElementFECWW = increasedFEC;
		}
		ambiguousElementQNameWW[ambiguousElementIndexWW] = qName;		
		ambiguousElementSystemIdWW[ambiguousElementIndexWW] = systemId;
		ambiguousElementLineNumberWW[ambiguousElementIndexWW] = lineNumber;
		ambiguousElementColumnNumberWW[ambiguousElementIndexWW] = columnNumber;
		ambiguousElementDefinitionWW[ambiguousElementIndexWW] = possibleDefinitions;
        
        ambiguousElementFECWW[ambiguousElementIndexWW] = functionalEquivalenceCode;
	}
	public void clearAmbiguousElementContentWarning(){
        errorTotalCount -= ambiguousElementSizeWW;
        ambiguousElementSizeWW = 0;
        ambiguousElementIndexWW = -1;	
        ambiguousElementQNameWW = null;			
        ambiguousElementSystemIdWW = null;			
        ambiguousElementLineNumberWW = null;
        ambiguousElementColumnNumberWW = null;
        ambiguousElementDefinitionWW = null;
        
        ambiguousElementFECWW = null;
    }
    public void clearAmbiguousElementContentWarning(int messageId){        
        int removeIndex = getRemoveIndex(messageId, ambiguousElementFECWW);
        int moved = ambiguousElementIndexWW - removeIndex;
        ambiguousElementIndexWW--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousElementQNameWW, removeIndex+1, ambiguousElementQNameWW, removeIndex, moved);
            System.arraycopy(ambiguousElementSystemIdWW, removeIndex+1, ambiguousElementSystemIdWW, removeIndex, moved);
            System.arraycopy(ambiguousElementLineNumberWW, removeIndex+1, ambiguousElementLineNumberWW, removeIndex, moved);
            System.arraycopy(ambiguousElementColumnNumberWW, removeIndex+1, ambiguousElementColumnNumberWW, removeIndex, moved);
            System.arraycopy(ambiguousElementDefinitionWW, removeIndex+1, ambiguousElementDefinitionWW, removeIndex, moved);
            System.arraycopy(ambiguousElementFECWW, removeIndex+1, ambiguousElementFECWW, removeIndex, moved);
        }
    }
    
    
	public void ambiguousAttributeContentWarning(int functionalEquivalenceCode,
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousAttributeSizeWW == 0){
			ambiguousAttributeSizeWW = 1;
			ambiguousAttributeIndexWW = 0;	
			ambiguousAttributeQNameWW = new String[ambiguousAttributeSizeWW];			
			ambiguousAttributeSystemIdWW = new String[ambiguousAttributeSizeWW];			
			ambiguousAttributeLineNumberWW = new int[ambiguousAttributeSizeWW];
			ambiguousAttributeColumnNumberWW = new int[ambiguousAttributeSizeWW];
			ambiguousAttributeDefinitionWW = new AAttribute[ambiguousAttributeSizeWW][];
            
            ambiguousAttributeFECWW = new int[ambiguousAttributeSizeWW];
		}else if(++ambiguousAttributeIndexWW == ambiguousAttributeSizeWW){			
			String[] increasedQN = new String[++ambiguousAttributeSizeWW];
			System.arraycopy(ambiguousAttributeQNameWW, 0, increasedQN, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeQNameWW = increasedQN;
			
			AAttribute[][] increasedDef = new AAttribute[ambiguousAttributeSizeWW][];
			System.arraycopy(ambiguousAttributeDefinitionWW, 0, increasedDef, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeDefinitionWW = increasedDef;
			
			String[] increasedSI = new String[ambiguousAttributeSizeWW];
			System.arraycopy(ambiguousAttributeSystemIdWW, 0, increasedSI, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousAttributeSizeWW];
			System.arraycopy(ambiguousAttributeLineNumberWW, 0, increasedLN, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousAttributeSizeWW];
			System.arraycopy(ambiguousAttributeColumnNumberWW, 0, increasedCN, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeColumnNumberWW = increasedCN;
            
            int[] increasedFEC = new int[ambiguousAttributeSizeWW];
			System.arraycopy(ambiguousAttributeFECWW, 0, increasedFEC, 0, ambiguousAttributeIndexWW);
			ambiguousAttributeFECWW = increasedFEC;
		}
		ambiguousAttributeQNameWW[ambiguousAttributeIndexWW] = qName;		
		ambiguousAttributeSystemIdWW[ambiguousAttributeIndexWW] = systemId;
		ambiguousAttributeLineNumberWW[ambiguousAttributeIndexWW] = lineNumber;
		ambiguousAttributeColumnNumberWW[ambiguousAttributeIndexWW] = columnNumber;
		ambiguousAttributeDefinitionWW[ambiguousAttributeIndexWW] = possibleDefinitions;
        
        ambiguousAttributeFECWW[ambiguousAttributeIndexWW] = functionalEquivalenceCode;
		
	}
	public void clearAmbiguousAttributeContentWarning(){
        errorTotalCount -= ambiguousAttributeSizeWW;
        ambiguousAttributeSizeWW = 0;
        ambiguousAttributeIndexWW = -1;	
        ambiguousAttributeQNameWW = null;			
        ambiguousAttributeSystemIdWW = null;			
        ambiguousAttributeLineNumberWW = null;
        ambiguousAttributeColumnNumberWW = null;
        ambiguousAttributeDefinitionWW = null;
    }
	public void clearAmbiguousAttributeContentWarning(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousAttributeFECWW);
        int moved = ambiguousAttributeIndexWW - removeIndex;
        ambiguousAttributeIndexWW--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAttributeQNameWW, removeIndex+1, ambiguousAttributeQNameWW, removeIndex, moved);
            System.arraycopy(ambiguousAttributeSystemIdWW, removeIndex+1, ambiguousAttributeSystemIdWW, removeIndex, moved);
            System.arraycopy(ambiguousAttributeLineNumberWW, removeIndex+1, ambiguousAttributeLineNumberWW, removeIndex, moved);
            System.arraycopy(ambiguousAttributeColumnNumberWW, removeIndex+1, ambiguousAttributeColumnNumberWW, removeIndex, moved);
            System.arraycopy(ambiguousAttributeDefinitionWW, removeIndex+1, ambiguousAttributeDefinitionWW, removeIndex, moved);    
            System.arraycopy(ambiguousAttributeFECWW, removeIndex+1, ambiguousAttributeFECWW, removeIndex, moved);
        }
    }
    
	public void ambiguousCharsContentWarning(int functionalEquivalenceCode,
                                    String systemId, 
									int lineNumber, 
									int columnNumber, 
									CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousCharsSizeWW == 0){
			ambiguousCharsSizeWW = 1;
			ambiguousCharsIndexWW = 0;
			ambiguousCharsSystemIdWW = new String[ambiguousCharsSizeWW];			
			ambiguousCharsLineNumberWW = new int[ambiguousCharsSizeWW];
			ambiguousCharsColumnNumberWW = new int[ambiguousCharsSizeWW];
			ambiguousCharsDefinitionWW = new CharsActiveTypeItem[ambiguousCharsSizeWW][];
            
            ambiguousCharsFECWW = new int[ambiguousCharsSizeWW];
		}else if(++ambiguousCharsIndexWW == ambiguousCharsSizeWW){			
			CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[++ambiguousCharsSizeWW][];
			System.arraycopy(ambiguousCharsDefinitionWW, 0, increasedDef, 0, ambiguousCharsIndexWW);
			ambiguousCharsDefinitionWW = increasedDef;
			
			String[] increasedSI = new String[ambiguousCharsSizeWW];
			System.arraycopy(ambiguousCharsSystemIdWW, 0, increasedSI, 0, ambiguousCharsIndexWW);
			ambiguousCharsSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousCharsSizeWW];
			System.arraycopy(ambiguousCharsLineNumberWW, 0, increasedLN, 0, ambiguousCharsIndexWW);
			ambiguousCharsLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousCharsSizeWW];
			System.arraycopy(ambiguousCharsColumnNumberWW, 0, increasedCN, 0, ambiguousCharsIndexWW);
			ambiguousCharsColumnNumberWW = increasedCN;
            
            int[] increasedFEC = new int[ambiguousCharsSizeWW];
			System.arraycopy(ambiguousCharsFECWW, 0, increasedFEC, 0, ambiguousCharsIndexWW);
			ambiguousCharsFECWW = increasedFEC;
		}        
		ambiguousCharsSystemIdWW[ambiguousCharsIndexWW] = systemId;
		ambiguousCharsLineNumberWW[ambiguousCharsIndexWW] = lineNumber;
		ambiguousCharsColumnNumberWW[ambiguousCharsIndexWW] = columnNumber;
		ambiguousCharsDefinitionWW[ambiguousCharsIndexWW] = possibleDefinitions;	
		
        ambiguousCharsFECWW[ambiguousCharsIndexWW] = functionalEquivalenceCode;
        
	}
	public void clearAmbiguousCharsContentWarning(){
        errorTotalCount -= ambiguousCharsSizeWW;
        ambiguousCharsSizeWW = 0;
        ambiguousCharsIndexWW = -1;
        ambiguousCharsSystemIdWW = null;			
        ambiguousCharsLineNumberWW = null;
        ambiguousCharsColumnNumberWW = null;
        ambiguousCharsDefinitionWW = null;
    }
	public void clearAmbiguousCharsContentWarning(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousCharsFECWW);
        int moved = ambiguousCharsIndexWW - removeIndex;
        ambiguousCharsIndexWW--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousCharsSystemIdWW, removeIndex+1, ambiguousCharsSystemIdWW, removeIndex, moved);
            System.arraycopy(ambiguousCharsLineNumberWW, removeIndex+1, ambiguousCharsLineNumberWW, removeIndex, moved);
            System.arraycopy(ambiguousCharsColumnNumberWW, removeIndex+1, ambiguousCharsColumnNumberWW, removeIndex, moved);
            System.arraycopy(ambiguousCharsDefinitionWW, removeIndex+1, ambiguousCharsDefinitionWW, removeIndex, moved);
            System.arraycopy(ambiguousCharsFECWW, removeIndex+1, ambiguousCharsFECWW, removeIndex, moved);
        }
    }
	
	
	
	
	public void missingContent(int functionalEquivalenceCode, 
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
								int[] columnNumber){
        errorTotalCount++;
		if(missingSize == 0){
			missingSize = 1;
			missingIndex = 0;
			missingContext = new APattern[missingSize];
			missingStartSystemId = new String[missingSize];			
			missingStartLineNumber = new int[missingSize];
			missingStartColumnNumber = new int[missingSize];
			missingDefinition = new APattern[missingSize];
			missingExpected = new int[missingSize];
			missingFound = new int[missingSize];
			missingQName = new String[missingSize][];			
			missingSystemId = new String[missingSize][];			
			missingLineNumber = new int[missingSize][];
			missingColumnNumber = new int[missingSize][];
            missingFEC = new int[missingSize];			
		}else if(++missingIndex == missingSize){
			APattern[] increasedEC = new APattern[++missingSize];
			System.arraycopy(missingContext, 0, increasedEC, 0, missingIndex);
			missingContext = increasedEC;
			
			String[] increasedSSI = new String[missingSize];
			System.arraycopy(missingStartSystemId, 0, increasedSSI, 0, missingIndex);
			missingStartSystemId = increasedSSI;
						
			int[] increasedSLN = new int[missingSize];
			System.arraycopy(missingStartLineNumber, 0, increasedSLN, 0, missingIndex);
			missingStartLineNumber = increasedSLN;
			
			int[] increasedSCN = new int[missingSize];
			System.arraycopy(missingStartColumnNumber, 0, increasedSCN, 0, missingIndex);
			missingStartColumnNumber = increasedSCN;
			
			APattern[] increasedED = new APattern[missingSize];
			System.arraycopy(missingDefinition, 0, increasedED, 0, missingIndex);
			missingDefinition = increasedED;
			
			int[] increasedE = new int[missingSize];
			System.arraycopy(missingExpected, 0, increasedE, 0, missingIndex);
			missingExpected = increasedE;
			
			int[] increasedF = new int[missingSize];
			System.arraycopy(missingFound, 0, increasedF, 0, missingIndex);
			missingFound = increasedF;
			
			String[][] increasedQN = new String[missingSize][];
			System.arraycopy(missingQName, 0, increasedQN, 0, missingIndex);
			missingQName = increasedQN;
			
			String[][] increasedSI = new String[missingSize][];
			System.arraycopy(missingSystemId, 0, increasedSI, 0, missingIndex);
			missingSystemId = increasedSI;
						
			int[][] increasedLN = new int[missingSize][];
			System.arraycopy(missingLineNumber, 0, increasedLN, 0, missingIndex);
			missingLineNumber = increasedLN;
			
			int[][] increasedCN = new int[missingSize][];
			System.arraycopy(missingColumnNumber, 0, increasedCN, 0, missingIndex);
			missingColumnNumber = increasedCN;
            
            int[] increasedFEC = new int[missingSize];
			System.arraycopy(missingFEC, 0, increasedFEC, 0, missingIndex);
			missingFEC = increasedFEC;
		}
		missingContext[missingIndex] = context;
		missingStartSystemId[missingIndex] = startSystemId;
		missingStartLineNumber[missingIndex] = startLineNumber;
		missingStartColumnNumber[missingIndex] = startColumnNumber;
		missingDefinition[missingIndex] = definition;
		missingExpected[missingIndex] = expected;
		missingFound[missingIndex] = found;
		if(qName != null)missingQName[missingIndex] = qName;
		if(systemId != null)missingSystemId[missingIndex] = systemId;
		missingLineNumber[missingIndex] = lineNumber;
		missingColumnNumber[missingIndex] = columnNumber;
        
        missingFEC[missingIndex] = functionalEquivalenceCode;
		
    }
	public void clearMissingContent(){
        errorTotalCount -= missingSize;
        missingSize = 0;
        missingIndex = -1;
        missingContext = null;
        missingStartSystemId = null;			
        missingStartLineNumber = null;
        missingStartColumnNumber = null;
        missingDefinition = null;
        missingExpected = null;
        missingFound = null;
        missingQName = null;			
        missingSystemId = null;			
        missingLineNumber = null;
        missingColumnNumber = null;
        
        missingFEC = null;
    }
    public void clearMissingContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, missingFEC);
        int moved = missingIndex - removeIndex;
        missingIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(missingContext, removeIndex+1, missingContext, removeIndex, moved);
            System.arraycopy(missingStartSystemId, removeIndex+1, missingStartSystemId, removeIndex, moved);
            System.arraycopy(missingStartLineNumber, removeIndex+1, missingStartLineNumber, removeIndex, moved);
            System.arraycopy(missingStartColumnNumber, removeIndex+1, missingStartColumnNumber, removeIndex, moved);
            System.arraycopy(missingDefinition, removeIndex+1, missingDefinition, removeIndex, moved);
            System.arraycopy(missingExpected, removeIndex+1, missingExpected, removeIndex, moved);
            System.arraycopy(missingFound, removeIndex+1, missingFound, removeIndex, moved);
            System.arraycopy(missingQName, removeIndex+1, missingQName, removeIndex, moved);
            System.arraycopy(missingSystemId, removeIndex+1, missingSystemId, removeIndex, moved);
            System.arraycopy(missingLineNumber, removeIndex+1, missingLineNumber, removeIndex, moved);
            System.arraycopy(missingColumnNumber, removeIndex+1, missingColumnNumber, removeIndex, moved);
            System.arraycopy(missingFEC, removeIndex+1, missingFEC, removeIndex, moved);
        }
    }
    
    
    
	public void illegalContent(int functionalEquivalenceCode, 
                            Rule context, 
							String startQName, 
							String startSystemId, 
							int startLineNumber, 
							int startColumnNumber){
        errorTotalCount++;
		if(illegalSize == 0){
			illegalSize = 1;
			illegalIndex = 0;
			illegalContext = new APattern[illegalSize];
			illegalQName = new String[illegalSize];
			illegalStartSystemId = new String[illegalSize];			
			illegalStartLineNumber = new int[illegalSize];
			illegalStartColumnNumber = new int[illegalSize];
            illegalFEC = new int[illegalSize];
		}else if(++illegalIndex == illegalSize){
			APattern[] increasedEC = new APattern[++illegalSize];
			System.arraycopy(illegalContext, 0, increasedEC, 0, illegalIndex);
			illegalContext = increasedEC;
			
			String[] increasedQN = new String[illegalSize];
			System.arraycopy(illegalQName, 0, increasedQN, 0, illegalIndex);
			illegalQName = increasedQN;
			
			String[] increasedSSI = new String[illegalSize];
			System.arraycopy(illegalStartSystemId, 0, increasedSSI, 0, illegalIndex);
			illegalStartSystemId = increasedSSI;
						
			int[] increasedSLN = new int[illegalSize];
			System.arraycopy(illegalStartLineNumber, 0, increasedSLN, 0, illegalIndex);
			illegalStartLineNumber = increasedSLN;
			
			int[] increasedSCN = new int[illegalSize];
			System.arraycopy(illegalStartColumnNumber, 0, increasedSCN, 0, illegalIndex);
			illegalStartColumnNumber = increasedSCN;

            int[] increasedFEC = new int[illegalSize];
			System.arraycopy(illegalFEC, 0, increasedFEC, 0, illegalIndex);
			illegalFEC = increasedFEC;			
		}
		illegalContext[illegalIndex] = context;
		illegalQName[illegalIndex] = startQName;
		illegalStartSystemId[illegalIndex] = startSystemId;
		illegalStartLineNumber[illegalIndex] = startLineNumber;
		illegalStartColumnNumber[illegalIndex] = startColumnNumber;
        
        illegalFEC[illegalIndex] = functionalEquivalenceCode;
	}
    public void clearIllegalContent(){
        errorTotalCount -= illegalSize;
        illegalSize = 0;
        illegalIndex = -1;
        illegalContext = null;
        illegalQName = null;
        illegalStartSystemId = null;			
        illegalStartLineNumber = null;
        illegalStartColumnNumber = null;
        
        illegalFEC = null;
    }
	public void clearIllegalContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, illegalFEC);
        int moved = illegalIndex - removeIndex;
        illegalIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(illegalContext, removeIndex+1, illegalContext, removeIndex, moved);
            System.arraycopy(illegalQName, removeIndex+1, illegalQName, removeIndex, moved);
            System.arraycopy(illegalStartSystemId, removeIndex+1, illegalStartSystemId, removeIndex, moved);
            System.arraycopy(illegalStartLineNumber, removeIndex+1, illegalStartLineNumber, removeIndex, moved);
            System.arraycopy(illegalStartColumnNumber, removeIndex+1, illegalStartColumnNumber, removeIndex, moved);
            System.arraycopy(illegalFEC, removeIndex+1, illegalFEC, removeIndex, moved);
        }
    }
    
    
	public void undeterminedByContent(int functionalEquivalenceCode, String qName, String candidateMessages){
        errorTotalCount++;
		undeterminedQName = qName;
		undeterminedCandidateMessages = candidateMessages; 
		
	}
    public void clearUndeterminedByContent(){
        errorTotalCount--;
        undeterminedQName = null;
		undeterminedCandidateMessages = null;
    }
	
    // {15}
	public void characterContentDatatypeError(int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        errorTotalCount++;
		if(datatypeSizeCC == 0){
			datatypeSizeCC = 1;
			datatypeIndexCC = 0;
			datatypeElementQNameCC = new String[datatypeSizeCC];
			datatypeCharsSystemIdCC = new String[datatypeSizeCC];
			datatypeCharsLineNumberCC = new int[datatypeSizeCC];
			datatypeCharsColumnNumberCC = new int[datatypeSizeCC];
			datatypeCharsDefinitionCC = new DatatypedActiveTypeItem[datatypeSizeCC];
			datatypeErrorMessageCC = new String[datatypeSizeCC];
            
            datatypeFECCC = new int[datatypeSizeCC];
		}else if(++datatypeIndexCC == datatypeSizeCC){
			String[] increasedEQ = new String[++datatypeSizeCC];
			System.arraycopy(datatypeElementQNameCC, 0, increasedEQ, 0, datatypeIndexCC);
			datatypeElementQNameCC = increasedEQ;
						
			String[] increasedCSI = new String[datatypeSizeCC];
			System.arraycopy(datatypeCharsSystemIdCC, 0, increasedCSI, 0, datatypeIndexCC);
			datatypeCharsSystemIdCC = increasedCSI;
			
			int[] increasedCLN = new int[datatypeSizeCC];
			System.arraycopy(datatypeCharsLineNumberCC, 0, increasedCLN, 0, datatypeIndexCC);
			datatypeCharsLineNumberCC = increasedCLN;
			
			int[] increasedCCN = new int[datatypeSizeCC];
			System.arraycopy(datatypeCharsColumnNumberCC, 0, increasedCCN, 0, datatypeIndexCC);
			datatypeCharsColumnNumberCC = increasedCCN;
			
			DatatypedActiveTypeItem[] increasedCD = new DatatypedActiveTypeItem[datatypeSizeCC];
			System.arraycopy(datatypeCharsDefinitionCC, 0, increasedCD, 0, datatypeIndexCC);
			datatypeCharsDefinitionCC = increasedCD;
			
			String[] increasedEM = new String[datatypeSizeCC];
			System.arraycopy(datatypeErrorMessageCC, 0, increasedEM, 0, datatypeIndexCC);
			datatypeErrorMessageCC = increasedEM;
            
            int[] increasedFEC = new int[datatypeSizeCC];
			System.arraycopy(datatypeFECCC, 0, increasedFEC, 0, datatypeIndexCC);
			datatypeFECCC = increasedFEC;
		}
		datatypeElementQNameCC[datatypeIndexCC] = elementQName;
		datatypeCharsSystemIdCC[datatypeIndexCC] = charsSystemId;
		datatypeCharsLineNumberCC[datatypeIndexCC] = charsLineNumber;
		datatypeCharsColumnNumberCC[datatypeIndexCC] = columnNumber;
		datatypeCharsDefinitionCC[datatypeIndexCC] = charsDefinition;
		datatypeErrorMessageCC[datatypeIndexCC] = datatypeErrorMessage;
        
        datatypeFECCC[datatypeIndexCC] = functionalEquivalenceCode;
	}
	public void clearCharacterContentDatatypeError(){
        errorTotalCount -= datatypeSizeCC;
        datatypeSizeCC = 0;
        datatypeIndexCC = -1;
        datatypeElementQNameCC = null;
        datatypeCharsSystemIdCC = null;
        datatypeCharsLineNumberCC = null;
        datatypeCharsColumnNumberCC = null;
        datatypeCharsDefinitionCC = null;
        datatypeErrorMessageCC = null;
        
        datatypeFECCC = null;
    }
    public void clearCharacterContentDatatypeError(int messageId){
        int removeIndex = getRemoveIndex(messageId, datatypeFECCC);
        int moved = datatypeIndexCC - removeIndex;
        datatypeIndexCC--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(datatypeElementQNameCC, removeIndex+1, datatypeElementQNameCC, removeIndex, moved);
            System.arraycopy(datatypeCharsSystemIdCC, removeIndex+1, datatypeCharsSystemIdCC, removeIndex, moved);
            System.arraycopy(datatypeCharsLineNumberCC, removeIndex+1, datatypeCharsLineNumberCC, removeIndex, moved);
            System.arraycopy(datatypeCharsColumnNumberCC, removeIndex+1, datatypeCharsColumnNumberCC, removeIndex, moved);
            System.arraycopy(datatypeCharsDefinitionCC, removeIndex+1, datatypeCharsDefinitionCC, removeIndex, moved);
            System.arraycopy(datatypeErrorMessageCC, removeIndex+1, datatypeErrorMessageCC, removeIndex, moved);
            System.arraycopy(datatypeFECCC, removeIndex+1, datatypeFECCC, removeIndex, moved);
        }
    }
        
    
    //{16}
	public void attributeValueDatatypeError(int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        errorTotalCount++;
		if(datatypeSizeAV == 0){
			datatypeSizeAV = 1;
			datatypeIndexAV = 0;
			datatypeAttributeQNameAV = new String[datatypeSizeAV];
			datatypeCharsSystemIdAV = new String[datatypeSizeAV];
			datatypeCharsLineNumberAV = new int[datatypeSizeAV];
			datatypeCharsColumnNumberAV = new int[datatypeSizeAV];
			datatypeCharsDefinitionAV = new DatatypedActiveTypeItem[datatypeSizeAV];
			datatypeErrorMessageAV = new String[datatypeSizeAV];
            
            datatypeFECAV = new int[datatypeSizeAV];
		}else if(++datatypeIndexAV == datatypeSizeAV){
			String[] increasedEQ = new String[++datatypeSizeAV];
			System.arraycopy(datatypeAttributeQNameAV, 0, increasedEQ, 0, datatypeIndexAV);
			datatypeAttributeQNameAV = increasedEQ;
						
			String[] increasedCSI = new String[datatypeSizeAV];
			System.arraycopy(datatypeCharsSystemIdAV, 0, increasedCSI, 0, datatypeIndexAV);
			datatypeCharsSystemIdAV = increasedCSI;
			
			int[] increasedCLN = new int[datatypeSizeAV];
			System.arraycopy(datatypeCharsLineNumberAV, 0, increasedCLN, 0, datatypeIndexAV);
			datatypeCharsLineNumberAV = increasedCLN;
			
			int[] increasedAVN = new int[datatypeSizeAV];
			System.arraycopy(datatypeCharsColumnNumberAV, 0, increasedAVN, 0, datatypeIndexAV);
			datatypeCharsColumnNumberAV = increasedAVN;
			
			DatatypedActiveTypeItem[] increasedCD = new DatatypedActiveTypeItem[datatypeSizeAV];
			System.arraycopy(datatypeCharsDefinitionAV, 0, increasedCD, 0, datatypeIndexAV);
			datatypeCharsDefinitionAV = increasedCD;
			
			String[] increasedEM = new String[datatypeSizeAV];
			System.arraycopy(datatypeErrorMessageAV, 0, increasedEM, 0, datatypeIndexAV);
			datatypeErrorMessageAV = increasedEM;
            
            int[] increasedFEC = new int[datatypeSizeAV];
			System.arraycopy(datatypeFECAV, 0, increasedFEC, 0, datatypeIndexAV);
			datatypeFECAV = increasedFEC;
		}
		datatypeAttributeQNameAV[datatypeIndexAV] = attributeQName;
		datatypeCharsSystemIdAV[datatypeIndexAV] = charsSystemId;
		datatypeCharsLineNumberAV[datatypeIndexAV] = charsLineNumber;
		datatypeCharsColumnNumberAV[datatypeIndexAV] = columnNumber;
		datatypeCharsDefinitionAV[datatypeIndexAV] = charsDefinition;
		datatypeErrorMessageAV[datatypeIndexAV] = datatypeErrorMessage;
        
        datatypeFECAV[datatypeIndexAV] = functionalEquivalenceCode;
	}
	public void clearAttributeValueDatatypeError(){
        errorTotalCount -= datatypeSizeAV;
        datatypeSizeAV = 0;
        datatypeIndexAV = -1;
        datatypeAttributeQNameAV = null;
        datatypeCharsSystemIdAV = null;
        datatypeCharsLineNumberAV = null;
        datatypeCharsColumnNumberAV = null;
        datatypeCharsDefinitionAV = null;
        datatypeErrorMessageAV = null;
        
        datatypeFECAV = null;
    }
    public void clearAttributeValueDatatypeError(int messageId){
        int removeIndex = getRemoveIndex(messageId, datatypeFECAV);
        int moved = datatypeIndexAV - removeIndex;
        datatypeIndexAV--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(datatypeAttributeQNameAV, removeIndex+1, datatypeAttributeQNameAV, removeIndex, moved);
            System.arraycopy(datatypeCharsSystemIdAV, removeIndex+1, datatypeCharsSystemIdAV, removeIndex, moved);
            System.arraycopy(datatypeCharsLineNumberAV, removeIndex+1, datatypeCharsLineNumberAV, removeIndex, moved);
            System.arraycopy(datatypeCharsColumnNumberAV, removeIndex+1, datatypeCharsColumnNumberAV, removeIndex, moved);
            System.arraycopy(datatypeCharsDefinitionAV, removeIndex+1, datatypeCharsDefinitionAV, removeIndex, moved);
            System.arraycopy(datatypeErrorMessageAV, removeIndex+1, datatypeErrorMessageAV, removeIndex, moved);
            System.arraycopy(datatypeFECAV, removeIndex+1, datatypeFECAV, removeIndex, moved);
        }
    }
        
        
	public void characterContentValueError(int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        errorTotalCount++;
		if(valueSizeCC == 0){
			valueSizeCC = 1;
			valueIndexCC = 0;
			valueElementQNameCC = new String[valueSizeCC];
			valueCharsSystemIdCC = new String[valueSizeCC];
			valueCharsLineNumberCC = new int[valueSizeCC];
			valueCharsColumnNumberCC = new int[valueSizeCC];
			valueCharsDefinitionCC = new AValue[valueSizeCC];
            
            valueFECCC = new int[valueSizeCC];
		}else if(++valueIndexCC == valueSizeCC){
			String[] increasedEQ = new String[++valueSizeCC];
			System.arraycopy(valueElementQNameCC, 0, increasedEQ, 0, valueIndexCC);
			valueElementQNameCC = increasedEQ;
						
			String[] increasedCSI = new String[valueSizeCC];
			System.arraycopy(valueCharsSystemIdCC, 0, increasedCSI, 0, valueIndexCC);
			valueCharsSystemIdCC = increasedCSI;
			
			int[] increasedCLN = new int[valueSizeCC];
			System.arraycopy(valueCharsLineNumberCC, 0, increasedCLN, 0, valueIndexCC);
			valueCharsLineNumberCC = increasedCLN;
			
			int[] increasedCCN = new int[valueSizeCC];
			System.arraycopy(valueCharsColumnNumberCC, 0, increasedCCN, 0, valueIndexCC);
			valueCharsColumnNumberCC = increasedCCN;
			
			AValue[] increasedCD = new AValue[valueSizeCC];
			System.arraycopy(valueCharsDefinitionCC, 0, increasedCD, 0, valueIndexCC);
			valueCharsDefinitionCC = increasedCD;

            int[] increasedFEC = new int[valueSizeCC];
			System.arraycopy(valueFECCC, 0, increasedFEC, 0, valueIndexCC);
			valueFECCC = increasedFEC;
						
		}
		valueElementQNameCC[valueIndexCC] = elementQName;
		valueCharsSystemIdCC[valueIndexCC] = charsSystemId;
		valueCharsLineNumberCC[valueIndexCC] = charsLineNumber;
		valueCharsColumnNumberCC[valueIndexCC] = columnNumber;
		valueCharsDefinitionCC[valueIndexCC] = charsDefinition;
        
        valueFECCC[valueIndexCC] = functionalEquivalenceCode;
	}
    public void clearCharacterContentValueError(){
        errorTotalCount -= valueSizeCC;
        valueSizeCC = 0;
        valueIndexCC = -1;
        valueElementQNameCC = null;
        valueCharsSystemIdCC = null;
        valueCharsLineNumberCC = null;
        valueCharsColumnNumberCC = null;
        valueCharsDefinitionCC = null;
        
        valueFECCC = null;
    }
    public void clearCharacterContentValueError(int messageId){
        int removeIndex = getRemoveIndex(messageId, valueFECCC);
        int moved = valueIndexCC - removeIndex;
        valueIndexCC--;
        errorTotalCount--;
        if(moved > 0){  
            System.arraycopy(valueElementQNameCC, removeIndex+1, valueElementQNameCC, removeIndex, moved);
            System.arraycopy(valueCharsSystemIdCC, removeIndex+1, valueCharsSystemIdCC, removeIndex, moved);
            System.arraycopy(valueCharsLineNumberCC, removeIndex+1, valueCharsLineNumberCC, removeIndex, moved);
            System.arraycopy(valueCharsColumnNumberCC, removeIndex+1, valueCharsColumnNumberCC, removeIndex, moved);
            System.arraycopy(valueCharsDefinitionCC, removeIndex+1, valueCharsDefinitionCC, removeIndex, moved);
            System.arraycopy(valueFECCC, removeIndex+1, valueFECCC, removeIndex, moved);
        }
    }
        
    
	public void attributeValueValueError(int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        errorTotalCount++;
		if(valueSizeAV == 0){
			valueSizeAV = 1;
			valueIndexAV = 0;
			valueAttributeQNameAV = new String[valueSizeAV];
			valueCharsSystemIdAV = new String[valueSizeAV];
			valueCharsLineNumberAV = new int[valueSizeAV];
			valueCharsColumnNumberAV = new int[valueSizeAV];
			valueCharsDefinitionAV = new AValue[valueSizeAV];
            
            valueFECAV = new int[valueSizeAV];
		}else if(++valueIndexAV == valueSizeAV){
			String[] increasedEQ = new String[++valueSizeAV];
			System.arraycopy(valueAttributeQNameAV, 0, increasedEQ, 0, valueIndexAV);
			valueAttributeQNameAV = increasedEQ;
						
			String[] increasedCSI = new String[valueSizeAV];
			System.arraycopy(valueCharsSystemIdAV, 0, increasedCSI, 0, valueIndexAV);
			valueCharsSystemIdAV = increasedCSI;
			
			int[] increasedCLN = new int[valueSizeAV];
			System.arraycopy(valueCharsLineNumberAV, 0, increasedCLN, 0, valueIndexAV);
			valueCharsLineNumberAV = increasedCLN;
			
			int[] increasedAVN = new int[valueSizeAV];
			System.arraycopy(valueCharsColumnNumberAV, 0, increasedAVN, 0, valueIndexAV);
			valueCharsColumnNumberAV = increasedAVN;
			
			AValue[] increasedCD = new AValue[valueSizeAV];
			System.arraycopy(valueCharsDefinitionAV, 0, increasedCD, 0, valueIndexAV);
			valueCharsDefinitionAV = increasedCD;
            
            int[] increasedFEC = new int[valueSizeAV];
			System.arraycopy(valueFECAV, 0, increasedFEC, 0, valueIndexAV);
			valueFECAV = increasedFEC;
		}
		valueAttributeQNameAV[valueIndexAV] = attributeQName;
		valueCharsSystemIdAV[valueIndexAV] = charsSystemId;
		valueCharsLineNumberAV[valueIndexAV] = charsLineNumber;
		valueCharsColumnNumberAV[valueIndexAV] = columnNumber;
		valueCharsDefinitionAV[valueIndexAV] = charsDefinition;
        
        valueFECAV[valueIndexAV] = functionalEquivalenceCode;
	}
	public void clearAttributeValueValueError(){
        errorTotalCount -= valueSizeAV;
        valueSizeAV = 0;
        valueIndexAV = -1;
        valueAttributeQNameAV = null;
        valueCharsSystemIdAV = null;
        valueCharsLineNumberAV = null;
        valueCharsColumnNumberAV = null;
        valueCharsDefinitionAV = null;
        
        valueFECAV = null;
    }    
    public void clearAttributeValueValueError(int messageId){
        int removeIndex = getRemoveIndex(messageId, valueFECAV);
        int moved = valueIndexAV - removeIndex;
        valueSizeAV--;
        errorTotalCount--;
        if(moved > 0){  
            System.arraycopy(valueAttributeQNameAV, removeIndex+1, valueAttributeQNameAV, removeIndex, moved);
            System.arraycopy(valueCharsSystemIdAV, removeIndex+1, valueCharsSystemIdAV, removeIndex, moved);
            System.arraycopy(valueCharsLineNumberAV, removeIndex+1, valueCharsLineNumberAV, removeIndex, moved);
            System.arraycopy(valueCharsColumnNumberAV, removeIndex+1, valueCharsColumnNumberAV, removeIndex, moved);
            System.arraycopy(valueCharsDefinitionAV, removeIndex+1, valueCharsDefinitionAV, removeIndex, moved);
            System.arraycopy(valueFECAV, removeIndex+1, valueFECAV, removeIndex, moved);
        }
    }
        
    
	public void characterContentExceptedError(int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        errorTotalCount++;
		if(exceptSizeCC == 0){
			exceptSizeCC = 1;
			exceptIndexCC = 0;
			exceptElementQNameCC = new String[exceptSizeCC];
			exceptCharsSystemIdCC = new String[exceptSizeCC];
			exceptCharsLineNumberCC = new int[exceptSizeCC];
			exceptCharsColumnNumberCC = new int[exceptSizeCC];
			exceptCharsDefinitionCC = new AData[exceptSizeCC];
            
            exceptFECCC = new int[exceptSizeCC];
		}else if(++exceptIndexCC == exceptSizeCC){
			String[] increasedEQ = new String[++exceptSizeCC];
			System.arraycopy(exceptElementQNameCC, 0, increasedEQ, 0, exceptIndexCC);
			exceptElementQNameCC = increasedEQ;
						
			String[] increasedCSI = new String[exceptSizeCC];
			System.arraycopy(exceptCharsSystemIdCC, 0, increasedCSI, 0, exceptIndexCC);
			exceptCharsSystemIdCC = increasedCSI;
			
			int[] increasedCLN = new int[exceptSizeCC];
			System.arraycopy(exceptCharsLineNumberCC, 0, increasedCLN, 0, exceptIndexCC);
			exceptCharsLineNumberCC = increasedCLN;
			
			int[] increasedCCN = new int[exceptSizeCC];
			System.arraycopy(exceptCharsColumnNumberCC, 0, increasedCCN, 0, exceptIndexCC);
			exceptCharsColumnNumberCC = increasedCCN;
			
			AData[] increasedCD = new AData[exceptSizeCC];
			System.arraycopy(exceptCharsDefinitionCC, 0, increasedCD, 0, exceptIndexCC);
			exceptCharsDefinitionCC = increasedCD;	
            
            int[] increasedFEC = new int[exceptSizeCC];
			System.arraycopy(exceptFECCC, 0, increasedFEC, 0, exceptIndexCC);
			exceptFECCC = increasedFEC;
		}
		exceptElementQNameCC[exceptIndexCC] = elementQName;
		exceptCharsSystemIdCC[exceptIndexCC] = charsSystemId;
		exceptCharsLineNumberCC[exceptIndexCC] = charsLineNumber;
		exceptCharsColumnNumberCC[exceptIndexCC] = columnNumber;
		exceptCharsDefinitionCC[exceptIndexCC] = charsDefinition;
        
        exceptFECCC[exceptIndexCC] = functionalEquivalenceCode;
	}
    public void clearCharacterContentExceptedError(){
        errorTotalCount -= exceptSizeCC;
        exceptSizeCC = 0;
        exceptIndexCC = -1;
        exceptElementQNameCC = null;
        exceptCharsSystemIdCC = null;
        exceptCharsLineNumberCC = null;
        exceptCharsColumnNumberCC = null;
        exceptCharsDefinitionCC = null;
        
        exceptFECCC = null;
    }
    public void clearCharacterContentExceptedError(int messageId){
        int removeIndex = getRemoveIndex(messageId, exceptFECCC);
        int moved = exceptIndexCC - removeIndex;
        exceptIndexCC--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(exceptElementQNameCC, removeIndex+1, exceptElementQNameCC, removeIndex, moved);
            System.arraycopy(exceptCharsSystemIdCC, removeIndex+1, exceptCharsSystemIdCC, removeIndex, moved);
            System.arraycopy(exceptCharsLineNumberCC, removeIndex+1, exceptCharsLineNumberCC, removeIndex, moved);
            System.arraycopy(exceptCharsColumnNumberCC, removeIndex+1, exceptCharsColumnNumberCC, removeIndex, moved);
            System.arraycopy(exceptCharsDefinitionCC, removeIndex+1, exceptCharsDefinitionCC, removeIndex, moved);
            System.arraycopy(exceptFECCC, removeIndex+1, exceptFECCC, removeIndex, moved);
        }
    }
    
        
	public void attributeValueExceptedError(int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        errorTotalCount++;
		if(exceptSizeAV == 0){
			exceptSizeAV = 1;
			exceptIndexAV = 0;
			exceptAttributeQNameAV = new String[exceptSizeAV];
			exceptCharsSystemIdAV = new String[exceptSizeAV];
			exceptCharsLineNumberAV = new int[exceptSizeAV];
			exceptCharsColumnNumberAV = new int[exceptSizeAV];
			exceptCharsDefinitionAV = new AData[exceptSizeAV];
            
            exceptFECAV = new int[exceptSizeAV];
		}else if(++exceptIndexAV == exceptSizeAV){
			String[] increasedEQ = new String[++exceptSizeAV];
			System.arraycopy(exceptAttributeQNameAV, 0, increasedEQ, 0, exceptIndexAV);
			exceptAttributeQNameAV = increasedEQ;
						
			String[] increasedCSI = new String[exceptSizeAV];
			System.arraycopy(exceptCharsSystemIdAV, 0, increasedCSI, 0, exceptIndexAV);
			exceptCharsSystemIdAV = increasedCSI;
			
			int[] increasedCLN = new int[exceptSizeAV];
			System.arraycopy(exceptCharsLineNumberAV, 0, increasedCLN, 0, exceptIndexAV);
			exceptCharsLineNumberAV = increasedCLN;
			
			int[] increasedAVN = new int[exceptSizeAV];
			System.arraycopy(exceptCharsColumnNumberAV, 0, increasedAVN, 0, exceptIndexAV);
			exceptCharsColumnNumberAV = increasedAVN;
			
			AData[] increasedCD = new AData[exceptSizeAV];
			System.arraycopy(exceptCharsDefinitionAV, 0, increasedCD, 0, exceptIndexAV);
			exceptCharsDefinitionAV = increasedCD;
            
            int[] increasedFEC = new int[exceptSizeAV];
			System.arraycopy(exceptFECAV, 0, increasedFEC, 0, exceptIndexAV);
			exceptFECAV = increasedFEC;
		}
		exceptAttributeQNameAV[exceptIndexAV] = attributeQName;
		exceptCharsSystemIdAV[exceptIndexAV] = charsSystemId;
		exceptCharsLineNumberAV[exceptIndexAV] = charsLineNumber;
		exceptCharsColumnNumberAV[exceptIndexAV] = columnNumber;
		exceptCharsDefinitionAV[exceptIndexAV] = charsDefinition;
        
        exceptFECAV[exceptIndexAV] = functionalEquivalenceCode;
	}
	public void clearAttributeValueExceptedError(){
        errorTotalCount -= exceptSizeAV;
        exceptSizeAV = 0;
        exceptIndexAV = -1;
        exceptAttributeQNameAV = null;
        exceptCharsSystemIdAV = null;
        exceptCharsLineNumberAV = null;
        exceptCharsColumnNumberAV = null;
        exceptCharsDefinitionAV = null;
        
        exceptFECAV = null;
    }
    public void clearAttributeValueExceptedError(int messageId){
        int removeIndex = getRemoveIndex(messageId, exceptFECAV);
        int moved = exceptIndexAV - removeIndex;
        exceptIndexAV--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(exceptAttributeQNameAV, removeIndex+1, exceptAttributeQNameAV, removeIndex, moved);
            System.arraycopy(exceptCharsSystemIdAV, removeIndex+1, exceptCharsSystemIdAV, removeIndex, moved);
            System.arraycopy(exceptCharsLineNumberAV, removeIndex+1, exceptCharsLineNumberAV, removeIndex, moved);
            System.arraycopy(exceptCharsColumnNumberAV, removeIndex+1, exceptCharsColumnNumberAV, removeIndex, moved);
            System.arraycopy(exceptCharsDefinitionAV, removeIndex+1, exceptCharsDefinitionAV, removeIndex, moved);
            System.arraycopy(exceptFECAV, removeIndex+1, exceptFECAV, removeIndex, moved);
        }
    }
        
    
	public void unexpectedCharacterContent(int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
        errorTotalCount++;
		if(unexpectedSizeCC == 0){
			unexpectedSizeCC = 1;
			unexpectedIndexCC = 0;		
			unexpectedCharsSystemIdCC = new String[unexpectedSizeCC];
			unexpectedCharsLineNumberCC = new int[unexpectedSizeCC];
			unexpectedCharsColumnNumberCC = new int[unexpectedSizeCC];
			unexpectedContextDefinitionCC = new AElement[unexpectedSizeCC];
            
            unexpectedFECCC = new int[unexpectedSizeCC];
		}else if(++unexpectedIndexCC == unexpectedSizeCC){
			String[] increasedCSI = new String[++unexpectedSizeCC];
			System.arraycopy(unexpectedCharsSystemIdCC, 0, increasedCSI, 0, unexpectedIndexCC);
			unexpectedCharsSystemIdCC = increasedCSI;
			
			int[] increasedCLN = new int[unexpectedSizeCC];
			System.arraycopy(unexpectedCharsLineNumberCC, 0, increasedCLN, 0, unexpectedIndexCC);
			unexpectedCharsLineNumberCC = increasedCLN;
			
			int[] increasedCCN = new int[unexpectedSizeCC];
			System.arraycopy(unexpectedCharsColumnNumberCC, 0, increasedCCN, 0, unexpectedIndexCC);
			unexpectedCharsColumnNumberCC = increasedCCN;
			
			AElement[] increasedCD = new AElement[unexpectedSizeCC];
			System.arraycopy(unexpectedContextDefinitionCC, 0, increasedCD, 0, unexpectedIndexCC);
			unexpectedContextDefinitionCC = increasedCD;

            int[] increasedFEC = new int[unexpectedSizeCC];
			System.arraycopy(unexpectedFECCC, 0, increasedFEC, 0, unexpectedIndexCC);
			unexpectedFECCC = increasedFEC;			
		}
		unexpectedCharsSystemIdCC[unexpectedIndexCC] = charsSystemId;
		unexpectedCharsLineNumberCC[unexpectedIndexCC] = charsLineNumber;
		unexpectedCharsColumnNumberCC[unexpectedIndexCC] = columnNumber;
		unexpectedContextDefinitionCC[unexpectedIndexCC] = elementDefinition;
        
        unexpectedFECCC[unexpectedIndexCC] = functionalEquivalenceCode;
	}
    public void clearUnexpectedCharacterContent(){
        errorTotalCount -= unexpectedSizeCC;
        unexpectedSizeCC = 0;
        unexpectedIndexCC = -1;		
        unexpectedCharsSystemIdCC = null;
        unexpectedCharsLineNumberCC = null;
        unexpectedCharsColumnNumberCC = null;
        unexpectedContextDefinitionCC = null;
        
        unexpectedFECCC = null;
    }
    public void clearUnexpectedCharacterContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, unexpectedFECCC);
        int moved = unexpectedIndexCC - removeIndex;
        unexpectedIndexCC--;
        errorTotalCount--;
        if(moved > 0){              
            System.arraycopy(unexpectedCharsSystemIdCC, removeIndex+1, unexpectedCharsSystemIdCC, removeIndex, moved);
            System.arraycopy(unexpectedCharsLineNumberCC, removeIndex+1, unexpectedCharsLineNumberCC, removeIndex, moved);
            System.arraycopy(unexpectedCharsColumnNumberCC, removeIndex+1, unexpectedCharsColumnNumberCC, removeIndex, moved);
            System.arraycopy(unexpectedContextDefinitionCC, removeIndex+1, unexpectedContextDefinitionCC, removeIndex, moved);
            System.arraycopy(unexpectedFECCC, removeIndex+1, unexpectedFECCC, removeIndex, moved);
        }
    }
    
    
	public void unexpectedAttributeValue(int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        errorTotalCount++;
		if(unexpectedSizeAV == 0){
			unexpectedSizeAV = 1;
			unexpectedIndexAV = 0;		
			unexpectedCharsSystemIdAV = new String[unexpectedSizeAV];
			unexpectedCharsLineNumberAV = new int[unexpectedSizeAV];
			unexpectedCharsColumnNumberAV = new int[unexpectedSizeAV];
			unexpectedContextDefinitionAV = new AAttribute[unexpectedSizeAV];
            
            unexpectedFECAV = new int[unexpectedSizeAV];
		}else if(++unexpectedIndexAV == unexpectedSizeAV){
			String[] increasedCSI = new String[++unexpectedSizeAV];
			System.arraycopy(unexpectedCharsSystemIdAV, 0, increasedCSI, 0, unexpectedIndexAV);
			unexpectedCharsSystemIdAV = increasedCSI;
			
			int[] increasedCLN = new int[unexpectedSizeAV];
			System.arraycopy(unexpectedCharsLineNumberAV, 0, increasedCLN, 0, unexpectedIndexAV);
			unexpectedCharsLineNumberAV = increasedCLN;
			
			int[] increasedAVN = new int[unexpectedSizeAV];
			System.arraycopy(unexpectedCharsColumnNumberAV, 0, increasedAVN, 0, unexpectedIndexAV);
			unexpectedCharsColumnNumberAV = increasedAVN;
			
			AAttribute[] increasedCD = new AAttribute[unexpectedSizeAV];
			System.arraycopy(unexpectedContextDefinitionAV, 0, increasedCD, 0, unexpectedIndexAV);
			unexpectedContextDefinitionAV = increasedCD;

            int[] increasedFEC = new int[unexpectedSizeAV];
			System.arraycopy(unexpectedFECAV, 0, increasedFEC, 0, unexpectedIndexAV);
			unexpectedFECAV = increasedFEC;			
		}
		unexpectedCharsSystemIdAV[unexpectedIndexAV] = charsSystemId;
		unexpectedCharsLineNumberAV[unexpectedIndexAV] = charsLineNumber;
		unexpectedCharsColumnNumberAV[unexpectedIndexAV] = columnNumber;
		unexpectedContextDefinitionAV[unexpectedIndexAV] = attributeDefinition;
        
        unexpectedFECAV[unexpectedIndexAV] = functionalEquivalenceCode;
	}
	public void clearUnexpectedAttributeValue(){
        errorTotalCount -= unexpectedSizeAV;
        unexpectedSizeAV = 0;
        unexpectedIndexAV = -1;		
        unexpectedCharsSystemIdAV = null;
        unexpectedCharsLineNumberAV = null;
        unexpectedCharsColumnNumberAV = null;
        unexpectedContextDefinitionAV = null;
        
        unexpectedFECAV = null;
    }
    public void clearUnexpectedAttributeValue(int messageId){
        int removeIndex = getRemoveIndex(messageId, unexpectedFECAV);
        int moved = unexpectedIndexAV - removeIndex;
        unexpectedIndexAV--;
        errorTotalCount--;
        if(moved > 0){  
            System.arraycopy(unexpectedCharsSystemIdAV, removeIndex+1, unexpectedCharsSystemIdAV, removeIndex, moved);
            System.arraycopy(unexpectedCharsLineNumberAV, removeIndex+1, unexpectedCharsLineNumberAV, removeIndex, moved);
            System.arraycopy(unexpectedCharsColumnNumberAV, removeIndex+1, unexpectedCharsColumnNumberAV, removeIndex, moved);
            System.arraycopy(unexpectedContextDefinitionAV, removeIndex+1, unexpectedContextDefinitionAV, removeIndex, moved);
            System.arraycopy(unexpectedFECAV, removeIndex+1, unexpectedFECAV, removeIndex, moved);
        }
    }
        
    
	public void ambiguousCharacterContent(int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousSizeCC == 0){
			ambiguousSizeCC = 1;
			ambiguousIndexCC = 0;		
			ambiguousCharsSystemIdEECC = new String[ambiguousSizeCC];
			ambiguousCharsLineNumberEECC = new int[ambiguousSizeCC];
			ambiguousCharsColumnNumberEECC = new int[ambiguousSizeCC];
			ambiguousPossibleDefinitionsCC = new CharsActiveTypeItem[ambiguousSizeCC][];
            
            ambiguousFECCC = new int[ambiguousSizeCC];
		}else if(++ambiguousIndexCC == ambiguousSizeCC){
			String[] increasedCSI = new String[++ambiguousSizeCC];
			System.arraycopy(ambiguousCharsSystemIdEECC, 0, increasedCSI, 0, ambiguousIndexCC);
			ambiguousCharsSystemIdEECC = increasedCSI;
			
			int[] increasedCLN = new int[ambiguousSizeCC];
			System.arraycopy(ambiguousCharsLineNumberEECC, 0, increasedCLN, 0, ambiguousIndexCC);
			ambiguousCharsLineNumberEECC = increasedCLN;
			
			int[] increasedCCN = new int[ambiguousSizeCC];
			System.arraycopy(ambiguousCharsColumnNumberEECC, 0, increasedCCN, 0, ambiguousIndexCC);
			ambiguousCharsColumnNumberEECC = increasedCCN;
			
			CharsActiveTypeItem[][] increasedPD = new CharsActiveTypeItem[ambiguousSizeCC][];
			System.arraycopy(ambiguousPossibleDefinitionsCC, 0, increasedPD, 0, ambiguousIndexCC);
			ambiguousPossibleDefinitionsCC = increasedPD;

            int[] increasedFEC = new int[ambiguousSizeCC];
			System.arraycopy(ambiguousFECCC, 0, increasedFEC, 0, ambiguousIndexCC);
			ambiguousFECCC = increasedFEC;			
		}
		ambiguousCharsSystemIdEECC[ambiguousIndexCC] = systemId;
		ambiguousCharsLineNumberEECC[ambiguousIndexCC] = lineNumber;
		ambiguousCharsColumnNumberEECC[ambiguousIndexCC] = columnNumber;
		ambiguousPossibleDefinitionsCC[ambiguousIndexCC] = possibleDefinitions;
        
        ambiguousFECCC[ambiguousIndexCC] = functionalEquivalenceCode;
	}
    public void clearAmbiguousCharacterContent(){
        errorTotalCount -= ambiguousSizeCC;
        ambiguousSizeCC = 0;
        ambiguousIndexCC = -1;		
        ambiguousCharsSystemIdEECC = null;
        ambiguousCharsLineNumberEECC = null;
        ambiguousCharsColumnNumberEECC = null;
        ambiguousPossibleDefinitionsCC = null;
        
        ambiguousFECCC = null;
    }
    public void clearAmbiguousCharacterContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousFECCC);
        int moved = ambiguousIndexCC - removeIndex;
        ambiguousIndexCC--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousCharsSystemIdEECC, removeIndex+1, ambiguousCharsSystemIdEECC, removeIndex, moved);
            System.arraycopy(ambiguousCharsLineNumberEECC, removeIndex+1, ambiguousCharsLineNumberEECC, removeIndex, moved);
            System.arraycopy(ambiguousCharsColumnNumberEECC, removeIndex+1, ambiguousCharsColumnNumberEECC, removeIndex, moved);
            System.arraycopy(ambiguousPossibleDefinitionsCC, removeIndex+1, ambiguousPossibleDefinitionsCC, removeIndex, moved);
            System.arraycopy(ambiguousFECCC, removeIndex+1, ambiguousFECCC, removeIndex, moved);
        }
    }
    
    
	// {24}
	public void ambiguousAttributeValue(int functionalEquivalenceCode, String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousSizeAV == 0){
			ambiguousSizeAV = 1;
			ambiguousIndexAV = 0;
			ambiguousAttributeQNameEEAV = new String[ambiguousSizeAV];
			ambiguousCharsSystemIdEEAV = new String[ambiguousSizeAV];
			ambiguousCharsLineNumberEEAV = new int[ambiguousSizeAV];
			ambiguousCharsColumnNumberEEAV = new int[ambiguousSizeAV];
			ambiguousPossibleDefinitionsAV = new CharsActiveTypeItem[ambiguousSizeAV][];
            
            ambiguousFECAV = new int[ambiguousSizeAV];
		}else if(++ambiguousIndexAV == ambiguousSizeAV){
			String[] increasedAQ = new String[++ambiguousSizeAV];
			System.arraycopy(ambiguousAttributeQNameEEAV, 0, increasedAQ, 0, ambiguousIndexAV);
			ambiguousAttributeQNameEEAV = increasedAQ;
			
			String[] increasedCSI = new String[++ambiguousSizeAV];
			System.arraycopy(ambiguousCharsSystemIdEEAV, 0, increasedCSI, 0, ambiguousIndexAV);
			ambiguousCharsSystemIdEEAV = increasedCSI;
			
			int[] increasedCLN = new int[ambiguousSizeAV];
			System.arraycopy(ambiguousCharsLineNumberEEAV, 0, increasedCLN, 0, ambiguousIndexAV);
			ambiguousCharsLineNumberEEAV = increasedCLN;
			
			int[] increasedAVN = new int[ambiguousSizeAV];
			System.arraycopy(ambiguousCharsColumnNumberEEAV, 0, increasedAVN, 0, ambiguousIndexAV);
			ambiguousCharsColumnNumberEEAV = increasedAVN;
			
			CharsActiveTypeItem[][] increasedPD = new CharsActiveTypeItem[ambiguousSizeAV][];
			System.arraycopy(ambiguousPossibleDefinitionsAV, 0, increasedPD, 0, ambiguousIndexAV);
			ambiguousPossibleDefinitionsAV = increasedPD;

            int[] increasedFEC = new int[ambiguousSizeAV];
			System.arraycopy(ambiguousFECAV, 0, increasedFEC, 0, ambiguousIndexAV);
			ambiguousFECAV = increasedFEC;			
		}
		ambiguousAttributeQNameEEAV[ambiguousIndexAV] = attributeQName;
		ambiguousCharsSystemIdEEAV[ambiguousIndexAV] = systemId;
		ambiguousCharsLineNumberEEAV[ambiguousIndexAV] = lineNumber;
		ambiguousCharsColumnNumberEEAV[ambiguousIndexAV] = columnNumber;
		ambiguousPossibleDefinitionsAV[ambiguousIndexAV] = possibleDefinitions;
        
        ambiguousFECAV[ambiguousIndexAV] = functionalEquivalenceCode;
	}
	public void clearAmbiguousAttributeValue(){
        errorTotalCount -= ambiguousSizeAV;
        ambiguousSizeAV = 0;
        ambiguousIndexAV = -1;
        ambiguousAttributeQNameEEAV = null;
        ambiguousCharsSystemIdEEAV = null;
        ambiguousCharsLineNumberEEAV = null;
        ambiguousCharsColumnNumberEEAV = null;
        ambiguousPossibleDefinitionsAV = null;
        
        ambiguousFECAV = null;
    }
    public void clearAmbiguousAttributeValue(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousFECAV);
        int moved = ambiguousIndexAV - removeIndex;
        ambiguousIndexAV--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousAttributeQNameEEAV, removeIndex+1, ambiguousAttributeQNameEEAV, removeIndex, moved);
            System.arraycopy(ambiguousCharsSystemIdEEAV, removeIndex+1, ambiguousCharsSystemIdEEAV, removeIndex, moved);
            System.arraycopy(ambiguousCharsLineNumberEEAV, removeIndex+1, ambiguousCharsLineNumberEEAV, removeIndex, moved);
            System.arraycopy(ambiguousCharsColumnNumberEEAV, removeIndex+1, ambiguousCharsColumnNumberEEAV, removeIndex, moved);
            System.arraycopy(ambiguousPossibleDefinitionsAV, removeIndex+1, ambiguousPossibleDefinitionsAV, removeIndex, moved);
            System.arraycopy(ambiguousFECAV, removeIndex+1, ambiguousFECAV, removeIndex, moved);
        }
    }
        
    
    // {25}
	public void listTokenDatatypeError(int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        errorTotalCount++;
		if(datatypeSizeLP == 0){
			datatypeSizeLP = 1;
			datatypeIndexLP = 0;
			datatypeTokenLP = new String[datatypeSizeLP];
			datatypeCharsSystemIdLP = new String[datatypeSizeLP];
			datatypeCharsLineNumberLP = new int[datatypeSizeLP];
			datatypeCharsColumnNumberLP = new int[datatypeSizeLP];
			datatypeCharsDefinitionLP = new DatatypedActiveTypeItem[datatypeSizeLP];
			datatypeErrorMessageLP = new String[datatypeSizeLP];
            
            datatypeFECLP = new int[datatypeSizeLP];
		}else if(++datatypeIndexLP == datatypeSizeLP){
			String[] increasedT = new String[++datatypeSizeLP];
			System.arraycopy(datatypeTokenLP, 0, increasedT, 0, datatypeIndexLP);
			datatypeTokenLP = increasedT;
						
			String[] increasedCSI = new String[datatypeSizeLP];
			System.arraycopy(datatypeCharsSystemIdLP, 0, increasedCSI, 0, datatypeIndexLP);
			datatypeCharsSystemIdLP = increasedCSI;
			
			int[] increasedCLN = new int[datatypeSizeLP];
			System.arraycopy(datatypeCharsLineNumberLP, 0, increasedCLN, 0, datatypeIndexLP);
			datatypeCharsLineNumberLP = increasedCLN;
			
			int[] increasedLPN = new int[datatypeSizeLP];
			System.arraycopy(datatypeCharsColumnNumberLP, 0, increasedLPN, 0, datatypeIndexLP);
			datatypeCharsColumnNumberLP = increasedLPN;
			
			DatatypedActiveTypeItem[] increasedCD = new DatatypedActiveTypeItem[datatypeSizeLP];
			System.arraycopy(datatypeCharsDefinitionLP, 0, increasedCD, 0, datatypeIndexLP);
			datatypeCharsDefinitionLP = increasedCD;
			
			String[] increasedEM = new String[datatypeSizeLP];
			System.arraycopy(datatypeErrorMessageLP, 0, increasedEM, 0, datatypeIndexLP);
			datatypeErrorMessageLP = increasedEM;
            
            int[] increasedFEC = new int[datatypeSizeLP];
			System.arraycopy(datatypeFECLP, 0, increasedFEC, 0, datatypeIndexLP);
			datatypeFECLP = increasedFEC;
		}
		datatypeTokenLP[datatypeIndexLP] = token;
		datatypeCharsSystemIdLP[datatypeIndexLP] = charsSystemId;
		datatypeCharsLineNumberLP[datatypeIndexLP] = charsLineNumber;
		datatypeCharsColumnNumberLP[datatypeIndexLP] = columnNumber;
		datatypeCharsDefinitionLP[datatypeIndexLP] = charsDefinition;
		datatypeErrorMessageLP[datatypeIndexLP] = datatypeErrorMessage;
        
        datatypeFECLP[datatypeIndexLP] = functionalEquivalenceCode;
	}
    public void clearListTokenDatatypeError(){
        errorTotalCount -= datatypeSizeLP;
        datatypeSizeLP = 0;
        datatypeIndexLP = -1;
        datatypeTokenLP = null;
        datatypeCharsSystemIdLP = null;
        datatypeCharsLineNumberLP = null;
        datatypeCharsColumnNumberLP = null;
        datatypeCharsDefinitionLP = null;
        datatypeErrorMessageLP = null;
        
        datatypeFECLP = null;
    }
    public void clearListTokenDatatypeError(int messageId){
        int removeIndex = getRemoveIndex(messageId, datatypeFECLP);
        int moved = datatypeIndexLP - removeIndex;
        datatypeIndexLP--;
        errorTotalCount--;
        if(moved > 0){    
            System.arraycopy(datatypeTokenLP, removeIndex+1, datatypeTokenLP, removeIndex, moved);
            System.arraycopy(datatypeCharsSystemIdLP, removeIndex+1, datatypeCharsSystemIdLP, removeIndex, moved);
            System.arraycopy(datatypeCharsLineNumberLP, removeIndex+1, datatypeCharsLineNumberLP, removeIndex, moved);
            System.arraycopy(datatypeCharsColumnNumberLP, removeIndex+1, datatypeCharsColumnNumberLP, removeIndex, moved);
            System.arraycopy(datatypeCharsDefinitionLP, removeIndex+1, datatypeCharsDefinitionLP, removeIndex, moved);
            System.arraycopy(datatypeErrorMessageLP, removeIndex+1, datatypeErrorMessageLP, removeIndex, moved);
            System.arraycopy(datatypeFECLP, removeIndex+1, datatypeFECLP, removeIndex, moved);
        }
    }
        
        
	public void listTokenValueError(int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        errorTotalCount++;
		if(valueSizeLP == 0){
			valueSizeLP = 1;
			valueIndexLP = 0;
			valueTokenLP = new String[valueSizeLP];
			valueCharsSystemIdLP = new String[valueSizeLP];
			valueCharsLineNumberLP = new int[valueSizeLP];
			valueCharsColumnNumberLP = new int[valueSizeLP];
			valueCharsDefinitionLP = new AValue[valueSizeLP];
            
            valueFECLP = new int[valueSizeLP];
		}else if(++valueIndexLP == valueSizeLP){
			String[] increasedT = new String[++valueSizeLP];
			System.arraycopy(valueTokenLP, 0, increasedT, 0, valueIndexLP);
			valueTokenLP = increasedT;
						
			String[] increasedCSI = new String[valueSizeLP];
			System.arraycopy(valueCharsSystemIdLP, 0, increasedCSI, 0, valueIndexLP);
			valueCharsSystemIdLP = increasedCSI;
			
			int[] increasedCLN = new int[valueSizeLP];
			System.arraycopy(valueCharsLineNumberLP, 0, increasedCLN, 0, valueIndexLP);
			valueCharsLineNumberLP = increasedCLN;
			
			int[] increasedLPN = new int[valueSizeLP];
			System.arraycopy(valueCharsColumnNumberLP, 0, increasedLPN, 0, valueIndexLP);
			valueCharsColumnNumberLP = increasedLPN;
			
			AValue[] increasedCD = new AValue[valueSizeLP];
			System.arraycopy(valueCharsDefinitionLP, 0, increasedCD, 0, valueIndexLP);
			valueCharsDefinitionLP = increasedCD;	
            
            int[] increasedFEC = new int[valueSizeLP];
			System.arraycopy(valueFECLP, 0, increasedFEC, 0, valueIndexLP);
			valueFECLP = increasedFEC;
		}
		valueTokenLP[valueIndexLP] = token;
		valueCharsSystemIdLP[valueIndexLP] = charsSystemId;
		valueCharsLineNumberLP[valueIndexLP] = charsLineNumber;
		valueCharsColumnNumberLP[valueIndexLP] = columnNumber;
		valueCharsDefinitionLP[valueIndexLP] = charsDefinition;
        
        valueFECLP[valueIndexLP] = functionalEquivalenceCode;
	}
    public void clearListTokenValueError(){
        errorTotalCount -= valueSizeLP;
        valueSizeLP = 0;
        valueIndexLP = -1;
        valueTokenLP = null;
        valueCharsSystemIdLP = null;
        valueCharsLineNumberLP = null;
        valueCharsColumnNumberLP = null;
        valueCharsDefinitionLP = null;
        
        valueFECLP = null;
    }
    public void clearListTokenValueError(int messageId){
        int removeIndex = getRemoveIndex(messageId, valueFECLP);
        int moved = valueIndexLP - removeIndex;
        valueIndexLP--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(valueTokenLP, removeIndex+1, valueTokenLP, removeIndex, moved);
            System.arraycopy(valueCharsSystemIdLP, removeIndex+1, valueCharsSystemIdLP, removeIndex, moved);
            System.arraycopy(valueCharsLineNumberLP, removeIndex+1, valueCharsLineNumberLP, removeIndex, moved);
            System.arraycopy(valueCharsColumnNumberLP, removeIndex+1, valueCharsColumnNumberLP, removeIndex, moved);
            System.arraycopy(valueCharsDefinitionLP, removeIndex+1, valueCharsDefinitionLP, removeIndex, moved);    
            System.arraycopy(valueFECLP, removeIndex+1, valueFECLP, removeIndex, moved);
        }
    }
        
    
	public void listTokenExceptedError(int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        errorTotalCount++;
		if(exceptSizeLP == 0){
			exceptSizeLP = 1;
			exceptIndexLP = 0;
			exceptTokenLP = new String[exceptSizeLP];
			exceptCharsSystemIdLP = new String[exceptSizeLP];
			exceptCharsLineNumberLP = new int[exceptSizeLP];
			exceptCharsColumnNumberLP = new int[exceptSizeLP];
			exceptCharsDefinitionLP = new AData[exceptSizeLP];
            
            exceptFECLP = new int[exceptSizeLP];
		}else if(++exceptIndexLP == exceptSizeLP){
			String[] increasedT = new String[++exceptSizeLP];
			System.arraycopy(exceptTokenLP, 0, increasedT, 0, exceptIndexLP);
			exceptTokenLP = increasedT;
						
			String[] increasedCSI = new String[exceptSizeLP];
			System.arraycopy(exceptCharsSystemIdLP, 0, increasedCSI, 0, exceptIndexLP);
			exceptCharsSystemIdLP = increasedCSI;
			
			int[] increasedCLN = new int[exceptSizeLP];
			System.arraycopy(exceptCharsLineNumberLP, 0, increasedCLN, 0, exceptIndexLP);
			exceptCharsLineNumberLP = increasedCLN;
			
			int[] increasedLPN = new int[exceptSizeLP];
			System.arraycopy(exceptCharsColumnNumberLP, 0, increasedLPN, 0, exceptIndexLP);
			exceptCharsColumnNumberLP = increasedLPN;
			
			AData[] increasedCD = new AData[exceptSizeLP];
			System.arraycopy(exceptCharsDefinitionLP, 0, increasedCD, 0, exceptIndexLP);
			exceptCharsDefinitionLP = increasedCD;	
            
            int[] increasedFEC = new int[exceptSizeLP];
			System.arraycopy(exceptFECLP, 0, increasedFEC, 0, exceptIndexLP);
			exceptFECLP = increasedFEC;
		}
		exceptTokenLP[exceptIndexLP] = token;
		exceptCharsSystemIdLP[exceptIndexLP] = charsSystemId;
		exceptCharsLineNumberLP[exceptIndexLP] = charsLineNumber;
		exceptCharsColumnNumberLP[exceptIndexLP] = columnNumber;
		exceptCharsDefinitionLP[exceptIndexLP] = charsDefinition;
        
        exceptFECLP[exceptIndexLP] = functionalEquivalenceCode;
	}
    public void clearListTokenExceptedError(){
        errorTotalCount -= exceptSizeLP;
        exceptSizeLP = 0;
        exceptIndexLP = -1;
        exceptTokenLP = null;
        exceptCharsSystemIdLP = null;
        exceptCharsLineNumberLP = null;
        exceptCharsColumnNumberLP = null;
        exceptCharsDefinitionLP = null;
        
        exceptFECLP = null;
    }
    public void clearListTokenExceptedError(int messageId){
        int removeIndex = getRemoveIndex(messageId, exceptFECLP);
        int moved = exceptIndexLP - removeIndex;
        exceptIndexLP--;
        errorTotalCount--;
        if(moved > 0){    
            System.arraycopy(exceptTokenLP, removeIndex+1, exceptTokenLP, removeIndex, moved);
            System.arraycopy(exceptCharsSystemIdLP, removeIndex+1, exceptCharsSystemIdLP, removeIndex, moved);
            System.arraycopy(exceptCharsLineNumberLP, removeIndex+1, exceptCharsLineNumberLP, removeIndex, moved);
            System.arraycopy(exceptCharsColumnNumberLP, removeIndex+1, exceptCharsColumnNumberLP, removeIndex, moved);
            System.arraycopy(exceptCharsDefinitionLP, removeIndex+1, exceptCharsDefinitionLP, removeIndex, moved);
            System.arraycopy(exceptFECLP, removeIndex+1, exceptFECLP, removeIndex, moved);
        }
    }
    
    
	public void ambiguousListToken(int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
		if(ambiguousSizeLP == 0){
			ambiguousSizeLP = 1;
			ambiguousIndexLP = 0;
			ambiguousTokenLP = new String[ambiguousSizeLP];
			ambiguousCharsSystemIdEELP = new String[ambiguousSizeLP];
			ambiguousCharsLineNumberEELP = new int[ambiguousSizeLP];
			ambiguousCharsColumnNumberEELP = new int[ambiguousSizeLP];
			ambiguousPossibleDefinitionsLP = new CharsActiveTypeItem[ambiguousSizeLP][];
            
            ambiguousFECLP = new int[ambiguousSizeLP];
		}else if(++ambiguousIndexLP == ambiguousSizeLP){
			String[] increasedT = new String[++ambiguousSizeLP];
			System.arraycopy(ambiguousTokenLP, 0, increasedT, 0, ambiguousIndexLP);
			ambiguousTokenLP = increasedT;
						
			String[] increasedCSI = new String[ambiguousSizeLP];
			System.arraycopy(ambiguousCharsSystemIdEELP, 0, increasedCSI, 0, ambiguousIndexLP);
			ambiguousCharsSystemIdEELP = increasedCSI;
			
			int[] increasedCLN = new int[ambiguousSizeLP];
			System.arraycopy(ambiguousCharsLineNumberEELP, 0, increasedCLN, 0, ambiguousIndexLP);
			ambiguousCharsLineNumberEELP = increasedCLN;
			
			int[] increasedLPN = new int[ambiguousSizeLP];
			System.arraycopy(ambiguousCharsColumnNumberEELP, 0, increasedLPN, 0, ambiguousIndexLP);
			ambiguousCharsColumnNumberEELP = increasedLPN;
			
			CharsActiveTypeItem[][] increasedPD = new CharsActiveTypeItem[ambiguousSizeLP][];
			System.arraycopy(ambiguousPossibleDefinitionsLP, 0, increasedPD, 0, ambiguousIndexLP);
			ambiguousPossibleDefinitionsLP = increasedPD;

            int[] increasedFEC = new int[ambiguousSizeLP];
			System.arraycopy(ambiguousFECLP, 0, increasedFEC, 0, ambiguousIndexLP);
			ambiguousFECLP = increasedLPN;			
		}
		ambiguousTokenLP[ambiguousIndexLP] = token;
		ambiguousCharsSystemIdEELP[ambiguousIndexLP] = systemId;
		ambiguousCharsLineNumberEELP[ambiguousIndexLP] = lineNumber;
		ambiguousCharsColumnNumberEELP[ambiguousIndexLP] = columnNumber;
		ambiguousPossibleDefinitionsLP[ambiguousIndexLP] = possibleDefinitions;
        
        ambiguousFECLP[ambiguousIndexLP] = functionalEquivalenceCode;
	}
    public void clearAmbiguousListToken(){
        errorTotalCount -= ambiguousSizeLP;
        ambiguousSizeLP = 0;
        ambiguousIndexLP = -1;
        ambiguousTokenLP = null;
        ambiguousCharsSystemIdEELP = null;
        ambiguousCharsLineNumberEELP = null;
        ambiguousCharsColumnNumberEELP = null;
        ambiguousPossibleDefinitionsLP = null;
        
        ambiguousFECLP = null;
    }
    public void clearAmbiguousListToken(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousFECLP);
        int moved = ambiguousIndexLP - removeIndex;
        ambiguousIndexLP--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousTokenLP, removeIndex+1, ambiguousTokenLP, removeIndex, moved);
            System.arraycopy(ambiguousCharsSystemIdEELP, removeIndex+1, ambiguousCharsSystemIdEELP, removeIndex, moved);
            System.arraycopy(ambiguousCharsLineNumberEELP, removeIndex+1, ambiguousCharsLineNumberEELP, removeIndex, moved);
            System.arraycopy(ambiguousCharsColumnNumberEELP, removeIndex+1, ambiguousCharsColumnNumberEELP, removeIndex, moved);
            System.arraycopy(ambiguousPossibleDefinitionsLP, removeIndex+1, ambiguousPossibleDefinitionsLP, removeIndex, moved);
            System.arraycopy(ambiguousFECLP, removeIndex+1, ambiguousFECLP, removeIndex, moved);
        }
    }
    
    
    public void ambiguousListTokenInContextError(int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
        if(ambiguousSizeLPICE == 0){
			ambiguousSizeLPICE = 1;
			ambiguousIndexLPICE = 0;
			ambiguousTokenLPICE = new String[ambiguousSizeLPICE];
			ambiguousCharsSystemIdEELPICE = new String[ambiguousSizeLPICE];
			ambiguousCharsLineNumberEELPICE = new int[ambiguousSizeLPICE];
			ambiguousCharsColumnNumberEELPICE = new int[ambiguousSizeLPICE];
			ambiguousPossibleDefinitionsLPICE = new CharsActiveTypeItem[ambiguousSizeLPICE][];
            
            ambiguousFECLPICE = new int[ambiguousSizeLPICE];
		}else if(++ambiguousIndexLPICE == ambiguousSizeLPICE){
			String[] increasedT = new String[++ambiguousSizeLPICE];
			System.arraycopy(ambiguousTokenLPICE, 0, increasedT, 0, ambiguousIndexLPICE);
			ambiguousTokenLPICE = increasedT;
						
			String[] increasedCSI = new String[ambiguousSizeLPICE];
			System.arraycopy(ambiguousCharsSystemIdEELPICE, 0, increasedCSI, 0, ambiguousIndexLPICE);
			ambiguousCharsSystemIdEELPICE = increasedCSI;
			
			int[] increasedCLN = new int[ambiguousSizeLPICE];
			System.arraycopy(ambiguousCharsLineNumberEELPICE, 0, increasedCLN, 0, ambiguousIndexLPICE);
			ambiguousCharsLineNumberEELPICE = increasedCLN;
			
			int[] increasedLPICEN = new int[ambiguousSizeLPICE];
			System.arraycopy(ambiguousCharsColumnNumberEELPICE, 0, increasedLPICEN, 0, ambiguousIndexLPICE);
			ambiguousCharsColumnNumberEELPICE = increasedLPICEN;
			
			CharsActiveTypeItem[][] increasedPD = new CharsActiveTypeItem[ambiguousSizeLPICE][];
			System.arraycopy(ambiguousPossibleDefinitionsLPICE, 0, increasedPD, 0, ambiguousIndexLPICE);
			ambiguousPossibleDefinitionsLPICE = increasedPD;	
            
            int[] increasedFECLPICEN = new int[ambiguousSizeLPICE];
			System.arraycopy(ambiguousFECLPICE, 0, increasedFECLPICEN, 0, ambiguousIndexLPICE);
			ambiguousFECLPICE = increasedFECLPICEN;
		}
		ambiguousTokenLPICE[ambiguousIndexLPICE] = token;
		ambiguousCharsSystemIdEELPICE[ambiguousIndexLPICE] = systemId;
		ambiguousCharsLineNumberEELPICE[ambiguousIndexLPICE] = lineNumber;
		ambiguousCharsColumnNumberEELPICE[ambiguousIndexLPICE] = columnNumber;
		ambiguousPossibleDefinitionsLPICE[ambiguousIndexLPICE] = possibleDefinitions;
        
        ambiguousFECLPICE[ambiguousIndexLPICE] = functionalEquivalenceCode;
    }
    public void clearAmbiguousListTokenInContextError(){
        errorTotalCount -= ambiguousSizeLPICE;
        ambiguousSizeLPICE = 0;
        ambiguousIndexLPICE = -1;
        ambiguousTokenLPICE = null;
        ambiguousCharsSystemIdEELPICE = null;
        ambiguousCharsLineNumberEELPICE = null;
        ambiguousCharsColumnNumberEELPICE = null;
        ambiguousPossibleDefinitionsLPICE = null;
        
        ambiguousFECLPICE = null;
    }
    public void clearAmbiguousListTokenInContextError(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousFECLPICE);
        int moved = ambiguousIndexLPICE - removeIndex;
        ambiguousIndexLPICE--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousTokenLPICE, removeIndex+1, ambiguousTokenLPICE, removeIndex, moved);
            System.arraycopy(ambiguousCharsSystemIdEELPICE, removeIndex+1, ambiguousCharsSystemIdEELPICE, removeIndex, moved);
            System.arraycopy(ambiguousCharsLineNumberEELPICE, removeIndex+1, ambiguousCharsLineNumberEELPICE, removeIndex, moved);
            System.arraycopy(ambiguousCharsColumnNumberEELPICE, removeIndex+1, ambiguousCharsColumnNumberEELPICE, removeIndex, moved);
            System.arraycopy(ambiguousPossibleDefinitionsLPICE, removeIndex+1, ambiguousPossibleDefinitionsLPICE, removeIndex, moved);
            System.arraycopy(ambiguousFECLPICE, removeIndex+1, ambiguousFECLPICE, removeIndex, moved);
        }
    }
    
    
    public void ambiguousListTokenInContextWarning(int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
        if(ambiguousSizeLPICW == 0){
			ambiguousSizeLPICW = 1;
			ambiguousIndexLPICW = 0;
			ambiguousTokenLPICW = new String[ambiguousSizeLPICW];
			ambiguousCharsSystemIdEELPICW = new String[ambiguousSizeLPICW];
			ambiguousCharsLineNumberEELPICW = new int[ambiguousSizeLPICW];
			ambiguousCharsColumnNumberEELPICW = new int[ambiguousSizeLPICW];
			ambiguousPossibleDefinitionsLPICW = new CharsActiveTypeItem[ambiguousSizeLPICW][];
            
            ambiguousFECLPICW = new int[ambiguousSizeLPICW];
		}else if(++ambiguousIndexLPICW == ambiguousSizeLPICW){
			String[] increasedT = new String[++ambiguousSizeLPICW];
			System.arraycopy(ambiguousTokenLPICW, 0, increasedT, 0, ambiguousIndexLPICW);
			ambiguousTokenLPICW = increasedT;
						
			String[] increasedCSI = new String[ambiguousSizeLPICW];
			System.arraycopy(ambiguousCharsSystemIdEELPICW, 0, increasedCSI, 0, ambiguousIndexLPICW);
			ambiguousCharsSystemIdEELPICW = increasedCSI;
			
			int[] increasedCLN = new int[ambiguousSizeLPICW];
			System.arraycopy(ambiguousCharsLineNumberEELPICW, 0, increasedCLN, 0, ambiguousIndexLPICW);
			ambiguousCharsLineNumberEELPICW = increasedCLN;
			
			int[] increasedLPICWN = new int[ambiguousSizeLPICW];
			System.arraycopy(ambiguousCharsColumnNumberEELPICW, 0, increasedLPICWN, 0, ambiguousIndexLPICW);
			ambiguousCharsColumnNumberEELPICW = increasedLPICWN;
			
			CharsActiveTypeItem[][] increasedPD = new CharsActiveTypeItem[ambiguousSizeLPICW][];
			System.arraycopy(ambiguousPossibleDefinitionsLPICW, 0, increasedPD, 0, ambiguousIndexLPICW);
			ambiguousPossibleDefinitionsLPICW = increasedPD;

            int[] increasedFEC = new int[ambiguousSizeLPICW];
			System.arraycopy(ambiguousFECLPICW, 0, increasedFEC, 0, ambiguousIndexLPICW);
			ambiguousFECLPICW = increasedFEC;			
		}
		ambiguousTokenLPICW[ambiguousIndexLPICW] = token;
		ambiguousCharsSystemIdEELPICW[ambiguousIndexLPICW] = systemId;
		ambiguousCharsLineNumberEELPICW[ambiguousIndexLPICW] = lineNumber;
		ambiguousCharsColumnNumberEELPICW[ambiguousIndexLPICW] = columnNumber;
		ambiguousPossibleDefinitionsLPICW[ambiguousIndexLPICW] = possibleDefinitions;
        
        ambiguousFECLPICW[ambiguousIndexLPICW] = functionalEquivalenceCode;
    }       
    public void clearAmbiguousListTokenInContextWarning(){
        errorTotalCount -= ambiguousSizeLPICW;
        ambiguousSizeLPICW = 0;
        ambiguousIndexLPICW = -1;
        ambiguousTokenLPICW = null;
        ambiguousCharsSystemIdEELPICW = null;
        ambiguousCharsLineNumberEELPICW = null;
        ambiguousCharsColumnNumberEELPICW = null;
        ambiguousPossibleDefinitionsLPICW = null;
    }
    public void clearAmbiguousListTokenInContextWarning(int messageId){
        int removeIndex = getRemoveIndex(messageId, ambiguousFECLPICW);
        int moved = ambiguousIndexLPICW - removeIndex;
        ambiguousIndexLPICW--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(ambiguousTokenLPICW, removeIndex+1, ambiguousTokenLPICW, removeIndex, moved);
            System.arraycopy(ambiguousCharsSystemIdEELPICW, removeIndex+1, ambiguousCharsSystemIdEELPICW, removeIndex, moved);
            System.arraycopy(ambiguousCharsLineNumberEELPICW, removeIndex+1, ambiguousCharsLineNumberEELPICW, removeIndex, moved);
            System.arraycopy(ambiguousCharsColumnNumberEELPICW, removeIndex+1, ambiguousCharsColumnNumberEELPICW, removeIndex, moved);
            System.arraycopy(ambiguousPossibleDefinitionsLPICW, removeIndex+1, ambiguousPossibleDefinitionsLPICW, removeIndex, moved);
            System.arraycopy(ambiguousFECLPICW, removeIndex+1, ambiguousFECLPICW, removeIndex, moved);
        }
    }
    
    
	public void missingCompositorContent(int functionalEquivalenceCode, 
                                Rule context, 
								String startSystemId, 
								int startLineNumber, 
								int startColumnNumber,								 
								APattern definition, 
								int expected, 
								int found){
        errorTotalCount++;
		if(missingCompositorContentSize == 0){
			missingCompositorContentSize = 1;
			missingCompositorContentIndex = 0;
			missingCompositorContentContext = new APattern[missingCompositorContentSize];
			missingCompositorContentStartSystemId = new String[missingCompositorContentSize];			
			missingCompositorContentStartLineNumber = new int[missingCompositorContentSize];
			missingCompositorContentStartColumnNumber = new int[missingCompositorContentSize];
			missingCompositorContentDefinition = new APattern[missingCompositorContentSize];
			missingCompositorContentExpected = new int[missingCompositorContentSize];
			missingCompositorContentFound = new int[missingCompositorContentSize];			
            
            missingCompositorContentFEC = new int[missingCompositorContentSize];
		}else if(++missingCompositorContentIndex == missingCompositorContentSize){
			APattern[] increasedEC = new APattern[++missingCompositorContentSize];
			System.arraycopy(missingCompositorContentContext, 0, increasedEC, 0, missingCompositorContentIndex);
			missingCompositorContentContext = increasedEC;
			
			String[] increasedSSI = new String[missingCompositorContentSize];
			System.arraycopy(missingCompositorContentStartSystemId, 0, increasedSSI, 0, missingCompositorContentIndex);
			missingCompositorContentStartSystemId = increasedSSI;
						
			int[] increasedSLN = new int[missingCompositorContentSize];
			System.arraycopy(missingCompositorContentStartLineNumber, 0, increasedSLN, 0, missingCompositorContentIndex);
			missingCompositorContentStartLineNumber = increasedSLN;
			
			int[] increasedSCN = new int[missingCompositorContentSize];
			System.arraycopy(missingCompositorContentStartColumnNumber, 0, increasedSCN, 0, missingCompositorContentIndex);
			missingCompositorContentStartColumnNumber = increasedSCN;
			
			APattern[] increasedED = new APattern[missingCompositorContentSize];
			System.arraycopy(missingCompositorContentDefinition, 0, increasedED, 0, missingCompositorContentIndex);
			missingCompositorContentDefinition = increasedED;
			
			int[] increasedE = new int[missingCompositorContentSize];
			System.arraycopy(missingCompositorContentExpected, 0, increasedE, 0, missingCompositorContentIndex);
			missingCompositorContentExpected = increasedE;
			
			int[] increasedF = new int[missingCompositorContentSize];
			System.arraycopy(missingCompositorContentFound, 0, increasedF, 0, missingCompositorContentIndex);
			missingCompositorContentFound = increasedF;			
		
            int[] increasedFEC = new int[missingCompositorContentSize];
			System.arraycopy(missingCompositorContentFEC, 0, increasedFEC, 0, missingCompositorContentIndex);
			missingCompositorContentFEC = increasedFEC;
		}
		missingCompositorContentContext[missingCompositorContentIndex] = context;
		missingCompositorContentStartSystemId[missingCompositorContentIndex] = startSystemId;
		missingCompositorContentStartLineNumber[missingCompositorContentIndex] = startLineNumber;
		missingCompositorContentStartColumnNumber[missingCompositorContentIndex] = startColumnNumber;
		missingCompositorContentDefinition[missingCompositorContentIndex] = definition;
		missingCompositorContentExpected[missingCompositorContentIndex] = expected;
		missingCompositorContentFound[missingCompositorContentIndex] = found;
        
        missingCompositorContentFEC[missingCompositorContentIndex] = functionalEquivalenceCode;
				
	}    
	public void clearMissingCompositorContent(){
        errorTotalCount -= missingCompositorContentSize;
        missingCompositorContentSize = 0;
        missingCompositorContentIndex = -1;
        missingCompositorContentContext = null;
        missingCompositorContentStartSystemId = null;			
        missingCompositorContentStartLineNumber = null;
        missingCompositorContentStartColumnNumber = null;
        missingCompositorContentDefinition = null;
        missingCompositorContentExpected = null;
        missingCompositorContentFound = null;
        
        missingCompositorContentFEC = null;
    }
    public void clearMissingCompositorContent(int messageId){
        int removeIndex = getRemoveIndex(messageId, missingCompositorContentFEC);
        int moved = missingCompositorContentIndex - removeIndex;
        missingCompositorContentIndex--;
        errorTotalCount--;
        if(moved > 0){
            System.arraycopy(missingCompositorContentContext, removeIndex+1, missingCompositorContentContext, removeIndex, moved);
            System.arraycopy(missingCompositorContentStartSystemId, removeIndex+1, missingCompositorContentStartSystemId, removeIndex, moved);
            System.arraycopy(missingCompositorContentStartLineNumber, removeIndex+1, missingCompositorContentStartLineNumber, removeIndex, moved);
            System.arraycopy(missingCompositorContentStartColumnNumber, removeIndex+1, missingCompositorContentStartColumnNumber, removeIndex, moved);
            System.arraycopy(missingCompositorContentDefinition, removeIndex+1, missingCompositorContentDefinition, removeIndex, moved);
            System.arraycopy(missingCompositorContentExpected, removeIndex+1, missingCompositorContentExpected, removeIndex, moved);
            System.arraycopy(missingCompositorContentFound, removeIndex+1, missingCompositorContentFound, removeIndex, moved);
            System.arraycopy(missingCompositorContentFEC, removeIndex+1, missingCompositorContentFEC, removeIndex, moved);
        }
    }
    
    /*
    public void clearUnknownElement(int messageId){
        int removeIndex = getRemoveIndex(messageId, unknownElementFEC);
        int moved = unknownElementIndex - removeIndex;
        if(moved > 0){            
            someIndex--;
            errorTotalCount--;
            System.arraycopy(something, removeIndex+1, something, removeIndex, moved);               
        }
    }*/
    
 
    int getRemoveIndex(int messageId, int[] messageCodes){
        int index = -1;
		for(int i = 0; i < messageCodes.length; i++){
			if(messageId == messageCodes[i]){
				index = i;
				break;
			}
		}
		if(index == -1){
            throw new IllegalArgumentException();
        }
        return index;
    }
    
    
    public  void conflict(int functionalEquivalenceCode, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
        errorTotalCount++;
        this.candidatesCount = candidatesCount;
        this.commonMessages = commonMessages;
        this.disqualified = disqualified;
        this.candidateMessages = candidateMessages;
    }
    
    public void clearConflict(){
        errorTotalCount--;
        candidatesCount = -1;
        commonMessages = null;
        disqualified = null;
        candidateMessages = null;
    }
	
    int getErrorMessageCount(){
        return errorTotalCount;
    }    
    
    void clearLastMessage(int errorId){		
        if(errorId == UNKNOWN_ELEMENT){
            if(unknownElementIndex < 0) throw new IllegalArgumentException();
            unknownElementIndex--;
            errorTotalCount--;            
        }else if(errorId == UNEXPECTED_ELEMENT){        
            if(unexpectedElementIndex < 0) throw new IllegalArgumentException();
            unexpectedElementIndex--;
            errorTotalCount--;
        }else if(errorId == UNEXPECTED_AMBIGUOUS_ELEMENT){
            if(unexpectedAmbiguousElementIndex < 0) throw new IllegalArgumentException();
            unexpectedAmbiguousElementIndex--;
            errorTotalCount--;
        }else if(errorId == UNKNOWN_ATTRIBUTE){
            if(unknownAttributeIndex < 0) throw new IllegalArgumentException();
            unknownAttributeIndex--;
            errorTotalCount--;
        }else if(errorId == UNEXPECTED_ATTRIBUTE){
            if(unexpectedAttributeIndex < 0) throw new IllegalArgumentException();
            unexpectedAttributeIndex--;
            errorTotalCount--;
        }else if(errorId == UNEXPECTED_AMBIGUOUS_ATTRIBUTE){
            if(unexpectedAmbiguousAttributeIndex < 0) throw new IllegalArgumentException();
            unexpectedAmbiguousAttributeIndex--;
            errorTotalCount--;
        }else if(errorId == MISPLACED_ELEMENT){
            if(misplacedIndex < 0) throw new IllegalArgumentException();
            misplacedIndex--;
            errorTotalCount--;
        }else if(errorId == EXCESSIVE_CONTENT){
            if(excessiveIndex < 0) throw new IllegalArgumentException();
            excessiveIndex--;
            errorTotalCount--;
        }else if(errorId == AMBIGUOUS_ELEMENT_CONTENT_ERROR){
            if(ambiguousElementIndexEE < 0) throw new IllegalArgumentException();
            ambiguousElementIndexEE--;
            errorTotalCount--;
        }else if(errorId == AMBIGUOUS_ATTRIBUTE_CONTENT_ERROR){
            if(ambiguousAttributeIndexEE < 0) throw new IllegalArgumentException();
            ambiguousAttributeIndexEE--;
            errorTotalCount--;
        }else if(errorId == AMBIGUOUS_CHARS_CONTENT_ERROR){
            if(ambiguousCharsIndexEE < 0) throw new IllegalArgumentException();
            ambiguousCharsIndexEE--;
            errorTotalCount--;
        }else if(errorId == MISSING_CONTENT){
            if(missingIndex < 0) throw new IllegalArgumentException();
            missingIndex--;	  
            errorTotalCount--;
        }else if(errorId == ILLEGAL_CONTENT){
            if(illegalIndex < 0) throw new IllegalArgumentException();
            illegalIndex--;
            errorTotalCount--;
        }else if(errorId == UNDETERMINED_BY_CONTENT){
            if(undeterminedQName == null) throw new IllegalArgumentException();
            undeterminedQName = null;
            undeterminedCandidateMessages = null;
            errorTotalCount--;
        }else if(errorId == CHARACTER_CONTENT_DATATYPE_ERROR){
            if(datatypeIndexCC < 0) throw new IllegalArgumentException();
            datatypeIndexCC--;
            errorTotalCount--;
        }else if(errorId == ATTRIBUTE_VALUE_DATATYPE_ERROR){
            if(datatypeIndexAV < 0) throw new IllegalArgumentException();
            datatypeIndexAV--;
            errorTotalCount--;
        }else if(errorId == CHARACTER_CONTENT_VALUE_ERROR){
            if(valueIndexCC < 0) throw new IllegalArgumentException();
            valueIndexCC--;
            errorTotalCount--;
        }else if(errorId == ATTRIBUTE_VALUE_VALUE_ERROR){
            if(valueIndexAV < 0) throw new IllegalArgumentException();
            valueIndexAV--;
            errorTotalCount--;
        }else if(errorId == CHARACTER_CONTENT_EXCEPTED_ERROR){
            if(exceptIndexCC < 0) throw new IllegalArgumentException();
            exceptIndexCC--;
            errorTotalCount--;
        }else if(errorId == ATTRIBUTE_VALUE_EXCEPTED_ERROR){
            if(exceptIndexAV < 0) throw new IllegalArgumentException();
            exceptIndexAV--;
            errorTotalCount--;
        }else if(errorId == UNEXPECTED_CHARACTER_CONTENT){
            if(unexpectedIndexCC < 0) throw new IllegalArgumentException();
            unexpectedIndexCC--;
            errorTotalCount--;
        }else if(errorId == UNEXPECTED_ATTRIBUTE_VALUE){
            if(unexpectedIndexAV < 0) throw new IllegalArgumentException();
            unexpectedIndexAV--;
            errorTotalCount--;
        }else if(errorId == AMBIGUOUS_CHARACTER_CONTENT){
            if(ambiguousIndexCC < 0) throw new IllegalArgumentException();
            ambiguousIndexCC--;
            errorTotalCount--;
        }else if(errorId == AMBIGUOUS_ATTRIBUTE_VALUE){
            if(ambiguousIndexAV < 0) throw new IllegalArgumentException();
            ambiguousIndexAV--;
            errorTotalCount--;
        }else if(errorId == LIST_TOKEN_DATATYPE_ERROR){
            if(datatypeIndexLP < 0) throw new IllegalArgumentException();
            datatypeIndexLP--;
            errorTotalCount--;
        }else if(errorId == LIST_TOKEN_VALUE_ERROR){
            if(valueIndexLP < 0) throw new IllegalArgumentException();
            valueIndexLP--;
            errorTotalCount--;
        }else if(errorId == LIST_TOKEN_EXCEPTED_ERROR){
            if(exceptIndexLP < 0) throw new IllegalArgumentException();
            exceptIndexLP--;
            errorTotalCount--;
        }else if(errorId == AMBIGUOUS_LIST_TOKEN){
            if(ambiguousIndexLP < 0) throw new IllegalArgumentException();
            ambiguousIndexLP--;
            errorTotalCount--;
        }else if(errorId == AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_ERROR){
            if(ambiguousIndexLPICE < 0) throw new IllegalArgumentException();
            ambiguousIndexLPICE--;
            errorTotalCount--;
        }else if(errorId == MISSING_COMPOSITOR_CONTENT){
            if(missingCompositorContentIndex < 0) throw new IllegalArgumentException();
            missingCompositorContentIndex--;
            errorTotalCount--;
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
            clearMisplacedElement(messageId);
        }else if(errorId == EXCESSIVE_CONTENT){
            if(excessiveIndex < 0) throw new IllegalArgumentException();
            clearExcessiveContent(messageId);
        }else if(errorId == AMBIGUOUS_ELEMENT_CONTENT_ERROR){
            if(ambiguousElementIndexEE < 0) throw new IllegalArgumentException();
            clearAmbiguousElementContentError(messageId);
        }else if(errorId == AMBIGUOUS_ATTRIBUTE_CONTENT_ERROR){
            if(ambiguousAttributeIndexEE < 0) throw new IllegalArgumentException();
            clearAmbiguousAttributeContentError(messageId);
        }else if(errorId == AMBIGUOUS_CHARS_CONTENT_ERROR){
            if(ambiguousCharsIndexEE < 0) throw new IllegalArgumentException();
            clearAmbiguousCharsContentError(messageId);
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
            errorTotalCount--;
        }else if(errorId == CHARACTER_CONTENT_DATATYPE_ERROR){
            if(datatypeIndexCC < 0) throw new IllegalArgumentException();
            clearCharacterContentDatatypeError(messageId);
        }else if(errorId == ATTRIBUTE_VALUE_DATATYPE_ERROR){
            if(datatypeIndexAV < 0) throw new IllegalArgumentException();
            clearAttributeValueDatatypeError(messageId);
        }else if(errorId == CHARACTER_CONTENT_VALUE_ERROR){
            if(valueIndexCC < 0) throw new IllegalArgumentException();
            clearCharacterContentValueError(messageId);
        }else if(errorId == ATTRIBUTE_VALUE_VALUE_ERROR){
            if(valueIndexAV < 0) throw new IllegalArgumentException();
            clearAttributeValueValueError(messageId);
        }else if(errorId == CHARACTER_CONTENT_EXCEPTED_ERROR){
            if(exceptIndexCC < 0) throw new IllegalArgumentException();
            clearCharacterContentExceptedError(messageId);
        }else if(errorId == ATTRIBUTE_VALUE_EXCEPTED_ERROR){
            if(exceptIndexAV < 0) throw new IllegalArgumentException();
            clearAttributeValueExceptedError(messageId);
        }else if(errorId == UNEXPECTED_CHARACTER_CONTENT){
            if(unexpectedIndexCC < 0) throw new IllegalArgumentException();
            clearUnexpectedCharacterContent(messageId);
        }else if(errorId == UNEXPECTED_ATTRIBUTE_VALUE){
            if(unexpectedIndexAV < 0) throw new IllegalArgumentException();
            clearUnexpectedAttributeValue(messageId);
        }else if(errorId == AMBIGUOUS_CHARACTER_CONTENT){
            if(ambiguousIndexCC < 0) throw new IllegalArgumentException();
            clearAmbiguousCharacterContent(messageId);
        }else if(errorId == AMBIGUOUS_ATTRIBUTE_VALUE){
            if(ambiguousIndexAV < 0) throw new IllegalArgumentException();
            clearAmbiguousAttributeValue(messageId);
        }else if(errorId == LIST_TOKEN_DATATYPE_ERROR){
            if(datatypeIndexLP < 0) throw new IllegalArgumentException();
            clearListTokenDatatypeError(messageId);
        }else if(errorId == LIST_TOKEN_VALUE_ERROR){
            if(valueIndexLP < 0) throw new IllegalArgumentException();
            clearListTokenValueError(messageId);
        }else if(errorId == LIST_TOKEN_EXCEPTED_ERROR){
            if(exceptIndexLP < 0) throw new IllegalArgumentException();
            clearListTokenExceptedError(messageId);
        }else if(errorId == AMBIGUOUS_LIST_TOKEN){
            if(ambiguousIndexLP < 0) throw new IllegalArgumentException();
            clearAmbiguousListToken(messageId);
        }else if(errorId == AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_ERROR){
            if(ambiguousIndexLPICE < 0) throw new IllegalArgumentException();
            clearAmbiguousListTokenInContextError(messageId);
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
        
    
    void clearWarningMessage(int warningId, int messageId){        
        if(warningId == AMBIGUOUS_ELEMENT_CONTENT_WARNING){
            if(ambiguousElementIndexWW < 0) throw new IllegalArgumentException();
            clearAmbiguousElementContentWarning(messageId);
        }else if(warningId == AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING){
            if(ambiguousAttributeIndexWW < 0) throw new IllegalArgumentException();
            clearAmbiguousAttributeContentWarning(messageId);
        }else if(warningId == AMBIGUOUS_CHARS_CONTENT_WARNING){
            if(ambiguousCharsIndexWW < 0) throw new IllegalArgumentException();
            clearAmbiguousCharsContentWarning(messageId);
        }else if(warningId == AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING){            
            if(ambiguousIndexLPICW < 0) throw new IllegalArgumentException();
            clearAmbiguousListTokenInContextWarning(messageId);
        }else{
            throw new IllegalArgumentException();
        }
    
    }
    
    public void clear(){
        // TODO check sizes to only clear when full
        // and refactor the creation of new instances in the ErrorHandlers
        clearUnknownElement();
		clearUnexpectedElement();
		clearUnexpectedAmbiguousElement();
		clearUnknownAttribute();
        clearUnexpectedAttribute();
        clearUnexpectedAmbiguousAttribute();        
        clearMisplacedElement();
        clearExcessiveContent();
        clearAmbiguousElementContentError();
        clearAmbiguousAttributeContentError();
        clearAmbiguousCharsContentError();
        clearAmbiguousElementContentWarning();
        clearAmbiguousAttributeContentWarning();
        clearAmbiguousCharsContentWarning();
        clearMissingContent();
        clearIllegalContent();
        clearUndeterminedByContent();
        clearCharacterContentDatatypeError();
        clearAttributeValueDatatypeError();
        clearCharacterContentValueError();
        clearAttributeValueValueError();
        clearCharacterContentExceptedError();
        clearAttributeValueExceptedError();
        clearUnexpectedCharacterContent();
        clearUnexpectedAttributeValue();
        clearAmbiguousCharacterContent();
        clearAmbiguousAttributeValue();
        clearListTokenDatatypeError();
        clearListTokenValueError();
        clearListTokenExceptedError();
        clearAmbiguousListToken();
        clearAmbiguousListTokenInContextError();
        clearAmbiguousListTokenInContextWarning();
        clearMissingCompositorContent();
        clearConflict();
        
        errorTotalCount = 0;
        
        parent = null;
    
        qName = null;
        definition = null;
        systemId = null;
        lineNumber = -1;
        columnNumber = -1;
        conflictResolutionId = UNRESOLVED;
    }   
}
