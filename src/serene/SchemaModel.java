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


package serene;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.active.ActiveModel;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.dtdcompatibility.DTDCompatibilityModel;
import serene.dtdcompatibility.AttributeDefaultValueModel;
import serene.dtdcompatibility.AttributeIdTypeModel;

import serene.validation.handlers.stack.impl.ValidatorStackHandlerPool;

import serene.validation.schema.ValidationModel;

public class SchemaModel implements ValidationModel, DTDCompatibilityModel{
    ValidationModel validationModel;    
    DTDCompatibilityModel dtdCompatibilityModel;
    public SchemaModel(ValidationModel validationModel,
            DTDCompatibilityModel dtdCompatibilityModel){
        this.validationModel = validationModel;
        this.dtdCompatibilityModel = dtdCompatibilityModel;        
    }
    
    
    public ParsedModel getParsedModel(){
        if(validationModel == null) return null;
        return validationModel.getParsedModel();
    }
    
    public SimplifiedModel getSimplifiedModel(){
        if(validationModel == null) return null;
        return validationModel.getSimplifiedModel();
    }
    
    public ActiveModel getActiveModel(ValidatorStackHandlerPool stackHandlerPool, ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor, ErrorDispatcher errorDispatcher){
        if(validationModel == null) return null;
        return validationModel.getActiveModel(stackHandlerPool, activeInputDescriptor, inputStackDescriptor, errorDispatcher);
    }    
    
    public AttributeDefaultValueModel getAttributeDefaultValueModel(){
        if(dtdCompatibilityModel == null) return null;
        return dtdCompatibilityModel.getAttributeDefaultValueModel();
    }
    
    public AttributeIdTypeModel getAttributeIdTypeModel(){
        if(dtdCompatibilityModel == null) return null;
        return dtdCompatibilityModel.getAttributeIdTypeModel();
    }
}
