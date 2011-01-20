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

import org.xml.sax.SAXException;

import serene.validation.schema.parsed.components.Pattern;

import serene.validation.schema.parsed.ParsedComponentVisitor;
import serene.validation.schema.parsed.SimplifyingVisitor;

import sereneWrite.MessageWriter;

public class ListPattern extends AbstractMultipleChildrenPattern{
	
	public ListPattern(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, Pattern[] children, String qName, String location, MessageWriter debugWriter){
		super(prefixMapping, xmlBase, ns, datatypeLibrary, children, qName, location, debugWriter);
	}	
			
	public void accept(ParsedComponentVisitor v){
		v.visit(this);
	}	
	
	public void accept(SimplifyingVisitor v) throws SAXException{
		v.visit(this);
	}
	
	public String toString(){
		String s = "List";		
		return s;
	}
}