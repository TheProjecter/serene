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

package serene.validation.schema.active.util;

import java.util.Arrays;

import serene.validation.schema.active.components.APattern;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.AValue;


import serene.validation.schema.active.components.AName;
import serene.validation.schema.active.components.AAnyName;
import serene.validation.schema.active.components.ANsName;
import serene.validation.schema.active.components.AChoiceNameClass;


import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AExceptNameClass;

import sereneWrite.MessageWriter;
import sereneWrite.ActiveComponentWriter;

public class ContextCacheMaker extends AbstractActiveComponentVisitor{
	int elementIndex; //boyscout type, always prepared
	int elementSize;	
	AElement[] elements;
	
	int attributeIndex; //boyscout type, always prepared
	int attributeSize;	
	AAttribute[] attributes;
	
	int dataIndex; //boyscout type, always prepared
	int dataSize;	
	AData[] datas;
	
	int valueIndex; //boyscout type, always prepared
	int valueSize;	
	AValue[] values;
	
	int listPatternIndex; //boyscout type, always prepared
	int listPatternSize;	
	AListPattern[] listPatterns;
	
	int textIndex; //boyscout type, always prepared
	int textSize;	
	AText[] texts;
	
	
	int refIndex; //boyscout type, always prepared
	int refSize;	
	ARef[] refs;
	
	ContextCacheMaker cacheMaker;
	
	MessageWriter debugWriter;
	ActiveComponentWriter acw;
	public ContextCacheMaker(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		acw = new ActiveComponentWriter();
		
		elementIndex = 0;
		elementSize = 10;	
		elements  = new AElement[elementSize];
		
		attributeIndex = 0;
		attributeSize = 10;	
		attributes = new AAttribute[attributeSize];
		
		dataIndex = 0;
		dataSize = 10;	
		datas = new AData[dataSize];
		
		valueIndex = 0;
		valueSize = 10;	
		values = new AValue[valueSize];
		
		listPatternIndex = 0;
		listPatternSize = 10;	
		listPatterns = new AListPattern[listPatternSize];
		
		textIndex = 0;
		textSize = 10;	
		texts = new AText[textSize];
		
		
		refIndex = 0; 
		refSize = 10;	
		refs = new ARef[refSize];
	}	
	
	public AElement[] getElements(){
		return Arrays.copyOf(elements, elementIndex);
	}
	
	public AAttribute[] getAttributes(){
		return Arrays.copyOf(attributes, attributeIndex);
	}
	
	public AData[] getDatas(){
		return Arrays.copyOf(datas, dataIndex);
	}
	
	public AValue[] getValues(){
		return Arrays.copyOf(values, valueIndex);
	}
	
	public AListPattern[] getListPatterns(){
		return Arrays.copyOf(listPatterns, listPatternIndex);
	}
	
	public AText[] getTexts(){
		return Arrays.copyOf(texts, textIndex);
	}
	
	public ARef[] getRefs(){
		return Arrays.copyOf(refs, refIndex);
	}
	
	public void init(){
		elementIndex = 0;		
		attributeIndex = 0;		
		dataIndex = 0;
		valueIndex = 0;
		listPatternIndex = 0;
		textIndex = 0;		
		refIndex = 0; 
	}
	
	public void makeCache(APattern topPattern){
		topPattern.accept(this);		
	}
	
	private void increaseElementSize(){
		elementSize += 10;
		
		AElement[] increased = new AElement[elementSize];
		System.arraycopy(elements, 0, increased, 0, elementIndex);
		elements = increased;
	}
		
	private void increaseAttributeSize(){
		attributeSize += 10;
		
		AAttribute[] increased = new AAttribute[attributeSize];
		System.arraycopy(attributes, 0, increased, 0, attributeIndex);
		attributes = increased;		
	}
	
	private void increaseDataSize(){
		dataSize += 10;
		
		AData[] increased = new AData[dataSize];
		System.arraycopy(datas, 0, increased, 0, dataIndex);
		datas = increased;		
	}
	
	private void increaseValueSize(){
		valueSize += 10;
		
		AValue[] increased = new AValue[valueSize];
		System.arraycopy(values, 0, increased, 0, valueIndex);
		values = increased;		
	}
		
	private void increaseListPatternSize(){
		listPatternSize += 10;
		
		AListPattern[] increased = new AListPattern[listPatternSize];
		System.arraycopy(listPatterns, 0, increased, 0, listPatternIndex);
		listPatterns = increased;		
	}
	
	private void increaseTextSize(){
		textSize += 10;
		
		AText[] increased = new AText[textSize];
		System.arraycopy(texts, 0, increased, 0, textIndex);
		texts = increased;		
	}
	
	
	private void increaseRefSize(){
		refSize += 10;
		
		ARef[] increased = new ARef[refSize];
		System.arraycopy(refs, 0, increased, 0, refIndex);
		refs = increased;
	}
		
	
	public void visit(AElement element){
		if(elementIndex == elementSize) increaseElementSize();		 
		elements[elementIndex++] = element;
	}	
	public void visit(AAttribute attribute){
		if(attributeIndex == attributeSize) increaseAttributeSize();		 
		attributes[attributeIndex++] = attribute;
	}	
	public void visit(AData data){
		if(dataIndex == dataSize) increaseDataSize();		 
		datas[dataIndex++] = data;
	}	
	public void visit(AValue value){
		if(valueIndex == valueSize) increaseValueSize();		 
		values[valueIndex++] = value;
	}	
	public void visit(AListPattern list){
		if(listPatternIndex == listPatternSize) increaseListPatternSize();		 
		listPatterns[listPatternIndex++] = list;
		if(cacheMaker == null)cacheMaker = new ContextCacheMaker(debugWriter);
		cacheMaker.init();
		cacheMaker.makeCache(list.getChild());
		list.setContextCache(cacheMaker.getDatas(), cacheMaker.getValues(), cacheMaker.getRefs());
	}
	public void visit(AText text){
		if(textIndex == textSize) increaseTextSize();		 
		texts[textIndex++] = text;
	}	
	public void visit(ARef ref){
		if(refIndex == refSize) increaseRefSize();		 
		refs[refIndex++] = ref;
	}
	
	public void visit(AExceptPattern exceptAPattern){
		throw new IllegalStateException();
	}
	public void visit(AExceptNameClass exceptANameClass){
		throw new IllegalStateException();		
	}
		
	public void visit(AName component){
		throw new IllegalStateException();
	}
	public void visit(AAnyName anyName){
		throw new IllegalStateException();
	}
	public void visit(ANsName nsName){
		throw new IllegalStateException();
	}
	public void visit(AChoiceNameClass choice){
		throw new IllegalStateException();
	}	
}