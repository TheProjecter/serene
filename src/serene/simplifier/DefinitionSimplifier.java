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

package serene.simplifier;

import java.net.URI;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Set;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.relaxng.datatype.DatatypeLibrary;

import serene.util.ObjectIntHashMap;
import serene.util.IntList;
import serene.util.IntStack;
import serene.util.BooleanList;
import serene.bind.util.DocumentIndexedData;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.parsed.ParsedComponent;

import serene.validation.schema.parsed.Pattern;
import serene.validation.schema.parsed.Definition;
import serene.validation.schema.parsed.Grammar;
import serene.validation.schema.parsed.ExternalRef;
import serene.validation.schema.parsed.Define;
import serene.validation.schema.parsed.Start;

import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.ReferenceModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.SPattern;

import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SPattern;

import serene.internal.InternalRNGFactory;

import serene.validation.handlers.error.ErrorDispatcher;

import sereneWrite.ParsedComponentWriter;

import serene.validation.jaxp.SchematronParser;

import serene.Reusable;

class DefinitionSimplifier extends Simplifier implements Reusable{	
	
	String combine;
	IntList nullCombine;
	IntList otherCombine;
	
	DefinitionSimplifier(DefinitionSimplifierPool pool, ErrorDispatcher errorDispatcher, SchematronParser schematronParser){
		super(errorDispatcher, schematronParser);
		this.pool = pool;
		
		nullCombine = new IntList();		
		//pcw = new ParsedComponentWriter();
        
        currentDefinitionTopPatterns = new ArrayList<SPattern>();
	}
	
	public void setReplaceMissingDatatypeLibrary(boolean value){
		throw new IllegalStateException();
	}
	
	void init(Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions,	
			Map<ExternalRef, URI> externalRefs,
			Map<URI, ParsedModel> docParsedModels,
			ArrayList<SPattern> definitionTopPatterns,
			NamespaceInheritanceHandler namespaceInheritanceHandler,	
			Map<ParsedComponent, String> componentAsciiDL,
			Map<String, DatatypeLibrary> asciiDlDatatypeLibrary,
			ObjectIntHashMap indexes,
			IntStack referencePath,
			BooleanList definitionEmptyChild,
			BooleanList definitionNotAllowedChild,
			ReferenceModel referenceModel, 
            RecursionModel recursionModel,
			Grammar currentGrammar,
			Stack<Grammar> previousGrammars,
            DocumentSimplificationContext simplificationContext){
		this.grammarDefinitions = grammarDefinitions;	
		this.externalRefs = externalRefs;
		this.docParsedModels = docParsedModels;	
		
		this.definitionTopPatterns = definitionTopPatterns;
		
		this.namespaceInheritanceHandler = namespaceInheritanceHandler;		
		this.componentAsciiDL = componentAsciiDL;	
		this.asciiDlDatatypeLibrary = asciiDlDatatypeLibrary;
		
		this.indexes = indexes;	
		this.referencePath = referencePath;
		this.definitionEmptyChild = definitionEmptyChild;
		this.definitionNotAllowedChild = definitionNotAllowedChild;
		this.referenceModel = referenceModel;
        this.recursionModel = recursionModel;
        
		this.currentGrammar = currentGrammar;
		this.previousGrammars = previousGrammars;
        
        this.simplificationContext = simplificationContext;      
	}
	
	public void recycle(){
		grammarDefinitions = null;	
		externalRefs = null;
		docParsedModels = null;	
		namespaceInheritanceHandler = null;	
		
		componentAsciiDL = null;	
		asciiDlDatatypeLibrary = null;
		
		currentGrammar = null;
		previousGrammars = null;	
		
		indexes = null;	
		recursionModel = null;
		
		pool.recycle(this);
	}
	    
