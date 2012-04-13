/*
Copyright 2011 Radu Cernuta 

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

import serene.validation.handlers.content.AttributeEventHandler;

abstract class ComparableAEH extends AbstractAEH{

	ComparableAEH(){
		super();
	}
		
    abstract boolean functionalEquivalent(ComparableAEH other);
    
	abstract boolean functionalEquivalent(AttributeDefinitionHandler other);
	abstract boolean functionalEquivalent(UnexpectedAttributeHandler other);
	abstract boolean functionalEquivalent(UnexpectedAmbiguousAttributeHandler other);
	abstract boolean functionalEquivalent(UnknownAttributeHandler other);
	abstract boolean functionalEquivalent(AttributeConcurrentHandler other);
	abstract boolean functionalEquivalent(AttributeParallelHandler other);
    abstract boolean functionalEquivalent(AttributeDefaultHandler other);
}
