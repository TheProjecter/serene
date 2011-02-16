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

import serene.bind.ValidatorQueuePool;
import serene.bind.Queue;
import serene.bind.ElementTaskPool;
import serene.bind.ElementTask;
import serene.bind.BindingPool;
import serene.bind.BindingModel;

import serene.validation.DTDMapping;

import serene.validation.schema.parsed.ParsedComponentBuilder;
import serene.validation.schema.parsed.Grammar;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.Constants;

import sereneWrite.MessageWriter;
import sereneWrite.ParsedComponentWriter;

class IncludeParser{
	XMLReader xmlReader;		
	
	ValidatorHandler validatorHandler;		
	Queue queue;
	ParsedComponentBuilder parsedComponentBuilder;
	
	ErrorDispatcher errorDispatcher;
	
	MessageWriter debugWriter;
	ParsedComponentWriter pcw;
	
	IncludeParser(XMLReader xmlReader, InternalRNGFactory internalRNGFactory, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.xmlReader = xmlReader;
		this.errorDispatcher = errorDispatcher;	
		
		parsedComponentBuilder = new ParsedComponentBuilder(debugWriter);
		
		InternalRNGSchema schema = internalRNGFactory.getIncludeSchema();
		
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
		validatorHandler.setErrorHandler(errorDispatcher);
				
		pcw = new ParsedComponentWriter();
	}
	
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
		parsedComponentBuilder.startBuild();
		queue.executeAll();
		Grammar g = null;
		try{
            g = (Grammar)parsedComponentBuilder.getCurrentParsedComponent();
		}catch(ClassCastException cce){
			// TODO
			// the included document was not valid, 
			// you should have aborted earlier
			// see about that
            // SEEN , but needs review
            return null;
		}
        
        DTDMapping dtdMapping = null;
        
        try{
            dtdMapping = (DTDMapping)validatorHandler.getProperty(Constants.DTD_MAPPING_PROPERTY);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
        }
        
		return new IncludedParsedModel(dtdMapping, g, debugWriter); 
	}	
}