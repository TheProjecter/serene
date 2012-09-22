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
import java.util.ArrayList;

import javax.xml.XMLConstants;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.DatatypeLibraryFinder;

import serene.validation.schema.simplified.ReferenceModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SimplifiedComponent;
//import serene.validation.schema.simplified.NameClass;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SRef;

import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SimplifiedComponentBuilder;
import serene.validation.schema.simplified.SimplifiedModel;


import serene.bind.AttributeTaskFactory;
import serene.bind.ElementTaskFactory;

import serene.util.ObjectIntHashMap;

import serene.Constants;


class RNGDirector{
    //********************
    // ref elementIndexing
    //********************
    final int PATTERN = 0;
    //SPattern patternDefinitionTopPattern;
    
    final int PARAM = 1;
    //SPattern paramDefinitionTopPattern;
    
    final int EXCEPT_PATTERN = 2;
    //SPattern exceptPatternDefinitionTopPattern;
    
    final int GRAMMAR_CONTENT = 3;
    //SPattern grammarContentDefinitionTopPattern;
    
    final int INCLUDE_CONTENT = 4;
    //SPattern includeContentDefinitionTopPattern;
    
    final int START = 5;
    //SPattern startDefinitionTopPattern;
    
    final int DEFINE = 6;
    //SPattern defineDefinitionTopPattern;
    
    final int NAME_CLASS = 7;
    //SPattern nameClassDefinitionTopPattern;
    
    final int EXCEPT_NAME_CLASS = 8;
    //SPattern exceptNameClassDefinitionTopPattern;
    
    final int NS_ATTRIBUTE = 9;
    //SPattern nsAttributeDefinitionTopPattern;
    
    final int DL_ATTRIBUTE = 10;
    //SPattern dlAttributeDefinitionTopPattern;
    
    final int FOREIGN_ATTRIBUTES = 11;
    //SPattern foreignAttributesDefinitionTopPattern;
    
    final int FOREIGN_ELEMENT = 12;
    //SPattern foreignElementDefinitionTopPattern;
    
    final int ANY_ELEMENT = 13;
    //SPattern anyElementDefinitionTopPattern;
    
    final int ANY_ATTRIBUTE = 14;
    //SPattern anyAttributeDefinitionTopPattern;
    
    final int DEFINITION_TOP_PATTERN_COUNT = 15;
    
    
	HashMap<SElement, Object> needsStartTask;
	HashMap<SElement, RNGParseElementTaskFactory> endElementTaskFactory;
	HashMap<SAttribute, RNGParseAttributeTaskFactory> attributeTaskFactory;
	
	
	SimplifiedComponentBuilder builder;
	SPattern rngStartTopPattern;
	SPattern includeStartTopPattern;
	SPattern externalRefStartTopPattern;
	SPattern[] refDefinitionTopPattern;
	
	ArrayList<SElement> selements;
	int elementIndex;
	
	ArrayList<SAttribute> sattributes;
	int attributeIndex;
	
	/*
	private SAttribute ns;
	private SAttribute datatypeLibrary;
	private SAttribute qName;
    private SAttribute ncName;
	private SAttribute combine;
	private SAttribute type;
	private SAttribute href;  
    private SAttribute any;
    private SAttribute foreign;  */
    
	StartLevelTaskFactory startLevelTaskFactory;
    NsTaskFactory nsTaskFactory;
    DatatypeLibraryTaskFactory datatypeLibraryTaskFactory;
    NameAttributeTaskFactory nameAttributeTaskFactory;
    TypeTaskFactory typeTaskFactory;
    CombineTaskFactory combineTaskFactory;
    HrefTaskFactory hrefTaskFactory;
    ForeignAttributeTaskFactory foreignAttributeTaskFactory;
	
    
	DatatypeLibrary internalLibrary;
    DatatypeLibrary nativeLibrary;
	
    boolean needsSynchronizedBindingPool;
    
    InternalIndexedData internalIndexedData;
	
    ReferenceModel referenceModel;
    RecursionModel recursionModel;
	RNGDirector(boolean needsSynchronizedBindingPool){
		this.needsSynchronizedBindingPool = needsSynchronizedBindingPool;
                
        SecuritySupport ss = new SecuritySupport();		
		ClassLoader cl;        
        cl = ss.getContextClassLoader();
        
        if (cl == null) {
            //cl = ClassLoader.getSystemClassLoader();
            //use the current class loader
            cl = RNGDirector.class.getClassLoader();
        } 

        DatatypeLibraryFactory datatypeLibraryFactory = new DatatypeLibraryFinder(cl);
        
        internalLibrary = datatypeLibraryFactory.createDatatypeLibrary(Constants.INTERNAL_DATATYPE_LIBRARY);
        nativeLibrary = datatypeLibraryFactory.createDatatypeLibrary("");      
        	    
        internalIndexedData = new InternalIndexedData();
        
        
        selements = new ArrayList<SElement>();
        sattributes = new ArrayList<SAttribute>();   
	}	
	
	void createModels(SimplifiedComponentBuilder builder)  throws DatatypeException{
	    this.builder = builder;
	    
		selements.clear();
		elementIndex = 0;
	
		sattributes.clear();
		attributeIndex = 0;
	
		refDefinitionTopPattern = new SPattern[DEFINITION_TOP_PATTERN_COUNT];
		needsStartTask = new HashMap<SElement, Object>();
		endElementTaskFactory = new HashMap<SElement, RNGParseElementTaskFactory>();
		attributeTaskFactory = new HashMap<SAttribute, RNGParseAttributeTaskFactory>();
		
		startLevelTaskFactory = new StartLevelTaskFactory();
		nsTaskFactory = new NsTaskFactory();
		datatypeLibraryTaskFactory = new DatatypeLibraryTaskFactory();
		nameAttributeTaskFactory = new NameAttributeTaskFactory();
		typeTaskFactory = new TypeTaskFactory();
		combineTaskFactory = new CombineTaskFactory();
		hrefTaskFactory = new HrefTaskFactory();
		foreignAttributeTaskFactory = new ForeignAttributeTaskFactory();
        
		
        referenceModel = new ReferenceModel();     
        recursionModel = new RecursionModel();
						
        
		defineAnyAttribute();
		defineAnyElement();
		defineForeignAttributes();
		defineForeignElement();
		
		defineOptionalNsAttribute();
		defineOptionalDlAttribute();
		
		defineExceptNameClass();
		defineNameClass();
				
		defineParam();
		definePattern();				
		defineExceptPattern();
		defineStart();
		defineDefine();
		defineGrammarContent();
		defineIncludeContent();
		
		startGrammar();
		
		referenceModel.init(refDefinitionTopPattern);
	}
	
	SimplifiedModel getRNGModel(){
		SPattern[] start = {rngStartTopPattern};
        
		SElement startElement = new SElement(elementIndex, null, rngStartTopPattern, -1, null, recursionModel);
        selements.add(startElement);        
		SimplifiedModel s = new SimplifiedModel(elementIndex,
									selements.toArray(new SElement[selements.size()]),
									sattributes.toArray(new SAttribute[sattributes.size()]),
									null,
									start,
									referenceModel,
									recursionModel);
		elementIndex++;
        return s;
	}
	
