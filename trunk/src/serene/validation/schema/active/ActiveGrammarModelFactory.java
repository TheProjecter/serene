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

import java.util.Arrays;

import serene.validation.schema.active.components.ANameClass;
import serene.validation.schema.active.components.AElement;

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SExceptNameClass;

import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SDummy;

import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.simplified.util.AbstractSimplifiedComponentVisitor;

import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;


import serene.util.ObjectIntHashMap;

import sereneWrite.MessageWriter;

public class ActiveGrammarModelFactory extends AbstractSimplifiedComponentVisitor{	

	AElement startElement;
	
	ActiveDefinitionPool[] refDefinitions;
	
	int elementIndex; //boyscout type, always prepared
	int elementSize;	
	ActiveDefinitionPool[] elementDefinitions;
	ANameClass[] elementNameClasses;
	
	int attributeIndex; //boyscout type, always prepared
	int attributeSize;	
	ActiveDefinitionPool[] attributeDefinitions;
	ANameClass[] attributeNameClasses;
	
	int exceptPatternIndex; //boyscout type, always prepared
	int exceptPatternSize;	
	ActiveDefinitionPool[] exceptPatternDefinitions;
	
	/**
	* Maps an SElement to an int representing the index of an ActiveDefinitionPool	
	* and ANameClass in the ActiveModel. When building an AElement based on the 
	* SElement this index will be passed to the constructor as value and it will 
	* be later used to obtain and assemble the corresponding ActiveDefinition.
	*/
	ObjectIntHashMap selementIndexMap;	
	/**
	* Maps an SAttribute to an int representing the index of an ActiveDefinitionPool	
	* and ANameClass in the ActiveModel. When building an AAttribute based on the 
	* SAttribute this index will be passed to the constructor as value and it will 
	* be later used to obtain and assemble the corresponding ActiveDefinition.
	*/
	ObjectIntHashMap sattributeIndexMap;
	/**
	* Maps an SExceptPattern to an int representing the index of an ActiveDefinitionPool	
	* and ANameClass in the ActiveModel. When building an AExceptPattern based on the 
	* SExceptPattern this index will be passed to the constructor as value and it will 
	* be later used to obtain and assemble the corresponding ActiveDefinition.
	*/
	ObjectIntHashMap sexceptPatternIndexMap;	
	
	ActiveGrammarModel model;
	
	ActiveDefinitionDirector definitionDirector;
	ActiveComponentBuilder componentBuilder;
		
	ActiveNameClassDirector nameClassDirector;
	
	MessageWriter debugWriter;
	
	public ActiveGrammarModelFactory(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		nameClassDirector = new ActiveNameClassDirector(debugWriter);
	}
	
	public ActiveGrammarModel createActiveGrammarModel(SimplifiedModel simplifiedModel,													
													ActiveModelRuleHandlerPool ruleHandlerPool,
													ActiveModelStackHandlerPool stackHandlerPool){
		init(stackHandlerPool, ruleHandlerPool);
		model = new ActiveGrammarModel(debugWriter);
        
		createStart(simplifiedModel, stackHandlerPool, ruleHandlerPool);
        
        SPattern[] refDefTops = simplifiedModel.getRefDefinitionTopPattern();
		createRecord(refDefTops);
				
		model.init(startElement, 
					refDefinitions,
					selementIndexMap,
					Arrays.copyOf(elementDefinitions, elementIndex),
					Arrays.copyOf(elementNameClasses, elementIndex),
					sattributeIndexMap,
					Arrays.copyOf(attributeDefinitions, attributeIndex),
					Arrays.copyOf(attributeNameClasses, attributeIndex),
					sexceptPatternIndexMap,
					Arrays.copyOf(exceptPatternDefinitions, exceptPatternIndex));
		
		return model;
	}
	
	private void init(ActiveModelStackHandlerPool stackHandlerPool, ActiveModelRuleHandlerPool ruleHandlerPool){		
		definitionDirector = new ActiveDefinitionDirector(debugWriter);
		componentBuilder = new ActiveComponentBuilder(stackHandlerPool,
														ruleHandlerPool, 
														debugWriter);
		
		selementIndexMap = new ObjectIntHashMap(debugWriter);
		sattributeIndexMap = new ObjectIntHashMap(debugWriter);
		sexceptPatternIndexMap = new ObjectIntHashMap(debugWriter);
		
		elementIndex = 0;
		elementSize = 10;		
		elementDefinitions = new ActiveDefinitionPool[elementSize];
		elementNameClasses = new ANameClass[elementSize];
		
		attributeIndex = 0; 
		attributeSize = 10;	
		attributeDefinitions = new ActiveDefinitionPool[attributeSize];
		attributeNameClasses = new ANameClass[attributeSize];
		
		exceptPatternIndex = 0; 
		exceptPatternSize = 10;	
		exceptPatternDefinitions = new ActiveDefinitionPool[exceptPatternSize];
	}
	
