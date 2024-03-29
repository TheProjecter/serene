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

import org.xml.sax.SAXException;

import org.relaxng.datatype.Datatype;

import serene.bind.util.DocumentIndexedData;

import serene.validation.handlers.match.DataMatchPath;
import serene.validation.handlers.match.ValueMatchPath;
import serene.validation.handlers.match.ListPatternMatchPath;
import serene.validation.handlers.match.TextMatchPath;
import serene.validation.handlers.match.ElementMatchPath;
import serene.validation.handlers.match.AttributeMatchPath;
import serene.validation.handlers.match.MatchPathPool;


public class SValue extends SNoChildrenPattern{
	String ns;
    Datatype datatype;
	String charContent;
	
	public SValue(String ns, 
				Datatype datatype,
				String charContent,
				int recordIndex, 
				DocumentIndexedData documentIndexedData){
		super(recordIndex, documentIndexedData);
		this.ns = ns;		
		this.datatype = datatype;
		this.charContent = charContent;
	}

	void setParent(SRule parent, int childIndex){		 
		this.parent = parent;				
		this.childIndex = childIndex;
		
		parent.setAllowsValues();
	}
	
	void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool){
        ValueMatchPath mp = matchPathPool.getValueMatchPath();
        mp.addValue(this);
        valueMatchPathes.add(mp);
    }
	void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, MatchPathPool matchPathPool){            
 	    ValueMatchPath mp = matchPathPool.getValueMatchPath();
        mp.addValue(this);
        valueMatchPathes.add(mp);
    }
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, MatchPathPool matchPathPool){
        ValueMatchPath mp = matchPathPool.getValueMatchPath();
        mp.addValue(this);
        valueMatchPathes.add(mp);
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
	    return true;
	}
	boolean isListPatternContent(){
	    return false;
	}
	boolean isTextContent(){
	    return false;
	}
	boolean isCharsContent(){
	    return true;
	}	
	boolean isStructuredDataContent(){
	    return true;
	}	
	boolean isUnstructuredDataContent(){
	    return true;
	}
	
	
	public String getNamespaceURI(){
		return ns;
	}
	public Datatype getDatatype(){
		return datatype;
	}
	public String getCharContent(){
		return charContent;
	}
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}	
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public void accept(SimplifiedRuleVisitor v){
		v.visit(this);
	}
	public String toString(){
		return "SValue datatype "+datatype+" charContent "+charContent;
	}
}