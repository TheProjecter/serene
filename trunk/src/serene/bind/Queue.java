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


package serene.bind;

import java.util.Arrays;
import java.util.HashMap;

import org.xml.sax.SAXNotRecognizedException;

import serene.validation.schema.simplified.components.SAttribute;

import serene.util.ObjectIntHashMap;

import serene.util.NameInfo;
import serene.util.AttributeInfo;

import serene.Reusable;

import sereneWrite.MessageWriter;

public class Queue implements ElementTaskContext, AttributeTaskContext, Reusable{
	ValidatorQueuePool pool;
	
	int currentIndex;
	int size;
	
	int currentAttributeIndex;

	String[] location;
    NameInfo[] elementNameInfo;
	HashMap<String, String>[] xmlns;
	String[] xmlBase;	
    // There can be several attribute instances for the same definition when 
    // the name is defined using a wild card. This implies a one to many relation
    // between the attributeIndex(corresponding to definition) and the actual
    // attribute data (name and value). So the queue must be prepared to 
    // accomodate all the occurrences, but, at least for now, the relationship
    // definition - task stays one to one.
    // Implementation [record index][attribute definition index][instance index]
    AttributeInfo[][][] attributeInfo;
	AttributeTask[][] attributeTask;	 
	String[] characterContent;
	ElementTask[] elementTask;
	int[] correspStartIndex;
	int[] correspEndIndex;
		
	ObjectIntHashMap sattributeIndexMap;
	int attributeCount;

	int[] reservationOffset;
	int[] reservationEntriesCount;
	boolean[] reservationUsed;
	int reservationIndex;
	int reservationSize;
		
	HashMap<String, String> prefixMapping;	
	
	boolean isClear;
	
	MessageWriter debugWriter;
	
	public Queue(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		currentIndex = -1;
		size = 20;
				
		currentAttributeIndex = -1;
		
		location = new String[size];
        elementNameInfo = new NameInfo[size];
		xmlns = new HashMap[size];
		xmlBase = new String[size];
        attributeInfo = new AttributeInfo[size][][];
		attributeTask = new AttributeTask[size][];		
		characterContent = new String[size];	
		elementTask = new ElementTask[size];
		correspStartIndex = new int[size];
		correspEndIndex = new int[size];
		
		reservationIndex = -1;
		reservationSize = 5;
		
		reservationOffset = new int[reservationSize];
		reservationEntriesCount = new int[reservationSize];
		reservationUsed = new boolean[reservationSize];
		
		isClear = true;
	}	
	
	public void clear(){
		// System.out.println(hashCode()+" CLEAR ");		
		currentIndex = -1;
		currentAttributeIndex = -1;
		
		isClear = true;
		
		clearContent();				
	}
	
	
	public void clearContent(){
		// System.out.println(hashCode()+" CLEAR ");
		
		Arrays.fill(location, null);
        Arrays.fill(elementNameInfo, null);
		Arrays.fill(xmlns, null);
		Arrays.fill(xmlBase, null);
        Arrays.fill(attributeInfo, null);
		Arrays.fill(attributeTask, null);		
		Arrays.fill(characterContent, null);	
		Arrays.fill(elementTask, null);
		Arrays.fill(correspStartIndex, -1);
		Arrays.fill(correspEndIndex, -1);
		
		if(prefixMapping != null)prefixMapping.clear();
		
		reservationIndex = -1;
				
		Arrays.fill(reservationOffset, -1);
		Arrays.fill(reservationEntriesCount, -1);
		Arrays.fill(reservationUsed, false);
	}
	
	void init(ValidatorQueuePool pool){
		this.pool = pool;
		isClear = false;
	}
	
	public void recycle(){
		if(!isClear)clear();
		sattributeIndexMap = null;
		attributeCount = -1;
		pool.recycle(this);
	}
	
