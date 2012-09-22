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

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

import serene.util.IntList;

/*import serene.validation.schema.active.Rule;
import serene.validation.schema.active.ActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ACompositor;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;*/

import serene.validation.schema.simplified.Type;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SMultipleChildrenPattern;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.ValidatorRuleHandlerPool;

import serene.validation.handlers.conflict.ValidatorConflictHandlerPool;
import serene.validation.handlers.conflict.ContextConflictsDescriptor;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.match.MatchPath;

import serene.Reusable;

public class ValidatorStackHandlerPool implements Reusable{
	
	// int contextStackHCreated;
	int contextStackHMaxSize;
	int contextStackHFree;
	int contextStackHMinFree;
	ContextStackHandler[] contextStackH;
	
	// int minimalReduceStackHCreated;
	int minimalReduceStackHMaxSize;
	int minimalReduceStackHFree;
	int minimalReduceStackHMinFree;
	MinimalReduceStackHandler[] minimalReduceStackH;
	
	// int maximalReduceStackHCreated;
	int maximalReduceStackHMaxSize;
	int maximalReduceStackHFree;
	int maximalReduceStackHMinFree;
	MaximalReduceStackHandler[] maximalReduceStackH;
	
		
	int candidateStackHCreated;
	int candidateStackHRequested;
	int candidateStackHRecycled;
	int candidateStackHMaxSize;
	int candidateStackHFree;
	int candidateStackHMinFree;
	CandidateStackHandlerImpl[] candidateStackH;
		
	int concurrentStackHCreated;
	int concurrentStackHMaxSize;
	int concurrentStackHFree;
	int concurrentStackHMinFree;
	ConcurrentStackHandlerImpl[] concurrentStackH;
	
	StackHandlerPool pool;
	
	boolean full;
	
	InputStackDescriptor inputStackDescriptor;
	ValidatorConflictHandlerPool conflictHandlerPool;
	ValidatorRuleHandlerPool structureHandlerPool;
	
	public ValidatorStackHandlerPool(StackHandlerPool pool){
		this.pool = pool;
		
		contextStackHMaxSize = 20;
        minimalReduceStackHMaxSize = 20;
        maximalReduceStackHMaxSize = 20;
        candidateStackHMaxSize = 40;
        concurrentStackHMaxSize = 20;
        
        full = false;
	}
	
	public void recycle(){
		if(full)releaseHandlers();
		pool.recycle(this);
	}
	
	public void fill(InputStackDescriptor inputStackDescriptor, ValidatorConflictHandlerPool conflictHandlerPool, ValidatorRuleHandlerPool structureHandlerPool){
		this.inputStackDescriptor = inputStackDescriptor;
		this.conflictHandlerPool = conflictHandlerPool;
		this.structureHandlerPool = structureHandlerPool;
		
		if(pool != null){
		    pool.fill(this,
				contextStackH,
				minimalReduceStackH,
				maximalReduceStackH,
				candidateStackH,
				concurrentStackH);
		}else{
		    contextStackH = new ContextStackHandler[10];
            minimalReduceStackH = new MinimalReduceStackHandler[10];
            maximalReduceStackH = new MaximalReduceStackHandler[10];
                
            candidateStackH = new CandidateStackHandlerImpl[10];
            concurrentStackH = new ConcurrentStackHandlerImpl[10];
		}
		full = true;
	}	
	
