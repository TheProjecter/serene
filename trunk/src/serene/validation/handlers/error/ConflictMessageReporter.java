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

import java.util.BitSet;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import serene.validation.handlers.conflict.ElementConflictResolver;

import serene.validation.schema.active.components.AElement;

import serene.util.IntList;

public class ConflictMessageReporter extends AbstractMessageReporter{
    
    MessageReporter commonMessages; // It can be only one because it is about this context.
    int candidatesCount;
    BitSet disqualified;
    MessageReporter[] candidateMessages;
    //boolean restrictToFileName;
    ErrorDispatcher errorDispatcher;
    
    int conflictInternalResolutionId;
    BitSet qualified;
    int winnerIndex;
    AElement winnerDefinition;
    
    boolean isMessageRetrieved;
    boolean isDiscarded;
    
    int clientCount;
    
    public ConflictMessageReporter(MessageReporter parent,
                                    int reportingContextType,
                                    String reportingContextQName,
                                    AElement reportingContextDefinition,
                                    String reportingContextPublicId,
                                    String reportingContextSystemId,
                                    int reportingContextLineNumber,
                                    int reportingContextColumnNumber,
                                    int conflictResolutionId,
                                    boolean restrictToFileName,
                                    MessageReporter commonMessages,
                                    int candidatesCount,
                                    BitSet disqualified,
                                    MessageReporter[] candidateMessages,
                                    ErrorDispatcher errorDispatcher){
        super();
        
        this.parent = parent;
        this.reportingContextType = reportingContextType;
        this.reportingContextQName = reportingContextQName;
        this.reportingContextDefinition = reportingContextDefinition;
        this.reportingContextPublicId = reportingContextPublicId;
        this.reportingContextSystemId = reportingContextSystemId;
        this.reportingContextLineNumber = reportingContextLineNumber;
        this.reportingContextColumnNumber = reportingContextColumnNumber;
        this.conflictResolutionId = conflictResolutionId;
        this.restrictToFileName = restrictToFileName;
        this.commonMessages = commonMessages;
        this.candidatesCount = candidatesCount;
        this.disqualified = disqualified;
        this.candidateMessages = candidateMessages;
        
        this.candidatesCount = candidatesCount;
        this.commonMessages = commonMessages;
        this.disqualified = disqualified;
        this.candidateMessages = candidateMessages;
        
        this.errorDispatcher = errorDispatcher;
        
        isMessageRetrieved = false;
        isDiscarded = false;
        
        clientCount = 0;
        
        if(commonMessages != null)commonMessages.registerClient(this);
        if(candidateMessages != null){
            for(int i = 0; i < candidateMessages.length; i++){
                if(candidateMessages[i] != null)candidateMessages[i].registerClient(this);
            }
        }
        
    }
        
    public ConflictMessageReporter getConflictMessageReporter(ErrorDispatcher errorDispatcher){
        return new ConflictMessageReporter(parent,
                                    reportingContextType,
                                    reportingContextQName,
                                    reportingContextDefinition,
                                    reportingContextPublicId,
                                    reportingContextSystemId,
                                    reportingContextLineNumber,
                                    reportingContextColumnNumber,
                                    conflictResolutionId,
                                    restrictToFileName,
                                    commonMessages,
                                    candidatesCount,
                                    disqualified,
                                    candidateMessages,
                                    errorDispatcher);
    }
    
    public void setDiscarded(boolean isDiscarded){
        this.isDiscarded = isDiscarded;    
        
        /*if(commonMessages != null)commonMessages.setDiscarded(isDiscarded);
        
        if(candidateMessages!= null){
            for(int i = 0; i < candidateMessages.length; i++){
                if(candidateMessages[i] != null){
                    candidateMessages[i].setDiscarded(isDiscarded);
                }
            }
        }
        if(parent != null){
            parent.setDiscarded(isDiscarded);
        }*/
    }
    
