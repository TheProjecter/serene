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

class UnknownAttributeHandler extends ErrorAEH{
		
	UnknownAttributeHandler(){
		super();
	}
	
	public void recycle(){
		pool.recycle(this);
	}

	void validateInContext(){
		parent.unknownAttribute(inputStackDescriptor.getCurrentItemInputRecordIndex());
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
        return false;
    }    
	boolean functionalEquivalent(UnknownAttributeHandler other){
        return true;
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
		return "UnknownAttributeHandler";
	}
}