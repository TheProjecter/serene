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

package serene.validation.schema.parsed.components;

import java.util.Map;

import serene.validation.schema.parsed.components.Pattern;

import sereneWrite.MessageWriter;

public abstract class AbstractUniqueChildPattern extends Pattern{
	Pattern child;
	
	public AbstractUniqueChildPattern(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, Pattern child, String qName, String location, 
				MessageWriter debugWriter){		
		super(prefixMapping, xmlBase, ns, datatypeLibrary, qName, location, debugWriter);
		asParent(child);
	}
		
	protected void asParent(Pattern child){		
		this.child = child;
		if(child != null){		
			child.setParent(this);
			child.setChildIndex(0);
		}
	}	
	
	public Pattern getChild(){
		return child;
	}	
		
	public String toString(){
		String s = "Attribute";		
		return s;
	}
}