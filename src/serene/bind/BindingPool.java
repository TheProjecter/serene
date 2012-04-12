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

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.util.ObjectIntHashMap;

import sereneWrite.MessageWriter;

public abstract class BindingPool{
    protected ElementTaskPool startDocumentElementTaskPool;
    protected AttributeTaskPool[] documentElementTaskPool;
    protected ElementTaskPool endDocumentElementTaskPool;
    
    protected Map<SElement, ElementTaskPool> startElementTaskPools;
    protected ElementTaskPool genericStartElementTaskPool;
	protected Map<SElement, ElementTaskPool> endElementTaskPools;
	protected ElementTaskPool genericEndElementTaskPool;
	protected Map<SAttribute, AttributeTaskPool> attributeTaskPools;
	protected AttributeTaskPool genericAttributeTaskPool;
    
	protected ValidatorQueuePool[] queuePool;
	protected int queuePoolFree, queuePoolMaxSize;
	
	protected MessageWriter debugWriter;
	
	public BindingPool(ElementTaskPool startDocumentElementTaskPool,
	                                AttributeTaskPool[] documentElementTaskPool,
	                                ElementTaskPool endDocumentElementTaskPool,
	                                Map<SElement, ElementTaskPool> startElementTaskPools,
	                                ElementTaskPool genericStartElementTaskPool,
									Map<SAttribute, AttributeTaskPool> attributeTaskPools,
									AttributeTaskPool genericAttributeTaskPool,
									Map<SElement, ElementTaskPool> endElementTaskPools,
                                    ElementTaskPool genericEndElementTaskPool,									
									MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		this.startDocumentElementTaskPool = startDocumentElementTaskPool;
	    this.documentElementTaskPool = documentElementTaskPool;
	    this.endDocumentElementTaskPool = endDocumentElementTaskPool;
	    
		this.startElementTaskPools = startElementTaskPools;
		this.genericStartElementTaskPool = genericStartElementTaskPool;
		this.endElementTaskPools = endElementTaskPools;
		this.genericEndElementTaskPool = genericEndElementTaskPool;
		this.attributeTaskPools = attributeTaskPools;
		this.genericAttributeTaskPool = genericAttributeTaskPool;
		
		
		queuePoolFree = 0;
		queuePoolMaxSize = 3;	
		queuePool = new ValidatorQueuePool[5];					
	}	
	
	public abstract BindingModel getBindingModel();
		
	public abstract void recycle(BindingModel bm);
		
	public abstract ValidatorQueuePool getValidatorQueuePool();
	
	public abstract void recycle(ValidatorQueuePool qp);	
}