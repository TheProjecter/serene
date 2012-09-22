/*
Copyright 2012 Radu Cernuta 

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

package serene.validation.handlers.match;

import serene.validation.schema.simplified.SRule;

import serene.Reusable;

public abstract class MatchPath  implements Reusable{
    // At index 0 is the match, at lastIndex is the context top pattern.
    public final static int ELEMENT = 0;
    public final static int ATTRIBUTE = 1;
    public final static int CHARS = 2;
    
    
    SRule[] list;
	int lastIndex;
    int initialSize;
    int increaseSizeAmount;
	
    MatchPathPool pool;
    
    int itemId;
    
    public MatchPath(MatchPathPool pool){
		initialSize = 10;
        increaseSizeAmount = 10;
		lastIndex = -1;
		list = new SRule[initialSize];
		
		this.pool = pool;
    }

	
    public int size(){
		return lastIndex+1;
	}

    public boolean isEmpty(){
		return lastIndex<0;
	}
    
    public boolean contains(SRule r){
        if (lastIndex < 0)return false;
		for(int j = 0; j <= lastIndex; j++){
			if(r == list[j])return true;
		}
		return false;
    }

    public int indexOf(SRule r) {
		for(int j = 0; j <= lastIndex; j++){
			if(r == list[j])return j;
		}
		return -1;
    }

    public SRule[] toArray(){
		SRule[] array = new SRule[lastIndex+1];
		System.arraycopy(list, 0, array, 0, lastIndex+1);
		return array;
	}

    public SRule get(int index) {
		RangeCheck(index);
		return list[index];
	}
    
    /*public SRule set(int index, SRule r) {
		RangeCheck(index);
	
		SRule oldValue = list[index];
		list[index] = r;
		return oldValue;
    }*/
    
    public boolean add(SRule r){
		if(++lastIndex == list.length)increaseSize();
		list[lastIndex] = r;
		return true;
    }

    /*public boolean remove(SRule r) {
        if(lastIndex < 0)return false;
        
		int index = -1;
		for(int j = 0; j <= lastIndex; j++){
			if(r == list[j]){
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
    }*/

	/*public void removeFromIndex(int index) {
	    if(index < 0 || index > lastIndex) throw new IllegalArgumentException();
	    
	    int numMoved = lastIndex - index;
		if (numMoved > 0)
			System.arraycopy(list, index+1, list, index,
					 numMoved);
		lastIndex--;
	}*/
	
	public void trimToSize(){
		SRule[] decreased = new SRule[lastIndex+1];
		System.arraycopy(list, 0, decreased, 0, lastIndex+1);
		list = decreased;
	}
    
	public void clear() {	
		lastIndex = -1;
    }
	
	private void increaseSize(){
		SRule[] increased = new SRule[increaseSizeAmount+list.length];
		System.arraycopy(list, 0, increased, 0, list.length);
		list = increased;
	}
		
    private void RangeCheck(int index) {
		if (index >= lastIndex+1)
	    throw new IndexOutOfBoundsException(
		"Index: "+index+", Size: "+(lastIndex+1));
    }
	
    public int getItemId(){
        return itemId; 
    }
    
    //Always includes the limit too.
    public abstract MatchPath getHeadPath(int to);
    public abstract ElementMatchPath getElementHeadPath(int to);
    public abstract AttributeMatchPath getAttributeHeadPath(int to);
    public abstract CharsMatchPath getCharsHeadPath(int to);
    public abstract MatchPath getTailPath(int from);
    public abstract MatchPath getHeadPath(SRule to);
    public abstract ElementMatchPath getElementHeadPath(SRule to);
    public abstract AttributeMatchPath getAttributeHeadPath(SRule to);
    public abstract CharsMatchPath getCharsHeadPath(SRule to);
    public abstract MatchPath getTailPath(SRule from);
    
    
    // from inclusive to exclusive
    public SRule[] getArray(int from, int to){        
        if(from  < 0 
            || to > lastIndex+1 
            || from >= to) throw new IllegalArgumentException();
        
        int l = to-from;
        SRule[] result = new SRule[l];
        System.arraycopy(list, from, result, 0, l);
        return result;
    }
    
    // to exclusive
    public SRule[] getHeadArray(int to){
        if(to > lastIndex+1 || to  < 0) throw new IllegalArgumentException();
        
        SRule[] result = new SRule[to];
        System.arraycopy(list, 0, result, 0, to);
        return result;
    }
    // from inclusive
    public SRule[] getTailArray(int from){
        if(from > lastIndex || from  < 0) throw new IllegalArgumentException();
        
        int l = lastIndex-from+1;
        SRule[] result = new SRule[l];
        System.arraycopy(list, from, result, from, l);
        return result;
    }
    // to exclusive
    public SRule[] getHeadArray(SRule to){      
        for(int i = 0; i <= lastIndex; i++){
            if(list[i] == to){        
                SRule[] result = new SRule[i];
                System.arraycopy(list, 0, result, 0, i);
                return result;
            }
        } 
        throw new IllegalArgumentException();
    }
    // from inclusive
    public SRule[] getTailArray(SRule from){  
        for(int i = 0; i <= lastIndex; i++){
            if(list[i] == from){        
                int l = lastIndex - i +1;
                
                SRule[] result = new SRule[l];
                System.arraycopy(list, i, result, i, l);
                return result;
            }
        }
        throw new IllegalArgumentException();
    }
	
    public SRule getChild(SRule parent){
        for(int i = 0; i <= lastIndex; i++){
            if(list[i] == parent){
                if(i == 0) throw new IllegalArgumentException();
                return list[i-1];                
            }
        }        
        throw new IllegalArgumentException();
    }
     
    public SRule getParent(SRule child){
        for(int i = 0; i <= lastIndex; i++){
            if(list[i] == child){
                if(i == lastIndex) throw new IllegalArgumentException();
                return list[i+1];                
            }
        }        
        throw new IllegalArgumentException();
    }
    
    
    public boolean ruleRequiresDoubleHandler(SRule rule){
        boolean process = false;
        boolean isInterleaved = false;
        boolean hasMultipleCardinality = false;
        
        for(int i = 0; i <= lastIndex; i++){
            if(process){
                if(list[i].specifiesInterleaving()){
                    isInterleaved = true;
                    break;
                }else if(!list[i].isIntermediary()){
                    break;
                }else if(!hasMultipleCardinality){
                    hasMultipleCardinality = list[i].hasMultipleCardinality();
                }            
            }else if( list[i] == rule){
                process = true;
                hasMultipleCardinality = rule.hasMultipleCardinality();
            }
        }
        return hasMultipleCardinality && isInterleaved;
    }
    
    public boolean ruleHasMultipleCardinality(SRule rule){
        boolean process = false;
        boolean hasMultipleCardinality = false;
        
        for(int i = 0; i <= lastIndex; i++){
            if(process){
                if(!rule.isIntermediary()){
                    break;
                }else if(!hasMultipleCardinality){
                    hasMultipleCardinality = rule.hasMultipleCardinality();
                }            
            }else if( list[i] == rule){                
                if(rule.hasMultipleCardinality()){
                    return true;
                }
                process = true;
            }
        }
        return hasMultipleCardinality;
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
