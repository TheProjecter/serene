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
import java.util.Set;

import javax.xml.transform.Templates;

import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TemplatesHandler;


import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

import serene.datatype.DatatypeLibraryFinder;

import serene.internal.InternalRNGFactory;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.Pattern;
import serene.validation.schema.parsed.Definition;
import serene.validation.schema.parsed.Grammar;
import serene.validation.schema.parsed.ExternalRef;

import serene.validation.handlers.error.ErrorDispatcher;

class Mapper{	
	Map<Definition, Map<ExternalRef, URI>> definitionExternalRefs;
	
	XMLReader xmlReader;
	InternalRNGFactory internalRNGFactory;
	
	boolean processEmbededSchematron;
	boolean restrictToFileName;
	
	TransformerHandler schematronStartTransformerHandler;
    SAXResult expandedSchematronResult;
    TransformerHandler schematronCompilerXSLT1;
    TransformerHandler schematronCompilerXSLT2;
    TemplatesHandler schematronTemplatesHandler;
	
	NamespaceInheritanceHandler namespaceInheritanceHandler;
	GrammarDefinitionsMapper grammarDefinitionsMapper; 
	
	ExternalRefParser externalRefParser;
	Mapper mapper;
	
	ErrorDispatcher errorDispatcher;
	
	Mapper(ErrorDispatcher errorDispatcher, NamespaceInheritanceHandler namespaceInheritanceHandler){
				
		this.errorDispatcher = errorDispatcher;
		this.namespaceInheritanceHandler = namespaceInheritanceHandler;
		
		definitionExternalRefs = new HashMap<Definition, Map<ExternalRef, URI>>();
		
		SecuritySupport ss = new SecuritySupport();		
		ClassLoader cl;        
        cl = ss.getContextClassLoader();
        
        if (cl == null) {
            //cl = ClassLoader.getSystemClassLoader();
            //use the current class loader
            cl = Simplifier.class.getClassLoader();
        } 

		grammarDefinitionsMapper = new GrammarDefinitionsMapper(errorDispatcher, namespaceInheritanceHandler, new DatatypeLibraryFinder(cl));
	}
	
	void setReplaceMissingDatatypeLibrary(boolean value){
		grammarDefinitionsMapper.setReplaceMissingDatatypeLibrary(value);
	}
	
    void setRestrictToFileName(boolean restrictToFileName){
        this.restrictToFileName = restrictToFileName;
        grammarDefinitionsMapper.setRestrictToFileName(restrictToFileName);        
        if(mapper != null)
	        mapper.setRestrictToFileName(restrictToFileName);
	    if(externalRefParser != null) externalRefParser.setRestrictToFileName(restrictToFileName);
    }    
   
    void setParserComponents(XMLReader xmlReader, InternalRNGFactory internalRNGFactory){
	    this.xmlReader = xmlReader;
		this.internalRNGFactory = internalRNGFactory;
		grammarDefinitionsMapper.setParserComponents(xmlReader, internalRNGFactory);
		if(externalRefParser != null) externalRefParser.setParserComponents(xmlReader, internalRNGFactory);
	}
	
	void setProcessEmbededSchematron(boolean processEmbededSchematron){
	    this.processEmbededSchematron = processEmbededSchematron;
	    grammarDefinitionsMapper.setProcessEmbededSchematron(processEmbededSchematron);
	    if(mapper != null)mapper.setProcessEmbededSchematron(processEmbededSchematron);
	    if(externalRefParser != null){
	        externalRefParser.setProcessEmbededSchematron(processEmbededSchematron);
	        // TODO what about enabling Schematron?	        
	    }
	}	
    void setSchematronParserComponents(TransformerHandler schematronStartTransformerHandler,
	                                            SAXResult expandedSchematronResult,
	                                            TransformerHandler schematronCompilerXSLT1,
	                                            TransformerHandler schematronCompilerXSLT2,
	                                            TemplatesHandler schematronTemplatesHandler){
	    this.schematronStartTransformerHandler = schematronStartTransformerHandler;
	    this.expandedSchematronResult = expandedSchematronResult;
	    this.schematronCompilerXSLT1 = schematronCompilerXSLT1;
	    this.schematronCompilerXSLT2 = schematronCompilerXSLT2;
	    this.schematronTemplatesHandler = schematronTemplatesHandler;
	    grammarDefinitionsMapper.setSchematronParserComponents(schematronStartTransformerHandler,
	                                                            expandedSchematronResult,
	                                                            schematronCompilerXSLT1,
	                                                            schematronCompilerXSLT2,
	                                                            schematronTemplatesHandler);
	    if(mapper != null)mapper.setSchematronParserComponents(schematronStartTransformerHandler,
	                                                            expandedSchematronResult,
	                                                            schematronCompilerXSLT1,
	                                                            schematronCompilerXSLT2,
	                                                            schematronTemplatesHandler);
	    if(externalRefParser != null)externalRefParser.setSchematronParserComponents(schematronStartTransformerHandler,
	                                                            expandedSchematronResult,
	                                                            schematronCompilerXSLT1,
	                                                            schematronCompilerXSLT2,
	                                                            schematronTemplatesHandler);
	}
	
