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

import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SMultipleChildrenPattern;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.CardinalityHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.util.IntList;

import serene.validation.handlers.match.MatchPath;
// TODO
// Cosider getting rid of the AbstractSaturatedContent subclasses.
// Replace with a on the fly computing of saturation in the isSaturated()
// Seems simpler and more consistent.
abstract class MinimalReduceCountHandler extends MinimalReduceHandler{
		
	public MinimalReduceCountHandler(){
		super();
		// TODO		
		//create contentHandler subclasses
		//set the default contentHandler value	
		
		//**************************************************************************
        //**************************************************************************
        //**************************************************************************
        // START from MultipleChildrenPatternHandler        
        size  = 5;		
		
		childParticleHandlers = new ParticleHandler[size];
		childStructureHandlers = new StructureHandler[size];		
        // END from MultipleChildrenPatternHandler
        //**************************************************************************
        //**************************************************************************
        //**************************************************************************
	}	
	

	public StructureHandler getChildHandler(SRule child, MatchPath path){		
		//if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		SRule[] children = rule.getChildren();
		boolean missing = true;
		for(int i = 0; i < children.length; i++){
		    if(children[i] == child)missing = false;
		}
		if(missing)  throw new IllegalArgumentException();
		
		
		int childIndex = child.getChildIndex();
		if(childStructureHandlers[childIndex] == null){
			StructureHandler handler = pool.getStructureHandler(child, errorCatcher, this, stackHandler);
			childStructureHandlers[childIndex] = handler;
			return handler;
		}			
		return childStructureHandlers[childIndex];
	}
	
	
	//Start InnerPattern------------------------------------------------------------------	
	/**
	* It asseses the state of the handler and triggers reduce due to the saturation 
	* state of a handler(isReduceRequired() and isReduceAcceptable() are used).
	*/
	// boolean handleStateSaturationReduce() super;
		
	//boolean isReduceAllowed(){
		//return contentHandler.isSatisfied();
	//}
	boolean isCurrentChildReduceRequired(){
		return currentChildParticleHandler.getIndex() == CardinalityHandler.SATURATED;
	}
	//boolean isReduceAcceptable();
		
	//void setCurrentChildParticleHandler(APattern childPattern);
		
	//void closeContent();	
	//void closeContentParticle(SPattern childPattern);

	//for Reusable implementation
	//void setEmptyState();		
	//End InnerPattern------------------------------------------------------------------
	
	
	public String toString(){
		return "MinimalReduceHandler contentIndex="+contentIndex;
	}
		
	public abstract MinimalReduceCountHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract MinimalReduceCountHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
	
	
	
	//**************************************************************************
	//**************************************************************************
	//**************************************************************************
	// START from LimitReduceCountHandler
	
	SPattern[][] childDefinition;
	String[][] childQName;
	String[][] childSystemId;
	int[][] childLineNumber;
	int[][] childColumnNumber;	
	
	int size;
	
	
	/**
	* Used to count how many reductions of this handler could be triggered
	* by each of the children occurrences provided all other conditions for 
	* reduction, determined by the other siblings, were met. 
	*/
	IntList reduceCountList;
	
	/*public LimitHandler getChildHandler(Rule child){
		throw new IllegalStateException();
	}*/	
	public boolean acceptsMDescendentReduce(SPattern p){		
		return true;
	}	
	
	public int functionalEquivalenceCode(){
		int fec = rule.hashCode();
		for(int i = 0; i < childParticleHandlers.length; i++){
			int childCode = 0;
			
			if(childParticleHandlers[i] != null){
				childCode = childParticleHandlers[i].getRule().hashCode();
				fec += childParticleHandlers[i].functionalEquivalenceCode(); 
			}
			
			if(childStructureHandlers[i] != null){
				if(childCode == 0){
					childCode = childStructureHandlers[i].getRule().hashCode();
				}
				fec += childStructureHandlers[i].functionalEquivalenceCode();
			}
			
			if(childCode == 0){
				throw new IllegalStateException("what is this?");
			}
			fec += reduceCountList.get(i) * childCode;
		}		
		return fec;
	}
	
	//Start ChildEventHandler---------------------------------------------------------
	public int getContentIndex(){
		int result = LIMIT_REDUCE;
		return result;
	}
    
