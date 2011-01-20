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

import sereneWrite.MessageWriter;

public class CharsBuffer{
	
	char[] content;
	int count;
	int size;
	
	MessageWriter debugWriter;
	public CharsBuffer(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		count = 0;
		size = 100;
		content = new char[size];		
	}

	public void append(char[] characters, int start, int length){	
		if((size-count) < length)increaseSize(length);
		System.arraycopy(characters, start, content, count, length);
		count+=length;	
	}
	private void increaseSize(int length){
		size+=length;
		char[] increased = new char[size];
		System.arraycopy(content, 0, increased, 0, count);
		content = increased;
	}
	
	/*public void trimSpace(){		
		if(count == 0) return;
		int start = 0;
		char c = content[start];
		while ((start < count) 
			&& (c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' ')) {
			c = content[++start];
		}
		c = content[count - 1];
		while ((start < count) 
			&& (c== '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' ')) {			
			c = content[(--count) - 1];
		}		
		char[] trimed = new char[size];
		System.arraycopy(content, start, trimed, 0, count-start);
		content = trimed;
		count = count-start;
	}
	
	public void collapseSpace(){
		
		boolean collapsing = false;		
		int length = 0;	//number of non-space characters in the wait
		int offset = 0;//waiting start point for non-space characters
		char[] collapsed = new char[size];
		int newCount = 0;
		
		for(int i = 0; i< count; i++){
			char c = content[i];
			if(c == '\r' 
			|| c == '\n'
      		|| c == '\t'
			|| c == ' '){
				if(!collapsing){
					collapsing = true;
					System.arraycopy(content, offset, collapsed, newCount, length);
					newCount += length;
					length = 0;
				}
			}
			else{
				if(collapsing){
					collapsing = false;
					collapsed[newCount] = ' ';
					offset = i;
					newCount++;
				}
				length++;		
			}
		}
		if(collapsing){
			collapsed[newCount] = ' ';
			newCount++;
		}
		else{
			System.arraycopy(content, offset, collapsed, newCount, length);
			newCount += length;
		}
		
		content = collapsed;
		count = newCount;
	}*/
	
	public char[] getCharsArray(){
		char[] result = new char[count];
		System.arraycopy(content, 0, result, 0, count);
		return result;
	}
	
	public String getString(){
		return new String(content, 0, count);
	}
	
	public char[] removeCharsArray(){
		char[] result = new char[count];
		System.arraycopy(content, 0, result, 0, count);
		count = 0;
		return result;
	}
	
	public String removeString(){
		String result = new String(content, 0, count); 
		count = 0;		
		return result;
	}
	public void clear(){
		count = 0;
	}
}