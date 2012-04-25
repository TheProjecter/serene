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

package serene.restrictor;

import serene.validation.schema.simplified.components.SNameClass;
import serene.validation.schema.simplified.components.SExceptNameClass;

import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SAnyName;

class NsNameOverlapController extends NameClassOverlapController{
	SNsName nsName;
	SNameClass excepted;
	
	AnyNameOverlapController anyNameOverlapController;
	
	NameInclusionController nameInclusionController;
	
	NsNameOverlapController(AnyNameOverlapController anyNameOverlapController,
							NameInclusionController nameInclusionController){
		super();	
		this.anyNameOverlapController = anyNameOverlapController;	
		this.nameInclusionController = nameInclusionController;
	}
	
	void init(SNsName nsName){
		this.nsName = nsName;
		SExceptNameClass except = nsName.getExceptNameClass();
		if(except != null) excepted = except.getChild();
		else excepted = null;
	}
	
	public void visit(SAnyName anyName){
		anyNameOverlapController.init(anyName);
		overlap = anyNameOverlapController.overlap(nsName);
	}		
	public void visit(SNsName nsName){		
		overlap = this.nsName.getNamespaceURI().equals(nsName.getNamespaceURI()); 
	}
	public void visit(SName name){
		overlap = nsName.getNamespaceURI().equals(name.getNamespaceURI());		
		if(!overlap || excepted == null) return;		
		nameInclusionController.init(name);		
		overlap = !nameInclusionController.isIncludedIn(excepted);
	}
}
