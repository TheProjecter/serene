/*
Copyright 2011 Radu Cernuta 

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

package serene.dtdcompatibility.datatype;

import java.util.HashMap;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.DefaultBuilder;

import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.XSSimpleType;

import serene.datatype.xsd.XsdValidationContext;

public class IdTypeLibrary implements DatatypeLibrary{	
    SchemaDVFactory xercesFactory;
    XsdValidationContext xsdValidationContext;
    HashMap<String, IdTypeDT> datatypes;
    // TODO 
    // map of builders ?
    
	IdTypeLibrary(){
        xercesFactory = SchemaDVFactory.getInstance("org.apache.xerces.impl.dv.xs.FullDVFactory");// Make sure you get the Full factory
        if(xercesFactory == null) xercesFactory = SchemaDVFactory.getInstance();// Make sure you get the some factory
        //xercesFactory = SchemaDVFactory.getInstance();        
        //System.out.println("*****"+xercesFactory);
        
        xsdValidationContext = new XsdValidationContext();
        
        datatypes = new HashMap<String, IdTypeDT>();
	}
    
    public Datatype createDatatype(String typeLocalName) throws DatatypeException{
        IdTypeDT datatype = datatypes.get(typeLocalName);
        if(datatype == null){
            int idType = Datatype.ID_TYPE_NULL;
            if(typeLocalName.equals("ID")){
                idType = Datatype.ID_TYPE_ID;
            }else if(typeLocalName.equals("IDREF")){
                idType = Datatype.ID_TYPE_IDREF;
            }else if(typeLocalName.equals("IDREFS")){
                idType = Datatype.ID_TYPE_IDREFS;
            }else{
                throw new DatatypeException("Type \"" + typeLocalName+"\" is not known in the library.");
            }
            XSSimpleType xercesType = null;
            xercesType = xercesFactory.getBuiltInType(typeLocalName);            
            //if(xercesType == null) throw new DatatypeException("Type \"" + typeLocalName+"\" is not known in the library.");
            
            datatype =  new IdTypeDT(xsdValidationContext, xercesType);
            datatype.setIdType(idType);
            datatypes.put(typeLocalName, datatype);
        }        
        return datatype;
	}
	
	public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException{
        return new IdTypeBuilder(baseTypeLocalName, xercesFactory.getBuiltInType(baseTypeLocalName), xercesFactory, xsdValidationContext);
	}
}
