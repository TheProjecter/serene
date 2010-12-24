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
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;


import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

public class ParticleHandler implements CardinalityHandler, Reusable{
 
	ParticleHandler original;
	
	private ErrorCatcher errorCatcher;
	
	private ChildEventHandler childEventHandler;
			
	/**
	* The minimum number of occurrences required for this particle.  
	*/
	private int occursSatisfied;	
	/**
	* The maximum number of occurrences allowed for this particle. 
	*/
	private int occursSaturated;
	/**
	* The actual number of occurrences recorded for this particle.
	*/	
	private int occurs;
	
	
	private APattern definition;
	private String[] qName;
	private String[] systemId;
	private int[] lineNumber;
	private int[] columnNumber;
	private int index;
	private int size;
	private int MAX_SIZE;//used to save memory, when reseting if size > MAX_SIZE, downsize 
	
	private int recordAllLocations;
	
	//private final int RECORD_ALL = 0;//start
	//private final int RECORD_LAST = 1;//excessive
	//private final int RECORD_NONE = 2;//satisfied never full
	
	private State state;
	
	private State noOccurrence;
	private State open;
	private State satisfiedSimple;
	private State satisfiedNeverSaturated;
	private State saturated;
	private State excessive;	
		
	
	
	private RuleHandlerRecycler recycler;
	
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
		
