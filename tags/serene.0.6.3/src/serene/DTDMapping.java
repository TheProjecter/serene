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
import java.util.Set;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public class DTDMapping implements DTDHandler{
    HashMap<String, NotationDeclaration> notationDeclarations;
    HashMap<String, EntityDeclaration> entityDeclarations;
    
    public DTDMapping(){
		notationDeclarations = new HashMap<String, NotationDeclaration>(); 
        entityDeclarations = new HashMap<String, EntityDeclaration>();
    }
    
    public void reset(){
        notationDeclarations.clear(); 
        entityDeclarations.clear();
    }
    
    public void unparsedEntityDecl(String entityName, String publicId, String systemId, String notationName){
		entityDeclarations.put(entityName, new EntityDeclaration(entityName, publicId, systemId, notationName));
	}
	
    public void notationDecl(String notationName, String publicId, String systemId){
		notationDeclarations.put(notationName, new NotationDeclaration(notationName, publicId, systemId));
	}
	
    
    public boolean isUnparsedEntity(String entityName){
		return entityDeclarations.containsKey(entityName);
	}

    public boolean isNotation(String entityName){
		return notationDeclarations.containsKey(entityName);
	}	
    
    public void merge(DTDMapping other){
        entityDeclarations.putAll(other.entityDeclarations);
        notationDeclarations.putAll(other.notationDeclarations);
    }
    
    void transferMappings(DTDHandler dh) throws SAXException{
        Set<String> names = entityDeclarations.keySet();
        for(String name : names){
            NotationDeclaration nd = notationDeclarations.get(name);
            dh.notationDecl(name, nd.getPublicId(), nd.getSystemId());
        }
        
        names = notationDeclarations.keySet();
        for(String name : names){
            EntityDeclaration ed = entityDeclarations.get(name);
            dh.unparsedEntityDecl(name, ed.getPublicId(), ed.getSystemId(), ed.getNotationName());
        }
    }
}
