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

import java.util.Arrays;
import java.util.Map;

import serene.validation.handlers.structure.impl.*;
import serene.validation.handlers.error.*;
import serene.bind.util.Queue;

import serene.bind.util.DocumentIndexedData;
/**
* Stores location and item identification data for the document items 
* involved in validation. 
*/
public class ActiveInputDescriptor implements DocumentIndexedData{
                
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
	
	
	int initialSize;
	int startSearch;	
	int increaseSizeAmount;	
	
	int[] recordedClientsCount;
	
	
	/**
	* Indicates an open place in the array.
	*/
	final int AVAILABLE = -1;
	
	/**
	* Always keep first record free for null stringValues that actually need to be stored.
	* When other positions hold a null stringValue it means they are free.
	*/
	final int NULL_INDEX = 0;	
	/*String[] itemDescriptionTable;	
	String[] localNameTable;
	String[] namespaceURITable;
	String[] stringValueTable;
	String[] systemIdTable;
	String[] publicIdTable;*/
	
	//int used = 0;
	public ActiveInputDescriptor(){
	    initialSize = 10;
	    startSearch = 0;
	    increaseSizeAmount = 20;         
	    
	    
	    itemIdTable = new int[initialSize];	   
        itemDescriptionIndexTable = new int[initialSize];
        localNameIndexTable = new int[initialSize];
        namespaceURIIndexTable = new int[initialSize];
        attributeTypeIndexTable = new int[initialSize];
        stringValueIndexTable = new int[initialSize];
        charArrayValueTable = new char[initialSize][];
        systemIdIndexTable = new int[initialSize];
        publicIdIndexTable = new int[initialSize]; 	
        lineNumberTable = new int[initialSize];
        columnNumberTable = new int[initialSize];
        declaredXmlns = new Map[initialSize];
        
        
        itemDescriptionTable = new String[5];
        localNameTable = new String[5];
        namespaceURITable = new String[5];
        attributeTypeTable = new String[5];
        stringValueTable = new String[5];
        systemIdTable = new String[5];
        publicIdTable = new String[5];
        
        // TODO
        // Consider not interning stringValues, it's unlikely they are often repeated
        // and so comparing everything is wasteful. 
	    	           
	   
        recordedClientsCount = new int[initialSize];
        Arrays.fill(recordedClientsCount, 0);
	    /*itemIdTable = new int[initialSize];
        itemDescriptionIndexTable = new int[initialSize];
        localNameIndexTable = new int[initialSize];
        namespaceURIIndexTable = new int[initialSize];
        stringValueIndexTable = new int[initialSize];
        systemIdIndexTable = new int[initialSize];
        publicIdIndexTable = new int[initialSize]; 	
        lineNumberTable = new int[initialSize];
        columnNumberTable = new int[initialSize];
        
        
        itemDescriptionTable = new String[5];
        localNameTable = new String[5];
        namespaceURITable = new String[5];
        stringValueTable = new String[5];
        systemIdTable = new String[5];
        publicIdTable = new String[5];*/
	}
	
	
	public void clear(){
	    
	    Arrays.fill(recordedClientsCount, 0);
	    
        Arrays.fill(declaredXmlns, null);
            
        Arrays.fill(itemDescriptionTable, null);	
        Arrays.fill(localNameTable, null);
        Arrays.fill(namespaceURITable, null);
        Arrays.fill(attributeTypeTable, null);
        Arrays.fill(stringValueTable, null);
        Arrays.fill(systemIdTable, null);
        Arrays.fill(publicIdTable, null);        
        
        startSearch = 0;        
	}
	private void increaseSize(int oldSize){
	    int newSize = oldSize + increaseSizeAmount;		    
        
        int[] increasedRCC = new int[newSize];
        System.arraycopy(recordedClientsCount, 0, increasedRCC, 0, oldSize);
        recordedClientsCount = increasedRCC;
        Arrays.fill(recordedClientsCount, oldSize, newSize, 0);
               
        
        int[] increasedII = new int[newSize];
        System.arraycopy(itemIdTable, 0, increasedII, 0, oldSize);
        itemIdTable = increasedII;
        
        Map<String, String>[] increasedDX = new Map[newSize];
        System.arraycopy(declaredXmlns, 0, increasedDX, 0, oldSize);
        declaredXmlns = increasedDX;
                
        int[] increasedQN = new int[newSize];
        System.arraycopy(itemDescriptionIndexTable, 0, increasedQN, 0, oldSize);
        itemDescriptionIndexTable = increasedQN;
        
        
        int[] increasedNsURI = new int[newSize];
        System.arraycopy(namespaceURIIndexTable, 0, increasedNsURI, 0, oldSize);
        namespaceURIIndexTable = increasedNsURI;
        
        int[] increasedLocalName = new int[newSize];
        System.arraycopy(localNameIndexTable, 0, increasedLocalName, 0, oldSize);
        localNameIndexTable = increasedLocalName;
        
        int[] increasedAT = new int[newSize];
        System.arraycopy(attributeTypeIndexTable, 0, increasedAT, 0, oldSize);
        attributeTypeIndexTable = increasedAT;
        
        int[] increasedSV = new int[newSize];
        System.arraycopy(stringValueIndexTable, 0, increasedSV, 0, oldSize);
        stringValueIndexTable = increasedSV;
        
        char[][] increasedCAV = new char[newSize][];
        System.arraycopy(charArrayValueTable, 0, increasedCAV, 0, oldSize);
        charArrayValueTable = increasedCAV;
        
        int[] increasedSI = new int[newSize];
        System.arraycopy(systemIdIndexTable, 0, increasedSI, 0, oldSize);
        systemIdIndexTable = increasedSI;
        
        int[] increasedPI = new int[newSize];
        System.arraycopy(publicIdIndexTable, 0, increasedPI, 0, oldSize);
        publicIdIndexTable = increasedPI;
                    
        int[] increasedLN = new int[newSize];
        System.arraycopy(lineNumberTable, 0, increasedLN, 0, oldSize);
        lineNumberTable = increasedLN;
        
        int[] increasedCN = new int[newSize];
        System.arraycopy(columnNumberTable, 0, increasedCN, 0, oldSize);
        columnNumberTable = increasedCN;	
	}
	
	
	/**
	* Record the data returning the index of the record. It does not register client.	
	*/
	// for bound attribute
	int recordItem(int itemId,	    
                    String itemDescription,
                    String namespaceURI, 
                    String localName,
                    String attributeType,
                    String stringValue,
                    String systemId, 
                    String publicId, 
                    int lineNumber, 
                    int columnNumber){
        for(int i = startSearch; i  < recordedClientsCount.length ; i++){
            if(recordedClientsCount[i] == 0){
                itemIdTable[i] = itemId;
                itemDescriptionIndexTable[i] = getItemDescriptionIndex(itemDescription);
                namespaceURIIndexTable[i] = getNamespaceURIIndex(namespaceURI);
                localNameIndexTable[i] = getLocalNameIndex(localName);
                attributeTypeIndexTable[i] = getAttributeTypeIndex(attributeType);                
                stringValueIndexTable[i] = getValueIndex(stringValue);
                systemIdIndexTable[i] = getSystemIdIndex(systemId);
                publicIdIndexTable[i] = getPublicIdIndex(publicId); 	
                lineNumberTable[i] = lineNumber;
                columnNumberTable[i] = columnNumber;
                startSearch = i;
                return i;
            }
        }
    
	    
        
        int oldSize = recordedClientsCount.length;
        increaseSize(oldSize);
    
		itemIdTable[oldSize] = itemId;
        itemDescriptionIndexTable[oldSize] = getItemDescriptionIndex(itemDescription);
        namespaceURIIndexTable[oldSize] = getNamespaceURIIndex(namespaceURI);
        localNameIndexTable[oldSize] = getLocalNameIndex(localName);
        attributeTypeIndexTable[oldSize] = getAttributeTypeIndex(attributeType);        
        stringValueIndexTable[oldSize] = getValueIndex(stringValue);
        systemIdIndexTable[oldSize] = getSystemIdIndex(systemId);
        publicIdIndexTable[oldSize] = getPublicIdIndex(publicId); 	
        lineNumberTable[oldSize] = lineNumber;
        columnNumberTable[oldSize] = columnNumber;
        
        return oldSize;
	}
	
	
	/**
	* Record the data returning the index of the record. It does not register client.	
	*/
	// for unbound attribute
	int recordItem(int itemId,	    
                    String itemDescription,
                    String namespaceURI, 
                    String localName,
                    String attributeType,
                    //String stringValue,
                    String systemId, 
                    String publicId, 
                    int lineNumber, 
                    int columnNumber){   
        
        for(int i = startSearch; i  < recordedClientsCount.length ; i++){
            if(recordedClientsCount[i] == 0){
                itemIdTable[i] = itemId;
                itemDescriptionIndexTable[i] = getItemDescriptionIndex(itemDescription);
                namespaceURIIndexTable[i] = getNamespaceURIIndex(namespaceURI);
                localNameIndexTable[i] = getLocalNameIndex(localName);
                attributeTypeIndexTable[i] = getAttributeTypeIndex(attributeType);                
                //stringValueIndexTable[i] = getValueIndex(stringValue);
                systemIdIndexTable[i] = getSystemIdIndex(systemId);
                publicIdIndexTable[i] = getPublicIdIndex(publicId); 	
                lineNumberTable[i] = lineNumber;
                columnNumberTable[i] = columnNumber;
                startSearch = i;
                return i;
            }
        }
    
	    
        
        int oldSize = recordedClientsCount.length;
        increaseSize(oldSize);
    
		itemIdTable[oldSize] = itemId;
        itemDescriptionIndexTable[oldSize] = getItemDescriptionIndex(itemDescription);
        namespaceURIIndexTable[oldSize] = getNamespaceURIIndex(namespaceURI);
        localNameIndexTable[oldSize] = getLocalNameIndex(localName);
        attributeTypeIndexTable[oldSize] = getAttributeTypeIndex(attributeType);        
        stringValueIndexTable[oldSize] = NULL_INDEX;
        systemIdIndexTable[oldSize] = getSystemIdIndex(systemId);
        publicIdIndexTable[oldSize] = getPublicIdIndex(publicId); 	
        lineNumberTable[oldSize] = lineNumber;
        columnNumberTable[oldSize] = columnNumber;
        
        return oldSize;
	}
	
	
	
