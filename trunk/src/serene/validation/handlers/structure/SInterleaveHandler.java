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

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.AInterleave;


import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SMultipleChildrenPattern;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.match.MatchPath;

public class SInterleaveHandler extends InterleaveHandler{
	MInterleaveHandler primaryHandler;
		
	SInterleaveHandler(){
		super();		
	}	
	
	void init(SMultipleChildrenPattern interleave, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler, MInterleaveHandler primaryHandler){
		this.rule = interleave;		
		satisfactionIndicator = interleave.getSatisfactionIndicator();
		saturationIndicator = interleave.getSaturationIndicator();
		this.errorCatcher = errorCatcher;
		this.parent = parent;
		this.stackHandler = stackHandler;		
		this.primaryHandler = primaryHandler;
		size = interleave.getChildrenCount();
		if(size > childParticleHandlers.length){
			childParticleHandlers = new ParticleHandler[size];
		}	
	}
			
	public void recycle(){
		setEmptyState();
		pool.recycle(this);
	}	
		
	
	public boolean isSatisfied(){
		return satisfactionLevel >= satisfactionIndicator;
	}
	
	public boolean isSaturated(){
		throw new UnsupportedOperationException();
	}
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	// StructureHandler getAncestorOrSelfHandler(Rule rule) super
	public void deactivate(){
		throw new IllegalStateException();
	}
    public boolean mayDeactivate(){
        stackHandler.setAsCurrentHandler(primaryHandler);      
        return false;
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
			childStructureHandlers[childIndex] = pool.getStructureHandler(child, path, errorCatcher, this, stackHandler);
		}
		return childStructureHandlers[childIndex];
	
	}
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	// void handleValidatingReduce() super
	// int functionalEquivalenceCode() super
	public SInterleaveHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public SInterleaveHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public SInterleaveHandler getOriginal(){
			throw new IllegalStateException();
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
	// boolean handleContentOrder(int expectedOrderHandlingCount, boolean conflict, APattern childDefinition, AElement sourceDefinition) super	
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
		
	//Start ChildEventHandler---------------------------------------------------------
	public int getContentIndex(){
		return contentIndex;
	}
	public void childOpen(){
		if(satisfactionIndicator == 0){
			primaryHandler.satisfiedOccurrence();
		}
	}	
	public void requiredChildSatisfied(){
		++satisfactionLevel;
		if(satisfactionLevel == satisfactionIndicator) {
			primaryHandler.satisfiedOccurrence();
		}
	}
	
	public void optionalChildSatisfied(){
	}

	public void childSaturated(){
	}
	
	public void childExcessive(){
		primaryHandler.excessiveOccurrence();
	}	
		
	public int functionalEquivalenceCode(){
		throw new IllegalArgumentException("hehe functional equivalence!");
		//return contentHandler.functionalEquivalenceCode();
	}
	//End ChildEventHandler-----------------------------------------------------------
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	
	public String toString(){		
		//return "SInterleaveHandler "+hashCode()+" "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
		return "SInterleaveHandler  "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentIndex="+contentIndex;
	}	
} 