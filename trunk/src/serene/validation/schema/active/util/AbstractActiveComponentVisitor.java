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

package serene.validation.schema.active.util;


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

import serene.validation.schema.active.ActiveComponent;
import serene.validation.schema.active.ActiveComponentVisitor;

public abstract class AbstractActiveComponentVisitor implements ActiveComponentVisitor{

	public void visit(AParam param){}	
	public void visit(AExceptPattern exceptAPattern){
		ActiveComponent child = exceptAPattern.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(AExceptNameClass exceptANameClass){
		ActiveComponent child = exceptANameClass.getChild();
		if(child != null) child.accept(this);		
	}
		
	public void visit(AName component){}
	public void visit(AAnyName anyName){
		ActiveComponent child = anyName.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(ANsName nsName){
		ActiveComponent child = nsName.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(AChoiceNameClass choice){
		ActiveComponent[] children = choice.getChildren();
		if(children != null) next(children);
	}	
	
	
	public void visit(AElement element){
		ActiveComponent nameClass = element.getNameClass();
		if(nameClass != null) nameClass.accept(this);
		ActiveComponent child = element.getChild();
		if(child != null) child.accept(this);	
	}	
	public void visit(AAttribute attribute){
		ActiveComponent nameClass = attribute.getNameClass();
		if(nameClass != null) nameClass.accept(this);		
		ActiveComponent child = attribute.getChild();
		if(child != null) child.accept(this);		
	}
	public void visit(AChoicePattern choice){
		ActiveComponent[] children = choice.getChildren();
		if(children != null) next(children);
	}
	public void visit(AInterleave interleave){
		ActiveComponent[] children = interleave.getChildren();
		if(children != null) next(children);
	}
	public void visit(AGroup group){
		ActiveComponent[] children = group.getChildren();
		if(children != null) next(children);
	}
	public void visit(AListPattern list){
		ActiveComponent child = list.getChild();
		if(child != null) child.accept(this);
	}	
	public void visit(AEmpty empty){}
	public void visit(AText text){}
	public void visit(ANotAllowed notAllowed){}
	public void visit(ARef ref){}
	public void visit(AValue value){}
	public void visit(AData data){	
		ActiveComponent[] param = data.getParam();
		if(param != null) next(param);
		ActiveComponent exceptAPattern = data.getExceptPattern();
		if(exceptAPattern != null) exceptAPattern.accept(this);
	}	
	public void visit(AGrammar grammar){
		ActiveComponent child = grammar.getChild();
		if(child != null) child.accept(this);
	}
		
		
	protected void next(ActiveComponent[] children){
		for(ActiveComponent child : children){			
			child.accept(this);
		}
	} 
}