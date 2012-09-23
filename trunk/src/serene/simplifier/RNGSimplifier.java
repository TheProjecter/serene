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
import java.util.List;
import java.util.Stack;

import javax.xml.XMLConstants;

import javax.xml.transform.Templates;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TemplatesHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.relaxng.datatype.DatatypeLibrary;

import serene.util.ObjectIntHashMap;
import serene.util.BooleanList;
import serene.util.IntStack;

import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.parsed.Pattern;
import serene.validation.schema.parsed.Definition;
import serene.validation.schema.parsed.Grammar;
import serene.validation.schema.parsed.ExternalRef;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.ReferenceModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.SPattern;

import serene.validation.schema.simplified.SRef;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SPattern;

import serene.internal.InternalRNGFactory;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.validation.jaxp.SchematronParser;

import sereneWrite.ParsedComponentWriter;

public class RNGSimplifier extends Simplifier{	
	Pattern topPattern;
				
	Mapper mapper;
	Stack<URI> inclusionPath;
	
    
	public RNGSimplifier(ErrorDispatcher errorDispatcher, SchematronParser schematronParser){
		super(errorDispatcher, schematronParser);
				
		grammarDefinitions = new HashMap<Grammar, Map<String, ArrayList<Definition>>>();		
		externalRefs = new HashMap<ExternalRef, URI>();			
		docParsedModels = new HashMap<URI, ParsedModel>();
		namespaceInheritanceHandler = new NamespaceInheritanceHandler();
		pool = new DefinitionSimplifierPool(errorDispatcher, schematronParser);
		
		componentAsciiDL = new HashMap<ParsedComponent, String>();
		asciiDlDatatypeLibrary = new HashMap<String, DatatypeLibrary>();
			
		
		indexes = new ObjectIntHashMap();	
		indexes.setNullValue(-1);
				
		previousGrammars = new Stack<Grammar>();
		
		definitionTopPatterns = new ArrayList<SPattern>();
		referencePath = new IntStack();		
		definitionEmptyChild = new BooleanList();
		definitionNotAllowedChild = new BooleanList();
		
		//pcw = new ParsedComponentWriter();		
		
		inclusionPath = new Stack<URI>();		
        
        simplificationContext = new DocumentSimplificationContext();
        
        mapper = new Mapper(errorDispatcher, namespaceInheritanceHandler, schematronParser);
	}
	
	public void setRestrictToFileName(boolean restrictToFileName){
        this.restrictToFileName = restrictToFileName;
        mapper.setRestrictToFileName(restrictToFileName);
    }    
    public void setProcessEmbededSchematron(boolean processEmbededSchematron){
        this.processEmbededSchematron = processEmbededSchematron;
        mapper.setProcessEmbededSchematron(processEmbededSchematron);
    }
    
	public void setParserComponents(XMLReader xmlReader, InternalRNGFactory internalRNGFactory){
	    mapper.setParserComponents(xmlReader, internalRNGFactory);
	}
	
