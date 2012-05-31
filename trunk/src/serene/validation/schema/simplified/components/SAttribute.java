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

package serene.validation.schema.simplified.components;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.RestrictingVisitor;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import serene.validation.schema.DefinitionPointer;

import serene.bind.util.DocumentIndexedData;

public class SAttribute extends AbstractMultipleChildrenPattern implements DefinitionPointer{
	SNameClass nameClass;
	int defaultValueRecordIndex;
	
	int definitionIndex;
	public SAttribute(int definitionIndex,
	                            SNameClass nameClass, 
								SPattern[] children,
                                int defaultValue,
								int recordIndex, 
								DocumentIndexedData documentIndexedData){		
		super(children, recordIndex, documentIndexedData);
		this.nameClass = nameClass;
        this.defaultValueRecordIndex = defaultValue;
        this.definitionIndex = definitionIndex;
	}	
	
	public int getDefinitionIndex(){
	    return definitionIndex;
	}
	
	public SNameClass getNameClass(){
		return nameClass;
	}
	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
    public String getDefaultValue(){
        if(defaultValueRecordIndex == DocumentIndexedData.NO_RECORD) return null;
		return documentIndexedData.getStringValue(defaultValueRecordIndex);
    }    
	public String toString(){
		String s = "SAttribute "+nameClass.toString()+" defaultValue "+getDefaultValue();		
		return s;
	}
}