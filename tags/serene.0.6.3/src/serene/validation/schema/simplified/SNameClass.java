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

package serene.validation.schema.simplified;

import serene.bind.util.DocumentIndexedData;

public abstract class SNameClass extends SComponent implements Identifier{	
    SimplifiedComponent parent;
	public SNameClass(int recordIndex, 
			DocumentIndexedData documentIndexedData){
	    // TODO 
	    // Consider null the location data, if it is never needed (restrictions 
	    // control?). Changes should be made in the constructors of the 
	    // subclasses. 
		super(recordIndex, documentIndexedData);
	}
	
	public SimplifiedComponent getParent(){		 
		return parent;				
	}
	
	void setParent(SimplifiedComponent parent, int childIndex){		 
		this.parent = parent;				
		this.childIndex = childIndex;
	}
	
	public String toString(){
		return "abstract SNameClass";
	}	
}