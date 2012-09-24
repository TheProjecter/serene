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

package serene.validation.schema.simplified.util;


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

import serene.validation.schema.simplified.SName;
import serene.validation.schema.simplified.SAnyName;
import serene.validation.schema.simplified.SNsName;
import serene.validation.schema.simplified.SChoiceNameClass;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

public abstract class AbstractSimplifiedComponentVisitor implements SimplifiedComponentVisitor{
	
	public void visit(SExceptPattern exceptPattern){
		SimplifiedComponent child = exceptPattern.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(SExceptNameClass exceptNameClass){
		SimplifiedComponent child = exceptNameClass.getChild();
		if(child != null) child.accept(this);		
	}
		
	public void visit(SName component){}
	public void visit(SAnyName anyName){
		SimplifiedComponent child = anyName.getExceptNameClass();
		if(child != null) child.accept(this);
	}
	public void visit(SNsName nsName){
		SimplifiedComponent child = nsName.getExceptNameClass();
		if(child != null) child.accept(this);
	}
	public void visit(SChoiceNameClass choice){
		SimplifiedComponent[] children = choice.getChildren();
		if(children != null) next(children);
	}	
	
	
	public void visit(SElement element){
		SimplifiedComponent nameClass = element.getNameClass();
		if(nameClass != null) nameClass.accept(this);
		SimplifiedComponent child = element.getChild();
		if(child != null) child.accept(this);	
	}	
	public void visit(SAttribute attribute){
		SimplifiedComponent nameClass = attribute.getNameClass();
		if(nameClass != null) nameClass.accept(this);		
		SimplifiedComponent[] children = attribute.getChildren();
		if(children != null)next(children);		
	}
	public void visit(SChoicePattern choice){
		SimplifiedComponent[] children = choice.getChildren();
		if(children != null) next(children);
	}
	public void visit(SInterleave interleave){
		SimplifiedComponent[] children = interleave.getChildren();
		if(children != null) next(children);
	}
	public void visit(SGroup group){
		SimplifiedComponent[] children = group.getChildren();
		if(children != null) next(children);
	}
	public void visit(SListPattern list){
		SimplifiedComponent child = list.getChild();
		if(child != null) child.accept(this);
	}	
	public void visit(SEmpty empty){}
	public void visit(SText text){}
	public void visit(SNotAllowed notAllowed){}
	public void visit(SRef ref){}
	public void visit(SValue value){}
	public void visit(SData data){
		SimplifiedComponent[] exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) next(exceptPattern);
	}	
	public void visit(SGrammar grammar){
		SimplifiedComponent child = grammar.getChild();
		if(child != null) child.accept(this);
	}
		
		
	protected void next(SimplifiedComponent[] children){
		for(SimplifiedComponent child : children){            
			child.accept(this);
		}
	} 
}