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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.schema.active.ActiveComponentVisitor;
import serene.validation.schema.active.RuleVisitor;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.MinimalReduceHandler;
import serene.validation.handlers.structure.MaximalReduceHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import sereneWrite.MessageWriter;

public class AValue extends DatatypedCharsAPattern{
	String ns;
	String charContent;
	
    boolean isSpace;
    boolean isSpaceAssessed;
        
	public AValue(String ns, 		
                    Datatype datatype, 
					String charContent,
					ActiveGrammarModel grammarModel, 					
					ActiveModelRuleHandlerPool ruleHandlerPool,
					String qName, String location, 
					MessageWriter debugWriter){
		super(datatype, grammarModel, ruleHandlerPool, qName, location, debugWriter);
		this.ns = ns;
		this.charContent = charContent;
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
		return "AValue datatype "+datatype+ " min "+minOccurs+" max "+maxOccurs+" /"+charContent+"/";
	}
}