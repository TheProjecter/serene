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

package serene.dtdcompatibility;

import java.util.Arrays;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.relaxng.datatype.Datatype;

import serene.util.BooleanList;
import serene.util.IntList;

import serene.BaseSchema;

import serene.restrictor.ControllerPool;

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RestrictingVisitor;

import serene.validation.schema.simplified.SimplifiedPattern;
import serene.validation.schema.simplified.components.SNameClass;

import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SExceptNameClass;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SEmpty;
import serene.validation.schema.simplified.components.SText;
import serene.validation.schema.simplified.components.SNotAllowed;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SValue;
import serene.validation.schema.simplified.components.SGrammar;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SDummy;

import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;


import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.RestrictingVisitor;

import serene.validation.schema.active.ActiveModelPool;
import serene.validation.schema.active.ActiveModel;
import serene.validation.schema.active.ActiveGrammarModel;

import serene.validation.schema.Identifier;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;


import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.impl.ContentHandlerPool;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;

import serene.validation.handlers.content.CharactersEventHandler;

import serene.validation.handlers.error.ErrorDispatcher;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.AttributeDefaultValueErrorHandler;

import serene.validation.handlers.content.DefaultValueAttributeHandler;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;

import serene.validation.handlers.content.util.InputStackDescriptor;
import serene.validation.handlers.content.util.ActiveInputDescriptor;

import serene.SchemaModel;


import serene.validation.schema.ValidationModel;

import serene.util.AttributeInfo;
import serene.util.NameInfo;
import serene.bind.util.DocumentIndexedData;

public class CompatibilityHandler implements RestrictingVisitor{
    SimplifiedPattern[] startTopPattern;
	SimplifiedPattern[] refDefinitionTopPattern;
    RecursionModel recursionModel;
        
    boolean level1AttributeDefaultValue;
    boolean level1AttributeIdType;
    boolean restrictToFileName;
    
    CompatibilityControlAttribute ccAttribute;
    DefaultValueAttributeHandler defaultValueHandler;
    
    ActiveModel activeModel;
    ActiveGrammarModel grammarModel;
    
    ValidatorErrorHandlerPool errorHandlerPool;
    ValidatorEventHandlerPool eventHandlerPool;
    ActiveInputDescriptor activeInputDescriptor;
    InputStackDescriptor inputStackDescriptor;
        
    ErrorDispatcher errorDispatcher;

    AttributeDefaultValueErrorHandler defaultValueErrorHandler;   
    
    //for competition simetry
    Stack<ArrayList<SAttribute>> attributeListsStack;   
    ArrayList<SAttribute> currentAttributesList;
    
    
    // for attribute default value
    boolean defaultedAttributeContent; // for element level handling: handle choices, required branches, name 
    boolean defaultedAttributeContext; // for datatype: report context dependent   
    boolean controlAlternative;// for choice handling
    // TODO What is controlAlternative actually doing and why do you turn it off 
    // inside cardinality elements? Also some clarification needed for hasAlternative 
    // in the presence of optional and zeroOrMore. 
    boolean hasAlternative;// for choice handling
    boolean hasName; // for name class control
    
    Stack<ArrayList<AttributeInfo>> attributesDVListsStack;
    ArrayList<AttributeInfo> currentAttributesDVList;
    
    Stack<BooleanList> isRequiredBranchStack;
    BooleanList isRequiredBranch;
    
    Stack<BooleanList> needsOptionalChoiceStack;
    BooleanList needsOptionalChoice;
    
    AttributeDefaultValueModel attributeDefaultValueModel;
        
    // for  ID IDREF IDREFS
    boolean attributeContext; // must be true when idType is not null
    boolean idTypeAttributeContent; // for element level handling: name
    int idType;
    
    Stack<ArrayList<SAttribute>> idAttributeListsStack;
    ArrayList<SAttribute> currentIdAttributesList;

    IntList currentAttributeIdTypesList;
    Stack<IntList> attributeIdTypeListsStack;
    
    SElement currentElement;

    AttributeIdTypeModel attributeIdTypeModel;
    
    // for default values that are ID IDREF IDREFS when both features are set
    HashMap<String, SAttribute> idAttributes;
    ArrayList<SAttribute> idrefAttributes;
    ArrayList<SAttribute> idrefsAttributes;
    ArrayList<String> errorTokens;
    
    ControllerPool controllerPool;
    CompetitionSimetryController simetryController;
    
    boolean optimizedForResourceSharing;
        
