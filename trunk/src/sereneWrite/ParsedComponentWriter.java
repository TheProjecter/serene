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


package sereneWrite;

import serene.validation.schema.parsed.ParsedComponent;

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

import serene.validation.schema.parsed.util.AbstractParsedComponentVisitor;

public class ParsedComponentWriter extends AbstractParsedComponentVisitor{
	MessageWriter debugWriter;
	int tab;
	
	public void write(ParsedComponent component, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		tab = 0;
		component.accept(this);
	}
	
	private String getTabString(){
		String s = "";
		for(int i =0; i<=tab;i++){
			s+="\t";
		}
		return s;
	}
	public void visit(Param param){
		tab++;
		debugWriter.write(getTabString() + param.toString());
		tab--;
	}
	
	public void visit(Include include){
		tab++;
		debugWriter.write(getTabString() + include.toString());
		ParsedComponent[] children = include.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(ExceptPattern exceptPattern){
		tab++;
		debugWriter.write(getTabString() + exceptPattern.toString());
		ParsedComponent[] children = exceptPattern.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(ExceptNameClass exceptNameClass){
		tab++;
		debugWriter.write(getTabString() + exceptNameClass.toString());
		ParsedComponent[] children = exceptNameClass.getChildren();
		if(children != null) next(children);		
		tab--;
	}
	public void visit(DivGrammarContent div){
		tab++;
		debugWriter.write(getTabString() + div.toString());
		ParsedComponent[] children = div.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(DivIncludeContent div){
		tab++;
		debugWriter.write(getTabString() + div.toString());
		ParsedComponent[] children = div.getChildren();
		if(children != null) next(children);
		tab--;
	}
		
	public void visit(Name name){
		tab++;
		debugWriter.write(getTabString() + name.toString());
		tab--;
	}
	public void visit(AnyName anyName){
		tab++;
		debugWriter.write(getTabString() + anyName.toString());
		ParsedComponent[] children = anyName.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(NsName nsName){
		tab++;
		debugWriter.write(getTabString() + nsName.toString());
		ParsedComponent[] children = nsName.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(ChoiceNameClass choice){
		tab++;
		debugWriter.write(getTabString() + choice.toString());
		ParsedComponent[] children = choice.getChildren();
		if(children != null) next(children);
		tab--;
	}	
	
	public void visit(Define define){
		tab++;
		debugWriter.write(getTabString() + define.toString());
		ParsedComponent[] children = define.getChildren();
		if(children != null)next(children);
		tab--;
	}
	public void visit(Start start){
		tab++;
		debugWriter.write(getTabString() + start.toString());
		ParsedComponent[] children = start.getChildren();
		if(children != null) next(children);
		tab--;
	}
		
	public void visit(ElementWithNameClass element){
		tab++;
		debugWriter.write(getTabString() + element.toString());
		ParsedComponent[] children = element.getChildren();
		if(children != null)next(children);
		tab--;
	}	
	public void visit(ElementWithNameInstance element){
		tab++;
		debugWriter.write(getTabString() + element.toString());		
		ParsedComponent[] children = element.getChildren();
		if(children != null)next(children);
		tab--;
	}	
	public void visit(AttributeWithNameClass attribute){
		tab++;
		debugWriter.write(getTabString() + attribute.toString());
		ParsedComponent[] children = attribute.getChildren();
		if(children != null) next(children);		
		tab--;
	}
	public void visit(AttributeWithNameInstance attribute){
		tab++;
		debugWriter.write(getTabString() + attribute.toString());
		ParsedComponent[] children = attribute.getChildren();
		if(children != null) next(children);		
		tab--;
	}
	public void visit(ChoicePattern choice){
		tab++;
		debugWriter.write(getTabString() + choice.toString());
		ParsedComponent[] children = choice.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(Interleave interleave){
		tab++;
		debugWriter.write(getTabString() + interleave.toString());
		ParsedComponent[] children = interleave.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(Group group){
		tab++;
		debugWriter.write(getTabString() + group.toString());
		ParsedComponent[] children = group.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(ZeroOrMore zeroOrMore){
		tab++;
		debugWriter.write(getTabString() + zeroOrMore.toString());
		ParsedComponent[] children = zeroOrMore.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(OneOrMore oneOrMore){
		tab++;
		debugWriter.write(getTabString() + oneOrMore.toString());
		ParsedComponent[] children = oneOrMore.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(Optional optional){
		tab++;
		debugWriter.write(getTabString() + optional.toString());
		ParsedComponent[] children = optional.getChildren();
		if(children != null) next(children);
		tab--;
	}	
	public void visit(ListPattern list){
		tab++;
		debugWriter.write(getTabString() + list.toString());
		ParsedComponent[] children = list.getChildren();
		if(children != null) next(children);
		tab--;
	}	
	public void visit(Mixed mixed){
		tab++;
		debugWriter.write(getTabString() + mixed.toString());
		ParsedComponent[] children = mixed.getChildren();
		if(children != null) next(children);
		tab--;
	}	
	public void visit(Empty empty){
		tab++;
		debugWriter.write(getTabString() + empty.toString());
		tab--;
	}
	public void visit(Text text){
		tab++;
		debugWriter.write(getTabString() + text.toString());
		tab--;
	}
	public void visit(NotAllowed notAllowed){
		tab++;
		debugWriter.write(getTabString() + notAllowed.toString());
		tab--;
	}
	public void visit(ExternalRef externalRef){
		tab++;
		debugWriter.write(getTabString() + externalRef.toString());
		tab--;
	}
	public void visit(Ref ref){
		tab++;
		debugWriter.write(getTabString() + ref.toString());
		tab--;
	}
	public void visit(ParentRef parentRef){
		tab++;
		debugWriter.write(getTabString() + parentRef.toString());
		tab--;
	}
	public void visit(Value value){
		tab++;
		debugWriter.write(getTabString() + value.toString());
		tab--;
	}
	public void visit(Data data){
		tab++;
		debugWriter.write(getTabString() + data.toString());
        ParsedComponent[] children = data.getChildren();
		if(children != null) next(children);
		tab--;
	}	
	public void visit(Grammar grammar){
		tab++;
		debugWriter.write(getTabString() + grammar.toString());
		ParsedComponent[] children = grammar.getChildren();
		if(children != null)next(children);
		tab--;
	}
	public void visit(Dummy dummy){
		tab++;
		debugWriter.write(getTabString() + dummy.toString());
		ParsedComponent[] children = dummy.getChildren();
		if(children != null)next(children);
		tab--;
	}
    
    public void visit(ForeignComponent fg){
		tab++;
		debugWriter.write(getTabString() + fg.toString());
		ParsedComponent[] children = fg.getChildren();
		if(children != null)next(children);
		tab--;
	}
		
		
	public void next(ParsedComponent[] children){		
		for(ParsedComponent child : children){            
			child.accept(this);
		}
	} 
}
