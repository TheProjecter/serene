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

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.xml.XMLConstants;

import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.TypeInfoProvider;

import javax.xml.transform.Templates;

import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.SAXResult;


import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.Attributes;

import org.xml.sax.helpers.AttributesImpl;

import org.w3c.dom.ls.LSResourceResolver;

import org.relaxng.datatype.ValidationContext;

import serene.SchemaModel;

import serene.bind.BoundValidatorHandler; 

import serene.validation.schema.parsed.ParsedComponentBuilder;
import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.parsed.Pattern;
import serene.validation.schema.parsed.Grammar;
import serene.validation.schema.parsed.Definition;

import serene.validation.schema.active.ActiveModel;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.match.MatchHandler;

import serene.validation.handlers.content.ElementEventHandler;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;

import serene.validation.handlers.content.util.ActiveInputDescriptor;
import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptor;
import serene.validation.handlers.content.util.CharacterContentDescriptorPool;

import serene.validation.handlers.error.ValidatorErrorHandlerPool;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.DocumentContext;

import serene.util.SpaceCharsHandler;

import serene.bind.BindingModel;
import serene.bind.BindingPool;
import serene.bind.ElementTask;

import serene.bind.util.Queue;
import serene.bind.util.QueuePool;

import serene.simplifier.IncludedParsedModel;

import serene.dtdcompatibility.DocumentationElementHandler;


import serene.Constants;
import serene.DTDMapping;

class InternalIncludeValidatorHandler extends BoundValidatorHandler{
    static final String QUERY_LANGUAGE = "queryLanguage";
    static final String SCHEMA_LOCAL_NAME = "schema";
    static final String PATTERN_LOCAL_NAME = "pattern";
    static final String RULE_LOCAL_NAME = "rule";
    static final String DIAGNOSTICS_LOCAL_NAME = "diagnostics";
    static final String TITLE_LOCAL_NAME = "title";    
    static final String CDATA_ATTRIBUTE_TYPE = "CDATA";
    static final char[] CONTAINER_PATTERN_ADDED_BY_SERENE = {'C', 'o', 'n', 't', 'a', 'i', 'n', 'e', 'r', ' ', 'p', 'a', 't', 't', 'e', 'r', 'n', ' ', 'a', 'd', 'd', 'e', 'd', ' ', 'b', 'y', ' ', 'S', 'e', 'r', 'e', 'n', 'e', '.'};
    static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
   
    static final String DEFINE_LOCAL_NAME = "define";
    static final String START_LOCAL_NAME = "start";
    static final String NAME_LOCAL_NAME = "name";
    
    boolean isQLBSupported;
    int schematronDepth;
    boolean openedSchematronSchema;
    boolean openedSchematronPattern;
    int definitionDepth;
    boolean isOverriden;
    
    TransformerHandler schematronStartTransformerHandler;    
    SAXResult expandedSchematronResult;
    TransformerHandler schematronCompilerXSLT1;
    TransformerHandler schematronCompilerXSLT2;
    
    TemplatesHandler schematronTemplatesHandler; 
    List<Templates> schematronTemplates;
	
    Map<String, ArrayList<Definition>> overrideDefinitions;
    
    
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
	ActiveInputDescriptor activeInputDescriptor;
	InputStackDescriptor inputStackDescriptor;	
	CharacterContentDescriptor characterContentDescriptor;
	CharacterContentDescriptorPool characterContentDescriptorPool;
	
	DocumentContext documentContext;
	
	ElementEventHandler elementHandler;	
	ActiveModel activeModel;
		
	//int count = 0;
    
    
	RNGParseBindingPool bindingPool;
	RNGParseBindingModel bindingModel;	
	QueuePool queuePool;
	Queue queue;
	//ParsedComponentBuilder builder;
	Pattern topPattern;
	DTDMapping dtdMapping;
	
    boolean level1DocumentationElement;
    boolean restrictToFileName;
    boolean optimizedForResourceSharing;
    boolean processEmbededSchematron;
    
    DocumentationElementHandler documentationElementHandler;
    
