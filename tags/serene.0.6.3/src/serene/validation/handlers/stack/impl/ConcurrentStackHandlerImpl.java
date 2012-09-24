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
import java.util.BitSet;
import java.util.Arrays;

import org.xml.sax.SAXException;

import serene.bind.util.Queue;
import serene.bind.BindingModel;


import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.stack.CandidateStackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;
import serene.validation.handlers.stack.StackHandler;

import serene.validation.handlers.stack.util.StackRedundanceHandler;

import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.conflict.ValidatorConflictHandlerPool;
import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.ElementConflictResolver;
import serene.validation.handlers.conflict.AttributeConflictResolver;
import serene.validation.handlers.conflict.AmbiguousCharsConflictResolver;
import serene.validation.handlers.conflict.UnresolvedCharsConflictResolver;
import serene.validation.handlers.conflict.AmbiguousListTokenConflictResolver;
import serene.validation.handlers.conflict.UnresolvedListTokenConflictResolver;
import serene.validation.handlers.conflict.BoundElementConflictResolver;
import serene.validation.handlers.conflict.BoundAttributeConflictResolver;
import serene.validation.handlers.conflict.ContextConflictsDescriptor;
import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.InnerPatternHandler;

import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.CharsMatchPath;
import serene.validation.handlers.match.MatchPath;

public class ConcurrentStackHandlerImpl implements ConcurrentStackHandler{
	
	ArrayList<CandidateStackHandler> temporary;
	ArrayList<CandidateStackHandler> candidates;
	
	ArrayList<InternalConflictResolver> resolvers;
	ValidatorStackHandlerPool pool;
	
	ContextConflictsDescriptor contextConflictsDescriptor; 	
	ValidatorConflictHandlerPool conflictHandlerPool;
		 	
	/*ConflictPathMaker conflictPathMaker;*/
	StackRedundanceHandler stackRedundanceHandler;	
	
	ErrorCatcher errorCatcher;
	
	boolean reportExcessive;
	boolean reportMissing;
	boolean reportIllegal;
	boolean reportPreviousMisplaced;
	boolean reportCurrentMisplaced;
	boolean reportCompositorContentMissing;

	InputStackDescriptor inputStackDescriptor;	
	
	MatchPath currentPath;
	List<MatchPath> currentPathes;
	
	ConcurrentStackHandlerImpl(){		
		candidates = new ArrayList<CandidateStackHandler>();
		temporary = new ArrayList<CandidateStackHandler>();
		
		resolvers = new ArrayList<InternalConflictResolver>();
		
		contextConflictsDescriptor = new ContextConflictsDescriptor();
		
		stackRedundanceHandler = new StackRedundanceHandler();
		/*conflictPathMaker = new ConflictPathMaker();*/
		
		currentPathes = new ArrayList<MatchPath>();
	}
	
