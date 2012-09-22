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

import serene.bind.util.DocumentIndexedData;

abstract class SMultipleChildrenPattern extends SPattern{
 	SPattern[] children; 
 	
 	boolean allowsElements;
	boolean allowsAttributes;
	boolean allowsDatas;
	boolean allowsValues;	
	boolean allowsListPatterns;
	boolean allowsText;
	boolean containsRecursion;
	
	SMultipleChildrenPattern(SPattern[] children,
				int recordIndex, 
				DocumentIndexedData documentIndexedData){		
		super(recordIndex, documentIndexedData);
		asParent(children);
	}
	
	void setParent(SRule parent, int childIndex){		 
		this.parent = parent;				
		this.childIndex = childIndex;
		
		if(allowsElements) parent.setAllowsElements();
        if(allowsAttributes) parent.setAllowsAttributes();
        if(allowsDatas) parent.setAllowsDatas();
        if(allowsValues) parent.setAllowsValues();	
        if(allowsListPatterns) parent.setAllowsListPatterns();
        if(allowsText) parent.setAllowsText();
        if(containsRecursion)parent.setContainsRecursion();
	}
	
    void adjust(){
        for(SPattern child : children){
            if(child.containsRecursion()){
                child.adjust();
            }
        }
        if(allowsElements) parent.setAllowsElements();
        if(allowsAttributes) parent.setAllowsAttributes();
        if(allowsDatas) parent.setAllowsDatas();
        if(allowsValues) parent.setAllowsValues();	
        if(allowsListPatterns) parent.setAllowsListPatterns();
        if(allowsText) parent.setAllowsText();
    }
	
	void setAllowsElements(){
 	    allowsElements = true;
 	}
	void setAllowsAttributes(){
 	    allowsAttributes = true;
 	}
	void setAllowsDatas(){
 	    allowsDatas = true;
 	}
	void setAllowsValues(){
 	    allowsValues = true;
 	}	
	void setAllowsListPatterns(){
 	    allowsListPatterns = true;
 	}
	void setAllowsText(){
 	    allowsText = true;
 	}
 	void setContainsRecursion(){
 	    containsRecursion = true;
 	}
 	
 	
	boolean isElementContent(){
        return allowsElements;
    }
	boolean isAttributeContent(){
	    return allowsAttributes;
	}
	boolean isDataContent(){
	    return allowsDatas;
	}
	boolean isValueContent(){
	    return allowsValues;
	}
	boolean isListPatternContent(){
	    return allowsListPatterns;
	}
	boolean isTextContent(){
	    return allowsText;
	}
	boolean isCharsContent(){
	    return allowsDatas || allowsValues || allowsListPatterns || allowsText;
	}	
	boolean isStructuredDataContent(){
	    return allowsDatas || allowsValues || allowsListPatterns;
	}	
	boolean isUnstructuredDataContent(){
	    return allowsDatas || allowsValues;
	}
	boolean containsRecursion(){
        return containsRecursion;
    }
    
	protected void asParent(SPattern[] children){
		this.children = children;
		if(children != null){		
			for(int i = 0; i< children.length; i++){				
				children[i].setParent(this, i);
			}
		}
	}	
	
	public SPattern[] getChildren(){
		return children;
	}	
	
	public SPattern getChild(int childIndex){
		if(children == null)return null;
		return children[childIndex];
	}
	
	public int getChildrenCount(){
        if(children == null) return 0;
		return children.length;
	}	
	
	public String toString(){
		String s = "AbstractMultipleChildrenPattern";
		return s;
	}
}