	/**
	* Record the data returning the index of the record. It does not register client.	
	*/
	// for element
	int recordItem(int itemId,
	                Map<String, String> xmlns,
                    String itemDescription,
                    String namespaceURI, 
                    String localName,
                    //String attributeType,
                    //String stringValue,
                    String systemId, 
                    String publicId, 
                    int lineNumber, 
                    int columnNumber){
        for(int i = startSearch; i  < recordedClientsCount.length ; i++){
            if(recordedClientsCount[i] == 0){
                itemIdTable[i] = itemId;
                declaredXmlns[i] = xmlns;
                itemDescriptionIndexTable[i] = getItemDescriptionIndex(itemDescription);
                namespaceURIIndexTable[i] = getNamespaceURIIndex(namespaceURI);
                localNameIndexTable[i] = getLocalNameIndex(localName);
                attributeTypeIndexTable[i] = NULL_INDEX;                
                stringValueIndexTable[i] = NULL_INDEX;
                systemIdIndexTable[i] = getSystemIdIndex(systemId);
                publicIdIndexTable[i] = getPublicIdIndex(publicId); 	
                lineNumberTable[i] = lineNumber;
                columnNumberTable[i] = columnNumber;
                startSearch = i;
                return i;
            }
        }
    
	    
        
        int oldSize = recordedClientsCount.length;
        increaseSize(oldSize);
    
		itemIdTable[oldSize] = itemId;
		declaredXmlns[oldSize] = xmlns;
        itemDescriptionIndexTable[oldSize] = getItemDescriptionIndex(itemDescription);
        namespaceURIIndexTable[oldSize] = getNamespaceURIIndex(namespaceURI);
        localNameIndexTable[oldSize] = getLocalNameIndex(localName);
        attributeTypeIndexTable[oldSize] = NULL_INDEX;        
        stringValueIndexTable[oldSize] = NULL_INDEX;
        systemIdIndexTable[oldSize] = getSystemIdIndex(systemId);
        publicIdIndexTable[oldSize] = getPublicIdIndex(publicId); 	
        lineNumberTable[oldSize] = lineNumber;
        columnNumberTable[oldSize] = columnNumber;
        
        return oldSize;
	}
	
	
	/**
	* Record the data returning the index of the record. It does not register client.	
	*/
	// for element
	int recordItem(int itemId,
                    String itemDescription,
                    String namespaceURI, 
                    String localName,
                    //String attributeType,
                    //String stringValue,
                    String systemId, 
                    String publicId, 
                    int lineNumber, 
                    int columnNumber){    
        for(int i = startSearch; i  < recordedClientsCount.length ; i++){
            if(recordedClientsCount[i] == 0){
                itemIdTable[i] = itemId;
                itemDescriptionIndexTable[i] = getItemDescriptionIndex(itemDescription);
                namespaceURIIndexTable[i] = getNamespaceURIIndex(namespaceURI);
                localNameIndexTable[i] = getLocalNameIndex(localName);
                attributeTypeIndexTable[i] = NULL_INDEX;                
                stringValueIndexTable[i] = NULL_INDEX;
                systemIdIndexTable[i] = getSystemIdIndex(systemId);
                publicIdIndexTable[i] = getPublicIdIndex(publicId); 	
                lineNumberTable[i] = lineNumber;
                columnNumberTable[i] = columnNumber;
                startSearch = i;
                return i;
            }
        }
    
	    
        
        int oldSize = recordedClientsCount.length;
        increaseSize(oldSize);
    
		itemIdTable[oldSize] = itemId;
        itemDescriptionIndexTable[oldSize] = getItemDescriptionIndex(itemDescription);
        namespaceURIIndexTable[oldSize] = getNamespaceURIIndex(namespaceURI);
        localNameIndexTable[oldSize] = getLocalNameIndex(localName);
        attributeTypeIndexTable[oldSize] = NULL_INDEX;        
        stringValueIndexTable[oldSize] = NULL_INDEX;
        systemIdIndexTable[oldSize] = getSystemIdIndex(systemId);
        publicIdIndexTable[oldSize] = getPublicIdIndex(publicId); 	
        lineNumberTable[oldSize] = lineNumber;
        columnNumberTable[oldSize] = columnNumber;
        
        return oldSize;
	}
	
	
	/**
	* Record the data returning the index of the record. It does not register client.	
	*/
	// for bound character content and token
	int recordItem(int itemId,
                    String itemDescription,
                    //String namespaceURI, 
                    //String localName,
                    //String attributeType,
                    String stringValue,
                    String systemId, 
                    String publicId, 
                    int lineNumber, 
                    int columnNumber){    
        for(int i = startSearch; i  < recordedClientsCount.length ; i++){
            if(recordedClientsCount[i] == 0){
                itemIdTable[i] = itemId;
                itemDescriptionIndexTable[i] = getItemDescriptionIndex(itemDescription);
                namespaceURIIndexTable[i] = NULL_INDEX;
                localNameIndexTable[i] = NULL_INDEX;
                attributeTypeIndexTable[i] = NULL_INDEX;                
                stringValueIndexTable[i] = getValueIndex(stringValue);
                systemIdIndexTable[i] = getSystemIdIndex(systemId);
                publicIdIndexTable[i] = getPublicIdIndex(publicId); 	
                lineNumberTable[i] = lineNumber;
                columnNumberTable[i] = columnNumber;
                startSearch = i;
                return i;
            }
        }
    
	    
        
        int oldSize = recordedClientsCount.length;
        increaseSize(oldSize);
    
		itemIdTable[oldSize] = itemId;
        itemDescriptionIndexTable[oldSize] = getItemDescriptionIndex(itemDescription);
        namespaceURIIndexTable[oldSize] = NULL_INDEX;
        localNameIndexTable[oldSize] = NULL_INDEX;
        attributeTypeIndexTable[oldSize] = NULL_INDEX;        
        stringValueIndexTable[oldSize] = getValueIndex(stringValue);
        systemIdIndexTable[oldSize] = getSystemIdIndex(systemId);
        publicIdIndexTable[oldSize] = getPublicIdIndex(publicId); 	
        lineNumberTable[oldSize] = lineNumber;
        columnNumberTable[oldSize] = columnNumber;
        
        return oldSize;
	}
	/**
	* Record the data returning the index of the record. It does not register client.	
	*/
	// for bound character content and token
	int recordItem(int itemId,
                    String itemDescription,
                    //String namespaceURI, 
                    //String localName,
                    //String attributeType,
                    char[] value,
                    String systemId, 
                    String publicId, 
                    int lineNumber, 
                    int columnNumber){    
        for(int i = startSearch; i  < recordedClientsCount.length ; i++){
            if(recordedClientsCount[i] == 0){
                itemIdTable[i] = itemId;
                itemDescriptionIndexTable[i] = getItemDescriptionIndex(itemDescription);
                namespaceURIIndexTable[i] = NULL_INDEX;
                localNameIndexTable[i] = NULL_INDEX;
                attributeTypeIndexTable[i] = NULL_INDEX;                
                stringValueIndexTable[i] = NULL_INDEX;
                charArrayValueTable[i] = value;
                systemIdIndexTable[i] = getSystemIdIndex(systemId);
                publicIdIndexTable[i] = getPublicIdIndex(publicId); 	
                lineNumberTable[i] = lineNumber;
                columnNumberTable[i] = columnNumber;
                startSearch = i;
                return i;
            }
        }
    
	    
        
        int oldSize = recordedClientsCount.length;
        increaseSize(oldSize);
    
		itemIdTable[oldSize] = itemId;
        itemDescriptionIndexTable[oldSize] = getItemDescriptionIndex(itemDescription);
        namespaceURIIndexTable[oldSize] = NULL_INDEX;
        localNameIndexTable[oldSize] = NULL_INDEX;
        attributeTypeIndexTable[oldSize] = NULL_INDEX;        
        stringValueIndexTable[oldSize] = NULL_INDEX;
        charArrayValueTable[oldSize] = value;
        systemIdIndexTable[oldSize] = getSystemIdIndex(systemId);
        publicIdIndexTable[oldSize] = getPublicIdIndex(publicId); 	
        lineNumberTable[oldSize] = lineNumber;
        columnNumberTable[oldSize] = columnNumber;
        
        return oldSize;
	}
		
	
	/**
	* Record the data returning the index of the record. It does not register client.	
	*/
	// for unbound character content 
	int recordItem(int itemId,
                    String itemDescription,
                    //String namespaceURI, 
                    //String localName,
                    //String attributeType,
                    //String stringValue,
                    String systemId, 
                    String publicId, 
                    int lineNumber, 
                    int columnNumber){    
        for(int i = startSearch; i  < recordedClientsCount.length ; i++){
            if(recordedClientsCount[i] == 0){
                itemIdTable[i] = itemId;
                itemDescriptionIndexTable[i] = getItemDescriptionIndex(itemDescription);
                namespaceURIIndexTable[i] = NULL_INDEX;
                localNameIndexTable[i] = NULL_INDEX;
                attributeTypeIndexTable[i] = NULL_INDEX;                
                stringValueIndexTable[i] = NULL_INDEX;
                systemIdIndexTable[i] = getSystemIdIndex(systemId);
                publicIdIndexTable[i] = getPublicIdIndex(publicId); 	
                lineNumberTable[i] = lineNumber;
                columnNumberTable[i] = columnNumber;
                startSearch = i;
                return i;
            }
        }
    
	    
        
        int oldSize = recordedClientsCount.length;
        increaseSize(oldSize);
    
		itemIdTable[oldSize] = itemId;
        itemDescriptionIndexTable[oldSize] = getItemDescriptionIndex(itemDescription);
        namespaceURIIndexTable[oldSize] = NULL_INDEX;
        localNameIndexTable[oldSize] = NULL_INDEX;
        attributeTypeIndexTable[oldSize] = NULL_INDEX;        
        stringValueIndexTable[oldSize] = NULL_INDEX;
        systemIdIndexTable[oldSize] = getSystemIdIndex(systemId);
        publicIdIndexTable[oldSize] = getPublicIdIndex(publicId); 	
        lineNumberTable[oldSize] = lineNumber;
        columnNumberTable[oldSize] = columnNumber;
        
        return oldSize;
	}
	
	

