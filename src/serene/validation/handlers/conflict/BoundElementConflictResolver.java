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


package serene.validation.handlers.conflict;

import java.util.BitSet;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.relaxng.datatype.Datatype;

import serene.validation.schema.active.components.AElement;

import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ConflictMessageReporter;

import serene.bind.util.Queue;
import serene.bind.BindingModel;


public abstract class BoundElementConflictResolver extends ElementConflictResolver{
    BindingModel bindingModel;
    
	Queue targetQueue;
	int reservationStartEntry;
    int reservationEndEntry;	
	
	Map<AElement, Queue> candidateQueues;
	
	public BoundElementConflictResolver(){
		super();
	}
	
	void init(ConflictMessageReporter conflictMessageReporter,
	        BindingModel bindingModel,
	        Queue targetQueue,
			int reservationStartEntry,
			int reservationEndEntry,
			Map<AElement, Queue> candidateQueues){    
		super.init(conflictMessageReporter);
		this.bindingModel = bindingModel;
		this.targetQueue = targetQueue;
		this.reservationStartEntry = reservationStartEntry;
		this.reservationEndEntry = reservationEndEntry;		
		this.candidateQueues = candidateQueues;
	}
	
	
	void reset(){
	    super.reset();
		targetQueue = null;
		reservationStartEntry = -1;
		candidateDefinitions.clear();
		Collection<Queue> queues = candidateQueues.values();
		for(Queue qq : queues)qq.recycle();
		candidateQueues = null;
	}
    
	
	void closeReservation(int qual){        				
        Queue qq = candidateQueues.get(candidateDefinitions.get(qual));
        targetQueue.useReservation(reservationStartEntry, qq, 0, qq.getSize()-1);        
    }
    
    void closeReservation(){ 
        /*for(Queue qq : candidateQueues.values()){
            if(qq != null){
                int size = qq.getSize();*/
                /*qq.clearContent();
                qq.addIndexCorrespondence(size-1, 0);
                ElementBinder fallbackBinder = bindingModel.getElementBinder();
                fallbackBinder.bindTasks(qq, size-1);*/
                /*targetQueue.replaceSection(0, 
                                        -1, 
                                        bindingModel.getGenericStartElementTask(),
                                        size-1,
                                        bindingModel.getGenericEndElementTask());                
                targetQueue.closeReservation(reservationStartEntry, qq);
                return;
            }            
        }   
        */       
        targetQueue.replaceSection(reservationStartEntry,
                                        bindingModel.getGenericStartElementTask(),
                                        reservationEndEntry,
                                        bindingModel.getGenericEndElementTask());                
        targetQueue.cancelReservation(reservationStartEntry);
    }
    
	public String toString(){
		return "BoundElementConflictResolver ";
	}
}
