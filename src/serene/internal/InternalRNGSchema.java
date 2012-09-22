/*
Copyright 2012 Radu Cernuta 

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

package serene.internal;

import serene.bind.util.Queue;

import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import serene.BaseSchema;
import serene.SchemaModel;

//SPECIFICATION
//	thread safe(should be shared across parsers and threads)
//	immutable(validating the same document over the same schema gives the same result)
// 	created by SchemaFactory

//	creates
//			Validator - thread unsafe
//			ValidatorHandler - thread unsafe


class InternalRNGSchema extends BaseSchema{		
    boolean level1DocumentationElement;	
    boolean restrictToFileName;
    boolean processEmbededSchematron;
    RNGParseBindingPool bindingPool;
	public InternalRNGSchema(boolean secureProcessing,
                    boolean level1DocumentationElement,
                    boolean restrictToFileName,
                    boolean optimizedForResourceSharing,
                    boolean processEmbededSchematron,
                    SchemaModel schemaModel,
                    RNGParseBindingPool bindingPool){
		super(secureProcessing, optimizedForResourceSharing, schemaModel);
        this.level1DocumentationElement = level1DocumentationElement;
        this.restrictToFileName = restrictToFileName;
        this.bindingPool = bindingPool;
        this.processEmbededSchematron = processEmbededSchematron;
	}
    
    public Validator newValidator(){
		throw new UnsupportedOperationException();
	}
    
	
	public ValidatorHandler newValidatorHandler(){
		return new InternalValidatorHandler(contentHandlerPool.getValidatorEventHandlerPool(),
		                                conflictHandlerPool.getValidatorConflictHandlerPool(),
		                                stackHandlerPool.getValidatorStackHandlerPool(),
		                                ruleHandlerPool.getValidatorRuleHandlerPool(),
										errorHandlerPool.getValidatorErrorHandlerPool(),
										schemaModel,
										bindingPool,
                                        level1DocumentationElement,
                                        restrictToFileName,
                                        optimizedForResourceSharing,
                                        processEmbededSchematron);
	}    
}