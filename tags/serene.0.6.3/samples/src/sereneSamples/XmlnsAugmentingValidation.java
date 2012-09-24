package sereneSamples;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

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
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import org.xml.sax.helpers.XMLReaderFactory;


import org.w3c.dom.Document;

import serene.validation.jaxp.RNGSchemaFactory;

import sereneWrite.WriteErrorHandler;
import sereneWrite.WriteHandler;
import sereneWrite.ConsoleHandler;

public class XmlnsAugmentingValidation{	
	
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
		
		SchemaFactory schemaFactory;
		Schema schema;
		
		WriteErrorHandler debugErrorHandler;		
				
		debugErrorHandler = new WriteErrorHandler();
		debugErrorHandler.setWriteHandler(new ConsoleHandler());
		try{
            debugErrorHandler.setFeature("http://example.com/countMissingLibraryExceptions", false);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }	
		
		debugErrorHandler.print("*SCHEMA "+args[0]);
				
		//schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try{
		    schemaFactory = new RNGSchemaFactory();
		}catch(SAXException e){
		    e.printStackTrace();
			return;
		}
		
		try{	
			schema = schemaFactory.newSchema(new File(args[0]));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		if(debugErrorHandler.hasError())return;		
				
		ValidatorHandler vh = schema.newValidatorHandler();
		vh.setErrorHandler(debugErrorHandler);
		
		ContentHandler ch = new WritingContentHandler();
		vh.setContentHandler(ch);	
		
		XMLReader xmlReader = null;
		try{
			xmlReader = XMLReaderFactory.createXMLReader();
		}catch(SAXException e){
			e.printStackTrace();
		}
		
		try{
			vh.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
			
			xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);			
			xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);		
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
		}catch(SAXNotRecognizedException e){
			e.printStackTrace();
		}catch(SAXNotSupportedException e){
			e.printStackTrace();
		}		
					
		xmlReader.setContentHandler(vh);
		
		for(int i = 1; i < args.length; i++){
			String name = args[i].substring(args[i].lastIndexOf(File.separator)+1);
			
			debugErrorHandler.print("*FILE "+ name);						
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
		debugErrorHandler.close();		
	}	
}
