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

package serene.bind;

import sereneWrite.MessageWriter;

public class AttributeBinder{
	AttributeTask task;
	
	MessageWriter debugWriter;
	
	public AttributeBinder(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	public void setTask(AttributeTask task){
		this.task = task;
	}
	
	public void resetTask(){
		task = null;
	}
	
	public void bindQName(Queue queue, int  record, int attributeIndex, String qName){
		queue.addAttributeQName(record, attributeIndex, qName);
	}

	
	public void bind(Queue queue, int  record, int attributeIndex, String value){
		queue.addAttributeValue(record, attributeIndex, value);
		if(task!=null)queue.addAttributeTask(record, attributeIndex, task);
	}	
}