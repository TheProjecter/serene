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

package serene.validation.handlers.structure;

public class UnsynchronizedRuleHandlerPool extends RuleHandlerPool{	
	UnsynchronizedRuleHandlerPool(){
		super();
	}
	
	public static UnsynchronizedRuleHandlerPool getInstance(){
		return new UnsynchronizedRuleHandlerPool();
	}
	
	public ValidatorRuleHandlerPool getValidatorRuleHandlerPool(){
		return new ValidatorRuleHandlerPool(null);
	}
		
	public void recycle(ValidatorRuleHandlerPool amrhp){
	    throw new IllegalStateException();
	}	
	
	void fill(ValidatorRuleHandlerPool ruleHandlerPool,
				ParticleHandler[] particleHandlerToFill,
				ChoiceHandler[] choiceHandlerToFill,
				GroupHandler[] groupHandlerToFill,
				IntermediaryPatternHandler[] intermediaryPatternHandlerToFill,
				UInterleaveHandler[] uinterleaveHandlerToFill,
				MInterleaveHandler[] minterleaveHandlerToFill,
				SInterleaveHandler[] sinterleaveHandlerToFill,
				TypeHandler[] typeHandlerToFill,
				GroupDoubleHandler[] groupDoubleHandlerToFill,
				InterleaveDoubleHandler[] interleaveDoubleHandlerToFill,
				GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandlerToFill,
				GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandlerToFill,
				InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandlerToFill,
				InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandlerToFill,
				IntermediaryPatternMinimalReduceHandler[] intermediaryPatternMinimalReduceHandlerToFill,
				IntermediaryPatternMaximalReduceHandler[] intermediaryPatternMaximalReduceHandlerToFill,
				ChoiceMinimalReduceHandler[] choiceMinimalReduceHandlerToFill,
				ChoiceMaximalReduceHandler[] choiceMaximalReduceHandlerToFill,
				GroupMinimalReduceHandler[] groupMinimalReduceHandlerToFill,
				GroupMaximalReduceHandler[] groupMaximalReduceHandlerToFill,
				InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandlerToFill,
				InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandlerToFill){	    
		throw new IllegalStateException();
	}	
	void recycle(int particleHandlerRecycledCount,
	                        int particleHandlerEffectivellyUsed,
							ParticleHandler[] particleHandlerRecycled,
							int choiceHandlerRecycledCount,
							int choiceHandlerEffectivellyUsed,
							ChoiceHandler[] choiceHandlerRecycled,
							int groupHandlerRecycledCount,
							int groupHandlerEffectivellyUsed,
							GroupHandler[] groupHandlerRecycled,
							int intermediaryPatternHandlerRecycledCount,
							int intermediaryPatternHandlerEffectivellyUsed,
							IntermediaryPatternHandler[] intermediaryPatternHandlerRecycled,
							int uinterleaveHandlerRecycledCount,
							int uinterleaveHandlerEffectivellyUsed,
							UInterleaveHandler[] uinterleaveHandlerRecycled,
							int minterleaveHandlerRecycledCount,
							int minterleaveHandlerEffectivellyUsed,
							MInterleaveHandler[] minterleaveHandlerRecycled,
							int sinterleaveHandlerRecycledCount,
							int sinterleaveHandlerEffectivellyUsed,
							SInterleaveHandler[] sinterleaveHandlerRecycled,
							int typeHandlerRecycledCount,
							int typeHandlerEffectivellyUsed,
							TypeHandler[] typeHandlerRecycled,
							int groupDoubleHandlerRecycledCount,
							int groupDoubleHandlerEffectivellyUsed,
							GroupDoubleHandler[] groupDoubleHandlerRecycled,
							int interleaveDoubleHandlerRecycledCount,
							int interleaveDoubleHandlerEffectivellyUsed,
							InterleaveDoubleHandler[] interleaveDoubleHandlerRecycled,
							int groupMinimalReduceCountHandlerRecycledCount,
							int groupMinimalReduceCountHandlerEffectivellyUsed,
							GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandlerRecycled,
							int groupMaximalReduceCountHandlerRecycledCount,
							int groupMaximalReduceCountHandlerEffectivellyUsed,
							GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandlerRecycled,
							int interleaveMinimalReduceCountHandlerRecycledCount,
							int interleaveMinimalReduceCountHandlerEffectivellyUsed,
							InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandlerRecycled,
							int interleaveMaximalReduceCountHandlerRecycledCount,
							int interleaveMaximalReduceCountHandlerEffectivellyUsed,
							InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandlerRecycled,
							int intermediaryPatternMinimalReduceHandlerRecycledCount,
							int intermediaryPatternMinimalReduceHandlerEffectivellyUsed,
							IntermediaryPatternMinimalReduceHandler[] intermediaryPatternMinimalReduceHandlerRecycled,
							int intermediaryPatternMaximalReduceHandlerRecycledCount,
							int intermediaryPatternMaximalReduceHandlerEffectivellyUsed,
							IntermediaryPatternMaximalReduceHandler[] intermediaryPatternMaximalReduceHandlerRecycled,
							int choiceMinimalReduceHandlerRecycledCount,
							int choiceMinimalReduceHandlerEffectivellyUsed,
							ChoiceMinimalReduceHandler[] choiceMinimalReduceHandlerRecycled,
							int choiceMaximalReduceHandlerRecycledCount,
							int choiceMaximalReduceHandlerEffectivellyUsed,
							ChoiceMaximalReduceHandler[] choiceMaximalReduceHandlerRecycled,
							int groupMinimalReduceHandlerRecycledCount,
							int groupMinimalReduceHandlerEffectivellyUsed,
							GroupMinimalReduceHandler[] groupMinimalReduceHandlerRecycled,
							int groupMaximalReduceHandlerRecycledCount,
							int groupMaximalReduceHandlerEffectivellyUsed,
							GroupMaximalReduceHandler[] groupMaximalReduceHandlerRecycled,
							int interleaveMinimalReduceHandlerRecycledCount,
							int interleaveMinimalReduceHandlerEffectivellyUsed,
							InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandlerRecycled,
							int interleaveMaximalReduceHandlerRecycledCount,
							int interleaveMaximalReduceHandlerEffectivellyUsed,
							InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandlerRecycled){
	    throw new IllegalStateException();
	}
} 