	/*public void setSchematronParserComponents(TransformerHandler schematronStartTransformerHandler,
	                                            SAXResult expandedSchematronResult,
	                                            TransformerHandler schematronCompilerXSLT1,
	                                            TransformerHandler schematronCompilerXSLT2,
	                                            TemplatesHandler schematronTemplatesHandler){
	    mapper.setSchematronParserComponents(schematronStartTransformerHandler,
	                expandedSchematronResult,
	                schematronCompilerXSLT1,
	                schematronCompilerXSLT2,
	                schematronTemplatesHandler);
	}*/
	public void setReplaceMissingDatatypeLibrary(boolean value){
		replaceMissingDatatypeLibrary =  value;
		mapper.setReplaceMissingDatatypeLibrary(value);
	}
    
    
	public SimplifiedModel simplify(URI base, ParsedModel parsedModel)  throws SAXException{
        if(parsedModel == null) return null;
        
        topPattern = parsedModel.getTopPattern();        
		if(topPattern == null) return null;
		
		inclusionPath.push(base);
		docParsedModels.put(base, parsedModel);
				
		grammarDefinitions.clear();
		externalRefs.clear();
		docParsedModels.clear();
		inclusionPath.clear();
		
        componentAsciiDL.clear();
		asciiDlDatatypeLibrary.clear();
					
        simplificationContext.reset();
		
		mapper.map(base,
					topPattern,
					grammarDefinitions,
					externalRefs,
					docParsedModels,
					inclusionPath,
					componentAsciiDL,
					asciiDlDatatypeLibrary,
                    simplificationContext);
		
		//System.out.println("grammarDefinitions "+grammarDefinitions);
		//System.out.println("externalRefs "+externalRefs);
		//System.out.println("docParsedModels "+docParsedModels);
		//System.out.println("**************************************");
		        
		simplify();
		SPattern[] simplifiedTopPattern = builder.getAllCurrentPatterns();
		if(simplifiedTopPattern == null){
            if(emptyChild){
                // for the 7.1.5 restrictions on start
                // TODO make sure it is correct to treat notAllowed like this
                builder.buildEmpty(topPattern.getRecordIndex(), topPattern.getDocumentIndexedData(), true);
                simplifiedTopPattern = builder.getAllCurrentPatterns();
            }else if(notAllowedElement || notAllowedChild){
                builder.buildNotAllowed(topPattern.getRecordIndex(), topPattern.getDocumentIndexedData(), true);
                simplifiedTopPattern = builder.getAllCurrentPatterns();
            }
            
		}
        SElement startElement;        
        if(simplifiedTopPattern != null && simplifiedTopPattern.length > 0){
            startElement = new SElement(selements.size(), null, simplifiedTopPattern[0], -1, null, recursionModel);
        }else{
            startElement = new SElement(selements.size(), null, null, -1, null, recursionModel);
        }
        selements.add(startElement); 
        referenceModel.init(definitionTopPatterns.toArray(new SPattern[definitionTopPatterns.size()]));
		SimplifiedModel simplifiedModel = new SimplifiedModel( selements.size()-1,
											selements.toArray(new SElement[selements.size()]),
											sattributes.toArray(new SAttribute[sattributes.size()]),
											sexceptPatterns.toArray(new SExceptPattern[sexceptPatterns.size()]),
											simplifiedTopPattern,
											referenceModel,
											recursionModel);
		return simplifiedModel;
	}
		
	
	/*public SimplifiedModel simplify(URI base, ParsedModel parsedModel, List<Templates> schematronTemplates)  throws SAXException{
        if(parsedModel == null) return null;
        
        topPattern = parsedModel.getTopPattern();        
		if(topPattern == null) return null;
			
		inclusionPath.push(base);
		docParsedModels.put(base, parsedModel);
				
		grammarDefinitions.clear();
		externalRefs.clear();
		docParsedModels.clear();
		inclusionPath.clear();
        
		componentAsciiDL.clear();
		asciiDlDatatypeLibrary.clear();
						
        simplificationContext.reset();
		
		mapper.map(base,
					topPattern,
					grammarDefinitions,
					externalRefs,
					docParsedModels,
					inclusionPath,
					componentAsciiDL,
					asciiDlDatatypeLibrary,
                    simplificationContext,
                    schematronTemplates);
		
		//System.out.println("grammarDefinitions "+grammarDefinitions);
		//System.out.println("externalRefs "+externalRefs);
		//System.out.println("docParsedModels "+docParsedModels);
		//System.out.println("**************************************");
		
        
		simplify();
		SPattern[] simplifiedTopPattern = builder.getAllCurrentPatterns();
		if(simplifiedTopPattern == null){
            if(emptyChild){
                // for the 7.1.5 restrictions on start
                // TODO make sure it is correct to treat notAllowed like this
                builder.buildEmpty(topPattern.getRecordIndex(), topPattern.getDocumentIndexedData(), true);
                simplifiedTopPattern = builder.getAllCurrentPatterns();
            }else if(notAllowedElement || notAllowedChild){
                builder.buildNotAllowed(topPattern.getRecordIndex(), topPattern.getDocumentIndexedData(), true);
                simplifiedTopPattern = builder.getAllCurrentPatterns();
            }
            
		}
        
		SElement startElement;        
        if(simplifiedTopPattern != null && simplifiedTopPattern.length > 0){
            startElement = new SElement(selements.size(), null, simplifiedTopPattern[0], -1, null, recursionModel);
        }else{
            startElement = new SElement(selements.size(), null, null, -1, null, recursionModel);
        }
        selements.add(startElement);   
        referenceModel.init(definitionTopPatterns.toArray(new SPattern[definitionTopPatterns.size()]));
		SimplifiedModel simplifiedModel = new SimplifiedModel(selements.size()-1,
											selements.toArray(new SElement[selements.size()]),
											sattributes.toArray(new SAttribute[sattributes.size()]),
											sexceptPatterns.toArray(new SExceptPattern[sexceptPatterns.size()]),
											simplifiedTopPattern,
											referenceModel,
											recursionModel);
		return simplifiedModel;
	}*/
	
		
	private void simplify()  throws SAXException{   	
        paramStack.clear();
        
	    selements.clear();	
	    sattributes.clear();	
	    sexceptPatterns.clear();
	    	    
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
		
		currentGrammar = null;
		if(previousGrammars != null)previousGrammars.clear();		
				
		recursionModel = new RecursionModel();
		referenceModel = new ReferenceModel();
		
		definitionTopPatterns.clear();
		builder.startBuild();
		topPattern.accept(this);
	}		
}