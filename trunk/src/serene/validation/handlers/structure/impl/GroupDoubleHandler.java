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

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;


import serene.validation.handlers.structure.util.ChildFinder;

import serene.util.IntList;

import sereneWrite.MessageWriter;

public class GroupDoubleHandler extends StructureDoubleHandler{
	IntList minimalStartedCount;
	IntList maximalStartedCount;
	
	ChildFinder childFinder;
	
	GroupDoubleHandler original;
	
	GroupDoubleHandler(MessageWriter debugWriter){
		super(debugWriter);		
		minimalStartedCount = new IntList();
		maximalStartedCount = new IntList();
		
		childFinder = new ChildFinder(debugWriter);
	}
		
	public void recycle(){
		original = null;
		minimalReduceStackHandler.recycle();
		maximalReduceStackHandler.recycle();
		recycler.recycle(this);
	}
	
	
	void init(AGroup pattern, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler, ActiveModelStackHandlerPool stackHandlerPool){
		this.rule = pattern;		
		this.errorCatcher = errorCatcher;		
		this.parent = parent;
		this.stackHandler = stackHandler;		
		int size = minimalReduceCount.size();				
		for(int i = 0; i < size; i++){
			minimalReduceCount.set(i, 0);
			maximalReduceCount.set(i, 0);
			minimalStartedCount.set(i, 0);
			maximalStartedCount.set(i, 0);	
		}
		int childrenCount = pattern.getChildrenCount();
		if(size < childrenCount){
			for(int i = size; i < childrenCount; i++){
				minimalReduceCount.add(0);
				maximalReduceCount.add(0);
				minimalStartedCount.add(0);
				maximalStartedCount.add(0);	
			}
		}
		minimalReduceStackHandler = stackHandlerPool.getMinimalReduceStackHandler(minimalReduceCount, minimalStartedCount, pattern, errorCatcher);
		maximalReduceStackHandler = stackHandlerPool.getMaximalReduceStackHandler(maximalReduceCount, maximalStartedCount, pattern, errorCatcher);
		// TODO
		// Consider using the same set start in the subclasses of StructureValdiationHandler
		// It might be possible to eliminate the calls to parent for childOpen, and have
		// one less walk up the structure validation tree.
		setStart();
	}
	
	
	// Always shift first on minimal and then on maximal, so that expectations are 
	// always first created and then fullfiled.
	public boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount){
		// System.out.println("****"+this);		
		// System.out.println(expectedOrderHandlingCount+"****"+pattern);
		if(expectedOrderHandlingCount > 1){
			if(!handleContentOrder(expectedOrderHandlingCount-1, pattern, pattern)){
				return false;
			}
		}
		if(pattern instanceof AElement){
			minimalReduceStackHandler.shift((AElement)pattern);
			maximalReduceStackHandler.shift((AElement)pattern);
		}else if(pattern instanceof AAttribute){
			minimalReduceStackHandler.shift((AAttribute)pattern);
			maximalReduceStackHandler.shift((AAttribute)pattern);
		}else if(pattern instanceof CharsActiveTypeItem){
			minimalReduceStackHandler.shift((CharsActiveTypeItem)pattern);
			maximalReduceStackHandler.shift((CharsActiveTypeItem)pattern);
		}else throw new IllegalStateException();
		handleContentOrder(pattern);
		return true;
	}
	
	
	
	
	// Always shift first on minimal and then on maximal, so that expectations are 
	// always first created and then fullfiled.
	public boolean handleChildShift(APattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		
		if(expectedOrderHandlingCount > 1){
			if(!handleContentOrder(expectedOrderHandlingCount-1, pattern, pattern)){
				return false;
			}
		}
		if(pattern instanceof AElement){
			minimalReduceStackHandler.shift((AElement)pattern);
			maximalReduceStackHandler.shift((AElement)pattern);
		}else if(pattern instanceof AAttribute){
			minimalReduceStackHandler.shift((AAttribute)pattern);
			maximalReduceStackHandler.shift((AAttribute)pattern);
		}else if(pattern instanceof CharsActiveTypeItem){
			minimalReduceStackHandler.shift((CharsActiveTypeItem)pattern);
			maximalReduceStackHandler.shift((CharsActiveTypeItem)pattern);
		}else throw new IllegalStateException();
		handleContentOrder(pattern);
		return true;
	}
	
	
	void handleContentOrder(APattern sourceDefinition){
		APattern currentChild = childFinder.findChild(rule, sourceDefinition);
		int currentIndex = currentChild.getChildIndex();
		int maxCount = maximalStartedCount.get(currentIndex);
		int size = rule.getChildrenCount();	
		//System.out.println("ORDER "+sourceDefinition+" maxCount "+maxCount);
		for(int i = currentIndex+1; i < size; i++){
			// System.out.println(startSystemId+":"+startLineNumber);
			// System.out.println(validationItemLocator.getSystemId()+":"+validationItemLocator.getLineNumber());
			// System.out.println(i+" "+maxCount+"//"+minimalStartedCount.get(i));
			if(minimalStartedCount.get(i) >= maxCount){	
				APattern reper = ((AGroup)rule).getChild(i);
				errorCatcher.misplacedContent(rule, starttSystemId, starttLineNumber, starttColumnNumber, currentChild, validationItemLocator.getItemId(), validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), sourceDefinition, reper);
				//System.out.println("order error "+sourceDefinition
				//	+"   MIN "+minimalStartedCount.get(i)+" MAX "+maxCount);
				break;
			}
		}		
	}

	public GroupDoubleHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		IntList minimalReduceCountCopy = minimalReduceCount.getCopy(); 
		IntList maximalReduceCountCopy = maximalReduceCount.getCopy();
		
		IntList minimalStartedCountCopy = minimalStartedCount.getCopy();
		IntList maximalStartedCountCopy = maximalStartedCount.getCopy();
		
		MinimalReduceStackHandler minimalReduceStackHandlerCopy = minimalReduceStackHandler.getCopy(minimalReduceCountCopy, minimalStartedCountCopy, errorCatcher);
		MaximalReduceStackHandler maximalReduceStackHandlerCopy = maximalReduceStackHandler.getCopy(maximalReduceCountCopy, maximalStartedCountCopy, errorCatcher);
		
		GroupDoubleHandler copy = (GroupDoubleHandler)((AGroup)rule).getStructureHandler(errorCatcher, (StructureValidationHandler)parent, stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						minimalReduceStackHandlerCopy,
						maximalReduceStackHandlerCopy,
						starttSystemId,
						starttLineNumber,
						starttColumnNumber,
						starttQName);
		copy.setOriginal(this);
		return copy;
	}
	private void setOriginal(GroupDoubleHandler original){
		this.original = original;
	}
	public GroupDoubleHandler getOriginal(){
		return original;
	}
	private void setState(StackHandler stackHandler,
							ErrorCatcher errorCatcher,
							MinimalReduceStackHandler minimalReduceStackHandler,
							MaximalReduceStackHandler maximalReduceStackHandler,
							String startSystemId,
							int startLineNumber,
							int startColumnNumber,
							String startQName){
		this.stackHandler = stackHandler;
		this.errorCatcher = errorCatcher;
		this.minimalReduceStackHandler = minimalReduceStackHandler;
		this.maximalReduceStackHandler = maximalReduceStackHandler;
		this.starttSystemId = startSystemId;
		this.starttLineNumber = startLineNumber;
		this.starttColumnNumber = startColumnNumber;
		this.starttQName = startQName;
	}
	
	public void accept(RuleHandlerVisitor visitor){
		visitor.visit(this);
	}
	
	public String toString(){
		return "GroupDoubleHandler";
	}
}