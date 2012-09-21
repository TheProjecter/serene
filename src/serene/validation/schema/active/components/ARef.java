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
import serene.validation.schema.simplified.components.SRef;

import serene.validation.schema.active.RuleVisitor;
import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.ActiveDefinitionPointer;
import serene.validation.schema.active.ActiveGrammarModel;

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

public class ARef extends UniqueChildAPattern implements ActiveDefinitionPointer, 														
														AInnerPattern{	
	int index;	
	
	ActiveGrammarModel grammarModel;	
	
	SRef sref;
	
	public ARef(int index,
					ActiveGrammarModel grammarModel,
					ActiveModelRuleHandlerPool ruleHandlerPool,
					SRef sref){
		super(null, ruleHandlerPool);
		this.index = index;
		this.grammarModel = grammarModel;
		this.sref = sref;
	}
	
	
	public int getMinOccurs(){
	    return sref.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return sref.getMaxOccurs();
	}
		
	public boolean isElementContent(){
        if(child == null) return false;
        return child.isElementContent();
    }
	public boolean isAttributeContent(){
	    if(child == null) return false;
	    return child.isAttributeContent();
	}
	public boolean isDataContent(){
	    if(child == null) return false;
	    return child.isDataContent();
	}
	public boolean isValueContent(){
	    if(child == null) return false;
	    return child.isValueContent();
	}
	public boolean isListPatternContent(){
	    if(child == null) return false;
	    return child.isListPatternContent();
	}
	public boolean isTextContent(){
	    if(child == null) return false;
	    return child.isTextContent();
	}
	public boolean isCharsContent(){
	    if(child == null) return false;
	    return child.isCharsContent();
	}	
	public boolean isStructuredDataContent(){
	    if(child == null) return false;
	    return child.isStructuredDataContent();
	}	
	public boolean isUnstructuredDataContent(){
	    if(child == null) return false;
	    return child.isUnstructuredDataContent();
	}

	
	/**
	* Throws a NullPointerException if the definition is not assembled. 
	* This means the method can only be used during validation.
	*/
	public boolean isRequiredContent(){
		if(getMinOccurs() == 0) return false;	
		return child.isRequiredContent();
	}
	
        
	//ActiveDefinitionPointer
	//--------------------------------------------------------------------------
	public int getDefinitionIndex(){
		return index;
	}  
	public void assembleDefinition(){
		asParent(grammarModel.getRefDefinitionTopPattern(index));
	}
	public void releaseDefinition(){
		if(child != null){
			child.setReleased();
			grammarModel.recycleRefDefinitionTopPattern(index, child);
			child = null;
		}	
	}
	//--------------------------------------------------------------------------
	
	
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
	
	//--------------------------------------------------------------------------
	
	
	//StructuredDataActiveType
	//--------------------------------------------------------------------------
	
	//--------------------------------------------------------------------------
	
	
	
	//CharsActiveType
	//--------------------------------------------------------------------------
	
	//--------------------------------------------------------------------------
	
	
		
	//AttributesType
	//--------------------------------------------------------------------------	
	
	//--------------------------------------------------------------------------
	
	
	//ElementContentType
	//--------------------------------------------------------------------------
		
	//--------------------------------------------------------------------------

	
	
	public String getQName(){
		return sref.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return sref.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        //return simplifiedComponent.hashCode();
        //return definition.getTopPattern().functionalEquivalenceCode();
        return child.functionalEquivalenceCode();
    }  
    
    public SRef getCorrespondingSimplifiedComponent(){
        return sref;
    }
    
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
	
	public String toString(){
		String s = "ARef \""+index+"\""+ " min "+getMinOccurs()+" max "+getMaxOccurs();
		return s;
	}
}


