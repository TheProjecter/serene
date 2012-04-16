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

package serene.validation.handlers.structure.impl;


import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.MultipleChildrenAPattern;
import serene.validation.schema.active.components.AInnerPattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.handlers.structure.StructureHandler;

import serene.validation.handlers.content.util.ActiveInputDescriptor;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.util.IntList;

public abstract class StructureDoubleHandler implements StructureHandler{

	MultipleChildrenAPattern rule;
	
	StructureHandler parent;	
			
	IntList minimalReduceCount;
	IntList maximalReduceCount;	
		
	MinimalReduceStackHandler minimalReduceStackHandler;
	MaximalReduceStackHandler maximalReduceStackHandler;
	
	ActiveInputDescriptor activeInputDescriptor;
	InputStackDescriptor inputStackDescriptor;	
	
	ErrorCatcher errorCatcher; 
	StackHandler stackHandler;
	
	RuleHandlerRecycler recycler;
	
    int startInputRecordIndex;	
    boolean isStartSet;
	
	StructureDoubleHandler(){				
		minimalReduceCount = new IntList();
		maximalReduceCount = new IntList();
		
		startInputRecordIndex = -1;	
        isStartSet = false;
	}
	
	void init(RuleHandlerRecycler recycler, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor){				
		this.recycler = recycler;
		this.activeInputDescriptor = activeInputDescriptor;
		this.inputStackDescriptor = inputStackDescriptor; 
	}	
	
	
	//Start RuleHandler---------------------------------------------------------------
	public boolean isSatisfied(){
		throw new IllegalStateException();
	}	
	public boolean isSaturated(){
		throw new IllegalStateException();
	}
	public void setStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
		throw new UnsupportedOperationException();
	}
	//End RuleHandler-----------------------------------------------------------------
	
	
	//Start StructureHandler----------------------------------------------------------
	public StructureHandler getParentHandler(){
		return parent;
	}
	public StructureHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		else return parent.getAncestorOrSelfHandler(rule);
	}
	public void deactivate(){
		if(!parent.handleDeactivation())throw new IllegalStateException();
	}
    public boolean handleDeactivation(){
		throw new IllegalStateException();	
	}
	
	public StructureHandler getChildHandler(Rule child){		
		//if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		return this;
	}
	public Rule getRule(){
		return rule;
	}
	//boolean handleChildShiftAndOrder(APattern pattern, int expectedOrderHandlingCount) subclass
	public boolean handleChildShift(APattern pattern, int startInputRecordIndex){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}	
	public boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}
	
	
	//reduce
	public boolean handleChildShift(APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}	
	public boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}
	
	public void handleValidatingReduce(){
		minimalReduceStackHandler.endValidation();
		maximalReduceStackHandler.endValidation();
		int size = rule.getChildrenCount();	
		for(int i = 0; i < size; i++){
		}
		int MIN = minimalReduceCount.getGreatestValue();
		int MAX = maximalReduceCount.getLowestValue();
		if(MAX < MIN){
			//loop the maximalReduceCount and report errors for every max value 
			// that is smaller than the MIN
			for(int i = 0; i < maximalReduceCount.size(); i++){
				if(maximalReduceCount.get(i) < MIN){
					// found maximalReduceCount.get(i)
					// expected at least MIN
					// void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber);
					errorCatcher.missingCompositorContent(rule, startInputRecordIndex, rule.getChild(i), MIN, maximalReduceCount.get(i));
				}
			}
		}
		if(MAX < MIN) stackHandler.blockReduce(this, MIN, rule, startInputRecordIndex);
		else stackHandler.limitReduce(this, MIN, MAX, rule, startInputRecordIndex);
	}
	
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();		
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition, InternalConflictResolver resolver){
		throw new UnsupportedOperationException();		
	}
	public boolean acceptsMDescendentReduce(APattern p){
		throw new IllegalStateException();
	}	
	public void childOpen(){
		throw new IllegalStateException();
	}
	public int functionalEquivalenceCode(){
		return minimalReduceStackHandler.functionalEquivalenceCode() + maximalReduceStackHandler.functionalEquivalenceCode();
	}	
	public StructureDoubleHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		// TODO
		throw new UnsupportedOperationException("TODO");
	}
	public StructureDoubleHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		// TODO
		throw new UnsupportedOperationException("TODO");
	}
	
	public int getItemId(){
	    throw new IllegalStateException();
	}
	public String getStartQName(){
		throw new IllegalStateException();
	}
	public String getStartSystemId(){
		throw new IllegalStateException();
	}
	public int getStartLineNumber(){
		throw new IllegalStateException();
	}
	public int getStartColumnNumber(){
		throw new IllegalStateException();
	}		
	public String stackToString(){
		String s = "";
		if(parent != null){
			s+=" // "+parent.stackToString();
		}
		return toString()+s;
	}
	public void closeContentStructure(APattern childPattern){
		throw new UnsupportedOperationException();
	}
	
	public void setConflict(int index, Rule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		throw new UnsupportedOperationException();
	}
	
	public StructureHandler getCopy(IntList reduceCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public StructureHandler getCopy(IntList reduceCountList, IntList startedCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	//End StructureHandler------------------------------------------------------------
	
	void setStart(){
        startInputRecordIndex = inputStackDescriptor.getCurrentItemInputRecordIndex();
        isStartSet = true;
        activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
	}	
	
	public int getStartInputRecordIndex(){
	    return startInputRecordIndex;
	}
}