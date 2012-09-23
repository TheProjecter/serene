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

package serene.simplifier;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import java.net.URI;

import java.io.IOException;

import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.Schema;

/*import javax.xml.transform.Templates;

import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.TemplatesHandler;*/


import org.xml.sax.XMLReader;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import serene.internal.InternalRNGFactory;

import serene.DTDMapping;


import serene.validation.schema.parsed.ParsedComponentBuilder;
import serene.validation.schema.parsed.Grammar;
import serene.validation.schema.parsed.Definition;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.validation.jaxp.SchematronParser;

import serene.Constants;

class IncludeParser{
	boolean restrictToFileName;
    boolean processEmbededSchematron;
    
    /*TransformerHandler schematronStartTransformerHandler;
    SAXResult expandedSchematronResult;
    TransformerHandler schematronCompilerXSLT1;
    TransformerHandler schematronCompilerXSLT2;
    TemplatesHandler schematronTemplatesHandler;*/
    
    
	XMLReader xmlReader;	
	ValidatorHandler validatorHandler;
	
	SchematronParser schematronParser;
	
	ErrorDispatcher errorDispatcher;
	
	IncludeParser(boolean processEmbededSchematron, ErrorDispatcher errorDispatcher){
	    this.errorDispatcher = errorDispatcher;		
	    this.processEmbededSchematron = processEmbededSchematron;
	}
	
