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
	public void buildElement(String qName, String location){
		SElement e = new SElement(getLastContentNameClass(), getLastContentPattern(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(e);
	}
	public void buildAttribute(String defaultValue, String qName, String location){
		SAttribute a = new SAttribute(getLastContentNameClass(), getContentPatterns(), defaultValue, qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(a);
	}
	public void buildGroup(String qName, String location){
		SGroup g = new SGroup(getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(g);
	}
    public void buildReplacementGroup(String qName, String location){
        SPattern[] children = getAllCurrentPatterns();
		SGroup g = new SGroup(children, qName, location, debugWriter);
		clearCurrentPatterns();
		addToCurrentLevel(g);
	}
	public void buildInterleave(String qName, String location){
		SInterleave i = new SInterleave(getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(i);
	}
    public void buildInterleave(String qName, ArrayList<String> allLocations, SPattern[] children){
		SInterleave i = new SInterleave(children, qName, allLocations, debugWriter);
		addToCurrentLevel(i);	
	}
	public void buildChoicePattern(String qName, String location){
		SChoicePattern cp = new SChoicePattern(getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cp);
	}
    public void buildChoicePattern(String qName, ArrayList<String> allLocations, SPattern[] children){
		SChoicePattern cp = new SChoicePattern(children, qName, allLocations, debugWriter);		
		addToCurrentLevel(cp);
	}
    public void buildReplacementChoicePattern(String qName, String location){
        SPattern[] children = getAllCurrentPatterns();
		SChoicePattern c = new SChoicePattern(children, qName, location, debugWriter);
		clearCurrentPatterns();
		addToCurrentLevel(c);	
	}
	public void buildMixed(String qName, String location){
		SMixed o = new SMixed(getLastContentPattern(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(o);
	}
	public void buildOptional(String qName, String location){
		SOptional o = new SOptional(getLastContentPattern(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(o);
	}
	public void buildZeroOrMore(String qName, String location){
		SZeroOrMore zom = new SZeroOrMore(getLastContentPattern(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(zom);
	}
	public void buildOneOrMore(String qName, String location){
		SOneOrMore oom = new SOneOrMore(getLastContentPattern(), qName, location, debugWriter);
		clearContent();		
		addToCurrentLevel(oom);
	}
	public void buildListPattern(String qName, String location){
		SListPattern lp = new SListPattern(getLastContentPattern(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(lp);
	}
	public void buildRef(int definitionIndex, String qName, String location){
		SRef r = new SRef(definitionIndex, qName, location, debugWriter);
		addToCurrentLevel(r);
	}
	public void buildEmpty(String qName, String location){
		SEmpty e = new SEmpty(qName, location, debugWriter);
		addToCurrentLevel(e);
	}
	public void buildText(String qName, String location){
		SText t = new SText(qName, location, debugWriter);
		addToCurrentLevel(t);
	}
	public void buildValue(String ns, Datatype datatype, String charContent, String qName, String location){
		SValue v = new SValue(ns, datatype, charContent, qName, location, debugWriter);
		addToCurrentLevel(v);
	}
	public void buildData(Datatype datatype, String qName, String location){
		SData d = new SData(datatype, getContentExceptPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(d);
	}		
	public void buildNotAllowed(String qName, String location){
		SNotAllowed na = new SNotAllowed(qName, location, debugWriter);
		addToCurrentLevel(na);
	}	
	public void buildGrammar(String qName, String location){
		SGrammar g = new SGrammar(getLastContentPattern(), qName, location, debugWriter);
		clearContent();		
		addToCurrentLevel(g);
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	//**************************************************************************
	//START NAME CLASS BUILDING ************************************************
	//**************************************************************************
	public void buildName(String ns, String localPart, String qName, String location){
		SName n = new SName(ns, localPart, qName, location, debugWriter);
		addToCurrentLevel(n);
	}
	public void buildAnyName(String qName, String location){
		SAnyName an = new SAnyName(getContentExceptNameClass(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(an);
	}
	public void buildNsName(String ns, String qName, String location){
		SNsName nn = new SNsName(ns, getContentExceptNameClass(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(nn);
	}
	public void buildChoiceNameClass(String qName, String location){
		SChoiceNameClass cnc = new SChoiceNameClass(getContentNameClasses(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cnc);
	}	
    public void buildReplacementChoiceNameClass(String qName, String location){
        SNameClass[] children = getAllCurrentNameClasses();
		SChoiceNameClass c = new SChoiceNameClass(children, qName, location, debugWriter);
		clearCurrentNameClasses();
		addToCurrentLevel(c);
	}
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************	
	public void buildExceptNameClass(String qName, String location){
		SExceptNameClass enc = new SExceptNameClass(getLastContentNameClass(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(enc);
	}
	public void buildExceptPattern(String qName, String location){
		SExceptPattern ep = new SExceptPattern(getLastContentPattern(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(ep);
	}
	
	
	public void buildDummy(String qName, String location){
		SDummy d = new SDummy(getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(d);
	}		
}  
