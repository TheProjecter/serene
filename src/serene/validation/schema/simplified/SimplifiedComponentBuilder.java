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

import serene.bind.util.DocumentIndexedData;
import serene.util.IntList;

/**
* Builds components, the big picture SimplifiedModel is the directors responsibility. 
* Can return/remove the components of the current level(usefull for attaching commands).
* Can check if the current level is top level.
*/
public class SimplifiedComponentBuilder implements ComponentBuilder{
	
	Level level;
	//for debug only
	Level topLevel;
	
	public SimplifiedComponentBuilder(){
		level = Level.getTopInstance();
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
	    if(p == null) return;
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
	public SElement buildElement(int definitionIndex, int recordIndex, DocumentIndexedData documentIndexedData, RecursionModel recursionModel){
		SElement e = new SElement(definitionIndex, getLastContentNameClass(), getLastContentPattern(), recordIndex, documentIndexedData, recursionModel);
		clearContent();
		addToCurrentLevel(e);
		return e;
	}
	public SAttribute buildAttribute(int definitionIndex, 
	                            int defaultValue,
                                int recordIndex, 
                                DocumentIndexedData documentIndexedData){
		SAttribute a = new SAttribute(definitionIndex, 
		                        getLastContentNameClass(), 
		                        getContentPatterns(),
		                        defaultValue,
                                recordIndex, 
                                documentIndexedData);
		clearContent();
		addToCurrentLevel(a);
		return a;
	}
	public SGroup buildGroup(int recordIndex, 
                        DocumentIndexedData documentIndexedData, 
                        boolean addedBySimplification){
		SGroup g = new SGroup(getContentPatterns(),
                        recordIndex, 
                        documentIndexedData, 
                        addedBySimplification);
		clearContent();
		addToCurrentLevel(g);
		return g;
	}
    public SGroup buildReplacementGroup(int recordIndex, 
                        DocumentIndexedData documentIndexedData, 
                        boolean addedBySimplification){
        SPattern[] children = getAllCurrentPatterns();
		SGroup g = new SGroup(children, 
                            recordIndex, 
                            documentIndexedData, 
                            addedBySimplification);
		clearCurrentPatterns();
		addToCurrentLevel(g);
		return g;
	}
	public SInterleave buildInterleave(int recordIndex, 
                        DocumentIndexedData documentIndexedData){
		SInterleave i = new SInterleave(getContentPatterns(),
                                        recordIndex, 
                                        documentIndexedData);
		clearContent();
		addToCurrentLevel(i);
		return i;
	}
    public SInterleave buildInterleave(SPattern[] children,
                                IntList allRecordIndexes, 
                                ArrayList<DocumentIndexedData> allDocumentIndexedData, 
                                boolean addedBySimplification){
        SPattern[] c = null;
        if(children != null){
            int l = children.length;
            c = new SPattern[children.length];
            for(int i = 0; i < l; i++){
                if(children[i] != null) c[i] = (SPattern)children[i];
            }
        }
		SInterleave i = new SInterleave(c, 
                                        allRecordIndexes, 
                                        allDocumentIndexedData, 
                                        addedBySimplification);
		addToCurrentLevel(i);
		return i;
	}
	public SChoicePattern buildChoicePattern(int recordIndex, 
                        DocumentIndexedData documentIndexedData, 
                        boolean addedBySimplification){
		SChoicePattern cp = new SChoicePattern(getContentPatterns(),
                                            recordIndex, 
                                            documentIndexedData, 
                                            addedBySimplification);
		clearContent();
		addToCurrentLevel(cp);
		return cp;
	}
    public SChoicePattern buildChoicePattern(SPattern[] children,
                                        IntList allRecordIndexes, 
                                        ArrayList<DocumentIndexedData> allDocumentIndexedData, 
                                        boolean addedBySimplification){
        SPattern[] c = null;
        if(children != null){
            int l = children.length;
            c = new SPattern[children.length];
            for(int i = 0; i < l; i++){
                if(children[i] != null) c[i] = (SPattern)children[i];
            }
        }
		SChoicePattern cp = new SChoicePattern(c, 
                                            allRecordIndexes, 
                                            allDocumentIndexedData, 
                                            addedBySimplification);		
		addToCurrentLevel(cp);
		return cp;
	}
    public SChoicePattern buildReplacementChoicePattern(int recordIndex, 
                                            DocumentIndexedData documentIndexedData, 
                                            boolean addedBySimplification){
        SPattern[] children = getAllCurrentPatterns();
		SChoicePattern c = new SChoicePattern(children, 
		                                    recordIndex, 
                                            documentIndexedData, 
                                            addedBySimplification);
		clearCurrentPatterns();
		addToCurrentLevel(c);
		return c;
	}
	public SListPattern buildListPattern(int recordIndex, 
                        DocumentIndexedData documentIndexedData){
		SListPattern lp = new SListPattern(getLastContentPattern(),
                                        recordIndex, 
                                        documentIndexedData);
		clearContent();
		addToCurrentLevel(lp);
		return lp;
	}
	public SRef buildRef(int definitionIndex, ReferenceModel referenceModel, int recordIndex, DocumentIndexedData documentIndexedData){
		SRef r = new SRef(definitionIndex, referenceModel, recordIndex, documentIndexedData);
		addToCurrentLevel(r);
		return r;
	}
	public SRef buildRef(SPattern child, int definitionIndex, ReferenceModel referenceModel, int recordIndex, DocumentIndexedData documentIndexedData){
		SRef r = new SRef(child, definitionIndex, referenceModel, recordIndex, documentIndexedData);
		addToCurrentLevel(r);
		return r;
	}
	public SEmpty buildEmpty(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
		SEmpty e = new SEmpty(recordIndex, documentIndexedData, addedBySimplification);
		addToCurrentLevel(e);
		return e;
	}
	public SText buildText(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
		SText t = new SText(recordIndex, documentIndexedData, addedBySimplification);
		addToCurrentLevel(t);
		return t;
	}
	public SValue buildValue(String ns, Datatype datatype, String charContent, int recordIndex, DocumentIndexedData documentIndexedData){
		SValue v = new SValue(ns, datatype, charContent, recordIndex, documentIndexedData);
		addToCurrentLevel(v);
		return v;
	}
	public SData buildData(Datatype datatype, int recordIndex, DocumentIndexedData documentIndexedData){
		SData d = new SData(datatype, getContentExceptPatterns(), recordIndex, documentIndexedData);
		clearContent();
		addToCurrentLevel(d);
		return d;
	}		
	public SNotAllowed buildNotAllowed(int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
		SNotAllowed na = new SNotAllowed(recordIndex, documentIndexedData, addedBySimplification);
		addToCurrentLevel(na);
		return na;
	}	
	public SGrammar buildGrammar(int recordIndex, 
                        DocumentIndexedData documentIndexedData){
		SGrammar g = new SGrammar(getLastContentPattern(),
                                recordIndex, 
                                documentIndexedData);
		clearContent();		
		addToCurrentLevel(g);
		return g;
	}
	//**************************************************************************
	//END PATTERN BUILDING *****************************************************
	//**************************************************************************

	
	//**************************************************************************
	//START NAME CLASS BUILDING ************************************************
	//**************************************************************************
	public SName buildName(String ns, String localPart, int recordIndex, DocumentIndexedData documentIndexedData, boolean addedBySimplification){
	    if(localPart == null) throw new IllegalStateException(); 
		SName n = new SName(ns, localPart, recordIndex, documentIndexedData, addedBySimplification);
		addToCurrentLevel(n);
		return n;
	}
	public SAnyName buildAnyName(int recordIndex, DocumentIndexedData documentIndexedData){
		SAnyName an = new SAnyName(getContentExceptNameClass(), recordIndex, documentIndexedData);
		clearContent();
		addToCurrentLevel(an);
		return an;
	}
	public SNsName buildNsName(String ns, int recordIndex, DocumentIndexedData documentIndexedData){
		SNsName nn = new SNsName(ns, getContentExceptNameClass(), recordIndex, documentIndexedData);
		clearContent();
		addToCurrentLevel(nn);
		return nn;
	}
	public SChoiceNameClass buildChoiceNameClass(int recordIndex, DocumentIndexedData documentIndexedData){
		SChoiceNameClass cnc = new SChoiceNameClass(getContentNameClasses(), recordIndex, documentIndexedData);
		clearContent();
		addToCurrentLevel(cnc);
		return cnc;
	}	
    public SChoiceNameClass buildReplacementChoiceNameClass(int recordIndex, DocumentIndexedData documentIndexedData){
        SNameClass[] children = getAllCurrentNameClasses();
		SChoiceNameClass c = new SChoiceNameClass(children, recordIndex, documentIndexedData);
		clearCurrentNameClasses();
		addToCurrentLevel(c);
		return c;
	}
	//**************************************************************************
	//END NAME CLASS BUILDING **************************************************
	//**************************************************************************	
	public SExceptNameClass buildExceptNameClass(int recordIndex, DocumentIndexedData documentIndexedData){
		SExceptNameClass enc = new SExceptNameClass(getLastContentNameClass(), recordIndex, documentIndexedData);
		clearContent();
		addToCurrentLevel(enc);
		return enc;
	}
	public SExceptPattern buildExceptPattern(int definitionIndex, int recordIndex, DocumentIndexedData documentIndexedData){
		SExceptPattern ep = new SExceptPattern(definitionIndex, getLastContentPattern(), recordIndex, documentIndexedData);
		clearContent();
		addToCurrentLevel(ep);
		return ep;
	}
	
	
	public SDummy buildDummy(int recordIndex, DocumentIndexedData documentIndexedData){
		SDummy d = new SDummy(getContentPatterns(), recordIndex, documentIndexedData);
		clearContent();
		addToCurrentLevel(d);
		return d;
	}	


    public void oneOrMore(int recordIndex, DocumentIndexedData documentIndexedData){
        SPattern p = getLastContentPattern();
        p.setOneOrMore(recordIndex, documentIndexedData);
        clearContent();
        addToCurrentLevel(p);
    }
    public void zeroOrMore(int recordIndex, DocumentIndexedData documentIndexedData){
        SPattern p = getLastContentPattern();
        p.setZeroOrMore(recordIndex, documentIndexedData);
        clearContent();
        addToCurrentLevel(p);
    }	
    public void optional(int recordIndex, DocumentIndexedData documentIndexedData){
        SPattern p = getLastContentPattern();
        p.setOptional(recordIndex, documentIndexedData);
        clearContent();
        addToCurrentLevel(p);
    }	    
}  
