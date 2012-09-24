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


import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SChoicePattern;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.match.MatchPath;

// The reductions are done at childSaturated and at change(in the getHandlerForPattern).
// Choice can never have excessive content. In case too many occurrences of 
// one of the children happen, then the error will be reported as too many
// occurrence of choice by the parent.
public class ChoiceHandler extends UniqueChildPatternHandler{		
	SRule currentChild;
	
	ChoiceHandler original;
	SChoicePattern rule;
	
	ChoiceHandler(){
		super();			
	}	
	
	
	//It seems that the choice is only sensitive to interleave if it's more than one.
	//This means it can be reduced every time the input changes from one name to another
	//It only needs to remember which is the current child particleHandler, or even better it's
	//childId	
	void init(SChoicePattern choice, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		this.rule = choice;	
		this.parent = parent;		
		this.stackHandler = stackHandler;
		this.errorCatcher = errorCatcher;			
		//childParticleHandler = null;
		//childStructureHandler = null;
	}
	
	public void recycle(){
		original = null;
		setEmptyState();
		pool.recycle(this);
	}	
		
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	//StructureHandler getAncestorOrSelfHandler(Rule rule); super
    public boolean handleDeactivation(){
        if(!parent.handleDeactivation()){
            stackHandler.endSubtreeValidation(this);
        }
        return true;
	}
	public StructureHandler getChildHandler(SRule child, MatchPath path){		
		//if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		SRule[] children = rule.getChildren();
		boolean missing = true;
		for(int i = 0; i < children.length; i++){
		    if(children[i] == child)missing = false;
		}
		if(missing)  throw new IllegalArgumentException();
		
		if(currentChild != null && currentChild != child){
			stackHandler.reset(this);
			return null;			
		}		
		if(childStructureHandler == null){
			childStructureHandler = pool.getStructureHandler(child, path, errorCatcher, this, stackHandler);
			currentChild = child;
			return childStructureHandler;
		}		
		return childStructureHandler;
	}	
	// APattern getRule() super
	public boolean handleChildShiftAndOrder(SPattern pattern, int expectedOrderHandlingCount){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}		
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(inputStackDescriptor.getCurrentItemInputRecordIndex(), pattern);
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(SPattern pattern, int startInputRecordIndex){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int count, SPattern pattern, int startInputRecordIndex){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int MIN, int MAX, SPattern pattern, int startInputRecordIndex){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(startInputRecordIndex, pattern);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	
	
	// TODO handleParticleShift()	
	public boolean handleChildShift(SPattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}		
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern, resolver)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(pattern, stackConflictsHandler, resolver);
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	//reduce
	public boolean handleChildShift(SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int count, SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	public boolean handleChildShift(int MIN, int MAX, SPattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		if(currentChild != null && currentChild != pattern){
			stackHandler.validatingReshift(this, pattern);
			return false;			
		}else{
			currentChild = pattern;
		}
		handleParticleShift(pattern, stackConflictsHandler);		
		boolean result = !handleStateSaturationReduce();
		return result;
	}
	
	// void handleValidatingReduce() super
	public int functionalEquivalenceCode(){
	    int fec = rule.hashCode();
		if(childParticleHandler != null)fec += childParticleHandler.functionalEquivalenceCode();		
		if(childStructureHandler != null)fec += childStructureHandler.functionalEquivalenceCode();
		
		if(currentChild != null){
			if(childStructureHandler != null)return fec + childStructureHandler.getRule().hashCode();
			else return fec + childParticleHandler.getRule().hashCode();
		}		
		return fec;
	}
	public ChoiceHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		ChoiceHandler copy = pool.getCopy(this, rule, errorCatcher, parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler,					
					contentIndex,
					startInputRecordIndex,
					isStartSet,
					currentChild);
		copy.setOriginal(this);
		return copy; 		
	}
	public ChoiceHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		ChoiceHandler copy = pool.getCopy(this, rule, errorCatcher, parent, stackHandler);
		copy.setState(stackHandler, 
					errorCatcher, 
					childParticleHandler, 
					childStructureHandler,					
					contentIndex,
					startInputRecordIndex,
					isStartSet,
					currentChild);
		copy.setOriginal(this);
		return copy;
	}	
	private void setOriginal(ChoiceHandler original){
		this.original = original;
	}
	public ChoiceHandler getOriginal(){
		return original;
	}
	//String stackToString(); super
	//End StructureHandler------------------------------------------------------------
	
	

	//Start ValidationHandler---------------------------------------------------------		
	void handleParticleShift(int inputRecordIndex, SPattern childPattern){				
		setCurrentChildParticleHandler(childPattern);
		currentChildParticleHandler.handleOccurrence(inputRecordIndex);
	}	
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
	
			
	//Start InnerPattern------------------------------------------------------------------	
	// boolean handleStateSaturationReduce() super	
	// boolean isReduceAllowed() super
	// boolean isReduceRequired() super
	// boolean isReduceAcceptable() super
	// void setCurrentChildParticleHandler(APattern childPattern)
	// void closeContent() super
	// void closeContentParticle(SPattern childPattern) super
	//for Reusable implementation
	void setEmptyState(){
		super.setEmptyState();
		currentChild = null;
	}
	//End InnerPattern------------------------------------------------------------------
		
	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler childParticleHandler, 
							StructureHandler childStructureHandler,							
							int contentIndex,
							int startInputRecordIndex,
							boolean isStartSet,
							SRule currentChild){
		if(childParticleHandler != null)this.childParticleHandler = childParticleHandler.getCopy(this, errorCatcher);
		if(childStructureHandler != null)this.childStructureHandler = childStructureHandler.getCopy(this, stackHandler, errorCatcher);
		
		this.contentIndex = contentIndex;
		
		if(this.isStartSet){
            activeInputDescriptor.unregisterClientForRecord(this.startInputRecordIndex, this);
        }
		this.startInputRecordIndex = startInputRecordIndex;
		this.isStartSet = isStartSet;
		if(isStartSet){		    
		    activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		}
		
		this.currentChild = currentChild;	
	}	
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	
	public String toString(){
		//return "ChoiceHandler "+hashCode()+" "+rule.toString()+" contentHandler "+contentHandler.toString();
		return "ChoiceHandler "+rule.toString()+" contentIndex="+contentIndex;
	}
	
	
	//**************************************************************************
    // START form UniqueChildStructureHandler
    /*public StructureHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		else return parent.getAncestorOrSelfHandler(rule);
	}*/
	
	/*public StructureHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler == null) childStructureHandler = child.getStructureHandler(errorCatcher, this, stackHandler);
		return childStructureHandler;
	}*/	
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	public void handleValidatingReduce(){
		// - it must have some child or else it would not exist
		// - reduce first any childStructureHandler present 
		//		-> updates the childParticleHandler
		//				-> may trigger saturated state reduce >> childParticleHandler will be null 
		// - afterwards if the childParticleHandler is present(there was no saturated state reduce)
		//		-> if reduce is allowed (state is satisfied) reduce this
		//		-> else report missing content and than reduce
		if(childStructureHandler != null){
			childStructureHandler.handleValidatingReduce();
		}
		if(childParticleHandler != null){
			if(!isReduceAcceptable()){
				childParticleHandler.reportMissing(rule, startInputRecordIndex);
			}
			handleReduce();
		}
	}
	public SPattern getRule(){
		return rule;
	}
	
	/*public void handleValidatingReduce(){
		// - it must have some child or else it would not exist
		// - reduce first any childStructureHandler present 
		//		-> updates the childParticleHandler
		//				-> may trigger saturated state reduce >> childParticleHandler will be null 
		// - afterwards if the childParticleHandler is present(there was no saturated state reduce)
		//		-> if reduce is allowed (state is satisfied) reduce this
		//		-> else report missing content and than reduce
		if(childStructureHandler != null){
			childStructureHandler.handleValidatingReduce();
		}
		if(childParticleHandler != null){
			if(!isReduceAcceptable()){
				childParticleHandler.reportMissing(rule, startInputRecordIndex);
			}
			handleReduce();
		}
	}*/
	
	public boolean acceptsMDescendentReduce(SPattern p){
		// TODO should you be more defensive and check if the p is the actual 
		// pattern from the particle handler?
		// First thought: here is not really the case, there can be only one child.
				
		// if there is no corresponding ParticlHandler, get one
		// if the ParticleHandler does not expect the last occurrence
		//				return true
		// else determine if this handler is satisfied or if it could be 
		// satisfied by the shifting of p // here it's enough to see that the 
		// particleHandler's distanceToSatisfaction is smaller then or equal to 1
		// 		yes return parent.acceptsMDescendentReduce(rule)
		//		no return false
		
		// before every return recycle the ParticleHandler if it was aquired 
		// only for this method
		
		boolean transitoryParticle = false;
		if(childParticleHandler == null) {
			childParticleHandler = pool.getParticleHandler(rule, this, errorCatcher);
			transitoryParticle = true;
		}
		if(!childParticleHandler.expectsLastOccurrence()){
			if(transitoryParticle){
				childParticleHandler.recycle();
				childParticleHandler = null;
			}
			return true;
		}else{
			int distanceToSatisfaction = childParticleHandler.getDistanceToSatisfaction();
			if(transitoryParticle){
				childParticleHandler.recycle();
				childParticleHandler = null;
			}
			/*if(isSatisfied()) return parent.acceptsMDescendentReduce(rule);
			else*/ if(distanceToSatisfaction <= 1 )return parent.acceptsMDescendentReduce(rule);
			else return false;
		}		
	}
	/*public int functionalEquivalenceCode(){
		int fec = rule.hashCode();
		if(childParticleHandler != null)fec += childParticleHandler.functionalEquivalenceCode();		
		if(childStructureHandler != null)fec += childStructureHandler.functionalEquivalenceCode();
		return fec;
	}*/
	public void closeContentStructure(SPattern childPattern){
		//if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
		
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern child, SPattern sourceDefinition, InternalConflictResolver resolver){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
	}
	
	public void setConflict(int index, SRule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(path[index] != rule) throw new IllegalStateException();
		if(this.stackConflictsHandler == null) this.stackConflictsHandler = stackConflictsHandler;
		boolean newRecord = stackConflictsHandler.record(rule, this, resolver);
		if(newRecord && ++index != path.length)parent.setConflict(index, path, stackConflictsHandler, resolver);
	}
	
	void setCurrentChildParticleHandler(SPattern childPattern){	
		//if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		
		boolean missing = true;
		for(int i = 0; i < rule.getChildrenCount(); i++){
		    if(rule.getChild(i) == childPattern)missing = false;
		}
		if(missing)  throw new IllegalArgumentException();
				
		
		if(childParticleHandler == null){
			childParticleHandler = pool.getParticleHandler(childPattern, this, errorCatcher);
		}
		currentChildParticleHandler = childParticleHandler;
	}
	
	void closeContentParticle(SPattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
	}	
    // END form UniqueChildStructureHandler
    //**************************************************************************
} 