	void simplify(ArrayList<Definition> definitions,
                ArrayList<SElement> selements,
                ArrayList<SAttribute> sattributes,
                ArrayList<SExceptPattern> sexceptPatterns) throws SAXException{
    
        this.selements = selements;	
	    this.sattributes = sattributes;
	    this.sexceptPatterns = sexceptPatterns;
    
		emptyChild = false;
        emptyComponent = null;
		notAllowedChild = false;
        patternChild = false;
		notAllowedElement = false;
        
		anyNameContext = false;
		anyNameExceptContext = false;
		nsNameContext = false;
		nsNameExceptContext = false;
		attributeContext = false;
                      
		combine = null;
		nullCombine.clear();        
		if(otherCombine != null) otherCombine.clear();
		
        paramStack.clear();
         
		builder.startBuild();		
        
        currentDefinitionTopPatterns.clear();		
        //String location = "";
        IntList allRecordIndexes = new IntList(); 
        ArrayList<DocumentIndexedData> allDocumentIndexedData = new ArrayList<DocumentIndexedData>();
		for(int i = 0; i < definitions.size(); i++){
			Definition d = definitions.get(i);
			String c = d.getCombine();
			
			if(c == null) nullCombine.add(i);
            else{ 
                c = c.trim();            
                if(c.equals("choice") || c.equals("interleave")){
                    if(combine == null)combine = c;
                    else if(!combine.equals(c)){
                        if(otherCombine == null) otherCombine = new IntList();
                        otherCombine.add(i);
                    }
                }
            }
            allRecordIndexes.add(d.getRecordIndex()); 
            allDocumentIndexedData.add(d.getDocumentIndexedData());
            
            d.accept(this);
			
		}
		if(nullCombine.size() > 1){
			// error 4.17
			String message = "Simplification 4.17 error.";
			Definition d = definitions.get(0);			
			if(d instanceof Define){
				message += " Several define elements with the same name attribute and without combine attribute: ";
			}else{
				message += " Several start elements without combine attribute: ";
			}
			for(int i = 0; i < nullCombine.size(); i++){
				d = definitions.get(nullCombine.get(i));
				message += "\n<"+d.getQName()+"> at "+d.getLocation(restrictToFileName);
			}
			message += ".";
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(otherCombine != null && !otherCombine.isEmpty()){
			// error 4.17
			String message = "Simplification 4.17 error.";
			Definition d = definitions.get(0);			
			if(d instanceof Define){
				message += " define elements with the same name attribute and different combine attributes: ";
			}else{
				message += " start elements with different combine attribute: ";
			}			
			String oCombine = combine.equals("choice") ? "interleave" : "choice";
			String oDefinitions = "\n\""+oCombine+"\"";
			message += "\n\""+combine+"\"";
			for(int i = 0; i < definitions.size(); i++){
				d = definitions.get(i);
				if(otherCombine.contains(i)){
					oDefinitions += "\n<"+d.getQName()+"> at "+d.getLocation(restrictToFileName);
				}else{
					message += "\n<"+d.getQName()+"> at "+d.getLocation(restrictToFileName);
				}
			}
			message += oDefinitions+".";
			errorDispatcher.error(new SAXParseException(message, null));
			//System.out.println(message);			
		}
		
		// the current level is that of the definitions,
		// they were not built, so combine can be handled directly
		if(combine != null){
			if(combine.equals("choice")){
				builder.buildChoicePattern(currentDefinitionTopPatterns.toArray(new SPattern[currentDefinitionTopPatterns.size()]),
				            allRecordIndexes, 
                            allDocumentIndexedData, 
                            true);
			}else if(combine.equals("interleave")){
				builder.buildInterleave(currentDefinitionTopPatterns.toArray(new SPattern[currentDefinitionTopPatterns.size()]),
				            allRecordIndexes, 
                            allDocumentIndexedData, 
                            true);
			}
		}else{
			SPattern[] p = currentDefinitionTopPatterns.toArray(new SPattern[currentDefinitionTopPatterns.size()]);
			builder.clearContent();
			builder.addAllToCurrentLevel(p);
		}		
	}
	
	SPattern getCurrentPattern(){
		return builder.getCurrentPattern();
	}
	
	SPattern[] getAllCurrentPatterns(){
		return builder.getAllCurrentPatterns();
	}
	
	boolean getEmptyChild(){
		return emptyChild;
	}
	
	boolean getNotAllowedChild(){
		return notAllowedChild;
	}    
}