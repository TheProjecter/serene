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

import javax.xml.XMLConstants;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.DatatypeLibraryFinder;

import serene.validation.schema.simplified.components.SPattern;
//import serene.validation.schema.simplified.components.NameClass;
import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.validation.schema.simplified.SimplifiedComponentBuilder;
import serene.validation.schema.simplified.SimplifiedModel;

import serene.parser.AnyNamePool;
import serene.parser.AttributeWithNameClassPool;
import serene.parser.AttributeWithNameInstancePool;
import serene.parser.ChoiceNameClassPool;
import serene.parser.ChoicePatternPool;
import serene.parser.DataPool;
import serene.parser.DefinePool;
import serene.parser.DivGrammarContentPool;
import serene.parser.DivIncludeContentPool;
import serene.parser.ElementWithNameClassPool;
import serene.parser.ElementWithNameInstancePool;
import serene.parser.EmptyPool;
import serene.parser.ExceptNameClassPool;
import serene.parser.ExceptPatternPool;
import serene.parser.ExternalRefPool;
import serene.parser.GrammarPool;
import serene.parser.GroupPool;
import serene.parser.IncludePool;
import serene.parser.InterleavePool;
import serene.parser.ListPatternPool;
import serene.parser.MixedPool;
import serene.parser.NamePool;
import serene.parser.NotAllowedPool;
import serene.parser.NsNamePool;
import serene.parser.OneOrMorePool;
import serene.parser.OptionalPool;
import serene.parser.ParamPool;
import serene.parser.ParentRefPool;
import serene.parser.RefPool;
import serene.parser.StartLevelPool;
import serene.parser.StartPool;
import serene.parser.TextPool;
import serene.parser.ValuePool;
import serene.parser.ZeroOrMorePool;

import serene.parser.ForeignElementTaskPool;

import serene.parser.StartLevelPool;

import serene.parser.RNGParseElementTaskPool;
import serene.parser.RNGParseBindingPool;

import serene.bind.AttributeTaskPool;
import serene.bind.ElementTaskPool;

import serene.Constants;

import sereneWrite.MessageWriter;

//********************
// ref indexing legend
//********************
// pattern = 0
// nameClass = 1
// grammarContent = 2
// includeContent = 3
// foreignElement = 4
// anyElement = 5


//********************
// TODO
// start() and define()
// must become startGrammarContent() and defineGrammarContent()
// in order to be able to add the creation of corresponding 
// command objects that need to be used for creating the ParsedModel 
// when parsing a schema.
//*********************

class RNGDirector{
	HashMap<SElement, ElementTaskPool> startElementTaskPool;
	HashMap<SElement, ElementTaskPool> endElementTaskPool;
	HashMap<SAttribute, AttributeTaskPool> attributeTaskPool;
	
	SimplifiedComponentBuilder builder;
	SPattern rngStartTopPattern;
	SPattern includeStartTopPattern;
	SPattern externalRefStartTopPattern;
	SPattern[] refDefinitionTopPattern;
	
	private SAttribute ns;
	private SAttribute datatypeLibrary;
	private SAttribute name;
	private SAttribute combine;
	private SAttribute type;
	private SAttribute href;  
    private SAttribute any;
    private SAttribute foreign;  
	private StartLevelPool startLevelPool;
    
	DatatypeLibrary internalLibrary;
    DatatypeLibrary nativeLibrary;
	
	MessageWriter debugWriter;
	
	RNGDirector(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
                
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
        	    
	}
	
	
	void createModels(SimplifiedComponentBuilder builder)  throws DatatypeException{
		this.builder = builder;
		
		refDefinitionTopPattern = new SPattern[6];
		startElementTaskPool = new HashMap<SElement, ElementTaskPool>();
		endElementTaskPool = new HashMap<SElement, ElementTaskPool>();
		attributeTaskPool = new HashMap<SAttribute, AttributeTaskPool>();
		
		startLevelPool = new StartLevelPool(debugWriter);
		
		startGrammar();
		definePattern();
		defineGrammarContent();
		defineIncludeContent();
		defineNameClass();
		defineForeign();
		defineAnyElement();
	}
	
	SimplifiedModel getRNGModel(){
		SPattern[] start = {rngStartTopPattern};
		return new SimplifiedModel(start,
									refDefinitionTopPattern,
									null,
									debugWriter);
	}
	
	SimplifiedModel getIncludeModel(){
		SPattern[] start = {includeStartTopPattern};
		return new SimplifiedModel(start,
									refDefinitionTopPattern,									
									null,
									debugWriter);
	}
	
