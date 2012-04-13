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

import serene.validation.handlers.content.ElementEventHandler;

abstract class ComparableEEH extends AbstractEEH{

	ComparableEEH(){
		super();
	}
	
	public abstract ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException;
	    
	/**
	* This method must answer the need of ElementParallelHandler to determine 
	* if the handlers used to process a certain child element occurrence are
	* functionally identical for all the individual handlers. 
	* <p> In that case eventual errors will be the same for all individual 
	* handlers and will not have a disqualifying effect on any candidate of the 
	* original ElementConcurrentHandler. 
	*/
	abstract boolean functionalEquivalent(ComparableEEH other);
	abstract boolean functionalEquivalent(ElementValidationHandler other);
	abstract boolean functionalEquivalent(UnexpectedElementHandler other);
	abstract boolean functionalEquivalent(UnexpectedAmbiguousElementHandler other);
	abstract boolean functionalEquivalent(UnknownElementHandler other);
	abstract boolean functionalEquivalent(ElementDefaultHandler other);
	abstract boolean functionalEquivalent(ElementConcurrentHandler other);
	abstract boolean functionalEquivalent(ElementParallelHandler other);
	abstract boolean functionalEquivalent(ElementCommonHandler other);
}