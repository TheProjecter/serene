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

import serene.validation.schema.active.components.AbstractAPattern;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.schema.simplified.SimplifiedComponent;

public abstract class NoChildrenAPattern extends AbstractAPattern{ 	 
	NoChildrenAPattern(ActiveModelRuleHandlerPool ruleHandlerPool){		
		super(ruleHandlerPool);
	}
	
	public void setElementMatches(String ns, String name, List<AElement> elements){
	    throw new IllegalStateException();
	}
    public void setAttributeMatches(String ns, String name, List<AAttribute> attributes){
	    throw new IllegalStateException();
    }    
    
    public void setMatches(List<AText> texts){
        throw new IllegalStateException();
    }    
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts){       
        throw new IllegalStateException();
    }
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns){
        throw new IllegalStateException();
    }
    public void setMatches(List<AData> datas, List<AValue> values){
        throw new IllegalStateException();
    }
	
	boolean isInterleaved(){
		throw new UnsupportedOperationException();
	}		
	
	
	
	public boolean isElementContent(){
        return false;
    }
	public boolean isAttributeContent(){
	    return false;
	}
	public boolean isDataContent(){
	    return false;
	}
	public boolean isValueContent(){
	    return false;
	}
	public boolean isListPatternContent(){
	    return false;
	}
	public boolean isTextContent(){
	    return false;
	}
	public boolean isCharsContent(){
	    return false;
	}	
	public boolean isStructuredDataContent(){
	    return false;
	}	
	public boolean isUnstructuredDataContent(){
	    return false;
	}
	
	public String toString(){
		String s = "NoChildrenAPattern";
		return s;
	}
}