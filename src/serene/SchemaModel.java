package serene;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.active.ActiveModel;

import serene.validation.handlers.error.ErrorDispatcher;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.dtdcompatibility.InfosetModificationModel;
import serene.dtdcompatibility.InfosetModificationModelImpl;
import serene.dtdcompatibility.AttributeDefaultValueModel;

import serene.validation.schema.ValidationModel;

import sereneWrite.MessageWriter;

public class SchemaModel implements ValidationModel, InfosetModificationModel{
    ValidationModel validationModel;
    InfosetModificationModel infosetModificationModel;
    
    MessageWriter debugWriter;
    public SchemaModel(ValidationModel validationModel,
            InfosetModificationModel infosetModificationModel,
            MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.validationModel = validationModel;
        this.infosetModificationModel = infosetModificationModel;        
    }
    
    
    public ParsedModel getParsedModel(){
        if(validationModel == null) return null;
        return validationModel.getParsedModel();
    }
    
    public SimplifiedModel getSimplifiedModel(){
        if(validationModel == null) return null;
        return validationModel.getSimplifiedModel();
    }
    
    public ActiveModel getActiveModel(ValidationItemLocator validationItemLocator, ErrorDispatcher errorDispatcher){
        if(validationModel == null) return null;
        return validationModel.getActiveModel(validationItemLocator, errorDispatcher);
    }    
    
    public AttributeDefaultValueModel getAttributeDefaultValueModel(){
        if(infosetModificationModel == null) return null;
        return infosetModificationModel.getAttributeDefaultValueModel();
    }
}
