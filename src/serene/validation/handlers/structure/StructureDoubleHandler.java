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
import serene.validation.schema.simplified.SMultipleChildrenPattern;

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

import serene.validation.handlers.match.MatchPath;

public abstract class StructureDoubleHandler extends StructureHandler{

	SMultipleChildrenPattern rule;
	
	/*StructureHandler parent;*/	
			
	IntList minimalReduceCount;
	IntList maximalReduceCount;	
		
	MinimalReduceStackHandler minimalReduceStackHandler;
	MaximalReduceStackHandler maximalReduceStackHandler;
	
	/*ActiveInputDescriptor activeInputDescriptor;
	InputStackDescriptor inputStackDescriptor;	
	
	ErrorCatcher errorCatcher; 
	StackHandler stackHandler;*/
	
	/*ValidatorRuleHandlerPool pool;*/
	
    /*int startInputRecordIndex;	
    boolean isStartSet;*/
	
    MatchPath currentPath;
    
	StructureDoubleHandler(){
	    super();
		minimalReduceCount = new IntList();
		maximalReduceCount = new IntList();
		
		/*startInputRecordIndex = -1;	
        isStartSet = false;*/
	}
	
	void init(ValidatorRuleHandlerPool pool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor){				
		this.pool = pool;
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
	/*public StructureHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		else return parent.getAncestorOrSelfHandler(rule);
	}*/
	public void deactivate(){
		if(!parent.handleDeactivation())throw new IllegalStateException();
	}
    public boolean handleDeactivation(){
		throw new IllegalStateException();	
	}
	
	public StructureHandler getChildHandler(SRule child, MatchPath path){		
		//if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		SRule[] children = rule.getChildren();
		boolean missing = true;
		for(int i = 0; i < children.length; i++){
		    if(children[i] == child)missing = false;
		}
		if(missing)  throw new IllegalArgumentException();
		
		
		return this;
	}
	public SRule getRule(){
		return rule;
	}
	//boolean handleChildShiftAndOrder(APattern pattern, int expectedOrderHandlingCount) subclass
	public boolean handleChildShift(SPattern pattern, int startInputRecordIndex){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}	
	public boolean handleChildShift(int count, SPattern pattern, int startInputRecordIndex){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}
	public boolean handleChildShift(int MIN, int MAX, SPattern pattern, int startInputRecordIndex){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}
	
	
	//reduce
	public boolean handleChildShift(SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}	
	public boolean handleChildShift(int count, SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		//handleChildShift(startSystemId, startLineNumber, startColumnNumber, startQName, pattern);		
		//handleStateSaturationReduce();
		throw new UnsupportedOperationException();
	}
	public boolean handleChildShift(int MIN, int MAX, SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
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
	
	public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();		
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition, InternalConflictResolver resolver){
		throw new UnsupportedOperationException();		
	}
	public boolean acceptsMDescendentReduce(SPattern p){
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
	public void closeContentStructure(SPattern childPattern){
		throw new UnsupportedOperationException();
	}
	
	public void setConflict(int index, SRule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		throw new UnsupportedOperationException();
	}
	
	public StructureHandler getCopy(IntList reduceCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public StructureHandler getCopy(IntList reduceCountList, IntList startedCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	//End StructureHandler------------------------------------------------------------
	
	
	public StructureHandler getChildHandler(SRule r){
	    throw new IllegalStateException();
	}
	void handleParticleShift(int inputRecordIndex, SPattern childPattern){
	    throw new IllegalStateException();
	}
	void handleParticleShift(SPattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
	    throw new IllegalStateException();
	}
	
	void setStart(){
        startInputRecordIndex = inputStackDescriptor.getCurrentItemInputRecordIndex();
        isStartSet = true;
        activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
	}	
	
	public int getStartInputRecordIndex(){
	    return startInputRecordIndex;
	}
	
	
	public int getContentIndex(){
	    throw new IllegalStateException();
	}		
	public void childOpen(){
	    throw new IllegalStateException();
	}
	public void requiredChildSatisfied(){
	    throw new IllegalStateException();
	}
	public void optionalChildSatisfied(){
	    throw new IllegalStateException();
	}
	public void childSaturated(){
	    throw new IllegalStateException();
	}	                                               
	public void childExcessive(){
	    throw new IllegalStateException();
	}		
}