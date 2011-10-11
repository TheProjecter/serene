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

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;

import serene.validation.handlers.error.ErrorCatcher;


import sereneWrite.MessageWriter;

public class GroupMaximalReduceHandler extends MCMaximalReduceHandler{
	final int GOOD_ORDER = -1;	
	final int CURRENT_MISPLACED = 0;
	final int PREVIOUS_MISPLACED = 1;
	
	int[] childIndex;
	APattern[] childDefinition;
	int[][] childItemId;
	String[][] childQName;
	String[][] childSystemId;
	int[][] childLineNumber;
	int[][] childColumnNumber;
	APattern[][] childSourceDefinition;
	
	int lastCorrectChildIndexIndex;
	int correctChildIndexesSize;
	
	GroupMaximalReduceHandler original;
	
	GroupMaximalReduceHandler(MessageWriter debugWriter){
		super(debugWriter);
	
		lastCorrectChildIndexIndex = 1;
		correctChildIndexesSize = 2;
		
		childIndex = new int[correctChildIndexesSize];
		childIndex[0] = -1;
		childIndex[1] = -1;
		
		childDefinition = new APattern[correctChildIndexesSize];
		childItemId = new int[correctChildIndexesSize][];
		childQName = new String[correctChildIndexesSize][];
		childSystemId = new String[correctChildIndexesSize][];
		childLineNumber = new int[correctChildIndexesSize][];
		childColumnNumber = new int[correctChildIndexesSize][];
		childSourceDefinition = new APattern[correctChildIndexesSize][];
	}

