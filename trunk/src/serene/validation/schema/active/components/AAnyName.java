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

import serene.validation.schema.active.components.AExceptNameClass;

import serene.validation.schema.active.ActiveComponentVisitor;

import serene.validation.schema.simplified.components.SAnyName;

import sereneWrite.MessageWriter;

public class AAnyName extends AbstractWildCard{		
    SAnyName sanyName;
	public AAnyName(AExceptNameClass child,
			SAnyName sanyName, 
			MessageWriter debugWriter){
		super(child, debugWriter);
		this.sanyName = sanyName;
	}
	
	public boolean matches(String namespace, String name){
		if(child != null) return child.matches(namespace, name);
		return true;
	}	
	
	public String getQName(){
		return sanyName.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sanyName.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return sanyName.hashCode();
    }   
    
    public SAnyName getCorrespondingSimplifiedComponent(){
        return sanyName;
    }
    
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}	
	
	public String toString(){
		String s = "AAnyName";
		if(child != null){
			s+= "[";
			s+= child.toString();
			s+="]";
		}
		return s;
	}
	
}