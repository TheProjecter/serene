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
import java.util.Arrays;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ANameClass;
import serene.validation.schema.active.components.NamedActiveTypeItem;

import serene.validation.schema.active.RuleVisitor;
import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.ActiveGrammarModel;
import serene.validation.schema.active.ElementContentType;
import serene.validation.schema.active.AttributesType;

import serene.validation.handlers.structure.impl.ElementHandler;
import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.StructureDoubleHandler;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.ActiveComponentWriter;
import sereneWrite.MessageWriter;

public class AElement extends MarkupAPattern 
					implements AttributesType, ElementContentType, NamedActiveTypeItem{	
	boolean allowsElementContent;	 	
	AElement[] contextElements;
	
	boolean allowsAttributes;
	AAttribute[] contextAttributes;
	 
	ActiveComponentWriter acw;
	
	public AElement(int index,
				ActiveGrammarModel grammarModel,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				String qName, String location, 
				MessageWriter debugWriter){		
		super(index,
				null, 
				grammarModel, 
				stackHandlerPool, 
				ruleHandlerPool, 
				qName, 
				location, 
				debugWriter);		
		this.index = index;
		acw = new ActiveComponentWriter();
	}
		
	
	
	//ActiveNameClassPointer
	//--------------------------------------------------------------------------
	//int getNameClassIndex() super
	public ANameClass getNameClass(){
		return grammarModel.getElementNameClass(index);
	}  
	//--------------------------------------------------------------------------
	
	//ActiveDefinitionPointer
	//--------------------------------------------------------------------------
	//int getDefinitionIndex() super
	// void assembleDefinition() super
	// void releaseDefinition() super
	//--------------------------------------------------------------------------
	protected void setDefinition(){
		definition = grammarModel.getElementDefinition(index);
		asParent(definition.getTopPattern());
	}
	protected void setContextCache(){
		super.setContextCache();
		contextElements = definition.getElements();
		if(contextElements != null && contextElements.length != 0) allowsElementContent = true;
		
		contextAttributes = definition.getAttributes();
		if(contextAttributes != null && contextAttributes.length != 0) allowsAttributes = true;	
		
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
				if(!allowsElementContent){
					allowsElementContent = contextRefs[i].allowsElementContent();
				}
				if(!allowsAttributes){
					allowsAttributes = contextRefs[i].allowsAttributes();
				}
			}
		if(allowsListPatternContent){
			for(int i = 0; i < contextListPatterns.length; i++){
				contextListPatterns[i].assembleRefDefinitions();
			}
		}
	}
	
	protected void resetContextCache(){
		super.resetContextCache();
		allowsElementContent = false;
		contextElements = null;
		
		allowsAttributes = false;		
		contextAttributes = null;
	}	
	//releaseRefDefinitions() super
	
	
	
	//Type
	//--------------------------------------------------------------------------
	public ElementHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler){
		return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);
	}	
	//StackHandler getStackHandler(ErrorCatcher ec) super
	//StackHandler getStackHandler(StackHandler originalHandler, int candidatesCount, ErrorCatcher errorCatcher) super
	//StackHandler getStackHandler(StackHandler originalHandler, int candidatesCount, ExternalConflictHandler conflictHandler, ErrorCatcher errorCatcher) super	
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
	
	
	//AttributesType
	//--------------------------------------------------------------------------	
	public boolean allowsAttributes(){
		return allowsAttributes;
	}
	public List<AAttribute> getAttributeMatches(String namespace, String name, List<AAttribute> matches){
		if(!allowsAttributes) return matches;
		if(contextAttributes != null){
			for(AAttribute descendent : contextAttributes){				
				if(descendent.nameClassMatches(namespace, name)){
					matches.add(descendent);
				}
			}
		}
		if(contextRefs != null){			 
			for(ARef ref : contextRefs){
				if(ref.allowsAttributes())
					matches = ref.getAttributeMatches(namespace, name, matches);
			}		
		}
		return matches;
	}
	//--------------------------------------------------------------------------
	
	
	//ElementContentType
	//--------------------------------------------------------------------------
	public boolean allowsElementContent(){
		return allowsElementContent;
	}
	public List<AElement> getElementMatches(String namespace, String name, List<AElement> matches){			
		if(!allowsElementContent) return matches;
		//System.out.println(Arrays.toString(contextRefs));
		//System.out.println(toString()+"\t\t start "+matches);
		if(contextElements != null){
			for(AElement descendent : contextElements){				
				if(descendent.nameClassMatches(namespace, name)){
					matches.add(descendent);
				}
			}
		}
		//System.out.println(toString()+"\t\t direct "+matches);
		if(contextRefs != null){	
			for(ARef ref : contextRefs){
				if(ref.allowsElementContent())
					matches = ref.getElementMatches(namespace, name, matches);
			}		
		}
		//System.out.println(toString()+"\t\t ref "+matches);
		return matches;
	}	
	//--------------------------------------------------------------------------
	
	
	//NamedActiveTypeItem
	//--------------------------------------------------------------------------
	public boolean nameClassMatches(String namespace, String name){	
		return grammarModel.getElementNameClass(index).matches(namespace, name);
	} 
	//--------------------------------------------------------------------------
	
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}
			
	public ElementHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		//return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);*/
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
		//String s = "AElement "+hashCode()+" " + getNameClass()+" "+index+ " min "+minOccurs+" max "+maxOccurs;
		String s = "AElement  " + getNameClass()+" "+index+ " min "+minOccurs+" max "+maxOccurs;
		return s;
	}
	
	public boolean isAncestorInContext(Rule another){
		return false;
	}
}