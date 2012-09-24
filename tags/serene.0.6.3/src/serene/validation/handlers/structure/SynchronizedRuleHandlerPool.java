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

public class SynchronizedRuleHandlerPool extends RuleHandlerPool{
	private static volatile SynchronizedRuleHandlerPool instance;
	
	int amrhPoolFree; 
	int amrhPoolMaxSize;
	ValidatorRuleHandlerPool[] amrhPools;
	
	int particleHandlerAverageUse;
	int particleHandlerMaxSize;
	int particleHandlerFree;
	ParticleHandler[] particleHandler;
	
	int choiceHandlerAverageUse;
	int choiceHandlerMaxSize;
	int choiceHandlerFree;
	ChoiceHandler[] choiceHandler;
	
	int groupHandlerAverageUse;
	int groupHandlerMaxSize;
	int groupHandlerFree;
	GroupHandler[] groupHandler;
		
	int intermediaryPatternHandlerAverageUse;
	int intermediaryPatternHandlerMaxSize;
	int intermediaryPatternHandlerFree;
	IntermediaryPatternHandler[] intermediaryPatternHandler;
		
	int uinterleaveHandlerAverageUse;
	int uinterleaveHandlerMaxSize;
	int uinterleaveHandlerFree;
	UInterleaveHandler[] uinterleaveHandler;
	
	int minterleaveHandlerAverageUse;
	int minterleaveHandlerMaxSize;
	int minterleaveHandlerFree;
	MInterleaveHandler[] minterleaveHandler;
	
	int sinterleaveHandlerAverageUse;
	int sinterleaveHandlerMaxSize;
	int sinterleaveHandlerFree;
	SInterleaveHandler[] sinterleaveHandler;
	
	
	int typeHandlerAverageUse;
	int typeHandlerMaxSize;
	int typeHandlerFree;
	TypeHandler[] typeHandler;
	
		
	
	int groupDoubleHandlerAverageUse;
	int groupDoubleHandlerMaxSize;
	int groupDoubleHandlerFree;
	GroupDoubleHandler[] groupDoubleHandler;
	
	int interleaveDoubleHandlerAverageUse;
	int interleaveDoubleHandlerMaxSize;
	int interleaveDoubleHandlerFree;
	InterleaveDoubleHandler[] interleaveDoubleHandler;
	
	
	
	int groupMinimalReduceCountHandlerAverageUse;
	int groupMinimalReduceCountHandlerMaxSize;
	int groupMinimalReduceCountHandlerFree;
	GroupMinimalReduceCountHandler[] groupMinimalReduceCountHandler;
	
	int groupMaximalReduceCountHandlerAverageUse;
	int groupMaximalReduceCountHandlerMaxSize;
	int groupMaximalReduceCountHandlerFree;
	GroupMaximalReduceCountHandler[] groupMaximalReduceCountHandler;
	
	int interleaveMinimalReduceCountHandlerAverageUse;
	int interleaveMinimalReduceCountHandlerMaxSize;
	int interleaveMinimalReduceCountHandlerFree;
	InterleaveMinimalReduceCountHandler[] interleaveMinimalReduceCountHandler;
	
	int interleaveMaximalReduceCountHandlerAverageUse;
	int interleaveMaximalReduceCountHandlerMaxSize;
	int interleaveMaximalReduceCountHandlerFree;
	InterleaveMaximalReduceCountHandler[] interleaveMaximalReduceCountHandler;
		
	
	
	
	int intermediaryPatternMinimalReduceHandlerAverageUse;
	int intermediaryPatternMinimalReduceHandlerMaxSize;
	int intermediaryPatternMinimalReduceHandlerFree;
	IntermediaryPatternMinimalReduceHandler[] intermediaryPatternMinimalReduceHandler;
	
	int intermediaryPatternMaximalReduceHandlerAverageUse;
	int intermediaryPatternMaximalReduceHandlerMaxSize;
	int intermediaryPatternMaximalReduceHandlerFree;
	IntermediaryPatternMaximalReduceHandler[] intermediaryPatternMaximalReduceHandler;
		
	int choiceMinimalReduceHandlerAverageUse;
	int choiceMinimalReduceHandlerMaxSize;
	int choiceMinimalReduceHandlerFree;
	ChoiceMinimalReduceHandler[] choiceMinimalReduceHandler;
	
	int choiceMaximalReduceHandlerAverageUse;
	int choiceMaximalReduceHandlerMaxSize;
	int choiceMaximalReduceHandlerFree;
	ChoiceMaximalReduceHandler[] choiceMaximalReduceHandler;
	
	int groupMinimalReduceHandlerAverageUse;
	int groupMinimalReduceHandlerMaxSize;
	int groupMinimalReduceHandlerFree;
	GroupMinimalReduceHandler[] groupMinimalReduceHandler;
	
	int groupMaximalReduceHandlerAverageUse;
	int groupMaximalReduceHandlerMaxSize;
	int groupMaximalReduceHandlerFree;
	GroupMaximalReduceHandler[] groupMaximalReduceHandler;
	
	int interleaveMinimalReduceHandlerAverageUse;
	int interleaveMinimalReduceHandlerMaxSize;
	int interleaveMinimalReduceHandlerFree;
	InterleaveMinimalReduceHandler[] interleaveMinimalReduceHandler;
	
	int interleaveMaximalReduceHandlerAverageUse;
	int interleaveMaximalReduceHandlerMaxSize;
	int interleaveMaximalReduceHandlerFree;
	InterleaveMaximalReduceHandler[] interleaveMaximalReduceHandler;
	
	SynchronizedRuleHandlerPool(){
		super();
		
		amrhPoolFree = 0;
		amrhPoolMaxSize = 10;
		amrhPools = new ValidatorRuleHandlerPool[10];
		
		particleHandlerAverageUse = 0;
		particleHandlerFree = 0;
		particleHandler = new ParticleHandler[30];
		
		choiceHandlerAverageUse = 0;
		choiceHandlerFree = 0;
		choiceHandler = new ChoiceHandler[10];
		
		groupHandlerAverageUse = 0;
		groupHandlerFree = 0;
		groupHandler = new GroupHandler[10];
		
		intermediaryPatternHandlerAverageUse = 0;
		intermediaryPatternHandlerFree = 0;
		intermediaryPatternHandler = new IntermediaryPatternHandler[10];
		
		uinterleaveHandlerAverageUse = 0;
		uinterleaveHandlerFree = 0;
		uinterleaveHandler = new UInterleaveHandler[10];
		
		minterleaveHandlerAverageUse = 0;
		minterleaveHandlerFree = 0;
		minterleaveHandler = new MInterleaveHandler[10];
		
		sinterleaveHandlerAverageUse = 0;
		sinterleaveHandlerFree = 0;
		sinterleaveHandler = new SInterleaveHandler[10];
		
		
		typeHandlerAverageUse = 0;
		typeHandlerFree = 0;
		typeHandler = new TypeHandler[10];
		
		
		
		
		
		groupDoubleHandlerAverageUse = 0;
		groupDoubleHandlerFree = 0;
		groupDoubleHandler = new GroupDoubleHandler[10];
				
		interleaveDoubleHandlerAverageUse = 0;
		interleaveDoubleHandlerFree = 0;
		interleaveDoubleHandler = new InterleaveDoubleHandler[10];
				
		
		
		groupMinimalReduceCountHandlerAverageUse = 0;
		groupMinimalReduceCountHandlerFree = 0;
		groupMinimalReduceCountHandler = new GroupMinimalReduceCountHandler[5];
		
		groupMaximalReduceCountHandlerAverageUse = 0;
		groupMaximalReduceCountHandlerFree = 0;
		groupMaximalReduceCountHandler = new GroupMaximalReduceCountHandler[5];
		
		interleaveMinimalReduceCountHandlerAverageUse = 0;
		interleaveMinimalReduceCountHandlerFree = 0;
		interleaveMinimalReduceCountHandler = new InterleaveMinimalReduceCountHandler[5];
				
		interleaveMaximalReduceCountHandlerAverageUse = 0;
		interleaveMaximalReduceCountHandlerFree = 0;
		interleaveMaximalReduceCountHandler = new InterleaveMaximalReduceCountHandler[5];
		
			
		
		
		intermediaryPatternMinimalReduceHandlerAverageUse = 0;
		intermediaryPatternMinimalReduceHandlerFree = 0;
		intermediaryPatternMinimalReduceHandler = new IntermediaryPatternMinimalReduceHandler[5];
		
		intermediaryPatternMaximalReduceHandlerAverageUse = 0;
		intermediaryPatternMaximalReduceHandlerFree = 0;
		intermediaryPatternMaximalReduceHandler = new IntermediaryPatternMaximalReduceHandler[5];
			
		choiceMinimalReduceHandlerAverageUse = 0;
		choiceMinimalReduceHandlerFree = 0;
		choiceMinimalReduceHandler = new ChoiceMinimalReduceHandler[5];
		
		choiceMaximalReduceHandlerAverageUse = 0;
		choiceMaximalReduceHandlerFree = 0;
		choiceMaximalReduceHandler = new ChoiceMaximalReduceHandler[5];
		
		groupMinimalReduceHandlerAverageUse = 0;
		groupMinimalReduceHandlerFree = 0;
		groupMinimalReduceHandler = new GroupMinimalReduceHandler[5];
		
		groupMaximalReduceHandlerAverageUse = 0;
		groupMaximalReduceHandlerFree = 0;
		groupMaximalReduceHandler = new GroupMaximalReduceHandler[5];
		
		interleaveMinimalReduceHandlerAverageUse = 0;
		interleaveMinimalReduceHandlerFree = 0;
		interleaveMinimalReduceHandler = new InterleaveMinimalReduceHandler[5];
		
		interleaveMaximalReduceHandlerAverageUse = 0;
		interleaveMaximalReduceHandlerFree = 0;
		interleaveMaximalReduceHandler = new InterleaveMaximalReduceHandler[5];
		
		
		amrhPoolMaxSize = 10;
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
	}
	
	public static SynchronizedRuleHandlerPool getInstance(){
		if(instance == null){
			synchronized(RuleHandlerPool.class){
				if(instance == null){
					instance = new SynchronizedRuleHandlerPool(); 
				}
			}
		}
		return instance;
	}
	
	public synchronized ValidatorRuleHandlerPool getValidatorRuleHandlerPool(){
		if(amrhPoolFree == 0){
			ValidatorRuleHandlerPool amrhp = new ValidatorRuleHandlerPool(this);
			return amrhp;
		}else{
			ValidatorRuleHandlerPool amrhp = amrhPools[--amrhPoolFree];
			return amrhp;
		}
	}
		
	public synchronized void recycle(ValidatorRuleHandlerPool amrhp){
	    if(amrhPools.length == amrhPoolMaxSize) return;
		if(amrhPoolFree == amrhPools.length){		    
			ValidatorRuleHandlerPool[] increased = new ValidatorRuleHandlerPool[10+amrhPools.length];
			System.arraycopy(amrhPools, 0, increased, 0, amrhPoolFree);
			amrhPools = increased;
		}
		amrhPools[amrhPoolFree++] = amrhp;
	}	
	
