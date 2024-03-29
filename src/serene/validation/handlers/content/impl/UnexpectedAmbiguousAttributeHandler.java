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
import java.util.Arrays;

import serene.validation.schema.simplified.SimplifiedComponent;

class UnexpectedAmbiguousAttributeHandler extends ErrorAEH{
	SimplifiedComponent[] attributes;
	
	UnexpectedAmbiguousAttributeHandler(){
		super();
	}
	
	public void recycle(){
		pool.recycle(this);
	}

	void init(List<SimplifiedComponent> attributes, ElementValidationHandler parent){
		this.parent = parent;
		this.attributes = attributes.toArray(new SimplifiedComponent[attributes.size()]);
	}
	
	void validateInContext(){		
		parent.unexpectedAmbiguousAttribute(Arrays.copyOf(attributes, attributes.length), inputStackDescriptor.getCurrentItemInputRecordIndex());
	}
 
    boolean functionalEquivalent(ComparableAEH other){
        return other.functionalEquivalent(this);
    }
    
    boolean functionalEquivalent(AttributeDefinitionHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAmbiguousAttributeHandler other){
        return other.functionalEquivalent(attributes);
    }
	private boolean functionalEquivalent(SimplifiedComponent[] otherSAttributes){
		int attributesCount = attributes.length;
		if(attributesCount != otherSAttributes.length)return false;
		for(int i = 0; i < attributesCount; i++){
			if(attributes[i] != otherSAttributes[i]) return false;
		}
		return true;
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
		return "UnexpectedAmbiguousAttributeHandler attributes "+Arrays.toString(attributes);
		
	}	
}