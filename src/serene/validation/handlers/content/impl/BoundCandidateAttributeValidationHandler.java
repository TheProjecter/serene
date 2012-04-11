/*
Copyright 2011 Radu Cernuta 

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

import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.content.BoundAttributeHandler;

import serene.validation.handlers.error.ContextErrorHandler;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.bind.AttributeBinder;
import serene.bind.BindingModel;
import serene.bind.Queue;

import serene.Reusable;

import sereneWrite.MessageWriter;

class BoundCandidateAttributeValidationHandler extends CandidateAttributeValidationHandler implements BoundAttributeHandler{
	BindingModel bindingModel;
	Queue queue;
	int entry;
	BoundCandidateAttributeValidationHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
	
	void init(AAttribute attribute, ElementValidationHandler parent, ExternalConflictHandler conflictHandler, int candidateIndex, BindingModel bindingModel, Queue queue, int entry){
		super.init(attribute, parent, conflictHandler, candidateIndex, temporaryMessageStorage);
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
		pool.recycle(this);
	}
	
	public void handleAttribute(String value) throws SAXException{
		validateValue(value);
		attributeBinding(value);
		validateInContext();
	}	
	
	public void attributeBinding(String value){		
		int definitionIndex = attribute.getDefinitionIndex();
		AttributeBinder binder = bindingModel.getAttributeBinder(definitionIndex);		
		if(binder != null){
			binder.bindAttribute(queue, entry, definitionIndex, inputStackDescriptor.getNamespaceURI(), inputStackDescriptor.getLocalName(), inputStackDescriptor.getItemDescription(), Datatype.ID_TYPE_NULL, value);
		}
	}
	
	AttributeBinder getBinder(){
		int definitionIndex = attribute.getDefinitionIndex();
		return bindingModel.getAttributeBinder(definitionIndex);
	}
	
	public String toString(){
		return "BoundAttributeValidationHandler "+attribute;
	}
		
}