	SimplifiedModel getIncludeModel(){
		SPattern[] start = {includeStartTopPattern};
        
		SElement startElement = new SElement(elementIndex, null, includeStartTopPattern, -1, null, recursionModel);
        selements.add(startElement);     
		SimplifiedModel s = new SimplifiedModel(elementIndex,
									selements.toArray(new SElement[selements.size()]),
									sattributes.toArray(new SAttribute[sattributes.size()]),
									null,
									start,
									referenceModel,
									recursionModel);
		elementIndex++;
		return s;
	}
	
	SimplifiedModel getExternalRefModel(){
		SPattern[] start = {externalRefStartTopPattern};
        
		SElement startElement = new SElement(elementIndex, null, externalRefStartTopPattern, -1, null, recursionModel);
        selements.add(startElement);    
		SimplifiedModel s = new SimplifiedModel(elementIndex,
									selements.toArray(new SElement[selements.size()]),
									sattributes.toArray(new SAttribute[sattributes.size()]),
									null,
									start,
									referenceModel,
									recursionModel);
		elementIndex++;
		return s;
	}
	
	RNGParseBindingPool getBindingModelPool(){
	    DummyTaskFactory dummyTaskFactory = new DummyTaskFactory();
	    DocumentStartTaskFactory startDocumentTaskFactory = new DocumentStartTaskFactory();
	    	    
	    if(needsSynchronizedBindingPool){
	        return new SynchronizedRNGParseBindingPool(startDocumentTaskFactory, 
	                                                    //null,
	                                                    
	                                                    startLevelTaskFactory, 
	                                                    needsStartTask,
	                                                    endElementTaskFactory, 
	                                                    attributeTaskFactory,
	                                                                                                       
	                                                    startLevelTaskFactory, 
	                                                    dummyTaskFactory, 
	                                                    null);
	    }
	    return new UnsynchronizedRNGParseBindingPool(startDocumentTaskFactory, 
	                                                    //null,
	                                                    
	                                                    startLevelTaskFactory, 
	                                                    needsStartTask,
	                                                    endElementTaskFactory, 
	                                                    attributeTaskFactory,
	                                                                                                       
	                                                    startLevelTaskFactory, 
	                                                    dummyTaskFactory, 
	                                                    null);
	}
	
	private void startGrammar(){
		builder.startBuild();	
		SRef r = builder.buildRef(refDefinitionTopPattern[PATTERN], PATTERN, referenceModel, InternalIndexedData.REF_PATTERN, internalIndexedData);			
		
		rngStartTopPattern = r;
		externalRefStartTopPattern = r;		
	}
	
	private void definePattern() throws DatatypeException{
		builder.startBuild();	
		builder.startLevel();{
			elementWithNameClass();			
			elementWithNameInstance();
			attributeWithNameClass();
			attributeWithNameInstance();
			group();
			interleave();
			choicePattern();
			optional();
			zeroOrMore();
			oneOrMore();
			list();
			mixed();
			ref();
			parentRef();
			empty();
			text();
			value();
			data();			
			notAllowed();			
			externalRef();			
			grammar();
		}builder.endLevel();
		refDefinitionTopPattern[PATTERN] = builder.buildChoicePattern(InternalIndexedData.DEFINE_PATTERN, 
		                        internalIndexedData, 
		                        false);	
	}	
	
	
	//**************************************************************************
	//START PATTERN METHODS ****************************************************
	//**************************************************************************
	
		
	
    //<element name="QName">pattern+</element>
    //**************************************************************************
    private void elementWithNameInstance() throws DatatypeException{
        builder.startLevel();{			
            builder.startLevel();{				
                builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_PATTERN, internalIndexedData /*"pattern","RELAXNG Specification 3.Full Syntax: element with name attribute"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);		
                
                nameQNameAttributeForElement();
                
                builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                
            }builder.endLevel();
            builder.buildGroup(InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
            builder.buildName(XMLConstants.RELAXNG_NS_URI, "element", InternalIndexedData.ELEMENT_NI_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
        }builder.endLevel();
        builder.buildElement(elementIndex++, InternalIndexedData.ELEMENT_NI_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
        
        SElement e = (SElement)builder.getCurrentPattern();
        needsStartTask.put(e, null);
        endElementTaskFactory.put(e, new ElementWithNameInstanceTaskFactory());
        
        selements.add(e);
    }
    private void nameQNameAttributeForElement()  throws DatatypeException{
        builder.startLevel();{
            builder.buildName("","name", InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: name attribute with QName value"*/);
            builder.buildData(internalLibrary.createDatatype("QName"),InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name attribute with QName value"*/);
        }builder.endLevel();
        builder.buildAttribute(attributeIndex++,
                                InternalIndexedData.NO_RECORD,
                                InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE, 
                                internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with QName value"*/);
        
        SAttribute qName = (SAttribute)builder.getCurrentPattern();
        attributeTaskFactory.put(qName, nameAttributeTaskFactory);

        sattributes.add(qName);
    }
    //**************************************************************************
    
   
	//<element>nameClass pattern+</element>
	//**************************************************************************
	private void elementWithNameClass() throws DatatypeException{		
		builder.startLevel();{			
			builder.startLevel();{				
				nameClassPatternPlus();
								
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "element", InternalIndexedData.ELEMENT_NC_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
		}builder.endLevel();		
		builder.buildElement(elementIndex++, InternalIndexedData.ELEMENT_NC_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ElementWithNameClassTaskFactory());
		
        selements.add(e);
	}	
	private void nameClassPatternPlus(){		
		builder.startLevel();{
			builder.startLevel();{
			builder.buildRef(refDefinitionTopPattern[NAME_CLASS], NAME_CLASS, referenceModel, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS, internalIndexedData  /*"nameClass","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
				builder.startLevel();{
					recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_PATTERN, internalIndexedData  /*"pattern","RELAXNG Specification 3.Full Syntax: element with name class child"*/));						
				}builder.endLevel();
				builder.oneOrMore(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: element with name class child"*/);				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP, 
		                        internalIndexedData, 
		                        false   /*"group of name class and pattern elements","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
			
			builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData  /*"interleaving of name class and pattern elements group with foreign elements","RELAXNG Specification 3.Full Syntax: element with name class child"*/);		
	}
	//**************************************************************************
	
	
    //<attribute>nameClass pattern</attribute>
	private void attributeWithNameClass() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameClassPatternSquare();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
								
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /* "attributes and elements group","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "attribute", InternalIndexedData.ATTRIBUTE_NC_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
		}builder.endLevel();		
		builder.buildElement(elementIndex++, InternalIndexedData.ATTRIBUTE_NC_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new AttributeWithNameClassTaskFactory());
		
        selements.add(e);
	}
	private void nameClassPatternSquare(){
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(refDefinitionTopPattern[NAME_CLASS], NAME_CLASS, referenceModel, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS, internalIndexedData    /*"nameClass","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
				builder.startLevel();{
					recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/));						
				}builder.endLevel();
				builder.optional(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN_SQUARE, internalIndexedData  /*"optional","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP, 
		                        internalIndexedData, 
		                        false   /*"group of name class and pattern elements","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
			
			builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData  /*"interleaving of name class and pattern elements group with foreign elements","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);		
	}
	
	
	//<attribute name="QName">pattern</attribute>
	private void attributeWithNameInstance() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{				
				nameQNameAttributeForAttribute();
				patternSquare();	
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "attribute", InternalIndexedData.ATTRIBUTE_NI_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ATTRIBUTE_NI_ELEMENT, internalIndexedData, recursionModel    /*"element","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new AttributeWithNameInstanceTaskFactory());
		
        selements.add(e);
	}
	private void nameQNameAttributeForAttribute()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","name", InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: name attribute with QName value"*/);
			builder.buildData(internalLibrary.createDatatype("QName"), InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData     /*"data","RELAXNG Specification 3.Full Syntax: name attribute with QName value"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, 
		                        InternalIndexedData.NO_RECORD,
		                        InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE, 
                                internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with QName value"*/);
        
		SAttribute qName = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(qName, nameAttributeTaskFactory);

        sattributes.add(qName);
    }
    private void patternSquare(){				
		builder.startLevel();{
			builder.startLevel();{
				recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN, internalIndexedData  /*"pattern","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/));
			}builder.endLevel();
			builder.optional(InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN_SQUARE, internalIndexedData  /*"optional","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
			
			builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData  /*"interleaving of pattern element and foreign elements","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);	
	}
    
    
    //<group>pattern+</group>
	private void group() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.GROUP_ELEMENT_CONTENT_PATTERN, internalIndexedData /*"pattern","RELAXNG Specification 3.Full Syntax: group"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.GROUP_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData /*"oneOrMore","RELAXNG Specification 3.Full Syntax: group"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: group"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: group"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData  /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: group"*/);		
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.GROUP_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: group"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.GROUP_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: group"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.GROUP_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: group"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.GROUP_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: group"*/);			
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "group", InternalIndexedData.GROUP_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: group"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.GROUP_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: group"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new GroupTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<interleave>pattern+</interleave>
	private void interleave() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN,  referenceModel, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: interleave"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: interleave"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: interleave"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: interleave"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);		
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: interleave"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: interleave"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: interleave"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: interleave"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "interleave", InternalIndexedData.INTERLEAVE_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: interleave"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.INTERLEAVE_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: interleave"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new InterleaveTaskFactory());
		
