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

package serene.dtdcompatibility;

import serene.validation.schema.simplified.SAttribute;

import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;

class CompatibilityControlAttribute extends AAttribute{
    
    CompatibilityControlAttribute(){
            super(-1, null, null, null, null, null);                
    }
    
    void init(ActiveGrammarModel grammarModel,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool){
        this.grammarModel = grammarModel;
        this.stackHandlerPool = stackHandlerPool;
        this.ruleHandlerPool = ruleHandlerPool;
    }
    
    void init(int index, SAttribute sattribute){
        this.sattribute = sattribute;
        this.index = index;
    }
    
    public String toString(){
		String s = "CompatibilityControlAttribute "+getIdentifier()+" "+index+ " min "+getMinOccurs()+" max "+getMaxOccurs();		
		return s;
	}
}
