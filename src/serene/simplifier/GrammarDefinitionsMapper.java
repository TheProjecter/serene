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
import java.util.Set;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

import serene.datatype.MissingLibraryException;

import serene.internal.InternalRNGFactory;

import serene.DTDMapping;

import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.SimplifyingVisitor;

import serene.validation.schema.parsed.Pattern;
import serene.validation.schema.parsed.Param;
import serene.validation.schema.parsed.Include;
import serene.validation.schema.parsed.ExceptPattern;
import serene.validation.schema.parsed.ExceptNameClass;
import serene.validation.schema.parsed.DivGrammarContent;
import serene.validation.schema.parsed.DivIncludeContent;
import serene.validation.schema.parsed.Definition;

import serene.validation.schema.parsed.ElementWithNameClass;
import serene.validation.schema.parsed.ElementWithNameInstance;
import serene.validation.schema.parsed.AttributeWithNameClass;
import serene.validation.schema.parsed.AttributeWithNameInstance;
import serene.validation.schema.parsed.ChoicePattern;
import serene.validation.schema.parsed.Interleave;
import serene.validation.schema.parsed.Group;
import serene.validation.schema.parsed.ZeroOrMore;
import serene.validation.schema.parsed.OneOrMore;
import serene.validation.schema.parsed.Optional;
import serene.validation.schema.parsed.ListPattern;
import serene.validation.schema.parsed.Mixed;
import serene.validation.schema.parsed.Empty;
import serene.validation.schema.parsed.Text;
import serene.validation.schema.parsed.NotAllowed;
import serene.validation.schema.parsed.ExternalRef;
import serene.validation.schema.parsed.Ref;
import serene.validation.schema.parsed.ParentRef;
import serene.validation.schema.parsed.Data;
import serene.validation.schema.parsed.Value;
import serene.validation.schema.parsed.Grammar;
import serene.validation.schema.parsed.Dummy;

import serene.validation.schema.parsed.Name;
import serene.validation.schema.parsed.AnyName;
import serene.validation.schema.parsed.NsName;
import serene.validation.schema.parsed.ChoiceNameClass;

import serene.validation.schema.parsed.Define;
import serene.validation.schema.parsed.Start;

import serene.validation.schema.parsed.ForeignComponent;

import serene.validation.schema.parsed.DefinitionCopier;

import serene.validation.handlers.error.ErrorDispatcher;

import sereneWrite.MessageWriter;

class GrammarDefinitionsMapper implements SimplifyingVisitor{
	
	Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions;	
	Map<Definition, Map<ExternalRef, URI>> definitionExternalRefs;
	
	Map<ParsedComponent, String> componentAsciiDL;
	Map<String, DatatypeLibrary> asciiDlDatatypeLibrary;
	
	URI xmlBaseUri;
	Grammar currentGrammar;
	Definition currentDefinition;
	Map<String, ArrayList<Definition>> currentContextDefinitions;
	
	Stack<URI> inclusionPath;
	
	XMLReader xmlReader;
	InternalRNGFactory internalRNGFactory;
	
	IncludeParser includeParser;
	IncludedGrammarDefinitionsMapper includedGrammarDefinitionsMapper;
	NamespaceInheritanceHandler namespaceInheritanceHandler;
	DefinitionCopier definitionCopier; 
	
	DatatypeLibraryFactory datatypeLibraryFactory;
	String currentDatatypeLibrary;
	
	boolean replaceMissingDatatypeLibrary;
    boolean restrictToFileName;
    
    DocumentSimplificationContext simplificationContext;
	
	ErrorDispatcher errorDispatcher;
	
	MessageWriter debugWriter;
	
	GrammarDefinitionsMapper(XMLReader xmlReader, 
							InternalRNGFactory internalRNGFactory, 
							ErrorDispatcher errorDispatcher, 
							NamespaceInheritanceHandler namespaceInheritanceHandler,
							DatatypeLibraryFactory datatypeLibraryFactory,
							MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
		this.xmlReader = xmlReader;
		this.internalRNGFactory = internalRNGFactory;
		this.errorDispatcher = errorDispatcher;
		this.namespaceInheritanceHandler = namespaceInheritanceHandler;
		this.datatypeLibraryFactory = datatypeLibraryFactory;
		
		replaceMissingDatatypeLibrary = true;
	}
	
	void setReplaceMissingDatatypeLibrary(boolean value){
		this.replaceMissingDatatypeLibrary = value;
	}
	
