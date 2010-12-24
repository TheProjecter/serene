package sereneSamples;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;

import org.w3c.dom.Document;

import sereneWrite.WriteErrorHandler;
import sereneWrite.MessageWriter;
import sereneWrite.WriteHandler;
import sereneWrite.ConsoleHandler;

public class SourceValidation{	
	
	public static void main(String args[]){
		if(args == null || args.length == 0){
			System.out.println("Usage: java sereneSamples.SourceValidation schema-file xml-file ... ");
			return;
		}
		if(!args[0].endsWith(".rng")){
			System.out.println("Usage: java sereneSamples.SourceValidation schema-file xml-file ... ");
			return;
		}
		for(int i = 1; i < args.length; i++){
			if(!args[i].endsWith(".xml")){
				System.out.println("Usage: java sereneSamples.SourceValidation schema-file xml-file ... ");
				return;
			}
		}
		
		SchemaFactory schemaFactory = null;
		Schema schema;
		
		MessageWriter debugWriter;
		WriteErrorHandler debugErrorHandler;		
					
		
		debugWriter = new MessageWriter();			
		debugWriter.setWriteHandler(new ConsoleHandler());
        
		
		debugErrorHandler = new WriteErrorHandler();
		debugErrorHandler.setWriteHandler(new ConsoleHandler());
		try{
            debugErrorHandler.setFeature("http://example.com/countMissingLibraryExceptions", false);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }	
		
		debugErrorHandler.print("SCHEMA "+args[0]);
			
		schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
	
		try{	
			schema = schemaFactory.newSchema(new File(args[0]));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		if(debugErrorHandler.hasError())return;		
				
		Validator v = schema.newValidator();
		v.setErrorHandler(debugErrorHandler);

		for(int i = 1; i < args.length; i++){
			String name = args[i].substring(args[i].lastIndexOf(File.separator)+1);
			
			debugErrorHandler.print("FILE "+ name);
						
			try{				
				//SAXSource
				debugErrorHandler.print("as SAXSource");				
				Source source = new SAXSource(new InputSource(args[i]));
				v.validate(source);
								
				//DOMSource
				debugErrorHandler.print("as DOMSource");				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = null;
				try{
					builder = factory.newDocumentBuilder();
				}catch(ParserConfigurationException e) {
					e.printStackTrace();
				}
				Document doc = builder.parse(new File(args[i]));
				source = new DOMSource(doc);
				source.setSystemId(new File(args[i]).getAbsolutePath());
				v.validate(source);
				
				//StreamSource
				debugErrorHandler.print("as StreamSource");				
				source = new StreamSource(new File(args[i]));
				v.validate(source);
				
				//StAXSource
				debugErrorHandler.print("as StAXSource");				
				source = new StreamSource(args[i]);
				v.validate(source);
			}catch(IOException e){
				e.printStackTrace();
			}catch(SAXParseException e){
				e.printStackTrace();
			}catch(SAXException e){
				e.printStackTrace();
			}
		}
		debugErrorHandler.close();		
	}	
}
