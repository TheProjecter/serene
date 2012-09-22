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

public class SData extends SNoChildrenPattern{
    Datatype datatype;
	SExceptPattern[] exceptPattern; 	
		
	public SData(Datatype datatype, 
	            SExceptPattern[] exceptPattern, 
	            int recordIndex, 
				DocumentIndexedData documentIndexedData){
		super(recordIndex, documentIndexedData);
		this.datatype = datatype;
		asParent(exceptPattern);
	}
	
	void setParent(SRule parent, int childIndex){		 
		this.parent = parent;				
		this.childIndex = childIndex;
	
        parent.setAllowsDatas();
	}
		
	
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, List<TextMatchPath> textMatchPathes, MatchPathPool matchPathPool){
        DataMatchPath mp = matchPathPool.getDataMatchPath();
        mp.addData(this);
        dataMatchPathes.add(mp);
    }
	void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, List<ListPatternMatchPath> listPatternMatchPathes, MatchPathPool matchPathPool){            
 	    DataMatchPath mp = matchPathPool.getDataMatchPath();
        mp.addData(this);
        dataMatchPathes.add(mp);
    }
    void setMatchPathes(List<DataMatchPath> dataMatchPathes, List<ValueMatchPath> valueMatchPathes, MatchPathPool matchPathPool){
        DataMatchPath mp = matchPathPool.getDataMatchPath();
        mp.addData(this);
        dataMatchPathes.add(mp);
    }
    
	boolean isElementContent(){
        return false;
    }
	boolean isAttributeContent(){
	    return false;
	}
	boolean isDataContent(){
	    return true;
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
	    return true;
	}	
	boolean isStructuredDataContent(){
	    return true;
	}	
	boolean isUnstructuredDataContent(){
	    return true;
	}
	
	
	public Datatype getDatatype(){
		return datatype;
	}
    
	protected void asParent(SExceptPattern[] exceptPattern){		
		this.exceptPattern = exceptPattern;
		if(exceptPattern != null){		
			for(int i = 0; i< exceptPattern.length; i++){
				exceptPattern[i].setParent(this, i);
			}
		}
	}	
	
	public SExceptPattern[] getExceptPattern(){
		return exceptPattern;
	}
	
	public SExceptPattern getExceptPattern(int childIndex){
		if(exceptPattern == null || exceptPattern.length == 0)return null;
		return exceptPattern[childIndex];
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
		return "SData datatype "+datatype;
	}
}