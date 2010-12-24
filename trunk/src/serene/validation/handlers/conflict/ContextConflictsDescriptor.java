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

import java.util.HashSet;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.ActiveTypeItem;

import sereneWrite.MessageWriter;


public class ContextConflictsDescriptor implements InternalConflictDescriptor{
	
	HashSet<ActiveTypeItem> conflictActiveTypeItems;
	HashSet<Rule> conflictPathRules;
	
	MessageWriter debugWriter;
	
	public ContextConflictsDescriptor(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		conflictActiveTypeItems = new HashSet<ActiveTypeItem>();
		conflictPathRules = new HashSet<Rule>();
	}
		
	
	public void clear(){
		conflictActiveTypeItems.clear();
		conflictPathRules.clear();
	}
	
	public void record(ActiveTypeItem pattern, Rule[] path){
		conflictActiveTypeItems.add(pattern);
		for(int i = 0; i < path.length; i++){
			conflictPathRules.add(path[i]);
		}
	}
	
	
	public boolean isConflictActiveTypeItem(ActiveTypeItem item){
		return conflictActiveTypeItems.contains(item);
	}
	
	public boolean isConflictPathRule(Rule rule){
		return conflictPathRules.contains(rule);
	}
	
	public boolean isConflictRule(Rule rule){
		return conflictPathRules.contains(rule) || conflictActiveTypeItems.contains(rule);	
	}
	
	
	public String toString(){
		//return "ContextConflictsDescriptor "+hashCode();
		return "ContextConflictsDescriptor ";
	}
}