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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.xml.sax.SAXNotRecognizedException;

import serene.bind.BindingPool;
import serene.bind.BindingModel;
import serene.bind.AttributeTaskFactory;
import serene.bind.ElementTaskFactory;
import serene.bind.ElementTask;
import serene.bind.util.Queue;

import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

class UnsynchronizedRNGParseBindingPool extends RNGParseBindingPool{	
	
	UnsynchronizedRNGParseBindingPool(RNGParseDocumentTaskFactory startDocumentTaskFactory,
                                    //ElementTaskFactory endDocumentTaskFactory,    
                                    
                                    RNGParseElementTaskFactory startElementTaskFactory,
                                    Map<SElement, Object> needsStartTask,
                                    Map<SElement, RNGParseElementTaskFactory> endElementTaskFactory,
                                    Map<SAttribute, RNGParseAttributeTaskFactory> attributeTaskFactory,
                                        
                                    RNGParseElementTaskFactory genericStartElementTaskFactory,
                                    RNGParseElementTaskFactory genericEndElementTaskFactory,
                                    RNGParseAttributeTaskFactory genericAttributeTaskFactory){
		super(startDocumentTaskFactory,
                        //endDocumentTaskFactory,    
                        
                        startElementTaskFactory,
                        needsStartTask,
                        endElementTaskFactory,
                        attributeTaskFactory,
                            
                        genericStartElementTaskFactory,
                        genericEndElementTaskFactory,
                        genericAttributeTaskFactory);
	} 
	
	
	public RNGParseBindingModel getBindingModel(){			
	    return createModel();
	}
	
	public void recycle(BindingModel bm){		
		throw new IllegalStateException();
	}	
	
	public synchronized void recycle(RNGParseBindingModel bm){
	    throw new IllegalStateException();
	} 
}
