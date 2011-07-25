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

import serene.validation.handlers.content.ElementEventHandler;

import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

class ElementDefaultHandler extends ComparableEEH{	
	int depth;
	ElementEventHandler parent;
	
	ElementDefaultHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
	
	void init(ElementEventHandler parent){
		this.parent = parent;
		depth = 0;		
	}
	public void recycle(){
		pool.recycle(this);
	}	
	
	public ElementEventHandler getParentHandler(){		
		if(depth == 0)return parent;
		depth--;
		return this;
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name){
		depth++;	
		return this;
	}	
	ComparableAEH getAttributeHandler(String qName, String namespace, String name){
        return pool.getAttributeDefaultHandler(this);
    }
    
	public void handleAttributes(Attributes attributes, Locator locator){}	
	public void handleEndElement(boolean restrictToFileName, Locator locator) throws SAXException{
	}
	void validateContext(){}
	void reportContextErrors(boolean restrictToFileName, Locator locator) throws SAXException{}
	void validateInContext(){}
	public void handleInnerCharacters(char[] chars){
	}
    public void handleLastCharacters(char[] chars){
	}
	
	boolean functionalEquivalent(ComparableEEH other){
		return other.functionalEquivalent(this);
	}
	boolean functionalEquivalent(ElementValidationHandler other){
		return false;
	}
	boolean functionalEquivalent(UnexpectedElementHandler other){
		return false;
	}
	boolean functionalEquivalent(UnexpectedAmbiguousElementHandler other){
		return false;
	}
	boolean functionalEquivalent(UnknownElementHandler other){
		return false;
	}
	boolean functionalEquivalent(ElementDefaultHandler other){
		return true;
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
		return "ElementDefaultHandler ";
	}
	
} 