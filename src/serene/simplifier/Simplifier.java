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

import java.util.Arrays;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Collection;

import javax.xml.XMLConstants;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.DatatypeException;

import serene.util.ObjectIntHashMap;
import serene.util.BooleanList;
import serene.util.IntStack;
import serene.util.IntList;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.SimplifyingVisitor;

import serene.validation.schema.parsed.Pattern;
import serene.validation.schema.parsed.NameClass;
import serene.validation.schema.parsed.Definition;

import serene.validation.schema.parsed.Param;
import serene.validation.schema.parsed.Include;
import serene.validation.schema.parsed.ExceptPattern;
import serene.validation.schema.parsed.ExceptNameClass;
import serene.validation.schema.parsed.DivGrammarContent;
import serene.validation.schema.parsed.DivIncludeContent;

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

import serene.validation.schema.simplified.ReferenceModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.SPattern;

import serene.validation.schema.simplified.SimplifiedComponentBuilder;
import serene.validation.schema.simplified.SNameClass;
import serene.validation.schema.simplified.SRef;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SPattern;


import serene.internal.InternalRNGFactory;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.bind.util.DocumentIndexedData;

import serene.validation.jaxp.SchematronParser;

import sereneWrite.ParsedComponentWriter;

import serene.Constants;

abstract class Simplifier implements SimplifyingVisitor{	
	Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions;	
	Map<ExternalRef, URI> externalRefs;
	Map<URI, ParsedModel> docParsedModels;
	
	ArrayList<SPattern> definitionTopPatterns;
	
	
	Map<ParsedComponent, String> componentAsciiDL;
	Map<String, DatatypeLibrary> asciiDlDatatypeLibrary;
	
	NamespaceInheritanceHandler namespaceInheritanceHandler;
	
	/*
    Index processed structures (top patterns of external refs and definitions 
    lists) to keep track of what was already processed. Used to determine 
    recursions and unprocessed definitions.
    */
	ObjectIntHashMap indexes;	
	IntStack referencePath;
	BooleanList definitionEmptyChild;
	BooleanList definitionNotAllowedChild;	
	RecursionModel recursionModel;
		
	Grammar currentGrammar;
	Stack<Grammar> previousGrammars;
    
    ArrayList<SPattern> currentDefinitionTopPatterns;
	
	boolean emptyChild;
	ParsedComponent emptyComponent;
	boolean notAllowedChild;
    boolean patternChild;
    boolean notAllowedElement;
    
	boolean anyNameContext;
	boolean anyNameExceptContext;
	boolean nsNameContext;
	boolean nsNameExceptContext;
	boolean attributeContext;
    
    Stack<ArrayList<Param>> paramStack;
    ArrayList<Param> currentParams;
    
    DocumentSimplificationContext simplificationContext;
	
	DefinitionSimplifierPool pool;
	SimplifiedComponentBuilder builder;
	
	ErrorDispatcher errorDispatcher;

    boolean level1AttributeDefaultValue;
    boolean level1AttributeIdType;
	boolean replaceMissingDatatypeLibrary;
    boolean restrictToFileName;
    boolean processEmbeddedSchematron;

	//ParsedComponentWriter pcw;
	
	ArrayList<SElement> selements;	
	ArrayList<SAttribute> sattributes;	
	ArrayList<SExceptPattern> sexceptPatterns;
	
	ReferenceModel referenceModel;
		
	SchematronParser schematronParser;
	
	Simplifier(ErrorDispatcher errorDispatcher, SchematronParser schematronParser){	
		this.errorDispatcher = errorDispatcher;
		this.schematronParser = schematronParser;

		builder = new SimplifiedComponentBuilder();		
		//pcw = new ParsedComponentWriter();
		
		replaceMissingDatatypeLibrary =  true;
        level1AttributeDefaultValue  = false;
        level1AttributeIdType = false;
        
        paramStack = new Stack<ArrayList<Param>>();
        
        
        selements = new ArrayList<SElement>();
        sattributes = new ArrayList<SAttribute>();
        sexceptPatterns = new ArrayList<SExceptPattern>();        
	}
	
	public abstract void setReplaceMissingDatatypeLibrary(boolean value);
	public void setLevel1AttributeDefaultValue(boolean level1AttributeDefaultValue){
        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
    }
    
    public void setLevel1AttributeIdType(boolean value){
        level1AttributeIdType = value;
    }    
    public void setRestrictToFileName(boolean restrictToFileName){
        this.restrictToFileName = restrictToFileName;
    }    
    public void setProcessEmbeddedSchematron(boolean processEmbeddedSchematron){
        this.processEmbeddedSchematron = processEmbeddedSchematron;
    }
    
