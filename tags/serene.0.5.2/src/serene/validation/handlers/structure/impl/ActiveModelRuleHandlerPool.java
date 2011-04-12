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

import serene.util.IntList;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.MultipleChildrenAPattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AGrammar;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AListPattern;


import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.ChildEventHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.util.ValidationItemLocator;

import org.relaxng.datatype.ValidationContext;

import serene.Reusable;

import sereneWrite.MessageWriter;

public class ActiveModelRuleHandlerPool implements Reusable, RuleHandlerRecycler{

	RuleHandlerPool pool;
	
	// int particleHandlerCreated;
	int particleHandlerPoolSize;
	int particleHandlerFree = 0;
	ParticleHandler[] particleHandler;
	
	// int choiceHandlerCreated;
	int choiceHandlerPoolSize;
	int choiceHandlerFree = 0;
	ChoiceHandler[] choiceHandler;
	
	// int groupHandlerCreated;
	int groupHandlerPoolSize;
	int groupHandlerFree = 0;
	GroupHandler[] groupHandler;
		
	// int grammarHandlerCreated;
	int grammarHandlerPoolSize;
	int grammarHandlerFree = 0;
	GrammarHandler[] grammarHandler;	
	
	// int refHandlerCreated;
	int refHandlerPoolSize;
	int refHandlerFree = 0;
	RefHandler[] refHandler;
		
	// int uinterleaveHandlerCreated;
	int uinterleaveHandlerPoolSize;
	int uinterleaveHandlerFree = 0;
	UInterleaveHandler[] uinterleaveHandler;
		
	// int minterleaveHandlerCreated;
	int minterleaveHandlerPoolSize;
	int minterleaveHandlerFree = 0;
	MInterleaveHandler[] minterleaveHandler;
	
	// int sinterleaveHandlerCreated;
	int sinterleaveHandlerPoolSize;
	int sinterleaveHandlerFree = 0;
	SInterleaveHandler[] sinterleaveHandler;
	
	// int elementHandlerCreated;
	int elementHandlerPoolSize;
	int elementHandlerFree = 0;
	ElementHandler[] elementHandler;
	
	// int attributeHandlerCreated;
	int attributeHandlerPoolSize;
	int attributeHandlerFree = 0;
	AttributeHandler[] attributeHandler;
	
	// int exceptPatternHandlerCreated;
	int exceptPatternHandlerPoolSize;
	int exceptPatternHandlerFree = 0;
	ExceptPatternHandler[] exceptPatternHandler;
	
	// int listPatternHandlerCreated;
	int listPatternHandlerPoolSize;
	int listPatternHandlerFree = 0;
	ListPatternHandler[] listPatternHandler;
	
	
	
	
	// int groupDoubleHandlerCreated;
	int groupDoubleHandlerPoolSize;
	int groupDoubleHandlerFree = 0;
	GroupDoubleHandler[] groupDoubleHandler;
	
	// int interleaveDoubleHandlerCreated;
	int interleaveDoubleHandlerPoolSize;
	int interleaveDoubleHandlerFree = 0;
	InterleaveDoubleHandler[] interleaveDoubleHandler;

	
	// int groupMinimalReduceCountHandlerCreated;
	int groupMinimalReduceCountHandlerPoolSize;
	int groupMinimalReduceCountHandlerFree = 0;
	GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandler;
	
	// int groupMaximalReduceCountHandlerCreated;
	int groupMaximalReduceCountHandlerPoolSize;
	int groupMaximalReduceCountHandlerFree = 0;
	GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandler;
	
	// int interleaveMinimalReduceCountHandlerCreated;
	int interleaveMinimalReduceCountHandlerPoolSize;
	int interleaveMinimalReduceCountHandlerFree = 0;
	InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandler;
	
	// int interleaveMaximalReduceCountHandlerCreated;
	int interleaveMaximalReduceCountHandlerPoolSize;
	int interleaveMaximalReduceCountHandlerFree = 0;
	InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandler;
	
	
	
	
	// int grammarMinimalReduceHandlerCreated;
	int grammarMinimalReduceHandlerPoolSize;
	int grammarMinimalReduceHandlerFree = 0;
	GrammarMinimalReduceHandler[] grammarMinimalReduceHandler;
	
	// int grammarMaximalReduceHandlerCreated;
	int grammarMaximalReduceHandlerPoolSize;
	int grammarMaximalReduceHandlerFree = 0;
	GrammarMaximalReduceHandler[] grammarMaximalReduceHandler;
	
	// int refMinimalReduceHandlerCreated;
	int refMinimalReduceHandlerPoolSize;
	int refMinimalReduceHandlerFree = 0;
	RefMinimalReduceHandler[] refMinimalReduceHandler;
	
	// int refMaximalReduceHandlerCreated;
	int refMaximalReduceHandlerPoolSize;
	int refMaximalReduceHandlerFree = 0;
	RefMaximalReduceHandler[] refMaximalReduceHandler;
		
	// int choiceMinimalReduceHandlerCreated;
	int choiceMinimalReduceHandlerPoolSize;
	int choiceMinimalReduceHandlerFree = 0;
	ChoiceMinimalReduceHandler[] choiceMinimalReduceHandler;
	
	// int choiceMaximalReduceHandlerCreated;
	int choiceMaximalReduceHandlerPoolSize;
	int choiceMaximalReduceHandlerFree = 0;
	ChoiceMaximalReduceHandler[] choiceMaximalReduceHandler;
	
	// int groupMinimalReduceHandlerCreated;
	int groupMinimalReduceHandlerPoolSize;
	int groupMinimalReduceHandlerFree = 0;
	GroupMinimalReduceHandler[] groupMinimalReduceHandler;
	
	// int groupMaximalReduceHandlerCreated;
	int groupMaximalReduceHandlerPoolSize;
	int groupMaximalReduceHandlerFree = 0;
	GroupMaximalReduceHandler[] groupMaximalReduceHandler;
	
	// int interleaveMinimalReduceHandlerCreated;
	int interleaveMinimalReduceHandlerPoolSize;
	int interleaveMinimalReduceHandlerFree = 0;
	InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandler;
	
	// int interleaveMaximalReduceHandlerCreated;
	int interleaveMaximalReduceHandlerPoolSize;
	int interleaveMaximalReduceHandlerFree = 0;
	InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandler;
	
	ValidationItemLocator validationItemLocator;
		
	MessageWriter debugWriter;
	
	public ActiveModelRuleHandlerPool(RuleHandlerPool pool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.pool = pool;
	}
	
