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


public class SGroup extends SMultipleChildrenPattern{
    
	int satisfactionIndicator;
	int saturationIndicator;
	
    boolean addedBySimplification;
	public SGroup(SPattern[] children,
				int recordIndex, 
				DocumentIndexedData documentIndexedData,
				boolean addedBySimplification){	
		super(children, 
                recordIndex, 
                documentIndexedData);
		this.addedBySimplification = addedBySimplification;
	}
	
	
	void asParent(SPattern[] children){
		this.children = children;
		satisfactionIndicator = 0;
		saturationIndicator = 0;
		
		if(children != null){		
			for(int i = 0; i< children.length; i++){				
				children[i].setParent(this, i);
				if(children[i].getMinOccurs() > 0) satisfactionIndicator++;
				if(children[i].getMaxOccurs() != UNBOUNDED)saturationIndicator++;
			}
		}
	}
	public boolean specifiesOrder(){
	    return true;
	}
	
	public int getSatisfactionIndicator(){
		return satisfactionIndicator;
	}
	
	public int getSaturationIndicator(){
		return saturationIndicator;
	}
	
	
	public String getQName(){
	    if(addedBySimplification) return "group added by "+documentIndexedData.getLocalName(recordIndex)+" simplification";
        return documentIndexedData.getItemDescription(recordIndex);   
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
		String s = "SGroup";		
		return s;
	}
}