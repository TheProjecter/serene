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

package serene.restrictor;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Map;
import java.util.Set;

import serene.util.IntStack;
import serene.util.IntList;

import serene.validation.schema.simplified.components.SRef;

import sereneWrite.MessageWriter;

class OpenAlternativesHandler{
	int definitionCount;
	ArrayList<ArrayList<SRef>> definitionBlindLoops;
	ArrayList<ArrayList<SRef>> definitionInfiniteLoops;
	ArrayList<Map<SRef, ArrayList<SRef>>> definitionOpenAlternatives;

	IntStack undecidedLoopDefinitionPath;
	IntList interlocking;
	
	OpenAlternativesHandler openAlternativesHandler;
	
	MessageWriter debugWriter;
	
	OpenAlternativesHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	void init(int definitionCount,
			ArrayList<ArrayList<SRef>> definitionBlindLoops,
			ArrayList<ArrayList<SRef>> definitionInfiniteLoops,
			ArrayList<Map<SRef, ArrayList<SRef>>> definitionOpenAlternatives,			
			IntStack undecidedLoopDefinitionPath){
		this.definitionCount = definitionCount;
		this.definitionBlindLoops = definitionBlindLoops;
		this.definitionInfiniteLoops = definitionInfiniteLoops;
		this.definitionOpenAlternatives = definitionOpenAlternatives;
	
		this.undecidedLoopDefinitionPath = undecidedLoopDefinitionPath;		
	}
	
	void handleAlternatives(){
		for(int i = 0; i < definitionCount; i++){
			if(definitionOpenAlternatives.get(i) != null){	
				handle(definitionOpenAlternatives.get(i));
				definitionOpenAlternatives.set(i, null);
			}
		}
	}
	
	void handle(Map<SRef, ArrayList<SRef>> openAlternatives){		
		Set<SRef> undecided = openAlternatives.keySet();
		for(SRef undecidedRef : undecided){
			handle(undecidedRef, openAlternatives.get(undecidedRef));
		}
	}
	
	void handle(SRef undecidedRef, ArrayList<SRef> alternatives){
		
		if(interlocking != null)interlocking.clear();
		for(SRef alternative  : alternatives){
			int aIndex = alternative.getDefinitionIndex();
			if(isProvenIllegalRecursionDefinition(aIndex));
			else if(isOpenDefinition(aIndex)){
				if(interlocking == null) interlocking = new IntList();
				if(!interlocking.contains(aIndex))interlocking.add(aIndex);				
			}else return;			
		}	
		
		int uIndex = undecidedRef.getDefinitionIndex();
		if(interlocking != null && !interlocking.isEmpty()){
			undecidedLoopDefinitionPath.push(uIndex);
			for(int i = 0; i < interlocking.size(); i++){
				int iIndex = interlocking.get(i);
				if(!undecidedLoopDefinitionPath.contains(iIndex)){					
					if(openAlternativesHandler == null){
						openAlternativesHandler = new OpenAlternativesHandler(debugWriter);					
					}
					openAlternativesHandler.init(definitionCount,
												definitionBlindLoops,
												definitionInfiniteLoops,
												definitionOpenAlternatives,
												undecidedLoopDefinitionPath);
					openAlternativesHandler.handle(definitionOpenAlternatives.get(iIndex));
					definitionOpenAlternatives.set(iIndex, null);
					if(!isProvenIllegalRecursionDefinition(iIndex)){
						undecidedLoopDefinitionPath.pop();
						return;
					}
				}
			}
			undecidedLoopDefinitionPath.pop();
		}
		
		ArrayList<SRef> loopRefs = definitionInfiniteLoops.get(uIndex);
		if(loopRefs == null){
			loopRefs = new ArrayList<SRef>();
			definitionInfiniteLoops.set(uIndex, loopRefs);
		}
		loopRefs.add(undecidedRef);		
	}
	
	private boolean isProvenIllegalRecursionDefinition(int index){
		return (definitionBlindLoops.get(index) != null
			&& !definitionBlindLoops.get(index).isEmpty())
			|| (definitionInfiniteLoops.get(index) != null
			&& !definitionInfiniteLoops.get(index).isEmpty());
	}	
	
	private boolean isOpenDefinition(int index){
		if(definitionOpenAlternatives.get(index) == null) return false;
		return !definitionOpenAlternatives.get(index).isEmpty(); 
	}
}