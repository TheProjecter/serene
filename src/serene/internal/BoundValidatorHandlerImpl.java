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

package serene.internal;

import javax.xml.XMLConstants;

import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.TypeInfoProvider;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.Attributes;

import org.w3c.dom.ls.LSResourceResolver;


import serene.validation.schema.active.ActiveModelPool;
import serene.validation.schema.active.ActiveModel;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.datatype.ValidationEventContext;

import serene.util.CharsBuffer;
import serene.util.SpaceCharsHandler;

import serene.bind.BindingModel;
import serene.bind.ValidatorQueuePool;
import serene.bind.Queue;
import serene.bind.XmlBaseBinder;
import serene.bind.XmlnsBinder;

import sereneWrite.MessageWriter;

class BoundValidatorHandlerImpl extends ValidatorHandler{
	ContentHandler contentHandler;	
	LSResourceResolver lsResourceResolver;
	TypeInfoProvider typeInfoProvider;
	Locator locator;
		
	
	ValidatorEventHandlerPool eventHandlerPool;	
	ValidatorErrorHandlerPool errorHandlerPool;
	
	ActiveModelPool activeModelPool;
							
	SpaceCharsHandler spaceHandler;
	MatchHandler matchHandler;
	ErrorDispatcher errorDispatcher;
	ValidationItemLocator validationItemLocator;
	CharsBuffer charsBuffer;
	ValidationEventContext validationEventContext;
	
	ElementEventHandler elementHandler;	
	ActiveModel activeModel;
	
	MessageWriter debugWriter;	
	int count = 0;	
	
	

	BindingModel bindingModel;	
	ValidatorQueuePool queuePool;
	Queue queue;
	XmlBaseBinder xmlBaseBinder;
	XmlnsBinder xmlnsBinder;
	
	BoundValidatorHandlerImpl(ValidatorEventHandlerPool eventHandlerPool,
							ValidatorErrorHandlerPool errorHandlerPool,
							ActiveModelPool activeModelPool,
							BindingModel bindingModel,
							Queue queue,
							ValidatorQueuePool queuePool,
							MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		this.eventHandlerPool = eventHandlerPool;	
		this.errorHandlerPool = errorHandlerPool;
				
		
		this.activeModelPool = activeModelPool;
			
		validationItemLocator = new ValidationItemLocator(debugWriter);		
		errorDispatcher = new ErrorDispatcher(debugWriter);
		matchHandler  = new MatchHandler(debugWriter);		
		charsBuffer = new CharsBuffer(debugWriter);		
		spaceHandler = new SpaceCharsHandler(debugWriter);
		
		errorHandlerPool.fill(errorDispatcher);
		eventHandlerPool.fill(spaceHandler, matchHandler, validationItemLocator, errorHandlerPool);
		
		
		
		this.bindingModel = bindingModel;
		this.queuePool = queuePool;		
		this.queue = queue;
		
		xmlBaseBinder = new XmlBaseBinder(debugWriter);
		xmlnsBinder = new XmlnsBinder(debugWriter);
	}
	
	public ValidationItemLocator getValidationItemLocator(){
		return validationItemLocator;
	}
	
	public ContentHandler getContentHandler(){
		return contentHandler;		
	}
	
	public void setContentHandler(ContentHandler contentHanlder){
		this.contentHandler = contentHandler;
	}
	
	public ErrorHandler getErrorHandler(){
		return errorDispatcher.getErrorHandler();
	}
	
	public void setErrorHandler(ErrorHandler errorHandler){		
		errorDispatcher.setErrorHandler(errorHandler);		
	}
	
	public LSResourceResolver getResourceResolver(){
		return lsResourceResolver;
	} 
	
	public void setResourceResolver(LSResourceResolver lsResourceResolver){
		this.lsResourceResolver = lsResourceResolver;
	}
	
	public TypeInfoProvider getTypeInfoProvider(){
		return typeInfoProvider;
	}
	
	void setTypeInfoProvider(TypeInfoProvider typeInfoProvider){
		this.typeInfoProvider = typeInfoProvider;
	}
	
	public void processingInstruction(String target, String date){} 
	public void skippedEntity(String name){
		validationEventContext.addUnparsedEntity(name);
	}	
	public void ignorableWhitespace(char[] ch, int start, int len){}
	public void startDocument(){
		validationEventContext = new ValidationEventContext(locator.getSystemId(), validationEventContext, debugWriter);
		eventHandlerPool.setValidationContext(validationEventContext);
		validationItemLocator.clear();
		activeModel = activeModelPool.getActiveModel(validationItemLocator, 
													errorDispatcher);
		bindingModel.index(activeModel.getSElementIndexMap(), activeModel.getSAttributeIndexMap());
		queue.clear();
		queue.index(activeModel.getSAttributeIndexMap());
		queuePool.index(activeModel.getSAttributeIndexMap());
		
		elementHandler = eventHandlerPool.getBoundStartValidationHandler(activeModel.getStartElement(), bindingModel, queue, queuePool);
			
		xmlBaseBinder.bind(queue, locator.getSystemId());// must happen last, after queue.newRecord() which is in elementHandler's init, might need to be moved  
		
		//Note that locator is only garanteed to pass correct information AFTER
		//startDocument. The base URI of the document is also passed independently 
		//to the Simplifier. This needs reviewing. 		
	}			
	public void startPrefixMapping(String prefix, String uri){
		validationEventContext.startPrefixMapping(prefix, uri);
		xmlnsBinder.bind(queue, prefix, uri);
	}	
	public void endPrefixMapping(String prefix){
		validationEventContext.endPrefixMapping(prefix);
	}	
	public void setDocumentLocator(Locator locator){
		this.locator = locator;
	}
	public void characters(char[] chars, int start, int length)throws SAXException{
		//TODO make sure this is correct for all circumstances
		String chunk = new String(chars, start, length);	
		charsBuffer.append(chars, start, length);		
		validationItemLocator.newCharsContent(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber());
	}
	
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{		
		char[] charContent = charsBuffer.removeCharsArray();
		elementHandler.handleCharacters(charContent);		
		if(charContent.length > 0){	
			validationItemLocator.closeCharsContent();			
		}
				
		validationItemLocator.newElement(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), qName);
		elementHandler = elementHandler.handleStartElement(qName, namespaceURI, localName);		
		String xmlBase = attributes.getValue(XMLConstants.XML_NS_URI, "base");		
		if(xmlBase != null){
			xmlBaseBinder.bind(queue, xmlBase);
		}
		elementHandler.handleAttributes(attributes, locator);
		
	}		
	
	public void endElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{
		char[] charContent = charsBuffer.removeCharsArray();
		elementHandler.handleCharacters(charContent);		
		if(charContent.length > 0){
			validationItemLocator.closeCharsContent();
		} 
		
		elementHandler.handleEndElement(locator);		
		ElementEventHandler parent = elementHandler.getParentHandler(); 
		elementHandler.recycle();
		elementHandler = parent;
		validationItemLocator.closeElement();		
	}
	
	public void endDocument() throws SAXException{
		elementHandler.handleEndElement(locator);
		elementHandler.recycle();
		elementHandler = null;
		activeModel.recycle();
		activeModel = null;
		
		validationEventContext = validationEventContext.getParent();
		eventHandlerPool.setValidationContext(validationEventContext);
	}	
}