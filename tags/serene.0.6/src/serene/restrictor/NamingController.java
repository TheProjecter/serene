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

import java.io.File;

import org.xml.sax.SAXException;

import serene.util.IntList;
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

import sereneWrite.MessageWriter;

abstract class NamingController extends CompositionController{
		
	final int ALLOW_OVERLAP_COMPOSITOR = 0;
	final int FORBID_OVERLAP_COMPOSITOR = 1;
	
	
	/**
	* List of all the name class instances that need to be compared in order to 
	* detect overlap. They are added to the list in preorder as the schema tree 
	* is walked. A null is added every time a significant compositor starts and 
	* ends.
	*/
	ArrayList<SNameClass> nameClasses;
	
	/**
	* List of all the patterns named by the name classes that need to be compared 
	* in order to detect overlap. They are added to the list in parallel to the  
	* name classes.
	*/
	ArrayList<SPattern> namedPatterns;
		
	OverlapController overlapController; 
	
    boolean restrictToFileName;
	
	NamingController(ControllerPool pool, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		super(pool, errorDispatcher, debugWriter);
		
		nameClasses = new ArrayList<SNameClass>();
		namedPatterns = new ArrayList<SPattern>();
	}
	
    public void setRestrictToFileName(boolean value){
        restrictToFileName = value;
    }
    
	void start(SOptional optional){
		throw new IllegalStateException();
	}
	void start(SOneOrMore oneOrMore){
		throw new IllegalStateException();
	}
	void start(SZeroOrMore zeroOrMore){
		throw new IllegalStateException();
	}
	
	void end(SInterleave interleave){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(END_COMPOSITOR);
		compositorPatterns.add(null);
	}
	void end(SMixed mixed){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(END_COMPOSITOR);
		compositorPatterns.add(null);
	}
	void end(SGroup group){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(END_COMPOSITOR);
		compositorPatterns.add(null);
	}
	
	void end(SChoicePattern choice){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(END_COMPOSITOR);
		compositorPatterns.add(null);
	}
		
	void add(SPattern np, SNameClass nc){
		namedPatterns.add(np);
		nameClasses.add(nc);
		compositors.add(NO_COMPOSITOR);
		compositorPatterns.add(null);
	}

	void end(SOptional optional){
		throw new IllegalStateException();
	}
	void end(SOneOrMore oneOrMore){
		throw new IllegalStateException();
	}
	void end(SZeroOrMore zeroOrMore){
		throw new IllegalStateException();
	}	

	
	void control() throws SAXException{
		overlapController = pool.getOverlapController();
		int otherPath = 0;		
		// compositors on other pathes are only counted and telescoped without 
		// effect on the compositorPath  
		int ncSize = nameClasses.size();
		for(int i = 0; i < ncSize; i++){
			SNameClass nc = nameClasses.get(i);
			if(nc != null){				
				setCompositorPath(i);
				for(int j = i+1; j < nameClasses.size(); j++){
					SNameClass otherNc = nameClasses.get(j);
					if(otherNc == null){
						int compositor = compositors.get(j);
						if( compositor == ALLOW_OVERLAP_COMPOSITOR
							|| compositor == FORBID_OVERLAP_COMPOSITOR){
							otherPath++;
						}else if(compositor == END_COMPOSITOR){
							if(otherPath == 0){
								compositorPath.pop();
								compositorPatternPath.pop();
							}
							else otherPath--;
						}
					}else{
						if(compositorPath.peek() == FORBID_OVERLAP_COMPOSITOR){
							if(overlapController.overlap(nc, otherNc))
								reportError(compositorPatternPath.peek(), i, j);
						//System.out.println(nc+"  "+otherNc);
						}
					}
				}
			}
		}
		overlapController.recycle();
	}

	private void setCompositorPath(int index){
		compositorPath.clear();
		for(int i = 0; i < index; i++){
			int compositor = compositors.get(i);
			SPattern compositorPattern = compositorPatterns.get(i);
			if( compositor == ALLOW_OVERLAP_COMPOSITOR
				|| compositor == FORBID_OVERLAP_COMPOSITOR){
				compositorPath.push(compositor);
				compositorPatternPath.push(compositorPattern);
			}else if(compositor == END_COMPOSITOR){
				compositorPath.pop();
				compositorPatternPath.pop();
			}
		}
	}	
	
	abstract void reportError(SPattern context, int i, int j) throws SAXException; 
}