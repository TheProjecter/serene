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


package serene.validation.schema.parsed;

import java.util.Map;

import org.xml.sax.SAXException;

import serene.bind.util.DocumentIndexedData;

public class Data extends MultipleChildrenPattern{
	int typeRecordIndex;		
	Data( 
	                int xmlBase,
                    int ns, 
                    int datatypeLibrary, 
                    int type,
                    ParsedComponent[] children, 
                    int recordIndex,
                    DocumentIndexedData documentIndexedData){		
		super( xmlBase, ns, datatypeLibrary, children, recordIndex, documentIndexedData);
		this.typeRecordIndex = type; 
	}
	public String getType(){
		if(typeRecordIndex == DocumentIndexedData.NO_RECORD) return null;
		return documentIndexedData.getStringValue(typeRecordIndex);	
	}
	
    public int getTypeRecordIndex(){
        return typeRecordIndex;
    }    
    
	public void accept(ParsedComponentVisitor v){
		v.visit(this);
	}
	public void accept(SimplifyingVisitor v) throws SAXException{
		v.visit(this);
	}
	
	public String toString(){
		return "Data type "+getType();
	}
}
