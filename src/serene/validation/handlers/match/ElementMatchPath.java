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
import serene.validation.schema.simplified.SElement;

public class ElementMatchPath extends MatchPath{
    SElement match;
           
    ElementMatchPath(MatchPathPool pool){
        super(pool);
        itemId = ELEMENT;
    }
     
    void init(SElement match, SRule[] list, int lastIndex){
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
    
    public ElementMatchPath getCopy(){
        return pool.getElementMatchPath(match, list, lastIndex);
    }
    
    public void addElement(SElement match){
        this.match = match;
        add(match);
    }
    
    public SElement getElement(){
        return match;
    }
    
    public ElementMatchPath getHeadPath(int to){
        if(to > lastIndex || to  < 0) throw new IllegalArgumentException();
        
        ElementMatchPath copy = pool.getElementMatchPath();
        copy.addElement(match);
        for(int i = 1; i <= to; i++){ 
            copy.add(list[i]);
        }
        return copy;
    }
    public MatchPath getTailPath(int from){
        if(from > lastIndex || from  < 0) throw new IllegalArgumentException();
        
        ElementMatchPath copy = pool.getElementMatchPath();        
        for(int i = from; i <= lastIndex; i++){ 
            copy.add(list[i]);
        }
        return copy;
    }
    public ElementMatchPath getHeadPath(SRule to){
        boolean containsFrom = false;        
        ElementMatchPath copy = pool.getElementMatchPath();
        copy.addElement(match);
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
        ElementMatchPath copy = pool.getElementMatchPath();        
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
        return getHeadPath(to);
    }
    public AttributeMatchPath getAttributeHeadPath(int to){
        throw new IllegalStateException();
    }
    public CharsMatchPath getCharsHeadPath(int to){
        throw new IllegalStateException();
    }
    public ElementMatchPath getElementHeadPath(SRule to){
        return getHeadPath(to);
    }
    public AttributeMatchPath getAttributeHeadPath(SRule to){
        throw new IllegalStateException();
    }
    public CharsMatchPath getCharsHeadPath(SRule to){
        throw new IllegalStateException();
    }
    
    
    public String toString(){
        return "ElementMatchPath "+Arrays.toString(list)+" lastIndex="+lastIndex;
    }
}
