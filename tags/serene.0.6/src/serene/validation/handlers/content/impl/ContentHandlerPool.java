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

package serene.validation.handlers.content.impl;

import java.util.Arrays;

import javax.xml.validation.ValidatorHandler;

import serene.bind.BindingPool;

import serene.validation.schema.active.ActiveModelPool;

import sereneWrite.MessageWriter;

public class ContentHandlerPool{
	private static volatile ContentHandlerPool instance; 
	
	MessageWriter debugWriter;
		
	int eventHandlerPoolFree; 
	int eventHandlerPoolPoolSize;
	ValidatorEventHandlerPool[] eventHandlerPools;	
	
	
	int elementVHAverageUse;
	int elementVHPoolSize;
	int elementVHFree;	
	ElementValidationHandler[] elementVH;
	
	int startVHAverageUse;
	int startVHPoolSize;
	int startVHFree;	
	StartValidationHandler[] startVH;
		
	int unexpectedElementHAverageUse;
	int unexpectedElementHPoolSize;
	int unexpectedElementHFree;	
	UnexpectedElementHandler[] unexpectedElementH;
	
	int unexpectedAmbiguousEHAverageUse;
	int unexpectedAmbiguousEHPoolSize;
	int unexpectedAmbiguousEHFree;
	UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEH;
	
	int unknownElementHAverageUse;
	int unknownElementHPoolSize;
	int unknownElementHFree;	
	UnknownElementHandler[] unknownElementH;
	
	int defaultElementHAverageUse;
	int defaultElementHPoolSize;
	int defaultElementHFree;	
	ElementDefaultHandler[] defaultElementH;
	
	int elementConcurrentHAverageUse;
	int elementConcurrentHPoolSize;
	int elementConcurrentHFree;
	ElementConcurrentHandler[] elementConcurrentH;
	
	int elementParallelHAverageUse;
	int elementParallelHPoolSize;
	int elementParallelHFree;
	ElementParallelHandler[] elementParallelH;
	
	int elementCommonHAverageUse;
	int elementCommonHPoolSize;
	int elementCommonHFree;
	ElementCommonHandler[] elementCommonH;
	
	int unexpectedAttributeHAverageUse;
	int unexpectedAttributeHPoolSize;
	int unexpectedAttributeHFree;	
	UnexpectedAttributeHandler[] unexpectedAttributeH;
	
	int unexpectedAmbiguousAHAverageUse;
	int unexpectedAmbiguousAHPoolSize;
	int unexpectedAmbiguousAHFree;
	UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAH;
	
	int unknownAttributeHAverageUse;
	int unknownAttributeHPoolSize;
	int unknownAttributeHFree;	
	UnknownAttributeHandler[] unknownAttributeH;
	
	int attributeVHAverageUse;
	int attributeVHPoolSize;
	int attributeVHFree;	
	AttributeValidationHandler[] attributeVH;
    
    int candidateAttributeVHAverageUse;
	int candidateAttributeVHPoolSize;
	int candidateAttributeVHFree;	
	CandidateAttributeValidationHandler[] candidateAttributeVH;
		
	int attributeConcurrentHAverageUse;
	int attributeConcurrentHPoolSize;
	int attributeConcurrentHFree;
	AttributeConcurrentHandler[] attributeConcurrentH;
    
    int attributeParallelHAverageUse;
	int attributeParallelHPoolSize;
	int attributeParallelHFree;
	AttributeParallelHandler[] attributeParallelH;
    
    int attributeDefaultHAverageUse;
	int attributeDefaultHPoolSize;
	int attributeDefaultHFree;
	AttributeDefaultHandler[] attributeDefaultH;
	
	int charactersValidationHAverageUse;
	int charactersValidationHPoolSize;
	int charactersValidationHFree;
	CharactersValidationHandler[] charactersValidationH;
	
	int structuredDataValidationHAverageUse;
	int structuredDataValidationHPoolSize;
	int structuredDataValidationHFree;
	StructuredDataValidationHandler[] structuredDataValidationH;
    
	int dataValidationHAverageUse;
	int dataValidationHPoolSize;
	int dataValidationHFree;
	DataValidationHandler[] dataValidationH;
	
    int defaultVAttributeHAverageUse;
	int defaultVAttributeHPoolSize;
	int defaultVAttributeHFree;
	DefaultValueAttributeValidationHandler[] defaultVAttributeH;
		
	int listPatternVHAverageUse;
	int listPatternVHPoolSize;
	int listPatternVHFree;
	ListPatternValidationHandler[] listPatternVH;
	
	int exceptPatternVHAverageUse;
	int exceptPatternVHPoolSize;
	int exceptPatternVHFree;
	ExceptPatternValidationHandler[] exceptPatternVH;
	
	
	
	int boundElementVHAverageUse;
	int boundElementVHPoolSize;
	int boundElementVHFree;	
	BoundElementValidationHandler[] boundElementVH;
	
	int boundStartVHAverageUse;
	int boundStartVHPoolSize;
	int boundStartVHFree;	
	BoundStartValidationHandler[] boundStartVH;
	
	int boundElementConcurrentHAverageUse;
	int boundElementConcurrentHPoolSize;
	int boundElementConcurrentHFree;
	BoundElementConcurrentHandler[] boundElementConcurrentH;
		
	int boundElementParallelHAverageUse;
	int boundElementParallelHPoolSize;
	int boundElementParallelHFree;
	BoundElementParallelHandler[] boundElementParallelH;
	
	int boundAttributeVHAverageUse;
	int boundAttributeVHPoolSize;
	int boundAttributeVHFree;	
	BoundAttributeValidationHandler[] boundAttributeVH;
    
    int boundCandidateAttributeVHAverageUse;
	int boundCandidateAttributeVHPoolSize;
	int boundCandidateAttributeVHFree;	
	BoundCandidateAttributeValidationHandler[] boundCandidateAttributeVH;
		
	int boundAttributeConcurrentHAverageUse;
	int boundAttributeConcurrentHPoolSize;
	int boundAttributeConcurrentHFree;
	BoundAttributeConcurrentHandler[] boundAttributeConcurrentH;
    
    int boundAttributeParallelHAverageUse;
	int boundAttributeParallelHPoolSize;
	int boundAttributeParallelHFree;
	BoundAttributeParallelHandler[] boundAttributeParallelH;
	
