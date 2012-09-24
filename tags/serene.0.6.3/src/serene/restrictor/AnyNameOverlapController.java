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

import serene.validation.schema.simplified.SNameClass;
import serene.validation.schema.simplified.SExceptNameClass;

import serene.validation.schema.simplified.SName;
import serene.validation.schema.simplified.SNsName;
import serene.validation.schema.simplified.SAnyName;

class AnyNameOverlapController extends NameClassOverlapController{
	SNameClass excepted;
	
	NsNameInclusionController nsNameInclusionController;
	NameInclusionController nameInclusionController;
	
	AnyNameOverlapController(NsNameInclusionController nsNameInclusionController,
							NameInclusionController nameInclusionController){
		super();	
		this.nsNameInclusionController = nsNameInclusionController;
		this.nameInclusionController = nameInclusionController;
	}
	
	void init(SAnyName anyName){
		SExceptNameClass except = anyName.getExceptNameClass();
		if(except != null) excepted = except.getChild();
		else excepted = null;
	}
	
	public void visit(SAnyName anyName){
		overlap = true;
	}		
	public void visit(SNsName nsName){
		overlap = true;
		if(excepted == null) return;
		nsNameInclusionController.init(nsName);
		overlap = !nsNameInclusionController.isIncludedIn(excepted);
	}
	public void visit(SName name){
		overlap = true;
		if(excepted == null) return;
		nameInclusionController.init(name);
		overlap = !nameInclusionController.isIncludedIn(excepted);
	}
}
