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


import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.File;

import java.util.Stack;

import javax.xml.XMLConstants;

import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;


import serene.validation.handlers.error.ErrorDispatcher;

import serene.util.IntStack;

import serene.Constants;

import sereneWrite.MessageWriter;


class ValidatorImpl extends Validator{    
	LSResourceResolver lsResourceResolver;
	
	ErrorDispatcher errorDispatcher;
	
	MessageWriter debugWriter;
	
	ValidatorHandler validatorHandler;
	
    DOMHandler domHandler;
    DOMAugmentingHandler domAugmentingHandler;
    DOMBuildingHandler domBuildingHandler;

    StAXHandler stAXHandler;
 	StAXStreamBuildingHandler stAXStreamBuildingHandler;
    StAXEventBuildingHandler stAXEventBuildingHandler;
    
	TransformerHandler identityTransformerHandler;
	
    boolean secureProcessing;
    boolean namespacePrefixes;
    boolean level1AttributeDefaultValue;
    boolean level2AttributeDefaultValue;
    
    
    //boolean defaultSecureProcessing; can't be changed anyway
    boolean defaultNamespacePrefixes;
    //boolean defaultLevel1AttributeDefaultValue; can't be changed anyway
    boolean defaultLevel2AttributeDefaultValue;
    
	ValidatorImpl(boolean secureProcessing,                            
                    boolean namespacePrefixes,
                    boolean level1AttributeDefaultValue,
                    boolean level2AttributeDefaultValue,
                    ValidatorHandler validatorHandler, 
                    MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
        
        this.secureProcessing = secureProcessing;
        this.namespacePrefixes = namespacePrefixes; 
        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
        this.level2AttributeDefaultValue = level2AttributeDefaultValue;
		
        
        //defaultSecureProcessing = secureProcessing;
        defaultNamespacePrefixes = namespacePrefixes;
        //defaultLevel1AttributeDefaultValue = level1AttributeDefaultValue;
        defaultLevel2AttributeDefaultValue = level2AttributeDefaultValue;
        
		this.validatorHandler = validatorHandler;
        
		errorDispatcher = new ErrorDispatcher(debugWriter);
        
        validatorHandler.setErrorHandler(errorDispatcher);
	}
	
