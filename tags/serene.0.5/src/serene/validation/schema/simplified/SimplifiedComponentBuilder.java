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

import serene.validation.schema.ComponentBuilder;

import serene.validation.schema.simplified.components.SParam;
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
	
		
	
	public void addToCurrentLevel(SParam p){
		level.add(p);
	}
	public SParam[] getAllCurrentParams(){
		return level.getParams();
	}	
	public SParam getCurrentParam(){
		return level.getLastParam();
	}
	public int getCurrentParamsCount(){				
		return level.getParamsCount();
	}
	public int getContentParamsCount(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getParamsCount();
	}
	public SParam[] getContentParams(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getParams();
	}	
	public SParam getLastContentParam(){		
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastParam();
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
	public void buildAttribute(String qName, String location){
		SAttribute a = new SAttribute(getLastContentNameClass(), getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(a);
	}
	public void buildGroup(String qName, String location){
		SGroup g = new SGroup(getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(g);	
	}		
	public void buildInterleave(String qName, String location){
		SInterleave i = new SInterleave(getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(i);	
	}
	public void buildChoicePattern(String qName, String location){
		SChoicePattern cp = new SChoicePattern(getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cp);
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
	public void buildValue(String ns, String datatypeLibrary, String type, String charContent, String qName, String location){
		SValue v = new SValue(ns, datatypeLibrary, type, charContent, qName, location, debugWriter);
		addToCurrentLevel(v);
	}
	public void buildData(String datatypeLibrary, String type, String qName, String location){
		SData d = new SData(datatypeLibrary, type, getContentParams(), getContentExceptPatterns(), qName, location, debugWriter);
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
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************
	public void buildParam(String name, String charContent, String qName, String location){
		SParam p = new SParam(name, charContent, qName, location, debugWriter);
		addToCurrentLevel(p);
	}

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