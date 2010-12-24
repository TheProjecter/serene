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
import serene.bind.Queue;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import sereneWrite.MessageWriter;

public class RNGParseBindingPool extends BindingPool{
	Map<SElement, RNGParseElementTaskPool> startElementTaskPools;
	Map<SElement, RNGParseElementTaskPool> endElementTaskPools;
	Map<SAttribute, ? extends AttributeTaskPool> attributeTaskPools;
		
	RNGParseBindingModel[] bindingModel;
	int bindingModelFree, bindingModelPoolSize;
	
	ParsedComponentBuilder builder;
	
	public RNGParseBindingPool(Map<SElement, RNGParseElementTaskPool> startElementTaskPools,
									Map<SAttribute, ? extends AttributeTaskPool> attributeTaskPools,
									Map<SElement, RNGParseElementTaskPool> endElementTaskPools,									
									MessageWriter debugWriter){
		super(startElementTaskPools, attributeTaskPools, endElementTaskPools,debugWriter);
		this.startElementTaskPools = startElementTaskPools;
		this.endElementTaskPools = endElementTaskPools;
		this.attributeTaskPools = attributeTaskPools;
			
	} 
	
	public synchronized RNGParseBindingModel getBindingModel(){
		if(bindingModelFree == 0){			
			return createModel();			
		}
		else{				
			return bindingModel[--bindingModelFree];
		}
	}
	
	public void setProperty(String name, Object value) throws SAXNotRecognizedException{
		if(name.equals("builder")){
			builder = (ParsedComponentBuilder)value;
			return;
		}
		throw new SAXNotRecognizedException();
	}
	
	public Object getProperty(String name)  throws SAXNotRecognizedException{
		if(name.equals("builder")){
			return builder;
		}
		throw new SAXNotRecognizedException();
	}

	public void setFeature(String name, boolean value)  throws SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }
		throw new SAXNotRecognizedException();
	}

	public boolean getFeature(String name)  throws SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }
		throw new SAXNotRecognizedException();
	}
	
	
	
	private RNGParseBindingModel createModel(){
		//ParsedComponentBuilder builder = new ParsedComponentBuilder(debugWriter);
		
		Set<SElement> startKeys = startElementTaskPools.keySet();
		Map<SElement, ElementBinder> selementBinders = new HashMap<SElement, ElementBinder>();
		for(SElement element : startKeys){
			RNGParseElementTaskPool taskPool = startElementTaskPools.get(element);
			RNGParseElementTask task = taskPool.getTask();
			task.setExecutant(builder);
			
			ElementBinder binder = new ElementBinder(debugWriter);
			binder.setStartTask(task);			
			selementBinders.put(element, binder);
		}		
		Set<SElement> endKeys = endElementTaskPools.keySet();
		for(SElement element : endKeys){
			RNGParseElementTaskPool taskPool = endElementTaskPools.get(element);
			RNGParseElementTask task = taskPool.getTask();
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
		builder = null; // so it cannot be reused for another calls
		return new RNGParseBindingModel(selementBinders, sattributeBinders, this, builder, debugWriter);
	}
	public synchronized void recycle(BindingModel bm){
		if(bm instanceof RNGParseBindingModel) recycle((RNGParseBindingModel)bm);
		else throw new IllegalStateException();
	}
	
	public synchronized void recycle(RNGParseBindingModel bm){
		if(bindingModelFree == bindingModelPoolSize){			 
			bindingModelPoolSize+=3;
			RNGParseBindingModel[] increased = new RNGParseBindingModel[bindingModelPoolSize];
			System.arraycopy(bindingModel, 0, increased, 0, bindingModelFree);
			bindingModel = increased;
		}
		bindingModel[bindingModelFree++] = bm;
	} 
}