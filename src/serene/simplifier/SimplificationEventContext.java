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


package serene.simplifier;

import org.relaxng.datatype.ValidationContext;

import serene.validation.PrefixMapping;
import serene.validation.DTDMapping;
import serene.validation.NotationDeclaration;
import serene.validation.EntityDeclaration;

import sereneWrite.MessageWriter;

class SimplificationEventContext implements  ValidationContext{
    
	boolean isBaseURISet; 
	String baseURI;
    
	PrefixMapping prefixMapping;
    DTDMapping dtdMapping;
    
	MessageWriter debugWriter;	
	
    SimplificationEventContext(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		isBaseURISet = false;
        
        prefixMapping = new PrefixMapping(debugWriter);
	}
    	
    public void reset(){
        isBaseURISet = false;
        baseURI = null;
                
        prefixMapping = prefixMapping.reset();//only needed when revious topPattern was not grammar, the rest are clean
        if(dtdMapping != null) dtdMapping.reset();
    }
        
    void setDTDMapping(DTDMapping dtdMapping){
        this.dtdMapping = dtdMapping;
    }
    
    DTDMapping getDTDMapping(){
        return dtdMapping;
    }
    
    void merge(DTDMapping dtdMapping){
        if(this.dtdMapping == null) this.dtdMapping = dtdMapping; 
        this.dtdMapping.merge(dtdMapping);
    }
    
	PrefixMapping getPrefixMapping(){
		return prefixMapping;
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
    public String getBaseUri(){
		return baseURI;
	}
	
	public boolean isNotation(String notationName){
		return dtdMapping.isNotation(notationName);
	}
	    
	public boolean isUnparsedEntity(String entityName){
		return dtdMapping.isUnparsedEntity(entityName);
	} 
	
	public String resolveNamespacePrefix(String prefix){
		return prefixMapping.getURI(prefix);
	}
        
    public String toString(){
        return"SimplificationEventContext ";
    }
}
