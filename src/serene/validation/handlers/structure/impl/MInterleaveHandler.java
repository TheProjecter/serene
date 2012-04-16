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

import java.util.ArrayList;

import serene.util.IntList;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.structure.RuleHandlerVisitor;

/**
* Used for the handling of interleave when it may have a cardinality greater then
* one. It is not reduced until the end of the an entire group of interleave 
* occurrences, so it overrides all the usual cardinality handling and reduce 
* behaviour of the superclasses.
* 
*/
public class MInterleaveHandler extends InterleaveHandler{
	
	// children in the order they come
	APattern[] definition;
	int[] childInputRecordIndex;
	int currentChildIndex;
	int childMaxSize;
	int childInitialSize;
	int increaseSizeAmount;
	
	ArrayList<SInterleaveHandler> secondaryHandlers;	
	IntList lastShiftIndex;
	SInterleaveHandler currentSecondaryHandler;
	int mValidationLoopCounter;
	ActiveModelRuleHandlerPool pool;
	
	MInterleaveHandler original;
	
	MInterleaveHandler(){
		super();
		
	    currentChildIndex = -1;
	    childMaxSize = 20;
	    childInitialSize = 10;
	    increaseSizeAmount = 10;
	    childInputRecordIndex = new int[childInitialSize];	
	    definition = new APattern[childInitialSize];
	    
		secondaryHandlers = new ArrayList<SInterleaveHandler>();	
		lastShiftIndex = new IntList();
	}	
	
	void init(AInterleave interleave, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler, ActiveModelRuleHandlerPool pool){
		this.rule = interleave;		
		satisfactionIndicator = interleave.getSatisfactionIndicator();
		saturationIndicator = interleave.getSaturationIndicator();
		this.errorCatcher = errorCatcher;
		this.parent = parent;
		this.stackHandler = stackHandler;
		this.pool = pool;
		size = interleave.getChildrenCount();
		if(size > childParticleHandlers.length){			
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
	public StructureHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		int childIndex = child.getChildIndex();		
		if(childStructureHandlers[childIndex] == null){
			childStructureHandlers[childIndex] = child.getStructureHandler(errorCatcher, this, stackHandler);
		}
		return childStructureHandlers[childIndex];
	
	}
	// APattern getRule() super		
	public boolean handleChildShiftAndOrder(APattern pattern, int expectedOrderHandlingCount){		
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(inputStackDescriptor.getCurrentItemInputRecordIndex(), pattern);
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(APattern pattern, int startInputRecordIndex){
		handleParticleShift(startInputRecordIndex, pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex){
		handleParticleShift(startInputRecordIndex, pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex){
		throw new UnsupportedOperationException("Serene does not yet support interleave patterns "
			+"containing other compositors(group, interleave) with multiple cardinality.");
	}
	public void handleValidatingReduce(){
		//TODO		
		for(int i = 0; i < size; i++){			
			if(childStructureHandlers[i] != null){
				childStructureHandlers[i].handleValidatingReduce();					
			}
		}
		int occurrenceCount = computeOccurrencesCount();		
		stackHandler.blockReduce(this, occurrenceCount, rule, childInputRecordIndex[0]);
	}
	
	
	public int functionalEquivalenceCode(){		
			int fec = super.functionalEquivalenceCode();	
			for(int i = 0; i <= currentChildIndex; i++){
				fec += definition[i].hashCode();
			}
			return fec;
	}	
	
	//TODO
	public MInterleaveHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		if(!secondaryHandlers.isEmpty())throw new IllegalStateException();
		
		MInterleaveHandler copy = (MInterleaveHandler)((AInterleave)rule).getStructureHandler(errorCatcher, parent, stackHandler);
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
						definition,
						childInputRecordIndex,
						currentChildIndex);
		copy.setOriginal(this);
		return copy; 
	}
	public MInterleaveHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		if(!secondaryHandlers.isEmpty())throw new IllegalStateException();
		
		MInterleaveHandler copy = (MInterleaveHandler)((AInterleave)rule).getStructureHandler(errorCatcher, (StructureValidationHandler)parent, stackHandler);
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
					definition,
					childInputRecordIndex,
					currentChildIndex);
		copy.setOriginal(this);
		return copy;
	}
	private void setOriginal(MInterleaveHandler original){
		this.original = original;
	}
	public MInterleaveHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
	
