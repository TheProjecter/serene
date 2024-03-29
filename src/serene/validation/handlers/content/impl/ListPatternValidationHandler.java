/*
Copyright 2011 Radu Cernuta 

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

package serene.validation.handlers.content.impl;

import java.util.List;
import java.util.BitSet;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SListPattern;


import serene.validation.handlers.content.DataEventHandler;
import serene.validation.handlers.content.util.InputStackDescriptor;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.util.SpaceCharsHandler;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.TemporaryMessageStorage;

import serene.validation.handlers.match.UnstructuredDataMatchPath;

class ListPatternValidationHandler implements DataEventHandler,
                                        DataContentTypeHandler{
    ValidatorEventHandlerPool pool;
    InputStackDescriptor inputStackDescriptor;
    SpaceCharsHandler spaceHandler;
    
    AbstractSDVH parent;
    SListPattern listPattern;
        
    ErrorCatcher errorCatcher;    
    StackHandler stackHandler;
    ValidatorStackHandlerPool stackHandlerPool;
    
    ListPatternValidationHandler(){
    }
        
    
    void init(ValidatorEventHandlerPool pool, ValidatorStackHandlerPool stackHandlerPool, InputStackDescriptor inputStackDescriptor, SpaceCharsHandler spaceHandler){		
		this.inputStackDescriptor = inputStackDescriptor;		
		this.pool = pool;
		this.stackHandlerPool = stackHandlerPool;
		this.spaceHandler = spaceHandler; 
	}
    
	void init(SListPattern listPattern, AbstractSDVH parent, ErrorCatcher errorCatcher){
        this.listPattern = listPattern;
        this.parent = parent;
        this.errorCatcher = errorCatcher;
    }
    
    void reset(){
        parent = null;
        listPattern = null;
        
        errorCatcher = null;
    }
    
    public void recycle(){
        reset();        
        pool.recycle(this);
    } 
    
    public AbstractSDVH getParentHandler(){
        return parent;    
    }
    
    public void handleChars(char[] chars, SListPattern context) throws SAXException{
        // System.out.println("LIST PATTERN VALIDATION HANDLER chars="+new String(chars)+"  context="+context);
        stackHandler = stackHandlerPool.getContextStackHandler(listPattern, errorCatcher);
        
        DataValidationHandler dvh = pool.getDataValidationHandler(this, this, errorCatcher);        
        
        char[][] tokens = spaceHandler.removeSpace(chars);
        
		for(int i = 0; i < tokens.length; i++){
		    inputStackDescriptor.pushListToken(new String(tokens[i]), inputStackDescriptor.getSystemId(), inputStackDescriptor.getPublicId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber());
			dvh.handleChars(tokens[i], context); 
			dvh.reset();
			inputStackDescriptor.popListToken();
		}
		dvh.recycle();
				
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
    }
	
	public void handleString(String value, SListPattern context) throws SAXException{
	    stackHandler = stackHandlerPool.getContextStackHandler(listPattern, errorCatcher);
	    
	    DataValidationHandler dvh = pool.getDataValidationHandler(this, this, errorCatcher);
	    
	    char[][] tokens = spaceHandler.removeSpace(value.toCharArray());
        
		for(int i = 0; i < tokens.length; i++){
		    inputStackDescriptor.pushListToken(new String(tokens[i]), inputStackDescriptor.getSystemId(), inputStackDescriptor.getPublicId(), inputStackDescriptor.getLineNumber(), inputStackDescriptor.getColumnNumber());
			dvh.handleChars(tokens[i], context);   
			dvh.reset();
			inputStackDescriptor.popListToken();
		}
		dvh.recycle();
				
		stackHandler.endValidation();
		stackHandler.recycle();
		stackHandler = null;
	}
	
	
//StructuredDataContentTypeHandler
//==============================================================================
    public void addData(UnstructuredDataMatchPath data){        
        stackHandler.shift(data);
    }
	public void addData(List<UnstructuredDataMatchPath> candidateDefinitions, TemporaryMessageStorage[] temporaryMessageStorage){	    
	    if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, errorCatcher);
		    oldStackHandler.recycle();
		} 
		stackHandler.shiftAllTokenDefinitions(candidateDefinitions, temporaryMessageStorage);
	}
	
	public void addData(List<UnstructuredDataMatchPath> candidateDefinitions, BitSet disqualified, TemporaryMessageStorage[] temporaryMessageStorage){	    
	    if(!stackHandler.handlesConflict()){
		    StackHandler oldStackHandler = stackHandler;
		    stackHandler = stackHandlerPool.getConcurrentStackHandler(oldStackHandler, errorCatcher);
		    oldStackHandler.recycle();
		}
		stackHandler.shiftAllTokenDefinitions(candidateDefinitions, disqualified, temporaryMessageStorage);
	}
//==============================================================================

}
