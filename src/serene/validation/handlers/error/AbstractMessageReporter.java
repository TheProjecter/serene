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

import serene.validation.schema.active.components.AElement;

import sereneWrite.MessageWriter;

public abstract class AbstractMessageReporter implements MessageReporter{
    MessageReporter parent;// the handler that is supposed to report before this when delayed in a conflict situation
    
    int contextType;
	String qName;
	AElement definition;
	String publicId;
	String systemId;
	int lineNumber;
	int columnNumber;
	
    boolean restrictToFileName;
    
    int conflictResolutionId;
       
    
	MessageWriter debugWriter;
	
    public AbstractMessageReporter(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
        conflictResolutionId = RESOLVED;
	}	
    
    public void setConflictResolutionId(int conflictResolutionId){
        this.conflictResolutionId = conflictResolutionId;
    }
    
    public int getConflictResolutionId(){
        return conflictResolutionId;
    }
    
	public void setContextType(int contextType){
        this.contextType = contextType;
    }    
    
	public void setContextQName(String qName){
		this.qName = qName;
	}	
	public void setContextLocation(String publicId, String systemId, int lineNumber, int columnNumber){
	    this.publicId = publicId;
		this.systemId = systemId;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;		
	}
	public void setContextDefinition(AElement definition){
		this.definition = definition;		
	}
	public void setRestrictToFileName(boolean restrictToFileName){
	    this.restrictToFileName = restrictToFileName;
	}
	
	public void setParent(MessageReporter parent){
        if(this.parent != null) throw new IllegalStateException();
        this.parent = parent;
    }
}
