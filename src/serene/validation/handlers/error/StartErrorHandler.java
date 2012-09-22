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
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

/**
* Handles errors that occur in the context of well determined elements. The error
* data is stored in a ContextMessageHandler and is reported to the ErrorDispatcher 
* at the end of the processing of the corresponding element. 
*/
public class StartErrorHandler extends AbstractContextErrorHandler{
    StartMessageHandler messageHandler;
    
	public StartErrorHandler(){
		super();
		id = ContextErrorHandlerManager.VALIDATION;
        messageHandler = new StartMessageHandler();
	}
	
	public void recycle(){
        messageHandler.clear(this);
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
	
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
        throw new IllegalStateException();
	}
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
        throw new IllegalStateException();
	}
	
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
        throw new IllegalStateException();
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
        throw new IllegalStateException();
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
	}
	
	public void illegalContent(SRule context, int startInputRecordIndex){
        throw new IllegalStateException();
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		messageHandler.unresolvedAmbiguousElementContentError(activeInputDescriptor.getItemDescription(inputRecordIndex), activeInputDescriptor.getSystemId(inputRecordIndex), activeInputDescriptor.getLineNumber(inputRecordIndex), activeInputDescriptor.getColumnNumber(inputRecordIndex), possibleDefinitions);
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		messageHandler.unresolvedUnresolvedElementContentError(activeInputDescriptor.getItemDescription(inputRecordIndex), activeInputDescriptor.getSystemId(inputRecordIndex), activeInputDescriptor.getLineNumber(inputRecordIndex), activeInputDescriptor.getColumnNumber(inputRecordIndex), possibleDefinitions);
	}
	
	public void unresolvedAttributeContentError(int inputRecordIndex, SAttribute[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		messageHandler.ambiguousUnresolvedElementContentWarning(activeInputDescriptor.getItemDescription(inputRecordIndex), activeInputDescriptor.getSystemId(inputRecordIndex), activeInputDescriptor.getLineNumber(inputRecordIndex), activeInputDescriptor.getColumnNumber(inputRecordIndex), possibleDefinitions);
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		messageHandler.ambiguousAmbiguousElementContentWarning(activeInputDescriptor.getItemDescription(inputRecordIndex), activeInputDescriptor.getSystemId(inputRecordIndex), activeInputDescriptor.getLineNumber(inputRecordIndex), activeInputDescriptor.getColumnNumber(inputRecordIndex), possibleDefinitions);
	}
	
	public void ambiguousAttributeContentWarning(int inputRecordIndex, SAttribute[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        throw new IllegalStateException();
	}
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
        throw new IllegalStateException();
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
        throw new IllegalStateException();
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
        throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
        throw new IllegalStateException();
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
        throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
        throw new IllegalStateException();
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
        throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
        throw new IllegalStateException();
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
        throw new IllegalStateException();
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
        throw new IllegalStateException();
	}
	    
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
        throw new IllegalStateException();
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        throw new IllegalStateException();
    }
	
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
        throw new IllegalStateException();
	}
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter [] candidateMessages){
        throw new IllegalStateException();
    }
    
    public void internalConflict(ConflictMessageReporter conflictMessageReporter) throws SAXException{
	    conflictMessageReporter.report();
	    conflictMessageReporter.clear(this);
    }
    
	public void handle(int contextType, String qName, SElement definition, boolean restrictToFileName, Locator locator)
				throws SAXException{
        messageHandler.report(contextType, qName, definition, restrictToFileName, locator, errorDispatcher);
		messageHandler.clear(this);
	}
	
	public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator)
				throws SAXException{
        messageHandler.report(contextType, qName, null, restrictToFileName, locator, errorDispatcher);
		messageHandler.clear(this);
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
