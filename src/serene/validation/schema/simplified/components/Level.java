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

package serene.validation.schema.simplified.components;

import java.util.Arrays;


public abstract class Level{
		
	Level child;
	
	SNameClass[] nameClasses;
	int ncIndex;
	int ncSize;
	
	SPattern[] patterns;
	int ptIndex;
	int ptSize;
	
	
	SExceptNameClass exceptNameClass;
	
	SExceptPattern[] exceptPatterns;
	int epIndex;
	int epSize;
	
	Level(){
		ptIndex = -1;
		ptSize = 1;
		patterns = new SPattern[ptSize];
		
		ncIndex = -1;
		ncSize = 1;
		nameClasses = new SNameClass[ncSize];
	
        epIndex = -1;
		epSize = 1;
		exceptPatterns = new SExceptPattern[epSize];	
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
	
	
	public void add(SPattern p){
		if(++ptIndex == ptSize){			
			SPattern[] increased = new SPattern[++ptSize];
			System.arraycopy(patterns, 0, increased, 0, ptIndex);
			patterns = increased;
		}
		patterns[ptIndex] = p;	
	}
	public void add(SPattern[] p){	
		if(p == null)return;
		int length = p.length;
		if(ptIndex + length >= ptSize){
			ptSize += length;
			SPattern[] increased = new SPattern[ptSize];
			System.arraycopy(patterns, 0, increased, 0, ptIndex+1);
			patterns = increased;
		}
		System.arraycopy(p, 0, patterns, ptIndex+1, length);
		ptIndex += length;
	}	
	public SPattern[] getPatterns(){
		if(ptIndex < 0) return null;
		return Arrays.copyOf(patterns, ptIndex+1);
	}		
	public SPattern getLastPattern(){
		if(ptIndex < 0) return null;
		return patterns[ptIndex];
	}
	public void clearPatterns(){		
		ptIndex = -1;
		Arrays.fill(patterns, null);
	}
	public int getPatternsCount(){
		return ptIndex+1;
	}
	
	
	
	public void add(SNameClass nc){
		if(++ncIndex == ncSize){
			SNameClass[] increased = new SNameClass[++ncSize];
			System.arraycopy(nameClasses, 0, increased, 0, ncIndex);
			nameClasses = increased;
		}
		nameClasses[ncIndex] = nc;
	}
	public void add(SNameClass[] nc){
		int length = nc.length;
		if(ncIndex + length >= ncSize){
			ncSize += length;
			SNameClass[] increased = new SNameClass[ncSize];
			System.arraycopy(nameClasses, 0, increased, 0, ncIndex+1);
			nameClasses = increased;
		}
		System.arraycopy(nc, 0, nameClasses, ncIndex+1, length);
		ncIndex += length;
	}
	public SNameClass[] getNameClasses(){
		if(ncIndex < 0) return null;
		return Arrays.copyOf(nameClasses, ncIndex+1);
	}		
	public SNameClass getLastNameClass(){
		if(ncIndex < 0) return null;
		return nameClasses[ncIndex];
	}
	public int getNameClassesCount(){
		return ncIndex+1;
	}
	public void clearNameClasses(){
		ncIndex = -1;
		Arrays.fill(nameClasses, null);
	}
	
		
	
	public void add(SExceptNameClass enc){
		this.exceptNameClass = enc;
	}		
	public SExceptNameClass getExceptNameClass(){
		return exceptNameClass;
	}	
	public void clearExceptNameClass(){
		exceptNameClass = null;
	}
		
	
	public void add(SExceptPattern p){
		if(++epIndex == epSize){			
			SExceptPattern[] increased = new SExceptPattern[++epSize];
			System.arraycopy(exceptPatterns, 0, increased, 0, epIndex);
			exceptPatterns = increased;
		}
		exceptPatterns[epIndex] = p;
	}
	public void add(SExceptPattern[] p){		
		int length = p.length;
		if(epIndex + length >= epSize){
			epSize += length;
			SExceptPattern[] increased = new SExceptPattern[epSize];
			System.arraycopy(exceptPatterns, 0, increased, 0, epIndex+1);
			exceptPatterns = increased;
		}
		System.arraycopy(p, 0, exceptPatterns, epIndex+1, length);
		epIndex += length;
	}
	public SExceptPattern[] getExceptPatterns(){
		if(epIndex < 0) return null;
		return Arrays.copyOf(exceptPatterns, epIndex+1);
	}		
	public SExceptPattern getLastExceptPattern(){
		if(epIndex < 0) return null;
		return exceptPatterns[epIndex];
	}
	public int getExceptPatternsCount(){
		return epIndex+1;
	}
	public void clearExceptPatterns(){
		epIndex = -1;
		Arrays.fill(exceptPatterns, null);
	}	
	
	public void clear(){
		clearPatterns();
		clearNameClasses();
		clearExceptPatterns();
		exceptNameClass = null;
	}
		
	abstract String toString(int i);	
}


