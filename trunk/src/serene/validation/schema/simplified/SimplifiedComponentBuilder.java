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


package serene.validation.schema.simplified;

import java.util.Arrays;
import java.util.ArrayList;

import org.relaxng.datatype.Datatype;

import serene.validation.schema.ComponentBuilder;

import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SExceptNameClass;
import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SNameClass;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SZeroOrMore;
import serene.validation.schema.simplified.components.SOneOrMore;
import serene.validation.schema.simplified.components.SOptional;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SEmpty;
import serene.validation.schema.simplified.components.SText;
import serene.validation.schema.simplified.components.SNotAllowed;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SValue;
import serene.validation.schema.simplified.components.SGrammar;
import serene.validation.schema.simplified.components.SDummy;


import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.simplified.util.Level;

import serene.bind.util.DocumentIndexedData;
import serene.util.IntList;

import sereneWrite.MessageWriter;

/**
* Builds components, the big picture SimplifiedModel is the directors responsibility. 
* Can return/remove the components of the current level(usefull for attaching commands).
* Can check if the current level is top level.
*/
public class SimplifiedComponentBuilder implements ComponentBuilder{
	
	Level level;
	//for debug only
	Level topLevel;	
	MessageWriter debugWriter;
	
	
	public SimplifiedComponentBuilder(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		level = Level.getTopInstance(debugWriter);
		topLevel = level;
	}
	
	public void startBuild(){
		level = topLevel;
		level.clearAll();
	}
	
	public void startLevel(){
		level = level.getLevelDown();
	}	
	public void endLevel(){		
		level = level.getLevelUp();
	}
		
	
	public void clearContent(){
		if(level.isBottomLevel())return;
		Level content = level.getLevelDown();
		content.clear();
	}
	
	
	public void addToCurrentLevel(SPattern p){
		level.add(p);
	}
	public void addAllToCurrentLevel(SPattern[] p){
		
		level.add(p);
		
	}
	public SPattern[] getAllCurrentPatterns(){
		return level.getPatterns();
	}	
	public SPattern getCurrentPattern(){
		return level.getLastPattern();
	}	
	public int getCurrentPatternsCount(){
		return level.getPatternsCount();
	}
    public void clearCurrentPatterns(){
        level.clearPatterns();
    }
	public int getContentPatternsCount(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getPatternsCount();
	}
	public SPattern[] getContentPatterns(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getPatterns();
	}	
	
	public SPattern getLastContentPattern(){
		if(level.isBottomLevel())return null;		
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastPattern();
	}	
	
	
	public void addToCurrentLevel(SNameClass nc){
		level.add(nc);
	}
	public SNameClass[] getAllCurrentNameClasses(){
		return level.getNameClasses();
	}		
	public SNameClass getCurrentNameClass(){		
		return level.getLastNameClass();
	}
	public int getCurrentNameClassesCount(){
		return level.getNameClassesCount();
	}
    public void clearCurrentNameClasses(){
        level.clearNameClasses();
    }
	public int getContentNameClassesCount(){
		Level contentLevel = level.getLevelDown();
		return contentLevel.getNameClassesCount();
	}		
	public SNameClass[] getContentNameClasses(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();
		return contentLevel.getNameClasses();
	}		
	public SNameClass getLastContentNameClass(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastNameClass();
	}
			
	
	
	public void addToCurrentLevel(SExceptPattern ep){
		level.add(ep);
	}
	public SExceptPattern[] getAllCurrentExceptPatterns(){
		return level.getExceptPatterns();
	}	
	public SExceptPattern getCurrentExceptPattern(){
		return level.getLastExceptPattern();
	}
	public int getCurrentExceptPatternsCount(){				
		return level.getExceptPatternsCount();
	}
    public void clearCurrentExceptPatterns(){
        level.clearExceptPatterns();
    }
	public int getContentExceptPatternsCount(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getExceptPatternsCount();
	}
	public SExceptPattern[] getContentExceptPatterns(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getExceptPatterns();
	}	
	public SExceptPattern getLastContentExceptPattern(){		
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastExceptPattern();
	}		
	
	
	public void addToCurrentLevel(SExceptNameClass enc){
		level.add(enc);
	}
	public SExceptNameClass getCurrentExceptNameClass(){
		return level.getExceptNameClass();
	}		
	public SExceptNameClass getContentExceptNameClass(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getExceptNameClass();
	}	
    public void clearCurrentExceptNameClass(){
        level.clearExceptNameClass();
    }
	
	public void addGroup(){}
	public void addInterleave(){}	
	
