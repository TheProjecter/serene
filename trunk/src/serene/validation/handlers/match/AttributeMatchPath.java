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

import java.util.Arrays;

import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SAttribute;

public class AttributeMatchPath extends MatchPath{
    SAttribute match;
           
    AttributeMatchPath(MatchPathPool pool){
        super(pool);
        itemId = ATTRIBUTE;
    }
     
    void init(SAttribute match, SRule[] list, int lastIndex){
        if(lastIndex >= this.list.length)adjustSize(lastIndex);
        
        System.arraycopy(list, 0, this.list, 0, lastIndex+1);
        this.match = match;
        this.lastIndex = lastIndex;
    }
        
    public void recycle(){
        lastIndex = -1;
        match = null;
        pool.recycle(this);
        
    }
    
    public AttributeMatchPath getCopy(){
        //return pool.getAttributeMatchPath(match, list, lastIndex);
        AttributeMatchPath copy = pool.getAttributeMatchPath(match, list, lastIndex);
        return copy;
    }
    
    public void addAttribute(SAttribute match){        
        this.match = match;
        add(match);        
    }
    
    public SAttribute getAttribute(){
        return match;
    }
    
    public AttributeMatchPath getHeadPath(int to){
        if(to > lastIndex || to  < 0) throw new IllegalArgumentException();
        
        AttributeMatchPath copy = pool.getAttributeMatchPath();
        copy.addAttribute(match);
        for(int i = 1; i <= to; i++){ 
            copy.add(list[i]);
        }
        return copy;
    }    
    public MatchPath getTailPath(int from){
        if(from > lastIndex || from  < 0) throw new IllegalArgumentException();
        
        AttributeMatchPath copy = pool.getAttributeMatchPath();        
        for(int i = from; i <= lastIndex; i++){ 
            copy.add(list[i]);
        }
        return copy;
    }
    public AttributeMatchPath getHeadPath(SRule to){
        boolean containsFrom = false;        
        AttributeMatchPath copy = pool.getAttributeMatchPath();
        copy.addAttribute(match);
        for(int i = 1; i <= lastIndex; i++){ 
            copy.add(list[i]);
            if(list[i] == to){
                containsFrom = true;
                break;
            }
        }
        if(!containsFrom) throw new IllegalArgumentException();
        return copy;
    }
    public MatchPath getTailPath(SRule from){
        boolean containsFrom = false;        
        AttributeMatchPath copy = pool.getAttributeMatchPath();        
        for(int i = 0; i <= lastIndex; i++){            
            if(list[i] == from){
                containsFrom = true;                
            }
            if(containsFrom){
                copy.add(list[i]);
            }
        }
        if(!containsFrom) throw new IllegalArgumentException();
        return copy;
    }  
    
    
    public ElementMatchPath getElementHeadPath(int to){
        throw new IllegalStateException();
    }
    public AttributeMatchPath getAttributeHeadPath(int to){
        return getHeadPath(to);
    }
    public CharsMatchPath getCharsHeadPath(int to){
        throw new IllegalStateException();
    }
    public ElementMatchPath getElementHeadPath(SRule to){
        throw new IllegalStateException();
    }
    public AttributeMatchPath getAttributeHeadPath(SRule to){
        return getHeadPath(to);
    }
    public CharsMatchPath getCharsHeadPath(SRule to){
        throw new IllegalStateException();
    }
    
    public String toString(){
        return "AttributeMatchPath "+Arrays.toString(list)+" lastIndex="+lastIndex;
    }
}
