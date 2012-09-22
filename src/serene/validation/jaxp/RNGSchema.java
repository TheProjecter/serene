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

package serene.validation.jaxp;

import java.util.List;
import java.util.ArrayList;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;


import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;

import serene.BaseSchema;
import serene.SchemaModel;

public class RNGSchema extends BaseSchema{		
	boolean namespacePrefixes;
    boolean level1AttributeDefaultValue;
    boolean level2AttributeDefaultValue;
	boolean level1AttributeIdType;
    boolean level2AttributeIdType;
    boolean restrictToFileName;
    
    List<Templates> schemaTemplates;
    
    SAXTransformerFactory saxTransformerFactory;
    List<TransformerHandler> validatingTransformerHandlers;
    
	public RNGSchema(boolean secureProcessing,
                    boolean namespacePrefixes,
                    boolean level1AttributeDefaultValue,
                    boolean level2AttributeDefaultValue,
                    boolean level1AttributeIdType,
                    boolean level2AttributeIdType,
                    boolean restrictToFileName,
                    boolean optimizedForResourceSharing,
                    SchemaModel schemaModel){
		super(secureProcessing, optimizedForResourceSharing, schemaModel);
        this.namespacePrefixes = namespacePrefixes;
        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
        this.level2AttributeDefaultValue = level2AttributeDefaultValue;
        this.level1AttributeIdType = level1AttributeIdType;
        this.level2AttributeIdType = level2AttributeIdType;
        this.restrictToFileName = restrictToFileName;
	}
	
	public RNGSchema(boolean secureProcessing,
                    boolean namespacePrefixes,
                    boolean level1AttributeDefaultValue,
                    boolean level2AttributeDefaultValue,
                    boolean level1AttributeIdType,
                    boolean level2AttributeIdType,
                    boolean restrictToFileName,
                    boolean optimizedForResourceSharing,                    
                    SchemaModel schemaModel,
                    List<Templates> schemaTemplates) throws SAXException{
		super(secureProcessing, optimizedForResourceSharing, schemaModel);
        this.namespacePrefixes = namespacePrefixes;
        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
        this.level2AttributeDefaultValue = level2AttributeDefaultValue;
        this.level1AttributeIdType = level1AttributeIdType;
        this.level2AttributeIdType = level2AttributeIdType;
        this.restrictToFileName = restrictToFileName;
        
        this.schemaTemplates = schemaTemplates;
        
        TransformerFactory tf = TransformerFactory.newInstance();
        if(tf.getFeature(SAXTransformerFactory.FEATURE)){
            saxTransformerFactory = (SAXTransformerFactory)tf;
        }else{
            throw new SAXException("Could not load validating transformer.");
        }
        
        validatingTransformerHandlers = new ArrayList<TransformerHandler>(schemaTemplates.size());
        
        for(int i = 0; i < schemaTemplates.size(); i++){
            try{
                validatingTransformerHandlers.add(saxTransformerFactory.newTransformerHandler(schemaTemplates.get(i)));
            }catch(TransformerConfigurationException tce){
                throw new SAXException(tce);
            }
        }        
	}
	
	
	public Validator newValidator(){
		return new ValidatorImpl(secureProcessing,                            
                                    namespacePrefixes,
                                    level1AttributeDefaultValue,
                                    level2AttributeDefaultValue,
                                    level1AttributeIdType,
                                    level2AttributeIdType,
                                    restrictToFileName,
                                    newValidatorHandler());
	}
	
	public ValidatorHandler newValidatorHandler(){	
	    if(schemaTemplates == null){
	        return new RNGValidatorHandlerImpl(secureProcessing,
                                        namespacePrefixes,
                                        level1AttributeDefaultValue,
                                        level2AttributeDefaultValue,
                                        level1AttributeIdType,
                                        level2AttributeIdType,
                                        restrictToFileName,
                                        optimizedForResourceSharing,
                                        contentHandlerPool.getValidatorEventHandlerPool(),
                                        conflictHandlerPool.getValidatorConflictHandlerPool(),
                                        stackHandlerPool.getValidatorStackHandlerPool(),
										errorHandlerPool.getValidatorErrorHandlerPool(),
										schemaModel);
		}
		
		// This method may not throw any exceptions and must always return non-null
	    // valid objects, so the first validating tranformer handler must be 
	    // created in the constructor where the TransformerConfigurationException
	    // can be caught, wrapped in a SAXException and thrown, as a test, here 
	    // the errors will just be discarded.
	    
	    
	    
	    if(validatingTransformerHandlers == null){
            validatingTransformerHandlers = new ArrayList<TransformerHandler>(schemaTemplates.size());
            for(int i = 0; i < validatingTransformerHandlers.size(); i++){
                try{
                    validatingTransformerHandlers.add(saxTransformerFactory.newTransformerHandler(schemaTemplates.get(i)));
                }catch(TransformerConfigurationException tce){
                    // never happens
                }
            }
        }
        
		ValidatorHandler vh = new RNGSchematronValidatorHandlerImpl(secureProcessing,
                                        namespacePrefixes,
                                        level1AttributeDefaultValue,
                                        level2AttributeDefaultValue,
                                        level1AttributeIdType,
                                        level2AttributeIdType,
                                        restrictToFileName,
                                        optimizedForResourceSharing,
                                        contentHandlerPool.getValidatorEventHandlerPool(),
                                        conflictHandlerPool.getValidatorConflictHandlerPool(),
                                        stackHandlerPool.getValidatorStackHandlerPool(),
										errorHandlerPool.getValidatorErrorHandlerPool(),
										schemaModel,
										validatingTransformerHandlers);
        validatingTransformerHandlers = null;
        
		return vh;
		
	}
}