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
import java.util.ArrayList;

import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Entity;
import org.w3c.dom.Notation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

import javax.xml.XMLConstants;
import javax.xml.validation.ValidatorHandler;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.EntityImpl;
import org.apache.xerces.dom.NotationImpl;


import serene.dtdcompatibility.AttributeDefaultValueHandler;
import serene.dtdcompatibility.AttributeIdTypeHandler;

import serene.util.IntStack;

import serene.DocumentContext;
import serene.Constants;

import sereneWrite.MessageWriter;

class DOMBuildingHandler extends DOMHandler{

    /** Table for quick check of child insertion. */
    private final static int[] kidOK;
    
    static {
        kidOK = new int[13];
        kidOK[Node.DOCUMENT_NODE] =
            1 << Node.ELEMENT_NODE | 1 << Node.PROCESSING_INSTRUCTION_NODE |
            1 << Node.COMMENT_NODE | 1 << Node.DOCUMENT_TYPE_NODE;
        kidOK[Node.DOCUMENT_FRAGMENT_NODE] =
        kidOK[Node.ENTITY_NODE] =
        kidOK[Node.ENTITY_REFERENCE_NODE] =
        kidOK[Node.ELEMENT_NODE] =
            1 << Node.ELEMENT_NODE | 1 << Node.PROCESSING_INSTRUCTION_NODE |
            1 << Node.COMMENT_NODE | 1 << Node.TEXT_NODE |
            1 << Node.CDATA_SECTION_NODE | 1 << Node.ENTITY_REFERENCE_NODE ;
        kidOK[Node.ATTRIBUTE_NODE] = 1 << Node.TEXT_NODE | 1 << Node.ENTITY_REFERENCE_NODE;
        kidOK[Node.DOCUMENT_TYPE_NODE] = 0;
        kidOK[Node.PROCESSING_INSTRUCTION_NODE] = 0;
        kidOK[Node.COMMENT_NODE] = 0;
        kidOK[Node.TEXT_NODE] = 0;
        kidOK[Node.CDATA_SECTION_NODE] = 0;
        kidOK[Node.NOTATION_NODE] = 0;
    } // static
    
    Document resultDocument;
    Node resultNode;    
    Node resultNextSibling;    
    ArrayList<Node> resultNodeChildren;
    
    Node currentNode;    
    Node currentFragmentRoot;
    Element currentElement;
    
    AttributeDefaultValueHandler attributeDefaultValueHandler;
    AttributeIdTypeHandler attributeIdTypeHandler;
    
    boolean level2AttributeDefaultValue;    
    boolean level2AttributeIdType;
    
    DocumentContext documentContext;
    
    DOMBuildingHandler(MessageWriter debugWriter){
        super(debugWriter);
        resultNodeChildren = new ArrayList<Node>(); 
    }
    
    void setLevel2AttributeDefaultValue(boolean level2AttributeDefaultValue){
        this.level2AttributeDefaultValue = level2AttributeDefaultValue;
    }    
    
    void setLevel2AttributeIdType(boolean level2AttributeIdType){
        this.level2AttributeIdType = level2AttributeIdType;
    }
    
	void handle(String systemId, ValidatorHandler validatorHandler, Node sourceNode, Node resultNode) throws SAXException{
        if(level2AttributeDefaultValue) {
            attributeDefaultValueHandler = (AttributeDefaultValueHandler)validatorHandler.getProperty(Constants.ATTRIBUTE_DEFAULT_VALUE_HANDLER_PROPERTY);
        }
        if(level2AttributeIdType){
            attributeIdTypeHandler = (AttributeIdTypeHandler)validatorHandler.getProperty(Constants.ATTRIBUTE_ID_TYPE_HANDLER_PROPERTY);
            attributeIdTypeHandler.init();
        }
        documentContext = (DocumentContext)validatorHandler.getProperty(Constants.DOCUMENT_CONTEXT_PROPERTY);
        
        currentNode = null;
        currentFragmentRoot = null;
        currentElement = null;
        
        this.resultNode = resultNode;
        resultNextSibling = resultNode.getNextSibling();
        resultDocument = (resultNode.getNodeType() == Node.DOCUMENT_NODE) ? (Document)resultNode : resultNode.getOwnerDocument();
        resultNodeChildren.clear();
               
        super.handle(systemId, validatorHandler, sourceNode);
        
        close();
        
        if(level2AttributeIdType){
            attributeIdTypeHandler.handleRefs(locator);
        }
    }
    
