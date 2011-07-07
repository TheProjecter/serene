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

import serene.Reusable;

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
public class AttributeConflictErrorHandler implements Reusable, ErrorCatcher{
	ValidatorErrorHandlerPool pool;
	
	ExternalConflictHandler conflictHandler;
	int candidateIndex;
	
	MessageWriter debugWriter;	
	public AttributeConflictErrorHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
	}
	
	public void recycle(){
		pool.recycle(this);
	}	
	
	void init(ValidatorErrorHandlerPool pool){	
		this.pool = pool;
	}
	
	void init(ExternalConflictHandler conflictHandler, int candidateIndex){		
		this.candidateIndex = candidateIndex;
		this.conflictHandler = conflictHandler;
	}	
	
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
			
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
		throw new IllegalStateException();
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
		throw new IllegalStateException();
	}
	
		
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		conflictHandler.disqualify(candidateIndex);
	}
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		conflictHandler.disqualify(candidateIndex);
		//System.out.println(startLineNumber+":"+startColumnNumber);
		//throw new UnsupportedOperationException();
	}

	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
	}
	
	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		conflictHandler.disqualify(candidateIndex);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		conflictHandler.disqualify(candidateIndex);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		conflictHandler.disqualify(candidateIndex);
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		conflictHandler.disqualify(candidateIndex);
	}
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		conflictHandler.disqualify(candidateIndex);
	}	

	
	public String toString(){
		//return "AttributeConflictErrorHandler "+hashCode()+" candidate "+candidateIndex+" in conflict "+conflictHandler.toString();
		return "AttributeConflictErrorHandler candidate "+candidateIndex+" in conflict "+conflictHandler.toString();
	}
}