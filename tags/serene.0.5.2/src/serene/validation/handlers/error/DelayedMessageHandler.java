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

import java.util.ArrayList;

import serene.validation.schema.active.components.APattern;

import sereneWrite.MessageWriter;

public class DelayedMessageHandler{
		
	ArrayList<ContextMessageHandler> messageHandler;	
	
	MessageWriter debugWriter;
	
	public DelayedMessageHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;	
		messageHandler = new ArrayList<ContextMessageHandler>();
	}
	 
	public void add(ContextMessageHandler mh){
		// actually the possibility exists there are some warnings first and then 
		// the candidate gets disualified, this allows for several messageHandlers
		messageHandler.add(mh);
	}
	
	public String getMessage(APattern definition){
		//TODO add warnings handling
		
		String message = "Candidate definition <"+definition.getQName()+"> at "+definition.getLocation()+" first error: \n" +getMessage();		
		return message;
	}
	
	public String getMessage(){
		String message = "";
		for(ContextMessageHandler mh : messageHandler){
			message += mh.getLocatedErrorMessage("\t");
		}
		return message;
	}
	public String toString(){
		return "DelayedMessageHandler \n"+getMessage();
	}
}