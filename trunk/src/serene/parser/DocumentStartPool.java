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

public class DocumentStartPool extends RNGParseEndElementTaskPool{	
	DocumentStartTask[] task;
	int taskFree, taskMaxSize;
	
	public DocumentStartPool(MessageWriter debugWriter){
		super(null, null, null, debugWriter);
		taskFree = 0;
		taskMaxSize = 10;
		task = new DocumentStartTask[5];
	}
	
	public DocumentStartTask getTask(){
		if(taskFree == 0){			
			return new DocumentStartTask(this, debugWriter);			
		}
		else{				
			return task[--taskFree];
		}
	}
	
	public void recycle(ElementTask t){
		if(!(t instanceof DocumentStartTask)) throw new IllegalStateException();
		recycle((DocumentStartTask)t);
	}
	
	void recycle(DocumentStartTask t){		
		if(taskFree == taskMaxSize) return;			
		if(taskFree == task.length){
			DocumentStartTask[] increased = new DocumentStartTask[5+task.length];
			System.arraycopy(task, 0, increased, 0, taskFree);
			task = increased;
		}
		task[taskFree++] = t;
	}
}
