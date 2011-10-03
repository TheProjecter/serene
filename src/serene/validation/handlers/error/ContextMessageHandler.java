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
        conflictResolutionId = RESOLVED;        
	}	
    
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
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
	
    public void clearUnknownElement(){
        errorTotalCount -= unknownElementSize; 
        unknownElementSize = 0;
        unknownElementIndex = -1;	
        unknownElementQName = null;			
        unknownElementSystemId = null;			
        unknownElementLineNumber = null;
        unknownElementColumnNumber = null;
    }
	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
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
		
	public void clearUnexpectedElement(){
        errorTotalCount -= unexpectedElementSize;
        unexpectedElementSize = 0;
        unexpectedElementIndex = -1;	
        unexpectedElementQName = null;
        unexpectedElementDefinition = null;
        unexpectedElementSystemId = null;			
        unexpectedElementLineNumber = null;
        unexpectedElementColumnNumber = null;
    }
    
    
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
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
	
	public void clearUnexpectedAmbiguousElement(){
        errorTotalCount -= unexpectedAmbiguousElementSize;
        unexpectedAmbiguousElementSize = 0;
        unexpectedAmbiguousElementIndex = -1;	
        unexpectedAmbiguousElementQName = null;
        unexpectedAmbiguousElementDefinition = null;
        unexpectedAmbiguousElementSystemId = null;			
        unexpectedAmbiguousElementLineNumber = null;
        unexpectedAmbiguousElementColumnNumber = null;
    }
	
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
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
		}
		unknownAttributeQName[unknownAttributeIndex] = qName;
		unknownAttributeSystemId[unknownAttributeIndex] = systemId;
		unknownAttributeLineNumber[unknownAttributeIndex] = lineNumber;
		unknownAttributeColumnNumber[unknownAttributeIndex] = columnNumber;
	}
	public void clearUnknownAttribute(){
        errorTotalCount -= unknownAttributeSize;
        unknownAttributeSize = 0;
        unknownAttributeIndex = -1;	
        unknownAttributeQName = null;			
        unknownAttributeSystemId = null;			
        unknownAttributeLineNumber = null;
        unknownAttributeColumnNumber = null;
    }
	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        
        errorTotalCount++;
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
	public void clearUnexpectedAttribute(){
        errorTotalCount -= unexpectedAttributeSize;
        unexpectedAttributeSize = 0;
        unexpectedAttributeIndex = -1;	
        unexpectedAttributeQName = null;
        unexpectedAttributeDefinition = null;
        unexpectedAttributeSystemId = null;			
        unexpectedAttributeLineNumber = null;
        unexpectedAttributeColumnNumber = null;
    }
	
	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        errorTotalCount++;
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
	public void clearUnexpectedAmbiguousAttribute(){
        errorTotalCount -= unexpectedAmbiguousAttributeSize;
        unexpectedAmbiguousAttributeSize = 0;
        unexpectedAmbiguousAttributeIndex = -1;	
        unexpectedAmbiguousAttributeQName = null;
        unexpectedAmbiguousAttributeDefinition = null;
        unexpectedAmbiguousAttributeSystemId = null;			
        unexpectedAmbiguousAttributeLineNumber = null;
        unexpectedAmbiguousAttributeColumnNumber = null;
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
    }
    
    
	public void ambiguousElementContentError(String qName, 
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
    }
    
    
	public void ambiguousAttributeContentError(String qName, 
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
    }
	
	public void ambiguousCharsContentError(String systemId, 
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
    public void clearAmbiguousCharsContentError(){
        errorTotalCount -= ambiguousCharsSizeEE;
        ambiguousCharsSizeEE = 0;
        ambiguousCharsIndexEE = -1;
        ambiguousCharsSystemIdEE = null;			
        ambiguousCharsLineNumberEE = null;
        ambiguousCharsColumnNumberEE = null;
        ambiguousCharsDefinitionEE = null;
    }	
	
	
	public void ambiguousElementContentWarning(String qName, 
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
    }
        
	public void ambiguousAttributeContentWarning(String qName, 
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
	
	public void ambiguousCharsContentWarning(String systemId, 
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
	public void clearAmbiguousCharsContentWarning(){
        errorTotalCount -= ambiguousCharsSizeWW;
        ambiguousCharsSizeWW = 0;
        ambiguousCharsIndexWW = -1;
        ambiguousCharsSystemIdWW = null;			
        ambiguousCharsLineNumberWW = null;
        ambiguousCharsColumnNumberWW = null;
        ambiguousCharsDefinitionWW = null;
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
    }
    
	public void illegalContent(Rule context, 
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
	public void clearIllegalContent(){
        errorTotalCount -= illegalSize;
        illegalSize = 0;
        illegalIndex = -1;
        illegalContext = null;
        illegalQName = null;
        illegalStartSystemId = null;			
        illegalStartLineNumber = null;
        illegalStartColumnNumber = null;
    }
        
	public void undeterminedByContent(String qName, String candidateMessages){
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
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
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
    }
        
    //{16}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
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
    }
        
        
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        errorTotalCount++;
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
    public void clearCharacterContentValueError(){
        errorTotalCount -= valueSizeCC;
        valueSizeCC = 0;
        valueIndexCC = -1;
        valueElementQNameCC = null;
        valueCharsSystemIdCC = null;
        valueCharsLineNumberCC = null;
        valueCharsColumnNumberCC = null;
        valueCharsDefinitionCC = null;
    }
    
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        errorTotalCount++;
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
	public void clearAttributeValueValueError(){
        errorTotalCount -= valueSizeAV;
        valueSizeAV = 0;
        valueIndexAV = -1;
        valueAttributeQNameAV = null;
        valueCharsSystemIdAV = null;
        valueCharsLineNumberAV = null;
        valueCharsColumnNumberAV = null;
        valueCharsDefinitionAV = null;
    }
    
    
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        errorTotalCount++;
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
    public void clearCharacterContentExceptedError(){
        errorTotalCount -= exceptSizeCC;
        exceptSizeCC = 0;
        exceptIndexCC = -1;
        exceptElementQNameCC = null;
        exceptCharsSystemIdCC = null;
        exceptCharsLineNumberCC = null;
        exceptCharsColumnNumberCC = null;
        exceptCharsDefinitionCC = null;
    }
    
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        errorTotalCount++;
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
	public void clearAttributeValueExceptedError(){
        errorTotalCount -= exceptSizeAV;
        exceptSizeAV = 0;
        exceptIndexAV = -1;
        exceptAttributeQNameAV = null;
        exceptCharsSystemIdAV = null;
        exceptCharsLineNumberAV = null;
        exceptCharsColumnNumberAV = null;
        exceptCharsDefinitionAV = null;
    }
    
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
        errorTotalCount++;
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
    public void clearUnexpectedCharacterContent(){
        errorTotalCount -= unexpectedSizeCC;
        unexpectedSizeCC = 0;
        unexpectedIndexCC = -1;		
        unexpectedCharsSystemIdCC = null;
        unexpectedCharsLineNumberCC = null;
        unexpectedCharsColumnNumberCC = null;
        unexpectedContextDefinitionCC = null;
    }
    
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        errorTotalCount++;
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
	public void clearUnexpectedAttributeValue(){
        errorTotalCount -= unexpectedSizeAV;
        unexpectedSizeAV = 0;
        unexpectedIndexAV = -1;		
        unexpectedCharsSystemIdAV = null;
        unexpectedCharsLineNumberAV = null;
        unexpectedCharsColumnNumberAV = null;
        unexpectedContextDefinitionAV = null;
    }
    
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
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
    public void clearAmbiguousCharacterContent(){
        errorTotalCount -= ambiguousSizeCC;
        ambiguousSizeCC = 0;
        ambiguousIndexCC = -1;		
        ambiguousCharsSystemIdEECC = null;
        ambiguousCharsLineNumberEECC = null;
        ambiguousCharsColumnNumberEECC = null;
        ambiguousPossibleDefinitionsCC = null;
    }
    
	// {24}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
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
	public void clearAmbiguousAttributeValue(){
        errorTotalCount -= ambiguousSizeAV;
        ambiguousSizeAV = 0;
        ambiguousIndexAV = -1;
        ambiguousAttributeQNameEEAV = null;
        ambiguousCharsSystemIdEEAV = null;
        ambiguousCharsLineNumberEEAV = null;
        ambiguousCharsColumnNumberEEAV = null;
        ambiguousPossibleDefinitionsAV = null;
    }
    
    
    // {25}
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
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
    }
    
        
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        errorTotalCount++;
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
    public void clearListTokenValueError(){
        errorTotalCount -= valueSizeLP;
        valueSizeLP = 0;
        valueIndexLP = -1;
        valueTokenLP = null;
        valueCharsSystemIdLP = null;
        valueCharsLineNumberLP = null;
        valueCharsColumnNumberLP = null;
        valueCharsDefinitionLP = null;
    }
    
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        errorTotalCount++;
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
    public void clearListTokenExceptedError(){
        errorTotalCount -= exceptSizeLP;
        exceptSizeLP = 0;
        exceptIndexLP = -1;
        exceptTokenLP = null;
        exceptCharsSystemIdLP = null;
        exceptCharsLineNumberLP = null;
        exceptCharsColumnNumberLP = null;
        exceptCharsDefinitionLP = null;
    }
    
    
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
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
    public void clearAmbiguousListToken(){
        errorTotalCount -= ambiguousSizeLP;
        ambiguousSizeLP = 0;
        ambiguousIndexLP = -1;
        ambiguousTokenLP = null;
        ambiguousCharsSystemIdEELP = null;
        ambiguousCharsLineNumberEELP = null;
        ambiguousCharsColumnNumberEELP = null;
        ambiguousPossibleDefinitionsLP = null;
    }
        
    public void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
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
    public void clearAmbiguousListTokenInContextError(){
        errorTotalCount -= ambiguousSizeLPICE;
        ambiguousSizeLPICE = 0;
        ambiguousIndexLPICE = -1;
        ambiguousTokenLPICE = null;
        ambiguousCharsSystemIdEELPICE = null;
        ambiguousCharsLineNumberEELPICE = null;
        ambiguousCharsColumnNumberEELPICE = null;
        ambiguousPossibleDefinitionsLPICE = null;
    }
    
    public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        errorTotalCount++;
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
    
	public void missingCompositorContent(Rule context, 
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
    }
    
    
    public  void conflict(MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
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
        conflictResolutionId = RESOLVED;
    }
    
    
}
