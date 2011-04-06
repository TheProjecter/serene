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
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

import javax.xml.validation.ValidatorHandler;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventFactory;

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

import serene.dtdcompatibility.AttributeDefaultValueHandler;
import serene.dtdcompatibility.AttributeIdTypeHandler;

import serene.validation.jaxp.util.NamespacesIterator;
import serene.validation.jaxp.util.AttributesIterator;

import serene.DocumentContext;

import serene.Constants;

import sereneWrite.MessageWriter;

class StAXEventBuildingHandler extends StAXHandler{
    XMLEventWriter xmlEventWriter;
    XMLEventFactory xmlEventFactory;
    
    Stack<Iterator> namespaceIteratorStack;
    HashMap<String, EntityDeclaration> entityDeclarations;
    
    AttributeDefaultValueHandler attributeDefaultValueHandler;
    AttributeIdTypeHandler attributeIdTypeHandler;
    
    boolean level2AttributeDefaultValue;    
    boolean level2AttributeIdType;
    
    DocumentContext documentContext;
    
    StAXEventBuildingHandler(MessageWriter debugWriter){
        super(debugWriter);
        xmlEventFactory = xmlEventFactory.newInstance();
        
        namespaceIteratorStack = new Stack<Iterator>();
        entityDeclarations = new HashMap<String, EntityDeclaration>();
    }

    void setLevel2AttributeDefaultValue(boolean level2AttributeDefaultValue){
        this.level2AttributeDefaultValue = level2AttributeDefaultValue;
    }

    void setLevel2AttributeIdType(boolean level2AttributeIdType){
        this.level2AttributeIdType = level2AttributeIdType;
    }    
    
