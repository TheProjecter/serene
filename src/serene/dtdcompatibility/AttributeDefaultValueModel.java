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
import java.util.HashSet;

import serene.validation.schema.simplified.Identifier;
import serene.validation.schema.simplified.SNameClass;

import serene.util.AttributeInfo;

public class AttributeDefaultValueModel{
    
    Identifier[] elementIdentifiers;
    AttributeInfo[][] defaultedAttributes;
    
    HashSet<SNameClass> elementNames;
    
    public AttributeDefaultValueModel(){
        elementNames = new HashSet<SNameClass>();
    }
    
    void wrapUp(){
        elementNames.clear();
    }
    
    void addAttributeInfo(SNameClass nameClass, Identifier elementId, AttributeInfo[] attrInfo){
        if(!elementNames.add(nameClass))return;//records have already been made for this name        
        if(elementIdentifiers == null){
            elementIdentifiers = new Identifier[1];
            elementIdentifiers[0] = elementId;
            
            defaultedAttributes = new AttributeInfo[1][];
            defaultedAttributes[0] = attrInfo;
        }else{
            int index = elementIdentifiers.length;
                        
            Identifier[] increasedE = new Identifier[index+1];
            System.arraycopy(elementIdentifiers, 0, increasedE, 0, index);
            elementIdentifiers = increasedE;
            elementIdentifiers[index] = elementId;
            
            AttributeInfo[][] increasedA = new AttributeInfo[index+1][];
            System.arraycopy(defaultedAttributes, 0, increasedA, 0, index);
            defaultedAttributes = increasedA;
            defaultedAttributes[index] = attrInfo;
		}
    }
    
    public AttributeInfo[] getAttributeInfo(String elementNamespaceURI, String elementName){
        if(elementIdentifiers == null) return null;
        for(int i= 0; i < elementIdentifiers.length; i++){
            if(elementIdentifiers[i].matches(elementNamespaceURI, elementName)){
                return defaultedAttributes[i];
            }
        }
        return null;
    }   
    
    public String toString(){
        String s = "AttributeDefaultValueModel";
        if(elementIdentifiers == null) return s;
        s += ": ";           
        for(int i= 0; i < elementIdentifiers.length; i++){
            s += "\n"+elementIdentifiers[i].toString()+" "+Arrays.toString(defaultedAttributes[i]);
        }
        return s;
    }
}
