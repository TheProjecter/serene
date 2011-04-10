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

import serene.SereneRecoverableException;

import serene.datatype.MissingLibraryException;

import serene.dtdcompatibility.DTDCompatibilityException;
import serene.dtdcompatibility.AttributeDefaultValueException;
import serene.dtdcompatibility.AttributeIdTypeException;
import serene.dtdcompatibility.DocumentationElementException;

import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;
import sereneWrite.FileHandler;

public class ErrorDispatcher implements ErrorHandler{
	
	ErrorHandler errorHandler;
    ErrorDispatcher errorDispatcher;
    
    boolean hasError = false;
	boolean hasUnrecoverableError = false;
    boolean hasMissingDatatypeLibraryError = false;
    boolean hasDTDCompatibilityError = false;
    boolean hasAttributeDefaultValueError = false;
    boolean hasAttributeIdTypeError = false;
    boolean hasDocumentationElementError = false;
    
	MessageWriter debugWriter;
		
	public ErrorDispatcher(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	public void init(){
		hasError = false;
        hasUnrecoverableError = false;
        hasMissingDatatypeLibraryError = false;
        hasDTDCompatibilityError = false;
        hasAttributeDefaultValueError = false;
        hasAttributeIdTypeError = false;
        hasDocumentationElementError = false;
	}
	
	
	public ErrorHandler getErrorHandler(){
        if(errorDispatcher != null) return errorDispatcher;
		return errorHandler;
	}
	
	public void setErrorHandler(ErrorHandler errorHandler){
        if(errorHandler instanceof ErrorDispatcher){
            setErrorDispatcher((ErrorDispatcher)errorHandler);
        }else{
            this.errorHandler = errorHandler;
            errorDispatcher = null;
        }
	}

    public ErrorDispatcher getErrorDispatcher(){
		return errorDispatcher;
	}
	
	public void setErrorDispatcher(ErrorDispatcher errorDispatcher){
		this.errorDispatcher = errorDispatcher;
        errorHandler = null;
	}
    
	public void fatalError(SAXParseException exception) throws SAXException{
		if(errorHandler != null) errorHandler.fatalError(exception);
        else if(errorDispatcher != null) errorDispatcher.fatalError(exception);
		hasError = true;
        hasUnrecoverableError = true;
	}
	
	public void error(SAXParseException exception) throws SAXException{        
        if(errorHandler != null) errorHandler.error(exception);
        else if(errorDispatcher != null) errorDispatcher.error(exception);        
        hasError = true;
        hasUnrecoverableError = true;        
	}
	
	public void error(SereneRecoverableException exception) throws SAXException{
		if(errorHandler != null) errorHandler.error(exception);
        else if(errorDispatcher != null) errorDispatcher.error(exception);
        hasError = true;
	}
    
    public void error(MissingLibraryException exception) throws SAXException{
        if(errorHandler != null) errorHandler.error(exception);
        else if(errorDispatcher != null) errorDispatcher.error(exception);
        hasError = true;
        hasMissingDatatypeLibraryError = true;
    }
    
    public void error(DTDCompatibilityException exception) throws SAXException{
		if(errorHandler != null) errorHandler.error(exception);
        else if(errorDispatcher != null) errorDispatcher.error(exception);
        hasError = true;
        hasDTDCompatibilityError = true;
	}
    
    public void error(AttributeDefaultValueException exception) throws SAXException{
		if(errorHandler != null) errorHandler.error((AttributeDefaultValueException)exception);
        else if(errorDispatcher != null) errorDispatcher.error(exception);
        hasError = true;
        hasAttributeDefaultValueError = true;        
	}
    public void error(AttributeIdTypeException exception) throws SAXException{
		if(errorHandler != null) errorHandler.error(exception);
        else if(errorDispatcher != null) errorDispatcher.error(exception);
        hasError = true;
        hasAttributeIdTypeError = true;        
	}
    public void error(DocumentationElementException exception) throws SAXException{
		if(errorHandler != null) errorHandler.error(exception);
        else if(errorDispatcher != null) errorDispatcher.error(exception);
        hasError = true;
        hasDocumentationElementError = true;        
	}
    
	public void warning(SAXParseException exception) throws SAXException{
		if(errorHandler != null) errorHandler.warning(exception);
        else if(errorDispatcher != null) errorDispatcher.warning(exception);
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
        return hasDTDCompatibilityError;
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