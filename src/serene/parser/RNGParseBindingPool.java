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

import serene.bind.BindingPool;
import serene.bind.BindingModel;
import serene.bind.ValidatorQueuePool;
import serene.bind.DocumentBinder;
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

public abstract class RNGParseBindingPool extends BindingPool{		
	RNGParseBindingModel[] bindingModel;
	int bindingModelFree, bindingModelMaxSize;	
	
	public RNGParseBindingPool(ElementTaskPool startDocumentElementTaskPool,
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
	
	public abstract RNGParseBindingModel getBindingModel();

	
	RNGParseBindingModel createModel(){
		ParsedComponentBuilder builder = new ParsedComponentBuilder(debugWriter);
		
		ElementBinder documentElementBinder = new ElementBinder(debugWriter);
		ElementTask startDocumentElementTask = startDocumentElementTaskPool.getTask();
		startDocumentElementTask.setExecutant(builder);
		documentElementBinder.setStartTask(startDocumentElementTask);
		
		DocumentBinder documentBinder = new DocumentBinder(debugWriter);
		documentBinder.setElementBinder(documentElementBinder);
		
		Set<SElement> startKeys = startElementTaskPools.keySet();
		Map<SElement, ElementBinder> selementBinders = new HashMap<SElement, ElementBinder>();
		for(SElement element : startKeys){
			ElementTaskPool taskPool = startElementTaskPools.get(element);
			ElementTask task = taskPool.getTask();
			task.setExecutant(builder);
			
			ElementBinder binder = new ElementBinder(debugWriter);
			binder.setStartTask(task);			
			selementBinders.put(element, binder);
		}		
		Set<SElement> endKeys = endElementTaskPools.keySet();
		for(SElement element : endKeys){
			ElementTaskPool taskPool = endElementTaskPools.get(element);
			ElementTask task = taskPool.getTask();
			task.setExecutant(builder);
			
			ElementBinder binder = selementBinders.get(element);
			if(binder == null){
				binder = new ElementBinder(debugWriter);				
			}
			binder.setEndTask(task);
			selementBinders.put(element, binder);			
		}
		
		Map<SAttribute, AttributeBinder> sattributeBinders = new HashMap<SAttribute, AttributeBinder>();
		Set<SAttribute> attributeKeys = attributeTaskPools.keySet();
		for(SAttribute attribute : attributeKeys){
			sattributeBinders.put(attribute, new AttributeBinder(debugWriter));			
		}
		
		ElementBinder genericElementBinder = new ElementBinder(debugWriter);
		ElementTask genericStartElementTask = genericStartElementTaskPool.getTask();
		genericStartElementTask.setExecutant(builder);
		genericElementBinder.setStartTask(genericStartElementTask);
		ElementTask genericEndElementTask = genericEndElementTaskPool.getTask();
		genericEndElementTask.setExecutant(builder);
		genericElementBinder.setEndTask(genericEndElementTask);
		
		// not here: builder = null; // so it cannot be reused for another calls	
		return new RNGParseBindingModel(documentBinder, selementBinders, genericElementBinder, sattributeBinders, null, this, builder, debugWriter);
	}
	public abstract void recycle(BindingModel bm);	
	public abstract void recycle(RNGParseBindingModel bm);
}