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

import serene.validation.schema.simplified.components.SNameClass;
import serene.validation.schema.simplified.components.SPattern;
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


import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.handlers.error.ErrorDispatcher;

import sereneWrite.MessageWriter;

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
	
	
	public RRController(ControllerPool pool, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		super(pool, errorDispatcher, debugWriter);
		
		loopElement = new ArrayList<BooleanStack>();
		loopOptional = new ArrayList<BooleanStack>();
		loopChoice = new ArrayList<Stack<SChoicePattern>>();
		recursiveDefinitionContext = new BooleanStack();

		definitionBlindLoops = new ArrayList<ArrayList<SRef>>();
		definitionInfiniteLoops = new ArrayList<ArrayList<SRef>>();
		definitionOpenAlternatives = new ArrayList<Map<SRef, ArrayList<SRef>>>();
		
		openAlternativesHandler = new OpenAlternativesHandler(debugWriter);
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
						 +"No element definition in recursion loop path for element <"+ref.getQName()+"> at "+ref.getLocation()+".";
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
						 +"Infinite recursion loop for element <"+ref.getQName()+"> at "+ref.getLocation()+".";
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
		
	public void visit(SChoicePattern choice) throws SAXException{
		elementNamingController.start(choice);
		attributeNamingController.start(choice);
		
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
		
		if(choiceContainsText)texts.add(choice);
		
		choiceContext = oldChoiceContext;
		choiceContainsText = oldChoiceContainsText;		
	}
	
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
			//System.out.println(" 14 "+message);
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
		
		moreContext = oldMoreContext;
		morePath.pop();
		moreInterleaveContext = oldMoreInterleaveContext;
		moreInterleaveMoreContext = oldMoreInterleaveMoreContext;
		
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
	}
	
	public void visit(SOptional optional) throws SAXException{
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
			//System.out.println(" 15 "+message);
			errorDispatcher.error(new SAXParseException(message, null));
		}
		
		boolean oldExceptPatternContext = exceptPatternContext;
		exceptPatternContext = false;
		
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
		
		exceptPatternContext = oldExceptPatternContext;
		startContext = oldStartContext;
	}
	
	public void visit(SRef ref)throws SAXException{		
		int index = ref.getDefinitionIndex();
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
			return;
		}		
	}
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
				alternativeSeeker = new AlternativeSeeker(debugWriter);
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