    public boolean containsErrorMessage(){
        if(commonMessages != null && commonMessages.containsErrorMessage())return true; 
	    if(disqualified != null && disqualified.cardinality() == candidatesCount){
	        return true;
	    }else if(disqualified != null){
	        for(int i = 0; i < candidatesCount; i++){
	            if(!disqualified.get(i)
	                && candidateMessages[i] != null
	                && candidateMessages[i].containsErrorMessage())return true;
	        }
	    }
	        
	    if(parent != null)return parent.containsErrorMessage();
	    return false;
    }
    
    public boolean containsOtherErrorMessage(IntList exceptedErrorIds, IntList exceptedErrorCodes){
        // TODO review!!!
        if(commonMessages != null && commonMessages.containsErrorMessage())return true; 
	    if(disqualified != null && disqualified.cardinality() == candidatesCount){
	        return true;
	    }else if(disqualified != null){
	        for(int i = 0; i < candidatesCount; i++){
	            if(!disqualified.get(i)
	                && candidateMessages[i] != null
	                && candidateMessages[i].containsErrorMessage())return true;
	        }
	    }
	        
	    if(parent != null)return parent.containsErrorMessage();
	    return false;
    }
    
    public void setConflictInternalResolution(int  conflictInternalResolutionId){
        this.conflictInternalResolutionId = conflictInternalResolutionId;
    }
    
    public void setConflictInternalResolution(int  conflictInternalResolutionId, BitSet qualified){
        this.conflictInternalResolutionId = conflictInternalResolutionId;
        this.qualified = qualified; 
    }
    
    public void setConflictInternalResolution(int  conflictInternalResolutionId, int winnerIndex, AElement winnerDefinition){
        this.conflictInternalResolutionId = conflictInternalResolutionId;
        this.winnerIndex = winnerIndex;
        this.winnerDefinition = winnerDefinition;
    }
    
    void report() throws SAXException{
        isMessageRetrieved = true;
        if(conflictInternalResolutionId == UNRESOLVED){
            if(parent != null){
                parent.report(restrictToFileName, null, errorDispatcher, "");//parent should have been located, else illegal state
            }
            
            handleConflict(reportingContextType, reportingContextQName, restrictToFileName, null, errorDispatcher, "");
            
            // report common as any other errors            
            if(commonMessages != null){
                commonMessages.report(restrictToFileName, null, errorDispatcher, "");                
            }
        }else if(conflictInternalResolutionId == AMBIGUOUS){
            if(candidateMessages != null){
                for(int i = 0; i < candidateMessages.length; i++){
                    if(!qualified.get(i) && candidateMessages[i] != null){
                        /*candidateMessages[i].setDiscarded(true);*/
                        candidateMessages[i].unregisterClient(this);
                        candidateMessages[i] = null;
                    }
                }
            }
            
            if(parent != null){
                parent.report(restrictToFileName, null, errorDispatcher, "");//parent should have been located, else illegal state
            }      
              
            handleConflict(reportingContextType, reportingContextQName, restrictToFileName, null, errorDispatcher, "");
            
            // report common as any other errors            
            if(commonMessages != null){
                commonMessages.report(restrictToFileName, null, errorDispatcher, "");                
            }
        }else if(conflictInternalResolutionId == RESOLVED){
            if(candidateMessages[winnerIndex] != null){
                candidateMessages[winnerIndex].report(reportingContextType, reportingContextQName, winnerDefinition, restrictToFileName, null, errorDispatcher);
            }
                                
            if(commonMessages != null){
                commonMessages.report(reportingContextType, reportingContextQName, winnerDefinition, restrictToFileName, null, errorDispatcher);
            }
        }
    }
    
    
    public void report(int reportingContextType, String reportingContextQName, AElement reportingContextDefinition, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher) throws SAXException{
        isMessageRetrieved = true;
        
        this.reportingContextType = reportingContextType;
        this.reportingContextQName = reportingContextQName;
        this.reportingContextDefinition = reportingContextDefinition;
        
        if(parent != null){
            parent.report(restrictToFileName, locator, errorDispatcher, "");//parent should have been located, else illegal state
        }
               
          
        handleConflict(reportingContextType, reportingContextQName, restrictToFileName, locator, errorDispatcher, "");
        
        // report common as any other errors            
        if(commonMessages != null){
            commonMessages.report(restrictToFileName, locator, errorDispatcher, "");                
        }
    }
    
