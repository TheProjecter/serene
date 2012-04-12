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

import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.BitSet;

import org.xml.sax.Locator;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.validation.handlers.error.ConflictMessageReporter;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

import serene.bind.Queue;
import serene.bind.AttributeBinder;
import serene.bind.BindingModel;


import serene.Reusable;

import sereneWrite.MessageWriter;

public class ActiveModelConflictHandlerPool implements Reusable{
	ConflictHandlerPool pool;
	InputStackDescriptor inputStackDescriptor;
	ActiveInputDescriptor activeInputDescriptor;
	
	//int ambiguousElementConflictResolverCreated;
	int ambiguousElementConflictResolverMaxSize;
	int ambiguousElementConflictResolverFree;
	int ambiguousElementConflictResolverMinFree;
	AmbiguousElementConflictResolver[] ambiguousElementConflictResolver;
	
	//int unresolvedElementConflictResolverCreated;
	int unresolvedElementConflictResolverMaxSize;
	int unresolvedElementConflictResolverFree;
	int unresolvedElementConflictResolverMinFree;
	UnresolvedElementConflictResolver[] unresolvedElementConflictResolver;
	
	
	//int ambiguousAttributeConflictResolverCreated;
	int ambiguousAttributeConflictResolverMaxSize;
	int ambiguousAttributeConflictResolverFree;
	int ambiguousAttributeConflictResolverMinFree;
	AmbiguousAttributeConflictResolver[] ambiguousAttributeConflictResolver;
	
	//int unresolvedAttributeConflictResolverCreated;
	int unresolvedAttributeConflictResolverMaxSize;
	int unresolvedAttributeConflictResolverFree;
	int unresolvedAttributeConflictResolverMinFree;
	UnresolvedAttributeConflictResolver[] unresolvedAttributeConflictResolver;
	
	
	//int ambiguousCharsConflictResolverCreated;
	int ambiguousCharsConflictResolverMaxSize;
	int ambiguousCharsConflictResolverFree;
	int ambiguousCharsConflictResolverMinFree;
	AmbiguousCharsConflictResolver[] ambiguousCharsConflictResolver;
	
	//int unresolvedCharsConflictResolverCreated;
	int unresolvedCharsConflictResolverMaxSize;
	int unresolvedCharsConflictResolverFree;
	int unresolvedCharsConflictResolverMinFree;
	UnresolvedCharsConflictResolver[] unresolvedCharsConflictResolver;
    
	
    //int ambiguousListTokenConflictResolverCreated;
	int ambiguousListTokenConflictResolverMaxSize;
	int ambiguousListTokenConflictResolverFree;
	int ambiguousListTokenConflictResolverMinFree;
	AmbiguousListTokenConflictResolver[] ambiguousListTokenConflictResolver;
	
	//int unresolvedListTokenConflictResolverCreated;
	int unresolvedListTokenConflictResolverMaxSize;
	int unresolvedListTokenConflictResolverFree;
	int unresolvedListTokenConflictResolverMinFree;
	UnresolvedListTokenConflictResolver[] unresolvedListTokenConflictResolver;
	
	
	
	//int boundAmbiguousElementConflictResolverCreated;
	int boundAmbiguousElementConflictResolverMaxSize;
	int boundAmbiguousElementConflictResolverFree;
	int boundAmbiguousElementConflictResolverMinFree;
	BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver;
	
	//int boundUnresolvedElementConflictResolverCreated;
	int boundUnresolvedElementConflictResolverMaxSize;
	int boundUnresolvedElementConflictResolverFree;
	int boundUnresolvedElementConflictResolverMinFree;
	BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver;
	
	
	//int boundAmbiguousAttributeConflictResolverCreated;
	int boundAmbiguousAttributeConflictResolverMaxSize;
	int boundAmbiguousAttributeConflictResolverFree;
	int boundAmbiguousAttributeConflictResolverMinFree;
	BoundAmbiguousAttributeConflictResolver[] boundAmbiguousAttributeConflictResolver;
	
	
	//int boundUnresolvedAttributeConflictResolverCreated;
	int boundUnresolvedAttributeConflictResolverMaxSize;
	int boundUnresolvedAttributeConflictResolverFree;
	int boundUnresolvedAttributeConflictResolverMinFree;
	BoundUnresolvedAttributeConflictResolver[] boundUnresolvedAttributeConflictResolver;
	
	boolean full;
	
