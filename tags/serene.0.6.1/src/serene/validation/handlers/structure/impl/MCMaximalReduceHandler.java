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

package serene.validation.handlers.structure.impl;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AInnerPattern;

import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.StructureHandler;

abstract class MCMaximalReduceHandler extends MultipleChildrenPatternHandler implements MaximalReduceHandler{

	public MCMaximalReduceHandler(){
		super();
	}
	
	/*boolean isReduceRequired(){
		return contentHandler.isSatisfied();
	}*/
		
	public MaximalReduceHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		int childIndex = child.getChildIndex();
		if(childStructureHandlers[childIndex] == null){
			MaximalReduceHandler handler = child.getStructureHandler(errorCatcher, this, stackHandler);
			childStructureHandlers[childIndex] = handler;
			return handler;
		}			
		return (MaximalReduceHandler)childStructureHandlers[childIndex];
	}	
}