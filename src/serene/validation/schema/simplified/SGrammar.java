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

public class SGrammar extends SUniqueChildPattern{	
	
	public SGrammar(SPattern child, 
	            int recordIndex, 
				DocumentIndexedData documentIndexedData){
		super(child, recordIndex, documentIndexedData);
	}
		
	
	/*public boolean isElementContent(){
        if(child == null) return false;
        return child.isElementContent();
    }
	public boolean isAttributeContent(){
	    if(child == null) return false;
	    return child.isAttributeContent();
	}
	public boolean isDataContent(){
	    if(child == null) return false;
	    return child.isDataContent();
	}
	public boolean isValueContent(){
	    if(child == null) return false;
	    return child.isValueContent();
	}
	public boolean isListPatternContent(){
	    if(child == null) return false;
	    return child.isListPatternContent();
	}
	public boolean isTextContent(){
	    if(child == null) return false;
	    return child.isTextContent();
	}
	public boolean isCharsContent(){
	    if(child == null) return false;
	    return child.isCharsContent();
	}	
	public boolean isStructuredDataContent(){
	    if(child == null) return false;
	    return child.isStructuredDataContent();
	}	
	public boolean isUnstructuredDataContent(){
	    if(child == null) return false;
	    return child.isUnstructuredDataContent();
	}*/
	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String toString(){
		String s = "SGrammar";		
		return s;
	}

}