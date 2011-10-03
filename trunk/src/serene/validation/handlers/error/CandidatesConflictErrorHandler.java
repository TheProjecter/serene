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
import java.util.ArrayList;
import java.util.BitSet;

import org.xml.sax.Locator;
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

import serene.util.IntList;

import sereneWrite.MessageWriter;

public class CandidatesConflictErrorHandler implements CandidatesConflictErrorCatcher{	
    
	MessageWriter debugWriter;
    
    /**
    * It contains, for every error type, a mapping of reported error functional 
    * equivalence code to a BitSet marking the candidates that actually reported
    * the error. When the validation stage is over, the errors are processed 
    * and the messages that are common to all candidates are kept for reporting, 
    * the errors that only affect some candidates lead to the respective 
    * candidates being disqualified and the messages removed.    
    */
    int[][] errorCodes;
    BitSet[][]errorCandidates;
    /**
    * Keeps track of all the error messages that have actually been recorded in 
    * the message handler for the reported error. It is necessary because it is 
    * always attempted to record the message from a qualified candidate, so the
    * recording is delayed and sometimes it might not happen if the qualified 
    * candidate does not report the error (in which case it is good it was not
    * recorded since it should be deleted and reported from the candidates as
    * disqualifying anyway). 
    */
    int[][] recordedErrorMessages;
    
    
    int[][] warningCodes;
    // TODO
    // Not needed for disqualifications, can be replaced with simple int counts.
    BitSet[][]warningCandidates;
    int[][] recordedWarningMessages;
        
    
    ExternalConflictHandler conflictHandler;
    List<AElement> candidates;
    int candidatesCount;    
    ArrayList<ConflictMessageHandler> candidateMessageHandlers;
    
    ConflictMessageHandler localMessageHandler;    
    MessageReporter[] candidateDelayedMessages;
    MessageReporter commonMessages;
    MessageReporter candidatesCommonMessages;

    boolean allDisqualified;    
    boolean hasDelayedMessages;
    
	public CandidatesConflictErrorHandler(ExternalConflictHandler conflictHandler, MessageWriter debugWriter){
        this.debugWriter = debugWriter;        
        this.conflictHandler = conflictHandler;
        
        errorCodes = new int[ERROR_COUNT][];
        errorCandidates = new BitSet[ERROR_COUNT][];
        recordedErrorMessages = new int[ERROR_COUNT][];
        
        warningCodes = new int[WARNING_COUNT][];
        warningCandidates = new BitSet[WARNING_COUNT][];
        recordedWarningMessages = new int[WARNING_COUNT][];
        
        candidateMessageHandlers = new ArrayList<ConflictMessageHandler>();        
	}	       
    
    public void init(){
        localMessageHandler = new ConflictMessageHandler(debugWriter);
    }
    
    void addCandidateMessageHandler(ConflictMessageHandler messageHandler){
        candidateMessageHandlers.add(messageHandler);        
    }
	
    public void setCandidates(List<AElement> candidates){
        this.candidates = candidates;
        this.candidatesCount = candidates.size();
        conflictHandler.init(candidatesCount);
    }
    
    public void endValidationStage(){        
        for(int i = 0; i < ERROR_COUNT; i++){
            if(errorCandidates[i] !=  null){
                for(int j = 0; j < errorCandidates[i].length; j++){
                    if(errorCandidates[i][j].cardinality() == candidatesCount){
                        for(ConflictMessageHandler candidate: candidateMessageHandlers){
                            candidate.clearErrorMessage(i, errorCodes[i][j]);
                        }                 
                    }else{
                        conflictHandler.disqualify(errorCandidates[i][j]);
                        if(recordedErrorMessages[i] != null){
                            for(int k = 0; k < recordedErrorMessages[i].length; k++){
                                if(recordedErrorMessages[i][k] == errorCodes[i][j]){
                                    localMessageHandler.clearErrorMessage(i, errorCodes[i][j]);
                                    break;
                                }
                            }
                        }
                            
                    }                    
                }
                recordedErrorMessages[i] = null;
                errorCandidates[i] = null;
                errorCodes[i] = null;
            }                        
        }
        
        for(int i = 0; i < WARNING_COUNT; i++){
            if(warningCandidates[i] !=  null){
                for(int j = 0; j < warningCandidates[i].length; j++){
                    if(warningCandidates[i][j].cardinality() == candidatesCount){
                        // Not needed since it was not recorded.
                        /*for(ConflictMessageHandler candidate: candidateMessageHandlers){
                            candidate.clearWarningMessage(i, warningCodes[i][j]);
                        } */                
                    }else{
                        //conflictHandler.disqualify(warningCandidates[i][j]);
                        if(recordedWarningMessages[i] != null){
                            for(int k = 0; k < recordedWarningMessages[i].length; k++){
                                if(recordedWarningMessages[i][k] == warningCodes[i][j]){
                                    localMessageHandler.clearWarningMessage(i, warningCodes[i][j]);
                                    break;
                                }
                            }
                        }                           
                    }                    
                }
                recordedWarningMessages[i] = null;
                warningCandidates[i] = null;
                warningCodes[i] = null;
            }                        
        }
    }
    
