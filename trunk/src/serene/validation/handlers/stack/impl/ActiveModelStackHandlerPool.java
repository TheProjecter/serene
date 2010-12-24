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

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.ActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ACompositor;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.StructureHandler;

import serene.validation.handlers.conflict.ActiveModelConflictHandlerPool;
import serene.validation.handlers.conflict.ContextConflictsDescriptor;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.Reusable;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;

public class ActiveModelStackHandlerPool implements Reusable, StackHandlerRecycler{
	
	// int contextStackHCreated;
	int contextStackHPoolSize;
	int contextStackHFree = 0;
	ContextStackHandler[] contextStackH;
	
	// int minimalReduceStackHCreated;
	int minimalReduceStackHPoolSize;
	int minimalReduceStackHFree = 0;
	MinimalReduceStackHandler[] minimalReduceStackH;
	
	// int maximalReduceStackHCreated;
	int maximalReduceStackHPoolSize;
	int maximalReduceStackHFree = 0;
	MaximalReduceStackHandler[] maximalReduceStackH;
	
	
	int candidateStackHCreated;
	int candidateStackHPoolSize;
	int candidateStackHFree = 0;
	CandidateStackHandlerImpl[] candidateStackH;
		
	int concurrentStackHCreated;
	int concurrentStackHPoolSize;
	int concurrentStackHFree = 0;
	ConcurrentStackHandlerImpl[] concurrentStackH;
	
	/*int compositeConcurrentStackHCreated;
	int compositeConcurrentStackHPoolSize;
	int compositeConcurrentStackHFree = 0;
	CompositeConcurrentStackHandlerImpl[] compositeConcurrentStackH;*/
	
	StackHandlerPool pool;
	
	
	ValidationItemLocator validationItemLocator;
	ActiveModelConflictHandlerPool conflictHandlerPool;
	
	MessageWriter debugWriter;
	
	public ActiveModelStackHandlerPool(StackHandlerPool pool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.pool = pool;
	}
	
	public void recycle(){
		if(contextStackHFree != 0 ||
			minimalReduceStackHFree != 0 ||
			maximalReduceStackHFree != 0 ||
			candidateStackHFree != 0 ||
			concurrentStackHFree != 0 /*||
			compositeConcurrentStackHFree != 0*/)	releaseHandlers();
		pool.recycle(this);
	}
	
	public void fill(ValidationItemLocator validationItemLocator, ActiveModelConflictHandlerPool conflictHandlerPool){
		this.validationItemLocator = validationItemLocator;
		this.conflictHandlerPool = conflictHandlerPool;
		
		pool.fill(this,
				contextStackH,
				minimalReduceStackH,
				maximalReduceStackH,
				candidateStackH,
				concurrentStackH/*,
				compositeConcurrentStackH*/);	
	}	
	
