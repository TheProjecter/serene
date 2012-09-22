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

import serene.validation.schema.DefinitionPointer;

import serene.bind.util.DocumentIndexedData;

public class SElement extends SUniqueChildPattern implements DefinitionPointer, 
                                                                CharsType,
                                                                ElementContentType,
                                                                AttributesType{
	SNameClass nameClass;	
	int definitionIndex;
	public SElement(int definitionIndex,
	            SNameClass nameClass,
				SPattern child,
				int recordIndex, 
				DocumentIndexedData documentIndexedData,
				RecursionModel recursionModel){		
		super(child, recordIndex, documentIndexedData);
		this.nameClass = nameClass;
		this.definitionIndex = definitionIndex;
		if(containsRecursion) recursionModel.add(this);
	}
	
	void adjustForRecursions(){
	    if(child.containsRecursion()) child.adjust();
	}
	
	void setParent(SRule parent, int childIndex){		 
		this.parent = parent;				
		this.childIndex = childIndex;
		
		parent.setAllowsElements();
	}
	
	
	public boolean allowsElements(){
	    return allowsElements;
	}
	public boolean allowsAttributes(){
	    return allowsAttributes;
	}
	public boolean allowsDatas(){
	    return allowsDatas;
	}
	public boolean allowsValues(){
	    return allowsValues;
	}
	public boolean allowsListPatterns(){
	    return allowsListPatterns;
	}
	public boolean allowsText(){
	    return allowsText;
	}
	public boolean allowsUnstructuredDataContent(){
	    return allowsDatas || allowsValues;
	}
	public boolean allowsStructuredDataContent(){
	    return allowsDatas || allowsValues || allowsListPatterns;
	}
	public boolean allowsCharsContent(){
	    return allowsDatas || allowsValues || allowsListPatterns || allowsText;
	}
	
	
	boolean isElementContent(){
        return true;
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
	
	
	public int getDefinitionIndex(){
	    return definitionIndex;
	}
	
	public SNameClass getNameClass(){
		return nameClass;
	}
	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String toString(){
		String s = "SElement "+nameClass;				
		return s;
	}
}