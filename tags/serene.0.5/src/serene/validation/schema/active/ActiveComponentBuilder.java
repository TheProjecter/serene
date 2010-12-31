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

package serene.validation.schema.active;

import serene.validation.schema.ComponentBuilder;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ANameClass;
import serene.validation.schema.active.components.AParam;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AExceptNameClass;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AEmpty;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.ANotAllowed;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AGrammar;


import serene.validation.schema.active.components.AName;
import serene.validation.schema.active.components.AAnyName;
import serene.validation.schema.active.components.ANsName;
import serene.validation.schema.active.components.AChoiceNameClass;

import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.schema.active.util.Level;

import sereneWrite.MessageWriter;

/**
* Builds components, the big picture ActiveModel is the directors responsibility. 
* Can return/remove the components of the current level(usefull for attaching commands).
* Can check if the current level is top level.
*/
public class ActiveComponentBuilder implements ComponentBuilder{
	// Levels are used for storing the already build components.
	// When a component with children is built a "take what you need, distroy 
	// the rest" policy is used.
	Level level;
	//for debug only
	Level topLevel;


	
	ActiveModelStackHandlerPool stackHandlerPool;
	ActiveModelRuleHandlerPool ruleHandlerPool;

	
	MessageWriter debugWriter;
	
	public ActiveComponentBuilder(ActiveModelStackHandlerPool stackHandlerPool,
										ActiveModelRuleHandlerPool ruleHandlerPool, 
										MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.stackHandlerPool = stackHandlerPool;
		this.ruleHandlerPool = ruleHandlerPool;
		
		level = Level.getTopInstance(debugWriter);
		topLevel = level;
	}

	public void startLevel(){
		level = level.getLevelDown();		
	}	
	public void endLevel(){		
		level = level.getLevelUp();
	}
	
	
	public  void startBuild(){
		// TODO set top here// always start from the top
		level = topLevel;
		level.clearAll();
	}
	
	
	void clearContent(){
		if(!level.isBottomLevel()){
			Level contentLevel = level.getLevelDown();
			contentLevel.clear();
		}
	}
	
	
	void addToCurrentLevel(APattern p){
		level.add(p);
	}
	APattern[] getAllCurrentPatterns(){
		return level.getPatterns();
	}	
	APattern getCurrentPattern(){
		return level.getLastPattern();
	}	
	int getNumberOfChildPatterns(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getPatternsCount();
	}
	APattern[] getContentPatterns(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getPatterns();
	}	
	APattern getLastContentPattern(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastPattern();
	}
		
	
	void addToCurrentLevel(ANameClass nc){
		level.add(nc);
	}
	ANameClass[] getAllCurrentNameClasses(){
		return level.getNameClasses();
	}		
	ANameClass getCurrentNameClass(){		
		return level.getLastNameClass();
	}	
	int getNumberOfChildNameClasses(){
		Level contentLevel = level.getLevelDown();
		return contentLevel.getNameClassesCount();
	}		
	ANameClass[] getContentNameClasses(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();
		return contentLevel.getNameClasses();
	}		
	ANameClass getLastContentNameClass(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getLastNameClass();
	}
		
	
	void addToCurrentLevel(AParam p){
		level.add(p);
	}	
	AParam[] getAllCurrentParams(){
		return level.getParams();
	}	
	AParam getCurrentParam(){
		return level.getLastParam();
	}	
	int getNumberOfChildParams(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getParamsCount();
	}
	AParam[] getContentParams(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getParams();
	}	
	AParam getLastContentParam(){
		if(level.isBottomLevel())return null;		
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastParam();
	}	
		
	
	protected void addToCurrentLevel(AExceptPattern ep){
		level.add(ep);
	}
	protected AExceptPattern getCurrentExceptPattern(){
		return level.getExceptPattern();
	}		
	protected AExceptPattern getContentExceptPattern(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getExceptPattern();
	}	
	
	
	
	protected void addToCurrentLevel(AExceptNameClass enc){
		level.add(enc);
	}
	protected AExceptNameClass getCurrentExceptNameClass(){
		return level.getExceptNameClass();
	}		
	protected AExceptNameClass getContentExceptNameClass(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getExceptNameClass();
	}	
		

	//**************************************************************************
	//START PATTERN BUILDING ***************************************************
	//**************************************************************************
	public void buildElement(int index, ActiveGrammarModel model, String qName, String location){
		AElement e = new AElement(index, model, stackHandlerPool, ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(e);
	}
	public void buildAttribute(int index, ActiveGrammarModel model, String qName, String location){
		AAttribute a = new AAttribute(index, model, stackHandlerPool, ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(a);
	}
	public void buildGroup(String qName, String location){
		AGroup g = new AGroup(getContentPatterns(), stackHandlerPool, ruleHandlerPool, qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(g);	
	}		
	public void buildInterleave(String qName, String location){
		AInterleave i = new AInterleave(getContentPatterns(), stackHandlerPool, ruleHandlerPool, qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(i);	
	}
	public void buildChoicePattern(String qName, String location){
		AChoicePattern cp = new AChoicePattern(getContentPatterns(), ruleHandlerPool, qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cp);
	}	
	public void buildListPattern(String qName, String location){
		AListPattern lp = new AListPattern(getLastContentPattern(), stackHandlerPool, ruleHandlerPool, qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(lp);
	}
	public void buildRef(int definitionIndex, ActiveGrammarModel model, String qName, String location){
		ARef r = new ARef(definitionIndex, model, ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(r);
	}
	public void buildEmpty(String qName, String location){
		AEmpty e = new AEmpty(ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(e);
	}
	public void buildText(String qName, String location){
		AText t = new AText(ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(t);
	}
	public void buildValue(String ns, String datatypeLibrary, String type, String charContent, ActiveGrammarModel model, String qName, String location){
		AValue v = new AValue(ns, datatypeLibrary, type, charContent, model, ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(v);
	}
	public void buildData(String datatypeLibrary, String type, ActiveGrammarModel model, String qName, String location){
		AData d = new AData(datatypeLibrary, type, getContentParams(), getContentExceptPattern(),  model, ruleHandlerPool, qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(d);
	}		
	public void buildNotAllowed(String qName, String location){
		ANotAllowed na = new ANotAllowed(ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(na);
	}	
	public void buildGrammar(String qName, String location){
		AGrammar g = new AGrammar(getLastContentPattern(), ruleHandlerPool, qName, location, debugWriter);		
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
		AName n = new AName(ns, localPart, qName, location, debugWriter);
		addToCurrentLevel(n);
	}
	public void buildAnyName(String qName, String location){
		AAnyName an = new AAnyName(getContentExceptNameClass(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(an);
	}
	public void buildNsName(String ns, String qName, String location){
		ANsName nn = new ANsName(ns, getContentExceptNameClass(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(nn);
	}
	public void buildChoiceNameClass(String qName, String location){
		AChoiceNameClass cnc = new AChoiceNameClass(getContentNameClasses(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cnc);
	}	
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************
	
	
	public void buildParam(String name, String charContent, String qName, String location){
		AParam p = new AParam(name, charContent, ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(p);
	}

	public void buildExceptNameClass(String qName, String location){
		AExceptNameClass enc = new AExceptNameClass(getLastContentNameClass(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(enc);
	}
	public void buildExceptPattern(int index, ActiveGrammarModel model, String qName, String location){
		AExceptPattern ep = new AExceptPattern(index, model, stackHandlerPool, ruleHandlerPool, qName, location, debugWriter);
		addToCurrentLevel(ep);
	}	
}  