	int getItemDescriptionIndex(String itemDescription){	    
	    if(itemDescription == null)return NULL_INDEX;
	    
	    int firstNullRecordIndex = NULL_INDEX;
	    for(int i = 1; i < itemDescriptionTable.length; i++){//first record is null anyway by convention
	        if(itemDescription.equals(itemDescriptionTable[i])){
	            //if(itemDescription != itemDescriptionTable[i]) {
	                //System.out.println("OOPS ITEM DESCRIPTION "+itemDescription);
	            //}
	            return i;
	        }
	        else if(itemDescriptionTable[i] == null && firstNullRecordIndex == NULL_INDEX) firstNullRecordIndex = i;  
	    }
	    if(firstNullRecordIndex != NULL_INDEX){
	        itemDescriptionTable[firstNullRecordIndex] = itemDescription;
	        return firstNullRecordIndex;
	    }
	    
	    int length = itemDescriptionTable.length;
	    
	    String[] increased = new String[length+5];
	    System.arraycopy(itemDescriptionTable, 0, increased, 0, length);
	    itemDescriptionTable = increased;
	    itemDescriptionTable[length] = itemDescription;
	    return length;
	}
	int getNamespaceURIIndex(String namespaceURI){
	    if(namespaceURI == null)return NULL_INDEX;
	    
	    int firstNullRecordIndex = NULL_INDEX;
	    for(int i = 1; i < namespaceURITable.length; i++){//first record is null anyway by convention
	        if(namespaceURI.equals(namespaceURITable[i])){
	            //if(namespaceURI != namespaceURITable[i]) System.out.println("OOPS NAMESPACE URI");
	            return i;
	        }
	        else if(namespaceURITable[i] == null && firstNullRecordIndex == NULL_INDEX) firstNullRecordIndex = i;  
	    }
	    if(firstNullRecordIndex != NULL_INDEX){
	        namespaceURITable[firstNullRecordIndex] = namespaceURI;
	        return firstNullRecordIndex;
	    }
	    
	    int length = namespaceURITable.length;
	    
	    String[] increased = new String[length+5];
	    System.arraycopy(namespaceURITable, 0, increased, 0, length);
	    namespaceURITable = increased;
	    namespaceURITable[length] = namespaceURI;
	    return length;
	}
	int getLocalNameIndex(String localName){
	    if(localName == null)return NULL_INDEX;
	    
	    int firstNullRecordIndex = NULL_INDEX;
	    for(int i = 1; i < localNameTable.length; i++){//first record is null anyway by convention
	        if(localName.equals(localNameTable[i])){
	            //if(localName != localNameTable[i]) System.out.println("OOPS LOCAL NAME");
	            return i;    
	        }	        
	        else if(localNameTable[i] == null && firstNullRecordIndex == NULL_INDEX) firstNullRecordIndex = i;  
	    }
	    if(firstNullRecordIndex != NULL_INDEX){
	        localNameTable[firstNullRecordIndex] = localName;
	        return firstNullRecordIndex;
	    }
	    
	    int length = localNameTable.length;
	    
	    String[] increased = new String[length+5];
	    System.arraycopy(localNameTable, 0, increased, 0, length);
	    localNameTable = increased;
	    localNameTable[length] = localName;
	    return length;
	}
	int getAttributeTypeIndex(String attributeType){
	    if(attributeType == null)return NULL_INDEX;
	    
	    int firstNullRecordIndex = NULL_INDEX;
	    for(int i = 1; i < attributeTypeTable.length; i++){//first record is null anyway by convention
	        if(attributeType.equals(attributeTypeTable[i])){
	            //if(attributeType != attributeTypeTable[i]) System.out.println("OOPS NAMESPACE URI");
	            return i;
	        }
	        else if(attributeTypeTable[i] == null && firstNullRecordIndex == NULL_INDEX) firstNullRecordIndex = i;  
	    }
	    if(firstNullRecordIndex != NULL_INDEX){
	        attributeTypeTable[firstNullRecordIndex] = attributeType;
	        return firstNullRecordIndex;
	    }
	    
	    int length = attributeTypeTable.length;
	    
	    String[] increased = new String[length+5];
	    System.arraycopy(attributeTypeTable, 0, increased, 0, length);
	    attributeTypeTable = increased;
	    attributeTypeTable[length] = attributeType;
	    return length;
	}
	int getValueIndex(String stringValue){
	    if(stringValue == null)return NULL_INDEX;
	    
	    int firstNullRecordIndex = NULL_INDEX;
	    for(int i = 1; i < stringValueTable.length; i++){//first record is null anyway by convention
	        if(stringValue.equals(stringValueTable[i])){
	            //if(stringValue != stringValueTable[i]) System.out.println("OOPS NAMESPACE URI");
	            return i;
	        }
	        else if(stringValueTable[i] == null && firstNullRecordIndex == NULL_INDEX) firstNullRecordIndex = i;  
	    }
	    if(firstNullRecordIndex != NULL_INDEX){
	        stringValueTable[firstNullRecordIndex] = stringValue;
	        return firstNullRecordIndex;
	    }
	    
	    int length = stringValueTable.length;
	    
	    String[] increased = new String[length+5];
	    System.arraycopy(stringValueTable, 0, increased, 0, length);
	    stringValueTable = increased;
	    stringValueTable[length] = stringValue;
	    return length;
	}
	int getSystemIdIndex(String systemId){
	    if(systemId == null)return NULL_INDEX;
	    
	    int firstNullRecordIndex = NULL_INDEX;
	    for(int i = 1; i < systemIdTable.length; i++){//first record is null anyway by convention
	        if(systemId.equals(systemIdTable[i])){
	            //if(systemId != systemIdTable[i]) System.out.println("OOPS SYSTEM ID");
	            return i;
	        }
	        else if(systemIdTable[i] == null && firstNullRecordIndex == NULL_INDEX) firstNullRecordIndex = i;  
	    }
	    if(firstNullRecordIndex != NULL_INDEX){
	        systemIdTable[firstNullRecordIndex] = systemId;
	        return firstNullRecordIndex;
	    }
	    
	    int length = systemIdTable.length;
	    
	    String[] increased = new String[length+5];
	    System.arraycopy(systemIdTable, 0, increased, 0, length);
	    systemIdTable = increased;
	    systemIdTable[length] = systemId;
	    return length;
	}
	int getPublicIdIndex(String publicId){
	    if(publicId == null)return NULL_INDEX;
	    
	    int firstNullRecordIndex = NULL_INDEX;
	    for(int i = 1; i < publicIdTable.length; i++){//first record is null anyway by convention
	        if(publicId.equals(publicIdTable[i])){
	            //if(publicId != publicIdTable[i]) System.out.println("OOPS PUBLIC ID");
	            return i;
	        }
	        else if(publicIdTable[i] == null && firstNullRecordIndex == NULL_INDEX) firstNullRecordIndex = i;  
	    }
	    if(firstNullRecordIndex != NULL_INDEX){
	        publicIdTable[firstNullRecordIndex] = publicId;
	        return firstNullRecordIndex;
	    }
	    
	    int length = publicIdTable.length;
	    
	    String[] increased = new String[length+5];
	    System.arraycopy(publicIdTable, 0, increased, 0, length);
	    publicIdTable = increased;
	    publicIdTable[length] = publicId;
	    return length;
	}
	
