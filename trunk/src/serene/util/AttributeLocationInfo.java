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

package serene.util;

import sereneWrite.MessageWriter;

public class AttributeLocationInfo extends AttributeInfo{
    String publicId;
    String systemId;
    int lineNumber;
    int columnNumber;
    MessageWriter debugWriter;
    
    public AttributeLocationInfo(String namespaceURI,
                        String localName,
                        String qName,
                        String value,
                        int idType,
                        String publicId,
                        String systemId,
                        int lineNumber,
                        int columnNumber,
                        MessageWriter debugWriter){
        super(namespaceURI, localName, qName, value, idType, debugWriter);        
        this.publicId = publicId;
        this.systemId = systemId;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getPublicId(){
        return publicId;
    }
    
    public String getSystemId(){
        return systemId;
    }
    
    public int getLineNumber(){
        return lineNumber;
    }
    
    public int getColumnNumber(){
        return columnNumber;
    }
}
