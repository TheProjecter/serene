/*
Copyright 2012 Radu Cernuta 

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

import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SText;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SListPattern;

public class MatchPathPool{
	int elementMatchPathMaxSize;
	int elementMatchPathFree;
	ElementMatchPath[] elementMatchPath;
	/*int elementMatchPathCreated;*/
	
	int attributeMatchPathMaxSize;
	int attributeMatchPathFree;
	AttributeMatchPath[] attributeMatchPath;
	/*int attributeMatchPathCreated;*/
	
	int dataMatchPathMaxSize;
	int dataMatchPathFree;
	DataMatchPath[] dataMatchPath;
	/*int dataMatchPathCreated;*/
	
	int valueMatchPathMaxSize;
	int valueMatchPathFree;
	ValueMatchPath[] valueMatchPath;
	/*int valueMatchPathCreated;*/
	
	int listPatternMatchPathMaxSize;
	int listPatternMatchPathFree;
	ListPatternMatchPath[] listPatternMatchPath;
	/*int listPatternMatchPathCreated;*/
	
	int textMatchPathMaxSize;
	int textMatchPathFree;
	TextMatchPath[] textMatchPath;
	/*int textMatchPathCreated;*/
	
	
	MatchPathPool(){
	    elementMatchPathMaxSize = 100;
	    attributeMatchPathMaxSize = 50;
	    dataMatchPathMaxSize = 50;
	    valueMatchPathMaxSize = 50;
	    listPatternMatchPathMaxSize = 50;
	    textMatchPathMaxSize = 50;
	    
	    elementMatchPathFree = 0;
	    /*elementMatchPathCreated = 0;*/
	    elementMatchPath = new ElementMatchPath[elementMatchPathFree];
	    	    
	    attributeMatchPathFree = 0;
	    /*attributeMatchPathCreated = 0;*/
	    attributeMatchPath = new AttributeMatchPath[attributeMatchPathFree];
	    
	    dataMatchPathFree = 0;
	    /*dataMatchPathCreated = 0;*/
	    dataMatchPath = new DataMatchPath[dataMatchPathFree];
	    
	    valueMatchPathFree = 0;
	    /*valueMatchPathCreated = 0;*/
	    valueMatchPath = new ValueMatchPath[valueMatchPathFree];	  
	    	    
	    listPatternMatchPathFree = 0;
	    /*listPatternMatchPathCreated = 0;*/
	    listPatternMatchPath = new ListPatternMatchPath[listPatternMatchPathFree];
	    
	    textMatchPathFree = 0;
	    /*textMatchPathCreated = 0;*/
	    textMatchPath = new TextMatchPath[textMatchPathFree];
	    
	}
	
	/*
	void writeDifferences(){
	    String d = "ELEMENT created="+elementMatchPathCreated+"  free="+elementMatchPathFree;
	    if(elementMatchPathCreated != elementMatchPathFree) d+= "    DIFF";
	    System.out.println(d);
	    
	    d = "ATTRIBUTE created="+attributeMatchPathCreated+"  free="+attributeMatchPathFree;
	    if(attributeMatchPathCreated != attributeMatchPathFree) d+= "    DIFF";
	    System.out.println(d);	    
	 
	    d = "DATA created="+dataMatchPathCreated+"  free="+dataMatchPathFree;
	    if(dataMatchPathCreated != dataMatchPathFree) d+= "    DIFF";
	    System.out.println(d);
	    
	    d = "VALUE created="+valueMatchPathCreated+"  free="+valueMatchPathFree;
	    if(valueMatchPathCreated != valueMatchPathFree) d+= "    DIFF";
	    System.out.println(d);
	    
	    d = "TEXT created="+textMatchPathCreated+"  free="+textMatchPathFree;
	    if(textMatchPathCreated != textMatchPathFree) d+= "    DIFF";
	    System.out.println(d);
	    
	    d = "LIST PATTERN created="+listPatternMatchPathCreated+"  free="+listPatternMatchPathFree;
	    if(listPatternMatchPathCreated != listPatternMatchPathFree) d+= "    DIFF";
	    System.out.println(d);	    
	}*/
	
	public ElementMatchPath getElementMatchPath(){				
		if(elementMatchPathFree == 0){
		    /*elementMatchPathCreated++;*/
			ElementMatchPath emp = new ElementMatchPath(this);
			return emp;
		}else{
			ElementMatchPath emp = elementMatchPath[--elementMatchPathFree];
			return emp;
		}		
	}
	
    public ElementMatchPath getElementMatchPath(SElement match, SRule[] list, int lastIndex){				
		if(elementMatchPathFree == 0){
		    /*elementMatchPathCreated++;*/
			ElementMatchPath mp = new ElementMatchPath(this);
			mp.init(match, list, lastIndex);
			return mp;
		}else{
			ElementMatchPath mp = elementMatchPath[--elementMatchPathFree];
			mp.init(match, list, lastIndex);
			return mp;
		}		
	}
	
	public void recycle(ElementMatchPath emp){
	    /*for(int i = 0; i < elementMatchPathFree; i++){
	        if(elementMatchPath[i] == emp) throw new IllegalStateException();
	    }*/
	    
		if(elementMatchPathFree == elementMatchPath.length){
		    if(elementMatchPathFree == elementMatchPathMaxSize) return;
			ElementMatchPath[] increased = new ElementMatchPath[5+elementMatchPath.length];
			System.arraycopy(elementMatchPath, 0, increased, 0, elementMatchPathFree);
			elementMatchPath = increased;
		}
		elementMatchPath[elementMatchPathFree++] = emp;
	}
	
	
	public AttributeMatchPath getAttributeMatchPath(){				
		if(attributeMatchPathFree == 0){
			AttributeMatchPath mp = new AttributeMatchPath(this);
			/*attributeMatchPathCreated++;*/
			return mp;
		}else{
			AttributeMatchPath mp = attributeMatchPath[--attributeMatchPathFree];
			return mp;
		}		
	}
	
	public AttributeMatchPath getAttributeMatchPath(SAttribute match, SRule[] list, int lastIndex){				
		if(attributeMatchPathFree == 0){
		    /*attributeMatchPathCreated++;*/
			AttributeMatchPath mp = new AttributeMatchPath(this);
			mp.init(match, list, lastIndex);
			return mp;
		}else{
			AttributeMatchPath mp = attributeMatchPath[--attributeMatchPathFree];
			mp.init(match, list, lastIndex);
			return mp;
		}		
	}
		
	public void recycle(AttributeMatchPath emp){	
	    /*for(int i = 0; i < attributeMatchPathFree; i++){
		    if(attributeMatchPath[i] == emp){
		        System.out.println("MATCH PATH POOL    2 RECYCLE  mp="+emp+"   "+emp.hashCode()+"    attributeMatchPathFree="+attributeMatchPathFree);
		        throw new IllegalStateException();
		    }    
		}*/
		
		if(attributeMatchPathFree == attributeMatchPath.length){
		    if(attributeMatchPathFree == attributeMatchPathMaxSize) return;
			AttributeMatchPath[] increased = new AttributeMatchPath[5+attributeMatchPath.length];
			System.arraycopy(attributeMatchPath, 0, increased, 0, attributeMatchPathFree);
			attributeMatchPath = increased;
		}
		attributeMatchPath[attributeMatchPathFree++] = emp;
		
		
	}

    public DataMatchPath getDataMatchPath(){				
		if(dataMatchPathFree == 0){
		    /*dataMatchPathCreated++;*/
			return new DataMatchPath(this);
		}else{
			return dataMatchPath[--dataMatchPathFree];
		}		
	}
	
	public DataMatchPath getDataMatchPath(SData match, SRule[] list, int lastIndex){				
		if(dataMatchPathFree == 0){
		    /*dataMatchPathCreated++;*/
			DataMatchPath mp = new DataMatchPath(this);
			mp.init(match, list, lastIndex);
			return mp;
		}else{
			DataMatchPath mp = dataMatchPath[--dataMatchPathFree];
			mp.init(match, list, lastIndex);
			return mp;
		}		
	}
	
	public void recycle(DataMatchPath emp){	
		if(dataMatchPathFree == dataMatchPath.length){
		    if(dataMatchPathFree == dataMatchPathMaxSize) return;
			DataMatchPath[] increased = new DataMatchPath[5+dataMatchPath.length];
			System.arraycopy(dataMatchPath, 0, increased, 0, dataMatchPathFree);
			dataMatchPath = increased;
		}
		dataMatchPath[dataMatchPathFree++] = emp;
	}


    public ValueMatchPath getValueMatchPath(){				
		if(valueMatchPathFree == 0){
		    /*valueMatchPathCreated++;*/
			return new ValueMatchPath(this);
		}else{
			return valueMatchPath[--valueMatchPathFree];
		}		
	}
	
	public ValueMatchPath getValueMatchPath(SValue match, SRule[] list, int lastIndex){				
		if(valueMatchPathFree == 0){
		    /*valueMatchPathCreated++;*/
			ValueMatchPath mp = new ValueMatchPath(this);
			mp.init(match, list, lastIndex);
			return mp;
		}else{
			ValueMatchPath mp = valueMatchPath[--valueMatchPathFree];
			mp.init(match, list, lastIndex);
			return mp;
		}		
	}
	
	public void recycle(ValueMatchPath emp){	
		if(valueMatchPathFree == valueMatchPath.length){
		    if(valueMatchPathFree == valueMatchPathMaxSize) return;
			ValueMatchPath[] increased = new ValueMatchPath[5+valueMatchPath.length];
			System.arraycopy(valueMatchPath, 0, increased, 0, valueMatchPathFree);
			valueMatchPath = increased;
		}
		valueMatchPath[valueMatchPathFree++] = emp;
	}
	
	public ListPatternMatchPath getListPatternMatchPath(){				
		if(listPatternMatchPathFree == 0){
		    /*listPatternMatchPathCreated++;*/
			return new ListPatternMatchPath(this);
		}else{
			return listPatternMatchPath[--listPatternMatchPathFree];
		}		
	}
	
	public ListPatternMatchPath getListPatternMatchPath(SListPattern match, SRule[] list, int lastIndex){				
		if(listPatternMatchPathFree == 0){
		    /*listPatternMatchPathCreated++;*/
			ListPatternMatchPath mp = new ListPatternMatchPath(this);
			mp.init(match, list, lastIndex);
			return mp;
		}else{
			ListPatternMatchPath mp = listPatternMatchPath[--listPatternMatchPathFree];
			mp.init(match, list, lastIndex);
			return mp;
		}		
	}
	
	public void recycle(ListPatternMatchPath emp){	
		if(listPatternMatchPathFree == listPatternMatchPath.length){
		    if(listPatternMatchPathFree == listPatternMatchPathMaxSize) return;
			ListPatternMatchPath[] increased = new ListPatternMatchPath[5+listPatternMatchPath.length];
			System.arraycopy(listPatternMatchPath, 0, increased, 0, listPatternMatchPathFree);
			listPatternMatchPath = increased;
		}
		listPatternMatchPath[listPatternMatchPathFree++] = emp;
	}

	public TextMatchPath getTextMatchPath(){				
		if(textMatchPathFree == 0){
		    /*textMatchPathCreated++;*/
			return new TextMatchPath(this);
		}else{
			return textMatchPath[--textMatchPathFree];
		}		
	}
	
	public TextMatchPath getTextMatchPath(SText match, SRule[] list, int lastIndex){				
		if(textMatchPathFree == 0){
		    /*textMatchPathCreated++;*/
			TextMatchPath mp = new TextMatchPath(this);
			mp.init(match, list, lastIndex);
			return mp;
		}else{
			TextMatchPath mp = textMatchPath[--textMatchPathFree];
			mp.init(match, list, lastIndex);
			return mp;
		}		
	}
	
	public void recycle(TextMatchPath emp){	
		if(textMatchPathFree == textMatchPath.length){
		    if(textMatchPathFree == textMatchPathMaxSize) return;
			TextMatchPath[] increased = new TextMatchPath[5+textMatchPath.length];
			System.arraycopy(textMatchPath, 0, increased, 0, textMatchPathFree);
			textMatchPath = increased;
		}
		textMatchPath[textMatchPathFree++] = emp;
	}
	
}
