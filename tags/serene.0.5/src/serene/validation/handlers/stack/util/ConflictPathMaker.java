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

package serene.validation.handlers.stack.util;

import java.util.List;
import java.util.Arrays;

import serene.validation.schema.active.Rule;

import serene.validation.schema.active.components.AParam;
import serene.validation.schema.active.components.AExceptPattern;

import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AEmpty;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.ANotAllowed;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AGrammar;

import serene.validation.schema.active.RuleVisitor;

import sereneWrite.MessageWriter;

public class ConflictPathMaker implements RuleVisitor{
	int maxSize;
	int size;
	Rule[] path;
	Rule[][] innerPathes;
	
	MessageWriter debugWriter;
	
	public ConflictPathMaker(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	// Returns a bidimensional array containing all the pathes from every rule 
	// (exclusively) to the first common ancestor (inclusively).
	public Rule[][] getInnerPathes(List<? extends ActiveTypeItem> items){
		Rule[][] innerPathes = new Rule[items.size()][];
		for(int i = 0; i < innerPathes.length; i++){
			size = 0;
			maxSize = 10;		
			path = new Rule[maxSize];
			next(items.get(i).getParent());
			innerPathes[i] = new Rule[size];
			System.arraycopy(path, 0, innerPathes[i], 0, size);
		}
		// distance between the first common ancestor and the end of the path, practicly the count of common ancestors -1 
		// the same for all pathes
		int commonDistance = -1;
		int lastIndex = -1;
		all:{
		do{
			commonDistance++;
			lastIndex = size - 1 - commonDistance;//size has the last value of the last path handled
			if(lastIndex < 0) break all;
			Rule testRule = innerPathes[innerPathes.length-1][lastIndex];
			for(int i = 0; i < innerPathes.length-1; i++){
				lastIndex = innerPathes[i].length - 1 - commonDistance;			
				if(lastIndex < 0) break all;				
				else if(innerPathes[i][lastIndex] != testRule) break all;
			}
		}while(true);
		}
		commonDistance--;
		
		Rule[][] result = new Rule[innerPathes.length][];
		for(int  i = 0; i < innerPathes.length; i++){
			int resultPathSize = innerPathes[i].length-commonDistance;
			result[i] = new Rule[resultPathSize];
			System.arraycopy(innerPathes[i], 0, result[i], 0, resultPathSize);	
		}		
		innerPathes = null;
		return result;
	}
	
	public void visit(AExceptPattern rule){}	
	public void visit(AElement rule){}
	public void visit(AAttribute rule){}
	public void visit(AListPattern rule){}
	
	public void visit(AParam rule){
		throw new IllegalStateException();
	}
		
	public void visit(AChoicePattern rule){
		next(rule);
	}
	public void visit(AInterleave rule){
		next(rule);
	}
	public void visit(AGroup rule){
		next(rule);
	}
	
	public void visit(AEmpty rule){
		next(rule);
	}
	public void visit(AText rule){
		next(rule);
	}
	public void visit(ANotAllowed rule){
		next(rule);
	}
	public void visit(ARef rule){
		next(rule);
	}
	public void visit(AValue rule){
		next(rule);
	}
	public void visit(AData rule){
		next(rule);
	}
	public void visit(AGrammar rule){
		next(rule);
	}
	
	private void next(Rule rule){		
		if(size == maxSize)increaseSize();		
		path[size++] = rule;		
		rule.getParent().accept(this);
	}
	
	private void increaseSize(){
		maxSize += 10; 
		Rule[] increased = new Rule[maxSize];
		System.arraycopy(path, 0, increased, 0, size);
		path = increased;
	}
	
	public String toString(){
		//return "InnerPathMaker "+hashCode();
		return "InnerPathMaker ";
	}
}