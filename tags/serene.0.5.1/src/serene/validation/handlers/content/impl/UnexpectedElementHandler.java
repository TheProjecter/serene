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

import java.util.List;

import org.xml.sax.Locator;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

class UnexpectedElementHandler extends ErrorEEH{
	AElement element;
	UnexpectedElementHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
	
	void init(AElement element, ElementValidationHandler parent){
		this.element = element;
		this.parent = parent;
	}
	
	public void recycle(){
		pool.recycle(this);
	}
	
	void validateInContext(){		
		parent.unexpectedElement(validationItemLocator.getQName(), element, validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber());
	}
	
	public boolean functionalEquivalent(ComparableEEH other){
		return other.functionalEquivalent(this);
	}
	public boolean functionalEquivalent(ElementValidationHandler other){
		return false;
	}		
	public boolean functionalEquivalent(UnrecognizedElementHandler other){
		return false;
	}
	public boolean functionalEquivalent(UnexpectedElementHandler other){		
		return other.functionalEquivalent(element);
	}
	private boolean functionalEquivalent(AElement otherAElement){
		return element.getDefinitionIndex() == otherAElement.getDefinitionIndex(); 
	}
	public boolean functionalEquivalent(UnexpectedAmbiguousElementHandler other){
		return false;
	}
	public boolean functionalEquivalent(UnknownElementHandler other){
		return false;
	}
	public boolean functionalEquivalent(ElementDefaultHandler other){
		return false;
	}
	public boolean functionalEquivalent(ElementConcurrentHandler other){
		return false;
	}
	public boolean functionalEquivalent(ElementParallelHandler other){
		return false;
	}	
	public boolean functionalEquivalent(ElementCommonHandler other){
		return false;
	}
	
	public String toString(){
		return "UnexpectedElementHandler "+element.toString();
	}
	
} 