	//Start ValidationHandler---------------------------------------------------------		
	void handleParticleShift(int inputRecordIndex, APattern cd){
		// for(int i = 0; i < childrenSize; i++){		
			// System.out.print(qName[i]+" ");			
		// }
		// System.out.println();
		
		if(++currentChildIndex == childInputRecordIndex.length) increaseChildrenStorageSize();
		if(currentChildIndex == 0)setStart();
		
		childInputRecordIndex[currentChildIndex] = inputRecordIndex;
		definition[currentChildIndex] = cd;
		activeInputDescriptor.registerClientForRecord(inputRecordIndex, this);
		
	}	
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		throw new UnsupportedOperationException();
	}
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler){
		throw new UnsupportedOperationException();
	}
	// boolean acceptsMDescendentReduce(APattern p) super
	// boolean handleContentOrder(int expectedOrderHandlingCount, boolean conflict, APattern childDefinition, AElement sourceDefinition) super	
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
		
	
	//Start InnerPattern---------------------------------------------------------------	
	boolean handleStateSaturationReduce(){
		//never reduce until the secondary handlers can be used
		throw new IllegalStateException();
	}
	// boolean isReduceAllowed() super
	// boolean isReduceRequired() super
	// boolean isReduceAcceptable() super
	// void setCurrentChildParticleHandler(APattern childPattern) super
	// void closeContent() super
	// void closeContentParticle(APattern childPattern) super
	void setEmptyState(){
		closeContent();
		contentIndex = NO_CONTENT;
		satisfactionLevel = 0;
		saturationLevel = 0;				
		//***		isReduceLocked = false;
		
		for(int i =0; i <= currentChildIndex; i++){		    
		    activeInputDescriptor.unregisterClientForRecord(childInputRecordIndex[i], this);
		    definition[i] = null;
		}
		if(definition.length > childMaxSize){
		    definition = new APattern[childInitialSize];
		    childInputRecordIndex = new int[childInitialSize];
		}
		currentChildIndex = -1;
		
		for(int i = 0; i < secondaryHandlers.size(); i++){
			SInterleaveHandler sih = secondaryHandlers.get(i);
			sih.recycle();
		}		
		secondaryHandlers.clear();
		
		currentSecondaryHandler.recycle();
		currentSecondaryHandler = null;
		
		lastShiftIndex.clear();
		mValidationLoopCounter = -1;
		
		if(isStartSet){
		    activeInputDescriptor.unregisterClientForRecord(startInputRecordIndex, this);
		    isStartSet = false;
		    startInputRecordIndex = -1;
		}	
	}
	//End InnerPattern--------------------------------------------------------------------

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
							APattern[] definition,
							int[] childInputRecordIndex,
							int currentChildIndex){
		
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
		
		
		this.currentChildIndex = currentChildIndex;
		
		while(definition.length <= currentChildIndex) increaseChildrenStorageSize();
		for(int i = 0; i <= currentChildIndex; i++){
		    this.definition[i] = definition[i];
		    this.childInputRecordIndex[i] = childInputRecordIndex[i];
		    activeInputDescriptor.registerClientForRecord(childInputRecordIndex[i], this);
		}
	}	
	
	
	int computeOccurrencesCount(){
		// System.out.println("m validating reduce");
		currentSecondaryHandler = pool.getSInterleaveHandler((AInterleave)rule, errorCatcher, null, stackHandler, this);
		int occurrencesCount = 0;	
		
		for(mValidationLoopCounter = 0; mValidationLoopCounter <= currentChildIndex; mValidationLoopCounter++){
			// System.out.println("\tloop "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
			currentSecondaryHandler.handleParticleShift(childInputRecordIndex[mValidationLoopCounter],
														definition[mValidationLoopCounter]);
			if(mValidationLoopCounter == currentChildIndex){				
				if(!currentSecondaryHandler.isSatisfied()){					
					// System.out.println("last unsatisfied "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
					currentSecondaryHandler.recycle();
					currentSecondaryHandler = secondaryHandlers.remove(secondaryHandlers.size()-1);
					mValidationLoopCounter = lastShiftIndex.removeLast();
					// System.out.println("last unsatisfied go to "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
				}
			}
			if(secondaryHandlers.size() > occurrencesCount){
				occurrencesCount = secondaryHandlers.size();
			}
		}
		
		// System.out.println("occurrences count "+secondaryHandlers.size()+" "+occurrencesCount);
		return occurrencesCount;
	}
	
	void satisfiedOccurrence(){
		if(mValidationLoopCounter == currentChildIndex)return;		
		secondaryHandlers.add(currentSecondaryHandler);
		lastShiftIndex.add(mValidationLoopCounter);
		currentSecondaryHandler = pool.getSInterleaveHandler((AInterleave)rule, errorCatcher, null, stackHandler, this);
		// System.out.println("satisfied "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
		// System.out.println("satisfied "+secondaryHandlers);
	}
	
	void excessiveOccurrence(){
		if(secondaryHandlers.isEmpty()){
			errorCatcher.illegalContent(rule, childInputRecordIndex[0]);			
			mValidationLoopCounter = currentChildIndex+1;//stop the loop
			return;
		}
		// System.out.println("excessive "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
		currentSecondaryHandler.recycle();
		currentSecondaryHandler = secondaryHandlers.remove(secondaryHandlers.size()-1);
		mValidationLoopCounter = lastShiftIndex.removeLast();		
		// System.out.println("excessive go to "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
	}
	
	void increaseChildrenStorageSize(){
		
		int newSize = childInputRecordIndex.length + increaseSizeAmount;
		
		APattern[] increasedD = new APattern[newSize];
		System.arraycopy(definition, 0, increasedD, 0, currentChildIndex);
		definition = increasedD;
		
		int[] increasedCIRI = new int[newSize];
		System.arraycopy(childInputRecordIndex, 0, increasedCIRI, 0, currentChildIndex);
		childInputRecordIndex = increasedCIRI;
		
	}
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	public String toString(){		
		//return "MInterleaveHandler "+hashCode()+" "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
		return "MInterleaveHandler  "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentIndex="+contentIndex;
	}
} 