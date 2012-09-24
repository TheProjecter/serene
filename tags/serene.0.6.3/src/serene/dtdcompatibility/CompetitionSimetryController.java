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

package serene.dtdcompatibility;

import java.util.ArrayList;

import org.xml.sax.SAXException;

import serene.restrictor.OverlapController;
import serene.restrictor.ControllerPool;

import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SNameClass;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.util.IntList;

class CompetitionSimetryController{
    ArrayList<ElementRecord> records;
    OverlapController overlapController;
    
    ErrorDispatcher errorDispatcher;
    
    boolean restrictToFileName;
    
    CompetitionSimetryController(ControllerPool controllerPool, ErrorDispatcher errorDispatcher){
        this.errorDispatcher = errorDispatcher;
        records = new ArrayList<ElementRecord>();
        overlapController = new OverlapController(controllerPool);
    }
    
    public void setRestrictToFileName(boolean value){
        restrictToFileName = value;
    }
    
    void clear(){
        records.clear();
    }
    void control(SElement element, ArrayList<SAttribute> attributes) throws SAXException{
        SNameClass nameClass = element.getNameClass();        
        for(ElementRecord record : records){
            SNameClass recordNameClass = record.element.getNameClass();
            if(overlapController.overlap(nameClass, recordNameClass)){
                for(SAttribute attribute : attributes){
                    String defaultValue = attribute.getDefaultValue();
                    boolean foundCorrespondent = false;
                    SNameClass attributeNC = attribute.getNameClass();
                    for(SAttribute recordAttribute : record.attributes){
                        SNameClass recordAttributeNC = recordAttribute.getNameClass();
                        if(attributeNC.equals(recordAttributeNC)){
                            String recordDefaultValue = recordAttribute.getDefaultValue();
                            foundCorrespondent = true;
                            if(defaultValue == null){                                
                                if(recordDefaultValue == null){                                    
                                    break; // attribute handling done, move to next
                                }else{
                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
                                    +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" without default value;"
                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+" and default value \""+recordDefaultValue+"\".";
                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                                    break; // attribute handling done, move to next
                                }
                            }else{
                                if(recordDefaultValue != null && recordDefaultValue.equals(defaultValue)){
                                    break; // attribute handling done, move to next 
                                }else if(recordDefaultValue == null){
                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
                                    +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" and default value \""+defaultValue+"\";"
                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+" without default value .";
                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                                    break; // attribute handling done, move to next
                                }else{
                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
                                    +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" and default value \""+defaultValue+"\";"
                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+" and default value \""+recordDefaultValue+"\".";
                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                                    break; // attribute handling done, move to next                                    
                                }
                            }
                        }
                    }
                    if(!foundCorrespondent  && defaultValue != null){
                        String message = "DTD compatibility error. Competing element definitions without corresponding attribute definitions with default values:"
                                +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)
                                +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" without corresponding attribute definition.";
                        errorDispatcher.error(new AttributeDefaultValueException(message, null));
                    }                    
                }
                for(SAttribute recordAttribute : record.attributes){
                    String defaultValue = recordAttribute.getDefaultValue();                                        
                    if(defaultValue != null){
                        boolean foundCorrespondent = false;
                        SNameClass recordAttributeNC = recordAttribute.getNameClass();
                        for(SAttribute attribute : attributes){
                            SNameClass attributeNC = attribute.getNameClass();
                            if(attributeNC.equals(recordAttributeNC)){
                                foundCorrespondent = true;// already handled previously
                            }
                        }
                        if(!foundCorrespondent && defaultValue != null){
                            String message = "DTD compatibility error. Competing element definitions without corresponding attribute definitions with default values:"
                                    +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" without corresponding attribute definition."
                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName);
                            errorDispatcher.error(new AttributeDefaultValueException(message, null));
                        }
                    }
                }
            }
        }
        ElementRecord er = new ElementRecord(element, attributes, null);
        records.add(er);        
    }
    
    
    
    void control(SElement element, ArrayList<SAttribute> attributes, IntList attributeIdTypes) throws SAXException{
        SNameClass nameClass = element.getNameClass();        
        for(ElementRecord record : records){
            SNameClass recordNameClass = record.element.getNameClass();
            if(overlapController.overlap(nameClass, recordNameClass)){
                //Handle both default value and ID-type
                for(int i = 0; i < attributes.size(); i++){
                    SAttribute attribute = attributes.get(i);
                    String defaultValue = attribute.getDefaultValue();
                    boolean foundCorrespondent = false;
                    SNameClass attributeNC = attribute.getNameClass();
                    for(int j = 0; j < record.attributes.size(); j++){
                        SAttribute recordAttribute = record.attributes.get(j);
                        SNameClass recordAttributeNC = recordAttribute.getNameClass();
                        if(attributeNC.equals(recordAttributeNC)){
                            String recordDefaultValue = recordAttribute.getDefaultValue();
                            foundCorrespondent = true;
                            if(defaultValue == null){                                
                                if(recordDefaultValue == null){  
                                }else{
                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
                                    +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" without default value;"
                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+" and default value \""+recordDefaultValue+"\".";
                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                                }
                            }else{
                                if(recordDefaultValue != null && recordDefaultValue.equals(defaultValue)){
                                }else if(recordDefaultValue == null){
                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
                                    +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" and default value \""+defaultValue+"\";"
                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+" without default value .";
                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                                }else{
                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
                                    +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" and default value \""+defaultValue+"\";"
                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+" and default value \""+recordDefaultValue+"\".";
                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));          
                                }
                            }
                            
                            int attributeIdType = attributeIdTypes.get(i);
                            int recordAttributeIdType = record.attributeIdTypes.get(j);
                            if(attributeIdType != recordAttributeIdType){
                                String message = "DTD compatibility error. Competing attribute definitions specify attribute values with different ID-types:"
                                    +"\n<"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+";"
                                    +"\n<"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+".";
                                    errorDispatcher.error(new AttributeIdTypeException(message, null));
                                    break; // attribute handling done, move to next
                            }
                        }else if(overlapController.overlap(attributeNC, recordAttributeNC)){
                            int attributeIdType = attributeIdTypes.get(i);
                            int recordAttributeIdType = record.attributeIdTypes.get(j);
                            if(attributeIdType != recordAttributeIdType){
                                String message = "DTD compatibility error. Competing attribute definitions specify attribute values with different ID-types:"
                                    +"\n<"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+";"
                                    +"\n<"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+".";
                                    errorDispatcher.error(new AttributeIdTypeException(message, null));
                            }
                        }
                    }
                    if(!foundCorrespondent && defaultValue != null){
                        String message = "DTD compatibility error. Competing element definitions without corresponding attribute definitions with default values:"
                                +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)
                                +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" without corresponding attribute definition.";
                        errorDispatcher.error(new AttributeDefaultValueException(message, null));
                    }                    
                }
                for(SAttribute recordAttribute : record.attributes){
                    String defaultValue = recordAttribute.getDefaultValue();                                        
                    if(defaultValue != null){
                        boolean foundCorrespondent = false;
                        SNameClass recordAttributeNC = recordAttribute.getNameClass();
                        for(SAttribute attribute : attributes){
                            SNameClass attributeNC = attribute.getNameClass();
                            if(attributeNC.equals(recordAttributeNC)){
                                foundCorrespondent = true;// already handled previously
                            }
                        }
                        if(!foundCorrespondent  && defaultValue != null){
                            String message = "DTD compatibility error. Competing element definitions without corresponding attribute definitions with default values:"
                                    +"\n<"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" without corresponding attribute definition."
                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation(restrictToFileName)+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName);
                            errorDispatcher.error(new AttributeDefaultValueException(message, null));
                        }
                    }
                }
            }else{                
                //Handle only the idType.
                for(int i = 0; i < attributes.size(); i++){
                    SAttribute attribute = attributes.get(i);
                    SNameClass attributeNC = attribute.getNameClass();
                    for(int j = 0; j < record.attributes.size(); j++){
                        SAttribute recordAttribute = record.attributes.get(j);
                        SNameClass recordAttributeNC = recordAttribute.getNameClass();
                        if(overlapController.overlap(attributeNC, recordAttributeNC)){
                            int attributeIdType = attributeIdTypes.get(i);
                            int recordAttributeIdType = record.attributeIdTypes.get(j);
                            if(attributeIdType != recordAttributeIdType){
                                String message = "DTD compatibility error. Competing attribute definitions specify attribute values with different ID-types:"
                                    +"\n<"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+";"
                                    +"\n<"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+".";
                                    errorDispatcher.error(new AttributeIdTypeException(message, null));
                                    break; // attribute handling done, move to next
                            }
                        }
                    }                 
                }
            }
        }
        ElementRecord er = new ElementRecord(element, attributes, attributeIdTypes);
        records.add(er);        
    }
    
    void control(ArrayList<SAttribute> attributes, IntList attributeIdTypes) throws SAXException{
        //there is only one dummy record
        ElementRecord record = records.get(0);
        //Handle only the idType.
        for(int i = 0; i < attributes.size(); i++){
            SAttribute attribute = attributes.get(i);
            SNameClass attributeNC = attribute.getNameClass();
            for(int j = 0; j < record.attributes.size(); j++){
                SAttribute recordAttribute = record.attributes.get(j);
                SNameClass recordAttributeNC = recordAttribute.getNameClass();                            
                if(overlapController.overlap(attributeNC, recordAttributeNC)){
                    int attributeIdType = attributeIdTypes.get(i);
                    int recordAttributeIdType = record.attributeIdTypes.get(j);
                    if(attributeIdType != recordAttributeIdType){
                        String message = "DTD compatibility error. Competing attribute definitions specify attribute values with different ID-types:"
                            +"\n<"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+";"
                            +"\n<"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation(restrictToFileName)+".";
                            errorDispatcher.error(new AttributeIdTypeException(message, null));
                            break; // attribute handling done, move to next
                    }
                }
            }                 
        }
        record.add(attributes, attributeIdTypes);
    }
    
    class ElementRecord{
        SElement element;
        ArrayList<SAttribute> attributes;
        IntList attributeIdTypes;
        
        ElementRecord(SElement element, ArrayList<SAttribute> attributes, IntList attributeIdTypes){
            this.element = element;
            this.attributes = attributes;
            this.attributeIdTypes = attributeIdTypes;
        }

        void add(ArrayList<SAttribute> attributes, IntList attributeIdTypes){
            this.attributes.addAll(attributes);
            for(int i = 0; i < attributeIdTypes.size(); i++){
                this.attributeIdTypes.add(attributeIdTypes.get(i));
            }
        }
        public String toString(){
            return element.toString()+" "+attributes.toString();
        }        
    }
}