	public void childOpen(){
		/*switch(contentIndex){
            case NO_CONTENT :
                setStart();
                if(satisfactionIndicator == 0){
                    contentIndex = SATISFIED_CONTENT;
                }else{
                    contentIndex = OPEN_CONTENT;
                }
                break;
            case OPEN_CONTENT :
                break;
            case SATISFIED_CONTENT :
                break;
            case SATURATED_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_SATURATED_CONTENT :
                break;
            case SATISFIED_SATURATED_CONTENT :
                break;
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_EXCESSIVE_CONTENT :
                break;
            case SATISFIED_EXCESSIVE_CONTENT :
                break;
            default :
                throw new IllegalStateException();
        }*/	
	}
	
	
	public void optionalChildSatisfied(){
		/*switch(contentIndex){
            case NO_CONTENT :
                setStart();
                if(satisfactionIndicator == 0){
                    contentIndex = SATISFIED_CONTENT;
                }else{
                    contentIndex = OPEN_CONTENT;
                }
                break;
            case OPEN_CONTENT :
                break;
            case SATISFIED_CONTENT :
                break;  
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                break;  
            case SATISFIED_SATURATED_CONTENT :
                break;
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_EXCESSIVE_CONTENT :
                break;  
            case SATISFIED_EXCESSIVE_CONTENT :
                break;  
            default :
                throw new IllegalStateException();    
        }*/		
	}
	
	public void requiredChildSatisfied(){
	    /*satisfactionLevel++;
	    
		switch(contentIndex){
            case NO_CONTENT :
                setStart();			
                if(satisfactionLevel == satisfactionIndicator) {
                    contentIndex = SATISFIED_CONTENT;
                }else{
                    contentIndex = OPEN_CONTENT;
                }
                break;
            case OPEN_CONTENT :
                if(satisfactionLevel == satisfactionIndicator) {
                    contentIndex = SATISFIED_CONTENT;
                }
                break;
            case SATISFIED_CONTENT :
                throw new IllegalStateException();  
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                if(satisfactionLevel == satisfactionIndicator){
                    contentIndex = SATISFIED_SATURATED_CONTENT;
                }
                break;
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_EXCESSIVE_CONTENT :
                if(satisfactionLevel == satisfactionIndicator){
                    contentIndex = SATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            default :
                throw new IllegalStateException();    
        }*/		
	}
		
	public void childSaturated(){
	    /*saturationLevel++;
	    
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                if(saturationLevel == saturationIndicator){
                    contentIndex = UNSATISFIED_SATURATED_CONTENT;
                }
                else contentIndex = OPEN_CONTENT;
                break;
            case OPEN_CONTENT :
                if(saturationLevel == saturationIndicator){
                    contentIndex = UNSATISFIED_SATURATED_CONTENT;
                }
                break;
            case SATISFIED_CONTENT :
                if(saturationLevel == saturationIndicator){
                    contentIndex = SATISFIED_SATURATED_CONTENT;
                }
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
                break;  
            case SATISFIED_EXCESSIVE_CONTENT :
                break;  
            default :
                throw new IllegalStateException();    
        }*/		
	}
	
	
	public void childExcessive(){
		/*switch(contentIndex){
            case NO_CONTENT :
                // To be totally transparent and consistent with total delegation of 
                // reduce decision responsibility to dedicated methods 
                // handleExcessiveChildReduce() is used here too, but it really seems
                // a bit exagerated.
                if(!handleExcessiveChildReduce()){
                    reportExcessive();			
                    contentIndex = UNSATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case OPEN_CONTENT :
                // To be totally transparent and consistent with total delegation of 
                // reduce decision responsibility to dedicated methods 
                // handleExcessiveChildReduce() is used here too, but it really seems
                // a bit exagerated.
                if(!handleExcessiveChildReduce()){
                    reportExcessive();			
                    contentIndex = UNSATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case SATISFIED_CONTENT :
                if(!handleExcessiveChildReduce()){
                    reportExcessive();			
                    contentIndex = SATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                if(!handleExcessiveChildReduce()){
                    reportExcessive();
                    contentIndex = UNSATISFIED_EXCESSIVE_CONTENT;
                }  
                break;
            case SATISFIED_SATURATED_CONTENT :
                if(!handleExcessiveChildReduce()){
                    reportExcessive();			
                    contentIndex = SATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_EXCESSIVE_CONTENT :
                if(!handleExcessiveChildReduce()){
                    reportExcessive();
                }
                break;
            case SATISFIED_EXCESSIVE_CONTENT :
                if(!handleExcessiveChildReduce()){
				reportExcessive();
				}
				break;
            default :
                throw new IllegalStateException();    
        }*/		
	}
	