	SimplifiedModel getExternalRefModel(){
		SPattern[] start = {externalRefStartTopPattern};
		return new SimplifiedModel(start,
									refDefinitionTopPattern,
									null,
									debugWriter);
	}
	
	RNGParseBindingPool getBindingModelPool(){
		return new RNGParseBindingPool(startElementTaskPool, attributeTaskPool, endElementTaskPool, debugWriter);
	}
	
	private void startGrammar(){
		builder.startBuild();	
		builder.buildRef(0,"pattern","RELAXNG Specification 3.Full Syntax");			
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
		builder.buildChoicePattern("choice pattern","RELAXNG Specification 3.Full Syntax");	
		SPattern[] cp = builder.getAllCurrentPatterns();
		refDefinitionTopPattern[0] = cp[0];
	}	
	
	private void defineGrammarContent() throws DatatypeException{
		builder.startBuild();
		builder.startLevel();{
			start();
			define();
			divGrammarContent();
			include();
		}builder.endLevel();
		builder.buildChoicePattern("choice grammar content","RELAXNG Specification 3.Full Syntax");
		SPattern[] cp = builder.getAllCurrentPatterns();
		refDefinitionTopPattern[2] = cp[0];
	}
	
	private void defineIncludeContent() throws DatatypeException{
		builder.startBuild();
		builder.startLevel();{
			start();
			define();
			divIncludeContent();
		}builder.endLevel();
		builder.buildChoicePattern("choice include content","RELAXNG Specification 3.Full Syntax");
		SPattern[] cp = builder.getAllCurrentPatterns();
		refDefinitionTopPattern[3] = cp[0];
	}	
	
	private void defineNameClass()  throws DatatypeException{		
		builder.startBuild();
		builder.startLevel();{
			name();
			anyName();
			nsName();
			choiceNameClass();
		}builder.endLevel();
		builder.buildChoicePattern("choice name class","RELAXNG Specification 3.Full Syntax");
		SPattern[] cp = builder.getAllCurrentPatterns();
		refDefinitionTopPattern[1] = cp[0];
	}
	
	private void defineForeign(){
		builder.startBuild();
		builder.startLevel();{
			anyContent();
			anyNameExceptRNG();
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: foreign element");
		//SPattern[] e = builder.getAllCurrentPatterns();
		//refDefinitionTopPattern[4] = e[0];
        
        SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
        refDefinitionTopPattern[4] = e;
		endElementTaskPool.put(e, new ForeignElementTaskPool(any, debugWriter));
	}
	
	private void defineAnyElement(){
		builder.startBuild();
		builder.startLevel();{
			anyContent();
			builder.buildAnyName("anyName","RELAXNG Specification 3.Full Syntax: any element");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: any element");
		//SPattern[] e = builder.getAllCurrentPatterns();
		//refDefinitionTopPattern[5] = e[0];
        
        SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
        refDefinitionTopPattern[5] = e;
		endElementTaskPool.put(e, new ForeignElementTaskPool(any, debugWriter));
	}
	
	//**************************************************************************
	//START PATTERN METHODS ****************************************************
	//**************************************************************************
	private void elementWithNameClass() throws DatatypeException{		
		builder.startLevel();{			
			builder.startLevel();{				
				nameClassPatternPlus();
				commonAttributes();
				foreignAttributes();				
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: element with name class child");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"element","name","RELAXNG Specification 3.Full Syntax: element with name class child");
		}builder.endLevel();		
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: element with name class child");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new ElementWithNameClassPool(ns, datatypeLibrary, foreign, debugWriter));
	}			
	
	private void elementWithNameInstance() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				patternPlus();		
				nameAttributeQName();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: element with name attribute");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"element","name","RELAXNG Specification 3.Full Syntax: element with name attribute");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: element with name attribute");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new ElementWithNameInstancePool(ns, datatypeLibrary, name, foreign, debugWriter));
	}
	
