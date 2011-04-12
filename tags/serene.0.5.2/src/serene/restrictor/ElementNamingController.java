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

class ElementNamingController extends NamingController{
		
	ElementNamingController(ControllerPool pool, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		super(pool, errorDispatcher, debugWriter);
	}
		
	public void recycle(){
		nameClasses.clear();
		namedPatterns.clear();
		compositors.clear();
		compositorPatterns.clear();
		compositorPath.clear();
		compositorPatternPath.clear();
		
		pool.recycle(this);		
	}
	
	void start(SInterleave interleave){
		nameClasses.add(null);
		namedPatterns.add(null);
		compositors.add(FORBID_OVERLAP_COMPOSITOR);	
		compositorPatterns.add(interleave);
	}
	
	void start(SMixed mixed){
		nameClasses.add(null);
		namedPatterns.add(null);
		compositors.add(FORBID_OVERLAP_COMPOSITOR);	
		compositorPatterns.add(mixed);
	}
	
	void start(SGroup group){
		nameClasses.add(null);
		namedPatterns.add(null);
		compositors.add(ALLOW_OVERLAP_COMPOSITOR);
		compositorPatterns.add(group);		
	}
	
	void start(SChoicePattern choice){
		nameClasses.add(null);
		namedPatterns.add(null);
		compositors.add(ALLOW_OVERLAP_COMPOSITOR);
		compositorPatterns.add(choice);
	}

	void reportError(SPattern context, int i, int j) throws SAXException{
		SPattern e1 = namedPatterns.get(i);
		SPattern e2 = namedPatterns.get(j);		
		// error 7.4
		String message = "Restrictions 7.4 error. "
		+"In the context of <"+context.getQName()+"> at "+context.getLocation()+" overlaping name classes in elements: "
		+"\n\t<"+e1.getQName()+"> at "+e1.getLocation()
		+"\n\t<"+e2.getQName()+"> at "+e2.getLocation()+".";
		//System.out.println(message);
		errorDispatcher.error(new SAXParseException(message, null));
	}

	public String toString(){
		return "ElementNamingController";
	}	
}