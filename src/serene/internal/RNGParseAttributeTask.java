/*
Copyright 2012 Radu Cernuta 

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

package serene.internal;

import serene.bind.AttributeTask;
import serene.bind.AttributeTaskContext;

import sereneWrite.MessageWriter;

public abstract class RNGParseAttributeTask extends RNGParseTask implements AttributeTask{	
	AttributeTaskContext context;	
	RNGParseAttributeTask(MessageWriter debugWriter){
		super(debugWriter);
	}
		
	public boolean executeBeforeStartElementTask(){
	    return false;
	}
    public boolean executeAfterStartElementTask(){
        return false;
    }
    public boolean executeBeforeEndElementTask(){
        return true;
    }
    public boolean executeAfterEndElementTask(){
        return false;
    }
    public boolean needsDocumentInputData(){
        return true;
    }  
	public void setContext(AttributeTaskContext context){
		this.context = context;
	}	
}

