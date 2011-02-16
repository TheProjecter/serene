/*
Copyright 2011 Radu Cernuta 

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

package serene.parser;

import serene.bind.ElementTask;
import serene.bind.ElementTaskContext;

import serene.validation.schema.simplified.components.SAttribute;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import serene.util.NameInfo;
import serene.util.AttributeInfo;

import sereneWrite.MessageWriter;

class ForeignElementTask  implements ElementTask{
    SAttribute any;
    
    ParsedComponentBuilder builder;
    ElementTaskContext context;
	ForeignElementTaskPool pool;

    MessageWriter debugWriter;	
	ForeignElementTask(SAttribute any,
                        ForeignElementTaskPool pool, 
						MessageWriter debugWriter){
		this.debugWriter = debugWriter;
        this.any = any;
		this.pool = pool;
	}
    public void recycle(){
		context = null;
		builder = null;
		pool.recycle(this);
	}
	public void setContext(ElementTaskContext context){
		this.context = context;
	}
	
	public void setExecutant(Object builder){
		this.builder = (ParsedComponentBuilder)builder;	
	}
	
	void setExecutant(ParsedComponentBuilder builder){
		this.builder = builder;	
	}    
	
	public void execute(){
		builder.endLevel();
        
        NameInfo elementNameInfo = context.getElementNameInfo();
        
        String namespaceURI = elementNameInfo.getNamespaceURI();
        String localName = elementNameInfo.getLocalName();
        String qName = elementNameInfo.getQName();
        
        
		builder.buildForeignComponent(namespaceURI, 
                                localName, 
                                context.getPrefixMapping(), 
                                context.getXmlBase(), 
                                context.getAttributeInfo(any), 
                                qName, 
                                context.getStartLocation());
	}       
}
