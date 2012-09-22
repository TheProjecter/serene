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
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

/**
* Handles errors that occur in the context of well determined elements. The error
* data is stored in a ContextMessageHandler and is reported to the ErrorDispatcher 
* at the end of the processing of the corresponding element. 
*/
public class ValidationErrorHandler extends AbstractContextErrorHandler{
    ContextMessageHandler messageHandler;
    boolean isHandled;
	public ValidationErrorHandler(){
		super();
		id = ContextErrorHandlerManager.VALIDATION;
        messageHandler = new ContextMessageHandler();        
	}
	
	void init(ValidatorErrorHandlerPool pool, ErrorDispatcher errorDispatcher, ActiveInputDescriptor activeInputDescriptor){
		this.errorDispatcher = errorDispatcher;//maybe it could be moved to DefaultErrorHandler only
		this.pool = pool;
		this.activeInputDescriptor = activeInputDescriptor;	
		messageHandler.init(activeInputDescriptor);
	}
	public void recycle(){
        if(!isHandled){
            messageHandler.clear(this);            
        }
		pool.recycle(this);        
		isHandled = false;
	}
	
    public boolean isCandidate(){
        return false;
    }
    public void setCandidate(boolean isCandidate){}
    
    
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
		messageHandler.unexpectedAttribute( definition, inputRecordIndex);	
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
		messageHandler.unexpectedAmbiguousAttribute( possibleDefinition, inputRecordIndex);	
	}
	
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
		messageHandler.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
		messageHandler.misplacedContent(contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
	}
	
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
		messageHandler.excessiveContent(context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
		messageHandler.excessiveContent(context, excessiveDefinition, inputRecordIndex);
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){
		messageHandler.missingContent(context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
	}
	
	public void illegalContent(SRule context, int startInputRecordIndex){
		messageHandler.illegalContent(context, startInputRecordIndex);
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		messageHandler.unresolvedAmbiguousElementContentError(inputRecordIndex, possibleDefinitions);
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
		messageHandler.unresolvedUnresolvedElementContentError(inputRecordIndex, possibleDefinitions);
	}
	
	public void unresolvedAttributeContentError(int inputRecordIndex, SAttribute[] possibleDefinitions){
		messageHandler.unresolvedAttributeContentError(inputRecordIndex, possibleDefinitions);
	}
	

	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		messageHandler.ambiguousUnresolvedElementContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
		messageHandler.ambiguousAmbiguousElementContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeContentWarning(int inputRecordIndex, SAttribute[] possibleDefinitions){
		messageHandler.ambiguousAttributeContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		messageHandler.ambiguousCharacterContentWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
		messageHandler.ambiguousAttributeValueWarning(inputRecordIndex, possibleDefinitions);
	}
	
	public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		messageHandler.characterContentDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		messageHandler.attributeValueDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
		messageHandler.characterContentValueError(inputRecordIndex, charsDefinition);
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
		messageHandler.attributeValueValueError(inputRecordIndex, charsDefinition);
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
		messageHandler.characterContentExceptedError(inputRecordIndex, charsDefinition);
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
		messageHandler.attributeValueExceptedError(inputRecordIndex, charsDefinition);
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
		messageHandler.unexpectedCharacterContent(inputRecordIndex, elementDefinition);
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
		messageHandler.unexpectedAttributeValue(inputRecordIndex, attributeDefinition);
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
		messageHandler.unresolvedCharacterContent(inputRecordIndex, possibleDefinitions);
	}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
		messageHandler.unresolvedAttributeValue(inputRecordIndex, possibleDefinitions);
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
		messageHandler.listTokenDatatypeError(inputRecordIndex, charsDefinition, datatypeErrorMessage);
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
		messageHandler.listTokenValueError(inputRecordIndex, charsDefinition);
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
		messageHandler.listTokenExceptedError(inputRecordIndex, charsDefinition);
	}
    
    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
        messageHandler.unresolvedListTokenInContextError(inputRecordIndex, possibleDefinitions);
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
        messageHandler.ambiguousListTokenInContextWarning(inputRecordIndex, possibleDefinitions);
    }
	
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
		messageHandler.missingCompositorContent(context, startInputRecordIndex, definition, expected, found);
	}
    
	public void internalConflict(ConflictMessageReporter conflictMessageReporter) throws SAXException{
        conflictMessageReporter.report();
        conflictMessageReporter.clear(this);
    }
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter [] candidateMessages){        
		messageHandler.conflict(conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
    }
    
	public void handle(int contextType, String qName, SElement definition, boolean restrictToFileName, Locator locator)
				throws SAXException{
        messageHandler.report(contextType, qName, definition, restrictToFileName, locator, errorDispatcher);
		messageHandler.clear(this);
		isHandled = true;
	}
	
	public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator)
				throws SAXException{
        messageHandler.report(contextType, qName, null, restrictToFileName, locator, errorDispatcher);
		messageHandler.clear(this);
		isHandled = true;
	}

	public void discard(){
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
		//return "ValidationErrorHandler "+hashCode();
		return "ValidationErrorHandler ";
	}
}