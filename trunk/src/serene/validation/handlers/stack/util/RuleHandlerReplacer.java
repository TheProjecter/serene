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

package serene.validation.handlers.stack.util;

import serene.validation.handlers.structure.RuleHandlerVisitor;
import serene.validation.handlers.structure.StructureHandler;
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

import serene.validation.handlers.conflict.StackConflictsHandler;

public class RuleHandlerReplacer implements RuleHandlerVisitor{
	StackConflictsHandler stackConflictsHandler;
		
	public RuleHandlerReplacer(){
	}
	
	public void replaceHandlers(StackConflictsHandler stackConflictsHandler, StructureHandler topHandler){
		this.stackConflictsHandler = stackConflictsHandler;
		next(topHandler);
	}
	
	public void visit(TypeHandler handler){
		StructureHandler sh = handler.getStructureHandler();
		if(sh != null) next(sh);
		ParticleHandler ph = handler.getParticleHandler();
		if(ph != null) next(ph);
	}
	public void visit(ChoiceHandler handler){
		StructureHandler sh = handler.getStructureHandler();
		if(sh != null) next(sh);
		ParticleHandler ph = handler.getParticleHandler();
		if(ph != null) next(ph);
	}
	public void visit(ChoiceMinimalReduceHandler handler){
		StructureHandler sh = handler.getStructureHandler();
		if(sh != null) next(sh);
		ParticleHandler ph = handler.getParticleHandler();
		if(ph != null) next(ph);
	}
	public void visit(ChoiceMaximalReduceHandler handler){
		StructureHandler sh = handler.getStructureHandler();
		if(sh != null) next(sh);
		ParticleHandler ph = handler.getParticleHandler();
		if(ph != null) next(ph);
	}
	public void visit(GroupDoubleHandler handler){
		throw new UnsupportedOperationException();
	}
	public void visit(GroupHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(GroupMinimalReduceHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(GroupMaximalReduceHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(GroupMinimalReduceCountHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(GroupMaximalReduceCountHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(InterleaveDoubleHandler handler){
		throw new UnsupportedOperationException();
	}
	public void visit(InterleaveMinimalReduceHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(InterleaveMaximalReduceHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(InterleaveMinimalReduceCountHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(InterleaveMaximalReduceCountHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(MInterleaveHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(ParticleHandler handler){
	
	}
	public void visit(IntermediaryPatternHandler handler){
		StructureHandler sh = handler.getStructureHandler();
		if(sh != null) next(sh);
		ParticleHandler ph = handler.getParticleHandler();
		if(ph != null) next(ph);
	}
	public void visit(IntermediaryPatternMinimalReduceHandler handler){
		StructureHandler sh = handler.getStructureHandler();
		if(sh != null) next(sh);
		ParticleHandler ph = handler.getParticleHandler();
		if(ph != null) next(ph);
	}
	public void visit(IntermediaryPatternMaximalReduceHandler handler){
		StructureHandler sh = handler.getStructureHandler();
		if(sh != null) next(sh);
		ParticleHandler ph = handler.getParticleHandler();
		if(ph != null) next(ph);
	}
	public void visit(UInterleaveHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}
	public void visit(SInterleaveHandler handler){
		StructureHandler[] sh = handler.getStructureHandlers();
		if(sh != null) next(sh);
		ParticleHandler[] ph = handler.getParticleHandlers();
		if(ph != null) next(ph);
	}

	void next(StructureHandler sh){
		stackConflictsHandler.replace(sh);
		sh.accept(this);
	}

	void next(StructureHandler[] shs){
		for(StructureHandler sh : shs){
			if(sh != null){
				stackConflictsHandler.replace(sh);
				sh.accept(this);
			}
		}
	}

	void next(ParticleHandler ph){
		stackConflictsHandler.replace(ph);
		//ph.setStackConflictsHandler(stackConflictsHandler);
	}

	void next(ParticleHandler[] phs){
		for(ParticleHandler ph : phs){
			if(ph != null){
				stackConflictsHandler.replace(ph);
				//ph.setStackConflictsHandler(stackConflictsHandler);
			}
		}
	}

	public String toString(){
		return "RuleHandlerReplacer ";
	}	
}