	public void recycle(){
		if(particleHandlerFree != 0 ||
			choiceHandlerFree != 0 ||
			groupHandlerFree != 0 ||
			grammarHandlerFree != 0 ||
			refHandlerFree != 0 ||
			uinterleaveHandlerFree != 0 ||
			minterleaveHandlerFree != 0 ||
			sinterleaveHandlerFree != 0 ||
			elementHandlerFree != 0 ||
			attributeHandlerFree != 0 ||
			exceptPatternHandlerFree != 0 ||
			listPatternHandlerFree != 0 ||
			groupDoubleHandlerFree != 0 ||
			interleaveDoubleHandlerFree != 0 ||			
			groupMinimalReduceCountHandlerFree != 0 ||
			groupMaximalReduceCountHandlerFree != 0 ||
			interleaveMinimalReduceCountHandlerFree != 0 ||
			interleaveMaximalReduceCountHandlerFree != 0 ||
			grammarMinimalReduceHandlerFree != 0 ||
			grammarMaximalReduceHandlerFree != 0 ||
			refMinimalReduceHandlerFree != 0 ||
			refMaximalReduceHandlerFree != 0 ||
			choiceMinimalReduceHandlerFree != 0 ||
			choiceMaximalReduceHandlerFree != 0 ||
			groupMinimalReduceHandlerFree != 0 ||
			groupMaximalReduceHandlerFree != 0 ||
			interleaveMinimalReduceHandlerFree != 0 ||
			interleaveMaximalReduceHandlerFree != 0){
			releaseHandlers();	
		}
		pool.recycle(this);
	}
	public void fill(ValidationItemLocator validationItemLocator){
		this.validationItemLocator = validationItemLocator;
		 pool.fill(this,
			 		particleHandler,
					choiceHandler,
					groupHandler,
					grammarHandler,
					refHandler,
					uinterleaveHandler,
					minterleaveHandler,
					sinterleaveHandler,
					elementHandler,
					attributeHandler,
					exceptPatternHandler,
					listPatternHandler,
					groupDoubleHandler,
					interleaveDoubleHandler,
					groupMinimalReduceCountHandler,
					groupMaximalReduceCountHandler,
					interleaveMinimalReduceCountHandler,
					interleaveMaximalReduceCountHandler,
					grammarMinimalReduceHandler,
					grammarMaximalReduceHandler,
					refMinimalReduceHandler,
					refMaximalReduceHandler,
					choiceMinimalReduceHandler,
					choiceMaximalReduceHandler,
					groupMinimalReduceHandler,
					groupMaximalReduceHandler,
					interleaveMinimalReduceHandler,
					interleaveMaximalReduceHandler);				
	}
	
