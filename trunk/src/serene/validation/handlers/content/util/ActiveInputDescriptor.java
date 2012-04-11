package serene.validation.handlers.content.util;

import java.util.Arrays;

import serene.validation.handlers.structure.impl.*;
import serene.validation.handlers.error.*;


/**
* Stores location and item identification data for the document items 
* involved in validation. 
*/
public class ActiveInputDescriptor{
            
    int[] recordedClientsCount;
    int[] itemIdTable;
    int[] itemDescriptionIndexTable;
    int[] localNameIndexTable;
    int[] namespaceURIIndexTable;
    int[] systemIdIndexTable;
    int[] publicIdIndexTable; 	
	int[] lineNumberTable;
	int[] columnNumberTable;
	int startSearch;
	int initialSize;
	int increaseSizeAmount;
	
	/**
	* Indicates an open place in the array.
	*/
	final int AVAILABLE = -1;
	
	/**
	* Always keep first record free for null values that actually need to be stored.
	* When other positions hold a null value it means they are free.
	*/
	final int NULL_INDEX = 0;	
	String[] itemDescriptionTable;
	String[] localNameTable;
	String[] namespaceURITable;
	String[] systemIdTable;
	String[] publicIdTable;
	
	//int used = 0;
	public ActiveInputDescriptor(){
	    startSearch = 0;
	    initialSize = 10;
	    increaseSizeAmount = 20;
                
        recordedClientsCount = new int[initialSize];
        Arrays.fill(recordedClientsCount, 0);
	    itemIdTable = new int[initialSize];
        itemDescriptionIndexTable = new int[initialSize];
        localNameIndexTable = new int[initialSize];
        namespaceURIIndexTable = new int[initialSize];
        systemIdIndexTable = new int[initialSize];
        publicIdIndexTable = new int[initialSize]; 	
        lineNumberTable = new int[initialSize];
        columnNumberTable = new int[initialSize];
        
        
        itemDescriptionTable = new String[5];
        localNameTable = new String[5];
        namespaceURITable = new String[5];
        systemIdTable = new String[5];
        publicIdTable = new String[5];
	}
	