        selements.add(e);
    }
	
	
	
	//<choice>pattern+</choice>
	private void choicePattern() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN, internalIndexedData  /*"pattern","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData  /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);		
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT,
		                        internalIndexedData, 
		                        false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "choice",InternalIndexedData.CHOICE_PATTERN_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.CHOICE_PATTERN_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ChoicePatternTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<optional>pattern+</optional>
	private void optional() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_PATTERN, internalIndexedData    /*"pattern","RELAXNG Specification 3.Full Syntax: optional"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData    /*"oneOrMore","RELAXNG Specification 3.Full Syntax: optional"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: optional"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: optional"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData    /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: optional"*/);		
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: optional"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: optional"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData    /*"foreign attributes","RELAXNG Specification 3.Full Syntax: optional"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.OPTIONAL_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: optional"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "optional",  InternalIndexedData.OPTIONAL_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: optional"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.OPTIONAL_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: optional"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new OptionalTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<zeroOrMore>pattern+</zeroOrMore>
	private void zeroOrMore() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);		
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "zeroOrMore", InternalIndexedData.ZERO_OR_MORE_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ZERO_OR_MORE_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);	
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ZeroOrMoreTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<oneOrMore>pattern+</oneOrMore>
	private void oneOrMore() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: oneOrMore"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);		
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "oneOrMore", InternalIndexedData.ONE_OR_MORE_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ONE_OR_MORE_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new OneOrMoreTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<list>pattern+</list>
	private void list() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.LIST_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: list"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.LIST_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: list"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: list"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: list"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: list"*/);		
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.LIST_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: list"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.LIST_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: list"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.LIST_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: list"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.LIST_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: list"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "list", InternalIndexedData.LIST_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: list"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.LIST_ELEMENT, internalIndexedData, recursionModel   /*"element","RELAXNG Specification 3.Full Syntax: list"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ListPatternTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<mixed>pattern+</mixed>
	private void mixed() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.MIXED_ELEMENT_CONTENT_PATTERN, internalIndexedData  /*"pattern","RELAXNG Specification 3.Full Syntax: mixed"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.MIXED_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: mixed"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: mixed"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: mixed"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: mixed"*/);		
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.MIXED_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: mixed"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.MIXED_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: mixed"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.MIXED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: mixed"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.MIXED_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: mixed"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "mixed", InternalIndexedData.MIXED_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: mixed"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.MIXED_ELEMENT, internalIndexedData, recursionModel   /*"element","RELAXNG Specification 3.Full Syntax: mixed"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new MixedTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<ref name="NCName"/>
	private void ref() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameNCNameAttributeForRef();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.REF_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: ref"*/);				
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.REF_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: ref"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: ref"*/);
				
                builder.startLevel();{
                    builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.REF_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: ref"*/);
                }builder.endLevel();
                builder.zeroOrMore(InternalIndexedData.REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: ref"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.REF_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes group","RELAXNG Specification 3.Full Syntax: ref"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "ref", InternalIndexedData.REF_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: ref"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.REF_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: ref"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new RefTaskFactory());
		
        selements.add(e);
	}
	private void nameNCNameAttributeForRef()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","name", InternalIndexedData.REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);				
			builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, 
		                        InternalIndexedData.NO_RECORD,
		                        InternalIndexedData.REF_ELEMENT_CONTENT_NAME_ATTRIBUTE, 
                                internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		SAttribute ncName = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(ncName, nameAttributeTaskFactory);
		sattributes.add(ncName);
    }
    
	
	
	//<parentRef name="NCName"/>
	private void parentRef() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameNCNameAttributeForParentRef();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: parentRef"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: parentRef"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: parentRef"*/);
				
                builder.startLevel();{
                    builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: parentRef"*/);
                }builder.endLevel();
                builder.zeroOrMore(InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: parentRef"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.PARENT_REF_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes group","RELAXNG Specification 3.Full Syntax: parentRef"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "parentRef", InternalIndexedData.PARENT_REF_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: parentRef"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.PARENT_REF_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: parentRef"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new ParentRefTaskFactory());
		
        selements.add(e);
	}
	private void nameNCNameAttributeForParentRef()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","name", InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);				
			builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, 
		                        InternalIndexedData.NO_RECORD, 
		                        InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE, 
                                internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		SAttribute ncName = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(ncName, nameAttributeTaskFactory);

        sattributes.add(ncName);
    }
	
	
	//<empty/>
	private void empty() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.EMPTY_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: empty"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.EMPTY_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: empty"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.EMPTY_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: empty"*/);
				
                builder.startLevel();{
                    builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: emppty"*/);
                }builder.endLevel();
                builder.zeroOrMore(InternalIndexedData.EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: empty"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.EMPTY_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes group","RELAXNG Specification 3.Full Syntax: empty"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "empty", InternalIndexedData.EMPTY_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: empty"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.EMPTY_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: empty"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new EmptyTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<text/>
	private void text() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.TEXT_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: text"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.TEXT_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: text"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.TEXT_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: text"*/);
				
                builder.startLevel();{
                    builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: text"*/);
                }builder.endLevel();
                builder.zeroOrMore(InternalIndexedData.TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: text"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.TEXT_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes group","RELAXNG Specification 3.Full Syntax: text"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "text",  InternalIndexedData.TEXT_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: text"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.TEXT_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: text"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new TextTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<value [type="NCName"]>string</value>
	private void value() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				builder.buildText(InternalIndexedData.VALUE_ELEMENT_CONTENT_TEXT, internalIndexedData, false     /*"text","RELAXNG Specification 3.Full Syntax: value"*/);
				
				builder.startLevel();{
                    builder.startLevel();{
                        builder.buildName("","type", InternalIndexedData.VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: type attribute"*/);				
                        builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: type attribute"*/);
                    }builder.endLevel();
                    builder.buildAttribute(attributeIndex++, 
                                            InternalIndexedData.NO_RECORD,
                                            InternalIndexedData.VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE, 
                                            internalIndexedData   /*"attribute","RELAXNG Specification 3.Full Syntax: type attribute"*/);
                    SAttribute type = (SAttribute)builder.getCurrentPattern();
                    attributeTaskFactory.put(type, typeTaskFactory);

                    sattributes.add(type);                    
                }builder.endLevel();				
                builder.optional(InternalIndexedData.VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_SQUARE, internalIndexedData  /*"optional","RELAXNG Specification 3.Full Syntax: type attribute"*/);
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.VALUE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: value"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.VALUE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: value"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.VALUE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: value"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.VALUE_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and text group","RELAXNG Specification 3.Full Syntax: value"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "value",  InternalIndexedData.VALUE_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: value"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.VALUE_ELEMENT, internalIndexedData, recursionModel    /*"element","RELAXNG Specification 3.Full Syntax: value"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new ValueTaskFactory());
		
        selements.add(e);
	}
	
	
	//<data type="NCName">param* [exceptPattern]</data>
	private void data() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				paramStarExceptPatternSquare();
				
				builder.startLevel();{
                    builder.buildName("","type", InternalIndexedData.DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE_NAME, internalIndexedData, false     /*"name","RELAXNG Specification 3.Full Syntax: type attribute"*/);
                    builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE_VALUE, internalIndexedData      /*"data","RELAXNG Specification 3.Full Syntax: type attribute"*/);
                }builder.endLevel();
                builder.buildAttribute(attributeIndex++, 
                                        InternalIndexedData.NO_RECORD, 
                                        InternalIndexedData.DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE, 
                                        internalIndexedData   /*"attribute","RELAXNG Specification 3.Full Syntax: type attribute"*/);
                SAttribute type = (SAttribute)builder.getCurrentPattern();
                attributeTaskFactory.put(type, typeTaskFactory);

                sattributes.add(type);
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.DATA_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData     /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: data"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.DATA_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData     /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: data"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.DATA_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData     /*"foreign attributes","RELAXNG Specification 3.Full Syntax: data"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DATA_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false      /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: data"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "data", InternalIndexedData.DATA_ELEMENT_NAME, internalIndexedData, false   /* "name","RELAXNG Specification 3.Full Syntax: data"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.DATA_ELEMENT, internalIndexedData, recursionModel    /*"element","RELAXNG Specification 3.Full Syntax: data"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new DataTaskFactory());
		
        selements.add(e);
	}
	private void paramStarExceptPatternSquare() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				builder.startLevel();{
					builder.buildRef(refDefinitionTopPattern[PARAM], PARAM, referenceModel, InternalIndexedData.DATA_ELEMENT_CONTENT_PARAM, internalIndexedData     /*"param","RELAXNG Specification 3.Full Syntax: data"*/);
				}builder.endLevel();
				builder.zeroOrMore(InternalIndexedData.DATA_ELEMENT_CONTENT_PARAM_STAR, internalIndexedData     /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: data"*/);
				builder.startLevel();{
					builder.buildRef(refDefinitionTopPattern[EXCEPT_PATTERN], EXCEPT_PATTERN, referenceModel, InternalIndexedData.DATA_ELEMENT_CONTENT_EXCEPT_PATTERN, internalIndexedData     /*"exceptPattern","RELAXNG Specification 3.Full Syntax: data"*/);
				}builder.endLevel();
				builder.optional(InternalIndexedData.DATA_ELEMENT_CONTENT_EXCEPT_PATTERN_SQUARE, internalIndexedData     /*"optional","RELAXNG Specification 3.Full Syntax: data"*/);
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DATA_ELEMENT_CONTENT_PARAM_EXCEPT_PATTERN_GROUP, 
		                        internalIndexedData, 
		                        false      /*"group of param and except elements","RELAXNG Specification 3.Full Syntax: data"*/);
    
            builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData     /*"foreignElement","RELAXNG Specification 3.Full Syntax: data"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, 
		                        internalIndexedData     /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: data"*/);
            
		}builder.endLevel();
		builder.buildInterleave(InternalIndexedData.DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData     /*"interleave param and except elements group with foreign elements","RELAXNG Specification 3.Full Syntax: data"*/);					
	}	
	
	
	//<notAllowed/>
	private void notAllowed() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
				
                builder.startLevel();{
                    builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
                }builder.endLevel();
                builder.zeroOrMore(InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false    /*"attributes group","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "notAllowed", InternalIndexedData.NOT_ALLOWED_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.NOT_ALLOWED_ELEMENT, internalIndexedData, recursionModel    /*"element","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new NotAllowedTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<externalRef href="anyURI"/>
	private void externalRef() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				hrefAttributeExternalRef();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: externalRef"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: externalRef"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: externalRef"*/);
            
                builder.startLevel();{
                    builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: externalRef"*/);
                }builder.endLevel();
                builder.zeroOrMore(InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: externalRef"*/);
                                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes group","RELAXNG Specification 3.Full Syntax: externalRef"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "externalRef", InternalIndexedData.EXTERNAL_REF_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: externalRef"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.EXTERNAL_REF_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: externalRef"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new ExternalRefTaskFactory());
		
        selements.add(e);
	}
	private void hrefAttributeExternalRef()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","href", InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: href attribute"*/);
			builder.buildData(internalLibrary.createDatatype("hrefURI"), InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE, internalIndexedData  /* "data","RELAXNG Specification 3.Full Syntax: href attribute"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, 
		                        InternalIndexedData.NO_RECORD, 
		                        InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE, 
                                internalIndexedData  /* "attribute","RELAXNG Specification 3.Full Syntax: href attribute"*/);
		SAttribute  href = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(href, hrefTaskFactory);

        sattributes.add(href);
    }
	
	
	//<grammar>grammarContent*</grammar>
	private void grammar() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				grammarContentStarForGrammar();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: grammar"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: grammar"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData /*"foreign attributes","RELAXNG Specification 3.Full Syntax: grammar"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.GRAMMAR_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false    /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: grammar"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "grammar", InternalIndexedData.GRAMMAR_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: grammar"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.GRAMMAR_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: grammar"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		includeStartTopPattern = e;
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new GrammarTaskFactory());
		
        selements.add(e);
	}		
	private void grammarContentStarForGrammar(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(refDefinitionTopPattern[GRAMMAR_CONTENT], GRAMMAR_CONTENT, referenceModel, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_GC, internalIndexedData   /*"grammarContent","RELAXNG Specification 3.Full Syntax: grammar content"*/);
			}builder.endLevel();
			builder.zeroOrMore(InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_GC_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: grammar content"*/);
			
            builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: grammar content"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: grammar content"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC, internalIndexedData   /*"interleaving of grammar content elements with foreign elements","RELAXNG Specification 3.Full Syntax: grammar content"*/);	
	}
	//**************************************************************************
	//END PATTERN METHODS ******************************************************
	//**************************************************************************
 
	
	//**************************************************************************
	//START PATTERN RELATED COMPONENTS METHODS *********************************
	//**************************************************************************
	//<param name=NCName>string</param>
	private void defineParam() throws DatatypeException{	
	    builder.startBuild();	
		builder.startLevel();{
			builder.startLevel();{				
				builder.buildText(InternalIndexedData.PARAM_ELEMENT_CONTENT_TEXT, internalIndexedData, false   /*"text","RELAXNG Specification 3.Full Syntax: param"*/);
				
				nameNCNameAttributeForParam();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.PARAM_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: param"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.PARAM_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: param"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.PARAM_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: param"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.PARAM_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and text group","RELAXNG Specification 3.Full Syntax: param"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "param", InternalIndexedData.PARAM_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: param"*/);
		}builder.endLevel();
		SElement e = builder.buildElement(elementIndex++, InternalIndexedData.PARAM_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: param"*/);		
		 
		endElementTaskFactory.put(e, new ParamTaskFactory());
		refDefinitionTopPattern[PARAM] = e;
		
        selements.add(e);
	}
	private void nameNCNameAttributeForParam()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","name", InternalIndexedData.PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
			builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, 
		                        InternalIndexedData.NO_RECORD, 
		                        InternalIndexedData.PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE, 
                                internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		SAttribute  ncName = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(ncName, nameAttributeTaskFactory);

        sattributes.add(ncName);
    }
	
    //<except>pattern+</except>
	private void defineExceptPattern() throws DatatypeException{
	    builder.startBuild();	
		builder.startLevel();{	
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        recursionModel.add(builder.buildRef(PATTERN, referenceModel, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: except in pattern context"*/));
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "except", InternalIndexedData.EXCEPT_PATTERN_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
		}builder.endLevel();
		SElement e = builder.buildElement(elementIndex++, InternalIndexedData.EXCEPT_PATTERN_ELEMENT, internalIndexedData, recursionModel   /*"element","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
		
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ExceptPatternTaskFactory());
		refDefinitionTopPattern[EXCEPT_PATTERN] = e;
		
        selements.add(e);
	}
	//**************************************************************************
	//END PATTERN RELATED METHODS **********************************************
	//**************************************************************************
	
	
	
	//**************************************************************************
	//START DEFINITIONS METHODS ************************************************
	//**************************************************************************	
	private void defineGrammarContent() throws DatatypeException{
		builder.startBuild();
		builder.startLevel();{
		    
		    builder.buildRef(refDefinitionTopPattern[START], START, referenceModel, InternalIndexedData.DEFINE_GRAMMAR_CONTENT_START, internalIndexedData   /*"start","RELAXNG Specification 3.Full Syntax: start"*/);
			builder.buildRef(refDefinitionTopPattern[DEFINE], DEFINE, referenceModel, InternalIndexedData.DEFINE_GRAMMAR_CONTENT_DEFINE, internalIndexedData   /*"define","RELAXNG Specification 3.Full Syntax: define"*/);
			
			divGrammarContent();
			include();
		}builder.endLevel();
		refDefinitionTopPattern[GRAMMAR_CONTENT] = builder.buildChoicePattern(InternalIndexedData.DEFINE_GRAMMAR_CONTENT, 
		                        internalIndexedData, 
		                        false);		
	}
	//<div> grammarContent* </div>
	private void divGrammarContent() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				
				grammarContentStarForDiv();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData    /*"foreign attributes","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DIV_GC_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false     /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "div", InternalIndexedData.DIV_GC_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.DIV_GC_ELEMENT, internalIndexedData, recursionModel   /*"element","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new DivGrammarContentTaskFactory());
		
        selements.add(e);
	}
	private void grammarContentStarForDiv(){				
		builder.startLevel();{
			builder.startLevel();{
				recursionModel.add(builder.buildRef(GRAMMAR_CONTENT, referenceModel, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_GC, internalIndexedData   /*"grammarContent","RELAXNG Specification 3.Full Syntax: div in grammar context"*/));
			}builder.endLevel();
			builder.zeroOrMore(InternalIndexedData.DIV_GC_ELEMENT_CONTENT_GC_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
			
            builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC, 
		                        internalIndexedData   /*"interleaving of grammar content elements with foreign elements","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);	
	}
	
	//<include href="anyURI"> includeContent* </include> 
	private void include() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{	
			    
				includeContentStarForInclude();
				hrefAttributeInclude();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: include"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: include"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: include"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.INCLUDE_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: include"*/);			
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "include",  InternalIndexedData.INCLUDE_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: include"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.INCLUDE_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: include"*/);		
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new IncludeTaskFactory());
		
        selements.add(e);
	}
	private void includeContentStarForInclude(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(refDefinitionTopPattern[INCLUDE_CONTENT], INCLUDE_CONTENT, referenceModel, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_IC, internalIndexedData   /*"includeContent","RELAXNG Specification 3.Full Syntax: include content"*/);
			}builder.endLevel();
			builder.zeroOrMore(InternalIndexedData.INCLUDE_ELEMENT_CONTENT_IC_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: include content"*/);
    
            builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: include content"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: include content"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC, 
		                        internalIndexedData   /*"interleaving of include content elements with foreign elements","RELAXNG Specification 3.Full Syntax: include content"*/);	
	}
	private void hrefAttributeInclude()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","href", InternalIndexedData.INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: href attribute"*/);
			builder.buildData(internalLibrary.createDatatype("hrefURI"), InternalIndexedData.INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE, internalIndexedData  /* "data","RELAXNG Specification 3.Full Syntax: href attribute"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++,
                                InternalIndexedData.NO_RECORD,
                                InternalIndexedData.INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE, 
                                internalIndexedData  /* "attribute","RELAXNG Specification 3.Full Syntax: href attribute"*/);
		SAttribute href = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(href, hrefTaskFactory);

        sattributes.add(href);
    }    
    
    private void defineIncludeContent() throws DatatypeException{
		builder.startBuild();
		builder.startLevel();{
		    
			builder.buildRef(refDefinitionTopPattern[START], START, referenceModel, InternalIndexedData.DEFINE_INCLUDE_CONTENT_START, internalIndexedData   /*"start","RELAXNG Specification 3.Full Syntax: start"*/);
			builder.buildRef(refDefinitionTopPattern[DEFINE], DEFINE, referenceModel, InternalIndexedData.DEFINE_INCLUDE_CONTENT_DEFINE, internalIndexedData   /*"define","RELAXNG Specification 3.Full Syntax: define"*/);
						
			divIncludeContent();
		}builder.endLevel();
		refDefinitionTopPattern[INCLUDE_CONTENT] = builder.buildChoicePattern(InternalIndexedData.DEFINE_INCLUDE_CONTENT, internalIndexedData, false);		
		
	}	
	
	//<div> includeContent* </div>
	private void divIncludeContent() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				includeContentStarForDiv();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: div in include context"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: div in include context"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: div in include context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DIV_IC_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false    /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: div in include context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "div", InternalIndexedData.DIV_IC_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: div in include context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.DIV_IC_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: div in include context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new DivIncludeContentTaskFactory());
		
        selements.add(e);
	}
	private void includeContentStarForDiv(){				
		builder.startLevel();{
			builder.startLevel();{
				recursionModel.add(builder.buildRef(INCLUDE_CONTENT, referenceModel, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_IC, internalIndexedData   /*"includeContent","RELAXNG Specification 3.Full Syntax: include content"*/));
			}builder.endLevel();
			builder.zeroOrMore(InternalIndexedData.DIV_IC_ELEMENT_CONTENT_IC_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: include content"*/);
    
            builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: include content"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: include content"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC, 
		                        internalIndexedData   /*"interleaving of include content elements with foreign elements","RELAXNG Specification 3.Full Syntax: include content"*/);	
	}
		
	//<start [combine="method"]>pattern</start>
    private void defineStart()  throws DatatypeException{
        builder.startBuild();	
        builder.startLevel();{
			builder.startLevel();{				
				
			    builder.startLevel();{
                    builder.buildRef(refDefinitionTopPattern[PATTERN], PATTERN,  referenceModel, InternalIndexedData.START_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: pattern"*/);
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.START_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: pattern"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.START_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: pattern"*/);
                    
                }builder.endLevel();
                builder.buildInterleave(InternalIndexedData.START_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData   /*"interleaving of pattern element and foreign elements","RELAXNG Specification 3.Full Syntax: pattern"*/);
                
				combineAttributeSquareForStart();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.START_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: start"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.START_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: start"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.START_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: start"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.START_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: start"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "start", InternalIndexedData.START_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: start"*/);
		}builder.endLevel();
		SElement e = builder.buildElement(elementIndex++, InternalIndexedData.START_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: start"*/);
		
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new StartTaskFactory());
        refDefinitionTopPattern[START] = e;
        
        selements.add(e);
    }
    private void combineAttributeSquareForStart() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
                builder.buildName("","combine", InternalIndexedData.START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: combine attribute"*/);
                builder.buildData(internalLibrary.createDatatype("combine"), InternalIndexedData.START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_VALUE, internalIndexedData    /*"value","RELAXNG Specification 3.Full Syntax: combine attribute"*/);
            }builder.endLevel();
            builder.buildAttribute(attributeIndex++, 
                                    InternalIndexedData.NO_RECORD, 
                                    InternalIndexedData.START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE, 
                                    internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: combine attribute"*/);
            SAttribute combine = (SAttribute)builder.getCurrentPattern();
            attributeTaskFactory.put(combine, combineTaskFactory);

            sattributes.add(combine);
		}builder.endLevel();				
		builder.optional(InternalIndexedData.START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE, internalIndexedData   /*"optional","RELAXNG Specification 3.Full Syntax: start"*/);
	}
	
	
    //<define name="NCName" [combine="method"]>pattern+</define>
	private void defineDefine() throws DatatypeException{
	    builder.startBuild();	
        builder.startLevel();{
			builder.startLevel();{
				builder.startLevel();{
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[PATTERN], PATTERN, referenceModel, InternalIndexedData.DEFINE_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: define"*/);
                    }builder.endLevel();
                    builder.oneOrMore(InternalIndexedData.DEFINE_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: define"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: define"*/);
                    }builder.endLevel();
                    builder.zeroOrMore(InternalIndexedData.DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: define"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, 
		                        internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: define"*/);
				
                nameNCNameAttributeForDefine();
				combineAttributeSquareForDefine();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.DEFINE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: define"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.DEFINE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: define"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.DEFINE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: define"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DEFINE_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: define"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "define", InternalIndexedData.DEFINE_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: define"*/);
		}builder.endLevel();
		SElement e = builder.buildElement(elementIndex++, InternalIndexedData.DEFINE_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: define"*/);
		
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new DefineTaskFactory());        	
        refDefinitionTopPattern[DEFINE] = e;
        
        selements.add(e);
    }
    private void nameNCNameAttributeForDefine()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","name", InternalIndexedData.DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: define"*/);
			builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: define"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, 
		                        InternalIndexedData.NO_RECORD, 
		                        InternalIndexedData.DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE, 
                                internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: define"*/);
		SAttribute ncName = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(ncName, nameAttributeTaskFactory);

        sattributes.add(ncName);
    }
    private void combineAttributeSquareForDefine() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
                builder.buildName("","combine", InternalIndexedData.DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: define"*/);
                builder.buildData(internalLibrary.createDatatype("combine"), InternalIndexedData.DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_VALUE, internalIndexedData    /*"value","RELAXNG Specification 3.Full Syntax: define"*/);
            }builder.endLevel();
            builder.buildAttribute(attributeIndex++, 
                                    InternalIndexedData.NO_RECORD, 
                                    InternalIndexedData.DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE, 
                                    internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: define"*/);
            SAttribute combine = (SAttribute)builder.getCurrentPattern();
            attributeTaskFactory.put(combine, combineTaskFactory);

            sattributes.add(combine);
		}builder.endLevel();				
		builder.optional(InternalIndexedData.DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE, internalIndexedData   /*"optional","RELAXNG Specification 3.Full Syntax: start"*/);
	}
	//**************************************************************************
	//END DEFINITIONS METHODS **************************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START NAME CLASS METHODS *************************************************
	//**************************************************************************
	private void defineNameClass()  throws DatatypeException{		
		builder.startBuild();
		builder.startLevel();{
			name();
			anyName();
			nsName();
			choiceNameClass();
		}builder.endLevel();
		refDefinitionTopPattern[NAME_CLASS] = builder.buildChoicePattern(InternalIndexedData.DEFINE_NAME_CLASS, 
		                        internalIndexedData, 
		                        false);
	}
	
	private void name() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				builder.buildData(internalLibrary.createDatatype("QName"), InternalIndexedData.NAME_ELEMENT_CONTENT_DATA, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name"*/);
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.NAME_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: name"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.NAME_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: name"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: name"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.NAME_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false    /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: name"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "name", InternalIndexedData.NAME_ELEMENT_NAME, internalIndexedData, false     /*"name","RELAXNG Specification 3.Full Syntax: name"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.NAME_ELEMENT, internalIndexedData, recursionModel    /*"element","RELAXNG Specification 3.Full Syntax: name"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new NameTaskFactory());
		
        selements.add(e);
	}
	
	private void anyName() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				exceptNameClassSquareForAnyName();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: anyName"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: anyName"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData    /*"foreign attributes","RELAXNG Specification 3.Full Syntax: anyName"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ANY_NAME_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false     /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: anyName"*/);			
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "anyName", InternalIndexedData.ANY_NAME_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: anyName"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ANY_NAME_ELEMENT, internalIndexedData, recursionModel     /*"element","RELAXNG Specification 3.Full Syntax: anyName"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new AnyNameTaskFactory());
		
        selements.add(e);
	}
	private void exceptNameClassSquareForAnyName() throws DatatypeException{	
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(refDefinitionTopPattern[EXCEPT_NAME_CLASS], EXCEPT_NAME_CLASS, referenceModel, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC, internalIndexedData    /*"except name class","RELAXNG Specification 3.Full Syntax: except in name class context"*/);			
			}builder.endLevel();
			builder.optional(InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE, internalIndexedData    /*"optional","RELAXNG Specification 3.Full Syntax: optional"*/);
    
            builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: TODO"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: TODO"*/);
            
		}builder.endLevel();
		// TODO move
		builder.buildInterleave(InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC, 
		                        internalIndexedData    /*"interleaving of except element with foreign elements","RELAXNG Specification 3.Full Syntax: infinite name class content"*/);
	}
	
	private void nsName() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				exceptNameClassSquareForNsName();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: nsName"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: nsName"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: nsName"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.NS_NAME_ELEMENT_CONTENT,
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: nsName"*/);			
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "nsName",InternalIndexedData.NS_NAME_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: nsName"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.NS_NAME_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: nsName"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new NsNameTaskFactory());
		
        selements.add(e);
	}
	private void exceptNameClassSquareForNsName() throws DatatypeException{	
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(refDefinitionTopPattern[EXCEPT_NAME_CLASS], EXCEPT_NAME_CLASS, referenceModel, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_EXCEPT_NC, internalIndexedData    /*"except name class","RELAXNG Specification 3.Full Syntax: except in name class context"*/);			
			}builder.endLevel();
			builder.optional(InternalIndexedData.NS_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE, internalIndexedData    /*"optional","RELAXNG Specification 3.Full Syntax: optional"*/);
    
            builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: TODO"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: TODO"*/);
            
		}builder.endLevel();
		// TODO move
		builder.buildInterleave(InternalIndexedData.NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC, 
		                        internalIndexedData    /*"interleaving of except element with foreign elements","RELAXNG Specification 3.Full Syntax: infinite name class content"*/);
	}
	
	private void choiceNameClass() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{	
				nameClassPlusForChoice();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
								
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "choice", InternalIndexedData.CHOICE_NC_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.CHOICE_NC_ELEMENT, internalIndexedData, recursionModel  /*"element","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ChoiceNameClassTaskFactory());
		
        selements.add(e);
	}	
	private void nameClassPlusForChoice(){				
		builder.startLevel();{
			builder.startLevel();{
				recursionModel.add(builder.buildRef(NAME_CLASS, referenceModel, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_NC, internalIndexedData  /*"nameClass","RELAXNG Specification 3.Full Syntax: name class"*/));
			}builder.endLevel();
			builder.oneOrMore(InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_NC_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
			
			builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: foreign element"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: foreign element"*/);
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC, 
		                        internalIndexedData  /*"interleaving of name class elements with foreign elements","RELAXNG Specification 3.Full Syntax: name class content"*/);	
	}
	//**************************************************************************
	//END NAME CLASS METHODS ***************************************************
	//**************************************************************************
	
	
	
	
	//**************************************************************************
	//START EXCEPT NAME CLASS METHODS ******************************************
	//**************************************************************************
	private void defineExceptNameClass() throws DatatypeException{
	    builder.startBuild();	
        builder.startLevel();{
			builder.startLevel();{	
			    
				nameClassPlusForExcept();
				
				builder.buildRef(refDefinitionTopPattern[NS_ATTRIBUTE], NS_ATTRIBUTE, referenceModel, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
				builder.buildRef(refDefinitionTopPattern[DL_ATTRIBUTE], DL_ATTRIBUTE, referenceModel, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
				builder.buildRef(refDefinitionTopPattern[FOREIGN_ATTRIBUTES], FOREIGN_ATTRIBUTES, referenceModel, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT, 
		                        internalIndexedData, 
		                        false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "except", InternalIndexedData.EXCEPT_NC_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
		}builder.endLevel();
		SElement e = builder.buildElement(elementIndex++, InternalIndexedData.EXCEPT_NC_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
		
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ExceptNameClassTaskFactory());
        refDefinitionTopPattern[EXCEPT_NAME_CLASS] = e;
        
        selements.add(e);
    }
    
    private void nameClassPlusForExcept(){				
		builder.startLevel();{
			builder.startLevel();{
				recursionModel.add(builder.buildRef(NAME_CLASS, referenceModel, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_NC, internalIndexedData  /*"nameClass","RELAXNG Specification 3.Full Syntax: name class"*/));
			}builder.endLevel();
			builder.oneOrMore(InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_NC_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
			
			builder.startLevel();{
                builder.buildRef(refDefinitionTopPattern[FOREIGN_ELEMENT], FOREIGN_ELEMENT, referenceModel, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: foreign element"*/);
            }builder.endLevel();
            builder.zeroOrMore(InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: foreign element"*/);
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC, 
		                        internalIndexedData  /*"interleaving of name class elements with foreign elements","RELAXNG Specification 3.Full Syntax: name class content"*/);	
	}
	//**************************************************************************
	//END EXCEPT NAME CLASS METHODS ********************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START COMMON ATTRIBUTES METHODS ******************************************
	//**************************************************************************
	private void defineOptionalNsAttribute() throws DatatypeException{
	    builder.startBuild();	
	    builder.startLevel();{
            builder.startLevel();{
                builder.buildName("","ns", InternalIndexedData.NS_ATTRIBUTE_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: ns attribute"*/);	
                builder.buildData(nativeLibrary.createDatatype("token"), InternalIndexedData.NS_ATTRIBUTE_VALUE, internalIndexedData    /*"data","RELAXNG Specification 3.Full Syntax: ns attribute"*/);
            }builder.endLevel();
            SAttribute ns = builder.buildAttribute(attributeIndex++, 
                                    InternalIndexedData.NO_RECORD,  
                                    InternalIndexedData.NS_ATTRIBUTE, 
                                    internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: ns attribute"*/);
            
            attributeTaskFactory.put(ns, nsTaskFactory);
            sattributes.add(ns);
            refDefinitionTopPattern[NS_ATTRIBUTE] = ns; 
        }builder.endLevel();				
        builder.optional(InternalIndexedData.NS_ATTRIBUTE_SQUARE, internalIndexedData    /*"optional","RELAXNG Specification 3.Full Syntax: ns attribute"*/);
        
	}
	
	private void defineOptionalDlAttribute()  throws DatatypeException{
	    builder.startBuild();	
        builder.startLevel();{
            builder.startLevel();{
                builder.buildName("","datatypeLibrary", InternalIndexedData.DL_ATTRIBUTE_NAME, internalIndexedData, false     /*"name","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute"*/);
                builder.buildData(internalLibrary.createDatatype("datatypeLibraryURI"), InternalIndexedData.DL_ATTRIBUTE_VALUE, internalIndexedData     /*"data","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute"*/);
            }builder.endLevel();
            SAttribute datatypeLibrary = builder.buildAttribute(attributeIndex++, 
                                    InternalIndexedData.NO_RECORD, 
                                    InternalIndexedData.DL_ATTRIBUTE, 
                                    internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute"*/);
            
            attributeTaskFactory.put(datatypeLibrary, datatypeLibraryTaskFactory);
            sattributes.add(datatypeLibrary);
            refDefinitionTopPattern[DL_ATTRIBUTE] = datatypeLibrary;
        }builder.endLevel();				
        builder.optional(InternalIndexedData.DL_ATTRIBUTE_SQUARE, internalIndexedData    /*"optional","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute"*/);
    
        
    }
    //**************************************************************************
	//END COMMON ATTRIBUTES METHODS ********************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START FOREIGN ELEMENTS METHODS *******************************************
	//**************************************************************************
	private void defineForeignElement(){
		builder.startBuild();
		builder.startLevel();{
		    anyNameExceptRNG();
			anyContentForForeign();			
		}builder.endLevel();
		SElement e = builder.buildElement(elementIndex++, InternalIndexedData.FOREIGN_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: foreign element"*/);
		
		needsStartTask.put(e, null);        
		endElementTaskFactory.put(e, new ForeignElementTaskFactory());
		refDefinitionTopPattern[FOREIGN_ELEMENT] = e;
		
        selements.add(e);
	}
	private void anyNameExceptRNG(){
		builder.startLevel();{
			builder.startLevel();{
				builder.buildNsName(XMLConstants.RELAXNG_NS_URI,  InternalIndexedData.FOREIGN_ELEMENT_ANY_NAME_EXCEPT_NS, internalIndexedData  /*"nsName","RELAXNG Specification 3.Full Syntax: nsName"*/);
			}builder.endLevel();
			builder.buildExceptNameClass(InternalIndexedData.FOREIGN_ELEMENT_ANY_NAME_EXCEPT, internalIndexedData  /*"except","RELAXNG Specification 3.Full Syntax: except"*/);
		}builder.endLevel();
		builder.buildAnyName(InternalIndexedData.FOREIGN_ELEMENT_ANY_NAME, internalIndexedData /*"anyName","RELAXNG Specification 3.Full Syntax: anyName"*/);
	}
	
	
	private void anyContentForForeign(){		
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(refDefinitionTopPattern[ANY_ATTRIBUTE], ANY_ATTRIBUTE, referenceModel, InternalIndexedData.FOREIGN_ELEMENT_CONTENT_ANY_ATTRIBUTE, internalIndexedData   /*"any attribute","RELAXNG Specification 3.Full Syntax: any attribute"*/);
				builder.buildText(InternalIndexedData.FOREIGN_ELEMENT_CONTENT_TEXT, internalIndexedData, false    /*"text","RELAXNG Specification 3.Full Syntax: text"*/);
				builder.buildRef(refDefinitionTopPattern[ANY_ELEMENT], ANY_ELEMENT, referenceModel, InternalIndexedData.FOREIGN_ELEMENT_CONTENT_ANY_ELEMENT, internalIndexedData   /*"anyElement","RELAXNG Specification 3.Full Syntax: any element"*/);
			}builder.endLevel();
			builder.buildChoicePattern(InternalIndexedData.FOREIGN_ELEMENT_CONTENT_CHOICE, 
		                        internalIndexedData, 
		                        false   /*"choice","RELAXNG Specification 3.Full Syntax: any content"*/);
		}builder.endLevel();
		builder.zeroOrMore(InternalIndexedData.FOREIGN_ELEMENT_CONTENT_CHOICE_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: any content"*/);
	}
	
	private void defineAnyElement(){
		builder.startBuild();
		builder.startLevel();{
			anyContentForAny();
			builder.buildAnyName(InternalIndexedData.ANY_ELEMENT_NAME, internalIndexedData /*"anyName","RELAXNG Specification 3.Full Syntax: any element"*/);
		}builder.endLevel();
		SElement e = builder.buildElement(elementIndex++, InternalIndexedData.ANY_ELEMENT, internalIndexedData, recursionModel /*"element","RELAXNG Specification 3.Full Syntax: any element"*/);
		
		needsStartTask.put(e, null);        
		endElementTaskFactory.put(e, new ForeignElementTaskFactory());
		refDefinitionTopPattern[ANY_ELEMENT] = e;
		
        selements.add(e);
	}
	private void anyContentForAny(){		
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(refDefinitionTopPattern[ANY_ATTRIBUTE], ANY_ATTRIBUTE, referenceModel, InternalIndexedData.ANY_ELEMENT_CONTENT_ANY_ATTRIBUTE, internalIndexedData   /*"any attribute","RELAXNG Specification 3.Full Syntax: any attribute"*/);
				builder.buildText(InternalIndexedData.ANY_ELEMENT_CONTENT_TEXT, internalIndexedData, false    /*"text","RELAXNG Specification 3.Full Syntax: text"*/);
				recursionModel.add(builder.buildRef(ANY_ELEMENT, referenceModel, InternalIndexedData.ANY_ELEMENT_CONTENT_ANY_ELEMENT, internalIndexedData   /*"anyElement","RELAXNG Specification 3.Full Syntax: any element"*/));
			}builder.endLevel();
			builder.buildChoicePattern(InternalIndexedData.ANY_ELEMENT_CONTENT_CHOICE, 
		                        internalIndexedData, 
		                        false   /*"choice","RELAXNG Specification 3.Full Syntax: any content"*/);
		}builder.endLevel();
		builder.zeroOrMore(InternalIndexedData.ANY_ELEMENT_CONTENT_CHOICE_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: any content"*/);
	}
	
	private void defineForeignAttributes(){
	    builder.startBuild();	
        builder.startLevel();{
			builder.startLevel();{
				anyNameExceptNullOrRNG();	
				builder.buildText(InternalIndexedData.FOREIGN_ATTRIBUTE_VALUE, internalIndexedData, false  /*"text","RELAXNG Specification 3.Full Syntax: foreign attribute"*/);
			}builder.endLevel();
			SAttribute foreign = builder.buildAttribute(attributeIndex++, 
			                        InternalIndexedData.NO_RECORD, 
			                        InternalIndexedData.FOREIGN_ATTRIBUTE, 
                                    internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: foreign attribute"*/);
            
			attributeTaskFactory.put(foreign, foreignAttributeTaskFactory);
            sattributes.add(foreign);
            refDefinitionTopPattern[FOREIGN_ATTRIBUTES] = foreign;
		}builder.endLevel();				
		builder.zeroOrMore(InternalIndexedData.FOREIGN_ATTRIBUTE_STAR, internalIndexedData /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: foreign attribute"*/);
    }
	private void anyNameExceptNullOrRNG(){
		builder.startLevel();{
			builder.startLevel();{
				builder.startLevel();{
					builder.buildNsName(XMLConstants.RELAXNG_NS_URI,  InternalIndexedData.FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE_NS_RNG, internalIndexedData    /*"nsName","RELAXNG Specification 3.Full Syntax: nsName"*/);
					builder.buildNsName(XMLConstants.NULL_NS_URI,  InternalIndexedData.FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE_NS_NULL, internalIndexedData    /*"nsName","RELAXNG Specification 3.Full Syntax: nsName"*/);
				}builder.endLevel();
				builder.buildChoiceNameClass(InternalIndexedData.FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE, internalIndexedData    /*"choice","RELAXNG Specification 3.Full Syntax: choice"*/);
			}builder.endLevel();
			builder.buildExceptNameClass(InternalIndexedData.FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT, internalIndexedData    /*"except","RELAXNG Specification 3.Full Syntax: except"*/);
		}builder.endLevel();
		builder.buildAnyName(InternalIndexedData.FOREIGN_ATTRIBUTE_ANY_NAME, internalIndexedData    /*"anyName","RELAXNG Specification 3.Full Syntax: anyName"*/);
	}
	
	
	private void defineAnyAttribute(){
	    builder.startBuild();	
        builder.startLevel();{
			builder.buildAnyName(InternalIndexedData.ANY_ATTRIBUTE_NAME, internalIndexedData /*"anyName","RELAXNG Specification 3.Full Syntax: any attribute"*/);
			builder.buildText(InternalIndexedData.ANY_ATTRIBUTE_VALUE, internalIndexedData, false   /*"text","RELAXNG Specification 3.Full Syntax: any attribute"*/);
		}builder.endLevel();
		SAttribute any = builder.buildAttribute(attributeIndex++, 
		                        InternalIndexedData.NO_RECORD, 
		                        InternalIndexedData.ANY_ATTRIBUTE, 
                                internalIndexedData /*"attribute","RELAXNG Specification 3.Full Syntax: any attribute"*/);
        
        refDefinitionTopPattern[ANY_ATTRIBUTE] = any;
        sattributes.add(any);
    }
    //**************************************************************************
	//END FOREIGN ELEMENTS METHODS *********************************************
	//**************************************************************************
    
}
