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

import serene.validation.handlers.structure.impl.AttributeHandler;
import serene.validation.handlers.structure.impl.ChoiceHandler;
import serene.validation.handlers.structure.impl.ChoiceMinimalReduceHandler;
import serene.validation.handlers.structure.impl.ChoiceMaximalReduceHandler;
import serene.validation.handlers.structure.impl.ElementHandler;
import serene.validation.handlers.structure.impl.ExceptPatternHandler;
import serene.validation.handlers.structure.impl.GrammarHandler;
import serene.validation.handlers.structure.impl.GrammarMinimalReduceHandler;
import serene.validation.handlers.structure.impl.GrammarMaximalReduceHandler;
import serene.validation.handlers.structure.impl.GroupDoubleHandler;
import serene.validation.handlers.structure.impl.GroupHandler;
import serene.validation.handlers.structure.impl.GroupMinimalReduceHandler;
import serene.validation.handlers.structure.impl.GroupMaximalReduceHandler;
import serene.validation.handlers.structure.impl.GroupMinimalReduceCountHandler;
import serene.validation.handlers.structure.impl.GroupMaximalReduceCountHandler;
import serene.validation.handlers.structure.impl.InterleaveDoubleHandler;
import serene.validation.handlers.structure.impl.InterleaveMinimalReduceHandler;
import serene.validation.handlers.structure.impl.InterleaveMaximalReduceHandler;
import serene.validation.handlers.structure.impl.InterleaveMinimalReduceCountHandler;
import serene.validation.handlers.structure.impl.InterleaveMaximalReduceCountHandler;
import serene.validation.handlers.structure.impl.ListPatternHandler;
import serene.validation.handlers.structure.impl.MInterleaveHandler;
import serene.validation.handlers.structure.impl.ParticleHandler;
import serene.validation.handlers.structure.impl.RefHandler;
import serene.validation.handlers.structure.impl.RefMinimalReduceHandler;
import serene.validation.handlers.structure.impl.RefMaximalReduceHandler;
import serene.validation.handlers.structure.impl.UInterleaveHandler;
import serene.validation.handlers.structure.impl.SInterleaveHandler;


public interface RuleHandlerVisitor{
	
	void visit(AttributeHandler handler);
	void visit(ChoiceHandler handler);
	void visit(ChoiceMinimalReduceHandler handler);
	void visit(ChoiceMaximalReduceHandler handler);
	void visit(ElementHandler handler);
	void visit(ExceptPatternHandler handler);
	void visit(GrammarHandler handler);
	void visit(GrammarMinimalReduceHandler handler);
	void visit(GrammarMaximalReduceHandler handler);
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
	void visit(ListPatternHandler handler);
	void visit(MInterleaveHandler handler);
	void visit(ParticleHandler handler);
	void visit(RefHandler handler);
	void visit(RefMinimalReduceHandler handler);
	void visit(RefMaximalReduceHandler handler);
	void visit(UInterleaveHandler handler);
	void visit(SInterleaveHandler handler);

}