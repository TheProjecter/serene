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

import javax.xml.validation.Schema;

import serene.validation.handlers.content.impl.ContentHandlerPool;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ErrorHandlerPool;
import serene.validation.handlers.error.ErrorDispatcher;

import sereneWrite.MessageWriter;

public abstract class BaseSchema extends Schema{
    protected boolean secureProcessing;    
	protected SchemaModel schemaModel;
    		
	protected ContentHandlerPool contentHandlerPool;	
	protected ErrorHandlerPool errorHandlerPool;
		
	protected MessageWriter debugWriter;
    
    public BaseSchema(boolean secureProcessing,
                    SchemaModel schemaModel,
                    MessageWriter debugWriter){
        this.debugWriter = debugWriter; 
        this.schemaModel = schemaModel;
        this.secureProcessing = secureProcessing;        
        
        contentHandlerPool = ContentHandlerPool.getInstance(debugWriter);
		errorHandlerPool = ErrorHandlerPool.getInstance(debugWriter);
    }    
}
