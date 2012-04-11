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

import serene.util.IntList;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AInterleave;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

public class InterleaveMinimalReduceCountHandler extends MinimalReduceCountHandler{
	ContentHandler superSatisfiedContent; 
	ContentHandler satisfiedNeverReduceContent;
	
	InterleaveMinimalReduceCountHandler original;
	
	InterleaveMinimalReduceCountHandler(MessageWriter debugWriter){
		super(debugWriter);		
		satisfiedNeverReduceContent = new SatisfiedNeverReduceContent();
		superSatisfiedContent = satisfiedContent;
	}	
	
	void init(IntList reduceCountList, AInterleave interleave, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler){
		this.reduceCountList = reduceCountList;
		this.rule = interleave;		
		satisfactionIndicator = interleave.getSatisfactionIndicator();
		saturationIndicator = interleave.getSaturationIndicator();
		this.errorCatcher = errorCatcher;
		this.stackHandler = stackHandler;		
		size = interleave.getChildrenCount();
		if(size > childParticleHandlers.length){
			childParticleHandlers = new ParticleHandler[size];
			childStructureHandlers = new StructureHandler[size];
		}		
		childDefinition = new APattern[size][];
		childQName = new String[size][];
		childSystemId = new String[size][];
		childLineNumber = new int[size][];
		childColumnNumber = new int[size][];
		if(saturationIndicator == 0 && interleave.getMinOccurs() <= 1)satisfiedContent = satisfiedNeverReduceContent;
		else satisfiedContent = superSatisfiedContent;
	}
			
	public void recycle(){
		original = null;
		setEmptyState();
		recycler.recycle(this);
	}	
		
	
	//Start StructureHandler----------------------------------------------------------
	//StructureHandler getParentHandler(); super	
	// StructureValidationHandler getAncestorOrSelfHandler(Rule rule) super
	// StructureHandler getChildHandler(Rule child) super
	// APattern getRule() super
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount) super
	// void handleValidatingReduce() super
	// int functionalEquivalenceCode() super
	public InterleaveMinimalReduceCountHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		// TODO
		InterleaveMinimalReduceCountHandler copy = ((AInterleave)rule).getStructureHandler(new IntList(), errorCatcher, (MinimalReduceStackHandler)stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandlers, 
						childStructureHandlers,
						size,
						satisfactionLevel,
						saturationLevel,
						contentHandler.getContentIndex(),
						startInputRecordIndex,
						isStartSet);
		copy.setOriginal(this);
		return copy; 
	}
	public InterleaveMinimalReduceCountHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	//String stackToString(); super
	// String getStartQName() super
	// String getStartSystemId() super
	// int getStartLineNumber() super
	// int getStartColumnNumber() super
	public InterleaveMinimalReduceCountHandler getCopy(IntList reduceCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		InterleaveMinimalReduceCountHandler copy = (InterleaveMinimalReduceCountHandler)rule.getStructureHandler(reduceCountList, errorCatcher, (MinimalReduceStackHandler)stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						childParticleHandlers, 
						childStructureHandlers,
						size,
						satisfactionLevel,
						saturationLevel,
						contentHandler.getContentIndex(),
						startInputRecordIndex,
						isStartSet);
		copy.setOriginal(this);
		return copy;
	}
	private void setOriginal(InterleaveMinimalReduceCountHandler original){
		this.original = original;
	}
	public InterleaveMinimalReduceCountHandler getOriginal(){
		return original;
	}
	//End StructureHandler------------------------------------------------------------
	
	
	//Start ValidationHandler---------------------------------------------------------
	// boolean handleChildShift(String systemId, int lineNumber, int columnNumber, String qName, APattern childPattern) super
	// boolean acceptsMDescendentReduce(APattern p) super
	// boolean handleContentOrder(int expectedOrderHandlingCount, boolean conflict, APattern childDefinition, AElement sourceDefinition) super	
	// void setStart() super	
	//End ValidationHandler-----------------------------------------------------------
		
	private void setState(StackHandler stackHandler, 
							ErrorCatcher errorCatcher,
							ParticleHandler[] cph, 
							StructureHandler[] csh,	
							int size,
							int satisfactionLevel,
							int saturationLevel,
							int contentHandlerContentIndex,
							int startInputRecordIndex,
							boolean isStartSet){
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
		
		if(this.isStartSet){
            activeInputDescriptor.unregisterClientForRecord(this.startInputRecordIndex, this);
        }
		this.startInputRecordIndex = startInputRecordIndex;
		this.isStartSet = isStartSet;
		if(isStartSet){		    
		    activeInputDescriptor.registerClientForRecord(startInputRecordIndex, this);
		}
	}	
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	public String toString(){		
		//return "InterleaveMinimalReduceCountHandler "+hashCode()+" "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
		return "InterleaveMinimalReduceCountHandler  "+rule.toString()+" "+satisfactionLevel+"/"+satisfactionIndicator+" contentHandler "+contentHandler.toString();
	}
	
	protected class SatisfiedNeverReduceContent extends AbstractSatisfiedContent{
		public boolean isSatisfied(){
			for(int i = 0; i < size; i++){
				if(childStructureHandlers[i] != null && !childStructureHandlers[i].isSatisfied())
					return false;
				
				if(childParticleHandlers[i] != null && !childParticleHandlers[i].isSatisfied()
					&& !(childParticleHandlers[i].getDistanceToSatisfaction() == 1 && childStructureHandlers[i] != null))
						return false;				
			}
			return true;
		}
		public void childOpen(){
		}
		public void optionalChildSatisfied(){
		}
		public void requiredChildSatisfied(){			
			throw new IllegalStateException();
		}
		/*public void childSatisfiedPlus(){			
			return ACCEPT;
		}*/
		public void childSaturated(){
			throw new IllegalStateException();
		}
		public void childExcessive(){
			throw new IllegalStateException();
		}		
	}
} 