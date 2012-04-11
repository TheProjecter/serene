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
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.ValidationContext;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.SchemaDVFactory;

import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;

import serene.datatype.xsd.XsdValidationContext;

import sereneWrite.MessageWriter;

import serene.Constants;

public class IdTypeBuilder implements DatatypeBuilder {
    String typeLocalName;
    XSSimpleType baseType;
    XSSimpleType restrictedType;    
    
    XsdValidationContext xsdValidationContext;
    SchemaDVFactory xercesFactory;    
    XSFacets xercesFacets;
    short presentFacets;
    
    XSFacets memoXercesFacets;
    short memoPresentFacets;
    
    XSObjectList dummyAnnotations = null;
    
    String targetNamespace;
    
    
    
    MessageWriter debugWriter;
    
	public IdTypeBuilder(String typeLocalName, XSSimpleType baseType, SchemaDVFactory xercesFactory, XsdValidationContext xsdValidationContext, MessageWriter debugWriter){
        
        this.debugWriter = debugWriter;
        this.typeLocalName = typeLocalName;
        this.xercesFactory = xercesFactory;
        this.xsdValidationContext = xsdValidationContext;
		this.baseType = baseType;
        restrictedType = baseType;
        xercesFacets = new XSFacets();
        presentFacets = XSSimpleTypeDefinition.FACET_NONE;
        
        targetNamespace = "";             
	}
	
	public void addParameter( String name, String value, ValidationContext context )
			throws DatatypeException {       
        if(name.equals(Constants.TARGET_NAMESPACE_NAME)){
            targetNamespace = value;
            return;
        }else if(name.equals("pattern")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.pattern = value;            
            presentFacets |= XSSimpleTypeDefinition.FACET_PATTERN;
        }else if(name.equals("length")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.length = Integer.parseInt(value);
            presentFacets |= XSSimpleTypeDefinition.FACET_LENGTH;
        }else if(name.equals("maxLength")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.maxLength = Integer.parseInt(value);
            presentFacets |= XSSimpleTypeDefinition.FACET_MAXLENGTH;
        }else if(name.equals("minLength")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.minLength = Integer.parseInt(value);
            presentFacets |= XSSimpleTypeDefinition.FACET_MINLENGTH;
        }else if(name.equals("maxExclusive")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.maxExclusive = value;
            presentFacets |= XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE;
        }else if(name.equals("minExclusive")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.minExclusive = value;
            presentFacets |= XSSimpleTypeDefinition.FACET_MINEXCLUSIVE;
        }else if(name.equals("maxInclusive")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.maxInclusive = value;
            presentFacets |= XSSimpleTypeDefinition.FACET_MAXINCLUSIVE;
        }else if(name.equals("minInclusive")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.minInclusive = value;
            presentFacets |= XSSimpleTypeDefinition.FACET_MININCLUSIVE;
        }else if(name.equals("totalDigits")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.totalDigits = Integer.parseInt(value);
            presentFacets |= XSSimpleTypeDefinition.FACET_TOTALDIGITS;
        }else if(name.equals("fractionDigits")){
            memoXercesFacets = xercesFacets;
            memoPresentFacets = presentFacets;
            xercesFacets.fractionDigits = Integer.parseInt(value);
            presentFacets |= XSSimpleTypeDefinition.FACET_FRACTIONDIGITS;
        }else if(name.equals("whiteSpace")){
            throw new DatatypeException("No whitespace facets allowed.");            
        }else if(name.equals("enumeration")){
            throw new DatatypeException("No enumeration facets allowed.");
        }
        
        xsdValidationContext.setRngValidationContext(context);//normally the context is needed only for namespaces and entities
        XSSimpleType typeRestriction = xercesFactory.createTypeRestriction(typeLocalName, targetNamespace, (short)0, baseType, dummyAnnotations);
        try{
            typeRestriction.applyFacets(xercesFacets, presentFacets, XSSimpleTypeDefinition.FACET_NONE, xsdValidationContext);
            restrictedType = typeRestriction;
        }catch(InvalidDatatypeFacetException e){
            xercesFacets = memoXercesFacets;
            presentFacets = memoPresentFacets;
            throw new DatatypeException(e.getMessage());
        }        
	}	
    
    
    public Datatype createDatatype() throws DatatypeException{
        IdTypeDT datatype = new IdTypeDT(xsdValidationContext, restrictedType, debugWriter);        
        return datatype;
    }
}
