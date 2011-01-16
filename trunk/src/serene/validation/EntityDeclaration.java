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

package serene.validation;

import sereneWrite.MessageWriter;

public class EntityDeclaration{
    String name;
    String publicId;
    String systemId;
    String notationName;
    
    MessageWriter debugWriter;
    
    public EntityDeclaration(String name,
                    String publicId,
                    String systemId,
                    String notationName,
                    MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.name = name;
        this.publicId = publicId;
        this.systemId = systemId;
        this.notationName = notationName;
    }
    
    String getName(){
        return name;
    }
    
    String getPublicId(){
        return publicId;
    }
    
    String getSystemId(){
        return systemId;
    }
    
    String getNotationName(){
        return notationName;
    }
}