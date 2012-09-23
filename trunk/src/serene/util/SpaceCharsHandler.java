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

package serene.util;

public class SpaceCharsHandler{

	public SpaceCharsHandler(){		
	}
	
	public boolean containsSpace(char[] chars){
	    if(chars.length == 0)throw new IllegalArgumentException();
        for(int i = 0; i < chars.length; i++){
            char c = chars[i];
            if((c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' '))return true;
        }
        return false;
	} 
    public boolean isSpace(char[] chars){
        if(chars.length == 0)throw new IllegalArgumentException();
        for(int i = 0; i < chars.length; i++){
            char c = chars[i];
            if(!(c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' '))return false;
        }
        return true;
    }
    
	public char[] trimSpace(char[] chars){	    
		int count = chars.length;		
		if(count == 0) return chars;
		int start = 0;
		char c = chars[start];
		while ((c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' ')) {			
			if(++start == count) return new char[0];
			c = chars[start];
		}		
		c = chars[count-1];
		while ((c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' ')) {			
			if(start == --count) return new char[0];
			c = chars[count-1];
		}		
		char[] trimed = new char[count-start];
		System.arraycopy(chars, start, trimed, 0, count-start);
		return trimed;		
	}
	
	public char[] trimHeadSpace(char[] chars){	    
		int count = chars.length;		
		if(count == 0) return chars;
		int start = 0;
		char c = chars[start];
		while ((c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' ')) {			
			if(++start == count) return new char[0];
			c = chars[start];
		}		
		char[] trimed = new char[count-start];
		System.arraycopy(chars, start, trimed, 0, count-start);
		return trimed;		
	}
	
	public char[] trimTailSpace(char[] chars){	    
		int count = chars.length;		
		if(count == 0) return chars;
		char c = chars[count-1];
		while ((c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' ')) {			
			if(0 == --count) return new char[0];
			c = chars[count-1];
		}		
		char[] trimed = new char[count];
		System.arraycopy(chars, 0, trimed, 0, count);
		return trimed;		
	}
	
	/**
	* Return the index of the first non space character.
	*/
	public int getHeadSpaceEnd(char[] chars){
	    int count = chars.length;		
		if(count == 0) return -1;
		int start = 0;
		char c = chars[start];
		while ((c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' ')) {			
			if(++start == count) return start;
			c = chars[start];
		}		
		return start;	
	}
	
	/**
	* Return the index of the first space character of the end whitespace, or 
	* length if there is no whitespace at the end.  
	*/
	public int getTailSpaceStart(char[] chars){	    
		int count = chars.length;		
		if(count == 0) return 0;
		char c = chars[count-1];
		while ((c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' ')) {			
			if(0 == --count) return count;
			c = chars[count-1];
		}		
		return count;		
	}
	
	
	public char[] collapseSpace(char[] chars){
		int count = chars.length;
		int queueOffset = 0;//start point for queued non-space characters
		int queueLength = 0;//number of non-space characters in the queue
		
		char[] collapsed = new char[count];
		int collapsedCount = 0;// number of characters in the collapsed array			
		boolean collapsing = false;
		
		
		
		for(int i = 0; i< count; i++){
			char c = chars[i];
			if(c == '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' '){
				if(!collapsing){
					collapsing = true;
					System.arraycopy(chars, queueOffset, collapsed, collapsedCount, queueLength);
					collapsedCount += queueLength;
					queueLength = 0;
				}
			}
			else{
				if(collapsing){
					collapsing = false;
					collapsed[collapsedCount] = ' ';
					queueOffset = i;
					collapsedCount++;
				}
				queueLength++;		
			}
		}
		if(collapsing){
			collapsed[collapsedCount] = ' ';
			collapsedCount++;
		}
		else{
			System.arraycopy(chars, queueOffset, collapsed, collapsedCount, queueLength);
			collapsedCount += queueLength;
		}
		
		char[] result = new char[collapsedCount];
		System.arraycopy(collapsed, 0, result, 0, collapsedCount);
		
		return result;
	}
	
	
	public char[][] removeSpace(char[] chars){		
		int count = chars.length;
		int queueOffset = 0;//start point for queued non-space characters
		int queueLength = 0;//number of non-space characters in the queue
		
		char[][]tokens = new char[count/2+1][];
		int tokensCount = 0;//number of tokens in the array			
		boolean removing = false;	
		
		for(int i = 0; i< count; i++){
			char c = chars[i];
			if(c == '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' '){
				if(!removing){
					removing = true;
                    if(queueLength > 0){ // for when the first character is whitespace
                        char[] token = new char[queueLength];					
                        System.arraycopy(chars, queueOffset, token, 0, queueLength);
                        tokens[tokensCount++] = token;					
                        queueLength = 0;
                    }
				}
			}
			else{
				if(removing){
					removing = false;
					queueOffset = i;
				}
				queueLength++;		
			}
		}
		if(!removing && queueLength > 0){
			char[] token = new char[queueLength];					
			System.arraycopy(chars, queueOffset, token, 0, queueLength);
			tokens[tokensCount++] = token;
		}
		
		char[][] result = new char[tokensCount][];       
		for(int i = 0; i< tokensCount; i++){
            char[] newToken = new char[tokens[i].length];
            System.arraycopy(tokens[i], 0, newToken, 0, tokens[i].length);
            result[i] = newToken; 
		}
		
		return result;
	}
}