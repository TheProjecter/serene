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

import sereneWrite.MessageWriter;

public class TemporaryMessageStorage  implements ErrorCatcher{	
    
	// {1}
	String undeterminedQName;
	String undeterminedCandidateMessages;
		
	// {2}
	String[] unknownElementQName;
	String[] unknownElementSystemId;
	int[] unknownElementLineNumber;
	int[] unknownElementColumnNumber;
	int unknownElementIndex;
	int unknownElementSize;	
	
	// {3}
	String[] unexpectedElementQName;
	SimplifiedComponent[] unexpectedElementDefinition;
	String[] unexpectedElementSystemId;
	int[] unexpectedElementLineNumber;
	int[] unexpectedElementColumnNumber;
	int unexpectedElementIndex;
	int unexpectedElementSize;
	
	// {4}
	String[] unexpectedAmbiguousElementQName;
	SimplifiedComponent[][] unexpectedAmbiguousElementDefinition;
	String[] unexpectedAmbiguousElementSystemId;
	int[] unexpectedAmbiguousElementLineNumber;
	int[] unexpectedAmbiguousElementColumnNumber;
	int unexpectedAmbiguousElementIndex;
	int unexpectedAmbiguousElementSize;
	
	// {5}
	String[] unknownAttributeQName;
	String[] unknownAttributeSystemId;
	int[] unknownAttributeLineNumber;
	int[] unknownAttributeColumnNumber;
	int unknownAttributeIndex;
	int unknownAttributeSize;	
	
	// {6}
	String[] unexpectedAttributeQName;
	SimplifiedComponent[] unexpectedAttributeDefinition;
	String[] unexpectedAttributeSystemId;
	int[] unexpectedAttributeLineNumber;
	int[] unexpectedAttributeColumnNumber;
	int unexpectedAttributeIndex;
	int unexpectedAttributeSize;
	
	// {7}
	String[] unexpectedAmbiguousAttributeQName;
	SimplifiedComponent[][] unexpectedAmbiguousAttributeDefinition;
	String[] unexpectedAmbiguousAttributeSystemId;
	int[] unexpectedAmbiguousAttributeLineNumber;
	int[] unexpectedAmbiguousAttributeColumnNumber;
	int unexpectedAmbiguousAttributeIndex;
	int unexpectedAmbiguousAttributeSize;
	
	
	// {8}
	APattern[] misplacedContext;
	String[] misplacedStartSystemId;
	int[] misplacedStartLineNumber;
	int[] misplacedStartColumnNumber;
	APattern[][] misplacedDefinition;
	String[][][] misplacedQName;	
	String[][][] misplacedSystemId;
	int[][][] misplacedLineNumber;
	int[][][] misplacedColumnNumber;
	int misplacedIndex;
	int misplacedSize;

	// {9}
	Rule[] excessiveContext;
	String[] excessiveStartSystemId;
	int[] excessiveStartLineNumber;
	int[] excessiveStartColumnNumber;
	APattern[] excessiveDefinition;
	String[][] excessiveQName;
	String[][] excessiveSystemId;
	int[][] excessiveLineNumber;
	int[][] excessiveColumnNumber;
	int excessiveIndex;
	int excessiveSize;
	
	// {10}
	Rule[] missingContext;
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
	int missingSize;
	
	
	// {11}
	Rule[] illegalContext;
	String[] illegalQName;
	String[] illegalStartSystemId;
	int[] illegalStartLineNumber;
	int[] illegalStartColumnNumber;	
	int illegalIndex;
	int illegalSize;
	
	// {12 A}
	String[] unresolvedAmbiguousElementQNameEE;
	String[] unresolvedAmbiguousElementSystemIdEE;
	int[] unresolvedAmbiguousElementLineNumberEE;
	int[] unresolvedAmbiguousElementColumnNumberEE;
	AElement[][] unresolvedAmbiguousElementDefinitionEE;
	int unresolvedAmbiguousElementIndexEE;
	int unresolvedAmbiguousElementSizeEE;
	
	// {12 U}
	String[] unresolvedUnresolvedElementQNameEE;
	String[] unresolvedUnresolvedElementSystemIdEE;
	int[] unresolvedUnresolvedElementLineNumberEE;
	int[] unresolvedUnresolvedElementColumnNumberEE;
	AElement[][] unresolvedUnresolvedElementDefinitionEE;
	int unresolvedUnresolvedElementIndexEE;
	int unresolvedUnresolvedElementSizeEE;

	// {13}
	String[] unresolvedAttributeQNameEE;
	String[] unresolvedAttributeSystemIdEE;
	int[] unresolvedAttributeLineNumberEE;
	int[] unresolvedAttributeColumnNumberEE;
	AAttribute[][] unresolvedAttributeDefinitionEE;
	int unresolvedAttributeIndexEE;
	int unresolvedAttributeSizeEE;

	// {14}
	String[] ambiguousCharsSystemIdEE;
	int[] ambiguousCharsLineNumberEE;
	int[] ambiguousCharsColumnNumberEE;
	CharsActiveTypeItem[][] ambiguousCharsDefinitionEE;
	int ambiguousCharsIndexEE;
	int ambiguousCharsSizeEE;
	
	
	// {w1 U}
	String[] ambiguousUnresolvedElementQNameWW;
	String[] ambiguousUnresolvedElementSystemIdWW;
	int[] ambiguousUnresolvedElementLineNumberWW;
	int[] ambiguousUnresolvedElementColumnNumberWW;
	AElement[][] ambiguousUnresolvedElementDefinitionWW;
	int ambiguousUnresolvedElementIndexWW;
	int ambiguousUnresolvedElementSizeWW;
	
	// {w1 A}
	String[] ambiguousAmbiguousElementQNameWW;
	String[] ambiguousAmbiguousElementSystemIdWW;
	int[] ambiguousAmbiguousElementLineNumberWW;
	int[] ambiguousAmbiguousElementColumnNumberWW;
	AElement[][] ambiguousAmbiguousElementDefinitionWW;
	int ambiguousAmbiguousElementIndexWW;
	int ambiguousAmbiguousElementSizeWW;
	
	
	
