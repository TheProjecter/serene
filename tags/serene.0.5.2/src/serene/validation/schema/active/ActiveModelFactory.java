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

import serene.validation.schema.simplified.SimplifiedModel;

import serene.validation.handlers.content.impl.ContentHandlerPool;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;


import serene.validation.handlers.conflict.ActiveModelConflictHandlerPool;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import sereneWrite.MessageWriter;

public class ActiveModelFactory{	
	ActiveGrammarModelFactory grammarModelFactory;
	
	MessageWriter debugWriter;
	
	public ActiveModelFactory(MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
		grammarModelFactory = new ActiveGrammarModelFactory(debugWriter);
	}
	
	public ActiveModel createActiveModel(SimplifiedModel simplifiedModel,		
										ActiveModelRuleHandlerPool ruleHandlerPool,
										ActiveModelStackHandlerPool stackHandlerPool,
										ActiveModelConflictHandlerPool conflictHandlerPool,
										ActiveModelPool pool){
				
		ActiveGrammarModel activeGrammarModel = grammarModelFactory.createActiveGrammarModel(simplifiedModel,
																								ruleHandlerPool,
																								stackHandlerPool);
		return new ActiveModel(activeGrammarModel,
							ruleHandlerPool,
							stackHandlerPool,
							conflictHandlerPool,
							pool,
							debugWriter);																							
	}
}