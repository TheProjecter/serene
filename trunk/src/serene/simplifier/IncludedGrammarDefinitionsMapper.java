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
import java.net.URISyntaxException;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

import serene.internal.InternalRNGFactory;

import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.ParsedComponentVisitor;

import serene.validation.schema.parsed.Pattern;
import serene.validation.schema.parsed.Definition;

import serene.validation.schema.parsed.Grammar;
import serene.validation.schema.parsed.ExternalRef;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.validation.jaxp.SchematronParser;

class IncludedGrammarDefinitionsMapper extends GrammarDefinitionsMapper{
	Map<Definition, ArrayList<Grammar>> definitionGrammars;
	
	IncludedGrammarDefinitionsMapper(ErrorDispatcher errorDispatcher, 
								NamespaceInheritanceHandler namespaceInheritanceHandler,
								DatatypeLibraryFactory datatypeLibraryFactory,
								SchematronParser schematronParser){
		super(errorDispatcher, namespaceInheritanceHandler, datatypeLibraryFactory, schematronParser);
	}
	
	void map(URI base,
			Pattern topPattern,
			Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions,
			Map<Definition, Map<ExternalRef, URI>> definitionExternalRefs,
			Map<Definition, ArrayList<Grammar>> definitionGrammars, 
			Stack<URI> inclusionPath,
			Map<ParsedComponent, String> componentAsciiDL,
			Map<String, DatatypeLibrary> asciiDlDatatypeLibrary,
            DocumentSimplificationContext simplificationContext) throws SAXException{
		this.definitionGrammars = definitionGrammars;
		map(base, topPattern, grammarDefinitions, definitionExternalRefs, inclusionPath, componentAsciiDL, asciiDlDatatypeLibrary, simplificationContext);
	}	
	
	public void visit(Grammar grammar) throws SAXException{	
		String dla = grammar.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(grammar);			
		}
		
		Map<String, ArrayList<Definition>> parentContext = currentContextDefinitions;
		currentContextDefinitions = new HashMap<String, ArrayList<Definition>>();
		grammarDefinitions.put(grammar, currentContextDefinitions);
		
		if(currentDefinition!= null){
			ArrayList<Grammar> gg = definitionGrammars.get(currentDefinition);
			if(gg == null){
				gg = new ArrayList<Grammar>();
				definitionGrammars.put(currentDefinition, gg);
			}
			gg.add(grammar);
		}
		
		Grammar contextGrammar = currentGrammar;
		currentGrammar = grammar;
		
		URI oldBase = xmlBaseUri;
		String xmlBase = grammar.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, grammar);
		}
		
		ParsedComponent[] children = grammar.getChildren();
		if(children != null)next(children);
		
		xmlBaseUri = oldBase;
		currentGrammar = contextGrammar;		
		currentContextDefinitions = parentContext;	

		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}		
	}
	
		
	public String toString(){
		return "IncludedGrammarDefinitionsMapper ";
	}
}