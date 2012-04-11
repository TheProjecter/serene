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

import serene.validation.handlers.content.util.ActiveInputDescriptor;

import sereneWrite.MessageWriter;

public class TemporaryMessageStorage  implements ErrorCatcher{	
    
    ActiveInputDescriptor activeInputDescriptor;
        
    int initialSize = 5;
    int increaseSizeAmount = 5;
    
	// {1}
	String undeterminedQName;
	String undeterminedCandidateMessages;
		
	// {2}
	/*String[] unknownElementQName;
	String[] unknownElementSystemId;
	int[] unknownElementLineNumber;
	int[] unknownElementColumnNumber;
	int unknownElementIndex;
	int unknownElementSize;	*/
	int[] unknownElementInputRecordIndex;
	int unknownElementIndex;
	
	// {3}
	/*String[] unexpectedElementQName;
	SimplifiedComponent[] unexpectedElementDefinition;
	String[] unexpectedElementSystemId;
	int[] unexpectedElementLineNumber;
	int[] unexpectedElementColumnNumber;
	int unexpectedElementIndex;
	int unexpectedElementSize;*/
	SimplifiedComponent[] unexpectedElementDefinition;
	int[] unexpectedElementInputRecordIndex;
	int unexpectedElementIndex;
	
	// {4}
	/*String[] unexpectedAmbiguousElementQName;
	SimplifiedComponent[][] unexpectedAmbiguousElementDefinition;
	String[] unexpectedAmbiguousElementSystemId;
	int[] unexpectedAmbiguousElementLineNumber;
	int[] unexpectedAmbiguousElementColumnNumber;
	int unexpectedAmbiguousElementIndex;
	int unexpectedAmbiguousElementSize;*/
	SimplifiedComponent[][] unexpectedAmbiguousElementDefinition;
	int[] unexpectedAmbiguousElementInputRecordIndex;
	int unexpectedAmbiguousElementIndex;
	
	// {5}
	/*String[] unknownAttributeQName;
	String[] unknownAttributeSystemId;
	int[] unknownAttributeLineNumber;
	int[] unknownAttributeColumnNumber;
	int unknownAttributeIndex;
	int unknownAttributeSize;	*/
	int[] unknownAttributeInputRecordIndex;
	int unknownAttributeIndex;
	
	// {6}
	/*String[] unexpectedAttributeQName;
	SimplifiedComponent[] unexpectedAttributeDefinition;
	String[] unexpectedAttributeSystemId;
	int[] unexpectedAttributeLineNumber;
	int[] unexpectedAttributeColumnNumber;
	int unexpectedAttributeIndex;
	int unexpectedAttributeSize;*/
	SimplifiedComponent[] unexpectedAttributeDefinition;
	int[] unexpectedAttributeInputRecordIndex;
	int unexpectedAttributeIndex;
	
	// {7}
	/*String[] unexpectedAmbiguousAttributeQName;
	SimplifiedComponent[][] unexpectedAmbiguousAttributeDefinition;
	String[] unexpectedAmbiguousAttributeSystemId;
	int[] unexpectedAmbiguousAttributeLineNumber;
	int[] unexpectedAmbiguousAttributeColumnNumber;
	int unexpectedAmbiguousAttributeIndex;
	int unexpectedAmbiguousAttributeSize;*/
	SimplifiedComponent[][] unexpectedAmbiguousAttributeDefinition;
	int[] unexpectedAmbiguousAttributeInputRecordIndex;
	int unexpectedAmbiguousAttributeIndex;
	
	
	// {8}
	/*APattern[] misplacedContext;
	String[] misplacedStartSystemId;
	int[] misplacedStartLineNumber;
	int[] misplacedStartColumnNumber;
	APattern[][] misplacedDefinition;
	int[][][] misplacedItemId;
	String[][][] misplacedQName;	
	String[][][] misplacedSystemId;
	int[][][] misplacedLineNumber;
	int[][][] misplacedColumnNumber;
	int misplacedIndex;
	int misplacedSize;*/
	APattern[] misplacedContext;
	int[] misplacedStartInputRecordIndex;
	APattern[][] misplacedDefinition;
	int[][][] misplacedInputRecordIndex;
	int misplacedIndex;

	// {9}
	/*Rule[] excessiveContext;
	String[] excessiveStartSystemId;
	int[] excessiveStartLineNumber;
	int[] excessiveStartColumnNumber;
	APattern[] excessiveDefinition;
	int[][] excessiveItemId;
	String[][] excessiveQName;
	String[][] excessiveSystemId;
	int[][] excessiveLineNumber;
	int[][] excessiveColumnNumber;
	int excessiveIndex;
	int excessiveSize;*/
	Rule[] excessiveContext;
	int[] excessiveStartInputRecordIndex;
	APattern[] excessiveDefinition;
	int[][] excessiveInputRecordIndex;
	int excessiveIndex;
	
	
	// {10}
	/*Rule[] missingContext;
	String[] missingStartSystemId;
	int[] missingStartLineNumber;
	int[] missingStartColumnNumber;
	APattern[] missingDefinition;
	int[] missingExpected;
	int[] missingFound;
	String[][] missingQName;
	String[][] missingSystemId;
	int[][] missingLineNumber;
	int[][] missingColumnNumber;
	int missingIndex;
	int missingSize;*/
	Rule[] missingContext;
	int[] missingStartInputRecordIndex;
	APattern[] missingDefinition;
	int[] missingExpected;
	int[] missingFound;
	int[][] missingInputRecordIndex;
	int missingIndex;
	
	
	// {11}
	/*Rule[] illegalContext;
	int[] illegalItemId;
	String[] illegalQName;
	String[] illegalStartSystemId;
	int[] illegalStartLineNumber;
	int[] illegalStartColumnNumber;	
	int illegalIndex;
	int illegalSize;*/
	Rule[] illegalContext;
	int[] illegalStartInputRecordIndex;
	int illegalIndex;
	
	
	// {12 A}
	/*String[] unresolvedAmbiguousElementQNameEE;
	String[] unresolvedAmbiguousElementSystemIdEE;
	int[] unresolvedAmbiguousElementLineNumberEE;
	int[] unresolvedAmbiguousElementColumnNumberEE;
	AElement[][] unresolvedAmbiguousElementDefinitionEE;
	int unresolvedAmbiguousElementIndexEE;
	int unresolvedAmbiguousElementSizeEE;*/
	int[] unresolvedAmbiguousElementInputRecordIndexEE;
	AElement[][] unresolvedAmbiguousElementDefinitionEE;
	int unresolvedAmbiguousElementIndexEE;
	
	
	// {12 U}
	/*String[] unresolvedUnresolvedElementQNameEE;
	String[] unresolvedUnresolvedElementSystemIdEE;
	int[] unresolvedUnresolvedElementLineNumberEE;
	int[] unresolvedUnresolvedElementColumnNumberEE;
	AElement[][] unresolvedUnresolvedElementDefinitionEE;
	int unresolvedUnresolvedElementIndexEE;
	int unresolvedUnresolvedElementSizeEE;*/
	int[] unresolvedUnresolvedElementInputRecordIndexEE;
	AElement[][] unresolvedUnresolvedElementDefinitionEE;
	int unresolvedUnresolvedElementIndexEE;

	// {13}
	/*String[] unresolvedAttributeQNameEE;
	String[] unresolvedAttributeSystemIdEE;
	int[] unresolvedAttributeLineNumberEE;
	int[] unresolvedAttributeColumnNumberEE;
	AAttribute[][] unresolvedAttributeDefinitionEE;
	int unresolvedAttributeIndexEE;
	int unresolvedAttributeSizeEE;*/
	int[] unresolvedAttributeInputRecordIndexEE;
	AAttribute[][] unresolvedAttributeDefinitionEE;
	int unresolvedAttributeIndexEE;
	

	// {14}
	
	
	// {w1 U}
	/*String[] ambiguousUnresolvedElementQNameWW;
	String[] ambiguousUnresolvedElementSystemIdWW;
	int[] ambiguousUnresolvedElementLineNumberWW;
	int[] ambiguousUnresolvedElementColumnNumberWW;
	AElement[][] ambiguousUnresolvedElementDefinitionWW;
	int ambiguousUnresolvedElementIndexWW;
	int ambiguousUnresolvedElementSizeWW;*/
	int[] ambiguousUnresolvedElementInputRecordIndexWW;
	AElement[][] ambiguousUnresolvedElementDefinitionWW;
	int ambiguousUnresolvedElementIndexWW;
	
	
	// {w1 A}
	/*String[] ambiguousAmbiguousElementQNameWW;
	String[] ambiguousAmbiguousElementSystemIdWW;
	int[] ambiguousAmbiguousElementLineNumberWW;
	int[] ambiguousAmbiguousElementColumnNumberWW;
	AElement[][] ambiguousAmbiguousElementDefinitionWW;
	int ambiguousAmbiguousElementIndexWW;
	int ambiguousAmbiguousElementSizeWW;*/
	int[] ambiguousAmbiguousElementInputRecordIndexWW;
	AElement[][] ambiguousAmbiguousElementDefinitionWW;
	int ambiguousAmbiguousElementIndexWW;
	
	
	
	// {w2}
	/*String[] ambiguousAttributeQNameWW;
	String[] ambiguousAttributeSystemIdWW;
	int[] ambiguousAttributeLineNumberWW;
	int[] ambiguousAttributeColumnNumberWW;
	AAttribute[][] ambiguousAttributeDefinitionWW;
	int ambiguousAttributeIndexWW;
	int ambiguousAttributeSizeWW;*/
	int[] ambiguousAttributeInputRecordIndexWW;
	AAttribute[][] ambiguousAttributeDefinitionWW;
	int ambiguousAttributeIndexWW;
	

