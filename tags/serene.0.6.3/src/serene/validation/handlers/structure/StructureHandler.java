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

import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;


import serene.validation.handlers.content.util.ActiveInputDescriptor;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.util.IntList;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchPath;

public abstract class StructureHandler extends RuleHandler  implements ChildEventHandler{
	
	StructureHandler parent;
	
	int contentIndex;
	
	ActiveInputDescriptor activeInputDescriptor;
	InputStackDescriptor inputStackDescriptor;	
	ErrorCatcher errorCatcher; 
	StackHandler stackHandler;
	
	ValidatorRuleHandlerPool pool;
		
    int startInputRecordIndex;
    boolean isStartSet;	
	
	StructureHandler(){		
		startInputRecordIndex = -1;
		isStartSet = false;	
	}	
		
	void init(ValidatorRuleHandlerPool pool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor){
		this.pool = pool;
		this.inputStackDescriptor = inputStackDescriptor;
		this.activeInputDescriptor = activeInputDescriptor;
	}	
	
	
	
	//Start RuleHandler---------------------------------------------------------------
	
	/*public boolean isSaturated(){
		switch(contentIndex){
            case NO_CONTENT :
                return false;
            case OPEN_CONTENT :
                return false;
            case SATISFIED_CONTENT :
                return false;
            case SATURATED_CONTENT :
                return true;
            case UNSATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();
            case EXCESSIVE_CONTENT :
                return true;
            case UNSATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            default :
                throw new IllegalStateException();
        }		
	}*/
	
	
	public void setStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
		throw new IllegalStateException();
	}
	//End RuleHandler-----------------------------------------------------------------
	
	public StructureHandler getParentHandler(){
		return parent;
	}
	/*public abstract StructureHandler getAncestorOrSelfHandler(Rule rule);*/
	/**
	* Called from the StackHandler when this is the current active StructureHandler
	* and the pathes formed by this handler with the top handler and by the next 
	* shift handler with the top handler are different. In most cases it is 
	* necessary to reduce the current handler. This doesn't happen in the context 
	* of an interleave when a group or interleave handler(StructureDoubleHandler)
	* must not end when another sibling starts. In those cases the interleave is 
    * simply set as the current handler in the StackHandler.
	*/    
	public abstract void deactivate();
    /**
    * Called from MultipleChildrenPatternHandler when the subtree cannot be 
    * reduced. It goes up in the hierarchy untill it meets the next 
    * MultipleChildrenPatternHandler which will then be set as current handler
    * in the StackHandler. Returns <code>true</code> when the currentHandler was 
    * set. If it returns <code>false</code> to a ChoiceHandler this will end the 
    * subtree validation, instances of MultipleChildrenPatternHandler
    * will throw IllegalStateException.
    */
    abstract boolean handleDeactivation();
	public abstract StructureHandler getChildHandler(SRule child, MatchPath path);	
	public abstract SRule getRule();	
	
	//need to be this specific so that they can shift on the stacks of the limit handling	
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality and order in the context of the pattern handled by this handler.
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	public abstract boolean handleChildShiftAndOrder(SPattern pattern, int expectedOrderHandlingCount);
	
	
	// for reduce
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality in the context of the pattern handled by this handler, used for 
	* reduce. 
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	public abstract boolean handleChildShift(SPattern pattern, int startInputRecordIndex);
		
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
	public abstract boolean handleChildShift(int count, SPattern pattern, int startInputRecordIndex);	
	
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
	public abstract boolean handleChildShift(int MIN, int MAX, SPattern pattern, int startInputRecordIndex);
			
	
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality and order in the context of the pattern handled by this handler.
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	public abstract boolean handleChildShift(SPattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver);
	
	
	// for reduce
	/**
	* Handles an occurrence corresponding to the child pattern with respect to 
	* cardinality in the context of the pattern handled by this handler, used for 
	* reduce. 
	* Returns true if the shift did not determine the reduce of the parent and 
	* the handler created by the shift remains for further processing.
	*/
	public abstract boolean handleChildShift(SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler);
	
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
	public abstract boolean handleChildShift(int count, SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler);
	
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
	public abstract boolean handleChildShift(int MIN, int MAX, SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler);
	
	
	
	/**
	* Handles the last reduce checking for missing content and reporting errors.
	*/
	public abstract void handleValidatingReduce();
	
	
	// Moved here in order to be able to handle block reduce from the StackHandler.
	// Some more of this might be necessary, but this is an intermediary phase.
	public abstract void closeContentStructure(SPattern childPattern);
	
	abstract boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition);
	
	abstract boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition, InternalConflictResolver resolver);
	
	
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
	abstract boolean acceptsMDescendentReduce(SPattern p);
	/*public abstract void childOpen();	*/
	
	//index is the index of this handler's rule in the path, starts with 0 and when equal to length returns 
	public abstract void setConflict(int index, SRule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver);
	
	public abstract StructureHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract StructureHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
	public StructureHandler getCopy(IntList reduceCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public StructureHandler getCopy(IntList reduceCountList, IntList startedCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}	
	
	/*int getItemId();
	String getStartQName();
	String getStartSystemId();
	int getStartLineNumber();
	int getStartColumnNumber();*/
	public int getStartInputRecordIndex(){
	    return startInputRecordIndex;
	}
	public String stackToString(){
		String s = "";
		if(parent != null){
			s+=" // "+parent.stackToString();
		}
		return toString()+s;
	}
	
	public abstract StructureHandler getOriginal();
	
	
	
	//Start ValidationHandler---------------------------------------------------------
	
	
	abstract void handleParticleShift(int inputRecordIndex, SPattern childPattern);
	abstract void handleParticleShift(SPattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver);
				
	
	/**
	* Used to handle the order in groups and propagate the content order 
	* handling up to the highest group. It returns true if the errors must be 
	* reported and false if somewhere in the hierarchy there has been a reduce
	* reshift and no errors need to be reported since the entire thing is redone.
	* This reduce/reshift can only happen during order validation in group, all 
	* the other patterns are intermediary and simply forward the parent's return
	* value.
	*/
	// actually pattern is always a compositor, more precisely group or interleave
	// LATER really???	
	
	void setStart(){
	    //System.out.println("SET START "+this);
		if(isStartSet){
		    //throw new IllegalStateException();
		    activeInputDescriptor.unregisterClientForRecord(startInputRecordIndex, this);
		    startInputRecordIndex = inputStackDescriptor.getCurrentItemInputRecordIndex();
		    activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		}else{
            startInputRecordIndex = inputStackDescriptor.getCurrentItemInputRecordIndex();            
            activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
            isStartSet = true;            
        }
	}	
	//End ValidationHandler-----------------------------------------------------------
	
	//Start ChildEventHandler---------------------------------------------------------
	public int getContentIndex(){		
		return contentIndex;
	}
			
	public int functionalEquivalenceCode(){
		throw new IllegalArgumentException("hehe functional equivalence!");
		//return contentHandler.functionalEquivalenceCode();
	}
	//End ChildEventHandler-----------------------------------------------------------
		
} 