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

import java.util.Arrays;

/**
* Very simple implementation that allows the mapping of an Object key to an int
* value. The actual instance of Object is not kept in the map, it cannot be accessed,
* only used to store and retrieve the value.  
*/
public class ObjectIntHashMap{

	int[][] hashTable;
	int[][] intTable; 
	
	/**
     * The initial capacity - MUST be a power of two.
     */
    static final int INITIAL_CAPACITY = 32;

    /**
     * The maximum capacity - MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor.
     */
    static final float LOAD_FACTOR = 0.75f;
	 
	 /**
     * The number of hashes contained in the hashTable.
     */
    int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     * @serial
     */
    int threshold;
	
	int nullValue;	
	boolean isNullValueSet;
	
	public ObjectIntHashMap(){		
        threshold = (int)(INITIAL_CAPACITY * LOAD_FACTOR);
        hashTable = new int[INITIAL_CAPACITY][];
		intTable = new int[INITIAL_CAPACITY][];
		
		isNullValueSet = false;
	}
	
	private ObjectIntHashMap(int[][] hashTable,
								int[][] intTable,
								int size,
								int threshold,
								int nullValue,	
								boolean isNullValueSet){
        this.hashTable = hashTable;
		this.intTable = intTable;
		this.size = size;
		this.threshold = threshold;
		this.nullValue = nullValue;	
		this.isNullValueSet = isNullValueSet;
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
	
	public int size(){
		return size;
	}
	
	public boolean isEmpty(){
		return size == 0;
	}
	public void clear(){
		for(int i = 0; i < hashTable.length; i++){
			hashTable[i] = null;
			intTable[i] = null;
		}
		size = 0;
	}
	
    public boolean containsKey(Object key){
        int hash = 0;		
        if (key != null) hash = hash(key.hashCode());
			
		//check to see if there is already a record for this key
		int bucketIndex = indexFor(hash, hashTable.length);
		if(hashTable[bucketIndex] != null){
			for (int i = 0; i< hashTable[bucketIndex].length; i++) {
				if (hash == hashTable[bucketIndex][i]){
					return true;
				}
			}			
		}
        return false;
    }
    
	public void put(Object key, int value){	
		int hash = 0;		
        if (key != null) hash = hash(key.hashCode());
			
		//check to see if there is already a record for this key
		int bucketIndex = indexFor(hash, hashTable.length);
		if(hashTable[bucketIndex] != null){
			for (int i = 0; i< hashTable[bucketIndex].length; i++) {
				if (hash == hashTable[bucketIndex][i]){
					//System.out.println("key "+key+": value "+value);
					intTable[bucketIndex][i] = value;
					return;
				}
			}			
		}
		handleSize();
		bucketIndex = indexFor(hash, hashTable.length);
		handleAdd(bucketIndex, hash, value);
    }
		 
    public ObjectIntHashMap getCopy(){
		int[][] newHashTable = new int[hashTable.length][];
		for(int i = 0; i < hashTable.length; i++){
			if(hashTable[i] != null){
				int[] newHashBucket = new int[hashTable[i].length];
				System.arraycopy(hashTable[i], 0, newHashBucket, 0, hashTable[i].length);
				newHashTable[i] = newHashBucket;
			}
		}
		//System.arraycopy(hashTable, 0, newHashTable, 0, hashTable.length);
		
		int[][] newIntTable = new int[intTable.length][];
		for(int i = 0; i < intTable.length; i++){
			if(intTable[i] != null){
				int[] newIntBucket = new int[intTable[i].length];
				System.arraycopy(intTable[i], 0, newIntBucket, 0, intTable[i].length);
				newIntTable[i] = newIntBucket;
			}
		}
		//System.arraycopy(intTable, 0, newIntTable, 0, intTable.length);
		
		return new ObjectIntHashMap(newHashTable,
								newIntTable,
								size,
								threshold,
								nullValue,	
								isNullValueSet);
	}
	
	public void cumulate(ObjectIntHashMap other){
		int[][] otherHashTable = other.hashTable;
		int[][] otherIntTable = other.intTable;
		for(int i = 0; i < otherHashTable.length; i++){
			for(int j = 0; j < otherHashTable[i].length; j++){
				int otherHash =  otherHashTable[i][j];
				int otherValue = otherIntTable[i][j];
				putCumulate(otherHash, otherValue);
			}
		}
	}
	
	private void putCumulate(int hash, int value){			
		//check to see if there is already a record for this key
		int bucketIndex = indexFor(hash, hashTable.length);
		if(hashTable[bucketIndex] != null){
			for (int i = 0; i< hashTable[bucketIndex].length; i++) {
				if (hash == hashTable[bucketIndex][i]){
					//System.out.println("key "+key+": value "+value);
					intTable[bucketIndex][i] = intTable[bucketIndex][i]+value;
					return;
				}
			}			
		}
		handleSize();
		bucketIndex = indexFor(hash, hashTable.length);
		handleAdd(bucketIndex, hash, value);
    }

	
	
	/**
	* If a mapping exists for the key, it is returned, otherwise a null value is 
	* return if any was set, or an IllegalArgumentException is thrown if no null 
    * value is available.
	*/
	public int get(Object key) {
        int hash = 0;		
        if (key != null) hash = hash(key.hashCode());
		int bucketIndex = indexFor(hash, hashTable.length);
		if(hashTable[bucketIndex] == null) return getNullValue();
		for (int i = 0; i< hashTable[bucketIndex].length; i++) {
			if (hash == hashTable[bucketIndex][i]) return intTable[bucketIndex][i];
		}        
       return getNullValue();	   
    }
	
	
	
	/**
	* The mapping for thekey is removed. If a mapping exists for the key, the 
	* value is returned, otherwise a null value is return if any was set, or an 
	* IllegalArgumentException is thrown.
	*/
	public int remove(Object key) {
        int hash = 0;		
        if (key != null) hash = hash(key.hashCode());
		int bucketIndex = indexFor(hash, hashTable.length);
		if(hashTable[bucketIndex] == null) return getNullValue();//key was not mapped, do not change size
		for (int i = 0; i< hashTable[bucketIndex].length; i++) {
			if (hash == hashTable[bucketIndex][i]){
				size--;
				hashTable[bucketIndex][i] = -1;
				int oldValue = intTable[bucketIndex][i];
				intTable[bucketIndex][i] = -1;
				return oldValue;
			}
		}
		return getNullValue();	
    }
	
	
	
	/**
     * Applies a supplemental hash function to a given functionalEquivalenceCode, 
	 * which defends against poor quality hash functions upon which the
	 * functionalEquivalenceCode is based. This is critical because the 
	 * ObjectIntHashMap uses power-of-two length tables and could otherwise
	 * encounter collisions for hash that do not differ in lower bits. 
     */
    private int hash(int h) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);       
    }

    /**
     * Returns index for hash.
     */
    private int indexFor(int hash, int length) {
        return hash & (length-1);
    }
	
	private void handleSize(){
		if (++size >= threshold)
            resize(2 * hashTable.length);
	} 
	
	private void handleAdd(int bucketIndex, int hash, int value){
		int index;		
		if(hashTable[bucketIndex] == null){
			index = 0;
			hashTable[bucketIndex] = new int[1];
			intTable[bucketIndex] = new int[1];
		}else{
			index = hashTable[bucketIndex].length;
			
			int[] increasedCodesBucket = new int[index+1];
			System.arraycopy(hashTable[bucketIndex], 0, increasedCodesBucket, 0, index);
			hashTable[bucketIndex] = increasedCodesBucket;
			
			int[] increasedCandidateIndexesBucket = new int[index+1];
			System.arraycopy(intTable[bucketIndex], 0, increasedCandidateIndexesBucket, 0, index);
			intTable[bucketIndex] = increasedCandidateIndexesBucket;
		}
		hashTable[bucketIndex][index] = hash;
		intTable[bucketIndex][index] = value;
	}
	
	private void resize(int newCapacity) {
        int[][] oldHashTable = hashTable;
		int[][] oldIntTable = intTable;
        int oldCapacity = oldHashTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        hashTable = new int[newCapacity][];
		intTable = new int[newCapacity][];
		threshold = (int)(newCapacity * LOAD_FACTOR);
        transfer(newCapacity, oldHashTable, oldIntTable);
    }
	
	/**
     * Transfers all entries from old tables to the new Tables
     */
    private void transfer(int newCapacity, int[][] srcHash, int[][] srcInt) {         
        for (int j = 0; j < srcHash.length; j++) {
            int[] hashBucket = srcHash[j];
			int[] valuesBucket = srcInt[j];
            if (hashBucket != null) {
                srcHash[j] = null;
				srcInt[j] = null;
				for(int i = 0; i < hashBucket.length; i++){
					int newHashBucketIndex = indexFor(hashBucket[i], newCapacity);
					handleAdd(newHashBucketIndex, hashBucket[i], valuesBucket[i]);
				}
            }
        }
    }
	
	public String toString(){
		//return "ObjectIntHashMap "+hashCode();
		return "ObjectIntHashMap "
					+"values "+Arrays.deepToString(intTable);
	}
}
