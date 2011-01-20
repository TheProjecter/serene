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

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.ChoiceHandler;
import serene.validation.handlers.structure.impl.ChoiceMinimalReduceHandler;
import serene.validation.handlers.structure.impl.ChoiceMaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import sereneWrite.MessageWriter;

public class AChoicePattern extends MultipleChildrenAPattern  implements AInnerPattern{		
	public AChoicePattern(APattern[] children,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				String qName, String location, 
				MessageWriter debugWriter){		
		super(children, ruleHandlerPool, qName, location, debugWriter);
	}

	public boolean isRequired(){
		if(minOccurs == 0) return false;		
		for(int i = 0; i < children.length; i++){
			if(!children[i].isRequired())return false;
		}
		return true;
	}
	
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}
		
	public ChoiceHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, parent, stackHandler);
	}
		
	public ChoiceMinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMinimalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	public ChoiceMaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMaximalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	
	boolean transmitsMultipleOccurrence(){
		if(maxOccurs > 1 || maxOccurs == UNBOUNDED)return true;
		if(parent instanceof AbstractAPattern)return ((AbstractAPattern)parent).isInterleaved();
		return false;
	}
	
	public String toString(){
		String s = "AChoicePattern"+ " min "+minOccurs+" max "+maxOccurs;
		return s;
	}
}