package serene.validation.handlers.content;

import serene.validation.schema.active.components.AAttribute;
import serene.validation.handlers.error.ErrorCatcher;

public interface DefaultValueAttributeHandler extends AttributeEventHandler{
    void init(AAttribute attribute, ErrorCatcher errorCatcher);
    void reset();
}
