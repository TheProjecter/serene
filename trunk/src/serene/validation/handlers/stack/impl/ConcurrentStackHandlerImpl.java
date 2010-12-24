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

package serene.validation.handlers.stack.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.stack.CandidateStackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.stack.util.StackRedundanceHandler;
import serene.validation.handlers.stack.util.ConflictPathMaker;

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.conflict.ActiveModelConflictHandlerPool;
import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.ContextConflictsDescriptor;
import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.structure.StructureHandler;

import sereneWrite.MessageWriter;

public class ConcurrentStackHandlerImpl implements ConcurrentStackHandler{
	
	ArrayList<CandidateStackHandler> temporary;
	ArrayList<CandidateStackHandler> candidates;
	
	ArrayList<InternalConflictResolver> resolvers;
	ActiveModelStackHandlerPool pool;
	
	ContextConflictsDescriptor contextConflictsDescriptor; 	
	ActiveModelConflictHandlerPool conflictHandlerPool;
		 	
	ConflictPathMaker conflictPathMaker;
	StackRedundanceHandler stackRedundanceHandler;	
	
	ErrorCatcher errorCatcher;
	
	boolean reportExcessive;
	boolean reportMissing;
	boolean reportIllegal;
	boolean reportPreviousMisplaced;
	boolean reportCurrentMisplaced;
	boolean reportCompositorContentMissing;

	ValidationItemLocator validationItemLocator;	
	
	MessageWriter debugWriter;
	
	ConcurrentStackHandlerImpl(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		candidates = new ArrayList<CandidateStackHandler>();
		temporary = new ArrayList<CandidateStackHandler>();
		
		resolvers = new ArrayList<InternalConflictResolver>();
		
		contextConflictsDescriptor = new ContextConflictsDescriptor(debugWriter);
		
		stackRedundanceHandler = new StackRedundanceHandler(debugWriter);
		conflictPathMaker = new ConflictPathMaker(debugWriter);
	}
	
	public void recycle(){		
		contextConflictsDescriptor.clear();
		
		for(CandidateStackHandler candidate : candidates){
			if(candidate != null) candidate.recycle();
		}				
		candidates.clear();
		temporary.clear();		
		resolvers.clear();
		pool.recycle(this);		
	}
	
	void init(ValidationItemLocator validationItemLocator, ActiveModelConflictHandlerPool conflictHandlerPool, ActiveModelStackHandlerPool pool){
		this.pool = pool;
		this.validationItemLocator = validationItemLocator;
		this.conflictHandlerPool = conflictHandlerPool;
	}
	
	// first init
	void init(StackHandler originalHandler, ErrorCatcher errorCatcher){		
		StructureHandler originalTopHandler = originalHandler.getTopHandler();
		StructureHandler originalCurrentHandler = originalHandler.getCurrentHandler();		
		Rule originalCurrentRule = originalCurrentHandler.getRule();
		CandidateStackHandler candidate = pool.getCandidateStackHandler(originalTopHandler,
																		originalCurrentRule,
																		this,
																		contextConflictsDescriptor,
																		errorCatcher);
		candidates.add(candidate);
		this.errorCatcher = errorCatcher;
	}
		
	
	public boolean handlesConflict(){
		return true;
	}
		
	public void reportedExcessive(){
		reportExcessive = false;
	}	
	
	public void reportedMissing(){
		reportMissing = false;
	}	
		
	public void reportedIllegal(){
		reportIllegal = false;
	}
	
	public void reportedPreviousMisplaced(){
		reportPreviousMisplaced = false;
	}

	public void reportedCurrentMisplaced(){
		reportCurrentMisplaced = false;
	}
		
	public void reportedCompositorContentMissing(){
		reportCompositorContentMissing = false;
	}	
	
			
	public StructureHandler getTopHandler(){
		throw new IllegalStateException();
	}
	
	public StructureHandler getCurrentHandler(){		
		throw new IllegalStateException();
	}
		
		
	public StackRedundanceHandler getStackRedundanceHandler(){
		return stackRedundanceHandler;
	}
		
