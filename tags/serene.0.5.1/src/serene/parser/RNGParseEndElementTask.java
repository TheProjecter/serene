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

import serene.validation.schema.simplified.components.SAttribute;

import sereneWrite.MessageWriter;

abstract class RNGParseEndElementTask extends RNGParseElementTask{
	SAttribute ns;
	SAttribute datatypeLibrary;
	
	RNGParseEndElementTask(SAttribute ns,
						SAttribute datatypeLibrary, 
						MessageWriter debugWriter){
		super(debugWriter);
		this.ns = ns;
		this.datatypeLibrary = datatypeLibrary;		
	}
	
	protected String getNs(){
		return context.getAttributeValue(ns);
	}
	
	protected String getDatatypeLibrary(){
		return context.getAttributeValue(datatypeLibrary);
	}

	protected Map<String, String> getPrefixMapping(){
		return context.getPrefixMapping();
	}	
	
	protected String getXmlBase(){
		return context.getXmlBase();
	}
	
	protected String getQName(){
		return context.getQName();
	}
	
	protected String getLocation(){
		return context.getStartLocation();
	}
}
