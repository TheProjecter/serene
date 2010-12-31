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

import sereneWrite.MessageWriter;

public class StackHandlerPool{
	private static volatile StackHandlerPool instance;
	
	int vshPoolFree; 
	int vshPoolPoolSize;
	ActiveModelStackHandlerPool[] vshPools;
		
	int contextStackHAverageUse;	
	int contextStackHPoolSize;
	int contextStackHFree;
	ContextStackHandler[] contextStackH;
	
	int minimalReduceStackHAverageUse;	
	int minimalReduceStackHPoolSize;
	int minimalReduceStackHFree;
	MinimalReduceStackHandler[] minimalReduceStackH;
	
	int maximalReduceStackHAverageUse;	
	int maximalReduceStackHPoolSize;
	int maximalReduceStackHFree;
	MaximalReduceStackHandler[] maximalReduceStackH;
	
	
		
	int candidateStackHAverageUse;
	int candidateStackHPoolSize;
	int candidateStackHFree;
	CandidateStackHandlerImpl[] candidateStackH;
		
	int concurrentStackHAverageUse;
	int concurrentStackHPoolSize;
	int concurrentStackHFree;
	ConcurrentStackHandlerImpl[] concurrentStackH;
		
	/*int compositeConcurrentStackHAverageUse;
	int compositeConcurrentStackHPoolSize;
	int compositeConcurrentStackHFree;
	CompositeConcurrentStackHandlerImpl[] compositeConcurrentStackH;*/
	
	final int UNUSED = 0;
	
	MessageWriter debugWriter;
	
	private StackHandlerPool(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		vshPoolFree = 0;
		vshPoolPoolSize = 10;
		vshPools = new ActiveModelStackHandlerPool[vshPoolPoolSize];
		
		
		contextStackHAverageUse = UNUSED;
		contextStackHPoolSize = 10;
		contextStackHFree = 0;
		contextStackH = new ContextStackHandler[contextStackHPoolSize];

		
		minimalReduceStackHAverageUse = UNUSED;
		minimalReduceStackHPoolSize = 10;
		minimalReduceStackHFree = 0;
		minimalReduceStackH = new MinimalReduceStackHandler[minimalReduceStackHPoolSize];		
		
		
		maximalReduceStackHAverageUse = UNUSED;
		maximalReduceStackHPoolSize = 10;
		maximalReduceStackHFree = 0;
		maximalReduceStackH = new MaximalReduceStackHandler[maximalReduceStackHPoolSize];
		
		
		
		candidateStackHAverageUse = UNUSED;
		candidateStackHPoolSize = 10;
		candidateStackHFree = 0;
		candidateStackH = new CandidateStackHandlerImpl[candidateStackHPoolSize];		
				
		
		concurrentStackHAverageUse = UNUSED;
		concurrentStackHPoolSize = 5;
		concurrentStackHFree = 0;
		concurrentStackH = new ConcurrentStackHandlerImpl[concurrentStackHPoolSize];
		
		
		/*compositeConcurrentStackHAverageUse = UNUSED;
		compositeConcurrentStackHPoolSize = 5;
		compositeConcurrentStackHFree = 0;
		compositeConcurrentStackH = new CompositeConcurrentStackHandlerImpl[compositeConcurrentStackHPoolSize];*/
	}
	
	public static StackHandlerPool getInstance(MessageWriter debugWriter){
		if(instance == null){
			synchronized(StackHandlerPool.class){
				if(instance == null){
					instance = new StackHandlerPool(debugWriter); 
				}
			}
		}
		return instance;
	}
	
	public synchronized ActiveModelStackHandlerPool getActiveModelStackHandlerPool(){
		if(vshPoolFree == 0){
			ActiveModelStackHandlerPool vshp = new ActiveModelStackHandlerPool(this, debugWriter);
			return vshp;
		}else{
			ActiveModelStackHandlerPool vshp = vshPools[--vshPoolFree];
			return vshp;
		}
	}
		
