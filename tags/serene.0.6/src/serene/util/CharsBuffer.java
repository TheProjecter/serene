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