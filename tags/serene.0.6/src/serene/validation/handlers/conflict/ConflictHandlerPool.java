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

import sereneWrite.MessageWriter;

public class ConflictHandlerPool{
	private static volatile ConflictHandlerPool instance;
	
	int vchPoolFree; 
	int vchPoolPoolSize;
	ActiveModelConflictHandlerPool[] vchPools;
	
	int ambiguousElementConflictResolverAverageUse;
	int ambiguousElementConflictResolverPoolSize;
	int ambiguousElementConflictResolverFree;
	AmbiguousElementConflictResolver[] ambiguousElementConflictResolver;
	
	int unresolvedElementConflictResolverAverageUse;
	int unresolvedElementConflictResolverPoolSize;
	int unresolvedElementConflictResolverFree;
	UnresolvedElementConflictResolver[] unresolvedElementConflictResolver;
    
	int ambiguousAttributeConflictResolverAverageUse;
	int ambiguousAttributeConflictResolverPoolSize;
	int ambiguousAttributeConflictResolverFree;
	AmbiguousAttributeConflictResolver[] ambiguousAttributeConflictResolver;
	
	int unresolvedAttributeConflictResolverAverageUse;
	int unresolvedAttributeConflictResolverPoolSize;
	int unresolvedAttributeConflictResolverFree;
	UnresolvedAttributeConflictResolver[] unresolvedAttributeConflictResolver;
	
	int ambiguousCharsConflictResolverAverageUse;
	int ambiguousCharsConflictResolverPoolSize;
	int ambiguousCharsConflictResolverFree;
	AmbiguousCharsConflictResolver[] ambiguousCharsConflictResolver;
	
	int unresolvedCharsConflictResolverAverageUse;
	int unresolvedCharsConflictResolverPoolSize;
	int unresolvedCharsConflictResolverFree;
	UnresolvedCharsConflictResolver[] unresolvedCharsConflictResolver;
    
    
    int ambiguousListTokenConflictResolverAverageUse;
	int ambiguousListTokenConflictResolverPoolSize;
	int ambiguousListTokenConflictResolverFree;
	AmbiguousListTokenConflictResolver[] ambiguousListTokenConflictResolver;
	
	int unresolvedListTokenConflictResolverAverageUse;
	int unresolvedListTokenConflictResolverPoolSize;
	int unresolvedListTokenConflictResolverFree;
	UnresolvedListTokenConflictResolver[] unresolvedListTokenConflictResolver;
	
	
	
	int boundAmbiguousElementConflictResolverAverageUse;
	int boundAmbiguousElementConflictResolverPoolSize;
	int boundAmbiguousElementConflictResolverFree;
	BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver;
	
	int boundUnresolvedElementConflictResolverAverageUse;
	int boundUnresolvedElementConflictResolverPoolSize;
	int boundUnresolvedElementConflictResolverFree;
	BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver;
	
	
	int boundAmbiguousAttributeConflictResolverAverageUse;
	int boundAmbiguousAttributeConflictResolverPoolSize;
	int boundAmbiguousAttributeConflictResolverFree;
	BoundAmbiguousAttributeConflictResolver[] boundAmbiguousAttributeConflictResolver;
		
	int boundUnresolvedAttributeConflictResolverAverageUse;
	int boundUnresolvedAttributeConflictResolverPoolSize;
	int boundUnresolvedAttributeConflictResolverFree;
	BoundUnresolvedAttributeConflictResolver[] boundUnresolvedAttributeConflictResolver;
		
	
	final int UNUSED = 0;
	
	MessageWriter debugWriter;
	