	//End ChildEventHandler-----------------------------------------------------------
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	/*public StructureHandler getAncestorOrSelfHandler(SRule rule){
		if(this.rule.equals(rule))return this;
		return null;
	}	*/
	public void deactivate(){
		throw new IllegalStateException();
	}
    public boolean handleDeactivation(){
		return false;	
	}
	//StructureHandler getChildHandler(Rule child); subclasses	
	public SPattern getRule(){
		return rule;
	}
	// boolean handleChildShift(AElement pattern)
	// boolean handleChildShift(AElement pattern, int expectedOrderHandlingCount)
	// boolean handleChildShift(AAttribute pattern)
	// /*boolean handleChildShift(AAttribute pattern, int expectedOrderHandlingCount)*/
	// boolean handleChildShift(CharsActiveTypeItem pattern)
	// boolean handleChildShift(CharsActiveTypeItem pattern, int expectedOrderHandlingCount)
	// boolean handleChildShift(APattern pattern)
	// boolean handleChildShiftAndOrder(APattern pattern, int expectedOrderHandlingCount)
	// boolean handleChildShift(APattern pattern, int startInputRecordIndex)
	// boolean handleChildShift(int count, APattern pattern, int startInputRecordIndex)
	// boolean handleChildShift(int MIN, int MAX, APattern pattern, int startInputRecordIndex)
	public void handleValidatingReduce(){
		// This method might need to go to subclasses to support the handling 
		// of content that is too incomplete to even support one child reduce
		// (discrete cardinalities).
		
		for(int i = 0; i < size; i++){	
			if(childStructureHandlers[i] != null){
				childStructureHandlers[i].handleValidatingReduce();								
			}
			if(childParticleHandlers[i] != null){
				currentChildParticleHandler = childParticleHandlers[i];
				if(isCurrentChildReduceAllowed()){
					handleCurrentChildReduce();
				}else{
					throw new IllegalStateException();
					// This should never happen with the current cardinalities.
					// In other conditions(see above) remaining ParticleHandlers 
					// should be handled later.
				}
			}
			// errors will be reported by the StructureDoubleHandler
		}
	}
	public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
		
	}	
	public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition, InternalConflictResolver resolver){
		throw new IllegalStateException();
	}	
	/*public abstract LimitReduceCountHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract LimitReduceCountHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);*/
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	//End StructureHandler------------------------------------------------------------
	
	//Start ValidationHandler---------------------------------------------------------
	void handleReduce(){
		throw new IllegalStateException();		
	}	
	/*void handleParticleShift(String systemId, int lineNumber, int columnNumber, String qName, APattern childPattern){
		setCurrentChildParticleHandler(childPattern);
		currentChildParticleHandler.handleOccurrence(qName, systemId, lineNumber, columnNumber);
	}*/	
	void handleParticleShift(SPattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		throw new UnsupportedOperationException();
	}
	void handleParticleShift(SPattern childPattern, StackConflictsHandler stackConflictsHandler){
		throw new UnsupportedOperationException();
	}
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
	
	
	
	//Start InnerPattern------------------------------------------------------------------	
	/**
	* Asseses the state of the handler and triggers reduce due to the saturation 
	* state of a handler(isReduceRequired() and isReduceAcceptable() are used).
	*/
	boolean handleStateSaturationReduce(){
		// For the currentChildParticleHandler get the state index, according to 
		// this the reduce count corresponding to the current child is incremented
		// if necessary. Implementations are different for minimal and maximal
		// handlers.
		if(isCurrentChildReduceRequired()){
			handleCurrentChildReduce();
			return true;
		}
		return false;
	}
		
	boolean isReduceAllowed(){
		//return contentHandler.isSatisfied();
		throw new IllegalStateException();
	}
	boolean isReduceRequired(){
		throw new IllegalStateException();
	}
	boolean isReduceAcceptable(){
		throw new IllegalStateException();
	}
		
	//void setCurrentChildParticleHandler(APattern childPattern);
		
	//void closeContent();	
	//void closeContentParticle(SPattern childPattern);

	//for Reusable implementation
	//void setEmptyState();		
	//End InnerPattern------------------------------------------------------------------
		
	/*void handleReshift(APattern pattern){
		throw new IllegalStateException();
	}*/
	void handleCurrentChildReduce(){		
		SPattern pattern = currentChildParticleHandler.getRule();
		int childIndex = pattern.getChildIndex();
		int count = reduceCountList.get(childIndex);
		reduceCountList.set(childIndex, 1+count);
		if(childStructureHandlers[childIndex] != null){
			childStructureHandlers[childIndex].recycle();
			childStructureHandlers[childIndex] = null;
		}
		currentChildParticleHandler.recycle();
		childParticleHandlers[childIndex] = null;
	}	
	boolean isCurrentChildReduceAllowed(){
		return currentChildParticleHandler.getIndex() == CardinalityHandler.SATISFIED_NEVER_SATURATED 
			|| currentChildParticleHandler.getIndex() == CardinalityHandler.SATISFIED_SIMPLE
			|| currentChildParticleHandler.getIndex() == CardinalityHandler.SATURATED;
	}
	/*abstract boolean isCurrentChildReduceRequired();*/
	boolean isCurrentChildReduceAcceptable(){
		throw new IllegalStateException();
	}
	// It can all be done from here, without calling the stack handler, because
	// it is the top of the hierarchy and no additional reduces could ever be 
	// triggered by this and no setCutrrentHandler is required.
	boolean handleCurrentChildOrderReduce(SPattern child, SPattern sourceDefinition){
		if(isCurrentChildReduceAllowed()){
			handleCurrentChildReduce();			
			handleChildShift(child, 0);//no more order handling, it should be correct	
			return true;
		}
		return false;
	}
	
	/*public String toString(){
		return "LimitReduceCountHandler contentIndex="+contentIndex;
	}*/
	
	void recordOccurrence(String systemId, 
						int lineNumber, 
						int columnNumber, 
						String qName, 
						SPattern pattern){
		/*
		This is actually a reduce for an occurrence, it must be done only after 
		the ParticleHandler event was received and interpreted. 
		int childIndex = pattern.getChildIndex();	
		int count = reduceCountList.get(childIndex);
		reduceCountList.set(childIndex, 1+count);*/
		// TODO 
		/*if(childSystemId[childIndex] == null || childSystemId[childIndex].length == 0){
			childSystemId[childIndex] = new String[1];
			childLineNumber[childIndex] = new int[1];
			childColumnNumber[childIndex] = new int[1];
			childQName[childIndex] = new String[1];
			childDefinition[childIndex] = new APattern[1];
			
			childSystemId[childIndex][1] = systemId;
			childLineNumber[childIndex][1] = lineNumber;
			childColumnNumber[childIndex][1] = columnNumber;
			childQName[childIndex][1] = qName;
			childDefinition[childIndex][1] = pattern;
		}else{
			int oldCount = childSystemId[childIndex].length;
			int newCount = oldCount+1;
			
			String[] increasedCSI = new String[newCount];
			System.arraycopy(childSystemId[childIndex], 0, increasedCSI, 0, oldCount);
			childSystemId[childIndex] = increasedCSI;
			
			int[] increasedLN = new int[newCount];
			System.arraycopy(childLineNumber[childIndex], 0, increasedLN, 0, oldCount);
			childLineNumber[childIndex] = increasedLN;
			
			int[] increasedCN = new int[newCount];
			System.arraycopy(childColumnNumber[childIndex], 0, increasedCN, 0, oldCount);
			childColumnNumber[childIndex] = increasedCN;
			
			String[] increasedQN = new String[newCount];
			System.arraycopy(childQName[childIndex], 0, increasedQN, 0, oldCount);
			childQName[childIndex] = increasedQN;
			
			APattern[] increasedD = new APattern[newCount];
			System.arraycopy(childDefinition[childIndex], 0, increasedD, 0, oldCount);
			childDefinition[childIndex] = increasedD;
			
			childSystemId[childIndex][oldCount] = systemId;
			childLineNumber[childIndex][oldCount] = lineNumber;
			childColumnNumber[childIndex][oldCount] = columnNumber;
			childQName[childIndex][oldCount] = qName;
			childDefinition[childIndex][oldCount] = pattern;
		}*/
	}
	// END from LimitReduceCountHandler
	//**************************************************************************
	//**************************************************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//**************************************************************************
	//**************************************************************************
	// START from MultipleChildrenPatternHandler
	
		
    SMultipleChildrenPattern rule;
    
	protected ParticleHandler[] childParticleHandlers;
	protected StructureHandler[] childStructureHandlers;
	/*protected int size;*/
		
	/**
	* Represents the total number of childSatisfied events expected by this 
	* handler. It equals the total number of required chilren of the pattern 
	* handled. When that has been reached the schema rule embodied by this handler 
	* is satisfied by what was found in the document.
	*/
	protected int satisfactionIndicator;
	/**
	* Represents the actual number of childSatisfied events fired by the 
	* child ParticleHandlers.
	*/
	protected int satisfactionLevel;
		
	/**
	* Represents the total number of childSaturated events expected by the 
	* handler from the child ParticleHandlers. If this number equals the total 
	* number of children this rule is eligible for reduction. Still, reduction is 
	* only performed if the schema configuration allows it.
	*/
	protected int saturationIndicator;
	/**
	* Represents the actual number of childSaturated events fired by the 
	* child ParticleHandlers.
	*/	
	protected int saturationLevel;
	
	
	//--------------------------------------------------------------------------------------------
	//Start ChildEventHandler---------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------
	
	public boolean isSaturated(){
		switch(contentIndex){
            case NO_CONTENT :
                return false;
            case OPEN_CONTENT :
                return false;
            case SATISFIED_CONTENT :
                return false;
            case SATURATED_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_SATURATED_CONTENT :
                return true;
            case SATISFIED_SATURATED_CONTENT :
                return true;
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_EXCESSIVE_CONTENT :
                if(saturationLevel == saturationIndicator)
                    return true;
                return false;
            case SATISFIED_EXCESSIVE_CONTENT :
                if(saturationLevel == saturationIndicator)
                    return true;
                return false;
            default :
                throw new IllegalStateException();
        }		
	}
	
	
	public boolean isSatisfied(){
		switch(contentIndex){
            case NO_CONTENT :
                return false;
            case OPEN_CONTENT :
                return false;
            case SATISFIED_CONTENT :
                for(int i = 0; i < size; i++){
                    if(childStructureHandlers[i] != null && !childStructureHandlers[i].isSatisfied())
                        return false;
                    
                    if(childParticleHandlers[i] != null && !childParticleHandlers[i].isSatisfied()
                        && !(childParticleHandlers[i].getDistanceToSatisfaction() == 1 && childStructureHandlers[i] != null))
                            return false;				
                }
                return true;
            case SATURATED_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_SATURATED_CONTENT :
                return false;
            case SATISFIED_SATURATED_CONTENT :
                for(int i = 0; i < size; i++){
                    //System.out.println(i);
                    //System.out.println(childStructureHandlers[i]);
                    if(childStructureHandlers[i] != null && !childStructureHandlers[i].isSatisfied())
                        return false;
                    //System.out.println(childParticleHandlers[i]);
                    if(childParticleHandlers[i] != null && !childParticleHandlers[i].isSatisfied()
                        && !(childParticleHandlers[i].getDistanceToSatisfaction() == 1 && childStructureHandlers[i] != null))
                            return false;				
                }
                return true;
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_EXCESSIVE_CONTENT :
                return false;
            case SATISFIED_EXCESSIVE_CONTENT :
                for(int i = 0; i < size; i++){
                    if(childStructureHandlers[i] != null && !childStructureHandlers[i].isSatisfied())
                        return false;
                    
                    if(childParticleHandlers[i] != null && !childParticleHandlers[i].isSatisfied()
                        && !(childParticleHandlers[i].getDistanceToSatisfaction() == 1 && childStructureHandlers[i] != null))
                            return false;				
                }
                return true;
            default :
                throw new IllegalStateException();
        }		
	}
	
    /*public void childOpen(){
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                parent.childOpen();
                if(satisfactionIndicator == 0){
                    contentIndex = SATISFIED_CONTENT;
                }else{
                    contentIndex = OPEN_CONTENT;
                }
                break;
            case OPEN_CONTENT :
                break;
            case SATISFIED_CONTENT :
                break;
            case SATURATED_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_SATURATED_CONTENT :
                break;
            case SATISFIED_SATURATED_CONTENT :
                break;
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_EXCESSIVE_CONTENT :
                break;
            case SATISFIED_EXCESSIVE_CONTENT :
                break;
            default :
                throw new IllegalStateException();
        }		
	}*/
	
	
	/*public void optionalChildSatisfied(){
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                parent.childOpen();
                if(satisfactionIndicator == 0){
                    contentIndex = SATISFIED_CONTENT;
                }else{
                    contentIndex = OPEN_CONTENT;
                }
                break;
            case OPEN_CONTENT :
                break;
            case SATISFIED_CONTENT :
                break;  
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                break;  
            case SATISFIED_SATURATED_CONTENT :
                break;
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_EXCESSIVE_CONTENT :
                break;  
            case SATISFIED_EXCESSIVE_CONTENT :
                break;  
            default :
                throw new IllegalStateException();    
        }		
	}*/
	
	/*public void requiredChildSatisfied(){
	    ++satisfactionLevel;
	    
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                parent.childOpen();			
                if(satisfactionLevel == satisfactionIndicator) {
                    contentIndex = SATISFIED_CONTENT;
                }else{
                    contentIndex = OPEN_CONTENT;
                }
                break;
            case OPEN_CONTENT :
                if(satisfactionLevel == satisfactionIndicator) {
                    contentIndex = SATISFIED_CONTENT;
                }
                break;
            case SATISFIED_CONTENT :
                throw new IllegalStateException();  
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                if(satisfactionLevel == satisfactionIndicator){
                    contentIndex = SATISFIED_SATURATED_CONTENT;
                }
                break;
            case SATISFIED_SATURATED_CONTENT :
                throw new IllegalStateException();  
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_EXCESSIVE_CONTENT :
                if(satisfactionLevel == satisfactionIndicator){
                    contentIndex = SATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case SATISFIED_EXCESSIVE_CONTENT :
                throw new IllegalStateException();  
            default :
                throw new IllegalStateException();    
        }		
	}*/
		
	/*public void childSaturated(){
	    ++saturationLevel;
	    
		switch(contentIndex){
            case NO_CONTENT :
                setStart();
                parent.childOpen();
                if(saturationLevel == saturationIndicator){
                    contentIndex = UNSATISFIED_SATURATED_CONTENT;
                }
                else contentIndex = OPEN_CONTENT;
                break;
            case OPEN_CONTENT :
                if(saturationLevel == saturationIndicator){
                    contentIndex = UNSATISFIED_SATURATED_CONTENT;
                }
                break;
            case SATISFIED_CONTENT :
                if(saturationLevel == saturationIndicator){
                    contentIndex = SATISFIED_SATURATED_CONTENT;
                }
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
                break;  
            case SATISFIED_EXCESSIVE_CONTENT :
                break;  
            default :
                throw new IllegalStateException();    
        }		
	}*/
	
	
	/*public void childExcessive(){
		switch(contentIndex){
            case NO_CONTENT :
                // To be totally transparent and consistent with total delegation of 
                // reduce decision responsibility to dedicated methods 
                // handleExcessiveChildReduce() is used here too, but it really seems
                // a bit exagerated.
                if(!handleExcessiveChildReduce()){
                    reportExcessive();			
                    contentIndex = UNSATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case OPEN_CONTENT :
                // To be totally transparent and consistent with total delegation of 
                // reduce decision responsibility to dedicated methods 
                // handleExcessiveChildReduce() is used here too, but it really seems
                // a bit exagerated.
                if(!handleExcessiveChildReduce()){
                    reportExcessive();			
                    contentIndex = UNSATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case SATISFIED_CONTENT :
                if(!handleExcessiveChildReduce()){
                    reportExcessive();			
                    contentIndex = SATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case SATURATED_CONTENT :
                throw new IllegalStateException();  
            case UNSATISFIED_SATURATED_CONTENT :
                if(!handleExcessiveChildReduce()){
                    reportExcessive();
                    contentIndex = UNSATISFIED_EXCESSIVE_CONTENT;
                }  
                break;
            case SATISFIED_SATURATED_CONTENT :
                if(!handleExcessiveChildReduce()){
                    reportExcessive();			
                    contentIndex = SATISFIED_EXCESSIVE_CONTENT;
                }
                break;
            case EXCESSIVE_CONTENT :
                throw new IllegalStateException();
            case UNSATISFIED_EXCESSIVE_CONTENT :
                if(!handleExcessiveChildReduce()){
                    reportExcessive();
                }
                break;
            case SATISFIED_EXCESSIVE_CONTENT :
                if(!handleExcessiveChildReduce()){
				reportExcessive();
				}
				break;
            default :
                throw new IllegalStateException();    
        }		
	}*/	
	//--------------------------------------------------------------------------------------------
	//End ChildEventHandler-----------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------

	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	/*public StructureHandler getAncestorOrSelfHandler(Rule rule){
		if(this.rule.equals(rule))return this;
		else return parent.getAncestorOrSelfHandler(rule);
	}*/
    /*public void deactivate(){
        if(isReduceAllowed() && isReduceAcceptable()) stackHandler.endSubtreeValidation(this);
        else{
            if(!parent.handleDeactivation()) throw new IllegalStateException();
        }
	}*/
    /*public boolean handleDeactivation(){
		stackHandler.setAsCurrentHandler(this);
        return true;
	}*/
	/*public StructureHandler getChildHandler(Rule child){		
		if(!child.getParent().equals(rule)) throw new IllegalArgumentException();
		int childIndex = child.getChildIndex();
		if(childStructureHandlers[childIndex] == null){
			childStructureHandlers[childIndex] = child.getStructureHandler(errorCatcher, this, stackHandler);
		}
		return childStructureHandlers[childIndex];	
	}*/
	/*public SPattern getRule(){
		return rule;
	}*/
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	/*public void handleValidatingReduce(){		
		reduce:{
			for(int i = 0; i < size; i++){			
				if(childStructureHandlers[i] != null){
					childStructureHandlers[i].handleValidatingReduce();
					if(childParticleHandlers[i] == null) //this handler was already reduced as result of the child's reduction
						break reduce; 
					childParticleHandlers[i].reportMissing(rule, startInputRecordIndex);
				}else if(childParticleHandlers[i] != null){					
					childParticleHandlers[i].reportMissing(rule, startInputRecordIndex);
				}else{
					if(rule.isChildRequired(i)){						
						SPattern child = rule.getChild(i);				
						int minOccurs = child.getMinOccurs();
						errorCatcher.missingContent(rule, startInputRecordIndex, child, minOccurs, 0, null);						
					}
				}
			}			
			handleReduce();
		}
	}*/
	/*public boolean acceptsMDescendentReduce(SPattern p){
		// TODO should you be more defensive and check if the p is the actual 
		// pattern from the particle handler?
		// First thought: here is not really the case, there can be only one child.
			
				
		// if there is no corresponding ParticlEHandler, get one
		// if the ParticleHandler does not expect the last occurrence
		//				return true
		// else determine if this handler is satisfied or if it could be 
		// satisfied by the shifting of p // here if the particleHandler's 
		// distanceToSatisfaction < 1 it means that it is satisfied and it 
		// cannot have any influence on the satisfactionLevel
		// if distanceToSatisfaction > 1 it means it is too far and one shift
		// cannot satisfy the rule anyway
		// if distanceToSatisfaction == 1 then the satisfactionLevel needs to 
		// be tested
		// 		yes return parent.acceptsMDescendentReduce(rule)
		//		no return false
		
		// before every return recycle the ParticleHandler if it was aquired 
		// only for this method
		
		ParticleHandler childParticleHandler = childParticleHandlers[p.getChildIndex()];
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
			if(isSatisfied()) return parent.acceptsMDescendentReduce(rule);
			else if(distanceToSatisfaction == 1 && satisfactionLevel == satisfactionIndicator+1)return parent.acceptsMDescendentReduce(rule);
			else return false;
		}
	}*/
	/*public int functionalEquivalenceCode(){		
		int fec = rule.hashCode();
		for(int i = 0; i < childParticleHandlers.length; i++){
			if(childParticleHandlers[i] != null)fec += childParticleHandlers[i].functionalEquivalenceCode(); 
			if(childStructureHandlers[i] != null)fec += childStructureHandlers[i].functionalEquivalenceCode();
		}		
		return fec;
	}*/
	public void closeContentStructure(SPattern pattern){
		int index = pattern.getChildIndex();
		childStructureHandlers[index].recycle();		
		childStructureHandlers[index] = null;
	}	
	
	//void handleValidatingReduce(); subclasses			
	/*public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern childDefinition, SPattern sourceDefinition){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
		
	}*/
	/*public boolean handleContentOrder(int expectedOrderHandlingCount, SPattern child, SPattern sourceDefinition, InternalConflictResolver resolver){
		if(expectedOrderHandlingCount > 0) return parent.handleContentOrder(expectedOrderHandlingCount, rule, sourceDefinition);
		else throw new IllegalStateException();
	}*/
	//int functionalEquivalenceCode(); subclass	
	
	public void setConflict(int index, SRule[] path, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		if(path[index] != rule) throw new IllegalStateException();
		if(this.stackConflictsHandler == null) this.stackConflictsHandler = stackConflictsHandler;
		boolean newRecord = stackConflictsHandler.record(rule, this, resolver);
		if(newRecord && ++index != path.length)parent.setConflict(index, path, stackConflictsHandler, resolver);
	}
	
	
	/*public abstract MultipleChildrenPatternHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract MultipleChildrenPatternHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);*/
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
	//boolean handleStateSaturationReduce() super	
	//boolean isReduceAllowed() super
	/*boolean isReduceRequired(){
		return saturationIndicator == size && isSaturated();
	}
	boolean isReduceAcceptable(){
		return parent.acceptsMDescendentReduce(rule);
	}	*/
	void setCurrentChildParticleHandler(SPattern childPattern){		
		//if(!childPattern.getParent().equals(rule)) throw new IllegalArgumentException();

		boolean missing = true;
		for(int i = 0; i < rule.getChildrenCount(); i++){
		    if(rule.getChild(i) == childPattern)missing = false;
		}
		if(missing)  throw new IllegalArgumentException();
						
		int childIndex = childPattern.getChildIndex();
		if(childParticleHandlers[childIndex] == null){
			childParticleHandlers[childIndex] = pool.getParticleHandler(childPattern, this, errorCatcher);
		}	
		currentChildParticleHandler = childParticleHandlers[childIndex];
	}
	void closeContent(){
		for(int i = 0; i < size; i++){			
			if(childStructureHandlers[i] != null){
				closeContentStructure(i);
			}
			if(childParticleHandlers[i] != null){
				closeContentParticle(i);
			}		
		}
	}	
	void closeContentParticle(SPattern pattern){		
		int index = pattern.getChildIndex();
		childParticleHandlers[index].recycle();
		childParticleHandlers[index] = null;
		
	}
	//for Reusable implementation
	void setEmptyState(){
		closeContent();
		if(stackConflictsHandler != null){
			stackConflictsHandler.close(this);
			stackConflictsHandler = null;
		}
		contentIndex = NO_CONTENT;
		satisfactionLevel = 0;
		saturationLevel = 0;
		
		if(isStartSet){
		    activeInputDescriptor.unregisterClientForRecord(startInputRecordIndex, this);
		    isStartSet = false;
		    startInputRecordIndex = -1;
		}
	}			
	//End InnerPattern------------------------------------------------------------------
	
	boolean handleExcessiveChildReduce(){
		//APattern child = currentChildParticleHandler.getPattern();
		//handleReset();
		//handleChildShift(inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber(), inputStackDescriptor.getQName(), child);
		
		if(isReduceAllowed() && isReduceAcceptable()){
			handleReshift(currentChildParticleHandler.getRule());
			return true;
		}
		return false;
	} 
	
	void handleReshift(SPattern pattern){
		stackHandler.reshift(this, pattern);
	}
    
    void handleValidatingReshift(SPattern pattern){
		stackHandler.validatingReshift(this, pattern);
	}
    
	void closeContentStructure(){
		for(int i = 0; i < size; i++){			
			if(childStructureHandlers[i] != null){
				closeContentStructure(i);
			}
		}
	}	
	void closeContentParticle(){
		for(int i = 0; i < size; i++){
			if(childParticleHandlers[i] != null){
				closeContentParticle(i);
			}		
		}
	}	
	void closeContentStructure(int index){
		childStructureHandlers[index].recycle();		
		childStructureHandlers[index] = null;
	}
	
	void closeContentParticle(int index){
		childParticleHandlers[index].recycle();
		childParticleHandlers[index] = null;
	}
		
	
	void reportExcessive(){
		currentChildParticleHandler.reportExcessive(rule, startInputRecordIndex);
	}
	
	public StructureHandler[] getStructureHandlers(){
		return childStructureHandlers;
	}
	
	public ParticleHandler[] getParticleHandlers(){
		return childParticleHandlers;
	}
	
	
	/*public String toString(){
		return "MultipleChildrenPatternHandler contentIndex="+contentIndex;
	}*/
	// END from MultipleChildrenPatternHandler
	//**************************************************************************
	//**************************************************************************
	//**************************************************************************
	
	
	
	
} 