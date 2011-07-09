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

import serene.validation.schema.active.ActiveComponentVisitor;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.Identifier;

import sereneWrite.MessageWriter;

public class AExceptNameClass extends AbstractIdentifier{		
	ANameClass child;
	public AExceptNameClass(ANameClass child,
			SimplifiedComponent simplifiedComponent, 
			MessageWriter debugWriter){
		super(simplifiedComponent, debugWriter);
		asParent(child);
	}	
	
	protected void asParent(ANameClass child){
		this.child = child;
		if(child != null){	
			child.setParent(this);
			child.setChildIndex(0);
		}
	}	
	
	public ANameClass getChild(){
		return child;
	}	
		
	public boolean matches(String namespace, String name){
		if(child != null){			
			if(child.matches(namespace, name)) return false;
		}
		return true;
	}	
		
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	
	public String toString(){
		String s = "AExceptNameClass ";
		if(child!=null)s+=child.toString();
		return s;
	}
}