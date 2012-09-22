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

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.schema.active.ActiveComponent;
import serene.validation.schema.active.RuleVisitor;
import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.ActiveGrammarModel;


import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;
import serene.validation.handlers.structure.impl.AttributeHandler;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.schema.simplified.Identifier;

import serene.bind.util.Queue;

public class AAttribute extends MarkupAPattern{
    protected SAttribute sattribute;	
	public AAttribute(int index,
	        Identifier identifier,
				ActiveGrammarModel grammarModel,
				/*ValidatorStackHandlerPool stackHandlerPool,*/
				ActiveModelRuleHandlerPool ruleHandlerPool,
				SAttribute sattribute){		
		super(index,
		        identifier,
				null, 
				grammarModel,  
				/*stackHandlerPool,*/ 
				ruleHandlerPool);		
		this.sattribute = sattribute;
	}	

	
	public boolean allowsDatas(){
	    return sattribute.allowsDatas();
	}
	public boolean allowsValues(){
	    return sattribute.allowsValues();
	}
	public boolean allowsListPatterns(){
	    return sattribute.allowsListPatterns();
	}
	public boolean allowsText(){
	    return sattribute.allowsText();
	}
	public boolean allowsCharsContent(){
	    return sattribute.allowsCharsContent();
	}	
	public boolean allowsStructuredDataContent(){
	    return sattribute.allowsStructuredDataContent();
	}	
	public boolean allowsUnstructuredDataContent(){
	    return sattribute.allowsUnstructuredDataContent();
	}
	
	
    public int getMinOccurs(){
	    return sattribute.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return sattribute.getMaxOccurs();
	}	
	
	public boolean isAttributeContent(){
	    return true;
	}
	
	
    public void setContentMatches(List<AText> texts){
        if(child.isTextContent()) child.setMatches(texts);
    }    
    public void setContentMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts){
        if(child.isCharsContent()) child.setMatches(datas, values, listPatterns, texts);
    }
    public void setContentMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns){
        if(child.isStructuredDataContent()) child.setMatches(datas, values, listPatterns);
    }
    public void setContentMatches(List<AData> datas, List<AValue> values){
        if(child.isUnstructuredDataContent()) child.setMatches(datas, values);
	}
	
	
	
    public void setAttributeMatches(String ns, String name, List<AAttribute> attributes){
	    if(identifier.matches(ns, name)) attributes.add(this);
    } 
    
	//ActiveDefinitionPointer
	//--------------------------------------------------------------------------
	//int getDefinitionIndex()super
	public void assembleDefinition(){
		asParent(grammarModel.getAttributeDefinitionTopPattern(index));
	}
	public void releaseDefinition(){
		if(child != null){
			child.setReleased();
			grammarModel.recycleAttributeDefinitionTopPattern(index, child);
			child = null;
		}	
	}
	//--------------------------------------------------------------------------
	
	
	
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
	//boolean allowsUnstructuredDataContent() super
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
	
	public String getQName(){
		return sattribute.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sattribute.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return sattribute.hashCode();
    }   
    
    public SAttribute getCorrespondingSimplifiedComponent(){
        return sattribute;
    }
	
    
	
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
		String s = "AAttribute "+identifier+" "+index+ " min "+getMinOccurs()+" max "+getMaxOccurs();		
		return s;
	}
	
	boolean isAncestorInContext(ActiveComponent another){
		return false;
	}
}