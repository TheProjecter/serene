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

package serene.validation.schema.simplified.components;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.RestrictingVisitor;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import serene.validation.schema.Identifier;

import serene.bind.util.DocumentIndexedData;

public class SExceptNameClass extends SExcept implements Identifier{		
	SNameClass child;
	public SExceptNameClass(SNameClass child,
			int recordIndex, 
			DocumentIndexedData documentIndexedData){
		super(recordIndex, documentIndexedData);
		asParent(child);
	}	
	
	protected void asParent(SNameClass child){
		this.child = child;
		if(child != null){		
			child.setParent(this);
			child.setChildIndex(0);
		}
	}	
	
	public boolean matches(String namespace, String name){
		if(child != null){			
			if(child.matches(namespace, name)) return false;
		}
		return true;
	}	
	
	public SNameClass getChild(){
		return child;
	}	
			
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
    
    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof SExceptNameClass))return false;
        SExceptNameClass other = (SExceptNameClass)o;
        SNameClass otherChild = other.getChild();
        if(child != null){            
            if(otherChild == null)return false;
            return child.equals(otherChild);
        }
        if(otherChild != null)return false;
        return true;        
    }
    
	public String toString(){
		String s = "SExceptNameClass ";
		if(child!=null)s+=child.toString();
		return s;
	}
}