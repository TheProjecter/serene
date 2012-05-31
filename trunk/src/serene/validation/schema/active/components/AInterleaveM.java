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

import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.schema.simplified.components.SMixed;

public class AInterleaveM extends AInterleave{
    
	SMixed smixed;
	
	public AInterleaveM(APattern[] children,
	            boolean allowsElements,
                boolean allowsAttributes,
                boolean allowsDatas,
                boolean allowsValues,	
                boolean allowsListPatterns,
                boolean allowsText,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool, 
				SMixed smixed){		
		super(children, 
		        allowsElements,
                allowsAttributes,
                allowsDatas,
                allowsValues,	
                allowsListPatterns,
                allowsText,
                stackHandlerPool, 
                ruleHandlerPool);
		this.smixed = smixed;
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
