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

/*
import java.util.ArrayList;

import org.xml.sax.SAXException;

import serene.util.IntList;

*/

import serene.util.IntStack;

import serene.validation.schema.simplified.components.SNameClass;
import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SOptional;
import serene.validation.schema.simplified.components.SOneOrMore;
import serene.validation.schema.simplified.components.SZeroOrMore;


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
	void start(SMixed mixed){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(ALLOW_OVERLAP_COMPOSITOR);		
		compositorPatterns.add(mixed);
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
	
	void start(SOneOrMore oneOrMore){
		if(compositorRelevance.peek() == RELEVANT){
			cardinalityRelevance.push(RELEVANT);
		}else{
			cardinalityRelevance.push(IRELEVANT);
		}
	}
	
	void start(SZeroOrMore zeroOrMore){
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
	void end(SMixed mixed){
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
	
	
	void end(SOneOrMore oneOrMore){
		cardinalityRelevance.pop();
	}
	void end(SZeroOrMore zeroOrMore){
		cardinalityRelevance.pop();
	}		
}