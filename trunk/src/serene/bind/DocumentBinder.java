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

import serene.util.AttributeInfo;
import sereneWrite.MessageWriter;

public class DocumentBinder{
    ElementBinder elementBinder;
	AttributeBinder[] attributeBinders;
	
	MessageWriter debugWriter;
	
	public DocumentBinder(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	public void setElementBinder(ElementBinder elementBinder){
		this.elementBinder = elementBinder;
	}
	
	public void setAttributeBinders(AttributeBinder[] attributeBinders){
	    this.attributeBinders = attributeBinders;
	}
	
	public void resetBinders(){
		elementBinder = null;
		attributeBinders = null;
	}
	
	public void bindName(Queue queue, int startRecord, String namespaceURI, String localName, String qName){
		elementBinder.bindName(queue, startRecord, namespaceURI, localName, qName);
	}
	
	public void bindLocation(Queue queue, int  record, String location){
		elementBinder.bindLocation(queue, record, location);
	}
	
	public void bindElementTasks(Queue queue, int endRecord){
		elementBinder.bindTasks(queue, endRecord);
	}
	
	/*public void bindAttribute(Queue queue, int  record, int attributeIndex, String namespaceURI, String localName, String qName, int type, String value){
		queue.addAttributeInfo(record, attributeIndex, new AttributeInfo(namespaceURI, localName, qName, value, type, debugWriter));
	}*/
}