	void setHandlers(int particleHandlerFree,
					ParticleHandler[] particleHandler,
					int choiceHandlerFree,
					ChoiceHandler[] choiceHandler,
					int groupHandlerFree,
					GroupHandler[] groupHandler,
					int grammarHandlerFree,
					GrammarHandler[] grammarHandler,
					int refHandlerFree,
					RefHandler[] refHandler,
					int uinterleaveHandlerFree,
					UInterleaveHandler[] uinterleaveHandler,
					int minterleaveHandlerFree,
					MInterleaveHandler[] minterleaveHandler,
					int sinterleaveHandlerFree,
					SInterleaveHandler[] sinterleaveHandler,
					int elementHandlerFree,
					ElementHandler[] elementHandler,
					int attributeHandlerFree,
					AttributeHandler[] attributeHandler,
					int exceptPatternHandlerFree,
					ExceptPatternHandler[] exceptPatternHandler,
					int listPatternHandlerFree,
					ListPatternHandler[] listPatternHandler,
					int groupDoubleHandlerFree,
					GroupDoubleHandler[] groupDoubleHandler,
					int interleaveDoubleHandlerFree,
					InterleaveDoubleHandler[] interleaveDoubleHandler,
					int groupMinimalReduceCountHandlerFree,
					GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandler,		
					int groupMaximalReduceCountHandlerFree,
					GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandler,
					int interleaveMinimalReduceCountHandlerFree,
					InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandler,
					int interleaveMaximalReduceCountHandlerFree,
					InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandler,
					int grammarMinimalReduceHandlerFree,
					GrammarMinimalReduceHandler[] grammarMinimalReduceHandler,
					int grammarMaximalReduceHandlerFree,
					GrammarMaximalReduceHandler[] grammarMaximalReduceHandler,
					int refMinimalReduceHandlerFree,
					RefMinimalReduceHandler[] refMinimalReduceHandler,
					int refMaximalReduceHandlerFree,
					RefMaximalReduceHandler[] refMaximalReduceHandler,
					int choiceMinimalReduceHandlerFree,
					ChoiceMinimalReduceHandler[] choiceMinimalReduceHandler,
					int choiceMaximalReduceHandlerFree,
					ChoiceMaximalReduceHandler[] choiceMaximalReduceHandler,
					int groupMinimalReduceHandlerFree,
					GroupMinimalReduceHandler[] groupMinimalReduceHandler,
					int groupMaximalReduceHandlerFree,
					GroupMaximalReduceHandler[] groupMaximalReduceHandler,
					int interleaveMinimalReduceHandlerFree,
					InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandler,
					int interleaveMaximalReduceHandlerFree,
					InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandler){
		particleHandlerPoolSize = particleHandler.length;
		this.particleHandlerFree = particleHandlerFree;
		this.particleHandler = particleHandler;
		for(int i = 0; i < particleHandlerFree; i++){	
			particleHandler[i].init(this);
		}
		
		choiceHandlerPoolSize = choiceHandler.length;
		this.choiceHandlerFree = choiceHandlerFree;
		this.choiceHandler = choiceHandler;
		for(int i = 0; i < choiceHandlerFree; i++){	
			choiceHandler[i].init(this, validationItemLocator);
		}
		
		groupHandlerPoolSize = groupHandler.length;
		this.groupHandlerFree = groupHandlerFree;
		this.groupHandler = groupHandler;
		for(int i = 0; i < groupHandlerFree; i++){	
			groupHandler[i].init(this, validationItemLocator);
		}
				
		grammarHandlerPoolSize = grammarHandler.length;
		this.grammarHandlerFree = grammarHandlerFree;
		this.grammarHandler = grammarHandler;
		for(int i = 0; i < grammarHandlerFree; i++){	
			grammarHandler[i].init(this, validationItemLocator);
		}
				
		uinterleaveHandlerPoolSize = uinterleaveHandler.length;
		this.uinterleaveHandlerFree = uinterleaveHandlerFree;
		this.uinterleaveHandler = uinterleaveHandler;
		for(int i = 0; i < uinterleaveHandlerFree; i++){	
			uinterleaveHandler[i].init(this, validationItemLocator);
		}
		
		minterleaveHandlerPoolSize = minterleaveHandler.length;
		this.minterleaveHandlerFree = minterleaveHandlerFree;
		this.minterleaveHandler = minterleaveHandler;
		for(int i = 0; i < minterleaveHandlerFree; i++){	
			minterleaveHandler[i].init(this, validationItemLocator);
		}
		
		sinterleaveHandlerPoolSize = sinterleaveHandler.length;
		this.sinterleaveHandlerFree = sinterleaveHandlerFree;
		this.sinterleaveHandler = sinterleaveHandler;
		for(int i = 0; i < sinterleaveHandlerFree; i++){	
			sinterleaveHandler[i].init(this, validationItemLocator);
		}
				
		refHandlerPoolSize = refHandler.length;
		this.refHandlerFree = refHandlerFree;
		this.refHandler = refHandler;
		for(int i = 0; i < refHandlerFree; i++){	
			refHandler[i].init(this, validationItemLocator);
		}
				
		elementHandlerPoolSize = elementHandler.length;
		this.elementHandlerFree = elementHandlerFree;
		this.elementHandler = elementHandler;
		for(int i = 0; i < elementHandlerFree; i++){	
			elementHandler[i].init(this, validationItemLocator);
		}
		
		attributeHandlerPoolSize = attributeHandler.length;
		this.attributeHandlerFree = attributeHandlerFree;
		this.attributeHandler = attributeHandler;
		for(int i = 0; i < attributeHandlerFree; i++){	
			attributeHandler[i].init(this, validationItemLocator);
		}
		
		exceptPatternHandlerPoolSize = exceptPatternHandler.length;
		this.exceptPatternHandlerFree = exceptPatternHandlerFree;
		this.exceptPatternHandler = exceptPatternHandler;
		for(int i = 0; i < exceptPatternHandlerFree; i++){	
			exceptPatternHandler[i].init(this, validationItemLocator);
		}
		
		listPatternHandlerPoolSize = listPatternHandler.length;
		this.listPatternHandlerFree = listPatternHandlerFree;
		this.listPatternHandler = listPatternHandler;
		for(int i = 0; i < listPatternHandlerFree; i++){	
			listPatternHandler[i].init(this, validationItemLocator);
		}
		
		groupDoubleHandlerPoolSize = groupDoubleHandler.length;
		this.groupDoubleHandlerFree = groupDoubleHandlerFree;
		this.groupDoubleHandler = groupDoubleHandler;
		for(int i = 0; i < groupDoubleHandlerFree; i++){	
			groupDoubleHandler[i].init(this, validationItemLocator);
		}
		
		interleaveDoubleHandlerPoolSize = interleaveDoubleHandler.length;
		this.interleaveDoubleHandlerFree = interleaveDoubleHandlerFree;
		this.interleaveDoubleHandler = interleaveDoubleHandler;
		for(int i = 0; i < interleaveDoubleHandlerFree; i++){	
			interleaveDoubleHandler[i].init(this, validationItemLocator);
		}

		groupMinimalReduceCountHandlerPoolSize = groupMinimalReduceCountHandler.length;
		this.groupMinimalReduceCountHandlerFree = groupMinimalReduceCountHandlerFree;
		this.groupMinimalReduceCountHandler = groupMinimalReduceCountHandler;
		for(int i = 0; i < groupMinimalReduceCountHandlerFree; i++){	
			groupMinimalReduceCountHandler[i].init(this, validationItemLocator);
		}	
		
		groupMaximalReduceCountHandlerPoolSize = groupMaximalReduceCountHandler.length;
		this.groupMaximalReduceCountHandlerFree = groupMaximalReduceCountHandlerFree;
		this.groupMaximalReduceCountHandler = groupMaximalReduceCountHandler;
		for(int i = 0; i < groupMaximalReduceCountHandlerFree; i++){	
			groupMaximalReduceCountHandler[i].init(this, validationItemLocator);
		}
		
		
		interleaveMinimalReduceCountHandlerPoolSize = interleaveMinimalReduceCountHandler.length;
		this.interleaveMinimalReduceCountHandlerFree = interleaveMinimalReduceCountHandlerFree;
		this.interleaveMinimalReduceCountHandler = interleaveMinimalReduceCountHandler;
		for(int i = 0; i < interleaveMinimalReduceCountHandlerFree; i++){	
			interleaveMinimalReduceCountHandler[i].init(this, validationItemLocator);
		}	
		
		interleaveMaximalReduceCountHandlerPoolSize = interleaveMaximalReduceCountHandler.length;
		this.interleaveMaximalReduceCountHandlerFree = interleaveMaximalReduceCountHandlerFree;
		this.interleaveMaximalReduceCountHandler = interleaveMaximalReduceCountHandler;
		for(int i = 0; i < interleaveMaximalReduceCountHandlerFree; i++){	
			interleaveMaximalReduceCountHandler[i].init(this, validationItemLocator);
		}
		
		
		
				
		grammarMinimalReduceHandlerPoolSize = grammarMinimalReduceHandler.length;
		this.grammarMinimalReduceHandlerFree = grammarMinimalReduceHandlerFree;
		this.grammarMinimalReduceHandler = grammarMinimalReduceHandler;
		for(int i = 0; i < grammarMinimalReduceHandlerFree; i++){	
			grammarMinimalReduceHandler[i].init(this, validationItemLocator);
		}	
		
		grammarMaximalReduceHandlerPoolSize = grammarMaximalReduceHandler.length;
		this.grammarMaximalReduceHandlerFree = grammarMaximalReduceHandlerFree;
		this.grammarMaximalReduceHandler = grammarMaximalReduceHandler;
		for(int i = 0; i < grammarMaximalReduceHandlerFree; i++){	
			grammarMaximalReduceHandler[i].init(this, validationItemLocator);
		}
		
		refMinimalReduceHandlerPoolSize = refMinimalReduceHandler.length;
		this.refMinimalReduceHandlerFree = refMinimalReduceHandlerFree;
		this.refMinimalReduceHandler = refMinimalReduceHandler;
		for(int i = 0; i < refMinimalReduceHandlerFree; i++){	
			refMinimalReduceHandler[i].init(this, validationItemLocator);
		}	
		
		refMaximalReduceHandlerPoolSize = refMaximalReduceHandler.length;
		this.refMaximalReduceHandlerFree = refMaximalReduceHandlerFree;
		this.refMaximalReduceHandler = refMaximalReduceHandler;
		for(int i = 0; i < refMaximalReduceHandlerFree; i++){	
			refMaximalReduceHandler[i].init(this, validationItemLocator);
		}
		
		choiceMinimalReduceHandlerPoolSize = choiceMinimalReduceHandler.length;
		this.choiceMinimalReduceHandlerFree = choiceMinimalReduceHandlerFree;
		this.choiceMinimalReduceHandler = choiceMinimalReduceHandler;
		for(int i = 0; i < choiceMinimalReduceHandlerFree; i++){	
			choiceMinimalReduceHandler[i].init(this, validationItemLocator);
		}	
		
		choiceMaximalReduceHandlerPoolSize = choiceMaximalReduceHandler.length;
		this.choiceMaximalReduceHandlerFree = choiceMaximalReduceHandlerFree;
		this.choiceMaximalReduceHandler = choiceMaximalReduceHandler;
		for(int i = 0; i < choiceMaximalReduceHandlerFree; i++){	
			choiceMaximalReduceHandler[i].init(this, validationItemLocator);
		}
		
		groupMinimalReduceHandlerPoolSize = groupMinimalReduceHandler.length;
		this.groupMinimalReduceHandlerFree = groupMinimalReduceHandlerFree;
		this.groupMinimalReduceHandler = groupMinimalReduceHandler;
		for(int i = 0; i < groupMinimalReduceHandlerFree; i++){	
			groupMinimalReduceHandler[i].init(this, validationItemLocator);
		}	
		
		groupMaximalReduceHandlerPoolSize = groupMaximalReduceHandler.length;
		this.groupMaximalReduceHandlerFree = groupMaximalReduceHandlerFree;
		this.groupMaximalReduceHandler = groupMaximalReduceHandler;
		for(int i = 0; i < groupMaximalReduceHandlerFree; i++){	
			groupMaximalReduceHandler[i].init(this, validationItemLocator);
		}
		
		interleaveMinimalReduceHandlerPoolSize = interleaveMinimalReduceHandler.length;
		this.interleaveMinimalReduceHandlerFree = interleaveMinimalReduceHandlerFree;
		this.interleaveMinimalReduceHandler = interleaveMinimalReduceHandler;
		for(int i = 0; i < interleaveMinimalReduceHandlerFree; i++){	
			interleaveMinimalReduceHandler[i].init(this, validationItemLocator);
		}	
		
		interleaveMaximalReduceHandlerPoolSize = interleaveMaximalReduceHandler.length;
		this.interleaveMaximalReduceHandlerFree = interleaveMaximalReduceHandlerFree;
		this.interleaveMaximalReduceHandler = interleaveMaximalReduceHandler;
		for(int i = 0; i < interleaveMaximalReduceHandlerFree; i++){	
			interleaveMaximalReduceHandler[i].init(this, validationItemLocator);
		}
	}
	
