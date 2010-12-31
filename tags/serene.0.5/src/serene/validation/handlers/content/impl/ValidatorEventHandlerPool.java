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

import java.util.List;

import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AData;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.content.MarkupEventHandler;
import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.ErrorCatcher;

import serene.bind.BindingModel;
import serene.bind.ValidatorQueuePool;
import serene.bind.Queue;

import serene.Reusable;

import serene.util.SpaceCharsHandler;

import sereneWrite.MessageWriter;

public class ValidatorEventHandlerPool implements Reusable{
	ContentHandlerPool contentHandlerPool;
	
	SpaceCharsHandler spaceHandler;
	MatchHandler matchHandler;
	ValidationItemLocator validationItemLocator;
	ValidatorErrorHandlerPool errorHandlerPool;
	ValidationContext validationContext;
	 
	// Free is the number of handlers available in the pool.	
	// PoolSize is the size of the array used for holding the handlers. 
	int elementVHPoolSize;
	int elementVHFree = 0;	
	ElementValidationHandler[] elementVH;
	
	int startVHPoolSize;
	int startVHFree = 0;	
	StartValidationHandler[] startVH;
		
	int unrecognizedElementHPoolSize;
	int unrecognizedElementHFree = 0;	
	UnrecognizedElementHandler[] unrecognizedElementH;
	
	int unexpectedElementHPoolSize;
	int unexpectedElementHFree = 0;	
	UnexpectedElementHandler[] unexpectedElementH;
	
	int unexpectedAmbiguousEHPoolSize;
	int unexpectedAmbiguousEHFree = 0;
	UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEH;
	
	int unknownElementHPoolSize;
	int unknownElementHFree = 0;	
	UnknownElementHandler[] unknownElementH;
	
	int defaultElementHPoolSize;
	int defaultElementHFree = 0;	
	ElementDefaultHandler[] defaultElementH;
	
	int elementConcurrentHPoolSize;
	int elementConcurrentHFree = 0;
	ElementConcurrentHandler[] elementConcurrentH;
	
	int elementParallelHPoolSize;
	int elementParallelHFree = 0;
	ElementParallelHandler[] elementParallelH;
	
	int elementCommonHPoolSize;
	int elementCommonHFree = 0;
	ElementCommonHandler[] elementCommonH;
	
	
	int unrecognizedAttributeHPoolSize;
	int unrecognizedAttributeHFree = 0;	
	UnrecognizedAttributeHandler[] unrecognizedAttributeH;
	
	int unexpectedAttributeHPoolSize;
	int unexpectedAttributeHFree = 0;	
	UnexpectedAttributeHandler[] unexpectedAttributeH;
	
	int unexpectedAmbiguousAHPoolSize;
	int unexpectedAmbiguousAHFree = 0;
	UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAH;
	
	int unknownAttributeHPoolSize;
	int unknownAttributeHFree = 0;	
	UnknownAttributeHandler[] unknownAttributeH;
	
	int attributeVHPoolSize;
	int attributeVHFree = 0;	
	AttributeValidationHandler[] attributeVH;
	
	int attributeConcurrentHPoolSize;
	int attributeConcurrentHFree = 0;
	AttributeConcurrentHandler[] attributeConcurrentH;
	
	int characterContentHPoolSize;
	int characterContentHFree = 0;
	CharacterContentValidationHandler[] characterContentH;
	
	int attributeValueHPoolSize;
	int attributeValueHFree = 0;
	AttributeValueValidationHandler[] attributeValueH;
	
	int listPatternTPoolSize;
	int listPatternTFree = 0;
	ListPatternTester[] listPatternT;
	
	int exceptPatternTPoolSize;
	int exceptPatternTFree = 0;
	ExceptPatternTester[] exceptPatternT;
	
	
	
	int boundElementVHPoolSize;
	int boundElementVHFree = 0;	
	BoundElementValidationHandler[] boundElementVH;
	
	int boundStartVHPoolSize;
	int boundStartVHFree = 0;	
	BoundStartValidationHandler[] boundStartVH;
	
	int boundElementConcurrentHPoolSize;
	int boundElementConcurrentHFree = 0;
	BoundElementConcurrentHandler[] boundElementConcurrentH;
	
	int boundElementParallelHPoolSize;
	int boundElementParallelHFree = 0;
	BoundElementParallelHandler[] boundElementParallelH;
	
	int boundAttributeVHPoolSize;
	int boundAttributeVHFree = 0;	
	BoundAttributeValidationHandler[] boundAttributeVH;
	
	int boundAttributeConcurrentHPoolSize;
	int boundAttributeConcurrentHFree = 0;
	BoundAttributeConcurrentHandler[] boundAttributeConcurrentH;
	
	
	MessageWriter debugWriter;
	
	ValidatorEventHandlerPool(ContentHandlerPool contentHandlerPool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.contentHandlerPool = contentHandlerPool;
	}
	
	public void recycle(){
		if(elementVHFree != 0 ||
			startVHFree != 0 ||
			unrecognizedElementHFree != 0 ||
			unexpectedElementHFree != 0 ||
			unexpectedAmbiguousEHFree != 0 ||
			unknownElementHFree != 0 ||
			defaultElementHFree != 0 ||
			elementConcurrentHFree != 0 ||
			elementParallelHFree != 0 ||
			elementCommonHFree != 0 ||
			unrecognizedAttributeHFree != 0 ||
			unexpectedAttributeHFree != 0 ||
			unexpectedAmbiguousAHFree != 0 ||
			unknownAttributeHFree != 0 ||
			attributeVHFree != 0 ||
			attributeConcurrentHFree != 0 ||
			characterContentHFree != 0 ||
			attributeValueHFree != 0 ||
			listPatternTFree != 0 ||
			exceptPatternTFree != 0 ||
			boundElementVHFree != 0 ||
			boundStartVHFree != 0 ||
			boundElementConcurrentHFree != 0 ||
			boundElementParallelHFree != 0 ||
			boundAttributeVHFree != 0 ||
			boundAttributeConcurrentHFree != 0 ) releaseHandlers();
		contentHandlerPool.recycle(this);
	}
	
