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

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.StringTokenizer;

import java.io.File;

import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.relaxng.datatype.Datatype;

import serene.util.AttributeInfo;
import serene.util.NameLocationInfo;
import serene.util.AttributeLocationInfo;

import serene.validation.jaxp.util.AttributeWrapper;
//import serene.validation.jaxp.util.AttrWrapper;

import serene.validation.handlers.error.ErrorDispatcher;

import sereneWrite.MessageWriter;

public class AttributeIdTypeHandler{  
    String ID_TYPE = "ID";
    String IDREF_TYPE = "IDREF";
    String IDREFS_TYPE = "IDREFS";
    
    AttributeIdTypeModel attributeIdTypeModel;
    
    HashMap<String, NameLocationInfo> idAttributes;
    ArrayList<AttributeLocationInfo> refAttributes;

    ArrayList<String> errorTokens;

    ErrorDispatcher errorDispatcher;

    boolean restrictToFileName;
    
    MessageWriter debugWriter;
    
    public AttributeIdTypeHandler(AttributeIdTypeModel attributeIdTypeModel,
                                ErrorDispatcher errorDispatcher,
                                MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.errorDispatcher = errorDispatcher;
        if(errorDispatcher == null) throw new NullPointerException();
        
        this.attributeIdTypeModel = attributeIdTypeModel;
        
        idAttributes = new HashMap<String, NameLocationInfo>();
        refAttributes = new ArrayList<AttributeLocationInfo>();
        
        errorTokens = new ArrayList<String>();
    }
    
    public void init(){
        idAttributes.clear();
        refAttributes.clear();
    }
  
    public void setRestrictToFileName(boolean restrictToFileName){
        this.restrictToFileName = restrictToFileName;
    }
    
    public AttributeIdTypeModel getAttributeIdTypeModel(){
        return attributeIdTypeModel;
    }
       
    
    public void handle(boolean noModification, String elementNamespaceURI, String elementLocalName, Attributes attributes, Locator locator) throws SAXException{
        if(!attributeIdTypeModel.hasIdAttributes(elementNamespaceURI, elementLocalName)) return;
        int attributesCount = attributes.getLength();        
        for(int i = 0; i < attributesCount; i++){            
            String namespaceURI = attributes.getURI(i);
            String localName = attributes.getLocalName(i);
            String qName = attributes.getQName(i);
            String value = attributes.getValue(i);
            
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
            int lineNumber = locator.getLineNumber();
            int columnNumber = locator.getColumnNumber();
                        
            AttributeInfo attributeInfo = attributeIdTypeModel.getAttributeInfo(namespaceURI, localName);
            if(attributeInfo == null){
            }else{
                int idType = attributeInfo.getIdType();            
                if(idType == Datatype.ID_TYPE_ID){
                    NameLocationInfo previousId = idAttributes.put(value, new NameLocationInfo(namespaceURI,
                                                                                            localName,
                                                                                            qName,
                                                                                            publicId,
                                                                                            systemId,
                                                                                            lineNumber,
                                                                                            columnNumber,
                                                                                            debugWriter)); 
                    if(previousId != null){
                        String message = "Soundness error. Value of attribute "+qName+" with the ID-type ID is the same as  value of attribute "+
                        previousId.getQName()+" at "+getLocation(previousId.getSystemId())+":"+previousId.getLineNumber()+":"+previousId.getColumnNumber()+" with the ID-type ID.";
                        errorDispatcher.error(new AttributeIdTypeException(message, publicId, systemId, lineNumber, columnNumber));
                    }
                }else if(idType == Datatype.ID_TYPE_IDREF || idType == Datatype.ID_TYPE_IDREFS){
                    refAttributes.add(new AttributeLocationInfo(namespaceURI,
                                                                localName,
                                                                qName,
                                                                value,
                                                                idType,
                                                                publicId,
                                                                systemId,
                                                                lineNumber,
                                                                columnNumber,
                                                                debugWriter));
                }else{
                    throw new IllegalStateException();   
                }
            }
        }    
    }
    