    public void reset(){
        try{
            validatorHandler.setFeature(Constants.NAMESPACES_PREFIXES_SAX_FEATURE, defaultNamespacePrefixes);
            validatorHandler.setFeature(Constants.LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE, defaultLevel2AttributeDefaultValue);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
        }
	}
    
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException{
        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            namespacePrefixes = value;  
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE)){
            level2AttributeDefaultValue = value;                        
        }else{
            throw new SAXNotRecognizedException(name);
        }
        validatorHandler.setFeature(name, value);
    }
    
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException{
        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            return namespacePrefixes;  
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE)){
            return level2AttributeDefaultValue;
        }else{
            throw new SAXNotRecognizedException(name);
        }        
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
		if(source == null)throw new NullPointerException();        
        if(result == null){
			validate(source);
			return;
		}
        
        if(source instanceof SAXSource){
			validate((SAXSource)source, result);
		}else if(source instanceof DOMSource){
			validate((DOMSource)source, result);
		}else if(source instanceof StreamSource){
			validate((StreamSource)source, result);
		}else if(source instanceof StAXSource){
			validate((StAXSource)source, result);
		}else{
			throw new IllegalArgumentException();
		}
	}
	
	public void validate(SAXSource source, Result result) throws SAXException, IOException{
        if(source == null)throw new NullPointerException();
        if(result == null)validate(source);
		if(!(result instanceof SAXResult)) throw new IllegalArgumentException();        
		validatorHandler.setContentHandler(((SAXResult)result).getHandler());
		validate(source);
        validatorHandler.setContentHandler(null);
	}
		
	public void validate(SAXSource source) throws SAXException, IOException{
		XMLReader xmlReader = source.getXMLReader();
		if(xmlReader == null){
			xmlReader = XMLReaderFactory.createXMLReader();					
			
			xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);			
			xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);		
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
		}
		InputSource inputSource = source.getInputSource();
		
		xmlReader.setContentHandler(validatorHandler);
        DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY);
        xmlReader.setDTDHandler(dtdHandler);
		xmlReader.setErrorHandler(errorDispatcher);		
		xmlReader.parse(inputSource);
	}
	
	public void validate(StreamSource source, Result result) throws SAXException, IOException{
        if(source == null)throw new NullPointerException();
		if(result == null) validate(source);
		if(!(result instanceof StreamResult)) throw new IllegalArgumentException();				
		if(identityTransformerHandler == null) createTransformerHandler();		
		identityTransformerHandler.setResult(result);
		validatorHandler.setContentHandler(identityTransformerHandler);
		validate(source);
        validatorHandler.setContentHandler(null);
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
		
		xmlReader.setContentHandler(validatorHandler);
        DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY);
        xmlReader.setDTDHandler(dtdHandler);
		xmlReader.setErrorHandler(errorDispatcher);
		xmlReader.parse(inputSource);
	}

	public void validate(StAXSource source, Result result) throws SAXException, IOException{
        if(source == null)throw new NullPointerException();
        if(result == null)validate(source);		
		if(!(result instanceof StAXResult)) throw new IllegalArgumentException();
        StAXResult stAXResult = (StAXResult)result;
        
        XMLStreamWriter xmlStreamWriter = stAXResult.getXMLStreamWriter();
        XMLEventWriter xmlEventWriter = stAXResult.getXMLEventWriter();
        
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
                if(xmlStreamWriter != null){
                    if(level2AttributeDefaultValue){                        
                        if(stAXStreamBuildingHandler == null) stAXStreamBuildingHandler = new StAXStreamBuildingHandler(debugWriter);
                        stAXStreamBuildingHandler.setLevel2AttributeDefaultValue(level2AttributeDefaultValue);
                        stAXStreamBuildingHandler.handle(source.getSystemId(), validatorHandler, xmlStreamReader, xmlStreamWriter);
                        stAXStreamBuildingHandler.setLevel2AttributeDefaultValue(false);
                    }else{
                        if(stAXStreamBuildingHandler == null) stAXStreamBuildingHandler = new StAXStreamBuildingHandler(debugWriter);
                        stAXStreamBuildingHandler.handle(source.getSystemId(), validatorHandler, xmlStreamReader, xmlStreamWriter);                        
                    }
                }else{
                    if(level2AttributeDefaultValue){
                        if(stAXEventBuildingHandler == null) stAXEventBuildingHandler = new StAXEventBuildingHandler(debugWriter);
                        stAXEventBuildingHandler.setLevel2AttributeDefaultValue(level2AttributeDefaultValue);
                        stAXEventBuildingHandler.handle(source.getSystemId(), validatorHandler, xmlStreamReader, xmlEventWriter);
                        stAXEventBuildingHandler.setLevel2AttributeDefaultValue(false);
                    }else{
                        if(stAXEventBuildingHandler == null) stAXEventBuildingHandler = new StAXEventBuildingHandler(debugWriter);
                        stAXEventBuildingHandler.handle(source.getSystemId(), validatorHandler, xmlStreamReader, xmlEventWriter);
                    }
                }                
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
                if(xmlStreamWriter != null){
                    if(level2AttributeDefaultValue){
                        if(stAXStreamBuildingHandler == null) stAXStreamBuildingHandler = new StAXStreamBuildingHandler(debugWriter);
                        stAXStreamBuildingHandler.setLevel2AttributeDefaultValue(level2AttributeDefaultValue);                        
                        stAXStreamBuildingHandler.handle(source.getSystemId(), validatorHandler, xmlEventReader, xmlStreamWriter);
                        stAXStreamBuildingHandler.setLevel2AttributeDefaultValue(false);
                    }else{                        
                        if(stAXStreamBuildingHandler == null) stAXStreamBuildingHandler = new StAXStreamBuildingHandler(debugWriter);
                        stAXStreamBuildingHandler.handle(source.getSystemId(), validatorHandler, xmlEventReader, xmlStreamWriter);
                    }
                }else{
                    if(level2AttributeDefaultValue){
                        if(stAXEventBuildingHandler == null) stAXEventBuildingHandler = new StAXEventBuildingHandler(debugWriter);
                        stAXEventBuildingHandler.setLevel2AttributeDefaultValue(level2AttributeDefaultValue);
                        stAXEventBuildingHandler.handle(source.getSystemId(), validatorHandler, xmlEventReader, xmlEventWriter);
                        stAXEventBuildingHandler.setLevel2AttributeDefaultValue(false);
                    }else{
                        if(stAXEventBuildingHandler == null) stAXEventBuildingHandler = new StAXEventBuildingHandler(debugWriter);
                        stAXEventBuildingHandler.handle(source.getSystemId(), validatorHandler, xmlEventReader, xmlEventWriter);
                    }
                }
             }catch(XMLStreamException e){
                 throw new SAXException(e);
             }
         }
	}
	
	public void validate(StAXSource source)throws SAXException, IOException{         
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
	}
	
    public void validate(DOMSource source, Result result) throws SAXException, IOException{
        if(source == null)throw new NullPointerException();
        if(result == null)validate(source);
		if(!(result instanceof DOMResult)) throw new IllegalArgumentException();
			
        Node sourceNode = source.getNode();   
        if(sourceNode == null) throw new IllegalArgumentException("Source does not represent an XML artifact. Input is expected to be XML documents or elements.");
        int type = sourceNode.getNodeType();
        if(!(type == Node.ELEMENT_NODE || type == Node.DOCUMENT_NODE))
            throw new IllegalArgumentException("Source represents an XML artifact that cannot be validated. Input is expected to be XML documents or elements.");
		
        DOMResult domResult = (DOMResult)result; 
        Node resultNode = domResult.getNode();
        
        String systemId = source.getSystemId();
        
        if(resultNode == sourceNode){
            if(level2AttributeDefaultValue){
                if(domAugmentingHandler == null) domAugmentingHandler = new DOMAugmentingHandler(debugWriter);
                domAugmentingHandler.setLevel2AttributeDefaultValue(level2AttributeDefaultValue);
                domAugmentingHandler.handle(systemId, validatorHandler, sourceNode, resultNode);
                domAugmentingHandler.setLevel2AttributeDefaultValue(false);
            }else{
                if(domHandler == null) domHandler = new DOMHandler(debugWriter);
                domHandler.handle(systemId, validatorHandler, sourceNode);
            }
        }else{
            if(resultNode == null){
                try{
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    resultNode = builder.newDocument();
                    domResult.setNode(resultNode);
                } catch (ParserConfigurationException e) {
                    throw new SAXException(e);
                }
            }            
            if(domBuildingHandler == null) domBuildingHandler = new DOMBuildingHandler(debugWriter);
            if(level2AttributeDefaultValue) domBuildingHandler.setLevel2AttributeDefaultValue(level2AttributeDefaultValue);
            domBuildingHandler.handle(systemId, validatorHandler, sourceNode, resultNode);
            if(level2AttributeDefaultValue) domBuildingHandler.setLevel2AttributeDefaultValue(false);
        }    
	}
    
	public void validate(DOMSource source) throws SAXException, IOException{
		Node node = source.getNode();        
        if(node == null) throw new IllegalArgumentException("Source does not represent an XML artifact. Input is expected to be XML documents or elements.");
        int type = node.getNodeType();
        if(!(type == Node.ELEMENT_NODE || type == Node.DOCUMENT_NODE))
            throw new IllegalArgumentException("Source represents an XML artifact that cannot be validated. Input is expected to be XML documents or elements.");
		
        String systemId = source.getSystemId();        
        if(domHandler == null) domHandler = new DOMHandler(debugWriter);
        domHandler.handle(systemId, validatorHandler, node);        
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