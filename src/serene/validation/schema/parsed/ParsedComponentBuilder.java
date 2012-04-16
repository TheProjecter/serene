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
import serene.validation.schema.parsed.util.Level;

import serene.bind.util.DocumentIndexedData;

public class ParsedComponentBuilder implements ComponentBuilder{	
	
	Level level;
	//for debug only
	Level topLevel;
	
	int xmlBaseRecordIndex;
	int nsRecordIndex;
	int datatypeLibraryRecordIndex;
	int combineRecordIndex;
	int hrefRecordIndex;
	int nameRecordIndex;
	int typeRecordIndex;
	int defaultValueRecordIndex;
	
	DocumentIndexedData schemaDocumentIndexedData;
		
	
	public ParsedComponentBuilder(){
		level = Level.getTopInstance();
		topLevel = level;
		
		
		xmlBaseRecordIndex = DocumentIndexedData.NO_RECORD;
        nsRecordIndex = DocumentIndexedData.NO_RECORD;
        datatypeLibraryRecordIndex = DocumentIndexedData.NO_RECORD;
        combineRecordIndex = DocumentIndexedData.NO_RECORD;
        hrefRecordIndex = DocumentIndexedData.NO_RECORD;
        nameRecordIndex = DocumentIndexedData.NO_RECORD;
        typeRecordIndex = DocumentIndexedData.NO_RECORD;
        defaultValueRecordIndex = DocumentIndexedData.NO_RECORD;;
	}

	public void setDocumentIndexedData(DocumentIndexedData schemaDocumentIndexedData){
	    this.schemaDocumentIndexedData = schemaDocumentIndexedData;
	}
	
	public void setXMLBase(int xmlBase){
	    this.xmlBaseRecordIndex = xmlBase;
	}
	
	public void setNs(int ns){
	    this.nsRecordIndex = ns;
	}
	
	public void setDatatypeLibrary(int datatypeLibrary){
	    this.datatypeLibraryRecordIndex = datatypeLibrary;
	}
	
	public void setCombine(int combine){
	    this.combineRecordIndex = combine;
	}
	
	public void setHref(int href){
	    this.hrefRecordIndex = href;
	}
	
	public void setType(int type){
	    this.typeRecordIndex = type;
	}
	
	public void setName(int name){    
	    this.nameRecordIndex = name;
	}
		
