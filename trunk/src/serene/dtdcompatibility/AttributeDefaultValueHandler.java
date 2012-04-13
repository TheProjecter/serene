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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.xerces.dom.AttrImpl;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;

import javax.xml.stream.events.Attribute;

import serene.util.AttributeInfo;

import serene.validation.jaxp.util.AttrWrapper;
import serene.validation.jaxp.util.AttributeWrapper;

import serene.validation.handlers.error.ErrorDispatcher;

public class AttributeDefaultValueHandler{
    final String defaultAttributeType = "CDATA";
    
    AttributeDefaultValueModel attributeDefaultValueModel;
    BitSet matchedAttributes;
    
    XMLEventFactory xmlEventFactory; 
    
    ErrorDispatcher errorDispatcher;
    
    public AttributeDefaultValueHandler(AttributeDefaultValueModel attributeDefaultValueModel, ErrorDispatcher errorDispatcher){
        this.errorDispatcher = errorDispatcher;
        this.attributeDefaultValueModel = attributeDefaultValueModel;
        matchedAttributes = new BitSet();
    }
    
    public AttributeDefaultValueModel getAttributeDefaultValueModel( ){
        return attributeDefaultValueModel;
    }
    
    public Attributes handle(String elementNamespaceURI, String elementLocalName, Attributes attributes, InfosetModificationContext infosetModificationContext){
        // must add the attributes and their default values if not in Attributes
        String s = "[";
        for(int i = 0; i < attributes.getLength(); i++){
            s += "("+attributes.getQName(i)+"=\""+attributes.getValue(i)+"\")";
        }        
        s += "]";        
        AttributeInfo[] defaultValues = attributeDefaultValueModel.getAttributeInfo(elementNamespaceURI, elementLocalName);
        if(defaultValues != null){
            attributes = handle(attributes, defaultValues, infosetModificationContext);
            s = "[";
            for(int i = 0; i < attributes.getLength(); i++){
                s += "("+attributes.getQName(i)+"=\""+attributes.getValue(i)+"\")";
            }        
            s += "]";
            return attributes;
        }
        return attributes;
    }
    
    public void handle(String elementNamespaceURI, String elementLocalName, Element parentElement, NamedNodeMap attrs, InfosetModificationContext infosetModificationContext) throws SAXException{
        // must add the attributes and their default values if not in Attributes
        final int attrCount = attrs.getLength();
        String s = "[";
        for (int i = 0; i < attrCount; ++i) {			
            Attr attr = (Attr) attrs.item(i);
			
			String qName = attr.getNodeName();
			if(qName == null) qName = "";
			
            String value = attr.getValue();
            if (value == null) value = "";
            
            s += "("+qName+"=\""+value+"\")";
        }   
        s += "]";        
        AttributeInfo[] defaultValues = attributeDefaultValueModel.getAttributeInfo(elementNamespaceURI, elementLocalName);
        if(defaultValues != null){
            handle(parentElement, attrs, defaultValues, infosetModificationContext);
            attrs = parentElement.getAttributes();
            final int attrCount2 = attrs.getLength();
            s = "[";
            for (int i = 0; i < attrCount2; ++i) {			
                Attr attr = (Attr) attrs.item(i);
                
                String qName = attr.getNodeName();
                if(qName == null) qName = "";
                
                String value = attr.getValue();
                if (value == null) value = "";
                
                s += "("+qName+"=\""+value+"\")";
            }        
            s += "]";
            return;
        }
    }
    
    /**
    * When needsFurtherProcessing indicates if it is necessary to also modify the
    * AttributesImpl attributes in order to make changes available for further 
    * processing, or just write to the stream.
    */    
    public void handle(boolean needsFurtherProcessing, String elementNamespaceURI, String elementLocalName, AttributesImpl attributes, XMLStreamWriter xmlStreamWriter, InfosetModificationContext infosetModificationContext) throws SAXException{
        
         // must add the attributes and their default values to the list if not in attrsIterator
        String s = "[";
        for(int i = 0; i < attributes.getLength(); i++){
            s += "("+attributes.getQName(i)+"=\""+attributes.getValue(i)+"\")";
        }        
        s += "]";
        AttributeInfo[] defaultValues = attributeDefaultValueModel.getAttributeInfo(elementNamespaceURI, elementLocalName);
        if(defaultValues != null){
            if(needsFurtherProcessing){
                handle(needsFurtherProcessing, attributes, defaultValues, xmlStreamWriter, infosetModificationContext);
            }else{
                handle(attributes, defaultValues, xmlStreamWriter, infosetModificationContext);
            }
            s = "[";
            for(int i = 0; i < attributes.getLength(); i++){
                s += "("+attributes.getQName(i)+"=\""+attributes.getValue(i)+"\")";
            }        
            s += "]";
        }
    }
    
