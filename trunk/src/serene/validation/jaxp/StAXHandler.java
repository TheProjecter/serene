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
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

import javax.xml.validation.ValidatorHandler;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.NotationDeclaration;

import javax.xml.namespace.QName;

import serene.Constants;


import serene.util.IntStack;

import sereneWrite.MessageWriter;

class StAXHandler extends Handler{
    int depth = 0;
    
    StAXHandler(MessageWriter debugWriter){
        super(debugWriter);
    }
    
    void handle(String systemId, ValidatorHandler validatorHandler, XMLEventReader xmlEventReader) throws SAXException{
        this.validatorHandler = validatorHandler;
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
        
        validatorHandler.startDocument();
        
        XMLEvent currentEvent;
        int eventType;
        depth = 0;
        loop: while (xmlEventReader.hasNext()) {
            try{
                currentEvent = xmlEventReader.nextEvent();
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
            eventType = currentEvent.getEventType();
            switch (eventType) {
                case XMLStreamConstants.START_ELEMENT:
                    ++depth;
                    handleLocation(currentEvent.getLocation());
                    handleStartElement(currentEvent.asStartElement());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    handleLocation(currentEvent.getLocation());
                    handleEndElement(currentEvent.asEndElement());
                    if(--depth <= 0)break loop;
                    break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.SPACE:                   
                case XMLStreamConstants.CDATA:
                    handleLocation(currentEvent.getLocation());
                    handleCharacters(currentEvent.asCharacters().getData());
                    break;
                case XMLStreamConstants.START_DOCUMENT:
                    ++depth;
                    break;
                case XMLStreamConstants.DTD:
                    handleDTDContext((DTD) currentEvent, (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY));
                    break;
            }
        }
        validatorHandler.endDocument();
    }
    
    void handle(String systemId, ValidatorHandler validatorHandler, XMLStreamReader xmlStreamReader) throws SAXException{
        this.validatorHandler = validatorHandler;
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
        
        validatorHandler.startDocument();
        
        int eventType;
        depth = 0;
        try{
            loop: while (xmlStreamReader.hasNext()) {
                //eventType = xmlStreamReader.getEventType();
                eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLStreamConstants.START_ELEMENT:
                        ++depth;
                        handleLocation(xmlStreamReader.getLocation());
                        handleStartElement(xmlStreamReader);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        handleLocation(xmlStreamReader.getLocation());
                        handleEndElement(xmlStreamReader);
                        if(--depth <= 0)break loop;
                        break;
                    case XMLStreamConstants.CHARACTERS:
                    case XMLStreamConstants.SPACE:
                    case XMLStreamConstants.CDATA:
                        handleLocation(xmlStreamReader.getLocation());
                        // review this, it could be more efficient with char[]
                        handleCharacters(xmlStreamReader.getText());
                        break;
                    case XMLStreamConstants.START_DOCUMENT:
                        ++depth;
                        break;
                    case XMLStreamConstants.DTD:
                        handleDTDContext(xmlStreamReader, (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY));
                        break;
                }
            }
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
        validatorHandler.endDocument();
    }
    
    
    void handleLocation(Location location){
        locator.setSystemId(location.getSystemId());
        locator.setPublicId(location.getPublicId());
        locator.setLineNumber(location.getLineNumber());
        locator.setColumnNumber(location.getColumnNumber());
    }
    
    void handleStartElement(StartElement startElement) throws SAXException{
        Iterator nsIterator = startElement.getNamespaces();
        while(nsIterator.hasNext()){
            Namespace ns = (Namespace)nsIterator.next();
            validatorHandler.startPrefixMapping(ns.getPrefix(), ns.getNamespaceURI());
        }
        
        Iterator attrsIterator = startElement.getAttributes();
        attributes.clear();
        while(attrsIterator.hasNext()){
            Attribute attribute = (Attribute)attrsIterator.next();
            QName attrName = attribute.getName();
            String namespaceURI = attrName.getNamespaceURI();
            if(namespaceURI == null) namespaceURI = "";
            String localPart = attrName.getLocalPart();
            String prefix = attrName.getPrefix();
            String qName = "";
            if(prefix != null && !prefix.equals("")){
                qName = prefix+ ":" + localPart;
            }else{
                qName = localPart;   
            }
            String value = attribute.getValue();
            String type = attribute.getDTDType();
            attributes.addAttribute(namespaceURI, localPart, qName, type, value);
        }        
        QName elemName = startElement.getName();
        String namespaceURI = elemName.getNamespaceURI();
        if(namespaceURI == null) namespaceURI = "";
        String localPart = elemName.getLocalPart();
        String prefix = elemName.getPrefix();
        String qName = "";
        if(prefix != null && !prefix.equals("")){
            qName = prefix+ ":" + localPart;
        }else{
            qName = localPart;   
        }
        validatorHandler.startElement(namespaceURI, localPart, qName, attributes);
    }


    void handleStartElement(XMLStreamReader xmlStreamReader) throws SAXException{
        int nsCount = xmlStreamReader.getNamespaceCount();
        for(int i = 0; i < nsCount; i++){            
            validatorHandler.startPrefixMapping(xmlStreamReader.getNamespacePrefix(i), xmlStreamReader.getNamespaceURI(i));
        }
        
        int attrCount = xmlStreamReader.getAttributeCount();
        attributes.clear();
        for(int i = 0; i < attrCount; i++){
            String namespaceURI = xmlStreamReader.getAttributeNamespace(i);
            if(namespaceURI == null)namespaceURI="";
            String prefix = xmlStreamReader.getAttributePrefix(i);
            String localPart = xmlStreamReader.getAttributeLocalName(i);
            String qName = "";
            if(prefix != null && !prefix.equals("")){
                qName = prefix+ ":" + localPart;
            }else{
                qName = localPart;   
            }
            String type = xmlStreamReader.getAttributeType(i);
            String value = xmlStreamReader.getAttributeValue(i);
            attributes.addAttribute(namespaceURI, localPart, qName, type, value);
        }
        String namespaceURI = xmlStreamReader.getNamespaceURI();
        if(namespaceURI == null)namespaceURI="";
        String localPart = xmlStreamReader.getLocalName();
        String prefix = xmlStreamReader.getPrefix();
        String qName = "";
        if(prefix != null && !prefix.equals("")){
            qName = prefix+ ":" + localPart;
        }else{
            qName = localPart;   
        }
        validatorHandler.startElement(namespaceURI, localPart, qName, attributes);
    }    
 
    void handleEndElement(EndElement endElement) throws SAXException{        
        QName elemName = endElement.getName();
        String namespaceURI = elemName.getNamespaceURI();
        if(namespaceURI == null) namespaceURI = "";
        String localPart = elemName.getLocalPart();
        String prefix = elemName.getPrefix();
        String qName = "";
        if(prefix != null && !prefix.equals("")){
            qName = prefix+ ":" + localPart;
        }else{
            qName = localPart;   
        }
        validatorHandler.endElement(namespaceURI, localPart, qName);
        
        Iterator nsIterator = endElement.getNamespaces();
        while(nsIterator.hasNext()){
            Namespace ns = (Namespace)nsIterator.next();
            validatorHandler.endPrefixMapping(ns.getPrefix());
        }
    }
    
    
    void handleEndElement(XMLStreamReader xmlStreamReader) throws SAXException{
        String namespaceURI = xmlStreamReader.getNamespaceURI();
        if(namespaceURI == null)namespaceURI="";
        String localPart = xmlStreamReader.getLocalName();
        String prefix = xmlStreamReader.getPrefix();
        String qName = "";
        if(prefix != null && !prefix.equals("")){
            qName = prefix+ ":" + localPart;
        }else{
            qName = localPart;   
        }
        validatorHandler.endElement(namespaceURI, localPart, qName);
        
        int nsCount = xmlStreamReader.getNamespaceCount();
        for(int i = 0; i < nsCount; i++){            
            validatorHandler.endPrefixMapping(xmlStreamReader.getNamespacePrefix(i));
        }
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
    
    void handleDTDContext(DTD dtd, DTDHandler dtdHandler) throws SAXException{
        List entities = dtd.getEntities();
        for(int i = 0; i < entities.size(); i++){
            EntityDeclaration entity = (EntityDeclaration)entities.get(i);
            if(entity.getNotationName() != null){//unparsed entity
                dtdHandler.unparsedEntityDecl(entity.getName(), entity.getPublicId(), entity.getSystemId(), entity.getNotationName());
            }
        }
        
        List notations = dtd.getNotations();
        for(int i = 0; i < notations.size(); i++){
            NotationDeclaration notation = (NotationDeclaration)notations.get(i);
            dtdHandler.notationDecl(notation.getName(), notation.getPublicId(), notation.getSystemId());            
        }
    }    
    
    void handleDTDContext(XMLStreamReader xmlStreamReader, DTDHandler dtdHandler) throws SAXException{
        List entities = (List)xmlStreamReader.getProperty("javax.xml.stream.entities");
        for(int i = 0; i < entities.size(); i++){
            EntityDeclaration entity = (EntityDeclaration)entities.get(i);
            if(entity.getNotationName() != null){//unparsed entity
                dtdHandler.unparsedEntityDecl(entity.getName(), entity.getPublicId(), entity.getSystemId(), entity.getNotationName());
            }
        }
        
        List notations = (List)xmlStreamReader.getProperty("javax.xml.stream.notations");
        for(int i = 0; i < notations.size(); i++){
            NotationDeclaration notation = (NotationDeclaration)notations.get(i);
            dtdHandler.notationDecl(notation.getName(), notation.getPublicId(), notation.getSystemId());            
        }
    }    
}
