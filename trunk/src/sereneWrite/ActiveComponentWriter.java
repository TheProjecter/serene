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

import serene.validation.schema.active.ActiveComponent;

import serene.validation.schema.active.components.AParam;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AExceptNameClass;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AEmpty;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.ANotAllowed;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AGrammar;

import serene.validation.schema.active.components.AName;
import serene.validation.schema.active.components.AAnyName;
import serene.validation.schema.active.components.ANsName;
import serene.validation.schema.active.components.AChoiceNameClass;

import serene.validation.schema.active.util.AbstractActiveComponentVisitor;

public class ActiveComponentWriter extends AbstractActiveComponentVisitor{
	MessageWriter debugWriter;
	int tab;
	
	public void write(ActiveComponent ac, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		debugWriter.write("activeComponentWriter WRITE: ");
		tab = 0;
		ac.accept(this);
	}
	
	private String getTabString(){
		String s = "";
		for(int i =0; i<=tab;i++){
			s+="\t";
		}
		return s;
	}
	public void visit(AParam param){
		tab++;
		debugWriter.write(getTabString() + param.toString());
		tab--;
	}
		
	public void visit(AExceptPattern exceptPattern){
		tab++;
		debugWriter.write(getTabString() + exceptPattern.toString());
		ActiveComponent child = exceptPattern.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(AExceptNameClass exceptNameClass){
		tab++;
		debugWriter.write(getTabString() + exceptNameClass.toString());
		ActiveComponent child = exceptNameClass.getChild();
		if(child != null) child.accept(this);		
		tab--;
	}
		
	public void visit(AName name){
		tab++;
		debugWriter.write(getTabString() + name.toString());
		tab--;
	}
	public void visit(AAnyName anyName){
		tab++;
		debugWriter.write(getTabString() + anyName.toString());
		ActiveComponent child = anyName.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(ANsName nsName){
		tab++;
		debugWriter.write(getTabString() + nsName.toString());
		ActiveComponent child = nsName.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(AChoiceNameClass choice){
		tab++;
		debugWriter.write(getTabString() + choice.toString());
		ActiveComponent[] children = choice.getChildren();
		if(children != null) next(children);
		tab--;
	}	
	
		
	public void visit(AElement element){
		tab++;
		debugWriter.write(getTabString() + element.toString());
		ActiveComponent nameClass = element.getNameClass();
		if(nameClass != null) nameClass.accept(this);
		ActiveComponent child = element.getChild();
		if(child != null) child.accept(this);	
		tab--;
	}	
	public void visit(AAttribute attribute){
		tab++;
		debugWriter.write(getTabString() + attribute.toString());
		ActiveComponent nameClass = attribute.getNameClass();
		if(nameClass != null) nameClass.accept(this);		
		ActiveComponent child = attribute.getChild();
		if(child != null) child.accept(this);		
		tab--;
	}
	public void visit(AChoicePattern choice){
		tab++;
		debugWriter.write(getTabString() + choice.toString());
		ActiveComponent[] children = choice.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(AInterleave interleave){
		tab++;
		debugWriter.write(getTabString() + interleave.toString());
		ActiveComponent[] children = interleave.getChildren();
		if(children != null) next(children);
		tab--;
	}
	public void visit(AGroup group){
		tab++;
		debugWriter.write(getTabString() + group.toString());
		ActiveComponent[] children = group.getChildren();
		if(children != null) next(children);
		tab--;
	}	
	public void visit(AListPattern list){
		tab++;
		debugWriter.write(getTabString() + list.toString());
		ActiveComponent child = list.getChild();
		if(child != null) child.accept(this);
		tab--;
	}	
	public void visit(AEmpty empty){
		tab++;
		debugWriter.write(getTabString() + empty.toString());
		tab--;
	}
	public void visit(AText text){
		tab++;
		debugWriter.write(getTabString() + text.toString());
		tab--;
	}
	public void visit(ANotAllowed notAllowed){
		tab++;
		debugWriter.write(getTabString() + notAllowed.toString());
		tab--;
	}
	public void visit(ARef ref){
		tab++;
		debugWriter.write(getTabString() + ref.toString());
		ActiveComponent child = ref.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
	public void visit(AValue value){
		tab++;
		debugWriter.write(getTabString() + value.toString());
		tab--;
	}
	public void visit(AData data){
		tab++;
		debugWriter.write(getTabString() + data.toString());
		ActiveComponent[] param = data.getParam();
		if(param != null) next(param);
		ActiveComponent exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) exceptPattern.accept(this);
		tab--;
	}	
	public void visit(AGrammar grammar){
		tab++;
		debugWriter.write(getTabString() + grammar.toString());
		ActiveComponent child = grammar.getChild();
		if(child != null) child.accept(this);
		tab--;
	}
		
		
	public void next(ActiveComponent[] children){		
		for(ActiveComponent child : children){            
			child.accept(this);
		}
	} 
}