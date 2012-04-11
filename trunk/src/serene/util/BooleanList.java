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

public class BooleanList{
    boolean[] list;
	int lastIndex;
    int initialSize;
    int increaseSizeAmount;

    public BooleanList() {
		initialSize = 10;
        increaseSizeAmount = 10;
		lastIndex = -1;
		list = new boolean[initialSize];
    }

    public int size(){
		return lastIndex+1;
	}

    public boolean isEmpty(){
		return lastIndex<0;
	}
    
    public boolean contains(boolean b){
		for(int j = 0; j <= lastIndex; j++){
			if(b == list[j])return true;
		}
		return false;
    }

    public int indexOf(boolean b) {
		for(int j = 0; j <= lastIndex; j++){
			if(b == list[j])return j;
		}
		return -1;
    }

    public int lastIndexOf(boolean b){	
		for(int j = lastIndex; j >=0 ; j--){
			if(b == list[j])return j;
		}
		return -1;
    }

    public boolean[] toArray(){
		boolean[] array = new boolean[lastIndex+1];
		System.arraycopy(list, 0, array, 0, lastIndex+1);
		return array;
	}

    public boolean get(int index) {
		RangeCheck(index);
		return list[index];
	}
    
    public boolean set(int index, boolean b) {
		RangeCheck(index);
	
		boolean oldValue = list[index];
		list[index] = b;
		return oldValue;
    }
    
    public boolean add(boolean b) {
		if(++lastIndex == list.length)increaseSize();
		list[lastIndex] = b;
		return true;
    }

    public void add(int index, boolean b) {
		if (index > ++lastIndex || index < 0)
			throw new IndexOutOfBoundsException(
			"Index: "+index+", Size: "+(lastIndex+1));
	
		if(lastIndex == list.length)increaseSize();
		System.arraycopy(list, index, list, index + 1,
				 lastIndex - index);
		list[index] = b;
    }
	
    public boolean remove(boolean b) {
		int index = -1;
		for(int j = 0; j <= lastIndex; j++){
			if(b == list[j]){
				index = j;
				break;
			}
		}
		if(index == -1)return false;
		
		boolean oldValue = list[index];
	
		int numMoved = lastIndex - index;
		if (numMoved > 0)
			System.arraycopy(list, index+1, list, index,
					 numMoved);
		lastIndex--;
		return  true;
    }

	
	public void trimToSize(){
		int size = lastIndex+1; 
		boolean[] decreased = new boolean[size];
		System.arraycopy(list, 0, decreased, 0, size);
		list = decreased;
	}
    
	public void clear() {	
		lastIndex = -1;
    }
	
	private void increaseSize(){
		boolean[] increased = new boolean[increaseSizeAmount+list.length];
		System.arraycopy(list, 0, increased, 0, list.length);
		list = increased;
	}
	
    public void removeRange(int fromIndex, int toIndex){//toIndex not included in the removed range
		if(fromIndex <= toIndex) throw new IllegalArgumentException();
		int numMoved = lastIndex - toIndex;
        System.arraycopy(list, toIndex, list, fromIndex,
                         numMoved);
		lastIndex -= numMoved;
    }

    private void RangeCheck(int index) {
		if (index >= lastIndex+1)
	    throw new IndexOutOfBoundsException(
		"Index: "+index+", Size: "+(lastIndex+1));
    }   
}