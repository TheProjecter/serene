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

package serene.parser;

import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.bind.BindingModel;
import serene.bind.DocumentBinder;
import serene.bind.ElementBinder;
import serene.bind.AttributeBinder;
import serene.bind.CharacterContentBinder;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import serene.Constants;

import sereneWrite.MessageWriter;

public class RNGParseBindingModel extends BindingModel{
	RNGParseBindingPool pool;
	ParsedComponentBuilder builder;
	
	RNGParseBindingModel(DocumentBinder documentBinder,
	                Map<SElement, ElementBinder> selementBinder,
	                ElementBinder genericElementBinder,
					Map<SAttribute, AttributeBinder> sattributeBinder,
					AttributeBinder genericAttributeBinder,
					RNGParseBindingPool pool,
					ParsedComponentBuilder builder,
					MessageWriter debugWriter){
		super(documentBinder, selementBinder, genericElementBinder, sattributeBinder, genericAttributeBinder, debugWriter);		
		this.pool = pool;
		this.builder = builder;
	}
	
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null){
	        throw new NullPointerException();
	    }else if(name.equals(Constants.PARSED_COMPONENT_BUILDER_PROPERTY)){
	        throw new SAXNotSupportedException();
		}
		throw new SAXNotRecognizedException();
	}
	
	public Object getProperty(String name)  throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null){
	        throw new NullPointerException();
	    }else if(name.equals(Constants.PARSED_COMPONENT_BUILDER_PROPERTY)){
	        return builder;
		}
		throw new SAXNotRecognizedException();
	}

	public void setFeature(String name, boolean value)  throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null){
	        throw new NullPointerException();
	    }
		throw new SAXNotRecognizedException();
	}

	public boolean getFeature(String name)  throws SAXNotRecognizedException{
	    if(name == null){
	        throw new NullPointerException();
	    }
		throw new SAXNotRecognizedException();
	}
	
	public void recycle(){
		pool.recycle(this);	
	}
}
	