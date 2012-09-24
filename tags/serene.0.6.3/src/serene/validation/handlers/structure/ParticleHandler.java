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

package serene.validation.handlers.structure;

import java.util.Arrays;

import serene.Reusable;

import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;

import serene.validation.handlers.structure.CardinalityHandler;
import serene.validation.handlers.structure.ChildEventHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

public class ParticleHandler extends RuleHandler implements CardinalityHandler, Reusable{
 
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
	
	SPattern definition;
	int[] correspondingInputRecordIndex;
	int currentIndex;
	int startSize;
	int increaseSizeAmount;
	int maxSize;
		
	
	ValidatorRuleHandlerPool pool;
	
	StackConflictsHandler stackConflictsHandler;
	
	public ParticleHandler(){			
		stateId = NO_OCCURRENCE;
		occurs =0;
		
		currentIndex = -1;
		startSize = 10;
		increaseSizeAmount = 10;
		maxSize = 20;
		correspondingInputRecordIndex = new int[startSize];
	}
		
	public void init(ActiveInputDescriptor activeInputDescriptor, ValidatorRuleHandlerPool pool){
        this.activeInputDescriptor = activeInputDescriptor;		
		this.pool = pool;
	}
		
	public void init(ChildEventHandler childEventHandler, SPattern definition, ErrorCatcher errorCatcher){		
		this.childEventHandler = childEventHandler;
		this.definition = definition;		
		this.occursSatisfied = definition.getMinOccurs();
		this.occursSaturated = definition.getMaxOccurs();
		this.errorCatcher = errorCatcher;
	}
	
	public void recycle(){
		original = null;
		reset();
		pool.recycle(this);		
	}
	
	public SPattern getRule(){
	    return definition;
	}
	
	public int functionalEquivalenceCode(){
		switch(stateId){
		    case NO_OCCURRENCE :
		        return -1000;
		    case SATISFIED_SIMPLE :
		        return occurs*definition.hashCode();
		    case SATISFIED_NEVER_SATURATED :
		        return 0;
		    case SATURATED :
		        return occurs*definition.hashCode();
		    case EXCESSIVE :
		        return 0;
		    case OPEN :
		        return occurs*definition.hashCode();
		    default:
		        throw new IllegalStateException();
		}
	}
	