	//**************************************************************************
	//START PATTERN BUILDING ***************************************************
	//**************************************************************************	
	public void buildElement(int recordIndex, DocumentIndexedData documentIndexedData){
		SElement e = new SElement(getLastContentNameClass(), getLastContentPattern(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(e);
	}
	public void buildAttribute(int defaultValue, int recordIndex, DocumentIndexedData documentIndexedData){
		SAttribute a = new SAttribute(getLastContentNameClass(), getContentPatterns(), defaultValue, recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(a);
	}
	public void buildGroup(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
		SGroup g = new SGroup(getContentPatterns(), recordIndex, documentIndexedData, addedBySimplification, debugWriter);
		clearContent();
		addToCurrentLevel(g);
	}
    public void buildReplacementGroup(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
        SPattern[] children = getAllCurrentPatterns();
		SGroup g = new SGroup(children, recordIndex, documentIndexedData, addedBySimplification, debugWriter);
		clearCurrentPatterns();
		addToCurrentLevel(g);
	}
	public void buildInterleave(int recordIndex, DocumentIndexedData documentIndexedData){
		SInterleave i = new SInterleave(getContentPatterns(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(i);
	}
    public void buildInterleave(SPattern[] children, IntList allRecordIndexes, ArrayList<DocumentIndexedData> allDocumentIndexedData, boolean addedBySimplification){
		SInterleave i = new SInterleave(children, allRecordIndexes, allDocumentIndexedData, addedBySimplification, debugWriter);
		addToCurrentLevel(i);	
	}
	public void buildChoicePattern(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
		SChoicePattern cp = new SChoicePattern(getContentPatterns(), recordIndex, documentIndexedData, addedBySimplification, debugWriter);
		clearContent();
		addToCurrentLevel(cp);
	}
    public void buildChoicePattern(SPattern[] children, IntList allRecordIndexes, ArrayList<DocumentIndexedData> allDocumentIndexedData, boolean addedBySimplification){
		SChoicePattern cp = new SChoicePattern(children, allRecordIndexes, allDocumentIndexedData, addedBySimplification, debugWriter);		
		addToCurrentLevel(cp);
	}
    public void buildReplacementChoicePattern(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
        SPattern[] children = getAllCurrentPatterns();
		SChoicePattern c = new SChoicePattern(children, recordIndex, documentIndexedData, addedBySimplification, debugWriter);
		clearCurrentPatterns();
		addToCurrentLevel(c);	
	}
	public void buildMixed(int recordIndex, DocumentIndexedData documentIndexedData){
		SMixed o = new SMixed(getLastContentPattern(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(o);
	}
	public void buildOptional(int recordIndex, DocumentIndexedData documentIndexedData){
		SOptional o = new SOptional(getLastContentPattern(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(o);
	}
	public void buildZeroOrMore(int recordIndex, DocumentIndexedData documentIndexedData){
		SZeroOrMore zom = new SZeroOrMore(getLastContentPattern(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(zom);
	}
	public void buildOneOrMore(int recordIndex, DocumentIndexedData documentIndexedData){
		SOneOrMore oom = new SOneOrMore(getLastContentPattern(), recordIndex, documentIndexedData, debugWriter);
		clearContent();		
		addToCurrentLevel(oom);
	}
	public void buildListPattern(int recordIndex, DocumentIndexedData documentIndexedData){
		SListPattern lp = new SListPattern(getLastContentPattern(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(lp);
	}
	public void buildRef(int definitionIndex, int recordIndex, DocumentIndexedData documentIndexedData){
		SRef r = new SRef(definitionIndex, recordIndex, documentIndexedData, debugWriter);
		addToCurrentLevel(r);
	}
	public void buildEmpty(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
		SEmpty e = new SEmpty(recordIndex, documentIndexedData, addedBySimplification, debugWriter);
		addToCurrentLevel(e);
	}
	public void buildText(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
		SText t = new SText(recordIndex, documentIndexedData, addedBySimplification, debugWriter);
		addToCurrentLevel(t);
	}
	public void buildValue(String ns, Datatype datatype, String charContent, int recordIndex, DocumentIndexedData documentIndexedData){
		SValue v = new SValue(ns, datatype, charContent, recordIndex, documentIndexedData, debugWriter);
		addToCurrentLevel(v);
	}
	public void buildData(Datatype datatype, int recordIndex, DocumentIndexedData documentIndexedData){
		SData d = new SData(datatype, getContentExceptPatterns(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(d);
	}		
	public void buildNotAllowed(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
		SNotAllowed na = new SNotAllowed(recordIndex, documentIndexedData, addedBySimplification, debugWriter);
		addToCurrentLevel(na);
	}	
	public void buildGrammar(int recordIndex, DocumentIndexedData documentIndexedData){
		SGrammar g = new SGrammar(getLastContentPattern(), recordIndex, documentIndexedData, debugWriter);
		clearContent();		
		addToCurrentLevel(g);
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	//**************************************************************************
	//START NAME CLASS BUILDING ************************************************
	//**************************************************************************
	public void buildName(String ns, String localPart, int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
	    if(localPart == null) throw new IllegalStateException(); 
		SName n = new SName(ns, localPart, recordIndex, documentIndexedData, addedBySimplification, debugWriter);
		addToCurrentLevel(n);
	}
	public void buildAnyName(int recordIndex, DocumentIndexedData documentIndexedData){
		SAnyName an = new SAnyName(getContentExceptNameClass(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(an);
	}
	public void buildNsName(String ns, int recordIndex, DocumentIndexedData documentIndexedData){
		SNsName nn = new SNsName(ns, getContentExceptNameClass(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(nn);
	}
	public void buildChoiceNameClass(int recordIndex, DocumentIndexedData documentIndexedData){
		SChoiceNameClass cnc = new SChoiceNameClass(getContentNameClasses(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(cnc);
	}	
    public void buildReplacementChoiceNameClass(int recordIndex, DocumentIndexedData documentIndexedData){
        SNameClass[] children = getAllCurrentNameClasses();
		SChoiceNameClass c = new SChoiceNameClass(children, recordIndex, documentIndexedData, debugWriter);
		clearCurrentNameClasses();
		addToCurrentLevel(c);
	}
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************	
	public void buildExceptNameClass(int recordIndex, DocumentIndexedData documentIndexedData){
		SExceptNameClass enc = new SExceptNameClass(getLastContentNameClass(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(enc);
	}
	public void buildExceptPattern(int recordIndex, DocumentIndexedData documentIndexedData){
		SExceptPattern ep = new SExceptPattern(getLastContentPattern(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(ep);
	}
	
	
	public void buildDummy(int recordIndex, DocumentIndexedData documentIndexedData){
		SDummy d = new SDummy(getContentPatterns(), recordIndex, documentIndexedData, debugWriter);
		clearContent();
		addToCurrentLevel(d);
	}		
}  
