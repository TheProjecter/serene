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

import serene.validation.schema.active.components.APattern;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.RuleVisitor;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.ValidatorRuleHandlerPool;

public class AEmpty extends NoChildrenAPattern{
	SimplifiedComponent sempty;
	public AEmpty(ValidatorRuleHandlerPool ruleHandlerPool, SimplifiedComponent sempty){
		super(ruleHandlerPool);
		this.sempty = sempty;
	}

    public int getMinOccurs(){
	    //return sempty.getMinOccurs();
	    return 1;
	}
	
	public int getMaxOccurs(){
	    //return sempty.getMaxOccurs();
	    return 1;
	}	
		
	public boolean isRequiredContent(){
		return false;
	}	
	
    public boolean isRequiredBranch(){
		return false;
	}
    
	public String getQName(){
		return sempty.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sempty.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return sempty.hashCode();
    }   
    
    public SimplifiedComponent getCorrespondingSimplifiedComponent(){
        return sempty;
    }
    
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}	
	public StructureHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException("TODO");
	}
			
	public MinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		//return ruleHandlerPool.getStructureHandler(this, errorCatcher, stackHandler);*/
	}
	
	public MaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		//return ruleHandlerPool.getStructureHandler(this, errorCatcher, stackHandler);*/
	}
	
		
	public String toString(){
		String s = "AEmpty"+ " min "+getMinOccurs()+" max "+getMaxOccurs();		
		return s;
	}
}