	public void fill(SpaceCharsHandler spaceHandler, MatchHandler matchHandler, ValidationItemLocator validationItemLocator, ValidatorErrorHandlerPool errorHandlerPool){
		this.spaceHandler = spaceHandler;
		this.matchHandler = matchHandler;
		this.validationItemLocator = validationItemLocator;
		this.errorHandlerPool = errorHandlerPool;
		
		contentHandlerPool.fill(this,
						elementVH,
						startVH,
						unrecognizedElementH,
						unexpectedElementH,
						unexpectedAmbiguousEH,
						unknownElementH,
						defaultElementH,
						elementConcurrentH,
						elementParallelH,
						elementCommonH,
						unrecognizedAttributeH,
						unexpectedAttributeH,
						unexpectedAmbiguousAH,
						unknownAttributeH,
						attributeVH,
						attributeConcurrentH,
						characterContentH,
						attributeValueH,
						listPatternT,
						exceptPatternT,
						boundElementVH,
						boundStartVH,						
						boundElementConcurrentH,
						boundElementParallelH,
						boundAttributeVH,
						boundAttributeConcurrentH);
	}	
	
	void setHandlers(int elementVHFree,	
					ElementValidationHandler[] elementVH,
					int startVHFree,	
					StartValidationHandler[] startVH,
					int unrecognizedElementHFree,	
					UnrecognizedElementHandler[] unrecognizedElementH,
					int unexpectedElementHFree,	
					UnexpectedElementHandler[] unexpectedElementH,
					int unexpectedAmbiguousEHFree,
					UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEH,
					int unknownElementHFree,	
					UnknownElementHandler[] unknownElementH,
					int defaultElementHFree,	
					ElementDefaultHandler[] defaultElementH,
					int elementConcurrentHFree,
					ElementConcurrentHandler[] elementConcurrentH,
					int elementParallelHFree,
					ElementParallelHandler[] elementParallelH,
					int elementCommonHFree,
					ElementCommonHandler[] elementCommonH,
					int unrecognizedAttributeHFree,	
					UnrecognizedAttributeHandler[] unrecognizedAttributeH,
					int unexpectedAttributeHFree,	
					UnexpectedAttributeHandler[] unexpectedAttributeH,
					int unexpectedAmbiguousAHFree,
					UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAH,
					int unknownAttributeHFree,	
					UnknownAttributeHandler[] unknownAttributeH,
					int attributeVHFree,	
					AttributeValidationHandler[] attributeVH,
					int attributeConcurrentHFree,
					AttributeConcurrentHandler[] attributeConcurrentH,
					int characterContentHFree,
					CharacterContentValidationHandler[] characterContentH,					
					int attributeValueHFree,
					AttributeValueValidationHandler[] attributeValueH,
					int listPatternTFree,
					ListPatternTester[] listPatternT,
					int exceptPatternTFree,
					ExceptPatternTester[] exceptPatternT,
					int boundElementVHFree,	
					BoundElementValidationHandler[] boundElementVH,
					int boundStartVHFree,	
					BoundStartValidationHandler[] boundStartVH,
					int boundElementConcurrentHFree,
					BoundElementConcurrentHandler[] boundElementConcurrentH,
					int boundElementParallelHFree,
					BoundElementParallelHandler[] boundElementParallelH,
					int boundAttributeVHFree,	
					BoundAttributeValidationHandler[] boundAttributeVH,
					int boundAttributeConcurrentHFree,
					BoundAttributeConcurrentHandler[] boundAttributeConcurrentH){
		elementVHPoolSize = elementVH.length;
		this.elementVHFree = elementVHFree;
		this.elementVH = elementVH;
		for(int i = 0; i < elementVHFree; i++){	
			elementVH[i].init(this, validationItemLocator, spaceHandler, matchHandler, errorHandlerPool);
		}
		
		startVHPoolSize = startVH.length;
		this.startVHFree = startVHFree;
		this.startVH = startVH;
		for(int i = 0; i < startVHFree; i++){	
			startVH[i].init(this, validationItemLocator, spaceHandler, matchHandler, errorHandlerPool);
		}
				
		
		unrecognizedElementHPoolSize = unrecognizedElementH.length;
		this.unrecognizedElementHFree = unrecognizedElementHFree;
		this.unrecognizedElementH = unrecognizedElementH;
		for(int i = 0; i < unrecognizedElementHFree; i++){	
			unrecognizedElementH[i].init(this, validationItemLocator);
		}
		
		
		unexpectedElementHPoolSize = unexpectedElementH.length;
		this.unexpectedElementHFree = unexpectedElementHFree;
		this.unexpectedElementH = unexpectedElementH;
		for(int i = 0; i < unexpectedElementHFree; i++){	
			unexpectedElementH[i].init(this, validationItemLocator);
		}
		
		
		unexpectedAmbiguousEHPoolSize = unexpectedAmbiguousEH.length;
		this.unexpectedAmbiguousEHFree = unexpectedAmbiguousEHFree;
		this.unexpectedAmbiguousEH = unexpectedAmbiguousEH;
		for(int i = 0; i < unexpectedAmbiguousEHFree; i++){	
			unexpectedAmbiguousEH[i].init(this, validationItemLocator);
		}
		
		
		unknownElementHPoolSize = unknownElementH.length;
		this.unknownElementHFree = unknownElementHFree;
		this.unknownElementH = unknownElementH;
		for(int i = 0; i < unknownElementHFree; i++){	
			unknownElementH[i].init(this, validationItemLocator);
		}
				
		
		defaultElementHPoolSize = defaultElementH.length;
		this.defaultElementHFree = defaultElementHFree;
		this.defaultElementH = defaultElementH;
		for(int i = 0; i < defaultElementHFree; i++){	
			defaultElementH[i].init(this, validationItemLocator);
		}
				
		
		elementConcurrentHPoolSize = elementConcurrentH.length;
		this.elementConcurrentHFree = elementConcurrentHFree;
		this.elementConcurrentH = elementConcurrentH;
		for(int i = 0; i < elementConcurrentHFree; i++){	
			elementConcurrentH[i].init(this, validationItemLocator, errorHandlerPool);
		}
				
		
		elementParallelHPoolSize = elementParallelH.length;
		this.elementParallelHFree = elementParallelHFree;
		this.elementParallelH = elementParallelH;
		for(int i = 0; i < elementParallelHFree; i++){	
			elementParallelH[i].init(this, validationItemLocator);
		}
				
		
		elementCommonHPoolSize = elementCommonH.length;
		this.elementCommonHFree = elementCommonHFree;
		this.elementCommonH = elementCommonH;
		for(int i = 0; i < elementCommonHFree; i++){	
			elementCommonH[i].init(this, validationItemLocator);
		}
		
		unrecognizedAttributeHPoolSize = unrecognizedAttributeH.length;
		this.unrecognizedAttributeHFree = unrecognizedAttributeHFree;
		this.unrecognizedAttributeH = unrecognizedAttributeH;
		for(int i = 0; i < unrecognizedAttributeHFree; i++){	
			unrecognizedAttributeH[i].init(this, validationItemLocator);
		}
		
		
		unexpectedAttributeHPoolSize = unexpectedAttributeH.length;
		this.unexpectedAttributeHFree = unexpectedAttributeHFree;
		this.unexpectedAttributeH = unexpectedAttributeH;
		for(int i = 0; i < unexpectedAttributeHFree; i++){	
			unexpectedAttributeH[i].init(this, validationItemLocator);
		}
		
		
		unexpectedAmbiguousAHPoolSize = unexpectedAmbiguousAH.length;
		this.unexpectedAmbiguousAHFree = unexpectedAmbiguousAHFree;
		this.unexpectedAmbiguousAH = unexpectedAmbiguousAH;
		for(int i = 0; i < unexpectedAmbiguousAHFree; i++){	
			unexpectedAmbiguousAH[i].init(this, validationItemLocator);
		}
		
		
		unknownAttributeHPoolSize = unknownAttributeH.length;
		this.unknownAttributeHFree = unknownAttributeHFree;
		this.unknownAttributeH = unknownAttributeH;
		for(int i = 0; i < unknownAttributeHFree; i++){	
			unknownAttributeH[i].init(this, validationItemLocator);
		}
			
		attributeVHPoolSize = attributeVH.length;
		this.attributeVHFree = attributeVHFree;
		this.attributeVH = attributeVH;
		for(int i = 0; i < attributeVHFree; i++){	
			attributeVH[i].init(this, validationItemLocator, matchHandler);
		}
		
		attributeConcurrentHPoolSize = attributeConcurrentH.length;
		this.attributeConcurrentHFree = attributeConcurrentHFree;
		this.attributeConcurrentH = attributeConcurrentH;
		for(int i = 0; i < attributeConcurrentHFree; i++){	
			attributeConcurrentH[i].init(this, validationItemLocator, errorHandlerPool);
		}
				
		characterContentHPoolSize = characterContentH.length;
		this.characterContentHFree = characterContentHFree;
		this.characterContentH = characterContentH;
		for(int i = 0; i < characterContentHFree; i++){	
			characterContentH[i].init(this, validationItemLocator, matchHandler);
		}
		
		attributeValueHPoolSize = attributeValueH.length;
		this.attributeValueHFree = attributeValueHFree;
		this.attributeValueH = attributeValueH;
		for(int i = 0; i < attributeValueHFree; i++){	
			attributeValueH[i].init(this, validationItemLocator, matchHandler);
		}
		
		listPatternTPoolSize = listPatternT.length;
		this.listPatternTFree = listPatternTFree;
		this.listPatternT = listPatternT;
		for(int i = 0; i < listPatternTFree; i++){	
			listPatternT[i].init(this, validationItemLocator, spaceHandler, matchHandler);
		}
		
		exceptPatternTPoolSize = exceptPatternT.length;
		this.exceptPatternTFree = exceptPatternTFree;
		this.exceptPatternT = exceptPatternT;
		for(int i = 0; i < exceptPatternTFree; i++){	
			exceptPatternT[i].init(this, validationItemLocator, matchHandler);
		}
		
		
		
		boundElementVHPoolSize = boundElementVH.length;
		this.boundElementVHFree = boundElementVHFree;
		this.boundElementVH = boundElementVH;
		for(int i = 0; i < boundElementVHFree; i++){	
			boundElementVH[i].init(this, validationItemLocator, spaceHandler, matchHandler, errorHandlerPool);
		}
		
		boundStartVHPoolSize = boundStartVH.length;
		this.boundStartVHFree = boundStartVHFree;
		this.boundStartVH = boundStartVH;
		for(int i = 0; i < boundStartVHFree; i++){	
			boundStartVH[i].init(this, validationItemLocator, spaceHandler, matchHandler, errorHandlerPool);
		}
		
		boundElementConcurrentHPoolSize = boundElementConcurrentH.length;
		this.boundElementConcurrentHFree = boundElementConcurrentHFree;
		this.boundElementConcurrentH = boundElementConcurrentH;
		for(int i = 0; i < boundElementConcurrentHFree; i++){	
			boundElementConcurrentH[i].init(this, validationItemLocator, errorHandlerPool);
		}
		
		boundElementParallelHPoolSize = boundElementParallelH.length;
		this.boundElementParallelHFree = boundElementParallelHFree;
		this.boundElementParallelH = boundElementParallelH;
		for(int i = 0; i < boundElementParallelHFree; i++){	
			boundElementParallelH[i].init(this, validationItemLocator, errorHandlerPool);
		}
			
		boundAttributeVHPoolSize = boundAttributeVH.length;
		this.boundAttributeVHFree = boundAttributeVHFree;
		this.boundAttributeVH = boundAttributeVH;
		for(int i = 0; i < boundAttributeVHFree; i++){	
			boundAttributeVH[i].init(this, validationItemLocator, matchHandler);
		}
		
		boundAttributeConcurrentHPoolSize = boundAttributeConcurrentH.length;
		this.boundAttributeConcurrentHFree = boundAttributeConcurrentHFree;
		this.boundAttributeConcurrentH = boundAttributeConcurrentH;
		for(int i = 0; i < boundAttributeConcurrentHFree; i++){	
			boundAttributeConcurrentH[i].init(this, validationItemLocator, errorHandlerPool);
		}
	}
	
