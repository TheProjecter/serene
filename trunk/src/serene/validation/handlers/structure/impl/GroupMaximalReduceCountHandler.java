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
	
	APattern[] childDefinition;	
	int[][] childInputRecordIndex;	
	APattern[][] childSourceDefinition;
	
	int childRecordIndex;
	int childRecordIncreaseSizeAmount;
	int childRecordInitialSize;
	
    /**
    * To keep track of the current index in the childInputRecordIndex and  
    * childSourceDefinition so that it's not necessary to increment size, but
    * it can grow with bigger steps.
    */	
	int[] childDetailsCurrentIndex;
	int childDetailsInitialSize;
	int childDetailsIncreaseSizeAmount;

	IntList startedCountList;
	
	GroupMaximalReduceCountHandler original;
	GroupMaximalReduceCountHandler(MessageWriter debugWriter){
		super(debugWriter);
	
		childRecordIndex = -1;
		childRecordInitialSize = 10;
		childRecordIncreaseSizeAmount = 10;
		
		
		childDefinition = new APattern[childRecordInitialSize];
		childInputRecordIndex = new int[childRecordInitialSize][];
		childSourceDefinition = new APattern[childRecordInitialSize][];
        
		childDetailsCurrentIndex = new int[childRecordInitialSize];
		Arrays.fill(childDetailsCurrentIndex, -1);
		childDetailsIncreaseSizeAmount = 10;
		childDetailsInitialSize = 10;
		
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
		int prevCorrectChildIndex = childRecordIndex < 0 ? -1 : childDefinition[childRecordIndex].getChildIndex();
		int orderIndex = GOOD_ORDER;
		
		if(newChildIndex < prevCorrectChildIndex){
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
		}else if(newChildIndex == prevCorrectChildIndex){
			addLastCorrectChildIndex(sourceDefinition);
		}else{			
			addLastCorrectChildIndex(child, sourceDefinition);					
		}
		return true;		
	}
	
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern child, APattern sourceDefinition, InternalConflictResolver resolver){
		throw new IllegalStateException();
	}
	
	public int functionalEquivalenceCode(){
		int orderCode = 0;
		for(int i = 0; i <= childRecordIndex; i++){
			orderCode += childDefinition[i].getChildIndex();
		}	
		orderCode *= (childRecordIndex+1);
		orderCode *= 1000;
		return super.functionalEquivalenceCode() + orderCode;
	}	
	public GroupMaximalReduceCountHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public GroupMaximalReduceCountHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
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
						startInputRecordIndex,
						isStartSet,
						childDefinition,
						childInputRecordIndex,
						childSourceDefinition,
						childRecordIndex,
						childDetailsCurrentIndex);
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
		
		for(int j = 0; j <= childRecordIndex; j++){
		    for(int i = 0; i <= childDetailsCurrentIndex[j]; i++){
		        activeInputDescriptor.unregisterClientForRecord(childInputRecordIndex[j][i], this);
		        childSourceDefinition[j][i] = null;
		    }
		    childDetailsCurrentIndex[j] = -1;
		    childDefinition[j] = null;
		}
		childRecordIndex = -1;
		
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
							int startInputRecordIndex,
							boolean isStartSet,
							APattern[] childDefinition,
							int[][] childInputRecordIndex,
							APattern[][] childSourceDefinition,
							int childRecordIndex,
							int[] childDetailsCurrentIndex){		
		if(this.childDefinition.length < childDefinition.length){
			this.childDefinition = new APattern[childDefinition.length];
			this.childInputRecordIndex = new int[childDefinition.length][];
			this.childSourceDefinition = new APattern[childDefinition.length][];
            this.childDetailsCurrentIndex = new int[childDefinition.length];			
		}		
		
		for(int i = 0; i <= childRecordIndex; i++){		
		    this.childDefinition[i] = childDefinition[i];				
            this.childDetailsCurrentIndex[i] = childDetailsCurrentIndex[i];    
		    
            this.childInputRecordIndex[i] = new int[childInputRecordIndex[i].length];                
            this.childSourceDefinition[i] = new APattern[childSourceDefinition[i].length];
            for(int j = 0; j <= childDetailsCurrentIndex[i]; j++){
                this.childInputRecordIndex[i][j] = childInputRecordIndex[i][j];                
                this.childSourceDefinition[i][j] = childSourceDefinition[i][j];
                activeInputDescriptor.registerClientForRecord(childInputRecordIndex[i][j], this);
			}
		}
		this.childRecordIndex = childRecordIndex;
			
		
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
		
		if(this.isStartSet){
            activeInputDescriptor.unregisterClientForRecord(this.startInputRecordIndex, this);
        }
		this.startInputRecordIndex = startInputRecordIndex;
		this.isStartSet = isStartSet;
		if(isStartSet){		    
		    activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		}
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
	private void addLastCorrectChildIndex(APattern definition, APattern sourceDefinition){
		if(++childRecordIndex == childDefinition.length){
			
			int size = childDefinition.length+childRecordIncreaseSizeAmount;
			
			APattern increasedDefinition[] = new APattern[size];
			System.arraycopy(childDefinition, 0, increasedDefinition, 0, childRecordIndex);
			childDefinition = increasedDefinition;
			
			int increasedCDCI[] = new int[size];
			System.arraycopy(childDetailsCurrentIndex, 0, increasedCDCI, 0, childRecordIndex);
			childDetailsCurrentIndex = increasedCDCI;			
			Arrays.fill(childDetailsCurrentIndex, childRecordIndex, childDetailsCurrentIndex.length, -1);			
			
			int[][] increasedCII = new int[size][];
			for(int i = 0; i < childRecordIndex; i++){
				if(childInputRecordIndex[i] != null){
					increasedCII[i] = new int[childInputRecordIndex[i].length];
					System.arraycopy(childInputRecordIndex[i], 0, increasedCII[i], 0, childDetailsCurrentIndex[i]+1);
				}
			}
			//System.arraycopy(childInputRecordIndex, 0, increasedCII, 0, childRecordIndex);
			childInputRecordIndex = increasedCII;
			
			APattern increasedSourceDefinition[][] = new APattern[size][];
			for(int i = 0; i < childRecordIndex; i++){
				if(childSourceDefinition[i] != null){
					increasedSourceDefinition[i] = new APattern[childSourceDefinition[i].length];
					System.arraycopy(childSourceDefinition[i], 0, increasedSourceDefinition[i], 0, childDetailsCurrentIndex[i]+1);
				}
			}
			//System.arraycopy(childSourceDefinition, 0, increasedSourceDefinition, 0, childRecordIndex);
			childSourceDefinition = increasedSourceDefinition;
		}
		childDefinition[childRecordIndex] = definition;
		childDetailsCurrentIndex[childRecordIndex] = 0;
		
		childInputRecordIndex[childRecordIndex] = new int[childDetailsInitialSize];
		childInputRecordIndex[childRecordIndex][childDetailsCurrentIndex[childRecordIndex]] = inputStackDescriptor.getCurrentItemInputRecordIndex();
		
		
		childSourceDefinition[childRecordIndex] = new ActiveTypeItem[childDetailsInitialSize];
		childSourceDefinition[childRecordIndex][childDetailsCurrentIndex[childRecordIndex]] = sourceDefinition;
				
		activeInputDescriptor.registerClientForRecord(childInputRecordIndex[childRecordIndex][childDetailsCurrentIndex[childRecordIndex]], this);
	}	
	//-> adds to the end of the arrays from lastCorrectChildIndexIndex
	private void addLastCorrectChildIndex(APattern sourceDefinition){
		int oldLength = childInputRecordIndex[childRecordIndex].length;	
		childDetailsCurrentIndex[childRecordIndex] += 1;
		
		if(childDetailsCurrentIndex[childRecordIndex] == oldLength){
		    int newLength = oldLength+childDetailsIncreaseSizeAmount;
		
		
            int increasedCII[] = new int[newLength];
            System.arraycopy(childInputRecordIndex[childRecordIndex], 0, increasedCII, 0, oldLength);
            childInputRecordIndex[childRecordIndex] = increasedCII;
			
            APattern increasedSourceDefinition[] = new APattern[newLength];
            System.arraycopy(childSourceDefinition[childRecordIndex], 0, increasedSourceDefinition, 0, oldLength);
            childSourceDefinition[childRecordIndex] = increasedSourceDefinition;
        }
				
		childInputRecordIndex[childRecordIndex][childDetailsCurrentIndex[childRecordIndex]] = inputStackDescriptor.getCurrentItemInputRecordIndex();
		childSourceDefinition[childRecordIndex][childDetailsCurrentIndex[childRecordIndex]] = sourceDefinition;
		
		activeInputDescriptor.registerClientForRecord(childInputRecordIndex[childRecordIndex][childDetailsCurrentIndex[childRecordIndex]], this);
	}	
	private void removeLastCorrectChildIndex(){
		for(int i = 0; i <= childDetailsCurrentIndex[childRecordIndex]; i++){
	        activeInputDescriptor.unregisterClientForRecord(childInputRecordIndex[childRecordIndex][i], this);
	        childSourceDefinition[childRecordIndex][i] = null;
	    }
	    
		childDefinition[childRecordIndex] = null;	
		childDetailsCurrentIndex[childRecordIndex] = -1;
		
		childRecordIndex--;
	}	
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}	
	public String toString(){
		//return "GroupMaximalReduceCountHandler "+hashCode()+" "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
		return "GroupMaximalReduceCountHandler  "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
	}
} 