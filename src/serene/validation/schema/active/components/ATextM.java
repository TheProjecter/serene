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

package serene.validation.schema.active.components;

import serene.validation.schema.simplified.components.SMixed;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import sereneWrite.MessageWriter;

public class ATextM extends AText{
	SMixed smixed;
	public ATextM(ActiveModelRuleHandlerPool ruleHandlerPool, 
	            SMixed smixed, 
	            MessageWriter debugWriter){
		super(ruleHandlerPool, debugWriter);
		this.smixed = smixed;
		minOccurs = 0;
		maxOccurs = UNBOUNDED;
		
	}	
			
	
	public String getQName(){
		return smixed.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return smixed.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return smixed.hashCode();
    }   
    
    public SMixed getCorrespondingSimplifiedComponent(){
        return smixed;
    }
}