	void setRestrictToFileName(boolean restrictToFileName){
	    if(this.restrictToFileName != restrictToFileName){
            this.restrictToFileName = restrictToFileName;
            if(validatorHandler != null){
                try{
                    validatorHandler.setFeature(Constants.RESTRICT_TO_FILE_NAME_FEATURE, restrictToFileName);
                }catch (SAXNotRecognizedException e) {
                    e.printStackTrace();
                }catch (SAXNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
    }    
   
    void setParserComponents(XMLReader xmlReader, InternalRNGFactory internalRNGFactory, SchematronParser schematronParser){
	    this.xmlReader = xmlReader;
	    this.schematronParser = schematronParser;
		
	    Schema schema = internalRNGFactory.getIncludeSchema();
		validatorHandler = schema.newValidatorHandler();// the parsedComponentBuilder has already been set to the bindingPool		
		validatorHandler.setErrorHandler(errorDispatcher);
		
		try{		    
            validatorHandler.setFeature(Constants.RESTRICT_TO_FILE_NAME_FEATURE, restrictToFileName);
            validatorHandler.setFeature(Constants.PROCESS_EMBEDED_SCHEMATRON_FEATURE, processEmbededSchematron);
            if(processEmbededSchematron){
                validatorHandler.setProperty(Constants.SCHEMATRON_PARSER_PROPERTY, schematronParser);
                /*validatorHandler.setProperty(Constants.SCHEMATRON_COMPILER_FOR_XSLT1_PROPERTY, schematronCompilerXSLT1);
                validatorHandler.setProperty(Constants.SCHEMATRON_COMPILER_FOR_XSLT2_PROPERTY, schematronCompilerXSLT2);
                validatorHandler.setProperty(Constants.SCHEMATRON_EXPANDED_SCHEMA_RESULT_PROPERTY, expandedSchematronResult);
                validatorHandler.setProperty(Constants.SCHEMATRON_TEMPLATES_HANDLER_PROPERTY, schematronTemplatesHandler);
                validatorHandler.setProperty(Constants.SCHEMATRON_START_TRANSFORMER_HANDLER_PROPERTY, schematronStartTransformerHandler);*/
            }
        }catch (SAXNotRecognizedException e) {
            e.printStackTrace();
        }catch (SAXNotSupportedException e) {
            e.printStackTrace();
        }
	}
	
	void setProcessEmbededSchematron(boolean processEmbededSchematron){
	    if(this.processEmbededSchematron != processEmbededSchematron){
	        this.processEmbededSchematron = processEmbededSchematron;
	        if(validatorHandler != null){
                try{
                    validatorHandler.setFeature(Constants.PROCESS_EMBEDED_SCHEMATRON_FEATURE, processEmbededSchematron);
                }catch (SAXNotRecognizedException e) {
                    e.printStackTrace();
                }catch (SAXNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
	}	
    /*void setSchematronParserComponents(TransformerHandler schematronStartTransformerHandler,
	                                            SAXResult expandedSchematronResult,
	                                            TransformerHandler schematronCompilerXSLT1,
	                                            TransformerHandler schematronCompilerXSLT2,
	                                            TemplatesHandler schematronTemplatesHandler){
	    this.schematronStartTransformerHandler = schematronStartTransformerHandler;
	    this.expandedSchematronResult = expandedSchematronResult;
	    this.schematronCompilerXSLT1 = schematronCompilerXSLT1;
	    this.schematronCompilerXSLT2 = schematronCompilerXSLT2;
	    this.schematronTemplatesHandler = schematronTemplatesHandler;
	    if(validatorHandler != null){
            try{
                validatorHandler.setProperty(Constants.SCHEMATRON_COMPILER_FOR_XSLT1_PROPERTY, schematronCompilerXSLT1);
                validatorHandler.setProperty(Constants.SCHEMATRON_COMPILER_FOR_XSLT2_PROPERTY, schematronCompilerXSLT2);
                validatorHandler.setProperty(Constants.SCHEMATRON_EXPANDED_SCHEMA_RESULT_PROPERTY, expandedSchematronResult);
                validatorHandler.setProperty(Constants.SCHEMATRON_TEMPLATES_HANDLER_PROPERTY, schematronTemplatesHandler);
                validatorHandler.setProperty(Constants.SCHEMATRON_START_TRANSFORMER_HANDLER_PROPERTY, schematronStartTransformerHandler);
            }catch (SAXNotRecognizedException e) {
                e.printStackTrace();
            }catch (SAXNotSupportedException e) {
                e.printStackTrace();
            }
        }
	}*/
	
	IncludedParsedModel parse(URI uri){
		xmlReader.setContentHandler(validatorHandler);
		try{
            DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY);
            xmlReader.setDTDHandler(dtdHandler);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
        }
        
        
		try{					
			xmlReader.parse(uri.toString());			
		}catch(IOException e){
			e.printStackTrace();
		}catch(SAXParseException e){
			e.printStackTrace();
		}catch(SAXException e){
			e.printStackTrace();
		}	
		
		IncludedParsedModel p = null;		
		try{
            p = (IncludedParsedModel)validatorHandler.getProperty(Constants.INCLUDED_PARSED_MODEL_PROPERTY);
        }catch(ClassCastException c){
            // syntax error, already handled
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
        }
        
        return p;
	}

	IncludedParsedModel parse(URI uri, Map<String, ArrayList<Definition>> overrideDefinitions/*, List<Templates> schematronTemplates*/){
		xmlReader.setContentHandler(validatorHandler);
		try{
            DTDHandler dtdHandler = (DTDHandler)validatorHandler.getProperty(Constants.DTD_HANDLER_PROPERTY);
            xmlReader.setDTDHandler(dtdHandler);
            validatorHandler.setProperty(Constants.OVERRIDE_DEFINITIONS_PROPERTY, overrideDefinitions);
            /*validatorHandler.setProperty(Constants.SCHEMATRON_TEMPLATES_PROPERTY, schematronTemplates);*/
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
        }
        
        
		try{					
			xmlReader.parse(uri.toString());			
		}catch(IOException e){
			e.printStackTrace();
		}catch(SAXParseException e){
			e.printStackTrace();
		}catch(SAXException e){
			e.printStackTrace();
		}	
		
		IncludedParsedModel p = null;		
		try{
            p = (IncludedParsedModel)validatorHandler.getProperty(Constants.INCLUDED_PARSED_MODEL_PROPERTY);
        }catch(ClassCastException c){
            // syntax error, already handled
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
        }
        
        return p;
	}	
}