    void handle(String systemId, ValidatorHandler validatorHandler, XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws SAXException{
        this.validatorHandler = validatorHandler;
        this.xmlEventWriter = xmlEventWriter;
        
        attributeDefaultValueHandler = (AttributeDefaultValueHandler)validatorHandler.getProperty(Constants.ATTRIBUTE_DEFAULT_VALUE_HANDLER_PROPERTY);
        attributeIdTypeHandler = (AttributeIdTypeHandler)validatorHandler.getProperty(Constants.ATTRIBUTE_ID_TYPE_HANDLER_PROPERTY);
        documentContext = (DocumentContext)validatorHandler.getProperty(Constants.DOCUMENT_CONTEXT_PROPERTY);
        
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
        try{
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
                        super.handleEndElement(currentEvent.asEndElement());
                        xmlEventWriter.add(currentEvent);
                        if(--depth <= 0)break loop;
                        break;
                    case XMLStreamConstants.CHARACTERS:                    
                    case XMLStreamConstants.SPACE:                   
                    case XMLStreamConstants.CDATA:
                        handleLocation(currentEvent.getLocation());
                        super.handleCharacters(currentEvent.asCharacters().getData());
                        xmlEventWriter.add(currentEvent);
                        break;
                    case XMLStreamConstants.DTD:
                        handleDTDContext((DTD) currentEvent, (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY));                    
                        break;
                    default:
                        xmlEventWriter.add(currentEvent);
                }
            }
            validatorHandler.endDocument();
            xmlEventWriter.flush();
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handle(String systemId, ValidatorHandler validatorHandler, XMLStreamReader xmlStreamReader, XMLEventWriter xmlEventWriter) throws SAXException{
        this.validatorHandler = validatorHandler;
        this.xmlEventWriter = xmlEventWriter;
        
        attributeDefaultValueHandler = (AttributeDefaultValueHandler)validatorHandler.getProperty(Constants.ATTRIBUTE_DEFAULT_VALUE_HANDLER_PROPERTY);
        attributeIdTypeHandler = (AttributeIdTypeHandler)validatorHandler.getProperty(Constants.ATTRIBUTE_ID_TYPE_HANDLER_PROPERTY);
        documentContext = (DocumentContext)validatorHandler.getProperty(Constants.DOCUMENT_CONTEXT_PROPERTY);
        
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
                        if(--depth <= 0)break loop;
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
            validatorHandler.endDocument();
            xmlEventWriter.flush();            
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }        
    }
    
    
    void handleStartElement(StartElement startElement) throws SAXException{
        QName elemName = startElement.getName();
        String namespaceURI = elemName.getNamespaceURI();
        String localPart = elemName.getLocalPart();
        String prefix = elemName.getPrefix();
        String qName = "";
        if(prefix != null && prefix.length() != 0){
            qName = prefix+ ":" + localPart;            
        }else{
            qName = localPart;
        }        
        
        Iterator nsIterator = startElement.getNamespaces();
        ArrayList<Namespace> nsList = new ArrayList<Namespace>();  
        while(nsIterator.hasNext()){
            Namespace ns = (Namespace)nsIterator.next();
            nsList.add(ns);
            String nsPrefix = ns.getPrefix();
            String nsURI = ns.getNamespaceURI();
            try{
                xmlEventWriter.add(xmlEventFactory.createNamespace(nsPrefix, nsURI));
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
            validatorHandler.startPrefixMapping(nsPrefix, nsURI);
        }        
        
        ArrayList<Attribute> attrList = new ArrayList<Attribute>(); 
        Iterator attrsIterator = startElement.getAttributes();
        attributes.clear();
        while(attrsIterator.hasNext()){
            Attribute attribute = (Attribute)attrsIterator.next();
            attrList.add(attribute);
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
            attributes.addAttribute(attrNamespaceURI, attrLocalPart, attrQName, type, value);            
        }
        validatorHandler.startElement(namespaceURI, localPart, qName, attributes);
        
        if(level2AttributeDefaultValue){            
            attributeDefaultValueHandler.handle(namespaceURI, localPart, attributes, attrList, documentContext);
        }
        
        if(level2AttributeIdType){            
            attributeIdTypeHandler.handle(namespaceURI, localPart, attrList, startElement.getLocation());
        }
        
        Iterator namespacesIterator = new NamespacesIterator(nsList, debugWriter);
        namespaceIteratorStack.push(namespacesIterator);
        try{
            xmlEventWriter.add(xmlEventFactory.createStartElement(elemName,
                                                                new AttributesIterator(attrList, debugWriter),
                                                                namespacesIterator));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }        
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
        QName elemName;
        if(prefix != null){
            elemName = new QName(namespaceURI, localPart, prefix);
        }else{
            elemName = new QName(namespaceURI, localPart);
        }
        
        int nsCount = xmlStreamReader.getNamespaceCount();
        ArrayList<Namespace> nsList = new ArrayList<Namespace>();
        for(int i = 0; i < nsCount; i++){            
            String nsPrefix = xmlStreamReader.getNamespacePrefix(i);
            String nsURI = xmlStreamReader.getNamespaceURI(i);
            try{
                xmlEventWriter.add(xmlEventFactory.createNamespace(nsPrefix, nsURI));
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
            Namespace ns = xmlEventFactory.createNamespace(nsPrefix, nsURI);
            nsList.add(ns);
            validatorHandler.startPrefixMapping(nsPrefix, nsURI);
        }
        
        ArrayList<Attribute> attrList = new ArrayList<Attribute>(); 
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
            attributes.addAttribute(attrNamespaceURI, attrLocalPart, attrQName, type, value);
            Attribute a = xmlEventFactory.createAttribute(prefix != null ? prefix : "", 
                                                                attrNamespaceURI,
                                                                attrLocalPart,
                                                                value); 
            attrList.add(a);
        }
        
        validatorHandler.startElement(namespaceURI, localPart, qName, attributes);
        
        if(level2AttributeDefaultValue){            
            attributeDefaultValueHandler.handle(namespaceURI, localPart, attributes, attrList, documentContext);
        }
        
        if(level2AttributeIdType){            
            attributeIdTypeHandler.handle(namespaceURI, localPart, attrList, xmlStreamReader.getLocation());
        }
        
        Iterator namespacesIterator = new NamespacesIterator(nsList, debugWriter);
        namespaceIteratorStack.push(namespacesIterator);
        try{
            xmlEventWriter.add(xmlEventFactory.createStartElement(elemName,
                                                                new AttributesIterator(attrList, debugWriter),
                                                                namespacesIterator));
        }catch(XMLStreamException e){
            throw new SAXException(e);
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
        QName elemName;
        if(prefix != null){
            elemName = new QName(namespaceURI, localPart, prefix);
        }else{
            elemName = new QName(namespaceURI, localPart);
        }
        
        validatorHandler.endElement(namespaceURI, localPart, qName);
        
        int nsCount = xmlStreamReader.getNamespaceCount();
        for(int i = 0; i < nsCount; i++){            
            validatorHandler.endPrefixMapping(xmlStreamReader.getNamespacePrefix(i));
        }
        
        try{
            xmlEventWriter.add(xmlEventFactory.createEndElement(elemName, namespaceIteratorStack.pop()));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleCharacters(String string) throws SAXException{
        super.handleCharacters(string);
        try{
            xmlEventWriter.add(xmlEventFactory.createCharacters(string));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleSpace(String string) throws SAXException{
        super.handleCharacters(string);
        try{
            xmlEventWriter.add(xmlEventFactory.createSpace(string));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleCDATA(String string) throws SAXException{
        super.handleCharacters(string);
        try{
            xmlEventWriter.add(xmlEventFactory.createCData(string));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleStartDocument(XMLStreamReader xmlStreamReader)throws SAXException{
        String version = xmlStreamReader.getVersion();
        String encoding = xmlStreamReader.getCharacterEncodingScheme();
        try{
            xmlEventWriter.add(xmlEventFactory.createStartDocument(encoding != null ? encoding : "UTF-8",
                version != null ? version : "1.0", xmlStreamReader.isStandalone()));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
    
    void handleEndDocument() throws SAXException{
        try{
            xmlEventWriter.add(xmlEventFactory.createEndDocument());
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
        
    void handleProcessingInstruction(XMLStreamReader xmlStreamReader) throws SAXException{
        try{
            xmlEventWriter.add(xmlEventFactory.createProcessingInstruction(xmlStreamReader.getPITarget(), xmlStreamReader.getPIData()));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }    
    }
    
    void handleComment(XMLStreamReader xmlStreamReader) throws SAXException{
        try{
            xmlEventWriter.add(xmlEventFactory.createComment(xmlStreamReader.getText()));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }
     
    
    void handleEntityReference(XMLStreamReader xmlStreamReader) throws SAXException{
        try{
            String name = xmlStreamReader.getLocalName();
            xmlEventWriter.add(xmlEventFactory.createEntityReference(name, entityDeclarations.get(name)));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }

    void handleDTDContext(DTD dtd, DTDHandler dtdHandler) throws SAXException{
        List entities = dtd.getEntities();
        for(int i = 0; i < entities.size(); i++){
            EntityDeclaration entity = (EntityDeclaration)entities.get(i);
            String name = entity.getName();
            entityDeclarations.put(name, entity);
            if(entity.getNotationName() != null){//unparsed entity
                dtdHandler.unparsedEntityDecl(name, entity.getPublicId(), entity.getSystemId(), entity.getNotationName());
            }
        }
        
        List notations = dtd.getNotations();
        for(int i = 0; i < notations.size(); i++){
            NotationDeclaration notation = (NotationDeclaration)notations.get(i);
            dtdHandler.notationDecl(notation.getName(), notation.getPublicId(), notation.getSystemId());            
        }
        try{
            xmlEventWriter.add(dtd);
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }    
    
    void handleDTDContext(XMLStreamReader xmlStreamReader, DTDHandler dtdHandler) throws SAXException{
        List entities = (List)xmlStreamReader.getProperty("javax.xml.stream.entities");
        for(int i = 0; i < entities.size(); i++){
            EntityDeclaration entity = (EntityDeclaration)entities.get(i);
            String name = entity.getName();
            entityDeclarations.put(name, entity);
            if(entity.getNotationName() != null){//unparsed entity
                dtdHandler.unparsedEntityDecl(entity.getName(), entity.getPublicId(), entity.getSystemId(), entity.getNotationName());
            }
        }
        
        List notations = (List)xmlStreamReader.getProperty("javax.xml.stream.notations");
        for(int i = 0; i < notations.size(); i++){
            NotationDeclaration notation = (NotationDeclaration)notations.get(i);
            dtdHandler.notationDecl(notation.getName(), notation.getPublicId(), notation.getSystemId());            
        }
        
        try{
            xmlEventWriter.add(xmlEventFactory.createDTD(xmlStreamReader.getText()));
        }catch(XMLStreamException e){
            throw new SAXException(e);
        }
    }    
}