	final int UNUSED = 0;
	
	
	private ContentHandlerPool(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
				
		
		eventHandlerPoolFree = 0; 
		eventHandlerPoolPoolSize = 10;
		eventHandlerPools = new ValidatorEventHandlerPool[eventHandlerPoolPoolSize];
		
		elementVHAverageUse = UNUSED;
		elementVHPoolSize = 20;
		elementVHFree = 0;
		elementVH = new ElementValidationHandler[elementVHPoolSize];
		
		startVHAverageUse = UNUSED;
		startVHPoolSize = 20;
		startVHFree = 0;
		startVH = new StartValidationHandler[startVHPoolSize];
		

		unexpectedElementHAverageUse = UNUSED;
		unexpectedElementHPoolSize = 3;
		unexpectedElementHFree = 0;
		unexpectedElementH = new UnexpectedElementHandler[unexpectedElementHPoolSize];
	
		unexpectedAmbiguousEHAverageUse = UNUSED;
		unexpectedAmbiguousEHPoolSize = 1;
		unexpectedAmbiguousEHFree = 0;
		unexpectedAmbiguousEH = new UnexpectedAmbiguousElementHandler[unexpectedAmbiguousEHPoolSize];
	
		unknownElementHAverageUse = UNUSED;
		unknownElementHPoolSize = 3;
		unknownElementHFree = 0;
		unknownElementH = new UnknownElementHandler[unknownElementHPoolSize];
		
		defaultElementHAverageUse = UNUSED;
		defaultElementHPoolSize = 3;
		defaultElementHFree = 0;	
		defaultElementH = new ElementDefaultHandler[defaultElementHPoolSize];
		
		elementConcurrentHAverageUse = UNUSED;
		elementConcurrentHPoolSize = 1;
		elementConcurrentHFree = 0;
		elementConcurrentH = new ElementConcurrentHandler[elementConcurrentHPoolSize];
	
		elementParallelHAverageUse = UNUSED;
		elementParallelHPoolSize = 3;
		elementParallelHFree = 0;
		elementParallelH = new ElementParallelHandler[elementParallelHPoolSize];
		
		elementCommonHAverageUse = UNUSED;
		elementCommonHPoolSize = 3;
		elementCommonHFree = 0;
		elementCommonH = new ElementCommonHandler[elementCommonHPoolSize];		
	

		unexpectedAttributeHAverageUse = UNUSED;
		unexpectedAttributeHPoolSize = 3;
		unexpectedAttributeHFree = 0;
		unexpectedAttributeH = new UnexpectedAttributeHandler[unexpectedAttributeHPoolSize];
	
		unexpectedAmbiguousAHAverageUse = UNUSED;
		unexpectedAmbiguousAHPoolSize = 1;
		unexpectedAmbiguousAHFree = 0;
		unexpectedAmbiguousAH = new UnexpectedAmbiguousAttributeHandler[unexpectedAmbiguousAHPoolSize];
	
		unknownAttributeHAverageUse = UNUSED;
		unknownAttributeHPoolSize = 3;
		unknownAttributeHFree = 0;
		unknownAttributeH = new UnknownAttributeHandler[unknownAttributeHPoolSize];
		
		attributeVHAverageUse = UNUSED;
		attributeVHPoolSize = 20;
		attributeVHFree = 0;
		attributeVH = new AttributeValidationHandler[attributeVHPoolSize];
        
		candidateAttributeVHAverageUse = UNUSED;
		candidateAttributeVHPoolSize = 20;
		candidateAttributeVHFree = 0;
		candidateAttributeVH = new CandidateAttributeValidationHandler[candidateAttributeVHPoolSize];
		
		attributeConcurrentHAverageUse = UNUSED;
		attributeConcurrentHPoolSize = 1;
		attributeConcurrentHFree = 0;
		attributeConcurrentH = new AttributeConcurrentHandler[attributeConcurrentHPoolSize];
        
        attributeParallelHAverageUse = UNUSED;
		attributeParallelHPoolSize = 1;
		attributeParallelHFree = 0;
		attributeParallelH = new AttributeParallelHandler[attributeParallelHPoolSize];
		
        attributeDefaultHAverageUse = UNUSED;
		attributeDefaultHPoolSize = 1;
		attributeDefaultHFree = 0;
		attributeDefaultH = new AttributeDefaultHandler[attributeDefaultHPoolSize];
        
        
		charactersValidationHAverageUse = UNUSED;
		charactersValidationHPoolSize = 1;
		charactersValidationHFree = 0;
		charactersValidationH = new CharactersValidationHandler[charactersValidationHPoolSize];
		
		structuredDataValidationHAverageUse = UNUSED;
		structuredDataValidationHPoolSize = 1;
		structuredDataValidationHFree = 0;
		structuredDataValidationH = new StructuredDataValidationHandler[structuredDataValidationHPoolSize];
        
		dataValidationHAverageUse = UNUSED;
		dataValidationHPoolSize = 1;
		dataValidationHFree = 0;
		dataValidationH = new DataValidationHandler[dataValidationHPoolSize];
        
		
        defaultVAttributeHAverageUse = UNUSED;
		defaultVAttributeHPoolSize = 1;
		defaultVAttributeHFree = 0;
		defaultVAttributeH = new DefaultValueAttributeValidationHandler[defaultVAttributeHPoolSize];
		
		listPatternVHAverageUse = UNUSED;
		listPatternVHPoolSize = 1;
		listPatternVHFree = 0;
		listPatternVH = new ListPatternValidationHandler[listPatternVHPoolSize];
		
		exceptPatternVHAverageUse = UNUSED;
		exceptPatternVHPoolSize = 1;
		exceptPatternVHFree = 0;
		exceptPatternVH = new ExceptPatternValidationHandler[exceptPatternVHPoolSize];
		
		
		
		boundElementVHAverageUse = UNUSED;
		boundElementVHPoolSize = 20;
		boundElementVHFree = 0;
		boundElementVH = new BoundElementValidationHandler[boundElementVHPoolSize];
		
		boundStartVHAverageUse = UNUSED;
		boundStartVHPoolSize = 20;
		boundStartVHFree = 0;
		boundStartVH = new BoundStartValidationHandler[boundStartVHPoolSize];
		
		boundElementConcurrentHAverageUse = UNUSED;
		boundElementConcurrentHPoolSize = 1;
		boundElementConcurrentHFree = 0;
		boundElementConcurrentH = new BoundElementConcurrentHandler[boundElementConcurrentHPoolSize];
		
		boundElementParallelHAverageUse = UNUSED;
		boundElementParallelHPoolSize = 3;
		boundElementParallelHFree = 0;
		boundElementParallelH = new BoundElementParallelHandler[boundElementParallelHPoolSize];
		
		boundAttributeVHAverageUse = UNUSED;
		boundAttributeVHPoolSize = 20;
		boundAttributeVHFree = 0;
		boundAttributeVH = new BoundAttributeValidationHandler[boundAttributeVHPoolSize];
        
        boundCandidateAttributeVHAverageUse = UNUSED;
		boundCandidateAttributeVHPoolSize = 20;
		boundCandidateAttributeVHFree = 0;
		boundCandidateAttributeVH = new BoundCandidateAttributeValidationHandler[boundCandidateAttributeVHPoolSize];
		
		boundAttributeConcurrentHAverageUse = UNUSED;
		boundAttributeConcurrentHPoolSize = 1;
		boundAttributeConcurrentHFree = 0;
		boundAttributeConcurrentH = new BoundAttributeConcurrentHandler[boundAttributeConcurrentHPoolSize];
        
        boundAttributeParallelHAverageUse = UNUSED;
		boundAttributeParallelHPoolSize = 1;
		boundAttributeParallelHFree = 0;
		boundAttributeParallelH = new BoundAttributeParallelHandler[boundAttributeParallelHPoolSize];
		
	}
	
	public static ContentHandlerPool getInstance(MessageWriter debugWriter){
		if(instance == null){
			synchronized(ContentHandlerPool.class){
				if(instance == null){
					instance = new ContentHandlerPool(debugWriter); 
				}
			}
		}
		return instance;
	}
	
	public synchronized ValidatorEventHandlerPool getValidatorEventHandlerPool(){
		if(eventHandlerPoolFree == 0){
			ValidatorEventHandlerPool ehp = new ValidatorEventHandlerPool(this, debugWriter);
			return ehp;
		}else{
			ValidatorEventHandlerPool ehp = eventHandlerPools[--eventHandlerPoolFree];
			return ehp;
		}
	}
	
	public synchronized void recycle(ValidatorEventHandlerPool eventHandlerPool){		
		if(eventHandlerPoolFree == eventHandlerPoolPoolSize){
			ValidatorEventHandlerPool[] increased = new ValidatorEventHandlerPool[++eventHandlerPoolPoolSize];
			System.arraycopy(eventHandlerPools, 0, increased, 0, eventHandlerPoolFree);
			eventHandlerPools = increased;
		}
		eventHandlerPools[eventHandlerPoolFree++] = eventHandlerPool;
	}
	
