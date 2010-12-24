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

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;
import serene.validation.handlers.conflict.ActiveModelConflictHandlerPool;

import serene.validation.handlers.match.MatchHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.util.ObjectIntHashMap;

import serene.Reusable;

import sereneWrite.MessageWriter;

public class ActiveModel  implements Reusable{

	ActiveGrammarModel grammarModel;
		
	ActiveModelRuleHandlerPool ruleHandlerPool;
	ActiveModelStackHandlerPool stackHandlerPool;	
	ActiveModelConflictHandlerPool conflictHandlerPool;
	
	ActiveModelPool pool;	
	
	MessageWriter debugWriter;
	
	public ActiveModel(ActiveGrammarModel grammarModel,
					ActiveModelRuleHandlerPool ruleHandlerPool,
					ActiveModelStackHandlerPool stackHandlerPool,
					ActiveModelConflictHandlerPool conflictHandlerPool,
					ActiveModelPool pool,
					MessageWriter debugWriter){
		this.debugWriter = debugWriter;	
		this.grammarModel = grammarModel;
		
		this.ruleHandlerPool = ruleHandlerPool;
		this.stackHandlerPool = stackHandlerPool;
		this.conflictHandlerPool = conflictHandlerPool;
				
		this.pool = pool;		
	}
	
	protected void finalize(){
		ruleHandlerPool.releaseHandlers();
		stackHandlerPool.releaseHandlers();		
		conflictHandlerPool.releaseHandlers();
		
		ruleHandlerPool.recycle();
		stackHandlerPool.recycle();		
		conflictHandlerPool.recycle();
	}	
	
	public void recycle(){		
		ruleHandlerPool.releaseHandlers();
		stackHandlerPool.releaseHandlers();		
		conflictHandlerPool.releaseHandlers();
		pool.recycle(this);
	}
	
	public void init(ValidationItemLocator validationItemLocator, ErrorDispatcher errorDispatcher){
		ruleHandlerPool.fill(validationItemLocator);
		stackHandlerPool.fill(validationItemLocator, conflictHandlerPool);		
		conflictHandlerPool.fill(validationItemLocator);
	}
	
	public AElement getStartElement(){		
		return grammarModel.getStartElement();
	}
	
	public ObjectIntHashMap getSElementIndexMap(){
		return grammarModel.getSElementIndexMap();
	}
	
	public ObjectIntHashMap getSAttributeIndexMap(){
		return grammarModel.getSAttributeIndexMap();
	}
} 