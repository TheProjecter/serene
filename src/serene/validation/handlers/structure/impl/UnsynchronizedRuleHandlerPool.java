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

package serene.validation.handlers.structure.impl;



import sereneWrite.MessageWriter;

public class UnsynchronizedRuleHandlerPool extends RuleHandlerPool{	
	UnsynchronizedRuleHandlerPool(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	public static UnsynchronizedRuleHandlerPool getInstance(MessageWriter debugWriter){
		return new UnsynchronizedRuleHandlerPool(debugWriter);
	}
	
	public ActiveModelRuleHandlerPool getActiveModelRuleHandlerPool(){
		return new ActiveModelRuleHandlerPool(null, debugWriter);
	}
		
	public void recycle(ActiveModelRuleHandlerPool amrhp){
	    throw new IllegalStateException();
	}	
	
	void fill(ActiveModelRuleHandlerPool ruleHandlerPool,
				ParticleHandler[] particleHandlerToFill,
				ChoiceHandler[] choiceHandlerToFill,
				GroupHandler[] groupHandlerToFill,
				GrammarHandler[] grammarHandlerToFill,
				RefHandler[] refHandlerToFill,
				UInterleaveHandler[] uinterleaveHandlerToFill,
				MInterleaveHandler[] minterleaveHandlerToFill,
				SInterleaveHandler[] sinterleaveHandlerToFill,
				ElementHandler[] elementHandlerToFill,
				AttributeHandler[] attributeHandlerToFill,
				ExceptPatternHandler[] exceptPatternHandlerToFill,
				ListPatternHandler[] listPatternHandlerToFill,
				GroupDoubleHandler[] groupDoubleHandlerToFill,
				InterleaveDoubleHandler[] interleaveDoubleHandlerToFill,
				GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandlerToFill,
				GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandlerToFill,
				InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandlerToFill,
				InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandlerToFill,				
				GrammarMinimalReduceHandler[] grammarMinimalReduceHandlerToFill,
				GrammarMaximalReduceHandler[] grammarMaximalReduceHandlerToFill,
				RefMinimalReduceHandler[] refMinimalReduceHandlerToFill,
				RefMaximalReduceHandler[] refMaximalReduceHandlerToFill,
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
							int grammarHandlerRecycledCount,
							int grammarHandlerEffectivellyUsed,
							GrammarHandler[] grammarHandlerRecycled,
							int refHandlerRecycledCount,
							int refHandlerEffectivellyUsed,
							RefHandler[] refHandlerRecycled,
							int uinterleaveHandlerRecycledCount,
							int uinterleaveHandlerEffectivellyUsed,
							UInterleaveHandler[] uinterleaveHandlerRecycled,
							int minterleaveHandlerRecycledCount,
							int minterleaveHandlerEffectivellyUsed,
							MInterleaveHandler[] minterleaveHandlerRecycled,
							int sinterleaveHandlerRecycledCount,
							int sinterleaveHandlerEffectivellyUsed,
							SInterleaveHandler[] sinterleaveHandlerRecycled,
							int elementHandlerRecycledCount,
							int elementHandlerEffectivellyUsed,
							ElementHandler[] elementHandlerRecycled,
							int attributeHandlerRecycledCount,
							int attributeHandlerEffectivellyUsed,
							AttributeHandler[] attributeHandlerRecycled,
							int exceptPatternHandlerRecycledCount,
							int exceptPatternHandlerEffectivellyUsed,
							ExceptPatternHandler[] exceptPatternHandlerRecycled,
							int listPatternHandlerRecycledCount,
							int listPatternHandlerEffectivellyUsed,
							ListPatternHandler[] listPatternHandlerRecycled,
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
							int grammarMinimalReduceHandlerRecycledCount,
							int grammarMinimalReduceHandlerEffectivellyUsed,
							GrammarMinimalReduceHandler[] grammarMinimalReduceHandlerRecycled,
							int grammarMaximalReduceHandlerRecycledCount,
							int grammarMaximalReduceHandlerEffectivellyUsed,
							GrammarMaximalReduceHandler[] grammarMaximalReduceHandlerRecycled,
							int refMinimalReduceHandlerRecycledCount,
							int refMinimalReduceHandlerEffectivellyUsed,
							RefMinimalReduceHandler[] refMinimalReduceHandlerRecycled,
							int refMaximalReduceHandlerRecycledCount,
							int refMaximalReduceHandlerEffectivellyUsed,
							RefMaximalReduceHandler[] refMaximalReduceHandlerRecycled,
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
