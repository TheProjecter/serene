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

import serene.validation.schema.simplified.SInterleave;

public class AInterleaveI extends AInterleave{
    
	SInterleave sinterleave;
	
	public AInterleaveI(APattern[] children,
	            boolean allowsElements,
                boolean allowsAttributes,
                boolean allowsDatas,
                boolean allowsValues,	
                boolean allowsListPatterns,
                boolean allowsText,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool, 
				SInterleave sinterleave){		
		super(children, 
		        allowsElements,
                allowsAttributes,
                allowsDatas,
                allowsValues,	
                allowsListPatterns,
                allowsText,
                stackHandlerPool,
                ruleHandlerPool);
		this.sinterleave = sinterleave;
	}	
	
	/*public boolean isElementContent(){
        return sinterleave.isElementContent();
    }
	public boolean isAttributeContent(){
	    return sinterleave.isAttributeContent();
	}
	public boolean isDataContent(){
	    return sinterleave.isDataContent();
	}
	public boolean isValueContent(){
	    return sinterleave.isValueContent();
	}
	public boolean isListPatternContent(){
	    return sinterleave.isListPatternContent();
	}
	public boolean isTextContent(){
	    return sinterleave.isTextContent();
	}
	public boolean isCharsContent(){
	    return sinterleave.isCharsContent();
	}	
	public boolean isStructuredDataContent(){
	    return sinterleave.isStructuredDataContent();
	}	
	public boolean isUnstructuredDataContent(){
	    return sinterleave.isUnstructuredDataContent();
	}*/
	
	public int getMinOccurs(){
	    return sinterleave.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return sinterleave.getMaxOccurs();
	}
	
	public String getQName(){
		return sinterleave.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sinterleave.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return sinterleave.hashCode();
    }   
    
    public SInterleave getCorrespondingSimplifiedComponent(){
        return sinterleave;
    }
    
}
