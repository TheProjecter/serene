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

import serene.Reusable;

import sereneWrite.MessageWriter;

public class Queue implements ElementTaskContext, AttributeTaskContext, Reusable{

	ValidatorQueuePool pool;
	
	int currentIndex;
	int size;
	
	int currentAttributeIndex;

	String[] location;
	String[] qName;
	HashMap<String, String>[] xmlns;
	String[] xmlBase;	
	String[][] attributeQName;
	String[][] attributeValue;
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
	
	
	ElementTask reservationStartDummyElementTask;
	ElementTask reservationEndDummyElementTask;
	
	boolean useReservationStartDummyElementTask;
	boolean useReservationEndDummyElementTask;
	
	MessageWriter debugWriter;
	
	public Queue(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		currentIndex = -1;
		size = 20;
				
		currentAttributeIndex = -1;
		
		location = new String[size];
		qName = new String[size];
		xmlns = new HashMap[size];
		xmlBase = new String[size];
		attributeQName = new String[size][];
		attributeValue = new String[size][];	
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
	}	
	
	public void clear(){
		// System.out.println(hashCode()+" CLEAR ");		
		currentIndex = -1;
		currentAttributeIndex = -1;
		
		Arrays.fill(location, null);
		Arrays.fill(qName, null);
		Arrays.fill(xmlns, null);
		Arrays.fill(xmlBase, null);
		Arrays.fill(attributeQName, null);
		Arrays.fill(attributeValue, null);	
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
	}
	
	public void recycle(){
		clear();
		sattributeIndexMap = null;
		attributeCount = -1;
		pool.recycle(this);
	}
	
	public void index(ObjectIntHashMap sattributeIndexMap){		
		this.sattributeIndexMap = sattributeIndexMap;
		attributeCount = sattributeIndexMap.size();
	}	
	
	public void setProperty(String name, Object value) throws SAXNotRecognizedException{		
		if(name.equals("reservationStartDummyElementTask")){
			reservationStartDummyElementTask = (ElementTask)value;
		}else if(name.equals("reservationEndDummyElementTask")){
			reservationEndDummyElementTask = (ElementTask)value;
		}else{
			throw new SAXNotRecognizedException("UNKNOWN PROPERTY.");
		}
	}
	
	public Object getProperty(String name)  throws SAXNotRecognizedException{
		if(name.equals("reservationStartDummyElementTask")){
			return reservationStartDummyElementTask;
		}else if(name.equals("reservationEndDummyElementTask")){
			return reservationEndDummyElementTask;
		}else{
			throw new SAXNotRecognizedException("UNKNOWN PROPERTY.");
		}
	}
	