    // In case the result is ambiguous (several qualified candidates) it attempts 
    // to disqualify all those with errors in the subtree (delayed messages), but 
    // the result of the operation is only kept if it doesn't result in 
    // unresolved (no qualified candidates at all); it also sets the booleans 
    // used for mustReport() meanwhile.
    public void endContextValidation(){
        int disqualifiedCount = conflictHandler.getDisqualifiedCount();
        int qualifiedCount = candidatesCount - disqualifiedCount;
        
        if(qualifiedCount == 0){
            allDisqualified = true;
            return;
        }
        
        if(candidateDelayedMessages == null){
            return;
        }
        
        if(qualifiedCount == 1){
            int winnerIndex = conflictHandler.getNextQualified(0);
            if(candidateDelayedMessages[winnerIndex] != null) hasDelayedMessages = true;
            for(int i = 0; i < candidateDelayedMessages.length; i++){
                if(i != winnerIndex && candidateDelayedMessages[i] != null) candidateDelayedMessages[i] = null;
            }
        }
        
        BitSet disqualified = conflictHandler.getDisqualified();// it's a clone, not the actual object, so it can be modified without changing the conflict handler
        for(int i = 0; i < candidatesCount; i++){
            if(candidateDelayedMessages[i] != null){
                if(!disqualified.get(i)){
                    disqualified.set(i);
                    if(!hasDelayedMessages)  hasDelayedMessages = true;
                }else{
                    candidateDelayedMessages[i] = null;
                }
            }
        }
        
        if(candidatesCount == disqualified.cardinality()){//using the subtree for disqualification results in unresolved, so it is not applied
            return;
        }
        
        hasDelayedMessages = false;
        for(int i = 0; i < candidatesCount; i++){
            if(disqualified.get(i))candidateDelayedMessages[i] = null;
            else if(!hasDelayedMessages && candidateDelayedMessages[i] != null) hasDelayedMessages = true;
        }
    }
    
    public boolean mustReport(){
        if(allDisqualified || hasDelayedMessages){
            return true; 
        }
        if(commonMessages != null){
            return true;
        }
        if(localMessageHandler.getErrorMessageCount() > 0){
            return true;
        }
        return false;
    }