	public void setValidationContext(ValidationContext validationContext){
		this.validationContext = validationContext;
	}
	
	public void releaseHandlers(){
		contentHandlerPool.recycle(elementVHFree,	
									elementVH,
									startVHFree,	
									startVH,
									unrecognizedElementHFree,	
									unrecognizedElementH,
									unexpectedElementHFree,	
									unexpectedElementH,
									unexpectedAmbiguousEHFree,
									unexpectedAmbiguousEH,
									unknownElementHFree,	
									unknownElementH,
									defaultElementHFree,	
									defaultElementH,
									elementConcurrentHFree,
									elementConcurrentH,
									elementParallelHFree,
									elementParallelH,
									elementCommonHFree,
									elementCommonH,
									unrecognizedAttributeHFree,	
									unrecognizedAttributeH,
									unexpectedAttributeHFree,	
									unexpectedAttributeH,
									unexpectedAmbiguousAHFree,
									unexpectedAmbiguousAH,
									unknownAttributeHFree,	
									unknownAttributeH,
									attributeVHFree,	
									attributeVH,
									attributeConcurrentHFree,
									attributeConcurrentH,
									characterContentHFree,
									characterContentH,
									attributeValueHFree,
									attributeValueH,
									listPatternTFree,
									listPatternT,
									exceptPatternTFree,
									exceptPatternT,
									boundElementVHFree,
									boundElementVH,
									boundStartVHFree,
									boundStartVH,
									boundElementConcurrentHFree,
									boundElementConcurrentH,
									boundElementParallelHFree,
									boundElementParallelH,
									boundAttributeVHFree,	
									boundAttributeVH,
									boundAttributeConcurrentHFree,
									boundAttributeConcurrentH);
		
		elementVHFree = 0;
		startVHFree = 0;		
		unrecognizedElementHFree = 0;
		unexpectedElementHFree = 0;
		unexpectedAmbiguousEHFree = 0;
		unknownElementHFree = 0;
		defaultElementHFree = 0;
		elementConcurrentHFree = 0;
		elementParallelHFree = 0;
		elementCommonHFree = 0;
		unrecognizedAttributeHFree = 0;
		unexpectedAttributeHFree = 0;
		unexpectedAmbiguousAHFree = 0;
		unknownAttributeHFree = 0;
		attributeVHFree = 0;
		attributeConcurrentHFree = 0;
		characterContentHFree = 0;
		listPatternTFree = 0;
		exceptPatternTFree = 0;
		boundElementVHFree = 0;
		boundStartVHFree = 0;
		boundElementConcurrentHFree = 0;
		boundElementParallelHFree = 0;
		boundAttributeVHFree = 0;
		boundAttributeConcurrentHFree = 0;
	}
	
	
	public ElementValidationHandler getElementValidationHandler(AElement element, ElementValidationHandler parent){		
		if(elementVHFree == 0){
			ElementValidationHandler che = new ElementValidationHandler(debugWriter);
			che.init(this, validationItemLocator, spaceHandler, matchHandler, errorHandlerPool);			
			che.init(element,  parent);
			return che;			
		}
		else{						
			ElementValidationHandler che = elementVH[--elementVHFree];
			che.init(element, parent);
			return che;
		}		
	}
	
