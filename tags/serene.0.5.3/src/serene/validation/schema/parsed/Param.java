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

import org.xml.sax.SAXException;

import serene.util.AttributeInfo;
import sereneWrite.MessageWriter;

public class Param extends ParsedComponent{
	String name;
	String charContent;		
	Param(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String name, AttributeInfo[] foreignAttributes, String charContent, String qName, String location, MessageWriter debugWriter){
		super(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, qName, location, debugWriter);
		this.charContent = charContent;
		this.name = name; 
	}
	
	public void accept(ParsedComponentVisitor v){
		v.visit(this);
	}
	
	public void accept(SimplifyingVisitor v)throws SAXException{
        v.visit(this);
    }
	
	public String getName(){
		return name;
	}	
	public String getCharacterContent(){
		return charContent;
	}
	
	public String toString(){
		String s = "Param name " +name+", charContent "+charContent ;
		return s;
	}
}
