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

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.URI;

import javax.xml.XMLConstants;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.TemplatesHandler;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.w3c.dom.ls.LSResourceResolver;

import serene.Constants;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.schematron.TransformerFactoryImpl;
import serene.schematron.URIResolver;

public class SchematronSchemaFactory extends SchemaFactory{    
    static final String SCHEMA_QLB_XSLT1 = "xslt";
    static final String SCHEMA_QLB_XSLT2 = "xslt2";
    
    int qlbProperty;
    
	private LSResourceResolver resourceResolver;
	
	private boolean secureProcessing = false;
    private boolean namespacePrefixes;    
    private boolean restrictToFileName;	
    private boolean optimizedForResourceSharing;
	
	ErrorDispatcher errorDispatcher;
		
	Transformer schemaDocumentTransformer;
	SAXResult resolvedIncludesResult;
    SAXResult resolvedAbstractPatternsResult;
    TransformerHandler schematronCompilerXSLT1;
    TransformerHandler schematronCompilerXSLT2;
    TemplatesHandler schemaTemplatesHandler;
    
    TransformerFactoryImpl schematronTransformerFactory;
    
    boolean qlbSupported;
    
	public SchematronSchemaFactory() throws SAXException{		
		errorDispatcher = new ErrorDispatcher();
		
		initDefaultFeatures();
				
		createTransformerFactory();
	}
	
	
	
	private void initDefaultFeatures(){
        namespacePrefixes = false;
        restrictToFileName = true;
        optimizedForResourceSharing = false;
	}
	
		
	private void createTransformerFactory() throws SAXException{
	    schematronTransformerFactory = new TransformerFactoryImpl();
	    schematronTransformerFactory.setURIResolver(new URIResolver());
	}
	
