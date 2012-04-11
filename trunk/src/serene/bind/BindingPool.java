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

package serene.bind;

import java.util.Map;

import org.xml.sax.SAXNotRecognizedException;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.util.ObjectIntHashMap;

import sereneWrite.MessageWriter;

public abstract class BindingPool{
	ValidatorQueuePool[] queuePool;
	int queuePoolFree, queuePoolMaxSize;
	
	protected MessageWriter debugWriter;
	
	public BindingPool(Map<SElement, ? extends ElementTaskPool> startElementTaskPools,
						Map<SAttribute, ? extends AttributeTaskPool> attributeTaskPools,
						Map<SElement, ? extends ElementTaskPool> endElementTaskPools,									
						MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		// the rest will be done by the subclasses so they can use the concrete 
		// task pool classes, cheaper, no more casts  
		// this.startElementTaskPools = startElementTaskPools;
		// this.endElementTaskPools = endElementTaskPools;
		// this.attributeTaskPools = attributeTaskPools;
		
		queuePoolFree = 0;
		queuePoolMaxSize = 3;	
		queuePool = new ValidatorQueuePool[5];
					
	}	
	
	public abstract BindingModel getBindingModel();
		
	public abstract void recycle(BindingModel bm);
	
		
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
	
	public abstract void setProperty(String name, Object value) throws SAXNotRecognizedException;
	
	public abstract Object getProperty(String name)  throws SAXNotRecognizedException;

	public abstract void setFeature(String name, boolean value)  throws SAXNotRecognizedException;

	public abstract boolean getFeature(String name)  throws SAXNotRecognizedException;	
	
}