	void map(URI base,
			Pattern topPattern,
			Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions,	
			Map<ExternalRef, URI> externalRefs,
			Map<URI, ParsedModel> docParsedModels,
			Stack<URI> inclusionPath,
			Map<ParsedComponent, String> componentAsciiDL,
			Map<String, DatatypeLibrary> asciiDlDatatypeLibrary,
            DocumentSimplificationContext simplificationContext) throws SAXException{
		
		
		definitionExternalRefs.clear();
		
		grammarDefinitionsMapper.map(base, topPattern, grammarDefinitions, definitionExternalRefs, inclusionPath, componentAsciiDL, asciiDlDatatypeLibrary, simplificationContext);
				
		if(definitionExternalRefs.isEmpty())return;
			
		if(externalRefParser == null){
		    externalRefParser = new ExternalRefParser(errorDispatcher);		    
		    externalRefParser.setParserComponents(xmlReader, internalRNGFactory);		    
		    if(processEmbededSchematron) externalRefParser.setSchematronParserComponents(schematronStartTransformerHandler,
	                                                            expandedSchematronResult,
	                                                            schematronCompilerXSLT1,
	                                                            schematronCompilerXSLT2,
	                                                            schematronTemplatesHandler);
	        externalRefParser.setRestrictToFileName(restrictToFileName);
		    externalRefParser.setProcessEmbededSchematron(processEmbededSchematron);
		}
		if(mapper == null){
		    mapper = new Mapper(errorDispatcher, namespaceInheritanceHandler);
		    mapper.setParserComponents(xmlReader, internalRNGFactory);
		    if(processEmbededSchematron)
		        mapper.setSchematronParserComponents(schematronStartTransformerHandler,
	                expandedSchematronResult,
	                schematronCompilerXSLT1,
	                schematronCompilerXSLT2,
	                schematronTemplatesHandler);
	        mapper.setProcessEmbededSchematron(processEmbededSchematron);
	        mapper.setRestrictToFileName(restrictToFileName);
		}
		
		Set<Definition> externalRefKeys = definitionExternalRefs.keySet();
		for(Definition key : externalRefKeys){
			Map<ExternalRef, URI> externalRefURIs =  definitionExternalRefs.get(key);
			Set<ExternalRef> eRefs = externalRefURIs.keySet();
			for(ExternalRef eRef : eRefs){
				URI uri = externalRefURIs.get(eRef);
				externalRefs.put(eRef, uri);
				if(!docParsedModels.containsKey(uri)){
					ParsedModel refModel = externalRefParser.parse(uri);
					if(refModel != null){
                        docParsedModels.put(uri, refModel);
                        Pattern docTopPattern = refModel.getTopPattern();
                        namespaceInheritanceHandler.put(docTopPattern, eRef);
                        mapper.map(uri,
                            docTopPattern,
                            grammarDefinitions,	
                            externalRefs,
                            docParsedModels,
                            inclusionPath,
                            componentAsciiDL,
                            asciiDlDatatypeLibrary,
                            simplificationContext);
                    }
				}
			}
		} 	
	}	
	
	
	void map(URI base,
			Pattern topPattern,
			Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions,	
			Map<ExternalRef, URI> externalRefs,
			Map<URI, ParsedModel> docParsedModels,
			Stack<URI> inclusionPath,
			Map<ParsedComponent, String> componentAsciiDL,
			Map<String, DatatypeLibrary> asciiDlDatatypeLibrary,
            DocumentSimplificationContext simplificationContext,
            List<Templates> schematronTemplates) throws SAXException{
		
		
		definitionExternalRefs.clear();
		
		grammarDefinitionsMapper.map(base, topPattern, grammarDefinitions, definitionExternalRefs, inclusionPath, componentAsciiDL, asciiDlDatatypeLibrary, simplificationContext, schematronTemplates);
				
		if(definitionExternalRefs.isEmpty())return;
			
		if(externalRefParser == null){
		    externalRefParser = new ExternalRefParser(errorDispatcher);
		    externalRefParser.setParserComponents(xmlReader, internalRNGFactory);		    		    
		    if(processEmbededSchematron) externalRefParser.setSchematronParserComponents(schematronStartTransformerHandler,
	                                                            expandedSchematronResult,
	                                                            schematronCompilerXSLT1,
	                                                            schematronCompilerXSLT2,
	                                                            schematronTemplatesHandler);
	        externalRefParser.setRestrictToFileName(restrictToFileName);
		    externalRefParser.setProcessEmbededSchematron(processEmbededSchematron);
		}
		if(mapper == null){
		    mapper = new Mapper(errorDispatcher, namespaceInheritanceHandler);
		    mapper.setParserComponents(xmlReader, internalRNGFactory);
		    if(processEmbededSchematron)
		        mapper.setSchematronParserComponents(schematronStartTransformerHandler,
	                expandedSchematronResult,
	                schematronCompilerXSLT1,
	                schematronCompilerXSLT2,
	                schematronTemplatesHandler);
	        mapper.setProcessEmbededSchematron(processEmbededSchematron);
	        mapper.setRestrictToFileName(restrictToFileName);
		}
		
		Set<Definition> externalRefKeys = definitionExternalRefs.keySet();
		for(Definition key : externalRefKeys){
			Map<ExternalRef, URI> externalRefURIs =  definitionExternalRefs.get(key);
			Set<ExternalRef> eRefs = externalRefURIs.keySet();
			for(ExternalRef eRef : eRefs){
				URI uri = externalRefURIs.get(eRef);
				externalRefs.put(eRef, uri);
				if(!docParsedModels.containsKey(uri)){
					ParsedModel refModel = externalRefParser.parse(uri, schematronTemplates);
					if(refModel != null){
                        docParsedModels.put(uri, refModel);
                        Pattern docTopPattern = refModel.getTopPattern();
                        namespaceInheritanceHandler.put(docTopPattern, eRef);
                        mapper.map(uri,
                            docTopPattern,
                            grammarDefinitions,	
                            externalRefs,
                            docParsedModels,
                            inclusionPath,
                            componentAsciiDL,
                            asciiDlDatatypeLibrary,
                            simplificationContext, 
                            schematronTemplates);
                    }
				}
			}
		} 	
	}	
}