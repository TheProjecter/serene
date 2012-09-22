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

package serene.internal;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


import serene.bind.ElementTask;
import serene.bind.ElementTaskContext;

import serene.validation.schema.simplified.SAttribute;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import serene.util.NameInfo;
import serene.util.AttributeInfo;

class ForeignElementTask  extends RNGParseElementTask{      
	ForeignElementTask(){
		super();
	}	
	
	public void execute(){
		builder.endLevel();          
		builder.buildForeignComponent(/*context.getDeclaredXmlns(),*/ context.getElementInputRecordIndex());
	}   

	public boolean needsStartElementInputData(){
	    return true;
	}
}
