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
        
        warningIds = new IntList();
        warningCodes = new int[4][];
        warningsCount = new int[4][];
        
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
            }
            if(!commonWarningIds.isEmpty()){
                transferAllCommonWarnings();
            }
            return;
        }
        
        if(candidateDelayedMessages == null){           
            if(!commonErrorIds.isEmpty()
                || !commonWarningIds.isEmpty()){
                throw new IllegalStateException();
            }           
            return;
        }
        
        if(qualifiedCount == 1){
            int winnerIndex = conflictHandler.getNextQualified(0);
            if(candidateDelayedMessages[winnerIndex] != null) hasDelayedMessages = true;
            for(int i = 0; i < candidateDelayedMessages.length; i++){
                if(i != winnerIndex && candidateDelayedMessages[i] != null) candidateDelayedMessages[i] = null;
            }
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
            }
            if(!commonWarningIds.isEmpty()){
                transferQualifiedCommonWarnings();
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
            }
            if(!commonWarningIds.isEmpty()){
                transferQualifiedCommonWarnings();
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
	}	
	
	public void unexpectedElement(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_ELEMENT, functionalEquivalenceCode, candidateIndex);
	}	
    
    
	public void unexpectedAmbiguousElement(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_AMBIGUOUS_ELEMENT, functionalEquivalenceCode, candidateIndex);
	}		
	
	
	public void unknownAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber){
        recordError(UNKNOWN_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);
	}
	
	public void unexpectedAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent definition, String systemId, int lineNumber, int columnNumber){	    
        recordError(UNEXPECTED_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);
	}
	
	
	public void unexpectedAmbiguousAttribute(int candidateIndex, int functionalEquivalenceCode, String qName, SimplifiedComponent[] possibleDefinitions, String systemId, int lineNumber, int columnNumber){
        recordError(UNEXPECTED_AMBIGUOUS_ATTRIBUTE, functionalEquivalenceCode, candidateIndex);
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
	}   
	public void excessiveContent(int candidateIndex, int functionalEquivalenceCode, 
                                Rule context, 
								APattern definition,
								int itemId, 
								String qName, 
								String systemId, 
								int lineNumber,		
								int columnNumber){
	}
    
    
	public void unresolvedAmbiguousElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        recordError(UNRESOLVED_AMBIGUOUS_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);
	}
	
	public void unresolvedUnresolvedElementContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
        recordError(UNRESOLVED_UNRESOLVED_ELEMENT_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);        
	}
    
    
	public void unresolvedAttributeContentError(int candidateIndex, int functionalEquivalenceCode, 
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AAttribute[] possibleDefinitions){
        recordError(UNRESOLVED_ATTRIBUTE_CONTENT_ERROR, functionalEquivalenceCode, candidateIndex);        
	}

    public void ambiguousUnresolvedElementContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        recordWarning(AMBIGUOUS_UNRESOLVED_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);        
    }
    
    public void ambiguousAmbiguousElementContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        recordWarning(AMBIGUOUS_AMBIGUOUS_ELEMENT_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);        
    }
    
	public void ambiguousAttributeContentWarning(int candidateIndex, int functionalEquivalenceCode, String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
        recordWarning(AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);        
    }
    
	public void ambiguousCharacterContentWarning(int candidateIndex, int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordWarning(AMBIGUOUS_CHARACTER_CONTENT_WARNING, functionalEquivalenceCode, candidateIndex);        
    }

    public void ambiguousAttributeValueWarning(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordWarning(AMBIGUOUS_ATTRIBUTE_VALUE_WARNING, functionalEquivalenceCode, candidateIndex);        
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
    }
    
	public void illegalContent(int candidateIndex, int functionalEquivalenceCode, 
                            Rule context, 
                            int startItemId, 
							String startQName, 
							String startSystemId, 
							int startLineNumber, 
							int startColumnNumber){
        recordError(ILLEGAL_CONTENT, functionalEquivalenceCode, candidateIndex);        
	}
    
    // {15}
	public void characterContentDatatypeError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){	    
        recordError(CHARACTER_CONTENT_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);	    
	}
        
    //{16}
	public void attributeValueDatatypeError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        recordError(ATTRIBUTE_VALUE_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);	    
	}
        
        
	public void characterContentValueError(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(CHARACTER_CONTENT_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);	    
	}
    
	public void attributeValueValueError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(ATTRIBUTE_VALUE_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);	    
	}
    
    
	public void characterContentExceptedError(int candidateIndex, int functionalEquivalenceCode, String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(CHARACTER_CONTENT_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);	    
	}
    
	public void attributeValueExceptedError(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(ATTRIBUTE_VALUE_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);	    
	}
    
	public void unexpectedCharacterContent(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
        recordError(UNEXPECTED_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex);	    
	}
    
	public void unexpectedAttributeValue(int candidateIndex, int functionalEquivalenceCode, String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        recordError(UNEXPECTED_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex);	    
	}
    
	public void unresolvedCharacterContent(int candidateIndex, int functionalEquivalenceCode, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(UNRESOLVED_CHARACTER_CONTENT, functionalEquivalenceCode, candidateIndex);	    
	}
    
	// {24}
	public void unresolvedAttributeValue(int candidateIndex, int functionalEquivalenceCode, String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(UNRESOLVED_ATTRIBUTE_VALUE, functionalEquivalenceCode, candidateIndex);	    
	}        
    
    
    // {25}
	public void listTokenDatatypeError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
        recordError(LIST_TOKEN_DATATYPE_ERROR, functionalEquivalenceCode, candidateIndex);        
	}
    
        
	public void listTokenValueError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
        recordError(LIST_TOKEN_VALUE_ERROR, functionalEquivalenceCode, candidateIndex);	    
	}        
    
	public void listTokenExceptedError(int candidateIndex, int functionalEquivalenceCode, String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
        recordError(LIST_TOKEN_EXCEPTED_ERROR, functionalEquivalenceCode, candidateIndex);		
	}
    
    	    
    public void unresolvedListTokenInContextError(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordError(UNRESOLVED_LIST_TOKEN_IN_CONTEXT_ERROR, functionalEquivalenceCode, candidateIndex);        
    }
    public void ambiguousListTokenInContextWarning(int candidateIndex, int functionalEquivalenceCode, String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        recordWarning(AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING, functionalEquivalenceCode, candidateIndex);        
    }
    
    public  void conflict(int candidateIndex, int functionalEquivalenceCode, int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter[] candidateMessages){
        recordError(CONFLICT, functionalEquivalenceCode, candidateIndex);
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
	}


    public void clear(){
        Arrays.fill(errorCodes, null);
        Arrays.fill(errorCandidates, null);
        
        conflictHandler.clear();
        if(candidateMessageHandlers != null) candidateMessageHandlers = null;
        candidatesCount = -1;
        candidateDelayedMessages = null;
        commonMessages = null;
        allDisqualified = false;
        hasDelayedMessages = false;
        candidatesCommonMessages = null;
                
        commonErrorIds.clear();
        commonErrorCodes.clear();
    
        commonWarningIds.clear();
        commonWarningCodes.clear();
    }	
}