	void recycle(ElementValidationHandler eh){		
		if(elementVHFree == elementVHPoolSize){			 
			elementVHPoolSize+=5;
			ElementValidationHandler[] increased = new ElementValidationHandler[elementVHPoolSize];
			System.arraycopy(elementVH, 0, increased, 0, elementVHFree);
			elementVH = increased;
		}
		elementVH[elementVHFree++] = eh; 
	}
	
	public StartValidationHandler getStartValidationHandler(AElement start){		
		if(startVHFree == 0){
			StartValidationHandler che = new StartValidationHandler(debugWriter);
			che.init(this, validationItemLocator, spaceHandler, matchHandler, errorHandlerPool);			
			che.init(start, null);
			return che;			
		}
		else{						
			StartValidationHandler che = startVH[--startVHFree];
			che.init(start, null);
			return che;
		}		
	}
		
	void recycle(StartValidationHandler eh){		
		if(startVHFree == startVHPoolSize){			 
			startVHPoolSize+=5;
			StartValidationHandler[] increased = new StartValidationHandler[startVHPoolSize];
			System.arraycopy(startVH, 0, increased, 0, startVHFree);
			startVH = increased;
		}
		startVH[startVHFree++] = eh; 
	}
			
	UnrecognizedElementHandler getUnrecognizedElementHandler(ElementValidationHandler parent){				
		if(unrecognizedElementHFree == 0){
			UnrecognizedElementHandler ueh = new UnrecognizedElementHandler(debugWriter);
			ueh.init(this, validationItemLocator);
			ueh.init(parent);
			return ueh;			
		}
		else{						
			UnrecognizedElementHandler ueh = unrecognizedElementH[--unrecognizedElementHFree];
			ueh.init(parent);
			return ueh;
		}		
	}
	
	void recycle(UnrecognizedElementHandler ueh){		
		if(unrecognizedElementHFree == unrecognizedElementHPoolSize){
			unrecognizedElementHPoolSize+=5;
			UnrecognizedElementHandler[] increased = new UnrecognizedElementHandler[unrecognizedElementHPoolSize];
			System.arraycopy(unrecognizedElementH, 0, increased, 0, unrecognizedElementHFree);
			unrecognizedElementH = increased;
		}
		unrecognizedElementH[unrecognizedElementHFree++] = ueh; 
	}
	
	UnexpectedElementHandler getUnexpectedElementHandler(AElement e, ElementValidationHandler parent){				
		if(unexpectedElementHFree == 0){
			UnexpectedElementHandler ueh = new UnexpectedElementHandler(debugWriter);
			ueh.init(this, validationItemLocator);
			ueh.init(e, parent);			
			return ueh;			
		}
		else{						
			UnexpectedElementHandler ueh = unexpectedElementH[--unexpectedElementHFree];
			ueh.init(e, parent);
			return ueh;
		}		
	}
	
	void recycle(UnexpectedElementHandler ueh){		
		if(unexpectedElementHFree == unexpectedElementHPoolSize){
			unexpectedElementHPoolSize+=5;
			UnexpectedElementHandler[] increased = new UnexpectedElementHandler[unexpectedElementHPoolSize];
			System.arraycopy(unexpectedElementH, 0, increased, 0, unexpectedElementHFree);
			unexpectedElementH = increased;
		}
		unexpectedElementH[unexpectedElementHFree++] = ueh; 
	}
	
