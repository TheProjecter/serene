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

public class SDummy extends SMultipleChildrenPattern{	
	public SDummy(SPattern[] children,
				int recordIndex, 
				DocumentIndexedData documentIndexedData){	
		super(children, recordIndex, documentIndexedData);
	}
	
	
	boolean isElementContent(){
        return false;
    }
	boolean isAttributeContent(){
	    return false;
	}
	boolean isDataContent(){
	    return false;
	}
	boolean isValueContent(){
	    return false;
	}
	boolean isListPatternContent(){
	    return false;
	}
	boolean isTextContent(){
	    return false;
	}
	boolean isCharsContent(){
	    return false;
	}	
	boolean isStructuredDataContent(){
	    return false;
	}	
	boolean isUnstructuredDataContent(){
	    return false;
	}
	
	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}	
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public void accept(SimplifiedRuleVisitor v){
		v.visit(this);
	}
	public String toString(){
		String s = "SDummy";		
		return s;
	}
}