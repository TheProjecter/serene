/*
Copyright 2010 Radu Cernuta 

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

import sereneWrite.MessageWriter;

public class ContextMessageHandler implements ErrorCatcher{	
	String qName;
	AElement definition;
	String systemId;
	int lineNumber;
	int columnNumber;

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
	AElement[] unexpectedElementDefinition;
	String[] unexpectedElementSystemId;
	int[] unexpectedElementLineNumber;
	int[] unexpectedElementColumnNumber;
	int unexpectedElementIndex;
	int unexpectedElementSize;
	
	// {4}
	String[] unexpectedAmbiguousElementQName;
	AElement[][] unexpectedAmbiguousElementDefinition;
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
	AAttribute[] unexpectedAttributeDefinition;
	String[] unexpectedAttributeSystemId;
	int[] unexpectedAttributeLineNumber;
	int[] unexpectedAttributeColumnNumber;
	int unexpectedAttributeIndex;
	int unexpectedAttributeSize;
	
	// {7}
	String[] unexpectedAmbiguousAttributeQName;
	AAttribute[][] unexpectedAmbiguousAttributeDefinition;
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
	
	// {12}
	String[] ambiguousElementQNameEE;
	String[] ambiguousElementSystemIdEE;
	int[] ambiguousElementLineNumberEE;
	int[] ambiguousElementColumnNumberEE;
	AElement[][] ambiguousElementDefinitionEE;
	int ambiguousElementIndexEE;
	int ambiguousElementSizeEE;

	// {13}
	String[] ambiguousAttributeQNameEE;
	String[] ambiguousAttributeSystemIdEE;
	int[] ambiguousAttributeLineNumberEE;
	int[] ambiguousAttributeColumnNumberEE;
	AAttribute[][] ambiguousAttributeDefinitionEE;
	int ambiguousAttributeIndexEE;
	int ambiguousAttributeSizeEE;

	// {14}
	String[] ambiguousCharsSystemIdEE;
	int[] ambiguousCharsLineNumberEE;
	int[] ambiguousCharsColumnNumberEE;
	CharsActiveTypeItem[][] ambiguousCharsDefinitionEE;
	int ambiguousCharsIndexEE;
	int ambiguousCharsSizeEE;
	
	
	// {w1}
	String[] ambiguousElementQNameWW;
	String[] ambiguousElementSystemIdWW;
	int[] ambiguousElementLineNumberWW;
	int[] ambiguousElementColumnNumberWW;
	AElement[][] ambiguousElementDefinitionWW;
	int ambiguousElementIndexWW;
	int ambiguousElementSizeWW;

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
	
	MessageWriter debugWriter;
	
	public ContextMessageHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
				
		unknownElementSize = 0;
		unexpectedElementSize = 0;
		unexpectedAmbiguousElementSize = 0;
		misplacedSize = 0;
		excessiveSize = 0;		
		missingSize = 0;	
		illegalSize = 0;
		unknownAttributeSize = 0;		
		unexpectedAttributeSize = 0;
		unexpectedAmbiguousAttributeSize = 0;
		ambiguousElementSizeEE = 0;
		ambiguousAttributeSizeEE = 0;
		ambiguousCharsSizeEE = 0;
		
		ambiguousElementSizeWW = 0;
		ambiguousAttributeSizeWW = 0;
		ambiguousCharsSizeWW = 0;
		
		
		datatypeSizeCC = 0;
		datatypeSizeAV = 0;
		valueSizeCC = 0;
		valueSizeAV = 0;
		exceptSizeCC = 0;
		exceptSizeAV = 0;
		unexpectedSizeCC = 0;
		unexpectedSizeAV = 0;
		ambiguousSizeCC = 0;
		ambiguousSizeAV = 0;
		datatypeSizeLP = 0;
		valueSizeLP = 0;
		exceptSizeLP = 0;
		ambiguousSizeLP = 0;
		
		missingCompositorContentSize = 0;
	}	
	
	public void setContextQName(String qName){
		this.qName = qName;
	}	
	public void setContextLocation(String systemId, int lineNumber, int columnNumber){
		this.systemId = systemId;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;		
	}
	public void setContextDefinition(AElement definition){
		this.definition = definition;
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
		
		String message = "Unknown element in content. Element "
						+qName
						+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" is not known in the vocabulary described by the schema." ;
	}
	
	
	public void unexpectedElement(String qName, AElement definition, String systemId, int lineNumber, int columnNumber){
		if(unexpectedElementSize == 0){
			unexpectedElementSize = 1;
			unexpectedElementIndex = 0;	
			unexpectedElementQName = new String[unexpectedElementSize];
			unexpectedElementDefinition = new AElement[unexpectedElementSize];
			unexpectedElementSystemId = new String[unexpectedElementSize];			
			unexpectedElementLineNumber = new int[unexpectedElementSize];
			unexpectedElementColumnNumber = new int[unexpectedElementSize];			
		}else if(++unexpectedElementIndex == unexpectedElementSize){			
			String[] increasedQN = new String[++unexpectedElementSize];
			System.arraycopy(unexpectedElementQName, 0, increasedQN, 0, unexpectedElementIndex);
			unexpectedElementQName = increasedQN;
			
			AElement[] increasedDef = new AElement[unexpectedElementSize];
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
		
		String message = "Unexpected element in content. Element "
						+qName+" corresponding to definition "+definition.toString()
						+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" is not known in the vocabulary described by the schema." ;
	}
	
	
	
	public void unexpectedAmbiguousElement(String qName, AElement[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
		if(unexpectedAmbiguousElementSize == 0){
			unexpectedAmbiguousElementSize = 1;
			unexpectedAmbiguousElementIndex = 0;	
			unexpectedAmbiguousElementQName = new String[unexpectedAmbiguousElementSize];
			unexpectedAmbiguousElementDefinition = new AElement[unexpectedAmbiguousElementSize][];
			unexpectedAmbiguousElementSystemId = new String[unexpectedAmbiguousElementSize];			
			unexpectedAmbiguousElementLineNumber = new int[unexpectedAmbiguousElementSize];
			unexpectedAmbiguousElementColumnNumber = new int[unexpectedAmbiguousElementSize];			
		}else if(++unexpectedAmbiguousElementIndex == unexpectedAmbiguousElementSize){			
			String[] increasedQN = new String[++unexpectedAmbiguousElementSize];
			System.arraycopy(unexpectedAmbiguousElementQName, 0, increasedQN, 0, unexpectedAmbiguousElementIndex);
			unexpectedAmbiguousElementQName = increasedQN;
			
			AElement[][] increasedDef = new AElement[unexpectedAmbiguousElementSize][];
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
		
		String message = "Unexpected element in content. Element "
						+qName+" corresponding to one of definitions "+Arrays.toString(possibleDefinitions)
						+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" is not known in the vocabulary described by the schema." ;
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
		
		String message = "Unknown attribute in content. Attribute "
						+qName
						+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" is not known in the vocabulary described by the schema." ;
	}
	
	
	public void unexpectedAttribute(String qName, AAttribute definition, String systemId, int lineNumber, int columnNumber){
		if(unexpectedAttributeSize == 0){
			unexpectedAttributeSize = 1;
			unexpectedAttributeIndex = 0;	
			unexpectedAttributeQName = new String[unexpectedAttributeSize];
			unexpectedAttributeDefinition = new AAttribute[unexpectedAttributeSize];
			unexpectedAttributeSystemId = new String[unexpectedAttributeSize];			
			unexpectedAttributeLineNumber = new int[unexpectedAttributeSize];
			unexpectedAttributeColumnNumber = new int[unexpectedAttributeSize];			
		}else if(++unexpectedAttributeIndex == unexpectedAttributeSize){			
			String[] increasedQN = new String[++unexpectedAttributeSize];
			System.arraycopy(unexpectedAttributeQName, 0, increasedQN, 0, unexpectedAttributeIndex);
			unexpectedAttributeQName = increasedQN;
			
			AAttribute[] increasedDef = new AAttribute[unexpectedAttributeSize];
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
		
		String message = "Unexpected attribute in content. Attribute "
						+qName+" corresponding to definition "+definition.toString()
						+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" is not known in the vocabulary described by the schema." ;
	}
	
	
	
	public void unexpectedAmbiguousAttribute(String qName, AAttribute[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
		if(unexpectedAmbiguousAttributeSize == 0){
			unexpectedAmbiguousAttributeSize = 1;
			unexpectedAmbiguousAttributeIndex = 0;	
			unexpectedAmbiguousAttributeQName = new String[unexpectedAmbiguousAttributeSize];
			unexpectedAmbiguousAttributeDefinition = new AAttribute[unexpectedAmbiguousAttributeSize][];
			unexpectedAmbiguousAttributeSystemId = new String[unexpectedAmbiguousAttributeSize];			
			unexpectedAmbiguousAttributeLineNumber = new int[unexpectedAmbiguousAttributeSize];
			unexpectedAmbiguousAttributeColumnNumber = new int[unexpectedAmbiguousAttributeSize];			
		}else if(++unexpectedAmbiguousAttributeIndex == unexpectedAmbiguousAttributeSize){			
			String[] increasedQN = new String[++unexpectedAmbiguousAttributeSize];
			System.arraycopy(unexpectedAmbiguousAttributeQName, 0, increasedQN, 0, unexpectedAmbiguousAttributeIndex);
			unexpectedAmbiguousAttributeQName = increasedQN;
			
			AAttribute[][] increasedDef = new AAttribute[unexpectedAmbiguousAttributeSize][];
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
		
		String message = "Unexpected attribute in content. Attribute "
						+qName+" corresponding to one of definitions "+Arrays.toString(possibleDefinitions)
						+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" is not known in the vocabulary described by the schema." ;
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
			System.arraycopy(misplacedContext, 0, increasedCDef, 0, misplacedIndex);
			misplacedContext = increasedCDef;
			misplacedContext[++misplacedIndex] = contextDefinition;
			
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
		String message = "";		
		for(int i = 0; i < misplacedSize; i++){
			 message = "Order error."+
			 "\nMisplaced elements within the context of "+misplacedContext[i] + " starting at "+misplacedStartSystemId[i]+":"+misplacedStartLineNumber[i]+":"+misplacedStartColumnNumber[misplacedIndex]+":";
			for(int j = 0; j < misplacedDefinition[i].length; j++){
				for(int k = 0; k < misplacedQName[i][j].length; k++){
					message += "\n"+misplacedQName[i][j][k]+" at "+misplacedSystemId[i][j][k]+":"+misplacedLineNumber[i][j][k]+":"+misplacedColumnNumber[i][j][k];
				}
				message += ", corresponding to definition "+misplacedDefinition[i][j];
			}
			message += ".";
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
		String message = "Excessive content in "+context+", starting at "+startSystemId+":"+startLineNumber+":"+startColumnNumber+"."
		+" Expected "+definition.getMaxOccurs()+" "+definition+", found "+qName.length+": "
		+excessive;
	}
	public void excessiveContent(Rule context, 
								APattern definition, 
								String qName, 
								String systemId, 
								int lineNumber,		
								int columnNumber){
		String excessive = "";
		String message = "";
		for(int i = 0; i < excessiveSize; i++){
			if(excessiveContext[i].equals(context) &&
				excessiveDefinition[i].equals(definition)){
			
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
				
				for(int j = 0; j < excessiveQName.length; j++){
					excessive+="\n"+excessiveSystemId[i][j]+":"+excessiveLineNumber[i][j]+":"+excessiveColumnNumber[i][j]+":"+excessiveQName[i][j];
				}
				excessive.trim();
				message = "Excessive content in "+context+", starting at "+excessiveStartSystemId[i]+":"+excessiveStartLineNumber[i]+":"+excessiveStartColumnNumber[i]+"."
				+" Expected "+definition.getMaxOccurs()+" "+definition+", found "+excessiveQName[i].length+": "
				+excessive;
				
				break;
			}
		}		
	}
	
	public void ambiguousElementContentError(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
		if(ambiguousElementSizeEE == 0){
			ambiguousElementSizeEE = 1;
			ambiguousElementIndexEE = 0;	
			ambiguousElementQNameEE = new String[ambiguousElementSizeEE];			
			ambiguousElementSystemIdEE = new String[ambiguousElementSizeEE];			
			ambiguousElementLineNumberEE = new int[ambiguousElementSizeEE];
			ambiguousElementColumnNumberEE = new int[ambiguousElementSizeEE];
			ambiguousElementDefinitionEE = new AElement[ambiguousElementSizeEE][];
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
		}
		ambiguousElementQNameEE[ambiguousElementIndexEE] = qName;		
		ambiguousElementSystemIdEE[ambiguousElementIndexEE] = systemId;
		ambiguousElementLineNumberEE[ambiguousElementIndexEE] = lineNumber;
		ambiguousElementColumnNumberEE[ambiguousElementIndexEE] = columnNumber;
		ambiguousElementDefinitionEE[ambiguousElementIndexEE] = possibleDefinitions;	
		
		
		String message = "Ambiguous content. Element <"+qName+"> at "+systemId+":"+lineNumber+":"+columnNumber
						+" cannot be uniquely resolved to one definition. Candidates "+Arrays.toString(possibleDefinitions);
	}
	
	public void ambiguousAttributeContentError(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions){
		if(ambiguousAttributeSizeEE == 0){
			ambiguousAttributeSizeEE = 1;
			ambiguousAttributeIndexEE = 0;	
			ambiguousAttributeQNameEE = new String[ambiguousAttributeSizeEE];			
			ambiguousAttributeSystemIdEE = new String[ambiguousAttributeSizeEE];			
			ambiguousAttributeLineNumberEE = new int[ambiguousAttributeSizeEE];
			ambiguousAttributeColumnNumberEE = new int[ambiguousAttributeSizeEE];
			ambiguousAttributeDefinitionEE = new AAttribute[ambiguousAttributeSizeEE][];
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
		}
		ambiguousAttributeQNameEE[ambiguousAttributeIndexEE] = qName;		
		ambiguousAttributeSystemIdEE[ambiguousAttributeIndexEE] = systemId;
		ambiguousAttributeLineNumberEE[ambiguousAttributeIndexEE] = lineNumber;
		ambiguousAttributeColumnNumberEE[ambiguousAttributeIndexEE] = columnNumber;
		ambiguousAttributeDefinitionEE[ambiguousAttributeIndexEE] = possibleDefinitions;	
		
		
		String message = "Ambiguous content. Attribute "
						+qName+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" cannot be uniquely resolved to one definition. Candidates "+Arrays.toString(possibleDefinitions);
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
		ambiguousCharsSystemIdEE[ambiguousCharsIndexEE] = systemId;
		ambiguousCharsLineNumberEE[ambiguousCharsIndexEE] = lineNumber;
		ambiguousCharsColumnNumberEE[ambiguousCharsIndexEE] = columnNumber;
		ambiguousCharsDefinitionEE[ambiguousCharsIndexEE] = possibleDefinitions;
	}
	
	
	
	public void ambiguousElementContentWarning(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
		if(ambiguousElementSizeWW == 0){
			ambiguousElementSizeWW = 1;
			ambiguousElementIndexWW = 0;	
			ambiguousElementQNameWW = new String[ambiguousElementSizeWW];			
			ambiguousElementSystemIdWW = new String[ambiguousElementSizeWW];			
			ambiguousElementLineNumberWW = new int[ambiguousElementSizeWW];
			ambiguousElementColumnNumberWW = new int[ambiguousElementSizeWW];
			ambiguousElementDefinitionWW = new AElement[ambiguousElementSizeWW][];
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
		}
		ambiguousElementQNameWW[ambiguousElementIndexWW] = qName;		
		ambiguousElementSystemIdWW[ambiguousElementIndexWW] = systemId;
		ambiguousElementLineNumberWW[ambiguousElementIndexWW] = lineNumber;
		ambiguousElementColumnNumberWW[ambiguousElementIndexWW] = columnNumber;
		ambiguousElementDefinitionWW[ambiguousElementIndexWW] = possibleDefinitions;	
		
		
		String message = "Ambiguous content. Element <"+qName+"> at "+systemId+":"+lineNumber+":"+columnNumber
						+" cannot be uniquely resolved to one definition. Candidates "+Arrays.toString(possibleDefinitions);
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
		
		
		String message = "Ambiguous content. Attribute "
						+qName+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" cannot be uniquely resolved to one definition. Candidates "+Arrays.toString(possibleDefinitions);
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
		
		
		String message = "Ambiguous content. Chars "
						+qName+" at "+systemId+":"+lineNumber+":"+columnNumber
						+" cannot be uniquely resolved to one definition. Candidates "+Arrays.toString(possibleDefinitions);
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
				
		String message = "";
		if(found > 0){
			String missing = "";
			for(int i = 0; i < qName.length; i++){
				missing+="\n"+systemId[i]+":"+lineNumber[i]+":"+columnNumber[i]+":"+qName[i];
			}
			missing.trim();
			message = "Missing content in "+context+", starting at "+startSystemId+":"+startLineNumber+":"+startColumnNumber+"."
			+" Expected "+expected+" "+definition+", found "+found+": "
			+missing+".";
		}else{
			message = "Missing content in "+context+", starting at "+startSystemId+":"+startLineNumber+":"+startColumnNumber+"."
			+" Expected "+expected+" "+definition+", found "+found+".";
		}
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
				
		String message = "";
		message = "Illegal content in "+context+", starting at "+startSystemId+":"+startLineNumber+":"+startColumnNumber+".";
		
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		undeterminedQName = qName;
		undeterminedCandidateMessages = candidateMessages; 
		
		String message = " Element "+undeterminedQName+" could not be resolved to a single schema definition:"
						+"\nUnresolved by content."
						+"\nValidation  of candidate definitions resulted in errors:"
						+ undeterminedCandidateMessages;
	}
	
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
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
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
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
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
				
		String message = "";
		if(found > 0){			
			message = "Missing content in "+context+", starting at "+startSystemId+":"+startLineNumber+":"+startColumnNumber+"."
			+" Expected "+expected+" "+definition+", found "+found+".";
		}else{
			message = "Missing content in "+context+", starting at "+startSystemId+":"+startLineNumber+":"+startColumnNumber+"."
			+" Expected "+expected+" "+definition+", found "+found+".";
		}
	}	
	
	
	public  String getWarningMessage(String prefix){
		String message = "";
		// {w1}
		if(ambiguousElementQNameWW != null){
			for(int i = 0; i <= ambiguousElementIndexWW; i++){
				message += "\n"+prefix+"Ambiguous content."
						+"\n"+prefix+"Element <"+ambiguousElementQNameWW[i] + "> at "+ambiguousElementSystemIdWW[i]+":"+ambiguousElementLineNumberWW[i]+":"+ambiguousElementColumnNumberWW[i]
						+" cannot be resolved by in context validation, possible definitions: ";
				for(int j = 0; j < ambiguousElementDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousElementDefinitionWW[i][j].getQName()+"> at "+ambiguousElementDefinitionWW[i][j].getLocation();
				}
				message += ".";
			}
		}
		// {w2}
		if(ambiguousAttributeQNameWW != null){
			for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
				message += "\n"+prefix+"Ambiguous content."
						+"\n"+prefix+"Attribute "+ambiguousAttributeQNameWW[i] + " at "+ambiguousAttributeSystemIdWW[i]+":"+ambiguousAttributeLineNumberWW[i]+":"+ambiguousAttributeColumnNumberWW[i]
						+" cannot be resolved by in context validation, possible definitions: ";
				for(int j = 0; j < ambiguousAttributeDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousAttributeDefinitionWW[i][j].getQName()+"> at "+ambiguousAttributeDefinitionWW[i][j].getLocation();
				}
				message += ".";
			}
		}
		// {w3}
		if(ambiguousCharsDefinitionWW != null){
			for(int i = 0; i <= ambiguousCharsIndexWW; i++){
				message += "\n"+prefix+"Ambiguous content."
						+"\n"+prefix+"Chars at "+ambiguousCharsSystemIdWW[i]+":"+ambiguousCharsLineNumberWW[i]+":"+ambiguousCharsColumnNumberWW[i]
						+" cannot be resolved by in context validation, possible definitions: ";
				for(int j = 0; j < ambiguousCharsDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousCharsDefinitionWW[i][j].getQName()+"> at "+ambiguousCharsDefinitionWW[i][j].getLocation();
				}
				message += ".";
			}
		}
		return message;
	}
	
	public  String getLocatedWarningMessage(){
		return "TODO";
	}
	
	public  String getErrorMessage(String prefix){
		String message = "";
		// {1}
		if(undeterminedCandidateMessages != null){
			message +=  " Element "+undeterminedQName+" could not be resolved to a single schema definition:"
						+ "\n"+prefix+"Unresolved by content."
						+ "\n"+prefix+"Validation  of candidate definitions resulted in errors:"						
						+ undeterminedCandidateMessages;
			return message;
		}
		// {2}
		if(unknownElementQName != null){
			for(int i = 0; i <= unknownElementIndex; i++){
				message += "\n"+prefix+"Unknown element in content."
				+"\n"+prefix+"Element <"+unknownElementQName[i]+"> at "+unknownElementSystemId[i]+":"+unknownElementLineNumber[i]+":"+unknownElementColumnNumber[i]
				+" is not known in this context.";
			}
		}	
		// {3}
		if(unexpectedElementQName != null){
			for(int i = 0; i <= unexpectedElementIndex; i++){
				message += "\n"+prefix+"Unexpected element."
						+"\n"+prefix+"Element <"+unexpectedElementQName[i]+"> at "+unexpectedElementSystemId[i]+":"+unexpectedElementLineNumber[i]+":"+unexpectedElementColumnNumber[i]+"corresponding to definition <"+unexpectedElementDefinition[i].getQName()+"> at "+unexpectedElementDefinition[i].getLocation()+" is not part of the parent's content model." ;
			}
		}
		// {4}
		if(unexpectedAmbiguousElementQName != null){
			for(int i = 0; i <= unexpectedAmbiguousElementIndex; i++){
				String definitions = "";
				for(int j = 0; j < unexpectedAmbiguousElementDefinition[i].length; j++ ){
					definitions += "\n"+prefix+"<"+unexpectedAmbiguousElementDefinition[i][j].getQName()+"> at "+unexpectedAmbiguousElementDefinition[i][j].getLocation();
				}
				message += "\n"+prefix+"Unexpected element."
						+"\n"+prefix+"Element <"+unexpectedAmbiguousElementQName[i]+"> at "+unexpectedAmbiguousElementSystemId[i]+":"+unexpectedAmbiguousElementLineNumber[i]+":"+unexpectedAmbiguousElementColumnNumber[i]
						+", corresponding to one of the schema definitions: "+definitions						
						+"is not part of the parent's content model." ;
			}
		}
		// {5}
		if(unknownAttributeQName != null){
			for(int i = 0; i <= unknownAttributeIndex; i++){
				message += "\n"+prefix+"Unknown attribute in content."
				+"\n"+prefix+"Attribute "+unknownAttributeQName[i]+" at "+unknownAttributeSystemId[i]+":"+unknownAttributeLineNumber[i]+":"+unknownAttributeColumnNumber[i]
				+" is not known in the vocabulary described by the schema.";
			}
		}	
		// {6}
		if(unexpectedAttributeQName != null){
			for(int i = 0; i <= unexpectedAttributeIndex; i++){
				message += "\n"+prefix+"Unexpected attribute."
						+"\n"+prefix+"Attribute "+unexpectedAttributeQName[i]+" at "+unexpectedAttributeSystemId[i]+":"+unexpectedAttributeLineNumber[i]+":"+unexpectedAttributeColumnNumber[i]+"corresponding to definition <"+unexpectedAttributeDefinition[i]+"> at "+unexpectedAttributeDefinition[i]+" is not part of the parent's content model." ;
			}
		}
		// {7}
		if(unexpectedAmbiguousAttributeQName != null){
			for(int i = 0; i <= unexpectedAmbiguousAttributeIndex; i++){
				String definitions = "";
				for(int j = 0; j < unexpectedAmbiguousAttributeDefinition[i].length; j++ ){
					definitions += "\n"+prefix+"<"+unexpectedAmbiguousAttributeDefinition[i][j].getQName()+"> at "+unexpectedAmbiguousAttributeDefinition[i][j].getLocation();
				}
				message += "\n"+prefix+"Unexpected attribute."
						+"\n"+prefix+"Attribute "+unexpectedAmbiguousAttributeQName[i]+" at "+unexpectedAmbiguousAttributeSystemId[i]+":"+unexpectedAmbiguousAttributeLineNumber[i]+":"+unexpectedAmbiguousAttributeColumnNumber[i]
						+", corresponding to one of the definitions: "+definitions						
						+"is not part of the parent's content model." ;
			}
		}

		
		// {8}
		if(misplacedContext != null){
			for(int i = 0; i <= misplacedIndex; i++){
				message += "\n"+prefix+"Order error."
				+"\n"+prefix+"Misplaced elements in the document structure starting at "+misplacedStartSystemId[i]+":"+misplacedStartLineNumber[i]+":"+misplacedStartColumnNumber[i]+", corresponding to definition <"+misplacedContext[i].getQName()+"> at "+misplacedContext[i].getLocation()+ ":";
				for(int j = 0; j < misplacedDefinition[i].length; j++){
					for(int k = 0; k < misplacedQName[i][j].length; k++){
						message += "\n"+prefix+misplacedQName[i][j][k]+" at "+misplacedSystemId[i][j][k]+":"+misplacedLineNumber[i][j][k]+":"+misplacedColumnNumber[i][j][k];
					}
					message += ", corresponding to definition <"+misplacedDefinition[i][j].getQName()+"> at "+misplacedDefinition[i][j].getLocation();
				}
				message += ".";				
			}
		}
		// {9}
		if(excessiveContext != null){
			for(int i = 0; i <= excessiveIndex; i++){
				message += "\n"+prefix+"Excessive content."
						+"\n"+prefix+"In the document structure starting at "+excessiveStartSystemId[i]+":"+excessiveStartLineNumber[i]+":"+excessiveStartColumnNumber[i]+", corresponding to definition <"+excessiveContext[i].getQName()+"> at "+excessiveContext[i].getLocation()+", "
						+" expected "+getExpectedCardinality(excessiveDefinition[i].getMinOccurs(), excessiveDefinition[i].getMaxOccurs())+" corresponding to definition <"+excessiveDefinition[i].getQName()+"> at "+excessiveDefinition[i].getLocation()+", found "+excessiveQName[i].length+": ";
				for(int j = 0; j < excessiveQName[i].length; j++){
					message += "\n"+prefix+"<"+excessiveQName[i][j]+"> at "+excessiveSystemId[i][j]+":"+excessiveLineNumber[i][j]+":"+excessiveColumnNumber[i][j];
				}
				message += ".";
			}
		}
		// {10}
		if(missingContext != null){
			for(int i = 0; i <= missingIndex; i++){
				int found = missingFound[i];				
				if(found > 0){
					message += "\n"+prefix+"Missing content."
							+"\n"+prefix+"In the document structure starting at "+missingStartSystemId[i]+":"+missingStartLineNumber[i]+":"+missingStartColumnNumber[i]+", corresponding to definition <"+missingContext[i].getQName()+"> at "+missingContext[i].getLocation()+", "
							+"expected "+getExpectedCardinality(missingDefinition[i].getMinOccurs(), missingDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingDefinition[i].getQName()+"> at "+missingDefinition[i].getLocation()+", found "+found+": ";
					for(int j = 0; j < missingQName[i].length; i++){
						message += "\n"+prefix+"<"+missingQName[i][j]+"> at "+missingSystemId[i][j]+":"+missingLineNumber[i][j]+":"+missingColumnNumber[i][j];
					}
				}else{
					message += "\n"+prefix+"Missing content."
							+"\n"+prefix+"In the document structure starting at "+missingStartSystemId[i]+":"+missingStartLineNumber[i]+":"+missingStartColumnNumber[i]+", corresponding to definition <"+missingContext[i].getQName()+"> at "+missingContext[i].getLocation()+", "
							+"expected "+getExpectedCardinality(missingDefinition[i].getMinOccurs(), missingDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingDefinition[i].getQName()+"> at "+missingDefinition[i].getLocation()+", found "+found;
				}
				message += ".";
			}			
		}		
		// {11}
		if(illegalContext != null){
			for(int i = 0; i <= illegalIndex; i++){
				message += "\n"+prefix+"Illegal content."
							+"\n"+prefix+"The document structure starting with <"+illegalQName[i] +"> at "+illegalStartSystemId[i]+":"+illegalStartLineNumber[i]+":"+illegalStartColumnNumber[i]+" does not match schema definition <"+illegalContext[i].getQName()+"> at "+illegalContext[i].getLocation()+".";
			}			
		}
		// {12}
		if(ambiguousElementQNameEE != null){
			for(int i = 0; i <= ambiguousElementIndexEE; i++){
				message += "\n"+prefix+"Ambiguous content."
						+"\n"+prefix+"Element <"+ambiguousElementQNameEE[i] + "> at "+ambiguousElementSystemIdEE[i]+":"+ambiguousElementLineNumberEE[i]+":"+ambiguousElementColumnNumberEE[i]
						+" cannot be resolved by in context validation, all candidates resulted in errors. Possible definitions: ";
				for(int j = 0; j < ambiguousElementDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousElementDefinitionEE[i][j].getQName()+"> at "+ambiguousElementDefinitionEE[i][j].getLocation();
				}
				message += ".";
			}
		}
		// {13}
		if(ambiguousAttributeQNameEE != null){
			for(int i = 0; i <= ambiguousAttributeIndexEE; i++){
				message += "\n"+prefix+"Ambiguous content."
						+"\n"+prefix+"Attribute "+ambiguousAttributeQNameEE[i] + " at "+ambiguousAttributeSystemIdEE[i]+":"+ambiguousAttributeLineNumberEE[i]+":"+ambiguousAttributeColumnNumberEE[i]
						+" cannot be resolved by in context validation, all candidates resulted in errors. Possible definitions: ";
				for(int j = 0; j < ambiguousAttributeDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousAttributeDefinitionEE[i][j].getQName()+"> at "+ambiguousAttributeDefinitionEE[i][j].getLocation();
				}
				message += ".";
			}
		}
		// {14}
		if(ambiguousCharsDefinitionEE != null){
			for(int i = 0; i <= ambiguousCharsIndexEE; i++){
				message += "\n"+prefix+"Ambiguous content."
						+"\n"+prefix+"Chars at "+ambiguousCharsSystemIdEE[i]+":"+ambiguousCharsLineNumberEE[i]+":"+ambiguousCharsColumnNumberEE[i]
						+" cannot be resolved by in context validation, all candidates resulted in errors. Possible definitions: ";
				for(int j = 0; j < ambiguousCharsDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousCharsDefinitionEE[i][j].getQName()+"> at "+ambiguousCharsDefinitionEE[i][j].getLocation();
				}
				message += ".";
			}
		}
		// {15}
		if(datatypeCharsSystemIdCC != null){
			for(int i = 0; i <= datatypeIndexCC; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "Character content at "+datatypeCharsSystemIdCC[i]+":"+datatypeCharsLineNumberCC[i]+":"+datatypeCharsColumnNumberCC[i]
				+ " does not match the datatype required by schema definition <" +datatypeCharsDefinitionCC[i].getQName()+"> at "+datatypeCharsDefinitionCC[i].getLocation()+". "
				+ datatypeErrorMessageCC[i];
			}
		}
		// {16}
		if(datatypeCharsSystemIdAV != null){
			for(int i = 0; i <= datatypeIndexAV; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "Value of attribute "+datatypeAttributeQNameAV[i]+" at "+datatypeCharsSystemIdAV[i]+":"+datatypeCharsLineNumberAV[i]+":"+datatypeCharsColumnNumberAV[i]
				+ " does not match the datatype required by schema definition <" +datatypeCharsDefinitionAV[i].getQName()+"> at "+datatypeCharsDefinitionAV[i].getLocation()+". "
				+ datatypeErrorMessageAV[i];
			}
		}
		// {17}
		if(valueCharsSystemIdCC != null){
			for(int i = 0; i <= valueIndexCC; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "Character content at "+valueCharsSystemIdCC[i]+":"+valueCharsLineNumberCC[i]+":"+valueCharsColumnNumberCC[i]
				+ " does not match the value required by schema definition <" +valueCharsDefinitionCC[i].getQName()+"> at "+valueCharsDefinitionCC[i].getLocation()+".";
			}
		}
		// {18}
		if(valueCharsSystemIdAV != null){
			for(int i = 0; i <= valueIndexAV; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "Value of attribute "+valueAttributeQNameAV[i]+" at "+valueCharsSystemIdAV[i]+":"+valueCharsLineNumberAV[i]+":"+valueCharsColumnNumberAV[i]
				+ " does not match the value required by schema definition <" +valueCharsDefinitionAV[i].getQName()+"> at "+valueCharsDefinitionAV[i].getLocation()+".";
			}
		}
		// {19}
		if(exceptCharsSystemIdCC != null){
			for(int i = 0; i <= exceptIndexCC; i++){
				message += "\n"+prefix+"Excepted character content."
				+"\n"+prefix+ "Character content at "+exceptCharsSystemIdCC[i]+":"+exceptCharsLineNumberCC[i]+":"+exceptCharsColumnNumberCC[i]
				+ " matches a value excepted by schema definition <" +exceptCharsDefinitionCC[i].getQName()+"> at "+exceptCharsDefinitionCC[i].getLocation()+".";
			}
		}
		// {20}
		if(exceptCharsSystemIdAV != null){
			for(int i = 0; i <= exceptIndexAV; i++){
				message += "\n"+prefix+"Excepted attribute value"
				+"\n"+prefix+ "Value of attribute "+exceptAttributeQNameAV[i]+" at "+exceptCharsSystemIdAV[i]+":"+exceptCharsLineNumberAV[i]+":"+exceptCharsColumnNumberAV[i]
				+ " matches a value excepted by schema definition <" +exceptCharsDefinitionAV[i].getQName()+"> at "+exceptCharsDefinitionAV[i].getLocation()+".";
			}
		}
		// {21}
		if(unexpectedCharsSystemIdCC != null){
			for(int i = 0; i <= unexpectedIndexCC; i++){
				message += "\n"+prefix+"Unexpected character content."
				+"\n"+prefix+ "Character content at "+unexpectedCharsSystemIdCC[i]+":"+unexpectedCharsLineNumberCC[i]+":"+unexpectedCharsColumnNumberCC[i]
				+ " is not allowed by the element's schema definition <" +unexpectedContextDefinitionCC[i].getQName()+"> at "+unexpectedContextDefinitionCC[i].getLocation()+".";
			}
		}
		// {22}
		if(unexpectedCharsSystemIdAV != null){
			for(int i = 0; i <= unexpectedIndexAV; i++){
				message += "\n"+prefix+"Unexpected attribute unexpected."
				+"\n"+prefix+ "Value of attribute "+unexpectedAttributeQName[i]+" at "+unexpectedCharsSystemIdAV[i]+":"+unexpectedCharsLineNumberAV[i]+":"+unexpectedCharsColumnNumberAV[i]
				+ " is not allowed by the attributes's schema definition <" +unexpectedContextDefinitionAV[i].getQName()+"> at "+unexpectedContextDefinitionAV[i].getLocation()+".";
			}
		}
		// {23}
		if(ambiguousCharsSystemIdEECC != null){
			for(int i = 0; i <= ambiguousIndexCC; i++){
				message += "\n"+prefix+"Ambiguous character content."
				+"\n"+prefix+ "Character content at "+ambiguousCharsSystemIdEECC[i]+":"+ambiguousCharsLineNumberEECC[i]+":"+ambiguousCharsColumnNumberEECC[i]
				+ " cannot be resolved by datatype and structure validation to one schema definition, all candidates resulted in errors."
				+" Possible definitions:";
				for(int j = 0; j < ambiguousPossibleDefinitionsCC[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousPossibleDefinitionsCC[i][j].getQName()+"> at "+ambiguousPossibleDefinitionsCC[i][j].getLocation();
				}
				message += ".";
			}
		}
		// {24}
		if(ambiguousCharsSystemIdEEAV != null){			
			for(int i = 0; i <= ambiguousIndexAV; i++){				
				message += "\n"+prefix+"Ambiguous attribute value."
				+"\n"+prefix+ "Value of attribute "+ambiguousAttributeQNameEEAV[i]+" at "+ambiguousCharsSystemIdEEAV[i]+":"+ambiguousCharsLineNumberEEAV[i]+":"+ambiguousCharsColumnNumberEEAV[i]
				+ " cannot be resolved by datatype and structure validation to one schema definition, all candidates resulted in errors."
				+" Possible definitions:";
				for(int j = 0; j < ambiguousPossibleDefinitionsAV[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousPossibleDefinitionsAV[i][j].getQName()+"> at "+ambiguousPossibleDefinitionsAV[i][j].getLocation();
				}
				message += ".";
			}
		}
		// {25}
		if(datatypeCharsSystemIdLP != null){
			for(int i = 0; i <= datatypeIndexLP; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "List token "+datatypeTokenLP[i]+" at "+datatypeCharsSystemIdLP[i]+":"+datatypeCharsLineNumberLP[i]+":"+datatypeCharsColumnNumberLP[i]
				+ " does not match the datatype required by schema definition <" +datatypeCharsDefinitionLP[i].getQName()+"> at "+datatypeCharsDefinitionLP[i].getLocation()+". "
				+ datatypeErrorMessageLP[i];
			}
		}
		// {26}
		if(valueCharsSystemIdLP != null){
			for(int i = 0; i <= valueIndexLP; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "List token "+valueTokenLP[i]+" at "+valueCharsSystemIdLP[i]+":"+valueCharsLineNumberLP[i]+":"+valueCharsColumnNumberLP[i]
				+ " does not match the value required by schema definition <" +valueCharsDefinitionLP[i].getQName()+"> at "+valueCharsDefinitionLP[i].getLocation()+".";
			}
		}
		// {27}
		if(exceptCharsSystemIdLP != null){
			for(int i = 0; i <= exceptIndexLP; i++){
				message += "\n"+prefix+"Excepted token."
				+"\n"+prefix+ "List token "+exceptTokenLP[i]+" at "+exceptCharsSystemIdLP[i]+":"+exceptCharsLineNumberLP[i]+":"+exceptCharsColumnNumberLP[i]
				+ " matches a value excepted by schema definition " +exceptCharsDefinitionLP[i].getQName()+" at "+exceptCharsDefinitionLP[i].getLocation()+".";
			}
		}
		// {28}
		if(ambiguousCharsSystemIdEELP != null){
			for(int i = 0; i <= ambiguousIndexLP; i++){
				message += "\n"+prefix+"Illegal ambiguous."
				+"\n"+prefix+ "List token "+ambiguousTokenLP[i]+" at "+ambiguousCharsSystemIdEELP[i]+":"+ambiguousCharsLineNumberEELP[i]+":"+ambiguousCharsColumnNumberEELP[i]
				+ " cannot be resolved by datatype and structure validation to one schema definition, all candidates resulted in errors."
				+ " Possible definitions: ";
				for(int j = 0; j < ambiguousPossibleDefinitionsLP[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousPossibleDefinitionsLP[i][j].getQName()+"> at "+ambiguousPossibleDefinitionsLP[i][j].getLocation();
				}
				message += ".";
			}
		}
		
		// {29}
		if(missingCompositorContentContext != null){
			for(int i = 0; i <= missingCompositorContentIndex; i++){
				message += "\n"+prefix+"Missing compositor content."
						+"\n"+prefix+"In the document structure starting at "+missingCompositorContentStartSystemId[i]+":"+missingCompositorContentStartLineNumber[i]+":"+missingCompositorContentStartColumnNumber[i]+", corresponding to definition <"+missingCompositorContentContext[i].getQName()+"> at "+missingCompositorContentContext[i].getLocation()+", "
						+"expected "+getExpectedCardinality(missingCompositorContentDefinition[i].getMinOccurs(), missingCompositorContentDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingCompositorContentDefinition[i].getQName()+"> at "+missingCompositorContentDefinition[i].getLocation()+", found "+missingCompositorContentFound[i]+". ";
			}			
		}		
		return message;
	}
	public String getLocatedErrorMessage(String prefix){
		
		if(definition == null){
			return prefix+systemId+":"+lineNumber+":"+columnNumber+getErrorMessage(prefix);
		}
		return prefix+systemId+":"+lineNumber+":"+columnNumber+"  Element <"+qName+"> corresponding to definition <"+definition.getQName() +"> at "+definition.getLocation()+" contains errors: "+getErrorMessage(prefix);
	}
	
	public  String getFatalErrorMessage(){
		return "TODO";
	}
	public  String getLocatedFatalErrorMessage(){
		return "TODO";
	}	
	
	String getExpectedCardinality(int expectedMin, int expectedMax){
		if(expectedMax == 1){
			if(expectedMin == 0) return "optional occurrence";
			else return "one occurrence";
		}
		
		if(expectedMin == 0){
			return "zero or more occurrences";
		}else{
			return "one or more occurrences";
		}
	}
}