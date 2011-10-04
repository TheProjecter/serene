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

public abstract class AbstractMessageHandler  extends AbstractMessageReporter{	
    
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
	
	// {w4}
	String[] ambiguousAVAttributeQNameWW;
	String[] ambiguousAVSystemIdWW;
	int[] ambiguousAVLineNumberWW;
	int[] ambiguousAVColumnNumberWW;
	CharsActiveTypeItem[][] ambiguousAVDefinitionWW;
	int ambiguousAVIndexWW;
	int ambiguousAVSizeWW;
	
	
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
	String unresolvedCharsSystemIdEECC[];//CC character content
	int unresolvedCharsLineNumberEECC[];
	int unresolvedCharsColumnNumberEECC[];
	CharsActiveTypeItem unresolvedPossibleDefinitionsCC[][];
	int unresolvedIndexCC;
	int unresolvedSizeCC;
	
	// {24}
	String unresolvedAttributeQNameEEAV[];
	String unresolvedCharsSystemIdEEAV[];//AV attribute unresolved
	int unresolvedCharsLineNumberEEAV[];
	int unresolvedCharsColumnNumberEEAV[];
	CharsActiveTypeItem unresolvedPossibleDefinitionsAV[][];
	int unresolvedIndexAV;
	int unresolvedSizeAV;
	
	
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
	    
    // {28_1}
	String unresolvedTokenLPICE[];//LPICE list pattern in context validation error
	String unresolvedCharsSystemIdEELPICE[];
	int unresolvedCharsLineNumberEELPICE[];
	int unresolvedCharsColumnNumberEELPICE[];
	CharsActiveTypeItem unresolvedPossibleDefinitionsLPICE[][];
	int unresolvedIndexLPICE;
	int unresolvedSizeLPICE;
    
    
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
    
    MessageReporter commonMessages; // It can be only one because it is about this context.
    int candidatesCount;
    BitSet disqualified;
    MessageReporter[] candidateMessages;
    
    
    int errorTotalCount;
		
	public AbstractMessageHandler(MessageWriter debugWriter){
		super(debugWriter);
			
        errorTotalCount = 0;  
        
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
        
        // {w4}
        ambiguousAVIndexWW = -1;
        ambiguousAVSizeWW = 0;
        
        
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
        unresolvedIndexCC = -1;
        unresolvedSizeCC = 0;
        
        // {24}
        unresolvedIndexAV = -1;
        unresolvedSizeAV = 0;
        
        
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

        // {28_1}
        unresolvedIndexLPICE = -1;
        unresolvedSizeLPICE = 0;
        
        
        // {28_2}
        ambiguousIndexLPICW = -1;
        ambiguousSizeLPICW = 0;
        
        
        // {29}
        missingCompositorContentIndex = -1;
        missingCompositorContentSize = 0;
        
        // {30}
	}  
    
    public void report(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher) throws SAXException{
        this.contextType = contextType;
        this.qName = qName;
        this.definition = definition;
        
        if(parent != null){
            parent.report(restrictToFileName, locator, errorDispatcher, "");//parent should have been located, else illegal state
        }
                
        //do the conflict
        if(disqualified != null){
            handleConflict(contextType, qName, restrictToFileName, locator, errorDispatcher, "");            
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
                else errorDispatcher.error(new SAXParseException(errorMessage, publicId, systemId, lineNumber, columnNumber));
            }
        }
        
