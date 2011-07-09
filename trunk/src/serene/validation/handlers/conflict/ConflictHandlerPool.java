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
	
	int internalConflictResolverAverageUse;
	int internalConflictResolverPoolSize;
	int internalConflictResolverFree;
	InternalConflictResolver[] internalConflictResolver;
    
    
    int listTokenConflictResolverAverageUse;
	int listTokenConflictResolverPoolSize;
	int listTokenConflictResolverFree;
	ListTokenConflictResolver[] listTokenConflictResolver;
	
	int boundInternalConflictResolverAverageUse;
	int boundInternalConflictResolverPoolSize;
	int boundInternalConflictResolverFree;
	BoundInternalConflictResolver[] boundInternalConflictResolver;
	
	int contextConflictResolverAverageUse;
	int contextConflictResolverPoolSize;
	int contextConflictResolverFree;
	ContextConflictResolver[] contextConflictResolver;
		
	final int UNUSED = 0;
	
	MessageWriter debugWriter;
	
	private ConflictHandlerPool(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		vchPoolFree = 0;
		vchPoolPoolSize = 10;
		vchPools = new ActiveModelConflictHandlerPool[vchPoolPoolSize];
		
		internalConflictResolverAverageUse = UNUSED;
		internalConflictResolverPoolSize = 10;
		internalConflictResolverFree = 0;
		internalConflictResolver = new InternalConflictResolver[internalConflictResolverPoolSize];
        
        listTokenConflictResolverAverageUse = UNUSED;
		listTokenConflictResolverPoolSize = 10;
		listTokenConflictResolverFree = 0;
		listTokenConflictResolver = new ListTokenConflictResolver[listTokenConflictResolverPoolSize];
		
		boundInternalConflictResolverAverageUse = UNUSED;
		boundInternalConflictResolverPoolSize = 10;
		boundInternalConflictResolverFree = 0;
		boundInternalConflictResolver = new BoundInternalConflictResolver[boundInternalConflictResolverPoolSize];
		
		contextConflictResolverAverageUse = UNUSED;
		contextConflictResolverPoolSize = 10;
		contextConflictResolverFree = 0;
		contextConflictResolver = new ContextConflictResolver[contextConflictResolverPoolSize];
		
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
					InternalConflictResolver[] internalConflictResolver,
                    ListTokenConflictResolver[] listTokenConflictResolver,
					BoundInternalConflictResolver[] boundInternalConflictResolver,
					ContextConflictResolver[] contextConflictResolver){
		int internalConflictResolverFillCount;
		if(internalConflictResolver == null || internalConflictResolver.length < internalConflictResolverAverageUse)
			internalConflictResolver = new InternalConflictResolver[internalConflictResolverAverageUse];
		if(internalConflictResolverFree > internalConflictResolverAverageUse){
			internalConflictResolverFillCount = internalConflictResolverAverageUse;
			internalConflictResolverFree = internalConflictResolverFree - internalConflictResolverAverageUse;
		}else{
			internalConflictResolverFillCount = internalConflictResolverFree;
			internalConflictResolverFree = 0;
		}		
		System.arraycopy(this.internalConflictResolver, internalConflictResolverFree, 
							internalConflictResolver, 0, internalConflictResolverFillCount);
        
        
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
		
		int boundInternalConflictResolverFillCount;
		if(boundInternalConflictResolver == null || boundInternalConflictResolver.length < boundInternalConflictResolverAverageUse)
			boundInternalConflictResolver = new BoundInternalConflictResolver[boundInternalConflictResolverAverageUse];
		if(boundInternalConflictResolverFree > boundInternalConflictResolverAverageUse){
			boundInternalConflictResolverFillCount = boundInternalConflictResolverAverageUse;
			boundInternalConflictResolverFree = boundInternalConflictResolverFree - boundInternalConflictResolverAverageUse;
		}else{
			boundInternalConflictResolverFillCount = boundInternalConflictResolverFree;
			boundInternalConflictResolverFree = 0;
		}		
		System.arraycopy(this.boundInternalConflictResolver, boundInternalConflictResolverFree, 
							boundInternalConflictResolver, 0, boundInternalConflictResolverFillCount);
		
		int contextConflictResolverFillCount;
		if(contextConflictResolver == null || contextConflictResolver.length < contextConflictResolverAverageUse)
			contextConflictResolver = new ContextConflictResolver[contextConflictResolverAverageUse];
		if(contextConflictResolverFree > contextConflictResolverAverageUse){
			contextConflictResolverFillCount = contextConflictResolverAverageUse;
			contextConflictResolverFree = contextConflictResolverFree - contextConflictResolverAverageUse;
		}else{
			contextConflictResolverFillCount = contextConflictResolverFree;
			contextConflictResolverFree = 0;
		}		
		System.arraycopy(this.contextConflictResolver, contextConflictResolverFree, 
							contextConflictResolver, 0, contextConflictResolverFillCount);
		
				
		pool.setHandlers(internalConflictResolverFillCount,
							internalConflictResolver,
                            listTokenConflictResolverFillCount,
							listTokenConflictResolver,
							boundInternalConflictResolverFillCount,
							boundInternalConflictResolver,
							contextConflictResolverFillCount,
							contextConflictResolver);
	}
	
	synchronized void recycle(int internalConflictResolverAverageUse,
						InternalConflictResolver[] internalConflictResolver,
                        int listTokenConflictResolverAverageUse,
						ListTokenConflictResolver[] listTokenConflictResolver,
						int boundInternalConflictResolverAverageUse,
						BoundInternalConflictResolver[] boundInternalConflictResolver,
						int contextConflictResolverAverageUse,
						ContextConflictResolver[] contextConflictResolver){
		if(internalConflictResolverFree + internalConflictResolverAverageUse >= internalConflictResolverPoolSize){			 
			internalConflictResolverPoolSize+= internalConflictResolverAverageUse;
			InternalConflictResolver[] increased = new InternalConflictResolver[internalConflictResolverPoolSize];
			System.arraycopy(this.internalConflictResolver, 0, increased, 0, internalConflictResolverFree);
			this.internalConflictResolver = increased;
		}
		System.arraycopy(internalConflictResolver, 0, this.internalConflictResolver, internalConflictResolverFree, internalConflictResolverAverageUse);
		internalConflictResolverFree += internalConflictResolverAverageUse;
		if(this.internalConflictResolverAverageUse != 0) this.internalConflictResolverAverageUse = (this.internalConflictResolverAverageUse + internalConflictResolverAverageUse)/2;
		else this.internalConflictResolverAverageUse = internalConflictResolverAverageUse;
		//System.out.println("internalConflictResolver "+this.internalConflictResolverAverageUse);
        
        
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
		
		
		if(boundInternalConflictResolverFree + boundInternalConflictResolverAverageUse >= boundInternalConflictResolverPoolSize){			 
			boundInternalConflictResolverPoolSize+= boundInternalConflictResolverAverageUse;
			BoundInternalConflictResolver[] increased = new BoundInternalConflictResolver[boundInternalConflictResolverPoolSize];
			System.arraycopy(this.boundInternalConflictResolver, 0, increased, 0, boundInternalConflictResolverFree);
			this.boundInternalConflictResolver = increased;
		}
		System.arraycopy(boundInternalConflictResolver, 0, this.boundInternalConflictResolver, boundInternalConflictResolverFree, boundInternalConflictResolverAverageUse);
		boundInternalConflictResolverFree += boundInternalConflictResolverAverageUse;
		if(this.boundInternalConflictResolverAverageUse != 0) this.boundInternalConflictResolverAverageUse = (this.boundInternalConflictResolverAverageUse + boundInternalConflictResolverAverageUse)/2;
		else this.boundInternalConflictResolverAverageUse = boundInternalConflictResolverAverageUse;
		//System.out.println("boundInternalConflictResolver "+this.boundInternalConflictResolverAverageUse);
		
		
		if(contextConflictResolverFree + contextConflictResolverAverageUse >= contextConflictResolverPoolSize){			 
			contextConflictResolverPoolSize+= contextConflictResolverAverageUse;
			ContextConflictResolver[] increased = new ContextConflictResolver[contextConflictResolverPoolSize];
			System.arraycopy(this.contextConflictResolver, 0, increased, 0, contextConflictResolverFree);
			this.contextConflictResolver = increased;
		}
		System.arraycopy(contextConflictResolver, 0, this.contextConflictResolver, contextConflictResolverFree, contextConflictResolverAverageUse);
		contextConflictResolverFree += contextConflictResolverAverageUse;
		if(this.contextConflictResolverAverageUse != 0) this.contextConflictResolverAverageUse = (this.contextConflictResolverAverageUse + contextConflictResolverAverageUse)/2;
		else this.contextConflictResolverAverageUse = contextConflictResolverAverageUse;
		//System.out.println("contextConflictResolver "+this.contextConflictResolverAverageUse);		
		
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