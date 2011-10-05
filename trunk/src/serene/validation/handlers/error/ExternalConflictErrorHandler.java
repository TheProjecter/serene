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
    ConflictMessageHandler messageHandler;
    //ConflictErrorCatcher messageHandler; only for debuging
    
	int candidateIndex;    
    CandidatesConflictErrorHandler candidatesConflictErrorHandler;
    boolean isCandidate;

	public ExternalConflictErrorHandler(MessageWriter debugWriter){
		super(debugWriter);
		id = ContextErrorHandlerManager.CONFLICT;		
	}
	
	public void recycle(){
        candidateIndex = -1;
        candidatesConflictErrorHandler = null;
        isCandidate = false;        
		messageHandler = null;
        
		pool.recycle(this);
	}	
	
	public void init(CandidatesConflictErrorHandler candidatesConflictErrorHandler, int candidateIndex, boolean isCandidate){		
		this.candidateIndex = candidateIndex;
        this.candidatesConflictErrorHandler = candidatesConflictErrorHandler;
        this.isCandidate = isCandidate;
        
		messageHandler = new ConflictMessageHandler(debugWriter);
        if(isCandidate)candidatesConflictErrorHandler.addCandidateMessageHandler(candidateIndex, messageHandler);
	}	
	public boolean isCandidate(){
        return isCandidate;
    }
    public void setCandidate(boolean isCandidate){
        this.isCandidate = isCandidate;
    }
    
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
	    int functionalEquivalenceCode = qName.hashCode();
        if(isCandidate){            
            messageHandler.unknownElement(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unknownElement(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unknownElement(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
        }
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
	    int functionalEquivalenceCode = qName.hashCode()+definition.hashCode();
        if(isCandidate){            
            messageHandler.unexpectedElement(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unexpectedElement(candidateIndex,  functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unexpectedElement(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
	    int functionalEquivalenceCode = qName.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].hashCode();
        }
        if(isCandidate){            
            messageHandler.unexpectedAmbiguousElement(functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unexpectedAmbiguousElement(candidateIndex, functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unexpectedAmbiguousElement(functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
        }
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
	    int functionalEquivalenceCode = qName.hashCode();
        if(isCandidate){
            messageHandler.unknownAttribute(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unknownAttribute(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unknownAttribute(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
        }
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
	    int functionalEquivalenceCode = qName.hashCode()+definition.hashCode();	    
        if(isCandidate){            
            messageHandler.unexpectedAttribute(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unexpectedAttribute(candidateIndex, functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unexpectedAttribute(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
	    int functionalEquivalenceCode = qName.hashCode();
        for(int i = 0; i < definition.length; i++){
            functionalEquivalenceCode += definition[i].hashCode();
        }
        if(isCandidate){            
            messageHandler.unexpectedAmbiguousAttribute(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unexpectedAmbiguousAttribute( candidateIndex, functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unexpectedAmbiguousAttribute(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }
	}
	
	
	public void misplacedContent(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int[] itemId, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
	    int functionalEquivalenceCode = contextDefinition.functionalEquivalenceCode()+
                                                definition.functionalEquivalenceCode();
        for(int i = 0; i < qName.length; i++){
            functionalEquivalenceCode += qName[i].hashCode()+
                                            sourceDefinition[i].functionalEquivalenceCode();
        }
        if(isCandidate){
            messageHandler.misplacedContent(functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, itemId, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
            candidatesConflictErrorHandler.misplacedContent(candidateIndex, functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, itemId, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        }else{
            messageHandler.misplacedContent(functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, itemId, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        }
	}
	
	public void misplacedContent(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int itemId, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
	    int functionalEquivalenceCode = contextDefinition.functionalEquivalenceCode()+
											definition.functionalEquivalenceCode()+ 
											qName.hashCode()+
											sourceDefinition.functionalEquivalenceCode();
        if(isCandidate){
            messageHandler.misplacedContent(functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, itemId, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
            candidatesConflictErrorHandler.misplacedContent(candidateIndex, functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, itemId, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        }else{
            messageHandler.misplacedContent(functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, itemId, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        }
	}
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, int[] itemId, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
	    int functionalEquivalenceCode = /*context.functionalEquivalenceCode()+*/
                                            excessiveDefinition.functionalEquivalenceCode();
        for(int i = 0; i < qName.length; i++){
            functionalEquivalenceCode += qName[i].hashCode();
        }
        if(isCandidate){            
            messageHandler.excessiveContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.excessiveContent(candidateIndex, functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.excessiveContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
        }
	}
	public void excessiveContent(Rule context, APattern excessiveDefinition, int itemId, String qName, String systemId, int lineNumber, int columnNumber){        
		messageHandler.excessiveContent(-1, context, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
        if(isCandidate)candidatesConflictErrorHandler.excessiveContent(candidateIndex, -1, context, excessiveDefinition, itemId, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
	    int functionalEquivalenceCode = /*context.functionalEquivalenceCode()+*/
                                            missingDefinition.functionalEquivalenceCode()+
                                            expected+ 
                                            found;
        if(qName != null){
            for(int i = 0; i < qName.length; i++){
                functionalEquivalenceCode += qName[i].hashCode();
            }
        }
        if(isCandidate){
            messageHandler.missingContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.missingContent(candidateIndex, functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.missingContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
        }
	}

	public void illegalContent(Rule context, int startItemId, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
	    int functionalEquivalenceCode = context.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.illegalContent(functionalEquivalenceCode, context, startItemId, startQName, startSystemId, startLineNumber, startColumnNumber);
            candidatesConflictErrorHandler.illegalContent(candidateIndex, functionalEquivalenceCode, context, startItemId, startQName, startSystemId, startLineNumber, startColumnNumber);
        }else{
            messageHandler.illegalContent(functionalEquivalenceCode, context, startItemId, startQName, startSystemId, startLineNumber, startColumnNumber);
        }
	}
	
	public void unresolvedAmbiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
	    int functionalEquivalenceCode = qName.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){
            messageHandler.unresolvedAmbiguousElementContentError(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedAmbiguousElementContentError(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.unresolvedAmbiguousElementContentError(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void unresolvedUnresolvedElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
	    int functionalEquivalenceCode = qName.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedUnresolvedElementContentError(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedUnresolvedElementContentError(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.unresolvedUnresolvedElementContentError(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void unresolvedAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
	    int functionalEquivalenceCode = qName.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedAttributeContentError(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedAttributeContentError(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.unresolvedAttributeContentError(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	
	public void ambiguousUnresolvedElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
	    int functionalEquivalenceCode = qName.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousUnresolvedElementContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousUnresolvedElementContentWarning(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousUnresolvedElementContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void ambiguousAmbiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
	    int functionalEquivalenceCode = qName.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousAmbiguousElementContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousAmbiguousElementContentWarning(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousAmbiguousElementContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
	    int functionalEquivalenceCode = qName.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousAttributeContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousAttributeContentWarning(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousAttributeContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void ambiguousCharacterContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = 0;
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousCharacterContentWarning(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousCharacterContentWarning(candidateIndex, functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousCharacterContentWarning(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void ambiguousAttributeValueWarning(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = 0;
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousAttributeValueWarning(functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousAttributeValueWarning(candidateIndex, functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousAttributeValueWarning(functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
	    int functionalEquivalenceCode = qName.hashCode()+
                                            candidateMessages.hashCode();
        if(isCandidate){            
            messageHandler.undeterminedByContent(functionalEquivalenceCode, qName, candidateMessages);
            candidatesConflictErrorHandler.undeterminedByContent(candidateIndex, functionalEquivalenceCode, qName, candidateMessages);
        }else{
            messageHandler.undeterminedByContent(functionalEquivalenceCode, qName, candidateMessages);
        }
	}	
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	    int functionalEquivalenceCode = elementQName.hashCode()+
                                               charsDefinition.functionalEquivalenceCode()+
                                               datatypeErrorMessage.hashCode();
        if(isCandidate){
            messageHandler.characterContentDatatypeError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.characterContentDatatypeError(candidateIndex, functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.characterContentDatatypeError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	    int functionalEquivalenceCode = attributeQName.hashCode()+
                                           charsDefinition.functionalEquivalenceCode()+
                                           datatypeErrorMessage.hashCode();
        if(isCandidate){            
            messageHandler.attributeValueDatatypeError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.attributeValueDatatypeError(candidateIndex, functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.attributeValueDatatypeError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }
	}
	
	public void characterContentValueError(String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.characterContentValueError(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.characterContentValueError(candidateIndex, functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.characterContentValueError(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
	    int functionalEquivalenceCode = attributeQName.hashCode()+
                                           charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.attributeValueValueError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.attributeValueValueError(candidateIndex, functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.attributeValueValueError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
	    int functionalEquivalenceCode = elementQName.hashCode()+
                                           charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.characterContentExceptedError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.characterContentExceptedError(candidateIndex, functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.characterContentExceptedError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
	    int functionalEquivalenceCode = attributeQName.hashCode()+
                                           charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.attributeValueExceptedError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.attributeValueExceptedError(candidateIndex, functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.attributeValueExceptedError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
	    //int functionalEquivalenceCode = elementDefinition.functionalEquivalenceCode();
        int functionalEquivalenceCode = charsSystemId.hashCode()+
                                            charsLineNumber+
                                            columnNumber;
        if(isCandidate){            
            messageHandler.unexpectedCharacterContent(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, elementDefinition);
            candidatesConflictErrorHandler.unexpectedCharacterContent(candidateIndex, functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, elementDefinition);
        }else{
            messageHandler.unexpectedCharacterContent(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, elementDefinition);
        }
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
	    //int functionalEquivalenceCode = attributeDefinition.functionalEquivalenceCode();
        int functionalEquivalenceCode = charsSystemId.hashCode()+
                                            charsLineNumber+
                                            columnNumber;
        if(isCandidate){            
            messageHandler.unexpectedAttributeValue(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
            candidatesConflictErrorHandler.unexpectedAttributeValue(candidateIndex, functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
        }else{
            messageHandler.unexpectedAttributeValue(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
        }
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = 0;
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedCharacterContent(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedCharacterContent(candidateIndex, functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.unresolvedCharacterContent(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = attributeQName.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedAttributeValue(functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedAttributeValue(candidateIndex, functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.unresolvedAttributeValue(functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	    int functionalEquivalenceCode = token.hashCode()+
                                        charsDefinition.functionalEquivalenceCode()+
                                        datatypeErrorMessage.hashCode();
        if(isCandidate){            
            messageHandler.listTokenDatatypeError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.listTokenDatatypeError(candidateIndex, functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.listTokenDatatypeError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
	    int functionalEquivalenceCode = token.hashCode()+
                                        charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.listTokenValueError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.listTokenValueError(candidateIndex, functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.listTokenValueError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
	    int functionalEquivalenceCode = token.hashCode()+
                                        charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.listTokenExceptedError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.listTokenExceptedError(candidateIndex, functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.listTokenExceptedError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}

    public void unresolvedListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        int functionalEquivalenceCode = token.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedListTokenInContextError(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedListTokenInContextError(candidateIndex, functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.unresolvedListTokenInContextError(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = token.hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousListTokenInContextWarning(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousListTokenInContextWarning(candidateIndex, functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousListTokenInContextWarning(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
    }
    
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
	    int functionalEquivalenceCode = context.functionalEquivalenceCode()+
                                        definition.functionalEquivalenceCode()+
                                        expected+
                                        found;
        if(isCandidate){
            messageHandler.missingCompositorContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
            candidatesConflictErrorHandler.missingCompositorContent(candidateIndex, functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
        }else{
            messageHandler.missingCompositorContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
        }
	}
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter [] candidateMessages){
        int functionalEquivalenceCode = disqualified.hashCode();                                        
        if(commonMessages != null) functionalEquivalenceCode += commonMessages.hashCode();
        if(candidateMessages != null){
            for(int i = 0; i < candidateMessages.length; i++){
                if(candidateMessages[i] != null)functionalEquivalenceCode += candidateMessages[i].hashCode();
            }
        }
        if(isCandidate){            
            messageHandler.conflict(functionalEquivalenceCode, conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
            candidatesConflictErrorHandler.conflict(candidateIndex, functionalEquivalenceCode, conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
        }else{
            messageHandler.conflict(functionalEquivalenceCode, conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
        }
    }
    
    public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    candidatesConflictErrorHandler.delayMessageReporter(conflictMessageReporter, candidateIndex);
    }
    
	public void handle(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator)
					throws SAXException{
		candidatesConflictErrorHandler.delayMessageReporter(contextType, qName, definition, locator, messageHandler, candidateIndex);
	}
	
	public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator)
					throws SAXException{
		candidatesConflictErrorHandler.delayMessageReporter(contextType, qName, locator, messageHandler, candidateIndex);
	}
	
	public void record(int contextType, String qName, boolean restrictToFileName, Locator locator){
	    messageHandler.setContextQName(qName);
        messageHandler.setContextLocation(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        messageHandler.setContextType(contextType);
        messageHandler.setRestrictToFileName(restrictToFileName);
	}
		
	
    public int getConflictResolutionId(){
        return messageHandler.getConflictResolutionId();
    }	
        
    public ConflictMessageReporter getConflictMessageReporter(){
        return messageHandler.getConflictMessageReporter(errorDispatcher);
    } 
	
	public String toString(){
		//return "ExternalConflictErrorHandler "+hashCode()+" candidate "+candidateIndex+" in conflict "+conflictHandler.toString();
		return "ExternalConflictErrorHandler candidate "+candidateIndex;
	}
}