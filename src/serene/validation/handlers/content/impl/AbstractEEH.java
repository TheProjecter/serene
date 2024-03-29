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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import serene.validation.handlers.content.ElementEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

abstract class AbstractEEH implements ElementEventHandler{
    ActiveInputDescriptor activeInputDescriptor;
	InputStackDescriptor inputStackDescriptor;	
	ValidatorEventHandlerPool pool;
	
	AbstractEEH(){
	}
	
	void init(ValidatorEventHandlerPool pool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor){
		this.pool = pool;
		this.inputStackDescriptor = inputStackDescriptor;
		this.activeInputDescriptor = activeInputDescriptor;
	}	
    
	abstract ComparableAEH getAttributeHandler(String qName, String namespace, String name);
	abstract void validateContext() throws SAXException;
	abstract void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException;
	abstract void discardContextErrors();
	abstract void validateInContext();	
}