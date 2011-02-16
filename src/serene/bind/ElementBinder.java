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

import serene.util.NameInfo;

import sereneWrite.MessageWriter;

public class ElementBinder{
	ElementTask startTask;
	ElementTask endTask;
	
	MessageWriter debugWriter;
	
	public ElementBinder(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
		
	public void setStartTask(ElementTask startTask){
		this.startTask = startTask;
	}
	
	public void setEndTask(ElementTask endTask){
		this.endTask = endTask;
	}
	
	public void resetTasks(){
		startTask = null;
		endTask = null;
	}
	
	public void bindName(Queue queue, int startRecord, String namespaceURI, String localName, String qName){
		queue.addNameInfo(startRecord, new NameInfo(namespaceURI, localName, qName, debugWriter));
	}
	
	public void bindLocation(Queue queue, int  record, String location){
		queue.addLocation(record, location);
	}
	
	public void bind(Queue queue, int endRecord){
		queue.addElementTasks(endRecord, startTask, endTask);
	}
}
