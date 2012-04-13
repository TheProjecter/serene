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

package serene.internal;

import java.io.File;

import java.net.URL;

import javax.xml.transform.Source;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;

import org.relaxng.datatype.DatatypeException;

import serene.SchemaModel;

import serene.validation.schema.ValidationModel;
import serene.validation.schema.ValidationModelImpl;

import serene.validation.schema.simplified.SimplifiedComponentBuilder;
import serene.validation.schema.simplified.SimplifiedModel;


public abstract class InternalRNGFactory{	
	
	SimplifiedComponentBuilder builder;	
	RNGDirector rngDirector;

	SimplifiedModel rngModel;
	SimplifiedModel includeModel;
	SimplifiedModel externalRefModel;	
	RNGParseBindingPool pool;
	
    boolean level1DocumentationElement;
    boolean restrictToFileName;
    boolean optimizedForResourceSharing;
	
	protected InternalRNGFactory(boolean level1DocumentationElement, 
	                                boolean restrictToFileName, 
	                                boolean optimizedForResourceSharing) throws DatatypeException{
        this.level1DocumentationElement = level1DocumentationElement;
        this.restrictToFileName = restrictToFileName;
        this.optimizedForResourceSharing = optimizedForResourceSharing;
		
		builder = new SimplifiedComponentBuilder();			
		rngDirector = new RNGDirector(false);

		rngDirector.createModels(builder);		
		
		rngModel = rngDirector.getRNGModel();		
		pool = rngDirector.getBindingModelPool();
	}
    
    public void setLevel1DocumentationElement(boolean level1DocumentationElement){
        this.level1DocumentationElement = level1DocumentationElement;
    }
    
    public void setRestrictToFileName(boolean restrictToFileName){
        this.restrictToFileName = restrictToFileName;    
    }
    
  
	public InternalRNGSchema getInternalRNGSchema(){
		//TODO could be loaded from a previously serialized 
		//or created on the spot
		// TODO see that you add the unexpected and the unknown nodes somehow
		// here or in the builder, whereever you see fit
		// they have to be added to all elements, including empty		
		ValidationModel vm = new ValidationModelImpl(null, rngModel, optimizedForResourceSharing); 
        SchemaModel sm = new SchemaModel(vm, null); 
		InternalRNGSchema schema = new InternalRNGSchema(false,
                                        level1DocumentationElement,                                        
                                        restrictToFileName,
                                        optimizedForResourceSharing,
                                        sm, 
                                        pool);					
		return schema;
	}
	
	public InternalRNGSchema getExternalRefSchema(){	
	    if(externalRefModel == null) externalRefModel = rngDirector.getExternalRefModel();
		ValidationModel vm = new ValidationModelImpl(null, externalRefModel, optimizedForResourceSharing); 
        SchemaModel sm = new SchemaModel(vm, null);
		InternalRNGSchema schema = new InternalRNGSchema(false,
                                        level1DocumentationElement,
                                        restrictToFileName,
                                        optimizedForResourceSharing,
                                        sm,
                                        pool);		
		return schema;
	}
	
	public InternalRNGSchema getIncludeSchema(){	
	    if(includeModel == null) includeModel = rngDirector.getIncludeModel();
		ValidationModel vm = new ValidationModelImpl(null, includeModel, optimizedForResourceSharing); 
        SchemaModel sm = new SchemaModel(vm, null);
		InternalRNGSchema schema = new InternalRNGSchema(false,
                                        level1DocumentationElement,
                                        restrictToFileName,
                                        optimizedForResourceSharing,
                                        sm,
                                        pool);		
		return schema;
	}
	
}