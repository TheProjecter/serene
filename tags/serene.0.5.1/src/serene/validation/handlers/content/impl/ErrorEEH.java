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

import serene.util.CharsBuffer;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

abstract class ErrorEEH extends ComparableEEH{	
	ElementValidationHandler parent;	
	ErrorEEH(MessageWriter debugWriter){
		super(debugWriter);		
	}
	
	void init(ElementValidationHandler parent){
		this.parent = parent;
	}	
	
	public ElementValidationHandler getParentHandler(){
		return parent;
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name){
		ElementDefaultHandler next = pool.getElementDefaultHandler(this);		
		return next;
	}	
	public void handleAttributes(Attributes attributes, Locator locator){}	
	void handleAttribute(String qName, String namespace, String name, String value){}
	
	public void handleEndElement(Locator locator){			
		// validateContext(locator); unnecessary, does nothing
		validateInContext();
	}	
	void validateContext(){}
	void reportContextErrors(Locator locator) throws SAXException{}
	public void handleCharacters(char[] chars){
	}
	
	public String toString(){
		return "ErrorEEH ";
	}
	
} 