	public void printLeftOvers(){
	    //System.out.println(hashCode()+"  LEFT OVERS------------------------------------------");
	    boolean hasLeftOvers = false;
	    for(int i = 0; i < recordedClientsCount.length; i++){
	        if(recordedClientsCount[i] == 0){
	            //System.out.println(i+"\tAVAILABLE" );
	        }else{
	            //System.out.println(i+"\t"+recordedClientsCount[i]);
	            hasLeftOvers = true;
	            //throw new IllegalStateException();
	        }
	    }	    
	    
	   if(hasLeftOvers) System.out.println(hashCode()+" LEFT OVERS------------------------------------------");
	   else System.out.println(hashCode()+" ----");
	}
	
	public void registerClientForRecord(int recordIndex, Object o){
	    recordedClientsCount[recordIndex] += 1;
	    //System.out.println("+++ 1 recordIndex="+recordIndex+" clientsCount="+recordedClientsCount[recordIndex]+" "+o.hashCode()+" "+o.getClass().getName());
	}
	
	
	public void registerClientForRecord(int[] recordIndex, Object o){
	    for(int i = 0; i < recordIndex.length; i++){
	        recordedClientsCount[recordIndex[i]] += 1;
	        //System.out.println("+++ 2 recordIndex="+recordIndex[i]+" clientsCount="+recordedClientsCount[recordIndex[i]]+" "+o.hashCode()+" "+o.getClass().getName());
	    }
	}
	
