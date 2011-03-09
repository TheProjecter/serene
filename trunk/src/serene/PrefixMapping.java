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



package serene;

import java.util.HashMap;

import javax.xml.XMLConstants;

import sereneWrite.MessageWriter;
public class PrefixMapping{
	HashMap<String, String> prefixMapping;
    HashMap<String, String> uriMapping;
	PrefixMapping parent;
	MessageWriter debugWriter;	
    
	public PrefixMapping(MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
		prefixMapping = new HashMap<String, String>();
        prefixMapping.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        
        uriMapping = new HashMap<String, String>();
        uriMapping.put(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
	}
	
	public PrefixMapping(PrefixMapping parent, MessageWriter debugWriter){
		this.debugWriter = debugWriter;	
		this.parent = parent;
		prefixMapping = new HashMap<String, String>();
        prefixMapping.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        
        uriMapping = new HashMap<String, String>();
        uriMapping.put(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
	}
	
    public PrefixMapping reset(){
        if(parent != null) return parent.reset();
        prefixMapping.clear();
        prefixMapping.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        
        uriMapping.clear();
        uriMapping.put(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
        return this;
    }
    
	public PrefixMapping startMapping(String prefix, String uri){
		if(prefixMapping.containsKey(prefix)){
            if(prefix.equals(XMLConstants.XML_NS_PREFIX)) return this;
			PrefixMapping newMapping = new PrefixMapping(this, debugWriter);
			newMapping.startMapping(prefix, uri);
			return newMapping;
		}
		prefixMapping.put(prefix, uri);
        uriMapping.put(uri, prefix);
		return this;
	}
		
	public PrefixMapping endMapping(String prefix){
		String uri = prefixMapping.remove(prefix);
        uriMapping.remove(uri);        
		if(prefixMapping.size() == 1 && parent != null){
            return parent;
        }
		return this;
	}
	
	public String getURI(String prefix){
		String uri = prefixMapping.get(prefix);
		if(uri != null) return uri;
		if(parent != null) return parent.getURI(prefix);
		return null;
	}
    
    public String getPrefix(String uri){
		String prefix = uriMapping.get(uri);
		if(prefix != null) return prefix;
		if(parent != null) return parent.getPrefix(uri);
		return null;
	}
    
    public String toString(){
        return "PREFIX MAPPING "+prefixMapping+" "+uriMapping+" PARENT "+parent;
    }
}
