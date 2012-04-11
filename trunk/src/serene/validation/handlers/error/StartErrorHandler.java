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

import sereneWrite.MessageWriter;

/**
* Handles errors that occur in the context of well determined elements. The error
* data is stored in a ContextMessageHandler and is reported to the ErrorDispatcher 
* at the end of the processing of the corresponding element. 
*/
public class StartErrorHandler extends AbstractContextErrorHandler{
    StartMessageHandler messageHandler;
    
	public StartErrorHandler(MessageWriter debugWriter){
		super(debugWriter);
		id = ContextErrorHandlerManager.VALIDATION;
        messageHandler = new StartMessageHandler(debugWriter);
	}
	
	public void recycle(){
        messageHandler.clear();
		pool.recycle(this);        
	}
    
    public boolean isCandidate(){
        return false;
    }
    public void setCandidate(boolean isCandidate){}
    
    
	public void unknownElement(int inputRecordIndex){
		messageHandler.unknownElement(activeInputDescriptor.getItemDescription(inputRecordIndex));		
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
		messageHandler.unexpectedElement(activeInputDescriptor.getItemDescription(inputRecordIndex), definition);	
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] definition, int inputRecordIndex){
		messageHandler.unexpectedAmbiguousElement(activeInputDescriptor.getItemDescription(inputRecordIndex), definition);	
	}

	
	public void unknownAttribute(int inputRecordIndex){
        throw new IllegalStateException();
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
        throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
        throw new IllegalStateException();
	}
	
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper){
        throw new IllegalStateException();
	}
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int inputRecordIndex, APattern sourceDefinition, APattern reper){
        throw new IllegalStateException();
	}
	
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex){
        throw new IllegalStateException();
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex){
        throw new IllegalStateException();
	}
	
	public void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex){
	}
	
	public void illegalContent(Rule context, int startInputRecordIndex){
        throw new IllegalStateException();
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
		messageHandler.unresolvedAmbiguousElementContentError(activeInputDescriptor.getItemDescription(inputRecordIndex), activeInputDescriptor.getSystemId(inputRecordIndex), activeInputDescriptor.getLineNumber(inputRecordIndex), activeInputDescriptor.getColumnNumber(inputRecordIndex), possibleDefinitions);
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
		messageHandler.unresolvedUnresolvedElementContentError(activeInputDescriptor.getItemDescription(inputRecordIndex), activeInputDescriptor.getSystemId(inputRecordIndex), activeInputDescriptor.getLineNumber(inputRecordIndex), activeInputDescriptor.getColumnNumber(inputRecordIndex), possibleDefinitions);
	}
	
	public void unresolvedAttributeContentError(int inputRecordIndex, AAttribute[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
		messageHandler.ambiguousUnresolvedElementContentWarning(activeInputDescriptor.getItemDescription(inputRecordIndex), activeInputDescriptor.getSystemId(inputRecordIndex), activeInputDescriptor.getLineNumber(inputRecordIndex), activeInputDescriptor.getColumnNumber(inputRecordIndex), possibleDefinitions);
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
		messageHandler.ambiguousAmbiguousElementContentWarning(activeInputDescriptor.getItemDescription(inputRecordIndex), activeInputDescriptor.getSystemId(inputRecordIndex), activeInputDescriptor.getLineNumber(inputRecordIndex), activeInputDescriptor.getColumnNumber(inputRecordIndex), possibleDefinitions);
	}
	
	public void ambiguousAttributeContentWarning(int inputRecordIndex, AAttribute[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        throw new IllegalStateException();
	}
	
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
        throw new IllegalStateException();
	}
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
        throw new IllegalStateException();
	}
	
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
        throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
        throw new IllegalStateException();
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
        throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
        throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        throw new IllegalStateException();
	}
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
        throw new IllegalStateException();
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
        throw new IllegalStateException();
	}
	    
    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
    }
	
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
        throw new IllegalStateException();
	}
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter [] candidateMessages){
        throw new IllegalStateException();
    }
    
    public void internalConflict(ConflictMessageReporter conflictMessageReporter) throws SAXException{
	    conflictMessageReporter.report();
	    conflictMessageReporter.clear();
    }
    
	public void handle(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator)
				throws SAXException{
        messageHandler.report(contextType, qName, definition, restrictToFileName, locator, errorDispatcher/*, ""*/);
		messageHandler.clear();
	}
	
	public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator)
				throws SAXException{
        messageHandler.report(contextType, qName, null, restrictToFileName, locator, errorDispatcher/*, ""*/);
		messageHandler.clear();
	}
	
	public void discard(){
	    throw new IllegalStateException();
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
		return "StartErrorHandler ";
	}
}
