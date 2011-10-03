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
    
	int attributeConflictResolverAverageUse;
	int attributeConflictResolverPoolSize;
	int attributeConflictResolverFree;
	AttributeConflictResolver[] attributeConflictResolver;
	
	int charsConflictResolverAverageUse;
	int charsConflictResolverPoolSize;
	int charsConflictResolverFree;
	CharsConflictResolver[] charsConflictResolver;
    
    
    int listTokenConflictResolverAverageUse;
	int listTokenConflictResolverPoolSize;
	int listTokenConflictResolverFree;
	ListTokenConflictResolver[] listTokenConflictResolver;
	
	int boundAmbiguousElementConflictResolverAverageUse;
	int boundAmbiguousElementConflictResolverPoolSize;
	int boundAmbiguousElementConflictResolverFree;
	BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver;
	
	int boundUnresolvedElementConflictResolverAverageUse;
	int boundUnresolvedElementConflictResolverPoolSize;
	int boundUnresolvedElementConflictResolverFree;
	BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver;
	
	
	int boundAttributeConflictResolverAverageUse;
	int boundAttributeConflictResolverPoolSize;
	int boundAttributeConflictResolverFree;
	BoundAttributeConflictResolver[] boundAttributeConflictResolver;
		
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
		
		attributeConflictResolverAverageUse = UNUSED;
		attributeConflictResolverPoolSize = 10;
		attributeConflictResolverFree = 0;
		attributeConflictResolver = new AttributeConflictResolver[attributeConflictResolverPoolSize];
        
		charsConflictResolverAverageUse = UNUSED;
		charsConflictResolverPoolSize = 10;
		charsConflictResolverFree = 0;
		charsConflictResolver = new CharsConflictResolver[charsConflictResolverPoolSize];
        		
		
        listTokenConflictResolverAverageUse = UNUSED;
		listTokenConflictResolverPoolSize = 10;
		listTokenConflictResolverFree = 0;
		listTokenConflictResolver = new ListTokenConflictResolver[listTokenConflictResolverPoolSize];
		
		boundAmbiguousElementConflictResolverAverageUse = UNUSED;
		boundAmbiguousElementConflictResolverPoolSize = 10;
		boundAmbiguousElementConflictResolverFree = 0;
		boundAmbiguousElementConflictResolver = new BoundAmbiguousElementConflictResolver[boundAmbiguousElementConflictResolverPoolSize];
		
		boundUnresolvedElementConflictResolverAverageUse = UNUSED;
		boundUnresolvedElementConflictResolverPoolSize = 10;
		boundUnresolvedElementConflictResolverFree = 0;
		boundUnresolvedElementConflictResolver = new BoundUnresolvedElementConflictResolver[boundUnresolvedElementConflictResolverPoolSize];
		
		boundAttributeConflictResolverAverageUse = UNUSED;
		boundAttributeConflictResolverPoolSize = 10;
		boundAttributeConflictResolverFree = 0;
		boundAttributeConflictResolver = new BoundAttributeConflictResolver[boundAttributeConflictResolverPoolSize];
		
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
					AttributeConflictResolver[] attributeConflictResolver,
					CharsConflictResolver[] charsConflictResolver,
                    ListTokenConflictResolver[] listTokenConflictResolver,
					BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver,
					BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver,
					BoundAttributeConflictResolver[] boundAttributeConflictResolver){
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
        
		
		int attributeConflictResolverFillCount;
		if(attributeConflictResolver == null || attributeConflictResolver.length < attributeConflictResolverAverageUse)
			attributeConflictResolver = new AttributeConflictResolver[attributeConflictResolverAverageUse];
		if(attributeConflictResolverFree > attributeConflictResolverAverageUse){
			attributeConflictResolverFillCount = attributeConflictResolverAverageUse;
			attributeConflictResolverFree = attributeConflictResolverFree - attributeConflictResolverAverageUse;
		}else{
			attributeConflictResolverFillCount = attributeConflictResolverFree;
			attributeConflictResolverFree = 0;
		}		
		System.arraycopy(this.attributeConflictResolver, attributeConflictResolverFree, 
							attributeConflictResolver, 0, attributeConflictResolverFillCount);
        
		int charsConflictResolverFillCount;
		if(charsConflictResolver == null || charsConflictResolver.length < charsConflictResolverAverageUse)
			charsConflictResolver = new CharsConflictResolver[charsConflictResolverAverageUse];
		if(charsConflictResolverFree > charsConflictResolverAverageUse){
			charsConflictResolverFillCount = charsConflictResolverAverageUse;
			charsConflictResolverFree = charsConflictResolverFree - charsConflictResolverAverageUse;
		}else{
			charsConflictResolverFillCount = charsConflictResolverFree;
			charsConflictResolverFree = 0;
		}		
		System.arraycopy(this.charsConflictResolver, charsConflictResolverFree, 
							charsConflictResolver, 0, charsConflictResolverFillCount);
        
		
        int listTokenConflictResolverFillCount;
		if(listTokenConflictResolver == null || listTokenConflictResolver.length < listTokenConflictResolverAverageUse)
			listTokenConflictResolver = new ListTokenConflictResolver[listTokenConflictResolverAverageUse];
		if(listTokenConflictResolverFree > listTokenConflictResolverAverageUse){
			listTokenConflictResolverFillCount = listTokenConflictResolverAverageUse;
			listTokenConflictResolverFree = listTokenConflictResolverFree - listTokenConflictResolverAverageUse;
		}else{
			listTokenConflictResolverFillCount = listTokenConflictResolverFree;
			listTokenConflictResolverFree = 0;
		}		
		System.arraycopy(this.listTokenConflictResolver, listTokenConflictResolverFree, 
							listTokenConflictResolver, 0, listTokenConflictResolverFillCount);
		
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
		
		
		int boundAttributeConflictResolverFillCount;
		if(boundAttributeConflictResolver == null || boundAttributeConflictResolver.length < boundAttributeConflictResolverAverageUse)
			boundAttributeConflictResolver = new BoundAttributeConflictResolver[boundAttributeConflictResolverAverageUse];
		if(boundAttributeConflictResolverFree > boundAttributeConflictResolverAverageUse){
			boundAttributeConflictResolverFillCount = boundAttributeConflictResolverAverageUse;
			boundAttributeConflictResolverFree = boundAttributeConflictResolverFree - boundAttributeConflictResolverAverageUse;
		}else{
			boundAttributeConflictResolverFillCount = boundAttributeConflictResolverFree;
			boundAttributeConflictResolverFree = 0;
		}		
		System.arraycopy(this.boundAttributeConflictResolver, boundAttributeConflictResolverFree, 
							boundAttributeConflictResolver, 0, boundAttributeConflictResolverFillCount);
		
				
		pool.setHandlers(ambiguousElementConflictResolverFillCount,
							ambiguousElementConflictResolver,
							unresolvedElementConflictResolverFillCount,
							unresolvedElementConflictResolver,
							attributeConflictResolverFillCount,
							attributeConflictResolver,
							charsConflictResolverFillCount,
							charsConflictResolver,
                            listTokenConflictResolverFillCount,
							listTokenConflictResolver,
							boundAmbiguousElementConflictResolverFillCount,
							boundAmbiguousElementConflictResolver,
							boundUnresolvedElementConflictResolverFillCount,
							boundUnresolvedElementConflictResolver,
							boundAttributeConflictResolverFillCount,
							boundAttributeConflictResolver);
	}
	
	synchronized void recycle(int ambiguousElementConflictResolverAverageUse,
						AmbiguousElementConflictResolver[] ambiguousElementConflictResolver,
						int unresolvedElementConflictResolverAverageUse,
						UnresolvedElementConflictResolver[] unresolvedElementConflictResolver,
						int attributeConflictResolverAverageUse,
						AttributeConflictResolver[] attributeConflictResolver,
						int charsConflictResolverAverageUse,
						CharsConflictResolver[] charsConflictResolver,
                        int listTokenConflictResolverAverageUse,
						ListTokenConflictResolver[] listTokenConflictResolver,
						int boundAmbiguousElementConflictResolverAverageUse,
						BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver,
						int boundUnresolvedElementConflictResolverAverageUse,
						BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver,
						int boundAttributeConflictResolverAverageUse,
						BoundAttributeConflictResolver[] boundAttributeConflictResolver){
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
        
		if(attributeConflictResolverFree + attributeConflictResolverAverageUse >= attributeConflictResolverPoolSize){			 
			attributeConflictResolverPoolSize+= attributeConflictResolverAverageUse;
			AttributeConflictResolver[] increased = new AttributeConflictResolver[attributeConflictResolverPoolSize];
			System.arraycopy(this.attributeConflictResolver, 0, increased, 0, attributeConflictResolverFree);
			this.attributeConflictResolver = increased;
		}
		System.arraycopy(attributeConflictResolver, 0, this.attributeConflictResolver, attributeConflictResolverFree, attributeConflictResolverAverageUse);
		attributeConflictResolverFree += attributeConflictResolverAverageUse;
		if(this.attributeConflictResolverAverageUse != 0) this.attributeConflictResolverAverageUse = (this.attributeConflictResolverAverageUse + attributeConflictResolverAverageUse)/2;
		else this.attributeConflictResolverAverageUse = attributeConflictResolverAverageUse;
		//System.out.println("attributeConflictResolver "+this.attributeConflictResolverAverageUse);
        
		if(charsConflictResolverFree + charsConflictResolverAverageUse >= charsConflictResolverPoolSize){			 
			charsConflictResolverPoolSize+= charsConflictResolverAverageUse;
			CharsConflictResolver[] increased = new CharsConflictResolver[charsConflictResolverPoolSize];
			System.arraycopy(this.charsConflictResolver, 0, increased, 0, charsConflictResolverFree);
			this.charsConflictResolver = increased;
		}
		System.arraycopy(charsConflictResolver, 0, this.charsConflictResolver, charsConflictResolverFree, charsConflictResolverAverageUse);
		charsConflictResolverFree += charsConflictResolverAverageUse;
		if(this.charsConflictResolverAverageUse != 0) this.charsConflictResolverAverageUse = (this.charsConflictResolverAverageUse + charsConflictResolverAverageUse)/2;
		else this.charsConflictResolverAverageUse = charsConflictResolverAverageUse;
		//System.out.println("charsConflictResolver "+this.charsConflictResolverAverageUse);
        
        if(listTokenConflictResolverFree + listTokenConflictResolverAverageUse >= listTokenConflictResolverPoolSize){			 
			listTokenConflictResolverPoolSize+= listTokenConflictResolverAverageUse;
			ListTokenConflictResolver[] increased = new ListTokenConflictResolver[listTokenConflictResolverPoolSize];
			System.arraycopy(this.listTokenConflictResolver, 0, increased, 0, listTokenConflictResolverFree);
			this.listTokenConflictResolver = increased;
		}
		System.arraycopy(listTokenConflictResolver, 0, this.listTokenConflictResolver, listTokenConflictResolverFree, listTokenConflictResolverAverageUse);
		listTokenConflictResolverFree += listTokenConflictResolverAverageUse;
		if(this.listTokenConflictResolverAverageUse != 0) this.listTokenConflictResolverAverageUse = (this.listTokenConflictResolverAverageUse + listTokenConflictResolverAverageUse)/2;
		else this.listTokenConflictResolverAverageUse = listTokenConflictResolverAverageUse;
		//System.out.println("listTokenConflictResolver "+this.listTokenConflictResolverAverageUse);
		
		
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
		
		
		if(boundAttributeConflictResolverFree + boundAttributeConflictResolverAverageUse >= boundAttributeConflictResolverPoolSize){			 
			boundAttributeConflictResolverPoolSize+= boundAttributeConflictResolverAverageUse;
			BoundAttributeConflictResolver[] increased = new BoundAttributeConflictResolver[boundAttributeConflictResolverPoolSize];
			System.arraycopy(this.boundAttributeConflictResolver, 0, increased, 0, boundAttributeConflictResolverFree);
			this.boundAttributeConflictResolver = increased;
		}
		System.arraycopy(boundAttributeConflictResolver, 0, this.boundAttributeConflictResolver, boundAttributeConflictResolverFree, boundAttributeConflictResolverAverageUse);
		boundAttributeConflictResolverFree += boundAttributeConflictResolverAverageUse;
		if(this.boundAttributeConflictResolverAverageUse != 0) this.boundAttributeConflictResolverAverageUse = (this.boundAttributeConflictResolverAverageUse + boundAttributeConflictResolverAverageUse)/2;
		else this.boundAttributeConflictResolverAverageUse = boundAttributeConflictResolverAverageUse;
		//System.out.println("boundAttributeConflictResolver "+this.boundAttributeConflictResolverAverageUse);		
		
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