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

package serene.restrictor;

import java.util.Stack;
import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import serene.util.IntList;
import serene.util.ObjectIntHashMap;
import serene.util.SereneArrayList;

import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SNameClass;

import serene.validation.schema.simplified.components.SParam;
import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SExceptNameClass;


import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SZeroOrMore;
import serene.validation.schema.simplified.components.SOneOrMore;
import serene.validation.schema.simplified.components.SOptional;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SEmpty;
import serene.validation.schema.simplified.components.SText;
import serene.validation.schema.simplified.components.SNotAllowed;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SValue;
import serene.validation.schema.simplified.components.SGrammar;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SDummy;


import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.RestrictingVisitor;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.restrictor.util.MorePath;
import serene.restrictor.util.DataPath;

import sereneWrite.MessageWriter;

/**
* Restrictions controller.
*/
public class RController implements RestrictingVisitor{
	
	SPattern[] topPatterns;
	SPattern[] definitionTopPatterns;
	int definitionCount;
		
	IntList handledDefinitions;
	ObjectIntHashMap definitionsContentTypes;//7.2 SRef contentTypes

	boolean attributeContext;//7.1.1
	Stack<SAttribute> attributesPath;
	
	boolean moreContext;//7.1.2	
	boolean moreMultiChildrenContext;//7.1.2	
	// interleave limitation 
	boolean moreInterleaveContext;
	boolean moreInterleaveMoreContext;
	MorePath morePath;
	
	boolean listContext;//7.1.3
	Stack<SListPattern> listsPath;
	
	boolean exceptPatternContext;//7.1.4
	DataPath dataPath;
	
	boolean startContext;//7.1.5
	
	boolean moreAttributeContext;//7.3
	boolean reportInfiniteNameClass;//7.3
		
	
	int contentType;
	
	SereneArrayList<SPattern> texts;
	boolean choiceContext;
	boolean choiceContainsText;
	
	ControllerPool pool;
		
	ElementNamingController elementNamingController;
	AttributeNamingController attributeNamingController;
	
	ElementLimitationNamingController elementLimitationNamingController;
	AttributeLimitationNamingController attributeLimitationNamingController;
	
	ErrorDispatcher errorDispatcher;
	MessageWriter debugWriter;
	
	public RController(ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		this.errorDispatcher = errorDispatcher;
		this.debugWriter = debugWriter;
				
		handledDefinitions = new IntList();
		definitionsContentTypes = new ObjectIntHashMap(debugWriter);
        
		pool = new ControllerPool(errorDispatcher, debugWriter);
		
		attributesPath = new Stack<SAttribute>();
		morePath = new MorePath();
		listsPath = new Stack<SListPattern>();
		dataPath = new DataPath();
		
		texts = new SereneArrayList<SPattern>();
	}
	
	
	public void control(SimplifiedModel simplifiedModel)throws SAXException{		
		init(simplifiedModel);		
		if(topPatterns != null && topPatterns.length != 0){//to catch situations where start element was missing
			for(SPattern topPattern : topPatterns){
				if(topPattern != null)topPattern.accept(this);
			}
		}
		close();
	}
	void init(SimplifiedModel simplifiedModel){
		topPatterns = simplifiedModel.getStartTopPattern();
		definitionTopPatterns = simplifiedModel.getRefDefinitionTopPattern();
			
		definitionCount = definitionTopPatterns.length;
		
		handledDefinitions.clear();
        definitionsContentTypes.clear();

		attributeContext = false;
		attributesPath.clear();
		
		moreContext = false;		
		moreMultiChildrenContext = false;
		moreInterleaveContext = false;
		moreInterleaveMoreContext = false;
		morePath.clear();
		
		listContext = false;
		listsPath.clear();
		
		exceptPatternContext = false;
		dataPath.clear();
		
		startContext = true;
		
		moreAttributeContext = false;
		reportInfiniteNameClass = false;
				
		texts.clear();
		choiceContext = false;
		choiceContainsText = false;
		
		elementNamingController = pool.getElementNamingController();
		attributeNamingController = pool.getAttributeNamingController();
		
		elementLimitationNamingController = pool.getElementLimitationNamingController();
		attributeLimitationNamingController = pool.getAttributeLimitationNamingController();
		
		contentType = ContentType.EMPTY;
	}
	void close() throws SAXException{		
		elementNamingController.control();
		elementNamingController.recycle();
		
		attributeNamingController.control();
		attributeNamingController.recycle();
		
		elementLimitationNamingController.control();
		elementLimitationNamingController.recycle();
		
		attributeLimitationNamingController.control();
		attributeLimitationNamingController.recycle();		
	}	
	
