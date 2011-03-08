package serene;

public interface Constants{  
    String DTD_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdHandler";
    String DTD_MAPPING_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdMapping";
    String ERROR_HANDLER_POOL_PROPERTY = "http://serenerng.org/validatorHandler/property/errorHandlerPool";
    String EVENT_HANDLER_POOL_PROPERTY = "http://serenerng.org/validatorHandler/property/eventHandlerPool";
    String ITEM_LOCATOR_PROPERTY = "http://serenerng.org/validatorHandler/property/validationItemLocator";
    String MATCH_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/matchHandler";
    String CONTROLLER_POOL_PROPERTY = "http://serenerng.org/controller/property/controllerPool";
    
    
    String TARGET_NAMESPACE_NAME = "http://serenerng.org/param/targetNamespace";
    
    String XSD_DATATYPE_LIBRARY = "http://www.w3.org/2001/XMLSchema-datatypes";
    String INTERNAL_DATATYPE_LIBRARY = "http://serenerng.org/datatype/internal";
    String NATIVE_DATATYPE_LIBRARY = "";    
	String TOKEN_DT = "token";
  
    String PARSED_MODEL_SCHEMA_FEATURE = "http://serenerng.org/features//schemaFactory/features/parsedModelSchema";
    String REPLACE_MISSING_LIBRARY_FEATURE = "http://serenerng.org/features/schemaFactory/replaceMissingDatatypeLibrary";
    
    String DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE = "http://relaxng.org/ns/compatibility/annotations/1.0";
    String DTD_COMPATIBILITY_DEFAULT_VALUE = "defaultValue";
    
    String LEVEL1_ATTRIBUTE_DEFAULT_VALUE_FEATURE = "http://serenerng.org/features/DTDCompatibility/level1/compatibility/attributeDefaultValue";
    String LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE = "http://serenerng.org/features/DTDCompatibility/level2/attributeDefaultValue";
}
