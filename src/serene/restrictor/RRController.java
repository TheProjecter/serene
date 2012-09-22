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

import java.util.ArrayList;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import serene.util.IntList;
import serene.util.IntStack;
import serene.util.BooleanStack;

import serene.validation.schema.simplified.SPattern;

import serene.validation.schema.simplified.SNameClass;
import serene.validation.schema.simplified.SExceptPattern;
import serene.validation.schema.simplified.SExceptNameClass;


import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SChoicePattern;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SGroup;
import serene.validation.schema.simplified.SListPattern;
import serene.validation.schema.simplified.SEmpty;
import serene.validation.schema.simplified.SText;
import serene.validation.schema.simplified.SNotAllowed;
import serene.validation.schema.simplified.SRef;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SGrammar;
import serene.validation.schema.simplified.SDummy;


import serene.validation.schema.simplified.SName;
import serene.validation.schema.simplified.SAnyName;
import serene.validation.schema.simplified.SNsName;
import serene.validation.schema.simplified.SChoiceNameClass;

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.error.ErrorDispatcher;

/**
* Restrictions controller for recursive reference schemas.
*/
public class RRController extends RController{
		
	RecursionModel recursionModel;
	
	ArrayList<BooleanStack> loopElement;
	ArrayList<BooleanStack> loopOptional;
	ArrayList<Stack<SChoicePattern>> loopChoice;
	BooleanStack recursiveDefinitionContext;
	
	ArrayList<ArrayList<SRef>> definitionBlindLoops;
	ArrayList<ArrayList<SRef>> definitionInfiniteLoops;
	ArrayList<Map<SRef, ArrayList<SRef>>> definitionOpenAlternatives;

	AlternativeSeeker alternativeSeeker;
	OpenAlternativesHandler openAlternativesHandler;
	
	
	public RRController(ControllerPool pool, ErrorDispatcher errorDispatcher){
		super(pool, errorDispatcher);
		
		loopElement = new ArrayList<BooleanStack>();
		loopOptional = new ArrayList<BooleanStack>();
		loopChoice = new ArrayList<Stack<SChoicePattern>>();
		recursiveDefinitionContext = new BooleanStack();

		definitionBlindLoops = new ArrayList<ArrayList<SRef>>();
		definitionInfiniteLoops = new ArrayList<ArrayList<SRef>>();
		definitionOpenAlternatives = new ArrayList<Map<SRef, ArrayList<SRef>>>();
		
		openAlternativesHandler = new OpenAlternativesHandler();
	}
	
	public void control(SimplifiedModel simplifiedModel) throws SAXException{
		init(simplifiedModel);
		
		if(topPatterns != null && topPatterns.length != 0){//to catch situations where start element was missing
			for(SPattern topPattern : topPatterns){
				if(topPattern != null){
                    open();
                    topPattern.accept(this);
                    close();
                }
			}
		}
						
		openAlternativesHandler.init(definitionCount,
								definitionBlindLoops,
								definitionInfiniteLoops,
								definitionOpenAlternatives,
								new IntStack());
		openAlternativesHandler.handleAlternatives();
		// report errors		
		if(!definitionBlindLoops.isEmpty()){			
			for(int i = 0; i < definitionBlindLoops.size(); i++){
				ArrayList<SRef> refs = definitionBlindLoops.get(i);
				if(refs != null && !refs.isEmpty()){
					for(int j = 0; j < refs.size(); j++){
						SRef ref = refs.get(j);
						// error 4.19
						String message = "Simplification 4.19 error. "
						 +"No element definition in recursion loop path for element <"+ref.getQName()+"> at "+ref.getLocation(restrictToFileName)+".";
						// System.out.println(message);
						errorDispatcher.error(new SAXParseException(message, null));
					}
				}
			}
		}
		
		if(!definitionInfiniteLoops.isEmpty()){			 
			for(int i = 0; i < definitionInfiniteLoops.size(); i++){
				ArrayList<SRef> refs = definitionInfiniteLoops.get(i);
				if(refs != null && !refs.isEmpty()){
					for(int j = 0; j < refs.size(); j++){
						SRef ref = refs.get(j);
						// error 4.19
						String message = "Simplification 4.19 error. "
						 +"Infinite recursion loop for element <"+ref.getQName()+"> at "+ref.getLocation(restrictToFileName)+".";
						 // System.out.println(message);
						 errorDispatcher.error(new SAXParseException(message, null));
					}
				}
			}
		}
	}
	void init(SimplifiedModel simplifiedModel){
		super.init(simplifiedModel);		
		
		recursionModel = simplifiedModel.getRecursionModel();		
		loopElement.clear();
		loopOptional.clear();
		loopChoice.clear();
		
		definitionBlindLoops.clear();
		definitionInfiniteLoops.clear();
		definitionOpenAlternatives.clear();
		
		
		for(int i = 0; i < definitionCount; i++){
			loopElement.add(null);
			loopOptional.add(null);
			loopChoice.add(null);
			
			definitionBlindLoops.add(null);
			definitionInfiniteLoops.add(null);
			definitionOpenAlternatives.add(null);
		}
						
		recursiveDefinitionContext.clear();
		recursiveDefinitionContext.push(false);
		
		handledDefinitions.clear();		
	}	
	
	
	