	private ConflictHandlerPool(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		vchPoolFree = 0;
		vchPoolPoolSize = 10;
		vchPools = new ActiveModelConflictHandlerPool[vchPoolPoolSize];
		
		ambiguousElementConflictResolverAverageUse = UNUSED;
		ambiguousElementConflictResolverPoolSize = 10;
		ambiguousElementConflictResolverFree = 0;
		ambiguousElementConflictResolver = new AmbiguousElementConflictResolver[ambiguousElementConflictResolverPoolSize];
		
		unresolvedElementConflictResolverAverageUse = UNUSED;
		unresolvedElementConflictResolverPoolSize = 10;
		unresolvedElementConflictResolverFree = 0;
		unresolvedElementConflictResolver = new UnresolvedElementConflictResolver[unresolvedElementConflictResolverPoolSize];
		
		ambiguousAttributeConflictResolverAverageUse = UNUSED;
		ambiguousAttributeConflictResolverPoolSize = 10;
		ambiguousAttributeConflictResolverFree = 0;
		ambiguousAttributeConflictResolver = new AmbiguousAttributeConflictResolver[ambiguousAttributeConflictResolverPoolSize];
        
		unresolvedAttributeConflictResolverAverageUse = UNUSED;
		unresolvedAttributeConflictResolverPoolSize = 10;
		unresolvedAttributeConflictResolverFree = 0;
		unresolvedAttributeConflictResolver = new UnresolvedAttributeConflictResolver[unresolvedAttributeConflictResolverPoolSize];
        
		
		ambiguousCharsConflictResolverAverageUse = UNUSED;
		ambiguousCharsConflictResolverPoolSize = 10;
		ambiguousCharsConflictResolverFree = 0;
		ambiguousCharsConflictResolver = new AmbiguousCharsConflictResolver[ambiguousCharsConflictResolverPoolSize];
        		
		unresolvedCharsConflictResolverAverageUse = UNUSED;
		unresolvedCharsConflictResolverPoolSize = 10;
		unresolvedCharsConflictResolverFree = 0;
		unresolvedCharsConflictResolver = new UnresolvedCharsConflictResolver[unresolvedCharsConflictResolverPoolSize];
        		
		
        ambiguousListTokenConflictResolverAverageUse = UNUSED;
		ambiguousListTokenConflictResolverPoolSize = 10;
		ambiguousListTokenConflictResolverFree = 0;
		ambiguousListTokenConflictResolver = new AmbiguousListTokenConflictResolver[ambiguousListTokenConflictResolverPoolSize];
		
		unresolvedListTokenConflictResolverAverageUse = UNUSED;
		unresolvedListTokenConflictResolverPoolSize = 10;
		unresolvedListTokenConflictResolverFree = 0;
		unresolvedListTokenConflictResolver = new UnresolvedListTokenConflictResolver[unresolvedListTokenConflictResolverPoolSize];
		
		
		
		boundAmbiguousElementConflictResolverAverageUse = UNUSED;
		boundAmbiguousElementConflictResolverPoolSize = 10;
		boundAmbiguousElementConflictResolverFree = 0;
		boundAmbiguousElementConflictResolver = new BoundAmbiguousElementConflictResolver[boundAmbiguousElementConflictResolverPoolSize];
		
		boundUnresolvedElementConflictResolverAverageUse = UNUSED;
		boundUnresolvedElementConflictResolverPoolSize = 10;
		boundUnresolvedElementConflictResolverFree = 0;
		boundUnresolvedElementConflictResolver = new BoundUnresolvedElementConflictResolver[boundUnresolvedElementConflictResolverPoolSize];
		
		boundAmbiguousAttributeConflictResolverAverageUse = UNUSED;
		boundAmbiguousAttributeConflictResolverPoolSize = 10;
		boundAmbiguousAttributeConflictResolverFree = 0;
		boundAmbiguousAttributeConflictResolver = new BoundAmbiguousAttributeConflictResolver[boundAmbiguousAttributeConflictResolverPoolSize];
		
		boundUnresolvedAttributeConflictResolverAverageUse = UNUSED;
		boundUnresolvedAttributeConflictResolverPoolSize = 10;
		boundUnresolvedAttributeConflictResolverFree = 0;
		boundUnresolvedAttributeConflictResolver = new BoundUnresolvedAttributeConflictResolver[boundUnresolvedAttributeConflictResolverPoolSize];
		
	}
	
	public static ConflictHandlerPool getInstance(MessageWriter debugWriter){
		if(instance == null){
			synchronized(ConflictHandlerPool.class){
				if(instance == null){
					instance = new ConflictHandlerPool(debugWriter); 
				}
			}
		}
		return instance;
	}
	
