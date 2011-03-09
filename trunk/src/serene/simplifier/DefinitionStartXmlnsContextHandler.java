/*
Copyright 2011 Radu Cernuta 

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

import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;

import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.ParsedComponentVisitor;

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

import serene.validation.schema.parsed.Definition;

import serene.validation.schema.parsed.ForeignComponent;

import sereneWrite.MessageWriter;

class DefinitionStartXmlnsContextHandler implements ParsedComponentVisitor{
	DocumentSimplificationContext simplificationContext;    
	Map<ParsedComponent, ParsedComponent> descendanceMap;
	
	MessageWriter debugWriter;
	
	DefinitionStartXmlnsContextHandler(Map<ParsedComponent, ParsedComponent> descendanceMap,
						MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.descendanceMap = descendanceMap;
	}
	
	void handle(DocumentSimplificationContext simplificationContext, Definition definition){
		this.simplificationContext = simplificationContext;
		definition.accept(this);
	}
	
	public void visit(Param param){
		throw new IllegalStateException();
	}
    
	public void visit(Include include){        
		ParsedComponent parent = include.getParent();
		if(parent == null ) parent = descendanceMap.get(include);
		if(parent != null) parent.accept(this);        
        Map<String, String> prefixMapping = include.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);        
	}
    
	public void visit(ExceptPattern exceptPattern){
		throw new IllegalStateException();
	}
	public void visit(ExceptNameClass exceptNameClass){
		throw new IllegalStateException();		
	}
	public void visit(DivGrammarContent div){
		ParsedComponent parent = div.getParent();
		if(parent == null ) parent = descendanceMap.get(div);
		if(parent != null) parent.accept(this);
        Map<String, String> prefixMapping = div.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);		
	}
	public void visit(DivIncludeContent div){
		ParsedComponent parent = div.getParent();
		if(parent == null ) parent = descendanceMap.get(div);
		if(parent != null) parent.accept(this);
        Map<String, String> prefixMapping = div.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);		
	}
		
	public void visit(Name name){
		throw new IllegalStateException();		
	}
	public void visit(AnyName anyName){
		throw new IllegalStateException();
	}
	public void visit(NsName namespaceURIName){
		throw new IllegalStateException();		
	}
	public void visit(ChoiceNameClass choice){
		throw new IllegalStateException();		
	}	
	
	public void visit(Define define){
		ParsedComponent parent = define.getParent();
		if(parent == null ) parent = descendanceMap.get(define);
		if(parent != null) parent.accept(this);
        Map<String, String> prefixMapping = define.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);		
	}
	public void visit(Start start){
		ParsedComponent parent = start.getParent();
		if(parent == null ) parent = descendanceMap.get(start);
		if(parent != null) parent.accept(this);
        Map<String, String> prefixMapping = start.getXmlns();
        if(prefixMapping != null) startXmlnsContext(prefixMapping);		
	}
		
	public void visit(ElementWithNameClass element){
		throw new IllegalStateException();		
	}	
	public void visit(ElementWithNameInstance element){		
		throw new IllegalStateException();		
	}	
	public void visit(AttributeWithNameClass attribute){
		throw new IllegalStateException();				
	}
	public void visit(AttributeWithNameInstance attribute){				
		throw new IllegalStateException();						
	}
	public void visit(ChoicePattern choice){
		throw new IllegalStateException();				
	}
	public void visit(Interleave interleave){
		throw new IllegalStateException();	
	}
	public void visit(Group group){
		throw new IllegalStateException();
	}
	public void visit(ZeroOrMore zeroOrMore){
		throw new IllegalStateException();
	}
	public void visit(OneOrMore oneOrMore){
		throw new IllegalStateException();
	}
	public void visit(Optional optional){
		throw new IllegalStateException();
	}	
	public void visit(ListPattern list){
		throw new IllegalStateException();			
	}	
	public void visit(Mixed mixed){
		throw new IllegalStateException();			
	}	
	public void visit(Empty empty){
		throw new IllegalStateException();
	}
	public void visit(Text text){
		throw new IllegalStateException();
	}
	public void visit(NotAllowed notAllowed){
		throw new IllegalStateException();
	}
	public void visit(ExternalRef externalRef){
		throw new IllegalStateException();
	}
	public void visit(Ref ref){
		throw new IllegalStateException();
	}
	public void visit(ParentRef parentRef){
		throw new IllegalStateException();
	}
	public void visit(Value value){
		throw new IllegalStateException();
	}
	public void visit(Data data){	
		throw new IllegalStateException();
	}	
	public void visit(Grammar grammar){}
	
	public void visit(Dummy dummy){
		throw new IllegalStateException();
	}
    
    public void visit(ForeignComponent fg){
		throw new IllegalStateException();
	}
    
    private void startXmlnsContext(Map<String, String> prefixMapping){
        Set<String> prefixes = prefixMapping.keySet();
        for(String prefix : prefixes){
            String namespace = prefixMapping.get(prefix);
            simplificationContext.startPrefixMapping(prefix, namespace);
        }
    }
}
