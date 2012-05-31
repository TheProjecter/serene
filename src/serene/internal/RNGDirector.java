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

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.components.SPattern;
//import serene.validation.schema.simplified.components.NameClass;
import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

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
    final int PARAM = 1;
    final int EXCEPT_PATTERN = 2;
    final int GRAMMAR_CONTENT = 3;
    final int INCLUDE_CONTENT = 4;
    final int START = 5;
    final int DEFINE = 6;
    final int NAME_CLASS = 7;
    final int EXCEPT_NAME_CLASS = 8;
    
    final int NS_ATTRIBUTE = 9;
    final int DL_ATTRIBUTE = 10;
    final int FOREIGN_ATTRIBUTES = 11;
    
    final int FOREIGN_ELEMENT = 12;
    final int ANY_ELEMENT = 13;
    final int ANY_ATTRIBUTE = 14;
    
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
        
		startGrammar();
		
		definePattern();
		defineParam();
		defineExceptPattern();
		defineGrammarContent();
		defineIncludeContent();
		defineStart();
		defineDefine();
		defineNameClass();
		defineExceptNameClass();
		
		defineOptionalNsAttribute();
		defineOptionalDlAttribute();
		defineForeignAttributes();
		defineForeignElement();
		defineAnyElement();
		defineAnyAttribute();	
	}
	
	SimplifiedModel getRNGModel(){
		SPattern[] start = {rngStartTopPattern};
        
		SElement startElement = new SElement(elementIndex, null, rngStartTopPattern, -1, null);
        selements.add(startElement);        
		SimplifiedModel s = new SimplifiedModel(start,
									refDefinitionTopPattern,
									elementIndex,
									selements.toArray(new SElement[selements.size()]),
									sattributes.toArray(new SAttribute[sattributes.size()]),
									null,
									null);
		elementIndex++;
        return s;
	}
	
	SimplifiedModel getIncludeModel(){
		SPattern[] start = {includeStartTopPattern};
        
		SElement startElement = new SElement(elementIndex, null, includeStartTopPattern, -1, null);
        selements.add(startElement);     
		SimplifiedModel s = new SimplifiedModel(start,
									refDefinitionTopPattern,
									elementIndex,
									selements.toArray(new SElement[selements.size()]),
									sattributes.toArray(new SAttribute[sattributes.size()]),
									null,
									null);
		elementIndex++;
		return s;
	}
	
	SimplifiedModel getExternalRefModel(){
		SPattern[] start = {externalRefStartTopPattern};
        
		SElement startElement = new SElement(elementIndex, null, externalRefStartTopPattern, -1, null);
        selements.add(startElement);    
		SimplifiedModel s = new SimplifiedModel(start,
									refDefinitionTopPattern,
									elementIndex,
									selements.toArray(new SElement[selements.size()]),
									sattributes.toArray(new SAttribute[sattributes.size()]),
									null,
									null);
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
		builder.buildRef(PATTERN, InternalIndexedData.REF_PATTERN, internalIndexedData);			
		SPattern r = builder.getCurrentPattern();
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
		builder.buildChoicePattern(InternalIndexedData.DEFINE_PATTERN, internalIndexedData, false);	
		refDefinitionTopPattern[PATTERN] = builder.getCurrentPattern();
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
                        builder.buildRef(PATTERN,InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_PATTERN, internalIndexedData /*"pattern","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);		
                
                nameQNameAttributeForElement();
                
                builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
                
            }builder.endLevel();
            builder.buildGroup( InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT, internalIndexedData, false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
            builder.buildName(XMLConstants.RELAXNG_NS_URI, "element", InternalIndexedData.ELEMENT_NI_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
        }builder.endLevel();
        builder.buildElement(elementIndex++, InternalIndexedData.ELEMENT_NI_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: element with name attribute"*/);
        
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
        builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE, internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with QName value"*/);
        
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
								
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "element", InternalIndexedData.ELEMENT_NC_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
		}builder.endLevel();		
		builder.buildElement(elementIndex++, InternalIndexedData.ELEMENT_NC_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ElementWithNameClassTaskFactory());
		
        selements.add(e);
	}	
	private void nameClassPatternPlus(){		
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(NAME_CLASS, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS, internalIndexedData  /*"nameClass","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
				builder.startLevel();{
					builder.buildRef(PATTERN, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_PATTERN, internalIndexedData  /*"pattern","RELAXNG Specification 3.Full Syntax: element with name class child"*/);						
				}builder.endLevel();
				builder.buildOneOrMore(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: element with name class child"*/);				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP, internalIndexedData, false   /*"group of name class and pattern elements","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
			
			builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: element with name class child"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData  /*"interleaving of name class and pattern elements group with foreign elements","RELAXNG Specification 3.Full Syntax: element with name class child"*/);		
	}
	//**************************************************************************
	
	
    //<attribute>nameClass pattern</attribute>
	private void attributeWithNameClass() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameClassPatternSquare();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
								
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT, internalIndexedData, false   /* "attributes and elements group","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "attribute", InternalIndexedData.ATTRIBUTE_NC_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
		}builder.endLevel();		
		builder.buildElement(elementIndex++, InternalIndexedData.ATTRIBUTE_NC_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new AttributeWithNameClassTaskFactory());
		
        selements.add(e);
	}
	private void nameClassPatternSquare(){
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(NAME_CLASS, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS, internalIndexedData    /*"nameClass","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
				builder.startLevel();{
					builder.buildRef(PATTERN, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);						
				}builder.endLevel();
				builder.buildOptional(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN_SQUARE, internalIndexedData  /*"optional","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP, internalIndexedData, false   /*"group of name class and pattern elements","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
			
			builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData  /*"interleaving of name class and pattern elements group with foreign elements","RELAXNG Specification 3.Full Syntax: attribute with name class child"*/);		
	}
	
	
	//<attribute name="QName">pattern</attribute>
	private void attributeWithNameInstance() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{				
				nameQNameAttributeForAttribute();
				patternSquare();	
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "attribute", InternalIndexedData.ATTRIBUTE_NI_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ATTRIBUTE_NI_ELEMENT, internalIndexedData    /*"element","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
		
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
		builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE, internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with QName value"*/);
        
		SAttribute qName = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(qName, nameAttributeTaskFactory);

        sattributes.add(qName);
    }
    private void patternSquare(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(PATTERN,InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN, internalIndexedData  /*"pattern","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
			}builder.endLevel();
			builder.buildOptional(InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN_SQUARE, internalIndexedData  /*"optional","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
			
			builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData  /*"interleaving of pattern element and foreign elements","RELAXNG Specification 3.Full Syntax: attribute with name attribute"*/);	
	}
    
    
    //<group>pattern+</group>
	private void group() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{				
				builder.startLevel();{
                    builder.startLevel();{
                        builder.buildRef(PATTERN, InternalIndexedData.GROUP_ELEMENT_CONTENT_PATTERN, internalIndexedData /*"pattern","RELAXNG Specification 3.Full Syntax: group"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.GROUP_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData /*"oneOrMore","RELAXNG Specification 3.Full Syntax: group"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: group"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: group"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData  /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: group"*/);		
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.GROUP_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: group"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.GROUP_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: group"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.GROUP_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: group"*/);
				
			}builder.endLevel();
			builder.buildGroup( InternalIndexedData.GROUP_ELEMENT_CONTENT, internalIndexedData, false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: group"*/);			
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "group", InternalIndexedData.GROUP_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: group"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.GROUP_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: group"*/);
		
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
                        builder.buildRef(PATTERN,  InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: interleave"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: interleave"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: interleave"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: interleave"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);		
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: interleave"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: interleave"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: interleave"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.INTERLEAVE_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: interleave"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "interleave", InternalIndexedData.INTERLEAVE_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: interleave"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.INTERLEAVE_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: interleave"*/);
		
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
                        builder.buildRef(PATTERN, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN, internalIndexedData  /*"pattern","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData  /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);		
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
				
			}builder.endLevel();
			builder.buildGroup( InternalIndexedData.CHOICE_PATTERN_ELEMENT_CONTENT, internalIndexedData, false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "choice",InternalIndexedData.CHOICE_PATTERN_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.CHOICE_PATTERN_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: choice in pattern context"*/);
		
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
                        builder.buildRef(PATTERN, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_PATTERN, internalIndexedData    /*"pattern","RELAXNG Specification 3.Full Syntax: optional"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData    /*"oneOrMore","RELAXNG Specification 3.Full Syntax: optional"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: optional"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: optional"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData    /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: optional"*/);		
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: optional"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: optional"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.OPTIONAL_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData    /*"foreign attributes","RELAXNG Specification 3.Full Syntax: optional"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.OPTIONAL_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: optional"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "optional",  InternalIndexedData.OPTIONAL_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: optional"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.OPTIONAL_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: optional"*/);
		
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
                        builder.buildRef(PATTERN, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);		
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ZERO_OR_MORE_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "zeroOrMore", InternalIndexedData.ZERO_OR_MORE_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ZERO_OR_MORE_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: zeroOrMore"*/);	
		
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
                        builder.buildRef(PATTERN, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);		
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ONE_OR_MORE_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "oneOrMore", InternalIndexedData.ONE_OR_MORE_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ONE_OR_MORE_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
		
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
                        builder.buildRef(PATTERN, InternalIndexedData.LIST_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: list"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.LIST_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: list"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: list"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: list"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: list"*/);		
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.LIST_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: list"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.LIST_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: list"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.LIST_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: list"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.LIST_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: list"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "list", InternalIndexedData.LIST_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: list"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.LIST_ELEMENT, internalIndexedData   /*"element","RELAXNG Specification 3.Full Syntax: list"*/);
		
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
                        builder.buildRef(PATTERN, InternalIndexedData.MIXED_ELEMENT_CONTENT_PATTERN, internalIndexedData  /*"pattern","RELAXNG Specification 3.Full Syntax: mixed"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.MIXED_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: mixed"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: mixed"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: mixed"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: mixed"*/);		
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.MIXED_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: mixed"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.MIXED_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: mixed"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.MIXED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: mixed"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.MIXED_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: mixed"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "mixed", InternalIndexedData.MIXED_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: mixed"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.MIXED_ELEMENT, internalIndexedData   /*"element","RELAXNG Specification 3.Full Syntax: mixed"*/);
		
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
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.REF_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: ref"*/);				
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.REF_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: ref"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: ref"*/);
				
                builder.startLevel();{
                    builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.REF_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: ref"*/);
                }builder.endLevel();
                builder.buildZeroOrMore(InternalIndexedData.REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: ref"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.REF_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes group","RELAXNG Specification 3.Full Syntax: ref"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "ref", InternalIndexedData.REF_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: ref"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.REF_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: ref"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new RefTaskFactory());
		
        selements.add(e);
	}
	private void nameNCNameAttributeForRef()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","name", InternalIndexedData.REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);				
			builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.REF_ELEMENT_CONTENT_NAME_ATTRIBUTE, internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		SAttribute ncName = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(ncName, nameAttributeTaskFactory);
		sattributes.add(ncName);
    }
    
	
	
	//<parentRef name="NCName"/>
	private void parentRef() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameNCNameAttributeForParentRef();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: parentRef"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: parentRef"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: parentRef"*/);
				
                builder.startLevel();{
                    builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: parentRef"*/);
                }builder.endLevel();
                builder.buildZeroOrMore(InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: parentRef"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.PARENT_REF_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes group","RELAXNG Specification 3.Full Syntax: parentRef"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "parentRef", InternalIndexedData.PARENT_REF_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: parentRef"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.PARENT_REF_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: parentRef"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new ParentRefTaskFactory());
		
        selements.add(e);
	}
	private void nameNCNameAttributeForParentRef()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","name", InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);				
			builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE, internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		SAttribute ncName = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(ncName, nameAttributeTaskFactory);

        sattributes.add(ncName);
    }
	
	
	//<empty/>
	private void empty() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.EMPTY_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: empty"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.EMPTY_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: empty"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.EMPTY_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: empty"*/);
				
                builder.startLevel();{
                    builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: emppty"*/);
                }builder.endLevel();
                builder.buildZeroOrMore(InternalIndexedData.EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: empty"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.EMPTY_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes group","RELAXNG Specification 3.Full Syntax: empty"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "empty", InternalIndexedData.EMPTY_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: empty"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.EMPTY_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: empty"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new EmptyTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<text/>
	private void text() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.TEXT_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: text"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.TEXT_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: text"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.TEXT_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: text"*/);
				
                builder.startLevel();{
                    builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: text"*/);
                }builder.endLevel();
                builder.buildZeroOrMore(InternalIndexedData.TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: text"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.TEXT_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes group","RELAXNG Specification 3.Full Syntax: text"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "text",  InternalIndexedData.TEXT_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: text"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.TEXT_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: text"*/);
		
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
                    builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE, internalIndexedData   /*"attribute","RELAXNG Specification 3.Full Syntax: type attribute"*/);
                    SAttribute type = (SAttribute)builder.getCurrentPattern();
                    attributeTaskFactory.put(type, typeTaskFactory);

                    sattributes.add(type);                    
                }builder.endLevel();				
                builder.buildOptional(InternalIndexedData.VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_SQUARE, internalIndexedData  /*"optional","RELAXNG Specification 3.Full Syntax: type attribute"*/);
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.VALUE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: value"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.VALUE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: value"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.VALUE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: value"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.VALUE_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and text group","RELAXNG Specification 3.Full Syntax: value"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "value",  InternalIndexedData.VALUE_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: value"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.VALUE_ELEMENT, internalIndexedData    /*"element","RELAXNG Specification 3.Full Syntax: value"*/);
		
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
                builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE, internalIndexedData   /*"attribute","RELAXNG Specification 3.Full Syntax: type attribute"*/);
                SAttribute type = (SAttribute)builder.getCurrentPattern();
                attributeTaskFactory.put(type, typeTaskFactory);

                sattributes.add(type);
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.DATA_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData     /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: data"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.DATA_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData     /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: data"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.DATA_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData     /*"foreign attributes","RELAXNG Specification 3.Full Syntax: data"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DATA_ELEMENT_CONTENT, internalIndexedData, false      /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: data"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "data", InternalIndexedData.DATA_ELEMENT_NAME, internalIndexedData, false   /* "name","RELAXNG Specification 3.Full Syntax: data"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.DATA_ELEMENT, internalIndexedData    /*"element","RELAXNG Specification 3.Full Syntax: data"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new DataTaskFactory());
		
        selements.add(e);
	}
	private void paramStarExceptPatternSquare() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				builder.startLevel();{
					builder.buildRef(PARAM, InternalIndexedData.DATA_ELEMENT_CONTENT_PARAM, internalIndexedData     /*"param","RELAXNG Specification 3.Full Syntax: data"*/);
				}builder.endLevel();
				builder.buildZeroOrMore(InternalIndexedData.DATA_ELEMENT_CONTENT_PARAM_STAR, internalIndexedData     /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: data"*/);
				builder.startLevel();{
					builder.buildRef(EXCEPT_PATTERN,InternalIndexedData.DATA_ELEMENT_CONTENT_EXCEPT_PATTERN, internalIndexedData     /*"exceptPattern","RELAXNG Specification 3.Full Syntax: data"*/);
				}builder.endLevel();
				builder.buildOptional(InternalIndexedData.DATA_ELEMENT_CONTENT_EXCEPT_PATTERN_SQUARE, internalIndexedData     /*"optional","RELAXNG Specification 3.Full Syntax: data"*/);
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DATA_ELEMENT_CONTENT_PARAM_EXCEPT_PATTERN_GROUP, internalIndexedData, false      /*"group of param and except elements","RELAXNG Specification 3.Full Syntax: data"*/);
    
            builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData     /*"foreignElement","RELAXNG Specification 3.Full Syntax: data"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData     /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: data"*/);
            
		}builder.endLevel();
		builder.buildInterleave(InternalIndexedData.DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData     /*"interleave param and except elements group with foreign elements","RELAXNG Specification 3.Full Syntax: data"*/);					
	}	
	
	
	//<notAllowed/>
	private void notAllowed() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
				
                builder.startLevel();{
                    builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
                }builder.endLevel();
                builder.buildZeroOrMore(InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.NOT_ALLOWED_ELEMENT_CONTENT, internalIndexedData, false    /*"attributes group","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "notAllowed", InternalIndexedData.NOT_ALLOWED_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.NOT_ALLOWED_ELEMENT, internalIndexedData    /*"element","RELAXNG Specification 3.Full Syntax: notAllowed"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new NotAllowedTaskFactory());
		
        selements.add(e);
	}
	
	
	
	//<externalRef href="anyURI"/>
	private void externalRef() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				hrefAttributeExternalRef();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: externalRef"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: externalRef"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: externalRef"*/);
            
                builder.startLevel();{
                    builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: externalRef"*/);
                }builder.endLevel();
                builder.buildZeroOrMore(InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: externalRef"*/);
                                
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes group","RELAXNG Specification 3.Full Syntax: externalRef"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "externalRef", InternalIndexedData.EXTERNAL_REF_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: externalRef"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.EXTERNAL_REF_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: externalRef"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new ExternalRefTaskFactory());
		
        selements.add(e);
	}
	private void hrefAttributeExternalRef()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","href", InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: href attribute"*/);
			builder.buildData(internalLibrary.createDatatype("hrefURI"), InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE, internalIndexedData  /* "data","RELAXNG Specification 3.Full Syntax: href attribute"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE, internalIndexedData  /* "attribute","RELAXNG Specification 3.Full Syntax: href attribute"*/);
		SAttribute  href = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(href, hrefTaskFactory);

        sattributes.add(href);
    }
	
	
	//<grammar>grammarContent*</grammar>
	private void grammar() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				grammarContentStarForGrammar();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: grammar"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: grammar"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData /*"foreign attributes","RELAXNG Specification 3.Full Syntax: grammar"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.GRAMMAR_ELEMENT_CONTENT, internalIndexedData, false    /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: grammar"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "grammar", InternalIndexedData.GRAMMAR_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: grammar"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.GRAMMAR_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: grammar"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		includeStartTopPattern = e;
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new GrammarTaskFactory());
		
        selements.add(e);
	}		
	private void grammarContentStarForGrammar(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(GRAMMAR_CONTENT, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_GC, internalIndexedData   /*"grammarContent","RELAXNG Specification 3.Full Syntax: grammar content"*/);
			}builder.endLevel();
			builder.buildZeroOrMore(InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_GC_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: grammar content"*/);
			
            builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: grammar content"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: grammar content"*/);
            
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
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.PARAM_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: param"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.PARAM_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: param"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.PARAM_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: param"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.PARAM_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and text group","RELAXNG Specification 3.Full Syntax: param"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "param", InternalIndexedData.PARAM_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: param"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.PARAM_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: param"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new ParamTaskFactory());
		refDefinitionTopPattern[PARAM] = e;
		
        selements.add(e);
	}
	private void nameNCNameAttributeForParam()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","name", InternalIndexedData.PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
			builder.buildData(internalLibrary.createDatatype("NCName"), InternalIndexedData.PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE, internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: name attribute with NCName value"*/);
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
                        builder.buildRef(PATTERN, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.EXCEPT_PATTERN_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "except", InternalIndexedData.EXCEPT_PATTERN_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.EXCEPT_PATTERN_ELEMENT, internalIndexedData   /*"element","RELAXNG Specification 3.Full Syntax: except in pattern context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
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
		    
		    builder.buildRef(START, InternalIndexedData.DEFINE_GRAMMAR_CONTENT_START, internalIndexedData   /*"start","RELAXNG Specification 3.Full Syntax: start"*/);
			builder.buildRef(DEFINE, InternalIndexedData.DEFINE_GRAMMAR_CONTENT_DEFINE, internalIndexedData   /*"define","RELAXNG Specification 3.Full Syntax: define"*/);
			
			divGrammarContent();
			include();
		}builder.endLevel();
		builder.buildChoicePattern(InternalIndexedData.DEFINE_GRAMMAR_CONTENT, internalIndexedData, false);		
		refDefinitionTopPattern[GRAMMAR_CONTENT] = builder.getCurrentPattern();
	}
	//<div> grammarContent* </div>
	private void divGrammarContent() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				
				grammarContentStarForDiv();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData    /*"foreign attributes","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DIV_GC_ELEMENT_CONTENT, internalIndexedData, false     /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "div", InternalIndexedData.DIV_GC_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.DIV_GC_ELEMENT, internalIndexedData   /*"element","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new DivGrammarContentTaskFactory());
		
        selements.add(e);
	}
	private void grammarContentStarForDiv(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(GRAMMAR_CONTENT, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_GC, internalIndexedData   /*"grammarContent","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
			}builder.endLevel();
			builder.buildZeroOrMore(InternalIndexedData.DIV_GC_ELEMENT_CONTENT_GC_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
			
            builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC, internalIndexedData   /*"interleaving of grammar content elements with foreign elements","RELAXNG Specification 3.Full Syntax: div in grammar context"*/);	
	}
	
	//<include href="anyURI"> includeContent* </include> 
	private void include() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{	
			    
				includeContentStarForInclude();
				hrefAttributeInclude();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: include"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: include"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: include"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.INCLUDE_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: include"*/);			
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "include",  InternalIndexedData.INCLUDE_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: include"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.INCLUDE_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: include"*/);		
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new IncludeTaskFactory());
		
        selements.add(e);
	}
	private void includeContentStarForInclude(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(INCLUDE_CONTENT, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_IC, internalIndexedData   /*"includeContent","RELAXNG Specification 3.Full Syntax: include content"*/);
			}builder.endLevel();
			builder.buildZeroOrMore(InternalIndexedData.INCLUDE_ELEMENT_CONTENT_IC_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: include content"*/);
    
            builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: include content"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: include content"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC, internalIndexedData   /*"interleaving of include content elements with foreign elements","RELAXNG Specification 3.Full Syntax: include content"*/);	
	}
	private void hrefAttributeInclude()  throws DatatypeException{
        builder.startLevel();{
			builder.buildName("","href", InternalIndexedData.INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: href attribute"*/);
			builder.buildData(internalLibrary.createDatatype("hrefURI"), InternalIndexedData.INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE, internalIndexedData  /* "data","RELAXNG Specification 3.Full Syntax: href attribute"*/);
		}builder.endLevel();
		builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE, internalIndexedData  /* "attribute","RELAXNG Specification 3.Full Syntax: href attribute"*/);
		SAttribute href = (SAttribute)builder.getCurrentPattern();
		attributeTaskFactory.put(href, hrefTaskFactory);

        sattributes.add(href);
    }    
    
    private void defineIncludeContent() throws DatatypeException{
		builder.startBuild();
		builder.startLevel();{
		    
			builder.buildRef(START, InternalIndexedData.DEFINE_INCLUDE_CONTENT_START, internalIndexedData   /*"start","RELAXNG Specification 3.Full Syntax: start"*/);
			builder.buildRef(DEFINE, InternalIndexedData.DEFINE_INCLUDE_CONTENT_DEFINE, internalIndexedData   /*"define","RELAXNG Specification 3.Full Syntax: define"*/);
						
			divIncludeContent();
		}builder.endLevel();
		builder.buildChoicePattern(InternalIndexedData.DEFINE_INCLUDE_CONTENT, internalIndexedData, false);		
		refDefinitionTopPattern[INCLUDE_CONTENT] = builder.getCurrentPattern();
	}	
	
	//<div> includeContent* </div>
	private void divIncludeContent() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				includeContentStarForDiv();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: div in include context"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: div in include context"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: div in include context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.DIV_IC_ELEMENT_CONTENT, internalIndexedData, false    /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: div in include context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "div", InternalIndexedData.DIV_IC_ELEMENT_NAME, internalIndexedData, false   /*"name","RELAXNG Specification 3.Full Syntax: div in include context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.DIV_IC_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: div in include context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new DivIncludeContentTaskFactory());
		
        selements.add(e);
	}
	private void includeContentStarForDiv(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(INCLUDE_CONTENT, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_IC, internalIndexedData   /*"includeContent","RELAXNG Specification 3.Full Syntax: include content"*/);
			}builder.endLevel();
			builder.buildZeroOrMore(InternalIndexedData.DIV_IC_ELEMENT_CONTENT_IC_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: include content"*/);
    
            builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: include content"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: include content"*/);
            
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC, internalIndexedData   /*"interleaving of include content elements with foreign elements","RELAXNG Specification 3.Full Syntax: include content"*/);	
	}
		
	//<start [combine="method"]>pattern</start>
    private void defineStart()  throws DatatypeException{
        builder.startBuild();	
        builder.startLevel();{
			builder.startLevel();{				
				
			    builder.startLevel();{
                    builder.buildRef(PATTERN,  InternalIndexedData.START_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: pattern"*/);
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.START_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: pattern"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.START_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: pattern"*/);
                    
                }builder.endLevel();
                builder.buildInterleave(InternalIndexedData.START_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData   /*"interleaving of pattern element and foreign elements","RELAXNG Specification 3.Full Syntax: pattern"*/);
                
				combineAttributeSquareForStart();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.START_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: start"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.START_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: start"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.START_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: start"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.START_ELEMENT_CONTENT, internalIndexedData, false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: start"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "start", InternalIndexedData.START_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: start"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.START_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: start"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
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
            builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE, internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: combine attribute"*/);
            SAttribute combine = (SAttribute)builder.getCurrentPattern();
            attributeTaskFactory.put(combine, combineTaskFactory);

            sattributes.add(combine);
		}builder.endLevel();				
		builder.buildOptional(InternalIndexedData.START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE, internalIndexedData   /*"optional","RELAXNG Specification 3.Full Syntax: start"*/);
	}
	
	
    //<define name="NCName" [combine="method"]>pattern+</define>
	private void defineDefine() throws DatatypeException{
	    builder.startBuild();	
        builder.startLevel();{
			builder.startLevel();{
				builder.startLevel();{
                    builder.startLevel();{
                        builder.buildRef(PATTERN, InternalIndexedData.DEFINE_ELEMENT_CONTENT_PATTERN, internalIndexedData   /*"pattern","RELAXNG Specification 3.Full Syntax: define"*/);
                    }builder.endLevel();
                    builder.buildOneOrMore(InternalIndexedData.DEFINE_ELEMENT_CONTENT_PATTERN_PLUS, internalIndexedData   /*"oneOrMore","RELAXNG Specification 3.Full Syntax: define"*/);			
                    
                    builder.startLevel();{
                        builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData   /*"foreignElement","RELAXNG Specification 3.Full Syntax: define"*/);
                    }builder.endLevel();
                    builder.buildZeroOrMore(InternalIndexedData.DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: define"*/);
                    
                }builder.endLevel();				
                builder.buildInterleave(InternalIndexedData.DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE, internalIndexedData   /*"interleaving of pattern elements with foreign elements","RELAXNG Specification 3.Full Syntax: define"*/);
				
                nameNCNameAttributeForDefine();
				combineAttributeSquareForDefine();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.DEFINE_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: define"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.DEFINE_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: define"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.DEFINE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: define"*/);
				
			}builder.endLevel();
			builder.buildGroup( InternalIndexedData.DEFINE_ELEMENT_CONTENT, internalIndexedData, false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: define"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "define", InternalIndexedData.DEFINE_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: define"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.DEFINE_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: define"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
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
		builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE, internalIndexedData  /*"attribute","RELAXNG Specification 3.Full Syntax: define"*/);
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
            builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE, internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: define"*/);
            SAttribute combine = (SAttribute)builder.getCurrentPattern();
            attributeTaskFactory.put(combine, combineTaskFactory);

            sattributes.add(combine);
		}builder.endLevel();				
		builder.buildOptional(InternalIndexedData.DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE, internalIndexedData   /*"optional","RELAXNG Specification 3.Full Syntax: start"*/);
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
		builder.buildChoicePattern(InternalIndexedData.DEFINE_NAME_CLASS, internalIndexedData, false);
		refDefinitionTopPattern[NAME_CLASS] = builder.getCurrentPattern();
	}
	
	private void name() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				builder.buildData(internalLibrary.createDatatype("QName"), InternalIndexedData.NAME_ELEMENT_CONTENT_DATA, internalIndexedData   /*"data","RELAXNG Specification 3.Full Syntax: name"*/);
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.NAME_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: name"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.NAME_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData   /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: name"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: name"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.NAME_ELEMENT_CONTENT, internalIndexedData, false    /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: name"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "name", InternalIndexedData.NAME_ELEMENT_NAME, internalIndexedData, false     /*"name","RELAXNG Specification 3.Full Syntax: name"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.NAME_ELEMENT, internalIndexedData    /*"element","RELAXNG Specification 3.Full Syntax: name"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskFactory.put(e, new NameTaskFactory());
		
        selements.add(e);
	}
	
	private void anyName() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				exceptNameClassSquareForAnyName();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: anyName"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData    /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: anyName"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData    /*"foreign attributes","RELAXNG Specification 3.Full Syntax: anyName"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.ANY_NAME_ELEMENT_CONTENT, internalIndexedData, false     /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: anyName"*/);			
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "anyName", InternalIndexedData.ANY_NAME_ELEMENT_NAME, internalIndexedData, false    /*"name","RELAXNG Specification 3.Full Syntax: anyName"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ANY_NAME_ELEMENT, internalIndexedData     /*"element","RELAXNG Specification 3.Full Syntax: anyName"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new AnyNameTaskFactory());
		
        selements.add(e);
	}
	private void exceptNameClassSquareForAnyName() throws DatatypeException{	
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(EXCEPT_NAME_CLASS, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC, internalIndexedData    /*"except name class","RELAXNG Specification 3.Full Syntax: except in name class context"*/);			
			}builder.endLevel();
			builder.buildOptional(InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE, internalIndexedData    /*"optional","RELAXNG Specification 3.Full Syntax: optional"*/);
    
            builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: TODO"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: TODO"*/);
            
		}builder.endLevel();
		// TODO move
		builder.buildInterleave(InternalIndexedData.ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC, internalIndexedData    /*"interleaving of except element with foreign elements","RELAXNG Specification 3.Full Syntax: infinite name class content"*/);
	}
	
	private void nsName() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				exceptNameClassSquareForNsName();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: nsName"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: nsName"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: nsName"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.NS_NAME_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: nsName"*/);			
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "nsName",InternalIndexedData.NS_NAME_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: nsName"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.NS_NAME_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: nsName"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new NsNameTaskFactory());
		
        selements.add(e);
	}
	private void exceptNameClassSquareForNsName() throws DatatypeException{	
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(EXCEPT_NAME_CLASS, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_EXCEPT_NC, internalIndexedData    /*"except name class","RELAXNG Specification 3.Full Syntax: except in name class context"*/);			
			}builder.endLevel();
			builder.buildOptional(InternalIndexedData.NS_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE, internalIndexedData    /*"optional","RELAXNG Specification 3.Full Syntax: optional"*/);
    
            builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData    /*"foreignElement","RELAXNG Specification 3.Full Syntax: TODO"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData    /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: TODO"*/);
            
		}builder.endLevel();
		// TODO move
		builder.buildInterleave(InternalIndexedData.NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC, internalIndexedData    /*"interleaving of except element with foreign elements","RELAXNG Specification 3.Full Syntax: infinite name class content"*/);
	}
	
	private void choiceNameClass() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{	
				nameClassPlusForChoice();
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData  /*"foreign attributes","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
								
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT, internalIndexedData, false   /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "choice", InternalIndexedData.CHOICE_NC_ELEMENT_NAME, internalIndexedData, false  /*"name","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.CHOICE_NC_ELEMENT, internalIndexedData  /*"element","RELAXNG Specification 3.Full Syntax: choice in name class context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ChoiceNameClassTaskFactory());
		
        selements.add(e);
	}	
	private void nameClassPlusForChoice(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(NAME_CLASS, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_NC, internalIndexedData  /*"nameClass","RELAXNG Specification 3.Full Syntax: name class"*/);
			}builder.endLevel();
			builder.buildOneOrMore(InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_NC_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
			
			builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: foreign element"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: foreign element"*/);
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC, internalIndexedData  /*"interleaving of name class elements with foreign elements","RELAXNG Specification 3.Full Syntax: name class content"*/);	
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
				
				builder.buildRef(NS_ATTRIBUTE, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
				builder.buildRef(DL_ATTRIBUTE, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE, internalIndexedData  /*"optional attributes datatypeLibrary and ns","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
				builder.buildRef(FOREIGN_ATTRIBUTES, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES, internalIndexedData   /*"foreign attributes","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
				
			}builder.endLevel();
			builder.buildGroup(InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT, internalIndexedData, false  /*"attributes and elements group","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
			builder.buildName(XMLConstants.RELAXNG_NS_URI, "except", InternalIndexedData.EXCEPT_NC_ELEMENT_NAME, internalIndexedData, false /*"name","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.EXCEPT_NC_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: except in name class context"*/);
		
		SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);
		endElementTaskFactory.put(e, new ExceptNameClassTaskFactory());
        refDefinitionTopPattern[EXCEPT_NAME_CLASS] = e;
        
        selements.add(e);
    }
    
    private void nameClassPlusForExcept(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(NAME_CLASS, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_NC, internalIndexedData  /*"nameClass","RELAXNG Specification 3.Full Syntax: name class"*/);
			}builder.endLevel();
			builder.buildOneOrMore(InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_NC_PLUS, internalIndexedData  /*"oneOrMore","RELAXNG Specification 3.Full Syntax: oneOrMore"*/);
			
			builder.startLevel();{
                builder.buildRef(FOREIGN_ELEMENT, InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT, internalIndexedData  /*"foreignElement","RELAXNG Specification 3.Full Syntax: foreign element"*/);
            }builder.endLevel();
            builder.buildZeroOrMore(InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR, internalIndexedData  /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: foreign element"*/);
		}builder.endLevel();				
		builder.buildInterleave(InternalIndexedData.EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC, internalIndexedData  /*"interleaving of name class elements with foreign elements","RELAXNG Specification 3.Full Syntax: name class content"*/);	
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
            builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD,  InternalIndexedData.NS_ATTRIBUTE, internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: ns attribute"*/);
            SAttribute ns = (SAttribute)builder.getCurrentPattern();
            attributeTaskFactory.put(ns, nsTaskFactory);

            sattributes.add(ns);
        }builder.endLevel();				
        builder.buildOptional(InternalIndexedData.NS_ATTRIBUTE_SQUARE, internalIndexedData    /*"optional","RELAXNG Specification 3.Full Syntax: ns attribute"*/);
        
        refDefinitionTopPattern[NS_ATTRIBUTE] = builder.getCurrentPattern();
	}
	
	private void defineOptionalDlAttribute()  throws DatatypeException{
	    builder.startBuild();	
        builder.startLevel();{
            builder.startLevel();{
                builder.buildName("","datatypeLibrary", InternalIndexedData.DL_ATTRIBUTE_NAME, internalIndexedData, false     /*"name","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute"*/);
                builder.buildData(internalLibrary.createDatatype("datatypeLibraryURI"), InternalIndexedData.DL_ATTRIBUTE_VALUE, internalIndexedData     /*"data","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute"*/);
            }builder.endLevel();
            builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.DL_ATTRIBUTE, internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute"*/);
            SAttribute datatypeLibrary = (SAttribute)builder.getCurrentPattern();
            attributeTaskFactory.put(datatypeLibrary, datatypeLibraryTaskFactory);

            sattributes.add(datatypeLibrary);
        }builder.endLevel();				
        builder.buildOptional(InternalIndexedData.DL_ATTRIBUTE_SQUARE, internalIndexedData    /*"optional","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute"*/);
    
        refDefinitionTopPattern[DL_ATTRIBUTE] = builder.getCurrentPattern();
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
		builder.buildElement(elementIndex++, InternalIndexedData.FOREIGN_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: foreign element"*/);
		
        SElement e = (SElement)builder.getCurrentPattern();
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
				builder.buildRef(ANY_ATTRIBUTE, InternalIndexedData.FOREIGN_ELEMENT_CONTENT_ANY_ATTRIBUTE, internalIndexedData   /*"any attribute","RELAXNG Specification 3.Full Syntax: any attribute"*/);
				builder.buildText(InternalIndexedData.FOREIGN_ELEMENT_CONTENT_TEXT, internalIndexedData, false    /*"text","RELAXNG Specification 3.Full Syntax: text"*/);
				builder.buildRef(ANY_ELEMENT, InternalIndexedData.FOREIGN_ELEMENT_CONTENT_ANY_ELEMENT, internalIndexedData   /*"anyElement","RELAXNG Specification 3.Full Syntax: any element"*/);
			}builder.endLevel();
			builder.buildChoicePattern(InternalIndexedData.FOREIGN_ELEMENT_CONTENT_CHOICE, internalIndexedData, false   /*"choice","RELAXNG Specification 3.Full Syntax: any content"*/);
		}builder.endLevel();
		builder.buildZeroOrMore(InternalIndexedData.FOREIGN_ELEMENT_CONTENT_CHOICE_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: any content"*/);
	}
	
	private void defineAnyElement(){
		builder.startBuild();
		builder.startLevel();{
			anyContentForAny();
			builder.buildAnyName(InternalIndexedData.ANY_ELEMENT_NAME, internalIndexedData /*"anyName","RELAXNG Specification 3.Full Syntax: any element"*/);
		}builder.endLevel();
		builder.buildElement(elementIndex++, InternalIndexedData.ANY_ELEMENT, internalIndexedData /*"element","RELAXNG Specification 3.Full Syntax: any element"*/);
		
        SElement e = (SElement)builder.getCurrentPattern();
		needsStartTask.put(e, null);        
		endElementTaskFactory.put(e, new ForeignElementTaskFactory());
		refDefinitionTopPattern[ANY_ELEMENT] = e;
		
        selements.add(e);
	}
	private void anyContentForAny(){		
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(ANY_ATTRIBUTE, InternalIndexedData.ANY_ELEMENT_CONTENT_ANY_ATTRIBUTE, internalIndexedData   /*"any attribute","RELAXNG Specification 3.Full Syntax: any attribute"*/);
				builder.buildText(InternalIndexedData.ANY_ELEMENT_CONTENT_TEXT, internalIndexedData, false    /*"text","RELAXNG Specification 3.Full Syntax: text"*/);
				builder.buildRef(ANY_ELEMENT, InternalIndexedData.ANY_ELEMENT_CONTENT_ANY_ELEMENT, internalIndexedData   /*"anyElement","RELAXNG Specification 3.Full Syntax: any element"*/);
			}builder.endLevel();
			builder.buildChoicePattern(InternalIndexedData.ANY_ELEMENT_CONTENT_CHOICE, internalIndexedData, false   /*"choice","RELAXNG Specification 3.Full Syntax: any content"*/);
		}builder.endLevel();
		builder.buildZeroOrMore(InternalIndexedData.ANY_ELEMENT_CONTENT_CHOICE_STAR, internalIndexedData   /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: any content"*/);
	}
	
	private void defineForeignAttributes(){
	    builder.startBuild();	
        builder.startLevel();{
			builder.startLevel();{
				anyNameExceptNullOrRNG();	
				builder.buildText(InternalIndexedData.FOREIGN_ATTRIBUTE_VALUE, internalIndexedData, false  /*"text","RELAXNG Specification 3.Full Syntax: foreign attribute"*/);
			}builder.endLevel();
			builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.FOREIGN_ATTRIBUTE, internalIndexedData    /*"attribute","RELAXNG Specification 3.Full Syntax: foreign attribute"*/);
            SAttribute foreign = (SAttribute)builder.getCurrentPattern();
			attributeTaskFactory.put(foreign, foreignAttributeTaskFactory);

            sattributes.add(foreign);
		}builder.endLevel();				
		builder.buildZeroOrMore(InternalIndexedData.FOREIGN_ATTRIBUTE_STAR, internalIndexedData /*"zeroOrMore","RELAXNG Specification 3.Full Syntax: foreign attribute"*/);
        
        SPattern p = builder.getCurrentPattern();
        refDefinitionTopPattern[FOREIGN_ATTRIBUTES] = p;
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
		builder.buildAttribute(attributeIndex++, InternalIndexedData.NO_RECORD, InternalIndexedData.ANY_ATTRIBUTE, internalIndexedData /*"attribute","RELAXNG Specification 3.Full Syntax: any attribute"*/);
        SAttribute any = (SAttribute)builder.getCurrentPattern();
        refDefinitionTopPattern[ANY_ATTRIBUTE] = any;

        sattributes.add(any);
    }
    //**************************************************************************
	//END FOREIGN ELEMENTS METHODS *********************************************
	//**************************************************************************
    
}
