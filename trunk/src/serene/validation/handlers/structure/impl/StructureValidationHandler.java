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

package serene.validation.handlers.structure.impl;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.structure.ChildEventHandler;
import serene.validation.handlers.structure.RuleHandler;
import serene.validation.handlers.structure.StructureHandler;
import serene.validation.handlers.structure.RuleHandlerVisitor;

import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.conflict.InternalConflictResolver;
import serene.validation.handlers.conflict.StackConflictsHandler;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.error.ErrorCatcher;

import serene.util.IntList;

import sereneWrite.MessageWriter;

public abstract class StructureValidationHandler implements StructureHandler, ChildEventHandler{
	
	protected StructureHandler parent;
	
	protected ContentHandler contentHandler;
	protected ContentHandler noContent;
	protected ContentHandler openContent;
	protected ContentHandler satisfiedContent;
	protected ContentHandler saturatedContent;
	protected ContentHandler excessiveContent;


	protected ValidationItemLocator validationItemLocator;	
	protected ErrorCatcher errorCatcher; 
	protected StackHandler stackHandler;
	
	protected RuleHandlerRecycler recycler;
	
	protected String starttQName;
	protected String starttSystemId;
	protected int starttLineNumber;
	protected int starttColumnNumber;	
	
	
	protected MessageWriter debugWriter;
	
	StructureValidationHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;		
	}	
		
	void init(RuleHandlerRecycler recycler, ValidationItemLocator validationItemLocator){
		this.recycler = recycler;
		this.validationItemLocator = validationItemLocator; 
	}	
	
	//Start RuleHandler---------------------------------------------------------------
	public boolean isSatisfied(){
		return contentHandler.isSatisfied();
	}	
	public boolean isSaturated(){
		return contentHandler.isSaturated();
	}
	public void setStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
		throw new IllegalStateException();
	}
	//End RuleHandler-----------------------------------------------------------------
	
	
	//Start StructureHandler----------------------------------------------------------
	public StructureHandler getParentHandler(){
		return parent;
	}
	//StructureHandler getAncestorOrSelfHandler(Rule rule); subclass
	// StructureHandler getChildHandler(Rule child); subclass
	// Rule getRule(); subclass
	// boolean handleChildShift(AElement element, int expectedOrderHandlingCount); subclass
	// void handleValidatingReduce(); subclass
	// int functionalEquivalenceCode(); subclass	
	public abstract StructureValidationHandler getCopy(StackHandler stackHandler, ErrorCatcher errorCatcher);
	public abstract StructureValidationHandler getCopy(StructureHandler parent, StackHandler stackHandler, ErrorCatcher errorCatcher);
	public String getStartQName(){
		return starttQName;
	}
	public String getStartSystemId(){
		return starttSystemId;
	}
	public int getStartLineNumber(){
		return starttLineNumber;
	}
	public int getStartColumnNumber(){
		return starttColumnNumber;
	}		
	public String stackToString(){
		String s = "";
		if(parent != null){
			s+=" // "+parent.stackToString();
		}
		return toString()+s;
	}
	//End StructureHandler------------------------------------------------------------
	
	//Start ValidationHandler---------------------------------------------------------
	void handleReduce(){
		stackHandler.reduce(this);
	}
	
	abstract void handleParticleShift(String systemId, int lineNumber, int columnNumber, String qName, APattern childPattern);
	abstract void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler, InternalConflictResolver resolver);
	/*abstract void handleParticleShift(APattern childPattern, StackConflictsHandler stackConflictsHandler);*/	
			
	
	/**
	* Used to handle the order in groups and propagate the content order 
	* handling up to the highest group. It returns true if the errors must be 
	* reported and false if somewhere in the hierarchy there has been a reduce
	* reshift and no errors need to be reported since the entire thing is redone.
	* This reduce/reshift can only happen during order validation in group, all 
	* the other patterns are intermediary and simply forward the parent's return
	* value.
	*/
	// actually pattern is always a compositor, more precisely group or interleave
	// LATER really???	
	
	void setStart(){	
		starttSystemId = validationItemLocator.getSystemId();		
		starttLineNumber = validationItemLocator.getLineNumber();
		starttColumnNumber = validationItemLocator.getColumnNumber();
		starttQName = validationItemLocator.getQName();
	}	
	//End ValidationHandler-----------------------------------------------------------
	
	//Start ChildEventHandler---------------------------------------------------------
	public int getContentIndex(){
		int result = contentHandler.getContentIndex();
		return result;
	}
	public void childOpen(){
		contentHandler.childOpen();
	}	
	public void requiredChildSatisfied(){
		contentHandler.requiredChildSatisfied();
	}
	
	public void optionalChildSatisfied(){
		contentHandler.optionalChildSatisfied();
	}
	
	/*public void childSatisfiedPlus(){
		contentHandler.childSatisfiedPlus();
		return result;
	}*/
	
	public void childSaturated(){
		contentHandler.childSaturated();
	}
	
	public void childExcessive(){
		contentHandler.childExcessive();
	}	
		
	public int functionalEquivalenceCode(){
		throw new IllegalArgumentException("hehe functional equivalence!");
		//return contentHandler.functionalEquivalenceCode();
	}
	//End ChildEventHandler-----------------------------------------------------------
		
	public StructureHandler getCopy(IntList reduceCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	public StructureHandler getCopy(IntList reduceCountList, IntList startedCountList, StackHandler stackHandler, ErrorCatcher errorCatcher){
		throw new IllegalStateException();
	}
	
	protected abstract class ContentHandler implements RuleHandler, ChildEventHandler{
		public void recycle(){
			throw new UnsupportedOperationException();
		}
		public void accept(RuleHandlerVisitor visitor){
			throw new IllegalStateException();
		}
		public Rule getRule(){
			throw new UnsupportedOperationException();
		}
		public RuleHandler getOriginal(){
			throw new IllegalStateException();
		}
		public void setStackConflictsHandler(StackConflictsHandler stackConflictsHandler){
			throw new IllegalStateException();
		}
	}
		
	protected abstract class AbstractNoContent extends ContentHandler{
		public int getContentIndex(){
			return NO_CONTENT;
		}		
		/*public void childSatisfiedPlus(){
			throw new IllegalStateException();
		}*/	
		public void childExcessive(){			
			throw new IllegalStateException();
		}			
		public boolean isSatisfied(){
			return false;
		}
		public boolean isSaturated(){
			return false;
		}
		
		public int functionalEquivalenceCode(){
			throw new UnsupportedOperationException();
		}
		public String toString(){
			return "NO CONTENT";
		}		
	}
	
	protected abstract class AbstractOpenContent extends ContentHandler{
		public int getContentIndex(){
			return OPEN_CONTENT;
		}		
		public boolean isSatisfied(){
			return false;
		}
		public boolean isSaturated(){
			return false;
		}
		public int functionalEquivalenceCode(){
			throw new UnsupportedOperationException();
		}
		public String toString(){
			return "CONTENT OPEN";
		}		
	}
	protected abstract class AbstractSatisfiedContent extends ContentHandler{
		public int getContentIndex(){
			return SATISFIED_CONTENT;
		}
		public boolean isSaturated(){
			return false;
		}
		public int functionalEquivalenceCode(){
			throw new UnsupportedOperationException();
		}
		public String toString(){
			return "CONTENT SATISFIED";
		}		
	}	
	
	protected abstract class AbstractSaturatedContent extends ContentHandler{
		public int getContentIndex(){
			return SATURATED_CONTENT;
		}		
		public boolean isSaturated(){
			return true;
		}
		public int functionalEquivalenceCode(){
			throw new UnsupportedOperationException();
		}
		public String toString(){
			return "CONTENT SATURATED";
		}		
	}
	
	protected abstract class AbstractExcessiveContent extends ContentHandler{
		public int getContentIndex(){
			return EXCESSIVE_CONTENT;
		}
		public boolean isSaturated(){
			return true;
		}
		public int functionalEquivalenceCode(){
			throw new UnsupportedOperationException();
		}
		public String toString(){
			return "CONTENT EXCESSIVE";
		}		
	}
}