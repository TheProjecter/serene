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

package serene.internal.datatype.xmlName;

import java.util.HashMap;

import java.text.MessageFormat;

public class XMLNameHandler{
	// ranges as open intervals(allows the transparent treatment of ranges and singles)
	// min at even indexes starting from 0; max at corresponding uneven indexes starting from 1
	int[] startCharRanges; 
	int[] charRanges;
	
	int[] usedStartCharMin;
	int[] usedStartCharMax;
	int[] usedStartCharIndex;//index of min in the ranges; index of max is +1 
	int usedStartCharLength;

	int[] usedCharMin;
	int[] usedCharMax;
	int[] usedCharIndex;//index of min in the ranges; index of max is +1 
	int usedCharLength;	
	
	boolean severalColons;
	boolean reserved;

	HashMap<String, String> messages; 
	String message;
	
	public XMLNameHandler(){	
		usedStartCharMin = new int[10];
		usedStartCharMax = new int[10];
		usedStartCharIndex = new int[10];
		usedStartCharLength = 0;
		
		usedCharMin = new int[10];
		usedCharMax = new int[10];
		usedCharIndex = new int[10];
		usedCharLength = 0;
		
		severalColons = false;
		reserved = true;
		messages = new HashMap<String, String>();
		messages.put("notValidNameCharacter", "Code point(hex) {0} {1} is not a valid name character.");
		messages.put("emptyString", "Name is empty string."); 
		messages.put("prefixEmptyString", "Prefix is empty string.");
		messages.put("localNameEmptyString", "Local name is empty string.");
		messages.put("severalColons", "Several colons.");
		messages.put("reserved", "Names starting with (\'x\'|\'X\')(\'m\'|\'M\')(\'l\'|\'L\') are reserved for standardization.");
	}
	
	public void version(String version){
		version.trim();
		if(version.equals("1.0")){
			startCharRanges = XML10CharRanges.startCharRanges;
			charRanges = XML10CharRanges.charRanges;
		}
		else if(version.equals("1.1")){
			startCharRanges = XML11CharRanges.startCharRanges;
			charRanges = XML11CharRanges.charRanges;
		}
	}
	
	public void handleNCName(String candidate)throws NameInvalidException, NameReservedException{
		message = "";		
		int length = candidate.length();			
		if (length == 0){
			message = message + " "+ (messages.get("emptyString"));
			return;
		}
		
		int codePoint = candidate.codePointAt(0);				
		
		if(codePoint == 'x' || codePoint == 'X'){
			reserved = true;
		}
		else if(codePoint == ':' || !isNameStartChar(codePoint)){
			int[] cp =  {codePoint};
			message = message + " "+ (MessageFormat.format(
							messages.get("notValidNameCharacter"),
							Integer.toHexString(codePoint),
							"\'"+new String(cp,0, 1)+"\'"));
			reserved = false;
		}
		else{
			reserved = false;
		}
		
		int i;
		if(codePoint > 0xffff){
			i = 2;			
		}
		else{ 
			i = 1;
		}
		for (; i < length; i++){
			codePoint = candidate.codePointAt(i);
			if(codePoint > 0xffff){
				i++;				
			}
			
			if (!isNCNameChar(codePoint)){
				int[] cp =  {codePoint};
				message = message + " "+ (MessageFormat.format(
												messages.get("notValidNameCharacter"),
												Integer.toHexString(codePoint),
												"\'"+new String(cp,0, 1)+"\'"));
				
			}
			
			if(reserved && i == 1 && (codePoint != 'm' && codePoint != 'M')){
				reserved = false;									
			}								
			else if(reserved && i == 2 && (codePoint == 'l' || codePoint == 'L')){					
				//System.out.println("warning names starting with (\'x\' | ...) are reserved for standardization");
			}
		}		
		if(message.length() != 0){
			if(reserved){
				message = message+"\n"+messages.get("reserved");
			}			
			throw new NameInvalidException(message.trim());
		}
		if(reserved){			
			message = message+"\n"+messages.get("reserved");
			throw new NameReservedException(message.trim());
		}
	}
	
