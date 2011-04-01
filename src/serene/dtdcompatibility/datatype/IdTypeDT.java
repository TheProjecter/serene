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


import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeStreamingValidator;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;

import serene.datatype.xsd.XsdValidationContext;

import sereneWrite.MessageWriter;

class IdTypeDT implements Datatype{
    boolean isContextDependent;
    boolean needsExtraChecking;
    boolean needsFacetChecking;
    boolean needsToNormalize;
    
    int idType = ID_TYPE_NULL;
    XsdValidationContext xsdValidationContext;
    
    XSSimpleType xercesType;
    
    IdTypeDT(XsdValidationContext xsdValidationContext,
                XSSimpleType xercesType, 
                MessageWriter debugWriter){
        this.xercesType = xercesType;
        this.xsdValidationContext = xsdValidationContext;
        
        needsExtraChecking = false;
        needsFacetChecking = true;
        needsToNormalize = true;  
    }
    
    void setIdType(int idType){
        this.idType = idType;
    }
    
    void setContextDependent(boolean value){
        isContextDependent = value;
    }
    
    void setNeedsExtraChecking(boolean value){
        needsExtraChecking = value;
    }
    
    void setNeedsFacetChecking(boolean value){
        needsFacetChecking = value;
    }
    
    void setNeedsToNormalize(boolean value){
        needsToNormalize = value;
    }
    
    public boolean isValid(String str, ValidationContext vc) {
        try{
            checkValid(str, vc);
            return true;
        }catch(DatatypeException de){
            return false;    
        }
	}

	public void checkValid(String str, ValidationContext vc) throws DatatypeException {
		try{
            xsdValidationContext.setNeedsExtraChecking(needsExtraChecking);
            xsdValidationContext.setNeedsFacetChecking(needsFacetChecking);
            xsdValidationContext.setNeedsToNormalize(needsToNormalize);
            xsdValidationContext.setRngValidationContext(vc);
            xercesType.validate(str, xsdValidationContext, null);
        }catch(InvalidDatatypeValueException xercesException){
            throw new DatatypeException(xercesException.getMessage());
        }
	}

	public Object createValue(String str, ValidationContext vc) {
		try{
            xsdValidationContext.setNeedsExtraChecking(needsExtraChecking);
            xsdValidationContext.setNeedsFacetChecking(needsFacetChecking);
            xsdValidationContext.setNeedsToNormalize(needsToNormalize);
            xsdValidationContext.setRngValidationContext(vc);
            return xercesType.validate(str, xsdValidationContext, null);
        }catch(InvalidDatatypeValueException xercesException){
            //throw new DatatypeException(xercesException.getMessage());
            return null;
        }
	}

	public boolean isContextDependent() {
		return isContextDependent;
	}

	public boolean alwaysValid() {
		throw new IllegalStateException();
	}

	public int getIdType() {
		return idType;
	}

	public boolean sameValue(Object obj1, Object obj2) {
		return xercesType.isEqual(obj1, obj2);
	}

	public int valueHashCode(Object obj) {
		return obj.hashCode();
	}

	public DatatypeStreamingValidator createStreamingValidator(ValidationContext vc) {
		throw new UnsupportedOperationException("Serene doesn't support streaming validation yet");
	}
 
    public String toString(){
        return "xsd DT "+xercesType.toString();
    }
}
