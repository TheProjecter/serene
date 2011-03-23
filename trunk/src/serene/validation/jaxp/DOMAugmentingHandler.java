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

package serene.validation.jaxp;

import java.util.Stack;

import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Entity;
import org.w3c.dom.Notation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

import javax.xml.XMLConstants;
import javax.xml.validation.ValidatorHandler;

import serene.util.IntStack;

import serene.DocumentContext;
import serene.Constants;

import sereneWrite.MessageWriter;

class DOMAugmentingHandler extends DOMHandler{

    Element currentElement;
    Document resultDocument;
    
    DOMAugmentingHandler(MessageWriter debugWriter){
        super(debugWriter);
    }    
    
	void handle(String systemId, ValidatorHandler validatorHandler, Node sourceNode, Node resultNode) throws SAXException{        
        // TODO
        // What if the result has null node? It will throw some NullPointerException
        currentElement = null;                
        resultDocument = (resultNode.getNodeType() == Node.DOCUMENT_NODE) ? (Document)resultNode : resultNode.getOwnerDocument();        
             
        super.handle(systemId, validatorHandler, sourceNode);
    }
	    
    
	void beginNode(Node node) throws SAXException{
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:		
				handleStartElement(node);
                break;
            case Node.TEXT_NODE:
				handleCharacters(node.getNodeValue());
                break;
            case Node.CDATA_SECTION_NODE:
                handleCharacters(node.getNodeValue());
                break;
            default: 
                break;
        }
    }
	
    
    void handleStartElement(Node element) throws SAXException{
        String namespaceURI = element.getNamespaceURI();
        if(namespaceURI == null) namespaceURI = "";
        
        String qName = element.getNodeName();
        if(qName == null) qName = "";
        
        String localName = element.getLocalName();
        if(localName == null){
            int j = qName.indexOf(':');
            if(j > 0) localName = qName.substring(j+1);
            else localName = "";
        }
        
        NamedNodeMap attrs = element.getAttributes(); 
        handleAttributes(attrs); // fires startPrefixMapping and add copies to currentElement
        
        validatorHandler.startElement(namespaceURI, localName, qName, attributes);
    }
}
