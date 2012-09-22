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

import serene.validation.schema.simplified.SText;

import serene.validation.handlers.structure.ValidatorRuleHandlerPool;

public class ATextT extends AText{
	SText stext;
	public ATextT(ValidatorRuleHandlerPool ruleHandlerPool, 
	            SText stext){
		super(ruleHandlerPool);
		this.stext = stext;
		/*minOccurs = 0;
		maxOccurs = UNBOUNDED;*/
		
	}	
			
	/*public int getMinOccurs(){
	    return stext.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return stext.getMaxOccurs();
	}*/
	
	
	public boolean isTextContent(){
	    return stext.isTextContent();
	}
	public boolean isCharsContent(){
	    return stext.isCharsContent();
	}	
	
	
	public String getQName(){
		return stext.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return stext.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return stext.hashCode();
    }   
    
    public SText getCorrespondingSimplifiedComponent(){
        return stext;
    }
}