	public ErrorCatcher getErrorCatcher(){
		return errorCatcher;
	}
		
	
	public void shift(AElement element){
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
				
		for(int i = 0; i < candidates.size(); i++){
			CandidateStackHandler candidate = candidates.get(i);
			if(candidate != null){
				candidate.shift(element, 
								reportExcessive,  
								reportPreviousMisplaced, 
								reportCurrentMisplaced, 
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!candidate.hasActiveConflicts()){
					if(candidates.size() > 1){						
						candidate.recycle();
						candidates.remove(i);					
						i--;
					}
				}else if(stackRedundanceHandler.isRedundant(candidate)){
					candidate.recycle();
					candidates.remove(i);					
					i--;
				}
			}
		}
	}
	
	public void shiftAllElements(List<AElement> elementDefinitions){
			
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		InternalConflictResolver resolver = conflictHandlerPool.getInternalConflictResolver();		
		resolvers.add(resolver);		
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(elementDefinitions);
		int lastQualifiedIndex = elementDefinitions.size()-1;
		
		for(int i = 0; i < lastQualifiedIndex; i++){				
			AElement element = elementDefinitions.get(i);			
			resolver.addCandidate(element);			
			
			contextConflictsDescriptor.record(element, innerPathes[i]);
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(element, 
									innerPathes[i],
									resolver, 
									i,
									reportExcessive,  
									reportPreviousMisplaced, 
									reportCurrentMisplaced, 
									reportMissing, 
									reportIllegal, 
									reportCompositorContentMissing);
					if(!candidate.hasActiveConflicts()){
						/*if(candidates.size() > 0){
							candidate.recycle();
						}else{
							candidates.add(candidate);	
						}*/
						candidate.recycle();
					}else if(stackRedundanceHandler.isRedundant(candidate)){
						candidate.recycle();
					}else{
						candidates.add(candidate);	
					}
				}
			}			
		}
		
		AElement element = elementDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(element);
		
		contextConflictsDescriptor.record(element, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(element,
								innerPathes[lastQualifiedIndex],
								resolver, 
								lastQualifiedIndex,
								reportExcessive,  
								reportPreviousMisplaced, 
								reportCurrentMisplaced, 
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						temp.recycle();
					}else{
						candidates.add(temp);	
					}
				}else if(stackRedundanceHandler.isRedundant(temp)){
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}			
		temporary.clear();
	}
	
	public void shiftAllElements(List<AElement> elementDefinitions, Queue targetQueue, int targetEntry, Map<AElement, Queue> candidateQueues){
			
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		InternalConflictResolver resolver = conflictHandlerPool.getBoundInternalConflictResolver(targetQueue, targetEntry, candidateQueues);		
		resolvers.add(resolver);		
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(elementDefinitions);
		int lastQualifiedIndex = elementDefinitions.size()-1;
		
		for(int i = 0; i < lastQualifiedIndex; i++){				
			AElement element = elementDefinitions.get(i);			
			resolver.addCandidate(element);			
			
			contextConflictsDescriptor.record(element, innerPathes[i]);
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(element, 
									innerPathes[i],
									resolver, 
									i,
									reportExcessive,  
									reportPreviousMisplaced, 
									reportCurrentMisplaced, 
									reportMissing, 
									reportIllegal, 
									reportCompositorContentMissing);
					if(!candidate.hasActiveConflicts()){
						/*if(candidates.size() > 0){
							candidate.recycle();
						}else{
							candidates.add(candidate);	
						}*/
						candidate.recycle();
					}else if(stackRedundanceHandler.isRedundant(candidate)){
						candidate.recycle();
					}else{
						candidates.add(candidate);	
					}
				}
			}			
		}
		
		AElement element = elementDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(element);
		
		contextConflictsDescriptor.record(element, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(element,
								innerPathes[lastQualifiedIndex],
								resolver, 
								lastQualifiedIndex,
								reportExcessive,  
								reportPreviousMisplaced, 
								reportCurrentMisplaced, 
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						temp.recycle();
					}else{
						candidates.add(temp);	
					}
				}else if(stackRedundanceHandler.isRedundant(temp)){
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}			
		temporary.clear();
	}
	
	public void shiftAllElements(List<AElement> elementDefinitions, ExternalConflictHandler conflictHandler){
		
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		InternalConflictResolver resolver = conflictHandlerPool.getInternalConflictResolver();
		resolvers.add(resolver);
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(elementDefinitions);
		int lastQualifiedIndex = conflictHandler.getPreviousQualified(elementDefinitions.size());
		//int shifts = 0;
		//int redundant = 0;
		//int inactivated = 0;
		for(int i = 0; i < lastQualifiedIndex; i++){
			if(!conflictHandler.isDisqualified(i)){
				AElement element = elementDefinitions.get(i);
				
				resolver.addCandidate(element);
				
				contextConflictsDescriptor.record(element, innerPathes[i]);
				
				for(int j = 0; j < temporary.size(); j++){
					//shifts++;
					//System.out.println(shifts+"  "+element+" "+resolver);
					CandidateStackHandler temp = temporary.get(j);
					if(temp != null){
						CandidateStackHandler candidate = temp.getCopy();
						candidate.shift(element, 
										innerPathes[i],
										resolver, 
										i,
										reportExcessive,  
										reportPreviousMisplaced, 
										reportCurrentMisplaced, 
										reportMissing, 
										reportIllegal, 
										reportCompositorContentMissing);
						if(!candidate.hasActiveConflicts()){							
							/*if(candidates.size() > 0){
								//inactivated++;
								//System.out.println("inactive "+candidate);
								candidate.recycle();
							}else{
								candidates.add(candidate);	
							}*/
							candidate.recycle();
						}else if(stackRedundanceHandler.isRedundant(candidate)){
							//redundant++;
							//System.out.println("redundant");
							candidate.recycle();
						}else{
							candidates.add(candidate);
						}
					}
				}
			}
		}
		
		AElement element = elementDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(element);
		
		contextConflictsDescriptor.record(element, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){			
			//shifts++;
			//System.out.println(shifts+"  "+element+" "+resolver);
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(element,
								innerPathes[lastQualifiedIndex],
								resolver,
								lastQualifiedIndex,
								reportExcessive,  
								reportPreviousMisplaced, 
								reportCurrentMisplaced, 
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						//inactivated++;
						temp.recycle();
					}else{
						candidates.add(temp);	
					}					
				}else if(stackRedundanceHandler.isRedundant(temp)){
					//redundant++;
					//System.out.println("redundant");
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}				
		temporary.clear();
	}
	public void shiftAllElements(List<AElement> elementDefinitions, ExternalConflictHandler conflictHandler, Queue targetQueue, int targetEntry, Map<AElement, Queue> candidateQueues){
		
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		InternalConflictResolver resolver = conflictHandlerPool.getBoundInternalConflictResolver(targetQueue, targetEntry, candidateQueues);
		resolvers.add(resolver);
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(elementDefinitions);
		int lastQualifiedIndex = conflictHandler.getPreviousQualified(elementDefinitions.size());
		
		for(int i = 0; i < lastQualifiedIndex; i++){
			if(!conflictHandler.isDisqualified(i)){
				AElement element = elementDefinitions.get(i);
				
				resolver.addCandidate(element);
				
				contextConflictsDescriptor.record(element, innerPathes[i]);
				
				for(int j = 0; j < temporary.size(); j++){
					CandidateStackHandler temp = temporary.get(j);
					if(temp != null){
						CandidateStackHandler candidate = temp.getCopy();
						candidate.shift(element, 
										innerPathes[i],
										resolver, 
										i,
										reportExcessive,  
										reportPreviousMisplaced, 
										reportCurrentMisplaced, 
										reportMissing, 
										reportIllegal, 
										reportCompositorContentMissing);
						if(!candidate.hasActiveConflicts()){							
							/*if(candidates.size() > 0){
								candidate.recycle();
							}else{
								candidates.add(candidate);	
							}*/
							candidate.recycle();
						}else if(stackRedundanceHandler.isRedundant(candidate)){
							candidate.recycle();
						}else{
							candidates.add(candidate);
						}
					}
				}
			}
		}
		
		
		AElement element = elementDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(element);
		
		contextConflictsDescriptor.record(element, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(element,
								innerPathes[lastQualifiedIndex],
								resolver,
								lastQualifiedIndex,
								reportExcessive,  
								reportPreviousMisplaced, 
								reportCurrentMisplaced, 
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						temp.recycle();
					}else{
						candidates.add(temp);	
					}
				}else if(stackRedundanceHandler.isRedundant(temp)){
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}				
		temporary.clear();
	}
	
		
	
	public void shift(AAttribute attribute){
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		for(int i = 0; i < candidates.size(); i++){
			CandidateStackHandler candidate = candidates.get(i);
			if(candidate != null){
				candidate.shift(attribute, 
								reportExcessive, 
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!candidate.hasActiveConflicts()){
					if(candidates.size() > 1){
						candidate.recycle();
						candidates.remove(i);					
						i--;
					}
				}else if(stackRedundanceHandler.isRedundant(candidate)){
					candidate.recycle();
					candidates.remove(i);					
					i--;
				}
			}
		}
	}
			
	public void shiftAllAttributes(List<AAttribute> attributeDefinitions){
		
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
		
		InternalConflictResolver resolver = conflictHandlerPool.getInternalConflictResolver();
		resolvers.add(resolver);
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(attributeDefinitions);
		int lastQualifiedIndex = attributeDefinitions.size()-1;
		for(int i = 0; i < lastQualifiedIndex; i++){				
			AAttribute attribute = attributeDefinitions.get(i);
			
			resolver.addCandidate(attribute);
						
			contextConflictsDescriptor.record(attribute, innerPathes[i]);
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(attribute, 
									innerPathes[i],
									resolver, 
									i,
									reportExcessive, 
									reportMissing, 
									reportIllegal, 
									reportCompositorContentMissing);
					if(!candidate.hasActiveConflicts()){
						/*if(candidates.size() > 0){
							candidate.recycle();
						}else{
							candidates.add(candidate);	
						}*/
						candidate.recycle();
					}else if(stackRedundanceHandler.isRedundant(candidate)){
						candidate.recycle();
					}else{
						candidates.add(candidate);
					}
				}
			}			
		}
		
		AAttribute attribute = attributeDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(attribute);
		
		contextConflictsDescriptor.record(attribute, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(attribute,
								innerPathes[lastQualifiedIndex],
								resolver,
								lastQualifiedIndex,
								reportExcessive,   
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						temp.recycle();
					}else{
						candidates.add(temp);	
					}
				}else if(stackRedundanceHandler.isRedundant(temp)){
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}
		temporary.clear();
	}
	
	public void shiftAllAttributes(List<AAttribute> attributeDefinitions, String value, Queue targetQueue, int targetEntry, Map<AAttribute, AttributeBinder> attributeBinders){
		
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
		
		InternalConflictResolver resolver = conflictHandlerPool.getBoundInternalConflictResolver(value, targetQueue, targetEntry, attributeBinders);
		resolvers.add(resolver);
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(attributeDefinitions);
		int lastQualifiedIndex = attributeDefinitions.size()-1;
		for(int i = 0; i < lastQualifiedIndex; i++){				
			AAttribute attribute = attributeDefinitions.get(i);
			
			resolver.addCandidate(attribute);
						
			contextConflictsDescriptor.record(attribute, innerPathes[i]);
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(attribute, 
									innerPathes[i],
									resolver, 
									i,
									reportExcessive, 
									reportMissing, 
									reportIllegal, 
									reportCompositorContentMissing);
					if(!candidate.hasActiveConflicts()){
						/*if(candidates.size() > 0){
							candidate.recycle();
						}else{
							candidates.add(candidate);	
						}*/
						candidate.recycle();
					}else if(stackRedundanceHandler.isRedundant(candidate)){
						candidate.recycle();
					}else{
						candidates.add(candidate);
					}
				}
			}			
		}
		
		AAttribute attribute = attributeDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(attribute);
		
		contextConflictsDescriptor.record(attribute, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(attribute,
								innerPathes[lastQualifiedIndex],
								resolver,
								lastQualifiedIndex,
								reportExcessive,   
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						temp.recycle();
					}else{
						candidates.add(temp);	
					}
				}else if(stackRedundanceHandler.isRedundant(temp)){
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}
		temporary.clear();
	}
	
	public void shiftAllAttributes(List<AAttribute> attributeDefinitions, ExternalConflictHandler conflictHandler){
		
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
		
		InternalConflictResolver resolver = conflictHandlerPool.getInternalConflictResolver();
		resolvers.add(resolver);
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(attributeDefinitions);
		int lastQualifiedIndex = conflictHandler.getPreviousQualified(attributeDefinitions.size());
		for(int i = 0; i < lastQualifiedIndex; i++){
			if(!conflictHandler.isDisqualified(i)){
				AAttribute attribute = attributeDefinitions.get(i);
				
				resolver.addCandidate(attribute);
				
				contextConflictsDescriptor.record(attribute, innerPathes[i]);
				
				for(int j = 0; j < temporary.size(); j++){
					CandidateStackHandler temp = temporary.get(j);
					if(temp != null){
						CandidateStackHandler candidate = temp.getCopy();
						candidate.shift(attribute, 
										innerPathes[i],
										resolver, 
										i,
										reportExcessive, 
										reportMissing, 
										reportIllegal, 
										reportCompositorContentMissing);
						if(!candidate.hasActiveConflicts()){
							/*if(candidates.size() > 0){
								candidate.recycle();
							}else{
								candidates.add(candidate);	
							}*/
							candidate.recycle();
						}else if(stackRedundanceHandler.isRedundant(candidate)){
							candidate.recycle();
						}else{
							candidates.add(candidate);
						}
					}
				}
			}
		}
		
		AAttribute attribute = attributeDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(attribute);
		
		contextConflictsDescriptor.record(attribute, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(attribute,
								innerPathes[lastQualifiedIndex],
								resolver, 
								lastQualifiedIndex,
								reportExcessive,   
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						temp.recycle();
					}else{
						candidates.add(temp);	
					}
				}else if(stackRedundanceHandler.isRedundant(temp)){
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}
		temporary.clear();
	}
	public void shiftAllAttributes(List<AAttribute> attributeDefinitions, ExternalConflictHandler conflictHandler, String value, Queue targetQueue, int targetEntry, Map<AAttribute, AttributeBinder> attributeBinders){
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
		
		InternalConflictResolver resolver = conflictHandlerPool.getBoundInternalConflictResolver(value, targetQueue, targetEntry, attributeBinders);
		resolvers.add(resolver);
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(attributeDefinitions);
		int lastQualifiedIndex = conflictHandler.getPreviousQualified(attributeDefinitions.size());
		for(int i = 0; i < lastQualifiedIndex; i++){
			if(!conflictHandler.isDisqualified(i)){
				AAttribute attribute = attributeDefinitions.get(i);
				
				resolver.addCandidate(attribute);
				
				contextConflictsDescriptor.record(attribute, innerPathes[i]);
				
				for(int j = 0; j < temporary.size(); j++){
					CandidateStackHandler temp = temporary.get(j);
					if(temp != null){
						CandidateStackHandler candidate = temp.getCopy();
						candidate.shift(attribute, 
										innerPathes[i],
										resolver, 
										i,
										reportExcessive, 
										reportMissing, 
										reportIllegal, 
										reportCompositorContentMissing);
						if(!candidate.hasActiveConflicts()){
							/*if(candidates.size() > 0){
								candidate.recycle();
							}else{
								candidates.add(candidate);	
							}*/
							candidate.recycle();
						}else if(stackRedundanceHandler.isRedundant(candidate)){
							candidate.recycle();
						}else{
							candidates.add(candidate);
						}
					}
				}
			}
		}
		
		AAttribute attribute = attributeDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(attribute);
		
		contextConflictsDescriptor.record(attribute, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(attribute,
								innerPathes[lastQualifiedIndex],
								resolver, 
								lastQualifiedIndex,
								reportExcessive,   
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						temp.recycle();
					}else{
						candidates.add(temp);	
					}
				}else if(stackRedundanceHandler.isRedundant(temp)){
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}
		temporary.clear();
	} 	
		 	
	public void shift(CharsActiveTypeItem chars){
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		for(int i = 0; i < candidates.size(); i++){
			CandidateStackHandler candidate = candidates.get(i);
			if(candidate != null){
				candidate.shift(chars, 
								reportExcessive,  
								reportPreviousMisplaced, 
								reportCurrentMisplaced, 
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!candidate.hasActiveConflicts()){
					if(candidates.size() > 1){
						candidate.recycle();
						candidates.remove(i);					
						i--;
					}
				}else if(stackRedundanceHandler.isRedundant(candidate)){
					candidate.recycle();
					candidates.remove(i);					
					i--;
				}
			}
		}
	}
	
	
	public void shiftAllCharsDefinitions(List<CharsActiveTypeItem> charsDefinitions){				
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		InternalConflictResolver resolver = conflictHandlerPool.getInternalConflictResolver();
		resolvers.add(resolver);
		
		Rule[][] innerPathes = conflictPathMaker.getInnerPathes(charsDefinitions);
		int lastQualifiedIndex = charsDefinitions.size()-1;
		for(int i = 0; i < lastQualifiedIndex; i++){				
			CharsActiveTypeItem chars = charsDefinitions.get(i);
		
			resolver.addCandidate(chars);
			
			contextConflictsDescriptor.record(chars, innerPathes[i]);
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(chars, 
									innerPathes[i],
									resolver,
									i,
									reportExcessive,  
									reportPreviousMisplaced, 
									reportCurrentMisplaced, 
									reportMissing, 
									reportIllegal, 
									reportCompositorContentMissing);
					if(!candidate.hasActiveConflicts()){
						/*if(candidates.size() > 0){
							candidate.recycle();
						}else{
							candidates.add(candidate);	
						}*/
						candidate.recycle();
					}else if(stackRedundanceHandler.isRedundant(candidate)){
						candidate.recycle();
					}else{
						candidates.add(candidate);
					}
				}
			}			
		}
		
		CharsActiveTypeItem chars = charsDefinitions.get(lastQualifiedIndex);
		
		resolver.addCandidate(chars);
		
		contextConflictsDescriptor.record(chars, innerPathes[lastQualifiedIndex]);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(chars,
								innerPathes[lastQualifiedIndex],
								resolver,
								lastQualifiedIndex,
								reportExcessive,  
								reportPreviousMisplaced, 
								reportCurrentMisplaced, 
								reportMissing, 
								reportIllegal, 
								reportCompositorContentMissing);
				if(!temp.hasActiveConflicts()){
					if(candidates.size() > 0){
						temp.recycle();
					}else{
						candidates.add(temp);	
					}
				}else if(stackRedundanceHandler.isRedundant(temp)){
					temp.recycle();
				}else{
					candidates.add(temp);
				}
			}
		}			
		temporary.clear();
	}
	
	
	
	public void reduce(StructureHandler handler){
		throw new IllegalStateException();
	}
	
	public void reshift(StructureHandler handler, APattern child){
		throw new IllegalStateException();
	}
	
	public void validatingReshift(StructureHandler handler, APattern child){
		throw new IllegalStateException();
	}
	
	public void reset(StructureHandler handler){
		throw new IllegalStateException();
	}
	
	public void endSubtreeValidation(StructureHandler handler){		
		throw new IllegalStateException();
	}
		
	public void blockReduce(StructureHandler handler, int count, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}
	
	public void limitReduce(StructureHandler handler, int MIN, int MAX, APattern pattern, String startQName, String startSystemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();
	}

	public void endValidation(){		
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
				
		ArrayList<CandidateStackHandler> errorless = new ArrayList<CandidateStackHandler>();
		for(int i = 0; i < candidates.size(); i++){
			CandidateStackHandler candidate = candidates.get(i);
			if(candidate != null)candidate.endValidation(reportMissing, 
													reportIllegal, 
													reportCompositorContentMissing);
			if(!candidate.hasDisqualifyingError()){
				errorless.add(candidate);
			}			
		}
		if(errorless.isEmpty()){
			for(int i = 0; i < candidates.size(); i++){
				CandidateStackHandler candidate = candidates.get(i);
				if(candidate != null)candidate.handleConflicts();
			}
		}else{
			for(int i = 0; i < errorless.size(); i++){
				CandidateStackHandler candidate = errorless.get(i);
				if(candidate != null)candidate.handleConflicts();
			}
		}
		for(InternalConflictResolver resolver : resolvers){		
			resolver.resolve(errorCatcher);			
		}
	}
	
	public int functionalEquivalenceCode(){
		int fec = 0;
		for(CandidateStackHandler candidate : candidates){
			if(candidate != null)fec+=candidate.functionalEquivalenceCode();
		}
		return fec;
	}
	
	public void setParentCurrentHandler(){
		throw new IllegalStateException();
	}
	
	public String toString(){
		return "ConcurrentStackHandlerImpl candidates "+candidates;
	}
}