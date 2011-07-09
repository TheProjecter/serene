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

public class AbstractMessageHandler  implements MessageReporter{	
    MessageReporter parent;// the handler that is supposed to report before this when delayed in a conflict situation
    
    int contextType;
	String qName;
	AElement definition;
	String systemId;
	int lineNumber;
	int columnNumber;
    
    int conflictResolutionId;

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
    MessageReporter commonMessages; // It can be only one because it is about this context.
    int candidatesCount;
    BitSet disqualified;
    MessageReporter[] candidateMessages;
    
    
    int errorTotalCount;
	
	MessageWriter debugWriter;
	
	public AbstractMessageHandler(MessageWriter debugWriter){
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
        ambiguousSizeLPICE = 0;
        ambiguousSizeLPICW = 0;
		
		missingCompositorContentSize = 0;
        
        errorTotalCount = 0;        
	}	


    public void setConflictResolutionId(int conflictResolutionId){
        this.conflictResolutionId = conflictResolutionId;
    }
	public void setContextType(int contextType){
        this.contextType = contextType;
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
    
    
    public void setParent(MessageReporter parent){
        if(this.parent != null) throw new IllegalStateException();
        this.parent = parent;
    }
    
    
    
    public void report(int contextType, String qName, AElement definition, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        this.contextType = contextType;
        this.qName = qName;
        this.definition = definition;
                
        if(parent != null){
            // System.out.println("REPORT PARENT 1 "+qName);
            parent.report(locator, errorDispatcher, prefix);//parent should have been located, else illegal state
        }
                
        //do the conflict
        if(disqualified != null){            
            handleConflict(contextType, qName, locator, errorDispatcher, prefix);
            
            // report common as any other errors            
            if(commonMessages != null){
                // System.out.println("REPORT COMMON 1 "+qName+" "+commonMessages.hashCode());
                commonMessages.report(locator, errorDispatcher, prefix);                
            }
            
            return;
        }
        
        // System.out.println(hashCode()+" LOCAL MESSAGE 1 "+getValidationErrorMessage(""));
        
        String errorMessage = getValidationErrorMessage(prefix);
        errorMessage = errorMessage.trim();
        if(errorMessage != null && !errorMessage.equals("")) errorDispatcher.error(new SAXParseException(errorMessage, locator));
        
        String warningMessage = getValidationWarningMessage(prefix);
        warningMessage = warningMessage.trim();
        if(warningMessage != null && !warningMessage.equals("")) errorDispatcher.warning(new SAXParseException(warningMessage, locator));
    }
    
    private void handleConflict(int contextType, String qName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualified = candidatesCount - disqualified.cardinality();
        
        if(qualified == 0){
            reportUnresolved(contextType, qName, locator, errorDispatcher, prefix);
        }else if(qualified == 1){
            reportResolved(contextType, qName, locator, errorDispatcher, prefix);
        }else{
            reportAmbiguous(contextType, qName, locator, errorDispatcher, prefix);
        }
    }
    
    private void reportUnresolved(int contextType, String qName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            message += candidateMessages[i].getCandidateErrorMessage(prefix);
        }
        if(!message.equals(""))errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, locator));
        else throw new IllegalStateException();
    }
    
    private void reportResolved(int contextType, String qName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        //System.out.println(hashCode()+" REPORT RESOLVED 1 "+disqualified);
        int qualifiedIndex = disqualified.nextClearBit(0);        
        //System.out.println("qualifiedIndex "+qualifiedIndex);
        if(candidateMessages != null && candidateMessages[qualifiedIndex] != null) candidateMessages[qualifiedIndex].report(locator, errorDispatcher, prefix);        
    }
    
    private void reportAmbiguous(int contextType, String qName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            //System.out.println(disqualified+" "+candidateMessages);
            if(!disqualified.get(i) && candidateMessages != null && candidateMessages[i] != null){
                 message += candidateMessages[i].getCandidateErrorMessage(prefix);            
            }
        }
        if(!message.equals("")){            
            errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Possible definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, locator));
        }        
    }
        
    public void report(Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        //System.out.println(hashCode()+" REPORT 2 "+qName+"  "+getErrorMessageCount());                        
        if(parent != null){
            //System.out.println("REPORT PARENT 2 "+qName);
            parent.report(locator, errorDispatcher, prefix);//parent should have been located, else illegal state            
        }
        
        //do the conflict
        if(disqualified != null){
            handleConflict(locator, errorDispatcher, prefix);
            
            // report common as any other errors            
            if(commonMessages != null){
                //System.out.println("REPORT COMMON 2 "+qName);
                commonMessages.report(locator, errorDispatcher, prefix);                
            }
            return;
        }
        
        //System.out.println(hashCode()+" LOCAL MESSAGE 2 "+getValidationErrorMessage(""));
        
        String errorMessage = getValidationErrorMessage(prefix);
        errorMessage = errorMessage.trim();
        if(errorMessage != null && !errorMessage.equals("")) errorDispatcher.error(new SAXParseException(errorMessage, locator));
        
        String warningMessage = getValidationWarningMessage(prefix);
        warningMessage = warningMessage.trim();
        if(warningMessage != null && !warningMessage.equals("")) errorDispatcher.warning(new SAXParseException(warningMessage, locator));
    }
    
    private void handleConflict(Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualified = candidatesCount - disqualified.cardinality();
        
        if(qualified == 0){
            reportUnresolved(locator, errorDispatcher, prefix);
        }else if(qualified == 1){
            reportResolved(locator, errorDispatcher, prefix);
        }else{
            reportAmbiguous(locator, errorDispatcher, prefix);
        }
    }
    
    private void reportUnresolved(Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = "";
        for(int i = 0; i < candidatesCount; i++){
            message += candidateMessages[i].getCandidateErrorMessage(prefix);
        }
        if(!message.equals(""))errorDispatcher.error(new SAXParseException(prefix+"Syntax error.Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, locator));
    }
    
    private void reportResolved(Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        //System.out.println(hashCode()+" REPORT RESOLVED 2 "+disqualified);
        int qualifiedIndex = disqualified.nextClearBit(0);
        //System.out.println("qualifiedIndex "+qualifiedIndex);
        if(candidateMessages != null && candidateMessages[qualifiedIndex] != null) candidateMessages[qualifiedIndex].report(locator, errorDispatcher, prefix);
    }
    
    private void reportAmbiguous(Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            if(!disqualified.get(i)  && candidateMessages != null && candidateMessages[i] != null)message += candidateMessages[i].getCandidateErrorMessage(prefix);
        }
        message = message.trim();
        if(!message.equals("")){
            errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Possible definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, locator));
        }
    }
    
    private String getFatalErrorMessage(){
		return "TODO";
	}
        
    
	
    public String getCandidateErrorMessage(String prefix){
		if(definition == null){
			throw new IllegalStateException();
		}
        String message = "";
        
        String localPrefix = prefix+"\t";
        String localMessage = getErrorMessage(localPrefix); 
        
        if(!localMessage.equals("")){
            if(parent != null){
                String parentMessage = parent.getErrorMessage(localPrefix); 
                if(!parentMessage.equals(""))
                    message = "\n"+prefix+"Candidate definition <"+definition.getQName() +"> at "+definition.getLocation()+" contains errors: "
                            + parentMessage;
            }else{                
                message = "\n"+prefix+"Candidate definition <"+definition.getQName() +"> at "+definition.getLocation()+" contains errors: ";                
            }
            message += localMessage; 
        }else{
            if(parent != null){
                String parentMessage = parent.getErrorMessage(prefix); 
                if(!parentMessage.equals(""))
                    message = "\n"+prefix+"Candidate definition <"+definition.getQName() +"> at "+definition.getLocation()+" contains errors: "
                            + parentMessage;
            }
        }
        return message;
	}
       	
    
	public String getErrorMessage(String prefix){
		String message = "";		
        if(disqualified != null){
            message += getConflictErrorMessage(prefix);
        }else{
            message += getValidationErrorMessage(prefix);
        }
        return message;
    }
    
    private String getConflictErrorMessage(String prefix){
        int qualified = candidatesCount - disqualified.cardinality();
        
        if(qualified == 0){
            return getUnresolvedMessage(prefix);
        }else if(qualified == 1){
            return getResolvedMessage(prefix);
        }else{
            return getAmbiguousMessage(prefix);
        }
    }
    
    private String getUnresolvedMessage(String prefix){        
        String message = "";
        for(int i = 0; i < candidatesCount; i++){
            message += candidateMessages[i].getCandidateErrorMessage(prefix);
        }
        message = message.trim();
        if(!message.equals(""))return "\n"+prefix+"Syntax error.Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message;
        return message;
    }
    
    private String getResolvedMessage(String prefix){
        int qualifiedIndex = disqualified.nextClearBit(0);
        if(candidateMessages != null && candidateMessages[qualifiedIndex] != null)  return candidateMessages[qualifiedIndex].getErrorMessage(prefix);
        return null;
    }
    
    private String getAmbiguousMessage(String prefix){
        String message = "";
        for(int i = 0; i < candidatesCount; i++){
            if(!disqualified.get(i)){
                message += candidateMessages[i].getCandidateErrorMessage(prefix);                
            }
        }
        message = message.trim();
        if(!message.equals("")){
            return "\n"+prefix+"Syntax error. Possible definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message;
        }
        return message;
    }
    
    
    private String getValidationErrorMessage(String prefix){
		// {2}
        String message = "";
		if(unknownElementQName != null){
			for(int i = 0; i <= unknownElementIndex; i++){
				message += "\n"+prefix+"Unknown element."
				+"\n"+prefix+"Element <"+unknownElementQName[i]+"> at "+unknownElementSystemId[i]+":"+unknownElementLineNumber[i]+":"+unknownElementColumnNumber[i]
				+" is not known in the vocabulary described by the schema.";
			}
		}	
		// {3}
		if(unexpectedElementQName != null){
			for(int i = 0; i <= unexpectedElementIndex; i++){
				message += "\n"+prefix+"Unexpected element."
                +"\n"+prefix+"Element <"+unexpectedElementQName[i]+"> at "+unexpectedElementSystemId[i]+":"+unexpectedElementLineNumber[i]+":"+unexpectedElementColumnNumber[i]+" corresponding to definition: <"+unexpectedElementDefinition[i].getQName()+"> at "+unexpectedElementDefinition[i].getLocation()+" is not part of the parent's content model." ;
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
						+", corresponding to one of the schema definitions: "
                        +definitions						
						+"\n"+prefix+"is not part of the parent's content model." ;
			}
		}
		// {5}
		if(unknownAttributeQName != null){
			for(int i = 0; i <= unknownAttributeIndex; i++){
				message += "\n"+prefix+"Unknown attribute."
				+"\n"+prefix+"Attribute "+unknownAttributeQName[i]+" at "+unknownAttributeSystemId[i]+":"+unknownAttributeLineNumber[i]+":"+unknownAttributeColumnNumber[i]
				+" is not known in the vocabulary described by the schema.";
			}
		}	
		// {6}
		if(unexpectedAttributeQName != null){
			for(int i = 0; i <= unexpectedAttributeIndex; i++){
				message += "\n"+prefix+"Unexpected attribute."
						+"\n"+prefix+"Attribute "+unexpectedAttributeQName[i]+" at "+unexpectedAttributeSystemId[i]+":"+unexpectedAttributeLineNumber[i]+":"+unexpectedAttributeColumnNumber[i]+" corresponding to definition <"+unexpectedAttributeDefinition[i].getQName()+"> at "+unexpectedAttributeDefinition[i].getLocation()+" is not part of the parent's content model." ;
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
						+", corresponding to one of the definitions: "
                        +definitions						
						+"\n"+prefix+"is not part of the parent's content model." ;
			}
		}

		
		// {8}
		if(misplacedContext != null){
			for(int i = 0; i <= misplacedIndex; i++){
				message += "\n"+prefix+"Order error."
				+"\n"+prefix+"Misplaced content in the document structure starting at "+misplacedStartSystemId[i]+":"+misplacedStartLineNumber[i]+":"+misplacedStartColumnNumber[i]+", corresponding to definition <"+misplacedContext[i].getQName()+"> at "+misplacedContext[i].getLocation()+ ":";
				for(int j = 0; j < misplacedDefinition[i].length; j++){
					for(int k = 0; k < misplacedQName[i][j].length; k++){
						message += "\n"+prefix+"<"+misplacedQName[i][j][k]+"> at "+misplacedSystemId[i][j][k]+":"+misplacedLineNumber[i][j][k]+":"+misplacedColumnNumber[i][j][k];
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
				+"\n"+prefix+ "List token \""+datatypeTokenLP[i]+"\" at "+datatypeCharsSystemIdLP[i]+":"+datatypeCharsLineNumberLP[i]+":"+datatypeCharsColumnNumberLP[i]
				+ " does not match the datatype required by schema definition <" +datatypeCharsDefinitionLP[i].getQName()+"> at "+datatypeCharsDefinitionLP[i].getLocation()+". "
				+ datatypeErrorMessageLP[i];
			}
		}
		// {26}
		if(valueCharsSystemIdLP != null){
			for(int i = 0; i <= valueIndexLP; i++){
				message += "\n"+prefix+"Illegal value."
				+"\n"+prefix+ "List token \""+valueTokenLP[i]+"\" at "+valueCharsSystemIdLP[i]+":"+valueCharsLineNumberLP[i]+":"+valueCharsColumnNumberLP[i]
				+ " does not match the value required by schema definition <"+valueCharsDefinitionLP[i].getQName()+"> at "+valueCharsDefinitionLP[i].getLocation()+".";
			}
		}
		// {27}
		if(exceptCharsSystemIdLP != null){
			for(int i = 0; i <= exceptIndexLP; i++){
				message += "\n"+prefix+"Excepted token."
				+"\n"+prefix+ "List token \""+exceptTokenLP[i]+"\" at "+exceptCharsSystemIdLP[i]+":"+exceptCharsLineNumberLP[i]+":"+exceptCharsColumnNumberLP[i]
				+ " matches a value excepted by schema definition <"+exceptCharsDefinitionLP[i].getQName()+"> at "+exceptCharsDefinitionLP[i].getLocation()+".";
			}
		}
		// {28}
		if(ambiguousCharsSystemIdEELP != null){
			for(int i = 0; i <= ambiguousIndexLP; i++){
				message += "\n"+prefix+"Illegal ambiguous."
				+"\n"+prefix+ "List token \""+ambiguousTokenLP[i]+"\" at "+ambiguousCharsSystemIdEELP[i]+":"+ambiguousCharsLineNumberEELP[i]+":"+ambiguousCharsColumnNumberEELP[i]
				+ " cannot be resolved by datatype and structure validation to one schema definition, all candidates resulted in errors."
				+ " Possible definitions: ";
				for(int j = 0; j < ambiguousPossibleDefinitionsLP[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousPossibleDefinitionsLP[i][j].getQName()+"> at "+ambiguousPossibleDefinitionsLP[i][j].getLocation();
				}
				message += ".";
			}
		}
        
        // {28_1}
        if(ambiguousCharsSystemIdEELPICE != null){
			for(int i = 0; i <= ambiguousIndexLPICE; i++){
				message += "\n"+prefix+"Ambiguous list token."
				+"\n"+prefix+ "List token \""+ambiguousTokenLPICE[i]+"\" at "+ambiguousCharsSystemIdEELPICE[i]+":"+ambiguousCharsLineNumberEELPICE[i]+":"+ambiguousCharsColumnNumberEELPICE[i]
				+ " cannot be resolved by in context validation to one schema definition, all candidates resulted in errors."
				+ " Possible definitions: ";
				for(int j = 0; j < ambiguousPossibleDefinitionsLPICE[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousPossibleDefinitionsLPICE[i][j].getQName()+"> at "+ambiguousPossibleDefinitionsLPICE[i][j].getLocation();
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


        if(!message.equals("")){            
            message = getErrorIntro(prefix) + message;
        }
		
		return message;
	} 
    
    private String getErrorIntro(String prefix){
        String intro = "";        
        if(contextType == ContextErrorHandler.ELEMENT){
            if(conflictResolutionId == RESOLVED){
                if(definition == null){
                    intro = "\n"+prefix+"Syntax error. Element <"+qName+"> contains errors: ";
                }else{
                    intro = "\n"+prefix+"Syntax error. Element <"+qName+"> corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation()+" contains errors: ";
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
                    intro = "\n"+prefix+"Syntax error. Error at the root of the document corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation()+": ";
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
    
    private String getWarningIntro(String prefix){        
        String intro = "";        
        if(contextType == ContextErrorHandler.ELEMENT){
            if(conflictResolutionId == RESOLVED){
                if(definition == null){
                    intro = "\n"+prefix+"Syntax warning. Element <"+qName+"> generates warning: ";
                }else{
                    intro = "\n"+prefix+"Syntax warning. Element <"+qName+"> corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation()+" generates warning: ";
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
                    intro = "\n"+prefix+"Syntax warning. Warning at the root of the document corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation()+": ";
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
    
	private String getExpectedCardinality(int expectedMin, int expectedMax){
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
    
    
    private String getValidationWarningMessage(String prefix){
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
        
        // {28_2}
        if(ambiguousCharsSystemIdEELPICW != null){
			for(int i = 0; i <= ambiguousIndexLPICW; i++){
				message += "\n"+prefix+"Ambiguous list token."
				+"\n"+prefix+ "List token \""+ambiguousTokenLPICW[i]+"\" at "+ambiguousCharsSystemIdEELPICW[i]+":"+ambiguousCharsLineNumberEELPICW[i]+":"+ambiguousCharsColumnNumberEELPICW[i]
				+ " cannot be resolved by in context validation to one schema definition."
				+ " Possible definitions: ";
				for(int j = 0; j < ambiguousPossibleDefinitionsLPICW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousPossibleDefinitionsLPICW[i][j].getQName()+"> at "+ambiguousPossibleDefinitionsLPICW[i][j].getLocation();
				}
				message += ".";
			}
		}
         
        if(!message.equals("")){            
            message = getWarningIntro(prefix) + message;
        }
        
		return message;
	}	
    
    public String toString(){
        return "AbstractMessageHandler "+getErrorMessage("");
    }
}
