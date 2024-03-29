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

import serene.validation.schema.simplified.SElement;

public abstract class AbstractMessageReporter implements MessageReporter{
    MessageReporter parent;// the handler that is supposed to report before this when delayed in a conflict situation
    
    int reportingContextType;// TODO it could probably be removed
	String reportingContextQName;
	SElement reportingContextDefinition;
	String reportingContextPublicId;
	String reportingContextSystemId;
	int reportingContextLineNumber;
	int reportingContextColumnNumber;
	
    boolean restrictToFileName;
    
    int conflictResolutionId;
       
    public AbstractMessageReporter(){
        conflictResolutionId = RESOLVED;
	}	
    
	
    public void setConflictResolutionId(int conflictResolutionId){
        this.conflictResolutionId = conflictResolutionId;
    }
    
    public int getConflictResolutionId(){
        return conflictResolutionId;
    }
    
	public void setReportingContextType(int reportingContextType){
        this.reportingContextType = reportingContextType;
    }    
    
	public void setReportingContextQName(String reportingContextQName){
		this.reportingContextQName = reportingContextQName;
	}	
	public void setReportingContextLocation(String reportingContextPublicId, String reportingContextSystemId, int reportingContextLineNumber, int reportingContextColumnNumber){
	    this.reportingContextPublicId = reportingContextPublicId;
		this.reportingContextSystemId = reportingContextSystemId;
		this.reportingContextLineNumber = reportingContextLineNumber;
		this.reportingContextColumnNumber = reportingContextColumnNumber;		
	}
		
	public void setReportingContextDefinition(SElement reportingContextDefinition){
		this.reportingContextDefinition = reportingContextDefinition;		
	}
	public void setRestrictToFileName(boolean restrictToFileName){
	    this.restrictToFileName = restrictToFileName;
	}
	
	public void setParent(MessageReporter parent){
        if(this.parent != null) throw new IllegalStateException();
        
        this.parent = parent;
        
        parent.registerClient(this);
    }    
    
}
