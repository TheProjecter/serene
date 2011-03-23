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

import serene.parser.RNGParseBindingPool;
import serene.parser.StartLevelPool;
import serene.parser.DummyPool;

import sereneWrite.MessageWriter;

/**
* The InternalRNGFactory class is not thread-safe and not re-entrant. One instance *
* is created for every InternalRNGSchemaFactory.  
*/
public class InternalRNGFactory{	
	private static volatile InternalRNGFactory instance; 
	
	SimplifiedComponentBuilder builder;	
	RNGDirector rngDirector;

	SimplifiedModel rngModel;
	SimplifiedModel includeModel;
	SimplifiedModel externalRefModel;	
	RNGParseBindingPool pool;
	
	StartLevelPool startPool;
	DummyPool dummyPool;
	
	MessageWriter debugWriter;
	
	private InternalRNGFactory(MessageWriter debugWriter) throws DatatypeException{		
		super();		
		this.debugWriter = debugWriter;		
		builder = new SimplifiedComponentBuilder(debugWriter);
		
		rngDirector = new RNGDirector(debugWriter);

		rngDirector.createModels(builder);		
		
		rngModel = rngDirector.getRNGModel();
		includeModel = rngDirector.getIncludeModel();
		externalRefModel = rngDirector.getExternalRefModel();
		pool = rngDirector.getBindingModelPool();
		
		dummyPool = new DummyPool(debugWriter);
		startPool = new StartLevelPool(debugWriter);
	}
    
	public static InternalRNGFactory getInstance(MessageWriter debugWriter)  throws DatatypeException{
		if(instance == null){
			synchronized(InternalRNGFactory.class){
				if(instance == null){
					instance = new InternalRNGFactory(debugWriter); 
				}
			}
		}
		return instance;
	}
	
	public InternalRNGSchema getInternalRNGSchema(){
		//TODO could be loaded from a previously serialized 
		//or created on the spot
		// TODO see that you add the unexpected and the unknown nodes somehow
		// here or in the builder, whereever you see fit
		// they have to be added to all elements, including empty		
		ValidationModel vm = new ValidationModelImpl(null, rngModel, debugWriter); 
        SchemaModel sm = new SchemaModel(vm, null, debugWriter); 
		InternalRNGSchema schema = new InternalRNGSchema(false,
                                        sm,
										debugWriter);					
		return schema;
	}
	
	public InternalRNGSchema getExternalRefSchema(){		
		ValidationModel vm = new ValidationModelImpl(null, externalRefModel, debugWriter); 
        SchemaModel sm = new SchemaModel(vm, null, debugWriter);
		InternalRNGSchema schema = new InternalRNGSchema(false,
                                        sm,
										debugWriter);		
		return schema;
	}
	
	public InternalRNGSchema getIncludeSchema(){		
		ValidationModel vm = new ValidationModelImpl(null, includeModel, debugWriter); 
        SchemaModel sm = new SchemaModel(vm, null, debugWriter);
		InternalRNGSchema schema = new InternalRNGSchema(false,
                                        sm,
										debugWriter);		
		return schema;
	}
	public RNGParseBindingPool getRNGParseBindingPool(){		
		return pool;
	}
	
	public StartLevelPool getStartDummyPool(){
		return startPool;
	}
	
	public DummyPool getEndDummyPool(){
		return dummyPool;
	}
}