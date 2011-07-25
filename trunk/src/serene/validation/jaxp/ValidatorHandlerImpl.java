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
import java.util.HashMap;
import java.util.Map;
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

import serene.SchemaModel;
import serene.validation.schema.active.ActiveModel;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.dtdcompatibility.AttributeDefaultValueHandler;
import serene.dtdcompatibility.AttributeDefaultValueModel;
import serene.dtdcompatibility.AttributeIdTypeHandler;
import serene.dtdcompatibility.AttributeIdTypeModel;

import serene.DocumentContext;

import serene.util.CharsBuffer;
import serene.util.SpaceCharsHandler;

import sereneWrite.MessageWriter;

import serene.Constants;

public class ValidatorHandlerImpl extends ValidatorHandler{    
	ContentHandler contentHandler;	
	LSResourceResolver lsResourceResolver;
	TypeInfoProvider typeInfoProvider;
	Locator locator;
		
	
	ValidatorEventHandlerPool eventHandlerPool;	
	ValidatorErrorHandlerPool errorHandlerPool;
	
	SchemaModel schemaModel;
							
	SpaceCharsHandler spaceHandler;
	MatchHandler matchHandler;
	ErrorDispatcher errorDispatcher;
	ValidationItemLocator validationItemLocator;
	CharsBuffer charsBuffer;
		
	ElementEventHandler elementHandler;	
	ActiveModel activeModel;
	
    boolean secureProcessing;
    boolean namespacePrefixes;
    boolean level1AttributeDefaultValue;
    boolean level2AttributeDefaultValue;
    boolean level1AttributeIdType;
    boolean level2AttributeIdType;
    boolean level1AttributeIdTypeMemo;
    boolean restrictToFileName;
        
    AttributeDefaultValueHandler attributeDefaultValueHandler;
    AttributeIdTypeHandler attributeIdTypeHandler;
        
    //for namespacesPrefixes feature
    String defaultNamespace;
    HashMap<String, String> prefixNamespaces;
    DocumentContext documentContext;
    
