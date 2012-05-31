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

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AEmpty;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.ANotAllowed;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AGrammar;


import serene.validation.schema.active.ActiveComponentVisitor;

import serene.validation.handlers.structure.StructureHandler;

public class PathHandler implements ActiveComponentVisitor{
	StructureHandler topHandler;
	Rule topRule;
	StructureHandler currentHandler;
	
	StructureHandler bottomHandler;	
	int expectedOrderHandlingCount;	

	public PathHandler(){				
	}
	
	public void init(StructureHandler topHandler){
		this.topHandler = topHandler;
		this.topRule = topHandler.getRule();
		//System.out.println("init pathHandler "+topRule+"  "+hashCode());
	}
	
	public void activatePath(StructureHandler currentHandler, Rule pattern){
		expectedOrderHandlingCount = 0;
		this.currentHandler = currentHandler;
		pattern.accept(this);
	}
	
	public StructureHandler getBottomHandler(){
		return bottomHandler;
	}
	
	public int getExpectedOrderHandlingCount(){
		return expectedOrderHandlingCount;
	}	
	
	
	// TODO	
	public void visit(AExceptPattern except){
		if(except == topRule){
			bottomHandler = topHandler;
		}else{
			throw new IllegalStateException();
		}
	}
	
	
	public void visit(AListPattern list){
		if(list == topRule){
			bottomHandler = topHandler;
		}else{
			throw new IllegalStateException();
		}

	}
	public void visit(AEmpty component){}
	public void visit(AText component){}
	public void visit(ANotAllowed component){}	
	public void visit(AValue component){}
	public void visit(AData data){
		if(data == topRule){
			bottomHandler = topHandler;
			return;
		}
		StructureHandler dataHandler = currentHandler.getAncestorOrSelfHandler(data);
		if(dataHandler != null){
			bottomHandler = dataHandler;
			return;
		}	
		data.getParent().accept(this);
		if(bottomHandler!= null)bottomHandler = bottomHandler.getChildHandler(data);
	}
	

	public void visit(AElement element){
		if(element == topRule){
			bottomHandler = topHandler;
		}else{
			throw new IllegalStateException();
		}		
	}

	public void visit(AAttribute attribute){
		if(attribute == topRule){
			bottomHandler = topHandler;
		}else{
			throw new IllegalStateException();
		}	
	}	
	public void visit(AChoicePattern choice){
		if(choice == topRule){
			bottomHandler = topHandler;
			return;
		}
		StructureHandler choiceHandler = currentHandler.getAncestorOrSelfHandler(choice);
		if(choiceHandler != null){
			bottomHandler = choiceHandler;
			return;
		}	
		choice.getParent().accept(this);
		if(bottomHandler!= null)bottomHandler = bottomHandler.getChildHandler(choice);
	}
	
	public void visit(AInterleave interleave){
		if(interleave == topRule){
			bottomHandler = topHandler;
			return;
		}
		StructureHandler interleaveHandler = currentHandler.getAncestorOrSelfHandler(interleave);
		if(interleaveHandler != null){
			bottomHandler = interleaveHandler;
			return;
		}	
		interleave.getParent().accept(this);
		if(bottomHandler!= null)bottomHandler = bottomHandler.getChildHandler(interleave);
	}
	
	public void visit(AGroup group){
		expectedOrderHandlingCount++;
		if(group == topRule){
			bottomHandler = topHandler;
			return;
		}
		StructureHandler groupHandler = currentHandler.getAncestorOrSelfHandler(group);
		if(groupHandler != null){
			bottomHandler = groupHandler;
			return;
		}
		group.getParent().accept(this);
		if(bottomHandler!= null)bottomHandler = bottomHandler.getChildHandler(group);
	}
	
	public void visit(ARef ref){		
		if(ref == topRule){
			bottomHandler = topHandler;
			return;
		}
		StructureHandler refHandler = currentHandler.getAncestorOrSelfHandler(ref);
		if(refHandler != null){
			bottomHandler = refHandler;
			return;
		}
		ref.getParent().accept(this);
		if(bottomHandler!= null)bottomHandler = bottomHandler.getChildHandler(ref);
	}
	
	public void visit(AGrammar grammar){
		if(grammar == topRule){
			bottomHandler = topHandler;
			return;
		}
		StructureHandler grammarHandler = currentHandler.getAncestorOrSelfHandler(grammar);
		if(grammarHandler != null){
			bottomHandler = grammarHandler;
			return;
		}
		grammar.getParent().accept(this);
		if(bottomHandler!= null)bottomHandler = bottomHandler.getChildHandler(grammar);
	}	
}