	void setHandlers(int contextStackHFree,
					ContextStackHandler[] contextStackH,
					int minimalReduceStackHFree,
					MinimalReduceStackHandler[] minimalReduceStackH,
					int maximalReduceStackHFree,
					MaximalReduceStackHandler[] maximalReduceStackH,
					int candidateStackHFree,
					CandidateStackHandlerImpl[] candidateStackH,
					int concurrentStackHFree,
					ConcurrentStackHandlerImpl[] concurrentStackH/*,
					int compositeConcurrentStackHFree,
					CompositeConcurrentStackHandlerImpl[] compositeConcurrentStackH*/){
		contextStackHPoolSize = contextStackH.length;
		this.contextStackHFree = contextStackHFree;
		this.contextStackH = contextStackH;
		for(int i = 0; i < contextStackHFree; i++){	
			contextStackH[i].init(validationItemLocator, this);
		}
		
		minimalReduceStackHPoolSize = minimalReduceStackH.length;
		this.minimalReduceStackHFree = minimalReduceStackHFree;
		this.minimalReduceStackH = minimalReduceStackH;
		for(int i = 0; i < minimalReduceStackHFree; i++){	
			minimalReduceStackH[i].init(validationItemLocator, this);
		}
		
		maximalReduceStackHPoolSize = maximalReduceStackH.length;
		this.maximalReduceStackHFree = maximalReduceStackHFree;
		this.maximalReduceStackH = maximalReduceStackH;
		for(int i = 0; i < maximalReduceStackHFree; i++){	
			maximalReduceStackH[i].init(validationItemLocator, this);
		}
		
		candidateStackHPoolSize = candidateStackH.length;
		this.candidateStackHFree = candidateStackHFree;
		this.candidateStackH = candidateStackH;
		for(int i = 0; i < candidateStackHFree; i++){	
			candidateStackH[i].init(validationItemLocator, this);
		}
		
		concurrentStackHPoolSize = concurrentStackH.length;
		this.concurrentStackHFree = concurrentStackHFree;
		this.concurrentStackH = concurrentStackH;
		for(int i = 0; i < concurrentStackHFree; i++){	
			concurrentStackH[i].init(validationItemLocator, conflictHandlerPool, this);
		}
		
		/*compositeConcurrentStackHPoolSize = compositeConcurrentStackH.length;
		this.compositeConcurrentStackHFree = compositeConcurrentStackHFree;
		this.compositeConcurrentStackH = compositeConcurrentStackH;
		for(int i = 0; i < compositeConcurrentStackHFree; i++){	
			compositeConcurrentStackH[i].init(validationItemLocator, conflictHandlerPool, this);
		}*/
	}
	
	public void releaseHandlers(){
		// System.out.println("context created "+contextStackHCreated);
		// System.out.println(Arrays.toString(contextStackH));
		// System.out.println("candidate created "+candidateStackHCreated);
		// System.out.println(Arrays.toString(candidateStackH));
		// System.out.println("concurrent created "+concurrentStackHCreated);
		// System.out.println(Arrays.toString(concurrentStackH));
		// System.out.println("compositeConcurrent created "+compositeConcurrentStackHCreated);
		// System.out.println(Arrays.toString(compositeConcurrentStackH));
		pool.recycle(contextStackHFree,
					contextStackH,
					minimalReduceStackHFree,
					minimalReduceStackH,
					maximalReduceStackHFree,
					maximalReduceStackH,
					candidateStackHFree,
					candidateStackH,
					concurrentStackHFree,
					concurrentStackH/*,
					compositeConcurrentStackHFree,
					compositeConcurrentStackH*/);
		contextStackHFree = 0;
		minimalReduceStackHFree = 0;
		maximalReduceStackHFree = 0;
		candidateStackHFree = 0;
		concurrentStackHFree = 0;
		/*compositeConcurrentStackHFree = 0;	*/
	}
	
	public ContextStackHandler getContextStackHandler(ActiveType type, ErrorCatcher ehm){			
		if(contextStackHFree == 0){
			// contextStackHCreated++;			
			ContextStackHandler csh = new ContextStackHandler(debugWriter);	
			csh.init(validationItemLocator, this);
			csh.init(type, ehm);
			return csh;			
		}else{
			ContextStackHandler csh = contextStackH[--contextStackHFree];
			csh.init(type, ehm);
			return csh;
		}		
	}
		
	public void recycle(ContextStackHandler csh){		
		if(contextStackHFree == contextStackHPoolSize){
			if(contextStackHPoolSize == 100) return;
			ContextStackHandler[] increased = new ContextStackHandler[++contextStackHPoolSize];
			System.arraycopy(contextStackH, 0, increased, 0, contextStackHFree);
			contextStackH = increased;
		}
		contextStackH[contextStackHFree++] = csh;
	}
	
	public MinimalReduceStackHandler getMinimalReduceStackHandler(IntList reduceCountList, IntList startedCountList, ACompositor compositor, ErrorCatcher ehm){			
		if(minimalReduceStackHFree == 0){
			// minimalReduceStackHCreated++;			
			MinimalReduceStackHandler csh = new MinimalReduceStackHandler(debugWriter);	
			csh.init(validationItemLocator, this);
			csh.init(reduceCountList, startedCountList, compositor, ehm);
			return csh;			
		}else{
			MinimalReduceStackHandler csh = minimalReduceStackH[--minimalReduceStackHFree];
			csh.init(reduceCountList, startedCountList, compositor, ehm);
			return csh;
		}		
	}
	