	// {w2}
	String[] ambiguousAttributeQNameWW;
	String[] ambiguousAttributeSystemIdWW;
	int[] ambiguousAttributeLineNumberWW;
	int[] ambiguousAttributeColumnNumberWW;
	AAttribute[][] ambiguousAttributeDefinitionWW;
	int ambiguousAttributeIndexWW;
	int ambiguousAttributeSizeWW;

	// {w3}
	String[] ambiguousCharsSystemIdWW;
	int[] ambiguousCharsLineNumberWW;
	int[] ambiguousCharsColumnNumberWW;
	CharsActiveTypeItem[][] ambiguousCharsDefinitionWW;
	int ambiguousCharsIndexWW;
	int ambiguousCharsSizeWW;
	
	
	// {15}
	String datatypeElementQNameCC[];
	String datatypeCharsSystemIdCC[];//CC character content
	int datatypeCharsLineNumberCC[];
	int datatypeCharsColumnNumberCC[];
	DatatypedActiveTypeItem datatypeCharsDefinitionCC[];
	String datatypeErrorMessageCC[];
	int datatypeIndexCC;
	int datatypeSizeCC;
	
	// {16}
	String datatypeAttributeQNameAV[];
	String datatypeCharsSystemIdAV[];//AV attribute value
	int datatypeCharsLineNumberAV[];
	int datatypeCharsColumnNumberAV[];
	DatatypedActiveTypeItem datatypeCharsDefinitionAV[];
	String datatypeErrorMessageAV[];
	int datatypeIndexAV;
	int datatypeSizeAV;
   
	
	// {17}
	String valueElementQNameCC[];
	String valueCharsSystemIdCC[];//CC character content
	int valueCharsLineNumberCC[];
	int valueCharsColumnNumberCC[];
	AValue valueCharsDefinitionCC[];
	int valueIndexCC;
	int valueSizeCC;
	
	// {18}
	String valueAttributeQNameAV[];
	String valueCharsSystemIdAV[];//AV attribute value
	int valueCharsLineNumberAV[];
	int valueCharsColumnNumberAV[];
	AValue valueCharsDefinitionAV[];
	int valueIndexAV;
	int valueSizeAV;
	
	// {19}
	String exceptElementQNameCC[];
	String exceptCharsSystemIdCC[];//CC character content
	int exceptCharsLineNumberCC[];
	int exceptCharsColumnNumberCC[];
	AData exceptCharsDefinitionCC[];
	int exceptIndexCC;
	int exceptSizeCC;
	
	// {20}
	String exceptAttributeQNameAV[];
	String exceptCharsSystemIdAV[];//AV attribute except
	int exceptCharsLineNumberAV[];
	int exceptCharsColumnNumberAV[];
	AData exceptCharsDefinitionAV[];
	int exceptIndexAV;
	int exceptSizeAV;
	
	// {21}
	String unexpectedCharsSystemIdCC[];//CC character content
	int unexpectedCharsLineNumberCC[];
	int unexpectedCharsColumnNumberCC[];
	AElement unexpectedContextDefinitionCC[];
	int unexpectedIndexCC;
	int unexpectedSizeCC;
	
	// {22}
	String unexpectedCharsSystemIdAV[];//AV attribute unexpected
	int unexpectedCharsLineNumberAV[];
	int unexpectedCharsColumnNumberAV[];
	AAttribute unexpectedContextDefinitionAV[];
	int unexpectedIndexAV;
	int unexpectedSizeAV;
	
	
	// {23}
	String ambiguousCharsSystemIdEECC[];//CC character content
	int ambiguousCharsLineNumberEECC[];
	int ambiguousCharsColumnNumberEECC[];
	CharsActiveTypeItem ambiguousPossibleDefinitionsCC[][];
	int ambiguousIndexCC;
	int ambiguousSizeCC;
	
	// {24}
	String ambiguousAttributeQNameEEAV[];
	String ambiguousCharsSystemIdEEAV[];//AV attribute ambiguous
	int ambiguousCharsLineNumberEEAV[];
	int ambiguousCharsColumnNumberEEAV[];
	CharsActiveTypeItem ambiguousPossibleDefinitionsAV[][];
	int ambiguousIndexAV;
	int ambiguousSizeAV;
	
	
	// {25}
	String datatypeTokenLP[];//LP list pattern
	String datatypeCharsSystemIdLP[];
	int datatypeCharsLineNumberLP[];
	int datatypeCharsColumnNumberLP[];
	DatatypedActiveTypeItem datatypeCharsDefinitionLP[];
	String datatypeErrorMessageLP[];
	int datatypeIndexLP;
	int datatypeSizeLP;
    	
	// {26}
	String valueTokenLP[];//LP list pattern
	String valueCharsSystemIdLP[];
	int valueCharsLineNumberLP[];
	int valueCharsColumnNumberLP[];
	AValue valueCharsDefinitionLP[];
	int valueIndexLP;
	int valueSizeLP;
	
	// {27}
	String exceptTokenLP[];//LP list pattern
	String exceptCharsSystemIdLP[];
	int exceptCharsLineNumberLP[];
	int exceptCharsColumnNumberLP[];
	AData exceptCharsDefinitionLP[];
	int exceptIndexLP;
	int exceptSizeLP;
	
	// {28}
	String ambiguousTokenLP[];//LP list pattern
	String ambiguousCharsSystemIdEELP[];
	int ambiguousCharsLineNumberEELP[];
	int ambiguousCharsColumnNumberEELP[];
	CharsActiveTypeItem ambiguousPossibleDefinitionsLP[][];
	int ambiguousIndexLP;
	int ambiguousSizeLP;
    
