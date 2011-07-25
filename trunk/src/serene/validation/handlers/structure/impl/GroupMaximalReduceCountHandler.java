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

import serene.util.IntList;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.CardinalityHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;

import serene.validation.handlers.error.ErrorCatcher;

import serene.util.IntList;

import sereneWrite.MessageWriter;

public class GroupMaximalReduceCountHandler extends MaximalReduceCountHandler{
	final int GOOD_ORDER = -1;	
	final int CURRENT_MISPLACED = 0;
	final int PREVIOUS_MISPLACED = 1;
	
	int[] childIndex;
	APattern[] childDefinition;
	String[][] childQName;
	String[][] childSystemId;
	int[][] childLineNumber;
	int[][] childColumnNumber;
	APattern[][] childSourceDefinition;
	
	int lastCorrectChildIndexIndex;
	int correctChildIndexesSize;

	IntList startedCountList;
	
	GroupMaximalReduceCountHandler original;
	GroupMaximalReduceCountHandler(MessageWriter debugWriter){
		super(debugWriter);
	
		lastCorrectChildIndexIndex = 1;
		correctChildIndexesSize = 2;
		
		childIndex = new int[correctChildIndexesSize];
		childIndex[0] = -1;
		childIndex[1] = -1;
		
		childDefinition = new APattern[correctChildIndexesSize];
		childQName = new String[correctChildIndexesSize][];
		childSystemId = new String[correctChildIndexesSize][];
		childLineNumber = new int[correctChildIndexesSize][];
		childColumnNumber = new int[correctChildIndexesSize][];
		childSourceDefinition = new APattern[correctChildIndexesSize][];
	}

	void init(IntList reduceCountList, IntList startedCountList, AGroup group, ErrorCatcher errorCatcher, MaximalReduceStackHandler stackHandler){
		this.reduceCountList = reduceCountList;
		this.startedCountList = startedCountList;
		this.rule = group;	
		satisfactionIndicator = group.getSatisfactionIndicator();
		saturationIndicator = group.getSaturationIndicator();
		this.errorCatcher = errorCatcher;		
		this.stackHandler = stackHandler;
		size = group.getChildrenCount();
		if(size > childParticleHandlers.length){
			childParticleHandlers = new ParticleHandler[size];
			childStructureHandlers = new StructureHandler[size];
		}		
	}
	
