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

public class ElementWithNameClassPool extends RNGParseEndElementTaskPool{
	
	ElementWithNameClassTask[] task;
	int taskFree, taskPoolSize;
		
	public ElementWithNameClassPool(SAttribute ns,
						SAttribute datatypeLibrary,
						MessageWriter debugWriter){
		super(ns, datatypeLibrary, debugWriter);
		
		taskFree = 0;
		taskPoolSize = 32;
		task = new ElementWithNameClassTask[taskPoolSize];
	}
	
	public ElementWithNameClassTask getTask(){
		if(taskFree == 0){			
			return new ElementWithNameClassTask(ns, datatypeLibrary, this, debugWriter);			
		}
		else{				
			return task[--taskFree];
		}
	}
	
	public void recycle(ElementTask t){
		if(!(t instanceof ElementWithNameClassTask)) throw new IllegalStateException();
		recycle((ElementWithNameClassTask)t);
	}
	
	void recycle(ElementWithNameClassTask t){		
		if(taskFree == taskPoolSize){			 
			taskPoolSize+=3;
			ElementWithNameClassTask[] increased = new ElementWithNameClassTask[taskPoolSize];
			System.arraycopy(task, 0, increased, 0, taskFree);
			task = increased;
		}
		task[taskFree++] = t;
	}
}