	public void registerClientForRecord(int[] recordIndex, int offset, int count, Object o){
	    for(int i = offset; i < offset+count; i++){
	        recordedClientsCount[recordIndex[i]] += 1;
	        //System.out.println(i+"+++ 3 recordIndex="+recordIndex[i]+" clientsCount="+recordedClientsCount[recordIndex[i]]+" "+o.hashCode()+" "+o.getClass().getName());
	    }
	}
	
	public void unregisterClientForRecord(int recordIndex, Object o){    
	    recordedClientsCount[recordIndex] -= 1;
	    //System.out.println("--- 1 recordIndex="+recordIndex+" clientsCount="+recordedClientsCount[recordIndex]+" "+o.hashCode()+" "+o.getClass().getName());	   
	    if(recordedClientsCount[recordIndex] == 0){
	        
	        itemIdTable[recordIndex] = AVAILABLE;
	        
	        if(declaredXmlns[recordIndex] != null){
	            declaredXmlns[recordIndex] = null;
	        }
	        
	        handleItemDescriptionTableRemove(recordIndex);
	        itemDescriptionIndexTable[recordIndex] = AVAILABLE;
	        
	        
	        if(localNameIndexTable[recordIndex] != AVAILABLE){
	            handleLocalNameTableRemove(recordIndex);
	            localNameIndexTable[recordIndex] = AVAILABLE;	            
	        }
	        
	        if(namespaceURIIndexTable[recordIndex] != AVAILABLE){
	            handleNamespaceURITableRemove(recordIndex);
	            namespaceURIIndexTable[recordIndex] = AVAILABLE;	            
	        }
	        
	        if(charArrayValueTable[recordIndex] != null){
	            charArrayValueTable[recordIndex] = null;
	        }
	        
	        if(stringValueIndexTable[recordIndex] != AVAILABLE){
	            handleValueTableRemove(recordIndex);
	            stringValueIndexTable[recordIndex] = AVAILABLE;	            
	        }
	        
	        handleSystemIdTableRemove(recordIndex);
	        systemIdIndexTable[recordIndex] = AVAILABLE;
	        
            handlePublicIdTableRemove(recordIndex);	        
	        publicIdIndexTable[recordIndex] = AVAILABLE;
	        	        
	        lineNumberTable[recordIndex] = AVAILABLE;
	        
	        columnNumberTable[recordIndex] = AVAILABLE;
	        
	        if(recordIndex < startSearch) startSearch = recordIndex;
	    }else if(recordedClientsCount[recordIndex] < 0){
	        throw new IllegalStateException();
	    }
	}
	public void unregisterClientForRecord(int[] recordIndex, Object o){
	    for(int i = 0; i < recordIndex.length; i++){
	        //System.out.println("--- 2 recordIndex="+recordIndex[i]+" clientsCount="+recordedClientsCount[recordIndex[i]]+" "+o.hashCode()+" "+o.getClass().getName());
	        unregisterClientForRecord(recordIndex[i], o);
        }
	}
	public void unregisterClientForRecord(int[] recordIndex, int offset, int count, Object o){
	    for(int i = offset; i < offset+count; i++){
	        //System.out.println("--- 3 recordIndex="+recordIndex[i]+" clientsCount="+recordedClientsCount[recordIndex[i]]+" "+o.hashCode()+" "+o.getClass().getName());
	        unregisterClientForRecord(recordIndex[i], o);
        }
	}	
	
