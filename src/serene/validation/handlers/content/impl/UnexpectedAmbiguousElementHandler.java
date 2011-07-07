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

import java.util.Arrays;
import java.util.List;

import org.xml.sax.Locator;

import serene.validation.handlers.content.ElementEventHandler;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

class UnexpectedAmbiguousElementHandler extends ErrorEEH{
	//String qName;
	SimplifiedComponent[] elements;
	UnexpectedAmbiguousElementHandler(MessageWriter debugWriter){
		super(debugWriter);	
	}		
	
	void init(List<SimplifiedComponent> elements, ElementValidationHandler parent){
		this.elements = elements.toArray(new SimplifiedComponent[elements.size()]);
		this.parent = parent;
	}
	
	public void recycle(){
		pool.recycle(this);
	}
	
	void validateInContext(){
		parent.unexpectedAmbiguousElement(validationItemLocator.getQName(), Arrays.copyOf(elements, elements.length), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber());
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
		return false;
	}
	public boolean functionalEquivalent(UnexpectedAmbiguousElementHandler other){
		return other.functionalEquivalent(elements);
	}
	private boolean functionalEquivalent(SimplifiedComponent[] otherSElements){
		int elementsCount = elements.length;
		if(elementsCount != otherSElements.length)return false;
		for(int i = 0; i < elementsCount; i++){
			if(elements[i] != otherSElements[i]) return false;
		}
		return true;
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
		return "UnexpectedAmbiguousElementHandler elements "+Arrays.toString(elements);
		
	}	
} 