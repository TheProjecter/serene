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


package serene.validation.schema.parsed;

import java.util.Map;

import java.io.File;

import org.xml.sax.SAXException;

import serene.util.AttributeInfo;

import serene.validation.schema.Component;

import sereneWrite.MessageWriter;

public abstract class ParsedComponent implements Component{
	int childIndex;	
	ParsedComponent parent;
			
	String ns;
	String datatypeLibrary;
	
	Map<String, String> prefixMapping; 
	String xmlBase;
									
	String qName;
	String location;
	
    AttributeInfo[] foreignAttributes;
	MessageWriter debugWriter;	
	
	ParsedComponent(Map<String, String> prefixMapping, 
									String xmlBase, 
									String ns, 
									String datatypeLibrary,
                                    AttributeInfo[] foreignAttributes,
									String qName,
									String location,
									MessageWriter debugWriter){		
		this.debugWriter = debugWriter;
		this.prefixMapping = prefixMapping; 
		this.xmlBase = xmlBase;		
		this.ns = ns;
		this.datatypeLibrary = datatypeLibrary;
        this.foreignAttributes = foreignAttributes;
		this.qName = qName;
		this.location = location;
		
		childIndex = -1;
	}
    
    // START Component
    public int getChildIndex(){
		return childIndex;
	}
    
    public ParsedComponent getParent(){
		return parent;
	}
    // END Component
	
	abstract public void accept(ParsedComponentVisitor v);
	abstract public void accept(SimplifyingVisitor v) throws SAXException;
	
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
	
	/**
	* Returns the value of the xml:base attribute if any was present in the
	* corresponding element.
	*/
	public String getXmlBaseAttribute(){
		return xmlBase;
	}
	
    public AttributeInfo[] getForeignAttributes(){
        return foreignAttributes;
    }
    
	public String getQName(){
		return qName;
	}
	
	public String getLocation(){
		return location;
	}
    public String getLocation(boolean restrictToFileName){
        if(location == null || !restrictToFileName)return location;
        int nameIndex = location.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = location.lastIndexOf('/')+1;
        return location.substring(nameIndex);
	}
    
	void setParent(ParsedComponent parent){		 
		this.parent = parent;				
	}
	
	
	void setChildIndex(int childIndex){			
		this.childIndex = childIndex;			
	}
}	
