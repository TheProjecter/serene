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

package serene.validation.schema.parsed;

import java.util.Map;

import serene.validation.schema.ComponentBuilder;


import serene.validation.schema.parsed.components.Pattern;
import serene.validation.schema.parsed.components.NameClass;
import serene.validation.schema.parsed.components.Param;
import serene.validation.schema.parsed.components.Include;
import serene.validation.schema.parsed.components.IncludeContent;
import serene.validation.schema.parsed.components.GrammarContent;
import serene.validation.schema.parsed.components.ExceptPattern;
import serene.validation.schema.parsed.components.ExceptNameClass;
import serene.validation.schema.parsed.components.DivGrammarContent;
import serene.validation.schema.parsed.components.DivIncludeContent;

import serene.validation.schema.parsed.components.ElementWithNameClass;
import serene.validation.schema.parsed.components.ElementWithNameInstance;
import serene.validation.schema.parsed.components.AttributeWithNameClass;
import serene.validation.schema.parsed.components.AttributeWithNameInstance;
import serene.validation.schema.parsed.components.ChoicePattern;
import serene.validation.schema.parsed.components.Interleave;
import serene.validation.schema.parsed.components.Group;
import serene.validation.schema.parsed.components.ZeroOrMore;
import serene.validation.schema.parsed.components.OneOrMore;
import serene.validation.schema.parsed.components.Optional;
import serene.validation.schema.parsed.components.ListPattern;
import serene.validation.schema.parsed.components.Mixed;
import serene.validation.schema.parsed.components.Empty;
import serene.validation.schema.parsed.components.Text;
import serene.validation.schema.parsed.components.NotAllowed;
import serene.validation.schema.parsed.components.ExternalRef;
import serene.validation.schema.parsed.components.Ref;
import serene.validation.schema.parsed.components.ParentRef;
import serene.validation.schema.parsed.components.Data;
import serene.validation.schema.parsed.components.Value;
import serene.validation.schema.parsed.components.Grammar;
import serene.validation.schema.parsed.components.Dummy;

import serene.validation.schema.parsed.components.Name;
import serene.validation.schema.parsed.components.AnyName;
import serene.validation.schema.parsed.components.NsName;
import serene.validation.schema.parsed.components.ChoiceNameClass;

import serene.validation.schema.parsed.components.Define;
import serene.validation.schema.parsed.components.Start;

import sereneWrite.MessageWriter;


import serene.validation.schema.parsed.util.Level;

public class ParsedComponentBuilder implements ComponentBuilder{	
	
	Level level;
	//for debug only
	Level topLevel;	
	MessageWriter debugWriter;
	
	public ParsedComponentBuilder(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		level = Level.getTopInstance(debugWriter);
		topLevel = level;
	}
	
	public void startLevel(){
		level = level.getLevelDown();		
	}	
	public void endLevel(){		
		level = level.getLevelUp();
	}
	
	
	public void startBuild(){
		// TODO set top here// always start from the top
		level = topLevel;
		level.clearAll();
	}
	
	void clearContent(){
		if(level.isBottomLevel())return;
		Level content = level.getLevelDown();
		content.clear();
	}
	
	void addToCurrentLevel(Pattern p){
		level.add(p);
	}
	Pattern[] getAllCurrentPatterns(){
		return level.getPatterns();
	}	
	public Pattern getCurrentPattern(){
		return level.getLastPattern();
	}
	
	int getNumberOfChildPatterns(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getPatternsCount();
	}
	Pattern[] getContentPatterns(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getPatterns();
	}	
	
	Pattern getLastContentPattern(){
		if(level.isBottomLevel())return null;		
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastPattern();
	}	
	
	
	void addToCurrentLevel(NameClass nc){
		level.add(nc);
	}
	NameClass[] getAllCurrentNameClasses(){
		return level.getNameClasses();
	}		
	public NameClass getCurrentNameClass(){		
		return level.getLastNameClass();
	}
	
	int getNumberOfChildNameClasses(){
		Level contentLevel = level.getLevelDown();
		return contentLevel.getNameClassesCount();
	}		
	NameClass[] getContentNameClasses(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();
		return contentLevel.getNameClasses();
	}		
	NameClass getLastContentNameClass(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastNameClass();
	}	
	
	void addToCurrentLevel(Param p){
		level.add(p);
	}
	Param[] getAllCurrentParams(){
		return level.getParams();
	}	
	public Param getCurrentParam(){
		return level.getLastParam();
	}
	
