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

import serene.validation.schema.simplified.components.SNsName;

import serene.validation.schema.active.components.AExceptNameClass;

import serene.validation.schema.active.ActiveComponentVisitor;

public class ANsName extends AbstractWildCard{		
	String ns;
	SNsName snsName;
	public ANsName(String ns, AExceptNameClass child,
			SNsName snsName){	
		super(child);
		this.ns = ns;
		this.snsName = snsName;
	}
	
	public boolean matches(String namespace, String name){
		if(child != null) return ns.equals(namespace) && child.matches(namespace, name);
		return ns.equals(namespace);
	}

	public String getQName(){
		return snsName.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return snsName.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return snsName.hashCode();
    }   
    
    public SNsName getCorrespondingSimplifiedComponent(){
        return snsName;
    }
    
    
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}	
	
	public String toString(){
		String s = "ANsName ";
		if(child != null){
			s+= "[";
			s+= child.toString();
			s+="]";
		}
		return s;
	}
}