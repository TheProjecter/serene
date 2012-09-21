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

package serene.validation.schema.active.components;

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

import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.simplified.components.SExceptNameClass;
import serene.validation.schema.simplified.components.SExceptPattern;

import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.schema.active.util.Level;

import serene.validation.schema.Identifier;

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

	ActiveComponentBuilder parent;
	ActiveComponentBuilder child;
	
	public ActiveComponentBuilder(ActiveModelStackHandlerPool stackHandlerPool,
										ActiveModelRuleHandlerPool ruleHandlerPool){
		this.stackHandlerPool = stackHandlerPool;
		this.ruleHandlerPool = ruleHandlerPool;
		
		level = Level.getTopInstance();
		topLevel = level;
	}
	
	public ActiveComponentBuilder(ActiveModelStackHandlerPool stackHandlerPool,
										ActiveModelRuleHandlerPool ruleHandlerPool,
										ActiveComponentBuilder parent){
		this.stackHandlerPool = stackHandlerPool;
		this.ruleHandlerPool = ruleHandlerPool;
	    this.parent = parent;
	    
		level = Level.getTopInstance();
		topLevel = level;
	}

	public ActiveComponentBuilder getChild(){
	    if(child != null)return child;
	    child = new ActiveComponentBuilder(stackHandlerPool,
	                                    ruleHandlerPool, 
	                                    this);
	    return child;
	}
	
	public ActiveComponentBuilder getParent(){
	    return parent;
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
	public APattern getCurrentPattern(){
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
	
		
	//**************************************************************************
	//START PATTERN BUILDING ***************************************************
	//**************************************************************************
	public AElement buildElement(int index, Identifier identifier, ActiveGrammarModel model, SElement simplifiedComponent){
		AElement e = new AElement(index, identifier, model, stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(e);
		return e;
	}
	public AAttribute buildAttribute(int index, Identifier identifier, ActiveGrammarModel model, SAttribute simplifiedComponent){
		AAttribute a = new AAttribute(index, identifier, model, stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(a);
		return a;
	}
	public AGroup buildGroup(boolean allowsElements,
                        boolean allowsAttributes,
                        boolean allowsDatas,
                        boolean allowsValues,	
                        boolean allowsListPatterns,
                        boolean allowsText,
                        SGroup simplifiedComponent){
		AGroup g = new AGroup(getContentPatterns(), 
		                        allowsElements,
                                allowsAttributes,
                                allowsDatas,
                                allowsValues,	
                                allowsListPatterns,
                                allowsText,
                                stackHandlerPool, 
                                ruleHandlerPool, 
                                simplifiedComponent);
		clearContent();
		addToCurrentLevel(g);
		return g;
	}		
	public AInterleave buildInterleave(boolean allowsElements,
                            boolean allowsAttributes,
                            boolean allowsDatas,
                            boolean allowsValues,	
                            boolean allowsListPatterns,
                            boolean allowsText,
                            SInterleave simplifiedComponent){
		AInterleave i = new AInterleaveI(getContentPatterns(), 
		                            allowsElements,
                                    allowsAttributes,
                                    allowsDatas,
                                    allowsValues,	
                                    allowsListPatterns,
                                    allowsText,
                                    stackHandlerPool, 
                                    ruleHandlerPool, 
                                    simplifiedComponent);
		clearContent();
		addToCurrentLevel(i);
		return i;
	}
	/*public AInterleave buildInterleave(boolean allowsElements,
                            boolean allowsAttributes,
                            boolean allowsDatas,
                            boolean allowsValues,	
                            boolean allowsListPatterns,
                            boolean allowsText,
                            SMixed simplifiedComponent){
		AInterleave i = new AInterleaveM(getContentPatterns(), 
		                            allowsElements,
                                    allowsAttributes,
                                    allowsDatas,
                                    allowsValues,	
                                    allowsListPatterns,
                                    allowsText,
                                    stackHandlerPool, 
                                    ruleHandlerPool, 
                                    simplifiedComponent);
		clearContent();
		addToCurrentLevel(i);
		return i;
	}*/
	public AChoicePattern buildChoicePattern(boolean allowsElements,
                                boolean allowsAttributes,
                                boolean allowsDatas,
                                boolean allowsValues,	
                                boolean allowsListPatterns,
                                boolean allowsText,
                                SChoicePattern simplifiedComponent){
		AChoicePattern cp = new AChoicePattern(getContentPatterns(), 
		                                    allowsElements,
                                            allowsAttributes,
                                            allowsDatas,
                                            allowsValues,	
                                            allowsListPatterns,
                                            allowsText,
                                            ruleHandlerPool, 
                                            simplifiedComponent);
		clearContent();
		addToCurrentLevel(cp);
		return cp;
	}	
	public AListPattern buildListPattern(boolean allowsElements,
                                boolean allowsAttributes,
                                boolean allowsDatas,
                                boolean allowsValues,	
                                boolean allowsListPatterns,
                                boolean allowsText,
                                SListPattern simplifiedComponent){
		AListPattern lp = new AListPattern(getLastContentPattern(), 
		                            allowsElements,
                                    allowsAttributes,
                                    allowsDatas,
                                    allowsValues,	
                                    allowsListPatterns,
                                    allowsText,
                                    stackHandlerPool, 
                                    ruleHandlerPool, 
                                    simplifiedComponent);
		clearContent();
		addToCurrentLevel(lp);
		return lp;
	}
	public ARef buildRef(int definitionIndex,
                        ActiveGrammarModel model, 
                        SRef simplifiedComponent){
		ARef r = new ARef(definitionIndex, 
                        model, 
                        ruleHandlerPool, 
                        simplifiedComponent);
		addToCurrentLevel(r);
		return r;
	}
	public AEmpty buildEmpty(SimplifiedComponent simplifiedComponent){
		AEmpty e = new AEmpty(ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(e);
		return e;
	}
	public AText buildText(SText simplifiedComponent){
		AText t = new ATextT(ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(t);
		return t;
	}
	/*public AText buildText(SText simplifiedComponent){
		AText t = new ATextM(ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(t);
		return t;
	}*/
	public AValue buildValue(String ns, Datatype datatype, String charContent, ActiveGrammarModel model, SValue simplifiedComponent){
		AValue v = new AValue(ns, datatype, charContent, model, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(v);
		return v;
	}
	public AData buildData(Datatype datatype, ActiveGrammarModel model, SData simplifiedComponent){
		AData d = new AData(datatype, getContentExceptPattern(),  model, ruleHandlerPool, simplifiedComponent);
		clearContent();
		addToCurrentLevel(d);
		return d;
	}		
	public ANotAllowed buildNotAllowed(SNotAllowed simplifiedComponent){
		ANotAllowed na = new ANotAllowed(ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(na);
		return na;
	}	
	public AGrammar buildGrammar(boolean allowsElements,
                        boolean allowsAttributes,
                        boolean allowsDatas,
                        boolean allowsValues,	
                        boolean allowsListPatterns,
                        boolean allowsText,
                        SGrammar simplifiedComponent){
		AGrammar g = new AGrammar(getLastContentPattern(), 
		                    allowsElements,
                            allowsAttributes,
                            allowsDatas,
                            allowsValues,	
                            allowsListPatterns,
                            allowsText,
                            ruleHandlerPool, 
                            simplifiedComponent);		
		clearContent();
		addToCurrentLevel(g);
		return g;
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	
	public AExceptPattern buildExceptPattern(int index, ActiveGrammarModel model, SExceptPattern simplifiedComponent){
		AExceptPattern ep = new AExceptPattern(index, model, stackHandlerPool, ruleHandlerPool, simplifiedComponent);
		addToCurrentLevel(ep);
		return ep;
	}


    public void zeroOrMore(){
        
    }	
}  