    public void handle(String elementNamespaceURI, String elementLocalName, Attributes attributes, List<Attribute> attributeEvents, InfosetModificationContext infosetModificationContext){
         // must add the attributes and their default values to the list if not in attrsIterator
        String s = "[";
        for(int i = 0; i < attributes.getLength(); i++){
            s += "("+attributes.getQName(i)+"=\""+attributes.getValue(i)+"\")";
        }        
        s += "]";
        if(xmlEventFactory == null) xmlEventFactory = XMLEventFactory.newInstance();
        AttributeInfo[] defaultValues = attributeDefaultValueModel.getAttributeInfo(elementNamespaceURI, elementLocalName);
        if(defaultValues != null){
            handle(attributes, defaultValues, attributeEvents, infosetModificationContext);
            s = "[";
            for(int i = 0; i < attributes.getLength(); i++){
                s += "("+attributes.getQName(i)+"=\""+attributes.getValue(i)+"\")";
            }        
            s += "]";
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
    
    private void handle(Element parentElement, NamedNodeMap attrs, AttributeInfo[] defaultValues, InfosetModificationContext infosetModificationContext) throws SAXException{
        final int attrCount = attrs.getLength();
        matchedAttributes.clear();
        Document document = null;
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
                if(document == null) document = parentElement.getOwnerDocument();
                Attr defaultAttribute = document.createAttributeNS(dvNsURI, getQName(dvNsURI, dvLocalName, infosetModificationContext));
                defaultAttribute.setNodeValue(defaultValueInfo.getValue());                
                if(defaultAttribute instanceof AttrImpl){
                    AttrImpl defaultAttrImpl = (AttrImpl)defaultAttribute;
                    defaultAttrImpl.setSpecified(false);
                    parentElement.setAttributeNodeNS(defaultAttrImpl);
                }else{
                    try{
                        defaultAttribute = new AttrWrapper(defaultAttribute, false);
                    }catch(Exception e){
                        errorDispatcher.warning(new SAXParseException("DTD compatibility warning. In the context of element <"+parentElement.getTagName()+"> for the attribute "+getQName(dvNsURI, dvLocalName, infosetModificationContext)+" added by validator the Specified parameter could not be set to false. "+e.getMessage(), null));
                    }
                    parentElement.setAttributeNodeNS(defaultAttribute);
                }
            }
        }        
    } 
      
    private void handle(boolean needsFurtherProcesing, AttributesImpl attributes, AttributeInfo[] defaultValues, XMLStreamWriter xmlStreamWriter, InfosetModificationContext infosetModificationContext) throws SAXException{
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
                    attributes.addAttribute(dvNsURI, dvLocalName, getQName(dvNsURI, dvLocalName, infosetModificationContext), defaultAttributeType, defaultValueInfo.getValue());                    
                    xmlStreamWriter.writeAttribute(infosetModificationContext.getPrefix(dvNsURI), dvNsURI, dvLocalName, defaultValueInfo.getValue());
                }catch(XMLStreamException e){
                    throw new SAXException(e);
                }  
            }
        }    
    }
    
    private void handle(AttributesImpl attributes, AttributeInfo[] defaultValues, XMLStreamWriter xmlStreamWriter, InfosetModificationContext infosetModificationContext) throws SAXException{
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
                Attribute defaultAttribute = xmlEventFactory.createAttribute(infosetModificationContext.getPrefix(dvNsURI), 
                                                                    dvNsURI,
                                                                    dvLocalName,
                                                                    defaultValueInfo.getValue());
                defaultAttribute = new AttributeWrapper(defaultAttribute, null, false);
                attributeEvents.add(defaultAttribute);
            }
        }    
    }
    
    private String getQName(String nsURI, String localPart, InfosetModificationContext infosetModificationContext){
        String prefix = infosetModificationContext.getPrefix(nsURI);
        if(prefix == null || prefix.equals(""))return localPart;
        return prefix+":"+localPart;
    }
}
