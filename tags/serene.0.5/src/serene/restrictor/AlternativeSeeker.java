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

import java.util.ArrayList;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

import serene.util.BooleanStack;
import serene.util.IntList;

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

import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import sereneWrite.MessageWriter;

class AlternativeSeeker implements SimplifiedComponentVisitor{
	RecursionModel recursionModel;
	ArrayList<ArrayList<SRef>> definitionBlindLoops;
	ArrayList<ArrayList<SRef>> definitionInfiniteLoops;
	ArrayList<Map<SRef, ArrayList<SRef>>> definitionOpenAlternatives;
					
	IntList handledDefinitions;
	
	boolean isFiniteAlternative;
	SRef testRecursion;
	int testIndex;
	
	MessageWriter debugWriter;
	
	AlternativeSeeker(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	void init(RecursionModel recursionModel,
					ArrayList<ArrayList<SRef>> definitionBlindLoops,
					ArrayList<ArrayList<SRef>> definitionInfiniteLoops,
					ArrayList<Map<SRef, ArrayList<SRef>>> definitionOpenAlternatives,
					IntList handledDefinitions){
		this.recursionModel = recursionModel;
		this.definitionBlindLoops = definitionBlindLoops;
		this.definitionInfiniteLoops = definitionInfiniteLoops;
		this.definitionOpenAlternatives = definitionOpenAlternatives;
		this.handledDefinitions = handledDefinitions;
	}
	
	boolean hasAlternatives(SRef testRecursion,
					Stack<SChoicePattern> loopChoices){
		this.testRecursion = testRecursion;
		this.testIndex = testRecursion.getDefinitionIndex();
		isFiniteAlternative = false;
		
		for(SChoicePattern choice : loopChoices){
			choice.accept(this);
			if(isFiniteAlternative){
				definitionOpenAlternatives.set(testIndex, null);
				return true;
			}
		}		
		if(definitionOpenAlternatives.get(testIndex) != null &&
			!definitionOpenAlternatives.get(testIndex).isEmpty())return true;
		return false;
	}
	public void visit(SParam param){
		isFiniteAlternative = true;
	}	
	public void visit(SExceptPattern exceptPattern){
		SimplifiedComponent child = exceptPattern.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(SExceptNameClass exceptNameClass){
		throw new IllegalStateException();		
	}
		
	public void visit(SName component){
		throw new IllegalStateException();
	}
	public void visit(SAnyName anyName){
		throw new IllegalStateException();
	}
	public void visit(SNsName nsName){
		throw new IllegalStateException();
	}
	public void visit(SChoiceNameClass choice){
		throw new IllegalStateException();
	}	
	
	
	public void visit(SElement element){		
		SimplifiedComponent child = element.getChild();
		if(child != null) child.accept(this);
		else isFiniteAlternative = true;
	}	
	public void visit(SAttribute attribute){
		SimplifiedComponent[] children = attribute.getChildren();
		if(children != null)next(children);		
	}
	public void visit(SChoicePattern choice){
		SimplifiedComponent[] children = choice.getChildren();
		if(children != null) next(children);
	}
	public void visit(SInterleave interleave){
		SimplifiedComponent[] children = interleave.getChildren();
		if(children != null) next(children);
	}
	public void visit(SGroup group){
		SimplifiedComponent[] children = group.getChildren();
		if(children != null) next(children);
	}
	public void visit(SZeroOrMore zeroOrMore){
		SimplifiedComponent child = zeroOrMore.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(SOneOrMore oneOrMore){
		SimplifiedComponent child = oneOrMore.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(SOptional optional){
		SimplifiedComponent child = optional.getChild();
		if(child != null) child.accept(this);
	}
	public void visit(SMixed mixed){
		SimplifiedComponent child = mixed.getChild();
		if(child != null) child.accept(this);
	}	
	public void visit(SListPattern list){
		SimplifiedComponent child = list.getChild();
		if(child != null) child.accept(this);
	}	
	public void visit(SEmpty empty){
		isFiniteAlternative = true;
	}
	public void visit(SText text){
		isFiniteAlternative = true;
	}
	public void visit(SNotAllowed notAllowed){}
	public void visit(SRef ref){
		int index = ref.getDefinitionIndex();
		if(testIndex == index) return;		
		if(isProvenIllegalRecursionDefinition(index)){			
			return;
		}
		if(isProvenLegalRecursionDefinition(index)){			
			isFiniteAlternative = true;
			return;
		}
		
		Map<SRef, ArrayList<SRef>> openAlternatives = definitionOpenAlternatives.get(testIndex);
		if(openAlternatives == null){
			openAlternatives  = new HashMap<SRef, ArrayList<SRef>>();
			definitionOpenAlternatives.set(testIndex, openAlternatives);
		}
		ArrayList<SRef> alt = openAlternatives.get(testRecursion);
		if(alt == null){
			alt = new ArrayList<SRef>();
			openAlternatives.put(testRecursion, alt);
		}
		alt.add(ref);
	}
	
	private boolean isProvenIllegalRecursionDefinition(int index){
		return (definitionBlindLoops.get(index) != null
			&& !definitionBlindLoops.get(index).isEmpty())
			|| (definitionInfiniteLoops.get(index) != null
			&& !definitionInfiniteLoops.get(index).isEmpty());
	}
	
	private boolean isProvenLegalRecursionDefinition(int index){
		if(!recursionModel.isRecursiveDefinition(index)){
			return true;
		}
		
		return  handledDefinitions.contains(index) 
				&&(definitionBlindLoops.get(index) == null ||
				definitionBlindLoops.get(index).isEmpty()) 
				&&(definitionInfiniteLoops.get(index) == null ||
				definitionInfiniteLoops.get(index).isEmpty())
				&& definitionOpenAlternatives.get(index) == null;
	}
	public void visit(SValue value){
		isFiniteAlternative = true;
	}
	public void visit(SData data){	
		SimplifiedComponent[] param = data.getParam();
		if(param != null) next(param);
		SimplifiedComponent[] exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) next(exceptPattern);
		if(param == null && exceptPattern == null)isFiniteAlternative = true;
	}	
	public void visit(SGrammar grammar){
		SimplifiedComponent child = grammar.getChild();
		if(child != null) child.accept(this);
	}
	
	public void visit(SDummy dummy){
		SimplifiedComponent[] children = dummy.getChildren();
		if(children != null) next(children);
	}
		
		
	protected void next(SimplifiedComponent[] children){
		for(SimplifiedComponent child : children){            
			child.accept(this);
		}
	} 
}