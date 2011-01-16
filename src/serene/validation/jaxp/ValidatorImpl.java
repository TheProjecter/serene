/*
Copyright 2010 Radu Cernuta 

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

import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Entity;
import org.w3c.dom.Notation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.File;

import java.util.Stack;

import javax.xml.XMLConstants;

import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.Schema;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stax.StAXResult;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.util.IntStack;

import sereneWrite.MessageWriter;


class ValidatorImpl extends Validator{
    String DTD_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdHandler";
    
	LSResourceResolver lsResourceResolver;
	
	ErrorDispatcher errorDispatcher;
	
	MessageWriter debugWriter;
	
	Schema schema;
	ValidatorHandler validatorHandler;
	
	// for DOM
	/**
	* Locator to be initiated with the systemId of the source and used for DOM
	* validation.
	*/
	LocatorImpl locator;
	/**
	* Attributes list as described in the org.xml.sax, updated for every element
	* and passed to the validator. Used to fire startElement events.
	*/
	AttributesImpl attributes;
	/**
	* Stack of all the namespace prefixes defined for the context. The prefixes 
	* are pushed on the stack in the begining of the start element processing and
	* are popped at the end of end element processing of the corresponding element. 
	* Used to fire endPrefixMapping events.
	*/
	Stack<String> prefixes;
	/**
	* Stack containing the count of namespace prefixes defined for the an element.
	* The count is pushed on the stack in the begining of start element processing
	* and is popped at the end of end element processing of the corresponding 
	* element. Used to know how many endPrefixMapping event must be fire, if any.
	*/
	IntStack prefixesCount;
	
	
	/** Chunk size (1024). */
    final int CHUNK_SIZE = (1 << 10);
    
    /** Chunk mask (CHUNK_SIZE - 1). */
    final int CHUNK_MASK = CHUNK_SIZE - 1;
	
	/** Array for holding character data. **/
    char[] chars = new char[CHUNK_SIZE];
	
	
	TransformerHandler identityTransformerHandler;
	
	ValidatorImpl(Schema schema, MessageWriter debugWriter){
		this.schema = schema;
		this.debugWriter = debugWriter;		
		
		errorDispatcher = new ErrorDispatcher(debugWriter);
		
		chars = new char[CHUNK_SIZE];
	}
	
	public ErrorHandler getErrorHandler(){
		return errorDispatcher.getErrorHandler();
	}
	
	public void setErrorHandler(ErrorHandler errorHandler){		
		errorDispatcher.setErrorHandler(errorHandler);		
	}
	
	public LSResourceResolver getResourceResolver(){
		return lsResourceResolver;
	} 
	
	public void setResourceResolver(LSResourceResolver lsResourceResolver){
		this.lsResourceResolver = lsResourceResolver;
	}
	
	public void validate(Source source) throws SAXException, IOException{
		if(source == null){
			throw new NullPointerException();
		}else if(source instanceof SAXSource){
			validate((SAXSource)source);
		}else if(source instanceof DOMSource){
			validate((DOMSource)source);
		}else if(source instanceof StreamSource){
			validate((StreamSource)source);
		}else if(source instanceof StAXSource){
			validate((StAXSource)source);
		}else{
			throw new IllegalArgumentException();
		}	
	}
	
	public void validate(Source source, Result result) throws SAXException, IOException{
		if(source == null){
			throw new NullPointerException();
		}else if(source instanceof SAXSource){
			validate((SAXSource)source, result);
		}else if(source instanceof DOMSource){
			validate((DOMSource)source, result);
		}else if(source instanceof StreamSource){
			validate((StreamSource)source);
		}else if(source instanceof StAXSource){
			validate((StAXSource)source, result);
		}else{
			throw new IllegalArgumentException();
		}
	}
	
	public void reset(){
		validatorHandler = null;
		lsResourceResolver = null;
		
		errorDispatcher = new ErrorDispatcher(debugWriter);
	}
	
	public void validate(SAXSource source, Result result) throws SAXException, IOException{
		if(result == null){
			validate(source);
			return;
		}		
		if(!(result instanceof SAXResult)) throw new IllegalArgumentException();
		validatorHandler.setContentHandler(((SAXResult)result).getHandler());
		validate(source);
	}
		
	public void validate(SAXSource source) throws SAXException, IOException{
		if(validatorHandler == null) validatorHandler = schema.newValidatorHandler();
		XMLReader xmlReader = source.getXMLReader();
		if(xmlReader == null){
			xmlReader = XMLReaderFactory.createXMLReader();					
			
			xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);			
			xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);		
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
		}
		InputSource inputSource = source.getInputSource();
		
		xmlReader.setContentHandler(validatorHandler);
        DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(DTD_HANDLER_PROPERTY);
        xmlReader.setDTDHandler(dtdHandler);
		xmlReader.setErrorHandler(errorDispatcher);
		validatorHandler.setErrorHandler(errorDispatcher);
		xmlReader.parse(inputSource);
	}
	
	public void validate(StreamSource source, Result result) throws SAXException, IOException{
		if(result == null){
			validate(source);
			return;
		}		
		if(!(result instanceof StreamResult)) throw new IllegalArgumentException();
				
		if(identityTransformerHandler == null) createTransformerHandler();
		
		identityTransformerHandler.setResult(result);
		validatorHandler.setContentHandler(identityTransformerHandler);
		validate(source);
	}
	
	public void validate(StreamSource source) throws SAXException, IOException{		
		InputSource inputSource;
		
		InputStream is = source.getInputStream();
		if(is == null){
			Reader r = source.getReader();
			if(r == null){				
				String si = source.getSystemId();
				if(si == null){
					throw new IOException("Cannot read source.");
				}else{
					inputSource = new InputSource(si);
				}
			}else{
				inputSource = new InputSource(r);
			}			
		}else{
			inputSource = new InputSource(is);
		}
				
		XMLReader xmlReader; 
		xmlReader = XMLReaderFactory.createXMLReader();		
			
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);			
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);		
		xmlReader.setFeature("http://xml.org/sax/features/validation", false);				
				
		if(validatorHandler == null) validatorHandler = schema.newValidatorHandler();
		
		xmlReader.setContentHandler(validatorHandler);
        DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(DTD_HANDLER_PROPERTY);
        xmlReader.setDTDHandler(dtdHandler);
		xmlReader.setErrorHandler(errorDispatcher);
		validatorHandler.setErrorHandler(errorDispatcher);
		xmlReader.parse(inputSource);
	}

	public void validate(StAXSource source, Result result) throws SAXException, IOException{
		if(result == null){
			validate(source);
			return;
		}		
		if(!(result instanceof StAXResult)) throw new IllegalArgumentException();
				
		if(identityTransformerHandler == null) createTransformerHandler();
		
		identityTransformerHandler.setResult(result);
		validatorHandler.setContentHandler(identityTransformerHandler);
		validate(source);
	}
	
	public void validate(StAXSource source)throws SAXException, IOException{	
		InputSource inputSource;		
		
		String si = source.getSystemId();
		if(si == null){
			throw new IOException("Cannot read source.");
		}else{
			inputSource = new InputSource(si);
		}
				
		XMLReader xmlReader; 
		xmlReader = XMLReaderFactory.createXMLReader();
					
			
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);			
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);		
		xmlReader.setFeature("http://xml.org/sax/features/validation", false);				
				
		if(validatorHandler == null) validatorHandler = schema.newValidatorHandler();
		
		xmlReader.setContentHandler(validatorHandler);
        DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(DTD_HANDLER_PROPERTY);
        xmlReader.setDTDHandler(dtdHandler);
		xmlReader.setErrorHandler(errorDispatcher);
		validatorHandler.setErrorHandler(errorDispatcher);
		xmlReader.parse(inputSource);
	}
	
	public void validate(DOMSource source) throws SAXException, IOException{
		Node node = source.getNode();
		if (node != null) {
			//create the ValidatorHandler			
			if(locator == null){
				locator = new LocatorImpl();
				locator.setSystemId(source.getSystemId());
				locator.setPublicId(null);
				locator.setLineNumber(-1);
				locator.setColumnNumber(-1);
			}
			if(validatorHandler == null) validatorHandler = schema.newValidatorHandler();
			validatorHandler.setDocumentLocator(locator);
			//create attributes
			if(attributes == null)attributes = new AttributesImpl();
			//create prefixes stacks
			if(prefixes == null) prefixes = new Stack<String>();
			else prefixes.clear();
			if(prefixesCount == null) prefixesCount = new IntStack();
			else prefixesCount.clear();
			
            try{
                handleDTDContext((Document)node, (DTDHandler)validatorHandler.getProperty(DTD_HANDLER_PROPERTY));
            }catch(ClassCastException e){}
			validate(node);
		}
	}
		
    private void handleDTDContext(Document document, DTDHandler dtdHandler) throws SAXException{
        DocumentType doctype = document.getDoctype();
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
    
	private void validate(Node node) throws SAXException{
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
	
	private void beginNode(Node node) throws SAXException{
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:				
                handleAttributes(node.getAttributes()); // fires startPrefixMapping
				
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
				validatorHandler.startElement(namespaceURI, localName, qName, attributes);
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
	
	private void handleAttributes(NamedNodeMap attrMap) throws SAXException{		
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

	private void handleCharacters(String string) throws SAXException{
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
	
	private void finishNode(Node node) throws SAXException{
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
	
	private void createTransformerHandler(){
		try {
			SAXTransformerFactory tf = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
			identityTransformerHandler = tf.newTransformerHandler();
		} catch (TransformerConfigurationException e) {
			throw new TransformerFactoryConfigurationError(e);
		}
	}
}