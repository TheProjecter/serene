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

//import serene.validation.handlers.ErrorDispatcher;


import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.active.ActiveModel;
import serene.validation.schema.active.ActiveModelPool;

import serene.validation.handlers.content.impl.ContentHandlerPool;
import serene.validation.handlers.error.ErrorHandlerPool;


import serene.bind.BindingModel;
import serene.bind.Queue;
import serene.bind.ValidatorQueuePool;

import sereneWrite.MessageWriter;

//SPECIFICATION
//	thread safe(should be shared across parsers and threads)
//	immutable(validating the same document over the same schema gives the same result)
// 	created by SchemaFactory

//	creates
//			Validator - thread unsafe
//			ValidatorHandler - thread unsafe


class RNGSchema extends Schema{		
	final ParsedModel parsedModel;
	final SimplifiedModel simplifiedModel;	
		
	ActiveModelPool activeModelPool;
		
	ContentHandlerPool contentHandlerPool;	
	ErrorHandlerPool errorHandlerPool;
		
	MessageWriter debugWriter;
	
	RNGSchema(ParsedModel parsedModel,
					SimplifiedModel simplifiedModel,
					MessageWriter debugWriter){		
		this.debugWriter = debugWriter;
		this.parsedModel = parsedModel;		
		this.simplifiedModel = simplifiedModel;
		if(simplifiedModel == null)throw new NullPointerException();
		
		contentHandlerPool = ContentHandlerPool.getInstance(debugWriter);
		errorHandlerPool = ErrorHandlerPool.getInstance(debugWriter);
		
		activeModelPool = new ActiveModelPool(simplifiedModel, debugWriter);				
	}
	
	
	public Validator newValidator(){
        //TODO see about the JAXP 1.4 features clarification
		return new ValidatorImpl(this, debugWriter);
	}
	
	public ValidatorHandler newValidatorHandler(){		
		//TODO see about the JAXP 1.4 features clarification
		return new ValidatorHandlerImpl(contentHandlerPool.getValidatorEventHandlerPool(),
										errorHandlerPool.getValidatorErrorHandlerPool(),
										activeModelPool,
										debugWriter);
	}
		
	public ParsedModel getParsedModel(){
		return parsedModel;
	}
	
	public SimplifiedModel getSimplifiedModel(){
		return simplifiedModel;
	}	
}