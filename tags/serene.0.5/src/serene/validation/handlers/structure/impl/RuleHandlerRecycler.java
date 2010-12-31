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

interface RuleHandlerRecycler{

	void recycle(ParticleHandler handler);
	void recycle(AttributeHandler handler);
	void recycle(ElementHandler handler);
	void recycle(ChoiceHandler handler);
	void recycle(GroupHandler handler);
	void recycle(UInterleaveHandler handler);
	void recycle(MInterleaveHandler handler);
	void recycle(SInterleaveHandler handler);
	void recycle(RefHandler handler);
	void recycle(GrammarHandler handler);
	void recycle(ExceptPatternHandler handler);
	void recycle(ListPatternHandler handler);
	
	
	void recycle(GroupDoubleHandler handler);
	void recycle(InterleaveDoubleHandler handler);
	
	void recycle(GroupMinimalReduceCountHandler handler);
	void recycle(GroupMaximalReduceCountHandler handler);
	void recycle(InterleaveMinimalReduceCountHandler handler);
	void recycle(InterleaveMaximalReduceCountHandler handler);
	
		
	void recycle(GrammarMinimalReduceHandler handler);
	void recycle(GrammarMaximalReduceHandler handler);
	void recycle(RefMinimalReduceHandler handler);
	void recycle(RefMaximalReduceHandler handler);
	void recycle(ChoiceMinimalReduceHandler handler);
	void recycle(ChoiceMaximalReduceHandler handler);
	
	void recycle(GroupMinimalReduceHandler handler);
	void recycle(GroupMaximalReduceHandler handler);
	void recycle(InterleaveMinimalReduceHandler handler);
	void recycle(InterleaveMaximalReduceHandler handler);
	
}