	void initFilled(int contextStackHFillCount,
					int minimalReduceStackHFillCount,
					int maximalReduceStackHFillCount,
					int candidateStackHFillCount,
					int concurrentStackHFillCount){
		contextStackHFree = contextStackHFillCount;
		contextStackHMinFree = contextStackHFree;
		for(int i = 0; i < contextStackHFree; i++){	
			contextStackH[i].init(inputStackDescriptor, structureHandlerPool, this);
		}
		
		minimalReduceStackHFree = minimalReduceStackHFillCount;
		minimalReduceStackHMinFree = minimalReduceStackHFree;
		for(int i = 0; i < minimalReduceStackHFree; i++){	
			minimalReduceStackH[i].init(inputStackDescriptor, structureHandlerPool, this);
		}
		
		maximalReduceStackHFree = maximalReduceStackHFillCount;
		maximalReduceStackHMinFree = maximalReduceStackHFree;
		for(int i = 0; i < maximalReduceStackHFree; i++){	
			maximalReduceStackH[i].init(inputStackDescriptor, structureHandlerPool, this);
		}
		
		candidateStackHFree = candidateStackHFillCount;
		candidateStackHMinFree = candidateStackHFree;
		for(int i = 0; i < candidateStackHFree; i++){	
			candidateStackH[i].init(inputStackDescriptor, this);
		}
		
		concurrentStackHFree = concurrentStackHFillCount;
		concurrentStackHMinFree = concurrentStackHFree;
		for(int i = 0; i < concurrentStackHFree; i++){	
			concurrentStackH[i].init(inputStackDescriptor, conflictHandlerPool, this);
		}		
	}
	
	public void releaseHandlers(){
		pool.recycle(contextStackHFree,
		            contextStackHFree - contextStackHMinFree,
					contextStackH,
					minimalReduceStackHFree,
					minimalReduceStackHFree - minimalReduceStackHMinFree,
					minimalReduceStackH,
					maximalReduceStackHFree,
					maximalReduceStackHFree - maximalReduceStackHMinFree,
					maximalReduceStackH,
					candidateStackHFree,
					candidateStackHFree - candidateStackHMinFree,
					candidateStackH,					
					concurrentStackHFree,
					concurrentStackHFree - concurrentStackHMinFree,
					concurrentStackH);
		
		contextStackHFree = 0;
        minimalReduceStackHFree = 0;
        maximalReduceStackHFree = 0;
        candidateStackHFree = 0;			
        concurrentStackHFree = 0;
        
		full = false;
	}
	
	public ContextStackHandler getContextStackHandler(SRule type, ErrorCatcher ehm){			
		if(contextStackHFree == 0){
			// contextStackHCreated++;			
			ContextStackHandler csh = new ContextStackHandler();	
			csh.init(inputStackDescriptor, structureHandlerPool, this);
			csh.init(type, ehm);
			return csh;			
		}else{
			ContextStackHandler csh = contextStackH[--contextStackHFree];
			csh.init(type, ehm);
			if(contextStackHFree < contextStackHMinFree) contextStackHMinFree = contextStackHFree;
			return csh;
		}		
	}
		
	public void recycle(ContextStackHandler csh){		
		if(contextStackHFree == contextStackH.length){
			if(contextStackH.length == contextStackHMaxSize) return;
			ContextStackHandler[] increased = new ContextStackHandler[10+contextStackH.length];
			System.arraycopy(contextStackH, 0, increased, 0, contextStackHFree);
			contextStackH = increased;
		}
		contextStackH[contextStackHFree++] = csh;
	}
	
	public MinimalReduceStackHandler getMinimalReduceStackHandler(IntList reduceCountList, IntList startedCountList, SMultipleChildrenPattern compositor, ErrorCatcher ehm){			
		if(minimalReduceStackHFree == 0){
			// minimalReduceStackHCreated++;			
			MinimalReduceStackHandler csh = new MinimalReduceStackHandler();	
			csh.init(inputStackDescriptor, structureHandlerPool, this);
			csh.init(reduceCountList, startedCountList, compositor, ehm);
			return csh;			
		}else{
			MinimalReduceStackHandler csh = minimalReduceStackH[--minimalReduceStackHFree];
			csh.init(reduceCountList, startedCountList, compositor, ehm);
			if(minimalReduceStackHFree < minimalReduceStackHMinFree) minimalReduceStackHMinFree = minimalReduceStackHFree;
			return csh;
		}		
	}
	
