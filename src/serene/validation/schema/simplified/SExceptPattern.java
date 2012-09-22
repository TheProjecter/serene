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

public class SExceptPattern extends SRule implements DefinitionPointer, StructuredDataType{
    SRule parent;
	SPattern child;
	
	int definitionIndex;
	
	boolean allowsDatas;
	boolean allowsValues;	
	boolean allowsListPatterns;
	public SExceptPattern(int definitionIndex,
	        SPattern child,
			int recordIndex, 
			DocumentIndexedData documentIndexedData){
		super(recordIndex, documentIndexedData);
		this.definitionIndex = definitionIndex;
		asParent(child);		
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
	public boolean allowsUnstructuredDataContent(){
	    return allowsDatas || allowsValues;
	}
	public boolean allowsStructuredDataContent(){
	    return allowsDatas || allowsValues || allowsListPatterns;
	}
	
	void setAllowsElements(){}
	void setAllowsAttributes(){}
	void setAllowsDatas(){
 	    allowsDatas = true;
 	}
	void setAllowsValues(){
 	    allowsValues = true;
 	}	
	void setAllowsListPatterns(){
 	    allowsListPatterns = true;
 	}
 	void setAllowsText(){}
 	void setContainsRecursion(){}
 	
 	
	public SRule getParent(){
	    return parent;
	}
	protected void asParent(SPattern child){
		this.child = child;
		if(child != null){	
			child.setParent(this, 0);
		}
	}	
				
	public int getDefinitionIndex(){
	    return definitionIndex;
	}
	
	public SPattern getChild(){
		return child;
	}
	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}	
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String toString(){
		String s = "SExceptPattern";
		return s;
	}
}