	MessageWriter debugWriter;	
	
	
	public ActiveModelConflictHandlerPool(ConflictHandlerPool pool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;	
		this.pool = pool;		
		
		ambiguousElementConflictResolverMaxSize = 10;
        unresolvedElementConflictResolverMaxSize = 10;
        ambiguousAttributeConflictResolverMaxSize = 10;
        unresolvedAttributeConflictResolverMaxSize = 10;
        ambiguousCharsConflictResolverMaxSize = 10;
        unresolvedCharsConflictResolverMaxSize = 10;
        ambiguousListTokenConflictResolverMaxSize = 10;
        unresolvedListTokenConflictResolverMaxSize = 10;
        boundAmbiguousElementConflictResolverMaxSize = 10;
        boundUnresolvedElementConflictResolverMaxSize = 10;
        boundAmbiguousAttributeConflictResolverMaxSize = 10;
        boundUnresolvedAttributeConflictResolverMaxSize = 10;
        
        full = false;
	}

	public void recycle(){
		if(full)releaseHandlers();
		pool.recycle(this);
	}	
	public void fill(ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor){
		this.inputStackDescriptor = inputStackDescriptor;
		this.activeInputDescriptor = activeInputDescriptor;
		if(pool != null){
		    pool.fill(this,
				ambiguousElementConflictResolver,
				unresolvedElementConflictResolver,
				ambiguousAttributeConflictResolver,
				unresolvedAttributeConflictResolver,
				ambiguousCharsConflictResolver,
				unresolvedCharsConflictResolver,
				ambiguousListTokenConflictResolver,
				unresolvedListTokenConflictResolver,
				boundAmbiguousElementConflictResolver,
				boundUnresolvedElementConflictResolver,
				boundAmbiguousAttributeConflictResolver,
				boundUnresolvedAttributeConflictResolver);
		}else{		    
            ambiguousElementConflictResolver = new AmbiguousElementConflictResolver[10];            
            unresolvedElementConflictResolver = new UnresolvedElementConflictResolver[10];            
            ambiguousAttributeConflictResolver = new AmbiguousAttributeConflictResolver[10];            
            unresolvedAttributeConflictResolver = new UnresolvedAttributeConflictResolver[10];
                        
            ambiguousCharsConflictResolver = new AmbiguousCharsConflictResolver[10];            
            unresolvedCharsConflictResolver = new UnresolvedCharsConflictResolver[10];
                                
            ambiguousListTokenConflictResolver = new AmbiguousListTokenConflictResolver[10];
            unresolvedListTokenConflictResolver = new UnresolvedListTokenConflictResolver[10];            
                        
            boundAmbiguousElementConflictResolver = new BoundAmbiguousElementConflictResolver[10];
            boundUnresolvedElementConflictResolver = new BoundUnresolvedElementConflictResolver[10];
            boundAmbiguousAttributeConflictResolver = new BoundAmbiguousAttributeConflictResolver[10];
            boundUnresolvedAttributeConflictResolver = new BoundUnresolvedAttributeConflictResolver[10];            
		}
		
		full = true;
	}
	
