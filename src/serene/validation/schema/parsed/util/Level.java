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

import serene.validation.schema.parsed.components.Pattern;
import serene.validation.schema.parsed.components.NameClass;
import serene.validation.schema.parsed.components.Param;
import serene.validation.schema.parsed.components.ExceptNameClass;
import serene.validation.schema.parsed.components.ExceptPattern;
import serene.validation.schema.parsed.components.IncludeContent;
import serene.validation.schema.parsed.components.GrammarContent;

import serene.validation.schema.parsed.components.Define;
import serene.validation.schema.parsed.components.Start;

import sereneWrite.MessageWriter;

public abstract class Level{
		
	Level child;
	
	NameClass[] nameClasses;
	int ncIndex;
	int ncSize;
	
	Pattern[] patterns;
	int ptIndex;
	int ptSize;
	
	Param[] params;
	int prIndex;
	int prSize;
	
	ExceptNameClass exceptNameClass;
	
	ExceptPattern[] exceptPatterns;
	int epIndex;
	int epSize;
	
	
	IncludeContent[] includeContent;
	int icIndex;
	int icSize;
	
	GrammarContent[] grammarContent;
	int gcIndex;
	int gcSize;
	
	MessageWriter debugWriter;
	
	Level(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		ptIndex = -1;
		ptSize = 1;
		patterns = new Pattern[ptSize];
		
		ncIndex = -1;
		ncSize = 1;
		nameClasses = new NameClass[ncSize];
		
		prIndex = -1;
		prSize = 1;
		params = new Param[prSize];
		
		epIndex = -1;
		epSize = 1;
		exceptPatterns = new ExceptPattern[epSize];
		
		icIndex = -1;
		icSize = 1;
		includeContent = new IncludeContent[icSize];
		
		gcIndex = -1;
		gcSize = 1;
		grammarContent = new GrammarContent[gcSize];
	}
	
	
	public static Level getTopInstance(MessageWriter debugWriter){
		return new LevelTop(debugWriter);
	}
	
	public abstract boolean isTopLevel();	
	public abstract boolean isBottomLevel();	
	public abstract Level getLevelDown();
	public abstract Level getLevelUp();
	public abstract void clearAll();
	abstract void setChild(Level child);
	
	
	
