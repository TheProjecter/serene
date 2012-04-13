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

package serene.validation.handlers.structure.util;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
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

import serene.validation.schema.active.ActiveComponentVisitor;

public class ChildFinder implements ActiveComponentVisitor{	
	APattern parent;
	APattern child;

	public ChildFinder(){				
	}
	
	
	public APattern findChild(APattern top, APattern bottom){
		parent = top;
		child = null;
		bottom.accept(this);
		return child;
	}
	
	
	// TODO	
	public void visit(AExceptPattern except){
		
	}
	
	public void visit(AExceptNameClass component){}		
	
	public void visit(AListPattern list){
		Rule localParent = list.getParent();		
		if(parent == localParent){
			child = list;
			return;
		}
		localParent.accept(this);
	}
	public void visit(AEmpty empty){
		Rule localParent = empty.getParent();		
		if(parent == localParent){
			child = empty;
			return;
		}
		localParent.accept(this);
	}
	public void visit(AText text){
		Rule localParent = text.getParent();		
		if(parent == localParent){
			child = text;
			return;
		}
		localParent.accept(this);
	}
	public void visit(ANotAllowed notAllowed){
		Rule localParent = notAllowed.getParent();		
		if(parent == localParent){
			child = notAllowed;
			return;
		}
		localParent.accept(this);
	}	
	public void visit(AValue value){
		Rule localParent = value.getParent();		
		if(parent == localParent){
			child = value;
			return;
		}
		localParent.accept(this);
	}
	public void visit(AData data){
		Rule localParent = data.getParent();		
		if(parent == localParent){
			child = data;
			return;
		}
		localParent.accept(this);
	}
	
	public void visit(AName component){}
	public void visit(AAnyName component){}
	public void visit(ANsName component){}
	public void visit(AChoiceNameClass component){}	
	
	public void visit(AElement element){		
		Rule localParent = element.getParent();		
		if(parent == localParent){
			child = element;
			return;
		}
		localParent.accept(this);
	}

	public void visit(AAttribute attribute){
		Rule localParent = attribute.getParent();		
		if(parent == localParent){
			child = attribute;
			return;
		}
		localParent.accept(this);
	}	
	public void visit(AChoicePattern choice){		
		Rule localParent = choice.getParent();		
		if(parent == localParent){
			child = choice;
			return;
		}
		localParent.accept(this);
	}
	
	public void visit(AInterleave interleave){
		Rule localParent = interleave.getParent();		
		if(parent == localParent){
			child = interleave;
			return;
		}
		localParent.accept(this);
	}
	
	public void visit(AGroup group){
		Rule localParent = group.getParent();		
		if(parent == localParent){
			child = group;
			return;
		}
		localParent.accept(this);
	}
	
	public void visit(ARef ref){		
		Rule localParent = ref.getParent();		
		if(parent == localParent){
			child = ref;
			return;
		}
		localParent.accept(this);
	}
	
	public void visit(AGrammar grammar){	
		Rule localParent = grammar.getParent();		
		if(parent == localParent){
			child = grammar;
			return;
		}
		localParent.accept(this);
	}	
}