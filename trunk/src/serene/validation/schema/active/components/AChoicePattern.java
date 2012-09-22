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

import serene.validation.schema.active.components.APattern;

import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.RuleVisitor;

import serene.validation.schema.simplified.SChoicePattern;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.ChoiceHandler;
import serene.validation.handlers.structure.ChoiceMinimalReduceHandler;
import serene.validation.handlers.structure.ChoiceMaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.ValidatorRuleHandlerPool;

public class AChoicePattern extends MultipleChildrenAPattern  implements AInnerPattern{
    SChoicePattern schoicePattern;
	public AChoicePattern(APattern[] children,
        	    boolean allowsElements,
                boolean allowsAttributes,
                boolean allowsDatas,
                boolean allowsValues,	
                boolean allowsListPatterns,
                boolean allowsText,
				ValidatorRuleHandlerPool ruleHandlerPool,
				SChoicePattern schoicePattern){		
		super(children, 
		        allowsElements,
                allowsAttributes,
                allowsDatas,
                allowsValues,	
                allowsListPatterns,
                allowsText,
                ruleHandlerPool);
		this.schoicePattern = schoicePattern;
	}

	/*public boolean isElementContent(){
        return schoicePattern.isElementContent();
    }
	public boolean isAttributeContent(){
	    return schoicePattern.isAttributeContent();
	}
	public boolean isDataContent(){
	    return schoicePattern.isDataContent();
	}
	public boolean isValueContent(){
	    return schoicePattern.isValueContent();
	}
	public boolean isListPatternContent(){
	    return schoicePattern.isListPatternContent();
	}
	public boolean isTextContent(){
	    return schoicePattern.isTextContent();
	}
	public boolean isCharsContent(){
	    return schoicePattern.isCharsContent();
	}	
	public boolean isStructuredDataContent(){
	    return schoicePattern.isStructuredDataContent();
	}	
	public boolean isUnstructuredDataContent(){
	    return schoicePattern.isUnstructuredDataContent();
	}*/
	
	public int getMinOccurs(){
	    return schoicePattern.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return schoicePattern.getMaxOccurs();
	}
	
	public boolean isRequiredContent(){
		if(getMinOccurs() == 0) return false;		
		for(int i = 0; i < children.length; i++){
			if(!children[i].isRequiredContent())return false;
		}
		return true;
	}
	
    boolean isChildBranchRequired(AbstractAPattern child){
        for(int i = 0; i < children.length; i++){
            if(children[i] != child && !children[i].isRequiredContent())return false;
        }
        return true;
    }
    
    
    public String getQName(){
		return schoicePattern.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return schoicePattern.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return schoicePattern.hashCode();
    }   
    
    public SChoicePattern getCorrespondingSimplifiedComponent(){
        return schoicePattern;
    }
    
    
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}
		
	/*public ChoiceHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getStructureHandler(this, errorCatcher, parent, stackHandler);
	}
		
	public ChoiceMinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMinimalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	public ChoiceMaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMaximalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	*/
	
	public String toString(){
		String s = "AChoicePattern"+ " min "+getMinOccurs()+" max "+getMaxOccurs();
		return s;
	}
}