	public void visit(SParam param){}	
	public void visit(SExceptPattern exceptPattern)throws SAXException{
		boolean oldExceptPatternContext = exceptPatternContext; 
		exceptPatternContext = true;
				
		dataPath.push(exceptPattern);
		
		SimplifiedComponent child = exceptPattern.getChild();
		if(child != null) child.accept(this);
				
		exceptPatternContext = oldExceptPatternContext;
		
		dataPath.popItem();
	}
	public void visit(SExceptNameClass exceptNameClass)throws SAXException{
		SimplifiedComponent child = exceptNameClass.getChild();
		if(child != null) child.accept(this);		
	}
		
	public void visit(SName component){}
	public void visit(SAnyName anyName)throws SAXException{
		// it is safe to not check if inside except since that was not built as 
		// simplified component  
		if(!moreAttributeContext){
			// error 7.3
			// report at attribute
			reportInfiniteNameClass = true;
		}
		SimplifiedComponent child = anyName.getExceptNameClass();
		if(child != null) child.accept(this);
	}
	public void visit(SNsName nsName)throws SAXException{
		if(!moreAttributeContext){
			// error 7.3
			// report at attribute
			reportInfiniteNameClass = true;			
		}
		SimplifiedComponent child = nsName.getExceptNameClass();
		if(child != null) child.accept(this);
	}
	public void visit(SChoiceNameClass choice)throws SAXException{
		SimplifiedComponent[] children = choice.getChildren();
		if(children != null) next(children);
	}	

