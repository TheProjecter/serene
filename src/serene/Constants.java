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

package serene;

public interface Constants{    
    String INFOSET_MODIFICATION_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/infosetModificationHandler";
    String DTD_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdHandler";
    String DTD_MAPPING_PROPERTY = "http://serenerng.org/validatorHandler/property/dtdMapping";
    String ERROR_HANDLER_POOL_PROPERTY = "http://serenerng.org/validatorHandler/property/errorHandlerPool";
    String EVENT_HANDLER_POOL_PROPERTY = "http://serenerng.org/validatorHandler/property/eventHandlerPool";
    String ITEM_LOCATOR_PROPERTY = "http://serenerng.org/validatorHandler/property/validationItemLocator";
    String DOCUMENT_CONTEXT_PROPERTY = "http://serenerng.org/validatorHandler/property/documentContextProperty";
    String MATCH_HANDLER_PROPERTY = "http://serenerng.org/validatorHandler/property/matchHandler";
    String CONTROLLER_POOL_PROPERTY = "http://serenerng.org/controller/property/controllerPool";
    
    
    String TARGET_NAMESPACE_NAME = "http://serenerng.org/param/targetNamespace";
    
    String XSD_DATATYPE_LIBRARY = "http://www.w3.org/2001/XMLSchema-datatypes";
    String INTERNAL_DATATYPE_LIBRARY = "http://serenerng.org/datatype/internal";
    String DTD_COMPATIBILITY_DATATYPE_LIBRARY = "http://relaxng.org/ns/compatibility/datatypes/1.0";
    String NATIVE_DATATYPE_LIBRARY = "";    
	String TOKEN_DT = "token";
  
    String NAMESPACES_PREFIXES_SAX_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    String PARSED_MODEL_SCHEMA_FEATURE = "http://serenerng.org/features//schemaFactory/features/parsedModelSchema";
    String REPLACE_MISSING_LIBRARY_FEATURE = "http://serenerng.org/features/schemaFactory/replaceMissingDatatypeLibrary";
    
    String DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE = "http://relaxng.org/ns/compatibility/annotations/1.0";
    String DTD_COMPATIBILITY_DEFAULT_VALUE = "defaultValue";
    
    String LEVEL1_ATTRIBUTE_DEFAULT_VALUE_FEATURE = "http://serenerng.org/features/DTDCompatibility/level1/attributeDefaultValue";
    String LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE = "http://serenerng.org/features/DTDCompatibility/level2/attributeDefaultValue";
    
    String LEVEL1_ID_IDREF_IDREFS_FEATURE = "http://serenerng.org/features/DTDCompatibility/level1/idIdrefIdrefs";
    String LEVEL2_ID_IDREF_IDREFS_FEATURE = "http://serenerng.org/features/DTDCompatibility/level2/idIdrefIdrefs";
}
