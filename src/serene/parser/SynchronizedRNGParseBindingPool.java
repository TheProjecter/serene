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

package serene.parser;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.xml.sax.SAXNotRecognizedException;

import serene.bind.BindingPool;
import serene.bind.BindingModel;
import serene.bind.ValidatorQueuePool;
import serene.bind.ElementBinder;
import serene.bind.AttributeBinder;
import serene.bind.CharacterContentBinder;
import serene.bind.AttributeTaskPool;
import serene.bind.ElementTaskPool;
import serene.bind.ElementTask;
import serene.bind.Queue;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import sereneWrite.MessageWriter;

public class SynchronizedRNGParseBindingPool extends RNGParseBindingPool{
	public SynchronizedRNGParseBindingPool(ElementTaskPool startDocumentElementTaskPool,
	                                AttributeTaskPool[] documentElementTaskPool,
	                                ElementTaskPool endDocumentElementTaskPool,
	                                Map<SElement, ElementTaskPool> startElementTaskPools,
	                                ElementTaskPool genericStartElementTaskPool,
									Map<SAttribute, AttributeTaskPool> attributeTaskPools,
									AttributeTaskPool genericAttributeTaskPool,
									Map<SElement, ElementTaskPool> endElementTaskPools,
                                    ElementTaskPool genericEndElementTaskPool,									
									MessageWriter debugWriter){
		super(startDocumentElementTaskPool,
	                    documentElementTaskPool,
	                    endDocumentElementTaskPool,
	                    startElementTaskPools, 
	                    genericStartElementTaskPool,
	                    attributeTaskPools, 
	                    genericAttributeTaskPool,
	                    endElementTaskPools,
	                    genericEndElementTaskPool,
	                    debugWriter);
			
	} 
	
	synchronized public ValidatorQueuePool getValidatorQueuePool(){
		if(queuePoolFree == 0){			
			return new ValidatorQueuePool(this, debugWriter);			
		}
		else{				
			return queuePool[--queuePoolFree];
		}
	}
	synchronized public void recycle(ValidatorQueuePool qp){
	    if(queuePoolFree == queuePoolMaxSize) return;
		if(queuePoolFree == queuePool.length){
			ValidatorQueuePool[] increased = new ValidatorQueuePool[5+queuePool.length];
			System.arraycopy(queuePool, 0, increased, 0, queuePoolFree);
			queuePool = increased;
		}
		queuePool[queuePoolFree++] = qp;
	}
	public synchronized RNGParseBindingModel getBindingModel(){
		if(bindingModelFree == 0){			
			return createModel();			
		}
		else{				
			return bindingModel[--bindingModelFree];
		}
	}
	
	public synchronized void recycle(BindingModel bm){
		if(bm instanceof RNGParseBindingModel) recycle((RNGParseBindingModel)bm);
		else throw new IllegalStateException();
	}
	
	public synchronized void recycle(RNGParseBindingModel bm){
	    if(bindingModelFree == bindingModelMaxSize) return;	
		if(bindingModelFree == bindingModel.length){
			RNGParseBindingModel[] increased = new RNGParseBindingModel[5+bindingModel.length];
			System.arraycopy(bindingModel, 0, increased, 0, bindingModelFree);
			bindingModel = increased;
		}
		bindingModel[bindingModelFree++] = bm;
	} 
}
