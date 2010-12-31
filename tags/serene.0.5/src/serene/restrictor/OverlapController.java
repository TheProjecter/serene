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

import serene.validation.schema.simplified.components.SParam;
import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SExceptNameClass;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SZeroOrMore;
import serene.validation.schema.simplified.components.SOneOrMore;
import serene.validation.schema.simplified.components.SOptional;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SEmpty;
import serene.validation.schema.simplified.components.SText;
import serene.validation.schema.simplified.components.SNotAllowed;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SValue;
import serene.validation.schema.simplified.components.SGrammar;
import serene.validation.schema.simplified.components.SDummy;

import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import serene.Reusable;

import sereneWrite.MessageWriter; 

class OverlapController implements SimplifiedComponentVisitor, Reusable{
	
	SNameClass test;
	boolean overlap;
	
	AnyNameOverlapController anyNameOverlapController;
	NsNameOverlapController nsNameOverlapController;	
	NameOverlapController nameOverlapController;
		
	ControllerPool pool;
	
	MessageWriter debugWriter;
	
	OverlapController(ControllerPool pool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.pool = pool;
		
		NsNameInclusionController nsNameInclusionController = new NsNameInclusionController(debugWriter);
		NameInclusionController nameInclusionController = new NameInclusionController(debugWriter);
		anyNameOverlapController = new AnyNameOverlapController(nsNameInclusionController,
																nameInclusionController,
																debugWriter);
		nsNameOverlapController = new NsNameOverlapController(anyNameOverlapController,
															nameInclusionController,
															debugWriter);	
		nameOverlapController = new NameOverlapController(anyNameOverlapController,
														nsNameOverlapController,
														debugWriter);
	}
		
	public void recycle(){
		pool.recycle(this);
	}
	
	boolean overlap(SNameClass nc1, SNameClass nc2){
		this.test = nc1;
		nc2.accept(this);
		return overlap;
	}
	
	public void visit(SName name){
		nameOverlapController.init(name);
		overlap = nameOverlapController.overlap(test);
	}
	
	public void visit(SNsName nsName){
		nsNameOverlapController.init(nsName);
		overlap = nsNameOverlapController.overlap(test);
	}	
	
	public void visit(SAnyName anyName){
		anyNameOverlapController.init(anyName);
		overlap = anyNameOverlapController.overlap(test);
	}	
	public void visit(SChoiceNameClass choice){
		SNameClass[] children = choice.getChildren();
		if(children != null) {
			for(SNameClass child : children){
				child.accept(this);
				if(overlap) return;
			}			
		}
	}	
		
	
	public void visit(SParam param){
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
	public void visit(SZeroOrMore zeroOrMore){
		throw new IllegalStateException();
	}
	public void visit(SOneOrMore oneOrMore){
		throw new IllegalStateException();
	}
	public void visit(SOptional optional){
		throw new IllegalStateException();
	}
	public void visit(SMixed mixed){
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