	UnexpectedAmbiguousElementHandler getUnexpectedAmbiguousElementHandler(List<AElement> elements, ElementValidationHandler parent){		
		if(unexpectedAmbiguousEHFree == 0){
			UnexpectedAmbiguousElementHandler uach = new UnexpectedAmbiguousElementHandler(debugWriter);
			uach.init(this, validationItemLocator);
			uach.init(elements, parent);
			return uach;
		}
		else{
			UnexpectedAmbiguousElementHandler uach =  unexpectedAmbiguousEH[--unexpectedAmbiguousEHFree];
			uach.init(elements, parent);
			return uach;
		}		
	}
	
	void recycle(UnexpectedAmbiguousElementHandler ucch){		
		if(unexpectedAmbiguousEHFree == unexpectedAmbiguousEHPoolSize){			
			UnexpectedAmbiguousElementHandler[] increased = new UnexpectedAmbiguousElementHandler[++unexpectedAmbiguousEHPoolSize];
			System.arraycopy(unexpectedAmbiguousEH, 0, increased, 0, unexpectedAmbiguousEHFree);
			unexpectedAmbiguousEH = increased;
		}
		unexpectedAmbiguousEH[unexpectedAmbiguousEHFree++] = ucch;
	}		
	
	UnknownElementHandler getUnknownElementHandler(ElementValidationHandler parent){
		if(unknownElementHFree == 0){
			UnknownElementHandler ueh = new UnknownElementHandler(debugWriter);
			ueh.init(this, validationItemLocator);
			ueh.init(parent);			
			return ueh;			
		}
		else{						
			UnknownElementHandler ueh = unknownElementH[--unknownElementHFree];
			ueh.init(parent);
			return ueh;
		}		
	}
	
	void recycle(UnknownElementHandler ueh){		
		if(unknownElementHFree == unknownElementHPoolSize){			
			UnknownElementHandler[] increased = new UnknownElementHandler[++unknownElementHPoolSize];
			System.arraycopy(unknownElementH, 0, increased, 0, unknownElementHFree);
			unknownElementH = increased;
		}
		unknownElementH[unknownElementHFree++] = ueh; 
	}
	
	ElementDefaultHandler getElementDefaultHandler(ElementEventHandler parent){
		if(defaultElementHFree == 0){
			ElementDefaultHandler deh = new ElementDefaultHandler(debugWriter);
			deh.init(this, validationItemLocator);
			deh.init(parent);
			return deh;			
		}
		else{						
			ElementDefaultHandler deh = defaultElementH[--defaultElementHFree];
			deh.init(parent);
			return deh;
		}		
	}
	
	void recycle(ElementDefaultHandler deh){		
		if(defaultElementHFree == defaultElementHPoolSize){			
			ElementDefaultHandler[] increased = new ElementDefaultHandler[++defaultElementHPoolSize];
			System.arraycopy(defaultElementH, 0, increased, 0, defaultElementHFree);
			defaultElementH = increased;
		}
		defaultElementH[defaultElementHFree++] = deh; 
	}
	
	ElementConcurrentHandler getElementConcurrentHandler(List<AElement> candidateDefinitions, ElementValidationHandler parent){		
		if(elementConcurrentHFree == 0){
			ElementConcurrentHandler ech = new ElementConcurrentHandler(debugWriter);
			ech.init(this, validationItemLocator, errorHandlerPool);			
			ech.init(candidateDefinitions, parent);
			return ech;
		}
		else{
			ElementConcurrentHandler ech = elementConcurrentH[--elementConcurrentHFree];
			ech.init(candidateDefinitions, parent);
			return ech; 
		}		
	}
	
	void recycle(ElementConcurrentHandler cpch){		
		if(elementConcurrentHFree == elementConcurrentHPoolSize){			
			ElementConcurrentHandler[] increased = new ElementConcurrentHandler[++elementConcurrentHPoolSize];
			System.arraycopy(elementConcurrentH, 0, increased, 0, elementConcurrentHFree);
			elementConcurrentH = increased;
		}
		elementConcurrentH[elementConcurrentHFree++] = cpch;
	}	
	
	ElementParallelHandler getElementParallelHandler(ExternalConflictHandler conflictHandler, CandidatesEEH parent){		
		if(elementParallelHFree == 0){			
			ElementParallelHandler eph = new ElementParallelHandler(debugWriter);
			eph.init(this, validationItemLocator);
			eph.init(conflictHandler, parent);
			return eph;
		}
		else{
			ElementParallelHandler eph = elementParallelH[--elementParallelHFree];
			eph.init(conflictHandler, parent);
			return eph; 
		}		
	}	
	
	void recycle(ElementParallelHandler eph){	
		if(elementParallelHFree == elementParallelHPoolSize){			
			ElementParallelHandler[] increased = new ElementParallelHandler[++elementParallelHPoolSize];
			System.arraycopy(elementParallelH, 0, increased, 0, elementParallelHFree);
			elementParallelH = increased;
		}
		elementParallelH[elementParallelHFree++] = eph;
	}

	ElementCommonHandler getElementCommonHandler(ExternalConflictHandler conflictHandler, int candidateCount, ValidatingEEH parent){		
		if(elementCommonHFree == 0){			
			ElementCommonHandler eph = new ElementCommonHandler(debugWriter);
			eph.init(this, validationItemLocator);
			eph.init(conflictHandler, candidateCount, parent);
			return eph;
		}
		else{
			ElementCommonHandler eph = elementCommonH[--elementCommonHFree];
			eph.init(conflictHandler, candidateCount, parent);
			return eph; 
		}		
	}	
	
