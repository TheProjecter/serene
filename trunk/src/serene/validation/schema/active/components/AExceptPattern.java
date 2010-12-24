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

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.RuleVisitor;
import serene.validation.schema.active.ActiveDefinition;
import serene.validation.schema.active.ActiveDefinitionPointer;
import serene.validation.schema.active.ActiveGrammarModel;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.ActiveComponentVisitor;


import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.ExceptPatternHandler;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import org.relaxng.datatype.ValidationContext;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;

public class AExceptPattern extends AbstractRule 
							implements StructuredDataActiveType, ActiveDefinitionPointer{
	APattern child;
	
	int index;
	
	ActiveDefinition definition;
	ActiveGrammarModel grammarModel;
	
	ActiveModelStackHandlerPool stackHandlerPool;
	
	boolean allowsDataContent;
	AData[] contextDatas;
	
	boolean allowsValueContent;
	AValue[] contextValues;
	
	boolean allowsListPatternContent;
	AListPattern[] contextListPatterns;
	
	ARef[] contextRefs;
	
	public AExceptPattern(int index,
			ActiveGrammarModel grammarModel,
			ActiveModelStackHandlerPool stackHandlerPool,
			ActiveModelRuleHandlerPool ruleHandlerPool,
			String qName, String location, 
			MessageWriter debugWriter){
		super(ruleHandlerPool, qName, location, debugWriter);
		this.index = index;
		this.grammarModel = grammarModel;
		this.stackHandlerPool = stackHandlerPool;		
	}	
	
	protected void asParent(APattern child){
		this.child = child;
		if(child != null){			
			((AbstractAPattern)child).setParent(this);
			((AbstractAPattern)child).setChildIndex(0);
		}
	}	
			
	public APattern getChild(){
		return child;
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
		setDefinition();
		setContextCache();
		assembleRefDefinitions();
	}
	public void releaseDefinition(){
		if(child != null){
			((AbstractAPattern)child).setParent(null);
			((AbstractAPattern)child).setChildIndex(-1);		
			child = null;
		}
		releaseRefDefinitions();
		resetContextCache();		
		definition.recycle();
	}
	//--------------------------------------------------------------------------
	protected void setDefinition(){
		definition = grammarModel.getExceptPatternDefinition(index);
		asParent(definition.getTopPattern());		
	}
	protected void setContextCache(){
		contextRefs = definition.getRefs();
	
		contextDatas = definition.getDatas();
		if(contextDatas != null && contextDatas.length != 0) allowsDataContent = true;
		
		contextValues = definition.getValues();
		if(contextValues != null && contextValues.length != 0) allowsValueContent = true;
		
		contextListPatterns = definition.getListPatterns();
		if(contextListPatterns != null && contextListPatterns.length != 0) allowsListPatternContent = true;
	}
	protected void assembleRefDefinitions(){
		if(contextRefs != null)
			for(int i = 0; i< contextRefs.length; i++){						
				contextRefs[i].assembleDefinition();						
				if(!allowsDataContent){
					allowsDataContent = contextRefs[i].allowsDataContent();
				}
				if(!allowsValueContent){
					allowsValueContent = contextRefs[i].allowsValueContent();
				}
				if(!allowsListPatternContent){
					allowsListPatternContent = contextRefs[i].allowsListPatternContent();
				}
			}
		if(allowsListPatternContent)
			for(int i = 0; i < contextListPatterns.length; i++){
				contextListPatterns[i].assembleRefDefinitions();
			}			
	}
	
	protected void resetContextCache(){
		allowsDataContent = false;
		contextDatas = null;
		
		allowsValueContent = false;
		contextValues = null;
		
		allowsListPatternContent = false;
		contextListPatterns = null;
	}
	protected void releaseRefDefinitions(){
		for(int i = 0; i< contextRefs.length; i++){						
			contextRefs[i].releaseDefinition();
		}	
	}	
	
	//Type
	//--------------------------------------------------------------------------
	public ExceptPatternHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler){
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
				|| allowsValueContent 
				|| allowsListPatternContent;
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
	
	
	//StructuredDataActiveType
	//--------------------------------------------------------------------------
	public boolean allowsListPatternContent(){
		return allowsListPatternContent;
	}	
	public List<AListPattern> getListPatterns(List<AListPattern> listPatterns){
		if(!allowsListPatternContent) return listPatterns;
		for(AListPattern listPattern: contextListPatterns){
			listPatterns.add(listPattern);
		}
		if(contextRefs != null){			 
			for(ARef ref : contextRefs){
				if(ref.allowsListPatternContent())
					listPatterns = ref.getListPatterns(listPatterns);
			}		
		}
		return listPatterns;
	}
	//--------------------------------------------------------------------------
	
	public boolean isChildRequired(){
		if(child == null)return false;
		return child.isRequired();
	}
	
	public ExceptPatternHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
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
		String s = "AExceptPattern ";
		return s;
	}
	
	public boolean isAncestorInContext(Rule another){
		return false;
	}
}