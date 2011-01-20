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

import sereneWrite.MessageWriter; 

class NameInclusionController extends NameClassInclusionController{
	
	SName in;
	
	NameInclusionController(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	void init(SName in){
		this.in = in;
	}
	
	public void visit(SName name){
		isIncluded = name.getLocalPart().equals(in.getLocalPart())
			&& name.getNamespaceURI().equals(in.getNamespaceURI());
	}
	
	public void visit(SNsName nsName){
		isIncluded = nsName.getNamespaceURI().equals(in.getNamespaceURI());
		if(!isIncluded) return;
		isIncluded = false;
		SExceptNameClass except = nsName.getExceptNameClass();
		if(except != null)except.getChild().accept(this);
		isIncluded = !isIncluded;
	}
}