	public void visit(SExceptPattern exceptPattern)throws SAXException{
		boolean oldExceptPatternContext714 = exceptPatternContext714; 
		exceptPatternContext714 = true;
        boolean oldExceptPatternContext72 = exceptPatternContext72;
        exceptPatternContext72 = true;
				
		dataPath.push(exceptPattern);
		
		SimplifiedComponent child = exceptPattern.getChild();
		if(child != null) child.accept(this);
				
		exceptPatternContext714 = oldExceptPatternContext714;
        exceptPatternContext72 = oldExceptPatternContext72;
		
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
			reportError711(element);			
		}
		if(listContext){
			// error 7.1.3
			reportError713(element);
		}
		if(exceptPatternContext714){
			// error 7.1.4
			if(element.hasCardinalityElement())reportCardinalityElementError714(element);
			else reportError714(element);
		}
		if(startContext && element.hasCardinalityElement()){
			// error 7.1.5
			reportCardinalityElementError715(element);
		}
		
		if(recursiveDefinitionContext.peek()){
		    boolean isOptional = element.getMinOccurs() == 0;
            for(int i = 0; i < definitionCount; i++){
                if(isOptional && loopOptional.get(i) != null) loopOptional.get(i).push(true);
                if(loopElement.get(i) != null)loopElement.get(i).push(true);
            }
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
		
        boolean oldHasSeveralChildren = hasSeveralChildren;
        hasSeveralChildren = false;
        
		boolean oldListContext = listContext;
		listContext = false;
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
        
        boolean oldExceptPatternContext72 = exceptPatternContext72;
		exceptPatternContext72 = false;
		
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
        elementNamingController.setRestrictToFileName(restrictToFileName);        
		attributeNamingController = pool.getAttributeNamingController();
		attributeNamingController.setRestrictToFileName(restrictToFileName);
        
		elementLimitationNamingController.add(element, nameClass);
		ElementLimitationNamingController oldELNC = elementLimitationNamingController;
		AttributeLimitationNamingController oldALNC = attributeLimitationNamingController;
		
		elementLimitationNamingController = pool.getElementLimitationNamingController();
        elementLimitationNamingController.setRestrictToFileName(restrictToFileName);
		attributeLimitationNamingController = pool.getAttributeLimitationNamingController();
        elementLimitationNamingController.setRestrictToFileName(restrictToFileName);
				
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
        hasSeveralChildren = oldHasSeveralChildren;		
		listContext = oldListContext;
		exceptPatternContext714 = oldExceptPatternContext714;
        exceptPatternContext72 = oldExceptPatternContext72;
		startContext = oldStartContext;		
		moreAttributeContext = oldMoreAttributeContext;
		
		if(texts.size() > textsOffset)texts.removeTail(textsOffset);
		
		contentType = ContentType.COMPLEX;

		
		if(recursiveDefinitionContext.peek()){
		    boolean isOptional = element.getMinOccurs() == 0;
            for(int i = 0; i < definitionCount; i++){                
                if(loopElement.get(i) != null)loopElement.get(i).pop();
                if(isOptional && loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
        
        
	}	
	//  !!! subclass !!!
	//------------------
	
	public void visit(SAttribute attribute)throws SAXException{
		if(attributeContext){
			// error 7.1.1	
			reportError711(attribute);
		}		
		if(moreMultiChildrenContext){
            if(hasSeveralChildren){
                // error 7.1.2
                reportError712(attribute);
            }else{
                 moreAttributeContext = true;
            }
		}else{
            if(moreContext) moreAttributeContext = true;
            else moreAttributeContext = false;
        }        
        
        if(attribute.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
            }
        }
		
        if(attribute.getMaxOccurs() == SPattern.UNBOUNDED){
            moreAttributeContext = true;
            morePath.pushMultipleCardinalityPattern(attribute);
            elementLimitationNamingController.startMultipleCardinality();
            attributeLimitationNamingController.startMultipleCardinality();            
        }
        
		if(listContext){
		    // error 7.1.3
			reportError713(attribute);
		}
		if(exceptPatternContext714){
			// error 7.1.4
			if(attribute.hasCardinalityElement())reportCardinalityElementError714(attribute);
			else reportError714(attribute);
		}
		if(startContext){
			// error 7.1.5
			if(attribute.hasCardinalityElement())reportCardinalityElementError715(attribute);
			else reportError715(attribute);
		}
		
		boolean oldMoreAttributeContext = moreAttributeContext;
							
		boolean oldReportInfiniteNameClass = reportInfiniteNameClass;
		reportInfiniteNameClass = false;
				
		SNameClass nameClass = attribute.getNameClass();
		if(nameClass != null) nameClass.accept(this);
		
		if(reportInfiniteNameClass){
			//error 7.3
			reportError73(attribute);
			
		}		
		reportInfiniteNameClass = oldReportInfiniteNameClass;		
		
        //ISSUE 45, 46
		if(!attributeContext)attributeNamingController.add(attribute, nameClass);

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
		
        boolean oldHasSeveralChildren = hasSeveralChildren;
        hasSeveralChildren = false;
        
		boolean oldListContext = listContext;		
		listContext = false;
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
        
        boolean oldExceptPatternContext72 = exceptPatternContext72;
		exceptPatternContext72 = false;
		
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
        hasSeveralChildren = oldHasSeveralChildren;		
		listContext = oldListContext;
		exceptPatternContext714 = oldExceptPatternContext714;
        exceptPatternContext72 = oldExceptPatternContext72;
		startContext = oldStartContext;
		moreAttributeContext = oldMoreAttributeContext;
		
		attributesPath.pop();
		
		if(texts.size() > textsOffset)texts.removeTail(textsOffset);
		
		contentType = ContentType.EMPTY;
		
		if(attribute.getMaxOccurs() == SPattern.UNBOUNDED){
		    morePath.pop();
            elementLimitationNamingController.endMultipleCardinality();
            attributeLimitationNamingController.endMultipleCardinality();
        }
        
        if(attribute.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
	}
	
	//------------------
	//  !!! subclass !!!
	public void visit(SChoicePattern choice)throws SAXException{
	    if(exceptPatternContext714 && choice.hasCardinalityElement()){
			// error 7.1.4
			reportCardinalityElementError714(choice);
		}
		if(startContext && choice.hasCardinalityElement()){
			// error 7.1.5
			reportCardinalityElementError715(choice);
		}
		
		if(recursiveDefinitionContext.peek()){
		    boolean isOptional = choice.getMinOccurs() == 0;
            for(int i = 0; i < definitionCount; i++){
                if(isOptional && loopOptional.get(i) != null) loopOptional.get(i).push(true);
                if(loopChoice.get(i) != null)loopChoice.get(i).push(choice);
            }
        }
        
		elementNamingController.start(choice);
		attributeNamingController.start(choice);
		
		elementLimitationNamingController.start(choice);
		attributeLimitationNamingController.start(choice);
		
		boolean oldChoiceContext = choiceContext;
		choiceContext = true;
		
		boolean oldChoiceContainsText = choiceContainsText;
		choiceContainsText = false;
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		if(choice.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = true;	
		    morePath.pushMultipleCardinalityPattern(choice);
		    if(moreInterleaveContext) moreInterleaveMoreContext = true;
		    elementLimitationNamingController.startMultipleCardinality();
            attributeLimitationNamingController.startMultipleCardinality();            
		}
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		boolean oldStartContext = startContext;
		if(choice.hasCardinalityElement()){
		    exceptPatternContext714 = false;
		    startContext = false;
		}

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

        if(choice.hasCardinalityElement()){
		    exceptPatternContext714 = oldExceptPatternContext714;
		    startContext = oldStartContext;
		}
        if(choice.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = oldMoreContext;	
		    morePath.pop();
		    moreInterleaveMoreContext = oldMoreInterleaveMoreContext;		 
            elementLimitationNamingController.endMultipleCardinality();
            attributeLimitationNamingController.endMultipleCardinality();
		}		
		
		if(recursiveDefinitionContext.peek()){
		    boolean isOptional = choice.getMinOccurs() == 0;
            for(int i = 0; i < definitionCount; i++){                
                if(loopChoice.get(i) != null)loopChoice.get(i).pop();
                if(isOptional && loopOptional.get(i) != null) loopOptional.get(i).pop();
            }
        }
        
	}
	//  !!! subclass !!!
	//------------------
	
	public void visit(SInterleave interleave)throws SAXException{
		if(listContext){
			// error 7.1.3
			reportError713(interleave);
		}
		if(exceptPatternContext714){
			// error 7.1.4
			if(interleave.hasCardinalityElement())reportCardinalityElementError714(interleave);
			else reportError714(interleave);
		}
		
		if(interleave.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
            }
        }
        
		boolean oldStartContext = startContext;
		if(startContext && interleave.getChildrenCount() > 1){
			startContext = false;
			// error 7.1.5
			if(interleave.hasCardinalityElement())reportCardinalityElementError715(interleave);
			else reportError715(interleave);
		}
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreMultiChildrenContext = moreMultiChildrenContext;
		boolean oldHasSeveralChildren = hasSeveralChildren;
        
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
				
        if(interleave.getChildrenCount() > 1)hasSeveralChildren = true;
        else hasSeveralChildren = false;
        
        if(interleave.getMaxOccurs() == SPattern.UNBOUNDED){
            moreContext = true;
            morePath.pushMultipleCardinalityPattern(interleave);
            elementLimitationNamingController.startMultipleCardinality();
            attributeLimitationNamingController.startMultipleCardinality();
            if(moreInterleaveContext) moreInterleaveMoreContext = true;
        }        
		
		if(moreContext){            
			moreMultiChildrenContext = true;
			morePath.push(interleave);
			moreContext = false;
			moreInterleaveContext = true;
			if(moreInterleaveMoreContext){
				// Serene limitation
				//System.out.println("Serene DOES NOT SUPPORT "+interleave);
				String message = "Unsupported schema configuration. For the moment serene does not support <group> or <interleave> with multiple cardinality in the context of an <interleave> with multiple cardinality, path: ";
				ArrayList<SPattern> path = morePath.doublePeek();
				message += "\n"+path.get(0).getCardinalityElementQName()+" at "+path.get(0).getCardinalityElementLocation(restrictToFileName);
				for(int i = 1; i < path.size(); i++){
					message += "\n"+path.get(i).getQName()+" at "+path.get(i).getLocation(restrictToFileName); 
				}
				path = morePath.peek();
				message += "\n"+path.get(0).getCardinalityElementQName()+" at "+path.get(0).getCardinalityElementLocation(restrictToFileName);
				for(int i = 1; i < path.size(); i++){
					message += "\n"+path.get(i).getQName()+" at "+path.get(i).getLocation(restrictToFileName); 
				}			
				message += ".";		
				//System.out.println(message);
				errorDispatcher.error(new SAXParseException(message, null));
				moreInterleaveMoreContext = false;			
			}		
		}		
		
		boolean oldListContext = listContext;		
		listContext = false;
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
		
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
                cth.setRestrictToFileName(restrictToFileName);
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
        hasSeveralChildren = oldHasSeveralChildren;
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
				
		listContext = oldListContext;
		exceptPatternContext714 = oldExceptPatternContext714;
		startContext = oldStartContext;
		
		elementNamingController.end(interleave);
		attributeNamingController.end(interleave);
				
		elementLimitationNamingController.end(interleave);
		attributeLimitationNamingController.end(interleave);
		
		int textsCount = texts.size()-textsOffset;
		if(textsCount > 1){
			// report error 7.4
			// remove all the records starting with offset
			reportError74(textsOffset, interleave);
			texts.removeTail(textsOffset);
		}
		
		if(interleave.getMaxOccurs() == SPattern.UNBOUNDED){
		    morePath.pop();
            elementLimitationNamingController.endMultipleCardinality();
            attributeLimitationNamingController.endMultipleCardinality();
        }
        
        if(interleave.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
	}
	public void visit(SGroup group) throws SAXException{
	    //System.out.println("R CONTROLLER VISIT Element <"+group.getQName()+"> at "+group.getLocation(restrictToFileName));
		if(exceptPatternContext714){
			// error 7.1.4
			if(group.hasCardinalityElement())reportCardinalityElementError714(group);
			else reportError714(group);
		}
		
		if(group.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
            }
        }
        
		boolean oldStartContext = startContext;
		if(startContext && group.getChildrenCount() > 1){
			startContext = false;
			// error 7.1.5
			if(group.hasCardinalityElement())reportCardinalityElementError715(group);
			else reportError715(group);
		}
		
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreMultiChildrenContext = moreMultiChildrenContext;
		boolean oldHasSeveralChildren =hasSeveralChildren;
        
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		
        if(group.getChildrenCount() > 1)hasSeveralChildren = true;
        else hasSeveralChildren = false;
        
        if(group.getMaxOccurs() == SPattern.UNBOUNDED){
            moreContext = true;
            morePath.pushMultipleCardinalityPattern(group);
            elementLimitationNamingController.startMultipleCardinality();
            attributeLimitationNamingController.startMultipleCardinality();
            if(moreInterleaveContext) moreInterleaveMoreContext = true;
        }
        
		if(moreContext){
			moreMultiChildrenContext = true;
			morePath.push(group);
			moreContext = false;			
			if(moreInterleaveMoreContext){							
				// Serene limitation
				// System.out.println("Serene DOES NOT SUPPORT "+group);
				String message = "Unsupported schema configuration. For the moment serene does not support <group> or <interleave> with multiple cardinality in the context of an <interleave> with multiple cardinality, path: ";
				ArrayList<SPattern> path = morePath.doublePeek();
				message += "\n"+path.get(0).getCardinalityElementQName()+" at "+path.get(0).getCardinalityElementLocation(restrictToFileName);
				for(int i = 1; i < path.size(); i++){
					message += "\n"+path.get(i).getQName()+" at "+path.get(i).getLocation(restrictToFileName); 
				}
				path = morePath.peek();
				message += "\n"+path.get(0).getCardinalityElementQName()+" at "+path.get(0).getCardinalityElementLocation(restrictToFileName);
				for(int i = 1; i < path.size(); i++){
					message += "\n"+path.get(i).getQName()+" at "+path.get(i).getLocation(restrictToFileName); 
				}			
				message += ".";		
				//System.out.println(message);
				errorDispatcher.error(new SAXParseException(message, null));
				moreInterleaveMoreContext = false;			
			}			
		}		
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
		
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
                cth.setRestrictToFileName(restrictToFileName);
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
        hasSeveralChildren = oldHasSeveralChildren;
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		
		exceptPatternContext714 = oldExceptPatternContext714;
		startContext = oldStartContext;
		
		if(group.getMaxOccurs() == SPattern.UNBOUNDED){
		    morePath.pop();
            elementLimitationNamingController.endMultipleCardinality();
            attributeLimitationNamingController.endMultipleCardinality();
        }
        
        if(group.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
	}
	
	//------------------
	//  !!! subclass !!!
	/*public void visit(SZeroOrMore zeroOrMore)throws SAXException{		
		boolean oldMoreContext = moreContext;
		moreContext = true;
		morePath.push(zeroOrMore);
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		
		if(moreInterleaveContext) moreInterleaveMoreContext = true;		
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
		
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
			
		exceptPatternContext714 = oldExceptPatternContext714;
		startContext = oldStartContext;
	}*/
	//  !!! subclass !!!
	//------------------
	
	/*public void visit(SOneOrMore oneOrMore)throws SAXException{
		
		
		boolean oldMoreContext = moreContext;
		moreContext = true;
		morePath.push(oneOrMore);
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		
		if(moreInterleaveContext) moreInterleaveMoreContext = true;
			
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
		
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
				
		exceptPatternContext714 = oldExceptPatternContext714;
		startContext = oldStartContext;
	}*/
	
	//------------------
	//  !!! subclass !!!
	/*public void visit(SOptional optional)throws SAXException{
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		SimplifiedComponent child = optional.getChild();
		if(child != null) child.accept(this);
		
		exceptPatternContext714 = oldExceptPatternContext714;
		startContext = oldStartContext;
	}*/
	//  !!! subclass !!!
	//------------------
	
	
	public void visit(SListPattern list) throws SAXException{
		if(listContext){
			// error 7.1.3
			reportError713(list);
		}
		if(exceptPatternContext714){
			// error 7.1.4
			if(list.hasCardinalityElement())reportCardinalityElementError714(list);
			else reportError714(list);
		}
		if(startContext){
			// error 7.1.5
			if(list.hasCardinalityElement())reportCardinalityElementError715(list);
			else reportError715(list);
		}
		if((moreContext || moreMultiChildrenContext) && !(listContext || exceptPatternContext72)){
            // error 7.2 simple content type repeated in the context
			reportError72(list);
        }
        
        if(list.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
            }
        }
        
		boolean oldListContext = listContext;		
		listContext = true;
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
        
		boolean oldMoreContext = moreContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		if(list.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = true;
		    morePath.pushMultipleCardinalityPattern(list);
		    if(moreInterleaveContext) moreInterleaveMoreContext = true;
		    elementLimitationNamingController.startMultipleCardinality();
			attributeLimitationNamingController.startMultipleCardinality();
		}
		
        boolean oldExceptPatternContext72 = exceptPatternContext72;
		exceptPatternContext72 = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		listsPath.push(list);		
		
		SimplifiedComponent child = list.getChild();
		if(child != null) child.accept(this);
		
		listContext = oldListContext;
		exceptPatternContext714 = oldExceptPatternContext714;
        exceptPatternContext72 = oldExceptPatternContext72;
		startContext = oldStartContext;
		
		listsPath.pop();
		
		contentType = ContentType.SIMPLE;
		
		if(list.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = oldMoreContext;	
		    morePath.pop();
		    moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		    elementLimitationNamingController.endMultipleCardinality();
			attributeLimitationNamingController.endMultipleCardinality();
		}
		
		if(list.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
	}	
	public void visit(SEmpty empty) throws SAXException{
		if(exceptPatternContext714){
			// error 7.1.4
			if(empty.hasCardinalityElement())reportCardinalityElementError714(empty);
			else reportError714(empty);
		}
		if(startContext){
			// error 7.1.5
			if(empty.hasCardinalityElement())reportCardinalityElementError715(empty);
			else reportError715(empty);
		}
		if(attributeContext){
			// Serene limitation
			String message = "Unsupported schema configuration. For the moment serene does not support <empty> in the context of <attribute>, path: "				
				+"\n<"+attributesPath.peek().getQName()+"> at "+attributesPath.peek().getLocation(restrictToFileName)
				+"\n<"+empty.getQName()+"> at "+empty.getLocation(restrictToFileName)+".";		
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));			
		}
		contentType = ContentType.EMPTY;
	}
	public void visit(SText text) throws SAXException{
		if(listContext){
			// error 7.1.3
			reportError713(text);
		}
		if(exceptPatternContext714){
			// error 7.1.4
			if(text.hasCardinalityElement())reportCardinalityElementError714(text);
			else reportError714(text);
		}
		if(startContext){
			// error 7.1.5
			if(text.hasCardinalityElement())reportCardinalityElementError715(text);
			else reportError715(text);
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
	    if(exceptPatternContext714 && ref.hasCardinalityElement()){
			// error 7.1.4
			reportCardinalityElementError714(ref);
		}
		if(startContext && ref.hasCardinalityElement()){
			// error 7.1.5
			reportCardinalityElementError715(ref);
		}
		
		
		int index = ref.getDefinitionIndex();
        if(index < 0) return;
		if(handledDefinitions.contains(index)){
            contentType = definitionsContentTypes.get(definitionTopPatterns[index]);
            return;
        }
        
        
		if(ref.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
            }
        }       
        
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		boolean oldStartContext = startContext;
		if(ref.hasCardinalityElement()){
		    exceptPatternContext714 = false;
		    startContext = false;
		}
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		if(ref.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = true;
		    morePath.pushMultipleCardinalityPattern(ref);
		    if(moreInterleaveContext) moreInterleaveMoreContext = true;
		    elementLimitationNamingController.startMultipleCardinality();
			attributeLimitationNamingController.startMultipleCardinality();
		}
		
		
		
		//***************
		/*definitionTopPatterns[index].accept(this);
		handledDefinitions.add(index);
        definitionsContentTypes.put(definitionTopPatterns[index], contentType);*/
        handle:{
        if(recursionModel.isRecursiveDefinition(index)){			
			if(recursionModel.isRecursiveReference(ref)){
				if(isBlindBranch(index)){					
					ArrayList<SRef> b = definitionBlindLoops.get(index);
					if(b == null){
						b = new ArrayList<SRef>();
						definitionBlindLoops.set(index, b);
					}
					b.add(ref);
					handledDefinitions.add(index);
                    definitionsContentTypes.put(definitionTopPatterns[index], contentType);
					break handle;
				}
				if(isRequiredBranch(index)){
					if(hasAlternative(ref, index)){	
						break handle;
					}else{
						ArrayList<SRef> i = definitionInfiniteLoops.get(index);
						if(i == null){
							i = new ArrayList<SRef>();
							definitionInfiniteLoops.set(index, i);
						}
						i.add(ref);						
					}		
					handledDefinitions.add(index);
                    definitionsContentTypes.put(definitionTopPatterns[index], contentType);
					break handle;
				}
			}else{
				BooleanStack eStack = new BooleanStack();
				eStack.push(false);
				loopElement.set(index, eStack);
				
				BooleanStack oStack = new BooleanStack();
				oStack.push(false);
				loopOptional.set(index, oStack);
				
				Stack<SChoicePattern> cStack = new Stack<SChoicePattern>();
				loopChoice.set(index, cStack);
				
				recursiveDefinitionContext.push(true);
				
				definitionTopPatterns[index].accept(this);
				
				recursiveDefinitionContext.pop();
				
				loopElement.set(index, null);
				loopOptional.set(index, null);
				loopChoice.set(index, null);
				
				handledDefinitions.add(index);
                definitionsContentTypes.put(definitionTopPatterns[index], contentType);
				break handle;
			}
		}else{
			definitionTopPatterns[index].accept(this);
			handledDefinitions.add(index);
            definitionsContentTypes.put(definitionTopPatterns[index], contentType);
		}       
		}
        //*****************
        
        
        
        if(ref.hasCardinalityElement()){
		    exceptPatternContext714 = oldExceptPatternContext714;
		    startContext = oldStartContext;
		}
		
		if(ref.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = oldMoreContext;
		    morePath.pop();		    
		    moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		    elementLimitationNamingController.endMultipleCardinality();
			attributeLimitationNamingController.endMultipleCardinality();
		}
		
		if(ref.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
	}	
	//  !!! subclass !!!
	//------------------
	
	public void visit(SValue value) throws SAXException{
	    if(exceptPatternContext714 && value.hasCardinalityElement()){
			// error 7.1.4
			reportCardinalityElementError714(value);
		}
		if(startContext){
			// error 7.1.5
			if(value.hasCardinalityElement())reportCardinalityElementError715(value);
			else reportError715(value);
		}
        if((moreContext || moreMultiChildrenContext) && !(listContext || exceptPatternContext72)){
            // error 7.2 simple content type repeated in the context
			reportError72(value);
        }
		contentType = ContentType.SIMPLE;
	}
	public void visit(SData data)throws SAXException{
	    if(exceptPatternContext714 && data.hasCardinalityElement()){
			// error 7.1.4
			reportCardinalityElementError714(data);
		}
		if(startContext){
			// error 7.1.5
			if(data.hasCardinalityElement())reportCardinalityElementError715(data);
			else reportError715(data);
		}
		if((moreContext || moreMultiChildrenContext) && !(listContext || exceptPatternContext72)){
            // error 7.2 simple content type repeated in the context
			reportError72(data);
        }
        
        if(data.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
            }
        }
        
		boolean oldStartContext = startContext;
		startContext = false;
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		if(data.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = true;
		    morePath.pushMultipleCardinalityPattern(data);
		    if(moreInterleaveContext) moreInterleaveMoreContext = true;
		    elementLimitationNamingController.startMultipleCardinality();
			attributeLimitationNamingController.startMultipleCardinality();
		}
		
		dataPath.push(data);

		SimplifiedComponent[] exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) next(exceptPattern);
		
		startContext = oldStartContext;
		
		dataPath.pop();
		
		contentType = ContentType.SIMPLE;
		
		if(data.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = oldMoreContext;	
		    morePath.pop();
		    moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		    elementLimitationNamingController.endMultipleCardinality();
			attributeLimitationNamingController.endMultipleCardinality();
		}
		
		if(data.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
	}	
	public void visit(SGrammar grammar)throws SAXException{
	    if(exceptPatternContext714 && grammar.hasCardinalityElement()){
			// error 7.1.4
			reportCardinalityElementError714(grammar);
		}
		if(startContext && grammar.hasCardinalityElement()){
			// error 7.1.5
			reportCardinalityElementError715(grammar);
		}
		
		if(grammar.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
            }
        }
        
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		boolean oldStartContext = startContext;
		if(grammar.hasCardinalityElement()){
		    exceptPatternContext714 = false;
		    startContext = false;
		}
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		if(grammar.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = true;
		    morePath.pushMultipleCardinalityPattern(grammar);
		    if(moreInterleaveContext) moreInterleaveMoreContext = true;
		    elementLimitationNamingController.startMultipleCardinality();
			attributeLimitationNamingController.startMultipleCardinality();
		}
		
		SimplifiedComponent child = grammar.getChild();
		if(child != null) child.accept(this);
		
		if(grammar.hasCardinalityElement()){
		    exceptPatternContext714 = oldExceptPatternContext714;
		    startContext = oldStartContext;
		}
		
		if(grammar.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = oldMoreContext;	
		    morePath.pop();
		    moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		    elementLimitationNamingController.endMultipleCardinality();
			attributeLimitationNamingController.endMultipleCardinality();
		}
		
		if(grammar.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
	}
	
	public void visit(SDummy dummy)throws SAXException{
	    if(exceptPatternContext714 && dummy.hasCardinalityElement()){
			// error 7.1.4
			reportCardinalityElementError714(dummy);
		}
		if(startContext && dummy.hasCardinalityElement()){
			// error 7.1.5
			reportCardinalityElementError715(dummy);
		}
		
		if(dummy.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
            }
        }
        
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		boolean oldStartContext = startContext;
		if(dummy.hasCardinalityElement()){
		    exceptPatternContext714 = false;
		    startContext = false;
		}
		
		boolean oldMoreContext = moreContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		if(dummy.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = true;
		    morePath.pushMultipleCardinalityPattern(dummy);
		    if(moreInterleaveContext) moreInterleaveMoreContext = true;
		    elementLimitationNamingController.startMultipleCardinality();
			attributeLimitationNamingController.startMultipleCardinality();
		}
		
		SimplifiedComponent[] children = dummy.getChildren();
		if(children != null) next(children);
		
		if(dummy.hasCardinalityElement()){
		    exceptPatternContext714 = oldExceptPatternContext714;
		    startContext = oldStartContext;
		}
		
		if(dummy.getMaxOccurs() == SPattern.UNBOUNDED){
		    moreContext = oldMoreContext;	
		    morePath.pop();
		    moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		    elementLimitationNamingController.endMultipleCardinality();
			attributeLimitationNamingController.endMultipleCardinality();
		}
		
		if(dummy.getMinOccurs() == 0 && recursiveDefinitionContext.peek()){
            for(int i = 0; i < definitionCount; i++){
                if(loopOptional.get(i) != null)loopOptional.get(i).pop();
            }
        }
	}	

	
	
