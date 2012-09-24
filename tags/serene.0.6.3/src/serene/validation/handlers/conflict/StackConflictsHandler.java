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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SMultipleChildrenPattern;

import serene.validation.handlers.structure.RuleHandler;
import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.CardinalityHandler;

import serene.util.IntList;
import serene.util.ObjectIntHashMap;

public class StackConflictsHandler implements InternalConflictDescriptor{
			
	HashSet<SPattern> activeTypeItems;
	
	HashSet<RuleHandler> ruleHandlers;
	HashSet<SMultipleChildrenPattern> groups;
		
	HashMap<SRule, Set<InternalConflictResolver>> handledResolvers;	
	HashMap<InternalConflictResolver, IntList> handledIndexes;
	
	ArrayList<InternalConflictResolver> removable;
	// Maps a rule to the count of all the handlers and group that are involved 
	// with that rule and could result in disqualifying errors;
	ObjectIntHashMap ruleDisqualifiersCount; 

	public StackConflictsHandler(){		
		activeTypeItems = new HashSet<SPattern>();
		
		ruleHandlers = new HashSet<RuleHandler>();
		groups = new HashSet<SMultipleChildrenPattern>();
		
		handledResolvers = new HashMap<SRule, Set<InternalConflictResolver>>();
	
		handledIndexes = new HashMap<InternalConflictResolver, IntList>();

		ruleDisqualifiersCount = new ObjectIntHashMap();
		ruleDisqualifiersCount.setNullValue(0);
	}	
	
	public void init(StackConflictsHandler other){
		activeTypeItems.addAll(other.activeTypeItems);			
		ruleHandlers.addAll(other.ruleHandlers);
		groups.addAll(other.groups);
		
		Set<SRule> otherRules = other.handledResolvers.keySet();
		for(SRule rule : otherRules){
			Set<InternalConflictResolver> resolvers = other.handledResolvers.get(rule);
			if(resolvers != null){
				handledResolvers.put(rule, new HashSet<InternalConflictResolver>(resolvers));
			}else{
				handledResolvers.put(rule, null);
			}
		}

		Set<InternalConflictResolver> otherResolvers = other.handledIndexes.keySet();
		for(InternalConflictResolver resolver : otherResolvers){
			IntList indexes = other.handledIndexes.get(resolver); 
			handledIndexes.put(resolver, indexes.getCopy());
		}				
				
		ruleDisqualifiersCount = other.ruleDisqualifiersCount.getCopy();
	}
	
	public void replace(RuleHandler ruleHandler){
		//System.out.println(hashCode()+" REPLACE "+structureHandlers);
		RuleHandler original = ruleHandler.getOriginal();
		if(ruleHandlers.remove(original)){
			ruleHandlers.add(ruleHandler);
			ruleHandler.setStackConflictsHandler(this);
		}
		//System.out.println(hashCode()+" after replace "+structureHandlers);
	}
		
	public void clear(){
		activeTypeItems.clear();		
		ruleHandlers.clear();	
		groups.clear();
		handledResolvers.clear();	
		handledIndexes.clear();
		ruleDisqualifiersCount.clear();
	}
		
	boolean addRuleResolver(SRule rule, InternalConflictResolver resolver){
		if(handledResolvers.containsKey(rule)){
			Set<InternalConflictResolver> resolvers = handledResolvers.get(rule);
			if(resolvers == null){
				resolvers = new HashSet<InternalConflictResolver>();
			}
			boolean result = resolvers.add(resolver);//needed for reshifting
			handledResolvers.put(rule, resolvers);
			return result;
		}else{
			Set<InternalConflictResolver> resolvers = new HashSet<InternalConflictResolver>();
			resolvers.add(resolver);
			handledResolvers.put(rule, resolvers);
			return true;
		}
	}
	
	void addRuleHandler(SRule rule, RuleHandler rh){
		if(!ruleHandlers.contains(rh)){
			ruleHandlers.add(rh);
			ruleDisqualifiersCount.put(rule, (1+ruleDisqualifiersCount.get(rule)));
		}
	}
	
	//once from candidateStackHandler at shift	
	public void record(SPattern item, InternalConflictResolver resolver, int candidateIndex){
		//System.out.println(hashCode()+" RECORD ITEM "+" "+item);
		activeTypeItems.add(item);
		
		addRuleResolver(item, resolver);
		if(handledIndexes.containsKey(resolver)){
			handledIndexes.get(resolver).add(candidateIndex);
		}else{
			IntList indexes = new IntList();
			indexes.add(candidateIndex);
			handledIndexes.put(resolver, indexes);
		}		
		// System.out.println("record item "+item+" "+candidateIndex+" "+activeTypeItems);
	}
	
	// only the rules from the innerPath of the conflict are added here, no items
	// the activeTypeItems are always added as particleHandlers
	// from candidateStackHandler at activatePath()
	public boolean record(SRule rule, RuleHandler rh, InternalConflictResolver resolver){
		//System.out.println(hashCode()+" RECORD STR "+" "+sh);		
		addRuleHandler(rule, rh);
		return addRuleResolver(rule, resolver);
	}
	
	
	public boolean record(SRule rule, SMultipleChildrenPattern group, InternalConflictResolver resolver){
		//System.out.println(hashCode()+" RECORD STR "+" "+sh);
		if(groups.add(group)){
			ruleDisqualifiersCount.put(rule, (1+ruleDisqualifiersCount.get(rule)));
		}
		return addRuleResolver(rule, resolver);
	}
	
