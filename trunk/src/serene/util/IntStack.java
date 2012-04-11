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

public class IntStack{
	int[] stack;
	int topIndex;
	int initialSize;
	int increaseSizeAmount;
	
	int nullValue;	
	boolean isNullValueSet;
	
	public IntStack(){
		initialSize = 10;
	    increaseSizeAmount = 10;
		topIndex = -1;
		stack = new int[initialSize];
		
		isNullValueSet = false;
	}
	
	public void setNullValue(int value){
		nullValue = value;	
		isNullValueSet = true;
	}
	public void resetNullValue(){		
		isNullValueSet = false;
	}	
	public int getNullValue(){
		if(isNullValueSet)return nullValue;
		throw new IllegalArgumentException();
	}
	
	public void push(int i){
		if(++topIndex == stack.length)increaseSize();
		stack[topIndex] = i;
	}
	
	public int peek(){
		if(topIndex < 0){
			if(isNullValueSet)return nullValue;
		}
		return stack[topIndex];
	}
	
	public int pop(){
		if(topIndex < 0){
			if(isNullValueSet)return nullValue;
		}
		return stack[topIndex--];
	}
	
	public boolean isEmpty(){
		return topIndex < 0;
	}

	public boolean contains(int i){
		for(int j = 0; j <= topIndex; j++){
			if(i == stack[j])return true;
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
		int[] increased = new int[increaseSizeAmount+stack.length];
		System.arraycopy(stack, 0, increased, 0, stack.length);
		stack = increased;
	}
}