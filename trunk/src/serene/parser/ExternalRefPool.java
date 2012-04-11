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

public class ExternalRefPool extends RNGParseEndElementTaskPool{
	SAttribute href;
	
	ExternalRefTask[] task;
	int taskFree, taskMaxSize;
	
	public ExternalRefPool(SAttribute ns,
						SAttribute datatypeLibrary,
						SAttribute href,
                        SAttribute foreign, 
						MessageWriter debugWriter){
		super(ns, datatypeLibrary, foreign, debugWriter);
		this.href = href;
		taskFree = 0;
		taskMaxSize = 10;
		task = new ExternalRefTask[5];
	}
	
	public ExternalRefTask getTask(){
		if(taskFree == 0){			
			return new ExternalRefTask(ns, datatypeLibrary, href, foreign, this, debugWriter);			
		}
		else{				
			return task[--taskFree];
		}
	}
	
	public void recycle(ElementTask t){
		if(!(t instanceof ExternalRefTask)) throw new IllegalStateException();
		recycle((ExternalRefTask)t);
	}
	
	void recycle(ExternalRefTask t){		
		if(taskFree == taskMaxSize) return;			
		if(taskFree == task.length){
			ExternalRefTask[] increased = new ExternalRefTask[5+task.length];
			System.arraycopy(task, 0, increased, 0, taskFree);
			task = increased;
		}
		task[taskFree++] = t;
	}
}