	public synchronized void recycle(ActiveModelStackHandlerPool vshp){
		if(vshPoolFree == vshPoolPoolSize){
			ActiveModelStackHandlerPool[] increased = new ActiveModelStackHandlerPool[++vshPoolPoolSize];
			System.arraycopy(vshPools, 0, increased, 0, vshPoolFree);
			vshPools = increased;
		}
		vshPools[vshPoolFree++] = vshp;
	}	
	
	
	synchronized void fill(ActiveModelStackHandlerPool stackHandlerPool,
							ContextStackHandler[] contextStackH,
							MinimalReduceStackHandler[] minimalReduceStackH,
							MaximalReduceStackHandler[] maximalReduceStackH,
							CandidateStackHandlerImpl[] candidateStackH,
							ConcurrentStackHandlerImpl[] concurrentStackH/*,
							CompositeConcurrentStackHandlerImpl[] compositeConcurrentStackH*/){
		int contextStackHFillCount;		
		if(contextStackH == null || contextStackH.length < contextStackHAverageUse)
			contextStackH = new ContextStackHandler[contextStackHAverageUse];
		if(contextStackHFree > contextStackHAverageUse){
			contextStackHFillCount = contextStackHAverageUse;
			contextStackHFree = contextStackHFree - contextStackHAverageUse;
		}else{
			contextStackHFillCount = contextStackHFree;
			contextStackHFree = 0;
		}
		System.arraycopy(this.contextStackH, contextStackHFree,
							contextStackH, 0, contextStackHFillCount);
		
		int minimalReduceStackHFillCount;		
		if(minimalReduceStackH == null || minimalReduceStackH.length < minimalReduceStackHAverageUse)
			minimalReduceStackH = new MinimalReduceStackHandler[minimalReduceStackHAverageUse];
		if(minimalReduceStackHFree > minimalReduceStackHAverageUse){
			minimalReduceStackHFillCount = minimalReduceStackHAverageUse;
			minimalReduceStackHFree = minimalReduceStackHFree - minimalReduceStackHAverageUse;
		}else{
			minimalReduceStackHFillCount = minimalReduceStackHFree;
			minimalReduceStackHFree = 0;
		}
		System.arraycopy(this.minimalReduceStackH, minimalReduceStackHFree,
							minimalReduceStackH, 0, minimalReduceStackHFillCount);
		
		int maximalReduceStackHFillCount;		
		if(maximalReduceStackH == null || maximalReduceStackH.length < maximalReduceStackHAverageUse)
			maximalReduceStackH = new MaximalReduceStackHandler[maximalReduceStackHAverageUse];
		if(maximalReduceStackHFree > maximalReduceStackHAverageUse){
			maximalReduceStackHFillCount = maximalReduceStackHAverageUse;
			maximalReduceStackHFree = maximalReduceStackHFree - maximalReduceStackHAverageUse;
		}else{
			maximalReduceStackHFillCount = maximalReduceStackHFree;
			maximalReduceStackHFree = 0;
		}
		System.arraycopy(this.maximalReduceStackH, maximalReduceStackHFree,
							maximalReduceStackH, 0, maximalReduceStackHFillCount);
		
		
		int candidateStackHFillCount;		
		if(candidateStackH == null || candidateStackH.length < candidateStackHAverageUse)
			candidateStackH = new CandidateStackHandlerImpl[candidateStackHAverageUse];
		if(candidateStackHFree > candidateStackHAverageUse){
			candidateStackHFillCount = candidateStackHAverageUse;
			candidateStackHFree = candidateStackHFree - candidateStackHAverageUse;
		}else{
			candidateStackHFillCount = candidateStackHFree;
			candidateStackHFree = 0;
		}
		System.arraycopy(this.candidateStackH, candidateStackHFree,
							candidateStackH, 0, candidateStackHFillCount);
		
		
		int concurrentStackHFillCount;		
		if(concurrentStackH == null || concurrentStackH.length < concurrentStackHAverageUse)
			concurrentStackH = new ConcurrentStackHandlerImpl[concurrentStackHAverageUse];
		if(concurrentStackHFree > concurrentStackHAverageUse){
			concurrentStackHFillCount = concurrentStackHAverageUse;
			concurrentStackHFree = concurrentStackHFree - concurrentStackHAverageUse;
		}else{
			concurrentStackHFillCount = concurrentStackHFree;
			concurrentStackHFree = 0;
		}
		System.arraycopy(this.concurrentStackH, concurrentStackHFree,
							concurrentStackH, 0, concurrentStackHFillCount);
		
		/*
		int compositeConcurrentStackHFillCount;		
		if(compositeConcurrentStackH == null || compositeConcurrentStackH.length < compositeConcurrentStackHAverageUse)
			compositeConcurrentStackH = new CompositeConcurrentStackHandlerImpl[compositeConcurrentStackHAverageUse];
		if(compositeConcurrentStackHFree > compositeConcurrentStackHAverageUse){
			compositeConcurrentStackHFillCount = compositeConcurrentStackHAverageUse;
			compositeConcurrentStackHFree = compositeConcurrentStackHFree - compositeConcurrentStackHAverageUse;
		}else{
			compositeConcurrentStackHFillCount = compositeConcurrentStackHFree;
			compositeConcurrentStackHFree = 0;
		}
		System.arraycopy(this.compositeConcurrentStackH, compositeConcurrentStackHFree,
							compositeConcurrentStackH, 0, compositeConcurrentStackHFillCount);
		*/
		stackHandlerPool.setHandlers(contextStackHFillCount,
									contextStackH,
									minimalReduceStackHFillCount,
									minimalReduceStackH,
									maximalReduceStackHFillCount,
									maximalReduceStackH,
									candidateStackHFillCount,
									candidateStackH,
									concurrentStackHFillCount,
									concurrentStackH/*,
									compositeConcurrentStackHFillCount,
									compositeConcurrentStackH*/);
	}
	
