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

package serene.validation.handlers.conflict;

import java.util.BitSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public abstract class ConflictHandler{
	   
	int candidatesCount;	
	BitSet disqualified;	
	
	public ConflictHandler(){
		disqualified = new BitSet();
        candidatesCount = -1;
	}
		
    public void init(int candidatesCount){
        if(candidatesCount < 0)throw new IllegalArgumentException();
        this.candidatesCount = candidatesCount;
    }
	public void clearDisqualified(int candidateIndex){
		disqualified.clear(candidateIndex);		
	}
	
	public void clearAllDisqualified(){
		disqualified.clear();
	}
	
	public void clear(){
		clearAllDisqualified();
        candidatesCount = -1;
	}
	
	public void disqualify(int candidateIndex){
		disqualified.set(candidateIndex);
	}
	
	public boolean isDisqualified(int candidateIndex){
		return disqualified.get(candidateIndex);
	}
	public BitSet getDisqualified(){
        BitSet result = new BitSet();
        result.or(disqualified);
		return result;
	}
	public int getDisqualifiedCount(){
		return disqualified.cardinality();
	}
	/*public int getLength(){
	    return disqualified.length();
	}*/
	public int getNextQualified(int fromIndex){	    
	    if(candidatesCount < 0)throw new IllegalStateException();
		int nextQ = disqualified.nextClearBit(fromIndex);
        if(nextQ >= candidatesCount) return -1;
        return nextQ;
	}
	
	public int getPreviousQualified(int fromIndex){
	    if(candidatesCount < 0)throw new IllegalStateException();
		for(int i = fromIndex-1; i >=0; i--){
			if(!disqualified.get(i))return i;
		}
		return -1;		
	}
    
	public String toString(){
		return "ConflictHandler "+disqualified.toString();
	}
}