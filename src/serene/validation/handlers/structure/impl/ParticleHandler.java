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

import serene.Reusable;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;

import serene.validation.handlers.structure.CardinalityHandler;
import serene.validation.handlers.structure.ChildEventHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

import sereneWrite.MessageWriter;

public class ParticleHandler implements CardinalityHandler, Reusable{
 
	ParticleHandler original;
	
	ActiveInputDescriptor activeInputDescriptor;
	
	ErrorCatcher errorCatcher;
	
	ChildEventHandler childEventHandler;
			
	/**
	* The minimum number of occurrences required for this particle.  
	*/
	int occursSatisfied;	
	/**
	* The maximum number of occurrences allowed for this particle. 
	*/
	int occursSaturated;
	/**
	* The actual number of occurrences recorded for this particle.
	*/	
	int occurs;
	
	
	APattern definition;
	int[] correspondingInputRecordIndex;
	int currentIndex;
	int startSize;
	int increaseSizeAmount;
	int maxSize;
	
	/*int itemId[];
	String[] qName;
	String[] systemId;
	int[] lineNumber;
	int[] columnNumber;
	int index;
	int size;
	int MAX_SIZE;//used to save memory, when reseting if size > MAX_SIZE, downsize
	*/
	
	//int maintainAllCorrespondingInputRecordIndex;
	
	//final int RECORD_ALL = 0;//start
	//final int RECORD_LAST = 1;//excessive
	//final int RECORD_NONE = 2;//satisfied never full
	
	State state;
	
	State noOccurrence;
	State open;
	State satisfiedSimple;
	State satisfiedNeverSaturated;
	State saturated;
	State excessive;	
		
	
	
	RuleHandlerRecycler recycler;
	
	StackConflictsHandler stackConflictsHandler;
	
	MessageWriter debugWriter;	
	
	public ParticleHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		noOccurrence = new NoOccurrence();
		open = new Open();		
		saturated = new Saturated();
		excessive = new Excessive();		
		satisfiedSimple = new SatisfiedSimple();		
		satisfiedNeverSaturated = new SatisfiedNeverSaturated();
		state = noOccurrence;
				
