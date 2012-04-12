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

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.util.InputStackDescriptor;

import sereneWrite.MessageWriter;

class UnexpectedElementHandler extends ErrorEEH{
	SimplifiedComponent element;
	UnexpectedElementHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
	
	void init(SimplifiedComponent element, ElementValidationHandler parent){
		this.element = element;
		this.parent = parent;
	}
	
	public void recycle(){
	    element = null;
	    
		pool.recycle(this);
	}
	
	void validateInContext(){		
		parent.unexpectedElement(element, inputStackDescriptor.getCurrentItemInputRecordIndex());
	}
	
	boolean functionalEquivalent(ComparableEEH other){
		return other.functionalEquivalent(this);
	}
	boolean functionalEquivalent(ElementValidationHandler other){
		return false;
	}		
	boolean functionalEquivalent(UnexpectedElementHandler other){		
		return other.functionalEquivalent(element);
	}
	private boolean functionalEquivalent(SimplifiedComponent otherSElement){
		return element == otherSElement; 
	}
	boolean functionalEquivalent(UnexpectedAmbiguousElementHandler other){
		return false;
	}
	boolean functionalEquivalent(UnknownElementHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementDefaultHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementConcurrentHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementParallelHandler other){
		return false;
	}	
	boolean functionalEquivalent(ElementCommonHandler other){
		return false;
	}
	
	public String toString(){
		return "UnexpectedElementHandler "+element.toString();
	}
	
} 