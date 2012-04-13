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

import java.net.URI;

import java.io.IOException;

import javax.xml.validation.ValidatorHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


import serene.internal.InternalRNGFactory;
import serene.internal.InternalRNGSchema;

import serene.DTDMapping;

import serene.validation.schema.parsed.ParsedModel;

import serene.validation.schema.parsed.ParsedComponentBuilder;
import serene.validation.schema.parsed.Pattern;

import serene.validation.handlers.error.ErrorDispatcher;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.Constants;

import sereneWrite.MessageWriter;

class ExternalRefParser{    
	XMLReader xmlReader;
	
	ValidatorHandler validatorHandler;
	
	ErrorDispatcher errorDispatcher;
	
	MessageWriter debugWriter;
	
	ExternalRefParser(XMLReader xmlReader, 
	                    InternalRNGFactory internalRNGFactory, 
	                    ErrorDispatcher errorDispatcher, 
	                    MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.xmlReader = xmlReader;	
		
		InternalRNGSchema schema = internalRNGFactory.getExternalRefSchema();
		validatorHandler = schema.newValidatorHandler();// the parsedComponentBuilder has already been set to the bindingPool		
		validatorHandler.setErrorHandler(errorDispatcher);
	}
	
	ParsedModel parse(URI uri){	
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
		
		
		ParsedModel p = null;		
		try{
            p = (ParsedModel)validatorHandler.getProperty(Constants.PARSED_MODEL_PROPERTY);
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