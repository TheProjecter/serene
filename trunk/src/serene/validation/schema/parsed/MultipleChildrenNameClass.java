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


package serene.validation.schema.parsed;

import java.util.Map;

import serene.bind.util.DocumentIndexedData;

abstract class MultipleChildrenNameClass extends NameClass{
 	ParsedComponent[] children; 
	MultipleChildrenNameClass(/*Map<String, String> prefixMapping,*/
	                                int xmlBase,
                                    int ns, 
                                    int datatypeLibrary, 
                                    ParsedComponent[] children,
                                    int recordIndex,
                                    DocumentIndexedData documentIndexedData){		
		super(/*prefixMapping,*/ xmlBase, ns, datatypeLibrary, recordIndex, documentIndexedData);
		asParent(children);
	}
		
	void asParent(ParsedComponent[] children){
		this.children = children;
		if(children != null){		
			for(int i = 0; i< children.length; i++){
				children[i].setParent(this);
				children[i].setChildIndex(i);
			}
		}
	}	
	
	public ParsedComponent[] getChildren(){
		return children;
	}	
	
	public ParsedComponent getChild(int childIndex){
		if(children == null)return null;
		return children[childIndex];
	}
	
	public int getChildrenCount(){
        if(children == null) return 0;
		return children.length;
	}	
	
	public String toString(){
		String s = "MultipleChildrenPattern";
		return s;
	}
}
