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

import serene.validation.schema.Component;

public interface ParsedComponent extends Component{
	ParsedComponent getParent();
	
	void accept(ParsedComponentVisitor v);
	void accept(SimplifyingVisitor v) throws SAXException;
	
	String getNs();
	String getNsAttribute();
	
	String getDatatypeLibrary();
	String getDatatypeLibraryAttribute();
	
	String getNamespaceURI(String prefix);
	String getNamespaceURIAttribute(String prefix);
	Map<String, String> getXmlns();
	
	/**
	* Returns the value of the xml:base attribute if any was present in the
	* corresponding element.
	*/
	String getXmlBaseAttribute();
	
	String getQName();
	String getLocation();
}