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

import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SExceptNameClass;

import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SChoicePattern;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SGroup;
import serene.validation.schema.simplified.SListPattern;
import serene.validation.schema.simplified.SEmpty;
import serene.validation.schema.simplified.SText;
import serene.validation.schema.simplified.SNotAllowed;
import serene.validation.schema.simplified.SRef;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SGrammar;
import serene.validation.schema.simplified.SDummy;

import serene.validation.schema.simplified.SName;
import serene.validation.schema.simplified.SAnyName;
import serene.validation.schema.simplified.SNsName;
import serene.validation.schema.simplified.SChoiceNameClass;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

class InclusionController implements SimplifiedComponentVisitor{
	
	SNameClass including;
	boolean inclusion;
	
	NsNameInclusionController nsNameInclusionController;	
	NameInclusionController nameInclusionController;
	
	InclusionController(){
		nsNameInclusionController = new NsNameInclusionController();	
		nameInclusionController = new NameInclusionController();
	}
	
	/**
	* Tests if the set of names defined by the including class includes the set 
	* of names defined by the included class. 
	*/
	boolean inclusion(SNameClass included, SNameClass including){
		this.including = including;
		inclusion = false;
		included.accept(this);
		return inclusion;
	}
	
	public void visit(SName name){
		nameInclusionController.init(name);
		inclusion = nameInclusionController.isIncludedIn(including);
	}
	
	public void visit(SNsName nsName){
		nsNameInclusionController.init(nsName);
		inclusion = nsNameInclusionController.isIncludedIn(including);
	}	
		
	public void visit(SChoiceNameClass choice){
		SNameClass[] children = choice.getChildren();
		if(children != null) {
			for(SNameClass child : children){
				child.accept(this);
				if(!inclusion) return;
			}			
		}
	}	
	
	public void visit(SAnyName component){
		throw new IllegalStateException();
	}
	

	public void visit(SExceptPattern exceptPattern){
		throw new IllegalStateException();
	}
	public void visit(SExceptNameClass exceptNameClass){
		throw new IllegalStateException();		
	}	
	
	
	public void visit(SElement element){
		throw new IllegalStateException();	
	}	
	public void visit(SAttribute attribute){
		throw new IllegalStateException();
	}
	public void visit(SChoicePattern choice){
		throw new IllegalStateException();
	}
	public void visit(SInterleave interleave){
		throw new IllegalStateException();
	}
	public void visit(SGroup group){
		throw new IllegalStateException();
	}
	public void visit(SListPattern list){
		throw new IllegalStateException();
	}	
	public void visit(SEmpty empty){
		throw new IllegalStateException();
	}
	public void visit(SText text){
		throw new IllegalStateException();
	}
	public void visit(SNotAllowed notAllowed){
		throw new IllegalStateException();
	}
	public void visit(SRef ref){
		throw new IllegalStateException();
	}
	public void visit(SValue value){
		throw new IllegalStateException();
	}
	public void visit(SData data){	
		throw new IllegalStateException();
	}	
	public void visit(SGrammar grammar){
		throw new IllegalStateException();
	}
	public void visit(SDummy dummy){
		throw new IllegalStateException();
	}	
}