	private void createStart(SimplifiedModel simplifiedModel, ActiveModelStackHandlerPool stackHandlerPool, ActiveModelRuleHandlerPool ruleHandlerPool){
        SPattern originalTopPattern = simplifiedModel.getStartTopPattern()[0];
        
		if(elementIndex == elementSize) increaseElementSize();	
        selementIndexMap.put(null, elementIndex);
		elementNameClasses[elementIndex] = null;		
        elementDefinitions[elementIndex] = new ActiveDefinitionPool(originalTopPattern,
																	model,
																	definitionDirector,
																	componentBuilder,
																	debugWriter);
        startElement = new AElement(elementIndex, model, stackHandlerPool, ruleHandlerPool, null, debugWriter);
        elementIndex++;
        
        originalTopPattern.accept(this);
	}
	
	private void createRecord(SPattern[] originalTopPatterns){
		refDefinitions = new ActiveDefinitionPool[originalTopPatterns.length];
		for(int i = 0; i< originalTopPatterns.length; i++){						
			refDefinitions[i] = new ActiveDefinitionPool(originalTopPatterns[i],
																	model,
																	definitionDirector,
																	componentBuilder,
																	debugWriter);
		}
        
        for(SPattern originalTopPattern : originalTopPatterns){
			if(originalTopPattern != null)//check for empty or notAllowed, the place was created anyway  
                originalTopPattern.accept(this);
		}
	}
	
	private void createRecord(SElement originalElement){
		if(elementIndex == elementSize) increaseElementSize();
		selementIndexMap.put(originalElement, elementIndex);
		elementNameClasses[elementIndex] = nameClassDirector.createActiveNameClass(componentBuilder,
																	originalElement.getNameClass());       
		elementDefinitions[elementIndex++] = new ActiveDefinitionPool(originalElement.getChild(),
																	model,
																	definitionDirector,
																	componentBuilder,
																	debugWriter);
	}
	
	private void createRecord(SAttribute originalAttribute){		
		if(attributeIndex == attributeSize) increaseAttributeSize();		
		sattributeIndexMap.put(originalAttribute, attributeIndex);
		attributeNameClasses[attributeIndex] = nameClassDirector.createActiveNameClass(componentBuilder,
																	originalAttribute.getNameClass());
       
		attributeDefinitions[attributeIndex++] = new ActiveDefinitionPool(originalAttribute.getChildren()[0],
																	model,
																	definitionDirector,
																	componentBuilder,
																	debugWriter);
	}
	
	private void createRecord(SExceptPattern originalExceptPattern){		
		if(exceptPatternIndex == exceptPatternSize) increaseExceptPatternSize();
		sexceptPatternIndexMap.put(originalExceptPattern, exceptPatternIndex);		
		exceptPatternDefinitions[exceptPatternIndex++] = new ActiveDefinitionPool(originalExceptPattern.getChild(),
																	model,
																	definitionDirector,
																	componentBuilder,
																	debugWriter);
	}
	
	private void increaseElementSize(){
		elementSize += 10;
		
		ActiveDefinitionPool[] increasedDef = new ActiveDefinitionPool[elementSize];
		System.arraycopy(elementDefinitions, 0, increasedDef, 0, elementIndex);
		elementDefinitions = increasedDef;
		
		ANameClass[] increasedNC = new ANameClass[elementSize];
		System.arraycopy(elementNameClasses, 0, increasedNC, 0, elementIndex);
		elementNameClasses = increasedNC;
	}
		
	private void increaseAttributeSize(){
		attributeSize += 10;
		
		ActiveDefinitionPool[] increasedDef = new ActiveDefinitionPool[attributeSize];
		System.arraycopy(attributeDefinitions, 0, increasedDef, 0, attributeIndex);
		attributeDefinitions = increasedDef;
		
		ANameClass[] increasedNC = new ANameClass[attributeSize];
		System.arraycopy(attributeNameClasses, 0, increasedNC, 0, attributeIndex);
		attributeNameClasses = increasedNC;
	}
	
	private void increaseExceptPatternSize(){
		exceptPatternSize += 10;
		
		ActiveDefinitionPool[] increasedDef = new ActiveDefinitionPool[exceptPatternSize];
		System.arraycopy(exceptPatternDefinitions, 0, increasedDef, 0, exceptPatternIndex);
		exceptPatternDefinitions = increasedDef;		
	}
	
	
	
	public void visit(SElement element){
        if(selementIndexMap.containsKey(element))return;
		createRecord(element);
		SimplifiedComponent child = element.getChild();
		if(child != null) child.accept(this);	
	}
	
	public void visit(SAttribute attribute){
        if(sattributeIndexMap.containsKey(attribute))return;
		createRecord(attribute);
		SimplifiedComponent[] children = attribute.getChildren();
		if(children != null) children[0].accept(this);
	}		
	
	public void visit(SExceptPattern exceptPattern){
        if(sexceptPatternIndexMap.containsKey(exceptPattern))return;
		createRecord(exceptPattern);
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
	
	public void visit(SDummy dummy){
	}
}
