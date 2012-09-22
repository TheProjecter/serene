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

import serene.validation.schema.simplified.SNameClass;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SChoicePattern;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SGroup;

import serene.validation.handlers.error.ErrorDispatcher;

class AttributeLimitationNamingController extends LimitationNamingController{
		
	AttributeLimitationNamingController(ControllerPool pool, ErrorDispatcher errorDispatcher){
		super(pool, errorDispatcher);
		
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
		+"For the moment serene does not support overlapping name classes in attributes in the context of a <group> that has multiple cardinality and is in the context of an <interleave>:"	
		+"\n<"+a1.getQName()+"> at "+a1.getLocation(restrictToFileName)
		+"\n<"+a2.getQName()+"> at "+a2.getLocation(restrictToFileName)+".";
		errorDispatcher.error(new SAXParseException(message, null));
	}
	
	public String toString(){
		return "AttributeNamingController";
	}	
}