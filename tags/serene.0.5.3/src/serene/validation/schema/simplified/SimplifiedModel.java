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

import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SAttribute;

import sereneWrite.MessageWriter;
import sereneWrite.SimplifiedComponentWriter;

public class SimplifiedModel{
	
    SimplifiedComponent schemaStart;
    
	SPattern[] startTopPattern;
	SPattern[] refDefinitionTopPattern;

	RecursionModel recursionModel;
    
	MessageWriter debugWriter;
	SimplifiedComponentWriter writer;
	
	public SimplifiedModel(SimplifiedComponent schemaStart,
                                SPattern[] startTopPattern,
								SPattern[] refDefinitionTopPattern,
								RecursionModel recursionModel,
								MessageWriter debugWriter){
		this.debugWriter = debugWriter;
        this.schemaStart = schemaStart;
		this.startTopPattern = startTopPattern;
		this.refDefinitionTopPattern = refDefinitionTopPattern;
		this.recursionModel = recursionModel;
        
		writer = new SimplifiedComponentWriter();
	}
	
    /*public String getStartQName(){
        return startQName;
    }
    
    public String getStartLocation(){
        return startLocation;        
    }*/
    
    public SimplifiedComponent getSchemaStart(){
        return schemaStart;
    }
    
	public SPattern[] getStartTopPattern(){
		return startTopPattern;
	}
	
	public SPattern[] getRefDefinitionTopPattern(){
		return refDefinitionTopPattern;
	}
	    
	public RecursionModel getRecursionModel(){
		return recursionModel;
	}
	
	public boolean hasRecursions(){
		return recursionModel.hasRecursions();
	}
}