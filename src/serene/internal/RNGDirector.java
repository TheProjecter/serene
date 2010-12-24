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

import java.util.Map;
import java.util.HashMap;

import javax.xml.XMLConstants;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

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

import serene.parser.StartLevelPool;

import serene.parser.RNGParseElementTaskPool;
import serene.parser.RNGParseBindingPool;

import serene.bind.AttributeTaskPool;

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


class RNGDirector{	

    String INTERNAL_LIBRARY = "http://serenerng.org/datatype/internal";  
    
	HashMap<SElement, RNGParseElementTaskPool> startElementTaskPool;
	HashMap<SElement, RNGParseElementTaskPool> endElementTaskPool;
	HashMap<SAttribute, AttributeTaskPool> attributeTaskPool;
	
	SimplifiedComponentBuilder builder;
	SPattern rngStartTopPattern;
	SPattern includeStartTopPattern;
	SPattern externalRefStartTopPattern;
	SPattern[] refDefinitionTopPattern;

	Map<String, DatatypeLibrary> datatypeLibraries;
	
	private SAttribute ns;
	private SAttribute datatypeLibrary;
	private SAttribute name;
	private SAttribute combine;
	private SAttribute type;
	private SAttribute href;
	private StartLevelPool startLevelPool;
	
	private DatatypeLibraryFactory datatypeLibraryFactory;
	
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

