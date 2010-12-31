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

package serene.validation.schema.simplified.components;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.RestrictingVisitor;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import sereneWrite.MessageWriter;

public class SParam extends AbstractSimplifiedComponent{
	String name;
	String charContent;	
	
	public SParam(String name, String charContent, String qName, String location, MessageWriter debugWriter){
		super(qName, location, debugWriter);
		this.charContent = charContent;
		this.name = name; 
	}
	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String getName(){
		return name;
	}	
	public String getCharContent(){
		return charContent;
	}
	
	public String toString(){
		String s = "SParam name " +name+", charContent "+charContent ;
		return s;
	}
}