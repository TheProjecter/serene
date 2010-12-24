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
import serene.validation.schema.active.components.ANameClass;
import serene.validation.schema.active.components.AParam;
import serene.validation.schema.active.components.AExceptNameClass;
import serene.validation.schema.active.components.AExceptPattern;

import sereneWrite.MessageWriter;

public abstract class Level{	
	Level child;
	
	ANameClass[] nameClasses;
	int ncIndex;
	int ncSize;
	
	APattern[] patterns;
	int ptIndex;
	int ptSize;
	
	AParam[] params;
	int prIndex;
	int prSize;
	
	AExceptNameClass exceptNameClass;
	
	AExceptPattern exceptPattern;
	
	
	MessageWriter debugWriter;
	
	Level(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		ptIndex = -1;
		ptSize = 1;
		patterns = new APattern[ptSize];
		
		ncIndex = -1;
		ncSize = 1;
		nameClasses = new ANameClass[ncSize];
		
		prIndex = -1;
		prSize = 1;
		params = new AParam[prSize];
	}
		
	public static Level getTopInstance(MessageWriter debugWriter){
		return new LevelTop(debugWriter);
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
	
	
	
	public void add(ANameClass nc){
		if(++ncIndex == ncSize){
			ANameClass[] increased = new ANameClass[++ncSize];
			System.arraycopy(nameClasses, 0, increased, 0, ncIndex);
			nameClasses = increased;
		}
		nameClasses[ncIndex] = nc;		
	}
	public void add(ANameClass[] nc){
		int length = nc.length;
		if(ncIndex + length >= ncSize){
			ncSize += length;
			ANameClass[] increased = new ANameClass[ncSize];
			System.arraycopy(nameClasses, 0, increased, 0, ncIndex+1);
			nameClasses = increased;
		}
		System.arraycopy(nc, 0, nameClasses, ncIndex+1, length);
	}
	public ANameClass[] getNameClasses(){
		if(ncIndex == -1)return null;
		return Arrays.copyOf(nameClasses, ncIndex+1);		
	}
	public ANameClass getLastNameClass(){
		if(ncIndex < 0) return null;
		return nameClasses[ncIndex];
	}	
	public int getNameClassesCount(){
		return ncIndex+1;
	}
	void clearNameClasses(){			
		ncIndex = -1;
		Arrays.fill(nameClasses, null);
	}
	
	
	
	public void add(AParam p){
		if(++prIndex == prSize){			
			AParam[] increased = new AParam[++prSize];
			System.arraycopy(params, 0, increased, 0, prIndex);
			params = increased;
		}
		params[prIndex] = p;
	}
	public void add(AParam[] p){		
		int length = p.length;
		if(prIndex + length >= prSize){
			prSize += length;
			AParam[] increased = new AParam[prSize];
			System.arraycopy(params, 0, increased, 0, prIndex+1);
			params = increased;
		}
		System.arraycopy(p, 0, params, prIndex+1, length);
	}
	public AParam[] getParams(){
		if(prIndex == -1)return null;
		return Arrays.copyOf(params, prIndex+1);
	}
	public AParam getLastParam(){
		if(prIndex < 0) return null;
		return params[prIndex];
	}
	public int getParamsCount(){
		return prIndex+1;
	}	
	void clearParams(){
		prIndex = -1;
		Arrays.fill(params, null);
	}
			
	
	public void add(AExceptNameClass enc){
		this.exceptNameClass = enc;
	}		
	public AExceptNameClass getExceptNameClass(){
		return exceptNameClass;
	}	
	void clearExceptNameClass(){
		exceptNameClass = null;
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
		if(ncIndex>=0)clearNameClasses();
		if(prIndex>=0)clearParams();
		exceptPattern = null;
		exceptNameClass = null;		
	}
	
	public void clearAll(){
		clear();
		child.clearAll();
	}
	abstract String toString(int i);	
	abstract String allToString();	
	
	protected String componentsToString(){
		return "nameClasses "+ncIndex+" "+Arrays.toString( nameClasses)
		+"patterns"+ptIndex+" "+ Arrays.toString(patterns)	
		+"params"+prIndex+" "+ Arrays.toString(params)
		+ exceptNameClass
		+ exceptPattern;
	} 
}


