/*
Copyright 2011 Radu Cernuta 

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

import serene.bind.ElementTask;
import serene.bind.ElementTaskPool;

import serene.validation.schema.simplified.components.SAttribute;

import sereneWrite.MessageWriter;

public class ForeignElementTaskPool implements ElementTaskPool{
    ForeignElementTask[] task;
	int taskFree, taskMaxSize;
    
    SAttribute any;
    
	MessageWriter debugWriter;
	
	public ForeignElementTaskPool(SAttribute any, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
        this.any = any;
        taskFree = 0;
		taskMaxSize = 10;
		task = new ForeignElementTask[5];
	}
    
    public void recycle(ElementTask t){
		if(!(t instanceof ForeignElementTask)) throw new IllegalStateException();
		recycle((ForeignElementTask)t);
	}
    
    void recycle(ForeignElementTask t){		
		if(taskFree == taskMaxSize) return;			
		if(taskFree == task.length){
			ForeignElementTask[] increased = new ForeignElementTask[5+task.length];
			System.arraycopy(task, 0, increased, 0, taskFree);
			task = increased;
		}
		task[taskFree++] = t;
	}
    
	public ForeignElementTask getTask(){
        if(taskFree == 0){			
			return new ForeignElementTask(any, this, debugWriter);			
		}
		else{				
			return task[--taskFree];
		}
    }
}