    void setRestrictToFileName(boolean restrictToFileName){
        this.restrictToFileName = restrictToFileName;
    }
    
	void map(URI xmlBaseUri,
			Pattern topPattern,
			Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions,
			Map<Definition, Map<ExternalRef, URI>> definitionExternalRefs,
			Stack<URI> inclusionPath,
			Map<ParsedComponent, String> componentAsciiDL,
			Map<String, DatatypeLibrary> asciiDlDatatypeLibrary,
            DocumentSimplificationContext simplificationContext) throws SAXException{
		this.grammarDefinitions = grammarDefinitions;	
		this.definitionExternalRefs = definitionExternalRefs;	
		this.inclusionPath = inclusionPath;
		this.componentAsciiDL = componentAsciiDL;
		this.asciiDlDatatypeLibrary = asciiDlDatatypeLibrary;
        this.simplificationContext = simplificationContext;
		
		this.xmlBaseUri = xmlBaseUri;
		currentGrammar = null;
		currentDefinition = null;
		currentContextDefinitions = null;
		currentDatatypeLibrary = "";
		
		if(!asciiDlDatatypeLibrary.containsKey(currentDatatypeLibrary)){
			DatatypeLibrary nativeDL = datatypeLibraryFactory.createDatatypeLibrary(currentDatatypeLibrary);
			asciiDlDatatypeLibrary.put(currentDatatypeLibrary, nativeDL);
			if(nativeDL == null){
				//System.out.println("error 4.3 unknown or unsupported DatatypeLibrary");
				String message = "Simplification 4.3 error. "
				+"Native datatype libarary is unknown or unsupported.";				
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}
		
		if(topPattern !=null)//to catch situations when href uris were faultive
			topPattern.accept(this);
	}
	
	public void visit(Include include) throws SAXException{
		String dla = include.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(include);			
		}
		
		String href = include.getHref();
		if(href == null){
			//syntax error			
			return;
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = include.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, include);
		}
		
		URI hrefURI = null;
		try{
			hrefURI = getHrefURI(href);
		}catch(URISyntaxException e){			
			// report 4.5 error
			String message = "Simplification 4.5 error. "
			+"Illegal href attribute value in <"+include.getQName()+"> at "+include.getLocation(restrictToFileName)+": "
			+"\n"+e.getMessage(); 
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			xmlBaseUri = oldBase;
			return;
		}
		if(hrefURI.getFragment() != null){
			String message = "Simplification 4.5 error. "
			+"Illegal href attribute value in <"+include.getQName()+"> at "+include.getLocation(restrictToFileName)+": "
			+"\n"+"Fragment identifier present";
			//System.out.println(message);
			// report 4.5 error
			errorDispatcher.error(new SAXParseException(message, null));
			xmlBaseUri = oldBase;
			return;
		}
		if(inclusionPath.contains(hrefURI)){
			String message = "Simplification 4.6 error. "
			+"Illegal href attribute value in <"+include.getQName()+"> at "+include.getLocation(restrictToFileName)+": "
			+"\n"+"Recursive inclusion.";
			//System.out.println(message);
			// report 4.6 error
			errorDispatcher.error(new SAXParseException(message, null));
			xmlBaseUri = oldBase;
			return; 
		}
		
        IncludedParsedModel includedModel = parse(hrefURI); 
		Grammar includedGrammar = includedModel.getTopPattern();
        
		if(includedGrammar == null) return;

        DTDMapping dtdMapping = includedModel.getDTDMapping();
        if(dtdMapping != null) simplificationContext.merge(dtdMapping);	
		
		Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions = new HashMap<Grammar, Map<String, ArrayList<Definition>>>();
		// Maps Definitions to the Grammars embeded in them.
		// Used to stop the grammars embeded in definitions that have been 
		// overriden to be taken over in this.grammarDefinitions.
		// The top grammar(includedGrammar) is not recorded here.(no null key)		
		Map<Definition, ArrayList<Grammar>> definitionGrammars = new HashMap<Definition, ArrayList<Grammar>>();
		inclusionPath.push(hrefURI);        
		map(hrefURI, includedGrammar, grammarDefinitions, definitionGrammars);
		inclusionPath.pop();
		
		Map<String, ArrayList<Definition>> parentContextDefinitions = currentContextDefinitions;
		currentContextDefinitions = new HashMap<String, ArrayList<Definition>>();
						
