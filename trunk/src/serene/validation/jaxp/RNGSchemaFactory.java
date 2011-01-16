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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Stack;

import javax.xml.XMLConstants;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stax.StAXSource;


import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.AttributesImpl;

import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Entity;
import org.w3c.dom.Notation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

import org.relaxng.datatype.DatatypeException;

import sereneWrite.MessageWriter;

import serene.internal.InternalRNGFactory;
import serene.internal.InternalRNGSchema;

import serene.bind.ValidatorQueuePool;
import serene.bind.Queue;
import serene.bind.ElementTaskPool;
import serene.bind.ElementTask;
import serene.bind.BindingPool;
import serene.bind.BindingModel;

import serene.simplifier.RNGSimplifier;
import serene.restrictor.RestrictionController;

import serene.validation.DTDMapping;

import serene.validation.schema.parsed.ParsedComponentBuilder;
import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.parsed.components.Pattern;

import serene.validation.schema.simplified.SimplifiedModel;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.util.IntStack;

// SPECIFICATION
// 		not thread-safe; client application's responsibility
//		not re-entrant; while one newSchema is invoked, applications must not 
//		attemp to recursively invoke the newSchema method, even from the same thread
/**
*
*
*/
public class RNGSchemaFactory extends SchemaFactory{
    String DTD_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdHandler";
    String DTD_MAPPING_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdMapping";	
	
	private LSResourceResolver resourceResolver;
	
	private XMLReader xmlReader;
	private InternalRNGFactory internalRNGFactory;	
	private ValidatorHandler validatorHandler;
	private Queue queue;
	private ParsedComponentBuilder parsedComponentBuilder;
	
	private RNGSimplifier simplifier;
	private RestrictionController restrictionController;

	private boolean replaceMissingDatatypeLibrary;
	private boolean parsedModelSchema;
		
	
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
	
	
	ErrorDispatcher errorDispatcher;

	MessageWriter debugWriter;
	
	public RNGSchemaFactory(){		
		errorDispatcher = new ErrorDispatcher(null);
		
		initDefaultFeatures();
		initDefaultProperties();	
				
		try{
		    createParser();
        }catch(DatatypeException e){
            throw new IllegalStateException(e.getMessage());
        }
		createSimplifier();
		createRestrictionController();
	}
	
	public RNGSchemaFactory(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		errorDispatcher = new ErrorDispatcher(debugWriter);
		
		initDefaultFeatures();
		initDefaultProperties();	
				
		try{
		    createParser();
        }catch(DatatypeException e){
            throw new IllegalStateException(e.getMessage());
        }
		createSimplifier();
		createRestrictionController();		
	}
	
	private void initDefaultFeatures(){
		replaceMissingDatatypeLibrary = true;
		parsedModelSchema = false;	
	}
	
