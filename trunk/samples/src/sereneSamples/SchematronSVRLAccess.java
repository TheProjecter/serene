
package sereneSamples;

import java.util.Stack;
import java.util.Arrays;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;
import javax.xml.XMLConstants;

import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.xml.sax.helpers.XMLReaderFactory;

//import serene.validation.jaxp.RNGSchemaFactory;


import sereneWrite.MessageWriter;
import sereneWrite.WriteHandler;
import sereneWrite.ConsoleHandler;
import sereneWrite.FileHandler;

import serene.Constants;

class SchematronSVRLAccess{	
	static SchemaFactory schemaFactory;
	
	static SchematronErrorHandler errorHandler;
		
	static Stack<String> sourceDirNames;

	static XMLReader xmlReader;
	    
	static void init(){
		errorHandler = new SchematronErrorHandler();
        
		sourceDirNames = new Stack<String>();
		
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
			xmlReader.setErrorHandler(errorHandler);
		}catch(SAXException e){
			e.printStackTrace();
		}	
	}
	
    
	public static void main(String[] args){				
		init();
		File sourceDir = new File("data/schematron");
				
		if(schemaFactory == null){
			schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
            try{
                schemaFactory.setFeature(Constants.REPLACE_MISSING_LIBRARY_FEATURE, true);
                schemaFactory.setFeature(Constants.LEVEL1_ATTRIBUTE_DEFAULT_VALUE_FEATURE, true);
                schemaFactory.setFeature(Constants.LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE, true);
                schemaFactory.setFeature(Constants.LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE, true);
                schemaFactory.setFeature(Constants.LEVEL2_ATTRIBUTE_ID_TYPE_FEATURE, true);
                schemaFactory.setFeature(Constants.LEVEL1_DOCUMENTATION_ELEMENT_FEATURE, true);
                schemaFactory.setFeature(Constants.PROCESS_EMBEDED_SCHEMATRON_FEATURE, true);
            }catch (SAXNotRecognizedException e) {
				e.printStackTrace();
			}catch (SAXNotSupportedException e) {
				e.printStackTrace();
			}
			schemaFactory.setErrorHandler(errorHandler);
		}
		sourceDirNames.clear();
		
		
        System.out.println();
		System.out.println("TESTED DIRECTORY "+sourceDir);		
		File[] content = sourceDir.listFiles();
		if(content.length == 0){
			System.out.println("empty directory ");
			return;
		}
		
		String[] xmlFiles = new String[content.length];
		File rngFile = null;
		for(int i = 0; i < content.length; i++){			
			String fileName = content[i].getName();
			if(content[i].isDirectory()){
				test(content[i]);				
			}else{		
				if(fileName.equals("schema.rng")){
					rngFile = content[i]; 
				}else if(fileName.endsWith(".xml") && fileName.startsWith("document")){
					// get the number
					// place in the
					xmlFiles[getXMLTestNumber(fileName)-1] = content[i].getAbsolutePath();
				}
			}
		}
		if(rngFile != null){		
			System.out.println("TESTED SCHEMA "+ rngFile);
			if(!initReader(rngFile)){
				return;
			}
			for(int i = 0; i < xmlFiles.length; i++){
				if(xmlFiles[i] != null){		
					System.out.println("TESTED DOCUMENT "+ xmlFiles[i]);
					validateXMLFile(xmlFiles[i]);
				}
			}
		}else if(xmlFiles != null){
			for(int i = 0; i < xmlFiles.length; i++){
				if(xmlFiles[i] != null){
					System.out.println("no schema file ");
					return;
				}
			}			
		}
	}
	
	
	private static void test(File sourceDir){
        System.out.println();
		System.out.println("TESTED DIRECTORY "+sourceDir);		
		File[] content = sourceDir.listFiles();
		if(content.length == 0){
			System.out.println("empty directory ");
			return;
		}
		
		sourceDirNames.push(sourceDir.getName());
		
		String[] xmlFiles = new String[content.length];
		File rngFile = null;
		for(int i = 0; i < content.length; i++){			
			String fileName = content[i].getName();
			if(content[i].isDirectory()){
				test(content[i]);				
			}else{		
				if(fileName.equals("schema.rng")){
					rngFile = content[i]; 
				}else if(fileName.endsWith(".xml") && fileName.startsWith("document")){
					// get the number
					// place in the
					xmlFiles[getXMLTestNumber(fileName)-1] = content[i].getAbsolutePath();
				}
			}
		}
		if(rngFile != null){		
			System.out.println("TESTED SCHEMA "+ rngFile);
			if(!initReader(rngFile)){
				sourceDirNames.pop();
				return;
			}
			for(int i = 0; i < xmlFiles.length; i++){
				if(xmlFiles[i] != null){		
					System.out.println("TESTED DOCUMENT "+ xmlFiles[i]);
					validateXMLFile(xmlFiles[i]);
				}
			}
		}else if(xmlFiles != null){
			for(int i = 0; i < xmlFiles.length; i++){
				if(xmlFiles[i] != null){
					System.out.println("no schema file ");
					sourceDirNames.pop();
					return;
				}
			}			
		}
		sourceDirNames.pop();
	}
	
	static int getXMLTestNumber(String name){
		int start = name.lastIndexOf("document")+8;
		int end = name.length()-4;
		int testNumber = Integer.parseInt(name.substring(start, end));
		return testNumber;
	}
	
	static boolean initReader(File schemaFile){
		Schema schema;    
		try{            
			schema = schemaFactory.newSchema(schemaFile);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
        
		ValidatorHandler vh = schema.newValidatorHandler();               
        
		vh.setErrorHandler(errorHandler);
		
		xmlReader.setContentHandler(vh);
		
		return true;
	}	
	
	static void validateXMLFile(String filePath){
		String name = filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.indexOf(".xml"));		
		try{					
			xmlReader.parse(filePath);			
		}catch(IOException e){
			e.printStackTrace();
		}catch(SAXParseException e){
			e.printStackTrace();
		}catch(SAXException e){
			e.printStackTrace();
		}
	}
}
