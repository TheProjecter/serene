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
    
    MessageWriter debugWriter;
    
    public ValidationModelImpl(ParsedModel parsedModel,
                        SimplifiedModel simplifiedModel,
                        MessageWriter debugWriter){
        this.debugWriter = debugWriter; 
        this.parsedModel = parsedModel;
        this.simplifiedModel = simplifiedModel;
        activeModelPool = new ActiveModelPool(simplifiedModel, debugWriter);
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
