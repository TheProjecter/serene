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
import serene.validation.schema.simplified.SimplifiedPattern;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;

import serene.validation.handlers.error.ErrorDispatcher;

class AttributeNamingController extends NamingController{
		
	AttributeNamingController(ControllerPool pool, ErrorDispatcher errorDispatcher){
		super(pool, errorDispatcher);
		
	}
	
	public void recycle(){
		namedPatterns.clear();
		nameClasses.clear();
		compositors.clear();
		compositorPatterns.clear();
		compositorPath.clear();
		compositorPatternPath.clear();
		
		pool.recycle(this);
	}
	   
	void start(SInterleave interleave){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(FORBID_OVERLAP_COMPOSITOR);		
		compositorPatterns.add(interleave);
	}

	void start(SGroup group){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(FORBID_OVERLAP_COMPOSITOR);
		compositorPatterns.add(group);		
	}

	void start(SChoicePattern choice){
		namedPatterns.add(null);
		nameClasses.add(null);
		compositors.add(ALLOW_OVERLAP_COMPOSITOR);
		compositorPatterns.add(choice);		
	}	
		
	
	void reportError(SimplifiedPattern context, int i, int j) throws SAXException{
		SimplifiedPattern a1 = namedPatterns.get(i);
		SimplifiedPattern a2 = namedPatterns.get(j);		
		// error 7.3
        
		String message = "Restrictions 7.3 error. "
		+"In the context of <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)+" overlaping name classes in attributes: "
		+"\n<"+a1.getQName()+"> at "+a1.getLocation(restrictToFileName)
		+"\n<"+a2.getQName()+"> at "+a2.getLocation(restrictToFileName)+".";
		//System.out.println(message);
		errorDispatcher.error(new SAXParseException(message, null));
	}
	
	public String toString(){
		return "AttributeNamingController";
	}	
}