	private void createParser() throws SAXException{
        try{          
            schemaTemplatesHandler = schematronTransformerFactory.newTemplatesHandler(); // here the Templates object representing the compiled schema can be obtained
            
            schematronCompilerXSLT2 = schematronTransformerFactory.newTransformerHandler(new StreamSource(SchematronSchemaFactory.class.getResourceAsStream(Constants.ISO_SVRL_FOR_XSLT2_LOCATION)));
            schematronCompilerXSLT2.setResult(new SAXResult(schemaTemplatesHandler));
            
            schematronCompilerXSLT1 = schematronTransformerFactory.newTransformerHandler(new StreamSource(SchematronSchemaFactory.class.getResourceAsStream(Constants.ISO_SVRL_FOR_XSLT1_LOCATION)));
            schematronCompilerXSLT1.setResult(new SAXResult(schemaTemplatesHandler));
            
            TransformerHandler abstarctPatternsHandler = schematronTransformerFactory.newTransformerHandler(new StreamSource(SchematronSchemaFactory.class.getResourceAsStream(Constants.ISO_ABSTRACT_EXPAND_LOCATION)), this);
            resolvedAbstractPatternsResult = new SAXResult(); // content handler will be set according to qlbProperty and maybe adjusted
            abstarctPatternsHandler.setResult(resolvedAbstractPatternsResult);
            
            TransformerHandler includeHandler = schematronTransformerFactory.newTransformerHandler(new StreamSource(SchematronSchemaFactory.class.getResourceAsStream(Constants.ISO_DSDL_INCLUDE_LOCATION)));                        
            schemaDocumentTransformer = includeHandler.getTransformer(); // used to transform the schema Source 
            resolvedIncludesResult = new SAXResult(abstarctPatternsHandler); // result for the above transformation
        }catch(TransformerConfigurationException tce){
            throw new SAXException(tce);
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
    
	public void setFeature(String name, boolean value) throws SAXNotSupportedException, 
	                                                            SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)){
            if(value)throw new SAXNotSupportedException("Serene does not control yet the processing security.");            
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            namespacePrefixes = value;  
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
        }else if(name.equals(Constants.RESTRICT_TO_FILE_NAME_FEATURE)){
            return restrictToFileName;
        }else if(name.equals(Constants.OPTIMIZE_FOR_RESOURCE_SHARING_FEATURE)){
            return optimizedForResourceSharing;
        }else{
        	throw new SAXNotRecognizedException("Unknown feature.");
        }
	}
	
	public void setProperty(String name, Object object) throws SAXNotRecognizedException, SAXNotSupportedException{
		if (name == null) {
            throw new NullPointerException();
        }
        
		throw new SAXNotRecognizedException("Unknown property.");
	}    
    
	public Object getProperty(String name) throws SAXNotRecognizedException{
		if (name == null) {
            throw new NullPointerException();
        }
		throw new SAXNotRecognizedException("Unknown property.");
	}
    
	public boolean isSchemaLanguageSupported(String schemaLanguage){
		if(schemaLanguage == null) throw new NullPointerException();
		if(schemaLanguage.length() == 0) throw new IllegalArgumentException();
		return schemaLanguage.equals(Constants.SCHEMATRON_NS_URI);
	}
		
	public Schema newSchema(){		
		throw new UnsupportedOperationException();
	}
	
	public Schema newSchema(Source[] schemas){
		throw new UnsupportedOperationException();
	}  
			
	public Schema newSchema(File file) throws SAXException {
		if(file == null) throw new NullPointerException();
        return newSchema(new StreamSource(file));
    }
	
	public Schema newSchema(URL url) throws SAXException{
		if(url == null) throw new NullPointerException();
		try{
		    return newSchema(new StreamSource(url.openStream()));
		}catch(IOException e){
		    throw new SAXException(e);
		}
    }
	
	public Schema newSchema(Source source) throws SAXException{
		if(source == null)throw new NullPointerException();
		
		errorDispatcher.init();		
		createParser();
		qlbSupported = true;
		
		try{
            schemaDocumentTransformer.transform(source, resolvedIncludesResult);
        }catch(TransformerException te){
            if(qlbSupported)throw new SAXException(te);
        }catch(IllegalStateException ise){
            if(qlbSupported)throw ise;
        }
		
		/*TransformerException firstException;
		
        switch(qlbProperty){
            case QLB_XSLT1:
                resolvedAbstractPatternsResult.setHandler(schematronCompilerXSLT1);
                break;
            case QLB_XSLT2:
                resolvedAbstractPatternsResult.setHandler(schematronCompilerXSLT2);
                break;
            default: throw new SAXException("Uknown query language binding");
        }
        
        try{
            schemaDocumentTransformer.transform(source, resolvedIncludesResult);
        }catch(TransformerException te){
            firstException = te;
            
            createParser();
            switch(qlbProperty){
                case QLB_XSLT2:
                    resolvedAbstractPatternsResult.setHandler(schematronCompilerXSLT1);
                    break;
                case QLB_XSLT1:
                    resolvedAbstractPatternsResult.setHandler(schematronCompilerXSLT2);
                    break;
                default: throw new SAXException("Uknown query language binding");
            }
            
            try{                
                schemaDocumentTransformer.transform(source, resolvedIncludesResult);
            }catch(TransformerException t){
                throw new SAXException(firstException);
            }
        }*/
		return new SchematronSchema(secureProcessing,
                    namespacePrefixes,
                    restrictToFileName,
                    optimizedForResourceSharing,
                    schemaTemplatesHandler.getTemplates());
	}
	
	public void setQLB(String qlb, Locator locator) throws SAXException{
	    if(qlb == null || qlb.equals(SCHEMA_QLB_XSLT1)){
            resolvedAbstractPatternsResult.setHandler(schematronCompilerXSLT1);
        }else if(qlb.equals(SCHEMA_QLB_XSLT2)){
            resolvedAbstractPatternsResult.setHandler(schematronCompilerXSLT2);
        }else{
            errorDispatcher.error(new SAXParseException("Unsupported Schematron query language. Serene supports \"xslt\" and \"xslt2\".", locator));
            qlbSupported  = false;           
        }
	}
    //------------------------------------------------------------------------------------------
	//END methods of the javax.xml.validation.SchemaFactory
	//------------------------------------------------------------------------------------------
	
	public String toString(){
		return "SchematronSchemaFactory";
	}
}