	private void initDefaultProperties(){		
	}
	
		
	private void createParser()  throws DatatypeException{
		internalRNGFactory = InternalRNGFactory.getInstance(debugWriter);
		
		parsedComponentBuilder = new ParsedComponentBuilder(debugWriter);
		
		InternalRNGSchema schema = internalRNGFactory.getInternalRNGSchema();
		
		ElementTaskPool startDummyPool = internalRNGFactory.getStartDummyPool();
		ElementTask startDummyTask = startDummyPool.getTask();
		startDummyTask.setExecutant(parsedComponentBuilder);
		
		ElementTaskPool endDummyPool = internalRNGFactory.getEndDummyPool();
		ElementTask endDummyTask = endDummyPool.getTask();
		endDummyTask.setExecutant(parsedComponentBuilder);
		
		BindingPool bindingPool = internalRNGFactory.getRNGParseBindingPool();
		ValidatorQueuePool queuePool = bindingPool.getValidatorQueuePool();
		queue = queuePool.getQueue();
		try{
			bindingPool.setProperty("builder", parsedComponentBuilder);
			queue.setFeature("useReservationStartDummyElementTask", true);
			queue.setProperty("reservationStartDummyElementTask", startDummyTask);
			queue.setFeature("useReservationEndDummyElementTask", true);
			queue.setProperty("reservationEndDummyElementTask", endDummyTask);
		}catch(SAXNotRecognizedException snre){
			snre.printStackTrace();
		}
				
		BindingModel model = bindingPool.getBindingModel();
		
		validatorHandler = schema.newValidatorHandler(model, queue, queuePool);
		//validatorHandler = rngSchema.newValidatorHandler();
		validatorHandler.setErrorHandler(errorDispatcher);		

		
		
		try{
			xmlReader = XMLReaderFactory.createXMLReader();
			//TODO see that the features are correctly set			
			try {
				xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);			
				xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);		
				xmlReader.setFeature("http://xml.org/sax/features/validation", false);				
			}catch (SAXNotRecognizedException e) {
				e.printStackTrace();
			}
		}catch(SAXException e){
			e.printStackTrace();
		}		
		xmlReader.setErrorHandler(errorDispatcher);				
	}
	private void createSimplifier(){
		simplifier = new RNGSimplifier(xmlReader, internalRNGFactory, errorDispatcher, debugWriter);		
		simplifier.setReplaceMissingDatatypeLibrary(replaceMissingDatatypeLibrary);
	}
	private void createRestrictionController(){
		restrictionController = new RestrictionController(errorDispatcher, debugWriter);
	}
	//------------------------------------------------------------------------------------------
	//START methods of the javax.xml.validation.SchemaFactory  
	//------------------------------------------------------------------------------------------	
	public void setErrorHandler(ErrorHandler errorHandler){		
		errorDispatcher.setErrorHandler(errorHandler);		
		//All errors generated during schema reading, parsing and compiling must 
		// pass through the errorDispatcher. It	is passed as ErrorHandler to
		//	- xmlReader
		//	- contentHandler(ValidatorHandler)
		//	- simplifier
		//	- restrictionController
	}
	
	public ErrorHandler getErrorHandler(){
		return errorDispatcher.getErrorHandler();
	}
    
	public void setResourceResolver(LSResourceResolver resourceResolver){
		this.resourceResolver = resourceResolver;
	}  
	
	public LSResourceResolver getResourceResolver(){
		return resourceResolver;
	}
    
	public void setFeature(String name, boolean value){
		if (name == null) {
            throw new NullPointerException();
        }
		if(name.equals("http://serenerng.org/features/schemaFactory/replaceMissingDatatypeLibrary")){
			replaceMissingDatatypeLibrary = value;
			simplifier.setReplaceMissingDatatypeLibrary(value);
			return;	
		}else if(name.equals("http://serenerng.org/features/schemaFactory/preserveParsedModel")){
			parsedModelSchema = value;
			return;	
		}		
		throw new IllegalArgumentException("Unknown feature.");
	}
	
	public boolean getFeature(String name){
		if(name.equals("http://serenerng.org/features/schemaFactory/replaceMissingDatatypeLibrary")){
			return replaceMissingDatatypeLibrary;
		}else if(name.equals("http://serenerng.org/features/schemaFactory/preserveParsedModel")){
			return parsedModelSchema;
		}
		throw new IllegalArgumentException("Unknown feature.");
	}
	
	public void setProperty(String name, Object object){
		if (name == null) {
            throw new NullPointerException();
        }
		throw new IllegalArgumentException("Unknown property.");
	}    
    
	public Object getProperty(String name){
		if (name == null) {
            throw new NullPointerException();
        }
		throw new IllegalArgumentException("Unknown property.");
	}
    
	public boolean isSchemaLanguageSupported(String schemaLanguage){
		if(schemaLanguage == null) throw new NullPointerException();
		if(schemaLanguage.length() == 0) throw new IllegalArgumentException();
		return schemaLanguage.equals(XMLConstants.RELAXNG_NS_URI);
	}
	
	
	public Schema newSchema(){		
		throw new UnsupportedOperationException();
	}
	
	public Schema newSchema(Source[] schemas){
		throw new UnsupportedOperationException();
	}  
			
	public Schema newSchema(File file) throws SAXException {
        // TODO see about the JAXP 1.4 features clarification
		if(file == null) throw new NullPointerException();
        return newSchema(new InputSource(file.toURI().toASCIIString()));
    }
	
	public Schema newSchema(URL url) throws SAXException {
        // TODO see about the JAXP 1.4 features clarification
		if(url == null) throw new NullPointerException();
        return newSchema(new InputSource(url.toExternalForm()));
    }
	
	public Schema newSchema(Source source) throws SAXException{
        // TODO see about the JAXP 1.4 features clarification
		if(source == null){
			throw new NullPointerException();
		}else if(source instanceof SAXSource){
			return newSchema((SAXSource)source);
		}else if(source instanceof DOMSource){
			return newSchema((DOMSource)source);
		}else if(source instanceof StreamSource){
			return newSchema((StreamSource)source);
		}else if(source instanceof StAXSource){
			return newSchema((StAXSource)source);
		}else{
			throw new IllegalArgumentException();
		}	
	} 
	
	public Schema newSchema(SAXSource source) throws SAXException{
        // TODO see about the JAXP 1.4 features clarification		
		InputSource inputSource = source.getInputSource();
		if(inputSource.getSystemId() == null) inputSource.setSystemId(source.getSystemId());		
		return newSchema(inputSource);
	}
	
	
	public Schema newSchema(StAXSource source)throws SAXException{
        // TODO see about the JAXP 1.4 features clarification
		InputSource inputSource;		
		String si = source.getSystemId();
		if(si == null){
			throw new SAXException("Cannot read source: "+source.toString()+".");
		}else{
			inputSource = new InputSource(si);
		}				
		return newSchema(inputSource);
	}
	
	public Schema newSchema(StreamSource source) throws SAXException{
        // TODO see about the JAXP 1.4 features clarification		
		InputSource inputSource;		
		InputStream is = source.getInputStream();
		if(is == null){
			Reader r = source.getReader();
			if(r == null){				
				String si = source.getSystemId();
				if(si == null){
					throw new SAXException("Cannot read source: "+source.toString()+".");
				}else{
					inputSource = new InputSource(si);
				}
			}else{
				inputSource = new InputSource(r);
				if(inputSource.getSystemId() == null) inputSource.setSystemId(source.getSystemId());
			}			
		}else{
			inputSource = new InputSource(is);
			if(inputSource.getSystemId() == null) inputSource.setSystemId(source.getSystemId());
		}
		
		return newSchema(inputSource);
	}
	
	private Schema newSchema(InputSource inputSource) throws SAXException{
		String systemId = inputSource.getSystemId();		
		
		//read schema
		xmlReader.setContentHandler(validatorHandler);
        DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(DTD_HANDLER_PROPERTY);
        xmlReader.setDTDHandler(dtdHandler);
		try{
			xmlReader.parse(inputSource);
		}catch(IOException e){			
			throw new SAXException(e.getMessage(), e);
		}
		
		//build parsed model
		parsedComponentBuilder.startBuild();
		queue.executeAll();		
		Pattern p = parsedComponentBuilder.getCurrentPattern();
		if(p == null) {
			// !!! MUST always return a non-null Schema object, meaningfull or not.
			return new RNGSchema(null, null, debugWriter);
		}		
        DTDMapping dtdMapping = (DTDMapping)validatorHandler.getProperty(DTD_MAPPING_PROPERTY);        
		ParsedModel pm = new ParsedModel(dtdMapping, p, debugWriter);
        
		//build simplified model
		SimplifiedModel sm = null;		
		if(systemId != null){
			try{
				sm = simplifier.simplify(new URI(systemId), pm);
			}catch(URISyntaxException use){
				throw new SAXException(use.getMessage());
			}
		}else{
			sm = simplifier.simplify(null, pm);
		}
		
		//apply restrictions
		if(sm != null) restrictionController.control(sm);
		
		//create schema		
		// !!! MUST always return a non-null Schema object, meaningfull or not.
		RNGSchema schema = new RNGSchema(pm, sm, debugWriter);
		return schema;
	}
	
	
	public Schema newSchema(DOMSource source) throws SAXException{
		//read schema
		Node node = source.getNode();
		String systemId = source.getSystemId();
		if (node != null) {			
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
			
            try{
                handleDTDContext((Document)node, (DTDHandler)validatorHandler.getProperty(DTD_HANDLER_PROPERTY));
            }catch(ClassCastException e){}
			validate(node);
		}
		
		
		//build parsed model
		parsedComponentBuilder.startBuild();
		queue.executeAll();		
		Pattern p = parsedComponentBuilder.getCurrentPattern();
		if(p == null) {
			// !!! MUST always return a non-null Schema object, meaningfull or not.
			return new RNGSchema(null, null, debugWriter);
		}
		
		DTDMapping dtdMapping = (DTDMapping)validatorHandler.getProperty(DTD_MAPPING_PROPERTY);        
		ParsedModel pm = new ParsedModel(dtdMapping, p, debugWriter);
		
		//build simplified model
		SimplifiedModel sm = null;		
		if(systemId != null){
			try{
				sm = simplifier.simplify(new URI(systemId), pm);
			}catch(URISyntaxException use){
				throw new SAXException(use.getMessage());
			}
		}else{
			sm = simplifier.simplify(null, pm);
		}
		
		//apply restrictions
		if(sm != null) restrictionController.control(sm);
		else return null;
		
		//create schema		
		// !!! MUST always return a non-null Schema object, meaningfull or not.
		RNGSchema schema = new RNGSchema(pm, sm, debugWriter);
		return schema;
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
			if(namespaceURI == null) namespaceURI = XMLConstants.NULL_NS_URI;
			
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
			
            if (namespaceURI == XMLConstants.XMLNS_ATTRIBUTE_NS_URI) {      
				prefixCount++;
                							
                if (localName.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                    validatorHandler.startPrefixMapping(XMLConstants.NULL_NS_URI, value);
					prefixes.push(XMLConstants.NULL_NS_URI);
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
	//------------------------------------------------------------------------------------------
	//END methods of the javax.xml.validation.SchemaFactory
	//------------------------------------------------------------------------------------------
	
	public String toString(){
		return "RNGSchemaFactory";
	}
}