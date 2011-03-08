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
	
	int unrecognizedElementHAverageUse;
	int unrecognizedElementHPoolSize;
	int unrecognizedElementHFree;	
	UnrecognizedElementHandler[] unrecognizedElementH;
	
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
	
	int unrecognizedAttributeHAverageUse;
	int unrecognizedAttributeHPoolSize;
	int unrecognizedAttributeHFree;	
	UnrecognizedAttributeHandler[] unrecognizedAttributeH;
	
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
		
	int attributeConcurrentHAverageUse;
	int attributeConcurrentHPoolSize;
	int attributeConcurrentHFree;
	AttributeConcurrentHandler[] attributeConcurrentH;
	
	int characterContentHAverageUse;
	int characterContentHPoolSize;
	int characterContentHFree;
	CharacterContentValidationHandler[] characterContentH;
	
	int attributeValueHAverageUse;
	int attributeValueHPoolSize;
	int attributeValueHFree;
	AttributeValueValidationHandler[] attributeValueH;
    
    int defaultVAttributeHAverageUse;
	int defaultVAttributeHPoolSize;
	int defaultVAttributeHFree;
	DefaultValueAttributeValidationHandler[] defaultVAttributeH;
		
	int listPatternTAverageUse;
	int listPatternTPoolSize;
	int listPatternTFree;
	ListPatternTester[] listPatternT;
	
	int exceptPatternTAverageUse;
	int exceptPatternTPoolSize;
	int exceptPatternTFree;
	ExceptPatternTester[] exceptPatternT;
	
	
	
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
		
	int boundAttributeConcurrentHAverageUse;
	int boundAttributeConcurrentHPoolSize;
	int boundAttributeConcurrentHFree;
	BoundAttributeConcurrentHandler[] boundAttributeConcurrentH;
	
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
		
		unrecognizedElementHAverageUse = UNUSED;
		unrecognizedElementHPoolSize = 3;
		unrecognizedElementHFree = 0;
		unrecognizedElementH = new UnrecognizedElementHandler[unrecognizedElementHPoolSize];
	
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
		
		unrecognizedAttributeHAverageUse = UNUSED;
		unrecognizedAttributeHPoolSize = 3;
		unrecognizedAttributeHFree = 0;
		unrecognizedAttributeH = new UnrecognizedAttributeHandler[unrecognizedAttributeHPoolSize];
	
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
		
		attributeConcurrentHAverageUse = UNUSED;
		attributeConcurrentHPoolSize = 1;
		attributeConcurrentHFree = 0;
		attributeConcurrentH = new AttributeConcurrentHandler[attributeConcurrentHPoolSize];
		
		characterContentHAverageUse = UNUSED;
		characterContentHPoolSize = 1;
		characterContentHFree = 0;
		characterContentH = new CharacterContentValidationHandler[characterContentHPoolSize];
		
		attributeValueHAverageUse = UNUSED;
		attributeValueHPoolSize = 1;
		attributeValueHFree = 0;
		attributeValueH = new AttributeValueValidationHandler[attributeValueHPoolSize];
        
        defaultVAttributeHAverageUse = UNUSED;
		defaultVAttributeHPoolSize = 1;
		defaultVAttributeHFree = 0;
		defaultVAttributeH = new DefaultValueAttributeValidationHandler[defaultVAttributeHPoolSize];
		
		listPatternTAverageUse = UNUSED;
		listPatternTPoolSize = 1;
		listPatternTFree = 0;
		listPatternT = new ListPatternTester[listPatternTPoolSize];
		
		exceptPatternTAverageUse = UNUSED;
		exceptPatternTPoolSize = 1;
		exceptPatternTFree = 0;
		exceptPatternT = new ExceptPatternTester[exceptPatternTPoolSize];
		
		
		
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
		
		boundAttributeConcurrentHAverageUse = UNUSED;
		boundAttributeConcurrentHPoolSize = 1;
		boundAttributeConcurrentHFree = 0;
		boundAttributeConcurrentH = new BoundAttributeConcurrentHandler[boundAttributeConcurrentHPoolSize];
		
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
					UnrecognizedElementHandler[] unrecognizedElementH,
					UnexpectedElementHandler[] unexpectedElementH,
					UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEH,
					UnknownElementHandler[] unknownElementH,
					ElementDefaultHandler[] defaultElementH,
					ElementConcurrentHandler[] elementConcurrentH,
					ElementParallelHandler[] elementParallelH,
					ElementCommonHandler[] elementCommonH,
					UnrecognizedAttributeHandler[] unrecognizedAttributeH,
					UnexpectedAttributeHandler[] unexpectedAttributeH,
					UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAH,
					UnknownAttributeHandler[] unknownAttributeH,
					AttributeValidationHandler[] attributeVH,
					AttributeConcurrentHandler[] attributeConcurrentH,
					CharacterContentValidationHandler[] characterContentH,					
					AttributeValueValidationHandler[] attributeValueH,
                    DefaultValueAttributeValidationHandler[] defaultVAttributeH,
					ListPatternTester[] listPatternT,
					ExceptPatternTester[] exceptPatternT,
					BoundElementValidationHandler[] boundElementVH,
					BoundStartValidationHandler[] boundStartVH,
					BoundElementConcurrentHandler[] boundElementConcurrentH,
					BoundElementParallelHandler[] boundElementParallelH,
					BoundAttributeValidationHandler[] boundAttributeVH,
					BoundAttributeConcurrentHandler[] boundAttributeConcurrentH){
	
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
		

		int unrecognizedElementHFillCount;
		if(unrecognizedElementH == null || unrecognizedElementH.length < unrecognizedElementHAverageUse)
			unrecognizedElementH = new UnrecognizedElementHandler[unrecognizedElementHAverageUse];		
		if(unrecognizedElementHFree > unrecognizedElementHAverageUse){
			unrecognizedElementHFillCount = unrecognizedElementHAverageUse;
			unrecognizedElementHFree = unrecognizedElementHFree - unrecognizedElementHAverageUse;
		}else{
			unrecognizedElementHFillCount = unrecognizedElementHFree;
			unrecognizedElementHFree = 0;
		}		
		System.arraycopy(this.unrecognizedElementH, unrecognizedElementHFree, 
							unrecognizedElementH, 0, unrecognizedElementHFillCount);
		
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
		
		int unrecognizedAttributeHFillCount;
		if(unrecognizedAttributeH == null || unrecognizedAttributeH.length < unrecognizedAttributeHAverageUse)
			unrecognizedAttributeH = new UnrecognizedAttributeHandler[unrecognizedAttributeHAverageUse];		
		if(unrecognizedAttributeHFree > unrecognizedAttributeHAverageUse){
			unrecognizedAttributeHFillCount = unrecognizedAttributeHAverageUse;
			unrecognizedAttributeHFree = unrecognizedAttributeHFree - unrecognizedAttributeHAverageUse;
		}else{
			unrecognizedAttributeHFillCount = unrecognizedAttributeHFree;
			unrecognizedAttributeHFree = 0;
		}		
		System.arraycopy(this.unrecognizedAttributeH, unrecognizedAttributeHFree, 
							unrecognizedAttributeH, 0, unrecognizedAttributeHFillCount);
		
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
		
		int characterContentHFillCount;
		if(characterContentH == null || characterContentH.length < characterContentHAverageUse)
			characterContentH = new CharacterContentValidationHandler[characterContentHAverageUse];		
		if(characterContentHFree > characterContentHAverageUse){
			characterContentHFillCount = characterContentHAverageUse;
			characterContentHFree = characterContentHFree - characterContentHAverageUse;
		}else{
			characterContentHFillCount = characterContentHFree;
			characterContentHFree = 0;
		}		
		System.arraycopy(this.characterContentH, characterContentHFree, 
							characterContentH, 0, characterContentHFillCount);
		
		int attributeValueHFillCount;
		if(attributeValueH == null || attributeValueH.length < attributeValueHAverageUse)
			attributeValueH = new AttributeValueValidationHandler[attributeValueHAverageUse];		
		if(attributeValueHFree > attributeValueHAverageUse){
			attributeValueHFillCount = attributeValueHAverageUse;
			attributeValueHFree = attributeValueHFree - attributeValueHAverageUse;
		}else{
			attributeValueHFillCount = attributeValueHFree;
			attributeValueHFree = 0;
		}		
		System.arraycopy(this.attributeValueH, attributeValueHFree, 
							attributeValueH, 0, attributeValueHFillCount);
        
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
		
		int listPatternTFillCount;
		if(listPatternT == null || listPatternT.length < listPatternTAverageUse)
			listPatternT = new ListPatternTester[listPatternTAverageUse];		
		if(listPatternTFree > listPatternTAverageUse){
			listPatternTFillCount = listPatternTAverageUse;
			listPatternTFree = listPatternTFree - listPatternTAverageUse;
		}else{
			listPatternTFillCount = listPatternTFree;
			listPatternTFree = 0;
		}		
		System.arraycopy(this.listPatternT, listPatternTFree, 
							listPatternT, 0, listPatternTFillCount);
		
		int exceptPatternTFillCount;
		if(exceptPatternT == null || exceptPatternT.length < exceptPatternTAverageUse)
			exceptPatternT = new ExceptPatternTester[exceptPatternTAverageUse];		
		if(exceptPatternTFree > exceptPatternTAverageUse){
			exceptPatternTFillCount = exceptPatternTAverageUse;
			exceptPatternTFree = exceptPatternTFree - exceptPatternTAverageUse;
		}else{
			exceptPatternTFillCount = exceptPatternTFree;
			exceptPatternTFree = 0;
		}		
		System.arraycopy(this.exceptPatternT, exceptPatternTFree, 
							exceptPatternT, 0, exceptPatternTFillCount);
		
		
		
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
		
		
		eventHandlerPool.setHandlers(elementVHFillCount,	
										elementVH,
										startVHFillCount,	
										startVH,
										unrecognizedElementHFillCount,	
										unrecognizedElementH,
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
										unrecognizedAttributeHFillCount,	
										unrecognizedAttributeH,
										unexpectedAttributeHFillCount,	
										unexpectedAttributeH,
										unexpectedAmbiguousAHFillCount,
										unexpectedAmbiguousAH,
										unknownAttributeHFillCount,	
										unknownAttributeH,
										attributeVHFillCount,	
										attributeVH,
										attributeConcurrentHFillCount,
										attributeConcurrentH,
										characterContentHFillCount,
										characterContentH,
										attributeValueHFillCount,
										attributeValueH,
                                        defaultVAttributeHFillCount,
										defaultVAttributeH,
										listPatternTFillCount,
										listPatternT,
										exceptPatternTFillCount,
										exceptPatternT,
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
										boundAttributeConcurrentHFillCount,
										boundAttributeConcurrentH);
	}
	
	synchronized void recycle(int elementVHAverageUse,	
							ElementValidationHandler[] elementVH,
							int startVHAverageUse,	
							StartValidationHandler[] startVH,
							int unrecognizedElementHAverageUse,	
							UnrecognizedElementHandler[] unrecognizedElementH,
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
							int unrecognizedAttributeHAverageUse,	
							UnrecognizedAttributeHandler[] unrecognizedAttributeH,
							int unexpectedAttributeHAverageUse,	
							UnexpectedAttributeHandler[] unexpectedAttributeH,
							int unexpectedAmbiguousAHAverageUse,
							UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAH,
							int unknownAttributeHAverageUse,	
							UnknownAttributeHandler[] unknownAttributeH,
							int attributeVHAverageUse,	
							AttributeValidationHandler[] attributeVH,
							int attributeConcurrentHAverageUse,
							AttributeConcurrentHandler[] attributeConcurrentH,
							int characterContentHAverageUse,
							CharacterContentValidationHandler[] characterContentH,
							int attributeValueHAverageUse,
							AttributeValueValidationHandler[] attributeValueH,
                            int defaultVAttributeHAverageUse,
							DefaultValueAttributeValidationHandler[] defaultVAttributeH,
							int listPatternTAverageUse,
							ListPatternTester[] listPatternT,
							int exceptPatternTAverageUse,
							ExceptPatternTester[] exceptPatternT,
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
							int boundAttributeConcurrentHAverageUse,
							BoundAttributeConcurrentHandler[] boundAttributeConcurrentH){			

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
		
		if(unrecognizedElementHFree + unrecognizedElementHAverageUse >= unrecognizedElementHPoolSize){
			unrecognizedElementHPoolSize+= unrecognizedElementHAverageUse;
			UnrecognizedElementHandler[] increased = new UnrecognizedElementHandler[unrecognizedElementHPoolSize];
			System.arraycopy(this.unrecognizedElementH, 0, increased, 0, unrecognizedElementHFree);
			this.unrecognizedElementH = increased;
		}
		System.arraycopy(unrecognizedElementH, 0, this.unrecognizedElementH, unrecognizedElementHFree, unrecognizedElementHAverageUse);
		unrecognizedElementHFree += unrecognizedElementHAverageUse;
		if(this.unrecognizedElementHAverageUse != 0)this.unrecognizedElementHAverageUse = (this.unrecognizedElementHAverageUse + unrecognizedElementHAverageUse)/2;
		else this.unrecognizedElementHAverageUse = unrecognizedElementHAverageUse;
		// System.out.println("unrecognized "+this.unrecognizedElementHAverageUse);
		
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
		
		
		if(unrecognizedAttributeHFree + unrecognizedAttributeHAverageUse >= unrecognizedAttributeHPoolSize){
			unrecognizedAttributeHPoolSize+= unrecognizedAttributeHAverageUse;
			UnrecognizedAttributeHandler[] increased = new UnrecognizedAttributeHandler[unrecognizedAttributeHPoolSize];
			System.arraycopy(this.unrecognizedAttributeH, 0, increased, 0, unrecognizedAttributeHFree);
			this.unrecognizedAttributeH = increased;
		}
		System.arraycopy(unrecognizedAttributeH, 0, this.unrecognizedAttributeH, unrecognizedAttributeHFree, unrecognizedAttributeHAverageUse);
		unrecognizedAttributeHFree += unrecognizedAttributeHAverageUse;
		if(this.unrecognizedAttributeHAverageUse != 0)this.unrecognizedAttributeHAverageUse = (this.unrecognizedAttributeHAverageUse + unrecognizedAttributeHAverageUse)/2;
		else this.unrecognizedAttributeHAverageUse = unrecognizedAttributeHAverageUse;
		// System.out.println("unrecognized "+this.unrecognizedAttributeHAverageUse);
		
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
		
		if(characterContentHFree + characterContentHAverageUse >= characterContentHPoolSize){			 
			characterContentHPoolSize+= characterContentHAverageUse;
			CharacterContentValidationHandler[] increased = new CharacterContentValidationHandler[characterContentHPoolSize];
			System.arraycopy(this.characterContentH, 0, increased, 0, characterContentHFree);
			this.characterContentH = increased;
		}
		System.arraycopy(characterContentH, 0, this.characterContentH, characterContentHFree, characterContentHAverageUse);
		characterContentHFree += characterContentHAverageUse;
		if(this.characterContentHAverageUse != 0)this.characterContentHAverageUse = (this.characterContentHAverageUse + characterContentHAverageUse)/2;
		else this.characterContentHAverageUse = characterContentHAverageUse;
		// System.out.println("vh "+this.characterContentHAverageUse);
		
		if(attributeValueHFree + attributeValueHAverageUse >= attributeValueHPoolSize){			 
			attributeValueHPoolSize+= attributeValueHAverageUse;
			AttributeValueValidationHandler[] increased = new AttributeValueValidationHandler[attributeValueHPoolSize];
			System.arraycopy(this.attributeValueH, 0, increased, 0, attributeValueHFree);
			this.attributeValueH = increased;
		}
		System.arraycopy(attributeValueH, 0, this.attributeValueH, attributeValueHFree, attributeValueHAverageUse);
		attributeValueHFree += attributeValueHAverageUse;
		if(this.attributeValueHAverageUse != 0)this.attributeValueHAverageUse = (this.attributeValueHAverageUse + attributeValueHAverageUse)/2;
		else this.attributeValueHAverageUse = attributeValueHAverageUse;
		// System.out.println("vh "+this.attributeValueHAverageUse);
        
        
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
		
		
		if(listPatternTFree + listPatternTAverageUse >= listPatternTPoolSize){			 
			listPatternTPoolSize+= listPatternTAverageUse;
			ListPatternTester[] increased = new ListPatternTester[listPatternTPoolSize];
			System.arraycopy(this.listPatternT, 0, increased, 0, listPatternTFree);
			this.listPatternT = increased;
		}
		System.arraycopy(listPatternT, 0, this.listPatternT, listPatternTFree, listPatternTAverageUse);
		listPatternTFree += listPatternTAverageUse;
		if(this.listPatternTAverageUse != 0)this.listPatternTAverageUse = (this.listPatternTAverageUse + listPatternTAverageUse)/2;
		else this.listPatternTAverageUse = listPatternTAverageUse;
		// System.out.println("vh "+this.listPatternTAverageUse);
		
		if(exceptPatternTFree + exceptPatternTAverageUse >= exceptPatternTPoolSize){			 
			exceptPatternTPoolSize+= exceptPatternTAverageUse;
			ExceptPatternTester[] increased = new ExceptPatternTester[exceptPatternTPoolSize];
			System.arraycopy(this.exceptPatternT, 0, increased, 0, exceptPatternTFree);
			this.exceptPatternT = increased;
		}
		System.arraycopy(exceptPatternT, 0, this.exceptPatternT, exceptPatternTFree, exceptPatternTAverageUse);
		exceptPatternTFree += exceptPatternTAverageUse;
		if(this.exceptPatternTAverageUse != 0)this.exceptPatternTAverageUse = (this.exceptPatternTAverageUse + exceptPatternTAverageUse)/2;
		else this.exceptPatternTAverageUse = exceptPatternTAverageUse;
		// System.out.println("vh "+this.exceptPatternTAverageUse);
		
		
		
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
		
	}
}