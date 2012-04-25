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

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.XMLConstants;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.xml.sax.helpers.XMLReaderFactory;

import serene.validation.jaxp.RNGSchemaFactory;

import serene.util.BooleanList;

import sereneWrite.WriteErrorHandler;
import sereneWrite.WriteHandler;
import sereneWrite.ConsoleHandler;

public class Driver{	
	static final int SCHEMA = 0;
    static final int DOCS = 1;
    static final int FEATURES = 2;
    static final int OPTIONS_COUNT = 3;
    
	public static void main(String args[]){
		if(args == null || args.length == 0){
			System.out.println("Usage: java serene.Driver -f [name] [value] ... -s [schema-file] -d [xml-file] ... "
                            +"\nPlease use:"
                            +"\n-s [schema-file] to pass the schema file"
                            +"\n-d [xml-file] ... to pass the list of xml files "
                            +"\n-f [name] [value] ... if you want to pass a list of validation features. The [name] is a fully qualified URI, the [value] is a boolean, accepted values are true and false.");
			return;
		}
		
		XMLReader xmlReader = null;
		
		SchemaFactory schemaFactory;
		Schema schema;
		WriteErrorHandler errorHandler = new WriteErrorHandler(); 
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
				
		schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
				
		schemaFactory.setErrorHandler(errorHandler);
        errorHandler.init();
		ArrayList<File> schemaFiles = new ArrayList<File>(1);
        ArrayList<File> docFiles = new ArrayList<File>(); 
        BooleanList handledOptions = new BooleanList();
        
        for(int i = 0; i < OPTIONS_COUNT; i++){
            handledOptions.add(false);
        }
        
        String first = args[0];
        boolean correctArgs = false;
        try{
            if(first.equals("-d"))correctArgs = handleDocs(args, 1, handledOptions, schemaFiles, docFiles, schemaFactory);
            else if(first.equals("-s"))correctArgs = handleSchema(args, 1, handledOptions, schemaFiles, docFiles, schemaFactory);
            else if(first.equals("-f"))correctArgs = handleFeatures(args, 1, handledOptions, schemaFiles, docFiles, schemaFactory);
        }catch(SAXNotRecognizedException e){
            e.printStackTrace();
            System.out.println("\nFor recognized features please consult documentation.");
			return;
        }catch(SAXNotSupportedException e){
            e.printStackTrace();
            System.out.println("\nFor supported features please consult documentation.");
			return;
        }
              
        
        if(!correctArgs){
            System.out.println("Usage: java serene.Driver -f [name] [value] ... -s [schema-file] -d [xml-file] ... "
                            +"\nPlease use:"
                            +"\n-s [schema-file] to pass the schema file"
                            +"\n-d [xml-file] ... to pass the list of xml files "
                            +"\n-f [name] [value] ... if you want to pass a list of validation features. The [name] is a fully qualified URI, the [value] is a boolean, accepted values are true and false.");
			return;
        }
        
        File schemaFile = schemaFiles.get(0);
        
        
        try{	
			schema = schemaFactory.newSchema(schemaFile);
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
        
		if(errorHandler.hasError())return;		
		ValidatorHandler vh = schema.newValidatorHandler();
		vh.setErrorHandler(errorHandler);
		
		xmlReader.setContentHandler(vh);

		for(int i = 0; i < docFiles.size(); i++){                        
            File docFile = docFiles.get(i);
			String name = docFile.getName();
            if(!name.endsWith(".xml")){
                System.out.println("Usage: java serene.Driver -f [name] [value] ... -s [schema-file] -d [xml-file] ... "
                            +"\nPlease use:"
                            +"\n-s [schema-file] to pass the schema file"
                            +"\n-d [xml-file] ... to pass the list of xml files "
                            +"\n-f [name] [value] ... if you want to pass a list of validation features. The [name] is a fully qualified URI, the [value] is a boolean, accepted values are true and false.");
                return;
            } 			
			try{				
				xmlReader.parse(new InputSource(docFile.toURI().toASCIIString()));
			}catch(IOException e){
				e.printStackTrace();
			}catch(SAXParseException e){
				e.printStackTrace();
			}catch(SAXException e){
				e.printStackTrace();
			}
		}		
	}

    static boolean handleDocs(String[] args,  int offset, BooleanList handledOptions, ArrayList<File> schemaFiles, ArrayList<File> docFiles, SchemaFactory schemaFactory) throws SAXNotRecognizedException, SAXNotSupportedException{
        if(offset == args.length) return false;
        handledOptions.set(DOCS, true);        
        for(int i = offset; i < args.length; i++){            
            String current = args[i];
            if(current.equals("-d")){
                return false;
            }else if(current.equals("-f")){
                if(handledOptions.get(FEATURES)) return false;
                return handleFeatures(args, i+1, handledOptions, schemaFiles, docFiles, schemaFactory);
            }else if(current.equals("-s")){
                if(handledOptions.get(SCHEMA)) return false;
                return handleSchema(args, i+1, handledOptions, schemaFiles, docFiles, schemaFactory);
            }
            docFiles.add(new File(current));            
        }      
        return true;
    }
    
    static boolean handleSchema(String[] args,  int offset, BooleanList handledOptions, ArrayList<File> schemaFiles, ArrayList<File> docFiles, SchemaFactory schemaFactory) throws SAXNotRecognizedException, SAXNotSupportedException{
        if(offset == args.length) return false;        
        handledOptions.set(SCHEMA, true);        
        String current = args[offset];
        File schemaFile = null;
        if(current.equals("-d")){
            if(handledOptions.get(DOCS)) return false;
            handleDocs(args, offset+1, handledOptions, schemaFiles, docFiles, schemaFactory);
            return false;
        }else if(current.equals("-f")){
            if(handledOptions.get(FEATURES)) return false;
            handleFeatures(args, offset+1, handledOptions, schemaFiles, docFiles, schemaFactory);
            return false;
        }else if(current.equals("-s")){
            return false;
        }else{
            schemaFiles.add(new File(current));
        }
        
        if(++offset == args.length) return true;
        
        current = args[offset];
        
        if(current.equals("-d")){
            if(handledOptions.get(DOCS)) return false;
            return handleDocs(args, offset+1, handledOptions, schemaFiles, docFiles, schemaFactory);
        }else if(current.equals("-f")){
            if(handledOptions.get(FEATURES)) return false;
            return handleFeatures(args, offset+1, handledOptions, schemaFiles, docFiles, schemaFactory);
        }else if(current.equals("-s")){
            return false;
        }else{
            return false;
        }        
    }
    
    static boolean handleFeatures(String[] args,  int offset, BooleanList handledOptions, ArrayList<File> schemaFiles, ArrayList<File> docFiles, SchemaFactory schemaFactory) throws SAXNotRecognizedException, SAXNotSupportedException{
        if(offset == args.length) return false;
        handledOptions.set(FEATURES, true);
        for(int i = offset; i < args.length; i++){
            String current = args[i];
            if(current.equals("-d")){
                if(handledOptions.get(DOCS)) return false;
                return handleDocs(args, i+1, handledOptions, schemaFiles, docFiles, schemaFactory);
            }else if(current.equals("-f")){
                return false;
            }else if(current.equals("-s")){
                if(handledOptions.get(SCHEMA)) return false;
                return handleSchema(args, i+1, handledOptions, schemaFiles, docFiles, schemaFactory);
            }            
            if(++i == args.length) return false;
            
            boolean value;
            String currentValue = args[i];
            if(currentValue.equals("false")){
                value = false;
            }else if(currentValue.equals("true")){
                value = true;
            }else{
                return false;
            }                       
            schemaFactory.setFeature(current, value);
            
        }      
        return true;
    }	
}