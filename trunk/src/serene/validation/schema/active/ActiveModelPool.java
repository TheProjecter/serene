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

import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.simplified.SimplifiedModel;

import serene.validation.handlers.conflict.ConflictHandlerPool;
import serene.validation.handlers.stack.impl.StackHandlerPool;
import serene.validation.handlers.structure.impl.RuleHandlerPool;

import serene.validation.handlers.conflict.ActiveModelConflictHandlerPool;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;


import serene.validation.handlers.error.ErrorDispatcher;
import serene.validation.handlers.content.util.InputStackDescriptor;

import sereneWrite.MessageWriter;

public class ActiveModelPool{

	ConflictHandlerPool conflictHandlerPool;
	StackHandlerPool stackHandlerPool;
	RuleHandlerPool ruleHandlerPool;
	
	SimplifiedModel simplifiedModel;
	
	int modelFree; 
	int modelMaxSize;
	ActiveModel[] models;	
	
	ActiveModelFactory modelFactory;	
		
	MessageWriter debugWriter;
	
	public ActiveModelPool(SimplifiedModel simplifiedModel,
							MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.simplifiedModel = simplifiedModel;
		modelFactory = new ActiveModelFactory(debugWriter);
		
		modelFree = 0; 
		modelMaxSize = 10;
		models = new ActiveModel[5];

		conflictHandlerPool = ConflictHandlerPool.getInstance(debugWriter);
		stackHandlerPool = StackHandlerPool.getInstance(debugWriter);
		ruleHandlerPool = RuleHandlerPool.getInstance(debugWriter);		
	}
	
	public ActiveModel getActiveModel(InputStackDescriptor inputStackDescriptor, ErrorDispatcher errorDispatcher){
        
        if(simplifiedModel == null) return null;
        
		if(modelFree == 0){			
			ActiveModelConflictHandlerPool conflict = conflictHandlerPool.getActiveModelConflictHandlerPool();
			ActiveModelStackHandlerPool stack = stackHandlerPool.getActiveModelStackHandlerPool();
			ActiveModelRuleHandlerPool rule = ruleHandlerPool.getActiveModelRuleHandlerPool();
			ActiveModel model = modelFactory.createActiveModel(simplifiedModel, 
															rule,
															stack,
															conflict,
															this);			
			model.init(inputStackDescriptor, errorDispatcher);
			return model;
		}else{
			ActiveModel model = models[--modelFree];
			model.init(inputStackDescriptor, errorDispatcher);
			return model;
		}
	}
	
	public synchronized void recycle(ActiveModel model){
	    if(modelFree == modelMaxSize) return;
		if(modelFree == models.length){
			ActiveModel[] increased = new ActiveModel[5+models.length];
			System.arraycopy(models, 0, increased, 0, modelFree);
			models = increased;
		}
		models[modelFree++] = model;
	}
	
}