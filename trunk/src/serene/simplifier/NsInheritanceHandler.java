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

import sereneWrite.MessageWriter;

class NsInheritanceHandler implements ParsedComponentVisitor{
	
	String ns;
	
	Map<ParsedComponent, ParsedComponent> descendanceMap;
	
	MessageWriter debugWriter;
	
	NsInheritanceHandler(Map<ParsedComponent, ParsedComponent> descendanceMap,
						MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.descendanceMap = descendanceMap;
	}
	
	String getURI(ParsedComponent component){
		ns = null;
		component.accept(this);		
		return ns;
	}
	
	public void visit(Param param){
		ns = param.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = param.getParent();
		if(parent == null ) parent = descendanceMap.get(param);
		if(parent != null) parent.accept(this);
	}
	public void visit(Include include){
		ns = include.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = include.getParent();
		if(parent == null ) parent = descendanceMap.get(include);
		if(parent != null) parent.accept(this);
	}
	public void visit(ExceptPattern exceptPattern){
		ns = exceptPattern.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = exceptPattern.getParent();
		if(parent == null ) parent = descendanceMap.get(exceptPattern);
		if(parent != null) parent.accept(this);
	}
	public void visit(ExceptNameClass exceptNameClass){
		ns = exceptNameClass.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = exceptNameClass.getParent();
		if(parent == null ) parent = descendanceMap.get(exceptNameClass);
		if(parent != null) parent.accept(this);		
	}
	public void visit(DivGrammarContent div){
		ns = div.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = div.getParent();
		if(parent == null ) parent = descendanceMap.get(div);
		if(parent != null) parent.accept(this);		
	}
	public void visit(DivIncludeContent div){
		ns = div.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = div.getParent();
		if(parent == null ) parent = descendanceMap.get(div);
		if(parent != null) parent.accept(this);		
	}
		
	public void visit(Name name){
		ns = name.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = name.getParent();
		if(parent == null ) parent = descendanceMap.get(name);
		if(parent != null) parent.accept(this);		
	}
	public void visit(AnyName anyName){
		ns = anyName.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = anyName.getParent();
		if(parent == null ) parent = descendanceMap.get(anyName);
		if(parent != null) parent.accept(this);		
	}
	public void visit(NsName nsName){
		ns = nsName.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = nsName.getParent();
		if(parent == null ) parent = descendanceMap.get(nsName);
		if(parent != null) parent.accept(this);		
	}
	public void visit(ChoiceNameClass choice){
		ns = choice.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = choice.getParent();
		if(parent == null ) parent = descendanceMap.get(choice);
		if(parent != null) parent.accept(this);		
	}	
	
	public void visit(Define define){
		ns = define.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = define.getParent();
		if(parent == null ) parent = descendanceMap.get(define);
		if(parent != null) parent.accept(this);		
	}
	public void visit(Start start){
		ns = start.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = start.getParent();
		if(parent == null ) parent = descendanceMap.get(start);
		if(parent != null) parent.accept(this);		
	}
		
