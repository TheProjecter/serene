package sereneSamples;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;

import serene.datatype.MissingLibraryException;
import serene.datatype.DatatypeErrorHandler;

import serene.dtdcompatibility.AttributeDefaultValueException;
import serene.dtdcompatibility.AttributeIdTypeException;
import serene.dtdcompatibility.DocumentationElementException;
import serene.dtdcompatibility.DTDCompatibilityErrorHandler;

class DifferentiatedErrorHandler implements DTDCompatibilityErrorHandler, DatatypeErrorHandler{
    boolean hasError;
    boolean hasUnrecoverableError;
    boolean hasMissingDatatypeLibraryError;
    boolean hasAttributeDefaultValueError;
    boolean hasAttributeIdTypeError;
    boolean hasDocumentationElementError; 
    public DifferentiatedErrorHandler(){
        init();
    }	   	
    public void init(){
        hasError = false;
        hasUnrecoverableError = false;
        hasMissingDatatypeLibraryError = false;
        hasAttributeDefaultValueError = false;
        hasAttributeIdTypeError = false;
        hasDocumentationElementError = false;
    }    
    
	public void fatalError(SAXParseException exception) throws SAXException{
        System.out.println("FATAL ERROR");
        System.out.println(exception.getMessage());
    }
	
	public void error(SAXParseException exception) throws SAXException{
        System.out.println("ERROR");
        /*if(exception instanceof MissingLibraryException){
            error((MissingLibraryException)exception);
        }else if(exception instanceof AttributeDefaultValueException){
            error((AttributeDefaultValueException)exception);
        }else if(exception instanceof AttributeIdTypeException){
            error((AttributeIdTypeException)exception);
        }else if(exception instanceof DocumentationElementException){
            error((DocumentationElementException)exception);
        }else{*/
            System.out.println(exception.getMessage());
            hasError = true;
            hasUnrecoverableError = true;
        /*}*/
    }
	public void error(MissingLibraryException exception) throws SAXException{
        System.out.println("MISSING DATATYPE LIBRARY ERROR");
        System.out.println(exception.getMessage());
        hasError = true;
        hasMissingDatatypeLibraryError = true;
    }
    public void error(AttributeDefaultValueException exception) throws SAXException{
        System.out.println("ATTTRIBUTE DEFAULT VALUE ERROR");
        System.out.println(exception.getMessage());
        hasError = true;
        hasAttributeDefaultValueError = true;
    }
    public void error(AttributeIdTypeException exception) throws SAXException{
        System.out.println("ATTRIBUTE ID TYPE ERROR");
        System.out.println(exception.getMessage());
        hasError = true;
        hasAttributeIdTypeError = true;
    }
    public void error(DocumentationElementException exception) throws SAXException{
        System.out.println("DOCUMENTATION ELEMENT ERROR");
        System.out.println(exception.getMessage());
        hasError = true;
        hasDocumentationElementError = true;
    }
    
	public void warning(SAXParseException exception) throws SAXException{
        System.out.println("WARNING");
        System.out.println(exception.getMessage());
    }
	
	
    boolean hasError(){
        return hasError;
    }
    boolean hasUnrecoverableError(){
        return hasUnrecoverableError;
    }
    boolean hasMissingDatatypeLibraryError(){
        return hasMissingDatatypeLibraryError;
    }
    boolean hasAttributeDefaultValueError(){
        return hasAttributeDefaultValueError;
    }
    boolean hasAttributeIdTypeError(){
        return hasAttributeIdTypeError;
    }
    boolean hasDocumentationElementError(){
        return hasDocumentationElementError;
    }
}
