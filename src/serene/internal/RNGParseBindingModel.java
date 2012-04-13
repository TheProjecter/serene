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
import java.util.Set;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.bind.ElementTask;
import serene.bind.AttributeTask;
import serene.bind.DocumentTask;
import serene.bind.BindingModel;

import serene.util.ObjectIntHashMap;

import serene.Reusable;

import sereneWrite.MessageWriter;

class RNGParseBindingModel implements BindingModel, Reusable{
    ParsedComponentBuilder parsedComponentBuilder;
    
    RNGParseDocumentTask startDocumentTask;
    RNGParseDocumentTask endDocumentTask;    
    
	Map<SElement, RNGParseElementTask> startElementTask;
	Map<SElement, RNGParseElementTask> endElementTask;
	Map<SAttribute, RNGParseAttributeTask> attributeTask;
		
	RNGParseElementTask genericStartElementTask;
	RNGParseElementTask genericEndElementTask;
	RNGParseAttributeTask genericAttributeTask;
	
	RNGParseBindingPool pool;
	
	MessageWriter debugWriter;	
	
	// TODO
	// Question: should you use task factories for every definition, so that for 
	// each new occurrence a new task can be created if necessary? Ot maybe just
	// make verything abstract here and allow subclasses to decide...
	
	RNGParseBindingModel(RNGParseDocumentTask startDocumentTask,
                        RNGParseDocumentTask endDocumentTask,                        
                        Map<SElement, RNGParseElementTask> startElementTask,
                        Map<SElement, RNGParseElementTask> endElementTask,
                        Map<SAttribute, RNGParseAttributeTask> attributeTask,                            
                        RNGParseElementTask genericStartElementTask,
                        RNGParseElementTask genericEndElementTask,
                        RNGParseAttributeTask genericAttributeTask,
                        ParsedComponentBuilder parsedComponentBuilder,
                        RNGParseBindingPool pool,                        
                        MessageWriter debugWriter){
		this.startDocumentTask = startDocumentTask;
        this.endDocumentTask = endDocumentTask;    
        
        this.startElementTask = startElementTask;
        this.endElementTask = endElementTask;
        this.attributeTask = attributeTask;
            
        this.genericStartElementTask = genericStartElementTask;
        this.genericEndElementTask = genericEndElementTask;
        this.genericAttributeTask = genericAttributeTask;
        
        this.parsedComponentBuilder = parsedComponentBuilder;
        
        this.pool = pool;
        
        this.debugWriter = debugWriter;	
	}
	
	ParsedComponentBuilder getParsedComponentBuilder(){
	    return parsedComponentBuilder;
	}
	
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null) throw new NullPointerException();
	    throw new SAXNotRecognizedException();
	}
	
	public Object getProperty(String name)  throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null) throw new NullPointerException();
	    throw new SAXNotRecognizedException();
	}

	public void setFeature(String name, boolean value)  throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null) throw new NullPointerException();
	    throw new SAXNotRecognizedException();
	}

	public boolean getFeature(String name)  throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null) throw new NullPointerException();
	    throw new SAXNotRecognizedException();
	}
	
	/*public void index(ObjectIntHashMap selementIndexMap, ObjectIntHashMap sattributeIndexMap){
		int elementCount = selementIndexMap.size();		
		if(elementBinder == null){
			elementBinder = new ElementBinder[elementCount];
		}else if(elementBinder.length != elementCount){
			elementBinder = new ElementBinder[elementCount];
		}
		Set<SElement> boundElement = selementBinder.keySet();
		for(SElement elem : boundElement){
			elementBinder[selementIndexMap.get(elem)] = selementBinder.get(elem);
		}
		
		int attributeCount = sattributeIndexMap.size();
		if(attributeBinder == null){
			attributeBinder = new AttributeBinder[attributeCount];
		}else if(attributeBinder.length != attributeCount){
			attributeBinder = new AttributeBinder[attributeCount];
		}
		Set<SAttribute> boundAttribute = sattributeBinder.keySet();
		for(SAttribute attr : boundAttribute){			
			AttributeBinder ab = sattributeBinder.get(attr);
			if(ab == null){
				ab = new AttributeBinder(debugWriter);
				sattributeBinder.put(attr, ab);
			}
			attributeBinder[sattributeIndexMap.get(attr)] = ab;
		}		
	}*/
	
	public RNGParseDocumentTask getStartDocumentTask(){
	    return startDocumentTask;
	}
	public RNGParseDocumentTask getEndDocumentTask(){
	    return endDocumentTask;
	}
    	
	public RNGParseElementTask getStartElementTask(SElement element){		
		return startElementTask.get(element);
	}
	public RNGParseElementTask getEndElementTask(SElement element){		
		return endElementTask.get(element);
	}
	
	
	public RNGParseElementTask getGenericStartElementTask(){		
		return genericStartElementTask;
	}
	public RNGParseElementTask getGenericEndElementTask(){		
		return genericEndElementTask;
	}
	
	
	public RNGParseAttributeTask getAttributeTask(SAttribute attribute){		
		return attributeTask.get(attribute);
	}
	
	public RNGParseAttributeTask getGenericAttributeTask(){		
		return genericAttributeTask;
	}
	
	
	public void recycle(){
		pool.recycle(this);	
	}
}