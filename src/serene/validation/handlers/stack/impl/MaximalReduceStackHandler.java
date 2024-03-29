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

package serene.validation.handlers.stack.impl;

import serene.util.IntList;


import serene.validation.schema.simplified.SMultipleChildrenPattern;
import serene.validation.schema.simplified.SRule;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.StructureHandler;

import serene.validation.handlers.match.MatchPath;
import serene.validation.handlers.match.AttributeMatchPath;

public class MaximalReduceStackHandler extends ContextStackHandler{
	
	MaximalReduceStackHandler(){
		super();
	}		
	
	public void recycle(){
		endingValidation = false;
		isCurrentHandlerReseted = false;
		topHandler.recycle();
		topHandler = null;
		currentHandler = null;
		currentPath = null;
		pool.recycle(this);		
	}
	
	/*void init(InputStackDescriptor inputStackDescriptor, ValidatorStackHandlerPool pool){
		this.pool = pool;
		this.inputStackDescriptor = inputStackDescriptor;
	}*/
	
	void init(IntList reduceCountList, SMultipleChildrenPattern topPattern, ErrorCatcher errorCatcher){
		if(topPattern != null)topHandler = structureHandlerPool.getMaximalReduceCountHandler(topPattern, reduceCountList, errorCatcher, this);
		currentHandler = topHandler;		
		/*pathHandler.init(topHandler);		*/
	}
	
	void init(IntList reduceCountList, IntList startedCountList, SMultipleChildrenPattern topPattern, ErrorCatcher errorCatcher){
		if(topPattern != null){
			topHandler = structureHandlerPool.getMaximalReduceCountHandler(topPattern, reduceCountList, startedCountList, errorCatcher, this);
			currentHandler = topHandler;		
			/*pathHandler.init(topHandler);*/
		}
	}
	
	public MaximalReduceStackHandler getCopy(IntList reduceCountList, ErrorCatcher errorCatcher){		
		MaximalReduceStackHandler copy = pool.getMaximalReduceStackHandler(reduceCountList, null, errorCatcher);
		StructureHandler topCopy = topHandler.getCopy(reduceCountList, copy, errorCatcher);
		copy.setState(topCopy);
		return copy;
	}
	
	public MaximalReduceStackHandler getCopy(IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher){		
		MaximalReduceStackHandler copy = pool.getMaximalReduceStackHandler(reduceCountList, startedCountList, null, errorCatcher);
		StructureHandler topCopy = topHandler.getCopy(reduceCountList, startedCountList, copy, errorCatcher);
		copy.setState(topCopy);
		return copy;
	}
	
	private void setState(StructureHandler topHandler){
		this.topHandler = topHandler;
		currentHandler = topHandler;
		/*pathHandler.init(topHandler);*/
	}
	
	
	void activatePath(MatchPath path){	    
	    expectedOrderHandlingCount = 0;
        StructureHandler s = currentHandler;
        
        SRule currentRule = currentHandler.getRule();
        boolean process = false;
        
	    for(int i = path.size()-1; i > 0; i--){
	        SRule r = path.get(i);
	        if(process){
	            if(r.specifiesOrder())expectedOrderHandlingCount++;
	            s = s.getChildHandler(r, path);
	            if(s == null){// null is returned after a ChoiceHandler was reset due to a child that differs from currentChild
                    activatePath(path);
                    return;
                }
	        }else if(r == currentRule){
	            if(r.specifiesOrder())expectedOrderHandlingCount++;
	            process = true;
	        }	        	 
	    }
	    currentHandler = s;
	    currentPath = path;
	}
		
	void activatePath(AttributeMatchPath path){	      
	    expectedOrderHandlingCount = 0;
        StructureHandler s = currentHandler;
        
        SRule currentRule = currentHandler.getRule();
        boolean process = false;
        
	    for(int i = path.size()-1; i > 0; i--){
	        SRule r = path.get(i);
	        if(process){
	            s = s.getChildHandler(r, path);
	            if(s == null){// null is returned after a ChoiceHandler was reset due to a child that differs from currentChild
                    activatePath(path);
                    return;
                }
	        }else if(r == currentRule){
	            process = true;
	        }	        	 
	    }
	    currentHandler = s;
	    currentPath = path;
	}
	
	public String toString(){
		//if(currentHandler != null) return "MinimalReduceStackHandler "+hashCode()+" stack: "+currentHandler.stackToString();
		if(currentHandler != null) return "MinimalReduceStackHandler  stack: "+currentHandler.stackToString();
		//return "MinimalReduceStackHandler "+hashCode()+" stack: null";
		return "MinimalReduceStackHandler  stack: null";
	}
}