    Map<String, String> declaredXmlns;	
    
    
	InternalIncludeValidatorHandler(ValidatorEventHandlerPool eventHandlerPool,
							ValidatorErrorHandlerPool errorHandlerPool,
							SchemaModel schemaModel,
                            RNGParseBindingPool bindingPool,
                            boolean level1DocumentationElement,
                            boolean restrictToFileName,
                            boolean optimizedForResourceSharing,
                            boolean processEmbededSchematron){		
		this.eventHandlerPool = eventHandlerPool;	
		this.errorHandlerPool = errorHandlerPool;
		
		this.schemaModel = schemaModel;
							
		
        this.level1DocumentationElement = level1DocumentationElement;
        this.restrictToFileName = restrictToFileName;
        this.optimizedForResourceSharing = optimizedForResourceSharing;
                
        this.bindingPool = bindingPool;
        this.processEmbededSchematron = processEmbededSchematron;
        
        matchHandler  = new MatchHandler();
		spaceHandler = new SpaceCharsHandler();					
        documentContext = new DocumentContext();
        
        
		errorDispatcher = new ErrorDispatcher();		
		activeInputDescriptor = new ActiveInputDescriptor();
		inputStackDescriptor = new InputStackDescriptor(activeInputDescriptor);
		characterContentDescriptorPool = new CharacterContentDescriptorPool(activeInputDescriptor, spaceHandler);
		characterContentDescriptor = characterContentDescriptorPool.getCharacterContentDescriptor();
			
        
        queuePool = new QueuePool(activeInputDescriptor);
        queue = queuePool.getQueue();
        // TODO move
        
        errorHandlerPool.init(errorDispatcher, activeInputDescriptor);        
        eventHandlerPool.init(spaceHandler, matchHandler, activeInputDescriptor, inputStackDescriptor, documentContext, errorHandlerPool);
        		
		if(!optimizedForResourceSharing)initResources();
	}
	
