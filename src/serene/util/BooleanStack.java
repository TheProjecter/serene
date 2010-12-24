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

public class BooleanStack{
	private boolean[] stack;
	private int topIndex;
	private int size;
	
	private boolean nullValue;	
	private boolean isNullValueSet;
	
	public BooleanStack(){
		size = 5;
		topIndex = -1;
		stack = new boolean[size];
		
		isNullValueSet = false;
	}
	
	
	public void setNullValue(boolean value){
		nullValue = value;	
		isNullValueSet = true;
	}
	public void resetNullValue(){		
		isNullValueSet = false;
	}	
	public boolean getNullValue(){
		if(isNullValueSet)return nullValue;
		throw new IllegalArgumentException();
	}
	
	public void push(boolean b){
		if(++topIndex == size)increaseSize();
		stack[topIndex] = b;
	}
	
	public boolean peek(){
		if(topIndex < 0){
			if(isNullValueSet)return nullValue;
		}
		return stack[topIndex];
	}
	
	public boolean pop(){
		if(topIndex < 0){
			if(isNullValueSet)return nullValue;
		}
		return stack[topIndex--];
	}
	
	public boolean isEmpty(){
		return topIndex < 0;
	}

	public boolean contains(boolean b){
		for(int j = 0; j <= topIndex; j++){
			if(b == stack[j])return true;
		}
		return false;
	}	
	
	public void clear(){
		topIndex = -1;
	}
	
	public int size(){
		return topIndex+1;
	}
	
	private void increaseSize(){
		boolean[] increased = new boolean[++size];
		System.arraycopy(stack, 0, increased, 0, size-1);
		stack = increased;
	}
}