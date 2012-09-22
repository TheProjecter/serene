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


public class MatchPathPool{
	int elementMatchPathMaxSize;
	int elementMatchPathFree;
	ElementMatchPath[] elementMatchPath;
	
	
	int attributeMatchPathMaxSize;
	int attributeMatchPathFree;
	AttributeMatchPath[] attributeMatchPath;
	
	int dataMatchPathMaxSize;
	int dataMatchPathFree;
	DataMatchPath[] dataMatchPath;
	
	int valueMatchPathMaxSize;
	int valueMatchPathFree;
	ValueMatchPath[] valueMatchPath;
	
	int listPatternMatchPathMaxSize;
	int listPatternMatchPathFree;
	ListPatternMatchPath[] listPatternMatchPath;
	
	int textMatchPathMaxSize;
	int textMatchPathFree;
	TextMatchPath[] textMatchPath;
	
	MatchPathPool(){
	    elementMatchPathMaxSize = 50;
	    attributeMatchPathMaxSize = 50;
	    dataMatchPathMaxSize = 50;
	    valueMatchPathMaxSize = 50;
	    listPatternMatchPathMaxSize = 50;
	    textMatchPathMaxSize = 50;
	    
	    elementMatchPathFree = 0;
	    elementMatchPath = new ElementMatchPath[elementMatchPathFree];
	    	    
	    attributeMatchPathFree = 0;
	    attributeMatchPath = new AttributeMatchPath[attributeMatchPathFree];
	    
	    dataMatchPathFree = 0;
	    dataMatchPath = new DataMatchPath[dataMatchPathFree];
	    
	    valueMatchPathFree = 0;
	    valueMatchPath = new ValueMatchPath[valueMatchPathFree];	  
	    	    
	    listPatternMatchPathFree = 0;
	    listPatternMatchPath = new ListPatternMatchPath[listPatternMatchPathFree];
	    
	    textMatchPathFree = 0;
	    textMatchPath = new TextMatchPath[textMatchPathFree];
	    
	}
	
	
	public ElementMatchPath getElementMatchPath(){				
		if(elementMatchPathFree == 0){
			return new ElementMatchPath(this);
		}else{
			return elementMatchPath[--elementMatchPathFree];
		}		
	}
		
	public void recycle(ElementMatchPath emp){	
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
			return new AttributeMatchPath(this);
		}else{
			return attributeMatchPath[--attributeMatchPathFree];
		}		
	}
		
	public void recycle(AttributeMatchPath emp){	
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
			return new DataMatchPath(this);
		}else{
			return dataMatchPath[--dataMatchPathFree];
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
			return new ValueMatchPath(this);
		}else{
			return valueMatchPath[--valueMatchPathFree];
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
			return new ListPatternMatchPath(this);
		}else{
			return listPatternMatchPath[--listPatternMatchPathFree];
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
			return new TextMatchPath(this);
		}else{
			return textMatchPath[--textMatchPathFree];
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
