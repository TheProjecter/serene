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

import java.util.List;
import java.util.ArrayList;

import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

public class ContextConflictResolver implements ConflictResolver{

	List<InternalConflictResolver> conflictResolvers;
	ActiveModelConflictHandlerPool pool;
	
	MessageWriter debugWriter;
	
	ContextConflictResolver(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		conflictResolvers = new ArrayList<InternalConflictResolver>();
	}
	
	public void recycle(){
		for(InternalConflictResolver conflictResolver : conflictResolvers){
			conflictResolver.recycle();
		}
		conflictResolvers.clear();
		pool.recycle(this);
	}
	
	void init(ActiveModelConflictHandlerPool pool){
		this.pool = pool;
	}
	
	public void add(InternalConflictResolver resolver){
		conflictResolvers.add(resolver);
	}
	
	public void resolve(ErrorCatcher errorCatcher){
		for(InternalConflictResolver resolver : conflictResolvers)
			resolver.resolve(errorCatcher);
	}
	
	public String toString(){
		//return "ContextConflictResolver "+hashCode()+" resolvers "+conflictResolvers;
		return "ContextConflictResolver resolvers "+conflictResolvers;
	}
}