	//------------------
	//  !!! subclass !!!
	public void visit(SElement element) throws SAXException{		
		if(attributeContext){
			// error 7.1.1	
			String message = "Restrictions 7.1.1 error. Forbiden path:"
			+"\n\t<"+attributesPath.peek().getQName()+"> at "+attributesPath.peek().getLocation()
			+"\n\t<"+element.getQName()+"> at "+element.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));			
		}
		if(listContext){
			// error 7.1.3
			String message = "Restrictions 7.1.3 error. Forbiden path:"
			+"\n\t<"+listsPath.peek().getQName()+"> at "+listsPath.peek().getLocation()
			+"\n\t<"+element.getQName()+"> at "+element.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+element.getQName()+"> at "+element.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldAttributeContext = attributeContext;
		attributeContext = false;
		
		boolean oldMoreContext = moreContext;
		moreContext = false;
				
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		moreInterleaveContext = false;
		
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		moreInterleaveMoreContext = false;
			
		boolean oldMoreMultiChildrenContext = moreMultiChildrenContext;
		moreMultiChildrenContext = false;
		
		boolean oldListContext = listContext;
		listContext = false;
		
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		boolean oldMoreAttributeContext = moreAttributeContext;
		moreAttributeContext = false;				
		
		int textsOffset = texts.size();  
		
		SNameClass nameClass = element.getNameClass();
		if(nameClass != null) nameClass.accept(this);
		
		elementNamingController.add(element, nameClass);
		ElementNamingController oldENC = elementNamingController;
		AttributeNamingController oldANC = attributeNamingController;
		
		elementNamingController = pool.getElementNamingController();
		attributeNamingController = pool.getAttributeNamingController();
		
		elementLimitationNamingController.add(element, nameClass);
		ElementLimitationNamingController oldELNC = elementLimitationNamingController;
		AttributeLimitationNamingController oldALNC = attributeLimitationNamingController;
		
		elementLimitationNamingController = pool.getElementLimitationNamingController();
		attributeLimitationNamingController = pool.getAttributeLimitationNamingController();
				
		SimplifiedComponent child = element.getChild();
		if(child != null) child.accept(this);
				
		attributeNamingController.control();
		attributeNamingController.recycle();
		attributeNamingController = oldANC;
		
		elementNamingController.control();
		elementNamingController.recycle();
		elementNamingController = oldENC;
		
		attributeLimitationNamingController.control();
		attributeLimitationNamingController.recycle();
		attributeLimitationNamingController = oldALNC;
		
		elementLimitationNamingController.control();
		elementLimitationNamingController.recycle();
		elementLimitationNamingController = oldELNC;
		
		attributeContext = oldAttributeContext;		
		moreContext = oldMoreContext;
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;				
		moreMultiChildrenContext = oldMoreMultiChildrenContext;		
		listContext = oldListContext;
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;		
		moreAttributeContext = oldMoreAttributeContext;
		
		if(texts.size() > textsOffset)texts.removeTail(textsOffset);
		
		contentType = ContentType.COMPLEX;		
	}	
	//  !!! subclass !!!
	//------------------
	
	public void visit(SAttribute attribute)throws SAXException{
		if(attributeContext){
			// error 7.1.1	
			String message = "Restrictions 7.1.1 error. Forbiden path:"
			+"\n\t<"+attributesPath.peek().getQName()+"> at "+attributesPath.peek().getLocation()
			+"\n\t<"+attribute.getQName()+"> at "+attribute.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}		
		if(moreMultiChildrenContext){
			// error 7.1.2
			ArrayList<SPattern> path = morePath.peek();
			String message = "Restrictions 7.1.2 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+attribute.getQName()+"> at "+attribute.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(listContext){// error 7.1.3
			String message = "Restrictions 7.1.3 error. Forbiden path:"
			+"\n\t<"+listsPath.peek().getQName()+"> at "+listsPath.peek().getLocation()
			+"\n\t<"+attribute.getQName()+"> at "+attribute.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+attribute.getQName()+"> at "+attribute.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+attribute.getQName()+"> at "+attribute.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldMoreAttributeContext = moreAttributeContext;
		if(moreContext) moreAttributeContext = true;
		else moreAttributeContext = false;
					
		boolean oldReportInfiniteNameClass = reportInfiniteNameClass;
		reportInfiniteNameClass = false;
				
		SNameClass nameClass = attribute.getNameClass();
		if(nameClass != null) nameClass.accept(this);
		
		if(reportInfiniteNameClass){
			//error 7.3
			String message = "Restrictions 7.3 error. "
			+" Attribute <"+attribute.getQName()+"> at "+attribute.getLocation() 
			+" uses infinite name class without being repeated.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			
		}		
		reportInfiniteNameClass = oldReportInfiniteNameClass;		
		
		attributeNamingController.add(attribute, nameClass);

		boolean oldAttributeContext = attributeContext;
		attributeContext = true;
		
		boolean oldMoreContext = moreContext;
		moreContext = false;
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		moreInterleaveContext = false;
		
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		moreInterleaveMoreContext = false;
				
		boolean oldMoreMultiChildrenContext = moreMultiChildrenContext;
		moreMultiChildrenContext = false;
		
		boolean oldListContext = listContext;		
		listContext = false;
		
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		int textsOffset = texts.size();
		
		attributesPath.push(attribute);		
			
		SimplifiedComponent[] children = attribute.getChildren();
		if(children != null) next(children);
				
		attributeContext = oldAttributeContext;
		moreContext = oldMoreContext;
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		moreMultiChildrenContext = oldMoreMultiChildrenContext;		
		listContext = oldListContext;
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
		moreAttributeContext = oldMoreAttributeContext;
		
		attributesPath.pop();
		
		if(texts.size() > textsOffset)texts.removeTail(textsOffset);
		
		contentType = ContentType.EMPTY;
	}
	
	//------------------
	//  !!! subclass !!!
	public void visit(SChoicePattern choice)throws SAXException{
		elementNamingController.start(choice);
		attributeNamingController.start(choice);
		
		elementLimitationNamingController.start(choice);
		attributeLimitationNamingController.start(choice);
		
		boolean oldChoiceContext = choiceContext;
		choiceContext = true;
		
		boolean oldChoiceContainsText = choiceContainsText;
		choiceContainsText = false;

		SimplifiedComponent[] children = choice.getChildren();		
		if(children != null) {
			int ct = ContentType.ERROR;
			for(SimplifiedComponent child : children){    
				
				child.accept(this);
				ct = ct > contentType ? ct : contentType;
			}
			contentType = ct;
		}
		
		elementNamingController.end(choice);
		attributeNamingController.end(choice);
		
		elementLimitationNamingController.end(choice);
		attributeLimitationNamingController.end(choice);
		
		if(choiceContainsText)texts.add(choice);
		
		choiceContext = oldChoiceContext;
		choiceContainsText = oldChoiceContainsText;		
	}
	//  !!! subclass !!!
	//------------------
	
	public void visit(SInterleave interleave)throws SAXException{
		if(listContext){
			// error 7.1.3
			String message = "Restrictions 7.1.3 error. Forbiden path:"
			+"\n\t<"+listsPath.peek().getQName()+"> at "+listsPath.peek().getLocation()
			+"\n\t<"+interleave.getQName()+"> at "+interleave.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+interleave.getQName()+"> at "+interleave.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldStartContext = startContext;
		if(startContext && interleave.getChildrenCount() > 1){
			startContext = false;
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+interleave.getQName()+"> at "+interleave.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreMultiChildrenContext = moreMultiChildrenContext;
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
				
		if(moreContext){
			moreMultiChildrenContext = true;
			morePath.push(interleave);
			moreContext = false;
			moreInterleaveContext = true;
			if(moreInterleaveMoreContext){
				// Serene limitation
				//System.out.println("Serene DOES NOT SUPPORT "+interleave);
				String message = "Unsupported schema configuration. For the moment, Serene does not support <group> or <interleave> with multiple cardinality in the context of an <interleave> with multiple cardinality, path: ";
				ArrayList<SPattern> path = morePath.doublePeek();
				for(int i = 0; i < path.size(); i++){
					message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
				}
				path = morePath.peek();
				for(int i = 0; i < path.size(); i++){
					message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
				}			
				message += ".";		
				//System.out.println(message);
				errorDispatcher.error(new SAXParseException(message, null));
				moreInterleaveMoreContext = false;			
			}
		}		
		
		boolean oldListContext = listContext;		
		listContext = false;
		
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		int textsOffset = texts.size();		
		
		elementNamingController.start(interleave);
		attributeNamingController.start(interleave);
		
		elementLimitationNamingController.start(interleave);
		attributeLimitationNamingController.start(interleave);
		
		SPattern[] children = interleave.getChildren();
		if(children != null){
			if(listContext){
				next(children);
			}else{
				ContentTypeController cth = pool.getContentTypeController();
				for(int i = 0; i < children.length; i++){            
					children[i].accept(this);
					cth.add(i, contentType);					
				}				
				contentType = cth.handle(interleave, children);
				cth.recycle();
			}
		}	
		
		if(moreMultiChildrenContext) morePath.popItem();
		moreContext = oldMoreContext;
		moreMultiChildrenContext = oldMoreMultiChildrenContext;
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
				
		listContext = oldListContext;
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
		
		elementNamingController.end(interleave);
		attributeNamingController.end(interleave);
				
		elementLimitationNamingController.end(interleave);
		attributeLimitationNamingController.end(interleave);
		
		int textsCount = texts.size()-textsOffset;
		if(textsCount > 1){
			// report error 7.4
			// remove all the records starting with offset
			String message = "Restrictions error 7.4. "
			+ "Several text patterns in the context of <"+interleave.getQName()+"> at "+interleave.getLocation()+":";
			int last = texts.size()-1;
			for(int i = textsOffset; i < texts.size(); i++){
				SPattern text = texts.get(i);
				message += "\n\t<"+text.getQName()+"> at "+text.getLocation();
			}
			message += ".";			
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			texts.removeTail(textsOffset);
		}
	}
	public void visit(SGroup group) throws SAXException{
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+group.getQName()+"> at "+group.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldStartContext = startContext;
		if(startContext && group.getChildrenCount() > 1){
			startContext = false;
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+group.getQName()+"> at "+group.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreMultiChildrenContext = moreMultiChildrenContext;
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		
		if(moreContext){
			moreMultiChildrenContext = true;
			morePath.push(group);
			moreContext = false;			
			if(moreInterleaveMoreContext){							
				// Serene limitation
				// System.out.println("Serene DOES NOT SUPPORT "+group);
				String message = "Unsupported schema configuration. For the moment, Serene does not support <group> or <interleave> with multiple cardinality in the context of an <interleave> with multiple cardinality, path: ";
				ArrayList<SPattern> path = morePath.doublePeek();
				for(int i = 0; i < path.size(); i++){
					message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
				}
				path = morePath.peek();
				for(int i = 0; i < path.size(); i++){
					message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
				}			
				message += ".";		
				//System.out.println(message);
				errorDispatcher.error(new SAXParseException(message, null));
				moreInterleaveMoreContext = false;			
			}
		}		
		
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		elementNamingController.start(group);
		attributeNamingController.start(group);
		
		elementLimitationNamingController.start(group);
		attributeLimitationNamingController.start(group);
		
		SPattern[] children = group.getChildren();
		if(children != null){
			if(listContext){
				next(children);
			}else{
				ContentTypeController cth = pool.getContentTypeController();
				for(int i = 0; i < children.length; i++){            
					children[i].accept(this);
					cth.add(i, contentType);					
				}				
				contentType = cth.handle(group, children);
				cth.recycle();
			}
		}
		
		elementNamingController.end(group);
		attributeNamingController.end(group);
		
		elementLimitationNamingController.end(group);
		attributeLimitationNamingController.end(group);
		
		if(moreMultiChildrenContext) morePath.popItem();
		moreContext = oldMoreContext;
		moreMultiChildrenContext = oldMoreMultiChildrenContext;
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
	}
	
	//------------------
	//  !!! subclass !!!
	public void visit(SZeroOrMore zeroOrMore)throws SAXException{
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+zeroOrMore.getQName()+"> at "+zeroOrMore.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+zeroOrMore.getQName()+"> at "+zeroOrMore.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldMoreContext = moreContext;
		moreContext = true;
		morePath.push(zeroOrMore);
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		
		if(moreInterleaveContext) moreInterleaveMoreContext = true;		
		
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		elementLimitationNamingController.start(zeroOrMore);
		attributeLimitationNamingController.start(zeroOrMore);
		
		SimplifiedComponent child = zeroOrMore.getChild();
		if(child != null) child.accept(this);
		
		elementLimitationNamingController.end(zeroOrMore);
		attributeLimitationNamingController.end(zeroOrMore);
		
		moreContext = oldMoreContext;
		morePath.pop();
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
			
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
	}
	//  !!! subclass !!!
	//------------------
	
	public void visit(SOneOrMore oneOrMore)throws SAXException{
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+oneOrMore.getQName()+"> at "+oneOrMore.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+oneOrMore.getQName()+"> at "+oneOrMore.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldMoreContext = moreContext;
		moreContext = true;
		morePath.push(oneOrMore);
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		
		if(moreInterleaveContext) moreInterleaveMoreContext = true;
			
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		elementLimitationNamingController.start(oneOrMore);
		attributeLimitationNamingController.start(oneOrMore);
		
		SimplifiedComponent child = oneOrMore.getChild();
		if(child != null) child.accept(this);
		
		elementLimitationNamingController.end(oneOrMore);
		attributeLimitationNamingController.end(oneOrMore);
		
		moreContext = oldMoreContext;
		morePath.pop();
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
				
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
	}
	
	//------------------
	//  !!! subclass !!!
	public void visit(SOptional optional)throws SAXException{
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+optional.getQName()+"> at "+optional.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+optional.getQName()+"> at "+optional.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		SimplifiedComponent child = optional.getChild();
		if(child != null) child.accept(this);
		
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
	}
	//  !!! subclass !!!
	//------------------
	
	public void visit(SMixed mixed) throws SAXException{
		if(listContext){
			// error 7.1.3
			String message = "Restrictions 7.1.3 error. Forbiden path:"
			+"\n\t<"+listsPath.peek().getQName()+"> at "+listsPath.peek().getLocation()
			+"\n\t<"+mixed.getQName()+"> at "+mixed.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+mixed.getQName()+"> at "+mixed.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+mixed.getQName()+"> at "+mixed.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreMultiChildrenContext = moreMultiChildrenContext;
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
				
		if(moreContext){
			moreMultiChildrenContext = true;
			morePath.push(mixed);
			moreContext = false;
			moreInterleaveContext = true;
			if(moreInterleaveMoreContext){
				// Serene limitation
				//System.out.println("Serene DOES NOT SUPPORT "+mixed);
				String message = "Unsupported schema configuration. For the moment, Serene does not support <group> or <interleave> with multiple cardinality in the context of an <interleave> with multiple cardinality, path: ";
				ArrayList<SPattern> path = morePath.doublePeek();
				for(int i = 0; i < path.size(); i++){
					message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
				}
				path = morePath.peek();
				for(int i = 0; i < path.size(); i++){
					message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
				}			
				message += ".";		
				//System.out.println(message);
				errorDispatcher.error(new SAXParseException(message, null));
				moreInterleaveMoreContext = false;			
			}
		}		
				
		boolean oldListContext = listContext;		
		listContext = false;
				
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		int textsOffset = texts.size();
		
		elementNamingController.start(mixed);
		attributeNamingController.start(mixed);
		
		elementLimitationNamingController.start(mixed);
		attributeLimitationNamingController.start(mixed);
		
		SPattern child = mixed.getChild();
		if(child != null){
			if(listContext){
				child.accept(this);
			}else{
				ContentTypeController cth = pool.getContentTypeController();
				child.accept(this);
				cth.add(0, contentType);							
				contentType = cth.handle(mixed, child, ContentType.COMPLEX);
				cth.recycle(); 
			}
		}
		
		elementNamingController.end(mixed);
		attributeNamingController.end(mixed);
		
		elementLimitationNamingController.end(mixed);
		attributeLimitationNamingController.end(mixed);
		
		if(moreMultiChildrenContext) morePath.popItem();
		moreContext = oldMoreContext;
		moreMultiChildrenContext = oldMoreMultiChildrenContext;
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		
		
		listContext = oldListContext;
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
		
		int textsCount = texts.size()-textsOffset;
		if(textsCount > 0){
			// report error 7.4
			// add default text of current mixed at the end			
			// remove all the records starting with offset
			String message = "Restrictions error 7.4. "
			+ "Several text patterns in the context of <"+mixed.getQName()+"> at "+mixed.getLocation()+":";
			int last = texts.size()-1;
			for(int i = textsOffset; i < texts.size(); i++){
				SPattern text = texts.get(i);
				message += "\n\t<"+text.getQName()+"> at "+text.getLocation();
			}			
			message += "\n\tdefault text pattern of <"+mixed.getQName()+"> at "+mixed.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			
			texts.removeTail(textsOffset);						
		}else if(choiceContext){
			choiceContainsText = true;
		}else{
			texts.add(mixed);
		}
	}	
	
	public void visit(SListPattern list) throws SAXException{
		if(listContext){
			// error 7.1.3
			String message = "Restrictions 7.1.3 error. Forbiden path:"
			+"\n\t<"+listsPath.peek().getQName()+"> at "+listsPath.peek().getLocation()
			+"\n\t<"+list.getQName()+"> at "+list.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+list.getQName()+"> at "+list.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+list.getQName()+"> at "+list.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldListContext = listContext;		
		listContext = true;
		
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		listsPath.push(list);		
		
		SimplifiedComponent child = list.getChild();
		if(child != null) child.accept(this);
		
		listContext = oldListContext;
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
		
		listsPath.pop();
		
		contentType = ContentType.SIMPLE;
	}	
	public void visit(SEmpty empty) throws SAXException{
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+empty.getQName()+"> at "+empty.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+empty.getQName()+"> at "+empty.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(attributeContext){
			// Serene limitation
			String message = "Unsupported schema configuration. For the moment, Serene does not support <empty> in the context of <attribute>, path: "				
				+"\n\t<"+attributesPath.peek().getQName()+"> at "+attributesPath.peek().getLocation()
				+"\n\t<"+empty.getQName()+"> at "+empty.getLocation()+".";		
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));			
		}
		contentType = ContentType.EMPTY;
	}
	public void visit(SText text) throws SAXException{
		if(listContext){
			// error 7.1.3
			String message = "Restrictions 7.1.3. Forbiden path:"
			+"\n\t<"+listsPath.peek().getQName()+"> at "+listsPath.peek().getLocation()
			+"\n\t<"+text.getQName()+"> at "+text.getLocation()+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(exceptPatternContext){
			// error 7.1.4
			ArrayList<SimplifiedComponent> path = dataPath.peek();
			String message = "Restrictions 7.1.4 error. Forbiden path: ";
			for(int i = 0; i < path.size(); i++){
				message += "\n\t<"+path.get(i).getQName()+"> at "+path.get(i).getLocation(); 
			}
			message += "\n\t<"+text.getQName()+"> at "+text.getLocation();
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+text.getQName()+"> at "+text.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		if(choiceContext){
			choiceContainsText = true;
		}else{
			texts.add(text);
		}
		
		contentType = ContentType.COMPLEX;
	}
	public void visit(SNotAllowed notAllowed){}
	
	//------------------
	//  !!! subclass !!!
	public void visit(SRef ref)throws SAXException{
		int index = ref.getDefinitionIndex();
		if(handledDefinitions.contains(index)){
            contentType = definitionsContentTypes.get(definitionTopPatterns[index]);
            return;
        }
		definitionTopPatterns[index].accept(this);		
		handledDefinitions.add(index);
        definitionsContentTypes.put(definitionTopPatterns[index], contentType);
	}	
	//  !!! subclass !!!
	//------------------
	
	public void visit(SValue value) throws SAXException{
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+value.getQName()+"> at "+value.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		contentType = ContentType.SIMPLE;
	}
	public void visit(SData data)throws SAXException{
		if(startContext){
			// error 7.1.5
			String message = "Restrictions 7.1.5 error. "
			+"Element <"+data.getQName()+"> at "+data.getLocation()+" is not expected as start of the schema.";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		dataPath.push(data);
		
		SimplifiedComponent[] param = data.getParam();
		if(param != null) next(param);
		SimplifiedComponent[] exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) next(exceptPattern);
		
		startContext = oldStartContext;
		
		dataPath.pop();
		
		contentType = ContentType.SIMPLE;
	}	
	public void visit(SGrammar grammar)throws SAXException{
		SimplifiedComponent child = grammar.getChild();
		if(child != null) child.accept(this);
	}
	
	public void visit(SDummy dummy)throws SAXException{
		SimplifiedComponent[] children = dummy.getChildren();
		if(children != null) next(children);
	}	

		
	protected void next(SimplifiedComponent[] children)throws SAXException{
		for(SimplifiedComponent child : children){            
			child.accept(this);
		}
	}
}