	private void attributeWithNameClass() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameClassPatternSquare();
				commonAttributes();
				foreignAttributes();				
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: attribute with name class child");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"attribute","name","RELAXNG Specification 3.Full Syntax: attribute with name class child");
		}builder.endLevel();		
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: attribute with name class child");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new AttributeWithNameClassPool(ns, datatypeLibrary, foreign, debugWriter));
	}			
	
	private void attributeWithNameInstance() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{				
				nameAttributeQName();
				patternSquare();	
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: attribute with name attribute");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"attribute","name","RELAXNG Specification 3.Full Syntax: attribute with name attribute");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: attribute with name attribute");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new AttributeWithNameInstancePool(ns, datatypeLibrary, name, foreign, debugWriter));
	}
	
	private void group() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{				
				patternPlus();		
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: group");			
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"group","name","RELAXNG Specification 3.Full Syntax: group");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: group");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new GroupPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void interleave() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				patternPlus();		
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: interleave");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"interleave","name","RELAXNG Specification 3.Full Syntax: interleave");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: interleave");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new InterleavePool(ns, datatypeLibrary, foreign, debugWriter));
	}		
	
	private void choicePattern() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				patternPlus();		
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: choice in pattern context");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"choice","name","RELAXNG Specification 3.Full Syntax: choice in pattern context");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: choice in pattern context");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new ChoicePatternPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void optional() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				patternPlus();		
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: optional");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"optional","name","RELAXNG Specification 3.Full Syntax: optional");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: optional");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new OptionalPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void zeroOrMore() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				patternPlus();		
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: zeroOrMore");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"zeroOrMore","name","RELAXNG Specification 3.Full Syntax: zeroOrMore");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: zeroOrMore");	
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new ZeroOrMorePool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void oneOrMore() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				patternPlus();		
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: oneOrMore");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"oneOrMore","name","RELAXNG Specification 3.Full Syntax: oneOrMore");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: oneOrMore");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new OneOrMorePool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void list() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				patternPlus();		
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: list");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"list","name","RELAXNG Specification 3.Full Syntax: list");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: list");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new ListPatternPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void mixed() throws DatatypeException{
		builder.startLevel();{			
			builder.startLevel();{				
				patternPlus();		
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: mixed");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"mixed","name","RELAXNG Specification 3.Full Syntax: mixed");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: mixed");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new MixedPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void ref() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameAttributeNCName();
				commonAttributes();
				foreignAttributes();
                foreignStar();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: ref");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"ref","name","RELAXNG Specification 3.Full Syntax: ref");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: ref");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new RefPool(ns, datatypeLibrary, name, foreign, debugWriter));
	}
	
	private void parentRef() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameAttributeNCName();
				commonAttributes();
				foreignAttributes();
                foreignStar();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: parentRef");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"parentRef","name","RELAXNG Specification 3.Full Syntax: parentRef");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: parentRef");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new ParentRefPool(ns, datatypeLibrary, name, foreign, debugWriter));
	}
	
	private void empty() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				commonAttributes();
				foreignAttributes();
                foreignStar();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: empty");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"empty","name","RELAXNG Specification 3.Full Syntax: empty");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: empty");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new EmptyPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void text() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				commonAttributes();
				foreignAttributes();
                foreignStar();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: text");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"text","name","RELAXNG Specification 3.Full Syntax: text");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: text");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new TextPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void value() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				builder.buildText("text","RELAXNG Specification 3.Full Syntax: value");
				typeAttributeNCNameSquare();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and text group","RELAXNG Specification 3.Full Syntax: value");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"value","name","RELAXNG Specification 3.Full Syntax: value");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: value");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new ValuePool(ns, datatypeLibrary, type, foreign, debugWriter));
	}
	
	private void data() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				paramStarExceptPatternSquare();
				typeAttributeNCName();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: data");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"data","name","RELAXNG Specification 3.Full Syntax: data");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: data");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new DataPool(ns, datatypeLibrary, type, foreign, debugWriter));
	}
	
	private void notAllowed() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				commonAttributes();
				foreignAttributes();
                foreignStar();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: notAllowed");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"notAllowed","name","RELAXNG Specification 3.Full Syntax: notAllowed");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: notAllowed");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new NotAllowedPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void externalRef() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				hrefAttribute();
				commonAttributes();
				foreignAttributes();
                foreignStar();				
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: externalRef");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"externalRef","name","RELAXNG Specification 3.Full Syntax: externalRef");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: externalRef");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new ExternalRefPool(ns, datatypeLibrary, href, foreign, debugWriter));
	}
	
	private void grammar() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				grammarContentStar();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: grammar");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"grammar","name","RELAXNG Specification 3.Full Syntax: grammar");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: grammar");
		
		SElement e = (SElement)builder.getCurrentPattern();
		includeStartTopPattern = e;
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new GrammarPool(ns, datatypeLibrary, foreign, debugWriter));
	}		
	//**************************************************************************
	//END PATTERN METHODS ******************************************************
	//**************************************************************************
 
	//**************************************************************************
	//START TOP COMPONENT METHODS **********************************************
	//**************************************************************************
	private void start() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				pattern();
				combineAttributeSquare();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: start");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"start","name","RELAXNG Specification 3.Full Syntax: start");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: start");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new StartPool(ns, datatypeLibrary, combine, foreign, debugWriter));
	}
	
	private void define() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				patternPlus();
				nameAttributeNCName();
				combineAttributeSquare();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: define");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"define","name","RELAXNG Specification 3.Full Syntax: define");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: define");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new DefinePool(ns, datatypeLibrary, name, combine, foreign, debugWriter));
	}
	
	private void divGrammarContent() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				grammarContentStar();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: div in grammar context");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"div","name","RELAXNG Specification 3.Full Syntax: div in grammar context");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: div in grammar context");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new DivGrammarContentPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void divIncludeContent() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				includeContentStar();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: div in include context");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"div","name","RELAXNG Specification 3.Full Syntax: div in include context");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: div in include context");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new DivIncludeContentPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void include() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				includeContentStar();
				hrefAttribute();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: include");			
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"include","name","RELAXNG Specification 3.Full Syntax: include");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: include");		
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new IncludePool(ns, datatypeLibrary, href, foreign, debugWriter));
	}
	//**************************************************************************
	//END TOP COMPONENT METHODS ************************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START NAME CLASS METHODS *************************************************
	//**************************************************************************
	private void name() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				builder.buildData(internalLibrary.createDatatype("QName"),"data","RELAXNG Specification 3.Full Syntax: name");
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: name");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"name","name","RELAXNG Specification 3.Full Syntax: name");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: name");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new NamePool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void anyName() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				exceptNameClassSquare();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: anyName");			
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"anyName","name","RELAXNG Specification 3.Full Syntax: anyName");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: anyName");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new AnyNamePool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void nsName() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				exceptNameClassSquare();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: nsName");			
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"nsName","name","RELAXNG Specification 3.Full Syntax: nsName");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new NsNamePool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void choiceNameClass() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{	
				nameClassPlus();
				commonAttributes();			
				foreignAttributes();				
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: choice in name class context");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"choice","name","RELAXNG Specification 3.Full Syntax: choice in name class context");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: choice in name class context");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new ChoiceNameClassPool(ns, datatypeLibrary, foreign, debugWriter));
	}	
	//**************************************************************************
	//END NAME CLASS METHODS ***************************************************
	//**************************************************************************
	
	//**************************************************************************
	//START OTHER COMPONENT METHODS ********************************************
	//**************************************************************************
	private void param() throws DatatypeException{		
		builder.startLevel();{
			builder.startLevel();{				
				builder.buildText("text","RELAXNG Specification 3.Full Syntax: param");
				nameAttributeNCName();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and text group","RELAXNG Specification 3.Full Syntax: param");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"param","name","RELAXNG Specification 3.Full Syntax: param");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: param");				
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new ParamPool(ns, datatypeLibrary, name, foreign, debugWriter));
	}
	
	private void exceptPattern() throws DatatypeException{
		builder.startLevel();{	
			builder.startLevel();{				
				patternPlus();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: except in pattern context");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"except","name","RELAXNG Specification 3.Full Syntax: except in pattern context");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: except in pattern context");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new ExceptPatternPool(ns, datatypeLibrary, foreign, debugWriter));
	}
	
	private void exceptNameClass() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{				
				nameClassPlus();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: except in name class context");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"except","name","RELAXNG Specification 3.Full Syntax: except in name class context");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: except in name class context");
		
		SElement e = (SElement)builder.getCurrentPattern();
		startElementTaskPool.put(e, startLevelPool);
		endElementTaskPool.put(e, new ExceptNameClassPool(ns, datatypeLibrary, foreign, debugWriter));
	}	
	//**************************************************************************
	//END OTHER COMPONENT METHODS **********************************************
	//**************************************************************************
	
	//**************************************************************************
	//START COMMON METHODS ****************************************************
	//**************************************************************************	
	private void commonAttributes() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				builder.buildName("","ns","name","RELAXNG Specification 3.Full Syntax: ns attribute");	
				builder.buildData(nativeLibrary.createDatatype("token"),"data","RELAXNG Specification 3.Full Syntax: ns attribute");
			}builder.endLevel();
			builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: ns attribute");
			ns = (SAttribute)builder.getCurrentPattern();
			attributeTaskPool.put(ns, null);		
		}builder.endLevel();				
		builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax: ns attribute");
		
		builder.startLevel();{
			builder.startLevel();{
				builder.buildName("","datatypeLibrary","name","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute");				
				builder.buildData(internalLibrary.createDatatype("datatypeLibraryURI"),"data","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute");
			}builder.endLevel();
			builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute");
			datatypeLibrary = (SAttribute)builder.getCurrentPattern();
			attributeTaskPool.put(datatypeLibrary, null);
		}builder.endLevel();				
		builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute");
	}
	
	private void foreignAttributes(){
		builder.startLevel();{
			builder.startLevel();{
				anyNameExceptNullOrRNG();	
				builder.buildText("text","RELAXNG Specification 3.Full Syntax: foreign attribute");
			}builder.endLevel();
			builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: foreign attribute");
            foreign = (SAttribute)builder.getCurrentPattern();
			attributeTaskPool.put(foreign, null);	
		}builder.endLevel();				
		builder.buildZeroOrMore("zeroOrMore","RELAXNG Specification 3.Full Syntax: foreign attribute");
	}
	
	private void nameAttributeQName()  throws DatatypeException{
		builder.startLevel();{
			builder.buildName("","name","name","RELAXNG Specification 3.Full Syntax: name attribute");
			builder.buildData(internalLibrary.createDatatype("QName"),"data","RELAXNG Specification 3.Full Syntax: name attribute");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: name attribute");
		name = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(name, null);
	}
	
	private void nameAttributeNCName()  throws DatatypeException{
		builder.startLevel();{
			builder.buildName("","name","name","RELAXNG Specification 3.Full Syntax: name attribute");				
			builder.buildData(internalLibrary.createDatatype("NCName"),"attribute","RELAXNG Specification 3.Full Syntax: name attribute");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: name attribute");
		name = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(name, null);
	}
	
	private void typeAttributeNCNameSquare() throws DatatypeException{
		builder.startLevel();{
			typeAttributeNCName();			
		}builder.endLevel();				
		builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax: type attribute");
	}
	
	private void typeAttributeNCName() throws DatatypeException{
		builder.startLevel();{
			builder.buildName("","type","name","RELAXNG Specification 3.Full Syntax: type attribute");				
			builder.buildData(internalLibrary.createDatatype("NCName"),"data","RELAXNG Specification 3.Full Syntax: type attribute");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: type attribute");
		type = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(type, null);
	}
	
	private void hrefAttribute() throws DatatypeException{
		builder.startLevel();{
			builder.buildName("","href","name","RELAXNG Specification 3.Full Syntax: href attribute");
			builder.buildData(internalLibrary.createDatatype("hrefURI"),"data","RELAXNG Specification 3.Full Syntax: href attribute");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: href attribute");
		href = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(href, null);
	}
	
	private void combineAttributeSquare() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				builder.buildName("","combine","name","RELAXNG Specification 3.Full Syntax: combine attribute");
				builder.buildData(internalLibrary.createDatatype("combine"), "value","RELAXNG Specification 3.Full Syntax: combine attribute");
			}builder.endLevel();
			builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: combine attribute");
			combine = (SAttribute)builder.getCurrentPattern();
			attributeTaskPool.put(combine, null);
		}builder.endLevel();				
		builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax: combine attribute");
	}
	
	
	
	private void nameClassPatternPlus(){		
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(1,"nameClass","RELAXNG Specification 3.Full Syntax");
				builder.startLevel();{
					builder.buildRef(0,"pattern","RELAXNG Specification 3.Full Syntax");						
				}builder.endLevel();
				builder.buildOneOrMore("oneOrMore","RELAXNG Specification 3.Full Syntax");				
			}builder.endLevel();
			builder.buildGroup("group name class, patterns","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();				
		builder.buildInterleave("interleave name class, patterns group and foreign elements","RELAXNG Specification 3.Full Syntax");		
	}
	
	private void patternPlus(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(0,"pattern","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildOneOrMore("oneOrMore","RELAXNG Specification 3.Full Syntax");			
			foreignStar();
		}builder.endLevel();				
		builder.buildInterleave("interleave patterns and foreign elements","RELAXNG Specification 3.Full Syntax");	
	}
	
	private void nameClassPatternSquare(){
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(1,"nameClass","RELAXNG Specification 3.Full Syntax");
				builder.startLevel();{
					builder.buildRef(0,"pattern","RELAXNG Specification 3.Full Syntax");						
				}builder.endLevel();
				builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildGroup("group name class, pattern group","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();				
		builder.buildInterleave("interleave name class, pattern group and foreign elements","RELAXNG Specification 3.Full Syntax");		
	}
	
	private void patternSquare(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(0,"pattern","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();				
		builder.buildInterleave("interleave patterns and foreign elements","RELAXNG Specification 3.Full Syntax");	
	}
	
	private void paramStarExceptPatternSquare() throws DatatypeException{
		builder.startLevel();{
			builder.startLevel();{
				builder.startLevel();{
					param();						
				}builder.endLevel();
				builder.buildZeroOrMore("zeroOrMore","RELAXNG Specification 3.Full Syntax");
				builder.startLevel();{
					exceptPattern();			
				}builder.endLevel();
				builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildGroup("group param except","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();
		builder.buildInterleave("interleave param, except group and foreign elements","RELAXNG Specification 3.Full Syntax");					
	}	

	private void pattern(){
		builder.startLevel();{
			builder.buildRef(0,"pattern","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();
		builder.buildInterleave("interleave pattern and foreign elements","RELAXNG Specification 3.Full Syntax");
	}
	
	private void grammarContentStar(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(2,"grammarContent","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildZeroOrMore("zeroOrMore","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();				
		builder.buildInterleave("interleave grammar content and foreign elements","RELAXNG Specification 3.Full Syntax");	
	}
	
	private void includeContentStar(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(3,"includeContent","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildZeroOrMore("zeroOrMore","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();				
		builder.buildInterleave("interleave include content and foreign elements","RELAXNG Specification 3.Full Syntax");	
	}

	private void exceptNameClassSquare() throws DatatypeException{	
		builder.startLevel();{
			builder.startLevel();{
				exceptNameClass();			
			}builder.endLevel();
			builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();
		builder.buildInterleave("interleave except and foreign elements","RELAXNG Specification 3.Full Syntax");
	}
	
	private void nameClassPlus(){				
		builder.startLevel();{
			builder.startLevel();{
				builder.buildRef(1,"nameClass","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildOneOrMore("oneOrMore","RELAXNG Specification 3.Full Syntax");
			foreignStar();
		}builder.endLevel();				
		builder.buildInterleave("interleave name classes and foreign elements","RELAXNG Specification 3.Full Syntax");	
	}

	private void foreignStar(){//zeroOrMore elements from any namespace except rng
		builder.startLevel();{
			builder.buildRef(4,"foreignElement","RELAXNG Specification 3.Full Syntax");
		}builder.endLevel();
		builder.buildZeroOrMore("zeroOrMore","RELAXNG Specification 3.Full Syntax");
	}
	
	private void anyNameExceptRNG(){
		builder.startLevel();{
			builder.startLevel();{
				builder.buildNsName(XMLConstants.RELAXNG_NS_URI,"nsName","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildExceptNameClass("except","RELAXNG Specification 3.Full Syntax");
		}builder.endLevel();
		builder.buildAnyName("anyName","RELAXNG Specification 3.Full Syntax");
	}
	
	private void anyNameExceptNullOrRNG(){
		builder.startLevel();{
			builder.startLevel();{
				builder.startLevel();{
					builder.buildNsName(XMLConstants.RELAXNG_NS_URI,"nsName","RELAXNG Specification 3.Full Syntax");
					builder.buildNsName(XMLConstants.NULL_NS_URI,"nsName","RELAXNG Specification 3.Full Syntax");
				}builder.endLevel();
				builder.buildChoiceNameClass("choice","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildExceptNameClass("except","RELAXNG Specification 3.Full Syntax");
		}builder.endLevel();
		builder.buildAnyName("anyName","RELAXNG Specification 3.Full Syntax");
	}
	private void anyContent(){		
		builder.startLevel();{
			builder.startLevel();{
				anyAttribute();
				builder.buildText("text","RELAXNG Specification 3.Full Syntax");
				builder.buildRef(5,"anyElement","RELAXNG Specification 3.Full Syntax");
			}builder.endLevel();
			builder.buildChoicePattern("choice","RELAXNG Specification 3.Full Syntax");
		}builder.endLevel();
		builder.buildZeroOrMore("zeroOrMore","RELAXNG Specification 3.Full Syntax");
	}
	
	private void anyAttribute(){
		builder.startLevel();{
			builder.buildAnyName("anyName","RELAXNG Specification 3.Full Syntax");
			builder.buildText("text","RELAXNG Specification 3.Full Syntax");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax");
        any = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(any, null);	
	}	
	//**************************************************************************
	//END COMMON METHODS ******************************************************
	//**************************************************************************
}