	void initFilled(int ambiguousElementConflictResolverFillCount,
				int unresolvedElementConflictResolverFillCount,
				int ambiguousAttributeConflictResolverFillCount,
				int unresolvedAttributeConflictResolverFillCount,
				int ambiguousCharsConflictResolverFillCount,
				int unresolvedCharsConflictResolverFillCount,
                int ambiguousListTokenConflictResolverFillCount,
				int unresolvedListTokenConflictResolverFillCount,
				int boundAmbiguousElementConflictResolverFillCount,
				int boundUnresolvedElementConflictResolverFillCount,
				int boundAmbiguousAttributeConflictResolverFillCount,
				int boundUnresolvedAttributeConflictResolverFillCount){
		ambiguousElementConflictResolverFree = ambiguousElementConflictResolverFillCount;
		ambiguousElementConflictResolverMinFree = ambiguousElementConflictResolverFree;
		for(int i = 0; i < ambiguousElementConflictResolverFree; i++){	
			ambiguousElementConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		unresolvedElementConflictResolverFree = unresolvedElementConflictResolverFillCount;
		unresolvedElementConflictResolverMinFree = unresolvedElementConflictResolverFree;
		for(int i = 0; i < unresolvedElementConflictResolverFree; i++){	
			unresolvedElementConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		ambiguousAttributeConflictResolverFree = ambiguousAttributeConflictResolverFillCount;
		ambiguousAttributeConflictResolverMinFree = ambiguousAttributeConflictResolverFree;
		for(int i = 0; i < ambiguousAttributeConflictResolverFree; i++){	
			ambiguousAttributeConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
				
		unresolvedAttributeConflictResolverFree = unresolvedAttributeConflictResolverFillCount;
		unresolvedAttributeConflictResolverMinFree = unresolvedAttributeConflictResolverFree;
		for(int i = 0; i < unresolvedAttributeConflictResolverFree; i++){	
			unresolvedAttributeConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		ambiguousCharsConflictResolverFree = ambiguousCharsConflictResolverFillCount;
		ambiguousCharsConflictResolverMinFree = ambiguousCharsConflictResolverFree;
		for(int i = 0; i < ambiguousCharsConflictResolverFree; i++){	
			ambiguousCharsConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		unresolvedCharsConflictResolverFree = unresolvedCharsConflictResolverFillCount;
		unresolvedCharsConflictResolverMinFree = unresolvedCharsConflictResolverFree;
		for(int i = 0; i < unresolvedCharsConflictResolverFree; i++){	
			unresolvedCharsConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
        
		ambiguousListTokenConflictResolverFree = ambiguousListTokenConflictResolverFillCount;
		ambiguousListTokenConflictResolverMinFree = ambiguousListTokenConflictResolverFree;
		for(int i = 0; i < ambiguousListTokenConflictResolverFree; i++){	
			ambiguousListTokenConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		unresolvedListTokenConflictResolverFree = unresolvedListTokenConflictResolverFillCount;
		unresolvedListTokenConflictResolverMinFree = unresolvedListTokenConflictResolverFree;
		for(int i = 0; i < unresolvedListTokenConflictResolverFree; i++){	
			unresolvedListTokenConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		
		boundAmbiguousElementConflictResolverFree = boundAmbiguousElementConflictResolverFillCount;
		boundAmbiguousElementConflictResolverMinFree = boundAmbiguousElementConflictResolverFree;
		for(int i = 0; i < boundAmbiguousElementConflictResolverFree; i++){	
			boundAmbiguousElementConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		boundUnresolvedElementConflictResolverFree = boundUnresolvedElementConflictResolverFillCount;
		boundUnresolvedElementConflictResolverMinFree = boundUnresolvedElementConflictResolverFree;
		for(int i = 0; i < boundUnresolvedElementConflictResolverFree; i++){	
			boundUnresolvedElementConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		boundAmbiguousAttributeConflictResolverFree = boundAmbiguousAttributeConflictResolverFillCount;
		boundAmbiguousAttributeConflictResolverMinFree = boundAmbiguousAttributeConflictResolverFree;
		for(int i = 0; i < boundAmbiguousAttributeConflictResolverFree; i++){	
			boundAmbiguousAttributeConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
		
		boundUnresolvedAttributeConflictResolverFree = boundUnresolvedAttributeConflictResolverFillCount;
		boundUnresolvedAttributeConflictResolverMinFree = boundUnresolvedAttributeConflictResolverFree;
		for(int i = 0; i < boundUnresolvedAttributeConflictResolverFree; i++){	
			boundUnresolvedAttributeConflictResolver[i].init(this, activeInputDescriptor, inputStackDescriptor);
		}
	}
	
	public void releaseHandlers(){
		        pool.recycle(ambiguousElementConflictResolverFree,
		        ambiguousElementConflictResolverFree - ambiguousElementConflictResolverFree,
				ambiguousElementConflictResolver,
				unresolvedElementConflictResolverFree,
				unresolvedElementConflictResolverFree - unresolvedElementConflictResolverFree,
				unresolvedElementConflictResolver,
				ambiguousAttributeConflictResolverFree,
				ambiguousAttributeConflictResolverFree - ambiguousAttributeConflictResolverFree,
				ambiguousAttributeConflictResolver,
				unresolvedAttributeConflictResolverFree,
				unresolvedAttributeConflictResolverFree - unresolvedAttributeConflictResolverFree,
				unresolvedAttributeConflictResolver,
				ambiguousCharsConflictResolverFree,
				ambiguousCharsConflictResolverFree - ambiguousCharsConflictResolverFree,
				ambiguousCharsConflictResolver,
				unresolvedCharsConflictResolverFree,
				unresolvedCharsConflictResolverFree - unresolvedCharsConflictResolverFree,
				unresolvedCharsConflictResolver,
                ambiguousListTokenConflictResolverFree,
                ambiguousListTokenConflictResolverFree - ambiguousListTokenConflictResolverFree,
				ambiguousListTokenConflictResolver,
				unresolvedListTokenConflictResolverFree,
				unresolvedListTokenConflictResolverFree - unresolvedListTokenConflictResolverFree,
				unresolvedListTokenConflictResolver,
				boundAmbiguousElementConflictResolverFree,
				boundAmbiguousElementConflictResolverFree - boundAmbiguousElementConflictResolverFree,
				boundAmbiguousElementConflictResolver,
				boundUnresolvedElementConflictResolverFree,
				boundUnresolvedElementConflictResolverFree - boundUnresolvedElementConflictResolverFree,
				boundUnresolvedElementConflictResolver,
				boundAmbiguousAttributeConflictResolverFree,
				boundAmbiguousAttributeConflictResolverFree - boundAmbiguousAttributeConflictResolverFree,
				boundAmbiguousAttributeConflictResolver,
				boundUnresolvedAttributeConflictResolverFree,
				boundUnresolvedAttributeConflictResolverFree - boundUnresolvedAttributeConflictResolverFree,
				boundUnresolvedAttributeConflictResolver);
				
		ambiguousElementConflictResolverFree = 0;
        unresolvedElementConflictResolverFree = 0;
        ambiguousAttributeConflictResolverFree = 0;
        unresolvedAttributeConflictResolverFree = 0;
        ambiguousCharsConflictResolverFree = 0;
        unresolvedCharsConflictResolverFree = 0;
        ambiguousListTokenConflictResolverFree = 0;
        unresolvedListTokenConflictResolverFree = 0;
        boundAmbiguousElementConflictResolverFree = 0;
        boundUnresolvedElementConflictResolverFree = 0;
        boundAmbiguousAttributeConflictResolverFree = 0;
        boundUnresolvedAttributeConflictResolverFree = 0;
		
		full = false;
	}
	
	public AmbiguousElementConflictResolver getAmbiguousElementConflictResolver(ConflictMessageReporter conflictMessageReporter){				
		if(ambiguousElementConflictResolverFree == 0){
			// ambiguousElementConflictResolverCreated++;
			AmbiguousElementConflictResolver icr = new AmbiguousElementConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(conflictMessageReporter);			
			return icr;			
		}else{
			AmbiguousElementConflictResolver icr = ambiguousElementConflictResolver[--ambiguousElementConflictResolverFree];
			icr.init(conflictMessageReporter);
			if(ambiguousElementConflictResolverFree < ambiguousElementConflictResolverMinFree) ambiguousElementConflictResolverMinFree = ambiguousElementConflictResolverFree;
			return icr;
		}		
	}
		
	public void recycle(AmbiguousElementConflictResolver icr){	
		if(ambiguousElementConflictResolverFree == ambiguousElementConflictResolver.length){
		    if(ambiguousElementConflictResolverFree == ambiguousElementConflictResolverMaxSize)return;
			AmbiguousElementConflictResolver[] increased = new AmbiguousElementConflictResolver[5+ambiguousElementConflictResolver.length];
			System.arraycopy(ambiguousElementConflictResolver, 0, increased, 0, ambiguousElementConflictResolverFree);
			ambiguousElementConflictResolver = increased;
		}
		ambiguousElementConflictResolver[ambiguousElementConflictResolverFree++] = icr;
	}
	
	public UnresolvedElementConflictResolver getUnresolvedElementConflictResolver(ConflictMessageReporter conflictMessageReporter){				
		if(unresolvedElementConflictResolverFree == 0){
			// unresolvedElementConflictResolverCreated++;
			UnresolvedElementConflictResolver icr = new UnresolvedElementConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(conflictMessageReporter);			
			return icr;			
		}else{
			UnresolvedElementConflictResolver icr = unresolvedElementConflictResolver[--unresolvedElementConflictResolverFree];
			icr.init(conflictMessageReporter);
			if(unresolvedElementConflictResolverFree < unresolvedElementConflictResolverMinFree) unresolvedElementConflictResolverMinFree = unresolvedElementConflictResolverFree;
			return icr;			
		}		
	}
		
	public void recycle(UnresolvedElementConflictResolver icr){	
		if(unresolvedElementConflictResolverFree == unresolvedElementConflictResolver.length){
		    if(unresolvedElementConflictResolverFree == unresolvedElementConflictResolverMaxSize)return;
			UnresolvedElementConflictResolver[] increased = new UnresolvedElementConflictResolver[5+unresolvedElementConflictResolver.length];
			System.arraycopy(unresolvedElementConflictResolver, 0, increased, 0, unresolvedElementConflictResolverFree);
			unresolvedElementConflictResolver = increased;
		}
		unresolvedElementConflictResolver[unresolvedElementConflictResolverFree++] = icr;
	}
	
	public AmbiguousAttributeConflictResolver getAmbiguousAttributeConflictResolver(BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){				
		if(ambiguousAttributeConflictResolverFree == 0){
			// ambiguousAttributeConflictResolverCreated++;
			AmbiguousAttributeConflictResolver icr = new AmbiguousAttributeConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(disqualified, temporaryMessageStorage);			
			return icr;			
		}else{
			AmbiguousAttributeConflictResolver icr = ambiguousAttributeConflictResolver[--ambiguousAttributeConflictResolverFree];
			icr.init(disqualified, temporaryMessageStorage);
			if(ambiguousAttributeConflictResolverFree < ambiguousAttributeConflictResolverMinFree) ambiguousAttributeConflictResolverMinFree = ambiguousAttributeConflictResolverFree;
			return icr;
		}		
	}
		
	public void recycle(AmbiguousAttributeConflictResolver icr){	
		if(ambiguousAttributeConflictResolverFree == ambiguousAttributeConflictResolver.length){
		    if(ambiguousAttributeConflictResolverFree == ambiguousAttributeConflictResolverMaxSize) return; 
			AmbiguousAttributeConflictResolver[] increased = new AmbiguousAttributeConflictResolver[5+ambiguousAttributeConflictResolver.length];
			System.arraycopy(ambiguousAttributeConflictResolver, 0, increased, 0, ambiguousAttributeConflictResolverFree);
			ambiguousAttributeConflictResolver = increased;
		}
		ambiguousAttributeConflictResolver[ambiguousAttributeConflictResolverFree++] = icr;
	}
	
	
	public UnresolvedAttributeConflictResolver getUnresolvedAttributeConflictResolver(TemporaryMessageStorage[] temporaryMessageStorage){				
		if(unresolvedAttributeConflictResolverFree == 0){
			// unresolvedAttributeConflictResolverCreated++;
			UnresolvedAttributeConflictResolver icr = new UnresolvedAttributeConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(temporaryMessageStorage);			
			return icr;			
		}else{
			UnresolvedAttributeConflictResolver icr = unresolvedAttributeConflictResolver[--unresolvedAttributeConflictResolverFree];
			icr.init(temporaryMessageStorage);
			if(unresolvedAttributeConflictResolverFree < unresolvedAttributeConflictResolverMinFree) unresolvedAttributeConflictResolverMinFree = unresolvedAttributeConflictResolverFree;
			return icr;
		}		
	}
		
	public void recycle(UnresolvedAttributeConflictResolver icr){	
		if(unresolvedAttributeConflictResolverFree == unresolvedAttributeConflictResolver.length){
		    if(unresolvedAttributeConflictResolverFree == unresolvedAttributeConflictResolverMaxSize) return;
			UnresolvedAttributeConflictResolver[] increased = new UnresolvedAttributeConflictResolver[5+unresolvedAttributeConflictResolver.length];
			System.arraycopy(unresolvedAttributeConflictResolver, 0, increased, 0, unresolvedAttributeConflictResolverFree);
			unresolvedAttributeConflictResolver = increased;
		}
		unresolvedAttributeConflictResolver[unresolvedAttributeConflictResolverFree++] = icr;
	}
	
	public AmbiguousCharsConflictResolver getAmbiguousCharsConflictResolver(BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){				
		if(ambiguousCharsConflictResolverFree == 0){
			// ambiguousCharsConflictResolverCreated++;
			AmbiguousCharsConflictResolver icr = new AmbiguousCharsConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(disqualified, temporaryMessageStorage);			
			return icr;			
		}else{
			AmbiguousCharsConflictResolver icr = ambiguousCharsConflictResolver[--ambiguousCharsConflictResolverFree];
			icr.init(disqualified, temporaryMessageStorage);
			if(ambiguousCharsConflictResolverFree < ambiguousCharsConflictResolverMinFree) ambiguousCharsConflictResolverMinFree = ambiguousCharsConflictResolverFree;
			return icr;
		}		
	}
		
	public void recycle(AmbiguousCharsConflictResolver icr){	
		if(ambiguousCharsConflictResolverFree == ambiguousCharsConflictResolver.length){
		    if(ambiguousCharsConflictResolverFree == ambiguousCharsConflictResolverMaxSize) return;
			AmbiguousCharsConflictResolver[] increased = new AmbiguousCharsConflictResolver[5+ambiguousCharsConflictResolver.length];
			System.arraycopy(ambiguousCharsConflictResolver, 0, increased, 0, ambiguousCharsConflictResolverFree);
			ambiguousCharsConflictResolver = increased;
		}
		ambiguousCharsConflictResolver[ambiguousCharsConflictResolverFree++] = icr;
	}
	
	public UnresolvedCharsConflictResolver getUnresolvedCharsConflictResolver(TemporaryMessageStorage[] temporaryMessageStorage){				
		if(unresolvedCharsConflictResolverFree == 0){
			// unresolvedCharsConflictResolverCreated++;
			UnresolvedCharsConflictResolver icr = new UnresolvedCharsConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(temporaryMessageStorage);			
			return icr;			
		}else{
			UnresolvedCharsConflictResolver icr = unresolvedCharsConflictResolver[--unresolvedCharsConflictResolverFree];
			icr.init(temporaryMessageStorage);
			if(unresolvedCharsConflictResolverFree < unresolvedCharsConflictResolverMinFree) unresolvedCharsConflictResolverMinFree = unresolvedCharsConflictResolverFree;
			return icr;
		}		
	}
		
	public void recycle(UnresolvedCharsConflictResolver icr){	
		if(unresolvedCharsConflictResolverFree == unresolvedCharsConflictResolver.length){
		    if(unresolvedCharsConflictResolverFree == unresolvedCharsConflictResolverMaxSize) return;
			UnresolvedCharsConflictResolver[] increased = new UnresolvedCharsConflictResolver[5+unresolvedCharsConflictResolver.length];
			System.arraycopy(unresolvedCharsConflictResolver, 0, increased, 0, unresolvedCharsConflictResolverFree);
			unresolvedCharsConflictResolver = increased;
		}
		unresolvedCharsConflictResolver[unresolvedCharsConflictResolverFree++] = icr;
	}
        
    public AmbiguousListTokenConflictResolver getAmbiguousListTokenConflictResolver(BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){				
		if(ambiguousListTokenConflictResolverFree == 0){
			// ambiguousListTokenConflictResolverCreated++;
			AmbiguousListTokenConflictResolver icr = new AmbiguousListTokenConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(disqualified, temporaryMessageStorage);			
			return icr;			
		}else{
			AmbiguousListTokenConflictResolver icr = ambiguousListTokenConflictResolver[--ambiguousListTokenConflictResolverFree];
			icr.init(disqualified, temporaryMessageStorage);
			if(unresolvedCharsConflictResolverFree < unresolvedCharsConflictResolverMinFree) unresolvedCharsConflictResolverMinFree = unresolvedCharsConflictResolverFree; 
			return icr;
		}		
	}
		
	public void recycle(AmbiguousListTokenConflictResolver icr){	
		if(ambiguousListTokenConflictResolverFree == ambiguousListTokenConflictResolver.length){
		    if(ambiguousListTokenConflictResolverFree == ambiguousListTokenConflictResolverMaxSize) return;
			AmbiguousListTokenConflictResolver[] increased = new AmbiguousListTokenConflictResolver[5+ambiguousListTokenConflictResolver.length];
			System.arraycopy(ambiguousListTokenConflictResolver, 0, increased, 0, ambiguousListTokenConflictResolverFree);
			ambiguousListTokenConflictResolver = increased;
		}
		ambiguousListTokenConflictResolver[ambiguousListTokenConflictResolverFree++] = icr;
	}
	
	public UnresolvedListTokenConflictResolver getUnresolvedListTokenConflictResolver(TemporaryMessageStorage[] temporaryMessageStorage){				
		if(unresolvedListTokenConflictResolverFree == 0){
			// unresolvedListTokenConflictResolverCreated++;
			UnresolvedListTokenConflictResolver icr = new UnresolvedListTokenConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(temporaryMessageStorage);
			return icr;			
		}else{
			UnresolvedListTokenConflictResolver icr = unresolvedListTokenConflictResolver[--unresolvedListTokenConflictResolverFree];
			icr.init(temporaryMessageStorage);
			if(unresolvedListTokenConflictResolverFree < unresolvedListTokenConflictResolverMinFree) unresolvedListTokenConflictResolverMinFree = unresolvedListTokenConflictResolverFree;
			return icr;
		}		
	}
		
	public void recycle(UnresolvedListTokenConflictResolver icr){	
		if(unresolvedListTokenConflictResolverFree == unresolvedListTokenConflictResolver.length){
		    if(unresolvedListTokenConflictResolverFree == unresolvedListTokenConflictResolverMaxSize) return;
			UnresolvedListTokenConflictResolver[] increased = new UnresolvedListTokenConflictResolver[5+unresolvedListTokenConflictResolver.length];
			System.arraycopy(unresolvedListTokenConflictResolver, 0, increased, 0, unresolvedListTokenConflictResolverFree);
			unresolvedListTokenConflictResolver = increased;
		}
		unresolvedListTokenConflictResolver[unresolvedListTokenConflictResolverFree++] = icr;
	}
	
	
	
	public BoundAmbiguousElementConflictResolver getBoundAmbiguousElementConflictResolver(ConflictMessageReporter conflictMessageReporter,
	                                                                    BindingModel bindingModel,
	                                                                    Queue targetQueue,
																		int targetEntry,
																		Map<AElement, Queue> candidateQueues){				
		if(boundAmbiguousElementConflictResolverFree == 0){
			// boundAmbiguousElementConflictResolverCreated++;
			BoundAmbiguousElementConflictResolver icr = new BoundAmbiguousElementConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(conflictMessageReporter,
			        bindingModel,
			        targetQueue,
					targetEntry,
					candidateQueues);			
			return icr;			
		}else{
			BoundAmbiguousElementConflictResolver icr = boundAmbiguousElementConflictResolver[--boundAmbiguousElementConflictResolverFree];
			icr.init(conflictMessageReporter,
			        bindingModel,
			        targetQueue,
					targetEntry,
					candidateQueues);
			if(boundAmbiguousElementConflictResolverFree < boundAmbiguousElementConflictResolverMinFree) boundAmbiguousElementConflictResolverMinFree = boundAmbiguousElementConflictResolverFree;
			return icr;
		}		
	}
			
	public void recycle(BoundAmbiguousElementConflictResolver icr){		
		if(boundAmbiguousElementConflictResolverFree == boundAmbiguousElementConflictResolver.length){
		    if(boundAmbiguousElementConflictResolverFree == boundAmbiguousElementConflictResolverMaxSize) return;
			BoundAmbiguousElementConflictResolver[] increased = new BoundAmbiguousElementConflictResolver[5+boundAmbiguousElementConflictResolver.length];
			System.arraycopy(boundAmbiguousElementConflictResolver, 0, increased, 0, boundAmbiguousElementConflictResolverFree);
			boundAmbiguousElementConflictResolver = increased;
		}
		boundAmbiguousElementConflictResolver[boundAmbiguousElementConflictResolverFree++] = icr;
	}
	
	public BoundUnresolvedElementConflictResolver getBoundUnresolvedElementConflictResolver(ConflictMessageReporter conflictMessageReporter,
	                                                                    BindingModel bindingModel,
	                                                                    Queue targetQueue,
																		int targetEntry,
																		Map<AElement, Queue> candidateQueues){				
		if(boundUnresolvedElementConflictResolverFree == 0){
			// boundUnresolvedElementConflictResolverCreated++;
			BoundUnresolvedElementConflictResolver icr = new BoundUnresolvedElementConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(conflictMessageReporter,
			        bindingModel,
			        targetQueue,
					targetEntry,
					candidateQueues);			
			return icr;			
		}else{
			BoundUnresolvedElementConflictResolver icr = boundUnresolvedElementConflictResolver[--boundUnresolvedElementConflictResolverFree];
			icr.init(conflictMessageReporter,
			        bindingModel,
			        targetQueue,
					targetEntry,
					candidateQueues);
			if(boundUnresolvedElementConflictResolverFree < boundUnresolvedElementConflictResolverMinFree) boundUnresolvedElementConflictResolverMinFree = boundUnresolvedElementConflictResolverFree; 
			return icr;
		}		
	}
			
	public void recycle(BoundUnresolvedElementConflictResolver icr){		
		if(boundUnresolvedElementConflictResolverFree == boundUnresolvedElementConflictResolver.length){
		    if(boundUnresolvedElementConflictResolverFree == boundUnresolvedElementConflictResolverMaxSize) return;
			BoundUnresolvedElementConflictResolver[] increased = new BoundUnresolvedElementConflictResolver[5+boundUnresolvedElementConflictResolver.length];
			System.arraycopy(boundUnresolvedElementConflictResolver, 0, increased, 0, boundUnresolvedElementConflictResolverFree);
			boundUnresolvedElementConflictResolver = increased;
		}
		boundUnresolvedElementConflictResolver[boundUnresolvedElementConflictResolverFree++] = icr;
	}
	
	public BoundAmbiguousAttributeConflictResolver getBoundAmbiguousAttributeConflictResolver(BitSet disqualified,
	                                                                                        TemporaryMessageStorage[] temporaryMessageStorage,
                                                                                            /*String namespaceURI,
                                                                                            String localName,
                                                                                            String qName,*/
                                                                                            String value, 
                                                                                            Queue queue, 
                                                                                            int entry, 
                                                                                            Map<AAttribute, AttributeBinder> attributeBinders){				
		if(boundAmbiguousAttributeConflictResolverFree == 0){
			// boundAmbiguousAttributeConflictResolverCreated++;
			BoundAmbiguousAttributeConflictResolver icr = new BoundAmbiguousAttributeConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(disqualified,
			        temporaryMessageStorage,
                    value, 
					queue, 
					entry, 
					attributeBinders);			
			return icr;			
		}else{
			BoundAmbiguousAttributeConflictResolver icr = boundAmbiguousAttributeConflictResolver[--boundAmbiguousAttributeConflictResolverFree];
			icr.init(disqualified,
			        temporaryMessageStorage,
                    value, 
					queue, 
					entry, 
					attributeBinders);
            if(boundAmbiguousAttributeConflictResolverFree < boundAmbiguousAttributeConflictResolverMinFree) boundAmbiguousAttributeConflictResolverMinFree = boundAmbiguousAttributeConflictResolverFree;			
			return icr;
		}		
	}
	
	public void recycle(BoundAmbiguousAttributeConflictResolver icr){		
		if(boundAmbiguousAttributeConflictResolverFree == boundAmbiguousAttributeConflictResolver.length){
		    if(boundAmbiguousAttributeConflictResolverFree == boundAmbiguousAttributeConflictResolverMaxSize) return;
			BoundAmbiguousAttributeConflictResolver[] increased = new BoundAmbiguousAttributeConflictResolver[5+boundAmbiguousAttributeConflictResolver.length];
			System.arraycopy(boundAmbiguousAttributeConflictResolver, 0, increased, 0, boundAmbiguousAttributeConflictResolverFree);
			boundAmbiguousAttributeConflictResolver = increased;
		}
		boundAmbiguousAttributeConflictResolver[boundAmbiguousAttributeConflictResolverFree++] = icr;
	}
	
	public BoundUnresolvedAttributeConflictResolver getBoundUnresolvedAttributeConflictResolver(TemporaryMessageStorage[] temporaryMessageStorage,
                                                                                            /*String namespaceURI,
                                                                                            String localName,
                                                                                            String qName,*/
                                                                                            String value, 
                                                                                            Queue queue, 
                                                                                            int entry, 
                                                                                            Map<AAttribute, AttributeBinder> attributeBinders){				
		if(boundUnresolvedAttributeConflictResolverFree == 0){
			// boundUnresolvedAttributeConflictResolverCreated++;
			BoundUnresolvedAttributeConflictResolver icr = new BoundUnresolvedAttributeConflictResolver(debugWriter);
			icr.init(this, activeInputDescriptor, inputStackDescriptor);
			icr.init(temporaryMessageStorage,
                    value, 
					queue, 
					entry, 
					attributeBinders);			
			return icr;			
		}else{
			BoundUnresolvedAttributeConflictResolver icr = boundUnresolvedAttributeConflictResolver[--boundUnresolvedAttributeConflictResolverFree];
			icr.init(temporaryMessageStorage,
                    value, 
					queue, 
					entry, 
					attributeBinders);
            if(boundUnresolvedAttributeConflictResolverFree < boundUnresolvedAttributeConflictResolverMinFree) boundUnresolvedAttributeConflictResolverMinFree = boundUnresolvedAttributeConflictResolverFree;			
			return icr;
		}		
	}
	
	public void recycle(BoundUnresolvedAttributeConflictResolver icr){		
		if(boundUnresolvedAttributeConflictResolverFree == boundUnresolvedAttributeConflictResolver.length){
		    if(boundUnresolvedAttributeConflictResolverFree == boundUnresolvedAttributeConflictResolverMaxSize) return;
			BoundUnresolvedAttributeConflictResolver[] increased = new BoundUnresolvedAttributeConflictResolver[5+boundUnresolvedAttributeConflictResolver.length];
			System.arraycopy(boundUnresolvedAttributeConflictResolver, 0, increased, 0, boundUnresolvedAttributeConflictResolverFree);
			boundUnresolvedAttributeConflictResolver = increased;
		}
		boundUnresolvedAttributeConflictResolver[boundUnresolvedAttributeConflictResolverFree++] = icr;
	}
}