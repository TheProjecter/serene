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

import serene.validation.schema.parsed.components.Name;
import serene.validation.schema.parsed.components.AnyName;
import serene.validation.schema.parsed.components.NsName;
import serene.validation.schema.parsed.components.ChoiceNameClass;

import serene.validation.schema.parsed.components.Define;
import serene.validation.schema.parsed.components.Start;

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
		ParsedComponent child = anyName.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(NsName nsName){
		ParsedComponent child = nsName.getChild();
		if(child != null) child.accept(this);
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
		ParsedComponent nameClass = element.getNameClass();
		if(nameClass != null) nameClass.accept(this);
		ParsedComponent[] children = element.getChildren();
		if(children != null)next(children);
	}	
	public void visit(ElementWithNameInstance element){		
		ParsedComponent[] children = element.getChildren();
		if(children != null)next(children);
	}	
	public void visit(AttributeWithNameClass attribute){
		ParsedComponent nameClass = attribute.getNameClass();
		if(nameClass != null) nameClass.accept(this);		
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
		ParsedComponent[] param = data.getParam();
		if(param != null) next(param);
		ParsedComponent[] exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) next(exceptPattern);
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