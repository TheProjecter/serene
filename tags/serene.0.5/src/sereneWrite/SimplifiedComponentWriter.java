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

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.simplified.components.SParam;
import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SExceptNameClass;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SZeroOrMore;
import serene.validation.schema.simplified.components.SOneOrMore;
import serene.validation.schema.simplified.components.SOptional;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SEmpty;
import serene.validation.schema.simplified.components.SText;
import serene.validation.schema.simplified.components.SNotAllowed;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SValue;
import serene.validation.schema.simplified.components.SGrammar;
import serene.validation.schema.simplified.components.SDummy;

import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.simplified.util.AbstractSimplifiedComponentVisitor;

public class SimplifiedComponentWriter extends AbstractSimplifiedComponentVisitor{
	MessageWriter debugWriter;
	int tab;
	
	public void write(SimplifiedComponent component, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		debugWriter.write("simplifiedComponentWriter WRITE: ");
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
	public void visit(SParam param){
		tab++;
		debugWriter.write(getTabString() + param.toString());
		tab--;
	}
		
	public void visit(SExceptPattern exceptPattern){
		tab++;
		debugWriter.write(getTabString() + exceptPattern.toString());
		SimplifiedComponent child = exceptPattern.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(SExceptNameClass exceptNameClass){
		tab++;
		debugWriter.write(getTabString() + exceptNameClass.toString());
		SimplifiedComponent child = exceptNameClass.getChild();
		if(child != null) child.accept(this);		
		tab--;
	}
		
	public void visit(SName name){
		tab++;
		debugWriter.write(getTabString() + name.toString());
		tab--;
	}
	public void visit(SAnyName anyName){
		tab++;
		debugWriter.write(getTabString() + anyName.toString());
		SimplifiedComponent child = anyName.getExceptNameClass();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(SNsName nsName){
		tab++;
		debugWriter.write(getTabString() + nsName.toString());
		SimplifiedComponent child = nsName.getExceptNameClass();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(SChoiceNameClass choice){
		tab++;
		debugWriter.write(getTabString() + choice.toString());
		SimplifiedComponent[] children = choice.getChildren();
		if(children != null) next(children);
		tab--;
	}	
	
		
	public void visit(SElement element){
		tab++;
		debugWriter.write(getTabString() + element.toString());
		SimplifiedComponent nameClass = element.getNameClass();
		if(nameClass != null) nameClass.accept(this);
		SimplifiedComponent child = element.getChild();
		if(child != null) child.accept(this);	
		tab--;
	}	
	public void visit(SAttribute attribute){
		tab++;
		debugWriter.write(getTabString() + attribute.toString());
		SimplifiedComponent nameClass = attribute.getNameClass();
		if(nameClass != null) nameClass.accept(this);		
		SimplifiedComponent[] children = attribute.getChildren();
		if(children != null)next(children);		
		tab--;
	}
	public void visit(SChoicePattern choice){
		tab++;
		debugWriter.write(getTabString() + choice.toString());
		SimplifiedComponent[] children = choice.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(SInterleave interleave){
		tab++;
		debugWriter.write(getTabString() + interleave.toString());
		SimplifiedComponent[] children = interleave.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(SGroup group){
		tab++;
		debugWriter.write(getTabString() + group.toString());
		SimplifiedComponent[] children = group.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(SZeroOrMore zeroOrMore){
		tab++;
		debugWriter.write(getTabString() + zeroOrMore.toString());
		SimplifiedComponent child = zeroOrMore.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(SOneOrMore oneOrMore){
		tab++;
		debugWriter.write(getTabString() + oneOrMore.toString());
		SimplifiedComponent child = oneOrMore.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(SOptional optional){
		tab++;
		debugWriter.write(getTabString() + optional.toString());
		SimplifiedComponent child = optional.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(SMixed mixed){
		tab++;
		debugWriter.write(getTabString() + mixed.toString());
		SimplifiedComponent child = mixed.getChild();
		if(child != null) child.accept(this);
		tab--;
	}	
	public void visit(SListPattern list){
		tab++;
		debugWriter.write(getTabString() + list.toString());
		SimplifiedComponent child = list.getChild();
		if(child != null) child.accept(this);
		tab--;
	}	
	public void visit(SEmpty empty){
		tab++;
		debugWriter.write(getTabString() + empty.toString());
		tab--;
	}
	public void visit(SText text){
		tab++;
		debugWriter.write(getTabString() + text.toString());
		tab--;
	}
	public void visit(SNotAllowed notAllowed){
		tab++;
		debugWriter.write(getTabString() + notAllowed.toString());
		tab--;
	}
	public void visit(SRef ref){
		tab++;
		debugWriter.write(getTabString() + ref.toString());
		tab--;
	}
	public void visit(SValue value){
		tab++;
		debugWriter.write(getTabString() + value.toString());
		tab--;
	}
	public void visit(SData data){
		tab++;
		debugWriter.write(getTabString() + data.toString());
		SimplifiedComponent[] param = data.getParam();
		if(param != null) next(param);
		SimplifiedComponent[] exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) next(exceptPattern);
		tab--;
	}	
	public void visit(SGrammar grammar){
		tab++;
		debugWriter.write(getTabString() + grammar.toString());
		SimplifiedComponent child = grammar.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
		
	
	public void visit(SDummy dummy){
		tab++;
		debugWriter.write(getTabString() + dummy.toString());
		SimplifiedComponent[] children = dummy.getChildren();
		if(children != null) next(children);
		tab--;
	}
	
	public void next(SimplifiedComponent[] children){		
		for(SimplifiedComponent child : children){            
			child.accept(this);
		}
	} 
}