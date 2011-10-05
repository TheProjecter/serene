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

import serene.validation.handlers.error.util.MissingContentAnalyser;

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
    IntList errorIds;
    int[][] errorCodes;
    BitSet[][]errorCandidates;
        
    APattern[] missingContentDefinitions;
    IntList missingContentCandidateIndexes;
    MissingContentAnalyser missingContentAnalyser;
    BitSet cumulatorDummy;
    
    IntList warningIds;
    int[][] warningCodes;
    int[][] warningsCount;
    // TODO
    // Not needed for disqualifications, can be replaced with simple int counts.
    /*BitSet[][]warningCandidates;*/
    /*int[][] recordedWarningMessages;*/
        
    IntList commonErrorIds;
    IntList commonErrorCodes;
    
    IntList commonWarningIds;
    IntList commonWarningCodes;
    
    
    ExternalConflictHandler conflictHandler;
    List<AElement> candidates;
    int candidatesCount;    
    ConflictMessageHandler[] candidateMessageHandlers;
    
    ConflictMessageHandler localMessageHandler;    
    MessageReporter[] candidateDelayedMessages;
    MessageReporter commonMessages;
    MessageReporter candidatesCommonMessages;

    boolean allDisqualified;    
    boolean hasDelayedMessages;
    
	public CandidatesConflictErrorHandler(ExternalConflictHandler conflictHandler, MessageWriter debugWriter){
        this.debugWriter = debugWriter;        
        this.conflictHandler = conflictHandler;
        
        errorIds = new IntList();
        errorCodes = new int[4][];
        errorCandidates = new BitSet[4][];
        /*recordedErrorMessages = new int[ERROR_COUNT][];*/
        
        warningIds = new IntList();
        warningCodes = new int[4][];
        warningsCount = new int[4][];
        /*warningCandidates = new BitSet[WARNING_COUNT][];*/
        /*recordedWarningMessages = new int[WARNING_COUNT][];*/
        
        missingContentCandidateIndexes = new IntList();
        
        commonErrorIds = new IntList();
        commonErrorCodes = new IntList();
        
        commonWarningIds = new IntList();
        commonWarningCodes = new IntList();
	}	       
    
    public void init(){
        localMessageHandler = new CandidatesConflictMessageHandler(debugWriter);        
    }
    
    void addCandidateMessageHandler(int candidateIndex, ConflictMessageHandler messageHandler){
        if(candidateMessageHandlers == null) candidateMessageHandlers = new ConflictMessageHandler[candidatesCount];
        candidateMessageHandlers[candidateIndex] = messageHandler;        
    }
	
    public void setCandidates(List<AElement> candidates){
        this.candidates = candidates;
        this.candidatesCount = candidates.size();
        conflictHandler.init(candidatesCount);
    }
    
    public void endValidationStage(){     
        // There are three situations that need to be detected:      
        //      => common error:         an errorId/FEC combination is present to all the candidates
        //      => disqualifying error:  an errorId/FEC combination is present to some of the candidates, but not the others
        //      => partially common error: for MISSING_CONTENT error present to all candidates with several different FECs 
        //          where there is at least one candidate whose missingDefinition constitutes a subset of the other candidates' definitions   
        //      
        if(! errorIds.isEmpty()){
            for(int i = 0; i < errorIds.size(); i++){
                int errorId = errorIds.get(i);                
                if(errorId == MISSING_CONTENT 
                            && errorCandidates[i].length > 1 
                        && missingContentCandidateIndexes.size() == candidatesCount ){
                                    
                    if(missingContentAnalyser == null) missingContentAnalyser = new MissingContentAnalyser(debugWriter);
                    BitSet partiallyCommon = missingContentAnalyser.getPartiallyCommon(missingContentDefinitions);
                    
                    if(partiallyCommon.isEmpty()){
                        // DISQUALIFYING ERRORS
                        for(int j = 0; j < errorCandidates[i].length; j++){
                            conflictHandler.disqualify(errorCandidates[i][j]);
                        }
                    }else if(partiallyCommon.cardinality() == missingContentDefinitions.length){
                        // COMMON
                        // Store the errorId and the codes. Transfer at the end.
                        for(int j = 0; j < errorCodes[i].length; j++){
                            commonErrorIds.add(errorId);
                            commonErrorCodes.add(errorCodes[i][j]);
                        }                        
                        /*for(int j = 0; j < errorCandidates[i].length; j++){
                           for(int k = 0; k < errorCandidates[i][j].length(); k++){
                               if(errorCandidates[i][j].get(k))candidateMessageHandlers[k].transferErrorMessage(errorId, errorCodes[i][j], localMessageHandler);
                           }
                        }*/
                    }else{
                        // PARTIALLY COMMON
                        int k = 0;                        
                        for(int j = 0; j < missingContentCandidateIndexes.size(); j++){
                           if(!partiallyCommon.get(j)){
                               conflictHandler.disqualify(missingContentCandidateIndexes.get(j));
                           }
                        }
                    }                     
                }else{
                    for(int j = 0; j < errorCandidates[i].length; j++){
                        if(errorCandidates[i][j].cardinality() == candidatesCount){
                            // COMMON
                            // Store the errorId and the codes. Transfer at the end.
                            commonErrorIds.add(errorId);
                            commonErrorCodes.add(errorCodes[i][j]);
                            /*for(ConflictMessageHandler candidate: candidateMessageHandlers){                            
                                //candidate.clearErrorMessage(i, errorCodes[i][j]);
                                candidate.transferErrorMessage(errorId, errorCodes[i][j], localMessageHandler);
                            }*/               
                        }else{
                            // DISQUALIFYING
                            conflictHandler.disqualify(errorCandidates[i][j]);
                        }                    
                    }
                }
                missingContentDefinitions = null;
                missingContentCandidateIndexes.clear();
                errorCandidates[i] = null;
                errorCodes[i] = null;
            }
            errorIds.clear();
        }  
            
            
        if(! warningIds.isEmpty()){
            for(int i = 0; i < warningIds.size(); i++){
                int warningId = warningIds.get(i);
                for(int j = 0; j < warningsCount[i].length; j++){
                    if(warningsCount[i][j] == candidatesCount){
                        // COMMON
                        // Store the warningId and the codes. Transfer at the end.
                        commonWarningIds.add(warningId);
                        commonWarningCodes.add(warningCodes[i][j]);
                        /*for(ConflictMessageHandler candidate: candidateMessageHandlers){                            
                            //candidate.clearErrorMessage(i, warningCodes[i][j]);
                            candidate.transferErrorMessage(warningId, warningCodes[i][j], localMessageHandler);
                        }*/               
                    }                    
                }
                warningsCount[i] = null;
                warningCodes[i] = null;
            }
            warningIds.clear();
        } 
    }
    
    
    private boolean allCandidatesPresentError(int errorId){
        if(cumulatorDummy == null) cumulatorDummy = new BitSet();
        for(int j = 0; j < errorCandidates[errorId].length; j++){
           cumulatorDummy.or(errorCandidates[errorId][j]);
           if(cumulatorDummy.cardinality() == candidatesCount){
               cumulatorDummy.clear();
               return true;
           }
        }   
        cumulatorDummy.clear();
        return false;
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
            if(!commonErrorIds.isEmpty()){
                transferAllCommonErrors();
                commonErrorIds.clear();
                commonErrorCodes.clear();
            }
            if(!commonWarningIds.isEmpty()){
                transferAllCommonWarnings();
                commonWarningIds.clear();
                commonWarningCodes.clear();
            }
            return;
        }
        
        if(candidateDelayedMessages == null){
            if(qualifiedCount != 1){
                if(!commonErrorIds.isEmpty()){
                    transferQualifiedCommonErrors();
                    commonErrorIds.clear();
                    commonErrorCodes.clear();
                }
                if(!commonWarningIds.isEmpty()){
                    transferQualifiedCommonWarnings();
                    commonWarningIds.clear();
                    commonWarningCodes.clear();
                }
            }
            return;
        }
        
        if(qualifiedCount == 1){
            int winnerIndex = conflictHandler.getNextQualified(0);
            if(candidateDelayedMessages[winnerIndex] != null) hasDelayedMessages = true;
            for(int i = 0; i < candidateDelayedMessages.length; i++){
                if(i != winnerIndex && candidateDelayedMessages[i] != null) candidateDelayedMessages[i] = null;
            }
            commonErrorIds.clear();
            commonErrorCodes.clear();
            commonWarningIds.clear();
            commonWarningCodes.clear();
            return;
        }
        
        BitSet disqualified = conflictHandler.getDisqualified();// it's a clone, not the actual object, so it can be modified without changing the conflict handler
        
        for(int i = 0; i < candidatesCount; i++){
            if(candidateDelayedMessages[i] != null){
                if(!disqualified.get(i)){
                    if(candidateDelayedMessages[i].containsOtherErrorMessage(commonErrorIds, commonErrorCodes)
                        //candidateDelayedMessages[i].containsErrorMessage()
                        ){                    
                        disqualified.set(i);
                        if(!hasDelayedMessages)  hasDelayedMessages = true;
                    }
                }else{
                    candidateDelayedMessages[i] = null;
                }
            }
        }
        
        
        if(candidatesCount == disqualified.cardinality()){//using the subtree for disqualification results in unresolved, so it is not applied
            if(!commonErrorIds.isEmpty()){
                transferQualifiedCommonErrors();
                commonErrorIds.clear();
                commonErrorCodes.clear();
            }
            if(!commonWarningIds.isEmpty()){
                transferQualifiedCommonWarnings();
                commonWarningIds.clear();
                commonWarningCodes.clear();
            }
            return;
        }
        
        hasDelayedMessages = false;
        conflictHandler.disqualify(disqualified);
        for(int i = 0; i < candidatesCount; i++){            
            if(disqualified.get(i)){
                candidateDelayedMessages[i] = null; 
            }
            else if(!hasDelayedMessages && candidateDelayedMessages[i] != null) hasDelayedMessages = true;
        }
        
        disqualifiedCount = conflictHandler.getDisqualifiedCount();
        qualifiedCount = candidatesCount - disqualifiedCount;
        
        if(qualifiedCount != 1){
            if(!commonErrorIds.isEmpty()){
                transferQualifiedCommonErrors();
                commonErrorIds.clear();
                commonErrorCodes.clear();
            }
            if(!commonWarningIds.isEmpty()){
                transferQualifiedCommonWarnings();
                commonWarningIds.clear();
                commonWarningCodes.clear();
            }
        }
    }
    
    void transferAllCommonErrors(){
        for(int i = 0; i < commonErrorIds.size(); i++){
            for(int j = 0; j < candidatesCount; j++){
                candidateMessageHandlers[j].transferErrorMessage(commonErrorIds.get(i), commonErrorCodes.get(i), localMessageHandler);
            }
        }
    }
    
    void transferQualifiedCommonErrors(){
        for(int i = 0; i < commonErrorIds.size(); i++){
            for(int j = 0; j < candidatesCount; j++){
                if(!conflictHandler.isDisqualified(j))candidateMessageHandlers[j].transferErrorMessage(commonErrorIds.get(i), commonErrorCodes.get(i), localMessageHandler);
            }
        }
    }
    
    void transferAllCommonWarnings(){
        for(int i = 0; i < commonWarningIds.size(); i++){
            for(int j = 0; j < candidatesCount; j++){
                candidateMessageHandlers[j].transferWarningMessage(commonWarningIds.get(i), commonWarningCodes.get(i), localMessageHandler);
            }
        }
    }
    
    void transferQualifiedCommonWarnings(){
        for(int i = 0; i < commonWarningIds.size(); i++){
            for(int j = 0; j < candidatesCount; j++){
                if(!conflictHandler.isDisqualified(j))candidateMessageHandlers[j].transferWarningMessage(commonWarningIds.get(i), commonWarningCodes.get(i), localMessageHandler);
            }
        }
    }
    
    public boolean mustReport(){
        if(allDisqualified || hasDelayedMessages){
            return true; 
        }
        if(commonMessages != null){
            return true;
        }
        if(localMessageHandler.getMessageTotalCount() > 0){
            return true;
        }
        return false;
    }

    public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator, ContextErrorHandler contextErrorHandler) throws SAXException{
        if(localMessageHandler.getMessageTotalCount() > 0){
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
        int index = errorIds.indexOf(errorId);        
        if(index >= 0){
            for(int i = 0; i < errorCodes[index].length; i++){
                if(errorCodes[index][i] == errorFunctionalEquivalenceCode){
                    errorCandidates[index][i].set(candidateIndex);
                    return;
                }
            }
            
            int length = errorCodes[index].length;
        
            int[] increasedCodes = new int[length+1];
            System.arraycopy(errorCodes[index], 0, increasedCodes, 0, length);
            errorCodes[index] = increasedCodes;
            errorCodes[index][length] = errorFunctionalEquivalenceCode;
            
            BitSet[] increasedCandidates = new BitSet[length+1];
            System.arraycopy(errorCandidates[index], 0, increasedCandidates, 0, length);
            errorCandidates[index] = increasedCandidates;
            errorCandidates[index][length] = new BitSet();
            errorCandidates[index][length].set(candidateIndex);
        }else{
            index = errorIds.size();
            errorIds.add(errorId);
                                    
            if(index == errorCodes.length){
                int size = index+1;
                int[][] increasedCodes = new int[size][];
                System.arraycopy(errorCodes, 0, increasedCodes, 0, index);
                errorCodes = increasedCodes;
                                
                BitSet[][] increasedCandidates = new BitSet[size][];
                System.arraycopy(errorCandidates, 0, increasedCandidates, 0, index);
                errorCandidates = increasedCandidates;
            }
            errorCodes[index] = new int[1];
            errorCodes[index][0] = errorFunctionalEquivalenceCode;
            
            errorCandidates[index] = new BitSet[1];
            errorCandidates[index][0] = new BitSet();
            errorCandidates[index][0].set(candidateIndex);
        }
    }

    void recordMissingContentError(int errorFunctionalEquivalenceCode, int candidateIndex, APattern missingDefinition){
        recordError(MISSING_CONTENT, errorFunctionalEquivalenceCode, candidateIndex);
        
        missingContentCandidateIndexes.add(candidateIndex);
        
        if(missingContentDefinitions == null){
            missingContentDefinitions = new APattern[1];
            missingContentDefinitions[0] = missingDefinition;
            return;
        }        
        
        int length = missingContentDefinitions.length;
        
        APattern[] increasedRDI = new APattern[length+1];
        System.arraycopy(missingContentDefinitions, 0, increasedRDI, 0, length);
        missingContentDefinitions = increasedRDI;
        missingContentDefinitions[length] = missingDefinition;
    }
    
    
    void recordWarning(int warningId, int warningFunctionalEquivalenceCode, int candidateIndex){       
        int index = warningIds.indexOf(warningId);        
        if(index >= 0){
            for(int i = 0; i < warningCodes[index].length; i++){
                if(warningCodes[index][i] == warningFunctionalEquivalenceCode){
                    warningsCount[index][i] = warningsCount[index][i] + 1;
                    return;
                }
            }
            
            int length = warningCodes[index].length;
        
            int[] increasedCodes = new int[length+1];
            System.arraycopy(warningCodes[index], 0, increasedCodes, 0, length);
            warningCodes[index] = increasedCodes;
            warningCodes[index][length] = warningFunctionalEquivalenceCode;
            
            int[] increasedCount = new int[length+1];
            System.arraycopy(warningsCount[index], 0, increasedCount, 0, length);
            warningsCount[index] = increasedCount;            
            warningsCount[index][length] = 1;
        }else{
            index = warningIds.size();
            warningIds.add(warningId);
                                    
            if(index == warningCodes.length){
                int size = index+1;
                int[][] increasedCodes = new int[size][];
                System.arraycopy(warningCodes, 0, increasedCodes, 0, index);
                warningCodes = increasedCodes;
                                
                int[][] increasedCount = new int[size][];
                System.arraycopy(warningsCount, 0, increasedCount, 0, index);
                warningsCount = increasedCount;
            }
            warningCodes[index] = new int[1];
            warningCodes[index][0] = warningFunctionalEquivalenceCode;
            
            warningsCount[index] = new int[1];
            warningsCount[index][0] = 1;
        }
    }    
        
    /*boolean mustRecordErrorMessage(int errorId, int errorFunctionalEquivalenceCode, int candidateIndex){
        return candidateIndex == getErrorRecordingIndex(errorId, errorFunctionalEquivalenceCode, candidateIndex);
    }
    
    boolean mustRecordWarningMessage(int warningId, int warningFunctionalEquivalenceCode, int candidateIndex){
        return candidateIndex == getWarningRecordingIndex(warningId, warningFunctionalEquivalenceCode, candidateIndex);
    }*/
    
    /*int getErrorRecordingIndex(int errorId, int errorFunctionalEquivalenceCode, int candidateIndex){        
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
    }*/
    
    /*void setErrorMessageRecorded(int errorId, int errorFunctionalEquivalenceCode){
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
    }*/
    
    /*void setWarningMessageRecorded(int warningId, int warningFunctionalEquivalenceCode){
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
    }*/
    
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
		/*if(mustRecordErrorMessage(UNKNOWN_ELEMENT, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unknownElement(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
            /*setErrorMessageRecorded(UNKNOWN_ELEMENT, functionalEquivalenceCode);
        }*/
	}	
	
	public void unexpectedElement(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_ELEMENT, functionalEquivalenceCode, candidateIndex);
		/*if(mustRecordErrorMessage(UNEXPECTED_ELEMENT, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unexpectedElement(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            /*setErrorMessageRecorded(UNEXPECTED_ELEMENT, functionalEquivalenceCode);
        }*/
	}	
    
    
	public void unexpectedAmbiguousElement(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_AMBIGUOUS_ELEMENT, functionalEquivalenceCode, candidateIndex);     
		/*if(mustRecordErrorMessage(UNEXPECTED_AMBIGUOUS_ELEMENT, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unexpectedAmbiguousElement(functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
            /*setErrorMessageRecorded(UNEXPECTED_AMBIGUOUS_ELEMENT, functionalEquivalenceCode);
        }*/
	}		
	
	
	public void unknownAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber){
        recordError(UNKNOWN_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);
		/*if(mustRecordErrorMessage(UNKNOWN_ATTRIBUTE, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unknownAttribute(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber);
            /*setErrorMessageRecorded(UNKNOWN_ATTRIBUTE, functionalEquivalenceCode);
        }*/
	}
	
	public void unexpectedAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){	    
        recordError(UNEXPECTED_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);        
		/*if(mustRecordErrorMessage(UNEXPECTED_ATTRIBUTE, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unexpectedAttribute(functionalEquivalenceCode, qName, definition, systemId, lineNumber, columnNumber);
            /*setErrorMessageRecorded(UNEXPECTED_ATTRIBUTE, functionalEquivalenceCode);
        }*/
	}
	
	
	public void unexpectedAmbiguousAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_AMBIGUOUS_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(UNEXPECTED_AMBIGUOUS_ATTRIBUTE, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unexpectedAmbiguousAttribute(functionalEquivalenceCode, qName, possibleDefinitions, systemId, lineNumber, columnNumber);
            /*setErrorMessageRecorded(UNEXPECTED_AMBIGUOUS_ATTRIBUTE, functionalEquivalenceCode);
        }*/
	}
	
	public void misplacedContent(int candidateIndex, int functionalEquivalenceCode, 
                                            APattern contextDefinition, 
											String startSystemId, 
											int startLineNumber, 
											int startColumnNumber, 
											APattern definition, 
											int itemId, 
											String qName, 
											String systemId, 
											int lineNumber, 
											int columnNumber,
											APattern sourceDefinition, 
											APattern reper){//not stored, only used for internal conflict handling
        recordError(MISPLACED_ELEMENT, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordErrorMessage(MISPLACED_ELEMENT, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.misplacedContent(functionalEquivalenceCode,
                                                        contextDefinition, 
                                                        startSystemId, 
                                                        startLineNumber, 
                                                        startColumnNumber, 
                                                        definition,
                                                        itemId, 
                                                        qName, 
                                                        systemId, 
                                                        lineNumber, 
                                                        columnNumber,
                                                        sourceDefinition, 
                                                        reper);*/
            /*setErrorMessageRecorded(MISPLACED_ELEMENT, functionalEquivalenceCode);
        }*/
	}
    public void misplacedContent(int candidateIndex, int functionalEquivalenceCode, 
                                            APattern contextDefinition, 
											String startSystemId, 
											int startLineNumber, 
											int startColumnNumber, 
											APattern definition,
											int[] itemId, 
											String[] qName, 
											String[] systemId, 
											int[] lineNumber, 
											int[] columnNumber,
											APattern[] sourceDefinition, 
											APattern reper){//not stored, only used for internal conflict handling
        recordError(MISPLACED_ELEMENT, functionalEquivalenceCode, candidateIndex);		        
        /*if(mustRecordErrorMessage(MISPLACED_ELEMENT, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.misplacedContent(functionalEquivalenceCode,
                                                            contextDefinition, 
                                                            startSystemId, 
                                                            startLineNumber, 
                                                            startColumnNumber, 
                                                            definition,
                                                            itemId, 
                                                            qName, 
                                                            systemId, 
                                                            lineNumber, 
                                                            columnNumber,
                                                            sourceDefinition, 
                                                            reper);*/
            /*setErrorMessageRecorded(MISPLACED_ELEMENT, functionalEquivalenceCode);
        }*/
	}
			
	
	public void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                    Rule context,
									String startSystemId,
									int startLineNumber,
									int startColumnNumber,
									APattern definition, 
									int[] itemId, 
									String[] qName, 
									String[] systemId, 
									int[] lineNumber, 
									int[] columnNumber){
        recordError(EXCESSIVE_CONTENT, functionalEquivalenceCode, candidateIndex);                     
	    /*if(mustRecordErrorMessage(EXCESSIVE_CONTENT, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.excessiveContent(functionalEquivalenceCode,
                                                            context,
                                                            startSystemId,
                                                            startLineNumber,
                                                            startColumnNumber,
                                                            definition,
                                                            itemId, 
                                                            qName, 
                                                            systemId, 
                                                            lineNumber, 
                                                            columnNumber);*/
            /*setErrorMessageRecorded(EXCESSIVE_CONTENT, functionalEquivalenceCode);
        }*/
	}   
	public void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                Rule context, 
								APattern definition,
								int itemId, 
								String qName, 
								String systemId, 
								int lineNumber,		
								int columnNumber){
		//if(recordError(EXCESSIVE_CONTENT, candidateIndex))
        /*try{
        localMessageHandler.excessiveContent(functionalEquivalenceCode, 
                                                                context, 
                                                                definition,
                                                                itemId, 
                                                                qName, 
                                                                systemId, 
                                                                lineNumber,		
                                                                columnNumber);
        }catch(IllegalArgumentException e){
            if(!conflictHandler.isDisqualified(candidateIndex)){
                throw new IllegalStateException(e);
            }
        }*/
	}
    
    
	public void unresolvedAmbiguousElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        recordError(UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordErrorMessage(UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.unresolvedAmbiguousElementContentError(functionalEquivalenceCode,
                                                                qName, 
                                                                systemId, 
                                                                lineNumber, 
                                                                columnNumber, 
                                                                possibleDefinitions);*/
            /*setErrorMessageRecorded(UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode);
        }*/
	}
	
	public void unresolvedUnresolvedElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        recordError(UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordErrorMessage(UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.unresolvedUnresolvedElementContentError(functionalEquivalenceCode,
                                                                qName, 
                                                                systemId, 
                                                                lineNumber, 
                                                                columnNumber, 
                                                                possibleDefinitions);*/
            /*setErrorMessageRecorded(UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode);
        }*/
	}
    
    
	public void unresolvedAttributeContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions){
        recordError(UNRESOLVED_ATTRIBUTE_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordErrorMessage(UNRESOLVED_ATTRIBUTE_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.unresolvedAttributeContentError(functionalEquivalenceCode,
                                                                            qName, 
                                                                            systemId, 
                                                                            lineNumber, 
                                                                            columnNumber, 
                                                                            possibleDefinitions);*/
            /*setErrorMessageRecorded(UNRESOLVED_ATTRIBUTE_CONTENT_ERROR, functionalEquivalenceCode);
        }*/
	}

    public void ambiguousUnresolvedElementContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        recordWarning(AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordWarningMessage(AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.ambiguousUnresolvedElementContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setWarningMessageRecorded(AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode);
        }*/
    }
    
    public void ambiguousAmbiguousElementContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        recordWarning(AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordWarningMessage(AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.ambiguousAmbiguousElementContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setWarningMessageRecorded(AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode);
        }*/
    }
    
	public void ambiguousAttributeContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
        recordWarning(AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordWarningMessage(AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.ambiguousAttributeContentWarning(functionalEquivalenceCode, qName, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setWarningMessageRecorded(AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING, functionalEquivalenceCode);
        }*/
    }
    
	public void ambiguousCharacterContentWarning(int candidateIndex, int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordWarning(AMBIGUOUS_CHARACTER_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordWarningMessage(AMBIGUOUS_CHARACTER_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.ambiguousCharacterContentWarning(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setWarningMessageRecorded(AMBIGUOUS_CHARACTER_CONTENT_WARNING, functionalEquivalenceCode);
        }*/
    }

    public void ambiguousAttributeValueWarning(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordWarning(AMBIGUOUS_ATTRIBUTE_VALUE_WARNING, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordWarningMessage(AMBIGUOUS_ATTRIBUTE_VALUE_WARNING, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.ambiguousAttributeValueWarning(functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setWarningMessageRecorded(AMBIGUOUS_ATTRIBUTE_VALUE_WARNING, functionalEquivalenceCode);
        }*/
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
	    //if(definition.getQName().equals("name attribute with QName value"))throw new IllegalStateException();
        recordMissingContentError(functionalEquivalenceCode, candidateIndex, definition);
        /*if(mustRecordErrorMessage(MISSING_CONTENT, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.missingContent(functionalEquivalenceCode,
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
                                                                columnNumber);*/
            /*setErrorMessageRecorded(MISSING_CONTENT, functionalEquivalenceCode);
        }*/
    }
    
	public void illegalContent(int candidateIndex, int functionalEquivalenceCode, 
                            Rule context, 
                            int startItemId, 
							String startQName, 
							String startSystemId, 
							int startLineNumber, 
							int startColumnNumber){
        recordError(ILLEGAL_CONTENT, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordErrorMessage(ILLEGAL_CONTENT, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.illegalContent(functionalEquivalenceCode,
                                                                context, 
                                                                startItemId, 
                                                                startQName, 
                                                                startSystemId, 
                                                                startLineNumber, 
                                                                startColumnNumber);*/
            /*setErrorMessageRecorded(ILLEGAL_CONTENT, functionalEquivalenceCode);
        }*/
	}
        
	public void undeterminedByContent(int candidateIndex, int functionalEquivalenceCode, String qName, String candidateDelayedMessages){
        recordError(UNDETERMINED_BY_CONTENT, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(UNDETERMINED_BY_CONTENT, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.undeterminedByContent(functionalEquivalenceCode, qName, candidateDelayedMessages);
            /*setErrorMessageRecorded(UNDETERMINED_BY_CONTENT, functionalEquivalenceCode);
        }*/
	}
	
    // {15}
	public void characterContentDatatypeError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){	    
        recordError(CHARACTER_CONTENT_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(CHARACTER_CONTENT_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.characterContentDatatypeError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            /*setErrorMessageRecorded(CHARACTER_CONTENT_DATATYPE_ERROR, functionalEquivalenceCode);
        }*/
	}
        
    //{16}
	public void attributeValueDatatypeError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        recordError(ATTRIBUTE_VALUE_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(ATTRIBUTE_VALUE_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.attributeValueDatatypeError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            /*setErrorMessageRecorded(ATTRIBUTE_VALUE_DATATYPE_ERROR, functionalEquivalenceCode);
        }*/
	}
        
        
	public void characterContentValueError(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(CHARACTER_CONTENT_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(CHARACTER_CONTENT_VALUE_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.characterContentValueError(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            /*setErrorMessageRecorded(CHARACTER_CONTENT_VALUE_ERROR, functionalEquivalenceCode);
        }*/
	}
    
	public void attributeValueValueError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(ATTRIBUTE_VALUE_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(ATTRIBUTE_VALUE_VALUE_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.attributeValueValueError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            /*setErrorMessageRecorded(ATTRIBUTE_VALUE_VALUE_ERROR, functionalEquivalenceCode);
        }*/
	}
    
    
	public void characterContentExceptedError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(CHARACTER_CONTENT_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(CHARACTER_CONTENT_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.characterContentExceptedError(functionalEquivalenceCode, elementQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            /*setErrorMessageRecorded(CHARACTER_CONTENT_EXCEPTED_ERROR, functionalEquivalenceCode);
        }*/
	}
    
	public void attributeValueExceptedError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(ATTRIBUTE_VALUE_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(ATTRIBUTE_VALUE_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.attributeValueExceptedError(functionalEquivalenceCode, attributeQName, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            /*setErrorMessageRecorded(ATTRIBUTE_VALUE_EXCEPTED_ERROR, functionalEquivalenceCode);
        }*/
	}
    
	public void unexpectedCharacterContent(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
        recordError(UNEXPECTED_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(UNEXPECTED_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unexpectedCharacterContent(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, elementDefinition);
            /*setErrorMessageRecorded(UNEXPECTED_CHARACTER_CONTENT, functionalEquivalenceCode);
        }*/
	}
    
	public void unexpectedAttributeValue(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        recordError(UNEXPECTED_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(UNEXPECTED_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unexpectedAttributeValue(functionalEquivalenceCode, charsSystemId, charsLineNumber, columnNumber, attributeDefinition);
            /*setErrorMessageRecorded(UNEXPECTED_ATTRIBUTE_VALUE, functionalEquivalenceCode);
        }*/
	}
    
	public void unresolvedCharacterContent(int candidateIndex, int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(UNRESOLVED_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(UNRESOLVED_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unresolvedCharacterContent(functionalEquivalenceCode, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setErrorMessageRecorded(UNRESOLVED_CHARACTER_CONTENT, functionalEquivalenceCode);
        }*/
	}
    
	// {24}
	public void unresolvedAttributeValue(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(UNRESOLVED_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(UNRESOLVED_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unresolvedAttributeValue(functionalEquivalenceCode, attributeQName, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setErrorMessageRecorded(UNRESOLVED_ATTRIBUTE_VALUE, functionalEquivalenceCode);
        }*/
	}        
    
    
    // {25}
	public void listTokenDatatypeError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        recordError(LIST_TOKEN_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordErrorMessage(LIST_TOKEN_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.listTokenDatatypeError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition, datatypeErrorMessage);
            /*setErrorMessageRecorded(LIST_TOKEN_DATATYPE_ERROR, functionalEquivalenceCode);
        }*/
	}
    
        
	public void listTokenValueError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(LIST_TOKEN_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);
	    /*if(mustRecordErrorMessage(LIST_TOKEN_VALUE_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.listTokenValueError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            /*setErrorMessageRecorded(LIST_TOKEN_VALUE_ERROR, functionalEquivalenceCode);
        }*/
	}        
    
	public void listTokenExceptedError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(LIST_TOKEN_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);
		/*if(mustRecordErrorMessage(LIST_TOKEN_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.listTokenExceptedError(functionalEquivalenceCode, token, charsSystemId, charsLineNumber, columnNumber, charsDefinition);
            /*setErrorMessageRecorded(LIST_TOKEN_EXCEPTED_ERROR, functionalEquivalenceCode);
        }*/
	}
    
    	    
    public void unresolvedListTokenInContextError(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(UNRESOLVED_LIST_TOKEN_IN_CONTEXT_ERROR, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordErrorMessage(UNRESOLVED_LIST_TOKEN_IN_CONTEXT_ERROR, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.unresolvedListTokenInContextError(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setErrorMessageRecorded(UNRESOLVED_LIST_TOKEN_IN_CONTEXT_ERROR, functionalEquivalenceCode);
        }*/
    }
    public void ambiguousListTokenInContextWarning(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordWarning(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING, functionalEquivalenceCode, candidateIndex);
        /*if(mustRecordWarningMessage(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.ambiguousListTokenInContextWarning(functionalEquivalenceCode, token, systemId, lineNumber, columnNumber, possibleDefinitions);
            /*setWarningMessageRecorded(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING, functionalEquivalenceCode);
        }*/
    }
    
    public  void conflict(int candidateIndex, int functionalEquivalenceCode, int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
        recordError(CONFLICT, functionalEquivalenceCode, candidateIndex);        
        /*if(mustRecordErrorMessage(CONFLICT, functionalEquivalenceCode, candidateIndex)){*/
            //localMessageHandler.conflict(functionalEquivalenceCode, conflictResolutionId, commonMessages, candidatesCount, disqualified, candidateMessages);
            /*setErrorMessageRecorded(CONFLICT, functionalEquivalenceCode);
        }*/
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
        /*if(mustRecordErrorMessage(MISSING_COMPOSITOR_CONTENT, functionalEquivalenceCode, candidateIndex)){*/
            /*localMessageHandler.missingCompositorContent(functionalEquivalenceCode,
                                                                        context, 
                                                                        startSystemId, 
                                                                        startLineNumber, 
                                                                        startColumnNumber,								 
                                                                        definition, 
                                                                        expected, 
                                                                        found);*/
            /*setErrorMessageRecorded(MISSING_COMPOSITOR_CONTENT, functionalEquivalenceCode);
        }*/
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
        /*Arrays.fill(recordedErrorMessages, null);*/
        
        conflictHandler.clear();
        if(candidateMessageHandlers != null) candidateMessageHandlers = null;
        candidatesCount = -1;
        candidateDelayedMessages = null;
        commonMessages = null;
        allDisqualified = false;
        hasDelayedMessages = false;
        candidatesCommonMessages = null;
    }	
}