	public void recycle(){
		original = null;
		setEmptyState();	
		recycler.recycle(this);
	}	
	
	
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	// StructureValidationHandler getAncestorOrSelfHandler(Rule rule) super
	// StructureHandler getChildHandler(Rule child) super
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	public void handleValidatingReduce(){
		// This method might need to go to subclasses to support the handling 
		// of content that is too incomplete to even support one child reduce
		// (discrete cardinalities).		
		int started = -1;
		int reduced = -1;
		for(int i = 0; i < size; i++){	
			if(childStructureHandlers[i] != null){
				childStructureHandlers[i].handleValidatingReduce();									
			}
			started = startedCountList.get(i);
			reduced = reduceCountList.get(i);
			if(started > reduced){
				currentChildParticleHandler = childParticleHandlers[i];
				if(isCurrentChildReduceAllowed())handleCurrentChildReduce();
			}
			
			if(started > reduceCountList.get(i)){
				throw new IllegalStateException(); 
				// This should never happen with the current cardinalities.
				// In other conditions(see above) remaining ParticleHandlers 
				// should be handled later. 
			}
			if(childParticleHandlers[i] != null){
				childParticleHandlers[i].recycle();
				childParticleHandlers[i] = null;
			}
			// errors will be reported by the StructureDoubleHandler
		}
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern child, APattern sourceDefinition){
		int newChildIndex = child.getChildIndex();
		if(newChildIndex < childIndex[lastCorrectChildIndexIndex]){
			// Set the currentChildParticleHandler so that the handleOrderReduce
			// knows what to reduce. The decision and reduction takes place only
			// on the current child.
			setAsCurrentChildParticleHandler(child);
			if(currentChildParticleHandler == null){
				return true;
			}
			if(handleCurrentChildOrderReduce(child, sourceDefinition)){
				return false;
			}
		}else if(newChildIndex == childIndex[lastCorrectChildIndexIndex]){
			addLastCorrectChildIndex(sourceDefinition);
		}else{			
			addLastCorrectChildIndex(newChildIndex, child, sourceDefinition);					
		}
		return true;		
	}
	
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern child, APattern sourceDefinition, InternalConflictResolver resolver){
		throw new IllegalStateException();
	}
	
	public int functionalEquivalenceCode(){
		int orderCode = 0;
		for(int i = 0; i < childIndex.length; i++){
			orderCode += childIndex[i];
		}	
		orderCode *= childIndex.length;
		orderCode *= 1000;
		return super.functionalEquivalenceCode() + orderCode;
	}	
	public GroupMaximalReduceCountHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
		/*
		GroupMaximalReduceCountHandler copy = ((AGroup)rule).getStructureHandler(null,
																				null,
																				errorCatcher,
																				(MaximalReduceStackHandler)stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandlers, 
						childStructureHandlers,
						satisfactionLevel,
						saturationLevel,
						contentHandler.getContentIndex(),
						starttSystemId,
						starttLineNumber,
						starttColumnNumber,
						starttQName,
						childIndex,
						childDefinition,
						childQName,
						childSystemId,
						childLineNumber,
						childColumnNumber,
						childSourceDefinition,
						lastCorrectChildIndexIndex,
						correctChildIndexesSize);
		return copy;
		*/
	}
	public GroupMaximalReduceCountHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
		/*GroupMaximalReduceCountHandler copy = ((AGroup)rule).getMaximalReduceCountHandler(errorCatcher, 
													(StructureValidationHandler)parent, 
													stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandlers, 
						childStructureHandlers,
						satisfactionLevel,
						saturationLevel,
						contentHandler.getContentIndex(),
						starttSystemId,
						starttLineNumber,
						starttColumnNumber,
						starttQName,
						childIndex,
						childDefinition,
						childQName,
						childSystemId,
						childLineNumber,
						childColumnNumber,
						childSourceDefinition,
						lastCorrectChildIndexIndex,
						correctChildIndexesSize);
		return copy;*/
	}	
	public GroupMaximalReduceCountHandler getCopy(IntList reduceCountList, IntList startedCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		GroupMaximalReduceCountHandler copy = ((AGroup)rule).getStructureHandler(reduceCountList,
																				startedCountList,
																				errorCatcher,
																				(MaximalReduceStackHandler)stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandlers, 
						childStructureHandlers,
						size,
						satisfactionLevel,
						saturationLevel,
						contentHandler.getContentIndex(),
						starttSystemId,
						starttLineNumber,
						starttColumnNumber,
						starttQName,
						childIndex,
						childDefinition,
						childQName,
						childSystemId,
						childLineNumber,
						childColumnNumber,
						childSourceDefinition,
						lastCorrectChildIndexIndex,
						correctChildIndexesSize);
		copy.setOriginal(this);
		return copy;
	}
	private void setOriginal(GroupMaximalReduceCountHandler original){
		this.original = original;
	}
	public GroupMaximalReduceCountHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
	
	//Start ValidationHandler---------------------------------------------------------	
	
	// boolean acceptsMDescendentReduce(APattern p) super		
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
	

	
	//Start InnerPattern------------------------------------------------------------------
	/**
	* It asseses the state of the handler and triggers reduce due to the saturation 
	* state of a handler(isReduceRequired() and isReduceAcceptable() are used).
	*/
	boolean handleStateSaturationReduce(){
		// For the currentChildParticleHandler get the state index, according to 
		// this the reduce count corresponding to the current child is incremented
		// if necessary. Implementations are different for minimal and maximal
		// handlers.
		if(isCurrentChildReduceRequired() && isCurrentChildReduceAcceptable()) {
			handleCurrentChildReduce();
			return true;
		}
		return false;
	}
	// boolean isReduceAllowed() super
	// boolean isReduceRequired() super
	// boolean isReduceAcceptable() super
	void setAsCurrentChildParticleHandler(APattern childPattern){		
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();		
		int childIndex = childPattern.getChildIndex();
		currentChildParticleHandler = childParticleHandlers[childIndex];
	}
	void setCurrentChildParticleHandler(APattern childPattern){		
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();		
		int childIndex = childPattern.getChildIndex();
		int started = startedCountList.get(childIndex);
		if(childParticleHandlers[childIndex] == null){
			childParticleHandlers[childIndex] = childPattern.getParticleHandler(this, errorCatcher);
			startedCountList.set(childIndex, ++started);//relies on the fact that 
			// the lists are initially filled with 0
		}else{			
			int reduced = reduceCountList.get(childIndex);
			if(started == reduced){//isn't this always true if it gets here?
				if(isChildStartAcceptable(childIndex, started)){
					childParticleHandlers[childIndex].reset();
					startedCountList.set(childIndex, ++started);//relies on the fact that 
					// the lists are initially filled with 0
				}
			}//else the new input is packed over the old occurrence using existing childParticleHandler
		}
		currentChildParticleHandler = childParticleHandlers[childIndex];
	}	
	// void closeContent() super
	// void closeContentParticle(APattern childPattern) super
	//for Reusable implementation
	void setEmptyState(){
		super.setEmptyState();
		lastCorrectChildIndexIndex = 1;
		childIndex[0] = -1;
		childIndex[1] = -1;
		for(int i = 0; i < correctChildIndexesSize; i++){
			childDefinition[i] = null;
			childQName[i] = null;
			childSystemId[i] = null;
			childLineNumber[i] = null;
			childColumnNumber[i] = null;
			childSourceDefinition[i] = null;
		}		
		
	}			
	//End InnerPattern------------------------------------------------------------------
	
	boolean handleOrderCheckedReduce(APattern sourceDefinition){		
		throw new IllegalStateException();
	}
    
    boolean handleOrderUncheckedReduce(APattern sourceDefinition){		
		throw new IllegalStateException();
	}
	
	boolean handleExcessiveChildReduce(){
		throw new IllegalStateException();
	}
	/**
	* Checks if the starting of a new occurrence for the child input doesn't
	* introduce an order error that would go undetected by the double handler
	* and could mask occurrence errors. Returns true if the present started
	* count of the child in question is lower that the started count of any 
	* previous sibling, and false otherwise.
	*/
	// Example
	// group+{ aa+ bb+ cc+}
	// input
	// <aa/>
	// <bb/>
	// <aa/>	
	// <cc/>
	// <cc/>
	// <bb/>
	// The two <cc> will be packed on one occurrence
	// See testSuite/implementation/reduce/limitReduce/orderReduce
	boolean isChildStartAcceptable(int childIndex, int started){
		// If childParticleHandlers[childIndex] == null an exception should be 
		// thrown. This is necessary since if it was the first ever occurrence 
		// of the child it should have been checked and never have gotten here,
		// otherwise there should always be a ParticleHandler present.	
		
		
		for(int i = 0; i < childIndex; i++){
			if(startedCountList.get(i) <= started)return false;			
		}
		return true;
	}
		
	void handleCurrentChildReduce(){	
		APattern pattern = currentChildParticleHandler.getRule();
		int childIndex = pattern.getChildIndex();	
		int started = startedCountList.get(childIndex);
		int reduced = reduceCountList.get(childIndex);
		if(started == reduced){			
			return; // the call was made for input "packed" over an already 
			//reduced occurrence, no need to reduce again
		}
		reduceCountList.set(childIndex, 1+reduced);
		if(childStructureHandlers[childIndex] != null){
			childStructureHandlers[childIndex].recycle();
			childStructureHandlers[childIndex] = null;
		}
		// If the particleHandler is not saturated it is not removed. It remains
		// for the case when a the input consists of a child of the same kind of 
		// child and the situation of the previous siblings doesn't allow the 
		// creation of a new occurrence. This will be packed on the same 
		// ParticleHandler.
		if(childParticleHandlers[childIndex].getIndex() == CardinalityHandler.SATURATED){
			currentChildParticleHandler.recycle();
			childParticleHandlers[childIndex] = null;
		}				
	}	
	
	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler[] cph, 
							StructureHandler[] csh,
							int size,
							int satisfactionLevel,
							int saturationLevel,
							int contentHandlerContentIndex,
							String startSystemId,
							int startLineNumber,
							int startColumnNumber,
							String startQName,
							int[] childIndex,
							APattern[] childDefinition,
							String[][] childQName,
							String[][] childSystemId,
							int[][] childLineNumber,
							int[][] childColumnNumber,
							APattern[][] childSourceDefinition,
							int lastCorrectChildIndexIndex,
							int correctChildIndexesSize){		
		if(this.correctChildIndexesSize < correctChildIndexesSize){
			this.childIndex = new int[correctChildIndexesSize];
			this.childDefinition = new APattern[correctChildIndexesSize];
			this.childQName = new String[correctChildIndexesSize][];
			this.childSystemId = new String[correctChildIndexesSize][];
			this.childLineNumber = new int[correctChildIndexesSize][];
			this.childColumnNumber = new int[correctChildIndexesSize][];
			this.childSourceDefinition = new APattern[correctChildIndexesSize][];
			
			this.correctChildIndexesSize = correctChildIndexesSize;
		}
		
		
		for(int i = 2; i <= lastCorrectChildIndexIndex; i++){
			this.childIndex[i] = childIndex[i];			
			this.childDefinition[i] = childDefinition[i];
						
			this.childQName[i] = new String[childQName[i].length];
			System.arraycopy(childQName[i], 0, this.childQName[i], 0, childQName[i].length);
			
			this.childSystemId[i] = new String[childSystemId[i].length];
			System.arraycopy(childSystemId[i], 0, this.childSystemId[i], 0, childSystemId[i].length);
			
			this.childLineNumber[i] = new int[childLineNumber[i].length];
			System.arraycopy(childLineNumber[i], 0, this.childLineNumber[i], 0, childLineNumber[i].length);
			
			this.childColumnNumber[i] = new int[childColumnNumber[i].length];
			System.arraycopy(childColumnNumber[i], 0, this.childColumnNumber[i], 0, childColumnNumber[i].length);
						
			this.childSourceDefinition[i] = new APattern[childSourceDefinition[i].length];
			System.arraycopy(childSourceDefinition[i], 0, this.childSourceDefinition[i], 0, childSourceDefinition[i].length);
		}
		this.lastCorrectChildIndexIndex = lastCorrectChildIndexIndex;
		
		if(this.size < size){
			childParticleHandlers = new ParticleHandler[size];
			childStructureHandlers = new StructureHandler[size];
		}
		this.size = size;
		for(int i = 0; i < size; i++){
			if(cph[i] != null)childParticleHandlers[i] = cph[i].getCopy(this, errorCatcher);
			if(csh[i] != null)childStructureHandlers[i] = csh[i].getCopy(this, stackHandler, errorCatcher);
		}
		if(contentHandlerContentIndex == NO_CONTENT){
			contentHandler = noContent;
		}else if(contentHandlerContentIndex == OPEN_CONTENT){
			contentHandler = openContent;
		}else if(contentHandlerContentIndex == SATISFIED_CONTENT){
			contentHandler = satisfiedContent;
		}else if(contentHandlerContentIndex == UNSATISFIED_SATURATED_CONTENT){
			contentHandler = unsatisfiedSaturatedContent;
		}else if(contentHandlerContentIndex == SATISFIED_SATURATED_CONTENT){
			contentHandler = satisfiedSaturatedContent;
		}else if(contentHandlerContentIndex == UNSATISFIED_EXCESSIVE_CONTENT){
			contentHandler = unsatisfiedExcessiveContent;
		}else if(contentHandlerContentIndex == SATISFIED_EXCESSIVE_CONTENT){
			contentHandler = satisfiedExcessiveContent;
		}else{
			throw new IllegalArgumentException();
		}
		this.satisfactionLevel = satisfactionLevel;
		this.saturationLevel = saturationLevel;
		this.starttSystemId = startSystemId;
		this.starttLineNumber = startLineNumber;
		this.starttColumnNumber = startColumnNumber;
		this.starttQName = startQName;		
	}
	
	// TODO 
	// Shouldn't this be based on startedCount? See reduce strategy order handling.
	boolean isCurrentChildReduceAcceptable(){
		if(currentChildParticleHandler.getIndex() == CardinalityHandler.SATURATED)return true;
		
		APattern currentChildPattern = currentChildParticleHandler.getRule();
		int currentChildIndex = currentChildPattern.getChildIndex();
		int potentialReduceCount = reduceCountList.get(currentChildIndex)+1;
		for(int i = 0; i < currentChildIndex; i++){
			if(reduceCountList.get(i) < potentialReduceCount)return false;
		}
		return true;
	}
	
	// -> creates new arrays at lastCorrectChildIndexIndex++
	private void addLastCorrectChildIndex(int newChildIndex, APattern definition, APattern sourceDefinition){
		if(++lastCorrectChildIndexIndex == correctChildIndexesSize){
			
			correctChildIndexesSize++;
			
			int increased[] = new int[correctChildIndexesSize];
			System.arraycopy(childIndex, 0, increased, 0, lastCorrectChildIndexIndex);
			childIndex = increased;
			
			APattern increasedDefinition[] = new APattern[correctChildIndexesSize];
			System.arraycopy(childDefinition, 0, increasedDefinition, 0, lastCorrectChildIndexIndex);
			childDefinition = increasedDefinition;
			
			String increasedQName[][] = new String[correctChildIndexesSize][];
			for(int i = 0; i < lastCorrectChildIndexIndex; i++){
				if(childQName[i] != null){
					increasedQName[i] = new String[childQName[i].length];
					System.arraycopy(childQName[i], 0, increasedQName[i], 0, childQName[i].length);
				}
			}
			//System.arraycopy(childQName, 0, increasedQName, 0, lastCorrectChildIndexIndex);
			childQName = increasedQName;
						
			String increasedSystemId[][] = new String[correctChildIndexesSize][];
			for(int i = 0; i < lastCorrectChildIndexIndex; i++){
				if(childSystemId[i] != null){
					increasedSystemId[i] = new String[childSystemId[i].length];
					System.arraycopy(childSystemId[i], 0, increasedSystemId[i], 0, childSystemId[i].length);
				}
			}
			//System.arraycopy(childSystemId, 0, increasedSystemId, 0, lastCorrectChildIndexIndex);
			childSystemId = increasedSystemId;
			
			int increasedLineNumber[][] = new int[correctChildIndexesSize][];
			for(int i = 0; i < lastCorrectChildIndexIndex; i++){
				if(childLineNumber[i] != null){
					increasedLineNumber[i] = new int[childLineNumber[i].length];
					System.arraycopy(childLineNumber[i], 0, increasedLineNumber[i], 0, childLineNumber[i].length);
				}
			}
			//System.arraycopy(childLineNumber, 0, increasedLineNumber, 0, lastCorrectChildIndexIndex);
			childLineNumber = increasedLineNumber;
						
			int increasedColumnNumber[][] = new int[correctChildIndexesSize][];
			for(int i = 0; i < lastCorrectChildIndexIndex; i++){
				if(childColumnNumber[i] != null){
					increasedColumnNumber[i] = new int[childColumnNumber[i].length];
					System.arraycopy(childColumnNumber[i], 0, increasedColumnNumber[i], 0, childColumnNumber[i].length);
				}
			}
			//System.arraycopy(childColumnNumber, 0, increasedColumnNumber, 0, lastCorrectChildIndexIndex);
			childColumnNumber = increasedColumnNumber;
			
			APattern increasedSourceDefinition[][] = new APattern[correctChildIndexesSize][];
			for(int i = 0; i < lastCorrectChildIndexIndex; i++){
				if(childSourceDefinition[i] != null){
					increasedSourceDefinition[i] = new APattern[childSourceDefinition[i].length];
					System.arraycopy(childSourceDefinition[i], 0, increasedSourceDefinition[i], 0, childSourceDefinition[i].length);
				}
			}
			//System.arraycopy(childSourceDefinition, 0, increasedSourceDefinition, 0, lastCorrectChildIndexIndex);
			childSourceDefinition = increasedSourceDefinition;
		}
		childIndex[lastCorrectChildIndexIndex] = newChildIndex;
		childDefinition[lastCorrectChildIndexIndex] = definition;
		
		childQName[lastCorrectChildIndexIndex] = new String[1];
		childQName[lastCorrectChildIndexIndex][0] = validationItemLocator.getQName();
		
		childSystemId[lastCorrectChildIndexIndex] = new String[1];
		childSystemId[lastCorrectChildIndexIndex][0] = validationItemLocator.getSystemId();
		
		childLineNumber[lastCorrectChildIndexIndex] = new int[1];
		childLineNumber[lastCorrectChildIndexIndex][0] = validationItemLocator.getLineNumber();
		
		childColumnNumber[lastCorrectChildIndexIndex] = new int[1];
		childColumnNumber[lastCorrectChildIndexIndex][0] = validationItemLocator.getColumnNumber();
		
		childSourceDefinition[lastCorrectChildIndexIndex] = new APattern[1];
		childSourceDefinition[lastCorrectChildIndexIndex][0] = sourceDefinition;
	}	
	//-> adds to the end of the arrays from lastCorrectChildIndexIndex
	private void addLastCorrectChildIndex(APattern sourceDefinition){
			
		int oldLength = childQName[lastCorrectChildIndexIndex].length;
		int newLength = (oldLength+1);
				
		
		String increasedQName[] = new String[newLength];
		System.arraycopy(childQName[lastCorrectChildIndexIndex], 0, increasedQName, 0, oldLength);
		childQName[lastCorrectChildIndexIndex] = increasedQName;
					
		String increasedSystemId[] = new String[newLength];
		System.arraycopy(childSystemId[lastCorrectChildIndexIndex], 0, increasedSystemId, 0, oldLength);
		childSystemId[lastCorrectChildIndexIndex] = increasedSystemId;
		
		int increasedLineNumber[] = new int[newLength];
		System.arraycopy(childLineNumber[lastCorrectChildIndexIndex], 0, increasedLineNumber, 0, oldLength);
		childLineNumber[lastCorrectChildIndexIndex] = increasedLineNumber;
					
		int increasedColumnNumber[] = new int[newLength];
		System.arraycopy(childColumnNumber[lastCorrectChildIndexIndex], 0, increasedColumnNumber, 0, oldLength);
		childColumnNumber[lastCorrectChildIndexIndex] = increasedColumnNumber;
		
		APattern increasedSourceDefinition[] = new APattern[newLength];
		System.arraycopy(childSourceDefinition[lastCorrectChildIndexIndex], 0, increasedSourceDefinition, 0, oldLength);
		childSourceDefinition[lastCorrectChildIndexIndex] = increasedSourceDefinition;
		
		int newIndex = oldLength;
		
		childQName[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getQName();
		childSystemId[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getSystemId();
		childLineNumber[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getLineNumber();
		childColumnNumber[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getColumnNumber();
		childSourceDefinition[lastCorrectChildIndexIndex][newIndex] = sourceDefinition;
	}	
	private void removeLastCorrectChildIndex(){
		childDefinition[lastCorrectChildIndexIndex] = null;
		childQName[lastCorrectChildIndexIndex] = null;
		childSystemId[lastCorrectChildIndexIndex] = null;
		childLineNumber[lastCorrectChildIndexIndex] = null;
		childColumnNumber[lastCorrectChildIndexIndex] = null;
		childSourceDefinition[lastCorrectChildIndexIndex] = null;
		lastCorrectChildIndexIndex--;
	}	
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}	
	public String toString(){
		//return "GroupMaximalReduceCountHandler "+hashCode()+" "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
		return "GroupMaximalReduceCountHandler  "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
	}
} 