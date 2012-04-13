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

import serene.bind.BindingPool;
import serene.bind.BindingModel;
/*import serene.bind.DocumentBinder;
import serene.bind.ElementBinder;
import serene.bind.AttributeBinder;
import serene.bind.CharacterContentBinder;*/
import serene.bind.AttributeTaskFactory;
import serene.bind.ElementTaskFactory;
import serene.bind.ElementTask;
import serene.bind.AttributeTask;
import serene.bind.util.Queue;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.validation.schema.parsed.ParsedComponentBuilder;


import sereneWrite.MessageWriter;

public abstract class RNGParseBindingPool implements BindingPool{		
    RNGParseDocumentTaskFactory startDocumentTaskFactory;
    //RNGParseDocumentTaskFactory endDocumentTaskFactory;    
    
	RNGParseElementTaskFactory startElementTaskFactory;
	Map<SElement, Object> needsStartTask;
	Map<SElement, RNGParseElementTaskFactory> endElementTaskFactory;
	Map<SAttribute, RNGParseAttributeTaskFactory> attributeTaskFactory;
		
	RNGParseElementTaskFactory genericStartElementTaskFactory;
	RNGParseElementTaskFactory genericEndElementTaskFactory;
	RNGParseAttributeTaskFactory genericAttributeTaskFactory;
	  
		
	MessageWriter debugWriter;
	
	public RNGParseBindingPool(RNGParseDocumentTaskFactory startDocumentTaskFactory,
                                    //RNGParseDocumentTaskFactory endDocumentTaskFactory,    
                                    
                                    RNGParseElementTaskFactory startElementTaskFactory,
                                    Map<SElement, Object> needsStartTask,
                                    Map<SElement, RNGParseElementTaskFactory> endElementTaskFactory,
                                    Map<SAttribute, RNGParseAttributeTaskFactory> attributeTaskFactory,
                                        
                                    RNGParseElementTaskFactory genericStartElementTaskFactory,
                                    RNGParseElementTaskFactory genericEndElementTaskFactory,
                                    RNGParseAttributeTaskFactory genericAttributeTaskFactory,
                                    
									MessageWriter debugWriter){
		
	    this.debugWriter = debugWriter;
		
		this.startDocumentTaskFactory = startDocumentTaskFactory;
        //this.endDocumentTaskFactory = endDocumentTaskFactory;    
        
        this.startElementTaskFactory = startElementTaskFactory;
        this.needsStartTask = needsStartTask;
        this.endElementTaskFactory = endElementTaskFactory;
        this.attributeTaskFactory = attributeTaskFactory;
            
        this.genericStartElementTaskFactory = genericStartElementTaskFactory;
        this.genericEndElementTaskFactory = genericEndElementTaskFactory;
        this.genericAttributeTaskFactory = genericAttributeTaskFactory;        	
	} 
	
	public abstract RNGParseBindingModel getBindingModel();

	
	RNGParseBindingModel createModel(){
		ParsedComponentBuilder builder = new ParsedComponentBuilder(debugWriter);
		
		RNGParseDocumentTask startDocumentTask = startDocumentTaskFactory.getTask();
		startDocumentTask.setExecutant(builder);
		
		
		/*Set<SElement> startKeys = startElementTaskFactory.keySet();
		
		for(SElement element : startKeys){
			RNGParseElementTaskFactory taskFactory = startElementTaskFactory.get(element);
			RNGParseElementTask task = taskFactory.getTask();
			task.setExecutant(builder);
			startElementTask.put(element, task);
		}*/

			
		RNGParseElementTask startTask = startElementTaskFactory.getTask();
		Map<SElement, RNGParseElementTask> startElementTask = new HashMap<SElement, RNGParseElementTask>();
		startTask.setExecutant(builder);
		
		Set<SElement> endKeys = endElementTaskFactory.keySet();
		Map<SElement, RNGParseElementTask> endElementTask = new HashMap<SElement, RNGParseElementTask>();
		for(SElement element : endKeys){
		    
		    if(needsStartTask.containsKey(element))startElementTask.put(element, startTask);
		    
			RNGParseElementTaskFactory taskFactory = endElementTaskFactory.get(element);
			RNGParseElementTask task = taskFactory.getTask();
			task.setExecutant(builder);
			endElementTask.put(element, task);
		}
		
		Set<SAttribute> attributeKeys = attributeTaskFactory.keySet();
		Map<SAttribute, RNGParseAttributeTask> attributeTask = new HashMap<SAttribute, RNGParseAttributeTask>();
		for(SAttribute attribute : attributeKeys){
			RNGParseAttributeTaskFactory taskFactory = attributeTaskFactory.get(attribute);
			RNGParseAttributeTask task = taskFactory.getTask();
			task.setExecutant(builder);
			attributeTask.put(attribute, task);
		}
		
		RNGParseElementTask genericStartElementTask = genericStartElementTaskFactory.getTask();
		genericStartElementTask.setExecutant(builder);
		RNGParseElementTask genericEndElementTask = genericEndElementTaskFactory.getTask();
		genericEndElementTask.setExecutant(builder);
		
		// TODO genericAttributeTask
					
		return new RNGParseBindingModel(startDocumentTask,
                        null,                        
                        startElementTask,
                        endElementTask,
                        attributeTask,                            
                        genericStartElementTask,
                        genericEndElementTask,
                        null,
                        builder,
                        this,
                        debugWriter);
	}
	public abstract void recycle(BindingModel bm);	
	public abstract void recycle(RNGParseBindingModel bm);
}