/*
Copyright 2011 Radu Cernuta 

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

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;


import serene.validation.schema.active.components.AElement;

import sereneWrite.MessageWriter;

public class StartValidationErrorHandler extends ValidationErrorHandler{
	public StartValidationErrorHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	public void init(){
		messageHandler = new ContextMessageHandler(debugWriter);
	}
	public void recycle(){
		pool.recycle(this);
	}
		

    public void handle(String qName, AElement definition, Locator locator)
				throws SAXException{
		String message = messageHandler.getErrorMessage("");		
		if(!message.equals("")){
			errorDispatcher.error(new SAXParseException("Syntax error. Error at the root of the document corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation()+": "+message, locator));
		}
        
		String warningMessage = messageHandler.getWarningMessage("");		
		if(!warningMessage.equals("")){
			errorDispatcher.warning(new SAXParseException("Syntax warning. Warning at the root of the document corresponding to definition <"+definition.getQName()+"> at "+definition.getLocation()+": "+warningMessage, locator));
		}
		messageHandler.clear();
	}
	
	public void handle(String qName, Locator locator)
				throws SAXException{
		String message = messageHandler.getErrorMessage("");		
		if(!message.equals("")){
			errorDispatcher.error(new SAXParseException("Syntax error. "+message, locator));
		}
		
		String warningMessage = messageHandler.getWarningMessage("");		
		if(!warningMessage.equals("")){
			errorDispatcher.warning(new SAXParseException("Warning. "+warningMessage, locator));
		}
		messageHandler.clear();
	}
	
	public String toString(){
		//return "ValidationErrorHandler "+hashCode();
		return "StartValidationErrorHandler ";
	}
}
