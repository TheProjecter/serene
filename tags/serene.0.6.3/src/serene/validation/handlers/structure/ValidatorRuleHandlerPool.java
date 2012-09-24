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

import serene.util.IntList;

import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SMultipleChildrenPattern;
import serene.validation.schema.simplified.SUniqueChildPattern;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SChoicePattern;
import serene.validation.schema.simplified.SGroup;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SListPattern;
import serene.validation.schema.simplified.SGrammar;
import serene.validation.schema.simplified.SRef;
import serene.validation.schema.simplified.SEmpty;
import serene.validation.schema.simplified.SText;
import serene.validation.schema.simplified.SNotAllowed;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SDummy;
import serene.validation.schema.simplified.SimplifiedRuleVisitor;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.ChildEventHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;
import serene.validation.handlers.stack.impl.MinimalReduceStackHandler;
import serene.validation.handlers.stack.impl.MaximalReduceStackHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.content.util.ActiveInputDescriptor;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.match.MatchPath;

import serene.Reusable;

public class ValidatorRuleHandlerPool implements Reusable, SimplifiedRuleVisitor{

	RuleHandlerPool pool;
	
	
	int particleHandlerCreated;
	int particleHandlerRequested;
	int particleHandlerRecycled;	
	int particleHandlerMaxSize;
	int particleHandlerFree;
	int particleHandlerMinFree;
	ParticleHandler[] particleHandler;
	
	
	int choiceHandlerCreated;
	int choiceHandlerRequested;
	int choiceHandlerRecycled;
	int choiceHandlerMaxSize;	
	int choiceHandlerFree;
	int choiceHandlerMinFree;
	ChoiceHandler[] choiceHandler;
		
	int groupHandlerCreated;
	int groupHandlerRequested;
	int groupHandlerRecycled;	
	int groupHandlerMaxSize;
	int groupHandlerFree;
	int groupHandlerMinFree;
	GroupHandler[] groupHandler;
	
	
	// int intermediaryPatternHandlerCreated;
	int intermediaryPatternHandlerMaxSize;
	int intermediaryPatternHandlerFree;
	int intermediaryPatternHandlerMinFree;
	IntermediaryPatternHandler[] intermediaryPatternHandler;
		
	// int uinterleaveHandlerCreated;
	int uinterleaveHandlerMaxSize;
	int uinterleaveHandlerFree;
	int uinterleaveHandlerMinFree;
	UInterleaveHandler[] uinterleaveHandler;
		
	// int minterleaveHandlerCreated;
	int minterleaveHandlerMaxSize;
	int minterleaveHandlerFree;
	int minterleaveHandlerMinFree;
	MInterleaveHandler[] minterleaveHandler;
	
	// int sinterleaveHandlerCreated;
	int sinterleaveHandlerMaxSize;
	int sinterleaveHandlerFree;
	int sinterleaveHandlerMinFree;
	SInterleaveHandler[] sinterleaveHandler;
		
	
	// int typeHandlerCreated;
	int typeHandlerMaxSize;
	int typeHandlerFree;
	int typeHandlerMinFree;
	TypeHandler[] typeHandler;
	
	
	
	
	// int groupDoubleHandlerCreated;
	int groupDoubleHandlerMaxSize;
	int groupDoubleHandlerFree;
	int groupDoubleHandlerMinFree;
	GroupDoubleHandler[] groupDoubleHandler;
	
	// int interleaveDoubleHandlerCreated;
	int interleaveDoubleHandlerMaxSize;
	int interleaveDoubleHandlerFree;
	int interleaveDoubleHandlerMinFree;
	InterleaveDoubleHandler[] interleaveDoubleHandler;

	
	// int groupMinimalReduceCountHandlerCreated;
	int groupMinimalReduceCountHandlerMaxSize;
	int groupMinimalReduceCountHandlerFree;
	int groupMinimalReduceCountHandlerMinFree;
	GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandler;
	
	// int groupMaximalReduceCountHandlerCreated;
	int groupMaximalReduceCountHandlerMaxSize;
	int groupMaximalReduceCountHandlerFree;
	int groupMaximalReduceCountHandlerMinFree;
	GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandler;
	
	// int interleaveMinimalReduceCountHandlerCreated;
	int interleaveMinimalReduceCountHandlerMaxSize;
	int interleaveMinimalReduceCountHandlerFree;
	int interleaveMinimalReduceCountHandlerMinFree;
	InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandler;
	
	// int interleaveMaximalReduceCountHandlerCreated;
	int interleaveMaximalReduceCountHandlerMaxSize;
	int interleaveMaximalReduceCountHandlerFree;
	int interleaveMaximalReduceCountHandlerMinFree;
	InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandler;
	
	
	
	
	
	
	// int intermediaryPatternMinimalReduceHandlerCreated;
	int intermediaryPatternMinimalReduceHandlerMaxSize;
	int intermediaryPatternMinimalReduceHandlerFree;
	int intermediaryPatternMinimalReduceHandlerMinFree;
	IntermediaryPatternMinimalReduceHandler[] intermediaryPatternMinimalReduceHandler;
	
	// int intermediaryPatternMaximalReduceHandlerCreated;
	int intermediaryPatternMaximalReduceHandlerMaxSize;
	int intermediaryPatternMaximalReduceHandlerFree;
	int intermediaryPatternMaximalReduceHandlerMinFree;
	IntermediaryPatternMaximalReduceHandler[] intermediaryPatternMaximalReduceHandler;
		
	// int choiceMinimalReduceHandlerCreated;
	int choiceMinimalReduceHandlerMaxSize;
	int choiceMinimalReduceHandlerFree;
	int choiceMinimalReduceHandlerMinFree;
	ChoiceMinimalReduceHandler[] choiceMinimalReduceHandler;
	
	// int choiceMaximalReduceHandlerCreated;
	int choiceMaximalReduceHandlerMaxSize;
	int choiceMaximalReduceHandlerFree;
	int choiceMaximalReduceHandlerMinFree;
	ChoiceMaximalReduceHandler[] choiceMaximalReduceHandler;
	
	// int groupMinimalReduceHandlerCreated;
	int groupMinimalReduceHandlerMaxSize;
	int groupMinimalReduceHandlerFree;
	int groupMinimalReduceHandlerMinFree;
	GroupMinimalReduceHandler[] groupMinimalReduceHandler;
	
	// int groupMaximalReduceHandlerCreated;
	int groupMaximalReduceHandlerMaxSize;
	int groupMaximalReduceHandlerFree;
	int groupMaximalReduceHandlerMinFree;
	GroupMaximalReduceHandler[] groupMaximalReduceHandler;
	
	// int interleaveMinimalReduceHandlerCreated;
	int interleaveMinimalReduceHandlerMaxSize;
	int interleaveMinimalReduceHandlerFree;
	int interleaveMinimalReduceHandlerMinFree;
	InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandler;
	
	// int interleaveMaximalReduceHandlerCreated;
	int interleaveMaximalReduceHandlerMaxSize;
	int interleaveMaximalReduceHandlerFree;
	int interleaveMaximalReduceHandlerMinFree;
	InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandler;
	
	ActiveInputDescriptor activeInputDescriptor;
	InputStackDescriptor inputStackDescriptor;
	ValidatorStackHandlerPool stackHandlerPool;	
	
	boolean full;
	

	ErrorCatcher errorCatcher;
	StructureHandler parent;
	StackHandler stackHandler;
	IntList reduceCountList;
	IntList startedCountList;
	StructureHandler handler;
	MatchPath path;
	int handlerId;
	
	
	final int MAX_REDUCE_COUNT = 0;
	final int MIN_REDUCE_COUNT = 1;
	final int SIMPLE = 2;
	final int MIN_REDUCE = 3;
	final int MAX_REDUCE = 4;
	
	public ValidatorRuleHandlerPool(RuleHandlerPool pool){
		this.pool = pool;		
		
		particleHandlerMaxSize = 100;        
        choiceHandlerMaxSize = 50;
        groupHandlerMaxSize = 50;
        intermediaryPatternHandlerMaxSize = 50;
        uinterleaveHandlerMaxSize = 50;
        minterleaveHandlerMaxSize = 50;
        sinterleaveHandlerMaxSize = 50;
        typeHandlerMaxSize = 100;
        groupDoubleHandlerMaxSize = 50;
        interleaveDoubleHandlerMaxSize = 50;
        groupMinimalReduceCountHandlerMaxSize = 50;
        groupMaximalReduceCountHandlerMaxSize = 50;
        interleaveMinimalReduceCountHandlerMaxSize = 50;
        interleaveMaximalReduceCountHandlerMaxSize = 50;
        intermediaryPatternMinimalReduceHandlerMaxSize = 50;
        intermediaryPatternMaximalReduceHandlerMaxSize = 50;
        choiceMinimalReduceHandlerMaxSize = 50;
        choiceMaximalReduceHandlerMaxSize = 50;
        groupMinimalReduceHandlerMaxSize = 50;
        groupMaximalReduceHandlerMaxSize = 50;
        interleaveMinimalReduceHandlerMaxSize = 50;
        interleaveMaximalReduceHandlerMaxSize = 50;
    
        full = false;
	}
	
		
	public int getParticleHandlerRequested(){
	    return particleHandlerRequested;
	}
	
	public void recycle(){		
		if(full)releaseHandlers();
		pool.recycle(this);
	}
	
