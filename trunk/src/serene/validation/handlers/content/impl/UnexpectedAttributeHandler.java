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

import serene.validation.schema.simplified.SimplifiedComponent;

import sereneWrite.MessageWriter;

class UnexpectedAttributeHandler extends ErrorAEH{
	SimplifiedComponent attribute;	
	
	UnexpectedAttributeHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	public void recycle(){
		pool.recycle(this);
	}

	void init(SimplifiedComponent attribute, ElementValidationHandler parent){
		this.parent = parent;
		this.attribute = attribute;
	}
	
	void validateInContext(){
		parent.unexpectedAttribute(inputStackDescriptor.getItemDescription(), attribute, inputStackDescriptor.getSystemId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber());
	}

    boolean functionalEquivalent(ComparableAEH other){
        return other.functionalEquivalent(this);
    }
    
    boolean functionalEquivalent(AttributeDefinitionHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAttributeHandler other){
        return other.functionalEquivalent(attribute);
	}
	private boolean functionalEquivalent(SimplifiedComponent otherSAttribute){
		return attribute == otherSAttribute; 
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
    public String toString(){
		return "UnexpectedAttributeHandler "+attribute.toString();
	}	
}