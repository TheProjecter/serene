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

package serene.internal;


import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import serene.bind.Task;

public abstract class RNGParseTask implements Task{
	ParsedComponentBuilder builder;	
	
	RNGParseTask(){
	}
		
	void setExecutant(ParsedComponentBuilder builder){
		this.builder = builder;	
	} 
	
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null) throw new NullPointerException();
	    throw new SAXNotRecognizedException();
	}
	
	public Object getProperty(String name)  throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null) throw new NullPointerException();
	    throw new SAXNotRecognizedException();
	}

	public void setFeature(String name, boolean value)  throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null) throw new NullPointerException();
	    throw new SAXNotRecognizedException();
	}

	public boolean getFeature(String name)  throws SAXNotRecognizedException, SAXNotSupportedException{
	    if(name == null) throw new NullPointerException();
	    throw new SAXNotRecognizedException();
	}
}

