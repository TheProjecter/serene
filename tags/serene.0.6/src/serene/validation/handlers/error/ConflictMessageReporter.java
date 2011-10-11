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


import serene.validation.schema.active.components.AElement;

import serene.util.IntList;

import sereneWrite.MessageWriter;

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
    
    public ConflictMessageReporter(MessageReporter parent,
                                    int contextType,
                                    String qName,
                                    AElement definition,
                                    String publicId,
                                    String systemId,
                                    int lineNumber,
                                    int columnNumber,
                                    int conflictResolutionId,
                                    boolean restrictToFileName,
                                    MessageReporter commonMessages,
                                    int candidatesCount,
                                    BitSet disqualified,
                                    MessageReporter[] candidateMessages,
                                    ErrorDispatcher errorDispatcher,                                    
                                    MessageWriter debugWriter){
        super(debugWriter);
        
        this.parent = parent;
        this.contextType = contextType;
        this.qName = qName;
        this.definition = definition;
        this.publicId = publicId;
        this.systemId = systemId;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
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
    }
        
    public ConflictMessageReporter getConflictMessageReporter(ErrorDispatcher errorDispatcher){
        return new ConflictMessageReporter(parent,
                                    contextType,
                                    qName,
                                    definition,
                                    publicId, 
                                    systemId,
                                    lineNumber,
                                    columnNumber,
                                    conflictResolutionId,
                                    restrictToFileName,
                                    commonMessages,
                                    candidatesCount,
                                    disqualified,
                                    candidateMessages,
                                    errorDispatcher,                                    
                                    debugWriter);
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
        if(conflictInternalResolutionId == UNRESOLVED){
            if(parent != null){
                parent.report(restrictToFileName, null, errorDispatcher, "");//parent should have been located, else illegal state
            }
            
            handleConflict(contextType, qName, restrictToFileName, null, errorDispatcher, "");
            
            // report common as any other errors            
            if(commonMessages != null){
                commonMessages.report(restrictToFileName, null, errorDispatcher, "");                
            }
        }else if(conflictInternalResolutionId == AMBIGUOUS){
            if(candidateMessages != null){
                for(int i = 0; i < candidateMessages.length; i++){
                    if(!qualified.get(i))candidateMessages[i] = null;
                }
            }
            
            if(parent != null){
                parent.report(restrictToFileName, null, errorDispatcher, "");//parent should have been located, else illegal state
            }      
              
            handleConflict(contextType, qName, restrictToFileName, null, errorDispatcher, "");
            
            // report common as any other errors            
            if(commonMessages != null){
                commonMessages.report(restrictToFileName, null, errorDispatcher, "");                
            }
        }else if(conflictInternalResolutionId == RESOLVED){
            if(candidateMessages[winnerIndex] != null){
                candidateMessages[winnerIndex].report(contextType, qName, winnerDefinition, restrictToFileName, null, errorDispatcher);
            }
                                
            if(commonMessages != null){
                commonMessages.report(contextType, qName, winnerDefinition, restrictToFileName, null, errorDispatcher);
            }
        }
    }
    
    
    public void report(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher) throws SAXException{
        this.contextType = contextType;
        this.qName = qName;
        this.definition = definition;
        
        if(parent != null){
            // System.out.println("REPORT PARENT 1 "+qName);
            parent.report(restrictToFileName, locator, errorDispatcher, "");//parent should have been located, else illegal state
        }
               
          
        handleConflict(contextType, qName, restrictToFileName, locator, errorDispatcher, "");
        
        // report common as any other errors            
        if(commonMessages != null){
            // System.out.println("REPORT COMMON 1 "+qName+" "+commonMessages.hashCode());
            commonMessages.report(restrictToFileName, locator, errorDispatcher, "");                
        }
    }
    
    private void handleConflict(int contextType, String qName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        int qualified = candidatesCount - disqualified.cardinality();
        //System.out.println(hashCode()+" HANDLE CONFLICT "+getClass());
        if(qualified == 0){
            reportUnresolved(contextType, qName, restrictToFileName, locator, errorDispatcher, prefix);
        }else if(qualified == 1){
            reportResolved(contextType, qName, restrictToFileName, locator, errorDispatcher, prefix);
        }else{
            reportAmbiguous(contextType, qName, restrictToFileName, locator, errorDispatcher, prefix);
        }
    }
    
    private void reportUnresolved(int contextType, String qName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            if(candidateMessages[i] != null)message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);
        }
        if(!message.equals("")){
            //System.out.println(hashCode()+" 3 "+message);
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Element <"+qName+"> is unresolved by content validation, all candidate definitions resulted in errors:"+message, publicId, systemId, lineNumber, columnNumber));
        }else throw new IllegalStateException();
    }
    
    private void reportResolved(int contextType, String qName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        //System.out.println(hashCode()+" REPORT RESOLVED 1 "+disqualified);      
        int qualifiedIndex = disqualified.nextClearBit(0);
        if(candidateMessages != null && candidateMessages[qualifiedIndex] != null) candidateMessages[qualifiedIndex].report(restrictToFileName, locator, errorDispatcher, prefix);        
    }
    
    private void reportAmbiguous(int contextType, String qName, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        String message = ""; 
        for(int i = 0; i < candidatesCount; i++){
            //System.out.println(disqualified+" "+candidateMessages);
            if(!disqualified.get(i) && candidateMessages != null && candidateMessages[i] != null){
                 message += candidateMessages[i].getCandidateErrorMessage(prefix, restrictToFileName);            
            }
        }
        if(!message.equals("")){         
            //System.out.println(hashCode()+" 4 "+message);
            if(locator != null) errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, locator));
            else errorDispatcher.error(new SAXParseException(prefix+"Syntax error. Candidate definitions of ambiguous element <"+qName+"> contain errors in their subtrees:"+message, publicId, systemId, lineNumber, columnNumber));
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

    public String toString(){
        return "ConflictMessageReporter ";
    }
}
