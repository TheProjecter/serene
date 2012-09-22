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

import serene.bind.util.DocumentIndexedData;

import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.ListPatternMatchPath;
import serene.validation.handlers.match.TextMatchPath;
import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.MatchPathPool;


public class SListPattern extends SUniqueChildPattern implements DataType{
	public SListPattern(SPattern child, 
	            int recordIndex, 
				DocumentIndexedData documentIndexedData){
		super(child, recordIndex, documentIndexedData);
	}	
		
	void setParent(SRule parent, int childIndex){		 
		this.parent = parent;				
		this.childIndex = childIndex;
		
        parent.setAllowsListPatterns();
	}
	
	public boolean isRequiredContent(){
		return minOccurs != 0;
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
    
    
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool){
        ListPatternMatchPath mp = matchPathPool.getListPatternMatchPath();
        mp.addListPattern(this);
        listPatternMatchPathes.add(mp);
    }
	void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, MatchPathPool matchPathPool){            
 	    ListPatternMatchPath mp = matchPathPool.getListPatternMatchPath();
        mp.addListPattern(this);
        listPatternMatchPathes.add(mp);
    }
    
	public boolean allowsDatas(){
	    return allowsDatas;
	}
	public boolean allowsValues(){
	    return allowsValues;
	}
		
	public boolean allowsUnstructuredDataContent(){
	    return allowsDatas || allowsValues;
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
	    return true;
	}
	boolean isTextContent(){
	    return false;
	}
	boolean isCharsContent(){
	    return true;
	}	
	boolean isStructuredDataContent(){
	    return true;
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
		String s = "SList";		
		return s;
	}
}