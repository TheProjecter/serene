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

import java.util.List;

import serene.validation.schema.active.components.AAttribute;

import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.stack.StackHandler;

import sereneWrite.MessageWriter;

abstract class AttributeDefinitionHandler extends ValidatingAEH{
    AAttribute attribute;
    
    MatchHandler matchHandler;
	
	StackHandler stackHandler;
	
    AttributeDefinitionHandler(MessageWriter debugWriter){
        super(debugWriter);
    }

    abstract void addChars(CharsActiveTypeItem charsDefinition);	
	abstract void addChars(List<CharsActiveTypeItem> charsCandidateDefinitions);
    
    
    boolean functionalEquivalent(ComparableAEH other){
        return other.functionalEquivalent(this);
    }
    
    boolean functionalEquivalent(AttributeDefinitionHandler other){
        return functionalEquivalenceCode() == other.functionalEquivalenceCode();
    }
	private int functionalEquivalenceCode(){
		return attribute.getDefinitionIndex();
	}
    
	boolean functionalEquivalent(UnexpectedAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAmbiguousAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnknownAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(AttributeConcurrentHandler other){
        return false;        
	}	
	boolean functionalEquivalent(AttributeParallelHandler other){
        return false;
    }
    boolean functionalEquivalent(AttributeDefaultHandler other){
        return false;
    }
}
