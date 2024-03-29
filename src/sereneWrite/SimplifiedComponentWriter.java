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

import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SExceptNameClass;

import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SChoicePattern;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SGroup;
import serene.validation.schema.simplified.SListPattern;
import serene.validation.schema.simplified.SEmpty;
import serene.validation.schema.simplified.SText;
import serene.validation.schema.simplified.SNotAllowed;
import serene.validation.schema.simplified.SRef;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SGrammar;
import serene.validation.schema.simplified.SDummy;

import serene.validation.schema.simplified.SName;
import serene.validation.schema.simplified.SAnyName;
import serene.validation.schema.simplified.SNsName;
import serene.validation.schema.simplified.SChoiceNameClass;

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
	
	public void write(SimplifiedComponent component){
	    debugWriter = new MessageWriter();
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