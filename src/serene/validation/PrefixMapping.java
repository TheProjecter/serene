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

package serene.validation;

import java.util.HashMap;

import javax.xml.XMLConstants;

import sereneWrite.MessageWriter;
public class PrefixMapping{
	HashMap<String, String> mapping;
	PrefixMapping parent;
	MessageWriter debugWriter;	
    
	public PrefixMapping(MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
		mapping = new HashMap<String, String>();
        mapping.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
	}
	
	public PrefixMapping(PrefixMapping parent, MessageWriter debugWriter){
		this.debugWriter = debugWriter;	
		this.parent = parent;
		mapping = new HashMap<String, String>();
        mapping.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
	}
	
    public PrefixMapping reset(){
        if(parent != null) return parent.reset();
        mapping.clear();
        mapping.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        return this;
    }
    
	public PrefixMapping startMapping(String prefix, String uri){
		if(mapping.containsKey(prefix)){
            if(prefix.equals(XMLConstants.XML_NS_PREFIX)) return this;
			PrefixMapping newMapping = new PrefixMapping(this, debugWriter);
			newMapping.startMapping(prefix, uri);
			return newMapping;
		}
		mapping.put(prefix, uri);
		return this;
	}
		
	public PrefixMapping endMapping(String prefix){
		mapping.remove(prefix);
		if(mapping.size() == 1 && parent != null){
            return parent;
        }
		return this;
	}
	
	public String getURI(String prefix){
		String uri = mapping.get(prefix);
		if(uri != null) return uri;
		if(parent != null) return parent.getURI(prefix);
		return null;
	}
    
    public String toString(){
        return " MAPPING "+mapping+" PARENT "+parent;
    }
}