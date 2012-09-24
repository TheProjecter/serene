package sereneSamples;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import javax.xml.XMLConstants;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.ContentHandler;
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

public class InfosetModificationValidation{	
	
	public static void main(String args[]){
		if(args == null || args.length == 0){
			System.out.println("Usage: java sereneSamples.InfosetModificationValidation schema-file xml-file ... ");
			return;
		}
		if(!args[0].endsWith(".rng")){
			System.out.println("Usage: java sereneSamples.InfosetModificationValidation schema-file xml-file ... ");
			return;
		}
		for(int i = 1; i < args.length; i++){
			if(!args[i].endsWith(".xml")){
				System.out.println("Usage: java sereneSamples.InfosetModificationValidation schema-file xml-file ... ");
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
            schemaFactory.setFeature("http://serenerng.com/features/DTDCompatibility/level2/attributeDefaultValue", true);
            schemaFactory.setFeature("http://serenerng.com/features/DTDCompatibility/level2/attributeIdType", true);
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
		if(debugErrorHandler.hasError())return;		
				
		Validator v = schema.newValidator();
		v.setErrorHandler(debugErrorHandler);

        NodeWriter nodeWriter = new NodeWriter();
        
		for(int i = 1; i < args.length; i++){
			String name = args[i].substring(args[i].lastIndexOf(File.separator)+1);
			
			debugErrorHandler.print("FILE "+ name);
						
			try{				
				//SAXSource
                ContentHandler ch = new WritingContentHandler();
                Result result = new SAXResult(ch);
				debugErrorHandler.print("as SAXSource");				
				Source source = new SAXSource(new InputSource(args[i]));
				v.validate(source, result);
								
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
                String systemId = new File(args[i]).getAbsolutePath();
                debugErrorHandler.print("AUGMENTING");
				source = new DOMSource(doc, systemId);
                result = new DOMResult(doc, systemId);
				v.validate(source, result);
                nodeWriter.write(((DOMResult)result).getNode());
                
                debugErrorHandler.print("BUILDING");
				source = new DOMSource(doc, systemId);
                result = new DOMResult();
				v.validate(source, result);
                nodeWriter.write(((DOMResult)result).getNode());
                
				//StreamSource
				debugErrorHandler.print("as StreamSource");				
				source = new StreamSource(new File(args[i]));
                result = new StreamResult(System.out);
				v.validate(source, result);
				
				//StAXSource
				debugErrorHandler.print("as StAXSource");
                XMLInputFactory xif = XMLInputFactory.newInstance();
                XMLOutputFactory xof = XMLOutputFactory.newInstance();                
                debugErrorHandler.print("source: XMLEventReader   result:XMLEventWriter");
                InputStream is = new FileInputStream(args[i]);
                try{
                    XMLEventReader xer = xif.createXMLEventReader(is); 
                    source = new StAXSource(xer);
                                        
                    XMLEventWriter xew = xof.createXMLEventWriter(System.out, "UTF-8");
                    result = new StAXResult(xew);
                }catch(XMLStreamException e){
                    e.printStackTrace();
                }
				v.validate(source, result);                
                
                debugErrorHandler.print("source: XMLEventReader   result:XMLStreamWriter");
                is = new FileInputStream(args[i]);
                try{
                    XMLEventReader xer = xif.createXMLEventReader(is); 
                    source = new StAXSource(xer);                    
                    
                    XMLStreamWriter xsw = xof.createXMLStreamWriter(System.out, "UTF-8");
                    result = new StAXResult(xsw);
                }catch(XMLStreamException e){
                    e.printStackTrace();
                }
				v.validate(source, result);
                
                debugErrorHandler.print("source: XMLStreamReader   result:XMLEventWriter");
                is = new FileInputStream(args[i]);
                try{
                    XMLStreamReader xsr = xif.createXMLStreamReader(is); 
                    source = new StAXSource(xsr);
                                        
                    XMLEventWriter xew = xof.createXMLEventWriter(System.out, "UTF-8");
                    result = new StAXResult(xew);
                }catch(XMLStreamException e){
                    e.printStackTrace();
                }
				v.validate(source, result);
                
                debugErrorHandler.print("source: XMLStreamReader   result:XMLStreamWriter");
                is = new FileInputStream(args[i]);
                try{
                    XMLStreamReader xsr = xif.createXMLStreamReader(is); 
                    source = new StAXSource(xsr);
                    
                    XMLStreamWriter xsw = xof.createXMLStreamWriter(System.out, "UTF-8");
                    result = new StAXResult(xsw);
                }catch(XMLStreamException e){
                    e.printStackTrace();
                }
				v.validate(source, result);
                
                debugErrorHandler.print("source: XMLStreamReader   result:null");
                is = new FileInputStream(args[i]);
                try{
                    XMLStreamReader xsr = xif.createXMLStreamReader(is); 
                    source = new StAXSource(xsr);
                    
                    XMLStreamWriter xsw = xof.createXMLStreamWriter(System.out, "UTF-8");
                    result = new StAXResult(xsw);
                }catch(XMLStreamException e){
                    e.printStackTrace();
                }
				v.validate(source, result);
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
