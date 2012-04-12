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


package serene.validation.schema;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.active.ActiveModel;
import serene.validation.schema.active.ActiveModelPool;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import sereneWrite.MessageWriter;

public class ValidationModelImpl implements ValidationModel{
    ParsedModel parsedModel;
    SimplifiedModel simplifiedModel;
    ActiveModelPool activeModelPool;
    
    boolean optimizedForResourceSharing;
    
    MessageWriter debugWriter;
    
    public ValidationModelImpl(ParsedModel parsedModel,
                        SimplifiedModel simplifiedModel,
                        boolean optimizedForResourceSharing,
                        MessageWriter debugWriter){
        this.debugWriter = debugWriter; 
        this.parsedModel = parsedModel;
        this.simplifiedModel = simplifiedModel;
        this.optimizedForResourceSharing = optimizedForResourceSharing;
        activeModelPool = new ActiveModelPool(simplifiedModel, optimizedForResourceSharing, debugWriter);
    }
    
    
    public ParsedModel getParsedModel(){
        return parsedModel;
    }
    
    public SimplifiedModel getSimplifiedModel(){
        return simplifiedModel;
    }
    
    public ActiveModel getActiveModel(ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor, ErrorDispatcher errorDispatcher){
        return activeModelPool.getActiveModel(activeInputDescriptor, inputStackDescriptor, errorDispatcher);
    }    
}