	public void fill(ValidatorStackHandlerPool stackHandlerPool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor){
	    this.activeInputDescriptor = activeInputDescriptor;
		this.inputStackDescriptor = inputStackDescriptor;
		this.stackHandlerPool = stackHandlerPool;
		if(pool != null){
		    pool.fill(this,
			 		particleHandler,
					choiceHandler,
					groupHandler,
					intermediaryPatternHandler,
					uinterleaveHandler,
					minterleaveHandler,
					sinterleaveHandler,
					typeHandler,
					groupDoubleHandler,
					interleaveDoubleHandler,
					groupMinimalReduceCountHandler,
					groupMaximalReduceCountHandler,
					interleaveMinimalReduceCountHandler,
					interleaveMaximalReduceCountHandler,
					intermediaryPatternMinimalReduceHandler,
					intermediaryPatternMaximalReduceHandler,
					choiceMinimalReduceHandler,
					choiceMaximalReduceHandler,
					groupMinimalReduceHandler,
					groupMaximalReduceHandler,
					interleaveMinimalReduceHandler,
					interleaveMaximalReduceHandler);
		}else{
		    particleHandler = new ParticleHandler[30];
            choiceHandler = new ChoiceHandler[10];
            groupHandler = new GroupHandler[10];
            intermediaryPatternHandler = new IntermediaryPatternHandler[10];
            uinterleaveHandler = new UInterleaveHandler[10];
            minterleaveHandler = new MInterleaveHandler[10];
            sinterleaveHandler = new SInterleaveHandler[10];
            typeHandler = new TypeHandler[10];
            
            
            groupDoubleHandler = new GroupDoubleHandler[10];
            interleaveDoubleHandler = new InterleaveDoubleHandler[10];
            
            groupMinimalReduceCountHandler = new GroupMinimalReduceCountHandler[5];
            groupMaximalReduceCountHandler = new GroupMaximalReduceCountHandler[5];
            interleaveMinimalReduceCountHandler = new InterleaveMinimalReduceCountHandler[5];
            interleaveMaximalReduceCountHandler = new InterleaveMaximalReduceCountHandler[5];
            
            
            intermediaryPatternMinimalReduceHandler = new IntermediaryPatternMinimalReduceHandler[5];
            intermediaryPatternMaximalReduceHandler = new IntermediaryPatternMaximalReduceHandler[5];
            choiceMinimalReduceHandler = new ChoiceMinimalReduceHandler[5];
            choiceMaximalReduceHandler = new ChoiceMaximalReduceHandler[5];
            groupMinimalReduceHandler = new GroupMinimalReduceHandler[5];
            groupMaximalReduceHandler = new GroupMaximalReduceHandler[5];
            interleaveMinimalReduceHandler = new InterleaveMinimalReduceHandler[5];
            interleaveMaximalReduceHandler = new InterleaveMaximalReduceHandler[5];            
		}
		full = true;
	}
	
