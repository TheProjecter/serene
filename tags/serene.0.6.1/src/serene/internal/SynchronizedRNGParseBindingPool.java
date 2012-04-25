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

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

class SynchronizedRNGParseBindingPool extends RNGParseBindingPool{
    RNGParseBindingModel[] bindingModel;
	int bindingModelFree, bindingModelMaxSize;	    
	SynchronizedRNGParseBindingPool(RNGParseDocumentTaskFactory startDocumentTaskFactory,
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
