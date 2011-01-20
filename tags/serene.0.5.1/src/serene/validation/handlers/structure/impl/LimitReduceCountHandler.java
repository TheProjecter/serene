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

import java.util.Arrays;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AInnerPattern;
import serene.validation.schema.active.components.MultipleChildrenAPattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;


import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.LimitHandler;
import serene.validation.handlers.structure.CardinalityHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.util.IntList;

import sereneWrite.MessageWriter;

// TODO
// Cosider getting rid of the AbstractSaturatedContent subclasses.
// Replace with a on the fly computing of saturation in the isSaturated()
// Seems simpler and more consistent.
abstract class LimitReduceCountHandler extends MultipleChildrenPatternHandler implements LimitHandler{
	APattern[][] childDefinition;
	String[][] childQName;
	String[][] childSystemId;
	int[][] childLineNumber;
	int[][] childColumnNumber;	
	
	int size;
	
	
	/**
	* Used to count how many reductions of this handler could be triggered
	* by each of the children occurrences provided all other conditions for 
	* reduction, determined by the other siblings, were met. 
	*/
	IntList reduceCountList;
	public LimitReduceCountHandler(MessageWriter debugWriter){
		super(debugWriter);
		noContent = new TopNoContent();
		contentHandler = noContent;
		// TODO	
		//create contentHandler subclasses
		//set the default contentHandler value
	}	
		
		
	public LimitHandler getChildHandler(Rule child){
		throw new IllegalStateException();
	}	
	public boolean acceptsMDescendentReduce(APattern p){		
		return true;
	}	
	
	public int functionalEquivalenceCode(){
		int fec = rule.hashCode();
		for(int i = 0; i < childParticleHandlers.length; i++){
			int childCode = 0;
			
			if(childParticleHandlers[i] != null){
				childCode = childParticleHandlers[i].getRule().hashCode();
				fec += childParticleHandlers[i].functionalEquivalenceCode(); 
			}
			
			if(childStructureHandlers[i] != null){
				if(childCode == 0){
					childCode = childStructureHandlers[i].getRule().hashCode();
				}
				fec += childStructureHandlers[i].functionalEquivalenceCode();
			}
			
			if(childCode == 0){
				throw new IllegalStateException("what is this?");
			}
			fec += reduceCountList.get(i) * childCode;
		}		
		return fec;
	}
	
	//Start ChildEventHandler---------------------------------------------------------
	public int getContentIndex(){
		int result = LIMIT_REDUCE;
		return result;
	}
	public void childOpen(){
		//contentHandler.childOpen();
	}	
	public void requiredChildSatisfied(){
		//++satisfactionLevel;		
		//contentHandler.requiredChildSatisfied();
	}
	public void optionalChildSatisfied(){
		//contentHandler.optionalChildSatisfied();
	}
	
	public void childSaturated(){
		//++saturationLevel;
		//contentHandler.childSaturated();
	}
	
