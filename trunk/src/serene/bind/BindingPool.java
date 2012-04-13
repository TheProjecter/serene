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

import serene.validation.handlers.content.util.ActiveInputDescriptor;

/*import java.util.Map;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.util.ObjectIntHashMap;

import sereneWrite.MessageWriter;*/

public interface BindingPool{
    
	
	/*public BindingPool(ElementTaskFactory startDocumentTaskFactory,
                                    ElementTaskFactory endDocumentTaskFactory,    
                                    
                                    Map<SElement, ElementTaskFactory> startElementTaskFactory,
                                    Map<SElement, ElementTaskFactory> endElementTaskFactory,
                                    Map<SAttribute, AttributeTaskFactory> attributeTaskFactory,
                                        
                                    ElementTaskFactory genericStartElementTaskFactory,
                                    ElementTaskFactory genericEndElementTaskFactory,
                                    AttributeTaskFactory genericAttributeTaskFactory,
                                    
									MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		this.startDocumentTaskFactory = startDocumentTaskFactory;
        this.endDocumentTaskFactory = endDocumentTaskFactory;    
        
        this.startElementTaskFactory = startElementTaskFactory;
        this.endElementTaskFactory = endElementTaskFactory;
        this.attributeTaskFactory = attributeTaskFactory;
            
        this.genericStartElementTaskFactory = genericStartElementTaskFactory;
        this.genericEndElementTaskFactory = genericEndElementTaskFactory;
        this.genericAttributeTaskFactory = genericAttributeTaskFactory;
        
		
		queuePoolFree = 0;
		queuePoolMaxSize = 3;	
		queuePool = new ValidatorQueuePool[5];					
	}	*/
	
	BindingModel getBindingModel();
		
	void recycle(BindingModel bm);	
}