    final boolean noModification = true;
	MessageWriter debugWriter;	
	public ValidatorHandlerImpl(boolean secureProcessing,                            
                            boolean namespacePrefixes,
                            boolean level1AttributeDefaultValue,
                            boolean level2AttributeDefaultValue,
                            boolean level1AttributeIdType,
                            boolean level2AttributeIdType,
                            boolean restrictToFileName,
                            ValidatorEventHandlerPool eventHandlerPool,
							ValidatorErrorHandlerPool errorHandlerPool,
							SchemaModel schemaModel,
							MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
        this.secureProcessing = secureProcessing;
        this.namespacePrefixes = namespacePrefixes; 
        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
        this.level2AttributeDefaultValue = level2AttributeDefaultValue;
        this.level1AttributeIdType = level1AttributeIdType;
        this.level2AttributeIdType = level2AttributeIdType;
        level1AttributeIdTypeMemo = level1AttributeIdType;
        this.restrictToFileName = restrictToFileName;
		this.eventHandlerPool = eventHandlerPool;	
		this.errorHandlerPool = errorHandlerPool;
				
		
		this.schemaModel = schemaModel;
		                
		validationItemLocator = new ValidationItemLocator(debugWriter);		
		errorDispatcher = new ErrorDispatcher(debugWriter);
		matchHandler  = new MatchHandler(debugWriter);		
		charsBuffer = new CharsBuffer(debugWriter);		
		spaceHandler = new SpaceCharsHandler(debugWriter);
		
		errorHandlerPool.fill(errorDispatcher);
		eventHandlerPool.fill(spaceHandler, matchHandler, validationItemLocator, errorHandlerPool);
		
        documentContext = new DocumentContext(debugWriter);
        eventHandlerPool.setValidationContext(documentContext);
		//default recognizeOutOfContext is true
        
        prefixNamespaces = new HashMap<String, String>();
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
	
	
	public void processingInstruction(String target, String data) throws SAXException{
        if(contentHandler != null) contentHandler.processingInstruction(target, data);
    } 
    
	public void skippedEntity(String name) throws SAXException{
		if(contentHandler != null) contentHandler.skippedEntity(name);
    }
	
	public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException{
        if(contentHandler != null) contentHandler.ignorableWhitespace(ch, start, len);
    }
    
	public void startDocument()  throws SAXException{		
		errorDispatcher.init();
		documentContext.reset();        
		validationItemLocator.clear();
		activeModel = schemaModel.getActiveModel(validationItemLocator, 
														errorDispatcher);
        if(activeModel == null) throw new IllegalStateException("Attempting to use incorrect schema. Due to errors in the schema document, it cannot be used for validation.");
        
        matchHandler.setActiveModel(activeModel);
        
        if(level2AttributeDefaultValue){                        
            AttributeDefaultValueModel attributeDefaultValueModel = schemaModel.getAttributeDefaultValueModel();
            if(attributeDefaultValueModel == null) throw new IllegalStateException("Attempting to use incorrect schema. Feature "+Constants.LEVEL1_ATTRIBUTE_DEFAULT_VALUE_FEATURE+" cannot be supported.");
            if(attributeDefaultValueHandler == null) attributeDefaultValueHandler = new AttributeDefaultValueHandler(attributeDefaultValueModel, errorDispatcher, debugWriter);
        }
        
        if(level1AttributeIdType || level2AttributeIdType){
            AttributeIdTypeModel attributeIdTypeModel = schemaModel.getAttributeIdTypeModel();
            if(attributeIdTypeModel == null) throw new IllegalStateException("Attempting to use incorrect schema. Feature "+Constants.LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE+" cannot be supported.");
            if(attributeIdTypeHandler == null){
                attributeIdTypeHandler = new AttributeIdTypeHandler(attributeIdTypeModel, errorDispatcher, debugWriter);
                attributeIdTypeHandler.setRestrictToFileName(restrictToFileName);
            }
            else attributeIdTypeHandler.init();
        }
        // TODO see about this, according to SAX spec the functioning of the Locator 
        // is not guaranteed here. It seems to work though.
        validationItemLocator.newElement(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), "", "document root", "document root");
		elementHandler = eventHandlerPool.getStartValidationHandler(activeModel.getStartElement());
                        
        defaultNamespace = null;
		prefixNamespaces.clear();
		
		if(contentHandler != null) contentHandler.startDocument();
        
		//count = 0; 		
	}			
	public void startPrefixMapping(String prefix, String uri)  throws SAXException{
		documentContext.startPrefixMapping(prefix, uri);
        if(namespacePrefixes){
			if(prefix.equals("")){
				defaultNamespace = uri;
			}else{
				prefixNamespaces.put(prefix, uri);
			}
		}
		if(contentHandler != null) contentHandler.startPrefixMapping(prefix, uri);
	}	
	public void endPrefixMapping(String prefix)  throws SAXException{
		documentContext.endPrefixMapping(prefix);
        if(namespacePrefixes){
			if(prefix.equals("")){
				defaultNamespace = null;
			}else{
				prefixNamespaces.remove(prefix);
			}
		}
        if(contentHandler != null) contentHandler.endPrefixMapping(prefix);
	}	
	public void setDocumentLocator(Locator locator){
		this.locator = locator;
        if(!documentContext.isBaseURISet())documentContext.setBaseURI(locator.getSystemId());
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
		//System.out.println((++count)+" "+locator.getLineNumber()+" "+qName);
		char[] charContent = charsBuffer.removeCharsArray();
		if(charContent.length > 0){			
			elementHandler.handleInnerCharacters(charContent);		
			validationItemLocator.closeCharsContent();
		}
		validationItemLocator.newElement(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber(), namespaceURI, localName, qName);
		elementHandler = elementHandler.handleStartElement(qName, namespaceURI, localName, restrictToFileName);
		elementHandler.handleAttributes(attributes, locator);
        
        
        if(level2AttributeDefaultValue){
            // TODO 
            // Review to make more efficient. When contentHandler == null this 
            // needs to be done only if 
            //      - there are default values for attributes with non-null ID-type
            //      - level1AttributeIdType is set  
            attributes = attributeDefaultValueHandler.handle(namespaceURI, localName, attributes, documentContext);
        } 
        if(level1AttributeIdType && !level2AttributeIdType){
            attributeIdTypeHandler.handle(noModification, namespaceURI, localName, attributes, locator);
        }else if(level2AttributeIdType){
            if(contentHandler != null){
                attributeIdTypeHandler.handle(noModification, namespaceURI, localName, attributes, locator);
            }else{    
                attributes = attributeIdTypeHandler.handle(namespaceURI, localName, attributes, locator);
            }
        }
        if(namespacePrefixes && contentHandler != null){
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
        }else if(contentHandler != null){
            contentHandler.startElement(namespaceURI, localName, qName, attributes);
        }
	}		
	
	public void endElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{
		char[] charContent = charsBuffer.removeCharsArray();
		elementHandler.handleLastCharacters(charContent);
        if(charContent.length > 0)validationItemLocator.closeCharsContent();
		
		elementHandler.handleEndElement(restrictToFileName, locator);
		ElementEventHandler parent = elementHandler.getParentHandler(); 
		elementHandler.recycle();
		elementHandler = parent;		
		validationItemLocator.closeElement();
        
        if(contentHandler != null) contentHandler.endElement(namespaceURI, localName, qName);
	}
	
	public void endDocument()  throws SAXException {
		elementHandler.handleEndElement(restrictToFileName, locator);
		elementHandler.recycle();
		elementHandler = null;
		activeModel.recycle();
		activeModel = null;
        if(level1AttributeIdType){
            attributeIdTypeHandler.handleRefs(locator);
        }
        if(contentHandler != null) contentHandler.endDocument();
	}
	
	void xmlVersion(String version){}
	
