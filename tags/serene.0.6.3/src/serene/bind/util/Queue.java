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

package serene.bind.util;

import java.util.Arrays;

import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;

import serene.bind.DocumentTaskContext;
import serene.bind.ElementTaskContext;
import serene.bind.AttributeTaskContext;
import serene.bind.DocumentTask;
import serene.bind.ElementTask;
import serene.bind.AttributeTask;


import serene.Reusable;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

public class Queue implements Reusable, 
                                DocumentTaskContext,
                                ElementTaskContext,
                                AttributeTaskContext{

    // TODO 
    // Clarify the role of Queue as context -> is it some kind of connection to 
    // the (indexed) docuemnt data accesed by way of bound definitions? Do 
    // definitions actually play any role? In case they don't maybe an 
    // intermediary is unnecessary.
    
	QueuePool pool;
	
	int currentIndex;
	int size;
	
	int currentAttributeIndex;

	
	int documentInputIndex;
	DocumentTask startDocumentTask;
	DocumentTask endDocumentTask;
	
    int[] elementInputIndex;    
	int[][] attributeInputIndex;
	AttributeTask[][] attributeTask;
	//Implementation [queue record index of corresponding start element][input record]
	ElementTask[] elementTask;
	String[] characterContent;
	//Implementation [queue record index of corresponding start element][input record]	
	int[] correspStartIndex;
	int[] correspEndIndex;
	// NOTE:
	// This type of recording with start and end element has the advantage of 
	// allowing to record the end element location, maybe also other data. If
	// not for that it would be more simple.
		
	
	int attributeInitialSize;
	int attributeIncreaseSize;
	
	int characterContentInitialSize;
	int characterContentIncreaseSize;

	int[] reservationStartEntry;
	int[] reservationEntriesCount;
	boolean[] reservationUsed;
	int reservationIndex;
	int reservationSize;
		
	boolean isClear;	
	
	ActiveInputDescriptor activeInputDescriptor;
	
	DocumentIndexedData documentIndexedData;
	
	Queue(ActiveInputDescriptor activeInputDescriptor, QueuePool pool){
		this.pool = pool;
		this.activeInputDescriptor = activeInputDescriptor;
		
		currentIndex = -1;
		size = 100;
				
		documentInputIndex = -1;
		
		currentAttributeIndex = -1;
		
		
        elementInputIndex = new int[size];
        Arrays.fill(elementInputIndex, -1);
		
        attributeInputIndex = new int[size][];
		attributeTask = new AttributeTask[size][];
		elementTask = new ElementTask[size];
		characterContent = new String[size];
		correspStartIndex = new int[size];		
		Arrays.fill(correspStartIndex, -1);
		correspEndIndex = new int[size];
		Arrays.fill(correspEndIndex, -1);
		
		
		attributeInitialSize = 10;
		attributeIncreaseSize = 10;
		
		characterContentInitialSize = 10;
		characterContentIncreaseSize = 10;
		
		reservationIndex = -1;
		reservationSize = 5;
		
		reservationStartEntry = new int[reservationSize];
		Arrays.fill(reservationStartEntry, -1);
		reservationEntriesCount = new int[reservationSize];
		Arrays.fill(reservationEntriesCount, -1);		
		reservationUsed = new boolean[reservationSize];
		//Arrays.fill(reservationUsed, false);
		
		isClear = true;
	}	
	
	public void clear(){
		// System.out.println(hashCode()+" CLEAR ");		
		currentIndex = -1;
		currentAttributeIndex = -1;
		
		isClear = true;
		
		clearContent();				
	}
	
	
	void clearContent(){
        for(int i = 0; i < elementInputIndex.length; i++){
            if(elementInputIndex[i] != -1){
                activeInputDescriptor.unregisterClientForRecord(elementInputIndex[i], this);
                elementInputIndex[i] = -1;
            }
        }
        for(int i = 0; i < attributeTask.length; i++){
            if(attributeInputIndex[i] != null){
                for(int j = 0; j < attributeTask[i].length; j++){
                    if(attributeInputIndex[i][j] != -1){
                        activeInputDescriptor.unregisterClientForRecord(attributeInputIndex[i][j], this);
                    }
                }
            }
            attributeTask[i] = null;
            attributeInputIndex[i] = null;
        }				
        
        Arrays.fill(characterContent, null);
		Arrays.fill(elementTask, null);
		Arrays.fill(correspStartIndex, -1);
		Arrays.fill(correspEndIndex, -1);
		
		reservationIndex = -1;
				
		Arrays.fill(reservationStartEntry, -1);
		Arrays.fill(reservationEntriesCount, -1);
		Arrays.fill(reservationUsed, false);
		
		startDocumentTask = null;
		endDocumentTask = null;
		if(documentInputIndex != -1) activeInputDescriptor.unregisterClientForRecord(documentInputIndex, this);
		documentInputIndex = -1;
		
		
		documentIndexedData = null;
	}
	
	void init(){	    		
		isClear = false;
	}
		
	public void recycle(){
		if(!isClear)clear();
		pool.recycle(this);
	}
	
	public void setProperty(String name, Object value) throws SAXNotRecognizedException{
	    if(name == null)throw new NullPointerException();
		throw new SAXNotRecognizedException("UNKNOWN PROPERTY.");
	}
	
	public Object getProperty(String name)  throws SAXNotRecognizedException{
		if(name == null)throw new NullPointerException();
		throw new SAXNotRecognizedException("UNKNOWN PROPERTY.");
	}
	
	public void setFeature(String name, boolean value)  throws SAXNotRecognizedException{	
		if (name == null) {
            throw new NullPointerException();
        }
		throw new SAXNotRecognizedException("UNKNOWN FEATURE.");
	}
	
	public boolean getFeature(String name) throws SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }
		throw new SAXNotRecognizedException("UNKNOWN FEATURE.");
	}
	
	public void addStartDocument(int inputIndex, DocumentTask startTask){
	    documentInputIndex = inputIndex;
	    activeInputDescriptor.registerClientForRecord(inputIndex, this);
	    startDocumentTask = startTask;	    
	}
	public void addStartDocument(DocumentTask startTask){
	    startDocumentTask = startTask;
	}
	public void addStartDocument(int inputIndex){
	    documentInputIndex = inputIndex;
	    activeInputDescriptor.registerClientForRecord(inputIndex, this);
	}	
	public void addEndDocument(DocumentTask endTask){
	    endDocumentTask = endTask;
	}
	
	public int addStartElement(int inputIndex, ElementTask task){
	    if(++currentIndex == size)increaseSize();
		correspStartIndex[currentIndex] = -1;
		
		elementInputIndex[currentIndex] = inputIndex;
		activeInputDescriptor.registerClientForRecord(inputIndex, this);
		elementTask[currentIndex] = task;
		
		return currentIndex;
	}
	
	public int addStartElement(int inputIndex){
	    if(++currentIndex == size)increaseSize();
		correspStartIndex[currentIndex] = -1;
		
		elementInputIndex[currentIndex] = inputIndex;
		activeInputDescriptor.registerClientForRecord(inputIndex, this);
		
		return currentIndex;
	}
	
	public int addStartElement(ElementTask task){
	    if(++currentIndex == size)increaseSize();
		correspStartIndex[currentIndex] = -1;
		
		elementInputIndex[currentIndex] = -1;
		elementTask[currentIndex] = task;
		
		return currentIndex;
	}
	
	public int addEndElement(int startElementQIndex, ElementTask task){
	    if(++currentIndex == size)increaseSize();
	        
	    correspStartIndex[currentIndex] = startElementQIndex;
		correspEndIndex[startElementQIndex] = currentIndex;
		
		elementTask[currentIndex] = task;
		
		return currentIndex;
	}
	
	
	public void addAttribute(int startElementQIndex, int inputIndex, AttributeTask task){
		
		if(attributeInputIndex[startElementQIndex] == null ){
            attributeInputIndex[startElementQIndex] = new int[attributeInitialSize];
            attributeInputIndex[startElementQIndex][0] = inputIndex;            
            activeInputDescriptor.registerClientForRecord(inputIndex, this);
            Arrays.fill(attributeInputIndex[startElementQIndex], 1, attributeInitialSize, -1);
            
            attributeTask[startElementQIndex] = new AttributeTask[attributeInitialSize];
            attributeTask[startElementQIndex][0] = task;
        }else{
            for(int i = 0; i < attributeTask[startElementQIndex].length; i++){
                if(attributeTask[startElementQIndex][i] == null){
                    attributeTask[startElementQIndex][i] = task;
                    attributeInputIndex[startElementQIndex][i] = inputIndex;                    
                    activeInputDescriptor.registerClientForRecord(inputIndex, this);
                    return;
                }
            }
        }        
        
        int length = attributeInputIndex[startElementQIndex].length;
        
        int[] increasedAII = new int[length+attributeIncreaseSize];
        System.arraycopy(attributeInputIndex[startElementQIndex], 0, increasedAII, 0, length);
        attributeInputIndex[startElementQIndex] = increasedAII;
        attributeInputIndex[startElementQIndex][length] = inputIndex;
		activeInputDescriptor.registerClientForRecord(inputIndex, this);
		Arrays.fill(attributeInputIndex[startElementQIndex], length+1, length+attributeIncreaseSize, -1);
		
        AttributeTask[] increasedAT = new AttributeTask[length+attributeIncreaseSize];
        System.arraycopy(attributeTask[startElementQIndex], 0, increasedAT, 0, length);
        attributeTask[startElementQIndex] = increasedAT;
        attributeTask[startElementQIndex][length] = task;
	}
	
	public void addAttribute(int startElementQIndex, AttributeTask task){
		
		if(attributeInputIndex[startElementQIndex] == null ){
            attributeInputIndex[startElementQIndex] = new int[attributeInitialSize];
            Arrays.fill(attributeInputIndex[startElementQIndex], 0, attributeInitialSize, -1);
            
            attributeTask[startElementQIndex] = new AttributeTask[attributeInitialSize];
            attributeTask[startElementQIndex][0] = task;
        }else{
            for(int i = 0; i < attributeTask[startElementQIndex].length; i++){
                if(attributeTask[startElementQIndex][i] == null){
                    attributeTask[startElementQIndex][i] = task;
                    attributeInputIndex[startElementQIndex][i] = -1;
                    return;
                }
            }
        }        
        
        int length = attributeInputIndex[startElementQIndex].length;
        
        int[] increasedAII = new int[length+attributeIncreaseSize];
        System.arraycopy(attributeInputIndex[startElementQIndex], 0, increasedAII, 0, length);
        attributeInputIndex[startElementQIndex] = increasedAII;
        Arrays.fill(attributeInputIndex[startElementQIndex], length, length+attributeIncreaseSize, -1);
        
        AttributeTask[] increasedAT = new AttributeTask[length+attributeIncreaseSize];
        System.arraycopy(attributeTask[startElementQIndex], 0, increasedAT, 0, length);
        attributeTask[startElementQIndex] = increasedAT;
        attributeTask[startElementQIndex][length] = task;
	}
	
	public void addCharacterContent(int startElementQIndex, String cc){				
		characterContent[startElementQIndex] = cc;        
	}
	
	
	/**
	* Creates places in the queue starting from the current index, it is the 
	* caller's responsibility to make sure the currentIndex corresponds to a 
	* start of an element and the number of reserved entries is that needed the 
	* entire subtree until the end of the element, inclusively. The currentIndex 
	* is set to the last reserved index if that is greater. By default the 
	* reservationUsed is set to false.
	*/
	public void registerReservation(int reservationStartIndex, int entriesCount){	
	    
        boolean startIsReserved = false;
        int i = 0;
		for(; i < reservationStartEntry.length; i++){
		    if(reservationStartEntry[i] == reservationStartIndex){
		        startIsReserved = true;
		        break;
		    }
		}
		
		if(startIsReserved){
		    reservationEntriesCount[i] = entriesCount;
		    reservationUsed[i] = false;
		}else{		
            if(++reservationIndex == reservationSize) increaseReservation();
            reservationStartEntry[reservationIndex] = reservationStartIndex;
            reservationEntriesCount[reservationIndex] = entriesCount;
            reservationUsed[reservationIndex] = false;
        }
        
        
        
        if(reservationStartIndex+entriesCount >= size){
            increaseSize(entriesCount);
        }
    
				
		int lastIndex = reservationStartIndex + entriesCount-1;
		
		correspEndIndex[reservationStartIndex] = lastIndex;
		correspStartIndex[lastIndex] = reservationStartIndex;		
				
		
		
		if(currentIndex < lastIndex){
	        for(i = reservationStartIndex; i <= currentIndex; i++){
	            if(elementInputIndex[i] != -1){
	                activeInputDescriptor.unregisterClientForRecord(elementInputIndex[i], this);
	                elementInputIndex[i] = -1;
	            }
	            if(attributeInputIndex[i] != null){
	                for(int j = 0; j < attributeInputIndex[i].length; j++){
	                    if(attributeInputIndex[i][j] != -1){
	                        activeInputDescriptor.unregisterClientForRecord(attributeInputIndex[i][j], this);
	                    }
	                }
	                attributeInputIndex[i] = null;
	                attributeTask[i] = null;
	            }
	        }
	        currentIndex = lastIndex;
	    }else{
	        // clear from reservationStartIndex to reservationStartIndex+entriesCount exlcusive
	        for(i = reservationStartIndex; i <= lastIndex; i++){
	            if(elementInputIndex[i] != -1){
	                activeInputDescriptor.unregisterClientForRecord(elementInputIndex[i], this);
	                elementInputIndex[i] = -1;
	            }
	            if(attributeInputIndex[i] != null){
	                for(int j = 0; j < attributeInputIndex[i].length; j++){
	                    if(attributeInputIndex[i][j] != -1){
	                        activeInputDescriptor.unregisterClientForRecord(attributeInputIndex[i][j], this);
	                    }
	                }
	                attributeInputIndex[i] = null;
	                attributeTask[i] = null;
	            }
	        }
	    }
	}
	
	
	/**
	* Creates places in the queue starting from the current index, it is the 
	* caller's responsibility to make sure the currentIndex corresponds to a 
	* start of an element and the number of reserved entries is that needed the 
	* entire subtree until the end of the element, inclusively. When creating
	* the reserved places any existing data is removed. The currentIndex is set 
	* to the last reserved index if that is greater. 
	*/
	public void registerReservation(int reservationStartIndex, int entriesCount, boolean isUsed){			 
		boolean startIsReserved = false;
		int i = 0;
		for(; i < reservationStartEntry.length; i++){
		    // TODO 
		    // Think about what when the index is in a reserved strech, but not 
		    // the start. Might actually want to forbid any type of reservation 
		    // overlap.
		    if(reservationStartEntry[i] == reservationStartIndex){
		        startIsReserved = true;
		        break;
		    }
		}
		
		if(startIsReserved){
		    reservationEntriesCount[i] = entriesCount;
		    reservationUsed[i] = isUsed;
		}else{		
            if(++reservationIndex == reservationSize) increaseReservation();
            reservationStartEntry[reservationIndex] = reservationStartIndex;
            reservationEntriesCount[reservationIndex] = entriesCount;
            reservationUsed[reservationIndex] = isUsed;
        }
              
        
        if(reservationStartIndex+entriesCount >= size)increaseSize(entriesCount);
    				
				
		int lastIndex = reservationStartIndex + entriesCount-1;
		
		correspEndIndex[reservationStartIndex] = lastIndex;
		correspStartIndex[lastIndex] = reservationStartIndex;	
		
	    if(currentIndex < lastIndex){
	        // clear from reservationStartIndex to currentIndex inlcusive
	        for(i = reservationStartIndex; i <= currentIndex; i++){
	            if(elementInputIndex[i] != -1){
	                activeInputDescriptor.unregisterClientForRecord(elementInputIndex[i], this);
	                elementInputIndex[i] = -1;
	            }
	            if(attributeInputIndex[i] != null){
	                for(int j = 0; j < attributeInputIndex[i].length; j++){
	                    if(attributeInputIndex[i][j] != -1){
	                        activeInputDescriptor.unregisterClientForRecord(attributeInputIndex[i][j], this);
	                    }
	                }
	                attributeInputIndex[i] = null;
	                attributeTask[i] = null;
	            }
	        }
	        currentIndex = lastIndex;
	    }else{
	        // clear from reservationStartIndex to reservationStartIndex+entriesCount exlcusive
	        for(i = reservationStartIndex; i < reservationStartIndex+entriesCount; i++){
	            if(elementInputIndex[i] != -1){
	                activeInputDescriptor.unregisterClientForRecord(elementInputIndex[i], this);
	                elementInputIndex[i] = -1;
	            }
	            if(attributeInputIndex[i] != null){
	                for(int j = 0; j < attributeInputIndex[i].length; j++){
	                    if(attributeInputIndex[i][j] != -1){
	                        activeInputDescriptor.unregisterClientForRecord(attributeInputIndex[i][j], this);
	                    }
	                }
	                attributeInputIndex[i] = null;
	                attributeTask[i] = null;
	            }
	        }
	    }
	}
	
	/**
	* Checks if the startIndex and endIndex correspond to the same element start
	* and end records respectively and replaces the records removing all the 
	* records in between, effectively replacing the section corresponding to the
	* element's subtree. 
	*/
	public void replaceSection(int startIndex,
	                        ElementTask startTask,
	                        int endIndex, 
	                        ElementTask endTask){
	                 	    
	    //System.out.println(hashCode()+" startIndex="+startIndex+"  correspEndIndex[startIndex]="+correspEndIndex[startIndex]);
	    //System.out.println(hashCode()+" endIndex="+endIndex+"  correspStartIndex[endIndex]="+correspStartIndex[endIndex]);
	    if(correspStartIndex[endIndex] != startIndex
	        || correspEndIndex[startIndex] != endIndex){
	        throw new IllegalArgumentException();
	    }
	    // System.out.println(hashCode()+" END REPLACE SECTION ");
	    // System.out.println();
	    
	    
	   
	    // clear all records between start and end indexes, except for the index 
	    // correspondence	    
	    for(int i = startIndex; i <= endIndex; i++){
	        if(elementInputIndex[i] != -1){
                activeInputDescriptor.unregisterClientForRecord(elementInputIndex[i], this);
                elementInputIndex[i] = -1;
            }        
            if(attributeInputIndex[i] != null){
                for(int j = 0; j < attributeInputIndex[i].length; j++){
                    if(attributeInputIndex[i][j] >=0){
                        activeInputDescriptor.unregisterClientForRecord(attributeInputIndex[i][j], this);
                    }
                }
                attributeInputIndex[i] = null;
            }
	        attributeTask[i] = null;
	        elementTask[i] = null;
	        characterContent[i] = null;
	    }
	    
	    elementTask[startIndex] = startTask;
	    elementTask[endIndex] = endTask;
	}
	
	/**
	* Checks if the startIndex and endIndex correspond to the same element start
	* and end records respectively and replaces the records removing all the 
	* records in between, effectively replacing the section corresponding to the
	* element's subtree. 
	*/
	public void replaceSection(int startIndex,
	                        ElementTask startTask,
	                        AttributeTask attrTask,
	                        int endIndex, 
	                        ElementTask endTask){
	    // System.out.println(hashCode()+" START REPLACE SECTION ");
	    if(correspStartIndex[endIndex] != startIndex
	        || correspEndIndex[startIndex] != endIndex){
	        // System.out.println(hashCode()+" startIndex="+startIndex+"  correspEndIndex="+correspEndIndex[startIndex]);
	        // System.out.println(hashCode()+" endIndex="+endIndex+"  correspStartIndex="+correspStartIndex[endIndex]);
	        throw new IllegalArgumentException();
	    }
	    // System.out.println(hashCode()+" END REPLACE SECTION ");
	    // System.out.println();
	    	    
	    
	    // clear all records between start en end indexes, except for the index 
	    // correspondence	    
	    for(int i = startIndex; i <= endIndex; i++){
	        if(elementInputIndex[i] != -1){
                activeInputDescriptor.unregisterClientForRecord(elementInputIndex[i], this);
                elementInputIndex[i] = -1;
            }       
	        if(attributeInputIndex[i] != null){
                for(int j = 0; j < attributeInputIndex[i].length; j++){
                    if(attributeInputIndex[i][j] >=0){
                        activeInputDescriptor.unregisterClientForRecord(attributeInputIndex[i][j], this);
                    }
                }
                attributeInputIndex[i] = null;
            }
	        attributeTask[i] = null;
	        elementTask[i] = null;
	        characterContent[i] = null;
	    }
	    attributeTask[startIndex] = new AttributeTask[1];
	    attributeTask[startIndex][0] = attrTask;
	    
	    
	    elementTask[startIndex] = startTask;
	    elementTask[endIndex] = endTask;
	}
	
	
	// TODO : xmlns!!!
	// The declared xmlns is set from the ValidatorHandler, the replacement 
	// queues are from the ElementHandlers; when closing reservations you might
	// override the existing records for xmlns with the empty ones from other 
	// queues.
	public void useReservation( int reservedStartEntry, Queue other, int otherStartEntry, int otherEndEntry){	    
        //System.out.println(hashCode()+" QUEUE USE RESERVATION reservedStartEntry="+reservedStartEntry+"  other="+other.hashCode()+" otherStartEntry="+otherStartEntry+"  otherEndEntry="+otherEndEntry);	    
		int reservationRegistrationIndex = -1;		 
		for(int i = 0; i < reservationStartEntry.length; i++){
			if(reservationStartEntry[i] == reservedStartEntry) {
				reservationRegistrationIndex = i;
				break;
			}
		}
				
		if(reservationRegistrationIndex == -1)throw new IllegalArgumentException();
		if(reservationUsed[reservationRegistrationIndex]) throw new IllegalStateException();
		int reservationCount = reservationEntriesCount[reservationRegistrationIndex]; 
		if(reservationCount != otherEndEntry - otherStartEntry + 1) throw new IllegalArgumentException("Reservation count is "+reservationCount+"; other queue requires "+(otherEndEntry - otherStartEntry + 1)+".");
	
		
        int[] otherEII = other.getAllElementInputIndexes(); 
		        
        int[][] otherAII = other.getAllAttributeInputIndexes();
		AttributeTask[][] otherAT = other.getAllAttributeTasks();
		String[] otherCC = other.getAllCharacterContent();
		ElementTask[] otherET = other.getAllElementTasks();
		//int[] tempOtherCSI = other.getAllCorrespStartIndexes();
		int[] otherCSI = new int[otherET.length];
		// System.arraycopy(tempOtherCSI, 0, otherCSI, 0, tempOtherCSI.length);
		
		int[] otherCEI = other.getAllCorrespEndIndexes();
		//int[] otherCEI = new int[tempOtherCEI.length];
		//System.arraycopy(tempOtherCEI, 0, otherCEI, 0, tempOtherCEI.length);
		
		
		int thisStartEntry = reservationStartEntry[reservationRegistrationIndex];
		int diff = thisStartEntry - otherStartEntry; // this = other+diff
		
		int otherIndex = -1;
		int thisIndex = -1;
		int transferII = -1;
		int attributesLength = -1;
		for(int i = 0; i < reservationCount; i++){
		    otherIndex = i + otherStartEntry;
		    thisIndex = i + thisStartEntry;
		    
		    if(elementInputIndex[thisIndex] > -1)activeInputDescriptor.unregisterClientForRecord(elementInputIndex[thisIndex], this);
            transferII = otherEII[otherIndex];
            elementInputIndex[thisIndex] = transferII;
            if(transferII > -1)activeInputDescriptor.registerClientForRecord(transferII, this);
			
			if(otherAII[otherIndex] != null){
			    attributesLength = otherAII[otherIndex].length;
                attributeInputIndex[thisIndex] = new int[attributesLength];
                attributeTask[thisIndex]  = new AttributeTask[attributesLength];
                for(int j = 0; j < attributesLength; j++){
                    transferII = otherAII[otherIndex][j];
                    attributeInputIndex[thisIndex][j] = transferII;
                    if(transferII > -1)activeInputDescriptor.registerClientForRecord(transferII, this);
                    
                    attributeTask[thisIndex][j] = otherAT[otherIndex][j];
                }
            }
			characterContent[thisIndex] = otherCC[otherIndex];
			elementTask[thisIndex] = otherET[otherIndex];
			// When a start element entry is handled(otherCEI != -1) modify the
			// otherCSI to contain this index. It will be set when the loop gets
			// there.			
			// When an end element entry is handled(otherCSI != -1) modify the
			// correspEndIndex value at the otherCSI index to contain the 
			// current index.			
			if(otherCEI[otherIndex]!=-1){
			    // start element
 			    otherCSI[otherCEI[otherIndex]] = thisIndex; 			     
 			    correspStartIndex[thisIndex] = -1; // should be otherCSI[otherIndex], but it wasn't filled 
			}else if(otherCSI[otherIndex]!=-1){
			    // end element			    
			    correspStartIndex[thisIndex] = otherCSI[otherIndex];
			    //correspEndIndex[thisIndex] = otherCEI[otherIndex]; // should be -1	
			    correspEndIndex[thisIndex] = -1;
                correspEndIndex[otherCSI[otherIndex]] = thisIndex;                
			}else{
			    throw new IllegalStateException();
			}		
		}	
		
		
		// Only take over the task and task related content, not the reservations
		// since that implies a relation with the specific document handler, not 
		// with the document.		
		reservationUsed[reservationRegistrationIndex] = true;	
	}
	
	// TODO : xmlns!!!
	// The declared xmlns is set from the ValidatorHandler, the replacement 
	// queues are from the ElementHandlers; when closing reservations you might
	// override the existing records for xmlns with the empty ones from other 
	// queues.
	public void cancelReservation(int reservedStartEntry){
		//System.out.println(hashCode()+" CANCEL RESERVATION reservedStartEntry="+reservedStartEntry);
		int reservationRegistrationIndex = -1;		 
		for(int i = 0; i < reservationStartEntry.length; i++){
			if(reservationStartEntry[i] == reservedStartEntry) {
				reservationRegistrationIndex = i;
				break;
			}
		}
				
		if(reservationRegistrationIndex == -1)throw new IllegalArgumentException();
		if(reservationUsed[reservationRegistrationIndex]) throw new IllegalStateException();
				
		reservationUsed[reservationRegistrationIndex] = true;	
	}
	
	
	public void executeAll(){
		// System.out.println(hashCode()+" EXECUTE ALL");		
		//System.out.println(hashCode()+" ");
		//System.out.println(toString());
		
		if(startDocumentTask != null){
		    startDocumentTask.setContext(this);
		    startDocumentTask.execute();
		}
		int count = currentIndex+1;
		//System.out.println(hashCode()+" QUEUE EXECUTE ALL count="+count);
		for(currentIndex = 0 ; currentIndex < count; currentIndex++){			
			//System.out.println(currentIndex+"/"+elementTask[currentIndex]);	
			if(correspStartIndex[currentIndex] == -1)executeStartElementTasks();
			else if(correspEndIndex[currentIndex] == -1)executeEndElementTasks();
			else{
			    //System.out.println(hashCode()+" QUEUE EXECUTE ALL  currentIndex="+currentIndex+"   correspStartIndex[currentIndex]="+correspStartIndex[currentIndex]+" correspEndIndex[currentIndex]="+correspEndIndex[currentIndex]);
			    throw new IllegalStateException();
			}
		}
		if(endDocumentTask != null){
		    endDocumentTask.setContext(this);
		    endDocumentTask.execute();
		}
		// No unregistering here, done at clear.
		//if(documentInputIndex != -1) activeInputDescriptor.unregisterClientForRecord(documentInputIndex, this);
		// System.out.println(hashCode()+" currentIndex "+currentIndex);		
	}
	private void executeStartElementTasks(){
	    boolean attributeTaskAfter = false;
	    if(attributeTask[currentIndex] != null){
            for(currentAttributeIndex = 0; currentAttributeIndex < attributeTask[currentIndex].length; currentAttributeIndex++){
                if(attributeTask[currentIndex][currentAttributeIndex] != null){
                    if(!attributeTaskAfter && attributeTask[currentIndex][currentAttributeIndex].executeAfterStartElementTask()) attributeTaskAfter = true;
                    if(attributeTask[currentIndex][currentAttributeIndex].executeBeforeStartElementTask()){
                        attributeTask[currentIndex][currentAttributeIndex].setContext(this);
                        attributeTask[currentIndex][currentAttributeIndex].execute();
                    }
                }
            }
        }
	    
	    if(elementTask[currentIndex] != null){
	        elementTask[currentIndex].setContext(this);
			elementTask[currentIndex].execute();
	    }
	    
	    if(attributeTask[currentIndex] != null){
            if(attributeTaskAfter){
                for(currentAttributeIndex = 0; currentAttributeIndex < attributeTask[currentIndex].length; currentAttributeIndex++){                
                    if(attributeTask[currentIndex][currentAttributeIndex] != null && attributeTask[currentIndex][currentAttributeIndex].executeAfterStartElementTask()){
                        attributeTask[currentIndex][currentAttributeIndex].setContext(this);
                        attributeTask[currentIndex][currentAttributeIndex].execute();
                    }
                }
            }
        }
	}
	private void executeEndElementTasks(){
	    int endIndex = currentIndex;
	    int startIndex = correspStartIndex[currentIndex];
	    
	    currentIndex = startIndex;
	    if(attributeTask[currentIndex] != null){
            for(currentAttributeIndex = 0; currentAttributeIndex < attributeTask[currentIndex].length; currentAttributeIndex++){
                if(attributeTask[currentIndex][currentAttributeIndex] != null && attributeTask[currentIndex][currentAttributeIndex].executeBeforeEndElementTask()){
                    attributeTask[currentIndex][currentAttributeIndex].setContext(this);
                    attributeTask[currentIndex][currentAttributeIndex].execute();
                }
            }
        }
	    
        currentIndex = endIndex;
	    if(elementTask[currentIndex] != null){
	        elementTask[currentIndex].setContext(this);
			elementTask[currentIndex].execute();
	    }
	    // No unregistering here, done at clear.
	    //if(elementInputIndex[currentIndex] != -1) activeInputDescriptor.unregisterClientForRecord(elementInputIndex[currentIndex], this);
	    
	    currentIndex = startIndex;
	    if(attributeTask[currentIndex] != null){
            for(currentAttributeIndex = 0; currentAttributeIndex < attributeTask[currentIndex].length; currentAttributeIndex++){                
                if(attributeTask[currentIndex][currentAttributeIndex] != null && attributeTask[currentIndex][currentAttributeIndex].executeAfterEndElementTask()){
                    attributeTask[currentIndex][currentAttributeIndex].setContext(this);
                    attributeTask[currentIndex][currentAttributeIndex].execute();
                }
                // No unregistering here, done at clear.
                //if(attributeInputIndex[currentIndex][currentAttributeIndex] != -1) activeInputDescriptor.unregisterClientForRecord(attributeInputIndex[currentIndex][currentAttributeIndex], this);
            }
        }

        currentIndex = endIndex;        
	}	
	
	public int getSize(){
		// System.out.println(hashCode()+" GET SIZE ");
		return currentIndex+1;
	}
	
	
	
	
	//TaskContext
	//--------------------------------------------------------------------------
	public DocumentIndexedData getDocumentIndexedData(){
	    if(documentIndexedData == null) documentIndexedData = activeInputDescriptor.getDocumentIndexedData();
	    return documentIndexedData;
	} 
	//--------------------------------------------------------------------------
	
	//ElementTaskContext
	//--------------------------------------------------------------------------
	public int getDocumentInputRecordIndex(){
	    return documentInputIndex;
	}
	//--------------------------------------------------------------------------
	
	
	//ElementTaskContext
	//--------------------------------------------------------------------------
	
	public int getElementInputRecordIndex(){
	    int startIndex;
	    if(!isCurrentEntryStart()) startIndex = correspStartIndex[currentIndex];
	    else startIndex = currentIndex;
	    return elementInputIndex[startIndex];
	}
	
	public String getCharacterContent(){
		int characterContentIndex;
		if(!isCurrentEntryStart()) characterContentIndex = correspStartIndex[currentIndex];
		else characterContentIndex = currentIndex;
				
		return characterContent[characterContentIndex];
	}	
	//--------------------------------------------------------------------------
	
	//AttributeTaskContext
	//--------------------------------------------------------------------------
    
	public int getAttributeInputRecordIndex(){
		// when executing an attribute task the currentIndex is always at a start tag
		return attributeInputIndex[currentIndex][currentAttributeIndex];
	}
	//--------------------------------------------------------------------------
	
	private boolean isCurrentEntryStart(){
		return correspStartIndex[currentIndex] == -1;
	}	
	
	private void increaseSize(){
		increaseSize(20);
	}
	private void increaseSize(int amount){	
	    int oldSize = size;
		size = size+amount+10;
		if(size <= currentIndex) size = currentIndex+amount+10;		
				
		int[] increasedIRI = new int[size];
		System.arraycopy(elementInputIndex, 0, increasedIRI, 0, oldSize);
		elementInputIndex = increasedIRI;		
		Arrays.fill(elementInputIndex, oldSize, size, -1);
		
		int[][] increasedAII = new int[size][];
		for(int i = 0; i< oldSize; i++){
			if(attributeInputIndex[i] != null){
				int[] newInputIndexs = new int[attributeInputIndex[i].length];
				System.arraycopy(attributeInputIndex[i], 0, newInputIndexs, 0, attributeInputIndex[i].length);
				increasedAII[i] = newInputIndexs; 
			}				
		}
		attributeInputIndex = increasedAII;
		
		AttributeTask[][] increasedAT = new AttributeTask[size][];
		for(int i = 0; i< oldSize; i++){
			if(attributeTask[i] != null){
				AttributeTask[] newTasks = new AttributeTask[attributeTask[i].length];
				System.arraycopy(attributeTask[i], 0, newTasks, 0, attributeTask[i].length);
				increasedAT[i] = newTasks; 
			}				
		}
		attributeTask = increasedAT;
		
		String[] increasedCC = new String[size];
		System.arraycopy(characterContent, 0, increasedCC, 0, oldSize);				
		characterContent = increasedCC;
		
		ElementTask[] increasedT = new ElementTask[size];
		System.arraycopy(elementTask, 0, increasedT, 0, oldSize);
		elementTask = increasedT;
		
		int[] increasedSI = new int[size];
		System.arraycopy(correspStartIndex, 0, increasedSI, 0, oldSize);
		correspStartIndex = increasedSI;
		Arrays.fill(correspStartIndex, oldSize, size, -1);
		
		int[] increasedEI = new int[size];
		System.arraycopy(correspEndIndex, 0, increasedEI, 0, oldSize);
		correspEndIndex = increasedEI;
		Arrays.fill(correspEndIndex, oldSize, size, -1);
	}
	
	private void increaseReservation(int amount){
		reservationSize += amount;
		
		int[] increasedRO = new int[reservationSize];
		System.arraycopy(reservationStartEntry, 0, increasedRO, 0, reservationIndex);
		reservationStartEntry = increasedRO;
		Arrays.fill(reservationStartEntry, reservationIndex, reservationSize, -1);
		
		int[] increasedREC = new int[reservationSize];
		System.arraycopy(reservationEntriesCount, 0, increasedREC, 0, reservationIndex);
		reservationEntriesCount = increasedREC;
		//Arrays.fill(reservationEntriesCount, reservationIndex, reservationSize, -1);
		
		boolean[] increasedRU = new boolean[reservationSize];
		System.arraycopy(reservationUsed, 0, increasedRU, 0, reservationIndex);
		reservationUsed = increasedRU;
	}
	private void increaseReservation(){
		reservationSize += 5;
		
		int[] increasedRO = new int[reservationSize];
		System.arraycopy(reservationStartEntry, 0, increasedRO, 0, reservationIndex);
		reservationStartEntry = increasedRO;
		Arrays.fill(reservationStartEntry, reservationIndex, reservationSize, -1);
		
		int[] increasedREC = new int[reservationSize];
		System.arraycopy(reservationEntriesCount, 0, increasedREC, 0, reservationIndex);
		reservationEntriesCount = increasedREC;
		
		boolean[] increasedRU = new boolean[reservationSize];
		System.arraycopy(reservationUsed, 0, increasedRU, 0, reservationIndex);
		reservationUsed = increasedRU;
	}
	
	private int[] getAllElementInputIndexes(){
	    return elementInputIndex;
	}
	
    private int[][] getAllAttributeInputIndexes(){
		return attributeInputIndex;
	}

	private AttributeTask[][] getAllAttributeTasks(){
		return attributeTask;
	}	
	
	private String[] getAllCharacterContent(){
		return characterContent;
	}
	
	private ElementTask[] getAllElementTasks(){
		return elementTask;
	}	
	
	private int[] getAllCorrespStartIndexes(){
		return correspStartIndex;
	}
	
	private int[] getAllCorrespEndIndexes(){
		return correspEndIndex;
	}
	
	private int getReservationsCount(){
		return reservationIndex+1;
	}
	
	private int[] getReservationStartEntry(){
		return reservationStartEntry;
	}
	private int[] getReservationEntriesCount(){
		return reservationEntriesCount;
	}
	private boolean[] getReservationUsed(){
		return reservationUsed;
	}

	public String toString(){		
        String s = "";
        /*s = s+"Element name info "+Arrays.deepToString(elementNameInfo);*/
		/*s = s+"Attribute info "+Arrays.deepToString(attributeInfo);*/
		s = s+"\nAttribute tasks " +Arrays.deepToString(attributeTask);	 
		/*s = s+"\nCharacter content "+Arrays.toString(characterContent);*/
		s = s+"\nElement tasks "+Arrays.toString(elementTask);
		s = s+"\nCorresponding start index "+Arrays.toString( correspStartIndex);
		s = s+"\nCurrent index "+currentIndex;
		s = s+"\nReservation offset "+Arrays.toString(reservationStartEntry);
		s = s+"\nReservation entries count "+Arrays.toString(reservationEntriesCount);
		s = s+"\nReservation used "+Arrays.toString(reservationUsed);
		return s;
	}		
}
