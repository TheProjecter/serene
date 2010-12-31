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

import serene.validation.schema.parsed.ParsedComponentVisitor;
import serene.validation.schema.parsed.ParsedComponent;

import sereneWrite.MessageWriter;

public abstract class AbstractParsedComponent implements ParsedComponent{
	protected int childIndex;	
	protected ParsedComponent parent;
			
	protected String ns;
	protected String datatypeLibrary;
	
	protected Map<String, String> prefixMapping; 
	protected String xmlBase;
									
	protected String qName;
	protected String location;
	
	protected MessageWriter debugWriter;	
	
	public AbstractParsedComponent(Map<String, String> prefixMapping, 
									String xmlBase, 
									String ns, 
									String datatypeLibrary,
									String qName,
									String location,
									MessageWriter debugWriter){		
		this.debugWriter = debugWriter;
		this.prefixMapping = prefixMapping; 
		this.xmlBase = xmlBase;		
		this.ns = ns;
		this.datatypeLibrary = datatypeLibrary;
		this.qName = qName;
		this.location = location;
		
		childIndex = -1;
	}

	void setParent(ParsedComponent parent){		 
		this.parent = parent;
	}
	
	public ParsedComponent getParent(){
		return parent;
	}
	
	void setChildIndex(int childIndex){			
		this.childIndex = childIndex;
	}	
	
	public int getChildIndex(){
		return childIndex;
	}	
		
	public String getNs(){
		if(ns == null && parent != null) return parent.getNs();
		return ns;
	}
	public String getNsAttribute(){		
		return ns;
	}
	
	public String getDatatypeLibrary(){
		if(datatypeLibrary == null && parent != null) return parent.getDatatypeLibrary();
		if(datatypeLibrary == null) return "";
		return datatypeLibrary;
	}
	
	public String getDatatypeLibraryAttribute(){
		return datatypeLibrary;
	}
	
	public String getNamespaceURI(String prefix){
		String uri = null;
		if(prefixMapping != null){
			uri = prefixMapping.get(prefix);
			if(uri!= null)return uri;
		}
		if(parent != null)return parent.getNamespaceURI(prefix);
		return null;
	}
	public String getNamespaceURIAttribute(String prefix){
		if(prefixMapping != null) return prefixMapping.get(prefix);
		return null;
	}
	public Map<String, String> getXmlns(){
		return prefixMapping;
	}
	public String getXmlBaseAttribute(){
		return xmlBase;
	}
	
	public String getQName(){
		return qName;
	}
	
	public String getLocation(){
		return location;
	}
}	