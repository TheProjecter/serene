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

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AListPattern;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.content.MarkupEventHandler;
import serene.validation.handlers.content.ElementEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.CandidatesConflictErrorHandler;
import serene.validation.handlers.error.ContextErrorHandlerManager;
import serene.validation.handlers.error.ContextErrorHandler;
import serene.validation.handlers.error.TemporaryMessageStorage;

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
	ActiveInputDescriptor activeInputDescriptor;
	InputStackDescriptor inputStackDescriptor;
	ValidatorErrorHandlerPool errorHandlerPool;
	ValidationContext validationContext;
	 
	
	int elementVHMaxSize;
	int elementVHFree;
	int elementVHMinFree;	
	ElementValidationHandler[] elementVH;
	
	int startVHMaxSize;
	int startVHFree;
    int startVHMinFree;	
	StartValidationHandler[] startVH;
		
	int unexpectedElementHMaxSize;
	int unexpectedElementHFree;
    int unexpectedElementHMinFree;	
	UnexpectedElementHandler[] unexpectedElementH;
	
	int unexpectedAmbiguousEHMaxSize;
	int unexpectedAmbiguousEHFree;
	int unexpectedAmbiguousEHMinFree;
	UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEH;
	
	int unknownElementHMaxSize;
	int unknownElementHFree;
    int unknownElementHMinFree;	
	UnknownElementHandler[] unknownElementH;
	
	int elementDefaultHMaxSize;
	int elementDefaultHFree;
    int elementDefaultHMinFree;	
	ElementDefaultHandler[] elementDefaultH;
	
	
	int boundUnexpectedElementHMaxSize;
	int boundUnexpectedElementHFree;
    int boundUnexpectedElementHMinFree;	
	BoundUnexpectedElementHandler[] boundUnexpectedElementH;
	
	int boundUnexpectedAmbiguousEHMaxSize;
	int boundUnexpectedAmbiguousEHFree;
	int boundUnexpectedAmbiguousEHMinFree;
	BoundUnexpectedAmbiguousElementHandler[] boundUnexpectedAmbiguousEH;
	
	int boundUnknownElementHMaxSize;
	int boundUnknownElementHFree;
    int boundUnknownElementHMinFree;	
	BoundUnknownElementHandler[] boundUnknownElementH;
	
	int boundElementDefaultHMaxSize;
	int boundElementDefaultHFree;
    int boundElementDefaultHMinFree;	
	BoundElementDefaultHandler[] boundElementDefaultH;
	
	
	
	int elementConcurrentHMaxSize;
	int elementConcurrentHFree;
	int elementConcurrentHMinFree;
	ElementConcurrentHandler[] elementConcurrentH;
	
	int elementParallelHMaxSize;
	int elementParallelHFree;
	int elementParallelHMinFree;
	ElementParallelHandler[] elementParallelH;
	
	int elementCommonHMaxSize;
	int elementCommonHFree;
	int elementCommonHMinFree;
	ElementCommonHandler[] elementCommonH;
	
		
	int unexpectedAttributeHMaxSize;
	int unexpectedAttributeHFree;
    int unexpectedAttributeHMinFree;	
	UnexpectedAttributeHandler[] unexpectedAttributeH;
	
	int unexpectedAmbiguousAHMaxSize;
	int unexpectedAmbiguousAHFree;
	int unexpectedAmbiguousAHMinFree;
	UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAH;
	
	int unknownAttributeHMaxSize;
	int unknownAttributeHFree;
	int unknownAttributeHMinFree;	
	UnknownAttributeHandler[] unknownAttributeH;
	
	int attributeVHMaxSize;
	int attributeVHFree;
	int attributeVHMinFree;	
	AttributeValidationHandler[] attributeVH;
    
    int candidateAttributeVHMaxSize;
	int candidateAttributeVHFree;
    int candidateAttributeVHMinFree;	
	CandidateAttributeValidationHandler[] candidateAttributeVH;
	
	int attributeConcurrentHMaxSize;
	int attributeConcurrentHFree;
	int attributeConcurrentHMinFree;
	AttributeConcurrentHandler[] attributeConcurrentH;
        
    int attributeParallelHMaxSize;
	int attributeParallelHFree;
	int attributeParallelHMinFree;
	AttributeParallelHandler[] attributeParallelH;
    
    int attributeDefaultHMaxSize;
	int attributeDefaultHFree;
	int attributeDefaultHMinFree;
	AttributeDefaultHandler[] attributeDefaultH;
	
	int charactersValidationHMaxSize;
	int charactersValidationHFree;
	int charactersValidationHMinFree;
	CharactersValidationHandler[] charactersValidationH;
	
	int structuredDataValidationHMaxSize;
	int structuredDataValidationHFree;
	int structuredDataValidationHMinFree;
	StructuredDataValidationHandler[] structuredDataValidationH;
	
	int dataValidationHMaxSize;
	int dataValidationHFree;
	int dataValidationHMinFree;
	DataValidationHandler[] dataValidationH;
    
    int defaultVAttributeHMaxSize;
	int defaultVAttributeHFree;
	int defaultVAttributeHMinFree;
	DefaultValueAttributeValidationHandler[] defaultVAttributeH;
	
	int listPatternVHMaxSize;
	int listPatternVHFree;
	int listPatternVHMinFree;
	ListPatternValidationHandler[] listPatternVH;
	
	int exceptPatternVHMaxSize;
	int exceptPatternVHFree;
	int exceptPatternVHMinFree;
	ExceptPatternValidationHandler[] exceptPatternVH;
	
	
	
	int boundElementVHMaxSize;
	int boundElementVHFree;
	int boundElementVHMinFree;	
	BoundElementValidationHandler[] boundElementVH;
	
	int boundStartVHMaxSize;
	int boundStartVHFree;
	int boundStartVHMinFree;	
	BoundStartValidationHandler[] boundStartVH;
	
	int boundElementConcurrentHMaxSize;
	int boundElementConcurrentHFree;
	int boundElementConcurrentHMinFree;
	BoundElementConcurrentHandler[] boundElementConcurrentH;
	
	int boundElementParallelHMaxSize;
	int boundElementParallelHFree;
	int boundElementParallelHMinFree;
	BoundElementParallelHandler[] boundElementParallelH;
	
	int boundAttributeVHMaxSize;
	int boundAttributeVHFree;	
	int boundAttributeVHMinFree;	
	BoundAttributeValidationHandler[] boundAttributeVH;
    
    int boundCandidateAttributeVHMaxSize;
	int boundCandidateAttributeVHFree;
	int boundCandidateAttributeVHMinFree;	
	BoundCandidateAttributeValidationHandler[] boundCandidateAttributeVH;
	
	int boundAttributeConcurrentHMaxSize;
	int boundAttributeConcurrentHFree;
	int boundAttributeConcurrentHMinFree;
	BoundAttributeConcurrentHandler[] boundAttributeConcurrentH;
    
    int boundAttributeParallelHMaxSize;
	int boundAttributeParallelHFree;
	int boundAttributeParallelHMinFree;
	BoundAttributeParallelHandler[] boundAttributeParallelH;
	
	boolean full;
	
	MessageWriter debugWriter;
	
	ValidatorEventHandlerPool(ContentHandlerPool contentHandlerPool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.contentHandlerPool = contentHandlerPool;
		
		elementVHMaxSize = 40;
        startVHMaxSize = 5;
        unexpectedElementHMaxSize = 20;
        unexpectedAmbiguousEHMaxSize = 20;
        unknownElementHMaxSize = 20;
        elementDefaultHMaxSize = 20;
        boundUnexpectedElementHMaxSize = 20;
        boundUnexpectedAmbiguousEHMaxSize = 20;
        boundUnknownElementHMaxSize = 20;
        boundElementDefaultHMaxSize = 20;
        elementConcurrentHMaxSize = 20;
        elementParallelHMaxSize = 20;
        elementCommonHMaxSize = 20;
            
        unexpectedAttributeHMaxSize = 20;
        unexpectedAmbiguousAHMaxSize = 20;
        unknownAttributeHMaxSize = 20;
        attributeVHMaxSize = 20;
        candidateAttributeVHMaxSize = 20;
        attributeConcurrentHMaxSize = 20;
        attributeParallelHMaxSize = 20;
        attributeDefaultHMaxSize = 20;
        charactersValidationHMaxSize = 20;
        structuredDataValidationHMaxSize = 20;
        dataValidationHMaxSize = 20;
        defaultVAttributeHMaxSize = 20;
        listPatternVHMaxSize = 20;
        exceptPatternVHMaxSize = 20;
                
        boundElementVHMaxSize = 40;
        boundStartVHMaxSize = 5;
        boundElementConcurrentHMaxSize = 20;
        boundElementParallelHMaxSize = 20;
        boundAttributeVHMaxSize = 20;
        boundCandidateAttributeVHMaxSize = 20;
        boundAttributeConcurrentHMaxSize = 20;
        boundAttributeParallelHMaxSize = 20;
	}
	
	public void recycle(){
		if(full) releaseHandlers();
		contentHandlerPool.recycle(this);
	}
	
	public void init(SpaceCharsHandler spaceHandler, 
	                    MatchHandler matchHandler, 
	                    ActiveInputDescriptor activeInputDescriptor,	                    InputStackDescriptor inputStackDescriptor, 
	                    ValidationContext validationContext,
	                    ValidatorErrorHandlerPool errorHandlerPool){
	    this.spaceHandler = spaceHandler;
		this.matchHandler = matchHandler;
		this.activeInputDescriptor = activeInputDescriptor;
		this.inputStackDescriptor = inputStackDescriptor;
		this.validationContext = validationContext;
		this.errorHandlerPool = errorHandlerPool;
	}
	
	public void fill(){	
		if(contentHandlerPool != null){
		    contentHandlerPool.fill(this,
						elementVH,
						startVH,
						unexpectedElementH,
						unexpectedAmbiguousEH,
						unknownElementH,
						elementDefaultH,
						boundUnexpectedElementH,
						boundUnexpectedAmbiguousEH,
						boundUnknownElementH,
						boundElementDefaultH,
						elementConcurrentH,
						elementParallelH,
						elementCommonH,
						unexpectedAttributeH,
						unexpectedAmbiguousAH,
						unknownAttributeH,
						attributeVH,
                        candidateAttributeVH,
						attributeConcurrentH,
                        attributeParallelH,
                        attributeDefaultH,
						charactersValidationH,
						structuredDataValidationH,
						dataValidationH,
                        defaultVAttributeH,
						listPatternVH,
						exceptPatternVH,
						boundElementVH,
						boundStartVH,						
						boundElementConcurrentH,
						boundElementParallelH,
						boundAttributeVH,
						boundCandidateAttributeVH,
						boundAttributeConcurrentH,
                        boundAttributeParallelH);
        }else{
            elementVH = new ElementValidationHandler[10];
            startVH = new StartValidationHandler[10];
            
    
            unexpectedElementH = new UnexpectedElementHandler[10];
            unexpectedAmbiguousEH = new UnexpectedAmbiguousElementHandler[10];
            unknownElementH = new UnknownElementHandler[10];
            elementDefaultH = new ElementDefaultHandler[10];
            
            
            boundUnexpectedElementH = new BoundUnexpectedElementHandler[10];
            boundUnexpectedAmbiguousEH = new BoundUnexpectedAmbiguousElementHandler[10];
            boundUnknownElementH = new BoundUnknownElementHandler[10];
            boundElementDefaultH = new BoundElementDefaultHandler[10];
            
            
            elementConcurrentH = new ElementConcurrentHandler[10];
            elementParallelH = new ElementParallelHandler[10];
            elementCommonH = new ElementCommonHandler[10];		
        
    
            unexpectedAttributeH = new UnexpectedAttributeHandler[10];
            unexpectedAmbiguousAH = new UnexpectedAmbiguousAttributeHandler[10];
            unknownAttributeH = new UnknownAttributeHandler[10];
            attributeVH = new AttributeValidationHandler[10];
            candidateAttributeVH = new CandidateAttributeValidationHandler[10];
            attributeConcurrentH = new AttributeConcurrentHandler[10];
            attributeParallelH = new AttributeParallelHandler[10];
            attributeDefaultH = new AttributeDefaultHandler[10];
            
            
            charactersValidationH = new CharactersValidationHandler[10];
            structuredDataValidationH = new StructuredDataValidationHandler[10];
            dataValidationH = new DataValidationHandler[10];
            
            
            defaultVAttributeH = new DefaultValueAttributeValidationHandler[10];
            listPatternVH = new ListPatternValidationHandler[10];
            exceptPatternVH = new ExceptPatternValidationHandler[10];
            
            
            boundElementVH = new BoundElementValidationHandler[10];
            boundStartVH = new BoundStartValidationHandler[10];
            boundElementConcurrentH = new BoundElementConcurrentHandler[10];
            boundElementParallelH = new BoundElementParallelHandler[10];
            boundAttributeVH = new BoundAttributeValidationHandler[10];
            boundCandidateAttributeVH = new BoundCandidateAttributeValidationHandler[10];
            boundAttributeConcurrentH = new BoundAttributeConcurrentHandler[10];
            boundAttributeParallelH = new BoundAttributeParallelHandler[10];
        }
        
        full = true;
	}	
	
	void initFilled(int elementVHFillCount,
					int startVHFillCount,	
					int unexpectedElementHFillCount,
					int unexpectedAmbiguousEHFillCount,
					int unknownElementHFillCount,	
					int elementDefaultHFillCount,
					int boundUnexpectedElementHFillCount,
					int boundUnexpectedAmbiguousEHFillCount,
					int boundUnknownElementHFillCount,	
					int boundElementDefaultHFillCount,
					int elementConcurrentHFillCount,
					int elementParallelHFillCount,
					int elementCommonHFillCount,
					int unexpectedAttributeHFillCount,
					int unexpectedAmbiguousAHFillCount,
					int unknownAttributeHFillCount,	
					int attributeVHFillCount,	
					int candidateAttributeVHFillCount,
					int attributeConcurrentHFillCount,
                    int attributeParallelHFillCount,
                    int attributeDefaultHFillCount,
					int charactersValidationHFillCount,					
					int structuredDataValidationHFillCount,
					int dataValidationHFillCount,
                    int defaultVAttributeHFillCount,
					int listPatternVHFillCount,
					int exceptPatternVHFillCount,
					int boundElementVHFillCount,	
					int boundStartVHFillCount,	
					int boundElementConcurrentHFillCount,
					int boundElementParallelHFillCount,
					int boundAttributeVHFillCount,	
                    int boundCandidateAttributeVHFillCount,
					int boundAttributeConcurrentHFillCount,
                    int boundAttributeParallelHFillCount){
		elementVHFree = elementVHFillCount;
		elementVHMinFree = elementVHFree;
		for(int i = 0; i < elementVHFree; i++){	
			elementVH[i].init(this, activeInputDescriptor, inputStackDescriptor, spaceHandler, matchHandler, errorHandlerPool);
		}
				
		startVHFree = startVHFillCount;
		startVHMinFree = startVHFree;
		for(int i = 0; i < startVHFree; i++){	
			startVH[i].init(this, activeInputDescriptor, inputStackDescriptor, spaceHandler, matchHandler, errorHandlerPool);
		}
				
				
		
		unexpectedElementHFree = unexpectedElementHFillCount;
		unexpectedElementHMinFree = unexpectedElementHFree;
		for(int i = 0; i < unexpectedElementHFree; i++){	
			unexpectedElementH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		unexpectedAmbiguousEHFree = unexpectedAmbiguousEHFillCount;
		unexpectedAmbiguousEHMinFree = unexpectedAmbiguousEHFree;
		for(int i = 0; i < unexpectedAmbiguousEHFree; i++){	
			unexpectedAmbiguousEH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		unknownElementHFree = unknownElementHFillCount;
		unknownElementHMinFree = unknownElementHFree;
		for(int i = 0; i < unknownElementHFree; i++){	
			unknownElementH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
				
		
		elementDefaultHFree = elementDefaultHFillCount;
		elementDefaultHMinFree = elementDefaultHFree;
		for(int i = 0; i < elementDefaultHFree; i++){	
			elementDefaultH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		
		
		boundUnexpectedElementHFree = boundUnexpectedElementHFillCount;
		boundUnexpectedElementHMinFree = boundUnexpectedElementHFree;
		for(int i = 0; i < boundUnexpectedElementHFree; i++){	
			boundUnexpectedElementH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		boundUnexpectedAmbiguousEHFree = boundUnexpectedAmbiguousEHFillCount;
		boundUnexpectedAmbiguousEHMinFree = boundUnexpectedAmbiguousEHFree;
		for(int i = 0; i < boundUnexpectedAmbiguousEHFree; i++){	
			boundUnexpectedAmbiguousEH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		boundUnknownElementHFree = boundUnknownElementHFillCount;
		boundUnknownElementHMinFree = boundUnknownElementHFree;
		for(int i = 0; i < boundUnknownElementHFree; i++){	
			boundUnknownElementH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
				
		
		boundElementDefaultHFree = boundElementDefaultHFillCount;
		boundElementDefaultHMinFree = boundElementDefaultHFree;
		for(int i = 0; i < boundElementDefaultHFree; i++){	
			boundElementDefaultH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
				
		
		elementConcurrentHFree = elementConcurrentHFillCount;
		elementConcurrentHMinFree = elementConcurrentHFree;
		for(int i = 0; i < elementConcurrentHFree; i++){	
			elementConcurrentH[i].init(this, activeInputDescriptor, inputStackDescriptor, errorHandlerPool);
		}
        
				
		
		elementParallelHFree = elementParallelHFillCount;
		elementParallelHMinFree = elementParallelHFree;
		for(int i = 0; i < elementParallelHFree; i++){	
			elementParallelH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
				
		
		elementCommonHFree = elementCommonHFillCount;
		elementCommonHMinFree = elementCommonHFree;
		for(int i = 0; i < elementCommonHFree; i++){	
			elementCommonH[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		unexpectedAttributeHFree = unexpectedAttributeHFillCount;
		unexpectedAttributeHMinFree = unexpectedAttributeHFree;
		for(int i = 0; i < unexpectedAttributeHFree; i++){	
			unexpectedAttributeH[i].init(this, inputStackDescriptor);
		}
		
		
		unexpectedAmbiguousAHFree = unexpectedAmbiguousAHFillCount;
		unexpectedAmbiguousAHMinFree = unexpectedAmbiguousAHFree;
		for(int i = 0; i < unexpectedAmbiguousAHFree; i++){	
			unexpectedAmbiguousAH[i].init(this, inputStackDescriptor);
		}
		
		
		unknownAttributeHFree = unknownAttributeHFillCount;
		unknownAttributeHMinFree = unknownAttributeHFree;
		for(int i = 0; i < unknownAttributeHFree; i++){	
			unknownAttributeH[i].init(this, inputStackDescriptor);
		}
		
		
		attributeVHFree = attributeVHFillCount;
		attributeVHMinFree = attributeVHFree;
		for(int i = 0; i < attributeVHFree; i++){	
			attributeVH[i].init(this, inputStackDescriptor, matchHandler);
		}
        
        
		candidateAttributeVHFree = candidateAttributeVHFillCount;
		candidateAttributeVHMinFree = candidateAttributeVHFree;
		for(int i = 0; i < candidateAttributeVHFree; i++){	
			candidateAttributeVH[i].init(this, activeInputDescriptor, inputStackDescriptor, matchHandler);
		}
		
		
		attributeConcurrentHFree = attributeConcurrentHFillCount;
		attributeConcurrentHMinFree = attributeConcurrentHFree;
		for(int i = 0; i < attributeConcurrentHFree; i++){	
			attributeConcurrentH[i].init(this, inputStackDescriptor, errorHandlerPool);
		}
        
		
		attributeParallelHFree = attributeParallelHFillCount;
		attributeParallelHMinFree = attributeParallelHFree;
		for(int i = 0; i < attributeParallelHFree; i++){	
			attributeParallelH[i].init(this, inputStackDescriptor);
		}
		
		
		attributeDefaultHFree = attributeDefaultHFillCount;
		attributeDefaultHMinFree = attributeDefaultHFree;
		for(int i = 0; i < attributeDefaultHFree; i++){	
			attributeDefaultH[i].init(this, inputStackDescriptor);
		}
		
		
		charactersValidationHFree = charactersValidationHFillCount;
		charactersValidationHMinFree = charactersValidationHFree;
		for(int i = 0; i < charactersValidationHFree; i++){	
			charactersValidationH[i].init(matchHandler, validationContext, spaceHandler, activeInputDescriptor, inputStackDescriptor, this);
		}
		
		
		structuredDataValidationHFree = structuredDataValidationHFillCount;
		structuredDataValidationHMinFree = structuredDataValidationHFree;
		for(int i = 0; i < structuredDataValidationHFree; i++){	
			structuredDataValidationH[i].init(matchHandler, validationContext, spaceHandler, activeInputDescriptor, inputStackDescriptor, this);
		}
		
		
		dataValidationHFree = dataValidationHFillCount;
		dataValidationHMinFree = dataValidationHFree;
		for(int i = 0; i < dataValidationHFree; i++){	
			dataValidationH[i].init(matchHandler, validationContext, spaceHandler, activeInputDescriptor, inputStackDescriptor, this);
		}
        
		
		defaultVAttributeHFree = defaultVAttributeHFillCount;
		defaultVAttributeHMinFree = defaultVAttributeHFree;
		for(int i = 0; i < defaultVAttributeHFree; i++){	
			defaultVAttributeH[i].init(this, inputStackDescriptor, matchHandler);
		}
		
		
		listPatternVHFree = listPatternVHFillCount;
		listPatternVHMinFree = listPatternVHFree;
		for(int i = 0; i < listPatternVHFree; i++){	
			listPatternVH[i].init(this, inputStackDescriptor, spaceHandler);
		}
		
		
		exceptPatternVHFree = exceptPatternVHFillCount;
		exceptPatternVHMinFree = exceptPatternVHFree;
		for(int i = 0; i < exceptPatternVHFree; i++){	
			exceptPatternVH[i].init(this, inputStackDescriptor);
		}
		
		
		
		boundElementVHFree = boundElementVHFillCount;
		boundElementVHMinFree = boundElementVHFree;
		for(int i = 0; i < boundElementVHFree; i++){	
			boundElementVH[i].init(this, activeInputDescriptor, inputStackDescriptor, spaceHandler, matchHandler, errorHandlerPool);
		}
		
		
		boundStartVHFree = boundStartVHFillCount;
		boundStartVHMinFree = boundStartVHFree;
		for(int i = 0; i < boundStartVHFree; i++){	
			boundStartVH[i].init(this, activeInputDescriptor, inputStackDescriptor, spaceHandler, matchHandler, errorHandlerPool);
		}
		
		
		boundElementConcurrentHFree = boundElementConcurrentHFillCount;
		boundElementConcurrentHMinFree = boundElementConcurrentHFree;
		for(int i = 0; i < boundElementConcurrentHFree; i++){	
			boundElementConcurrentH[i].init(this, activeInputDescriptor, inputStackDescriptor, errorHandlerPool);
		}
		
		
		boundElementParallelHFree = boundElementParallelHFillCount;
		boundElementParallelHMinFree = boundElementParallelHFree;
		for(int i = 0; i < boundElementParallelHFree; i++){	
			boundElementParallelH[i].init(this, activeInputDescriptor, inputStackDescriptor, errorHandlerPool);
		}
		
		
		boundAttributeVHFree = boundAttributeVHFillCount;
		boundAttributeVHMinFree = boundAttributeVHFree;
		for(int i = 0; i < boundAttributeVHFree; i++){	
			boundAttributeVH[i].init(this, inputStackDescriptor, matchHandler);
		}
        
		
		boundCandidateAttributeVHFree = boundCandidateAttributeVHFillCount;
		boundCandidateAttributeVHMinFree = boundCandidateAttributeVHFree;
		for(int i = 0; i < boundCandidateAttributeVHFree; i++){	
			boundCandidateAttributeVH[i].init(this, activeInputDescriptor, inputStackDescriptor, matchHandler);
		}
		
		
		boundAttributeConcurrentHFree = boundAttributeConcurrentHFillCount;
		boundAttributeConcurrentHMinFree = boundAttributeConcurrentHFree;
		for(int i = 0; i < boundAttributeConcurrentHFree; i++){	
			boundAttributeConcurrentH[i].init(this, inputStackDescriptor, errorHandlerPool);
		}
        
		
		boundAttributeParallelHFree = boundAttributeParallelHFillCount;
		boundAttributeConcurrentHMinFree = boundAttributeConcurrentHFree;
		for(int i = 0; i < boundAttributeParallelHFree; i++){	
			boundAttributeParallelH[i].init(this, inputStackDescriptor);
		}
	}
	
	
	
	public void releaseHandlers(){
		contentHandlerPool.recycle(elementVHFree,
		                            elementVHFree - elementVHMinFree,
									elementVH,
									startVHFree,
									startVHFree - startVHMinFree,
									startVH,
									unexpectedElementHFree,
									unexpectedElementHFree - unexpectedElementHMinFree,
									unexpectedElementH,
									unexpectedAmbiguousEHFree,
									unexpectedAmbiguousEHFree - unexpectedAmbiguousEHMinFree,
									unexpectedAmbiguousEH,
									unknownElementHFree,
									unknownElementHFree - unknownElementHMinFree,
									unknownElementH,
									elementDefaultHFree,	
									elementDefaultHFree - elementDefaultHMinFree,
									elementDefaultH,
									boundUnexpectedElementHFree,
									boundUnexpectedElementHFree - boundUnexpectedElementHMinFree,
									boundUnexpectedElementH,
									boundUnexpectedAmbiguousEHFree,
									boundUnexpectedAmbiguousEHFree - boundUnexpectedAmbiguousEHMinFree,
									boundUnexpectedAmbiguousEH,
									boundUnknownElementHFree,
									boundUnknownElementHFree - boundUnknownElementHMinFree,
									boundUnknownElementH,
									boundElementDefaultHFree,	
									boundElementDefaultHFree - boundElementDefaultHMinFree,
									boundElementDefaultH,
									elementConcurrentHFree,
									elementConcurrentHFree - elementConcurrentHMinFree,
									elementConcurrentH,									
									elementParallelHFree,
									elementParallelHFree - elementParallelHMinFree,
									elementParallelH,
									elementCommonHFree,
									elementCommonHFree - elementCommonHMinFree,
									elementCommonH,
									unexpectedAttributeHFree,
									unexpectedAttributeHFree - unexpectedAttributeHMinFree,
									unexpectedAttributeH,
									unexpectedAmbiguousAHFree,
									unexpectedAmbiguousAHFree - unexpectedAmbiguousAHMinFree,
									unexpectedAmbiguousAH,
									unknownAttributeHFree,	
									unknownAttributeHFree - unknownAttributeHMinFree,	
									unknownAttributeH,
									attributeVHFree,
									attributeVHFree - attributeVHMinFree,
									attributeVH,
                                    candidateAttributeVHFree,
                                    candidateAttributeVHFree - candidateAttributeVHMinFree,
									candidateAttributeVH,
									attributeConcurrentHFree,
									attributeConcurrentHFree - attributeConcurrentHMinFree,
									attributeConcurrentH,
									attributeParallelHFree,
									attributeParallelHFree - attributeParallelHMinFree,
									attributeParallelH,
                                    attributeDefaultHFree,
                                    attributeDefaultHFree - attributeDefaultHMinFree,
									attributeDefaultH,                                    
									charactersValidationHFree,
									charactersValidationHFree - charactersValidationHMinFree,
									charactersValidationH,
									structuredDataValidationHFree,
									structuredDataValidationHFree - structuredDataValidationHMinFree,
									structuredDataValidationH,
									dataValidationHFree,
									dataValidationHFree - dataValidationHMinFree,
									dataValidationH,
                                    defaultVAttributeHFree,
                                    defaultVAttributeHFree - defaultVAttributeHMinFree,
									defaultVAttributeH,
									listPatternVHFree,
									listPatternVHFree - listPatternVHMinFree,
									listPatternVH,
									exceptPatternVHFree,
									exceptPatternVHFree - exceptPatternVHMinFree,
									exceptPatternVH,
									boundElementVHFree,
									boundElementVHFree - boundElementVHMinFree,
									boundElementVH,
									boundStartVHFree,
									boundStartVHFree - boundStartVHMinFree,
									boundStartVH,
									boundElementConcurrentHFree,
									boundElementConcurrentHFree - boundElementConcurrentHMinFree,
									boundElementConcurrentH,
									boundElementParallelHFree,
									boundElementParallelHFree - boundElementParallelHMinFree,
									boundElementParallelH,
									boundAttributeVHFree,
									boundAttributeVHFree - boundAttributeVHMinFree,
									boundAttributeVH,
                                    boundCandidateAttributeVHFree,
                                    boundCandidateAttributeVHFree - boundCandidateAttributeVHMinFree,
									boundCandidateAttributeVH,
									boundAttributeConcurrentHFree,
									boundAttributeConcurrentHFree - boundAttributeConcurrentHMinFree,
									boundAttributeConcurrentH,
                                    boundAttributeParallelHFree,
                                    boundAttributeParallelHFree - boundAttributeParallelHMinFree,
									boundAttributeParallelH);			
		
		elementVHFree = 0;
        startVHFree = 0;
        unexpectedElementHFree = 0;
        unexpectedAmbiguousEHFree = 0;
        unknownElementHFree = 0;
        elementDefaultHFree = 0;
        boundUnexpectedElementHFree = 0;
        boundUnexpectedAmbiguousEHFree = 0;
        boundUnknownElementHFree = 0;
        boundElementDefaultHFree = 0;
        elementConcurrentHFree = 0;							
        elementParallelHFree = 0;
        elementCommonHFree = 0;
        unexpectedAttributeHFree = 0;
        unexpectedAmbiguousAHFree = 0;
        unknownAttributeHFree = 0;	
        attributeVHFree = 0;
        candidateAttributeVHFree = 0;
        attributeConcurrentHFree = 0;
        attributeParallelHFree = 0;
        attributeDefaultHFree = 0;                               
        charactersValidationHFree = 0;
        structuredDataValidationHFree = 0;
        dataValidationHFree = 0;
        defaultVAttributeHFree = 0;
        listPatternVHFree = 0;
        exceptPatternVHFree = 0;
        boundElementVHFree = 0;
        boundStartVHFree = 0;
        boundElementConcurrentHFree = 0;
        boundElementParallelHFree = 0;
        boundAttributeVHFree = 0;
        boundCandidateAttributeVHFree = 0;
        boundAttributeConcurrentHFree = 0;
        boundAttributeParallelHFree = 0;
        
		full = false;
	}
	
	
	public ElementValidationHandler getElementValidationHandler(AElement element, ElementValidationHandler parent){		
		if(elementVHFree == 0){
			ElementValidationHandler che = new ElementValidationHandler(debugWriter);
			che.init(this, activeInputDescriptor, inputStackDescriptor, spaceHandler, matchHandler, errorHandlerPool);			
			che.init(element,  parent);
			return che;			
		}
		else{						
			ElementValidationHandler che = elementVH[--elementVHFree];
			che.init(element, parent);
			if(elementVHFree < elementVHMinFree) elementVHMinFree = elementVHFree;
			return che;
		}		
	}
	
	void recycle(ElementValidationHandler eh){		
		if(elementVHFree == elementVH.length){			 
		    if(elementVHFree == elementVHMaxSize)return;
			ElementValidationHandler[] increased = new ElementValidationHandler[10+elementVH.length];
			System.arraycopy(elementVH, 0, increased, 0, elementVHFree);
			elementVH = increased;
		}
		elementVH[elementVHFree++] = eh; 
	}
	
	public StartValidationHandler getStartValidationHandler(AElement start){		
		if(startVHFree == 0){
			StartValidationHandler che = new StartValidationHandler(debugWriter);
			che.init(this, activeInputDescriptor, inputStackDescriptor, spaceHandler, matchHandler, errorHandlerPool);			
			che.init(start, null);
			return che;			
		}
		else{						
			StartValidationHandler che = startVH[--startVHFree];
			che.init(start, null);
			if(startVHFree < startVHMinFree) startVHMinFree = startVHFree;
			return che;
		}		
	}
		
	void recycle(StartValidationHandler eh){		
		if(startVHFree == startVH.length){
		    if(startVHFree == startVHMaxSize) return;
			StartValidationHandler[] increased = new StartValidationHandler[5];
			System.arraycopy(startVH, 0, increased, 0, startVHFree);
			startVH = increased;
		}
		startVH[startVHFree++] = eh; 
	}
	
    
	UnexpectedElementHandler getUnexpectedElementHandler(SimplifiedComponent e, ElementValidationHandler parent){				
		if(unexpectedElementHFree == 0){
			UnexpectedElementHandler ueh = new UnexpectedElementHandler(debugWriter);
			ueh.init(this, activeInputDescriptor, inputStackDescriptor);
			ueh.init(e, parent);			
			return ueh;			
		}
		else{						
			UnexpectedElementHandler ueh = unexpectedElementH[--unexpectedElementHFree];
			ueh.init(e, parent);
			if(unexpectedElementHFree < unexpectedElementHMinFree) unexpectedElementHMinFree = unexpectedElementHFree;
			return ueh;
		}		
	}
	
	void recycle(UnexpectedElementHandler ueh){		
		if(unexpectedElementHFree == unexpectedElementH.length){
		    if(unexpectedElementHFree == unexpectedElementHMaxSize) return;
			UnexpectedElementHandler[] increased = new UnexpectedElementHandler[10+unexpectedElementH.length];
			System.arraycopy(unexpectedElementH, 0, increased, 0, unexpectedElementHFree);
			unexpectedElementH = increased;
		}
		unexpectedElementH[unexpectedElementHFree++] = ueh; 
	}
	
	UnexpectedAmbiguousElementHandler getUnexpectedAmbiguousElementHandler(List<SimplifiedComponent> elements, ElementValidationHandler parent){		
		if(unexpectedAmbiguousEHFree == 0){
			UnexpectedAmbiguousElementHandler uach = new UnexpectedAmbiguousElementHandler(debugWriter);
			uach.init(this, activeInputDescriptor, inputStackDescriptor);
			uach.init(elements, parent);
			return uach;
		}
		else{
			UnexpectedAmbiguousElementHandler uach =  unexpectedAmbiguousEH[--unexpectedAmbiguousEHFree];
			uach.init(elements, parent);
			if(unexpectedElementHFree < unexpectedElementHMinFree) unexpectedElementHMinFree = unexpectedElementHFree;
			return uach;
		}		
	}
	
	void recycle(UnexpectedAmbiguousElementHandler ucch){		
		if(unexpectedAmbiguousEHFree == unexpectedAmbiguousEH.length){			
		    if(unexpectedAmbiguousEHFree == unexpectedAmbiguousEHMaxSize) return;
			UnexpectedAmbiguousElementHandler[] increased = new UnexpectedAmbiguousElementHandler[10+unexpectedAmbiguousEH.length];
			System.arraycopy(unexpectedAmbiguousEH, 0, increased, 0, unexpectedAmbiguousEHFree);
			unexpectedAmbiguousEH = increased;
		}
		unexpectedAmbiguousEH[unexpectedAmbiguousEHFree++] = ucch;
	}		
	
	UnknownElementHandler getUnknownElementHandler(ElementValidationHandler parent){
		if(unknownElementHFree == 0){
			UnknownElementHandler ueh = new UnknownElementHandler(debugWriter);
			ueh.init(this, activeInputDescriptor, inputStackDescriptor);
			ueh.init(parent);			
			return ueh;			
		}
		else{						
			UnknownElementHandler ueh = unknownElementH[--unknownElementHFree];
			ueh.init(parent);
			if(unknownElementHFree < unknownElementHMinFree) unknownElementHMinFree = unknownElementHFree;
			return ueh;
		}		
	}
	
	void recycle(UnknownElementHandler ueh){		
		if(unknownElementHFree == unknownElementH.length){
            if(unknownElementHFree == unknownElementHMaxSize) return;			
			UnknownElementHandler[] increased = new UnknownElementHandler[10+unknownElementH.length];
			System.arraycopy(unknownElementH, 0, increased, 0, unknownElementHFree);
			unknownElementH = increased;
		}
		unknownElementH[unknownElementHFree++] = ueh; 
	}
	
	ElementDefaultHandler getElementDefaultHandler(ElementEventHandler parent){
		if(elementDefaultHFree == 0){
			ElementDefaultHandler deh = new ElementDefaultHandler(debugWriter);
			deh.init(this, activeInputDescriptor, inputStackDescriptor);
			deh.init(parent);
			return deh;			
		}
		else{						
			ElementDefaultHandler deh = elementDefaultH[--elementDefaultHFree];
			deh.init(parent);
			if(unknownElementHFree < unknownElementHMinFree) unknownElementHMinFree = unknownElementHFree;
			return deh;
		}		
	}
	
	void recycle(ElementDefaultHandler deh){		
		if(elementDefaultHFree == elementDefaultH.length){
		    if(elementDefaultHFree == elementDefaultHMaxSize) return;			
			ElementDefaultHandler[] increased = new ElementDefaultHandler[10+elementDefaultH.length];
			System.arraycopy(elementDefaultH, 0, increased, 0, elementDefaultHFree);
			elementDefaultH = increased;
		}
		elementDefaultH[elementDefaultHFree++] = deh; 
	}
	
	
	
	
	
	BoundUnexpectedElementHandler getBoundUnexpectedElementHandler(SimplifiedComponent e, ElementValidationHandler parent, Queue queue){				
		if(boundUnexpectedElementHFree == 0){
			BoundUnexpectedElementHandler ueh = new BoundUnexpectedElementHandler(debugWriter);
			ueh.init(this, activeInputDescriptor, inputStackDescriptor);
			ueh.init(e, parent, queue);			
			return ueh;			
		}
		else{						
			BoundUnexpectedElementHandler ueh = boundUnexpectedElementH[--boundUnexpectedElementHFree];
			ueh.init(e, parent, queue);
			if(boundUnexpectedElementHFree < boundUnexpectedElementHMinFree) boundUnexpectedElementHMinFree = boundUnexpectedElementHFree;
			return ueh;
		}		
	}
	
	void recycle(BoundUnexpectedElementHandler ueh){		
		if(boundUnexpectedElementHFree == boundUnexpectedElementH.length){
		    if(boundUnexpectedElementHFree == boundUnexpectedElementHMaxSize) return;
			BoundUnexpectedElementHandler[] increased = new BoundUnexpectedElementHandler[10+boundUnexpectedElementH.length];
			System.arraycopy(boundUnexpectedElementH, 0, increased, 0, boundUnexpectedElementHFree);
			boundUnexpectedElementH = increased;
		}
		boundUnexpectedElementH[boundUnexpectedElementHFree++] = ueh; 
	}
	
	BoundUnexpectedAmbiguousElementHandler getBoundUnexpectedAmbiguousElementHandler(List<SimplifiedComponent> boundElements, ElementValidationHandler parent, Queue queue){		
		if(boundUnexpectedAmbiguousEHFree == 0){
			BoundUnexpectedAmbiguousElementHandler uach = new BoundUnexpectedAmbiguousElementHandler(debugWriter);
			uach.init(this, activeInputDescriptor, inputStackDescriptor);
			uach.init(boundElements, parent, queue);
			return uach;
		}
		else{
			BoundUnexpectedAmbiguousElementHandler uach =  boundUnexpectedAmbiguousEH[--boundUnexpectedAmbiguousEHFree];
			uach.init(boundElements, parent, queue);
			if(boundUnexpectedElementHFree < boundUnexpectedElementHMinFree) boundUnexpectedElementHMinFree = boundUnexpectedElementHFree;
			return uach;
		}		
	}
	
	void recycle(BoundUnexpectedAmbiguousElementHandler ucch){		
		if(boundUnexpectedAmbiguousEHFree == boundUnexpectedAmbiguousEH.length){			
		    if(boundUnexpectedAmbiguousEHFree == boundUnexpectedAmbiguousEHMaxSize) return;
			BoundUnexpectedAmbiguousElementHandler[] increased = new BoundUnexpectedAmbiguousElementHandler[10+boundUnexpectedAmbiguousEH.length];
			System.arraycopy(boundUnexpectedAmbiguousEH, 0, increased, 0, boundUnexpectedAmbiguousEHFree);
			boundUnexpectedAmbiguousEH = increased;
		}
		boundUnexpectedAmbiguousEH[boundUnexpectedAmbiguousEHFree++] = ucch;
	}		
	
	BoundUnknownElementHandler getBoundUnknownElementHandler(BoundElementValidationHandler parent, Queue queue){
		if(boundUnknownElementHFree == 0){
			BoundUnknownElementHandler ueh = new BoundUnknownElementHandler(debugWriter);
			ueh.init(this, activeInputDescriptor, inputStackDescriptor);
			ueh.init(parent, queue);			
			return ueh;			
		}
		else{						
			BoundUnknownElementHandler ueh = boundUnknownElementH[--boundUnknownElementHFree];
			ueh.init(parent, queue);
			if(boundUnknownElementHFree < boundUnknownElementHMinFree) boundUnknownElementHMinFree = boundUnknownElementHFree;
			return ueh;
		}		
	}
	
	void recycle(BoundUnknownElementHandler ueh){		
		if(boundUnknownElementHFree == boundUnknownElementH.length){
            if(boundUnknownElementHFree == boundUnknownElementHMaxSize) return;			
			BoundUnknownElementHandler[] increased = new BoundUnknownElementHandler[10+boundUnknownElementH.length];
			System.arraycopy(boundUnknownElementH, 0, increased, 0, boundUnknownElementHFree);
			boundUnknownElementH = increased;
		}
		boundUnknownElementH[boundUnknownElementHFree++] = ueh; 
	}
	
	BoundElementDefaultHandler getBoundElementDefaultHandler(ElementEventHandler parent, Queue queue){
		if(boundElementDefaultHFree == 0){
			BoundElementDefaultHandler deh = new BoundElementDefaultHandler(debugWriter);
			deh.init(this, activeInputDescriptor, inputStackDescriptor);
			deh.init(parent, queue);
			return deh;			
		}
		else{						
			BoundElementDefaultHandler deh = boundElementDefaultH[--boundElementDefaultHFree];
			deh.init(parent, queue);
			if(boundUnknownElementHFree < boundUnknownElementHMinFree) boundUnknownElementHMinFree = boundUnknownElementHFree;
			return deh;
		}		
	}
	
	void recycle(BoundElementDefaultHandler deh){		
		if(boundElementDefaultHFree == boundElementDefaultH.length){
		    if(boundElementDefaultHFree == boundElementDefaultHMaxSize) return;			
			BoundElementDefaultHandler[] increased = new BoundElementDefaultHandler[10+boundElementDefaultH.length];
			System.arraycopy(boundElementDefaultH, 0, increased, 0, boundElementDefaultHFree);
			boundElementDefaultH = increased;
		}
		boundElementDefaultH[boundElementDefaultHFree++] = deh; 
	}
	
	
	
	
	ElementConcurrentHandler getElementConcurrentHandler(List<AElement> candidateDefinitions, ElementValidationHandler parent){		
		if(elementConcurrentHFree == 0){
			ElementConcurrentHandler ech = new ElementConcurrentHandler(debugWriter);
			ech.init(this, activeInputDescriptor, inputStackDescriptor, errorHandlerPool);			
			ech.init(candidateDefinitions, parent);
			return ech;
		}
		else{
			ElementConcurrentHandler ech = elementConcurrentH[--elementConcurrentHFree];
			ech.init(candidateDefinitions, parent);
			if(unknownElementHFree < unknownElementHMinFree) unknownElementHMinFree = unknownElementHFree;
			return ech; 
		}		
	}
	
	void recycle(ElementConcurrentHandler cpch){		
		if(elementConcurrentHFree == elementConcurrentH.length){
		    if(elementConcurrentHFree == elementConcurrentHMaxSize) return;			
			ElementConcurrentHandler[] increased = new ElementConcurrentHandler[10+elementConcurrentH.length];
			System.arraycopy(elementConcurrentH, 0, increased, 0, elementConcurrentHFree);
			elementConcurrentH = increased;
		}
		elementConcurrentH[elementConcurrentHFree++] = cpch;
	}	
	
	ElementParallelHandler getElementParallelHandler(ExternalConflictHandler conflictHandler, CandidatesConflictErrorHandler candidatesConflictErrorHandler, CandidatesEEH parent){		
		if(elementParallelHFree == 0){			
			ElementParallelHandler eph = new ElementParallelHandler(debugWriter);
			eph.init(this, activeInputDescriptor, inputStackDescriptor);
			eph.init(conflictHandler, candidatesConflictErrorHandler, parent);
			return eph;
		}
		else{
			ElementParallelHandler eph = elementParallelH[--elementParallelHFree];
			eph.init(conflictHandler, candidatesConflictErrorHandler, parent);
			if(unknownElementHFree < unknownElementHMinFree) unknownElementHMinFree = unknownElementHFree;
			return eph; 
		}		
	}	
	
	void recycle(ElementParallelHandler eph){	
		if(elementParallelHFree == elementParallelH.length){
		    if(elementParallelHFree == elementParallelHMaxSize) return;			
			ElementParallelHandler[] increased = new ElementParallelHandler[10+elementParallelH.length];
			System.arraycopy(elementParallelH, 0, increased, 0, elementParallelHFree);
			elementParallelH = increased;
		}
		elementParallelH[elementParallelHFree++] = eph;
	}

	ElementCommonHandler getElementCommonHandler(ExternalConflictHandler conflictHandler, int candidateCount, ValidatingEEH parent){		
		if(elementCommonHFree == 0){			
			ElementCommonHandler eph = new ElementCommonHandler(debugWriter);
			eph.init(this, activeInputDescriptor, inputStackDescriptor);
			eph.init(conflictHandler, candidateCount, parent);
			return eph;
		}
		else{
			ElementCommonHandler eph = elementCommonH[--elementCommonHFree];
			eph.init(conflictHandler, candidateCount, parent);
			if(elementCommonHFree < elementCommonHMinFree) elementCommonHMinFree = elementCommonHFree;
			return eph; 
		}		
	}	
	
	void recycle(ElementCommonHandler eph){	
		if(elementCommonHFree == elementCommonH.length){			
		    if(elementCommonHFree == elementCommonHMaxSize) return;
			ElementCommonHandler[] increased = new ElementCommonHandler[10+elementCommonH.length];
			System.arraycopy(elementCommonH, 0, increased, 0, elementCommonHFree);
			elementCommonH = increased;
		}
		elementCommonH[elementCommonHFree++] = eph;
	}

		
	
	UnexpectedAttributeHandler getUnexpectedAttributeHandler(SimplifiedComponent a, ElementValidationHandler parent){				
		if(unexpectedAttributeHFree == 0){
			UnexpectedAttributeHandler uah = new UnexpectedAttributeHandler(debugWriter);
			uah.init(this, inputStackDescriptor);
			uah.init(a, parent);			
			return uah;			
		}
		else{						
			UnexpectedAttributeHandler uah = unexpectedAttributeH[--unexpectedAttributeHFree];
			uah.init(a, parent);
			if(unexpectedAttributeHFree < unexpectedAttributeHMinFree) unexpectedAttributeHMinFree = unexpectedAttributeHFree;
			return uah;
		}		
	}
	
	void recycle(UnexpectedAttributeHandler ueh){		
		if(unexpectedAttributeHFree == unexpectedAttributeH.length){
		    if(unexpectedAttributeHFree == unexpectedAttributeHMaxSize) return;
			UnexpectedAttributeHandler[] increased = new UnexpectedAttributeHandler[10+unexpectedAttributeH.length];
			System.arraycopy(unexpectedAttributeH, 0, increased, 0, unexpectedAttributeHFree);
			unexpectedAttributeH = increased;
		}
		unexpectedAttributeH[unexpectedAttributeHFree++] = ueh; 
	}
	
	UnexpectedAmbiguousAttributeHandler getUnexpectedAmbiguousAttributeHandler(List<SimplifiedComponent> aa, ElementValidationHandler parent){		
		if(unexpectedAmbiguousAHFree == 0){
			UnexpectedAmbiguousAttributeHandler uach = new UnexpectedAmbiguousAttributeHandler(debugWriter);
			uach.init(this, inputStackDescriptor);
			uach.init(aa, parent);
			return uach;
		}
		else{
			UnexpectedAmbiguousAttributeHandler uach =  unexpectedAmbiguousAH[--unexpectedAmbiguousAHFree];
			uach.init(aa, parent);
			if(unexpectedAmbiguousAHFree < unexpectedAmbiguousAHMinFree) unexpectedAmbiguousAHMinFree = unexpectedAmbiguousAHFree;
			return uach;
		}		
	}
	
	void recycle(UnexpectedAmbiguousAttributeHandler ucch){		
		if(unexpectedAmbiguousAHFree == unexpectedAmbiguousAH.length){
            if(unexpectedAmbiguousAHFree == unexpectedAmbiguousAHMaxSize) return;			
			UnexpectedAmbiguousAttributeHandler[] increased = new UnexpectedAmbiguousAttributeHandler[10+unexpectedAmbiguousAH.length];
			System.arraycopy(unexpectedAmbiguousAH, 0, increased, 0, unexpectedAmbiguousAHFree);
			unexpectedAmbiguousAH = increased;
		}
		unexpectedAmbiguousAH[unexpectedAmbiguousAHFree++] = ucch;
	}		
	
	UnknownAttributeHandler getUnknownAttributeHandler(ElementValidationHandler parent){
		if(unknownAttributeHFree == 0){
			UnknownAttributeHandler uah = new UnknownAttributeHandler(debugWriter);
			uah.init(this, inputStackDescriptor);
			uah.init(parent);			
			return uah;			
		}
		else{						
			UnknownAttributeHandler uah = unknownAttributeH[--unknownAttributeHFree];
			uah.init(parent);
			if(unknownAttributeHFree < unknownAttributeHMinFree) unknownAttributeHMinFree = unknownAttributeHFree;
			return uah;
		}		
	}
	
	void recycle(UnknownAttributeHandler ueh){		
		if(unknownAttributeHFree == unknownAttributeH.length){
            if(unknownAttributeHFree == unknownAttributeHMaxSize) return;			
			UnknownAttributeHandler[] increased = new UnknownAttributeHandler[10+unknownAttributeH.length];
			System.arraycopy(unknownAttributeH, 0, increased, 0, unknownAttributeHFree);
			unknownAttributeH = increased;
		}
		unknownAttributeH[unknownAttributeHFree++] = ueh; 
	}
	
	
	
	public AttributeValidationHandler getAttributeValidationHandler(AAttribute attribute, ElementValidationHandler parent, ContextErrorHandlerManager contextErrorHandlerManager){		
		if(attributeVHFree == 0){
			AttributeValidationHandler avh = new AttributeValidationHandler(debugWriter);
			avh.init(this, inputStackDescriptor, matchHandler);
			avh.init(attribute,  parent, contextErrorHandlerManager);
			return avh;			
		}
		else{						
			AttributeValidationHandler avh = attributeVH[--attributeVHFree];
			avh.init(attribute, parent, contextErrorHandlerManager);
			if(attributeVHFree < attributeVHMinFree) attributeVHMinFree = attributeVHFree;
			return avh;
		}		
	}
		
	void recycle(AttributeValidationHandler eh){		
		if(attributeVHFree == attributeVH.length){
		    if(attributeVHFree == attributeVHMaxSize) return;
			AttributeValidationHandler[] increased = new AttributeValidationHandler[10+attributeVH.length];
			System.arraycopy(attributeVH, 0, increased, 0, attributeVHFree);
			attributeVH = increased;
		}
		attributeVH[attributeVHFree++] = eh; 
	}
    
    
    public CandidateAttributeValidationHandler getCandidateAttributeValidationHandler(AAttribute candidateAttribute, ElementValidationHandler parent, ExternalConflictHandler conflictHandler, int candidateIndex, TemporaryMessageStorage[] temporaryMessageStorage){		
		if(candidateAttributeVHFree == 0){
			CandidateAttributeValidationHandler avh = new CandidateAttributeValidationHandler(debugWriter);
			avh.init(this, activeInputDescriptor, inputStackDescriptor, matchHandler);
			avh.init(candidateAttribute,  parent, conflictHandler, candidateIndex, temporaryMessageStorage);
			return avh;			
		}
		else{						
			CandidateAttributeValidationHandler avh = candidateAttributeVH[--candidateAttributeVHFree];
			avh.init(candidateAttribute, parent, conflictHandler, candidateIndex, temporaryMessageStorage);
			if(candidateAttributeVHFree < candidateAttributeVHMinFree) candidateAttributeVHMinFree = candidateAttributeVHFree;
			return avh;
		}		
	}
		
	void recycle(CandidateAttributeValidationHandler eh){		
		if(candidateAttributeVHFree == candidateAttributeVH.length){
		    if(candidateAttributeVHFree == candidateAttributeVHMaxSize) return;
			CandidateAttributeValidationHandler[] increased = new CandidateAttributeValidationHandler[10+candidateAttributeVH.length];
			System.arraycopy(candidateAttributeVH, 0, increased, 0, candidateAttributeVHFree);
			candidateAttributeVH = increased;
		}
		candidateAttributeVH[candidateAttributeVHFree++] = eh; 
	}
	
	AttributeConcurrentHandler getAttributeConcurrentHandler(List<AAttribute> candidateDefinitions, ElementValidationHandler parent){		
		if(attributeConcurrentHFree == 0){
			AttributeConcurrentHandler ach = new AttributeConcurrentHandler(debugWriter);
			ach.init(this, inputStackDescriptor, errorHandlerPool);
			ach.init(candidateDefinitions, parent);
			return ach;
		}
		else{
			AttributeConcurrentHandler ach = attributeConcurrentH[--attributeConcurrentHFree];
			ach.init(candidateDefinitions, parent);
			if(attributeConcurrentHFree < attributeConcurrentHMinFree) attributeConcurrentHMinFree = attributeConcurrentHFree;
			return ach; 
		}		
	}
	
	void recycle(AttributeConcurrentHandler ach){		
		if(attributeConcurrentHFree == attributeConcurrentH.length){			
		    if(attributeConcurrentHFree == attributeConcurrentHMaxSize) return;
			AttributeConcurrentHandler[] increased = new AttributeConcurrentHandler[10+attributeConcurrentH.length];
			System.arraycopy(attributeConcurrentH, 0, increased, 0, attributeConcurrentHFree);
			attributeConcurrentH = increased;
		}
		attributeConcurrentH[attributeConcurrentHFree++] = ach;
	}

    AttributeParallelHandler getAttributeParallelHandler(CandidatesEEH parent, ExternalConflictHandler candidatesConflictHandler, CandidatesConflictErrorHandler elementCandidatesConflictErrorHandler){		
		if(attributeParallelHFree == 0){
			AttributeParallelHandler ach = new AttributeParallelHandler(debugWriter);
			ach.init(this, inputStackDescriptor);
			ach.init(parent, candidatesConflictHandler, elementCandidatesConflictErrorHandler);
			return ach;
		}
		else{
			AttributeParallelHandler ach = attributeParallelH[--attributeParallelHFree];
			ach.init(parent, candidatesConflictHandler, elementCandidatesConflictErrorHandler);
			if(attributeParallelHFree < attributeParallelHMinFree) attributeParallelHMinFree = attributeParallelHFree;
			return ach; 
		}		
	}
	
	void recycle(AttributeParallelHandler ach){		
		if(attributeParallelHFree == attributeParallelH.length){
            if(attributeParallelHFree == attributeParallelHMaxSize) return;			
			AttributeParallelHandler[] increased = new AttributeParallelHandler[10+attributeParallelH.length];
			System.arraycopy(attributeParallelH, 0, increased, 0, attributeParallelHFree);
			attributeParallelH = increased;
		}
		attributeParallelH[attributeParallelHFree++] = ach;
	}
    
    AttributeDefaultHandler getAttributeDefaultHandler(ComparableEEH parent){		
		if(attributeDefaultHFree == 0){
			AttributeDefaultHandler ach = new AttributeDefaultHandler(debugWriter);
			ach.init(this, inputStackDescriptor);
			ach.init(parent);
			return ach;
		}
		else{
			AttributeDefaultHandler ach = attributeDefaultH[--attributeDefaultHFree];
			ach.init(parent);
			if(attributeDefaultHFree < attributeDefaultHMinFree) attributeDefaultHMinFree = attributeDefaultHFree;
			return ach; 
		}		
	}
	
	void recycle(AttributeDefaultHandler ach){		
		if(attributeDefaultHFree == attributeDefaultH.length){
            if(attributeDefaultHFree == attributeDefaultHMaxSize) return;			
			AttributeDefaultHandler[] increased = new AttributeDefaultHandler[10+attributeDefaultH.length];
			System.arraycopy(attributeDefaultH, 0, increased, 0, attributeDefaultHFree);
			attributeDefaultH = increased;
		}
		attributeDefaultH[attributeDefaultHFree++] = ach;
	}

	CharactersValidationHandler getCharactersValidationHandler(MarkupEventHandler parent, CharsContentTypeHandler charsContentTypeHandler, ErrorCatcher contextErrorCatcher){		
		if(charactersValidationHFree == 0){
			CharactersValidationHandler ach = new CharactersValidationHandler(debugWriter);
			ach.init(matchHandler, validationContext, spaceHandler, activeInputDescriptor, inputStackDescriptor, this);
			ach.init(parent, charsContentTypeHandler, contextErrorCatcher);
			return ach;
		}else{
			CharactersValidationHandler ach = charactersValidationH[--charactersValidationHFree];
			ach.init(parent, charsContentTypeHandler, contextErrorCatcher);
			if(charactersValidationHFree < charactersValidationHMinFree) charactersValidationHMinFree = charactersValidationHFree;
			return ach; 
		}		
	}
		
	void recycle(CharactersValidationHandler ach){
		if(charactersValidationHFree == charactersValidationH.length){
		    if(charactersValidationHFree == charactersValidationHMaxSize) return;			
			CharactersValidationHandler[] increased = new CharactersValidationHandler[10+charactersValidationH.length];
			System.arraycopy(charactersValidationH, 0, increased, 0, charactersValidationHFree);
			charactersValidationH = increased;
		}
		charactersValidationH[charactersValidationHFree++] = ach;
	}
	
	
	StructuredDataValidationHandler getStructuredDataValidationHandler(ExceptPatternValidationHandler parent, StructuredDataContentTypeHandler structuredDataContentTypeHandler, ErrorCatcher contextErrorCatcher){		
		if(structuredDataValidationHFree == 0){
			StructuredDataValidationHandler ach = new StructuredDataValidationHandler(debugWriter);
			ach.init(matchHandler, validationContext, spaceHandler, activeInputDescriptor, inputStackDescriptor, this);
			ach.init(parent, structuredDataContentTypeHandler, contextErrorCatcher);
			return ach;
		}
		else{
			StructuredDataValidationHandler ach = structuredDataValidationH[--structuredDataValidationHFree];
			ach.init(parent, structuredDataContentTypeHandler, contextErrorCatcher);
			if(structuredDataValidationHFree < structuredDataValidationHMinFree) structuredDataValidationHMinFree = structuredDataValidationHFree;
			return ach; 
		}		
	}
		
	void recycle(StructuredDataValidationHandler ach){		
		if(structuredDataValidationHFree == structuredDataValidationH.length){
            if(structuredDataValidationHFree == structuredDataValidationHMaxSize) return;			
			StructuredDataValidationHandler[] increased = new StructuredDataValidationHandler[10+structuredDataValidationH.length];
			System.arraycopy(structuredDataValidationH, 0, increased, 0, structuredDataValidationHFree);
			structuredDataValidationH = increased;
		}
		structuredDataValidationH[structuredDataValidationHFree++] = ach;
	}
	
	DataValidationHandler getDataValidationHandler(ListPatternValidationHandler parent, DataContentTypeHandler dataContentTypeHandler, ErrorCatcher contextErrorCatcher){		
		if(dataValidationHFree == 0){
			DataValidationHandler ach = new DataValidationHandler(debugWriter);
			ach.init(matchHandler, validationContext, spaceHandler, activeInputDescriptor, inputStackDescriptor, this);
			ach.init(parent, dataContentTypeHandler, contextErrorCatcher);
			return ach;
		}
		else{
			DataValidationHandler ach = dataValidationH[--dataValidationHFree];
			ach.init(parent, dataContentTypeHandler, contextErrorCatcher);
			if(dataValidationHFree < dataValidationHMinFree) dataValidationHMinFree = dataValidationHFree; 
			return ach; 
		}		
	}
		
	void recycle(DataValidationHandler ach){		
		if(dataValidationHFree == dataValidationH.length){
            if(dataValidationHFree == dataValidationHMaxSize) return;
			DataValidationHandler[] increased = new DataValidationHandler[10+dataValidationH.length];
			System.arraycopy(dataValidationH, 0, increased, 0, dataValidationHFree);
			dataValidationH = increased;
		}
		dataValidationH[dataValidationHFree++] = ach;
	}
	

    public DefaultValueAttributeValidationHandler getDefaultValueAttributeValidationHandler(){		
		if(defaultVAttributeHFree == 0){
			DefaultValueAttributeValidationHandler dvah = new DefaultValueAttributeValidationHandler(debugWriter);
			dvah.init(this, inputStackDescriptor, matchHandler);
			return dvah;
		}
		else{
			DefaultValueAttributeValidationHandler dvah = defaultVAttributeH[--defaultVAttributeHFree];
            dvah.init(this, inputStackDescriptor, matchHandler);
            if(defaultVAttributeHFree < defaultVAttributeHMinFree) defaultVAttributeHMinFree = defaultVAttributeHFree; 			
			return dvah; 
		}		
	}
		
	void recycle(DefaultValueAttributeValidationHandler ach){		
		if(defaultVAttributeHFree == defaultVAttributeH.length){
            if(defaultVAttributeHFree == defaultVAttributeHMaxSize) return;
			DefaultValueAttributeValidationHandler[] increased = new DefaultValueAttributeValidationHandler[10+defaultVAttributeH.length];
			System.arraycopy(defaultVAttributeH, 0, increased, 0, defaultVAttributeHFree);
			defaultVAttributeH = increased;
		}
		defaultVAttributeH[defaultVAttributeHFree++] = ach;
	}
	
	ListPatternValidationHandler getListPatternValidationHandler(AListPattern listPattern, AbstractSDVH parent, ErrorCatcher errorCatcher){		
		if(listPatternVHFree == 0){
			ListPatternValidationHandler lpvh = new ListPatternValidationHandler(debugWriter);
			lpvh.init(this, inputStackDescriptor, spaceHandler);
			lpvh.init(listPattern, parent, errorCatcher);
			return lpvh;
		}
		else{
			ListPatternValidationHandler lpvh = listPatternVH[--listPatternVHFree];
			lpvh.init(listPattern, parent, errorCatcher);
			if(listPatternVHFree < listPatternVHMinFree) listPatternVHMinFree = listPatternVHFree;
			return lpvh; 
		}		
	}
	
	void recycle(ListPatternValidationHandler lpvh){		
		if(listPatternVHFree == listPatternVH.length){
            if(listPatternVHFree == listPatternVHMaxSize) return;			
			ListPatternValidationHandler[] increased = new ListPatternValidationHandler[10+listPatternVH.length];
			System.arraycopy(listPatternVH, 0, increased, 0, listPatternVHFree);
			listPatternVH = increased;
		}
		listPatternVH[listPatternVHFree++] = lpvh;
	}
	
	ExceptPatternValidationHandler getExceptPatternValidationHandler(AData data, AExceptPattern exceptPattern, AbstractDVH parent, ErrorCatcher errorCatcher){		
		if(exceptPatternVHFree == 0){
			ExceptPatternValidationHandler ach = new ExceptPatternValidationHandler(debugWriter);
			ach.init(this, inputStackDescriptor);
			ach.init(data, exceptPattern, parent, errorCatcher);
			return ach;
		}
		else{
			ExceptPatternValidationHandler ach = exceptPatternVH[--exceptPatternVHFree];
			ach.init(data, exceptPattern, parent, errorCatcher);
			if(exceptPatternVHFree < exceptPatternVHMinFree) exceptPatternVHMinFree = exceptPatternVHFree;
			return ach; 
		}		
	}
	
	void recycle(ExceptPatternValidationHandler ach){		
		if(exceptPatternVHFree == exceptPatternVH.length){
		    if(exceptPatternVHFree == exceptPatternVHMaxSize) return;			
			ExceptPatternValidationHandler[] increased = new ExceptPatternValidationHandler[10+exceptPatternVH.length];
			System.arraycopy(exceptPatternVH, 0, increased, 0, exceptPatternVHFree);
			exceptPatternVH = increased;
		}
		exceptPatternVH[exceptPatternVHFree++] = ach;
	}
	
	

	
	public BoundElementValidationHandler getElementValidationHandler(AElement element, BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		if(boundElementVHFree == 0){
			BoundElementValidationHandler bevh = new BoundElementValidationHandler(debugWriter);
			bevh.init(this, activeInputDescriptor, inputStackDescriptor, spaceHandler, matchHandler, errorHandlerPool);			
			bevh.init(element,  parent, bindingModel, queue, queuePool);
			return bevh;			
		}
		else{						
			BoundElementValidationHandler bevh = boundElementVH[--boundElementVHFree];
			bevh.init(element,  parent, bindingModel, queue, queuePool);
			if(boundElementVHFree < boundElementVHMinFree) boundElementVHMinFree = boundElementVHFree;
			return bevh;			
		}		
	}
	
	void recycle(BoundElementValidationHandler bevh){		
		if(boundElementVHFree == boundElementVH.length){
		    if(boundElementVHFree == boundElementVHMaxSize) return;
			BoundElementValidationHandler[] increased = new BoundElementValidationHandler[10+boundElementVH.length];
			System.arraycopy(boundElementVH, 0, increased, 0, boundElementVHFree);
			boundElementVH = increased;
		}
		boundElementVH[boundElementVHFree++] = bevh; 
	}
	
	public BoundStartValidationHandler getBoundStartValidationHandler(AElement element, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		if(boundStartVHFree == 0){
			BoundStartValidationHandler bevh = new BoundStartValidationHandler(debugWriter);
			bevh.init(this, activeInputDescriptor, inputStackDescriptor, spaceHandler, matchHandler, errorHandlerPool);			
			bevh.init(element,  null, bindingModel, queue, queuePool);
			return bevh;			
		}
		else{						
			BoundStartValidationHandler bevh = boundStartVH[--boundStartVHFree];
			bevh.init(element,  null, bindingModel, queue, queuePool);
			if(boundStartVHFree < boundStartVHMinFree) boundStartVHMinFree = boundStartVHFree;
			return bevh;			
		}		
	}
		
	void recycle(BoundStartValidationHandler bevh){		
		if(boundStartVHFree == boundStartVH.length){
		    if(boundStartVHFree == boundStartVHMaxSize) return;
			BoundStartValidationHandler[] increased = new BoundStartValidationHandler[5];
			System.arraycopy(boundStartVH, 0, increased, 0, boundStartVHFree);
			boundStartVH = increased;
		}
		boundStartVH[boundStartVHFree++] = bevh; 
	}

	BoundElementConcurrentHandler getElementConcurrentHandler(List<AElement> candidateDefinitions, BoundElementValidationHandler parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		if(boundElementConcurrentHFree == 0){
			BoundElementConcurrentHandler ech = new BoundElementConcurrentHandler(debugWriter);
			ech.init(this, activeInputDescriptor, inputStackDescriptor, errorHandlerPool);			
			ech.init(candidateDefinitions, parent, bindingModel, queue, queuePool);
			return ech;
		}
		else{
			BoundElementConcurrentHandler ech = boundElementConcurrentH[--boundElementConcurrentHFree];
			ech.init(candidateDefinitions, parent, bindingModel, queue, queuePool);
			if(boundElementConcurrentHFree < boundElementConcurrentHMinFree) boundElementConcurrentHMinFree = boundElementConcurrentHFree;
			return ech; 
		}		
	}
	
	void recycle(BoundElementConcurrentHandler cpch){		
		if(boundElementConcurrentHFree == boundElementConcurrentH.length){
            if(boundElementConcurrentHFree == boundElementConcurrentHMaxSize) return;			
			BoundElementConcurrentHandler[] increased = new BoundElementConcurrentHandler[10+boundElementConcurrentH.length];
			System.arraycopy(boundElementConcurrentH, 0, increased, 0, boundElementConcurrentHFree);
			boundElementConcurrentH = increased;
		}
		boundElementConcurrentH[boundElementConcurrentHFree++] = cpch;
	}		
	
	
	BoundElementParallelHandler getElementParallelHandler(ExternalConflictHandler conflictHandler, CandidatesConflictErrorHandler candidatesConflictErrorHandler, CandidatesEEH parent, BindingModel bindingModel, Queue queue, ValidatorQueuePool queuePool){		
		if(boundElementParallelHFree == 0){			
			BoundElementParallelHandler eph = new BoundElementParallelHandler(debugWriter);
			eph.init(this, activeInputDescriptor, inputStackDescriptor, errorHandlerPool);
			eph.init(conflictHandler, candidatesConflictErrorHandler, parent, bindingModel, queue, queuePool);
			return eph;
		}
		else{
			BoundElementParallelHandler eph = boundElementParallelH[--boundElementParallelHFree];
			eph.init(conflictHandler, candidatesConflictErrorHandler, parent, bindingModel, queue, queuePool);
			if(boundElementParallelHFree < boundElementParallelHMinFree) boundElementParallelHMinFree = boundElementParallelHFree; 
			return eph; 
		}		
	}	
	
	void recycle(BoundElementParallelHandler eph){	
		if(boundElementParallelHFree == boundElementParallelH.length){			
		    if(boundElementParallelHFree == boundElementParallelHMaxSize) return;
			BoundElementParallelHandler[] increased = new BoundElementParallelHandler[10+boundElementParallelH.length];
			System.arraycopy(boundElementParallelH, 0, increased, 0, boundElementParallelHFree);
			boundElementParallelH = increased;
		}
		boundElementParallelH[boundElementParallelHFree++] = eph;
	}
	
	
	public BoundAttributeValidationHandler getAttributeValidationHandler(AAttribute boundAttribute, ElementValidationHandler parent, ContextErrorHandlerManager contextErrorHandlerManager, BindingModel bindingModel, Queue queue, int entry){		
		if(boundAttributeVHFree == 0){
			BoundAttributeValidationHandler bavh = new BoundAttributeValidationHandler(debugWriter);
			bavh.init(this, inputStackDescriptor, matchHandler);
			bavh.init(boundAttribute,  parent, contextErrorHandlerManager, bindingModel, queue, entry);
			return bavh;			
		}
		else{						
			BoundAttributeValidationHandler bavh = boundAttributeVH[--boundAttributeVHFree];
			bavh.init(boundAttribute, parent, contextErrorHandlerManager, bindingModel, queue, entry);
			if(boundAttributeVHFree < boundAttributeVHMinFree) boundAttributeVHMinFree = boundAttributeVHFree;
			return bavh;
		}		
	}
		
	void recycle(BoundAttributeValidationHandler eh){		
		if(boundAttributeVHFree == boundAttributeVH.length){
		    if(boundAttributeVHFree == boundAttributeVHMaxSize) return;
			BoundAttributeValidationHandler[] increased = new BoundAttributeValidationHandler[10+boundAttributeVH.length];
			System.arraycopy(boundAttributeVH, 0, increased, 0, boundAttributeVHFree);
			boundAttributeVH = increased;
		}
		boundAttributeVH[boundAttributeVHFree++] = eh; 
	}
    
    public BoundCandidateAttributeValidationHandler getCandidateAttributeValidationHandler(AAttribute boundCandidateAttribute, ElementValidationHandler parent, ExternalConflictHandler conflictHandler, int candidateIndex, BindingModel bindingModel, Queue queue, int entry){		
		if(boundCandidateAttributeVHFree == 0){
			BoundCandidateAttributeValidationHandler bavh = new BoundCandidateAttributeValidationHandler(debugWriter);
			bavh.init(this, activeInputDescriptor, inputStackDescriptor, matchHandler);
			bavh.init(boundCandidateAttribute,  parent, conflictHandler, candidateIndex, bindingModel, queue, entry);
			return bavh;			
		}
		else{						
			BoundCandidateAttributeValidationHandler bavh = boundCandidateAttributeVH[--boundCandidateAttributeVHFree];
			bavh.init(boundCandidateAttribute, parent, conflictHandler, candidateIndex, bindingModel, queue, entry);
			if(boundCandidateAttributeVHFree < boundCandidateAttributeVHMinFree) boundCandidateAttributeVHMinFree = boundCandidateAttributeVHFree;
			return bavh;
		}		
	}
		
	void recycle(BoundCandidateAttributeValidationHandler eh){		
		if(boundCandidateAttributeVHFree == boundCandidateAttributeVH.length){
		    if(boundCandidateAttributeVHFree == boundCandidateAttributeVHMaxSize) return;
			BoundCandidateAttributeValidationHandler[] increased = new BoundCandidateAttributeValidationHandler[10+boundCandidateAttributeVH.length];
			System.arraycopy(boundCandidateAttributeVH, 0, increased, 0, boundCandidateAttributeVHFree);
			boundCandidateAttributeVH = increased;
		}
		boundCandidateAttributeVH[boundCandidateAttributeVHFree++] = eh; 
	}
	
	BoundAttributeConcurrentHandler getAttributeConcurrentHandler(List<AAttribute> candidateDefinitions, ElementValidationHandler parent, BindingModel bindingModel, Queue queue, int entry){		
		if(boundAttributeConcurrentHFree == 0){
			BoundAttributeConcurrentHandler ach = new BoundAttributeConcurrentHandler(debugWriter);
			ach.init(this, inputStackDescriptor, errorHandlerPool);
			ach.init(candidateDefinitions, parent, bindingModel, queue, entry);
			return ach;
		}
		else{
			BoundAttributeConcurrentHandler ach = boundAttributeConcurrentH[--boundAttributeConcurrentHFree];
			ach.init(candidateDefinitions, parent, bindingModel, queue, entry);
			if(boundAttributeConcurrentHFree < boundAttributeConcurrentHMinFree) boundAttributeConcurrentHMinFree = boundAttributeConcurrentHFree;
			return ach; 
		}		
	}
	
	void recycle(BoundAttributeConcurrentHandler ach){		
		if(boundAttributeConcurrentHFree == boundAttributeConcurrentH.length){
            if(boundAttributeConcurrentHFree == boundAttributeConcurrentHMaxSize) return;			
			BoundAttributeConcurrentHandler[] increased = new BoundAttributeConcurrentHandler[10+boundAttributeConcurrentH.length];
			System.arraycopy(boundAttributeConcurrentH, 0, increased, 0, boundAttributeConcurrentHFree);
			boundAttributeConcurrentH = increased;
		}
		boundAttributeConcurrentH[boundAttributeConcurrentHFree++] = ach;
	}
    
    
    BoundAttributeParallelHandler getAttributeParallelHandler(CandidatesEEH parent, ExternalConflictHandler candidatesConflictHandler, CandidatesConflictErrorHandler elementCandidatesConflictErrorHandler, BindingModel bindingModel, Queue queue, int entry){		
		if(boundAttributeParallelHFree == 0){
			BoundAttributeParallelHandler ach = new BoundAttributeParallelHandler(debugWriter);
			ach.init(this, inputStackDescriptor);
			ach.init(parent, candidatesConflictHandler, elementCandidatesConflictErrorHandler, bindingModel, queue, entry);
			return ach;
		}
		else{
			BoundAttributeParallelHandler ach = boundAttributeParallelH[--boundAttributeParallelHFree];
			ach.init(parent, candidatesConflictHandler, elementCandidatesConflictErrorHandler, bindingModel, queue, entry);
			if(boundAttributeParallelHFree < boundAttributeParallelHMinFree) boundAttributeParallelHMinFree = boundAttributeParallelHFree;
			return ach; 
		}		
	}
	
	void recycle(BoundAttributeParallelHandler ach){		
		if(boundAttributeParallelHFree == boundAttributeParallelH.length){			
		    if(boundAttributeParallelHFree == boundAttributeParallelHMaxSize) return;
			BoundAttributeParallelHandler[] increased = new BoundAttributeParallelHandler[10+boundAttributeParallelH.length];
			System.arraycopy(boundAttributeParallelH, 0, increased, 0, boundAttributeParallelHFree);
			boundAttributeParallelH = increased;
		}
		boundAttributeParallelH[boundAttributeParallelHFree++] = ach;
	}
}