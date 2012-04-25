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
import serene.validation.schema.simplified.components.SGrammar;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.GrammarHandler;
import serene.validation.handlers.structure.impl.GrammarMinimalReduceHandler;
import serene.validation.handlers.structure.impl.GrammarMaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

public class AGrammar extends UniqueChildAPattern implements AInnerPattern{	
	SGrammar sgrammar;
	public AGrammar(APattern child,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				SGrammar sgrammar){
		super(child, ruleHandlerPool);
		this.sgrammar = sgrammar;
	}
		
	public boolean isRequiredContent(){
		if(minOccurs == 0) return false;
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
	public GrammarHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, parent, stackHandler);
	}
	
	public GrammarMinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMinimalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	public GrammarMaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMaximalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	
	boolean isInterleaved(){
		if(parent instanceof AbstractAPattern)return ((AbstractAPattern)parent).isInterleaved();
		return false;
	}	
	
	boolean transmitsMultipleOccurrence(){
		if(maxOccurs > 1 || maxOccurs == UNBOUNDED)return true;
		if(parent instanceof AbstractAPattern)return ((AbstractAPattern)parent).isInterleaved();
		return false;
	}
	
	public String toString(){
		String s = "AGrammar"+ " min "+minOccurs+" max "+maxOccurs;		
		return s;
	}

}