	void recycle(ElementCommonHandler eph){	
		if(elementCommonHFree == elementCommonHPoolSize){			
			ElementCommonHandler[] increased = new ElementCommonHandler[++elementCommonHPoolSize];
			System.arraycopy(elementCommonH, 0, increased, 0, elementCommonHFree);
			elementCommonH = increased;
		}
		elementCommonH[elementCommonHFree++] = eph;
	}

	
	UnrecognizedAttributeHandler getUnrecognizedAttributeHandler(ElementValidationHandler parent){				
		if(unrecognizedAttributeHFree == 0){
			UnrecognizedAttributeHandler uah = new UnrecognizedAttributeHandler(debugWriter);
			uah.init(this, validationItemLocator);
			uah.init(parent);
			return uah;			
		}
		else{						
			UnrecognizedAttributeHandler uah = unrecognizedAttributeH[--unrecognizedAttributeHFree];
			uah.init(parent);
			return uah;
		}		
	}
	
	void recycle(UnrecognizedAttributeHandler ueh){		
		if(unrecognizedAttributeHFree == unrecognizedAttributeHPoolSize){
			unrecognizedAttributeHPoolSize+=5;
			UnrecognizedAttributeHandler[] increased = new UnrecognizedAttributeHandler[unrecognizedAttributeHPoolSize];
			System.arraycopy(unrecognizedAttributeH, 0, increased, 0, unrecognizedAttributeHFree);
			unrecognizedAttributeH = increased;
		}
		unrecognizedAttributeH[unrecognizedAttributeHFree++] = ueh; 
	}
	
	UnexpectedAttributeHandler getUnexpectedAttributeHandler(AAttribute a, ElementValidationHandler parent){				
		if(unexpectedAttributeHFree == 0){
			UnexpectedAttributeHandler uah = new UnexpectedAttributeHandler(debugWriter);
			uah.init(this, validationItemLocator);
			uah.init(a, parent);			
			return uah;			
		}
		else{						
			UnexpectedAttributeHandler uah = unexpectedAttributeH[--unexpectedAttributeHFree];
			uah.init(a, parent);
			return uah;
		}		
	}
	
	void recycle(UnexpectedAttributeHandler ueh){		
		if(unexpectedAttributeHFree == unexpectedAttributeHPoolSize){
			unexpectedAttributeHPoolSize+=5;
			UnexpectedAttributeHandler[] increased = new UnexpectedAttributeHandler[unexpectedAttributeHPoolSize];
			System.arraycopy(unexpectedAttributeH, 0, increased, 0, unexpectedAttributeHFree);
			unexpectedAttributeH = increased;
		}
		unexpectedAttributeH[unexpectedAttributeHFree++] = ueh; 
	}
	
	UnexpectedAmbiguousAttributeHandler getUnexpectedAmbiguousAttributeHandler(List<AAttribute> aa, ElementValidationHandler parent){		
		if(unexpectedAmbiguousAHFree == 0){
			UnexpectedAmbiguousAttributeHandler uach = new UnexpectedAmbiguousAttributeHandler(debugWriter);
			uach.init(this, validationItemLocator);
			uach.init(aa, parent);
			return uach;
		}
		else{
			UnexpectedAmbiguousAttributeHandler uach =  unexpectedAmbiguousAH[--unexpectedAmbiguousAHFree];
			uach.init(aa, parent);
			return uach;
		}		
	}
	
	void recycle(UnexpectedAmbiguousAttributeHandler ucch){		
		if(unexpectedAmbiguousAHFree == unexpectedAmbiguousAHPoolSize){			
			UnexpectedAmbiguousAttributeHandler[] increased = new UnexpectedAmbiguousAttributeHandler[++unexpectedAmbiguousAHPoolSize];
			System.arraycopy(unexpectedAmbiguousAH, 0, increased, 0, unexpectedAmbiguousAHFree);
			unexpectedAmbiguousAH = increased;
		}
		unexpectedAmbiguousAH[unexpectedAmbiguousAHFree++] = ucch;
	}		
	
	UnknownAttributeHandler getUnknownAttributeHandler(ElementValidationHandler parent){
		if(unknownAttributeHFree == 0){
			UnknownAttributeHandler uah = new UnknownAttributeHandler(debugWriter);
			uah.init(this, validationItemLocator);
			uah.init(parent);			
			return uah;			
		}
		else{						
			UnknownAttributeHandler uah = unknownAttributeH[--unknownAttributeHFree];
			uah.init(parent);
			return uah;
		}		
	}
	
	void recycle(UnknownAttributeHandler ueh){		
		if(unknownAttributeHFree == unknownAttributeHPoolSize){			
			UnknownAttributeHandler[] increased = new UnknownAttributeHandler[++unknownAttributeHPoolSize];
			System.arraycopy(unknownAttributeH, 0, increased, 0, unknownAttributeHFree);
			unknownAttributeH = increased;
		}
		unknownAttributeH[unknownAttributeHFree++] = ueh; 
	}
	
	
	/*public AttributeValidationHandler getAttributeValidationHandler(AAttribute attribute, ElementValidationHandler parent){		
		if(attributeVHFree == 0){
			AttributeValidationHandler avh = new AttributeValidationHandler(debugWriter);
			avh.init(this, validationItemLocator, validationContext, matchHandler);
			avh.init(attribute,  parent);
			return avh;			
		}
		else{						
			AttributeValidationHandler avh = attributeVH[--attributeVHFree];
			avh.init(attribute, parent);
			return avh;
		}		
	}*/
	
	public AttributeValidationHandler getAttributeValidationHandler(AAttribute attribute, ElementValidationHandler parent, ErrorCatcher errorCatcher){		
		if(attributeVHFree == 0){
			AttributeValidationHandler avh = new AttributeValidationHandler(debugWriter);
			avh.init(this, validationItemLocator, matchHandler);
			avh.init(attribute,  parent, errorCatcher);
			return avh;			
		}
		else{						
			AttributeValidationHandler avh = attributeVH[--attributeVHFree];
			avh.init(attribute, parent, errorCatcher);
			return avh;
		}		
	}
		
	void recycle(AttributeValidationHandler eh){		
		if(attributeVHFree == attributeVHPoolSize){			 
			attributeVHPoolSize+=5;
			AttributeValidationHandler[] increased = new AttributeValidationHandler[attributeVHPoolSize];
			System.arraycopy(attributeVH, 0, increased, 0, attributeVHFree);
			attributeVH = increased;
		}
		attributeVH[attributeVHFree++] = eh; 
	}
	
