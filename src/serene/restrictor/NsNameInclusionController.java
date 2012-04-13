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

import serene.validation.schema.simplified.components.SExceptNameClass;
import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SNsName;

class NsNameInclusionController extends NameClassInclusionController{
	
	SNsName inn;	
	InclusionController inclusionController;
	
	NsNameInclusionController(){
		super();
	}
	
	
	void init(SNsName inn){
		this.inn = inn;
	}
	
	public void visit(SName name){
		isIncluded = false;
	}
	
	public void visit(SNsName nsName){
		if(!nsName.getNamespaceURI().equals(inn.getNamespaceURI())){
			isIncluded = false;
			return;
		}
		SExceptNameClass except = nsName.getExceptNameClass();
		if(except == null){
			isIncluded = true;
			return;
		}
		SExceptNameClass iExcept = inn.getExceptNameClass();
		if(iExcept == null){
			isIncluded = false;
			return;
		}		
	 
		if(inclusionController == null){
			inclusionController = new InclusionController();
		}		
		isIncluded = inclusionController.inclusion(except.getChild(), iExcept.getChild());
	}
}
