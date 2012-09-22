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

package serene.validation.schema.simplified;

import org.xml.sax.SAXException;

import serene.bind.util.DocumentIndexedData;

public class SChoiceNameClass extends SNameClass{//TODO review	
	SNameClass[] children;
	
	public SChoiceNameClass(SNameClass[] children,
						int recordIndex, 
						DocumentIndexedData documentIndexedData){
		super(recordIndex, documentIndexedData);
		asParent(children);
	}
	
	protected void asParent(SNameClass[] children){
		this.children = children;
		if(children != null){		
			for(int i = 0; i< children.length; i++){
				children[i].setParent(this, i);
			}
		}
	}	
	
	public boolean matches(String namespace, String name){
		if(children != null){
			for(SNameClass child : children){
				if(child.matches(namespace, name)) return true;
			}
		}
		return false;
	}		
	
	public SNameClass[] getChildren(){
		return children;
	}	
	
	public SNameClass getChild(int childIndex){
		if(children == null)return null;
		return children[childIndex];
	}
		
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}	
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
    
    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof SChoiceNameClass))return false;
        SChoiceNameClass other = (SChoiceNameClass)o;
        SNameClass[] otherChildren = other.getChildren();
        if(children == null){
            if(otherChildren != null)return false;
            else return true;
        }
        if(otherChildren == null)return false;
        
        for(SNameClass child : children){
            first:{
                for(SNameClass otherChild : otherChildren){
                    if(otherChild.equals(child))break first;
                }
                return false;
            }
        }
        return true;
    }
    
	public String toString(){
		String s = "SChoiceNameClass";
		if(children != null && children.length != 0){
			s += "[";
			for(SNameClass child : children){
				s += " "+child.toString();
			}
			s += "]";
		}
		return s;
	}
}