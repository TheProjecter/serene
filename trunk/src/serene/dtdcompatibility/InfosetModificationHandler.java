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


package serene.dtdcompatibility;

import java.util.BitSet;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;

import javax.xml.stream.events.Attribute;

import serene.util.AttributeInfo;

import sereneWrite.MessageWriter;

public class InfosetModificationHandler{
    final String defaultAttributeType = "CDATA";
    
    AttributeDefaultValueModel attributeDefaultValueModel;
    BitSet matchedAttributes;
    
    XMLEventFactory xmlEventFactory; 
    
    MessageWriter debugWriter;
    
    public InfosetModificationHandler(MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        matchedAttributes = new BitSet();
    }
    
    public void setAttributeDefaultValueModel(AttributeDefaultValueModel attributeDefaultValueModel){
        this.attributeDefaultValueModel = attributeDefaultValueModel;
    }
    
    public AttributeDefaultValueModel getAttributeDefaultValueModel( ){
        return attributeDefaultValueModel;
    }
    
    public Attributes modify(String elementNamespaceURI, String elementLocalName, Attributes attributes, InfosetModificationContext infosetModificationContext){
        // must add the attributes and their default values if not in Attributes        
        AttributeInfo[] defaultValues = attributeDefaultValueModel.getAttributeInfo(elementNamespaceURI, elementLocalName);
        if(defaultValues != null){
            attributes = handle(attributes, defaultValues, infosetModificationContext);            
            return attributes;
        }
        return attributes;
    }
    
    public void modify(String elementNamespaceURI, String elementLocalName, Element parentElement, NamedNodeMap attrs, InfosetModificationContext infosetModificationContext){
        // must add the attributes and their default values if not in Attributes
        AttributeInfo[] defaultValues = attributeDefaultValueModel.getAttributeInfo(elementNamespaceURI, elementLocalName);
        if(defaultValues != null){
            handle(parentElement, attrs, defaultValues, infosetModificationContext);
            attrs = parentElement.getAttributes();            
            return;
        }
    }
    
    public void modify(String elementNamespaceURI, String elementLocalName, Attributes attributes, XMLStreamWriter xmlStreamWriter, InfosetModificationContext infosetModificationContext) throws SAXException{
         // must add the attributes and their default values to the list if not in attrsIterator        
        AttributeInfo[] defaultValues = attributeDefaultValueModel.getAttributeInfo(elementNamespaceURI, elementLocalName);
        if(defaultValues != null){
            handle(attributes, defaultValues, xmlStreamWriter, infosetModificationContext);            
        }
    }
    
    public void modify(String elementNamespaceURI, String elementLocalName, Attributes attributes, List<Attribute> attributeEvents, InfosetModificationContext infosetModificationContext){
         // must add the attributes and their default values to the list if not in attrsIterator        
        if(xmlEventFactory == null) xmlEventFactory = XMLEventFactory.newInstance();
        AttributeInfo[] defaultValues = attributeDefaultValueModel.getAttributeInfo(elementNamespaceURI, elementLocalName);
        if(defaultValues != null){
            handle(attributes, defaultValues, attributeEvents, infosetModificationContext);            
        }
    }
    
    
    private Attributes handle(Attributes attributes, AttributeInfo[] defaultValues, InfosetModificationContext infosetModificationContext){
        final int attributesCount = attributes.getLength();
        AttributesImpl modifiedAttributes = null;
        matchedAttributes.clear();
        for(AttributeInfo defaultValueInfo : defaultValues){
            String dvNsURI = defaultValueInfo.getNamespaceURI();           
            String dvLocalName = defaultValueInfo.getLocalName();
            boolean attributeAbsent = true;
            for(int i = 0; i < attributesCount; i++){
                if(!matchedAttributes.get(i)){
                    String attrNsURI = attributes.getURI(i);
                    if(dvNsURI.equals(attrNsURI)){
                        String attrLocalName = attributes.getLocalName(i);
                        if(dvLocalName.equals(attrLocalName)){
                            attributeAbsent = false;
                            matchedAttributes.set(i);
                            // attribute present 
                            // mark so it won't be tested any more
                        }                   
                    }
                }
            }
            if(attributeAbsent){
                if(modifiedAttributes == null)modifiedAttributes = new AttributesImpl(attributes);
                modifiedAttributes.addAttribute(dvNsURI, dvLocalName, getQName(dvNsURI, dvLocalName, infosetModificationContext), defaultAttributeType, defaultValueInfo.getValue()); 
            }
        }
        if(modifiedAttributes != null)return modifiedAttributes;
        return attributes;
    }
    
