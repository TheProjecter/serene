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

import org.relaxng.datatype.Datatype;

import serene.validation.schema.ComponentBuilder;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SEmpty;
import serene.validation.schema.simplified.components.SText;
import serene.validation.schema.simplified.components.SNotAllowed;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SValue;
import serene.validation.schema.simplified.components.SGrammar;
import serene.validation.schema.simplified.components.SMixed;

import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.simplified.components.SExceptNameClass;
import serene.validation.schema.simplified.components.SExceptPattern;


import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ANameClass;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AExceptNameClass;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AInterleaveI;
import serene.validation.schema.active.components.AInterleaveM;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AEmpty;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.ATextM;
import serene.validation.schema.active.components.ATextT;
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


	public ActiveComponentBuilder(ActiveModelStackHandlerPool stackHandlerPool,
										ActiveModelRuleHandlerPool ruleHandlerPool){
		this.stackHandlerPool = stackHandlerPool;
		this.ruleHandlerPool = ruleHandlerPool;
		
		level = Level.getTopInstance();
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
	public void buildElement(int index, ActiveGrammarModel model, SElement simplifiedComponent){
		AElement e = new AElement(index, model, stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(e);
	}
	public void buildAttribute(int index, ActiveGrammarModel model, SAttribute simplifiedComponent){
		AAttribute a = new AAttribute(index, model, stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(a);
	}
	public void buildGroup(SGroup simplifiedComponent){
		AGroup g = new AGroup(getContentPatterns(), stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		clearContent();
		addToCurrentLevel(g);	
	}		
	public void buildInterleave(SInterleave simplifiedComponent){
		AInterleave i = new AInterleaveI(getContentPatterns(), stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		clearContent();
		addToCurrentLevel(i);	
	}
	public void buildInterleave(SMixed simplifiedComponent){
		AInterleave i = new AInterleaveM(getContentPatterns(), stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		clearContent();
		addToCurrentLevel(i);	
	}
	public void buildChoicePattern(SChoicePattern simplifiedComponent){
		AChoicePattern cp = new AChoicePattern(getContentPatterns(), ruleHandlerPool, simplifiedComponent);
		clearContent();
		addToCurrentLevel(cp);
	}	
	public void buildListPattern(SListPattern simplifiedComponent){
		AListPattern lp = new AListPattern(getLastContentPattern(), stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		clearContent();
		addToCurrentLevel(lp);
	}
	public void buildRef(int definitionIndex, ActiveGrammarModel model, SRef simplifiedComponent){
		ARef r = new ARef(definitionIndex, model, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(r);
	}
	public void buildEmpty(SimplifiedComponent simplifiedComponent){
		AEmpty e = new AEmpty(ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(e);
	}
	public void buildText(SText simplifiedComponent){
		AText t = new ATextT(ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(t);
	}
	public void buildText(SMixed simplifiedComponent){
		AText t = new ATextM(ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(t);
	}
	public void buildValue(String ns, Datatype datatype, String charContent, ActiveGrammarModel model, SValue simplifiedComponent){
		AValue v = new AValue(ns, datatype, charContent, model, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(v);
	}
	public void buildData(Datatype datatype, ActiveGrammarModel model, SData simplifiedComponent){
		AData d = new AData(datatype, getContentExceptPattern(),  model, ruleHandlerPool, simplifiedComponent);
		clearContent();
		addToCurrentLevel(d);
	}		
	public void buildNotAllowed(SNotAllowed simplifiedComponent){
		ANotAllowed na = new ANotAllowed(ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(na);
	}	
	public void buildGrammar(SGrammar simplifiedComponent){
		AGrammar g = new AGrammar(getLastContentPattern(), ruleHandlerPool, simplifiedComponent);		
		clearContent();
		addToCurrentLevel(g);
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	//**************************************************************************
	//START NAME CLASS BUILDING ************************************************
	//**************************************************************************	
	public void buildName(String ns, String localPart, SName simplifiedComponent){
		AName n = new AName(ns, localPart, simplifiedComponent);
		addToCurrentLevel(n);
	}
	public void buildAnyName(SAnyName simplifiedComponent){
		AAnyName an = new AAnyName(getContentExceptNameClass(), simplifiedComponent);
		clearContent();
		addToCurrentLevel(an);
	}
	public void buildNsName(String ns, SNsName simplifiedComponent){
		ANsName nn = new ANsName(ns, getContentExceptNameClass(), simplifiedComponent);
		clearContent();
		addToCurrentLevel(nn);
	}
	public void buildChoiceNameClass(SChoiceNameClass simplifiedComponent){
		AChoiceNameClass cnc = new AChoiceNameClass(getContentNameClasses(), simplifiedComponent);
		clearContent();
		addToCurrentLevel(cnc);
	}	
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************

	public void buildExceptNameClass(SExceptNameClass simplifiedComponent){
		AExceptNameClass enc = new AExceptNameClass(getLastContentNameClass(), simplifiedComponent);
		clearContent();
		addToCurrentLevel(enc);
	}
	public void buildExceptPattern(int index, ActiveGrammarModel model, SExceptPattern simplifiedComponent){
		AExceptPattern ep = new AExceptPattern(index, model, stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(ep);
	}	
}  