    // {28_1}
	String ambiguousTokenLPICE[];//LPICE list pattern in context validation error
	String ambiguousCharsSystemIdEELPICE[];
	int ambiguousCharsLineNumberEELPICE[];
	int ambiguousCharsColumnNumberEELPICE[];
	CharsActiveTypeItem ambiguousPossibleDefinitionsLPICE[][];
	int ambiguousIndexLPICE;
	int ambiguousSizeLPICE;
    
    
    // {28_2}
	String ambiguousTokenLPICW[];//LPICW list pattern in context validation warning
	String ambiguousCharsSystemIdEELPICW[];
	int ambiguousCharsLineNumberEELPICW[];
	int ambiguousCharsColumnNumberEELPICW[];
	CharsActiveTypeItem ambiguousPossibleDefinitionsLPICW[][];
	int ambiguousIndexLPICW;
	int ambiguousSizeLPICW;
    
	
	// {29}
	Rule[] missingCompositorContentContext;
	String[] missingCompositorContentStartSystemId;
	int[] missingCompositorContentStartLineNumber;
	int[] missingCompositorContentStartColumnNumber;
	APattern[] missingCompositorContentDefinition;
	int[] missingCompositorContentExpected;
	int[] missingCompositorContentFound;
	int missingCompositorContentIndex;
	int missingCompositorContentSize;
    
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
    
	public TemporaryMessageStorage(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
				
		// {2}
        unknownElementIndex = -1;
        unknownElementSize = 0;	
        
        // {3}
        unexpectedElementIndex = -1;
        unexpectedElementSize = 0;
        
        // {4}
        unexpectedAmbiguousElementIndex = -1;
        unexpectedAmbiguousElementSize = 0;
        
        // {5}
        unknownAttributeIndex = -1;
        unknownAttributeSize = 0;	
        
        // {6}
        unexpectedAttributeIndex = -1;
        unexpectedAttributeSize = 0;
        
        // {7}
        unexpectedAmbiguousAttributeIndex = -1;
        unexpectedAmbiguousAttributeSize = 0;
        
        
        // {8}
        misplacedIndex = -1;
        misplacedSize = 0;
    
        // {9}
        excessiveIndex = -1;
        excessiveSize = 0;
        
        // {10}
        missingIndex = -1;
        missingSize = 0;
        
        
        // {11}	
        illegalIndex = -1;
        illegalSize = 0;
        
        // {12 A}
        unresolvedAmbiguousElementIndexEE = -1;
        unresolvedAmbiguousElementSizeEE = 0;
        
        // {12 U}
        unresolvedUnresolvedElementIndexEE = -1;
        unresolvedUnresolvedElementSizeEE = 0;
    
        // {13}
        unresolvedAttributeIndexEE = -1;
        unresolvedAttributeSizeEE = 0;
    
        // {14}
        ambiguousCharsIndexEE = -1;
        ambiguousCharsSizeEE = 0;
        
        
        // {w1 U}
        ambiguousUnresolvedElementIndexWW = -1;
        ambiguousUnresolvedElementSizeWW = 0;
        
        // {w1 A}
        ambiguousAmbiguousElementIndexWW = -1;
        ambiguousAmbiguousElementSizeWW = 0;
        
        
        
        // {w2}
        ambiguousAttributeIndexWW = -1;
        ambiguousAttributeSizeWW = 0;
    
        // {w3}
        ambiguousCharsIndexWW = -1;
        ambiguousCharsSizeWW = 0;
        
        
        // {15}
        datatypeIndexCC = -1;
        datatypeSizeCC = 0;
        
        // {16}
        datatypeIndexAV = -1;
        datatypeSizeAV = 0;
       
        
        // {17}
        valueIndexCC = -1;
        valueSizeCC = 0;
        
        // {18}
        valueIndexAV = -1;
        valueSizeAV = 0;
        
        // {19}
        exceptIndexCC = -1;
        exceptSizeCC = 0;
        
        // {20}
        exceptIndexAV = -1;
        exceptSizeAV = 0;
        
        // {21}
        unexpectedIndexCC = -1;
        unexpectedSizeCC = 0;
        
        // {22}
        unexpectedIndexAV = -1;
        unexpectedSizeAV = 0;
        
        
        // {23}
        ambiguousIndexCC = -1;
        ambiguousSizeCC = 0;
        
        // {24}
        ambiguousIndexAV = -1;
        ambiguousSizeAV = 0;
        
        
        // {25}
        datatypeIndexLP = -1;
        datatypeSizeLP = 0;
            
        // {26}
        valueIndexLP = -1;
        valueSizeLP = 0;
        
        // {27}
        exceptIndexLP = -1;
        exceptSizeLP = 0;
        
        // {28}
        ambiguousIndexLP = -1;
        ambiguousSizeLP = 0;
        
        // {28_1}
        ambiguousIndexLPICE = -1;
        ambiguousSizeLPICE = 0;
        
        
        // {28_2}
        ambiguousIndexLPICW = -1;
        ambiguousSizeLPICW = 0;
        
        
        // {29}
        missingCompositorContentIndex = -1;
        missingCompositorContentSize = 0;
        
        // {30}
        
		conflict = false;
		
		internalConflict = false;
	}  
    
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
        
