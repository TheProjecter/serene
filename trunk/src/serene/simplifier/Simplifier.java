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
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;

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

import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.SimplifiedComponentBuilder;

import serene.validation.schema.simplified.components.SNameClass;
import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SAttribute;

import serene.internal.InternalRNGFactory;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.util.AttributeInfo;

import sereneWrite.MessageWriter;
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
	
	
	ObjectIntHashMap indexes;
	IntStack referencePath;
	BooleanList definitionEmptyChild;
	BooleanList definitionNotAllowedChild;	
	RecursionModel recursionModel;
		
	Grammar currentGrammar;
	Stack<Grammar> previousGrammars;
	
	boolean emptyChild;
	ParsedComponent emptyComponent;
	boolean notAllowedChild;
	
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
	boolean replaceMissingDatatypeLibrary;
     
		
	MessageWriter debugWriter;
	ParsedComponentWriter pcw;
	
	Simplifier(ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		this.debugWriter = debugWriter;				
		this.errorDispatcher = errorDispatcher;

		builder = new SimplifiedComponentBuilder(debugWriter);		
		pcw = new ParsedComponentWriter();
		
		replaceMissingDatatypeLibrary =  true;
        level1AttributeDefaultValue  = false;
        
        paramStack = new Stack<ArrayList<Param>>();
	}
	
	public abstract void setReplaceMissingDatatypeLibrary(boolean value);
	public void setLevel1AttributeDefaultValue(boolean level1AttributeDefaultValue){
        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
    }
    
    
	public void visit(Include include){
		throw new IllegalStateException();
	}
	public void visit(ExceptPattern exceptPattern) throws SAXException{
		ParsedComponent[] children = exceptPattern.getChildren();
		if(children == null) {
			builder.buildExceptPattern(exceptPattern.getQName(), exceptPattern.getLocation());
			return;
		}
        Map<String, String> prefixMapping = exceptPattern.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int childrenCount = children.length;
		int allowedChildrenCount = childrenCount;
		int emptyCount = 0;
		builder.startLevel();        
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild) {
                    allowedChildrenCount--;
                    notAllowedChild = false;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }
			else{
				childrenCount--;
				allowedChildrenCount--;				
			}
		}		
		if(allowedChildrenCount == 0){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}
		if(emptyCount > 0){
			builder.buildEmpty("empty", emptyComponent.getLocation());
			allowedChildrenCount = allowedChildrenCount - emptyCount + 1;					
		}
        if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementChoicePattern("choice added by except simplification", exceptPattern.getLocation());
		}
		builder.endLevel();
		builder.buildExceptPattern( exceptPattern.getQName(), exceptPattern.getLocation());
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(ExceptNameClass exceptNameClass) throws SAXException{
        ParsedComponent[] children = exceptNameClass.getChildren();
        if(children == null) {
			builder.buildExceptNameClass(exceptNameClass.getQName(), exceptNameClass.getLocation());
			return;
		}        
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
			builder.buildReplacementChoiceNameClass("choice added by except simplification", exceptNameClass.getLocation());
		}
        builder.endLevel();
		builder.buildExceptNameClass(exceptNameClass.getQName(), exceptNameClass.getLocation());
		
		anyNameExceptContext = false;	
		nsNameExceptContext = false;
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
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
				+"Illegal character content of element <"+name.getQName()+"> at "+name.getLocation()+".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
			if(ns.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)){
				// 4.16 error
				String message = "Simplification 4.16 error. "
				+"Illegal ns of element <"+name.getQName()+"> at "+name.getLocation()+".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}
		builder.buildName(ns, localPart, name.getQName(), name.getLocation());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
    
	public void visit(AnyName anyName) throws SAXException{
		if(anyNameExceptContext){
			// 4.16 error
			String message = "Simplification 4.16 error. "
				+"Presence of element <"+anyName.getQName()+"> at "+anyName.getLocation()+" is forbiden in this context.";
			errorDispatcher.error(new SAXParseException(message, null));
			// TODO Q: do you need to build a dummy?
			return;
		}
		if(nsNameExceptContext){
			// 4.16 error
			String message = "Simplification 4.16 error. "
				+"Presence of element <"+anyName.getQName()+"> at "+anyName.getLocation()+" is forbiden in this context.";
				//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			// TODO Q: do you need to build a dummy?
			return;
		}	
        Map<String, String> prefixMapping = anyName.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		anyNameContext = true;
		ParsedComponent[] children = anyName.getChildren();
		if(children != null) nextLevel(children);
		anyNameContext = false;
		
		builder.buildAnyName(anyName.getQName(), anyName.getLocation());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(NsName nsName) throws SAXException{
		if(nsNameExceptContext){
			// 4.16 error
			String message = "Simplification 4.16 error. "
				+"Presence of element <"+nsName.getQName()+"> at "+nsName.getLocation()+" is forbiden in this context.";
			errorDispatcher.error(new SAXParseException(message, null));
			// TODO Q: do you need to build a dummy?
			return;
		}        
		String ns = namespaceInheritanceHandler.getNsURI(nsName);	
		if(ns == null)ns ="";
		if(attributeContext){			
			if(ns.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)){
				// 4.16 error
				String message = "Simplification 4.16 error. "
				+"Illegal ns of element <"+nsName.getQName()+"> at "+nsName.getLocation()+".";
				errorDispatcher.error(new SAXParseException(message, null));
				// TODO Q: do you need to build a dummy?
				return;
			}
		}
		
        Map<String, String> prefixMapping = nsName.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		nsNameContext = true;
		ParsedComponent[] children = nsName.getChildren();
		if(children != null) nextLevel(children);
		nsNameContext = false;
				
		builder.buildNsName(ns, nsName.getQName(), nsName.getLocation());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
    
	public void visit(ChoiceNameClass choice) throws SAXException{
        Map<String, String> prefixMapping = choice.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		ParsedComponent[] children = choice.getChildren();
		if(children != null) nextLevel(children);
		builder.buildChoiceNameClass(choice.getQName(), choice.getLocation());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}	
	
	public void visit(Define define) throws SAXException{		
		ParsedComponent[] children = define.getChildren();
			
		if(children == null) {			
			return;
		}
        
        namespaceInheritanceHandler.startXmlnsContext(simplificationContext, define);		
        int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){				
                    builder.endLevel();
                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
                    //notAllowedChild = true;
                    emptyChild = false;
                    namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
                    return;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else childrenCount--;			
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = define;
            namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
			return;
		}
		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup("group added by define simplification", define.getLocation());			
		}		
		builder.endLevel();
		// this is the define level, nothing gets built
		// it is kept for combining
        namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
	}
	public void visit(Start start)  throws SAXException{
		ParsedComponent[] children = start.getChildren();
			
		if(children == null) {			
			return;
		}				
        namespaceInheritanceHandler.startXmlnsContext(simplificationContext, start);
		builder.startLevel();
		next(children);
		if(notAllowedChild){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			builder.endLevel();
			// notAllowedChild = true;
			emptyChild = false;
            namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);
			return;
		}
		if(emptyChild){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			//emptyChild = true;
			//notAllowedChild = false;
            namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);
			return;
		}		
		builder.endLevel();
		// this is the start level, nothing gets built
		// it is kept for combining
        namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);
	}
		
	public void visit(ElementWithNameClass element)  throws SAXException{
		ParsedComponent[] children = element.getChildren();		
		        		
		if(children == null) {			
			builder.buildElement(element.getQName(), element.getLocation());
			return;
		}	
        
        Map<String, String> prefixMapping = element.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int notAllowedCount = 0;
        builder.startLevel();
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){
                    notAllowedCount++;
                    notAllowedChild = false;
                }
                if(emptyChild){
                    emptyChild = false;
                }
            }
		}		
		if(notAllowedCount > 0){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}        
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup("group added by element simplification", element.getLocation());
		}
		builder.endLevel();
		builder.buildElement(element.getQName(), element.getLocation());
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}	
	public void visit(ElementWithNameInstance element)  throws SAXException{
		ParsedComponent[] children = element.getChildren();
		        
		if(children == null) {		
			builder.buildElement(element.getQName(), element.getLocation());
			return;
		}

        Map<String, String> prefixMapping = element.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
		
		int notAllowedCount = 0;
		builder.startLevel();//children
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){
                    notAllowedCount++;
                    notAllowedChild = false;
                }
                if(emptyChild){
                    emptyChild = false;
                }
            }
		}		
		if(notAllowedCount > 0){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
            
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup("group added by element simplification", element.getLocation());
		}
		String name = element.getName().trim();
		String prefix = getPrefix(name);
		String localPart = getLocalPart(name);
		String ns;
		if(prefix != null) ns = simplificationContext.resolveNamespacePrefix(prefix);
        else ns = namespaceInheritanceHandler.getNsURI(element);
		if(ns == null)ns = "";		
		builder.buildName(ns, localPart, "name", element.getLocation());
		
		builder.endLevel();
		builder.buildElement(element.getQName(), element.getLocation());
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}	
	public void visit(AttributeWithNameClass attribute)  throws SAXException{
		ParsedComponent[] children = attribute.getChildren();		
		
        Map<String, String> prefixMapping = attribute.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
                
        if(children == null){
            builder.startLevel();
            builder.buildText("default text", attribute.getLocation());
			builder.endLevel();
            if(level1AttributeDefaultValue){
                AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
                if(foreignAttributes != null){
                    builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
                }else{
                    builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
                }                
            }else{
                builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
            }
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            return;
        }
        
		builder.startLevel();		
		next(children);
		if(notAllowedChild){			
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			//notAllowedChild = true;
			emptyChild = false;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}
		if(emptyChild){
            builder.buildEmpty("empty", emptyComponent.getLocation());
            emptyChild = false;
		}else if(builder.getCurrentPattern() == null){
            builder.buildText("default text", attribute.getLocation());            
        }       
        
		builder.endLevel();
		if(level1AttributeDefaultValue){
            AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
            if(foreignAttributes != null){
                builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
            }else{
                builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
            }                
        }else{
            builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
        }
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(AttributeWithNameInstance attribute)  throws SAXException{
		ParsedComponent[] children = attribute.getChildren();		
		
        Map<String, String> prefixMapping = attribute.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		builder.startLevel();
		if(children == null) {
			//4.12
			builder.buildText("default text", attribute.getLocation());
			String name = attribute.getName().trim();		
			String prefix = getPrefix(name);
			String localPart = getLocalPart(name);
			String ns;
			if(prefix != null)ns = simplificationContext.resolveNamespacePrefix(prefix);
			else ns = namespaceInheritanceHandler.getNsURI(attribute);
			if(ns == null)ns = "";		
			builder.buildName(ns, localPart, "name", attribute.getLocation());
			builder.endLevel();
			if(level1AttributeDefaultValue){
                AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
                if(foreignAttributes != null){
                    builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
                }else{
                    builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
                }                
            }else{
                builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
            }
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}				
		if(children != null)next(children);
		if(notAllowedChild){			
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			//notAllowedChild = true;
			emptyChild = false;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}
		
		if(emptyChild){
            builder.buildEmpty("empty", emptyComponent.getLocation());
            emptyChild = false;
		}				
		
		String name = attribute.getName().trim();		
		String prefix = getPrefix(name);
		String localPart = getLocalPart(name);
		String ns;
		if(prefix != null)ns = simplificationContext.resolveNamespacePrefix(prefix);
		else ns = namespaceInheritanceHandler.getNsURI(attribute);
		if(ns == null)ns = "";
		if(ns.equals("") && localPart.equals("xmlns")){				 
			// 4.16 error
			String message = "Simplification 4.16 error. "
			+"Illegal value of name attribute of element <"+attribute.getQName()+"> at "+attribute.getLocation()+".";
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(ns.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)){
			// 4.16 error
			String message = "Simplification 4.16 error. "
			+"Illegal ns of element <"+attribute.getQName()+"> at "+attribute.getLocation()+".";
			errorDispatcher.error(new SAXParseException(message, null));
		}		
		builder.buildName(ns, localPart, "name", attribute.getLocation());
		
		builder.endLevel();
		if(level1AttributeDefaultValue){
            AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
            if(foreignAttributes != null){
                builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
            }else{
                builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
            }                
        }else{
            builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
        }
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(ChoicePattern choice)  throws SAXException{
		ParsedComponent[] children = choice.getChildren();	
		if(children == null){
			builder.buildChoicePattern(choice.getQName(), choice.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = choice.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int childrenCount = children.length;
		int allowedChildrenCount = childrenCount;
		int emptyCount = 0;
		builder.startLevel();
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){
                    allowedChildrenCount--;
                    notAllowedChild = false;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else{
				childrenCount--;
				allowedChildrenCount--;
			}			
		}		
		if(allowedChildrenCount == 0){						
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			notAllowedChild = true;
			emptyChild = false;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}
		if(emptyCount == allowedChildrenCount){			
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = choice;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}
		if(emptyCount > 0){
			builder.buildEmpty("empty", emptyComponent.getLocation());
		}
		builder.endLevel();
		builder.buildChoicePattern(choice.getQName(), choice.getLocation());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(Interleave interleave) throws SAXException{
		ParsedComponent[] children = interleave.getChildren();
		
		if(children == null) {
			builder.buildInterleave(interleave.getQName(), interleave.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = interleave.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){				
                    builder.endLevel();
                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
                    //notAllowedChild = true;
                    emptyChild = false;                
                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
                    return;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else childrenCount--;			
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = interleave;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}	
		
		builder.endLevel();
		builder.buildInterleave(interleave.getQName(), interleave.getLocation());
		
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(Group group)  throws SAXException{
		ParsedComponent[] children = group.getChildren();
		
		if(children == null) {
			builder.buildGroup(group.getQName(), group.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = group.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){				
                    builder.endLevel();
                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
                    //notAllowedChild = true;
                    emptyChild = false;
                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
                    return;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else childrenCount--;			
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = group;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}
		
		builder.endLevel();
		builder.buildGroup(group.getQName(), group.getLocation());
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(ZeroOrMore zeroOrMore)  throws SAXException{
		ParsedComponent[] children = zeroOrMore.getChildren();
		
		if(children == null) {
			builder.buildZeroOrMore(zeroOrMore.getQName(), zeroOrMore.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = zeroOrMore.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();//children
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){				
                    builder.endLevel();
                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
                    //notAllowedChild = true;
                    emptyChild = false;
                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
                    return;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else childrenCount--;			
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = zeroOrMore;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup("group added by zerOrMore simplification", zeroOrMore.getLocation());
		}	
		builder.endLevel();
		builder.buildZeroOrMore(zeroOrMore.getQName(), zeroOrMore.getLocation());
        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(OneOrMore oneOrMore)  throws SAXException{
		ParsedComponent[] children = oneOrMore.getChildren();
		
		if(children == null) {
			builder.buildOneOrMore(oneOrMore.getQName(), oneOrMore.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = oneOrMore.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();//children
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){				
                    builder.endLevel();
                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
                    //notAllowedChild = true;
                    emptyChild = false;
                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
                    return;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else childrenCount--;			
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = oneOrMore;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup("group added by oneOrMore simplification", oneOrMore.getLocation());
		}		
		builder.endLevel();
		builder.buildOneOrMore(oneOrMore.getQName(), oneOrMore.getLocation());
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(Optional optional)  throws SAXException{
		ParsedComponent[] children = optional.getChildren();
		
		if(children == null) {
			builder.buildOptional(optional.getQName(), optional.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = optional.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();//children
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){				
                    builder.endLevel();
                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
                    //notAllowedChild = true;
                    emptyChild = false;
                    
                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
                    return;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else childrenCount--;			
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = optional;            
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup("group added by optional simplification", optional.getLocation());
		}		
		builder.endLevel();
		builder.buildOptional(optional.getQName(), optional.getLocation());
		    
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}	
	public void visit(ListPattern list)  throws SAXException{
		ParsedComponent[] children = list.getChildren();
		
		if(children == null) {
			builder.buildListPattern(list.getQName(), list.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = list.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();//children
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){				
                    builder.endLevel();
                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
                    //notAllowedChild = true;
                    emptyChild = false;
                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
                    return;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else childrenCount--;			
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = list;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup("group added by list simplification", list.getLocation());
		}		
		builder.endLevel();
		builder.buildListPattern(list.getQName(), list.getLocation());
		        
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}	
	public void visit(Mixed mixed)  throws SAXException{
		ParsedComponent[] children = mixed.getChildren();
		
		if(children == null) {
			builder.buildMixed(mixed.getQName(), mixed.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = mixed.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();//children
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
                next(children[i]);
                if(notAllowedChild){				
                    builder.endLevel();
                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
                    //notAllowedChild = true;
                    emptyChild = false;
                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
                    return;
                }
                if(emptyChild){
                    emptyCount++;
                    emptyChild = false;
                }
            }else childrenCount--;			
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			builder.buildText("default text", mixed.getLocation());
			emptyChild = false;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}		
		if(builder.getCurrentPatternsCount() > 1){
			builder.buildReplacementGroup("group added by mixed simplification", mixed.getLocation());
		}		
		builder.endLevel();
		builder.buildMixed(mixed.getQName(), mixed.getLocation());
		
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}	
	
	public void visit(Empty empty){
		emptyChild = true;
		emptyComponent = empty;
	}
	
	public void visit(Text text){		
		builder.buildText(text.getQName(), text.getLocation());
	}
	public void visit(NotAllowed notAllowed){
		notAllowedChild = true;
	}
	public void visit(ExternalRef externalRef)  throws SAXException{
		Pattern docTopPattern = getReferencedPattern(externalRef);		
		if(docTopPattern == null){
			// ? error ?
			// or was that reported already
			// YES it was
			return;
		}
        Map<String, String> prefixMapping = externalRef.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int index = indexes.get(docTopPattern);
		if(index != indexes.getNullValue()){			
			if(referencePath.contains(index)){
				builder.buildRef(index, externalRef.getQName(), externalRef.getLocation());				
				recursionModel.add(builder.getCurrentPattern());
				if(!recursionModel.isRecursiveDefinition(index)) recursionModel.add(index);
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
				return;
			}
			emptyChild = definitionEmptyChild.get(index);
			notAllowedChild = definitionNotAllowedChild.get(index);
			if(emptyChild || notAllowedChild){
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                return;
            }
			builder.buildRef(index, externalRef.getQName(), externalRef.getLocation());
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
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
            return;
        }
		builder.buildRef(index, externalRef.getQName(), externalRef.getLocation());
        if(prefixMapping != null) endXmlnsContext(prefixMapping);	
	}
	public void visit(Ref ref) throws SAXException{
		ArrayList<Definition> definitions = getReferencedDefinition(currentGrammar, ref.getName().trim());
		
		if(definitions == null){
			// error 4.18
			String message = "Simplification 4.18 error. "
				+"No correspoding define was found for element <"+ref.getQName()+"> at "+ref.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			return;
		}
		
        Map<String, String> prefixMapping = ref.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int index = indexes.get(definitions);
		
		if(index != indexes.getNullValue()){			
			if(referencePath.contains(index)){
				builder.buildRef(index, ref.getQName(), ref.getLocation());
				recursionModel.add(builder.getCurrentPattern());
				if(!recursionModel.isRecursiveDefinition(index)) recursionModel.add(index);
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
				return;
			}
			emptyChild = definitionEmptyChild.get(index);
			notAllowedChild = definitionNotAllowedChild.get(index);
			if(emptyChild || notAllowedChild){
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                return;
            }
			builder.buildRef(index, ref.getQName(), ref.getLocation());
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
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
			recursionModel,			
			currentGrammar,
			previousGrammars,
            simplificationContext);
		
        ds.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
		ds.simplify(definitions);
		
		SPattern topPattern = ds.getCurrentPattern();
		definitionTopPatterns.set(index, topPattern);
		
		emptyChild = ds.getEmptyChild();
		definitionEmptyChild.set(index, emptyChild);
		
		notAllowedChild = ds.getNotAllowedChild();
		definitionNotAllowedChild.set(index, notAllowedChild);
		
		referencePath.pop();
		
		if(emptyChild || notAllowedChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            return;
        }
		builder.buildRef(index, ref.getQName(), ref.getLocation());	
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	public void visit(ParentRef parentRef) throws SAXException{
		ArrayList<Definition> definitions = getReferencedDefinition(previousGrammars.peek(), parentRef.getName().trim());
		if(definitions == null){
			// error 4.18
			String message = "Simplification 4.18 error. "
				+"No correspoding define was found for element <"+parentRef.getQName()+"> at "+parentRef.getLocation()+".";
			errorDispatcher.error(new SAXParseException(message, null));
			return;
		}
        
        Map<String, String> prefixMapping = parentRef.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int index = indexes.get(definitions);
		if(index != indexes.getNullValue()){			
			if(referencePath.contains(index)){
				builder.buildRef(index, parentRef.getQName(), parentRef.getLocation());
				recursionModel.add(builder.getCurrentPattern());
				if(!recursionModel.isRecursiveDefinition(index)) recursionModel.add(index);
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
				return;
			}
			emptyChild = definitionEmptyChild.get(index);
			notAllowedChild = definitionNotAllowedChild.get(index);
			if(emptyChild || notAllowedChild){
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
                return;
            }
			builder.buildRef(index, parentRef.getQName(), parentRef.getLocation());
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
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
			recursionModel,			
			currentGrammar,
			previousGrammars,
            simplificationContext);
		
        ds.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
		ds.simplify(definitions);
		
		SPattern topPattern = ds.getCurrentPattern();
		definitionTopPatterns.set(index, topPattern);
		
		emptyChild = ds.getEmptyChild();
		definitionEmptyChild.set(index, emptyChild);
		
		notAllowedChild = ds.getNotAllowedChild();
		definitionNotAllowedChild.set(index, notAllowedChild);
		
        
		referencePath.pop();
		
		if(emptyChild || notAllowedChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            return;
        }
		builder.buildRef(index, parentRef.getQName(), parentRef.getLocation());
        if(prefixMapping != null) endXmlnsContext(prefixMapping);	
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
	}	
	private void handleValueBuild(Value value, String ns, String asciiDL, String type) throws SAXException{
		DatatypeLibrary datatypeLibrary = asciiDlDatatypeLibrary.get(asciiDL);
        Datatype datatype = null;
		if(datatypeLibrary == null){
			if(replaceMissingDatatypeLibrary){
                type = Constants.TOKEN_DT;				
                datatypeLibrary = asciiDlDatatypeLibrary.get(Constants.NATIVE_DATATYPE_LIBRARY);
			}else{
				builder.buildValue(ns, null, value.getCharacterContent(), value.getQName(), value.getLocation());
				return;
			}
		}
		try{
			datatype = datatypeLibrary.createDatatype(type);
			if(datatype == null){
				// error 4.16				
				String message = "Simplification 4.16 error. "
				+"For element <"+value.getQName()+"> at "+value.getLocation()
				+"the datatype specified by type attribute, \""+type+"\", is not known "
				+"in the corresponding datatype library \""+value.getDatatypeLibrary()+"\".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}catch(DatatypeException de){
			String message = "Simplification 4.16 error. "
				+"Datatype error for element <"+value.getQName()+"> at "+value.getLocation()
				+", datatype library \""+value.getDatatypeLibrary()+"\". "
				+de.getMessage();
				errorDispatcher.error(new SAXParseException(message, null));
		}		
		builder.buildValue(ns, datatype, value.getCharacterContent(), value.getQName(), value.getLocation());
	}
	
	public void visit(Data data)  throws SAXException{
		ParsedComponent[] children = data.getChildren();
		
        Map<String, String> prefixMapping = data.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		builder.startLevel();
		if(children != null){
            paramStack.push(currentParams);
            currentParams = new ArrayList<Param>();
            next(children);
        }
		builder.endLevel();
				
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
	}

    public void visit(Param param)  throws SAXException{
        currentParams.add(param);
    }	
	private void handleDataBuild(Data data, String asciiDL, String type) throws SAXException{
		DatatypeLibrary datatypeLibrary = asciiDlDatatypeLibrary.get(asciiDL);
        Datatype datatype = null;
		if(datatypeLibrary == null){
			if(replaceMissingDatatypeLibrary){
				type = Constants.TOKEN_DT;
				datatypeLibrary = asciiDlDatatypeLibrary.get(Constants.NATIVE_DATATYPE_LIBRARY);
			}else{
				builder.buildData(null, data.getQName(), data.getLocation());
				return;
			}
		}		
		try{
			datatype = datatypeLibrary.createDatatype(type);
			if(datatype == null){
				String message = "Simplification 4.16 error. "
				+"For element <"+data.getQName()+"> at "+data.getLocation()
				+"the datatype specified by type attribute, \""+type+"\", is not known "
				+"in the corresponding datatype library \""+data.getDatatypeLibrary()+"\".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}catch(DatatypeException de){
			String message = "Simplification 4.16 error. "
				+"Datatype error for element <"+data.getQName()+"> at "+data.getLocation()
				+", datatype library \""+data.getDatatypeLibrary()+"\". "
				+de.getMessage();
				errorDispatcher.error(new SAXParseException(message, null));
		}
		builder.buildData(datatype, data.getQName(), data.getLocation());
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
				builder.buildData(null, data.getQName(), data.getLocation());
				return;
			}
		}       
		try{
            datatypeBuilder = datatypeLibrary.createDatatypeBuilder(type);            		
			if(datatypeBuilder == null){
				String message = "Simplification 4.16 error. "
				+"For element <"+data.getQName()+"> at "+data.getLocation()
				+"the datatype specified by type attribute, \""+type+"\", is not known "
				+"in the corresponding datatype library \""+data.getDatatypeLibrary()+"\".";
				errorDispatcher.error(new SAXParseException(message, null));
			}
		}catch(DatatypeException de){
			String message = "Simplification 4.16 error. "
				+"Datatype error for element <"+data.getQName()+"> at "+data.getLocation()
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
                    String message = "Simplification 4.16 error. "
                        +"Parameter with the name \""+param.getName()+"\" at "+param.getLocation()
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
		builder.buildData(datatype, data.getQName(), data.getLocation());
	}

	
	public void visit(Grammar grammar) throws SAXException{		
		ArrayList<Definition> start = getStart(grammar);		
		if(start == null){
			// error 4.18
			String message = "Simplification 4.18 error. "
				+"No start element was found in the subtree of element <"+grammar.getQName()+"> at "+grammar.getLocation()+".";
				//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			return;
		}
		
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
			recursionModel,			
			currentGrammar,
			previousGrammars,
            simplificationContext);
		ds.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
		ds.simplify(start);		
		
		notAllowedChild = ds.getNotAllowedChild();
		if(notAllowedChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            return;
        }
		
		emptyChild = ds.getEmptyChild();		
		if(emptyChild){
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
            return;
        }
		
		SPattern[] topPattern = ds.getAllCurrentPatterns();		
		if(grammar.getParent() != null){
			builder.startLevel();
			builder.addAllToCurrentLevel(topPattern);
			builder.endLevel();
			builder.buildGrammar(grammar.getQName(), grammar.getLocation());
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}
		builder.addAllToCurrentLevel(topPattern);
		
		currentGrammar = previousGrammars.pop();
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}
	
	public void visit(Dummy dummy) throws SAXException{
		ParsedComponent[] children = dummy.getChildren();
		
		if(children == null) {
			builder.buildDummy(dummy.getQName(), dummy.getLocation());
			return;
		}
        
        Map<String, String> prefixMapping = dummy.getXmlns();        
        if(prefixMapping != null) startXmlnsContext(prefixMapping);
        
		int emptyCount = 0;
		int childrenCount = children.length;
		builder.startLevel();//children
		for(int i = 0; i < children.length; i++){
			if(children[i] != null)next(children[i]);
			else childrenCount--;
			if(notAllowedChild){				
				builder.endLevel();
				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
				//notAllowedChild = true;
				emptyChild = false;
                if(prefixMapping != null) endXmlnsContext(prefixMapping);
				return;
			}
			if(emptyChild){
				emptyCount++;
				emptyChild = false;
			}
		}		
		if(emptyCount == childrenCount){
			builder.endLevel();
			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
			emptyChild = true;
			emptyComponent = dummy;
			notAllowedChild = false;
            if(prefixMapping != null) endXmlnsContext(prefixMapping);
			return;
		}		
				
		builder.endLevel();
		builder.buildDummy(dummy.getQName(), dummy.getLocation());
		
		notAllowedChild = false;
		emptyChild = false;
        if(prefixMapping != null) endXmlnsContext(prefixMapping);
	}

    public void visit(ForeignComponent fc) throws SAXException{
		System.out.println("TODO simplify "+fc);
	}	
		
	private void nextLevel(ParsedComponent[] children)  throws SAXException{
		builder.startLevel();
		for(ParsedComponent child : children){            
			child.accept(this);
		}
		builder.endLevel();
	}
	
	private void nextLevel(ParsedComponent child)  throws SAXException{
		builder.startLevel();
		child.accept(this);
		builder.endLevel();
	}
	
	private void next(ParsedComponent[] children)  throws SAXException{
		for(ParsedComponent child : children){            
			child.accept(this);
		}
	}
	
	private void next(ParsedComponent child)  throws SAXException{
		child.accept(this);
	}
	
	private String getPrefix(String qname){
		int i = qname.indexOf(':'); 
		if(i<0)return null;
		return qname.substring(0, i);
	}
	
	private String getLocalPart(String qname){
		int i = qname.indexOf(':'); 
		if(i<0)return qname;
		return qname.substring(i+1);
	}
	
	private ArrayList<Definition> getStart(Grammar grammar){
		Map<String, ArrayList<Definition>> definitions = grammarDefinitions.get(grammar);
		if(definitions == null) return null;
		return definitions.get(null);
	}
		
	private ArrayList<Definition> getReferencedDefinition(Grammar grammar, String name){
		Map<String, ArrayList<Definition>> definitions = grammarDefinitions.get(grammar);
		if(definitions == null) return null;
		return definitions.get(name);
	}
	
	private Pattern getReferencedPattern(ExternalRef ref){
		URI uri = externalRefs.get(ref);
		if(uri == null)return null;
		return docParsedModels.get(uri).getTopPattern();
	}

    private void startXmlnsContext(Map<String, String> prefixMapping){
        Set<String> prefixes = prefixMapping.keySet();
        for(String prefix : prefixes){
            String namespace = prefixMapping.get(prefix);
            simplificationContext.startPrefixMapping(prefix, namespace);
        }
    }

    private void endXmlnsContext(Map<String, String> prefixMapping){
        Set<String> prefixes = prefixMapping.keySet();
        for(String prefix : prefixes){
            simplificationContext.endPrefixMapping(prefix);
        }
    }

    private String getDefaultValue(AttributeInfo[] foreignAttributes){
        for(AttributeInfo attributeInfo : foreignAttributes){
            String uri = attributeInfo.getNamespaceURI(); 
            String ln = attributeInfo.getLocalName();
            if( uri != null && uri.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE)
                && ln != null && ln.equals(Constants.DTD_COMPATIBILITY_DEFAULT_VALUE)) 
            return attributeInfo.getValue();
        }
    
        return null;
    }	
}
