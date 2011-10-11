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
import serene.validation.schema.active.components.AInnerPattern;
import serene.validation.schema.active.components.APattern;

import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.StructureHandler;

import sereneWrite.MessageWriter;

abstract class UCMinimalReduceHandler extends UniqueChildPatternHandler implements MinimalReduceHandler{
	public UCMinimalReduceHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
		
	/*boolean isReduceRequired(){
		return contentHandler.isSaturated();
	}*/
	
	
	public MinimalReduceHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		int childIndex = child.getChildIndex();
		if(childStructureHandler == null){
			MinimalReduceHandler handler = child.getStructureHandler(errorCatcher, this, stackHandler);
			childStructureHandler = handler;
			return handler;
		}
		return (MinimalReduceHandler)childStructureHandler;
	}		
}