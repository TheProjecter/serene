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

import serene.validation.schema.simplified.components.SExceptNameClass;

import serene.bind.util.DocumentIndexedData;

import sereneWrite.MessageWriter;

public class SNsName extends AbstractWildCard{		
	String ns;
	public SNsName(String ns, 
	        SExceptNameClass child,
			int recordIndex, 
			DocumentIndexedData documentIndexedData,  
			MessageWriter debugWriter){	
		super(child, recordIndex, documentIndexedData, debugWriter);
		this.ns = ns;
	}
	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}	
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String getNamespaceURI(){
		return ns;
	}
	

    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof SNsName))return false;
        SNsName other = (SNsName)o;
        String otherNs = other.getNamespaceURI();
        boolean nsEquals = false;
        if(ns != null){            
            if(otherNs != null)nsEquals = ns.equals(otherNs);            
        }else{
            if(otherNs == null)nsEquals = true;
        }
        SExceptNameClass otherChild = other.getExceptNameClass();        
        if(child != null){            
            if(otherChild == null)return false;
            return nsEquals && child.equals(otherChild);
        }
        if(otherChild != null)return false;
        return nsEquals;        
    }
    
    
	public String toString(){
		String s = "SNsName /"+ns+"/ ";
		if(child != null){
			s+= "[";
			s+= child.toString();
			s+="]";
		}
		return s;
	}
}