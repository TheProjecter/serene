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

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SElement;

import serene.validation.schema.active.Rule;

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

import serene.validation.schema.simplified.Identifier;

import serene.validation.handlers.error.ErrorCatcher;

//import sereneWrite.ActiveComponentWriter;

public class AElement extends MarkupAPattern 
					implements AttributesType, ElementContentType{
	
	//ActiveComponentWriter acw;
	
	SElement selement;
	
	public AElement(int index,
	            Identifier identifier,
				ActiveGrammarModel grammarModel,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				SElement selement){		
		super(index,
		        identifier,
				null, 
				grammarModel, 
				stackHandlerPool, 
				ruleHandlerPool);		
		this.index = index;
		this.selement = selement;
		//acw = new ActiveComponentWriter();
		
	}
	
	
	public boolean allowsElements(){
        return selement.allowsElements();
    }
	public boolean allowsAttributes(){
	    return selement.allowsAttributes();
	}
	public boolean allowsDatas(){
	    return selement.allowsDatas();
	}
	public boolean allowsValues(){
	    return selement.allowsValues();
	}
	public boolean allowsListPatterns(){
	    return selement.allowsListPatterns();
	}
	public boolean allowsText(){
	    return selement.allowsText();
	}
	public boolean allowsCharsContent(){
	    return selement.allowsCharsContent();
	}	
	public boolean allowsStructuredDataContent(){
	    return selement.allowsStructuredDataContent();
	}	
	public boolean allowsUnstructuredDataContent(){
	    return selement.allowsUnstructuredDataContent();
	}
	
	
	public int getMinOccurs(){
	    return selement.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return selement.getMaxOccurs();
	}
	
	public void setElementContentMatches(String ns, String name, List<AElement> elements){
	    if(child.isElementContent()) child.setElementMatches(ns, name, elements);
	}
    public void setAttributeContentMatches(String ns, String name, List<AAttribute> attributes){
	    if(child.isAttributeContent()) child.setAttributeMatches(ns, name, attributes);
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
	
	
	
	public void setElementMatches(String ns, String name, List<AElement> elements){
	    if(identifier.matches(ns, name)) elements.add(this);
	}
    
    
	public boolean isElementContent(){
        return true;
    }
    
	//ActiveDefinitionPointer
	//--------------------------------------------------------------------------
	//int getDefinitionIndex() super
	public void assembleDefinition(){
		asParent(grammarModel.getElementDefinitionTopPattern(index));
	}
	public void releaseDefinition(){
		if(child != null){
			child.setReleased();
			grammarModel.recycleElementDefinitionTopPattern(index, child);
			child = null;
		}	
	}
	//--------------------------------------------------------------------------
	
	
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
	
	
	//AttributesType
	//--------------------------------------------------------------------------	
	
	//--------------------------------------------------------------------------
	
	
	//ElementContentType
	//--------------------------------------------------------------------------
		
	//--------------------------------------------------------------------------
	
		
	public String getQName(){
		return selement.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return selement.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return selement.hashCode();
    }   
    
    public SElement getCorrespondingSimplifiedComponent(){
        return selement;
    }
	
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
		//String s = "AElement "+hashCode()+" " + getNameClass()+" "+index+ " min "+getMinOccurs()+" max "+getMaxOccurs();
		String s = "AElement  " + identifier+" "+index+ " min "+getMinOccurs()+" max "+getMaxOccurs();
		return s;
	}
	
	public boolean isAncestorInContext(Rule another){
		return false;
	}
}