package serene.validation.schema;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.active.ActiveModel;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

public interface ValidationModel{
    ParsedModel getParsedModel();
    SimplifiedModel getSimplifiedModel();
    ActiveModel getActiveModel(ActiveInputDescriptor activeInputDescriptor, InputStackDescriptor inputStackDescriptor, ErrorDispatcher errorDispatcher);
}
