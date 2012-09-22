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

abstract class SUniqueChildPattern extends SPattern{
	SPattern child;
	boolean allowsElements;
	boolean allowsAttributes;
	boolean allowsDatas;
	boolean allowsValues;	
	boolean allowsListPatterns;
	boolean allowsText;
	boolean containsRecursion;
	
	public SUniqueChildPattern(SPattern child,
				int recordIndex, 
				DocumentIndexedData documentIndexedData){		
		super(recordIndex, documentIndexedData);
		asParent(child);
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
        // Recursion is being transmited all the way up to the first containing 
        // type. In case of blind recursions this will go out the recursion loop,
        // but it's not an issue since it's an error anyway and the SRef will
        // not be resolved in that case. 
	}
	
	void adjust(){
        if(!child.containsRecursion())throw new IllegalStateException();
        child.adjust();
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
	
	protected void asParent(SPattern child){		
		this.child = child;
		if(child != null){		
			child.setParent(this, 0);
		}
	}	
	
	public SPattern getChild(){
		return child;
	}	
		
	public String toString(){
		String s = "Attribute";		
		return s;
	}
}