	void reset(){
		for(int i = 0; i <= currentIndex; i++){
		    activeInputDescriptor.unregisterClientForRecord(correspondingInputRecordIndex[i], this);
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
		ParticleHandler copy = pool.getCopy(this, childEventHandler, errorCatcher);
		
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
						SPattern definition,
						int[] correspondingInputRecordIndex,
						int currentIndex,
						ParticleHandler original){	
		this.occurs = occurs;
		
		this.stateId = stateId;
		this.definition = definition;
		this.correspondingInputRecordIndex = correspondingInputRecordIndex;
		this.currentIndex = currentIndex;		
		this.original = original;
		
		if(currentIndex >= 0)activeInputDescriptor.registerClientForRecord(correspondingInputRecordIndex, 0, currentIndex+1, this);
	}		
	
	public int getIndex(){
		return stateId;
	}
	
	public int getOccurs(){
		return occurs;
	}
	
	public SPattern getPattern(){
		return definition;
	}
			
	public boolean isSatisfied(){
	    switch(stateId){
		    case NO_OCCURRENCE :
		        return false;
		    case SATISFIED_SIMPLE :
		        return true;
		    case SATISFIED_NEVER_SATURATED :
		        return true;
		    case SATURATED :
		        return true;
		    case EXCESSIVE :
		        return true;
		    case OPEN :
		        return false;
		    default:
		        throw new IllegalStateException();
		}
	}
	
	public boolean isSaturated(){
	    switch(stateId){
		    case NO_OCCURRENCE :
		        return false;
		    case SATISFIED_SIMPLE :
		        return false;
		    case SATISFIED_NEVER_SATURATED :
		        return false;
		    case SATURATED :
		        return true;
		    case EXCESSIVE :
		        return true;
		    case OPEN :
		        return false;
		    default:
		        throw new IllegalStateException();
		}
	}
	
	public void handleOccurrence(int inputRecordIndex){
		occurs++;		
		switch(stateId){
		    case NO_OCCURRENCE :
		        if(occursSatisfied == 0){			
                    if(occursSaturated == SPattern.UNBOUNDED){
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
                    if(occursSaturated == SPattern.UNBOUNDED){
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
		        break;
		    case SATISFIED_SIMPLE :
		        maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
                if(occurs == occursSaturated){				
                    childEventHandler.childSaturated();
                    stateId = SATURATED;
                }
		        break;
		    case SATISFIED_NEVER_SATURATED :
		        break;
		    case SATURATED :
		        maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
		        handleOccurrenceForSaturated();			
		        break;
		    case EXCESSIVE :
		        maintainAllCorrespondingInputRecordIndex(inputRecordIndex);
		        handleOccurrenceForExcessive();
		        break;
		    case OPEN :
		        if(occurs == occursSatisfied){	
                    if(occursSaturated == SPattern.UNBOUNDED){
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
		        break;
		    default:
		        throw new IllegalStateException();
		}
	}
		
	public void handleOccurrence(StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		occurs++;
				
		switch(stateId){
		    case NO_OCCURRENCE :		      
		        registerWithStackConflictsHandler(stackConflictsHandler, resolver);
		        handleOccurrenceForNoOccurrence();
		        break;
		    case SATISFIED_SIMPLE :
		        registerWithStackConflictsHandler(stackConflictsHandler, resolver);
		        handleOccurrenceForSatisfiedSimple();
		        break;
		    case SATISFIED_NEVER_SATURATED :
		        break;
		    case SATURATED :
		        registerWithStackConflictsHandler(stackConflictsHandler, resolver);
		        handleOccurrenceForSaturated();
		        break;
		    case EXCESSIVE :
		        registerWithStackConflictsHandler(stackConflictsHandler, resolver);
		        handleOccurrenceForExcessive();
		        break;
		    case OPEN :
		        registerWithStackConflictsHandler(stackConflictsHandler, resolver);
		        handleOccurrenceForOpen();
		        break;
		    default:
		        throw new IllegalStateException();
		}
	}
	
	public void handleOccurrence(StackConflictsHandler stackConflictsHandler){
		occurs++;
		
		switch(stateId){
		    case NO_OCCURRENCE :		      
		        registerWithStackConflictsHandler(stackConflictsHandler);
		        handleOccurrenceForNoOccurrence();
		        break;
		    case SATISFIED_SIMPLE :
		        registerWithStackConflictsHandler(stackConflictsHandler);
		        handleOccurrenceForSatisfiedSimple();
		        break;
		    case SATISFIED_NEVER_SATURATED :
		        break;
		    case SATURATED :
		        registerWithStackConflictsHandler(stackConflictsHandler);
		        handleOccurrenceForSaturated();
		        break;
		    case EXCESSIVE :
		        registerWithStackConflictsHandler(stackConflictsHandler);
		        handleOccurrenceForExcessive();
		        break;
		    case OPEN :
		        registerWithStackConflictsHandler(stackConflictsHandler);
		        handleOccurrenceForOpen();
		        break;
		    default:
		        throw new IllegalStateException();
		}	
	}
	
	boolean alwaysValid(){
		return occursSatisfied == 0 && occursSaturated == SPattern.UNBOUNDED;
	}
		
	void maintainAllCorrespondingInputRecordIndex(int lastIndex){
	    if(++currentIndex == correspondingInputRecordIndex.length){
	        int[] increased = new int[correspondingInputRecordIndex.length+increaseSizeAmount];
	        System.arraycopy(increased, 0, correspondingInputRecordIndex, 0, currentIndex);;
	        correspondingInputRecordIndex = increased;
	    }
	    correspondingInputRecordIndex[currentIndex] = lastIndex;
	
	    activeInputDescriptor.registerClientForRecord(lastIndex, this);
	}
		
	void maintainLastCorrespondingInputRecordIndex(int lastIndex){
	    clearCorrespondingInputRecordIndex();
	    currentIndex = 0;
	    correspondingInputRecordIndex[currentIndex] = lastIndex;
	
	    activeInputDescriptor.registerClientForRecord(lastIndex, this);
	}
	
	
	void clearCorrespondingInputRecordIndex(){
	    for(int i = 0; i <= currentIndex; i++){
		    activeInputDescriptor.unregisterClientForRecord(correspondingInputRecordIndex[i], this);
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
	public void reportExcessive(SRule context, int startInputRecordIndex){
		
		switch(stateId){		    
		    case SATURATED :
		        if(stackConflictsHandler != null){
                    stackConflictsHandler.disqualify(definition);
                    return;
                }			
                errorCatcher.excessiveContent(context,
                                                startInputRecordIndex,
                                                definition, 
                                                Arrays.copyOfRange(correspondingInputRecordIndex, 0, currentIndex+1));
		        break;
		    case EXCESSIVE :
		        if(stackConflictsHandler != null){
                    stackConflictsHandler.disqualify(definition);
                    return;
                }
                errorCatcher.excessiveContent(context,											
                                                definition, 
                                                correspondingInputRecordIndex[currentIndex]);
		        break;
		    case NO_OCCURRENCE :		        	
		        throw new IllegalStateException();
		    case SATISFIED_SIMPLE :
		        throw new IllegalStateException();
		    case SATISFIED_NEVER_SATURATED :
		        throw new IllegalStateException();
		    case OPEN :
		        throw new IllegalStateException();
		    default:
		        throw new IllegalStateException();
		}		
	}
	
	public void reportMissing(SRule context, int startInputRecordIndex){	    
		switch(stateId){
		    case NO_OCCURRENCE :		        	
		        if(stackConflictsHandler != null){
                    stackConflictsHandler.disqualify(context);
                    return;
                }	
                errorCatcher.missingContent(context,  
                                        startInputRecordIndex,
                                        definition, 
                                        occursSatisfied, 
                                        occurs, 
                                        Arrays.copyOfRange(correspondingInputRecordIndex, 0, currentIndex+1));
		        break;
		    case OPEN :
		        if(stackConflictsHandler != null){
                    stackConflictsHandler.disqualify(context);
                    return;
                }	
                errorCatcher.missingContent(context,  
                                        startInputRecordIndex,
                                        definition, 
                                        occursSatisfied, 
                                        occurs,
                                        Arrays.copyOfRange(correspondingInputRecordIndex, 0, currentIndex+1));
		        break;
		    case SATISFIED_SIMPLE :
		        //throw new IllegalStateException();
		        break;
		    case SATISFIED_NEVER_SATURATED :
		        //throw new IllegalStateException();
		        break;
		    case SATURATED :
		        //throw new IllegalStateException();
		        break;
		    case EXCESSIVE :
		        //throw new IllegalStateException();
		        break;
		    default:
		        throw new IllegalStateException();
		}		
	}	
	
	
	void handleOccurrenceForNoOccurrence(){
	    if(occursSatisfied == 0){			
            if(occursSaturated == SPattern.UNBOUNDED){
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
            if(occursSaturated == SPattern.UNBOUNDED){
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
            if(occursSaturated == SPattern.UNBOUNDED){
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
	    
	    switch(stateId){
		    case NO_OCCURRENCE :
		        s = "NO_OCCURRENCE";
		        break;
		    case SATISFIED_SIMPLE :
		        s = "SATISFIED_SIMPLE";
		        break;
		    case SATISFIED_NEVER_SATURATED :
		        s = "SATISFIED_NEVER_SATURATED";
		        break;
		    case SATURATED :
		        s = "SATURATED";
		        break;
		    case EXCESSIVE :
		        s = "EXCESSIVE";
		        break;
		    case OPEN :
		        s = "OPEN";
		        break;
		    default:
		        throw new IllegalStateException();
		}		
		return "ParticleHandler  "+definition+" occurs "+occurs+" state "+s+" "+stackConflictsHandler;
	}
}