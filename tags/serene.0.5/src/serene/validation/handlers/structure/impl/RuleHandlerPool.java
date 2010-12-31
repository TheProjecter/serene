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



import sereneWrite.MessageWriter;

public class RuleHandlerPool{
	private static volatile RuleHandlerPool instance;
	
	int vshPoolFree; 
	int vshPoolPoolSize;
	ActiveModelRuleHandlerPool[] vshPools;
	
	int particleHandlerAverageUse;
	int particleHandlerPoolSize;
	int particleHandlerFree;
	ParticleHandler[] particleHandler;
	
	int choiceHandlerAverageUse;
	int choiceHandlerPoolSize;
	int choiceHandlerFree;
	ChoiceHandler[] choiceHandler;
	
	int groupHandlerAverageUse;
	int groupHandlerPoolSize;
	int groupHandlerFree;
	GroupHandler[] groupHandler;
		
	int grammarHandlerAverageUse;
	int grammarHandlerPoolSize;
	int grammarHandlerFree;
	GrammarHandler[] grammarHandler;	
	
	int refHandlerAverageUse;
	int refHandlerPoolSize;
	int refHandlerFree;
	RefHandler[] refHandler;
		
	int uinterleaveHandlerAverageUse;
	int uinterleaveHandlerPoolSize;
	int uinterleaveHandlerFree;
	UInterleaveHandler[] uinterleaveHandler;
	
	int minterleaveHandlerAverageUse;
	int minterleaveHandlerPoolSize;
	int minterleaveHandlerFree;
	MInterleaveHandler[] minterleaveHandler;
	
	int sinterleaveHandlerAverageUse;
	int sinterleaveHandlerPoolSize;
	int sinterleaveHandlerFree;
	SInterleaveHandler[] sinterleaveHandler;
	
	int elementHandlerAverageUse;
	int elementHandlerPoolSize;
	int elementHandlerFree;
	ElementHandler[] elementHandler;
	
	int attributeHandlerAverageUse;
	int attributeHandlerPoolSize;
	int attributeHandlerFree;
	AttributeHandler[] attributeHandler;
	
	int exceptPatternHandlerAverageUse;
	int exceptPatternHandlerPoolSize;
	int exceptPatternHandlerFree;
	ExceptPatternHandler[] exceptPatternHandler;
	
	int listPatternHandlerAverageUse;
	int listPatternHandlerPoolSize;
	int listPatternHandlerFree;
	ListPatternHandler[] listPatternHandler;
	
	
	
	int groupDoubleHandlerAverageUse;
	int groupDoubleHandlerPoolSize;
	int groupDoubleHandlerFree;
	GroupDoubleHandler[] groupDoubleHandler;
	
	int interleaveDoubleHandlerAverageUse;
	int interleaveDoubleHandlerPoolSize;
	int interleaveDoubleHandlerFree;
	InterleaveDoubleHandler[] interleaveDoubleHandler;
	
	
	
	int groupMinimalReduceCountHandlerAverageUse;
	int groupMinimalReduceCountHandlerPoolSize;
	int groupMinimalReduceCountHandlerFree;
	GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandler;
	
	int groupMaximalReduceCountHandlerAverageUse;
	int groupMaximalReduceCountHandlerPoolSize;
	int groupMaximalReduceCountHandlerFree;
	GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandler;
	
	int interleaveMinimalReduceCountHandlerAverageUse;
	int interleaveMinimalReduceCountHandlerPoolSize;
	int interleaveMinimalReduceCountHandlerFree;
	InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandler;
	
	int interleaveMaximalReduceCountHandlerAverageUse;
	int interleaveMaximalReduceCountHandlerPoolSize;
	int interleaveMaximalReduceCountHandlerFree;
	InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandler;
		
	
	
	
	int grammarMinimalReduceHandlerAverageUse;
	int grammarMinimalReduceHandlerPoolSize;
	int grammarMinimalReduceHandlerFree;
	GrammarMinimalReduceHandler[] grammarMinimalReduceHandler;
	
	int grammarMaximalReduceHandlerAverageUse;
	int grammarMaximalReduceHandlerPoolSize;
	int grammarMaximalReduceHandlerFree;
	GrammarMaximalReduceHandler[] grammarMaximalReduceHandler;
	
	int refMinimalReduceHandlerAverageUse;
	int refMinimalReduceHandlerPoolSize;
	int refMinimalReduceHandlerFree;
	RefMinimalReduceHandler[] refMinimalReduceHandler;
	
	int refMaximalReduceHandlerAverageUse;
	int refMaximalReduceHandlerPoolSize;
	int refMaximalReduceHandlerFree;
	RefMaximalReduceHandler[] refMaximalReduceHandler;
		
	int choiceMinimalReduceHandlerAverageUse;
	int choiceMinimalReduceHandlerPoolSize;
	int choiceMinimalReduceHandlerFree;
	ChoiceMinimalReduceHandler[] choiceMinimalReduceHandler;
	
	int choiceMaximalReduceHandlerAverageUse;
	int choiceMaximalReduceHandlerPoolSize;
	int choiceMaximalReduceHandlerFree;
	ChoiceMaximalReduceHandler[] choiceMaximalReduceHandler;
	
	int groupMinimalReduceHandlerAverageUse;
	int groupMinimalReduceHandlerPoolSize;
	int groupMinimalReduceHandlerFree;
	GroupMinimalReduceHandler[] groupMinimalReduceHandler;
	
	int groupMaximalReduceHandlerAverageUse;
	int groupMaximalReduceHandlerPoolSize;
	int groupMaximalReduceHandlerFree;
	GroupMaximalReduceHandler[] groupMaximalReduceHandler;
	
	int interleaveMinimalReduceHandlerAverageUse;
	int interleaveMinimalReduceHandlerPoolSize;
	int interleaveMinimalReduceHandlerFree;
	InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandler;
	
	int interleaveMaximalReduceHandlerAverageUse;
	int interleaveMaximalReduceHandlerPoolSize;
	int interleaveMaximalReduceHandlerFree;
	InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandler;
	
	final int UNUSED = 0;
	
	MessageWriter debugWriter;
	
