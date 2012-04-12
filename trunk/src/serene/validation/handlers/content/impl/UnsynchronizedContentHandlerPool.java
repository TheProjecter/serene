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

package serene.validation.handlers.content.impl;

import java.util.Arrays;

import javax.xml.validation.ValidatorHandler;

import serene.bind.BindingPool;

import serene.validation.schema.active.ActiveModelPool;

import sereneWrite.MessageWriter;

public class UnsynchronizedContentHandlerPool extends ContentHandlerPool{
	UnsynchronizedContentHandlerPool(MessageWriter debugWriter){
		super(debugWriter);				
	}
	
	public static UnsynchronizedContentHandlerPool getInstance(MessageWriter debugWriter){
		return new UnsynchronizedContentHandlerPool(debugWriter);
	}	
	
	public ValidatorEventHandlerPool getValidatorEventHandlerPool(){
		return new ValidatorEventHandlerPool(null, debugWriter);
	}	
	public void recycle(ValidatorEventHandlerPool eventHandlerPool){
	    throw new IllegalStateException();
	}
	
	void fill(ValidatorEventHandlerPool eventHandlerPool,
					ElementValidationHandler[] elementVHToFill,
					StartValidationHandler[] startVHToFill,
					UnexpectedElementHandler[] unexpectedElementHToFill,
					UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEHToFill,
					UnknownElementHandler[] unknownElementHToFill,
					ElementDefaultHandler[] elementDefaultHToFill,
					BoundUnexpectedElementHandler[] boundUnexpectedElementHToFill,
					BoundUnexpectedAmbiguousElementHandler[] boundUnexpectedAmbiguousEHToFill,
					BoundUnknownElementHandler[] boundUnknownElementHToFill,
					BoundElementDefaultHandler[] boundElementDefaultHToFill,
					ElementConcurrentHandler[] elementConcurrentHToFill,
					ElementParallelHandler[] elementParallelHToFill,
					ElementCommonHandler[] elementCommonHToFill,
					UnexpectedAttributeHandler[] unexpectedAttributeHToFill,
					UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAHToFill,
					UnknownAttributeHandler[] unknownAttributeHToFill,
					AttributeValidationHandler[] attributeVHToFill,
					CandidateAttributeValidationHandler[] candidateAttributeVHToFill,
					AttributeConcurrentHandler[] attributeConcurrentHToFill,
                    AttributeParallelHandler[] attributeParallelHToFill,
                    AttributeDefaultHandler[] attributeDefaultHToFill,
					CharactersValidationHandler[] charactersValidationHToFill,					
					StructuredDataValidationHandler[] structuredDataValidationHToFill,
					DataValidationHandler[] dataValidationHToFill,
                    DefaultValueAttributeValidationHandler[] defaultVAttributeHToFill,
					ListPatternValidationHandler[] listPatternVHToFill,
					ExceptPatternValidationHandler[] exceptPatternVHToFill,
					BoundElementValidationHandler[] boundElementVHToFill,
					BoundStartValidationHandler[] boundStartVHToFill,
					BoundElementConcurrentHandler[] boundElementConcurrentHToFill,
					BoundElementParallelHandler[] boundElementParallelHToFill,
					BoundAttributeValidationHandler[] boundAttributeVHToFill,
					BoundCandidateAttributeValidationHandler[] boundCandidateAttributeVHToFill,
					BoundAttributeConcurrentHandler[] boundAttributeConcurrentHToFill,
					BoundAttributeParallelHandler[] boundAttributeParallelHToFill){
	    throw new IllegalStateException();
	}	
	void recycle(int elementVHRecycledCount,
	                        int elementVHEffectivellyUsed,
							ElementValidationHandler[] elementVHRecycled,
							int startVHRecycledCount,	
							int startVHEffectivellyUsed,
							StartValidationHandler[] startVHRecycled,
							int unexpectedElementHRecycledCount,
							int unexpectedElementHEffectivellyUsed,
							UnexpectedElementHandler[] unexpectedElementHRecycled,
							int unexpectedAmbiguousEHRecycledCount,
							int unexpectedAmbiguousEHEffectivellyUsed,
							UnexpectedAmbiguousElementHandler[] unexpectedAmbiguousEHRecycled,
							int unknownElementHRecycledCount,
							int unknownElementHEffectivellyUsed,
							UnknownElementHandler[] unknownElementHRecycled,
							int elementDefaultHRecycledCount,
							int elementDefaultHEffectivellyUsed,
							ElementDefaultHandler[] elementDefaultHRecycled,
							int boundUnexpectedElementHRecycledCount,
							int boundUnexpectedElementHEffectivellyUsed,
							BoundUnexpectedElementHandler[] boundUnexpectedElementHRecycled,
							int boundUnexpectedAmbiguousEHRecycledCount,
							int boundUnexpectedAmbiguousEHEffectivellyUsed,
							BoundUnexpectedAmbiguousElementHandler[] boundUnexpectedAmbiguousEHRecycled,
							int boundUnknownElementHRecycledCount,
							int boundUnknownElementHEffectivellyUsed,
							BoundUnknownElementHandler[] boundUnknownElementHRecycled,
							int boundElementDefaultHRecycledCount,
							int boundElementDefaultHEffectivellyUsed,
							BoundElementDefaultHandler[] boundElementDefaultHRecycled,
							int elementConcurrentHRecycledCount,
							int elementConcurrentHEffectivellyUsed,
							ElementConcurrentHandler[] elementConcurrentHRecycled,
							int elementParallelHRecycledCount,
							int elementParallelHEffectivellyUsed,
							ElementParallelHandler[] elementParallelHRecycled,
							int elementCommonHRecycledCount,
							int elementCommonHEffectivellyUsed,
							ElementCommonHandler[] elementCommonHRecycled,
							int unexpectedAttributeHRecycledCount,
							int unexpectedAttributeHEffectivellyUsed,
							UnexpectedAttributeHandler[] unexpectedAttributeHRecycled,
							int unexpectedAmbiguousAHRecycledCount,
							int unexpectedAmbiguousAHEffectivellyUsed,
							UnexpectedAmbiguousAttributeHandler[] unexpectedAmbiguousAHRecycled,
							int unknownAttributeHRecycledCount,	
							int unknownAttributeHEffectivellyUsed,	
							UnknownAttributeHandler[] unknownAttributeHRecycled,
							int attributeVHRecycledCount,	
							int attributeVHEffectivellyUsed,	
							AttributeValidationHandler[] attributeVHRecycled,
							int candidateAttributeVHRecycledCount,
							int candidateAttributeVHEffectivellyUsed,
							CandidateAttributeValidationHandler[] candidateAttributeVHRecycled,
							int attributeConcurrentHRecycledCount,
							int attributeConcurrentHEffectivellyUsed,
							AttributeConcurrentHandler[] attributeConcurrentHRecycled,
                            int attributeParallelHRecycledCount,
                            int attributeParallelHEffectivellyUsed,
							AttributeParallelHandler[] attributeParallelHRecycled,
                            int attributeDefaultHRecycledCount,
                            int attributeDefaultHEffectivellyUsed,
							AttributeDefaultHandler[] attributeDefaultHRecycled,
							int charactersValidationHRecycledCount,
							int charactersValidationHEffectivellyUsed,
							CharactersValidationHandler[] charactersValidationHRecycled,
							int structuredDataValidationHRecycledCount,
							int structuredDataValidationHEffectivellyUsed,
							StructuredDataValidationHandler[] structuredDataValidationHRecycled,
							int dataValidationHRecycledCount,
							int dataValidationHEffectivellyUsed,
							DataValidationHandler[] dataValidationHRecycled,
                            int defaultVAttributeHRecycledCount,
                            int defaultVAttributeHEffectivellyUsed,
							DefaultValueAttributeValidationHandler[] defaultVAttributeHRecycled,
							int listPatternVHRecycledCount,
							int listPatternVHEffectivellyUsed,
							ListPatternValidationHandler[] listPatternVHRecycled,
							int exceptPatternVHRecycledCount,
							int exceptPatternVHEffectivellyUsed,
							ExceptPatternValidationHandler[] exceptPatternVHRecycled,
							int boundElementVHRecycledCount,
							int boundElementVHEffectivellyUsed,
							BoundElementValidationHandler[] boundElementVHRecycled,
							int boundStartVHRecycledCount,	
							int boundStartVHEffectivellyUsed,	
							BoundStartValidationHandler[] boundStartVHRecycled,
							int boundElementConcurrentHRecycledCount,
							int boundElementConcurrentHEffectivellyUsed,
							BoundElementConcurrentHandler[] boundElementConcurrentHRecycled,
							int boundElementParallelHRecycledCount,
							int boundElementParallelHEffectivellyUsed,
							BoundElementParallelHandler[] boundElementParallelHRecycled,
							int boundAttributeVHRecycledCount,	
							int boundAttributeVHEffectivellyUsed,	
							BoundAttributeValidationHandler[] boundAttributeVHRecycled,
                            int boundCandidateAttributeVHRecycledCount,
                            int boundCandidateAttributeVHEffectivellyUsed,
							BoundCandidateAttributeValidationHandler[] boundCandidateAttributeVHRecycled,
							int boundAttributeConcurrentHRecycledCount,
							int boundAttributeConcurrentHEffectivellyUsed,
							BoundAttributeConcurrentHandler[] boundAttributeConcurrentHRecycled,
                            int boundAttributeParallelHRecycledCount,
                            int boundAttributeParallelHEffectivellyUsed,
							BoundAttributeParallelHandler[] boundAttributeParallelHRecycled){
	    throw new IllegalStateException();
	}
}
