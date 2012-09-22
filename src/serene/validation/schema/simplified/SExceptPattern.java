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

import org.xml.sax.SAXException;

import serene.validation.schema.DefinitionPointer;

import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.ListPatternMatchPath;
import serene.validation.handlers.match.MatchPathPool;

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
	
	public boolean isRequired(){
	    throw new IllegalStateException();
	}
	public boolean isChildRequired(){
		if(child == null)return false;
		return child.isRequiredContent();
	}
	public boolean isChildRequired(int childIndex){
	    if(childIndex != 0) throw new IllegalStateException();
		if(child == null)return false;
		return child.isRequiredContent();
	}
	
	public void setCharsMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, MatchPathPool matchPathPool){
        if(child.isStructuredDataContent()){
            child.setMatchPathes(dataMatchPathes, valueMatchPathes, listPatternMatchPathes, matchPathPool);
            for(int i = 0; i < dataMatchPathes.size(); i++){
	            dataMatchPathes.get(i).add(this);
	        }
	        for(int i = 0; i < valueMatchPathes.size(); i++){
	            valueMatchPathes.get(i).add(this);
	        }
	        for(int i = 0; i < listPatternMatchPathes.size(); i++){
	            listPatternMatchPathes.get(i).add(this);
	        }
        }
    }
    public void setCharsMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, MatchPathPool matchPathPool){
        if(child.isUnstructuredDataContent()){
            child.setMatchPathes(dataMatchPathes, valueMatchPathes, matchPathPool);
            for(int i = 0; i < dataMatchPathes.size(); i++){
	            dataMatchPathes.get(i).add(this);
	        }
	        for(int i = 0; i < valueMatchPathes.size(); i++){
	            valueMatchPathes.get(i).add(this);
	        }
        }
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
	public int getChildrenCount(){
	    return 1;
	}
	public SPattern getChild(){
		return child;
	}
	
	public SPattern getChild(int childIndex){
	    if(childIndex != 0) throw new IllegalStateException();
		return child;
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
		String s = "SExceptPattern";
		return s;
	}
}