    public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator, ContextErrorHandler contextErrorHandler) throws SAXException{
        
        if(localMessageHandler.getErrorMessageCount() > 0){
            delayMessageReporter(contextType, qName, locator, localMessageHandler, false);
        }        
        int qualifiedCount = candidatesCount - conflictHandler.getDisqualifiedCount();
    
        localMessageHandler.setContextQName(qName);
        localMessageHandler.setContextLocation(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        localMessageHandler.setContextType(contextType);
        if(qualifiedCount == 1){
            int q = conflictHandler.getNextQualified(0);
            localMessageHandler.setConflictResolutionId(MessageReporter.RESOLVED);
            localMessageHandler.setContextDefinition(candidates.get(q));            
            if(candidatesCommonMessages != null){
                candidatesCommonMessages.setConflictResolutionId(MessageReporter.RESOLVED);
                candidatesCommonMessages.setContextDefinition(candidates.get(conflictHandler.getNextQualified(0)));
            }
                          
            contextErrorHandler.conflict(MessageReporter.RESOLVED, commonMessages, candidatesCount, conflictHandler.getDisqualified(), candidateDelayedMessages);
            contextErrorHandler.handle(contextType, qName, candidates.get(conflictHandler.getNextQualified(0)), restrictToFileName, locator);
        }else if(qualifiedCount == 0){
            if(candidatesCommonMessages != null)candidatesCommonMessages.setConflictResolutionId(MessageReporter.UNRESOLVED);
            localMessageHandler.setConflictResolutionId(MessageReporter.UNRESOLVED);
            
            contextErrorHandler.conflict(MessageReporter.UNRESOLVED, commonMessages, candidatesCount, conflictHandler.getDisqualified(), candidateDelayedMessages);
            contextErrorHandler.record(contextType, qName, restrictToFileName, locator);
            // The contextErrorHandler's MessageReporter will be passed to the 
            // InternalConflictResolver and the messages will be integrated 
        }else{
            if(candidatesCommonMessages != null)candidatesCommonMessages.setConflictResolutionId(MessageReporter.AMBIGUOUS);
            localMessageHandler.setConflictResolutionId(MessageReporter.AMBIGUOUS);
            
            contextErrorHandler.conflict(MessageReporter.AMBIGUOUS, commonMessages, candidatesCount, conflictHandler.getDisqualified(), candidateDelayedMessages);
            contextErrorHandler.record(contextType, qName, restrictToFileName, locator);
            // The contextErrorHandler's MessageReporter will be passed to the 
            // InternalConflictResolver and the messages will be integrated 
        }       
    }   
        
    void recordError(int errorId, int errorFunctionalEquivalenceCode, int candidateIndex){       
        if(errorCodes[errorId] == null){          
            errorCodes[errorId] = new int[1];
            errorCandidates[errorId] = new BitSet[1];
            
            errorCodes[errorId][0] = errorFunctionalEquivalenceCode;
            errorCandidates[errorId][0] = new BitSet();
            errorCandidates[errorId][0].set(candidateIndex);
            return;
        }        
        boolean existingError = false;
        for(int i = errorCodes[errorId].length - 1; i >=0; i--){
            if(errorCodes[errorId][i] == errorFunctionalEquivalenceCode){
                errorCandidates[errorId][i].set(candidateIndex);
                existingError = true;
            }
        }
        if(!existingError){
            //increase size of arrays for the error id
            //record the errorFunctionalEquivalenceCode and candidateIndex
            int length = errorCodes[errorId].length;
            
            int[] increasedCodes = new int[length+1];
            System.arraycopy(errorCodes[errorId], 0, increasedCodes, 0, length);
            errorCodes[errorId] = increasedCodes;
            errorCodes[errorId][length] = errorFunctionalEquivalenceCode;
            
            BitSet[] increasedCandidates = new BitSet[length+1];
            System.arraycopy(errorCandidates[errorId], 0, increasedCandidates, 0, length);
            errorCandidates[errorId] = increasedCandidates;
            errorCandidates[errorId][length] = new BitSet();
            errorCandidates[errorId][length].set(candidateIndex);
        }
    }

    void recordWarning(int warningId, int warningFunctionalEquivalenceCode, int candidateIndex){       
        if(warningCodes[warningId] == null){          
            warningCodes[warningId] = new int[1];
            warningCandidates[warningId] = new BitSet[1];
            
            warningCodes[warningId][0] = warningFunctionalEquivalenceCode;
            warningCandidates[warningId][0] = new BitSet();
            warningCandidates[warningId][0].set(candidateIndex);
            return;
        }        
        boolean existingWarning = false;
        for(int i = warningCodes[warningId].length - 1; i >=0; i--){
            if(warningCodes[warningId][i] == warningFunctionalEquivalenceCode){
                warningCandidates[warningId][i].set(candidateIndex);
                existingWarning = true;
            }
        }
        if(!existingWarning){
            //increase size of arrays for the warning id
            //record the warningFunctionalEquivalenceCode and candidateIndex
            int length = warningCodes[warningId].length;
            
            int[] increasedCodes = new int[length+1];
            System.arraycopy(warningCodes[warningId], 0, increasedCodes, 0, length);
            warningCodes[warningId] = increasedCodes;
            warningCodes[warningId][length] = warningFunctionalEquivalenceCode;
            
            BitSet[] increasedCandidates = new BitSet[length+1];
            System.arraycopy(warningCandidates[warningId], 0, increasedCandidates, 0, length);
            warningCandidates[warningId] = increasedCandidates;
            warningCandidates[warningId][length] = new BitSet();
            warningCandidates[warningId][length].set(candidateIndex);
        }
    }    
        
    boolean mustRecordErrorMessage(int errorId, int errorFunctionalEquivalenceCode, int candidateIndex){
        return candidateIndex == getErrorRecordingIndex(errorId, errorFunctionalEquivalenceCode, candidateIndex);
    }
    
    boolean mustRecordWarningMessage(int warningId, int warningFunctionalEquivalenceCode, int candidateIndex){
        return candidateIndex == getWarningRecordingIndex(warningId, warningFunctionalEquivalenceCode, candidateIndex);
    }
    
    int getErrorRecordingIndex(int errorId, int errorFunctionalEquivalenceCode, int candidateIndex){        
        int firstQ = conflictHandler.getNextQualified(0);        
        if(!isErrorMessageRecorded(errorId, errorFunctionalEquivalenceCode)  && candidateIndex > firstQ){ 
            return candidateIndex;
        }
        return firstQ;
    }

    int getWarningRecordingIndex(int warningId, int warningFunctionalEquivalenceCode, int candidateIndex){        
        int firstQ = conflictHandler.getNextQualified(0);        
        if(!isWarningMessageRecorded(warningId, warningFunctionalEquivalenceCode)  && candidateIndex > firstQ){ 
            return candidateIndex;
        }
        return firstQ;
    }    
    
    
    boolean isErrorMessageRecorded(int errorId, int errorFunctionalEquivalenceCode){
        int[] recordedErrorCodes = recordedErrorMessages[errorId];
        if( recordedErrorCodes == null ) return false;
        for(int i = 0; i <  recordedErrorCodes.length; i++){
            if(recordedErrorCodes[i] == errorFunctionalEquivalenceCode) return true;
        }
        return false;
    }
    
    boolean isWarningMessageRecorded(int warningId, int warningFunctionalEquivalenceCode){
        int[] recordedWarningCodes = recordedWarningMessages[warningId];
        if( recordedWarningCodes == null ) return false;
        for(int i = 0; i <  recordedWarningCodes.length; i++){
            if(recordedWarningCodes[i] == warningFunctionalEquivalenceCode) return true;
        }
        return false;
    }
    
    void setErrorMessageRecorded(int errorId, int errorFunctionalEquivalenceCode){
        if( recordedErrorMessages[errorId] == null ){
            recordedErrorMessages[errorId] = new int[1];
            recordedErrorMessages[errorId][0] = errorFunctionalEquivalenceCode;
            return;
        }
        int length = recordedErrorMessages[errorId].length;
        int[] increased = new int[length+1];
        System.arraycopy(recordedErrorMessages[errorId], 0, increased, 0, length);
        recordedErrorMessages[errorId] = increased;
        recordedErrorMessages[errorId][length] = errorFunctionalEquivalenceCode;
    }
    
    void setWarningMessageRecorded(int warningId, int warningFunctionalEquivalenceCode){
        if( recordedWarningMessages[warningId] == null ){
            recordedWarningMessages[warningId] = new int[1];
            recordedWarningMessages[warningId][0] = warningFunctionalEquivalenceCode;
            return;
        }
        int length = recordedWarningMessages[warningId].length;
        int[] increased = new int[length+1];
        System.arraycopy(recordedWarningMessages[warningId], 0, increased, 0, length);
        recordedWarningMessages[warningId] = increased;
        recordedWarningMessages[warningId][length] = warningFunctionalEquivalenceCode;
    }
    
    // from direct candidates and subtree through ExternalConflictErrorHandler    
    public void delayMessageReporter(int contextType, String qName, AElement definition, Locator locator, MessageReporter messageHandler, int candidateIndex){
        messageHandler.setContextType(contextType);
        messageHandler.setContextQName(qName);
		messageHandler.setContextLocation(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
		messageHandler.setContextDefinition(definition);
        
        if(candidateDelayedMessages == null)candidateDelayedMessages = new MessageReporter[candidatesCount];
        if(candidateDelayedMessages[candidateIndex] == null){
            candidateDelayedMessages[candidateIndex] = messageHandler;
        }else{
            messageHandler.setParent(candidateDelayedMessages[candidateIndex]);
            candidateDelayedMessages[candidateIndex] = messageHandler;
        }
    }
    public void delayMessageReporter(int contextType, String qName, Locator locator, MessageReporter messageHandler, int candidateIndex){        
        messageHandler.setContextType(contextType);
        messageHandler.setContextQName(qName);
		messageHandler.setContextLocation(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        
        if(candidateDelayedMessages == null)candidateDelayedMessages = new MessageReporter[candidatesCount];
        if(candidateDelayedMessages[candidateIndex] == null){
            candidateDelayedMessages[candidateIndex] = messageHandler;
        }else{
            messageHandler.setParent(candidateDelayedMessages[candidateIndex]);
            candidateDelayedMessages[candidateIndex] = messageHandler;
        }
    }
    // internal conflict from direct candidates and subtree through ExternalConflictErrorHandler
    public void delayMessageReporter(ConflictMessageReporter conflictMessageReporter, int candidateIndex){
        if(candidateDelayedMessages == null)candidateDelayedMessages = new MessageReporter[candidatesCount];
        if(candidateDelayedMessages[candidateIndex] == null){
            candidateDelayedMessages[candidateIndex] = conflictMessageReporter;
        }else{
            conflictMessageReporter.setParent(candidateDelayedMessages[candidateIndex]);
            candidateDelayedMessages[candidateIndex] = conflictMessageReporter;
        }
    }
    
    
    // from direct candidates and subtree through CommonErrorHandler or localMessageHandler
    public void delayMessageReporter(int contextType, String qName, AElement definition, Locator locator, MessageReporter messageHandler, boolean isCandidate){
        messageHandler.setContextType(contextType);
        messageHandler.setContextQName(qName);
		messageHandler.setContextLocation(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
		messageHandler.setContextDefinition(definition); 
                
        if(commonMessages == null){
            commonMessages = messageHandler;
        }else{
            messageHandler.setParent(commonMessages);
            commonMessages = messageHandler;
        }
        
        if(isCandidate) candidatesCommonMessages = messageHandler;
    }
    public void delayMessageReporter(int contextType, String qName, Locator locator, MessageReporter messageHandler, boolean isCandidate){
        messageHandler.setContextType(contextType);
        messageHandler.setContextQName(qName);
		messageHandler.setContextLocation(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
        
        if(commonMessages == null){
            commonMessages = messageHandler;
        }else{
            messageHandler.setParent(commonMessages);
            commonMessages = messageHandler;
        }
        
        if(isCandidate) candidatesCommonMessages = messageHandler;
    }
    // internal conflict from direct candidates and subtree through CommonErrorHandler or localMessageHandler
    public void delayMessageReporter(ConflictMessageReporter conflictMessageReporter, boolean isCandidate){
        if(commonMessages == null){
            commonMessages = conflictMessageReporter;
        }else{
            conflictMessageReporter.setParent(commonMessages);
            commonMessages = conflictMessageReporter;
        }
        
        if(isCandidate) candidatesCommonMessages = conflictMessageReporter;
    }
    
	public void unknownElement(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber){
        recordError(UNKNOWN_ELEMENT, functionalEquivalenceCode, candidateIndex);
		if(mustRecordErrorMessage(UNKNOWN_ELEMENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unknownElement(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
            setErrorMessageRecorded(UNKNOWN_ELEMENT, functionalEquivalenceCode);
        }
	}	
	
	public void unexpectedElement(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_ELEMENT, functionalEquivalenceCode, candidateIndex);
		if(mustRecordErrorMessage(UNEXPECTED_ELEMENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unexpectedElement(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            setErrorMessageRecorded(UNEXPECTED_ELEMENT, functionalEquivalenceCode);
        }
	}	
    
    
	public void unexpectedAmbiguousElement(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_AMBIGUOUS_ELEMENT, functionalEquivalenceCode, candidateIndex);     
		if(mustRecordErrorMessage(UNEXPECTED_AMBIGUOUS_ELEMENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unexpectedAmbiguousElement(functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
            setErrorMessageRecorded(UNEXPECTED_AMBIGUOUS_ELEMENT, functionalEquivalenceCode);
        }
	}		
	
	
	public void unknownAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber){
        recordError(UNKNOWN_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);
		if(mustRecordErrorMessage(UNKNOWN_ATTRIBUTE, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unknownAttribute(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
            setErrorMessageRecorded(UNKNOWN_ATTRIBUTE, functionalEquivalenceCode);
        }
	}
	
	public void unexpectedAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){	    
        recordError(UNEXPECTED_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);        
		if(mustRecordErrorMessage(UNEXPECTED_ATTRIBUTE, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unexpectedAttribute(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            setErrorMessageRecorded(UNEXPECTED_ATTRIBUTE, functionalEquivalenceCode);
        }
	}
	
	
	public void unexpectedAmbiguousAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_AMBIGUOUS_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(UNEXPECTED_AMBIGUOUS_ATTRIBUTE, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unexpectedAmbiguousAttribute(functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
            setErrorMessageRecorded(UNEXPECTED_AMBIGUOUS_ATTRIBUTE, functionalEquivalenceCode);
        }
	}
	
	public void misplacedElement(int candidateIndex, int functionalEquivalenceCode, 
                                            APattern contextDefinition, 
											String startSystemId, 
											int startLineNumber, 
											int startColumnNumber, 
											APattern definition, 
											String qName, 
											String systemId, 
											int lineNumber, 
											int columnNumber,
											APattern sourceDefinition, 
											APattern reper){//not stored, only used for internal conflict handling
        recordError(MISPLACED_ELEMENT, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(MISPLACED_ELEMENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.misplacedElement(functionalEquivalenceCode,
                                                        contextDefinition, 
                                                        startSystemId, 
                                                        startLineNumber, 
                                                        startColumnNumber, 
                                                        definition, 
                                                        qName, 
                                                        systemId, 
                                                        lineNumber, 
                                                        columnNumber,
                                                        sourceDefinition, 
                                                        reper);
            setErrorMessageRecorded(MISPLACED_ELEMENT, functionalEquivalenceCode);
        }
	}
    public void misplacedElement(int candidateIndex, int functionalEquivalenceCode, 
                                            APattern contextDefinition, 
											String startSystemId, 
											int startLineNumber, 
											int startColumnNumber, 
											APattern definition, 
											String[] qName, 
											String[] systemId, 
											int[] lineNumber, 
											int[] columnNumber,
											APattern[] sourceDefinition, 
											APattern reper){//not stored, only used for internal conflict handling
        recordError(MISPLACED_ELEMENT, functionalEquivalenceCode, candidateIndex);		        
        if(mustRecordErrorMessage(MISPLACED_ELEMENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.misplacedElement(functionalEquivalenceCode,
                                                            contextDefinition, 
                                                            startSystemId, 
                                                            startLineNumber, 
                                                            startColumnNumber, 
                                                            definition, 
                                                            qName, 
                                                            systemId, 
                                                            lineNumber, 
                                                            columnNumber,
                                                            sourceDefinition, 
                                                            reper);
            setErrorMessageRecorded(MISPLACED_ELEMENT, functionalEquivalenceCode);
        }
	}
			
	
	public void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                    Rule context,
									String startSystemId,
									int startLineNumber,
									int startColumnNumber,
									APattern definition, 
									String[] qName, 
									String[] systemId, 
									int[] lineNumber, 
									int[] columnNumber){
        recordError(EXCESSIVE_CONTENT, functionalEquivalenceCode, candidateIndex);                     
	    if(mustRecordErrorMessage(EXCESSIVE_CONTENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.excessiveContent(functionalEquivalenceCode,
                                                            context,
                                                            startSystemId,
                                                            startLineNumber,
                                                            startColumnNumber,
                                                            definition, 
                                                            qName, 
                                                            systemId, 
                                                            lineNumber, 
                                                            columnNumber);
            setErrorMessageRecorded(EXCESSIVE_CONTENT, functionalEquivalenceCode);
        }
	}   
	public void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                Rule context, 
								APattern definition, 
								String qName, 
								String systemId, 
								int lineNumber,		
								int columnNumber){
		//if(recordError(EXCESSIVE_CONTENT, candidateIndex))
        try{
        localMessageHandler.excessiveContent(functionalEquivalenceCode, 
                                                                context, 
                                                                definition, 
                                                                qName, 
                                                                systemId, 
                                                                lineNumber,		
                                                                columnNumber);
        }catch(IllegalArgumentException e){
            if(!conflictHandler.isDisqualified(candidateIndex)){
                throw new IllegalStateException(e);
            }
        }
	}
    
    
	public void unresolvedAmbiguousElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        recordError(UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unresolvedAmbiguousElementContentError(functionalEquivalenceCode,
                                                                qName, 
                                                                systemId, 
                                                                lineNumber, 
                                                                columnNumber, 
                                                                possibleDefinitions);
            setErrorMessageRecorded(UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode);
        }
	}
	
	public void unresolvedUnresolvedElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        recordError(UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unresolvedUnresolvedElementContentError(functionalEquivalenceCode,
                                                                qName, 
                                                                systemId, 
                                                                lineNumber, 
                                                                columnNumber, 
                                                                possibleDefinitions);
            setErrorMessageRecorded(UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode);
        }
	}
    
    
	public void unresolvedAttributeContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions){
        recordError(UNRESOLVED_ATTRIBUTE_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(UNRESOLVED_ATTRIBUTE_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unresolvedAttributeContentError(functionalEquivalenceCode,
                                                                            qName, 
                                                                            systemId, 
                                                                            lineNumber, 
                                                                            columnNumber, 
                                                                            possibleDefinitions);
            setErrorMessageRecorded(UNRESOLVED_ATTRIBUTE_CONTENT_ERROR, functionalEquivalenceCode);
        }
	}
	
	public void ambiguousCharsContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String systemId, 
									int lineNumber, 
									int columnNumber, 
									CharsActiveTypeItem[] possibleDefinitions){
        recordError(AMBIGUOUS_CHARS_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(AMBIGUOUS_CHARS_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.ambiguousCharsContentError(functionalEquivalenceCode,
                                                                        systemId, 
                                                                        lineNumber, 
                                                                        columnNumber, 
                                                                        possibleDefinitions);
            setErrorMessageRecorded(AMBIGUOUS_CHARS_CONTENT_ERROR, functionalEquivalenceCode);
        }
	}
	

    public void ambiguousUnresolvedElementContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        recordWarning(AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);
        if(mustRecordWarningMessage(AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.ambiguousUnresolvedElementContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            setWarningMessageRecorded(AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode);
        }
    }
    
    public void ambiguousAmbiguousElementContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        recordWarning(AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);
        if(mustRecordWarningMessage(AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.ambiguousAmbiguousElementContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            setWarningMessageRecorded(AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode);
        }
    }
    
	public void ambiguousAttributeContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
        recordWarning(AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);
        if(mustRecordWarningMessage(AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.ambiguousAttributeContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            setWarningMessageRecorded(AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING, functionalEquivalenceCode);
        }
    }
    
	public void ambiguousCharsContentWarning(int candidateIndex, int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordWarning(AMBIGUOUS_CHARS_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);
        if(mustRecordWarningMessage(AMBIGUOUS_CHARS_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.ambiguousCharsContentWarning(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
            setWarningMessageRecorded(AMBIGUOUS_CHARS_CONTENT_WARNING, functionalEquivalenceCode);
        }
    }    
	
	
	public void missingContent(int candidateIndex, int functionalEquivalenceCode, 
                                Rule context, 
								String startSystemId, 
								int startLineNumber, 
								int startColumnNumber,								 
								APattern definition, 
								int expected, 
								int found,
								String[] qName, 
								String[] systemId, 
								int[] lineNumber, 
								int[] columnNumber){
        recordError(MISSING_CONTENT, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(MISSING_CONTENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.missingContent(functionalEquivalenceCode,
                                                                context, 
                                                                startSystemId, 
                                                                startLineNumber, 
                                                                startColumnNumber,								 
                                                                definition, 
                                                                expected, 
                                                                found,
                                                                qName, 
                                                                systemId, 
                                                                lineNumber, 
                                                                columnNumber);
            setErrorMessageRecorded(MISSING_CONTENT, functionalEquivalenceCode);
        }
    }
    
	public void illegalContent(int candidateIndex, int functionalEquivalenceCode, 
                            Rule context, 
							String startQName, 
							String startSystemId, 
							int startLineNumber, 
							int startColumnNumber){
        recordError(ILLEGAL_CONTENT, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(ILLEGAL_CONTENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.illegalContent(functionalEquivalenceCode,
                                                                context, 
                                                                startQName, 
                                                                startSystemId, 
                                                                startLineNumber, 
                                                                startColumnNumber);
            setErrorMessageRecorded(ILLEGAL_CONTENT, functionalEquivalenceCode);
        }
	}
        
	public void undeterminedByContent(int candidateIndex, int functionalEquivalenceCode, String qName, String candidateDelayedMessages){
        recordError(UNDETERMINED_BY_CONTENT, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(UNDETERMINED_BY_CONTENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.undeterminedByContent(functionalEquivalenceCode, qName, candidateDelayedMessages);
            setErrorMessageRecorded(UNDETERMINED_BY_CONTENT, functionalEquivalenceCode);
        }
	}
	
    // {15}
	public void characterContentDatatypeError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        recordError(CHARACTER_CONTENT_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(CHARACTER_CONTENT_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.characterContentDatatypeError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            setErrorMessageRecorded(CHARACTER_CONTENT_DATATYPE_ERROR, functionalEquivalenceCode);
        }
	}
        
    //{16}
	public void attributeValueDatatypeError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        recordError(ATTRIBUTE_VALUE_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(ATTRIBUTE_VALUE_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.attributeValueDatatypeError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            setErrorMessageRecorded(ATTRIBUTE_VALUE_DATATYPE_ERROR, functionalEquivalenceCode);
        }
	}
        
        
	public void characterContentValueError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(CHARACTER_CONTENT_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(CHARACTER_CONTENT_VALUE_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.characterContentValueError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            setErrorMessageRecorded(CHARACTER_CONTENT_VALUE_ERROR, functionalEquivalenceCode);
        }
	}
    
	public void attributeValueValueError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(ATTRIBUTE_VALUE_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(ATTRIBUTE_VALUE_VALUE_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.attributeValueValueError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            setErrorMessageRecorded(ATTRIBUTE_VALUE_VALUE_ERROR, functionalEquivalenceCode);
        }
	}
    
    
	public void characterContentExceptedError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(CHARACTER_CONTENT_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(CHARACTER_CONTENT_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.characterContentExceptedError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            setErrorMessageRecorded(CHARACTER_CONTENT_EXCEPTED_ERROR, functionalEquivalenceCode);
        }
	}
    
	public void attributeValueExceptedError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(ATTRIBUTE_VALUE_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(ATTRIBUTE_VALUE_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.attributeValueExceptedError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            setErrorMessageRecorded(ATTRIBUTE_VALUE_EXCEPTED_ERROR, functionalEquivalenceCode);
        }
	}
    
	public void unexpectedCharacterContent(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
        recordError(UNEXPECTED_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(UNEXPECTED_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unexpectedCharacterContent(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, elementDefinition);
            setErrorMessageRecorded(UNEXPECTED_CHARACTER_CONTENT, functionalEquivalenceCode);
        }
	}
    
	public void unexpectedAttributeValue(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        recordError(UNEXPECTED_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(UNEXPECTED_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unexpectedAttributeValue(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
            setErrorMessageRecorded(UNEXPECTED_ATTRIBUTE_VALUE, functionalEquivalenceCode);
        }
	}
    
	public void unresolvedCharacterContent(int candidateIndex, int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(AMBIGUOUS_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(AMBIGUOUS_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unresolvedCharacterContent(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
            setErrorMessageRecorded(AMBIGUOUS_CHARACTER_CONTENT, functionalEquivalenceCode);
        }
	}
    
	// {24}
	public void unresolvedAttributeValue(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(AMBIGUOUS_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(AMBIGUOUS_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.unresolvedAttributeValue(functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
            setErrorMessageRecorded(AMBIGUOUS_ATTRIBUTE_VALUE, functionalEquivalenceCode);
        }
	}        
    
    
    // {25}
	public void listTokenDatatypeError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        recordError(LIST_TOKEN_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(LIST_TOKEN_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.listTokenDatatypeError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            setErrorMessageRecorded(LIST_TOKEN_DATATYPE_ERROR, functionalEquivalenceCode);
        }
	}
    
        
	public void listTokenValueError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(LIST_TOKEN_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(LIST_TOKEN_VALUE_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.listTokenValueError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            setErrorMessageRecorded(LIST_TOKEN_VALUE_ERROR, functionalEquivalenceCode);
        }
	}        
    
	public void listTokenExceptedError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(LIST_TOKEN_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);
		if(mustRecordErrorMessage(LIST_TOKEN_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.listTokenExceptedError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            setErrorMessageRecorded(LIST_TOKEN_EXCEPTED_ERROR, functionalEquivalenceCode);
        }
	}
    
    
	public void ambiguousListToken(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(AMBIGUOUS_LIST_TOKEN, functionalEquivalenceCode, candidateIndex);
	    if(mustRecordErrorMessage(AMBIGUOUS_LIST_TOKEN, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.ambiguousListToken(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            setErrorMessageRecorded(AMBIGUOUS_LIST_TOKEN, functionalEquivalenceCode);
        }
	}
        
    public void ambiguousListTokenInContextError(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_ERROR, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_ERROR, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.ambiguousListTokenInContextError(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            setErrorMessageRecorded(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_ERROR, functionalEquivalenceCode);
        }
    }
    public void ambiguousListTokenInContextWarning(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordWarning(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING, functionalEquivalenceCode, candidateIndex);
        if(mustRecordWarningMessage(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.ambiguousListTokenInContextWarning(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            setWarningMessageRecorded(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING, functionalEquivalenceCode);
        }
    }
    
    public  void conflict(int candidateIndex, int functionalEquivalenceCode, int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
        recordError(CONFLICT, functionalEquivalenceCode, candidateIndex);        
        if(mustRecordErrorMessage(CONFLICT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.conflict(functionalEquivalenceCode, conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
            setErrorMessageRecorded(CONFLICT, functionalEquivalenceCode);
        }
    }
        
	public void missingCompositorContent(int candidateIndex, int functionalEquivalenceCode, 
                                Rule context, 
								String startSystemId, 
								int startLineNumber, 
								int startColumnNumber,								 
								APattern definition, 
								int expected, 
								int found){
        recordError(MISSING_COMPOSITOR_CONTENT, functionalEquivalenceCode, candidateIndex);
        if(mustRecordErrorMessage(MISSING_COMPOSITOR_CONTENT, functionalEquivalenceCode, candidateIndex)){
            localMessageHandler.missingCompositorContent(functionalEquivalenceCode,
                                                                        context, 
                                                                        startSystemId, 
                                                                        startLineNumber, 
                                                                        startColumnNumber,								 
                                                                        definition, 
                                                                        expected, 
                                                                        found);
            setErrorMessageRecorded(MISSING_COMPOSITOR_CONTENT, functionalEquivalenceCode);
        }
	}

    /*public void clearErrors(){
        super.clearErrors();
        for(int i = 0; i < ERROR_COUNT; i++){
            candidatesWithError.add(null);
        }       
    }*/
       

    public void clear(){
        Arrays.fill(errorCodes, null);
        Arrays.fill(errorCandidates, null);
        Arrays.fill(recordedErrorMessages, null);
        
        conflictHandler.clear();
        candidateMessageHandlers.clear();
        candidatesCount = -1;
        candidateDelayedMessages = null;
        commonMessages = null;
        allDisqualified = false;
        hasDelayedMessages = false;
        candidatesCommonMessages = null;
    }	
}