        datatypeLibraryFactory = new DatatypeLibraryFinder(cl);
	}
	
	
	void createModels(SimplifiedComponentBuilder builder){
		this.builder = builder;
		
		refDefinitionTopPattern = new SPattern[6];
		startElementTaskPool = new HashMap<SElement, RNGParseElementTaskPool>();
		endElementTaskPool = new HashMap<SElement, RNGParseElementTaskPool>();
		attributeTaskPool = new HashMap<SAttribute, AttributeTaskPool>();
		
		startLevelPool = new StartLevelPool(debugWriter);
		
		startGrammar();
		definePattern();
		defineGrammarContent();
		defineIncludeContent();
		defineNameClass();
		defineForeign();
		defineAnyElement();
		
		
		createDatatypeLibraries();		
	}
	
	SimplifiedModel getRNGModel(){
		SPattern[] start = {rngStartTopPattern};
		return new SimplifiedModel(start,
									refDefinitionTopPattern,
									null,
									datatypeLibraries,
									debugWriter);
	}
	
	SimplifiedModel getIncludeModel(){
		SPattern[] start = {includeStartTopPattern};
		return new SimplifiedModel(start,
									refDefinitionTopPattern,									
									null,
									datatypeLibraries,
									debugWriter);
	}
	
	SimplifiedModel getExternalRefModel(){
		SPattern[] start = {externalRefStartTopPattern};
		return new SimplifiedModel(start,
									refDefinitionTopPattern,
									null,
									datatypeLibraries,
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
	
	private void definePattern(){
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
	
	private void defineGrammarContent(){
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
	
	private void defineIncludeContent(){
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
	
	private void defineNameClass(){		
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
		SPattern[] e = builder.getAllCurrentPatterns();
		refDefinitionTopPattern[4] = e[0];
	}
	
	private void defineAnyElement(){
		builder.startBuild();
		builder.startLevel();{
			anyContent();
			builder.buildAnyName("anyName","RELAXNG Specification 3.Full Syntax: any element");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: any element");
		SPattern[] e = builder.getAllCurrentPatterns();
		refDefinitionTopPattern[5] = e[0];
	}
	
	private void createDatatypeLibraries(){
		datatypeLibraries = new HashMap<String, DatatypeLibrary>();
		
		datatypeLibraries.put("", datatypeLibraryFactory.createDatatypeLibrary(""));
		datatypeLibraries.put(INTERNAL_LIBRARY, datatypeLibraryFactory.createDatatypeLibrary(INTERNAL_LIBRARY));
		
		// TODO create internal library	
	}
	//**************************************************************************
	//START PATTERN METHODS ****************************************************
	//**************************************************************************
	private void elementWithNameClass(){		
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
		endElementTaskPool.put(e, new ElementWithNameClassPool(ns, datatypeLibrary, debugWriter));
	}			
	/*Some test stuff that I didn't modify on 25.09.2009(see log)
	
	//refNameClassPatternPlus();
				
	private void defineNameClassPatternPlus(){
		System.out.println("1elementWithNameClass");
		builder.startLevel();{
			builder.startLevel();{
				builder.startLevel();{
					builder.buildRef(1);
					builder.buildRef(1);	
					builder.buildRef(0);			
				}builder.endLevel();
				builder.buildGroup("group","RELAXNG Specification 3.Full Syntax");			
			}builder.endLevel();				
			builder.buildChoicePattern();
		}builder.endLevel();
		builder.buildDefine("elementWithNameClass");
	}
	private void refNameClassPatternPlus(){
		System.out.println("2elementWithNameClass");
		builder.startLevel();{
			builder.buildRef("elementWithNameClass");
		}builder.endLevel();
		builder.buildOneOrMore();
	}*/
	
	private void elementWithNameInstance(){
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
		endElementTaskPool.put(e, new ElementWithNameInstancePool(ns, datatypeLibrary, name, debugWriter));
	}
	
	private void attributeWithNameClass(){
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
		endElementTaskPool.put(e, new AttributeWithNameClassPool(ns, datatypeLibrary, debugWriter));
	}			
	
	private void attributeWithNameInstance(){
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
		endElementTaskPool.put(e, new AttributeWithNameInstancePool(ns, datatypeLibrary, name, debugWriter));
	}
	
	private void group(){
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
		endElementTaskPool.put(e, new GroupPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void interleave(){
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
		endElementTaskPool.put(e, new InterleavePool(ns, datatypeLibrary, debugWriter));
	}		
	
	private void choicePattern(){
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
		endElementTaskPool.put(e, new ChoicePatternPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void optional(){
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
		endElementTaskPool.put(e, new OptionalPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void zeroOrMore(){
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
		endElementTaskPool.put(e, new ZeroOrMorePool(ns, datatypeLibrary, debugWriter));
	}
	
	private void oneOrMore(){
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
		endElementTaskPool.put(e, new OneOrMorePool(ns, datatypeLibrary, debugWriter));
	}
	
	private void list(){
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
		endElementTaskPool.put(e, new ListPatternPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void mixed(){
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
		endElementTaskPool.put(e, new MixedPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void ref(){
		builder.startLevel();{
			builder.startLevel();{				
				nameAttributeNCName();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: ref");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"ref","name","RELAXNG Specification 3.Full Syntax: ref");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: ref");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new RefPool(ns, datatypeLibrary, name, debugWriter));
	}
	
	private void parentRef(){
		builder.startLevel();{
			builder.startLevel();{				
				nameAttributeNCName();
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: parentRef");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"parentRef","name","RELAXNG Specification 3.Full Syntax: parentRef");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: parentRef");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new ParentRefPool(ns, datatypeLibrary, name, debugWriter));
	}
	
	private void empty(){
		builder.startLevel();{
			builder.startLevel();{
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: empty");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"empty","name","RELAXNG Specification 3.Full Syntax: empty");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: empty");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new EmptyPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void text(){
		builder.startLevel();{
			builder.startLevel();{
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: text");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"text","name","RELAXNG Specification 3.Full Syntax: text");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: text");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new TextPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void value(){
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
		endElementTaskPool.put(e, new ValuePool(ns, datatypeLibrary, type, debugWriter));
	}
	
	private void data(){
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
		endElementTaskPool.put(e, new DataPool(ns, datatypeLibrary, type, debugWriter));
	}
	
	private void notAllowed(){
		builder.startLevel();{
			builder.startLevel();{
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: notAllowed");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"notAllowed","name","RELAXNG Specification 3.Full Syntax: notAllowed");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: notAllowed");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new NotAllowedPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void externalRef(){
		builder.startLevel();{
			builder.startLevel();{
				hrefAttribute();
				commonAttributes();
				foreignAttributes();				
			}builder.endLevel();
			builder.buildGroup("attributes group","RELAXNG Specification 3.Full Syntax: externalRef");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"externalRef","name","RELAXNG Specification 3.Full Syntax: externalRef");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: externalRef");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new ExternalRefPool(ns, datatypeLibrary, href, debugWriter));
	}
	
	private void grammar(){
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
		endElementTaskPool.put(e, new GrammarPool(ns, datatypeLibrary, debugWriter));
	}		
	//**************************************************************************
	//END PATTERN METHODS ******************************************************
	//**************************************************************************
 
	//**************************************************************************
	//START TOP COMPONENT METHODS **********************************************
	//**************************************************************************
	private void start(){
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
		endElementTaskPool.put(e, new StartPool(ns, datatypeLibrary, combine, debugWriter));
	}
	
	private void define(){
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
		endElementTaskPool.put(e, new DefinePool(ns, datatypeLibrary, name, combine, debugWriter));
	}
	
	private void divGrammarContent(){
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
		endElementTaskPool.put(e, new DivGrammarContentPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void divIncludeContent(){
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
		endElementTaskPool.put(e, new DivIncludeContentPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void include(){
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
		endElementTaskPool.put(e, new IncludePool(ns, datatypeLibrary, href, debugWriter));
	}
	//**************************************************************************
	//END TOP COMPONENT METHODS ************************************************
	//**************************************************************************
	
	
	//**************************************************************************
	//START NAME CLASS METHODS *************************************************
	//**************************************************************************
	private void name(){
		builder.startLevel();{
			builder.startLevel();{				
				builder.buildData(INTERNAL_LIBRARY,"QName","data","RELAXNG Specification 3.Full Syntax: name");
				commonAttributes();
				foreignAttributes();
			}builder.endLevel();
			builder.buildGroup("attributes and elements group","RELAXNG Specification 3.Full Syntax: name");
			builder.buildName(XMLConstants.RELAXNG_NS_URI,"name","name","RELAXNG Specification 3.Full Syntax: name");
		}builder.endLevel();
		builder.buildElement("element","RELAXNG Specification 3.Full Syntax: name");
		
		SElement e = (SElement)builder.getCurrentPattern();
		endElementTaskPool.put(e, new NamePool(ns, datatypeLibrary, debugWriter));
	}
	
	private void anyName(){
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
		endElementTaskPool.put(e, new AnyNamePool(ns, datatypeLibrary, debugWriter));
	}
	
	private void nsName(){
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
		endElementTaskPool.put(e, new NsNamePool(ns, datatypeLibrary, debugWriter));
	}
	
	private void choiceNameClass(){
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
		endElementTaskPool.put(e, new ChoiceNameClassPool(ns, datatypeLibrary, debugWriter));
	}	
	//**************************************************************************
	//END NAME CLASS METHODS ***************************************************
	//**************************************************************************
	
	//**************************************************************************
	//START OTHER COMPONENT METHODS ********************************************
	//**************************************************************************
	private void param(){		
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
		endElementTaskPool.put(e, new ParamPool(ns, datatypeLibrary, name, debugWriter));
	}
	
	private void exceptPattern(){
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
		endElementTaskPool.put(e, new ExceptPatternPool(ns, datatypeLibrary, debugWriter));
	}
	
	private void exceptNameClass(){
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
		endElementTaskPool.put(e, new ExceptNameClassPool(ns, datatypeLibrary, debugWriter));
	}	
	//**************************************************************************
	//END OTHER COMPONENT METHODS **********************************************
	//**************************************************************************
	
	//**************************************************************************
	//START COMMON METHODS ****************************************************
	//**************************************************************************	
	private void commonAttributes(){
		builder.startLevel();{
			builder.startLevel();{
				builder.buildName("","ns","name","RELAXNG Specification 3.Full Syntax: ns attribute");	
				builder.buildData("","token","data","RELAXNG Specification 3.Full Syntax: ns attribute");
			}builder.endLevel();
			builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: ns attribute");
			ns = (SAttribute)builder.getCurrentPattern();
			attributeTaskPool.put(ns, null);		
		}builder.endLevel();				
		builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax: ns attribute");
		
		builder.startLevel();{
			builder.startLevel();{
				builder.buildName("","datatypeLibrary","name","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute");				
				builder.buildData(INTERNAL_LIBRARY,"datatypeLibraryURI","data","RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute");
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
		}builder.endLevel();				
		builder.buildZeroOrMore("zeroOrMore","RELAXNG Specification 3.Full Syntax: foreign attribute");
	}
	
	private void nameAttributeQName(){
		builder.startLevel();{
			builder.buildName("","name","name","RELAXNG Specification 3.Full Syntax: name attribute");
			builder.buildData(INTERNAL_LIBRARY,"QName","data","RELAXNG Specification 3.Full Syntax: name attribute");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: name attribute");
		name = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(name, null);
	}
	
	private void nameAttributeNCName(){
		builder.startLevel();{
			builder.buildName("","name","name","RELAXNG Specification 3.Full Syntax: name attribute");				
			builder.buildData(INTERNAL_LIBRARY,"NCName","attribute","RELAXNG Specification 3.Full Syntax: name attribute");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: name attribute");
		name = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(name, null);
	}
	
	private void typeAttributeNCNameSquare(){
		builder.startLevel();{
			typeAttributeNCName();			
		}builder.endLevel();				
		builder.buildOptional("optional","RELAXNG Specification 3.Full Syntax: type attribute");
	}
	
	private void typeAttributeNCName(){
		builder.startLevel();{
			builder.buildName("","type","name","RELAXNG Specification 3.Full Syntax: type attribute");				
			builder.buildData(INTERNAL_LIBRARY,"NCName","data","RELAXNG Specification 3.Full Syntax: type attribute");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: type attribute");
		type = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(type, null);
	}
	
	private void hrefAttribute(){
		builder.startLevel();{
			builder.buildName("","href","name","RELAXNG Specification 3.Full Syntax: href attribute");
			builder.buildData(INTERNAL_LIBRARY,"hrefURI","data","RELAXNG Specification 3.Full Syntax: href attribute");
		}builder.endLevel();
		builder.buildAttribute("attribute","RELAXNG Specification 3.Full Syntax: href attribute");
		href = (SAttribute)builder.getCurrentPattern();
		attributeTaskPool.put(href, null);
	}
	
	private void combineAttributeSquare(){
		builder.startLevel();{
			builder.startLevel();{
				builder.buildName("","combine","name","RELAXNG Specification 3.Full Syntax: combine attribute");
				builder.buildData(INTERNAL_LIBRARY,"combine", "value","RELAXNG Specification 3.Full Syntax: combine attribute");
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
	
	private void paramStarExceptPatternSquare(){
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

	private void exceptNameClassSquare(){	
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
	}	
	//**************************************************************************
	//END COMMON METHODS ******************************************************
	//**************************************************************************
}