/*
Copyright 2012 Radu Cernuta 

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

import serene.validation.schema.simplified.SimplifiedModel;


/**
* The InternalRNGFactory class is not thread-safe and not re-entrant. One instance *
* is created for every InternalRNGSchemaFactory.  
*/
public class UnsynchronizedInternalRNGFactory extends InternalRNGFactory{	
	private UnsynchronizedInternalRNGFactory(boolean level1DocumentationElement, 
	                                            boolean restrictToFileName, 
	                                            boolean optimizedForResourceSharing,
	                                            boolean processEmbeddedSchematron) throws DatatypeException{		
		super(level1DocumentationElement, restrictToFileName, optimizedForResourceSharing, processEmbeddedSchematron);	
		
		
	}
    
	public static InternalRNGFactory getInstance(boolean level1DocumentationElement, 
	                                                boolean restrictToFileName, 
	                                                boolean optimizedForResourceSharing,
	                                                boolean processEmbeddedSchematron)  throws DatatypeException{
	    if(optimizedForResourceSharing) throw new IllegalStateException();
		return new UnsynchronizedInternalRNGFactory(level1DocumentationElement, restrictToFileName, optimizedForResourceSharing, processEmbeddedSchematron);
	}	
}
