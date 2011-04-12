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
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

class UnrecognizedElementHandler extends ErrorEEH{	
	UnrecognizedElementHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
	
	void init(ElementValidationHandler parent){
		this.parent = parent;
	}
	
	public void recycle(){
		pool.recycle(this);
	}
	
	void validateInContext(){
		// TODO 
		// unrecognized error
	}
	
	public boolean functionalEquivalent(ComparableEEH other){
		return other.functionalEquivalent(this);
	}
	public boolean functionalEquivalent(ElementValidationHandler other){
		return false;
	}	
	public boolean functionalEquivalent(UnrecognizedElementHandler other){
		return true;
	}	
	public boolean functionalEquivalent(UnexpectedElementHandler other){		
		return false;
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
		return "UnrecognizedElementHandler ";
	}
	
} 