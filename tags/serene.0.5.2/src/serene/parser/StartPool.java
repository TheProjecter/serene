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

public class StartPool extends RNGParseEndElementTaskPool{
	SAttribute combine;
	
	StartTask[] task;
	int taskFree, taskPoolSize;
	
	public StartPool(SAttribute ns,
						SAttribute datatypeLibrary,
						SAttribute combine,
                        SAttribute foreign, 
						MessageWriter debugWriter){
		super(ns, datatypeLibrary, foreign, debugWriter);
		this.combine = combine;
		taskFree = 0;
		taskPoolSize = 3;
		task = new StartTask[taskPoolSize];
	}
	
	public StartTask getTask(){
		if(taskFree == 0){			
			return new StartTask(ns, datatypeLibrary, combine, foreign, this, debugWriter);			
		}
		else{				
			return task[--taskFree];
		}
	}
	
	public void recycle(ElementTask t){
		if(!(t instanceof StartTask)) throw new IllegalStateException();
		recycle((StartTask)t);
	}
	
	void recycle(StartTask t){		
		if(taskFree == taskPoolSize){			 
			taskPoolSize+=3;
			StartTask[] increased = new StartTask[taskPoolSize];
			System.arraycopy(task, 0, increased, 0, taskFree);
			task = increased;
		}
		task[taskFree++] = t;
	}
}
