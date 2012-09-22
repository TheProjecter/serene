/*
Copyright 2012 Radu Cernuta 

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
import java.util.List;
import java.util.ArrayList;

import java.net.URI;

import javax.xml.XMLConstants;

import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.TypeInfoProvider;


import javax.xml.transform.sax.TransformerHandler;

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
import serene.validation.schema.simplified.SimplifiedModel;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;

import serene.validation.handlers.content.util.ActiveInputDescriptor;
import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptorPool;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;

import serene.validation.handlers.conflict.ValidatorConflictHandlerPool;

import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.handlers.structure.ValidatorRuleHandlerPool;


import serene.validation.handlers.error.ErrorDispatcher;

import serene.dtdcompatibility.AttributeDefaultValueHandler;
import serene.dtdcompatibility.AttributeDefaultValueModel;
import serene.dtdcompatibility.AttributeIdTypeHandler;
import serene.dtdcompatibility.AttributeIdTypeModel;

import serene.DocumentContext;

import serene.util.SpaceCharsHandler;

import serene.Constants;

public class RNGSchematronValidatorHandlerImpl extends ValidatorHandler{    
	ContentHandler contentHandler;	
	LSResourceResolver lsResourceResolver;
	TypeInfoProvider typeInfoProvider;
	Locator locator;
		
	
	ValidatorEventHandlerPool eventHandlerPool;	
	ValidatorStackHandlerPool stackHandlerPool;
	ValidatorErrorHandlerPool errorHandlerPool;
	
	SchemaModel schemaModel;
	SimplifiedModel simplifiedModel;
							
	SpaceCharsHandler spaceHandler;
	MatchHandler matchHandler;
	ErrorDispatcher errorDispatcher;
	ActiveInputDescriptor activeInputDescriptor;
	InputStackDescriptor inputStackDescriptor;
	CharacterContentDescriptorPool characterContentDescriptorPool;
	CharacterContentDescriptor characterContentDescriptor;
		
	ElementEventHandler elementHandler;	
	
    boolean secureProcessing;
    boolean namespacePrefixes;
    boolean level1AttributeDefaultValue;
    boolean level2AttributeDefaultValue;
    boolean level1AttributeIdType;
    boolean level2AttributeIdType;
    boolean level1AttributeIdTypeMemo;
    boolean restrictToFileName;
    boolean optimizedForResourceSharing;
        
    AttributeDefaultValueHandler attributeDefaultValueHandler;
    AttributeIdTypeHandler attributeIdTypeHandler;
        
    //for namespacesPrefixes feature
    String defaultNamespace;
    HashMap<String, String> prefixNamespaces;
    DocumentContext documentContext;
    
    final boolean noModification = true;
    
    List<ValidatorHandler> schematronValidatorHandlers; 
    
	public RNGSchematronValidatorHandlerImpl(boolean secureProcessing,                            
                            boolean namespacePrefixes,
                            boolean level1AttributeDefaultValue,
                            boolean level2AttributeDefaultValue,
                            boolean level1AttributeIdType,
                            boolean level2AttributeIdType,
                            boolean restrictToFileName,
                            boolean optimizedForResourceSharing,
                            ValidatorEventHandlerPool eventHandlerPool,
                            ValidatorConflictHandlerPool conflictHandlerPool,
                            ValidatorStackHandlerPool stackHandlerPool,
                            ValidatorRuleHandlerPool structureHandlerPool,
							ValidatorErrorHandlerPool errorHandlerPool,
							SchemaModel schemaModel,
							List<TransformerHandler> validatingTransformerHandlers){		
        this.secureProcessing = secureProcessing;
        this.namespacePrefixes = namespacePrefixes; 
        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
        this.level2AttributeDefaultValue = level2AttributeDefaultValue;
        this.level1AttributeIdType = level1AttributeIdType;
        this.level2AttributeIdType = level2AttributeIdType;
        level1AttributeIdTypeMemo = level1AttributeIdType;
        this.restrictToFileName = restrictToFileName;
        this.optimizedForResourceSharing = optimizedForResourceSharing;
        
		this.eventHandlerPool = eventHandlerPool;	
		this.errorHandlerPool = errorHandlerPool;				
		
		this.schemaModel = schemaModel;
		this.simplifiedModel = schemaModel.getSimplifiedModel();
		
		this.stackHandlerPool = stackHandlerPool;
		
		
		matchHandler  = new MatchHandler(simplifiedModel);
		spaceHandler = new SpaceCharsHandler();					
        documentContext = new DocumentContext();
        prefixNamespaces = new HashMap<String, String>();
        
		errorDispatcher = new ErrorDispatcher();
        activeInputDescriptor = new ActiveInputDescriptor();		
		inputStackDescriptor = new InputStackDescriptor(activeInputDescriptor);
		characterContentDescriptorPool = new CharacterContentDescriptorPool(activeInputDescriptor, spaceHandler); 
		characterContentDescriptor = characterContentDescriptorPool.getCharacterContentDescriptor();
		   
		conflictHandlerPool.fill(activeInputDescriptor, inputStackDescriptor);
        stackHandlerPool.fill(inputStackDescriptor, conflictHandlerPool, structureHandlerPool);
        structureHandlerPool.fill(stackHandlerPool, activeInputDescriptor, inputStackDescriptor);
        
        errorHandlerPool.init(errorDispatcher, activeInputDescriptor);        
        eventHandlerPool.init(spaceHandler, matchHandler, activeInputDescriptor, inputStackDescriptor, documentContext, stackHandlerPool, errorHandlerPool);
        

        if(!optimizedForResourceSharing)initResources();    
        
        schematronValidatorHandlers = new ArrayList<ValidatorHandler>(validatingTransformerHandlers.size());
        RNGSVRLParser rngSVRLParser = new RNGSVRLParser();
        for(int i = 0; i < validatingTransformerHandlers.size(); i++){
            ValidatorHandler schematronValidatorHandler = new SchematronValidatorHandlerImpl(secureProcessing,
                                            namespacePrefixes,
                                            restrictToFileName,
                                            optimizedForResourceSharing,
                                            validatingTransformerHandlers.get(i),
                                            rngSVRLParser);        
            schematronValidatorHandler.setErrorHandler(errorDispatcher);
            schematronValidatorHandlers.add(schematronValidatorHandler);
        }
	}
	
	void initResources(){
	    errorHandlerPool.fill();
        eventHandlerPool.fill();
        

		if(simplifiedModel == null) throw new IllegalStateException("Attempting to use an erroneous schema.");
        
	}
	void releaseResources(){
	    eventHandlerPool.releaseHandlers();
		errorHandlerPool.releaseHandlers();		
	}
	
	protected void finalize(){	
        if(!optimizedForResourceSharing) return;		
		eventHandlerPool.recycle();
		errorHandlerPool.recycle();
	}
	
	
	public InputStackDescriptor getInputStackDescriptor(){
		return inputStackDescriptor;
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
	    for(int i = 0; i < schematronValidatorHandlers.size(); i++){
	        schematronValidatorHandlers.get(i).processingInstruction(target, data);
	    }
        if(contentHandler != null) contentHandler.processingInstruction(target, data);        
    } 
    
	public void skippedEntity(String name) throws SAXException{
	    for(int i = 0; i < schematronValidatorHandlers.size(); i++){
	        schematronValidatorHandlers.get(i).skippedEntity(name);
	    }
		if(contentHandler != null) contentHandler.skippedEntity(name);		
    }
	
	public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException{
	    for(int i = 0; i < schematronValidatorHandlers.size(); i++){
	        schematronValidatorHandlers.get(i).ignorableWhitespace(ch, start, len);
	    }
        if(contentHandler != null) contentHandler.ignorableWhitespace(ch, start, len);        
    }
    
	public void startDocument()  throws SAXException{		
		errorDispatcher.init();			
		if(optimizedForResourceSharing)initResources();
		    
		
        if(level2AttributeDefaultValue){                        
            AttributeDefaultValueModel attributeDefaultValueModel = schemaModel.getAttributeDefaultValueModel();
            if(attributeDefaultValueModel == null) throw new IllegalStateException("Attempting to use incorrect schema. Feature "+Constants.LEVEL1_ATTRIBUTE_DEFAULT_VALUE_FEATURE+" cannot be supported.");
            if(attributeDefaultValueHandler == null) attributeDefaultValueHandler = new AttributeDefaultValueHandler(attributeDefaultValueModel, errorDispatcher);
        }
        
        if(level1AttributeIdType || level2AttributeIdType){
            AttributeIdTypeModel attributeIdTypeModel = schemaModel.getAttributeIdTypeModel();
            if(attributeIdTypeModel == null) throw new IllegalStateException("Attempting to use incorrect schema. Feature "+Constants.LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE+" cannot be supported.");
            if(attributeIdTypeHandler == null){
                attributeIdTypeHandler = new AttributeIdTypeHandler(attributeIdTypeModel, errorDispatcher);
                attributeIdTypeHandler.setRestrictToFileName(restrictToFileName);
            }
            else attributeIdTypeHandler.init();
        }
        // TODO see about this, according to SAX spec the functioning of the Locator 
        // is not guaranteed here. It seems to work though.
        inputStackDescriptor.pushElement("document root",
            "",
            "document root",
            locator.getSystemId(), 
            locator.getPublicId(), 
            locator.getLineNumber(), 
            locator.getColumnNumber());
		elementHandler = eventHandlerPool.getStartValidationHandler(simplifiedModel.getStartElement());
                        
        defaultNamespace = null;
		prefixNamespaces.clear();
		
		for(int i = 0; i < schematronValidatorHandlers.size(); i++){
		    schematronValidatorHandlers.get(i).startDocument();
		}
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
		for(int i = 0; i < schematronValidatorHandlers.size(); i++){
		    schematronValidatorHandlers.get(i).startPrefixMapping(prefix, uri);
		}
		if(contentHandler != null) contentHandler.startPrefixMapping(prefix, uri);
	}	
	public void endPrefixMapping(String prefix)  throws SAXException{
		documentContext.endPrefixMapping(prefix);
        if(namespacePrefixes){// TODO see if it isn't cheaper to check features before doing this
			if(prefix.equals("")){
				defaultNamespace = null;
			}else{
				prefixNamespaces.remove(prefix);
			}
		}
		for(int i = 0; i < schematronValidatorHandlers.size(); i++){
		    schematronValidatorHandlers.get(i).endPrefixMapping(prefix);
		}
        if(contentHandler != null) contentHandler.endPrefixMapping(prefix);
	}	
	public void setDocumentLocator(Locator locator){
		this.locator = locator;
        if(!documentContext.isBaseURISet())documentContext.setBaseURI(locator.getSystemId());
        for(int i = 0; i < schematronValidatorHandlers.size(); i++){
            schematronValidatorHandlers.get(i).setDocumentLocator(locator);
        }
        if(contentHandler != null) contentHandler.setDocumentLocator(locator);
	}
	public void characters(char[] chars, int start, int length)throws SAXException{
		//TODO make sure this is correct for all circumstances
		String chunk = new String(chars, start, length);	
		characterContentDescriptor.add(chars, start, length, locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber());
		/*inputStackDescriptor.pushCharsContent(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber());*/
		for(int i = 0; i < schematronValidatorHandlers.size(); i++){
		    schematronValidatorHandlers.get(i).characters(chars, start, length);
		}
        if(contentHandler != null) contentHandler.characters(chars, start, length);		
	}
	
	
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{
		/*char[] charContent = charsBuffer.removeCharsArray();
		if(charContent.length > 0){			
			elementHandler.handleInnerCharacters(charContent);		
			inputStackDescriptor.popCharsContent();
		}*/
		if(!characterContentDescriptor.isEmpty()){
		    elementHandler.handleInnerCharacters(characterContentDescriptor, characterContentDescriptorPool);
		    characterContentDescriptor.clear(); 
		}
		
		inputStackDescriptor.pushElement(qName,
            namespaceURI,
            localName,
            locator.getSystemId(), 
            locator.getPublicId(), 
            locator.getLineNumber(), 
            locator.getColumnNumber());
		elementHandler = elementHandler.handleStartElement(qName, namespaceURI, localName, restrictToFileName);
		elementHandler.handleAttributes(attributes, locator);
        
        
		for(int i = 0; i < schematronValidatorHandlers.size(); i++){
		    schematronValidatorHandlers.get(i).startElement(namespaceURI, localName, qName, attributes);
		}
		
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
		/*char[] charContent = charsBuffer.removeCharsArray();
		if(charContent.length == 0)inputStackDescriptor.pushCharsContent(locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber());
		elementHandler.handleLastCharacters(charContent);
        inputStackDescriptor.popCharsContent();*/        
        elementHandler.handleLastCharacters(characterContentDescriptor);
		characterContentDescriptor.clear(); 
		
		elementHandler.handleEndElement(restrictToFileName, locator);
		ElementEventHandler parent = elementHandler.getParentHandler(); 
		elementHandler.recycle();
		elementHandler = parent;		
		inputStackDescriptor.popElement();
        
		for(int i = 0; i < schematronValidatorHandlers.size(); i++){
		    schematronValidatorHandlers.get(i).endElement(namespaceURI, localName, qName);
		}
        if(contentHandler != null) contentHandler.endElement(namespaceURI, localName, qName);
	}
	
	public void endDocument()  throws SAXException {
		elementHandler.handleEndElement(restrictToFileName, locator);
		elementHandler.recycle();
		elementHandler = null;
		inputStackDescriptor.popElement();
				
        if(level1AttributeIdType){
            attributeIdTypeHandler.handleRefs(locator);
        }        
        if(contentHandler != null) contentHandler.endDocument();
        
        //just in case        
		inputStackDescriptor.clear();
		documentContext.reset();
				
		if(optimizedForResourceSharing)releaseResources();
		
		//activeInputDescriptor.printLeftOvers();
		for(int i = 0; i < schematronValidatorHandlers.size(); i++){
		    schematronValidatorHandlers.get(i).endDocument();
		}
		activeInputDescriptor.clear();// shouldn't be necessary, but just in case
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
                    attributeDefaultValueHandler = new AttributeDefaultValueHandler(attributeDefaultValueModel, errorDispatcher);
                }
            }            
        }else if(name.equals(Constants.LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE)){
            if(value){
                if(!level1AttributeIdTypeMemo) throw new SAXNotSupportedException("Schema model configuration cannot support feature, SchemaFactory features for creating needed structures were not set.");
                AttributeIdTypeModel attributeIdTypeModel = schemaModel.getAttributeIdTypeModel();
                if(attributeIdTypeModel == null) throw new SAXNotSupportedException("Schema model configuration cannot support feature, needed schema structures are incorrect.");
                if(attributeIdTypeHandler == null){
                    attributeIdTypeHandler = new AttributeIdTypeHandler(attributeIdTypeModel, errorDispatcher);
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
                    attributeIdTypeHandler = new AttributeIdTypeHandler(attributeIdTypeModel, errorDispatcher);
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
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.DTD_MAPPING_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.ERROR_HANDLER_POOL_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.EVENT_HANDLER_POOL_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.STACK_HANDLER_POOL_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.ACTIVE_INPUT_DESCRIPTOR_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.INPUT_STACK_DESCRIPTOR_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.DOCUMENT_CONTEXT_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.MATCH_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.ATTRIBUTE_DEFAULT_VALUE_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.ATTRIBUTE_ID_TYPE_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
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
        }else if(name.equals(Constants.STACK_HANDLER_POOL_PROPERTY)){
            return stackHandlerPool;
        }else if(name.equals(Constants.INPUT_STACK_DESCRIPTOR_PROPERTY)){
            return inputStackDescriptor;
        }else if(name.equals(Constants.ACTIVE_INPUT_DESCRIPTOR_PROPERTY)){
            return activeInputDescriptor;
        }else if(name.equals(Constants.DOCUMENT_CONTEXT_PROPERTY)){
            return documentContext;
        }else if(name.equals(Constants.MATCH_HANDLER_PROPERTY)){
            return matchHandler;
        }else if(name.equals(Constants.ATTRIBUTE_DEFAULT_VALUE_HANDLER_PROPERTY)){
            if(attributeDefaultValueHandler == null){
                AttributeDefaultValueModel attributeDefaultValueModel = schemaModel.getAttributeDefaultValueModel();
                attributeDefaultValueHandler = new AttributeDefaultValueHandler(attributeDefaultValueModel, errorDispatcher);
            }
            return attributeDefaultValueHandler;
        }else if(name.equals(Constants.ATTRIBUTE_ID_TYPE_HANDLER_PROPERTY)){
            if(attributeIdTypeHandler == null){
                AttributeIdTypeModel attributeIdTypeModel = schemaModel.getAttributeIdTypeModel();
                attributeIdTypeHandler = new AttributeIdTypeHandler(attributeIdTypeModel, errorDispatcher);
            }
            return attributeIdTypeHandler;
        }

        throw new SAXNotRecognizedException(name);
    }
}
