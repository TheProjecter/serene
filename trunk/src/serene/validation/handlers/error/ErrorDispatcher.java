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

import serene.validation.handlers.content.util.ValidationItemLocator;

import sereneWrite.MessageWriter;

public class ErrorDispatcher implements ErrorHandler{
	
	ErrorHandler errorHandler;
	MessageWriter debugWriter;
		
	public ErrorDispatcher(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	
	public ErrorHandler getErrorHandler(){
		return errorHandler;
	}
	
	public void setErrorHandler(ErrorHandler errorHandler){
		this.errorHandler = errorHandler;
	}

	public void fatalError(SAXParseException exception) throws SAXException{
		if(errorHandler != null) errorHandler.fatalError(exception);
	}
	
	public void error(SAXParseException exception) throws SAXException{
		if(errorHandler != null) errorHandler.error(exception);
	}
	
	public void warning(SAXParseException exception) throws SAXException{
		if(errorHandler != null) errorHandler.warning(exception);
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