	public void setFeature(String name, boolean value)  throws SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }		
		if(name.equals("useReservationStartDummyElementTask")){
			useReservationStartDummyElementTask = value;
		}else if(name.equals("useReservationEndDummyElementTask")){
			useReservationEndDummyElementTask = value;
		}else{
			throw new SAXNotRecognizedException("UNKNOWN PROPERTY.");
		}
	}
	
	public boolean getFeature(String name) throws SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }
		if(name.equals("useReservationStartDummyElementTask")){
			return useReservationStartDummyElementTask;
		}else if(name.equals("useReservationEndDummyElementTask")){
			return useReservationEndDummyElementTask;
		}else{
			throw new SAXNotRecognizedException("UNKNOWN PROPERTY.");
		}
	}
		
	public int newRecord(){
		// System.out.println(hashCode()+" RECORD ");
		// System.out.println("size "+size);
		// System.out.println("currentIndex "+currentIndex);
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
	
	public void addElementQName(int record, String name){
		// System.out.println(hashCode()+" ADD ELEMENT QNAME ");
		//System.out.println(record+" /"+content+"/");
		qName[record] = name;
	}
	
	public void addAttributeQName(int record, int attributeIndex, String name){
		// System.out.println(hashCode()+" ADD ATTRIBUTE QNAME");		
		//System.out.println(record+" /"+attributeIndex+"/"+value+"/");
		if(attributeIndex < 0 || attributeIndex > attributeCount) throw new IllegalArgumentException();
		if(attributeQName[record] == null )attributeQName[record] = new String[attributeCount];
		attributeQName[record][attributeIndex] = name;		
	}
	
	public void addAttributeValue(int record, int attributeIndex, String value){
		// System.out.println(hashCode()+" ADD ATTRIBUTE VALUE ");		
		//System.out.println(record+" /"+attributeIndex+"/"+value+"/");
		if(attributeIndex < 0 || attributeIndex > attributeCount) throw new IllegalArgumentException();
		if(attributeValue[record] == null )attributeValue[record] = new String[attributeCount];
		attributeValue[record][attributeIndex] = value;		
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
		// System.out.println(hashCode()+" CLOSE RESERVATION "+other.hashCode());
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
		if(reservationCount != other.getSize()) throw new IllegalArgumentException();
	
		
		String[] otherL = other.getAllLocations();
		String[] otherQN = other.getAllQNames();
		HashMap[] otherXmlns = other.getAllXmlns();
		String[] otherXmlBase = other.getAllXmlBase();
		String[][] otherAQN = other.getAllAttributeQNames();
		String[][] otherAV = other.getAllAttributeValues();
		AttributeTask[][] otherAT = other.getAllAttributeTasks();
		String[] otherCI = other.getAllCharacterContent();
		ElementTask[] otherET = other.getAllElementTasks();
		int[] otherCSI = other.getAllCorrespStartIndexes();
		int[] otherCEI = other.getAllCorrespEndIndexes();
		
		
		int offset = reservationOffset[reservationRegistrationIndex];
		for(int i = 0; i < reservationCount; i++){
			location[i+offset] = otherL[i];
			qName[i+offset] = otherQN[i];
			if(xmlns[i+offset] == null) xmlns[i+offset] = otherXmlns[i];
			if(xmlBase[i+offset] == null) xmlBase[i+offset] = otherXmlBase[i];
			attributeQName[i+offset] = otherAQN[i];
			attributeValue[i+offset] = otherAV[i];
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
		// System.out.println(hashCode()+" CLOSE RESERVATION ");
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
		int offset = reservationOffset[reservationRegistrationIndex];
		
		if(useReservationStartDummyElementTask){
			elementTask[offset] = reservationStartDummyElementTask;
		}	
		if(useReservationEndDummyElementTask){
			elementTask[offset+reservationCount-1] = reservationEndDummyElementTask;
		}
				
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
	public String getQName(){
		int nameIndex;
		if(!isCurrentEntryStart()) nameIndex = correspStartIndex[currentIndex];
		else nameIndex = currentIndex;
		return qName[nameIndex];
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
	public String getAttributeQName(SAttribute attribute){
		int attributesRecordIndex;
		if(!isCurrentEntryStart()) attributesRecordIndex = correspStartIndex[currentIndex];
		else attributesRecordIndex = currentIndex;
		
		int attributeIndex = sattributeIndexMap.get(attribute);		
		//System.out.println(currentIndex+" /"+attributesRecordIndex+"/");
		//System.out.println(currentIndex+" /"+attributeIndex+"/"+attribute);
		//System.out.println(currentIndex+" /"+Arrays.toString(attributeValue[attributesRecordIndex]));
		if(attributeQName[attributesRecordIndex]!=null)return attributeQName[attributesRecordIndex][attributeIndex];
		return null;
	}
	
	public String getAttributeValue(SAttribute attribute){
		int attributesRecordIndex;
		if(!isCurrentEntryStart()) attributesRecordIndex = correspStartIndex[currentIndex];
		else attributesRecordIndex = currentIndex;
		
		int attributeIndex = sattributeIndexMap.get(attribute);		
		//System.out.println(currentIndex+" /"+attributesRecordIndex+"/");
		//System.out.println(currentIndex+" /"+attributeIndex+"/"+attribute);
		//System.out.println(currentIndex+" /"+Arrays.toString(attributeValue[attributesRecordIndex]));
		if(attributeValue[attributesRecordIndex]!=null)return attributeValue[attributesRecordIndex][attributeIndex];
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
	public String getAttributeQName(){
		// when executing an attribute task the currentIndex is always at a start tag
		return attributeQName[currentIndex][currentAttributeIndex];
	}
	
	public String getAttributeValue(){
		// when executing an attribute task the currentIndex is always at a start tag
		return attributeValue[currentIndex][currentAttributeIndex];
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
		
		String[] increasedQN = new String[size];
		System.arraycopy(qName, 0, increasedQN, 0, currentIndex);
		qName = increasedQN;
		
		HashMap<String, String>[] increasedX = new HashMap[size];
		System.arraycopy(xmlns, 0, increasedX, 0, currentIndex);
		xmlns = increasedX;
		
		String[] increasedXB = new String[size];
		System.arraycopy(xmlBase, 0, increasedXB, 0, currentIndex);
		xmlBase = increasedXB;
		
		String[][] increasedAQN = new String[size][];
		for(int i = 0; i< currentIndex; i++){
			if(attributeQName[i] != null){
				String[] newNames = new String[attributeQName[i].length];
				System.arraycopy(attributeQName[i], 0, newNames, 0, attributeQName[i].length);
				increasedAQN[i] = newNames; 
			}				
		}		
		attributeQName = increasedAQN;
		
		String[][] increasedAV = new String[size][];
		for(int i = 0; i< currentIndex; i++){
			if(attributeValue[i] != null){
				String[] newValues = new String[attributeValue[i].length];
				System.arraycopy(attributeValue[i], 0, newValues, 0, attributeValue[i].length);
				increasedAV[i] = newValues; 
			}				
		}	
		attributeValue = increasedAV;
		
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
	
	private String[] getAllQNames(){
		return qName;
	}
	
	private HashMap[] getAllXmlns(){
		return xmlns;
	}
	private String[] getAllXmlBase(){
		return xmlBase;
	}
	
	private String[][] getAllAttributeQNames(){
		return attributeQName;
	}
		
	private String[][] getAllAttributeValues(){
		return attributeValue;
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
		s = s+"Attribute values "+Arrays.deepToString(attributeValue);
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