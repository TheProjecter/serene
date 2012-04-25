/*
Copyright 2012 Radu Cernuta 

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

public class UnsynchronizedConflictHandlerPool extends ConflictHandlerPool{	
	UnsynchronizedConflictHandlerPool(){
		super();		
	}
	
	public static ConflictHandlerPool getInstance(){		
		return new UnsynchronizedConflictHandlerPool();
	}
	
	public ActiveModelConflictHandlerPool getActiveModelConflictHandlerPool(){
        return new ActiveModelConflictHandlerPool(null);
	}
		
	public void recycle(ActiveModelConflictHandlerPool amchp){
	    throw new IllegalStateException();
	}	
	void fill(ActiveModelConflictHandlerPool pool,
					AmbiguousElementConflictResolver[] ambiguousElementConflictResolverToFill,
					UnresolvedElementConflictResolver[] unresolvedElementConflictResolverToFill,
					AmbiguousAttributeConflictResolver[] ambiguousAttributeConflictResolverToFill,
					UnresolvedAttributeConflictResolver[] unresolvedAttributeConflictResolverToFill,
					AmbiguousCharsConflictResolver[] ambiguousCharsConflictResolverToFill,
					UnresolvedCharsConflictResolver[] unresolvedCharsConflictResolverToFill,
                    AmbiguousListTokenConflictResolver[] ambiguousListTokenConflictResolverToFill,
                    UnresolvedListTokenConflictResolver[] unresolvedListTokenConflictResolverToFill,
					BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolverToFill,
					BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolverToFill,
					BoundAmbiguousAttributeConflictResolver[] boundAmbiguousAttributeConflictResolverToFill,
					BoundUnresolvedAttributeConflictResolver[] boundUnresolvedAttributeConflictResolverToFill){
	    throw new IllegalStateException();
	}	
	void recycle(int ambiguousElementConflictResolverRecycledCount,
	                    int ambiguousElementConflictResolverEffectivellyUsed,
						AmbiguousElementConflictResolver[] ambiguousElementConflictResolverRecycled,
						int unresolvedElementConflictResolverRecycledCount,
						int unresolvedElementConflictResolverEffectivellyUsed,
						UnresolvedElementConflictResolver[] unresolvedElementConflictResolverRecycled,
						int ambiguousAttributeConflictResolverRecycledCount,
						int ambiguousAttributeConflictResolverEffectivellyUsed,
						AmbiguousAttributeConflictResolver[] ambiguousAttributeConflictResolverRecycled,
						int unresolvedAttributeConflictResolverRecycledCount,
						int unresolvedAttributeConflictResolverEffectivellyUsed,
						UnresolvedAttributeConflictResolver[] unresolvedAttributeConflictResolverRecycled,
						int ambiguousCharsConflictResolverRecycledCount,
						int ambiguousCharsConflictResolverEffectivellyUsed,
						AmbiguousCharsConflictResolver[] ambiguousCharsConflictResolverRecycled,
						int unresolvedCharsConflictResolverRecycledCount,
						int unresolvedCharsConflictResolverEffectivellyUsed,
						UnresolvedCharsConflictResolver[] unresolvedCharsConflictResolverRecycled,
                        int ambiguousListTokenConflictResolverRecycledCount,
                        int ambiguousListTokenConflictResolverEffectivellyUsed,
						AmbiguousListTokenConflictResolver[] ambiguousListTokenConflictResolverRecycled,
						int unresolvedListTokenConflictResolverRecycledCount,
						int unresolvedListTokenConflictResolverEffectivellyUsed,
						UnresolvedListTokenConflictResolver[] unresolvedListTokenConflictResolverRecycled,
						int boundAmbiguousElementConflictResolverRecycledCount,
						int boundAmbiguousElementConflictResolverEffectivellyUsed,
						BoundAmbiguousElementConflictResolver[] boundAmbiguousElementConflictResolverRecycled,
						int boundUnresolvedElementConflictResolverRecycledCount,
						int boundUnresolvedElementConflictResolverEffectivellyUsed,
						BoundUnresolvedElementConflictResolver[] boundUnresolvedElementConflictResolverRecycled,
						int boundAmbiguousAttributeConflictResolverRecycledCount,
						int boundAmbiguousAttributeConflictResolverEffectivellyUsed,
						BoundAmbiguousAttributeConflictResolver[] boundAmbiguousAttributeConflictResolverRecycled,
						int boundUnresolvedAttributeConflictResolverRecycledCount,
						int boundUnresolvedAttributeConflictResolverEffectivellyUsed,
						BoundUnresolvedAttributeConflictResolver[] boundUnresolvedAttributeConflictResolverRecycled){
	    throw new IllegalStateException();	 		
	}	
} 
