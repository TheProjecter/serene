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

package serene.validation.handlers.content;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import serene.validation.handlers.content.util.CharacterContentDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptorPool;

public interface ElementEventHandler extends MarkupEventHandler{
	ElementEventHandler getParentHandler();	
	ElementEventHandler handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException;
	void handleAttributes(Attributes attributes, Locator locator) throws SAXException;
	/**
	* Performes validation of the context and shift for correct and well 
	* determined occurrences of elements. 
	*/
	void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException;
	 
	void handleInnerCharacters(CharacterContentDescriptor characterContentDescriptor, CharacterContentDescriptorPool characterContentDescriptorPool) throws SAXException;
    void handleLastCharacters(CharacterContentDescriptor characterContentDescriptor) throws SAXException;
    
}