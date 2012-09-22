/*
Copyright 2011 Radu Cernuta 

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


package serene;

import javax.xml.validation.Schema;

import serene.validation.handlers.content.impl.ContentHandlerPool;
import serene.validation.handlers.content.impl.SynchronizedContentHandlerPool;
import serene.validation.handlers.content.impl.UnsynchronizedContentHandlerPool;

import serene.validation.handlers.stack.impl.StackHandlerPool;
import serene.validation.handlers.stack.impl.SynchronizedStackHandlerPool;
import serene.validation.handlers.stack.impl.UnsynchronizedStackHandlerPool;

import serene.validation.handlers.conflict.ConflictHandlerPool;
import serene.validation.handlers.conflict.SynchronizedConflictHandlerPool;
import serene.validation.handlers.conflict.UnsynchronizedConflictHandlerPool;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.structure.RuleHandlerPool;
import serene.validation.handlers.structure.SynchronizedRuleHandlerPool;
import serene.validation.handlers.structure.UnsynchronizedRuleHandlerPool;

import serene.validation.handlers.error.ErrorHandlerPool;
import serene.validation.handlers.error.SynchronizedErrorHandlerPool;
import serene.validation.handlers.error.UnsynchronizedErrorHandlerPool;
import serene.validation.handlers.error.ErrorDispatcher;

import serene.validation.handlers.stack.impl.StackHandlerPool;

public abstract class BaseSchema extends Schema{
    protected boolean secureProcessing;
    protected boolean optimizedForResourceSharing;
    
	protected SchemaModel schemaModel;
    		
	protected ContentHandlerPool contentHandlerPool;	
	protected ErrorHandlerPool errorHandlerPool;
	protected StackHandlerPool stackHandlerPool;
	protected ConflictHandlerPool conflictHandlerPool;
	protected RuleHandlerPool ruleHandlerPool;
	
    public BaseSchema(boolean secureProcessing,
                    boolean optimizedForResourceSharing,
                    SchemaModel schemaModel){
        this.schemaModel = schemaModel;
        this.secureProcessing = secureProcessing;       
        this.optimizedForResourceSharing = optimizedForResourceSharing;
        
        if(optimizedForResourceSharing){
            contentHandlerPool = SynchronizedContentHandlerPool.getInstance();
            errorHandlerPool = SynchronizedErrorHandlerPool.getInstance();
            stackHandlerPool = SynchronizedStackHandlerPool.getInstance();
            conflictHandlerPool = SynchronizedConflictHandlerPool.getInstance();
            ruleHandlerPool = SynchronizedRuleHandlerPool.getInstance();
        }else{
            contentHandlerPool = UnsynchronizedContentHandlerPool.getInstance();
            errorHandlerPool = UnsynchronizedErrorHandlerPool.getInstance();
            stackHandlerPool = UnsynchronizedStackHandlerPool.getInstance();
            conflictHandlerPool = UnsynchronizedConflictHandlerPool.getInstance();
            ruleHandlerPool = UnsynchronizedRuleHandlerPool.getInstance();
        }
    }    
}
