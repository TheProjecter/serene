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

package serene.restrictor;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import serene.validation.schema.simplified.components.SNameClass;
import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SGroup;

import serene.validation.handlers.error.ErrorDispatcher;

import sereneWrite.MessageWriter;

class AttributeLimitationNamingController extends LimitationNamingController{
		
	AttributeLimitationNamingController(ControllerPool pool, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		super(pool, errorDispatcher, debugWriter);
		
	}
	
	public void recycle(){
		namedPatterns.clear();
		nameClasses.clear();
		compositors.clear();
		compositorPatterns.clear();
		compositorPath.clear();
		compositorPatternPath.clear();
		
		compositorRelevance.clear();
		cardinalityRelevance.clear();
		
		pool.recycle(this);
	}
	
	
		
	
	void reportError(SPattern context, int i, int j) throws SAXException{
		SPattern a1 = namedPatterns.get(i);
		SPattern a2 = namedPatterns.get(j);		
		// error 7.3
		String message = "Unsupported schema configuration. "
		+"For the moment, Serene does not support overlapping name classes in attributes in the context of a <group> that has multiple cardinality and is in the context of an <interleave>:"	
		+"\n\t<"+a1.getQName()+"> at "+a1.getLocation()
		+"\n\t<"+a2.getQName()+"> at "+a2.getLocation()+".";
		errorDispatcher.error(new SAXParseException(message, null));
	}
	
	public String toString(){
		return "AttributeNamingController";
	}	
}