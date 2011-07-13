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

import org.xml.sax.SAXNotRecognizedException;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.util.ObjectIntHashMap;

import sereneWrite.MessageWriter;

public abstract class BindingPool{
	/*ElementBinder[] element;
	int elementFree, elementPoolSize;
	
	AttributeBinder[] attribute;
	int attributeFree, attributePoolSize;
	
	CharacterContentBinder[] characterContent;
	int characterContentFree, characterContentPoolSize;*/
		
	ValidatorQueuePool[] queuePool;
	int queuePoolFree, queuePoolPoolSize;
	
	protected MessageWriter debugWriter;
	
	public BindingPool(Map<SElement, ? extends ElementTaskPool> startElementTaskPools,
						Map<SAttribute, ? extends AttributeTaskPool> attributeTaskPools,
						Map<SElement, ? extends ElementTaskPool> endElementTaskPools,									
						MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		// the rest will be done by the subclasses so they can use the concrete 
		// task pool classes, cheaper, no more casts  
		// this.startElementTaskPools = startElementTaskPools;
		// this.endElementTaskPools = endElementTaskPools;
		// this.attributeTaskPools = attributeTaskPools;
		
		/*elementFree = 0;
		elementPoolSize = 50;
		element = new ElementBinder[elementPoolSize] ;
		
		attributeFree = 0;
		attributePoolSize = 20;
		attribute = new AttributeBinder[attributePoolSize];
		
		characterContentFree = 0;
		characterContentPoolSize = 3;
		characterContent = new CharacterContentBinder[characterContentPoolSize];*/
		
		queuePoolFree = 0;
		queuePoolPoolSize = 3;	
		queuePool = new ValidatorQueuePool[queuePoolPoolSize];
					
	}	
	
	public abstract BindingModel getBindingModel();
		
	public abstract void recycle(BindingModel bm);
	
	/*
	protected ElementBinder getElementBinder(){
		if(elementFree == 0){			
			return new ElementBinder(debugWriter);			
		}
		else{				
			return element[--elementFree];
		}		
	}
	protected void recycle(ElementBinder[] elementBinder){
		int recycleCount = elementBinder.length;		
		if(elementFree+recycleCount >= elementPoolSize){			 
			elementPoolSize+=recycleCount;
			ElementBinder[] increased = new ElementBinder[elementPoolSize];
			System.arraycopy(element, 0, increased, 0, elementFree);
			element = increased;
		}
		System.arraycopy(elementBinder, 0, element, elementFree, recycleCount);
		elementFree += recycleCount;
	}
	
	protected AttributeBinder getAttributeBinder(){
		if(attributeFree == 0){			
			return new AttributeBinder(debugWriter);			
		}
		else{				
			return attribute[--attributeFree];
		}
	}
	protected void recycle(AttributeBinder[] attributeBinder){
		int recycleCount = attributeBinder.length;
		if(attributeFree+recycleCount >= attributePoolSize){			 
			attributePoolSize+=recycleCount;
			AttributeBinder[] increased = new AttributeBinder[attributePoolSize];
			System.arraycopy(attribute, 0, increased, 0, attributeFree);
			attribute = increased;
		}
		System.arraycopy(attributeBinder, 0, attribute, attributeFree, recycleCount);
		attributeFree += recycleCount;
	}
	
	protected CharacterContentBinder getCharacterContentBinder(){
		if(characterContentFree == 0){			
			return new CharacterContentBinder(debugWriter);			
		}
		else{				
			return characterContent[--characterContentFree];
		}
	}
	protected void recycle(CharacterContentBinder characterContentBinder){
		if(characterContentFree == characterContentPoolSize){			 
			characterContentPoolSize+=3;
			CharacterContentBinder[] increased = new CharacterContentBinder[characterContentPoolSize];
			System.arraycopy(characterContent, 0, increased, 0, characterContentFree);
			characterContent = increased;
		}
		characterContent[characterContentFree++] = characterContentBinder;
	}*/
	
		
	synchronized public ValidatorQueuePool getValidatorQueuePool(){
		if(queuePoolFree == 0){			
			return new ValidatorQueuePool(this, debugWriter);			
		}
		else{				
			return queuePool[--queuePoolFree];
		}
	}
	synchronized public void recycle(ValidatorQueuePool qp){
		if(queuePoolFree == queuePoolPoolSize){			 
			queuePoolPoolSize+=3;
			ValidatorQueuePool[] increased = new ValidatorQueuePool[queuePoolPoolSize];
			System.arraycopy(queuePool, 0, increased, 0, queuePoolFree);
			queuePool = increased;
		}
		queuePool[queuePoolFree++] = qp;
	}
	
	public abstract void setProperty(String name, Object value) throws SAXNotRecognizedException;
	
	public abstract Object getProperty(String name)  throws SAXNotRecognizedException;

	public abstract void setFeature(String name, boolean value)  throws SAXNotRecognizedException;

	public abstract boolean getFeature(String name)  throws SAXNotRecognizedException;	
	
}