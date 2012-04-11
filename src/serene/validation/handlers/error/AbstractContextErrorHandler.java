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

package serene.validation.handlers.error;

import org.xml.sax.SAXException;
import org.xml.sax.Locator;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.Reusable;

import sereneWrite.MessageWriter;

public abstract class AbstractContextErrorHandler implements Reusable, ContextErrorHandler{
	
	ContextMessageHandler messageHandler;
	
	ErrorDispatcher errorDispatcher;	
	ValidatorErrorHandlerPool pool;
	
	ActiveInputDescriptor activeInputDescriptor;
	int id;
	MessageWriter debugWriter;
	
	AbstractContextErrorHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;				
	}
	
	void init(ValidatorErrorHandlerPool pool, ErrorDispatcher errorDispatcher, ActiveInputDescriptor activeInputDescriptor){
		this.errorDispatcher = errorDispatcher;//maybe it could be moved to DefaultErrorHandler only
		this.pool = pool;
		this.activeInputDescriptor = activeInputDescriptor;		
	}
	
	public int getId(){
		return id;
	}	
	
} 