	public void add(Pattern p){
		if(++ptIndex == ptSize){			
			Pattern[] increased = new Pattern[++ptSize];
			System.arraycopy(patterns, 0, increased, 0, ptIndex);
			patterns = increased;
		}
		patterns[ptIndex] = p;
	}
	public void add(Pattern[] p){		
		int length = p.length;
		if(ptIndex + length >= ptSize){
			ptSize += length;
			Pattern[] increased = new Pattern[ptSize];
			System.arraycopy(patterns, 0, increased, 0, ptIndex+1);
			patterns = increased;
		}
		System.arraycopy(p, 0, patterns, ptIndex+1, length);
		ptIndex += length;
	}	
	public Pattern[] getPatterns(){	
		if(ptIndex < 0) return null;		
		return Arrays.copyOf(patterns, ptIndex+1);
	}		
	public Pattern getLastPattern(){
		if(ptIndex < 0) return null;
		return patterns[ptIndex];
	}
	public int getPatternsCount(){
		return ptIndex+1;
	}
	public void clearPatterns(){		
		ptIndex = -1;
		Arrays.fill(patterns, null);
	}
	
	
	public void add(NameClass nc){
		if(++ncIndex == ncSize){
			NameClass[] increased = new NameClass[++ncSize];
			System.arraycopy(nameClasses, 0, increased, 0, ncIndex);
			nameClasses = increased;
		}
		nameClasses[ncIndex] = nc;
	}
	public void add(NameClass[] nc){
		int length = nc.length;
		if(ncIndex + length >= ncSize){
			ncSize += length;
			NameClass[] increased = new NameClass[ncSize];
			System.arraycopy(nameClasses, 0, increased, 0, ncIndex+1);
			nameClasses = increased;
		}
		System.arraycopy(nc, 0, nameClasses, ncIndex+1, length);
		ncIndex += length;
	}
	public NameClass[] getNameClasses(){
		if(ncIndex < 0) return null;
		return Arrays.copyOf(nameClasses, ncIndex+1);
	}	
	public NameClass getLastNameClass(){
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
	
	
	
	public void add(Param p){
		if(++prIndex == prSize){			
			Param[] increased = new Param[++prSize];
			System.arraycopy(params, 0, increased, 0, prIndex);
			params = increased;
		}
		params[prIndex] = p;
	}
	public void add(Param[] p){		
		int length = p.length;
		if(prIndex + length >= prSize){
			prSize += length;
			Param[] increased = new Param[prSize];
			System.arraycopy(params, 0, increased, 0, prIndex+1);
			params = increased;
		}
		System.arraycopy(p, 0, params, prIndex+1, length);
		prIndex += length;
	}
	public Param[] getParams(){
		if(prIndex < 0) return null;
		return Arrays.copyOf(params, prIndex+1);
	}		
	public Param getLastParam(){
		if(prIndex < 0) return null;
		return params[prIndex];
	}
	public int getParamsCount(){
		return prIndex+1;
	}
	public void clearParams(){
		prIndex = -1;
		Arrays.fill(params, null);
	}
	
	
	public void add(Define d){
		if(++icIndex == icSize){			
			IncludeContent[] increased = new IncludeContent[++icSize];
			System.arraycopy(includeContent, 0, increased, 0, icIndex);
			includeContent = increased;
		}
		includeContent[icIndex] = d;
		
		if(++gcIndex == gcSize){			
			GrammarContent[] increased = new GrammarContent[++gcSize];
			System.arraycopy(grammarContent, 0, increased, 0, gcIndex);
			grammarContent = increased;
		}
		grammarContent[gcIndex] = d;
	}
	public void add(Define[] d){		
		int length = d.length;
		
		if(icIndex + length >= icSize){
			icSize += length;
			IncludeContent[] increased = new IncludeContent[icSize];
			System.arraycopy(includeContent, 0, increased, 0, icIndex+1);
			includeContent = increased;
		}
		System.arraycopy(d, 0, includeContent, icIndex+1, length);
		icIndex += length;
		
		if(gcIndex + length >= gcSize){
			gcSize += length;
			GrammarContent[] increased = new GrammarContent[gcSize];
			System.arraycopy(grammarContent, 0, increased, 0, gcIndex+1);
			grammarContent = increased;
		}
		System.arraycopy(d, 0, grammarContent, gcIndex+1, length);
		gcIndex += length;
	}
	public void add(Start s){
		if(++icIndex == icSize){			
			IncludeContent[] increased = new IncludeContent[++icSize];
			System.arraycopy(includeContent, 0, increased, 0, icIndex);
			includeContent = increased;
		}
		includeContent[icIndex] = s;
		
		if(++gcIndex == gcSize){			
			GrammarContent[] increased = new GrammarContent[++gcSize];
			System.arraycopy(grammarContent, 0, increased, 0, gcIndex);
			grammarContent = increased;
		}
		grammarContent[gcIndex] = s;
	}
	public void add(Start[] s){		
		int length = s.length;
		
		if(icIndex + length >= icSize){
			icSize += length;
			IncludeContent[] increased = new IncludeContent[icSize];
			System.arraycopy(includeContent, 0, increased, 0, icIndex+1);
			includeContent = increased;
		}
		System.arraycopy(s, 0, includeContent, icIndex+1, length);
		icIndex += length;
		
		if(gcIndex + length >= gcSize){
			gcSize += length;
			GrammarContent[] increased = new GrammarContent[gcSize];
			System.arraycopy(grammarContent, 0, increased, 0, gcIndex+1);
			grammarContent = increased;
		}
		System.arraycopy(s, 0, grammarContent, gcIndex+1, length);
		gcIndex += length;
	}
	
	
	public void add(IncludeContent ic){
		if(++icIndex == icSize){			
			IncludeContent[] increased = new IncludeContent[++icSize];
			System.arraycopy(includeContent, 0, increased, 0, icIndex);
			includeContent = increased;
		}
		includeContent[icIndex] = ic;
	}
	public void add(IncludeContent[] ic){		
		int length = ic.length;
		if(icIndex + length >= icSize){
			icSize += length;
			IncludeContent[] increased = new IncludeContent[icSize];
			System.arraycopy(includeContent, 0, increased, 0, icIndex+1);
			includeContent = increased;
		}
		System.arraycopy(ic, 0, includeContent, icIndex+1, length);
		icIndex += length;
	}
	public IncludeContent[] getIncludeContent(){
		if(icIndex < 0) return null;		
		return Arrays.copyOf(includeContent, icIndex+1);
	}		
	public IncludeContent getLastIncludeContent(){
		if(icIndex < 0) return null;		
		return includeContent[icIndex];
	}
	public int getIncludeContentCount(){
		return icIndex+1;
	}
	public void clearIncludeContent(){
		icIndex = -1;
		Arrays.fill(includeContent, null);
	}
	
	
	public void add(GrammarContent gc){
		if(++gcIndex == gcSize){			
			GrammarContent[] increased = new GrammarContent[++gcSize];
			System.arraycopy(grammarContent, 0, increased, 0, gcIndex);
			grammarContent = increased;
		}
		grammarContent[gcIndex] = gc;
	}		
	public void add(GrammarContent[] gc){		
		int length = gc.length;
		if(gcIndex + length >= gcSize){
			gcSize += length;
			GrammarContent[] increased = new GrammarContent[gcSize];
			System.arraycopy(grammarContent, 0, increased, 0, gcIndex+1);
			grammarContent = increased;
		}
		System.arraycopy(gc, 0, grammarContent, gcIndex+1, length);
		gcIndex += length;
	}
	public GrammarContent[] getGrammarContent(){
		if(gcIndex < 0) return null;
		return Arrays.copyOf(grammarContent, gcIndex+1);
	}	
	public GrammarContent getLastGrammarContent(){
		if(gcIndex < 0) return null;		
		return grammarContent[gcIndex];
	}
	public int getGrammarContentCount(){
		return gcIndex+1;
	}	
	public void clearGrammarContent(){
		gcIndex = -1;
		Arrays.fill(grammarContent, null);
	}
	
	
	
	public void add(ExceptNameClass enc){
		this.exceptNameClass = enc;
	}		
	public ExceptNameClass getExceptNameClass(){
		return exceptNameClass;
	}	
	public void clearExceptNameClass(){
		exceptNameClass = null;
	}
	
	
	public void add(ExceptPattern ep){
		if(++epIndex == epSize){			
			ExceptPattern[] increased = new ExceptPattern[++epSize];
			System.arraycopy(exceptPatterns, 0, increased, 0, epIndex);
			exceptPatterns = increased;
		}
		exceptPatterns[epIndex] = ep;
	}
	public void add(ExceptPattern[] ep){		
		int length = ep.length;
		if(epIndex + length >= epSize){
			epSize += length;
			ExceptPattern[] increased = new ExceptPattern[epSize];
			System.arraycopy(exceptPatterns, 0, increased, 0, epIndex+1);
			exceptPatterns = increased;
		}
		System.arraycopy(ep, 0, exceptPatterns, epIndex+1, length);
		epIndex += length;
	}
	public ExceptPattern[] getExceptPatterns(){
		if(epIndex < 0) return null;
		return Arrays.copyOf(exceptPatterns, epIndex+1);
	}		
	public ExceptPattern getLastExceptPattern(){
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
		clearParams();
		clearIncludeContent();
		clearGrammarContent();
		clearExceptPatterns();
		exceptNameClass = null;
	}
		
	abstract String toString(int i);	
}


