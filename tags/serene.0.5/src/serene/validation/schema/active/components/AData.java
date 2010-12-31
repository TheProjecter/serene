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

public class AData extends DatatypedCharsAPattern{	
	AParam[] param;
	AExceptPattern exceptPattern; 			
	public AData(String datatypeLibrary,
				String type, 
				AParam[] param, 
				AExceptPattern exceptPattern,
				ActiveGrammarModel grammarModel,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				String qName, String location, 
				MessageWriter debugWriter){
		super(datatypeLibrary, type, grammarModel, ruleHandlerPool, qName, location, debugWriter);
		asParent(param);
		asParent(exceptPattern);
	}
	
	protected void asParent(AParam[] param){
		this.param = param;
		if(param != null){		
			for(int i = 0; i< param.length; i++){
				param[i].setParent(this);
				param[i].setChildIndex(i);
			}
		}
	}	
	
	protected void asParent(AExceptPattern exceptPattern){		
		this.exceptPattern = exceptPattern;
		if(exceptPattern != null){		
			exceptPattern.setParent(this);
			exceptPattern.setChildIndex(0);
		}
	}	
	
	public AParam[] getParam(){
		return param;
	}
	
	public AParam getParam(int childIndex){
		if(param == null || param.length == 0)return null;
		return param[childIndex];
	}
	
	public AExceptPattern getExceptPattern(){
		return exceptPattern;
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
		//return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);*/
	}
	
	public MaximalReduceHandler getStructureHandler(ErrorCatcher errorCatcher, MaximalReduceHandler parent, StackHandler stackHandler){
		throw new UnsupportedOperationException();
		//return ruleHandlerPool.getStructureValidationHandler(this, errorCatcher, stackHandler);*/
	}
	
	public String toString(){
		//return "AData type "+type+ " min "+minOccurs+" max "+maxOccurs+"  "+hashCode();
		return "AData type "+type+ " min "+minOccurs+" max "+maxOccurs+"  ";
	}
}