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

import serene.Reusable;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AText;

import sereneWrite.MessageWriter;

public class ActiveDefinition implements Reusable{
		
	APattern topAPattern;
	
	// cached context info
	AElement[] elements;
	AAttribute[] attributes;
	AData[] datas;
	AValue[] values;
	AListPattern[] listPatterns;
	AText[] texts;
	
	ARef[] refs;
	
	ActiveDefinitionRecycler recycler;	
	
	MessageWriter debugWriter;
	
	ActiveDefinition(APattern topAPattern,
						AElement[] elements,
						AAttribute[] attributes,
						AData[] datas,
						AValue[] values,
						AListPattern[] listPatterns,
						AText[] texts,
						ARef[] refs,						
						ActiveDefinitionRecycler recycler,						
						MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.topAPattern = topAPattern;
		this.elements = elements;
		this.attributes = attributes;
		this.datas = datas;
		this.values = values;
		this.listPatterns = listPatterns;
		this.texts = texts;
		this.refs = refs;		
		this.recycler = recycler;
		
		
	}
	
	public APattern getTopPattern(){
		return topAPattern;
	}
 
	public AElement[] getElements(){
		return elements;
	}
	
	public AAttribute[] getAttributes(){
		return attributes;
	}
	
	public AData[] getDatas(){
		return datas;
	}
	
	public AValue[] getValues(){
		return values;
	}
	
	public AListPattern[] getListPatterns(){
		return listPatterns;
	}
	
	public AText[] getTexts(){
		return texts;
	}
	
	public ARef[] getRefs(){
		return refs;
	}
		
	public void recycle(){
		recycler.recycle(this);
	}
}