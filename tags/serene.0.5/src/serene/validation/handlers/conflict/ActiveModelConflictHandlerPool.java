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

import org.xml.sax.Locator;

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

import serene.bind.Queue;
import serene.bind.AttributeBinder;

import serene.Reusable;

import sereneWrite.MessageWriter;

public class ActiveModelConflictHandlerPool implements Reusable{
	ConflictHandlerPool pool;
	ValidationItemLocator validationItemLocator;
	
	int internalConflictResolverCreated = 0;
	int internalConflictResolverPoolSize;
	int internalConflictResolverFree = 0;
	InternalConflictResolver[] internalConflictResolver;
	
	int boundInternalConflictResolverCreated = 0;
	int boundInternalConflictResolverPoolSize;
	int boundInternalConflictResolverFree = 0;
	BoundInternalConflictResolver[] boundInternalConflictResolver;
	
	
	int contextConflictResolverCreated = 0;
	int contextConflictResolverPoolSize;
	int contextConflictResolverFree = 0;
	ContextConflictResolver[] contextConflictResolver;
	
	/*int internalConflictsDescriptorCreated = 0;
	int internalConflictsDescriptorPoolSize;
	int internalConflictsDescriptorFree = 0;
	InternalConflictsDescriptor[] internalConflictsDescriptor;*/
	
	MessageWriter debugWriter;	
	
	
	public ActiveModelConflictHandlerPool(ConflictHandlerPool pool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;	
		this.pool = pool;		
	}

	public void recycle(){
		if(internalConflictResolverFree != 0)releaseHandlers();
		pool.recycle(this);
	}	
	public void fill(ValidationItemLocator validationItemLocator){
		this.validationItemLocator = validationItemLocator;
		
		pool.fill(this,
				internalConflictResolver,
				boundInternalConflictResolver,
				contextConflictResolver/*,
				internalConflictsDescriptor*/);
	}
	
	void setHandlers(int internalConflictResolverFree,
				InternalConflictResolver[] internalConflictResolver,
				int boundInternalConflictResolverFree,
				BoundInternalConflictResolver[] boundInternalConflictResolver,
				int contextConflictResolverFree,
				ContextConflictResolver[] contextConflictResolver/*,
				int internalConflictsDescriptorFree,
				InternalConflictsDescriptor[] internalConflictsDescriptor*/){
		internalConflictResolverPoolSize = internalConflictResolver.length;
		this.internalConflictResolverFree = internalConflictResolverFree;
		this.internalConflictResolver = internalConflictResolver;
		for(int i = 0; i < internalConflictResolverFree; i++){	
			internalConflictResolver[i].init(this, validationItemLocator);
		}
		
		boundInternalConflictResolverPoolSize = boundInternalConflictResolver.length;
		this.boundInternalConflictResolverFree = boundInternalConflictResolverFree;
		this.boundInternalConflictResolver = boundInternalConflictResolver;
		for(int i = 0; i < boundInternalConflictResolverFree; i++){	
			boundInternalConflictResolver[i].init(this, validationItemLocator);
		}
		
		contextConflictResolverPoolSize = contextConflictResolver.length;
		this.contextConflictResolverFree = contextConflictResolverFree;
		this.contextConflictResolver = contextConflictResolver;
		for(int i = 0; i < contextConflictResolverFree; i++){	
			contextConflictResolver[i].init(this);
		}
		
		/*internalConflictsDescriptorPoolSize = internalConflictsDescriptor.length;
		this.internalConflictsDescriptorFree = internalConflictsDescriptorFree;
		this.internalConflictsDescriptor = internalConflictsDescriptor;
		for(int i = 0; i < internalConflictsDescriptorFree; i++){	
			internalConflictsDescriptor[i].init(this);
		}*/
	}
	
	public void releaseHandlers(){
		// System.out.println("internalConflictResolver created "+internalConflictResolverCreated);
		// System.out.println(internalConflictResolver.length+" "+Arrays.toString(internalConflictResolver));
		// System.out.println("contextConflictResolver created "+contextConflictResolverCreated);
		// System.out.println(contextConflictResolver.length+" "+Arrays.toString(contextConflictResolver));
		// System.out.println("internalConflictsDescriptor created "+internalConflictsDescriptorCreated);
		// System.out.println(internalConflictsDescriptor.length+" "+Arrays.toString(internalConflictsDescriptor));
		
		pool.recycle(internalConflictResolverFree,
				internalConflictResolver,
				boundInternalConflictResolverFree,
				boundInternalConflictResolver,
				contextConflictResolverFree,
				contextConflictResolver/*,
				internalConflictsDescriptorFree,
				internalConflictsDescriptor*/);
		
		internalConflictResolverFree = 0;
		boundInternalConflictResolverFree = 0;
		contextConflictResolverFree = 0;/*
		internalConflictsDescriptorFree = 0;*/
	}
	
	public InternalConflictResolver getInternalConflictResolver(){				
		if(internalConflictResolverFree == 0){
			// internalConflictResolverCreated++;
			InternalConflictResolver icr = new InternalConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init();			
			return icr;			
		}else{
			InternalConflictResolver icr = internalConflictResolver[--internalConflictResolverFree];
			icr.init();
			return icr;
		}		
	}
		
