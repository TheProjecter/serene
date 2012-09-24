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

import serene.Constants;

import serene.util.IntStack;

class DOMHandler extends Handler{
	
    DOMHandler(){
        super();
    }
    
	void handle(String systemId, ValidatorHandler validatorHandler, Node node) throws SAXException{        
        this.validatorHandler = validatorHandler;
        if (node != null) {
			//create the ValidatorHandler			
			if(locator == null){
				locator = new LocatorImpl();
				locator.setSystemId(systemId);
				locator.setPublicId(null);
				locator.setLineNumber(-1);
				locator.setColumnNumber(-1);
			}
			
			validatorHandler.setDocumentLocator(locator);
			//create attributes
			if(attributes == null)attributes = new AttributesImpl();
			//create prefixes stacks
			if(prefixes == null) prefixes = new Stack<String>();
			else prefixes.clear();
			if(prefixesCount == null) prefixesCount = new IntStack();
			else prefixesCount.clear();
            
            // node is a Document or Element, else error was thrown already
            if(node.getNodeType() == Node.ELEMENT_NODE)handleDTDContext(node.getOwnerDocument(), (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY));
            else handleDTDContext((Document)node, (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY));
		}
                
        
		validatorHandler.startDocument();
		
		final Node top = node;	
        // Performs a non-recursive traversal of the DOM. This
        // will avoid a stack overflow for DOMs with high depth.		
        while (node != null) {
            beginNode(node);
            Node next = node.getFirstChild();
            while (next == null) {
                finishNode(node);           
                if (top == node) {
                    break;
                }
                next = node.getNextSibling();
                if (next == null) {
                    node = node.getParentNode();
                    if (node == null || top == node) {
                        if (node != null) {
                            finishNode(node);
                        }
                        next = null;
                        break;
                    }
                }
            }
            node = next;
        }
		
		validatorHandler.endDocument();
    }
	
    
    void handleDTDContext(Document document, DTDHandler dtdHandler) throws SAXException{
        DocumentType doctype = document.getDoctype();
        if(doctype == null) return;
        NamedNodeMap entities = doctype.getEntities();
        for(int i = 0; i < entities.getLength(); i++){
            Entity entity = null;
            try{
                entity = (Entity)entities.item(i);
            }catch(ClassCastException e){}
            String notationName = entity.getNotationName(); 
            if(notationName != null){ // unparsed entity
                dtdHandler.unparsedEntityDecl(entity.getNodeName(), entity.getPublicId(), entity.getSystemId(), notationName);
            }
        }
        
        NamedNodeMap notations = doctype.getNotations();
        for(int i = 0; i < notations.getLength(); i++){
            Notation notation = null;
            try{
                notation = (Notation)notations.item(i);
            }catch(ClassCastException e){}
            dtdHandler.notationDecl(notation.getNodeName(), notation.getPublicId(), notation.getSystemId());            
        }
        
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
        handleAttributes(attrs); // fires startPrefixMapping
        
        validatorHandler.startElement(namespaceURI, localName, qName, attributes);
    }
    
	void handleAttributes(NamedNodeMap attrMap) throws SAXException{		
        final int attrCount = attrMap.getLength();        
		attributes.clear();
		String type = "CDATA";
		int prefixCount = 0;
        for (int i = 0; i < attrCount; ++i) {			
            Attr attr = (Attr) attrMap.item(i);
			
			String namespaceURI = attr.getNamespaceURI();
			if(namespaceURI == null) namespaceURI = "";
			
			String qName = attr.getNodeName();
			if(qName == null) qName = "";
			
			String localName = attr.getLocalName();
			if(localName == null){
				int j = qName.indexOf(':');
				if(j > 0) localName = qName.substring(j+1);
				else localName = "";
			}
			
            String value = attr.getValue();
            if (value == null) value = "";
			
            if (namespaceURI == XMLConstants.XML_NS_URI) {
				prefixCount++;
                							
                if (localName.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                    validatorHandler.startPrefixMapping("", value);
					prefixes.push("");
                }
                else {
                    validatorHandler.startPrefixMapping(localName, value);
					prefixes.push(localName);
                }				
            }else{
				attributes.addAttribute(namespaceURI, localName, qName, type, value);
			}
        }
		prefixesCount.push(prefixCount);
    }

	void handleCharacters(String string) throws SAXException{
        if (string != null) {
            int length = string.length();
            int remainder = length & CHUNK_MASK;
            if (remainder > 0) {
                string.getChars(0, remainder, chars, 0);
                validatorHandler.characters(chars, 0, remainder);
            }
            int i = remainder;
            while (i < length) {
                string.getChars(i, i += CHUNK_SIZE, chars, 0);
                validatorHandler.characters(chars, 0, CHUNK_SIZE);
            }
        }
    }
	
	void finishNode(Node node) throws SAXException{
        if (node.getNodeType() == Node.ELEMENT_NODE) {
			String namespaceURI = node.getNamespaceURI();
			if(namespaceURI == null) namespaceURI = "";
			
			String qName = node.getNodeName();
			if(qName == null) qName = "";
			
			String localName = node.getLocalName();
			if(localName == null){
				int j = qName.indexOf(':');
				if(j > 0) localName = qName.substring(j+1);
				else localName = "";
			}	
			validatorHandler.endElement(namespaceURI, localName, qName);
			int prefixCount = prefixesCount.pop();
			for(int i = 0; i < prefixCount; i++){
				String prefix = prefixes.pop();
				validatorHandler.endPrefixMapping(prefix);
			}			
        }
    }

}