	void init(AGroup group, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		this.rule = group;		
		satisfactionIndicator = group.getSatisfactionIndicator();
		saturationIndicator = group.getSaturationIndicator();
		this.errorCatcher = errorCatcher;		
		this.parent = parent;
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
    // void deactivate() super
	// StructureHandler getChildHandler(Rule child) super
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	// void handleValidatingReduce() super
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern child, APattern sourceDefinition){		
		int newChildIndex = child.getChildIndex();
		int orderIndex = GOOD_ORDER;
		
		APattern reper = null;
		boolean[] prevConflict = null;
		APattern prevDefinition = null;
		int[] prevItemId = null;
		String[] prevQName = null; 
		String[] prevSystemId = null; 
		int[] prevLineNumber = null; 
		int[] prevColumnNumber = null; 
		APattern[] prevSourceDefinition = null;
									
		if(newChildIndex < childIndex[lastCorrectChildIndexIndex]){			
			// add here the reduce/reset part if the contentHandler is satisfied and reduction acceptable			
			// return; the error is neihter located nor reported			
			
			if(newChildIndex < childIndex[lastCorrectChildIndexIndex-1]){
				//newChildIndex is misplaced					
                if(handleOrderUncheckedReduce(sourceDefinition)){					
                    return false;
                }
				orderIndex = CURRENT_MISPLACED;
				reper = childDefinition[lastCorrectChildIndexIndex]; 				
				// do not store, it cannot be used for comparisons
			}else if(newChildIndex == childIndex[lastCorrectChildIndexIndex-1]){				
				// childIndex[lastCorrectChildIndexIndex] is misplaced
                if(handleOrderCheckedReduce(sourceDefinition)){					
                    return false;
                }
				orderIndex = PREVIOUS_MISPLACED;
				
				reper = child;
				prevDefinition = childDefinition[lastCorrectChildIndexIndex];
				prevItemId = childItemId[lastCorrectChildIndexIndex];
				prevQName = childQName[lastCorrectChildIndexIndex];
				prevSystemId = childSystemId[lastCorrectChildIndexIndex]; 
				prevLineNumber = childLineNumber[lastCorrectChildIndexIndex]; 
				prevColumnNumber = childColumnNumber[lastCorrectChildIndexIndex];
				prevSourceDefinition = childSourceDefinition[lastCorrectChildIndexIndex];
				
				removeLastCorrectChildIndex();					
				addLastCorrectChildIndex(sourceDefinition);
			}else{
				//childIndex[lastCorrectChildIndexIndex] is misplaced
                if(handleOrderCheckedReduce(sourceDefinition)){					
                    return false;
                }
				orderIndex = PREVIOUS_MISPLACED;				
				
				reper = child;
				prevDefinition = childDefinition[lastCorrectChildIndexIndex];
				prevItemId = childItemId[lastCorrectChildIndexIndex];
				prevQName = childQName[lastCorrectChildIndexIndex];
				prevSystemId = childSystemId[lastCorrectChildIndexIndex]; 
				prevLineNumber = childLineNumber[lastCorrectChildIndexIndex]; 
				prevColumnNumber = childColumnNumber[lastCorrectChildIndexIndex];
				prevSourceDefinition = childSourceDefinition[lastCorrectChildIndexIndex];
				
				removeLastCorrectChildIndex();				
				addLastCorrectChildIndex(newChildIndex, child, sourceDefinition);
			}
		}else if(newChildIndex == childIndex[lastCorrectChildIndexIndex]){
			addLastCorrectChildIndex(sourceDefinition);
		}else{			
			addLastCorrectChildIndex(newChildIndex, child, sourceDefinition);					
		}
		if(--expectedOrderHandlingCount > 0){		
			if(parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition)){
				// report errors
				String contextSystemId;
				int contextLineNumber;
				int contextColumnNumber;
				if(starttSystemId == null){
					contextSystemId = validationItemLocator.getSystemId();
					contextLineNumber = validationItemLocator.getLineNumber();
					contextColumnNumber = validationItemLocator.getColumnNumber();
				}else{
					contextSystemId = starttSystemId;
					contextLineNumber = starttLineNumber;
					contextColumnNumber = starttColumnNumber;
				}
				
				if(orderIndex == CURRENT_MISPLACED){					
					errorCatcher.misplacedContent(rule, 
														contextSystemId,//validationItemLocator.getSystemId(), 
														contextLineNumber, //validationItemLocator.getLineNumber(), 
														contextColumnNumber, //validationItemLocator.getColumnNumber(), 
														child, 
														validationItemLocator.getItemId(),
														validationItemLocator.getItemIdentifier(),
														validationItemLocator.getSystemId(), 
														validationItemLocator.getLineNumber(), 
														validationItemLocator.getColumnNumber(), 
														sourceDefinition, 
														reper);
				}else if(orderIndex == PREVIOUS_MISPLACED){						
					errorCatcher.misplacedContent(rule, 
														contextSystemId,//validationItemLocator.getSystemId(), 
														contextLineNumber, //validationItemLocator.getLineNumber(), 
														contextColumnNumber, //validationItemLocator.getColumnNumber(),															
														prevDefinition,
														prevItemId,
														prevQName,
														prevSystemId, 
														prevLineNumber, 
														prevColumnNumber,
														prevSourceDefinition,
														reper);
				}
				return true;
			}else{
				return false;
			}
		}else{
			// report errors
			String contextSystemId;
			int contextLineNumber;
			int contextColumnNumber;
			if(starttSystemId == null){
				contextSystemId = validationItemLocator.getSystemId();
				contextLineNumber = validationItemLocator.getLineNumber();
				contextColumnNumber = validationItemLocator.getColumnNumber();
			}else{
				contextSystemId = starttSystemId;
				contextLineNumber = starttLineNumber;
				contextColumnNumber = starttColumnNumber;
			}
				
			if(orderIndex == CURRENT_MISPLACED){				
				errorCatcher.misplacedContent(rule, 
													contextSystemId,//validationItemLocator.getSystemId(), 
													contextLineNumber, //validationItemLocator.getLineNumber(), 
													contextColumnNumber, //validationItemLocator.getColumnNumber(), 
													child,
													validationItemLocator.getItemId(),
													validationItemLocator.getItemIdentifier(),
													validationItemLocator.getSystemId(), 
													validationItemLocator.getLineNumber(), 
													validationItemLocator.getColumnNumber(), 
													sourceDefinition,
													reper);
				}else if(orderIndex == PREVIOUS_MISPLACED){
					errorCatcher.misplacedContent(rule, 
														contextSystemId,//validationItemLocator.getSystemId(), 
														contextLineNumber, //validationItemLocator.getLineNumber(), 
														contextColumnNumber, //validationItemLocator.getColumnNumber(),															
														prevDefinition,
														prevItemId,
														prevQName,
														prevSystemId, 
														prevLineNumber, 
														prevColumnNumber,
														prevSourceDefinition,
														reper);
				}
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
	public GroupMaximalReduceHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		GroupMaximalReduceHandler copy = ((AGroup)rule).getStructureHandler(errorCatcher, 
													(MaximalReduceHandler)parent, 
													stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandlers, 
						childStructureHandlers,
						size,
						contentHandler.getContentIndex(),
						starttSystemId,
						starttLineNumber,
						starttColumnNumber,
						starttQName,
						childIndex,
						childDefinition,
						childItemId,
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
	public GroupMaximalReduceHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		GroupMaximalReduceHandler copy = ((AGroup)rule).getStructureHandler(errorCatcher, 
													(MaximalReduceHandler)parent, 
													stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandlers, 
						childStructureHandlers,
						size,
						contentHandler.getContentIndex(),
						starttSystemId,
						starttLineNumber,
						starttColumnNumber,
						starttQName,
						childIndex,
						childDefinition,
						childItemId,
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
	private void setOriginal(GroupMaximalReduceHandler original){
		this.original = original;
	}
	public GroupMaximalReduceHandler getOriginal(){
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
	// void setCurrentChildParticleHandler(APattern childPattern) super
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
			childItemId[i] = null;
			childQName[i] = null;
			childSystemId[i] = null;
			childLineNumber[i] = null;
			childColumnNumber[i] = null;
			childSourceDefinition[i] = null;
		}		
		
	}			
	//End InnerPattern------------------------------------------------------------------
	
	boolean handleOrderCheckedReduce(APattern sourceDefinition){		
		if(isReduceAllowed() && isReduceAcceptable()){
			handleReshift(sourceDefinition);			
			return true;
		}		
		return false;
	}
    
    boolean handleOrderUncheckedReduce(APattern sourceDefinition){		
		if(isReduceAcceptable()){
			handleValidatingReshift(sourceDefinition);			
			return true;
		}		
		return false;
	}
	
	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler[] cph, 
							StructureHandler[] csh,		
							int size,
							int contentHandlerContentIndex,
							String startSystemId,
							int startLineNumber,
							int startColumnNumber,
							String startQName,
							int[] childIndex,
							APattern[] childDefinition,
							int[][] childItemId,
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
			this.childItemId = new int[correctChildIndexesSize][];
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
						
			this.childItemId[i] = new int[childItemId[i].length];
			System.arraycopy(childItemId[i], 0, this.childItemId[i], 0, childItemId[i].length);
			
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
			
			int[][] increasedCII = new int[correctChildIndexesSize][];
			for(int i = 0; i < lastCorrectChildIndexIndex; i++){
				if(childItemId[i] != null){
					increasedCII[i] = new int[childItemId[i].length];
					System.arraycopy(childItemId[i], 0, increasedCII[i], 0, childItemId[i].length);
				}
			}
			//System.arraycopy(childItemId, 0, increasedItemId, 0, lastCorrectChildIndexIndex);
			childItemId = increasedCII;
			
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
		
		childItemId[lastCorrectChildIndexIndex] = new int[1];
		childItemId[lastCorrectChildIndexIndex][0] = validationItemLocator.getItemId();
		
		childQName[lastCorrectChildIndexIndex] = new String[1];
		childQName[lastCorrectChildIndexIndex][0] = validationItemLocator.getItemIdentifier();
		
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
		
		int[] increasedCII = new int[newLength];
		System.arraycopy(childItemId[lastCorrectChildIndexIndex], 0, increasedCII, 0, oldLength);
		childItemId[lastCorrectChildIndexIndex] = increasedCII;
					
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
		
		childItemId[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getItemId();
		childQName[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getItemIdentifier();
		childSystemId[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getSystemId();
		childLineNumber[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getLineNumber();
		childColumnNumber[lastCorrectChildIndexIndex][newIndex] = validationItemLocator.getColumnNumber();
		childSourceDefinition[lastCorrectChildIndexIndex][newIndex] = sourceDefinition;
	}	
	private void removeLastCorrectChildIndex(){
		childDefinition[lastCorrectChildIndexIndex] = null;
		childItemId[lastCorrectChildIndexIndex] = null;
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
		//return "GroupMaximalReduceHandler "+hashCode()+" "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
		return "GroupMaximalReduceHandler  "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
	}
} 