	synchronized void fill(ActiveModelConflictHandlerPool pool,
					AmbiguousElementConflictResolver[] ambiguousElementConflictResolver,
					UnresolvedElementConflictResolver[] unresolvedElementConflictResolver,
					AmbiguousAttributeConflictResolver[] ambiguousAttributeConflictResolver,
					UnresolvedAttributeConflictResolver[] unresolvedAttributeConflictResolver,
					AmbiguousCharsConflictResolver[] ambiguousCharsConflictResolver,
					UnresolvedCharsConflictResolver[] unresolvedCharsConflictResolver,
                    AmbiguousListTokenConflictResolver[] ambiguousListTokenConflictResolver,
                    UnresolvedListTokenConflictResolver[] unresolvedListTokenConflictResolver,
					BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver,
					BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver,
					BoundAmbiguousAttributeConflictResolver[] boundAmbiguousAttributeConflictResolver,
					BoundUnresolvedAttributeConflictResolver[] boundUnresolvedAttributeConflictResolver){
		int ambiguousElementConflictResolverFillCount;
		if(ambiguousElementConflictResolver == null || ambiguousElementConflictResolver.length < ambiguousElementConflictResolverAverageUse)
			ambiguousElementConflictResolver = new AmbiguousElementConflictResolver[ambiguousElementConflictResolverAverageUse];
		if(ambiguousElementConflictResolverFree > ambiguousElementConflictResolverAverageUse){
			ambiguousElementConflictResolverFillCount = ambiguousElementConflictResolverAverageUse;
			ambiguousElementConflictResolverFree = ambiguousElementConflictResolverFree - ambiguousElementConflictResolverAverageUse;
		}else{
			ambiguousElementConflictResolverFillCount = ambiguousElementConflictResolverFree;
			ambiguousElementConflictResolverFree = 0;
		}		
		System.arraycopy(this.ambiguousElementConflictResolver, ambiguousElementConflictResolverFree, 
							ambiguousElementConflictResolver, 0, ambiguousElementConflictResolverFillCount);
        
        
		int unresolvedElementConflictResolverFillCount;
		if(unresolvedElementConflictResolver == null || unresolvedElementConflictResolver.length < unresolvedElementConflictResolverAverageUse)
			unresolvedElementConflictResolver = new UnresolvedElementConflictResolver[unresolvedElementConflictResolverAverageUse];
		if(unresolvedElementConflictResolverFree > unresolvedElementConflictResolverAverageUse){
			unresolvedElementConflictResolverFillCount = unresolvedElementConflictResolverAverageUse;
			unresolvedElementConflictResolverFree = unresolvedElementConflictResolverFree - unresolvedElementConflictResolverAverageUse;
		}else{
			unresolvedElementConflictResolverFillCount = unresolvedElementConflictResolverFree;
			unresolvedElementConflictResolverFree = 0;
		}		
		System.arraycopy(this.unresolvedElementConflictResolver, unresolvedElementConflictResolverFree, 
							unresolvedElementConflictResolver, 0, unresolvedElementConflictResolverFillCount);
        
		
		int ambiguousAttributeConflictResolverFillCount;
		if(ambiguousAttributeConflictResolver == null || ambiguousAttributeConflictResolver.length < ambiguousAttributeConflictResolverAverageUse)
			ambiguousAttributeConflictResolver = new AmbiguousAttributeConflictResolver[ambiguousAttributeConflictResolverAverageUse];
		if(ambiguousAttributeConflictResolverFree > ambiguousAttributeConflictResolverAverageUse){
			ambiguousAttributeConflictResolverFillCount = ambiguousAttributeConflictResolverAverageUse;
			ambiguousAttributeConflictResolverFree = ambiguousAttributeConflictResolverFree - ambiguousAttributeConflictResolverAverageUse;
		}else{
			ambiguousAttributeConflictResolverFillCount = ambiguousAttributeConflictResolverFree;
			ambiguousAttributeConflictResolverFree = 0;
		}		
		System.arraycopy(this.ambiguousAttributeConflictResolver, ambiguousAttributeConflictResolverFree, 
							ambiguousAttributeConflictResolver, 0, ambiguousAttributeConflictResolverFillCount);
        
		int unresolvedAttributeConflictResolverFillCount;
		if(unresolvedAttributeConflictResolver == null || unresolvedAttributeConflictResolver.length < unresolvedAttributeConflictResolverAverageUse)
			unresolvedAttributeConflictResolver = new UnresolvedAttributeConflictResolver[unresolvedAttributeConflictResolverAverageUse];
		if(unresolvedAttributeConflictResolverFree > unresolvedAttributeConflictResolverAverageUse){
			unresolvedAttributeConflictResolverFillCount = unresolvedAttributeConflictResolverAverageUse;
			unresolvedAttributeConflictResolverFree = unresolvedAttributeConflictResolverFree - unresolvedAttributeConflictResolverAverageUse;
		}else{
			unresolvedAttributeConflictResolverFillCount = unresolvedAttributeConflictResolverFree;
			unresolvedAttributeConflictResolverFree = 0;
		}		
		System.arraycopy(this.unresolvedAttributeConflictResolver, unresolvedAttributeConflictResolverFree, 
							unresolvedAttributeConflictResolver, 0, unresolvedAttributeConflictResolverFillCount);
        
		
		int ambiguousCharsConflictResolverFillCount;
		if(ambiguousCharsConflictResolver == null || ambiguousCharsConflictResolver.length < ambiguousCharsConflictResolverAverageUse)
			ambiguousCharsConflictResolver = new AmbiguousCharsConflictResolver[ambiguousCharsConflictResolverAverageUse];
		if(ambiguousCharsConflictResolverFree > ambiguousCharsConflictResolverAverageUse){
			ambiguousCharsConflictResolverFillCount = ambiguousCharsConflictResolverAverageUse;
			ambiguousCharsConflictResolverFree = ambiguousCharsConflictResolverFree - ambiguousCharsConflictResolverAverageUse;
		}else{
			ambiguousCharsConflictResolverFillCount = ambiguousCharsConflictResolverFree;
			ambiguousCharsConflictResolverFree = 0;
		}		
		System.arraycopy(this.ambiguousCharsConflictResolver, ambiguousCharsConflictResolverFree, 
							ambiguousCharsConflictResolver, 0, ambiguousCharsConflictResolverFillCount);
        
		int unresolvedCharsConflictResolverFillCount;
		if(unresolvedCharsConflictResolver == null || unresolvedCharsConflictResolver.length < unresolvedCharsConflictResolverAverageUse)
			unresolvedCharsConflictResolver = new UnresolvedCharsConflictResolver[unresolvedCharsConflictResolverAverageUse];
		if(unresolvedCharsConflictResolverFree > unresolvedCharsConflictResolverAverageUse){
			unresolvedCharsConflictResolverFillCount = unresolvedCharsConflictResolverAverageUse;
			unresolvedCharsConflictResolverFree = unresolvedCharsConflictResolverFree - unresolvedCharsConflictResolverAverageUse;
		}else{
			unresolvedCharsConflictResolverFillCount = unresolvedCharsConflictResolverFree;
			unresolvedCharsConflictResolverFree = 0;
		}		
		System.arraycopy(this.unresolvedCharsConflictResolver, unresolvedCharsConflictResolverFree, 
							unresolvedCharsConflictResolver, 0, unresolvedCharsConflictResolverFillCount);
        
		
        int ambiguousListTokenConflictResolverFillCount;
		if(ambiguousListTokenConflictResolver == null || ambiguousListTokenConflictResolver.length < ambiguousListTokenConflictResolverAverageUse)
			ambiguousListTokenConflictResolver = new AmbiguousListTokenConflictResolver[ambiguousListTokenConflictResolverAverageUse];
		if(ambiguousListTokenConflictResolverFree > ambiguousListTokenConflictResolverAverageUse){
			ambiguousListTokenConflictResolverFillCount = ambiguousListTokenConflictResolverAverageUse;
			ambiguousListTokenConflictResolverFree = ambiguousListTokenConflictResolverFree - ambiguousListTokenConflictResolverAverageUse;
		}else{
			ambiguousListTokenConflictResolverFillCount = ambiguousListTokenConflictResolverFree;
			ambiguousListTokenConflictResolverFree = 0;
		}		
		System.arraycopy(this.ambiguousListTokenConflictResolver, ambiguousListTokenConflictResolverFree, 
							ambiguousListTokenConflictResolver, 0, ambiguousListTokenConflictResolverFillCount);
		
		int unresolvedListTokenConflictResolverFillCount;
		if(unresolvedListTokenConflictResolver == null || unresolvedListTokenConflictResolver.length < unresolvedListTokenConflictResolverAverageUse)
			unresolvedListTokenConflictResolver = new UnresolvedListTokenConflictResolver[unresolvedListTokenConflictResolverAverageUse];
		if(unresolvedListTokenConflictResolverFree > unresolvedListTokenConflictResolverAverageUse){
			unresolvedListTokenConflictResolverFillCount = unresolvedListTokenConflictResolverAverageUse;
			unresolvedListTokenConflictResolverFree = unresolvedListTokenConflictResolverFree - unresolvedListTokenConflictResolverAverageUse;
		}else{
			unresolvedListTokenConflictResolverFillCount = unresolvedListTokenConflictResolverFree;
			unresolvedListTokenConflictResolverFree = 0;
		}		
		System.arraycopy(this.unresolvedListTokenConflictResolver, unresolvedListTokenConflictResolverFree, 
							unresolvedListTokenConflictResolver, 0, unresolvedListTokenConflictResolverFillCount);
		
		
		
		int boundAmbiguousElementConflictResolverFillCount;
		if(boundAmbiguousElementConflictResolver == null || boundAmbiguousElementConflictResolver.length < boundAmbiguousElementConflictResolverAverageUse)
			boundAmbiguousElementConflictResolver = new BoundAmbiguousElementConflictResolver[boundAmbiguousElementConflictResolverAverageUse];
		if(boundAmbiguousElementConflictResolverFree > boundAmbiguousElementConflictResolverAverageUse){
			boundAmbiguousElementConflictResolverFillCount = boundAmbiguousElementConflictResolverAverageUse;
			boundAmbiguousElementConflictResolverFree = boundAmbiguousElementConflictResolverFree - boundAmbiguousElementConflictResolverAverageUse;
		}else{
			boundAmbiguousElementConflictResolverFillCount = boundAmbiguousElementConflictResolverFree;
			boundAmbiguousElementConflictResolverFree = 0;
		}		
		System.arraycopy(this.boundAmbiguousElementConflictResolver, boundAmbiguousElementConflictResolverFree, 
							boundAmbiguousElementConflictResolver, 0, boundAmbiguousElementConflictResolverFillCount);
		
		int boundUnresolvedElementConflictResolverFillCount;
		if(boundUnresolvedElementConflictResolver == null || boundUnresolvedElementConflictResolver.length < boundUnresolvedElementConflictResolverAverageUse)
			boundUnresolvedElementConflictResolver = new BoundUnresolvedElementConflictResolver[boundUnresolvedElementConflictResolverAverageUse];
		if(boundUnresolvedElementConflictResolverFree > boundUnresolvedElementConflictResolverAverageUse){
			boundUnresolvedElementConflictResolverFillCount = boundUnresolvedElementConflictResolverAverageUse;
			boundUnresolvedElementConflictResolverFree = boundUnresolvedElementConflictResolverFree - boundUnresolvedElementConflictResolverAverageUse;
		}else{
			boundUnresolvedElementConflictResolverFillCount = boundUnresolvedElementConflictResolverFree;
			boundUnresolvedElementConflictResolverFree = 0;
		}		
		System.arraycopy(this.boundUnresolvedElementConflictResolver, boundUnresolvedElementConflictResolverFree, 
							boundUnresolvedElementConflictResolver, 0, boundUnresolvedElementConflictResolverFillCount);
		
		
		int boundAmbiguousAttributeConflictResolverFillCount;
		if(boundAmbiguousAttributeConflictResolver == null || boundAmbiguousAttributeConflictResolver.length < boundAmbiguousAttributeConflictResolverAverageUse)
			boundAmbiguousAttributeConflictResolver = new BoundAmbiguousAttributeConflictResolver[boundAmbiguousAttributeConflictResolverAverageUse];
		if(boundAmbiguousAttributeConflictResolverFree > boundAmbiguousAttributeConflictResolverAverageUse){
			boundAmbiguousAttributeConflictResolverFillCount = boundAmbiguousAttributeConflictResolverAverageUse;
			boundAmbiguousAttributeConflictResolverFree = boundAmbiguousAttributeConflictResolverFree - boundAmbiguousAttributeConflictResolverAverageUse;
		}else{
			boundAmbiguousAttributeConflictResolverFillCount = boundAmbiguousAttributeConflictResolverFree;
			boundAmbiguousAttributeConflictResolverFree = 0;
		}		
		System.arraycopy(this.boundAmbiguousAttributeConflictResolver, boundAmbiguousAttributeConflictResolverFree, 
							boundAmbiguousAttributeConflictResolver, 0, boundAmbiguousAttributeConflictResolverFillCount);
		
		int boundUnresolvedAttributeConflictResolverFillCount;
		if(boundUnresolvedAttributeConflictResolver == null || boundUnresolvedAttributeConflictResolver.length < boundUnresolvedAttributeConflictResolverAverageUse)
			boundUnresolvedAttributeConflictResolver = new BoundUnresolvedAttributeConflictResolver[boundUnresolvedAttributeConflictResolverAverageUse];
		if(boundUnresolvedAttributeConflictResolverFree > boundUnresolvedAttributeConflictResolverAverageUse){
			boundUnresolvedAttributeConflictResolverFillCount = boundUnresolvedAttributeConflictResolverAverageUse;
			boundUnresolvedAttributeConflictResolverFree = boundUnresolvedAttributeConflictResolverFree - boundUnresolvedAttributeConflictResolverAverageUse;
		}else{
			boundUnresolvedAttributeConflictResolverFillCount = boundUnresolvedAttributeConflictResolverFree;
			boundUnresolvedAttributeConflictResolverFree = 0;
		}		
		System.arraycopy(this.boundUnresolvedAttributeConflictResolver, boundUnresolvedAttributeConflictResolverFree, 
							boundUnresolvedAttributeConflictResolver, 0, boundUnresolvedAttributeConflictResolverFillCount);
		
				
		pool.setHandlers(ambiguousElementConflictResolverFillCount,
							ambiguousElementConflictResolver,
							unresolvedElementConflictResolverFillCount,
							unresolvedElementConflictResolver,
							ambiguousAttributeConflictResolverFillCount,
							ambiguousAttributeConflictResolver,
							unresolvedAttributeConflictResolverFillCount,
							unresolvedAttributeConflictResolver,
							ambiguousCharsConflictResolverFillCount,
							ambiguousCharsConflictResolver,
							unresolvedCharsConflictResolverFillCount,
							unresolvedCharsConflictResolver,
                            ambiguousListTokenConflictResolverFillCount,
							ambiguousListTokenConflictResolver,
							unresolvedListTokenConflictResolverFillCount,
							unresolvedListTokenConflictResolver,
							boundAmbiguousElementConflictResolverFillCount,
							boundAmbiguousElementConflictResolver,
							boundUnresolvedElementConflictResolverFillCount,
							boundUnresolvedElementConflictResolver,
							boundAmbiguousAttributeConflictResolverFillCount,
							boundAmbiguousAttributeConflictResolver,
							boundUnresolvedAttributeConflictResolverFillCount,
							boundUnresolvedAttributeConflictResolver);
	}
	
