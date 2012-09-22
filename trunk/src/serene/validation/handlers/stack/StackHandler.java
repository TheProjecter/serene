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

package serene.validation.handlers.stack;

import java.util.List;
import java.util.Map;
import java.util.BitSet;

import org.xml.sax.SAXException;

import serene.bind.util.Queue;
import serene.bind.BindingModel;

import serene.validation.handlers.FunctionallyEquivalable;
/*
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

*/
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;


import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.InnerPatternHandler;

import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.CharsMatchPath;
import serene.validation.handlers.match.MatchPath;

public interface StackHandler extends FunctionallyEquivalable{	
	// TODO 
	// There is some stuff here that is only done by ContextStackHandler, not by concurrent
	// See that you identify it and put it in it's place. 
	// It might also be necessary to make sure you set the right type of handler
	// to the concrete structure handlers.
	void shift(ElementMatchPath element);	
	void shiftAllElements(List<ElementMatchPath> elementDefinitions, ConflictMessageReporter conflictMessageReporter);	
	void shiftAllElements(List<ElementMatchPath> elementDefinitions, ConflictMessageReporter conflictMessageReporter, BindingModel bindingModel, Queue targetQueue, int reservationStartEntry, int reservationEndEntry, Map<SElement, Queue> candidateQueues);	
	void shiftAllElements(List<ElementMatchPath> elementDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter);
	void shiftAllElements(List<ElementMatchPath> elementDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter, BindingModel bindingModel, Queue targetQueue, int reservationStartEntry, int reservationEndEntry, Map<SElement, Queue> candidateQueues);
	
	void shift(AttributeMatchPath attribute);	
	void shiftAllAttributes(List<AttributeMatchPath> attributeDefinitions, TemporaryMessageStorage[] temporaryMessageStorage);
	void shiftAllAttributes(List<AttributeMatchPath> attributeDefinitions, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue targetQueue, int targetEntry, BindingModel bindingModel);
	void shiftAllAttributes(List<AttributeMatchPath> attributeDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage);
	void shiftAllAttributes(List<AttributeMatchPath> attributeDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue targetQueue, int targetEntry, BindingModel bindingModel);
	
	void shift(CharsMatchPath chars);	
	void shiftAllCharsDefinitions(List<? extends CharsMatchPath> charsDefinitions, TemporaryMessageStorage[] temporaryMessageStorage);
	void shiftAllCharsDefinitions(List<? extends CharsMatchPath> charsDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage);
    
	void shiftAllTokenDefinitions(List<? extends CharsMatchPath> charsDefinitions, TemporaryMessageStorage[] temporaryMessageStorage);
	void shiftAllTokenDefinitions(List<? extends CharsMatchPath> charsDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage);
	
	void reduce(InnerPatternHandler handler);
	
	void reshift(InnerPatternHandler handler, SPattern child);
	
	void validatingReshift(InnerPatternHandler handler, SPattern child);
	
	void reset(StructureHandler handler);
	
	/**
	* It is used for certain compositors that use block handling for their children
	* and all the occurrences are reduced at the end. It shifts every occurrence
	* one by one, setting every time the StructureHandler corresponding to the
	* pattern.
	*/
	void blockReduce(StructureHandler handler, int count, SPattern pattern, int startInputRecordIndex);
	
	/**
	* It is used for certain compositors that use limit handling for their children
	* and all the occurrences are reduced at the end. It shifts every occurrence
	* one by one, setting every time the StructureHandler corresponding to the
	* pattern. It is not fully implemented and it stops shifting after MIN. This 
	* means that in case the pattern needs to be shifted on a MInterleaveHandler
	* this will throw an UnsupportedOperationException error because here it not
	* shifting all the occurrences might introduce errors.  
	*/
	void limitReduce(StructureHandler handler, int MIN, int MAX, SPattern pattern, int startInputRecordIndex);
	
	
	/**
	* Used by the EventHandler when there is no more input for this stack to
	* trigger the validation of the context. Unlike during the rest of the handling,
	* this method makes it possible to reduce StructureHandlers that are not active.
	* During the execution of this method missing content errors are determined.
	*/
	void endValidation() throws SAXException;
	/**
	* Used when a subtree of the StructureHandler tree handled by this StackHandler
	* must be reduced. The argument represents the top of the subtree and it must 
	* be an active StructureHandler. During the execution of the method it is
	* possible to reduce handlers that are not active. Missing content errors
	* in the concerned subtree are determined.
	*/
	void endSubtreeValidation(StructureHandler sh);
		
	StructureHandler getTopHandler();
	StructureHandler getCurrentHandler();	
	MatchPath getCurrentMatchPath();
	
	/**
	* To be called from deactivate when the current handler must not be ended 
	* before another one can start, that is in the context of an interleave,
	* called from StructureDoubleHandlers or from GroupHandler.
	*/
	void setAsCurrentHandler(StructureHandler sh);
	
	boolean handlesConflict();
	
}