	void initFilled(int particleHandlerFillCount,
					int choiceHandlerFillCount,
					int groupHandlerFillCount,
					int intermediaryPatternHandlerFillCount,
					int uinterleaveHandlerFillCount,
					int minterleaveHandlerFillCount,
					int sinterleaveHandlerFillCount,
					int typeHandlerFillCount,
					int groupDoubleHandlerFillCount,
					int interleaveDoubleHandlerFillCount,
					int groupMinimalReduceCountHandlerFillCount,		
					int groupMaximalReduceCountHandlerFillCount,
					int interleaveMinimalReduceCountHandlerFillCount,
					int interleaveMaximalReduceCountHandlerFillCount,
					int intermediaryPatternMinimalReduceHandlerFillCount,
					int intermediaryPatternMaximalReduceHandlerFillCount,
					int choiceMinimalReduceHandlerFillCount,
					int choiceMaximalReduceHandlerFillCount,
					int groupMinimalReduceHandlerFillCount,
					int groupMaximalReduceHandlerFillCount,
					int interleaveMinimalReduceHandlerFillCount,
					int interleaveMaximalReduceHandlerFillCount){
		particleHandlerFree = particleHandlerFillCount;
		particleHandlerMinFree = particleHandlerFree;
		for(int i = 0; i < particleHandlerFree; i++){	
			particleHandler[i].init(activeInputDescriptor, this);
		}
		
		choiceHandlerFree = choiceHandlerFillCount;
		choiceHandlerMinFree = choiceHandlerFree;
		for(int i = 0; i < choiceHandlerFree; i++){	
			choiceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		groupHandlerFree = groupHandlerFillCount;
		groupHandlerMinFree = groupHandlerFree;
		for(int i = 0; i < groupHandlerFree; i++){	
			groupHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		uinterleaveHandlerFree = uinterleaveHandlerFillCount;
		uinterleaveHandlerMinFree = uinterleaveHandlerFree;
		for(int i = 0; i < uinterleaveHandlerFree; i++){	
			uinterleaveHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		minterleaveHandlerFree = minterleaveHandlerFillCount;
		minterleaveHandlerMinFree = minterleaveHandlerFree;
		for(int i = 0; i < minterleaveHandlerFree; i++){	
			minterleaveHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		sinterleaveHandlerFree = sinterleaveHandlerFillCount;
		sinterleaveHandlerMinFree = sinterleaveHandlerFree;
		for(int i = 0; i < sinterleaveHandlerFree; i++){	
			sinterleaveHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		intermediaryPatternHandlerFree = intermediaryPatternHandlerFillCount;
		intermediaryPatternHandlerMinFree = intermediaryPatternHandlerFree;
		for(int i = 0; i < intermediaryPatternHandlerFree; i++){	
			intermediaryPatternHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
				
		typeHandlerFree = typeHandlerFillCount;
		typeHandlerMinFree = typeHandlerFree;
		for(int i = 0; i < typeHandlerFree; i++){	
			typeHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		groupDoubleHandlerFree = groupDoubleHandlerFillCount;
		groupDoubleHandlerMinFree = groupDoubleHandlerFree;
		for(int i = 0; i < groupDoubleHandlerFree; i++){	
			groupDoubleHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		interleaveDoubleHandlerFree = interleaveDoubleHandlerFillCount;
		interleaveDoubleHandlerMinFree = interleaveDoubleHandlerFree;
		for(int i = 0; i < interleaveDoubleHandlerFree; i++){	
			interleaveDoubleHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}

		groupMinimalReduceCountHandlerFree = groupMinimalReduceCountHandlerFillCount;
		groupMinimalReduceCountHandlerMinFree = groupMinimalReduceCountHandlerFree;
		for(int i = 0; i < groupMinimalReduceCountHandlerFree; i++){	
			groupMinimalReduceCountHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}	

		groupMaximalReduceCountHandlerFree = groupMaximalReduceCountHandlerFillCount;
		groupMaximalReduceCountHandlerMinFree = groupMaximalReduceCountHandlerFree;
		for(int i = 0; i < groupMaximalReduceCountHandlerFree; i++){	
			groupMaximalReduceCountHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}

		
		
		interleaveMinimalReduceCountHandlerFree = interleaveMinimalReduceCountHandlerFillCount;
		interleaveMinimalReduceCountHandlerMinFree = interleaveMinimalReduceCountHandlerFree;
		for(int i = 0; i < interleaveMinimalReduceCountHandlerFree; i++){	
			interleaveMinimalReduceCountHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}	
		
		interleaveMaximalReduceCountHandlerFree = interleaveMaximalReduceCountHandlerFillCount;
		interleaveMaximalReduceCountHandlerMinFree = interleaveMaximalReduceCountHandlerFree;
		for(int i = 0; i < interleaveMaximalReduceCountHandlerFree; i++){	
			interleaveMaximalReduceCountHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		
						
		intermediaryPatternMinimalReduceHandlerFree = intermediaryPatternMinimalReduceHandlerFillCount;
		intermediaryPatternMinimalReduceHandlerMinFree = intermediaryPatternMinimalReduceHandlerFree;
		for(int i = 0; i < intermediaryPatternMinimalReduceHandlerFree; i++){	
			intermediaryPatternMinimalReduceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}	
				
		intermediaryPatternMaximalReduceHandlerFree = intermediaryPatternMaximalReduceHandlerFillCount;
		intermediaryPatternMaximalReduceHandlerMinFree = intermediaryPatternMaximalReduceHandlerFree;
		for(int i = 0; i < intermediaryPatternMaximalReduceHandlerFree; i++){	
			intermediaryPatternMaximalReduceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
				
		choiceMinimalReduceHandlerFree = choiceMinimalReduceHandlerFillCount;
		choiceMinimalReduceHandlerMinFree = choiceMinimalReduceHandlerFree;
		for(int i = 0; i < choiceMinimalReduceHandlerFree; i++){	
			choiceMinimalReduceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}	
		
		choiceMaximalReduceHandlerFree = choiceMaximalReduceHandlerFillCount;
		choiceMaximalReduceHandlerMinFree = choiceMaximalReduceHandlerFree;
		for(int i = 0; i < choiceMaximalReduceHandlerFree; i++){	
			choiceMaximalReduceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		groupMinimalReduceHandlerFree = groupMinimalReduceHandlerFillCount;
		groupMinimalReduceHandlerMinFree = groupMinimalReduceHandlerFree;
		for(int i = 0; i < groupMinimalReduceHandlerFree; i++){	
			groupMinimalReduceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}	
		
		groupMaximalReduceHandlerFree = groupMaximalReduceHandlerFillCount;
		groupMaximalReduceHandlerMinFree = groupMaximalReduceHandlerFree;
		for(int i = 0; i < groupMaximalReduceHandlerFree; i++){	
			groupMaximalReduceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		interleaveMinimalReduceHandlerFree = interleaveMinimalReduceHandlerFillCount;
		interleaveMinimalReduceHandlerMinFree = interleaveMinimalReduceHandlerFree;		
		for(int i = 0; i < interleaveMinimalReduceHandlerFree; i++){	
			interleaveMinimalReduceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}	
		
		interleaveMaximalReduceHandlerFree = interleaveMaximalReduceHandlerFillCount;
		interleaveMaximalReduceHandlerMinFree = interleaveMaximalReduceHandlerFree;
		for(int i = 0; i < interleaveMaximalReduceHandlerFree; i++){	
			interleaveMaximalReduceHandler[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
	}
	
	public void releaseHandlers(){
		pool.recycle(particleHandlerFree,
		            particleHandlerFree - particleHandlerMinFree,
					particleHandler,
					choiceHandlerFree,
					choiceHandlerFree - choiceHandlerMinFree,
					choiceHandler,
					groupHandlerFree,
					groupHandlerFree - groupHandlerMinFree,
					groupHandler,
					intermediaryPatternHandlerFree,
					intermediaryPatternHandlerFree - intermediaryPatternHandlerMinFree,
					intermediaryPatternHandler,
					uinterleaveHandlerFree,
					uinterleaveHandlerFree - uinterleaveHandlerMinFree,
					uinterleaveHandler,
					minterleaveHandlerFree,
					minterleaveHandlerFree - minterleaveHandlerMinFree,
					minterleaveHandler,
					sinterleaveHandlerFree,
					sinterleaveHandlerFree - sinterleaveHandlerMinFree,
					sinterleaveHandler,
					typeHandlerFree,
					typeHandlerFree - typeHandlerMinFree,
					typeHandler,
					groupDoubleHandlerFree,
					groupDoubleHandlerFree - groupDoubleHandlerMinFree,
					groupDoubleHandler,
					interleaveDoubleHandlerFree,
					interleaveDoubleHandlerFree - interleaveDoubleHandlerMinFree,
					interleaveDoubleHandler,
					groupMinimalReduceCountHandlerFree,
					groupMinimalReduceCountHandlerFree - groupMinimalReduceCountHandlerMinFree,
					groupMinimalReduceCountHandler,
					groupMaximalReduceCountHandlerFree,
					groupMaximalReduceCountHandlerFree - groupMaximalReduceCountHandlerMinFree,
					groupMaximalReduceCountHandler,
					interleaveMinimalReduceCountHandlerFree,
					interleaveMinimalReduceCountHandlerFree - interleaveMinimalReduceCountHandlerMinFree,
					interleaveMinimalReduceCountHandler,
					interleaveMaximalReduceCountHandlerFree,
					interleaveMaximalReduceCountHandlerFree - interleaveMaximalReduceCountHandlerMinFree,
					interleaveMaximalReduceCountHandler,
					intermediaryPatternMinimalReduceHandlerFree,
					intermediaryPatternMinimalReduceHandlerFree - intermediaryPatternMinimalReduceHandlerMinFree,
					intermediaryPatternMinimalReduceHandler,
					intermediaryPatternMaximalReduceHandlerFree,
					intermediaryPatternMaximalReduceHandlerFree - intermediaryPatternMaximalReduceHandlerMinFree,
					intermediaryPatternMaximalReduceHandler,
					choiceMinimalReduceHandlerFree,
					choiceMinimalReduceHandlerFree - choiceMinimalReduceHandlerMinFree,
					choiceMinimalReduceHandler,
					choiceMaximalReduceHandlerFree,
					choiceMaximalReduceHandlerFree - choiceMaximalReduceHandlerMinFree,    
					choiceMaximalReduceHandler,
					groupMinimalReduceHandlerFree,
					groupMinimalReduceHandlerFree - groupMinimalReduceHandlerMinFree,
					groupMinimalReduceHandler,
					groupMaximalReduceHandlerFree,
					groupMaximalReduceHandlerFree - groupMaximalReduceHandlerMinFree,
					groupMaximalReduceHandler,
					interleaveMinimalReduceHandlerFree,
					interleaveMinimalReduceHandlerFree - interleaveMinimalReduceHandlerMinFree,
					interleaveMinimalReduceHandler,
					interleaveMaximalReduceHandlerFree,
					interleaveMaximalReduceHandlerFree - interleaveMaximalReduceHandlerMinFree,
					interleaveMaximalReduceHandler);
		
		particleHandlerFree = 0;
        choiceHandlerFree = 0;
        groupHandlerFree = 0;
        intermediaryPatternHandlerFree = 0;
        uinterleaveHandlerFree = 0;
        minterleaveHandlerFree = 0;
        sinterleaveHandlerFree = 0;
        typeHandlerFree = 0;
        groupDoubleHandlerFree = 0;
        interleaveDoubleHandlerFree = 0;
        groupMinimalReduceCountHandlerFree = 0;
        groupMaximalReduceCountHandlerFree = 0;
        interleaveMinimalReduceCountHandlerFree = 0;
        interleaveMaximalReduceCountHandlerFree = 0;
        intermediaryPatternMinimalReduceHandlerFree = 0;
        intermediaryPatternMaximalReduceHandlerFree = 0;
        choiceMinimalReduceHandlerFree = 0;
        choiceMaximalReduceHandlerFree = 0;
        groupMinimalReduceHandlerFree = 0;
        groupMaximalReduceHandlerFree = 0;
        interleaveMinimalReduceHandlerFree = 0;
        interleaveMaximalReduceHandlerFree = 0;
        
		full = false;
	}
	
	public ParticleHandler getParticleHandler(SPattern p, ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
	    particleHandlerRequested++;
		if(particleHandlerFree == 0){
			particleHandlerCreated++;			
			ParticleHandler ph = new ParticleHandler();
			ph.init(activeInputDescriptor, this);
			ph.init(childEventHandler, p, errorCatcher);
			return ph;
		}else{		    
			ParticleHandler ph = particleHandler[--particleHandlerFree];
			ph.init(childEventHandler, p, errorCatcher);
			if(particleHandlerFree < particleHandlerMinFree) particleHandlerMinFree = particleHandlerFree;
			return ph;
		}
	}
	
	
	public void recycle(ParticleHandler psh){
	    particleHandlerRecycled++;	    	    
		if(particleHandlerFree == particleHandler.length){
		    if(particleHandler.length == particleHandlerMaxSize) return;
			ParticleHandler[] increased = new ParticleHandler[10+particleHandler.length];
			System.arraycopy(particleHandler, 0, increased, 0, particleHandlerFree);
			particleHandler = increased;
		}
		particleHandler[particleHandlerFree++] = psh;		
	}	
		
	
	public ChoiceHandler getChoiceHandler(SChoicePattern cp, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
	    choiceHandlerRequested++;
		if(choiceHandlerFree == 0){
			choiceHandlerCreated++;
			ChoiceHandler ch = new ChoiceHandler();
			ch.init(this, activeInputDescriptor, inputStackDescriptor);
			ch.init(cp, errorCatcher, parent, stackHandler);
			return ch;			
		}else{
			ChoiceHandler ch = choiceHandler[--choiceHandlerFree];			
			ch.init(cp, errorCatcher, parent, stackHandler);
			if(choiceHandlerFree < choiceHandlerMinFree) choiceHandlerMinFree = choiceHandlerFree;
			return ch;
		}		
	}
	
	public void recycle(ChoiceHandler ch){
	    choiceHandlerRecycled++;	    	    
		if(choiceHandlerFree == choiceHandler.length){
		    if(choiceHandler.length == choiceHandlerMaxSize) return;			
			ChoiceHandler[] increased = new ChoiceHandler[10+choiceHandler.length];
			System.arraycopy(choiceHandler, 0, increased, 0, choiceHandlerFree);
			choiceHandler = increased;
		}
		choiceHandler[choiceHandlerFree++] = ch;
	}
	
	public GroupHandler getGroupHandler(SGroup g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
	    groupHandlerRequested++;
		if(groupHandlerFree == 0){
			groupHandlerCreated++;
			GroupHandler gh = new GroupHandler();
			gh.init(this, activeInputDescriptor, inputStackDescriptor);			
			gh.init(g, errorCatcher, parent, stackHandler);
			return gh;			
		}else{
			GroupHandler gh = groupHandler[--groupHandlerFree];
			gh.init(g, errorCatcher, parent, stackHandler);
			if(groupHandlerFree < groupHandlerMinFree) groupHandlerMinFree = groupHandlerFree;
			return gh;
		}		
	}
	
	public void recycle(GroupHandler gh){
	    groupHandlerRecycled++;	    	    
		if(groupHandlerFree == groupHandler.length){
            if(groupHandler.length == groupHandlerMaxSize) return;			
			GroupHandler[] increased = new GroupHandler[10+groupHandler.length];
			System.arraycopy(groupHandler, 0, increased, 0, groupHandlerFree);
			groupHandler = increased;
		}
		groupHandler[groupHandlerFree++] = gh;
	}
		
	public IntermediaryPatternHandler getIntermediaryPatternHandler(SUniqueChildPattern r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(intermediaryPatternHandlerFree == 0){
			// intermediaryPatternHandlerCreated++;
			IntermediaryPatternHandler pih = new IntermediaryPatternHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(r, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			IntermediaryPatternHandler pih = intermediaryPatternHandler[--intermediaryPatternHandlerFree];
			pih.init(r, errorCatcher, parent, stackHandler);
			if(intermediaryPatternHandlerFree < intermediaryPatternHandlerMinFree) intermediaryPatternHandlerMinFree = intermediaryPatternHandlerFree;
			return pih;
		}		
	}	
	
	public void recycle(IntermediaryPatternHandler pih){	    
		if(intermediaryPatternHandlerFree == intermediaryPatternHandler.length){
            if(intermediaryPatternHandler.length == intermediaryPatternHandlerMaxSize) return;			
			IntermediaryPatternHandler[] increased = new IntermediaryPatternHandler[10+intermediaryPatternHandler.length];
			System.arraycopy(intermediaryPatternHandler, 0, increased, 0, intermediaryPatternHandlerFree);
			intermediaryPatternHandler = increased;
		}
		intermediaryPatternHandler[intermediaryPatternHandlerFree++] = pih;
	}	
		
	
	public UInterleaveHandler getUInterleaveHandler(SInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(uinterleaveHandlerFree == 0){
			// uinterleaveHandlerCreated++;
			UInterleaveHandler ih = new UInterleaveHandler();
			ih.init(this, activeInputDescriptor, inputStackDescriptor);
			ih.init(i, errorCatcher, parent, stackHandler);
			return ih;			
		}else{
			UInterleaveHandler ih = uinterleaveHandler[--uinterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler);
			if(uinterleaveHandlerFree < uinterleaveHandlerMinFree) uinterleaveHandlerMinFree = uinterleaveHandlerFree;
			return ih;
		}	
	}
	
	public void recycle(UInterleaveHandler ih){		
		if(uinterleaveHandlerFree == uinterleaveHandler.length){
            if(uinterleaveHandler.length == uinterleaveHandlerMaxSize) return;			
			UInterleaveHandler[] increased = new UInterleaveHandler[10+uinterleaveHandler.length];
			System.arraycopy(uinterleaveHandler, 0, increased, 0, uinterleaveHandlerFree);
			uinterleaveHandler = increased;
		}
		uinterleaveHandler[uinterleaveHandlerFree++] = ih;
	}

	public MInterleaveHandler getMInterleaveHandler(SInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(minterleaveHandlerFree == 0){
			// minterleaveHandlerCreated++;
			MInterleaveHandler ih = new MInterleaveHandler();
			ih.init(this, activeInputDescriptor, inputStackDescriptor);
			ih.init(i, errorCatcher, parent, stackHandler, this);
			return ih;			
		}else{
			MInterleaveHandler ih = minterleaveHandler[--minterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler, this);
			if(minterleaveHandlerFree < minterleaveHandlerMinFree) minterleaveHandlerMinFree = minterleaveHandlerFree;
			return ih;
		}	
	}
	
	public void recycle(MInterleaveHandler ih){		
		if(minterleaveHandlerFree == minterleaveHandler.length){
            if(minterleaveHandler.length == minterleaveHandlerMaxSize) return;			
			MInterleaveHandler[] increased = new MInterleaveHandler[10+minterleaveHandler.length];
			System.arraycopy(minterleaveHandler, 0, increased, 0, minterleaveHandlerFree);
			minterleaveHandler = increased;
		}
		minterleaveHandler[minterleaveHandlerFree++] = ih;
	}	
	
	public SInterleaveHandler getSInterleaveHandler(SMultipleChildrenPattern i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler, MInterleaveHandler primaryHandler){				
		if(sinterleaveHandlerFree == 0){
			// sinterleaveHandlerCreated++;
			SInterleaveHandler ih = new SInterleaveHandler();
			ih.init(this, activeInputDescriptor, inputStackDescriptor);
			ih.init(i, errorCatcher, parent, stackHandler, primaryHandler);
			return ih;			
		}else{
			SInterleaveHandler ih = sinterleaveHandler[--sinterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler, primaryHandler);
			if(sinterleaveHandlerFree < sinterleaveHandlerMinFree) sinterleaveHandlerMinFree = sinterleaveHandlerFree;
			return ih;
		}	
	}
	
	public void recycle(SInterleaveHandler ih){	    
		if(sinterleaveHandlerFree == sinterleaveHandler.length){
            if(sinterleaveHandler.length == sinterleaveHandlerMaxSize) return;			
			SInterleaveHandler[] increased = new SInterleaveHandler[10+sinterleaveHandler.length];
			System.arraycopy(sinterleaveHandler, 0, increased, 0, sinterleaveHandlerFree);
			sinterleaveHandler = increased;
		}
		sinterleaveHandler[sinterleaveHandlerFree++] = ih;
	}	
	
	
	public TypeHandler getTypeHandler(SRule a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(typeHandlerFree == 0){
			// typeHandlerCreated++;
			TypeHandler ah = new TypeHandler();
			ah.init(this, activeInputDescriptor, inputStackDescriptor);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			TypeHandler ah = typeHandler[--typeHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			if(typeHandlerFree < typeHandlerMinFree) typeHandlerMinFree = typeHandlerFree;
			return ah;
		}		
	}
	
	/*public TypeHandler getTypeHandler(SElement a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(typeHandlerFree == 0){
			// typeHandlerCreated++;
			TypeHandler ah = new TypeHandler();
			ah.init(this, activeInputDescriptor, inputStackDescriptor);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			TypeHandler ah = typeHandler[--typeHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			if(typeHandlerFree < typeHandlerMinFree) typeHandlerMinFree = typeHandlerFree;
			return ah;
		}		
	}
	
	public TypeHandler getTypeHandler(SAttribute a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(typeHandlerFree == 0){
			// typeHandlerCreated++;
			TypeHandler ah = new TypeHandler();
			ah.init(this, activeInputDescriptor, inputStackDescriptor);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			TypeHandler ah = typeHandler[--typeHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			if(typeHandlerFree < typeHandlerMinFree) typeHandlerMinFree = typeHandlerFree;
			return ah;
		}		
	}
	
	public TypeHandler getTypeHandler(SExceptPattern a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(typeHandlerFree == 0){
			// typeHandlerCreated++;
			TypeHandler ah = new TypeHandler();
			ah.init(this, activeInputDescriptor, inputStackDescriptor);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			TypeHandler ah = typeHandler[--typeHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			if(typeHandlerFree < typeHandlerMinFree) typeHandlerMinFree = typeHandlerFree;
			return ah;
		}		
	}
	
	public TypeHandler getTypeHandler(SListPattern a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(typeHandlerFree == 0){
			// typeHandlerCreated++;
			TypeHandler ah = new TypeHandler();
			ah.init(this, activeInputDescriptor, inputStackDescriptor);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			TypeHandler ah = typeHandler[--typeHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			if(typeHandlerFree < typeHandlerMinFree) typeHandlerMinFree = typeHandlerFree;
			return ah;
		}		
	}*/
	
	public void recycle(TypeHandler ah){	    
		if(typeHandlerFree == typeHandler.length){
            if(typeHandler.length == typeHandlerMaxSize) return;			
			TypeHandler[] increased = new TypeHandler[10+typeHandler.length];
			System.arraycopy(typeHandler, 0, increased, 0, typeHandlerFree);
			typeHandler = increased;
		}
		typeHandler[typeHandlerFree++] = ah;
	}
	
	
	
	
	public GroupDoubleHandler getGroupDoubleHandler(SGroup pattern, ErrorCatcher errorCatcher,  StructureHandler parent, StackHandler stackHandler/*, ValidatorStackHandlerPool stackHandlerPool*/){				
		if(groupDoubleHandlerFree == 0){
			// groupDoubleHandlerCreated++;
			GroupDoubleHandler sih = new GroupDoubleHandler();
			sih.init(this, activeInputDescriptor, inputStackDescriptor);
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			return sih;			
		}else{
			GroupDoubleHandler sih = groupDoubleHandler[--groupDoubleHandlerFree];
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			if(groupDoubleHandlerFree < groupDoubleHandlerMinFree) groupDoubleHandlerMinFree = groupDoubleHandlerFree;
			return sih;
		}		
	}
	
	public void recycle(GroupDoubleHandler sih){	    
		if(groupDoubleHandlerFree == groupDoubleHandler.length){
		    if(groupDoubleHandler.length == groupDoubleHandlerMaxSize) return;			
			GroupDoubleHandler[] increased = new GroupDoubleHandler[10+groupDoubleHandler.length];
			System.arraycopy(groupDoubleHandler, 0, increased, 0, groupDoubleHandlerFree);
			groupDoubleHandler = increased;
		}
		groupDoubleHandler[groupDoubleHandlerFree++] = sih;
	}
	
	public InterleaveDoubleHandler getInterleaveDoubleHandler(SInterleave pattern, ErrorCatcher errorCatcher,  StructureHandler parent, StackHandler stackHandler/*, ValidatorStackHandlerPool stackHandlerPool*/){				
		if(interleaveDoubleHandlerFree == 0){
			// interleaveDoubleHandlerCreated++;
			InterleaveDoubleHandler sih = new InterleaveDoubleHandler();
			sih.init(this, activeInputDescriptor, inputStackDescriptor);
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			return sih;			
		}else{
			InterleaveDoubleHandler sih = interleaveDoubleHandler[--interleaveDoubleHandlerFree];
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			if(interleaveDoubleHandlerFree < interleaveDoubleHandlerMinFree) interleaveDoubleHandlerMinFree = interleaveDoubleHandlerFree;
			return sih;
		}		
	}
	
	public void recycle(InterleaveDoubleHandler sih){
		if(interleaveDoubleHandlerFree == interleaveDoubleHandler.length){
    	    if(interleaveDoubleHandler.length == interleaveDoubleHandlerMaxSize) return;			
			InterleaveDoubleHandler[] increased = new InterleaveDoubleHandler[10+interleaveDoubleHandler.length];
			System.arraycopy(interleaveDoubleHandler, 0, increased, 0, interleaveDoubleHandlerFree);
			interleaveDoubleHandler = increased;
		}
		interleaveDoubleHandler[interleaveDoubleHandlerFree++] = sih;
	}
	
	public GroupMinimalReduceCountHandler getGroupMinimalReduceCountHandler(IntList reduceCountList, IntList startedCountList, SGroup g, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(groupMinimalReduceCountHandlerFree == 0){
			// groupMinimalReduceCountHandlerCreated++;
			GroupMinimalReduceCountHandler pih = new GroupMinimalReduceCountHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			return pih;			
		}else{
			GroupMinimalReduceCountHandler pih = groupMinimalReduceCountHandler[--groupMinimalReduceCountHandlerFree];			
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			if(groupMinimalReduceCountHandlerFree < groupMinimalReduceCountHandlerMinFree) groupMinimalReduceCountHandlerMinFree = groupMinimalReduceCountHandlerFree; 
			return pih;
		}		
	}

	public void recycle(GroupMinimalReduceCountHandler gh){	    
		if(groupMinimalReduceCountHandlerFree == groupMinimalReduceCountHandler.length){
		    if(groupMinimalReduceCountHandler.length == groupMinimalReduceCountHandlerMaxSize) return;			
			GroupMinimalReduceCountHandler[] increased = new GroupMinimalReduceCountHandler[10+groupMinimalReduceCountHandler.length];
			System.arraycopy(groupMinimalReduceCountHandler, 0, increased, 0, groupMinimalReduceCountHandlerFree);
			groupMinimalReduceCountHandler = increased;
		}
		groupMinimalReduceCountHandler[groupMinimalReduceCountHandlerFree++] = gh;
	}	
	
	public GroupMaximalReduceCountHandler getGroupMaximalReduceCountHandler(IntList reduceCountList, IntList startedCountList, SGroup g, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(groupMaximalReduceCountHandlerFree == 0){
			// groupMaximalReduceCountHandlerCreated++;
			GroupMaximalReduceCountHandler pih = new GroupMaximalReduceCountHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			return pih;			
		}else{
			GroupMaximalReduceCountHandler pih = groupMaximalReduceCountHandler[--groupMaximalReduceCountHandlerFree];			
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			if(groupMaximalReduceCountHandlerFree < groupMaximalReduceCountHandlerMinFree) groupMaximalReduceCountHandlerMinFree = groupMaximalReduceCountHandlerFree;
			return pih;
		}		
	}

	public void recycle(GroupMaximalReduceCountHandler gh){	    
		if(groupMaximalReduceCountHandlerFree == groupMaximalReduceCountHandler.length){
		    if(groupMaximalReduceCountHandler.length == groupMaximalReduceCountHandlerMaxSize) return;			
			GroupMaximalReduceCountHandler[] increased = new GroupMaximalReduceCountHandler[10+groupMaximalReduceCountHandler.length];
			System.arraycopy(groupMaximalReduceCountHandler, 0, increased, 0, groupMaximalReduceCountHandlerFree);
			groupMaximalReduceCountHandler = increased;
		}
		groupMaximalReduceCountHandler[groupMaximalReduceCountHandlerFree++] = gh;
	}

	public InterleaveMinimalReduceCountHandler getInterleaveMinimalReduceCountHandler(IntList reduceCountList, SInterleave i, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(interleaveMinimalReduceCountHandlerFree == 0){
			// interleaveMinimalReduceCountHandlerCreated++;
			InterleaveMinimalReduceCountHandler pih = new InterleaveMinimalReduceCountHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			return pih;			
		}else{
			InterleaveMinimalReduceCountHandler pih = interleaveMinimalReduceCountHandler[--interleaveMinimalReduceCountHandlerFree];			
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			if(interleaveMinimalReduceCountHandlerFree < interleaveMinimalReduceCountHandlerMinFree) interleaveMinimalReduceCountHandlerMinFree = interleaveMinimalReduceCountHandlerFree;
			return pih;
		}		
	}

	public void recycle(InterleaveMinimalReduceCountHandler ih){	    
		if(interleaveMinimalReduceCountHandlerFree == interleaveMinimalReduceCountHandler.length){
		    if(interleaveMinimalReduceCountHandler.length == interleaveMinimalReduceCountHandlerMaxSize) return;			
			InterleaveMinimalReduceCountHandler[] increased = new InterleaveMinimalReduceCountHandler[10+interleaveMinimalReduceCountHandler.length];
			System.arraycopy(interleaveMinimalReduceCountHandler, 0, increased, 0, interleaveMinimalReduceCountHandlerFree);
			interleaveMinimalReduceCountHandler = increased;
		}
		interleaveMinimalReduceCountHandler[interleaveMinimalReduceCountHandlerFree++] = ih;
	}	
	
	public InterleaveMaximalReduceCountHandler getInterleaveMaximalReduceCountHandler(IntList reduceCountList, SInterleave i, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(interleaveMaximalReduceCountHandlerFree == 0){
			// interleaveMaximalReduceCountHandlerCreated++;
			InterleaveMaximalReduceCountHandler pih = new InterleaveMaximalReduceCountHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			return pih;			
		}else{
			InterleaveMaximalReduceCountHandler pih = interleaveMaximalReduceCountHandler[--interleaveMaximalReduceCountHandlerFree];			
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			if(interleaveMaximalReduceCountHandlerFree < interleaveMaximalReduceCountHandlerMinFree) interleaveMaximalReduceCountHandlerMinFree = interleaveMaximalReduceCountHandlerFree; 
			return pih;
		}		
	}

	public void recycle(InterleaveMaximalReduceCountHandler ih){	    
		if(interleaveMaximalReduceCountHandlerFree == interleaveMaximalReduceCountHandler.length){
            if(interleaveMaximalReduceCountHandler.length == interleaveMaximalReduceCountHandlerMaxSize) return;			
			InterleaveMaximalReduceCountHandler[] increased = new InterleaveMaximalReduceCountHandler[10+interleaveMaximalReduceCountHandler.length];
			System.arraycopy(interleaveMaximalReduceCountHandler, 0, increased, 0, interleaveMaximalReduceCountHandlerFree);
			interleaveMaximalReduceCountHandler = increased;
		}
		interleaveMaximalReduceCountHandler[interleaveMaximalReduceCountHandlerFree++] = ih;
	}	
		
	
	public IntermediaryPatternMinimalReduceHandler getIntermediaryPatternMinimalReduceHandler(SUniqueChildPattern r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(intermediaryPatternMinimalReduceHandlerFree == 0){
			// intermediaryPatternMinimalReduceHandlerCreated++;
			IntermediaryPatternMinimalReduceHandler rmrh = new IntermediaryPatternMinimalReduceHandler();
			rmrh.init(this, activeInputDescriptor, inputStackDescriptor);
			rmrh.init(r, errorCatcher, parent, stackHandler);
			return rmrh;			
		}else{
			IntermediaryPatternMinimalReduceHandler rmrh = intermediaryPatternMinimalReduceHandler[--intermediaryPatternMinimalReduceHandlerFree];			
			rmrh.init(r, errorCatcher, parent, stackHandler);
			if(intermediaryPatternMinimalReduceHandlerFree < intermediaryPatternMinimalReduceHandlerMinFree) intermediaryPatternMinimalReduceHandlerMinFree = intermediaryPatternMinimalReduceHandlerFree;
			return rmrh;
		}		
	}

	public void recycle(IntermediaryPatternMinimalReduceHandler rmrh){	    
		if(intermediaryPatternMinimalReduceHandlerFree == intermediaryPatternMinimalReduceHandler.length){
		    if(intermediaryPatternMinimalReduceHandler.length == intermediaryPatternMinimalReduceHandlerMaxSize) return;			
			IntermediaryPatternMinimalReduceHandler[] increased = new IntermediaryPatternMinimalReduceHandler[10+intermediaryPatternMinimalReduceHandler.length];
			System.arraycopy(intermediaryPatternMinimalReduceHandler, 0, increased, 0, intermediaryPatternMinimalReduceHandlerFree);
			intermediaryPatternMinimalReduceHandler = increased;
		}
		intermediaryPatternMinimalReduceHandler[intermediaryPatternMinimalReduceHandlerFree++] = rmrh;
	}	
	
	public IntermediaryPatternMaximalReduceHandler getIntermediaryPatternMaximalReduceHandler(SUniqueChildPattern r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(intermediaryPatternMaximalReduceHandlerFree == 0){
			// intermediaryPatternMaximalReduceHandlerCreated++;
			IntermediaryPatternMaximalReduceHandler pih = new IntermediaryPatternMaximalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(r, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			IntermediaryPatternMaximalReduceHandler pih = intermediaryPatternMaximalReduceHandler[--intermediaryPatternMaximalReduceHandlerFree];			
			pih.init(r, errorCatcher, parent, stackHandler);
			if(intermediaryPatternMaximalReduceHandlerFree < intermediaryPatternMaximalReduceHandlerMinFree) intermediaryPatternMaximalReduceHandlerMinFree = intermediaryPatternMaximalReduceHandlerFree;
			return pih;
		}		
	}

	public void recycle(IntermediaryPatternMaximalReduceHandler rmrh){	    
		if(intermediaryPatternMaximalReduceHandlerFree == intermediaryPatternMaximalReduceHandler.length){
		    if(intermediaryPatternMaximalReduceHandler.length == intermediaryPatternMaximalReduceHandlerMaxSize) return;			
			IntermediaryPatternMaximalReduceHandler[] increased = new IntermediaryPatternMaximalReduceHandler[10+intermediaryPatternMaximalReduceHandler.length];
			System.arraycopy(intermediaryPatternMaximalReduceHandler, 0, increased, 0, intermediaryPatternMaximalReduceHandlerFree);
			intermediaryPatternMaximalReduceHandler = increased;
		}
		intermediaryPatternMaximalReduceHandler[intermediaryPatternMaximalReduceHandlerFree++] = rmrh;
	}

	public ChoiceMinimalReduceHandler getChoiceMinimalReduceHandler(SChoicePattern c, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(choiceMinimalReduceHandlerFree == 0){
			// choiceMinimalReduceHandlerCreated++;
			ChoiceMinimalReduceHandler pih = new ChoiceMinimalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(c, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			ChoiceMinimalReduceHandler pih = choiceMinimalReduceHandler[--choiceMinimalReduceHandlerFree];			
			pih.init(c, errorCatcher, parent, stackHandler);
			if(choiceMinimalReduceHandlerFree < choiceMinimalReduceHandlerMinFree) choiceMinimalReduceHandlerMinFree = choiceMinimalReduceHandlerFree;
			return pih;
		}		
	}

	public void recycle(ChoiceMinimalReduceHandler ch){	    
		if(choiceMinimalReduceHandlerFree == choiceMinimalReduceHandler.length){
		    if(choiceMinimalReduceHandler.length == choiceMinimalReduceHandlerMaxSize) return;			
			ChoiceMinimalReduceHandler[] increased = new ChoiceMinimalReduceHandler[10+choiceMinimalReduceHandler.length];
			System.arraycopy(choiceMinimalReduceHandler, 0, increased, 0, choiceMinimalReduceHandlerFree);
			choiceMinimalReduceHandler = increased;
		}
		choiceMinimalReduceHandler[choiceMinimalReduceHandlerFree++] = ch;
	}	
	
	public ChoiceMaximalReduceHandler getChoiceMaximalReduceHandler(SChoicePattern c, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(choiceMaximalReduceHandlerFree == 0){
			// choiceMaximalReduceHandlerCreated++;
			ChoiceMaximalReduceHandler pih = new ChoiceMaximalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(c, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			ChoiceMaximalReduceHandler pih = choiceMaximalReduceHandler[--choiceMaximalReduceHandlerFree];			
			pih.init(c, errorCatcher, parent, stackHandler);
			if(choiceMaximalReduceHandlerFree < choiceMaximalReduceHandlerMinFree) choiceMaximalReduceHandlerMinFree = choiceMaximalReduceHandlerFree;
			return pih;
		}		
	}

	public void recycle(ChoiceMaximalReduceHandler ch){	    
		if(choiceMaximalReduceHandlerFree == choiceMaximalReduceHandler.length){
		    if(choiceMaximalReduceHandler.length == choiceMaximalReduceHandlerMaxSize) return;			
			ChoiceMaximalReduceHandler[] increased = new ChoiceMaximalReduceHandler[10+choiceMaximalReduceHandler.length];
			System.arraycopy(choiceMaximalReduceHandler, 0, increased, 0, choiceMaximalReduceHandlerFree);
			choiceMaximalReduceHandler = increased;
		}
		choiceMaximalReduceHandler[choiceMaximalReduceHandlerFree++] = ch;
	}

	public GroupMinimalReduceHandler getGroupMinimalReduceHandler(SGroup g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(groupMinimalReduceHandlerFree == 0){
			// groupMinimalReduceHandlerCreated++;
			GroupMinimalReduceHandler pih = new GroupMinimalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GroupMinimalReduceHandler pih = groupMinimalReduceHandler[--groupMinimalReduceHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			if(groupMinimalReduceHandlerFree < groupMinimalReduceHandlerMinFree) groupMinimalReduceHandlerMinFree = groupMinimalReduceHandlerFree;
			return pih;
		}		
	}

	public void recycle(GroupMinimalReduceHandler gh){	    
		if(groupMinimalReduceHandlerFree == groupMinimalReduceHandler.length){
		    if(groupMinimalReduceHandler.length == groupMinimalReduceHandlerMaxSize) return;			
			GroupMinimalReduceHandler[] increased = new GroupMinimalReduceHandler[10+groupMinimalReduceHandler.length];
			System.arraycopy(groupMinimalReduceHandler, 0, increased, 0, groupMinimalReduceHandlerFree);
			groupMinimalReduceHandler = increased;
		}
		groupMinimalReduceHandler[groupMinimalReduceHandlerFree++] = gh;
	}	
	
	public GroupMaximalReduceHandler getGroupMaximalReduceHandler(SGroup g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(groupMaximalReduceHandlerFree == 0){
			// groupMaximalReduceHandlerCreated++;
			GroupMaximalReduceHandler pih = new GroupMaximalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GroupMaximalReduceHandler pih = groupMaximalReduceHandler[--groupMaximalReduceHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			if(groupMaximalReduceHandlerFree < groupMaximalReduceHandlerMinFree) groupMaximalReduceHandlerMinFree = groupMaximalReduceHandlerFree;
			return pih;
		}		
	}

	public void recycle(GroupMaximalReduceHandler gh){	    
		if(groupMaximalReduceHandlerFree == groupMaximalReduceHandler.length){
		    if(groupMaximalReduceHandler.length == groupMaximalReduceHandlerMaxSize) return;			
			GroupMaximalReduceHandler[] increased = new GroupMaximalReduceHandler[10+groupMaximalReduceHandler.length];
			System.arraycopy(groupMaximalReduceHandler, 0, increased, 0, groupMaximalReduceHandlerFree);
			groupMaximalReduceHandler = increased;
		}
		groupMaximalReduceHandler[groupMaximalReduceHandlerFree++] = gh;
	}

	public InterleaveMinimalReduceHandler getInterleaveMinimalReduceHandler(SInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(interleaveMinimalReduceHandlerFree == 0){
			// interleaveMinimalReduceHandlerCreated++;
			InterleaveMinimalReduceHandler pih = new InterleaveMinimalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(i, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			InterleaveMinimalReduceHandler pih = interleaveMinimalReduceHandler[--interleaveMinimalReduceHandlerFree];			
			pih.init(i, errorCatcher, parent, stackHandler);
			if(interleaveMinimalReduceHandlerFree < interleaveMinimalReduceHandlerMinFree) interleaveMinimalReduceHandlerMinFree = interleaveMinimalReduceHandlerFree;
			return pih;
		}		
	}

	public void recycle(InterleaveMinimalReduceHandler ih){	    
		if(interleaveMinimalReduceHandlerFree == interleaveMinimalReduceHandler.length){
		    if(interleaveMinimalReduceHandler.length == interleaveMinimalReduceHandlerMaxSize) return;			
			InterleaveMinimalReduceHandler[] increased = new InterleaveMinimalReduceHandler[10+interleaveMinimalReduceHandler.length];
			System.arraycopy(interleaveMinimalReduceHandler, 0, increased, 0, interleaveMinimalReduceHandlerFree);
			interleaveMinimalReduceHandler = increased;
		}
		interleaveMinimalReduceHandler[interleaveMinimalReduceHandlerFree++] = ih;
	}	
	
	public InterleaveMaximalReduceHandler getInterleaveMaximalReduceHandler(SInterleave i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(interleaveMaximalReduceHandlerFree == 0){
			// interleaveMaximalReduceHandlerCreated++;
			InterleaveMaximalReduceHandler pih = new InterleaveMaximalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(i, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			InterleaveMaximalReduceHandler pih = interleaveMaximalReduceHandler[--interleaveMaximalReduceHandlerFree];			
			pih.init(i, errorCatcher, parent, stackHandler);
			if(interleaveMaximalReduceHandlerFree < interleaveMaximalReduceHandlerMinFree) interleaveMaximalReduceHandlerMinFree = interleaveMaximalReduceHandlerFree;
			return pih;
		}		
	}

	public void recycle(InterleaveMaximalReduceHandler ih){	    
		if(interleaveMaximalReduceHandlerFree == interleaveMaximalReduceHandler.length){
		    if(interleaveMaximalReduceHandler.length == interleaveMaximalReduceHandlerMaxSize) return;			
			InterleaveMaximalReduceHandler[] increased = new InterleaveMaximalReduceHandler[10+interleaveMaximalReduceHandler.length];
			System.arraycopy(interleaveMaximalReduceHandler, 0, increased, 0, interleaveMaximalReduceHandlerFree);
			interleaveMaximalReduceHandler = increased;
		}
		interleaveMaximalReduceHandler[interleaveMaximalReduceHandlerFree++] = ih;
	}


	// For Type
	public StructureHandler getStructureHandler(SRule p, ErrorCatcher errorCatcher, StackHandler stackHandler){
        // TODO
        handler = null;
        this.errorCatcher = errorCatcher;
        this.stackHandler = stackHandler; 
        
        p.accept(this);
        
        handlerId = -1;
        this.errorCatcher = null;
        this.stackHandler = null;
        return handler;
    }

    // For compositor in double handler
    public StructureHandler getMinimalReduceCountHandler(SMultipleChildrenPattern p, IntList reduceCountList, ErrorCatcher errorCatcher, StackHandler stackHandler){
        // TODO
        handler = null;
        handlerId = MIN_REDUCE_COUNT;
        this.reduceCountList = reduceCountList;
        this.errorCatcher = errorCatcher;
        this.stackHandler = stackHandler;
        
        p.accept(this);
        
        handlerId = -1;
        this.reduceCountList = null;
        this.errorCatcher = null;
        this.stackHandler = null;
        
        return handler;
    }
    
    public StructureHandler getMaximalReduceCountHandler(SMultipleChildrenPattern p, IntList reduceCountList, ErrorCatcher errorCatcher, StackHandler stackHandler){
        // TODO
        handler = null;
        handlerId = MAX_REDUCE_COUNT;
        this.reduceCountList = reduceCountList;
        this.errorCatcher = errorCatcher;
        this.stackHandler = stackHandler;
        
        p.accept(this);
        
        handlerId = -1;
        this.reduceCountList = null;
        this.errorCatcher = null;
        this.stackHandler = null;
        return handler;
    }
    
    // For compositor in double handler
    public StructureHandler getMinimalReduceCountHandler(SMultipleChildrenPattern p, IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, StackHandler stackHandler){
        // TODO
        handler = null;
        handlerId = MIN_REDUCE_COUNT;
        this.reduceCountList = reduceCountList;
        this.startedCountList = startedCountList;
        this.errorCatcher = errorCatcher;
        this.stackHandler = stackHandler;
        
        p.accept(this);
        
        handlerId = -1;
        this.reduceCountList = null;
        this.startedCountList = null;
        this.errorCatcher = null;
        this.stackHandler = null;
        return handler;
    }
    
    // For compositor in double handler
    public StructureHandler getMaximalReduceCountHandler(SMultipleChildrenPattern p, IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, StackHandler stackHandler){
        // TODO
        handler = null;
        handlerId = MAX_REDUCE_COUNT;
        this.reduceCountList = reduceCountList;
        this.startedCountList = startedCountList;
        this.errorCatcher = errorCatcher;
        this.stackHandler = stackHandler;
        
        p.accept(this);
        
        handlerId = -1;
        this.reduceCountList = null;
        this.startedCountList = null;
        this.errorCatcher = null;
        this.stackHandler = null;
        return handler;
    }

    // For child handler
    public StructureHandler getStructureHandler(SRule p, MatchPath path, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
                
        handler = null;
        handlerId = SIMPLE;
        this.path = path;
        this.parent = parent;
        this.errorCatcher = errorCatcher;
        this.stackHandler = stackHandler;   
        
        p.accept(this);
        
        handlerId = -1;
        this.path = null;
        this.parent = null;
        this.errorCatcher = null;
        this.stackHandler = null;
        return handler;
    }
    

    // For child handler in maximal reduce situations
    public StructureHandler getStructureHandler(SRule p, ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
        // TODO        
        handler = null;
        handlerId = MAX_REDUCE;
        this.parent = parent;
        this.errorCatcher = errorCatcher;
        this.stackHandler = stackHandler;
        
        p.accept(this);
        
        handlerId = -1;
        this.parent = null;
        this.errorCatcher = null;
        this.stackHandler = null;
        return handler;
    }

    // For child handler in minimal reduce situations
    public StructureHandler getStructureHandler(SRule p, ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
        // TODO
        handler = null;
        handlerId = MIN_REDUCE;
        this.parent = parent;
        this.errorCatcher = errorCatcher;
        this.stackHandler = stackHandler;
        
        p.accept(this);
        
        handlerId = -1;
        this.parent = null;
        this.errorCatcher = null;
        this.stackHandler = null;
        return handler;
    }	
    
 
    
    public ParticleHandler getCopy(ParticleHandler original, ChildEventHandler childEventHandler, ErrorCatcher errorCatcher){
	    particleHandlerRequested++;
		if(particleHandlerFree == 0){
			particleHandlerCreated++;			
			ParticleHandler ph = new ParticleHandler();
			ph.init(activeInputDescriptor, this);
			ph.init(childEventHandler, original.getPattern(), errorCatcher);
			return ph;
		}else{		    
			ParticleHandler ph = particleHandler[--particleHandlerFree];
			ph.init(childEventHandler, original.getPattern(), errorCatcher);
			if(particleHandlerFree < particleHandlerMinFree) particleHandlerMinFree = particleHandlerFree;
			return ph;
		}
	}
	
	
	public ChoiceHandler getCopy(ChoiceHandler original, SChoicePattern cp, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
	    choiceHandlerRequested++;
		if(choiceHandlerFree == 0){
			choiceHandlerCreated++;
			ChoiceHandler ch = new ChoiceHandler();
			ch.init(this, activeInputDescriptor, inputStackDescriptor);
			ch.init(cp, errorCatcher, parent, stackHandler);
			return ch;			
		}else{
			ChoiceHandler ch = choiceHandler[--choiceHandlerFree];			
			ch.init(cp, errorCatcher, parent, stackHandler);
			if(choiceHandlerFree < choiceHandlerMinFree) choiceHandlerMinFree = choiceHandlerFree;
			return ch;
		}		
	}
	
	public GroupHandler getCopy(GroupHandler original, SMultipleChildrenPattern g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
	    groupHandlerRequested++;
		if(groupHandlerFree == 0){
			groupHandlerCreated++;
			GroupHandler gh = new GroupHandler();
			gh.init(this, activeInputDescriptor, inputStackDescriptor);			
			gh.init(g, errorCatcher, parent, stackHandler);
			return gh;			
		}else{
			GroupHandler gh = groupHandler[--groupHandlerFree];
			gh.init(g, errorCatcher, parent, stackHandler);
			if(groupHandlerFree < groupHandlerMinFree) groupHandlerMinFree = groupHandlerFree;
			return gh;
		}		
	}
		
	public IntermediaryPatternHandler getCopy(IntermediaryPatternHandler original, SUniqueChildPattern r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(intermediaryPatternHandlerFree == 0){
			// intermediaryPatternHandlerCreated++;
			IntermediaryPatternHandler pih = new IntermediaryPatternHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(r, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			IntermediaryPatternHandler pih = intermediaryPatternHandler[--intermediaryPatternHandlerFree];
			pih.init(r, errorCatcher, parent, stackHandler);
			if(intermediaryPatternHandlerFree < intermediaryPatternHandlerMinFree) intermediaryPatternHandlerMinFree = intermediaryPatternHandlerFree;
			return pih;
		}		
	}	
		
		
	public UInterleaveHandler getCopy(UInterleaveHandler original, SMultipleChildrenPattern i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(uinterleaveHandlerFree == 0){
			// uinterleaveHandlerCreated++;
			UInterleaveHandler ih = new UInterleaveHandler();
			ih.init(this, activeInputDescriptor, inputStackDescriptor);
			ih.init(i, errorCatcher, parent, stackHandler);
			return ih;			
		}else{
			UInterleaveHandler ih = uinterleaveHandler[--uinterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler);
			if(uinterleaveHandlerFree < uinterleaveHandlerMinFree) uinterleaveHandlerMinFree = uinterleaveHandlerFree;
			return ih;
		}	
	}
	
	public MInterleaveHandler getCopy(MInterleaveHandler original, SMultipleChildrenPattern i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(minterleaveHandlerFree == 0){
			// minterleaveHandlerCreated++;
			MInterleaveHandler ih = new MInterleaveHandler();
			ih.init(this, activeInputDescriptor, inputStackDescriptor);
			ih.init(i, errorCatcher, parent, stackHandler, this);
			return ih;			
		}else{
			MInterleaveHandler ih = minterleaveHandler[--minterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler, this);
			if(minterleaveHandlerFree < minterleaveHandlerMinFree) minterleaveHandlerMinFree = minterleaveHandlerFree;
			return ih;
		}	
	}
	
	public SInterleaveHandler getCopy(SInterleaveHandler original, SMultipleChildrenPattern i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler, MInterleaveHandler primaryHandler){				
		if(sinterleaveHandlerFree == 0){
			// sinterleaveHandlerCreated++;
			SInterleaveHandler ih = new SInterleaveHandler();
			ih.init(this, activeInputDescriptor, inputStackDescriptor);
			ih.init(i, errorCatcher, parent, stackHandler, primaryHandler);
			return ih;			
		}else{
			SInterleaveHandler ih = sinterleaveHandler[--sinterleaveHandlerFree];
			ih.init(i, errorCatcher, parent, stackHandler, primaryHandler);
			if(sinterleaveHandlerFree < sinterleaveHandlerMinFree) sinterleaveHandlerMinFree = sinterleaveHandlerFree;
			return ih;
		}	
	}
	
	
	
	
	public TypeHandler getCopy(TypeHandler original, SRule a, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(typeHandlerFree == 0){
			// typeHandlerCreated++;
			TypeHandler ah = new TypeHandler();
			ah.init(this, activeInputDescriptor, inputStackDescriptor);
			ah.init(a, errorCatcher, stackHandler);
			return ah;			
		}else{
			TypeHandler ah = typeHandler[--typeHandlerFree];
			ah.init(a, errorCatcher, stackHandler);
			if(typeHandlerFree < typeHandlerMinFree) typeHandlerMinFree = typeHandlerFree;
			return ah;
		}		
	}
	
	
	
	public GroupDoubleHandler getCopy(GroupDoubleHandler original, SMultipleChildrenPattern pattern, ErrorCatcher errorCatcher,  StructureHandler parent, StackHandler stackHandler/*, ValidatorStackHandlerPool stackHandlerPool*/){				
		if(groupDoubleHandlerFree == 0){
			// groupDoubleHandlerCreated++;
			GroupDoubleHandler sih = new GroupDoubleHandler();
			sih.init(this, activeInputDescriptor, inputStackDescriptor);
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			return sih;			
		}else{
			GroupDoubleHandler sih = groupDoubleHandler[--groupDoubleHandlerFree];
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			if(groupDoubleHandlerFree < groupDoubleHandlerMinFree) groupDoubleHandlerMinFree = groupDoubleHandlerFree;
			return sih;
		}		
	}
	
	public InterleaveDoubleHandler getCopy(InterleaveDoubleHandler original, SMultipleChildrenPattern pattern, ErrorCatcher errorCatcher,  StructureHandler parent, StackHandler stackHandler/*, ValidatorStackHandlerPool stackHandlerPool*/){				
		if(interleaveDoubleHandlerFree == 0){
			// interleaveDoubleHandlerCreated++;
			InterleaveDoubleHandler sih = new InterleaveDoubleHandler();
			sih.init(this, activeInputDescriptor, inputStackDescriptor);
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			return sih;			
		}else{
			InterleaveDoubleHandler sih = interleaveDoubleHandler[--interleaveDoubleHandlerFree];
			sih.init(pattern, errorCatcher, parent, stackHandler, stackHandlerPool);
			if(interleaveDoubleHandlerFree < interleaveDoubleHandlerMinFree) interleaveDoubleHandlerMinFree = interleaveDoubleHandlerFree;
			return sih;
		}		
	}
	
	
	public GroupMinimalReduceCountHandler getCopy(GroupMinimalReduceCountHandler original, SMultipleChildrenPattern g, IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(groupMinimalReduceCountHandlerFree == 0){
			// groupMinimalReduceCountHandlerCreated++;
			GroupMinimalReduceCountHandler pih = new GroupMinimalReduceCountHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			return pih;			
		}else{
			GroupMinimalReduceCountHandler pih = groupMinimalReduceCountHandler[--groupMinimalReduceCountHandlerFree];			
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			if(groupMinimalReduceCountHandlerFree < groupMinimalReduceCountHandlerMinFree) groupMinimalReduceCountHandlerMinFree = groupMinimalReduceCountHandlerFree; 
			return pih;
		}		
	}

	
	public GroupMaximalReduceCountHandler getCopy(GroupMaximalReduceCountHandler original, SMultipleChildrenPattern g, IntList reduceCountList, IntList startedCountList, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(groupMaximalReduceCountHandlerFree == 0){
			// groupMaximalReduceCountHandlerCreated++;
			GroupMaximalReduceCountHandler pih = new GroupMaximalReduceCountHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			return pih;			
		}else{
			GroupMaximalReduceCountHandler pih = groupMaximalReduceCountHandler[--groupMaximalReduceCountHandlerFree];			
			pih.init(reduceCountList, startedCountList, g, errorCatcher, stackHandler);
			if(groupMaximalReduceCountHandlerFree < groupMaximalReduceCountHandlerMinFree) groupMaximalReduceCountHandlerMinFree = groupMaximalReduceCountHandlerFree;
			return pih;
		}		
	}

	
	public InterleaveMinimalReduceCountHandler getCopy(InterleaveMinimalReduceCountHandler original, SMultipleChildrenPattern i, IntList reduceCountList, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(interleaveMinimalReduceCountHandlerFree == 0){
			// interleaveMinimalReduceCountHandlerCreated++;
			InterleaveMinimalReduceCountHandler pih = new InterleaveMinimalReduceCountHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			return pih;			
		}else{
			InterleaveMinimalReduceCountHandler pih = interleaveMinimalReduceCountHandler[--interleaveMinimalReduceCountHandlerFree];			
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			if(interleaveMinimalReduceCountHandlerFree < interleaveMinimalReduceCountHandlerMinFree) interleaveMinimalReduceCountHandlerMinFree = interleaveMinimalReduceCountHandlerFree;
			return pih;
		}		
	}

	
	public InterleaveMaximalReduceCountHandler getCopy(InterleaveMaximalReduceCountHandler original, SMultipleChildrenPattern i, IntList reduceCountList, ErrorCatcher errorCatcher, StackHandler stackHandler){				
		if(interleaveMaximalReduceCountHandlerFree == 0){
			// interleaveMaximalReduceCountHandlerCreated++;
			InterleaveMaximalReduceCountHandler pih = new InterleaveMaximalReduceCountHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			return pih;			
		}else{
			InterleaveMaximalReduceCountHandler pih = interleaveMaximalReduceCountHandler[--interleaveMaximalReduceCountHandlerFree];			
			pih.init(reduceCountList, i, errorCatcher, stackHandler);
			if(interleaveMaximalReduceCountHandlerFree < interleaveMaximalReduceCountHandlerMinFree) interleaveMaximalReduceCountHandlerMinFree = interleaveMaximalReduceCountHandlerFree; 
			return pih;
		}		
	}

		
	
	
	public IntermediaryPatternMinimalReduceHandler getCopy(IntermediaryPatternMinimalReduceHandler orioginal, SUniqueChildPattern r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(intermediaryPatternMinimalReduceHandlerFree == 0){
			// intermediaryPatternMinimalReduceHandlerCreated++;
			IntermediaryPatternMinimalReduceHandler rmrh = new IntermediaryPatternMinimalReduceHandler();
			rmrh.init(this, activeInputDescriptor, inputStackDescriptor);
			rmrh.init(r, errorCatcher, parent, stackHandler);
			return rmrh;			
		}else{
			IntermediaryPatternMinimalReduceHandler rmrh = intermediaryPatternMinimalReduceHandler[--intermediaryPatternMinimalReduceHandlerFree];			
			rmrh.init(r, errorCatcher, parent, stackHandler);
			if(intermediaryPatternMinimalReduceHandlerFree < intermediaryPatternMinimalReduceHandlerMinFree) intermediaryPatternMinimalReduceHandlerMinFree = intermediaryPatternMinimalReduceHandlerFree;
			return rmrh;
		}		
	}

	
	public IntermediaryPatternMaximalReduceHandler getCopy(IntermediaryPatternMaximalReduceHandler original, SUniqueChildPattern r, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(intermediaryPatternMaximalReduceHandlerFree == 0){
			// intermediaryPatternMaximalReduceHandlerCreated++;
			IntermediaryPatternMaximalReduceHandler pih = new IntermediaryPatternMaximalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(r, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			IntermediaryPatternMaximalReduceHandler pih = intermediaryPatternMaximalReduceHandler[--intermediaryPatternMaximalReduceHandlerFree];			
			pih.init(r, errorCatcher, parent, stackHandler);
			if(intermediaryPatternMaximalReduceHandlerFree < intermediaryPatternMaximalReduceHandlerMinFree) intermediaryPatternMaximalReduceHandlerMinFree = intermediaryPatternMaximalReduceHandlerFree;
			return pih;
		}		
	}

	
	public ChoiceMinimalReduceHandler getCopy(ChoiceMinimalReduceHandler original, SChoicePattern c, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(choiceMinimalReduceHandlerFree == 0){
			// choiceMinimalReduceHandlerCreated++;
			ChoiceMinimalReduceHandler pih = new ChoiceMinimalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(c, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			ChoiceMinimalReduceHandler pih = choiceMinimalReduceHandler[--choiceMinimalReduceHandlerFree];			
			pih.init(c, errorCatcher, parent, stackHandler);
			if(choiceMinimalReduceHandlerFree < choiceMinimalReduceHandlerMinFree) choiceMinimalReduceHandlerMinFree = choiceMinimalReduceHandlerFree;
			return pih;
		}		
	}

	
	public ChoiceMaximalReduceHandler getCopy(ChoiceMaximalReduceHandler original, SChoicePattern c, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(choiceMaximalReduceHandlerFree == 0){
			// choiceMaximalReduceHandlerCreated++;
			ChoiceMaximalReduceHandler pih = new ChoiceMaximalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(c, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			ChoiceMaximalReduceHandler pih = choiceMaximalReduceHandler[--choiceMaximalReduceHandlerFree];			
			pih.init(c, errorCatcher, parent, stackHandler);
			if(choiceMaximalReduceHandlerFree < choiceMaximalReduceHandlerMinFree) choiceMaximalReduceHandlerMinFree = choiceMaximalReduceHandlerFree;
			return pih;
		}		
	}

	
	public GroupMinimalReduceHandler getCopy(GroupMinimalReduceHandler original, SMultipleChildrenPattern g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(groupMinimalReduceHandlerFree == 0){
			// groupMinimalReduceHandlerCreated++;
			GroupMinimalReduceHandler pih = new GroupMinimalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GroupMinimalReduceHandler pih = groupMinimalReduceHandler[--groupMinimalReduceHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			if(groupMinimalReduceHandlerFree < groupMinimalReduceHandlerMinFree) groupMinimalReduceHandlerMinFree = groupMinimalReduceHandlerFree;
			return pih;
		}		
	}

	
	public GroupMaximalReduceHandler getCopy(GroupMaximalReduceHandler original, SMultipleChildrenPattern g, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(groupMaximalReduceHandlerFree == 0){
			// groupMaximalReduceHandlerCreated++;
			GroupMaximalReduceHandler pih = new GroupMaximalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(g, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			GroupMaximalReduceHandler pih = groupMaximalReduceHandler[--groupMaximalReduceHandlerFree];			
			pih.init(g, errorCatcher, parent, stackHandler);
			if(groupMaximalReduceHandlerFree < groupMaximalReduceHandlerMinFree) groupMaximalReduceHandlerMinFree = groupMaximalReduceHandlerFree;
			return pih;
		}		
	}

	
	public InterleaveMinimalReduceHandler getCopy(InterleaveMinimalReduceHandler original, SMultipleChildrenPattern i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(interleaveMinimalReduceHandlerFree == 0){
			// interleaveMinimalReduceHandlerCreated++;
			InterleaveMinimalReduceHandler pih = new InterleaveMinimalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(i, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			InterleaveMinimalReduceHandler pih = interleaveMinimalReduceHandler[--interleaveMinimalReduceHandlerFree];			
			pih.init(i, errorCatcher, parent, stackHandler);
			if(interleaveMinimalReduceHandlerFree < interleaveMinimalReduceHandlerMinFree) interleaveMinimalReduceHandlerMinFree = interleaveMinimalReduceHandlerFree;
			return pih;
		}		
	}

	
	public InterleaveMaximalReduceHandler getCopy(InterleaveMaximalReduceHandler original, SMultipleChildrenPattern i, ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){				
		if(interleaveMaximalReduceHandlerFree == 0){
			// interleaveMaximalReduceHandlerCreated++;
			InterleaveMaximalReduceHandler pih = new InterleaveMaximalReduceHandler();
			pih.init(this, activeInputDescriptor, inputStackDescriptor);
			pih.init(i, errorCatcher, parent, stackHandler);
			return pih;			
		}else{
			InterleaveMaximalReduceHandler pih = interleaveMaximalReduceHandler[--interleaveMaximalReduceHandlerFree];			
			pih.init(i, errorCatcher, parent, stackHandler);
			if(interleaveMaximalReduceHandlerFree < interleaveMaximalReduceHandlerMinFree) interleaveMaximalReduceHandlerMinFree = interleaveMaximalReduceHandlerFree;
			return pih;
		}		
	}
	
	public void visit(SExceptPattern component){
	    handler = getTypeHandler(component, errorCatcher, stackHandler);
	    return;
	}
	
	public void visit(SElement component){
	    handler = getTypeHandler(component, errorCatcher, stackHandler);
	    return;
	}
	public void visit(SAttribute component){
	    handler = getTypeHandler(component, errorCatcher, stackHandler);
	    return;
	}
	public void visit(SListPattern component){
	    handler = getTypeHandler(component, errorCatcher, stackHandler);
	    return;
	}
	
	public void visit(SChoicePattern component){
	    switch(handlerId){
	    case SIMPLE:
	        handler = getChoiceHandler(component, errorCatcher, parent, stackHandler);
	        return;    
	    case MIN_REDUCE:
	        handler = getChoiceMinimalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    case MAX_REDUCE:
	        handler = getChoiceMaximalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    }	    
	}
	public void visit(SInterleave component){
	    switch(handlerId){
	    case SIMPLE:
	        if(path.ruleRequiresDoubleHandler(component)){
	            handler = getInterleaveDoubleHandler(component, errorCatcher, parent, stackHandler);
	        }else{
	            if(path.ruleHasMultipleCardinality(component))
	                handler = getMInterleaveHandler(component, errorCatcher, parent, stackHandler);
	            else{
	                handler = getUInterleaveHandler(component, errorCatcher, parent, stackHandler);
	            }
	        }
	        return;
	    case MIN_REDUCE_COUNT:
	        handler = getInterleaveMinimalReduceCountHandler(reduceCountList, component, errorCatcher, stackHandler);
	        return;
	    case MAX_REDUCE_COUNT:
	        handler = getInterleaveMaximalReduceCountHandler(reduceCountList, component, errorCatcher, stackHandler);
	        return;
	    case MIN_REDUCE:
	        handler = getInterleaveMinimalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    case MAX_REDUCE:
	        handler = getInterleaveMaximalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    }
	}
	public void visit(SGroup component){
	    switch(handlerId){
	    case SIMPLE:
	        if(path.ruleRequiresDoubleHandler(component)){
	            handler = getGroupDoubleHandler(component, errorCatcher, parent, stackHandler);
	        }else{
	            handler = getGroupHandler(component, errorCatcher, parent, stackHandler);
	        }
	        return;
	    case MIN_REDUCE_COUNT:
	        handler = getGroupMinimalReduceCountHandler(reduceCountList, startedCountList, component, errorCatcher, stackHandler);
	        return;
	    case MAX_REDUCE_COUNT:
	        handler = getGroupMaximalReduceCountHandler(reduceCountList, startedCountList, component, errorCatcher, stackHandler);
	        return;
	    case MIN_REDUCE:
	        handler = getGroupMinimalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    case MAX_REDUCE:
	        handler = getGroupMaximalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    }
	}		
	
	public void visit(SRef component){
	    switch(handlerId){
	    case SIMPLE:
	        handler = getIntermediaryPatternHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    case MIN_REDUCE:
	        handler = getIntermediaryPatternMinimalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    case MAX_REDUCE:
	        handler = getIntermediaryPatternMaximalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    }
	}	
	public void visit(SGrammar component){
	    switch(handlerId){
	    case SIMPLE:
	        handler = getIntermediaryPatternHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    case MIN_REDUCE:
	        handler = getIntermediaryPatternMinimalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    case MAX_REDUCE:
	        handler = getIntermediaryPatternMaximalReduceHandler(component, errorCatcher, parent, stackHandler);
	        return;
	    }	
	}
	
	
	public void visit(SDummy component){
	    throw new IllegalStateException();
	}	
	public void visit(SEmpty component){
	    throw new IllegalStateException();
	}
	public void visit(SText component){
	    throw new IllegalStateException();
	}
	public void visit(SNotAllowed component){
	    throw new IllegalStateException();
	}
	public void visit(SValue component){
	    throw new IllegalStateException();
	}
	public void visit(SData component){
	    throw new IllegalStateException();
	}
}