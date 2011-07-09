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

package serene.validation.schema.active;

import serene.validation.schema.simplified.components.SPattern;
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

import serene.validation.schema.active.components.APattern;

import serene.validation.schema.active.util.ContextCacheMaker;

import serene.util.ObjectIntHashMap;

import sereneWrite.MessageWriter;
//import sereneWrite.ActiveComponentWriter;

class ActiveDefinitionDirector implements SimplifiedComponentVisitor{
	
	ContextCacheMaker cacheMaker;
	
	ActiveComponentBuilder builder;
	ActiveGrammarModel grammarModel;
	
	//ActiveComponentWriter acw;
	MessageWriter debugWriter;
	
	ActiveDefinitionDirector(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		//acw = new ActiveComponentWriter();
		cacheMaker = new ContextCacheMaker(debugWriter);
	}
	
	ActiveDefinition createDefinition(ActiveComponentBuilder builder,
										SPattern originalTopPattern, 
										ActiveGrammarModel grammarModel,
										ActiveDefinitionRecycler recycler){		
		this.builder = builder;
		this.grammarModel = grammarModel;		
		cacheMaker.init();
		
		APattern definitionTopPattern = null;		
		if(originalTopPattern != null){		
			builder.startBuild();
			originalTopPattern.accept(this);		
			definitionTopPattern = builder.getCurrentPattern();
			cacheMaker.makeCache(definitionTopPattern);
		}				
		ActiveDefinition def = new ActiveDefinition(definitionTopPattern, 
										cacheMaker.getElements(),
										cacheMaker.getAttributes(),
										cacheMaker.getDatas(),
										cacheMaker.getValues(),
										cacheMaker.getListPatterns(),
										cacheMaker.getTexts(),
										cacheMaker.getRefs(),
										recycler, 
										debugWriter);
		return def;
	}
	
	
	public void visit(SExceptPattern exceptPattern){		
		builder.buildExceptPattern(grammarModel.getIndex(exceptPattern), grammarModel, exceptPattern);
	}
	public void visit(SExceptNameClass exceptNameClass){
		SimplifiedComponent child = exceptNameClass.getChild();
		if(child != null) nextLevel(child);
		builder.buildExceptNameClass(exceptNameClass);
	}
		
	public void visit(SName name){
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
		builder.buildElement(grammarModel.getIndex(element), grammarModel, element);
	}	
	public void visit(SAttribute attribute){	
		builder.buildAttribute(grammarModel.getIndex(attribute), grammarModel, attribute);
	}
	public void visit(SChoicePattern choice){
		SimplifiedComponent[] children = choice.getChildren();
		if(children != null) nextLevel(children);
		builder.buildChoicePattern(choice);
	}
	public void visit(SInterleave interleave){
		SimplifiedComponent[] children = interleave.getChildren();
		if(children != null) nextLevel(children);
		builder.buildInterleave(interleave);
	}
	public void visit(SGroup group){
		SimplifiedComponent[] children = group.getChildren();
		if(children != null) nextLevel(children);
		builder.buildGroup(group);
	}
	public void visit(SZeroOrMore zeroOrMore){
		SimplifiedComponent child = zeroOrMore.getChild();
		if(child != null) next(child);
		APattern achild = builder.getCurrentPattern();
		achild.setMinOccurs(0);
		achild.setMaxOccurs(APattern.UNBOUNDED);
	}
	public void visit(SOneOrMore oneOrMore){
		SimplifiedComponent child = oneOrMore.getChild();
		if(child != null) next(child);
		APattern achild = builder.getCurrentPattern();
		achild.setMaxOccurs(APattern.UNBOUNDED);
	}
	public void visit(SOptional optional){
		SimplifiedComponent child = optional.getChild();
		if(child != null) next(child);
		APattern achild = builder.getCurrentPattern();
		achild.setMinOccurs(0);
	}
	public void visit(SMixed mixed){
		builder.startLevel();
		SimplifiedComponent child = mixed.getChild();
		if(child != null) next(child);
		builder.buildText(mixed);
		builder.endLevel();
		builder.buildInterleave(mixed);
	}	
	public void visit(SListPattern list){
		SimplifiedComponent child = list.getChild();
		if(child != null) nextLevel(child);
		builder.buildListPattern(list);
	}	
	public void visit(SEmpty empty){
		builder.buildEmpty(empty);
	}
	public void visit(SText text){
		builder.buildText(text);
	}
	public void visit(SNotAllowed notAllowed){
        // TODO review, 
        // NOTE if it changes, endValidation must change too
        builder.buildEmpty(notAllowed);
		//builder.buildNotAllowed(notAllowed.getQName(), notAllowed.getLocation());
	}
	public void visit(SRef ref){
		builder.buildRef(ref.getDefinitionIndex(), grammarModel, ref);
	}
	public void visit(SValue value){
		builder.buildValue(value.getNamespaceURI(), value.getDatatype(), value.getCharContent(), grammarModel, value);	
	}
	public void visit(SData data){	
		SimplifiedComponent[] exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) nextLevel(exceptPattern);
		builder.buildData(data.getDatatype(), grammarModel, data);
	}	
	public void visit(SGrammar grammar){
		SimplifiedComponent child = grammar.getChild();
		if(child != null) nextLevel(child);
		builder.buildGrammar(grammar);
	}
		
	public void visit(SDummy dummy){
		throw new IllegalStateException();
	}
	
	private void next(SimplifiedComponent child){
		child.accept(this);
	}

	private void nextLevel(SimplifiedComponent child){
		builder.startLevel();{			
			child.accept(this);
		}builder.endLevel();
	}
	
	private void nextLevel(SimplifiedComponent[] children){
		builder.startLevel();{			
			for(SimplifiedComponent child : children){            
				child.accept(this);
			}
		}builder.endLevel();
	}
}