		if(unknownElementSize == 0){
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
		unknownElementColumnNumber[unknownElementIndex] = columnNumber;
	}
	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        
		if(unexpectedElementSize == 0){
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
		unexpectedElementColumnNumber[unexpectedElementIndex] = columnNumber;
	}
    
    
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        
		if(unexpectedAmbiguousElementSize == 0){
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
		unexpectedAmbiguousElementColumnNumber[unexpectedAmbiguousElementIndex] = columnNumber;
	}
		
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
        
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
		}
		unknownAttributeQName[unknownAttributeIndex] = qName;
		unknownAttributeSystemId[unknownAttributeIndex] = systemId;
		unknownAttributeLineNumber[unknownAttributeIndex] = lineNumber;
		unknownAttributeColumnNumber[unknownAttributeIndex] = columnNumber;
	}
	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        
        
		if(unexpectedAttributeSize == 0){
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
		unexpectedAttributeColumnNumber[unexpectedAttributeIndex] = columnNumber;
	}	
	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        
		if(unexpectedAmbiguousAttributeSize == 0){
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
		unexpectedAmbiguousAttributeColumnNumber[unexpectedAmbiguousAttributeIndex] = columnNumber;
	}
	
	public void misplacedElement(APattern contextDefinition, 
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
				
				misplacedContext[misplacedIndex] = contextDefinition;
				misplacedStartSystemId[misplacedIndex] = startSystemId;
				misplacedStartLineNumber[misplacedIndex] = startLineNumber;
				misplacedStartColumnNumber[misplacedIndex] = startColumnNumber;
				misplacedDefinition[misplacedIndex][0] = definition; 
				misplacedQName[misplacedIndex][0][0] = qName; 		
				misplacedSystemId[misplacedIndex][0][0] = systemId;
				misplacedLineNumber[misplacedIndex][0][0] = lineNumber;
				misplacedColumnNumber[misplacedIndex][0][0] = columnNumber;
				
				break all;
			}
            
			for(int i = 0; i < misplacedSize; i++){
				if(misplacedContext[i].equals(contextDefinition)){
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
		}
	}
	public void misplacedElement(APattern contextDefinition, 
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
		}
	}
    
	
	public void excessiveContent(Rule context,
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
		}
        
		excessiveContext[excessiveIndex] = context;
		excessiveStartSystemId[excessiveIndex] = startSystemId;
		excessiveStartLineNumber[excessiveIndex] = startLineNumber;
		excessiveStartColumnNumber[excessiveIndex] = startColumnNumber;
		excessiveDefinition[excessiveIndex] = definition;
		excessiveQName[excessiveIndex] = qName;
		excessiveSystemId[excessiveIndex] = systemId;
		excessiveLineNumber[excessiveIndex] = lineNumber;
		excessiveColumnNumber[excessiveIndex] = columnNumber;		
		
		String excessive = "";
		for(int i = 0; i < qName.length; i++){
			excessive+="\n"+systemId[i]+":"+lineNumber[i]+":"+columnNumber[i]+":"+qName[i];
		}
		excessive.trim();
	}   
	public void excessiveContent(Rule context, 
								APattern definition, 
								String qName, 
								String systemId, 
								int lineNumber,		
								int columnNumber){
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
	    
	public void unresolvedAmbiguousElementContentError(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        
		if(unresolvedAmbiguousElementSizeEE == 0){
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
		unresolvedAmbiguousElementDefinitionEE[unresolvedAmbiguousElementIndexEE] = possibleDefinitions;
	}
	    
    public void unresolvedUnresolvedElementContentError(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        
		if(unresolvedUnresolvedElementSizeEE == 0){
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
		unresolvedUnresolvedElementDefinitionEE[unresolvedUnresolvedElementIndexEE] = possibleDefinitions;
	}
	    
	public void unresolvedAttributeContentError(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions){
        
		if(unresolvedAttributeSizeEE == 0){
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
		unresolvedAttributeDefinitionEE[unresolvedAttributeIndexEE] = possibleDefinitions;
	}
	
	
	public void ambiguousCharsContentError(String systemId, 
									int lineNumber, 
									int columnNumber, 
									CharsActiveTypeItem[] possibleDefinitions){
        
		if(ambiguousCharsSizeEE == 0){
			ambiguousCharsSizeEE = 1;
			ambiguousCharsIndexEE = 0;
			ambiguousCharsSystemIdEE = new String[ambiguousCharsSizeEE];			
			ambiguousCharsLineNumberEE = new int[ambiguousCharsSizeEE];
			ambiguousCharsColumnNumberEE = new int[ambiguousCharsSizeEE];
			ambiguousCharsDefinitionEE = new CharsActiveTypeItem[ambiguousCharsSizeEE][];
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
		}		
        // ISSUE 283        
        ambiguousCharsDefinitionEE[ambiguousCharsIndexEE] = possibleDefinitions;
		ambiguousCharsSystemIdEE[ambiguousCharsIndexEE] = systemId;
		ambiguousCharsLineNumberEE[ambiguousCharsIndexEE] = lineNumber;
		ambiguousCharsColumnNumberEE[ambiguousCharsIndexEE] = columnNumber;
		ambiguousCharsDefinitionEE[ambiguousCharsIndexEE] = possibleDefinitions;
	}
    	
	public void ambiguousUnresolvedElementContentWarning(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        
		if(ambiguousUnresolvedElementSizeWW == 0){
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
		ambiguousUnresolvedElementDefinitionWW[ambiguousUnresolvedElementIndexWW] = possibleDefinitions;        
	}
	    
    public void ambiguousAmbiguousElementContentWarning(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        
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
		ambiguousAmbiguousElementDefinitionWW[ambiguousAmbiguousElementIndexWW] = possibleDefinitions;        
	}
	        
	public void ambiguousAttributeContentWarning(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions){
        
		if(ambiguousAttributeSizeWW == 0){
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
		ambiguousAttributeDefinitionWW[ambiguousAttributeIndexWW] = possibleDefinitions;
	}
	
	public void ambiguousCharsContentWarning(String systemId, 
									int lineNumber, 
									int columnNumber, 
									CharsActiveTypeItem[] possibleDefinitions){
        
		if(ambiguousCharsSizeWW == 0){
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
		ambiguousCharsDefinitionWW[ambiguousCharsIndexWW] = possibleDefinitions;
	}
		
	public void missingContent(Rule context, 
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
		//throw new IllegalStateException();
    }
	
	public void illegalContent(Rule context, 
							String startQName, 
							String startSystemId, 
							int startLineNumber, 
							int startColumnNumber){
        
		if(illegalSize == 0){
			illegalSize = 1;
			illegalIndex = 0;
			illegalContext = new APattern[illegalSize];
			illegalQName = new String[illegalSize];
			illegalStartSystemId = new String[illegalSize];			
			illegalStartLineNumber = new int[illegalSize];
			illegalStartColumnNumber = new int[illegalSize];						
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
		}
		illegalContext[illegalIndex] = context;
		illegalQName[illegalIndex] = startQName;
		illegalStartSystemId[illegalIndex] = startSystemId;
		illegalStartLineNumber[illegalIndex] = startLineNumber;
		illegalStartColumnNumber[illegalIndex] = startColumnNumber;
	}
	    
	public void undeterminedByContent(String qName, String candidateMessages){
        
		undeterminedQName = qName;
		undeterminedCandidateMessages = candidateMessages;
	}
    
    // {15}
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        
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
		datatypeErrorMessageCC[datatypeIndexCC] = datatypeErrorMessage;        
	}
	    
    //{16}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        
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
		datatypeErrorMessageAV[datatypeIndexAV] = datatypeErrorMessage;
	}
	    
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        
		if(valueSizeCC == 0){
			valueSizeCC = 1;
			valueIndexCC = 0;
			valueElementQNameCC = new String[valueSizeCC];
			valueCharsSystemIdCC = new String[valueSizeCC];
			valueCharsLineNumberCC = new int[valueSizeCC];
			valueCharsColumnNumberCC = new int[valueSizeCC];
			valueCharsDefinitionCC = new AValue[valueSizeCC];
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
		}
		valueElementQNameCC[valueIndexCC] = elementQName;
		valueCharsSystemIdCC[valueIndexCC] = charsSystemId;
		valueCharsLineNumberCC[valueIndexCC] = charsLineNumber;
		valueCharsColumnNumberCC[valueIndexCC] = columnNumber;
		valueCharsDefinitionCC[valueIndexCC] = charsDefinition;
	}
    
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        
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
		valueCharsDefinitionAV[valueIndexAV] = charsDefinition;
	}
	    
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        
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
		exceptCharsDefinitionCC[exceptIndexCC] = charsDefinition;
	}
    
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        
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
		exceptCharsDefinitionAV[exceptIndexAV] = charsDefinition;
	}

	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
        
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
		unexpectedContextDefinitionCC[unexpectedIndexCC] = elementDefinition;
	}

	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        
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
		unexpectedContextDefinitionAV[unexpectedIndexAV] = attributeDefinition;
	}

	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        
		if(ambiguousSizeCC == 0){
			ambiguousSizeCC = 1;
			ambiguousIndexCC = 0;		
			ambiguousCharsSystemIdEECC = new String[ambiguousSizeCC];
			ambiguousCharsLineNumberEECC = new int[ambiguousSizeCC];
			ambiguousCharsColumnNumberEECC = new int[ambiguousSizeCC];
			ambiguousPossibleDefinitionsCC = new CharsActiveTypeItem[ambiguousSizeCC][];
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
		}
		ambiguousCharsSystemIdEECC[ambiguousIndexCC] = systemId;
		ambiguousCharsLineNumberEECC[ambiguousIndexCC] = lineNumber;
		ambiguousCharsColumnNumberEECC[ambiguousIndexCC] = columnNumber;
		ambiguousPossibleDefinitionsCC[ambiguousIndexCC] = possibleDefinitions;
	}

    
	// {24}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        
		if(ambiguousSizeAV == 0){
			ambiguousSizeAV = 1;
			ambiguousIndexAV = 0;
			ambiguousAttributeQNameEEAV = new String[ambiguousSizeAV];
			ambiguousCharsSystemIdEEAV = new String[ambiguousSizeAV];
			ambiguousCharsLineNumberEEAV = new int[ambiguousSizeAV];
			ambiguousCharsColumnNumberEEAV = new int[ambiguousSizeAV];
			ambiguousPossibleDefinitionsAV = new CharsActiveTypeItem[ambiguousSizeAV][];
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
		}
		ambiguousAttributeQNameEEAV[ambiguousIndexAV] = attributeQName;
		ambiguousCharsSystemIdEEAV[ambiguousIndexAV] = systemId;
		ambiguousCharsLineNumberEEAV[ambiguousIndexAV] = lineNumber;
		ambiguousCharsColumnNumberEEAV[ambiguousIndexAV] = columnNumber;
		ambiguousPossibleDefinitionsAV[ambiguousIndexAV] = possibleDefinitions;
	}

    
    // {25}
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        
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
		datatypeErrorMessageLP[datatypeIndexLP] = datatypeErrorMessage;
	}

        
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        
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
		valueCharsDefinitionLP[valueIndexLP] = charsDefinition;
	}

    
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        
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
		exceptCharsDefinitionLP[exceptIndexLP] = charsDefinition;
	}

    
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        
		if(ambiguousSizeLP == 0){
			ambiguousSizeLP = 1;
			ambiguousIndexLP = 0;
			ambiguousTokenLP = new String[ambiguousSizeLP];
			ambiguousCharsSystemIdEELP = new String[ambiguousSizeLP];
			ambiguousCharsLineNumberEELP = new int[ambiguousSizeLP];
			ambiguousCharsColumnNumberEELP = new int[ambiguousSizeLP];
			ambiguousPossibleDefinitionsLP = new CharsActiveTypeItem[ambiguousSizeLP][];
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
		}
		ambiguousTokenLP[ambiguousIndexLP] = token;
		ambiguousCharsSystemIdEELP[ambiguousIndexLP] = systemId;
		ambiguousCharsLineNumberEELP[ambiguousIndexLP] = lineNumber;
		ambiguousCharsColumnNumberEELP[ambiguousIndexLP] = columnNumber;
		ambiguousPossibleDefinitionsLP[ambiguousIndexLP] = possibleDefinitions;
	}

	
    public void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        
        if(ambiguousSizeLPICE == 0){
			ambiguousSizeLPICE = 1;
			ambiguousIndexLPICE = 0;
			ambiguousTokenLPICE = new String[ambiguousSizeLPICE];
			ambiguousCharsSystemIdEELPICE = new String[ambiguousSizeLPICE];
			ambiguousCharsLineNumberEELPICE = new int[ambiguousSizeLPICE];
			ambiguousCharsColumnNumberEELPICE = new int[ambiguousSizeLPICE];
			ambiguousPossibleDefinitionsLPICE = new CharsActiveTypeItem[ambiguousSizeLPICE][];
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
		}
		ambiguousTokenLPICE[ambiguousIndexLPICE] = token;
		ambiguousCharsSystemIdEELPICE[ambiguousIndexLPICE] = systemId;
		ambiguousCharsLineNumberEELPICE[ambiguousIndexLPICE] = lineNumber;
		ambiguousCharsColumnNumberEELPICE[ambiguousIndexLPICE] = columnNumber;
		ambiguousPossibleDefinitionsLPICE[ambiguousIndexLPICE] = possibleDefinitions;
    }
    
    
    public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        
        if(ambiguousSizeLPICW == 0){
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
		ambiguousPossibleDefinitionsLPICW[ambiguousIndexLPICW] = possibleDefinitions;
    }    
    
    
	public void missingCompositorContent(Rule context, 
								String startSystemId, 
								int startLineNumber, 
								int startColumnNumber,								 
								APattern definition, 
								int expected, 
								int found){
        
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
				
	}	
        
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
        conflict = true;
        this.conflictResolutionId = conflictResolutionId;
        this.candidatesCount = candidatesCount;
        this.commonMessages = commonMessages;
        this.disqualified = disqualified;
        this.candidateMessages = candidateMessages;
    }
    
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    internalConflict = true;
	    this.conflictMessageReporter = conflictMessageReporter;
	}
	
    public void transferErrorMessages(ErrorCatcher errorCatcher){
		// {2}
        String message = "";
		if(unknownElementIndex >= 0){
			for(int i = 0; i <= unknownElementIndex; i++){
				errorCatcher.unknownElement(unknownElementQName[i],
				                        unknownElementSystemId[i],
				                        unknownElementLineNumber[i],
				                        unknownElementColumnNumber[i]);
			}
		}	
		// {3}
		if(unexpectedElementIndex >= 0){
			for(int i = 0; i <= unexpectedElementIndex; i++){
				errorCatcher.unexpectedElement(unexpectedElementQName[i],
                                        unexpectedElementDefinition[i],
                                        unexpectedElementSystemId[i],
                                        unexpectedElementLineNumber[i],
                                        unexpectedElementColumnNumber[i]);
			}
		}
		// {4}
		if(unexpectedAmbiguousElementIndex  >= 0){
			for(int i = 0; i <= unexpectedAmbiguousElementIndex; i++){
				errorCatcher.unexpectedAmbiguousElement(unexpectedAmbiguousElementQName[i],
                                            unexpectedAmbiguousElementDefinition[i],
                                            unexpectedAmbiguousElementSystemId[i],
                                            unexpectedAmbiguousElementLineNumber[i],
                                            unexpectedAmbiguousElementColumnNumber[i]);
			}
		}
		// {5}
		if(unknownAttributeIndex  >= 0){
			for(int i = 0; i <= unknownAttributeIndex; i++){
				errorCatcher.unknownAttribute(unknownAttributeQName[i],
                                        unknownAttributeSystemId[i],
                                        unknownAttributeLineNumber[i],
                                        unknownAttributeColumnNumber[i]);
			}
		}	
		// {6}
		if(unexpectedAttributeIndex  >= 0){
			for(int i = 0; i <= unexpectedAttributeIndex; i++){
				errorCatcher.unexpectedAttribute(unexpectedAttributeQName[i],
                                        unexpectedAttributeDefinition[i],
                                        unexpectedAttributeSystemId[i],
                                        unexpectedAttributeLineNumber[i],
                                        unexpectedAttributeColumnNumber[i]);
			}
		}
		// {7}
		if(unexpectedAmbiguousAttributeIndex >= 0){
			for(int i = 0; i <= unexpectedAmbiguousAttributeIndex; i++){
				errorCatcher.unexpectedAmbiguousAttribute(unexpectedAmbiguousAttributeQName[i],
                                                    unexpectedAmbiguousAttributeDefinition[i],
                                                    unexpectedAmbiguousAttributeSystemId[i],
                                                    unexpectedAmbiguousAttributeLineNumber[i],
                                                    unexpectedAmbiguousAttributeColumnNumber[i]);
			}
		}
		
		// {8}
		if(misplacedIndex  >= 0){
		    APattern[] sourceDefinition = null;
		    APattern reper = null;
			for(int i = 0; i <= misplacedIndex; i++){
			    for(int j = 0; j < misplacedQName.length; j++){
                    errorCatcher.misplacedElement(misplacedContext[i],
                                            misplacedStartSystemId[i],
                                            misplacedStartLineNumber[i],
                                            misplacedStartColumnNumber[i],
                                            misplacedDefinition[i][j],
                                            misplacedQName[i][j],
                                            misplacedSystemId[i][j],
                                            misplacedLineNumber[i][j],
                                            misplacedColumnNumber[i][j],
                                            sourceDefinition,
                                            reper);
                }
			}
		}
		
		// {9}
		if(excessiveIndex  >= 0){
			for(int i = 0; i <= excessiveIndex; i++){
				errorCatcher.excessiveContent(excessiveContext[i],
                                        excessiveStartSystemId[i],
                                        excessiveStartLineNumber[i],
                                        excessiveStartColumnNumber[i],
                                        excessiveDefinition[i],
                                        excessiveQName[i],
                                        excessiveSystemId[i],
                                        excessiveLineNumber[i],
                                        excessiveColumnNumber[i]);
			}
		}
		// {10}
		if(missingIndex >= 0){
			for(int i = 0; i <= missingIndex; i++){
				errorCatcher.missingContent(missingContext[i],
                                        missingStartSystemId[i],
                                        missingStartLineNumber[i],
                                        missingStartColumnNumber[i],
                                        missingDefinition[i],
                                        missingExpected[i],
                                        missingFound[i],
                                        missingQName[i],
                                        missingSystemId[i],
                                        missingLineNumber[i],
                                        missingColumnNumber[i]);
			}			
		}		
		// {11}
		if(illegalIndex >= 0){
			for(int i = 0; i <= illegalIndex; i++){
				errorCatcher.illegalContent(illegalContext[i],
                                        illegalQName[i],
                                        illegalStartSystemId[i],
                                        illegalStartLineNumber[i],
                                        illegalStartColumnNumber[i]);
			}			
		}
		// {12 A}
		if(unresolvedAmbiguousElementIndexEE >= 0){
			for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
				errorCatcher.unresolvedAmbiguousElementContentError(unresolvedAmbiguousElementQNameEE[i],
                                                    unresolvedAmbiguousElementSystemIdEE[i],
                                                    unresolvedAmbiguousElementLineNumberEE[i],
                                                    unresolvedAmbiguousElementColumnNumberEE[i],
                                                    unresolvedAmbiguousElementDefinitionEE[i]);
			}
		}
		// {12 U}
		if(unresolvedUnresolvedElementIndexEE >= 0){
			for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
				errorCatcher.unresolvedUnresolvedElementContentError(unresolvedUnresolvedElementQNameEE[i],
                                                                unresolvedUnresolvedElementSystemIdEE[i],
                                                                unresolvedUnresolvedElementLineNumberEE[i],
                                                                unresolvedUnresolvedElementColumnNumberEE[i],
                                                                unresolvedUnresolvedElementDefinitionEE[i]);
			}
		}
		// {13}
		if(unresolvedAttributeIndexEE >= 0){
			for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
				errorCatcher.unresolvedAttributeContentError(unresolvedAttributeQNameEE[i],
                                                        unresolvedAttributeSystemIdEE[i],
                                                        unresolvedAttributeLineNumberEE[i],
                                                        unresolvedAttributeColumnNumberEE[i],
                                                        unresolvedAttributeDefinitionEE[i]);
			}
		}
		// {14}
		if(ambiguousCharsIndexEE >= 0){
			for(int i = 0; i <= ambiguousCharsIndexEE; i++){
				errorCatcher.ambiguousCharsContentError(ambiguousCharsSystemIdEE[i],
                                                    ambiguousCharsLineNumberEE[i],
                                                    ambiguousCharsColumnNumberEE[i],
                                                    ambiguousCharsDefinitionEE[i]);
			}
		}
		// {15}
		if(datatypeIndexCC >= 0){
			for(int i = 0; i <= datatypeIndexCC; i++){
				errorCatcher.characterContentDatatypeError(datatypeElementQNameCC[i],
                                                        datatypeCharsSystemIdCC[i],
                                                        datatypeCharsLineNumberCC[i],
                                                        datatypeCharsColumnNumberCC[i],
                                                        datatypeCharsDefinitionCC[i],
                                                        datatypeErrorMessageCC[i]);
			}
		}
		// {16}
		if(datatypeIndexAV >= 0){
		    for(int i = 0; i <= datatypeIndexAV; i++){
		        errorCatcher.attributeValueDatatypeError(datatypeAttributeQNameAV[i],
                                                    datatypeCharsSystemIdAV[i],
                                                    datatypeCharsLineNumberAV[i],
                                                    datatypeCharsColumnNumberAV[i],
                                                    datatypeCharsDefinitionAV[i],
                                                    datatypeErrorMessageAV[i]);
            }
		}
		// {17}
		if(valueIndexCC >= 0){
			for(int i = 0; i <= valueIndexCC; i++){
			    errorCatcher.characterContentValueError(valueElementQNameCC[i],
                                                    valueCharsSystemIdCC[i],
                                                    valueCharsLineNumberCC[i],
                                                    valueCharsColumnNumberCC[i],
                                                    valueCharsDefinitionCC[i]);	
			}
		}
		// {18}
		if(valueIndexAV >= 0){
			for(int i = 0; i <= valueIndexAV; i++){
				errorCatcher.attributeValueValueError(valueAttributeQNameAV[i],
                                                    valueCharsSystemIdAV[i],
                                                    valueCharsLineNumberAV[i],
                                                    valueCharsColumnNumberAV[i],
                                                    valueCharsDefinitionAV[i]);
			}
		}
		// {19}
		if(exceptIndexCC >= 0){
			for(int i = 0; i <= exceptIndexCC; i++){
				errorCatcher.characterContentExceptedError(exceptElementQNameCC[i],
                                                        exceptCharsSystemIdCC[i],
                                                        exceptCharsLineNumberCC[i],
                                                        exceptCharsColumnNumberCC[i],
                                                        exceptCharsDefinitionCC[i]);
			}
		}
		// {20}
		if(exceptIndexAV >= 0){
			for(int i = 0; i <= exceptIndexAV; i++){
				errorCatcher.attributeValueExceptedError(exceptAttributeQNameAV[i],
                                                        exceptCharsSystemIdAV[i],
                                                        exceptCharsLineNumberAV[i],
                                                        exceptCharsColumnNumberAV[i],
                                                        exceptCharsDefinitionAV[i]);
			}
		}
		// {21}
		if(unexpectedIndexCC >= 0){
			for(int i = 0; i <= unexpectedIndexCC; i++){
				errorCatcher.unexpectedCharacterContent(unexpectedCharsSystemIdCC[i],
                                                    unexpectedCharsLineNumberCC[i],
                                                    unexpectedCharsColumnNumberCC[i],
                                                    unexpectedContextDefinitionCC[i]);
			}
		}
		// {22}
		if(unexpectedIndexAV >= 0){
			for(int i = 0; i <= unexpectedIndexAV; i++){
				errorCatcher.unexpectedAttributeValue(unexpectedCharsSystemIdAV[i],
                                                    unexpectedCharsLineNumberAV[i],
                                                    unexpectedCharsColumnNumberAV[i],
                                                    unexpectedContextDefinitionAV[i]);
			}
		}
		// {23}
		if(ambiguousIndexCC >= 0){
			for(int i = 0; i <= ambiguousIndexCC; i++){
				errorCatcher.unresolvedCharacterContent(ambiguousCharsSystemIdEECC[i],
                                                    ambiguousCharsLineNumberEECC[i],
                                                    ambiguousCharsColumnNumberEECC[i],
                                                    ambiguousPossibleDefinitionsCC[i]);
			}
		}
		// {24}
		if(ambiguousIndexAV >= 0){			
			for(int i = 0; i <= ambiguousIndexAV; i++){				
				errorCatcher.unresolvedAttributeValue(ambiguousAttributeQNameEEAV[i],
				                                    ambiguousCharsSystemIdEEAV[i],
                                                    ambiguousCharsLineNumberEEAV[i],
                                                    ambiguousCharsColumnNumberEEAV[i],
                                                    ambiguousPossibleDefinitionsAV[i]);
			}
		}
		// {25}
		if(datatypeIndexLP >= 0){
			for(int i = 0; i <= datatypeIndexLP; i++){
				errorCatcher.listTokenDatatypeError(datatypeTokenLP[i],
                                                    datatypeCharsSystemIdLP[i],
                                                    datatypeCharsLineNumberLP[i],
                                                    datatypeCharsColumnNumberLP[i],
                                                    datatypeCharsDefinitionLP[i],
                                                    datatypeErrorMessageLP[i]);
			}
		}
		// {26}
		if(valueIndexLP >= 0){
			for(int i = 0; i <= valueIndexLP; i++){
				errorCatcher.listTokenValueError(valueTokenLP[i],
                                                valueCharsSystemIdLP[i],
                                                valueCharsLineNumberLP[i],
                                                valueCharsColumnNumberLP[i],
                                                valueCharsDefinitionLP[i]);
			}
		}
		// {27}
		if(exceptIndexLP >= 0){
			for(int i = 0; i <= exceptIndexLP; i++){
				errorCatcher.listTokenExceptedError(exceptTokenLP[i],
                                                exceptCharsSystemIdLP[i],
                                                exceptCharsLineNumberLP[i],
                                                exceptCharsColumnNumberLP[i],
                                                exceptCharsDefinitionLP[i]);
			}
		}
		// {28}
		if(ambiguousIndexLP >= 0){
			for(int i = 0; i <= ambiguousIndexLP; i++){
				errorCatcher.ambiguousListToken(ambiguousTokenLP[i],
                                            ambiguousCharsSystemIdEELP[i],
                                            ambiguousCharsLineNumberEELP[i],
                                            ambiguousCharsColumnNumberEELP[i],
                                            ambiguousPossibleDefinitionsLP[i]);
			}
		}
        
        // {28_1}
        if(ambiguousIndexLPICE >= 0){
			for(int i = 0; i <= ambiguousIndexLPICE; i++){
				errorCatcher.ambiguousListTokenInContextError(ambiguousTokenLPICE[i],
                                                        ambiguousCharsSystemIdEELPICE[i],
                                                        ambiguousCharsLineNumberEELPICE[i],
                                                        ambiguousCharsColumnNumberEELPICE[i],
                                                        ambiguousPossibleDefinitionsLPICE[i]);
			}
		}
		
		// {29}
		if(missingCompositorContentIndex >= 0){
			for(int i = 0; i <= missingCompositorContentIndex; i++){
				errorCatcher.missingCompositorContent(missingCompositorContentContext[i],
                                                missingCompositorContentStartSystemId[i],
                                                missingCompositorContentStartLineNumber[i],
                                                missingCompositorContentStartColumnNumber[i],
                                                missingCompositorContentDefinition[i],
                                                missingCompositorContentExpected[i],
                                                missingCompositorContentFound[i]);
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
				errorCatcher.ambiguousUnresolvedElementContentWarning(ambiguousUnresolvedElementQNameWW[i],
                                                                    ambiguousUnresolvedElementSystemIdWW[i],
                                                                    ambiguousUnresolvedElementLineNumberWW[i],
                                                                    ambiguousUnresolvedElementColumnNumberWW[i],
                                                                    ambiguousUnresolvedElementDefinitionWW[i]);				
			}
		}
		// {w1 A}
		if(ambiguousAmbiguousElementIndexWW >= 0){
			for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
				errorCatcher.ambiguousAmbiguousElementContentWarning(ambiguousAmbiguousElementQNameWW[i],
                                                                ambiguousAmbiguousElementSystemIdWW[i],
                                                                ambiguousAmbiguousElementLineNumberWW[i],
                                                                ambiguousAmbiguousElementColumnNumberWW[i],
                                                                ambiguousAmbiguousElementDefinitionWW[i]);				
			}
		}
		
		// {w2}
		if(ambiguousAttributeIndexWW >= 0){
			for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
				errorCatcher.ambiguousAttributeContentWarning(ambiguousAttributeQNameWW[i],
                                                            ambiguousAttributeSystemIdWW[i],
                                                            ambiguousAttributeLineNumberWW[i],
                                                            ambiguousAttributeColumnNumberWW[i],
                                                            ambiguousAttributeDefinitionWW[i]);
			}
		}
		// {w3}
		if(ambiguousCharsIndexWW >= 0){
			for(int i = 0; i <= ambiguousCharsIndexWW; i++){
				errorCatcher.ambiguousCharsContentWarning(ambiguousCharsSystemIdWW[i],
                                                    ambiguousCharsLineNumberWW[i],
                                                    ambiguousCharsColumnNumberWW[i],
                                                    ambiguousCharsDefinitionWW[i]);
			}
		}
        
        // {28_2}
        if(ambiguousIndexLPICW >= 0){
			for(int i = 0; i <= ambiguousIndexLPICW; i++){
				errorCatcher.ambiguousListTokenInContextWarning(ambiguousTokenLPICW[i],
                                                            ambiguousCharsSystemIdEELPICW[i],
                                                            ambiguousCharsLineNumberEELPICW[i],
                                                            ambiguousCharsColumnNumberEELPICW[i],
                                                            ambiguousPossibleDefinitionsLPICW[i]);
			}
		}
	}	
    
	public void transferMessages(ErrorCatcher errorCatcher){
	    transferErrorMessages(errorCatcher);
	    transferWarningMessages(errorCatcher);
	}
	
    public String toString(){
        return "TemporaryMessageStorage ";
    }
    
}
