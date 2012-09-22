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

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SGrammar;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.ValidatorRuleHandlerPool;

public class AGrammar extends UniqueChildAPattern implements AInnerPattern{	
	SGrammar sgrammar;
	
	boolean allowsElements;
	boolean allowsAttributes;
	boolean allowsDatas;
	boolean allowsValues;	
	boolean allowsListPatterns;
	boolean allowsText;
	
	public AGrammar(APattern child,
	            boolean allowsElements,
                boolean allowsAttributes,
                boolean allowsDatas,
                boolean allowsValues,	
                boolean allowsListPatterns,
                boolean allowsText,
				ValidatorRuleHandlerPool ruleHandlerPool,
				SGrammar sgrammar){
		super(child, ruleHandlerPool);
		this.allowsElements = allowsElements;
	    this.allowsAttributes = allowsAttributes;
	    this.allowsDatas = allowsDatas;
	    this.allowsValues = allowsValues;	
	    this.allowsListPatterns = allowsListPatterns;
	    this.allowsText = allowsText;
		this.sgrammar = sgrammar;
	}
		
    public int getMinOccurs(){
	    return sgrammar.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return sgrammar.getMaxOccurs();
	}
	
		
	/*public boolean isElementContent(){
        return sgrammar.isElementContent();
    }
	public boolean isAttributeContent(){
	    return sgrammar.isAttributeContent();
	}
	public boolean isDataContent(){
	    return sgrammar.isDataContent();
	}
	public boolean isValueContent(){
	    return sgrammar.isValueContent();
	}
	public boolean isListPatternContent(){
	    return sgrammar.isListPatternContent();
	}
	public boolean isTextContent(){
	    return sgrammar.isTextContent();
	}
	public boolean isCharsContent(){
	    return sgrammar.isCharsContent();
	}	
	public boolean isStructuredDataContent(){
	    return sgrammar.isStructuredDataContent();
	}	
	public boolean isUnstructuredDataContent(){
	    return sgrammar.isUnstructuredDataContent();
	}*/
	
	
	public boolean isRequiredContent(){
		if(getMinOccurs() == 0) return false;
		if(child != null)
			return child.isRequiredContent();
		return false;		
	}
	
	public String getQName(){
		return sgrammar.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sgrammar.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return sgrammar.hashCode();
    }   
    
    public SGrammar getCorrespondingSimplifiedComponent(){
        return sgrammar;
    }
	
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}
	/*public GrammarHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getStructureHandler(this, errorCatcher, parent, stackHandler);
	}
	
	public GrammarMinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMinimalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	public GrammarMaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMaximalReduceHandler(this, errorCatcher, parent, stackHandler);
	}*/
	
	boolean isInterleaved(){
		if(parent instanceof AbstractAPattern)return ((AbstractAPattern)parent).isInterleaved();
		return false;
	}	
	
	public String toString(){
		String s = "AGrammar"+ " min "+getMinOccurs()+" max "+getMaxOccurs();		
		return s;
	}

}