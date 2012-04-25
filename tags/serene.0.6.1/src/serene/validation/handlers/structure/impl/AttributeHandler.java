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


import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

public class AttributeHandler extends StructureValidationHandler{
	AAttribute rule;
	
	ParticleHandler childParticleHandler;
	StructureHandler childStructureHandler;
	
	AttributeHandler original;
	AttributeHandler(){
		super();
	}
		
	void init(AAttribute attribute, ErrorCatcher errorCatcher, StackHandler stackHandler){
		this.rule = attribute;
		this.stackHandler = stackHandler;
		this.errorCatcher = errorCatcher;
	}
		
	public void recycle(){
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
		contentIndex = NO_CONTENT;	
		
		original = null;
		
		if(isStartSet){
		    activeInputDescriptor.unregisterClientForRecord(startInputRecordIndex, this);
		    isStartSet = false;
		    startInputRecordIndex = -1;
		}
		
		recycler.recycle(this);
	}
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	public StructureValidationHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		return null;
	}
	public void deactivate(){
		throw new IllegalStateException();	
	}    
    public boolean handleDeactivation(){
		return false;	
	}
	public StructureHandler getChildHandler(Rule child){
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler == null) childStructureHandler = child.getStructureHandler(errorCatcher, this, stackHandler);
		return childStructureHandler;
	}	
	public AAttribute getRule(){
		return rule;
	}
	
	public boolean handleChildShiftAndOrder(APattern pattern, int expectedOrderHandlingCount){
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(inputStackDescriptor.getCurrentItemInputRecordIndex(), pattern);		
		//handleStateSaturationReduce();
		//throw new IllegalStateException();
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
		handleParticleShift(startInputRecordIndex, pattern);		
		//handleStateSaturationReduce();
		return true;
	}
	
	
	// TODO handleParticleShift()	
	public boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern, resolver)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		handleParticleShift(pattern, stackConflictsHandler, resolver);		
		//handleStateSaturationReduce();
		//throw new IllegalStateException();
		return true;
	}
	
	//reduce
	public boolean handleChildShift(APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		handleParticleShift(pattern, stackConflictsHandler);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		handleParticleShift(pattern, stackConflictsHandler);		
		//handleStateSaturationReduce();
		return true;
	}
	public boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex, StackConflictsHandler stackConflictsHandler){
		handleParticleShift(pattern, stackConflictsHandler);		
		//handleStateSaturationReduce();
		return true;
	}
	
	
	// TODO 
	// Review this, the functionality should be moved in a method that does not
	// have "reduce" in the name since this handler does not really get reduced.
	public void handleValidatingReduce(){
		if(!isStartSet)setStart();
		
		// - reduce first any childStructureHandler present 
		//		-> updates the childParticleHandler		 
		// - afterwards 
		// - if the childParticleHandler is present		
		//		-> if contentHandler is not satisfied report missing content by childParticleHandler
		// - else if child was required report missing content here 
		if(childStructureHandler != null){
			childStructureHandler.handleValidatingReduce();
		}
		if(childParticleHandler != null){
			if(isSatisfied()){
				childParticleHandler.reportMissing(rule, startInputRecordIndex);
			}
		}else{
			if(rule.isChildRequired()){
				APattern child = rule.getChild();				
				int minOccurs = child.getMinOccurs();
				errorCatcher.missingContent(rule, startInputRecordIndex, child, minOccurs, 0, null);
			}
		}
	}		
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return true;
		else throw new IllegalStateException();
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, APattern childDefinition, APattern sourceDefinition, InternalConflictResolver resolver){
		if(expectedOrderHandlingCount > 0) return true;
		else throw new IllegalStateException();
	}
	public boolean acceptsMDescendentReduce(APattern p){
		// TODO review !!!
		// TODO should you be more defensive and check if the p is the actual 
		// pattern from the particle handler?
		// First thought: here is not really the case, there can be only one child.
		
		// if there is no corresponding ParticleHandler, get one
		// if the ParticleHandler does not expect the last occurrence
		//				return true
		// else return false // this cannot be reduced until the content is finished
		
		// before every return recycle the ParticleHandler if it was aquired 
		// only for this method
		
		boolean transitoryParticle = false;
		if(childParticleHandler == null) {
			childParticleHandler = p.getParticleHandler(this, errorCatcher);
			transitoryParticle = true;
		}
		if(!childParticleHandler.expectsLastOccurrence()){
			if(transitoryParticle){
				childParticleHandler.recycle();
				childParticleHandler = null;
			}
			return true;
		}else{			
			if(transitoryParticle){
				childParticleHandler.recycle();
				childParticleHandler = null;
			}
			return false;
		}		
	}	
	
	public int functionalEquivalenceCode(){		
		int fec = rule.hashCode();
		if(childParticleHandler != null)fec += childParticleHandler.functionalEquivalenceCode();		
		if(childStructureHandler != null)fec += childStructureHandler.functionalEquivalenceCode();
		return fec;
	}	
	public void setConflict(int index, Rule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		throw new IllegalStateException();
	}
	public AttributeHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		AttributeHandler copy = rule.getStructureHandler(errorCatcher, stackHandler);
		copy.setState(stackHandler,			
						errorCatcher, 
						childParticleHandler, 
						childStructureHandler, 
						contentIndex,
						startInputRecordIndex,
						isStartSet);
		copy.setOriginal(this);
		return copy;
	}
	public AttributeHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){	
		throw new IllegalStateException();
	}
	private void setOriginal(AttributeHandler original){
		this.original = original;
	}
	public AttributeHandler getOriginal(){
		return original;
	}
	//String stackToString(); super	
	public void closeContentStructure(APattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
	}		
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
	
	
	//Start ValidationHandler---------------------------------------------------------		
	void handleParticleShift(int inputRecordIndex, APattern childPattern){
		setChildParticleHandler(childPattern);
		childParticleHandler.handleOccurrence(inputRecordIndex);
	}
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		setChildParticleHandler(childPattern);
		childParticleHandler.handleOccurrence(stackConflictsHandler, resolver);
	}
	void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler){
		setChildParticleHandler(childPattern);
		childParticleHandler.handleOccurrence(stackConflictsHandler);
	}
	// void setStart() super
	//End ValidationHandler-----------------------------------------------------------

	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler childParticleHandler, 
							StructureHandler childStructureHandler,
							int contentIndex,
							int startInputRecordIndex,
							boolean isStartSet){
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
	}
	
	
	//--------------------------------------------------------------------------------------------
	//Start UniqueChildHandler ----------------------------------------------------------------
	//--------------------------------------------------------------------------------------------	
	public void setChildParticleHandler(APattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();		
		if(childParticleHandler == null){
			childParticleHandler = childPattern.getParticleHandler(this, errorCatcher);
			//setStart();
		}		
	}
	//--------------------------------------------------------------------------------------------
	//End UniqueChildHandler ------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------
	
	public StructureHandler getStructureHandler(){
		return childStructureHandler;
	}
	
	public ParticleHandler getParticleHandler(){
		return childParticleHandler;
	}
	
	
	public boolean isSatisfied(){
		switch(contentIndex){
            case NO_CONTENT :
                return false;
            case OPEN_CONTENT :
                return false;
            case SATISFIED_CONTENT :
                if(childStructureHandler != null && !childStructureHandler.isSatisfied()){
                    return false;
                }			
                return true;
            case SATURATED_CONTENT :
                if(childStructureHandler != null && !childStructureHandler.isSatisfied()){
                    return false;
                }			
                return true;
            case UNSATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();
            case EXCESSIVE_CONTENT :
                if(childStructureHandler != null && !childStructureHandler.isSatisfied()){
                    return false;
                }			
                return true;
            case UNSATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            default :
                throw new IllegalStateException();
        }		
	}
	
	public void childOpen(){
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                contentIndex = OPEN_CONTENT;
                break;
            case OPEN_CONTENT :
                throw new IllegalStateException();
            case SATISFIED_CONTENT :
                break;
            case SATURATED_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();
            case EXCESSIVE_CONTENT :
                //throw new IllegalStateException();
                // It is possible to have this in cases when the child of element
                // is handled by a UniqueChildPatternHandler (choice, ref , etc)
                // and after that was reduced excessive content is started. It would
                // be the initial childOpen event fired from the NoContent state of 
                // the new childStructureHandler
                break;
            case UNSATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            default :
                throw new IllegalStateException();
        }		
	}
	
	public void requiredChildSatisfied(){
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                contentIndex = SATISFIED_CONTENT;
                break;
            case OPEN_CONTENT :
                contentIndex = SATISFIED_CONTENT;
                break;
            case SATISFIED_CONTENT :
                throw new IllegalStateException();  
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            default :
                throw new IllegalStateException();    
        }		
	}
	
	public void optionalChildSatisfied(){
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                contentIndex = SATISFIED_CONTENT;
                break;
            case OPEN_CONTENT :
                contentIndex = SATISFIED_CONTENT;
                break;
            case SATISFIED_CONTENT :
                throw new IllegalStateException();  
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            default :
                throw new IllegalStateException();    
        }		
	}
	
	public void childSaturated(){
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                contentIndex = SATURATED_CONTENT;
                break;
            case OPEN_CONTENT :
                contentIndex = SATURATED_CONTENT;
                break;
            case SATISFIED_CONTENT :
                contentIndex = SATURATED_CONTENT;
                break;
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            default :
                throw new IllegalStateException();    
        }		
	}
	
	
	public void childExcessive(){
		switch(contentIndex){
            case NO_CONTENT :
                throw new IllegalStateException();  
            case OPEN_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_CONTENT :
                throw new IllegalStateException();  
            case SATURATED_CONTENT :
                childParticleHandler.reportExcessive(rule, startInputRecordIndex);
                contentIndex = EXCESSIVE_CONTENT;
                break;
            case UNSATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case EXCESSIVE_CONTENT :
                childParticleHandler.reportExcessive(rule, startInputRecordIndex);
                break;
            case UNSATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            default :
                throw new IllegalStateException();    
        }		
	}
		
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	
	public String toString(){
		//return "AttributeHandler "+hashCode()+" "+rule.toString()+" contentHandler "+contentHandler.toString();
		return "AttributeHandler  "+rule.toString()+" contentIndex="+contentIndex;
	}
} 