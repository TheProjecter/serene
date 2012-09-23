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

import java.util.List;
import java.util.ArrayList;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.xml.sax.helpers.AttributesImpl;

import javax.xml.XMLConstants;

import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stax.StAXSource;

import javax.xml.transform.stream.StreamSource;


import javax.xml.transform.Source;
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

import javax.xml.validation.ValidatorHandler;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.Constants;
import serene.DocumentContext;

public class SchematronParser implements ContentHandler{
    String SCHEMA_QLB_XSLT1 = "xslt";
    String SCHEMA_QLB_XSLT2 = "xslt2";
    
    static final int TRANSFORMER_ONE = 1;
    static final int TRANSFORMER_TWO = 2;
    static final int NO_TRANSFORMER = 0;
    
    static final int QLB_NOT_SET = 0;
    static final int QLB_XSLT1 = 1;
    static final int QLB_XSLT2 = 2;
    static final int QLB_UNSUPPORTED = 3;
    int openSchemaQLB;
    //int schemaElementQLB;
    
	static final String QUERY_LANGUAGE = "queryLanguage";
    static final String SCHEMA_LOCAL_NAME = "schema";
    static final String PATTERN_LOCAL_NAME = "pattern";
    static final String RULE_LOCAL_NAME = "rule";
    static final String DIAGNOSTICS_LOCAL_NAME = "diagnostics";
    static final String TITLE_LOCAL_NAME = "title";
    static final String NS_LOCAL_NAME = "ns";
    static final String LET_LOCAL_NAME = "let";    
    static final String CDATA_ATTRIBUTE_TYPE = "CDATA";
    static final char[] CONTAINER_PATTERN_ADDED_BY_SERENE = {'C', 'o', 'n', 't', 'a', 'i', 'n', 'e', 'r', ' ', 'p', 'a', 't', 't', 'e', 'r', 'n', ' ', 'a', 'd', 'd', 'e', 'd', ' ', 'b', 'y', ' ', 'S', 'e', 'r', 'e', 'n', 'e', '.'};
    static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
   
    int depth;
    boolean openedSchematronSchema;
    boolean openedSchematronPattern;
    
    
    //boolean isInUse1;
	TransformerHandler schematronStartTransformerHandler1;//handles includes	
    SAXResult expandedSchematronResult1; // after includes and abstract patterns were handled
    TransformerHandler schematronCompilerXSLT11;
    TransformerHandler schematronCompilerXSLT21;
    TemplatesHandler schematronTemplatesHandler1;
    
    //boolean isInUse2;
    TransformerHandler schematronStartTransformerHandler2;//handles includes	
    SAXResult expandedSchematronResult2; // after includes and abstract patterns were handled
    TransformerHandler schematronCompilerXSLT12;
    TransformerHandler schematronCompilerXSLT22;
    TemplatesHandler schematronTemplatesHandler2;
    
    
    TransformerHandler schematronStartTransformerHandler;//handles includes	
    SAXResult expandedSchematronResult; // after includes and abstract patterns were handled
    TransformerHandler schematronCompilerXSLT1;
    TransformerHandler schematronCompilerXSLT2;
    TemplatesHandler schematronTemplatesHandler;
    int activeTransformer;
    int openSchemaTransformer;// actually is always ONE
    
    
    List<Templates> schematronTemplates;    
    ErrorDispatcher errorDispatcher;    
    Locator locator;    
    DocumentContext documentContext;
    ValidatorHandler validatorHandler;
    
    
    SAXTransformerFactory stf;
    
    SchematronParser(ErrorDispatcher errorDispatcher){
        this.errorDispatcher = errorDispatcher;
        
        openedSchematronSchema = false;
        openedSchematronPattern = false;
        activeTransformer = NO_TRANSFORMER;
        openSchemaTransformer = NO_TRANSFORMER;
        
        openSchemaQLB = QLB_NOT_SET;
    }
    
    public void setDocumentContext(DocumentContext documentContext){
        this.documentContext = documentContext;
    }
    public void setValidatorHandler(ValidatorHandler validatorHandler){
        this.validatorHandler = validatorHandler;
    }
    
