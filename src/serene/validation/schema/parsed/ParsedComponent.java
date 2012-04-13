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

import serene.bind.util.DocumentIndexedData;

import serene.validation.schema.Component;

public abstract class ParsedComponent implements Component{
	int childIndex;	
	ParsedComponent parent;
			
	int nsRecordIndex;
	int datatypeLibraryRecordIndex;
	
	/*Map<String, String> prefixMapping;*/ 
	int xmlBaseRecordIndex;
	/*								
	String qName;
	String location;
	
    AttributeInfo[] foreignAttributes;*/    
    int recordIndex;
    DocumentIndexedData documentIndexedData;
	
	ParsedComponent(/*Map<String, String> prefixMapping,*/ 
                            int xmlBase,
                            int ns, 
                            int datatypeLibrary,
                            /*AttributeInfo[] foreignAttributes,
                            String qName,
                            String location,*/
                            int recordIndex,
                            DocumentIndexedData documentIndexedData){
		/*this.prefixMapping = prefixMapping;*/ 
		this.xmlBaseRecordIndex = xmlBase;
		this.nsRecordIndex = ns;
		this.datatypeLibraryRecordIndex = datatypeLibrary;
        /*this.foreignAttributes = foreignAttributes;
		this.qName = qName;
		this.location = location;*/
	    this.recordIndex = recordIndex;
	    this.documentIndexedData = documentIndexedData;	
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
		if(nsRecordIndex == DocumentIndexedData.NO_RECORD && parent != null) return parent.getNs();
		return documentIndexedData.getStringValue(nsRecordIndex);
	}
	public String getNsAttribute(){		
		if(nsRecordIndex == DocumentIndexedData.NO_RECORD) return null;
		return documentIndexedData.getStringValue(nsRecordIndex);
	}
	
	public String getDatatypeLibrary(){
		if(datatypeLibraryRecordIndex == DocumentIndexedData.NO_RECORD && parent != null) return parent.getDatatypeLibrary();
		if(datatypeLibraryRecordIndex == DocumentIndexedData.NO_RECORD) return "";
		return documentIndexedData.getStringValue(datatypeLibraryRecordIndex);
	}
	public String getDatatypeLibraryAttribute(){
		if(datatypeLibraryRecordIndex == DocumentIndexedData.NO_RECORD) return null;
		return documentIndexedData.getStringValue(datatypeLibraryRecordIndex);
	}
	
	public String getNamespaceURI(String prefix){
	    Map<String, String> prefixMapping = documentIndexedData.getDeclaredXmlns(recordIndex);
		String uri = null;
		if(prefixMapping != null){
			uri = prefixMapping.get(prefix);
			if(uri!= null)return uri;
		}
		if(parent != null)return parent.getNamespaceURI(prefix);
		return null;
	}
	public String getNamespaceURIAttribute(String prefix){
	    Map<String, String> prefixMapping = documentIndexedData.getDeclaredXmlns(recordIndex);
		if(prefixMapping != null) return prefixMapping.get(prefix);
		return null;
	}
	public Map<String, String> getXmlns(){
		return documentIndexedData.getDeclaredXmlns(recordIndex);
	}
	
	
	/**
	* Returns the value of the xml:base attribute if any was present in the
	* corresponding element.
	*/
	public String getXmlBaseAttribute(){		
		if(xmlBaseRecordIndex == DocumentIndexedData.NO_RECORD) return null;
		return documentIndexedData.getStringValue(xmlBaseRecordIndex);
	}
	
	/*
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
	}*/
    
	void setParent(ParsedComponent parent){		 
		this.parent = parent;				
	}
	
	
	void setChildIndex(int childIndex){			
		this.childIndex = childIndex;			
	}
	
	public int getRecordIndex(){
	    return recordIndex;
	}
	
	public DocumentIndexedData getDocumentIndexedData(){
	    return documentIndexedData;
	}
	
	public String getQName(){
		return documentIndexedData.getItemDescription(recordIndex);
	}
	public String getLocation(boolean restrictToFileName){
	    String si = documentIndexedData.getSystemId(recordIndex);
	    int ln = documentIndexedData.getLineNumber(recordIndex);	    
	    if(ln == DocumentIndexedData.UNKNOWN){
	        if(si == null || !restrictToFileName)return si;
	        return getRestrictedSystemId(si);
	    }
	    
	    int cn = documentIndexedData.getColumnNumber(recordIndex);
	    
        if(si == null || !restrictToFileName){
            return si+":"+ln+":"+cn;
        }
        return getRestrictedSystemId(si)+":"+ln+":"+cn;
	}
	String getRestrictedSystemId(String si){
	    int nameIndex = si.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = si.lastIndexOf('/')+1;
        return si.substring(nameIndex);
	}
	public int getNsRecordIndex(){
	    return nsRecordIndex;
	}
	
	public int getDatatypeLibraryRecordIndex(){
	    return datatypeLibraryRecordIndex;
	}
	
	public int getXmlBaseRecordIndex(){
	    return xmlBaseRecordIndex;
	}
}	
