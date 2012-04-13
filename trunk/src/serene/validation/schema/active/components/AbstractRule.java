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

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.ActiveComponent;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.handlers.error.ErrorCatcher;

abstract class AbstractRule extends AbstractActiveComponent implements Rule{
	protected Rule parent;
	
	protected ActiveModelRuleHandlerPool ruleHandlerPool;
	
	AbstractRule(ActiveModelRuleHandlerPool ruleHandlerPool){
		super();
		this.ruleHandlerPool = ruleHandlerPool; 
	}

	void setParent(Rule parent){		 
		this.parent = parent;				
	}	
	public Rule getParent(){
		return parent;
	}		
	
	public MinimalReduceHandler getStructureHandler(IntList minimalReducecount, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler){
		throw new UnsupportedOperationException();
	}
	public MaximalReduceHandler getStructureHandler(IntList maximalReducecount, ErrorCatcher errorCatcher, MaximalReduceStackHandler stackHandler){
		throw new UnsupportedOperationException();
	}
	
	public MinimalReduceHandler getStructureHandler(IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler){
		throw new UnsupportedOperationException();
	}
	public MaximalReduceHandler getStructureHandler(IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, MaximalReduceStackHandler stackHandler){
		throw new UnsupportedOperationException();
	}
	
	public boolean isInContext(Rule another){
		if(another == parent)return true;
		return parent.isAncestorInContext(another);
	}	
	
	public boolean isAncestorInContext(Rule another){
		if(another == parent)return true;
		return parent.isAncestorInContext(another);
	}
}	

