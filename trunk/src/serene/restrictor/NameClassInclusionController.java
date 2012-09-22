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


/**
* The responsibility of this class is to test if the set of names defined by a 
* name class is included in the set of names defined by another class. It is 
* meant to be used for determining name class overlap in the case of wildcard
* name classes with an <except> child, not independently, and as such only the 
* relevant(allowed by specification) cases are treated, the rest throw an 
* IllegalStateException.
* 
* There is a concrete subclass for every type of included name class that tests
* inclusion in possible including name classes.
*/
abstract class NameClassInclusionController implements SimplifiedComponentVisitor{
	
	boolean isIncluded;
	
	NameClassInclusionController(){
	}
	
	boolean isIncludedIn(SNameClass nc){
		isIncluded = false;
		nc.accept(this);
		return isIncluded;
	}
	
	// public void visit(SName anyName) subclass
	// public void visit(SNsName nsName) subclass
	
	public void visit(SAnyName component){
		throw new IllegalStateException();
	}	
	public void visit(SChoiceNameClass choice){
		SNameClass[] children = choice.getChildren();
		if(children != null) {
			for(SNameClass child : children){
				child.accept(this);
				if(isIncluded) return;
			}			
		}
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