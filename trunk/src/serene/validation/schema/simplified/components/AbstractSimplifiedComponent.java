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

package serene.validation.schema.simplified.components;

import java.io.File;

import serene.validation.schema.simplified.SimplifiedComponentVisitor;
import serene.validation.schema.simplified.SimplifiedComponent;

import serene.bind.util.DocumentIndexedData;

import sereneWrite.MessageWriter;

abstract class AbstractSimplifiedComponent implements SimplifiedComponent{
	protected int childIndex;	
	protected SimplifiedComponent parent;
		
	/*protected String qName;
	protected String location;*/
    int recordIndex;
    DocumentIndexedData documentIndexedData;	
	protected MessageWriter debugWriter;	
	
	public AbstractSimplifiedComponent(int recordIndex, DocumentIndexedData documentIndexedData, MessageWriter debugWriter){		
		this.debugWriter = debugWriter;
		this.recordIndex = recordIndex;
		this.documentIndexedData = documentIndexedData;
		
		childIndex = -1;		
	}

	void setParent(SimplifiedComponent parent){		 
		this.parent = parent;				
	}
	
	public SimplifiedComponent getParent(){
		return parent;
	}
	
	void setChildIndex(int childIndex){			
		this.childIndex = childIndex;
	}	
	
	public int getChildIndex(){
		return childIndex;
	}
	
	public int getRecordIndex(){
	    return recordIndex;
	}
	
	public DocumentIndexedData getDocumentIndexedData(){
	    return documentIndexedData;
	}
	
	
	public String getQName(){
	    //System.out.println("ABSTRACT SIMPLIFIED COMPONENT documentIndexedData="+documentIndexedData);
		return documentIndexedData.getItemDescription(recordIndex);
	}
	public String getLocation(boolean restrictToFileName){
	    String si = documentIndexedData.getSystemId(recordIndex);
	    int ln = documentIndexedData.getLineNumber(recordIndex);
	    if(ln == DocumentIndexedData.UNKNOWN){
	        if(si == null || !restrictToFileName)return si;
	        return getRestrictedSystemId(si);
	    }
	    
	    int cn = documentIndexedData.getColumnNumber(recordIndex);
	    
        if(si == null || !restrictToFileName){
            return si+":"+ln+":"+cn;
        }
        return getRestrictedSystemId(si)+":"+ln+":"+cn;
	}
	String getRestrictedSystemId(String si){
	    int nameIndex = si.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = si.lastIndexOf('/')+1;
        return si.substring(nameIndex);
	}
}	