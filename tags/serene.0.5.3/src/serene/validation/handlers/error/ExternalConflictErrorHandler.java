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
        if(isCandidate)candidatesConflictErrorHandler.addCandidateMessageHandler(messageHandler);
	}	
	public boolean isCandidate(){
        return isCandidate;
    }
    public void setCandidate(boolean isCandidate){
        this.isCandidate = isCandidate;
    }
    
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode();
            messageHandler.unknownElement(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unknownElement(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unknownElement(-1, qName, systemId, lineNumber, columnNumber);
        }
	}	
	public void unexpectedElement(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode()+definition.hashCode();
            messageHandler.unexpectedElement(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unexpectedElement(candidateIndex,  functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unexpectedElement(-1, qName, definition, systemId, lineNumber, columnNumber);
        }
	}	
	public void unexpectedAmbiguousElement(String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].hashCode();
            }
            messageHandler.unexpectedAmbiguousElement(functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unexpectedAmbiguousElement(candidateIndex, functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unexpectedAmbiguousElement(-1, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
        }
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode();
            messageHandler.unknownAttribute(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unknownAttribute(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unknownAttribute(-1, qName, systemId, lineNumber, columnNumber);
        }
	}	
	public void unexpectedAttribute(String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode()+definition.hashCode();
            messageHandler.unexpectedAttribute(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unexpectedAttribute(candidateIndex, functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unexpectedAttribute(-1, qName, definition, systemId, lineNumber, columnNumber);
        }
	}	
	public void unexpectedAmbiguousAttribute(String qName, SimplifiedComponent[] definition, String systemId, int lineNumber, int columnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode();
            for(int i = 0; i < definition.length; i++){
                functionalEquivalenceCode += definition[i].hashCode();
            }
            messageHandler.unexpectedAmbiguousAttribute(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.unexpectedAmbiguousAttribute( candidateIndex, functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.unexpectedAmbiguousAttribute(-1, qName, definition, systemId, lineNumber, columnNumber);
        }
	}
	
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
        if(isCandidate){            
            int functionalEquivalenceCode = contextDefinition.functionalEquivalenceCode()+
                                                definition.functionalEquivalenceCode();
            for(int i = 0; i < qName.length; i++){
                functionalEquivalenceCode += qName[i].hashCode()+
                                                sourceDefinition[i].functionalEquivalenceCode();
            }
            messageHandler.misplacedElement(functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
            candidatesConflictErrorHandler.misplacedElement(candidateIndex, functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        }else{
            messageHandler.misplacedElement(-1, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        }
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName,  String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
        if(isCandidate){
            int functionalEquivalenceCode = contextDefinition.functionalEquivalenceCode()+
											definition.functionalEquivalenceCode()+ 
											qName.hashCode()+
											sourceDefinition.functionalEquivalenceCode();		
            messageHandler.misplacedElement(functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
            candidatesConflictErrorHandler.misplacedElement(candidateIndex, functionalEquivalenceCode, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        }else{
            messageHandler.misplacedElement(-1, contextDefinition, startSystemId, startLineNumber, startColumnNumber, definition, qName, systemId, lineNumber, columnNumber, sourceDefinition, reper);
        }
	}
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = /*context.functionalEquivalenceCode()+*/
                                            excessiveDefinition.functionalEquivalenceCode();
            for(int i = 0; i < qName.length; i++){
                functionalEquivalenceCode += qName[i].hashCode();
            }
            messageHandler.excessiveContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.excessiveContent(candidateIndex, functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.excessiveContent(-1, context, startSystemId, startLineNumber, startColumnNumber, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
        }
	}
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){        
		messageHandler.excessiveContent(-1, context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
        if(isCandidate)candidatesConflictErrorHandler.excessiveContent(candidateIndex, -1, context, excessiveDefinition, qName, systemId, lineNumber, columnNumber);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = /*context.functionalEquivalenceCode()+*/
                                            missingDefinition.functionalEquivalenceCode()+
                                            expected+ 
                                            found;
            if(qName != null){
                for(int i = 0; i < qName.length; i++){
                    functionalEquivalenceCode += qName[i].hashCode();
                }
            }        
            
            messageHandler.missingContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
            candidatesConflictErrorHandler.missingContent(candidateIndex, functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
        }else{
            messageHandler.missingContent(-1, context, startSystemId, startLineNumber, startColumnNumber, missingDefinition, expected, found, qName, systemId, lineNumber, columnNumber);
        }
	}

	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
        if(isCandidate){
            int functionalEquivalenceCode = context.functionalEquivalenceCode();
            messageHandler.illegalContent(functionalEquivalenceCode, context, startQName, startSystemId, startLineNumber, startColumnNumber);
            candidatesConflictErrorHandler.illegalContent(candidateIndex, functionalEquivalenceCode, context, startQName, startSystemId, startLineNumber, startColumnNumber);
        }else{
            messageHandler.illegalContent(-1, context, startQName, startSystemId, startLineNumber, startColumnNumber);
        }
	}
	
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            messageHandler.ambiguousElementContentError(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousElementContentError(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousElementContentError(-1, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            messageHandler.ambiguousAttributeContentError(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousAttributeContentError(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousAttributeContentError(-1, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = 0;
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            messageHandler.ambiguousCharsContentError(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousCharsContentError(candidateIndex, functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousCharsContentError(-1, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            candidatesConflictErrorHandler.ambiguousElementContentWarning(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousElementContentWarning(-1, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            candidatesConflictErrorHandler.ambiguousAttributeContentWarning(candidateIndex, functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousAttributeContentWarning(-1, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = 0;
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            candidatesConflictErrorHandler.ambiguousCharsContentWarning(candidateIndex, functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousCharsContentWarning(-1, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void undeterminedByContent(String qName, String candidateMessages){
        if(isCandidate){
            int functionalEquivalenceCode = qName.hashCode()+
                                            candidateMessages.hashCode();
            messageHandler.undeterminedByContent(functionalEquivalenceCode, qName, candidateMessages);
            candidatesConflictErrorHandler.undeterminedByContent(candidateIndex, functionalEquivalenceCode, qName, candidateMessages);
        }else{
            messageHandler.undeterminedByContent(-1, qName, candidateMessages);
        }
	}	
	
	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        if(isCandidate){            
            int functionalEquivalenceCode = elementQName.hashCode()+
                                               charsDefinition.functionalEquivalenceCode()+
                                               datatypeErrorMessage.hashCode();
            messageHandler.characterContentDatatypeError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.characterContentDatatypeError(candidateIndex, functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.characterContentDatatypeError(-1, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        if(isCandidate){
            int functionalEquivalenceCode = attributeQName.hashCode()+
                                           charsDefinition.functionalEquivalenceCode()+
                                           datatypeErrorMessage.hashCode();
            messageHandler.attributeValueDatatypeError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.attributeValueDatatypeError(candidateIndex, functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.attributeValueDatatypeError(-1, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        if(isCandidate){
            int functionalEquivalenceCode = elementQName.hashCode()+
                                               charsDefinition.functionalEquivalenceCode();
            messageHandler.characterContentValueError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.characterContentValueError(candidateIndex, functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.characterContentValueError(-1, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        if(isCandidate){
            int functionalEquivalenceCode = attributeQName.hashCode()+
                                           charsDefinition.functionalEquivalenceCode();
            messageHandler.attributeValueValueError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.attributeValueValueError(candidateIndex, functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.attributeValueValueError(-1, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        if(isCandidate){
            int functionalEquivalenceCode = elementQName.hashCode()+
                                           charsDefinition.functionalEquivalenceCode();
            messageHandler.characterContentExceptedError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.characterContentExceptedError(candidateIndex, functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.characterContentExceptedError(-1, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        if(isCandidate){
            int functionalEquivalenceCode = attributeQName.hashCode()+
                                           charsDefinition.functionalEquivalenceCode();
            messageHandler.attributeValueExceptedError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.attributeValueExceptedError(candidateIndex, functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.attributeValueExceptedError(-1, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
        if(isCandidate){
            //int functionalEquivalenceCode = elementDefinition.functionalEquivalenceCode();
            int functionalEquivalenceCode = charsSystemId.hashCode()+
                                            charsLineNumber+
                                            columnNumber;
            messageHandler.unexpectedCharacterContent(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, elementDefinition);
            candidatesConflictErrorHandler.unexpectedCharacterContent(candidateIndex, functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, elementDefinition);
        }else{
            messageHandler.unexpectedCharacterContent(-1, charsSystemId, charsLineNumber, columnNumber, elementDefinition);
        }
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        if(isCandidate){
            //int functionalEquivalenceCode = attributeDefinition.functionalEquivalenceCode();
            int functionalEquivalenceCode = charsSystemId.hashCode()+
                                                charsLineNumber+
                                                columnNumber;
            messageHandler.unexpectedAttributeValue(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
            candidatesConflictErrorHandler.unexpectedAttributeValue(candidateIndex, functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
        }else{
            messageHandler.unexpectedAttributeValue(-1, charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
        }
	}
	
	public void unresolvedCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = 0;
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            messageHandler.unresolvedCharacterContent(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedCharacterContent(candidateIndex, functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.unresolvedCharacterContent(-1, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	public void unresolvedAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = attributeQName.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            messageHandler.unresolvedAttributeValue(functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedAttributeValue(candidateIndex, functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.unresolvedAttributeValue(-1, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        if(isCandidate){
            int functionalEquivalenceCode = token.hashCode()+
                                        charsDefinition.functionalEquivalenceCode()+
                                        datatypeErrorMessage.hashCode();
            messageHandler.listTokenDatatypeError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.listTokenDatatypeError(candidateIndex, functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.listTokenDatatypeError(-1, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
        }
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        if(isCandidate){
            int functionalEquivalenceCode = token.hashCode()+
                                        charsDefinition.functionalEquivalenceCode();
            messageHandler.listTokenValueError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.listTokenValueError(candidateIndex, functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.listTokenValueError(-1, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        if(isCandidate){
            int functionalEquivalenceCode = token.hashCode()+
                                        charsDefinition.functionalEquivalenceCode();
            messageHandler.listTokenExceptedError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            candidatesConflictErrorHandler.listTokenExceptedError(candidateIndex, functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }else{
            messageHandler.listTokenExceptedError(-1, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
        }
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = token.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            messageHandler.ambiguousListToken(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousListToken(candidateIndex, functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousListToken(-1, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
	}
    public void ambiguousListTokenInContextError(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = token.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            messageHandler.ambiguousListTokenInContextError(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousListTokenInContextError(candidateIndex, functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousListTokenInContextError(-1, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
    }    
	public void ambiguousListTokenInContextWarning(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        if(isCandidate){
            int functionalEquivalenceCode = token.hashCode();
            for(int i = 0; i < possibleDefinitions.length; i++){
                functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
            }
            candidatesConflictErrorHandler.ambiguousListTokenInContextWarning(candidateIndex, functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }else{
            messageHandler.ambiguousListTokenInContextWarning(-1, token, systemId, lineNumber, columnNumber, possibleDefinitions);
        }
    }
    
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
        if(isCandidate){
            int functionalEquivalenceCode = context.functionalEquivalenceCode()+
                                        definition.functionalEquivalenceCode()+
                                        expected+
                                        found;
            messageHandler.missingCompositorContent(functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
            candidatesConflictErrorHandler.missingCompositorContent(candidateIndex, functionalEquivalenceCode, context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
        }else{
            messageHandler.missingCompositorContent(-1, context, startSystemId, startLineNumber, startColumnNumber, definition, expected, found);
        }
	}
    public  void conflict(MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter [] candidateMessages){
        if(isCandidate){
            int functionalEquivalenceCode = disqualified.hashCode();                                        
            if(commonMessages != null) functionalEquivalenceCode += commonMessages.hashCode();
            if(candidateMessages != null){
                for(int i = 0; i < candidateMessages.length; i++){
                    if(candidateMessages[i] != null)functionalEquivalenceCode += candidateMessages[i].hashCode();
                }
            }
            messageHandler.conflict(functionalEquivalenceCode, commonMessages, candidatesCount, disqualified, candidateMessages);
            candidatesConflictErrorHandler.conflict(candidateIndex, functionalEquivalenceCode, commonMessages, candidatesCount, disqualified, candidateMessages);
        }else{
            messageHandler.conflict(-1, commonMessages, candidatesCount, disqualified, candidateMessages);
        }
    }
    
	public void handle(int contextType, String qName, AElement definition, Locator locator)
					throws SAXException{				
		candidatesConflictErrorHandler.delayMessageReporter(contextType, qName, definition, locator, messageHandler, candidateIndex);
	}
	
	public void handle(int contextType, String qName, Locator locator)
					throws SAXException{
		candidatesConflictErrorHandler.delayMessageReporter(contextType, qName, locator, messageHandler, candidateIndex);
	}
	
	
	
	public String toString(){
		//return "ExternalConflictErrorHandler "+hashCode()+" candidate "+candidateIndex+" in conflict "+conflictHandler.toString();
		return "ExternalConflictErrorHandler candidate "+candidateIndex;
	}
}