    public Attributes handle(String elementNamespaceURI, String elementLocalName, Attributes attributes, Locator locator) throws SAXException{
        if(!attributeIdTypeModel.hasIdAttributes(elementNamespaceURI, elementLocalName)) return attributes;
        int attributesCount = attributes.getLength();
        AttributesImpl modifiedAttributes = new AttributesImpl();
        for(int i = 0; i < attributesCount; i++){            
            String namespaceURI = attributes.getURI(i);
            String localName = attributes.getLocalName(i);
            String qName = attributes.getQName(i);
            String value = attributes.getValue(i);
            
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
            int lineNumber = locator.getLineNumber();
            int columnNumber = locator.getColumnNumber();
                        
            AttributeInfo attributeInfo = attributeIdTypeModel.getAttributeInfo(namespaceURI, localName);
            if(attributeInfo == null){
                modifiedAttributes.addAttribute(namespaceURI,
                                        localName,
                                        qName,
                                        attributes.getType(i),
                                        value);
            }else{
                int idType = attributeInfo.getIdType();            
                if(idType == Datatype.ID_TYPE_ID){
                    modifiedAttributes.addAttribute(namespaceURI,
                                            localName,
                                            qName,
                                            ID_TYPE,
                                            value); 
                    NameLocationInfo previousId = idAttributes.put(value, new NameLocationInfo(namespaceURI,
                                                                                            localName,
                                                                                            qName,
                                                                                            publicId,
                                                                                            systemId,
                                                                                            lineNumber,
                                                                                            columnNumber,
                                                                                            debugWriter)); 
                    if(previousId != null){
                        String message = "Soundness error. Value of attribute "+qName+" with the ID-type ID is the same as  value of attribute "+
                        previousId.getQName()+" at "+getLocation(previousId.getSystemId())+":"+previousId.getLineNumber()+":"+previousId.getColumnNumber()+" with the ID-type ID.";
                        errorDispatcher.error(new AttributeIdTypeException(message, publicId, systemId, lineNumber, columnNumber));
                    }
                }else if(idType == Datatype.ID_TYPE_IDREF){
                    modifiedAttributes.addAttribute(namespaceURI,
                                            localName,
                                            qName,
                                            IDREF_TYPE,
                                            value);
                    refAttributes.add(new AttributeLocationInfo(namespaceURI,
                                                                localName,
                                                                qName,
                                                                value,
                                                                idType,
                                                                publicId,
                                                                systemId,
                                                                lineNumber,
                                                                columnNumber,
                                                                debugWriter));
                }else if(idType == Datatype.ID_TYPE_IDREFS){
                    modifiedAttributes.addAttribute(namespaceURI,
                                            localName,
                                            qName,
                                            IDREFS_TYPE,
                                            value);
                    refAttributes.add(new AttributeLocationInfo(namespaceURI,
                                                                localName,
                                                                qName,
                                                                value,
                                                                idType,
                                                                publicId,
                                                                systemId,
                                                                lineNumber,
                                                                columnNumber,
                                                                debugWriter));
                }else{
                    throw new IllegalStateException();   
                }
            }
        }
        return modifiedAttributes;
    }
    
    
    public void handle(String elementNamespaceURI, String elementLocalName, List<Attribute> attrList, Location location) throws SAXException{
        if(!attributeIdTypeModel.hasIdAttributes(elementNamespaceURI, elementLocalName)) return;
        int attributesCount = attrList.size();
        for(int i = 0; i < attributesCount; i++){
            Attribute attribute = attrList.get(i);
            QName attributeQName = attribute.getName();            
            String namespaceURI = attributeQName.getNamespaceURI();
            String localName = attributeQName.getLocalPart();
            String prefix = attributeQName.getPrefix();
            String qName = prefix == null || prefix.equals("") ? localName : prefix+":"+localName;
            String value = attribute.getValue();
            
            String publicId = location.getPublicId();
            String systemId = location.getSystemId();
            int lineNumber = location.getLineNumber();
            int columnNumber = location.getColumnNumber();
                                    
            AttributeInfo attributeInfo = attributeIdTypeModel.getAttributeInfo(namespaceURI, localName);
            if(attributeInfo != null){
                int idType = attributeInfo.getIdType();            
                if(idType == Datatype.ID_TYPE_ID){
                    AttributeWrapper modifiedAttribute = new AttributeWrapper(attrList.get(i), ID_TYPE, debugWriter);
                    attrList.set(i, modifiedAttribute); 
                    NameLocationInfo previousId = idAttributes.put(value, new NameLocationInfo(namespaceURI,
                                                                                            localName,
                                                                                            qName,
                                                                                            publicId,
                                                                                            systemId,
                                                                                            lineNumber,
                                                                                            columnNumber,
                                                                                            debugWriter)); 
                    if(previousId != null){
                        String message = "Soundness error. Value of attribute "+qName+" with the ID-type ID is the same as  value of attribute "+
                        previousId.getQName()+" at "+getLocation(previousId.getSystemId())+":"+previousId.getLineNumber()+":"+previousId.getColumnNumber()+" with the ID-type ID.";
                        errorDispatcher.error(new AttributeIdTypeException(message, publicId, systemId, lineNumber, columnNumber));
                    }
                }else if(idType == Datatype.ID_TYPE_IDREF){
                    AttributeWrapper modifiedAttribute = new AttributeWrapper(attrList.get(i), IDREF_TYPE, debugWriter);
                    attrList.set(i, modifiedAttribute);
                    refAttributes.add(new AttributeLocationInfo(namespaceURI,
                                                                localName,
                                                                qName,
                                                                value,
                                                                idType,
                                                                publicId,
                                                                systemId,
                                                                lineNumber,
                                                                columnNumber,
                                                                debugWriter));
                }else if(idType == Datatype.ID_TYPE_IDREFS){
                    AttributeWrapper modifiedAttribute = new AttributeWrapper(attrList.get(i), IDREFS_TYPE, debugWriter);
                    attrList.set(i, modifiedAttribute);
                    refAttributes.add(new AttributeLocationInfo(namespaceURI,
                                                                localName,
                                                                qName,
                                                                value,
                                                                idType,
                                                                publicId,
                                                                systemId,
                                                                lineNumber,
                                                                columnNumber,
                                                                debugWriter));
                }else{
                    throw new IllegalStateException();   
                }
            }
        }
    }
    
    
    public void handle(String elementNamespaceURI, String elementLocalName, Attributes attributes, Location location) throws SAXException{
        if(!attributeIdTypeModel.hasIdAttributes(elementNamespaceURI, elementLocalName)) return;
        int attributesCount = attributes.getLength();
        for(int i = 0; i < attributesCount; i++){            
            String namespaceURI = attributes.getURI(i);
            String localName = attributes.getLocalName(i);
            String qName = attributes.getQName(i);
            String value = attributes.getValue(i);
            
            String publicId = location.getPublicId();
            String systemId = location.getSystemId();
            int lineNumber = location.getLineNumber();
            int columnNumber = location.getColumnNumber();
                                    
            AttributeInfo attributeInfo = attributeIdTypeModel.getAttributeInfo(namespaceURI, localName);
            if(attributeInfo != null){
                int idType = attributeInfo.getIdType();            
                if(idType == Datatype.ID_TYPE_ID){
                    NameLocationInfo previousId = idAttributes.put(value, new NameLocationInfo(namespaceURI,
                                                                                            localName,
                                                                                            qName,
                                                                                            publicId,
                                                                                            systemId,
                                                                                            lineNumber,
                                                                                            columnNumber,
                                                                                            debugWriter)); 
                    if(previousId != null){
                        String message = "Soundness error. Value of attribute "+qName+" with the ID-type ID is the same as  value of attribute "+
                        previousId.getQName()+" at "+getLocation(previousId.getSystemId())+":"+previousId.getLineNumber()+":"+previousId.getColumnNumber()+" with the ID-type ID.";
                        errorDispatcher.error(new AttributeIdTypeException(message, publicId, systemId, lineNumber, columnNumber));
                    }
                }else if(idType == Datatype.ID_TYPE_IDREF || idType == Datatype.ID_TYPE_IDREFS){
                    refAttributes.add(new AttributeLocationInfo(namespaceURI,
                                                                localName,
                                                                qName,
                                                                value,
                                                                idType,
                                                                publicId,
                                                                systemId,
                                                                lineNumber,
                                                                columnNumber,
                                                                debugWriter));
                }else{
                    throw new IllegalStateException();   
                }
            }
        }
    }
    
