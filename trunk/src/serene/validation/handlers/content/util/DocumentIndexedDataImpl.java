/*
Copyright 2012 Radu Cernuta 

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


package serene.validation.handlers.content.util;

import java.util.Map;

import serene.bind.util.DocumentIndexedData;

class DocumentIndexedDataImpl implements DocumentIndexedData{            

	int[] itemIdTable;
    Map<String, String>[] declaredXmlns;
    int[] itemDescriptionIndexTable;
    int[] localNameIndexTable;
    int[] namespaceURIIndexTable;    
    int[] attributeTypeIndexTable;
    int[] stringValueIndexTable;   
    char[][] charArrayValueTable;
    int[] systemIdIndexTable;
    int[] publicIdIndexTable; 	
	int[] lineNumberTable;
	int[] columnNumberTable;
		
	String[] itemDescriptionTable;	
	String[] localNameTable;
	String[] namespaceURITable;
	String[] attributeTypeTable;
	String[] stringValueTable;
	String[] systemIdTable;
	String[] publicIdTable;
	
	
	/**
	* Indicates an open place in the array.
	*/
	final int AVAILABLE = -1;
	
	/**
	* Always keep first record free for null stringValues that actually need to be stored.
	* When other positions hold a null stringValue it means they are free.
	*/
	final int NULL_INDEX = 0;	
	
	
	
	DocumentIndexedDataImpl(int[] itemIdTable,
                            Map<String, String>[] declaredXmlns,
                            int[] itemDescriptionIndexTable,
                            int[] localNameIndexTable,
                            int[] namespaceURIIndexTable,    
                            int[] attributeTypeIndexTable,
                            int[] stringValueIndexTable,
                            char[][] charArrayValueTable,
                            int[] systemIdIndexTable,
                            int[] publicIdIndexTable, 	
                            int[] lineNumberTable,
                            int[] columnNumberTable,
                                
                            String[] itemDescriptionTable,	
                            String[] localNameTable,
                            String[] namespaceURITable,
                            String[] attributeTypeTable,
                            String[] stringValueTable,
                            String[] systemIdTable,
                            String[] publicIdTable){
	    this.itemIdTable = itemIdTable;
        this.declaredXmlns = declaredXmlns;
        this.itemDescriptionIndexTable = itemDescriptionIndexTable;
        this.localNameIndexTable = localNameIndexTable;
        this.namespaceURIIndexTable = namespaceURIIndexTable;    
        this.attributeTypeIndexTable = attributeTypeIndexTable;
        this.stringValueIndexTable = stringValueIndexTable;    
        this.charArrayValueTable = charArrayValueTable;
        this.systemIdIndexTable = systemIdIndexTable;
        this.publicIdIndexTable = publicIdIndexTable; 	
        this.lineNumberTable = lineNumberTable;
        this.columnNumberTable = columnNumberTable;
            
        this.itemDescriptionTable = itemDescriptionTable;	
        this.localNameTable = localNameTable;
        this.namespaceURITable = namespaceURITable;
        this.attributeTypeTable = attributeTypeTable;
        this.stringValueTable = stringValueTable;
        this.systemIdTable = systemIdTable;
        this.publicIdTable = publicIdTable;
	}
	
	
	
	
	public int getItemId(int recordIndex){
	    return itemIdTable[recordIndex];
	}
	
	public Map<String, String> getDeclaredXmlns(int recordIndex){
	    return declaredXmlns[recordIndex];
	}
	
	public String getItemDescription(int recordIndex){
		return itemDescriptionTable[itemDescriptionIndexTable[recordIndex]];
	}
    
    public String getNamespaceURI(int recordIndex){
		return namespaceURITable[namespaceURIIndexTable[recordIndex]];
	}
	
    public String getLocalName(int recordIndex){
		return localNameTable[localNameIndexTable[recordIndex]];
	}
		
	public String getAttributeType(int recordIndex){
		return attributeTypeTable[attributeTypeIndexTable[recordIndex]];
	}
    	
	public String getStringValue(int recordIndex){
	    if(stringValueTable[stringValueIndexTable[recordIndex]] == null){
	        if(charArrayValueTable[recordIndex] != null)return new String(charArrayValueTable[recordIndex]);
	        else return null;
	    }	    
		return stringValueTable[stringValueIndexTable[recordIndex]];
	}

    public char[] getCharArrayValue(int recordIndex){
        if(charArrayValueTable[recordIndex] == null){
            String s = stringValueTable[stringValueIndexTable[recordIndex]];
            if(s == null) return null;
            else return s.toCharArray();        
        }
		return charArrayValueTable[recordIndex];
	}    
	
	public String getSystemId(int recordIndex){
		return systemIdTable[systemIdIndexTable[recordIndex]];
	}
	
	public String getPublicId(int recordIndex){
		return publicIdTable[publicIdIndexTable[recordIndex]];
	}
	
	public int getLineNumber(int recordIndex){
		return lineNumberTable[recordIndex];
	}
	
	public int getColumnNumber(int recordIndex){
		return columnNumberTable[recordIndex];
	}
	
	public DocumentIndexedData getDocumentIndexedData(){
	    DocumentIndexedData did = new DocumentIndexedDataImpl(itemIdTable,
                            declaredXmlns,
                            itemDescriptionIndexTable,
                            localNameIndexTable,
                            namespaceURIIndexTable,    
                            attributeTypeIndexTable,
                            stringValueIndexTable,    
                            charArrayValueTable,
                            systemIdIndexTable,
                            publicIdIndexTable, 	
                            lineNumberTable,
                            columnNumberTable,
                                
                            itemDescriptionTable,	
                            localNameTable,
                            namespaceURITable,
                            attributeTypeTable,
                            stringValueTable,
                            systemIdTable,
                            publicIdTable);
        return did;
	}
}
