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

package serene.validation.schema.simplified;

import java.util.Set;
import java.util.HashSet;

import serene.util.IntList;

public class RecursionModel{
	Set<SRef> recursiveReferences;
	IntList recursiveDefinitionIndexes;
	Set<SElement> recursionContainingElements;
	public RecursionModel(){
		recursiveReferences = new HashSet<SRef>();
		recursiveDefinitionIndexes = new IntList();
		
		recursionContainingElements = new HashSet<SElement>();
	}
	
	public void add(SRef ref){
		recursiveReferences.add(ref);
		int definitionIndex = ref.getDefinitionIndex(); 
		if(!recursiveDefinitionIndexes.contains(definitionIndex))recursiveDefinitionIndexes.add(definitionIndex);
	}
	
	public void add(SElement element){
	    recursionContainingElements.add(element);
	}
	
	public boolean isRecursiveReference(SRef ref){
		return recursiveReferences.contains(ref);
	}
	public boolean isRecursiveDefinition(int definitionIndex){
		return recursiveDefinitionIndexes.contains(definitionIndex);
	}
	
	public boolean hasRecursions(){
		return !recursiveDefinitionIndexes.isEmpty();
	}	
	
	void adjustCach(){
	    if(recursionContainingElements != null) {
	        for(SElement element: recursionContainingElements){
	            element.adjustForRecursions();
	        }
	    }
	}
	
	public String toString(){
		String rd = "";
		if(!recursiveDefinitionIndexes.isEmpty()){
			
			rd = "["+rd+recursiveDefinitionIndexes.get(0);
			if(recursiveDefinitionIndexes.size()>1){			
				for(int i =1; i < recursiveDefinitionIndexes.size(); i++){
					rd = rd+", "+ recursiveDefinitionIndexes.get(i);
				}				
			}
			rd = rd +"]";
			
		}
		return "recursiveDefinitionsIndexes "+rd
				+"\n"+"recursiveReferences "+recursiveReferences.toString();
		
	}
}