	public void recycle(InternalConflictResolver icr){	
		if(internalConflictResolverFree == internalConflictResolverPoolSize){
			InternalConflictResolver[] increased = new InternalConflictResolver[++internalConflictResolverPoolSize];
			System.arraycopy(internalConflictResolver, 0, increased, 0, internalConflictResolverFree);
			internalConflictResolver = increased;
		}
		internalConflictResolver[internalConflictResolverFree++] = icr;
	}
	
	public BoundInternalConflictResolver getBoundInternalConflictResolver(Queue targetQueue,
																		int targetEntry,
																		Map<AElement, Queue> candidateQueues){				
		if(boundInternalConflictResolverFree == 0){
			// boundInternalConflictResolverCreated++;
			BoundInternalConflictResolver icr = new BoundInternalConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(targetQueue,
					targetEntry,
					candidateQueues);			
			return icr;			
		}else{
			BoundInternalConflictResolver icr = boundInternalConflictResolver[--boundInternalConflictResolverFree];
			icr.init(targetQueue,
					targetEntry,
					candidateQueues);
			return icr;
		}		
	}
	
	public BoundInternalConflictResolver getBoundInternalConflictResolver(String value, 
																	Queue queue, 
																	int entry, 
																	Map<AAttribute, AttributeBinder> attributeBinders){				
		if(boundInternalConflictResolverFree == 0){
			// boundInternalConflictResolverCreated++;
			BoundInternalConflictResolver icr = new BoundInternalConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(value, 
					queue, 
					entry, 
					attributeBinders);			
			return icr;			
		}else{
			BoundInternalConflictResolver icr = boundInternalConflictResolver[--boundInternalConflictResolverFree];
			icr.init(value, 
					queue, 
					entry, 
					attributeBinders);
			return icr;
		}		
	}
		
	public void recycle(BoundInternalConflictResolver icr){		
		if(boundInternalConflictResolverFree == boundInternalConflictResolverPoolSize){
			BoundInternalConflictResolver[] increased = new BoundInternalConflictResolver[++boundInternalConflictResolverPoolSize];
			System.arraycopy(boundInternalConflictResolver, 0, increased, 0, boundInternalConflictResolverFree);
			boundInternalConflictResolver = increased;
		}
		boundInternalConflictResolver[boundInternalConflictResolverFree++] = icr;
	}
	
	public ContextConflictResolver getContextConflictResolver(){				
		if(contextConflictResolverFree == 0){
			// contextConflictResolverCreated++;
			ContextConflictResolver icr = new ContextConflictResolver(debugWriter);
			icr.init(this);			
			return icr;			
		}else{
			ContextConflictResolver icr = contextConflictResolver[--contextConflictResolverFree];			
			return icr;
		}		
	}
	
	public void recycle(ContextConflictResolver icr){		
		if(contextConflictResolverFree == contextConflictResolverPoolSize){
			ContextConflictResolver[] increased = new ContextConflictResolver[++contextConflictResolverPoolSize];
			System.arraycopy(contextConflictResolver, 0, increased, 0, contextConflictResolverFree);
			contextConflictResolver = increased;
		}
		contextConflictResolver[contextConflictResolverFree++] = icr;
	}
	/*
	public InternalConflictsDescriptor getInternalConflictsDescriptor(){				
		if(internalConflictsDescriptorFree == 0){
			// internalConflictsDescriptorCreated++;
			InternalConflictsDescriptor cpc = new InternalConflictsDescriptor(debugWriter);
			cpc.init(this);
			return cpc;			
		}else{
			InternalConflictsDescriptor cpc = internalConflictsDescriptor[--internalConflictsDescriptorFree];			
			return cpc;
		}		
	}
	
	public InternalConflictsDescriptor getInternalConflictsDescriptor(Set<ActiveTypeItem> conflictPatterns, Set<Rule> conflictPathRules){				
		if(internalConflictsDescriptorFree == 0){
			// internalConflictsDescriptorCreated++;
			InternalConflictsDescriptor cpc = new InternalConflictsDescriptor(debugWriter);
			cpc.init(this);
			cpc.init(conflictPatterns, conflictPathRules);
			return cpc;			
		}else{
			InternalConflictsDescriptor cpc = internalConflictsDescriptor[--internalConflictsDescriptorFree];
			cpc.init(conflictPatterns, conflictPathRules);			
			return cpc;
		}		
	}
	
	public void recycle(InternalConflictsDescriptor icr){		
		if(internalConflictsDescriptorFree == internalConflictsDescriptorPoolSize){
			InternalConflictsDescriptor[] increased = new InternalConflictsDescriptor[++internalConflictsDescriptorPoolSize];
			System.arraycopy(internalConflictsDescriptor, 0, increased, 0, internalConflictsDescriptorFree);
			internalConflictsDescriptor = increased;
		}
		internalConflictsDescriptor[internalConflictsDescriptorFree++] = icr;
	}*/
}