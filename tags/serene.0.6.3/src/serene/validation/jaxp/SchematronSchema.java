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

package serene.validation.jaxp;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.xml.sax.SAXException;

import serene.BaseSchema;

public class SchematronSchema extends BaseSchema{		
	boolean namespacePrefixes;
    boolean restrictToFileName;
    
    Templates schemaTemplates;
    
    SAXTransformerFactory saxTransformerFactory;
    /*TransformerHandler validatingTransformerHandler;*/
    
	public SchematronSchema(boolean secureProcessing,
                    boolean namespacePrefixes,
                    boolean restrictToFileName,
                    boolean optimizedForResourceSharing,
                    Templates schemaTemplates)throws SAXException{
		super(secureProcessing, optimizedForResourceSharing, null);
		this.schemaTemplates = schemaTemplates;
        this.namespacePrefixes = namespacePrefixes;
        this.restrictToFileName = restrictToFileName;
        
        TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
        if(tf.getFeature(SAXTransformerFactory.FEATURE)){
            saxTransformerFactory = (SAXTransformerFactory)tf;
        }else{
            throw new SAXException("Could not load validating transformer.");
        }
        
        /*try{
            validatingTransformerHandler = saxTransformerFactory.newTransformerHandler(schemaTemplates);
        }catch(TransformerConfigurationException tce){
            throw new SAXException(tce);
        }*/
	}
	
	
	public Validator newValidator(){
		return new ValidatorImpl(secureProcessing,                            
                                    namespacePrefixes,
                                    false,
                                    false,
                                    false,
                                    false,
                                    restrictToFileName,
                                    newValidatorHandler());       
	}
	
	public ValidatorHandler newValidatorHandler(){
	    // This method may not throw any exceptions and must always return non-null
	    // valid objects, so the first validating tranformer handler must be 
	    // created in the constructor where the TransformerConfigurationException
	    // can be caught, wrapped in a SAXException and thrown, as a test, here 
	    // the errors will just be discarded.
	    
	    /*if(validatingTransformerHandler == null){
            try{
                validatingTransformerHandler = saxTransformerFactory.newTransformerHandler(schemaTemplates);
            }catch(TransformerConfigurationException tce){ }
        }*/
		ValidatorHandler vh = new SchematronValidatorHandlerImpl(secureProcessing,
                                        namespacePrefixes,
                                        restrictToFileName,
                                        optimizedForResourceSharing,
                                        schemaTemplates,    
                                        saxTransformerFactory,
                                        new SVRLParser());
        //validatingTransformerHandler = null;
        return vh;        
	}
}
