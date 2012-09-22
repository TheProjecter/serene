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


class NameOverlapController extends NameClassOverlapController{
	SName name;
	
	AnyNameOverlapController anyNameOverlapController;
	NsNameOverlapController nsNameOverlapController;
	
	NameOverlapController(AnyNameOverlapController anyNameOverlapController,
							NsNameOverlapController nsNameOverlapController){
		super();		
		this.anyNameOverlapController = anyNameOverlapController;
		this.nsNameOverlapController = nsNameOverlapController;
	}
	
	void init(SName name){
		this.name = name;
	}
	
	public void visit(SAnyName anyName){
		anyNameOverlapController.init(anyName);
		overlap = anyNameOverlapController.overlap(name);
	}		
	public void visit(SNsName nsName){		
		nsNameOverlapController.init(nsName);
		overlap = nsNameOverlapController.overlap(name); 
	}
	public void visit(SName name){
		overlap = this.name.getLocalPart().equals(name.getLocalPart())
			&& this.name.getNamespaceURI().equals(name.getNamespaceURI());
	}
}
