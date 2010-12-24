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

package serene.validation.handlers.conflict;

import java.util.BitSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.error.ContextMessageHandler;
import serene.validation.handlers.error.DelayedMessageHandler;

import sereneWrite.MessageWriter;

public class ExternalConflictHandler extends ConflictHandler{	
	
	DelayedMessageHandler[] candidateMessages;
	int candidateMessagesSize;
	int candidateMessagesIndex;
	
	/*DelayedMessageHandler[] commonMessages;
	int commonMessagesSize;
	int commonMessagesIndex;*/
	
	public ExternalConflictHandler(MessageWriter debugWriter){
		super(debugWriter);
		
		candidateMessagesSize = 0;		
		candidateMessagesIndex = -1;
		candidateMessages = new DelayedMessageHandler[candidateMessagesSize];
		
		/*commonMessagesSize = 0;		
		commonMessagesIndex = -1;
		commonMessages = new DelayedMessageHandler[commonMessagesSize];*/
	}
	
	
	public void reset(){
		disqualified.clear();
		Arrays.fill(candidateMessages, null);
		candidateMessagesIndex = -1;		
	}
	
	public void clear(int candidateIndex){
		disqualified.clear();
		candidateMessagesIndex = -1;
	}			
	
	public void addCandidateMessage(ContextMessageHandler messageHandler, int candidateIndex){
		if(candidateIndex >= candidateMessagesSize){
			int oldSize = candidateMessagesSize;
			candidateMessagesSize = candidateIndex+1;
			DelayedMessageHandler[] increased = new DelayedMessageHandler[candidateMessagesSize];
			System.arraycopy(candidateMessages, 0, increased, 0, oldSize);
			candidateMessages = increased;
		}		
		if(candidateMessages[candidateIndex] == null) 
			candidateMessages[candidateIndex] = new DelayedMessageHandler(debugWriter);
		
		candidateMessages[candidateIndex].add(messageHandler);
	}
	
	public DelayedMessageHandler[] getMessages(){		
		return candidateMessages;
	}
	
	public String toString(){
		String delayedMessages = "";
		for(int i = 0; i < candidateMessages.length; i++){
			if(candidateMessages[i] != null)delayedMessages += "\n"+i+" "+candidateMessages[i].getMessage();
			else delayedMessages += "\n"+i+" null";
		}		
		//return "ExternalConflictHandler "+hashCode()+" "+disqualified.toString()+delayedMessages;
		return "ExternalConflictHandler "+disqualified.toString();
	}
}