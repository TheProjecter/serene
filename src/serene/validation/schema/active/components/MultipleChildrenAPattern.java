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

import java.util.List;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AbstractAPattern;

import serene.validation.handlers.structure.ValidatorRuleHandlerPool;

public abstract class MultipleChildrenAPattern extends AbstractAPattern{
 	protected APattern[] children; 
 	
 	boolean allowsElements;
	boolean allowsAttributes;
	boolean allowsDatas;
	boolean allowsValues;	
	boolean allowsListPatterns;
	boolean allowsText;
	
	MultipleChildrenAPattern(APattern[] children,
	            boolean allowsElements,
                boolean allowsAttributes,
                boolean allowsDatas,
                boolean allowsValues,	
                boolean allowsListPatterns,
                boolean allowsText,
				ValidatorRuleHandlerPool ruleHandlerPool){		
		super(ruleHandlerPool);
		this.allowsElements = allowsElements;
	    this.allowsAttributes = allowsAttributes;
	    this.allowsDatas = allowsDatas;
	    this.allowsValues = allowsValues;	
	    this.allowsListPatterns = allowsListPatterns;
	    this.allowsText = allowsText; 
		asParent(children);
	}
		
	protected void asParent(APattern[] children){
		this.children = children;
		if(children != null){		
			for(int i = 0; i< children.length; i++){
				((AbstractAPattern)children[i]).setParent(this);
				((AbstractAPattern)children[i]).setChildIndex(i);
			}
		}
	}	
	
	
	
	public boolean isElementContent(){
        return allowsElements;
    }
	public boolean isAttributeContent(){
	    return allowsAttributes;
	}
	public boolean isDataContent(){
	    return allowsDatas;
	}
	public boolean isValueContent(){
	    return allowsValues;
	}
	public boolean isListPatternContent(){
	    return allowsListPatterns;
	}
	public boolean isTextContent(){
	    return allowsText;
	}
	public boolean isCharsContent(){
	    return allowsDatas || allowsValues || allowsListPatterns || allowsText;
	}	
	public boolean isStructuredDataContent(){
	    return allowsDatas || allowsValues || allowsListPatterns;
	}	
	public boolean isUnstructuredDataContent(){
	    return allowsDatas || allowsValues;
	}
	
	
	
	public void setElementMatches(String ns, String name, List<AElement> elements){
	    for(APattern child : children){
	        if(child.isElementContent()) child.setElementMatches(ns, name, elements);
	    }
	}
    public void setAttributeMatches(String ns, String name, List<AAttribute> attributes){
        for(APattern child : children){
	        if(child.isAttributeContent()) child.setAttributeMatches(ns, name, attributes);
	    }
    }    
    
    public void setMatches(List<AText> texts){
        for(APattern child : children){
	        if(child.isTextContent()) child.setMatches(texts);
	    }
    }    
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts){
        for(APattern child : children){
	        if(child.isCharsContent()) child.setMatches(datas, values, listPatterns, texts);
	    }
    }
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns){
        for(APattern child : children){
	        if(child.isStructuredDataContent()) child.setMatches(datas, values, listPatterns);
	    }
    }
    public void setMatches(List<AData> datas, List<AValue> values){
        for(APattern child : children){
	        if(child.isUnstructuredDataContent()) child.setMatches(datas, values);
	    }
    }
    
    
	
	public APattern[] getChildren(){
		return children;
	}	
	
	public APattern getChild(int childIndex){
		if(children == null)return null;
		return children[childIndex];
	}
	
	public int getChildrenCount(){
        if(children == null) return 0;
		return children.length;
	}	
	
	public boolean isChildRequired(int childIndex){
		if(children[childIndex] == null)return false;
		return children[childIndex].isRequiredContent();
	}
	
	public boolean isRequiredContent(){
		if(getMinOccurs() == 0) return false;		
		for(int i = 0; i < children.length; i++){
			if(children[i].isRequiredContent())return true;
		}
		return false;
	}
		
	public String toString(){
		String s = "MultipleChildrenAPattern";
		return s;
	}
}