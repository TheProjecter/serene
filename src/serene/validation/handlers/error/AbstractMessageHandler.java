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

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.util.IntList;

import sereneWrite.MessageWriter;

public abstract class AbstractMessageHandler  extends AbstractMessageReporter{	
    
    ActiveInputDescriptor activeInputDescriptor;
    
	// {1}
	String undeterminedQName;
	String undeterminedCandidateMessages;
	
    int initialSize = 5;
    int increaseSizeAmount = 5;
	
	// {2}
	/*String[] unknownElementQName;
	String[] unknownElementSystemId;
	int[] unknownElementLineNumber;
	int[] unknownElementColumnNumber;
	int unknownElementIndex;
	int unknownElementSize;*/
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
	int unknownAttributeSize;*/
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
    
    MessageReporter commonMessages; // It can be only one because it is about this context.
    int candidatesCount;
    BitSet disqualified;
    MessageReporter[] candidateMessages;
    
    
    int messageTotalCount;
		
    /*boolean isMessageRetrieved;
    boolean isDiscarded;*/
   
    int clientCount;
	public AbstractMessageHandler(MessageWriter debugWriter){
		super(debugWriter);
			
        messageTotalCount = 0;  
        
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
        
        /*isMessageRetrieved = false;
        isDiscarded = false;*/

        clientCount = 0;
	}  
    
	void init(ActiveInputDescriptor activeInputDescriptor){
	    /*isMessageRetrieved = false;
        isDiscarded = false;*/
	    this.activeInputDescriptor = activeInputDescriptor;
	}
	
	/*public void setDiscarded(boolean isDiscarded){
        this.isDiscarded = isDiscarded;
        
        /*if(commonMessages != null)commonMessages.setDiscarded(isDiscarded);
        
        if(candidateMessages!= null){
            for(int i = 0; i < candidateMessages.length; i++){
                if(candidateMessages[i] != null)candidateMessages[i].setDiscarded(isDiscarded);;
            }
        }
        if(parent != null)parent.setDiscarded(isDiscarded);*/
   /* }*/
    
	public void registerClient(MessageReporter mr){
	    clientCount++;
	}
	
	public boolean containsErrorMessage(){
	    if(unknownElementIndex >= 0
	        || unexpectedElementIndex >= 0
	        || unexpectedAmbiguousElementIndex  >= 0
	        || unknownAttributeIndex  >= 0
	        || unexpectedAttributeIndex  >= 0
	        || unexpectedAmbiguousAttributeIndex >= 0
	        || misplacedIndex  >= 0
	        || excessiveIndex  >= 0
	        || missingIndex >= 0
	        || illegalIndex >= 0
	        || unresolvedAmbiguousElementIndexEE >= 0
	        || unresolvedUnresolvedElementIndexEE >= 0
	        || unresolvedAttributeIndexEE >= 0
	        || datatypeCharsIndex >= 0
	        || datatypeAVIndex >= 0
	        || valueCharsIndex >= 0
	        || valueAVIndex >= 0
	        || exceptCharsIndex >= 0
	        || exceptAVIndex >= 0
	        || unexpectedCharsIndex >= 0
	        || unexpectedAVIndex >= 0
	        || unresolvedCharsIndexEE >= 0
	        || unresolvedAVIndexEE >= 0
	        || datatypeTokenIndex >= 0
	        || valueTokenIndex >= 0
	        || exceptTokenIndex >= 0
	        || unresolvedTokenIndexLPICE >= 0
	        || missingCompositorContentIndex >= 0) return true;
	    
	    if(commonMessages != null && commonMessages.containsErrorMessage())return true;	    
	    if(disqualified != null && disqualified.cardinality() == candidatesCount){
	        return true;
	    }else if(disqualified != null){	        
	        for(int i = 0; i < candidatesCount; i++){
	            if(!disqualified.get(i)
	                && candidateMessages[i] != null
	                && candidateMessages[i].containsErrorMessage())return true;
	        }
	    }
	    	
	    if(parent != null)return parent.containsErrorMessage();
	    return false;
	}
	
	public boolean containsOtherErrorMessage(IntList exceptedErrorIds, IntList exceptedErrorCodes){
	    throw new IllegalStateException();
    }
    
    public void report(int reportingContextType, String reportingContextQName, AElement reportingContextDefinition, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher) throws SAXException{
        /*isMessageRetrieved = true;*/
        
        this.reportingContextType = reportingContextType;
        this.reportingContextQName = reportingContextQName;
        this.reportingContextDefinition = reportingContextDefinition;
        
        if(parent != null){
            parent.report(restrictToFileName, locator, errorDispatcher, "");//parent should have been located, else illegal state
        }
                
        //do the conflict
        if(disqualified != null){
            handleConflict(reportingContextType, reportingContextQName, restrictToFileName, locator, errorDispatcher, "");            
            // report common as any other errors            
            if(commonMessages != null){
                commonMessages.report(restrictToFileName, locator, errorDispatcher, "");
            }            
            return;
        }
        
        String errorMessage = getValidationErrorMessage("", restrictToFileName);
        if(errorMessage != null){
            errorMessage = errorMessage.trim();
            if(!errorMessage.equals("")){
                if(locator != null) errorDispatcher.error(new SAXParseException(errorMessage, locator));
                else errorDispatcher.error(new SAXParseException(errorMessage, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
                                          
            }
        }
        
        String warningMessage = getValidationWarningMessage("", restrictToFileName);
        if(warningMessage != null){
            warningMessage = warningMessage.trim();
            if(!warningMessage.equals("")){
                if(locator != null) errorDispatcher.warning(new SAXParseException(warningMessage, locator));
                else errorDispatcher.warning(new SAXParseException(warningMessage, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
            }
        }
    }
    
    private void handleConflict(int contextType, String qName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualified = candidatesCount - disqualified.cardinality();
        if(qualified == 0){
            reportUnresolved(contextType, qName, restrictToFileName, locator, errorDispatcher, prefix);
        }else if(qualified == 1){
            reportResolved(contextType, qName, restrictToFileName, locator, errorDispatcher, prefix);
        }else{
            reportAmbiguous(contextType, qName, restrictToFileName, locator, errorDispatcher, prefix);
        }
    }
    
    private void reportUnresolved(int contextType, String qName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);
        }
        if(!message.equals("")){
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
        }else throw new IllegalStateException();
    }
    
    private void reportResolved(int contextType, String qName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualifiedIndex = disqualified.nextClearBit(0);
        if(candidateMessages != null && candidateMessages[qualifiedIndex] != null) candidateMessages[qualifiedIndex].report(restrictToFileName, locator, errorDispatcher, prefix);        
    }
    
    private void reportAmbiguous(int contextType, String qName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            if(!disqualified.get(i) && candidateMessages != null && candidateMessages[i] != null){
                 message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);            
            }
        }
        if(!message.equals("")){            
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
        }        
    }
        
