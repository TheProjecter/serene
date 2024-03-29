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

public class SName extends SNameClass{	
	String localPart;
	String ns;
	boolean addedBySimplification;
	public SName(String ns, 
	        String localPart, 
	        int recordIndex, 
			DocumentIndexedData documentIndexedData, 
			boolean addedBySimplification){
		super(recordIndex, documentIndexedData);		
		this.ns = ns;
		this.localPart = localPart;	
		this.addedBySimplification = addedBySimplification;
	}
		
	public boolean matches(String namespace, String name){		
		return ns.equals(namespace) && localPart.equals(name);
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
	
	public String getLocalPart(){
		return localPart;
	}
	
    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof SName)) return false;
        SName other = (SName)o;
        String otherNs = other.getNamespaceURI();
        String otherLocalPart = other.getLocalPart();
        boolean equalsNs = false;
        if(ns == null ){
            if(otherNs != null){
                return false;
            }else{
                equalsNs = true; 
            }
        }else{
            if(otherNs == null){
                return false;
            }else{
                equalsNs = otherNs.equals(ns); 
            }
        }
        
        if(localPart == null){
            if(otherLocalPart != null){
                return false;
            }else{
                return equalsNs;
            }
        }else{
            if(otherLocalPart == null){
                return false;
            }else{
                return equalsNs && localPart.equals(otherLocalPart);
            }
        }
    }
    
	public String toString(){
		return "SName "+ns+":"+localPart;
	}	
}