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

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;

import serene.validation.handlers.error.ErrorCatcher;

import serene.util.IntList;

public class GroupMinimalReduceCountHandler extends MinimalReduceCountHandler{
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
	
	GroupMinimalReduceCountHandler original;
	
	GroupMinimalReduceCountHandler(){
		super();
	
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

	void init(IntList reduceCountList, IntList startedCountList, AGroup group, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler){
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
	// void handleValidatingReduce() super
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
	public GroupMinimalReduceCountHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public GroupMinimalReduceCountHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public GroupMinimalReduceCountHandler getCopy(IntList reduceCountList, IntList startedCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		GroupMinimalReduceCountHandler copy = ((AGroup)rule).getStructureHandler(reduceCountList,
																				startedCountList,
																				errorCatcher,
																				(MinimalReduceStackHandler)stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandlers,						
						childStructureHandlers,
						size,
						satisfactionLevel,
						saturationLevel,
						contentIndex,
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
	private void setOriginal(GroupMinimalReduceCountHandler original){
		this.original = original;
	}
	public GroupMinimalReduceCountHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
	
	//Start ValidationHandler---------------------------------------------------------	
	// boolean handleChildShift(String systemId, int lineNumber, int columnNumber, String qName, APattern childPattern) super			
	// boolean acceptsMDescendentReduce(APattern p) super	
	// void setStart() super
	//End ValidationHandler-----------------------------------------------------------
	

	
	//Start InnerPattern------------------------------------------------------------------
	// boolean handleStateSaturationReduce() super	
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
		if(childParticleHandlers[childIndex] == null){
			childParticleHandlers[childIndex] = childPattern.getParticleHandler(this, errorCatcher);
			
			// Update the startedCountList since a new occurrence starts.
			int started = startedCountList.get(childIndex);
			startedCountList.set(childIndex, ++started);		
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
	
	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler[] cph, 
							StructureHandler[] csh,
							int size,
							int satisfactionLevel,
							int saturationLevel,
							int contentIndex,
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
		
		this.contentIndex = contentIndex;
		
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
		//return "GroupMinimalReduceCountHandler "+hashCode()+" "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
		return "GroupMinimalReduceCountHandler  "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentIndex="+contentIndex;
	}
	
} 