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

import serene.validation.schema.simplified.SimplifiedComponent;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import sereneWrite.MessageWriter;

public abstract class DatatypedCharsAPattern extends CharsAPattern implements DatatypedActiveTypeItem{
	Datatype datatype;	
	ActiveGrammarModel grammarModel;
	DatatypedCharsAPattern(Datatype datatype,
				ActiveGrammarModel grammarModel,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				SimplifiedComponent simplifiedComponent, 
				MessageWriter debugWriter){		
		super(ruleHandlerPool, simplifiedComponent, debugWriter);
		this.datatype = datatype;
		this.grammarModel = grammarModel;
	}
		
    public Datatype getDatatype(){
        return datatype;
    }
    
	//DatatypedActiveTypeItem
	//--------------------------------------------------------------------------
	public void datatypeMatches(String value, ValidationContext validationContext) throws DatatypeException{
        if(datatype == null) throw new DatatypeException(" No datatype definition.");
		datatype.checkValid(value, validationContext);
	}
	public void datatypeMatches(char[] chars, ValidationContext validationContext) throws DatatypeException{
		datatypeMatches(new String(chars), validationContext);
	}
	//--------------------------------------------------------------------------
	
	public String toString(){
		String s = "DatatypedCharsAPattern";
		return s;
	}
}