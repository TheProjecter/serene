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


import org.relaxng.datatype.Datatype;

import serene.validation.schema.simplified.components.SData;

import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.RuleVisitor;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

public class AData extends DatatypedCharsAPattern{	
	AExceptPattern exceptPattern;    
	
	SData sdata;
	public AData(Datatype datatype,
				AExceptPattern exceptPattern,
				ActiveGrammarModel grammarModel,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				SData sdata){
		super(datatype, grammarModel, ruleHandlerPool);
		this.sdata = sdata;
		asParent(exceptPattern);
	}
	
	
	public boolean isDataContent(){
	    return true;
	}
	public boolean isCharsContent(){
	    return true;
	}
	public boolean isStructuredDataContent(){
	    return true;
	}	
	public boolean isUnstructuredDataContent(){
	    return true;
	}
	
	
	
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts){
        datas.add(this);
    }
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns){
        datas.add(this);
    }
    public void setMatches(List<AData> datas, List<AValue> values){
        datas.add(this);
    }
    
    
	protected void asParent(AExceptPattern exceptPattern){		
		this.exceptPattern = exceptPattern;
		if(exceptPattern != null){		
			exceptPattern.setParent(this);
			exceptPattern.setChildIndex(0);
		}
	}	
	
	public AExceptPattern getExceptPattern(){
		return exceptPattern;
	}
	
	public String getQName(){
		return sdata.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sdata.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return sdata.hashCode();
    }   
    
    public SData getCorrespondingSimplifiedComponent(){
        return sdata;
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
		//return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);*/
	}
	
	public MaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		//return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);*/
	}
	
	public String toString(){
		return "AData datatype "+datatype+ " min "+minOccurs+" max "+maxOccurs+"  ";
	}
}