	public MinimalReduceStackHandler getMinimalReduceStackHandler(IntList reduceCountList, ACompositor compositor, ErrorCatcher ehm){			
		if(minimalReduceStackHFree == 0){
			// minimalReduceStackHCreated++;			
			MinimalReduceStackHandler csh = new MinimalReduceStackHandler(debugWriter);	
			csh.init(validationItemLocator, this);
			csh.init(reduceCountList, compositor, ehm);
			return csh;			
		}else{
			MinimalReduceStackHandler csh = minimalReduceStackH[--minimalReduceStackHFree];
			csh.init(reduceCountList, compositor, ehm);
			return csh;
		}		
	}	
	public void recycle(MinimalReduceStackHandler csh){				
		if(minimalReduceStackHFree == minimalReduceStackHPoolSize){
			if(100 == minimalReduceStackHPoolSize)return;
			MinimalReduceStackHandler[] increased = new MinimalReduceStackHandler[++minimalReduceStackHPoolSize];
			System.arraycopy(minimalReduceStackH, 0, increased, 0, minimalReduceStackHFree);
			minimalReduceStackH = increased;
		}
		minimalReduceStackH[minimalReduceStackHFree++] = csh;
	}
	
	public MaximalReduceStackHandler getMaximalReduceStackHandler(IntList reduceCountList, IntList startedCountList, ACompositor compositor, ErrorCatcher ehm){			
		if(maximalReduceStackHFree == 0){
			// maximalReduceStackHCreated++;			
			MaximalReduceStackHandler csh = new MaximalReduceStackHandler(debugWriter);	
			csh.init(validationItemLocator, this);
			csh.init(reduceCountList, startedCountList, compositor, ehm);
			return csh;			
		}else{
			MaximalReduceStackHandler csh = maximalReduceStackH[--maximalReduceStackHFree];
			csh.init(reduceCountList, startedCountList, compositor, ehm);
			return csh;
		}		
	}
	
	public MaximalReduceStackHandler getMaximalReduceStackHandler(IntList reduceCountList, ACompositor compositor, ErrorCatcher ehm){			
		if(maximalReduceStackHFree == 0){
			// maximalReduceStackHCreated++;			
			MaximalReduceStackHandler csh = new MaximalReduceStackHandler(debugWriter);	
			csh.init(validationItemLocator, this);
			csh.init(reduceCountList, compositor, ehm);
			return csh;			
		}else{
			MaximalReduceStackHandler csh = maximalReduceStackH[--maximalReduceStackHFree];
			csh.init(reduceCountList, compositor, ehm);
			return csh;
		}		
	}
	
	public void recycle(MaximalReduceStackHandler csh){				
		if(maximalReduceStackHFree == maximalReduceStackHPoolSize){
			if(maximalReduceStackHPoolSize == 100) return;
			MaximalReduceStackHandler[] increased = new MaximalReduceStackHandler[++maximalReduceStackHPoolSize];
			System.arraycopy(maximalReduceStackH, 0, increased, 0, maximalReduceStackHFree);
			maximalReduceStackH = increased;
		}
		maximalReduceStackH[maximalReduceStackHFree++] = csh;
	}
	