	void handleItemDescriptionTableRemove(int recordIndex){
	    int removeIndex = itemDescriptionIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < itemDescriptionIndexTable.length; i++){
	        if(itemDescriptionIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same stringValue.
	    }
	    
	    itemDescriptionTable[removeIndex] = null;
	}
	
	void handleLocalNameTableRemove(int recordIndex){
	    int removeIndex = localNameIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < localNameIndexTable.length; i++){
	        if(localNameIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same stringValue.
	    }
	    
	    localNameTable[removeIndex] = null;
	}
	
	void handleNamespaceURITableRemove(int recordIndex){
	    int removeIndex = namespaceURIIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < namespaceURIIndexTable.length; i++){
	        if(namespaceURIIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same stringValue.
	    }
	    
	    namespaceURITable[removeIndex] = null;
	}
	
	void handleValueTableRemove(int recordIndex){
	    int removeIndex = stringValueIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < stringValueIndexTable.length; i++){
	        if(stringValueIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same stringValue.
	    }
	    
	    stringValueTable[removeIndex] = null;
	}
	
	void handleSystemIdTableRemove(int recordIndex){
	    int removeIndex = systemIdIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < systemIdIndexTable.length; i++){
	        if(systemIdIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same stringValue.
	    }
	    
	    systemIdTable[removeIndex] = null;
	}
	
