package serene.validation;

import javax.xml.validation.Schema;

import serene.validation.schema.parsed.ParsedModel;
import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.active.ActiveModelPool;

public abstract class BaseSchema extends Schema{    
    public abstract ParsedModel getParsedModel();
    public abstract SimplifiedModel getSimplifiedModel();
    public abstract ActiveModelPool getActiveModelPool();
}