	public void handleQName(String candidate) throws NameInvalidException, NameReservedException{
		message = "";		
		int length = candidate.length();			
		if (length == 0){				
			message = message + " "+ (messages.get("emptyString"));
			return;				
		}
		
		severalColons = false;
		reserved = false;	
		
		int codePoint = candidate.codePointAt(0);
		boolean localReserved;
		if(codePoint == 'x' || codePoint == 'X'){
			localReserved = true;
		}else if(codePoint == ':'){				
			handleQName(candidate, 1);
			message = message + " "+ (messages.get("prefixEmptyString"));
			//if(reserved){
				//System.out.println("warning names starting with ("\'"x"\'" | ...) are reserved for standardization");					
			//}
			if(severalColons){
				message = message + " "+ (messages.get("severalColons"));
			}
			if(message.length() != 0){
				if(reserved){
					message = message+"\n"+messages.get("reserved");
				}				
				throw new NameInvalidException(message.trim());
			}
			if(reserved){
				message = message+"\n"+messages.get("reserved");
				throw new NameReservedException(message.trim());
			}
			return;
		}else if (!isNameStartChar(codePoint)){
			int[] cp =  {codePoint};
			message = message + " "+ (MessageFormat.format(
										messages.get("notValidNameCharacter"),
										Integer.toHexString(codePoint),
										"\'"+new String(cp,0, 1)+"\'"));
			localReserved  = false;
		}else{
			localReserved = false;
		}
		
		int i;
		if(codePoint > 0xffff){
			i = 2;			
		}
		else{
			i = 1;
		}
		for (; i < length; i++){
			codePoint = candidate.codePointAt(i);		
			if(codePoint > 0xffff){					
				i++;				
			}
			
			if(codePoint == ':' && reserved){
				handleReservedQName(candidate, i+1);
				//System.out.println("warning names starting with ("\'"x"\'" | ...) are reserved for standardization");
				if(severalColons){
					message = message + " "+ (messages.get("severalColons"));						
				}
				//return;
				if(message.length() != 0){
					if(reserved){
						message = message+"\n"+messages.get("reserved");
					}					
					throw new NameInvalidException(message.trim());
				}
				if(reserved){
					message = message+"\n"+messages.get("reserved");
					throw new NameReservedException(message.trim());
				}
				return;
			}
			else if(codePoint == ':' && !reserved){
				handleQName(candidate, i+1);				
				//if(reserved){
					//System.out.println("warning names starting with ("\'"x"\'" | ...) are reserved for standardization");						
				//}
				if(severalColons){
					message = message + " "+ (messages.get("severalColons"));
					//severalColons = false;
					//return;
				}
				//TODO handle prefix in context
				//return;
				if(message.length() != 0){
					if(reserved){
						message = message+"\n"+messages.get("reserved");
					}					
					throw new NameInvalidException(message.trim());
				}
				if(reserved){
					message = message+"\n"+messages.get("reserved");
					throw new NameReservedException(message.trim());
				}
				return;
			}
			
			if (!isNCNameChar(codePoint)){
				int[] cp =  {codePoint};
				message = message + " "+ (MessageFormat.format(
												messages.get("notValidNameCharacter"),
												Integer.toHexString(codePoint),
												"\'"+new String(cp,0, 1)+"\'"));			
			}
			
			if(localReserved && i == 1 && (codePoint != 'm' && codePoint != 'M')){
				localReserved = false;									
			}								
			else if(localReserved && i == 2 && (codePoint == 'l' || codePoint == 'L')){					
				reserved = true;
			}
		}
		//if(reserved){
			//System.out.println("warning names starting with ("\'"x"\'" | ...) are reserved for standardization");				
		//}			
		//TODO handle NO prefix in context
		if(message.length() != 0){
			if(reserved){
				message = message+"\n"+messages.get("reserved");
			}
			throw new NameInvalidException(message.trim());
		}
		if(reserved){
			message = message+"\n"+messages.get("reserved");
			throw new NameReservedException(message.trim());
		}
		// no error, do nothing
		//return true;
	}
	
	
	private void handleQName(String candidate, int startIndex){
		int length = candidate.length();			
		if (length == startIndex && !severalColons){				
			message = message + " "+ (messages.get("localNameEmptyString"));
			return;				
		}
		
		int codePoint = candidate.codePointAt(startIndex);			
		boolean localReserved;
		if(codePoint == 'x' || codePoint == 'X'){
			localReserved = true;				
		}
		else if(codePoint == ':'){	
			severalColons = true;
			handleQName(candidate, startIndex+1);				
			return;
		}
		else if (!isNameStartChar(codePoint)){
			int[] cp =  {codePoint};
			message = message + " "+ (MessageFormat.format(
											messages.get("notValidNameCharacter"),
											Integer.toHexString(codePoint),
											"\'"+new String(cp,0, 1)+"\'"));
			localReserved  = false;
		}
		else{
			localReserved = false;
		}
		
		//int j = 1;
		int i = startIndex;
		if(codePoint > 0xffff){
			i++;			
		}			
		for (; i < length; i++){
			codePoint = candidate.codePointAt(i);		
			if(codePoint > 0xffff){
				i++;				
			}
			if(codePoint == ':' && reserved){
				severalColons = true;					
				handleReservedQName(candidate, i+1);
				return;
			}
			else if(codePoint == ':' && !reserved){
				severalColons = true;					
				handleQName(candidate, i+1);
				return;
			}
			
			if (!isNCNameChar(codePoint)){					
				int[] cp =  {codePoint};
				message = message + " "+ (MessageFormat.format(
														messages.get("notValidNameCharacter"),
														Integer.toHexString(codePoint),
														"\'"+new String(cp,0, 1)+"\'"));			
			}
			
			if(localReserved && i == 1 && (codePoint != 'm' && codePoint != 'M')){
				localReserved = false;									
			}								
			else if(localReserved && i == 2 && (codePoint == 'l' || codePoint == 'L')){					
				reserved = true;
			}
		}
	}
	