	AttributeConcurrentHandler getAttributeConcurrentHandler(List<AAttribute> candidateDefinitions, ElementValidationHandler parent){		
		if(attributeConcurrentHFree == 0){
			AttributeConcurrentHandler ach = new AttributeConcurrentHandler(debugWriter);
			ach.init(this, validationItemLocator, errorHandlerPool);
			ach.init(candidateDefinitions, parent);
			return ach;
		}
		else{
			AttributeConcurrentHandler ach = attributeConcurrentH[--attributeConcurrentHFree];
			ach.init(candidateDefinitions, parent);
			return ach; 
		}		
	}
	
	void recycle(AttributeConcurrentHandler ach){		
		if(attributeConcurrentHFree == attributeConcurrentHPoolSize){			
			AttributeConcurrentHandler[] increased = new AttributeConcurrentHandler[++attributeConcurrentHPoolSize];
			System.arraycopy(attributeConcurrentH, 0, increased, 0, attributeConcurrentHFree);
			attributeConcurrentH = increased;
		}
		attributeConcurrentH[attributeConcurrentHFree++] = ach;
	}

	CharacterContentValidationHandler getCharacterContentValidationHandler(ElementValidationHandler parent, ErrorCatcher errorCatcher){		
		if(characterContentHFree == 0){
			CharacterContentValidationHandler ach = new CharacterContentValidationHandler(debugWriter);
			ach.init(this, validationItemLocator, matchHandler);
			ach.init(parent, validationContext, errorCatcher);
			return ach;
		}
		else{
			CharacterContentValidationHandler ach = characterContentH[--characterContentHFree];
			ach.init(parent, validationContext, errorCatcher);
			return ach; 
		}		
	}
		
	void recycle(CharacterContentValidationHandler ach){		
		if(characterContentHFree == characterContentHPoolSize){			
			CharacterContentValidationHandler[] increased = new CharacterContentValidationHandler[++characterContentHPoolSize];
			System.arraycopy(characterContentH, 0, increased, 0, characterContentHFree);
			characterContentH = increased;
		}
		characterContentH[characterContentHFree++] = ach;
	}
	
	
	AttributeValueValidationHandler getAttributeValueValidationHandler(AttributeValidationHandler parent, ErrorCatcher errorCatcher){		
		if(attributeValueHFree == 0){
			AttributeValueValidationHandler ach = new AttributeValueValidationHandler(debugWriter);
			ach.init(this, validationItemLocator, matchHandler);
			ach.init(parent, validationContext, errorCatcher);
			return ach;
		}
		else{
			AttributeValueValidationHandler ach = attributeValueH[--attributeValueHFree];
			ach.init(parent, validationContext, errorCatcher);
			return ach; 
		}		
	}
		
	void recycle(AttributeValueValidationHandler ach){		
		if(attributeValueHFree == attributeValueHPoolSize){			
			AttributeValueValidationHandler[] increased = new AttributeValueValidationHandler[++attributeValueHPoolSize];
			System.arraycopy(attributeValueH, 0, increased, 0, attributeValueHFree);
			attributeValueH = increased;
		}
		attributeValueH[attributeValueHFree++] = ach;
	}
	
	ListPatternTester getListPatternTester(List<CharsActiveTypeItem> totalCharsItemMatches, int totalCount, ErrorCatcher errorCatcher){		
		if(listPatternTFree == 0){
			ListPatternTester ach = new ListPatternTester(debugWriter);
			ach.init(this, validationItemLocator, spaceHandler, matchHandler);
			ach.init(totalCharsItemMatches, totalCount, validationContext, errorCatcher);
			return ach;
		}
		else{
			ListPatternTester ach = listPatternT[--listPatternTFree];
			ach.init(totalCharsItemMatches, totalCount, validationContext, errorCatcher);
			return ach; 
		}		
	}
	
	void recycle(ListPatternTester ach){		
		if(listPatternTFree == listPatternTPoolSize){			
			ListPatternTester[] increased = new ListPatternTester[++listPatternTPoolSize];
			System.arraycopy(listPatternT, 0, increased, 0, listPatternTFree);
			listPatternT = increased;
		}
		listPatternT[listPatternTFree++] = ach;
	}
	
	ExceptPatternTester getExceptPatternTester(AData data, List<CharsActiveTypeItem> totalCharsItemMatches, int totalCount, ErrorCatcher errorCatcher){		
		if(exceptPatternTFree == 0){
			ExceptPatternTester ach = new ExceptPatternTester(debugWriter);
			ach.init(this, validationItemLocator, matchHandler);
			ach.init(data, totalCharsItemMatches, totalCount, validationContext, errorCatcher);
			return ach;
		}
		else{
			ExceptPatternTester ach = exceptPatternT[--exceptPatternTFree];
			ach.init(data, totalCharsItemMatches, totalCount, validationContext, errorCatcher);
			return ach; 
		}		
	}
	
	void recycle(ExceptPatternTester ach){		
		if(exceptPatternTFree == exceptPatternTPoolSize){			
			ExceptPatternTester[] increased = new ExceptPatternTester[++exceptPatternTPoolSize];
			System.arraycopy(exceptPatternT, 0, increased, 0, exceptPatternTFree);
			exceptPatternT = increased;
		}
		exceptPatternT[exceptPatternTFree++] = ach;
	}
	
	

	
	public BoundElementValidationHandler getElementValidationHandler(AElement element, BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		if(boundElementVHFree == 0){
			BoundElementValidationHandler bevh = new BoundElementValidationHandler(debugWriter);
			bevh.init(this, validationItemLocator, spaceHandler, matchHandler, errorHandlerPool);			
			bevh.init(element,  parent, bindingModel, queue, queuePool);
			return bevh;			
		}
		else{						
			BoundElementValidationHandler bevh = boundElementVH[--boundElementVHFree];
			bevh.init(element,  parent, bindingModel, queue, queuePool);
			return bevh;			
		}		
	}
	
	void recycle(BoundElementValidationHandler bevh){		
		if(boundElementVHFree == boundElementVHPoolSize){			 
			boundElementVHPoolSize+=5;
			BoundElementValidationHandler[] increased = new BoundElementValidationHandler[boundElementVHPoolSize];
			System.arraycopy(boundElementVH, 0, increased, 0, boundElementVHFree);
			boundElementVH = increased;
		}
		boundElementVH[boundElementVHFree++] = bevh; 
	}
	