	synchronized void fill(ValidatorEventHandlerPool eventHandlerPool,
					ElementValidationHandler[] elementVH,
					StartValidationHandler[] startVH,
					UnexpectedElementHandler[] unexpectedElementH,
					UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEH,
					UnknownElementHandler[] unknownElementH,
					ElementDefaultHandler[] defaultElementH,
					ElementConcurrentHandler[] elementConcurrentH,
					ElementParallelHandler[] elementParallelH,
					ElementCommonHandler[] elementCommonH,
					UnexpectedAttributeHandler[] unexpectedAttributeH,
					UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAH,
					UnknownAttributeHandler[] unknownAttributeH,
					AttributeValidationHandler[] attributeVH,
					CandidateAttributeValidationHandler[] candidateAttributeVH,
					AttributeConcurrentHandler[] attributeConcurrentH,
                    AttributeParallelHandler[] attributeParallelH,
                    AttributeDefaultHandler[] attributeDefaultH,
					CharactersValidationHandler[] charactersValidationH,					
					StructuredDataValidationHandler[] structuredDataValidationH,
					DataValidationHandler[] dataValidationH,
                    DefaultValueAttributeValidationHandler[] defaultVAttributeH,
					ListPatternValidationHandler[] listPatternVH,
					ExceptPatternValidationHandler[] exceptPatternVH,
					BoundElementValidationHandler[] boundElementVH,
					BoundStartValidationHandler[] boundStartVH,
					BoundElementConcurrentHandler[] boundElementConcurrentH,
					BoundElementParallelHandler[] boundElementParallelH,
					BoundAttributeValidationHandler[] boundAttributeVH,
					BoundCandidateAttributeValidationHandler[] boundCandidateAttributeVH,
					BoundAttributeConcurrentHandler[] boundAttributeConcurrentH,
					BoundAttributeParallelHandler[] boundAttributeParallelH){
	
		int elementVHFillCount;
		if(elementVH == null || elementVH.length < elementVHAverageUse)
			elementVH = new ElementValidationHandler[elementVHAverageUse];		
		if(elementVHFree > elementVHAverageUse){
			elementVHFillCount = elementVHAverageUse;
			elementVHFree = elementVHFree - elementVHAverageUse;
		}else{
			elementVHFillCount = elementVHFree;
			elementVHFree = 0;
		}		
		System.arraycopy(this.elementVH, elementVHFree, 
							elementVH, 0, elementVHFillCount);
		
		int startVHFillCount;
		if(startVH == null || startVH.length < startVHAverageUse)
			startVH = new StartValidationHandler[startVHAverageUse];		
		if(startVHFree > startVHAverageUse){
			startVHFillCount = startVHAverageUse;
			startVHFree = startVHFree - startVHAverageUse;
		}else{
			startVHFillCount = startVHFree;
			startVHFree = 0;
		}		
		System.arraycopy(this.startVH, startVHFree, 
							startVH, 0, startVHFillCount);
		
	
		
		int unexpectedElementHFillCount;
		if(unexpectedElementH == null || unexpectedElementH.length < unexpectedElementHAverageUse)
			unexpectedElementH = new UnexpectedElementHandler[unexpectedElementHAverageUse];		
		if(unexpectedElementHFree > unexpectedElementHAverageUse){
			unexpectedElementHFillCount = unexpectedElementHAverageUse;
			unexpectedElementHFree = unexpectedElementHFree - unexpectedElementHAverageUse;
		}else{
			unexpectedElementHFillCount = unexpectedElementHFree;
			unexpectedElementHFree = 0;
		}		
		System.arraycopy(this.unexpectedElementH, unexpectedElementHFree, 
							unexpectedElementH, 0, unexpectedElementHFillCount);
			
		int unexpectedAmbiguousEHFillCount;
		if(unexpectedAmbiguousEH == null || unexpectedAmbiguousEH.length < unexpectedAmbiguousEHAverageUse)
			unexpectedAmbiguousEH = new UnexpectedAmbiguousElementHandler[unexpectedAmbiguousEHAverageUse];		
		if(unexpectedAmbiguousEHFree > unexpectedAmbiguousEHAverageUse){
			unexpectedAmbiguousEHFillCount = unexpectedAmbiguousEHAverageUse;
			unexpectedAmbiguousEHFree = unexpectedAmbiguousEHFree - unexpectedAmbiguousEHAverageUse;
		}else{
			unexpectedAmbiguousEHFillCount = unexpectedAmbiguousEHFree;
			unexpectedAmbiguousEHFree = 0;
		}		
		System.arraycopy(this.unexpectedAmbiguousEH, unexpectedAmbiguousEHFree, 
							unexpectedAmbiguousEH, 0, unexpectedAmbiguousEHFillCount);
		
		int unknownElementHFillCount;
		if(unknownElementH == null || unknownElementH.length < unknownElementHAverageUse)
			unknownElementH = new UnknownElementHandler[unknownElementHAverageUse];		
		if(unknownElementHFree > unknownElementHAverageUse){
			unknownElementHFillCount = unknownElementHAverageUse;
			unknownElementHFree = unknownElementHFree - unknownElementHAverageUse;
		}else{
			unknownElementHFillCount = unknownElementHFree;
			unknownElementHFree = 0;
		}		
		System.arraycopy(this.unknownElementH, unknownElementHFree, 
							unknownElementH, 0, unknownElementHFillCount);
				
		int defaultElementHFillCount;
		if(defaultElementH == null || defaultElementH.length < defaultElementHAverageUse)
			defaultElementH = new ElementDefaultHandler[defaultElementHAverageUse];		
		if(defaultElementHFree > defaultElementHAverageUse){
			defaultElementHFillCount = defaultElementHAverageUse;
			defaultElementHFree = defaultElementHFree - defaultElementHAverageUse;
		}else{
			defaultElementHFillCount = defaultElementHFree;
			defaultElementHFree = 0;
		}		
		System.arraycopy(this.defaultElementH, defaultElementHFree, 
							defaultElementH, 0, defaultElementHFillCount);

		int elementConcurrentHFillCount;
		if(elementConcurrentH == null || elementConcurrentH.length < elementConcurrentHAverageUse)
			elementConcurrentH = new ElementConcurrentHandler[elementConcurrentHAverageUse];		
		if(elementConcurrentHFree > elementConcurrentHAverageUse){
			elementConcurrentHFillCount = elementConcurrentHAverageUse;
			elementConcurrentHFree = elementConcurrentHFree - elementConcurrentHAverageUse;
		}else{
			elementConcurrentHFillCount = elementConcurrentHFree;
			elementConcurrentHFree = 0;
		}		
		System.arraycopy(this.elementConcurrentH, elementConcurrentHFree, 
							elementConcurrentH, 0, elementConcurrentHFillCount);
				
		int elementParallelHFillCount;
		if(elementParallelH == null || elementParallelH.length < elementParallelHAverageUse)
			elementParallelH = new ElementParallelHandler[elementParallelHAverageUse];		
		if(elementParallelHFree > elementParallelHAverageUse){
			elementParallelHFillCount = elementParallelHAverageUse;
			elementParallelHFree = elementParallelHFree - elementParallelHAverageUse;
		}else{
			elementParallelHFillCount = elementParallelHFree;
			elementParallelHFree = 0;
		}		
		System.arraycopy(this.elementParallelH, elementParallelHFree, 
							elementParallelH, 0, elementParallelHFillCount);
				
		int elementCommonHFillCount;
		if(elementCommonH == null || elementCommonH.length < elementCommonHAverageUse)
			elementCommonH = new ElementCommonHandler[elementCommonHAverageUse];		
		if(elementCommonHFree > elementCommonHAverageUse){
			elementCommonHFillCount = elementCommonHAverageUse;
			elementCommonHFree = elementCommonHFree - elementCommonHAverageUse;
		}else{
			elementCommonHFillCount = elementCommonHFree;
			elementCommonHFree = 0;
		}		
		System.arraycopy(this.elementCommonH, elementCommonHFree, 
							elementCommonH, 0, elementCommonHFillCount);
		
		
		
		int unexpectedAttributeHFillCount;
		if(unexpectedAttributeH == null || unexpectedAttributeH.length < unexpectedAttributeHAverageUse)
			unexpectedAttributeH = new UnexpectedAttributeHandler[unexpectedAttributeHAverageUse];		
		if(unexpectedAttributeHFree > unexpectedAttributeHAverageUse){
			unexpectedAttributeHFillCount = unexpectedAttributeHAverageUse;
			unexpectedAttributeHFree = unexpectedAttributeHFree - unexpectedAttributeHAverageUse;
		}else{
			unexpectedAttributeHFillCount = unexpectedAttributeHFree;
			unexpectedAttributeHFree = 0;
		}		
		System.arraycopy(this.unexpectedAttributeH, unexpectedAttributeHFree, 
							unexpectedAttributeH, 0, unexpectedAttributeHFillCount);
			
		int unexpectedAmbiguousAHFillCount;
		if(unexpectedAmbiguousAH == null || unexpectedAmbiguousAH.length < unexpectedAmbiguousAHAverageUse)
			unexpectedAmbiguousAH = new UnexpectedAmbiguousAttributeHandler[unexpectedAmbiguousAHAverageUse];		
		if(unexpectedAmbiguousAHFree > unexpectedAmbiguousAHAverageUse){
			unexpectedAmbiguousAHFillCount = unexpectedAmbiguousAHAverageUse;
			unexpectedAmbiguousAHFree = unexpectedAmbiguousAHFree - unexpectedAmbiguousAHAverageUse;
		}else{
			unexpectedAmbiguousAHFillCount = unexpectedAmbiguousAHFree;
			unexpectedAmbiguousAHFree = 0;
		}		
		System.arraycopy(this.unexpectedAmbiguousAH, unexpectedAmbiguousAHFree, 
							unexpectedAmbiguousAH, 0, unexpectedAmbiguousAHFillCount);
		
		int unknownAttributeHFillCount;
		if(unknownAttributeH == null || unknownAttributeH.length < unknownAttributeHAverageUse)
			unknownAttributeH = new UnknownAttributeHandler[unknownAttributeHAverageUse];		
		if(unknownAttributeHFree > unknownAttributeHAverageUse){
			unknownAttributeHFillCount = unknownAttributeHAverageUse;
			unknownAttributeHFree = unknownAttributeHFree - unknownAttributeHAverageUse;
		}else{
			unknownAttributeHFillCount = unknownAttributeHFree;
			unknownAttributeHFree = 0;
		}		
		System.arraycopy(this.unknownAttributeH, unknownAttributeHFree, 
							unknownAttributeH, 0, unknownAttributeHFillCount);
				
		int attributeVHFillCount;
		if(attributeVH == null || attributeVH.length < attributeVHAverageUse)
			attributeVH = new AttributeValidationHandler[attributeVHAverageUse];		
		if(attributeVHFree > attributeVHAverageUse){
			attributeVHFillCount = attributeVHAverageUse;
			attributeVHFree = attributeVHFree - attributeVHAverageUse;
		}else{
			attributeVHFillCount = attributeVHFree;
			attributeVHFree = 0;
		}		
		System.arraycopy(this.attributeVH, attributeVHFree, 
							attributeVH, 0, attributeVHFillCount);
		
        int candidateAttributeVHFillCount;
		if(candidateAttributeVH == null || candidateAttributeVH.length < candidateAttributeVHAverageUse)
			candidateAttributeVH = new CandidateAttributeValidationHandler[candidateAttributeVHAverageUse];		
		if(candidateAttributeVHFree > candidateAttributeVHAverageUse){
			candidateAttributeVHFillCount = candidateAttributeVHAverageUse;
			candidateAttributeVHFree = candidateAttributeVHFree - candidateAttributeVHAverageUse;
		}else{
			candidateAttributeVHFillCount = candidateAttributeVHFree;
			candidateAttributeVHFree = 0;
		}		
		System.arraycopy(this.candidateAttributeVH, candidateAttributeVHFree, 
							candidateAttributeVH, 0, candidateAttributeVHFillCount);
		
		int attributeConcurrentHFillCount;
		if(attributeConcurrentH == null || attributeConcurrentH.length < attributeConcurrentHAverageUse)
			attributeConcurrentH = new AttributeConcurrentHandler[attributeConcurrentHAverageUse];		
		if(attributeConcurrentHFree > attributeConcurrentHAverageUse){
			attributeConcurrentHFillCount = attributeConcurrentHAverageUse;
			attributeConcurrentHFree = attributeConcurrentHFree - attributeConcurrentHAverageUse;
		}else{
			attributeConcurrentHFillCount = attributeConcurrentHFree;
			attributeConcurrentHFree = 0;
		}		
		System.arraycopy(this.attributeConcurrentH, attributeConcurrentHFree, 
							attributeConcurrentH, 0, attributeConcurrentHFillCount);
        
        int attributeParallelHFillCount;
		if(attributeParallelH == null || attributeParallelH.length < attributeParallelHAverageUse)
			attributeParallelH = new AttributeParallelHandler[attributeParallelHAverageUse];		
		if(attributeParallelHFree > attributeParallelHAverageUse){
			attributeParallelHFillCount = attributeParallelHAverageUse;
			attributeParallelHFree = attributeParallelHFree - attributeParallelHAverageUse;
		}else{
			attributeParallelHFillCount = attributeParallelHFree;
			attributeParallelHFree = 0;
		}		
		System.arraycopy(this.attributeParallelH, attributeParallelHFree, 
							attributeParallelH, 0, attributeParallelHFillCount);
        
        int attributeDefaultHFillCount;
		if(attributeDefaultH == null || attributeDefaultH.length < attributeDefaultHAverageUse)
			attributeDefaultH = new AttributeDefaultHandler[attributeDefaultHAverageUse];		
		if(attributeDefaultHFree > attributeDefaultHAverageUse){
			attributeDefaultHFillCount = attributeDefaultHAverageUse;
			attributeDefaultHFree = attributeDefaultHFree - attributeDefaultHAverageUse;
		}else{
			attributeDefaultHFillCount = attributeDefaultHFree;
			attributeDefaultHFree = 0;
		}		
		System.arraycopy(this.attributeDefaultH, attributeDefaultHFree, 
							attributeDefaultH, 0, attributeDefaultHFillCount);
		
		int charactersValidationHFillCount;
		if(charactersValidationH == null || charactersValidationH.length < charactersValidationHAverageUse)
			charactersValidationH = new CharactersValidationHandler[charactersValidationHAverageUse];		
		if(charactersValidationHFree > charactersValidationHAverageUse){
			charactersValidationHFillCount = charactersValidationHAverageUse;
			charactersValidationHFree = charactersValidationHFree - charactersValidationHAverageUse;
		}else{
			charactersValidationHFillCount = charactersValidationHFree;
			charactersValidationHFree = 0;
		}		
		System.arraycopy(this.charactersValidationH, charactersValidationHFree, 
							charactersValidationH, 0, charactersValidationHFillCount);
		
		int structuredDataValidationHFillCount;
		if(structuredDataValidationH == null || structuredDataValidationH.length < structuredDataValidationHAverageUse)
			structuredDataValidationH = new StructuredDataValidationHandler[structuredDataValidationHAverageUse];		
		if(structuredDataValidationHFree > structuredDataValidationHAverageUse){
			structuredDataValidationHFillCount = structuredDataValidationHAverageUse;
			structuredDataValidationHFree = structuredDataValidationHFree - structuredDataValidationHAverageUse;
		}else{
			structuredDataValidationHFillCount = structuredDataValidationHFree;
			structuredDataValidationHFree = 0;
		}		
		System.arraycopy(this.structuredDataValidationH, structuredDataValidationHFree, 
							structuredDataValidationH, 0, structuredDataValidationHFillCount);
        
		int dataValidationHFillCount;
		if(dataValidationH == null || dataValidationH.length < dataValidationHAverageUse)
			dataValidationH = new DataValidationHandler[dataValidationHAverageUse];		
		if(dataValidationHFree > dataValidationHAverageUse){
			dataValidationHFillCount = dataValidationHAverageUse;
			dataValidationHFree = dataValidationHFree - dataValidationHAverageUse;
		}else{
			dataValidationHFillCount = dataValidationHFree;
			dataValidationHFree = 0;
		}		
		System.arraycopy(this.dataValidationH, dataValidationHFree, 
							dataValidationH, 0, dataValidationHFillCount);
        
		
        int defaultVAttributeHFillCount;
		if(defaultVAttributeH == null || defaultVAttributeH.length < defaultVAttributeHAverageUse)
			defaultVAttributeH = new DefaultValueAttributeValidationHandler[defaultVAttributeHAverageUse];		
		if(defaultVAttributeHFree > defaultVAttributeHAverageUse){
			defaultVAttributeHFillCount = defaultVAttributeHAverageUse;
			defaultVAttributeHFree = defaultVAttributeHFree - defaultVAttributeHAverageUse;
		}else{
			defaultVAttributeHFillCount = defaultVAttributeHFree;
			defaultVAttributeHFree = 0;
		}		
		System.arraycopy(this.defaultVAttributeH, defaultVAttributeHFree, 
							defaultVAttributeH, 0, defaultVAttributeHFillCount);
		
		int listPatternVHFillCount;
		if(listPatternVH == null || listPatternVH.length < listPatternVHAverageUse)
			listPatternVH = new ListPatternValidationHandler[listPatternVHAverageUse];		
		if(listPatternVHFree > listPatternVHAverageUse){
			listPatternVHFillCount = listPatternVHAverageUse;
			listPatternVHFree = listPatternVHFree - listPatternVHAverageUse;
		}else{
			listPatternVHFillCount = listPatternVHFree;
			listPatternVHFree = 0;
		}		
		System.arraycopy(this.listPatternVH, listPatternVHFree, 
							listPatternVH, 0, listPatternVHFillCount);
		
		int exceptPatternVHFillCount;
		if(exceptPatternVH == null || exceptPatternVH.length < exceptPatternVHAverageUse)
			exceptPatternVH = new ExceptPatternValidationHandler[exceptPatternVHAverageUse];		
		if(exceptPatternVHFree > exceptPatternVHAverageUse){
			exceptPatternVHFillCount = exceptPatternVHAverageUse;
			exceptPatternVHFree = exceptPatternVHFree - exceptPatternVHAverageUse;
		}else{
			exceptPatternVHFillCount = exceptPatternVHFree;
			exceptPatternVHFree = 0;
		}		
		System.arraycopy(this.exceptPatternVH, exceptPatternVHFree, 
							exceptPatternVH, 0, exceptPatternVHFillCount);
		
		
		
		int boundElementVHFillCount;
		if(boundElementVH == null || boundElementVH.length < boundElementVHAverageUse)
			boundElementVH = new BoundElementValidationHandler[boundElementVHAverageUse];		
		if(boundElementVHFree > boundElementVHAverageUse){
			boundElementVHFillCount = boundElementVHAverageUse;
			boundElementVHFree = boundElementVHFree - boundElementVHAverageUse;
		}else{
			boundElementVHFillCount = boundElementVHFree;
			boundElementVHFree = 0;
		}		
		System.arraycopy(this.boundElementVH, boundElementVHFree, 
							boundElementVH, 0, boundElementVHFillCount);
				
		int boundStartVHFillCount;
		if(boundStartVH == null || boundStartVH.length < boundStartVHAverageUse)
			boundStartVH = new BoundStartValidationHandler[boundStartVHAverageUse];		
		if(boundStartVHFree > boundStartVHAverageUse){
			boundStartVHFillCount = boundStartVHAverageUse;
			boundStartVHFree = boundStartVHFree - boundStartVHAverageUse;
		}else{
			boundStartVHFillCount = boundStartVHFree;
			boundStartVHFree = 0;
		}		
		System.arraycopy(this.boundStartVH, boundStartVHFree, 
							boundStartVH, 0, boundStartVHFillCount);
		
		int boundElementConcurrentHFillCount;
		if(boundElementConcurrentH == null || boundElementConcurrentH.length < boundElementConcurrentHAverageUse)
			boundElementConcurrentH = new BoundElementConcurrentHandler[boundElementConcurrentHAverageUse];		
		if(boundElementConcurrentHFree > boundElementConcurrentHAverageUse){
			boundElementConcurrentHFillCount = boundElementConcurrentHAverageUse;
			boundElementConcurrentHFree = boundElementConcurrentHFree - boundElementConcurrentHAverageUse;
		}else{
			boundElementConcurrentHFillCount = boundElementConcurrentHFree;
			boundElementConcurrentHFree = 0;
		}		
		System.arraycopy(this.boundElementConcurrentH, boundElementConcurrentHFree, 
							boundElementConcurrentH, 0, boundElementConcurrentHFillCount);
				
		int boundElementParallelHFillCount;
		if(boundElementParallelH == null || boundElementParallelH.length < boundElementParallelHAverageUse)
			boundElementParallelH = new BoundElementParallelHandler[boundElementParallelHAverageUse];		
		if(boundElementParallelHFree > boundElementParallelHAverageUse){
			boundElementParallelHFillCount = boundElementParallelHAverageUse;
			boundElementParallelHFree = boundElementParallelHFree - boundElementParallelHAverageUse;
		}else{
			boundElementParallelHFillCount = boundElementParallelHFree;
			boundElementParallelHFree = 0;
		}		
		System.arraycopy(this.boundElementParallelH, boundElementParallelHFree, 
							boundElementParallelH, 0, boundElementParallelHFillCount);
		
		
		int boundAttributeVHFillCount;
		if(boundAttributeVH == null || boundAttributeVH.length < boundAttributeVHAverageUse)
			boundAttributeVH = new BoundAttributeValidationHandler[boundAttributeVHAverageUse];		
		if(boundAttributeVHFree > boundAttributeVHAverageUse){
			boundAttributeVHFillCount = boundAttributeVHAverageUse;
			boundAttributeVHFree = boundAttributeVHFree - boundAttributeVHAverageUse;
		}else{
			boundAttributeVHFillCount = boundAttributeVHFree;
			boundAttributeVHFree = 0;
		}		
		System.arraycopy(this.boundAttributeVH, boundAttributeVHFree, 
							boundAttributeVH, 0, boundAttributeVHFillCount);
        
        int boundCandidateAttributeVHFillCount;
		if(boundCandidateAttributeVH == null || boundCandidateAttributeVH.length < boundCandidateAttributeVHAverageUse)
			boundCandidateAttributeVH = new BoundCandidateAttributeValidationHandler[boundCandidateAttributeVHAverageUse];		
		if(boundCandidateAttributeVHFree > boundCandidateAttributeVHAverageUse){
			boundCandidateAttributeVHFillCount = boundCandidateAttributeVHAverageUse;
			boundCandidateAttributeVHFree = boundCandidateAttributeVHFree - boundCandidateAttributeVHAverageUse;
		}else{
			boundCandidateAttributeVHFillCount = boundCandidateAttributeVHFree;
			boundCandidateAttributeVHFree = 0;
		}		
		System.arraycopy(this.boundCandidateAttributeVH, boundCandidateAttributeVHFree, 
							boundCandidateAttributeVH, 0, boundCandidateAttributeVHFillCount);
		
		int boundAttributeConcurrentHFillCount;
		if(boundAttributeConcurrentH == null || boundAttributeConcurrentH.length < boundAttributeConcurrentHAverageUse)
			boundAttributeConcurrentH = new BoundAttributeConcurrentHandler[boundAttributeConcurrentHAverageUse];		
		if(boundAttributeConcurrentHFree > boundAttributeConcurrentHAverageUse){
			boundAttributeConcurrentHFillCount = boundAttributeConcurrentHAverageUse;
			boundAttributeConcurrentHFree = boundAttributeConcurrentHFree - boundAttributeConcurrentHAverageUse;
		}else{
			boundAttributeConcurrentHFillCount = boundAttributeConcurrentHFree;
			boundAttributeConcurrentHFree = 0;
		}		
		System.arraycopy(this.boundAttributeConcurrentH, boundAttributeConcurrentHFree, 
							boundAttributeConcurrentH, 0, boundAttributeConcurrentHFillCount);
		
		
        int boundAttributeParallelHFillCount;
		if(boundAttributeParallelH == null || boundAttributeParallelH.length < boundAttributeParallelHAverageUse)
			boundAttributeParallelH = new BoundAttributeParallelHandler[boundAttributeParallelHAverageUse];		
		if(boundAttributeParallelHFree > boundAttributeParallelHAverageUse){
			boundAttributeParallelHFillCount = boundAttributeParallelHAverageUse;
			boundAttributeParallelHFree = boundAttributeParallelHFree - boundAttributeParallelHAverageUse;
		}else{
			boundAttributeParallelHFillCount = boundAttributeParallelHFree;
			boundAttributeParallelHFree = 0;
		}		
		System.arraycopy(this.boundAttributeParallelH, boundAttributeParallelHFree, 
							boundAttributeParallelH, 0, boundAttributeParallelHFillCount);
		
        
		eventHandlerPool.setHandlers(elementVHFillCount,	
										elementVH,
										startVHFillCount,	
										startVH,
										unexpectedElementHFillCount,	
										unexpectedElementH,
										unexpectedAmbiguousEHFillCount,
										unexpectedAmbiguousEH,
										unknownElementHFillCount,	
										unknownElementH,
										defaultElementHFillCount,	
										defaultElementH,
										elementConcurrentHFillCount,
										elementConcurrentH,
										elementParallelHFillCount,
										elementParallelH,
										elementCommonHFillCount,
										elementCommonH,
										unexpectedAttributeHFillCount,	
										unexpectedAttributeH,
										unexpectedAmbiguousAHFillCount,
										unexpectedAmbiguousAH,
										unknownAttributeHFillCount,	
										unknownAttributeH,
										attributeVHFillCount,	
										attributeVH,
										candidateAttributeVHFillCount,	
										candidateAttributeVH,
										attributeConcurrentHFillCount,
										attributeConcurrentH,
										attributeParallelHFillCount,
										attributeParallelH,
                                        attributeDefaultHFillCount,
										attributeDefaultH,
										charactersValidationHFillCount,
										charactersValidationH,
										structuredDataValidationHFillCount,
										structuredDataValidationH,
										dataValidationHFillCount,
										dataValidationH,
                                        defaultVAttributeHFillCount,
										defaultVAttributeH,
										listPatternVHFillCount,
										listPatternVH,
										exceptPatternVHFillCount,
										exceptPatternVH,
										boundElementVHFillCount,	
										boundElementVH,
										boundStartVHFillCount,	
										boundStartVH,
										boundElementConcurrentHFillCount,
										boundElementConcurrentH,
										boundElementParallelHFillCount,
										boundElementParallelH,
										boundAttributeVHFillCount,	
										boundAttributeVH,
										boundCandidateAttributeVHFillCount,	
										boundCandidateAttributeVH,
										boundAttributeConcurrentHFillCount,
										boundAttributeConcurrentH,
                                        boundAttributeParallelHFillCount,
										boundAttributeParallelH);
	}
	