	private void handleReservedQName(String candidate, int startIndex){
		int length = candidate.length();			
		if (length == startIndex){				
			message = message + " "+ (messages.get("localNameEmptyString"));
			return;				
		}
		
		int codePoint = candidate.codePointAt(startIndex);			
		
		if(codePoint == ':'){	
			severalColons = true;
			handleQName(candidate, 1);				
			return;
		}
		else if (!isNameStartChar(codePoint)){
			int[] cp =  {codePoint};
				message = message + " "+ (MessageFormat.format(
														messages.get("notValidNameCharacter"),
														Integer.toHexString(codePoint),
														"\'"+new String(cp,0, 1)+"\'"));			
		}
					
		int i = startIndex;
		if(codePoint > 0xffff){
			i++;			
		}			
		for (; i < length; i++){
			codePoint = candidate.codePointAt(i);		
			if(codePoint > 0xffff){
				i++;				
			}
			if(codePoint == ':'){
				severalColons = true;					
				handleReservedQName(candidate, i+1);
				return;
			}
							
			if (!isNCNameChar(codePoint)){
				int[] cp =  {codePoint};
				message = message + " "+ (MessageFormat.format(
												messages.get("notValidNameCharacter"),
												Integer.toHexString(codePoint),
												"\'"+new String(cp,0, 1)+"\'"));
			}				
		}
	}
	
	private boolean isNameStartChar(int searchVal){
		if(searchVal<= usedStartCharMin[0]){				
			return isInStartCharRanges(searchVal, 0, usedStartCharIndex[0], 0);
			//int result = inRange(searchVal, extenderRanges, 0, usedStartCharIndex[0]);
			//low = 0;
			//high = usedStartCharIndex[0];
		}		
		else if(searchVal < usedStartCharMax[0]){
			return true;
		}			
		int j;
		for(j = 1; j < usedStartCharLength; j++){
			if(searchVal <= usedStartCharMin[j]){
				if(searchVal < usedStartCharMax[j-1]){
					return true;
				}
				else{
					//low = usedStartCharIndex[j-1]+1;
					//high = useStartCharIndex[j];						
					//int result = inRange(searchVal, extenderRanges, usedStartCharIndex[j-1]+1, usedStartCharIndex[j]);						
					return isInStartCharRanges(searchVal,  usedStartCharIndex[j-1]+1, usedStartCharIndex[j], j);
				}
			}
		}
		if(searchVal < usedStartCharMax[j-1]){
			return true;
		}
		return isInStartCharRanges(searchVal, usedStartCharIndex[j-1], startCharRanges.length-1, usedStartCharLength);
		//int result = inRange(searchVal, extenderRanges, usedStartCharIndex[j], extenderRanges.length-1);			
	}
	
