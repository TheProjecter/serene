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

package serene.bind;

import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.util.ObjectIntHashMap;

import serene.Reusable;

import sereneWrite.MessageWriter;

public abstract class BindingModel implements Reusable{
    DocumentBinder documentBinder;    
	Map<SElement, ElementBinder> selementBinder;
	Map<SAttribute, AttributeBinder> sattributeBinder;
	
	ElementBinder[] elementBinder;
	ElementBinder genericElementBinder;
	AttributeBinder[] attributeBinder;
	AttributeBinder genericAttributeBinder;
	CharacterContentBinder characterContentBinder;
	
	MessageWriter debugWriter;	
	
	public BindingModel(DocumentBinder documentBinder,
	                    Map<SElement, ElementBinder> selementBinder,
	                    ElementBinder genericElementBinder,
						Map<SAttribute, AttributeBinder> sattributeBinder,
						AttributeBinder genericAttributeBinder,
						MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.documentBinder = documentBinder;
		this.selementBinder = selementBinder;
		this.genericElementBinder = genericElementBinder;
		this.sattributeBinder = sattributeBinder;
		this.genericAttributeBinder = genericAttributeBinder;
		characterContentBinder = new CharacterContentBinder(debugWriter);
	}
	
	public abstract void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException;
	
	public abstract Object getProperty(String name)  throws SAXNotRecognizedException, SAXNotSupportedException;

	public abstract void setFeature(String name, boolean value)  throws SAXNotRecognizedException, SAXNotSupportedException;

	public abstract boolean getFeature(String name)  throws SAXNotRecognizedException, SAXNotSupportedException;
	
	public void index(ObjectIntHashMap selementIndexMap, ObjectIntHashMap sattributeIndexMap){
		int elementCount = selementIndexMap.size();		
		if(elementBinder == null){
			elementBinder = new ElementBinder[elementCount];
		}else if(elementBinder.length != elementCount){
			elementBinder = new ElementBinder[elementCount];
		}
		Set<SElement> boundElement = selementBinder.keySet();
		for(SElement elem : boundElement){
			elementBinder[selementIndexMap.get(elem)] = selementBinder.get(elem);
		}
		
		int attributeCount = sattributeIndexMap.size();
		if(attributeBinder == null){
			attributeBinder = new AttributeBinder[attributeCount];
		}else if(attributeBinder.length != attributeCount){
			attributeBinder = new AttributeBinder[attributeCount];
		}
		Set<SAttribute> boundAttribute = sattributeBinder.keySet();
		for(SAttribute attr : boundAttribute){			
			AttributeBinder ab = sattributeBinder.get(attr);
			if(ab == null){
				ab = new AttributeBinder(debugWriter);
				sattributeBinder.put(attr, ab);
			}
			attributeBinder[sattributeIndexMap.get(attr)] = ab;
		}		
	}
	
	public DocumentBinder getDocumentBinder(){
	    return documentBinder;
	}	
	public ElementBinder getElementBinder(int elementDefinitionIndex){
		if(elementDefinitionIndex >= elementBinder.length) return null;
		return elementBinder[elementDefinitionIndex];
	}
	public ElementBinder getElementBinder(){		
		return genericElementBinder;
	}
	public AttributeBinder getAttributeBinder(int attributeDefinitionIndex){
		if(attributeDefinitionIndex >= attributeBinder.length) return null;
		return attributeBinder[attributeDefinitionIndex];
	}
	public AttributeBinder getAttributeBinder(){		
		return genericAttributeBinder;
	}
	public CharacterContentBinder getCharacterContentBinder(){
		return characterContentBinder;
	}	
}