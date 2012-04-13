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

package serene.datatype.xsd;

import java.util.HashMap;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.DefaultBuilder;

import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.XSSimpleType;

//import org.apache.xerces.impl.dv.xs.FullDVFactory;

public class XsdLibrary implements DatatypeLibrary{	
    SchemaDVFactory xercesFactory;
    XsdValidationContext xsdValidationContext;
    HashMap<String, XsdDatatype> datatypes;
    
	XsdLibrary(){
        xercesFactory = SchemaDVFactory.getInstance("org.apache.xerces.impl.dv.xs.FullDVFactory");
        if(xercesFactory == null){
            xercesFactory = SchemaDVFactory.getInstance();
        }
        
        xsdValidationContext = new XsdValidationContext();
        
        datatypes = new HashMap<String, XsdDatatype>();
	}
    
    public Datatype createDatatype(String typeLocalName) throws DatatypeException{
        XsdDatatype datatype = datatypes.get(typeLocalName);
        if(datatype == null){
            XSSimpleType xercesType = null;
            xercesType = xercesFactory.getBuiltInType(typeLocalName);            
            if(xercesType == null) throw new DatatypeException("Type \"" + typeLocalName+"\" is not known in the library.");
            
            datatype =  new XsdDatatype(xsdValidationContext, xercesType);
            
            if(typeLocalName.equals("string")) datatype.setNeedsToNormalize(false);
            if(/*typeLocalName.equals("ID") 
                || typeLocalName.equals("IDREF")
                ||*/ typeLocalName.equals("ENTITY")
                    || typeLocalName.equals("ENTITIES")) datatype.setNeedsExtraChecking(true);
            if(typeLocalName.equals("QName")
                || typeLocalName.equals("NOTATION"))datatype.setContextDependent(true);
            datatypes.put(typeLocalName, datatype);
        }        
        return datatype;
	}
	
	public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException{
        return new XsdBuilder(baseTypeLocalName, xercesFactory.getBuiltInType(baseTypeLocalName), xercesFactory, xsdValidationContext);
	}
}
