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
    
    boolean isHandled;

	public ExternalConflictErrorHandler(){
		super();
		id = ContextErrorHandlerManager.CONFLICT;		
	}
	
	public void recycle(){
        candidateIndex = -1;
        candidatesConflictErrorHandler = null;
        isCandidate = false;
        if(!isHandled && messageHandler != null){
            /*messageHandler.setDiscarded(true);*/
            messageHandler.clear(this);
        }        
		messageHandler = null;
        isHandled = false;
        
		pool.recycle(this);
	}	
	
	public void init(CandidatesConflictErrorHandler candidatesConflictErrorHandler, int candidateIndex, boolean isCandidate){		
		this.candidateIndex = candidateIndex;
        this.candidatesConflictErrorHandler = candidatesConflictErrorHandler;
        this.isCandidate = isCandidate;
        
		messageHandler = new ConflictMessageHandler();
		messageHandler.init(activeInputDescriptor);
        if(isCandidate)candidatesConflictErrorHandler.addCandidateMessageHandler(candidateIndex, messageHandler);
	}	
	public boolean isCandidate(){
        return isCandidate;
    }
    public void setCandidate(boolean isCandidate){
        this.isCandidate = isCandidate;
    }
    
	public void unknownElement(int inputRecordIndex){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        if(isCandidate){            
            messageHandler.unknownElement(functionalEquivalenceCode, inputRecordIndex);
            candidatesConflictErrorHandler.unknownElement(candidateIndex, functionalEquivalenceCode, inputRecordIndex);
        }else{
            messageHandler.unknownElement(functionalEquivalenceCode, inputRecordIndex);
        }
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode()+definition.hashCode();
        if(isCandidate){            
            messageHandler.unexpectedElement(functionalEquivalenceCode, definition, inputRecordIndex);
            candidatesConflictErrorHandler.unexpectedElement(candidateIndex,  functionalEquivalenceCode, definition, inputRecordIndex);
        }else{
            messageHandler.unexpectedElement(functionalEquivalenceCode, definition, inputRecordIndex);
        }
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] possibleDefinitions, int inputRecordIndex){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].hashCode();
        }
        if(isCandidate){            
            messageHandler.unexpectedAmbiguousElement(functionalEquivalenceCode, possibleDefinitions, inputRecordIndex);
            candidatesConflictErrorHandler.unexpectedAmbiguousElement(candidateIndex, functionalEquivalenceCode, possibleDefinitions, inputRecordIndex);
        }else{
            messageHandler.unexpectedAmbiguousElement(functionalEquivalenceCode, possibleDefinitions, inputRecordIndex);
        }
	}
	
	
	public void unknownAttribute(int inputRecordIndex){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        if(isCandidate){
            messageHandler.unknownAttribute(functionalEquivalenceCode, inputRecordIndex);
            candidatesConflictErrorHandler.unknownAttribute(candidateIndex, functionalEquivalenceCode, inputRecordIndex);
        }else{
            messageHandler.unknownAttribute(functionalEquivalenceCode, inputRecordIndex);
        }
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode()+definition.hashCode();	    
        if(isCandidate){            
            messageHandler.unexpectedAttribute(functionalEquivalenceCode, definition, inputRecordIndex);
            candidatesConflictErrorHandler.unexpectedAttribute(candidateIndex, functionalEquivalenceCode, definition, inputRecordIndex);
        }else{
            messageHandler.unexpectedAttribute(functionalEquivalenceCode, definition, inputRecordIndex);
        }
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinition.length; i++){
            functionalEquivalenceCode += possibleDefinition[i].hashCode();
        }
        if(isCandidate){            
            messageHandler.unexpectedAmbiguousAttribute(functionalEquivalenceCode, possibleDefinition, inputRecordIndex);
            candidatesConflictErrorHandler.unexpectedAmbiguousAttribute( candidateIndex, functionalEquivalenceCode, possibleDefinition, inputRecordIndex);
        }else{
            messageHandler.unexpectedAmbiguousAttribute(functionalEquivalenceCode, possibleDefinition, inputRecordIndex);
        }
	}
	
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper){
	    int functionalEquivalenceCode = contextDefinition.functionalEquivalenceCode()+
                                                definition.functionalEquivalenceCode();
        for(int i = 0; i < inputRecordIndex.length; i++){            
            functionalEquivalenceCode += activeInputDescriptor.getLineNumber(inputRecordIndex[i])+
                                            activeInputDescriptor.getColumnNumber(inputRecordIndex[i]);/*Quick hack, should work for the overwhelming majority of cases*/
            // TODO 
            // Replace with some integers representing the occurrence sequence. 
        }
        if(isCandidate){
            messageHandler.misplacedContent(functionalEquivalenceCode, contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
            candidatesConflictErrorHandler.misplacedContent(candidateIndex, functionalEquivalenceCode, contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
        }else{
            messageHandler.misplacedContent(functionalEquivalenceCode, contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
        }
	}
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int inputRecordIndex, APattern sourceDefinition, APattern reper){
	    int functionalEquivalenceCode = contextDefinition.functionalEquivalenceCode()+
											definition.functionalEquivalenceCode()+ 
											/*qName.hashCode()+
											sourceDefinition.functionalEquivalenceCode()*/
											activeInputDescriptor.getLineNumber(inputRecordIndex)+
                                            activeInputDescriptor.getColumnNumber(inputRecordIndex);/*Quick hack, should work for the overwhelming majority of cases*/
        if(isCandidate){
            messageHandler.misplacedContent(functionalEquivalenceCode, contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
            candidatesConflictErrorHandler.misplacedContent(candidateIndex, functionalEquivalenceCode, contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
        }else{
            messageHandler.misplacedContent(functionalEquivalenceCode, contextDefinition, startInputRecordIndex, definition, inputRecordIndex, sourceDefinition, reper);
        }
	}
	
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex){
	    int functionalEquivalenceCode = /*context.functionalEquivalenceCode()+*/
                                            excessiveDefinition.functionalEquivalenceCode();
        for(int i = 0; i < inputRecordIndex.length; i++){
            functionalEquivalenceCode += activeInputDescriptor.getItemDescription(inputRecordIndex[i]).hashCode();
        }
        if(isCandidate){            
            messageHandler.excessiveContent(functionalEquivalenceCode, context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
            candidatesConflictErrorHandler.excessiveContent(candidateIndex, functionalEquivalenceCode, context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
        }else{
            messageHandler.excessiveContent(functionalEquivalenceCode, context, startInputRecordIndex, excessiveDefinition, inputRecordIndex);
        }
	}
	public void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex){        
		messageHandler.excessiveContent(-1, context, excessiveDefinition, inputRecordIndex);
        if(isCandidate)candidatesConflictErrorHandler.excessiveContent(candidateIndex, -1, context, excessiveDefinition, inputRecordIndex);
	}
	
	public void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex){
	    int functionalEquivalenceCode = /*context.functionalEquivalenceCode()+*/
                                            definition.functionalEquivalenceCode()+
                                            expected+ 
                                            found;
        if(inputRecordIndex != null){
            for(int i = 0; i < inputRecordIndex.length; i++){
                functionalEquivalenceCode += activeInputDescriptor.getItemDescription(inputRecordIndex[i]).hashCode();
            }
        }
        if(isCandidate){
            messageHandler.missingContent(functionalEquivalenceCode, context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
            candidatesConflictErrorHandler.missingContent(candidateIndex, functionalEquivalenceCode, context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
        }else{
            messageHandler.missingContent(functionalEquivalenceCode, context, startInputRecordIndex, definition, expected, found, inputRecordIndex);
        }
	}

	public void illegalContent(Rule context, int startInputRecordIndex){
	    int functionalEquivalenceCode = context.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.illegalContent(functionalEquivalenceCode, context, startInputRecordIndex);
            candidatesConflictErrorHandler.illegalContent(candidateIndex, functionalEquivalenceCode, context, startInputRecordIndex);
        }else{
            messageHandler.illegalContent(functionalEquivalenceCode, context, startInputRecordIndex);
        }
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){
            messageHandler.unresolvedAmbiguousElementContentError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedAmbiguousElementContentError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.unresolvedAmbiguousElementContentError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedUnresolvedElementContentError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedUnresolvedElementContentError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.unresolvedUnresolvedElementContentError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	
	public void unresolvedAttributeContentError(int inputRecordIndex, AAttribute[] possibleDefinitions){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedAttributeContentError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedAttributeContentError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.unresolvedAttributeContentError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousUnresolvedElementContentWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousUnresolvedElementContentWarning(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.ambiguousUnresolvedElementContentWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousAmbiguousElementContentWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousAmbiguousElementContentWarning(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.ambiguousAmbiguousElementContentWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	
	public void ambiguousAttributeContentWarning(int inputRecordIndex, AAttribute[] possibleDefinitions){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousAttributeContentWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousAttributeContentWarning(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.ambiguousAttributeContentWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = 0;
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousCharacterContentWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousCharacterContentWarning(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.ambiguousCharacterContentWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = 0;
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousAttributeValueWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousAttributeValueWarning(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.ambiguousAttributeValueWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
		
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode()+
                                               datatypeErrorMessage.hashCode();
        if(isCandidate){
            messageHandler.characterContentDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.characterContentDatatypeError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.characterContentDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
        }
	}
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode()+
                                           datatypeErrorMessage.hashCode();
        if(isCandidate){            
            messageHandler.attributeValueDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.attributeValueDatatypeError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.attributeValueDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
        }
	}
	
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode();
        if(isCandidate){                        
            messageHandler.characterContentValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
            candidatesConflictErrorHandler.characterContentValueError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }else{
            messageHandler.characterContentValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }
	}
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.attributeValueValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
            candidatesConflictErrorHandler.attributeValueValueError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }else{
            messageHandler.attributeValueValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }
	}
	
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.characterContentExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
            candidatesConflictErrorHandler.characterContentExceptedError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }else{
            messageHandler.characterContentExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }
	}	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.attributeValueExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
            candidatesConflictErrorHandler.attributeValueExceptedError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }else{
            messageHandler.attributeValueExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
	    //int functionalEquivalenceCode = elementDefinition.functionalEquivalenceCode();
        int functionalEquivalenceCode = inputRecordIndex;//TODO review
        if(isCandidate){            
            messageHandler.unexpectedCharacterContent(functionalEquivalenceCode, inputRecordIndex, elementDefinition);
            candidatesConflictErrorHandler.unexpectedCharacterContent(candidateIndex, functionalEquivalenceCode, inputRecordIndex, elementDefinition);
        }else{
            messageHandler.unexpectedCharacterContent(functionalEquivalenceCode, inputRecordIndex, elementDefinition);
        }
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
	    //int functionalEquivalenceCode = attributeDefinition.functionalEquivalenceCode();
        int functionalEquivalenceCode = inputRecordIndex;// TODO review
        if(isCandidate){            
            messageHandler.unexpectedAttributeValue(functionalEquivalenceCode, inputRecordIndex, attributeDefinition);
            candidatesConflictErrorHandler.unexpectedAttributeValue(candidateIndex, functionalEquivalenceCode, inputRecordIndex, attributeDefinition);
        }else{
            messageHandler.unexpectedAttributeValue(functionalEquivalenceCode, inputRecordIndex, attributeDefinition);
        }
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = 0;
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedCharacterContent(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedCharacterContent(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.unresolvedCharacterContent(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedAttributeValue(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedAttributeValue(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.unresolvedAttributeValue(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode()+
                                        charsDefinition.functionalEquivalenceCode()+
                                        datatypeErrorMessage.hashCode();
        if(isCandidate){            
            messageHandler.listTokenDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
            candidatesConflictErrorHandler.listTokenDatatypeError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
        }else{
            messageHandler.listTokenDatatypeError(functionalEquivalenceCode, inputRecordIndex, charsDefinition, datatypeErrorMessage);
        }
	}
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.listTokenValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
            candidatesConflictErrorHandler.listTokenValueError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }else{
            messageHandler.listTokenValueError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
	    int functionalEquivalenceCode = charsDefinition.functionalEquivalenceCode();
        if(isCandidate){            
            messageHandler.listTokenExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
            candidatesConflictErrorHandler.listTokenExceptedError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }else{
            messageHandler.listTokenExceptedError(functionalEquivalenceCode, inputRecordIndex, charsDefinition);
        }
	}

    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.unresolvedListTokenInContextError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.unresolvedListTokenInContextError(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.unresolvedListTokenInContextError(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	    int functionalEquivalenceCode = activeInputDescriptor.getItemDescription(inputRecordIndex).hashCode();
        for(int i = 0; i < possibleDefinitions.length; i++){
            functionalEquivalenceCode += possibleDefinitions[i].functionalEquivalenceCode();
        }
        if(isCandidate){            
            messageHandler.ambiguousListTokenInContextWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
            candidatesConflictErrorHandler.ambiguousListTokenInContextWarning(candidateIndex, functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }else{
            messageHandler.ambiguousListTokenInContextWarning(functionalEquivalenceCode, inputRecordIndex, possibleDefinitions);
        }
    }
    
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
	    int functionalEquivalenceCode = context.functionalEquivalenceCode()+
                                        definition.functionalEquivalenceCode()+
                                        expected+
                                        found;
        if(isCandidate){
            messageHandler.missingCompositorContent(functionalEquivalenceCode, context, startInputRecordIndex, definition, expected, found);
            candidatesConflictErrorHandler.missingCompositorContent(candidateIndex, functionalEquivalenceCode, context, startInputRecordIndex, definition, expected, found);
        }else{
            messageHandler.missingCompositorContent(functionalEquivalenceCode, context, startInputRecordIndex, definition, expected, found);
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
		isHandled = true;	
	}
	
	public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator)
					throws SAXException{
		candidatesConflictErrorHandler.delayMessageReporter(contextType, qName, locator, messageHandler, candidateIndex);
		isHandled = true;	
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
        ConflictMessageReporter cmr = messageHandler.getConflictMessageReporter(errorDispatcher);
        return cmr;
    } 
	
	public String toString(){
		//return "ExternalConflictErrorHandler "+hashCode()+" candidate "+candidateIndex+" in conflict "+conflictHandler.toString();
		return "ExternalConflictErrorHandler candidate "+candidateIndex;
	}
}