	synchronized void recycle(int ambiguousElementConflictResolverAverageUse,
						AmbiguousElementConflictResolver[] ambiguousElementConflictResolver,
						int unresolvedElementConflictResolverAverageUse,
						UnresolvedElementConflictResolver[] unresolvedElementConflictResolver,
						int ambiguousAttributeConflictResolverAverageUse,
						AmbiguousAttributeConflictResolver[] ambiguousAttributeConflictResolver,
						int unresolvedAttributeConflictResolverAverageUse,
						UnresolvedAttributeConflictResolver[] unresolvedAttributeConflictResolver,
						int ambiguousCharsConflictResolverAverageUse,
						AmbiguousCharsConflictResolver[] ambiguousCharsConflictResolver,
						int unresolvedCharsConflictResolverAverageUse,
						UnresolvedCharsConflictResolver[] unresolvedCharsConflictResolver,
                        int ambiguousListTokenConflictResolverAverageUse,
						AmbiguousListTokenConflictResolver[] ambiguousListTokenConflictResolver,
						int unresolvedListTokenConflictResolverAverageUse,
						UnresolvedListTokenConflictResolver[] unresolvedListTokenConflictResolver,
						int boundAmbiguousElementConflictResolverAverageUse,
						BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver,
						int boundUnresolvedElementConflictResolverAverageUse,
						BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver,
						int boundAmbiguousAttributeConflictResolverAverageUse,
						BoundAmbiguousAttributeConflictResolver[] boundAmbiguousAttributeConflictResolver,
						int boundUnresolvedAttributeConflictResolverAverageUse,
						BoundUnresolvedAttributeConflictResolver[] boundUnresolvedAttributeConflictResolver){
		if(ambiguousElementConflictResolverFree + ambiguousElementConflictResolverAverageUse >= ambiguousElementConflictResolverPoolSize){			 
			ambiguousElementConflictResolverPoolSize+= ambiguousElementConflictResolverAverageUse;
			AmbiguousElementConflictResolver[] increased = new AmbiguousElementConflictResolver[ambiguousElementConflictResolverPoolSize];
			System.arraycopy(this.ambiguousElementConflictResolver, 0, increased, 0, ambiguousElementConflictResolverFree);
			this.ambiguousElementConflictResolver = increased;
		}
		System.arraycopy(ambiguousElementConflictResolver, 0, this.ambiguousElementConflictResolver, ambiguousElementConflictResolverFree, ambiguousElementConflictResolverAverageUse);
		ambiguousElementConflictResolverFree += ambiguousElementConflictResolverAverageUse;
		if(this.ambiguousElementConflictResolverAverageUse != 0) this.ambiguousElementConflictResolverAverageUse = (this.ambiguousElementConflictResolverAverageUse + ambiguousElementConflictResolverAverageUse)/2;
		else this.ambiguousElementConflictResolverAverageUse = ambiguousElementConflictResolverAverageUse;
		//System.out.println("ambiguousElementConflictResolver "+this.ambiguousElementConflictResolverAverageUse);
		
		if(unresolvedElementConflictResolverFree + unresolvedElementConflictResolverAverageUse >= unresolvedElementConflictResolverPoolSize){			 
			unresolvedElementConflictResolverPoolSize+= unresolvedElementConflictResolverAverageUse;
			UnresolvedElementConflictResolver[] increased = new UnresolvedElementConflictResolver[unresolvedElementConflictResolverPoolSize];
			System.arraycopy(this.unresolvedElementConflictResolver, 0, increased, 0, unresolvedElementConflictResolverFree);
			this.unresolvedElementConflictResolver = increased;
		}
		System.arraycopy(unresolvedElementConflictResolver, 0, this.unresolvedElementConflictResolver, unresolvedElementConflictResolverFree, unresolvedElementConflictResolverAverageUse);
		unresolvedElementConflictResolverFree += unresolvedElementConflictResolverAverageUse;
		if(this.unresolvedElementConflictResolverAverageUse != 0) this.unresolvedElementConflictResolverAverageUse = (this.unresolvedElementConflictResolverAverageUse + unresolvedElementConflictResolverAverageUse)/2;
		else this.unresolvedElementConflictResolverAverageUse = unresolvedElementConflictResolverAverageUse;
		//System.out.println("unresolvedElementConflictResolver "+this.unresolvedElementConflictResolverAverageUse);
        
		if(ambiguousAttributeConflictResolverFree + ambiguousAttributeConflictResolverAverageUse >= ambiguousAttributeConflictResolverPoolSize){			 
			ambiguousAttributeConflictResolverPoolSize+= ambiguousAttributeConflictResolverAverageUse;
			AmbiguousAttributeConflictResolver[] increased = new AmbiguousAttributeConflictResolver[ambiguousAttributeConflictResolverPoolSize];
			System.arraycopy(this.ambiguousAttributeConflictResolver, 0, increased, 0, ambiguousAttributeConflictResolverFree);
			this.ambiguousAttributeConflictResolver = increased;
		}
		System.arraycopy(ambiguousAttributeConflictResolver, 0, this.ambiguousAttributeConflictResolver, ambiguousAttributeConflictResolverFree, ambiguousAttributeConflictResolverAverageUse);
		ambiguousAttributeConflictResolverFree += ambiguousAttributeConflictResolverAverageUse;
		if(this.ambiguousAttributeConflictResolverAverageUse != 0) this.ambiguousAttributeConflictResolverAverageUse = (this.ambiguousAttributeConflictResolverAverageUse + ambiguousAttributeConflictResolverAverageUse)/2;
		else this.ambiguousAttributeConflictResolverAverageUse = ambiguousAttributeConflictResolverAverageUse;
		//System.out.println("ambiguousAttributeConflictResolver "+this.ambiguousAttributeConflictResolverAverageUse);
        
		if(unresolvedAttributeConflictResolverFree + unresolvedAttributeConflictResolverAverageUse >= unresolvedAttributeConflictResolverPoolSize){			 
			unresolvedAttributeConflictResolverPoolSize+= unresolvedAttributeConflictResolverAverageUse;
			UnresolvedAttributeConflictResolver[] increased = new UnresolvedAttributeConflictResolver[unresolvedAttributeConflictResolverPoolSize];
			System.arraycopy(this.unresolvedAttributeConflictResolver, 0, increased, 0, unresolvedAttributeConflictResolverFree);
			this.unresolvedAttributeConflictResolver = increased;
		}
		System.arraycopy(unresolvedAttributeConflictResolver, 0, this.unresolvedAttributeConflictResolver, unresolvedAttributeConflictResolverFree, unresolvedAttributeConflictResolverAverageUse);
		unresolvedAttributeConflictResolverFree += unresolvedAttributeConflictResolverAverageUse;
		if(this.unresolvedAttributeConflictResolverAverageUse != 0) this.unresolvedAttributeConflictResolverAverageUse = (this.unresolvedAttributeConflictResolverAverageUse + unresolvedAttributeConflictResolverAverageUse)/2;
		else this.unresolvedAttributeConflictResolverAverageUse = unresolvedAttributeConflictResolverAverageUse;
		//System.out.println("unresolvedAttributeConflictResolver "+this.unresolvedAttributeConflictResolverAverageUse);
        
		
		if(ambiguousCharsConflictResolverFree + ambiguousCharsConflictResolverAverageUse >= ambiguousCharsConflictResolverPoolSize){			 
			ambiguousCharsConflictResolverPoolSize+= ambiguousCharsConflictResolverAverageUse;
			AmbiguousCharsConflictResolver[] increased = new AmbiguousCharsConflictResolver[ambiguousCharsConflictResolverPoolSize];
			System.arraycopy(this.ambiguousCharsConflictResolver, 0, increased, 0, ambiguousCharsConflictResolverFree);
			this.ambiguousCharsConflictResolver = increased;
		}
		System.arraycopy(ambiguousCharsConflictResolver, 0, this.ambiguousCharsConflictResolver, ambiguousCharsConflictResolverFree, ambiguousCharsConflictResolverAverageUse);
		ambiguousCharsConflictResolverFree += ambiguousCharsConflictResolverAverageUse;
		if(this.ambiguousCharsConflictResolverAverageUse != 0) this.ambiguousCharsConflictResolverAverageUse = (this.ambiguousCharsConflictResolverAverageUse + ambiguousCharsConflictResolverAverageUse)/2;
		else this.ambiguousCharsConflictResolverAverageUse = ambiguousCharsConflictResolverAverageUse;
		//System.out.println("ambiguousCharsConflictResolver "+this.ambiguousCharsConflictResolverAverageUse);
        
		if(unresolvedCharsConflictResolverFree + unresolvedCharsConflictResolverAverageUse >= unresolvedCharsConflictResolverPoolSize){			 
			unresolvedCharsConflictResolverPoolSize+= unresolvedCharsConflictResolverAverageUse;
			UnresolvedCharsConflictResolver[] increased = new UnresolvedCharsConflictResolver[unresolvedCharsConflictResolverPoolSize];
			System.arraycopy(this.unresolvedCharsConflictResolver, 0, increased, 0, unresolvedCharsConflictResolverFree);
			this.unresolvedCharsConflictResolver = increased;
		}
		System.arraycopy(unresolvedCharsConflictResolver, 0, this.unresolvedCharsConflictResolver, unresolvedCharsConflictResolverFree, unresolvedCharsConflictResolverAverageUse);
		unresolvedCharsConflictResolverFree += unresolvedCharsConflictResolverAverageUse;
		if(this.unresolvedCharsConflictResolverAverageUse != 0) this.unresolvedCharsConflictResolverAverageUse = (this.unresolvedCharsConflictResolverAverageUse + unresolvedCharsConflictResolverAverageUse)/2;
		else this.unresolvedCharsConflictResolverAverageUse = unresolvedCharsConflictResolverAverageUse;
		//System.out.println("unresolvedCharsConflictResolver "+this.unresolvedCharsConflictResolverAverageUse);
        
        if(ambiguousListTokenConflictResolverFree + ambiguousListTokenConflictResolverAverageUse >= ambiguousListTokenConflictResolverPoolSize){			 
			ambiguousListTokenConflictResolverPoolSize+= ambiguousListTokenConflictResolverAverageUse;
			AmbiguousListTokenConflictResolver[] increased = new AmbiguousListTokenConflictResolver[ambiguousListTokenConflictResolverPoolSize];
			System.arraycopy(this.ambiguousListTokenConflictResolver, 0, increased, 0, ambiguousListTokenConflictResolverFree);
			this.ambiguousListTokenConflictResolver = increased;
		}
		System.arraycopy(ambiguousListTokenConflictResolver, 0, this.ambiguousListTokenConflictResolver, ambiguousListTokenConflictResolverFree, ambiguousListTokenConflictResolverAverageUse);
		ambiguousListTokenConflictResolverFree += ambiguousListTokenConflictResolverAverageUse;
		if(this.ambiguousListTokenConflictResolverAverageUse != 0) this.ambiguousListTokenConflictResolverAverageUse = (this.ambiguousListTokenConflictResolverAverageUse + ambiguousListTokenConflictResolverAverageUse)/2;
		else this.ambiguousListTokenConflictResolverAverageUse = ambiguousListTokenConflictResolverAverageUse;
		//System.out.println("ambiguousListTokenConflictResolver "+this.ambiguousListTokenConflictResolverAverageUse);
		
		if(unresolvedListTokenConflictResolverFree + unresolvedListTokenConflictResolverAverageUse >= unresolvedListTokenConflictResolverPoolSize){			 
			unresolvedListTokenConflictResolverPoolSize+= unresolvedListTokenConflictResolverAverageUse;
			UnresolvedListTokenConflictResolver[] increased = new UnresolvedListTokenConflictResolver[unresolvedListTokenConflictResolverPoolSize];
			System.arraycopy(this.unresolvedListTokenConflictResolver, 0, increased, 0, unresolvedListTokenConflictResolverFree);
			this.unresolvedListTokenConflictResolver = increased;
		}
		System.arraycopy(unresolvedListTokenConflictResolver, 0, this.unresolvedListTokenConflictResolver, unresolvedListTokenConflictResolverFree, unresolvedListTokenConflictResolverAverageUse);
		unresolvedListTokenConflictResolverFree += unresolvedListTokenConflictResolverAverageUse;
		if(this.unresolvedListTokenConflictResolverAverageUse != 0) this.unresolvedListTokenConflictResolverAverageUse = (this.unresolvedListTokenConflictResolverAverageUse + unresolvedListTokenConflictResolverAverageUse)/2;
		else this.unresolvedListTokenConflictResolverAverageUse = unresolvedListTokenConflictResolverAverageUse;
		//System.out.println("unresolvedListTokenConflictResolver "+this.unresolvedListTokenConflictResolverAverageUse);
		
		
		
		if(boundAmbiguousElementConflictResolverFree + boundAmbiguousElementConflictResolverAverageUse >= boundAmbiguousElementConflictResolverPoolSize){			 
			boundAmbiguousElementConflictResolverPoolSize+= boundAmbiguousElementConflictResolverAverageUse;
			BoundAmbiguousElementConflictResolver[] increased = new BoundAmbiguousElementConflictResolver[boundAmbiguousElementConflictResolverPoolSize];
			System.arraycopy(this.boundAmbiguousElementConflictResolver, 0, increased, 0, boundAmbiguousElementConflictResolverFree);
			this.boundAmbiguousElementConflictResolver = increased;
		}
		System.arraycopy(boundAmbiguousElementConflictResolver, 0, this.boundAmbiguousElementConflictResolver, boundAmbiguousElementConflictResolverFree, boundAmbiguousElementConflictResolverAverageUse);
		boundAmbiguousElementConflictResolverFree += boundAmbiguousElementConflictResolverAverageUse;
		if(this.boundAmbiguousElementConflictResolverAverageUse != 0) this.boundAmbiguousElementConflictResolverAverageUse = (this.boundAmbiguousElementConflictResolverAverageUse + boundAmbiguousElementConflictResolverAverageUse)/2;
		else this.boundAmbiguousElementConflictResolverAverageUse = boundAmbiguousElementConflictResolverAverageUse;
		//System.out.println("boundAmbiguousElementConflictResolver "+this.boundAmbiguousElementConflictResolverAverageUse);
		
		
		if(boundUnresolvedElementConflictResolverFree + boundUnresolvedElementConflictResolverAverageUse >= boundUnresolvedElementConflictResolverPoolSize){			 
			boundUnresolvedElementConflictResolverPoolSize+= boundUnresolvedElementConflictResolverAverageUse;
			BoundUnresolvedElementConflictResolver[] increased = new BoundUnresolvedElementConflictResolver[boundUnresolvedElementConflictResolverPoolSize];
			System.arraycopy(this.boundUnresolvedElementConflictResolver, 0, increased, 0, boundUnresolvedElementConflictResolverFree);
			this.boundUnresolvedElementConflictResolver = increased;
		}
		System.arraycopy(boundUnresolvedElementConflictResolver, 0, this.boundUnresolvedElementConflictResolver, boundUnresolvedElementConflictResolverFree, boundUnresolvedElementConflictResolverAverageUse);
		boundUnresolvedElementConflictResolverFree += boundUnresolvedElementConflictResolverAverageUse;
		if(this.boundUnresolvedElementConflictResolverAverageUse != 0) this.boundUnresolvedElementConflictResolverAverageUse = (this.boundUnresolvedElementConflictResolverAverageUse + boundUnresolvedElementConflictResolverAverageUse)/2;
		else this.boundUnresolvedElementConflictResolverAverageUse = boundUnresolvedElementConflictResolverAverageUse;
		//System.out.println("boundUnresolvedElementConflictResolver "+this.boundUnresolvedElementConflictResolverAverageUse);
		
		
		if(boundAmbiguousAttributeConflictResolverFree + boundAmbiguousAttributeConflictResolverAverageUse >= boundAmbiguousAttributeConflictResolverPoolSize){			 
			boundAmbiguousAttributeConflictResolverPoolSize+= boundAmbiguousAttributeConflictResolverAverageUse;
			BoundAmbiguousAttributeConflictResolver[] increased = new BoundAmbiguousAttributeConflictResolver[boundAmbiguousAttributeConflictResolverPoolSize];
			System.arraycopy(this.boundAmbiguousAttributeConflictResolver, 0, increased, 0, boundAmbiguousAttributeConflictResolverFree);
			this.boundAmbiguousAttributeConflictResolver = increased;
		}
		System.arraycopy(boundAmbiguousAttributeConflictResolver, 0, this.boundAmbiguousAttributeConflictResolver, boundAmbiguousAttributeConflictResolverFree, boundAmbiguousAttributeConflictResolverAverageUse);
		boundAmbiguousAttributeConflictResolverFree += boundAmbiguousAttributeConflictResolverAverageUse;
		if(this.boundAmbiguousAttributeConflictResolverAverageUse != 0) this.boundAmbiguousAttributeConflictResolverAverageUse = (this.boundAmbiguousAttributeConflictResolverAverageUse + boundAmbiguousAttributeConflictResolverAverageUse)/2;
		else this.boundAmbiguousAttributeConflictResolverAverageUse = boundAmbiguousAttributeConflictResolverAverageUse;
		//System.out.println("boundAmbiguousAttributeConflictResolver "+this.boundAmbiguousAttributeConflictResolverAverageUse);		
		
		if(boundUnresolvedAttributeConflictResolverFree + boundUnresolvedAttributeConflictResolverAverageUse >= boundUnresolvedAttributeConflictResolverPoolSize){			 
			boundUnresolvedAttributeConflictResolverPoolSize+= boundUnresolvedAttributeConflictResolverAverageUse;
			BoundUnresolvedAttributeConflictResolver[] increased = new BoundUnresolvedAttributeConflictResolver[boundUnresolvedAttributeConflictResolverPoolSize];
			System.arraycopy(this.boundUnresolvedAttributeConflictResolver, 0, increased, 0, boundUnresolvedAttributeConflictResolverFree);
			this.boundUnresolvedAttributeConflictResolver = increased;
		}
		System.arraycopy(boundUnresolvedAttributeConflictResolver, 0, this.boundUnresolvedAttributeConflictResolver, boundUnresolvedAttributeConflictResolverFree, boundUnresolvedAttributeConflictResolverAverageUse);
		boundUnresolvedAttributeConflictResolverFree += boundUnresolvedAttributeConflictResolverAverageUse;
		if(this.boundUnresolvedAttributeConflictResolverAverageUse != 0) this.boundUnresolvedAttributeConflictResolverAverageUse = (this.boundUnresolvedAttributeConflictResolverAverageUse + boundUnresolvedAttributeConflictResolverAverageUse)/2;
		else this.boundUnresolvedAttributeConflictResolverAverageUse = boundUnresolvedAttributeConflictResolverAverageUse;
		//System.out.println("boundUnresolvedAttributeConflictResolver "+this.boundUnresolvedAttributeConflictResolverAverageUse);		
		
	}
	
	public synchronized ActiveModelConflictHandlerPool getActiveModelConflictHandlerPool(){
		if(vchPoolFree == 0){
			ActiveModelConflictHandlerPool vchp = new ActiveModelConflictHandlerPool(instance, debugWriter);			
			return vchp;
		}else{
			ActiveModelConflictHandlerPool vchp = vchPools[--vchPoolFree];
			return vchp;
		}
	}
		
	public synchronized void recycle(ActiveModelConflictHandlerPool vchp){
		if(vchPoolFree == vchPoolPoolSize){
			ActiveModelConflictHandlerPool[] increased = new ActiveModelConflictHandlerPool[++vchPoolPoolSize];
			System.arraycopy(vchPools, 0, increased, 0, vchPoolFree);
			vchPools = increased;
		}
		vchPools[vchPoolFree++] = vchp;
	}	
} 