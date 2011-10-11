package serene.validation.schema;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.active.ActiveModel;

import serene.validation.handlers.error.ErrorDispatcher;
import serene.validation.handlers.content.util.ValidationItemLocator;

public interface ValidationModel{
    ParsedModel getParsedModel();
    SimplifiedModel getSimplifiedModel();
    ActiveModel getActiveModel(ValidationItemLocator validationItemLocator, ErrorDispatcher errorDispatcher);
}