	public void visit(Include include){
		throw new IllegalStateException();
	}
	public void visit(ExceptPattern exceptPattern) throws SAXException{        
		ParsedComponent[] children = exceptPattern.getChildren();
		if(children == null) {
			builder.buildExceptPattern(sexceptPatterns.size(), exceptPattern.getRecordIndex(), exceptPattern.getDocumentIndexedData());
            patternChild = true;
            SExceptPattern se = builder.getCurrentExceptPattern();
            sexceptPatterns.add(se);
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = exceptPattern.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int allowedChildrenCount = 0;
		int emptyCount = 0;
		builder.startLevel();
        boolean oldNotAllowedChild = notAllowedChild;     

        
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                next(children[i]);
                if(patternChild){
                    allowedChildrenCount++;
                    if(notAllowedChild) {
                        allowedChildrenCount--;
                    }
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }
            }
		}		
        notAllowedChild = oldNotAllowedChild;
		if(allowedChildrenCount == 0){
			builder.endLevel();
			builder.clearContent();
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;            
			return;
		}
		if(emptyCount > 0){
		    // TODO 
		    // Possible problem here!!!!!
			builder.buildEmpty(emptyComponent.getRecordIndex(), /*exceptPattern.getRecordIndex(),*/ exceptPattern.getDocumentIndexedData(), true);					
		}
        if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementChoicePattern(exceptPattern.getRecordIndex(), 
                            exceptPattern.getDocumentIndexedData(), 
                            true);
		}
		builder.endLevel();
		builder.buildExceptPattern(sexceptPatterns.size(), exceptPattern.getRecordIndex(), exceptPattern.getDocumentIndexedData());
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;
        SExceptPattern se = builder.getCurrentExceptPattern();
        sexceptPatterns.add(se);
	}
	public void visit(ExceptNameClass exceptNameClass) throws SAXException{
        ParsedComponent[] children = exceptNameClass.getChildren();
        if(children == null) {
			builder.buildExceptNameClass(exceptNameClass.getRecordIndex(), exceptNameClass.getDocumentIndexedData());
            patternChild = false;
			return;
		}
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = exceptNameClass.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		if(anyNameContext){
			anyNameExceptContext = true;
		}
		if(nsNameContext){
			nsNameExceptContext = true;
		}
		attributeContext = false;
		
        builder.startLevel();
		next(children);		
        if(builder.getCurrentNameClassesCount() > 1){
			builder.buildReplacementChoiceNameClass(exceptNameClass.getRecordIndex(), exceptNameClass.getDocumentIndexedData());
		}
        builder.endLevel();
		builder.buildExceptNameClass(exceptNameClass.getRecordIndex(), exceptNameClass.getDocumentIndexedData());
		
		anyNameExceptContext = false;	
		nsNameExceptContext = false;
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = false;
        attributeContext = oldAttributeContext;
	}
	
	public void visit(DivGrammarContent div){
		throw new IllegalStateException();
	}
	public void visit(DivIncludeContent div){
		throw new IllegalStateException();
	}
	/*
	4.16 errors should be reported at the top of the context, so that the faultive 
	SAnyName or SNsName is not built and doesn't take part in name class overlap
	handling during restrictions check.
	*/
	public void visit(Name name) throws SAXException{
        Map<String, String> prefixMapping = name.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		String qname = name.getCharacterContent().trim();
		String prefix = getPrefix(qname);
		String localPart = getLocalPart(qname);
		String ns;        
		if(prefix != null)ns = simplificationContext.resolveNamespacePrefix(prefix);
        else ns = namespaceInheritanceHandler.getNsURI(name);
		if(ns == null)ns = "";
        
		if(attributeContext){
			if(ns.equals("") && localPart.equals("xmlns")){				 
				// 4.16 error
				String message = "Simplification 4.16 error. "
				+"Illegal character content of element <"+name.getQName()+"> at "+name.getLocation(restrictToFileName)+".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
			if(ns.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)){
				// 4.16 error
				String message = "Simplification 4.16 error. "
				+"Illegal ns of element <"+name.getQName()+"> at "+name.getLocation(restrictToFileName)+".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}
		builder.buildName(ns, localPart, name.getRecordIndex(), name.getDocumentIndexedData(), false);
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = false;
	}
    
	public void visit(AnyName anyName) throws SAXException{
		if(anyNameExceptContext){
			// 4.16 error
			String message = "Simplification 4.16 error. "
				+"Presence of element <"+anyName.getQName()+"> at "+anyName.getLocation(restrictToFileName)+" is forbiden in this context.";
			errorDispatcher.error(new SAXParseException(message, null));
			// TODO Q: do you need to build a dummy?
            patternChild = false;
			return;
		}
		if(nsNameExceptContext){
			// 4.16 error
			String message = "Simplification 4.16 error. "
				+"Presence of element <"+anyName.getQName()+"> at "+anyName.getLocation(restrictToFileName)+" is forbiden in this context.";
				//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			// TODO Q: do you need to build a dummy?
            patternChild = false;
			return;
		}	
        Map<String, String> prefixMapping = anyName.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		anyNameContext = true;
		ParsedComponent[] children = anyName.getChildren();
		if(children != null) nextLevel(children);
		anyNameContext = false;
		
		builder.buildAnyName(anyName.getRecordIndex(), anyName.getDocumentIndexedData());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = false;
	}
	public void visit(NsName nsName) throws SAXException{
		if(nsNameExceptContext){
			// 4.16 error
			String message = "Simplification 4.16 error. "
				+"Presence of element <"+nsName.getQName()+"> at "+nsName.getLocation(restrictToFileName)+" is forbiden in this context.";
			errorDispatcher.error(new SAXParseException(message, null));
			// TODO Q: do you need to build a dummy?
            patternChild = false;
			return;
		}        
		String ns = namespaceInheritanceHandler.getNsURI(nsName);	
		if(ns == null)ns ="";
		if(attributeContext){			
			if(ns.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)){
				// 4.16 error
				String message = "Simplification 4.16 error. "
				+"Illegal ns of element <"+nsName.getQName()+"> at "+nsName.getLocation(restrictToFileName)+".";
				errorDispatcher.error(new SAXParseException(message, null));
				// TODO Q: do you need to build a dummy?
                patternChild = false;
				return;
			}
		}
		
        Map<String, String> prefixMapping = nsName.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		nsNameContext = true;
		ParsedComponent[] children = nsName.getChildren();
		if(children != null) nextLevel(children);
		nsNameContext = false;
				
		builder.buildNsName(ns, nsName.getRecordIndex(), nsName.getDocumentIndexedData());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = false;
	}
    
	public void visit(ChoiceNameClass choice) throws SAXException{
        Map<String, String> prefixMapping = choice.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		ParsedComponent[] children = choice.getChildren();
		if(children != null) nextLevel(children);
		builder.buildChoiceNameClass(choice.getRecordIndex(), choice.getDocumentIndexedData());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = false;
	}	
	
	public void visit(Define define) throws SAXException{	
	    //System.out.println("SIMPLIFIER VISIT Element <"+define.getQName()+"> at "+define.getLocation(restrictToFileName));
		ParsedComponent[] children = define.getChildren();
			
		if(children == null) {
            patternChild = false;			
			return;
		}
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        namespaceInheritanceHandler.startXmlnsContext(simplificationContext, define);
        builder.startLevel();	
        int emptyCount = 0;
		int childrenCount = 0;
        boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;   

		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild;                
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }                
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            //notAllowedChild = true;
            emptyChild = false;
            namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
            notAllowedChild = notAllowed;
            patternChild = false;
            attributeContext = oldAttributeContext;
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = define;
            namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
            patternChild = false;
            attributeContext = oldAttributeContext;
			return;
		}
        
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup(define.getRecordIndex(), 
                            define.getDocumentIndexedData(), 
                            true);			
		}		
		builder.endLevel();
        
        SPattern p = builder.getLastContentPattern();
        if( p != null){
            currentDefinitionTopPatterns.add(p);
            builder.clearContent();
        }
        
        namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
        patternChild = false;
        attributeContext = oldAttributeContext;        
	}
	public void visit(Start start)  throws SAXException{
		ParsedComponent[] children = start.getChildren();
			
		if(children == null) {
            patternChild = false;			
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        namespaceInheritanceHandler.startXmlnsContext(simplificationContext, start);
		
        builder.startLevel();
        
        int emptyCount = 0;
		int childrenCount = 0;		
        boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild;                
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            //notAllowedChild = true;
            emptyChild = false;
            namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);            
            notAllowedChild = notAllowed;
            patternChild = false;
            attributeContext = oldAttributeContext;
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = start;
            namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);
            patternChild = false;
            attributeContext = oldAttributeContext;
			return;
		}       
        		
		builder.endLevel();
		
        SPattern[] sChildren = builder.getContentPatterns();
        if(sChildren != null){
            for(SPattern sChild : sChildren){
                currentDefinitionTopPatterns.add(sChild);
            }
            builder.clearContent();
        }
        
        namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);
        patternChild = false;        
        attributeContext = oldAttributeContext;
	}
		
	public void visit(ElementWithNameClass element)  throws SAXException{
		ParsedComponent[] children = element.getChildren();		
		        		
		if(children == null) {			
			builder.buildElement(selements.size(), element.getRecordIndex(), element.getDocumentIndexedData(), recursionModel);
            patternChild = true;
            notAllowedElement = false;
            SElement e = (SElement)builder.getCurrentPattern();
            selements.add(e);
			return;
		}	
        
		
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = element.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
        int nonEmptyChildrenCount = 0;
        boolean containsPattern = false;
		int notAllowedCount = 0;
        builder.startLevel();
        boolean oldNotAllowedChild = notAllowedChild;        
        
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                next(children[i]);
                if(patternChild){
                    if(!containsPattern)containsPattern = true;
                    nonEmptyChildrenCount++;
                    if(notAllowedChild){
                        notAllowedCount++;
                    }
                    if(emptyChild){
                        nonEmptyChildrenCount--;
                        emptyChild = false;
                    }
                }
            }
		}		
        notAllowedChild = oldNotAllowedChild;
		if(notAllowedCount > 0){
			builder.endLevel();
			builder.clearContent();
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            notAllowedElement = true;
            attributeContext = oldAttributeContext;
            
			return;
		}
        if(containsPattern && nonEmptyChildrenCount == 0){
            // TODO 
            // Are you sure this is right? Shouldn't the "true" be set from somewhere else? 
            builder.buildEmpty(emptyComponent.getRecordIndex(), emptyComponent.getDocumentIndexedData(), true);
        }
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup(element.getRecordIndex(), 
                            element.getDocumentIndexedData(), 
                            true);
		}
		builder.endLevel();
		builder.buildElement(selements.size(), element.getRecordIndex(), element.getDocumentIndexedData(), recursionModel);
		SElement e = (SElement)builder.getCurrentPattern();
        selements.add(e);
		
		
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        notAllowedElement = true;
        attributeContext = oldAttributeContext;        
	}	
	public void visit(ElementWithNameInstance element)  throws SAXException{
		ParsedComponent[] children = element.getChildren();
		        
		if(children == null) {
			builder.buildElement(selements.size(), element.getRecordIndex(), element.getDocumentIndexedData(), recursionModel);
            patternChild = true;
            notAllowedElement = false;
            SElement e = (SElement)builder.getCurrentPattern();
            selements.add(e);
			return;
		}

        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = element.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
				
		builder.startLevel();//children
        
        int nonEmptyChildrenCount = 0;
        int notAllowedCount = 0;
        boolean oldNotAllowedChild = notAllowedChild;
        
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false; 
                                
                next(children[i]);
                if(patternChild){
                    nonEmptyChildrenCount++;
                    if(notAllowedChild){
                        notAllowedCount++;
                    }
                    if(emptyChild){
                        nonEmptyChildrenCount--;
                        emptyChild = false;
                    }
                }                
            }
		}		
        notAllowedChild = oldNotAllowedChild;
		if(notAllowedCount > 0){
			builder.endLevel();
			builder.clearContent();
            
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            notAllowedElement = true;
            attributeContext = oldAttributeContext;            
			return;
		}
        if(nonEmptyChildrenCount == 0){
            builder.buildEmpty(emptyComponent.getRecordIndex(), emptyComponent.getDocumentIndexedData(), true);
        }
		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup(element.getRecordIndex(), 
                            element.getDocumentIndexedData(), 
                            true);
		}
        
		String name = element.getName();
        String prefix = null;
		String localPart = null;
        String ns;
        if(name != null){
            name = name.trim();
            prefix = getPrefix(name);
            localPart = getLocalPart(name);
        }
        
		if(prefix != null) ns = simplificationContext.resolveNamespacePrefix(prefix);
        else ns = namespaceInheritanceHandler.getNsURI(element);
		if(ns == null)ns = "";
		builder.buildName(ns, localPart, element.getRecordIndex(), element.getDocumentIndexedData(), true);
		
		builder.endLevel();
		builder.buildElement(selements.size(), element.getRecordIndex(), element.getDocumentIndexedData(), recursionModel);
		SElement e = (SElement)builder.getCurrentPattern();
        selements.add(e);
            
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        notAllowedElement = false;
        attributeContext = oldAttributeContext;        
	}    
	public void visit(AttributeWithNameClass attribute)  throws SAXException{
		ParsedComponent[] children = attribute.getChildren();		
		
        Map<String, String> prefixMapping = attribute.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
                
        if(children == null){
            builder.startLevel();
            builder.buildText(attribute.getRecordIndex(), attribute.getDocumentIndexedData(), true);
			builder.endLevel();
            if(level1AttributeDefaultValue){
                /*AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
                if(foreignAttributes != null){
                    builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
                }else{
                    builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
                } */               
                builder.buildAttribute(sattributes.size(), 
                                    attribute.getDefaultValueRecordIndex(),
                                    attribute.getRecordIndex(), 
                                    attribute.getDocumentIndexedData());
                SAttribute a = (SAttribute)builder.getCurrentPattern();
                sattributes.add(a);
            }else{
                builder.buildAttribute(sattributes.size(), 
                                    DocumentIndexedData.NO_RECORD,
                                    attribute.getRecordIndex(), 
                                    attribute.getDocumentIndexedData());
                SAttribute a = (SAttribute)builder.getCurrentPattern();
                sattributes.add(a);
            }
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            return;
        }
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = true;
        
		builder.startLevel();
        
		int emptyCount = 0;
		int childrenCount = 0;
        boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild;                
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }
            }	
		}		
        if(notAllowed){		
            builder.endLevel();
            builder.clearContent();
            //notAllowedChild = true;
            emptyChild = false;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            notAllowedChild = notAllowed;
            patternChild = true;
            attributeContext = oldAttributeContext;
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		
        if(childrenCount == 0){
            builder.buildText(attribute.getRecordIndex(), attribute.getDocumentIndexedData(), true);
        }else if(emptyCount == childrenCount){
			/*
			Replaced with a limitation placed in restrictions control.
            LATER: 
            Changed again so it can be used for the validation during 
            compatibility control independent of whether the schema is correct
            or not. Check for null was added to datatypeMatches() in 
            DatatypedCharsAPattern.
            
			String ns  = namespaceInheritanceHandler.getNsURI(attribute);
			if(ns == null) ns = "";
			builder.buildValue(ns, null, null, "value", attribute.getLocation());*/
            builder.buildEmpty(emptyComponent.getRecordIndex(), emptyComponent.getDocumentIndexedData(), true);
            emptyChild = false;
		}
		        
		builder.endLevel();
		if(level1AttributeDefaultValue){
            builder.buildAttribute(sattributes.size(), 
                                    attribute.getDefaultValueRecordIndex(),
                                    attribute.getRecordIndex(), 
                                    attribute.getDocumentIndexedData());
            SAttribute a = (SAttribute)builder.getCurrentPattern();
            sattributes.add(a);
        }else{
            builder.buildAttribute(sattributes.size(), 
                                    DocumentIndexedData.NO_RECORD,
                                    attribute.getRecordIndex(), 
                                    attribute.getDocumentIndexedData());
            SAttribute a = (SAttribute)builder.getCurrentPattern();
            sattributes.add(a);
        }
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;        
	}
	public void visit(AttributeWithNameInstance attribute)  throws SAXException{
		ParsedComponent[] children = attribute.getChildren();		
		
        Map<String, String> prefixMapping = attribute.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		builder.startLevel();
		if(children == null) {
			//4.12
			builder.buildText(attribute.getRecordIndex(), attribute.getDocumentIndexedData(), true);
            String name = attribute.getName();
            
            String prefix = null;
            String localPart = null;            
            if(name != null){                
                name = name.trim();
                prefix = getPrefix(name);
                localPart = getLocalPart(name);                
            }
            String ns;
            if(prefix != null)ns = simplificationContext.resolveNamespacePrefix(prefix);
            else ns = namespaceInheritanceHandler.getNsURI(attribute);
            if(ns == null)ns = "";
            if(localPart != null && ns.equals("") && localPart.equals("xmlns")){				 
                // 4.16 error
                String message = "Simplification 4.16 error. "
                +"Illegal value of name attribute of element <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+".";
                errorDispatcher.error(new SAXParseException(message, null));
            }
			if(ns.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)){
				// 4.16 error
				String message = "Simplification 4.16 error. "
				+"Illegal ns of attribute name of element <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+".";
				errorDispatcher.error(new SAXParseException(message, null));
			}            
			builder.buildName(ns, localPart, attribute.getRecordIndex(), attribute.getDocumentIndexedData(), true);
			builder.endLevel();
			if(level1AttributeDefaultValue){
                builder.buildAttribute(sattributes.size(),                    
                                    attribute.getDefaultValueRecordIndex(),
                                    attribute.getRecordIndex(), 
                                    attribute.getDocumentIndexedData());
                SAttribute a = (SAttribute)builder.getCurrentPattern();
                sattributes.add(a);                
            }else{
                builder.buildAttribute(sattributes.size(), 
                                    DocumentIndexedData.NO_RECORD,
                                    attribute.getRecordIndex(), 
                                    attribute.getDocumentIndexedData());
                SAttribute a = (SAttribute)builder.getCurrentPattern();
                sattributes.add(a);
            }
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
			return;
		}
        
		

        boolean oldAttributeContext = attributeContext;
        attributeContext = true;
        
		int emptyCount = 0;
		int childrenCount = 0;
        boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;
        attributeContext = true;
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild;                
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }              
            }	
		}		
        if(notAllowed){		
            builder.endLevel();
            builder.clearContent();
            //notAllowedChild = true;
            emptyChild = false;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            notAllowedChild = notAllowed;
            patternChild = true;
            attributeContext = oldAttributeContext;  
            
            return;
        }
        notAllowedChild = oldNotAllowedChild;
        
		if(childrenCount == 0){
            builder.buildText(attribute.getRecordIndex(), attribute.getDocumentIndexedData(), true);
        }else if(emptyCount == childrenCount){
			/*
			Replaced with a limitation placed in restrictions control.
            LATER: 
            Changed again so it can be used for the validation during 
            compatibility control independent of whether the schema is correct
            or not. Check for null was added to datatypeMatches() in 
            DatatypedCharsAPattern.
            
			String ns  = namespaceInheritanceHandler.getNsURI(attribute);
			if(ns == null) ns = "";
			builder.buildValue(ns, null, null, "value", attribute.getLocation());*/
            builder.buildEmpty(emptyComponent.getRecordIndex(), emptyComponent.getDocumentIndexedData(), true);
            emptyChild = false;
		}				
		
		String name = attribute.getName();
        String prefix = null;
        String localPart = null;        
        if(name != null){
            name = name.trim();
            prefix  = getPrefix(name);
            localPart = getLocalPart(name);            
        }
        String ns;
        if(prefix != null)ns = simplificationContext.resolveNamespacePrefix(prefix);
        else ns = namespaceInheritanceHandler.getNsURI(attribute);
        if(ns == null)ns = "";
        if(localPart != null && ns.equals("") && localPart.equals("xmlns")){				 
            // 4.16 error
            String message = "Simplification 4.16 error. "
            +"Illegal value of name attribute of element <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+".";
            errorDispatcher.error(new SAXParseException(message, null));
        }
        if(ns.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)){
            // 4.16 error
            String message = "Simplification 4.16 error. "
            +"Illegal ns of attribute name of element <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+".";
            errorDispatcher.error(new SAXParseException(message, null));
        } 		
		builder.buildName(ns, localPart, attribute.getRecordIndex(), attribute.getDocumentIndexedData(), true);
		
		builder.endLevel();
		if(level1AttributeDefaultValue){
            builder.buildAttribute(sattributes.size(), 
                                    attribute.getDefaultValueRecordIndex(),
                                    attribute.getRecordIndex(), 
                                    attribute.getDocumentIndexedData());
            SAttribute a = (SAttribute)builder.getCurrentPattern();
            sattributes.add(a);
        }else{
            builder.buildAttribute(sattributes.size(), 
                                    DocumentIndexedData.NO_RECORD,
                                    attribute.getRecordIndex(), 
                                    attribute.getDocumentIndexedData());
            SAttribute a = (SAttribute)builder.getCurrentPattern();
            sattributes.add(a);
        }
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;
	}
	public void visit(ChoicePattern choice)  throws SAXException{
		ParsedComponent[] children = choice.getChildren();	
		if(children == null){
			builder.buildChoicePattern(choice.getRecordIndex(), 
			                        choice.getDocumentIndexedData(),
			                        false);
            patternChild = true;
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = choice.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		
        builder.startLevel();
        
        boolean oldNotAllowedChild = notAllowedChild;
        notAllowedChild = false;
        int childrenCount = 0;
		int notAllowedChildrenCount = 0;
		int emptyCount = 0;	
		
		for(int i = 0; i < children.length; i++){            
			if(children[i] != null){
			    
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(notAllowedChild){
                        notAllowedChildrenCount++;
                        notAllowedChild = false;
                    }
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }                
            }			
		}		
		if(notAllowedChildrenCount == childrenCount){						
			builder.endLevel();
			builder.clearContent();
			notAllowedChild = true;
			emptyChild = false;
            patternChild = true;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            attributeContext = oldAttributeContext;
            
			return;
		}
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){			
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = choice;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            
			return;
		}
		if(emptyCount > 0){
			builder.buildEmpty(emptyComponent.getRecordIndex(), emptyComponent.getDocumentIndexedData(), true);
		}
		builder.endLevel();
		builder.buildChoicePattern(choice.getRecordIndex(), 
                            choice.getDocumentIndexedData(), 
                            false);
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;        
	}
	public void visit(Interleave interleave) throws SAXException{
		ParsedComponent[] children = interleave.getChildren();
		
		if(children == null) {
			builder.buildInterleave(interleave.getRecordIndex(), 
			                        interleave.getDocumentIndexedData());
            patternChild = true;			
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = interleave.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
        builder.startLevel();
        
        boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;       
		int emptyCount = 0;
		int childrenCount = 0;
		
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild; 
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }                
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            notAllowedChild = true;
            emptyChild = false;
            patternChild = true;
            attributeContext = oldAttributeContext;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);            
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = interleave;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;            
			return;
		}	
		
		builder.endLevel();
		builder.buildInterleave(interleave.getRecordIndex(), 
                            interleave.getDocumentIndexedData());
		
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;        
	}
	public void visit(Group group)  throws SAXException{
		ParsedComponent[] children = group.getChildren();
		
		if(children == null) {
			builder.buildGroup(group.getRecordIndex(), 
                                group.getDocumentIndexedData(), 
                                false);
            patternChild = true;			
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = group.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
        builder.startLevel();
        
		boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;       
		int emptyCount = 0;
		int childrenCount = 0;
		
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                                               
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild; 
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }                
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            notAllowedChild = true;
            emptyChild = false;
            patternChild = true;
            attributeContext = oldAttributeContext;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = group;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            
			return;
		}
		
		builder.endLevel();
		builder.buildGroup(group.getRecordIndex(), 
                            group.getDocumentIndexedData(), 
                            false);
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;       
	}
	public void visit(ZeroOrMore zeroOrMore)  throws SAXException{
		ParsedComponent[] children = zeroOrMore.getChildren();
		
		if(children == null) {
			//builder.zeroOrMore(zeroOrMore.getRecordIndex(), zeroOrMore.getDocumentIndexedData());
            patternChild = true;			
			return;
		}
        		
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = zeroOrMore.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
       
		builder.startLevel();//children
        
		boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;       
		int emptyCount = 0;
		int childrenCount = 0;
				
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild; 
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            notAllowedChild = oldNotAllowedChild;
            emptyChild = true;
			emptyComponent = zeroOrMore;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = zeroOrMore;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            
			return;
		}
				
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup(zeroOrMore.getRecordIndex(), 
                            zeroOrMore.getDocumentIndexedData(), 
                            true);
		}	
		builder.endLevel();
		builder.zeroOrMore(zeroOrMore.getRecordIndex(), zeroOrMore.getDocumentIndexedData());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;        
	}
	public void visit(OneOrMore oneOrMore)  throws SAXException{
		ParsedComponent[] children = oneOrMore.getChildren();
		
		if(children == null) {
			//builder.oneOrMore(oneOrMore.getRecordIndex(), oneOrMore.getDocumentIndexedData());
            patternChild = true;			
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = oneOrMore.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        		
		builder.startLevel();//children
        
		boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;       
		int emptyCount = 0;
		int childrenCount = 0;
				
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                               
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild; 
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            notAllowedChild = true;
            emptyChild = false;
            patternChild = true;
            attributeContext = oldAttributeContext;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = oneOrMore;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            
			return;
		}
		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup(oneOrMore.getRecordIndex(), 
                            oneOrMore.getDocumentIndexedData(), 
                            true);
		}		
		builder.endLevel();
		builder.oneOrMore(oneOrMore.getRecordIndex(), oneOrMore.getDocumentIndexedData());
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;        
	}
    
	public void visit(Optional optional)  throws SAXException{
		ParsedComponent[] children = optional.getChildren();
		
		if(children == null) {
			//builder.optional(optional.getRecordIndex(), optional.getDocumentIndexedData());
            patternChild = true;			
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = optional.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);

		builder.startLevel();//children
        
        boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;       
		int emptyCount = 0;
		int childrenCount = 0;
		
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                               
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild; 
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }                
            }			
		}
       
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            notAllowedChild = oldNotAllowedChild;
            emptyChild = true;
            emptyComponent = optional;
            patternChild = true;
            attributeContext = oldAttributeContext;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = optional;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            
			return;
		}
		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup(optional.getRecordIndex(), 
                            optional.getDocumentIndexedData(), 
                            true);
		}		
		builder.endLevel();
		builder.optional(optional.getRecordIndex(), optional.getDocumentIndexedData());
		    
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;        
	}	
	public void visit(ListPattern list)  throws SAXException{
		ParsedComponent[] children = list.getChildren();
		
		if(children == null) {
			builder.buildListPattern(list.getRecordIndex(), 
                            list.getDocumentIndexedData());
            patternChild = true;
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = list.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
    
		builder.startLevel();//children
        
		boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;       
		int emptyCount = 0;
		int childrenCount = 0;
				 
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild; 
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }                
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            notAllowedChild = true;
            emptyChild = false;
            patternChild = true;
            attributeContext = oldAttributeContext;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
                 
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = list;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            
			return;
		}
		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup(list.getRecordIndex(), 
                            list.getDocumentIndexedData(),
                            true);
		}		
		builder.endLevel();
		builder.buildListPattern(list.getRecordIndex(), 
                            list.getDocumentIndexedData());
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;        
	}	
	public void visit(Mixed mixed)  throws SAXException{
		ParsedComponent[] children = mixed.getChildren();
		
		if(children == null) {
			builder.buildInterleave(mixed.getRecordIndex(), 
			                        mixed.getDocumentIndexedData());
            patternChild = true;	
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = mixed.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		builder.startLevel();//children
        
		boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;       
		int emptyCount = 0;
		int childrenCount = 0;
		      
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                                               
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild; 
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            notAllowedChild = true;
            emptyChild = false;
            patternChild = true;
            attributeContext = oldAttributeContext;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
                 
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = mixed;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
                 
			return;
		}
		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup(mixed.getRecordIndex(), 
                            mixed.getDocumentIndexedData(), 
                            true);
		}		
		builder.buildText(mixed.getRecordIndex(), mixed.getDocumentIndexedData(), true);
		builder.endLevel();
		builder.buildInterleave(mixed.getRecordIndex(), 
                            mixed.getDocumentIndexedData());
		
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;        
	}	
	
	public void visit(Empty empty){
		emptyChild = true;
		emptyComponent = empty;
        patternChild = true;
	}
	
	public void visit(Text text){		
		builder.buildText(text.getRecordIndex(), text.getDocumentIndexedData(), false);
        patternChild = true;
	}
	public void visit(NotAllowed notAllowed){
		notAllowedChild = true;
        patternChild = true;
	}
	public void visit(ExternalRef externalRef)  throws SAXException{
		Pattern docTopPattern = getReferencedPattern(externalRef);
		if(docTopPattern == null){
			// ? error ?
			// or was that reported already
			// YES it was
            builder.buildRef(null, -1, null, externalRef.getRecordIndex(), externalRef.getDocumentIndexedData());
            patternChild = true;
			return;
		}
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = externalRef.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int index = indexes.get(docTopPattern);	
		if(index != indexes.getNullValue()){			
			if(referencePath.contains(index)){
				SRef sref = builder.buildRef(index, referenceModel, externalRef.getRecordIndex(), externalRef.getDocumentIndexedData());				
				recursionModel.add(sref);
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                patternChild = true;
                attributeContext = oldAttributeContext;
				return;
			}
			emptyChild = definitionEmptyChild.get(index);
			notAllowedChild = definitionNotAllowedChild.get(index);
			if(emptyChild || notAllowedChild){
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                patternChild = true;
                attributeContext = oldAttributeContext;
                return;
            }
			builder.buildRef(definitionTopPatterns.get(index), index, referenceModel, externalRef.getRecordIndex(), externalRef.getDocumentIndexedData());
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
			return;
		}
		
		index = definitionTopPatterns.size();
		
		definitionTopPatterns.add(null);
		definitionEmptyChild.add(false);        
		definitionNotAllowedChild.add(false);
		
		referencePath.push(index);
		indexes.put(docTopPattern, index);
       
		nextLevel(docTopPattern);
		
		SPattern topPattern = builder.getLastContentPattern();				
		definitionTopPatterns.set(index, topPattern);
		builder.clearContent();
				
		definitionEmptyChild.set(index, emptyChild);
		definitionNotAllowedChild.set(index, notAllowedChild);
		
		referencePath.pop();
		
		if(emptyChild || notAllowedChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            return;
        }
		builder.buildRef(topPattern, index, referenceModel, externalRef.getRecordIndex(), externalRef.getDocumentIndexedData());
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;		
	}
	public void visit(Ref ref) throws SAXException{
        String name = ref.getName() == null ? "*" : ref.getName().trim();        
		ArrayList<Definition> definitions = getReferencedDefinition(currentGrammar, name);
		
		if(definitions == null){
			// error 4.18
			String message = "Simplification 4.18 error. "
				+"No correspoding definition was found for element <"+ref.getQName()+"> at "+ref.getLocation(restrictToFileName)+".";
			
			errorDispatcher.error(new SAXParseException(message, null));
            builder.buildRef(null, -1, null, ref.getRecordIndex(), ref.getDocumentIndexedData());
            patternChild = true;
			return;
		}
		
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = ref.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int index = indexes.get(definitions);
		
		if(index != indexes.getNullValue()){
			if(referencePath.contains(index)){
				SRef sref = builder.buildRef(index, referenceModel, ref.getRecordIndex(), ref.getDocumentIndexedData());
				recursionModel.add(sref);
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                patternChild = true;
                attributeContext = oldAttributeContext;
				return;
			}
			emptyChild = definitionEmptyChild.get(index);
			notAllowedChild = definitionNotAllowedChild.get(index);
			if(emptyChild || notAllowedChild){
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                patternChild = true;
                attributeContext = oldAttributeContext;
                return;
            }
			builder.buildRef(definitionTopPatterns.get(index), index, referenceModel, ref.getRecordIndex(), ref.getDocumentIndexedData());
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
			return;
		}
		
		index = definitionTopPatterns.size();
		
		definitionTopPatterns.add(null);
		definitionEmptyChild.add(false);
		definitionNotAllowedChild.add(false);
		
		referencePath.push(index);
		indexes.put(definitions, index);
		
		DefinitionSimplifier ds = pool.getDefinitionSimplifier();
		ds.init(grammarDefinitions,	
			externalRefs,
			docParsedModels,
			definitionTopPatterns,
			namespaceInheritanceHandler,	
			componentAsciiDL,
			asciiDlDatatypeLibrary,
			indexes,
			referencePath,
			definitionEmptyChild,
			definitionNotAllowedChild,
			referenceModel,
			recursionModel,			
			currentGrammar,
			previousGrammars,
            simplificationContext);
		
        ds.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
        ds.setLevel1AttributeIdType(level1AttributeIdType);
        ds.setRestrictToFileName(restrictToFileName);
		ds.simplify(definitions,
		        selements,
                sattributes,
                sexceptPatterns);
		        
        
		SPattern topPattern = ds.getCurrentPattern();
		definitionTopPatterns.set(index, topPattern);
		
		emptyChild = ds.getEmptyChild();
		definitionEmptyChild.set(index, emptyChild);
        
		notAllowedChild = ds.getNotAllowedChild();
		definitionNotAllowedChild.set(index, notAllowedChild);
        		
        ds.recycle();
        
		referencePath.pop();
		
		if(emptyChild || notAllowedChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            return;
        }
		builder.buildRef(topPattern, index, referenceModel, ref.getRecordIndex(), ref.getDocumentIndexedData());	
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;
	}
	public void visit(ParentRef parentRef) throws SAXException{
        String name = parentRef.getName() == null ? "*" : parentRef.getName().trim();        
		ArrayList<Definition> definitions = getReferencedDefinition(previousGrammars.peek(), name);
        
		if(definitions == null){
			// error 4.18
			String message = "Simplification 4.18 error. "
				+"No correspoding definition was found for element <"+parentRef.getQName()+"> at "+parentRef.getLocation(restrictToFileName)+".";
			errorDispatcher.error(new SAXParseException(message, null));
            builder.buildRef(null, -1, null, parentRef.getRecordIndex(), parentRef.getDocumentIndexedData());
            patternChild = true;
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = parentRef.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int index = indexes.get(definitions);
		if(index != indexes.getNullValue()){			
			if(referencePath.contains(index)){
				SRef sref = builder.buildRef(index, referenceModel, parentRef.getRecordIndex(), parentRef.getDocumentIndexedData());
				recursionModel.add(sref);
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                patternChild = true;
                attributeContext = oldAttributeContext;
				return;
			}
			emptyChild = definitionEmptyChild.get(index);
			notAllowedChild = definitionNotAllowedChild.get(index);
			if(emptyChild || notAllowedChild){
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                patternChild = true;
                attributeContext = oldAttributeContext;
                return;
            }
			builder.buildRef(definitionTopPatterns.get(index), index, referenceModel, parentRef.getRecordIndex(), parentRef.getDocumentIndexedData());
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
			return;
		}
		
		index = definitionTopPatterns.size();
		
		definitionTopPatterns.add(null);
		definitionEmptyChild.add(false);
		definitionNotAllowedChild.add(false);
		
		referencePath.push(index);
		indexes.put(definitions, index);
        
		DefinitionSimplifier ds = pool.getDefinitionSimplifier();
		ds.init(grammarDefinitions,	
			externalRefs,
			docParsedModels,
			definitionTopPatterns,
			namespaceInheritanceHandler,	
			componentAsciiDL,
			asciiDlDatatypeLibrary,
			indexes,
			referencePath,
			definitionEmptyChild,
			definitionNotAllowedChild,
			referenceModel,
			recursionModel,			
			currentGrammar,
			previousGrammars,
            simplificationContext);
		
        ds.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
        ds.setLevel1AttributeIdType(level1AttributeIdType);
        ds.setRestrictToFileName(restrictToFileName);
		ds.simplify(definitions,
		        selements,
                sattributes,
                sexceptPatterns);
		
		SPattern topPattern = ds.getCurrentPattern();
		definitionTopPatterns.set(index, topPattern);
		
		emptyChild = ds.getEmptyChild();
		definitionEmptyChild.set(index, emptyChild);
		
		notAllowedChild = ds.getNotAllowedChild();
		definitionNotAllowedChild.set(index, notAllowedChild);
		  		
        ds.recycle();       
        
		referencePath.pop();
		
		if(emptyChild || notAllowedChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            return;
        }
		builder.buildRef(topPattern, index, referenceModel, parentRef.getRecordIndex(), parentRef.getDocumentIndexedData());
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;		
	}
	public void visit(Value value) throws SAXException{
        Map<String, String> prefixMapping = value.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		String type = value.getType();
		String asciiDL;
		if(type == null){
			type = Constants.TOKEN_DT;
			asciiDL = Constants.NATIVE_DATATYPE_LIBRARY;
		}else{			
			type = type.trim();
			asciiDL = componentAsciiDL.get(value);
		}
		String ns = namespaceInheritanceHandler.getNsURI(value);				
		handleValueBuild(value, ns, asciiDL, type);
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
	}	
	private void handleValueBuild(Value value, String ns, String asciiDL, String type) throws SAXException{
		DatatypeLibrary datatypeLibrary = asciiDlDatatypeLibrary.get(asciiDL);
        Datatype datatype = null;
		if(datatypeLibrary == null){
			if(replaceMissingDatatypeLibrary){
                type = Constants.TOKEN_DT;				
                datatypeLibrary = asciiDlDatatypeLibrary.get(Constants.NATIVE_DATATYPE_LIBRARY);
			}else{
				builder.buildValue(ns, null, value.getCharacterContent(), value.getRecordIndex(), value.getDocumentIndexedData());
				return;
			}
		}
		try{
			datatype = datatypeLibrary.createDatatype(type);
			if(datatype == null){
				// error 4.16				
				String message = "Simplification 4.16 error. "
				+"For element <"+value.getQName()+"> at "+value.getLocation(restrictToFileName)
				+"the datatype specified by type attribute, \""+type+"\", is not known "
				+"in the corresponding datatype library \""+value.getDatatypeLibrary()+"\".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}catch(DatatypeException de){
			String message = "Simplification 4.16 error. "
				+"Datatype error for element <"+value.getQName()+"> at "+value.getLocation(restrictToFileName)
				+", datatype library \""+value.getDatatypeLibrary()+"\". "
				+de.getMessage();
				errorDispatcher.error(new SAXParseException(message, null));
		}	
		builder.buildValue(ns, datatype, value.getCharacterContent(), value.getRecordIndex(), value.getDocumentIndexedData());
	}
	
	public void visit(Data data)  throws SAXException{	    
		ParsedComponent[] children = data.getChildren();
		
        Map<String, String> prefixMapping = data.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        		
		if(children != null){
            builder.startLevel();
            paramStack.push(currentParams);
            currentParams = new ArrayList<Param>();
            next(children);
            builder.endLevel();
        }		
		attributeContext = oldAttributeContext;
		
		String type = data.getType();
		String asciiDL;		
		if(type == null){		    
			type = Constants.TOKEN_DT;
			asciiDL = Constants.NATIVE_DATATYPE_LIBRARY;
		}else{			
			type = type.trim();
			asciiDL = componentAsciiDL.get(data);
		}
        if(children != null){
            if(!currentParams.isEmpty()){
                Param[] param  = currentParams.toArray(new Param[currentParams.size()]);
                handleDataBuild(data, asciiDL, type, param);                
            }else{
                handleDataBuild(data, asciiDL, type);
            }
            currentParams = paramStack.pop();
        }else handleDataBuild(data, asciiDL, type);
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
	}

    public void visit(Param param)  throws SAXException{
        currentParams.add(param);
        patternChild = true;
    }	
	private void handleDataBuild(Data data, String asciiDL, String type) throws SAXException{
		DatatypeLibrary datatypeLibrary = asciiDlDatatypeLibrary.get(asciiDL);
        Datatype datatype = null;
		if(datatypeLibrary == null){
			if(replaceMissingDatatypeLibrary){
				type = Constants.TOKEN_DT;
				datatypeLibrary = asciiDlDatatypeLibrary.get(Constants.NATIVE_DATATYPE_LIBRARY);
			}else{
				builder.buildData(null, data.getRecordIndex(), data.getDocumentIndexedData());
				return;
			}
		}		
		try{
			datatype = datatypeLibrary.createDatatype(type);
			if(datatype == null){
				String message = "Simplification 4.16 error. "
				+"For element <"+data.getQName()+"> at "+data.getLocation(restrictToFileName)
				+"the datatype specified by type attribute, \""+type+"\", is not known "
				+"in the corresponding datatype library \""+data.getDatatypeLibrary()+"\".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}catch(DatatypeException de){
			String message = "Simplification 4.16 error. "
				+"Datatype error for element <"+data.getQName()+"> at "+data.getLocation(restrictToFileName)
				+", datatype library \""+data.getDatatypeLibrary()+"\". "
				+de.getMessage();
				errorDispatcher.error(new SAXParseException(message, null));
		}
		builder.buildData(datatype, data.getRecordIndex(), data.getDocumentIndexedData());
	}
    
    private void handleDataBuild(Data data, String asciiDL, String type, Param[] params) throws SAXException{
		DatatypeLibrary datatypeLibrary = asciiDlDatatypeLibrary.get(asciiDL);
        DatatypeBuilder datatypeBuilder = null;
        Datatype datatype = null;
		if(datatypeLibrary == null){
			if(replaceMissingDatatypeLibrary){
				type = Constants.TOKEN_DT;
				datatypeLibrary = asciiDlDatatypeLibrary.get(Constants.NATIVE_DATATYPE_LIBRARY);
			}else{
				builder.buildData(null, data.getRecordIndex(), data.getDocumentIndexedData());
				return;
			}
		}       
		try{
            datatypeBuilder = datatypeLibrary.createDatatypeBuilder(type);            		
			if(datatypeBuilder == null){
				String message = "Simplification 4.16 error. "
				+"For element <"+data.getQName()+"> at "+data.getLocation(restrictToFileName)
				+"the datatype specified by type attribute, \""+type+"\", is not known "
				+"in the corresponding datatype library \""+data.getDatatypeLibrary()+"\".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}catch(DatatypeException de){
			String message = "Simplification 4.16 error. "
				+"Datatype error for element <"+data.getQName()+"> at "+data.getLocation(restrictToFileName)
				+", datatype library \""+data.getDatatypeLibrary()+"\". "
				+de.getMessage();
				errorDispatcher.error(new SAXParseException(message, null));
		}
        
        if(datatypeBuilder != null){
            String ns = namespaceInheritanceHandler.getNsURI(data);
            if(ns == null)ns = "";
            try{
                datatypeBuilder.addParameter(Constants.TARGET_NAMESPACE_NAME, ns, simplificationContext);
            }catch(DatatypeException de){
                //throw new IllegalStateException();
                // Exceptions are thrown for every parameter when the type allows no
                // params at all. Here just do nothing and errors will be reported
                // when processing actual param. This will certainly happen, otherwise 
                // you'd be in another method.
            }
            for(Param param : params){
                String paramNs = param.getNsAttribute();
                boolean localNs = false;
                if(paramNs != null){
                    localNs = true;
                    try{
                        datatypeBuilder.addParameter(Constants.TARGET_NAMESPACE_NAME, paramNs, simplificationContext);
                    }catch(DatatypeException de){
                        //throw new IllegalStateException();
                    }
                }
                try{
                    datatypeBuilder.addParameter(param.getName(), param.getCharacterContent(), simplificationContext);
                }catch(DatatypeException de){
                    String name = param.getName();
                    if(name == null) name = "no name";
                    else name = "name \""+name+"\"";
                    String message = "Simplification 4.16 error. "
                        +"Parameter with "+name+" at "+param.getLocation(restrictToFileName)
                        +", is not allowed in this context. "
                        +de.getMessage();
                        errorDispatcher.error(new SAXParseException(message, null));
                }
                
                if(localNs){
                    try{
                        datatypeBuilder.addParameter(Constants.TARGET_NAMESPACE_NAME, ns, simplificationContext);
                    }catch(DatatypeException de){
                        //throw new IllegalStateException();
                    }
                }
            }
        
            try{
                datatype = datatypeBuilder.createDatatype();
            }catch(DatatypeException de){
                String message = "Simplification 4.16 error. "
                    +"Illegal datatype definition. "
                    +de.getMessage();
                    errorDispatcher.error(new SAXParseException(message, null));
            }
        }
		builder.buildData(datatype, data.getRecordIndex(), data.getDocumentIndexedData());
	}

	
	public void visit(Grammar grammar) throws SAXException{		
		ArrayList<Definition> start = getStart(grammar);		
		if(start == null || start.isEmpty()){
			// error 4.18
			String message = "Simplification 4.18 error. "
				+"No start element was found in the subtree of element <"+grammar.getQName()+"> at "+grammar.getLocation(restrictToFileName)+".";
				//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));            
            simplifyUnreachableDefines(grammar, start);
            if(grammar.getParent() != null){
                builder.buildGrammar(grammar.getRecordIndex(), 
                                    grammar.getDocumentIndexedData());
                patternChild = true;
            }            
			return;
		}
		
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
		previousGrammars.push(currentGrammar);
		currentGrammar = grammar;
		
        Map<String, String> prefixMapping = grammar.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		DefinitionSimplifier ds = pool.getDefinitionSimplifier();
		ds.init(grammarDefinitions,	
			externalRefs,
			docParsedModels,
			definitionTopPatterns,
			namespaceInheritanceHandler,	
			componentAsciiDL,
			asciiDlDatatypeLibrary,
			indexes,
			referencePath,
			definitionEmptyChild,
			definitionNotAllowedChild,
			referenceModel,
			recursionModel,			
			currentGrammar,
			previousGrammars,
            simplificationContext);
		ds.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
        ds.setLevel1AttributeIdType(level1AttributeIdType);
        ds.setRestrictToFileName(restrictToFileName);
		ds.simplify(start,
		        selements,
                sattributes,
                sexceptPatterns);		
		
		notAllowedChild = ds.getNotAllowedChild();
		
		if(notAllowedChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            ds.recycle();
            return;
        }
		
		emptyChild = ds.getEmptyChild();		
		if(emptyChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
            ds.recycle();
            return;
        }
		
		SPattern[] topPattern = ds.getAllCurrentPatterns();
        
        ds.recycle();
		
        simplifyUnreachableDefines(grammar, start);
        
		if(grammar.getParent() != null){
			builder.startLevel();
			builder.addAllToCurrentLevel(topPattern);
			builder.endLevel();
			builder.buildGrammar(grammar.getRecordIndex(), 
                            grammar.getDocumentIndexedData());
            
            currentGrammar = previousGrammars.pop();
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
			return;
		}
        
		builder.addAllToCurrentLevel(topPattern);
                
		currentGrammar = previousGrammars.pop();
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;
	}
	
    private void simplifyUnreachableDefines(Grammar grammar, ArrayList<Definition> start) throws SAXException{
        Map<String, ArrayList<Definition>> currentDefinitions = grammarDefinitions.get(grammar);
        if(currentDefinitions != null){
            UnreachableDefinitionSimplifier uds = pool.getUnreachableDefinitionSimplifier();
            uds.init(grammarDefinitions,	
                    externalRefs,
                    docParsedModels,
                    definitionTopPatterns,
                    namespaceInheritanceHandler,	
                    componentAsciiDL,
                    asciiDlDatatypeLibrary,
                    indexes,
                    referencePath,
                    definitionEmptyChild,
                    definitionNotAllowedChild,
                    referenceModel,
                    recursionModel,			
                    grammar,
                    previousGrammars,
                    simplificationContext);
            uds.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
            uds.setLevel1AttributeIdType(level1AttributeIdType);
            uds.setRestrictToFileName(restrictToFileName);            	
                
            Collection<ArrayList<Definition>> definitions = currentDefinitions.values();
            int NULL = indexes.getNullValue();
            for(ArrayList<Definition> definition : definitions){
                if(definition != start){
                    int index = indexes.get(definition);
                    if(index == NULL){
                        uds.simplify(definition);
                        unreachableDefinitionWarning(definition);
                    }
                }            
            }
            
            uds.recycle();
        }
    }
    
    void unreachableDefinitionWarning(ArrayList<Definition> definitions) throws SAXException{
        String message = "Simplification 4.19 warning. Unreachable definitions:";
        for(Definition def : definitions){
            message += "\n<"+def.getQName()+"> at "+def.getLocation(restrictToFileName);
        }
        message += ".\nRemoved before checking for illegal recursions and restrictions control.";
        errorDispatcher.warning(new SAXParseException(message, null));
    }
    
	public void visit(Dummy dummy) throws SAXException{
		ParsedComponent[] children = dummy.getChildren();
		
		if(children == null) {
			builder.buildDummy(dummy.getRecordIndex(), dummy.getDocumentIndexedData());
            patternChild = true;			
			return;
		}
        
        boolean oldAttributeContext = attributeContext;
        attributeContext = false;
        
        Map<String, String> prefixMapping = dummy.getXmlns();        
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        		
		builder.startLevel();//children
        
		boolean oldNotAllowedChild = notAllowedChild;
        boolean notAllowed = false;       
		int emptyCount = 0;
		int childrenCount = 0;
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                notAllowedChild = false;
                next(children[i]);
                if(patternChild){
                    childrenCount++;
                    if(!notAllowed) notAllowed = notAllowedChild; 
                    if(emptyChild){
                        emptyCount++;
                        emptyChild = false;
                    }
                }
            }			
		}		
        if(notAllowed){				
            builder.endLevel();
            builder.clearContent();
            notAllowedChild = true;
            emptyChild = false;
            patternChild = true;
            attributeContext = oldAttributeContext;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            return;
        }
        notAllowedChild = oldNotAllowedChild;
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();
			emptyChild = true;
			emptyComponent = dummy;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            patternChild = true;
            attributeContext = oldAttributeContext;
			return;
		}		
				
		builder.endLevel();
		builder.buildDummy(dummy.getRecordIndex(), dummy.getDocumentIndexedData());
		
		notAllowedChild = false;
		emptyChild = false;
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
        patternChild = true;
        attributeContext = oldAttributeContext;
	}

    public void visit(ForeignComponent fc) throws SAXException{
	}	
		
	void nextLevel(ParsedComponent[] children)  throws SAXException{
		builder.startLevel();
		for(ParsedComponent child : children){            
			child.accept(this);
		}
		builder.endLevel();
	}
	
	void nextLevel(ParsedComponent child)  throws SAXException{
		builder.startLevel();
		child.accept(this);
		builder.endLevel();
	}
	
	void next(ParsedComponent[] children)  throws SAXException{
		for(ParsedComponent child : children){            
			child.accept(this);
		}
	}
	
	void next(ParsedComponent child)  throws SAXException{
		child.accept(this);
	}
	
	String getPrefix(String qname){
		int i = qname.indexOf(':'); 
		if(i<0)return null;
		return qname.substring(0, i);
	}
	
	String getLocalPart(String qname){
		int i = qname.indexOf(':'); 
		if(i<0)return qname;
		return qname.substring(i+1);
	}
	
	ArrayList<Definition> getStart(Grammar grammar){
		Map<String, ArrayList<Definition>> definitions = grammarDefinitions.get(grammar);
		if(definitions == null) return null;
		return definitions.get(null);
	}
		
	ArrayList<Definition> getReferencedDefinition(Grammar grammar, String name){
		Map<String, ArrayList<Definition>> definitions = grammarDefinitions.get(grammar);
		if(definitions == null) return null;
		return definitions.get(name);
	}
	
	Pattern getReferencedPattern(ExternalRef ref){
		URI uri = externalRefs.get(ref);
		if(uri == null)return null;
		//docParsedModels.get(uri).write();
		ParsedModel pm = docParsedModels.get(uri);
		if(pm == null) return null;
		return pm.getTopPattern();
	}

    void startXmlnsContext(Map<String, String> prefixMapping){
        Set<String> prefixes = prefixMapping.keySet();
        for(String prefix : prefixes){
            String namespace = prefixMapping.get(prefix);
            simplificationContext.startPrefixMapping(prefix, namespace);
        }
    }

    void endXmlnsContext(Map<String, String> prefixMapping){
        Set<String> prefixes = prefixMapping.keySet();
        for(String prefix : prefixes){
            simplificationContext.endPrefixMapping(prefix);
        }
    }

    /*String getDefaultValue(AttributeInfo[] foreignAttributes){
        for(AttributeInfo attributeInfo : foreignAttributes){
            String uri = attributeInfo.getNamespaceURI(); 
            String ln = attributeInfo.getLocalName();
            if( uri != null && uri.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE)
                && ln != null && ln.equals(Constants.DTD_COMPATIBILITY_DEFAULT_VALUE)) 
            return attributeInfo.getValue();
        }
    
        return null;
    }*/	
}
