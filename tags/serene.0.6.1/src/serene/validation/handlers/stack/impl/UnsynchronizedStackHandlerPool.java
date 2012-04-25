/*
Copyright 2012 Radu Cernuta 

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

public class UnsynchronizedStackHandlerPool extends StackHandlerPool{
	UnsynchronizedStackHandlerPool(){
		super();
	}
	
	public static UnsynchronizedStackHandlerPool getInstance(){
		return new UnsynchronizedStackHandlerPool();
	}
	
	public ActiveModelStackHandlerPool getActiveModelStackHandlerPool(){
		return new ActiveModelStackHandlerPool(null);
	}
		
	public void recycle(ActiveModelStackHandlerPool amshp){
	    throw new IllegalStateException();
	}	
	
	
	void fill(ActiveModelStackHandlerPool stackHandlerPool,
							ContextStackHandler[] contextStackHToFill,
							MinimalReduceStackHandler[] minimalReduceStackHToFill,
							MaximalReduceStackHandler[] maximalReduceStackHToFill,
							CandidateStackHandlerImpl[] candidateStackHToFill,
							ConcurrentStackHandlerImpl[] concurrentStackHToFill){
	    throw new IllegalStateException();
	}
	
	synchronized void recycle(int contextStackHRecycledCount,
	                int contextStackHEffectivellyUsed,
					ContextStackHandler[] contextStackHRecycled,
					int minimalReduceStackHRecycledCount,
					int minimalReduceStackHEffectivellyUsed,
					MinimalReduceStackHandler[] minimalReduceStackHRecycled,
					int maximalReduceStackHRecycledCount,
					int maximalReduceStackHEffectivellyUsed,
					MaximalReduceStackHandler[] maximalReduceStackHRecycled,
					int candidateStackHRecycledCount,
					int candidateStackHEffectivellyUsed,
					CandidateStackHandlerImpl[] candidateStackHRecycled,
					int concurrentStackHRecycledCount,
					int concurrentStackHEffectivellyUsed,
					ConcurrentStackHandlerImpl[] concurrentStackHRecycled){
	    throw new IllegalStateException();
	}
} 