	// {w3}
	/*String[] ambiguousCharsSystemIdWW;
	int[] ambiguousCharsLineNumberWW;
	int[] ambiguousCharsColumnNumberWW;
	CharsActiveTypeItem[][] ambiguousCharsDefinitionWW;
	int ambiguousCharsIndexWW;
	int ambiguousCharsSizeWW;*/
	int[] ambiguousCharsInputRecordIndexWW;
	CharsActiveTypeItem[][] ambiguousCharsDefinitionWW;
	int ambiguousCharsIndexWW;
	
	
	// {w4}
	/*String[] ambiguousAVAttributeQNameWW;
	String[] ambiguousAVSystemIdWW;
	int[] ambiguousAVLineNumberWW;
	int[] ambiguousAVColumnNumberWW;
	CharsActiveTypeItem[][] ambiguousAVDefinitionWW;
	int ambiguousAVIndexWW;
	int ambiguousAVSizeWW;*/
	int[] ambiguousAVInputRecordIndexWW;
	CharsActiveTypeItem[][] ambiguousAVDefinitionWW;
	int ambiguousAVIndexWW;
		
	
	// {15}
	/*String datatypeElementQNameCC[];
	String datatypeCharsSystemIdCC[];//CC character content
	int datatypeCharsLineNumberCC[];
	int datatypeCharsColumnNumberCC[];
	DatatypedActiveTypeItem datatypeCharsDefinitionCC[];
	String datatypeErrorMessageCC[];
	int datatypeIndexCC;
	int datatypeSizeCC;*/
	int[] datatypeCharsInputRecordIndex;
	DatatypedActiveTypeItem[] datatypeCharsDefinition;
	String datatypeCharsErrorMessage[];
	int datatypeCharsIndex;
	
	
	// {16}
	/*String datatypeAttributeQNameAV[];
	String datatypeCharsSystemIdAV[];//AV attribute value
	int datatypeCharsLineNumberAV[];
	int datatypeCharsColumnNumberAV[];
	DatatypedActiveTypeItem datatypeCharsDefinitionAV[];
	String datatypeErrorMessageAV[];
	int datatypeIndexAV;
	int datatypeSizeAV;*/
	int[] datatypeAVInputRecordIndex;
	DatatypedActiveTypeItem[] datatypeAVDefinition;
	String datatypeAVErrorMessage[];
	int datatypeAVIndex;
   
	
	// {17}
	/*String valueCharsSystemIdCC[];//CC character content
	int valueCharsLineNumberCC[];
	int valueCharsColumnNumberCC[];
	AValue valueCharsDefinitionCC[];
	int valueIndexCC;
	int valueSizeCC;*/
	int[] valueCharsInputRecordIndex;
	AValue[] valueCharsDefinition;
	int valueCharsIndex;
	
	// {18}
	/*String valueAttributeQNameAV[];
	String valueCharsSystemIdAV[];//AV attribute value
	int valueCharsLineNumberAV[];
	int valueCharsColumnNumberAV[];
	AValue valueCharsDefinitionAV[];
	int valueIndexAV;
	int valueSizeAV;*/
	int[] valueAVInputRecordIndex;
	AValue[] valueAVDefinition;
	int valueAVIndex;
	
	// {19}
	/*String exceptElementQNameCC[];
	String exceptCharsSystemIdCC[];//CC character content
	int exceptCharsLineNumberCC[];
	int exceptCharsColumnNumberCC[];
	AData exceptCharsDefinitionCC[];
	int exceptIndexCC;
	int exceptSizeCC;*/
	int[] exceptCharsInputRecordIndex;
	AData[] exceptCharsDefinition;
	int exceptCharsIndex;
	
	// {20}
	/*String exceptAttributeQNameAV[];
	String exceptCharsSystemIdAV[];//AV attribute except
	int exceptCharsLineNumberAV[];
	int exceptCharsColumnNumberAV[];
	AData exceptCharsDefinitionAV[];
	int exceptIndexAV;
	int exceptSizeAV;*/
	int[] exceptAVInputRecordIndex;
	AData[] exceptAVDefinition;
	int exceptAVIndex;
	
	// {21}
	/*String unexpectedCharsSystemIdCC[];//CC character content
	int unexpectedCharsLineNumberCC[];
	int unexpectedCharsColumnNumberCC[];
	AElement unexpectedContextDefinitionCC[];
	int unexpectedIndexCC;
	int unexpectedSizeCC;*/
	int[] unexpectedCharsInputRecordIndex;
	AElement[] unexpectedCharsDefinition;
	int unexpectedCharsIndex;
	
	// {22}
	/*String unexpectedCharsSystemIdAV[];//AV attribute unexpected
	int unexpectedCharsLineNumberAV[];
	int unexpectedCharsColumnNumberAV[];
	AAttribute unexpectedContextDefinitionAV[];
	int unexpectedIndexAV;
	int unexpectedSizeAV;*/
	int[] unexpectedAVInputRecordIndex;
	AAttribute[] unexpectedAVDefinition;
	int unexpectedAVIndex;
	
	
	// {23}
	/*String unresolvedCharsSystemIdEECC[];//CC character content
	int unresolvedCharsLineNumberEECC[];
	int unresolvedCharsColumnNumberEECC[];
	CharsActiveTypeItem unresolvedPossibleDefinitionsCC[][];
	int unresolvedIndexCC;
	int unresolvedSizeCC;*/
	int[] unresolvedCharsInputRecordIndexEE;
	CharsActiveTypeItem[][] unresolvedCharsDefinitionEE;
	int unresolvedCharsIndexEE;
	
	
	// {24}
	/*String unresolvedAttributeQNameEEAV[];
	String unresolvedCharsSystemIdEEAV[];//AV attribute unresolved
	int unresolvedCharsLineNumberEEAV[];
	int unresolvedCharsColumnNumberEEAV[];
	CharsActiveTypeItem unresolvedPossibleDefinitionsAV[][];
	int unresolvedIndexAV;
	int unresolvedSizeAV;*/
	int[] unresolvedAVInputRecordIndexEE;
	CharsActiveTypeItem[][] unresolvedAVDefinitionEE;
	int unresolvedAVIndexEE;
	
	
	// {25}
	/*String datatypeTokenLP[];//LP list pattern
	String datatypeCharsSystemIdLP[];
	int datatypeCharsLineNumberLP[];
	int datatypeCharsColumnNumberLP[];
	DatatypedActiveTypeItem datatypeCharsDefinitionLP[];
	String datatypeErrorMessageLP[];
	int datatypeIndexLP;
	int datatypeSizeLP;*/
	int[] datatypeTokenInputRecordIndex;
	DatatypedActiveTypeItem[] datatypeTokenDefinition;
	String datatypeTokenErrorMessage[];
	int datatypeTokenIndex;
	
    	
	// {26}
	/*String valueTokenLP[];//LP list pattern
	String valueCharsSystemIdLP[];
	int valueCharsLineNumberLP[];
	int valueCharsColumnNumberLP[];
	AValue valueCharsDefinitionLP[];
	int valueIndexLP;
	int valueSizeLP;*/
	int[] valueTokenInputRecordIndex;
	AValue[] valueTokenDefinition;
	int valueTokenIndex;
	
	
	// {27}
	/*String exceptTokenLP[];//LP list pattern
	String exceptCharsSystemIdLP[];
	int exceptCharsLineNumberLP[];
	int exceptCharsColumnNumberLP[];
	AData exceptCharsDefinitionLP[];
	int exceptIndexLP;
	int exceptSizeLP;*/
	int[] exceptTokenInputRecordIndex;
	AData[] exceptTokenDefinition;
	int exceptTokenIndex;
	
	
	// {28}
	
    // {28_1}
	/*String unresolvedTokenLPICE[];//LPICE list pattern in context validation error
	String unresolvedCharsSystemIdEELPICE[];
	int unresolvedCharsLineNumberEELPICE[];
	int unresolvedCharsColumnNumberEELPICE[];
	CharsActiveTypeItem unresolvedPossibleDefinitionsLPICE[][];
	int unresolvedIndexLPICE;
	int unresolvedSizeLPICE;*/
	int[] unresolvedTokenInputRecordIndexLPICE;
    CharsActiveTypeItem unresolvedTokenDefinitionLPICE[][];
	int unresolvedTokenIndexLPICE;
    
    
    // {28_2}
	/*String ambiguousTokenLPICW[];//LPICW list pattern in context validation warning
	String ambiguousCharsSystemIdEELPICW[];
	int ambiguousCharsLineNumberEELPICW[];
	int ambiguousCharsColumnNumberEELPICW[];
	CharsActiveTypeItem ambiguousPossibleDefinitionsLPICW[][];
	int ambiguousIndexLPICW;
	int ambiguousSizeLPICW;*/
	int[] ambiguousTokenInputRecordIndexLPICW;
    CharsActiveTypeItem ambiguousTokenDefinitionLPICW[][];
	int ambiguousTokenIndexLPICW;
    
	
	// {29}
	/*Rule[] missingCompositorContentContext;
	String[] missingCompositorContentStartSystemId;
	int[] missingCompositorContentStartLineNumber;
	int[] missingCompositorContentStartColumnNumber;
	APattern[] missingCompositorContentDefinition;
	int[] missingCompositorContentExpected;
	int[] missingCompositorContentFound;
	int missingCompositorContentIndex;
	int missingCompositorContentSize;*/
	Rule[] missingCompositorContentContext;
	int[] missingCompositorContentStartInputRecordIndex;
	APattern[] missingCompositorContentDefinition;
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
    
    MessageWriter debugWriter;
    
    boolean isDiscarded;
    boolean isClear;
    
