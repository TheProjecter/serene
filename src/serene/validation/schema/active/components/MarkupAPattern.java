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

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.ActiveDefinitionPointer;
import serene.validation.schema.active.ActiveGrammarModel;
import serene.validation.schema.active.components.APattern;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.schema.simplified.Identifier;

import org.relaxng.datatype.ValidationContext;

public abstract class MarkupAPattern extends UniqueChildAPattern
									implements ActiveDefinitionPointer,
												CharsActiveType,
												NamedActiveTypeItem{
	
	protected int index;
	    
	protected ActiveGrammarModel grammarModel;	
	protected ActiveModelStackHandlerPool stackHandlerPool;
	
    Identifier identifier;
	
	public MarkupAPattern(int index,
	            Identifier identifier,
				APattern child,
				ActiveGrammarModel grammarModel,
				ActiveModelStackHandlerPool stackHandlerPool,
				ActiveModelRuleHandlerPool ruleHandlerPool){		
		super(child, ruleHandlerPool);
		this.index = index;
		this.grammarModel = grammarModel;		
		this.stackHandlerPool = stackHandlerPool;
		this.identifier = identifier;
        
	}
	
	public Identifier getIdentifier(){
	    return identifier;
	}
	
	public boolean identifierMatches(String ns, String name){
	    return identifier.matches(ns, name);
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
	    return false;
	}
	public boolean isTextContent(){
	    return false;
	}
	public boolean isCharsContent(){
	    return false;
	}	
	public boolean isStructuredDataContent(){
	    return false;
	}	
	public boolean isUnstructuredDataContent(){
	    return false;
	}	
	
	
	//ActiveDefinitionPointer
	//--------------------------------------------------------------------------
	public int getDefinitionIndex(){		
		return index;
	} 	
	//--------------------------------------------------------------------------
	
	
	
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
	
	//--------------------------------------------------------------------------
	
	
	//StructuredDataActiveType
	//--------------------------------------------------------------------------
	
	//--------------------------------------------------------------------------
	
	
	
	//CharsActiveType
	//--------------------------------------------------------------------------
	
	//--------------------------------------------------------------------------
    
    boolean isChildBranchRequired(AbstractAPattern child){
        return true;
    } 
}