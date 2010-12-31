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

package serene;

import java.io.File;
import java.io.IOException;

import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;

import javax.xml.XMLConstants;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.xml.sax.helpers.XMLReaderFactory;

import serene.validation.jaxp.RNGSchemaFactory;

import sereneWrite.WriteErrorHandler;
import sereneWrite.MessageWriter;
import sereneWrite.WriteHandler;
import sereneWrite.ConsoleHandler;

public class Driver{	
	
	// TODO 
	// Add options 
	// -d if you want to use the debuger; default debuger output is console 
	// -f if you want to use the file output facility for the debuger
	public static void main(String args[]){
		if(args == null || args.length == 0){
			System.out.println("Usage: java serene.Driver schema-file xml-file ... ");
			return;
		}
		if(!args[0].endsWith(".rng")){
			System.out.println("Usage: java serene.Driver schema-file xml-file ... ");
			return;
		}
		for(int i = 1; i < args.length; i++){
			if(!args[i].endsWith(".xml")){
				System.out.println("Usage: java serene.Driver schema-file xml-file ... ");
				return;
			}
		}
		
		XMLReader xmlReader = null;
		
		SchemaFactory schemaFactory;
		Schema schema;
		
		MessageWriter debugWriter = new MessageWriter();;
		WriteErrorHandler debugErrorHandler = new WriteErrorHandler();
        try{
            debugErrorHandler.setFeature("http://example.com/countMissingLibraryExceptions", false);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }		
				
		WriteHandler wh = new ConsoleHandler();
		debugWriter.setWriteHandler(wh);
		debugErrorHandler.setWriteHandler(wh);
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
			xmlReader.setErrorHandler(debugErrorHandler);
		}catch(SAXException e){
			e.printStackTrace();
		}		
				
		schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
				
		schemaFactory.setErrorHandler(debugErrorHandler);
        debugErrorHandler.init();
		try{	
			schema = schemaFactory.newSchema(new File(args[0]));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		if(debugErrorHandler.hasError())return;		
		ValidatorHandler vh = schema.newValidatorHandler();
		vh.setErrorHandler(debugErrorHandler);
		
		xmlReader.setContentHandler(vh);

		for(int i = 1; i < args.length; i++){
			try{					
				xmlReader.parse(args[i]);			
			}catch(IOException e){
				e.printStackTrace();
			}catch(SAXParseException e){
				e.printStackTrace();
			}catch(SAXException e){
				e.printStackTrace();
			}
		}		
	}	
}