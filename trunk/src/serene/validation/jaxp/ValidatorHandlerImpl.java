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

package serene.validation.jaxp;

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.net.URI;

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

import org.xml.sax.helpers.AttributesImpl;

import org.w3c.dom.ls.LSResourceResolver;

import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.ActiveModelPool;
import serene.validation.schema.active.ActiveModel;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.datatype.ValidationEventContext;

import serene.util.CharsBuffer;
import serene.util.SpaceCharsHandler;

import serene.Constants;

import sereneWrite.MessageWriter;

class ValidatorHandlerImpl extends ValidatorHandler{
    String DTD_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdHandler";
    String DTD_MAPPING_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdMapping";

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
	
	boolean namespacePrefixes;
	String defaultNamespace;
	HashMap<String, String> prefixNamespaces;	
	
	MessageWriter debugWriter;	
    
	public ValidatorHandlerImpl(ValidatorEventHandlerPool eventHandlerPool,
							ValidatorErrorHandlerPool errorHandlerPool,
							ActiveModelPool activeModelPool,
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
		
		namespacePrefixes = false;
		prefixNamespaces = new HashMap<String, String>();

        validationEventContext = new ValidationEventContext(debugWriter);
        eventHandlerPool.setValidationContext(validationEventContext);
	}
	
	protected void finalize(){		
		eventHandlerPool.releaseHandlers();
		errorHandlerPool.releaseHandlers();
		
		eventHandlerPool.recycle();
		errorHandlerPool.recycle();
	}
	
	
	public ValidationItemLocator getValidationItemLocator(){
		return validationItemLocator;
	}
	
	public ContentHandler getContentHandler(){
		return contentHandler;		
	}
	
	public void setContentHandler(ContentHandler contentHandler){
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
	
	
	public void processingInstruction(String target, String date) throws SAXException{
		if(contentHandler != null) contentHandler.processingInstruction(target, date);
	} 
	public void skippedEntity(String name) throws SAXException{}	
	public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException{
		if(contentHandler != null) contentHandler.ignorableWhitespace(ch, start, len);
	}
	public void startDocument() throws SAXException{
		validationEventContext.reset();
		validationItemLocator.clear();
		activeModel = activeModelPool.getActiveModel(validationItemLocator, 
														errorDispatcher);
		elementHandler = eventHandlerPool.getStartValidationHandler(activeModel.getStartElement());
		
		defaultNamespace = null;
		prefixNamespaces.clear();
		
		if(contentHandler != null) contentHandler.startDocument();
	}			
	public void startPrefixMapping(String prefix, String uri) throws SAXException{
		validationEventContext.startPrefixMapping(prefix, uri);
		if(namespacePrefixes){
			if(prefix.equals("")){
				defaultNamespace = uri;
			}else{
				prefixNamespaces.put(prefix, uri);
			}
		}
		if(contentHandler != null) contentHandler.startPrefixMapping(prefix, uri);
	}	
	public void endPrefixMapping(String prefix) throws SAXException{
		validationEventContext.endPrefixMapping(prefix);		
		if(contentHandler != null) contentHandler.endPrefixMapping(prefix);
	}	
	public void setDocumentLocator(Locator locator){
		this.locator = locator;
		if(contentHandler != null) contentHandler.setDocumentLocator(locator);
	}
	public void characters(char[] chars, int start, int length)throws SAXException{
		//TODO make sure this is correct for all circumstances
		String chunk = new String(chars, start, length);	
		charsBuffer.append(chars, start, length);
		validationItemLocator.newCharsContent(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber());
		if(contentHandler != null) contentHandler.characters(chars, start, length);
	}
	
	
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{
		char[] charContent = charsBuffer.removeCharsArray();
		if(charContent.length > 0){			
			elementHandler.handleCharacters(charContent);		
			validationItemLocator.closeCharsContent();
		}
		validationItemLocator.newElement(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), namespaceURI, localName, qName);
		elementHandler = elementHandler.handleStartElement(qName, namespaceURI, localName);
		elementHandler.handleAttributes(attributes, locator);
		
		if(contentHandler != null){
			if(namespacePrefixes){
				//create new AttributesImpl
				//add xmlns attributes
				AttributesImpl ai = new AttributesImpl(attributes);
				if(defaultNamespace != null){
					ai.addAttribute(XMLConstants.XMLNS_ATTRIBUTE_NS_URI , XMLConstants.XMLNS_ATTRIBUTE , XMLConstants.XMLNS_ATTRIBUTE, "CDATA", defaultNamespace);
					defaultNamespace = null;
				}
				if(!prefixNamespaces.isEmpty()){
					Iterator<Map.Entry<String, String>> iterator = prefixNamespaces.entrySet().iterator();
					while(iterator.hasNext()){
						Map.Entry<String, String> me = iterator.next();
						String p = me.getKey();
						String u = me.getValue();
						ai.addAttribute(XMLConstants.XMLNS_ATTRIBUTE_NS_URI , p , "xmlns:"+p, "CDATA", u);
					}
					prefixNamespaces.clear();
				}
				
				contentHandler.startElement(namespaceURI, localName, qName, ai);
			}else{
				contentHandler.startElement(namespaceURI, localName, qName, attributes);
			}
		}		
	}		
	