    private void handle(Element parentElement, NamedNodeMap attrs, AttributeInfo[] defaultValues, InfosetModificationContext infosetModificationContext){
        final int attrCount = attrs.getLength();
        matchedAttributes.clear();
        for(AttributeInfo defaultValueInfo : defaultValues){
            String dvNsURI = defaultValueInfo.getNamespaceURI();
            String dvLocalName = defaultValueInfo.getLocalName();
            boolean attributeAbsent = true;
            for(int i = 0; i < attrCount; i++){
                if(!matchedAttributes.get(i)){
                    Attr attr = (Attr) attrs.item(i);
                    
                    String attrNsURI = attr.getNamespaceURI();
                    if(attrNsURI == null) attrNsURI = "";
                    if(dvNsURI.equals(attrNsURI)){
                        String attrLocalName = attr.getLocalName();
                        if(attrLocalName == null){
                            String qName = attr.getNodeName();
                            if(qName == null) qName = "";
                            
                            int j = qName.indexOf(':');
                            if(j > 0) attrLocalName = qName.substring(j+1);
                            else attrLocalName = "";
                        }
                        
                        if(dvLocalName.equals(attrLocalName)){
                            attributeAbsent = false;
                            matchedAttributes.set(i);
                            // attribute present 
                            // mark so it won't be tested any more
                        }                   
                    }
                }
            }
            if(attributeAbsent){
                parentElement.setAttributeNS(dvNsURI, getQName(dvNsURI, dvLocalName, infosetModificationContext), defaultValueInfo.getValue()); 
            }
        }        
    } 
      
    private void handle(Attributes attributes, AttributeInfo[] defaultValues, XMLStreamWriter xmlStreamWriter, InfosetModificationContext infosetModificationContext) throws SAXException{
        final int attributesCount = attributes.getLength();
        matchedAttributes.clear();
        for(AttributeInfo defaultValueInfo : defaultValues){
            String dvNsURI = defaultValueInfo.getNamespaceURI();           
            String dvLocalName = defaultValueInfo.getLocalName();
            boolean attributeAbsent = true;
            for(int i = 0; i < attributesCount; i++){
                if(!matchedAttributes.get(i)){
                    String attrNsURI = attributes.getURI(i);
                    if(dvNsURI.equals(attrNsURI)){
                        String attrLocalName = attributes.getLocalName(i);
                        if(dvLocalName.equals(attrLocalName)){
                            attributeAbsent = false;
                            matchedAttributes.set(i);
                            // attribute present 
                            // mark so it won't be tested any more
                        }                   
                    }
                }
            }
            if(attributeAbsent){
                try{                
                    xmlStreamWriter.writeAttribute(infosetModificationContext.getPrefix(dvNsURI), dvNsURI, dvLocalName, defaultValueInfo.getValue());
                }catch(XMLStreamException e){
                    throw new SAXException(e);
                }  
            }
        }    
    }
    
    
    private void handle(Attributes attributes, AttributeInfo[] defaultValues, List<Attribute> attributeEvents, InfosetModificationContext infosetModificationContext){
        final int attributesCount = attributes.getLength();
        matchedAttributes.clear();
        for(AttributeInfo defaultValueInfo : defaultValues){
            String dvNsURI = defaultValueInfo.getNamespaceURI();           
            String dvLocalName = defaultValueInfo.getLocalName();
            boolean attributeAbsent = true;
            for(int i = 0; i < attributesCount; i++){
                if(!matchedAttributes.get(i)){
                    String attrNsURI = attributes.getURI(i);
                    if(dvNsURI.equals(attrNsURI)){
                        String attrLocalName = attributes.getLocalName(i);
                        if(dvLocalName.equals(attrLocalName)){
                            attributeAbsent = false;
                            matchedAttributes.set(i);
                            // attribute present 
                            // mark so it won't be tested any more
                        }                   
                    }
                }
            }
            if(attributeAbsent){    
                attributeEvents.add(xmlEventFactory.createAttribute(infosetModificationContext.getPrefix(dvNsURI), 
                                                                    dvNsURI,
                                                                    dvLocalName,
                                                                    defaultValueInfo.getValue()));
            }
        }    
    }
    
    private String getQName(String nsURI, String localPart, InfosetModificationContext infosetModificationContext){
        String prefix = infosetModificationContext.getPrefix(nsURI);
        if(prefix == null || prefix.equals(""))return localPart;
        return prefix+":"+localPart;
    }
}
