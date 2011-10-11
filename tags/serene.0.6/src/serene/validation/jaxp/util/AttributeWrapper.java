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

package serene.validation.jaxp.util;

import java.io.Writer;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import javax.xml.namespace.QName;

import sereneWrite.MessageWriter;

public class AttributeWrapper implements Attribute{
    Attribute attribute;
    String idType; 
    boolean isSpecified;
    boolean wrapsSpecified;
    
    MessageWriter debugWriter;
    
    public AttributeWrapper(Attribute attribute, String IdType, MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.attribute = attribute;
        this.idType = idType;
        wrapsSpecified = false;
    }
        
    public AttributeWrapper(Attribute attribute, String IdType, boolean isSpecified, MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.attribute = attribute;
        this.idType = idType;
        this.isSpecified = isSpecified;
        wrapsSpecified = true;
    }
    
    
    public Characters asCharacters(){
        return attribute.asCharacters();
    }
    public EndElement asEndElement(){
        return attribute.asEndElement();
    }
    public StartElement asStartElement(){
        return attribute.asStartElement();
    }
    public int getEventType(){
        return attribute.getEventType();
    }
    public Location getLocation(){
        return attribute.getLocation();
    }
    public QName getSchemaType(){
        return attribute.getSchemaType();
    }
    public boolean isAttribute(){
        return attribute.isAttribute();
    }
    public boolean isCharacters(){
        return attribute.isCharacters();
    }
    public boolean isEndDocument(){
        return attribute.isEndDocument();
    }
    public boolean isEndElement(){
        return attribute.isEndElement();
    }
    public boolean isEntityReference(){
        return attribute.isEntityReference();
    }
    public boolean isNamespace(){
        return attribute.isNamespace();
    }
    public boolean isProcessingInstruction(){
        return attribute.isProcessingInstruction();
    }
    public boolean isStartDocument(){
        return attribute.isStartDocument();
    }
    public boolean isStartElement(){
        return attribute.isStartElement();
    }
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException{
        attribute.writeAsEncodedUnicode(writer);
    } 
    public String getDTDType(){
        if(idType != null) return idType;
        return attribute.getDTDType();
    }
    public QName getName(){
        return attribute.getName();
    }
    public String getValue(){
        return attribute.getValue();
    }
    public boolean isSpecified(){
        if(wrapsSpecified) return isSpecified;
        return attribute.isSpecified();
    } 
}