	public void visit(ElementWithNameClass element){
		ns = element.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = element.getParent();
		if(parent == null ) parent = descendanceMap.get(element);
		if(parent != null) parent.accept(this);		
	}	
	public void visit(ElementWithNameInstance element){
		ns = element.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = element.getParent();
		if(parent == null ) parent = descendanceMap.get(element);
		if(parent != null) parent.accept(this);		
	}	
	public void visit(AttributeWithNameClass attribute){
		ns = attribute.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = attribute.getParent();
		if(parent == null ) parent = descendanceMap.get(attribute);
		if(parent != null) parent.accept(this);				
	}
	public void visit(AttributeWithNameInstance attribute){				
		ns = attribute.getNsAttribute();
        if(ns == null)ns = XMLConstants.NULL_NS_URI ;
		/*if(ns != null)return;
		ParsedComponent parent = attribute.getParent();
		if(parent == null ) parent = descendanceMap.get(attribute);
		if(parent != null) parent.accept(this);*/						
	}
	public void visit(ChoicePattern choice){
		ns = choice.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = choice.getParent();
		if(parent == null ) parent = descendanceMap.get(choice);
		if(parent != null) parent.accept(this);				
	}
	public void visit(Interleave interleave){
		ns = interleave.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = interleave.getParent();
		if(parent == null ) parent = descendanceMap.get(interleave);
		if(parent != null) parent.accept(this);				
	}
	public void visit(Group group){
		ns = group.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = group.getParent();
		if(parent == null ) parent = descendanceMap.get(group);
		if(parent != null) parent.accept(this);			
	}
	public void visit(ZeroOrMore zeroOrMore){
		ns = zeroOrMore.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = zeroOrMore.getParent();
		if(parent == null ) parent = descendanceMap.get(zeroOrMore);
		if(parent != null) parent.accept(this);			
	}
	public void visit(OneOrMore oneOrMore){
		ns = oneOrMore.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = oneOrMore.getParent();
		if(parent == null ) parent = descendanceMap.get(oneOrMore);
		if(parent != null) parent.accept(this);			
	}
	public void visit(Optional optional){
		ns = optional.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = optional.getParent();
		if(parent == null ) parent = descendanceMap.get(optional);
		if(parent != null) parent.accept(this);			
	}	
	public void visit(ListPattern list){
		ns = list.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = list.getParent();
		if(parent == null ) parent = descendanceMap.get(list);
		if(parent != null) parent.accept(this);			
	}	
	public void visit(Mixed mixed){
		ns = mixed.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = mixed.getParent();
		if(parent == null ) parent = descendanceMap.get(mixed);
		if(parent != null) parent.accept(this);			
	}	
	public void visit(Empty empty){
		ns = empty.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = empty.getParent();
		if(parent == null ) parent = descendanceMap.get(empty);
		if(parent != null) parent.accept(this);
	}
	public void visit(Text text){
		ns = text.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = text.getParent();
		if(parent == null ) parent = descendanceMap.get(text);
		if(parent != null) parent.accept(this);
	}
	public void visit(NotAllowed notAllowed){
		ns = notAllowed.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = notAllowed.getParent();
		if(parent == null ) parent = descendanceMap.get(notAllowed);
		if(parent != null) parent.accept(this);
	}
	public void visit(ExternalRef externalRef){
		ns = externalRef.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = externalRef.getParent();
		if(parent == null ) parent = descendanceMap.get(externalRef);
		if(parent != null) parent.accept(this);
	}
	public void visit(Ref ref){
		ns = ref.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = ref.getParent();
		if(parent == null ) parent = descendanceMap.get(ref);
		if(parent != null) parent.accept(this);
	}
	public void visit(ParentRef parentRef){
		ns = parentRef.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = parentRef.getParent();
		if(parent == null ) parent = descendanceMap.get(parentRef);
		if(parent != null) parent.accept(this);
	}
	public void visit(Value value){
		ns = value.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = value.getParent();
		if(parent == null ) parent = descendanceMap.get(value);
		if(parent != null) parent.accept(this);
	}
	public void visit(Data data){	
		ns = data.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = data.getParent();
		if(parent == null ) parent = descendanceMap.get(data);
		if(parent != null) parent.accept(this);
	}	
	public void visit(Grammar grammar){
		ns = grammar.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = grammar.getParent();
		if(parent == null ) parent = descendanceMap.get(grammar);
		if(parent != null) parent.accept(this);
	}	

	public void visit(Dummy dummy){
		ns = dummy.getNsAttribute();
		if(ns != null)return;
		ParsedComponent parent = dummy.getParent();
		if(parent == null ) parent = descendanceMap.get(dummy);
		if(parent != null) parent.accept(this);
	}
    
    public void visit(ForeignComponent fc){}
}