	public void releaseHandlers(){
		
		// System.out.println("particle created "+particleHandlerCreated);
		// System.out.println("choice created "+choiceHandlerCreated);
		// System.out.println("group created "+groupHandlerCreated);
		// System.out.println("grammar created "+grammarHandlerCreated);
		// System.out.println("interleave created "+uinterleaveHandlerCreated);
		// System.out.println("ref created "+refHandlerCreated);
		// System.out.println("element created "+elementHandlerCreated);
		// System.out.println("attribute created "+attributeHandlerCreated);
		// System.out.println();
		pool.recycle(particleHandlerFree,
					particleHandler,
					choiceHandlerFree,
					choiceHandler,
					groupHandlerFree,
					groupHandler,
					grammarHandlerFree,
					grammarHandler,
					refHandlerFree,
					refHandler,
					uinterleaveHandlerFree,
					uinterleaveHandler,
					minterleaveHandlerFree,
					minterleaveHandler,
					sinterleaveHandlerFree,
					sinterleaveHandler,
					elementHandlerFree,
					elementHandler,
					attributeHandlerFree,
					attributeHandler,
					exceptPatternHandlerFree,
					exceptPatternHandler,
					listPatternHandlerFree,
					listPatternHandler,
					groupDoubleHandlerFree,
					groupDoubleHandler,
					interleaveDoubleHandlerFree,
					interleaveDoubleHandler,
					groupMinimalReduceCountHandlerFree,
					groupMinimalReduceCountHandler,
					groupMaximalReduceCountHandlerFree,
					groupMaximalReduceCountHandler,
					interleaveMinimalReduceCountHandlerFree,
					interleaveMinimalReduceCountHandler,
					interleaveMaximalReduceCountHandlerFree,
					interleaveMaximalReduceCountHandler,
					grammarMinimalReduceHandlerFree,
					grammarMinimalReduceHandler,
					grammarMaximalReduceHandlerFree,
					grammarMaximalReduceHandler,
					refMinimalReduceHandlerFree,
					refMinimalReduceHandler,
					refMaximalReduceHandlerFree,
					refMaximalReduceHandler,
					choiceMinimalReduceHandlerFree,
					choiceMinimalReduceHandler,
					choiceMaximalReduceHandlerFree,
					choiceMaximalReduceHandler,
					groupMinimalReduceHandlerFree,
					groupMinimalReduceHandler,
					groupMaximalReduceHandlerFree,
					groupMaximalReduceHandler,
					interleaveMinimalReduceHandlerFree,
					interleaveMinimalReduceHandler,
					interleaveMaximalReduceHandlerFree,
					interleaveMaximalReduceHandler);
		
		particleHandlerFree = 0;
		choiceHandlerFree = 0;
		groupHandlerFree = 0;
		grammarHandlerFree = 0;
		refHandlerFree = 0;
		uinterleaveHandlerFree = 0;
		minterleaveHandlerFree = 0;
		sinterleaveHandlerFree = 0;
		elementHandlerFree = 0;
		attributeHandlerFree = 0;
		exceptPatternHandlerFree = 0;
		listPatternHandlerFree = 0;
		groupDoubleHandlerFree = 0;
		interleaveDoubleHandlerFree = 0;
		groupMinimalReduceCountHandlerFree = 0;
		groupMaximalReduceCountHandlerFree = 0;
		interleaveMinimalReduceCountHandlerFree = 0;
		interleaveMaximalReduceCountHandlerFree = 0;
		grammarMinimalReduceHandlerFree = 0;
		grammarMaximalReduceHandlerFree = 0;
		refMinimalReduceHandlerFree = 0;
		refMaximalReduceHandlerFree = 0;
		choiceMinimalReduceHandlerFree = 0;
		choiceMaximalReduceHandlerFree = 0;
		groupMinimalReduceHandlerFree = 0;
		groupMaximalReduceHandlerFree = 0;
		interleaveMinimalReduceHandlerFree = 0;
		interleaveMaximalReduceHandlerFree = 0;
	}
	
	public ParticleHandler getParticleHandler(ChildEventHandler childEventHandler, APattern p, ErrorCatcher errorCatcher){
		if(particleHandlerFree == 0){
			// particleHandlerCreated++;
			ParticleHandler ph = new ParticleHandler(debugWriter);
			ph.init(this);
			ph.init(childEventHandler, p, errorCatcher);
			return ph;
		}else{
			ParticleHandler ph = particleHandler[--particleHandlerFree];
			ph.init(childEventHandler, p, errorCatcher);
			return ph;
		}
	}
	
	/*public ParticleHandler getParticleHandler(StructureValidationHandler childEventHandler, APattern definition, int childIndex, int satisfied, int saturated, ErrorCatcher errorCatcher){
		if(particleHandlerFree == 0){
			ParticleHandler ph = new ParticleHandler(debugWriter);
			ph.init(this);
			ph.init(childEventHandler, definition, errorCatcher);
			return ph;
		}else{
			ParticleHandler ph = particleHandler[--particleHandlerFree];
			ph.init(childEventHandler, definition, errorCatcher);
			return ph;
		}
	}*/
	
	public void recycle(ParticleHandler psh){		
		if(particleHandlerFree == particleHandlerPoolSize){
			if(particleHandlerPoolSize == 100) return;			
			ParticleHandler[] increased = new ParticleHandler[++particleHandlerPoolSize];
			System.arraycopy(particleHandler, 0, increased, 0, particleHandlerFree);
			particleHandler = increased;
		}
		particleHandler[particleHandlerFree++] = psh;
	}	
		
	
	public ChoiceHandler getStructureValidationHandler(AChoicePattern cp, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(choiceHandlerFree == 0){
			// choiceHandlerCreated++;
			ChoiceHandler ch = new ChoiceHandler(debugWriter);
			ch.init(this, validationItemLocator);
			ch.init(cp, errorCatcher, parent, stackHandler);
			return ch;			
		}else{
			ChoiceHandler ch = choiceHandler[--choiceHandlerFree];			
			ch.init(cp, errorCatcher, parent, stackHandler);
			return ch;
		}		
	}
	
