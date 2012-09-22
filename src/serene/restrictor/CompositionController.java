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

import org.xml.sax.SAXException;

import serene.util.IntList;
import serene.util.IntStack;

import serene.validation.schema.simplified.SNameClass;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SGroup;
import serene.validation.schema.simplified.SChoicePattern;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.Reusable;

abstract class CompositionController implements Reusable{
	
	final int NO_COMPOSITOR = -1;
	final int END_COMPOSITOR = 2;
	
	/**
	* List with the functional ids of the significant compositors. They are added 
	* in preorder as the schema tree is walked at indexes corresponding to nulls 
	* in the nameClasses list as follows:
	* - at the start of the compositor the corresponding functional id
	* - at the end the END_COMPOSITOR
	*/
	IntList compositors;
	
	ArrayList<SPattern> compositorPatterns;
	
	/**
	* Stack of compositor ids created on the fly during overlap control that
	* represents the common compositor path of any two nameClasses that might 
	* need to be tested for overlap. The peek of the stack represents the common
	* compositor and determines whether or not it is necessary to test.
	*/
	IntStack compositorPath;	
	Stack<SPattern> compositorPatternPath;
		
	ControllerPool pool;
	
	ErrorDispatcher errorDispatcher;
	
	CompositionController(ControllerPool pool, ErrorDispatcher errorDispatcher){
		this.errorDispatcher = errorDispatcher;
		this.pool = pool;
		
		compositors = new IntList();
		compositorPatterns = new ArrayList<SPattern>();
		compositorPath = new IntStack();
		compositorPatternPath = new Stack<SPattern>();
	}
	
	abstract void start(SInterleave interleave);
	abstract void start(SGroup group);
	abstract void start(SChoicePattern choice);
		
	abstract void startMultipleCardinality();
	
	
	abstract void end(SInterleave interleave);
	abstract void end(SGroup group);
	abstract void end(SChoicePattern choice);
	
	abstract void endMultipleCardinality();
	
	abstract void control() throws SAXException;
}