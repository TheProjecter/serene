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

public class ContextConflictsDescriptor implements InternalConflictDescriptor{
	
	HashSet<ActiveTypeItem> conflictActiveTypeItems;
	HashSet<Rule> conflictPathRules;
    HashSet<Rule> conflictPathTops;
	
	public ContextConflictsDescriptor(){
		conflictActiveTypeItems = new HashSet<ActiveTypeItem>();
		conflictPathRules = new HashSet<Rule>();
        conflictPathTops = new HashSet<Rule>();
	}
		
	
	public void clear(){
		conflictActiveTypeItems.clear();
		conflictPathRules.clear();
        conflictPathTops.clear();
	}
	
	public void record(ActiveTypeItem pattern, Rule[] path){
		conflictActiveTypeItems.add(pattern);        
		for(int i = 0; i < path.length-1; i++){
			conflictPathRules.add(path[i]);
		}
        conflictPathTops.add(path[path.length-1]);
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
	
    public boolean isConflictPathTop(Rule rule){
        return conflictPathTops.contains(rule);
    }
	
	public String toString(){
		return "ContextConflictsDescriptor "
		+"\nitems "+conflictActiveTypeItems
		+"\nrules "+conflictPathRules
        +"\ntops "+conflictPathTops;
	}
}