    public void report(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        /*isMessageRetrieved = true;*/
        
        if(parent != null){
            parent.report(restrictToFileName, locator, errorDispatcher, prefix);//parent should have been located, else illegal state            
        }
        
        //do the conflict
        if(disqualified != null){
            handleConflict(restrictToFileName, locator, errorDispatcher, prefix);
            
            // report common as any other errors            
            if(commonMessages != null){
                commonMessages.report(restrictToFileName, locator, errorDispatcher, prefix);                
            }            
            return;
        }
                
        String errorMessage = getValidationErrorMessage(prefix, restrictToFileName);
        if(errorMessage != null){
            errorMessage = errorMessage.trim();
            if(!errorMessage.equals("")){
                if(locator != null) errorDispatcher.error(new SAXParseException(errorMessage, locator));
                else errorDispatcher.error(new SAXParseException(errorMessage, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
            }
        }
        
        String warningMessage = getValidationWarningMessage(prefix, restrictToFileName);
        if(warningMessage != null){
            warningMessage = warningMessage.trim();
            if(!warningMessage.equals("")){
                if(locator != null) errorDispatcher.warning(new SAXParseException(warningMessage, locator));
                else errorDispatcher.warning(new SAXParseException(warningMessage, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
            }
        }
    }
    
    private void handleConflict(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualified = candidatesCount - disqualified.cardinality();
        
        if(qualified == 0){
            reportUnresolved(restrictToFileName, locator, errorDispatcher, prefix);
        }else if(qualified == 1){
            reportResolved(restrictToFileName, locator, errorDispatcher, prefix);
        }else{
            reportAmbiguous(restrictToFileName, locator, errorDispatcher, prefix);
        }
    }
    
    private void reportUnresolved(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = "";
        for(int i = 0; i < candidatesCount; i++){
            message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);
        }
        if(!message.equals("")){
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error.Element <"+reportingContextQName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error.Element <"+reportingContextQName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
        }
    }
    
    private void reportResolved(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualifiedIndex = disqualified.nextClearBit(0);
        if(candidateMessages != null && candidateMessages[qualifiedIndex] != null) candidateMessages[qualifiedIndex].report(restrictToFileName, locator, errorDispatcher, prefix);
    }
    
    private void reportAmbiguous(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            if(!disqualified.get(i)  && candidateMessages != null && candidateMessages[i] != null)message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);
        }
        message = message.trim();
        if(!message.equals("")){
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+reportingContextQName+"> contain errors in their subtrees:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+reportingContextQName+"> contain errors in their subtrees:"+message, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
        }
    }
    
    private String getFatalErrorMessage(){
		return "TODO";
	}
        
    
	
    public String getCandidateErrorMessage(String prefix, boolean restrictToFileName){
		if(reportingContextDefinition == null){
			throw new IllegalStateException();
		}
        String message = "";
        
        String localPrefix = prefix+"\t";
        String localMessage = getErrorMessage(localPrefix, restrictToFileName); 
        
        if(!localMessage.equals("")){
            if(parent != null){
                String parentMessage = parent.getErrorMessage(localPrefix, restrictToFileName); 
                if(!parentMessage.equals(""))
                    message = "\n"+prefix+"Candidate definition <"+reportingContextDefinition.getQName() +"> at "+reportingContextDefinition.getLocation(restrictToFileName)+" contains errors: "
                            + parentMessage;
            }else{                
                message = "\n"+prefix+"Candidate definition <"+reportingContextDefinition.getQName() +"> at "+reportingContextDefinition.getLocation(restrictToFileName)+" contains errors: ";                
            }
            message += localMessage; 
        }else{
            if(parent != null){
                String parentMessage = parent.getErrorMessage(prefix, restrictToFileName); 
                if(!parentMessage.equals(""))
                    message = "\n"+prefix+"Candidate definition <"+reportingContextDefinition.getQName() +"> at "+reportingContextDefinition.getLocation(restrictToFileName)+" contains errors: "
                            + parentMessage;
            }
        }
        
        return message;
	}
       	
    
	public String getErrorMessage(String prefix, boolean restrictToFileName){
		String message = "";		
        if(disqualified != null){
            message += getConflictErrorMessage(prefix, restrictToFileName);
        }else{
            message += getValidationErrorMessage(prefix, restrictToFileName);
        }
        return message;
    }
    
    private String getConflictErrorMessage(String prefix, boolean restrictToFileName){
        int qualified = candidatesCount - disqualified.cardinality();
        
        if(qualified == 0){
            return getUnresolvedMessage(prefix, restrictToFileName);
        }else if(qualified == 1){
            return getResolvedMessage(prefix, restrictToFileName);
        }else{
            return getAmbiguousMessage(prefix, restrictToFileName);
        }
    }
    
    private String getUnresolvedMessage(String prefix, boolean restrictToFileName){        
        String message = "";
        for(int i = 0; i < candidatesCount; i++){
            message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);
        }
        message = message.trim();
        if(!message.equals(""))return "\n"+prefix+"Syntax error.Element <"+reportingContextQName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message;
        return message;
    }
    
