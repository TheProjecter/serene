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

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.structure.RuleHandlerVisitor;

import sereneWrite.MessageWriter;

/**
* Used for the handling of interleave when it may have a cardinality greater then
* one. It is not reduced until the end of the an entire group of interleave 
* occurrences, so it overrides all the usual cardinality handling and reduce 
* behaviour of the superclasses.
* 
*/
public class MInterleaveHandler extends InterleaveHandler{
	
	// children in the order they come
	private APattern[] definition;
	private int[] itemId;
	private String[] qName;
	private String[] systemId;
	private int[] lineNumber;
	private int[] columnNumber;
	private int childIndex;
	private int childrenSize;
	
	
	ArrayList<SInterleaveHandler> secondaryHandlers;	
	IntList lastShiftIndex;
	SInterleaveHandler currentSecondaryHandler;
	int mValidationLoopCounter;
	ActiveModelRuleHandlerPool pool;
	
	MInterleaveHandler original;
	
	MInterleaveHandler(MessageWriter debugWriter){
		super(debugWriter);
		
		childIndex = -1;
		childrenSize = 10;
		
		definition = new APattern[childrenSize];
		itemId = new int[childrenSize];
		qName = new String[childrenSize];
		systemId = new String[childrenSize];
		lineNumber = new int[childrenSize];
		columnNumber = new int[childrenSize];	
		
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
	public boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount){		
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), validationItemLocator.getQName(), validationItemLocator.getItemId(), pattern);
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(APattern pattern, int itemId, String startQName, String startSystemId, int lineNumber, int columnNumber){
		handleParticleShift(startSystemId, lineNumber, columnNumber, startQName, itemId, pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int count, APattern pattern, int itemId, String startQName, String startSystemId, int lineNumber, int columnNumber){
		handleParticleShift(startSystemId, lineNumber, columnNumber, startQName, itemId, pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int itemId, String startQName, String startSystemId, int lineNumber, int columnNumber){
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
		stackHandler.blockReduce(this, occurrenceCount, rule, itemId[0], qName[0], systemId[0], lineNumber[0], columnNumber[0]);
	}
	
	
	public int functionalEquivalenceCode(){		
			int fec = super.functionalEquivalenceCode();	
			for(int i = 0; i <= childIndex; i++){
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
						contentHandler.getContentIndex(),
						starttSystemId,
						starttLineNumber,
						starttColumnNumber,
						starttQName,
						definition,
						itemId,
						qName,
						systemId,
						lineNumber,
						columnNumber,
						childIndex,
						childrenSize);
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
					contentHandler.getContentIndex(),
					starttSystemId,
					starttLineNumber,
					starttColumnNumber,
					starttQName,
					definition,
					itemId,
					qName,
					systemId,
					lineNumber,
					columnNumber,
					childIndex,
					childrenSize);
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
	void handleParticleShift(String sysId, int ln, int cn, String qn, int iti, APattern cd){		
		if(++childIndex == childrenSize)increaseChildrenStorageSize();
		if(childIndex == 0)setStart();
		
		systemId[childIndex] = sysId;
		lineNumber[childIndex] = ln;
		columnNumber[childIndex] = cn;
		qName[childIndex] = qn;
		itemId[childIndex] = iti;
		definition[childIndex] = cd;
		
		// for(int i = 0; i < childrenSize; i++){		
			// System.out.print(qName[i]+" ");			
		// }
		// System.out.println();
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
		contentHandler = noContent;
		starttQName = null;
		starttSystemId = null;
		satisfactionLevel = 0;
		saturationLevel = 0;				
		//***		isReduceLocked = false;
		
		for(int i = 0; i < childrenSize; i++){
			definition[i] = null;
			qName[i] = null;
			systemId[i] = null;
			itemId[i] = ValidationItemLocator.NONE;
		}
		childIndex = -1;
		
		for(int i = 0; i < secondaryHandlers.size(); i++){
			SInterleaveHandler sih = secondaryHandlers.get(i);
			sih.recycle();
		}		
		secondaryHandlers.clear();
		lastShiftIndex.clear();
		mValidationLoopCounter = -1;
	}
	//End InnerPattern--------------------------------------------------------------------

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
							APattern[] definition,
							int[] itemId,
							String[] qName,
							String[] systemId,
							int[] lineNumber,
							int[] columnNumber,
							int childIndex,
							int childrenSize){
		
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
	
		this.childIndex = childIndex;
		this.childrenSize = childrenSize;
		
		this.definition = new APattern[childrenSize];
		this.itemId = new int[childrenSize];
		this.qName = new String[childrenSize];
		this.systemId = new String[childrenSize];
		this.lineNumber = new int[childrenSize];
		this.columnNumber = new int[childrenSize];
		
		for(int i = 0; i <= childIndex; i++){
			this.definition[i] = definition[i];
			this.itemId[i] = itemId[i];
			this.qName[i] = qName[i];
			this.systemId[i] = systemId[i];
			this.lineNumber[i] = lineNumber[i];
			this.columnNumber[i] = columnNumber[i];
		}
	}	
	
	
	int computeOccurrencesCount(){
		// System.out.println("m validating reduce");
		currentSecondaryHandler = pool.getSInterleaveHandler((AInterleave)rule, errorCatcher, null, stackHandler, this);
		int occurrencesCount = 0;		
		for(mValidationLoopCounter = 0; mValidationLoopCounter <= childIndex; mValidationLoopCounter++){
			// System.out.println("\tloop "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
			currentSecondaryHandler.handleParticleShift(systemId[mValidationLoopCounter],
														lineNumber[mValidationLoopCounter],
														columnNumber[mValidationLoopCounter],
														qName[mValidationLoopCounter],
														itemId[mValidationLoopCounter],
														definition[mValidationLoopCounter]);
			if(mValidationLoopCounter == childIndex){				
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
		if(mValidationLoopCounter == childIndex)return;
		secondaryHandlers.add(currentSecondaryHandler);
		lastShiftIndex.add(mValidationLoopCounter);
		currentSecondaryHandler = pool.getSInterleaveHandler((AInterleave)rule, errorCatcher, null, stackHandler, this);
		// System.out.println("satisfied "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
		// System.out.println("satisfied "+secondaryHandlers);
	}
	
	void excessiveOccurrence(){
		if(secondaryHandlers.isEmpty()){
			// System.out.println("ERROR: illegal content in document structure corresponding to interleave");
			errorCatcher.illegalContent(rule, qName[0], systemId[0], lineNumber[0], columnNumber[0]);
			mValidationLoopCounter = childIndex+1;//stop the loop
			return;
		}
		// System.out.println("excessive "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
		currentSecondaryHandler.recycle();
		currentSecondaryHandler = secondaryHandlers.remove(secondaryHandlers.size()-1);
		mValidationLoopCounter = lastShiftIndex.removeLast();		
		// System.out.println("excessive go to "+mValidationLoopCounter+" "+qName[mValidationLoopCounter]);
	}
	
	void increaseChildrenStorageSize(){
		childrenSize += 10;
		
		APattern[] increasedD = new APattern[childrenSize];
		System.arraycopy(definition, 0, increasedD, 0, childIndex);
		definition = increasedD;
		
		int[] increasedITI = new int[childrenSize];
		System.arraycopy(itemId, 0, increasedITI, 0, childIndex);
		itemId = increasedITI;
		
		String[] increasedQN = new String[childrenSize];
		System.arraycopy(qName, 0, increasedQN, 0, childIndex);
		qName = increasedQN;
		
		String[] increasedSI = new String[childrenSize];
		System.arraycopy(systemId, 0, increasedSI, 0, childIndex);
		systemId = increasedSI;
					
		int[] increasedLN = new int[childrenSize];
		System.arraycopy(lineNumber, 0, increasedLN, 0, childIndex);
		lineNumber = increasedLN;
		
		int[] increasedCN = new int[childrenSize];
		System.arraycopy(columnNumber, 0, increasedCN, 0, childIndex);
		columnNumber = increasedCN;
	}
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	public String toString(){		
		//return "MInterleaveHandler "+hashCode()+" "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
		return "MInterleaveHandler  "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
	}
} 