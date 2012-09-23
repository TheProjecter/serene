/*
Copyright 2012 Radu Cernuta 

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

import java.net.URI;

import javax.xml.XMLConstants;

import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.TypeInfoProvider;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXResult;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.Attributes;

import org.w3c.dom.ls.LSResourceResolver;


import net.sf.saxon.Controller;
import net.sf.saxon.serialize.MessageWarner;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.Constants;

public class SchematronValidatorHandlerImpl extends ValidatorHandler{    
	ContentHandler contentHandler;	
	LSResourceResolver lsResourceResolver;
	TypeInfoProvider typeInfoProvider;
	Locator locator;

    boolean secureProcessing;
    boolean namespacePrefixes;
    boolean restrictToFileName;
    boolean optimizedForResourceSharing;
        
    ErrorDispatcher errorDispatcher;
    
    TransformerHandler validatingTransformerHandler;
    SVRLParser svrlParser;
    
    Templates schemaTemplates;
    SAXTransformerFactory saxTransformerFactory;
                            
	public SchematronValidatorHandlerImpl(boolean secureProcessing,                            
                            boolean namespacePrefixes,
                            boolean restrictToFileName,
                            boolean optimizedForResourceSharing,
                            Templates schemaTemplates,                            
                            SAXTransformerFactory saxTransformerFactory,
                            SVRLParser svrlParser){        
        this.secureProcessing = secureProcessing;
        this.namespacePrefixes = namespacePrefixes;
        this.schemaTemplates = schemaTemplates;
        this.saxTransformerFactory = saxTransformerFactory;
        this.restrictToFileName = restrictToFileName;
        this.optimizedForResourceSharing = optimizedForResourceSharing;
        this.svrlParser = svrlParser;
        
		errorDispatcher = new ErrorDispatcher();       
		
		// Create a SAXResult for the validatingTransformerHandler where the
		// ContentHandler parses SVRL into error messages and passes them to the
		// errorDispatcher.
		
	
		svrlParser.setErrorHandler(errorDispatcher);
		
	}
	
	void createValidatingTransformerHandler() throws SAXException{
	    try{
            validatingTransformerHandler = saxTransformerFactory.newTransformerHandler(schemaTemplates);
        }catch(TransformerConfigurationException tce){
            throw new SAXException(tce);
        }
        
        validatingTransformerHandler.setResult(new SAXResult(svrlParser));
		//validatingTransformerHandler.getTransformer().setErrorListener(errorDispatcher);
		Transformer t = validatingTransformerHandler.getTransformer();            
        ((Controller)t).setMessageEmitter(new MessageWarner());
        t.setErrorListener(errorDispatcher);
	}
	
	public ContentHandler getContentHandler(){
		return contentHandler;		
	}
	
	public void setContentHandler(ContentHandler contentHandler){        
		this.contentHandler = contentHandler;
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
	
	public TypeInfoProvider getTypeInfoProvider(){
		return typeInfoProvider;
	}
	
	void setTypeInfoProvider(TypeInfoProvider typeInfoProvider){
		this.typeInfoProvider = typeInfoProvider;
	}
	
	
	public void processingInstruction(String target, String data) throws SAXException{
	    validatingTransformerHandler.processingInstruction(target, data);
        if(contentHandler != null) contentHandler.processingInstruction(target, data);
    } 
    
	public void skippedEntity(String name) throws SAXException{
	    validatingTransformerHandler.skippedEntity(name);
		if(contentHandler != null) contentHandler.skippedEntity(name);
    }
	
	public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException{
	    validatingTransformerHandler.ignorableWhitespace(ch, start, len);
        if(contentHandler != null) contentHandler.ignorableWhitespace(ch, start, len);
    }
    
	public void startDocument()  throws SAXException{	
	    createValidatingTransformerHandler();
	    validatingTransformerHandler.setDocumentLocator(locator);
	    svrlParser.setDocumentLocator(locator);
	    
	    validatingTransformerHandler.startDocument();
		errorDispatcher.init();	 		
	}			
	public void startPrefixMapping(String prefix, String uri)  throws SAXException{
        validatingTransformerHandler.startPrefixMapping(prefix, uri);		
		if(contentHandler != null) contentHandler.startPrefixMapping(prefix, uri);
	}	
	public void endPrefixMapping(String prefix)  throws SAXException{
        validatingTransformerHandler.endPrefixMapping(prefix);		
        if(contentHandler != null) contentHandler.endPrefixMapping(prefix);
	}	
	public void setDocumentLocator(Locator locator){
	    if(validatingTransformerHandler != null)validatingTransformerHandler.setDocumentLocator(locator);
		this.locator = locator;
		svrlParser.setDocumentLocator(locator);
        if(contentHandler != null) contentHandler.setDocumentLocator(locator);
	}
	public void characters(char[] chars, int start, int length)throws SAXException{
        validatingTransformerHandler.characters(chars, start, length);		
        if(contentHandler != null) contentHandler.characters(chars, start, length);		
	}
	
	
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{
	    validatingTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
		if(contentHandler != null){
            contentHandler.startElement(namespaceURI, localName, qName, attributes);
        }
	}		
	
	public void endElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{		
	    validatingTransformerHandler.endElement(namespaceURI, localName, qName);
        if(contentHandler != null) contentHandler.endElement(namespaceURI, localName, qName);
	}
	
	public void endDocument()  throws SAXException {
	    svrlParser.setAcceptLocator(false);		// workaround for the extra locator from saxon
	    validatingTransformerHandler.endDocument();
	    svrlParser.setAcceptLocator(true);		// workaround for the extra locator from saxon
        if(contentHandler != null) contentHandler.endDocument();        
	}
		
	public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            namespacePrefixes = value;  
        }else if(name.equals(Constants.RESTRICT_TO_FILE_NAME_FEATURE)){
            restrictToFileName = value;
        }else{
            throw new SAXNotRecognizedException(name);
        }
    }
	
	public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            return namespacePrefixes;  
        }else if(name.equals(Constants.RESTRICT_TO_FILE_NAME_FEATURE)){
            return restrictToFileName;                        
        }else{
            throw new SAXNotRecognizedException(name);
        }
    }
    
    public void setProperty(String name, Object object)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (name == null) {
            throw new NullPointerException();
        }

        throw new SAXNotRecognizedException(name);
    }
   
    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (name == null) {
            throw new NullPointerException();
        }

        throw new SAXNotRecognizedException(name);
    }
}
