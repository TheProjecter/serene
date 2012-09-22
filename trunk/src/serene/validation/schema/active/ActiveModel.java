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

import java.util.List;

import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SExceptPattern;


import serene.validation.schema.active.components.AElement;

/*import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;*/
import serene.validation.handlers.structure.ValidatorRuleHandlerPool;
/*import serene.validation.handlers.conflict.ValidatorConflictHandlerPool;*/

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.util.ObjectIntHashMap;

import serene.Reusable;

public class ActiveModel  implements Reusable{

	ActiveGrammarModel grammarModel;
		
	ValidatorRuleHandlerPool ruleHandlerPool;
	/*ValidatorStackHandlerPool stackHandlerPool;	
	ValidatorConflictHandlerPool conflictHandlerPool;*/
	
	ActiveModelPool pool;	
	
	public ActiveModel(ActiveGrammarModel grammarModel,
					ValidatorRuleHandlerPool ruleHandlerPool,
					/*ValidatorStackHandlerPool stackHandlerPool,
					ValidatorConflictHandlerPool conflictHandlerPool,*/
					ActiveModelPool pool){
		this.grammarModel = grammarModel;
		
		this.ruleHandlerPool = ruleHandlerPool;
		/*this.stackHandlerPool = stackHandlerPool;
		this.conflictHandlerPool = conflictHandlerPool;*/
				
		this.pool = pool;		
	}
	
	protected void finalize(){
		ruleHandlerPool.recycle();
		/*stackHandlerPool.recycle();		
		conflictHandlerPool.recycle();*/
	}	
	
	public void recycle(){
		ruleHandlerPool.releaseHandlers();
		/*stackHandlerPool.releaseHandlers();		
		conflictHandlerPool.releaseHandlers();*/
		pool.recycle(this);
		
	}
	
	public void init(ValidatorStackHandlerPool stackHandlerPool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor, ErrorDispatcher errorDispatcher){
		ruleHandlerPool.fill(stackHandlerPool, activeInputDescriptor, inputStackDescriptor);
		/*stackHandlerPool.fill(inputStackDescriptor, conflictHandlerPool);		
		conflictHandlerPool.fill(activeInputDescriptor, inputStackDescriptor);*/
	}
	
	public AElement getStartElement(){		
		return grammarModel.getStartElement();
	}
	
    /*public ActiveDefinition getActiveDefinition(SElement selement){
        int index = grammarModel.getIndex(selement);
        return grammarModel.getElementDefinition(index);
    }
    
    public ActiveDefinition getActiveDefinition(SAttribute sattribute){
        int index = grammarModel.getIndex(sattribute);
        return grammarModel.getAttributeDefinition(index);
    }
    
    
    public ActiveDefinition getActiveDefinition(SExceptPattern sexceptPattern){
        int index = grammarModel.getIndex(sexceptPattern);
        return grammarModel.getExceptPatternDefinition(index);
    }*/
    
	/*public ObjectIntHashMap getSElementIndexMap(){
		return grammarModel.getSElementIndexMap();
	}
	
	public ObjectIntHashMap getSAttributeIndexMap(){
		return grammarModel.getSAttributeIndexMap();
	}*/
    
    /*public ValidatorStackHandlerPool getStackHandlerPool(){
        return stackHandlerPool;
    }*/
    
    public ValidatorRuleHandlerPool getRuleHandlerPool(){
        return ruleHandlerPool;
    }
    
    public ActiveGrammarModel getGrammarModel(){
        return grammarModel;
    }
    
    
    public void setSimplifiedElementDefinitions(String namespace, String name, List<SimplifiedComponent> unexpectedMatches){
         grammarModel.setSimplifiedElementDefinitions(namespace, name, unexpectedMatches);
    }
    
    public void setSimplifiedAttributeDefinitions(String namespace, String name, List<SimplifiedComponent> unexpectedMatches){
         grammarModel.setSimplifiedAttributeDefinitions(namespace, name, unexpectedMatches);
    }
} 