	//copy
	public CandidateStackHandlerImpl getCandidateStackHandler(StructureHandler topHandler, 
													Rule currentRule,
													StackConflictsHandler stackConflictsHandler,
													ConcurrentStackHandler parent,
													ContextConflictsDescriptor contextConflictsDescriptor,
													boolean hasDisqualifyingError,
													ErrorCatcher errorCatcher){		
		if(candidateStackHFree == 0){
			
			// candidateStackHCreated++;
			// System.out.println("candidate created "+candidateStackHCreated);
			// System.out.println("candidate size "+candidateStackHPoolSize);
			
			CandidateStackHandlerImpl csh = new CandidateStackHandlerImpl(debugWriter);
			csh.init(validationItemLocator, this);			
			csh.init(topHandler, 
						currentRule,
						stackConflictsHandler,
						parent,
						contextConflictsDescriptor,
						hasDisqualifyingError,
						errorCatcher);
			return csh;			
		}else{
			
			// System.out.println("candidate free "+candidateStackHFree);
			// System.out.println("candidate size "+candidateStackHPoolSize);
			
			CandidateStackHandlerImpl csh = candidateStackH[--candidateStackHFree];
			csh.init(topHandler, 
						currentRule,						
						stackConflictsHandler,
						parent,
						contextConflictsDescriptor,
						hasDisqualifyingError,
						errorCatcher);
			return csh;	
		}		
	}
	
	//first
	public CandidateStackHandlerImpl getCandidateStackHandler(StructureHandler topHandler, 
													Rule currentRule,
													ConcurrentStackHandler parent,
													ContextConflictsDescriptor contextConflictsDescriptor,
													ErrorCatcher errorCatcher){
		
		if(candidateStackHFree == 0){
			
			// candidateStackHCreated++;
			// System.out.println("candidate created "+candidateStackHCreated);
			// System.out.println("candidate size "+candidateStackHPoolSize);
			
			CandidateStackHandlerImpl csh = new CandidateStackHandlerImpl(debugWriter);
			csh.init(validationItemLocator, this);			
			csh.init(topHandler, 
						currentRule,
						parent,
						contextConflictsDescriptor,
						errorCatcher);
			return csh;			
		}else{
			
			// System.out.println("candidate free "+candidateStackHFree);
			// System.out.println("candidate size "+candidateStackHPoolSize);			
			
			CandidateStackHandlerImpl csh = candidateStackH[--candidateStackHFree];
			csh.init(topHandler, 
						currentRule,
						parent,
						contextConflictsDescriptor,
						errorCatcher);
			return csh;	
		}		
	}
		
	public void recycle(CandidateStackHandlerImpl csh){		
	//	if(candidateStackHFree == 3) throw new IllegalStateException();
		if(candidateStackHFree == candidateStackHPoolSize){
			if(100 == candidateStackHPoolSize)return;
			CandidateStackHandlerImpl[] increased = new CandidateStackHandlerImpl[++candidateStackHPoolSize];
			System.arraycopy(candidateStackH, 0, increased, 0, candidateStackHFree);
			candidateStackH = increased;
		}
		candidateStackH[candidateStackHFree++] = csh;
	}
		
	public ConcurrentStackHandlerImpl getConcurrentStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher){				
		if(concurrentStackHFree == 0){
			
			// concurrentStackHCreated++;			
			// System.out.println("base created "+concurrentStackHCreated);
			// System.out.println("base size "+concurrentStackHPoolSize);
			
			ConcurrentStackHandlerImpl csh = new ConcurrentStackHandlerImpl(debugWriter);
			csh.init(validationItemLocator, conflictHandlerPool, this);
			csh.init(originalHandler, errorCatcher);
			return csh;			
		}else{
			
			// System.out.println("base free "+concurrentStackHFree);
			// System.out.println("base size "+concurrentStackHPoolSize);			
			
			ConcurrentStackHandlerImpl csh = concurrentStackH[--concurrentStackHFree];
			csh.init(originalHandler, errorCatcher);
			return csh;
		}		
	}
	
		
	public void recycle(ConcurrentStackHandlerImpl csh){
		if(concurrentStackHFree == concurrentStackHPoolSize){
			if(100 == concurrentStackHPoolSize)return;
			ConcurrentStackHandlerImpl[] increased = new ConcurrentStackHandlerImpl[++concurrentStackHPoolSize];
			System.arraycopy(concurrentStackH, 0, increased, 0, concurrentStackHFree);
			concurrentStackH = increased;
		}
		concurrentStackH[concurrentStackHFree++] = csh;
	}
	
	

}