	private boolean isInStartCharRanges(int searchVal, int low, int high, int fromIndex){
		while(low <= high){
			int mid = (low+high)/2;
			int midVal = startCharRanges[mid];
			
			if(midVal < searchVal){
				low = mid+1;				
			}
			else if(midVal > searchVal){
				high = mid -1;
			}
			else{
				//searchVal found - it means that it equals one of the (open) range limits					
				return false;					
			}
		}
		//now: 
		// low is the insertion point (index of upper limit of the range within which the searchVal can be found) 
		// high the index of the lower limit of the range
		if((high&1) == 1){			
			return false;
		}
		
		usedStartCharLength++;			
		
		if(usedStartCharLength == usedStartCharMin.length){ 
			increaseSize(usedStartCharMin);
			increaseSize(usedStartCharMax);
			increaseSize(usedStartCharIndex);
		}
		
		int tempMin = startCharRanges[high];
		int tempMax = startCharRanges[low];
		int tempIndex = high;
		int temp = 0;
		for(; fromIndex < usedStartCharLength; fromIndex++){
			temp = usedStartCharMin[fromIndex];
			usedStartCharMin[fromIndex] = tempMin;
			tempMin = temp;
			
			temp = usedStartCharMax[fromIndex];
			usedStartCharMax[fromIndex] = tempMax;
			tempMax = temp;
			
			temp = usedStartCharIndex[fromIndex];
			usedStartCharIndex[fromIndex] = tempIndex;
			tempIndex = temp;
		}
		return true;
	}
	
	private boolean isNCNameChar(int searchVal){
		return searchVal != ':' && isNameChar(searchVal);
	}
	
	private boolean isNameChar(int searchVal){			
		if(searchVal<= usedCharMin[0]){				
			return isInCharRanges(searchVal, 0, usedCharIndex[0], 0);
			//int result = inRange(searchVal, extenderRanges, 0, usedCharIndex[0]);
			//low = 0;
			//high = usedCharIndex[0];
		}	
		else if(searchVal < usedCharMax[0]){
			return true;
		}
		int j;
		for(j = 1; j < usedCharLength; j++){				
			if(searchVal <= usedCharMin[j]){	
				
				if(searchVal < usedCharMax[j-1]){						
					return true;
				}
				else{
					//low = usedCharIndex[j-1]+1;
					//high = useCharIndex[j];						
					//int result = inRange(searchVal, extenderRanges, usedCharIndex[j-1]+1, usedCharIndex[j]);						
					return isInCharRanges(searchVal,  usedCharIndex[j-1]+1, usedCharIndex[j], j);
				}
			}
		}			
		if(searchVal < usedCharMax[j-1]){				
			return true;
		}
		return isInCharRanges(searchVal, usedCharIndex[j-1], charRanges.length-1, usedCharLength);
		//int result = inRange(searchVal, extenderRanges, usedCharIndex[j], extenderRanges.length-1);			
	}
	
	private boolean isInCharRanges(int searchVal, int low, int high, int fromIndex){			
		while(low <= high){
			int mid = (low+high)/2;
			int midVal = charRanges[mid];
			
			if(midVal < searchVal){
				low = mid+1;				
			}
			else if(midVal > searchVal){
				high = mid -1;
			}
			else{
				//searchVal found - it means that it equals one of the (open) range limits					
				return false;					
			}
		}
		//now: 
		// low is the insertion point (index of upper limit of the range within which the searchVal can be found) 
		// high the index of the lower limit of the range
		if((low&1) == 0){			
			return false;
		}
		
		usedCharLength++;			
		
		if(usedCharLength == usedCharMin.length){ 
			increaseSize(usedCharMin);
			increaseSize(usedCharMax);
			increaseSize(usedCharIndex);
		}
		
		int tempMin = charRanges[high];
		int tempMax = charRanges[low];
		int tempIndex = high;
		int temp = 0;
		for(; fromIndex < usedCharLength; fromIndex++){
			temp = usedCharMin[fromIndex];
			usedCharMin[fromIndex] = tempMin;
			tempMin = temp;
			
			temp = usedCharMax[fromIndex];
			usedCharMax[fromIndex] = tempMax;
			tempMax = temp;
			
			temp = usedCharIndex[fromIndex];
			usedCharIndex[fromIndex] = tempIndex;
			tempIndex = temp;
		}
		return true;
	}
	
	private void increaseSize(int[] intArray){		
		int[] increased = new int[intArray.length+10];
		System.arraycopy(intArray, 0, increased, 0, intArray.length);
		intArray = increased;
	}		
}
