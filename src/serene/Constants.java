package serene;

public interface Constants{  
    String DTD_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdHandler";
    String DTD_MAPPING_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdMapping";
    
    String TARGET_NAMESPACE_NAME = "http://serenerng.org/param/targetNamespace";
    
    String INTERNAL_DATATYPE_LIBRARY = "http://serenerng.org/datatype/internal";
    String NATIVE_DATATYPE_LIBRARY = "";
    String XSD_DATATYPE_LIBRARY = "http://www.w3.org/2001/XMLSchema-datatypes";
	String TOKEN_DT = "token";
  
    String PARSED_MODEL_SCHEMA_FEATURE = "http://serenerng.org/features//schemaFactory/features/parsedModelSchema";
    String REPLACE_MISSING_LIBRARY_FEATURE = "http://serenerng.org/features/schemaFactory/replaceMissingDatatypeLibrary";
}
