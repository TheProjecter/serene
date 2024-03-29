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

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SNameClass;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SListPattern;
import serene.validation.schema.simplified.SText;

import serene.validation.handlers.error.ErrorCatcher;

public class MatchHandler{
	
	List<ElementMatchPath> elementMatchPathes;	
	List<AttributeMatchPath> attributeMatchPathes; 
	
	List<DataMatchPath> dataMatchPathes;
	List<ValueMatchPath> valueMatchPathes;
	List<ListPatternMatchPath> listPatternMatchPathes;
	List<TextMatchPath> textMatchPathes;
	
    List<SimplifiedComponent> unexpectedMatches;
    
	boolean recognizeOutOfContext;
	
	MatchPathPool matchPathPool;
	
	SimplifiedModel simplifiedModel;
	
	public MatchHandler(SimplifiedModel simplifiedModel){
	    this.simplifiedModel = simplifiedModel;
	    
		elementMatchPathes = new ArrayList<ElementMatchPath>();		
		attributeMatchPathes = new ArrayList<AttributeMatchPath>();
		
		
		dataMatchPathes = new ArrayList<DataMatchPath>();
		valueMatchPathes = new ArrayList<ValueMatchPath>();
		listPatternMatchPathes = new ArrayList<ListPatternMatchPath>();
		textMatchPathes = new ArrayList<TextMatchPath>();

		
        unexpectedMatches = new ArrayList<SimplifiedComponent>();

        matchPathPool = new MatchPathPool();		
	}	
			
	public List<SimplifiedComponent> matchElement(String namespace, String name){
		unexpectedMatches.clear();
        
		SElement[] elements = simplifiedModel.getElementDefinitions();
		if(elements == null) return unexpectedMatches;
		
		for(int i = 0; i < elements.length; i++){
		    SNameClass nc = elements[i].getNameClass(); 
		    if(nc != null && nc.matches(namespace, name)) unexpectedMatches.add(elements[i]);
		}
        
		return unexpectedMatches;
	}
	
	public List<SimplifiedComponent> matchAttribute(String namespace, String name){
		unexpectedMatches.clear();
        
		SAttribute[] attributes = simplifiedModel.getAttributeDefinitions();
		if(attributes == null) return unexpectedMatches;
		
		for(int i = 0; i < attributes.length; i++){
		    if(attributes[i].getNameClass().matches(namespace, name)) unexpectedMatches.add(attributes[i]);
		}
		
		return unexpectedMatches;
	}
	
	
	public List<ElementMatchPath> matchElement(String namespace, String name, SElement type){
		elementMatchPathes.clear();	
		type.setElementContentMatchPathes(namespace, name, elementMatchPathes, matchPathPool);
		return elementMatchPathes;		
	}
	
	public List<AttributeMatchPath> matchAttribute(String namespace, String name, SElement type){
		attributeMatchPathes.clear();
		type.setAttributeContentMatchPathes(namespace, name, attributeMatchPathes, matchPathPool);
		return attributeMatchPathes;		
	}
		
	
	public void handleCharsMatches(SElement type){
	    dataMatchPathes.clear();
	    valueMatchPathes.clear();
	    listPatternMatchPathes.clear();
	    textMatchPathes.clear();
	    type.setCharsMatchPathes(dataMatchPathes, valueMatchPathes, listPatternMatchPathes, textMatchPathes, matchPathPool);
	}
	
	public void handleCharsMatches(SAttribute type){
	    dataMatchPathes.clear();
	    valueMatchPathes.clear();
	    listPatternMatchPathes.clear();
	    textMatchPathes.clear();
	    type.setCharsMatchPathes(dataMatchPathes, valueMatchPathes, listPatternMatchPathes, textMatchPathes, matchPathPool);
	}
	
	public void handleCharsMatches(SListPattern type){
	    dataMatchPathes.clear();
	    valueMatchPathes.clear();
	    type.setCharsMatchPathes(dataMatchPathes, valueMatchPathes, matchPathPool);
	}
	
	public void handleCharsMatches(SExceptPattern type){
	    dataMatchPathes.clear();
	    valueMatchPathes.clear();
	    listPatternMatchPathes.clear();
	    type.setCharsMatchPathes(dataMatchPathes, valueMatchPathes, listPatternMatchPathes, matchPathPool);
	}
	
	public void handleTextMatches(SElement type){
	    textMatchPathes.clear();
	    type.setCharsMatchPathes(textMatchPathes, matchPathPool);
	}
	
	public void handleTextMatches(SAttribute type){
	    textMatchPathes.clear();
	    type.setCharsMatchPathes(textMatchPathes, matchPathPool);
	}
	
	public List<DataMatchPath> getDataMatchPathes(){
	     return dataMatchPathes;
	}
	
	public List<ValueMatchPath> getValueMatchPathes(){		
		return valueMatchPathes;
	}
	
	public List<TextMatchPath> getTextMatchPathes(){
		return textMatchPathes;
	}
	
	public List<ListPatternMatchPath> getListPatternMatchPathes(){
		return listPatternMatchPathes;
	}	
	
}