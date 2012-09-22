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

import java.util.Map;
import java.util.List;

import serene.validation.schema.simplified.SExceptPattern;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.RuleVisitor;
import serene.validation.schema.active.ActiveDefinitionPointer;
import serene.validation.schema.active.ActiveGrammarModel;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.ActiveComponentVisitor;


import serene.validation.schema.simplified.SPattern;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.structure.ValidatorRuleHandlerPool;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import org.relaxng.datatype.ValidationContext;

public class AExceptPattern extends AbstractRule 
							implements StructuredDataActiveType, ActiveDefinitionPointer{
	APattern child;
	
	int index;

	ActiveGrammarModel grammarModel;
	
	/*ValidatorStackHandlerPool stackHandlerPool;*/
		
	AData[] contextDatas;
	
	AValue[] contextValues;
	
	AListPattern[] contextListPatterns;
	
	ARef[] contextRefs;
	
	SExceptPattern sexceptPattern;
	
	
	public AExceptPattern(int index,
			ActiveGrammarModel grammarModel,
			/*ValidatorStackHandlerPool stackHandlerPool,*/
			ValidatorRuleHandlerPool ruleHandlerPool,
			SExceptPattern sexceptPattern){
		super(ruleHandlerPool);
		this.index = index;
		this.grammarModel = grammarModel;
		/*this.stackHandlerPool = stackHandlerPool;*/
		this.sexceptPattern = sexceptPattern;		
	}	
	
	protected void asParent(APattern child){
		this.child = child;
		if(child != null){			
			((AbstractAPattern)child).setParent(this);
			((AbstractAPattern)child).setChildIndex(0);
		}
	}	
		

	public boolean allowsDatas(){
	    return sexceptPattern.allowsDatas();
	}
	public boolean allowsValues(){
	    return sexceptPattern.allowsValues();
	}
	public boolean allowsListPatterns(){
	    return sexceptPattern.allowsListPatterns();
	}	
	public boolean allowsStructuredDataContent(){
	    return sexceptPattern.allowsStructuredDataContent();
	}	
	public boolean allowsUnstructuredDataContent(){
	    return sexceptPattern.allowsUnstructuredDataContent();
	}	
	
	
	
	public void setContentMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns){
        if(child.isStructuredDataContent()) child.setMatches(datas, values, listPatterns);
    }
    public void setContentMatches(List<AData> datas, List<AValue> values){
        if(child.isUnstructuredDataContent()) child.setMatches(datas, values);
	}
	
	
	
	public APattern getChild(){
		return child;
	}
	
	public String getQName(){
		return sexceptPattern.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sexceptPattern.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return sexceptPattern.hashCode();
    }   
    
    public SExceptPattern getCorrespondingSimplifiedComponent(){
        return sexceptPattern;
    }
	
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}	
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}
	
	//ActiveDefinitionPointer
	//--------------------------------------------------------------------------
	public int getDefinitionIndex(){
		return index;
	}  
	public void assembleDefinition(){
		asParent(grammarModel.getExceptPatternDefinitionTopPattern(index));		
	}
	public void releaseDefinition(){
		if(child != null){
			child.setReleased();
			grammarModel.recycleExceptPatternDefinitionTopPattern(index, child);
			child = null;
		}	
	}
	//--------------------------------------------------------------------------
	
	
	//Type
	//--------------------------------------------------------------------------
	/*public ExceptPatternHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler){
		return ruleHandlerPool.getStructureHandler(this, errorCatcher, stackHandler);
	}*/
	
	/*public StackHandler getStackHandler(ErrorCatcher ec){	
		return stackHandlerPool.getContextStackHandler(this, ec);
	}	
	public ConcurrentStackHandler getStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher){		
		return stackHandlerPool.getConcurrentStackHandler(originalHandler, errorCatcher);
	}*/
	//--------------------------------------------------------------------------
	
	
	//DataActiveType
	//--------------------------------------------------------------------------
	
	//--------------------------------------------------------------------------
	
	
	//StructuredDataActiveType
	//--------------------------------------------------------------------------
	
	//--------------------------------------------------------------------------
	
	public boolean isChildRequired(){
		if(child == null)return false;
		return child.isRequiredContent();
	}
	public boolean isChildRequired(int childIndex){
	    if(childIndex != 0) throw new IllegalStateException();
		if(child == null)return false;
		return child.isRequiredContent();
	}
	
	/*public ExceptPatternHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
	}*/

	/*public MinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		//return ruleHandlerPool.getStructureHandler(this, errorCatcher, stackHandler);*//*
	}*/
	
	/*public MaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		//return ruleHandlerPool.getStructureHandler(this, errorCatcher, stackHandler);*//*
	}*/
	
	public String toString(){
		String s = "AExceptPattern ";
		return s;
	}
	
	public boolean isAncestorInContext(Rule another){
		return false;
	}
}