	public void index(ObjectIntHashMap sattributeIndexMap){		
		this.sattributeIndexMap = sattributeIndexMap;
		attributeCount = sattributeIndexMap.size();
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
		
	public int newRecord(){
		// System.out.println(hashCode()+" RECORD ");
		// System.out.println("size "+size);
		// System.out.println("currentIndex "+currentIndex);
		// System.out.println("correspStartIndex "+correspStartIndex.length);
		 
		if(++currentIndex == size)increaseSize();
		correspStartIndex[currentIndex] = -1;
		if(prefixMapping != null){
			xmlns[currentIndex] = prefixMapping;
			prefixMapping = null;
		}
		return currentIndex;
	}
	
	public void addLocation(int record, String loc){
		// System.out.println(hashCode()+" ADD LOCATION ");
		location[record] = loc;
	}

	
    public void addNameInfo(int record, NameInfo eni){
		// System.out.println(hashCode()+" ADD ELEMENT QNAME ");
		//System.out.println(record+" /"+content+"/");
        elementNameInfo[record] = eni;
	}
    
	public void addAttributeInfo(int record, int attributeIndex, AttributeInfo ai){
		// System.out.println(hashCode()+" ADD ATTRIBUTE QNAME");		
		//System.out.println(record+" /"+attributeIndex+"/"+qN+"/");
		if(attributeIndex < 0 || attributeIndex > attributeCount) throw new IllegalArgumentException();
        
		if(attributeInfo[record] == null ){
            attributeInfo[record] = new AttributeInfo[attributeCount][];
            attributeInfo[record][attributeIndex] = new AttributeInfo[1];
            attributeInfo[record][attributeIndex][0] = ai;            
        }else{
            if(attributeInfo[record][attributeIndex] == null){
                attributeInfo[record][attributeIndex] = new AttributeInfo[1];
                attributeInfo[record][attributeIndex][0] = ai;
            }else{         
                int currentLength = attributeInfo[record][attributeIndex].length;
                
                AttributeInfo[] increasedURI = new AttributeInfo[currentLength+1];
                System.arraycopy(attributeInfo[record][attributeIndex], 0, increasedURI, 0, currentLength);
                attributeInfo[record][attributeIndex] = increasedURI;
                attributeInfo[record][attributeIndex][currentLength] = ai;
            }
        }        
	}
	
	public void addAttributeTask(int record, int attributeIndex, AttributeTask task){
		// System.out.println(hashCode()+" ADD ATTRIBUTE TASK ");
		if(attributeIndex < 0 || attributeIndex > attributeCount) throw new IllegalArgumentException();
		if(attributeTask[record] == null )attributeTask[record] = new AttributeTask[attributeCount];
		attributeTask[record][attributeIndex] = task;
	}
	public void addCharacterContent(int record, String content){
		// System.out.println(hashCode()+" ADD CHARACTER CONTENT ");
		//System.out.println(record+" /"+content+"/");
		if(characterContent[record] == null) characterContent[record] = content;
		else characterContent[record] += content;
	}
	public void addIndexCorrespondence(int endRecord, int startIndex){
		// System.out.println(hashCode()+" ADD INDEX CORRESPONDENCE ");
		//System.out.println(record+" "+startIndex);
		correspStartIndex[endRecord] = startIndex;
		correspEndIndex[startIndex] = endRecord;
	}	
	public void addElementTasks(int record, ElementTask startTask, ElementTask endTask){
		// System.out.println(hashCode()+" ADD ELEMENT TASK ");
		//System.out.println(record+" "+startTask+" "+endTask);
		int startRecord = correspStartIndex[record];
		if(startRecord == -1)throw new IllegalStateException();		
		elementTask[startRecord] = startTask;
		elementTask[record] = endTask;
	}
	public void addXmlns(String prefix, String uri){
		// System.out.println(hashCode()+" ADD XMLNS ");
		if(prefixMapping == null)prefixMapping = new HashMap<String, String>();
		prefixMapping.put(prefix, uri);
		//System.out.println("+++++"+prefixMapping);
	}
	public void addXmlBase(String base){
		// System.out.println(hashCode()+" ADD XML BASE ");
		xmlBase[currentIndex] = base;		
	}
	public void reserve(int reservationStartIndex, int entriesCount){
		// System.out.println(hashCode()+" RESERVE ");
		// System.out.println("start "+reservationStartIndex);
		// System.out.println("entries "+entriesCount);		
		if(reservationStartIndex != currentIndex) throw new IllegalArgumentException();		
		if(currentIndex+entriesCount >= size)increaseSize(entriesCount);		
		currentIndex += entriesCount-1;//the last reserved index
		
		if(++reservationIndex == reservationSize) increaseReservation();
		reservationOffset[reservationIndex] = reservationStartIndex;
		reservationEntriesCount[reservationIndex] = entriesCount;
		reservationUsed[reservationIndex] = false;
		// System.out.println("index after "+currentIndex);
	}
	public void closeReservation( int reservedStartEntry, Queue other){
		//System.out.println(hashCode()+" CLOSE RESERVATION "+other.hashCode());
		int reservationRegistrationIndex = -1;		 
		for(int i = 0; i < reservationOffset.length; i++){
			if(reservationOffset[i] == reservedStartEntry) {
				reservationRegistrationIndex = i;
				break;
			}
		}
				
		if(reservationRegistrationIndex == -1)throw new IllegalArgumentException();
		if(reservationUsed[reservationRegistrationIndex]) throw new IllegalStateException();
		int reservationCount = reservationEntriesCount[reservationRegistrationIndex]; 
		if(reservationCount != other.getSize()) throw new IllegalArgumentException("Reservation count is "+reservationCount+"; size of other queue "+other.getSize()+".");
	
		
		String[] otherL = other.getAllLocations();
        NameInfo[] otherENI = other.getAllNameInfos();
		HashMap[] otherXmlns = other.getAllXmlns();
		String[] otherXmlBase = other.getAllXmlBase();
        AttributeInfo[][][] otherAI = other.getAllAttributeInfos();
		AttributeTask[][] otherAT = other.getAllAttributeTasks();
		String[] otherCI = other.getAllCharacterContent();
		ElementTask[] otherET = other.getAllElementTasks();
		int[] otherCSI = other.getAllCorrespStartIndexes();
		int[] otherCEI = other.getAllCorrespEndIndexes();
		
		
		int offset = reservationOffset[reservationRegistrationIndex];
		for(int i = 0; i < reservationCount; i++){
			location[i+offset] = otherL[i];
            elementNameInfo[i+offset] = otherENI[i];
			if(xmlns[i+offset] == null) xmlns[i+offset] = otherXmlns[i];
			if(xmlBase[i+offset] == null) xmlBase[i+offset] = otherXmlBase[i];
            attributeInfo[i+offset] = otherAI[i];
			attributeTask[i+offset] = otherAT[i];			
			characterContent[i+offset] = otherCI[i];
			elementTask[i+offset] = otherET[i];
			if(otherCSI[i]!=-1)correspStartIndex[i+offset] = otherCSI[i]+offset;
			else correspStartIndex[i+offset] = otherCSI[i];
			if(otherCEI[i]!=-1)correspEndIndex[i+offset] = otherCEI[i]+offset;
			else correspEndIndex[i+offset] = otherCEI[i];
		}	
		
		
		
		int otherReservationsCount = other.getReservationsCount();		
		if(otherReservationsCount == 0 ){
			reservationUsed[reservationRegistrationIndex] = true;		
			return;
		}

		if(reservationIndex+otherReservationsCount+1 >= reservationSize)increaseReservation(otherReservationsCount);
		
		int[] otherReservationOffset = other.getReservationOffset();
		int[] otherReservationEntriesCount = other.getReservationEntriesCount();
		boolean[] otherReservationUsed = other.getReservationUsed();
		for(int i = 0; i < otherReservationsCount; i++){
			reservationOffset[++reservationIndex] = otherReservationOffset[i]+reservedStartEntry;
			reservationEntriesCount[reservationIndex] = otherReservationEntriesCount[i];
			reservationUsed[reservationIndex] = otherReservationUsed[i]; 			
		}
		reservationUsed[reservationRegistrationIndex] = true;	
	}
	
	public void closeReservation(int reservedStartEntry){
		//System.out.println(hashCode()+" CLOSE RESERVATION ");
		int reservationRegistrationIndex = -1;		 
		for(int i = 0; i < reservationOffset.length; i++){
			if(reservationOffset[i] == reservedStartEntry) {
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
		//System.out.println("");
		//System.out.println(toString());
		for(currentIndex = 0 ; currentIndex < size; currentIndex++){			
			//System.out.println(currentIndex+"/"+elementTask[currentIndex]);			
			if(elementTask[currentIndex] != null){
				elementTask[currentIndex].setContext(this);
				elementTask[currentIndex].execute();
			}
			if(attributeTask[currentIndex] != null){
				for(currentAttributeIndex = 0; currentAttributeIndex < attributeTask[currentIndex].length; currentAttributeIndex++){
					if(attributeTask[currentIndex][currentAttributeIndex] != null){
						attributeTask[currentIndex][currentAttributeIndex].setContext(this);
						attributeTask[currentIndex][currentAttributeIndex].execute();
					}
				}
			}
		}
		// System.out.println("currentIndex "+currentIndex);		
	}
	
		
	public int getSize(){
		// System.out.println(hashCode()+" GET SIZE ");
		return currentIndex+1;
	}
	//ElementTaskContext
	//--------------------------------------------------------------------------
	public String getStartLocation(){
		int startIndex;
		if(!isCurrentEntryStart()) startIndex = correspStartIndex[currentIndex];
		else startIndex = currentIndex;
		return location[startIndex];
	}
	public String getEndLocation(){
		int endIndex;
		if(isCurrentEntryStart()) endIndex = correspEndIndex[currentIndex];
		else endIndex = currentIndex;
		return location[endIndex];
	}
    public NameInfo getElementNameInfo(){
		int nameIndex;
		if(!isCurrentEntryStart()) nameIndex = correspStartIndex[currentIndex];
		else nameIndex = currentIndex;
		return elementNameInfo[nameIndex];
	}
	public String getXmlBase(){
		int baseIndex;
		if(!isCurrentEntryStart()) baseIndex = correspStartIndex[currentIndex];
		else baseIndex = currentIndex;
		return xmlBase[baseIndex];
	}	
	public HashMap<String, String> getPrefixMapping(){
		int mapIndex;
		if(!isCurrentEntryStart()) mapIndex = correspStartIndex[currentIndex];
		else mapIndex = currentIndex;
		return xmlns[mapIndex];
	}
    
    public AttributeInfo[] getAttributeInfo(SAttribute attribute){
		int attributesRecordIndex;
		if(!isCurrentEntryStart()) attributesRecordIndex = correspStartIndex[currentIndex];
		else attributesRecordIndex = currentIndex;
		
		int attributeIndex = sattributeIndexMap.get(attribute);		
		//System.out.println(currentIndex+" /"+attributesRecordIndex+"/");
		//System.out.println(currentIndex+" /"+attributeIndex+"/"+attribute);
		//System.out.println(currentIndex+" /"+Arrays.toString(attributeValue[attributesRecordIndex]));
		if(attributeInfo[attributesRecordIndex]!=null)return attributeInfo[attributesRecordIndex][attributeIndex];
		return null;
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
    public AttributeInfo[] getAttributeInfo(){
		// when executing an attribute task the currentIndex is always at a start tag
		return attributeInfo[currentIndex][currentAttributeIndex];
	}
	//--------------------------------------------------------------------------
	
	private boolean isCurrentEntryStart(){
		return correspStartIndex[currentIndex] == -1;
	}	
	
	private void increaseSize(){
		increaseSize(10);
	}
	private void increaseSize(int amount){	
		size = size+amount+10;
		if(size <= currentIndex) size = currentIndex+amount+10;		
		
		String[] increasedL = new String[size];
		System.arraycopy(location, 0, increasedL, 0, currentIndex);
		location = increasedL;
		
        NameInfo[] increasedENI = new NameInfo[size];
		System.arraycopy(elementNameInfo, 0, increasedENI, 0, currentIndex);
		elementNameInfo = increasedENI;
		
		HashMap<String, String>[] increasedX = new HashMap[size];
		System.arraycopy(xmlns, 0, increasedX, 0, currentIndex);
		xmlns = increasedX;
		
		String[] increasedXB = new String[size];
		System.arraycopy(xmlBase, 0, increasedXB, 0, currentIndex);
		xmlBase = increasedXB;
		
        
        AttributeInfo[][][] increasedAI = new AttributeInfo[size][][];
		for(int i = 0; i< currentIndex; i++){
			if(attributeInfo[i] != null){
                increasedAI[i] = new AttributeInfo[attributeInfo[i].length][];
                for(int j = 0; j < attributeInfo[i].length; j++){
                    if(attributeInfo[i][j] != null){
                        AttributeInfo[] newAI = new AttributeInfo[attributeInfo[i][j].length];
                        System.arraycopy(attributeInfo[i][j], 0, newAI, 0, attributeInfo[i][j].length);
                        increasedAI[i][j] = newAI;
                    }
                }
			}				
		}		
		attributeInfo = increasedAI;
		
		AttributeTask[][] increasedAT = new AttributeTask[size][];
		for(int i = 0; i< currentIndex; i++){
			if(attributeTask[i] != null){
				AttributeTask[] newTasks = new AttributeTask[attributeTask[i].length];
				System.arraycopy(attributeTask[i], 0, newTasks, 0, attributeTask[i].length);
				increasedAT[i] = newTasks; 
			}				
		}
		attributeTask = increasedAT;
		
		String[] increasedEC = new String[size];
		System.arraycopy(characterContent, 0, increasedEC, 0, currentIndex);
		characterContent = increasedEC;
		
		ElementTask[] increasedT = new ElementTask[size];
		System.arraycopy(elementTask, 0, increasedT, 0, currentIndex);
		elementTask = increasedT;
		
		int[] increasedSI = new int[size];
		System.arraycopy(correspStartIndex, 0, increasedSI, 0, currentIndex);
		correspStartIndex = increasedSI;
		
		int[] increasedEI = new int[size];
		System.arraycopy(correspEndIndex, 0, increasedEI, 0, currentIndex);
		correspEndIndex = increasedEI;
	}
	
	private void increaseReservation(int amount){
		reservationSize += amount;
		
		int[] increasedRO = new int[size];
		System.arraycopy(reservationOffset, 0, increasedRO, 0, reservationIndex+1);
		reservationOffset = increasedRO;
		
		int[] increasedREC = new int[size];
		System.arraycopy(reservationEntriesCount, 0, increasedREC, 0, reservationIndex+1);
		reservationEntriesCount = increasedREC;
		
		boolean[] increasedRU = new boolean[size];
		System.arraycopy(reservationUsed, 0, increasedRU, 0, reservationIndex+1);
		reservationUsed = increasedRU;
	}
	private void increaseReservation(){
		reservationSize += 5;
		
		int[] increasedRO = new int[size];
		System.arraycopy(reservationOffset, 0, increasedRO, 0, reservationIndex);
		reservationOffset = increasedRO;
		
		int[] increasedREC = new int[size];
		System.arraycopy(reservationEntriesCount, 0, increasedREC, 0, reservationIndex);
		reservationEntriesCount = increasedREC;
		
		boolean[] increasedRU = new boolean[size];
		System.arraycopy(reservationUsed, 0, increasedRU, 0, reservationIndex);
		reservationUsed = increasedRU;
	}
	
	private String[] getAllLocations(){
		return location;
	}
	
    private NameInfo[] getAllNameInfos(){
		return elementNameInfo;
	}
	
	private HashMap[] getAllXmlns(){
		return xmlns;
	}
	private String[] getAllXmlBase(){
		return xmlBase;
	}
	
    private AttributeInfo[][][] getAllAttributeInfos(){
		return attributeInfo;
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
	
	private int[] getReservationOffset(){
		return reservationOffset;
	}
	private int[] getReservationEntriesCount(){
		return reservationEntriesCount;
	}
	private boolean[] getReservationUsed(){
		return reservationUsed;
	}

	public String toString(){		
        String s = "";
        s = s+"Element name info "+Arrays.deepToString(elementNameInfo);
		s = s+"Attribute info "+Arrays.deepToString(attributeInfo);
		s = s+"\nAttribute tasks " +Arrays.deepToString(attributeTask);	 
		s = s+"\nCharacter content "+Arrays.toString(characterContent);
		s = s+"\nElement tasks "+Arrays.toString(elementTask);
		s = s+"\nCorresponding start index "+Arrays.toString( correspStartIndex);
		s = s+"\nCurrent index "+currentIndex;
		s = s+"\nReservation offset "+Arrays.toString(reservationOffset);
		s = s+"\nReservation entries count "+Arrays.toString(reservationEntriesCount);
		s = s+"\nReservation used "+Arrays.toString(reservationUsed);
		return s;
	}
}