	public void recycle(){		
		contextConflictsDescriptor.clear();
		
		for(CandidateStackHandler candidate : candidates){
			if(candidate != null) candidate.recycle();
		}				
		candidates.clear();			
		temporary.clear();

        for(InternalConflictResolver resolver: resolvers){          
            resolver.recycle();
        }
        resolvers.clear();
        
        if(currentPath != null){
            currentPath.recycle();
            currentPath = null;
        }
        if(!currentPathes.isEmpty()){
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        
		pool.recycle(this);		
	}
	
	void init(InputStackDescriptor inputStackDescriptor, ValidatorConflictHandlerPool conflictHandlerPool, ValidatorStackHandlerPool pool){
		this.pool = pool;
		this.inputStackDescriptor = inputStackDescriptor;
		this.conflictHandlerPool = conflictHandlerPool;
	}
	
	// first init
	void init(StackHandler originalHandler, ErrorCatcher errorCatcher){		
		StructureHandler originalTopHandler = originalHandler.getTopHandler();
		StructureHandler originalCurrentHandler = originalHandler.getCurrentHandler();		
		SRule originalCurrentRule = originalCurrentHandler.getRule();
		MatchPath originalCurrentPath = originalHandler.getCurrentMatchPath();
		if(originalCurrentPath != null)currentPath = originalCurrentPath.getCopy();
		CandidateStackHandler candidate = pool.getCandidateStackHandler(originalTopHandler,
		                                                                currentPath,
																		originalCurrentRule,
																		this,
																		contextConflictsDescriptor,
																		errorCatcher);
		candidates.add(candidate);
		this.errorCatcher = errorCatcher;
	}
		
	public void setAsCurrentHandler(StructureHandler sh){
        throw new IllegalStateException();
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
	public MatchPath getCurrentMatchPath(){
	    throw new IllegalStateException();
	}	
		
	public StackRedundanceHandler getStackRedundanceHandler(){
		return stackRedundanceHandler;
	}
		
	public ErrorCatcher getErrorCatcher(){
		return errorCatcher;
	}
		
	
	public void shift(ElementMatchPath element){
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
		
		if(currentPath != null){
            currentPath.recycle();            
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPath = element;
	}
	
	public void shiftAllElements(List<ElementMatchPath> elementDefinitions, ConflictMessageReporter conflictMessageReporter){
			
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		ElementConflictResolver resolver = conflictHandlerPool.getUnresolvedElementConflictResolver(conflictMessageReporter);		
		resolvers.add(resolver);		
		
		SRule[][] innerPathes = getInnerPathes(elementDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(elementDefinitions.get(i).getElement(), innerPathes[i]);
	    }	
		int lastQualifiedIndex = elementDefinitions.size()-1;
		    
		for(int i = 0; i < lastQualifiedIndex; i++){
		    ElementMatchPath elementPath = elementDefinitions.get(i);
			SElement element = elementPath.getElement();			
			resolver.addCandidate(element);			
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(elementPath, 
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
		
		ElementMatchPath elementPath = elementDefinitions.get(lastQualifiedIndex);
		SElement element = elementPath.getElement();
		
		resolver.addCandidate(element);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(elementPath,
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
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(elementDefinitions);
	}
	
	public void shiftAllElements(List<ElementMatchPath> elementDefinitions, ConflictMessageReporter conflictMessageReporter, BindingModel bindingModel, Queue targetQueue, int reservationStartEntry, int reservationEndEntry, Map<SElement, Queue> candidateQueues){
			
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		BoundElementConflictResolver resolver = conflictHandlerPool.getBoundUnresolvedElementConflictResolver(conflictMessageReporter, bindingModel, targetQueue, reservationStartEntry, reservationEndEntry, candidateQueues);		
		resolvers.add(resolver);		
		
		SRule[][] innerPathes = getInnerPathes(elementDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(elementDefinitions.get(i).getElement(), innerPathes[i]);
	    }
		int lastQualifiedIndex = elementDefinitions.size()-1;
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    ElementMatchPath elementPath = elementDefinitions.get(i); 
			SElement element = elementPath.getElement();
			resolver.addCandidate(element);			
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(elementPath, 
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
		
		ElementMatchPath elementPath = elementDefinitions.get(lastQualifiedIndex);
		SElement element = elementPath.getElement();
		
		resolver.addCandidate(element);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(elementPath,
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
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(elementDefinitions);
	}
	
	public void shiftAllElements(List<ElementMatchPath> elementDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter){
		
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		ElementConflictResolver resolver = conflictHandlerPool.getAmbiguousElementConflictResolver(conflictMessageReporter);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(elementDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(elementDefinitions.get(i).getElement(), innerPathes[i]);
	    }
		int lastQualifiedIndex = conflictHandler.getPreviousQualified(elementDefinitions.size());
		//int shifts = 0;
		//int redundant = 0;
		//int inactivated = 0;
		for(int i = 0; i < lastQualifiedIndex; i++){
		    ElementMatchPath elementPath = elementDefinitions.get(i);
		    SElement element = elementPath.getElement();
		    
		    resolver.addCandidate(element);
			if(!conflictHandler.isDisqualified(i)){
				for(int j = 0; j < temporary.size(); j++){
					//shifts++;
					CandidateStackHandler temp = temporary.get(j);
					if(temp != null){
						CandidateStackHandler candidate = temp.getCopy();
						candidate.shift(elementPath, 
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
		
		ElementMatchPath elementPath = elementDefinitions.get(lastQualifiedIndex);
		SElement element = elementPath.getElement();
		
		resolver.addCandidate(element);
		
		for(int j = 0; j < temporary.size(); j++){			
			//shifts++;
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(elementPath,
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
		
		for(++lastQualifiedIndex; lastQualifiedIndex < elementDefinitions.size(); lastQualifiedIndex++){
		    resolver.addCandidate(elementDefinitions.get(lastQualifiedIndex).getElement());
		}
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(elementDefinitions);
	}
	public void shiftAllElements(List<ElementMatchPath> elementDefinitions, ExternalConflictHandler conflictHandler, ConflictMessageReporter conflictMessageReporter, BindingModel bindingModel, Queue targetQueue, int reservationStartEntry, int reservationEndEntry, Map<SElement, Queue> candidateQueues){
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		BoundElementConflictResolver resolver = conflictHandlerPool.getBoundAmbiguousElementConflictResolver(conflictMessageReporter, bindingModel, targetQueue, reservationStartEntry, reservationEndEntry, candidateQueues);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(elementDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(elementDefinitions.get(i).getElement(), innerPathes[i]);
	    }
		int lastQualifiedIndex = conflictHandler.getPreviousQualified(elementDefinitions.size());
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    ElementMatchPath elementPath = elementDefinitions.get(i);
		    SElement element = elementPath.getElement();
			resolver.addCandidate(element);
			if(!conflictHandler.isDisqualified(i)){
				for(int j = 0; j < temporary.size(); j++){
					CandidateStackHandler temp = temporary.get(j);
					if(temp != null){
						CandidateStackHandler candidate = temp.getCopy();
						candidate.shift(elementPath, 
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
		
		
		ElementMatchPath elementPath = elementDefinitions.get(lastQualifiedIndex);
		SElement element = elementPath.getElement(); 
		
		resolver.addCandidate(element);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(elementPath,
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
		
		for(++lastQualifiedIndex; lastQualifiedIndex < elementDefinitions.size(); lastQualifiedIndex++){
		    resolver.addCandidate(elementDefinitions.get(lastQualifiedIndex).getElement());
		}
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(elementDefinitions);
	}
	
		
	
	public void shift(AttributeMatchPath attribute){
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
		
		        
        if(currentPath != null){
            currentPath.recycle();  
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPath = attribute;
	}
			
	public void shiftAllAttributes(List<AttributeMatchPath> attributeDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){
		
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
		
		AttributeConflictResolver resolver = conflictHandlerPool.getUnresolvedAttributeConflictResolver(temporaryMessageStorage);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(attributeDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(attributeDefinitions.get(i).getAttribute(), innerPathes[i]);
	    }
		int lastQualifiedIndex = attributeDefinitions.size()-1;
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    AttributeMatchPath attributePath = attributeDefinitions.get(i);
			SAttribute attribute = attributePath.getAttribute();
			
			resolver.addCandidate(attribute);
						
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(attributePath, 
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
		
		AttributeMatchPath attributePath = attributeDefinitions.get(lastQualifiedIndex);
		SAttribute attribute = attributePath.getAttribute(); 
		
		resolver.addCandidate(attribute);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(attributePath,
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
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(attributeDefinitions);
	}
	
	public void shiftAllAttributes(List<AttributeMatchPath> attributeDefinitions, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue targetQueue, int targetEntry, BindingModel bindingModel){
		
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
		
		BoundAttributeConflictResolver resolver = conflictHandlerPool.getBoundUnresolvedAttributeConflictResolver(temporaryMessageStorage,
		                                                                             /*inputStackDescriptor.getNamespaceURI(),
                                                                                     inputStackDescriptor.getLocalName(),
                                                                                     inputStackDescriptor.getItemDescription(),*/
                                                                                     value, 
                                                                                     targetQueue, 
                                                                                     targetEntry, 
                                                                                     bindingModel);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(attributeDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(attributeDefinitions.get(i).getAttribute(), innerPathes[i]);
	    }
		int lastQualifiedIndex = attributeDefinitions.size()-1;
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    AttributeMatchPath attributePath = attributeDefinitions.get(i);
			SAttribute attribute = attributePath.getAttribute();
			
			resolver.addCandidate(attribute);
						
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(attributePath, 
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
		
		AttributeMatchPath attributePath = attributeDefinitions.get(lastQualifiedIndex);
		SAttribute attribute = attributePath.getAttribute();
		
		resolver.addCandidate(attribute);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(attributePath,
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
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(attributeDefinitions);
	}
	
	public void shiftAllAttributes(List<AttributeMatchPath> attributeDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
		
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
		
		AttributeConflictResolver resolver = conflictHandlerPool.getAmbiguousAttributeConflictResolver(disqualified, temporaryMessageStorage);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(attributeDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(attributeDefinitions.get(i).getAttribute(), innerPathes[i]);
	    }
		int lastQualifiedIndex = attributeDefinitions.size()-1;
		determineIndex:{
            for(; lastQualifiedIndex >=0; lastQualifiedIndex--){
                if(!disqualified.get(lastQualifiedIndex))break determineIndex;
            }
            lastQualifiedIndex = -1;
        }
		
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    AttributeMatchPath attributePath = attributeDefinitions.get(i);
		    SAttribute attribute = attributePath.getAttribute();
		    resolver.addCandidate(attribute);
			if(!disqualified.get(i)){
				for(int j = 0; j < temporary.size(); j++){
					CandidateStackHandler temp = temporary.get(j);
					if(temp != null){
						CandidateStackHandler candidate = temp.getCopy();
						candidate.shift(attributePath, 
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
		
		AttributeMatchPath attributePath = attributeDefinitions.get(lastQualifiedIndex);
		SAttribute attribute = attributePath.getAttribute();
		resolver.addCandidate(attribute);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(attributePath,
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
		
		for(++lastQualifiedIndex; lastQualifiedIndex < attributeDefinitions.size(); lastQualifiedIndex++){
		    resolver.addCandidate(attributeDefinitions.get(lastQualifiedIndex).getAttribute());
		}
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(attributeDefinitions);
	}
	
	public void shiftAllAttributes(List<AttributeMatchPath> attributeDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage, String value, Queue targetQueue, int targetEntry, BindingModel bindingModel){		
		reportExcessive = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
		
		BoundAttributeConflictResolver resolver = conflictHandlerPool.getBoundAmbiguousAttributeConflictResolver(disqualified,
		                                                                                    temporaryMessageStorage,
		                                                                                    /*inputStackDescriptor.getNamespaceURI(),
                                                                                            inputStackDescriptor.getLocalName(),
                                                                                            inputStackDescriptor.getItemDescription(),*/
                                                                                            value, 
                                                                                            targetQueue, 
                                                                                            targetEntry, 
                                                                                            bindingModel);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(attributeDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(attributeDefinitions.get(i).getAttribute(), innerPathes[i]);
	    }
		int lastQualifiedIndex = attributeDefinitions.size()-1;
		determineIndex:{
            for(; lastQualifiedIndex >=0; lastQualifiedIndex--){
                if(!disqualified.get(lastQualifiedIndex))break determineIndex;
            }
            lastQualifiedIndex = -1;
        }
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    AttributeMatchPath attributePath = attributeDefinitions.get(i);
		    SAttribute attribute = attributePath.getAttribute(); 
			resolver.addCandidate(attribute);
			if(!disqualified.get(i)){
				for(int j = 0; j < temporary.size(); j++){
					CandidateStackHandler temp = temporary.get(j);
					if(temp != null){
						CandidateStackHandler candidate = temp.getCopy();
						candidate.shift(attributePath, 
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
		
		AttributeMatchPath attributePath = attributeDefinitions.get(lastQualifiedIndex);
		SAttribute attribute = attributePath.getAttribute();
		
		resolver.addCandidate(attribute);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(attributePath,
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
		
		for(++lastQualifiedIndex; lastQualifiedIndex < attributeDefinitions.size(); lastQualifiedIndex++){
		    resolver.addCandidate(attributeDefinitions.get(lastQualifiedIndex).getAttribute());
		}
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(attributeDefinitions);
	} 	
		 	
	public void shift(CharsMatchPath chars){
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
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPath = chars;
	}
		
	public void shiftAllCharsDefinitions(List<? extends CharsMatchPath> charsDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){
	    reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		UnresolvedCharsConflictResolver resolver = conflictHandlerPool.getUnresolvedCharsConflictResolver(temporaryMessageStorage);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(charsDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(charsDefinitions.get(i).getChars(), innerPathes[i]);
	    }
		int lastQualifiedIndex = charsDefinitions.size()-1;
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    CharsMatchPath charsPath = charsDefinitions.get(i); 
			SPattern chars = charsPath.getChars();
		
			resolver.addCandidate(chars);
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					
					candidate.shift(charsPath, 
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
		
		CharsMatchPath charsPath = charsDefinitions.get(lastQualifiedIndex);
		SPattern chars = charsPath.getChars();
		
		resolver.addCandidate(chars);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(charsPath,
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

        if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(charsDefinitions);		
	}
	public void shiftAllCharsDefinitions(List<? extends CharsMatchPath> charsDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){
	    reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
				
		AmbiguousCharsConflictResolver resolver = conflictHandlerPool.getAmbiguousCharsConflictResolver(disqualified, temporaryMessageStorage);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(charsDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(charsDefinitions.get(i).getChars(), innerPathes[i]);
	    }
		int lastQualifiedIndex = charsDefinitions.size()-1;
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    CharsMatchPath charsPath = charsDefinitions.get(i); 
			SPattern chars = charsPath.getChars();
		
			resolver.addCandidate(chars);
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(charsPath, 
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
		
		CharsMatchPath charsPath = charsDefinitions.get(lastQualifiedIndex);
		SPattern chars = charsPath.getChars();
		
		resolver.addCandidate(chars);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(charsPath,
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
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(charsDefinitions);
	}
    
	
	public void shiftAllTokenDefinitions(List<? extends CharsMatchPath> charsDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){		
				
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
			
		UnresolvedListTokenConflictResolver resolver = conflictHandlerPool.getUnresolvedListTokenConflictResolver(temporaryMessageStorage);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(charsDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(charsDefinitions.get(i).getChars(), innerPathes[i]);
	    }
		int lastQualifiedIndex = charsDefinitions.size()-1;
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    CharsMatchPath charsPath = charsDefinitions.get(i);
			SPattern chars = charsPath.getChars();
		
			resolver.addCandidate(chars);
			
			for(int j = 0; j < temporary.size(); j++){
				CandidateStackHandler temp = temporary.get(j);
				if(temp != null){
					CandidateStackHandler candidate = temp.getCopy();
					candidate.shift(charsPath, 
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
		
		CharsMatchPath charsPath = charsDefinitions.get(lastQualifiedIndex);
		SPattern chars = charsPath.getChars();
		
		resolver.addCandidate(chars);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(charsPath,
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
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(charsDefinitions);
	}
	
	public void shiftAllTokenDefinitions(List<? extends CharsMatchPath> charsDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){		
				
		reportExcessive = true;
		reportPreviousMisplaced = true;
		reportCurrentMisplaced = true;
		reportMissing = true;
		reportIllegal = true;
		reportCompositorContentMissing = true;
		
		stackRedundanceHandler.clear();
		
		temporary.addAll(candidates);
		candidates.clear();
			
		AmbiguousListTokenConflictResolver resolver = conflictHandlerPool.getAmbiguousListTokenConflictResolver(disqualified, temporaryMessageStorage);
		resolvers.add(resolver);
		
		SRule[][] innerPathes = getInnerPathes(charsDefinitions);
		for(int i = 0; i < innerPathes.length; i++){
	        contextConflictsDescriptor.record(charsDefinitions.get(i).getChars(), innerPathes[i]);
	    }
		int lastQualifiedIndex = charsDefinitions.size()-1;
		determineIndex:{
            for(; lastQualifiedIndex >=0; lastQualifiedIndex--){
                if(!disqualified.get(lastQualifiedIndex))break determineIndex;
            }
            lastQualifiedIndex = -1;
        }
		
		for(int i = 0; i < lastQualifiedIndex; i++){
		    CharsMatchPath charsPath = charsDefinitions.get(i);
		    SPattern chars = charsPath.getChars(); 
            resolver.addCandidate(chars);
		    if(!disqualified.get(i)){
                for(int j = 0; j < temporary.size(); j++){
                    CandidateStackHandler temp = temporary.get(j);
                    if(temp != null){
                        CandidateStackHandler candidate = temp.getCopy();
                        candidate.shift(charsPath, 
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
				
		CharsMatchPath charsPath = charsDefinitions.get(lastQualifiedIndex);
		SPattern chars = charsPath.getChars();
		
		resolver.addCandidate(chars);
		
		for(int j = 0; j < temporary.size(); j++){
			CandidateStackHandler temp = temporary.get(j);
			if(temp != null){
				temp.shift(charsPath,
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
		
		
		for(++lastQualifiedIndex; lastQualifiedIndex < charsDefinitions.size(); lastQualifiedIndex++){
		    resolver.addCandidate(charsDefinitions.get(lastQualifiedIndex).getChars());
		}
		
		if(currentPath != null){
            currentPath.recycle();      
            currentPath = null;
        }else{
            for(MatchPath path : currentPathes){
                path.recycle();
            }
            currentPathes.clear();
        }
        currentPathes.addAll(charsDefinitions);
	}
	
	public void reduce(InnerPatternHandler handler){
		throw new IllegalStateException();
	}
	
	public void reshift(InnerPatternHandler handler, SPattern child){
		throw new IllegalStateException();
	}
	
	public void validatingReshift(InnerPatternHandler handler, SPattern child){
		throw new IllegalStateException();
	}
	
	public void reset(StructureHandler handler){
		throw new IllegalStateException();
	}
	
	public void endSubtreeValidation(StructureHandler handler){		
		throw new IllegalStateException();
	}
		
	public void blockReduce(StructureHandler handler, int count, SPattern pattern, int startInputRecordIndex){
		throw new IllegalStateException();
	}
	
	public void limitReduce(StructureHandler handler, int MIN, int MAX, SPattern pattern, int startInputRecordIndex){
		throw new IllegalStateException();
	}

	public void endValidation() throws SAXException{		
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
	
	// Returns a bidimensional array containing all the pathes from every rule 
	// (exclusively) to the first common ancestor (inclusively).
	SRule[][] getInnerPathes(List<? extends MatchPath> itemPathes){
		SRule[][] innerPathes = new SRule[itemPathes.size()][];
		
		int commonDistance = 0;
		// distance between the first common ancestor and the top of the path, 
		// practicly the count of common ancestors, is the same for all pathes
		// effectively topIndex - commonAncestorIndex
		first:{
		for(int j = 0; true; j++){
		    for(int i = 0; i < innerPathes.length-1; i++){
		        MatchPath path1 = itemPathes.get(i);
		        MatchPath path2 = itemPathes.get(i+1);
		        // at size() - 1 would be the topIndex and that is always common
		        if(path1.get(path1.size() -2 -j) != path2.get(path2.size() -2 -j)){
		            commonDistance = j;
		            // it should be the last one that was common to all and j here
		            // indicates the first difference
		            break first;
		        }
			}
		}
		}
		for(int i = 0; i < innerPathes.length; i++){
		    MatchPath path = itemPathes.get(i);
            innerPathes[i] = path.getArray(1, path.size()-commonDistance);
        }
        
        return innerPathes;
	}
	
	public String toString(){
		return "ConcurrentStackHandlerImpl candidates "+candidates;
	}
}