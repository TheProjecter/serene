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

package serene.internal.datatype;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.XSSimpleType;

import serene.datatype.xsd.XsdValidationContext;

import serene.datatype.util.StringNormalizer;

import sereneWrite.MessageWriter;
 
class InternalLibrary implements DatatypeLibrary{    
    Datatype qNameDT;
    Datatype ncNameDT;
    Datatype hrefURIDT;
    Datatype datatypeLibraryURIDT;
    Datatype combineDT;    
    
    
	MessageWriter debugWriter;
	
	public InternalLibrary(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
        
        combineDT = new CombineDT();
    
        SchemaDVFactory xercesFactory = SchemaDVFactory.getInstance("org.apache.xerces.impl.dv.xs.FullDVFactory");
        if(xercesFactory == null){
            xercesFactory = SchemaDVFactory.getInstance();
        }
        
        XsdValidationContext xsdValidationContext = new XsdValidationContext(debugWriter);
        
        XSSimpleType anyURIXerces = xercesFactory.getBuiltInType("anyURI");
        
        hrefURIDT = new HrefURIDT(xsdValidationContext, anyURIXerces, debugWriter);
        datatypeLibraryURIDT = new DatatypeLibraryURIDT(xsdValidationContext, anyURIXerces, debugWriter);
        
        qNameDT = new QNameDT(xsdValidationContext, xercesFactory.getBuiltInType("QName"), debugWriter);
        ncNameDT = new NCNameDT(xsdValidationContext, xercesFactory.getBuiltInType("NCName"), debugWriter);
	}
	
	public Datatype createDatatype(String typeLocalName) throws DatatypeException{
		if(typeLocalName.equals("QName")){
			return qNameDT;
		}else if(typeLocalName.equals("NCName")){
			return ncNameDT;
		}else if(typeLocalName.equals("hrefURI")){
			return hrefURIDT;
		}else if(typeLocalName.equals("datatypeLibraryURI")){
			return datatypeLibraryURIDT;
		}else if(typeLocalName.equals("combine")){
			return combineDT;
		}else throw new DatatypeException("Unsupported type: " + typeLocalName);
	}
	
	public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException{
		throw new DatatypeException("Not needed.");
	}
}