    public void setQLB(String value) throws SAXException{
        if(openSchemaQLB != QLB_NOT_SET)return;
        if(value == null || value.equals(SCHEMA_QLB_XSLT1)){
            openSchemaQLB = QLB_XSLT1;
        }else if(value.equals(SCHEMA_QLB_XSLT2)){
            openSchemaQLB = QLB_XSLT2;
        }else{
            openSchemaQLB = QLB_UNSUPPORTED;
            validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, false);
            errorDispatcher.error(new SAXParseException("Unsupported Schematron query language. Serene supports \"xslt\" and \"xslt2\".", locator));
            return;
        }
        validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, true);
    }
    
    public List<Templates> getSchematronTemplates() throws SAXException{
        if(openedSchematronPattern){
            if(openSchemaTransformer != activeTransformer)changeActiveTransformer();
            closeSchematronPattern();
            closeSchematronSchema();
            endSchematronSchema();
        }else if(openedSchematronSchema){
            if(openSchemaTransformer != activeTransformer)changeActiveTransformer();            
            closeSchematronSchema();
            endSchematronSchema();
        }
        return schematronTemplates;
    }
    
    public void clear(){
        schematronTemplates = null;
        
        openedSchematronSchema = false;
        openedSchematronPattern = false;
        
        activeTransformer = NO_TRANSFORMER;
        openSchemaTransformer = NO_TRANSFORMER;
        
        schematronStartTransformerHandler = null;	
        expandedSchematronResult = null;
        schematronCompilerXSLT1 = null;
        schematronCompilerXSLT2 = null;
        schematronTemplatesHandler = null;
        
        openSchemaQLB = QLB_NOT_SET;
    }
    
	    
    public void processingInstruction(String target, String data) throws SAXException{}
	public void skippedEntity(String name) throws SAXException{}
	public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException{}
    
	public void startDocument()  throws SAXException{	    
	    if(schematronTemplates == null) schematronTemplates = new ArrayList<Templates>();
	    	    
	    if(openedSchematronSchema){
	        documentContext.transferDTDMappings((DTDHandler)schematronStartTransformerHandler);
	        documentContext.transferStartPrefixMappings(schematronStartTransformerHandler);
	    }
	}			
	public void startPrefixMapping(String prefix, String uri)  throws SAXException{}	
	public void endPrefixMapping(String prefix)  throws SAXException{}	
	public void setDocumentLocator(Locator locator){
	    this.locator = locator;
	}
	
	public void characters(char[] chars, int start, int length)throws SAXException{
	    schematronStartTransformerHandler.characters(chars, start, length);
	}
	
	
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{
	    if(depth == 0){
	        if(activeTransformer == NO_TRANSFORMER) setActiveTransformer();
            if(localName.equals(SCHEMA_LOCAL_NAME)){
                if(openedSchematronSchema){
                    changeActiveTransformer();    
                }                              
                
                startSchematronSchema(attributes.getValue(XMLConstants.NULL_NS_URI, QUERY_LANGUAGE));
                schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
            }else if(localName.equals(PATTERN_LOCAL_NAME)){
                if(openedSchematronSchema && openSchemaTransformer != activeTransformer)changeActiveTransformer();
                
                if(openedSchematronPattern){
                    closeSchematronPattern();
                }else if(!openedSchematronSchema){
                    startSchematronSchema();
                    openSchematronSchema();
                }                                
                schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);                
            }else if(localName.equals(NS_LOCAL_NAME) || localName.equals(LET_LOCAL_NAME)){
                System.out.println("SCHEMATRON PARSER   START ELEMENT localName="+localName);
                if(openedSchematronSchema && openSchemaTransformer != activeTransformer)changeActiveTransformer();
                
                if(!openedSchematronSchema){
                    startSchematronSchema();
                    openSchematronSchema();
                }                                
                schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);                
            }else if(localName.equals(RULE_LOCAL_NAME)){
                if(openedSchematronSchema && openSchemaTransformer != activeTransformer)changeActiveTransformer();
                
                
                if(!openedSchematronSchema){
                    startSchematronSchema();
                    openSchematronSchema();
                    openSchematronPattern();
                }else if(!openedSchematronPattern){
                    openSchematronPattern();
                }
                schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
            }else if(localName.equals(DIAGNOSTICS_LOCAL_NAME)){
                if(openedSchematronSchema && openSchemaTransformer != activeTransformer)changeActiveTransformer();
                
                
                if(openedSchematronPattern)closeSchematronPattern();
                schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
            }else{
                errorDispatcher.error(new SAXParseException("Unsupported Schematron element. Embeding of Schematron element <"+localName+"> here is not suported by Serene. Use <schema>, <pattern>, <rule>, or <diagnostics>.", locator));
            }
        }else{
            schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
        }	
        depth++;						
	}		
	
	public void endElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{
		depth--;	    
        schematronStartTransformerHandler.endElement(namespaceURI, localName, qName);
        if(depth == 0 && (!openedSchematronSchema || localName.equals(SCHEMA_LOCAL_NAME)))endSchematronSchema();
	}
	
	
	public void endDocument()  throws SAXException {
	    if(openedSchematronSchema){
	        if(activeTransformer != openSchemaTransformer) changeActiveTransformer();
	        documentContext.transferEndPrefixMappings(schematronStartTransformerHandler);
	    }
	}
	
	
	
	void startSchematronSchema(String qlb) throws SAXException{
	    // called for the schema element	    
	    if(qlb == null || qlb.equals(SCHEMA_QLB_XSLT1)){
	        //schemaElementQLB = QLB_XSLT1;
            expandedSchematronResult.setHandler(schematronCompilerXSLT1);
        }else if(qlb.equals(SCHEMA_QLB_XSLT2)){
            //schemaElementQLB = QLB_XSLT2;
            expandedSchematronResult.setHandler(schematronCompilerXSLT2);
        }else{
            //schemaElementQLB = QLB_UNSUPPORTED;
            validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, false);
            errorDispatcher.error(new SAXParseException("Unsupported Schematron query language. Serene supports \"xslt\" and \"xslt2\".", locator));
            return;
        }
	    
        validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, true);
        
	    schematronStartTransformerHandler.setDocumentLocator(locator);
	    schematronStartTransformerHandler.startDocument();
	    documentContext.transferDTDMappings((DTDHandler)schematronStartTransformerHandler);
	    documentContext.transferStartPrefixMappings(schematronStartTransformerHandler);
	}
	void startSchematronSchema() throws SAXException{
	    System.out.println("SCHEMATRON PARSER   START SCHEMA");
	    // called for the open schema
	    openSchemaTransformer = activeTransformer;
	    
	    if(openSchemaQLB == QLB_XSLT1){    
            expandedSchematronResult.setHandler(schematronCompilerXSLT1);
        }else if(openSchemaQLB == QLB_XSLT2){
            expandedSchematronResult.setHandler(schematronCompilerXSLT2);
        }else if(openSchemaQLB == QLB_NOT_SET){
            throw new IllegalStateException();
        }
        
	    schematronStartTransformerHandler.setDocumentLocator(locator);
	    schematronStartTransformerHandler.startDocument();
	    documentContext.transferDTDMappings((DTDHandler)schematronStartTransformerHandler);
	    documentContext.transferStartPrefixMappings(schematronStartTransformerHandler);
	}
	void openSchematronSchema() throws SAXException{        
        openedSchematronSchema = true;  
        openSchemaTransformer = activeTransformer;
        
        schematronStartTransformerHandler.startElement(Constants.SCHEMATRON_NS_URI, SCHEMA_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, SCHEMA_LOCAL_NAME), EMPTY_ATTRIBUTES);
	}
	void closeSchematronSchema() throws SAXException{
	    openedSchematronSchema = false;   
	    schematronStartTransformerHandler.endElement(Constants.SCHEMATRON_NS_URI, SCHEMA_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, SCHEMA_LOCAL_NAME));
	}
	void openSchematronPattern()  throws SAXException{
        openedSchematronPattern = true;   
        
        schematronStartTransformerHandler.startElement(Constants.SCHEMATRON_NS_URI, PATTERN_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, PATTERN_LOCAL_NAME), EMPTY_ATTRIBUTES);
        
        schematronStartTransformerHandler.startElement(Constants.SCHEMATRON_NS_URI, TITLE_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, TITLE_LOCAL_NAME), EMPTY_ATTRIBUTES);        
        schematronStartTransformerHandler.characters(CONTAINER_PATTERN_ADDED_BY_SERENE, 0, CONTAINER_PATTERN_ADDED_BY_SERENE.length);
        schematronStartTransformerHandler.endElement(Constants.SCHEMATRON_NS_URI, TITLE_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, TITLE_LOCAL_NAME));
        
	}	
	void closeSchematronPattern() throws SAXException{
	    openedSchematronPattern = false;
	    schematronStartTransformerHandler.endElement(Constants.SCHEMATRON_NS_URI, PATTERN_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, PATTERN_LOCAL_NAME));
	}
	
	String getQName(String nsURI, String localName){
	    String prefix = documentContext.getPrefix(nsURI);
	    if(prefix != null && !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
	        return prefix+":"+localName;
	    return localName;
	}
	
	void endSchematronSchema() throws SAXException{	    
	    schematronStartTransformerHandler.endDocument();
	    
	    Templates currentTemplates = schematronTemplatesHandler.getTemplates();
	    if(currentTemplates != null) schematronTemplates.add(currentTemplates);
	}

    void setActiveTransformer() throws SAXException{
        if(schematronStartTransformerHandler1 == null) createFirstTransformer();
        
        
        schematronStartTransformerHandler = schematronStartTransformerHandler1;	
        expandedSchematronResult = expandedSchematronResult1; // after includes and abstract patterns were handled
        schematronCompilerXSLT1 = schematronCompilerXSLT11;
        schematronCompilerXSLT2 = schematronCompilerXSLT21;
        schematronTemplatesHandler = schematronTemplatesHandler1;
        
        activeTransformer = TRANSFORMER_ONE;     
        
    }	
    
    void changeActiveTransformer() throws SAXException{
        switch (activeTransformer){
            case NO_TRANSFORMER:
                throw new IllegalStateException();
            case TRANSFORMER_ONE:
                if(schematronStartTransformerHandler2 == null) createSecondTransformer();
                
                schematronStartTransformerHandler = schematronStartTransformerHandler2;	
                expandedSchematronResult = expandedSchematronResult2; // after includes and abstract patterns were handled
                schematronCompilerXSLT1 = schematronCompilerXSLT12;
                schematronCompilerXSLT2 = schematronCompilerXSLT22;
                schematronTemplatesHandler = schematronTemplatesHandler2;
                
                activeTransformer = TRANSFORMER_TWO;
                if(activeTransformer == openSchemaTransformer){
                    if(openSchemaQLB == QLB_XSLT1){                        
                        validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, true);
                        expandedSchematronResult.setHandler(schematronCompilerXSLT1);
                    }else if(openSchemaQLB == QLB_XSLT2){                        
                        validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, true);
                        expandedSchematronResult.setHandler(schematronCompilerXSLT2);
                    }else if(openSchemaQLB == QLB_UNSUPPORTED){
                        validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, false);
                    }else{
                        throw new IllegalStateException(); 
                    }
                }// else it will be set from startSchematronSchema
                break;
            case TRANSFORMER_TWO:
                if(schematronStartTransformerHandler1 == null) createFirstTransformer();
                
                schematronStartTransformerHandler = schematronStartTransformerHandler1;	
                expandedSchematronResult = expandedSchematronResult1; // after includes and abstract patterns were handled
                schematronCompilerXSLT1 = schematronCompilerXSLT11;
                schematronCompilerXSLT2 = schematronCompilerXSLT21;
                schematronTemplatesHandler = schematronTemplatesHandler1;
                
                activeTransformer = TRANSFORMER_ONE;
                if(activeTransformer == openSchemaTransformer){
                    if(openSchemaQLB == QLB_XSLT1){                        
                        validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, true);
                        expandedSchematronResult.setHandler(schematronCompilerXSLT1);
                    }else if(openSchemaQLB == QLB_XSLT2){                        
                        validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, true);
                        expandedSchematronResult.setHandler(schematronCompilerXSLT2);
                    }else if(openSchemaQLB == QLB_UNSUPPORTED){
                        validatorHandler.setFeature(Constants.IS_QLB_SUPPORTED, false);
                    }else{
                        throw new IllegalStateException(); 
                    }
                }// else it will be set from startSchematronSchema
                break;
            default:
                throw new IllegalStateException();
        }
    }
    
    
    void createFirstTransformer() throws SAXException{        
        SAXTransformerFactory stf = null;
	    TransformerFactory tf = TransformerFactory.newInstance();
        if(tf.getFeature(SAXTransformerFactory.FEATURE)){
            stf = (SAXTransformerFactory)tf;
        }else{
            throw new SAXException("Could not create schema transformers.");
        }
        
        try{          
            schematronTemplatesHandler1 = stf.newTemplatesHandler(); // here the Templates object representing the compiled schema can be obtained
            
            schematronCompilerXSLT21 = stf.newTransformerHandler(new StreamSource(new File("isoSchematronImpl/iso_svrl_for_xslt2.xsl")));
            schematronCompilerXSLT21.setResult(new SAXResult(schematronTemplatesHandler1));
            schematronCompilerXSLT21.getTransformer().setErrorListener(errorDispatcher);
            
            schematronCompilerXSLT11 = stf.newTransformerHandler(new StreamSource(new File("isoSchematronImpl/iso_svrl_for_xslt1.xsl")));
            schematronCompilerXSLT11.setResult(new SAXResult(schematronTemplatesHandler1));
            schematronCompilerXSLT11.getTransformer().setErrorListener(errorDispatcher);
            
            TransformerHandler abstarctPatternsHandler1 = stf.newTransformerHandler(new StreamSource(new File("isoSchematronImpl/iso_abstract_expand.xsl")));
            expandedSchematronResult1 = new SAXResult(); // content handler will be set according to qlbProperty and maybe adjusted
            abstarctPatternsHandler1.setResult(expandedSchematronResult1);
            abstarctPatternsHandler1.getTransformer().setErrorListener(errorDispatcher);
            
            schematronStartTransformerHandler1 = stf.newTransformerHandler(new StreamSource(new File("isoSchematronImpl/iso_dsdl_include.xsl")));
            SAXResult resolvedIncludesResult1 = new SAXResult(abstarctPatternsHandler1); // result for the above transformation
            schematronStartTransformerHandler1.setResult(resolvedIncludesResult1);
            schematronStartTransformerHandler1.getTransformer().setErrorListener(errorDispatcher);
        }catch(TransformerConfigurationException tce){
            throw new SAXException(tce);
        }       
    }    
    
    
    void createSecondTransformer() throws SAXException{
        SAXTransformerFactory stf = null;
	    TransformerFactory tf = TransformerFactory.newInstance();
        if(tf.getFeature(SAXTransformerFactory.FEATURE)){
            stf = (SAXTransformerFactory)tf;
        }else{
            throw new SAXException("Could not create schema transformers.");
        }
        
        try{          
            schematronTemplatesHandler2 = stf.newTemplatesHandler(); // here the Templates object representing the compiled schema can be obtained
            
            schematronCompilerXSLT22 = stf.newTransformerHandler(new StreamSource(new File("isoSchematronImpl/iso_svrl_for_xslt2.xsl")));
            schematronCompilerXSLT22.setResult(new SAXResult(schematronTemplatesHandler2));
            schematronCompilerXSLT22.getTransformer().setErrorListener(errorDispatcher);
            
            schematronCompilerXSLT12 = stf.newTransformerHandler(new StreamSource(new File("isoSchematronImpl/iso_svrl_for_xslt1.xsl")));
            schematronCompilerXSLT12.setResult(new SAXResult(schematronTemplatesHandler2));
            schematronCompilerXSLT12.getTransformer().setErrorListener(errorDispatcher);
            
            TransformerHandler abstarctPatternsHandler2 = stf.newTransformerHandler(new StreamSource(new File("isoSchematronImpl/iso_abstract_expand.xsl")));
            expandedSchematronResult2 = new SAXResult(); // content handler will be set according to qlbProperty and maybe adjusted
            abstarctPatternsHandler2.setResult(expandedSchematronResult2);
            abstarctPatternsHandler2.getTransformer().setErrorListener(errorDispatcher);
            
            schematronStartTransformerHandler2 = stf.newTransformerHandler(new StreamSource(new File("isoSchematronImpl/iso_dsdl_include.xsl")));
            SAXResult resolvedIncludesResult2 = new SAXResult(abstarctPatternsHandler2); // result for the above transformation
            schematronStartTransformerHandler2.setResult(resolvedIncludesResult2);
            schematronStartTransformerHandler2.getTransformer().setErrorListener(errorDispatcher);
        }catch(TransformerConfigurationException tce){
            throw new SAXException(tce);
        }           
    }   
}
