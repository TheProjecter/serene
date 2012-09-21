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

import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SExceptPattern;

import sereneWrite.SimplifiedComponentWriter;

public class SimplifiedModel{
    
	SimplifiedPattern[] startTopPattern;
	SimplifiedPattern[] refDefinitionTopPattern;
	
	int startElementIndex;
    SElement[] selements;
    SAttribute[] sattributes;
    SExceptPattern[] sexceptPatterns;

	RecursionModel recursionModel;
	
	public SimplifiedModel(SimplifiedPattern[] startTopPattern,
								SimplifiedPattern[] refDefinitionTopPattern,
								int startElementIndex,
								SElement[] selements,
								SAttribute[] sattributes,
								SExceptPattern[] sexceptPatterns,
								RecursionModel recursionModel){
		this.startTopPattern = startTopPattern;
		this.refDefinitionTopPattern = refDefinitionTopPattern;
		this.recursionModel = recursionModel;
	
		this.startElementIndex = startElementIndex;
        this.selements = selements;
        this.sattributes = sattributes;
        this.sexceptPatterns = sexceptPatterns;
        
		/*SimplifiedComponentWriter scw = new SimplifiedComponentWriter();
		for(SimplifiedPattern stp : startTopPattern){
		    scw.write(stp);
		}*/		
	}
	
    
	public SimplifiedPattern[] getStartTopPattern(){
		return startTopPattern;
	}
	
	public SimplifiedPattern[] getRefDefinitionTopPattern(){
		return refDefinitionTopPattern;
	}
	 
	public int getStartElementIndex(){
	    return startElementIndex;
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