    public CompatibilityHandler(ControllerPool controllerPool,
                                ValidatorErrorHandlerPool errorHandlerPool,
                                ValidatorEventHandlerPool eventHandlerPool,
                                ActiveInputDescriptor activeInputDescriptor,
                                InputStackDescriptor inputStackDescriptor,
                                ErrorDispatcher errorDispatcher){
        this.controllerPool = controllerPool;
        this.errorHandlerPool = errorHandlerPool;
        this.eventHandlerPool = eventHandlerPool;
        this.activeInputDescriptor = activeInputDescriptor;
        this.inputStackDescriptor = inputStackDescriptor;     
        this.errorDispatcher = errorDispatcher; 
        defaultValueErrorHandler = new AttributeDefaultValueErrorHandler(activeInputDescriptor, errorDispatcher);
        simetryController = new CompetitionSimetryController(controllerPool, errorDispatcher);
        
        attributesDVListsStack = new Stack<ArrayList<AttributeInfo>>();
        attributeListsStack = new Stack<ArrayList<SAttribute>>();
        isRequiredBranchStack = new Stack<BooleanList>();
        needsOptionalChoiceStack = new Stack<BooleanList>();
        
        idAttributeListsStack = new Stack<ArrayList<SAttribute>>();
        currentIdAttributesList = new ArrayList<SAttribute>();
        
        attributeIdTypeListsStack = new Stack<IntList>();
        
        idAttributes = new HashMap<String, SAttribute>();
        idrefAttributes = new ArrayList<SAttribute>();
        idrefsAttributes = new ArrayList<SAttribute>();
        errorTokens = new ArrayList<String>();
    }
    
    public void setLevel1AttributeDefaultValue(boolean value){
        level1AttributeDefaultValue = value;
        if(level1AttributeDefaultValue){
            if(ccAttribute == null) ccAttribute = new CompatibilityControlAttribute();
            if(defaultValueHandler == null) defaultValueHandler = eventHandlerPool.getDefaultValueAttributeValidationHandler();  
        }
    }
    
    public void setLevel1AttributeIdType(boolean value){
        level1AttributeIdType = value;
    }
    
    public void setRestrictToFileName(boolean value){
        restrictToFileName = value;
        simetryController.setRestrictToFileName(restrictToFileName);
        defaultValueErrorHandler.setRestrictToFileName(restrictToFileName);
    }
    
    public void setOptimizeForResourceSharing(boolean optimizedForResourceSharing){
        this.optimizedForResourceSharing = optimizedForResourceSharing;
    }
    
    public SchemaModel handle(ValidationModel validationModel) throws SAXException{     
        SimplifiedModel simplifiedModel = validationModel.getSimplifiedModel();
        if(simplifiedModel == null)return new SchemaModel(validationModel, new DTDCompatibilityModelImpl(null, null));
        activeModel = validationModel.getActiveModel(activeInputDescriptor, inputStackDescriptor, errorDispatcher);
        grammarModel = activeModel.getGrammarModel();       
                
        startTopPattern = simplifiedModel.getStartTopPattern();
        refDefinitionTopPattern = simplifiedModel.getRefDefinitionTopPattern();
        recursionModel = simplifiedModel.getRecursionModel();
        
        attributeDefaultValueModel = null;
        
        DTDCompatibilityModel dtdCompatibilityModel = null;
        if(level1AttributeDefaultValue){
            
            attributeDefaultValueModel = new AttributeDefaultValueModel();
            
            ccAttribute.init(grammarModel,
                        activeModel.getStackHandlerPool(),
                        activeModel.getRuleHandlerPool());
            
            attributeListsStack.clear();
            isRequiredBranchStack.clear();
            needsOptionalChoiceStack.clear();
            
            simetryController.clear();
            
            currentAttributesList = new ArrayList<SAttribute>();
            currentAttributesDVList = new ArrayList<AttributeInfo>();                        
            isRequiredBranch = new BooleanList();
            needsOptionalChoice = new BooleanList();
            defaultedAttributeContent = false;
            defaultedAttributeContext = false;    
            controlAlternative = false; 
            hasAlternative = false;
            hasName = false;            
        }
        if(level1AttributeIdType){
            attributeIdTypeModel = new AttributeIdTypeModel();
            
            attributeContext = false;
            idTypeAttributeContent = false;
            idType = Datatype.ID_TYPE_NULL;
            
            idAttributeListsStack.clear();
            currentIdAttributesList = new ArrayList<SAttribute>();

            attributeIdTypeListsStack.clear();
            currentAttributeIdTypesList = new IntList();
        }
        
        for(SimplifiedPattern start : startTopPattern){
            start.accept(this);
        }
        if(optimizedForResourceSharing)activeModel.recycle();
        if(level1AttributeDefaultValue){
            if(errorDispatcher.hasAttributeDefaultValueError()) attributeDefaultValueModel = null;
            else attributeDefaultValueModel.wrapUp();
        }
        if(level1AttributeIdType){
            if(level1AttributeDefaultValue){
                handleIdRefs();
                idAttributes.clear();
                idrefAttributes.clear();
                idrefsAttributes.clear();
                errorTokens.clear();
            }
            
            if(errorDispatcher.hasAttributeIdTypeError()) attributeIdTypeModel = null;
            else attributeIdTypeModel.wrapUp();
        }
        dtdCompatibilityModel = new DTDCompatibilityModelImpl(attributeDefaultValueModel, attributeIdTypeModel);
        return new SchemaModel(validationModel, dtdCompatibilityModel);        
    }
    
