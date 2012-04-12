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

package serene.bind;

import serene.Reusable;

import serene.util.ObjectIntHashMap;

import sereneWrite.MessageWriter;

public class ValidatorQueuePool implements Reusable{
	BindingPool pool;
	
	MessageWriter debugWriter;
	
	int queueFree, queueMaxSize;
	Queue[] queue;
	
	ObjectIntHashMap sattributeIndexMap;
	
	public ValidatorQueuePool(BindingPool pool, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.pool = pool;
		
		queueFree = 0;
		queueMaxSize = 10;
		queue = new Queue[5];
	}
	
	public void recycle(){
		pool.recycle(this);
	}
	
	public void index(ObjectIntHashMap sattributeIndexMap){
		this.sattributeIndexMap = sattributeIndexMap;
	}
	public Queue getQueue(){
		if(queueFree == 0){
			Queue q = new Queue(debugWriter);
			q.init(this);
			if(sattributeIndexMap != null)q.index(sattributeIndexMap);
			return q;			
		}
		else{				
			Queue q = queue[--queueFree];
			if(sattributeIndexMap != null)q.index(sattributeIndexMap);
			return q;	
		}
	}
	public void recycle(Queue q){
	    if(queueFree == queueMaxSize) return;
		if(queueFree == queue.length){	
			Queue[] increased = new Queue[5+queue.length];
			System.arraycopy(queue, 0, increased, 0, queueFree);
			queue = increased;
		}
		queue[queueFree++] = q;
	}
}