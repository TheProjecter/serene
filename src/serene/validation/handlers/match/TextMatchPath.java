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
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SText;

public class TextMatchPath extends CharsMatchPath{
    SText match;
           
    TextMatchPath(MatchPathPool pool){
        super(pool);
    }
     
    void init(SText match, SRule[] list, int lastIndex){
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
    
    
    public TextMatchPath getCopy(){
        return pool.getTextMatchPath(match, list, lastIndex);
    }
    
    public void addText(SText match){
        this.match = match;
        add(match);
    }
    
    public SText getText(){
        return match;
    }
    
    public SPattern getChars(){
        return match;
    }
    
    public TextMatchPath getHeadPath(int to){
        if(to > lastIndex || to  < 0) throw new IllegalArgumentException();
        
        TextMatchPath copy = pool.getTextMatchPath();
        copy.addText(match);
        for(int i = 1; i <= to; i++){ 
            copy.add(list[i]);
        }
        return copy;
    }
    public MatchPath getTailPath(int from){
        if(from > lastIndex || from  < 0) throw new IllegalArgumentException();
        
        TextMatchPath copy = pool.getTextMatchPath();        
        for(int i = from; i <= lastIndex; i++){ 
            copy.add(list[i]);
        }
        return copy;
    }
    public TextMatchPath getHeadPath(SRule to){
        boolean containsFrom = false;        
        TextMatchPath copy = pool.getTextMatchPath();
        copy.addText(match);
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
        TextMatchPath copy = pool.getTextMatchPath();        
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
        throw new IllegalStateException();
    }
    public CharsMatchPath getCharsHeadPath(int to){
        return getHeadPath(to);
    }
    public ElementMatchPath getElementHeadPath(SRule to){
        throw new IllegalStateException();
    }
    public AttributeMatchPath getAttributeHeadPath(SRule to){
        throw new IllegalStateException();
    }
    public CharsMatchPath getCharsHeadPath(SRule to){
        return getHeadPath(to);
    }
    
    
    public String toString(){
        return "TextMatchPath "+Arrays.toString(list)+" lastIndex="+lastIndex;
    }
}
