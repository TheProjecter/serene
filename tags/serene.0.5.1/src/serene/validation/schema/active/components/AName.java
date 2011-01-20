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

public class AName extends ANameClass{	
	String localPart;
	String ns;
	public AName(String ns, String localPart, String qName, String location, MessageWriter debugWriter){
		super(qName, location, debugWriter);		
		this.ns = ns;
		this.localPart = localPart;		
	}
	
	public boolean matches(String namespace, String name){		
		return ns.equals(namespace) && localPart.equals(name);
	}
	
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}	
	
	public String toString(){
		return "AName "+ns+":"+localPart;
	}	
}