	private RuleHandlerPool(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		vshPoolFree = 0;
		vshPoolPoolSize = 10;
		vshPools = new ActiveModelRuleHandlerPool[vshPoolPoolSize];
		
		particleHandlerAverageUse = UNUSED;
		particleHandlerPoolSize = 30;
		particleHandlerFree = 0;
		particleHandler = new ParticleHandler[particleHandlerPoolSize];
		
		choiceHandlerAverageUse = UNUSED;
		choiceHandlerPoolSize = 10;
		choiceHandlerFree = 0;
		choiceHandler = new ChoiceHandler[choiceHandlerPoolSize];
		
		groupHandlerAverageUse = UNUSED;
		groupHandlerPoolSize = 10;
		groupHandlerFree = 0;
		groupHandler = new GroupHandler[groupHandlerPoolSize];
		
		grammarHandlerAverageUse = UNUSED;
		grammarHandlerPoolSize = 10;
		grammarHandlerFree = 0;
		grammarHandler = new GrammarHandler[grammarHandlerPoolSize];
		
		refHandlerAverageUse = UNUSED;
		refHandlerPoolSize = 10;
		refHandlerFree = 0;
		refHandler = new RefHandler[refHandlerPoolSize];
		
		uinterleaveHandlerAverageUse = UNUSED;
		uinterleaveHandlerPoolSize = 10;
		uinterleaveHandlerFree = 0;
		uinterleaveHandler = new UInterleaveHandler[uinterleaveHandlerPoolSize];
		
		minterleaveHandlerAverageUse = UNUSED;
		minterleaveHandlerPoolSize = 10;
		minterleaveHandlerFree = 0;
		minterleaveHandler = new MInterleaveHandler[minterleaveHandlerPoolSize];
		
		sinterleaveHandlerAverageUse = UNUSED;
		sinterleaveHandlerPoolSize = 10;
		sinterleaveHandlerFree = 0;
		sinterleaveHandler = new SInterleaveHandler[sinterleaveHandlerPoolSize];
		
		elementHandlerAverageUse = UNUSED;
		elementHandlerPoolSize = 10;
		elementHandlerFree = 0;
		elementHandler = new ElementHandler[elementHandlerPoolSize];
		
		attributeHandlerAverageUse = UNUSED;
		attributeHandlerPoolSize = 10;
		attributeHandlerFree = 0;
		attributeHandler = new AttributeHandler[attributeHandlerPoolSize];
		
		exceptPatternHandlerAverageUse = UNUSED;
		exceptPatternHandlerPoolSize = 10;
		exceptPatternHandlerFree = 0;
		exceptPatternHandler = new ExceptPatternHandler[exceptPatternHandlerPoolSize];
		
		listPatternHandlerAverageUse = UNUSED;
		listPatternHandlerPoolSize = 10;
		listPatternHandlerFree = 0;
		listPatternHandler = new ListPatternHandler[listPatternHandlerPoolSize];
		
		
		
		
		
		groupDoubleHandlerAverageUse = UNUSED;
		groupDoubleHandlerPoolSize = 10;
		groupDoubleHandlerFree = 0;
		groupDoubleHandler = new GroupDoubleHandler[groupDoubleHandlerPoolSize];
				
		interleaveDoubleHandlerAverageUse = UNUSED;
		interleaveDoubleHandlerPoolSize = 10;
		interleaveDoubleHandlerFree = 0;
		interleaveDoubleHandler = new InterleaveDoubleHandler[interleaveDoubleHandlerPoolSize];
				
		
		
		groupMinimalReduceCountHandlerAverageUse = UNUSED;
		groupMinimalReduceCountHandlerPoolSize = 5;
		groupMinimalReduceCountHandlerFree = 0;
		groupMinimalReduceCountHandler = new GroupMinimalReduceCountHandler[groupMinimalReduceCountHandlerPoolSize];
		
		groupMaximalReduceCountHandlerAverageUse = UNUSED;
		groupMaximalReduceCountHandlerPoolSize = 5;
		groupMaximalReduceCountHandlerFree = 0;
		groupMaximalReduceCountHandler = new GroupMaximalReduceCountHandler[groupMaximalReduceCountHandlerPoolSize];
		
		interleaveMinimalReduceCountHandlerAverageUse = UNUSED;
		interleaveMinimalReduceCountHandlerPoolSize = 5;
		interleaveMinimalReduceCountHandlerFree = 0;
		interleaveMinimalReduceCountHandler = new InterleaveMinimalReduceCountHandler[interleaveMinimalReduceCountHandlerPoolSize];
				
		interleaveMaximalReduceCountHandlerAverageUse = UNUSED;
		interleaveMaximalReduceCountHandlerPoolSize = 5;
		interleaveMaximalReduceCountHandlerFree = 0;
		interleaveMaximalReduceCountHandler = new InterleaveMaximalReduceCountHandler[interleaveMaximalReduceCountHandlerPoolSize];
		
			
		
		
		grammarMinimalReduceHandlerAverageUse = UNUSED;
		grammarMinimalReduceHandlerPoolSize = 5;
		grammarMinimalReduceHandlerFree = 0;
		grammarMinimalReduceHandler = new GrammarMinimalReduceHandler[grammarMinimalReduceHandlerPoolSize];
		
		grammarMaximalReduceHandlerAverageUse = UNUSED;
		grammarMaximalReduceHandlerPoolSize = 5;
		grammarMaximalReduceHandlerFree = 0;
		grammarMaximalReduceHandler = new GrammarMaximalReduceHandler[grammarMaximalReduceHandlerPoolSize];
		
		refMinimalReduceHandlerAverageUse = UNUSED;
		refMinimalReduceHandlerPoolSize = 5;
		refMinimalReduceHandlerFree = 0;
		refMinimalReduceHandler = new RefMinimalReduceHandler[refMinimalReduceHandlerPoolSize];
		
		refMaximalReduceHandlerAverageUse = UNUSED;
		refMaximalReduceHandlerPoolSize = 5;
		refMaximalReduceHandlerFree = 0;
		refMaximalReduceHandler = new RefMaximalReduceHandler[refMaximalReduceHandlerPoolSize];
			
		choiceMinimalReduceHandlerAverageUse = UNUSED;
		choiceMinimalReduceHandlerPoolSize = 5;
		choiceMinimalReduceHandlerFree = 0;
		choiceMinimalReduceHandler = new ChoiceMinimalReduceHandler[choiceMinimalReduceHandlerPoolSize];
		
		choiceMaximalReduceHandlerAverageUse = UNUSED;
		choiceMaximalReduceHandlerPoolSize = 5;
		choiceMaximalReduceHandlerFree = 0;
		choiceMaximalReduceHandler = new ChoiceMaximalReduceHandler[choiceMaximalReduceHandlerPoolSize];
		
		groupMinimalReduceHandlerAverageUse = UNUSED;
		groupMinimalReduceHandlerPoolSize = 5;
		groupMinimalReduceHandlerFree = 0;
		groupMinimalReduceHandler = new GroupMinimalReduceHandler[groupMinimalReduceHandlerPoolSize];
		
		groupMaximalReduceHandlerAverageUse = UNUSED;
		groupMaximalReduceHandlerPoolSize = 5;
		groupMaximalReduceHandlerFree = 0;
		groupMaximalReduceHandler = new GroupMaximalReduceHandler[groupMaximalReduceHandlerPoolSize];
		
		interleaveMinimalReduceHandlerAverageUse = UNUSED;
		interleaveMinimalReduceHandlerPoolSize = 5;
		interleaveMinimalReduceHandlerFree = 0;
		interleaveMinimalReduceHandler = new InterleaveMinimalReduceHandler[interleaveMinimalReduceHandlerPoolSize];
		
		interleaveMaximalReduceHandlerAverageUse = UNUSED;
		interleaveMaximalReduceHandlerPoolSize = 5;
		interleaveMaximalReduceHandlerFree = 0;
		interleaveMaximalReduceHandler = new InterleaveMaximalReduceHandler[interleaveMaximalReduceHandlerPoolSize];
	}
	
	public static RuleHandlerPool getInstance(MessageWriter debugWriter){
		if(instance == null){
			synchronized(RuleHandlerPool.class){
				if(instance == null){
					instance = new RuleHandlerPool(debugWriter); 
				}
			}
		}
		return instance;
	}
	
	public synchronized ActiveModelRuleHandlerPool getActiveModelRuleHandlerPool(){
		if(vshPoolFree == 0){
			ActiveModelRuleHandlerPool vshp = new ActiveModelRuleHandlerPool(this, debugWriter);
			return vshp;
		}else{
			ActiveModelRuleHandlerPool vshp = vshPools[--vshPoolFree];
			return vshp;
		}
	}
		
	public synchronized void recycle(ActiveModelRuleHandlerPool vshp){
		if(vshPoolFree == vshPoolPoolSize){
			ActiveModelRuleHandlerPool[] increased = new ActiveModelRuleHandlerPool[++vshPoolPoolSize];
			System.arraycopy(vshPools, 0, increased, 0, vshPoolFree);
			vshPools = increased;
		}
		vshPools[vshPoolFree++] = vshp;
	}	
	