	public void childExcessive(){
		//contentHandler.childExcessive();
	}		
	//End ChildEventHandler-----------------------------------------------------------
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	public StructureHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		return null;
	}	
	public void deactivate(){
		throw new IllegalStateException();
	}
	//StructureHandler getChildHandler(Rule child); subclasses	
	public APattern getRule(){
		return rule;
	}
	// boolean handleChildShift(AElement pattern)
	// boolean handleChildShift(AElement pattern, int expectedOrderHandlingCount)
	// boolean handleChildShift(AAttribute pattern)
	// /*boolean handleChildShift(AAttribute pattern, int expectedOrderHandlingCount)*/
	// boolean handleChildShift(CharsActiveTypeItem pattern)
	// boolean handleChildShift(CharsActiveTypeItem pattern, int expectedOrderHandlingCount)
	// boolean handleChildShift(APattern pattern)
	// boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount)
	// boolean handleChildShift(APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber)
	// boolean handleChildShift(int count, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber)
	// boolean handleChildShift(int MIN, int MAX, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber)
	public void handleValidatingReduce(){
		// This method might need to go to subclasses to support the handling 
		// of content that is too incomplete to even support one child reduce
		// (discrete cardinalities).
		for(int i = 0; i < size; i++){	
			if(childStructureHandlers[i] != null){
				childStructureHandlers[i].handleValidatingReduce();								
			}
			if(childParticleHandlers[i] != null){
				currentChildParticleHandler = childParticleHandlers[i];
				if(isCurrentChildReduceAllowed()){
					handleCurrentChildReduce();
				}else{
					throw new IllegalStateException();
					// This should never happen with the current cardinalities.
					// In other conditions(see above) remaining ParticleHandlers 
					// should be handled later.
				}
			}
			// errors will be reported by the StructureDoubleHandler
		}
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
		
	}	
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition, InternalConflictResolver resolver){
		throw new IllegalStateException();
	}	
	public abstract LimitReduceCountHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract LimitReduceCountHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
	//Start ValidationHandler---------------------------------------------------------
	void handleReduce(){
		throw new IllegalStateException();		
	}	
	/*void handleParticleShift(String systemId, int lineNumber, int columnNumber, String qName, APattern childPattern){
		setCurrentChildParticleHandler(childPattern);
		currentChildParticleHandler.handleOccurrence(qName, systemId, lineNumber, columnNumber);
	}*/	
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		throw new UnsupportedOperationException();
	}
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler){
		throw new UnsupportedOperationException();
	}
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
	
	
	
	//Start InnerPattern------------------------------------------------------------------	
	/**
	* Asseses the state of the handler and triggers reduce due to the saturation 
	* state of a handler(isReduceRequired() and isReduceAcceptable() are used).
	*/
	boolean handleStateSaturationReduce(){
		// For the currentChildParticleHandler get the state index, according to 
		// this the reduce count corresponding to the current child is incremented
		// if necessary. Implementations are different for minimal and maximal
		// handlers.
		if(isCurrentChildReduceRequired()){
			handleCurrentChildReduce();
			return true;
		}
		return false;
	}
		
	boolean isReduceAllowed(){
		//return contentHandler.isSatisfied();
		throw new IllegalStateException();
	}
	boolean isReduceRequired(){
		throw new IllegalStateException();
	}
	boolean isReduceAcceptable(){
		throw new IllegalStateException();
	}
		
	//void setCurrentChildParticleHandler(APattern childPattern);
		
	//void closeContent();	
	//void closeContentParticle(APattern childPattern);

	//for Reusable implementation
	//void setEmptyState();		
	//End InnerPattern------------------------------------------------------------------
		
	void handleReshift(APattern pattern){
		throw new IllegalStateException();
	}
	void handleCurrentChildReduce(){		
		APattern pattern = currentChildParticleHandler.getRule();
		int childIndex = pattern.getChildIndex();
		int count = reduceCountList.get(childIndex);
		reduceCountList.set(childIndex, 1+count);
		if(childStructureHandlers[childIndex] != null){
			childStructureHandlers[childIndex].recycle();
			childStructureHandlers[childIndex] = null;
		}
		currentChildParticleHandler.recycle();
		childParticleHandlers[childIndex] = null;
	}	
	boolean isCurrentChildReduceAllowed(){
		return currentChildParticleHandler.getIndex() == CardinalityHandler.SATISFIED_NEVER_SATURATED 
			|| currentChildParticleHandler.getIndex() == CardinalityHandler.SATISFIED_SIMPLE
			|| currentChildParticleHandler.getIndex() == CardinalityHandler.SATURATED;
	}
	abstract boolean isCurrentChildReduceRequired();
	boolean isCurrentChildReduceAcceptable(){
		throw new IllegalStateException();
	}
	// It can all be done from here, without calling the stack handler, because
	// it is the top of the hierarchy and no additional reduces could ever be 
	// triggered by this and no setCutrrentHandler is required.
	boolean handleCurrentChildOrderReduce(APattern child, APattern sourceDefinition){
		if(isCurrentChildReduceAllowed()){
			handleCurrentChildReduce();			
			handleChildShift(child, 0);//no more order handling, it should be correct	
			return true;
		}
		return false;
	}
	
	public String toString(){
		return "LimitReduceCountHandler contentHandler "+contentHandler.toString();
	}
	
	void recordOccurrence(String systemId, 
						int lineNumber, 
						int columnNumber, 
						String qName, 
						APattern pattern){
		/*
		This is actually a reduce for an occurrence, it must be done only after 
		the ParticleHandler event was received and interpreted. 
		int childIndex = pattern.getChildIndex();	
		int count = reduceCountList.get(childIndex);
		reduceCountList.set(childIndex, 1+count);*/
		// TODO 
		/*if(childSystemId[childIndex] == null || childSystemId[childIndex].length == 0){
			childSystemId[childIndex] = new String[1];
			childLineNumber[childIndex] = new int[1];
			childColumnNumber[childIndex] = new int[1];
			childQName[childIndex] = new String[1];
			childDefinition[childIndex] = new APattern[1];
			
			childSystemId[childIndex][1] = systemId;
			childLineNumber[childIndex][1] = lineNumber;
			childColumnNumber[childIndex][1] = columnNumber;
			childQName[childIndex][1] = qName;
			childDefinition[childIndex][1] = pattern;
		}else{
			int oldCount = childSystemId[childIndex].length;
			int newCount = oldCount+1;
			
			String[] increasedCSI = new String[newCount];
			System.arraycopy(childSystemId[childIndex], 0, increasedCSI, 0, oldCount);
			childSystemId[childIndex] = increasedCSI;
			
			int[] increasedLN = new int[newCount];
			System.arraycopy(childLineNumber[childIndex], 0, increasedLN, 0, oldCount);
			childLineNumber[childIndex] = increasedLN;
			
			int[] increasedCN = new int[newCount];
			System.arraycopy(childColumnNumber[childIndex], 0, increasedCN, 0, oldCount);
			childColumnNumber[childIndex] = increasedCN;
			
			String[] increasedQN = new String[newCount];
			System.arraycopy(childQName[childIndex], 0, increasedQN, 0, oldCount);
			childQName[childIndex] = increasedQN;
			
			APattern[] increasedD = new APattern[newCount];
			System.arraycopy(childDefinition[childIndex], 0, increasedD, 0, oldCount);
			childDefinition[childIndex] = increasedD;
			
			childSystemId[childIndex][oldCount] = systemId;
			childLineNumber[childIndex][oldCount] = lineNumber;
			childColumnNumber[childIndex][oldCount] = columnNumber;
			childQName[childIndex][oldCount] = qName;
			childDefinition[childIndex][oldCount] = pattern;
		}*/
	}
	
	protected class TopNoContent extends NoContent{
		public void childOpen(){
			setStart();
			if(satisfactionIndicator == 0){
				contentHandler = satisfiedContent;
			}else{
				contentHandler = openContent;
			}
		}	
		public void optionalChildSatisfied(){
			setStart();
			if(satisfactionIndicator == 0){
				contentHandler = satisfiedContent;
			}else{
				contentHandler = openContent;
			}
		}	
		public void requiredChildSatisfied(){
			setStart();			
			if(satisfactionLevel == satisfactionIndicator) {
				contentHandler = satisfiedContent;
			}else{
				contentHandler = openContent;
			}
		}
		public void childSaturated(){
			setStart();
			if(saturationLevel == saturationIndicator){
				contentHandler = unsatisfiedSaturatedContent;
			}
			else contentHandler = openContent;
		}		
	}
} 