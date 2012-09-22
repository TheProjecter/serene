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

public class SRef extends SUniqueChildPattern implements DefinitionPointer{
	int definitionIndex;
	ReferenceModel referenceModel;
	
	public SRef(int definitionIndex,
	                ReferenceModel referenceModel,
					int recordIndex, 
					DocumentIndexedData documentIndexedData){
		super(null, recordIndex, documentIndexedData);
		this.definitionIndex = definitionIndex;
		this.referenceModel = referenceModel;
		
		setContainsRecursion();
	}
	
	public SRef(SPattern child,
	                int definitionIndex,
	                ReferenceModel referenceModel,
					int recordIndex, 
					DocumentIndexedData documentIndexedData){
		super(child, recordIndex, documentIndexedData);
		this.definitionIndex = definitionIndex;
		this.referenceModel = referenceModel;
	}
		
	
	public boolean isIntermediary(){
	    return true;
	}
	
	public boolean isRequiredContent(){
        if(minOccurs == 0) return false;	
		return child.isRequiredContent();
    }
    
	void setChild(){
	    child = referenceModel.getRefDefinitionTopPattern(definitionIndex);
	    asParent(child);
	}
	
	void adjust(){
	    if(child == null)setChild();
	    
	    if(allowsElements) parent.setAllowsElements();
        if(allowsAttributes) parent.setAllowsAttributes();
        if(allowsDatas) parent.setAllowsDatas();
        if(allowsValues) parent.setAllowsValues();	
        if(allowsListPatterns) parent.setAllowsListPatterns();
        if(allowsText) parent.setAllowsText();
	}
	
	public int functionalEquivalenceCode(){
        return child.hashCode();
    }   
	public SPattern getChild(){
	    if(child == null) setChild();
		return child;
	}	
	
	public int getDefinitionIndex(){
		return definitionIndex;
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
		//String s = "SRef \""+definitionIndex+"\""+ " "+hashCode();
		String s = "SRef \""+definitionIndex+"\"";
		return s;
	}
}

