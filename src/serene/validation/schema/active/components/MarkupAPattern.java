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

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.ActiveDefinitionPointer;
import serene.validation.schema.active.ActiveNameClassPointer;
import serene.validation.schema.active.ActiveDefinition;
import serene.validation.schema.active.ActiveGrammarModel;
import serene.validation.schema.active.components.APattern;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import org.relaxng.datatype.ValidationContext;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;

public abstract class MarkupAPattern extends UniqueChildAPattern
									implements ActiveNameClassPointer,
												ActiveDefinitionPointer,
												CharsActiveType{
	APattern child;
	
	protected int index;
	    
	protected ActiveGrammarModel grammarModel;	
	protected ActiveModelStackHandlerPool stackHandlerPool;
	
    ActiveDefinition definition;
	
	boolean allowsDataContent;
	AData[] contextDatas;
	
	boolean allowsValueContent;
	AValue[] contextValues;
	
	boolean allowsListPatternContent;
	AListPattern[] contextListPatterns;
	
	boolean allowsTextContent;
	AText[] contextTexts;
			
	ARef[] contextRefs;	
	
	public MarkupAPattern(int index,
				APattern child,
				ActiveGrammarModel grammarModel,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				String qName, String location, 
				MessageWriter debugWriter){		
		super(child, ruleHandlerPool, qName, location, debugWriter);
		this.index = index;
		this.grammarModel = grammarModel;		
		this.stackHandlerPool = stackHandlerPool;
	}
	
	//ActiveNameClassPointer
	//--------------------------------------------------------------------------
	public int getNameClassIndex(){
		return index;
	}      
	//getNameClass() subclasses  
	//--------------------------------------------------------------------------
	
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
			AbstractAPattern achild = (AbstractAPattern)child;
			achild.setParent(null);
			achild.setChildIndex(-1);		
			child = null;
		}
		releaseRefDefinitions();
		resetContextCache();		
		definition.recycle();
	}
	//--------------------------------------------------------------------------
	protected abstract void setDefinition();
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
	}
	protected abstract void assembleRefDefinitions();
	
	protected void resetContextCache(){		
		allowsDataContent = false;
		contextDatas = null;
		
		allowsValueContent = false;
		contextValues = null;
		
		allowsListPatternContent = false;
		contextListPatterns = null;
		
		allowsTextContent = false;
		contextTexts = null;
		
	}
	protected void releaseRefDefinitions(){
		for(int i = 0; i< contextRefs.length; i++){						
			contextRefs[i].releaseDefinition();
		}	
	}	
	
	
	//Type
	//--------------------------------------------------------------------------
	//StructureHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler) subclasses
	
	public StackHandler getStackHandler(ErrorCatcher ec){	
		return stackHandlerPool.getContextStackHandler(this, ec);		
	}
	//do also except list 
	// return type concurrent
	public ConcurrentStackHandler getStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher){		
		return stackHandlerPool.getConcurrentStackHandler(originalHandler, errorCatcher);
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
}