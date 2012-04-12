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

import javax.xml.XMLConstants;

import org.xml.sax.DTDHandler;

import org.relaxng.datatype.ValidationContext;

import serene.dtdcompatibility.InfosetModificationContext;

import sereneWrite.MessageWriter;

public class DocumentContext implements  ValidationContext, InfosetModificationContext, DTDHandler{
    
	protected boolean isBaseURISet; 
	protected String baseURI;
    
	protected PrefixMapping prefixMapping;
    protected DTDMapping dtdMapping;
    
	protected MessageWriter debugWriter;	
	
    public DocumentContext(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		isBaseURISet = false;
        
        prefixMapping = new PrefixMapping(debugWriter);        
	}
    	
    public void reset(){
        isBaseURISet = false;
        baseURI = null;
              
        prefixMapping = prefixMapping.reset();
        dtdMapping = null;
    }
        
    // start DTDHandler
    public void unparsedEntityDecl(String entityName, String publicId, String systemId, String notationName){
        if(dtdMapping == null) dtdMapping = new DTDMapping(debugWriter);		
		dtdMapping.unparsedEntityDecl(entityName, publicId, systemId, notationName);
	}
	
    public void notationDecl(String notationName, String publicId, String systemId){
        if(dtdMapping == null) dtdMapping = new DTDMapping(debugWriter);		
		dtdMapping.notationDecl(notationName, publicId, systemId);
	} 
    // end DTDHandler
    
	/*public PrefixMapping getPrefixMapping(){
		return prefixMapping;
	}*/
	
	public DTDMapping getDTDMapping(){
		return dtdMapping;
	}
    
	public void startPrefixMapping(String prefix, String uri){
		prefixMapping = prefixMapping.startMapping(prefix, uri);		
	}
	
	public void endPrefixMapping(String prefix){
		prefixMapping = prefixMapping.endMapping(prefix);	
	}
	
	public void setBaseURI(String baseURI){
        this.baseURI = baseURI;
        isBaseURISet = true;
    }
    
    public boolean isBaseURISet(){
        return isBaseURISet;
    }
    
    // start ValidationContext
    public String getBaseUri(){
		return baseURI;
	}
	
	public boolean isNotation(String notationName){
	    if(dtdMapping == null) return false;		
		return dtdMapping.isNotation(notationName);
	}
	    
	public boolean isUnparsedEntity(String entityName){
	    if(dtdMapping == null) return false;
		return dtdMapping.isUnparsedEntity(entityName);
	} 
	
	public String resolveNamespacePrefix(String prefix){
		return prefixMapping.getURI(prefix);
	} 
    // end ValidationContext
    
    // start InfosetModificationContext    
    public String getPrefix(String uri){
        String prefix = prefixMapping.getPrefix(uri); 
        if(prefix == null) prefix = XMLConstants.DEFAULT_NS_PREFIX;
        return prefix;
    }
    // end InfosetModificationContext
    
    public String toString(){
        return"ValidationEventContext ";
    }
}