	public BoundStartValidationHandler getBoundStartValidationHandler(AElement element, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		if(boundStartVHFree == 0){
			BoundStartValidationHandler bevh = new BoundStartValidationHandler(debugWriter);
			bevh.init(this, validationItemLocator, spaceHandler, matchHandler, errorHandlerPool);			
			bevh.init(element,  null, bindingModel, queue, queuePool);
			return bevh;			
		}
		else{						
			BoundStartValidationHandler bevh = boundStartVH[--boundStartVHFree];
			bevh.init(element,  null, bindingModel, queue, queuePool);
			return bevh;			
		}		
	}
		
	void recycle(BoundStartValidationHandler bevh){		
		if(boundStartVHFree == boundStartVHPoolSize){			 
			boundStartVHPoolSize+=5;
			BoundStartValidationHandler[] increased = new BoundStartValidationHandler[boundStartVHPoolSize];
			System.arraycopy(boundStartVH, 0, increased, 0, boundStartVHFree);
			boundStartVH = increased;
		}
		boundStartVH[boundStartVHFree++] = bevh; 
	}

	BoundElementConcurrentHandler getElementConcurrentHandler(List<AElement> candidateDefinitions, BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		if(boundElementConcurrentHFree == 0){
			BoundElementConcurrentHandler ech = new BoundElementConcurrentHandler(debugWriter);
			ech.init(this, validationItemLocator, errorHandlerPool);			
			ech.init(candidateDefinitions, parent, bindingModel, queue, queuePool);
			return ech;
		}
		else{
			BoundElementConcurrentHandler ech = boundElementConcurrentH[--boundElementConcurrentHFree];
			ech.init(candidateDefinitions, parent, bindingModel, queue, queuePool);
			return ech; 
		}		
	}
	
	void recycle(BoundElementConcurrentHandler cpch){		
		if(boundElementConcurrentHFree == boundElementConcurrentHPoolSize){			
			BoundElementConcurrentHandler[] increased = new BoundElementConcurrentHandler[++boundElementConcurrentHPoolSize];
			System.arraycopy(boundElementConcurrentH, 0, increased, 0, boundElementConcurrentHFree);
			boundElementConcurrentH = increased;
		}
		boundElementConcurrentH[boundElementConcurrentHFree++] = cpch;
	}		
	
	
	BoundElementParallelHandler getElementParallelHandler(ExternalConflictHandler conflictHandler, CandidatesEEH parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		if(boundElementParallelHFree == 0){			
			BoundElementParallelHandler eph = new BoundElementParallelHandler(debugWriter);
			eph.init(this, validationItemLocator, errorHandlerPool);
			eph.init(conflictHandler, parent, bindingModel, queue, queuePool);
			return eph;
		}
		else{
			BoundElementParallelHandler eph = boundElementParallelH[--boundElementParallelHFree];
			eph.init(conflictHandler, parent, bindingModel, queue, queuePool);
			return eph; 
		}		
	}	
	
	void recycle(BoundElementParallelHandler eph){	
		if(boundElementParallelHFree == boundElementParallelHPoolSize){			
			BoundElementParallelHandler[] increased = new BoundElementParallelHandler[++boundElementParallelHPoolSize];
			System.arraycopy(boundElementParallelH, 0, increased, 0, boundElementParallelHFree);
			boundElementParallelH = increased;
		}
		boundElementParallelH[boundElementParallelHFree++] = eph;
	}
	
	
	public BoundAttributeValidationHandler getAttributeValidationHandler(AAttribute boundAttribute, ElementValidationHandler parent, ErrorCatcher errorCatcher, BindingModel bindingModel, Queue queue, int entry){		
		if(boundAttributeVHFree == 0){
			BoundAttributeValidationHandler bavh = new BoundAttributeValidationHandler(debugWriter);
			bavh.init(this, validationItemLocator, matchHandler);
			bavh.init(boundAttribute,  parent, errorCatcher, bindingModel, queue, entry);
			return bavh;			
		}
		else{						
			BoundAttributeValidationHandler bavh = boundAttributeVH[--boundAttributeVHFree];
			bavh.init(boundAttribute, parent, errorCatcher, bindingModel, queue, entry);
			return bavh;
		}		
	}
		
	void recycle(BoundAttributeValidationHandler eh){		
		if(boundAttributeVHFree == boundAttributeVHPoolSize){			 
			boundAttributeVHPoolSize+=5;
			BoundAttributeValidationHandler[] increased = new BoundAttributeValidationHandler[boundAttributeVHPoolSize];
			System.arraycopy(boundAttributeVH, 0, increased, 0, boundAttributeVHFree);
			boundAttributeVH = increased;
		}
		boundAttributeVH[boundAttributeVHFree++] = eh; 
	}
	
	BoundAttributeConcurrentHandler getAttributeConcurrentHandler(List<AAttribute> candidateDefinitions, ElementValidationHandler parent, BindingModel bindingModel, Queue queue, int entry){		
		if(boundAttributeConcurrentHFree == 0){
			BoundAttributeConcurrentHandler ach = new BoundAttributeConcurrentHandler(debugWriter);
			ach.init(this, validationItemLocator, errorHandlerPool);
			ach.init(candidateDefinitions, parent, bindingModel, queue, entry);
			return ach;
		}
		else{
			BoundAttributeConcurrentHandler ach = boundAttributeConcurrentH[--boundAttributeConcurrentHFree];
			ach.init(candidateDefinitions, parent, bindingModel, queue, entry);
			return ach; 
		}		
	}
	
	void recycle(BoundAttributeConcurrentHandler ach){		
		if(boundAttributeConcurrentHFree == boundAttributeConcurrentHPoolSize){			
			BoundAttributeConcurrentHandler[] increased = new BoundAttributeConcurrentHandler[++boundAttributeConcurrentHPoolSize];
			System.arraycopy(boundAttributeConcurrentH, 0, increased, 0, boundAttributeConcurrentHFree);
			boundAttributeConcurrentH = increased;
		}
		boundAttributeConcurrentH[boundAttributeConcurrentHFree++] = ach;
	}

}