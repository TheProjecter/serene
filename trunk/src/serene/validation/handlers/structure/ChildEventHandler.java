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

package serene.validation.handlers.structure;

public interface ChildEventHandler{	
	
	
	final static int NO_CONTENT = 0;
	final static int OPEN_CONTENT = 1;
	final static int SATISFIED_CONTENT = 2;	
	final static int SATURATED_CONTENT = 3;
	final static int UNSATISFIED_SATURATED_CONTENT = 4;
	final static int SATISFIED_SATURATED_CONTENT = 5;
	final static int EXCESSIVE_CONTENT = 6;
	final static int UNSATISFIED_EXCESSIVE_CONTENT = 7;
	final static int SATISFIED_EXCESSIVE_CONTENT = 8;
	
	final static int LIMIT_REDUCE = 9;
	
	int getContentIndex();
		
	void childOpen();
	void requiredChildSatisfied();
	void optionalChildSatisfied();
	/*void childSatisfiedPlus();*/
	void childSaturated();	                                               
	void childExcessive();		
} 