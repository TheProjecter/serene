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


package serene.validation.jaxp;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

import javax.xml.validation.ValidatorHandler;

import serene.util.IntStack;

import sereneWrite.MessageWriter;

abstract class Handler{
    /**
	* Locator to be initiated with the systemId of the source and used for DOM
	* validation.
	*/
	LocatorImpl locator;
	/**
	* Attributes list as described in the org.xml.sax, updated for every element
	* and passed to the validator. Used to fire startElement events.
	*/
	AttributesImpl attributes;
	/**
	* Stack of all the namespace prefixes defined for the context. The prefixes 
	* are pushed on the stack in the begining of the start element processing and
	* are popped at the end of end element processing of the corresponding element. 
	* Used to fire endPrefixMapping events.
	*/
	Stack<String> prefixes;
	/**
	* Stack containing the count of namespace prefixes defined for the an element.
	* The count is pushed on the stack in the begining of start element processing
	* and is popped at the end of end element processing of the corresponding 
	* element. Used to know how many endPrefixMapping event must be fire, if any.
	*/
	IntStack prefixesCount;
    
    /** Chunk size (1024). */
    final int CHUNK_SIZE = (1 << 10);
    
    /** Chunk mask (CHUNK_SIZE - 1). */
    final int CHUNK_MASK = CHUNK_SIZE - 1;
	
	/** Array for holding character data. **/
    char[] chars = new char[CHUNK_SIZE];	
    
    
    ValidatorHandler validatorHandler;
    
    MessageWriter debugWriter;
    
    Handler(MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        chars = new char[CHUNK_SIZE];
    }
    
    // TODO 
    // see about setUpLocator setUpPrefixes setUpAttributes
    // should be brought here
}
