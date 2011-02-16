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


package serene.validation.schema.parsed.util;


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

import serene.validation.schema.parsed.Name;
import serene.validation.schema.parsed.AnyName;
import serene.validation.schema.parsed.NsName;
import serene.validation.schema.parsed.ChoiceNameClass;

import serene.validation.schema.parsed.Define;
import serene.validation.schema.parsed.Start;

import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.ParsedComponentVisitor;

public abstract class AbstractParsedComponentVisitor implements ParsedComponentVisitor{

	public void visit(Param param){}
	public void visit(Include include){
		ParsedComponent[] children = include.getChildren();
		if(children != null) next(children);
	}
	public void visit(ExceptPattern exceptPattern){
		ParsedComponent[] children = exceptPattern.getChildren();
		if(children != null) next(children);
	}
	public void visit(ExceptNameClass exceptNameClass){
		ParsedComponent[] children = exceptNameClass.getChildren();
		if(children != null) next(children);		
	}
	public void visit(DivGrammarContent div){
		ParsedComponent[] children = div.getChildren();
		if(children != null) next(children);
	}
	public void visit(DivIncludeContent div){
		ParsedComponent[] children = div.getChildren();
		if(children != null) next(children);
	}
		
	public void visit(Name component){}
	public void visit(AnyName anyName){
		ParsedComponent[] children = anyName.getChildren();
		if(children != null) next(children);
	}
	public void visit(NsName nsName){
		ParsedComponent[] children = nsName.getChildren();
		if(children != null) next(children);
	}
	public void visit(ChoiceNameClass choice){
		ParsedComponent[] children = choice.getChildren();
		if(children != null) next(children);
	}	
	
	public void visit(Define define){
		ParsedComponent[] children = define.getChildren();
		if(children != null)next(children);
	}
	public void visit(Start start){
		ParsedComponent[] children = start.getChildren();
		if(children != null)next(children);
	}
		
	public void visit(ElementWithNameClass element){
		ParsedComponent[] children = element.getChildren();
		if(children != null)next(children);
	}	
	public void visit(ElementWithNameInstance element){		
		ParsedComponent[] children = element.getChildren();
		if(children != null)next(children);
	}	
	public void visit(AttributeWithNameClass attribute){	
		ParsedComponent[] children = attribute.getChildren();
		if(children != null) next(children);		
	}
	public void visit(AttributeWithNameInstance attribute){				
		ParsedComponent[] children = attribute.getChildren();
		if(children != null) next(children);		
	}
	public void visit(ChoicePattern choice){
		ParsedComponent[] children = choice.getChildren();
		if(children != null) next(children);
	}
	public void visit(Interleave interleave){
		ParsedComponent[] children = interleave.getChildren();
		if(children != null) next(children);
	}
	public void visit(Group group){
		ParsedComponent[] children = group.getChildren();
		if(children != null) next(children);
	}
	public void visit(ZeroOrMore zeroOrMore){
		ParsedComponent[] children = zeroOrMore.getChildren();
		if(children != null) next(children);
	}
	public void visit(OneOrMore oneOrMore){
		ParsedComponent[] children = oneOrMore.getChildren();
		if(children != null) next(children);
	}
	public void visit(Optional optional){
		ParsedComponent[] children = optional.getChildren();
		if(children != null) next(children);
	}	
	public void visit(ListPattern list){
		ParsedComponent[] children = list.getChildren();
		if(children != null) next(children);
	}	
	public void visit(Mixed mixed){
		ParsedComponent[] children = mixed.getChildren();
		if(children != null) next(children);
	}	
	public void visit(Empty empty){}
	public void visit(Text text){}
	public void visit(NotAllowed notAllowed){}
	public void visit(ExternalRef externalRef){}
	public void visit(Ref ref){}
	public void visit(ParentRef parentRef){}
	public void visit(Value value){}
	public void visit(Data data){	
		ParsedComponent[] children = data.getChildren();
		if(children != null) next(children);
	}	
	public void visit(Grammar grammar){
		ParsedComponent[] children = grammar.getChildren();
		if(children != null)next(children);
	}
		
		
	protected void next(ParsedComponent[] children){
		for(ParsedComponent child : children){            
			child.accept(this);
		}
	} 
}