	synchronized void recycle(int contextStackHAverageUse,
					ContextStackHandler[] contextStackH,
					int minimalReduceStackHAverageUse,
					MinimalReduceStackHandler[] minimalReduceStackH,
					int maximalReduceStackHAverageUse,
					MaximalReduceStackHandler[] maximalReduceStackH,
					int candidateStackHAverageUse,
					CandidateStackHandlerImpl[] candidateStackH,
					int concurrentStackHAverageUse,
					ConcurrentStackHandlerImpl[] concurrentStackH/*,
					int compositeConcurrentStackHAverageUse,
					CompositeConcurrentStackHandlerImpl[] compositeConcurrentStackH*/){
		if(contextStackHFree + contextStackHAverageUse >= contextStackHPoolSize){			 
			contextStackHPoolSize+= contextStackHAverageUse;
			ContextStackHandler[] increased = new ContextStackHandler[contextStackHPoolSize];
			System.arraycopy(this.contextStackH, 0, increased, 0, contextStackHFree);
			this.contextStackH = increased;
		}
		System.arraycopy(contextStackH, 0, this.contextStackH, contextStackHFree, contextStackHAverageUse);
		contextStackHFree += contextStackHAverageUse;
		if(this.contextStackHAverageUse != 0) this.contextStackHAverageUse = (this.contextStackHAverageUse + contextStackHAverageUse)/2;
		else this.contextStackHAverageUse = contextStackHAverageUse;
		// System.out.println("contextStackH "+this.contextStackHAverageUse);
		
		
		if(minimalReduceStackHFree + minimalReduceStackHAverageUse >= minimalReduceStackHPoolSize){			 
			minimalReduceStackHPoolSize+= minimalReduceStackHAverageUse;
			MinimalReduceStackHandler[] increased = new MinimalReduceStackHandler[minimalReduceStackHPoolSize];
			System.arraycopy(this.minimalReduceStackH, 0, increased, 0, minimalReduceStackHFree);
			this.minimalReduceStackH = increased;
		}
		System.arraycopy(minimalReduceStackH, 0, this.minimalReduceStackH, minimalReduceStackHFree, minimalReduceStackHAverageUse);
		minimalReduceStackHFree += minimalReduceStackHAverageUse;
		if(this.minimalReduceStackHAverageUse != 0) this.minimalReduceStackHAverageUse = (this.minimalReduceStackHAverageUse + minimalReduceStackHAverageUse)/2;
		else this.minimalReduceStackHAverageUse = minimalReduceStackHAverageUse;
		// System.out.println("minimalReduceStackH "+this.minimalReduceStackHAverageUse);
		
		
		if(maximalReduceStackHFree + maximalReduceStackHAverageUse >= maximalReduceStackHPoolSize){			 
			maximalReduceStackHPoolSize+= maximalReduceStackHAverageUse;
			MaximalReduceStackHandler[] increased = new MaximalReduceStackHandler[maximalReduceStackHPoolSize];
			System.arraycopy(this.maximalReduceStackH, 0, increased, 0, maximalReduceStackHFree);
			this.maximalReduceStackH = increased;
		}
		System.arraycopy(maximalReduceStackH, 0, this.maximalReduceStackH, maximalReduceStackHFree, maximalReduceStackHAverageUse);
		maximalReduceStackHFree += maximalReduceStackHAverageUse;
		if(this.maximalReduceStackHAverageUse != 0) this.maximalReduceStackHAverageUse = (this.maximalReduceStackHAverageUse + maximalReduceStackHAverageUse)/2;
		else this.maximalReduceStackHAverageUse = maximalReduceStackHAverageUse;
		// System.out.println("maximalReduceStackH "+this.maximalReduceStackHAverageUse);
		
		
		if(candidateStackHFree + candidateStackHAverageUse >= candidateStackHPoolSize){			 
			candidateStackHPoolSize+= candidateStackHAverageUse;
			CandidateStackHandlerImpl[] increased = new CandidateStackHandlerImpl[candidateStackHPoolSize];
			System.arraycopy(this.candidateStackH, 0, increased, 0, candidateStackHFree);
			this.candidateStackH = increased;
		}
		System.arraycopy(candidateStackH, 0, this.candidateStackH, candidateStackHFree, candidateStackHAverageUse);
		candidateStackHFree += candidateStackHAverageUse;
		if(this.candidateStackHAverageUse != 0)this.candidateStackHAverageUse = (this.candidateStackHAverageUse + candidateStackHAverageUse)/2;
		else this.candidateStackHAverageUse = candidateStackHAverageUse;
		// System.out.println("candidateStackH "+this.candidateStackHAverageUse);
		
		
		if(concurrentStackHFree + concurrentStackHAverageUse >= concurrentStackHPoolSize){			 
			concurrentStackHPoolSize+= concurrentStackHAverageUse;
			ConcurrentStackHandlerImpl[] increased = new ConcurrentStackHandlerImpl[concurrentStackHPoolSize];
			System.arraycopy(this.concurrentStackH, 0, increased, 0, concurrentStackHFree);
			this.concurrentStackH = increased;
		}
		System.arraycopy(concurrentStackH, 0, this.concurrentStackH, concurrentStackHFree, concurrentStackHAverageUse);
		concurrentStackHFree += concurrentStackHAverageUse;
		if(this.concurrentStackHAverageUse != 0)this.concurrentStackHAverageUse = (this.concurrentStackHAverageUse + concurrentStackHAverageUse)/2;
		else this.concurrentStackHAverageUse = concurrentStackHAverageUse;
		// System.out.println("concurrentStackH "+this.concurrentStackHAverageUse);
		
		/*
		if(compositeConcurrentStackHFree + compositeConcurrentStackHAverageUse >= compositeConcurrentStackHPoolSize){			 
			compositeConcurrentStackHPoolSize+= compositeConcurrentStackHAverageUse;
			CompositeConcurrentStackHandlerImpl[] increased = new CompositeConcurrentStackHandlerImpl[compositeConcurrentStackHPoolSize];
			System.arraycopy(this.compositeConcurrentStackH, 0, increased, 0, compositeConcurrentStackHFree);
			this.compositeConcurrentStackH = increased;
		}
		System.arraycopy(compositeConcurrentStackH, 0, this.compositeConcurrentStackH, compositeConcurrentStackHFree, compositeConcurrentStackHAverageUse);
		compositeConcurrentStackHFree += compositeConcurrentStackHAverageUse;
		if(this.compositeConcurrentStackHAverageUse != 0)this.compositeConcurrentStackHAverageUse = (this.compositeConcurrentStackHAverageUse + compositeConcurrentStackHAverageUse)/2;
		else this.compositeConcurrentStackHAverageUse = compositeConcurrentStackHAverageUse;
		// System.out.println("compositeConcurrentStackH "+this.compositeConcurrentStackHAverageUse);*/
	}
} 