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

public class ParamPool extends RNGParseEndElementTaskPool{
	SAttribute name;
	
	ParamTask[] task;
	int taskFree, taskPoolSize;
	
	public ParamPool(SAttribute ns,
						SAttribute datatypeLibrary,
						SAttribute name,
						MessageWriter debugWriter){
		super(ns, datatypeLibrary, debugWriter);
		this.name = name;
		taskFree = 0;
		taskPoolSize = 32;
		task = new ParamTask[taskPoolSize];
	}
	
	public ParamTask getTask(){
		if(taskFree == 0){			
			return new ParamTask(ns, datatypeLibrary, name, this, debugWriter);			
		}
		else{				
			return task[--taskFree];
		}
	}
	
	public void recycle(ElementTask t){
		if(!(t instanceof ParamTask)) throw new IllegalStateException();
		recycle((ParamTask)t);
	}
	
	void recycle(ParamTask t){		
		if(taskFree == taskPoolSize){			 
			taskPoolSize+=3;
			ParamTask[] increased = new ParamTask[taskPoolSize];
			System.arraycopy(task, 0, increased, 0, taskFree);
			task = increased;
		}
		task[taskFree++] = t;
	}
}