	int getNumberOfChildParams(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getParamsCount();
	}
	Param[] getContentParams(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getParams();
	}	
	
	Param getLastContentParam(){
		if(level.isBottomLevel())return null;		
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastParam();
	}
	
	void addToCurrentLevel(Define d){
		level.add(d);
	}
	void addToCurrentLevel(Start s){
		level.add(s);
	}
	
	void addToCurrentLevel(IncludeContent ic){
		level.add(ic);
	}
	IncludeContent[] getAllCurrentIncludeContent(){
		return level.getIncludeContent();
	}	
	public IncludeContent getCurrentIncludeContent(){
		return level.getLastIncludeContent();
	}
	
	int getNumberOfChildIncludeContent(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getIncludeContentCount();
	}
	IncludeContent[] getContentIncludeContent(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getIncludeContent();
	}	
	
	IncludeContent getLastContentIncludeContent(){
		if(level.isBottomLevel())return null;		
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastIncludeContent();
	}
	
	void addToCurrentLevel(GrammarContent ic){
		level.add(ic);
	}
	GrammarContent[] getAllCurrentGrammarContent(){
		return level.getGrammarContent();
	}	
	public GrammarContent getCurrentGrammarContent(){
		return level.getLastGrammarContent();
	}
	
	int getNumberOfChildGrammarContent(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getGrammarContentCount();
	}
	GrammarContent[] getContentGrammarContent(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getGrammarContent();
	}	
	
	GrammarContent getLastContentGrammarContent(){
		if(level.isBottomLevel())return null;		
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastGrammarContent();
	}
	
	void addToCurrentLevel(ExceptPattern ep){
		level.add(ep);
	}
	ExceptPattern[] getAllCurrentExceptPatterns(){
		return level.getExceptPatterns();
	}	
	public ExceptPattern getCurrentExceptPattern(){
		return level.getLastExceptPattern();
	}
	
	int getNumberOfChildExceptPatterns(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getExceptPatternsCount();
	}
	ExceptPattern[] getContentExceptPatterns(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getExceptPatterns();
	}	
	