	void initResources(){
	    errorHandlerPool.fill();
        eventHandlerPool.fill();
                	
        bindingModel = bindingPool.getBindingModel();
        
        activeModel = schemaModel.getActiveModel(activeInputDescriptor,
		                                            inputStackDescriptor, 
													errorDispatcher);
		if(activeModel == null) throw new IllegalStateException("Attempting to use an erroneous schema.");
        matchHandler.setActiveModel(activeModel);
        
        /*bindingModel.index(activeModel.getSElementIndexMap(), activeModel.getSAttributeIndexMap());*/
        /*queuePool.index(activeModel.getSAttributeIndexMap());*/        
	}
	void releaseResources(){
	    eventHandlerPool.releaseHandlers();
		errorHandlerPool.releaseHandlers();
		
		activeModel.recycle();
		activeModel = null;
		
		bindingModel.recycle();
        bindingModel = null;
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
	
	public void setContentHandler(ContentHandler ch){
		this.contentHandler = ch;
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
	    if(processEmbededSchematron && isQLBSupported && !isOverriden && schematronDepth > 0)schematronStartTransformerHandler.processingInstruction(target, data);
	} 
	public void skippedEntity(String name) throws SAXException{
	    if(processEmbededSchematron && isQLBSupported  && !isOverriden && schematronDepth > 0)schematronStartTransformerHandler.skippedEntity(name);
	}	
	public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException{
	    if(processEmbededSchematron && isQLBSupported  && !isOverriden && schematronDepth > 0)schematronStartTransformerHandler.ignorableWhitespace(ch, start, len);
	}
	
	public void startBinding(){}
	
	public void startDocument() throws SAXException{	    
		errorDispatcher.init();			
		if(optimizedForResourceSharing)initResources();		
		
		startBinding();
		
		// TODO see about this, according to SAX spec the functioning of the Locator 
        // is not guaranteed here. It seems to work though.
        inputStackDescriptor.pushElement("document root",
            "",
            "document root",
            locator.getSystemId(), 
            locator.getPublicId(), 
            locator.getLineNumber(), 
            locator.getColumnNumber());
        
		elementHandler = eventHandlerPool.getBoundStartValidationHandler(activeModel.getStartElement(), bindingModel, queue, queuePool);
		
        if(level1DocumentationElement){
            if(documentationElementHandler == null) documentationElementHandler = new DocumentationElementHandler(errorDispatcher);
            else documentationElementHandler.init();
        }
		//Note that locator is only garanteed to pass correct information AFTER
		//startDocument. The base URI of the document is also passed independently 
		//to the Simplifier. This needs reviewing.
		
        if(processEmbededSchematron){            
            schematronDepth = 0;
            isQLBSupported = true;
            openedSchematronSchema = false;
            definitionDepth = 0;
            isOverriden = false;
        }
	}	
	
	
	public void setDocumentLocator(Locator locator){
		this.locator = locator;
        if(!documentContext.isBaseURISet())documentContext.setBaseURI(locator.getSystemId());
    }
	public void characters(char[] chars, int start, int length)throws SAXException{
		characterContentDescriptor.add(chars, start, length, locator.getSystemId(), locator.getPublicId(), locator.getLineNumber(), locator.getColumnNumber());
        if(processEmbededSchematron && isQLBSupported  && !isOverriden && schematronDepth > 0)schematronStartTransformerHandler.characters(chars, start, length);		
	}
	public void startPrefixMapping(String prefix, String uri) throws SAXException{
		if(declaredXmlns == null)declaredXmlns = new HashMap<String, String>();
		declaredXmlns.put(prefix, uri);
		
		documentContext.startPrefixMapping(prefix, uri);
		
		if(processEmbededSchematron && isQLBSupported  && !isOverriden && schematronDepth > 0)schematronStartTransformerHandler.startPrefixMapping(prefix, uri);
	}
	public void endPrefixMapping(String prefix) throws SAXException{
		documentContext.endPrefixMapping(prefix);
		
		if(processEmbededSchematron && isQLBSupported  && !isOverriden && schematronDepth > 0)schematronStartTransformerHandler.endPrefixMapping(prefix);
	}	
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{		
		if(!characterContentDescriptor.isEmpty()){
		    elementHandler.handleInnerCharacters(characterContentDescriptor, characterContentDescriptorPool);
		    characterContentDescriptor.clear(); 
		}
		
		if(declaredXmlns != null){
		    inputStackDescriptor.pushElement(declaredXmlns,
                                        qName,
                                        namespaceURI,
                                        localName,
                                        locator.getSystemId(), 
                                        locator.getPublicId(), 
                                        locator.getLineNumber(), 
                                        locator.getColumnNumber());
            declaredXmlns = null;
        }else{
            inputStackDescriptor.pushElement(qName,
                                        namespaceURI,
                                        localName,
                                        locator.getSystemId(), 
                                        locator.getPublicId(), 
                                        locator.getLineNumber(), 
                                        locator.getColumnNumber());
        }
		
		elementHandler = elementHandler.handleStartElement(qName, namespaceURI, localName, restrictToFileName);		
		
		elementHandler.handleAttributes(attributes, locator);
        
        if(level1DocumentationElement){
            documentationElementHandler.startElement(namespaceURI, localName, qName, attributes, locator);
        }
        
        if(processEmbededSchematron){
            if(definitionDepth == 0){
                if(namespaceURI.equals(XMLConstants.RELAXNG_NS_URI))handleDefinitionOverrideForSchematron(localName, attributes);
            }else{
                definitionDepth++;
            }
        }
        if(processEmbededSchematron && isQLBSupported  && !isOverriden && (schematronDepth > 0 | namespaceURI.equals(Constants.SCHEMATRON_NS_URI)))
            startSchematronElement(namespaceURI, localName, qName, attributes);
		
	}
	void handleDefinitionOverrideForSchematron(String localName, Attributes attributes){
	    String name = null;
	    if(localName.equals(DEFINE_LOCAL_NAME)){
	        name = attributes.getValue(XMLConstants.NULL_NS_URI, NAME_LOCAL_NAME);
	        if(name == null) name = "*";
	    }else if(!localName.equals(START_LOCAL_NAME)){	        
	        return;
	    }
	    
	    if(overrideDefinitions.containsKey(name)){
	        isOverriden = true;	        
	    }
	    definitionDepth++;
	}
	void startSchematronElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{
        if(schematronDepth == 0){
            if(localName.equals(SCHEMA_LOCAL_NAME)){
                if(openedSchematronPattern){
                    closeSchematronPattern();
                    closeSchematronSchema();
                    endSchematronSchema();                    
                }else if(openedSchematronSchema){
                    closeSchematronSchema();
                    endSchematronSchema();
                }
                
                
                startSchematronSchema(attributes.getValue(XMLConstants.NULL_NS_URI, QUERY_LANGUAGE));
                if(isQLBSupported && !isOverriden) schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
            }else if(localName.equals(PATTERN_LOCAL_NAME)){
                if(openedSchematronPattern){
                    closeSchematronPattern();
                }else if(!openedSchematronSchema){
                    startSchematronSchema();
                    openSchematronSchema();
                }
                schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);                
            }else if(localName.equals(RULE_LOCAL_NAME)){
                if(!openedSchematronSchema){
                    startSchematronSchema();
                    openSchematronSchema();
                    openSchematronPattern();
                }else if(!openedSchematronPattern){
                    openSchematronPattern();
                }
                schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
            }else if(localName.equals(DIAGNOSTICS_LOCAL_NAME)){
                if(!openedSchematronSchema){
                    isOverriden = true;
                    return;
                }
                if(openedSchematronPattern)closeSchematronPattern();                
                schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
            }else{
                errorDispatcher.error(new SAXParseException("Unsupported Schematron element. Embeding of Schematron element <"+localName+"> here is not suported by Serene. Use <schema>, <pattern>, <rule>, or <diagnostics>.", locator));
            }
        }else{            
            schematronStartTransformerHandler.startElement(namespaceURI, localName, qName, attributes);
        }	
        schematronDepth++;
	}
	void startSchematronSchema(String qlb) throws SAXException{
	    if(qlb == null || qlb.equals(Constants.SCHEMATRON_QLB_XSLT1)){
            expandedSchematronResult.setHandler(schematronCompilerXSLT1);
        }else if(qlb.equals(Constants.SCHEMATRON_QLB_XSLT2)){
            expandedSchematronResult.setHandler(schematronCompilerXSLT2);
        }else{
            isQLBSupported = false;
            errorDispatcher.error(new SAXParseException("Unsupported Schematron query language. Serene supports \"xslt\" and \"xslt2\".", locator));
        }
	    
	    schematronStartTransformerHandler.setDocumentLocator(locator);
	    schematronStartTransformerHandler.startDocument();
	    documentContext.transferDTDMappings((DTDHandler)schematronStartTransformerHandler);
	    documentContext.transferPrefixMappings(schematronStartTransformerHandler);
	}
	void startSchematronSchema() throws SAXException{
        expandedSchematronResult.setHandler(schematronCompilerXSLT1);
	    
	    schematronStartTransformerHandler.setDocumentLocator(locator);
	    schematronStartTransformerHandler.startDocument();
	    documentContext.transferDTDMappings((DTDHandler)schematronStartTransformerHandler);
	    documentContext.transferPrefixMappings(schematronStartTransformerHandler);
	}
	void openSchematronSchema() throws SAXException{        
        openedSchematronSchema = true;  
        
        schematronStartTransformerHandler.startElement(Constants.SCHEMATRON_NS_URI, SCHEMA_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, SCHEMA_LOCAL_NAME), EMPTY_ATTRIBUTES);
	}
	void closeSchematronSchema() throws SAXException{
	    openedSchematronSchema = false;   
	    schematronStartTransformerHandler.endElement(Constants.SCHEMATRON_NS_URI, SCHEMA_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, SCHEMA_LOCAL_NAME));
	}
	void openSchematronPattern()  throws SAXException{
        openedSchematronPattern = true;   
        
        schematronStartTransformerHandler.startElement(Constants.SCHEMATRON_NS_URI, PATTERN_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, PATTERN_LOCAL_NAME), EMPTY_ATTRIBUTES);
        
        schematronStartTransformerHandler.startElement(Constants.SCHEMATRON_NS_URI, TITLE_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, TITLE_LOCAL_NAME), EMPTY_ATTRIBUTES);        
        schematronStartTransformerHandler.characters(CONTAINER_PATTERN_ADDED_BY_SERENE, 0, CONTAINER_PATTERN_ADDED_BY_SERENE.length);
        schematronStartTransformerHandler.endElement(Constants.SCHEMATRON_NS_URI, TITLE_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, TITLE_LOCAL_NAME));
	}	
	void closeSchematronPattern() throws SAXException{
	    openedSchematronPattern = false;
	    schematronStartTransformerHandler.endElement(Constants.SCHEMATRON_NS_URI, PATTERN_LOCAL_NAME, getQName(Constants.SCHEMATRON_NS_URI, PATTERN_LOCAL_NAME));
	}
	String getQName(String nsURI, String localName){
	    String prefix = documentContext.getPrefix(nsURI);
	    if(prefix != null && !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
	        return prefix+":"+localName;
	    return localName;
	}
	
	void endSchematronSchema() throws SAXException{
	    schematronStartTransformerHandler.endDocument();
	    
	    Templates currentTemplates = schematronTemplatesHandler.getTemplates();
	    if(currentTemplates != null) schematronTemplates.add(currentTemplates);
	}
	
	public void endElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{ 
		
        elementHandler.handleLastCharacters(characterContentDescriptor);
		characterContentDescriptor.clear();
		
		elementHandler.handleEndElement(restrictToFileName, locator);	
		ElementEventHandler parent = elementHandler.getParentHandler(); 
		elementHandler.recycle();
		elementHandler = parent;		
        if(level1DocumentationElement){
            documentationElementHandler.endElement(namespaceURI, localName, qName, locator);
        }		
        inputStackDescriptor.popElement();
        
        
        if(processEmbededSchematron && isQLBSupported  && !isOverriden && (schematronDepth > 0 | namespaceURI.equals(Constants.SCHEMATRON_NS_URI)))
            endSchematronElement(namespaceURI, localName, qName);
        if(processEmbededSchematron){
            if(definitionDepth > 0){
                if(--definitionDepth == 0)isOverriden = false;
            }
        }
	}
	
	void endSchematronElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{
	    schematronDepth--;	    
        schematronStartTransformerHandler.endElement(namespaceURI, localName, qName);
        if(!openedSchematronSchema && schematronDepth == 0)endSchematronSchema();
	}
	
	
	public void endDocument() throws SAXException{
		elementHandler.handleEndElement(restrictToFileName, locator);
		elementHandler.recycle();
		elementHandler = null;
		inputStackDescriptor.popElement();
				
		queue.executeAll();
		queue.clear();
		
		endBinding();
				
		//just in case		
		inputStackDescriptor.clear();	
		documentContext.reset();
		
		if(optimizedForResourceSharing)releaseResources();
		
		//activeInputDescriptor.printLeftOvers();
		activeInputDescriptor.clear();// shouldn't be necessary, but just in case
		
		if(processEmbededSchematron){
		    if(openedSchematronPattern) closeSchematronPattern();
		    if(openedSchematronSchema){
		        closeSchematronSchema();
		        endSchematronSchema();
		    }
		}		
	}

	public void endBinding(){
	    // TODO xmlBase - maybe the documentEndTask
	    ParsedComponentBuilder builder = bindingModel.getParsedComponentBuilder();
	    topPattern = (Pattern)builder.getCurrentParsedComponent();
	    dtdMapping = documentContext.getDTDMapping();
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
        }else if(name.equals(Constants.ACTIVE_INPUT_DESCRIPTOR_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.INPUT_STACK_DESCRIPTOR_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.MATCH_HANDLER_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.PARSED_MODEL_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.INCLUDED_PARSED_MODEL_PROPERTY)){
            // recognized but not set, only for retrieval
            throw new SAXNotSupportedException();
        }else if(name.equals(Constants.SCHEMATRON_COMPILER_FOR_XSLT1_PROPERTY)){
            if(object == null)throw new NullPointerException();
            else if(!(object instanceof TransformerHandler)) throw new SAXNotSupportedException();
            schematronCompilerXSLT1 = (TransformerHandler)object;
        }else if(name.equals(Constants.SCHEMATRON_COMPILER_FOR_XSLT2_PROPERTY)){
            if(object == null)throw new NullPointerException();
            else if(!(object instanceof TransformerHandler)) throw new SAXNotSupportedException();
            schematronCompilerXSLT2 = (TransformerHandler)object;
        }else if(name.equals(Constants.SCHEMATRON_EXPANDED_SCHEMA_RESULT_PROPERTY)){
            if(object == null)throw new NullPointerException();
            else if(!(object instanceof SAXResult)) throw new SAXNotSupportedException();
            expandedSchematronResult = (SAXResult)object;
        }else if(name.equals(Constants.SCHEMATRON_TEMPLATES_HANDLER_PROPERTY)){
            if(object == null)throw new NullPointerException();
            else if(!(object instanceof TemplatesHandler)) throw new SAXNotSupportedException();            
            schematronTemplatesHandler = (TemplatesHandler)object;
        }else if(name.equals(Constants.SCHEMATRON_TEMPLATES_PROPERTY)){
            if(object == null)throw new NullPointerException();
            else if(!(object instanceof List)) throw new SAXNotSupportedException();            
            schematronTemplates = (List<Templates>)object;
        }else if(name.equals(Constants.SCHEMATRON_START_TRANSFORMER_HANDLER_PROPERTY)){
            if(object == null)throw new NullPointerException();
            else if(!(object instanceof TransformerHandler)) throw new SAXNotSupportedException();            
            schematronStartTransformerHandler = (TransformerHandler)object;
        }else if(name.equals(Constants.OVERRIDE_DEFINITIONS_PROPERTY)){
            if(object == null)throw new NullPointerException();
            else if(!(object instanceof Map)) throw new SAXNotSupportedException();            
            overrideDefinitions = (Map<String, ArrayList<Definition>>)object;
        }else{
            throw new SAXNotRecognizedException(name);
        }
        
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
        }else if(name.equals(Constants.ACTIVE_INPUT_DESCRIPTOR_PROPERTY)){
            return activeInputDescriptor;
        }else if(name.equals(Constants.INPUT_STACK_DESCRIPTOR_PROPERTY)){
            return inputStackDescriptor;
        }else if(name.equals(Constants.MATCH_HANDLER_PROPERTY)){
            return matchHandler;
        }else if(name.equals(Constants.PARSED_MODEL_PROPERTY)){
            if(topPattern == null) return null;
            return new ParsedModel(dtdMapping, topPattern);
        }else if(name.equals(Constants.INCLUDED_PARSED_MODEL_PROPERTY)){
            if(topPattern == null) return null;
            Grammar g = null;
            try{
                g = (Grammar)topPattern;
            }catch(ClassCastException cce){
                // TODO
                // the included document was not valid, 
                // you should have aborted earlier
                // see about that
                // SEEN , but needs review
                return null;
            }
            return new IncludedParsedModel(dtdMapping, g);
        }else if(name.equals(Constants.SCHEMATRON_COMPILER_FOR_XSLT1_PROPERTY)){
            return schematronCompilerXSLT1;
        }else if(name.equals(Constants.SCHEMATRON_COMPILER_FOR_XSLT2_PROPERTY)){
            return schematronCompilerXSLT2;
        }else if(name.equals(Constants.SCHEMATRON_EXPANDED_SCHEMA_RESULT_PROPERTY)){
            return expandedSchematronResult;
        }else if(name.equals(Constants.SCHEMATRON_TEMPLATES_PROPERTY)){         
            return schematronTemplates;
        }else if(name.equals(Constants.SCHEMATRON_TEMPLATES_HANDLER_PROPERTY)){            
            return schematronTemplatesHandler;
        }else if(name.equals(Constants.SCHEMATRON_START_TRANSFORMER_HANDLER_PROPERTY)){
            return schematronStartTransformerHandler;
        }else if(name.equals(Constants.OVERRIDE_DEFINITIONS_PROPERTY)){
            return overrideDefinitions;
        }

        throw new SAXNotRecognizedException(name);
    }

    public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.LEVEL1_DOCUMENTATION_ELEMENT_FEATURE)){
            level1DocumentationElement = value;
        }else if(name.equals(Constants.RESTRICT_TO_FILE_NAME_FEATURE)){
            restrictToFileName = value;                        
        }else if(name.equals(Constants.PROCESS_EMBEDED_SCHEMATRON_FEATURE)){
            processEmbededSchematron = value;                        
        }else{
            throw new SAXNotRecognizedException(name);
        }
    }
	
	public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.LEVEL1_DOCUMENTATION_ELEMENT_FEATURE)){
            return level1DocumentationElement;
        }else if(name.equals(Constants.RESTRICT_TO_FILE_NAME_FEATURE)){
            return restrictToFileName;                        
        }else if(name.equals(Constants.PROCESS_EMBEDED_SCHEMATRON_FEATURE)){
            return processEmbededSchematron;                        
        }else{
            throw new SAXNotRecognizedException(name);
        }
    }	
}
