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
	
	int stateId;
	
	APattern definition;
	int[] correspondingInputRecordIndex;
	int currentIndex;
	int startSize;
	int increaseSizeAmount;
	int maxSize;
		
	
	RuleHandlerRecycler recycler;
	
	StackConflictsHandler stackConflictsHandler;
	
	MessageWriter debugWriter;	
	
	public ParticleHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
			
		stateId = NO_OCCURRENCE;
		occurs =0;
		
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
	}
	
	public void recycle(){
		original = null;
		reset();
		recycler.recycle(this);		
	}
	
	public int functionalEquivalenceCode(){	
	    if(stateId == CardinalityHandler.NO_OCCURRENCE){
			return -1000;
		}else if(stateId == CardinalityHandler.SATISFIED_SIMPLE){
			return occurs*definition.hashCode();
		}else if(stateId == CardinalityHandler.SATISFIED_NEVER_SATURATED){
			return 0;
		}else if(stateId == CardinalityHandler.SATURATED){
			return occurs*definition.hashCode();
		}else if(stateId == CardinalityHandler.EXCESSIVE){
			return 0;
		}else if(stateId == CardinalityHandler.OPEN){
			return occurs*definition.hashCode();
		}else{
			throw new IllegalStateException();
		}
	}
	
	void reset(){
		for(int i = 0; i <= currentIndex; i++){
		    activeInputDescriptor.unregisterClientForRecord(correspondingInputRecordIndex[i]);
		}
		if(correspondingInputRecordIndex.length > maxSize){
		    correspondingInputRecordIndex = new int[startSize];
		}
		currentIndex = -1;
		
		occurs = 0;
		stateId = NO_OCCURRENCE;	
		
		if(stackConflictsHandler != null){
			stackConflictsHandler.close(this);
			stackConflictsHandler = null;
		}		
	}
	public ParticleHandler getCopy(ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
		ParticleHandler copy = definition.getParticleHandler(childEventHandler, errorCatcher);
		
		int[] copyCorrespondingInputRecordIndex = new int[correspondingInputRecordIndex.length];
		for(int i = 0; i <= currentIndex; i++){
		    copyCorrespondingInputRecordIndex[i] = correspondingInputRecordIndex[i];
		}
		
		copy.setState(occurs, 
					stateId,
					definition,
					copyCorrespondingInputRecordIndex,
					currentIndex,
					this);
		return copy;
	}
	public ParticleHandler getOriginal(){
		return original;
	}
	private void setState(int occurs, 
						int stateId,
						APattern definition,
						int[] correspondingInputRecordIndex,
						int currentIndex,
						ParticleHandler original){	
		this.occurs = occurs;
		
		this.stateId = stateId;
		this.definition = definition;
		this.correspondingInputRecordIndex = correspondingInputRecordIndex;
		this.currentIndex = currentIndex;		
		this.original = original;
		
		if(currentIndex >= 0)activeInputDescriptor.registerClientForRecord(correspondingInputRecordIndex, 0, currentIndex+1);
	}		
	
	public int getIndex(){
		return stateId;
	}
	
	public int getOccurs(){
		return occurs;
	}
	
	public APattern getRule(){
		return definition;
	}
			
	public boolean isSatisfied(){
	    return stateId == SATISFIED_NEVER_SATURATED 
	                || stateId == SATISFIED_SIMPLE 
	                || stateId == SATURATED 
	                || stateId == EXCESSIVE ;
	}
	
	public boolean isSaturated(){
	    return stateId == SATURATED
	                || stateId == EXCESSIVE ;
	}
	
	public void handleOccurrence(int inputRecordIndex){
		occurs++;		
		
		
		if(stateId == CardinalityHandler.NO_OCCURRENCE){
		    if(occursSatisfied == 0){			
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						unregisterWithStackConflictsHandler();
					}					 
					childEventHandler.optionalChildSatisfied();
					stateId = SATISFIED_NEVER_SATURATED;
				}else{
					maintainAllCorrespondingInputRecordIndex(inputRecordIndex);					
					if(occurs == occursSaturated){						
						childEventHandler.optionalChildSatisfied();
						childEventHandler.childSaturated();
						stateId = SATURATED;
					}else{						
						childEventHandler.optionalChildSatisfied();
						stateId = SATISFIED_SIMPLE;
					}				
				}
			}else if(occurs == occursSatisfied){	
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						unregisterWithStackConflictsHandler();
					}					 
					childEventHandler.requiredChildSatisfied();
					stateId = SATISFIED_NEVER_SATURATED;
				}else{
					maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
					if(occurs == occursSaturated){						
						childEventHandler.requiredChildSatisfied();
						childEventHandler.childSaturated();
						stateId = SATURATED;
					}else{						
						childEventHandler.requiredChildSatisfied();
						stateId = SATISFIED_SIMPLE;
					}				
				}
			}else{				
				childEventHandler.childOpen();
				stateId = OPEN;
			}		    
		}else if(stateId == CardinalityHandler.SATISFIED_SIMPLE){
		    maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
			if(occurs == occursSaturated){				
				childEventHandler.childSaturated();
				stateId = SATURATED;
			}		    
		}else if(stateId == CardinalityHandler.SATISFIED_NEVER_SATURATED){
		    
		    
		}else if(stateId == CardinalityHandler.SATURATED){
		    maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
			handleOccurrenceForSaturated();			    
		}else if(stateId == CardinalityHandler.EXCESSIVE){
		    maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
			handleOccurrenceForExcessive();
		}else if(stateId == CardinalityHandler.OPEN){
		    if(occurs == occursSatisfied){	
				if(occursSaturated == APattern.UNBOUNDED){
					if(stackConflictsHandler != null){
						unregisterWithStackConflictsHandler();
					}
					clearCorrespondingInputRecordIndex();					 
					childEventHandler.requiredChildSatisfied();
					stateId = SATISFIED_NEVER_SATURATED;
				}else{
					maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
					if(occurs == occursSaturated){						
						childEventHandler.requiredChildSatisfied();
						childEventHandler.childSaturated();
						stateId = SATURATED;
					}else{						
						childEventHandler.requiredChildSatisfied();
						stateId = SATISFIED_SIMPLE;
					}				
				}
			}		    
		}else{
			throw new IllegalStateException();
		}
	}
		
	public void handleOccurrence(StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		occurs++;
		
		if(stateId == CardinalityHandler.NO_OCCURRENCE){
		    registerWithStackConflictsHandler(stackConflictsHandler, resolver);
			handleOccurrenceForNoOccurrence();		    
		}else if(stateId == CardinalityHandler.SATISFIED_SIMPLE){
		    registerWithStackConflictsHandler(stackConflictsHandler, resolver);
			handleOccurrenceForSatisfiedSimple();		    
		}else if(stateId == CardinalityHandler.SATISFIED_NEVER_SATURATED){
		    
		    
		}else if(stateId == CardinalityHandler.SATURATED){
		    registerWithStackConflictsHandler(stackConflictsHandler, resolver);
			handleOccurrenceForSaturated();		    
		}else if(stateId == CardinalityHandler.EXCESSIVE){
		    registerWithStackConflictsHandler(stackConflictsHandler, resolver);
			handleOccurrenceForExcessive();		    
		}else if(stateId == CardinalityHandler.OPEN){
		    registerWithStackConflictsHandler(stackConflictsHandler, resolver);
			handleOccurrenceForOpen();		    
		}else{
			throw new IllegalStateException();
		}
	}
	
	public void handleOccurrence(StackConflictsHandler stackConflictsHandler){
		occurs++;
		
		if(stateId == CardinalityHandler.NO_OCCURRENCE){
		    registerWithStackConflictsHandler(stackConflictsHandler);
			handleOccurrenceForNoOccurrence();		    
		}else if(stateId == CardinalityHandler.SATISFIED_SIMPLE){
		    registerWithStackConflictsHandler(stackConflictsHandler);
			handleOccurrenceForSatisfiedSimple();		    
		}else if(stateId == CardinalityHandler.SATISFIED_NEVER_SATURATED){
		    
		    
		}else if(stateId == CardinalityHandler.SATURATED){
		    registerWithStackConflictsHandler(stackConflictsHandler);
			handleOccurrenceForSaturated();		    
		}else if(stateId == CardinalityHandler.EXCESSIVE){
		    registerWithStackConflictsHandler(stackConflictsHandler);
			handleOccurrenceForExcessive();		    
		}else if(stateId == CardinalityHandler.OPEN){
		    registerWithStackConflictsHandler(stackConflictsHandler);
			handleOccurrenceForOpen();		    
		}else{
			throw new IllegalStateException();
		}	
	}
	
	boolean alwaysValid(){
		return occursSatisfied == 0 && occursSaturated == APattern.UNBOUNDED;
	}
		
	void maintainAllCorrespondingInputRecordIndex(int lastIndex){
	    if(++currentIndex == correspondingInputRecordIndex.length){
	        int[] increased = new int[correspondingInputRecordIndex.length+increaseSizeAmount];
	        System.arraycopy(increased, 0, correspondingInputRecordIndex, 0, currentIndex);;
	        correspondingInputRecordIndex = increased;
	    }
	    correspondingInputRecordIndex[currentIndex] = lastIndex;
	
	    activeInputDescriptor.registerClientForRecord(lastIndex);
	}
		
	void maintainLastCorrespondingInputRecordIndex(int lastIndex){
	    clearCorrespondingInputRecordIndex();
	    currentIndex = 0;
	    correspondingInputRecordIndex[currentIndex] = lastIndex;
	
	    activeInputDescriptor.registerClientForRecord(lastIndex);
	}
	
	
	void clearCorrespondingInputRecordIndex(){
	    for(int i = 0; i <= currentIndex; i++){
		    activeInputDescriptor.unregisterClientForRecord(correspondingInputRecordIndex[i]);
		}
	    currentIndex = -1;
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
		
		if(stateId == CardinalityHandler.SATURATED){
		    if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(definition);
				return;
			}			
			errorCatcher.excessiveContent(context,
											activeInputDescriptor.getSystemId(startInputRecordIndex),
                                            activeInputDescriptor.getLineNumber(startInputRecordIndex),
                                            activeInputDescriptor.getColumnNumber(startInputRecordIndex),
											definition, 
											activeInputDescriptor.getItemId(correspondingInputRecordIndex, 0, currentIndex+1),
											activeInputDescriptor.getItemDescription(correspondingInputRecordIndex, 0, currentIndex+1),
                                            activeInputDescriptor.getSystemId(correspondingInputRecordIndex, 0, currentIndex+1),
                                            activeInputDescriptor.getLineNumber(correspondingInputRecordIndex, 0, currentIndex+1),
                                            activeInputDescriptor.getColumnNumber(correspondingInputRecordIndex, 0, currentIndex+1));		    
		}else if(stateId == CardinalityHandler.EXCESSIVE){
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
                                            activeInputDescriptor.getColumnNumber(correspondingInputRecordIndex[currentIndex]));		    
		}else{
			throw new IllegalStateException();
		}
	}
	
	public void reportMissing(Rule context, int startInputRecordIndex){			
		if(stateId == CardinalityHandler.NO_OCCURRENCE){
		    if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(context);
				return;
			}	
			errorCatcher.missingContent(context,  
									activeInputDescriptor.getSystemId(startInputRecordIndex),
									activeInputDescriptor.getLineNumber(startInputRecordIndex),
									activeInputDescriptor.getColumnNumber(startInputRecordIndex),
									definition, 
									occursSatisfied, 
									occurs, 
									activeInputDescriptor.getItemDescription(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getSystemId(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getLineNumber(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getColumnNumber(correspondingInputRecordIndex, 0, currentIndex+1));		    
		}else if(stateId == CardinalityHandler.SATISFIED_SIMPLE){
		    
		    
		}else if(stateId == CardinalityHandler.SATISFIED_NEVER_SATURATED){
		    
		    
		}else if(stateId == CardinalityHandler.SATURATED){
		    
		    
		}else if(stateId == CardinalityHandler.EXCESSIVE){
		    
		    
		}else if(stateId == CardinalityHandler.OPEN){
		    if(stackConflictsHandler != null){
				stackConflictsHandler.disqualify(context);
				return;
			}	
			errorCatcher.missingContent(context,  
									activeInputDescriptor.getSystemId(startInputRecordIndex),
									activeInputDescriptor.getLineNumber(startInputRecordIndex),
									activeInputDescriptor.getColumnNumber(startInputRecordIndex),
									definition, 
									occursSatisfied, 
									occurs,
									activeInputDescriptor.getItemDescription(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getSystemId(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getLineNumber(correspondingInputRecordIndex, 0, currentIndex+1),
									activeInputDescriptor.getColumnNumber(correspondingInputRecordIndex, 0, currentIndex+1));		    
		}else{
			throw new IllegalStateException();
		}
	}	
	
	
	void handleOccurrenceForNoOccurrence(){
	    if(occursSatisfied == 0){			
            if(occursSaturated == APattern.UNBOUNDED){
                if(stackConflictsHandler != null){
                    unregisterWithStackConflictsHandler();
                }					 
                childEventHandler.optionalChildSatisfied();
                stateId = SATISFIED_NEVER_SATURATED;
            }else{					
                if(occurs == occursSaturated){						
                    childEventHandler.optionalChildSatisfied();
                    childEventHandler.childSaturated();
                    stateId = SATURATED;
                }else{						
                    childEventHandler.optionalChildSatisfied();
                    stateId = SATISFIED_SIMPLE;
                }
            }
        }else if(occurs == occursSatisfied){	
            if(occursSaturated == APattern.UNBOUNDED){
                if(stackConflictsHandler != null){
                    unregisterWithStackConflictsHandler();
                }					 
                childEventHandler.requiredChildSatisfied();
                stateId = SATISFIED_NEVER_SATURATED;
            }else{
                if(occurs == occursSaturated){						
                    childEventHandler.requiredChildSatisfied();
                    childEventHandler.childSaturated();
                    stateId = SATURATED;
                }else{						
                    childEventHandler.requiredChildSatisfied();
                    stateId = SATISFIED_SIMPLE;
                }
            }
        }else{				
            childEventHandler.childOpen();
            stateId = OPEN;
        }
	}
	
	void handleOccurrenceForOpen(){
	    if(occurs == occursSatisfied){
            if(occursSaturated == APattern.UNBOUNDED){
                if(stackConflictsHandler != null){
                    unregisterWithStackConflictsHandler();
                }
                clearCorrespondingInputRecordIndex();					 
                childEventHandler.requiredChildSatisfied();
                stateId = SATISFIED_NEVER_SATURATED;
            }else{
                if(occurs == occursSaturated){						
                    childEventHandler.requiredChildSatisfied();
                    childEventHandler.childSaturated();
                    stateId = SATURATED;
                }else{						
                    childEventHandler.requiredChildSatisfied();
                    stateId = SATISFIED_SIMPLE;
                }
            }
        }
	}
	
	void handleOccurrenceForSatisfiedSimple(){
	    if(occurs == occursSaturated){				
            childEventHandler.childSaturated();
            stateId = SATURATED;
        }
	}
	
	void handleOccurrenceForSaturated(){
	    childEventHandler.childExcessive();
		stateId = EXCESSIVE;//state must be set after passing event, so that the excessive content error reporting can be done here, with all data
	}
	
	void handleOccurrenceForExcessive(){
	    childEventHandler.childExcessive();			
	}
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	
	public void setStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
		this.stackConflictsHandler = stackConflictsHandler;
	}
	
	public String toString(){
	    String s = null;
	    if(stateId == CardinalityHandler.NO_OCCURRENCE){
			s = "NO_OCCURRENCE";
		}else if(stateId == CardinalityHandler.SATISFIED_SIMPLE){
			s = "SATISFIED_SIMPLE";
		}else if(stateId == CardinalityHandler.SATISFIED_NEVER_SATURATED){
			s = "SATISFIED_NEVER_SATURATED";
		}else if(stateId == CardinalityHandler.SATURATED){
			s = "SATURATED";
		}else if(stateId == CardinalityHandler.EXCESSIVE){
			s = "EXCESSIVE";
		}else if(stateId == CardinalityHandler.OPEN){
			s = "OPEN";
		}else{
			throw new IllegalStateException();
		}
		return "ParticleHandler  "+definition+" occurs "+occurs+" state "+s+" "+stackConflictsHandler;
	}
}