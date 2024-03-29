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

import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SMultipleChildrenPattern;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.util.IntList;

import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.CharsMatchPath;
import serene.validation.handlers.match.MatchPath;

public class InterleaveDoubleHandler extends StructureDoubleHandler{    
	InterleaveDoubleHandler original;
	InterleaveDoubleHandler(){
		super();
	}
	
	public void recycle(){
		original = null;
		minimalReduceStackHandler.recycle();
		maximalReduceStackHandler.recycle();
		
		if(isStartSet){
		    activeInputDescriptor.unregisterClientForRecord(startInputRecordIndex, this);
		    isStartSet = false;
		    startInputRecordIndex = -1;
		}
		
		
		minimalReduceCount.clear();
		maximalReduceCount.clear();		
		
		if(currentPath != null){
		    currentPath.recycle();
		    currentPath = null;
		}
		
		pool.recycle(this);
	}
	
	void init(SMultipleChildrenPattern pattern, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler, ValidatorStackHandlerPool stackHandlerPool){
		this.rule = pattern;		
		this.errorCatcher = errorCatcher;		
		this.parent = parent;
		this.stackHandler = stackHandler;
		int size = pattern.getChildrenCount();		
		
		for(int i = 0; i < size; i++){
			minimalReduceCount.add(0);
			maximalReduceCount.add(0);
		}		
		minimalReduceStackHandler = stackHandlerPool.getMinimalReduceStackHandler(minimalReduceCount, pattern, errorCatcher);
		maximalReduceStackHandler = stackHandlerPool.getMaximalReduceStackHandler(maximalReduceCount, pattern, errorCatcher);
		// TODO
		// Consider using the same set start in the subclasses of StructureValdiationHandler
		// It might be possible to eliminate the calls to parent for childOpen, and have
		// one less walk up the structure validation tree.
		setStart();
	}
	
	public boolean handleChildShiftAndOrder(SPattern pattern, int expectedOrderHandlingCount){
		// System.out.println("*"+this);		
		// System.out.println(expectedOrderHandlingCount+"*"+pattern);
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		
		MatchPath path = stackHandler.getCurrentMatchPath();
			
		if(path.getItemId() == MatchPath.ELEMENT){
		    ElementMatchPath hpath = path.getElementHeadPath(rule);
			minimalReduceStackHandler.shift(hpath);
			maximalReduceStackHandler.shift(hpath);
			if(currentPath != null)currentPath.recycle();
			currentPath = hpath;
		}else if(path.getItemId() == MatchPath.ATTRIBUTE){
			AttributeMatchPath hpath = path.getAttributeHeadPath(rule);
			minimalReduceStackHandler.shift(hpath);
			maximalReduceStackHandler.shift(hpath);
			if(currentPath != null)currentPath.recycle();
			currentPath = hpath;
		}else if(path.getItemId() == MatchPath.CHARS){
			CharsMatchPath hpath = path.getCharsHeadPath(rule);
			minimalReduceStackHandler.shift(hpath);
			maximalReduceStackHandler.shift(hpath);
			if(currentPath != null)currentPath.recycle();
			currentPath = hpath;
		}else throw new IllegalStateException();
		return true;
	}
	
		
	public boolean handleChildShift(SPattern pattern, int expectedOrderHandlingCount, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver){
		//System.out.println("*"+this);		
		//System.out.println(expectedOrderHandlingCount+"*"+pattern);
		if(expectedOrderHandlingCount > 0){
			if(!handleContentOrder(expectedOrderHandlingCount, pattern, pattern)){
				return false;//TODO problem is that it did shift, but in the order's reshift, so this is not 100% correct
			}				
		}
		
		MatchPath path = stackHandler.getCurrentMatchPath();
			
		if(path.getItemId() == MatchPath.ELEMENT){
		    ElementMatchPath hpath = path.getElementHeadPath(rule);
			minimalReduceStackHandler.shift(hpath);
			maximalReduceStackHandler.shift(hpath);
		}else if(path.getItemId() == MatchPath.ATTRIBUTE){
			AttributeMatchPath hpath = path.getAttributeHeadPath(rule);
			minimalReduceStackHandler.shift(hpath);
			maximalReduceStackHandler.shift(hpath);
		}else if(path.getItemId() == MatchPath.CHARS){
			CharsMatchPath hpath = path.getCharsHeadPath(rule);
			minimalReduceStackHandler.shift(hpath);
			maximalReduceStackHandler.shift(hpath);
		}else throw new IllegalStateException();
		return true;
	}
	
	
	public InterleaveDoubleHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();		 
	}
	public InterleaveDoubleHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher){
		IntList minimalReduceCountCopy = minimalReduceCount.getCopy(); 
		IntList maximalReduceCountCopy = maximalReduceCount.getCopy();
		
		MinimalReduceStackHandler minimalReduceStackHandlerCopy = minimalReduceStackHandler.getCopy(minimalReduceCountCopy, errorCatcher);
		MaximalReduceStackHandler maximalReduceStackHandlerCopy = maximalReduceStackHandler.getCopy(maximalReduceCountCopy, errorCatcher);
		
		InterleaveDoubleHandler copy = pool.getCopy(this, rule, errorCatcher, (StructureHandler)parent, stackHandler);
		copy.setState(stackHandler, 
						errorCatcher, 
						minimalReduceStackHandlerCopy,
						maximalReduceStackHandlerCopy,
						startInputRecordIndex,
						isStartSet);
		copy.setOriginal(this);
		return copy;
	}
	private void setOriginal(InterleaveDoubleHandler original){
		this.original = original;
	}
	public InterleaveDoubleHandler getOriginal(){
		return original;
	}
	private void setState(StackHandler stackHandler,
							ErrorCatcher errorCatcher,
							MinimalReduceStackHandler minimalReduceStackHandler,
							MaximalReduceStackHandler maximalReduceStackHandler,
							int startInputRecordIndex,
							boolean isStartSet){
		this.stackHandler = stackHandler;
		this.errorCatcher = errorCatcher;
		this.minimalReduceStackHandler = minimalReduceStackHandler;
		this.maximalReduceStackHandler = maximalReduceStackHandler;
		
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
		return "InterleaveDoubleHandler";
	}
}