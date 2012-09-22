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

abstract class SNoChildrenPattern extends SPattern{ 	 
	SNoChildrenPattern(int recordIndex, 
				DocumentIndexedData documentIndexedData){		
		super(recordIndex, documentIndexedData);
	}
		
	
	boolean isElementContent(){
        return false;
    }
	boolean isAttributeContent(){
	    return false;
	}
	boolean isDataContent(){
	    return false;
	}
	boolean isValueContent(){
	    return false;
	}
	boolean isListPatternContent(){
	    return false;
	}
	boolean isTextContent(){
	    return false;
	}
	boolean isCharsContent(){
	    return false;
	}	
	boolean isStructuredDataContent(){
	    return false;
	}	
	boolean isUnstructuredDataContent(){
	    return false;
	}
	
	
	public String toString(){
		String s = "AbstractNoChildrenPattern";
		return s;
	}
}