    public void handle(String elementNamespaceURI, String elementLocalName, Element element) throws SAXException{       
        if(!attributeIdTypeModel.hasIdAttributes(elementNamespaceURI, elementLocalName)) return;
        String publicId = null;
        String systemId = null;
        int lineNumber = -1;
        int columnNumber = -1;
        NamedNodeMap attributes = element.getAttributes();
        int attributesCount = attributes.getLength();
        for(int i = 0; i < attributesCount; i++){            
            Attr attribute = (Attr)attributes.item(i);
            
            String namespaceURI = attribute.getNamespaceURI();
            if(namespaceURI == null) namespaceURI = "";
            String localName = attribute.getLocalName();
            String qName = attribute.getNodeName();
            if(qName == null) qName = "";
            String value = attribute.getValue();
            
            if(localName == null){
                int j = qName.indexOf(':');
                if(j > 0) localName = qName.substring(j+1);
                else localName = "";
            }
            
                                    
            AttributeInfo attributeInfo = attributeIdTypeModel.getAttributeInfo(namespaceURI, localName);
            if(attributeInfo != null){
                int idType = attributeInfo.getIdType();            
                if(idType == Datatype.ID_TYPE_ID){
                    element.setIdAttributeNode(attribute, true);
                    NameLocationInfo previousId = idAttributes.put(value, new NameLocationInfo(namespaceURI,
                                                                                            localName,
                                                                                            qName,
                                                                                            publicId,
                                                                                            systemId,
                                                                                            lineNumber,
                                                                                            columnNumber,
                                                                                            debugWriter)); 
                    if(previousId != null){
                        String message = "Soundness error. Value of attribute "+qName+" with the ID-type ID is the same as  value of attribute "+
                        previousId.getQName()+" at "+getLocation(previousId.getSystemId())+":"+previousId.getLineNumber()+":"+previousId.getColumnNumber()+" with the ID-type ID.";
                        errorDispatcher.error(new AttributeIdTypeException(message, publicId, systemId, lineNumber, columnNumber));
                    }
                }else if(idType == Datatype.ID_TYPE_IDREF || idType == Datatype.ID_TYPE_IDREFS){
                    refAttributes.add(new AttributeLocationInfo(namespaceURI,
                                                                localName,
                                                                qName,
                                                                value,
                                                                idType,
                                                                publicId,
                                                                systemId,
                                                                lineNumber,
                                                                columnNumber,
                                                                debugWriter));
                }else{
                    throw new IllegalStateException();   
                }
            }
        }
    }
    
