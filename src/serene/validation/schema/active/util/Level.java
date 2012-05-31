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

package serene.validation.schema.active.util;

import java.util.Arrays;

import serene.validation.schema.active.components.APattern;

import serene.validation.schema.active.components.AExceptPattern;

public abstract class Level{	
	Level child;
	

	APattern[] patterns;
	int ptIndex;
	int ptSize;
	
	AExceptPattern exceptPattern;
	
	
	Level(){
		ptIndex = -1;
		ptSize = 1;
		patterns = new APattern[ptSize];
		
	}
		
	public static Level getTopInstance(){
		return new LevelTop();
	}
	
	public abstract boolean isTopLevel();
	public abstract boolean isBottomLevel();
	public abstract Level getLevelDown();
	public abstract Level getLevelUp();
	abstract void setChild(Level child);	
	
	public void add(APattern p){
		if(++ptIndex == ptSize){			
			APattern[] increased = new APattern[++ptSize];
			System.arraycopy(patterns, 0, increased, 0, ptIndex);
			patterns = increased;
		}
		patterns[ptIndex] = p;
	}
	public void add(APattern[] p){		
		int length = p.length;
		if(ptIndex + length >= ptSize){
			ptSize += length;
			APattern[] increased = new APattern[ptSize];
			System.arraycopy(patterns, 0, increased, 0, ptIndex+1);
			patterns = increased;
		}
		System.arraycopy(p, 0, patterns, ptIndex+1, length);
	}	
	public APattern[] getPatterns(){
		if(ptIndex == -1)return null;
		return Arrays.copyOf(patterns, ptIndex+1);
	}		
	public APattern getLastPattern(){
		if(ptIndex < 0) return null;
		return patterns[ptIndex];
	}	
	public int getPatternsCount(){
		return ptIndex+1;
	}
	void clearPatterns(){		
		ptIndex = -1;
		Arrays.fill(patterns, null);
	}
	
	
	
	
	public void add(AExceptPattern ep){
		this.exceptPattern = ep;
	}		
	public AExceptPattern getExceptPattern(){
		return exceptPattern;
	}	
	void clearExceptPattern(){
		exceptPattern = null;
	}
		
	
	public void clear(){
		if(ptIndex>=0)clearPatterns();
		exceptPattern = null;
	}
	
	public void clearAll(){
		clear();
		child.clearAll();
	}
	abstract String toString(int i);	
	abstract String allToString();	
	
	protected String componentsToString(){
		return "patterns"+ptIndex+" "+ Arrays.toString(patterns)	
		+ exceptPattern;
	} 
}


