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

package serene.validation.schema.active.components;

import serene.validation.schema.active.components.ANameClass;

import serene.validation.schema.active.ActiveComponentVisitor;

import sereneWrite.MessageWriter;

public class AChoiceNameClass extends ANameClass{	
	ANameClass[] children;
	
	public AChoiceNameClass(ANameClass[] children,
						String qName, String location, 
						MessageWriter debugWriter){
		super(qName, location, debugWriter);
		asParent(children);
	}
	
	protected void asParent(ANameClass[] children){
		this.children = children;
		if(children != null){		
			for(int i = 0; i< children.length; i++){
				children[i].setParent(this);
				children[i].setChildIndex(i);
			}
		}
	}	
	
	public ANameClass[] getChildren(){
		return children;
	}	
	
	public ANameClass getChild(int childIndex){
		if(children == null)return null;
		return children[childIndex];
	}
	
	public boolean matches(String namespace, String name){
		if(children != null){
			for(ANameClass child : children){
				if(child.matches(namespace, name)) return true;
			}
		}
		return false;
	}		
	
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}	
	
	public String toString(){
		String s = "AChoiceNameClass";
		if(children != null && children.length != 0){
			s += "[";
			for(ANameClass child : children){
				s += " "+child.toString();
			}
			s += "]";
		}
		return s;
	}
}