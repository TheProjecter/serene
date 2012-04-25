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

package serene.validation.schema.active;

import serene.validation.schema.simplified.components.SPattern;

import serene.util.ObjectIntHashMap;

public class ActiveDefinitionPool implements ActiveDefinitionRecycler{
	
	SPattern originalTopPattern;
	
	ActiveGrammarModel grammarModel;
	
	ActiveDefinitionDirector definitionDirector;
	ActiveComponentBuilder componentBuilder;
	
	int definitionFree; 
	int definitionMaxSize;
	ActiveDefinition[] definitions;	
	
	ActiveDefinitionPool(SPattern originalTopPattern,
							ActiveGrammarModel grammarModel,
							ActiveDefinitionDirector definitionDirector,
							ActiveComponentBuilder componentBuilder){
		this.originalTopPattern = originalTopPattern;
		this.grammarModel = grammarModel;
		this.definitionDirector = definitionDirector;
		this.componentBuilder = componentBuilder;
		
        //if(originalTopPattern == null) throw new NullPointerException();
        
		definitionFree = 0;
		definitionMaxSize = 10;
		definitions = new ActiveDefinition[5];
	}


    SPattern getOriginalTopPattern(){
        return originalTopPattern;
    }
	
	ActiveDefinition getActiveDefinition(){
		if(definitionFree == 0){
			return  definitionDirector.createDefinition(componentBuilder,
														originalTopPattern,
														grammarModel,
														this);

		}else{
			return definitions[--definitionFree];
		}
	}
	
	public void recycle(ActiveDefinition definition){
	    if(definitionFree == definitionMaxSize) return;
		if(definitionFree == definitions.length){
			ActiveDefinition[] increased = new ActiveDefinition[5+definitions.length];
			System.arraycopy(definitions, 0, increased, 0, definitionFree);
			definitions = increased;
		}
		definitions[definitionFree++] = definition;
	}
}