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

import serene.validation.handlers.structure.TypeHandler;
import serene.validation.handlers.structure.ChoiceHandler;
import serene.validation.handlers.structure.ChoiceMinimalReduceHandler;
import serene.validation.handlers.structure.ChoiceMaximalReduceHandler;
import serene.validation.handlers.structure.GroupDoubleHandler;
import serene.validation.handlers.structure.GroupHandler;
import serene.validation.handlers.structure.GroupMinimalReduceHandler;
import serene.validation.handlers.structure.GroupMaximalReduceHandler;
import serene.validation.handlers.structure.GroupMinimalReduceCountHandler;
import serene.validation.handlers.structure.GroupMaximalReduceCountHandler;
import serene.validation.handlers.structure.InterleaveDoubleHandler;
import serene.validation.handlers.structure.InterleaveMinimalReduceHandler;
import serene.validation.handlers.structure.InterleaveMaximalReduceHandler;
import serene.validation.handlers.structure.InterleaveMinimalReduceCountHandler;
import serene.validation.handlers.structure.InterleaveMaximalReduceCountHandler;
import serene.validation.handlers.structure.MInterleaveHandler;
import serene.validation.handlers.structure.ParticleHandler;
import serene.validation.handlers.structure.IntermediaryPatternHandler;
import serene.validation.handlers.structure.IntermediaryPatternMinimalReduceHandler;
import serene.validation.handlers.structure.IntermediaryPatternMaximalReduceHandler;
import serene.validation.handlers.structure.UInterleaveHandler;
import serene.validation.handlers.structure.SInterleaveHandler;


public interface RuleHandlerVisitor{
	
	void visit(TypeHandler handler);
	void visit(ChoiceHandler handler);
	void visit(ChoiceMinimalReduceHandler handler);
	void visit(ChoiceMaximalReduceHandler handler);
	void visit(GroupDoubleHandler handler);
	void visit(GroupHandler handler);
	void visit(GroupMinimalReduceHandler handler);
	void visit(GroupMaximalReduceHandler handler);
	void visit(GroupMinimalReduceCountHandler handler);
	void visit(GroupMaximalReduceCountHandler handler);
	void visit(InterleaveDoubleHandler handler);
	void visit(InterleaveMinimalReduceHandler handler);
	void visit(InterleaveMaximalReduceHandler handler);
	void visit(InterleaveMinimalReduceCountHandler handler);
	void visit(InterleaveMaximalReduceCountHandler handler);
	void visit(MInterleaveHandler handler);
	void visit(ParticleHandler handler);
	void visit(IntermediaryPatternHandler handler);
	void visit(IntermediaryPatternMinimalReduceHandler handler);
	void visit(IntermediaryPatternMaximalReduceHandler handler);
	void visit(UInterleaveHandler handler);
	void visit(SInterleaveHandler handler);

}