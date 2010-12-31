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

package serene.validation.handlers.match;

import java.util.List;
import java.util.ArrayList;

import serene.validation.schema.active.ElementContentType;
import serene.validation.schema.active.AttributesType;
import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.AExceptPattern;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AText;

import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;


public class MatchHandler{

	MessageWriter debugWriter;
	
	List<AElement> elementMatches;
	List<AAttribute> attributeMatches;
		
	List<AData> datas;
	List<AValue> values;
	List<AListPattern> listPatterns;
	List<AText> texts;
	

	boolean recognizeOutOfContext;
	
	public MatchHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		elementMatches = new ArrayList<AElement>();
		attributeMatches = new ArrayList<AAttribute>();
		datas = new ArrayList<AData>();
		values = new ArrayList<AValue>();
		listPatterns = new ArrayList<AListPattern>();
		texts = new ArrayList<AText>();		
	}	
	
	//TODO init with the definitions and nameClass arrays
	// for out of context handling 
	//		loop all definitions
	//		loop all cached elements(attributes)
	//		get index, get nameClass
	//		if match - add to the matches list
		
	
	
	public List<AElement> matchElement(String namespace, String name){
		// TODO
		return elementMatches;
	}
	
	public List<AAttribute> matchAttribute(String namespace, String name){
		// TODO
		return attributeMatches;
	}
	
	public void setRecognizeOutOfContext(boolean recognizeOutOfContext){
		this.recognizeOutOfContext = recognizeOutOfContext;
	}
	public boolean getRecognizeOutOfContext(){
		return recognizeOutOfContext;
	}
	
	public List<AElement> matchElement(String namespace, String name, ElementContentType type){		
		elementMatches.clear();		
		elementMatches = type.getElementMatches(namespace, name, elementMatches);
		return elementMatches;		
	}
	
	public List<AAttribute> matchAttribute(String namespace, String name, AttributesType type){		
		attributeMatches.clear();
		attributeMatches = type.getAttributeMatches(namespace, name, attributeMatches);				
		return attributeMatches;		
	}
		
	public List<AData> getDataMatches(DataActiveType type){
		datas.clear();
		datas = type.getDataMatches(datas);
		return datas;
	}
	public List<AValue> getValueMatches(DataActiveType type){
		values.clear();
		values = type.getValueMatches(values);		
		return values;
	}	
	public List<AText> getTextMatches(CharsActiveType type){
		texts.clear();
		texts = type.getTexts(texts);
		return texts;
		
	}
	public List<AListPattern> getListPatternMatches(StructuredDataActiveType type){
		listPatterns.clear();
		listPatterns = type.getListPatterns(listPatterns);
		/*for(AListPattern listPattern : listPatterns){
			if(listPatternTester == null)listPatternTester = new ListPatternTester(debugWriter);
			if(charsMatches.size() == 0){
				if(!listPatternTester.matches(chars, listPattern, validationContext, errorCatcher)){
					listPatterns.remove(listPattern);
				}
			}else{
				if(!listPatternTester.matches(chars, listPattern)){
					listPatterns.remove(listPattern);
				}
			}
		}*/
		return listPatterns;
	}
}