        String warningMessage = getValidationWarningMessage("", restrictToFileName);
        if(warningMessage != null){
            warningMessage = warningMessage.trim();
            if(!warningMessage.equals("")){
                if(locator != null) errorDispatcher.warning(new SAXParseException(warningMessage, locator));
                else errorDispatcher.warning(new SAXParseException(warningMessage, publicId, systemId, lineNumber, columnNumber));
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
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, publicId, systemId, lineNumber, columnNumber));
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
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, publicId, systemId, lineNumber, columnNumber));
        }        
    }
        
    public void report(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
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
                else errorDispatcher.error(new SAXParseException(errorMessage, publicId, systemId, lineNumber, columnNumber));
            }
        }
        
        String warningMessage = getValidationWarningMessage(prefix, restrictToFileName);
        if(warningMessage != null){
            warningMessage = warningMessage.trim();
            if(!warningMessage.equals("")){
                if(locator != null) errorDispatcher.warning(new SAXParseException(warningMessage, locator));
                else errorDispatcher.warning(new SAXParseException(warningMessage, publicId, systemId, lineNumber, columnNumber));
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
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error.Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error.Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, publicId, systemId, lineNumber, columnNumber));
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
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, publicId, systemId, lineNumber, columnNumber));
        }
    }
    
    private String getFatalErrorMessage(){
		return "TODO";
	}
        
    
	
    public String getCandidateErrorMessage(String prefix, boolean restrictToFileName){
		if(definition == null){
			throw new IllegalStateException();
		}
        String message = "";
        
        String localPrefix = prefix+"\t";
        String localMessage = getErrorMessage(localPrefix, restrictToFileName); 
        
        if(!localMessage.equals("")){
            if(parent != null){
                String parentMessage = parent.getErrorMessage(localPrefix, restrictToFileName); 
                if(!parentMessage.equals(""))
                    message = "\n"+prefix+"Candidate definition <"+definition.getQName() +"> at "+definition.getLocation(restrictToFileName)+" contains errors: "
                            + parentMessage;
            }else{                
                message = "\n"+prefix+"Candidate definition <"+definition.getQName() +"> at "+definition.getLocation(restrictToFileName)+" contains errors: ";                
            }
            message += localMessage; 
        }else{
            if(parent != null){
                String parentMessage = parent.getErrorMessage(prefix, restrictToFileName); 
                if(!parentMessage.equals(""))
                    message = "\n"+prefix+"Candidate definition <"+definition.getQName() +"> at "+definition.getLocation(restrictToFileName)+" contains errors: "
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
        if(!message.equals(""))return "\n"+prefix+"Syntax error.Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message;
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
            return "\n"+prefix+"Syntax error. Candidate definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message;
        }
        return message;
    }
    
    
    String getValidationErrorMessage(String prefix, boolean restrictToFileName){
		// {2}
        String message = "";
		if(unknownElementIndex >= 0){
			for(int i = 0; i <= unknownElementIndex; i++){
				message += "\n"+prefix+"Unknown element."
				+"\n"+prefix+"Element <"+unknownElementQName[i]+"> at "+getLocation(restrictToFileName, unknownElementSystemId[i])+":"+unknownElementLineNumber[i]+":"+unknownElementColumnNumber[i]
				+" is not known in the vocabulary described by the schema.";
			}
		}	
		// {3}
		if(unexpectedElementIndex >= 0){
			for(int i = 0; i <= unexpectedElementIndex; i++){
				message += "\n"+prefix+"Unexpected element."
                +"\n"+prefix+"Element <"+unexpectedElementQName[i]+"> at "+getLocation(restrictToFileName, unexpectedElementSystemId[i])+":"+unexpectedElementLineNumber[i]+":"+unexpectedElementColumnNumber[i]+" corresponding to definition: <"+unexpectedElementDefinition[i].getQName()+"> at "+unexpectedElementDefinition[i].getLocation(restrictToFileName)+" is not part of the parent's content model." ;
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
						+"\n"+prefix+"Element <"+unexpectedAmbiguousElementQName[i]+"> at "+getLocation(restrictToFileName, unexpectedAmbiguousElementSystemId[i])+":"+unexpectedAmbiguousElementLineNumber[i]+":"+unexpectedAmbiguousElementColumnNumber[i]
						+", corresponding to one of the schema definitions: "
                        +definitions						
						+"\n"+prefix+"is not part of the parent's content model." ;
			}
		}
		// {5}
		if(unknownAttributeIndex  >= 0){
			for(int i = 0; i <= unknownAttributeIndex; i++){
				message += "\n"+prefix+"Unknown attribute."
				+"\n"+prefix+"Attribute \""+unknownAttributeQName[i]+"\" at "+getLocation(restrictToFileName, unknownAttributeSystemId[i])+":"+unknownAttributeLineNumber[i]+":"+unknownAttributeColumnNumber[i]
				+" is not known in the vocabulary described by the schema.";
			}
		}	
		// {6}
		if(unexpectedAttributeIndex  >= 0){
			for(int i = 0; i <= unexpectedAttributeIndex; i++){
				message += "\n"+prefix+"Unexpected attribute."
						+"\n"+prefix+"Attribute \""+unexpectedAttributeQName[i]+"\" at "+getLocation(restrictToFileName, unexpectedAttributeSystemId[i])+":"+unexpectedAttributeLineNumber[i]+":"+unexpectedAttributeColumnNumber[i]+" corresponding to definition <"+unexpectedAttributeDefinition[i].getQName()+"> at "+unexpectedAttributeDefinition[i].getLocation(restrictToFileName)+" is not part of the parent's content model." ;
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
						+"\n"+prefix+"Attribute \""+unexpectedAmbiguousAttributeQName[i]+"\" at "+getLocation(restrictToFileName, unexpectedAmbiguousAttributeSystemId[i])+":"+unexpectedAmbiguousAttributeLineNumber[i]+":"+unexpectedAmbiguousAttributeColumnNumber[i]
						+", corresponding to one of the definitions: "
                        +definitions						
						+"\n"+prefix+"is not part of the parent's content model." ;
			}
		}

		
		// {8}
		if(misplacedIndex  >= 0){
			for(int i = 0; i <= misplacedIndex; i++){
				message += "\n"+prefix+"Order error."
				+"\n"+prefix+"Misplaced content in the document structure starting at "+getLocation(restrictToFileName, misplacedStartSystemId[i])+":"+misplacedStartLineNumber[i]+":"+misplacedStartColumnNumber[i]+", corresponding to definition <"+misplacedContext[i].getQName()+"> at "+misplacedContext[i].getLocation(restrictToFileName)+ ":";
				for(int j = 0; j < misplacedDefinition[i].length; j++){
					for(int k = 0; k < misplacedQName[i][j].length; k++){
						message += "\n"+prefix+"<"+misplacedQName[i][j][k]+"> at "+getLocation(restrictToFileName, misplacedSystemId[i][j][k])+":"+misplacedLineNumber[i][j][k]+":"+misplacedColumnNumber[i][j][k];
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
						+"\n"+prefix+"In the document structure starting at "+getLocation(restrictToFileName, excessiveStartSystemId[i])+":"+excessiveStartLineNumber[i]+":"+excessiveStartColumnNumber[i]+", corresponding to definition <"+excessiveContext[i].getQName()+"> at "+excessiveContext[i].getLocation(restrictToFileName)+", "
						+" expected "+getExpectedCardinality(excessiveDefinition[i].getMinOccurs(), excessiveDefinition[i].getMaxOccurs())+" corresponding to definition <"+excessiveDefinition[i].getQName()+"> at "+excessiveDefinition[i].getLocation(restrictToFileName)+", found "+excessiveQName[i].length+": ";
				for(int j = 0; j < excessiveQName[i].length; j++){
					message += "\n"+prefix+"<"+excessiveQName[i][j]+"> at "+getLocation(restrictToFileName, excessiveSystemId[i][j])+":"+excessiveLineNumber[i][j]+":"+excessiveColumnNumber[i][j];
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
							+"\n"+prefix+"In the document structure starting at "+getLocation(restrictToFileName, missingStartSystemId[i])+":"+missingStartLineNumber[i]+":"+missingStartColumnNumber[i]+", corresponding to definition <"+missingContext[i].getQName()+"> at "+missingContext[i].getLocation(restrictToFileName)+", "
							+"expected "+getExpectedCardinality(missingDefinition[i].getMinOccurs(), missingDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingDefinition[i].getQName()+"> at "+missingDefinition[i].getLocation(restrictToFileName)+", found "+found+": ";
					for(int j = 0; j < missingQName[i].length; i++){
						message += "\n"+prefix+"<"+missingQName[i][j]+"> at "+getLocation(restrictToFileName, missingSystemId[i][j])+":"+missingLineNumber[i][j]+":"+missingColumnNumber[i][j];
					}
				}else{
					message += "\n"+prefix+"Missing content."
							+"\n"+prefix+"In the document structure starting at "+getLocation(restrictToFileName, missingStartSystemId[i])+":"+missingStartLineNumber[i]+":"+missingStartColumnNumber[i]+", corresponding to definition <"+missingContext[i].getQName()+"> at "+missingContext[i].getLocation(restrictToFileName)+", "
							+"expected "+getExpectedCardinality(missingDefinition[i].getMinOccurs(), missingDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingDefinition[i].getQName()+"> at "+missingDefinition[i].getLocation(restrictToFileName)+", found "+found;
				}
				message += ".";
			}			
		}		
		// {11}
		if(illegalIndex >= 0){
			for(int i = 0; i <= illegalIndex; i++){
				message += "\n"+prefix+"Illegal content."
							+"\n"+prefix+"The document structure starting with <"+illegalQName[i] +"> at "+getLocation(restrictToFileName, illegalStartSystemId[i])+":"+illegalStartLineNumber[i]+":"+illegalStartColumnNumber[i]+" does not match schema definition <"+illegalContext[i].getQName()+"> at "+illegalContext[i].getLocation(restrictToFileName)+".";
			}			
		}
		// {12 A}
		if(unresolvedAmbiguousElementIndexEE >= 0){
			for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
				message += "\n"+prefix+"Unresolved element."
						+"\n"+prefix+"Element <"+unresolvedAmbiguousElementQNameEE[i] + "> at "+getLocation(restrictToFileName, unresolvedAmbiguousElementSystemIdEE[i])+":"+unresolvedAmbiguousElementLineNumberEE[i]+":"+unresolvedAmbiguousElementColumnNumberEE[i]
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
						+"\n"+prefix+"Element <"+unresolvedUnresolvedElementQNameEE[i] + "> at "+getLocation(restrictToFileName, unresolvedUnresolvedElementSystemIdEE[i])+":"+unresolvedUnresolvedElementLineNumberEE[i]+":"+unresolvedUnresolvedElementColumnNumberEE[i]
						+", unresolved by content validation, cannot be resolved by in context validation either, all candidates resulted in errors.";
				
			}
		}
		// {13}
		if(unresolvedAttributeIndexEE >= 0){
			for(int i = 0; i <= unresolvedAttributeIndexEE; i++){
				message += "\n"+prefix+"Unresolved attribute."
						+"\n"+prefix+"Attribute \""+unresolvedAttributeQNameEE[i] + "\" at "+getLocation(restrictToFileName, unresolvedAttributeSystemIdEE[i])+":"+unresolvedAttributeLineNumberEE[i]+":"+unresolvedAttributeColumnNumberEE[i]
						+" cannot be resolved, all candidates resulted in errors. Available definitions: ";
				for(int j = 0; j < unresolvedAttributeDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedAttributeDefinitionEE[i][j].getQName()+"> at "+unresolvedAttributeDefinitionEE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		// {14}

		// {15}
		if(datatypeIndexCC >= 0){
			for(int i = 0; i <= datatypeIndexCC; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, datatypeCharsSystemIdCC[i])+":"+datatypeCharsLineNumberCC[i]+":"+datatypeCharsColumnNumberCC[i]
				+ " does not match the datatype required by schema definition <" +datatypeCharsDefinitionCC[i].getQName()+"> at "+datatypeCharsDefinitionCC[i].getLocation(restrictToFileName)+". "
				+ datatypeErrorMessageCC[i];
			}
		}
		// {16}
		if(datatypeIndexAV >= 0){
			for(int i = 0; i <= datatypeIndexAV; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "Value of attribute \""+datatypeAttributeQNameAV[i]+"\" at "+getLocation(restrictToFileName, datatypeCharsSystemIdAV[i])+":"+datatypeCharsLineNumberAV[i]+":"+datatypeCharsColumnNumberAV[i]
				+ " does not match the datatype required by schema definition <" +datatypeCharsDefinitionAV[i].getQName()+"> at "+datatypeCharsDefinitionAV[i].getLocation(restrictToFileName)+". "
				+ datatypeErrorMessageAV[i];
			}
		}
		// {17}
		if(valueIndexCC >= 0){
			for(int i = 0; i <= valueIndexCC; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, valueCharsSystemIdCC[i])+":"+valueCharsLineNumberCC[i]+":"+valueCharsColumnNumberCC[i]
				+ " does not match the value required by schema definition <" +valueCharsDefinitionCC[i].getQName()+"> at "+valueCharsDefinitionCC[i].getLocation(restrictToFileName)+".";
			}
		}
		// {18}
		if(valueIndexAV >= 0){
			for(int i = 0; i <= valueIndexAV; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "Value of attribute \""+valueAttributeQNameAV[i]+"\" at "+getLocation(restrictToFileName, valueCharsSystemIdAV[i])+":"+valueCharsLineNumberAV[i]+":"+valueCharsColumnNumberAV[i]
				+ " does not match the value required by schema definition <" +valueCharsDefinitionAV[i].getQName()+"> at "+valueCharsDefinitionAV[i].getLocation(restrictToFileName)+".";
			}
		}
		// {19}
		if(exceptIndexCC >= 0){
			for(int i = 0; i <= exceptIndexCC; i++){
				message += "\n"+prefix+"Excepted character content."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, exceptCharsSystemIdCC[i])+":"+exceptCharsLineNumberCC[i]+":"+exceptCharsColumnNumberCC[i]
				+ " matches a value excepted by schema definition <" +exceptCharsDefinitionCC[i].getQName()+"> at "+exceptCharsDefinitionCC[i].getLocation(restrictToFileName)+".";
			}
		}
		// {20}
		if(exceptIndexAV >= 0){
			for(int i = 0; i <= exceptIndexAV; i++){
				message += "\n"+prefix+"Excepted attribute value"
				+"\n"+prefix+ "Value of attribute \""+exceptAttributeQNameAV[i]+"\" at "+getLocation(restrictToFileName, exceptCharsSystemIdAV[i])+":"+exceptCharsLineNumberAV[i]+":"+exceptCharsColumnNumberAV[i]
				+ " matches a value excepted by schema definition <" +exceptCharsDefinitionAV[i].getQName()+"> at "+exceptCharsDefinitionAV[i].getLocation(restrictToFileName)+".";
			}
		}
		// {21}
		if(unexpectedIndexCC >= 0){
			for(int i = 0; i <= unexpectedIndexCC; i++){
				message += "\n"+prefix+"Unexpected character content."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, unexpectedCharsSystemIdCC[i])+":"+unexpectedCharsLineNumberCC[i]+":"+unexpectedCharsColumnNumberCC[i]
				+ " is not allowed by the element's schema definition <" +unexpectedContextDefinitionCC[i].getQName()+"> at "+unexpectedContextDefinitionCC[i].getLocation(restrictToFileName)+".";
			}
		}
		// {22}
		if(unexpectedIndexAV >= 0){
			for(int i = 0; i <= unexpectedIndexAV; i++){
				message += "\n"+prefix+"Unexpected attribute value."
				+"\n"+prefix+ "Value of attribute \""+unexpectedAttributeQName[i]+"\" at "+getLocation(restrictToFileName, unexpectedCharsSystemIdAV[i])+":"+unexpectedCharsLineNumberAV[i]+":"+unexpectedCharsColumnNumberAV[i]
				+ " is not allowed by the attributes's schema definition <" +unexpectedContextDefinitionAV[i].getQName()+"> at "+unexpectedContextDefinitionAV[i].getLocation(restrictToFileName)+".";
			}
		}
		// {23}
		if(unresolvedIndexCC >= 0){
			for(int i = 0; i <= unresolvedIndexCC; i++){
				message += "\n"+prefix+"Unresolved character content."
				+"\n"+prefix+ "Character content at "+getLocation(restrictToFileName, unresolvedCharsSystemIdEECC[i])+":"+unresolvedCharsLineNumberEECC[i]+":"+unresolvedCharsColumnNumberEECC[i]
				+ " cannot be resolved to one schema definition, all candidates resulted in errors."
				+" Available definitions:";
				for(int j = 0; j < unresolvedPossibleDefinitionsCC[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedPossibleDefinitionsCC[i][j].getQName()+"> at "+unresolvedPossibleDefinitionsCC[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		// {24}
		if(unresolvedIndexAV >= 0){			
			for(int i = 0; i <= unresolvedIndexAV; i++){				
				message += "\n"+prefix+"Unresolved attribute value."
				+"\n"+prefix+ "Value of attribute \""+unresolvedAttributeQNameEEAV[i]+"\" at "+getLocation(restrictToFileName, unresolvedCharsSystemIdEEAV[i])+":"+unresolvedCharsLineNumberEEAV[i]+":"+unresolvedCharsColumnNumberEEAV[i]
				+ " cannot be resolved to one schema definition, all candidates resulted in errors."
				+" Available definitions:";
				for(int j = 0; j < unresolvedPossibleDefinitionsAV[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedPossibleDefinitionsAV[i][j].getQName()+"> at "+unresolvedPossibleDefinitionsAV[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		// {25}
		if(datatypeIndexLP >= 0){
			for(int i = 0; i <= datatypeIndexLP; i++){
				message += "\n"+prefix+"Illegal datatype."
				+"\n"+prefix+ "List token \""+datatypeTokenLP[i]+"\" at "+getLocation(restrictToFileName, datatypeCharsSystemIdLP[i])+":"+datatypeCharsLineNumberLP[i]+":"+datatypeCharsColumnNumberLP[i]
				+ " does not match the datatype required by schema definition <" +datatypeCharsDefinitionLP[i].getQName()+"> at "+datatypeCharsDefinitionLP[i].getLocation(restrictToFileName)+". "
				+ datatypeErrorMessageLP[i];
			}
		}
		// {26}
		if(valueIndexLP >= 0){
			for(int i = 0; i <= valueIndexLP; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "List token \""+valueTokenLP[i]+"\" at "+getLocation(restrictToFileName, valueCharsSystemIdLP[i])+":"+valueCharsLineNumberLP[i]+":"+valueCharsColumnNumberLP[i]
				+ " does not match the value required by schema definition <"+valueCharsDefinitionLP[i].getQName()+"> at "+valueCharsDefinitionLP[i].getLocation(restrictToFileName)+".";
			}
		}
		// {27}
		if(exceptIndexLP >= 0){
			for(int i = 0; i <= exceptIndexLP; i++){
				message += "\n"+prefix+"Excepted token."
				+"\n"+prefix+ "List token \""+exceptTokenLP[i]+"\" at "+getLocation(restrictToFileName, exceptCharsSystemIdLP[i])+":"+exceptCharsLineNumberLP[i]+":"+exceptCharsColumnNumberLP[i]
				+ " matches a value excepted by schema definition <"+exceptCharsDefinitionLP[i].getQName()+"> at "+exceptCharsDefinitionLP[i].getLocation(restrictToFileName)+".";
			}
		}
		// {28}
        
        // {28_1}
        if(unresolvedIndexLPICE >= 0){
			for(int i = 0; i <= unresolvedIndexLPICE; i++){
				message += "\n"+prefix+"Unresolved list token."
				+"\n"+prefix+ "List token \""+unresolvedTokenLPICE[i]+"\" at "+getLocation(restrictToFileName, unresolvedCharsSystemIdEELPICE[i])+":"+unresolvedCharsLineNumberEELPICE[i]+":"+unresolvedCharsColumnNumberEELPICE[i]
				+ " cannot be resolved to a single schema definition, all candidates resulted in errors."
				+ " Available definitions: ";
				for(int j = 0; j < unresolvedPossibleDefinitionsLPICE[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedPossibleDefinitionsLPICE[i][j].getQName()+"> at "+unresolvedPossibleDefinitionsLPICE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
		
		// {29}
		if(missingCompositorContentIndex >= 0){
			for(int i = 0; i <= missingCompositorContentIndex; i++){
				message += "\n"+prefix+"Missing compositor content."
						+"\n"+prefix+"In the document structure starting at "+getLocation(restrictToFileName, missingCompositorContentStartSystemId[i])+":"+missingCompositorContentStartLineNumber[i]+":"+missingCompositorContentStartColumnNumber[i]+", corresponding to definition <"+missingCompositorContentContext[i].getQName()+"> at "+missingCompositorContentContext[i].getLocation(restrictToFileName)+", "
						+"expected "+getExpectedCardinality(missingCompositorContentDefinition[i].getMinOccurs(), missingCompositorContentDefinition[i].getMaxOccurs())+" corresponding to definition <"+missingCompositorContentDefinition[i].getQName()+"> at "+missingCompositorContentDefinition[i].getLocation(restrictToFileName)+", found "+missingCompositorContentFound[i]+". ";
			}			
		}


        if(!message.equals("")){            
            message = getErrorIntro(prefix, restrictToFileName) + message;
        }
		
		return message;
	} 
    
    String getErrorIntro(String prefix, boolean restrictToFileName){
        String intro = "";        
        if(contextType == ContextErrorHandler.ELEMENT){
            if(conflictResolutionId == RESOLVED){
                if(definition == null){
                    intro = "\n"+prefix+"Syntax error. Element <"+qName+"> contains errors: ";
                }else{
                    intro = "\n"+prefix+"Syntax error. Element <"+qName+"> corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName)+" contains errors: ";
                }
            }else if(conflictResolutionId == AMBIGUOUS){
                intro = "\n"+prefix+"Syntax error. Ambiguous element <"+qName+"> contains errors common to all possible definitions: ";
            }else if(conflictResolutionId == UNRESOLVED){
                intro = "\n"+prefix+"Syntax error. Unresolved element <"+qName+"> contains errors common to all possible definitions: ";
            }
        }else if(contextType == ContextErrorHandler.ROOT){
            if(qName == null) qName = "root of the document";
            if(conflictResolutionId == RESOLVED){
                if(definition == null){
                    intro = "\n"+prefix+"Syntax error. Error at the root of the document: ";
                }else{
                    intro = "\n"+prefix+"Syntax error. Error at the root of the document corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName)+": ";
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
        if(contextType == ContextErrorHandler.ELEMENT){
            if(conflictResolutionId == RESOLVED){
                if(definition == null){
                    intro = "\n"+prefix+"Syntax warning. Element <"+qName+"> generates warning: ";
                }else{
                    intro = "\n"+prefix+"Syntax warning. Element <"+qName+"> corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName)+" generates warning: ";
                }
            }else if(conflictResolutionId == AMBIGUOUS){
                intro = "\n"+prefix+"Syntax warning. Ambiguous element <"+qName+"> generates warnings common to all possible definitions: ";
            }else if(conflictResolutionId == UNRESOLVED){
                
                intro = "\n"+prefix+"Syntax warning. Unresolved element <"+qName+"> generates warnings common to all possible definitions: ";
            }
        }else if(contextType == ContextErrorHandler.ROOT){
            if(qName == null) qName = "root of the document";
            if(conflictResolutionId == RESOLVED){
                if(definition == null){
                    intro = "\n"+prefix+"Syntax warning. Warning at the root of the document: ";
                }else{
                    intro = "\n"+prefix+"Syntax warning. Warning at the root of the document corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName)+": ";
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
						+"\n"+prefix+"Element <"+ambiguousUnresolvedElementQNameWW[i] + "> at "+getLocation(restrictToFileName, ambiguousUnresolvedElementSystemIdWW[i])+":"+ambiguousUnresolvedElementLineNumberWW[i]+":"+ambiguousUnresolvedElementColumnNumberWW[i]
						+", unresolved by content validation, cannot be resolved by in context validation, all candidates could be correct.";				
			}
		}
		// {w1 A}
		if(ambiguousAmbiguousElementIndexWW >= 0){
			for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
				message += "\n"+prefix+"Ambiguous element."
						+"\n"+prefix+"Element <"+ambiguousAmbiguousElementQNameWW[i] + "> at "+getLocation(restrictToFileName, ambiguousAmbiguousElementSystemIdWW[i])+":"+ambiguousAmbiguousElementLineNumberWW[i]+":"+ambiguousAmbiguousElementColumnNumberWW[i]
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
						+"\n"+prefix+"Attribute \""+ambiguousAttributeQNameWW[i] + "\" at "+getLocation(restrictToFileName, ambiguousAttributeSystemIdWW[i])+":"+ambiguousAttributeLineNumberWW[i]+":"+ambiguousAttributeColumnNumberWW[i]
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
						+"\n"+prefix+"Character content at "+getLocation(restrictToFileName, ambiguousCharsSystemIdWW[i])+":"+ambiguousCharsLineNumberWW[i]+":"+ambiguousCharsColumnNumberWW[i]
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
						+"\n"+prefix+"Value of attribute \""+ambiguousAVAttributeQNameWW[i]+"\" at "+getLocation(restrictToFileName, ambiguousAVSystemIdWW[i])+":"+ambiguousAVLineNumberWW[i]+":"+ambiguousAVColumnNumberWW[i]
						+" cannot be resolved to a single definition, several candidates could be correct. Possible definitions: ";
				for(int j = 0; j < ambiguousAVDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousAVDefinitionWW[i][j].getQName()+"> at "+ambiguousAVDefinitionWW[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
		}
        
        // {28_2}
        if(ambiguousIndexLPICW >= 0){
			for(int i = 0; i <= ambiguousIndexLPICW; i++){
				message += "\n"+prefix+"Ambiguous list token."
				+"\n"+prefix+ "List token \""+ambiguousTokenLPICW[i]+"\" at "+getLocation(restrictToFileName, ambiguousCharsSystemIdEELPICW[i])+":"+ambiguousCharsLineNumberEELPICW[i]+":"+ambiguousCharsColumnNumberEELPICW[i]
				+ " cannot be resolved to a single schema definition, several candidates could be correct."
				+ " Possible definitions: ";
				for(int j = 0; j < ambiguousPossibleDefinitionsLPICW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousPossibleDefinitionsLPICW[i][j].getQName()+"> at "+ambiguousPossibleDefinitionsLPICW[i][j].getLocation(restrictToFileName);
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
}