	public void record(SRule rule, RuleHandler rh){		
		addRuleHandler(rule, rh);
	}	
	
	public void close(RuleHandler ruleHandler){
		if(ruleHandlers.remove(ruleHandler)){
			SRule rule = ruleHandler.getRule();
			ruleDisqualifiersCount.put(rule, (ruleDisqualifiersCount.get(rule)-1));
			if(ruleDisqualifiersCount.get(rule) == 0){
				handledResolvers.put(rule, null);
			}
		}				
	}
	
	public void close(SMultipleChildrenPattern group){
		if(groups.remove(group)){
			SRule[] rules = group.getChildren();
			for(SRule rule : rules){
				ruleDisqualifiersCount.put(rule, (ruleDisqualifiersCount.get(rule)-1));
				if(ruleDisqualifiersCount.get(rule) == 0){
					handledResolvers.put(rule, null);
				}
			}
		}				
	}
	
	public boolean isConflictTypeItem(SPattern item){
		return activeTypeItems.contains(item);
	}
	
	public boolean isConflictPathRule(SRule rule){
		return !activeTypeItems.contains(rule)
				&& (handledResolvers.containsKey(rule));		
		// TODO here the rules that have been disqualified or inactivated might be missing
		// functionally not important, but might need a look
	}
	
	public boolean isConflictRule(SRule rule){
		//System.out.println(hashCode()+" IS CONFLICT RULE "+rule);
		//System.out.println("items "+activeTypeItems);
		//System.out.println("strH "+handledResolvers.keySet());
		return activeTypeItems.contains(rule) // needed because order is handled before shifting, so the item shifted might not be in the handledResolvers yet 
				|| handledResolvers.containsKey(rule);
		// TODO here the rules that have been disqualified or inactivated might be missing
		// functionally not important, but might need a look
		
	}
	
	/**
	* It means that all conflict definitions handled here were disqualified. 
	* It does not mean that handlers cannot any more generate disqualifying 
	* errors. In this case conflicts are still considered active untill the 
	* InternalConflictResolvers have been notified obout the qualified 
	* definitions.
	*/
	public boolean isInactive(){
		return handledIndexes.isEmpty();
	}
	
	public void disqualify(SRule definition){
		// remove value from structureHandlers and particleHandlers
		// for every active conflict resolver:
		// remove mappings from handledConflicts
		// remove mappings from handled indexes
		// System.out.println(hashCode()+" DISQUALIFY "+definition);			
		Set<InternalConflictResolver> resolvers = handledResolvers.put(definition, null);
		if(resolvers != null){
			for(InternalConflictResolver resolver : resolvers){
				handledIndexes.remove(resolver);
			}
		}
	}
	
	
	
	public void transferResolversFrom(StackConflictsHandler other){
		Set<SRule> rules = handledResolvers.keySet();
		for(SRule rule : rules){
			Set<InternalConflictResolver> otherResolvers = other.getHandledResolvers(rule);
			if(otherResolvers != null){
				Set<InternalConflictResolver> resolvers = handledResolvers.get(rule);
				if(resolvers != null){
					for(InternalConflictResolver resolver : otherResolvers){
						if(!resolvers.contains(resolver)){
							resolvers.add(resolver);			
						}
					}
				}else{
					resolvers = new HashSet<InternalConflictResolver>(otherResolvers);
					handledResolvers.put(rule, resolvers);
				}
			}
		}
		
			
		Set<InternalConflictResolver> otherResolvers = other.handledIndexes.keySet();
		for(InternalConflictResolver otherResolver : otherResolvers){
			IntList indexes = handledIndexes.get(otherResolver);
			IntList otherIndexes = other.handledIndexes.get(otherResolver);
			if(indexes != null){				
				for(int i = 0; i < otherIndexes.size(); i++){
					int otherIndex = otherIndexes.get(i); 
					if(!indexes.contains(otherIndex)){
						indexes.add(otherIndex);
					}
				}
			}else{
				handledIndexes.put(otherResolver, otherIndexes);
			}
		}	
	}
	
	private Set<InternalConflictResolver> getHandledResolvers(SRule rule){
		return handledResolvers.get(rule);
	}
	
	public void handleConflicts(){
		//System.out.println(hashCode()+" HANDLE CONFLICTS ");
		Set<InternalConflictResolver> resolvers = handledIndexes.keySet();
		for(InternalConflictResolver resolver : resolvers){			
			IntList candidateIndexes = handledIndexes.get(resolver);
			for(int i = 0; i < candidateIndexes.size(); i++){
				resolver.qualify(candidateIndexes.get(i));
			}			
		}
	}
	
	public String toString(){
		//return "StackConflictsHandler "+hashCode();
		return "StackConflictsHandler "/*+handledIndexes.toString()*/+" ACTIVE "+(!isInactive());
	}
}