	public TemporaryMessageStorage(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
				
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
        
		/*if(unknownElementSize == 0){
			unknownElementSize = 1;
			unknownElementIndex = 0;	
			unknownElementQName = new String[unknownElementSize];			
			unknownElementSystemId = new String[unknownElementSize];			
			unknownElementLineNumber = new int[unknownElementSize];
			unknownElementColumnNumber = new int[unknownElementSize];			
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
		}
		unknownElementQName[unknownElementIndex] = qName;
		unknownElementSystemId[unknownElementIndex] = systemId;
		unknownElementLineNumber[unknownElementIndex] = lineNumber;
		unknownElementColumnNumber[unknownElementIndex] = columnNumber;*/
		
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
        
		/*if(unexpectedElementSize == 0){
			unexpectedElementSize = 1;
			unexpectedElementIndex = 0;	
			unexpectedElementQName = new String[unexpectedElementSize];
			unexpectedElementDefinition = new SimplifiedComponent[unexpectedElementSize];
			unexpectedElementSystemId = new String[unexpectedElementSize];			
			unexpectedElementLineNumber = new int[unexpectedElementSize];
			unexpectedElementColumnNumber = new int[unexpectedElementSize];			
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
		}
		unexpectedElementQName[unexpectedElementIndex] = qName;
		unexpectedElementDefinition[unexpectedElementIndex] = definition;
		unexpectedElementSystemId[unexpectedElementIndex] = systemId;
		unexpectedElementLineNumber[unexpectedElementIndex] = lineNumber;
		unexpectedElementColumnNumber[unexpectedElementIndex] = columnNumber;*/
		
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
        
		/*if(unexpectedAmbiguousElementSize == 0){
			unexpectedAmbiguousElementSize = 1;
			unexpectedAmbiguousElementIndex = 0;	
			unexpectedAmbiguousElementQName = new String[unexpectedAmbiguousElementSize];
			unexpectedAmbiguousElementDefinition = new SimplifiedComponent[unexpectedAmbiguousElementSize][];
			unexpectedAmbiguousElementSystemId = new String[unexpectedAmbiguousElementSize];			
			unexpectedAmbiguousElementLineNumber = new int[unexpectedAmbiguousElementSize];
			unexpectedAmbiguousElementColumnNumber = new int[unexpectedAmbiguousElementSize];			
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
		}
		unexpectedAmbiguousElementQName[unexpectedAmbiguousElementIndex] = qName;
		unexpectedAmbiguousElementDefinition[unexpectedAmbiguousElementIndex] = possibleDefinitions;
		unexpectedAmbiguousElementSystemId[unexpectedAmbiguousElementIndex] = systemId;
		unexpectedAmbiguousElementLineNumber[unexpectedAmbiguousElementIndex] = lineNumber;
		unexpectedAmbiguousElementColumnNumber[unexpectedAmbiguousElementIndex] = columnNumber;*/
		
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
        
		/*if(unknownAttributeSize == 0){
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
		}
		unknownAttributeQName[unknownAttributeIndex] = qName;
		unknownAttributeSystemId[unknownAttributeIndex] = systemId;
		unknownAttributeLineNumber[unknownAttributeIndex] = lineNumber;
		unknownAttributeColumnNumber[unknownAttributeIndex] = columnNumber;*/
		
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
        
		/*if(unexpectedAttributeSize == 0){
			unexpectedAttributeSize = 1;
			unexpectedAttributeIndex = 0;	
			unexpectedAttributeQName = new String[unexpectedAttributeSize];
			unexpectedAttributeDefinition = new SimplifiedComponent[unexpectedAttributeSize];
			unexpectedAttributeSystemId = new String[unexpectedAttributeSize];			
			unexpectedAttributeLineNumber = new int[unexpectedAttributeSize];
			unexpectedAttributeColumnNumber = new int[unexpectedAttributeSize];			
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
		}
		unexpectedAttributeQName[unexpectedAttributeIndex] = qName;
		unexpectedAttributeDefinition[unexpectedAttributeIndex] = definition;
		unexpectedAttributeSystemId[unexpectedAttributeIndex] = systemId;
		unexpectedAttributeLineNumber[unexpectedAttributeIndex] = lineNumber;
		unexpectedAttributeColumnNumber[unexpectedAttributeIndex] = columnNumber;*/
		
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
        
		/*if(unexpectedAmbiguousAttributeSize == 0){
			unexpectedAmbiguousAttributeSize = 1;
			unexpectedAmbiguousAttributeIndex = 0;	
			unexpectedAmbiguousAttributeQName = new String[unexpectedAmbiguousAttributeSize];
			unexpectedAmbiguousAttributeDefinition = new SimplifiedComponent[unexpectedAmbiguousAttributeSize][];
			unexpectedAmbiguousAttributeSystemId = new String[unexpectedAmbiguousAttributeSize];			
			unexpectedAmbiguousAttributeLineNumber = new int[unexpectedAmbiguousAttributeSize];
			unexpectedAmbiguousAttributeColumnNumber = new int[unexpectedAmbiguousAttributeSize];			
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
		}
		unexpectedAmbiguousAttributeQName[unexpectedAmbiguousAttributeIndex] = qName;
		unexpectedAmbiguousAttributeDefinition[unexpectedAmbiguousAttributeIndex] = possibleDefinitions;
		unexpectedAmbiguousAttributeSystemId[unexpectedAmbiguousAttributeIndex] = systemId;
		unexpectedAmbiguousAttributeLineNumber[unexpectedAmbiguousAttributeIndex] = lineNumber;
		unexpectedAmbiguousAttributeColumnNumber[unexpectedAmbiguousAttributeIndex] = columnNumber;*/
		
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
	
	
	public void misplacedContent(APattern contextDefinition, 
											int startInputRecordIndex, 
											APattern definition, 
											int inputRecordIndex,
											APattern sourceDefinition, 
											APattern reper){//not stored, only used for internal conflict handling
        
		/*all: {	           
			if(misplacedSize == 0){
				misplacedSize = 1;
				misplacedIndex = 0;	
				misplacedContext = new APattern[misplacedSize];	
				misplacedStartSystemId = new String[misplacedSize];			
				misplacedStartLineNumber = new int[misplacedSize];
				misplacedStartColumnNumber = new int[misplacedSize];
				misplacedDefinition = new APattern[misplacedSize][1];
				misplacedItemId = new int[misplacedSize][1][1];
				misplacedQName = new String[misplacedSize][1][1];
				misplacedSystemId = new String[misplacedSize][1][1];			
				misplacedLineNumber = new int[misplacedSize][1][1];
				misplacedColumnNumber = new int[misplacedSize][1][1];
                
				misplacedContext[misplacedIndex] = contextDefinition;
				misplacedStartSystemId[misplacedIndex] = startSystemId;
				misplacedStartLineNumber[misplacedIndex] = startLineNumber;
				misplacedStartColumnNumber[misplacedIndex] = startColumnNumber;
				misplacedDefinition[misplacedIndex][0] = definition;
				misplacedItemId[misplacedIndex][0][0] = itemId;
				misplacedQName[misplacedIndex][0][0] = qName; 		
				misplacedSystemId[misplacedIndex][0][0] = systemId;
				misplacedLineNumber[misplacedIndex][0][0] = lineNumber;
				misplacedColumnNumber[misplacedIndex][0][0] = columnNumber;
                
				break all;
			}
            
			for(int i = 0; i < misplacedSize; i++){
				if(misplacedContext[i].equals(contextDefinition)
                    && misplacedStartSystemId[i].equals(startSystemId)			
                    && misplacedStartLineNumber[i] == startLineNumber
                    && misplacedStartColumnNumber[i] == startColumnNumber){
					for(int j = 0; j < misplacedDefinition[i].length; j++){
						if(misplacedDefinition[i][j].equals(definition)){
						    int length = misplacedItemId[i][j].length;
							int[] increasedII = new int[(length+1)];
							System.arraycopy(misplacedItemId[i][j], 0, increasedII, 0, length);
							misplacedItemId[i][j] = increasedII;
							misplacedItemId[i][j][length] = itemId;
							
							length = misplacedQName[i][j].length;
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
										
					
					int[][] increasedII = new int[(length+1)][];
					System.arraycopy(misplacedItemId[i], 0, increasedII, 0, length);
					misplacedItemId[i] = increasedII;			
					misplacedItemId[i][length] = new int[1];
					misplacedItemId[i][length][0] = itemId;
					
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
			
			int[][][] increasedII = new int[misplacedSize][][];
			System.arraycopy(misplacedItemId, 0, increasedII, 0, misplacedIndex);
			misplacedItemId = increasedII;
			misplacedItemId[misplacedIndex] = new int[1][1];			
			misplacedItemId[misplacedIndex][0][0] = itemId; 		
			
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
		}*/
		
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
		
		/*all: {	           
			if(misplacedSize == 0){
				misplacedSize = 1;
				misplacedIndex = 0;	
				misplacedContext = new APattern[misplacedSize];	
				misplacedStartSystemId = new String[misplacedSize];			
				misplacedStartLineNumber = new int[misplacedSize];
				misplacedStartColumnNumber = new int[misplacedSize];
				misplacedDefinition = new APattern[misplacedSize][1];
				misplacedItemId = new int[misplacedSize][1][];
				misplacedQName = new String[misplacedSize][1][];
				misplacedSystemId = new String[misplacedSize][1][];			
				misplacedLineNumber = new int[misplacedSize][1][];
				misplacedColumnNumber = new int[misplacedSize][1][];
				                
				misplacedContext[misplacedIndex] = contextDefinition;
				misplacedStartSystemId[misplacedIndex] = startSystemId;
				misplacedStartLineNumber[misplacedIndex] = startLineNumber;
				misplacedStartColumnNumber[misplacedIndex] = startColumnNumber;
				misplacedDefinition[misplacedIndex][0] = definition;
				misplacedItemId[misplacedIndex][0] = itemId;
				misplacedQName[misplacedIndex][0] = qName; 		
				misplacedSystemId[misplacedIndex][0] = systemId;
				misplacedLineNumber[misplacedIndex][0] = lineNumber;
				misplacedColumnNumber[misplacedIndex][0] = columnNumber;
				
                
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
                            
                            int length = misplacedItemId[i][j].length;
							int[] increasedII = new int[length+addedLength];
							System.arraycopy(misplacedItemId[i][j], 0, increasedII, 0, length);
							misplacedItemId[i][j] = increasedII;
                            System.arraycopy(itemId, 0, misplacedItemId[i][j], length, addedLength);
                            
							length = misplacedQName[i][j].length;
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
										
					int[][] increasedII = new int[(length+1)][];
					System.arraycopy(misplacedItemId[i], 0, increasedII, 0, length);
					misplacedItemId[i] = increasedII;
					misplacedItemId[i][length] = itemId;
					
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
			
			int[][][] increasedII = new int[misplacedSize][][];
			System.arraycopy(misplacedItemId, 0, increasedII, 0, misplacedIndex);
			misplacedItemId = increasedII;
			misplacedItemId[misplacedIndex] = new int[1][];			
			misplacedItemId[misplacedIndex][0] = itemId;
			
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
		}*/
		
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
	    
	
	public void excessiveContent(Rule context,
									int startInputRecordIndex,
									APattern definition, 
									int[] inputRecordIndex){
		/*if(excessiveSize == 0){            
			excessiveSize = 1;
			excessiveIndex = 0;
			excessiveContext = new APattern[excessiveSize];
			excessiveStartSystemId = new String[excessiveSize];			
			excessiveStartLineNumber = new int[excessiveSize];
			excessiveStartColumnNumber = new int[excessiveSize];
			excessiveDefinition = new APattern[excessiveSize];
			excessiveItemId = new int[excessiveSize][];
			excessiveQName = new String[excessiveSize][];			
			excessiveSystemId = new String[excessiveSize][];			
			excessiveLineNumber = new int[excessiveSize][];
			excessiveColumnNumber = new int[excessiveSize][];			
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
			
			int[][] increasedII = new int[excessiveSize][];
			System.arraycopy(excessiveItemId, 0, increasedII, 0, excessiveIndex);
			excessiveItemId = increasedII;
			
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
		}
        
		excessiveContext[excessiveIndex] = context;
		excessiveStartSystemId[excessiveIndex] = startSystemId;
		excessiveStartLineNumber[excessiveIndex] = startLineNumber;
		excessiveStartColumnNumber[excessiveIndex] = startColumnNumber;
		excessiveDefinition[excessiveIndex] = definition;
		excessiveItemId[excessiveIndex] = itemId;
		excessiveQName[excessiveIndex] = qName;
		excessiveSystemId[excessiveIndex] = systemId;
		excessiveLineNumber[excessiveIndex] = lineNumber;
		excessiveColumnNumber[excessiveIndex] = columnNumber;*/
		
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
	
	public void excessiveContent(Rule context, 
								APattern definition, 
								int inputRecordIndex){
        /*boolean recorded = false;
		for(int i = 0; i < excessiveSize; i++){
			if(excessiveContext[i].equals(context) &&
				excessiveDefinition[i].equals(definition)){
			    
                recorded =  true;
                
                int length = excessiveItemId[i].length;
                int[] increasedII = new int[length+1];
                System.arraycopy(excessiveItemId[i], 0, increasedII, 0, length);
                excessiveItemId[i] = increasedII;
                excessiveItemId[i][length] = itemId;
                
				length = excessiveQName[i].length;
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
        if(!recorded) throw new IllegalArgumentException();*/
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
    
	
	public void missingContent(Rule context,  
								int startInputRecordIndex,						 
								APattern definition, 
								int expected, 
								int found, 
								int[] inputRecordIndex){	    
        
		/*if(missingSize == 0){
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
		//throw new IllegalStateException();*/
		
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
	


    public void illegalContent(Rule context, 
	                        int startInputRecordIndex){
        
		/*if(illegalSize == 0){
			illegalSize = 1;
			illegalIndex = 0;
			illegalContext = new APattern[illegalSize];
			illegalItemId = new int[illegalSize];
			illegalQName = new String[illegalSize];
			illegalStartSystemId = new String[illegalSize];			
			illegalStartLineNumber = new int[illegalSize];
			illegalStartColumnNumber = new int[illegalSize];						
		}else if(++illegalIndex == illegalSize){
			APattern[] increasedEC = new APattern[++illegalSize];
			System.arraycopy(illegalContext, 0, increasedEC, 0, illegalIndex);
			illegalContext = increasedEC;
			
			int[] increasedII = new int[illegalSize];
			System.arraycopy(illegalItemId, 0, increasedII, 0, illegalIndex);
			illegalItemId = increasedII;
			
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
		}
		illegalContext[illegalIndex] = context;
		illegalItemId[illegalIndex] = startItemId;
		illegalQName[illegalIndex] = startQName;
		illegalStartSystemId[illegalIndex] = startSystemId;
		illegalStartLineNumber[illegalIndex] = startLineNumber;
		illegalStartColumnNumber[illegalIndex] = startColumnNumber;*/
		
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
        /*illegalSize = 0;
        illegalIndex = -1;
        illegalContext = null;
        illegalItemId = null;
        illegalQName = null;
        illegalStartSystemId = null;			
        illegalStartLineNumber = null;
        illegalStartColumnNumber = null;*/
        
        for(int i = 0; i <= illegalIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(illegalStartInputRecordIndex[i], this);
        }
        
        illegalIndex = -1;
        illegalContext = null;
        illegalStartInputRecordIndex = null;
    }

	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, 
									AElement[] possibleDefinitions){
        
		/*if(unresolvedAmbiguousElementSizeEE == 0){
			unresolvedAmbiguousElementSizeEE = 1;
			unresolvedAmbiguousElementIndexEE = 0;	
			unresolvedAmbiguousElementQNameEE = new String[unresolvedAmbiguousElementSizeEE];			
			unresolvedAmbiguousElementSystemIdEE = new String[unresolvedAmbiguousElementSizeEE];			
			unresolvedAmbiguousElementLineNumberEE = new int[unresolvedAmbiguousElementSizeEE];
			unresolvedAmbiguousElementColumnNumberEE = new int[unresolvedAmbiguousElementSizeEE];
			unresolvedAmbiguousElementDefinitionEE = new AElement[unresolvedAmbiguousElementSizeEE][];
		}else if(++unresolvedAmbiguousElementIndexEE == unresolvedAmbiguousElementSizeEE){			
			String[] increasedQN = new String[++unresolvedAmbiguousElementSizeEE];
			System.arraycopy(unresolvedAmbiguousElementQNameEE, 0, increasedQN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementQNameEE = increasedQN;
			
			AElement[][] increasedDef = new AElement[unresolvedAmbiguousElementSizeEE][];
			System.arraycopy(unresolvedAmbiguousElementDefinitionEE, 0, increasedDef, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[unresolvedAmbiguousElementSizeEE];
			System.arraycopy(unresolvedAmbiguousElementSystemIdEE, 0, increasedSI, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[unresolvedAmbiguousElementSizeEE];
			System.arraycopy(unresolvedAmbiguousElementLineNumberEE, 0, increasedLN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[unresolvedAmbiguousElementSizeEE];
			System.arraycopy(unresolvedAmbiguousElementColumnNumberEE, 0, increasedCN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementColumnNumberEE = increasedCN;
		}
		unresolvedAmbiguousElementQNameEE[unresolvedAmbiguousElementIndexEE] = qName;		
		unresolvedAmbiguousElementSystemIdEE[unresolvedAmbiguousElementIndexEE] = systemId;
		unresolvedAmbiguousElementLineNumberEE[unresolvedAmbiguousElementIndexEE] = lineNumber;
		unresolvedAmbiguousElementColumnNumberEE[unresolvedAmbiguousElementIndexEE] = columnNumber;
		unresolvedAmbiguousElementDefinitionEE[unresolvedAmbiguousElementIndexEE] = possibleDefinitions;*/
		
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
        /*unresolvedAmbiguousElementSizeEE = 0;
        unresolvedAmbiguousElementIndexEE = -1;	
        unresolvedAmbiguousElementQNameEE = null;			
        unresolvedAmbiguousElementSystemIdEE = null;			
        unresolvedAmbiguousElementLineNumberEE = null;
        unresolvedAmbiguousElementColumnNumberEE = null;
        unresolvedAmbiguousElementDefinitionEE = null;*/
                
        for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAmbiguousElementInputRecordIndexEE[i], this);
        }
        
        unresolvedAmbiguousElementInputRecordIndexEE = null;
        unresolvedAmbiguousElementDefinitionEE = null;
        unresolvedAmbiguousElementIndexEE = -1;
    }
	    
    public void unresolvedUnresolvedElementContentError(int inputRecordIndex, 
									AElement[] possibleDefinitions){
        
		/*if(unresolvedUnresolvedElementSizeEE == 0){
			unresolvedUnresolvedElementSizeEE = 1;
			unresolvedUnresolvedElementIndexEE = 0;	
			unresolvedUnresolvedElementQNameEE = new String[unresolvedUnresolvedElementSizeEE];			
			unresolvedUnresolvedElementSystemIdEE = new String[unresolvedUnresolvedElementSizeEE];			
			unresolvedUnresolvedElementLineNumberEE = new int[unresolvedUnresolvedElementSizeEE];
			unresolvedUnresolvedElementColumnNumberEE = new int[unresolvedUnresolvedElementSizeEE];
			unresolvedUnresolvedElementDefinitionEE = new AElement[unresolvedUnresolvedElementSizeEE][];
		}else if(++unresolvedUnresolvedElementIndexEE == unresolvedUnresolvedElementSizeEE){			
			String[] increasedQN = new String[++unresolvedUnresolvedElementSizeEE];
			System.arraycopy(unresolvedUnresolvedElementQNameEE, 0, increasedQN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementQNameEE = increasedQN;
			
			AElement[][] increasedDef = new AElement[unresolvedUnresolvedElementSizeEE][];
			System.arraycopy(unresolvedUnresolvedElementDefinitionEE, 0, increasedDef, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[unresolvedUnresolvedElementSizeEE];
			System.arraycopy(unresolvedUnresolvedElementSystemIdEE, 0, increasedSI, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[unresolvedUnresolvedElementSizeEE];
			System.arraycopy(unresolvedUnresolvedElementLineNumberEE, 0, increasedLN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[unresolvedUnresolvedElementSizeEE];
			System.arraycopy(unresolvedUnresolvedElementColumnNumberEE, 0, increasedCN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementColumnNumberEE = increasedCN;
		}
		unresolvedUnresolvedElementQNameEE[unresolvedUnresolvedElementIndexEE] = qName;		
		unresolvedUnresolvedElementSystemIdEE[unresolvedUnresolvedElementIndexEE] = systemId;
		unresolvedUnresolvedElementLineNumberEE[unresolvedUnresolvedElementIndexEE] = lineNumber;
		unresolvedUnresolvedElementColumnNumberEE[unresolvedUnresolvedElementIndexEE] = columnNumber;
		unresolvedUnresolvedElementDefinitionEE[unresolvedUnresolvedElementIndexEE] = possibleDefinitions;*/
		
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
        /*unresolvedUnresolvedElementSizeEE = 0;
        unresolvedUnresolvedElementIndexEE = -1;	
        unresolvedUnresolvedElementQNameEE = null;			
        unresolvedUnresolvedElementSystemIdEE = null;			
        unresolvedUnresolvedElementLineNumberEE = null;
        unresolvedUnresolvedElementColumnNumberEE = null;
        unresolvedUnresolvedElementDefinitionEE = null;*/
        
        for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedUnresolvedElementInputRecordIndexEE[i], this);
        }
        
        unresolvedUnresolvedElementInputRecordIndexEE = null;
        unresolvedUnresolvedElementDefinitionEE = null;
        unresolvedUnresolvedElementIndexEE = -1;    
    }
    
	    
	public void unresolvedAttributeContentError(int inputRecordIndex, 
									AAttribute[] possibleDefinitions){
        
		/*if(unresolvedAttributeSizeEE == 0){
			unresolvedAttributeSizeEE = 1;
			unresolvedAttributeIndexEE = 0;	
			unresolvedAttributeQNameEE = new String[unresolvedAttributeSizeEE];			
			unresolvedAttributeSystemIdEE = new String[unresolvedAttributeSizeEE];			
			unresolvedAttributeLineNumberEE = new int[unresolvedAttributeSizeEE];
			unresolvedAttributeColumnNumberEE = new int[unresolvedAttributeSizeEE];
			unresolvedAttributeDefinitionEE = new AAttribute[unresolvedAttributeSizeEE][];
		}else if(++unresolvedAttributeIndexEE == unresolvedAttributeSizeEE){			
			String[] increasedQN = new String[++unresolvedAttributeSizeEE];
			System.arraycopy(unresolvedAttributeQNameEE, 0, increasedQN, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeQNameEE = increasedQN;
			
			AAttribute[][] increasedDef = new AAttribute[unresolvedAttributeSizeEE][];
			System.arraycopy(unresolvedAttributeDefinitionEE, 0, increasedDef, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[unresolvedAttributeSizeEE];
			System.arraycopy(unresolvedAttributeSystemIdEE, 0, increasedSI, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[unresolvedAttributeSizeEE];
			System.arraycopy(unresolvedAttributeLineNumberEE, 0, increasedLN, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[unresolvedAttributeSizeEE];
			System.arraycopy(unresolvedAttributeColumnNumberEE, 0, increasedCN, 0, unresolvedAttributeIndexEE);
			unresolvedAttributeColumnNumberEE = increasedCN;
		}
		unresolvedAttributeQNameEE[unresolvedAttributeIndexEE] = qName;		
		unresolvedAttributeSystemIdEE[unresolvedAttributeIndexEE] = systemId;
		unresolvedAttributeLineNumberEE[unresolvedAttributeIndexEE] = lineNumber;
		unresolvedAttributeColumnNumberEE[unresolvedAttributeIndexEE] = columnNumber;
		unresolvedAttributeDefinitionEE[unresolvedAttributeIndexEE] = possibleDefinitions;*/
		
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
        /*unresolvedAttributeSizeEE = 0;
        unresolvedAttributeIndexEE = -1;	
        unresolvedAttributeQNameEE = null;			
        unresolvedAttributeSystemIdEE = null;			
        unresolvedAttributeLineNumberEE = null;
        unresolvedAttributeColumnNumberEE = null;
        unresolvedAttributeDefinitionEE = null;*/
        
        for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAttributeInputRecordIndexEE[i], this);
        }
        
        unresolvedAttributeInputRecordIndexEE = null;
        unresolvedAttributeDefinitionEE = null;
        unresolvedAttributeIndexEE = -1;
    }
	

	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, 
									AElement[] possibleDefinitions){
        
		/*if(ambiguousUnresolvedElementSizeWW == 0){
			ambiguousUnresolvedElementSizeWW = 1;
			ambiguousUnresolvedElementIndexWW = 0;	
			ambiguousUnresolvedElementQNameWW = new String[ambiguousUnresolvedElementSizeWW];			
			ambiguousUnresolvedElementSystemIdWW = new String[ambiguousUnresolvedElementSizeWW];			
			ambiguousUnresolvedElementLineNumberWW = new int[ambiguousUnresolvedElementSizeWW];
			ambiguousUnresolvedElementColumnNumberWW = new int[ambiguousUnresolvedElementSizeWW];
			ambiguousUnresolvedElementDefinitionWW = new AElement[ambiguousUnresolvedElementSizeWW][];
		}else if(++ambiguousUnresolvedElementIndexWW == ambiguousUnresolvedElementSizeWW){			
			String[] increasedQN = new String[++ambiguousUnresolvedElementSizeWW];
			System.arraycopy(ambiguousUnresolvedElementQNameWW, 0, increasedQN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementQNameWW = increasedQN;
			
			AElement[][] increasedDef = new AElement[ambiguousUnresolvedElementSizeWW][];
			System.arraycopy(ambiguousUnresolvedElementDefinitionWW, 0, increasedDef, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementDefinitionWW = increasedDef;
			
			String[] increasedSI = new String[ambiguousUnresolvedElementSizeWW];
			System.arraycopy(ambiguousUnresolvedElementSystemIdWW, 0, increasedSI, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousUnresolvedElementSizeWW];
			System.arraycopy(ambiguousUnresolvedElementLineNumberWW, 0, increasedLN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousUnresolvedElementSizeWW];
			System.arraycopy(ambiguousUnresolvedElementColumnNumberWW, 0, increasedCN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementColumnNumberWW = increasedCN;
		}
		ambiguousUnresolvedElementQNameWW[ambiguousUnresolvedElementIndexWW] = qName;		
		ambiguousUnresolvedElementSystemIdWW[ambiguousUnresolvedElementIndexWW] = systemId;
		ambiguousUnresolvedElementLineNumberWW[ambiguousUnresolvedElementIndexWW] = lineNumber;
		ambiguousUnresolvedElementColumnNumberWW[ambiguousUnresolvedElementIndexWW] = columnNumber;
		ambiguousUnresolvedElementDefinitionWW[ambiguousUnresolvedElementIndexWW] = possibleDefinitions;*/

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
        /*ambiguousUnresolvedElementSizeWW = 0;
        ambiguousUnresolvedElementIndexWW = -1;	
        ambiguousUnresolvedElementQNameWW = null;			
        ambiguousUnresolvedElementSystemIdWW = null;			
        ambiguousUnresolvedElementLineNumberWW = null;
        ambiguousUnresolvedElementColumnNumberWW = null;
        ambiguousUnresolvedElementDefinitionWW = null;*/
        
        for(int i = 0; i <= ambiguousUnresolvedElementIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousUnresolvedElementInputRecordIndexWW[i], this);
        }
        
        ambiguousUnresolvedElementInputRecordIndexWW = null;
        ambiguousUnresolvedElementDefinitionWW = null;
        ambiguousUnresolvedElementIndexWW = -1;
    }
	    
    public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, 
									AElement[] possibleDefinitions){
        /*
		if(ambiguousAmbiguousElementSizeWW == 0){
			ambiguousAmbiguousElementSizeWW = 1;
			ambiguousAmbiguousElementIndexWW = 0;	
			ambiguousAmbiguousElementQNameWW = new String[ambiguousAmbiguousElementSizeWW];			
			ambiguousAmbiguousElementSystemIdWW = new String[ambiguousAmbiguousElementSizeWW];			
			ambiguousAmbiguousElementLineNumberWW = new int[ambiguousAmbiguousElementSizeWW];
			ambiguousAmbiguousElementColumnNumberWW = new int[ambiguousAmbiguousElementSizeWW];
			ambiguousAmbiguousElementDefinitionWW = new AElement[ambiguousAmbiguousElementSizeWW][];
		}else if(++ambiguousAmbiguousElementIndexWW == ambiguousAmbiguousElementSizeWW){			
			String[] increasedQN = new String[++ambiguousAmbiguousElementSizeWW];
			System.arraycopy(ambiguousAmbiguousElementQNameWW, 0, increasedQN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementQNameWW = increasedQN;
			
			AElement[][] increasedDef = new AElement[ambiguousAmbiguousElementSizeWW][];
			System.arraycopy(ambiguousAmbiguousElementDefinitionWW, 0, increasedDef, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementDefinitionWW = increasedDef;
			
			String[] increasedSI = new String[ambiguousAmbiguousElementSizeWW];
			System.arraycopy(ambiguousAmbiguousElementSystemIdWW, 0, increasedSI, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousAmbiguousElementSizeWW];
			System.arraycopy(ambiguousAmbiguousElementLineNumberWW, 0, increasedLN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousAmbiguousElementSizeWW];
			System.arraycopy(ambiguousAmbiguousElementColumnNumberWW, 0, increasedCN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementColumnNumberWW = increasedCN;
		}
		ambiguousAmbiguousElementQNameWW[ambiguousAmbiguousElementIndexWW] = qName;		
		ambiguousAmbiguousElementSystemIdWW[ambiguousAmbiguousElementIndexWW] = systemId;
		ambiguousAmbiguousElementLineNumberWW[ambiguousAmbiguousElementIndexWW] = lineNumber;
		ambiguousAmbiguousElementColumnNumberWW[ambiguousAmbiguousElementIndexWW] = columnNumber;
		ambiguousAmbiguousElementDefinitionWW[ambiguousAmbiguousElementIndexWW] = possibleDefinitions;*/

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
        /*ambiguousAmbiguousElementSizeWW = 0;
        ambiguousAmbiguousElementIndexWW = -1;	
        ambiguousAmbiguousElementQNameWW = null;			
        ambiguousAmbiguousElementSystemIdWW = null;			
        ambiguousAmbiguousElementLineNumberWW = null;
        ambiguousAmbiguousElementColumnNumberWW = null;
        ambiguousAmbiguousElementDefinitionWW = null;*/
     
        for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAmbiguousElementInputRecordIndexWW[i], this);
        }
        
        ambiguousAmbiguousElementInputRecordIndexWW = null;
        ambiguousAmbiguousElementDefinitionWW = null;
        ambiguousAmbiguousElementIndexWW = -1;
    }
    
	        
	public void ambiguousAttributeContentWarning(int inputRecordIndex, 
									AAttribute[] possibleDefinitions){
        
		/*if(ambiguousAttributeSizeWW == 0){
			ambiguousAttributeSizeWW = 1;
			ambiguousAttributeIndexWW = 0;	
			ambiguousAttributeQNameWW = new String[ambiguousAttributeSizeWW];			
			ambiguousAttributeSystemIdWW = new String[ambiguousAttributeSizeWW];			
			ambiguousAttributeLineNumberWW = new int[ambiguousAttributeSizeWW];
			ambiguousAttributeColumnNumberWW = new int[ambiguousAttributeSizeWW];
			ambiguousAttributeDefinitionWW = new AAttribute[ambiguousAttributeSizeWW][];
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
		}
		ambiguousAttributeQNameWW[ambiguousAttributeIndexWW] = qName;		
		ambiguousAttributeSystemIdWW[ambiguousAttributeIndexWW] = systemId;
		ambiguousAttributeLineNumberWW[ambiguousAttributeIndexWW] = lineNumber;
		ambiguousAttributeColumnNumberWW[ambiguousAttributeIndexWW] = columnNumber;
		ambiguousAttributeDefinitionWW[ambiguousAttributeIndexWW] = possibleDefinitions;*/
		
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
        /*ambiguousAttributeSizeWW = 0;
        ambiguousAttributeIndexWW = -1;	
        ambiguousAttributeQNameWW = null;			
        ambiguousAttributeSystemIdWW = null;			
        ambiguousAttributeLineNumberWW = null;
        ambiguousAttributeColumnNumberWW = null;
        ambiguousAttributeDefinitionWW = null;*/
        
        for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAttributeInputRecordIndexWW[i], this);
        }
        
        ambiguousAttributeInputRecordIndexWW = null;
        ambiguousAttributeDefinitionWW = null;
        ambiguousAttributeIndexWW = -1;
    }
    
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, 
									CharsActiveTypeItem[] possibleDefinitions){
        
		/*if(ambiguousCharsSizeWW == 0){
			ambiguousCharsSizeWW = 1;
			ambiguousCharsIndexWW = 0;
			ambiguousCharsSystemIdWW = new String[ambiguousCharsSizeWW];			
			ambiguousCharsLineNumberWW = new int[ambiguousCharsSizeWW];
			ambiguousCharsColumnNumberWW = new int[ambiguousCharsSizeWW];
			ambiguousCharsDefinitionWW = new CharsActiveTypeItem[ambiguousCharsSizeWW][];
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
		}		
		ambiguousCharsSystemIdWW[ambiguousCharsIndexWW] = systemId;
		ambiguousCharsLineNumberWW[ambiguousCharsIndexWW] = lineNumber;
		ambiguousCharsColumnNumberWW[ambiguousCharsIndexWW] = columnNumber;
		ambiguousCharsDefinitionWW[ambiguousCharsIndexWW] = possibleDefinitions;*/
		
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
        /*ambiguousCharsSizeWW = 0;
        ambiguousCharsIndexWW = -1;
        ambiguousCharsSystemIdWW = null;			
        ambiguousCharsLineNumberWW = null;
        ambiguousCharsColumnNumberWW = null;
        ambiguousCharsDefinitionWW = null;*/
        
        for(int i = 0; i <= ambiguousCharsIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousCharsInputRecordIndexWW[i], this);
        }
        
        ambiguousCharsInputRecordIndexWW = null;
        ambiguousCharsDefinitionWW = null;
        ambiguousCharsIndexWW = -1;
    }
    
    
	public void ambiguousAttributeValueWarning(int inputRecordIndex, 
									CharsActiveTypeItem[] possibleDefinitions){
        
		/*if(ambiguousAVSizeWW == 0){
			ambiguousAVSizeWW = 1;
			ambiguousAVIndexWW = 0;
			ambiguousAVAttributeQNameWW = new String[ambiguousAVSizeWW];
			ambiguousAVSystemIdWW = new String[ambiguousAVSizeWW];			
			ambiguousAVLineNumberWW = new int[ambiguousAVSizeWW];
			ambiguousAVColumnNumberWW = new int[ambiguousAVSizeWW];
			ambiguousAVDefinitionWW = new CharsActiveTypeItem[ambiguousAVSizeWW][];
		}else if(++ambiguousAVIndexWW == ambiguousAVSizeWW){			
			CharsActiveTypeItem[][] increasedDef = new CharsActiveTypeItem[++ambiguousAVSizeWW][];
			System.arraycopy(ambiguousAVDefinitionWW, 0, increasedDef, 0, ambiguousAVIndexWW);
			ambiguousAVDefinitionWW = increasedDef;
			
			String[] increasedAQ = new String[ambiguousAVSizeWW];
			System.arraycopy(ambiguousAVAttributeQNameWW, 0, increasedAQ, 0, ambiguousAVIndexWW);
			ambiguousAVAttributeQNameWW = increasedAQ;
						
			String[] increasedSI = new String[ambiguousAVSizeWW];
			System.arraycopy(ambiguousAVSystemIdWW, 0, increasedSI, 0, ambiguousAVIndexWW);
			ambiguousAVSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousAVSizeWW];
			System.arraycopy(ambiguousAVLineNumberWW, 0, increasedLN, 0, ambiguousAVIndexWW);
			ambiguousAVLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousAVSizeWW];
			System.arraycopy(ambiguousAVColumnNumberWW, 0, increasedCN, 0, ambiguousAVIndexWW);
			ambiguousAVColumnNumberWW = increasedCN;
		}		
		ambiguousAVAttributeQNameWW[ambiguousAVIndexWW] = attributeQName;
		ambiguousAVSystemIdWW[ambiguousAVIndexWW] = systemId;
		ambiguousAVLineNumberWW[ambiguousAVIndexWW] = lineNumber;
		ambiguousAVColumnNumberWW[ambiguousAVIndexWW] = columnNumber;
		ambiguousAVDefinitionWW[ambiguousAVIndexWW] = possibleDefinitions;*/
		
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
        /*ambiguousAVSizeWW = 0;
        ambiguousAVIndexWW = -1;
        ambiguousAVAttributeQNameWW = null;
        ambiguousAVSystemIdWW = null;			
        ambiguousAVLineNumberWW = null;
        ambiguousAVColumnNumberWW = null;
        ambiguousAVDefinitionWW = null;*/
        
        for(int i = 0; i <= ambiguousAVIndexWW; i++){
            activeInputDescriptor.unregisterClientForRecord(ambiguousAVInputRecordIndexWW[i], this);
        }
        
        ambiguousAVInputRecordIndexWW = null;
        ambiguousAVDefinitionWW = null;
        ambiguousAVIndexWW = -1;
    }
	 
	    
    // {15}
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        /*
		if(datatypeSizeCC == 0){
			datatypeSizeCC = 1;
			datatypeIndexCC = 0;
			datatypeElementQNameCC = new String[datatypeSizeCC];
			datatypeCharsSystemIdCC = new String[datatypeSizeCC];
			datatypeCharsLineNumberCC = new int[datatypeSizeCC];
			datatypeCharsColumnNumberCC = new int[datatypeSizeCC];
			datatypeCharsDefinitionCC = new DatatypedActiveTypeItem[datatypeSizeCC];
			datatypeErrorMessageCC = new String[datatypeSizeCC];
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
		}
		datatypeElementQNameCC[datatypeIndexCC] = elementQName;
		datatypeCharsSystemIdCC[datatypeIndexCC] = charsSystemId;
		datatypeCharsLineNumberCC[datatypeIndexCC] = charsLineNumber;
		datatypeCharsColumnNumberCC[datatypeIndexCC] = columnNumber;
		datatypeCharsDefinitionCC[datatypeIndexCC] = charsDefinition;
		datatypeErrorMessageCC[datatypeIndexCC] = datatypeErrorMessage; */
		
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
        /*datatypeSizeCC = 0;
        datatypeIndexCC = -1;
        datatypeElementQNameCC = null;
        datatypeCharsSystemIdCC = null;
        datatypeCharsLineNumberCC = null;
        datatypeCharsColumnNumberCC = null;
        datatypeCharsDefinitionCC = null;
        datatypeErrorMessageCC = null;*/
        
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
        /*
		if(datatypeSizeAV == 0){
			datatypeSizeAV = 1;
			datatypeIndexAV = 0;
			datatypeAttributeQNameAV = new String[datatypeSizeAV];
			datatypeCharsSystemIdAV = new String[datatypeSizeAV];
			datatypeCharsLineNumberAV = new int[datatypeSizeAV];
			datatypeCharsColumnNumberAV = new int[datatypeSizeAV];
			datatypeCharsDefinitionAV = new DatatypedActiveTypeItem[datatypeSizeAV];
			datatypeErrorMessageAV = new String[datatypeSizeAV];
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
		}
		datatypeAttributeQNameAV[datatypeIndexAV] = attributeQName;
		datatypeCharsSystemIdAV[datatypeIndexAV] = charsSystemId;
		datatypeCharsLineNumberAV[datatypeIndexAV] = charsLineNumber;
		datatypeCharsColumnNumberAV[datatypeIndexAV] = columnNumber;
		datatypeCharsDefinitionAV[datatypeIndexAV] = charsDefinition;
		datatypeErrorMessageAV[datatypeIndexAV] = datatypeErrorMessage;*/
		
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
        /*datatypeSizeAV = 0;
        datatypeIndexAV = -1;
        datatypeAttributeQNameAV = null;
        datatypeCharsSystemIdAV = null;
        datatypeCharsLineNumberAV = null;
        datatypeCharsColumnNumberAV = null;
        datatypeCharsDefinitionAV = null;
        datatypeErrorMessageAV = null;*/
        
        for(int i = 0; i <= datatypeAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeAVInputRecordIndex[i], this);
        }
        
        datatypeAVInputRecordIndex = null;
        datatypeAVDefinition = null;
        datatypeAVErrorMessage = null;
        datatypeAVIndex = -1;
    }
    
	    
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
        /*
		if(valueSizeCC == 0){
			valueSizeCC = 1;
			valueIndexCC = 0;
			valueCharsSystemIdCC = new String[valueSizeCC];
			valueCharsLineNumberCC = new int[valueSizeCC];
			valueCharsColumnNumberCC = new int[valueSizeCC];
			valueCharsDefinitionCC = new AValue[valueSizeCC];
		}else if(++valueIndexCC == valueSizeCC){						
			String[] increasedCSI = new String[++valueSizeCC];
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
		}
		valueCharsSystemIdCC[valueIndexCC] = charsSystemId;
		valueCharsLineNumberCC[valueIndexCC] = charsLineNumber;
		valueCharsColumnNumberCC[valueIndexCC] = columnNumber;
		valueCharsDefinitionCC[valueIndexCC] = charsDefinition;*/

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
        /*valueSizeCC = 0;
        valueIndexCC = -1;
        valueCharsSystemIdCC = null;
        valueCharsLineNumberCC = null;
        valueCharsColumnNumberCC = null;
        valueCharsDefinitionCC = null;*/
        
        for(int i = 0; i <= valueCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueCharsInputRecordIndex[i], this);
        }
        
        valueCharsInputRecordIndex = null;
        valueCharsDefinition = null;
        valueCharsIndex = -1;
    }
        
    
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
        /*
		if(valueSizeAV == 0){
			valueSizeAV = 1;
			valueIndexAV = 0;
			valueAttributeQNameAV = new String[valueSizeAV];
			valueCharsSystemIdAV = new String[valueSizeAV];
			valueCharsLineNumberAV = new int[valueSizeAV];
			valueCharsColumnNumberAV = new int[valueSizeAV];
			valueCharsDefinitionAV = new AValue[valueSizeAV];
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
		}
		valueAttributeQNameAV[valueIndexAV] = attributeQName;
		valueCharsSystemIdAV[valueIndexAV] = charsSystemId;
		valueCharsLineNumberAV[valueIndexAV] = charsLineNumber;
		valueCharsColumnNumberAV[valueIndexAV] = columnNumber;
		valueCharsDefinitionAV[valueIndexAV] = charsDefinition;*/
		
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
        /*valueSizeAV = 0;
        valueIndexAV = -1;
        valueAttributeQNameAV = null;
        valueCharsSystemIdAV = null;
        valueCharsLineNumberAV = null;
        valueCharsColumnNumberAV = null;
        valueCharsDefinitionAV = null;*/
        
        for(int i = 0; i <= valueAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueAVInputRecordIndex[i], this);
        }
        
        valueAVInputRecordIndex = null;
        valueAVDefinition = null;
        valueAVIndex = -1;
    }

    
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
        /*
		if(exceptSizeCC == 0){
			exceptSizeCC = 1;
			exceptIndexCC = 0;
			exceptElementQNameCC = new String[exceptSizeCC];
			exceptCharsSystemIdCC = new String[exceptSizeCC];
			exceptCharsLineNumberCC = new int[exceptSizeCC];
			exceptCharsColumnNumberCC = new int[exceptSizeCC];
			exceptCharsDefinitionCC = new AData[exceptSizeCC];
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
		}
		exceptElementQNameCC[exceptIndexCC] = elementQName;
		exceptCharsSystemIdCC[exceptIndexCC] = charsSystemId;
		exceptCharsLineNumberCC[exceptIndexCC] = charsLineNumber;
		exceptCharsColumnNumberCC[exceptIndexCC] = columnNumber;
		exceptCharsDefinitionCC[exceptIndexCC] = charsDefinition;*/
		
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
        /*exceptSizeCC = 0;
        exceptIndexCC = -1;
        exceptElementQNameCC = null;
        exceptCharsSystemIdCC = null;
        exceptCharsLineNumberCC = null;
        exceptCharsColumnNumberCC = null;
        exceptCharsDefinitionCC = null;*/
        
        for(int i = 0; i <= exceptCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptCharsInputRecordIndex[i], this);
        }
        
        exceptCharsInputRecordIndex = null;
        exceptCharsDefinition = null;
        exceptCharsIndex = -1;
    }
    
    
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
        /*
		if(exceptSizeAV == 0){
			exceptSizeAV = 1;
			exceptIndexAV = 0;
			exceptAttributeQNameAV = new String[exceptSizeAV];
			exceptCharsSystemIdAV = new String[exceptSizeAV];
			exceptCharsLineNumberAV = new int[exceptSizeAV];
			exceptCharsColumnNumberAV = new int[exceptSizeAV];
			exceptCharsDefinitionAV = new AData[exceptSizeAV];
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
		}
		exceptAttributeQNameAV[exceptIndexAV] = attributeQName;
		exceptCharsSystemIdAV[exceptIndexAV] = charsSystemId;
		exceptCharsLineNumberAV[exceptIndexAV] = charsLineNumber;
		exceptCharsColumnNumberAV[exceptIndexAV] = columnNumber;
		exceptCharsDefinitionAV[exceptIndexAV] = charsDefinition;*/
		
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
        /*exceptSizeAV = 0;
        exceptIndexAV = -1;
        exceptAttributeQNameAV = null;
        exceptCharsSystemIdAV = null;
        exceptCharsLineNumberAV = null;
        exceptCharsColumnNumberAV = null;
        exceptCharsDefinitionAV = null;*/
        
        for(int i = 0; i <= exceptAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptAVInputRecordIndex[i], this);
        }
        
        exceptAVInputRecordIndex = null;
        exceptAVDefinition = null;
        exceptAVIndex = -1;
    }
    
    
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
        /*
		if(unexpectedSizeCC == 0){
			unexpectedSizeCC = 1;
			unexpectedIndexCC = 0;		
			unexpectedCharsSystemIdCC = new String[unexpectedSizeCC];
			unexpectedCharsLineNumberCC = new int[unexpectedSizeCC];
			unexpectedCharsColumnNumberCC = new int[unexpectedSizeCC];
			unexpectedContextDefinitionCC = new AElement[unexpectedSizeCC];
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
		}
		unexpectedCharsSystemIdCC[unexpectedIndexCC] = charsSystemId;
		unexpectedCharsLineNumberCC[unexpectedIndexCC] = charsLineNumber;
		unexpectedCharsColumnNumberCC[unexpectedIndexCC] = columnNumber;
		unexpectedContextDefinitionCC[unexpectedIndexCC] = elementDefinition;*/
		
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
        /*unexpectedSizeCC = 0;
        unexpectedIndexCC = -1;		
        unexpectedCharsSystemIdCC = null;
        unexpectedCharsLineNumberCC = null;
        unexpectedCharsColumnNumberCC = null;
        unexpectedContextDefinitionCC = null;*/
        
        for(int i = 0; i <= unexpectedCharsIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(unexpectedCharsInputRecordIndex[i], this);
        }
        
        unexpectedCharsInputRecordIndex = null;
        unexpectedCharsDefinition = null;
        unexpectedCharsIndex = -1;
    }
    

	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
        /*
		if(unexpectedSizeAV == 0){
			unexpectedSizeAV = 1;
			unexpectedIndexAV = 0;		
			unexpectedCharsSystemIdAV = new String[unexpectedSizeAV];
			unexpectedCharsLineNumberAV = new int[unexpectedSizeAV];
			unexpectedCharsColumnNumberAV = new int[unexpectedSizeAV];
			unexpectedContextDefinitionAV = new AAttribute[unexpectedSizeAV];
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
		}
		unexpectedCharsSystemIdAV[unexpectedIndexAV] = charsSystemId;
		unexpectedCharsLineNumberAV[unexpectedIndexAV] = charsLineNumber;
		unexpectedCharsColumnNumberAV[unexpectedIndexAV] = columnNumber;
		unexpectedContextDefinitionAV[unexpectedIndexAV] = attributeDefinition;*/
		
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
        /*unexpectedSizeAV = 0;
        unexpectedIndexAV = -1;		
        unexpectedCharsSystemIdAV = null;
        unexpectedCharsLineNumberAV = null;
        unexpectedCharsColumnNumberAV = null;
        unexpectedContextDefinitionAV = null;*/
        
        for(int i = 0; i <= unexpectedAVIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(unexpectedAVInputRecordIndex[i], this);
        }
        
        unexpectedAVInputRecordIndex = null;
        unexpectedAVDefinition = null;
        unexpectedAVIndex = -1;
    }
    

	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        
		/*if(unresolvedSizeCC == 0){
			unresolvedSizeCC = 1;
			unresolvedIndexCC = 0;		
			unresolvedCharsSystemIdEECC = new String[unresolvedSizeCC];
			unresolvedCharsLineNumberEECC = new int[unresolvedSizeCC];
			unresolvedCharsColumnNumberEECC = new int[unresolvedSizeCC];
			unresolvedPossibleDefinitionsCC = new CharsActiveTypeItem[unresolvedSizeCC][];
		}else if(++unresolvedIndexCC == unresolvedSizeCC){
			String[] increasedCSI = new String[++unresolvedSizeCC];
			System.arraycopy(unresolvedCharsSystemIdEECC, 0, increasedCSI, 0, unresolvedIndexCC);
			unresolvedCharsSystemIdEECC = increasedCSI;
			
			int[] increasedCLN = new int[unresolvedSizeCC];
			System.arraycopy(unresolvedCharsLineNumberEECC, 0, increasedCLN, 0, unresolvedIndexCC);
			unresolvedCharsLineNumberEECC = increasedCLN;
			
			int[] increasedCCN = new int[unresolvedSizeCC];
			System.arraycopy(unresolvedCharsColumnNumberEECC, 0, increasedCCN, 0, unresolvedIndexCC);
			unresolvedCharsColumnNumberEECC = increasedCCN;
			
			CharsActiveTypeItem[][] increasedPD = new CharsActiveTypeItem[unresolvedSizeCC][];
			System.arraycopy(unresolvedPossibleDefinitionsCC, 0, increasedPD, 0, unresolvedIndexCC);
			unresolvedPossibleDefinitionsCC = increasedPD;			
		}
		unresolvedCharsSystemIdEECC[unresolvedIndexCC] = systemId;
		unresolvedCharsLineNumberEECC[unresolvedIndexCC] = lineNumber;
		unresolvedCharsColumnNumberEECC[unresolvedIndexCC] = columnNumber;
		unresolvedPossibleDefinitionsCC[unresolvedIndexCC] = possibleDefinitions;*/
		
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
        /*unresolvedSizeCC = 0;
        unresolvedIndexCC = -1;		
        unresolvedCharsSystemIdEECC = null;
        unresolvedCharsLineNumberEECC = null;
        unresolvedCharsColumnNumberEECC = null;
        unresolvedPossibleDefinitionsCC = null;*/
        
        for(int i = 0; i <= unresolvedCharsIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedCharsInputRecordIndexEE[i], this);
        }
        
        unresolvedCharsInputRecordIndexEE = null;
        unresolvedCharsDefinitionEE = null;
        unresolvedCharsIndexEE = -1;
    }
    
    
	// {24}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        
		/*if(unresolvedSizeAV == 0){
			unresolvedSizeAV = 1;
			unresolvedIndexAV = 0;
			unresolvedAttributeQNameEEAV = new String[unresolvedSizeAV];
			unresolvedCharsSystemIdEEAV = new String[unresolvedSizeAV];
			unresolvedCharsLineNumberEEAV = new int[unresolvedSizeAV];
			unresolvedCharsColumnNumberEEAV = new int[unresolvedSizeAV];
			unresolvedPossibleDefinitionsAV = new CharsActiveTypeItem[unresolvedSizeAV][];
		}else if(++unresolvedIndexAV == unresolvedSizeAV){
			String[] increasedAQ = new String[++unresolvedSizeAV];
			System.arraycopy(unresolvedAttributeQNameEEAV, 0, increasedAQ, 0, unresolvedIndexAV);
			unresolvedAttributeQNameEEAV = increasedAQ;
			
			String[] increasedCSI = new String[++unresolvedSizeAV];
			System.arraycopy(unresolvedCharsSystemIdEEAV, 0, increasedCSI, 0, unresolvedIndexAV);
			unresolvedCharsSystemIdEEAV = increasedCSI;
			
			int[] increasedCLN = new int[unresolvedSizeAV];
			System.arraycopy(unresolvedCharsLineNumberEEAV, 0, increasedCLN, 0, unresolvedIndexAV);
			unresolvedCharsLineNumberEEAV = increasedCLN;
			
			int[] increasedAVN = new int[unresolvedSizeAV];
			System.arraycopy(unresolvedCharsColumnNumberEEAV, 0, increasedAVN, 0, unresolvedIndexAV);
			unresolvedCharsColumnNumberEEAV = increasedAVN;
			
			CharsActiveTypeItem[][] increasedPD = new CharsActiveTypeItem[unresolvedSizeAV][];
			System.arraycopy(unresolvedPossibleDefinitionsAV, 0, increasedPD, 0, unresolvedIndexAV);
			unresolvedPossibleDefinitionsAV = increasedPD;			
		}
		unresolvedAttributeQNameEEAV[unresolvedIndexAV] = attributeQName;
		unresolvedCharsSystemIdEEAV[unresolvedIndexAV] = systemId;
		unresolvedCharsLineNumberEEAV[unresolvedIndexAV] = lineNumber;
		unresolvedCharsColumnNumberEEAV[unresolvedIndexAV] = columnNumber;
		unresolvedPossibleDefinitionsAV[unresolvedIndexAV] = possibleDefinitions;*/
		
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
        /*unresolvedSizeAV = 0;
        unresolvedIndexAV = -1;
        unresolvedAttributeQNameEEAV = null;
        unresolvedCharsSystemIdEEAV = null;
        unresolvedCharsLineNumberEEAV = null;
        unresolvedCharsColumnNumberEEAV = null;
        unresolvedPossibleDefinitionsAV = null;*/
        
        for(int i = 0; i <= unresolvedAVIndexEE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedAVInputRecordIndexEE[i], this);
        }
        
        unresolvedAVInputRecordIndexEE = null;
        unresolvedAVDefinitionEE = null;
        unresolvedAVIndexEE = -1;
    }

    
    // {25}
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        /*
		if(datatypeSizeLP == 0){
			datatypeSizeLP = 1;
			datatypeIndexLP = 0;
			datatypeTokenLP = new String[datatypeSizeLP];
			datatypeCharsSystemIdLP = new String[datatypeSizeLP];
			datatypeCharsLineNumberLP = new int[datatypeSizeLP];
			datatypeCharsColumnNumberLP = new int[datatypeSizeLP];
			datatypeCharsDefinitionLP = new DatatypedActiveTypeItem[datatypeSizeLP];
			datatypeErrorMessageLP = new String[datatypeSizeLP];
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
		}
		datatypeTokenLP[datatypeIndexLP] = token;
		datatypeCharsSystemIdLP[datatypeIndexLP] = charsSystemId;
		datatypeCharsLineNumberLP[datatypeIndexLP] = charsLineNumber;
		datatypeCharsColumnNumberLP[datatypeIndexLP] = columnNumber;
		datatypeCharsDefinitionLP[datatypeIndexLP] = charsDefinition;
		datatypeErrorMessageLP[datatypeIndexLP] = datatypeErrorMessage;*/
		
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
        /*datatypeSizeLP = 0;
        datatypeIndexLP = -1;
        datatypeTokenLP = null;
        datatypeCharsSystemIdLP = null;
        datatypeCharsLineNumberLP = null;
        datatypeCharsColumnNumberLP = null;
        datatypeCharsDefinitionLP = null;
        datatypeErrorMessageLP = null;*/
        
        for(int i = 0; i <= datatypeTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(datatypeTokenInputRecordIndex[i], this);
        }
        
        datatypeTokenInputRecordIndex = null;
        datatypeTokenDefinition = null;
        datatypeTokenErrorMessage = null;
        datatypeTokenIndex = -1;
    }

        
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
        /*
		if(valueSizeLP == 0){
			valueSizeLP = 1;
			valueIndexLP = 0;
			valueTokenLP = new String[valueSizeLP];
			valueCharsSystemIdLP = new String[valueSizeLP];
			valueCharsLineNumberLP = new int[valueSizeLP];
			valueCharsColumnNumberLP = new int[valueSizeLP];
			valueCharsDefinitionLP = new AValue[valueSizeLP];
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
		}
		valueTokenLP[valueIndexLP] = token;
		valueCharsSystemIdLP[valueIndexLP] = charsSystemId;
		valueCharsLineNumberLP[valueIndexLP] = charsLineNumber;
		valueCharsColumnNumberLP[valueIndexLP] = columnNumber;
		valueCharsDefinitionLP[valueIndexLP] = charsDefinition;*/
		
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
        /*valueSizeLP = 0;
        valueIndexLP = -1;
        valueTokenLP = null;
        valueCharsSystemIdLP = null;
        valueCharsLineNumberLP = null;
        valueCharsColumnNumberLP = null;
        valueCharsDefinitionLP = null;*/
        
        for(int i = 0; i <= valueTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(valueTokenInputRecordIndex[i], this);
        }
        
        valueTokenInputRecordIndex = null;
        valueTokenDefinition = null;
        valueTokenIndex = -1;
    }

    
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
        /*
		if(exceptSizeLP == 0){
			exceptSizeLP = 1;
			exceptIndexLP = 0;
			exceptTokenLP = new String[exceptSizeLP];
			exceptCharsSystemIdLP = new String[exceptSizeLP];
			exceptCharsLineNumberLP = new int[exceptSizeLP];
			exceptCharsColumnNumberLP = new int[exceptSizeLP];
			exceptCharsDefinitionLP = new AData[exceptSizeLP];
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
		}
		exceptTokenLP[exceptIndexLP] = token;
		exceptCharsSystemIdLP[exceptIndexLP] = charsSystemId;
		exceptCharsLineNumberLP[exceptIndexLP] = charsLineNumber;
		exceptCharsColumnNumberLP[exceptIndexLP] = columnNumber;
		exceptCharsDefinitionLP[exceptIndexLP] = charsDefinition;*/
		
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
        /*exceptSizeLP = 0;
        exceptIndexLP = -1;
        exceptTokenLP = null;
        exceptCharsSystemIdLP = null;
        exceptCharsLineNumberLP = null;
        exceptCharsColumnNumberLP = null;
        exceptCharsDefinitionLP = null;*/
        
        for(int i = 0; i <= exceptTokenIndex; i++){
            activeInputDescriptor.unregisterClientForRecord(exceptTokenInputRecordIndex[i], this);
        }
        
        exceptTokenInputRecordIndex = null;
        exceptTokenDefinition = null;
        exceptTokenIndex = -1;
    }
    
	
    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        
        /*if(unresolvedSizeLPICE == 0){
			unresolvedSizeLPICE = 1;
			unresolvedIndexLPICE = 0;
			unresolvedTokenLPICE = new String[unresolvedSizeLPICE];
			unresolvedCharsSystemIdEELPICE = new String[unresolvedSizeLPICE];
			unresolvedCharsLineNumberEELPICE = new int[unresolvedSizeLPICE];
			unresolvedCharsColumnNumberEELPICE = new int[unresolvedSizeLPICE];
			unresolvedPossibleDefinitionsLPICE = new CharsActiveTypeItem[unresolvedSizeLPICE][];
		}else if(++unresolvedIndexLPICE == unresolvedSizeLPICE){
			String[] increasedT = new String[++unresolvedSizeLPICE];
			System.arraycopy(unresolvedTokenLPICE, 0, increasedT, 0, unresolvedIndexLPICE);
			unresolvedTokenLPICE = increasedT;
						
			String[] increasedCSI = new String[unresolvedSizeLPICE];
			System.arraycopy(unresolvedCharsSystemIdEELPICE, 0, increasedCSI, 0, unresolvedIndexLPICE);
			unresolvedCharsSystemIdEELPICE = increasedCSI;
			
			int[] increasedCLN = new int[unresolvedSizeLPICE];
			System.arraycopy(unresolvedCharsLineNumberEELPICE, 0, increasedCLN, 0, unresolvedIndexLPICE);
			unresolvedCharsLineNumberEELPICE = increasedCLN;
			
			int[] increasedLPICEN = new int[unresolvedSizeLPICE];
			System.arraycopy(unresolvedCharsColumnNumberEELPICE, 0, increasedLPICEN, 0, unresolvedIndexLPICE);
			unresolvedCharsColumnNumberEELPICE = increasedLPICEN;
			
			CharsActiveTypeItem[][] increasedPD = new CharsActiveTypeItem[unresolvedSizeLPICE][];
			System.arraycopy(unresolvedPossibleDefinitionsLPICE, 0, increasedPD, 0, unresolvedIndexLPICE);
			unresolvedPossibleDefinitionsLPICE = increasedPD;			
		}
		unresolvedTokenLPICE[unresolvedIndexLPICE] = token;
		unresolvedCharsSystemIdEELPICE[unresolvedIndexLPICE] = systemId;
		unresolvedCharsLineNumberEELPICE[unresolvedIndexLPICE] = lineNumber;
		unresolvedCharsColumnNumberEELPICE[unresolvedIndexLPICE] = columnNumber;
		unresolvedPossibleDefinitionsLPICE[unresolvedIndexLPICE] = possibleDefinitions;*/
		
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
        /*unresolvedSizeLPICE = 0;
        unresolvedIndexLPICE = -1;
        unresolvedTokenLPICE = null;
        unresolvedCharsSystemIdEELPICE = null;
        unresolvedCharsLineNumberEELPICE = null;
        unresolvedCharsColumnNumberEELPICE = null;
        unresolvedPossibleDefinitionsLPICE = null;*/
        
        for(int i = 0; i <= unresolvedTokenIndexLPICE; i++){
            activeInputDescriptor.unregisterClientForRecord(unresolvedTokenInputRecordIndexLPICE[i], this);
        }
        
        unresolvedTokenInputRecordIndexLPICE = null;
        unresolvedTokenDefinitionLPICE = null;
        unresolvedTokenIndexLPICE = -1;
    }
    
    
    public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        
        /*if(ambiguousSizeLPICW == 0){
			ambiguousSizeLPICW = 1;
			ambiguousIndexLPICW = 0;
			ambiguousTokenLPICW = new String[ambiguousSizeLPICW];
			ambiguousCharsSystemIdEELPICW = new String[ambiguousSizeLPICW];
			ambiguousCharsLineNumberEELPICW = new int[ambiguousSizeLPICW];
			ambiguousCharsColumnNumberEELPICW = new int[ambiguousSizeLPICW];
			ambiguousPossibleDefinitionsLPICW = new CharsActiveTypeItem[ambiguousSizeLPICW][];
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
		}
		ambiguousTokenLPICW[ambiguousIndexLPICW] = token;
		ambiguousCharsSystemIdEELPICW[ambiguousIndexLPICW] = systemId;
		ambiguousCharsLineNumberEELPICW[ambiguousIndexLPICW] = lineNumber;
		ambiguousCharsColumnNumberEELPICW[ambiguousIndexLPICW] = columnNumber;
		ambiguousPossibleDefinitionsLPICW[ambiguousIndexLPICW] = possibleDefinitions;*/
		
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
        /*ambiguousSizeLPICW = 0;
        ambiguousIndexLPICW = -1;
        ambiguousTokenLPICW = null;
        ambiguousCharsSystemIdEELPICW = null;
        ambiguousCharsLineNumberEELPICW = null;
        ambiguousCharsColumnNumberEELPICW = null;
        ambiguousPossibleDefinitionsLPICW = null;*/
        
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
        
		/*if(missingCompositorContentSize == 0){
			missingCompositorContentSize = 1;
			missingCompositorContentIndex = 0;
			missingCompositorContentContext = new APattern[missingCompositorContentSize];
			missingCompositorContentStartSystemId = new String[missingCompositorContentSize];			
			missingCompositorContentStartLineNumber = new int[missingCompositorContentSize];
			missingCompositorContentStartColumnNumber = new int[missingCompositorContentSize];
			missingCompositorContentDefinition = new APattern[missingCompositorContentSize];
			missingCompositorContentExpected = new int[missingCompositorContentSize];
			missingCompositorContentFound = new int[missingCompositorContentSize];			
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
		
		}
		missingCompositorContentContext[missingCompositorContentIndex] = context;
		missingCompositorContentStartSystemId[missingCompositorContentIndex] = startSystemId;
		missingCompositorContentStartLineNumber[missingCompositorContentIndex] = startLineNumber;
		missingCompositorContentStartColumnNumber[missingCompositorContentIndex] = startColumnNumber;
		missingCompositorContentDefinition[missingCompositorContentIndex] = definition;
		missingCompositorContentExpected[missingCompositorContentIndex] = expected;
		missingCompositorContentFound[missingCompositorContentIndex] = found;
			*/
			
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
        /*missingCompositorContentSize = 0;
        missingCompositorContentIndex = -1;
        missingCompositorContentContext = null;
        missingCompositorContentStartSystemId = null;			
        missingCompositorContentStartLineNumber = null;
        missingCompositorContentStartColumnNumber = null;
        missingCompositorContentDefinition = null;
        missingCompositorContentExpected = null;
        missingCompositorContentFound = null;*/
        
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
            if(isDiscarded)commonMessages.clear(); // It can be only one because it is about this context.
            commonMessages = null;
        }
        
        disqualified = null;
        
        if(candidateMessages != null){
            if(isDiscarded){
                for(MessageReporter cm : candidateMessages){                    
                    if(cm != null){
                        cm.clear();
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
	        conflictMessageReporter.clear();
	        conflictMessageReporter = null;
	    }
	    internalConflict = false;
	}
	
    public void transferErrorMessages(ErrorCatcher errorCatcher){
		// {2}
        //String message = "";
		if(unknownElementIndex >= 0){
			for(int i = 0; i <= unknownElementIndex; i++){
				/*errorCatcher.unknownElement(unknownElementQName[i],
				                        unknownElementSystemId[i],
				                        unknownElementLineNumber[i],
				                        unknownElementColumnNumber[i]);*/
				
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
		    APattern[] sourceDefinition = null;
		    APattern reper = null;
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
			/*datatypeCharsInputRecordIndex = null;
			datatypeCharsDefinition = null;
			datatypeCharsErrorMessage = null;
			datatypeCharsIndex = -1;*/
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
	
	public void setDiscarded(boolean isDiscarded){
	    this.isDiscarded = isDiscarded;
	    
	    if(commonMessages != null)commonMessages.setDiscarded(isDiscarded);
        
        if(candidateMessages!= null){
            for(int i = 0; i < candidateMessages.length; i++){
                if(candidateMessages[i] != null)candidateMessages[i].setDiscarded(isDiscarded);;
            }
        }
        
        if(internalConflict && conflictMessageReporter != null) conflictMessageReporter.setDiscarded(isDiscarded);
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
        
        /*messageTotalCount = 0;
        
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
        
        isMessageRetrieved = false;*/
        isDiscarded = false;
    }
}