    private void handleConflict(int reportingContextType, String reportingContextQName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualified = candidatesCount - disqualified.cardinality();
        if(qualified == 0){
            reportUnresolved(reportingContextType, reportingContextQName, restrictToFileName, locator, errorDispatcher, prefix);
        }else if(qualified == 1){
            reportResolved(reportingContextType, reportingContextQName, restrictToFileName, locator, errorDispatcher, prefix);
        }else{
            reportAmbiguous(reportingContextType, reportingContextQName, restrictToFileName, locator, errorDispatcher, prefix);
        }
    }
    
    private void reportUnresolved(int reportingContextType, String reportingContextQName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            if(candidateMessages[i] != null){
                message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);
            }
        }
        if(!message.equals("")){
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Element <"+reportingContextQName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Element <"+reportingContextQName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
        }else throw new IllegalStateException();
    }
    
    private void reportResolved(int reportingContextType, String reportingContextQName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualifiedIndex = disqualified.nextClearBit(0);
        if(candidateMessages != null && candidateMessages[qualifiedIndex] != null) candidateMessages[qualifiedIndex].report(restrictToFileName, locator, errorDispatcher, prefix);        
    }
    
    private void reportAmbiguous(int reportingContextType, String reportingContextQName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            if(!disqualified.get(i) && candidateMessages != null && candidateMessages[i] != null){
                 message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);            
            }
        }
        if(!message.equals("")){
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+reportingContextQName+"> contain errors in their subtrees:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+reportingContextQName+"> contain errors in their subtrees:"+message, reportingContextPublicId, reportingContextSystemId, reportingContextLineNumber, reportingContextColumnNumber));
        }        
    }
    
    public String getCandidateErrorMessage(String prefix, boolean restrictToFileName){
        throw new IllegalStateException();
    }
    
    public String getErrorMessage(String prefix, boolean restrictToFileName){
        throw new IllegalStateException();
    }
    
    public void report(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        report();
    }
    
    public void registerClient(MessageReporter mr){
        clientCount++;
    }
    public void unregisterClient(MessageReporter mr){
        clientCount--;
        if(clientCount == 0){
            isDiscarded = true;// just in case
            clear();
        }
    }
    public void clear(ContextErrorHandler ec){
        if(clientCount > 0) return;
        clear();
    }
    public void clear(CandidatesConflictErrorHandler cceh){
        if(clientCount > 0) return;
        clear();
    }
    public void clear(TemporaryMessageStorage tms){
        if(clientCount > 0) return;
        clear();
    }
    public void clear(ElementConflictResolver ecr){
        if(clientCount > 0) return;
        clear();
    }
    private void clear(){
        if(commonMessages != null){
            /*if(isMessageRetrieved || isDiscarded)*/commonMessages.unregisterClient(this); // It can be only one because it is about this context.
            commonMessages = null;
        }
        
        candidatesCount = 0;
        
        disqualified = null;
        
        if(candidateMessages != null){
            /*if(isMessageRetrieved || isDiscarded){*/
                for(MessageReporter cm : candidateMessages){                    
                    if(cm != null){
                        cm.unregisterClient(this);
                    }
                }
            /*}*/
            candidateMessages = null;
        }
             
        if(/*(isMessageRetrieved || isDiscarded) &&*/ parent != null) {
            parent.unregisterClient(this);
            parent = null;
        }
        
        qualified = null;
        winnerIndex = -1;
        winnerDefinition = null;
    }
    
    
    public String toString(){
        return "ConflictMessageReporter ";
    }
}
