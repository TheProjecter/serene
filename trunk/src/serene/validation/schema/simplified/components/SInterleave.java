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

import java.util.ArrayList;

import java.io.File;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.RestrictingVisitor;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import serene.validation.schema.simplified.components.SPattern;

import serene.bind.util.DocumentIndexedData;
import serene.util.IntList;

import sereneWrite.MessageWriter;

public class SInterleave extends AbstractMultipleChildrenPattern{
    IntList allRecordIndexes;	
    ArrayList<DocumentIndexedData> allDocumentIndexedData;
    boolean addedBySimplification;
	public SInterleave(SPattern[] children,
				IntList allRecordIndexes, 
				ArrayList<DocumentIndexedData> allDocumentIndexedData,
				boolean addedBySimplification,
				MessageWriter debugWriter){		
		super(children, DocumentIndexedData.NO_RECORD, null, debugWriter);
        this.allRecordIndexes = allRecordIndexes; 
        this.allDocumentIndexedData = allDocumentIndexedData;
        this.addedBySimplification = addedBySimplification;
	}		
	
    public SInterleave(SPattern[] children,
				int recordIndex, 
				DocumentIndexedData documentIndexedData,  
				MessageWriter debugWriter){		
		super(children, recordIndex, documentIndexedData, debugWriter);
		addedBySimplification = false;
	}
    
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
    
    public IntList getAllRecordIndexes(){
        return allRecordIndexes;
    } 
    
    public String getQName(){
        if(addedBySimplification){
            if(documentIndexedData != null) return "interleave added by "+documentIndexedData.getLocalName(recordIndex)+" simplification";
            return "interleave added by "+allDocumentIndexedData.get(0).getLocalName(allRecordIndexes.get(0))+" simplification";
        }
        if(documentIndexedData != null)return documentIndexedData.getItemDescription(recordIndex);        
        return allDocumentIndexedData.get(0).getItemDescription(allRecordIndexes.get(0));
	}
	
    public String getLocation(boolean restrictToFileName){
        if(recordIndex == DocumentIndexedData.NO_RECORD || documentIndexedData == null)return getAllLocations(restrictToFileName);
                
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
    
    private String getAllLocations(boolean restrictToFileName){
                
        if(allRecordIndexes == null || allRecordIndexes.size() == 0)return null;
        
        boolean first = true;
        String loc = "";
        for(int j = 0; j < allDocumentIndexedData.size(); j++){
            DocumentIndexedData documentIndexedData = allDocumentIndexedData.get(j); 
            int index = allRecordIndexes.get(j);
            
            String si = documentIndexedData.getSystemId(index);
            int ln = documentIndexedData.getLineNumber(index);
            if(ln == DocumentIndexedData.UNKNOWN){
                if(!(si == null || !restrictToFileName))si = getRestrictedSystemId(si);
                
                if(first){
                    first = false;
                    loc += "["+si;
                }else{
                    loc += ", "+si;
                }
            }else{
                int cn = documentIndexedData.getColumnNumber(index);
                
                if(!(si == null || !restrictToFileName))si = getRestrictedSystemId(si);
                if(first){
                    first = false;
                    loc += "["+si+":"+ln+":"+cn;
                }else{
                    loc += ", "+si+":"+ln+":"+cn;
                }
            }
        }
        return loc+"]";
    }
    
	public String toString(){
		String s = "SInterleave ";		
		return s;
	}
}