	public void endElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{
		char[] charContent = charsBuffer.removeCharsArray();
		if(charContent.length > 0){			
			elementHandler.handleCharacters(charContent);		
			validationItemLocator.closeCharsContent();
		}
		
		elementHandler.handleEndElement(locator);
		ElementEventHandler parent = elementHandler.getParentHandler(); 
		elementHandler.recycle();
		elementHandler = parent;
		validationItemLocator.closeElement();
		
		if(contentHandler != null) contentHandler.endElement(namespaceURI, localName, qName);
	}
	
	public void endDocument()  throws SAXException {		
		elementHandler.handleEndElement(locator);
		elementHandler.recycle();
		elementHandler = null;
		activeModel.recycle();
		activeModel = null;
		if(contentHandler != null) contentHandler.endDocument();
	}
		
	public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
		
        if (name == null) {
            throw new NullPointerException();
        }
		if(name.equals("http://xml.org/sax/features/namespace-prefixes")){
			namespacePrefixes = value;
			return;
		}
        throw new SAXNotRecognizedException(name);
    }
	
	public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (name == null) {
            throw new NullPointerException();
        }

        throw new SAXNotRecognizedException(name);
    }
    
    public void setProperty(String name, Object object)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.DTD_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
        }else if(name.equals(Constants.DTD_MAPPING_PROPERTY)){
            // recognized but not set, only for retrieval
        }else if(name.equals(Constants.ERROR_HANDLER_POOL_PROPERTY)){
            // recognized but not set, only for retrieval
        }else if(name.equals(Constants.EVENT_HANDLER_POOL_PROPERTY)){
            // recognized but not set, only for retrieval
        }else if(name.equals(Constants.ITEM_LOCATOR_PROPERTY)){
            // recognized but not set, only for retrieval
        }else if(name.equals(Constants.MATCH_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
        }

        throw new SAXNotRecognizedException(name);
    }
   
    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.DTD_HANDLER_PROPERTY)){
            return validationEventContext;
        }else if(name.equals(Constants.DTD_MAPPING_PROPERTY)){
            return validationEventContext.getDTDMapping();
        }else if(name.equals(Constants.ERROR_HANDLER_POOL_PROPERTY)){
            return errorHandlerPool;
        }else if(name.equals(Constants.EVENT_HANDLER_POOL_PROPERTY)){
            return eventHandlerPool;
        }else if(name.equals(Constants.ITEM_LOCATOR_PROPERTY)){
            return validationItemLocator;
        }else if(name.equals(Constants.MATCH_HANDLER_PROPERTY)){
            return matchHandler;
        }

        throw new SAXNotRecognizedException(name);
    }
}