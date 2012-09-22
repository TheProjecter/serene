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

import java.util.List;

import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.ListPatternMatchPath;
import serene.validation.handlers.match.TextMatchPath;
import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.MatchPathPool;

import serene.bind.util.DocumentIndexedData;

public abstract class SMultipleChildrenPattern extends SPattern{
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
	
    public int getSatisfactionIndicator(){
		throw new IllegalStateException();
	}
	
	public int getSaturationIndicator(){
		throw new IllegalStateException();
	}
	
    public boolean isRequiredContent(){
        if(getMinOccurs() == 0) return false;
		for(int i = 0; i < children.length; i++){
			if(children[i].isRequiredContent())return true;
		}
		return false;
    }
    public boolean isChildRequired(int childIndex){
		if(children[childIndex] == null)return false;
		return children[childIndex].isRequiredContent();
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
 	
 	
 	void setElementMatchPathes(String ns, String name, List<ElementMatchPath> pathes, MatchPathPool matchPathPool){
 	    int pathesCount = pathes.size();
 	    
 	    for(SPattern child : children){
 	        if(child.isElementContent())child.setElementMatchPathes(ns, name, pathes, matchPathPool);
 	    }
 	    
 	    if(pathesCount < pathes.size()){
 	        for(; pathesCount < pathes.size(); pathesCount++){
 	            pathes.get(pathesCount).add(this);
 	        }
 	    }
 	}
 	void setAttributeMatchPathes(String ns, String name, List<AttributeMatchPath> pathes, MatchPathPool matchPathPool){
 	    int pathesCount = pathes.size();
 	    
 	    for(SPattern child : children){
 	        if(child.isAttributeContent())child.setAttributeMatchPathes(ns, name, pathes, matchPathPool);
 	    }
 	    
 	    if(pathesCount < pathes.size()){
 	        for(; pathesCount < pathes.size(); pathesCount++){
 	            pathes.get(pathesCount).add(this);
 	        }
 	    }
 	}
 	void setMatchPathes(List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool){
 	    int pathesCount = textMatchPathes.size();
 	    
 	    for(SPattern child : children){
 	        if(child.isTextContent())child.setMatchPathes(textMatchPathes, matchPathPool);
 	    }
 	     	    
 	    if(pathesCount < textMatchPathes.size()){
 	        for(; pathesCount < textMatchPathes.size(); pathesCount++){
 	            textMatchPathes.get(pathesCount).add(this);
 	        }
 	    }
 	}
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool){
        int dataPathesCount = dataMatchPathes.size();
        int valuePathesCount = valueMatchPathes.size();
        int listPatternPathesCount = listPatternMatchPathes.size();
        int textPathesCount = textMatchPathes.size();
 	    
        for(SPattern child : children){
 	        if(child.isCharsContent())child.setMatchPathes(dataMatchPathes, valueMatchPathes, listPatternMatchPathes, textMatchPathes, matchPathPool);
 	    } 	    
 	     	    
 	    if(dataPathesCount < dataMatchPathes.size()){
 	        for(; dataPathesCount < dataMatchPathes.size(); dataPathesCount++){
 	            dataMatchPathes.get(dataPathesCount).add(this);
 	        }
 	    }
 	    if(valuePathesCount < valueMatchPathes.size()){
 	        for(; valuePathesCount < valueMatchPathes.size(); valuePathesCount++){
 	            valueMatchPathes.get(valuePathesCount).add(this);
 	        }
 	    }
 	    if(listPatternPathesCount < listPatternMatchPathes.size()){
 	        for(; listPatternPathesCount < listPatternMatchPathes.size(); listPatternPathesCount++){
 	            listPatternMatchPathes.get(listPatternPathesCount).add(this);
 	        }
 	    }
 	    if(textPathesCount < textMatchPathes.size()){
 	        for(; textPathesCount < textMatchPathes.size(); textPathesCount++){
 	            textMatchPathes.get(textPathesCount).add(this);
 	        }
 	    }
    }
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, MatchPathPool matchPathPool){
        int dataPathesCount = dataMatchPathes.size();
        int valuePathesCount = valueMatchPathes.size();
        int listPatternPathesCount = listPatternMatchPathes.size();
 	    
        for(SPattern child : children){
 	        if(child.isStructuredDataContent())child.setMatchPathes(dataMatchPathes, valueMatchPathes, listPatternMatchPathes, matchPathPool);
 	    } 	    
 	     	    
 	    if(dataPathesCount < dataMatchPathes.size()){
 	        for(; dataPathesCount < dataMatchPathes.size(); dataPathesCount++){
 	            dataMatchPathes.get(dataPathesCount).add(this);
 	        }
 	    }
 	    if(valuePathesCount < valueMatchPathes.size()){
 	        for(; valuePathesCount < valueMatchPathes.size(); valuePathesCount++){
 	            valueMatchPathes.get(valuePathesCount).add(this);
 	        }
 	    }
 	    if(listPatternPathesCount < listPatternMatchPathes.size()){
 	        for(; listPatternPathesCount < listPatternMatchPathes.size(); listPatternPathesCount++){
 	            listPatternMatchPathes.get(listPatternPathesCount).add(this);
 	        }
 	    }
    }
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, MatchPathPool matchPathPool){
        int dataPathesCount = dataMatchPathes.size();
        int valuePathesCount = valueMatchPathes.size();
 	    
        for(SPattern child : children){
 	        if(child.isUnstructuredDataContent())child.setMatchPathes(dataMatchPathes, valueMatchPathes, matchPathPool);
 	    } 	    
 	     	    
 	    if(dataPathesCount < dataMatchPathes.size()){
 	        for(; dataPathesCount < dataMatchPathes.size(); dataPathesCount++){
 	            dataMatchPathes.get(dataPathesCount).add(this);
 	        }
 	    }
 	    if(valuePathesCount < valueMatchPathes.size()){
 	        for(; valuePathesCount < valueMatchPathes.size(); valuePathesCount++){
 	            valueMatchPathes.get(valuePathesCount).add(this);
 	        }
 	    }
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
    
	void asParent(SPattern[] children){
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