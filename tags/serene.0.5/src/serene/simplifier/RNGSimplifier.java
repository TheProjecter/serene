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
import java.util.HashSet;

import javax.xml.XMLConstants;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import org.relaxng.datatype.DatatypeLibrary;

import serene.util.ObjectIntHashMap;
import serene.util.BooleanList;
import serene.util.IntStack;

import serene.validation.schema.parsed.ParsedComponent;

import serene.validation.schema.parsed.components.Pattern;
import serene.validation.schema.parsed.components.Definition;

import serene.validation.schema.parsed.components.Grammar;
import serene.validation.schema.parsed.components.ExternalRef;

import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.SimplifiedComponentBuilder;

import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SRef;

import serene.internal.InternalRNGFactory;

import serene.validation.handlers.error.ErrorDispatcher;

import sereneWrite.MessageWriter;
import sereneWrite.ParsedComponentWriter;

public class RNGSimplifier extends Simplifier{	
	Pattern topPattern;
				
	Mapper mapper;
	Stack<URI> inclusionPath;
	
	
	public RNGSimplifier(XMLReader xmlReader, InternalRNGFactory internalRNGFactory, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		super(errorDispatcher, debugWriter);
				
		grammarDefinitions = new HashMap<Grammar, Map<String, ArrayList<Definition>>>();		
		externalRefs = new HashMap<ExternalRef, URI>();			
		docTopPatterns = new HashMap<URI, Pattern>();
		namespaceInheritanceHandler = new NamespaceInheritanceHandler(debugWriter);
		pool = new DefinitionSimplifierPool(errorDispatcher, debugWriter);
		
		componentAsciiDL = new HashMap<ParsedComponent, String>();
		asciiDlDatatypeLibrary = new HashMap<String, DatatypeLibrary>();
		
		indexes = new ObjectIntHashMap(debugWriter);	
		indexes.setNullValue(-1);
				
		previousGrammars = new Stack<Grammar>();
		
		definitionTopPatterns = new ArrayList<SPattern>();
		referencePath = new IntStack();		
		definitionEmptyChild = new BooleanList();
		definitionNotAllowedChild = new BooleanList();
		
		pcw = new ParsedComponentWriter();
		
		
		inclusionPath = new Stack<URI>();
		mapper = new Mapper(xmlReader, internalRNGFactory, errorDispatcher, namespaceInheritanceHandler, debugWriter);				
	}
	
	public void setReplaceMissingDatatypeLibrary(boolean value){
		replaceMissingDatatypeLibrary =  value;
		mapper.setReplaceMissingDatatypeLibrary(value);
	}
	
	public SimplifiedModel simplify(URI base, Pattern topPattern)  throws SAXException{
		if(topPattern == null) return null;
					
		grammarDefinitions.clear();
		externalRefs.clear();
		docTopPatterns.clear();
		inclusionPath.clear();
		
		componentAsciiDL.clear();
		asciiDlDatatypeLibrary.clear();
		
		this.topPattern = topPattern;
		inclusionPath.push(base);
		docTopPatterns.put(base, topPattern);
		
		mapper.map(base,
					topPattern,
					grammarDefinitions,
					externalRefs,
					docTopPatterns,
					inclusionPath,
					componentAsciiDL,
					asciiDlDatatypeLibrary);
		
		//System.out.println("grammarDefinitions "+grammarDefinitions);
		//System.out.println("externalRefs "+externalRefs);
		//System.out.println("docTopPatterns "+docTopPatterns);
		//System.out.println("**************************************");
		
		recursionModel = new RecursionModel(debugWriter);
		
		simplify();
		SPattern[] sTopPattern = builder.getAllCurrentPatterns();
		if(sTopPattern == null && (emptyChild || notAllowedChild)){
			// for the 7.1.5 restrictions on start
			// TODO make sure it is correct to treat notAllowed like this
			builder.buildEmpty(topPattern.getQName(), topPattern.getLocation());
			sTopPattern = builder.getAllCurrentPatterns();			
		}
		SimplifiedModel simplifiedModel = new SimplifiedModel(sTopPattern, 
											definitionTopPatterns.toArray(new SPattern[definitionTopPatterns.size()]),
											recursionModel,
											asciiDlDatatypeLibrary,
											debugWriter);
		return simplifiedModel;
	}
		
	private void simplify()  throws SAXException{		
		emptyChild = false;
		notAllowedChild = false;
		
		anyNameContext = false;
		anyNameExceptContext = false;
		nsNameContext = false;
		nsNameExceptContext = false;
		attributeContext = false;
		
		currentGrammar = null;
		if(previousGrammars != null)previousGrammars.clear();		
		
		definitionTopPatterns.clear();
		builder.startBuild();
		topPattern.accept(this);	
	}		
}