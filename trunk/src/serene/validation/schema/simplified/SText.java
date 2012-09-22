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

import org.xml.sax.SAXException;

import serene.bind.util.DocumentIndexedData;

public class SText extends SNoChildrenPattern{
	boolean addedBySimplification;
	public SText(int recordIndex, 
				DocumentIndexedData documentIndexedData,
				boolean addedBySimplification){
		super(recordIndex, documentIndexedData);
		this.addedBySimplification = addedBySimplification;
		minOccurs = 0;
		maxOccurs = UNBOUNDED;	
	}	
		
	void setParent(SRule parent, int childIndex){		 
		this.parent = parent;				
		this.childIndex = childIndex;
		
		parent.setAllowsText();
	}
	
	public boolean isTextContent(){
	    return true;
	}
	public boolean isCharsContent(){
	    return true;
	}	
	
	
	void setOneOrMore(int recordIndex, DocumentIndexedData documentIndexedData){
	    cardinalityElementRecordIndex = recordIndex;
	    cardinalityElementDID = documentIndexedData;
	    hasCardinalityElement = true;
	}
	
	void setZeroOrMore(int recordIndex, DocumentIndexedData documentIndexedData){
	    cardinalityElementRecordIndex = recordIndex;
	    cardinalityElementDID = documentIndexedData;
	    hasCardinalityElement = true;
	}
	
	void setOptional(int recordIndex, DocumentIndexedData documentIndexedData){
	    cardinalityElementRecordIndex = recordIndex;
	    cardinalityElementDID = documentIndexedData;
	    hasCardinalityElement = true;
	}
		
	public String getQName(){
	    if(addedBySimplification) return "text added by "+documentIndexedData.getLocalName(recordIndex)+" simplification";
        return documentIndexedData.getItemDescription(recordIndex);   
	}	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}	
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String toString(){
		String s = "SText";		
		return s;
	}
}