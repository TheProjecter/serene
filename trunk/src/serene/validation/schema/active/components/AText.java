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

import java.util.List;

import serene.validation.schema.active.components.APattern;

import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.RuleVisitor;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

public abstract class AText extends CharsAPattern{
	public AText(ActiveModelRuleHandlerPool ruleHandlerPool){
		super(ruleHandlerPool);
		/*minOccurs = 0;
		maxOccurs = UNBOUNDED;*/
		
	}	
	
	public int getMinOccurs(){
	    return 0;
	}
	
	public int getMaxOccurs(){
	    return UNBOUNDED;
	}
	
	
	/*public boolean isTextContent(){
	    return true;
	}
	public boolean isCharsContent(){
	    return true;
	}	*/
	
    
	
    public void setMatches(List<AText> texts){
        texts.add(this);
    }    
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts){
        texts.add(this);
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
		throw new UnsupportedOperationException("TODO");
	}
	
	public MaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException("TODO");
	}
	
	public String toString(){
		String s = "AText"+ " min "+getMinOccurs()+" max "+getMaxOccurs();		
		return s;
	}
}