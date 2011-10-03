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


package serene.validation.handlers.conflict;

import java.util.BitSet;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.relaxng.datatype.Datatype;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.content.util.ValidationItemLocator;
import serene.validation.handlers.error.ErrorCatcher;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import sereneWrite.MessageWriter;


public abstract class BoundAttributeConflictResolver extends AttributeConflictResolver{
	Queue targetQueue;
	int targetEntry;	
	
	Map<AAttribute, AttributeBinder> attributeBinders;
	
	String namespaceURI;
    String localName;
    String value;
    
	public BoundAttributeConflictResolver(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	void init(String namespaceURI, 
            String localName,
            String qName,
            String value, 
			Queue queue, 
			int entry, 
			Map<AAttribute, AttributeBinder> attributeBinders){		
		super.init();			
		this.targetQueue = queue;
		this.targetEntry = entry;
		this.attributeBinders = attributeBinders;
		this.namespaceURI = namespaceURI;
        this.localName = localName;
        this.qName = qName;
        this.value = value;
	}

	void reset(){
	    super.reset();
		targetQueue = null;
		targetEntry = -1;
        
        attributeBinders = null;
		namespaceURI = null;
        localName = null;
        value = null;
	}
	
	public String toString(){
		return "BoundAttributeConflictResolver ";
	}
}
