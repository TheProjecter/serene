package sereneSamples;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;



import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.w3c.dom.Document;

import sereneWrite.WriteErrorHandler;
import sereneWrite.MessageWriter;
import sereneWrite.WriteHandler;
import sereneWrite.ConsoleHandler;

public class MissingDatatypeLibraryValidation{	
	
	public static void main(String args[]){
		if(args == null || args.length == 0){
			System.out.println("Usage: java sereneSamples.MissingDatatypeLibraryValidation schema-file xml-file ... ");
			return;
		}
		if(!args[0].endsWith(".rng")){
			System.out.println("Usage: java sereneSamples.MissingDatatypeLibraryValidation schema-file xml-file ... ");
			return;
		}
		for(int i = 1; i < args.length; i++){
			if(!args[i].endsWith(".xml")){
				System.out.println("Usage: java sereneSamples.MissingDatatypeLibraryValidation schema-file xml-file ... ");
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
				
		debugErrorHandler.print("SCHEMA "+args[0]);
			
		schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        schemaFactory.setErrorHandler(debugErrorHandler);
        
        debugErrorHandler.print("FEATURE http://serenerng.com/features/schemaFactory/replaceMissingDatatypeLibrary  true");
        try{
            schemaFactory.setFeature("http://serenerng.com/features/schemaFactory/replaceMissingDatatypeLibrary", true);            
            debugErrorHandler.setFeature("http://example.com/countMissingLibraryExceptions", false);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
        }
        try{	
			schema = schemaFactory.newSchema(new File(args[0]));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		if(!debugErrorHandler.hasError()){				
            Validator v = schema.newValidator();
            v.setErrorHandler(debugErrorHandler);
                            
            for(int i = 1; i < args.length; i++){
                String name = args[i].substring(args[i].lastIndexOf(File.separator)+1);			
                debugErrorHandler.print("FILE "+ name);						
                try{				
                    Source source = new SAXSource(new InputSource(args[i]));
                    v.validate(source);				
                }catch(IOException e){
                    e.printStackTrace();
                }catch(SAXParseException e){
                    e.printStackTrace();
                }catch(SAXException e){
                    e.printStackTrace();
                }
            }
        }else{
            debugErrorHandler.print("No document validation, schema contains unrecoverable errors.");
        }
        
        
        debugErrorHandler.print("FEATURE http://serenerng.com/features/schemaFactory/replaceMissingDatatypeLibrary  false");
        try{
            schemaFactory.setFeature("http://serenerng.com/features/schemaFactory/replaceMissingDatatypeLibrary", false);
            debugErrorHandler.setFeature("http://example.com/countMissingLibraryExceptions", true);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
        }
        try{	
			schema = schemaFactory.newSchema(new File(args[0]));
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		if(!debugErrorHandler.hasError()){				
            Validator v = schema.newValidator();
            v.setErrorHandler(debugErrorHandler);
                            
            for(int i = 1; i < args.length; i++){
                String name = args[i].substring(args[i].lastIndexOf(File.separator)+1);			
                debugErrorHandler.print("FILE "+ name);						
                try{				
                    Source source = new SAXSource(new InputSource(args[i]));
                    v.validate(source);				
                }catch(IOException e){
                    e.printStackTrace();
                }catch(SAXParseException e){
                    e.printStackTrace();
                }catch(SAXException e){
                    e.printStackTrace();
                }
            }
        }else{
            debugErrorHandler.print("No document validation, schema contains unrecoverable errors.");
        }
        
		debugErrorHandler.close();		
	}	
}