	void beginNode(Node node) throws SAXException{
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
				handleStartElement(node);
                break;
            case Node.TEXT_NODE:
                String value = node.getNodeValue();
                append(resultDocument.createTextNode(value));
				handleCharacters(value);
                break;
            case Node.CDATA_SECTION_NODE:
                value = node.getNodeValue();
                append(resultDocument.createCDATASection(value));
				handleCharacters(value);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                handleProcessingInstruction((ProcessingInstruction) node);
                break;
            case Node.COMMENT_NODE:
                append(resultDocument.createComment(node.getNodeValue()));
                break;
            case Node.DOCUMENT_TYPE_NODE:
                handleDoctypeDecl((DocumentType) node);
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
        
        currentElement = resultDocument.createElementNS(namespaceURI, qName);
        
        NamedNodeMap attrs = element.getAttributes(); 
        handleAttributes(attrs); // fires startPrefixMapping and add copies to currentElement
        
        validatorHandler.startElement(namespaceURI, localName, qName, attributes);
                
        if(level2AttributeDefaultValue){
            attributeDefaultValueHandler.handle(namespaceURI, localName, currentElement, attrs, documentContext);
        }
        
        if(level2AttributeIdType){
            attributeIdTypeHandler.handle(namespaceURI, localName, currentElement);
        }
        
        append(currentElement);
        currentNode = currentElement;
        if (currentFragmentRoot == null) {
            currentFragmentRoot = currentElement;
        }
        
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
			
            currentElement.setAttributeNS(namespaceURI, qName, value);
            
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
            
            // adjust current node reference
            if (currentNode == currentFragmentRoot) {
                currentNode = null;
                currentFragmentRoot = null;
                return;
            }
            currentNode = currentNode.getParentNode();            			
        }
    }
    
    void handleProcessingInstruction(ProcessingInstruction processingInstruction){
        append(resultDocument.createProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData()));
    }
    
    void handleDoctypeDecl(DocumentType documentType){    
        /** Create new DocumentType node for the target. */
        if (resultDocument instanceof CoreDocumentImpl) {
            CoreDocumentImpl xercesDocument = (CoreDocumentImpl)resultDocument; 
            DocumentType resultDocumentType = xercesDocument.createDocumentType(documentType.getName(), documentType.getPublicId(), documentType.getSystemId());
            final String internalSubset = documentType.getInternalSubset();
            /** Copy internal subset. */
            if (internalSubset != null) {
                ((DocumentTypeImpl) resultDocumentType).setInternalSubset(internalSubset);
            }
            /** Copy entities. */
            NamedNodeMap oldMap = documentType.getEntities();
            NamedNodeMap newMap = resultDocumentType.getEntities();
            int length = oldMap.getLength();
            for (int i = 0; i < length; ++i) {
                Entity oldEntity = (Entity) oldMap.item(i);
                EntityImpl newEntity = (EntityImpl) xercesDocument.createEntity(oldEntity.getNodeName());
                newEntity.setPublicId(oldEntity.getPublicId());
                newEntity.setSystemId(oldEntity.getSystemId());
                newEntity.setNotationName(oldEntity.getNotationName());
                newMap.setNamedItem(newEntity);
            }
            /** Copy notations. */
            oldMap = documentType.getNotations();
            newMap = resultDocumentType.getNotations();
            length = oldMap.getLength();
            for (int i = 0; i < length; ++i) {
                Notation oldNotation = (Notation) oldMap.item(i);
                NotationImpl newNotation = (NotationImpl) xercesDocument.createNotation(oldNotation.getNodeName());
                newNotation.setPublicId(oldNotation.getPublicId());
                newNotation.setSystemId(oldNotation.getSystemId());
                newMap.setNamedItem(newNotation);
            }
            append(resultDocumentType);
        }
    }

    void close(){
        final int length = resultNodeChildren.size();
        if (resultNextSibling == null) {
            for (int i = 0; i < length; ++i) {
                resultNode.appendChild((Node) resultNodeChildren.get(i));
            }
        }
        else {
            for (int i = 0; i < length; ++i) {
                resultNode.insertBefore((Node) resultNodeChildren.get(i), resultNextSibling);
            }
        }  
    }
    
    private void append(Node node) {
        if (currentNode != null) {
            currentNode.appendChild(node);
        }
        else {
            /** Check if this node can be attached to the target. */
            if ((kidOK[resultNode.getNodeType()] & (1 << node.getNodeType())) == 0) {                
                throw new IllegalStateException("Could not append to specified DOMResult.");
            }
            resultNodeChildren.add(node);
        }
    }
}