	public void recycle(ChoiceHandler ch){		
		if(choiceHandlerFree == choiceHandlerPoolSize){
			if(choiceHandlerPoolSize == 100) return;
			ChoiceHandler[] increased = new ChoiceHandler[++choiceHandlerPoolSize];
			System.arraycopy(choiceHandler, 0, increased, 0, choiceHandlerFree);
			choiceHandler = increased;
		}
		choiceHandler[choiceHandlerFree++] = ch;
	}
	
	public GroupHandler getStructureValidationHandler(AGroup g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		if(groupHandlerFree == 0){
			// groupHandlerCreated++;
			GroupHandler gh = new GroupHandler(debugWriter);
			gh.init(this, validationItemLocator);			
			gh.init(g, errorCatcher, parent, stackHandler);
			return gh;			
		}else{
			GroupHandler gh = groupHandler[--groupHandlerFree];
			gh.init(g, errorCatcher, parent, stackHandler);
			return gh;
		}		
	}
	
	public void recycle(GroupHandler gh){
		if(groupHandlerFree == groupHandlerPoolSize){
			if(groupHandlerPoolSize == 100) return;
			GroupHandler[] increased = new GroupHandler[++groupHandlerPoolSize];
			System.arraycopy(groupHandler, 0, increased, 0, groupHandlerFree);
			groupHandler = increased;
		}
		groupHandler[groupHandlerFree++] = gh;
	}
		