	public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            namespacePrefixes = value;  
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE)){
            level2AttributeDefaultValue = value;            
            if(level2AttributeDefaultValue){
                if(!level1AttributeDefaultValue) throw new SAXNotSupportedException("Schema model configuration cannot support feature, SchemaFactory features for creating needed structures were not set.");                
                AttributeDefaultValueModel attributeDefaultValueModel = schemaModel.getAttributeDefaultValueModel();
                if(attributeDefaultValueModel == null) throw new SAXNotSupportedException("Schema model configuration cannot support feature, needed schema structures are incorrect.");
                if(attributeDefaultValueHandler == null){
                    attributeDefaultValueHandler = new AttributeDefaultValueHandler(attributeDefaultValueModel, errorDispatcher, debugWriter);
                }
            }            
        }else if(name.equals(Constants.LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE)){
            if(value){
                if(!level1AttributeIdTypeMemo) throw new SAXNotSupportedException("Schema model configuration cannot support feature, SchemaFactory features for creating needed structures were not set.");
                AttributeIdTypeModel attributeIdTypeModel = schemaModel.getAttributeIdTypeModel();
                if(attributeIdTypeModel == null) throw new SAXNotSupportedException("Schema model configuration cannot support feature, needed schema structures are incorrect.");
                if(attributeIdTypeHandler == null){
                    attributeIdTypeHandler = new AttributeIdTypeHandler(attributeIdTypeModel, errorDispatcher, debugWriter);
                }
            }
            level1AttributeIdType = value;
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_ID_TYPE_FEATURE)){
            level2AttributeIdType = value;
            if(level2AttributeIdType){
                if(!level1AttributeIdTypeMemo) throw new SAXNotSupportedException("Schema model configuration cannot support feature, SchemaFactory features for creating needed structures were not set.");                
                AttributeIdTypeModel attributeIdTypeModel = schemaModel.getAttributeIdTypeModel();
                if(attributeIdTypeModel == null) throw new SAXNotSupportedException("Schema model configuration cannot support feature, needed schema structures are incorrect.");
                if(attributeIdTypeHandler == null){
                    attributeIdTypeHandler = new AttributeIdTypeHandler(attributeIdTypeModel, errorDispatcher, debugWriter);
                }
            }
        }else if(name.equals(Constants.RESTRICT_TO_FILE_NAME_FEATURE)){
            restrictToFileName = value;
            attributeIdTypeHandler.setRestrictToFileName(restrictToFileName);
        }else{
            throw new SAXNotRecognizedException(name);
        }
    }
	
	public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.NAMESPACES_PREFIXES_SAX_FEATURE)){
            return namespacePrefixes;  
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE)){
            return level2AttributeDefaultValue;
        }else if(name.equals(Constants.LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE)){
            return level1AttributeIdType;
        }else if(name.equals(Constants.LEVEL2_ATTRIBUTE_ID_TYPE_FEATURE)){
            return level2AttributeIdType;
        }else if(name.equals(Constants.RESTRICT_TO_FILE_NAME_FEATURE)){
            return restrictToFileName;                        
        }else{
            throw new SAXNotRecognizedException(name);
        }
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
        }else if(name.equals(Constants.DOCUMENT_CONTEXT_PROPERTY)){
            // recognized but not set, only for retrieval
        }else if(name.equals(Constants.MATCH_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
        }else if(name.equals(Constants.ATTRIBUTE_DEFAULT_VALUE_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
        }else if(name.equals(Constants.ATTRIBUTE_ID_TYPE_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
        }

        throw new SAXNotRecognizedException(name);
    }
   
    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.DTD_HANDLER_PROPERTY)){
            return documentContext;
        }else if(name.equals(Constants.DTD_MAPPING_PROPERTY)){
            return documentContext.getDTDMapping();
        }else if(name.equals(Constants.ERROR_HANDLER_POOL_PROPERTY)){
            return errorHandlerPool;
        }else if(name.equals(Constants.EVENT_HANDLER_POOL_PROPERTY)){
            return eventHandlerPool;
        }else if(name.equals(Constants.ITEM_LOCATOR_PROPERTY)){
            return validationItemLocator;
        }else if(name.equals(Constants.DOCUMENT_CONTEXT_PROPERTY)){
            return documentContext;
        }else if(name.equals(Constants.MATCH_HANDLER_PROPERTY)){
            return matchHandler;
        }else if(name.equals(Constants.ATTRIBUTE_DEFAULT_VALUE_HANDLER_PROPERTY)){
            if(attributeDefaultValueHandler == null){
                AttributeDefaultValueModel attributeDefaultValueModel = schemaModel.getAttributeDefaultValueModel();
                attributeDefaultValueHandler = new AttributeDefaultValueHandler(attributeDefaultValueModel, errorDispatcher, debugWriter);
            }
            return attributeDefaultValueHandler;
        }else if(name.equals(Constants.ATTRIBUTE_ID_TYPE_HANDLER_PROPERTY)){
            if(attributeIdTypeHandler == null){
                AttributeIdTypeModel attributeIdTypeModel = schemaModel.getAttributeIdTypeModel();
                attributeIdTypeHandler = new AttributeIdTypeHandler(attributeIdTypeModel, errorDispatcher, debugWriter);
            }
            return attributeIdTypeHandler;
        }

        throw new SAXNotRecognizedException(name);
    }
}