	public MinimalReduceStackHandler getMinimalReduceStackHandler(IntList reduceCountList, SMultipleChildrenPattern compositor, ErrorCatcher ehm){			
		if(minimalReduceStackHFree == 0){
			// minimalReduceStackHCreated++;			
			MinimalReduceStackHandler csh = new MinimalReduceStackHandler();	
			csh.init(inputStackDescriptor, structureHandlerPool, this);
			csh.init(reduceCountList, compositor, ehm);
			return csh;			
		}else{
			MinimalReduceStackHandler csh = minimalReduceStackH[--minimalReduceStackHFree];
			csh.init(reduceCountList, compositor, ehm);
			if(minimalReduceStackHFree < minimalReduceStackHMinFree) minimalReduceStackHMinFree = minimalReduceStackHFree;
			return csh;
		}		
	}	
	public void recycle(MinimalReduceStackHandler csh){				
		if(minimalReduceStackHFree == minimalReduceStackH.length){
			if(minimalReduceStackH.length == minimalReduceStackHMaxSize)return;
			MinimalReduceStackHandler[] increased = new MinimalReduceStackHandler[10+minimalReduceStackH.length];
			System.arraycopy(minimalReduceStackH, 0, increased, 0, minimalReduceStackHFree);
			minimalReduceStackH = increased;
		}
		minimalReduceStackH[minimalReduceStackHFree++] = csh;
	}
	
	public MaximalReduceStackHandler getMaximalReduceStackHandler(IntList reduceCountList, IntList startedCountList, SMultipleChildrenPattern compositor, ErrorCatcher ehm){			
		if(maximalReduceStackHFree == 0){
			// maximalReduceStackHCreated++;			
			MaximalReduceStackHandler csh = new MaximalReduceStackHandler();	
			csh.init(inputStackDescriptor, structureHandlerPool, this);
			csh.init(reduceCountList, startedCountList, compositor, ehm);
			return csh;			
		}else{
			MaximalReduceStackHandler csh = maximalReduceStackH[--maximalReduceStackHFree];
			csh.init(reduceCountList, startedCountList, compositor, ehm);
			if(maximalReduceStackHFree < maximalReduceStackHMinFree) maximalReduceStackHMinFree = maximalReduceStackHFree;
			return csh;
		}		
	}
	
	public MaximalReduceStackHandler getMaximalReduceStackHandler(IntList reduceCountList, SMultipleChildrenPattern compositor, ErrorCatcher ehm){			
		if(maximalReduceStackHFree == 0){
			// maximalReduceStackHCreated++;			
			MaximalReduceStackHandler csh = new MaximalReduceStackHandler();	
			csh.init(inputStackDescriptor, structureHandlerPool, this);
			csh.init(reduceCountList, compositor, ehm);
			return csh;			
		}else{
			MaximalReduceStackHandler csh = maximalReduceStackH[--maximalReduceStackHFree];
			csh.init(reduceCountList, compositor, ehm);
			if(maximalReduceStackHFree < maximalReduceStackHMinFree) maximalReduceStackHMinFree = maximalReduceStackHFree; 
			return csh;
		}		
	}
	
	public void recycle(MaximalReduceStackHandler csh){				
		if(maximalReduceStackHFree == maximalReduceStackH.length){
			if(maximalReduceStackH.length == maximalReduceStackHMaxSize) return;
			MaximalReduceStackHandler[] increased = new MaximalReduceStackHandler[10+maximalReduceStackH.length];
			System.arraycopy(maximalReduceStackH, 0, increased, 0, maximalReduceStackHFree);
			maximalReduceStackH = increased;
		}
		maximalReduceStackH[maximalReduceStackHFree++] = csh;
	}
	
