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

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.handlers.error.CandidatesConflictErrorHandler;

import sereneWrite.MessageWriter;

class AttributeParallelHandler extends ValidatingAEH{

    CandidatesEEH parent;
    
    ArrayList<ComparableAEH> individualHandlers;
    ExternalConflictHandler candidatesConflictHandler;
    CandidatesConflictErrorHandler elementCandidatesConflictErrorHandler;
    
    MessageWriter debugWriter;

    State state;
	Common common;
	Conflict conflict;
    
    AttributeParallelHandler(MessageWriter debugWriter){
        super(debugWriter);
        individualHandlers = new ArrayList<ComparableAEH>();
        initStates();
    }
            
    void initStates(){
        common = new Common();
		conflict = new Conflict();
        state = common;
    }
    
    public void recycle(){
        for(AttributeEventHandler individualHandler: individualHandlers){
			individualHandler.recycle();
		}		
		individualHandlers.clear();
        
        common.reset();
        state = common;
		pool.recycle(this);
    }
    
    void init(CandidatesEEH parent, ExternalConflictHandler candidatesConflictHandler, CandidatesConflictErrorHandler elementCandidatesConflictErrorHandler){
        this.parent = parent;
        this.candidatesConflictHandler = candidatesConflictHandler;
        this.elementCandidatesConflictErrorHandler = elementCandidatesConflictErrorHandler;
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
    
    boolean functionalEquivalent(ComparableAEH other){
        return other.functionalEquivalent(this);
    }
    
    boolean functionalEquivalent(AttributeDefinitionHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnexpectedAmbiguousAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(UnknownAttributeHandler other){
        return false;
    }
	boolean functionalEquivalent(AttributeConcurrentHandler other){
        return false;        
	}	
	boolean functionalEquivalent(AttributeParallelHandler other){
        return other.functionalEquivalent(individualHandlers);
    }
    private boolean functionalEquivalent(List<ComparableAEH> otherIndividualHandlers){
		int candidatesCount = individualHandlers.size();
		if(candidatesCount != otherIndividualHandlers.size()) return false;
		for(int i = 0; i < candidatesCount; i++){
			if(!individualHandlers.get(i).functionalEquivalent(otherIndividualHandlers.get(i))) return false;
		}
		return true;
	}
    boolean functionalEquivalent(AttributeDefaultHandler other){
        return false;
    }
    abstract class State{
        abstract void add(ComparableAEH individualHandler);
        abstract void handleAttribute(String value) throws SAXException;
    }
    
    class Common extends State{
        ComparableAEH uniqueSample;
        boolean isQualifiedSample;
        
        void reset(){
            if(uniqueSample == null) return;
            
            ComparableEEH sampleParent = uniqueSample.getParentHandler();
            if(sampleParent instanceof ValidatingEEH)((ValidatingEEH)sampleParent).restorePreviousHandler();
            
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
                    state = conflict;
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
            reset();
        }
    }
    class Conflict extends State{
        void add(ComparableAEH individualHandler){
            individualHandlers.add(individualHandler);
        }
        
        void handleAttribute(String value) throws SAXException{
            for(ComparableAEH individualHandler : individualHandlers){
                individualHandler.handleAttribute(value);
            }
        }
    }
}