	/**
	* Record the data returning the index of the record. It does not register client.	
	*/
	int recordItem(int itemId,
                            String itemDescription,
                            String localName,
                            String namespaceURI,
                            String systemId,
                            String publicId, 	
                            int lineNumber,
                            int columnNumber){
        for(int i = startSearch; i  < recordedClientsCount.length ; i++){
            if(recordedClientsCount[i] == 0){
                itemIdTable[i] = itemId;
                itemDescriptionIndexTable[i] = getItemDescriptionIndex(itemDescription);
                localNameIndexTable[i] = getLocalNameIndex(localName);
                namespaceURIIndexTable[i] = getNamespaceURIIndex(namespaceURI);
                systemIdIndexTable[i] = getSystemIdIndex(systemId);
                publicIdIndexTable[i] = getPublicIdIndex(publicId); 	
                lineNumberTable[i] = lineNumber;
                columnNumberTable[i] = columnNumber;
                startSearch = i;
                return i;
            }
        }
    
	    
        
        int oldSize = recordedClientsCount.length;
        int newSize = oldSize + increaseSizeAmount;		    
        
        int[] increasedRCC = new int[newSize];
        System.arraycopy(recordedClientsCount, 0, increasedRCC, 0, oldSize);
        recordedClientsCount = increasedRCC;
        Arrays.fill(recordedClientsCount, oldSize, newSize, 0);
        
        int[] increasedII = new int[newSize];
        System.arraycopy(itemIdTable, 0, increasedII, 0, oldSize);
        itemIdTable = increasedII;
        
        int[] increasedQN = new int[newSize];
        System.arraycopy(itemDescriptionIndexTable, 0, increasedQN, 0, oldSize);
        itemDescriptionIndexTable = increasedQN;
        
        int[] increasedLocalName = new int[newSize];
        System.arraycopy(localNameIndexTable, 0, increasedLocalName, 0, oldSize);
        localNameIndexTable = increasedLocalName;
        
        int[] increasedNsURI = new int[newSize];
        System.arraycopy(namespaceURIIndexTable, 0, increasedNsURI, 0, oldSize);
        namespaceURIIndexTable = increasedNsURI;
        
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
    
		itemIdTable[oldSize] = itemId;
        itemDescriptionIndexTable[oldSize] = getItemDescriptionIndex(itemDescription);
        localNameIndexTable[oldSize] = getLocalNameIndex(localName);
        namespaceURIIndexTable[oldSize] = getNamespaceURIIndex(namespaceURI);
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
	            throw new IllegalStateException();
	        }
	    }	    
	    
	   //if(hasLeftOvers) System.out.println(hashCode()+" LEFT OVERS------------------------------------------");
	   //else System.out.println(hashCode()+" ----");
	}
	public void registerClientForRecord(int recordIndex, Object o){
	    recordedClientsCount[recordIndex] += 1;
	    //System.out.println("+++ 1 recordIndex="+recordIndex+" clientsCount="+recordedClientsCount[recordIndex]+" "+o.hashCode()+" "+o.getClass().getName());
	    
	    /*if(o instanceof ConflictMessageHandler
	        && recordIndex == 4 
	        && recordedClientsCount[recordIndex] == 8) throw new IllegalStateException();*/
	}
	
	
	public void registerClientForRecord(int[] recordIndex, Object o){
	    //System.out.println("+++ 2 recordIndex="+Arrays.toString(recordIndex)+" "+o.hashCode()+" "+o.getClass().getName());
	    for(int i = 0; i < recordIndex.length; i++){
	        recordedClientsCount[recordIndex[i]] += 1;
	        //System.out.println("+++ 2 recordIndex="+recordIndex[i]+" clientsCount="+recordedClientsCount[recordIndex[i]]+" "+o.hashCode()+" "+o.getClass().getName());
	    }	    
	    //System.out.println("+++ 2 indexes="+Arrays.toString(recordedClientsCount));
	}
	
	public void registerClientForRecord(int[] recordIndex, int offset, int count, Object o){
	    //System.out.println("+++ 3 recordIndex="+Arrays.toString(recordIndex)+" offset="+offset+" count="+count+" "+o.hashCode()+" "+o.getClass().getName());
	    for(int i = offset; i < offset+count; i++){
	        recordedClientsCount[recordIndex[i]] += 1;
	        //System.out.println(i+"+++ 3 recordIndex="+recordIndex[i]+" clientsCount="+recordedClientsCount[recordIndex[i]]+" "+o.hashCode()+" "+o.getClass().getName());
	    }
	    //System.out.println("+++ 3 indexes="+Arrays.toString(recordedClientsCount));
	}
	
	public void unregisterClientForRecord(int recordIndex, Object o){
	    //System.out.println("--- 1 recordIndex="+recordIndex+" "+o.hashCode()+" "+o.getClass().getName());    
	    recordedClientsCount[recordIndex] -= 1;
	    //String s = o.getClass().getName(); 
	    //System.out.println("--- 1 recordIndex="+recordIndex+" clientsCount="+recordedClientsCount[recordIndex]+" "+o.hashCode()+" "+o.getClass().getName());
	   
	    /*if(o instanceof ConflictMessageHandler
	        && recordIndex == 4 
	        && recordedClientsCount[recordIndex] == 1) throw new IllegalStateException();*/
	    if(recordedClientsCount[recordIndex] == 0){
	        //System.out.println("--- 1 remove "+recordIndex);
	        handleItemDescriptionTableRemove(recordIndex);
	        handleLocalNameTableRemove(recordIndex);
	        handleNamespaceURITableRemove(recordIndex);
	        handleSystemIdTableRemove(recordIndex);
	        handlePublicIdTableRemove(recordIndex);
	        
	        
	        itemDescriptionIndexTable[recordIndex] = AVAILABLE;
	        itemIdTable[recordIndex] = AVAILABLE;
	        localNameIndexTable[recordIndex] = AVAILABLE;
	        namespaceURIIndexTable[recordIndex] = AVAILABLE;
	        systemIdIndexTable[recordIndex] = AVAILABLE;
	        publicIdIndexTable[recordIndex] = AVAILABLE;
	        lineNumberTable[recordIndex] = AVAILABLE;
	        columnNumberTable[recordIndex] = AVAILABLE;
	        
	        if(recordIndex < startSearch) startSearch = recordIndex;
	    }else if(recordedClientsCount[recordIndex] < 0){
	        throw new IllegalStateException();
	    }
	}
	public void unregisterClientForRecord(int[] recordIndex, Object o){
	    //System.out.println("--- 2 recordIndex="+Arrays.toString(recordIndex)+" "+o.hashCode()+" "+o.getClass().getName());
	    for(int i = 0; i < recordIndex.length; i++){
            recordedClientsCount[recordIndex[i]] -= 1;            
            //System.out.println("--- 2 recordIndex="+recordIndex[i]+" clientsCount="+recordedClientsCount[recordIndex[i]]+" "+o.hashCode()+" "+o.getClass().getName());
            if(recordedClientsCount[recordIndex[i]] == 0){	
                //System.out.println("--- 2 remove "+recordIndex[i]);
                handleItemDescriptionTableRemove(recordIndex[i]);
                handleLocalNameTableRemove(recordIndex[i]);
                handleNamespaceURITableRemove(recordIndex[i]);
                handleSystemIdTableRemove(recordIndex[i]);
                handlePublicIdTableRemove(recordIndex[i]);
                
                
                itemIdTable[recordIndex[i]] = AVAILABLE;
                itemIdTable[recordIndex[i]] = AVAILABLE;
                localNameIndexTable[recordIndex[i]] = AVAILABLE;
                namespaceURIIndexTable[recordIndex[i]] = AVAILABLE;
                systemIdIndexTable[recordIndex[i]] = AVAILABLE;
                publicIdIndexTable[recordIndex[i]] = AVAILABLE;
                lineNumberTable[recordIndex[i]] = AVAILABLE;
                columnNumberTable[recordIndex[i]] = AVAILABLE;
                
                if(recordIndex[i] < startSearch) startSearch = recordIndex[i];
            }else if(recordedClientsCount[recordIndex[i]] < 0){
                throw new IllegalStateException();
            }
        }
	}
	public void unregisterClientForRecord(int[] recordIndex, int offset, int count, Object o){
	    //System.out.println("--- 3 recordIndex="+recordIndex+" "+o.hashCode()+" "+o.getClass().getName());
	    for(int i = offset; i < offset+count; i++){
            recordedClientsCount[recordIndex[i]] -= 1;
            //System.out.println("--- 3 recordIndex="+recordIndex[i]+" clientsCount="+recordedClientsCount[recordIndex[i]]+" "+o.hashCode()+" "+o.getClass().getName());
            if(recordedClientsCount[recordIndex[i]] == 0){	
                //System.out.println("--- 3 remove "+recordIndex[i]);
                handleItemDescriptionTableRemove(recordIndex[i]);
                handleLocalNameTableRemove(recordIndex[i]);
                handleNamespaceURITableRemove(recordIndex[i]);
                handleSystemIdTableRemove(recordIndex[i]);
                handlePublicIdTableRemove(recordIndex[i]);
                                
                itemIdTable[recordIndex[i]] = AVAILABLE;
                itemIdTable[recordIndex[i]] = AVAILABLE;
                localNameIndexTable[recordIndex[i]] = AVAILABLE;
                namespaceURIIndexTable[recordIndex[i]] = AVAILABLE;
                systemIdIndexTable[recordIndex[i]] = AVAILABLE;
                publicIdIndexTable[recordIndex[i]] = AVAILABLE;
                lineNumberTable[recordIndex[i]] = AVAILABLE;
                columnNumberTable[recordIndex[i]] = AVAILABLE;
                
                if(recordIndex[i] < startSearch) startSearch = recordIndex[i];
            }else if(recordedClientsCount[recordIndex[i]] < 0){
                throw new IllegalStateException();
            }
        }
	}
	
	
	
	void handleItemDescriptionTableRemove(int recordIndex){
	    int removeIndex = itemDescriptionIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < itemDescriptionIndexTable.length; i++){
	        if(itemDescriptionIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same value.
	    }
	    
	    itemDescriptionTable[removeIndex] = null;
	}
	
	void handleLocalNameTableRemove(int recordIndex){
	    int removeIndex = localNameIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < localNameIndexTable.length; i++){
	        if(localNameIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same value.
	    }
	    
	    localNameTable[removeIndex] = null;
	}
	
	void handleNamespaceURITableRemove(int recordIndex){
	    int removeIndex = namespaceURIIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < namespaceURIIndexTable.length; i++){
	        if(namespaceURIIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same value.
	    }
	    
	    namespaceURITable[removeIndex] = null;
	}
	
	void handleSystemIdTableRemove(int recordIndex){
	    int removeIndex = systemIdIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < systemIdIndexTable.length; i++){
	        if(systemIdIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same value.
	    }
	    
	    systemIdTable[removeIndex] = null;
	}
	
	void handlePublicIdTableRemove(int recordIndex){
	    int removeIndex = publicIdIndexTable[recordIndex];
	    
	    if(removeIndex == NULL_INDEX) return; 
	    
	    for(int i = 0; i < publicIdIndexTable.length; i++){
	        if(publicIdIndexTable[i] == removeIndex && i != recordIndex) return;
	        // There are other clients recorded for the same value.
	    }
	    
	    publicIdTable[removeIndex] = null;
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

	public int getItemId(int recordIndex){
	    return itemIdTable[recordIndex];
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