	public void setDefaultValue(int defaultValue){
	    this.defaultValueRecordIndex = defaultValue;
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
	
	void resetCommonAttributtes(){
	    if(xmlBaseRecordIndex != DocumentIndexedData.NO_RECORD) xmlBaseRecordIndex = DocumentIndexedData.NO_RECORD;
	    if(nsRecordIndex != DocumentIndexedData.NO_RECORD)nsRecordIndex = DocumentIndexedData.NO_RECORD;
        if(datatypeLibraryRecordIndex != DocumentIndexedData.NO_RECORD)datatypeLibraryRecordIndex = DocumentIndexedData.NO_RECORD;
        if(defaultValueRecordIndex != DocumentIndexedData.NO_RECORD) defaultValueRecordIndex = DocumentIndexedData.NO_RECORD;
	}
	
	//**************************************************************************
	//START PATTERN BUILDING ***************************************************
	//**************************************************************************
	public void buildElementWithNameClass(
                                            int recordIndex){
		
		ElementWithNameClass ewnc = new ElementWithNameClass(xmlBaseRecordIndex, 
		                                                        nsRecordIndex, 
		                                                        datatypeLibraryRecordIndex,		                                                        
		                                                        getContentParsedComponents(), 
		                                                        recordIndex,
		                                                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(ewnc);
		
	}
	public void buildElementWithNameInstance(int recordIndex){
			
		ElementWithNameInstance ewni = new ElementWithNameInstance(xmlBaseRecordIndex, 
		                                                        nsRecordIndex, 
		                                                        datatypeLibraryRecordIndex,
                                                                nameRecordIndex,		                                                        
		                                                        getContentParsedComponents(), 
		                                                        recordIndex,
		                                                        schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(ewni);
		
	}
	public void buildAttributeWithNameClass(int recordIndex){
			
		AttributeWithNameClass awnc = new AttributeWithNameClass(xmlBaseRecordIndex, 
		                                                        nsRecordIndex,
		                                                        datatypeLibraryRecordIndex,
		                                                        defaultValueRecordIndex,
		                                                        getContentParsedComponents(), 
		                                                        recordIndex,
		                                                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(awnc);
		
	}
	public void buildAttributeWithNameInstance(int recordIndex){
			
		AttributeWithNameInstance awni = new AttributeWithNameInstance(xmlBaseRecordIndex, 
		                                                        nsRecordIndex, 
		                                                        datatypeLibraryRecordIndex, 
		                                                        nameRecordIndex, 
		                                                        defaultValueRecordIndex,
		                                                        getContentParsedComponents(), 
		                                                        recordIndex,
		                                                        schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(awni);
		
	}	
	public void buildGroup(
	                                            int recordIndex){
			
		Group g = new Group(xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
                                schemaDocumentIndexedData);
        resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(g);
		
	}		
	public void buildInterleave(int recordIndex){
		
		Interleave i = new Interleave(xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            getContentParsedComponents(), 
		                            recordIndex,
		                            schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(i);
		
	}
	public void buildChoicePattern(int recordIndex){
			
		ChoicePattern cp = new ChoicePattern(xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            getContentParsedComponents(), 
		                            recordIndex,
                                    schemaDocumentIndexedData);
        resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(cp);
		
	}
	public void buildOptional(int recordIndex){
			
		Optional o = new Optional(xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(o);
		
	}
	public void buildZeroOrMore(int recordIndex){
			
		ZeroOrMore zom = new ZeroOrMore(xmlBaseRecordIndex, 
		                                    nsRecordIndex, 
		                                    datatypeLibraryRecordIndex, 
		                                    getContentParsedComponents(), 
		                                    recordIndex,
		                                    schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(zom);
		
	}
	public void buildOneOrMore(int recordIndex){
			
		OneOrMore oom = new OneOrMore(xmlBaseRecordIndex, 
		                                    nsRecordIndex, 
		                                    datatypeLibraryRecordIndex, 
		                                    getContentParsedComponents(), 
		                                    recordIndex,
		                                    schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(oom);
		
	}
	public void buildListPattern(int recordIndex){
			
		ListPattern lp = new ListPattern(xmlBaseRecordIndex, 
                                                nsRecordIndex, 
                                                datatypeLibraryRecordIndex, 
                                                getContentParsedComponents(), 
                                                recordIndex,
                                                schemaDocumentIndexedData);
        resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(lp);
		
	}
	public void buildMixed(int recordIndex){
			
		Mixed m = new Mixed(xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(m);
		
	}
	public void buildRef(int recordIndex){
			
		Ref r = new Ref(xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        nameRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(r);
		
	}
	public void buildParentRef(int recordIndex){
			
		ParentRef r = new ParentRef(xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                nameRecordIndex, 
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(r);
		
	}
	public void buildEmpty(int recordIndex){
			
		Empty e = new Empty( xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		addToCurrentLevel(e);
		
	}
	public void buildText(int recordIndex){
			
		Text t = new Text( xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		addToCurrentLevel(t);
		
	}	
	public void buildValue(   String characterContent,
	                                            int recordIndex){
		
		Value v = new Value( xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            typeRecordIndex,
		                            characterContent,
		                            recordIndex,
		                            schemaDocumentIndexedData);
		if(typeRecordIndex != DocumentIndexedData.NO_RECORD)typeRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(v);
		
	}
	public void buildData(int recordIndex){
			
		Data d = new Data( xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            typeRecordIndex, 
		                            getContentParsedComponents(), 
		                            recordIndex,
		                            schemaDocumentIndexedData);
		if(typeRecordIndex != DocumentIndexedData.NO_RECORD)typeRecordIndex = DocumentIndexedData.NO_RECORD;
        resetCommonAttributtes();		
		clearContent();
		addToCurrentLevel(d);
		
	}		
	public void buildNotAllowed(int recordIndex){
			
		NotAllowed na = new NotAllowed(xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		addToCurrentLevel(na);
		
	}			
	public void buildExternalRef(int recordIndex){
			
		ExternalRef er = new ExternalRef(xmlBaseRecordIndex, 
		                                   nsRecordIndex, 
		                                   datatypeLibraryRecordIndex, 
		                                   hrefRecordIndex, 
		                                   getContentParsedComponents(), 
		                                   recordIndex,
		                                   schemaDocumentIndexedData);
		if(hrefRecordIndex != DocumentIndexedData.NO_RECORD)hrefRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(er);
		
	}
	public void buildGrammar(int recordIndex){
			
		Grammar g = new Grammar(xmlBaseRecordIndex, 
		                                    nsRecordIndex, 
		                                    datatypeLibraryRecordIndex, 
		                                    getContentParsedComponents(), 
		                                    recordIndex,
		                                    schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(g);
		
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	//**************************************************************************
	//START NAME CLASS BUILDING ************************************************
	//**************************************************************************	
	public void buildName( String characterContent,
	                                            int recordIndex){
			
		Name n = new Name(xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex,
		                        characterContent, 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		addToCurrentLevel(n);
		
	}
	public void buildAnyName(int recordIndex){
			
		AnyName an = new AnyName(xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex,
		                            getContentParsedComponents(), 
		                            recordIndex,
                                    schemaDocumentIndexedData);
        resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(an);
		
	}
	public void buildNsName(int recordIndex){
			
		NsName nn = new NsName(xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex,
		                                getContentParsedComponents(), 
		                                recordIndex,
                                        schemaDocumentIndexedData);
        resetCommonAttributtes();		
		clearContent();
		addToCurrentLevel(nn);
		
	}
	public void buildChoiceNameClass(int recordIndex){
			
		ChoiceNameClass cnc = new ChoiceNameClass(xmlBaseRecordIndex, 
		                                                nsRecordIndex, 
		                                                datatypeLibraryRecordIndex,
		                                                getContentParsedComponents(), 
		                                                recordIndex,
		                                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(cnc);
		
	}	
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START DEFINITION BUILDING ************************************************
	//**************************************************************************	
	public void buildDefine(int recordIndex){
			
		Define d = new Define(xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            nameRecordIndex, 
		                            combineRecordIndex,
		                            getContentParsedComponents(), 
		                            recordIndex,
		                            schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		if(combineRecordIndex != DocumentIndexedData.NO_RECORD)combineRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(d);
		
	}
	public void buildStart(int recordIndex){
		
		Start s = new Start(xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            combineRecordIndex, 
		                            getContentParsedComponents(),
		                            recordIndex,
		                            schemaDocumentIndexedData);
		if(combineRecordIndex != DocumentIndexedData.NO_RECORD)combineRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(s);
		
	}
	//**************************************************************************
	//END DEFINITION BUILDING **************************************************
	//**************************************************************************

	public void buildDivIncludeContent(int recordIndex){
			
		DivIncludeContent dic = new DivIncludeContent(xmlBaseRecordIndex, 
		                                                    nsRecordIndex, 
		                                                    datatypeLibraryRecordIndex, 
		                                                    getContentParsedComponents(), 
		                                                    recordIndex,
		                                                    schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(dic);
		
	}
	public void buildDivGrammarContent(int recordIndex){
			
		DivGrammarContent dgc = new DivGrammarContent(xmlBaseRecordIndex, 
		                                                        nsRecordIndex, 
		                                                        datatypeLibraryRecordIndex,
		                                                        getContentParsedComponents(), 
		                                                        recordIndex,
		                                                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(dgc);
		
	}

	public void buildParam( String characterContent,
                                                int recordIndex){
			
		Param p = new Param(xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            nameRecordIndex,
		                            characterContent, 
		                            recordIndex,
		                            schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(p);
		
	}

	public void buildExceptNameClass(int recordIndex){
			
		ExceptNameClass enc = new ExceptNameClass(xmlBaseRecordIndex, 
		                                                nsRecordIndex, 
		                                                datatypeLibraryRecordIndex,
		                                                getContentParsedComponents(), 
		                                                recordIndex,
		                                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(enc);
		
	}
	public void buildExceptPattern(int recordIndex){
			
		ExceptPattern ep = new ExceptPattern(xmlBaseRecordIndex, 
		                                            nsRecordIndex, 
		                                            datatypeLibraryRecordIndex, 
		                                            getContentParsedComponents(), 
		                                            recordIndex,
		                                            schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(ep);
		
	}
	
	public void buildInclude( int recordIndex){
			
		Include i = new Include( xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                hrefRecordIndex,
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		if(hrefRecordIndex != DocumentIndexedData.NO_RECORD)hrefRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(i);
		
	}
	
	public void buildDummy(int recordIndex){
			
		Dummy dd = new Dummy(xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(dd);
		
	}
    
    public void buildForeignComponent(int recordIndex){
			
		ForeignComponent fc = new ForeignComponent(xmlBaseRecordIndex, 
		                                                getContentParsedComponents(), 
		                                                recordIndex,
		                                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(fc);
		
	}
}
