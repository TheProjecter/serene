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

package serene.validation.schema.active.components;

import serene.validation.schema.active.Rule;
import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.structure.ChildEventHandler;
import serene.validation.handlers.structure.impl.ParticleHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

public abstract class AbstractAPattern extends AbstractRule implements APattern{	
	
	
	protected int minOccurs;
	protected int maxOccurs;
	
	public AbstractAPattern(ActiveModelRuleHandlerPool ruleHandlerPool){		
		super(ruleHandlerPool);
		minOccurs = 1;
		maxOccurs = 1;
	}
		
	public void setReleased(){
	    parent = null;
	    childIndex = -1;
	}
	public void setMinOccurs(int minOccurs){
		this.minOccurs = minOccurs;
	}
	public void setMaxOccurs(int maxOccurs){
		this.maxOccurs = maxOccurs;
	}
	
	public int getMinOccurs(){
		return minOccurs;
	}
	
	public int getMaxOccurs(){
		return maxOccurs;
	}
	
	public ParticleHandler getParticleHandler(ChildEventHandler ceh, ErrorCatcher ec){
		return ruleHandlerPool.getParticleHandler(ceh, this, ec);
	}
	
	public boolean isRequiredContent(){        
		return minOccurs > 0;
	}
	   
    public boolean isRequiredBranch(){
        if( minOccurs == 0) return false;
        if(parent == null) return true;
        if(!(parent instanceof AbstractAPattern)) return true;
        return ((AbstractAPattern)parent).isChildBranchRequired(this);
    }
    
    boolean isChildBranchRequired(AbstractAPattern child){
        if( minOccurs == 0) return false;
        if(parent == null) return true;
        if(!(parent instanceof AbstractAPattern)) return true;
        return ((AbstractAPattern)parent).isChildBranchRequired(this);
    }
    
	boolean transmitsMultipleCardinality(){
		return false;
	}
	boolean isInterleaved(){
		return false;
	}	
}	