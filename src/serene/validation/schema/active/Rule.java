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

package serene.validation.schema.active;

import serene.util.IntList;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.handlers.error.ErrorCatcher;

public interface Rule extends ActiveComponent{
	Rule getParent();
	//Check if it's in the context of another component, stops at types.
	boolean isInContext(Rule another);
	//Used by previous in order to be able to stop at types.
	boolean isAncestorInContext(Rule another);
	
	void accept(RuleVisitor rv);
		
	StructureHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler);

	MinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler);
	MaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler);

	//CountHandlers	
	//Interleave
	MinimalReduceHandler getStructureHandler(IntList minimalReducecount, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler);
	MaximalReduceHandler getStructureHandler(IntList maximalReducecount, ErrorCatcher errorCatcher, MaximalReduceStackHandler stackHandler);
	//Group	
	MinimalReduceHandler getStructureHandler(IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler);
	MaximalReduceHandler getStructureHandler(IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, MaximalReduceStackHandler stackHandler);
	   
}