	void handlePublicIdTableRemove(int recordIndex){
	    int removeIndex = publicIdIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < publicIdIndexTable.length; i++){
	        if(publicIdIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same stringValue.
	    }
	    
	    publicIdTable[removeIndex] = null;
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
	    int[] copyItemIdTable = new int[itemIdTable.length];
	    System.arraycopy(itemIdTable, 0, copyItemIdTable, 0, itemIdTable.length);
	    
	    Map<String, String>[] copyDeclaredXmlns = new Map[declaredXmlns.length];
	    System.arraycopy(declaredXmlns, 0, copyDeclaredXmlns, 0, declaredXmlns.length);
	    
	    int[] copyItemDescriptionIndexTable = new int[itemDescriptionIndexTable.length];
	    System.arraycopy(itemDescriptionIndexTable, 0, copyItemDescriptionIndexTable, 0, itemDescriptionIndexTable.length);
	    	        
	    int[] copyLocalNameIndexTable = new int[localNameIndexTable.length];
	    System.arraycopy(localNameIndexTable, 0, copyLocalNameIndexTable, 0, localNameIndexTable.length);
	    
	    int[] copyNamespaceURIIndexTable = new int[namespaceURIIndexTable.length];
	    System.arraycopy(namespaceURIIndexTable, 0, copyNamespaceURIIndexTable, 0, namespaceURIIndexTable.length);
	    
	    int[] copyAttributeTypeIndexTable = new int[attributeTypeIndexTable.length];
	    System.arraycopy(attributeTypeIndexTable, 0, copyAttributeTypeIndexTable, 0, attributeTypeIndexTable.length);
	    
	    int[] copyStringValueIndexTable = new int[stringValueIndexTable.length];
	    System.arraycopy(stringValueIndexTable, 0, copyStringValueIndexTable, 0, stringValueIndexTable.length);
	    
	    char[][] copyCharArrayValueTable = new char[charArrayValueTable.length][];
	    System.arraycopy(charArrayValueTable, 0, copyCharArrayValueTable, 0, charArrayValueTable.length);
	    
	    int[] copySystemIdIndexTable = new int[systemIdIndexTable.length];
	    System.arraycopy(systemIdIndexTable, 0, copySystemIdIndexTable, 0, systemIdIndexTable.length);
	    
	    int[] copyPublicIdIndexTable = new int[publicIdIndexTable.length];
	    System.arraycopy(publicIdIndexTable, 0, copyPublicIdIndexTable, 0, publicIdIndexTable.length);
	    
	    int[] copyLineNumberTable = new int[lineNumberTable.length];
	    System.arraycopy(lineNumberTable, 0, copyLineNumberTable, 0, lineNumberTable.length);
	    
	    int[] copyColumnNumberTable = new int[columnNumberTable.length];
	    System.arraycopy(columnNumberTable, 0, copyColumnNumberTable, 0, columnNumberTable.length);
	    
	    
	    
	    
	    String[] copyItemDescriptionTable = new String[itemDescriptionTable.length];
	    System.arraycopy(itemDescriptionTable, 0, copyItemDescriptionTable, 0, itemDescriptionTable.length);
	    	    
	    String[] copyLocalNameTable = new String[localNameTable.length];
	    System.arraycopy(localNameTable, 0, copyLocalNameTable, 0, localNameTable.length);
	    
	    String[] copyNamespaceURITable = new String[namespaceURITable.length];
	    System.arraycopy(namespaceURITable, 0, copyNamespaceURITable, 0, namespaceURITable.length);
	    
	    String[] copyAttributeTypeTable = new String[attributeTypeTable.length];
	    System.arraycopy(attributeTypeTable, 0, copyAttributeTypeTable, 0, attributeTypeTable.length);
	    
	    String[] copyValueTable = new String[stringValueTable.length];
	    System.arraycopy(stringValueTable, 0, copyValueTable, 0, stringValueTable.length);
	    
	    String[] copySystemIdTable = new String[systemIdTable.length];
	    System.arraycopy(systemIdTable, 0, copySystemIdTable, 0, systemIdTable.length);
	    
	    String[] copyPublicIdTable = new String[publicIdTable.length];
	    System.arraycopy(publicIdTable, 0, copyPublicIdTable, 0, publicIdTable.length);
	    
	    DocumentIndexedData did = new DocumentIndexedDataImpl(
	                        copyItemIdTable,
                            copyDeclaredXmlns,
                            copyItemDescriptionIndexTable,
                            copyLocalNameIndexTable,
                            copyNamespaceURIIndexTable,    
                            copyAttributeTypeIndexTable,
                            copyStringValueIndexTable,   
                            copyCharArrayValueTable,
                            copySystemIdIndexTable,
                            copyPublicIdIndexTable, 	
                            copyLineNumberTable,
                            copyColumnNumberTable,
                                
                            copyItemDescriptionTable,	
                            copyLocalNameTable,
                            copyNamespaceURITable,
                            copyAttributeTypeTable,
                            copyValueTable,
                            copySystemIdTable,
                            copyPublicIdTable);	  
        
        return did;
	}
	
	
	
	
	
	
	public String[] getItemDescription(int[] recordIndex, int offset, int count){
	    String[] result = new String[count];
	    for(int i = offset; i < offset+count; i++){
	        result[i] = itemDescriptionTable[itemDescriptionIndexTable[recordIndex[i]]];
	    }
		return result;
	}
    