	synchronized void fill(ActiveModelRuleHandlerPool ruleHandlerPool,
				ParticleHandler[] particleHandler,
				ChoiceHandler[] choiceHandler,
				GroupHandler[] groupHandler,
				GrammarHandler[] grammarHandler,
				RefHandler[] refHandler,
				UInterleaveHandler[] uinterleaveHandler,
				MInterleaveHandler[] minterleaveHandler,
				SInterleaveHandler[] sinterleaveHandler,
				ElementHandler[] elementHandler,
				AttributeHandler[] attributeHandler,
				ExceptPatternHandler[] exceptPatternHandler,
				ListPatternHandler[] listPatternHandler,
				GroupDoubleHandler[] groupDoubleHandler,
				InterleaveDoubleHandler[] interleaveDoubleHandler,
				GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandler,
				GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandler,
				InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandler,
				InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandler,				
				GrammarMinimalReduceHandler[] grammarMinimalReduceHandler,
				GrammarMaximalReduceHandler[] grammarMaximalReduceHandler,
				RefMinimalReduceHandler[] refMinimalReduceHandler,
				RefMaximalReduceHandler[] refMaximalReduceHandler,
				ChoiceMinimalReduceHandler[] choiceMinimalReduceHandler,
				ChoiceMaximalReduceHandler[] choiceMaximalReduceHandler,
				GroupMinimalReduceHandler[] groupMinimalReduceHandler,
				GroupMaximalReduceHandler[] groupMaximalReduceHandler,
				InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandler,
				InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandler){
		int particleHandlerFillCount;
		if(particleHandler == null || particleHandler.length < particleHandlerAverageUse)
			particleHandler = new ParticleHandler[particleHandlerAverageUse];
		if(particleHandlerFree > particleHandlerAverageUse){
			particleHandlerFillCount = particleHandlerAverageUse;
			particleHandlerFree = particleHandlerFree - particleHandlerAverageUse;
		}else{
			particleHandlerFillCount = particleHandlerFree;
			particleHandlerFree = 0;
		}		
		System.arraycopy(this.particleHandler, particleHandlerFree, 
							particleHandler, 0, particleHandlerFillCount);
		
		int choiceHandlerFillCount;
		if(choiceHandler == null || choiceHandler.length < choiceHandlerAverageUse)
			choiceHandler = new ChoiceHandler[choiceHandlerAverageUse];
		if(choiceHandlerFree > choiceHandlerAverageUse){
			choiceHandlerFillCount = choiceHandlerAverageUse;
			choiceHandlerFree = choiceHandlerFree - choiceHandlerAverageUse;
		}else{
			choiceHandlerFillCount = choiceHandlerFree;
			choiceHandlerFree = 0;
		}		
		System.arraycopy(this.choiceHandler, choiceHandlerFree, 
							choiceHandler, 0, choiceHandlerFillCount);
		
		int groupHandlerFillCount;
		if(groupHandler == null || groupHandler.length < groupHandlerAverageUse)
			groupHandler = new GroupHandler[groupHandlerAverageUse];
		if(groupHandlerFree > groupHandlerAverageUse){
			groupHandlerFillCount = groupHandlerAverageUse;
			groupHandlerFree = groupHandlerFree - groupHandlerAverageUse;
		}else{
			groupHandlerFillCount = groupHandlerFree;
			groupHandlerFree = 0;
		}		
		System.arraycopy(this.groupHandler, groupHandlerFree, 
							groupHandler, 0, groupHandlerFillCount);
		
		int grammarHandlerFillCount;
		if(grammarHandler == null || grammarHandler.length < grammarHandlerAverageUse)
			grammarHandler = new GrammarHandler[grammarHandlerAverageUse];
		if(grammarHandlerFree > grammarHandlerAverageUse){
			grammarHandlerFillCount = grammarHandlerAverageUse;
			grammarHandlerFree = grammarHandlerFree - grammarHandlerAverageUse;
		}else{
			grammarHandlerFillCount = grammarHandlerFree;
			grammarHandlerFree = 0;
		}		
		System.arraycopy(this.grammarHandler, grammarHandlerFree, 
							grammarHandler, 0, grammarHandlerFillCount);
		
		int refHandlerFillCount;
		if(refHandler == null || refHandler.length < refHandlerAverageUse)
			refHandler = new RefHandler[refHandlerAverageUse];
		if(refHandlerFree > refHandlerAverageUse){
			refHandlerFillCount = refHandlerAverageUse;
			refHandlerFree = refHandlerFree - refHandlerAverageUse;
		}else{
			refHandlerFillCount = refHandlerFree;
			refHandlerFree = 0;
		}		
		System.arraycopy(this.refHandler, refHandlerFree, 
							refHandler, 0, refHandlerFillCount);
		
		
		int uinterleaveHandlerFillCount;
		if(uinterleaveHandler == null || uinterleaveHandler.length < uinterleaveHandlerAverageUse)
			uinterleaveHandler = new UInterleaveHandler[uinterleaveHandlerAverageUse];
		if(uinterleaveHandlerFree > uinterleaveHandlerAverageUse){
			uinterleaveHandlerFillCount = uinterleaveHandlerAverageUse;
			uinterleaveHandlerFree = uinterleaveHandlerFree - uinterleaveHandlerAverageUse;
		}else{
			uinterleaveHandlerFillCount = uinterleaveHandlerFree;
			uinterleaveHandlerFree = 0;
		}		
		System.arraycopy(this.uinterleaveHandler, uinterleaveHandlerFree, 
							uinterleaveHandler, 0, uinterleaveHandlerFillCount);
		
		int minterleaveHandlerFillCount;
		if(minterleaveHandler == null || minterleaveHandler.length < minterleaveHandlerAverageUse)
			minterleaveHandler = new MInterleaveHandler[minterleaveHandlerAverageUse];
		if(minterleaveHandlerFree > minterleaveHandlerAverageUse){
			minterleaveHandlerFillCount = minterleaveHandlerAverageUse;
			minterleaveHandlerFree = minterleaveHandlerFree - minterleaveHandlerAverageUse;
		}else{
			minterleaveHandlerFillCount = minterleaveHandlerFree;
			minterleaveHandlerFree = 0;
		}		
		System.arraycopy(this.minterleaveHandler, minterleaveHandlerFree, 
							minterleaveHandler, 0, minterleaveHandlerFillCount);
		
		int sinterleaveHandlerFillCount;
		if(sinterleaveHandler == null || sinterleaveHandler.length < sinterleaveHandlerAverageUse)
			sinterleaveHandler = new SInterleaveHandler[sinterleaveHandlerAverageUse];
		if(sinterleaveHandlerFree > sinterleaveHandlerAverageUse){
			sinterleaveHandlerFillCount = sinterleaveHandlerAverageUse;
			sinterleaveHandlerFree = sinterleaveHandlerFree - sinterleaveHandlerAverageUse;
		}else{
			sinterleaveHandlerFillCount = sinterleaveHandlerFree;
			sinterleaveHandlerFree = 0;
		}		
		System.arraycopy(this.sinterleaveHandler, sinterleaveHandlerFree, 
							sinterleaveHandler, 0, sinterleaveHandlerFillCount);
		
		int elementHandlerFillCount;
		if(elementHandler == null || elementHandler.length < elementHandlerAverageUse)
			elementHandler = new ElementHandler[elementHandlerAverageUse];
		if(elementHandlerFree > elementHandlerAverageUse){
			elementHandlerFillCount = elementHandlerAverageUse;
			elementHandlerFree = elementHandlerFree - elementHandlerAverageUse;
		}else{
			elementHandlerFillCount = elementHandlerFree;
			elementHandlerFree = 0;
		}		
		System.arraycopy(this.elementHandler, elementHandlerFree, 
							elementHandler, 0, elementHandlerFillCount);
		
		int attributeHandlerFillCount;
		if(attributeHandler == null || attributeHandler.length < attributeHandlerAverageUse)
			attributeHandler = new AttributeHandler[attributeHandlerAverageUse];
		if(attributeHandlerFree > attributeHandlerAverageUse){
			attributeHandlerFillCount = attributeHandlerAverageUse;
			attributeHandlerFree = attributeHandlerFree - attributeHandlerAverageUse;
		}else{
			attributeHandlerFillCount = attributeHandlerFree;
			attributeHandlerFree = 0;
		}		
		System.arraycopy(this.attributeHandler, attributeHandlerFree, 
							attributeHandler, 0, attributeHandlerFillCount);
		
		int exceptPatternHandlerFillCount;
		if(exceptPatternHandler == null || exceptPatternHandler.length < exceptPatternHandlerAverageUse)
			exceptPatternHandler = new ExceptPatternHandler[exceptPatternHandlerAverageUse];
		if(exceptPatternHandlerFree > exceptPatternHandlerAverageUse){
			exceptPatternHandlerFillCount = exceptPatternHandlerAverageUse;
			exceptPatternHandlerFree = exceptPatternHandlerFree - exceptPatternHandlerAverageUse;
		}else{
			exceptPatternHandlerFillCount = exceptPatternHandlerFree;
			exceptPatternHandlerFree = 0;
		}		
		System.arraycopy(this.exceptPatternHandler, exceptPatternHandlerFree, 
							exceptPatternHandler, 0, exceptPatternHandlerFillCount);
		
		int listPatternHandlerFillCount;
		if(listPatternHandler == null || listPatternHandler.length < listPatternHandlerAverageUse)
			listPatternHandler = new ListPatternHandler[listPatternHandlerAverageUse];
		if(listPatternHandlerFree > listPatternHandlerAverageUse){
			listPatternHandlerFillCount = listPatternHandlerAverageUse;
			listPatternHandlerFree = listPatternHandlerFree - listPatternHandlerAverageUse;
		}else{
			listPatternHandlerFillCount = listPatternHandlerFree;
			listPatternHandlerFree = 0;
		}		
		System.arraycopy(this.listPatternHandler, listPatternHandlerFree, 
							listPatternHandler, 0, listPatternHandlerFillCount);
		
		
		
		
		
		int groupDoubleHandlerFillCount;
		if(groupDoubleHandler == null || groupDoubleHandler.length < groupDoubleHandlerAverageUse)
			groupDoubleHandler = new GroupDoubleHandler[groupDoubleHandlerAverageUse];
		if(groupDoubleHandlerFree > groupDoubleHandlerAverageUse){
			groupDoubleHandlerFillCount = groupDoubleHandlerAverageUse;
			groupDoubleHandlerFree = groupDoubleHandlerFree - groupDoubleHandlerAverageUse;
		}else{
			groupDoubleHandlerFillCount = groupDoubleHandlerFree;
			groupDoubleHandlerFree = 0;
		}		
		System.arraycopy(this.groupDoubleHandler, groupDoubleHandlerFree, 
							groupDoubleHandler, 0, groupDoubleHandlerFillCount);
		
		int interleaveDoubleHandlerFillCount;
		if(interleaveDoubleHandler == null || interleaveDoubleHandler.length < interleaveDoubleHandlerAverageUse)
			interleaveDoubleHandler = new InterleaveDoubleHandler[interleaveDoubleHandlerAverageUse];
		if(interleaveDoubleHandlerFree > interleaveDoubleHandlerAverageUse){
			interleaveDoubleHandlerFillCount = interleaveDoubleHandlerAverageUse;
			interleaveDoubleHandlerFree = interleaveDoubleHandlerFree - interleaveDoubleHandlerAverageUse;
		}else{
			interleaveDoubleHandlerFillCount = interleaveDoubleHandlerFree;
			interleaveDoubleHandlerFree = 0;
		}		
		System.arraycopy(this.interleaveDoubleHandler, interleaveDoubleHandlerFree, 
							interleaveDoubleHandler, 0, interleaveDoubleHandlerFillCount);
		
		
		
		
		int groupMinimalReduceCountHandlerFillCount;
		if(groupMinimalReduceCountHandler == null || groupMinimalReduceCountHandler.length < groupMinimalReduceCountHandlerAverageUse)
			groupMinimalReduceCountHandler = new GroupMinimalReduceCountHandler[groupMinimalReduceCountHandlerAverageUse];
		if(groupMinimalReduceCountHandlerFree > groupMinimalReduceCountHandlerAverageUse){
			groupMinimalReduceCountHandlerFillCount = groupMinimalReduceCountHandlerAverageUse;
			groupMinimalReduceCountHandlerFree = groupMinimalReduceCountHandlerFree - groupMinimalReduceCountHandlerAverageUse;
		}else{
			groupMinimalReduceCountHandlerFillCount = groupMinimalReduceCountHandlerFree;
			groupMinimalReduceCountHandlerFree = 0;
		}		
		System.arraycopy(this.groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, 
							groupMinimalReduceCountHandler, 0, groupMinimalReduceCountHandlerFillCount);
		
		int groupMaximalReduceCountHandlerFillCount;
		if(groupMaximalReduceCountHandler == null || groupMaximalReduceCountHandler.length < groupMaximalReduceCountHandlerAverageUse)
			groupMaximalReduceCountHandler = new GroupMaximalReduceCountHandler[groupMaximalReduceCountHandlerAverageUse];
		if(groupMaximalReduceCountHandlerFree > groupMaximalReduceCountHandlerAverageUse){
			groupMaximalReduceCountHandlerFillCount = groupMaximalReduceCountHandlerAverageUse;
			groupMaximalReduceCountHandlerFree = groupMaximalReduceCountHandlerFree - groupMaximalReduceCountHandlerAverageUse;
		}else{
			groupMaximalReduceCountHandlerFillCount = groupMaximalReduceCountHandlerFree;
			groupMaximalReduceCountHandlerFree = 0;
		}		
		System.arraycopy(this.groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, 
							groupMaximalReduceCountHandler, 0, groupMaximalReduceCountHandlerFillCount);
		
		int interleaveMinimalReduceCountHandlerFillCount;
		if(interleaveMinimalReduceCountHandler == null || interleaveMinimalReduceCountHandler.length < interleaveMinimalReduceCountHandlerAverageUse)
			interleaveMinimalReduceCountHandler = new InterleaveMinimalReduceCountHandler[interleaveMinimalReduceCountHandlerAverageUse];
		if(interleaveMinimalReduceCountHandlerFree > interleaveMinimalReduceCountHandlerAverageUse){
			interleaveMinimalReduceCountHandlerFillCount = interleaveMinimalReduceCountHandlerAverageUse;
			interleaveMinimalReduceCountHandlerFree = interleaveMinimalReduceCountHandlerFree - interleaveMinimalReduceCountHandlerAverageUse;
		}else{
			interleaveMinimalReduceCountHandlerFillCount = interleaveMinimalReduceCountHandlerFree;
			interleaveMinimalReduceCountHandlerFree = 0;
		}		
		System.arraycopy(this.interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, 
							interleaveMinimalReduceCountHandler, 0, interleaveMinimalReduceCountHandlerFillCount);
		
		int interleaveMaximalReduceCountHandlerFillCount;
		if(interleaveMaximalReduceCountHandler == null || interleaveMaximalReduceCountHandler.length < interleaveMaximalReduceCountHandlerAverageUse)
			interleaveMaximalReduceCountHandler = new InterleaveMaximalReduceCountHandler[interleaveMaximalReduceCountHandlerAverageUse];
		if(interleaveMaximalReduceCountHandlerFree > interleaveMaximalReduceCountHandlerAverageUse){
			interleaveMaximalReduceCountHandlerFillCount = interleaveMaximalReduceCountHandlerAverageUse;
			interleaveMaximalReduceCountHandlerFree = interleaveMaximalReduceCountHandlerFree - interleaveMaximalReduceCountHandlerAverageUse;
		}else{
			interleaveMaximalReduceCountHandlerFillCount = interleaveMaximalReduceCountHandlerFree;
			interleaveMaximalReduceCountHandlerFree = 0;
		}		
		System.arraycopy(this.interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, 
							interleaveMaximalReduceCountHandler, 0, interleaveMaximalReduceCountHandlerFillCount);
				
		
		
				
		
		int grammarMinimalReduceHandlerFillCount;
		if(grammarMinimalReduceHandler == null || grammarMinimalReduceHandler.length < grammarMinimalReduceHandlerAverageUse)
			grammarMinimalReduceHandler = new GrammarMinimalReduceHandler[grammarMinimalReduceHandlerAverageUse];
		if(grammarMinimalReduceHandlerFree > grammarMinimalReduceHandlerAverageUse){
			grammarMinimalReduceHandlerFillCount = grammarMinimalReduceHandlerAverageUse;
			grammarMinimalReduceHandlerFree = grammarMinimalReduceHandlerFree - grammarMinimalReduceHandlerAverageUse;
		}else{
			grammarMinimalReduceHandlerFillCount = grammarMinimalReduceHandlerFree;
			grammarMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.grammarMinimalReduceHandler, grammarMinimalReduceHandlerFree, 
							grammarMinimalReduceHandler, 0, grammarMinimalReduceHandlerFillCount);
		
		int grammarMaximalReduceHandlerFillCount;
		if(grammarMaximalReduceHandler == null || grammarMaximalReduceHandler.length < grammarMaximalReduceHandlerAverageUse)
			grammarMaximalReduceHandler = new GrammarMaximalReduceHandler[grammarMaximalReduceHandlerAverageUse];
		if(grammarMaximalReduceHandlerFree > grammarMaximalReduceHandlerAverageUse){
			grammarMaximalReduceHandlerFillCount = grammarMaximalReduceHandlerAverageUse;
			grammarMaximalReduceHandlerFree = grammarMaximalReduceHandlerFree - grammarMaximalReduceHandlerAverageUse;
		}else{
			grammarMaximalReduceHandlerFillCount = grammarMaximalReduceHandlerFree;
			grammarMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.grammarMaximalReduceHandler, grammarMaximalReduceHandlerFree, 
							grammarMaximalReduceHandler, 0, grammarMaximalReduceHandlerFillCount);
		
		int refMinimalReduceHandlerFillCount;
		if(refMinimalReduceHandler == null || refMinimalReduceHandler.length < refMinimalReduceHandlerAverageUse)
			refMinimalReduceHandler = new RefMinimalReduceHandler[refMinimalReduceHandlerAverageUse];
		if(refMinimalReduceHandlerFree > refMinimalReduceHandlerAverageUse){
			refMinimalReduceHandlerFillCount = refMinimalReduceHandlerAverageUse;
			refMinimalReduceHandlerFree = refMinimalReduceHandlerFree - refMinimalReduceHandlerAverageUse;
		}else{
			refMinimalReduceHandlerFillCount = refMinimalReduceHandlerFree;
			refMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.refMinimalReduceHandler, refMinimalReduceHandlerFree, 
							refMinimalReduceHandler, 0, refMinimalReduceHandlerFillCount);
		
		int refMaximalReduceHandlerFillCount;
		if(refMaximalReduceHandler == null || refMaximalReduceHandler.length < refMaximalReduceHandlerAverageUse)
			refMaximalReduceHandler = new RefMaximalReduceHandler[refMaximalReduceHandlerAverageUse];
		if(refMaximalReduceHandlerFree > refMaximalReduceHandlerAverageUse){
			refMaximalReduceHandlerFillCount = refMaximalReduceHandlerAverageUse;
			refMaximalReduceHandlerFree = refMaximalReduceHandlerFree - refMaximalReduceHandlerAverageUse;
		}else{
			refMaximalReduceHandlerFillCount = refMaximalReduceHandlerFree;
			refMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.refMaximalReduceHandler, refMaximalReduceHandlerFree, 
							refMaximalReduceHandler, 0, refMaximalReduceHandlerFillCount);
		
		
		int choiceMinimalReduceHandlerFillCount;
		if(choiceMinimalReduceHandler == null || choiceMinimalReduceHandler.length < choiceMinimalReduceHandlerAverageUse)
			choiceMinimalReduceHandler = new ChoiceMinimalReduceHandler[choiceMinimalReduceHandlerAverageUse];
		if(choiceMinimalReduceHandlerFree > choiceMinimalReduceHandlerAverageUse){
			choiceMinimalReduceHandlerFillCount = choiceMinimalReduceHandlerAverageUse;
			choiceMinimalReduceHandlerFree = choiceMinimalReduceHandlerFree - choiceMinimalReduceHandlerAverageUse;
		}else{
			choiceMinimalReduceHandlerFillCount = choiceMinimalReduceHandlerFree;
			choiceMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, 
							choiceMinimalReduceHandler, 0, choiceMinimalReduceHandlerFillCount);
		
		int choiceMaximalReduceHandlerFillCount;
		if(choiceMaximalReduceHandler == null || choiceMaximalReduceHandler.length < choiceMaximalReduceHandlerAverageUse)
			choiceMaximalReduceHandler = new ChoiceMaximalReduceHandler[choiceMaximalReduceHandlerAverageUse];
		if(choiceMaximalReduceHandlerFree > choiceMaximalReduceHandlerAverageUse){
			choiceMaximalReduceHandlerFillCount = choiceMaximalReduceHandlerAverageUse;
			choiceMaximalReduceHandlerFree = choiceMaximalReduceHandlerFree - choiceMaximalReduceHandlerAverageUse;
		}else{
			choiceMaximalReduceHandlerFillCount = choiceMaximalReduceHandlerFree;
			choiceMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, 
							choiceMaximalReduceHandler, 0, choiceMaximalReduceHandlerFillCount);
		
		
		int groupMinimalReduceHandlerFillCount;
		if(groupMinimalReduceHandler == null || groupMinimalReduceHandler.length < groupMinimalReduceHandlerAverageUse)
			groupMinimalReduceHandler = new GroupMinimalReduceHandler[groupMinimalReduceHandlerAverageUse];
		if(groupMinimalReduceHandlerFree > groupMinimalReduceHandlerAverageUse){
			groupMinimalReduceHandlerFillCount = groupMinimalReduceHandlerAverageUse;
			groupMinimalReduceHandlerFree = groupMinimalReduceHandlerFree - groupMinimalReduceHandlerAverageUse;
		}else{
			groupMinimalReduceHandlerFillCount = groupMinimalReduceHandlerFree;
			groupMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.groupMinimalReduceHandler, groupMinimalReduceHandlerFree, 
							groupMinimalReduceHandler, 0, groupMinimalReduceHandlerFillCount);
		
		int groupMaximalReduceHandlerFillCount;
		if(groupMaximalReduceHandler == null || groupMaximalReduceHandler.length < groupMaximalReduceHandlerAverageUse)
			groupMaximalReduceHandler = new GroupMaximalReduceHandler[groupMaximalReduceHandlerAverageUse];
		if(groupMaximalReduceHandlerFree > groupMaximalReduceHandlerAverageUse){
			groupMaximalReduceHandlerFillCount = groupMaximalReduceHandlerAverageUse;
			groupMaximalReduceHandlerFree = groupMaximalReduceHandlerFree - groupMaximalReduceHandlerAverageUse;
		}else{
			groupMaximalReduceHandlerFillCount = groupMaximalReduceHandlerFree;
			groupMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.groupMaximalReduceHandler, groupMaximalReduceHandlerFree, 
							groupMaximalReduceHandler, 0, groupMaximalReduceHandlerFillCount);
		
		int interleaveMinimalReduceHandlerFillCount;
		if(interleaveMinimalReduceHandler == null || interleaveMinimalReduceHandler.length < interleaveMinimalReduceHandlerAverageUse)
			interleaveMinimalReduceHandler = new InterleaveMinimalReduceHandler[interleaveMinimalReduceHandlerAverageUse];
		if(interleaveMinimalReduceHandlerFree > interleaveMinimalReduceHandlerAverageUse){
			interleaveMinimalReduceHandlerFillCount = interleaveMinimalReduceHandlerAverageUse;
			interleaveMinimalReduceHandlerFree = interleaveMinimalReduceHandlerFree - interleaveMinimalReduceHandlerAverageUse;
		}else{
			interleaveMinimalReduceHandlerFillCount = interleaveMinimalReduceHandlerFree;
			interleaveMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, 
							interleaveMinimalReduceHandler, 0, interleaveMinimalReduceHandlerFillCount);
		
		int interleaveMaximalReduceHandlerFillCount;
		if(interleaveMaximalReduceHandler == null || interleaveMaximalReduceHandler.length < interleaveMaximalReduceHandlerAverageUse)
			interleaveMaximalReduceHandler = new InterleaveMaximalReduceHandler[interleaveMaximalReduceHandlerAverageUse];
		if(interleaveMaximalReduceHandlerFree > interleaveMaximalReduceHandlerAverageUse){
			interleaveMaximalReduceHandlerFillCount = interleaveMaximalReduceHandlerAverageUse;
			interleaveMaximalReduceHandlerFree = interleaveMaximalReduceHandlerFree - interleaveMaximalReduceHandlerAverageUse;
		}else{
			interleaveMaximalReduceHandlerFillCount = interleaveMaximalReduceHandlerFree;
			interleaveMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(this.interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, 
							interleaveMaximalReduceHandler, 0, interleaveMaximalReduceHandlerFillCount);
		
		
		
		ruleHandlerPool.setHandlers(particleHandlerFillCount,
										particleHandler,
										choiceHandlerFillCount,
										choiceHandler,
										groupHandlerFillCount,
										groupHandler,
										grammarHandlerFillCount,
										grammarHandler,
										refHandlerFillCount,
										refHandler,
										uinterleaveHandlerFillCount,
										uinterleaveHandler,
										minterleaveHandlerFillCount,
										minterleaveHandler,
										sinterleaveHandlerFillCount,
										sinterleaveHandler,
										elementHandlerFillCount,
										elementHandler,
										attributeHandlerFillCount,
										attributeHandler,
										exceptPatternHandlerFillCount,
										exceptPatternHandler,
										listPatternHandlerFillCount,
										listPatternHandler,
										groupDoubleHandlerFillCount,
										groupDoubleHandler,
										interleaveDoubleHandlerFillCount,
										interleaveDoubleHandler,
										groupMinimalReduceCountHandlerFillCount,
										groupMinimalReduceCountHandler,
										groupMaximalReduceCountHandlerFillCount,
										groupMaximalReduceCountHandler,
										interleaveMinimalReduceCountHandlerFillCount,
										interleaveMinimalReduceCountHandler,
										interleaveMaximalReduceCountHandlerFillCount,
										interleaveMaximalReduceCountHandler,
										grammarMinimalReduceHandlerFillCount,
										grammarMinimalReduceHandler,
										grammarMaximalReduceHandlerFillCount,
										grammarMaximalReduceHandler,
										refMinimalReduceHandlerFillCount,
										refMinimalReduceHandler,
										refMaximalReduceHandlerFillCount,
										refMaximalReduceHandler,
										choiceMinimalReduceHandlerFillCount,
										choiceMinimalReduceHandler,
										choiceMaximalReduceHandlerFillCount,
										choiceMaximalReduceHandler,
										groupMinimalReduceHandlerFillCount,
										groupMinimalReduceHandler,
										groupMaximalReduceHandlerFillCount,
										groupMaximalReduceHandler,
										interleaveMinimalReduceHandlerFillCount,
										interleaveMinimalReduceHandler,
										interleaveMaximalReduceHandlerFillCount,
										interleaveMaximalReduceHandler);
	}
	
	synchronized void recycle(int particleHandlerAverageUse,
							ParticleHandler[] particleHandler,
							int choiceHandlerAverageUse,
							ChoiceHandler[] choiceHandler,
							int groupHandlerAverageUse,
							GroupHandler[] groupHandler,
							int grammarHandlerAverageUse,
							GrammarHandler[] grammarHandler,
							int refHandlerAverageUse,
							RefHandler[] refHandler,
							int uinterleaveHandlerAverageUse,
							UInterleaveHandler[] uinterleaveHandler,
							int minterleaveHandlerAverageUse,
							MInterleaveHandler[] minterleaveHandler,
							int sinterleaveHandlerAverageUse,
							SInterleaveHandler[] sinterleaveHandler,
							int elementHandlerAverageUse,
							ElementHandler[] elementHandler,
							int attributeHandlerAverageUse,
							AttributeHandler[] attributeHandler,
							int exceptPatternHandlerAverageUse,
							ExceptPatternHandler[] exceptPatternHandler,
							int listPatternHandlerAverageUse,
							ListPatternHandler[] listPatternHandler,
							int groupDoubleHandlerAverageUse,
							GroupDoubleHandler[] groupDoubleHandler,
							int interleaveDoubleHandlerAverageUse,
							InterleaveDoubleHandler[] interleaveDoubleHandler,
							int groupMinimalReduceCountHandlerAverageUse,
							GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandler,
							int groupMaximalReduceCountHandlerAverageUse,
							GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandler,
							int interleaveMinimalReduceCountHandlerAverageUse,
							InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandler,
							int interleaveMaximalReduceCountHandlerAverageUse,
							InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandler,
							int grammarMinimalReduceHandlerAverageUse,
							GrammarMinimalReduceHandler[] grammarMinimalReduceHandler,
							int grammarMaximalReduceHandlerAverageUse,
							GrammarMaximalReduceHandler[] grammarMaximalReduceHandler,
							int refMinimalReduceHandlerAverageUse,
							RefMinimalReduceHandler[] refMinimalReduceHandler,
							int refMaximalReduceHandlerAverageUse,
							RefMaximalReduceHandler[] refMaximalReduceHandler,
							int choiceMinimalReduceHandlerAverageUse,
							ChoiceMinimalReduceHandler[] choiceMinimalReduceHandler,
							int choiceMaximalReduceHandlerAverageUse,
							ChoiceMaximalReduceHandler[] choiceMaximalReduceHandler,
							int groupMinimalReduceHandlerAverageUse,
							GroupMinimalReduceHandler[] groupMinimalReduceHandler,
							int groupMaximalReduceHandlerAverageUse,
							GroupMaximalReduceHandler[] groupMaximalReduceHandler,
							int interleaveMinimalReduceHandlerAverageUse,
							InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandler,
							int interleaveMaximalReduceHandlerAverageUse,
							InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandler){
		if(particleHandlerFree + particleHandlerAverageUse >= particleHandlerPoolSize){			 
			particleHandlerPoolSize+= particleHandlerAverageUse;
			ParticleHandler[] increased = new ParticleHandler[particleHandlerPoolSize];
			System.arraycopy(this.particleHandler, 0, increased, 0, particleHandlerFree);
			this.particleHandler = increased;
		}
		System.arraycopy(particleHandler, 0, this.particleHandler, particleHandlerFree, particleHandlerAverageUse);
		particleHandlerFree += particleHandlerAverageUse;
		if(this.particleHandlerAverageUse != 0)this.particleHandlerAverageUse = (this.particleHandlerAverageUse + particleHandlerAverageUse)/2;
		else this.particleHandlerAverageUse = particleHandlerAverageUse;
		// System.out.println(" particleHandler "+this.particleHandlerAverageUse);		
		
		if(choiceHandlerFree + choiceHandlerAverageUse >= choiceHandlerPoolSize){			 
			choiceHandlerPoolSize+= choiceHandlerAverageUse;
			ChoiceHandler[] increased = new ChoiceHandler[choiceHandlerPoolSize];
			System.arraycopy(this.choiceHandler, 0, increased, 0, choiceHandlerFree);
			this.choiceHandler = increased;
		}
		System.arraycopy(choiceHandler, 0, this.choiceHandler, choiceHandlerFree, choiceHandlerAverageUse);
		choiceHandlerFree += choiceHandlerAverageUse;
		if(this.choiceHandlerAverageUse != 0) this.choiceHandlerAverageUse = (this.choiceHandlerAverageUse + choiceHandlerAverageUse)/2;
		else this.choiceHandlerAverageUse = choiceHandlerAverageUse;
		// System.out.println(" choiceHandler "+this.choiceHandlerAverageUse);
				
		if(groupHandlerFree + groupHandlerAverageUse >= groupHandlerPoolSize){			 
			groupHandlerPoolSize+= groupHandlerAverageUse;
			GroupHandler[] increased = new GroupHandler[groupHandlerPoolSize];
			System.arraycopy(this.groupHandler, 0, increased, 0, groupHandlerFree);
			this.groupHandler = increased;
		}
		System.arraycopy(groupHandler, 0, this.groupHandler, groupHandlerFree, groupHandlerAverageUse);
		groupHandlerFree += groupHandlerAverageUse;
		if(this.groupHandlerAverageUse != 0)this.groupHandlerAverageUse = (this.groupHandlerAverageUse + groupHandlerAverageUse)/2;
		else this.groupHandlerAverageUse = groupHandlerAverageUse;
		// System.out.println(" groupHandler "+this.groupHandlerAverageUse);
		
		if(grammarHandlerFree + grammarHandlerAverageUse >= grammarHandlerPoolSize){			 
			grammarHandlerPoolSize+= grammarHandlerAverageUse;
			GrammarHandler[] increased = new GrammarHandler[grammarHandlerPoolSize];
			System.arraycopy(this.grammarHandler, 0, increased, 0, grammarHandlerFree);
			this.grammarHandler = increased;
		}
		System.arraycopy(grammarHandler, 0, this.grammarHandler, grammarHandlerFree, grammarHandlerAverageUse);
		grammarHandlerFree += grammarHandlerAverageUse;
		if(this.grammarHandlerAverageUse != 0)this.grammarHandlerAverageUse = (this.grammarHandlerAverageUse + grammarHandlerAverageUse)/2;
		else this.grammarHandlerAverageUse = grammarHandlerAverageUse;
		// System.out.println(" grammarHandler "+this.grammarHandlerAverageUse);
		
		if(uinterleaveHandlerFree + uinterleaveHandlerAverageUse >= uinterleaveHandlerPoolSize){			 
			uinterleaveHandlerPoolSize+= uinterleaveHandlerAverageUse;
			UInterleaveHandler[] increased = new UInterleaveHandler[uinterleaveHandlerPoolSize];
			System.arraycopy(this.uinterleaveHandler, 0, increased, 0, uinterleaveHandlerFree);
			this.uinterleaveHandler = increased;
		}
		System.arraycopy(uinterleaveHandler, 0, this.uinterleaveHandler, uinterleaveHandlerFree, uinterleaveHandlerAverageUse);
		uinterleaveHandlerFree += uinterleaveHandlerAverageUse;
		if(this.uinterleaveHandlerAverageUse != 0) this.uinterleaveHandlerAverageUse = (this.uinterleaveHandlerAverageUse + uinterleaveHandlerAverageUse)/2;
		else this.uinterleaveHandlerAverageUse = uinterleaveHandlerAverageUse;
		// System.out.println(" uinterleaveHandler "+this.uinterleaveHandlerAverageUse);
		
		if(minterleaveHandlerFree + minterleaveHandlerAverageUse >= minterleaveHandlerPoolSize){			 
			minterleaveHandlerPoolSize+= minterleaveHandlerAverageUse;
			MInterleaveHandler[] increased = new MInterleaveHandler[minterleaveHandlerPoolSize];
			System.arraycopy(this.minterleaveHandler, 0, increased, 0, minterleaveHandlerFree);
			this.minterleaveHandler = increased;
		}
		System.arraycopy(minterleaveHandler, 0, this.minterleaveHandler, minterleaveHandlerFree, minterleaveHandlerAverageUse);
		minterleaveHandlerFree += minterleaveHandlerAverageUse;
		if(this.minterleaveHandlerAverageUse != 0) this.minterleaveHandlerAverageUse = (this.minterleaveHandlerAverageUse + minterleaveHandlerAverageUse)/2;
		else this.minterleaveHandlerAverageUse = minterleaveHandlerAverageUse;
		// System.out.println(" minterleaveHandler "+this.minterleaveHandlerAverageUse);
		
		if(sinterleaveHandlerFree + sinterleaveHandlerAverageUse >= sinterleaveHandlerPoolSize){			 
			sinterleaveHandlerPoolSize+= sinterleaveHandlerAverageUse;
			SInterleaveHandler[] increased = new SInterleaveHandler[sinterleaveHandlerPoolSize];
			System.arraycopy(this.sinterleaveHandler, 0, increased, 0, sinterleaveHandlerFree);
			this.sinterleaveHandler = increased;
		}
		System.arraycopy(sinterleaveHandler, 0, this.sinterleaveHandler, sinterleaveHandlerFree, sinterleaveHandlerAverageUse);
		sinterleaveHandlerFree += sinterleaveHandlerAverageUse;
		if(this.sinterleaveHandlerAverageUse != 0) this.sinterleaveHandlerAverageUse = (this.sinterleaveHandlerAverageUse + sinterleaveHandlerAverageUse)/2;
		else this.sinterleaveHandlerAverageUse = sinterleaveHandlerAverageUse;
		// System.out.println(" sinterleaveHandler "+this.sinterleaveHandlerAverageUse);
		
		if(refHandlerFree + refHandlerAverageUse >= refHandlerPoolSize){			 
			refHandlerPoolSize+= refHandlerAverageUse;
			RefHandler[] increased = new RefHandler[refHandlerPoolSize];
			System.arraycopy(this.refHandler, 0, increased, 0, refHandlerFree);
			this.refHandler = increased;
		}
		System.arraycopy(refHandler, 0, this.refHandler, refHandlerFree, refHandlerAverageUse);
		refHandlerFree += refHandlerAverageUse;
		if(this.refHandlerAverageUse != 0)this.refHandlerAverageUse = (this.refHandlerAverageUse + refHandlerAverageUse)/2;
		else this.refHandlerAverageUse = refHandlerAverageUse;
		// System.out.println(" refHandler "+this.refHandlerAverageUse);
		
		if(elementHandlerFree + elementHandlerAverageUse >= elementHandlerPoolSize){			 
			elementHandlerPoolSize+= elementHandlerAverageUse;
			ElementHandler[] increased = new ElementHandler[elementHandlerPoolSize];
			System.arraycopy(this.elementHandler, 0, increased, 0, elementHandlerFree);
			this.elementHandler = increased;
		}
		System.arraycopy(elementHandler, 0, this.elementHandler, elementHandlerFree, elementHandlerAverageUse);
		elementHandlerFree += elementHandlerAverageUse;
		if(this.elementHandlerAverageUse != 0)this.elementHandlerAverageUse = (this.elementHandlerAverageUse + elementHandlerAverageUse)/2;
		else this.elementHandlerAverageUse = elementHandlerAverageUse;
		// System.out.println(" elementHandler "+this.elementHandlerAverageUse);
		
		if(attributeHandlerFree + attributeHandlerAverageUse >= attributeHandlerPoolSize){			 
			attributeHandlerPoolSize+= attributeHandlerAverageUse;
			AttributeHandler[] increased = new AttributeHandler[attributeHandlerPoolSize];
			System.arraycopy(this.attributeHandler, 0, increased, 0, attributeHandlerFree);
			this.attributeHandler = increased;
		}
		System.arraycopy(attributeHandler, 0, this.attributeHandler, attributeHandlerFree, attributeHandlerAverageUse);
		attributeHandlerFree += attributeHandlerAverageUse;
		if(this.attributeHandlerAverageUse != 0)this.attributeHandlerAverageUse = (this.attributeHandlerAverageUse + attributeHandlerAverageUse)/2;
		else this.attributeHandlerAverageUse = attributeHandlerAverageUse;
		// System.out.println(" attributeHandler "+this.attributeHandlerAverageUse);
		
		if(exceptPatternHandlerFree + exceptPatternHandlerAverageUse >= exceptPatternHandlerPoolSize){			 
			exceptPatternHandlerPoolSize+= exceptPatternHandlerAverageUse;
			ExceptPatternHandler[] increased = new ExceptPatternHandler[exceptPatternHandlerPoolSize];
			System.arraycopy(this.exceptPatternHandler, 0, increased, 0, exceptPatternHandlerFree);
			this.exceptPatternHandler = increased;
		}
		System.arraycopy(exceptPatternHandler, 0, this.exceptPatternHandler, exceptPatternHandlerFree, exceptPatternHandlerAverageUse);
		exceptPatternHandlerFree += exceptPatternHandlerAverageUse;
		if(this.exceptPatternHandlerAverageUse != 0)this.exceptPatternHandlerAverageUse = (this.exceptPatternHandlerAverageUse + exceptPatternHandlerAverageUse)/2;
		else this.exceptPatternHandlerAverageUse = exceptPatternHandlerAverageUse;
		// System.out.println(" exceptPatternHandler "+this.exceptPatternHandlerAverageUse);
		
		if(listPatternHandlerFree + listPatternHandlerAverageUse >= listPatternHandlerPoolSize){			 
			listPatternHandlerPoolSize+= listPatternHandlerAverageUse;
			ListPatternHandler[] increased = new ListPatternHandler[listPatternHandlerPoolSize];
			System.arraycopy(this.listPatternHandler, 0, increased, 0, listPatternHandlerFree);
			this.listPatternHandler = increased;
		}
		System.arraycopy(listPatternHandler, 0, this.listPatternHandler, listPatternHandlerFree, listPatternHandlerAverageUse);
		listPatternHandlerFree += listPatternHandlerAverageUse;
		if(this.listPatternHandlerAverageUse != 0)this.listPatternHandlerAverageUse = (this.listPatternHandlerAverageUse + listPatternHandlerAverageUse)/2;
		else this.listPatternHandlerAverageUse = listPatternHandlerAverageUse;
		// System.out.println(" listPatternHandler "+this.listPatternHandlerAverageUse);
		
		
		
		
		
		if(groupDoubleHandlerFree + groupDoubleHandlerAverageUse >= groupDoubleHandlerPoolSize){			 
			groupDoubleHandlerPoolSize+= groupDoubleHandlerAverageUse;
			GroupDoubleHandler[] increased = new GroupDoubleHandler[groupDoubleHandlerPoolSize];
			System.arraycopy(this.groupDoubleHandler, 0, increased, 0, groupDoubleHandlerFree);
			this.groupDoubleHandler = increased;
		}
		System.arraycopy(groupDoubleHandler, 0, this.groupDoubleHandler, groupDoubleHandlerFree, groupDoubleHandlerAverageUse);
		groupDoubleHandlerFree += groupDoubleHandlerAverageUse;
		if(this.groupDoubleHandlerAverageUse != 0)this.groupDoubleHandlerAverageUse = (this.groupDoubleHandlerAverageUse + groupDoubleHandlerAverageUse)/2;
		else this.groupDoubleHandlerAverageUse = groupDoubleHandlerAverageUse;
		// System.out.println(" groupDoubleHandler "+this.groupDoubleHandlerAverageUse);
		
		if(interleaveDoubleHandlerFree + interleaveDoubleHandlerAverageUse >= interleaveDoubleHandlerPoolSize){			 
			interleaveDoubleHandlerPoolSize+= interleaveDoubleHandlerAverageUse;
			InterleaveDoubleHandler[] increased = new InterleaveDoubleHandler[interleaveDoubleHandlerPoolSize];
			System.arraycopy(this.interleaveDoubleHandler, 0, increased, 0, interleaveDoubleHandlerFree);
			this.interleaveDoubleHandler = increased;
		}
		System.arraycopy(interleaveDoubleHandler, 0, this.interleaveDoubleHandler, interleaveDoubleHandlerFree, interleaveDoubleHandlerAverageUse);
		interleaveDoubleHandlerFree += interleaveDoubleHandlerAverageUse;
		if(this.interleaveDoubleHandlerAverageUse != 0)this.interleaveDoubleHandlerAverageUse = (this.interleaveDoubleHandlerAverageUse + interleaveDoubleHandlerAverageUse)/2;
		else this.interleaveDoubleHandlerAverageUse = interleaveDoubleHandlerAverageUse;
		// System.out.println(" interleaveDoubleHandler "+this.interleaveDoubleHandlerAverageUse);
		
		
		
		if(groupMinimalReduceCountHandlerFree + groupMinimalReduceCountHandlerAverageUse >= groupMinimalReduceCountHandlerPoolSize){			 
			groupMinimalReduceCountHandlerPoolSize+= groupMinimalReduceCountHandlerAverageUse;
			GroupMinimalReduceCountHandler[] increased = new GroupMinimalReduceCountHandler[groupMinimalReduceCountHandlerPoolSize];
			System.arraycopy(this.groupMinimalReduceCountHandler, 0, increased, 0, groupMinimalReduceCountHandlerFree);
			this.groupMinimalReduceCountHandler = increased;
		}
		System.arraycopy(groupMinimalReduceCountHandler, 0, this.groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, groupMinimalReduceCountHandlerAverageUse);
		groupMinimalReduceCountHandlerFree += groupMinimalReduceCountHandlerAverageUse;
		if(this.groupMinimalReduceCountHandlerAverageUse != 0)this.groupMinimalReduceCountHandlerAverageUse = (this.groupMinimalReduceCountHandlerAverageUse + groupMinimalReduceCountHandlerAverageUse)/2;
		else this.groupMinimalReduceCountHandlerAverageUse = groupMinimalReduceCountHandlerAverageUse;
		// System.out.println(" groupMinimalReduceCountHandler "+this.groupMinimalReduceCountHandlerAverageUse);
		
		if(groupMaximalReduceCountHandlerFree + groupMaximalReduceCountHandlerAverageUse >= groupMaximalReduceCountHandlerPoolSize){			 
			groupMaximalReduceCountHandlerPoolSize+= groupMaximalReduceCountHandlerAverageUse;
			GroupMaximalReduceCountHandler[] increased = new GroupMaximalReduceCountHandler[groupMaximalReduceCountHandlerPoolSize];
			System.arraycopy(this.groupMaximalReduceCountHandler, 0, increased, 0, groupMaximalReduceCountHandlerFree);
			this.groupMaximalReduceCountHandler = increased;
		}
		System.arraycopy(groupMaximalReduceCountHandler, 0, this.groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, groupMaximalReduceCountHandlerAverageUse);
		groupMaximalReduceCountHandlerFree += groupMaximalReduceCountHandlerAverageUse;
		if(this.groupMaximalReduceCountHandlerAverageUse != 0)this.groupMaximalReduceCountHandlerAverageUse = (this.groupMaximalReduceCountHandlerAverageUse + groupMaximalReduceCountHandlerAverageUse)/2;
		else this.groupMaximalReduceCountHandlerAverageUse = groupMaximalReduceCountHandlerAverageUse;
		// System.out.println(" groupMaximalReduceCountHandler "+this.groupMaximalReduceCountHandlerAverageUse);
		
		if(interleaveMinimalReduceCountHandlerFree + interleaveMinimalReduceCountHandlerAverageUse >= interleaveMinimalReduceCountHandlerPoolSize){			 
			interleaveMinimalReduceCountHandlerPoolSize+= interleaveMinimalReduceCountHandlerAverageUse;
			InterleaveMinimalReduceCountHandler[] increased = new InterleaveMinimalReduceCountHandler[interleaveMinimalReduceCountHandlerPoolSize];
			System.arraycopy(this.interleaveMinimalReduceCountHandler, 0, increased, 0, interleaveMinimalReduceCountHandlerFree);
			this.interleaveMinimalReduceCountHandler = increased;
		}
		System.arraycopy(interleaveMinimalReduceCountHandler, 0, this.interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, interleaveMinimalReduceCountHandlerAverageUse);
		interleaveMinimalReduceCountHandlerFree += interleaveMinimalReduceCountHandlerAverageUse;
		if(this.interleaveMinimalReduceCountHandlerAverageUse != 0)this.interleaveMinimalReduceCountHandlerAverageUse = (this.interleaveMinimalReduceCountHandlerAverageUse + interleaveMinimalReduceCountHandlerAverageUse)/2;
		else this.interleaveMinimalReduceCountHandlerAverageUse = interleaveMinimalReduceCountHandlerAverageUse;
		// System.out.println(" interleaveMinimalReduceCountHandler "+this.interleaveMinimalReduceCountHandlerAverageUse);
		
		if(interleaveMaximalReduceCountHandlerFree + interleaveMaximalReduceCountHandlerAverageUse >= interleaveMaximalReduceCountHandlerPoolSize){			 
			interleaveMaximalReduceCountHandlerPoolSize+= interleaveMaximalReduceCountHandlerAverageUse;
			InterleaveMaximalReduceCountHandler[] increased = new InterleaveMaximalReduceCountHandler[interleaveMaximalReduceCountHandlerPoolSize];
			System.arraycopy(this.interleaveMaximalReduceCountHandler, 0, increased, 0, interleaveMaximalReduceCountHandlerFree);
			this.interleaveMaximalReduceCountHandler = increased;
		}
		System.arraycopy(interleaveMaximalReduceCountHandler, 0, this.interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, interleaveMaximalReduceCountHandlerAverageUse);
		interleaveMaximalReduceCountHandlerFree += interleaveMaximalReduceCountHandlerAverageUse;
		if(this.interleaveMaximalReduceCountHandlerAverageUse != 0)this.interleaveMaximalReduceCountHandlerAverageUse = (this.interleaveMaximalReduceCountHandlerAverageUse + interleaveMaximalReduceCountHandlerAverageUse)/2;
		else this.interleaveMaximalReduceCountHandlerAverageUse = interleaveMaximalReduceCountHandlerAverageUse;
		// System.out.println(" interleaveMaximalReduceCountHandler "+this.interleaveMaximalReduceCountHandlerAverageUse);
		
				
		
		
		if(grammarMinimalReduceHandlerFree + grammarMinimalReduceHandlerAverageUse >= grammarMinimalReduceHandlerPoolSize){			 
			grammarMinimalReduceHandlerPoolSize+= grammarMinimalReduceHandlerAverageUse;
			GrammarMinimalReduceHandler[] increased = new GrammarMinimalReduceHandler[grammarMinimalReduceHandlerPoolSize];
			System.arraycopy(this.grammarMinimalReduceHandler, 0, increased, 0, grammarMinimalReduceHandlerFree);
			this.grammarMinimalReduceHandler = increased;
		}
		System.arraycopy(grammarMinimalReduceHandler, 0, this.grammarMinimalReduceHandler, grammarMinimalReduceHandlerFree, grammarMinimalReduceHandlerAverageUse);
		grammarMinimalReduceHandlerFree += grammarMinimalReduceHandlerAverageUse;
		if(this.grammarMinimalReduceHandlerAverageUse != 0)this.grammarMinimalReduceHandlerAverageUse = (this.grammarMinimalReduceHandlerAverageUse + grammarMinimalReduceHandlerAverageUse)/2;
		else this.grammarMinimalReduceHandlerAverageUse = grammarMinimalReduceHandlerAverageUse;
		// System.out.println(" grammarMinimalReduceHandler "+this.grammarMinimalReduceHandlerAverageUse);
		
		if(grammarMaximalReduceHandlerFree + grammarMaximalReduceHandlerAverageUse >= grammarMaximalReduceHandlerPoolSize){			 
			grammarMaximalReduceHandlerPoolSize+= grammarMaximalReduceHandlerAverageUse;
			GrammarMaximalReduceHandler[] increased = new GrammarMaximalReduceHandler[grammarMaximalReduceHandlerPoolSize];
			System.arraycopy(this.grammarMaximalReduceHandler, 0, increased, 0, grammarMaximalReduceHandlerFree);
			this.grammarMaximalReduceHandler = increased;
		}
		System.arraycopy(grammarMaximalReduceHandler, 0, this.grammarMaximalReduceHandler, grammarMaximalReduceHandlerFree, grammarMaximalReduceHandlerAverageUse);
		grammarMaximalReduceHandlerFree += grammarMaximalReduceHandlerAverageUse;
		if(this.grammarMaximalReduceHandlerAverageUse != 0)this.grammarMaximalReduceHandlerAverageUse = (this.grammarMaximalReduceHandlerAverageUse + grammarMaximalReduceHandlerAverageUse)/2;
		else this.grammarMaximalReduceHandlerAverageUse = grammarMaximalReduceHandlerAverageUse;
		// System.out.println(" grammarMaximalReduceHandler "+this.grammarMaximalReduceHandlerAverageUse);
		
		if(refMinimalReduceHandlerFree + refMinimalReduceHandlerAverageUse >= refMinimalReduceHandlerPoolSize){			 
			refMinimalReduceHandlerPoolSize+= refMinimalReduceHandlerAverageUse;
			RefMinimalReduceHandler[] increased = new RefMinimalReduceHandler[refMinimalReduceHandlerPoolSize];
			System.arraycopy(this.refMinimalReduceHandler, 0, increased, 0, refMinimalReduceHandlerFree);
			this.refMinimalReduceHandler = increased;
		}
		System.arraycopy(refMinimalReduceHandler, 0, this.refMinimalReduceHandler, refMinimalReduceHandlerFree, refMinimalReduceHandlerAverageUse);
		refMinimalReduceHandlerFree += refMinimalReduceHandlerAverageUse;
		if(this.refMinimalReduceHandlerAverageUse != 0)this.refMinimalReduceHandlerAverageUse = (this.refMinimalReduceHandlerAverageUse + refMinimalReduceHandlerAverageUse)/2;
		else this.refMinimalReduceHandlerAverageUse = refMinimalReduceHandlerAverageUse;
		// System.out.println(" refMinimalReduceHandler "+this.refMinimalReduceHandlerAverageUse);
		
		if(refMaximalReduceHandlerFree + refMaximalReduceHandlerAverageUse >= refMaximalReduceHandlerPoolSize){			 
			refMaximalReduceHandlerPoolSize+= refMaximalReduceHandlerAverageUse;
			RefMaximalReduceHandler[] increased = new RefMaximalReduceHandler[refMaximalReduceHandlerPoolSize];
			System.arraycopy(this.refMaximalReduceHandler, 0, increased, 0, refMaximalReduceHandlerFree);
			this.refMaximalReduceHandler = increased;
		}
		System.arraycopy(refMaximalReduceHandler, 0, this.refMaximalReduceHandler, refMaximalReduceHandlerFree, refMaximalReduceHandlerAverageUse);
		refMaximalReduceHandlerFree += refMaximalReduceHandlerAverageUse;
		if(this.refMaximalReduceHandlerAverageUse != 0)this.refMaximalReduceHandlerAverageUse = (this.refMaximalReduceHandlerAverageUse + refMaximalReduceHandlerAverageUse)/2;
		else this.refMaximalReduceHandlerAverageUse = refMaximalReduceHandlerAverageUse;
		// System.out.println(" refMaximalReduceHandler "+this.refMaximalReduceHandlerAverageUse);
		
		if(choiceMinimalReduceHandlerFree + choiceMinimalReduceHandlerAverageUse >= choiceMinimalReduceHandlerPoolSize){			 
			choiceMinimalReduceHandlerPoolSize+= choiceMinimalReduceHandlerAverageUse;
			ChoiceMinimalReduceHandler[] increased = new ChoiceMinimalReduceHandler[choiceMinimalReduceHandlerPoolSize];
			System.arraycopy(this.choiceMinimalReduceHandler, 0, increased, 0, choiceMinimalReduceHandlerFree);
			this.choiceMinimalReduceHandler = increased;
		}
		System.arraycopy(choiceMinimalReduceHandler, 0, this.choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, choiceMinimalReduceHandlerAverageUse);
		choiceMinimalReduceHandlerFree += choiceMinimalReduceHandlerAverageUse;
		if(this.choiceMinimalReduceHandlerAverageUse != 0)this.choiceMinimalReduceHandlerAverageUse = (this.choiceMinimalReduceHandlerAverageUse + choiceMinimalReduceHandlerAverageUse)/2;
		else this.choiceMinimalReduceHandlerAverageUse = choiceMinimalReduceHandlerAverageUse;
		// System.out.println(" choiceMinimalReduceHandler "+this.choiceMinimalReduceHandlerAverageUse);
		
		if(choiceMaximalReduceHandlerFree + choiceMaximalReduceHandlerAverageUse >= choiceMaximalReduceHandlerPoolSize){			 
			choiceMaximalReduceHandlerPoolSize+= choiceMaximalReduceHandlerAverageUse;
			ChoiceMaximalReduceHandler[] increased = new ChoiceMaximalReduceHandler[choiceMaximalReduceHandlerPoolSize];
			System.arraycopy(this.choiceMaximalReduceHandler, 0, increased, 0, choiceMaximalReduceHandlerFree);
			this.choiceMaximalReduceHandler = increased;
		}
		System.arraycopy(choiceMaximalReduceHandler, 0, this.choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, choiceMaximalReduceHandlerAverageUse);
		choiceMaximalReduceHandlerFree += choiceMaximalReduceHandlerAverageUse;
		if(this.choiceMaximalReduceHandlerAverageUse != 0)this.choiceMaximalReduceHandlerAverageUse = (this.choiceMaximalReduceHandlerAverageUse + choiceMaximalReduceHandlerAverageUse)/2;
		else this.choiceMaximalReduceHandlerAverageUse = choiceMaximalReduceHandlerAverageUse;
		// System.out.println(" choiceMaximalReduceHandler "+this.choiceMaximalReduceHandlerAverageUse);
				
		if(groupMinimalReduceHandlerFree + groupMinimalReduceHandlerAverageUse >= groupMinimalReduceHandlerPoolSize){			 
			groupMinimalReduceHandlerPoolSize+= groupMinimalReduceHandlerAverageUse;
			GroupMinimalReduceHandler[] increased = new GroupMinimalReduceHandler[groupMinimalReduceHandlerPoolSize];
			System.arraycopy(this.groupMinimalReduceHandler, 0, increased, 0, groupMinimalReduceHandlerFree);
			this.groupMinimalReduceHandler = increased;
		}
		System.arraycopy(groupMinimalReduceHandler, 0, this.groupMinimalReduceHandler, groupMinimalReduceHandlerFree, groupMinimalReduceHandlerAverageUse);
		groupMinimalReduceHandlerFree += groupMinimalReduceHandlerAverageUse;
		if(this.groupMinimalReduceHandlerAverageUse != 0)this.groupMinimalReduceHandlerAverageUse = (this.groupMinimalReduceHandlerAverageUse + groupMinimalReduceHandlerAverageUse)/2;
		else this.groupMinimalReduceHandlerAverageUse = groupMinimalReduceHandlerAverageUse;
		// System.out.println(" groupMinimalReduceHandler "+this.groupMinimalReduceHandlerAverageUse);
		
		if(groupMaximalReduceHandlerFree + groupMaximalReduceHandlerAverageUse >= groupMaximalReduceHandlerPoolSize){			 
			groupMaximalReduceHandlerPoolSize+= groupMaximalReduceHandlerAverageUse;
			GroupMaximalReduceHandler[] increased = new GroupMaximalReduceHandler[groupMaximalReduceHandlerPoolSize];
			System.arraycopy(this.groupMaximalReduceHandler, 0, increased, 0, groupMaximalReduceHandlerFree);
			this.groupMaximalReduceHandler = increased;
		}
		System.arraycopy(groupMaximalReduceHandler, 0, this.groupMaximalReduceHandler, groupMaximalReduceHandlerFree, groupMaximalReduceHandlerAverageUse);
		groupMaximalReduceHandlerFree += groupMaximalReduceHandlerAverageUse;
		if(this.groupMaximalReduceHandlerAverageUse != 0)this.groupMaximalReduceHandlerAverageUse = (this.groupMaximalReduceHandlerAverageUse + groupMaximalReduceHandlerAverageUse)/2;
		else this.groupMaximalReduceHandlerAverageUse = groupMaximalReduceHandlerAverageUse;
		// System.out.println(" groupMaximalReduceHandler "+this.groupMaximalReduceHandlerAverageUse);
		
		if(interleaveMinimalReduceHandlerFree + interleaveMinimalReduceHandlerAverageUse >= interleaveMinimalReduceHandlerPoolSize){			 
			interleaveMinimalReduceHandlerPoolSize+= interleaveMinimalReduceHandlerAverageUse;
			InterleaveMinimalReduceHandler[] increased = new InterleaveMinimalReduceHandler[interleaveMinimalReduceHandlerPoolSize];
			System.arraycopy(this.interleaveMinimalReduceHandler, 0, increased, 0, interleaveMinimalReduceHandlerFree);
			this.interleaveMinimalReduceHandler = increased;
		}
		System.arraycopy(interleaveMinimalReduceHandler, 0, this.interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, interleaveMinimalReduceHandlerAverageUse);
		interleaveMinimalReduceHandlerFree += interleaveMinimalReduceHandlerAverageUse;
		if(this.interleaveMinimalReduceHandlerAverageUse != 0)this.interleaveMinimalReduceHandlerAverageUse = (this.interleaveMinimalReduceHandlerAverageUse + interleaveMinimalReduceHandlerAverageUse)/2;
		else this.interleaveMinimalReduceHandlerAverageUse = interleaveMinimalReduceHandlerAverageUse;
		// System.out.println(" interleaveMinimalReduceHandler "+this.interleaveMinimalReduceHandlerAverageUse);
		
		if(interleaveMaximalReduceHandlerFree + interleaveMaximalReduceHandlerAverageUse >= interleaveMaximalReduceHandlerPoolSize){			 
			interleaveMaximalReduceHandlerPoolSize+= interleaveMaximalReduceHandlerAverageUse;
			InterleaveMaximalReduceHandler[] increased = new InterleaveMaximalReduceHandler[interleaveMaximalReduceHandlerPoolSize];
			System.arraycopy(this.interleaveMaximalReduceHandler, 0, increased, 0, interleaveMaximalReduceHandlerFree);
			this.interleaveMaximalReduceHandler = increased;
		}
		System.arraycopy(interleaveMaximalReduceHandler, 0, this.interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, interleaveMaximalReduceHandlerAverageUse);
		interleaveMaximalReduceHandlerFree += interleaveMaximalReduceHandlerAverageUse;
		if(this.interleaveMaximalReduceHandlerAverageUse != 0)this.interleaveMaximalReduceHandlerAverageUse = (this.interleaveMaximalReduceHandlerAverageUse + interleaveMaximalReduceHandlerAverageUse)/2;
		else this.interleaveMaximalReduceHandlerAverageUse = interleaveMaximalReduceHandlerAverageUse;
		// System.out.println(" interleaveMaximalReduceHandler "+this.interleaveMaximalReduceHandlerAverageUse);
	}
} 