    private void handleIdRefs() throws SAXException{
        for(int i = 0; i < idrefAttributes.size(); i++){
            SAttribute attribute = idrefAttributes.get(i);
            String defaultValue = attribute.getDefaultValue();
            if(!idAttributes.containsKey(defaultValue)){
                String message = "DTD compatibility warning. No corresponding attribute of ID-type ID for attribute definition at "+attribute.getLocation(restrictToFileName)+" with the ID-type IDREF.";
                errorDispatcher.warning(new AttributeIdTypeException(message, null));
            }
        }
        for(int i = 0; i < idrefsAttributes.size(); i++){
            SAttribute attribute = idrefsAttributes.get(i);
            String defaultValue = attribute.getDefaultValue();
            errorTokens.clear();
            StringTokenizer tokenizer = new StringTokenizer(defaultValue);
            while(tokenizer.hasMoreTokens()){
                String token = tokenizer.nextToken();
                if(!idAttributes.containsKey(token)){
                    errorTokens.add(token);
                }
            }
            if(errorTokens.size() == 1){
                String message = "DTD compatibility warning. No corresponding attribute of ID-type ID for token \""+errorTokens.get(0)+"\" in the value of attribute definition at "+attribute.getLocation(restrictToFileName)+" with the ID-type IDREFS.";
                errorDispatcher.warning(new AttributeIdTypeException(message, null));
            }else if(errorTokens.size() > 1){
                String tokens = "";
                int lastToken = errorTokens.size()-1;
                for(int j = 0; j < lastToken; j++){
                    tokens += "\""+errorTokens.get(j)+"\", ";
                }
                tokens += "\""+errorTokens.get(lastToken)+"\"";
                String message = "DTD compatibility warning. No corresponding attribute of ID-type ID for tokens \""+tokens+"\" in the default value of attribute definition at "+attribute.getLocation(restrictToFileName)+" with the ID-type IDREFS.";
                errorDispatcher.warning(new AttributeIdTypeException(message, null));
            }
        }
    }
    public void visit(SExceptPattern exceptPattern)throws SAXException{
		SimplifiedComponent child = exceptPattern.getChild();
        if(child == null) return;
        if(controlAlternative){
            controlAlternative = false;
        }
		child.accept(this);        
	}
	public void visit(SExceptNameClass exceptNameClass)throws SAXException{
		SimplifiedComponent child = exceptNameClass.getChild();
		if(child != null) child.accept(this);
        hasName = false;
	}
		
