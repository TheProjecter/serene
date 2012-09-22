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

import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.StructureHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.match.MatchPath;

abstract class UCMaximalReduceHandler extends MaximalReduceHandler{
	public UCMaximalReduceHandler(){
		super();
	}
	
	/*boolean isReduceRequired(){
		return contentHandler.isSatisfied();
	}*/
		
	/*public StructureHandler getChildHandler(SRule child, MatchPath path){		
		//if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		boolean missing = true;
		for(int i = 0; i < rule.getChildrenCount(); i++){
		    if(rule.getChild(i) == child)missing = false;
		}
		if(missing)  throw new IllegalArgumentException();
		
		
		int childIndex = child.getChildIndex();
		if(childStructureHandler == null){
			StructureHandler handler = pool.getStructureHandler(child, errorCatcher, this, stackHandler);
			childStructureHandler = handler;
			return handler;
		}
		return childStructureHandler;
	}*/	


    //**************************************************************************
    //**************************************************************************
    //**************************************************************************
    // START from UniqueChildPatternHandler
    ParticleHandler childParticleHandler;
	StructureHandler childStructureHandler;	
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	/*public StructureHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		else return parent.getAncestorOrSelfHandler(rule);
	}*/
    public void deactivate(){
		stackHandler.endSubtreeValidation(this);
	}
    public boolean handleDeactivation(){
		return parent.handleDeactivation();
	}
	/*public StructureHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler == null) childStructureHandler = child.getStructureHandler(errorCatcher, this, stackHandler);
		return childStructureHandler;
	}	
	public SPattern getRule(){
		return rule;
	}*/
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
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
	/*public boolean acceptsMDescendentReduce(SPattern p){
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
			int distanceToSatisfaction = childParticleHandler.getDistanceToSatisfaction();
			if(transitoryParticle){
				childParticleHandler.recycle();
				childParticleHandler = null;
			}
			/*if(isSatisfied()) return parent.acceptsMDescendentReduce(rule);
			else*/ /*if(distanceToSatisfaction <= 1 )return parent.acceptsMDescendentReduce(rule);
			else return false;
		}		
	}*/
	/*public int functionalEquivalenceCode(){
		int fec = rule.hashCode();
		if(childParticleHandler != null)fec += childParticleHandler.functionalEquivalenceCode();		
		if(childStructureHandler != null)fec += childStructureHandler.functionalEquivalenceCode();
		return fec;
	}
	public void closeContentStructure(APattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
	}*/
	
	//void handleValidatingReduce(); subclasses			
	/*public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
		
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern child, SPattern sourceDefinition, InternalConflictResolver resolver){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
	}*/
	//int functionalEquivalenceCode(); subclass	
	
	/*public void setConflict(int index, SRule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(path[index] != rule) throw new IllegalStateException();
		if(this.stackConflictsHandler == null) this.stackConflictsHandler = stackConflictsHandler;
		boolean newRecord = stackConflictsHandler.record(rule, this, resolver);
		if(newRecord && ++index != path.length)parent.setConflict(index, path, stackConflictsHandler, resolver);
	}*/
	
	
	public abstract UCMaximalReduceHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract UCMaximalReduceHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
			
	
	//Start ValidationHandler---------------------------------------------------------		
	// boolean handleChildShift(String systemId, int lineNumber, int columnNumber, String qName, APattern childPattern) super
	// boolean handleContentOrder(int expectedOrderHandlingCount, boolean conflict, APattern childDefinition, AElement sourceDefinition) super	
	// void setStart() super
	//End ValidationHandler-----------------------------------------------------------
	
	
	
	
	//Start InnerPattern------------------------------------------------------------------	
	// boolean handleStateSaturationReduce() super	
	// boolean isReduceAllowed()super
	boolean isReduceRequired(){
		return isSaturated();
	}
	boolean isReduceAcceptable(){
		return true;
	}
	/*void setCurrentChildParticleHandler(APattern childPattern){	
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();				
		if(childParticleHandler == null){
			childParticleHandler = childPattern.getParticleHandler(this, errorCatcher);
		}
		currentChildParticleHandler = childParticleHandler;
	}*/		
	void closeContent(){
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
	}		
	/*void closeContentParticle(SPattern childPattern){
		if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
	}*/
	//for Reusable implementation
	void setEmptyState(){
		closeContent();
		if(stackConflictsHandler != null){
			stackConflictsHandler.close(this);
			stackConflictsHandler = null;
		}
		contentIndex = NO_CONTENT;	
		
		if(isStartSet){
		    activeInputDescriptor.unregisterClientForRecord(startInputRecordIndex, this);
		    isStartSet = false;
		    startInputRecordIndex = -1;
		}
	}
	//End InnerPattern------------------------------------------------------------------
	
	void closeContentStructure(){		
		if(childStructureHandler != null){
			childStructureHandler.recycle();
			childStructureHandler = null;
		}
	}
	void closeContentParticle(){		
		if(childParticleHandler != null){
			childParticleHandler.recycle();
			childParticleHandler = null;
		}
	}
	
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
                throw new IllegalStateException();
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
                parent.childOpen();
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
                throw new IllegalStateException();
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
                parent.childOpen();
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
                parent.childOpen();
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
                parent.childOpen();
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
        throw new IllegalStateException();  		
	}
		
	public String toString(){
		return "UniqueChildStructureHandler contentIndex="+contentIndex;
	}
    // END from UniqueChildPatternHandler	
    //**************************************************************************
    //**************************************************************************
    //**************************************************************************
    
    
    
        
}