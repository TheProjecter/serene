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

package serene.validation.jaxp;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import serene.BaseSchema;
import serene.SchemaModel;


import sereneWrite.MessageWriter;

//SPECIFICATION
//	thread safe(should be shared across parsers and threads)
//	immutable(validating the same document over the same schema gives the same result)
// 	created by SchemaFactory

//	creates
//			Validator - thread unsafe
//			ValidatorHandler - thread unsafe


public class RNGSchema extends BaseSchema{		
	   
	boolean namespacePrefixes;
    boolean level1AttributeDefaultValue;
    boolean level2AttributeDefaultValue;
    
	RNGSchema(boolean secureProcessing,
                    boolean namespacePrefixes,
                    boolean level1AttributeDefaultValue,
                    boolean level2AttributeDefaultValue,
                    SchemaModel schemaModel,
                    MessageWriter debugWriter){
		super(secureProcessing, schemaModel, debugWriter);
        this.namespacePrefixes = namespacePrefixes;
        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
        this.level2AttributeDefaultValue = level2AttributeDefaultValue;        
	}
	
	
	public Validator newValidator(){
		return new ValidatorImpl(secureProcessing,                            
                                    namespacePrefixes,
                                    level1AttributeDefaultValue,
                                    level2AttributeDefaultValue,
                                    newValidatorHandler(), 
                                    debugWriter);
	}
	
	public ValidatorHandler newValidatorHandler(){
		return new ValidatorHandlerImpl(secureProcessing,
                                        namespacePrefixes,
                                        level1AttributeDefaultValue,
                                        level2AttributeDefaultValue,
                                        contentHandlerPool.getValidatorEventHandlerPool(),
										errorHandlerPool.getValidatorErrorHandlerPool(),
										schemaModel,
										debugWriter);
	}
}