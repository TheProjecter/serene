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
	public void buildElementWithNameClass(/*Map<String, String> declaredXmlns, */
                                            int recordIndex){
		/*/*writeLevels();*/
		ElementWithNameClass ewnc = new ElementWithNameClass(/*declaredXmlns,*/ 
		                                                        xmlBaseRecordIndex, 
		                                                        nsRecordIndex, 
		                                                        datatypeLibraryRecordIndex,		                                                        
		                                                        getContentParsedComponents(), 
		                                                        recordIndex,
		                                                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(ewnc);
		/*writeLevels();*/
	}
	public void buildElementWithNameInstance(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ElementWithNameInstance ewni = new ElementWithNameInstance(/*declaredXmlns, */
		                                                        xmlBaseRecordIndex, 
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
		/*writeLevels();*/
	}
	public void buildAttributeWithNameClass(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		AttributeWithNameClass awnc = new AttributeWithNameClass(/*declaredXmlns, */
		                                                        xmlBaseRecordIndex, 
		                                                        nsRecordIndex,
		                                                        datatypeLibraryRecordIndex,
		                                                        defaultValueRecordIndex,
		                                                        getContentParsedComponents(), 
		                                                        recordIndex,
		                                                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(awnc);
		/*writeLevels();*/
	}
	public void buildAttributeWithNameInstance(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		AttributeWithNameInstance awni = new AttributeWithNameInstance(/*declaredXmlns,*/ 
		                                                        xmlBaseRecordIndex, 
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
		/*writeLevels();*/
	}	
	public void buildGroup(/*Map<String, String> declaredXmlns, */
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Group g = new Group(/*declaredXmlns,*/ 
		                        xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
                                schemaDocumentIndexedData);
        resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(g);
		/*/*writeLevels();*/
	}		
	public void buildInterleave(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*writeLevels();*/
		Interleave i = new Interleave(/*declaredXmlns,*/
		                            xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            getContentParsedComponents(), 
		                            recordIndex,
		                            schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(i);
		/*/*writeLevels();*/
	}
	public void buildChoicePattern(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ChoicePattern cp = new ChoicePattern(/*declaredXmlns, */
		                            xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            getContentParsedComponents(), 
		                            recordIndex,
                                    schemaDocumentIndexedData);
        resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(cp);
		/*writeLevels();*/
	}
	public void buildOptional(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Optional o = new Optional(/*declaredXmlns, */
		                                xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(o);
		/*/*writeLevels();*/
	}
	public void buildZeroOrMore(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ZeroOrMore zom = new ZeroOrMore(/*declaredXmlns, */
		                                    xmlBaseRecordIndex, 
		                                    nsRecordIndex, 
		                                    datatypeLibraryRecordIndex, 
		                                    getContentParsedComponents(), 
		                                    recordIndex,
		                                    schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(zom);
		/*writeLevels();*/
	}
	public void buildOneOrMore(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		OneOrMore oom = new OneOrMore(/*declaredXmlns, */
		                                    xmlBaseRecordIndex, 
		                                    nsRecordIndex, 
		                                    datatypeLibraryRecordIndex, 
		                                    getContentParsedComponents(), 
		                                    recordIndex,
		                                    schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(oom);
		/*writeLevels();*/
	}
	public void buildListPattern(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ListPattern lp = new ListPattern(/*declaredXmlns, */
                                                xmlBaseRecordIndex, 
                                                nsRecordIndex, 
                                                datatypeLibraryRecordIndex, 
                                                getContentParsedComponents(), 
                                                recordIndex,
                                                schemaDocumentIndexedData);
        resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(lp);
		/*writeLevels();*/
	}
	public void buildMixed(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Mixed m = new Mixed(/*declaredXmlns, */
		                        xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(m);
		/*writeLevels();*/
	}
	public void buildRef(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Ref r = new Ref(/*declaredXmlns, */
		                        xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        nameRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(r);
		/*writeLevels();*/
	}
	public void buildParentRef(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ParentRef r = new ParentRef(/*declaredXmlns, */
		                                xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                nameRecordIndex, 
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(r);
		/*writeLevels();*/
	}
	public void buildEmpty(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Empty e = new Empty(/*declaredXmlns, */
		                                xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		addToCurrentLevel(e);
		/*writeLevels();*/
	}
	public void buildText(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Text t = new Text(/*declaredXmlns, */
		                        xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		addToCurrentLevel(t);
		/*writeLevels();*/
	}	
	public void buildValue(/*Map<String, String> declaredXmlns,*/ 
	                                            String characterContent,
	                                            int recordIndex){
		/*/*writeLevels();*/
		Value v = new Value(/*declaredXmlns, */
		                            xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            typeRecordIndex,
		                            characterContent,
		                            recordIndex,
		                            schemaDocumentIndexedData);
		if(typeRecordIndex != DocumentIndexedData.NO_RECORD)typeRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(v);
		/*writeLevels();*/
	}
	public void buildData(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Data d = new Data(/*declaredXmlns, */
		                            xmlBaseRecordIndex, 
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
		/*writeLevels();*/
	}		
	public void buildNotAllowed(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		NotAllowed na = new NotAllowed(/*declaredXmlns, */
		                                xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex, 
		                                getContentParsedComponents(), 
		                                recordIndex,
		                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		addToCurrentLevel(na);
		/*writeLevels();*/
	}			
	public void buildExternalRef(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ExternalRef er = new ExternalRef(/*declaredXmlns, */
		                                   xmlBaseRecordIndex, 
		                                   nsRecordIndex, 
		                                   datatypeLibraryRecordIndex, 
		                                   hrefRecordIndex, 
		                                   getContentParsedComponents(), 
		                                   recordIndex,
		                                   schemaDocumentIndexedData);
		if(hrefRecordIndex != DocumentIndexedData.NO_RECORD)hrefRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(er);
		/*writeLevels();*/
	}
	public void buildGrammar(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Grammar g = new Grammar(/*declaredXmlns, */
		                                    xmlBaseRecordIndex, 
		                                    nsRecordIndex, 
		                                    datatypeLibraryRecordIndex, 
		                                    getContentParsedComponents(), 
		                                    recordIndex,
		                                    schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(g);
		/*writeLevels();*/
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	//**************************************************************************
	//START NAME CLASS BUILDING ************************************************
	//**************************************************************************	
	public void buildName(/*Map<String, String> declaredXmlns,*/ 
	                                            String characterContent,
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Name n = new Name(/*declaredXmlns, */
		                        xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex,
		                        characterContent, 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		addToCurrentLevel(n);
		/*writeLevels();*/
	}
	public void buildAnyName(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		AnyName an = new AnyName(/*declaredXmlns, */
		                            xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex,
		                            getContentParsedComponents(), 
		                            recordIndex,
                                    schemaDocumentIndexedData);
        resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(an);
		/*writeLevels();*/
	}
	public void buildNsName(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		NsName nn = new NsName(/*declaredXmlns, */
		                                xmlBaseRecordIndex, 
		                                nsRecordIndex, 
		                                datatypeLibraryRecordIndex,
		                                getContentParsedComponents(), 
		                                recordIndex,
                                        schemaDocumentIndexedData);
        resetCommonAttributtes();		
		clearContent();
		addToCurrentLevel(nn);
		/*writeLevels();*/
	}
	public void buildChoiceNameClass(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ChoiceNameClass cnc = new ChoiceNameClass(/*declaredXmlns, */
		                                                xmlBaseRecordIndex, 
		                                                nsRecordIndex, 
		                                                datatypeLibraryRecordIndex,
		                                                getContentParsedComponents(), 
		                                                recordIndex,
		                                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(cnc);
		/*writeLevels();*/
	}	
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START DEFINITION BUILDING ************************************************
	//**************************************************************************	
	public void buildDefine(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Define d = new Define(/*declaredXmlns, */
		                            xmlBaseRecordIndex, 
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
		/*writeLevels();*/
	}
	public void buildStart(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/
		Start s = new Start(/*declaredXmlns, */
		                            xmlBaseRecordIndex, 
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
		/*writeLevels();*/
	}
	//**************************************************************************
	//END DEFINITION BUILDING **************************************************
	//**************************************************************************

	public void buildDivIncludeContent(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		DivIncludeContent dic = new DivIncludeContent(/*declaredXmlns, */
		                                                    xmlBaseRecordIndex, 
		                                                    nsRecordIndex, 
		                                                    datatypeLibraryRecordIndex, 
		                                                    getContentParsedComponents(), 
		                                                    recordIndex,
		                                                    schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(dic);
		/*writeLevels();*/
	}
	public void buildDivGrammarContent(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		DivGrammarContent dgc = new DivGrammarContent(/*declaredXmlns, */
		                                                        xmlBaseRecordIndex, 
		                                                        nsRecordIndex, 
		                                                        datatypeLibraryRecordIndex,
		                                                        getContentParsedComponents(), 
		                                                        recordIndex,
		                                                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(dgc);
		/*writeLevels();*/
	}

	public void buildParam(/*Map<String, String> declaredXmlns,*/ 
	                                            String characterContent,
                                                int recordIndex){
		/*/*writeLevels();*/	
		Param p = new Param(/*declaredXmlns,*/ 
		                            xmlBaseRecordIndex, 
		                            nsRecordIndex, 
		                            datatypeLibraryRecordIndex, 
		                            nameRecordIndex,
		                            characterContent, 
		                            recordIndex,
		                            schemaDocumentIndexedData);
		if(nameRecordIndex != DocumentIndexedData.NO_RECORD)nameRecordIndex = DocumentIndexedData.NO_RECORD;
		resetCommonAttributtes();
		addToCurrentLevel(p);
		/*writeLevels();*/
	}

	public void buildExceptNameClass(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ExceptNameClass enc = new ExceptNameClass(/*declaredXmlns, */
		                                                xmlBaseRecordIndex, 
		                                                nsRecordIndex, 
		                                                datatypeLibraryRecordIndex,
		                                                getContentParsedComponents(), 
		                                                recordIndex,
		                                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(enc);
		/*writeLevels();*/
	}
	public void buildExceptPattern(/*Map<String, String> declaredXmlns,*/     
	                                            int recordIndex){
		/*/*writeLevels();*/	
		ExceptPattern ep = new ExceptPattern(/*declaredXmlns, */
		                                            xmlBaseRecordIndex, 
		                                            nsRecordIndex, 
		                                            datatypeLibraryRecordIndex, 
		                                            getContentParsedComponents(), 
		                                            recordIndex,
		                                            schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(ep);
		/*writeLevels();*/
	}
	
	public void buildInclude(/*Map<String, String> declaredXmlns,*/ 
                                                int recordIndex){
		/*/*writeLevels();*/	
		Include i = new Include(/*declaredXmlns, */
		                                xmlBaseRecordIndex, 
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
		/*writeLevels();*/
	}
	
	public void buildDummy(/*Map<String, String> declaredXmlns,*/ 
	                                            int recordIndex){
		/*/*writeLevels();*/	
		Dummy dd = new Dummy(/*declaredXmlns, */
		                        xmlBaseRecordIndex, 
		                        nsRecordIndex, 
		                        datatypeLibraryRecordIndex, 
		                        getContentParsedComponents(), 
		                        recordIndex,
		                        schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(dd);
		/*writeLevels();*/
	}
    
    public void buildForeignComponent(/*Map<String, String> declaredXmlns,*/ 
                                                int recordIndex){
		/*/*writeLevels();*/	
		ForeignComponent fc = new ForeignComponent(/*declaredXmlns, */
		                                                xmlBaseRecordIndex, 
		                                                getContentParsedComponents(), 
		                                                recordIndex,
		                                                schemaDocumentIndexedData);
		resetCommonAttributtes();
		clearContent();
		addToCurrentLevel(fc);
		/*writeLevels();*/
	}
}
