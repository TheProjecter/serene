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

package serene.simplifier;

import serene.validation.jaxp.SchematronParser;

import serene.validation.handlers.error.ErrorDispatcher;

class DefinitionSimplifierPool{
	DefinitionSimplifier[] ds;
	int dsFree;
	int dsSize;
    
    UnreachableDefinitionSimplifier[] uds;
	int udsFree;
	int udsSize;
	
	ErrorDispatcher errorDispatcher;	
	
	SchematronParser schematronParser;
	
	DefinitionSimplifierPool(ErrorDispatcher errorDispatcher, SchematronParser schematronParser){
		this.errorDispatcher = errorDispatcher;
		this.schematronParser = schematronParser;
		
		dsFree = 0;
		dsSize = 5;
		ds = new DefinitionSimplifier[dsSize];
        
        udsFree = 0;
		udsSize = 5;
		uds = new UnreachableDefinitionSimplifier[udsSize];
	}
	
	DefinitionSimplifier getDefinitionSimplifier(){
		if(dsFree == 0){
			return new DefinitionSimplifier(this, errorDispatcher, schematronParser);
		}else{			
			return ds[--dsFree];
		}		
	}
	
	void recycle(DefinitionSimplifier simplifier){
		if(dsFree == dsSize){
			DefinitionSimplifier[] increased = new DefinitionSimplifier[++dsSize];
			System.arraycopy(ds, 0, increased, 0, dsFree);
			ds = increased;
		}
		ds[dsFree++] = simplifier;
	}
    
    
    UnreachableDefinitionSimplifier getUnreachableDefinitionSimplifier(){
		if(udsFree == 0){
			return new UnreachableDefinitionSimplifier(this, errorDispatcher, schematronParser);
		}else{			
			return uds[--udsFree];
		}		
	}
	
	void recycle(UnreachableDefinitionSimplifier simplifier){
		if(udsFree == udsSize){
			UnreachableDefinitionSimplifier[] increased = new UnreachableDefinitionSimplifier[++udsSize];
			System.arraycopy(uds, 0, increased, 0, udsFree);
			uds = increased;
		}
		uds[udsFree++] = simplifier;
	}
}