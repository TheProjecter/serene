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

package serene.validation.handlers.content.impl;

import org.xml.sax.SAXException;
import org.xml.sax.Locator;

import serene.bind.Queue;

import serene.validation.handlers.content.BoundElementHandler;
import serene.validation.handlers.content.ElementEventHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import sereneWrite.MessageWriter;

class BoundElementDefaultHandler extends ElementDefaultHandler implements BoundElementHandler{
    Queue queue;
    int queueStartEntry;
    BoundElementDefaultHandler(MessageWriter debugWriter){
        super(debugWriter);
    }
    
    void init(ElementEventHandler parent, Queue queue){
        super.init(parent);
        this.queue = queue;
        queueStartEntry = queue.newRecord();
    }
    
    public void recycle(){
        queue = null;
        queueStartEntry = -1;
        
		pool.recycle(this);
	}
	
	public ComparableEEH handleStartElement(String qName, String namespace, String name, boolean restrictToFileName) throws SAXException{
		BoundElementDefaultHandler next = pool.getBoundElementDefaultHandler(this, queue);		
		return next;
	}	
	
	public void handleEndElement(boolean restrictToFileName, Locator locator){		
		super.handleEndElement(restrictToFileName, locator);
		elementTasksBinding();
	}	
	
    public void qNameBinding(){}
	public void startLocationBinding(){}
	public void endLocationBinding(){}
	public void characterContentBinding(char[] chars){}
	public void elementTasksBinding(){
        int queueEndEntry = queue.newRecord();
		queue.addIndexCorrespondence(queueEndEntry, queueStartEntry);
    }
}	