	public RefHandler getStructureValidationHandler(ARef r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(refHandlerFree == 0){
			// refHandlerCreated++;
			RefHandler pih = new RefHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(r, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			RefHandler pih = refHandler[--refHandlerFree];
			pih.init(r, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}	
	
	public void recycle(RefHandler pih){
		if(refHandlerFree == refHandlerPoolSize){
			if(refHandlerPoolSize == 100) return;
			RefHandler[] increased = new RefHandler[++refHandlerPoolSize];
			System.arraycopy(refHandler, 0, increased, 0, refHandlerFree);
			refHandler = increased;
		}
		refHandler[refHandlerFree++] = pih;
	}	
		
	public GrammarHandler getStructureValidationHandler(AGrammar g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(grammarHandlerFree == 0){
			// grammarHandlerCreated++;
			GrammarHandler pih = new GrammarHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GrammarHandler pih = grammarHandler[--grammarHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}	
	
	public void recycle(GrammarHandler pih){
		if(grammarHandlerFree == grammarHandlerPoolSize){
			if(grammarHandlerPoolSize == 100) return;
			GrammarHandler[] increased = new GrammarHandler[++grammarHandlerPoolSize];
			System.arraycopy(grammarHandler, 0, increased, 0, grammarHandlerFree);
			grammarHandler = increased;
		}
		grammarHandler[grammarHandlerFree++] = pih;
	}	
	
	public UInterleaveHandler getUInterleaveHandler(AInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(uinterleaveHandlerFree == 0){
			// uinterleaveHandlerCreated++;
			UInterleaveHandler ih = new UInterleaveHandler(debugWriter);
			ih.init(this, validationItemLocator);
			ih.init(i, errorCatcher, parent, stackHandler);
			//ih.pp();			
			return ih;			
		}else{
			UInterleaveHandler ih = uinterleaveHandler[--uinterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler);
			//ih.pp();
			return ih;
		}	
	}
	
	public void recycle(UInterleaveHandler ih){
		//ih.pp();
		if(uinterleaveHandlerFree == uinterleaveHandlerPoolSize){
			if(uinterleaveHandlerPoolSize == 100) return;
			UInterleaveHandler[] increased = new UInterleaveHandler[++uinterleaveHandlerPoolSize];
			System.arraycopy(uinterleaveHandler, 0, increased, 0, uinterleaveHandlerFree);
			uinterleaveHandler = increased;
		}
		uinterleaveHandler[uinterleaveHandlerFree++] = ih;
	}

	public MInterleaveHandler getMInterleaveHandler(AInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(minterleaveHandlerFree == 0){
			// minterleaveHandlerCreated++;
			MInterleaveHandler ih = new MInterleaveHandler(debugWriter);
			ih.init(this, validationItemLocator);
			ih.init(i, errorCatcher, parent, stackHandler, this);
			//ih.pp();			
			return ih;			
		}else{
			MInterleaveHandler ih = minterleaveHandler[--minterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler, this);
			//ih.pp();
			return ih;
		}	
	}
	
	public void recycle(MInterleaveHandler ih){
		//ih.pp();
		if(minterleaveHandlerFree == minterleaveHandlerPoolSize){
			if(minterleaveHandlerPoolSize == 100) return;
			MInterleaveHandler[] increased = new MInterleaveHandler[++minterleaveHandlerPoolSize];
			System.arraycopy(minterleaveHandler, 0, increased, 0, minterleaveHandlerFree);
			minterleaveHandler = increased;
		}
		minterleaveHandler[minterleaveHandlerFree++] = ih;
	}	
	
	public SInterleaveHandler getSInterleaveHandler(AInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler, MInterleaveHandler primaryHandler){				
		if(sinterleaveHandlerFree == 0){
			// sinterleaveHandlerCreated++;
			SInterleaveHandler ih = new SInterleaveHandler(debugWriter);
			ih.init(this, validationItemLocator);
			ih.init(i, errorCatcher, parent, stackHandler, primaryHandler);
			//ih.pp();			
			return ih;			
		}else{
			SInterleaveHandler ih = sinterleaveHandler[--sinterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler, primaryHandler);
			//ih.pp();
			return ih;
		}	
	}
	
	public void recycle(SInterleaveHandler ih){
		//ih.pp();
		if(sinterleaveHandlerFree == sinterleaveHandlerPoolSize){
			if(sinterleaveHandlerPoolSize == 100) return;
			SInterleaveHandler[] increased = new SInterleaveHandler[++sinterleaveHandlerPoolSize];
			System.arraycopy(sinterleaveHandler, 0, increased, 0, sinterleaveHandlerFree);
			sinterleaveHandler = increased;
		}
		sinterleaveHandler[sinterleaveHandlerFree++] = ih;
	}	
	
	
	public ElementHandler getStructureValidationHandler(AElement e, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(elementHandlerFree == 0){
			// elementHandlerCreated++;
			ElementHandler eh = new ElementHandler(debugWriter);
			eh.init(this, validationItemLocator);
			eh.init(e, errorCatcher, stackHandler);
			return eh;			
		}else{
			ElementHandler eh = elementHandler[--elementHandlerFree];
			eh.init(e, errorCatcher, stackHandler);
			return eh;
		}		
	}
	
	public void recycle(ElementHandler eh){
		if(elementHandlerFree == elementHandlerPoolSize){
			if(elementHandlerPoolSize == 100) return;
			ElementHandler[] increased = new ElementHandler[++elementHandlerPoolSize];
			System.arraycopy(elementHandler, 0, increased, 0, elementHandlerFree);
			elementHandler = increased;
		}
		elementHandler[elementHandlerFree++] = eh;
	}
	
	public AttributeHandler getStructureValidationHandler(AAttribute a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(attributeHandlerFree == 0){
			// attributeHandlerCreated++;
			AttributeHandler ah = new AttributeHandler(debugWriter);
			ah.init(this, validationItemLocator);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			AttributeHandler ah = attributeHandler[--attributeHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			return ah;
		}		
	}
	
	public void recycle(AttributeHandler ah){
		if(attributeHandlerFree == attributeHandlerPoolSize){
			if(attributeHandlerPoolSize == 100) return;
			AttributeHandler[] increased = new AttributeHandler[++attributeHandlerPoolSize];
			System.arraycopy(attributeHandler, 0, increased, 0, attributeHandlerFree);
			attributeHandler = increased;
		}
		attributeHandler[attributeHandlerFree++] = ah;
	}
	
	public ExceptPatternHandler getStructureValidationHandler(AExceptPattern a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(exceptPatternHandlerFree == 0){
			// exceptPatternHandlerCreated++;
			ExceptPatternHandler ah = new ExceptPatternHandler(debugWriter);
			ah.init(this, validationItemLocator);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			ExceptPatternHandler ah = exceptPatternHandler[--exceptPatternHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			return ah;
		}		
	}
	
	public void recycle(ExceptPatternHandler ah){
		if(exceptPatternHandlerFree == exceptPatternHandlerPoolSize){
			if(exceptPatternHandlerPoolSize == 100) return;
			ExceptPatternHandler[] increased = new ExceptPatternHandler[++exceptPatternHandlerPoolSize];
			System.arraycopy(exceptPatternHandler, 0, increased, 0, exceptPatternHandlerFree);
			exceptPatternHandler = increased;
		}
		exceptPatternHandler[exceptPatternHandlerFree++] = ah;
	}
	
	public ListPatternHandler getStructureValidationHandler(AListPattern a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(listPatternHandlerFree == 0){
			// listPatternHandlerCreated++;
			ListPatternHandler ah = new ListPatternHandler(debugWriter);
			ah.init(this, validationItemLocator);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			ListPatternHandler ah = listPatternHandler[--listPatternHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			return ah;
		}		
	}
	
	public void recycle(ListPatternHandler ah){
		if(listPatternHandlerFree == listPatternHandlerPoolSize){
			if(listPatternHandlerPoolSize == 100) return;
			ListPatternHandler[] increased = new ListPatternHandler[++listPatternHandlerPoolSize];
			System.arraycopy(listPatternHandler, 0, increased, 0, listPatternHandlerFree);
			listPatternHandler = increased;
		}
		listPatternHandler[listPatternHandlerFree++] = ah;
	}
	
	public GroupDoubleHandler getStructureDoubleHandler(AGroup pattern, ErrorCatcher errorCatcher,  StructureHandler parent, StackHandler stackHandler, ActiveModelStackHandlerPool stackHandlerPool){				
		if(groupDoubleHandlerFree == 0){
			// groupDoubleHandlerCreated++;
			GroupDoubleHandler sih = new GroupDoubleHandler(debugWriter);
			sih.init(this, validationItemLocator);
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			return sih;			
		}else{
			GroupDoubleHandler sih = groupDoubleHandler[--groupDoubleHandlerFree];
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			return sih;
		}		
	}
	
	public void recycle(GroupDoubleHandler sih){
		if(groupDoubleHandlerFree == groupDoubleHandlerPoolSize){
			if(groupDoubleHandlerPoolSize == 100) return;
			GroupDoubleHandler[] increased = new GroupDoubleHandler[++groupDoubleHandlerPoolSize];
			System.arraycopy(groupDoubleHandler, 0, increased, 0, groupDoubleHandlerFree);
			groupDoubleHandler = increased;
		}
		groupDoubleHandler[groupDoubleHandlerFree++] = sih;
	}
	
	public InterleaveDoubleHandler getStructureDoubleHandler(AInterleave pattern, ErrorCatcher errorCatcher,  StructureHandler parent, StackHandler stackHandler, ActiveModelStackHandlerPool stackHandlerPool){				
		if(interleaveDoubleHandlerFree == 0){
			// interleaveDoubleHandlerCreated++;
			InterleaveDoubleHandler sih = new InterleaveDoubleHandler(debugWriter);
			sih.init(this, validationItemLocator);
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			return sih;			
		}else{
			InterleaveDoubleHandler sih = interleaveDoubleHandler[--interleaveDoubleHandlerFree];
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			return sih;
		}		
	}
	
	public void recycle(InterleaveDoubleHandler sih){
		if(interleaveDoubleHandlerFree == interleaveDoubleHandlerPoolSize){
			if(interleaveDoubleHandlerPoolSize == 100) return;
			InterleaveDoubleHandler[] increased = new InterleaveDoubleHandler[++interleaveDoubleHandlerPoolSize];
			System.arraycopy(interleaveDoubleHandler, 0, increased, 0, interleaveDoubleHandlerFree);
			interleaveDoubleHandler = increased;
		}
		interleaveDoubleHandler[interleaveDoubleHandlerFree++] = sih;
	}
	
	public GroupMinimalReduceCountHandler getMinimalReduceCountHandler(IntList reduceCountList, IntList startedCountList, AGroup g, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler){				
		if(groupMinimalReduceCountHandlerFree == 0){
			// groupMinimalReduceCountHandlerCreated++;
			GroupMinimalReduceCountHandler pih = new GroupMinimalReduceCountHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			return pih;			
		}else{
			GroupMinimalReduceCountHandler pih = groupMinimalReduceCountHandler[--groupMinimalReduceCountHandlerFree];			
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			return pih;
		}		
	}

	public void recycle(GroupMinimalReduceCountHandler gh){
		if(groupMinimalReduceCountHandlerFree == groupMinimalReduceCountHandlerPoolSize){
			if(groupMinimalReduceCountHandlerPoolSize == 100) return;
			GroupMinimalReduceCountHandler[] increased = new GroupMinimalReduceCountHandler[++groupMinimalReduceCountHandlerPoolSize];
			System.arraycopy(groupMinimalReduceCountHandler, 0, increased, 0, groupMinimalReduceCountHandlerFree);
			groupMinimalReduceCountHandler = increased;
		}
		groupMinimalReduceCountHandler[groupMinimalReduceCountHandlerFree++] = gh;
	}	
	
	public GroupMaximalReduceCountHandler getMaximalReduceCountHandler(IntList reduceCountList, IntList startedCountList, AGroup g, ErrorCatcher errorCatcher, MaximalReduceStackHandler stackHandler){				
		if(groupMaximalReduceCountHandlerFree == 0){
			// groupMaximalReduceCountHandlerCreated++;
			GroupMaximalReduceCountHandler pih = new GroupMaximalReduceCountHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			return pih;			
		}else{
			GroupMaximalReduceCountHandler pih = groupMaximalReduceCountHandler[--groupMaximalReduceCountHandlerFree];			
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			return pih;
		}		
	}

	public void recycle(GroupMaximalReduceCountHandler gh){
		if(groupMaximalReduceCountHandlerFree == groupMaximalReduceCountHandlerPoolSize){
			if(groupMaximalReduceCountHandlerPoolSize == 100) return;
			GroupMaximalReduceCountHandler[] increased = new GroupMaximalReduceCountHandler[++groupMaximalReduceCountHandlerPoolSize];
			System.arraycopy(groupMaximalReduceCountHandler, 0, increased, 0, groupMaximalReduceCountHandlerFree);
			groupMaximalReduceCountHandler = increased;
		}
		groupMaximalReduceCountHandler[groupMaximalReduceCountHandlerFree++] = gh;
	}

	public InterleaveMinimalReduceCountHandler getMinimalReduceCountHandler(IntList reduceCountList, AInterleave i, ErrorCatcher errorCatcher, MinimalReduceStackHandler stackHandler){				
		if(interleaveMinimalReduceCountHandlerFree == 0){
			// interleaveMinimalReduceCountHandlerCreated++;
			InterleaveMinimalReduceCountHandler pih = new InterleaveMinimalReduceCountHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			return pih;			
		}else{
			InterleaveMinimalReduceCountHandler pih = interleaveMinimalReduceCountHandler[--interleaveMinimalReduceCountHandlerFree];			
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			return pih;
		}		
	}

	public void recycle(InterleaveMinimalReduceCountHandler ih){
		if(interleaveMinimalReduceCountHandlerFree == interleaveMinimalReduceCountHandlerPoolSize){
			if(interleaveMinimalReduceCountHandlerPoolSize == 100) return;
			InterleaveMinimalReduceCountHandler[] increased = new InterleaveMinimalReduceCountHandler[++interleaveMinimalReduceCountHandlerPoolSize];
			System.arraycopy(interleaveMinimalReduceCountHandler, 0, increased, 0, interleaveMinimalReduceCountHandlerFree);
			interleaveMinimalReduceCountHandler = increased;
		}
		interleaveMinimalReduceCountHandler[interleaveMinimalReduceCountHandlerFree++] = ih;
	}	
	
	public InterleaveMaximalReduceCountHandler getMaximalReduceCountHandler(IntList reduceCountList, AInterleave i, ErrorCatcher errorCatcher, MaximalReduceStackHandler stackHandler){				
		if(interleaveMaximalReduceCountHandlerFree == 0){
			// interleaveMaximalReduceCountHandlerCreated++;
			InterleaveMaximalReduceCountHandler pih = new InterleaveMaximalReduceCountHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			return pih;			
		}else{
			InterleaveMaximalReduceCountHandler pih = interleaveMaximalReduceCountHandler[--interleaveMaximalReduceCountHandlerFree];			
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			return pih;
		}		
	}

	public void recycle(InterleaveMaximalReduceCountHandler ih){
		if(interleaveMaximalReduceCountHandlerFree == interleaveMaximalReduceCountHandlerPoolSize){
			if(interleaveMaximalReduceCountHandlerPoolSize == 100) return;
			InterleaveMaximalReduceCountHandler[] increased = new InterleaveMaximalReduceCountHandler[++interleaveMaximalReduceCountHandlerPoolSize];
			System.arraycopy(interleaveMaximalReduceCountHandler, 0, increased, 0, interleaveMaximalReduceCountHandlerFree);
			interleaveMaximalReduceCountHandler = increased;
		}
		interleaveMaximalReduceCountHandler[interleaveMaximalReduceCountHandlerFree++] = ih;
	}	
		
	
	public GrammarMinimalReduceHandler getMinimalReduceHandler(AGrammar g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(grammarMinimalReduceHandlerFree == 0){
			// grammarMinimalReduceHandlerCreated++;
			GrammarMinimalReduceHandler pih = new GrammarMinimalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GrammarMinimalReduceHandler pih = grammarMinimalReduceHandler[--grammarMinimalReduceHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(GrammarMinimalReduceHandler gh){
		if(grammarMinimalReduceHandlerFree == grammarMinimalReduceHandlerPoolSize){
			if(grammarMinimalReduceHandlerPoolSize == 100) return;
			GrammarMinimalReduceHandler[] increased = new GrammarMinimalReduceHandler[++grammarMinimalReduceHandlerPoolSize];
			System.arraycopy(grammarMinimalReduceHandler, 0, increased, 0, grammarMinimalReduceHandlerFree);
			grammarMinimalReduceHandler = increased;
		}
		grammarMinimalReduceHandler[grammarMinimalReduceHandlerFree++] = gh;
	}	
	
	public GrammarMaximalReduceHandler getMaximalReduceHandler(AGrammar g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(grammarMaximalReduceHandlerFree == 0){
			// grammarMaximalReduceHandlerCreated++;
			GrammarMaximalReduceHandler pih = new GrammarMaximalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GrammarMaximalReduceHandler pih = grammarMaximalReduceHandler[--grammarMaximalReduceHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(GrammarMaximalReduceHandler gh){
		if(grammarMaximalReduceHandlerFree == grammarMaximalReduceHandlerPoolSize){
			if(grammarMaximalReduceHandlerPoolSize == 100) return;
			GrammarMaximalReduceHandler[] increased = new GrammarMaximalReduceHandler[++grammarMaximalReduceHandlerPoolSize];
			System.arraycopy(grammarMaximalReduceHandler, 0, increased, 0, grammarMaximalReduceHandlerFree);
			grammarMaximalReduceHandler = increased;
		}
		grammarMaximalReduceHandler[grammarMaximalReduceHandlerFree++] = gh;
	}

	public RefMinimalReduceHandler getMinimalReduceHandler(ARef r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(refMinimalReduceHandlerFree == 0){
			// refMinimalReduceHandlerCreated++;
			RefMinimalReduceHandler rmrh = new RefMinimalReduceHandler(debugWriter);
			rmrh.init(this, validationItemLocator);
			rmrh.init(r, errorCatcher, parent, stackHandler);
			return rmrh;			
		}else{
			RefMinimalReduceHandler rmrh = refMinimalReduceHandler[--refMinimalReduceHandlerFree];			
			rmrh.init(r, errorCatcher, parent, stackHandler);
			return rmrh;
		}		
	}

	public void recycle(RefMinimalReduceHandler rmrh){
		if(refMinimalReduceHandlerFree == refMinimalReduceHandlerPoolSize){
			if(refMinimalReduceHandlerPoolSize == 100) return;
			RefMinimalReduceHandler[] increased = new RefMinimalReduceHandler[++refMinimalReduceHandlerPoolSize];
			System.arraycopy(refMinimalReduceHandler, 0, increased, 0, refMinimalReduceHandlerFree);
			refMinimalReduceHandler = increased;
		}
		refMinimalReduceHandler[refMinimalReduceHandlerFree++] = rmrh;
	}	
	
	public RefMaximalReduceHandler getMaximalReduceHandler(ARef r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(refMaximalReduceHandlerFree == 0){
			// refMaximalReduceHandlerCreated++;
			RefMaximalReduceHandler pih = new RefMaximalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(r, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			RefMaximalReduceHandler pih = refMaximalReduceHandler[--refMaximalReduceHandlerFree];			
			pih.init(r, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(RefMaximalReduceHandler rmrh){
		if(refMaximalReduceHandlerFree == refMaximalReduceHandlerPoolSize){
			if(refMaximalReduceHandlerPoolSize == 100) return;
			RefMaximalReduceHandler[] increased = new RefMaximalReduceHandler[++refMaximalReduceHandlerPoolSize];
			System.arraycopy(refMaximalReduceHandler, 0, increased, 0, refMaximalReduceHandlerFree);
			refMaximalReduceHandler = increased;
		}
		refMaximalReduceHandler[refMaximalReduceHandlerFree++] = rmrh;
	}

	public ChoiceMinimalReduceHandler getMinimalReduceHandler(AChoicePattern c, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(choiceMinimalReduceHandlerFree == 0){
			// choiceMinimalReduceHandlerCreated++;
			ChoiceMinimalReduceHandler pih = new ChoiceMinimalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(c, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			ChoiceMinimalReduceHandler pih = choiceMinimalReduceHandler[--choiceMinimalReduceHandlerFree];			
			pih.init(c, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(ChoiceMinimalReduceHandler ch){
		if(choiceMinimalReduceHandlerFree == choiceMinimalReduceHandlerPoolSize){
			if(choiceMinimalReduceHandlerPoolSize == 100) return;
			ChoiceMinimalReduceHandler[] increased = new ChoiceMinimalReduceHandler[++choiceMinimalReduceHandlerPoolSize];
			System.arraycopy(choiceMinimalReduceHandler, 0, increased, 0, choiceMinimalReduceHandlerFree);
			choiceMinimalReduceHandler = increased;
		}
		choiceMinimalReduceHandler[choiceMinimalReduceHandlerFree++] = ch;
	}	
	
	public ChoiceMaximalReduceHandler getMaximalReduceHandler(AChoicePattern c, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(choiceMaximalReduceHandlerFree == 0){
			// choiceMaximalReduceHandlerCreated++;
			ChoiceMaximalReduceHandler pih = new ChoiceMaximalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(c, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			ChoiceMaximalReduceHandler pih = choiceMaximalReduceHandler[--choiceMaximalReduceHandlerFree];			
			pih.init(c, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(ChoiceMaximalReduceHandler ch){
		if(choiceMaximalReduceHandlerFree == choiceMaximalReduceHandlerPoolSize){
			if(choiceMaximalReduceHandlerPoolSize == 100) return;
			ChoiceMaximalReduceHandler[] increased = new ChoiceMaximalReduceHandler[++choiceMaximalReduceHandlerPoolSize];
			System.arraycopy(choiceMaximalReduceHandler, 0, increased, 0, choiceMaximalReduceHandlerFree);
			choiceMaximalReduceHandler = increased;
		}
		choiceMaximalReduceHandler[choiceMaximalReduceHandlerFree++] = ch;
	}

	public GroupMinimalReduceHandler getMinimalReduceHandler(AGroup g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(groupMinimalReduceHandlerFree == 0){
			// groupMinimalReduceHandlerCreated++;
			GroupMinimalReduceHandler pih = new GroupMinimalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GroupMinimalReduceHandler pih = groupMinimalReduceHandler[--groupMinimalReduceHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(GroupMinimalReduceHandler gh){
		if(groupMinimalReduceHandlerFree == groupMinimalReduceHandlerPoolSize){
			if(groupMinimalReduceHandlerPoolSize == 100) return;
			GroupMinimalReduceHandler[] increased = new GroupMinimalReduceHandler[++groupMinimalReduceHandlerPoolSize];
			System.arraycopy(groupMinimalReduceHandler, 0, increased, 0, groupMinimalReduceHandlerFree);
			groupMinimalReduceHandler = increased;
		}
		groupMinimalReduceHandler[groupMinimalReduceHandlerFree++] = gh;
	}	
	
	public GroupMaximalReduceHandler getMaximalReduceHandler(AGroup g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(groupMaximalReduceHandlerFree == 0){
			// groupMaximalReduceHandlerCreated++;
			GroupMaximalReduceHandler pih = new GroupMaximalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GroupMaximalReduceHandler pih = groupMaximalReduceHandler[--groupMaximalReduceHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(GroupMaximalReduceHandler gh){
		if(groupMaximalReduceHandlerFree == groupMaximalReduceHandlerPoolSize){
			if(groupMaximalReduceHandlerPoolSize == 100) return;
			GroupMaximalReduceHandler[] increased = new GroupMaximalReduceHandler[++groupMaximalReduceHandlerPoolSize];
			System.arraycopy(groupMaximalReduceHandler, 0, increased, 0, groupMaximalReduceHandlerFree);
			groupMaximalReduceHandler = increased;
		}
		groupMaximalReduceHandler[groupMaximalReduceHandlerFree++] = gh;
	}

	public InterleaveMinimalReduceHandler getMinimalReduceHandler(AInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(interleaveMinimalReduceHandlerFree == 0){
			// interleaveMinimalReduceHandlerCreated++;
			InterleaveMinimalReduceHandler pih = new InterleaveMinimalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(i, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			InterleaveMinimalReduceHandler pih = interleaveMinimalReduceHandler[--interleaveMinimalReduceHandlerFree];			
			pih.init(i, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(InterleaveMinimalReduceHandler ih){
		if(interleaveMinimalReduceHandlerFree == interleaveMinimalReduceHandlerPoolSize){
			if(interleaveMinimalReduceHandlerPoolSize == 100) return;
			InterleaveMinimalReduceHandler[] increased = new InterleaveMinimalReduceHandler[++interleaveMinimalReduceHandlerPoolSize];
			System.arraycopy(interleaveMinimalReduceHandler, 0, increased, 0, interleaveMinimalReduceHandlerFree);
			interleaveMinimalReduceHandler = increased;
		}
		interleaveMinimalReduceHandler[interleaveMinimalReduceHandlerFree++] = ih;
	}	
	
	public InterleaveMaximalReduceHandler getMaximalReduceHandler(AInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(interleaveMaximalReduceHandlerFree == 0){
			// interleaveMaximalReduceHandlerCreated++;
			InterleaveMaximalReduceHandler pih = new InterleaveMaximalReduceHandler(debugWriter);
			pih.init(this, validationItemLocator);
			pih.init(i, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			InterleaveMaximalReduceHandler pih = interleaveMaximalReduceHandler[--interleaveMaximalReduceHandlerFree];			
			pih.init(i, errorCatcher, parent, stackHandler);
			return pih;
		}		
	}

	public void recycle(InterleaveMaximalReduceHandler ih){
		if(interleaveMaximalReduceHandlerFree == interleaveMaximalReduceHandlerPoolSize){
			if(interleaveMaximalReduceHandlerPoolSize == 100) return;
			InterleaveMaximalReduceHandler[] increased = new InterleaveMaximalReduceHandler[++interleaveMaximalReduceHandlerPoolSize];
			System.arraycopy(interleaveMaximalReduceHandler, 0, increased, 0, interleaveMaximalReduceHandlerFree);
			interleaveMaximalReduceHandler = increased;
		}
		interleaveMaximalReduceHandler[interleaveMaximalReduceHandlerFree++] = ih;
	}	
}