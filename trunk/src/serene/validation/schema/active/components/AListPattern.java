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
import serene.validation.schema.simplified.SListPattern;

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

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.StructureDoubleHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.ValidatorRuleHandlerPool;

import org.relaxng.datatype.ValidationContext;

public class AListPattern extends UniqueChildAPattern 
                                        implements DataActiveType, 
                                        StructuredDataActiveTypeItem{
	/*ValidatorStackHandlerPool stackHandlerPool;*/
		
	
	AData[] contextDatas;
		
	AValue[] contextValues;
	
	ARef[] contextRefs;
	
	SListPattern slist;
	
	boolean allowsElements;
	boolean allowsAttributes;
	boolean allowsDatas;
	boolean allowsValues;	
	boolean allowsListPatterns;
	boolean allowsText;
	
	public AListPattern(APattern child,
	                boolean allowsElements,
                    boolean allowsAttributes,
                    boolean allowsDatas,
                    boolean allowsValues,	
                    boolean allowsListPatterns,
                    boolean allowsText,
					/*ValidatorStackHandlerPool stackHandlerPool,*/
					ValidatorRuleHandlerPool ruleHandlerPool,
					SListPattern slist){
		super(child, ruleHandlerPool);
		this.allowsElements = allowsElements;
	    this.allowsAttributes = allowsAttributes;
	    this.allowsDatas = allowsDatas;
	    this.allowsValues = allowsValues;	
	    this.allowsListPatterns = allowsListPatterns;
	    this.allowsText = allowsText;
		/*this.stackHandlerPool = stackHandlerPool;*/
		this.slist = slist;
	}	


    public int getMinOccurs(){
	    return slist.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return slist.getMaxOccurs();
	}
	
	
	public boolean allowsDatas(){
	    return slist.allowsDatas();
	}
	public boolean allowsValues(){
	    return slist.allowsValues();
	}
	public boolean allowsUnstructuredDataContent(){
	    return slist.allowsDatas() || slist.allowsValues();
	}
	
	
	
	public void setContentMatches(List<AData> datas, List<AValue> values){
        if(child.isUnstructuredDataContent()) child.setMatches(datas, values);
	}
	
	
	
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts){
        listPatterns.add(this);
    }
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns){
        listPatterns.add(this);
    }
    
    
	
	public boolean isElementContent(){
        return false;
    }
	public boolean isAttributeContent(){
	    return false;
	}
	public boolean isDataContent(){
	    return false;
	}
	public boolean isValueContent(){
	    return false;
	}
	public boolean isListPatternContent(){
	    return true;
	}
	public boolean isTextContent(){
	    return false;
	}
	public boolean isCharsContent(){
	    return true;
	}	
	public boolean isStructuredDataContent(){
	    return true;
	}	
	public boolean isUnstructuredDataContent(){
	    return false;
	}
	
		
	
	//Type
	//--------------------------------------------------------------------------
	/*public ListPatternHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler){
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
		//return ruleHandlerPool.getStructureHandler(this, errorCatcher, stackHandler);*/
	}
	
	public MaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		//return ruleHandlerPool.getStructureHandler(this, errorCatcher, stackHandler);*/
	}	
	public String toString(){
		String s = "List"+ " min "+getMinOccurs()+" max "+getMaxOccurs();		
		return s;
	}
	
	public boolean isAncestorInContext(Rule another){
		return false;
	}
}