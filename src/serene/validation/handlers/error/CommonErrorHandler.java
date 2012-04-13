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
import java.util.BitSet;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

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

import serene.validation.handlers.conflict.ExternalConflictHandler;

import sereneWrite.MessageWriter;

/**	
* Handles errors that occur in an undetermined context which resolves for all the
* individual parent handlers to functionally equivalent handlers. It is used by 
* the ElementConcurrentHandler and the ElementParallelHandler when they are the 
* parents of an ElementParallelHandler in state Common when uniqueSample is instance
* of ErrorEEH. Errors are reported and are not disqualifying.
*/
public class CommonErrorHandler extends AbstractContextErrorHandler{
    boolean isHandled;
    ContextMessageHandler messageHandler;	
    CandidatesConflictErrorHandler candidatesConflictErrorHandler;
    boolean isCandidate;
    
	public CommonErrorHandler(MessageWriter debugWriter){
		super(debugWriter);
		id = ContextErrorHandlerManager.COMMON;
	}
	
	public void recycle(){
        isCandidate = false;
        if(!isHandled && messageHandler != null){
            /*messageHandler.setDiscarded(true);*/
            messageHandler.clear(this);
        }        
        messageHandler = null;
        candidatesConflictErrorHandler = null;
		pool.recycle(this);
		isHandled = false;
	}
	public void init(CandidatesConflictErrorHandler candidatesConflictErrorHandler, boolean isCandidate){        
        this.candidatesConflictErrorHandler = candidatesConflictErrorHandler;
        this.isCandidate = isCandidate;
		messageHandler = new ContextMessageHandler(debugWriter);   
		messageHandler.init(activeInputDescriptor);
	}

    public boolean isCandidate(){
        return isCandidate;
    }
    public void setCandidate(boolean isCandidate){
        this.isCandidate = isCandidate;
    }
	
	public void unknownElement(int inputRecordIndex){
		messageHandler.unknownElement(inputRecordIndex);
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
		messageHandler.unexpectedElement( definition, inputRecordIndex);	
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] definition, int inputRecordIndex){
		messageHandler.unexpectedAmbiguousElement( definition, inputRecordIndex);	
	}
	
	
	public void unknownAttribute(int inputRecordIndex){
		messageHandler.unknownAttribute( inputRecordIndex);		
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
		messageHandler.unexpectedAttribute(definition, inputRecordIndex);
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
		messageHandler.unexpectedAmbiguousAttribute( possibleDefinition, inputRecordIndex);	
	}
	
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int inputRecordIndex, APattern sourceDefinition, APattern reper){
		messageHandler.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper){
		messageHandler.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
	
	
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex){
		messageHandler.excessiveContent(context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex){
		messageHandler.excessiveContent(context, excessiveDefinition, inputRecordIndex);
	}
	
	public void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex){
		messageHandler.missingContent(context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
	}
	
	public void illegalContent(Rule context, int startInputRecordIndex){
		messageHandler.illegalContent(context, startInputRecordIndex);
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
		messageHandler.unresolvedAmbiguousElementContentError(inputRecordIndex, possibleDefinitions);
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
		messageHandler.unresolvedUnresolvedElementContentError(inputRecordIndex, possibleDefinitions);
	}
	
	public void unresolvedAttributeContentError(int inputRecordIndex, AAttribute[] possibleDefinitions){
		messageHandler.unresolvedAttributeContentError(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
		messageHandler.ambiguousUnresolvedElementContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
		messageHandler.ambiguousAmbiguousElementContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeContentWarning(int inputRecordIndex, AAttribute[] possibleDefinitions){
		messageHandler.ambiguousAttributeContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		messageHandler.characterContentDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		messageHandler.attributeValueDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
		messageHandler.characterContentValueError(inputRecordIndex, charsDefinition);
	}
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
		messageHandler.attributeValueValueError(inputRecordIndex, charsDefinition);
	}
	
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
		messageHandler.characterContentExceptedError(inputRecordIndex, charsDefinition);
	}	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
		messageHandler.attributeValueExceptedError(inputRecordIndex, charsDefinition);
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
		messageHandler.unexpectedCharacterContent(inputRecordIndex, elementDefinition);
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
		messageHandler.unexpectedAttributeValue(inputRecordIndex, attributeDefinition);
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.unresolvedCharacterContent(inputRecordIndex, possibleDefinitions);
	}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.unresolvedAttributeValue(inputRecordIndex, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		messageHandler.listTokenDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
		messageHandler.listTokenValueError(inputRecordIndex, charsDefinition);
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
		messageHandler.listTokenExceptedError(inputRecordIndex, charsDefinition);
	}
	
    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.unresolvedListTokenInContextError(inputRecordIndex, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.ambiguousListTokenInContextWarning(inputRecordIndex, possibleDefinitions);
    }
    
    
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
		messageHandler.missingCompositorContent(context, startInputRecordIndex, definition, expected, found);
	}
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter [] candidateMessages){
		messageHandler.conflict(conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
    }
	
    public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    candidatesConflictErrorHandler.delayMessageReporter(conflictMessageReporter, isCandidate); // isCandidate???
    }
    
	public void handle(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator)
				throws SAXException{
		isHandled = true;
		candidatesConflictErrorHandler.delayMessageReporter(contextType, qName, definition, locator, messageHandler, isCandidate);
	}
	
	public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator) 
					throws SAXException{
		isHandled = true;
		candidatesConflictErrorHandler.delayMessageReporter(contextType, qName, locator, messageHandler, isCandidate);
	}
    
	public void discard(){
	    /*messageHandler.setDiscarded(true);*/
	    messageHandler.clear(this);
	}
	
    public void record(int contextType, String qName, boolean restrictToFileName, Locator locator){
	    messageHandler.setReportingContextQName(qName);
        messageHandler.setReportingContextLocation(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        messageHandler.setReportingContextType(contextType);
        messageHandler.setRestrictToFileName(restrictToFileName);
	}
		
    
    public int getConflictResolutionId(){
        return messageHandler.getConflictResolutionId();
    }
	    
    public ConflictMessageReporter getConflictMessageReporter(){
        return messageHandler.getConflictMessageReporter(errorDispatcher);
    } 
    
	public String toString(){
		//return "CommonErrorHandler "+hashCode() ;
		return "CommonErrorHandler ";
	}
}