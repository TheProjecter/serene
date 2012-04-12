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

import sereneWrite.MessageWriter;

class Mapper{	
	Map<Definition, Map<ExternalRef, URI>> definitionExternalRefs;
	
	XMLReader xmlReader;
	InternalRNGFactory internalRNGFactory;
	
	NamespaceInheritanceHandler namespaceInheritanceHandler;
	GrammarDefinitionsMapper grammarDefinitionsMapper; 
	
	ExternalRefParser externalRefParser;
	Mapper mapper;
	
	ErrorDispatcher errorDispatcher;
	
	
	MessageWriter debugWriter;
	
	Mapper(XMLReader xmlReader, InternalRNGFactory internalRNGFactory, ErrorDispatcher errorDispatcher, NamespaceInheritanceHandler namespaceInheritanceHandler, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.xmlReader = xmlReader;
		this.internalRNGFactory = internalRNGFactory;
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

		grammarDefinitionsMapper = new GrammarDefinitionsMapper(xmlReader, internalRNGFactory, errorDispatcher, namespaceInheritanceHandler, new DatatypeLibraryFinder(cl), debugWriter);
	}
	
	void setReplaceMissingDatatypeLibrary(boolean value){
		grammarDefinitionsMapper.setReplaceMissingDatatypeLibrary(value);
	}
	
    void setRestrictToFileName(boolean restrictToFileName){
        grammarDefinitionsMapper.setRestrictToFileName(restrictToFileName);
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
			
		if(externalRefParser == null)externalRefParser = new ExternalRefParser(xmlReader, internalRNGFactory, errorDispatcher, debugWriter);
		if(mapper == null)mapper = new Mapper(xmlReader, internalRNGFactory, errorDispatcher, namespaceInheritanceHandler, debugWriter);
		
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
}