    public String[] getNamespaceURI(int[] recordIndex, int offset, int count){
        String[] result = new String[count];
        for(int i = offset; i < offset+count; i++){
	        result[i] = namespaceURITable[namespaceURIIndexTable[recordIndex[i]]];
	    }
		return result;
	}
	
	public String[] getValue(int[] recordIndex, int offset, int count){
        String[] result = new String[count];
        for(int i = offset; i < offset+count; i++){
	        result[i] = stringValueTable[stringValueIndexTable[recordIndex[i]]];
	    }
		return result;
	}
    
    public String[] getLocalName(int[] recordIndex, int offset, int count){
        String[] result = new String[count];
        for(int i = offset; i < offset+count; i++){
	        result[i] = localNameTable[localNameIndexTable[recordIndex[i]]];
	    }
		return result;
	}
	
	public String[] getSystemId(int[] recordIndex, int offset, int count){
	    String[] result = new String[count];
        for(int i = offset; i < offset+count; i++){
	        result[i] = systemIdTable[systemIdIndexTable[recordIndex[i]]];
	    }
		return result;
	}
	
	public String[] getPublicId(int[] recordIndex, int offset, int count){
	    String[] result = new String[count];
        for(int i = offset; i < offset+count; i++){
	        result[i] = publicIdTable[publicIdIndexTable[recordIndex[i]]];
	    }
		return result;
	}
	
	public int[] getLineNumber(int[] recordIndex, int offset, int count){
	    int[] result = new int[count];
        for(int i = offset; i < offset+count; i++){
	        result[i] = lineNumberTable[recordIndex[i]];
	    }
		return result;
	}
	
	public int[] getColumnNumber(int[] recordIndex, int offset, int count){
	    int[] result = new int[count];
        for(int i = offset; i < offset+count; i++){
	        result[i] = columnNumberTable[recordIndex[i]];
	    }
		return result;
	}

	public int[] getItemId(int[] recordIndex, int offset, int count){
	    int[] result = new int[count];
        for(int i = offset; i < offset+count; i++){
	        result[i] = itemIdTable[recordIndex[i]];
	    }
		return result;
	}
	
	public String[] getItemDescription(int[] recordIndex){
	    String[] result = new String[recordIndex.length];
	    for(int i = 0; i < recordIndex.length; i++){
	        result[i] = itemDescriptionTable[itemDescriptionIndexTable[recordIndex[i]]];
	    }
		return result;
	}
    
    public String[] getNamespaceURI(int[] recordIndex){
        String[] result = new String[recordIndex.length];
        for(int i = 0; i < recordIndex.length; i++){
	        result[i] = namespaceURITable[namespaceURIIndexTable[recordIndex[i]]];
	    }
		return result;
	}
	
	public String[] getValue(int[] recordIndex){
        String[] result = new String[recordIndex.length];
        for(int i = 0; i < recordIndex.length; i++){
	        result[i] = stringValueTable[stringValueIndexTable[recordIndex[i]]];
	    }
		return result;
	}
    
    public String[] getLocalName(int[] recordIndex){
        String[] result = new String[recordIndex.length];
        for(int i = 0; i < recordIndex.length; i++){
	        result[i] = localNameTable[localNameIndexTable[recordIndex[i]]];
	    }
		return result;
	}
	
	public String[] getSystemId(int[] recordIndex){
	    String[] result = new String[recordIndex.length];
        for(int i = 0; i < recordIndex.length; i++){
	        result[i] = systemIdTable[systemIdIndexTable[recordIndex[i]]];
	    }
		return result;
	}
	
	public String[] getPublicId(int[] recordIndex){
	    String[] result = new String[recordIndex.length];
        for(int i = 0; i < recordIndex.length; i++){
	        result[i] = publicIdTable[publicIdIndexTable[recordIndex[i]]];
	    }
		return result;
	}
	
	public int[] getLineNumber(int[] recordIndex){
	    int[] result = new int[recordIndex.length];
        for(int i = 0; i < recordIndex.length; i++){
	        result[i] = lineNumberTable[recordIndex[i]];
	    }
		return result;
	}
	
	public int[] getColumnNumber(int[] recordIndex){
	    int[] result = new int[recordIndex.length];
        for(int i = 0; i < recordIndex.length; i++){
	        result[i] = columnNumberTable[recordIndex[i]];
	    }
		return result;
	}

	public int[] getItemId(int[] recordIndex){
	    int[] result = new int[recordIndex.length];
        for(int i = 0; i < recordIndex.length; i++){
	        result[i] = itemIdTable[recordIndex[i]];
	    }
		return result;
	}
}
