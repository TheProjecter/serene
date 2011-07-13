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

import serene.util.AttributeInfo;

import serene.validation.schema.ComponentBuilder;

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
	
	void addToCurrentLevel(ParsedComponent pc ){
		level.add(pc);
	}
	ParsedComponent[] getAllCurrentParsedComponents(){
		return level.getParsedComponents();
	}	
	public ParsedComponent getCurrentParsedComponent(){
		return level.getLastParsedComponent();
	}
	
	int getNumberOfChildParsedComponents(){
		Level contentLevel = level.getLevelDown();		
		return contentLevel.getParsedComponentsCount();
	}
	ParsedComponent[] getContentParsedComponents(){
		if(level.isBottomLevel())return null;
		Level contentLevel = level.getLevelDown();	
		return contentLevel.getParsedComponents();
	}	
	
	ParsedComponent getLastContentParsedComponent(){
		if(level.isBottomLevel())return null;		
		Level contentLevel = level.getLevelDown();
		return contentLevel.getLastParsedComponent();
	}	
	
	
	void writeLevels(){
	}
	
	//**************************************************************************
	//START PATTERN BUILDING ***************************************************
	//**************************************************************************
	public void buildElementWithNameClass(Map<String, String> prefixMapping, 
                                            String xmlBase, 
                                            String ns,
                                            String datatypeLibrary,
                                            AttributeInfo[] foreignAttributes,
                                            String qName, 
                                            String location){
		writeLevels();	
		ElementWithNameClass ewnc = new ElementWithNameClass(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(ewnc);
		writeLevels();
	}
	public void buildElementWithNameInstance(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ElementWithNameInstance ewni = new ElementWithNameInstance(prefixMapping, xmlBase, ns, datatypeLibrary, name, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(ewni);
		writeLevels();
	}
	public void buildAttributeWithNameClass(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		AttributeWithNameClass awnc = new AttributeWithNameClass(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(awnc);
		writeLevels();
	}
	public void buildAttributeWithNameInstance(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		AttributeWithNameInstance awni = new AttributeWithNameInstance(prefixMapping, xmlBase, ns, datatypeLibrary, name, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(awni);
		writeLevels();
	}	
	public void buildGroup(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Group g = new Group(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(g);
		writeLevels();	
	}		
	public void buildInterleave(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();
		Interleave i = new Interleave(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(i);
		writeLevels();	
	}
	public void buildChoicePattern(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ChoicePattern cp = new ChoicePattern(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cp);
		writeLevels();
	}
	public void buildOptional(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Optional o = new Optional(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(o);
		writeLevels();	
	}
	public void buildZeroOrMore(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ZeroOrMore zom = new ZeroOrMore(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(zom);
		writeLevels();
	}
	public void buildOneOrMore(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		OneOrMore oom = new OneOrMore(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(oom);
		writeLevels();
	}
	public void buildListPattern(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ListPattern lp = new ListPattern(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(lp);
		writeLevels();
	}
	public void buildMixed(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Mixed m = new Mixed(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(m);
		writeLevels();
	}
	public void buildRef(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Ref r = new Ref(prefixMapping, xmlBase, ns, datatypeLibrary, name, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		addToCurrentLevel(r);
		writeLevels();
	}
	public void buildParentRef(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ParentRef r = new ParentRef(prefixMapping, xmlBase, ns, datatypeLibrary, name, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		addToCurrentLevel(r);
		writeLevels();
	}
	public void buildEmpty(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Empty e = new Empty(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		addToCurrentLevel(e);
		writeLevels();
	}
	public void buildText(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Text t = new Text(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		addToCurrentLevel(t);
		writeLevels();
	}	
	public void buildValue(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String type, AttributeInfo[] foreignAttributes, String charContent, String qName, String location){
		writeLevels();		
		Value v = new Value(prefixMapping, xmlBase, ns, datatypeLibrary, type, foreignAttributes, charContent, qName, location, debugWriter);
		addToCurrentLevel(v);
		writeLevels();
	}
	public void buildData(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String type, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Data d = new Data(prefixMapping, xmlBase, ns, datatypeLibrary, type, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);		
		clearContent();
		addToCurrentLevel(d);
		writeLevels();
	}		
	public void buildNotAllowed(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		NotAllowed na = new NotAllowed(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		addToCurrentLevel(na);
		writeLevels();
	}			
	public void buildExternalRef(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String href, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ExternalRef er = new ExternalRef(prefixMapping, xmlBase, ns, datatypeLibrary, href, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		addToCurrentLevel(er);
		writeLevels();
	}
	public void buildGrammar(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Grammar g = new Grammar(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(g);
		writeLevels();
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	//**************************************************************************
	//START NAME CLASS BUILDING ************************************************
	//**************************************************************************	
	public void buildName(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String characterContent, String qName, String location){
		writeLevels();		
		Name n = new Name(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, characterContent, qName, location, debugWriter);
		addToCurrentLevel(n);
		writeLevels();
	}
	public void buildAnyName(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		AnyName an = new AnyName(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(an);
		writeLevels();
	}
	public void buildNsName(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		NsName nn = new NsName(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);		
		clearContent();
		addToCurrentLevel(nn);
		writeLevels();
	}
	public void buildChoiceNameClass(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ChoiceNameClass cnc = new ChoiceNameClass(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(cnc);
		writeLevels();
	}	
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START DEFINITION BUILDING ************************************************
	//**************************************************************************	
	public void buildDefine(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, String combine, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Define d = new Define(prefixMapping, xmlBase, ns, datatypeLibrary, name, combine, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(d);
		writeLevels();
	}
	public void buildStart(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String combine, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Start s = new Start(prefixMapping, xmlBase, ns, datatypeLibrary, combine, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(s);
		writeLevels();
	}
	//**************************************************************************
	//END DEFINITION BUILDING **************************************************
	//**************************************************************************

	public void buildDivIncludeContent(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		DivIncludeContent dic = new DivIncludeContent(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(dic);
		writeLevels();
	}
	public void buildDivGrammarContent(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		DivGrammarContent dgc = new DivGrammarContent(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(dgc);
		writeLevels();
	}

	public void buildParam(Map<String, String> prefixMapping, 
                            String xmlBase, 
                            String ns, 
                            String datatypeLibrary, 
                            String name,
                            AttributeInfo[] foreignAttributes,
                            String charContent, 
                            String qName, 
                            String location){
		writeLevels();		
		Param p = new Param(prefixMapping, xmlBase, ns, datatypeLibrary, name, foreignAttributes, charContent, qName, location, debugWriter);
		addToCurrentLevel(p);
		writeLevels();
	}

	public void buildExceptNameClass(Map<String, String> prefixMapping, String xmlBase,  String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ExceptNameClass enc = new ExceptNameClass(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(enc);
		writeLevels();
	}
	public void buildExceptPattern(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ExceptPattern ep = new ExceptPattern(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(ep);
		writeLevels();
	}
	
	public void buildInclude(Map<String, String> prefixMapping, 
                                String xmlBase, 
                                String ns, 
                                String datatypeLibrary, 
                                String href,
                                AttributeInfo[] foreignAttributes,
                                String qName, 
                                String location){
		writeLevels();		
		Include i = new Include(prefixMapping, xmlBase, ns, datatypeLibrary, href, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(i);
		writeLevels();
	}
	
	public void buildDummy(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		Dummy dd = new Dummy(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(dd);
		writeLevels();
	}
    
    public void buildForeignComponent(String namespaceURI, String localName, Map<String, String> prefixMapping, String xmlBase, AttributeInfo[] foreignAttributes, String qName, String location){
		writeLevels();		
		ForeignComponent fc = new ForeignComponent(namespaceURI, localName, prefixMapping, xmlBase, foreignAttributes, getContentParsedComponents(), qName, location, debugWriter);
		clearContent();
		addToCurrentLevel(fc);
		writeLevels();
	}
}
