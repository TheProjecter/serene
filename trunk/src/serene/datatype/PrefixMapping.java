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

package serene.datatype;

import java.util.HashMap;

import sereneWrite.MessageWriter;
class PrefixMapping{
	HashMap<String, String> mapping;
	PrefixMapping parent;
	MessageWriter debugWriter;	
	PrefixMapping(MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
		mapping = new HashMap<String, String>();
	}
	
	PrefixMapping(PrefixMapping parent, MessageWriter debugWriter){
		this.debugWriter = debugWriter;	
		this.parent = parent;
		mapping = new HashMap<String, String>();
	}
	
	PrefixMapping startMapping(String prefix, String uri){
		if(mapping.containsKey(prefix)){
			PrefixMapping newMapping = new PrefixMapping(this, debugWriter);
			newMapping.startMapping(prefix, uri);
			return newMapping;
		}
		mapping.put(prefix, uri);
		return this;
	}
		
	PrefixMapping endMapping(String prefix){
		mapping.remove(prefix);
		if(mapping.isEmpty())return parent;
		return this;
	}
	
	String getURI(String prefix){
		String uri = mapping.get(prefix);
		if(uri == null) return uri;
		if(parent != null) return parent.getURI(prefix);
		return null;
	}
}