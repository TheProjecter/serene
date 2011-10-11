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
import java.util.Arrays;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.RuleVisitor;
import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.ActiveDefinitionPointer;
import serene.validation.schema.active.ActiveDefinition;
import serene.validation.schema.active.ActiveGrammarModel;
import serene.validation.schema.active.ElementContentType;
import serene.validation.schema.active.AttributesType;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ANameClass;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.RefHandler;
import serene.validation.handlers.structure.impl.RefMinimalReduceHandler;
import serene.validation.handlers.structure.impl.RefMaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import org.relaxng.datatype.ValidationContext;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;

public class ARef extends UniqueChildAPattern implements ActiveDefinitionPointer, 
														ElementContentType, 
														AttributesType,
														CharsActiveType,
														AInnerPattern{	
	
	boolean allowsDataContent;
	AData[] contextDatas;
	
	boolean allowsValueContent;
	AValue[] contextValues;
	
	boolean allowsListPatternContent;
	AListPattern[] contextListPatterns;
	
	boolean allowsTextContent;
	AText[] contextTexts;
	
	boolean allowsElementContent;
	AElement[] contextElements;
	
	boolean allowsAttributes;	
	AAttribute[] contextAttributes;
	
	ARef[] contextRefs;
	
	int index;	
	
	ActiveGrammarModel grammarModel;	
	ActiveDefinition definition;
	
	public ARef(int index, 
					ActiveGrammarModel grammarModel,
					ActiveModelRuleHandlerPool ruleHandlerPool,
					SimplifiedComponent simplifiedComponent, 
					MessageWriter debugWriter){
		super(null, ruleHandlerPool, simplifiedComponent, debugWriter);
		this.index = index;
		this.grammarModel = grammarModel;
	}

	/**
	* Throws a NullPointerException if the definition is not assembled. 
	* This means the method can only be used during validation.
	*/
	public boolean isRequiredContent(){
		if(minOccurs == 0) return false;		
		return child.isRequiredContent();
	}
	
    public int functionalEquivalenceCode(){
        //return simplifiedComponent.hashCode();
        return definition.getTopPattern().functionalEquivalenceCode();
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
		definition = grammarModel.getRefDefinition(index);
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
		
		contextTexts = definition.getTexts();
		if(contextTexts != null && contextTexts.length != 0) allowsTextContent = true;
		
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
		
		allowsTextContent = false;
		contextTexts = null;
		
		allowsElementContent = false;
		contextElements = null;
		
		allowsAttributes = false;		
		contextAttributes = null;
	}	
	
	protected void releaseRefDefinitions(){
		for(int i = 0; i< contextRefs.length; i++){						
			contextRefs[i].releaseDefinition();
		}	
	}	
	
	
	//Type
	//--------------------------------------------------------------------------
	public StructureHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler) {
		throw new UnsupportedOperationException();
	}
	
	public StackHandler getStackHandler(ErrorCatcher ec){
		throw new UnsupportedOperationException();
	}
	public ConcurrentStackHandler getStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher){		
		throw new UnsupportedOperationException();
	}	
	
	//--------------------------------------------------------------------------
	
	//DataActiveType
	//--------------------------------------------------------------------------
	public boolean allowsChars(){
		return allowsDataContent 
				|| allowsValueContent 
				|| allowsListPatternContent
				|| allowsTextContent;
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
	
	
	
	//CharsActiveType
	//--------------------------------------------------------------------------
	public boolean allowsTextContent(){
		return allowsTextContent;
	}	
	public List<AText> getTexts(List<AText> texts){
		if(!allowsTextContent) return texts;
		for(AText text: contextTexts){
			texts.add(text);
		}
		if(contextRefs != null){			 
			for(ARef ref : contextRefs){
				if(ref.allowsTextContent())
					texts = ref.getTexts(texts);
			}		
		}
		return texts;
	}
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
		if(contextElements != null){
			for(AElement descendent : contextElements){
				if(descendent.nameClassMatches(namespace, name)){
					matches.add(descendent);
				}
			}
		}
		if(contextRefs != null){	
			for(ARef ref : contextRefs){
				if(ref.allowsElementContent())
					matches = ref.getElementMatches(namespace, name, matches);
			}		
		}
		return matches;
	}	
	//--------------------------------------------------------------------------

	
	
	public void accept(ActiveComponentVisitor v){
		v.visit(this);
	}
	public void accept(RuleVisitor rv){
		rv.visit(this);
	}

	public RefHandler getStructureHandler(ErrorCatcher errorCatcher, StructureHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, parent, stackHandler);
	}
	
	public RefMinimalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MinimalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMinimalReduceHandler(this, errorCatcher, parent, stackHandler);
	}
	public RefMaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		return ruleHandlerPool.getMaximalReduceHandler(this, errorCatcher, parent, stackHandler);
	}

	boolean isInterleaved(){
		if(parent instanceof AbstractAPattern)return ((AbstractAPattern)parent).isInterleaved();
		return false;
	}	
	
	boolean transmitsMultipleOccurrence(){
		if(maxOccurs > 1 || maxOccurs == UNBOUNDED)return true;
		if(parent instanceof AbstractAPattern)return ((AbstractAPattern)parent).isInterleaved();
		return false;
	}	
	
	public String toString(){
		String s = "ARef \""+index+"\""+ " min "+minOccurs+" max "+maxOccurs;
		return s;
	}
}


