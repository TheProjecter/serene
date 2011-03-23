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

import java.util.Iterator;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

import javax.xml.validation.ValidatorHandler;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.NotationDeclaration;

import javax.xml.namespace.QName;

import serene.Constants;

import sereneWrite.MessageWriter;

class StAXStreamBuildingHandler extends StAXHandler{
    XMLStreamWriter xmlStreamWriter;
    
    StAXStreamBuildingHandler(MessageWriter debugWriter){
        super(debugWriter);
    }
    
    void handle(String systemId, ValidatorHandler validatorHandler, XMLEventReader xmlEventReader, XMLStreamWriter xmlStreamWriter) throws SAXException{
        this.validatorHandler = validatorHandler;
        this.xmlStreamWriter = xmlStreamWriter;
        
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
                    if(--depth >= 0)break loop;
                    break;
                case XMLStreamConstants.CHARACTERS:
                    handleLocation(currentEvent.getLocation());
                    handleCharacters(currentEvent.asCharacters().getData());
                    break;
                case XMLStreamConstants.SPACE:
                    handleLocation(currentEvent.getLocation());
                    handleSpace(currentEvent.asCharacters().getData());
                    break;
                case XMLStreamConstants.CDATA:
                    handleLocation(currentEvent.getLocation());
                    handleCDATA(currentEvent.asCharacters().getData());
                    break;
                case XMLStreamConstants.START_DOCUMENT:
                    ++depth;
                    handleStartDocument((StartDocument)currentEvent);
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    handleEndDocument();
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    handleProcessingInstruction((ProcessingInstruction) currentEvent);
                    break;
                case XMLStreamConstants.COMMENT:
                    handleComment((Comment) currentEvent);
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    handleEntityReference((EntityReference) currentEvent);
                    break;
                case XMLStreamConstants.DTD:
                    handleDTDContext((DTD) currentEvent, (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY));
                    break;
            }
        }
        try{
            xmlStreamWriter.flush();
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
        validatorHandler.endDocument();
    }
    
    void handle(String systemId, ValidatorHandler validatorHandler, XMLStreamReader xmlStreamReader, XMLStreamWriter xmlStreamWriter) throws SAXException{
        this.validatorHandler = validatorHandler;
        this.xmlStreamWriter = xmlStreamWriter;
        
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
                eventType = xmlStreamReader.getEventType();
                switch (eventType) {
                    case XMLStreamConstants.START_ELEMENT:
                        ++depth;
                        handleLocation(xmlStreamReader.getLocation());
                        handleStartElement(xmlStreamReader);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        handleLocation(xmlStreamReader.getLocation());
                        handleEndElement(xmlStreamReader);
                        if(--depth >= 0)break loop;
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        handleLocation(xmlStreamReader.getLocation());
                        // review this, it could be more efficient with char[]
                        handleCharacters(xmlStreamReader.getText());
                        break;
                    case XMLStreamConstants.SPACE:
                        handleLocation(xmlStreamReader.getLocation());
                        // review this, it could be more efficient with char[]
                        handleSpace(xmlStreamReader.getText());
                        break;
                    case XMLStreamConstants.CDATA:
                        handleLocation(xmlStreamReader.getLocation());
                        // review this, it could be more efficient with char[]
                        handleCDATA(xmlStreamReader.getText());
                        break;
                    case XMLStreamConstants.START_DOCUMENT:
                        ++depth;
                        handleStartDocument(xmlStreamReader);
                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        handleEndDocument();
                        break;
                    case XMLStreamConstants.PROCESSING_INSTRUCTION:
                        handleProcessingInstruction(xmlStreamReader);
                        break;
                    case XMLStreamConstants.COMMENT:
                        handleComment(xmlStreamReader);
                        break;
                    case XMLStreamConstants.ENTITY_REFERENCE:
                        handleEntityReference(xmlStreamReader);
                        break;
                    case XMLStreamConstants.DTD:
                        handleDTDContext(xmlStreamReader, (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY));
                        break;
                }                
                eventType = xmlStreamReader.next();
            }
            xmlStreamWriter.flush();
            validatorHandler.endDocument();
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }        
    }
    
    void handleStartElement(StartElement startElement) throws SAXException{
        QName elemName = startElement.getName();
        String namespaceURI = elemName.getNamespaceURI();
        if(namespaceURI == null) namespaceURI = "";
        String localPart = elemName.getLocalPart();
        String prefix = elemName.getPrefix();
        String qName = "";
       
        if(prefix != null && prefix.length() != 0){
            qName = prefix+ ":" + localPart;            
        }else{
            qName = localPart;
        }
                
        try{
            xmlStreamWriter.writeStartElement(prefix != null ? prefix : "", localPart, namespaceURI != null ? namespaceURI : "");
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
        
        Iterator nsIterator = startElement.getNamespaces();
        
        while(nsIterator.hasNext()){
            Namespace ns = (Namespace)nsIterator.next();
            String nsPrefix = ns.getPrefix();
            String nsURI = ns.getNamespaceURI();
            try{
                xmlStreamWriter.writeNamespace(nsPrefix, nsURI);
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
            validatorHandler.startPrefixMapping(nsPrefix, nsURI);
        }
                
        Iterator attrsIterator = startElement.getAttributes();
        attributes.clear();
        while(attrsIterator.hasNext()){
            Attribute attribute = (Attribute)attrsIterator.next();
            String value = attribute.getValue();
            String type = attribute.getDTDType();
            
            QName attrName = attribute.getName();
            String attrNamespaceURI = attrName.getNamespaceURI();
            if(attrNamespaceURI == null) attrNamespaceURI = "";
            String attrLocalPart = attrName.getLocalPart();
            String attrPrefix = attrName.getPrefix();
            String attrQName = "";            
            if(attrPrefix != null && attrPrefix.length() > 0){
                attrQName = attrPrefix+ ":" + attrLocalPart;                
            }else{
                attrQName = attrLocalPart;
            }                     
            try{
                xmlStreamWriter.writeAttribute(attrPrefix != null ? attrPrefix : "", 
                    attrNamespaceURI != null ? attrNamespaceURI : "", 
                    attrLocalPart, value);
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }            
            attributes.addAttribute(attrNamespaceURI, attrLocalPart, attrQName, type, value);            
        }       
        
        validatorHandler.startElement(namespaceURI, localPart, qName, attributes);        
    }
    
    void handleStartElement(XMLStreamReader xmlStreamReader) throws SAXException{
        String namespaceURI = xmlStreamReader.getNamespaceURI();
        if(namespaceURI == null) namespaceURI = "";
        String localPart = xmlStreamReader.getLocalName();
        String prefix = xmlStreamReader.getPrefix();
        String qName = "";
        
        if(prefix != null && prefix.length() != 0){
            qName = prefix+ ":" + localPart;
        }else{
            qName = localPart;
        }
        try{
            xmlStreamWriter.writeStartElement(prefix != null ? prefix : "", localPart, namespaceURI != null ? namespaceURI : "");
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
        
        int nsCount = xmlStreamReader.getNamespaceCount();
        for(int i = 0; i < nsCount; i++){            
            String nsPrefix = xmlStreamReader.getNamespacePrefix(i);
            String nsURI = xmlStreamReader.getNamespaceURI(i);
            try{
                xmlStreamWriter.writeNamespace(nsPrefix, nsURI);
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
            validatorHandler.startPrefixMapping(nsPrefix, nsURI);
        }
        
        int attrCount = xmlStreamReader.getAttributeCount();
        attributes.clear();
        for(int i = 0; i < attrCount; i++){
            String type = xmlStreamReader.getAttributeType(i);
            String value = xmlStreamReader.getAttributeValue(i);
            
            String attrNamespaceURI = xmlStreamReader.getAttributeNamespace(i);
            if(attrNamespaceURI == null) attrNamespaceURI = "";
            String attrPrefix = xmlStreamReader.getAttributePrefix(i);
            String attrLocalPart = xmlStreamReader.getAttributeLocalName(i);
            String attrQName = "";
            if(attrPrefix != null && attrPrefix.length() > 0){
                attrQName = attrPrefix+ ":" + attrLocalPart;
            }else{
                attrQName = attrLocalPart;
            }
            try{
                xmlStreamWriter.writeAttribute(attrPrefix != null ? attrPrefix : "", 
                    attrNamespaceURI != null ? attrNamespaceURI : "", 
                    attrLocalPart, value);
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
            attributes.addAttribute(attrNamespaceURI, attrLocalPart, attrQName, type, value);
        }
        
        validatorHandler.startElement(namespaceURI, localPart, qName, attributes);        
    }
    
    void handleEndElement(EndElement endElement) throws SAXException{        
        super.handleEndElement(endElement);
        try{
            xmlStreamWriter.writeEndElement();
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleEndElement(XMLStreamReader xmlStreamReader) throws SAXException{        
        super.handleEndElement(xmlStreamReader);
        try{
            xmlStreamWriter.writeEndElement();
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleCharacters(String string) throws SAXException{        
        super.handleCharacters(string);
        try{
            xmlStreamWriter.writeCharacters(string);
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleSpace(String string) throws SAXException{        
        super.handleCharacters(string);
        try{
            xmlStreamWriter.writeCharacters(string);
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleCDATA(String string) throws SAXException{        
        super.handleCharacters(string);
        try{
            xmlStreamWriter.writeCData(string);
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleStartDocument(StartDocument startDocument)throws SAXException{
        String version = startDocument.getVersion();
        String encoding = startDocument.getCharacterEncodingScheme();
        try{
            xmlStreamWriter.writeStartDocument(encoding != null ? encoding : "UTF-8",
                version != null ? version : "1.0");
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleStartDocument(XMLStreamReader xmlStreamReader)throws SAXException{
        String version = xmlStreamReader.getVersion();
        String encoding = xmlStreamReader.getCharacterEncodingScheme();
        try{
            xmlStreamWriter.writeStartDocument(encoding != null ? encoding : "UTF-8",
                version != null ? version : "1.0");
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleEndDocument() throws SAXException{
        try{
            xmlStreamWriter.writeEndDocument();
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleProcessingInstruction(ProcessingInstruction processingInstruction) throws SAXException{
        String data = processingInstruction.getData();
        if (data != null && data.length() > 0) {
            try{
                xmlStreamWriter.writeProcessingInstruction(processingInstruction.getTarget(), data);
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
        }
        else{
            try{
                xmlStreamWriter.writeProcessingInstruction(processingInstruction.getTarget());
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
        }        
    }
    
    void handleProcessingInstruction(XMLStreamReader xmlStreamReader) throws SAXException{
        String data = xmlStreamReader.getPIData();
        if (data != null && data.length() > 0) {
            try{
                xmlStreamWriter.writeProcessingInstruction(xmlStreamReader.getPITarget(), data);
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
        }
        else{
            try{
                xmlStreamWriter.writeProcessingInstruction(xmlStreamReader.getPITarget());
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
        }        
    }
    
    void handleComment(Comment comment) throws SAXException{
        try{
            xmlStreamWriter.writeComment(comment.getText());
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleComment(XMLStreamReader xmlStreamReader) throws SAXException{
        try{
            xmlStreamWriter.writeComment(xmlStreamReader.getText());
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleEntityReference(EntityReference entityReference) throws SAXException{
        try{
            xmlStreamWriter.writeEntityRef(entityReference.getName());
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleEntityReference(XMLStreamReader xmlStreamReader) throws SAXException{
        try{
            xmlStreamWriter.writeEntityRef(xmlStreamReader.getLocalName());
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }

    void handleDTDContext(DTD dtd, DTDHandler dtdHandler) throws SAXException{
        super.handleDTDContext(dtd, dtdHandler);
        try{
            xmlStreamWriter.writeDTD(dtd.getDocumentTypeDeclaration());
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleDTDContext(XMLStreamReader xmlStreamReader, DTDHandler dtdHandler) throws SAXException{
        super.handleDTDContext(xmlStreamReader, dtdHandler);
        try{
            xmlStreamWriter.writeDTD(xmlStreamReader.getText());
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }

}