    public void handleRefs(Locator locator) throws SAXException{
        for(int i = 0; i < refAttributes.size(); i++){
            AttributeLocationInfo attribute = refAttributes.get(i);
            int idType = attribute.getIdType();
            String value = attribute.getValue();
            if(idType == Datatype.ID_TYPE_IDREF){
                if(!idAttributes.containsKey(value)){
                    String message = "Soundness error. No corresponding attribute of ID-type ID for attribute "+attribute.getQName()+" at "+getLocation(attribute.getSystemId())+":"+attribute.getLineNumber()+":"+attribute.getColumnNumber()+" with the ID-type IDREF.";
                    errorDispatcher.error(new AttributeIdTypeException(message, locator));
                }
            }else if(idType == Datatype.ID_TYPE_IDREFS){
                errorTokens.clear();
                StringTokenizer tokenizer = new StringTokenizer(value);
                while(tokenizer.hasMoreTokens()){
                    String token = tokenizer.nextToken();
                    if(!idAttributes.containsKey(token)){
                        errorTokens.add(token);
                    }
                }
                if(errorTokens.size() == 1){
                    String message = "Soundness error. No corresponding attribute of ID-type ID for token \""+errorTokens.get(0)+"\" in the value of attribute "+attribute.getQName()+" at "+getLocation(attribute.getSystemId())+":"+attribute.getLineNumber()+":"+attribute.getColumnNumber()+" with the ID-type IDREFS.";
                    errorDispatcher.error(new AttributeIdTypeException(message, locator));
                }else if(errorTokens.size() > 1){
                    String tokens = "";
                    int lastToken = errorTokens.size()-1;
                    for(int j = 0; j < lastToken; j++){
                        tokens += "\""+errorTokens.get(j)+"\", ";
                    }
                    tokens += "\""+errorTokens.get(lastToken)+"\"";
                    String message = "Soundness error. No corresponding attributes of ID-type ID for tokens "+tokens+" in the value of attribute "+attribute.getQName()+" at "+getLocation(attribute.getSystemId())+":"+attribute.getLineNumber()+":"+attribute.getColumnNumber()+" with the ID-type IDREFS.";
                    errorDispatcher.error(new AttributeIdTypeException(message, locator));
                }
            }
        }
    }

    private String getLocation(String systemId){     
        if(systemId == null || !restrictToFileName)return systemId;
        int nameIndex = systemId.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = systemId.lastIndexOf('/')+1;
        return systemId.substring(nameIndex);	
    }    
}
