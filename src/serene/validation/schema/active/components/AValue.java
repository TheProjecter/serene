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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.simplified.components.SValue;

import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.RuleVisitor;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

public class AValue extends DatatypedCharsAPattern{
	String ns;
	String charContent;
	
    boolean isSpace;
    boolean isSpaceAssessed;
        
    SValue svalue;
	public AValue(String ns, 		
                    Datatype datatype, 
					String charContent,
					ActiveGrammarModel grammarModel, 					
					ActiveModelRuleHandlerPool ruleHandlerPool,
					SValue svalue){
		super(datatype, grammarModel, ruleHandlerPool);
		this.ns = ns;
		this.charContent = charContent;
		this.svalue = svalue;
	}

	public int getMinOccurs(){
	    return svalue.getMinOccurs();
	}
	
	public int getMaxOccurs(){
	    return svalue.getMaxOccurs();
	}
	
	
	public boolean isValueContent(){
	    return true;
	}
	public boolean isCharsContent(){
	    return true;
	}	
	public boolean isStructuredDataContent(){
	    return true;
	}
	public boolean isUnstructuredDataContent(){
	    return true;
	}
	
	
	
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts){
        values.add(this);
    }
    public void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns){
        values.add(this);
    }
    public void setMatches(List<AData> datas, List<AValue> values){
        values.add(this);
    }
    
    
	
	public boolean valueMatches(char[] chars, ValidationContext validationContext){
		Object o1 = datatype.createValue(charContent, validationContext);
        Object o2 = datatype.createValue(new String(chars), validationContext);
		return datatype.sameValue(o1, o2);
	}
	public boolean valueMatches(String value, ValidationContext validationContext){ 
        Object o1 = datatype.createValue(charContent, validationContext);
        Object o2 = datatype.createValue(value, validationContext);
		return datatype.sameValue(o1, o2);
	}
    public boolean isSpaceOnly(){
        if(isSpaceAssessed)return isSpace;
        isSpaceAssessed = true;
        String v = charContent.trim();
        isSpace = v.equals("");
        return isSpace;
    }
    
    public String getQName(){
		return svalue.getQName();
	}
	
	public String getLocation(boolean restrictToFileName){
		return svalue.getLocation(restrictToFileName);
	}	
    
    public int functionalEquivalenceCode(){
        return svalue.hashCode();
    }   
    
    public SValue getCorrespondingSimplifiedComponent(){
        return svalue;
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
		throw new UnsupportedOperationException("TODO");
	}	
	public MaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException("TODO");
	}
	
	public String toString(){
		return "AValue datatype "+datatype+ " min "+getMinOccurs()+" max "+getMaxOccurs()+" /"+charContent+"/";
	}
}