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

import java.util.List;

import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.ListPatternMatchPath;
import serene.validation.handlers.match.TextMatchPath;
import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.MatchPathPool;

import serene.bind.util.DocumentIndexedData;

abstract class SNoChildrenPattern extends SPattern{ 	 
	SNoChildrenPattern(int recordIndex, 
				DocumentIndexedData documentIndexedData){		
		super(recordIndex, documentIndexedData);
	}
		
	public SPattern getChild(int childIndex){
	    return null;
	}
	public boolean isChildRequired(int childIndex){
	    throw new IllegalStateException();
	}
	
	void setElementMatchPathes(String ns, String name, List<ElementMatchPath> pathes, MatchPathPool matchPathPool){}
	void setAttributeMatchPathes(String ns, String name, List<AttributeMatchPath> pathes, MatchPathPool matchPathPool){}
	void setMatchPathes(List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool){}
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool){}
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, MatchPathPool matchPathPool){}
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, MatchPathPool matchPathPool){}
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
	
	public int getChildrenCount(){
	    return 0;
	}
	public String toString(){
		String s = "AbstractNoChildrenPattern";
		return s;
	}
}