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

import serene.validation.handlers.error.ConflictMessageReporter;

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
	
	int ambiguousElementConflictResolverCreated = 0;
	int ambiguousElementConflictResolverPoolSize;
	int ambiguousElementConflictResolverFree = 0;
	AmbiguousElementConflictResolver[] ambiguousElementConflictResolver;
	
	int unresolvedElementConflictResolverCreated = 0;
	int unresolvedElementConflictResolverPoolSize;
	int unresolvedElementConflictResolverFree = 0;
	UnresolvedElementConflictResolver[] unresolvedElementConflictResolver;
	
	
	int ambiguousAttributeConflictResolverCreated = 0;
	int ambiguousAttributeConflictResolverPoolSize;
	int ambiguousAttributeConflictResolverFree = 0;
	AmbiguousAttributeConflictResolver[] ambiguousAttributeConflictResolver;
	
	int unresolvedAttributeConflictResolverCreated = 0;
	int unresolvedAttributeConflictResolverPoolSize;
	int unresolvedAttributeConflictResolverFree = 0;
	UnresolvedAttributeConflictResolver[] unresolvedAttributeConflictResolver;
	
	
	int charsConflictResolverCreated = 0;
	int charsConflictResolverPoolSize;
	int charsConflictResolverFree = 0;
	CharsConflictResolver[] charsConflictResolver;
    
    int listTokenConflictResolverCreated = 0;
	int listTokenConflictResolverPoolSize;
	int listTokenConflictResolverFree = 0;
	ListTokenConflictResolver[] listTokenConflictResolver;
	
	int boundAmbiguousElementConflictResolverCreated = 0;
	int boundAmbiguousElementConflictResolverPoolSize;
	int boundAmbiguousElementConflictResolverFree = 0;
	BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver;
	
	int boundUnresolvedElementConflictResolverCreated = 0;
	int boundUnresolvedElementConflictResolverPoolSize;
	int boundUnresolvedElementConflictResolverFree = 0;
	BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver;
	
	
	int boundAmbiguousAttributeConflictResolverCreated = 0;
	int boundAmbiguousAttributeConflictResolverPoolSize;
	int boundAmbiguousAttributeConflictResolverFree = 0;
	BoundAmbiguousAttributeConflictResolver[] boundAmbiguousAttributeConflictResolver;
	
	
	int boundUnresolvedAttributeConflictResolverCreated = 0;
	int boundUnresolvedAttributeConflictResolverPoolSize;
	int boundUnresolvedAttributeConflictResolverFree = 0;
	BoundUnresolvedAttributeConflictResolver[] boundUnresolvedAttributeConflictResolver;
	
	
	MessageWriter debugWriter;	
	
	
	public ActiveModelConflictHandlerPool(ConflictHandlerPool pool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;	
		this.pool = pool;		
	}

	public void recycle(){
		if(ambiguousElementConflictResolverFree != 0
		       || unresolvedElementConflictResolverFree != 0
               || ambiguousAttributeConflictResolverFree != 0
               || unresolvedAttributeConflictResolverFree != 0
               || charsConflictResolverFree != 0
               || listTokenConflictResolverFree != 0
               || boundAmbiguousElementConflictResolverFree != 0
               || boundUnresolvedElementConflictResolverFree != 0
               || boundAmbiguousAttributeConflictResolverFree != 0
               || boundUnresolvedAttributeConflictResolverFree != 0)releaseHandlers();
		pool.recycle(this);
	}	
	public void fill(ValidationItemLocator validationItemLocator){
		this.validationItemLocator = validationItemLocator;
		pool.fill(this,
				ambiguousElementConflictResolver,
				unresolvedElementConflictResolver,
				ambiguousAttributeConflictResolver,
				unresolvedAttributeConflictResolver,
				charsConflictResolver,
				listTokenConflictResolver,
				boundAmbiguousElementConflictResolver,
				boundUnresolvedElementConflictResolver,
				boundAmbiguousAttributeConflictResolver,
				boundUnresolvedAttributeConflictResolver);
	}
	
	void setHandlers(int ambiguousElementConflictResolverFree,
				AmbiguousElementConflictResolver[] ambiguousElementConflictResolver,
				int unresolvedElementConflictResolverFree,
				UnresolvedElementConflictResolver[] unresolvedElementConflictResolver,
				int ambiguousAttributeConflictResolverFree,
				AmbiguousAttributeConflictResolver[] ambiguousAttributeConflictResolver,
				int unresolvedAttributeConflictResolverFree,
				UnresolvedAttributeConflictResolver[] unresolvedAttributeConflictResolver,
				int charsConflictResolverFree,
				CharsConflictResolver[] charsConflictResolver,
                int listTokenConflictResolverFree,
				ListTokenConflictResolver[] listTokenConflictResolver,
				int boundAmbiguousElementConflictResolverFree,
				BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolver,
				int boundUnresolvedElementConflictResolverFree,
				BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolver,
				int boundAmbiguousAttributeConflictResolverFree,
				BoundAmbiguousAttributeConflictResolver[] boundAmbiguousAttributeConflictResolver,
				int boundUnresolvedAttributeConflictResolverFree,
				BoundUnresolvedAttributeConflictResolver[] boundUnresolvedAttributeConflictResolver){
		ambiguousElementConflictResolverPoolSize = ambiguousElementConflictResolver.length;
		this.ambiguousElementConflictResolverFree = ambiguousElementConflictResolverFree;
		this.ambiguousElementConflictResolver = ambiguousElementConflictResolver;
		for(int i = 0; i < ambiguousElementConflictResolverFree; i++){	
			ambiguousElementConflictResolver[i].init(this, validationItemLocator);
		}
		
		unresolvedElementConflictResolverPoolSize = unresolvedElementConflictResolver.length;
		this.unresolvedElementConflictResolverFree = unresolvedElementConflictResolverFree;
		this.unresolvedElementConflictResolver = unresolvedElementConflictResolver;
		for(int i = 0; i < unresolvedElementConflictResolverFree; i++){	
			unresolvedElementConflictResolver[i].init(this, validationItemLocator);
		}
		
		ambiguousAttributeConflictResolverPoolSize = ambiguousAttributeConflictResolver.length;
		this.ambiguousAttributeConflictResolverFree = ambiguousAttributeConflictResolverFree;
		this.ambiguousAttributeConflictResolver = ambiguousAttributeConflictResolver;
		for(int i = 0; i < ambiguousAttributeConflictResolverFree; i++){	
			ambiguousAttributeConflictResolver[i].init(this, validationItemLocator);
		}
		
		unresolvedAttributeConflictResolverPoolSize = unresolvedAttributeConflictResolver.length;
		this.unresolvedAttributeConflictResolverFree = unresolvedAttributeConflictResolverFree;
		this.unresolvedAttributeConflictResolver = unresolvedAttributeConflictResolver;
		for(int i = 0; i < unresolvedAttributeConflictResolverFree; i++){	
			unresolvedAttributeConflictResolver[i].init(this, validationItemLocator);
		}
		
		charsConflictResolverPoolSize = charsConflictResolver.length;
		this.charsConflictResolverFree = charsConflictResolverFree;
		this.charsConflictResolver = charsConflictResolver;
		for(int i = 0; i < charsConflictResolverFree; i++){	
			charsConflictResolver[i].init(this, validationItemLocator);
		}
        
        listTokenConflictResolverPoolSize = listTokenConflictResolver.length;
		this.listTokenConflictResolverFree = listTokenConflictResolverFree;
		this.listTokenConflictResolver = listTokenConflictResolver;
		for(int i = 0; i < listTokenConflictResolverFree; i++){	
			listTokenConflictResolver[i].init(this, validationItemLocator);
		}
				
		boundAmbiguousElementConflictResolverPoolSize = boundAmbiguousElementConflictResolver.length;
		this.boundAmbiguousElementConflictResolverFree = boundAmbiguousElementConflictResolverFree;
		this.boundAmbiguousElementConflictResolver = boundAmbiguousElementConflictResolver;
		for(int i = 0; i < boundAmbiguousElementConflictResolverFree; i++){	
			boundAmbiguousElementConflictResolver[i].init(this, validationItemLocator);
		}
		
		boundUnresolvedElementConflictResolverPoolSize = boundUnresolvedElementConflictResolver.length;
		this.boundUnresolvedElementConflictResolverFree = boundUnresolvedElementConflictResolverFree;
		this.boundUnresolvedElementConflictResolver = boundUnresolvedElementConflictResolver;
		for(int i = 0; i < boundUnresolvedElementConflictResolverFree; i++){	
			boundUnresolvedElementConflictResolver[i].init(this, validationItemLocator);
		}
		
		boundAmbiguousAttributeConflictResolverPoolSize = boundAmbiguousAttributeConflictResolver.length;
		this.boundAmbiguousAttributeConflictResolverFree = boundAmbiguousAttributeConflictResolverFree;
		this.boundAmbiguousAttributeConflictResolver = boundAmbiguousAttributeConflictResolver;
		for(int i = 0; i < boundAmbiguousAttributeConflictResolverFree; i++){	
			boundAmbiguousAttributeConflictResolver[i].init(this, validationItemLocator);
		}
		
		boundUnresolvedAttributeConflictResolverPoolSize = boundUnresolvedAttributeConflictResolver.length;
		this.boundUnresolvedAttributeConflictResolverFree = boundUnresolvedAttributeConflictResolverFree;
		this.boundUnresolvedAttributeConflictResolver = boundUnresolvedAttributeConflictResolver;
		for(int i = 0; i < boundUnresolvedAttributeConflictResolverFree; i++){	
			boundUnresolvedAttributeConflictResolver[i].init(this, validationItemLocator);
		}
	}
	
	public void releaseHandlers(){
		pool.recycle(ambiguousElementConflictResolverFree,
				ambiguousElementConflictResolver,
				unresolvedElementConflictResolverFree,
				unresolvedElementConflictResolver,
				ambiguousAttributeConflictResolverFree,
				ambiguousAttributeConflictResolver,
				unresolvedAttributeConflictResolverFree,
				unresolvedAttributeConflictResolver,
				charsConflictResolverFree,
				charsConflictResolver,
                listTokenConflictResolverFree,
				listTokenConflictResolver,
				boundAmbiguousElementConflictResolverFree,
				boundAmbiguousElementConflictResolver,
				boundUnresolvedElementConflictResolverFree,
				boundUnresolvedElementConflictResolver,
				boundAmbiguousAttributeConflictResolverFree,
				boundAmbiguousAttributeConflictResolver,
				boundUnresolvedAttributeConflictResolverFree,
				boundUnresolvedAttributeConflictResolver);
		
		ambiguousElementConflictResolverFree = 0;
		unresolvedElementConflictResolverFree = 0;
		ambiguousAttributeConflictResolverFree = 0;
		unresolvedAttributeConflictResolverFree = 0;
		charsConflictResolverFree = 0;
        listTokenConflictResolverFree = 0;
		boundAmbiguousElementConflictResolverFree = 0;
		boundUnresolvedElementConflictResolverFree = 0;
		boundAmbiguousAttributeConflictResolverFree = 0;
		boundUnresolvedAttributeConflictResolverFree = 0;
	}
	
	public AmbiguousElementConflictResolver getAmbiguousElementConflictResolver(ConflictMessageReporter conflictMessageReporter){				
		if(ambiguousElementConflictResolverFree == 0){
			// ambiguousElementConflictResolverCreated++;
			AmbiguousElementConflictResolver icr = new AmbiguousElementConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(conflictMessageReporter);			
			return icr;			
		}else{
			AmbiguousElementConflictResolver icr = ambiguousElementConflictResolver[--ambiguousElementConflictResolverFree];
			icr.init(conflictMessageReporter);
			return icr;
		}		
	}
		
	public void recycle(AmbiguousElementConflictResolver icr){	
		if(ambiguousElementConflictResolverFree == ambiguousElementConflictResolverPoolSize){
			AmbiguousElementConflictResolver[] increased = new AmbiguousElementConflictResolver[++ambiguousElementConflictResolverPoolSize];
			System.arraycopy(ambiguousElementConflictResolver, 0, increased, 0, ambiguousElementConflictResolverFree);
			ambiguousElementConflictResolver = increased;
		}
		ambiguousElementConflictResolver[ambiguousElementConflictResolverFree++] = icr;
	}
	
	public UnresolvedElementConflictResolver getUnresolvedElementConflictResolver(ConflictMessageReporter conflictMessageReporter){				
		if(unresolvedElementConflictResolverFree == 0){
			// unresolvedElementConflictResolverCreated++;
			UnresolvedElementConflictResolver icr = new UnresolvedElementConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(conflictMessageReporter);			
			return icr;			
		}else{
			UnresolvedElementConflictResolver icr = unresolvedElementConflictResolver[--unresolvedElementConflictResolverFree];
			icr.init(conflictMessageReporter);
			return icr;
		}		
	}
		
	public void recycle(UnresolvedElementConflictResolver icr){	
		if(unresolvedElementConflictResolverFree == unresolvedElementConflictResolverPoolSize){
			UnresolvedElementConflictResolver[] increased = new UnresolvedElementConflictResolver[++unresolvedElementConflictResolverPoolSize];
			System.arraycopy(unresolvedElementConflictResolver, 0, increased, 0, unresolvedElementConflictResolverFree);
			unresolvedElementConflictResolver = increased;
		}
		unresolvedElementConflictResolver[unresolvedElementConflictResolverFree++] = icr;
	}
	
	public AmbiguousAttributeConflictResolver getAmbiguousAttributeConflictResolver(){				
		if(ambiguousAttributeConflictResolverFree == 0){
			// ambiguousAttributeConflictResolverCreated++;
			AmbiguousAttributeConflictResolver icr = new AmbiguousAttributeConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init();			
			return icr;			
		}else{
			AmbiguousAttributeConflictResolver icr = ambiguousAttributeConflictResolver[--ambiguousAttributeConflictResolverFree];
			icr.init();
			return icr;
		}		
	}
		
	public void recycle(AmbiguousAttributeConflictResolver icr){	
		if(ambiguousAttributeConflictResolverFree == ambiguousAttributeConflictResolverPoolSize){
			AmbiguousAttributeConflictResolver[] increased = new AmbiguousAttributeConflictResolver[++ambiguousAttributeConflictResolverPoolSize];
			System.arraycopy(ambiguousAttributeConflictResolver, 0, increased, 0, ambiguousAttributeConflictResolverFree);
			ambiguousAttributeConflictResolver = increased;
		}
		ambiguousAttributeConflictResolver[ambiguousAttributeConflictResolverFree++] = icr;
	}
	
	
	public UnresolvedAttributeConflictResolver getUnresolvedAttributeConflictResolver(){				
		if(unresolvedAttributeConflictResolverFree == 0){
			// unresolvedAttributeConflictResolverCreated++;
			UnresolvedAttributeConflictResolver icr = new UnresolvedAttributeConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init();			
			return icr;			
		}else{
			UnresolvedAttributeConflictResolver icr = unresolvedAttributeConflictResolver[--unresolvedAttributeConflictResolverFree];
			icr.init();
			return icr;
		}		
	}
		
	public void recycle(UnresolvedAttributeConflictResolver icr){	
		if(unresolvedAttributeConflictResolverFree == unresolvedAttributeConflictResolverPoolSize){
			UnresolvedAttributeConflictResolver[] increased = new UnresolvedAttributeConflictResolver[++unresolvedAttributeConflictResolverPoolSize];
			System.arraycopy(unresolvedAttributeConflictResolver, 0, increased, 0, unresolvedAttributeConflictResolverFree);
			unresolvedAttributeConflictResolver = increased;
		}
		unresolvedAttributeConflictResolver[unresolvedAttributeConflictResolverFree++] = icr;
	}
	
	public CharsConflictResolver getCharsConflictResolver(){				
		if(charsConflictResolverFree == 0){
			// charsConflictResolverCreated++;
			CharsConflictResolver icr = new CharsConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init();			
			return icr;			
		}else{
			CharsConflictResolver icr = charsConflictResolver[--charsConflictResolverFree];
			icr.init();
			return icr;
		}		
	}
		
	public void recycle(CharsConflictResolver icr){	
		if(charsConflictResolverFree == charsConflictResolverPoolSize){
			CharsConflictResolver[] increased = new CharsConflictResolver[++charsConflictResolverPoolSize];
			System.arraycopy(charsConflictResolver, 0, increased, 0, charsConflictResolverFree);
			charsConflictResolver = increased;
		}
		charsConflictResolver[charsConflictResolverFree++] = icr;
	}
        
    public ListTokenConflictResolver getListTokenConflictResolver(char[] token){				
		if(listTokenConflictResolverFree == 0){
			// listTokenConflictResolverCreated++;
			ListTokenConflictResolver icr = new ListTokenConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(token);			
			return icr;			
		}else{
			ListTokenConflictResolver icr = listTokenConflictResolver[--listTokenConflictResolverFree];
			icr.init(token);
			return icr;
		}		
	}
		
	public void recycle(ListTokenConflictResolver icr){	
		if(listTokenConflictResolverFree == listTokenConflictResolverPoolSize){
			ListTokenConflictResolver[] increased = new ListTokenConflictResolver[++listTokenConflictResolverPoolSize];
			System.arraycopy(listTokenConflictResolver, 0, increased, 0, listTokenConflictResolverFree);
			listTokenConflictResolver = increased;
		}
		listTokenConflictResolver[listTokenConflictResolverFree++] = icr;
	}
	
	public BoundAmbiguousElementConflictResolver getBoundAmbiguousElementConflictResolver(ConflictMessageReporter conflictMessageReporter,
	                                                                    Queue targetQueue,
																		int targetEntry,
																		Map<AElement, Queue> candidateQueues){				
		if(boundAmbiguousElementConflictResolverFree == 0){
			// boundAmbiguousElementConflictResolverCreated++;
			BoundAmbiguousElementConflictResolver icr = new BoundAmbiguousElementConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(conflictMessageReporter,
			        targetQueue,
					targetEntry,
					candidateQueues);			
			return icr;			
		}else{
			BoundAmbiguousElementConflictResolver icr = boundAmbiguousElementConflictResolver[--boundAmbiguousElementConflictResolverFree];
			icr.init(conflictMessageReporter,
			        targetQueue,
					targetEntry,
					candidateQueues);
			return icr;
		}		
	}
			
	public void recycle(BoundAmbiguousElementConflictResolver icr){		
		if(boundAmbiguousElementConflictResolverFree == boundAmbiguousElementConflictResolverPoolSize){
			BoundAmbiguousElementConflictResolver[] increased = new BoundAmbiguousElementConflictResolver[++boundAmbiguousElementConflictResolverPoolSize];
			System.arraycopy(boundAmbiguousElementConflictResolver, 0, increased, 0, boundAmbiguousElementConflictResolverFree);
			boundAmbiguousElementConflictResolver = increased;
		}
		boundAmbiguousElementConflictResolver[boundAmbiguousElementConflictResolverFree++] = icr;
	}
	
	public BoundUnresolvedElementConflictResolver getBoundUnresolvedElementConflictResolver(ConflictMessageReporter conflictMessageReporter,
	                                                                    Queue targetQueue,
																		int targetEntry,
																		Map<AElement, Queue> candidateQueues){				
		if(boundUnresolvedElementConflictResolverFree == 0){
			// boundUnresolvedElementConflictResolverCreated++;
			BoundUnresolvedElementConflictResolver icr = new BoundUnresolvedElementConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(conflictMessageReporter,
			        targetQueue,
					targetEntry,
					candidateQueues);			
			return icr;			
		}else{
			BoundUnresolvedElementConflictResolver icr = boundUnresolvedElementConflictResolver[--boundUnresolvedElementConflictResolverFree];
			icr.init(conflictMessageReporter,
			        targetQueue,
					targetEntry,
					candidateQueues);
			return icr;
		}		
	}
			
	public void recycle(BoundUnresolvedElementConflictResolver icr){		
		if(boundUnresolvedElementConflictResolverFree == boundUnresolvedElementConflictResolverPoolSize){
			BoundUnresolvedElementConflictResolver[] increased = new BoundUnresolvedElementConflictResolver[++boundUnresolvedElementConflictResolverPoolSize];
			System.arraycopy(boundUnresolvedElementConflictResolver, 0, increased, 0, boundUnresolvedElementConflictResolverFree);
			boundUnresolvedElementConflictResolver = increased;
		}
		boundUnresolvedElementConflictResolver[boundUnresolvedElementConflictResolverFree++] = icr;
	}
	
	public BoundAmbiguousAttributeConflictResolver getBoundAmbiguousAttributeConflictResolver(String namespaceURI,
                                                                    String localName,
                                                                    String qName,
                                                                    String value, 
																	Queue queue, 
																	int entry, 
																	Map<AAttribute, AttributeBinder> attributeBinders){				
		if(boundAmbiguousAttributeConflictResolverFree == 0){
			// boundAmbiguousAttributeConflictResolverCreated++;
			BoundAmbiguousAttributeConflictResolver icr = new BoundAmbiguousAttributeConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(namespaceURI,
                    localName,
                    qName,
                    value, 
					queue, 
					entry, 
					attributeBinders);			
			return icr;			
		}else{
			BoundAmbiguousAttributeConflictResolver icr = boundAmbiguousAttributeConflictResolver[--boundAmbiguousAttributeConflictResolverFree];
			icr.init(namespaceURI,
                    localName,
                    qName,
                    value, 
					queue, 
					entry, 
					attributeBinders);			
			return icr;
		}		
	}
	
	public void recycle(BoundAmbiguousAttributeConflictResolver icr){		
		if(boundAmbiguousAttributeConflictResolverFree == boundAmbiguousAttributeConflictResolverPoolSize){
			BoundAmbiguousAttributeConflictResolver[] increased = new BoundAmbiguousAttributeConflictResolver[++boundAmbiguousAttributeConflictResolverPoolSize];
			System.arraycopy(boundAmbiguousAttributeConflictResolver, 0, increased, 0, boundAmbiguousAttributeConflictResolverFree);
			boundAmbiguousAttributeConflictResolver = increased;
		}
		boundAmbiguousAttributeConflictResolver[boundAmbiguousAttributeConflictResolverFree++] = icr;
	}
	
	public BoundUnresolvedAttributeConflictResolver getBoundUnresolvedAttributeConflictResolver(String namespaceURI,
                                                                    String localName,
                                                                    String qName,
                                                                    String value, 
																	Queue queue, 
																	int entry, 
																	Map<AAttribute, AttributeBinder> attributeBinders){				
		if(boundUnresolvedAttributeConflictResolverFree == 0){
			// boundUnresolvedAttributeConflictResolverCreated++;
			BoundUnresolvedAttributeConflictResolver icr = new BoundUnresolvedAttributeConflictResolver(debugWriter);
			icr.init(this, validationItemLocator);
			icr.init(namespaceURI,
                    localName,
                    qName,
                    value, 
					queue, 
					entry, 
					attributeBinders);			
			return icr;			
		}else{
			BoundUnresolvedAttributeConflictResolver icr = boundUnresolvedAttributeConflictResolver[--boundUnresolvedAttributeConflictResolverFree];
			icr.init(namespaceURI,
                    localName,
                    qName,
                    value, 
					queue, 
					entry, 
					attributeBinders);			
			return icr;
		}		
	}
	
	public void recycle(BoundUnresolvedAttributeConflictResolver icr){		
		if(boundUnresolvedAttributeConflictResolverFree == boundUnresolvedAttributeConflictResolverPoolSize){
			BoundUnresolvedAttributeConflictResolver[] increased = new BoundUnresolvedAttributeConflictResolver[++boundUnresolvedAttributeConflictResolverPoolSize];
			System.arraycopy(boundUnresolvedAttributeConflictResolver, 0, increased, 0, boundUnresolvedAttributeConflictResolverFree);
			boundUnresolvedAttributeConflictResolver = increased;
		}
		boundUnresolvedAttributeConflictResolver[boundUnresolvedAttributeConflictResolverFree++] = icr;
	}
}