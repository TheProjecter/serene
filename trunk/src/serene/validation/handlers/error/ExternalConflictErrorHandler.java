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
* Handles errors that occur in an undetermined context processed parallely by a 
* different handler for each possible definition. It is used by ElementConcurrentHandler 
* and the ElementParallelHandler for every branch that has not been disqualified. 
* In case of error the corresponding branch is disqualified and no validation should 
* be done any more. Recognition of the input continues in order to be able to 
* identify occurrences that resolve for all the individual parent handlers to 
* functionally equivalent handlers and identify this way the Common states. 
* MessageHandlers generated during validation are stored by the ExternalConflictHandler 
* in order to be processed by the ElementConcurrentHandler. 
*/
public class ExternalConflictErrorHandler extends AbstractContextErrorHandler{	
	ExternalConflictHandler conflictHandler;
	int candidateIndex;
		
	public ExternalConflictErrorHandler(MessageWriter debugWriter){
		super(debugWriter);
		id = ContextErrorHandlerManager.CONFLICT;
	}
	
	public void recycle(){
		pool.recycle(this);
	}	
	
	void init(ExternalConflictHandler conflictHandler, int candidateIndex){		
		this.candidateIndex = candidateIndex;
		this.conflictHandler = conflictHandler;
		messageHandler = new ContextMessageHandler(debugWriter);
	}	
	
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
		messageHandler.unknownElement( qName, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		messageHandler.unexpectedElement( qName, definition, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		messageHandler.unexpectedAmbiguousElement( qName, definition, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		messageHandler.unknownAttribute( qName, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		messageHandler.unexpectedAttribute( qName, definition, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		messageHandler.unexpectedAmbiguousAttribute( qName, definition, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);
	}

	
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){		
		messageHandler.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		messageHandler.misplacedElement(contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		messageHandler.excessiveContent(context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);
	}
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		messageHandler.excessiveContent(context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		messageHandler.missingContent(context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
		conflictHandler.disqualify(candidateIndex);;
	}

	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		messageHandler.illegalContent(context, startQName, startSystemId, startLineNumber, startColumnNumber);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		messageHandler.ambiguousElementContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		messageHandler.ambiguousAttributeContentError(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.ambiguousCharsContentError(systemId, lineNumber, columnNumber, possibleDefinitions);
		conflictHandler.disqualify(candidateIndex);
	}
	
	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		messageHandler.ambiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
		//conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		messageHandler.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, possibleDefinitions);
		//conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, possibleDefinitions);
		//conflictHandler.disqualify(candidateIndex);
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		messageHandler.undeterminedByContent(qName, candidateMessages);
		conflictHandler.disqualify(candidateIndex);
	}	
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		messageHandler.characterContentDatatypeError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
		conflictHandler.disqualify(candidateIndex);
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		messageHandler.attributeValueDatatypeError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		messageHandler.characterContentValueError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
		conflictHandler.disqualify(candidateIndex);
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		messageHandler.attributeValueValueError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		messageHandler.characterContentExceptedError(elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
		conflictHandler.disqualify(candidateIndex);
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		messageHandler.attributeValueExceptedError(attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		messageHandler.unexpectedCharacterContent(charsSystemId, charsLineNumber, columnNumber, elementDefinition);
		conflictHandler.disqualify(candidateIndex);
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		messageHandler.unexpectedAttributeValue(charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.ambiguousCharacterContent(systemId, lineNumber, columnNumber, possibleDefinitions);
		conflictHandler.disqualify(candidateIndex);
	}
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.ambiguousAttributeValue(attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		messageHandler.listTokenDatatypeError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
		conflictHandler.disqualify(candidateIndex);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		messageHandler.listTokenValueError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
		conflictHandler.disqualify(candidateIndex);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		messageHandler.listTokenExceptedError(token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
		conflictHandler.disqualify(candidateIndex);
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		messageHandler.ambiguousListToken(token, systemId, lineNumber, columnNumber, possibleDefinitions);
		conflictHandler.disqualify(candidateIndex);
	}
    public void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        messageHandler.ambiguousListTokenInContextError(token, systemId, lineNumber, columnNumber, possibleDefinitions);
		conflictHandler.disqualify(candidateIndex);
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        messageHandler.ambiguousListTokenInContextWarning(token, systemId, lineNumber, columnNumber, possibleDefinitions);
		//conflictHandler.disqualify(candidateIndex);
    }
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		messageHandler.missingCompositorContent(context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
		conflictHandler.disqualify(candidateIndex);
	}
	public void handle(String qName, AElement definition, Locator locator)
					throws SAXException{		
		messageHandler.setContextQName(qName);
		messageHandler.setContextLocation(locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
		messageHandler.setContextDefinition(definition);		
		conflictHandler.addCandidateMessage(messageHandler, candidateIndex);
	}
	
	public void handle(String qName, Locator locator)
					throws SAXException{
		messageHandler.setContextQName(qName);
		messageHandler.setContextLocation(locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
		messageHandler.setContextDefinition(null);
		conflictHandler.addCandidateMessage(messageHandler, candidateIndex);
	}
	
	
	
	public String toString(){
		//return "ExternalConflictErrorHandler "+hashCode()+" candidate "+candidateIndex+" in conflict "+conflictHandler.toString();
		return "ExternalConflictErrorHandler candidate "+candidateIndex+" in conflict "+conflictHandler.toString();
	}
}