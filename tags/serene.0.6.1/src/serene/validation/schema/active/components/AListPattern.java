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
import java.util.Map;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.components.SListPattern;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.RuleVisitor;
import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.ListPatternHandler;
import serene.validation.handlers.structure.impl.StructureValidationHandler;
import serene.validation.handlers.structure.impl.StructureDoubleHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import org.relaxng.datatype.ValidationContext;

public class AListPattern extends UniqueChildAPattern 
                                        implements DataActiveType, 
                                        StructuredDataActiveTypeItem{
	ActiveModelStackHandlerPool stackHandlerPool;
		
	boolean allowsDataContent;
	AData[] contextDatas;
	
	boolean allowsValueContent;
	AValue[] contextValues;
	
	ARef[] contextRefs;
	
	SListPattern slist;
	
	public AListPattern(APattern child,
					ActiveModelStackHandlerPool stackHandlerPool,
					ActiveModelRuleHandlerPool ruleHandlerPool,
					SListPattern slist){
		super(child, ruleHandlerPool);
		this.stackHandlerPool = stackHandlerPool;
		this.slist = slist;
	}	
	
	public void setContextCache(AData[] contextDatas, AValue[] contextValues, ARef[] contextRefs){
		this.contextDatas = contextDatas;
		if(contextDatas != null && contextDatas.length != 0) allowsDataContent = true;
		
		this.contextValues = contextValues;
		if(contextValues != null && contextValues.length != 0) allowsValueContent = true;
		
		this.contextRefs = contextRefs;
	}
	
	public void assembleRefDefinitions(){
		if(contextRefs != null)
			for(int i = 0; i< contextRefs.length; i++){						
				contextRefs[i].assembleDefinition();						
				if(!allowsDataContent){
					allowsDataContent = contextRefs[i].allowsDataContent();
				}
				if(!allowsValueContent){
					allowsValueContent = contextRefs[i].allowsValueContent();
				}
			}
	}
	
	//Type
	//--------------------------------------------------------------------------
	public ListPatternHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler){
		return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);
	}
	
	public StackHandler getStackHandler(ErrorCatcher ec){	
		return stackHandlerPool.getContextStackHandler(this, ec);
	}	
	public ConcurrentStackHandler getStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher){		
		return stackHandlerPool.getConcurrentStackHandler(originalHandler, errorCatcher);
	}
	//--------------------------------------------------------------------------
	
	
	//DataActiveType
	//--------------------------------------------------------------------------
	public boolean allowsChars(){
		return allowsDataContent 
				|| allowsValueContent;
	}
	
	public boolean allowsDataContent(){
		return allowsDataContent;
	}	
	public List<AData> getDataMatches(List<AData> dataMatches){	
		if(!allowsDataContent) return dataMatches;
		for(AData data: contextDatas){
			dataMatches.add(data);
		}
		if(contextRefs != null){			 
			for(ARef ref : contextRefs){
				if(ref.allowsDataContent())
					dataMatches = ref.getDataMatches(dataMatches);
			}		
		}
		return dataMatches;
	}
	
	public boolean allowsValueContent(){
		return allowsValueContent;
	}
	public List<AValue> getValueMatches(List<AValue> valueMatches){	
		if(!allowsValueContent) return valueMatches;
		for(AValue avalue: contextValues){
			valueMatches.add(avalue);
		}
		if(contextRefs != null){			 
			for(ARef ref : contextRefs){
				if(ref.allowsValueContent())
					valueMatches = ref.getValueMatches(valueMatches);
			}		
		}
		return valueMatches;
	}
	//--------------------------------------------------------------------------
	
	
	public String getQName(){
		return slist.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return slist.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return slist.hashCode();
    }   
    
    public SListPattern getCorrespondingSimplifiedComponent(){
        return slist;
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
		String s = "List"+ " min "+minOccurs+" max "+maxOccurs;		
		return s;
	}
	
	public boolean isAncestorInContext(Rule another){
		return false;
	}
}