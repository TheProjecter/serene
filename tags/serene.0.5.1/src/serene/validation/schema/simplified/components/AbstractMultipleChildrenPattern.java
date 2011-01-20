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

package serene.validation.schema.simplified.components;

import serene.validation.schema.simplified.components.SPattern;

import sereneWrite.MessageWriter;

abstract class AbstractMultipleChildrenPattern extends SPattern{
 	protected SPattern[] children; 
	AbstractMultipleChildrenPattern(SPattern[] children,
				String qName, 
				String location, 
				MessageWriter debugWriter){		
		super(qName, location, debugWriter);
		asParent(children);
	}
		
	protected void asParent(SPattern[] children){
		this.children = children;
		if(children != null){		
			for(int i = 0; i< children.length; i++){				
				children[i].setParent(this);
				children[i].setChildIndex(i);
			}
		}
	}	
	
	public SPattern[] getChildren(){
		return children;
	}	
	
	public SPattern getChild(int childIndex){
		if(children == null)return null;
		return children[childIndex];
	}
	
	public int getChildrenCount(){
		return children.length;
	}	
	
	public String toString(){
		String s = "AbstractMultipleChildrenPattern";
		return s;
	}
}