		currentIndex = -1;
		startSize = 10;
		increaseSizeAmount = 10;
		maxSize = 20;
		correspondingInputRecordIndex = new int[startSize];
	}
	
	public void init(ActiveInputDescriptor activeInputDescriptor, RuleHandlerRecycler recycler){
        this.activeInputDescriptor = activeInputDescriptor;		
		this.recycler = recycler;
	}
		
	public void init(ChildEventHandler childEventHandler, APattern definition, ErrorCatcher errorCatcher){		
		this.childEventHandler = childEventHandler;
		this.definition = definition;		
		this.occursSatisfied = definition.getMinOccurs();
		this.occursSaturated = definition.getMaxOccurs();
		this.errorCatcher = errorCatcher;
		//System.out.println(hashCode()+" INIT so here it starts");
	}
	
	public void recycle(){
		original = null;
		reset();
		//System.out.println(hashCode()+" RECYCLE so here it stops");
		recycler.recycle(this);		
	}
	
	public int functionalEquivalenceCode(){		
		return state.functionalEquivalenceCode();
	}
	
	void reset(){
		/*if(size > MAX_SIZE){
			size = MAX_SIZE;
			itemId = new int[size];
			qName = new String[size];			
			systemId = new String[size];			
			lineNumber = new int[size];
			columnNumber = new int[size];
		}else{
			for(int i = 0; i <= index; i++){			
				qName[i] = null;
				systemId[i] = null;
			}
		}
		index = -1;*/
		
		//System.out.println(hashCode()+" START RESET UNREGISTERING");
		for(int i = 0; i <= currentIndex; i++){
		    activeInputDescriptor.unregisterClientForRecord(correspondingInputRecordIndex[i]);
		}
		//System.out.println(hashCode()+" END RESET UNREGISTERING");
		if(correspondingInputRecordIndex.length > maxSize){
		    correspondingInputRecordIndex = new int[startSize];
		}
		currentIndex = -1;
		
		occurs = 0;
		state = noOccurrence;
		
		if(stackConflictsHandler != null){
			//System.out.println("close particle 1 "+outerToString());
			stackConflictsHandler.close(this);
			stackConflictsHandler = null;
		}		
	}
	public ParticleHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
		ParticleHandler copy = definition.getParticleHandler(childEventHandler, errorCatcher);
		
		/*int[] cItemId = null;
		if(itemId != null) cItemId = Arrays.copyOf(itemId, itemId.length);
		String[] cQName = null;
		if(qName != null) cQName = Arrays.copyOf(qName, qName.length);
		String[] cSysId = null;
		if(systemId != null) cSysId = Arrays.copyOf(systemId, systemId.length);
		int[] cLinNb = null;
		if(lineNumber != null) cLinNb = Arrays.copyOf(lineNumber, lineNumber.length);
		int[] cColNb = null;
		if(columnNumber != null) cColNb = Arrays.copyOf(columnNumber, columnNumber.length);*/
		
		int[] copyCorrespondingInputRecordIndex = new int[correspondingInputRecordIndex.length];
		for(int i = 0; i <= currentIndex; i++){
		    copyCorrespondingInputRecordIndex[i] = correspondingInputRecordIndex[i];
		}
		
		copy.setState(occurs, 
					state.getIndex(),
					definition,
					/*cItemId,
					cQName,
					cSysId,
					cLinNb,
					cColNb,
					index,
					size*/
					copyCorrespondingInputRecordIndex,
					currentIndex,
					this);
		/*copy.setOriginal(this);*/
		return copy;
	}
	/*private void setOriginal(ParticleHandler original){
		this.original = original;
	}*/
	public ParticleHandler getOriginal(){
		return original;
	}
	private void setState(int occurs, 
						int stateIndex,
						APattern definition,
						/*int[] itemId,
						String[] qName,
						String[] systemId,
						int[] lineNumber,
						int[] columnNumber,
						int index,
						int size*/
						int[] correspondingInputRecordIndex,
						int currentIndex,
						ParticleHandler original){	
		this.occurs = occurs;
		if(stateIndex == CardinalityHandler.NO_OCCURRENCE){
			state = noOccurrence;
		}else if(stateIndex == CardinalityHandler.OPEN){
			state = open;
		}else if(stateIndex == CardinalityHandler.SATISFIED_NEVER_SATURATED){
			state = satisfiedNeverSaturated;
		}else if(stateIndex == CardinalityHandler.SATISFIED_SIMPLE){
			state = satisfiedSimple;
		}else if(stateIndex == CardinalityHandler.SATURATED){
			state = saturated;
		}else if(stateIndex == CardinalityHandler.EXCESSIVE){
			state = excessive;
		}else{
			throw new IllegalStateException();
		}
		
		this.definition = definition;
		/*this.itemId = itemId;
		this.qName = qName;
		this.systemId = systemId;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.index = index;
		this.size = size;*/
		this.correspondingInputRecordIndex = correspondingInputRecordIndex;
		this.currentIndex = currentIndex;		
		this.original = original;
		
		//System.out.println(hashCode()+" START COPY REGISTERING");
		if(currentIndex >= 0)activeInputDescriptor.registerClientForRecord(correspondingInputRecordIndex, 0, currentIndex+1);
		//System.out.println(hashCode()+" END COPY REGISTERING");
	}		
	
	public int getIndex(){
		return state.getIndex();
	}
	
	public int getOccurs(){
		return occurs;
	}
	
	public APattern getRule(){
		return definition;
	}
			
	public boolean isSatisfied(){
		return state.isSatisfied();
	}
	
	public boolean isSaturated(){
		return state.isSaturated();
	}
	
	public void handleOccurrence(int inputRecordIndex){
		occurs++;		
		state.handleOccurrence(inputRecordIndex);
	}
		
	public void handleOccurrence(StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		occurs++;
		state.handleOccurrence(stackConflictsHandler, resolver);
	}
	
	public void handleOccurrence(StackConflictsHandler stackConflictsHandler){
		occurs++;
		state.handleOccurrence(stackConflictsHandler);
	}
	
	boolean alwaysValid(){
		return occursSatisfied == 0 && occursSaturated == APattern.UNBOUNDED;
	}
		
	
	/*void recordAllLocations(int iti, String qN, String sI, int lN, int cN){		
		if(size == 0){
			size = 1;
			index = 0;			
			itemId = new int[size];
			qName = new String[size];			
			systemId = new String[size];			
			lineNumber = new int[size];
			columnNumber = new int[size];			
		}else if(++index == size){		    
            int[] increasedII = new int[++size];
            System.arraycopy(itemId, 0, increasedII, 0, index);
            itemId = increasedII;
 			
			String[] increasedQN = new String[size];
			System.arraycopy(qName, 0, increasedQN, 0, index);
			qName = increasedQN;
			
			String[] increasedSI = new String[size];
			System.arraycopy(systemId, 0, increasedSI, 0, index);
			systemId = increasedSI;
						
			int[] increasedLN = new int[size];
			System.arraycopy(lineNumber, 0, increasedLN, 0, index);
			lineNumber = increasedLN;
			
			int[] increasedCN = new int[size];
			System.arraycopy(columnNumber, 0, increasedCN, 0, index);
			columnNumber = increasedCN;
		}
		itemId[index] = iti;
		qName[index] = qN;
		systemId[index] = sI;
		lineNumber[index] = lN;
		columnNumber[index] = cN;		
	}*/
	
	void maintainAllCorrespondingInputRecordIndex(int lastIndex){
	    if(++currentIndex == correspondingInputRecordIndex.length){
	        int[] increased = new int[correspondingInputRecordIndex.length+increaseSizeAmount];
	        System.arraycopy(increased, 0, correspondingInputRecordIndex, 0, currentIndex);;
	        correspondingInputRecordIndex = increased;
	    }
	    correspondingInputRecordIndex[currentIndex] = lastIndex;
	    
	    //System.out.println(hashCode()+" START REGISTERING IN SEQUENCE");
	    activeInputDescriptor.registerClientForRecord(lastIndex);
	    //System.out.println(hashCode()+" END REGISTERING IN SEQUENCE");
	}
	
	/*void recordLastLocation(int iti, String qN, String sI, int lN, int cN){		
		if(size > MAX_SIZE){
			size = MAX_SIZE;
			itemId = new int[size];
			qName = new String[size];			
			systemId = new String[size];			
			lineNumber = new int[size];
			columnNumber = new int[size];
		}else{
			for(int i = 0; i <= index; i++){			
				qName[i] = null;
				systemId[i] = null;
			}
		}
		index = 0;
		itemId[index] = iti;
		qName[index] = qN;
		systemId[index] = sI;
		lineNumber[index] = lN;
		columnNumber[index] = cN;
			
	}*/
	
	void maintainLastCorrespondingInputRecordIndex(int lastIndex){
	    clearCorrespondingInputRecordIndex();
	    currentIndex = 0;
	    correspondingInputRecordIndex[currentIndex] = lastIndex;
	    
	    //System.out.println(hashCode()+" START REGISTERING LAST");
	    activeInputDescriptor.registerClientForRecord(lastIndex);
	    //System.out.println(hashCode()+" START REGISTERING LAST");
	}
	
	/*void clearLocations(){		
		if(size > MAX_SIZE){
			size = MAX_SIZE;
			itemId = new int[size];
			qName = new String[size];			
			systemId = new String[size];			
			lineNumber = new int[size];
			columnNumber = new int[size];
		}else{
			for(int i = 0; i <= index; i++){			
				qName[i] = null;
				systemId[i] = null;
			}
		}	
		index = -1;
	}*/
	
	void clearCorrespondingInputRecordIndex(){
	    //System.out.println(hashCode()+" START UNREGISTERING FOR CLEAR");
	    for(int i = 0; i <= currentIndex; i++){
		    activeInputDescriptor.unregisterClientForRecord(correspondingInputRecordIndex[i]);
		}
	    currentIndex = -1;
	    //System.out.println(hashCode()+" END UNREGISTERING FOR CLEAR");
	}
	
	void registerWithStackConflictsHandler(StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(stackConflictsHandler == null) throw new IllegalStateException();
		stackConflictsHandler.record(definition, this, resolver);
		this.stackConflictsHandler = stackConflictsHandler;
	}
	
	void registerWithStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
		if(this.stackConflictsHandler == null){
			stackConflictsHandler.record(definition, this);
			this.stackConflictsHandler = stackConflictsHandler;
		}
	}
	
	void unregisterWithStackConflictsHandler(){
		stackConflictsHandler.close(this);
		stackConflictsHandler = null;
	}
	public boolean expectsLastOccurrence(){
		boolean result = (occurs+1 == occursSaturated);
		return result;
	}
	
	public int getDistanceToSatisfaction(){
		return occursSatisfied-occurs;
	}
	public void reportExcessive(Rule context, int startInputRecordIndex){
		state.reportExcessive(context, startInputRecordIndex);
	}
	
	public void reportMissing(Rule context, int startInputRecordIndex){		
		state.reportMissing(context, startInputRecordIndex);
	}	
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	
	public void setStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
		this.stackConflictsHandler = stackConflictsHandler;
	}
	
	
	abstract class State implements CardinalityHandler{
		public void accept(RuleHandlerVisitor visitor){
			throw new IllegalStateException();
		}
		public Rule getRule(){
			throw new UnsupportedOperationException();
		}
		abstract String stateToString();
		public void setStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
			throw new IllegalStateException();
		}
	}	
	
	class NoOccurrence extends State{
		NoOccurrence(){}		
		public CardinalityHandler getOriginal(){
			throw new IllegalStateException();
		}
		public CardinalityHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
			throw new IllegalStateException();
		}
		public boolean isSatisfied(){
			return false;	
		}
		
		public boolean isSaturated(){
			return false;
		}
		void handleOccurrence(){			
			if(occursSatisfied == 0){			
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						//System.out.println("close particle 2 "+outerToString());
						unregisterWithStackConflictsHandler();
					}					 
					childEventHandler.optionalChildSatisfied();
					state = satisfiedNeverSaturated;
				}else{					
					if(occurs == occursSaturated){						
						childEventHandler.optionalChildSatisfied();
						childEventHandler.childSaturated();
						state = saturated;
					}else{						
						childEventHandler.optionalChildSatisfied();
						state = satisfiedSimple;
					}
				}
			}else if(occurs == occursSatisfied){	
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						//System.out.println("close particle 3 "+outerToString());
						unregisterWithStackConflictsHandler();
					}					 
					childEventHandler.requiredChildSatisfied();
					state = satisfiedNeverSaturated;
				}else{
					if(occurs == occursSaturated){						
						childEventHandler.requiredChildSatisfied();
						childEventHandler.childSaturated();
						state = saturated;
					}else{						
						childEventHandler.requiredChildSatisfied();
						state = satisfiedSimple;
					}
				}
			}else{				
				childEventHandler.childOpen();
				state = open;
			}
		}		
		
		
		public void handleOccurrence(int inputRecordIndex){
			// if(orderedParent()){
				// maintainAllCorrespondingInputRecordIndex(qN, sI, lN, cN);
				// handleOccurrence();
				// return;
			// }			
			if(occursSatisfied == 0){			
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						//System.out.println("close particle 4 "+outerToString());
						unregisterWithStackConflictsHandler();
					}					 
					childEventHandler.optionalChildSatisfied();
					state = satisfiedNeverSaturated;
				}else{
					maintainAllCorrespondingInputRecordIndex(inputRecordIndex);					
					if(occurs == occursSaturated){						
						childEventHandler.optionalChildSatisfied();
						childEventHandler.childSaturated();
						state = saturated;
					}else{						
						childEventHandler.optionalChildSatisfied();
						state = satisfiedSimple;
					}				
				}
			}else if(occurs == occursSatisfied){	
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						//System.out.println("close particle 5 "+outerToString());
						unregisterWithStackConflictsHandler();
					}					 
					childEventHandler.requiredChildSatisfied();
					state = satisfiedNeverSaturated;
				}else{
					maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
					if(occurs == occursSaturated){						
						childEventHandler.requiredChildSatisfied();
						childEventHandler.childSaturated();
						state = saturated;
					}else{						
						childEventHandler.requiredChildSatisfied();
						state = satisfiedSimple;
					}				
				}
			}else{				
				childEventHandler.childOpen();
				state = open;
			}	
		}
			
		public void handleOccurrence(StackConflictsHandler sch, InternalConflictResolver resolver){
			registerWithStackConflictsHandler(sch, resolver);
			handleOccurrence();
		}
		
		public void handleOccurrence(StackConflictsHandler sch){
			registerWithStackConflictsHandler(sch);
			handleOccurrence();
		}
		
		
		public void reportExcessive(Rule context, int startInputRecordIndex){
			throw new IllegalStateException();
		}
		
		public void reportMissing(Rule context, int startInputRecordIndex){
			if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(context);
				return;
			}	
			errorCatcher.missingContent(context, 
									/*startSystemId, 
									startLineNumber, 
									startColumnNumber,*/ 
									activeInputDescriptor.getSystemId(startInputRecordIndex),
									activeInputDescriptor.getLineNumber(startInputRecordIndex),
									activeInputDescriptor.getColumnNumber(startInputRecordIndex),
									definition, 
									occursSatisfied, 
									occurs, 
									activeInputDescriptor.getItemDescription(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getSystemId(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getLineNumber(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getColumnNumber(correspondingInputRecordIndex, 0, currentIndex+1)
									/*Arrays.copyOf(qName, (index+1)), 
									Arrays.copyOf(systemId, (index+1)), 
									Arrays.copyOf(lineNumber, (index+1)), 
									Arrays.copyOf(columnNumber, (index+1))*/);
		}
		
		public int getIndex(){
			return NO_OCCURRENCE;
		}
		
		public void recycle(){
			throw new UnsupportedOperationException();
		}
		
		public int functionalEquivalenceCode(){
			//throw new UnsupportedOperationException();
			return -1000;
		}
		
		String stateToString(){
			return "NO OCCURRENCE";
		}

		public String toString(){
			return "ParticleHandler state NO OCCURRENCE";
		}		
	}
	class Open extends State{		
		Open(){}
		public CardinalityHandler getOriginal(){
			throw new IllegalStateException();
		}
		public CardinalityHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
			throw new IllegalStateException();
		}
		public boolean isSatisfied(){
			return false;	
		}
		
		public boolean isSaturated(){
			return false;
		}
		void handleOccurrence(){
			if(occurs == occursSatisfied){
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						//System.out.println("close particle 6 "+outerToString());
						unregisterWithStackConflictsHandler();
					}
					clearCorrespondingInputRecordIndex();					 
					childEventHandler.requiredChildSatisfied();
					state = satisfiedNeverSaturated;
				}else{
					if(occurs == occursSaturated){						
						childEventHandler.requiredChildSatisfied();
						childEventHandler.childSaturated();
						state = saturated;
					}else{						
						childEventHandler.requiredChildSatisfied();
						state = satisfiedSimple;
					}
				}
			}
		}

		public void handleOccurrence(int inputRecordIndex){
			// if(orderedParent()){
				// maintainAllCorrespondingInputRecordIndex(qN, sI, lN, cN);
				// handleOccurrence();
				// return;
			// }
			if(occurs == occursSatisfied){	
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						//System.out.println("close particle 7 "+outerToString());
						unregisterWithStackConflictsHandler();
					}
					clearCorrespondingInputRecordIndex();					 
					childEventHandler.requiredChildSatisfied();
					state = satisfiedNeverSaturated;
				}else{
					maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
					if(occurs == occursSaturated){						
						childEventHandler.requiredChildSatisfied();
						childEventHandler.childSaturated();
						state = saturated;
					}else{						
						childEventHandler.requiredChildSatisfied();
						state = satisfiedSimple;
					}				
				}
			}
		}
			
		public void handleOccurrence(StackConflictsHandler sch, InternalConflictResolver resolver){
			registerWithStackConflictsHandler(sch, resolver);
			handleOccurrence();
		}
		
		public void handleOccurrence(StackConflictsHandler sch){
			registerWithStackConflictsHandler(sch);
			handleOccurrence();
		}
		
		
		public void reportExcessive(Rule context, int startInputRecordIndex){
			throw new IllegalStateException();
		}
		
		public void reportMissing(Rule context, int startInputRecordIndex){
			if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(context);
				return;
			}	
			errorCatcher.missingContent(context, 
									/*startSystemId,
									startLineNumber, 
									startColumnNumber,*/ 
									activeInputDescriptor.getSystemId(startInputRecordIndex),
									activeInputDescriptor.getLineNumber(startInputRecordIndex),
									activeInputDescriptor.getColumnNumber(startInputRecordIndex),
									definition, 
									occursSatisfied, 
									occurs,
									activeInputDescriptor.getItemDescription(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getSystemId(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getLineNumber(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getColumnNumber(correspondingInputRecordIndex, 0, currentIndex+1)
									/*Arrays.copyOf(qName, (index+1)), 
									Arrays.copyOf(systemId, (index+1)), 
									Arrays.copyOf(lineNumber, (index+1)), 
									Arrays.copyOf(columnNumber, (index+1))*/);
		}
		
		public int getIndex(){
			return OPEN;
		}
		
		public void recycle(){
			throw new UnsupportedOperationException();
		}
		public int functionalEquivalenceCode(){
			//throw new UnsupportedOperationException();
			return occurs*definition.hashCode();
		}
		
		
		
		String stateToString(){
			return "OPEN";
		}
		
		public String toString(){
			return "ParticleHandler state OPEN";
		}
	}	
	abstract class Satisfied extends State{
		Satisfied(){}	
		public boolean isSatisfied(){
			return true;	
		}
		
		public boolean isSaturated(){
			return false;
		}
		
		public void reportExcessive(Rule context, int startInputRecordIndex){			
			throw new IllegalStateException();
		}
		
		public void reportMissing(Rule context, int startInputRecordIndex){}
		public void recycle(){
			throw new UnsupportedOperationException();
		}		
	}
	
	class SatisfiedSimple extends Satisfied{
		SatisfiedSimple(){}
		public CardinalityHandler getOriginal(){
			throw new IllegalStateException();
		}
		public CardinalityHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
			throw new IllegalStateException();
		}
		void handleOccurrence(){
			if(occurs == occursSaturated){				
				childEventHandler.childSaturated();
				state = saturated;
			}
		}
		
		public void handleOccurrence(int inputRecordIndex){
			maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
			if(occurs == occursSaturated){				
				childEventHandler.childSaturated();
				state = saturated;
			}			
		}
			
		public void handleOccurrence(StackConflictsHandler sch, InternalConflictResolver resolver){
			registerWithStackConflictsHandler(sch, resolver);
			handleOccurrence();
		}
		
		public void handleOccurrence(StackConflictsHandler sch){
			registerWithStackConflictsHandler(sch);
			handleOccurrence();
		}
		
		
		public int getIndex(){
			return SATISFIED_SIMPLE;
		}
		public int functionalEquivalenceCode(){
			//throw new UnsupportedOperationException();
			return occurs*definition.hashCode();
		}
		
		String stateToString(){
			return "SATISFIED_SIMPLE";
		}
		
		public String toString(){
			return "ParticleHandler state SATISFIED SIMPLE";
		}
	}
	
	class SatisfiedNeverSaturated extends Satisfied{
		SatisfiedNeverSaturated(){}			
		public CardinalityHandler getOriginal(){
			throw new IllegalStateException();
		}
		public CardinalityHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
			throw new IllegalStateException();
		}
		void handleOccurrence(){}
		
		public void handleOccurrence(int inputRecordIndex){
			// if(orderedParent()){
				// maintainAllCorrespondingInputRecordIndex(qN, sI, lN, cN);
			// }
		}
			
		public void handleOccurrence(StackConflictsHandler sch, InternalConflictResolver resolver){
			//handleOccurrence();
		}
		
		public void handleOccurrence(StackConflictsHandler sch){
			/*sch.closeStructure(definition);*/
			//handleOccurrence();
		}
		public int getIndex(){
			return SATISFIED_NEVER_SATURATED;
		}
		public int functionalEquivalenceCode(){
			//return definition.hashCode()/2;
			return 0;
		}
		String stateToString(){
			return "SATISFIED_NEVER_SATURATED";
		}		
		
		public String toString(){
			return "ParticleHandler state SATISFIED NEVER SATURATED";
		}
	}
	
	class Saturated extends State{
		Saturated(){}
		public CardinalityHandler getOriginal(){
			throw new IllegalStateException();
		}
		public CardinalityHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
			throw new IllegalStateException();
		}
		public boolean isSatisfied(){
			return true;	
		}
		
		public boolean isSaturated(){
			return true;
		}
		void handleOccurrence(){				
			childEventHandler.childExcessive();
			state = excessive;//state must be set after passing event, so that the excessive content error reporting can be done here, with all data
		}
		
		public void handleOccurrence(int inputRecordIndex){
			maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
			handleOccurrence();			
		}
			
		public void handleOccurrence(StackConflictsHandler sch, InternalConflictResolver resolver){
			registerWithStackConflictsHandler(sch, resolver);
			handleOccurrence();
		}
		
		public void handleOccurrence(StackConflictsHandler sch){
			registerWithStackConflictsHandler(sch);
			handleOccurrence();
		}
		
		public void reportExcessive(Rule context, int startInputRecordIndex){
			if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(definition);
				return;
			}			
			errorCatcher.excessiveContent(context, 
											/*startSystemId, 
											startLineNumber,
											startColumnNumber,*/
											activeInputDescriptor.getSystemId(startInputRecordIndex),
                                            activeInputDescriptor.getLineNumber(startInputRecordIndex),
                                            activeInputDescriptor.getColumnNumber(startInputRecordIndex),
											definition, 
											activeInputDescriptor.getItemId(correspondingInputRecordIndex, 0, currentIndex+1),
											activeInputDescriptor.getItemDescription(correspondingInputRecordIndex, 0, currentIndex+1),
                                            activeInputDescriptor.getSystemId(correspondingInputRecordIndex, 0, currentIndex+1),
                                            activeInputDescriptor.getLineNumber(correspondingInputRecordIndex, 0, currentIndex+1),
                                            activeInputDescriptor.getColumnNumber(correspondingInputRecordIndex, 0, currentIndex+1)
											/*Arrays.copyOf(itemId, (index+1)),
											Arrays.copyOf(qName, (index+1)), 
											Arrays.copyOf(systemId, (index+1)), 
											Arrays.copyOf(lineNumber, (index+1)), 
											Arrays.copyOf(columnNumber, (index+1))*/);					
		}
		
		public void reportMissing(Rule context, int startInputRecordIndex){}
		
		public int getIndex(){
			return SATURATED;
		}
		public void recycle(){
			throw new UnsupportedOperationException();
		}
		public int functionalEquivalenceCode(){
			//throw new UnsupportedOperationException();
			return occurs*definition.hashCode();
		}
		
		
		
		String stateToString(){
			return "SATURATED";
		}
		
		public String toString(){
			return "ParticleHandler state SATURATED";
		}
	}
	
	class Excessive extends State{
		Excessive(){
			super();
		}
		public CardinalityHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
			throw new IllegalStateException();
		}
		public CardinalityHandler getOriginal(){
			throw new IllegalStateException();
		}
		public boolean isSatisfied(){
			return true;	
		}
		
		public boolean isSaturated(){
			return true;
		}
		void handleOccurrence(){				
			childEventHandler.childExcessive();			
		}
		
		public void handleOccurrence(int inputRecordIndex){
			// if(orderedParent()){
				// maintainAllCorrespondingInputRecordIndex(qN, sI, lN, cN);
				// handleOccurrence();
				// return;
			// }
			maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
			handleOccurrence();
		}
			
		public void handleOccurrence(StackConflictsHandler sch, InternalConflictResolver resolver){
			registerWithStackConflictsHandler(sch, resolver);
			handleOccurrence();
		}
		
		public void handleOccurrence(StackConflictsHandler sch){
			registerWithStackConflictsHandler(sch);
			handleOccurrence();
		}
		
		public void reportExcessive(Rule context, int startInputRecordIndex){
			if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(definition);
				return;
			}
			errorCatcher.excessiveContent(context,											
											definition, 
											activeInputDescriptor.getItemId(correspondingInputRecordIndex[currentIndex]),
											activeInputDescriptor.getItemDescription(correspondingInputRecordIndex[currentIndex]),
                                            activeInputDescriptor.getSystemId(correspondingInputRecordIndex[currentIndex]),
                                            activeInputDescriptor.getLineNumber(correspondingInputRecordIndex[currentIndex]),
                                            activeInputDescriptor.getColumnNumber(correspondingInputRecordIndex[currentIndex])
											/*itemId[index],
											qName[index],
											systemId[index],
											lineNumber[index],
											columnNumber[index]*/);			
		}
		
		public void reportMissing(Rule context, int startInputRecordIndex){}
		
		public int getIndex(){
			return EXCESSIVE;
		}
		public void recycle(){
			throw new UnsupportedOperationException();
		}
		
		public int functionalEquivalenceCode(){
			//return definition.hashCode()/2;
			return 0;
		}
		String stateToString(){
			return "EXCESSIVE";
		}
		
		public String toString(){
			return "ParticleHandler state EXCESSIVE";
		}
	}	

	public String outerToString(){
		//return "ParticleHandler "+ hashCode()+" "+definition+" occurs "+occurs+" state "+state.stateToString()+" "+stackConflictsHandler;
		return "ParticleHandler  "+definition+" occurs "+occurs+" state "+state.stateToString()+" "+stackConflictsHandler;
	}
	public String toString(){
		//return "ParticleHandler "+ hashCode()+" "+definition+" occurs "+occurs+" state "+state.stateToString()+" "+stackConflictsHandler;
		return "ParticleHandler  "+definition+" occurs "+occurs+" state "+state.stateToString()+" "+stackConflictsHandler;
	}
}