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

public class IntList{
    private int[] list;
	private int lastIndex;
    private int size;
	
    public IntList() {
		size = 5;
		lastIndex = -1;
		list = new int[size];
    }

	
	private IntList(int[] list, int lastIndex, int size) {
		this.list = list;
		this.lastIndex = lastIndex;
		this.size = size;
    }

    public int size(){
		return lastIndex+1;
	}

    public boolean isEmpty(){
		return lastIndex<0;
	}
    
    public boolean contains(int i){
		for(int j = 0; j <= lastIndex; j++){
			if(i == list[j])return true;
		}
		return false;
    }

    public int indexOf(int i) {
		for(int j = 0; j <= lastIndex; j++){
			if(i == list[j])return j;
		}
		return -1;
    }

    public int lastIndexOf(int i){	
		for(int j = lastIndex; j >=0 ; j--){
			if(i == list[j])return j;
		}
		return -1;
    }

    public int[] toArray(){
		int[] array = new int[lastIndex+1];
		System.arraycopy(list, 0, array, 0, lastIndex+1);
		return array;
	}

    public int get(int index) {
		RangeCheck(index);
		return list[index];
	}
    
    public int set(int index, int element) {
		RangeCheck(index);
	
		int oldValue = list[index];
		list[index] = element;
		return oldValue;
    }
    
    public boolean add(int i) {
		if(++lastIndex == size)increaseSize();
		list[lastIndex] = i;
		return true;
    }

    public void add(int index, int element) {
		if (index > ++lastIndex || index < 0)
			throw new IndexOutOfBoundsException(
			"Index: "+index+", Size: "+(lastIndex+1));
	
		if(lastIndex == size)increaseSize();
		System.arraycopy(list, index, list, index + 1,
				 lastIndex - index);
		list[index] = element;
    }
	
    public boolean remove(int i) {
		int index = -1;
		for(int j = 0; j <= lastIndex; j++){
			if(i == list[j]){
				index = j;
				break;
			}
		}
		if(index == -1)return false;
		
		int numMoved = lastIndex - index;
		if (numMoved > 0)
			System.arraycopy(list, index+1, list, index,
					 numMoved);
		lastIndex--;
		return  true;
    }

	public void removeFromIndex(int index) {
	    if(index < 0 || index > lastIndex) throw new IllegalArgumentException();
	    
	    int numMoved = lastIndex - index;
		if (numMoved > 0)
			System.arraycopy(list, index+1, list, index,
					 numMoved);
		lastIndex--;
	}
	
	public void trimToSize(){
		size = lastIndex+1; 
		int[] increased = new int[size];
		System.arraycopy(list, 0, increased, 0, size);
		list = increased;
	}
    
	public void clear() {	
		lastIndex = -1;
    }
	
	private void increaseSize(){
		int[] increased = new int[++size];
		System.arraycopy(list, 0, increased, 0, size-1);
		list = increased;
	}
	
    public void removeRange(int fromIndex, int toIndex){//toIndex not included in the removed range
		if(fromIndex <= toIndex) throw new IllegalArgumentException();
		int numMoved = lastIndex - toIndex;
        System.arraycopy(list, toIndex, list, fromIndex,
                         numMoved);
		lastIndex -= numMoved;
    }

	public int removeLast(){
		if(isEmpty())throw new IllegalStateException();			
		return list[lastIndex--];
	}
	
	public int getGreatestValue(){
		int greatest = -2147483648;
		for(int i = 0; i <= lastIndex; i++){
			if(list[i] > greatest) greatest = list[i]; 
		}
		return greatest;
	}
	
	public int getLowestValue(){
		int lowest = 2147483647;
		for(int i = 0; i <= lastIndex; i++){
			if(list[i] < lowest) lowest = list[i]; 
		}
		return lowest;
	}
	
    private void RangeCheck(int index) {
		if (index >= lastIndex+1)
	    throw new IndexOutOfBoundsException(
		"Index: "+index+", Size: "+(lastIndex+1));
    }
	
	public IntList getCopy(){
		int[] copyList = new int[size];
		System.arraycopy(list, 0, copyList, 0, size);
		return new IntList(copyList, lastIndex, size);
	}
	public String toString(){
		String s = "[";
		for(int i = 0; i <= lastIndex; i++){
			s = s+list[i]+", "; 
		}
		if(s.length() > 1)s = s.substring(0, s.length()-2);
		//return hashCode()+s+"]";
		return s+"]";
	}
}