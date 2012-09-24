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

package serene.validation.handlers.error;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;

import serene.RecoverableException;

import serene.datatype.DatatypeErrorHandler;
import serene.datatype.MissingLibraryException;

import serene.dtdcompatibility.DTDCompatibilityErrorHandler;
import serene.dtdcompatibility.AttributeDefaultValueException;
import serene.dtdcompatibility.AttributeIdTypeException;
import serene.dtdcompatibility.DocumentationElementException;

import serene.schematron.SchematronErrorHandler;
import serene.schematron.FailedAssertException;
import serene.schematron.SuccessfulReportException;

import serene.validation.handlers.content.util.InputStackDescriptor;

import sereneWrite.FileHandler;

public class ErrorDispatcher implements ErrorHandler,                                
                                ErrorListener, 
                                DatatypeErrorHandler,
                                DTDCompatibilityErrorHandler,
                                SchematronErrorHandler{
	
	ErrorHandler errorHandler;
    DatatypeErrorHandler datatypeErrorHandler;
    DTDCompatibilityErrorHandler dtdCompatibilityErrorHandler;
    SchematronErrorHandler schematronErrorHandler;
    
    boolean hasError = false;
	boolean hasUnrecoverableError = false;
    boolean hasMissingDatatypeLibraryError = false;
    boolean hasAttributeDefaultValueError = false;
    boolean hasAttributeIdTypeError = false;
    boolean hasDocumentationElementError = false;
    
	public ErrorDispatcher(){
	}
	
	public void init(){
		hasError = false;
        hasUnrecoverableError = false;
        hasMissingDatatypeLibraryError = false;
        hasAttributeDefaultValueError = false;
        hasAttributeIdTypeError = false;
        hasDocumentationElementError = false;
	}
	
	
	public ErrorHandler getErrorHandler(){
		return errorHandler;
	}
	
	public void setErrorHandler(ErrorHandler errorHandler){
	    this.errorHandler = errorHandler;
        if(errorHandler instanceof DatatypeErrorHandler){
            datatypeErrorHandler = (DatatypeErrorHandler)errorHandler;
        }
        if(errorHandler instanceof DTDCompatibilityErrorHandler){
            dtdCompatibilityErrorHandler = (DTDCompatibilityErrorHandler)errorHandler;
        }
        if(errorHandler instanceof SchematronErrorHandler){
            schematronErrorHandler = (SchematronErrorHandler)errorHandler;
        }
	}
	
    
    
	public void fatalError(SAXParseException exception) throws SAXException{
		if(errorHandler != null) errorHandler.fatalError(exception);
		hasError = true;
        hasUnrecoverableError = true;
	}
	
	public void error(SAXParseException exception) throws SAXException{ 
        if(errorHandler != null) errorHandler.error(exception);   
        hasError = true;
        hasUnrecoverableError = true;        
	}
	
    public void error(MissingLibraryException exception) throws SAXException{
        if(datatypeErrorHandler != null) datatypeErrorHandler.error(exception);
        else if(errorHandler != null) errorHandler.error(exception);
        hasError = true;
        hasMissingDatatypeLibraryError = true;
    }
        
    public void error(AttributeDefaultValueException exception) throws SAXException{
		if(dtdCompatibilityErrorHandler != null) dtdCompatibilityErrorHandler.error(exception);
        else if(errorHandler != null) errorHandler.error(exception);
        hasError = true;
        hasAttributeDefaultValueError = true;        
	}
    public void error(AttributeIdTypeException exception) throws SAXException{
		if(dtdCompatibilityErrorHandler != null) dtdCompatibilityErrorHandler.error(exception);
        else if(errorHandler != null) errorHandler.error(exception);
        hasError = true;
        hasAttributeIdTypeError = true;        
	}
    public void error(DocumentationElementException exception) throws SAXException{
		if(dtdCompatibilityErrorHandler != null) dtdCompatibilityErrorHandler.error(exception);
        else if(errorHandler != null) errorHandler.error(exception);
        hasError = true;
        hasDocumentationElementError = true;        
	}
	
	
	public void error(FailedAssertException exception) throws SAXException{
		if(schematronErrorHandler != null) schematronErrorHandler.error(exception);
        else if(errorHandler != null) errorHandler.error(exception);
        hasError = true;
        hasUnrecoverableError = true;
	}
	
	public void error(SuccessfulReportException exception) throws SAXException{
		if(schematronErrorHandler != null) schematronErrorHandler.error(exception);
        else if(errorHandler != null) errorHandler.error(exception);
        hasError = true;  
        hasUnrecoverableError = true;
	}
    
	public void warning(SAXParseException exception) throws SAXException{
		if(errorHandler != null) errorHandler.warning(exception);
	}
	
    
	
	public void fatalError(TransformerException exception) throws TransformerException{	     
		if(errorHandler != null){
		    try{
		        SourceLocator sl = exception.getLocator();
		        if(sl != null)errorHandler.fatalError(new SAXParseException(exception.getMessage(), sl.getPublicId(), sl.getSystemId(), sl.getLineNumber(), sl.getColumnNumber(), exception));
		        else errorHandler.fatalError(new SAXParseException(exception.getMessage(), null, null, -1, -1, exception));
		    }catch(SAXException e){
		        throw new TransformerException(e);
		    }
		}
		hasError = true;
        hasUnrecoverableError = true;
	}
	
	public void error(TransformerException exception) throws TransformerException{        
        if(errorHandler != null){		    	
		    try{
		        SourceLocator sl = exception.getLocator();
		        if(sl != null)errorHandler.error(new SAXParseException(exception.getMessage(), sl.getPublicId(), sl.getSystemId(), sl.getLineNumber(), sl.getColumnNumber(), exception));
		        else errorHandler.error(new SAXParseException(exception.getMessage(), null, null, -1, -1, exception));
		    }catch(SAXException e){
		        throw new TransformerException(e);
		    }
		}   
        hasError = true;
        hasUnrecoverableError = true;        
	}
	
	public void warning(TransformerException exception) throws TransformerException{
		if(errorHandler != null){
		    try{
		        SourceLocator sl = exception.getLocator();
		        if(sl != null)errorHandler.warning(new SAXParseException(exception.getMessage(), sl.getPublicId(), sl.getSystemId(), sl.getLineNumber(), sl.getColumnNumber(), exception));
		        else errorHandler.warning(new SAXParseException(exception.getMessage(), null, null, -1, -1, exception));
		    }catch(SAXException e){
		        throw new TransformerException(e);
		    }
		}
	}
	
    public boolean hasError(){
        return hasError;
    }
    
	public boolean hasUnrecoverableError(){
		return hasUnrecoverableError;
	}
	
    public boolean hasMissingDatatypeLibraryError(){
        return hasMissingDatatypeLibraryError;
    }
    
    public boolean hasDTDCompatibilityError(){
        return hasAttributeDefaultValueError || hasAttributeIdTypeError || hasDocumentationElementError;
    }
    
    public boolean hasAttributeDefaultValueError(){
        return hasAttributeDefaultValueError;
    }
    
    public boolean hasAttributeIdTypeError(){
        return hasAttributeIdTypeError;
    }
    
    public boolean hasDocumentationElementError(){
        return hasDocumentationElementError;
    }
    
	public String toString(){
		if(errorHandler != null)
			//return super.toString()+" "+hashCode()+" "+errorHandler.toString();
			return super.toString()+" "+errorHandler.toString();
		else
			//return super.toString()+" "+hashCode()+" null";
		return super.toString()+" null";
	}
}