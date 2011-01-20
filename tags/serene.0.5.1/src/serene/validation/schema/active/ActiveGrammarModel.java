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

import java.util.Map;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeException;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SExceptPattern;

import serene.validation.schema.active.components.ANameClass;

import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AExceptPattern;

import serene.util.ObjectIntHashMap;

import sereneWrite.MessageWriter;

public class ActiveGrammarModel{
	
	AElement startElement;
	
	ActiveDefinitionPool[] refDefinitionPool;
	
	// Definitions and nameClasses corresponding to the same element are placed 
	// at the same indexes.
	ObjectIntHashMap selementIndexMap;		
	ActiveDefinitionPool[] elementDefinitionPool;
	ANameClass[] elementNameClass;
	
	ObjectIntHashMap sattributeIndexMap;
	ActiveDefinitionPool[] attributeDefinitionPool;
	ANameClass[] attributeNameClass;
		
	ObjectIntHashMap sexceptPatternIndexMap;
	ActiveDefinitionPool[] exceptPatternDefinitionPool;
	
	MessageWriter debugWriter;
	
	ActiveGrammarModel(MessageWriter debugWriter){
		this.debugWriter  = debugWriter;
	}
	
	void init(AElement startElement,
					ActiveDefinitionPool[] refDefinitionPool,
					ObjectIntHashMap selementIndexMap,
					ActiveDefinitionPool[] elementDefinitionPool,
					ANameClass[] elementNameClass,
					ObjectIntHashMap sattributeIndexMap,
					ActiveDefinitionPool[] attributeDefinitionPool,
					ANameClass[] attributeNameClass,
					ObjectIntHashMap sexceptPatternIndexMap,
					ActiveDefinitionPool[] exceptPatternDefinitionPool){
		this.startElement = startElement;
		
		this.refDefinitionPool = refDefinitionPool;
		
		this.selementIndexMap = selementIndexMap;
		this.elementDefinitionPool = elementDefinitionPool;
		this.elementNameClass = elementNameClass;
		
		this.sattributeIndexMap = sattributeIndexMap;
		this.attributeDefinitionPool = attributeDefinitionPool;
		
		this.sexceptPatternIndexMap = sexceptPatternIndexMap;
		this.exceptPatternDefinitionPool = exceptPatternDefinitionPool;
		
		this.attributeNameClass = attributeNameClass;
	}
	
	ObjectIntHashMap getSElementIndexMap(){
		return selementIndexMap;
	}
	
	ObjectIntHashMap getSAttributeIndexMap(){
		return sattributeIndexMap;
	}
	
	public int getIndex(SElement element){
		return selementIndexMap.get(element);
	}
	
	public int getIndex(SAttribute attribute){
		return sattributeIndexMap.get(attribute);
	}
	
	public int getIndex(SExceptPattern exceptPattern){
		return sexceptPatternIndexMap.get(exceptPattern);
	}
	
	public AElement getStartElement(){
		return startElement;
	} 
	public ActiveDefinition getRefDefinition(int index){
		// A control of the argument can be implemented by keeping in the Pointer
		// a reference to the original SimplifiedPatterns upon which the Pointer
		// is based and checking against the originalTopPattern of the definition
		return refDefinitionPool[index].getActiveDefinition();
	}
	
	public ActiveDefinition getElementDefinition(int index){
		// A control of the argument can be implemented by keeping in the Pointer
		// a reference to the original SimplifiedPatterns upon which the Pointer
		// is based and checking against the originalTopPattern of the definition
		return elementDefinitionPool[index].getActiveDefinition();
	}
	
	public ANameClass getElementNameClass(int index){
		return elementNameClass[index];
	}
	
	public ActiveDefinition getAttributeDefinition(int index){
		// A control of the argument can be implemented by keeping in the Pointer
		// a reference to the original SimplifiedPatterns upon which the Pointer
		// is based and checking against the originalTopPattern of the definition
		return attributeDefinitionPool[index].getActiveDefinition();
	}
	
	public ANameClass getAttributeNameClass(int index){
		return attributeNameClass[index];
	}
	
	public ActiveDefinition getExceptPatternDefinition(int index){
		// A control of the argument can be implemented by keeping in the Pointer
		// a reference to the original SimplifiedPatterns upon which the Pointer
		// is based and checking against the originalTopPattern of the definition
		return exceptPatternDefinitionPool[index].getActiveDefinition();
	}
}