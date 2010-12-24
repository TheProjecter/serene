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

import java.util.Map;

import javax.xml.XMLConstants;

import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.ParsedComponentVisitor;

import serene.validation.schema.parsed.components.Param;
import serene.validation.schema.parsed.components.Include;
import serene.validation.schema.parsed.components.ExceptPattern;
import serene.validation.schema.parsed.components.ExceptNameClass;
import serene.validation.schema.parsed.components.DivGrammarContent;
import serene.validation.schema.parsed.components.DivIncludeContent;

import serene.validation.schema.parsed.components.ElementWithNameClass;
import serene.validation.schema.parsed.components.ElementWithNameInstance;
import serene.validation.schema.parsed.components.AttributeWithNameClass;
import serene.validation.schema.parsed.components.AttributeWithNameInstance;
import serene.validation.schema.parsed.components.ChoicePattern;
import serene.validation.schema.parsed.components.Interleave;
import serene.validation.schema.parsed.components.Group;
import serene.validation.schema.parsed.components.ZeroOrMore;
import serene.validation.schema.parsed.components.OneOrMore;
import serene.validation.schema.parsed.components.Optional;
import serene.validation.schema.parsed.components.ListPattern;
import serene.validation.schema.parsed.components.Mixed;
import serene.validation.schema.parsed.components.Empty;
import serene.validation.schema.parsed.components.Text;
import serene.validation.schema.parsed.components.NotAllowed;
import serene.validation.schema.parsed.components.ExternalRef;
import serene.validation.schema.parsed.components.Ref;
import serene.validation.schema.parsed.components.ParentRef;
import serene.validation.schema.parsed.components.Data;
import serene.validation.schema.parsed.components.Value;
import serene.validation.schema.parsed.components.Grammar;
import serene.validation.schema.parsed.components.Dummy;

import serene.validation.schema.parsed.components.Name;
import serene.validation.schema.parsed.components.AnyName;
import serene.validation.schema.parsed.components.NsName;
import serene.validation.schema.parsed.components.ChoiceNameClass;

import serene.validation.schema.parsed.components.Define;
import serene.validation.schema.parsed.components.Start;

import sereneWrite.MessageWriter;

class XmlnsInheritanceHandler implements ParsedComponentVisitor{
	
	String prefix;
	String namespaceURI;
	
	Map<ParsedComponent, ParsedComponent> descendanceMap;
	
	MessageWriter debugWriter;
	
	XmlnsInheritanceHandler(Map<ParsedComponent, ParsedComponent> descendanceMap,
						MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.descendanceMap = descendanceMap;
	}
	
	String getURI(String prefix, ParsedComponent component){
		if(prefix.equals(XMLConstants.XML_NS_PREFIX)) return XMLConstants.XML_NS_URI;  
		this.prefix = prefix;
		namespaceURI = null;
		component.accept(this);		
		return namespaceURI;
	}
	
	public void visit(Param param){
		namespaceURI = param.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = param.getParent();
		if(parent == null ) parent = descendanceMap.get(param);
		if(parent != null) parent.accept(this);
	}
	public void visit(Include include){
		namespaceURI = include.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = include.getParent();
		if(parent == null ) parent = descendanceMap.get(include);
		if(parent != null) parent.accept(this);
	}
	public void visit(ExceptPattern exceptPattern){
		namespaceURI = exceptPattern.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = exceptPattern.getParent();
		if(parent == null ) parent = descendanceMap.get(exceptPattern);
		if(parent != null) parent.accept(this);
	}
	public void visit(ExceptNameClass exceptNameClass){
		namespaceURI = exceptNameClass.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = exceptNameClass.getParent();
		if(parent == null ) parent = descendanceMap.get(exceptNameClass);
		if(parent != null) parent.accept(this);		
	}
	public void visit(DivGrammarContent div){
		namespaceURI = div.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = div.getParent();
		if(parent == null ) parent = descendanceMap.get(div);
		if(parent != null) parent.accept(this);		
	}
	public void visit(DivIncludeContent div){
		namespaceURI = div.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = div.getParent();
		if(parent == null ) parent = descendanceMap.get(div);
		if(parent != null) parent.accept(this);		
	}
		
	public void visit(Name name){
		namespaceURI = name.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = name.getParent();				
		if(parent == null ) parent = descendanceMap.get(name);
		if(parent != null) parent.accept(this);		
	}
	public void visit(AnyName anyName){
		namespaceURI = anyName.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = anyName.getParent();
		if(parent == null ) parent = descendanceMap.get(anyName);
		if(parent != null) parent.accept(this);		
	}
	public void visit(NsName namespaceURIName){
		namespaceURI = namespaceURIName.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = namespaceURIName.getParent();
		if(parent == null ) parent = descendanceMap.get(namespaceURIName);
		if(parent != null) parent.accept(this);		
	}
	public void visit(ChoiceNameClass choice){
		namespaceURI = choice.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = choice.getParent();
		if(parent == null ) parent = descendanceMap.get(choice);
		if(parent != null) parent.accept(this);		
	}	
	
