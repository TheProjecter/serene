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

package serene.validation.schema.parsed.components;

import java.util.Map;

import org.xml.sax.SAXException;

import serene.validation.schema.parsed.ParsedComponentVisitor;
import serene.validation.schema.parsed.SimplifyingVisitor;

import sereneWrite.MessageWriter;

public class ExceptNameClass extends Except{		
	NameClass[] children;
	public ExceptNameClass(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, NameClass[] children, String qName, String location,  
			MessageWriter debugWriter){
		super(prefixMapping, xmlBase, ns, datatypeLibrary, qName, location, debugWriter);
		asParent(children);
	}	
	
	protected void asParent(NameClass[] children){
		this.children = children;
		if(children != null){		
			for(int i = 0; i< children.length; i++){
				children[i].setParent(this);
				children[i].setChildIndex(i);
			}
		}
	}	
	
	public NameClass[] getChildren(){
		return children;
	}	
	
	public NameClass getChild(int childIndex){
		if(children == null)return null;
		return children[childIndex];
	}
			
	public void accept(ParsedComponentVisitor v){
		v.visit(this);
	}

	public void accept(SimplifyingVisitor v)throws SAXException{
		v.visit(this);
	}
		
	public String toString(){
		String s = "ExceptNameClass";
		if(children != null && children.length != 0){
			s += "[";
			for(NameClass child : children){
				s += " "+child.toString();
			}
			s += "]";
		}
		return s;
	}
}