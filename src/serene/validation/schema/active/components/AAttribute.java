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

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.ActiveComponent;
import serene.validation.schema.active.RuleVisitor;
import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.ANameClass;
import serene.validation.schema.active.components.NamedActiveTypeItem;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.AttributeHandler;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.bind.Queue;

import sereneWrite.MessageWriter;

public class AAttribute extends MarkupAPattern  implements NamedActiveTypeItem{	
	public AAttribute(int index,
				ActiveGrammarModel grammarModel,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				SimplifiedComponent simplifiedComponent, 
				MessageWriter debugWriter){		
		super(index,
				null, 
				grammarModel,  
				stackHandlerPool, 
				ruleHandlerPool, 
				simplifiedComponent, 
				debugWriter);		
	}	
	
	
	//ActiveNameClassPointer
	//--------------------------------------------------------------------------
	//int getNameClassIndex() super
	public ANameClass getNameClass(){
		return grammarModel.getAttributeNameClass(index);
	}  
	//--------------------------------------------------------------------------
	
	//ActiveDefinitionPointer
	//--------------------------------------------------------------------------
	//int getDefinitionIndex()super
	//void assembleDefinition() super
	//void releaseDefinition() super	
	//--------------------------------------------------------------------------
	protected void setDefinition(){
		definition = grammarModel.getAttributeDefinition(index);
		asParent(definition.getTopPattern());
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
				if(!allowsTextContent){
					allowsTextContent = contextRefs[i].allowsTextContent();
				}
			}
		if(allowsListPatternContent)
			for(int i = 0; i < contextListPatterns.length; i++){
				contextListPatterns[i].assembleRefDefinitions();
			}
	}
	
	//resetContextCache() super
	//releaseRefDefinitions() super
	
	//Type
	//--------------------------------------------------------------------------
	public AttributeHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler){
		return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);
	}
	
	//StackHandler getStackHandler(ErrorCatcher ec) super
	//StackHandler getStackHandler(StackHandler originalHandler, int candidatesCount, ErrorCatcher errorCatcher) super
	//StackHandler getStackHandler(StackHandler originalHandler, int candidatesCount, ExternalConflictHandler conflictHandler, ErrorCatcher errorCatcher) super
	public StackHandler getStackHandler(StackHandler originalHandler, int candidatesCount, ExternalConflictHandler conflictHandler, ErrorCatcher errorCatcher, Queue targetQueue, int targetEntry,  Map<AElement, Queue> candidateQueues){
		throw new UnsupportedOperationException();
	}	
	//--------------------------------------------------------------------------
	
	//DataActiveType
	//--------------------------------------------------------------------------
	//boolean allowsDataContent() super
	//List<AData> getDataMatches(String value) super	
	//boolean allowsValueContent() super
	//List<AValue> getValueMatches(String value) super
	//--------------------------------------------------------------------------
		
	//StructuredDataActiveType
	//--------------------------------------------------------------------------
	//boolean allowsListPatternContent() super
	//List<AListPattern> getListPatterns(String value) super
	//--------------------------------------------------------------------------
		
	//CharsActiveType
	//--------------------------------------------------------------------------
	//boolean allowsTextContent() super
	//List<AText> getTextPatterns() super
	//--------------------------------------------------------------------------
	
	//NamedActiveTypeItem
	//--------------------------------------------------------------------------
	public boolean nameClassMatches(String namespace, String name){
		return grammarModel.getAttributeNameClass(index).matches(namespace, name);
	} 
	//--------------------------------------------------------------------------
	
	
	
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}
	
		
	
	public AttributeHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		/*return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);*/
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
		String s = "AAttribute "+getNameClass()+" "+index+ " min "+minOccurs+" max "+maxOccurs;		
		return s;
	}
	
	boolean isAncestorInContext(ActiveComponent another){
		return false;
	}
}