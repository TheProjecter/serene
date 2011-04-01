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

import java.util.Arrays;


import serene.validation.schema.active.Identifier;

import serene.util.AttributeInfo;

import sereneWrite.MessageWriter;

public class AttributeIdTypeModel{    
    AttributeInfo[] idTypeAttributes;
    
    MessageWriter debugWriter;
    
    public AttributeIdTypeModel(MessageWriter debugWriter){
        this.debugWriter = debugWriter;
    }
    
    
    public void addAttributeInfo(AttributeInfo attrInfo){        
        if(idTypeAttributes == null){
            idTypeAttributes = new AttributeInfo[1];
            idTypeAttributes[0] = attrInfo;
        }else{
            int index = idTypeAttributes.length;
                        
            AttributeInfo[] increasedA = new AttributeInfo[index+1];
            System.arraycopy(idTypeAttributes, 0, increasedA, 0, index);
            idTypeAttributes = increasedA;
            idTypeAttributes[index] = attrInfo;            
		}
    }
    
    public AttributeInfo getAttributeInfo(String attributeNamespaceURI, String attributeName){
        if(idTypeAttributes == null) return null;
        for(int i= 0; i < idTypeAttributes.length; i++){            
            if(idTypeAttributes[i].getNamespaceURI().equals(attributeNamespaceURI) && idTypeAttributes[i].getLocalName().equals(attributeName)){
                return idTypeAttributes[i];
            }
        }
        return null;
    }   
    
    public String toString(){
        String s = "AttributeIdTypeModel";
        if(idTypeAttributes == null) return s;
        s += ": ";           
        for(int i= 0; i < idTypeAttributes.length; i++){
            s += "\n"+idTypeAttributes[i].toString();
        }
        return s;
    }
}
