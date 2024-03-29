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

public abstract class SPattern extends SRule{
    int minOccurs;
    int maxOccurs;

    int cardinalityElementRecordIndex;
	DocumentIndexedData cardinalityElementDID;
    boolean hasCardinalityElement;	

	public SPattern(int recordIndex, 
			DocumentIndexedData documentIndexedData){		
		super(recordIndex, documentIndexedData);
		minOccurs = 1;
		maxOccurs = 1;
		hasCardinalityElement = false;
	}
		
	
	abstract void setElementMatchPathes(String ns, String name, List<ElementMatchPath> pathes, MatchPathPool matchPathPool);
	abstract void setAttributeMatchPathes(String ns, String name, List<AttributeMatchPath> pathes, MatchPathPool matchPathPool);
	abstract void setMatchPathes(List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool);
    abstract void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool);
    abstract void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, MatchPathPool matchPathPool);
    abstract void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, MatchPathPool matchPathPool);
	
    abstract boolean isElementContent();
	abstract boolean isAttributeContent();
	abstract boolean isDataContent();
	abstract boolean isValueContent();
	abstract boolean isListPatternContent();
	abstract boolean isTextContent();
	
	abstract boolean isCharsContent();
    abstract boolean isStructuredDataContent();
    abstract boolean isUnstructuredDataContent();
    
    	
	public boolean hasMultipleCardinality(){
	    return maxOccurs == UNBOUNDED;
	}
	
    public boolean isRequiredContent(){
        return minOccurs > 0;
    }
    void adjust(){
        throw new IllegalStateException();
    }
    
       
    public boolean isRequired(){
        return minOccurs > 0;
    }
	public int getMinOccurs(){
	    return minOccurs;
	}
	public int getMaxOccurs(){
	    return maxOccurs;
	}
	
	public boolean hasCardinalityElement(){
	    return hasCardinalityElement;
	}
	
	public int getCardinalityElementRecordIndex(){
	    return cardinalityElementRecordIndex;
	}
	
	public DocumentIndexedData getCardinalityElementDocumentIndexedData(){
	    return cardinalityElementDID;
	}
	public String getCardinalityElementQName(){
		return cardinalityElementDID.getItemDescription(cardinalityElementRecordIndex);
	}
	public String getCardinalityElementLocation(boolean restrictToFileName){
	    String si = cardinalityElementDID.getSystemId(cardinalityElementRecordIndex);
	    int ln = cardinalityElementDID.getLineNumber(cardinalityElementRecordIndex);
	    if(ln == DocumentIndexedData.UNKNOWN){
	        if(si == null || !restrictToFileName)return si;
	        return getRestrictedSystemId(si);
	    }
	    
	    int cn = cardinalityElementDID.getColumnNumber(cardinalityElementRecordIndex);
	    
        if(si == null || !restrictToFileName){
            return si+":"+ln+":"+cn;
        }
        return getRestrictedSystemId(si)+":"+ln+":"+cn;
	}
	
	void setOneOrMore(int recordIndex, DocumentIndexedData documentIndexedData){
	    maxOccurs = UNBOUNDED; 
	    cardinalityElementRecordIndex = recordIndex;
	    cardinalityElementDID = documentIndexedData;
	    hasCardinalityElement = true;
	}
	
	void setZeroOrMore(int recordIndex, DocumentIndexedData documentIndexedData){
	    minOccurs = 0;
	    maxOccurs = UNBOUNDED;
	    cardinalityElementRecordIndex = recordIndex;
	    cardinalityElementDID = documentIndexedData;
	    hasCardinalityElement = true;
	}
	
	void setOptional(int recordIndex, DocumentIndexedData documentIndexedData){
	    minOccurs = 0;
	    cardinalityElementRecordIndex = recordIndex;
	    cardinalityElementDID = documentIndexedData;
	    hasCardinalityElement = true;
	}	
}	