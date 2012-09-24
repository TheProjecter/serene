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
    String SCHEMATRON_NS_URI = "http://purl.oclc.org/dsdl/schematron";
    String SVRL_NS_URI = "http://purl.oclc.org/dsdl/svrl";
    String SCHEMATRON_QLB_ATTRIBUTE = "schematronQueryBinding";
    String SERENE_SCHEMATRON_NS_URI = "http://serenerng.com/schematron";
    
    String ATTRIBUTE_DEFAULT_VALUE_HANDLER_PROPERTY = "http://serenerng.com/validatorHandler/property/attributeDefaultValueHandler";
    String ATTRIBUTE_ID_TYPE_HANDLER_PROPERTY = "http://serenerng.com/validatorHandler/property/attributeIdTypeHandler";
    String DTD_HANDLER_PROPERTY = "http://serenerng.com/validatorHandler/property/dtdHandler";
    String DTD_MAPPING_PROPERTY = "http://serenerng.com/validatorHandler/property/dtdMapping";
    String ERROR_HANDLER_POOL_PROPERTY = "http://serenerng.com/validatorHandler/property/errorHandlerPool";
    String EVENT_HANDLER_POOL_PROPERTY = "http://serenerng.com/validatorHandler/property/eventHandlerPool";
    String STACK_HANDLER_POOL_PROPERTY = "http://serenerng.com/validatorHandler/property/stackHandlerPool";
    String ACTIVE_INPUT_DESCRIPTOR_PROPERTY = "http://serenerng.com/validatorHandler/property/activeInputDescriptor";
    String INPUT_STACK_DESCRIPTOR_PROPERTY = "http://serenerng.com/validatorHandler/property/inputStackDescriptor";
    String DOCUMENT_CONTEXT_PROPERTY = "http://serenerng.com/validatorHandler/property/documentContextProperty";
    String MATCH_HANDLER_PROPERTY = "http://serenerng.com/validatorHandler/property/matchHandler";
    String CONTROLLER_POOL_PROPERTY = "http://serenerng.com/controller/property/controllerPool";
    String PARSED_COMPONENT_BUILDER_PROPERTY = "http://serenerng.com/controller/property/parsedComponentBuilder";
    String PARSED_MODEL_PROPERTY = "http://serenerng.com/controller/property/parsedModel";
    String INCLUDED_PARSED_MODEL_PROPERTY = "http://serenerng.com/controller/property/includedParsedModel";
    /*String SCHEMATRON_QUERY_BINDING_BINDING_PROPERTY = "http://serenerng.com/schemaFactory/property/schematronQueryLanguageBinding";
    String SCHEMATRON_COMPILER_FOR_XSLT1_PROPERTY = "http://serenerng.com/schemaFactory/property/schematronCompilerForXSLT1";
    String SCHEMATRON_COMPILER_FOR_XSLT2_PROPERTY = "http://serenerng.com/schemaFactory/property/schematronCompilerForXSLT2";
    String SCHEMATRON_EXPANDED_SCHEMA_RESULT_PROPERTY = "http://serenerng.com/schemaFactory/property/schematronExpandedSchemaResult";
    String SCHEMATRON_TEMPLATES_HANDLER_PROPERTY = "http://serenerng.com/schemaFactory/property/schematronTemplatesHandler";
    String SCHEMATRON_TEMPLATES_PROPERTY = "http://serenerng.com/schemaFactory/property/schematronTemplates";*/
    String SCHEMATRON_PARSER_PROPERTY = "http://serenerng.com/schemaFactory/property/schematronParserHandler";
    String OVERRIDE_DEFINITIONS_PROPERTY = "http://serenerng.com/schemaFactory/property/overrideDefinitions";
    
    String TARGET_NAMESPACE_NAME = "http://serenerng.com/param/targetNamespace";
    
    String XSD_DATATYPE_LIBRARY = "http://www.w3.org/2001/XMLSchema-datatypes";
    String INTERNAL_DATATYPE_LIBRARY = "http://serenerng.com/datatype/internal";
    String DTD_COMPATIBILITY_DATATYPE_LIBRARY = "http://relaxng.org/ns/compatibility/datatypes/1.0";
    String NATIVE_DATATYPE_LIBRARY = "";    
	String TOKEN_DT = "token";
    
    
    String DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE = "http://relaxng.org/ns/compatibility/annotations/1.0";
    String DTD_COMPATIBILITY_DEFAULT_VALUE = "defaultValue";
    String DTD_COMPATIBILITY_DOCUMENTATION = "documentation";
    
    String NAMESPACES_PREFIXES_SAX_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    String PARSED_MODEL_SCHEMA_FEATURE = "http://serenerng.com/features//schemaFactory/features/parsedModelSchema";
    String REPLACE_MISSING_LIBRARY_FEATURE = "http://serenerng.com/features/schemaFactory/replaceMissingDatatypeLibrary";
    
    String LEVEL1_ATTRIBUTE_DEFAULT_VALUE_FEATURE = "http://serenerng.com/features/DTDCompatibility/level1/attributeDefaultValue";
    String LEVEL2_ATTRIBUTE_DEFAULT_VALUE_FEATURE = "http://serenerng.com/features/DTDCompatibility/level2/attributeDefaultValue";
    
    String LEVEL1_ATTRIBUTE_ID_TYPE_FEATURE = "http://serenerng.com/features/DTDCompatibility/level1/attributeIdType";
    String LEVEL2_ATTRIBUTE_ID_TYPE_FEATURE = "http://serenerng.com/features/DTDCompatibility/level2/attributeIdType";
    
    String LEVEL1_DOCUMENTATION_ELEMENT_FEATURE = "http://serenerng.com/features/DTDCompatibility/level1/documentationElement";
    
    String RESTRICT_TO_FILE_NAME_FEATURE = "http://serenerng.com/features/restrictToFileName";
    
    String OPTIMIZE_FOR_RESOURCE_SHARING_FEATURE = "http://serenerng.com/features/optimizeForResourceSharing";
    
    String PROCESS_EMBEDDED_SCHEMATRON_FEATURE = "http://serenerng.com/features/processEmbeddedSchematron";
    
    String IS_QLB_SUPPORTED = "http://serenerng.com/features/isQLBSupported";     
    
    
    String ISO_SVRL_FOR_XSLT2_LOCATION  = "/iso_svrl_for_xslt2.xsl";
    String ISO_SVRL_FOR_XSLT1_LOCATION = "/iso_svrl_for_xslt1.xsl";
    String ISO_ABSTRACT_EXPAND_LOCATION = "/iso_abstract_expand.xsl";
    String ISO_DSDL_INCLUDE_LOCATION = "/iso_dsdl_include.xsl";
    
}
