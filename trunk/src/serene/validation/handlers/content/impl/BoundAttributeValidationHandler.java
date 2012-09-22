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

package serene.validation.handlers.content.impl;

import org.xml.sax.SAXException;

import org.relaxng.datatype.Datatype;

import serene.validation.handlers.content.BoundAttributeHandler;

import serene.validation.handlers.error.ContextErrorHandlerManager;

import serene.validation.handlers.match.AttributeMatchPath;

import serene.bind.BindingModel;
import serene.bind.util.Queue;
import serene.bind.AttributeTask;

import serene.Reusable;

class BoundAttributeValidationHandler extends AttributeValidationHandler implements BoundAttributeHandler{
	BindingModel bindingModel;
	Queue queue;
	int entry;
	BoundAttributeValidationHandler(){
		super();		
	}
	
	void init(AttributeMatchPath attributeMatchPath, ElementValidationHandler parent, ContextErrorHandlerManager contextErrorHandlerManager, BindingModel bindingModel, Queue queue, int entry){
		super.init(attributeMatchPath, parent, contextErrorHandlerManager);
		this.bindingModel = bindingModel;
		this.queue = queue;
		this.entry = entry;
	}
	
	void reset(){
	    super.reset();
	    bindingModel = null;
		queue = null;
		entry = -1;
	}
	
	public void recycle(){
		reset();
		pool.recycle(this);
	}
	
	public void handleAttribute(String value) throws SAXException{	    
		validateValue(value);
		attributeBinding();
		validateInContext();
	}	
	
	public void attributeBinding(){		
		AttributeTask task = bindingModel.getAttributeTask(attributeMatchPath.getAttribute());
		// TODO 
		// Implement checks for if record index is needed. For now, it's ok though,
		// I know it's needed.
		if(task != null){
			queue.addAttribute(entry, inputStackDescriptor.getCurrentItemInputRecordIndex(), task);
		}
	}
	
	
	public String toString(){
		return "BoundAttributeValidationHandler "+attribute;
	}
		
}