	ExceptPattern getLastContentExceptPattern(){
		if(level.isBottomLevel())return null;		
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastExceptPattern();
	}
	
	
	void addToCurrentLevel(ExceptNameClass enc){
		level.add(enc);
	}
	ExceptNameClass getCurrentExceptNameClass(){
		return level.getExceptNameClass();
	}		
	ExceptNameClass getContentExceptNameClass(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getExceptNameClass();
	}
	
	
	//**************************************************************************
	//START PATTERN BUILDING ***************************************************
	//**************************************************************************
	public void buildElementWithNameClass(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		ElementWithNameClass ewnc = new ElementWithNameClass(prefixMapping, xmlBase, ns, datatypeLibrary, getLastContentNameClass(), getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(ewnc);
	}
	public void buildElementWithNameInstance(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, String qName, String location){
		ElementWithNameInstance ewni = new ElementWithNameInstance(prefixMapping, xmlBase, ns, datatypeLibrary, name, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(ewni);
	}
	public void buildAttributeWithNameClass(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		AttributeWithNameClass awnc = new AttributeWithNameClass(prefixMapping, xmlBase, ns, datatypeLibrary, getLastContentNameClass(), getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(awnc);
	}
	public void buildAttributeWithNameInstance(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, String qName, String location){
		AttributeWithNameInstance awni = new AttributeWithNameInstance(prefixMapping, xmlBase, ns, datatypeLibrary, name, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(awni);
	}	
	public void buildGroup(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		Group g = new Group(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(g);	
	}		
	public void buildInterleave(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		Interleave i = new Interleave(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(i);	
	}
	public void buildChoicePattern(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		ChoicePattern cp = new ChoicePattern(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cp);
	}
	public void buildOptional(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		Optional o = new Optional(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(o);	
	}
	public void buildZeroOrMore(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		ZeroOrMore zom = new ZeroOrMore(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(zom);
	}
	public void buildOneOrMore(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		OneOrMore oom = new OneOrMore(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(oom);
	}
	public void buildListPattern(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		ListPattern lp = new ListPattern(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(lp);
	}
	public void buildMixed(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		Mixed m = new Mixed(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(m);
	}
	public void buildRef(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, String qName, String location){
		Ref r = new Ref(prefixMapping, xmlBase, ns, datatypeLibrary, name, qName, location, debugWriter);
		addToCurrentLevel(r);
	}
	public void buildParentRef(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, String qName, String location){
		ParentRef r = new ParentRef(prefixMapping, xmlBase, ns, datatypeLibrary, name, qName, location, debugWriter);
		addToCurrentLevel(r);
	}
	public void buildEmpty(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		Empty e = new Empty(prefixMapping, xmlBase, ns, datatypeLibrary, qName, location, debugWriter);
		addToCurrentLevel(e);
	}
	public void buildText(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		Text t = new Text(prefixMapping, xmlBase, ns, datatypeLibrary, qName, location, debugWriter);
		addToCurrentLevel(t);
	}	
	public void buildValue(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String type, String charContent, String qName, String location){
		Value v = new Value(prefixMapping, xmlBase, ns, datatypeLibrary, type, charContent, qName, location, debugWriter);
		addToCurrentLevel(v);
	}
	public void buildData(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String type, String qName, String location){
		Data d = new Data(prefixMapping, xmlBase, ns, datatypeLibrary, type, getContentParams(), getContentExceptPatterns(), qName, location, debugWriter);		
		clearContent();
		addToCurrentLevel(d);
	}		
	public void buildNotAllowed(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		NotAllowed na = new NotAllowed(prefixMapping, xmlBase, ns, datatypeLibrary, qName, location, debugWriter);
		addToCurrentLevel(na);
	}			
	public void buildExternalRef(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String href, String qName, String location){
		ExternalRef er = new ExternalRef(prefixMapping, xmlBase, ns, datatypeLibrary, href, qName, location, debugWriter);
		addToCurrentLevel(er);
	}
	public void buildGrammar(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		Grammar g = new Grammar(prefixMapping, xmlBase, ns, datatypeLibrary, getContentGrammarContent(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(g);
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	//**************************************************************************
	//START NAME CLASS BUILDING ************************************************
	//**************************************************************************	
	public void buildName(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String characterContent, String qName, String location){
		Name n = new Name(prefixMapping, xmlBase, ns, datatypeLibrary, characterContent, qName, location, debugWriter);
		addToCurrentLevel(n);
	}
	public void buildAnyName(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		AnyName an = new AnyName(prefixMapping, xmlBase, ns, datatypeLibrary, getContentExceptNameClass(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(an);
	}
	public void buildNsName(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		NsName nn = new NsName(prefixMapping, xmlBase, ns, datatypeLibrary, getContentExceptNameClass(), qName, location, debugWriter);		
		clearContent();
		addToCurrentLevel(nn);
	}
	public void buildChoiceNameClass(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		ChoiceNameClass cnc = new ChoiceNameClass(prefixMapping, xmlBase, ns, datatypeLibrary, getContentNameClasses(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cnc);
	}	
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START DEFINITION BUILDING ************************************************
	//**************************************************************************	
	public void buildDefine(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, String combine, String qName, String location){
		Define d = new Define(prefixMapping, xmlBase, ns, datatypeLibrary, name, combine, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(d);
	}
	public void buildStart(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String combine, String qName, String location){
		Start s = new Start(prefixMapping, xmlBase, ns, datatypeLibrary, combine, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(s);
	}
	//**************************************************************************
	//END DEFINITION BUILDING **************************************************
	//**************************************************************************

	public void buildDivIncludeContent(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		DivIncludeContent dic = new DivIncludeContent(prefixMapping, xmlBase, ns, datatypeLibrary, getContentIncludeContent(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(dic);
	}
	public void buildDivGrammarContent(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		DivGrammarContent dgc = new DivGrammarContent(prefixMapping, xmlBase, ns, datatypeLibrary, getContentGrammarContent(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(dgc);
	}

	public void buildParam(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, String charContent, String qName, String location){
		Param p = new Param(prefixMapping, xmlBase, ns, datatypeLibrary, name, charContent, qName, location, debugWriter);
		addToCurrentLevel(p);
	}

	public void buildExceptNameClass(Map<String, String> prefixMapping, String xmlBase,  String ns, String datatypeLibrary, String qName, String location){
		ExceptNameClass enc = new ExceptNameClass(prefixMapping, xmlBase, ns, datatypeLibrary, getContentNameClasses(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(enc);
	}
	public void buildExceptPattern(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		ExceptPattern ep = new ExceptPattern(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(ep);
	}
	
	public void buildInclude(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String href, String qName, String location){
		Include i = new Include(prefixMapping, xmlBase, ns, datatypeLibrary, href, getContentIncludeContent(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(i);
	}
	
	public void buildDummy(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String qName, String location){
		Dummy dd = new Dummy(prefixMapping, xmlBase, ns, datatypeLibrary, getContentPatterns(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(dd);
	}
}