	public void visit(Define define){
		namespaceURI = define.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = define.getParent();
		if(parent == null ) parent = descendanceMap.get(define);
		if(parent != null) parent.accept(this);		
	}
	public void visit(Start start){
		namespaceURI = start.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = start.getParent();
		if(parent == null ) parent = descendanceMap.get(start);
		if(parent != null) parent.accept(this);		
	}
		
	public void visit(ElementWithNameClass element){
		namespaceURI = element.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = element.getParent();
		if(parent == null ) parent = descendanceMap.get(element);
		if(parent != null) parent.accept(this);		
	}	
	public void visit(ElementWithNameInstance element){		
		namespaceURI = element.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = element.getParent();
		if(parent == null ) parent = descendanceMap.get(element);
		if(parent != null) parent.accept(this);		
	}	
	public void visit(AttributeWithNameClass attribute){
		namespaceURI = attribute.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = attribute.getParent();
		if(parent == null ) parent = descendanceMap.get(attribute);
		if(parent != null) parent.accept(this);				
	}
	public void visit(AttributeWithNameInstance attribute){				
		namespaceURI = attribute.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = attribute.getParent();
		if(parent == null ) parent = descendanceMap.get(attribute);
		if(parent != null) parent.accept(this);						
	}
	public void visit(ChoicePattern choice){
		namespaceURI = choice.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = choice.getParent();
		if(parent == null ) parent = descendanceMap.get(choice);
		if(parent != null) parent.accept(this);				
	}
	public void visit(Interleave interleave){
		namespaceURI = interleave.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = interleave.getParent();
		if(parent == null ) parent = descendanceMap.get(interleave);
		if(parent != null) parent.accept(this);				
	}
	public void visit(Group group){
		namespaceURI = group.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = group.getParent();
		if(parent == null ) parent = descendanceMap.get(group);
		if(parent != null) parent.accept(this);			
	}
	public void visit(ZeroOrMore zeroOrMore){
		namespaceURI = zeroOrMore.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = zeroOrMore.getParent();
		if(parent == null ) parent = descendanceMap.get(zeroOrMore);
		if(parent != null) parent.accept(this);			
	}
	public void visit(OneOrMore oneOrMore){
		namespaceURI = oneOrMore.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = oneOrMore.getParent();
		if(parent == null ) parent = descendanceMap.get(oneOrMore);
		if(parent != null) parent.accept(this);			
	}
	public void visit(Optional optional){
		namespaceURI = optional.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = optional.getParent();
		if(parent == null ) parent = descendanceMap.get(optional);
		if(parent != null) parent.accept(this);			
	}	
	public void visit(ListPattern list){
		namespaceURI = list.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = list.getParent();
		if(parent == null ) parent = descendanceMap.get(list);
		if(parent != null) parent.accept(this);			
	}	
	public void visit(Mixed mixed){
		namespaceURI = mixed.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = mixed.getParent();
		if(parent == null ) parent = descendanceMap.get(mixed);
		if(parent != null) parent.accept(this);			
	}	
	public void visit(Empty empty){
		namespaceURI = empty.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = empty.getParent();
		if(parent == null ) parent = descendanceMap.get(empty);
		if(parent != null) parent.accept(this);
	}
	public void visit(Text text){
		namespaceURI = text.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = text.getParent();
		if(parent == null ) parent = descendanceMap.get(text);
		if(parent != null) parent.accept(this);
	}
	public void visit(NotAllowed notAllowed){
		namespaceURI = notAllowed.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = notAllowed.getParent();
		if(parent == null ) parent = descendanceMap.get(notAllowed);
		if(parent != null) parent.accept(this);
	}
	public void visit(ExternalRef externalRef){
		namespaceURI = externalRef.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = externalRef.getParent();
		if(parent == null ) parent = descendanceMap.get(externalRef);
		if(parent != null) parent.accept(this);
	}
	public void visit(Ref ref){
		namespaceURI = ref.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = ref.getParent();
		if(parent == null ) parent = descendanceMap.get(ref);
		if(parent != null) parent.accept(this);
	}
	public void visit(ParentRef parentRef){
		namespaceURI = parentRef.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = parentRef.getParent();
		if(parent == null ) parent = descendanceMap.get(parentRef);
		if(parent != null) parent.accept(this);
	}
	public void visit(Value value){
		namespaceURI = value.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = value.getParent();
		if(parent == null ) parent = descendanceMap.get(value);
		if(parent != null) parent.accept(this);
	}
	public void visit(Data data){	
		namespaceURI = data.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = data.getParent();
		if(parent == null ) parent = descendanceMap.get(data);
		if(parent != null) parent.accept(this);
	}	
	public void visit(Grammar grammar){
		namespaceURI = grammar.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = grammar.getParent();
		if(parent == null ) parent = descendanceMap.get(grammar);		
		if(parent != null) parent.accept(this);
	}
	
	public void visit(Dummy dummy){
		namespaceURI = dummy.getNamespaceURIAttribute(prefix);
		if(namespaceURI != null)return;
		ParsedComponent parent = dummy.getParent();
		if(parent == null ) parent = descendanceMap.get(dummy);		
		if(parent != null) parent.accept(this);
	}
}