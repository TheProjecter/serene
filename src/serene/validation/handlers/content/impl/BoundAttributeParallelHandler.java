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
import java.util.ArrayList;

import org.xml.sax.SAXException;

import serene.validation.handlers.content.AttributeEventHandler;
import serene.validation.handlers.content.BoundAttributeHandler;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.CandidatesConflictErrorHandler;

import serene.bind.BindingModel;
import serene.bind.util.Queue;

import sereneWrite.MessageWriter;

class BoundAttributeParallelHandler extends AttributeParallelHandler{

    BoundCommon boundCommon;
    BoundConflict boundConflict;
    
    BindingModel bindingModel;
	Queue queue;
	int entry;
    
    BoundAttributeParallelHandler(MessageWriter debugWriter){
        super(debugWriter);
        individualHandlers = new ArrayList<ComparableAEH>();
    }
            
    void initStates(){
        boundCommon = new BoundCommon();
        boundConflict = new BoundConflict();
        state = boundCommon;
    }
    
    public void recycle(){
        for(AttributeEventHandler individualHandler: individualHandlers){
			individualHandler.recycle();
		}		
		individualHandlers.clear();
        
        boundCommon.reset();
        state = boundCommon;
		pool.recycle(this);
    }
    
    void init(CandidatesEEH parent, ExternalConflictHandler candidatesConflictHandler, CandidatesConflictErrorHandler elementCandidatesConflictErrorHandler, BindingModel bindingModel, Queue queue, int entry){        
        super.init(parent, candidatesConflictHandler, elementCandidatesConflictErrorHandler);
        
        this.bindingModel = bindingModel;
		this.queue = queue;
		this.entry = entry;
    }
    
    public CandidatesEEH getParentHandler(){
        return parent;
    }
    
    void add(ComparableAEH individualHandler){
        state.add(individualHandler);
    }
    
    public void handleAttribute(String value) throws SAXException{
        state.handleAttribute(value);
    }
    
    void validateValue(String value){
        throw new IllegalStateException();
    }
    
    void validateInContext(){
        throw new IllegalStateException();
    }
      
    
    class BoundCommon extends State{
        ComparableAEH uniqueSample;
        boolean isQualifiedSample;
        
        void reset(){   
            if(uniqueSample == null) return;
            
            ComparableEEH parent = uniqueSample.getParentHandler();
            if(parent instanceof ValidatingEEH)((ValidatingEEH)parent).restorePreviousHandler();
            
            uniqueSample = null;
            isQualifiedSample = false;
        }
        
        void add(ComparableAEH individualHandler){
            if(uniqueSample == null ){
                isQualifiedSample = !candidatesConflictHandler.isDisqualified(0);
                uniqueSample = individualHandler;
                //if(uniqueSample instanceof ValidatingEEH)((ValidatingEEH)uniqueSample).setCommon(candidatesConflictErrorHandler);
                ComparableEEH parent = uniqueSample.getParentHandler();
                if(parent instanceof ValidatingEEH)((ValidatingEEH)parent).setContextErrorHandlerIndex(ValidatingEEH.COMMON);
            }else{
                if(!individualHandler.functionalEquivalent(uniqueSample)){
                    state = boundConflict;
                    //if(uniqueSample instanceof ValidatingEEH)((ValidatingEEH)uniqueSample).restorePreviousHandler();
                    reset();
                }else if(!isQualifiedSample && !candidatesConflictHandler.isDisqualified(individualHandlers.size())){
                    //if(uniqueSample instanceof ValidatingEEH){
                        //((ValidatingEEH)uniqueSample).restorePreviousHandler();
                        //((ValidatingEEH)individualHandler).setCommon(candidatesConflictErrorHandler);
                    //}
                    
                    ComparableEEH sampleParent = uniqueSample.getParentHandler();
                    if(sampleParent instanceof ValidatingEEH)((ValidatingEEH)sampleParent).restorePreviousHandler();
                    ComparableEEH parent = individualHandler.getParentHandler();
                    if(parent instanceof ValidatingEEH)((ValidatingEEH)parent).setContextErrorHandlerIndex(ValidatingEEH.COMMON);
                    
                    uniqueSample = individualHandler;
                    isQualifiedSample = true;
                }
            }            
					
			individualHandlers.add(individualHandler);	
        }
        
        void handleAttribute(String value) throws SAXException{
            uniqueSample.handleAttribute(value);
            if(uniqueSample instanceof BoundAttributeHandler){
                for(int i = 0; i < individualHandlers.size(); i++){
                    // TODO 
                    // If you knew the candidate number of the corresponding element
                    // you could avoid binding for disqualified candidates.
                    if(!individualHandlers.get(i).equals(uniqueSample)){
                        ((BoundAttributeHandler)individualHandlers.get(i)).attributeBinding();
                    }
                }			
            }
            reset();
        }
    }
    int getHashCode(){return hashCode();}
    
    class BoundConflict extends Conflict{
        void add(ComparableAEH individualHandler){
            individualHandlers.add(individualHandler);
        }
       
    }
}