    private String getResolvedMessage(String prefix, boolean restrictToFileName){
        int qualifiedIndex = disqualified.nextClearBit(0);
        if(candidateMessages != null && candidateMessages[qualifiedIndex] != null)  return candidateMessages[qualifiedIndex].getErrorMessage(prefix, restrictToFileName);
        return null;
    }
    
    private String getAmbiguousMessage(String prefix, boolean restrictToFileName){
        String message = "";
        for(int i = 0; i < candidatesCount; i++){
            if(!disqualified.get(i)){
                message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);                
            }
        }
        message = message.trim();
        if(!message.equals("")){
            return "\n"+prefix+"Syntax error. Candidate definitions of ambiguous element <"+reportingContextQName+"> contain errors in their subtrees:"+message;
        }
        return message;
    }
    
    
    String getValidationErrorMessage(String prefix, boolean restrictToFileName){
		// {2}
        String message = "";
		if(unknownElementIndex >= 0){
			for(int i = 0; i <= unknownElementIndex; i++){
				message += "\n"+prefix+"Unknown element."
				+"\n"+prefix+"Element <"+activeInputDescriptor.getItemDescription(unknownElementInputRecordIndex[i])+"> at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unknownElementInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(unknownElementInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(unknownElementInputRecordIndex[i])
				+" is not known in the vocabulary described by the schema.";
				
				//activeInputDescriptor.unregisterClientForRecord(unknownElementInputRecordIndex[i]);
			}
		}	
		// {3}
		if(unexpectedElementIndex >= 0){
			for(int i = 0; i <= unexpectedElementIndex; i++){
				message += "\n"+prefix+"Unexpected element."
                +"\n"+prefix+"Element <"+activeInputDescriptor.getItemDescription(unexpectedElementInputRecordIndex[i])+"> at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unexpectedElementInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(unexpectedElementInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(unexpectedElementInputRecordIndex[i])+" corresponding to definition: <"+unexpectedElementDefinition[i].getQName()+"> at "+unexpectedElementDefinition[i].getLocation(restrictToFileName)+" is not part of the parent's content model." ;
			}
		}
		// {4}
		if(unexpectedAmbiguousElementIndex  >= 0){
			for(int i = 0; i <= unexpectedAmbiguousElementIndex; i++){
				String definitions = "";
				for(int j = 0; j < unexpectedAmbiguousElementDefinition[i].length; j++ ){
					definitions += "\n"+prefix+"<"+unexpectedAmbiguousElementDefinition[i][j].getQName()+"> at "+unexpectedAmbiguousElementDefinition[i][j].getLocation(restrictToFileName);
				}
				message += "\n"+prefix+"Unexpected element."
						+"\n"+prefix+"Element <"+activeInputDescriptor.getItemDescription(unexpectedAmbiguousElementInputRecordIndex[i])+"> at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unexpectedAmbiguousElementInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(unexpectedAmbiguousElementInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(unexpectedAmbiguousElementInputRecordIndex[i])
						+", corresponding to one of the schema definitions: "
                        +definitions						
						+"\n"+prefix+"is not part of the parent's content model." ;
			}
		}
		// {5}
		if(unknownAttributeIndex  >= 0){
			for(int i = 0; i <= unknownAttributeIndex; i++){
				message += "\n"+prefix+"Unknown attribute."
				+"\n"+prefix+"Attribute \""+activeInputDescriptor.getItemDescription(unknownAttributeInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unknownAttributeInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(unknownAttributeInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(unknownAttributeInputRecordIndex[i])
				+" is not known in the vocabulary described by the schema.";
			}
		}	
		// {6}
		if(unexpectedAttributeIndex  >= 0){
			for(int i = 0; i <= unexpectedAttributeIndex; i++){
				message += "\n"+prefix+"Unexpected attribute."
						+"\n"+prefix+"Attribute \""+activeInputDescriptor.getItemDescription(unexpectedAttributeInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unexpectedAttributeInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(unexpectedAttributeInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(unexpectedAttributeInputRecordIndex[i])+" corresponding to definition <"+unexpectedAttributeDefinition[i].getQName()+"> at "+unexpectedAttributeDefinition[i].getLocation(restrictToFileName)+" is not part of the parent's content model." ;
			}
		}
		// {7}
		if(unexpectedAmbiguousAttributeIndex >= 0){
			for(int i = 0; i <= unexpectedAmbiguousAttributeIndex; i++){
				String definitions = "";
				for(int j = 0; j < unexpectedAmbiguousAttributeDefinition[i].length; j++ ){
					definitions += "\n"+prefix+"<"+unexpectedAmbiguousAttributeDefinition[i][j].getQName()+"> at "+unexpectedAmbiguousAttributeDefinition[i][j].getLocation(restrictToFileName);
				}
				message += "\n"+prefix+"Unexpected attribute."
						+"\n"+prefix+"Attribute \""+activeInputDescriptor.getItemDescription(unexpectedAmbiguousAttributeInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unexpectedAmbiguousAttributeInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(unexpectedAmbiguousAttributeInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(unexpectedAmbiguousAttributeInputRecordIndex[i])
						+", corresponding to one of the definitions: "
                        +definitions						
						+"\n"+prefix+"is not part of the parent's content model." ;
			}
		}

		
		// {8}
		if(misplacedIndex  >= 0){
			for(int i = 0; i <= misplacedIndex; i++){
				message += "\n"+prefix+"Order error."
				+"\n"+prefix+"Misplaced content in the document structure starting at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(misplacedStartInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(misplacedStartInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(misplacedStartInputRecordIndex[i])+", corresponding to definition <"+misplacedContext[i].getQName()+"> at "+misplacedContext[i].getLocation(restrictToFileName)+ ":";
				for(int j = 0; j < misplacedDefinition[i].length; j++){
					for(int k = 0; k < misplacedInputRecordIndex[i][j].length; k++){
						message += "\n"+prefix+getItemDescription( misplacedInputRecordIndex[i][j][k])+" at "+getLocation(restrictToFileName,  activeInputDescriptor.getSystemId(misplacedInputRecordIndex[i][j][k]))+":"+ activeInputDescriptor.getLineNumber(misplacedInputRecordIndex[i][j][k])+":"+ activeInputDescriptor.getColumnNumber(misplacedInputRecordIndex[i][j][k]);
					}
					message += ", corresponding to definition <"+misplacedDefinition[i][j].getQName()+"> at "+misplacedDefinition[i][j].getLocation(restrictToFileName);
				}
				message += ".";				
			}
		}
		// {9}
		if(excessiveIndex  >= 0){
			for(int i = 0; i <= excessiveIndex; i++){
				message += "\n"+prefix+"Excessive content."
						+"\n"+prefix+"In the document structure starting at "+getLocation(restrictToFileName,  activeInputDescriptor.getSystemId(excessiveStartInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(excessiveStartInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(excessiveStartInputRecordIndex[i])+", corresponding to definition <"+excessiveContext[i].getQName()+"> at "+excessiveContext[i].getLocation(restrictToFileName)+", "
						+" expected "+getExpectedCardinality(excessiveDefinition[i].getMinOccurs(), excessiveDefinition[i].getMaxOccurs())+" corresponding to definition <"+excessiveDefinition[i].getQName()+"> at "+excessiveDefinition[i].getLocation(restrictToFileName)+", found "+excessiveInputRecordIndex[i].length+" starting at: ";
				for(int j = 0; j < excessiveInputRecordIndex[i].length; j++){
					message += "\n"+prefix+getItemDescription(excessiveInputRecordIndex[i][j])+" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(excessiveInputRecordIndex[i][j]))+":"+activeInputDescriptor.getLineNumber(excessiveInputRecordIndex[i][j])+":"+activeInputDescriptor.getColumnNumber(excessiveInputRecordIndex[i][j]);
				}
				message += ".";
			}
		}
		// {10}
		if(missingIndex >= 0){
			for(int i = 0; i <= missingIndex; i++){
				int found = missingFound[i];				
				if(found > 0){
					message += "\n"+prefix+"Missing content."
							+"\n"+prefix+"In the document structure starting at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(missingStartInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(missingStartInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(missingStartInputRecordIndex[i])+", corresponding to definition <"+missingContext[i].getQName()+"> at "+missingContext[i].getLocation(restrictToFileName)+", "
							+"expected "+getExpectedCardinality(missingDefinition[i].getMinOccurs(), missingDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingDefinition[i].getQName()+"> at "+missingDefinition[i].getLocation(restrictToFileName)+", found "+found+": ";
					for(int j = 0; j < missingInputRecordIndex[i].length; i++){
						message += "\n"+prefix+"<"+activeInputDescriptor.getItemDescription(missingInputRecordIndex[i][j])+"> at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(missingInputRecordIndex[i][j]))+":"+activeInputDescriptor.getLineNumber(missingInputRecordIndex[i][j])+":"+activeInputDescriptor.getColumnNumber(missingInputRecordIndex[i][j]);
					}
				}else{
					message += "\n"+prefix+"Missing content."
							+"\n"+prefix+"In the document structure starting at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(missingStartInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(missingStartInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(missingStartInputRecordIndex[i])+", corresponding to definition <"+missingContext[i].getQName()+"> at "+missingContext[i].getLocation(restrictToFileName)+", "
							+"expected "+getExpectedCardinality(missingDefinition[i].getMinOccurs(), missingDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingDefinition[i].getQName()+"> at "+missingDefinition[i].getLocation(restrictToFileName)+", found "+found;
				}
				message += ".";
			}			
		}		
		// {11}
		if(illegalIndex >= 0){
			for(int i = 0; i <= illegalIndex; i++){
				message += "\n"+prefix+"Illegal content."
							+"\n"+prefix+"The document structure starting with "+getItemDescription(illegalStartInputRecordIndex[i])+" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(illegalStartInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(illegalStartInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(illegalStartInputRecordIndex[i])+" does not match schema definition <"+illegalContext[i].getQName()+"> at "+illegalContext[i].getLocation(restrictToFileName)+".";
			}			
		}
		// {12 A}
		if(unresolvedAmbiguousElementIndexEE >= 0){
			for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
				message += "\n"+prefix+"Unresolved element."
						+"\n"+prefix+"Element <"+activeInputDescriptor.getItemDescription(unresolvedAmbiguousElementInputRecordIndexEE[i]) + "> at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unresolvedAmbiguousElementInputRecordIndexEE[i]))+":"+activeInputDescriptor.getLineNumber(unresolvedAmbiguousElementInputRecordIndexEE[i])+":"+activeInputDescriptor.getColumnNumber(unresolvedAmbiguousElementInputRecordIndexEE[i])
						+", ambiguous after content validation, cannot be resolved by in context validation either, all candidates resulted in errors. Possible definitions: ";
				for(int j = 0; j < unresolvedAmbiguousElementDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedAmbiguousElementDefinitionEE[i][j].getQName()+"> at "+unresolvedAmbiguousElementDefinitionEE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		// {12 U}
		if(unresolvedUnresolvedElementIndexEE >= 0){
			for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
				message += "\n"+prefix+"Unresolved element."
						+"\n"+prefix+"Element <"+activeInputDescriptor.getItemDescription(unresolvedUnresolvedElementInputRecordIndexEE[i]) + "> at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unresolvedUnresolvedElementInputRecordIndexEE[i]))+":"+activeInputDescriptor.getLineNumber(unresolvedUnresolvedElementInputRecordIndexEE[i])+":"+activeInputDescriptor.getColumnNumber(unresolvedUnresolvedElementInputRecordIndexEE[i])
						+", unresolved by content validation, cannot be resolved by in context validation either, all candidates resulted in errors.";
				
			}
		}
		// {13}
		if(unresolvedAttributeIndexEE >= 0){
			for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
				message += "\n"+prefix+"Unresolved attribute."
						+"\n"+prefix+"Attribute \""+activeInputDescriptor.getItemDescription(unresolvedAttributeInputRecordIndexEE[i]) + "\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unresolvedAttributeInputRecordIndexEE[i]))+":"+activeInputDescriptor.getLineNumber(unresolvedAttributeInputRecordIndexEE[i])+":"+activeInputDescriptor.getColumnNumber(unresolvedAttributeInputRecordIndexEE[i])
						+" cannot be resolved, all candidates resulted in errors. Available definitions: ";
				for(int j = 0; j < unresolvedAttributeDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedAttributeDefinitionEE[i][j].getQName()+"> at "+unresolvedAttributeDefinitionEE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		// {14}

		// {15}
		if(datatypeCharsIndex >= 0){
			for(int i = 0; i <= datatypeCharsIndex; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(datatypeCharsInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(datatypeCharsInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(datatypeCharsInputRecordIndex[i])
				+ " does not match the datatype required by schema definition <" +datatypeCharsDefinition[i].getQName()+"> at "+datatypeCharsDefinition[i].getLocation(restrictToFileName)+". "
				+ datatypeCharsErrorMessage[i];
			}
		}
		// {16}
		if(datatypeAVIndex >= 0){
			for(int i = 0; i <= datatypeAVIndex; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "Value of attribute \""+ activeInputDescriptor.getItemDescription(datatypeAVInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(datatypeAVInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(datatypeAVInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(datatypeAVInputRecordIndex[i])
				+ " does not match the datatype required by schema definition <" +datatypeAVDefinition[i].getQName()+"> at "+datatypeAVDefinition[i].getLocation(restrictToFileName)+". "
				+ datatypeAVErrorMessage[i];
			}
		}
		// {17}
		if(valueCharsIndex >= 0){
			for(int i = 0; i <= valueCharsIndex; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(valueCharsInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(valueCharsInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(valueCharsInputRecordIndex[i])
				+ " does not match the value required by schema definition <" +valueCharsDefinition[i].getQName()+"> at "+valueCharsDefinition[i].getLocation(restrictToFileName)+".";
			}
		}
		// {18}
		if(valueAVIndex >= 0){
			for(int i = 0; i <= valueAVIndex; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "Value of attribute \""+activeInputDescriptor.getItemDescription(valueAVInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(valueAVInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(valueAVInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(valueAVInputRecordIndex[i])
				+ " does not match the value required by schema definition <" +valueAVDefinition[i].getQName()+"> at "+valueAVDefinition[i].getLocation(restrictToFileName)+".";
			}
		}
		// {19}
		if(exceptCharsIndex >= 0){
			for(int i = 0; i <= exceptCharsIndex; i++){
				message += "\n"+prefix+"Excepted character content."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(exceptCharsInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(exceptCharsInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(exceptCharsInputRecordIndex[i])
				+ " matches a value excepted by schema definition <" +exceptCharsDefinition[i].getQName()+"> at "+exceptCharsDefinition[i].getLocation(restrictToFileName)+".";
			}
		}
		// {20}
		if(exceptAVIndex >= 0){
			for(int i = 0; i <= exceptAVIndex; i++){
				message += "\n"+prefix+"Excepted attribute value"
				+"\n"+prefix+ "Value of attribute \""+activeInputDescriptor.getItemDescription(exceptAVInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(exceptAVInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(exceptAVInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(exceptAVInputRecordIndex[i])
				+ " matches a value excepted by schema definition <" +exceptAVDefinition[i].getQName()+"> at "+exceptAVDefinition[i].getLocation(restrictToFileName)+".";
			}
		}
		// {21}
		if(unexpectedCharsIndex >= 0){
			for(int i = 0; i <= unexpectedCharsIndex; i++){
				message += "\n"+prefix+"Unexpected character content."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unexpectedCharsInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(unexpectedCharsInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(unexpectedCharsInputRecordIndex[i])
				+ " is not allowed by the element's schema definition <" +unexpectedCharsDefinition[i].getQName()+"> at "+unexpectedCharsDefinition[i].getLocation(restrictToFileName)+".";
			}
		}
		// {22}
		if(unexpectedAVIndex >= 0){
			for(int i = 0; i <= unexpectedAVIndex; i++){
				message += "\n"+prefix+"Unexpected attribute value."
				+"\n"+prefix+ "Value of attribute \""+activeInputDescriptor.getItemDescription(unexpectedAVInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unexpectedAVInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(unexpectedAVInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(unexpectedAVInputRecordIndex[i])
				+ " is not allowed by the attributes's schema definition <" +unexpectedAVDefinition[i].getQName()+"> at "+unexpectedAVDefinition[i].getLocation(restrictToFileName)+".";
			}
		}
		// {23}
		if(unresolvedCharsIndexEE >= 0){
			for(int i = 0; i <= unresolvedCharsIndexEE; i++){
				message += "\n"+prefix+"Unresolved character content."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unresolvedCharsInputRecordIndexEE[i]) )+":"+activeInputDescriptor.getLineNumber(unresolvedCharsInputRecordIndexEE[i])+":"+activeInputDescriptor.getColumnNumber(unresolvedCharsInputRecordIndexEE[i])
				+ " cannot be resolved to one schema definition, all candidates resulted in errors."
				+" Available definitions:";
				for(int j = 0; j < unresolvedCharsDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedCharsDefinitionEE[i][j].getQName()+"> at "+unresolvedCharsDefinitionEE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		// {24}
		if(unresolvedAVIndexEE >= 0){			
			for(int i = 0; i <= unresolvedAVIndexEE; i++){				
				message += "\n"+prefix+"Unresolved attribute value."
				+"\n"+prefix+ "Value of attribute \""+activeInputDescriptor.getItemDescription(unresolvedAVInputRecordIndexEE[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unresolvedAVInputRecordIndexEE[i]))+":"+activeInputDescriptor.getLineNumber(unresolvedAVInputRecordIndexEE[i])+":"+activeInputDescriptor.getColumnNumber(unresolvedAVInputRecordIndexEE[i])
				+ " cannot be resolved to one schema definition, all candidates resulted in errors."
				+" Available definitions:";
				for(int j = 0; j < unresolvedAVDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedAVDefinitionEE[i][j].getQName()+"> at "+unresolvedAVDefinitionEE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		// {25}
		if(datatypeTokenIndex >= 0){
			for(int i = 0; i <= datatypeTokenIndex; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "List token \""+activeInputDescriptor.getStringValue(datatypeTokenInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(datatypeTokenInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(datatypeTokenInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(datatypeTokenInputRecordIndex[i])
				+ " does not match the datatype required by schema definition <" +datatypeTokenDefinition[i].getQName()+"> at "+datatypeTokenDefinition[i].getLocation(restrictToFileName)+". "
				+ datatypeTokenErrorMessage[i];
			}
		}
		// {26}
		if(valueTokenIndex >= 0){
			for(int i = 0; i <= valueTokenIndex; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "List token \""+activeInputDescriptor.getStringValue(valueTokenInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(valueTokenInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(valueTokenInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(valueTokenInputRecordIndex[i])
				+ " does not match the value required by schema definition <"+valueTokenDefinition[i].getQName()+"> at "+valueTokenDefinition[i].getLocation(restrictToFileName)+".";
			}
		}
		// {27}
		if(exceptTokenIndex >= 0){
			for(int i = 0; i <= exceptTokenIndex; i++){
				message += "\n"+prefix+"Excepted token."
				+"\n"+prefix+ "List token \""+activeInputDescriptor.getStringValue(exceptTokenInputRecordIndex[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(exceptTokenInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(exceptTokenInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(exceptTokenInputRecordIndex[i])
				+ " matches a value excepted by schema definition <"+exceptTokenDefinition[i].getQName()+"> at "+exceptTokenDefinition[i].getLocation(restrictToFileName)+".";
			}
		}
		// {28}
        
        // {28_1}
        if(unresolvedTokenIndexLPICE >= 0){
			for(int i = 0; i <= unresolvedTokenIndexLPICE; i++){
				message += "\n"+prefix+"Unresolved list token."
				+"\n"+prefix+ "List token \""+ activeInputDescriptor.getStringValue(unresolvedTokenInputRecordIndexLPICE[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(unresolvedTokenInputRecordIndexLPICE[i]))+":"+activeInputDescriptor.getLineNumber(unresolvedTokenInputRecordIndexLPICE[i])+":"+activeInputDescriptor.getColumnNumber(unresolvedTokenInputRecordIndexLPICE[i])
				+ " cannot be resolved to a single schema definition, all candidates resulted in errors."
				+ " Available definitions: ";
				for(int j = 0; j < unresolvedTokenDefinitionLPICE[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedTokenDefinitionLPICE[i][j].getQName()+"> at "+unresolvedTokenDefinitionLPICE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		
		// {29}
		if(missingCompositorContentIndex >= 0){
			for(int i = 0; i <= missingCompositorContentIndex; i++){
				message += "\n"+prefix+"Missing compositor content."
						+"\n"+prefix+"In the document structure starting at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(missingCompositorContentStartInputRecordIndex[i]))+":"+activeInputDescriptor.getLineNumber(missingCompositorContentStartInputRecordIndex[i])+":"+activeInputDescriptor.getColumnNumber(missingCompositorContentStartInputRecordIndex[i])+", corresponding to definition <"+missingCompositorContentContext[i].getQName()+"> at "+missingCompositorContentContext[i].getLocation(restrictToFileName)+", "
						+"expected "+getExpectedCardinality(missingCompositorContentDefinition[i].getMinOccurs(), missingCompositorContentDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingCompositorContentDefinition[i].getQName()+"> at "+missingCompositorContentDefinition[i].getLocation(restrictToFileName)+", found "+missingCompositorContentFound[i]+". ";
			}			
		}


        if(!message.equals("")){            
            message = getErrorIntro(prefix, restrictToFileName) + message;
        }
		/*isMessageRetrieved = true;*/
		return message;
	} 
    
    String getErrorIntro(String prefix, boolean restrictToFileName){
        String intro = "";        
        // TODO is context type still used?
        if(reportingContextType == ContextErrorHandler.ELEMENT){
            if(conflictResolutionId == RESOLVED){
                if(reportingContextDefinition == null){
                    intro = "\n"+prefix+"Syntax error. Element <"+reportingContextQName+"> contains errors: ";
                }else{
                    intro = "\n"+prefix+"Syntax error. Element <"+reportingContextQName+"> corresponding to definition <"+reportingContextDefinition.getQName()+"> at "+reportingContextDefinition.getLocation(restrictToFileName)+" contains errors: ";
                }
            }else if(conflictResolutionId == AMBIGUOUS){
                intro = "\n"+prefix+"Syntax error. Ambiguous element <"+reportingContextQName+"> contains errors common to all possible definitions: ";
            }else if(conflictResolutionId == UNRESOLVED){
                intro = "\n"+prefix+"Syntax error. Unresolved element <"+reportingContextQName+"> contains errors common to all possible definitions: ";
            }
        }else if(reportingContextType == ContextErrorHandler.ROOT){
            if(reportingContextQName == null) reportingContextQName = "root of the document";
            if(conflictResolutionId == RESOLVED){
                if(reportingContextDefinition == null){
                    intro = "\n"+prefix+"Syntax error. Error at the root of the document: ";
                }else{
                    intro = "\n"+prefix+"Syntax error. Error at the root of the document corresponding to definition <"+reportingContextDefinition.getQName()+"> at "+reportingContextDefinition.getLocation(restrictToFileName)+": ";
                }
            }else if(conflictResolutionId == AMBIGUOUS){
                intro = "\n"+prefix+"Syntax error. Error at the ambiguous root of the document common to all possible definitions: ";
                //throw new IllegalStateException();
            }else if(conflictResolutionId == UNRESOLVED){
                intro = "\n"+prefix+"Syntax error. Error at the unresolved root of the document common to all possible definitions: ";
                //throw new IllegalStateException();
            }
        }
        return intro;
    }
    
    String getWarningIntro(String prefix, boolean restrictToFileName){        
        String intro = "";      
         // TODO is context type still used?
        if(reportingContextType == ContextErrorHandler.ELEMENT){
            if(conflictResolutionId == RESOLVED){
                if(reportingContextDefinition == null){
                    intro = "\n"+prefix+"Syntax warning. Element <"+reportingContextQName+"> generates warning: ";
                }else{
                    intro = "\n"+prefix+"Syntax warning. Element <"+reportingContextQName+"> corresponding to definition <"+reportingContextDefinition.getQName()+"> at "+reportingContextDefinition.getLocation(restrictToFileName)+" generates warning: ";
                }
            }else if(conflictResolutionId == AMBIGUOUS){
                intro = "\n"+prefix+"Syntax warning. Ambiguous element <"+reportingContextQName+"> generates warnings common to all possible definitions: ";
            }else if(conflictResolutionId == UNRESOLVED){
                
                intro = "\n"+prefix+"Syntax warning. Unresolved element <"+reportingContextQName+"> generates warnings common to all possible definitions: ";
            }
        }else if(reportingContextType == ContextErrorHandler.ROOT){
            if(reportingContextQName == null) reportingContextQName = "root of the document";
            if(conflictResolutionId == RESOLVED){
                if(reportingContextDefinition == null){
                    intro = "\n"+prefix+"Syntax warning. Warning at the root of the document: ";
                }else{
                    intro = "\n"+prefix+"Syntax warning. Warning at the root of the document corresponding to definition <"+reportingContextDefinition.getQName()+"> at "+reportingContextDefinition.getLocation(restrictToFileName)+": ";
                }
            }else if(conflictResolutionId == AMBIGUOUS){
                intro = "\n"+prefix+"Syntax warning. Warning at the ambiguous root of the document common to all possible definitions: ";
                //throw new IllegalStateException();
            }else if(conflictResolutionId == UNRESOLVED){
                intro = "\n"+prefix+"Syntax warning. Warning at the unresolved root of the document common to all possible definitions: ";
                //throw new IllegalStateException();
            }
        }
        return intro;
    }
    
	String getExpectedCardinality(int expectedMin, int expectedMax){
		if(expectedMax == 1){
			if(expectedMin == 0) return "at most 1 occurrence";
			else return "1 occurrence";
		}
		
		if(expectedMin == 0){
			return "0 or more occurrences";
		}else{
			return "1 or more occurrences";
		}
	}
    
    
    String getValidationWarningMessage(String prefix, boolean restrictToFileName){
		String message = "";
		// {w1 U}
		if(ambiguousUnresolvedElementIndexWW >= 0){
			for(int i = 0; i <= ambiguousUnresolvedElementIndexWW; i++){
				message += "\n"+prefix+"Ambiguous element."
						+"\n"+prefix+"Element <"+activeInputDescriptor.getItemDescription(ambiguousUnresolvedElementInputRecordIndexWW[i]) + "> at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(ambiguousUnresolvedElementInputRecordIndexWW[i]))+":"+activeInputDescriptor.getLineNumber(ambiguousUnresolvedElementInputRecordIndexWW[i])+":"+activeInputDescriptor.getColumnNumber(ambiguousUnresolvedElementInputRecordIndexWW[i])
						+", unresolved by content validation, cannot be resolved by in context validation, all candidates could be correct.";				
			}
		}
		// {w1 A}
		if(ambiguousAmbiguousElementIndexWW >= 0){
			for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
				message += "\n"+prefix+"Ambiguous element."
						+"\n"+prefix+"Element <"+activeInputDescriptor.getItemDescription(ambiguousAmbiguousElementInputRecordIndexWW[i]) + "> at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(ambiguousAmbiguousElementInputRecordIndexWW[i]))+":"+activeInputDescriptor.getLineNumber(ambiguousAmbiguousElementInputRecordIndexWW[i])+":"+activeInputDescriptor.getColumnNumber(ambiguousAmbiguousElementInputRecordIndexWW[i])
						+", ambiguous after content validation, cannot be desambiguated by in context validation, all candidates could be correct. Possible definitions:";
				for(int j = 0; j < ambiguousAmbiguousElementDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousAmbiguousElementDefinitionWW[i][j].getQName()+"> at "+ambiguousAmbiguousElementDefinitionWW[i][j].getLocation(restrictToFileName);
				}
				message += ".";				
			}
		}
		
		// {w2}
		if(ambiguousAttributeIndexWW >= 0){
			for(int i = 0; i <= ambiguousAttributeIndexWW; i++){
				message += "\n"+prefix+"Ambiguous attribute."
						+"\n"+prefix+"Attribute \""+activeInputDescriptor.getItemDescription(ambiguousAttributeInputRecordIndexWW[i]) + "\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(ambiguousAttributeInputRecordIndexWW[i]))+":"+activeInputDescriptor.getLineNumber(ambiguousAttributeInputRecordIndexWW[i])+":"+activeInputDescriptor.getColumnNumber(ambiguousAttributeInputRecordIndexWW[i])
						+" cannot be resolved to a single definition, several candidates could be correct. Possible definitions: ";
				for(int j = 0; j < ambiguousAttributeDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousAttributeDefinitionWW[i][j].getQName()+"> at "+ambiguousAttributeDefinitionWW[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		// {w3}
		if(ambiguousCharsIndexWW >= 0){
			for(int i = 0; i <= ambiguousCharsIndexWW; i++){
				message += "\n"+prefix+"Ambiguous character content."
						+"\n"+prefix+"Character content at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(ambiguousCharsInputRecordIndexWW[i]))+":"+activeInputDescriptor.getLineNumber(ambiguousCharsInputRecordIndexWW[i])+":"+activeInputDescriptor.getColumnNumber(ambiguousCharsInputRecordIndexWW[i])
						+" cannot be resolved to a single definition, several candidates could be correct. Possible definitions: ";
				for(int j = 0; j < ambiguousCharsDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousCharsDefinitionWW[i][j].getQName()+"> at "+ambiguousCharsDefinitionWW[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		
		// {w4}
		if(ambiguousAVIndexWW >= 0){
			for(int i = 0; i <= ambiguousAVIndexWW; i++){
				message += "\n"+prefix+"Ambiguous attribute value."
						+"\n"+prefix+"Value of attribute \""+activeInputDescriptor.getItemDescription(ambiguousAVInputRecordIndexWW[i])+"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(ambiguousAVInputRecordIndexWW[i]))+":"+activeInputDescriptor.getLineNumber(ambiguousAVInputRecordIndexWW[i])+":"+activeInputDescriptor.getColumnNumber(ambiguousAVInputRecordIndexWW[i])
						+" cannot be resolved to a single definition, several candidates could be correct. Possible definitions: ";
				for(int j = 0; j < ambiguousAVDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousAVDefinitionWW[i][j].getQName()+"> at "+ambiguousAVDefinitionWW[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
        
        // {28_2}
        if(ambiguousTokenIndexLPICW >= 0){
			for(int i = 0; i <= ambiguousTokenIndexLPICW; i++){
				message += "\n"+prefix+"Ambiguous list token."
				+"\n"+prefix+ "List token \""+ activeInputDescriptor.getStringValue(ambiguousTokenInputRecordIndexLPICW[i]) +"\" at "+getLocation(restrictToFileName, activeInputDescriptor.getSystemId(ambiguousTokenInputRecordIndexLPICW[i]))+":"+activeInputDescriptor.getLineNumber(ambiguousTokenInputRecordIndexLPICW[i])+":"+activeInputDescriptor.getColumnNumber(ambiguousTokenInputRecordIndexLPICW[i])
				+ " cannot be resolved to a single schema definition, several candidates could be correct."
				+ " Possible definitions: ";
				for(int j = 0; j < ambiguousTokenDefinitionLPICW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousTokenDefinitionLPICW[i][j].getQName()+"> at "+ambiguousTokenDefinitionLPICW[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
         
        if(!message.equals("")){            
            message = getWarningIntro(prefix, restrictToFileName) + message;
        }
        
		return message;
	}	
    
    public String toString(){
        return "AbstractMessageHandler "+getErrorMessage("", true);
    }
    
    private String getLocation(boolean restrictToFileName, String systemId){     
        if(systemId == null || !restrictToFileName)return systemId;
        int nameIndex = systemId.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = systemId.lastIndexOf('/')+1;
        return systemId.substring(nameIndex);	
    }
    
    /*private String getItemDescription(int itemId, String qName){
        String description = null;
        if(itemId == InputStackDescriptor.ELEMENT){
            description = "element <"+qName+">";
        }else if(itemId == InputStackDescriptor.ATTRIBUTE){
            description = "attribute \""+qName+"\"";
        }else if(itemId == InputStackDescriptor.CHARACTER_CONTENT){
            description = "character content";
        }else if(itemId == InputStackDescriptor.LIST_TOKEN){
            description = "list token \""+qName+"\"";
        }
        return description;
    }*/
    
    
    private String getItemDescription(int recordIndex){
        String description = null;
        int itemId = activeInputDescriptor.getItemId(recordIndex);
        if(itemId == InputStackDescriptor.ELEMENT){
            description = "element <"+activeInputDescriptor.getItemDescription(recordIndex)+">";
        }else if(itemId == InputStackDescriptor.ATTRIBUTE){
            description = "attribute \""+activeInputDescriptor.getItemDescription(recordIndex)+"\"";
        }else if(itemId == InputStackDescriptor.CHARACTER_CONTENT){
            description = "character content";
        }else if(itemId == InputStackDescriptor.LIST_TOKEN){
            description = "list token \""+activeInputDescriptor.getStringValue(recordIndex)+"\"";
        }
        return description;
    }
}