		index = -1;
		size = 0;
		MAX_SIZE = 10;		
	}
	
	public void init(RuleHandlerRecycler recycler){		
		this.recycler = recycler;
	} 
		
	public void init(ChildEventHandler childEventHandler, APattern definition, ErrorCatcher errorCatcher){		
		this.childEventHandler = childEventHandler;
		this.definition = definition;		
		this.occursSatisfied = definition.getMinOccurs();
		this.occursSaturated = definition.getMaxOccurs();
		this.errorCatcher = errorCatcher;
	}
	
	public void recycle(){
		original = null;
		reset();
		recycler.recycle(this);		
	}
	
	public int functionalEquivalenceCode(){		
		return state.functionalEquivalenceCode();
	}
	
	void reset(){
		if(size > MAX_SIZE){
			size = MAX_SIZE;
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
		
		String[] cQName = null;
		if(qName != null) cQName = Arrays.copyOf(qName, qName.length);
		String[] cSysId = null;
		if(systemId != null) cSysId = Arrays.copyOf(systemId, systemId.length);
		int[] cLinNb = null;
		if(lineNumber != null) cLinNb = Arrays.copyOf(lineNumber, lineNumber.length);
		int[] cColNb = null;
		if(columnNumber != null) cColNb = Arrays.copyOf(columnNumber, columnNumber.length);
		
		copy.setState(occurs, 
					state.getIndex(),
					definition,
					cQName,
					cSysId,
					cLinNb,
					cColNb,
					index,
					size);
		copy.setOriginal(this);
		return copy;
	}
	private void setOriginal(ParticleHandler original){
		this.original = original;
	}
	public ParticleHandler getOriginal(){
		return original;
	}
	private void setState(int occurs, 
						int stateIndex,
						APattern definition,
						String[] qName,
						String[] systemId,
						int[] lineNumber,
						int[] columnNumber,
						int index,
						int size){	
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
		this.qName = qName;
		this.systemId = systemId;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.index = index;
		this.size = size;
	
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
	
	public void handleOccurrence(String qN, String sI, int lN, int cN){
		occurs++;		
		state.handleOccurrence(qN, sI, lN, cN);		
		if(qN.equals("rng:ref") && lN == 5 )throw new IllegalStateException();
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
		
	
	void recordAllLocations(String qN, String sI, int lN, int cN){		
		if(size == 0){
			size = 1;
			index = 0;	
			qName = new String[size];			
			systemId = new String[size];			
			lineNumber = new int[size];
			columnNumber = new int[size];			
		}else if(++index == size){			
			String[] increasedQN = new String[++size];
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
		qName[index] = qN;
		systemId[index] = sI;
		lineNumber[index] = lN;
		columnNumber[index] = cN;
			
	}
	
	void recordLastLocation(String qN, String sI, int lN, int cN){		
		if(size > MAX_SIZE){
			size = MAX_SIZE;
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
		qName[index] = qN;
		systemId[index] = sI;
		lineNumber[index] = lN;
		columnNumber[index] = cN;
			
	}
	
	void clearLocations(){		
		if(size > MAX_SIZE){
			size = MAX_SIZE;
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
	public void reportExcessive(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){
		state.reportExcessive(context, startSystemId, startLineNumber, startColumnNumber);
	}
	
	public void reportMissing(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){		
		state.reportMissing(context, startSystemId, startLineNumber, startColumnNumber);
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
		
		
		public void handleOccurrence(String qN, String sI, int lN, int cN){
			// if(orderedParent()){
				// recordAllLocations(qN, sI, lN, cN);
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
					recordAllLocations(qN, sI, lN, cN);					
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
					recordAllLocations(qN, sI, lN, cN);
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
		
		
		public void reportExcessive(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){
			throw new IllegalStateException();
		}
		
		public void reportMissing(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){
			if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(context);
				return;
			}	
			errorCatcher.missingContent(context, 
									startSystemId, 
									startLineNumber, 
									startColumnNumber, 
									definition, 
									occursSatisfied, 
									occurs, 
									Arrays.copyOf(qName, (index+1)), 
									Arrays.copyOf(systemId, (index+1)), 
									Arrays.copyOf(lineNumber, (index+1)), 
									Arrays.copyOf(columnNumber, (index+1)));
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
					clearLocations();					 
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

		public void handleOccurrence(String qN, String sI, int lN, int cN){
			// if(orderedParent()){
				// recordAllLocations(qN, sI, lN, cN);
				// handleOccurrence();
				// return;
			// }
			if(occurs == occursSatisfied){	
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						//System.out.println("close particle 7 "+outerToString());
						unregisterWithStackConflictsHandler();
					}
					clearLocations();					 
					childEventHandler.requiredChildSatisfied();
					state = satisfiedNeverSaturated;
				}else{
					recordAllLocations(qN, sI, lN, cN);
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
		
		
		public void reportExcessive(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){
			throw new IllegalStateException();
		}
		
		public void reportMissing(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){
			if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(context);
				return;
			}	
			errorCatcher.missingContent(context, 
									startSystemId,
									startLineNumber, 
									startColumnNumber, 
									definition, 
									occursSatisfied, 
									occurs, 
									Arrays.copyOf(qName, (index+1)), 
									Arrays.copyOf(systemId, (index+1)), 
									Arrays.copyOf(lineNumber, (index+1)), 
									Arrays.copyOf(columnNumber, (index+1)));
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
		
		public void reportExcessive(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){			
			throw new IllegalStateException();
		}
		
		public void reportMissing(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){}
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
		
		public void handleOccurrence(String qN, String sI, int lN, int cN){
			recordAllLocations(qN, sI, lN, cN);
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
		
		public void handleOccurrence(String qN, String sI, int lN, int cN){
			// if(orderedParent()){
				// recordAllLocations(qN, sI, lN, cN);
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
		
		public void handleOccurrence(String qN, String sI, int lN, int cN){
			recordAllLocations(qN, sI, lN, cN);
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
		
		public void reportExcessive(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){
			if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(definition);
				return;
			}			
			errorCatcher.excessiveContent(context, 
											startSystemId, 
											startLineNumber,
											startColumnNumber,
											definition, 
											Arrays.copyOf(qName, (index+1)), 
											Arrays.copyOf(systemId, (index+1)), 
											Arrays.copyOf(lineNumber, (index+1)), 
											Arrays.copyOf(columnNumber, (index+1)));					
		}
		
		public void reportMissing(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){}
		
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
		
		public void handleOccurrence(String qN, String sI, int lN, int cN){
			// if(orderedParent()){
				// recordAllLocations(qN, sI, lN, cN);
				// handleOccurrence();
				// return;
			// }
			recordAllLocations(qN, sI, lN, cN);
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
		
		public void reportExcessive(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){
			if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(definition);
				return;
			}
			errorCatcher.excessiveContent(context,											
											definition, 
											qName[index],
											systemId[index],
											lineNumber[index],
											columnNumber[index]);			
		}
		
		public void reportMissing(Rule context, String startSystemId, int startLineNumber, int startColumnNumber){}
		
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