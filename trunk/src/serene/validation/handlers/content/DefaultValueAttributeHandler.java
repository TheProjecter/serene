package serene.validation.handlers.content;

import serene.validation.schema.simplified.SAttribute;

import serene.validation.handlers.error.ErrorCatcher;

public interface DefaultValueAttributeHandler extends AttributeEventHandler{
    void init(SAttribute attribute, ErrorCatcher errorCatcher);
    void reset();
}
