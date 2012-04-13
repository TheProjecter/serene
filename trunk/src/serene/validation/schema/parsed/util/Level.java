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


package serene.validation.schema.parsed.util;

import java.util.Arrays;

import serene.validation.schema.parsed.ParsedComponent;

public abstract class Level{
		
	Level child;
	
	ParsedComponent[] parsedComponents;
	int pcIndex;
	int pcSize;
	
	Level(){
		pcIndex = -1;
		pcSize = 1;
		parsedComponents = new ParsedComponent[pcSize];		
	}
	
	
	public static Level getTopInstance(){
		return new LevelTop();
	}
	
	public abstract boolean isTopLevel();	
	public abstract boolean isBottomLevel();	
	public abstract Level getLevelDown();
	public abstract Level getLevelUp();
	public abstract void clearAll();
	abstract void setChild(Level child);
	
	
	
	public void add(ParsedComponent p){
		if(++pcIndex == pcSize){			
			ParsedComponent[] increased = new ParsedComponent[++pcSize];
			System.arraycopy(parsedComponents, 0, increased, 0, pcIndex);
			parsedComponents = increased;
		}
		parsedComponents[pcIndex] = p;
	}
	public void add(ParsedComponent[] p){		
		int length = p.length;
		if(pcIndex + length >= pcSize){
			pcSize += length;
			ParsedComponent[] increased = new ParsedComponent[pcSize];
			System.arraycopy(parsedComponents, 0, increased, 0, pcIndex+1);
			parsedComponents = increased;
		}
		System.arraycopy(p, 0, parsedComponents, pcIndex+1, length);
		pcIndex += length;
	}	
	public ParsedComponent[] getParsedComponents(){	
		if(pcIndex < 0) return null;		
		return Arrays.copyOf(parsedComponents, pcIndex+1);
	}		
	public ParsedComponent getLastParsedComponent(){
		if(pcIndex < 0) return null;
		return parsedComponents[pcIndex];
	}
	public int getParsedComponentsCount(){
		return pcIndex+1;
	}
	public void clearParsedComponents(){		
		pcIndex = -1;
		Arrays.fill(parsedComponents, null);
	}	
	
	public void clear(){
		clearParsedComponents();
	}
		
	abstract String toString(int i);	
}