	/*public void visit(SElement element) throws SAXException{		
		if(attributeContext){
			// error 7.1.1	
			reportError711(element);			
		}
		if(listContext){
			// error 7.1.3
			reportError713(element);
		}
		if(exceptPatternContext714){
			// error 7.1.4
			reportError714(element);
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
		
        boolean oldHasSeveralChildren = hasSeveralChildren;
        hasSeveralChildren = false;
        
		boolean oldListContext = listContext;
		listContext = false;
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
        
        boolean oldExceptPatternContext72 = exceptPatternContext72;
		exceptPatternContext72 = false;
		
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
		
		if(recursiveDefinitionContext.peek())
		for(int i = 0; i < definitionCount; i++){
			if(loopElement.get(i) != null)loopElement.get(i).push(true);
		}
		
		SimplifiedComponent child = element.getChild();
		if(child != null) child.accept(this);
		
		if(recursiveDefinitionContext.peek())
		for(int i = 0; i < definitionCount; i++){
			if(loopElement.get(i) != null)loopElement.get(i).pop();
		}
				
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
        hasSeveralChildren = oldHasSeveralChildren;		
		listContext = oldListContext;
		exceptPatternContext714 = oldExceptPatternContext714;
        exceptPatternContext72 = oldExceptPatternContext72;
		startContext = oldStartContext;		
		moreAttributeContext = oldMoreAttributeContext;
		
		if(texts.size() > textsOffset)texts.removeTail(textsOffset);
		
		contentType = ContentType.COMPLEX;		
	}	
		
	public void visit(SChoicePattern choice) throws SAXException{
		elementNamingController.start(choice);
		attributeNamingController.start(choice);
		
        elementLimitationNamingController.start(choice);
		attributeLimitationNamingController.start(choice);
        
		boolean oldChoiceContext = choiceContext;
		choiceContext = true;
		
		boolean oldChoiceContainsText = choiceContainsText;
		choiceContainsText = false;

		if(recursiveDefinitionContext.peek())
		for(int i = 0; i < definitionCount; i++){
			if(loopChoice.get(i) != null)loopChoice.get(i).push(choice);
		}
		
		SimplifiedComponent[] children = choice.getChildren();		
		if(children != null) {
			int ct = ContentType.ERROR;
			for(SimplifiedComponent child : children){    
				
				child.accept(this);
				ct = ct > contentType ? ct : contentType;
			}
			contentType = ct;
		}
		
		if(recursiveDefinitionContext.peek())
		for(int i = 0; i < definitionCount; i++){
			if(loopChoice.get(i) != null)loopChoice.get(i).pop();
		}
		
		elementNamingController.end(choice);
		attributeNamingController.end(choice);
		
        elementLimitationNamingController.end(choice);
		attributeLimitationNamingController.end(choice);
        
		if(choiceContainsText)texts.add(choice);
		
		choiceContext = oldChoiceContext;
		choiceContainsText = oldChoiceContainsText;		
	}
	
	public void visit(SZeroOrMore zeroOrMore)throws SAXException{
		if(exceptPatternContext714){
			// error 7.1.4
			reportError714(zeroOrMore);
		}
		if(startContext){
			// error 7.1.5
			reportError715(zeroOrMore);
		}
		
		boolean oldMoreContext = moreContext;
		moreContext = true;
		morePath.push(zeroOrMore);
		
		boolean oldMoreInterleaveContext = moreInterleaveContext;
		boolean oldMoreInterleaveMoreContext = moreInterleaveMoreContext;
		
		if(moreInterleaveContext) moreInterleaveMoreContext = true;		
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
        elementLimitationNamingController.start(zeroOrMore);
		attributeLimitationNamingController.start(zeroOrMore);
        
		if(recursiveDefinitionContext.peek())
		for(int i = 0; i < definitionCount; i++){
			if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
		}
		
		SimplifiedComponent child = zeroOrMore.getChild();
		if(child != null) child.accept(this);
		
		if(recursiveDefinitionContext.peek())
		for(int i = 0; i < definitionCount; i++){
			if(loopOptional.get(i) != null)loopOptional.get(i).pop();
		}
		
        elementLimitationNamingController.end(zeroOrMore);
		attributeLimitationNamingController.end(zeroOrMore);
        
		moreContext = oldMoreContext;
		morePath.pop();
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		
		exceptPatternContext714 = oldExceptPatternContext714;
		startContext = oldStartContext;
	}
	
	public void visit(SOptional optional) throws SAXException{
		if(exceptPatternContext714){
			// error 7.1.4
			reportError714(optional);
		}
		if(startContext){
			// error 7.1.5
			reportError715(optional);
		}
		
		boolean oldExceptPatternContext714 = exceptPatternContext714;
		exceptPatternContext714 = false;
		
		boolean oldStartContext = startContext;
		startContext = false;
		
		if(recursiveDefinitionContext.peek())
		for(int i = 0; i < definitionCount; i++){
			if(loopOptional.get(i) != null)loopOptional.get(i).push(true);
		}
		
		SimplifiedComponent child = optional.getChild();
		if(child != null) child.accept(this);
		
		if(recursiveDefinitionContext.peek())
		for(int i = 0; i < definitionCount; i++){
			if(loopOptional.get(i) != null)loopOptional.get(i).pop();
		}
		
		exceptPatternContext714 = oldExceptPatternContext714;
		startContext = oldStartContext;
	}
	
	public void visit(SRef ref)throws SAXException{		
		int index = ref.getDefinitionIndex();
        if(index < 0) return;
		if(handledDefinitions.contains(index)){
            contentType = definitionsContentTypes.get(definitionTopPatterns[index]);
            return;
        }
        
		if(recursionModel.isRecursiveDefinition(index)){			
			if(recursionModel.isRecursiveReference(ref)){
				if(isBlindBranch(index)){					
					ArrayList<SRef> b = definitionBlindLoops.get(index);
					if(b == null){
						b = new ArrayList<SRef>();
						definitionBlindLoops.set(index, b);
					}
					b.add(ref);
					handledDefinitions.add(index);
                    definitionsContentTypes.put(definitionTopPatterns[index], contentType);
					return;
				}
				if(isRequiredBranch(index)){
					if(hasAlternative(ref, index)){	
						return;
					}else{
						ArrayList<SRef> i = definitionInfiniteLoops.get(index);
						if(i == null){
							i = new ArrayList<SRef>();
							definitionInfiniteLoops.set(index, i);
						}
						i.add(ref);						
					}		
					handledDefinitions.add(index);
                    definitionsContentTypes.put(definitionTopPatterns[index], contentType);
					return;
				}
			}else{
				BooleanStack eStack = new BooleanStack();
				eStack.push(false);
				loopElement.set(index, eStack);
				
				BooleanStack oStack = new BooleanStack();
				oStack.push(false);
				loopOptional.set(index, oStack);
				
				Stack<SChoicePattern> cStack = new Stack<SChoicePattern>();
				loopChoice.set(index, cStack);
				
				recursiveDefinitionContext.push(true);
				
				definitionTopPatterns[index].accept(this);
				
				recursiveDefinitionContext.pop();
				
				loopElement.set(index, null);
				loopOptional.set(index, null);
				loopChoice.set(index, null);
				
				handledDefinitions.add(index);
                definitionsContentTypes.put(definitionTopPatterns[index], contentType);
				return;
			}
		}else{
			definitionTopPatterns[index].accept(this);
			handledDefinitions.add(index);
            definitionsContentTypes.put(definitionTopPatterns[index], contentType);
		}		
	}*/
	
	
	private boolean isBlindBranch(int index){
		BooleanStack eStack = loopElement.get(index);
		return !eStack.peek();
	}
	private boolean isRequiredBranch(int index){
		BooleanStack oStack = loopOptional.get(index);
		return !oStack.peek();
	}
	private boolean hasAlternative(SRef ref, int index) throws SAXException{
		Stack<SChoicePattern> choices = loopChoice.get(index);
		if(choices == null || choices.isEmpty()){
			return false;
		}else{
			if(alternativeSeeker == null){
				alternativeSeeker = new AlternativeSeeker();
				alternativeSeeker.init(recursionModel,
								definitionBlindLoops,
								definitionInfiniteLoops,
								definitionOpenAlternatives,
								handledDefinitions);
			}
			return alternativeSeeker.hasAlternatives(ref, choices);			
		}
	}
			
	
}