	synchronized void recycle(int elementVHAverageUse,	
							ElementValidationHandler[] elementVH,
							int startVHAverageUse,	
							StartValidationHandler[] startVH,
							int unexpectedElementHAverageUse,	
							UnexpectedElementHandler[] unexpectedElementH,
							int unexpectedAmbiguousEHAverageUse,
							UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEH,
							int unknownElementHAverageUse,	
							UnknownElementHandler[] unknownElementH,
							int defaultElementHAverageUse,	
							ElementDefaultHandler[] defaultElementH,
							int elementConcurrentHAverageUse,
							ElementConcurrentHandler[] elementConcurrentH,
							int elementParallelHAverageUse,
							ElementParallelHandler[] elementParallelH,
							int elementCommonHAverageUse,
							ElementCommonHandler[] elementCommonH,
							int unexpectedAttributeHAverageUse,	
							UnexpectedAttributeHandler[] unexpectedAttributeH,
							int unexpectedAmbiguousAHAverageUse,
							UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAH,
							int unknownAttributeHAverageUse,	
							UnknownAttributeHandler[] unknownAttributeH,
							int attributeVHAverageUse,	
							AttributeValidationHandler[] attributeVH,
							int candidateAttributeVHAverageUse,	
							CandidateAttributeValidationHandler[] candidateAttributeVH,
							int attributeConcurrentHAverageUse,
							AttributeConcurrentHandler[] attributeConcurrentH,
                            int attributeParallelHAverageUse,
							AttributeParallelHandler[] attributeParallelH,
                            int attributeDefaultHAverageUse,
							AttributeDefaultHandler[] attributeDefaultH,
							int charactersValidationHAverageUse,
							CharactersValidationHandler[] charactersValidationH,
							int structuredDataValidationHAverageUse,
							StructuredDataValidationHandler[] structuredDataValidationH,
							int dataValidationHAverageUse,
							DataValidationHandler[] dataValidationH,
                            int defaultVAttributeHAverageUse,
							DefaultValueAttributeValidationHandler[] defaultVAttributeH,
							int listPatternVHAverageUse,
							ListPatternValidationHandler[] listPatternVH,
							int exceptPatternVHAverageUse,
							ExceptPatternValidationHandler[] exceptPatternVH,
							int boundElementVHAverageUse,	
							BoundElementValidationHandler[] boundElementVH,
							int boundStartVHAverageUse,	
							BoundStartValidationHandler[] boundStartVH,
							int boundElementConcurrentHAverageUse,
							BoundElementConcurrentHandler[] boundElementConcurrentH,
							int boundElementParallelHAverageUse,
							BoundElementParallelHandler[] boundElementParallelH,
							int boundAttributeVHAverageUse,	
							BoundAttributeValidationHandler[] boundAttributeVH,
                            int boundCandidateAttributeVHAverageUse,	
							BoundCandidateAttributeValidationHandler[] boundCandidateAttributeVH,
							int boundAttributeConcurrentHAverageUse,
							BoundAttributeConcurrentHandler[] boundAttributeConcurrentH,
                            int boundAttributeParallelHAverageUse,
							BoundAttributeParallelHandler[] boundAttributeParallelH){			

		if(elementVHFree + elementVHAverageUse >= elementVHPoolSize){			 
			elementVHPoolSize+= elementVHAverageUse;
			ElementValidationHandler[] increased = new ElementValidationHandler[elementVHPoolSize];
			System.arraycopy(this.elementVH, 0, increased, 0, elementVHFree);
			this.elementVH = increased;
		}
		System.arraycopy(elementVH, 0, this.elementVH, elementVHFree, elementVHAverageUse);
		elementVHFree += elementVHAverageUse;
		if(this.elementVHAverageUse != 0)this.elementVHAverageUse = (this.elementVHAverageUse + elementVHAverageUse)/2;
		else this.elementVHAverageUse = elementVHAverageUse;
		// System.out.println("vh "+this.elementVHAverageUse);
		
		if(startVHFree + startVHAverageUse >= startVHPoolSize){			 
			startVHPoolSize+= startVHAverageUse;
			StartValidationHandler[] increased = new StartValidationHandler[startVHPoolSize];
			System.arraycopy(this.startVH, 0, increased, 0, startVHFree);
			this.startVH = increased;
		}
		System.arraycopy(startVH, 0, this.startVH, startVHFree, startVHAverageUse);
		startVHFree += startVHAverageUse;
		if(this.startVHAverageUse != 0)this.startVHAverageUse = (this.startVHAverageUse + startVHAverageUse)/2;
		else this.startVHAverageUse = startVHAverageUse;
		// System.out.println("vh "+this.startVHAverageUse);
		

		if(unexpectedElementHFree + unexpectedElementHAverageUse >= unexpectedElementHPoolSize){
			unexpectedElementHPoolSize+= unexpectedElementHAverageUse;
			UnexpectedElementHandler[] increased = new UnexpectedElementHandler[unexpectedElementHPoolSize];
			System.arraycopy(this.unexpectedElementH, 0, increased, 0, unexpectedElementHFree);
			this.unexpectedElementH = increased;
		}
		System.arraycopy(unexpectedElementH, 0, this.unexpectedElementH, unexpectedElementHFree, unexpectedElementHAverageUse);
		unexpectedElementHFree += unexpectedElementHAverageUse;
		if(this.unexpectedElementHAverageUse != 0)this.unexpectedElementHAverageUse = (this.unexpectedElementHAverageUse + unexpectedElementHAverageUse)/2;
		else this.unexpectedElementHAverageUse = unexpectedElementHAverageUse;
		// System.out.println("unexpected "+this.unexpectedElementHAverageUse);
		
		if(unexpectedAmbiguousEHFree + unexpectedAmbiguousEHAverageUse >= unexpectedAmbiguousEHPoolSize){
			unexpectedAmbiguousEHPoolSize += unexpectedAmbiguousEHAverageUse;
			UnexpectedAmbiguousElementHandler[] increased = new UnexpectedAmbiguousElementHandler[unexpectedAmbiguousEHPoolSize];
			System.arraycopy(this.unexpectedAmbiguousEH, 0, increased, 0, unexpectedAmbiguousEHFree);
			this.unexpectedAmbiguousEH = increased;
		}
		System.arraycopy(unexpectedAmbiguousEH, 0, this.unexpectedAmbiguousEH, unexpectedAmbiguousEHFree, unexpectedAmbiguousEHAverageUse);
		unexpectedAmbiguousEHFree += unexpectedAmbiguousEHAverageUse;
		if(this.unexpectedAmbiguousEHAverageUse != 0)this.unexpectedAmbiguousEHAverageUse = (this.unexpectedAmbiguousEHAverageUse + unexpectedAmbiguousEHAverageUse)/2;
		else this.unexpectedAmbiguousEHAverageUse = unexpectedAmbiguousEHAverageUse;
		// System.out.println("unexpectedAmbiguous "+this.unexpectedAmbiguousEHAverageUse);
		
		if(unknownElementHFree + unknownElementHAverageUse >= unknownElementHPoolSize){		
			unknownElementHPoolSize += unknownElementHAverageUse;
			UnknownElementHandler[] increased = new UnknownElementHandler[unknownElementHPoolSize];
			System.arraycopy(this.unknownElementH, 0, increased, 0, unknownElementHFree);
			this.unknownElementH = increased;
		}
		System.arraycopy(unknownElementH, 0, this.unknownElementH, unknownElementHFree, unknownElementHAverageUse);
		unknownElementHFree += unknownElementHAverageUse;
		if(this.unknownElementHAverageUse != 0)this.unknownElementHAverageUse = (this.unknownElementHAverageUse + unknownElementHAverageUse)/2;
		else this.unknownElementHAverageUse = unknownElementHAverageUse;
		// System.out.println("unknown "+this.unknownElementHAverageUse);
		
		if(defaultElementHFree + defaultElementHAverageUse >= defaultElementHPoolSize){			
			defaultElementHPoolSize += defaultElementHAverageUse;
			ElementDefaultHandler[] increased = new ElementDefaultHandler[defaultElementHPoolSize];
			System.arraycopy(this.defaultElementH, 0, increased, 0, defaultElementHFree);
			this.defaultElementH = increased;
		}
		System.arraycopy(defaultElementH, 0, this.defaultElementH, defaultElementHFree, defaultElementHAverageUse);
		defaultElementHFree += defaultElementHAverageUse;
		if(this.defaultElementHAverageUse != 0) this.defaultElementHAverageUse = (this.defaultElementHAverageUse + defaultElementHAverageUse)/2;
		else this.defaultElementHAverageUse = defaultElementHAverageUse;
		// System.out.println("default "+this.defaultElementHAverageUse);
		
		if(elementConcurrentHFree + elementConcurrentHAverageUse >= elementConcurrentHPoolSize){
			elementConcurrentHPoolSize += elementConcurrentHAverageUse;			
			ElementConcurrentHandler[] increased = new ElementConcurrentHandler[elementConcurrentHPoolSize];
			System.arraycopy(this.elementConcurrentH, 0, increased, 0, elementConcurrentHFree);
			this.elementConcurrentH = increased;
		}	
		System.arraycopy(elementConcurrentH, 0, this.elementConcurrentH, elementConcurrentHFree, elementConcurrentHAverageUse);
		elementConcurrentHFree += elementConcurrentHAverageUse;
		if(this.elementConcurrentHAverageUse != 0)this.elementConcurrentHAverageUse = (this.elementConcurrentHAverageUse + elementConcurrentHAverageUse)/2;
		else this.elementConcurrentHAverageUse = elementConcurrentHAverageUse;
		// System.out.println("concurrent "+this.elementConcurrentHAverageUse);		
		
		if(elementParallelHFree + elementParallelHAverageUse >= elementParallelHPoolSize){
			elementParallelHPoolSize += elementParallelHAverageUse;			
			ElementParallelHandler[] increased = new ElementParallelHandler[elementParallelHPoolSize];
			System.arraycopy(this.elementParallelH, 0, increased, 0, elementParallelHFree);
			this.elementParallelH = increased;
		}
		System.arraycopy(elementParallelH, 0, this.elementParallelH, elementParallelHFree, elementParallelHAverageUse);
		elementParallelHFree += elementParallelHAverageUse;
		if(this.elementParallelHAverageUse != 0)this.elementParallelHAverageUse = (this.elementParallelHAverageUse + elementParallelHAverageUse)/2;
		else this.elementParallelHAverageUse = elementParallelHAverageUse;
		// System.out.println("parallel "+this.elementParallelHAverageUse);
		
		if(elementCommonHFree + elementCommonHAverageUse >= elementCommonHPoolSize){
			elementCommonHPoolSize += elementCommonHAverageUse;			
			ElementCommonHandler[] increased = new ElementCommonHandler[elementCommonHPoolSize];
			System.arraycopy(this.elementCommonH, 0, increased, 0, elementCommonHFree);
			this.elementCommonH = increased;
		}
		System.arraycopy(elementCommonH, 0, this.elementCommonH, elementCommonHFree, elementCommonHAverageUse);
		elementCommonHFree += elementCommonHAverageUse;
		if(this.elementCommonHAverageUse != 0)this.elementCommonHAverageUse = (this.elementCommonHAverageUse + elementCommonHAverageUse)/2;
		else this.elementCommonHAverageUse = elementCommonHAverageUse;
		// System.out.println("common "+this.elementCommonHAverageUse);
				
		
		if(unexpectedAttributeHFree + unexpectedAttributeHAverageUse >= unexpectedAttributeHPoolSize){
			unexpectedAttributeHPoolSize+= unexpectedAttributeHAverageUse;
			UnexpectedAttributeHandler[] increased = new UnexpectedAttributeHandler[unexpectedAttributeHPoolSize];
			System.arraycopy(this.unexpectedAttributeH, 0, increased, 0, unexpectedAttributeHFree);
			this.unexpectedAttributeH = increased;
		}
		System.arraycopy(unexpectedAttributeH, 0, this.unexpectedAttributeH, unexpectedAttributeHFree, unexpectedAttributeHAverageUse);
		unexpectedAttributeHFree += unexpectedAttributeHAverageUse;
		if(this.unexpectedAttributeHAverageUse != 0)this.unexpectedAttributeHAverageUse = (this.unexpectedAttributeHAverageUse + unexpectedAttributeHAverageUse)/2;
		else this.unexpectedAttributeHAverageUse = unexpectedAttributeHAverageUse;
		// System.out.println("unexpected "+this.unexpectedAttributeHAverageUse);
		
		if(unexpectedAmbiguousAHFree + unexpectedAmbiguousAHAverageUse >= unexpectedAmbiguousAHPoolSize){
			unexpectedAmbiguousAHPoolSize += unexpectedAmbiguousAHAverageUse;
			UnexpectedAmbiguousAttributeHandler[] increased = new UnexpectedAmbiguousAttributeHandler[unexpectedAmbiguousAHPoolSize];
			System.arraycopy(this.unexpectedAmbiguousAH, 0, increased, 0, unexpectedAmbiguousAHFree);
			this.unexpectedAmbiguousAH = increased;
		}
		System.arraycopy(unexpectedAmbiguousAH, 0, this.unexpectedAmbiguousAH, unexpectedAmbiguousAHFree, unexpectedAmbiguousAHAverageUse);
		unexpectedAmbiguousAHFree += unexpectedAmbiguousAHAverageUse;
		if(this.unexpectedAmbiguousAHAverageUse != 0)this.unexpectedAmbiguousAHAverageUse = (this.unexpectedAmbiguousAHAverageUse + unexpectedAmbiguousAHAverageUse)/2;
		else this.unexpectedAmbiguousAHAverageUse = unexpectedAmbiguousAHAverageUse;
		// System.out.println("unexpectedAmbiguous "+this.unexpectedAmbiguousAHAverageUse);
		
		if(unknownAttributeHFree + unknownAttributeHAverageUse >= unknownAttributeHPoolSize){		
			unknownAttributeHPoolSize += unknownAttributeHAverageUse;
			UnknownAttributeHandler[] increased = new UnknownAttributeHandler[unknownAttributeHPoolSize];
			System.arraycopy(this.unknownAttributeH, 0, increased, 0, unknownAttributeHFree);
			this.unknownAttributeH = increased;
		}
		System.arraycopy(unknownAttributeH, 0, this.unknownAttributeH, unknownAttributeHFree, unknownAttributeHAverageUse);
		unknownAttributeHFree += unknownAttributeHAverageUse;
		if(this.unknownAttributeHAverageUse != 0)this.unknownAttributeHAverageUse = (this.unknownAttributeHAverageUse + unknownAttributeHAverageUse)/2;
		else this.unknownAttributeHAverageUse = unknownAttributeHAverageUse;
		// System.out.println("unknown "+this.unknownAttributeHAverageUse);
		
		if(attributeVHFree + attributeVHAverageUse >= attributeVHPoolSize){			 
			attributeVHPoolSize+= attributeVHAverageUse;
			AttributeValidationHandler[] increased = new AttributeValidationHandler[attributeVHPoolSize];
			System.arraycopy(this.attributeVH, 0, increased, 0, attributeVHFree);
			this.attributeVH = increased;
		}
		System.arraycopy(attributeVH, 0, this.attributeVH, attributeVHFree, attributeVHAverageUse);
		attributeVHFree += attributeVHAverageUse;
		if(this.attributeVHAverageUse != 0)this.attributeVHAverageUse = (this.attributeVHAverageUse + attributeVHAverageUse)/2;
		else this.attributeVHAverageUse = attributeVHAverageUse;
		// System.out.println("vh "+this.attributeVHAverageUse);
        
        if(candidateAttributeVHFree + candidateAttributeVHAverageUse >= candidateAttributeVHPoolSize){			 
			candidateAttributeVHPoolSize+= candidateAttributeVHAverageUse;
			CandidateAttributeValidationHandler[] increased = new CandidateAttributeValidationHandler[candidateAttributeVHPoolSize];
			System.arraycopy(this.candidateAttributeVH, 0, increased, 0, candidateAttributeVHFree);
			this.candidateAttributeVH = increased;
		}
		System.arraycopy(candidateAttributeVH, 0, this.candidateAttributeVH, candidateAttributeVHFree, candidateAttributeVHAverageUse);
		candidateAttributeVHFree += candidateAttributeVHAverageUse;
		if(this.candidateAttributeVHAverageUse != 0)this.candidateAttributeVHAverageUse = (this.candidateAttributeVHAverageUse + candidateAttributeVHAverageUse)/2;
		else this.candidateAttributeVHAverageUse = candidateAttributeVHAverageUse;
		// System.out.println("vh "+this.candidateAttributeVHAverageUse);
		
		if(attributeConcurrentHFree + attributeConcurrentHAverageUse >= attributeConcurrentHPoolSize){			 
			attributeConcurrentHPoolSize+= attributeConcurrentHAverageUse;
			AttributeConcurrentHandler[] increased = new AttributeConcurrentHandler[attributeConcurrentHPoolSize];
			System.arraycopy(this.attributeConcurrentH, 0, increased, 0, attributeConcurrentHFree);
			this.attributeConcurrentH = increased;
		}
		System.arraycopy(attributeConcurrentH, 0, this.attributeConcurrentH, attributeConcurrentHFree, attributeConcurrentHAverageUse);
		attributeConcurrentHFree += attributeConcurrentHAverageUse;
		if(this.attributeConcurrentHAverageUse != 0)this.attributeConcurrentHAverageUse = (this.attributeConcurrentHAverageUse + attributeConcurrentHAverageUse)/2;
		else this.attributeConcurrentHAverageUse = attributeConcurrentHAverageUse;
		// System.out.println("vh "+this.attributeConcurrentHAverageUse);
        
        if(attributeParallelHFree + attributeParallelHAverageUse >= attributeParallelHPoolSize){			 
			attributeParallelHPoolSize+= attributeParallelHAverageUse;
			AttributeParallelHandler[] increased = new AttributeParallelHandler[attributeParallelHPoolSize];
			System.arraycopy(this.attributeParallelH, 0, increased, 0, attributeParallelHFree);
			this.attributeParallelH = increased;
		}
		System.arraycopy(attributeParallelH, 0, this.attributeParallelH, attributeParallelHFree, attributeParallelHAverageUse);
		attributeParallelHFree += attributeParallelHAverageUse;
		if(this.attributeParallelHAverageUse != 0)this.attributeParallelHAverageUse = (this.attributeParallelHAverageUse + attributeParallelHAverageUse)/2;
		else this.attributeParallelHAverageUse = attributeParallelHAverageUse;
		// System.out.println("vh "+this.attributeParallelHAverageUse);
        
        if(attributeDefaultHFree + attributeDefaultHAverageUse >= attributeDefaultHPoolSize){			 
			attributeDefaultHPoolSize+= attributeDefaultHAverageUse;
			AttributeDefaultHandler[] increased = new AttributeDefaultHandler[attributeDefaultHPoolSize];
			System.arraycopy(this.attributeDefaultH, 0, increased, 0, attributeDefaultHFree);
			this.attributeDefaultH = increased;
		}
		System.arraycopy(attributeDefaultH, 0, this.attributeDefaultH, attributeDefaultHFree, attributeDefaultHAverageUse);
		attributeDefaultHFree += attributeDefaultHAverageUse;
		if(this.attributeDefaultHAverageUse != 0)this.attributeDefaultHAverageUse = (this.attributeDefaultHAverageUse + attributeDefaultHAverageUse)/2;
		else this.attributeDefaultHAverageUse = attributeDefaultHAverageUse;
		// System.out.println("vh "+this.attributeDefaultHAverageUse);
		
		if(charactersValidationHFree + charactersValidationHAverageUse >= charactersValidationHPoolSize){			 
			charactersValidationHPoolSize+= charactersValidationHAverageUse;
			CharactersValidationHandler[] increased = new CharactersValidationHandler[charactersValidationHPoolSize];
			System.arraycopy(this.charactersValidationH, 0, increased, 0, charactersValidationHFree);
			this.charactersValidationH = increased;
		}
		System.arraycopy(charactersValidationH, 0, this.charactersValidationH, charactersValidationHFree, charactersValidationHAverageUse);
		charactersValidationHFree += charactersValidationHAverageUse;
		if(this.charactersValidationHAverageUse != 0)this.charactersValidationHAverageUse = (this.charactersValidationHAverageUse + charactersValidationHAverageUse)/2;
		else this.charactersValidationHAverageUse = charactersValidationHAverageUse;
		// System.out.println("vh "+this.charactersValidationHAverageUse);
		
		if(structuredDataValidationHFree + structuredDataValidationHAverageUse >= structuredDataValidationHPoolSize){			 
			structuredDataValidationHPoolSize+= structuredDataValidationHAverageUse;
			StructuredDataValidationHandler[] increased = new StructuredDataValidationHandler[structuredDataValidationHPoolSize];
			System.arraycopy(this.structuredDataValidationH, 0, increased, 0, structuredDataValidationHFree);
			this.structuredDataValidationH = increased;
		}
		System.arraycopy(structuredDataValidationH, 0, this.structuredDataValidationH, structuredDataValidationHFree, structuredDataValidationHAverageUse);
		structuredDataValidationHFree += structuredDataValidationHAverageUse;
		if(this.structuredDataValidationHAverageUse != 0)this.structuredDataValidationHAverageUse = (this.structuredDataValidationHAverageUse + structuredDataValidationHAverageUse)/2;
		else this.structuredDataValidationHAverageUse = structuredDataValidationHAverageUse;
		// System.out.println("vh "+this.structuredDataValidationHAverageUse);
        
		if(dataValidationHFree + dataValidationHAverageUse >= dataValidationHPoolSize){			 
			dataValidationHPoolSize+= dataValidationHAverageUse;
			DataValidationHandler[] increased = new DataValidationHandler[dataValidationHPoolSize];
			System.arraycopy(this.dataValidationH, 0, increased, 0, dataValidationHFree);
			this.dataValidationH = increased;
		}
		System.arraycopy(dataValidationH, 0, this.dataValidationH, dataValidationHFree, dataValidationHAverageUse);
		dataValidationHFree += dataValidationHAverageUse;
		if(this.dataValidationHAverageUse != 0)this.dataValidationHAverageUse = (this.dataValidationHAverageUse + dataValidationHAverageUse)/2;
		else this.dataValidationHAverageUse = dataValidationHAverageUse;
		// System.out.println("vh "+this.dataValidationHAverageUse);
        
        
        if(defaultVAttributeHFree + defaultVAttributeHAverageUse >= defaultVAttributeHPoolSize){			 
			defaultVAttributeHPoolSize+= defaultVAttributeHAverageUse;
			DefaultValueAttributeValidationHandler[] increased = new DefaultValueAttributeValidationHandler[defaultVAttributeHPoolSize];
			System.arraycopy(this.defaultVAttributeH, 0, increased, 0, defaultVAttributeHFree);
			this.defaultVAttributeH = increased;
		}
		System.arraycopy(defaultVAttributeH, 0, this.defaultVAttributeH, defaultVAttributeHFree, defaultVAttributeHAverageUse);
		defaultVAttributeHFree += defaultVAttributeHAverageUse;
		if(this.defaultVAttributeHAverageUse != 0)this.defaultVAttributeHAverageUse = (this.defaultVAttributeHAverageUse + defaultVAttributeHAverageUse)/2;
		else this.defaultVAttributeHAverageUse = defaultVAttributeHAverageUse;
		// System.out.println("vh "+this.defaultVAttributeHAverageUse);
		
		
		if(listPatternVHFree + listPatternVHAverageUse >= listPatternVHPoolSize){			 
			listPatternVHPoolSize+= listPatternVHAverageUse;
			ListPatternValidationHandler[] increased = new ListPatternValidationHandler[listPatternVHPoolSize];
			System.arraycopy(this.listPatternVH, 0, increased, 0, listPatternVHFree);
			this.listPatternVH = increased;
		}
		System.arraycopy(listPatternVH, 0, this.listPatternVH, listPatternVHFree, listPatternVHAverageUse);
		listPatternVHFree += listPatternVHAverageUse;
		if(this.listPatternVHAverageUse != 0)this.listPatternVHAverageUse = (this.listPatternVHAverageUse + listPatternVHAverageUse)/2;
		else this.listPatternVHAverageUse = listPatternVHAverageUse;
		// System.out.println("vh "+this.listPatternVHAverageUse);
		
		if(exceptPatternVHFree + exceptPatternVHAverageUse >= exceptPatternVHPoolSize){			 
			exceptPatternVHPoolSize+= exceptPatternVHAverageUse;
			ExceptPatternValidationHandler[] increased = new ExceptPatternValidationHandler[exceptPatternVHPoolSize];
			System.arraycopy(this.exceptPatternVH, 0, increased, 0, exceptPatternVHFree);
			this.exceptPatternVH = increased;
		}
		System.arraycopy(exceptPatternVH, 0, this.exceptPatternVH, exceptPatternVHFree, exceptPatternVHAverageUse);
		exceptPatternVHFree += exceptPatternVHAverageUse;
		if(this.exceptPatternVHAverageUse != 0)this.exceptPatternVHAverageUse = (this.exceptPatternVHAverageUse + exceptPatternVHAverageUse)/2;
		else this.exceptPatternVHAverageUse = exceptPatternVHAverageUse;
		// System.out.println("vh "+this.exceptPatternVHAverageUse);
		
		
		
		if(boundElementVHFree + boundElementVHAverageUse >= boundElementVHPoolSize){			 
			boundElementVHPoolSize+= boundElementVHAverageUse;
			BoundElementValidationHandler[] increased = new BoundElementValidationHandler[boundElementVHPoolSize];
			System.arraycopy(this.boundElementVH, 0, increased, 0, boundElementVHFree);
			this.boundElementVH = increased;
		}
		System.arraycopy(boundElementVH, 0, this.boundElementVH, boundElementVHFree, boundElementVHAverageUse);
		boundElementVHFree += boundElementVHAverageUse;
		if(this.boundElementVHAverageUse != 0)this.boundElementVHAverageUse = (this.boundElementVHAverageUse + boundElementVHAverageUse)/2;
		else this.boundElementVHAverageUse = boundElementVHAverageUse;
		// System.out.println("vh "+this.boundElementVHAverageUse);
		
		if(boundStartVHFree + boundStartVHAverageUse >= boundStartVHPoolSize){			 
			boundStartVHPoolSize+= boundStartVHAverageUse;
			BoundStartValidationHandler[] increased = new BoundStartValidationHandler[boundStartVHPoolSize];
			System.arraycopy(this.boundStartVH, 0, increased, 0, boundStartVHFree);
			this.boundStartVH = increased;
		}
		System.arraycopy(boundStartVH, 0, this.boundStartVH, boundStartVHFree, boundStartVHAverageUse);
		boundStartVHFree += boundStartVHAverageUse;
		if(this.boundStartVHAverageUse != 0)this.boundStartVHAverageUse = (this.boundStartVHAverageUse + boundStartVHAverageUse)/2;
		else this.boundStartVHAverageUse = boundStartVHAverageUse;
		// System.out.println("vh "+this.boundStartVHAverageUse);
		
		if(boundElementConcurrentHFree + boundElementConcurrentHAverageUse >= boundElementConcurrentHPoolSize){
			boundElementConcurrentHPoolSize += boundElementConcurrentHAverageUse;			
			BoundElementConcurrentHandler[] increased = new BoundElementConcurrentHandler[boundElementConcurrentHPoolSize];
			System.arraycopy(this.boundElementConcurrentH, 0, increased, 0, boundElementConcurrentHFree);
			this.boundElementConcurrentH = increased;
		}	
		System.arraycopy(boundElementConcurrentH, 0, this.boundElementConcurrentH, boundElementConcurrentHFree, boundElementConcurrentHAverageUse);
		boundElementConcurrentHFree += boundElementConcurrentHAverageUse;
		if(this.boundElementConcurrentHAverageUse != 0)this.boundElementConcurrentHAverageUse = (this.boundElementConcurrentHAverageUse + boundElementConcurrentHAverageUse)/2;
		else this.boundElementConcurrentHAverageUse = boundElementConcurrentHAverageUse;
		// System.out.println("concurrent "+this.boundElementConcurrentHAverageUse);	
		
		if(boundElementParallelHFree + boundElementParallelHAverageUse >= boundElementParallelHPoolSize){
			boundElementParallelHPoolSize += boundElementParallelHAverageUse;			
			BoundElementParallelHandler[] increased = new BoundElementParallelHandler[boundElementParallelHPoolSize];
			System.arraycopy(this.boundElementParallelH, 0, increased, 0, boundElementParallelHFree);
			this.boundElementParallelH = increased;
		}
		System.arraycopy(boundElementParallelH, 0, this.boundElementParallelH, boundElementParallelHFree, boundElementParallelHAverageUse);
		boundElementParallelHFree += boundElementParallelHAverageUse;
		if(this.boundElementParallelHAverageUse != 0)this.boundElementParallelHAverageUse = (this.boundElementParallelHAverageUse + boundElementParallelHAverageUse)/2;
		else this.boundElementParallelHAverageUse = boundElementParallelHAverageUse;
		// System.out.println("parallel "+this.boundElementParallelHAverageUse);
		
		if(boundAttributeVHFree + boundAttributeVHAverageUse >= boundAttributeVHPoolSize){			 
			boundAttributeVHPoolSize+= boundAttributeVHAverageUse;
			BoundAttributeValidationHandler[] increased = new BoundAttributeValidationHandler[boundAttributeVHPoolSize];
			System.arraycopy(this.boundAttributeVH, 0, increased, 0, boundAttributeVHFree);
			this.boundAttributeVH = increased;
		}
		System.arraycopy(boundAttributeVH, 0, this.boundAttributeVH, boundAttributeVHFree, boundAttributeVHAverageUse);
		boundAttributeVHFree += boundAttributeVHAverageUse;
		if(this.boundAttributeVHAverageUse != 0)this.boundAttributeVHAverageUse = (this.boundAttributeVHAverageUse + boundAttributeVHAverageUse)/2;
		else this.boundAttributeVHAverageUse = boundAttributeVHAverageUse;
		// System.out.println("vh "+this.boundAttributeVHAverageUse);
        
        if(boundCandidateAttributeVHFree + boundCandidateAttributeVHAverageUse >= boundCandidateAttributeVHPoolSize){			 
			boundCandidateAttributeVHPoolSize+= boundCandidateAttributeVHAverageUse;
			BoundCandidateAttributeValidationHandler[] increased = new BoundCandidateAttributeValidationHandler[boundCandidateAttributeVHPoolSize];
			System.arraycopy(this.boundCandidateAttributeVH, 0, increased, 0, boundCandidateAttributeVHFree);
			this.boundCandidateAttributeVH = increased;
		}
		System.arraycopy(boundCandidateAttributeVH, 0, this.boundCandidateAttributeVH, boundCandidateAttributeVHFree, boundCandidateAttributeVHAverageUse);
		boundCandidateAttributeVHFree += boundCandidateAttributeVHAverageUse;
		if(this.boundCandidateAttributeVHAverageUse != 0)this.boundCandidateAttributeVHAverageUse = (this.boundCandidateAttributeVHAverageUse + boundCandidateAttributeVHAverageUse)/2;
		else this.boundCandidateAttributeVHAverageUse = boundCandidateAttributeVHAverageUse;
		// System.out.println("vh "+this.boundCandidateAttributeVHAverageUse);
		
		if(boundAttributeConcurrentHFree + boundAttributeConcurrentHAverageUse >= boundAttributeConcurrentHPoolSize){			 
			boundAttributeConcurrentHPoolSize+= boundAttributeConcurrentHAverageUse;
			BoundAttributeConcurrentHandler[] increased = new BoundAttributeConcurrentHandler[boundAttributeConcurrentHPoolSize];
			System.arraycopy(this.boundAttributeConcurrentH, 0, increased, 0, boundAttributeConcurrentHFree);
			this.boundAttributeConcurrentH = increased;
		}
		System.arraycopy(boundAttributeConcurrentH, 0, this.boundAttributeConcurrentH, boundAttributeConcurrentHFree, boundAttributeConcurrentHAverageUse);
		boundAttributeConcurrentHFree += boundAttributeConcurrentHAverageUse;
		if(this.boundAttributeConcurrentHAverageUse != 0)this.boundAttributeConcurrentHAverageUse = (this.boundAttributeConcurrentHAverageUse + boundAttributeConcurrentHAverageUse)/2;
		else this.boundAttributeConcurrentHAverageUse = boundAttributeConcurrentHAverageUse;
		// System.out.println("vh "+this.boundAttributeConcurrentHAverageUse);
        
        if(boundAttributeParallelHFree + boundAttributeParallelHAverageUse >= boundAttributeParallelHPoolSize){			 
			boundAttributeParallelHPoolSize+= boundAttributeParallelHAverageUse;
			BoundAttributeParallelHandler[] increased = new BoundAttributeParallelHandler[boundAttributeParallelHPoolSize];
			System.arraycopy(this.boundAttributeParallelH, 0, increased, 0, boundAttributeParallelHFree);
			this.boundAttributeParallelH = increased;
		}
		System.arraycopy(boundAttributeParallelH, 0, this.boundAttributeParallelH, boundAttributeParallelHFree, boundAttributeParallelHAverageUse);
		boundAttributeParallelHFree += boundAttributeParallelHAverageUse;
		if(this.boundAttributeParallelHAverageUse != 0)this.boundAttributeParallelHAverageUse = (this.boundAttributeParallelHAverageUse + boundAttributeParallelHAverageUse)/2;
		else this.boundAttributeParallelHAverageUse = boundAttributeParallelHAverageUse;
		// System.out.println("vh "+this.boundAttributeParallelHAverageUse);
		
	}
}