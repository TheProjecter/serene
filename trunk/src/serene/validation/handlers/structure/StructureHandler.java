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

package serene.validation.handlers.structure;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.util.IntList;

import serene.validation.handlers.error.ErrorCatcher;

public interface StructureHandler extends RuleHandler{
	
	StructureHandler getParentHandler();
	StructureHandler getAncestorOrSelfHandler(Rule rule);
	/**
	* Called from the StackHandler when this is the current active StructureHandler
	* and the pathes formed by this handler with the top handler and by the next 
	* shift handler with the top handler are different. In most cases it is 
	* necessary to reduce the current handler. This doesn't happen in the context 
	* of an interleave when a group or interleave handler(StructureDoubleHandler)
	* must not end when another sibling starts. In those cases the interleave is 
    * simply set as the current handler in the StackHandler.
	*/    
	void deactivate();
    /**
    * Asks parent for deactivation permission, called from group to determine   
    * if it is in the context of interleave.
    */
    boolean mayDeactivate();
	StructureHandler getChildHandler(Rule child);	
	Rule getRule();	
	
	//need to be this specific so that they can shift on the stacks of the limit handling	
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality and order in the context of the pattern handled by this handler.
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount);
	
	
	// for reduce
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality in the context of the pattern handled by this handler, used for 
	* reduce. 
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	boolean handleChildShift(APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber);
		
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality in the context of the pattern handled by this handler. Order is
	* not handled since it was already when the original ActiveTypeItem was shifted.
	* It is used for certain compositors that use block handling for their children
	* and all the occurrences are reduced at the end. The location information 
	* only refers to the start of the block, so in case errors need to be reported
	* based on location data for later occurrences, they will not be very acurate.
	* The first parameter represents the count of all occurrences in the block, 
	* and I really don't remember the original intent of it.
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	boolean handleChildShift(int count, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber);
	
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality in the context of the pattern handled by this handler. Order is
	* not handled since it was already when the original ActiveTypeItem was shifted.
	* It is used for certain compositors that use limit handling for their children
	* and all the occurrences are reduced at the end. The location information 
	* only refers to the start of the pattern, so in case errors need to be reported
	* based on location data for later occurrences, they will not be very acurate.
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	boolean handleChildShift(int MIN, int MAX, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber);
	
	
	
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality and order in the context of the pattern handled by this handler.
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver);
	
	
	// for reduce
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality in the context of the pattern handled by this handler, used for 
	* reduce. 
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	boolean handleChildShift(APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber, StackConflictsHandler stackConflictsHandler);
	
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality in the context of the pattern handled by this handler. Order is
	* not handled since it was already when the original ActiveTypeItem was shifted.
	* It is used for certain compositors that use block handling for their children
	* and all the occurrences are reduced at the end. The location information 
	* only refers to the start of the block, so in case errors need to be reported
	* based on location data for later occurrences, they will not be very acurate.
	* The first parameter represents the count of all occurrences in the block, 
	* and I really don't remember the original intent of it.
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	boolean handleChildShift(int count, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber, StackConflictsHandler stackConflictsHandler);
	
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality in the context of the pattern handled by this handler. Order is
	* not handled since it was already when the original ActiveTypeItem was shifted.
	* It is used for certain compositors that use limit handling for their children
	* and all the occurrences are reduced at the end. The location information 
	* only refers to the start of the pattern, so in case errors need to be reported
	* based on location data for later occurrences, they will not be very acurate.
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	boolean handleChildShift(int MIN, int MAX, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber, StackConflictsHandler stackConflictsHandler);
	
	
	
	/**
	* Handles the last reduce checking for missing content and reporting errors.
	*/
	void handleValidatingReduce();
	
	
	// Moved here in order to be able to handle block reduce from the StackHandler.
	// Some more of this might be necessary, but this is an intermediary phase.
	void closeContentStructure(APattern childPattern);
	
	boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition);
	
	boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition, InternalConflictResolver resolver);
	
	
	/**
	* Determines whether it is acceptable to reduce the handler corresponding to 
	* the child pattern. This method is only called after it has been determined 
	* that all internal conditions for reduction(saturated content) are met. It 
	* is sometimes called recursively on all types of pattern handlers, but the 
	* origin is always a group or interleave. Reduce is acceptable if
	* afterwards there is still a possibility to correctly accomodate eventual 
	* ulterior occurrences. This happens when the child occurrence that is about
	* to be shifted is not the last legal occurrence, or when it is the last, but
	* the parent(this) is (after the shift) satisfied and its reduction is allowed 
	* and acceptable, that is, the parent(this) can be reduced without producing 
	* errors.
	*/	
	boolean acceptsMDescendentReduce(APattern p);
	void childOpen();	
	
	//index is the index of this handler's rule in the path, starts with 0 and when equal to length returns 
	void setConflict(int index, Rule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver);
	
	StructureHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	StructureHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
	StructureHandler getCopy(IntList reduceCountList, StackHandler stackHandler, ErrorCatcher errorCatcher);
	StructureHandler getCopy(IntList reduceCountList, IntList startedCountList, StackHandler stackHandler, ErrorCatcher errorCatcher);
	
	String getStartQName();
	String getStartSystemId();
	int getStartLineNumber();
	int getStartColumnNumber();
	
	StructureHandler getOriginal();
	
	String stackToString();
} 