	//copy
	public CandidateStackHandlerImpl getCandidateStackHandler(StructureHandler topHandler, 
	                                                MatchPath currentPath,
													SRule currentRule,
													StackConflictsHandler stackConflictsHandler,
													ConcurrentStackHandler parent,
													ContextConflictsDescriptor contextConflictsDescriptor,
													boolean hasDisqualifyingError,
													ErrorCatcher errorCatcher){
	    candidateStackHRequested++;
		if(candidateStackHFree == 0){
			candidateStackHCreated++;
			CandidateStackHandlerImpl csh = new CandidateStackHandlerImpl();
			csh.init(inputStackDescriptor, this);			
			csh.init(topHandler, 
			            currentPath,
						currentRule,
						stackConflictsHandler,
						parent,
						contextConflictsDescriptor,
						hasDisqualifyingError,
						errorCatcher);
			return csh;			
		}else{
			CandidateStackHandlerImpl csh = candidateStackH[--candidateStackHFree];
			csh.init(topHandler,
			            currentPath,
						currentRule,						
						stackConflictsHandler,
						parent,
						contextConflictsDescriptor,
						hasDisqualifyingError,
						errorCatcher);
			if(candidateStackHFree < candidateStackHMinFree) candidateStackHMinFree = candidateStackHFree;
			return csh;	
		}		
	}
	
	//first
	public CandidateStackHandlerImpl getCandidateStackHandler(StructureHandler topHandler,
	                                                MatchPath currentPath,
													SRule currentRule,
													ConcurrentStackHandler parent,
													ContextConflictsDescriptor contextConflictsDescriptor,
													ErrorCatcher errorCatcher){
		candidateStackHRequested++;
		if(candidateStackHFree == 0){
			candidateStackHCreated++;
			CandidateStackHandlerImpl csh = new CandidateStackHandlerImpl();
			csh.init(inputStackDescriptor, this);			
			csh.init(topHandler, 
			            currentPath,
						currentRule,
						parent,
						contextConflictsDescriptor,
						errorCatcher);
			return csh;			
		}else{
			CandidateStackHandlerImpl csh = candidateStackH[--candidateStackHFree];
			csh.init(topHandler,
			            currentPath,
						currentRule,
						parent,
						contextConflictsDescriptor,
						errorCatcher);
			if(candidateStackHFree < candidateStackHMinFree) candidateStackHMinFree = candidateStackHFree;
			return csh;	
		}		
	}
		
	public void recycle(CandidateStackHandlerImpl csh){
        candidateStackHRecycled++;		
	//	if(candidateStackHFree == 3) throw new IllegalStateException();
		if(candidateStackHFree == candidateStackH.length){
			if(candidateStackH.length == candidateStackHMaxSize)return;
			CandidateStackHandlerImpl[] increased = new CandidateStackHandlerImpl[10+candidateStackH.length];
			System.arraycopy(candidateStackH, 0, increased, 0, candidateStackHFree);
			candidateStackH = increased;
		}
		candidateStackH[candidateStackHFree++] = csh;
	}
		
	public ConcurrentStackHandlerImpl getConcurrentStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher){				
		if(concurrentStackHFree == 0){
			ConcurrentStackHandlerImpl csh = new ConcurrentStackHandlerImpl();
			csh.init(inputStackDescriptor, conflictHandlerPool, this);
			csh.init(originalHandler, errorCatcher);
			return csh;			
		}else{
			ConcurrentStackHandlerImpl csh = concurrentStackH[--concurrentStackHFree];
			csh.init(originalHandler, errorCatcher);
			if(concurrentStackHFree < concurrentStackHMinFree) concurrentStackHMinFree = concurrentStackHFree;
			return csh;
		}		
	}
	
		
	public void recycle(ConcurrentStackHandlerImpl csh){
		if(concurrentStackHFree == concurrentStackH.length){
			if(concurrentStackH.length == concurrentStackHMaxSize)return;
			ConcurrentStackHandlerImpl[] increased = new ConcurrentStackHandlerImpl[10+concurrentStackH.length];
			System.arraycopy(concurrentStackH, 0, increased, 0, concurrentStackHFree);
			concurrentStackH = increased;
		}
		concurrentStackH[concurrentStackHFree++] = csh;
	}
}