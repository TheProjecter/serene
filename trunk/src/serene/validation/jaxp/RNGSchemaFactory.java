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
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

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
import serene.restrictor.ControllerPool;

import serene.dtdcompatibility.CompatibilityHandler;
import serene.dtdcompatibility.DTDCompatibilityModelImpl;

import serene.validation.schema.ValidationModel;
import serene.validation.schema.ValidationModelImpl;

import serene.validation.schema.parsed.ParsedComponentBuilder;
import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.parsed.Pattern;

import serene.validation.schema.simplified.SimplifiedModel;

import serene.DTDMapping;
import serene.Constants;
import serene.SchemaModel;

import serene.validation.handlers.error.ErrorDispatcher;
import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.util.IntStack;

public class RNGSchemaFactory extends SchemaFactory{
	private LSResourceResolver resourceResolver;
	
	private XMLReader xmlReader;
    private DOMHandler domHandler;
    private StAXHandler stAXHandler;
	private InternalRNGFactory internalRNGFactory;	
	private ValidatorHandler validatorHandler;
	private Queue queue;
	private ParsedComponentBuilder parsedComponentBuilder;
	
	private RNGSimplifier simplifier;
	private RestrictionController restrictionController;
    private CompatibilityHandler compatibilityHandler;

    private boolean namespacePrefixes;
    private boolean level1AttributeDefaultValue;
    private boolean level2AttributeDefaultValue;
    private boolean level1AttributeIdType;
    private boolean level2AttributeIdType;	
	private boolean replaceMissingDatatypeLibrary;
	private boolean parsedModelSchema;	
	
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
        namespacePrefixes = false;            
        level1AttributeDefaultValue = true;
        level2AttributeDefaultValue = true;
        level1AttributeIdType = true;
        level2AttributeIdType = true;
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
        simplifier.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
        simplifier.setLevel1AttributeIdType(level1AttributeIdType);
	}
	private void createRestrictionController(){
		restrictionController = new RestrictionController(errorDispatcher, debugWriter);
	}
    private void createCompatibilityHandler(){
        try{
            ValidatorErrorHandlerPool vehp = (ValidatorErrorHandlerPool)validatorHandler.getProperty(Constants.ERROR_HANDLER_POOL_PROPERTY);
            ValidatorEventHandlerPool ehp = (ValidatorEventHandlerPool)validatorHandler.getProperty(Constants.EVENT_HANDLER_POOL_PROPERTY);
            ValidationItemLocator vil = (ValidationItemLocator)validatorHandler.getProperty(Constants.ITEM_LOCATOR_PROPERTY);
            ControllerPool cp = (ControllerPool)restrictionController.getProperty(Constants.CONTROLLER_POOL_PROPERTY);
            compatibilityHandler = new CompatibilityHandler(cp, vehp, ehp, vil, errorDispatcher, debugWriter);
        }catch (SAXNotRecognizedException e) {
            e.printStackTrace();
        }catch (SAXNotSupportedException e) {
            e.printStackTrace();
        }  
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
    
	public void setFeature(String name, boolean value) throws SAXNotSupportedException, SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)){
            if(value)throw new SAXNotSupportedException("Serene does not control yet the processing security.");            
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            namespacePrefixes = value;  
        }else if(name.equals(Constants.REPLACE_MISSING_LIBRARY_FEATURE)){
			replaceMissingDatatypeLibrary = value;
			simplifier.setReplaceMissingDatatypeLibrary(value);
		}else if(name.equals(Constants.PARSED_MODEL_SCHEMA_FEATURE)){
			parsedModelSchema = value;
		}else if(name.equals(Constants.LEVEL1_ATTRIBUTE_DEFAULT_VALUE_FEATURE)){
            level1AttributeDefaultValue = value;
            simplifier.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE)){
            level2AttributeDefaultValue = value;
            if(level2AttributeDefaultValue){
                level1AttributeDefaultValue = value;
                simplifier.setLevel1AttributeDefaultValue(level1AttributeDefaultValue);
            }
        }else if(name.equals(Constants.LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE)){
            level1AttributeIdType = value;
            simplifier.setLevel1AttributeIdType(level1AttributeIdType);
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_ID_TYPE_FEATURE)){
            level2AttributeIdType = value;
            if(level2AttributeIdType){
                level1AttributeIdType = value;
                simplifier.setLevel1AttributeIdType(level1AttributeIdType);
            }
        }else{
            throw new SAXNotRecognizedException("Unknown feature.");
        }
	}
	
	
	public boolean getFeature(String name) throws SAXNotSupportedException, SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }
		       
        if(name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)){
            return false;            
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            return namespacePrefixes;    
        }else if(name.equals(Constants.REPLACE_MISSING_LIBRARY_FEATURE)){
			return replaceMissingDatatypeLibrary;
		}else if(name.equals(Constants.PARSED_MODEL_SCHEMA_FEATURE)){
			return parsedModelSchema;
		}else if(name.equals(Constants.LEVEL1_ATTRIBUTE_DEFAULT_VALUE_FEATURE)){
            return level1AttributeDefaultValue;
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE)){
            return level2AttributeDefaultValue;
        }else if(name.equals(Constants.LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE)){
            return level1AttributeIdType;
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_ID_TYPE_FEATURE)){
            return level2AttributeIdType;
        }else{
        	throw new SAXNotRecognizedException("Unknown feature.");
        }
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
		if(file == null) throw new NullPointerException();
        
        errorDispatcher.init();
        
        InputSource inputSource = new InputSource(file.toURI().toASCIIString());        
        xmlReader.setContentHandler(validatorHandler);
        DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY);
        xmlReader.setDTDHandler(dtdHandler);
        try{
            xmlReader.parse(inputSource);
        }catch(IOException e){			
            throw new SAXException(e.getMessage(), e);
        }    
        
        ParsedModel pm = getParsedModel();
        return newSchema(inputSource.getSystemId(), pm);
    }
	
	public Schema newSchema(URL url) throws SAXException {
		if(url == null) throw new NullPointerException();
        
        errorDispatcher.init();
        
        InputSource inputSource = new InputSource(url.toExternalForm());        
        xmlReader.setContentHandler(validatorHandler);
        DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY);
        xmlReader.setDTDHandler(dtdHandler);
        try{
            xmlReader.parse(inputSource);
        }catch(IOException e){			
            throw new SAXException(e.getMessage(), e);
        }    
        
        ParsedModel pm = getParsedModel();
        return newSchema(inputSource.getSystemId(), pm);
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
        if(source == null) throw new NullPointerException();
		
		InputSource inputSource = source.getInputSource();        
		if(inputSource.getSystemId() == null) inputSource.setSystemId(source.getSystemId()); // TODO review this        
        XMLReader sourceReader = source.getXMLReader();
                
        errorDispatcher.init();
        
        //read schema
		if(sourceReader != null){            
            sourceReader.setContentHandler(validatorHandler);
            DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY);
            sourceReader.setDTDHandler(dtdHandler);
            try{
                sourceReader.parse(inputSource);
            }catch(IOException e){			
                throw new SAXException(e.getMessage(), e);
            }
		}else{
            xmlReader.setContentHandler(validatorHandler);
            DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY);
            xmlReader.setDTDHandler(dtdHandler);
            try{
                xmlReader.parse(inputSource);
            }catch(IOException e){			
                throw new SAXException(e.getMessage(), e);
            }        
        }

        ParsedModel pm = getParsedModel();
        return newSchema(inputSource.getSystemId(), pm);		
	}
	
	
	public Schema newSchema(StAXSource source)throws SAXException{
        if(source == null) throw new NullPointerException();
        
        errorDispatcher.init();
        
        // read schema
        XMLStreamReader xmlStreamReader = source.getXMLStreamReader();         
        if(xmlStreamReader != null){
            try{
                if(xmlStreamReader == null)throw new IllegalArgumentException("Source does not represent an XML artifact. Input is expected to be XML documents or elements.");
                // check for document or element, else error
                if(!xmlStreamReader.hasNext()){
                    throw new IllegalArgumentException("Source does not represent an XML artifact. Input is expected to be XML documents or elements.");
                }else if(!(xmlStreamReader.getEventType() == XMLStreamConstants.START_DOCUMENT
                    || xmlStreamReader.getEventType() == XMLStreamConstants.START_ELEMENT)){
                   throw new IllegalArgumentException("Source represents an XML artifact that cannot be validated. Input is expected to be XML documents or elements.");
                }
                // create handler and validate
                if(stAXHandler == null) stAXHandler = new StAXHandler(debugWriter);
                stAXHandler.handle(source.getSystemId(), validatorHandler, xmlStreamReader);
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
        }else{
            XMLEventReader xmlEventReader = source.getXMLEventReader();
            // check for document or element, else error
            try{
                if(!xmlEventReader.hasNext()){
                    throw new IllegalArgumentException("Source does not represent an XML artifact. Input is expected to be XML documents or elements.");
                }else if(!(xmlEventReader.peek().getEventType() == XMLStreamConstants.START_DOCUMENT
                    || xmlEventReader.peek().getEventType() == XMLStreamConstants.START_ELEMENT)){
                   throw new IllegalArgumentException("Source represents an XML artifact that cannot be validated. Input is expected to be XML documents or elements.");
                }
                // create handler and validate             
                if(stAXHandler == null) stAXHandler = new StAXHandler(debugWriter);                 
                stAXHandler.handle(source.getSystemId(), validatorHandler, xmlEventReader);
            }catch(XMLStreamException e){
                throw new SAXException(e);
            }
        }
         
        ParsedModel pm = getParsedModel();
        return newSchema(source.getSystemId(), pm);	
	}
	
	public Schema newSchema(StreamSource source) throws SAXException{       
		if(source == null) throw new NullPointerException();
        
        errorDispatcher.init();
         
        // read schema
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
		
		try{
            xmlReader.parse(inputSource);
        }catch(IOException e){			
            throw new SAXException(e.getMessage(), e);
        }
        
        ParsedModel pm = getParsedModel();
        return newSchema(inputSource.getSystemId(), pm);	
	}
	
    
    public Schema newSchema(DOMSource source) throws SAXException{
        if(source == null) throw new NullPointerException();
        
        errorDispatcher.init();
		
        // read schema
        Node node = source.getNode();        
        if(node == null) throw new IllegalArgumentException("Source does not represent an XML artifact. Input is expected to be XML documents or elements.");
        int type = node.getNodeType();
        if(!(type == Node.ELEMENT_NODE || type == Node.DOCUMENT_NODE))
            throw new IllegalArgumentException("Source represents an XML artifact that cannot be validated. Input is expected to be XML documents or elements.");
		
        String systemId = source.getSystemId();        
        if(domHandler == null) domHandler = new DOMHandler(debugWriter);
        domHandler.handle(systemId, validatorHandler, node);
        
        ParsedModel pm = getParsedModel();
        return newSchema(node.getBaseURI(), pm);
	}
    //------------------------------------------------------------------------------------------
	//END methods of the javax.xml.validation.SchemaFactory
	//------------------------------------------------------------------------------------------
	
    
    private ParsedModel getParsedModel() throws SAXException{
        parsedComponentBuilder.startBuild();
		queue.executeAll();
        Pattern p = null;		
		try{
            p = (Pattern)parsedComponentBuilder.getCurrentParsedComponent();
        }catch(ClassCastException c){
            // syntax error, already handled
        }
        
        if(p == null)return null;
        
        DTDMapping dtdMapping = null;
        try{
            dtdMapping = (DTDMapping)validatorHandler.getProperty(Constants.DTD_MAPPING_PROPERTY);
        }catch(SAXNotRecognizedException e){
            throw new SAXException(e);
        }
		return new ParsedModel(dtdMapping, p, debugWriter);
    }
    
	private Schema newSchema(String systemId, ParsedModel parsedModel) throws SAXException{        
        SchemaModel schemaModel = null;
        
		if(parsedModel == null) {
			schemaModel = new SchemaModel(new ValidationModelImpl(null, null, debugWriter), new DTDCompatibilityModelImpl(null, null, debugWriter), debugWriter);
            RNGSchema schema  = new RNGSchema(false,
                                        namespacePrefixes,
                                        level1AttributeDefaultValue,
                                        level2AttributeDefaultValue,
                                        level1AttributeIdType,
                                        level2AttributeIdType,
                                        schemaModel, 
                                        debugWriter);
            return schema;
		}	
        
		//build simplified model
		SimplifiedModel simplifiedModel = null;		
		if(systemId != null){
			try{
				simplifiedModel = simplifier.simplify(new URI(systemId), parsedModel);
			}catch(URISyntaxException use){
				throw new SAXException(use.getMessage());
			}
		}else{
			simplifiedModel = simplifier.simplify(null, parsedModel);
		}
		
        if(!parsedModelSchema) parsedModel = null;
        
		if(simplifiedModel != null){
            restrictionController.control(simplifiedModel);
            if(errorDispatcher.hasUnrecoverableError()){
                schemaModel = new SchemaModel(new ValidationModelImpl(null, null, debugWriter), new DTDCompatibilityModelImpl(null, null, debugWriter), debugWriter);
            }else{
                ValidationModel vm = new ValidationModelImpl(parsedModel, simplifiedModel, debugWriter);
                if(level1AttributeDefaultValue || level1AttributeIdType){
                    if(compatibilityHandler == null){                
                        ValidatorErrorHandlerPool vehp = (ValidatorErrorHandlerPool)validatorHandler.getProperty(Constants.ERROR_HANDLER_POOL_PROPERTY);
                        ValidatorEventHandlerPool ehp = (ValidatorEventHandlerPool)validatorHandler.getProperty(Constants.EVENT_HANDLER_POOL_PROPERTY);
                        ValidationItemLocator vil = (ValidationItemLocator)validatorHandler.getProperty(Constants.ITEM_LOCATOR_PROPERTY);
                        ControllerPool cp = (ControllerPool)restrictionController.getProperty(Constants.CONTROLLER_POOL_PROPERTY);
                        compatibilityHandler = new CompatibilityHandler(cp, vehp, ehp, vil, errorDispatcher, debugWriter);                
                    }
                    if(level1AttributeDefaultValue) compatibilityHandler.setLevel1AttributeDefaultValue(true);
                    if(level1AttributeIdType) compatibilityHandler.setLevel1AttributeIdType(true);
                    schemaModel = compatibilityHandler.handle(vm);
                }
            }
        }else{
            schemaModel = new SchemaModel(new ValidationModelImpl(null, null, debugWriter), new DTDCompatibilityModelImpl(null, null, debugWriter), debugWriter);
        }
        
		RNGSchema schema  = new RNGSchema(false,
                                        namespacePrefixes,
                                        level1AttributeDefaultValue,
                                        level2AttributeDefaultValue,
                                        level1AttributeIdType,
                                        level2AttributeIdType,
                                        schemaModel, 
                                        debugWriter);
		return schema;
	}
	
	
	public String toString(){
		return "RNGSchemaFactory";
	}
}