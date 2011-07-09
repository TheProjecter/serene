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

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AbstractAPattern;

import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;

import sereneWrite.MessageWriter;

public abstract class MultipleChildrenAPattern extends AbstractAPattern{
 	protected APattern[] children; 
	MultipleChildrenAPattern(APattern[] children,
				ActiveModelRuleHandlerPool ruleHandlerPool,
				String qName, String location, 
				MessageWriter debugWriter){		
		super(ruleHandlerPool, qName, location, debugWriter);
		asParent(children);
	}
		
	protected void asParent(APattern[] children){
		this.children = children;
		if(children != null){		
			for(int i = 0; i< children.length; i++){
				((AbstractAPattern)children[i]).setParent(this);
				((AbstractAPattern)children[i]).setChildIndex(i);
			}
		}
	}	
	
	public APattern[] getChildren(){
		return children;
	}	
	
	public APattern getChild(int childIndex){
		if(children == null)return null;
		return children[childIndex];
	}
	
	public int getChildrenCount(){
        if(children == null) return 0;
		return children.length;
	}	
	
	public boolean isChildRequired(int childIndex){
		if(children[childIndex] == null)return false;
		return children[childIndex].isRequiredContent();
	}
	
	public boolean isRequiredContent(){
		if(minOccurs == 0) return false;		
		for(int i = 0; i < children.length; i++){
			if(children[i].isRequiredContent())return true;
		}
		return false;
	}
		
	public String toString(){
		String s = "MultipleChildrenAPattern";
		return s;
	}
}