	public void visit(SName component){
        hasName = true;
    }
	public void visit(SAnyName anyName)throws SAXException{
        hasName = false;
	}
	public void visit(SNsName nsName)throws SAXException{
        hasName = false;
	}
	public void visit(SChoiceNameClass choice)throws SAXException{
        hasName = false;
	}	
	
	
	public void visit(SElement element)throws SAXException{
        SimplifiedComponent child = element.getChild();
        if(child == null){
            ArrayList<SAttribute> attributesList = new ArrayList<SAttribute>();
            //simetryController = new CompetitionSimetryController(controllerPool, errorDispatcher, debugWriter);
            simetryController.control(element, attributesList);
            if(controlAlternative && element.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
        
        
        boolean controlAlternativeMemo = false;
		int cardinalityAttributesOffset = -1;
		if(element.hasCardinalityElement()){		    
            if(controlAlternative){
                controlAlternativeMemo = controlAlternative;
                controlAlternative = false;
            }
            if(element.getMinOccurs() == 0){
                cardinalityAttributesOffset = currentAttributesList.size();                
            }
		}
        
        boolean defaultedAttributeContextMemo = defaultedAttributeContext;
        boolean defaultedAttributeContentMemo = defaultedAttributeContent;
        boolean idTypeAttributeContentMemo = idTypeAttributeContent;
        if(level1AttributeDefaultValue){            
            attributesDVListsStack.push(currentAttributesDVList);
            currentAttributesDVList = new ArrayList<AttributeInfo>();
            
            attributeListsStack.push(currentAttributesList);
            currentAttributesList = new ArrayList<SAttribute>();
            
            isRequiredBranchStack.push(isRequiredBranch);
            isRequiredBranch = new BooleanList();
            
            needsOptionalChoiceStack.push(needsOptionalChoice);
            needsOptionalChoice = new BooleanList();
            
            defaultedAttributeContent = false;
            defaultedAttributeContext = false;    
            controlAlternative = false; 
            hasAlternative = false;
        } 
            
        if(level1AttributeIdType){
            currentElement = element;
            
            idAttributeListsStack.push(currentIdAttributesList);
            currentIdAttributesList = new ArrayList<SAttribute>();
            
            attributeIdTypeListsStack.push(currentAttributeIdTypesList);
            currentAttributeIdTypesList = new IntList();
            
            idTypeAttributeContent = false;
        }
            
       
        child.accept(this);
        //see about this: only necessary when compatibility
                
        if(level1AttributeDefaultValue){
            if(defaultedAttributeContent){                
                hasName = false;
                SNameClass nameClass = element.getNameClass();
                if(nameClass != null) nameClass.accept(this);
                String attributes = "";
                if(!hasName){                    
                    for(int i = 0; i < currentAttributesList.size(); i++){
                        SAttribute attr = currentAttributesList.get(i);
                        if(attr.getDefaultValue() != null){
                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation(restrictToFileName);
                        }
                    }                                        
                    String message = "DTD compatibility error. Default value not allowed for attribute definitions in the content model of an element definition without a <name> name class, <"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" :"+attributes+".";
                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                }
                
                attributes = "";
                boolean hasRequiredBranch = false;
                for(int i = 0; i < isRequiredBranch.size(); i++){
                    if(isRequiredBranch.get(i)){
                        hasRequiredBranch = true;
                        SAttribute attr = currentAttributesList.get(i);
                        attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation(restrictToFileName);
                    }
                }
                
                if(hasRequiredBranch){                    
                    String message = "DTD compatibility error. Default value not allowed for required attribute definitions in the content model of <"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" :"
                                    +attributes+".";
                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                }
                
                attributeDefaultValueModel.addAttributeInfo(nameClass, element.getNameClass(), 
                                                    currentAttributesDVList.toArray(new AttributeInfo[currentAttributesDVList.size()]));
                    
            }            
            currentAttributesDVList = attributesDVListsStack.pop();
            
            if(!level1AttributeIdType){                
                simetryController.control(element, currentAttributesList);
                currentAttributesList = attributeListsStack.pop();
            }
            
            isRequiredBranch = isRequiredBranchStack.pop();
            needsOptionalChoice = needsOptionalChoiceStack.pop();
            
            defaultedAttributeContext = defaultedAttributeContextMemo;
            defaultedAttributeContent = defaultedAttributeContentMemo;
        }
        
        if(level1AttributeIdType){
            if(idTypeAttributeContent){
                if(level1AttributeDefaultValue && defaultedAttributeContent){
                    if(!hasName){
                        String attributes = "";
                        for(int i = 0; i < currentIdAttributesList.size(); i++){
                            SAttribute attr = currentIdAttributesList.get(i);
                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation(restrictToFileName);
                        }                                        
                        String message = "DTD compatibility error. Attribute definitions in the content model of an element definition without a <name> name class, <"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" may not have non-null ID-type: "+attributes+".";
                        errorDispatcher.error(new AttributeIdTypeException(message, null));
                    }else{
                        SName name = (SName)element.getNameClass();
                        attributeIdTypeModel.addElementNameInfo(new NameInfo(name.getNamespaceURI(), name.getLocalPart(), null));
                    }
                }else{
                    hasName = false;
                    SimplifiedComponent nameClass = element.getNameClass();
                    if(nameClass != null) nameClass.accept(this);
                    if(!hasName){
                        String attributes = "";
                        for(int i = 0; i < currentIdAttributesList.size(); i++){
                            SAttribute attr = currentIdAttributesList.get(i);
                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation(restrictToFileName);
                        }                                        
                        String message = "DTD compatibility error. Attribute definitions in the content model of an element definition without a <name> name class, <"+element.getQName()+"> at "+element.getLocation(restrictToFileName)+" may not have non-null ID-type: "+attributes+".";
                        errorDispatcher.error(new AttributeIdTypeException(message, null));
                    }else{
                        SName name = (SName)nameClass;
                        attributeIdTypeModel.addElementNameInfo(new NameInfo(name.getNamespaceURI(), name.getLocalPart(), null));
                    }
                }
            }
            
            if(level1AttributeDefaultValue){
                simetryController.control(element, currentAttributesList, currentAttributeIdTypesList);
                currentAttributesList = attributeListsStack.pop();
            }else{
                simetryController.control(currentAttributesList, currentAttributeIdTypesList);
                currentAttributesList = attributeListsStack.pop();
            }
                
            idTypeAttributeContent = idTypeAttributeContentMemo;            
            currentIdAttributesList = idAttributeListsStack.pop();
            currentAttributeIdTypesList = attributeIdTypeListsStack.pop();
        }   
        
        if(element.hasCardinalityElement()){		    
            if(controlAlternativeMemo){
                controlAlternative = controlAlternativeMemo;
            }
            if(element.getMinOccurs() == 0){
                for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                    isRequiredBranch.set(cardinalityAttributesOffset, false);
                } 
                if(controlAlternative) hasAlternative = true;
            }
		}
	}	
	public void visit(SAttribute attribute)throws SAXException{
	    boolean controlAlternativeMemo = false;
		int cardinalityAttributesOffset = -1;
		if(attribute.hasCardinalityElement()){		    
            if(controlAlternative){
                controlAlternativeMemo = controlAlternative;
                controlAlternative = false;
            }
            if(attribute.getMinOccurs() == 0){
                cardinalityAttributesOffset = currentAttributesList.size();                
            }
		}
		
        currentAttributesList.add(attribute);
        		
		SimplifiedComponent[] children = attribute.getChildren();
        
			
        if(children == null || children.length > 1){// syntax error        
            if(attribute.hasCardinalityElement()){		    
                if(controlAlternativeMemo){
                    controlAlternative = controlAlternativeMemo;
                }
                if(attribute.getMinOccurs() == 0){
                    for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                        isRequiredBranch.set(cardinalityAttributesOffset, false);
                    }
                    if(controlAlternative) hasAlternative = true;
                }
            }
            return; 
        }
            
        
        String defaultValue = null;
        boolean defaultedAttributeContextMemo = defaultedAttributeContext;
        boolean idTypeAttributeContentMemo = idTypeAttributeContent;  
        if(level1AttributeDefaultValue){
            defaultValue = attribute.getDefaultValue();
            if(defaultValue != null){                
                isRequiredBranch.add(true);
                needsOptionalChoice.add(true);
                
                ccAttribute.init(attribute.getDefinitionIndex(), attribute);
                //inputStackDescriptor.pushAttribute(null, null, -1, -1, null, null, attribute.getQName());
                simulateInput(attribute);                
                defaultValueErrorHandler.setAttribute(attribute.getQName(), attribute.getLocation(restrictToFileName));                
                defaultValueHandler.init(ccAttribute, defaultValueErrorHandler);
                defaultValueHandler.handleAttribute(defaultValue);
                defaultValueHandler.reset();
                inputStackDescriptor.popAttribute();
                                
                defaultedAttributeContext = true;    
                controlAlternative = false; 
                hasAlternative = false;
                hasName = false;
            }else{
                isRequiredBranch.add(false);
                needsOptionalChoice.add(false);
            }
        }
        if(level1AttributeIdType){
            attributeContext = true;
            idType = Datatype.ID_TYPE_NULL;
        }
        //see that this is done only if necessary
        children[0].accept(this);
        
        AttributeInfo attributeInfo = null;
        
        if(level1AttributeDefaultValue){
            if(defaultedAttributeContext){
                SNameClass nc = attribute.getNameClass();                
                nc.accept(this);                
                if(!hasName){
                    String message = "DTD compatibility error. Default value not allowed for an attribute definition without a <name> name class, <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" .";
                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                }else{
                    SName name = (SName)nc;
                    attributeInfo = new AttributeInfo(name.getNamespaceURI(), 
                                                    name.getLocalPart(), 
                                                    null, 
                                                    defaultValue,
                                                    idType);
                    currentAttributesDVList.add(attributeInfo);
                }                
                
                if(level1AttributeIdType){
                    if(idType == Datatype.ID_TYPE_ID){
                        if(idAttributes.containsKey(defaultValue)){
                            SAttribute correspondent = idAttributes.get(defaultValue);
                            String message = "DTD compatibility warning. Default value of attribute definition at "+attribute.getLocation(restrictToFileName)+" with the ID-type ID is the same as the default value of attribute definition at "+correspondent.getLocation(restrictToFileName)+" with the ID-type ID.";
                            errorDispatcher.warning(new SAXParseException(message, null));
                        }else{
                            idAttributes.put(defaultValue, attribute);
                        }
                    }else if(idType == Datatype.ID_TYPE_IDREF){
                        idrefAttributes.add(attribute);
                    }else if(idType == Datatype.ID_TYPE_IDREFS){
                        idrefsAttributes.add(attribute);
                    }
                }
                defaultedAttributeContent = true;                
            }            
            defaultValueErrorHandler.report();
            defaultValueErrorHandler.clear();            
            defaultedAttributeContext = defaultedAttributeContextMemo;
        }
        
        if(level1AttributeIdType){
            if(idType != Datatype.ID_TYPE_NULL){
                if(level1AttributeDefaultValue && defaultedAttributeContext){
                    if(!hasName) {
                        String message = "DTD compatibility error. Attribute definition without a <name> name class, <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" may not have non-null ID-type.";
                        errorDispatcher.error(new AttributeIdTypeException(message, null));
                    }else{
                        SNameClass nc = attribute.getNameClass();
                        attributeIdTypeModel.addAttributeInfo(nc, attributeInfo);
                    } 
                }else{
                    hasName = false;
                    SNameClass nc = attribute.getNameClass();                
                    nc.accept(this);
                    if(hasName){
                        SName name = (SName)nc;
                        attributeIdTypeModel.addAttributeInfo(nc, new AttributeInfo(name.getNamespaceURI(), 
                                                    name.getLocalPart(), 
                                                    null, 
                                                    defaultValue,
                                                    idType));
                    }else{
                        String message = "DTD compatibility error. Attribute definition without a <name> name class, <"+attribute.getQName()+"> at "+attribute.getLocation(restrictToFileName)+" may not have non-null ID-type.";
                        errorDispatcher.error(new AttributeIdTypeException(message, null));
                    }
                }
                
                currentIdAttributesList.add(attribute);
                idTypeAttributeContent = true;
            }else{
                idTypeAttributeContent = idTypeAttributeContentMemo;
            }
        }
        
		if(attribute.hasCardinalityElement()){		    
            if(controlAlternativeMemo){
                controlAlternative = controlAlternativeMemo;
            }
            if(attribute.getMinOccurs() == 0){
                for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                    isRequiredBranch.set(cardinalityAttributesOffset, false);
                }
                if(controlAlternative) hasAlternative = true;
            }
		}
	}
	private void simulateInput(SAttribute attribute){
	    DocumentIndexedData did = attribute.getDocumentIndexedData();
	    int recordIndex = attribute.getRecordIndex();
	    
	    inputStackDescriptor.pushAttribute(did.getItemDescription(recordIndex),
	                                    did.getNamespaceURI(recordIndex),
	                                    did.getLocalName(recordIndex),
	                                    did.getAttributeType(recordIndex),
	                                    did.getSystemId(recordIndex),
	                                    did.getPublicId(recordIndex),
	                                    did.getLineNumber(recordIndex),
	                                    did.getColumnNumber(recordIndex));
	}
	
    
	public void visit(SChoicePattern choice)throws SAXException{
		SimplifiedComponent[] children = choice.getChildren();	
        if(children == null){
            if(controlAlternative && choice.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
        
        boolean controlAlternativeMemo = false;
		int cardinalityAttributesOffset = -1;
		if(choice.hasCardinalityElement()){		    
            if(controlAlternative){
                controlAlternativeMemo = controlAlternative;
                controlAlternative = false;
            }
            if(choice.getMinOccurs() == 0){
                cardinalityAttributesOffset = currentAttributesList.size();                
            }
		}
		
        int attributesOffset;        
        
        if(level1AttributeDefaultValue){
            boolean defaultedAttributeContentMemo = defaultedAttributeContent;            
            attributesOffset = currentAttributesList.size();
            hasAlternative = false;
            for(SimplifiedComponent child : children){
                if(!hasAlternative)controlAlternative = true;
                else controlAlternative = false;
                child.accept(this);
            }
            if(defaultedAttributeContent){
                if(hasAlternative){
                    for(int i = attributesOffset; i < isRequiredBranch.size(); i++){
                        isRequiredBranch.set(i, false);
                    }
                }else{
                    String attributes = "";
                    for(;attributesOffset < needsOptionalChoice.size(); attributesOffset++){
                        if(needsOptionalChoice.get(attributesOffset)){
                            SAttribute attr = currentAttributesList.get(attributesOffset);
                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation(restrictToFileName);
                            
                            isRequiredBranch.set(attributesOffset, false);//needs no more checking, an error was reported already
                        }
                    }
                    String message = "DTD compatibility error. Default value not allowed for attribute definitions in the context of a choice without <empty> alternative, <"+choice.getQName()+"> at "+choice.getLocation(restrictToFileName)+" :"+attributes+".";
                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                }
            }else{
                defaultedAttributeContent = defaultedAttributeContentMemo;
            }
        }else{
            for(SimplifiedComponent child : children){
                child.accept(this); 
            }
        }
        
        if(choice.hasCardinalityElement()){		    
            if(controlAlternativeMemo){
                controlAlternative = controlAlternativeMemo;
            }
            if(choice.getMinOccurs() == 0){
                for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                    isRequiredBranch.set(cardinalityAttributesOffset, false);
                }
                if(controlAlternative) hasAlternative = true;
            }
		}
	}
	public void visit(SInterleave interleave)throws SAXException{
		SimplifiedComponent[] children = interleave.getChildren();
        if(children == null){
            if(controlAlternative && interleave.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
        
        int cardinalityAttributesOffset = -1;
		if(interleave.getMinOccurs() == 0){
            cardinalityAttributesOffset = currentAttributesList.size();
		}
		
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
        for(SimplifiedComponent child : children){
            child.accept(this); 
        }           
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
        
        if(interleave.getMinOccurs() == 0){
            for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                isRequiredBranch.set(cardinalityAttributesOffset, false);
            }
            if(controlAlternative) hasAlternative = true;
		}
	}
	public void visit(SGroup group)throws SAXException{
		SimplifiedComponent[] children = group.getChildren();		
        if(children == null){
            if(controlAlternative && group.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
                
		int cardinalityAttributesOffset = -1;
		if(group.getMinOccurs() == 0){
            cardinalityAttributesOffset = currentAttributesList.size();
		}
		
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
        for(SimplifiedComponent child : children){
            child.accept(this); 
        }
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
        
        if(group.getMinOccurs() == 0){
            for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                isRequiredBranch.set(cardinalityAttributesOffset, false);
            }
            if(controlAlternative) hasAlternative = true;
        }
	}
	public void visit(SMixed mixed)throws SAXException{
		SimplifiedComponent child = mixed.getChild();
        if(child == null) {
            if(controlAlternative && mixed.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
                
		int cardinalityAttributesOffset = -1;
		if(mixed.getMinOccurs() == 0){
            cardinalityAttributesOffset = currentAttributesList.size();
		}
		
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		child.accept(this);
		
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
        if(mixed.getMinOccurs() == 0){
            for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                isRequiredBranch.set(cardinalityAttributesOffset, false);
            }
            if(controlAlternative) hasAlternative = true;
        }
        
	}	
	public void visit(SListPattern list)throws SAXException{
	    // TODO review, might need the same handling as data 
		SimplifiedComponent child = list.getChild();
        if(child == null) {
            if(controlAlternative && list.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
        
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		child.accept(this);
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
	}	
	public void visit(SEmpty empty){
        if(controlAlternative){
            hasAlternative = true;
        }
    }
	public void visit(SText text){
        if(level1AttributeIdType && attributeContext){
            currentAttributeIdTypesList.add(Datatype.ID_TYPE_NULL);
        }
    }
	public void visit(SNotAllowed notAllowed){}
	public void visit(SRef ref)throws SAXException{        
        if(recursionModel.isRecursiveReference(ref)){
            if(controlAlternative && ref.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
        		
        int index = ref.getDefinitionIndex(); 
        if(index < 0){
            if(controlAlternative && ref.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
                
        boolean controlAlternativeMemo = false;
		int cardinalityAttributesOffset = -1;
		if(ref.hasCardinalityElement()){		    
            if(controlAlternative){
                controlAlternativeMemo = controlAlternative;
                controlAlternative = false;
            }
            if(ref.getMinOccurs() == 0){
                cardinalityAttributesOffset = currentAttributesList.size();
            }
		}
				
        SimplifiedPattern defTopPattern = refDefinitionTopPattern[index];
        if(defTopPattern != null)defTopPattern.accept(this);
        
        if(ref.hasCardinalityElement()){		    
            if(controlAlternativeMemo){
                controlAlternative = controlAlternativeMemo;
            }
            if(ref.getMinOccurs() == 0){
                for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                    isRequiredBranch.set(cardinalityAttributesOffset, false);
                }
                if(controlAlternative) hasAlternative = true;
            }
		}
    }
    
	public void visit(SValue value)throws SAXException{
        if(defaultedAttributeContext){
            Datatype datatype = value.getDatatype();
            if(datatype.isContextDependent()){
                defaultValueErrorHandler.contextDependentDatatypeError(value);
            }            
        }        
        
        if(level1AttributeIdType){
            Datatype dt = value.getDatatype();
            idType = dt.getIdType();           
            
            if(!attributeContext){
                if(idType != Datatype.ID_TYPE_NULL){
                    String message = "DTD compatibility error. Definition <"+value.getQName()+"> at "+value.getLocation(restrictToFileName)+" specifying the content of element <"+currentElement.getQName()+"> at "+currentElement.getLocation(restrictToFileName) +" may not have a non-null ID-type.";
                    errorDispatcher.error(new AttributeIdTypeException(message, null));
                }
            }else{
                currentAttributeIdTypesList.add(idType);
            }
        }
    }    
    
	public void visit(SData data)throws SAXException{	        
		if(defaultedAttributeContext){
            Datatype datatype = data.getDatatype();
            if(datatype.isContextDependent()){
                defaultValueErrorHandler.contextDependentDatatypeError(data);
            }            
        }
 
        if(level1AttributeIdType){
            Datatype dt = data.getDatatype();
            idType = dt.getIdType();
            
            if(!attributeContext){
                if(idType != Datatype.ID_TYPE_NULL){
                    String message = "DTD compatibility error. Definition <"+data.getQName()+"> at "+data.getLocation(restrictToFileName)+" specifying the content of element <"+currentElement.getQName()+"> at "+currentElement.getLocation(restrictToFileName) +" may not have a non-null ID-type.";
                    errorDispatcher.error(new AttributeIdTypeException(message, null));
                }
            }else{
                currentAttributeIdTypesList.add(idType);
            }
        }
	}	
	public void visit(SGrammar grammar)throws SAXException{
	    boolean controlAlternativeMemo = false;
		int cardinalityAttributesOffset = -1;
		
		if(grammar.hasCardinalityElement()){		    
            if(controlAlternative){
                controlAlternativeMemo = controlAlternative;
                controlAlternative = false;
            }
            if(grammar.getMinOccurs() == 0){
                cardinalityAttributesOffset = currentAttributesList.size();                
            }
		}
		
		SimplifiedComponent child = grammar.getChild();
		if(child != null) child.accept(this);
		
		if(grammar.hasCardinalityElement()){		    
            if(controlAlternativeMemo){
                controlAlternative = controlAlternativeMemo;
            }
            if(grammar.getMinOccurs() == 0){
                for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                    isRequiredBranch.set(cardinalityAttributesOffset, false);
                }
                if(controlAlternative) hasAlternative = true;
            }
		}
	}
    
    
    public void visit(SDummy dummy)throws SAXException{
		SimplifiedComponent[] children = dummy.getChildren();
        if(children == null){
            if(controlAlternative && dummy.getMinOccurs() == 0) hasAlternative = true;
            return;
        }
        
        
		int cardinalityAttributesOffset = -1;
		if(dummy.getMinOccurs() == 0){
            cardinalityAttributesOffset = currentAttributesList.size();
		}
		
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		next(children);
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
        
        
        if(dummy.getMinOccurs() == 0){
            for(;cardinalityAttributesOffset < isRequiredBranch.size(); cardinalityAttributesOffset++){
                isRequiredBranch.set(cardinalityAttributesOffset, false);
            }
            if(controlAlternative) hasAlternative = true;
        }
	}
		
		
	protected void next(SimplifiedComponent[] children)throws SAXException{
		for(SimplifiedComponent child : children){            
			child.accept(this);
		}
	} 
}
