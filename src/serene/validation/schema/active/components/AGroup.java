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

import serene.util.IntList;

import serene.validation.schema.active.components.APattern;

import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.RuleVisitor;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.GroupHandler;
import serene.validation.handlers.structure.impl.GroupMinimalReduceHandler;
import serene.validation.handlers.structure.impl.GroupMaximalReduceHandler;
import serene.validation.handlers.structure.impl.GroupMinimalReduceCountHandler;
import serene.validation.handlers.structure.impl.GroupMaximalReduceCountHandler;
import serene.validation.handlers.structure.impl.GroupDoubleHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.schema.simplified.SGroup;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

public class AGroup extends MultipleChildrenAPattern implements ACompositor{	
	
	int satisfactionIndicator;
	int saturationIndicator;
	
	ActiveModelStackHandlerPool stackHandlerPool;
	
	SGroup sgroup;
	public AGroup(APattern[] children,
	            boolean allowsElements,
                boolean allowsAttributes,
                boolean allowsDatas,
                boolean allowsValues,	
                boolean allowsListPatterns,
                boolean allowsText,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				SGroup sgroup){	
		super(children, 
		        allowsElements,
                allowsAttributes,
                allowsDatas,
                allowsValues,	
                allowsListPatterns,
                allowsText,
                ruleHandlerPool);
		this.stackHandlerPool = stackHandlerPool;
		this.sgroup = sgroup;
	}
		
	/*public boolean isElementContent(){
        return sgroup.isElementContent();
    }
	public boolean isAttributeContent(){
	    return sgroup.isAttributeContent();
	}
	public boolean isDataContent(){
	    return sgroup.isDataContent();
	}
	public boolean isValueContent(){
	    return sgroup.isValueContent();
	}
	public boolean isListPatternContent(){
	    return sgroup.isListPatternContent();
	}
	public boolean isTextContent(){
	    return sgroup.isTextContent();
	}
	public boolean isCharsContent(){
	    return sgroup.isCharsContent();
	}	
	public boolean isStructuredDataContent(){
	    return sgroup.isStructuredDataContent();
	}	
	public boolean isUnstructuredDataContent(){
	    return sgroup.isUnstructuredDataContent();
	}*/
	
	
	protected void asParent(APattern[] children){
		this.children = children;
		satisfactionIndicator = 0;
		saturationIndicator = 0;
		
		if(children != null){		
			for(int i = 0; i< children.length; i++){
				((AbstractAPattern)children[i]).setParent(this);
				((AbstractAPattern)children[i]).setChildIndex(i);
				if(children[i].getMinOccurs() > 0) satisfactionIndicator++;
				if(children[i].getMaxOccurs() != UNBOUNDED)saturationIndicator++;
			}
		}
	}	

	public int getMinOccurs(){
	    return sgroup.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return sgroup.getMaxOccurs();
	}
	
	public int getSatisfactionIndicator(){
		return satisfactionIndicator;
	}
	
	public int getSaturationIndicator(){
		return saturationIndicator;
	}
	
	
	public String getQName(){
		return sgroup.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sgroup.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return sgroup.hashCode();
    }   
    
    public SGroup getCorrespondingSimplifiedComponent(){
        return sgroup;
    }
    
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}	
	
	boolean hasMultipleCardinality(){
		if(getMaxOccurs() > 1 || getMaxOccurs() == UNBOUNDED)return true;
		if(parent instanceof AbstractAPattern) return ((AbstractAPattern)parent).transmitsMultipleCardinality();
		return false;
	}
	boolean requiresDoubleHandler(){		
		return hasMultipleCardinality() && parent instanceof AbstractAPattern && ((AbstractAPattern)parent).isInterleaved();
	}
	public StructureHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		if(requiresDoubleHandler()){
			return getDoubleHandler(errorCatcher, parent, stackHandler);
		}		
		return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, parent, stackHandler);
	}
	
	public GroupMinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMinimalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	
	public GroupMaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMaximalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	
	GroupDoubleHandler getDoubleHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		GroupDoubleHandler sih = ruleHandlerPool.getStructureDoubleHandler(this, errorCatcher, parent, stackHandler, stackHandlerPool);		
		return sih;
	}
	
	
	public GroupMinimalReduceCountHandler getStructureHandler(IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler){
		return ruleHandlerPool.getMinimalReduceCountHandler(reduceCountList, startedCountList, this, errorCatcher, stackHandler);
	}
	public GroupMaximalReduceCountHandler getStructureHandler(IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, MaximalReduceStackHandler stackHandler){
		return ruleHandlerPool.getMaximalReduceCountHandler(reduceCountList, startedCountList, this, errorCatcher, stackHandler);
	}
		
	public String toString(){
		//String s = "AGroup "+hashCode()+ " min "+getMinOccurs()+" max "+getMaxOccurs();		
		String s = "AGroup  min "+getMinOccurs()+" max "+getMaxOccurs();
		return s;
	}
}