		ParsedComponent[] children = include.getChildren();		
		if(children != null) next(children);

		doReplacements(include,
					includedGrammar, 
					parentContextDefinitions, 
					currentContextDefinitions,
					grammarDefinitions,
					definitionGrammars);
		
		
		
		xmlBaseUri = oldBase;
		currentContextDefinitions = parentContextDefinitions;

		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}		
	}
	private URI getHrefURI(String href) throws URISyntaxException{
		URI hrefURI = null;
		if(href != null){
			hrefURI = new URI(href);
			if(xmlBaseUri != null){
				hrefURI = xmlBaseUri.resolve(hrefURI);
			}			
		}		
		return hrefURI;
	}
	
	private IncludedParsedModel parse(URI hrefURI){
		if(includeParser == null)includeParser = new IncludeParser(xmlReader, internalRNGFactory, errorDispatcher, debugWriter);
		return includeParser.parse(hrefURI);		
	}
	private void map(URI base,
					Grammar includedGrammar, 
					Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions, 
					Map<Definition, ArrayList<Grammar>> definitionGrammars) throws SAXException{				
		if(includedGrammarDefinitionsMapper == null)includedGrammarDefinitionsMapper = new IncludedGrammarDefinitionsMapper(xmlReader, internalRNGFactory, errorDispatcher, namespaceInheritanceHandler, datatypeLibraryFactory, debugWriter);
		includedGrammarDefinitionsMapper.map(base, includedGrammar, grammarDefinitions, definitionExternalRefs, definitionGrammars, inclusionPath, componentAsciiDL, asciiDlDatatypeLibrary, simplificationContext);		
	}
	private void doReplacements(Include include,
								Grammar includedGrammar,
								Map<String, ArrayList<Definition>> contextDefinitions,// definitions of the including Grammar
								Map<String, ArrayList<Definition>> overrideDefinitions,// definitions of the include 								
								Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions,// definitions of the included Grammar and its descendants
								Map<Definition, ArrayList<Grammar>> definitionGrammars)// mapping of the definitions in the included Grammar and its descendants to the Grammars they contain
								throws SAXException{
		/*System.out.println("includedGrammar "+includedGrammar);
		System.out.println("contextDefinitions "+contextDefinitions);
		System.out.println("overrideDefinitions "+overrideDefinitions);
		System.out.println("grammarDefinitions "+grammarDefinitions);
		System.out.println("definitionGrammars "+definitionGrammars);
		System.out.println("**************************************");*/
		Map<String, ArrayList<Definition>> includedGrammarDefinitions = grammarDefinitions.remove(includedGrammar);
		Set<String> includedNames = includedGrammarDefinitions.keySet();		
		for(String name : includedNames){
			ArrayList<Definition> included = includedGrammarDefinitions.get(name);
			ArrayList<Definition> override = overrideDefinitions.remove(name);
			if(override != null){
				// remove grammars, externalRefs corresponding to included
				// they will be overriden/discarded
				for(Definition overriden : included){
					definitionExternalRefs.remove(overriden);
					ArrayList<Grammar> removedGrammars = definitionGrammars.get(overriden);
					if(removedGrammars != null){
						for(Grammar removed : removedGrammars){
							grammarDefinitions.remove(removed);
						}
					}
				}
				included = override;
			}else{
				// create copies of the included definitions 
				// and map to be ready for ns/currentDatatypeLibrary simplification
				if(definitionCopier == null) definitionCopier = new DefinitionCopier(debugWriter);
				for(int i = 0; i < included.size(); i++){
					Definition d = included.get(i);
					d = definitionCopier.copy(d);
					included.set(i, d);
					namespaceInheritanceHandler.put(d, include);
				}
			}
			//add included to the contextDefinitions
			ArrayList<Definition> contextNameDefinitions = contextDefinitions.get(name);
			if(contextNameDefinitions == null){
				contextNameDefinitions = new ArrayList<Definition>();				
				contextDefinitions.put(name, contextNameDefinitions);
			}
			contextNameDefinitions.addAll(included);
		}				
		if(!overrideDefinitions.isEmpty()){
			Set<String> remainingNames = overrideDefinitions.keySet();			
			for(String remainingName : remainingNames){
                //check for Simplification 4.7 errors
				if(remainingName == null){
					//report error start
					String message = "Simplification 4.7 error. "
					+"Included grammar contains no start element to match the content of <"+include.getQName()+"> at "+include.getLocation(restrictToFileName)+":";
					ArrayList<Definition> starts = overrideDefinitions.get(null);
					for(int i = 0; i < starts.size(); i++){
						message += "\n<"+starts.get(i).getQName()+"> at "+starts.get(i).getLocation(restrictToFileName);
					}
					message += ".";
					errorDispatcher.error(new SAXParseException(message, null));
				}else{
					//report error define
                    String reportName = remainingName.substring(0, remainingName.length()-1);//remove marker
					String message = "Simplification 4.7 error. "
					+"Included grammar contains no definition with name \""+reportName+"\" to match the content of <"+include.getQName()+"> at "+include.getLocation(restrictToFileName)+":";
					ArrayList<Definition> defines = overrideDefinitions.get(remainingName);
					for(int i = 0; i < defines.size(); i++){
						message += "\n<"+defines.get(i).getQName()+"> at "+defines.get(i).getLocation(restrictToFileName);
					}
					message += ".";
					errorDispatcher.error(new SAXParseException(message, null));
				}
                //add to the context
                ArrayList<Definition> included = overrideDefinitions.get(remainingName);
                ArrayList<Definition> contextNameDefinitions = contextDefinitions.get(remainingName);
                if(contextNameDefinitions == null){
                    contextNameDefinitions = new ArrayList<Definition>();				
                    contextDefinitions.put(remainingName, contextNameDefinitions);
                }
                contextNameDefinitions.addAll(included);
			}			
		}		
		if(!definitionGrammars.isEmpty()){
			Set<Definition> definitions = definitionGrammars.keySet();
			for(Definition definition : definitions){
				ArrayList<Grammar> grammars = definitionGrammars.get(definition);
				for(Grammar grammar : grammars){
					this.grammarDefinitions.put(grammar, grammarDefinitions.get(grammar));
				}
			}
		}		
	}
	public void visit(ExceptPattern exceptPattern) throws SAXException{
		String dla = exceptPattern.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(exceptPattern);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = exceptPattern.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, exceptPattern);
		}
		
		ParsedComponent[] children = exceptPattern.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(ExceptNameClass exceptNameClass){
		// normally this is not necessary since all the descendants are name classes
		// and no external references of any kind are possible any more
		/*URI oldBase = xmlBaseUri;
		String xmlBase = exceptNameClass.getXmlBaseAttribute();
		if(xmlBase != null) resolve(xmlBase); 
		
		ParsedComponent[] children = exceptNameClass.getChildren();
		if(children != null) next(children);

		xmlBaseUri = oldBase;*/		
	}
	public void visit(DivGrammarContent div) throws SAXException{
		String dla = div.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(div);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = div.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, div);
		}
			
		ParsedComponent[] children = div.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(DivIncludeContent div) throws SAXException{
		String dla = div.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(div);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = div.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, div);
		}
			
		ParsedComponent[] children = div.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
		
	public void visit(Name name){}
	public void visit(AnyName anyName){
		// normally this is not necessary since all the descendants are name classes
		// and no external references of any kind are possible any more
		/*URI oldBase = xmlBaseUri;
		String xmlBase = anyName.getXmlBaseAttribute();
		if(xmlBase != null) resolve(xmlBase); 
		
		ParsedComponent child = anyName.getChild();
		if(child != null) next(child);		
		
		xmlBaseUri = oldBase;*/		
		
	}
	public void visit(NsName nsName){
		// normally this is not necessary since all the descendants are name classes
		// and no external references of any kind are possible any more
		/*URI oldBase = xmlBaseUri;
		String xmlBase = nsName.getXmlBaseAttribute();
		if(xmlBase != null) resolve(xmlBase); 
		
		ParsedComponent child = nsName.getChild();
		if(child != null) next(child);		
		
		xmlBaseUri = oldBase;*/
	}
	public void visit(ChoiceNameClass choice){
		// normally this is not necessary since all the descendants are name classes
		// and no external references of any kind are possible any more
		/*URI oldBase = xmlBaseUri;
		String xmlBase = choice.getXmlBaseAttribute();
		if(xmlBase != null) resolve(xmlBase); 
		
		ParsedComponent[] children = choice.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;*/
	}	
	
	public void visit(Define define) throws SAXException{
		String dla = define.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(define);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = define.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, define);
		}
		
		String name = define.getName();
		add:{
			if(name == null) break add;
			name = name.trim()+'*';//add marker to separate define without name attribute from start
			ArrayList<Definition> defs = currentContextDefinitions.get(name);
			if(defs == null){
				defs = new ArrayList<Definition>();
				currentContextDefinitions.put(name, defs);
			}
			defs.add(define);
		}
		
		Definition contextDefinition = currentDefinition; 
		currentDefinition = define;
		
		ParsedComponent[] children = define.getChildren();
		if(children != null)next(children);
		
		xmlBaseUri = oldBase;		
		currentDefinition = contextDefinition;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(Start start) throws SAXException{
		String dla = start.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(start);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = start.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, start);
		}
			
		String name = null;
		ArrayList<Definition> defs = currentContextDefinitions.get(name);
		if(defs == null){
			defs = new ArrayList<Definition>();
			currentContextDefinitions.put(name, defs);
		}
		defs.add(start);
		
		Definition contextDefinition = currentDefinition; 
		currentDefinition = start;
		
		ParsedComponent[] children = start.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		currentDefinition = contextDefinition;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
		
	public void visit(ElementWithNameClass element) throws SAXException{
		String dla = element.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(element);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = element.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, element);
		}
				
		ParsedComponent[] children = element.getChildren();
		if(children != null)next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}	
	public void visit(ElementWithNameInstance element) throws SAXException{
		String dla = element.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(element);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = element.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, element);
		}
			
		ParsedComponent[] children = element.getChildren();
		if(children != null)next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}	
	public void visit(AttributeWithNameClass attribute) throws SAXException{
		String dla = attribute.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(attribute);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = attribute.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, attribute);
		}
						
		ParsedComponent[] children = attribute.getChildren();
		if(children != null) next(children);

		xmlBaseUri = oldBase;

		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}		
	}
	public void visit(AttributeWithNameInstance attribute) throws SAXException{
		String dla = attribute.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(attribute);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = attribute.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, attribute);
		}
			
		ParsedComponent[] children = attribute.getChildren();
		if(children != null) next(children);

		xmlBaseUri = oldBase;

		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}		
	}
	public void visit(ChoicePattern choice) throws SAXException{
		String dla = choice.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(choice);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = choice.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, choice);
		}
			
		ParsedComponent[] children = choice.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(Interleave interleave) throws SAXException{
		String dla = interleave.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(interleave);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = interleave.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, interleave);
		}
			
		ParsedComponent[] children = interleave.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(Group group) throws SAXException{
		String dla = group.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(group);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = group.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, group);
		}
			
		ParsedComponent[] children = group.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(ZeroOrMore zeroOrMore) throws SAXException{
		String dla = zeroOrMore.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(zeroOrMore);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = zeroOrMore.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, zeroOrMore);
		}
			
		ParsedComponent[] children = zeroOrMore.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(OneOrMore oneOrMore) throws SAXException{
		String dla = oneOrMore.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(oneOrMore);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = oneOrMore.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, oneOrMore);
		}
			
		ParsedComponent[] children = oneOrMore.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(Optional optional) throws SAXException{
		String dla = optional.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(optional);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = optional.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, optional);
		}
			
		ParsedComponent[] children = optional.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}	
	public void visit(ListPattern list) throws SAXException{
		String dla = list.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(list);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = list.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, list);
		}
			
		ParsedComponent[] children = list.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}	
	public void visit(Mixed mixed) throws SAXException{
		String dla = mixed.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(mixed);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = mixed.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, mixed);
		}
			
		ParsedComponent[] children = mixed.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}	
	public void visit(Empty empty){	}
	public void visit(Text text){}
	public void visit(NotAllowed notAllowed){}
	
	public void visit(ExternalRef externalRef)throws SAXException{
		String href = externalRef.getHref();
		if(href == null){
			//syntax error			
			return;
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = externalRef.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, externalRef);
		}
				
		URI hrefURI = null;
		try{
			hrefURI = getHrefURI(href);
		}catch(URISyntaxException e){			
			// report 4.5 error
			String message = "Simplification 4.5 error. "
			+"Illegal href attribute value in <"+externalRef.getQName()+"> at "+externalRef.getLocation(restrictToFileName)+": "
			+"\n"+e.getMessage(); 
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			xmlBaseUri = oldBase;
			return;
		}
		if(hrefURI.getFragment() != null){
			String message = "Simplification 4.5 error. "
			+"Illegal href attribute value in <"+externalRef.getQName()+"> at "+externalRef.getLocation(restrictToFileName)+": "
			+"\n"+"Fragment identifier present";
			//System.out.println(message);
			// report 4.5 error
			errorDispatcher.error(new SAXParseException(message, null));
			xmlBaseUri = oldBase;
			return;
		}
		
		Map<ExternalRef, URI> erefs = definitionExternalRefs.get(currentDefinition);
		if(erefs == null){
			erefs = new HashMap<ExternalRef, URI>();
			definitionExternalRefs.put(currentDefinition, erefs);
		}
		erefs.put(externalRef, hrefURI);
		
		xmlBaseUri = oldBase;
	}
	public void visit(Ref ref){}
	public void visit(ParentRef parentRef){}    
	public void visit(Value value) throws SAXException{
		String dla = value.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(value);			
		}
		componentAsciiDL.put(value, currentDatatypeLibrary);
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
	public void visit(Data data) throws SAXException{
		String dla = data.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(data);			
		}
		componentAsciiDL.put(data, currentDatatypeLibrary);
		
		URI oldBase = xmlBaseUri;
		String xmlBase = data.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, data);
		}
		
		ParsedComponent[] children = data.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}	
	public void visit(Param param){}
    
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
		
	public void visit(Dummy dummy) throws SAXException{
		String dla = dummy.getDatatypeLibraryAttribute();
		String oldDla = null;
		if(dla != null){
			oldDla = currentDatatypeLibrary;
			currentDatatypeLibrary = handleDatatypeLibraryAttribute(dla);
			mapDatatypeLibrary(dummy);			
		}
		
		URI oldBase = xmlBaseUri;
		String xmlBase = dummy.getXmlBaseAttribute();
		if(xmlBase != null){
			resolve(xmlBase, dummy);
		}
			
		ParsedComponent[] children = dummy.getChildren();
		if(children != null) next(children);
		
		xmlBaseUri = oldBase;
		
		if(oldDla != null){
			currentDatatypeLibrary = oldDla;
		}
	}
    
    public void visit(ForeignComponent fc) throws SAXException{
		
	}
	
	void next(ParsedComponent[] children) throws SAXException{	
		for(ParsedComponent child : children){            
			child.accept(this);
		}
	}
	
	void next(ParsedComponent child) throws SAXException{
		child.accept(this);
	}
	
	boolean resolve(String xmlBase, ParsedComponent c) throws SAXException{	
		try{
			if(xmlBaseUri == null){
				xmlBaseUri = new URI(xmlBase);
			}else{
				xmlBaseUri = xmlBaseUri.resolve(new URI(xmlBase)); 
			}
			return true;
		}catch(URISyntaxException use){
			String message = "Simplification 4.1 error. "
			+ "Could not resolve xml:base URI defined at element <"+c.getQName()+"> at "+c.getLocation(restrictToFileName)+". "
			+ use.getMessage();			
			errorDispatcher.error(new SAXParseException(message, null));
			return false;
		}
	}
	
	String handleDatatypeLibraryAttribute(String value){
		if(value.equals("")) return value;
		
		URI dl;
		try{
			dl = new URI(value); 
		}catch(URISyntaxException e){
			// Syntax error, same as relative URI, or fragment identifier
			// should have been reported already.
            // But the relative URI, or fragment identifier do not result in 
            // error, so they do return a non null string here, resulting in 
            // unknown datatype error. And untransparent behaviour.
            // TODO
			return null;
		}
		return dl.toASCIIString();
	}
	
	void mapDatatypeLibrary(ParsedComponent c) throws SAXException{
        if(currentDatatypeLibrary == null)return; //syntax error already reported
		if(asciiDlDatatypeLibrary.containsKey(currentDatatypeLibrary)) return;
		DatatypeLibrary datatypeLibrary = datatypeLibraryFactory.createDatatypeLibrary(currentDatatypeLibrary);
		asciiDlDatatypeLibrary.put(currentDatatypeLibrary, datatypeLibrary);
		if(datatypeLibrary == null){
			//System.out.println("error 4.3 unknown or unsupported DatatypeLibrary");
			String message = "Simplification 4.3 error. "
			+"Attribute datatypeLibrary of element <"+c.getQName()+"> at "+c.getLocation(restrictToFileName)+" specifies an unknown or unsupported datatype library.";
			if(replaceMissingDatatypeLibrary){
				errorDispatcher.error(new MissingLibraryException(message, null));
			}else{
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}
	}
	public String toString(){
		return "GrammarDefinitionsMapper ";
	}
}
