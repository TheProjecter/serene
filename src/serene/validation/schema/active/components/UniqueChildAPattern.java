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

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

public abstract class UniqueChildAPattern extends AbstractAPattern{
	APattern child;
	
	public UniqueChildAPattern(APattern child,
				ActiveModelRuleHandlerPool ruleHandlerPool){		
		super(ruleHandlerPool);
		asParent(child);
	}
		
	
	public void setElementMatches(String ns, String name, List<AElement> elements){
	    if(child.isElementContent()) child.setElementMatches(ns, name, elements);
	}
    public void setAttributeMatches(String ns, String name, List<AAttribute> attributes){
	    if(child.isAttributeContent()) child.setAttributeMatches(ns, name, attributes);
    }    
    
    public void setMatches(List<AText> texts){
        if(child.isTextContent()) child.setMatches(texts);
    }    
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts){
        if(child.isCharsContent()) child.setMatches(datas, values, listPatterns, texts);
    }
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns){
        if(child.isStructuredDataContent()) child.setMatches(datas, values, listPatterns);
    }
    public void setMatches(List<AData> datas, List<AValue> values){
        if(child.isUnstructuredDataContent()) child.setMatches(datas, values);
    }
    
    
	protected void asParent(APattern child){		
		this.child = child;
		if(child != null){
			((AbstractAPattern)child).setParent(this);
			((AbstractAPattern)child).setChildIndex(0);
		}
	}	
	
	public APattern getChild(){
		return child;
	}
	
	public boolean isChildRequired(){
		if(child == null)return false;
		return child.isRequiredContent();
	}
}