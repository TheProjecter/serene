/*
Copyright 2012 Radu Cernuta 

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


package serene.schematron;

import javax.xml.XMLConstants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

import serene.validation.jaxp.SchematronSchemaFactory;

public class TransformerHandlerImpl extends net.sf.saxon.TransformerHandlerImpl{
    static final String QUERY_BINDING = "queryBinding";
    SchematronSchemaFactory schematronSchemaFactory;
    boolean firstElement;
    Locator locator;
    protected TransformerHandlerImpl(net.sf.saxon.Controller controller, SchematronSchemaFactory schematronSchemaFactory){
        super(controller);
        
        this.schematronSchemaFactory = schematronSchemaFactory;
    }
    
    public void startDocument()  throws SAXException{
        firstElement = true;
        super.startDocument();
	}
	
	public void setDocumentLocator(Locator locator){
	    this.locator = locator;
	    super.setDocumentLocator(locator);
	}
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{
        if(firstElement){
            schematronSchemaFactory.setQLB(attributes.getValue(XMLConstants.NULL_NS_URI, QUERY_BINDING), locator);
            firstElement = false;
        }	
        super.startElement(namespaceURI, localName, qName, attributes);
	}
}

