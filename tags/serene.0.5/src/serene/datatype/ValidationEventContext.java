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

import java.util.ArrayList;
import java.util.HashMap;

import org.relaxng.datatype.ValidationContext;

import sereneWrite.MessageWriter;

public class ValidationEventContext implements  ValidationContext{
	ValidationEventContext parentContext;
	
	String baseURI;
	ArrayList<String> notations;
	ArrayList<String> unparsedEntities;
	
	PrefixMapping prefixMapping;
	
	MessageWriter debugWriter;	
	
	public ValidationEventContext(String baseURI, ValidationEventContext parentContext, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.parentContext = parentContext;
		this.baseURI = baseURI;
		
 		if(parentContext != null) this.prefixMapping = parentContext.getPrefixMapping();
		else prefixMapping = new PrefixMapping(debugWriter);
		
		notations = new ArrayList<String>();
		unparsedEntities = new ArrayList<String>();		
	}
		
	
	PrefixMapping getPrefixMapping(){
		return prefixMapping;
	}
	
	public  ValidationEventContext getParent(){
		return parentContext;
	}
	
	public void startPrefixMapping(String prefix, String uri){
		prefixMapping = prefixMapping.startMapping(prefix, uri);	
	}
	
	public void endPrefixMapping(String prefix){
		prefixMapping = prefixMapping.endMapping(prefix);	
	}
	
	public String getBaseUri(){
		return baseURI;
	}
	
	public boolean isNotation(String notationName){
		return notations.contains(notationName);
	}
	
	public void addUnparsedEntity(String entityName){
		unparsedEntities.add(entityName);
	}
	
	public boolean isUnparsedEntity(String entityName){
		return unparsedEntities.contains(entityName);
	} 
	
	public String resolveNamespacePrefix(String prefix){
		return prefixMapping.getURI(prefix);
	} 
}