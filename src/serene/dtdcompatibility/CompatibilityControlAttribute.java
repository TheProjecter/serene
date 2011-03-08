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

import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;

import sereneWrite.MessageWriter;

class CompatibilityControlAttribute extends AAttribute{
    
    CompatibilityControlAttribute(MessageWriter debugWriter){
            super(-1, null, null, null, null, null, debugWriter);                
    }
    
    void init(ActiveGrammarModel grammarModel,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool){
        this.grammarModel = grammarModel;
        this.stackHandlerPool = stackHandlerPool;
        this.ruleHandlerPool = ruleHandlerPool;
    }
    
    void init(int index, String qName, String location){
        this.qName = qName;
        this.location = location;
        this.index = index;
    }
    
    public String toString(){
		String s = "CompatibilityControlAttribute "+getNameClass()+" "+index+ " min "+minOccurs+" max "+maxOccurs;		
		return s;
	}
}
