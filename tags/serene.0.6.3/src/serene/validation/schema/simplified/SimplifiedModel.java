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

import java.util.ArrayList;

import org.relaxng.datatype.DatatypeLibrary;

import sereneWrite.SimplifiedComponentWriter;

public class SimplifiedModel{	
    
	int startElementIndex;
    SElement[] selements;
    SAttribute[] sattributes;
    SExceptPattern[] sexceptPatterns;

    SPattern[] startTopPatterns;
    ReferenceModel referenceModel;
	RecursionModel recursionModel;
	
	public SimplifiedModel(int startElementIndex,
								SElement[] selements,
								SAttribute[] sattributes,
								SExceptPattern[] sexceptPatterns,
								SPattern[] startTopPatterns,
								ReferenceModel referenceModel,
								RecursionModel recursionModel){
	    this.startTopPatterns = startTopPatterns;
		this.recursionModel = recursionModel;
		this.referenceModel = referenceModel;
	
		this.startElementIndex = startElementIndex;
        this.selements = selements;
        this.sattributes = sattributes;
        this.sexceptPatterns = sexceptPatterns;
        
		/*SimplifiedComponentWriter scw = new SimplifiedComponentWriter();
		for(SPattern stp : startTopPattern){
		    scw.write(stp);
		}*/		
		
		if(recursionModel != null && recursionModel.hasRecursions())
	        recursionModel.adjustCach();
	}
	
	
	public SPattern[] getStartTopPatterns(){
		return startTopPatterns;
	}
    	
	public SPattern[] getRefDefinitionTopPatterns(){
		return referenceModel.getRefDefinitionTopPatterns();
	}
	 
	public SPattern getRefDefinitionTopPattern(int index){
		return referenceModel.getRefDefinitionTopPattern(index);
	}
	
	public int getStartElementIndex(){
	    return startElementIndex;
	}
	
	public SElement getStartElement(){
	    return selements[startElementIndex];
	}	
	
	public SElement[] getElementDefinitions(){
	    return selements;
	}
	
	public SAttribute[] getAttributeDefinitions(){
	    return sattributes;
	}
	
	public SExceptPattern[] getExceptPatternDefinitions(){
	    return sexceptPatterns;
	}
	
	public RecursionModel getRecursionModel(){
		return recursionModel;
	}
	
	public boolean hasRecursions(){
		return recursionModel.hasRecursions();
	}	
	
}