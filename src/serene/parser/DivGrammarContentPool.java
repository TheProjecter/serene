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

package serene.parser;

import serene.validation.schema.simplified.components.SAttribute;

import serene.bind.ElementTask;

import sereneWrite.MessageWriter;

public class DivGrammarContentPool extends RNGParseEndElementTaskPool{
	DivGrammarContentTask[] task;
	int taskFree, taskPoolSize;
	
	public DivGrammarContentPool(SAttribute ns,
						SAttribute datatypeLibrary,
                        SAttribute foreign, 
						MessageWriter debugWriter){
		super(ns, datatypeLibrary, foreign, debugWriter);
		taskFree = 0;
		taskPoolSize = 3;
		task = new DivGrammarContentTask[taskPoolSize];
	}
	
	public DivGrammarContentTask getTask(){
		if(taskFree == 0){			
			return new DivGrammarContentTask(ns, datatypeLibrary, foreign, this, debugWriter);			
		}
		else{				
			return task[--taskFree];
		}
	}
	
	public void recycle(ElementTask t){
		if(!(t instanceof DivGrammarContentTask)) throw new IllegalStateException();
		recycle((DivGrammarContentTask)t);
	}
	
	void recycle(DivGrammarContentTask t){		
		if(taskFree == taskPoolSize){			 
			taskPoolSize+=3;
			DivGrammarContentTask[] increased = new DivGrammarContentTask[taskPoolSize];
			System.arraycopy(task, 0, increased, 0, taskFree);
			task = increased;
		}
		task[taskFree++] = t;
	}
}