	synchronized void fill(ValidatorRuleHandlerPool ruleHandlerPool,
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
		int particleHandlerFillCount;
		if(particleHandlerToFill == null || particleHandlerToFill.length < particleHandlerAverageUse){
			particleHandlerToFill = new ParticleHandler[particleHandlerAverageUse];
		    ruleHandlerPool.particleHandler = particleHandlerToFill;
		}
		if(particleHandlerFree > particleHandlerAverageUse){
			particleHandlerFillCount = particleHandlerAverageUse;
			particleHandlerFree = particleHandlerFree - particleHandlerAverageUse;
		}else{
			particleHandlerFillCount = particleHandlerFree;
			particleHandlerFree = 0;
		}		
		System.arraycopy(particleHandler, particleHandlerFree, 
							particleHandlerToFill, 0, particleHandlerFillCount);
		
		int choiceHandlerFillCount;
		if(choiceHandlerToFill == null || choiceHandlerToFill.length < choiceHandlerAverageUse){
			choiceHandlerToFill = new ChoiceHandler[choiceHandlerAverageUse];
		    ruleHandlerPool.choiceHandler = choiceHandlerToFill;
		}
		if(choiceHandlerFree > choiceHandlerAverageUse){
			choiceHandlerFillCount = choiceHandlerAverageUse;
			choiceHandlerFree = choiceHandlerFree - choiceHandlerAverageUse;
		}else{
			choiceHandlerFillCount = choiceHandlerFree;
			choiceHandlerFree = 0;
		}
		System.arraycopy(choiceHandler, choiceHandlerFree, 
							choiceHandlerToFill, 0, choiceHandlerFillCount);
		
		int groupHandlerFillCount;
		if(groupHandlerToFill == null || groupHandlerToFill.length < groupHandlerAverageUse){
			groupHandlerToFill = new GroupHandler[groupHandlerAverageUse];
		    ruleHandlerPool.groupHandler = groupHandlerToFill;
		}
		if(groupHandlerFree > groupHandlerAverageUse){
			groupHandlerFillCount = groupHandlerAverageUse;
			groupHandlerFree = groupHandlerFree - groupHandlerAverageUse;
		}else{
			groupHandlerFillCount = groupHandlerFree;
			groupHandlerFree = 0;
		}		
		System.arraycopy(groupHandler, groupHandlerFree, 
							groupHandlerToFill, 0, groupHandlerFillCount);
		
		
		int intermediaryPatternHandlerFillCount;
		if(intermediaryPatternHandlerToFill == null || intermediaryPatternHandlerToFill.length < intermediaryPatternHandlerAverageUse){
			intermediaryPatternHandlerToFill = new IntermediaryPatternHandler[intermediaryPatternHandlerAverageUse];
		    ruleHandlerPool.intermediaryPatternHandler = intermediaryPatternHandlerToFill;
		}
		if(intermediaryPatternHandlerFree > intermediaryPatternHandlerAverageUse){
			intermediaryPatternHandlerFillCount = intermediaryPatternHandlerAverageUse;
			intermediaryPatternHandlerFree = intermediaryPatternHandlerFree - intermediaryPatternHandlerAverageUse;
		}else{
			intermediaryPatternHandlerFillCount = intermediaryPatternHandlerFree;
			intermediaryPatternHandlerFree = 0;
		}		
		System.arraycopy(intermediaryPatternHandler, intermediaryPatternHandlerFree, 
							intermediaryPatternHandlerToFill, 0, intermediaryPatternHandlerFillCount);
		
		
		int uinterleaveHandlerFillCount;
		if(uinterleaveHandlerToFill == null || uinterleaveHandlerToFill.length < uinterleaveHandlerAverageUse){
			uinterleaveHandlerToFill = new UInterleaveHandler[uinterleaveHandlerAverageUse];
		    ruleHandlerPool.uinterleaveHandler = uinterleaveHandlerToFill;
        }
		if(uinterleaveHandlerFree > uinterleaveHandlerAverageUse){
			uinterleaveHandlerFillCount = uinterleaveHandlerAverageUse;
			uinterleaveHandlerFree = uinterleaveHandlerFree - uinterleaveHandlerAverageUse;
		}else{
			uinterleaveHandlerFillCount = uinterleaveHandlerFree;
			uinterleaveHandlerFree = 0;
		}		
		System.arraycopy(uinterleaveHandler, uinterleaveHandlerFree, 
							uinterleaveHandlerToFill, 0, uinterleaveHandlerFillCount);
		
		int minterleaveHandlerFillCount;
		if(minterleaveHandlerToFill == null || minterleaveHandlerToFill.length < minterleaveHandlerAverageUse){
			minterleaveHandlerToFill = new MInterleaveHandler[minterleaveHandlerAverageUse];
		    ruleHandlerPool.minterleaveHandler = minterleaveHandlerToFill;
		}
		if(minterleaveHandlerFree > minterleaveHandlerAverageUse){
			minterleaveHandlerFillCount = minterleaveHandlerAverageUse;
			minterleaveHandlerFree = minterleaveHandlerFree - minterleaveHandlerAverageUse;
		}else{
			minterleaveHandlerFillCount = minterleaveHandlerFree;
			minterleaveHandlerFree = 0;
		}		
		System.arraycopy(minterleaveHandler, minterleaveHandlerFree, 
							minterleaveHandlerToFill, 0, minterleaveHandlerFillCount);
		
		int sinterleaveHandlerFillCount;
		if(sinterleaveHandlerToFill == null || sinterleaveHandlerToFill.length < sinterleaveHandlerAverageUse){
			sinterleaveHandlerToFill = new SInterleaveHandler[sinterleaveHandlerAverageUse];
		    ruleHandlerPool.sinterleaveHandler = sinterleaveHandlerToFill;
		}
		if(sinterleaveHandlerFree > sinterleaveHandlerAverageUse){
			sinterleaveHandlerFillCount = sinterleaveHandlerAverageUse;
			sinterleaveHandlerFree = sinterleaveHandlerFree - sinterleaveHandlerAverageUse;
		}else{
			sinterleaveHandlerFillCount = sinterleaveHandlerFree;
			sinterleaveHandlerFree = 0;
		}		
		System.arraycopy(sinterleaveHandler, sinterleaveHandlerFree, 
							sinterleaveHandlerToFill, 0, sinterleaveHandlerFillCount);
		
		
		
		int typeHandlerFillCount;
		if(typeHandlerToFill == null || typeHandlerToFill.length < typeHandlerAverageUse){
			typeHandlerToFill = new TypeHandler[typeHandlerAverageUse];
		    ruleHandlerPool.typeHandler = typeHandlerToFill;
		}
		if(typeHandlerFree > typeHandlerAverageUse){
			typeHandlerFillCount = typeHandlerAverageUse;
			typeHandlerFree = typeHandlerFree - typeHandlerAverageUse;
		}else{
			typeHandlerFillCount = typeHandlerFree;
			typeHandlerFree = 0;
		}		
		System.arraycopy(typeHandler, typeHandlerFree, 
							typeHandlerToFill, 0, typeHandlerFillCount);
		
		
		
		
		
		
		int groupDoubleHandlerFillCount;
		if(groupDoubleHandlerToFill == null || groupDoubleHandlerToFill.length < groupDoubleHandlerAverageUse){
			groupDoubleHandlerToFill = new GroupDoubleHandler[groupDoubleHandlerAverageUse];
		    ruleHandlerPool.groupDoubleHandler = groupDoubleHandlerToFill;
		}
		if(groupDoubleHandlerFree > groupDoubleHandlerAverageUse){
			groupDoubleHandlerFillCount = groupDoubleHandlerAverageUse;
			groupDoubleHandlerFree = groupDoubleHandlerFree - groupDoubleHandlerAverageUse;
		}else{
			groupDoubleHandlerFillCount = groupDoubleHandlerFree;
			groupDoubleHandlerFree = 0;
		}		
		System.arraycopy(groupDoubleHandler, groupDoubleHandlerFree, 
							groupDoubleHandlerToFill, 0, groupDoubleHandlerFillCount);
		
		int interleaveDoubleHandlerFillCount;
		if(interleaveDoubleHandlerToFill == null || interleaveDoubleHandlerToFill.length < interleaveDoubleHandlerAverageUse){
			interleaveDoubleHandlerToFill = new InterleaveDoubleHandler[interleaveDoubleHandlerAverageUse];
		    ruleHandlerPool.interleaveDoubleHandler = interleaveDoubleHandlerToFill;
		}
		if(interleaveDoubleHandlerFree > interleaveDoubleHandlerAverageUse){
			interleaveDoubleHandlerFillCount = interleaveDoubleHandlerAverageUse;
			interleaveDoubleHandlerFree = interleaveDoubleHandlerFree - interleaveDoubleHandlerAverageUse;
		}else{
			interleaveDoubleHandlerFillCount = interleaveDoubleHandlerFree;
			interleaveDoubleHandlerFree = 0;
		}		
		System.arraycopy(interleaveDoubleHandler, interleaveDoubleHandlerFree, 
							interleaveDoubleHandlerToFill, 0, interleaveDoubleHandlerFillCount);
		
		
		
		
		int groupMinimalReduceCountHandlerFillCount;
		if(groupMinimalReduceCountHandlerToFill == null || groupMinimalReduceCountHandlerToFill.length < groupMinimalReduceCountHandlerAverageUse){
			groupMinimalReduceCountHandlerToFill = new GroupMinimalReduceCountHandler[groupMinimalReduceCountHandlerAverageUse];
		    ruleHandlerPool.groupMinimalReduceCountHandler = groupMinimalReduceCountHandlerToFill;
		}
		if(groupMinimalReduceCountHandlerFree > groupMinimalReduceCountHandlerAverageUse){
			groupMinimalReduceCountHandlerFillCount = groupMinimalReduceCountHandlerAverageUse;
			groupMinimalReduceCountHandlerFree = groupMinimalReduceCountHandlerFree - groupMinimalReduceCountHandlerAverageUse;
		}else{
			groupMinimalReduceCountHandlerFillCount = groupMinimalReduceCountHandlerFree;
			groupMinimalReduceCountHandlerFree = 0;
		}		
		System.arraycopy(groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, 
							groupMinimalReduceCountHandlerToFill, 0, groupMinimalReduceCountHandlerFillCount);
		
		int groupMaximalReduceCountHandlerFillCount;
		if(groupMaximalReduceCountHandlerToFill == null || groupMaximalReduceCountHandlerToFill.length < groupMaximalReduceCountHandlerAverageUse){
			groupMaximalReduceCountHandlerToFill = new GroupMaximalReduceCountHandler[groupMaximalReduceCountHandlerAverageUse];
		    ruleHandlerPool.groupMaximalReduceCountHandler = groupMaximalReduceCountHandlerToFill;
		}
		if(groupMaximalReduceCountHandlerFree > groupMaximalReduceCountHandlerAverageUse){
			groupMaximalReduceCountHandlerFillCount = groupMaximalReduceCountHandlerAverageUse;
			groupMaximalReduceCountHandlerFree = groupMaximalReduceCountHandlerFree - groupMaximalReduceCountHandlerAverageUse;
		}else{
			groupMaximalReduceCountHandlerFillCount = groupMaximalReduceCountHandlerFree;
			groupMaximalReduceCountHandlerFree = 0;
		}		
		System.arraycopy(groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, 
							groupMaximalReduceCountHandlerToFill, 0, groupMaximalReduceCountHandlerFillCount);
		
		int interleaveMinimalReduceCountHandlerFillCount;
		if(interleaveMinimalReduceCountHandlerToFill == null || interleaveMinimalReduceCountHandlerToFill.length < interleaveMinimalReduceCountHandlerAverageUse){
			interleaveMinimalReduceCountHandlerToFill = new InterleaveMinimalReduceCountHandler[interleaveMinimalReduceCountHandlerAverageUse];
		    ruleHandlerPool.interleaveMinimalReduceCountHandler = interleaveMinimalReduceCountHandlerToFill;
		}
		if(interleaveMinimalReduceCountHandlerFree > interleaveMinimalReduceCountHandlerAverageUse){
			interleaveMinimalReduceCountHandlerFillCount = interleaveMinimalReduceCountHandlerAverageUse;
			interleaveMinimalReduceCountHandlerFree = interleaveMinimalReduceCountHandlerFree - interleaveMinimalReduceCountHandlerAverageUse;
		}else{
			interleaveMinimalReduceCountHandlerFillCount = interleaveMinimalReduceCountHandlerFree;
			interleaveMinimalReduceCountHandlerFree = 0;
		}		
		System.arraycopy(interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, 
							interleaveMinimalReduceCountHandlerToFill, 0, interleaveMinimalReduceCountHandlerFillCount);
		
		int interleaveMaximalReduceCountHandlerFillCount;
		if(interleaveMaximalReduceCountHandlerToFill == null || interleaveMaximalReduceCountHandlerToFill.length < interleaveMaximalReduceCountHandlerAverageUse){
			interleaveMaximalReduceCountHandlerToFill = new InterleaveMaximalReduceCountHandler[interleaveMaximalReduceCountHandlerAverageUse];
		    ruleHandlerPool.interleaveMaximalReduceCountHandler = interleaveMaximalReduceCountHandlerToFill;
		}
		if(interleaveMaximalReduceCountHandlerFree > interleaveMaximalReduceCountHandlerAverageUse){
			interleaveMaximalReduceCountHandlerFillCount = interleaveMaximalReduceCountHandlerAverageUse;
			interleaveMaximalReduceCountHandlerFree = interleaveMaximalReduceCountHandlerFree - interleaveMaximalReduceCountHandlerAverageUse;
		}else{
			interleaveMaximalReduceCountHandlerFillCount = interleaveMaximalReduceCountHandlerFree;
			interleaveMaximalReduceCountHandlerFree = 0;
		}		
		System.arraycopy(interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, 
							interleaveMaximalReduceCountHandlerToFill, 0, interleaveMaximalReduceCountHandlerFillCount);
				
		
		
				
		
		
		
		int intermediaryPatternMinimalReduceHandlerFillCount;
		if(intermediaryPatternMinimalReduceHandlerToFill == null || intermediaryPatternMinimalReduceHandlerToFill.length < intermediaryPatternMinimalReduceHandlerAverageUse){
			intermediaryPatternMinimalReduceHandlerToFill = new IntermediaryPatternMinimalReduceHandler[intermediaryPatternMinimalReduceHandlerAverageUse];
		    ruleHandlerPool.intermediaryPatternMinimalReduceHandler = intermediaryPatternMinimalReduceHandlerToFill;
		}
		if(intermediaryPatternMinimalReduceHandlerFree > intermediaryPatternMinimalReduceHandlerAverageUse){
			intermediaryPatternMinimalReduceHandlerFillCount = intermediaryPatternMinimalReduceHandlerAverageUse;
			intermediaryPatternMinimalReduceHandlerFree = intermediaryPatternMinimalReduceHandlerFree - intermediaryPatternMinimalReduceHandlerAverageUse;
		}else{
			intermediaryPatternMinimalReduceHandlerFillCount = intermediaryPatternMinimalReduceHandlerFree;
			intermediaryPatternMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(intermediaryPatternMinimalReduceHandler, intermediaryPatternMinimalReduceHandlerFree, 
							intermediaryPatternMinimalReduceHandlerToFill, 0, intermediaryPatternMinimalReduceHandlerFillCount);
		
		int intermediaryPatternMaximalReduceHandlerFillCount;
		if(intermediaryPatternMaximalReduceHandlerToFill == null || intermediaryPatternMaximalReduceHandlerToFill.length < intermediaryPatternMaximalReduceHandlerAverageUse){
			intermediaryPatternMaximalReduceHandlerToFill = new IntermediaryPatternMaximalReduceHandler[intermediaryPatternMaximalReduceHandlerAverageUse];
		    ruleHandlerPool.intermediaryPatternMaximalReduceHandler = intermediaryPatternMaximalReduceHandlerToFill;
		}
		if(intermediaryPatternMaximalReduceHandlerFree > intermediaryPatternMaximalReduceHandlerAverageUse){
			intermediaryPatternMaximalReduceHandlerFillCount = intermediaryPatternMaximalReduceHandlerAverageUse;
			intermediaryPatternMaximalReduceHandlerFree = intermediaryPatternMaximalReduceHandlerFree - intermediaryPatternMaximalReduceHandlerAverageUse;
		}else{
			intermediaryPatternMaximalReduceHandlerFillCount = intermediaryPatternMaximalReduceHandlerFree;
			intermediaryPatternMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(intermediaryPatternMaximalReduceHandler, intermediaryPatternMaximalReduceHandlerFree, 
							intermediaryPatternMaximalReduceHandlerToFill, 0, intermediaryPatternMaximalReduceHandlerFillCount);
		
		
		int choiceMinimalReduceHandlerFillCount;
		if(choiceMinimalReduceHandlerToFill == null || choiceMinimalReduceHandlerToFill.length < choiceMinimalReduceHandlerAverageUse){
			choiceMinimalReduceHandlerToFill = new ChoiceMinimalReduceHandler[choiceMinimalReduceHandlerAverageUse];
		    ruleHandlerPool.choiceMinimalReduceHandler = choiceMinimalReduceHandlerToFill;
		}
		if(choiceMinimalReduceHandlerFree > choiceMinimalReduceHandlerAverageUse){
			choiceMinimalReduceHandlerFillCount = choiceMinimalReduceHandlerAverageUse;
			choiceMinimalReduceHandlerFree = choiceMinimalReduceHandlerFree - choiceMinimalReduceHandlerAverageUse;
		}else{
			choiceMinimalReduceHandlerFillCount = choiceMinimalReduceHandlerFree;
			choiceMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, 
							choiceMinimalReduceHandlerToFill, 0, choiceMinimalReduceHandlerFillCount);
		
		int choiceMaximalReduceHandlerFillCount;
		if(choiceMaximalReduceHandlerToFill == null || choiceMaximalReduceHandlerToFill.length < choiceMaximalReduceHandlerAverageUse){
			choiceMaximalReduceHandlerToFill = new ChoiceMaximalReduceHandler[choiceMaximalReduceHandlerAverageUse];
		    ruleHandlerPool.choiceMaximalReduceHandler = choiceMaximalReduceHandlerToFill;
		}
		if(choiceMaximalReduceHandlerFree > choiceMaximalReduceHandlerAverageUse){
			choiceMaximalReduceHandlerFillCount = choiceMaximalReduceHandlerAverageUse;
			choiceMaximalReduceHandlerFree = choiceMaximalReduceHandlerFree - choiceMaximalReduceHandlerAverageUse;
		}else{
			choiceMaximalReduceHandlerFillCount = choiceMaximalReduceHandlerFree;
			choiceMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, 
							choiceMaximalReduceHandlerToFill, 0, choiceMaximalReduceHandlerFillCount);
		
		
		int groupMinimalReduceHandlerFillCount;
		if(groupMinimalReduceHandlerToFill == null || groupMinimalReduceHandlerToFill.length < groupMinimalReduceHandlerAverageUse){
			groupMinimalReduceHandlerToFill = new GroupMinimalReduceHandler[groupMinimalReduceHandlerAverageUse];
		    ruleHandlerPool.groupMinimalReduceHandler = groupMinimalReduceHandlerToFill;
		}
		if(groupMinimalReduceHandlerFree > groupMinimalReduceHandlerAverageUse){
			groupMinimalReduceHandlerFillCount = groupMinimalReduceHandlerAverageUse;
			groupMinimalReduceHandlerFree = groupMinimalReduceHandlerFree - groupMinimalReduceHandlerAverageUse;
		}else{
			groupMinimalReduceHandlerFillCount = groupMinimalReduceHandlerFree;
			groupMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(groupMinimalReduceHandler, groupMinimalReduceHandlerFree, 
							groupMinimalReduceHandlerToFill, 0, groupMinimalReduceHandlerFillCount);
		
		int groupMaximalReduceHandlerFillCount;
		if(groupMaximalReduceHandlerToFill == null || groupMaximalReduceHandlerToFill.length < groupMaximalReduceHandlerAverageUse){
			groupMaximalReduceHandlerToFill = new GroupMaximalReduceHandler[groupMaximalReduceHandlerAverageUse];
		    ruleHandlerPool.groupMaximalReduceHandler = groupMaximalReduceHandlerToFill;
		}
		if(groupMaximalReduceHandlerFree > groupMaximalReduceHandlerAverageUse){
			groupMaximalReduceHandlerFillCount = groupMaximalReduceHandlerAverageUse;
			groupMaximalReduceHandlerFree = groupMaximalReduceHandlerFree - groupMaximalReduceHandlerAverageUse;
		}else{
			groupMaximalReduceHandlerFillCount = groupMaximalReduceHandlerFree;
			groupMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(groupMaximalReduceHandler, groupMaximalReduceHandlerFree, 
							groupMaximalReduceHandlerToFill, 0, groupMaximalReduceHandlerFillCount);
		
		int interleaveMinimalReduceHandlerFillCount;
		if(interleaveMinimalReduceHandlerToFill == null || interleaveMinimalReduceHandlerToFill.length < interleaveMinimalReduceHandlerAverageUse){
			interleaveMinimalReduceHandlerToFill = new InterleaveMinimalReduceHandler[interleaveMinimalReduceHandlerAverageUse];
		    ruleHandlerPool.interleaveMinimalReduceHandler = interleaveMinimalReduceHandlerToFill;
		}
		if(interleaveMinimalReduceHandlerFree > interleaveMinimalReduceHandlerAverageUse){
			interleaveMinimalReduceHandlerFillCount = interleaveMinimalReduceHandlerAverageUse;
			interleaveMinimalReduceHandlerFree = interleaveMinimalReduceHandlerFree - interleaveMinimalReduceHandlerAverageUse;
		}else{
			interleaveMinimalReduceHandlerFillCount = interleaveMinimalReduceHandlerFree;
			interleaveMinimalReduceHandlerFree = 0;
		}		
		System.arraycopy(interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, 
							interleaveMinimalReduceHandlerToFill, 0, interleaveMinimalReduceHandlerFillCount);
		
		int interleaveMaximalReduceHandlerFillCount;
		if(interleaveMaximalReduceHandlerToFill == null || interleaveMaximalReduceHandlerToFill.length < interleaveMaximalReduceHandlerAverageUse){
			interleaveMaximalReduceHandlerToFill = new InterleaveMaximalReduceHandler[interleaveMaximalReduceHandlerAverageUse];
		    ruleHandlerPool.interleaveMaximalReduceHandler = interleaveMaximalReduceHandlerToFill;
		}
		if(interleaveMaximalReduceHandlerFree > interleaveMaximalReduceHandlerAverageUse){
			interleaveMaximalReduceHandlerFillCount = interleaveMaximalReduceHandlerAverageUse;
			interleaveMaximalReduceHandlerFree = interleaveMaximalReduceHandlerFree - interleaveMaximalReduceHandlerAverageUse;
		}else{
			interleaveMaximalReduceHandlerFillCount = interleaveMaximalReduceHandlerFree;
			interleaveMaximalReduceHandlerFree = 0;
		}		
		System.arraycopy(interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, 
							interleaveMaximalReduceHandlerToFill, 0, interleaveMaximalReduceHandlerFillCount);
		
		
		
		ruleHandlerPool.initFilled(particleHandlerFillCount,
										choiceHandlerFillCount,
										groupHandlerFillCount,
										intermediaryPatternHandlerFillCount,
										uinterleaveHandlerFillCount,
										minterleaveHandlerFillCount,
										sinterleaveHandlerFillCount,
										typeHandlerFillCount,
										groupDoubleHandlerFillCount,
										interleaveDoubleHandlerFillCount,
										groupMinimalReduceCountHandlerFillCount,
										groupMaximalReduceCountHandlerFillCount,
										interleaveMinimalReduceCountHandlerFillCount,
										interleaveMaximalReduceCountHandlerFillCount,
										intermediaryPatternMinimalReduceHandlerFillCount,
										intermediaryPatternMaximalReduceHandlerFillCount,
										choiceMinimalReduceHandlerFillCount,
										choiceMaximalReduceHandlerFillCount,
										groupMinimalReduceHandlerFillCount,
										groupMaximalReduceHandlerFillCount,
										interleaveMinimalReduceHandlerFillCount,
										interleaveMaximalReduceHandlerFillCount);
	}
	
	synchronized void recycle(int particleHandlerRecycledCount,
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
	    if(particleHandlerEffectivellyUsed == 0){
	        int neededLength = particleHandlerFree + particleHandlerRecycledCount; 
            if(neededLength > particleHandler.length){
                if(neededLength > particleHandlerMaxSize){
                    neededLength = particleHandlerMaxSize;
                    ParticleHandler[] increased = new ParticleHandler[neededLength];
                    System.arraycopy(particleHandler, 0, increased, 0, particleHandler.length);
                    particleHandler = increased;		        
                    System.arraycopy(particleHandlerRecycled, 0, particleHandler, particleHandlerFree, particleHandlerMaxSize - particleHandlerFree);
                    particleHandlerFree = particleHandlerMaxSize; 
                }else{
                    ParticleHandler[] increased = new ParticleHandler[neededLength];
                    System.arraycopy(particleHandler, 0, increased, 0, particleHandler.length);
                    particleHandler = increased;
                    System.arraycopy(particleHandlerRecycled, 0, particleHandler, particleHandlerFree, particleHandlerRecycledCount);
                    particleHandlerFree += particleHandlerRecycledCount;
                }
            }else{
                System.arraycopy(particleHandlerRecycled, 0, particleHandler, particleHandlerFree, particleHandlerRecycledCount);
                particleHandlerFree += particleHandlerRecycledCount;
            }
                        
            for(int i = 0; i < particleHandlerRecycled.length; i++){
                particleHandlerRecycled[i] = null;
            }		
            
            
            neededLength = choiceHandlerFree + choiceHandlerRecycledCount; 
            if(neededLength > choiceHandler.length){
                if(neededLength > choiceHandlerMaxSize){
                    neededLength = choiceHandlerMaxSize;
                    ChoiceHandler[] increased = new ChoiceHandler[neededLength];
                    System.arraycopy(choiceHandler, 0, increased, 0, choiceHandler.length);
                    choiceHandler = increased;		        
                    System.arraycopy(choiceHandlerRecycled, 0, choiceHandler, choiceHandlerFree, choiceHandlerMaxSize - choiceHandlerFree);
                    choiceHandlerFree = choiceHandlerMaxSize; 
                }else{
                    ChoiceHandler[] increased = new ChoiceHandler[neededLength];
                    System.arraycopy(choiceHandler, 0, increased, 0, choiceHandler.length);
                    choiceHandler = increased;
                    System.arraycopy(choiceHandlerRecycled, 0, choiceHandler, choiceHandlerFree, choiceHandlerRecycledCount);
                    choiceHandlerFree += choiceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(choiceHandlerRecycled, 0, choiceHandler, choiceHandlerFree, choiceHandlerRecycledCount);
                choiceHandlerFree += choiceHandlerRecycledCount;
            }
            
            for(int i = 0; i < choiceHandlerRecycled.length; i++){
                choiceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = groupHandlerFree + groupHandlerRecycledCount; 
            if(neededLength > groupHandler.length){
                if(neededLength > groupHandlerMaxSize){
                    neededLength = groupHandlerMaxSize;
                    GroupHandler[] increased = new GroupHandler[neededLength];
                    System.arraycopy(groupHandler, 0, increased, 0, groupHandler.length);
                    groupHandler = increased;		        
                    System.arraycopy(groupHandlerRecycled, 0, groupHandler, groupHandlerFree, groupHandlerMaxSize - groupHandlerFree);
                    groupHandlerFree = groupHandlerMaxSize; 
                }else{
                    GroupHandler[] increased = new GroupHandler[neededLength];
                    System.arraycopy(groupHandler, 0, increased, 0, groupHandler.length);
                    groupHandler = increased;
                    System.arraycopy(groupHandlerRecycled, 0, groupHandler, groupHandlerFree, groupHandlerRecycledCount);
                    groupHandlerFree += groupHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupHandlerRecycled, 0, groupHandler, groupHandlerFree, groupHandlerRecycledCount);
                groupHandlerFree += groupHandlerRecycledCount;
            }
            
            for(int i = 0; i < groupHandlerRecycled.length; i++){
                groupHandlerRecycled[i] = null;
            }	
    
            
            
            
            neededLength = uinterleaveHandlerFree + uinterleaveHandlerRecycledCount; 
            if(neededLength > uinterleaveHandler.length){
                if(neededLength > uinterleaveHandlerMaxSize){
                    neededLength = uinterleaveHandlerMaxSize;
                    UInterleaveHandler[] increased = new UInterleaveHandler[neededLength];
                    System.arraycopy(uinterleaveHandler, 0, increased, 0, uinterleaveHandler.length);
                    uinterleaveHandler = increased;		        
                    System.arraycopy(uinterleaveHandlerRecycled, 0, uinterleaveHandler, uinterleaveHandlerFree, uinterleaveHandlerMaxSize - uinterleaveHandlerFree);
                    uinterleaveHandlerFree = uinterleaveHandlerMaxSize; 
                }else{
                    UInterleaveHandler[] increased = new UInterleaveHandler[neededLength];
                    System.arraycopy(uinterleaveHandler, 0, increased, 0, uinterleaveHandler.length);
                    uinterleaveHandler = increased;
                    System.arraycopy(uinterleaveHandlerRecycled, 0, uinterleaveHandler, uinterleaveHandlerFree, uinterleaveHandlerRecycledCount);
                    uinterleaveHandlerFree += uinterleaveHandlerRecycledCount;
                }
            }else{
                System.arraycopy(uinterleaveHandlerRecycled, 0, uinterleaveHandler, uinterleaveHandlerFree, uinterleaveHandlerRecycledCount);
                uinterleaveHandlerFree += uinterleaveHandlerRecycledCount;
            }
            
            for(int i = 0; i < uinterleaveHandlerRecycled.length; i++){
                uinterleaveHandlerRecycled[i] = null;
            }	
            
            
            neededLength = minterleaveHandlerFree + minterleaveHandlerRecycledCount; 
            if(neededLength > minterleaveHandler.length){
                if(neededLength > minterleaveHandlerMaxSize){
                    neededLength = minterleaveHandlerMaxSize;
                    MInterleaveHandler[] increased = new MInterleaveHandler[neededLength];
                    System.arraycopy(minterleaveHandler, 0, increased, 0, minterleaveHandler.length);
                    minterleaveHandler = increased;		        
                    System.arraycopy(minterleaveHandlerRecycled, 0, minterleaveHandler, minterleaveHandlerFree, minterleaveHandlerMaxSize - minterleaveHandlerFree);
                    minterleaveHandlerFree = minterleaveHandlerMaxSize; 
                }else{
                    MInterleaveHandler[] increased = new MInterleaveHandler[neededLength];
                    System.arraycopy(minterleaveHandler, 0, increased, 0, minterleaveHandler.length);
                    minterleaveHandler = increased;
                    System.arraycopy(minterleaveHandlerRecycled, 0, minterleaveHandler, minterleaveHandlerFree, minterleaveHandlerRecycledCount);
                    minterleaveHandlerFree += minterleaveHandlerRecycledCount;
                }
            }else{
                System.arraycopy(minterleaveHandlerRecycled, 0, minterleaveHandler, minterleaveHandlerFree, minterleaveHandlerRecycledCount);
                minterleaveHandlerFree += minterleaveHandlerRecycledCount;
            }
            
            for(int i = 0; i < minterleaveHandlerRecycled.length; i++){
                minterleaveHandlerRecycled[i] = null;
            }	
            
            neededLength = sinterleaveHandlerFree + sinterleaveHandlerRecycledCount; 
            if(neededLength > sinterleaveHandler.length){
                if(neededLength > sinterleaveHandlerMaxSize){
                    neededLength = sinterleaveHandlerMaxSize;
                    SInterleaveHandler[] increased = new SInterleaveHandler[neededLength];
                    System.arraycopy(sinterleaveHandler, 0, increased, 0, sinterleaveHandler.length);
                    sinterleaveHandler = increased;		        
                    System.arraycopy(sinterleaveHandlerRecycled, 0, sinterleaveHandler, sinterleaveHandlerFree, sinterleaveHandlerMaxSize - sinterleaveHandlerFree);
                    sinterleaveHandlerFree = sinterleaveHandlerMaxSize; 
                }else{
                    SInterleaveHandler[] increased = new SInterleaveHandler[neededLength];
                    System.arraycopy(sinterleaveHandler, 0, increased, 0, sinterleaveHandler.length);
                    sinterleaveHandler = increased;
                    System.arraycopy(sinterleaveHandlerRecycled, 0, sinterleaveHandler, sinterleaveHandlerFree, sinterleaveHandlerRecycledCount);
                    sinterleaveHandlerFree += sinterleaveHandlerRecycledCount;
                }
            }else{
                System.arraycopy(sinterleaveHandlerRecycled, 0, sinterleaveHandler, sinterleaveHandlerFree, sinterleaveHandlerRecycledCount);
                sinterleaveHandlerFree += sinterleaveHandlerRecycledCount;
            }
            
            for(int i = 0; i < sinterleaveHandlerRecycled.length; i++){
                sinterleaveHandlerRecycled[i] = null;
            }	
            
            neededLength = intermediaryPatternHandlerFree + intermediaryPatternHandlerRecycledCount; 
            if(neededLength > intermediaryPatternHandler.length){
                if(neededLength > intermediaryPatternHandlerMaxSize){
                    neededLength = intermediaryPatternHandlerMaxSize;
                    IntermediaryPatternHandler[] increased = new IntermediaryPatternHandler[neededLength];
                    System.arraycopy(intermediaryPatternHandler, 0, increased, 0, intermediaryPatternHandler.length);
                    intermediaryPatternHandler = increased;		        
                    System.arraycopy(intermediaryPatternHandlerRecycled, 0, intermediaryPatternHandler, intermediaryPatternHandlerFree, intermediaryPatternHandlerMaxSize - intermediaryPatternHandlerFree);
                    intermediaryPatternHandlerFree = intermediaryPatternHandlerMaxSize; 
                }else{
                    IntermediaryPatternHandler[] increased = new IntermediaryPatternHandler[neededLength];
                    System.arraycopy(intermediaryPatternHandler, 0, increased, 0, intermediaryPatternHandler.length);
                    intermediaryPatternHandler = increased;
                    System.arraycopy(intermediaryPatternHandlerRecycled, 0, intermediaryPatternHandler, intermediaryPatternHandlerFree, intermediaryPatternHandlerRecycledCount);
                    intermediaryPatternHandlerFree += intermediaryPatternHandlerRecycledCount;
                }
            }else{
                System.arraycopy(intermediaryPatternHandlerRecycled, 0, intermediaryPatternHandler, intermediaryPatternHandlerFree, intermediaryPatternHandlerRecycledCount);
                intermediaryPatternHandlerFree += intermediaryPatternHandlerRecycledCount;
            }
            
            for(int i = 0; i < intermediaryPatternHandlerRecycled.length; i++){
                intermediaryPatternHandlerRecycled[i] = null;
            }	
                        	
            
            
            neededLength = typeHandlerFree + typeHandlerRecycledCount; 
            if(neededLength > typeHandler.length){
                if(neededLength > typeHandlerMaxSize){
                    neededLength = typeHandlerMaxSize;
                    TypeHandler[] increased = new TypeHandler[neededLength];
                    System.arraycopy(typeHandler, 0, increased, 0, typeHandler.length);
                    typeHandler = increased;		        
                    System.arraycopy(typeHandlerRecycled, 0, typeHandler, typeHandlerFree, typeHandlerMaxSize - typeHandlerFree);
                    typeHandlerFree = typeHandlerMaxSize; 
                }else{
                    TypeHandler[] increased = new TypeHandler[neededLength];
                    System.arraycopy(typeHandler, 0, increased, 0, typeHandler.length);
                    typeHandler = increased;
                    System.arraycopy(typeHandlerRecycled, 0, typeHandler, typeHandlerFree, typeHandlerRecycledCount);
                    typeHandlerFree += typeHandlerRecycledCount;
                }
            }else{
                System.arraycopy(typeHandlerRecycled, 0, typeHandler, typeHandlerFree, typeHandlerRecycledCount);
                typeHandlerFree += typeHandlerRecycledCount;
            }
            
            for(int i = 0; i < typeHandlerRecycled.length; i++){
                typeHandlerRecycled[i] = null;
            }	
            
            
            
            
            
            
            neededLength = groupDoubleHandlerFree + groupDoubleHandlerRecycledCount; 
            if(neededLength > groupDoubleHandler.length){
                if(neededLength > groupDoubleHandlerMaxSize){
                    neededLength = groupDoubleHandlerMaxSize;
                    GroupDoubleHandler[] increased = new GroupDoubleHandler[neededLength];
                    System.arraycopy(groupDoubleHandler, 0, increased, 0, groupDoubleHandler.length);
                    groupDoubleHandler = increased;		        
                    System.arraycopy(groupDoubleHandlerRecycled, 0, groupDoubleHandler, groupDoubleHandlerFree, groupDoubleHandlerMaxSize - groupDoubleHandlerFree);
                    groupDoubleHandlerFree = groupDoubleHandlerMaxSize; 
                }else{
                    GroupDoubleHandler[] increased = new GroupDoubleHandler[neededLength];
                    System.arraycopy(groupDoubleHandler, 0, increased, 0, groupDoubleHandler.length);
                    groupDoubleHandler = increased;
                    System.arraycopy(groupDoubleHandlerRecycled, 0, groupDoubleHandler, groupDoubleHandlerFree, groupDoubleHandlerRecycledCount);
                    groupDoubleHandlerFree += groupDoubleHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupDoubleHandlerRecycled, 0, groupDoubleHandler, groupDoubleHandlerFree, groupDoubleHandlerRecycledCount);
                groupDoubleHandlerFree += groupDoubleHandlerRecycledCount;
            }
            
            for(int i = 0; i < groupDoubleHandlerRecycled.length; i++){
                groupDoubleHandlerRecycled[i] = null;
            }	
            
            
            neededLength = interleaveDoubleHandlerFree + interleaveDoubleHandlerRecycledCount; 
            if(neededLength > interleaveDoubleHandler.length){
                if(neededLength > interleaveDoubleHandlerMaxSize){
                    neededLength = interleaveDoubleHandlerMaxSize;
                    InterleaveDoubleHandler[] increased = new InterleaveDoubleHandler[neededLength];
                    System.arraycopy(interleaveDoubleHandler, 0, increased, 0, interleaveDoubleHandler.length);
                    interleaveDoubleHandler = increased;		        
                    System.arraycopy(interleaveDoubleHandlerRecycled, 0, interleaveDoubleHandler, interleaveDoubleHandlerFree, interleaveDoubleHandlerMaxSize - interleaveDoubleHandlerFree);
                    interleaveDoubleHandlerFree = interleaveDoubleHandlerMaxSize; 
                }else{
                    InterleaveDoubleHandler[] increased = new InterleaveDoubleHandler[neededLength];
                    System.arraycopy(interleaveDoubleHandler, 0, increased, 0, interleaveDoubleHandler.length);
                    interleaveDoubleHandler = increased;
                    System.arraycopy(interleaveDoubleHandlerRecycled, 0, interleaveDoubleHandler, interleaveDoubleHandlerFree, interleaveDoubleHandlerRecycledCount);
                    interleaveDoubleHandlerFree += interleaveDoubleHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveDoubleHandlerRecycled, 0, interleaveDoubleHandler, interleaveDoubleHandlerFree, interleaveDoubleHandlerRecycledCount);
                interleaveDoubleHandlerFree += interleaveDoubleHandlerRecycledCount;
            }
            
            for(int i = 0; i < interleaveDoubleHandlerRecycled.length; i++){
                interleaveDoubleHandlerRecycled[i] = null;
            }	
            
            
            
            
            neededLength = groupMinimalReduceCountHandlerFree + groupMinimalReduceCountHandlerRecycledCount; 
            if(neededLength > groupMinimalReduceCountHandler.length){
                if(neededLength > groupMinimalReduceCountHandlerMaxSize){
                    neededLength = groupMinimalReduceCountHandlerMaxSize;
                    GroupMinimalReduceCountHandler[] increased = new GroupMinimalReduceCountHandler[neededLength];
                    System.arraycopy(groupMinimalReduceCountHandler, 0, increased, 0, groupMinimalReduceCountHandler.length);
                    groupMinimalReduceCountHandler = increased;		        
                    System.arraycopy(groupMinimalReduceCountHandlerRecycled, 0, groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, groupMinimalReduceCountHandlerMaxSize - groupMinimalReduceCountHandlerFree);
                    groupMinimalReduceCountHandlerFree = groupMinimalReduceCountHandlerMaxSize; 
                }else{
                    GroupMinimalReduceCountHandler[] increased = new GroupMinimalReduceCountHandler[neededLength];
                    System.arraycopy(groupMinimalReduceCountHandler, 0, increased, 0, groupMinimalReduceCountHandler.length);
                    groupMinimalReduceCountHandler = increased;
                    System.arraycopy(groupMinimalReduceCountHandlerRecycled, 0, groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, groupMinimalReduceCountHandlerRecycledCount);
                    groupMinimalReduceCountHandlerFree += groupMinimalReduceCountHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupMinimalReduceCountHandlerRecycled, 0, groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, groupMinimalReduceCountHandlerRecycledCount);
                groupMinimalReduceCountHandlerFree += groupMinimalReduceCountHandlerRecycledCount;
            }
            
            for(int i = 0; i < groupMinimalReduceCountHandlerRecycled.length; i++){
                groupMinimalReduceCountHandlerRecycled[i] = null;
            }	
            
            
            neededLength = groupMaximalReduceCountHandlerFree + groupMaximalReduceCountHandlerRecycledCount; 
            if(neededLength > groupMaximalReduceCountHandler.length){
                if(neededLength > groupMaximalReduceCountHandlerMaxSize){
                    neededLength = groupMaximalReduceCountHandlerMaxSize;
                    GroupMaximalReduceCountHandler[] increased = new GroupMaximalReduceCountHandler[neededLength];
                    System.arraycopy(groupMaximalReduceCountHandler, 0, increased, 0, groupMaximalReduceCountHandler.length);
                    groupMaximalReduceCountHandler = increased;		        
                    System.arraycopy(groupMaximalReduceCountHandlerRecycled, 0, groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, groupMaximalReduceCountHandlerMaxSize - groupMaximalReduceCountHandlerFree);
                    groupMaximalReduceCountHandlerFree = groupMaximalReduceCountHandlerMaxSize; 
                }else{
                    GroupMaximalReduceCountHandler[] increased = new GroupMaximalReduceCountHandler[neededLength];
                    System.arraycopy(groupMaximalReduceCountHandler, 0, increased, 0, groupMaximalReduceCountHandler.length);
                    groupMaximalReduceCountHandler = increased;
                    System.arraycopy(groupMaximalReduceCountHandlerRecycled, 0, groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, groupMaximalReduceCountHandlerRecycledCount);
                    groupMaximalReduceCountHandlerFree += groupMaximalReduceCountHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupMaximalReduceCountHandlerRecycled, 0, groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, groupMaximalReduceCountHandlerRecycledCount);
                groupMaximalReduceCountHandlerFree += groupMaximalReduceCountHandlerRecycledCount;
            }
            
            for(int i = 0; i < groupMaximalReduceCountHandlerRecycled.length; i++){
                groupMaximalReduceCountHandlerRecycled[i] = null;
            }	
            
            neededLength = interleaveMinimalReduceCountHandlerFree + interleaveMinimalReduceCountHandlerRecycledCount; 
            if(neededLength > interleaveMinimalReduceCountHandler.length){
                if(neededLength > interleaveMinimalReduceCountHandlerMaxSize){
                    neededLength = interleaveMinimalReduceCountHandlerMaxSize;
                    InterleaveMinimalReduceCountHandler[] increased = new InterleaveMinimalReduceCountHandler[neededLength];
                    System.arraycopy(interleaveMinimalReduceCountHandler, 0, increased, 0, interleaveMinimalReduceCountHandler.length);
                    interleaveMinimalReduceCountHandler = increased;		        
                    System.arraycopy(interleaveMinimalReduceCountHandlerRecycled, 0, interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, interleaveMinimalReduceCountHandlerMaxSize - interleaveMinimalReduceCountHandlerFree);
                    interleaveMinimalReduceCountHandlerFree = interleaveMinimalReduceCountHandlerMaxSize; 
                }else{
                    InterleaveMinimalReduceCountHandler[] increased = new InterleaveMinimalReduceCountHandler[neededLength];
                    System.arraycopy(interleaveMinimalReduceCountHandler, 0, increased, 0, interleaveMinimalReduceCountHandler.length);
                    interleaveMinimalReduceCountHandler = increased;
                    System.arraycopy(interleaveMinimalReduceCountHandlerRecycled, 0, interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, interleaveMinimalReduceCountHandlerRecycledCount);
                    interleaveMinimalReduceCountHandlerFree += interleaveMinimalReduceCountHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveMinimalReduceCountHandlerRecycled, 0, interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, interleaveMinimalReduceCountHandlerRecycledCount);
                interleaveMinimalReduceCountHandlerFree += interleaveMinimalReduceCountHandlerRecycledCount;
            }
            
            for(int i = 0; i < interleaveMinimalReduceCountHandlerRecycled.length; i++){
                interleaveMinimalReduceCountHandlerRecycled[i] = null;
            }	
            
            neededLength = interleaveMaximalReduceCountHandlerFree + interleaveMaximalReduceCountHandlerRecycledCount; 
            if(neededLength > interleaveMaximalReduceCountHandler.length){
                if(neededLength > interleaveMaximalReduceCountHandlerMaxSize){
                    neededLength = interleaveMaximalReduceCountHandlerMaxSize;
                    InterleaveMaximalReduceCountHandler[] increased = new InterleaveMaximalReduceCountHandler[neededLength];
                    System.arraycopy(interleaveMaximalReduceCountHandler, 0, increased, 0, interleaveMaximalReduceCountHandler.length);
                    interleaveMaximalReduceCountHandler = increased;		        
                    System.arraycopy(interleaveMaximalReduceCountHandlerRecycled, 0, interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, interleaveMaximalReduceCountHandlerMaxSize - interleaveMaximalReduceCountHandlerFree);
                    interleaveMaximalReduceCountHandlerFree = interleaveMaximalReduceCountHandlerMaxSize; 
                }else{
                    InterleaveMaximalReduceCountHandler[] increased = new InterleaveMaximalReduceCountHandler[neededLength];
                    System.arraycopy(interleaveMaximalReduceCountHandler, 0, increased, 0, interleaveMaximalReduceCountHandler.length);
                    interleaveMaximalReduceCountHandler = increased;
                    System.arraycopy(interleaveMaximalReduceCountHandlerRecycled, 0, interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, interleaveMaximalReduceCountHandlerRecycledCount);
                    interleaveMaximalReduceCountHandlerFree += interleaveMaximalReduceCountHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveMaximalReduceCountHandlerRecycled, 0, interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, interleaveMaximalReduceCountHandlerRecycledCount);
                interleaveMaximalReduceCountHandlerFree += interleaveMaximalReduceCountHandlerRecycledCount;
            }
            
            for(int i = 0; i < interleaveMaximalReduceCountHandlerRecycled.length; i++){
                interleaveMaximalReduceCountHandlerRecycled[i] = null;
            }	
            
            
            
            
            
            
            neededLength = intermediaryPatternMinimalReduceHandlerFree + intermediaryPatternMinimalReduceHandlerRecycledCount; 
            if(neededLength > intermediaryPatternMinimalReduceHandler.length){
                if(neededLength > intermediaryPatternMinimalReduceHandlerMaxSize){
                    neededLength = intermediaryPatternMinimalReduceHandlerMaxSize;
                    IntermediaryPatternMinimalReduceHandler[] increased = new IntermediaryPatternMinimalReduceHandler[neededLength];
                    System.arraycopy(intermediaryPatternMinimalReduceHandler, 0, increased, 0, intermediaryPatternMinimalReduceHandler.length);
                    intermediaryPatternMinimalReduceHandler = increased;		        
                    System.arraycopy(intermediaryPatternMinimalReduceHandlerRecycled, 0, intermediaryPatternMinimalReduceHandler, intermediaryPatternMinimalReduceHandlerFree, intermediaryPatternMinimalReduceHandlerMaxSize - intermediaryPatternMinimalReduceHandlerFree);
                    intermediaryPatternMinimalReduceHandlerFree = intermediaryPatternMinimalReduceHandlerMaxSize; 
                }else{
                    IntermediaryPatternMinimalReduceHandler[] increased = new IntermediaryPatternMinimalReduceHandler[neededLength];
                    System.arraycopy(intermediaryPatternMinimalReduceHandler, 0, increased, 0, intermediaryPatternMinimalReduceHandler.length);
                    intermediaryPatternMinimalReduceHandler = increased;
                    System.arraycopy(intermediaryPatternMinimalReduceHandlerRecycled, 0, intermediaryPatternMinimalReduceHandler, intermediaryPatternMinimalReduceHandlerFree, intermediaryPatternMinimalReduceHandlerRecycledCount);
                    intermediaryPatternMinimalReduceHandlerFree += intermediaryPatternMinimalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(intermediaryPatternMinimalReduceHandlerRecycled, 0, intermediaryPatternMinimalReduceHandler, intermediaryPatternMinimalReduceHandlerFree, intermediaryPatternMinimalReduceHandlerRecycledCount);
                intermediaryPatternMinimalReduceHandlerFree += intermediaryPatternMinimalReduceHandlerRecycledCount;
            }
            
            for(int i = 0; i < intermediaryPatternMinimalReduceHandlerRecycled.length; i++){
                intermediaryPatternMinimalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = intermediaryPatternMaximalReduceHandlerFree + intermediaryPatternMaximalReduceHandlerRecycledCount; 
            if(neededLength > intermediaryPatternMaximalReduceHandler.length){
                if(neededLength > intermediaryPatternMaximalReduceHandlerMaxSize){
                    neededLength = intermediaryPatternMaximalReduceHandlerMaxSize;
                    IntermediaryPatternMaximalReduceHandler[] increased = new IntermediaryPatternMaximalReduceHandler[neededLength];
                    System.arraycopy(intermediaryPatternMaximalReduceHandler, 0, increased, 0, intermediaryPatternMaximalReduceHandler.length);
                    intermediaryPatternMaximalReduceHandler = increased;		        
                    System.arraycopy(intermediaryPatternMaximalReduceHandlerRecycled, 0, intermediaryPatternMaximalReduceHandler, intermediaryPatternMaximalReduceHandlerFree, intermediaryPatternMaximalReduceHandlerMaxSize - intermediaryPatternMaximalReduceHandlerFree);
                    intermediaryPatternMaximalReduceHandlerFree = intermediaryPatternMaximalReduceHandlerMaxSize; 
                }else{
                    IntermediaryPatternMaximalReduceHandler[] increased = new IntermediaryPatternMaximalReduceHandler[neededLength];
                    System.arraycopy(intermediaryPatternMaximalReduceHandler, 0, increased, 0, intermediaryPatternMaximalReduceHandler.length);
                    intermediaryPatternMaximalReduceHandler = increased;
                    System.arraycopy(intermediaryPatternMaximalReduceHandlerRecycled, 0, intermediaryPatternMaximalReduceHandler, intermediaryPatternMaximalReduceHandlerFree, intermediaryPatternMaximalReduceHandlerRecycledCount);
                    intermediaryPatternMaximalReduceHandlerFree += intermediaryPatternMaximalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(intermediaryPatternMaximalReduceHandlerRecycled, 0, intermediaryPatternMaximalReduceHandler, intermediaryPatternMaximalReduceHandlerFree, intermediaryPatternMaximalReduceHandlerRecycledCount);
                intermediaryPatternMaximalReduceHandlerFree += intermediaryPatternMaximalReduceHandlerRecycledCount;
            }
            
            for(int i = 0; i < intermediaryPatternMaximalReduceHandlerRecycled.length; i++){
                intermediaryPatternMaximalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = choiceMinimalReduceHandlerFree + choiceMinimalReduceHandlerRecycledCount; 
            if(neededLength > choiceMinimalReduceHandler.length){
                if(neededLength > choiceMinimalReduceHandlerMaxSize){
                    neededLength = choiceMinimalReduceHandlerMaxSize;
                    ChoiceMinimalReduceHandler[] increased = new ChoiceMinimalReduceHandler[neededLength];
                    System.arraycopy(choiceMinimalReduceHandler, 0, increased, 0, choiceMinimalReduceHandler.length);
                    choiceMinimalReduceHandler = increased;		        
                    System.arraycopy(choiceMinimalReduceHandlerRecycled, 0, choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, choiceMinimalReduceHandlerMaxSize - choiceMinimalReduceHandlerFree);
                    choiceMinimalReduceHandlerFree = choiceMinimalReduceHandlerMaxSize; 
                }else{
                    ChoiceMinimalReduceHandler[] increased = new ChoiceMinimalReduceHandler[neededLength];
                    System.arraycopy(choiceMinimalReduceHandler, 0, increased, 0, choiceMinimalReduceHandler.length);
                    choiceMinimalReduceHandler = increased;
                    System.arraycopy(choiceMinimalReduceHandlerRecycled, 0, choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, choiceMinimalReduceHandlerRecycledCount);
                    choiceMinimalReduceHandlerFree += choiceMinimalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(choiceMinimalReduceHandlerRecycled, 0, choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, choiceMinimalReduceHandlerRecycledCount);
                choiceMinimalReduceHandlerFree += choiceMinimalReduceHandlerRecycledCount;
            }
            
            for(int i = 0; i < choiceMinimalReduceHandlerRecycled.length; i++){
                choiceMinimalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = choiceMaximalReduceHandlerFree + choiceMaximalReduceHandlerRecycledCount; 
            if(neededLength > choiceMaximalReduceHandler.length){
                if(neededLength > choiceMaximalReduceHandlerMaxSize){
                    neededLength = choiceMaximalReduceHandlerMaxSize;
                    ChoiceMaximalReduceHandler[] increased = new ChoiceMaximalReduceHandler[neededLength];
                    System.arraycopy(choiceMaximalReduceHandler, 0, increased, 0, choiceMaximalReduceHandler.length);
                    choiceMaximalReduceHandler = increased;		        
                    System.arraycopy(choiceMaximalReduceHandlerRecycled, 0, choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, choiceMaximalReduceHandlerMaxSize - choiceMaximalReduceHandlerFree);
                    choiceMaximalReduceHandlerFree = choiceMaximalReduceHandlerMaxSize; 
                }else{
                    ChoiceMaximalReduceHandler[] increased = new ChoiceMaximalReduceHandler[neededLength];
                    System.arraycopy(choiceMaximalReduceHandler, 0, increased, 0, choiceMaximalReduceHandler.length);
                    choiceMaximalReduceHandler = increased;
                    System.arraycopy(choiceMaximalReduceHandlerRecycled, 0, choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, choiceMaximalReduceHandlerRecycledCount);
                    choiceMaximalReduceHandlerFree += choiceMaximalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(choiceMaximalReduceHandlerRecycled, 0, choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, choiceMaximalReduceHandlerRecycledCount);
                choiceMaximalReduceHandlerFree += choiceMaximalReduceHandlerRecycledCount;
            }
            
            for(int i = 0; i < choiceMaximalReduceHandlerRecycled.length; i++){
                choiceMaximalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = groupMinimalReduceHandlerFree + groupMinimalReduceHandlerRecycledCount; 
            if(neededLength > groupMinimalReduceHandler.length){
                if(neededLength > groupMinimalReduceHandlerMaxSize){
                    neededLength = groupMinimalReduceHandlerMaxSize;
                    GroupMinimalReduceHandler[] increased = new GroupMinimalReduceHandler[neededLength];
                    System.arraycopy(groupMinimalReduceHandler, 0, increased, 0, groupMinimalReduceHandler.length);
                    groupMinimalReduceHandler = increased;		        
                    System.arraycopy(groupMinimalReduceHandlerRecycled, 0, groupMinimalReduceHandler, groupMinimalReduceHandlerFree, groupMinimalReduceHandlerMaxSize - groupMinimalReduceHandlerFree);
                    groupMinimalReduceHandlerFree = groupMinimalReduceHandlerMaxSize; 
                }else{
                    GroupMinimalReduceHandler[] increased = new GroupMinimalReduceHandler[neededLength];
                    System.arraycopy(groupMinimalReduceHandler, 0, increased, 0, groupMinimalReduceHandler.length);
                    groupMinimalReduceHandler = increased;
                    System.arraycopy(groupMinimalReduceHandlerRecycled, 0, groupMinimalReduceHandler, groupMinimalReduceHandlerFree, groupMinimalReduceHandlerRecycledCount);
                    groupMinimalReduceHandlerFree += groupMinimalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupMinimalReduceHandlerRecycled, 0, groupMinimalReduceHandler, groupMinimalReduceHandlerFree, groupMinimalReduceHandlerRecycledCount);
                groupMinimalReduceHandlerFree += groupMinimalReduceHandlerRecycledCount;
            }
            
            for(int i = 0; i < groupMinimalReduceHandlerRecycled.length; i++){
                groupMinimalReduceHandlerRecycled[i] = null;
            }	
            
                    
            neededLength = groupMaximalReduceHandlerFree + groupMaximalReduceHandlerRecycledCount; 
            if(neededLength > groupMaximalReduceHandler.length){
                if(neededLength > groupMaximalReduceHandlerMaxSize){
                    neededLength = groupMaximalReduceHandlerMaxSize;
                    GroupMaximalReduceHandler[] increased = new GroupMaximalReduceHandler[neededLength];
                    System.arraycopy(groupMaximalReduceHandler, 0, increased, 0, groupMaximalReduceHandler.length);
                    groupMaximalReduceHandler = increased;		        
                    System.arraycopy(groupMaximalReduceHandlerRecycled, 0, groupMaximalReduceHandler, groupMaximalReduceHandlerFree, groupMaximalReduceHandlerMaxSize - groupMaximalReduceHandlerFree);
                    groupMaximalReduceHandlerFree = groupMaximalReduceHandlerMaxSize; 
                }else{
                    GroupMaximalReduceHandler[] increased = new GroupMaximalReduceHandler[neededLength];
                    System.arraycopy(groupMaximalReduceHandler, 0, increased, 0, groupMaximalReduceHandler.length);
                    groupMaximalReduceHandler = increased;
                    System.arraycopy(groupMaximalReduceHandlerRecycled, 0, groupMaximalReduceHandler, groupMaximalReduceHandlerFree, groupMaximalReduceHandlerRecycledCount);
                    groupMaximalReduceHandlerFree += groupMaximalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupMaximalReduceHandlerRecycled, 0, groupMaximalReduceHandler, groupMaximalReduceHandlerFree, groupMaximalReduceHandlerRecycledCount);
                groupMaximalReduceHandlerFree += groupMaximalReduceHandlerRecycledCount;
            }
            
            for(int i = 0; i < groupMaximalReduceHandlerRecycled.length; i++){
                groupMaximalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = interleaveMinimalReduceHandlerFree + interleaveMinimalReduceHandlerRecycledCount; 
            if(neededLength > interleaveMinimalReduceHandler.length){
                if(neededLength > interleaveMinimalReduceHandlerMaxSize){
                    neededLength = interleaveMinimalReduceHandlerMaxSize;
                    InterleaveMinimalReduceHandler[] increased = new InterleaveMinimalReduceHandler[neededLength];
                    System.arraycopy(interleaveMinimalReduceHandler, 0, increased, 0, interleaveMinimalReduceHandler.length);
                    interleaveMinimalReduceHandler = increased;		        
                    System.arraycopy(interleaveMinimalReduceHandlerRecycled, 0, interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, interleaveMinimalReduceHandlerMaxSize - interleaveMinimalReduceHandlerFree);
                    interleaveMinimalReduceHandlerFree = interleaveMinimalReduceHandlerMaxSize; 
                }else{
                    InterleaveMinimalReduceHandler[] increased = new InterleaveMinimalReduceHandler[neededLength];
                    System.arraycopy(interleaveMinimalReduceHandler, 0, increased, 0, interleaveMinimalReduceHandler.length);
                    interleaveMinimalReduceHandler = increased;
                    System.arraycopy(interleaveMinimalReduceHandlerRecycled, 0, interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, interleaveMinimalReduceHandlerRecycledCount);
                    interleaveMinimalReduceHandlerFree += interleaveMinimalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveMinimalReduceHandlerRecycled, 0, interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, interleaveMinimalReduceHandlerRecycledCount);
                interleaveMinimalReduceHandlerFree += interleaveMinimalReduceHandlerRecycledCount;
            }
            
            for(int i = 0; i < interleaveMinimalReduceHandlerRecycled.length; i++){
                interleaveMinimalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = interleaveMaximalReduceHandlerFree + interleaveMaximalReduceHandlerRecycledCount; 
            if(neededLength > interleaveMaximalReduceHandler.length){
                if(neededLength > interleaveMaximalReduceHandlerMaxSize){
                    neededLength = interleaveMaximalReduceHandlerMaxSize;
                    InterleaveMaximalReduceHandler[] increased = new InterleaveMaximalReduceHandler[neededLength];
                    System.arraycopy(interleaveMaximalReduceHandler, 0, increased, 0, interleaveMaximalReduceHandler.length);
                    interleaveMaximalReduceHandler = increased;		        
                    System.arraycopy(interleaveMaximalReduceHandlerRecycled, 0, interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, interleaveMaximalReduceHandlerMaxSize - interleaveMaximalReduceHandlerFree);
                    interleaveMaximalReduceHandlerFree = interleaveMaximalReduceHandlerMaxSize; 
                }else{
                    InterleaveMaximalReduceHandler[] increased = new InterleaveMaximalReduceHandler[neededLength];
                    System.arraycopy(interleaveMaximalReduceHandler, 0, increased, 0, interleaveMaximalReduceHandler.length);
                    interleaveMaximalReduceHandler = increased;
                    System.arraycopy(interleaveMaximalReduceHandlerRecycled, 0, interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, interleaveMaximalReduceHandlerRecycledCount);
                    interleaveMaximalReduceHandlerFree += interleaveMaximalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveMaximalReduceHandlerRecycled, 0, interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, interleaveMaximalReduceHandlerRecycledCount);
                interleaveMaximalReduceHandlerFree += interleaveMaximalReduceHandlerRecycledCount;
            }
            
            for(int i = 0; i < interleaveMaximalReduceHandlerRecycled.length; i++){
                interleaveMaximalReduceHandlerRecycled[i] = null;
            }
	    }else{
            int neededLength = particleHandlerFree + particleHandlerRecycledCount; 
            if(neededLength > particleHandler.length){
                if(neededLength > particleHandlerMaxSize){
                    neededLength = particleHandlerMaxSize;
                    ParticleHandler[] increased = new ParticleHandler[neededLength];
                    System.arraycopy(particleHandler, 0, increased, 0, particleHandler.length);
                    particleHandler = increased;		        
                    System.arraycopy(particleHandlerRecycled, 0, particleHandler, particleHandlerFree, particleHandlerMaxSize - particleHandlerFree);
                    particleHandlerFree = particleHandlerMaxSize; 
                }else{
                    ParticleHandler[] increased = new ParticleHandler[neededLength];
                    System.arraycopy(particleHandler, 0, increased, 0, particleHandler.length);
                    particleHandler = increased;
                    System.arraycopy(particleHandlerRecycled, 0, particleHandler, particleHandlerFree, particleHandlerRecycledCount);
                    particleHandlerFree += particleHandlerRecycledCount;
                }
            }else{
                System.arraycopy(particleHandlerRecycled, 0, particleHandler, particleHandlerFree, particleHandlerRecycledCount);
                particleHandlerFree += particleHandlerRecycledCount;
            }
            
            if(particleHandlerAverageUse != 0)particleHandlerAverageUse = (particleHandlerAverageUse + particleHandlerEffectivellyUsed)/2;
            else particleHandlerAverageUse = particleHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < particleHandlerRecycled.length; i++){
                particleHandlerRecycled[i] = null;
            }		
            
            
            neededLength = choiceHandlerFree + choiceHandlerRecycledCount; 
            if(neededLength > choiceHandler.length){
                if(neededLength > choiceHandlerMaxSize){
                    neededLength = choiceHandlerMaxSize;
                    ChoiceHandler[] increased = new ChoiceHandler[neededLength];
                    System.arraycopy(choiceHandler, 0, increased, 0, choiceHandler.length);
                    choiceHandler = increased;		        
                    System.arraycopy(choiceHandlerRecycled, 0, choiceHandler, choiceHandlerFree, choiceHandlerMaxSize - choiceHandlerFree);
                    choiceHandlerFree = choiceHandlerMaxSize; 
                }else{
                    ChoiceHandler[] increased = new ChoiceHandler[neededLength];
                    System.arraycopy(choiceHandler, 0, increased, 0, choiceHandler.length);
                    choiceHandler = increased;
                    System.arraycopy(choiceHandlerRecycled, 0, choiceHandler, choiceHandlerFree, choiceHandlerRecycledCount);
                    choiceHandlerFree += choiceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(choiceHandlerRecycled, 0, choiceHandler, choiceHandlerFree, choiceHandlerRecycledCount);
                choiceHandlerFree += choiceHandlerRecycledCount;
            }
            
            if(choiceHandlerAverageUse != 0)choiceHandlerAverageUse = (choiceHandlerAverageUse + choiceHandlerEffectivellyUsed)/2;
            else choiceHandlerAverageUse = choiceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < choiceHandlerRecycled.length; i++){
                choiceHandlerRecycled[i] = null;
            }	            
            
            neededLength = groupHandlerFree + groupHandlerRecycledCount; 
            if(neededLength > groupHandler.length){
                if(neededLength > groupHandlerMaxSize){
                    neededLength = groupHandlerMaxSize;
                    GroupHandler[] increased = new GroupHandler[neededLength];
                    System.arraycopy(groupHandler, 0, increased, 0, groupHandler.length);
                    groupHandler = increased;		        
                    System.arraycopy(groupHandlerRecycled, 0, groupHandler, groupHandlerFree, groupHandlerMaxSize - groupHandlerFree);
                    groupHandlerFree = groupHandlerMaxSize; 
                }else{
                    GroupHandler[] increased = new GroupHandler[neededLength];
                    System.arraycopy(groupHandler, 0, increased, 0, groupHandler.length);
                    groupHandler = increased;
                    System.arraycopy(groupHandlerRecycled, 0, groupHandler, groupHandlerFree, groupHandlerRecycledCount);
                    groupHandlerFree += groupHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupHandlerRecycled, 0, groupHandler, groupHandlerFree, groupHandlerRecycledCount);
                groupHandlerFree += groupHandlerRecycledCount;
            }
            
            if(groupHandlerAverageUse != 0)groupHandlerAverageUse = (groupHandlerAverageUse + groupHandlerEffectivellyUsed)/2;
            else groupHandlerAverageUse = groupHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < groupHandlerRecycled.length; i++){
                groupHandlerRecycled[i] = null;
            }	
    
            
            
            
            neededLength = uinterleaveHandlerFree + uinterleaveHandlerRecycledCount; 
            if(neededLength > uinterleaveHandler.length){
                if(neededLength > uinterleaveHandlerMaxSize){
                    neededLength = uinterleaveHandlerMaxSize;
                    UInterleaveHandler[] increased = new UInterleaveHandler[neededLength];
                    System.arraycopy(uinterleaveHandler, 0, increased, 0, uinterleaveHandler.length);
                    uinterleaveHandler = increased;		        
                    System.arraycopy(uinterleaveHandlerRecycled, 0, uinterleaveHandler, uinterleaveHandlerFree, uinterleaveHandlerMaxSize - uinterleaveHandlerFree);
                    uinterleaveHandlerFree = uinterleaveHandlerMaxSize; 
                }else{
                    UInterleaveHandler[] increased = new UInterleaveHandler[neededLength];
                    System.arraycopy(uinterleaveHandler, 0, increased, 0, uinterleaveHandler.length);
                    uinterleaveHandler = increased;
                    System.arraycopy(uinterleaveHandlerRecycled, 0, uinterleaveHandler, uinterleaveHandlerFree, uinterleaveHandlerRecycledCount);
                    uinterleaveHandlerFree += uinterleaveHandlerRecycledCount;
                }
            }else{
                System.arraycopy(uinterleaveHandlerRecycled, 0, uinterleaveHandler, uinterleaveHandlerFree, uinterleaveHandlerRecycledCount);
                uinterleaveHandlerFree += uinterleaveHandlerRecycledCount;
            }
            
            if(uinterleaveHandlerAverageUse != 0)uinterleaveHandlerAverageUse = (uinterleaveHandlerAverageUse + uinterleaveHandlerEffectivellyUsed)/2;
            else uinterleaveHandlerAverageUse = uinterleaveHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < uinterleaveHandlerRecycled.length; i++){
                uinterleaveHandlerRecycled[i] = null;
            }	
            
            
            neededLength = minterleaveHandlerFree + minterleaveHandlerRecycledCount; 
            if(neededLength > minterleaveHandler.length){
                if(neededLength > minterleaveHandlerMaxSize){
                    neededLength = minterleaveHandlerMaxSize;
                    MInterleaveHandler[] increased = new MInterleaveHandler[neededLength];
                    System.arraycopy(minterleaveHandler, 0, increased, 0, minterleaveHandler.length);
                    minterleaveHandler = increased;		        
                    System.arraycopy(minterleaveHandlerRecycled, 0, minterleaveHandler, minterleaveHandlerFree, minterleaveHandlerMaxSize - minterleaveHandlerFree);
                    minterleaveHandlerFree = minterleaveHandlerMaxSize; 
                }else{
                    MInterleaveHandler[] increased = new MInterleaveHandler[neededLength];
                    System.arraycopy(minterleaveHandler, 0, increased, 0, minterleaveHandler.length);
                    minterleaveHandler = increased;
                    System.arraycopy(minterleaveHandlerRecycled, 0, minterleaveHandler, minterleaveHandlerFree, minterleaveHandlerRecycledCount);
                    minterleaveHandlerFree += minterleaveHandlerRecycledCount;
                }
            }else{
                System.arraycopy(minterleaveHandlerRecycled, 0, minterleaveHandler, minterleaveHandlerFree, minterleaveHandlerRecycledCount);
                minterleaveHandlerFree += minterleaveHandlerRecycledCount;
            }
            
            if(minterleaveHandlerAverageUse != 0)minterleaveHandlerAverageUse = (minterleaveHandlerAverageUse + minterleaveHandlerEffectivellyUsed)/2;
            else minterleaveHandlerAverageUse = minterleaveHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < minterleaveHandlerRecycled.length; i++){
                minterleaveHandlerRecycled[i] = null;
            }	
            
            neededLength = sinterleaveHandlerFree + sinterleaveHandlerRecycledCount; 
            if(neededLength > sinterleaveHandler.length){
                if(neededLength > sinterleaveHandlerMaxSize){
                    neededLength = sinterleaveHandlerMaxSize;
                    SInterleaveHandler[] increased = new SInterleaveHandler[neededLength];
                    System.arraycopy(sinterleaveHandler, 0, increased, 0, sinterleaveHandler.length);
                    sinterleaveHandler = increased;		        
                    System.arraycopy(sinterleaveHandlerRecycled, 0, sinterleaveHandler, sinterleaveHandlerFree, sinterleaveHandlerMaxSize - sinterleaveHandlerFree);
                    sinterleaveHandlerFree = sinterleaveHandlerMaxSize; 
                }else{
                    SInterleaveHandler[] increased = new SInterleaveHandler[neededLength];
                    System.arraycopy(sinterleaveHandler, 0, increased, 0, sinterleaveHandler.length);
                    sinterleaveHandler = increased;
                    System.arraycopy(sinterleaveHandlerRecycled, 0, sinterleaveHandler, sinterleaveHandlerFree, sinterleaveHandlerRecycledCount);
                    sinterleaveHandlerFree += sinterleaveHandlerRecycledCount;
                }
            }else{
                System.arraycopy(sinterleaveHandlerRecycled, 0, sinterleaveHandler, sinterleaveHandlerFree, sinterleaveHandlerRecycledCount);
                sinterleaveHandlerFree += sinterleaveHandlerRecycledCount;
            }
            
            if(sinterleaveHandlerAverageUse != 0)sinterleaveHandlerAverageUse = (sinterleaveHandlerAverageUse + sinterleaveHandlerEffectivellyUsed)/2;
            else sinterleaveHandlerAverageUse = sinterleaveHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < sinterleaveHandlerRecycled.length; i++){
                sinterleaveHandlerRecycled[i] = null;
            }	
            
            neededLength = intermediaryPatternHandlerFree + intermediaryPatternHandlerRecycledCount; 
            if(neededLength > intermediaryPatternHandler.length){
                if(neededLength > intermediaryPatternHandlerMaxSize){
                    neededLength = intermediaryPatternHandlerMaxSize;
                    IntermediaryPatternHandler[] increased = new IntermediaryPatternHandler[neededLength];
                    System.arraycopy(intermediaryPatternHandler, 0, increased, 0, intermediaryPatternHandler.length);
                    intermediaryPatternHandler = increased;		        
                    System.arraycopy(intermediaryPatternHandlerRecycled, 0, intermediaryPatternHandler, intermediaryPatternHandlerFree, intermediaryPatternHandlerMaxSize - intermediaryPatternHandlerFree);
                    intermediaryPatternHandlerFree = intermediaryPatternHandlerMaxSize; 
                }else{
                    IntermediaryPatternHandler[] increased = new IntermediaryPatternHandler[neededLength];
                    System.arraycopy(intermediaryPatternHandler, 0, increased, 0, intermediaryPatternHandler.length);
                    intermediaryPatternHandler = increased;
                    System.arraycopy(intermediaryPatternHandlerRecycled, 0, intermediaryPatternHandler, intermediaryPatternHandlerFree, intermediaryPatternHandlerRecycledCount);
                    intermediaryPatternHandlerFree += intermediaryPatternHandlerRecycledCount;
                }
            }else{
                System.arraycopy(intermediaryPatternHandlerRecycled, 0, intermediaryPatternHandler, intermediaryPatternHandlerFree, intermediaryPatternHandlerRecycledCount);
                intermediaryPatternHandlerFree += intermediaryPatternHandlerRecycledCount;
            }
            
            if(intermediaryPatternHandlerAverageUse != 0)intermediaryPatternHandlerAverageUse = (intermediaryPatternHandlerAverageUse + intermediaryPatternHandlerEffectivellyUsed)/2;
            else intermediaryPatternHandlerAverageUse = intermediaryPatternHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < intermediaryPatternHandlerRecycled.length; i++){
                intermediaryPatternHandlerRecycled[i] = null;
            }	
                        
            
            neededLength = typeHandlerFree + typeHandlerRecycledCount; 
            if(neededLength > typeHandler.length){
                if(neededLength > typeHandlerMaxSize){
                    neededLength = typeHandlerMaxSize;
                    TypeHandler[] increased = new TypeHandler[neededLength];
                    System.arraycopy(typeHandler, 0, increased, 0, typeHandler.length);
                    typeHandler = increased;		        
                    System.arraycopy(typeHandlerRecycled, 0, typeHandler, typeHandlerFree, typeHandlerMaxSize - typeHandlerFree);
                    typeHandlerFree = typeHandlerMaxSize; 
                }else{
                    TypeHandler[] increased = new TypeHandler[neededLength];
                    System.arraycopy(typeHandler, 0, increased, 0, typeHandler.length);
                    typeHandler = increased;
                    System.arraycopy(typeHandlerRecycled, 0, typeHandler, typeHandlerFree, typeHandlerRecycledCount);
                    typeHandlerFree += typeHandlerRecycledCount;
                }
            }else{
                System.arraycopy(typeHandlerRecycled, 0, typeHandler, typeHandlerFree, typeHandlerRecycledCount);
                typeHandlerFree += typeHandlerRecycledCount;
            }
            
            if(typeHandlerAverageUse != 0)typeHandlerAverageUse = (typeHandlerAverageUse + typeHandlerEffectivellyUsed)/2;
            else typeHandlerAverageUse = typeHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < typeHandlerRecycled.length; i++){
                typeHandlerRecycled[i] = null;
            }	
            
            
                        
            
            
            neededLength = groupDoubleHandlerFree + groupDoubleHandlerRecycledCount; 
            if(neededLength > groupDoubleHandler.length){
                if(neededLength > groupDoubleHandlerMaxSize){
                    neededLength = groupDoubleHandlerMaxSize;
                    GroupDoubleHandler[] increased = new GroupDoubleHandler[neededLength];
                    System.arraycopy(groupDoubleHandler, 0, increased, 0, groupDoubleHandler.length);
                    groupDoubleHandler = increased;		        
                    System.arraycopy(groupDoubleHandlerRecycled, 0, groupDoubleHandler, groupDoubleHandlerFree, groupDoubleHandlerMaxSize - groupDoubleHandlerFree);
                    groupDoubleHandlerFree = groupDoubleHandlerMaxSize; 
                }else{
                    GroupDoubleHandler[] increased = new GroupDoubleHandler[neededLength];
                    System.arraycopy(groupDoubleHandler, 0, increased, 0, groupDoubleHandler.length);
                    groupDoubleHandler = increased;
                    System.arraycopy(groupDoubleHandlerRecycled, 0, groupDoubleHandler, groupDoubleHandlerFree, groupDoubleHandlerRecycledCount);
                    groupDoubleHandlerFree += groupDoubleHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupDoubleHandlerRecycled, 0, groupDoubleHandler, groupDoubleHandlerFree, groupDoubleHandlerRecycledCount);
                groupDoubleHandlerFree += groupDoubleHandlerRecycledCount;
            }
            
            if(groupDoubleHandlerAverageUse != 0)groupDoubleHandlerAverageUse = (groupDoubleHandlerAverageUse + groupDoubleHandlerEffectivellyUsed)/2;
            else groupDoubleHandlerAverageUse = groupDoubleHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < groupDoubleHandlerRecycled.length; i++){
                groupDoubleHandlerRecycled[i] = null;
            }	
            
            
            neededLength = interleaveDoubleHandlerFree + interleaveDoubleHandlerRecycledCount; 
            if(neededLength > interleaveDoubleHandler.length){
                if(neededLength > interleaveDoubleHandlerMaxSize){
                    neededLength = interleaveDoubleHandlerMaxSize;
                    InterleaveDoubleHandler[] increased = new InterleaveDoubleHandler[neededLength];
                    System.arraycopy(interleaveDoubleHandler, 0, increased, 0, interleaveDoubleHandler.length);
                    interleaveDoubleHandler = increased;		        
                    System.arraycopy(interleaveDoubleHandlerRecycled, 0, interleaveDoubleHandler, interleaveDoubleHandlerFree, interleaveDoubleHandlerMaxSize - interleaveDoubleHandlerFree);
                    interleaveDoubleHandlerFree = interleaveDoubleHandlerMaxSize; 
                }else{
                    InterleaveDoubleHandler[] increased = new InterleaveDoubleHandler[neededLength];
                    System.arraycopy(interleaveDoubleHandler, 0, increased, 0, interleaveDoubleHandler.length);
                    interleaveDoubleHandler = increased;
                    System.arraycopy(interleaveDoubleHandlerRecycled, 0, interleaveDoubleHandler, interleaveDoubleHandlerFree, interleaveDoubleHandlerRecycledCount);
                    interleaveDoubleHandlerFree += interleaveDoubleHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveDoubleHandlerRecycled, 0, interleaveDoubleHandler, interleaveDoubleHandlerFree, interleaveDoubleHandlerRecycledCount);
                interleaveDoubleHandlerFree += interleaveDoubleHandlerRecycledCount;
            }
            
            if(interleaveDoubleHandlerAverageUse != 0)interleaveDoubleHandlerAverageUse = (interleaveDoubleHandlerAverageUse + interleaveDoubleHandlerEffectivellyUsed)/2;
            else interleaveDoubleHandlerAverageUse = interleaveDoubleHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < interleaveDoubleHandlerRecycled.length; i++){
                interleaveDoubleHandlerRecycled[i] = null;
            }	
            
            
            
            
            neededLength = groupMinimalReduceCountHandlerFree + groupMinimalReduceCountHandlerRecycledCount; 
            if(neededLength > groupMinimalReduceCountHandler.length){
                if(neededLength > groupMinimalReduceCountHandlerMaxSize){
                    neededLength = groupMinimalReduceCountHandlerMaxSize;
                    GroupMinimalReduceCountHandler[] increased = new GroupMinimalReduceCountHandler[neededLength];
                    System.arraycopy(groupMinimalReduceCountHandler, 0, increased, 0, groupMinimalReduceCountHandler.length);
                    groupMinimalReduceCountHandler = increased;		        
                    System.arraycopy(groupMinimalReduceCountHandlerRecycled, 0, groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, groupMinimalReduceCountHandlerMaxSize - groupMinimalReduceCountHandlerFree);
                    groupMinimalReduceCountHandlerFree = groupMinimalReduceCountHandlerMaxSize; 
                }else{
                    GroupMinimalReduceCountHandler[] increased = new GroupMinimalReduceCountHandler[neededLength];
                    System.arraycopy(groupMinimalReduceCountHandler, 0, increased, 0, groupMinimalReduceCountHandler.length);
                    groupMinimalReduceCountHandler = increased;
                    System.arraycopy(groupMinimalReduceCountHandlerRecycled, 0, groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, groupMinimalReduceCountHandlerRecycledCount);
                    groupMinimalReduceCountHandlerFree += groupMinimalReduceCountHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupMinimalReduceCountHandlerRecycled, 0, groupMinimalReduceCountHandler, groupMinimalReduceCountHandlerFree, groupMinimalReduceCountHandlerRecycledCount);
                groupMinimalReduceCountHandlerFree += groupMinimalReduceCountHandlerRecycledCount;
            }
            
            if(groupMinimalReduceCountHandlerAverageUse != 0)groupMinimalReduceCountHandlerAverageUse = (groupMinimalReduceCountHandlerAverageUse + groupMinimalReduceCountHandlerEffectivellyUsed)/2;
            else groupMinimalReduceCountHandlerAverageUse = groupMinimalReduceCountHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < groupMinimalReduceCountHandlerRecycled.length; i++){
                groupMinimalReduceCountHandlerRecycled[i] = null;
            }	
            
            
            neededLength = groupMaximalReduceCountHandlerFree + groupMaximalReduceCountHandlerRecycledCount; 
            if(neededLength > groupMaximalReduceCountHandler.length){
                if(neededLength > groupMaximalReduceCountHandlerMaxSize){
                    neededLength = groupMaximalReduceCountHandlerMaxSize;
                    GroupMaximalReduceCountHandler[] increased = new GroupMaximalReduceCountHandler[neededLength];
                    System.arraycopy(groupMaximalReduceCountHandler, 0, increased, 0, groupMaximalReduceCountHandler.length);
                    groupMaximalReduceCountHandler = increased;		        
                    System.arraycopy(groupMaximalReduceCountHandlerRecycled, 0, groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, groupMaximalReduceCountHandlerMaxSize - groupMaximalReduceCountHandlerFree);
                    groupMaximalReduceCountHandlerFree = groupMaximalReduceCountHandlerMaxSize; 
                }else{
                    GroupMaximalReduceCountHandler[] increased = new GroupMaximalReduceCountHandler[neededLength];
                    System.arraycopy(groupMaximalReduceCountHandler, 0, increased, 0, groupMaximalReduceCountHandler.length);
                    groupMaximalReduceCountHandler = increased;
                    System.arraycopy(groupMaximalReduceCountHandlerRecycled, 0, groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, groupMaximalReduceCountHandlerRecycledCount);
                    groupMaximalReduceCountHandlerFree += groupMaximalReduceCountHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupMaximalReduceCountHandlerRecycled, 0, groupMaximalReduceCountHandler, groupMaximalReduceCountHandlerFree, groupMaximalReduceCountHandlerRecycledCount);
                groupMaximalReduceCountHandlerFree += groupMaximalReduceCountHandlerRecycledCount;
            }
            
            if(groupMaximalReduceCountHandlerAverageUse != 0)groupMaximalReduceCountHandlerAverageUse = (groupMaximalReduceCountHandlerAverageUse + groupMaximalReduceCountHandlerEffectivellyUsed)/2;
            else groupMaximalReduceCountHandlerAverageUse = groupMaximalReduceCountHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < groupMaximalReduceCountHandlerRecycled.length; i++){
                groupMaximalReduceCountHandlerRecycled[i] = null;
            }	
            
            neededLength = interleaveMinimalReduceCountHandlerFree + interleaveMinimalReduceCountHandlerRecycledCount; 
            if(neededLength > interleaveMinimalReduceCountHandler.length){
                if(neededLength > interleaveMinimalReduceCountHandlerMaxSize){
                    neededLength = interleaveMinimalReduceCountHandlerMaxSize;
                    InterleaveMinimalReduceCountHandler[] increased = new InterleaveMinimalReduceCountHandler[neededLength];
                    System.arraycopy(interleaveMinimalReduceCountHandler, 0, increased, 0, interleaveMinimalReduceCountHandler.length);
                    interleaveMinimalReduceCountHandler = increased;		        
                    System.arraycopy(interleaveMinimalReduceCountHandlerRecycled, 0, interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, interleaveMinimalReduceCountHandlerMaxSize - interleaveMinimalReduceCountHandlerFree);
                    interleaveMinimalReduceCountHandlerFree = interleaveMinimalReduceCountHandlerMaxSize; 
                }else{
                    InterleaveMinimalReduceCountHandler[] increased = new InterleaveMinimalReduceCountHandler[neededLength];
                    System.arraycopy(interleaveMinimalReduceCountHandler, 0, increased, 0, interleaveMinimalReduceCountHandler.length);
                    interleaveMinimalReduceCountHandler = increased;
                    System.arraycopy(interleaveMinimalReduceCountHandlerRecycled, 0, interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, interleaveMinimalReduceCountHandlerRecycledCount);
                    interleaveMinimalReduceCountHandlerFree += interleaveMinimalReduceCountHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveMinimalReduceCountHandlerRecycled, 0, interleaveMinimalReduceCountHandler, interleaveMinimalReduceCountHandlerFree, interleaveMinimalReduceCountHandlerRecycledCount);
                interleaveMinimalReduceCountHandlerFree += interleaveMinimalReduceCountHandlerRecycledCount;
            }
            
            if(interleaveMinimalReduceCountHandlerAverageUse != 0)interleaveMinimalReduceCountHandlerAverageUse = (interleaveMinimalReduceCountHandlerAverageUse + interleaveMinimalReduceCountHandlerEffectivellyUsed)/2;
            else interleaveMinimalReduceCountHandlerAverageUse = interleaveMinimalReduceCountHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < interleaveMinimalReduceCountHandlerRecycled.length; i++){
                interleaveMinimalReduceCountHandlerRecycled[i] = null;
            }	
            
            neededLength = interleaveMaximalReduceCountHandlerFree + interleaveMaximalReduceCountHandlerRecycledCount; 
            if(neededLength > interleaveMaximalReduceCountHandler.length){
                if(neededLength > interleaveMaximalReduceCountHandlerMaxSize){
                    neededLength = interleaveMaximalReduceCountHandlerMaxSize;
                    InterleaveMaximalReduceCountHandler[] increased = new InterleaveMaximalReduceCountHandler[neededLength];
                    System.arraycopy(interleaveMaximalReduceCountHandler, 0, increased, 0, interleaveMaximalReduceCountHandler.length);
                    interleaveMaximalReduceCountHandler = increased;		        
                    System.arraycopy(interleaveMaximalReduceCountHandlerRecycled, 0, interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, interleaveMaximalReduceCountHandlerMaxSize - interleaveMaximalReduceCountHandlerFree);
                    interleaveMaximalReduceCountHandlerFree = interleaveMaximalReduceCountHandlerMaxSize; 
                }else{
                    InterleaveMaximalReduceCountHandler[] increased = new InterleaveMaximalReduceCountHandler[neededLength];
                    System.arraycopy(interleaveMaximalReduceCountHandler, 0, increased, 0, interleaveMaximalReduceCountHandler.length);
                    interleaveMaximalReduceCountHandler = increased;
                    System.arraycopy(interleaveMaximalReduceCountHandlerRecycled, 0, interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, interleaveMaximalReduceCountHandlerRecycledCount);
                    interleaveMaximalReduceCountHandlerFree += interleaveMaximalReduceCountHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveMaximalReduceCountHandlerRecycled, 0, interleaveMaximalReduceCountHandler, interleaveMaximalReduceCountHandlerFree, interleaveMaximalReduceCountHandlerRecycledCount);
                interleaveMaximalReduceCountHandlerFree += interleaveMaximalReduceCountHandlerRecycledCount;
            }
            
            if(interleaveMaximalReduceCountHandlerAverageUse != 0)interleaveMaximalReduceCountHandlerAverageUse = (interleaveMaximalReduceCountHandlerAverageUse + interleaveMaximalReduceCountHandlerEffectivellyUsed)/2;
            else interleaveMaximalReduceCountHandlerAverageUse = interleaveMaximalReduceCountHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < interleaveMaximalReduceCountHandlerRecycled.length; i++){
                interleaveMaximalReduceCountHandlerRecycled[i] = null;
            }	
            
            
            
            
            
            neededLength = intermediaryPatternMinimalReduceHandlerFree + intermediaryPatternMinimalReduceHandlerRecycledCount; 
            if(neededLength > intermediaryPatternMinimalReduceHandler.length){
                if(neededLength > intermediaryPatternMinimalReduceHandlerMaxSize){
                    neededLength = intermediaryPatternMinimalReduceHandlerMaxSize;
                    IntermediaryPatternMinimalReduceHandler[] increased = new IntermediaryPatternMinimalReduceHandler[neededLength];
                    System.arraycopy(intermediaryPatternMinimalReduceHandler, 0, increased, 0, intermediaryPatternMinimalReduceHandler.length);
                    intermediaryPatternMinimalReduceHandler = increased;		        
                    System.arraycopy(intermediaryPatternMinimalReduceHandlerRecycled, 0, intermediaryPatternMinimalReduceHandler, intermediaryPatternMinimalReduceHandlerFree, intermediaryPatternMinimalReduceHandlerMaxSize - intermediaryPatternMinimalReduceHandlerFree);
                    intermediaryPatternMinimalReduceHandlerFree = intermediaryPatternMinimalReduceHandlerMaxSize; 
                }else{
                    IntermediaryPatternMinimalReduceHandler[] increased = new IntermediaryPatternMinimalReduceHandler[neededLength];
                    System.arraycopy(intermediaryPatternMinimalReduceHandler, 0, increased, 0, intermediaryPatternMinimalReduceHandler.length);
                    intermediaryPatternMinimalReduceHandler = increased;
                    System.arraycopy(intermediaryPatternMinimalReduceHandlerRecycled, 0, intermediaryPatternMinimalReduceHandler, intermediaryPatternMinimalReduceHandlerFree, intermediaryPatternMinimalReduceHandlerRecycledCount);
                    intermediaryPatternMinimalReduceHandlerFree += intermediaryPatternMinimalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(intermediaryPatternMinimalReduceHandlerRecycled, 0, intermediaryPatternMinimalReduceHandler, intermediaryPatternMinimalReduceHandlerFree, intermediaryPatternMinimalReduceHandlerRecycledCount);
                intermediaryPatternMinimalReduceHandlerFree += intermediaryPatternMinimalReduceHandlerRecycledCount;
            }
            
            if(intermediaryPatternMinimalReduceHandlerAverageUse != 0)intermediaryPatternMinimalReduceHandlerAverageUse = (intermediaryPatternMinimalReduceHandlerAverageUse + intermediaryPatternMinimalReduceHandlerEffectivellyUsed)/2;
            else intermediaryPatternMinimalReduceHandlerAverageUse = intermediaryPatternMinimalReduceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < intermediaryPatternMinimalReduceHandlerRecycled.length; i++){
                intermediaryPatternMinimalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = intermediaryPatternMaximalReduceHandlerFree + intermediaryPatternMaximalReduceHandlerRecycledCount; 
            if(neededLength > intermediaryPatternMaximalReduceHandler.length){
                if(neededLength > intermediaryPatternMaximalReduceHandlerMaxSize){
                    neededLength = intermediaryPatternMaximalReduceHandlerMaxSize;
                    IntermediaryPatternMaximalReduceHandler[] increased = new IntermediaryPatternMaximalReduceHandler[neededLength];
                    System.arraycopy(intermediaryPatternMaximalReduceHandler, 0, increased, 0, intermediaryPatternMaximalReduceHandler.length);
                    intermediaryPatternMaximalReduceHandler = increased;		        
                    System.arraycopy(intermediaryPatternMaximalReduceHandlerRecycled, 0, intermediaryPatternMaximalReduceHandler, intermediaryPatternMaximalReduceHandlerFree, intermediaryPatternMaximalReduceHandlerMaxSize - intermediaryPatternMaximalReduceHandlerFree);
                    intermediaryPatternMaximalReduceHandlerFree = intermediaryPatternMaximalReduceHandlerMaxSize; 
                }else{
                    IntermediaryPatternMaximalReduceHandler[] increased = new IntermediaryPatternMaximalReduceHandler[neededLength];
                    System.arraycopy(intermediaryPatternMaximalReduceHandler, 0, increased, 0, intermediaryPatternMaximalReduceHandler.length);
                    intermediaryPatternMaximalReduceHandler = increased;
                    System.arraycopy(intermediaryPatternMaximalReduceHandlerRecycled, 0, intermediaryPatternMaximalReduceHandler, intermediaryPatternMaximalReduceHandlerFree, intermediaryPatternMaximalReduceHandlerRecycledCount);
                    intermediaryPatternMaximalReduceHandlerFree += intermediaryPatternMaximalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(intermediaryPatternMaximalReduceHandlerRecycled, 0, intermediaryPatternMaximalReduceHandler, intermediaryPatternMaximalReduceHandlerFree, intermediaryPatternMaximalReduceHandlerRecycledCount);
                intermediaryPatternMaximalReduceHandlerFree += intermediaryPatternMaximalReduceHandlerRecycledCount;
            }
            
            if(intermediaryPatternMaximalReduceHandlerAverageUse != 0)intermediaryPatternMaximalReduceHandlerAverageUse = (intermediaryPatternMaximalReduceHandlerAverageUse + intermediaryPatternMaximalReduceHandlerEffectivellyUsed)/2;
            else intermediaryPatternMaximalReduceHandlerAverageUse = intermediaryPatternMaximalReduceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < intermediaryPatternMaximalReduceHandlerRecycled.length; i++){
                intermediaryPatternMaximalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = choiceMinimalReduceHandlerFree + choiceMinimalReduceHandlerRecycledCount; 
            if(neededLength > choiceMinimalReduceHandler.length){
                if(neededLength > choiceMinimalReduceHandlerMaxSize){
                    neededLength = choiceMinimalReduceHandlerMaxSize;
                    ChoiceMinimalReduceHandler[] increased = new ChoiceMinimalReduceHandler[neededLength];
                    System.arraycopy(choiceMinimalReduceHandler, 0, increased, 0, choiceMinimalReduceHandler.length);
                    choiceMinimalReduceHandler = increased;		        
                    System.arraycopy(choiceMinimalReduceHandlerRecycled, 0, choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, choiceMinimalReduceHandlerMaxSize - choiceMinimalReduceHandlerFree);
                    choiceMinimalReduceHandlerFree = choiceMinimalReduceHandlerMaxSize; 
                }else{
                    ChoiceMinimalReduceHandler[] increased = new ChoiceMinimalReduceHandler[neededLength];
                    System.arraycopy(choiceMinimalReduceHandler, 0, increased, 0, choiceMinimalReduceHandler.length);
                    choiceMinimalReduceHandler = increased;
                    System.arraycopy(choiceMinimalReduceHandlerRecycled, 0, choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, choiceMinimalReduceHandlerRecycledCount);
                    choiceMinimalReduceHandlerFree += choiceMinimalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(choiceMinimalReduceHandlerRecycled, 0, choiceMinimalReduceHandler, choiceMinimalReduceHandlerFree, choiceMinimalReduceHandlerRecycledCount);
                choiceMinimalReduceHandlerFree += choiceMinimalReduceHandlerRecycledCount;
            }
            
            if(choiceMinimalReduceHandlerAverageUse != 0)choiceMinimalReduceHandlerAverageUse = (choiceMinimalReduceHandlerAverageUse + choiceMinimalReduceHandlerEffectivellyUsed)/2;
            else choiceMinimalReduceHandlerAverageUse = choiceMinimalReduceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < choiceMinimalReduceHandlerRecycled.length; i++){
                choiceMinimalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = choiceMaximalReduceHandlerFree + choiceMaximalReduceHandlerRecycledCount; 
            if(neededLength > choiceMaximalReduceHandler.length){
                if(neededLength > choiceMaximalReduceHandlerMaxSize){
                    neededLength = choiceMaximalReduceHandlerMaxSize;
                    ChoiceMaximalReduceHandler[] increased = new ChoiceMaximalReduceHandler[neededLength];
                    System.arraycopy(choiceMaximalReduceHandler, 0, increased, 0, choiceMaximalReduceHandler.length);
                    choiceMaximalReduceHandler = increased;		        
                    System.arraycopy(choiceMaximalReduceHandlerRecycled, 0, choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, choiceMaximalReduceHandlerMaxSize - choiceMaximalReduceHandlerFree);
                    choiceMaximalReduceHandlerFree = choiceMaximalReduceHandlerMaxSize; 
                }else{
                    ChoiceMaximalReduceHandler[] increased = new ChoiceMaximalReduceHandler[neededLength];
                    System.arraycopy(choiceMaximalReduceHandler, 0, increased, 0, choiceMaximalReduceHandler.length);
                    choiceMaximalReduceHandler = increased;
                    System.arraycopy(choiceMaximalReduceHandlerRecycled, 0, choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, choiceMaximalReduceHandlerRecycledCount);
                    choiceMaximalReduceHandlerFree += choiceMaximalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(choiceMaximalReduceHandlerRecycled, 0, choiceMaximalReduceHandler, choiceMaximalReduceHandlerFree, choiceMaximalReduceHandlerRecycledCount);
                choiceMaximalReduceHandlerFree += choiceMaximalReduceHandlerRecycledCount;
            }
            
            if(choiceMaximalReduceHandlerAverageUse != 0)choiceMaximalReduceHandlerAverageUse = (choiceMaximalReduceHandlerAverageUse + choiceMaximalReduceHandlerEffectivellyUsed)/2;
            else choiceMaximalReduceHandlerAverageUse = choiceMaximalReduceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < choiceMaximalReduceHandlerRecycled.length; i++){
                choiceMaximalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = groupMinimalReduceHandlerFree + groupMinimalReduceHandlerRecycledCount; 
            if(neededLength > groupMinimalReduceHandler.length){
                if(neededLength > groupMinimalReduceHandlerMaxSize){
                    neededLength = groupMinimalReduceHandlerMaxSize;
                    GroupMinimalReduceHandler[] increased = new GroupMinimalReduceHandler[neededLength];
                    System.arraycopy(groupMinimalReduceHandler, 0, increased, 0, groupMinimalReduceHandler.length);
                    groupMinimalReduceHandler = increased;		        
                    System.arraycopy(groupMinimalReduceHandlerRecycled, 0, groupMinimalReduceHandler, groupMinimalReduceHandlerFree, groupMinimalReduceHandlerMaxSize - groupMinimalReduceHandlerFree);
                    groupMinimalReduceHandlerFree = groupMinimalReduceHandlerMaxSize; 
                }else{
                    GroupMinimalReduceHandler[] increased = new GroupMinimalReduceHandler[neededLength];
                    System.arraycopy(groupMinimalReduceHandler, 0, increased, 0, groupMinimalReduceHandler.length);
                    groupMinimalReduceHandler = increased;
                    System.arraycopy(groupMinimalReduceHandlerRecycled, 0, groupMinimalReduceHandler, groupMinimalReduceHandlerFree, groupMinimalReduceHandlerRecycledCount);
                    groupMinimalReduceHandlerFree += groupMinimalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupMinimalReduceHandlerRecycled, 0, groupMinimalReduceHandler, groupMinimalReduceHandlerFree, groupMinimalReduceHandlerRecycledCount);
                groupMinimalReduceHandlerFree += groupMinimalReduceHandlerRecycledCount;
            }
            
            if(groupMinimalReduceHandlerAverageUse != 0)groupMinimalReduceHandlerAverageUse = (groupMinimalReduceHandlerAverageUse + groupMinimalReduceHandlerEffectivellyUsed)/2;
            else groupMinimalReduceHandlerAverageUse = groupMinimalReduceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < groupMinimalReduceHandlerRecycled.length; i++){
                groupMinimalReduceHandlerRecycled[i] = null;
            }	
            
                    
            neededLength = groupMaximalReduceHandlerFree + groupMaximalReduceHandlerRecycledCount; 
            if(neededLength > groupMaximalReduceHandler.length){
                if(neededLength > groupMaximalReduceHandlerMaxSize){
                    neededLength = groupMaximalReduceHandlerMaxSize;
                    GroupMaximalReduceHandler[] increased = new GroupMaximalReduceHandler[neededLength];
                    System.arraycopy(groupMaximalReduceHandler, 0, increased, 0, groupMaximalReduceHandler.length);
                    groupMaximalReduceHandler = increased;		        
                    System.arraycopy(groupMaximalReduceHandlerRecycled, 0, groupMaximalReduceHandler, groupMaximalReduceHandlerFree, groupMaximalReduceHandlerMaxSize - groupMaximalReduceHandlerFree);
                    groupMaximalReduceHandlerFree = groupMaximalReduceHandlerMaxSize; 
                }else{
                    GroupMaximalReduceHandler[] increased = new GroupMaximalReduceHandler[neededLength];
                    System.arraycopy(groupMaximalReduceHandler, 0, increased, 0, groupMaximalReduceHandler.length);
                    groupMaximalReduceHandler = increased;
                    System.arraycopy(groupMaximalReduceHandlerRecycled, 0, groupMaximalReduceHandler, groupMaximalReduceHandlerFree, groupMaximalReduceHandlerRecycledCount);
                    groupMaximalReduceHandlerFree += groupMaximalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(groupMaximalReduceHandlerRecycled, 0, groupMaximalReduceHandler, groupMaximalReduceHandlerFree, groupMaximalReduceHandlerRecycledCount);
                groupMaximalReduceHandlerFree += groupMaximalReduceHandlerRecycledCount;
            }
            
            if(groupMaximalReduceHandlerAverageUse != 0)groupMaximalReduceHandlerAverageUse = (groupMaximalReduceHandlerAverageUse + groupMaximalReduceHandlerEffectivellyUsed)/2;
            else groupMaximalReduceHandlerAverageUse = groupMaximalReduceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < groupMaximalReduceHandlerRecycled.length; i++){
                groupMaximalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = interleaveMinimalReduceHandlerFree + interleaveMinimalReduceHandlerRecycledCount; 
            if(neededLength > interleaveMinimalReduceHandler.length){
                if(neededLength > interleaveMinimalReduceHandlerMaxSize){
                    neededLength = interleaveMinimalReduceHandlerMaxSize;
                    InterleaveMinimalReduceHandler[] increased = new InterleaveMinimalReduceHandler[neededLength];
                    System.arraycopy(interleaveMinimalReduceHandler, 0, increased, 0, interleaveMinimalReduceHandler.length);
                    interleaveMinimalReduceHandler = increased;		        
                    System.arraycopy(interleaveMinimalReduceHandlerRecycled, 0, interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, interleaveMinimalReduceHandlerMaxSize - interleaveMinimalReduceHandlerFree);
                    interleaveMinimalReduceHandlerFree = interleaveMinimalReduceHandlerMaxSize; 
                }else{
                    InterleaveMinimalReduceHandler[] increased = new InterleaveMinimalReduceHandler[neededLength];
                    System.arraycopy(interleaveMinimalReduceHandler, 0, increased, 0, interleaveMinimalReduceHandler.length);
                    interleaveMinimalReduceHandler = increased;
                    System.arraycopy(interleaveMinimalReduceHandlerRecycled, 0, interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, interleaveMinimalReduceHandlerRecycledCount);
                    interleaveMinimalReduceHandlerFree += interleaveMinimalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveMinimalReduceHandlerRecycled, 0, interleaveMinimalReduceHandler, interleaveMinimalReduceHandlerFree, interleaveMinimalReduceHandlerRecycledCount);
                interleaveMinimalReduceHandlerFree += interleaveMinimalReduceHandlerRecycledCount;
            }
            
            if(interleaveMinimalReduceHandlerAverageUse != 0)interleaveMinimalReduceHandlerAverageUse = (interleaveMinimalReduceHandlerAverageUse + interleaveMinimalReduceHandlerEffectivellyUsed)/2;
            else interleaveMinimalReduceHandlerAverageUse = interleaveMinimalReduceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < interleaveMinimalReduceHandlerRecycled.length; i++){
                interleaveMinimalReduceHandlerRecycled[i] = null;
            }	
            
            
            neededLength = interleaveMaximalReduceHandlerFree + interleaveMaximalReduceHandlerRecycledCount; 
            if(neededLength > interleaveMaximalReduceHandler.length){
                if(neededLength > interleaveMaximalReduceHandlerMaxSize){
                    neededLength = interleaveMaximalReduceHandlerMaxSize;
                    InterleaveMaximalReduceHandler[] increased = new InterleaveMaximalReduceHandler[neededLength];
                    System.arraycopy(interleaveMaximalReduceHandler, 0, increased, 0, interleaveMaximalReduceHandler.length);
                    interleaveMaximalReduceHandler = increased;		        
                    System.arraycopy(interleaveMaximalReduceHandlerRecycled, 0, interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, interleaveMaximalReduceHandlerMaxSize - interleaveMaximalReduceHandlerFree);
                    interleaveMaximalReduceHandlerFree = interleaveMaximalReduceHandlerMaxSize; 
                }else{
                    InterleaveMaximalReduceHandler[] increased = new InterleaveMaximalReduceHandler[neededLength];
                    System.arraycopy(interleaveMaximalReduceHandler, 0, increased, 0, interleaveMaximalReduceHandler.length);
                    interleaveMaximalReduceHandler = increased;
                    System.arraycopy(interleaveMaximalReduceHandlerRecycled, 0, interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, interleaveMaximalReduceHandlerRecycledCount);
                    interleaveMaximalReduceHandlerFree += interleaveMaximalReduceHandlerRecycledCount;
                }
            }else{
                System.arraycopy(interleaveMaximalReduceHandlerRecycled, 0, interleaveMaximalReduceHandler, interleaveMaximalReduceHandlerFree, interleaveMaximalReduceHandlerRecycledCount);
                interleaveMaximalReduceHandlerFree += interleaveMaximalReduceHandlerRecycledCount;
            }
            
            if(interleaveMaximalReduceHandlerAverageUse != 0)interleaveMaximalReduceHandlerAverageUse = (interleaveMaximalReduceHandlerAverageUse + interleaveMaximalReduceHandlerEffectivellyUsed)/2;
            else interleaveMaximalReduceHandlerAverageUse = interleaveMaximalReduceHandlerEffectivellyUsed;// this relies on the fact that the individual pools are smaller or equal to the common pool
            
            for(int i = 0; i < interleaveMaximalReduceHandlerRecycled.length; i++){
                interleaveMaximalReduceHandlerRecycled[i] = null;
            }
        }
	}
} 
