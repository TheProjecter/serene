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

package serene.validation.handlers.content.util;

import serene.util.IntList;
import serene.util.InputDescriptor;
import serene.util.SpaceCharsHandler;

import serene.Reusable;

public class CharacterContentDescriptor implements Reusable{
    IntList indexes;
    ActiveInputDescriptor activeInputDescriptor;
    
    SpaceCharsHandler spaceCharsHandler;
    
    final String CHARACTER_CONTENT_DESCRIPTION = "character content";
    
    CharacterContentDescriptorPool characterContentDescriptorPool;
    
    CharacterContentDescriptor(ActiveInputDescriptor activeInputDescriptor, SpaceCharsHandler spaceCharsHandler, CharacterContentDescriptorPool characterContentDescriptorPool){        
        this.activeInputDescriptor = activeInputDescriptor;
        this.spaceCharsHandler = spaceCharsHandler;
        this.characterContentDescriptorPool = characterContentDescriptorPool;
        indexes = new IntList();
    }
    
    
    public void recycle(){
        clear();
        characterContentDescriptorPool.recycle(this);
    }
    
    public void clear(){
        for(int i = 0; i < indexes.size(); i++){
            activeInputDescriptor.unregisterClientForRecord(indexes.get(i), this);
        }
        indexes.clear();
    }
    
    public boolean isEmpty(){
        return indexes.isEmpty();
    }
    
    public boolean isSpaceOnly(){
        if(indexes.isEmpty()) throw new IllegalArgumentException();
        for(int i = 0; i < indexes.size(); i++){
            char[] c = activeInputDescriptor.getCharArrayValue(indexes.get(i));
            if(!spaceCharsHandler.isSpace(c)) return false; 
        }
        return true;
    }
    public int size(){
        return indexes.size();
    }
    
    public int[] getAllIndexes(){
        return indexes.toArray();
    }
        
    public int getStartIndex(){
        return indexes.get(0);
    }
    
    public ActiveInputDescriptor getActiveInputDescriptor(){
        return activeInputDescriptor;
    }
    
    public void add(char[] value, 
                        int offset,
                        int length,
                        String systemId,
                        String publicId,
                        int lineNumber,
                        int columnNumber){
        char[] v = new char[length];
        System.arraycopy(value, offset, v, 0, length);
        int index = activeInputDescriptor.recordItem(InputDescriptor.CHARACTER_CONTENT,
                                            CHARACTER_CONTENT_DESCRIPTION,
                                            v,                                            
                                            systemId,
                                            publicId, 	
                                            lineNumber,
                                            columnNumber);
        indexes.add(index);
        activeInputDescriptor.registerClientForRecord(index, this);
    }
    
    public void add(int index){
        indexes.add(index);
        activeInputDescriptor.registerClientForRecord(index, this);
    }
    
    public void add(int[] otherIndexes){
        for(int i : otherIndexes){
            indexes.add(i);
            activeInputDescriptor.registerClientForRecord(i, this);
        }
    }
    
    public char[] getCharArrayContent(){
        // if indexes.size() == 1 return that array        
        if(indexes.size() == 1) return activeInputDescriptor.getCharArrayValue(indexes.get(0));
        
        // else concatenate
        //      loop indexes and compute the length
        //      create result array
        //      loop indexes, get char array value and coppy to result
        int length = 0;
        for(int i = 0; i < indexes.size(); i++){
            length += activeInputDescriptor.getCharArrayValue(indexes.get(i)).length;
        }
        
        int offset = 0;
        char[] result = new char[length];
        for(int i = 0; i < indexes.size(); i++){
            char[] v = activeInputDescriptor.getCharArrayValue(indexes.get(i));
            int l = v.length;            
            System.arraycopy(v, 0 , result, offset, l);
            offset += l;
        }      
        return result;
    }
    
    public String toString(){
        return "CharacterContentDescriptor content=["+new String(getCharArrayContent())+"]";
    }
}
