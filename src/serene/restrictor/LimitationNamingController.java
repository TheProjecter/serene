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


import serene.util.IntStack;

import serene.validation.schema.simplified.SNameClass;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SGroup;
import serene.validation.schema.simplified.SChoicePattern;


import serene.validation.handlers.error.ErrorDispatcher;

abstract class LimitationNamingController extends NamingController{	
	final int RELEVANT = 1;
	final int IRELEVANT = -1;
	
	IntStack compositorRelevance;
	IntStack cardinalityRelevance;
	
	LimitationNamingController(ControllerPool pool, ErrorDispatcher errorDispatcher){
		super(pool, errorDispatcher);
		
		compositorRelevance = new IntStack();
		cardinalityRelevance = new IntStack();
		
		cardinalityRelevance.setNullValue(IRELEVANT);
		compositorRelevance.setNullValue(IRELEVANT);
	}
	
	void start(SInterleave interleave){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(ALLOW_OVERLAP_COMPOSITOR);		
		compositorPatterns.add(interleave);
		compositorRelevance.push(RELEVANT);
	}
	void start(SGroup group){
		namedPatterns.add(null);
		nameClasses.add(null);
		if(compositorRelevance.peek() == RELEVANT && cardinalityRelevance.peek() == RELEVANT){
			compositors.add(FORBID_OVERLAP_COMPOSITOR);
		}else{
			compositors.add(ALLOW_OVERLAP_COMPOSITOR);
		}
		compositorPatterns.add(group);		
		compositorRelevance.push(IRELEVANT);
	}

	void start(SChoicePattern choice){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(ALLOW_OVERLAP_COMPOSITOR);
		compositorPatterns.add(choice);		
		compositorRelevance.push(IRELEVANT);
	}	
	
	void startMultipleCardinality(){
		if(compositorRelevance.peek() == RELEVANT){
			cardinalityRelevance.push(RELEVANT);
		}else{
			cardinalityRelevance.push(IRELEVANT);
		}
	}
	

	void end(SInterleave interleave){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(END_COMPOSITOR);
		compositorPatterns.add(null);
		compositorRelevance.pop();
	}
	void end(SGroup group){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(END_COMPOSITOR);
		compositorPatterns.add(null);
		compositorRelevance.pop();
	}
	
	void end(SChoicePattern choice){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(END_COMPOSITOR);
		compositorPatterns.add(null);
		compositorRelevance.pop();
	}
	
	
	void endMultipleCardinality(){
		cardinalityRelevance.pop();
	}
}